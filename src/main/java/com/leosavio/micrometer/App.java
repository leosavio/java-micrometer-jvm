package com.leosavio.micrometer;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.binder.BaseUnits;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.statsd.StatsdConfig;
import io.micrometer.statsd.StatsdMeterRegistry;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )  throws InterruptedException
    {
        
        // you can format to any output you want
        NumberFormat formatter = new DecimalFormat("0.00");

        StatsdConfig config = new StatsdConfig() {

            @Override
            public String get(String key) {
                // TODO Auto-generated method stub
                return null;
            }


        };
        
        //memoria
        MeterRegistry registryMem = new SimpleMeterRegistry();
        new JvmMemoryMetrics().bindTo(registryMem);
        Gauge memUsed = registryMem.find("jvm.memory.used").gauge();
        Gauge memUsed2 = registryMem.find("jvm.memory.committed").gauge();
        Gauge memUsed3 = registryMem.find("jvm.memory.max").gauge();

        System.out.println("Memory used: " + formatter.format(memUsed.value()));

        MeterRegistry registry = new StatsdMeterRegistry(config, Clock.SYSTEM);
        //num cpu
        Gauge gaugeCPU = Gauge.builder("system.cpu.count", Runtime.getRuntime(), Runtime::availableProcessors)
        .description("The number of processors available")
        .baseUnit(BaseUnits.ROWS)
        .register(registry);

        System.out.println("Num CPU: " + gaugeCPU.value());
        
        // SimpleMeterRegistry registryList = new SimpleMeterRegistry();
        List<String> list = new ArrayList<>();
        
        Gauge gaugeList = Gauge
        .builder("cache.size", list, List::size)
        .register(registry);
        
        System.out.println("Size of List: " + gaugeList.value());
        for(int i = 0; i < 10000; i++){
            list.add("1-" + i);
        }
        list.add("1");
        System.out.println("Size of List: " + gaugeList.value());

        //Counter increment in loop
        Counter myCounter = Counter.builder("loop.counter.test").description("total_loop").tags("total_loop","total_loop.test").register(registry);
        Metrics.addRegistry(registry);
        for (int i = 0; i < 50; i++) {
            myCounter.increment();
        }

        Thread.sleep(15000);
        System.out.println("Counter: " + myCounter.count());
        System.out.println("Memory used: " + formatter.format(memUsed.value()));
        System.out.println("Alocado Memoria: " + formatter.format(memUsed2.value()));
        System.out.println("Total Memoria: " + formatter.format(memUsed3.value()));
    }
}
