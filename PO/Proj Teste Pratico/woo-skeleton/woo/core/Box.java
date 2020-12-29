package woo.core;

public class Box extends Product {

	private final String _serviceLevel;
	private final int _dateLimit;

	public Box (String key, int price, int criticalValue,
				String supplierKey, String serviceLevel) {

		super(key, price, criticalValue, supplierKey);
		_serviceLevel = serviceLevel;
		_dateLimit = 5;
	}



	public int getN() {
		return _dateLimit;
	}

	public String getServiceLevel() {
		return _serviceLevel;
	}

	public String productTypeToStr() {
		return "BOX";
	}

	public String specificInfoToStr() {
		return this._serviceLevel;
	}
}