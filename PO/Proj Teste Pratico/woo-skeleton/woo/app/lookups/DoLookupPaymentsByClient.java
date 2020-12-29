package woo.app.lookups;


import java.util.Collection;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

import woo.core.StoreManager;
import woo.core.Transaction;

import woo.core.exception.MissingClientKeyException;
import woo.app.exception.UnknownClientKeyException;

/**
 * Lookup payments by given client.
 */
public class DoLookupPaymentsByClient extends Command<StoreManager> {

  private final Input<String> _clientKey;

  public DoLookupPaymentsByClient(StoreManager storefront) {
    super(Label.PAID_BY_CLIENT, storefront);
    _clientKey = _form.addStringInput(Message.requestClientKey());
  }

  @Override
  public void execute() throws DialogException {

    try{
      _form.parse();

      Collection<Transaction> transactions = _receiver.getPaidTransactionsByClient(_clientKey.value());
      for (Transaction t: transactions)
        _display.addLine(t.toString());
      
      _display.display();


    } catch(MissingClientKeyException e){
      throw new UnknownClientKeyException(_clientKey.value());
    }
  }

}
