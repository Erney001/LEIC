package woo.app.transactions;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

import java.util.Collection;

import woo.core.StoreManager;

/**
 * Show specific transaction.
 */
public class ProdutoMaisRequisitado extends Command<StoreManager> {

    private final Input<Integer> _transactionKey;

  public ProdutoMaisRequisitado(StoreManager receiver) {
    super(Label.SHOW_TRANSACTION, receiver);
    _transactionKey = _form.addIntegerInput(Message.requestTransactionKey());
  }

  @Override
  public final void execute() throws DialogException {
    _form.parse();

    String s = _receiver.produtoMaisRequisitado(_transactionKey.value());

    _display.addLine(s);
    
  }
}
