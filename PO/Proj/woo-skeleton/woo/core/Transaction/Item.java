package woo.core.Transaction;

import java.io.Serializable;
import woo.core.Product;

public class Item implements Serializable {
    private Product _product;
    private int _quantity;

    public Item(Product product, int quantity){
        _product = product;
        _quantity = quantity;
    }

    public String getProductID(){
        return _product.getKey();
    }

    public int getQuantity(){
        return _quantity;
    }
}