package woo.app.suppliers;

import java.util.Collection;
import java.util.List;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

import woo.core.StoreManager;
import woo.core.Transaction;
import woo.core.exception.MissingSupplierKeyException;

import woo.app.exception.UnknownSupplierKeyException;


/**
 * Show all transactions for specific supplier.
 */
public class DoShowSupplierTransactions extends Command<StoreManager> {

  private final Input<String> _supplierKey;

  public DoShowSupplierTransactions(StoreManager receiver) {
    super(Label.SHOW_SUPPLIER_TRANSACTIONS, receiver);
    _supplierKey = _form.addStringInput(Message.requestSupplierKey());
  }

  @Override
  public void execute() throws DialogException {

    try {
      _form.parse();

      Collection<Transaction> transactions = _receiver.getSupplierTransactions(_supplierKey.value());
      
      for (Transaction t: transactions)
        _display.addLine(t.toString());

    	_display.display();


    } catch(MissingSupplierKeyException e){
    	throw new UnknownSupplierKeyException(_supplierKey.value());
    }
  }
}
