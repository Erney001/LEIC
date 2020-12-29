package woo.core;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;

import java.util.Map;
import java.util.Collection;

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
import woo.core.exception.UnexistentProductInSupplierException;



/**
 * StoreManager: façade for the core classes.
 */
public class StoreManager {

  /** Current filename. */
  private String _filename = "";

  /** The actual store. */
  private Store _store = new Store();





/*************************************************************************************/
/*                                                                                   */
/*        This block corresponds to managing files with the state of the App         */            
/*                                                                                   */
/*************************************************************************************/


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
   * @throws IOException
   * @throws FileNotFoundException
   * @throws MissingFileAssociationException
   */
  public void save()
         throws IOException, FileNotFoundException, MissingFileAssociationException {
    
    if (_filename.equals(""))
      throw new MissingFileAssociationException();
    
    try (ObjectOutputStream _outFile = new ObjectOutputStream(new FileOutputStream(_filename))) {
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
   * @throws UnavailableFileException
   * @throws FileNotFoundException
   * @throws IOException
   * @throws ClassNotFoundException
   */
  public void load(String filename) throws UnavailableFileException, 
                              FileNotFoundException, IOException, ClassNotFoundException {

    try (ObjectInputStream inFile = new ObjectInputStream(new FileInputStream(filename))) {
      
      Object obj = inFile.readObject();
      _store = (Store) obj;
      _filename = filename;
    }
  }



  /**
   * @param textfile
   * @throws ImportFileException
   */
  public void importFile(String textfile) throws ImportFileException {
    try {
      _store.importFile(textfile);
    
    } catch (IOException | BadEntryException | IllegalArgumentException e) {
      throw new ImportFileException(textfile);
    }
  }





/*************************************************************************************/
/*                                                                                   */
/*               This block of functions correspond to the daysToAdvance             */            
/*                                                                                   */
/*************************************************************************************/


  public int displayDate() {
    return _store.getDate();
  }


  /**
   * @param daysToAdvance
   */
  public void advanceCurrentDate(int daysToAdvance) {
    _store.advanceCurrentDate(daysToAdvance);
  }




/*************************************************************************************/
/*                                                                                   */
/*         This block of functions correspond to store's Accounting Values.          */            
/*                                                                                   */
/*************************************************************************************/


  public double getSaldoDisponivel(){
    return _store.getSaldoDisponivel();
  }

  public double getSaldoContabilistico(){
    return _store.getSaldoContabilistico();
  }



/*************************************************************************************/
/*                                                                                   */
/*               This block of functions correspond to the Clients.                  */            
/*                                                                                   */
/*************************************************************************************/


  /**
   * @param id
   * @param name
   * @param address
   * @throws DupedClientKeyException
   */
  public void registClient(String id, String name, String address)
                           throws DupedClientKeyException, MissingClientKeyException {
    _store.registClient(id, name, address);
  }



  public Collection<Client> getClients() {
    return _store.getClients();
  }


  /**
   * @param clientKey
   */
  public Collection<Notification> getClientNotifications(String clientKey){
    return _store.getClientNotifications(clientKey);
  }


  /**
   * @param clientKey
   * @throws MissingClientKeyException
   */
  public Client getClient(String clientKey) throws MissingClientKeyException {
    return _store.getClient(clientKey);
  }


  /**
   * @param clientKey
   * @param productKey
   * @throws MissingClientKeyException
   * @throws MissingProductKeyException
   */
  public boolean toggleProductNotifications(String clientKey, String productKey)
                 throws MissingClientKeyException, MissingProductKeyException {

    return _store.toggleProductNotifications(clientKey, productKey);
  }


  /**
   * @param clientKey
   * @throws MissingClientKeyException
   */
  public Collection<Transaction> getClientTransactions(String clientKey)
        throws MissingClientKeyException {

    return _store.getClientTransactions(clientKey);
  }
  

  public Collection<Transaction> removeSales(String clientKey, int value){
    return _store.removeSales(clientKey, value);
  }



/*************************************************************************************/
/*                                                                                   */
/*             This block of functions correspond to the Suppliers.                  */            
/*                                                                                   */
/*************************************************************************************/


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


  public Collection<Supplier> getSuppliers(){
    return _store.getSuppliers();
  }


  /**
   * @param supplierKey
   * @throws MissingSupplierKeyException
   */
  public boolean toggleStatus(String supplierKey) throws MissingSupplierKeyException {
    return _store.toggleStatus(supplierKey);
  }



  /**
   * @param supplierKey
   * @throws MissingSupplierKeyException
   */
  public Collection<Transaction> getSupplierTransactions(String supplierKey)
         throws MissingSupplierKeyException {

    return _store.getSupplierTransactions(supplierKey);
  }





/*************************************************************************************/
/*                                                                                   */
/*                 This block of functions correspond to Products.                   */            
/*                                                                                   */
/*************************************************************************************/


  /**
   * @param productKey
   */
  public Product getProduct(String productKey) {
    return _store.getProduct(productKey);
  }
  

  public Collection<Product> getProducts() {
    return _store.getProducts();
  }


  /**
   * @param price
   */
  public Collection<Product> getProductsBelowPrice(int price){
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

    _store.registBook(key, price, criticalValue, supplierKey, title, author, isbn);      
  }



  /**
   * @param productKey
   * @param price
   * @throws MissingProductKeyException
   */
  public void changePrice(String productKey, int price) throws MissingProductKeyException {
    _store.changePrice(productKey, price);
  }


  public String produtoMaisRequisitado(int orderKey){
    return _store.produtoMaisRequisitado(orderKey);
  }


/*************************************************************************************/
/*                                                                                   */
/*                 This block of code corresponds to Transactions.                   */            
/*                                                                                   */
/*************************************************************************************/


  /**
   * @param transactionKey
   * @throws MissingTransactionKeyException
   */
  public Transaction getTransaction(int transactionKey) throws MissingTransactionKeyException {
    return _store.getTransaction(transactionKey);
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
  public void registSale(String clientKey, int paymentDeadline, String productKey,
                         int ammount) 
    throws MissingClientKeyException, MissingProductKeyException, LowStockException {
    
    _store.registSale(clientKey, paymentDeadline, productKey, ammount);
  }


  /**
   * @param supplierKey
   * @param itemsInfo
   * @throws MissingSupplierKeyException
   * @throws MissingProductKeyException
   * @throws CantTradeException
   * @throws UnexistentProductInSupplierException
   */
  public void registOrder(String supplierKey, Map<String, Integer> itemsInfo) 
        throws MissingSupplierKeyException, CantTradeException, 
        UnexistentProductInSupplierException, MissingProductKeyException {

    _store.registOrder(supplierKey, itemsInfo);
  }


  /**
   * @param clientKey
   * @throws MissingClientKeyException
   */
  public Collection<Transaction> getPaidTransactionsByClient(String clientKey) 
        throws MissingClientKeyException {

    return _store.getPaidTransactionsByClient(clientKey);
  }


  /**
   * @param transactionKey
   * @throws MissingTransactionKeyException
   */
  public void pay(int transactionKey) throws MissingTransactionKeyException {
    _store.pay(transactionKey);
  }
}
