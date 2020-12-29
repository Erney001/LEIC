package woo.app.suppliers;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

import woo.app.exception.UnknownSupplierKeyException;

import woo.core.exception.MissingSupplierKeyException;
import woo.core.StoreManager;

/**
 * Enable/disable supplier transactions.
 */
public class DoToggleTransactions extends Command<StoreManager> {

  private Input<String> _supplierKey;


  public DoToggleTransactions(StoreManager receiver) {
    super(Label.TOGGLE_TRANSACTIONS, receiver);
    _supplierKey = _form.addStringInput(Message.requestSupplierKey());
  }


  /**
   * @throws DialogException
   */
  @Override
  public void execute() throws DialogException {
    _form.parse();

    try {
    	boolean status = _receiver.toggleStatus(_supplierKey.value());
    	if (status) _display.addLine(Message.transactionsOn(_supplierKey.value()));
    	else _display.addLine(Message.transactionsOff(_supplierKey.value()));

    	_display.display();

    } catch (MissingSupplierKeyException exc) {
    	throw new UnknownSupplierKeyException(_supplierKey.value());
    }
  }

}
