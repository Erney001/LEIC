package woo.core;

import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.Serializable;

import woo.core.exception.BadEntryException;
import woo.core.exception.DupedProductKeyException;
import woo.core.exception.ServiceLevelNotFoundException;
import woo.core.exception.ServiceTypeNotFoundException;
import woo.core.exception.DupedClientKeyException;
import woo.core.exception.DupedSupplierKeyException;
import woo.core.exception.MissingSupplierKeyException;
import woo.core.exception.MissingClientKeyException;


public class MyParser implements Serializable {

  private Store _store;

  public MyParser(Store s) {
    _store = s;
  }


  /**
   * @param fileName
   * @throws IOException
   * @throws BadEntryException
  */
  void parseFile(String fileName) throws IOException, BadEntryException {

    try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
      String line;
      
      while ((line = reader.readLine()) != null)
        parseLine(line);
    } 
  }

  /**
  * @param line
  * @throws BadEntryException
  */
  void parseLine(String line) throws BadEntryException {
    
    String[] components = line.split("\\|");

    switch(components[0]) {
      case "SUPPLIER":
        parseSupplier(line, components);
        break;

      case "CLIENT":
        parseClient(line, components);
        break;

      case "BOX":
        parseBox(line, components);
        break;

      case "CONTAINER":
        parseContainer(line, components);
        break;

      case "BOOK":
        parseBook(line, components);
        break;

      default:
        throw new BadEntryException("Type of line not supported: " + line);
    }
  }



  /**
  * @param line
  * @param components
  * @throws BadEntryException
  */
  private void parseSupplier(String line, String[] components) throws BadEntryException {

    if (components.length != 4)
      throw new BadEntryException("Invalid number of fields in supplier description (4) " + line);

    String id = components[1];
    String name = components[2];
    String address = components[3];

	  try {
      _store.registSupplier(id, name, address);
    
    } catch (DupedSupplierKeyException exc) {
      throw new BadEntryException("Supplier key already exists: " + id);
    }
  }



  /**
  * @param line
  * @param components
  * @throws BadEntryException
  */
  private void parseClient(String line, String[] components) throws BadEntryException {

    if (components.length != 4)
      throw new BadEntryException("Invalid number of fields (4) in client description: " + line);

	  String id = components[1];
    String name = components[2];
    String address = components[3];

    try {
      _store.registClient(id, name, address);

    } catch (DupedClientKeyException exc) {
      throw new BadEntryException("Client key already exists: " + id);
    } catch (MissingClientKeyException exc) {
      throw new BadEntryException("Client key already exists: " + id);
    }
  }



  /**
  * @param line
  * @param components
  * @throws BadEntryException
  */
  private void parseBox(String line, String[] components) throws BadEntryException {

    if (components.length != 7)
      throw new BadEntryException("wrong number of fields in box description (7) " + line);

    String id = components[1];
    String serviceLevel = components[2];
	  String supplierKey = components[3];
    int price = Integer.parseInt(components[4]);
	  int criticalValue = Integer.parseInt(components[5]);
	  int stock = Integer.parseInt(components[6]);
    
  	try{
  		_store.registBox(id, price, criticalValue, supplierKey, serviceLevel);
      Product box = _store.getProduct(id);
      box.setStock(stock);

  	} catch(DupedProductKeyException exc) {
      throw new BadEntryException("Error: Product key already exists: " + id);
    
    } catch(ServiceTypeNotFoundException exc) {
      throw new BadEntryException("Error: Service Type is not valid: " + serviceLevel);
    
    } catch(MissingSupplierKeyException exc) {
      throw new BadEntryException("Error: There is no supplier with key: " + supplierKey);
    }
  }



  /**
  * @param line
  * @param components
  * @throws BadEntryException
  */
  private void parseContainer(String line, String[] components) throws BadEntryException {
    
    if (components.length != 8)
      throw new BadEntryException("Invalid number of fields (8) in container description: " + line);

    String id = components[1];
    String serviceLevel = components[2];
	  String qualityLevel = components[3];
	  String supplierKey = components[4];
    int price = Integer.parseInt(components[5]);
	  int criticalValue = Integer.parseInt(components[6]);
	  int stock = Integer.parseInt(components[7]);

  	try{
  		_store.registContainer(id, price, criticalValue, supplierKey, serviceLevel, qualityLevel);
  		Product container = _store.getProduct(id);
      container.setStock(stock);

  	} catch(DupedProductKeyException exc) {
      throw new BadEntryException("Error: Product key already exists: " + id);
    
    } catch(ServiceTypeNotFoundException exc) {
      throw new BadEntryException("Error: Service Type is not valid: " + serviceLevel);
    
    } catch(ServiceLevelNotFoundException exc) {
      throw new BadEntryException("Error: Quality Level is not valid: " + qualityLevel);
    
    } catch(MissingSupplierKeyException exc) {
      throw new BadEntryException("Error: There is no supplier with key: " + supplierKey); 
    }
  }
  


  /**
  * @param line
  * @param components
  * @throws BadEntryException
  */
  private void parseBook(String line, String[] components) throws BadEntryException{
                            
   if (components.length != 9)
      throw new BadEntryException("Invalid number of fields (9) in box description: " + line);

    String id = components[1];
    String title = components[2];
    String author = components[3];
    String isbn = components[4];
    String supplierKey = components[5];
    int price = Integer.parseInt(components[6]);
    int criticalValue = Integer.parseInt(components[7]);
    int stock = Integer.parseInt(components[8]);

    try{
      _store.registBook(id, price, criticalValue, supplierKey, title, author, isbn);
      Product book = _store.getProduct(id);
      book.setStock(stock);

    } catch(DupedProductKeyException exc) {
      throw new BadEntryException("Error: Product key already exists: " + id);
    
    } catch(MissingSupplierKeyException exc) {
      throw new BadEntryException("Error: There is no supplier with key: " + supplierKey); 
    }

  }
}