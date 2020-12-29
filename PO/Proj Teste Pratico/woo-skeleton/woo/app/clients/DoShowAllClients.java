package woo.app.clients;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException; 
import pt.tecnico.po.ui.Input; 

import woo.core.StoreManager;  
import woo.core.Client;

import java.util.Collection;


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
    Collection<Client> clients = _receiver.getClients();  

    for (Client client: clients)
      _display.addLine(client.toString());

   	_display.display();
  }
}
