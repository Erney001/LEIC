package woo.app.clients;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

import woo.core.StoreManager;
import woo.core.Client;
import woo.core.exception.DupedClientKeyException;

import woo.app.exception.DuplicateClientKeyException;

/**
 * Register new client.
 */
public class DoRegisterClient extends Command<StoreManager> {

  private final Input<String> _clientKey;
  private final Input<String> _name;
  private final Input<String> _address;


  public DoRegisterClient(StoreManager storefront) {

    super(Label.REGISTER_CLIENT, storefront);
    _clientKey = _form.addStringInput(Message.requestClientKey());
    _name = _form.addStringInput(Message.requestClientName());
    _address = _form.addStringInput(Message.requestClientAddress());
  
  }

  /**
   * @throws DialogException
   * @throws DuplicateClientKeyException
   */
  @Override
  public void execute() throws DialogException, DuplicateClientKeyException {
    _form.parse();
    
  	try {
      _receiver.registClient(_clientKey.value(), _name.value(), _address.value());

    } catch(DupedClientKeyException exc){
      throw new DuplicateClientKeyException(_clientKey.value());
    }
  }

}
