package woo.core;

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;

import java.io.Serializable;
import java.lang.Math;



public class Sale extends Transaction {
    private Client _client;
    private Item _item;
    private int _paymentDeadline;
    private double _payedValue;
    private double _currentValue;

    public Sale(int id, double baseValue, Client client, Item item, int paymentDeadline){
        
        super(id, baseValue, -1);
        _client = client;
        _item = item;
        _paymentDeadline = paymentDeadline;
    }


    public Item getItem() {
        return _item;
    }

    public Client getClient() {
        return _client;
    }


    public int getDeadline() {
        return _paymentDeadline;
    }

    @Override
    public double getBaseValue() { 
        return super.getBaseValue();
    }

    public double getPayedValue() {
        return _payedValue;
    }

    public double getCurrentValue() {
        return _currentValue;
    }


    /**
    * @param date
    */
    void computeSaleCost(int date) {
        if (! isPaid()) _currentValue = _client.computeSaleCost(this, date);
    }

    /**
    * @param pricePaid
    * @param date
    */
    void setAsPaid(double pricePaid, int date) {
        setPaymentDate(date);
        _payedValue = pricePaid;
        _currentValue = pricePaid;
    }
 

    @Override
    public String toString() {

        if (! isPaid())
            return getID() + "|" + _client.getClientKey() + "|" + _item.getProductID() + 
                "|" + _item.getAmmount() + "|" + Math.round(getBaseValue()) + "|" 
                + (int) Math.round(_currentValue) + "|" + getDeadline();

        return getID() + "|" + _client.getClientKey() + "|" + _item.getProductID() + 
            "|" + _item.getAmmount() + "|" + Math.round(getBaseValue()) + "|" 
            + (int) Math.round(_payedValue) + "|" + getDeadline() + "|" + getPaymentDate();
    }
}