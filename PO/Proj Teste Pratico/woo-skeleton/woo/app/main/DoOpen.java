package woo.app.main;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;

import woo.app.exception.FileOpenFailedException;

import woo.core.StoreManager;
import woo.core.exception.UnavailableFileException;

import java.io.IOException;
import java.io.FileNotFoundException;


/**
 * Open existing saved state.
 */
public class DoOpen extends Command<StoreManager> {

  private final Input<String> _filename;

  /** @param receiver */
  public DoOpen(StoreManager receiver) {
    super(Label.OPEN, receiver);
    _filename = _form.addStringInput(Message.openFile());
  }


  /** 
    * @see pt.tecnico.po.ui.Command#execute()
    * @throws DialogException
   */
  @Override
  public final void execute() throws DialogException {
    
    try {
      _form.parse();
      _receiver.load(_filename.value());

    } catch (Exception e){
      throw new FileOpenFailedException(_filename.value()); 
    }
  }

}