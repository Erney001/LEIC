package woo.app.clients;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

import woo.core.StoreManager;
import woo.core.Client;
import woo.app.exception.UnknownClientKeyException;
import java.util.List;
import java.util.ArrayList;
import woo.core.Notification;

/**
 * Show client.
 */
public class DoShowClient extends Command<StoreManager> {

	private Input<String> _clientKey;


  public DoShowClient(StoreManager storefront) {
    super(Label.SHOW_CLIENT, storefront);
    _clientKey = _form.addStringInput(Message.requestClientKey());
  }

  /**
   * @throws DialogException
   * @throws UnknownClientKeyException
   */
  @Override
  public void execute() throws DialogException, UnknownClientKeyException {
    _form.parse();

  	Client client = _receiver.getClient(_clientKey.value());
    if (client == null) throw new UnknownClientKeyException(_clientKey.value());

  	_display.addLine(client.toString());

    List<Notification> notifications = _receiver.getClientNotifications(_clientKey.value());

    for(Notification n: notifications){
      _display.addLine(n.toString());
    }
    
    _display.display();

  }
}
