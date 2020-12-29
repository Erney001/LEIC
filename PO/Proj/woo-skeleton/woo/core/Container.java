package woo.core;

public class Container extends Box {

	private final String _qualityLevel;

	public Container (String key, int price, int criticalValue,
				 String supplierKey, String serviceLevel, String qualityLevel) {
		super(key, price, criticalValue, supplierKey, serviceLevel);
		_qualityLevel = qualityLevel;
	}
	

	public String toString() {
		return "CONTAINER|" + getKey() + "|" + getSupplierKey() + "|" + getPrice() + "|" + 
			getCriticalValue() + "|" + getStock() + "|" + 
			getServiceLevel() + "|" + this._qualityLevel;
	}
}