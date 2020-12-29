package woo.app.main;

import pt.tecnico.po.ui.Command;
import pt.tecnico.po.ui.DialogException;
import pt.tecnico.po.ui.Input;
import woo.core.StoreManager;

/**
 * Show global balance.
 */
public class DoShowGlobalBalance extends Command<StoreManager> {

  private int saldoDisponivel, saldoContabilistico;

  public DoShowGlobalBalance(StoreManager receiver) {
    super(Label.SHOW_BALANCE, receiver);
  }

  @Override
  public final void execute() {
    saldoDisponivel = Math.round(_receiver.getSaldoDisponivel());
    saldoContabilistico = Math.round(_receiver.getSaldoContabilistico());
    _display.popup(Message.currentBalance(saldoDisponivel, saldoContabilistico));
  }
}
