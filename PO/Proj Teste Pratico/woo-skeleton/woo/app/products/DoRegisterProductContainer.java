package woo.app.products;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

import woo.app.products.Message;
import woo.app.exception.DuplicateProductKeyException;
import woo.app.exception.UnknownServiceTypeException;
import woo.app.exception.UnknownServiceLevelException;
import woo.app.exception.UnknownSupplierKeyException;


import woo.core.StoreManager;
import woo.core.exception.DupedProductKeyException;
import woo.core.exception.ServiceTypeNotFoundException;
import woo.core.exception.ServiceLevelNotFoundException;
import woo.core.exception.MissingSupplierKeyException;

/**
 * Register container.
 */
public class DoRegisterProductContainer extends Command<StoreManager> {

  private final Input<String> _key;
	private final Input<Integer> _price;
	private final Input<Integer> _criticalValue;
	private final Input<String> _supplierKey;
	private final Input<String> _serviceLevel;
	private final Input<String> _qualityLevel;

  public DoRegisterProductContainer(StoreManager receiver) {
    super(Label.REGISTER_CONTAINER, receiver);
    _key = _form.addStringInput(Message.requestProductKey());
    _price = _form.addIntegerInput(Message.requestPrice());
    _criticalValue = _form.addIntegerInput(Message.requestStockCriticalValue());
    _supplierKey = _form.addStringInput(Message.requestSupplierKey());
    _serviceLevel = _form.addStringInput(Message.requestServiceType());
    _qualityLevel = _form.addStringInput(Message.requestServiceLevel());
  }


  /**
   * @throws DialogException
   * @throws UnknownServiceTypeException
   * @throws UnknownServiceLevelException
   * @throws DuplicateProductKeyException
   * @throws UnknownSupplierKeyException
   */
  @Override
  public final void execute() throws DialogException, UnknownServiceTypeException,
                                     UnknownServiceLevelException, 
                                     DuplicateProductKeyException, UnknownSupplierKeyException {
    try {
      _form.parse();
  
    _receiver.registContainer(_key.value(), _price.value(), _criticalValue.value(),
         _supplierKey.value(), _serviceLevel.value(), _qualityLevel.value());

    } catch(ServiceTypeNotFoundException exception){
      throw new UnknownServiceTypeException(_serviceLevel.value());

    } catch(ServiceLevelNotFoundException exception) {
      throw new UnknownServiceLevelException(_qualityLevel.value());
    
    } catch(DupedProductKeyException exception) {
      throw new DuplicateProductKeyException(_key.value());
    
    } catch(MissingSupplierKeyException exc) {
      throw new UnknownSupplierKeyException(_supplierKey.value()); 
    }
  }
}
