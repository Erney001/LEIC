package woo.core;


public class NormalStatus implements ClientStatus {

	private final int _selectionPoints = 2000;
	private final int _elitePoints = 25000;

	public NormalStatus() {
		
	}


	/**
	* @param sale
	* @param actualDate
	*/
    public double computeSaleCost(Sale sale, int actualDate) {
    	int deadlineDate = sale.getDeadline();
		Item item = sale.getItem();
		Product p = item.getProduct();
		int n = p.getN();

		int daysToDeadline = deadlineDate - actualDate;
		int daysAfterDeadline = actualDate - deadlineDate;


        if (daysToDeadline >= n) {
        	double discount = 0.10;
        	return sale.getBaseValue() * (1-discount);
        }
	
		else if (0 <= daysToDeadline)
			return sale.getBaseValue();
			

		else if (daysAfterDeadline <= n) {
			double penalty = 0.05;
			return sale.getBaseValue() * (1 + (penalty * daysAfterDeadline));
		}
		
		else {
			double penalty = 0.10;
			return sale.getBaseValue() * (1 + (penalty * daysAfterDeadline));
		}
    }


    /**
	* @param paymentDelay
	* @param client
	*/
    public void updateStatus(int paymentDelay, Client client) {

    	if (client.getPoints() >= _selectionPoints) {

    		if (client.getPoints() < _elitePoints)
    			client.setClientStatus(new SelectionStatus());

    		else client.setClientStatus(new EliteStatus());
    	}
    }

    public String toString() {
    	return "NORMAL";
    }
}