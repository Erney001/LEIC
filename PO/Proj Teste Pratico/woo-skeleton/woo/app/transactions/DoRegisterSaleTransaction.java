package woo.app.transactions;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;


import woo.core.StoreManager;
import woo.core.Product;
import woo.core.exception.MissingClientKeyException;
import woo.core.exception.MissingProductKeyException;
import woo.core.exception.LowStockException;

import woo.app.exception.UnknownClientKeyException;
import woo.app.exception.UnknownProductKeyException;
import woo.app.exception.UnavailableProductException;

/**
 * Register sale.
 */
public class DoRegisterSaleTransaction extends Command<StoreManager> {

  private final Input<String> _clientKey;
  private final Input<Integer> _paymentDeadline;
  private final Input<String> _productKey;
  private final Input<Integer> _ammount;


  public DoRegisterSaleTransaction(StoreManager receiver) {
    super(Label.REGISTER_SALE_TRANSACTION, receiver);
    _clientKey = _form.addStringInput(Message.requestClientKey());
    _paymentDeadline = _form.addIntegerInput(Message.requestPaymentDeadline());
    _productKey = _form.addStringInput(Message.requestProductKey());
    _ammount = _form.addIntegerInput(Message.requestAmount());
  }


  @Override
  public final void execute() throws DialogException, UnknownClientKeyException,
                    UnknownProductKeyException, UnavailableProductException {

    try {
      _form.parse();
      _receiver.registSale(_clientKey.value(), _paymentDeadline.value(),
                           _productKey.value(), _ammount.value());
    
    } catch(MissingClientKeyException e){
      throw new UnknownClientKeyException(_clientKey.value());
    
    } catch(MissingProductKeyException e){
      throw new UnknownProductKeyException(_productKey.value());
    
    } catch(LowStockException e){
      Product product = _receiver.getProduct(_productKey.value());
      throw new UnavailableProductException(_productKey.value(), _ammount.value(), product.getStock());
    }
  }

}
