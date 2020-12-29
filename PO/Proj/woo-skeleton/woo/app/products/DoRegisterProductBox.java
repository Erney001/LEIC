package woo.app.products;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

import woo.app.products.Message;
import woo.app.exception.UnknownServiceTypeException;
import woo.app.exception.UnknownSupplierKeyException;
import woo.app.exception.DuplicateProductKeyException;

import woo.core.StoreManager;
import woo.core.exception.ServiceTypeNotFoundException;
import woo.core.exception.DupedProductKeyException;
import woo.core.exception.MissingSupplierKeyException;

/**
 * Register box.
 */
public class DoRegisterProductBox extends Command<StoreManager> {

	private final Input<String> _key;
	private Input<Integer> _price;
	private final Input<Integer> _criticalValue;
	private final Input<String> _supplierKey;
	private final Input<String> _serviceLevel;


  public DoRegisterProductBox(StoreManager receiver) {
    super(Label.REGISTER_BOX, receiver);
    _key = _form.addStringInput(Message.requestProductKey());
    _price = _form.addIntegerInput(Message.requestPrice());
    _criticalValue = _form.addIntegerInput(Message.requestStockCriticalValue());
    _supplierKey = _form.addStringInput(Message.requestSupplierKey());
    _serviceLevel = _form.addStringInput(Message.requestServiceType());
  }


  /**
   * @throws DialogException
   */
  @Override
  public final void execute() throws DialogException {
      _form.parse();

    try {
      _receiver.registBox(_key.value(), _price.value(), _criticalValue.value(),
        _supplierKey.value(), _serviceLevel.value());

    } catch(ServiceTypeNotFoundException exception) {
      throw new UnknownServiceTypeException(_serviceLevel.value());
    
    } catch(DupedProductKeyException exception) {
      throw new DuplicateProductKeyException(_key.value());
    
    } catch(MissingSupplierKeyException exc) {
      throw new UnknownSupplierKeyException(_supplierKey.value()); 
    }
  }
}
