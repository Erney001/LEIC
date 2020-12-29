package woo.app.main;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

import woo.core.StoreManager;
import woo.core.exception.MissingFileAssociationException;

import java.io.IOException;
import java.io.FileNotFoundException;


/**
 * Save current state to file under current name (if unnamed, query for name).
 */
public class DoSave extends Command<StoreManager> {

  private Input<String> _filename;

  /** @param receiver */
  public DoSave(StoreManager receiver) {
    super(Label.SAVE, receiver);
  }


  /** 
   * @see pt.tecnico.po.ui.Command#execute()
   * @throws DialogException
   */
  @Override
  public final void execute() throws DialogException {

    try{
      _receiver.save();

    } catch (MissingFileAssociationException e) {

        try {
          _filename = _form.addStringInput(Message.newSaveAs());
          _form.parse();
          _receiver.saveAs(_filename.value());

        } catch(Exception e1) {
            System.out.println("Operation failed");
        }


    } catch (FileNotFoundException e) {
        System.out.println("Ficheiro nao encontrado");
    
    } catch (IOException e) {
        System.out.println("IOException");
    }
  }
}
