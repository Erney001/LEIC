package woo.app.main;

import pt.tecnico.po.ui.Command;
import woo.core.StoreManager;

/** Main menu. */
public class Menu extends pt.tecnico.po.ui.Menu {

  /** @param receiver command executor */
  public Menu(StoreManager receiver) {
    super(Label.TITLE, new Command<?>[] { //
        new DoOpen(receiver), //
        new DoSave(receiver), //
        new DoDisplayDate(receiver), //
        new DoAdvanceDate(receiver), //
        new DoOpenMenuProducts(receiver), //
        new DoOpenMenuClients(receiver), //
        new DoOpenMenuSuppliers(receiver), //
        new DoOpenMenuTransactions(receiver), //
        new DoOpenMenuLookups(receiver), //
        new DoShowGlobalBalance(receiver), //
    });
  }
}
 
