package woo.app.main;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

import woo.core.StoreManager;

import woo.app.main.Message;
import woo.app.exception.InvalidDateException;


/**
 * Advance current date.
 */
public class DoAdvanceDate extends Command<StoreManager> {
  
  private Input<Integer> _daysToAdvance;


  public DoAdvanceDate(StoreManager receiver) {
    super(Label.ADVANCE_DATE, receiver);
    _daysToAdvance = _form.addIntegerInput(Message.requestDaysToAdvance());
  }


  /**
   * @throws DialogException
   * @throws InvalidDateException
   */
  @Override
  public final void execute() throws DialogException, InvalidDateException {
    _form.parse();

    if (_daysToAdvance.value() > 0)
      _receiver.advanceCurrentDate(_daysToAdvance.value());
    
    else throw new InvalidDateException(_daysToAdvance.value());
  }
}
