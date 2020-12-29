package woo.app.transactions;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;
import woo.core.StoreManager;
import woo.core.Transaction.Transaction;
import woo.app.exception.UnknownTransactionKeyException;
import woo.core.exception.MissingTransactionKeyException;

/**
 * Show specific transaction.
 */
public class DoShowTransaction extends Command<StoreManager> {

  private Input<Integer> _transactionKey;

  public DoShowTransaction(StoreManager receiver) {
    super(Label.SHOW_TRANSACTION, receiver);
    _transactionKey = _form.addIntegerInput(Message.requestTransactionKey());
  }

  @Override
  public final void execute() throws DialogException {
    try{
      _form.parse();
      Transaction transaction = _receiver.getTransaction(_transactionKey.value());
      // so dar popup ou receber aquilo tudo e construir a string aqui?
      _display.popup(transaction.toString());

    } catch(MissingTransactionKeyException e){
      throw new UnknownTransactionKeyException(_transactionKey.value());
    }
  }
}
