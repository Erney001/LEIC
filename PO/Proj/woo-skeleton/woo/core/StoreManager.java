package woo.core;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;

import java.util.Set;
import java.util.Map;
import java.util.List;

import woo.core.exception.UnavailableFileException;
import woo.core.exception.MissingFileAssociationException;
import woo.core.exception.ImportFileException;
import woo.core.exception.BadEntryException;
import woo.core.exception.ServiceTypeNotFoundException;
import woo.core.exception.ServiceLevelNotFoundException;
import woo.core.exception.DupedProductKeyException;
import woo.core.exception.DupedClientKeyException;
import woo.core.exception.DupedSupplierKeyException;
import woo.core.exception.MissingClientKeyException;
import woo.core.exception.MissingSupplierKeyException;
import woo.core.exception.MissingTransactionKeyException;
import woo.core.exception.MissingProductKeyException;
import woo.core.exception.LowStockException;
import woo.core.exception.CantTradeException;
import woo.core.exception.UnexistentProductInSupplier;

import woo.core.Transaction.Transaction;


/**
 * StoreManager: façade for the core classes.
 */
public class StoreManager {

  /** Current filename. */
  private String _filename = "";

  /** The actual store. */
  private Store _store = new Store();



  /* This block of code corresponds to file Management */


  /**
   * @throws IOException
   * @throws FileNotFoundException
   * @throws MissingFileAssociationException
   */
  public void save()
         throws IOException, FileNotFoundException, MissingFileAssociationException {
    
    if(_filename.equals("")) {
      throw new MissingFileAssociationException();
    }
    
    try (ObjectOutputStream _outFile = new ObjectOutputStream(new FileOutputStream(_filename))){
      _outFile.writeObject(_store);

    } catch (FileNotFoundException e){
        throw new FileNotFoundException();
        
    } catch (IOException e){
        System.out.println(e.getMessage());
        throw new IOException();
    }
  }


  /**
   * @param filename
   * @throws MissingFileAssociationException
   * @throws IOException
   * @throws FileNotFoundException
   */
  public void saveAs(String filename)
             throws MissingFileAssociationException, FileNotFoundException, IOException {
    
    _filename = filename;
    save();
  }


  /**
   * @param filename
   * @throws UnavailableFileException
   * @throws FileNotFoundException
   * @throws IOException
   * @throws ClassNotFoundException
   */
  public void load(String filename) throws UnavailableFileException,
                            FileNotFoundException, IOException, ClassNotFoundException {

    try (ObjectInputStream _inFile = new ObjectInputStream(new FileInputStream(filename))){

      Object obj = _inFile.readObject();
      _store = (Store) obj;
    }
  }


  /**
   * @param textfile
   * @throws ImportFileException
   */
  public void importFile(String textfile) throws ImportFileException {
    try {
      _store.importFile(textfile);
    
    } catch (IOException | BadEntryException e) {
      throw new ImportFileException(textfile);
    }
  }





  /* This block of functions correspond to the Date */


  public int displayDate() {
    return _store.getDate();
  }


  /**
   * @param daysToAdvance
   */
  public void advanceCurrentDate(int daysToAdvance) {
    _store.advanceCurrentDate(daysToAdvance);
  }


  /* This block of functions correspond to the Accounting Values. */

  public float getSaldoDisponivel(){
    return _store.getSaldoDisponivel();
  }

  public float getSaldoContabilistico(){
    return _store.getSaldoContabilistico();
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

    _store.registClient(id, name, address);
  }


  public Map<String, Client> getClients() {
    return _store.getClients();
  }

  public List<Notification> getClientNotifications(String clientKey){
    return _store.getClientNotifications(clientKey);
  }


  /**
   * @param clientKey
   */
  public Client getClient(String clientKey) {
    return _store.getClient(clientKey);
  }


  public boolean toggleProductNotifications(String clientKey, String productKey)
    throws DupedClientKeyException, DupedProductKeyException {
    return _store.toggleProductNotifications(clientKey, productKey);
  }
  


  /* This block of code correspond to Suppliers. */


  /**
   * @param supplierKey
   * @param name
   * @param address
   * @throws DupedSupplierKeyException
   */
  public void registSupplier(String supplierKey, String name, String address)
                                throws DupedSupplierKeyException {

    _store.registSupplier(supplierKey, name, address);
  }


  public Map<String, Supplier> getSuppliers(){
    return _store.getSuppliers();
  }


  /**
   * @param supplierKey
   * @throws MissingSupplierKeyException
   */
  public boolean toggleStatus(String supplierKey) throws MissingSupplierKeyException {
    return _store.toggleStatus(supplierKey);
  }


  public List<Transaction> getSupplierTransactions(String supplierKey) throws MissingSupplierKeyException {
    return _store.getSupplierTransactions(supplierKey);
  }



  /* This block of code correspond to Products */

  public Product getProduct(String productKey) {
    return _store.getProduct(productKey);
  }
  

  public Map<String, Product> getProducts() {
    return _store.getProducts();
  }


  public Map<String, Product> getProductsBelowPrice(int price){
    return _store.getProductsBelowPrice(price);
  }


  /**
   * @param key
   * @param price
   * @param criticalValue
   * @param supplierKey
   * @param serviceLevel
   * @throws ServiceTypeNotFoundException
   * @throws DupedProductKeyException
   * @throws MissingSupplierKeyException
   */
  public void registBox(String key, int price, int criticalValue,
                            String supplierKey, String serviceLevel)
                            throws ServiceTypeNotFoundException, DupedProductKeyException,
                            MissingSupplierKeyException {

    _store.registBox(key, price, criticalValue, supplierKey, serviceLevel);
  }


  /**
   * @param key
   * @param price
   * @param criticalValue
   * @param supplierKey
   * @param serviceLevel
   * @param qualityLevel
   * @throws ServiceTypeNotFoundException
   * @throws ServiceLevelNotFoundException
   * @throws DupedProductKeyException
   * @throws MissingSupplierKeyException
   */
  public void registContainer(String key, int price, int criticalValue,
                            String supplierKey, String serviceLevel, String qualityLevel)
                            throws ServiceTypeNotFoundException, ServiceLevelNotFoundException,
                            DupedProductKeyException, MissingSupplierKeyException {

    _store.registContainer(key, price, criticalValue, supplierKey, serviceLevel, qualityLevel);
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

    _store.registBook(key, price, criticalValue, supplierKey,
                              title, author, isbn);      
  }


  public void changePrice(String key, int price){
    _store.changePrice(key, price);
  }



  /* This block of code corresponds to Transactions */

  public Transaction getTransaction(int transactionKey) throws MissingTransactionKeyException {
    return _store.getTransaction(transactionKey);
  }


  public void registSale(String clientKey, int paymentDeadline, String productKey, int amount)
    throws MissingClientKeyException, MissingProductKeyException, LowStockException {
    _store.registSale(clientKey, paymentDeadline, productKey, amount);
  }


  public void registOrder(String supplierKey, String productKey, int amount) throws MissingSupplierKeyException,
    CantTradeException, UnexistentProductInSupplier, MissingProductKeyException {
    _store.registOrder(supplierKey, productKey, amount);
  }
  

  public void registProductInOrder(String productKey, int amount){
    // TO DO
    _store.registProductInOrder(productKey, amount);
  }


  public List<Transaction> getPaidTransactionsByClient(String clientKey) throws MissingClientKeyException {
    return _store.getPaidTransactionsByClient(clientKey);
  }
}
