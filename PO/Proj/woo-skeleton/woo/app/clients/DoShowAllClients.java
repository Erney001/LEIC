package woo.app.clients;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException; 
import pt.tecnico.po.ui.Input; 

import woo.core.StoreManager;  
import woo.core.Client;

import java.util.Map;
import java.util.LinkedHashMap;


/**
 * Show all clients.
 */
public class DoShowAllClients extends Command<StoreManager> {

  public DoShowAllClients(StoreManager storefront) {
    super(Label.SHOW_ALL_CLIENTS, storefront);
  }

  /**
   * @throws DialogException
   */
  @Override
  public void execute() throws DialogException {
    Map<String, Client> _clients;
    _clients = _receiver.getClients();  

    for(String clientID: _clients.keySet()){
      _display.addLine((_clients.get(clientID).toString()));
    }

   	_display.display();
  }
}
