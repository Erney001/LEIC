package woo.core;

import java.io.Serializable;

public class Item implements Serializable {

    private Product _product;
    private int _quantity;

    public Item(Product product, int quantity){
        _product = product;
        _quantity = quantity;
    }


    public String getProductID() {
        return _product.getKey();
    }

    public Product getProduct() {
        return _product;
    }

    public int getAmmount() {
        return _quantity;
    }

    public String toString() {
        return _product.getKey() + "|" + _quantity;
    }
}