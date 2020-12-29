package woo.core.Transaction;

import java.io.Serializable;

public abstract class Transaction implements Serializable {
    private int _id;
    private int _cost;
    private int _date;


    public Transaction(int id, int cost, int date){
        _id = id;
        _cost = cost;
        _date = date;
    }


    public int getID(){
        return _id;
    }


    public int getCost(){
        return _cost;
    }


    public int getDate(){
        return _date;
    }


    public abstract String toString();
}