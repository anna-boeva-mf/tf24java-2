package ru.tbank.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import org.springframework.stereotype.Component;

@Component
public class EventRequestCount {
    private final Counter counter;

    public EventRequestCount(MeterRegistry meterRegistry) {
        counter = meterRegistry.counter("event_request_counter");
    }

    public void incrementCounter() {
        counter.increment();
    }
}
