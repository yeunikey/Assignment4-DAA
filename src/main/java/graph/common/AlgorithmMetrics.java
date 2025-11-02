package graph.common;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class AlgorithmMetrics implements Metrics {

    private long startTime;
    private long endTime;
    private final Map<String, Long> counters = new HashMap<>();

    public void start() {
        reset();
        this.startTime = System.nanoTime();
    }

    public void stop() {
        this.endTime = System.nanoTime();
    }

    public void incrementCounter(String name) {
        counters.put(name, counters.getOrDefault(name, 0L) + 1);
    }

    public void addCounter(String name, long value) {
        counters.put(name, counters.getOrDefault(name, 0L) + value);
    }

    @Override
    public long getTimeNs() {
        return (endTime - startTime);
    }

    @Override
    public Map<String, Long> getCounters() {
        return new HashMap<>(counters);
    }

    @Override
    public void reset() {
        startTime = 0;
        endTime = 0;
        counters.clear();
    }

    @Override
    public String getSummary() {
        String counterSummary = counters.entrySet().stream()
                .map(e -> String.format("%s: %d", e.getKey(), e.getValue()))
                .collect(Collectors.joining(", "));
        return String.format("Time: %.4f ms, Counters: [%s]",
                getTimeNs() / 1_000_000.0, counterSummary);
    }

}