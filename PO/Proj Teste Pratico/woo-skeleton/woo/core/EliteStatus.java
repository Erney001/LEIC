package woo.core;


public class EliteStatus implements ClientStatus {
	

	public EliteStatus() {
		
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
			
		else if (0 <= daysToDeadline){
			double discount = 0.10;
			return sale.getBaseValue() * (1-discount);
		}
			

		else if (daysAfterDeadline <= n) {
			double discount = 0.05;
			return sale.getBaseValue() * (1-discount);
		}

		else return sale.getBaseValue();
		
    }


    /**
	* @param paymentDelay
	* @param client
	*/
    public void updateStatus(int paymentDelay, Client client) {

    	if (paymentDelay > 15){
    		client.setClientStatus(new SelectionStatus());
    		client.setPoints(client.getPoints() * 0.25);
    	}
    }

    public String toString() {
    	return "ELITE";
    }
}