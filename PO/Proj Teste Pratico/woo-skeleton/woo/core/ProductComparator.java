package woo.core;

import java.io.Serializable;
import java.util.Comparator;

public class ProductComparator implements Serializable, Comparator<Product> {

	public ProductComparator() {}


	/**
	 * @param product
	 */
	@Override
	public int compare(Product product1, Product product2) {
		if ((product1.getKey()).equalsIgnoreCase(product2.getKey()))
			return product1.getStock() - product2.getStock();
		return (product1.getKey()).toLowerCase().compareTo((product2.getKey()).toLowerCase());
	}
}