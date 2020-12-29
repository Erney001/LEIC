package woo.app.lookups;

import pt.tecnico.po.ui.Command;
import woo.core.StoreManager;

/** Lookups menu. */
public class Menu extends pt.tecnico.po.ui.Menu {

  /** @param receiver command executor */
  public Menu(StoreManager receiver) {
    super(Label.TITLE, new Command<?>[] { //
        new DoLookupProductsUnderTopPrice(receiver), //
        new DoLookupPaymentsByClient(receiver), //
    });
  }

}
