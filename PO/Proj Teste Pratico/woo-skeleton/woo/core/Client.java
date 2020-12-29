package woo.core;

import java.lang.Math;
import java.io.Serializable;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;


public class Client implements Serializable, Comparable<Client>, Observer {

	private final String _id;
	private final String _name;
	private final String _address;
	private double _totalTransactionsValue;
	private double _totalTransactionsPayedValue;
	private List<Transaction> _transactions;
	private ClientStatus _status;
	private double _numPoints;
	private NotificationDeliveryMethod _deliveryMethod;


	public Client(String clientid, String name, String address) {
		_id = clientid;
		_name = name;
		_address = address;
		_totalTransactionsValue = 0;
		_totalTransactionsPayedValue = 0;
		setClientStatus(new NormalStatus());
		setDeliveryMethod(new DefaultNotificationDeliveryMethod());
		_transactions = new ArrayList<Transaction>();
	}


	/**
	 * @param client
	 */
	@Override
	public int compareTo(Client client) {
		if (client._id.equalsIgnoreCase(this._id))
			return (client._name).toLowerCase().compareTo((this._name).toLowerCase());
		return (client._id).toLowerCase().compareTo((this._id).toLowerCase());
	}


	/**
	 * @param obj
	 */
	@Override
  	public boolean equals(Object obj) {
  		Client client = (Client) obj;
  		return (obj instanceof Client) && 
  			client.getClientKey().equalsIgnoreCase(this.getClientKey());
  	}


  	@Override
	public int hashCode() {
		return (_id.toLowerCase()).hashCode();
	}


	public String toString() {
		return _id + "|" + _name + "|" + _address + "|" + _status.toString() + "|" +  
			(int) Math.round(_totalTransactionsValue) + "|" + (int) Math.round(_totalTransactionsPayedValue);
	}


	/**
	* @param status
	*/
	void setClientStatus(ClientStatus status) {
		_status = status;
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

	public ClientStatus getStatus() {
		return _status;
	}

	public double getPoints() {
		return _numPoints;
	}

	/**
	* @param points
	*/
	void setPoints(double points) {
		_numPoints = points;
	}

	/**
	* @param deliveryMethod
	*/
	void setDeliveryMethod(NotificationDeliveryMethod deliveryMethod){
		_deliveryMethod = deliveryMethod;
	}


	/**
	* @param notification
	*/
	public void notify(Notification notification) {
		_deliveryMethod.notify(notification);
	}


	public Collection<Notification> getNotifications() {
		return _deliveryMethod.getNotifications();
	}


	void clearNotifications(){
		_deliveryMethod.clearNotifications();
	}



	/**
	* @param transaction
	*/
	void addTransaction(Transaction transaction){
		_transactions.add(transaction);
	}

	/**
	* @param value
	*/
	void addTransactionsValue(double value){
		_totalTransactionsValue += value;
	}

	/**
	* @param date
	*/
	public Collection<Transaction> getTransactions(int date) {
		updateTransactions(date);
		return Collections.unmodifiableList(_transactions);
	}

	/**
	* @param date
	*/
	void updateTransactions(int date) {
		for (Transaction t : _transactions) {
			if (! ((Sale) t).isPaid()) ((Sale) t).computeSaleCost(date);
		}
	}


	public Collection<Transaction> getPaidTransactions(){
		List<Transaction> transactions = new ArrayList<Transaction>();

		for (Transaction t: _transactions)
			if (((Sale) t).isPaid()) transactions.add(t);

		return Collections.unmodifiableList(transactions);
	}


	public Collection<Transaction> removeSales(int value){
		List<Transaction> transactions = new ArrayList<Transaction>();

		for (Transaction t: _transactions)
			if (((Sale) t).isPaid() && ((Sale) t).getPayedValue() < value){
				_transactions.remove(t);
				transactions.add(t);
			}

		return Collections.unmodifiableList(transactions);
	}


	/**
	* @param sale
	* @param actualDate
	*/
	void pay(Sale sale, int actualDate) {
		sale.computeSaleCost(actualDate); 
		double saleValue = sale.getCurrentValue();

		int paymentDelay = actualDate - sale.getDeadline();
		if (paymentDelay <= 0)
			_numPoints += 10 * saleValue;
	
		sale.setAsPaid(saleValue, actualDate);
		_status.updateStatus(paymentDelay, this);
		
		_totalTransactionsPayedValue += saleValue;
	}

	/**
	* @param sale
	* @param actualDate
	*/
	double computeSaleCost(Sale sale, int actualDate) {
		return _status.computeSaleCost(sale, actualDate);
	}

}