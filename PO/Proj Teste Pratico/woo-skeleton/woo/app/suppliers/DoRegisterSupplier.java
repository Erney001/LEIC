package woo.app.suppliers;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

import woo.core.StoreManager;
import woo.core.Supplier;
import woo.core.exception.DupedSupplierKeyException;

import woo.app.exception.DuplicateSupplierKeyException;


/**
 * Register supplier.
 */
public class DoRegisterSupplier extends Command<StoreManager> {

  private final Input<String> _supplierKey;
  private final Input<String> _name;
  private final Input<String> _address;


  public DoRegisterSupplier(StoreManager receiver) {
    super(Label.REGISTER_SUPPLIER, receiver);
    _supplierKey = _form.addStringInput(Message.requestSupplierKey());
    _name = _form.addStringInput(Message.requestSupplierName());
    _address = _form.addStringInput(Message.requestSupplierAddress());
  }


  /**
   * @throws DialogException
   * @throws DuplicateSupplierKeyException
   */
  @Override
  public void execute() throws DialogException, DuplicateSupplierKeyException {
    _form.parse();

    try {
      _receiver.registSupplier(_supplierKey.value(), _name.value(), _address.value());

    } catch(DupedSupplierKeyException exc) {
      throw new DuplicateSupplierKeyException(_supplierKey.value());
    }
  }
}
