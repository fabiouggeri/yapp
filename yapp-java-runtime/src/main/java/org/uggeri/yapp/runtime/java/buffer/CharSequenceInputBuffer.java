/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.runtime.java.buffer;

/**
 *
 * @author fabio
 */
public class CharSequenceInputBuffer extends AbstractInputBuffer {
   
   private final CharSequence buffer;

   public CharSequenceInputBuffer(char[] buffer) {
      this.buffer = new String(buffer);
   }
   
   public CharSequenceInputBuffer(CharSequence buffer) {
      this.buffer = buffer;
   }

   @Override
   protected CharSequence getBuffer() {
      return buffer;
   }
   
}
