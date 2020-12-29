package woo.app.suppliers;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

import woo.core.StoreManager;
import woo.core.Supplier;

import java.util.Collection;


/**
 * Show all suppliers.
 */
public class DoShowSuppliers extends Command<StoreManager> {

  public DoShowSuppliers(StoreManager receiver) {
    super(Label.SHOW_ALL_SUPPLIERS, receiver);
  }


  /**
   * @throws DialogException
   */
  @Override
  public void execute() throws DialogException {
    Collection<Supplier> suppliers;

    suppliers = _receiver.getSuppliers();  

    for (Supplier supplier: suppliers){
      if (supplier.getTransactionsStatus())
        _display.addLine((supplier.toString() + Message.yes()));
      
      else _display.addLine(supplier.toString() + Message.no());
    }

   	_display.display();
  }

}
