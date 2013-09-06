package com.maxifier.mxcache.activity;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: dalex
 * Date: 01.07.2010
 * Time: 12:10:31
 */
public class ActivityImpl implements Activity, Serializable {
    private static final Logger logger = LoggerFactory.getLogger(ActivityImpl.class);

    private static final long serialVersionUID = 100L;

    @NotNull
    private final String name;

    private int globalRuns;

    private List<ActivityListener> listeners;

    private transient ThreadLocal<Integer> local;

    public ActivityImpl(@NotNull String name) {
        this.name = name;
    }

    private synchronized ThreadLocal<Integer> getLocal() {
        if (local == null) {
            local = new ThreadLocal<Integer>();
        }
        return local;
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    public Object writeReplace() throws ObjectStreamException {
        return new ActivitySerializableImpl(this);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ActivityImpl that = (ActivityImpl) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public void start(@NotNull ActivityScope scope) {
        fireStarted(scope);
        switch (scope) {
            case GLOBAL:
                startGlobal();
                break;
            case THREAD_LOCAL:
                startThreadLocal();
                break;
            default:
                throw new UnsupportedOperationException("Unsupported activity scope: " + scope);
        }
    }

    @Override
    public void finish(@NotNull ActivityScope scope) {
        fireFinished(scope);
        switch (scope) {
            case GLOBAL:
                finishGlobal();
                break;
            case THREAD_LOCAL:
                finishThreadLocal();
                break;
            default:
                throw new UnsupportedOperationException("Unsupported activity scope: " + scope);
        }
    }

    private void startThreadLocal() {
        ThreadLocal<Integer> local = getLocal();
        Integer old = local.get();
        local.set(old == null ? 1 : (old + 1));
    }

    private void finishThreadLocal() {
        ThreadLocal<Integer> local = getLocal();
        Integer integer = local.get();
        if (integer == null || integer == 0) {
            throw new IllegalStateException("Activity was not runned in current thread");
        }
        local.set(integer - 1);
    }

    private synchronized void startGlobal() {
        globalRuns++;
    }

    private synchronized void finishGlobal() {
        if (globalRuns == 0) {
            throw new IllegalStateException("Activity is not running global");
        }
        globalRuns--;
    }

    @Override
    public synchronized boolean isRunning() {
        if (globalRuns > 0) {
            return true;
        }
        Integer localRuns = getLocal().get();
        return localRuns != null && localRuns > 0;
    }

    private synchronized void fireStarted(ActivityScope scope) {
        if (listeners != null) {
            for (ActivityListener activity : listeners) {
                try {
                    activity.started(scope);
                } catch (Exception e) {
                    logger.error("Error in ActivityListener for " + this, e);
                }
            }
        }
    }

    private synchronized void fireFinished(ActivityScope scope) {
        if (listeners != null) {
            for (ActivityListener activity : listeners) {
                try {
                    activity.finished(scope);
                } catch (Exception e) {
                    logger.error("Error in ActivityListener for " + this, e);
                }
            }
        }
    }

    @Override
    public synchronized void addListener(ActivityListener listener) {
        if (listeners == null) {
            listeners = new ArrayList<ActivityListener>();
        }
        listeners.add(listener);
    }

    @Override
    public synchronized void removeListener(ActivityListener listener) {
        if (listeners != null) {
            // если список не создан, то и удалять нечего
            listeners.remove(listener);
        }
    }
}
