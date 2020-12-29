package woo.core;

import java.io.Serializable;

import java.util.Collection;


public interface NotificationDeliveryMethod extends Serializable {
    
    /** Each specific delivery method will override the following method to
        send the notifications to the client */

    void notify(Notification notification);
    default Collection<Notification> getNotifications() { return null; }
    default void clearNotifications() {}
}