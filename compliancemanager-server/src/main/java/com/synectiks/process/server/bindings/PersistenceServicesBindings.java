/*
 * */
package com.synectiks.process.server.bindings;

import com.google.inject.AbstractModule;
import com.synectiks.process.server.alerts.AlertService;
import com.synectiks.process.server.alerts.AlertServiceImpl;
import com.synectiks.process.server.cluster.NodeService;
import com.synectiks.process.server.cluster.NodeServiceImpl;
import com.synectiks.process.server.indexer.IndexFailureService;
import com.synectiks.process.server.indexer.IndexFailureServiceImpl;
import com.synectiks.process.server.indexer.ranges.IndexRangeService;
import com.synectiks.process.server.indexer.ranges.LegacyMongoIndexRangeService;
import com.synectiks.process.server.indexer.ranges.MongoIndexRangeService;
import com.synectiks.process.server.inputs.InputService;
import com.synectiks.process.server.inputs.InputServiceImpl;
import com.synectiks.process.server.inputs.persistence.InputStatusService;
import com.synectiks.process.server.inputs.persistence.MongoInputStatusService;
import com.synectiks.process.server.notifications.NotificationService;
import com.synectiks.process.server.notifications.NotificationServiceImpl;
import com.synectiks.process.server.security.AccessTokenService;
import com.synectiks.process.server.security.AccessTokenServiceImpl;
import com.synectiks.process.server.security.MongoDBSessionService;
import com.synectiks.process.server.security.MongoDBSessionServiceImpl;
import com.synectiks.process.server.shared.users.UserService;
import com.synectiks.process.server.streams.StreamRuleService;
import com.synectiks.process.server.streams.StreamRuleServiceImpl;
import com.synectiks.process.server.streams.StreamService;
import com.synectiks.process.server.streams.StreamServiceImpl;
import com.synectiks.process.server.system.activities.SystemMessageService;
import com.synectiks.process.server.system.activities.SystemMessageServiceImpl;
import com.synectiks.process.server.users.UserServiceImpl;

public class PersistenceServicesBindings extends AbstractModule {
    @Override
    protected void configure() {
        bind(SystemMessageService.class).to(SystemMessageServiceImpl.class);
        bind(AlertService.class).to(AlertServiceImpl.class);
        bind(NotificationService.class).to(NotificationServiceImpl.class);
        bind(IndexFailureService.class).to(IndexFailureServiceImpl.class);
        bind(NodeService.class).to(NodeServiceImpl.class);
        bind(IndexRangeService.class).to(MongoIndexRangeService.class).asEagerSingleton();
        bind(LegacyMongoIndexRangeService.class).asEagerSingleton();
        bind(InputService.class).to(InputServiceImpl.class);
        bind(StreamRuleService.class).to(StreamRuleServiceImpl.class);
        bind(UserService.class).to(UserServiceImpl.class);
        bind(StreamService.class).to(StreamServiceImpl.class);
        bind(AccessTokenService.class).to(AccessTokenServiceImpl.class);
        bind(MongoDBSessionService.class).to(MongoDBSessionServiceImpl.class);
        bind(InputStatusService.class).to(MongoInputStatusService.class).asEagerSingleton();
    }
}
