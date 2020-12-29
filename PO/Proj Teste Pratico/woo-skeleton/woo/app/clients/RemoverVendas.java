package woo.app.clients;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

import woo.core.StoreManager;
import woo.core.Transaction;

/**
 * Change product price.
 */
public class RemoverVendas extends Command<StoreManager> {

  private final Input<String> _clientKey;
  private final Input<Integer> _value;

  public RemoverVendas(StoreManager receiver) {
    super("Remover Vendas", receiver);
    _clientKey = _form.addStringInput(Message.requestClientKey());
    _value = _form.addIntegerInput("Valor: ");
  }


  @Override
  public final void execute() throws DialogException {
    _form.parse();

    Collection<Transaction> transactions = _receiver.removeSales(_clientKey.value(), _value.value());
    for (Transaction t: transactions)
      _display.addLine(t.toString());
  
    _display.display();
  }
}
