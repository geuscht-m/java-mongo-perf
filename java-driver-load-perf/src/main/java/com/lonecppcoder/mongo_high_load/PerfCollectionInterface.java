package com.lonecppcoder.mongo_high_load;

import java.util.concurrent.atomic.AtomicLong;

interface PerfDataCollector {
    AtomicLong getStat();
    
    default void incStats(long num) {
        getStat().addAndGet(num);
    }

    default long getAndReset() {
        return getStat().getAndSet(0L);
    }
}
