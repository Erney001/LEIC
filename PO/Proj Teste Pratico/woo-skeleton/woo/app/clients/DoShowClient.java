package woo.app.clients;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

import java.util.Collection;

import woo.core.StoreManager;
import woo.core.Client;
import woo.core.Notification;

import woo.core.exception.MissingClientKeyException;
import woo.app.exception.UnknownClientKeyException;


/**
 * Show client.
 */
public class DoShowClient extends Command<StoreManager> {

	private final Input<String> _clientKey;


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

  	try {
      Client client = _receiver.getClient(_clientKey.value());
      _display.addLine(client.toString());

      Collection<Notification> notifications = _receiver.getClientNotifications(_clientKey.value());
      for (Notification n: notifications)
        _display.addLine(n.toString());
    
      _display.display();
    
    } catch (MissingClientKeyException e) {
      throw new UnknownClientKeyException(_clientKey.value());
    }
  }
}
