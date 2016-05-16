/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.grammar.rules;

/**
 *
 * @author fabio
 */
public abstract class TerminalRule implements GrammarRule {

   @Override
   public boolean isTerminal() {
      return true;
   }
   
   public abstract String getText();
   
   public abstract int length();
}
