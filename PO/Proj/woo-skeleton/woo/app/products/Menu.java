package woo.app.products;

import pt.tecnico.po.ui.Command;
import woo.core.StoreManager;

/** Products menu. */
public class Menu extends pt.tecnico.po.ui.Menu {

  /** @param receiver command executor */
  public Menu(StoreManager receiver) {
    super(Label.TITLE, new Command<?>[] { //
        new DoShowAllProducts(receiver), //
        new DoRegisterProductBox(receiver), //
        new DoRegisterProductContainer(receiver), //
        new DoRegisterProductBook(receiver), //
        new DoChangePrice(receiver), //
    });
  }

}
