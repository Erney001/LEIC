package woo.core;

public class SelectionStatus implements ClientStatus {

	private final int _elitePoints = 25000;

	public SelectionStatus() {
	
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


        if (daysToDeadline >= n){
        	double discount = 0.10;
        	return sale.getBaseValue() * (1-discount);
        }
	
		else if (0 <= daysToDeadline) {

			double discount = 0.05;
			if (daysToDeadline <= 2)
				return sale.getBaseValue() * (1-discount);

			return sale.getBaseValue();
		}


		else if (daysAfterDeadline <= n) {
			
			if (daysAfterDeadline <= 1)
				return sale.getBaseValue();

			double dailyPenalty = 0.02;
			return sale.getBaseValue() * (1 + (dailyPenalty * daysAfterDeadline));
		}
		
		else{
			double dailyPenalty = 0.05;
			return sale.getBaseValue() * (1 + (dailyPenalty * daysAfterDeadline));
		}
    }


    /**
	* @param paymentDelay
	* @param client
	*/
    public void updateStatus(int paymentDelay, Client client) {

    	if (client.getPoints() >= _elitePoints)
    		client.setClientStatus(new EliteStatus());

    	if (paymentDelay > 2) {
    		client.setClientStatus(new NormalStatus());
    		client.setPoints(client.getPoints() * 0.10);
    	}
    }


    public String toString() {
    	return "SELECTION";
    }
}