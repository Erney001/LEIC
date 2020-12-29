package woo.core;

public class Book extends Product {

	private final String _title;
	private final String _author;
	private final String _isbn;

	public Book(String key, int price, int criticalValue, String supplierKey, 
				String title, String author, String isbn) {
		super(key, price, criticalValue, supplierKey);
		_title = title;
		_author = author;
		_isbn = isbn;
	}

	

	public String toString() {
		return "BOOK|" + getKey() + "|" + getSupplierKey() + "|" + getPrice() + "|" + 
			getCriticalValue() + "|" + getStock() + "|" + this._title + "|" +
			this._author + "|" + this._isbn;
	}
}