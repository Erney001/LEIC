package woo.app.clients;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

import java.util.List;
import java.util.Collection;

import woo.core.StoreManager;
import woo.core.Transaction;
import woo.core.exception.MissingClientKeyException;

import woo.app.exception.UnknownClientKeyException;

/**
 * Show all transactions for a specific client.
 */
public class DoShowClientTransactions extends Command<StoreManager> {

  private final Input<String> _clientKey;

  public DoShowClientTransactions(StoreManager storefront) {
    super(Label.SHOW_CLIENT_TRANSACTIONS, storefront);
    _clientKey = _form.addStringInput(Message.requestClientKey());
  }

  @Override
  public void execute() throws DialogException {

    try {
      _form.parse();

      Collection<Transaction> transactions = _receiver.getClientTransactions(_clientKey.value());
      for (Transaction t: transactions)
        _display.addLine(t.toString());
      
    	_display.display();


    } catch(MissingClientKeyException e){
    	throw new UnknownClientKeyException(_clientKey.value());
    }
  }

}
