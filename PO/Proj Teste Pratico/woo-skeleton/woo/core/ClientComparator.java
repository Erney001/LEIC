package woo.core;

import java.io.Serializable;

import java.util.Comparator;

public class ClientComparator implements Serializable, Comparator<Client> {

	public ClientComparator() {}


	/**
	 * @param client
	 */
	@Override
	public int compare(Client client1, Client client2) {
		if ((client1.getClientKey()).toLowerCase().equalsIgnoreCase((client2.getClientKey()).toLowerCase()))
			return (client1.getClientName()).toLowerCase().compareTo((client2.getClientName()).toLowerCase());
		return (client1.getClientKey()).toLowerCase().compareTo((client2.getClientKey()).toLowerCase());
	}

}