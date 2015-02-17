package cc.blynk.common.stats;

import cc.blynk.common.model.messages.protocol.*;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.SortedMap;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/13/2015.
 */
public class GlobalStats {

    private static final Logger log = LogManager.getLogger(GlobalStats.class);

    private MetricRegistry metricRegistry;
    private Meter incomeMessages;
    private SortedMap<String, Counter> specificCounters;

    public GlobalStats() {
        this.metricRegistry = new MetricRegistry();
        this.incomeMessages = metricRegistry.meter("incomeMessages");

        metricRegistry.counter(GetTokenMessage.class.getName());
        metricRegistry.counter(HardwareMessage.class.getName());
        metricRegistry.counter(LoadProfileMessage.class.getName());
        metricRegistry.counter(LoginMessage.class.getName());
        metricRegistry.counter(PingMessage.class.getName());
        metricRegistry.counter(RegisterMessage.class.getName());
        metricRegistry.counter(SaveProfileMessage.class.getName());
        metricRegistry.counter(TweetMessage.class.getName());

        this.specificCounters = metricRegistry.getCounters();
    }

    public void mark(Class<?> clazz) {
        specificCounters.get(clazz.getName()).inc();
    }

    public void mark() {
        incomeMessages.mark(1);
    }

    public void log() {
        log.debug("1 min rate : {}", incomeMessages.getOneMinuteRate() < 0.01 ? 0 : String.format("%.2f", incomeMessages.getFiveMinuteRate()));
        for (Map.Entry<String, Counter> counterEntry : specificCounters.entrySet()) {
            log.debug("{} : {}", counterEntry.getKey(), counterEntry.getValue().getCount());
        }
    }
}
