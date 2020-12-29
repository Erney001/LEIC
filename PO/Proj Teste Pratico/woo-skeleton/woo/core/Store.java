package woo.core;

import java.io.Serializable;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import java.util.Collections;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import woo.core.enums.ServiceLevel;
import woo.core.enums.QualityLevel;
import woo.core.exception.ServiceLevelNotFoundException;
import woo.core.exception.ServiceTypeNotFoundException;
import woo.core.exception.BadEntryException;
import woo.core.exception.DupedProductKeyException;
import woo.core.exception.DupedClientKeyException;
import woo.core.exception.DupedSupplierKeyException;
import woo.core.exception.MissingClientKeyException;
import woo.core.exception.MissingSupplierKeyException;
import woo.core.exception.MissingTransactionKeyException;
import woo.core.exception.MissingProductKeyException;
import woo.core.exception.LowStockException;
import woo.core.exception.CantTradeException;
import woo.core.exception.UnexistentProductInSupplierException;



/**
 * Class Store implements a store.
 */
public class Store implements Serializable, Observer {

  /** Serial number for serialization. */
  private static final long serialVersionUID = 202009192006L;
  private Date _date;
  private double _actualBalance;
  private double _spendings;
  private Map<String, Client> _clients;
  private Map<String, Supplier> _suppliers;
  private Map<String, Product> _products;
  private List<Notification> _notifications;
  private Map<Integer, Sale> _sales;
  private Map<Integer, Order> _orders;
  private int _transactionID;


  public Store() {
    _clients = new HashMap<String, Client>();
    _suppliers = new HashMap<String, Supplier>();
    _products = new HashMap<String, Product>();
    _notifications = new ArrayList<Notification>();
    _sales = new LinkedHashMap<Integer, Sale>();
    _orders = new LinkedHashMap<Integer, Order>();
    _date = new Date();
    _actualBalance = 0d;
    _spendings = 0d;
    _transactionID = 0;
  }




/*************************************************************************************/
/*                                                                                   */
/*   This block corresponds to the import of a File with the state of the App        */            
/*                                                                                   */
/*************************************************************************************/

  /**
   * @param filename filename to be loaded.
   * @throws IOException
   * @throws BadEntryException
   * @throws IllegalArgumentException
   */
  void importFile(String filename) 
        throws IOException, IllegalArgumentException, BadEntryException {
    
    MyParser parser = new MyParser(this);
    parser.parseFile(filename);    
  }




/*************************************************************************************/
/*                                                                                   */
/*        This block of functions correspond to store's Accounting Values.           */            
/*                                                                                   */
/*************************************************************************************/


  public double getSaldoDisponivel() {
    return _actualBalance;
  }


  public double getSaldoContabilistico() {
    double income = 0;

    for (Map.Entry<Integer, Sale> entry : _sales.entrySet()) {
      Sale s = entry.getValue();
      s.computeSaleCost(_date.getDate());
      income += s.getCurrentValue();
    }

    return income - _spendings;
  }




/*************************************************************************************/
/*                                                                                   */
/*               This block of functions correspond to the Date.                     */            
/*                                                                                   */
/*************************************************************************************/


  public int getDate() {
    return _date.getDate();
  }

  /**
   * @param daysToAdvance
   */
  void advanceCurrentDate(int daysToAdvance){
    _date.advanceDate(daysToAdvance);
  }



/*************************************************************************************/
/*                                                                                   */
/*          This block of functions correspond to the Clients.                       */            
/*                                                                                   */
/*************************************************************************************/


  /**
   * @param id
   * @param name
   * @param address
   * @throws DupedClientKeyException
   */
  void registClient(String id, String name, String address)
                           throws DupedClientKeyException, MissingClientKeyException {

    if(_clients.size() + 1 > _suppliers.size()+ 10){
      throw new MissingClientKeyException(id);
    }

    if (! _clients.containsKey(id.toLowerCase())) {
      Client client = new Client(id, name, address);
      _clients.put(id.toLowerCase(), client);
      addObserverToAllProducts(client);
    }

    else throw new DupedClientKeyException(id);
  }




  /**
   * @param clientKey
   */
  public Client getClient(String clientKey) throws MissingClientKeyException {

    if (! _clients.containsKey(clientKey.toLowerCase()))
      throw new MissingClientKeyException(clientKey);

    Client client = _clients.get(clientKey.toLowerCase());
    client.updateTransactions(_date.getDate());
    return client;
  }




  public Collection<Client> getClients() {
    Set<Client> orderedClients = new TreeSet<Client>(new ClientComparator());

    for (Map.Entry<String,Client> entry: _clients.entrySet()) {
      Client c = entry.getValue();
      c.updateTransactions(_date.getDate());
      orderedClients.add(c);
    }

    return Collections.unmodifiableSet(orderedClients);
  }



  /**
  * @param clientKey
  */
  public Collection<Notification> getClientNotifications(String clientKey) {

    Client client = _clients.get(clientKey.toLowerCase());
    Collection<Notification> notifications = client.getNotifications();
    client.clearNotifications();
    return notifications;
  }
 


  /**
  * @param clientKey
  * @param productKey
  * @throws MissingClientKeyException
  * @throws MissingProductKeyException
  */
  boolean toggleProductNotifications(String clientKey, String productKey) 
         throws MissingClientKeyException, MissingProductKeyException {

    if (! _clients.containsKey(clientKey.toLowerCase()))
      throw new MissingClientKeyException(clientKey);

    if (! _products.containsKey(productKey.toLowerCase()))
      throw new MissingProductKeyException(productKey);


    Client client = _clients.get(clientKey.toLowerCase());
    Product product = _products.get(productKey.toLowerCase());

    boolean result;
    if (result = product.hasObserver(client)) 
      product.removeFromObservers(client);

    else product.addToObservers(client);

    return !result;
  }



  /**
  * @param clientKey
  * @throws MissingClientKeyException
  */
  public Collection<Transaction> getClientTransactions(String clientKey)
         throws MissingClientKeyException {
    
    if (! _clients.containsKey(clientKey.toLowerCase()))
      throw new MissingClientKeyException(clientKey);
    
    Client client = _clients.get(clientKey.toLowerCase());
    return client.getTransactions(_date.getDate());
  }



  /**
  * @param clientKey
  * @throws MissingClientKeyException
  */
  public Collection<Transaction> getPaidTransactionsByClient(String clientKey) 
         throws MissingClientKeyException {
    
    if (! _clients.containsKey(clientKey.toLowerCase()))
      throw new MissingClientKeyException(clientKey);
    
    Client client = _clients.get(clientKey.toLowerCase());
    return client.getPaidTransactions();
  }


  public Collection<Transaction> removeSales(String clientKey, int value){
    Client client = _clients.get(clientKey);
    return client.removeSales(value);
  }



/*************************************************************************************/
/*                                                                                   */
/*          This block of functions correspond to the Suppliers.                     */            
/*                                                                                   */
/*************************************************************************************/


  /**
   * @param supplierKey
   * @param name
   * @param address
   * @throws DupedSupplierKeyException
   */
  void registSupplier(String supplierKey, String name, String address)
                              throws DupedSupplierKeyException {

    if (! _suppliers.containsKey(supplierKey.toLowerCase())) {
      Supplier supplier = new Supplier(supplierKey, name, address);
      _suppliers.put(supplierKey.toLowerCase(), supplier);
    }

    else throw new DupedSupplierKeyException(supplierKey);
  }




  public Collection<Supplier> getSuppliers() {
    List<Supplier> orderedSuppliers = new ArrayList<Supplier>(_suppliers.values());
    Collections.sort(orderedSuppliers, new SupplierComparator());

    return Collections.unmodifiableList(orderedSuppliers);
  }



  /**
   * @param supplierKey
   * @throws MissingSupplierKeyException
   */
  boolean toggleStatus(String supplierKey) throws MissingSupplierKeyException {

    if (! _suppliers.containsKey(supplierKey.toLowerCase()))
      throw new MissingSupplierKeyException(supplierKey);
    
    Supplier supplier = _suppliers.get(supplierKey.toLowerCase());
    return supplier.toggleTransactionsStatus();
  }




  /**
  * @param supplierKey
  * @throws MissingSupplierKeyException
  */
  public Collection<Transaction> getSupplierTransactions(String supplierKey)
                           throws MissingSupplierKeyException {

    if (! _suppliers.containsKey(supplierKey.toLowerCase()))
      throw new MissingSupplierKeyException(supplierKey);
    
    Supplier supplier = _suppliers.get(supplierKey.toLowerCase());
    return supplier.getTransactions();
  }





/*************************************************************************************/
/*                                                                                   */
/*              This block of code corresponds to the Products.                      */            
/*                                                                                   */
/*************************************************************************************/


  /**
   * @param productKey
   */
  public Product getProduct(String productKey){
    return _products.containsKey(productKey.toLowerCase()) ?
           _products.get(productKey.toLowerCase()) : null;
  }




  public Collection<Product> getProducts() {

    List<Product> orderedProducts = new ArrayList<Product>(_products.values());
    Collections.sort(orderedProducts, new ProductComparator());

    return Collections.unmodifiableList(orderedProducts);
  }

  /*public Collection<Product> getProducts() {

    Set<Product> orderedProducts = new TreeSet<Product>(new ProductComparator());
    for (Map.Entry<String, Product> entry : _products.entrySet())
      orderedProducts.add(entry.getValue());

    return Collections.unmodifiableSet(orderedProducts);
  } */


  /**
   * @param price
   */
  public Collection<Product> getProductsBelowPrice(int price) {
    Set<Product> products = new TreeSet<Product>(new ProductComparator());

    for (Map.Entry<String, Product> entry: _products.entrySet()) {
      Product p = entry.getValue();
      if (p.getPrice() < price) products.add(p);
    }

    return Collections.unmodifiableSet(products);
  }



  /**
   * @param productKey
   * @param stockToAdd
   */
  void addStock(String productKey, int stockToAdd) {
    Product product = _products.get(productKey.toLowerCase());
    product.addStock(stockToAdd);
  }


    /**
   * @param productKey
   * @param price
   * @throws MissingProductKeyException
   */
  void changePrice(String productKey, int price) throws MissingProductKeyException {
  
    if (! _products.containsKey(productKey.toLowerCase()))
      throw new MissingProductKeyException(productKey);

    if (price >= 0) {
      Product p = _products.get(productKey.toLowerCase());
      p.changePrice(price);
    }
  }




  /**
   * @param key
   * @param price
   * @param criticalValue
   * @param supplierKey
   * @param serviceLevel
   * @throws SericeTypeNotFoundException
   * @throws DupedProductKeyException
   * @throws MissingSupplierKeyException
   */
  void registBox(String key, int price, int criticalValue, String supplierKey,
                         String serviceLevel) throws ServiceTypeNotFoundException, 
                         DupedProductKeyException, MissingSupplierKeyException {
    ServiceLevel sLevel;
    
    try {
      sLevel = ServiceLevel.valueOf(serviceLevel);

    } catch(IllegalArgumentException exc) {
      throw new ServiceTypeNotFoundException(serviceLevel);
    }

    if (! _products.containsKey(key.toLowerCase())){
      Box box = new Box(key, price, criticalValue, supplierKey, sLevel.toString());

      Supplier supplier = _suppliers.get(supplierKey.toLowerCase());

      if (supplier != null) {
        _products.put(key.toLowerCase(), (Product) box);
        supplier.addProduct((Product) box);
        addAllObserversToProduct(box);
      }
  
      else throw new MissingSupplierKeyException(supplierKey);
    }

    else throw new DupedProductKeyException(key);
  }




  /**
   * @param key
   * @param price
   * @param criticalValue
   * @param supplierKey
   * @param serviceLevel
   * @param qualityLevel
   * @throws SericeTypeNotFoundException
   * @throws ServiceLevelNotFoundException
   * @throws DupedProductKeyException
   * @throws MissingSupplierKeyException
   */
  void registContainer(String key, int price, int criticalValue,
                            String supplierKey, String serviceLevel, String qualityLevel)
                            throws ServiceTypeNotFoundException, ServiceLevelNotFoundException,
                            DupedProductKeyException, MissingSupplierKeyException {
    ServiceLevel sLevel;
    QualityLevel qLevel;

    try {
      sLevel = ServiceLevel.valueOf(serviceLevel);

    } catch(IllegalArgumentException exc) {
      throw new ServiceTypeNotFoundException(serviceLevel);
    }

    try {
      qLevel = QualityLevel.valueOf(qualityLevel);

    } catch(IllegalArgumentException exc) {
      throw new ServiceLevelNotFoundException(qualityLevel);
    }


    if (! _products.containsKey(key.toLowerCase())){
      Container container = new Container(key, price, criticalValue, supplierKey, 
                        sLevel.toString(), qLevel.toString());
      
      Supplier supplier = _suppliers.get(supplierKey.toLowerCase());
      if (supplier != null){
        _products.put(key.toLowerCase(), (Product) container);
        supplier.addProduct((Product) container);
        addAllObserversToProduct(container);
      }

      else throw new MissingSupplierKeyException(supplierKey);
    }

    else throw new DupedProductKeyException(key);
  }




  /**
   * @param key
   * @param price
   * @param criticalValue
   * @param supplierKey
   * @param title
   * @param author
   * @param isbn
   * @throws DupedProductKeyException
   * @throws MissingSupplierKeyException
   */
  void registBook(String key, int price, int criticalValue,
                            String supplierKey, String title, String author, String isbn)
                            throws DupedProductKeyException, MissingSupplierKeyException {

    if (! _products.containsKey(key.toLowerCase())){
      Book book = new Book(key, price, criticalValue, supplierKey, title, author, isbn);

      Supplier supplier = _suppliers.get(supplierKey.toLowerCase());
      if (supplier != null){
        _products.put(key.toLowerCase(), (Product) book);
        supplier.addProduct((Product) book);
        addAllObserversToProduct(book);
      }

      else throw new MissingSupplierKeyException(supplierKey);
    }

    else throw new DupedProductKeyException(key);
  }


  public String produtoMaisRequisitado(int orderKey){
    Order o = _orders.get(orderKey);
    Collection<Item> items = o.getItems();
    Product p;
    boolean first = true;
    int maxUnits;

    for(Item i: items){
      if(first){
        p = i.getProduct();
        maxUnits = i.getAmmount();
        first = false;

      } else{

        if(i.getAmmount() > maxUnits){
          p = i.getProduct();
          maxUnits = i.getAmmount();
        } else if(i.getAmmount() == maxUnits){
          String productKey = p.getKey();
          Product p2 = i.getProduct();

          if(productKey.compareTo(p2.getKey()) ){
            // continue this :(
          }
        }

      }
    }

    return "" +  p.getKey() + "|" + maxUnits;
  }









/*************************************************************************************/
/*                                                                                   */
/*              This block of code corresponds to Notifications.                     */            
/*                                                                                   */
/*************************************************************************************/



  /**
  * @param notification
  */
  public void notify(Notification notification){
    _notifications.add(notification);
  }


  /**
  * @param product
  */
  void addAllObserversToProduct(Product product){
    product.addToObservers(this);

    for (Map.Entry<String, Client> entry: _clients.entrySet()) {
      Client client = entry.getValue();
      product.addToObservers(client);
    }
  }


  /**
  * @param observer
  */
  void addObserverToAllProducts(Observer observer){

    for (Map.Entry<String, Product> entry: _products.entrySet()) {
      Product product = entry.getValue();
      product.addToObservers(observer);
    }
  }




/*************************************************************************************/
/*                                                                                   */
/*              This block of code corresponds to Transactions.                      */            
/*                                                                                   */
/*************************************************************************************/



  /**
  * @param transactionKey
  * @throws MissingTransactionKeyException
  */
  public Transaction getTransaction(int transactionKey) 
        throws MissingTransactionKeyException {

    if (_sales.containsKey(transactionKey)) {
      Sale sale = _sales.get(transactionKey);
      sale.computeSaleCost(_date.getDate());
      return sale;
    }

    else if (_orders.containsKey(transactionKey)) {
      Order order = _orders.get(transactionKey);
      return order;
    }

    else throw new MissingTransactionKeyException();
  }




  /**
  * @param clientKey
  * @param paymentDeadline
  * @param productKey
  * @param ammount
  * @throws MissingClientKeyException
  * @throws MissingProductKeyException
  * @throws LowStockException
  */
  void registSale(String clientKey, int paymentDeadline, String productKey, int ammount)
    throws MissingClientKeyException, MissingProductKeyException, LowStockException {

    if (!_clients.containsKey(clientKey.toLowerCase()))
      throw new MissingClientKeyException(clientKey);

    if (!_products.containsKey(productKey.toLowerCase()))
      throw new MissingProductKeyException(productKey);

    Product product = _products.get(productKey.toLowerCase());
    if (ammount > product.getStock())
      throw new LowStockException();


    Client client = _clients.get(clientKey.toLowerCase());
    Item item = new Item(product, ammount);

    double baseValue = product.getPrice() * ammount;

    Sale sale = new Sale(_transactionID++, baseValue, client, item, paymentDeadline);

    _sales.put(sale.getID(), sale);
    client.addTransaction(sale);
    client.addTransactionsValue(baseValue);
    product.removeStock(ammount);
  }



  /**
  * @param supplierKey
  * @param itemsInfo
  * @throws MissingSupplierKeyException
  * @throws CantTradeException
  * @throws UnexistentProductInSupplierException
  */
  void registOrder(String supplierKey, Map<String, Integer> itemsInfo) 
    throws MissingSupplierKeyException, CantTradeException, 
      UnexistentProductInSupplierException, MissingProductKeyException {

    if (! _suppliers.containsKey(supplierKey.toLowerCase()))
      throw new MissingSupplierKeyException(supplierKey);

    Supplier supplier = _suppliers.get(supplierKey.toLowerCase());
    if (! supplier.getTransactionsStatus()) 
      throw new CantTradeException(supplierKey);
    

    double baseValue = 0;
    Collection<Item> items = new ArrayList<Item>();

    for (Map.Entry<String, Integer> entry : itemsInfo.entrySet()) {

      String productKey = entry.getKey();  

      if (! _products.containsKey(productKey.toLowerCase())) 
        throw new MissingProductKeyException(productKey);
      
      if (! supplier.hasProduct(productKey.toLowerCase())) 
        throw new UnexistentProductInSupplierException(productKey);


      Product product = _products.get(productKey.toLowerCase());
      int amount = entry.getValue();
      product.addStock(amount);
      Item i = new Item(product, amount);
      items.add(i);

      baseValue += (double)(product.getPrice() * amount);
    }


    Order order = new Order(_transactionID, baseValue, supplier, items, getDate());
    _orders.put(_transactionID++ , order);
    supplier.addTransaction(order);

    _spendings += baseValue;
    _actualBalance -= baseValue;
  }



  /**
  * @param transactionKey
  * @throws MissingTransactionKeyException
  */
  void pay(int transactionKey) throws MissingTransactionKeyException {

    if (_sales.containsKey(transactionKey)) {
      Sale sale = _sales.get(transactionKey);

      if (! sale.isPaid()) {
        Client client = sale.getClient();
        client.pay(sale, getDate());
        _actualBalance += sale.getPayedValue();
      }
    }

    else if (! _orders.containsKey(transactionKey)) 
      throw new MissingTransactionKeyException(); 
  }
}