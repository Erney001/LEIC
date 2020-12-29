package woo.app.transactions;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;
import woo.core.StoreManager;

import woo.core.exception.MissingSupplierKeyException;
import woo.core.exception.CantTradeException;
import woo.core.exception.MissingProductKeyException;
import woo.core.exception.UnexistentProductInSupplier;

import woo.app.exception.UnknownSupplierKeyException;
import woo.app.exception.UnauthorizedSupplierException;
import woo.app.exception.UnknownProductKeyException;
import woo.app.exception.WrongSupplierException;

import java.util.List;
import java.util.ArrayList;

/**
 * Register order.
 */
public class DoRegisterOrderTransaction extends Command<StoreManager> {

  private Input<String> _supplierKey;
  private Input<String> _productKey;
  private Input<Integer> _amount;
  private Input<Boolean> _requestMore;

  public DoRegisterOrderTransaction(StoreManager receiver) {
    super(Label.REGISTER_ORDER_TRANSACTION, receiver);
    _supplierKey = _form.addStringInput(Message.requestSupplierKey());
    _productKey = _form.addStringInput(Message.requestProductKey());
    _amount = _form.addIntegerInput(Message.requestAmount());
  }

  @Override
  public final void execute() throws DialogException {
    try{
      _form.parse();

      // TO DO
      // ter collection ou array onde guardar os diversos items e dps chamar registOrder de uma so vez?
      List<String[]> orderItems = new ArrayList<String[]>();

      //_receiver.registOrder(_supplierKey.value(), _productKey.value(), _amount.value());

      while((_requestMore = _form.addBooleanInput(Message.requestMore())).value()){
        _productKey = _form.addStringInput(Message.requestProductKey());
        _amount = _form.addIntegerInput(Message.requestAmount());

        _form.parse();

        //_receiver.registProductInOrder(_productKey.value(), _amount.value());
      }

      _receiver.registOrder(_supplierKey.value(), _productKey.value(), _amount.value());

    } catch(MissingSupplierKeyException e){
      throw new UnknownSupplierKeyException(_supplierKey.value());
    } catch(CantTradeException e){
      throw new UnauthorizedSupplierException(_supplierKey.value());
    } catch(MissingProductKeyException e){
      throw new UnknownProductKeyException(_productKey.value());
    } catch(UnexistentProductInSupplier e){
      throw new WrongSupplierException(_supplierKey.value(), _productKey.value());
    }
  }
}
