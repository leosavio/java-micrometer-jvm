package com.leosavio.micrometer;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.binder.BaseUnits;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import com.leosavio.micrometer.metricas.MetricasRegistro;

public class CustomMetrics {

    public void simulacao() {
        // you can format to any output you want
        NumberFormat formatter = new DecimalFormat("0.00");

        // memoria
        // MeterRegistry registryMem = new SimpleMeterRegistry();
        MeterRegistry registryMem = MetricasRegistro.prometheus();
        // nome da aplicacao
        registryMem.config().commonTags("application", "TESTE_MICROMETER");

        new JvmMemoryMetrics().bindTo(registryMem);
        Gauge memUsed = registryMem.find("jvm.memory.used").gauge();
        Gauge memUsed2 = registryMem.find("jvm.memory.committed").gauge();
        Gauge memUsed3 = registryMem.find("jvm.memory.max").gauge();

        System.out.println("Memory used: " + formatter.format(memUsed.value()));

        // MeterRegistry registry = new StatsdMeterRegistry(config, Clock.SYSTEM);
        // num cpu
        Gauge gaugeCPU = Gauge.builder("system.cpu.count", Runtime.getRuntime(), Runtime::availableProcessors)
                .description("The number of processors available").baseUnit(BaseUnits.ROWS).register(registryMem);

        System.out.println("Num CPU: " + gaugeCPU.value());

        // just for presentation
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        // just for presentation

        List<String> list = new ArrayList<>();

        Gauge gaugeList = Gauge.builder("cache.size", list, List::size).register(registryMem);

        System.out.println("Size of List: " + gaugeList.value());
        for (int i = 0; i < 10000; i++) {
            list.add("1-" + i);
        }
        list.add("1");
        System.out.println("Size of List: " + gaugeList.value());

        // contador loop
        Counter myCounter = Counter.builder("loop.counter.test").description("total_loop")
                .tags("total_loop", "total_loop.test").register(registryMem);
        Metrics.addRegistry(registryMem);
        for (int i = 0; i < 50; i++) {
            myCounter.increment();
        }

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
         System.out.println("Counter: " + myCounter.count());
         System.out.println("Memory used: " + formatter.format(memUsed.value()));
         System.out.println("Alocado Memoria: " + formatter.format(memUsed2.value()));
         System.out.println("Total Memoria: " + formatter.format(memUsed3.value()));
    }
}