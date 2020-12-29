package woo.core;

public class Book extends Product {

	private final String _title;
	private final String _author;
	private final String _isbn;
	private final int _dateLimit;

	public Book(String key, int price, int criticalValue, String supplierKey, 
				String title, String author, String isbn) {
		super(key, price, criticalValue, supplierKey);
		_title = title;
		_author = author;
		_isbn = isbn;
		_dateLimit = 3;
	}

	
	public int getN() {
		return _dateLimit;
	}

	public String productTypeToStr() {
		return "BOOK";
	}

	public String specificInfoToStr() {
		return this._title + "|" + this._author + "|" + this._isbn;
	}
}