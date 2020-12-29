package woo.core;

import java.io.Serializable;

import java.util.List;
import java.util.ArrayList;


public abstract class Product implements Serializable, Comparable<Product> {

	private final String _productKey;
	private int _stock;
	private int _price;
	private final int _criticalValue;
	private final String _supplierKey;
	private List<Observer> _observers;


	public Product(String key, int price, int criticalValue, String supplierKey) {
		_productKey = key;
		_price = price;
		_criticalValue = criticalValue;
		_supplierKey = supplierKey;
		_stock = 0;
		_observers = new ArrayList<Observer>();
	}


	public int hashCode() {
		return (_productKey.toLowerCase()).hashCode();
	}


	/**
	 * @param product
	 */
	@Override
	public int compareTo(Product product) {
		if (product._productKey.equalsIgnoreCase(this._productKey))
			return product._stock - this._stock;
		return (product._productKey).toLowerCase().compareTo((this._productKey).toLowerCase());
	}


	public final String toString() {
		return productTypeToStr() + "|" + productInfoToStr() + "|" + specificInfoToStr();
	}

	private String productInfoToStr() {
		return getKey() + "|" + getSupplierKey() + "|" + getPrice() + "|" + 
				getCriticalValue() + "|" + getStock();
	}

	abstract String productTypeToStr();

	abstract String specificInfoToStr();

	abstract int getN();


	public String getKey() {
		return _productKey;
	}

	public int getPrice() {
		return _price;
	}

	public int getCriticalValue() {
		return _criticalValue;
	}

	public String getSupplierKey() {
		return _supplierKey;
	}

	public int getStock() {
		return _stock;
	}


	/**
	 * @param stock
	 */
	int setStock(int stock) {
		return _stock = stock;
	}

	/**
	 * @param stock
	 */
	void addStock(int stock) {
		int prevStock = _stock;
		_stock += stock;

		if (prevStock == 0 && _stock > 0){
			Notification n = new Notification("NEW", this);
			notifyObservers(n);
		}
	}

	/**
	 * @param stock
	 */
	void removeStock(int stock) {
		_stock -= stock;
	}

	/**
	 * @param price
	 */
	void changePrice(int price){
		int prevPrice = _price;
		_price = price;

		if (_price < prevPrice) {
			Notification n = new Notification("BARGAIN", this);
			notifyObservers(n);
		}
	}



	/* Product Notifications related things */

	/**
	 * @param observer
	 */
	void addToObservers(Observer observer) {
        _observers.add(observer);
    }
    
    /**
	 * @param observer
	 */
    void removeFromObservers(Observer observer) {
        _observers.remove(observer);
    }

    /**
	 * @param observer
	 */
    public boolean hasObserver(Observer observer) {
    	return _observers.contains(observer);
    }

    /**
	 * @param notification
	 */
    void notifyObservers(Notification notification) {
        for (Observer obs: _observers)
            obs.notify(notification);
    }

}