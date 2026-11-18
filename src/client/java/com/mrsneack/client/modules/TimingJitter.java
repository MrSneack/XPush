package com.mrsneack.client.modules;

import com.mrsneack.client.XpushClient;

import java.util.concurrent.ThreadLocalRandom;

public class TimingJitter {

    private static final int MAX_JITTER = 3;

    public static int apply(int baseDelay) {

        if (!XpushClient.config.enabled ||
                !XpushClient.config.enabledJitter) {
            return baseDelay;
        }

        // Prevent delay from becoming less than 1
        int minJitter = Math.max(-MAX_JITTER, 1 - baseDelay);

        // Random value from -3 to +3
        int jitter = ThreadLocalRandom.current()
                .nextInt(minJitter, MAX_JITTER + 1);

        return baseDelay + jitter;
    }
}