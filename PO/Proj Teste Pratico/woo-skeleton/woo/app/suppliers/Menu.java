package woo.app.suppliers;

import pt.tecnico.po.ui.Command;
import woo.core.StoreManager;

/** Suppliers menu. */
public class Menu extends pt.tecnico.po.ui.Menu {

  /** @param receiver command executor */
  public Menu(StoreManager receiver) {
    super(Label.TITLE, new Command<?>[] { //
        new DoShowSuppliers(receiver), //
        new DoRegisterSupplier(receiver), //
        new DoToggleTransactions(receiver), //
        new DoShowSupplierTransactions(receiver), //
    });
  }
}
