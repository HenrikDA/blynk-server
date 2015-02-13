package cc.blynk.common.stats;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/13/2015.
 */
public class GlobalStats {

    private MetricRegistry metricRegistry = new MetricRegistry();

    public Meter incomeMessages = metricRegistry.meter("incomeMessages");


}
