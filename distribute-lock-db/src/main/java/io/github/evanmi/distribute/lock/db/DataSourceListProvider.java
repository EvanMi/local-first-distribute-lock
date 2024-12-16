package io.github.evanmi.distribute.lock.db;

import javax.sql.DataSource;
import java.util.List;

public interface DataSourceListProvider {
    List<DataSource> initClients();
}
