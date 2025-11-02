package dev.yeunikey.common;

import java.util.Map;

public interface Metrics {

    long getTimeNs();
    Map<String, Long> getCounters();
    void reset();
    String getSummary();

}

