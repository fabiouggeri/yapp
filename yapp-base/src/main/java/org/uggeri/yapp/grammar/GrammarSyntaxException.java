/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.grammar;

/**
 *
 * @author fabio
 */
public class GrammarSyntaxException extends GrammarException {

   private int line;
   private int column;
   
   public GrammarSyntaxException(int lineNumber, int colNumber, String msg) {
      super(msg);
      this.line = lineNumber;
      this.column = colNumber;
   }

   public GrammarSyntaxException(int lineNumber, int colNumber, Throwable cause) {
      super(cause);
      this.line = lineNumber;
      this.column = colNumber;
   }

   public GrammarSyntaxException(int lineNumber, int colNumber, String message, Throwable cause) {
      super(message, cause);
      this.line = lineNumber;
      this.column = colNumber;
   }

   public int getLine() {
      return line;
   }

   public int getColumn() {
      return column;
   }
}
