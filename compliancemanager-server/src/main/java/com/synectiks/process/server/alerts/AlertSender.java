/*
 * */
package com.synectiks.process.server.alerts;

import org.apache.commons.mail.EmailException;

import com.synectiks.process.server.plugin.Message;
import com.synectiks.process.server.plugin.alarms.AlertCondition;
import com.synectiks.process.server.plugin.alarms.transports.TransportConfigurationException;
import com.synectiks.process.server.plugin.configuration.Configuration;
import com.synectiks.process.server.plugin.streams.Stream;

import java.util.List;

public interface AlertSender {
    void initialize(Configuration configuration);

    void sendEmails(Stream stream, EmailRecipients recipients, AlertCondition.CheckResult checkResult) throws TransportConfigurationException, EmailException;

    void sendEmails(Stream stream, EmailRecipients recipients, AlertCondition.CheckResult checkResult, List<Message> backlog) throws TransportConfigurationException, EmailException;
}
