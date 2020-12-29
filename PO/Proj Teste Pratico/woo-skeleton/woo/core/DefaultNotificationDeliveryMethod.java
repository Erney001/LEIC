package woo.core;



import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;


public class DefaultNotificationDeliveryMethod implements NotificationDeliveryMethod {

    private List<Notification> _notifications;

    public DefaultNotificationDeliveryMethod() {
        _notifications = new ArrayList<Notification>();
    }


    /**
    * @param notification
    */
    public void notify(Notification notification) {
        _notifications.add(notification);
    }

    public Collection<Notification> getNotifications() {
		return Collections.unmodifiableList(_notifications);
	}

    public void clearNotifications() {
		_notifications = new ArrayList<Notification>();
	}
}