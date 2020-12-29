package woo.core.Transaction;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import woo.core.Supplier;

public class Order extends Transaction {
    private Supplier _supplier;
    private List<Item> _items;
    //private int _dataPagamento;

    public Order(int id, int cost, int date, Supplier supplier){
        super(id, cost, date);
        _supplier = supplier;
        _items = new ArrayList<Item>();
    }

    public void addItem(Item item){
        _items.add(item);
    }


    public String printOrder(){
        String base = this.toString();

        for(Item i: _items){
            String s = i.getProductID() + "|" + i.getQuantity() + "\n";
            base = base.concat(s);
        }

        return base;
    }


    public String toString(){
        return super.getID() + "|" + _supplier.getSupplierKey() + "|" + super.getCost(); // + "|" + _dataPagamento;
    }
}