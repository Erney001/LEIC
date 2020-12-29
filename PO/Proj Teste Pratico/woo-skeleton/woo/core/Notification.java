package woo.core;

import java.io.Serializable;


public class Notification implements Serializable {

    private String _type;
    private Product _product;

    public Notification(String type, Product product){
        _type = type;
        _product = product;
    }


    public String toString() {
		return _type + "|" + _product.getKey() + "|" + _product.getPrice();
	}
}