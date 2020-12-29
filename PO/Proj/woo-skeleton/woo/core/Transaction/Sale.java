package woo.core.Transaction;

import java.io.Serializable;
import woo.core.Client;

public class Sale extends Transaction {
    private Client _client;
    private Item _itemSold;
    private int _paymentDate;
    private int _amountPaid;
    private int _amountToPay;
    private int _paymentDeadline;

    public Sale(int id, int cost, int date, Client client, Item item, int paymentDate){
        super(id, cost, date);
        _client = client;
        _itemSold = item;
        _paymentDate = paymentDate;
    }

    public void pay(){
        
    }

    public void getPaid(){

    }


    public boolean isPaid(){
        return _amountToPay - _amountPaid == 0;
    }


    public String toString(){
        return super.getID() + "|" + _client.getClientKey() + "|" + _itemSold.getProductID() + "|" +
            _itemSold.getQuantity() + "|" + super.getCost() + "|" + _amountToPay + "|" + _paymentDeadline + "|" + _paymentDate;
    }
}