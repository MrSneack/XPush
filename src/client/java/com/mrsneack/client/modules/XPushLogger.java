package com.mrsneack.client.modules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XPushLogger {
    private static final Logger LOG = LoggerFactory.getLogger("XPush");

    public void INFO(String message) {
        LOG.info(message);
    }

    public void ERROR(String message) {
        LOG.error(message);
    }

    public void WARN(String message) {
        LOG.warn(message);
    }
}
