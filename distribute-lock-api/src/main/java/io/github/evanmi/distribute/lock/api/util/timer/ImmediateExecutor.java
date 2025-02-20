package io.github.evanmi.distribute.lock.api.util.timer;

import java.util.concurrent.Executor;

public final class ImmediateExecutor implements Executor {
    public static final ImmediateExecutor INSTANCE = new ImmediateExecutor();

    private ImmediateExecutor() {
        // use static instance
    }

    @Override
    public void execute(Runnable command) {
        if (null != command) {
            command.run();
        }
    }
}
