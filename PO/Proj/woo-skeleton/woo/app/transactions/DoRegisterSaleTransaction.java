package woo.app.transactions;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;
import woo.core.StoreManager;

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

  private Input<String> _clientKey;
  private Input<Integer> _paymentDeadline;
  private Input<String> _productKey;
  private Input<Integer> _amount;

  public DoRegisterSaleTransaction(StoreManager receiver) {
    super(Label.REGISTER_SALE_TRANSACTION, receiver);
    _clientKey = _form.addStringInput(Message.requestClientKey());
    _paymentDeadline = _form.addIntegerInput(Message.requestPaymentDeadline());
    _productKey = _form.addStringInput(Message.requestProductKey());
    _amount= _form.addIntegerInput(Message.requestAmount());
  }

  @Override
  public final void execute() throws DialogException {
    try{
      _form.parse();

      _receiver.registSale(_clientKey.value(), _paymentDeadline.value(), _productKey.value(), _amount.value());
    
    } catch(MissingClientKeyException e){
      throw new UnknownClientKeyException(_clientKey.value());
    } catch(MissingProductKeyException e){
      throw new UnknownProductKeyException(_productKey.value());
    } catch(LowStockException e){
      throw new UnavailableProductException(_productKey.value(), _amount.value(), 0);
    }
  }

}
