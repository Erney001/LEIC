package woo.core;

import java.io.Serializable;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.List;

import woo.core.Client;
import woo.core.MyParser;
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
import woo.core.Observer.Observer;
import woo.core.Transaction.Transaction;
import woo.core.exception.MissingTransactionKeyException;
import woo.core.exception.MissingProductKeyException;
import woo.core.exception.LowStockException;
import woo.core.exception.CantTradeException;
import woo.core.exception.UnexistentProductInSupplier;

import woo.core.Transaction.Sale;
import woo.core.Transaction.Order;
import woo.core.Transaction.Item;

/**
 * Class Store implements a store.
 */
public class Store implements Serializable {

  /** Serial number for serialization. */
  private static final long serialVersionUID = 202009192006L;

  private int _dataAtual;
  private float _saldoDisponivel;
  private float _saldoContabilistico;
  private Map<String, Client> _clients;
  private Map<String, Supplier> _suppliers;
  private Map<String, Product> _products;
  private Map<String, Notification> _notifications;
  private Map<Integer, Transaction> _transactions;
  private int _transactionID;

  public Store() {
    _clients = new LinkedHashMap<String, Client>();
    _suppliers = new LinkedHashMap<String, Supplier>();
    _products = new LinkedHashMap<String, Product>();
    _notifications = new LinkedHashMap<String, Notification>();
    _transactions = new LinkedHashMap<Integer, Transaction>();
    _dataAtual = 0;
    _saldoDisponivel = 0f;
    _saldoContabilistico = 0f;
    _transactionID = 0;
  }



  /* This block corresponds to the import of a File with the state of the App */

  /**
   * @param filename filename to be loaded.
   * @throws IOException
   * @throws BadEntryException
   */
  public void importFile(String filename) throws IOException, BadEntryException {
    
    MyParser parser = new MyParser(this);  

    parser.parseFile(filename);    
  }




  /* This block of functions correspond to the Date */


  public int getDate() {
    return _dataAtual;
  }


  /**
   * @param daysToAdvance
   */
  public void advanceCurrentDate(int daysToAdvance) {
    _dataAtual = _dataAtual + daysToAdvance;
  }  


  /* This block of functions correspond to the Accounting Values. */

  public float getSaldoDisponivel(){
    return _saldoDisponivel;
  }

  public float getSaldoContabilistico(){
    return _saldoContabilistico;
  }


  /* This block of functions correspond to the Clients. */


  /**
   * @param id
   * @param name
   * @param address
   * @throws DupedClientKeyException
   */
  public void registClient(String id, String name, String address)
                           throws DupedClientKeyException {

    if (! _clients.containsKey(id)){
      Client client = new Client(id, name, address);
      _clients.put(id, client);
      addObserverToNotifications(client);
    }

    else throw new DupedClientKeyException(id);
  }



  /**
   * @param clientKey
   */
  public Client getClient(String clientKey){
    return _clients.containsKey(clientKey) ? _clients.get(clientKey) : null;
  }


  public Map<String, Client> getClients() {
    return new TreeMap<String, Client>(_clients);
  }


  public List<Notification> getClientNotifications(String clientKey){
    Client client = _clients.get(clientKey);
    return client.getNotifications();
  }


  public boolean toggleProductNotifications(String clientKey, String productKey) throws
      DupedClientKeyException, DupedProductKeyException {
    // n sei se sao estas as excessoes corretas, agr e que notei isso

    if(!_clients.containsKey(clientKey)){
      throw new DupedClientKeyException(clientKey);
    }

    if(!_products.containsKey(productKey)){
      throw new DupedProductKeyException(productKey);
    }

    boolean result;
    Client client = _clients.get(clientKey);
    //Product product = _products.get(productKey);
    Notification notification = _notifications.get(productKey);

    result = client.toggleProductNotifications(notification);

    // If activated notification, add Client as notification Observer
    if(result){
      notification.add(client);
    } else{
      notification.remove(client);
    }

    return result;
  }


  /* This block of code corresponds to Suppliers. */


  /**
   * @param supplierKey
   * @param name
   * @param address
   * @throws DupedSupplierKeyException
   */
  public void registSupplier(String supplierKey, String name, String address)
                              throws DupedSupplierKeyException {

    if (! _suppliers.containsKey(supplierKey)) {
      Supplier supplier = new Supplier(supplierKey, name, address);
      _suppliers.put(supplierKey, supplier);
    }

    else throw new DupedSupplierKeyException(supplierKey);
  }


  public Map<String, Supplier> getSuppliers() {
    return new TreeMap<String, Supplier>(_suppliers);
  }



  /**
   * @param supplierKey
   * @throws MissingSupplierKeyException
   */
  public boolean toggleStatus(String supplierKey) throws MissingSupplierKeyException {
    if (_suppliers.containsKey(supplierKey)){
      Supplier supplier = _suppliers.get(supplierKey);
      return supplier.toggleStatus();
    }

    else throw new MissingSupplierKeyException(supplierKey);
  }


  public List<Transaction> getSupplierTransactions(String supplierKey) throws MissingSupplierKeyException {
    if(!_suppliers.containsKey(supplierKey)){
      throw new MissingSupplierKeyException(supplierKey);
    }
    
    Supplier supplier = _suppliers.get(supplierKey);
    return supplier.getTransactions();
  }



  /* This block of code corresponds to the Products */


  public Product getProduct(String productKey){
    return _products.containsKey(productKey) ? _products.get(productKey) : null;
  }


  public Map<String, Product> getProducts() {
    return new TreeMap<String, Product>(_products);
  }


  public Map<String, Product> getProductsBelowPrice(int price) {
    Map<String, Product> products = new TreeMap<String, Product>();

    for(Map.Entry<String, Product> entry: _products.entrySet()){
      Product p = entry.getValue();
      if(p.getPrice() < price){
        products.put(entry.getKey(), p);
      }
    }

    // meter unmodifiable
    return products;
  }


  /**
   * @param productKey
   * @param stockToAdd
   */
  public void addStock(String productKey, int stockToAdd){
    Product product = _products.get(productKey);
    product.addStock(stockToAdd);
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
  public void registBox(String key, int price, int criticalValue, String supplierKey,
                         String serviceLevel) throws ServiceTypeNotFoundException, 
                         DupedProductKeyException, MissingSupplierKeyException {
    ServiceLevel sLevel;
    
    try {
      sLevel = ServiceLevel.valueOf(serviceLevel);

    } catch(IllegalArgumentException exc) {
      throw new ServiceTypeNotFoundException(serviceLevel);
    }

    if (! _products.containsKey(key)){
      Box box = new Box(key, price, criticalValue, supplierKey, sLevel.toString());

      Supplier supplier = _suppliers.get(supplierKey);
      if (supplier != null){
        _products.put(key, (Product) box);
        supplier.addProduct((Product) box);
        createNotification(box);
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
  public void registContainer(String key, int price, int criticalValue,
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


    if (! _products.containsKey(key)){
      Container container = new Container(key, price, criticalValue, supplierKey, 
                        sLevel.toString(), qLevel.toString());
      
      Supplier supplier = _suppliers.get(supplierKey);
      if (supplier != null){
        _products.put(key, (Product) container);
        supplier.addProduct((Product) container);
        createNotification(container);
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
  public void registBook(String key, int price, int criticalValue,
                            String supplierKey, String title, String author, String isbn)
                            throws DupedProductKeyException, MissingSupplierKeyException {

    if (! _products.containsKey(key)){
      Book book = new Book(key, price, criticalValue, supplierKey, title, author, isbn);

      Supplier supplier = _suppliers.get(supplierKey);
      if (supplier != null){
        _products.put(key, (Product) book);
        supplier.addProduct((Product) book);
        createNotification(book);
      }

      else throw new MissingSupplierKeyException(supplierKey);
    }

    else throw new DupedProductKeyException(key);
  }


  /**
   * @param key
   * @param price
   */
  public void changePrice(String productKey, int price){
    // como a funcao falha silenciosamente, n meti excessoes

    if(!_products.containsKey(productKey)){
      return;
    }

    Product p = _products.get(productKey);
    p.changePrice(price);

    // avisar clientes que o preco mudou
    Notification n = _notifications.get(productKey);
    n.setType("BARGAIN");
  }


  private void createNotification(Product p){
    Notification n = new Notification("DEFAULT", p);
    _notifications.put(p.getKey(), n);
    
    for(Map.Entry<String, Client> entry: _clients.entrySet()){
      n.add(entry.getValue());
    }

    //n.notifyObservers();
  }


  private void addObserverToNotifications(Observer o){
    for(Map.Entry<String, Notification> entry: _notifications.entrySet()){
      entry.getValue().add(o);
    }
  }



  /* This block of code correspond to Transactions */

  public Transaction getTransaction(int transactionKey) throws MissingTransactionKeyException {
    Transaction transaction = _transactions.get(transactionKey);

    if(transaction == null){
      throw new MissingTransactionKeyException();
    }

    return transaction;
  }


  public void registSale(String clientKey, int paymentDeadline, String productKey, int amount)
    throws MissingClientKeyException, MissingProductKeyException, LowStockException {

    if(!_clients.containsKey(clientKey)){
      throw new MissingClientKeyException();
    }

    if(!_products.containsKey(productKey)){
      throw new MissingProductKeyException();
    }

    Product product = _products.get(productKey);
    if(amount > product.getStock()){
      throw new LowStockException();
    }

    Client client = _clients.get(clientKey);
    Item item = new Item(product, amount);

    Sale sale = new Sale(_transactionID++, 0, _dataAtual, client, item, paymentDeadline);

    _transactions.put(sale.getID(), sale);
    client.addTransaction(sale);
  }


  public void registProductInOrder(String productKey, int amount){
    // TO DO
  }


  public void registOrder(String supplierKey, String productKey, int amount) throws MissingSupplierKeyException,
    CantTradeException, UnexistentProductInSupplier, MissingProductKeyException {

    if(!_suppliers.containsKey(supplierKey)){
      throw new MissingSupplierKeyException(supplierKey);
    }

    Supplier supplier = _suppliers.get(supplierKey);

    if(!supplier.getStatus()){
      throw new CantTradeException(supplierKey);
    }

    if(!_products.containsKey(productKey)){
      throw new MissingProductKeyException(productKey);
    }

    if(!supplier.hasProduct(productKey)){
      throw new UnexistentProductInSupplier(productKey);
    }

    // TO DO
  }


  public List<Transaction> getPaidTransactionsByClient(String clientKey) throws MissingClientKeyException {
    if(!_clients.containsKey(clientKey)){
      throw new MissingClientKeyException(clientKey);
    }

    Client client = _clients.get(clientKey);

    return client.getPaidTransactions();
  }
}