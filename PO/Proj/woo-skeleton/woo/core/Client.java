package woo.core;

import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;
import woo.core.Observer.Observer;
import java.util.Collections;
import woo.core.Transaction.Transaction;
import woo.core.Transaction.Sale;

public class Client implements Serializable, Comparable<Client>, Observer {

	private final String _id;
	private final String _name;
	private String _address;
	private int _totalTransactionsValue;
	private int _totalTransactionsPayedValue;

	// fazer o Status como enum?
	private String _status;
	private List<Notification> _notifications;
	private List<Transaction> _transactions;

	public Client(String clientid, String name, String address) {
		_id = clientid;
		_name = name;
		_address = address;
		_totalTransactionsValue = 0;
		_totalTransactionsPayedValue = 0;
		_status = "NORMAL";
		// manter ArrayList ou mudar ?
		_notifications = new ArrayList<Notification>();
		_transactions = new ArrayList<Transaction>();
	}


	/**
	 * @param client
	 */
	@Override
	public int compareTo(Client client) {
		if (client._id.equals(this._id))
			return client._name.compareTo(this._name);
		return client._id.compareTo(this._id);
	}


	/**
	 * @param obj
	 */
	@Override
  	public boolean equals(Object obj) {

  		Client client = (Client) obj;
  		return (obj instanceof Client) && client.getClientKey().equals(this.getClientKey());
  	}



	public int hashCode() {
		return _id.hashCode();
	}


	public String getClientKey() {
		return _id;
	}


	public String getClientName() {
		return _name;
	}


	public String getClientAddress() {
		return _address;
	}


	public void addNotification(Notification n){
		_notifications.add(n);
	}


	public boolean toggleProductNotifications(Notification n){
		if(_notifications.contains(n)){
			_notifications.remove(n);
			return false;
		}

		_notifications.add(n);
		return true;
	}


	public List<Notification> getNotifications(){
		List<Notification> notifications = Collections.unmodifiableList(_notifications);
		return notifications;
	}


	public void addTransaction(Transaction t){
		_transactions.add(t);
	}


	public List<Transaction> getPaidTransactions(){
		List<Transaction> transactions = new ArrayList<Transaction>();

		for(Transaction t: _transactions){
			if(((Sale) t).isPaid()){
				transactions.add(t);
			}
		}

		return Collections.unmodifiableList(transactions);
	}

	
	public String toString() {
		return _id + "|" + _name + "|" + _address + "|" + _status + "|" +
			_totalTransactionsValue + "|" + _totalTransactionsPayedValue;
	}
}