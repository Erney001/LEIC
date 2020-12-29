package woo.app.transactions;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

import java.util.Collection;

import woo.core.StoreManager;
import woo.core.Transaction;
import woo.core.exception.MissingTransactionKeyException;

import woo.app.exception.UnknownTransactionKeyException;


/**
 * Show specific transaction.
 */
public class DoShowTransaction extends Command<StoreManager> {

  private final Input<Integer> _transactionKey;

  public DoShowTransaction(StoreManager receiver) {
    super(Label.SHOW_TRANSACTION, receiver);
    _transactionKey = _form.addIntegerInput(Message.requestTransactionKey());
  }

  @Override
  public final void execute() throws DialogException {
    
    try {
      _form.parse();

      Transaction transaction = _receiver.getTransaction(_transactionKey.value());
      _display.addLine(transaction.toString());
      _display.display();
    
    } catch(MissingTransactionKeyException e){
      throw new UnknownTransactionKeyException(_transactionKey.value());
    }
  }
}
