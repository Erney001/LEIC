package woo.core;

import java.io.Serializable;
import java.util.Set;
import java.util.HashSet;
import woo.core.Observer.Observer;

public class Notification implements Serializable {
    private String _type;
    private Product _product;
    private Set<Observer> _observers = new HashSet<>();

    // implementar com PD Strategy mais tarde
    //private String _methodOfDelivery;

    // fazer outro construtor que n tem o type?
    public Notification(String type, Product product){
        _type = type;
        _product = product;
    }


    /*public boolean toggleNotification(){
        _state = !_state;
        return _state;
    }


    public String getNotificationProductKey(){
        return _product.getKey();
    }*/


    public void setType(String type){
        _type = type;
        notifyObservers();
    }


    public boolean add(Observer obs) {
        return _observers.add(obs);
    }
    

    public boolean remove(Observer obs) {
        return _observers.remove(obs);
    }

    
    private void notifyObservers() {
        for (Observer obs: _observers){
            // vai poassar o obj Notification para o Client ficar com ela e ter um conjunto das suas notificacoes
            obs.addNotification(this);
        }
    }


    @Override
    public String toString() {
		return _type + "|" + _product.getKey() + "|" + _product.getPrice();
	}
}