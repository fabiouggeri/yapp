/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.grammar;

/**
 *
 * @author fabio
 */
public class GrammarException extends Exception {

   public GrammarException() {
   }

   public GrammarException(String message) {
      super(message);
   }

   public GrammarException(Throwable cause) {
      super(cause);
   }

   public GrammarException(String message, Throwable cause) {
      super(message, cause);
   }
   
}
