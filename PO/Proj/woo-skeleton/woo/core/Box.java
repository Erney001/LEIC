package woo.core;

public class Box extends Product {

	private final String _serviceLevel;

	public Box (String key, int price, int criticalValue,
				 String supplierKey, String serviceLevel) {
		super(key, price, criticalValue, supplierKey);
		_serviceLevel = serviceLevel;
	}



	public String getServiceLevel() {
		return _serviceLevel;
	}


	public String toString() {
		return "BOX|" + getKey() + "|" + getSupplierKey() + "|" + getPrice() + "|" + 
			getCriticalValue() + "|" + getStock() + "|" + this._serviceLevel;
	}
}