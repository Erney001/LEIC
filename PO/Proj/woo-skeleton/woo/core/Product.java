package woo.core;

import java.io.Serializable;

public abstract class Product implements Serializable, Comparable<Product> {

	private final String _productKey;
	private int _stock;
	private int _price;
	private final int _criticalValue;
	private final String _supplierKey;

	public Product(String key, int price, int criticalValue, String supplierKey) {
		_productKey = key;
		_price = price;
		_criticalValue = criticalValue;
		_supplierKey = supplierKey;
		_stock = 0;
	}


	/**
	 * @param product
	 */
	@Override
	public int compareTo(Product product) {
		if (product._productKey.equals(this._productKey))
			return product._stock - this._stock;
		return product._productKey.compareTo(this._productKey);
	}

	// fazer como o stor fez e meter aqui codigo tb
	// dps cada subclasse chama super() + o codigo especifico
	public abstract String toString();
	

	public int hashCode() {
		return _productKey.hashCode();
	}


	public String getKey() {
		return _productKey;
	}


	public int getStock() {
		return _stock;
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


	/**
	 * @param stock
	 */
	public void addStock(int stock) {
		_stock += stock;
	}


	public void removeStock(int stock) {
		_stock -= stock;
	}


	/**
	 * @param price
	 */
	public void changePrice(int price){
		_price = price;
	}
}