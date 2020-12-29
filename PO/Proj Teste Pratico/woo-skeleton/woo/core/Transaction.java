package woo.core;

import java.io.Serializable;

import java.util.Collection;


public abstract class Transaction implements Serializable {

    private int _id;
    private double _baseValue;
    private int _paymentDate;

    public Transaction(int id, double baseValue, int paymentDate){
        _id = id;
        _baseValue = baseValue;
        _paymentDate = paymentDate;
    }

    public abstract String toString();

    public int hashCode() {
        return _id;
    }


    public int getID() {
        return _id;
    }
    

    public double getBaseValue() {
        return _baseValue;
    }

    public int getPaymentDate() {
        return _paymentDate;
    }

    public boolean isPaid() {
        return getPaymentDate() >= 0;
    }

    /**
    * @param paymentDate
    */
    void setPaymentDate(int paymentDate) {
        _paymentDate = paymentDate;
    }
}