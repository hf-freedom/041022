package com.saas.barbershop.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderNoGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final AtomicInteger SEQUENCE = new AtomicInteger(0);

    public static synchronized String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
        int seq = SEQUENCE.incrementAndGet();
        if (seq > 9999) {
            SEQUENCE.set(0);
            seq = 0;
        }
        return "ORD" + timestamp + String.format("%04d", seq);
    }
}
