package woo.app.transactions;

import pt.tecnico.po.ui.Command;
import woo.core.StoreManager;

/** Transactions menu. */
public class Menu extends pt.tecnico.po.ui.Menu {

  /** @param receiver command executor */
  public Menu(StoreManager receiver) {
    super(Label.TITLE, new Command<?>[] { //
        new DoShowTransaction(receiver), //
        new DoRegisterSaleTransaction(receiver), //
        new DoRegisterOrderTransaction(receiver), //
        new DoPay(receiver), //
    });
  }
}
