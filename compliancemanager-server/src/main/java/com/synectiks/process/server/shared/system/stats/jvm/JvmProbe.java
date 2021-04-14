/*
 * */
package com.synectiks.process.server.shared.system.stats.jvm;

import javax.inject.Singleton;

@Singleton
public class JvmProbe {
    public JvmStats jvmStats() {
        return JvmStats.INSTANCE;
    }
}
