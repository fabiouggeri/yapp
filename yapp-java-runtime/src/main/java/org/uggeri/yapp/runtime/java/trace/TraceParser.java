/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.runtime.java.trace;

import org.uggeri.yapp.runtime.java.buffer.InputBuffer;
import java.io.Closeable;

/**
 *
 * @author fabio
 */
public interface TraceParser extends Closeable {
   
   public void enterRule(final InputBuffer inputBuffer, final String ruleName, final int index);

   public void exitRule(final InputBuffer inputBuffer, final int index, final boolean success);
}
