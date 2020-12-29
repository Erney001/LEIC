package woo.app.products;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

import woo.core.StoreManager;

/**
 * Change product price.
 */
public class DoChangePrice extends Command<StoreManager> {

  private Input<String> _key;
  private Input<Integer> _price;

  public DoChangePrice(StoreManager receiver) {
    super(Label.CHANGE_PRICE, receiver);
    _key = _form.addStringInput(Message.requestProductKey());
    _price = _form.addIntegerInput(Message.requestPrice());
  }


  @Override
  public final void execute() throws DialogException {
    // a operacao nao vai apanhar excessoes, mas e preciso um try?

    _form.parse();
    _receiver.changePrice(_key.value(), _price.value());
  }
}
