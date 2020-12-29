package woo.app.lookups;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;
import woo.core.StoreManager;

import woo.core.Product;

import java.util.Map;
import java.util.LinkedHashMap;

/**
 * Lookup products cheaper than a given price.
 */
public class DoLookupProductsUnderTopPrice extends Command<StoreManager> {

  private Input<Integer> _priceLimit;

  public DoLookupProductsUnderTopPrice(StoreManager storefront) {
    super(Label.PRODUCTS_UNDER_PRICE, storefront);
    _priceLimit = _form.addIntegerInput(Message.requestPriceLimit());
  }

  @Override
  public void execute() throws DialogException {
    _form.parse();

    Map<String, Product> _products = new LinkedHashMap<String, Product>();
    _products = _receiver.getProductsBelowPrice(_priceLimit.value());

    for(String productID: _products.keySet()){
      _display.addLine((_products.get(productID)).toString());
    }

    _display.display();
  }
}
