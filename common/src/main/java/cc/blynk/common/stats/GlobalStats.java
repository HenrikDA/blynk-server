package cc.blynk.common.stats;

import cc.blynk.common.model.messages.ResponseMessage;
import cc.blynk.common.model.messages.protocol.HardwareMessage;
import cc.blynk.common.model.messages.protocol.PingMessage;
import cc.blynk.common.model.messages.protocol.appllication.*;
import cc.blynk.common.model.messages.protocol.hardware.TweetMessage;
import com.codahale.metrics.Meter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/13/2015.
 */
public class GlobalStats {

    private static final Logger log = LogManager.getLogger(GlobalStats.class);
    private Meter incomeMessages;
    private Map<Class<?>, LongAdder> specificCounters;

    public GlobalStats() {
        this.incomeMessages = new Meter();

        this.specificCounters = new HashMap<>();
        specificCounters.put(GetTokenMessage.class, new LongAdder());
        specificCounters.put(RefreshTokenMessage.class, new LongAdder());
        specificCounters.put(HardwareMessage.class, new LongAdder());
        specificCounters.put(LoadProfileMessage.class, new LongAdder());
        specificCounters.put(LoginMessage.class, new LongAdder());
        specificCounters.put(PingMessage.class, new LongAdder());
        specificCounters.put(RegisterMessage.class, new LongAdder());
        specificCounters.put(SaveProfileMessage.class, new LongAdder());
        specificCounters.put(ActivateDashboardMessage.class, new LongAdder());
        specificCounters.put(DeActivateDashboardMessage.class, new LongAdder());
        specificCounters.put(TweetMessage.class, new LongAdder());
        specificCounters.put(ResponseMessage.class, new LongAdder());
    }

    public void mark(Class<?> clazz) {
        specificCounters.get(clazz).increment();
    }

    public void mark() {
        incomeMessages.mark(1);
    }

    public void log() {
        log.debug("1 min rate : {}", incomeMessages.getOneMinuteRate() < 0.01 ? 0 : String.format("%.2f", incomeMessages.getOneMinuteRate()));
        for (Map.Entry<Class<?>, LongAdder> counterEntry : specificCounters.entrySet()) {
            log.debug("{} : {}", counterEntry.getKey().getSimpleName(), counterEntry.getValue().sum());
        }
        log.debug("--------------------------------------------------------------------------------------");
    }
}
