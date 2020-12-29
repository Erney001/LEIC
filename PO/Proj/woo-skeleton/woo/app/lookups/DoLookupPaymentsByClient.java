package woo.app.lookups;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;
import woo.core.StoreManager;

import woo.core.Transaction.Transaction;
import woo.core.exception.MissingClientKeyException;
import woo.app.exception.UnknownClientKeyException;

import java.util.List;

/**
 * Lookup payments by given client.
 */
public class DoLookupPaymentsByClient extends Command<StoreManager> {

  private Input<String> _clientKey;

  public DoLookupPaymentsByClient(StoreManager storefront) {
    super(Label.PAID_BY_CLIENT, storefront);
    _clientKey = _form.addStringInput(Message.requestClientKey());
  }

  @Override
  public void execute() throws DialogException {
    try{
      _form.parse();

      List<Transaction> transactions = _receiver.getPaidTransactionsByClient(_clientKey.value());

      for(Transaction t: transactions){
        _display.addLine(t.toString());
      }
      
      _display.display();

    } catch(MissingClientKeyException e){
      throw new UnknownClientKeyException(_clientKey.value());
    }
  }

}
