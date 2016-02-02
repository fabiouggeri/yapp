/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.runtime.java.parser;

import org.uggeri.yapp.runtime.java.buffer.InputBuffer;

/**
 *
 * @author fabio
 */
public class ParserContext {
   
   private final InputBuffer inputBuffer;

   public ParserContext(InputBuffer inputBuffer) {
      this.inputBuffer = inputBuffer;
   }

   public InputBuffer getInputBuffer() {
      return inputBuffer;
   }

}
