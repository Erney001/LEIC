package woo.core;

import java.io.Serializable;

import java.util.TreeMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.List;

import woo.core.Transaction;
import woo.core.Product;

public class Supplier implements Serializable, Comparable<Supplier> {

	private final String _supplierKey;
	private final String _name;
	private String _address;
	private boolean _transactionsStatus;
	private Map<String, Product> _products;
	private List<Transaction> _transactions;


	public Supplier(String id, String name, String address) {
		_supplierKey = id;
		_name = name;
		_address = address;
		_transactionsStatus = true;
		_products = new TreeMap<String, Product>();
		_transactions = new ArrayList<Transaction>();
	}


	/**
	 * @param supplier
	 */
	@Override
	public int compareTo(Supplier supplier) {
		if (supplier._supplierKey.equalsIgnoreCase(this._supplierKey))
			return (supplier._name).toLowerCase().compareTo((this._name).toLowerCase());
		return (supplier._supplierKey).toLowerCase().compareTo((this._supplierKey).toLowerCase());
	}

	/**
	 * @param obj
	 */
	@Override
  	public boolean equals(Object obj) {
  		Supplier client = (Supplier) obj;
  		return (obj instanceof Supplier) && 
  			client.getSupplierKey().equalsIgnoreCase(this.getSupplierKey());
  	}


  	@Override
  	public int hashCode() {
		return (_supplierKey.toLowerCase()).hashCode();
	}


  	public String toString() {
  		return _supplierKey + "|" + _name + "|" + _address + "|";
  	}


	public String getSupplierKey() {
		return _supplierKey;
	}

	public String getName() {
		return _name;
	}


	public boolean getTransactionsStatus() {
		return _transactionsStatus;
	}

	boolean toggleTransactionsStatus() {
		_transactionsStatus = ! _transactionsStatus;
		return _transactionsStatus;
	}

	
	/**
	 * @param product
	 */
	void addProduct(Product product) {
		_products.put(product.getKey().toLowerCase(), product);
	}

	/**
	 * @param productKey
	 */
	public boolean hasProduct(String productKey) {
		return _products.containsKey(productKey.toLowerCase());
	}


	/**
	 * @param order
	 */
	void addTransaction(Transaction order){
		_transactions.add(order);
	}

	public Collection<Transaction> getTransactions() {
		return Collections.unmodifiableList(_transactions);
	}
}