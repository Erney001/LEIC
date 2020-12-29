package woo.core;

import java.io.Serializable;


public interface Observer extends Serializable {

    void notify(Notification n);
}