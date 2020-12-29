package woo.app.transactions;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

import woo.core.StoreManager;
import woo.core.exception.MissingTransactionKeyException;

import woo.app.exception.UnknownTransactionKeyException;

/**
 * Pay transaction (sale).
 */
public class DoPay extends Command<StoreManager> {

  private final Input<Integer> _transactionKey;
  
  public DoPay(StoreManager storefront) {
    super(Label.PAY, storefront);
    _transactionKey = _form.addIntegerInput(Message.requestTransactionKey());
  }

  @Override
  public final void execute() throws DialogException {
    try {
      _form.parse();
      _receiver.pay(_transactionKey.value());

    } catch(MissingTransactionKeyException e){
      throw new UnknownTransactionKeyException(_transactionKey.value());
    }
  }
}
