package woo.app.products;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

import woo.app.products.Message;
import woo.app.exception.DuplicateProductKeyException;
import woo.app.exception.UnknownSupplierKeyException;

import woo.core.StoreManager;
import woo.core.exception.DupedProductKeyException;
import woo.core.exception.MissingSupplierKeyException;

/**
 * Register book.
 */
public class DoRegisterProductBook extends Command<StoreManager> {

  private final Input<String> _key;
	private Input<Integer> _price;
	private final Input<Integer> _criticalValue;
	private final Input<String> _supplierKey;
	private final Input<String> _title;
	private final Input<String> _author;
	private final Input<String> _isbn;


  public DoRegisterProductBook(StoreManager receiver) {
    super(Label.REGISTER_BOOK, receiver);
    _key = _form.addStringInput(Message.requestProductKey());
    _price = _form.addIntegerInput(Message.requestPrice());
    _criticalValue = _form.addIntegerInput(Message.requestStockCriticalValue());
    _supplierKey = _form.addStringInput(Message.requestSupplierKey());
    _title = _form.addStringInput(Message.requestBookTitle());
    _author = _form.addStringInput(Message.requestBookAuthor());
    _isbn = _form.addStringInput(Message.requestISBN());
  }


  /**
   * @throws DialogException
   * @throws DuplicateProductKeyException
   * @throws UnknownSupplierKeyException
   */
  @Override
  public final void execute() throws DialogException, DuplicateProductKeyException, 
                              UnknownSupplierKeyException {
    _form.parse();

    try {
      _receiver.registBook(_key.value(), _price.value(), _criticalValue.value(),
                  _supplierKey.value(), _title.value(), _author.value(), _isbn.value());

    } catch(DupedProductKeyException exception) {
      throw new DuplicateProductKeyException(_key.value());
    
    } catch(MissingSupplierKeyException exc) {
      throw new UnknownSupplierKeyException(_supplierKey.value()); 
    }
  }
}
