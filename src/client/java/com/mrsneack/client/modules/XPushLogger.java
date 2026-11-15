package com.mrsneack.client.modules;

import org.slf4j.Logger;

public class XPushLogger {
    private static Logger LOG;

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
