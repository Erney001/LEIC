package woo.core;

public class Container extends Box {

	private final String _qualityLevel;
	private final int _dateLimit;

	public Container (String key, int price, int criticalValue,
				 String supplierKey, String serviceLevel, String qualityLevel) {

		super(key, price, criticalValue, supplierKey, serviceLevel);
		_qualityLevel = qualityLevel;
		_dateLimit = 8;
	}


	public int getN() {
		return _dateLimit;
	}

	public String productTypeToStr() {
		return "CONTAINER";
	}

	public String specificInfoToStr() {
		return super.specificInfoToStr() + "|" + _qualityLevel;
	}
}