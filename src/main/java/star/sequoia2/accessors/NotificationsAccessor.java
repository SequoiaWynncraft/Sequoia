package star.sequoia2.accessors;


import star.sequoia2.client.NectarClient;
import star.sequoia2.client.notifications.Notifications;

public interface NotificationsAccessor {
    default Notifications notifications() {
        return NectarClient.getNotifications();
    }
}
