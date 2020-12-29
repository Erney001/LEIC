package woo.app.products;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

import java.util.Collection;

import woo.core.StoreManager;
import woo.core.Product;
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
    Collection<Product> products = _receiver.getProducts();  

    for (Product product: products)
      _display.addLine(product.toString());

   	_display.display();
  }

}
