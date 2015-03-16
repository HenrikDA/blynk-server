package cc.blynk.common.stats.metrics;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

import static java.lang.Math.exp;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 16.03.15.
 */
public class InstanceLoadMeter {

    private static final long TICK_INTERVAL = TimeUnit.SECONDS.toMillis(1);
    private static final double TICK_INTERVAL_DOUBLE = (double) TICK_INTERVAL;

    private final LongAdder count = new LongAdder();
    private final AtomicLong lastTick;
    private final LongAdder uncounted = new LongAdder();
    private final double alpha = 1 - exp(-1/60.0);
    private volatile boolean initialized = false;
    private volatile double rate = 0.0;

    public InstanceLoadMeter() {
        this.lastTick = new AtomicLong(System.currentTimeMillis());
    }

    /**
     * Mark the occurrence of an event.
     */
    public void mark() {
        mark(1);
    }

    /**
     * Mark the occurrence of a given number of events.
     *
     * @param n the number of events
     */
    public void mark(long n) {
        tickIfNecessary();
        count.add(n);
        uncounted.add(n);
    }

    private void tickIfNecessary() {
        final long oldTick = lastTick.get();
        final long newTick = System.currentTimeMillis();
        final long age = newTick - oldTick;
        if (age > TICK_INTERVAL) {
            final long newIntervalStartTick = newTick - age % TICK_INTERVAL;
            if (lastTick.compareAndSet(oldTick, newIntervalStartTick)) {
                final long requiredTicks = age / TICK_INTERVAL;
                for (long i = 0; i < requiredTicks; i++) {
                    tick();
                }
            }
        }
    }

    public long getCount() {
        return count.sum();
    }

    public double getOneMinuteRate() {
        tickIfNecessary();
        return rate * TICK_INTERVAL_DOUBLE;
    }

    private void tick() {
        final long count = uncounted.sumThenReset();
        final double instantRate = count / TICK_INTERVAL_DOUBLE;
        if (initialized) {
            rate += (alpha * (instantRate - rate));
        } else {
            rate = instantRate;
            initialized = true;
        }
    }

}
