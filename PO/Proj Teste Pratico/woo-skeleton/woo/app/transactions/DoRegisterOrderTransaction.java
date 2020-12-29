package woo.app.transactions;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

import java.util.Map;
import java.util.TreeMap;

import woo.core.StoreManager;
import woo.core.exception.MissingSupplierKeyException;
import woo.core.exception.CantTradeException;
import woo.core.exception.MissingProductKeyException;
import woo.core.exception.UnexistentProductInSupplierException;

import woo.app.exception.UnknownSupplierKeyException;
import woo.app.exception.UnauthorizedSupplierException;
import woo.app.exception.UnknownProductKeyException;
import woo.app.exception.WrongSupplierException;


/**
 * Register order.
 */
public class DoRegisterOrderTransaction extends Command<StoreManager> {

  private Input<String> _supplierKey;
  private Input<String> _productKey;
  private Input<Integer> _ammount;
  private Input<Boolean> _requestMore;

  public DoRegisterOrderTransaction(StoreManager receiver) {
    super(Label.REGISTER_ORDER_TRANSACTION, receiver);
  }

  @Override
  public final void execute() throws DialogException {

    try {
      _supplierKey = _form.addStringInput(Message.requestSupplierKey());

      Map<String, Integer> itemsInfo = new TreeMap<String, Integer>();
      boolean request = true;
      
      while (request) {

        _productKey = _form.addStringInput(Message.requestProductKey());
        _ammount = _form.addIntegerInput(Message.requestAmount());
        _requestMore = _form.addBooleanInput(Message.requestMore());
  
        _form.parse();
        _form.clear();


        if (itemsInfo.containsKey((_productKey.value()).toLowerCase()))
          itemsInfo.put((_productKey.value()).toLowerCase(), 
                  itemsInfo.get((_productKey.value()).toLowerCase()) + _ammount.value());

        else itemsInfo.put((_productKey.value()).toLowerCase(), _ammount.value());

        request = _requestMore.value();
      }

      _receiver.registOrder(_supplierKey.value(), itemsInfo);


    } catch(MissingSupplierKeyException e){
      throw new UnknownSupplierKeyException(_supplierKey.value());

    } catch(MissingProductKeyException e){
      throw new UnknownProductKeyException(_productKey.value());

    } catch(CantTradeException e){
      throw new UnauthorizedSupplierException(_supplierKey.value());

    } catch(UnexistentProductInSupplierException e){
      throw new WrongSupplierException(_supplierKey.value(), _productKey.value());
    }
  }
}
