package woo.app.products;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

import woo.core.StoreManager;
import woo.core.exception.MissingProductKeyException;

import woo.app.exception.UnknownProductKeyException;

/**
 * Change product price.
 */
public class DoChangePrice extends Command<StoreManager> {

  private final Input<String> _key;
  private final Input<Integer> _price;

  public DoChangePrice(StoreManager receiver) {
    super(Label.CHANGE_PRICE, receiver);
    _key = _form.addStringInput(Message.requestProductKey());
    _price = _form.addIntegerInput(Message.requestPrice());
  }


  @Override
  public final void execute() throws DialogException {
    
    try{
      _form.parse();
      _receiver.changePrice(_key.value(), _price.value());

    } catch (MissingProductKeyException e) {
      throw new UnknownProductKeyException(_key.value());
    }
  }
}
