package com.synectiks.process.server.syslog4j.impl.net.tcp.pool;

import com.synectiks.process.server.syslog4j.SyslogRuntimeException;
import com.synectiks.process.server.syslog4j.impl.AbstractSyslogWriter;
import com.synectiks.process.server.syslog4j.impl.net.tcp.TCPNetSyslog;
import com.synectiks.process.server.syslog4j.impl.pool.AbstractSyslogPoolFactory;
import com.synectiks.process.server.syslog4j.impl.pool.generic.GenericSyslogPoolFactory;

/**
 * PooledTCPNetSyslog is an extension of TCPNetSyslog which provides support
 * for Apache Commons Pool.
 * <p/>
 * <p>Syslog4j is licensed under the Lesser GNU Public License v2.1.  A copy
 * of the LGPL license is available in the META-INF folder in all
 * distributions of Syslog4j and in the base directory of the "doc" ZIP.</p>
 *
 * @author &lt;syslog4j@productivity.org&gt;
 * @version $Id: PooledTCPNetSyslog.java,v 1.5 2008/12/10 04:30:15 cvs Exp $
 */
public class PooledTCPNetSyslog extends TCPNetSyslog {
    private static final long serialVersionUID = 4279960451141784200L;

    protected AbstractSyslogPoolFactory poolFactory = null;

    public void initialize() throws SyslogRuntimeException {
        super.initialize();

        this.poolFactory = createSyslogPoolFactory();

        this.poolFactory.initialize(this);
    }

    protected AbstractSyslogPoolFactory createSyslogPoolFactory() {
        AbstractSyslogPoolFactory syslogPoolFactory = new GenericSyslogPoolFactory();

        return syslogPoolFactory;
    }

    public AbstractSyslogWriter getWriter() {
        try {
            AbstractSyslogWriter syslogWriter = this.poolFactory.borrowSyslogWriter();

            return syslogWriter;

        } catch (Exception e) {
            throw new SyslogRuntimeException(e);
        }
    }

    public void returnWriter(AbstractSyslogWriter syslogWriter) {
        try {
            this.poolFactory.returnSyslogWriter(syslogWriter);

        } catch (Exception e) {
            throw new SyslogRuntimeException(e);
        }
    }

    public void flush() throws SyslogRuntimeException {
        try {
            this.poolFactory.clear();

        } catch (Exception e) {
            //
        }
    }

    public void shutdown() throws SyslogRuntimeException {
        try {
            this.poolFactory.close();

        } catch (Exception e) {
            //
        }
    }
}
