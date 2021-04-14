/*
 * */
package com.synectiks.process.server.notifications;

import com.synectiks.process.server.cluster.Node;
import com.synectiks.process.server.plugin.database.PersistedService;

import java.util.List;

/**
 * @author Dennis Oelkers <dennis@torch.sh>
 */
public interface NotificationService extends PersistedService {
    Notification build();

    Notification buildNow();

    boolean fixed(Notification.Type type);

    boolean fixed(Notification.Type type, Node node);

    boolean isFirst(Notification.Type type);

    List<Notification> all();

    boolean publishIfFirst(Notification notification);

    boolean fixed(Notification notification);

    int destroyAllByType(Notification.Type type);
}
