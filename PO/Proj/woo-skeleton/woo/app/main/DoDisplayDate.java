package woo.app.main;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

import woo.core.StoreManager;

/**
 * Show current date.
 */
public class DoDisplayDate extends Command<StoreManager> {


  public DoDisplayDate(StoreManager receiver) {
    super(Label.SHOW_DATE, receiver);
  }


  /**
   * @throws DialogException
   */
  @Override
  public final void execute() throws DialogException {
    _display.popup(Message.currentDate(_receiver.displayDate()));
  }
}
