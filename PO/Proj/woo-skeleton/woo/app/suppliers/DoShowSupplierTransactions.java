package woo.app.suppliers;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;
import woo.core.StoreManager;
import woo.core.Transaction.Transaction;
import woo.app.exception.UnknownSupplierKeyException;
import woo.core.exception.MissingSupplierKeyException;

import java.util.List;

/**
 * Show all transactions for specific supplier.
 */
public class DoShowSupplierTransactions extends Command<StoreManager> {

  private Input<String> _supplierKey;

  public DoShowSupplierTransactions(StoreManager receiver) {
    super(Label.SHOW_SUPPLIER_TRANSACTIONS, receiver);
    _supplierKey = _form.addStringInput(Message.requestSupplierKey());
  }

  @Override
  public void execute() throws DialogException {
    try {
      _form.parse();

      List<Transaction> transactions = _receiver.getSupplierTransactions(_supplierKey.value());

      for(Transaction t: transactions){
        _display.addLine(t.toString());
      }

    	_display.display();

    } catch(MissingSupplierKeyException e){
    	throw new UnknownSupplierKeyException(_supplierKey.value());
    }
  }
}
