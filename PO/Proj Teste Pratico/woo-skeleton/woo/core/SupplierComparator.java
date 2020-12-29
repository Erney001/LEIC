package woo.core;

import java.io.Serializable;
import java.util.Comparator;

public class SupplierComparator implements Serializable, Comparator<Supplier> {

	public SupplierComparator() {}


	/**
	 * @param product
	 */
	@Override
	public int compare(Supplier supplier1, Supplier supplier2) {
		if ((supplier1.getSupplierKey()).equalsIgnoreCase(supplier2.getSupplierKey()))
			return (supplier1.getName()).toLowerCase().compareTo((supplier2.getName()).toLowerCase());
		return (supplier1.getSupplierKey()).toLowerCase().compareTo((supplier2.getSupplierKey()).toLowerCase());
	}
}