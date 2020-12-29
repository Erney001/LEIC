package woo.app.products;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

import woo.core.StoreManager;
import woo.core.Product;

import java.util.Map;
import java.util.LinkedHashMap;

/**
 * Show all products.
 */
public class DoShowAllProducts extends Command<StoreManager> {

  public DoShowAllProducts(StoreManager receiver) {
    super(Label.SHOW_ALL_PRODUCTS, receiver);
  }


  /**
   * @throws DialogException
   */
  @Override
  public final void execute() throws DialogException {
    Map<String, Product> _products = new LinkedHashMap<String, Product>();
    _products = _receiver.getProducts();  

    for(String productID: _products.keySet()){
      _display.addLine((_products.get(productID)).toString());
    }

   	_display.display();
  }

}
