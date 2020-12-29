package woo.app.main;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.Menu;
import woo.core.StoreManager;

/**
 * Open menu for lookups.
 */
public class DoOpenMenuLookups extends Command<StoreManager> {

  public DoOpenMenuLookups(StoreManager receiver) {
    super(Label.OPEM_MENU_LOOKUPS, receiver);
  }

  @Override
  public final void execute() {
    Menu menu = new woo.app.lookups.Menu(_receiver);
    menu.open();
  }
}
