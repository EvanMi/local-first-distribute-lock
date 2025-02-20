package io.github.evanmi.distribute.lock.db;

import io.github.evanmi.distribute.lock.api.Lock;
import io.github.evanmi.distribute.lock.api.renew.AutoRenewLock;
import io.github.evanmi.distribute.lock.db.sequence.Sequence;
import io.github.evanmi.distribute.lock.db.util.NetUtils;
import io.github.evanmi.distribute.lock.db.util.SystemClock;

import javax.sql.DataSource;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class DbSimpleLock extends AutoRenewLock implements Lock {
    private final DataSource dataSource;
    private final String path;
    private final String querySql;
    private final String deleteSql;
    private final String insertSql;

    private final String renewSql;
    private final Sequence sequence;

    public DbSimpleLock(long lockLeaseMills, String path, String tableName, DataSource dataSource) {
        super(lockLeaseMills);
        this.dataSource = dataSource;
        this.path = path;

        NetUtils.NetProperties netProperties = new NetUtils.NetProperties(new ArrayList<>(), new ArrayList<>());
        InetAddress inetAddress = new NetUtils(netProperties).findFirstNonLoopbackAddress();
        this.sequence = new Sequence(inetAddress);
        //sql
        this.querySql = "SELECT lock_key, thread_id, timeout from " + tableName + " WHERE lock_key = ?";
        this.deleteSql = "DELETE FROM " + tableName + " WHERE lock_key = ? AND thread_id = ?";
        this.insertSql = "INSERT INTO " + tableName + " (id, lock_key, thread_id, timeout) VALUES (?, ?, ?, ?)";
        this.renewSql = "UPDATE " + tableName + " SET timeout = ? WHERE lock_key = ? AND thread_id = ?";
    }

    @Override
    public boolean acquire(long nanos) throws Exception {
        String threadId = this.getThreadId();
        try (Connection connection = this.dataSource.getConnection()) {
            String localThreadId = null;
            Long localTimeout = null;
            try (PreparedStatement preparedStatement = connection.prepareStatement(querySql)) {
                // 设置参数
                preparedStatement.setString(1, this.path);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        localThreadId = resultSet.getString("thread_id");
                        localTimeout = resultSet.getLong("timeout");
                    }
                }
            }

            if (null != localThreadId) {
                if (SystemClock.now() < localTimeout) {
                    return false;
                } else {
                    try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSql)) {
                        preparedStatement.setString(1, this.path);
                        preparedStatement.setString(2, localThreadId);
                        int rowsUpdated = preparedStatement.executeUpdate();
                        return rowsUpdated != 0;
                    }
                }
            } else {
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
                    //(id, lock_key, thread_id, timeout)
                    preparedStatement.setLong(1, this.sequence.nextId());
                    preparedStatement.setString(2, this.path);
                    preparedStatement.setString(3, threadId);
                    preparedStatement.setLong(4, SystemClock.now() + this.lockLeaseMills);
                    int rowInserted = preparedStatement.executeUpdate();
                    if (rowInserted > 0) {
                        scheduleExpirationRenewal(getThreadId(), this.path);
                        return true;
                    }
                    return false;
                } catch (Exception ignored) {
                    return false;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void release() throws Exception {
        String threadId = this.getThreadId();
        try (Connection connection = this.dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(querySql)) {
                // 设置参数
                preparedStatement.setString(1, this.path);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String localThreadId = resultSet.getString("thread_id");
                        if (Objects.equals(localThreadId, threadId)) {
                            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
                                deleteStatement.setString(1, this.path);
                                deleteStatement.setString(2, localThreadId);
                                deleteStatement.executeUpdate();
                            } finally {
                                cancelExpirationRenewal(getThreadId(), this.path);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected CompletionStage<Boolean> renewExpirationAsync(String threadId) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = this.dataSource.getConnection()) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(renewSql)) {
                    preparedStatement.setLong(1, SystemClock.now() + this.lockLeaseMills);
                    preparedStatement.setString(2, this.path);
                    preparedStatement.setString(3, threadId);

                    int rowsUpdated = preparedStatement.executeUpdate();
                    return rowsUpdated > 0;
                }
            } catch (Exception e) {
                return false;
            }
        });
    }
}
