package woo.core;

import java.io.Serializable;

import java.util.TreeMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import woo.core.Transaction.Transaction;


public class Supplier implements Serializable, Comparable<Supplier> {

	private final String _supplierKey;
	private final String _name;
	private String _address;
	private boolean _status;
	private Map<String, Product> _products;
	private List<Transaction> _transactions;


	public Supplier(String id, String name, String address) {
		_supplierKey = id;
		_name = name;
		_address = address;
		_status = true;
		_products = new TreeMap<String, Product>();
		_transactions = new ArrayList<Transaction>();
	}



	/**
	 * @param supplier
	 */
	@Override
	public int compareTo(Supplier supplier) {
		if (supplier._supplierKey.equals(this._supplierKey))
			return supplier._name.compareTo(this._name);
		return supplier._supplierKey.compareTo(this._supplierKey);
	}


	/**
	 * @param obj
	 */
	@Override
  	public boolean equals(Object obj) {
  		Supplier client = (Supplier) obj;

  		return (obj instanceof Supplier) && 
  			client.getSupplierKey().equals(this.getSupplierKey());
  	}



  	public String toString() {
  		return _supplierKey + "|" + _name + "|" + _address + "|";
  	}


	public String getSupplierKey() {
		return this._supplierKey;
	}


	public boolean getStatus() {
		return this._status;
	}


	public boolean toggleStatus() {
		this._status = !this._status;
		return this._status;
	}


	public int hashCode() {
		return this._supplierKey.hashCode();
	}
	

	/**
	 * @param product
	 */
	public void addProduct(Product product) {
		_products.put(product.getKey(), product);
	}


	public boolean hasProduct(String productKey) {
		return _products.containsKey(productKey);
	}


	public void addTransaction(Transaction t){
		_transactions.add(t);
	}


	public List<Transaction> getTransactions(){
		return Collections.unmodifiableList(_transactions);
	}
}