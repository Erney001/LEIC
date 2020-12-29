package woo.app.suppliers;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

import woo.core.StoreManager;
import woo.core.Supplier;

import java.util.TreeMap;
import java.util.Map;
import java.util.LinkedHashMap;


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
    Map<String, Supplier> suppliers;

    suppliers = _receiver.getSuppliers();  

    for (String supplierID: suppliers.keySet()){
      Supplier supplier = suppliers.get(supplierID);
      
      if (supplier.getStatus())
        _display.addLine((supplier.toString() + Message.yes()));
      else _display.addLine(supplier.toString() + Message.no());
    }

   	_display.display();
  }

}
