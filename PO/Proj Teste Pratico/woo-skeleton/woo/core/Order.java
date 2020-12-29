package woo.core;

import java.io.Serializable;
import java.lang.Math;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


public class Order extends Transaction {
    private Supplier _supplier;
    private List<Item> _items;


    public Order(int id, double baseValue, Supplier supplier, Collection<Item> items, int pDate){
        super(id, baseValue, pDate);
        _supplier = supplier;
        _items = new ArrayList<Item>(items);
    }


    /**
    * @param item
    */
    void addItem(Item item) {
        _items.add(item);
    }


    @Override
    public double getBaseValue() {
        return super.getBaseValue();
    }


    public Collection<Item> getItems() {
        return Collections.unmodifiableList(_items);
    }

    @Override
    public String toString() {
        String stringToReturn = getID() + "|" + _supplier.getSupplierKey() + "|" +
            Math.round(getBaseValue()) + "|" + getPaymentDate();

        for (Item i : _items)
            stringToReturn += "\n" + i.toString();
        
        return stringToReturn;
    }
}