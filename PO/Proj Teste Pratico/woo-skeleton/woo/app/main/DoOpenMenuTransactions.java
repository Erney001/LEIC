package woo.app.main;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.Menu;
import woo.core.StoreManager;

/**
 * Open menu for transaction management.
 */
public class DoOpenMenuTransactions extends Command<StoreManager> {

  public DoOpenMenuTransactions(StoreManager receiver) {
    super(Label.OPEM_MENU_TRANSACTIONS, receiver);
  }

  @Override
  public final void execute() {
    Menu menu = new woo.app.transactions.Menu(_receiver);
    menu.open();
  }
}
