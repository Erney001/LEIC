package woo.app.clients;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

import woo.core.StoreManager;
import woo.core.exception.MissingClientKeyException;
import woo.core.exception.MissingProductKeyException;

import woo.app.exception.UnknownClientKeyException;
import woo.app.exception.UnknownProductKeyException;

/**
 * Toggle product-related notifications.
 */
public class DoToggleProductNotifications extends Command<StoreManager> {

  private final Input<String> _clientKey;
  private final Input<String> _productKey;
  
  public DoToggleProductNotifications(StoreManager storefront) {
    super(Label.TOGGLE_PRODUCT_NOTIFICATIONS, storefront);
    _clientKey = _form.addStringInput(Message.requestClientKey());
    _productKey = _form.addStringInput(Message.requestProductKey());
  }

  @Override
  public void execute() throws DialogException {

    try {
      _form.parse();
      
      if (_receiver.toggleProductNotifications(_clientKey.value(), _productKey.value())) 
        _display.popup(Message.notificationsOn(_clientKey.value(), _productKey.value()));
      else 
        _display.popup(Message.notificationsOff(_clientKey.value(), _productKey.value()));  
    
    } catch(MissingClientKeyException e) {
      throw new UnknownClientKeyException(_clientKey.value());

    } catch(MissingProductKeyException e) {
      throw new UnknownProductKeyException(_productKey.value());
    } 
  }

} 
