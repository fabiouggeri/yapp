/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.generation.stmt;

import java.util.List;

/**
 *
 * @author fabio
 */
public interface SwitchStatement extends Statement {
   
   public SwitchOption switchOption(final Object... literalValues);
   
   public List<SwitchOption> listOptions();
   
}
