package woo.core;

import java.io.Serializable;

public interface ClientStatus extends Serializable {
	
    double computeSaleCost(Sale s, int actualDate);
    void updateStatus(int paymentDelay, Client client);
    String toString();
}