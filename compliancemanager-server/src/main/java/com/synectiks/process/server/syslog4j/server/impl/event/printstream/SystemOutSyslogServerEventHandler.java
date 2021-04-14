package com.synectiks.process.server.syslog4j.server.impl.event.printstream;

import com.synectiks.process.server.syslog4j.server.SyslogServerSessionEventHandlerIF;

public class SystemOutSyslogServerEventHandler extends PrintStreamSyslogServerEventHandler {
    private static final long serialVersionUID = 1690551409588182601L;

    public static SyslogServerSessionEventHandlerIF create() {
        return new SystemOutSyslogServerEventHandler();
    }

    public SystemOutSyslogServerEventHandler() {
        super(System.out);
    }
}
