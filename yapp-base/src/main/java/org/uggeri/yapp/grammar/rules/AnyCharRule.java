/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.grammar.rules;

import org.uggeri.yapp.generation.ParserGenerationOptions;
import org.uggeri.yapp.grammar.GrammarRuleVisitor;

/**
 *
 * @author fabio
 */
public class AnyCharRule extends TerminalRule {

   @Override
   public void visit(ParserGenerationOptions options, GrammarRuleVisitor visitor) {
      visitor.visitAnyChar(options, this);
   }

   @Override
   public String toString() {
      return ".";
   }

   @Override
   public String getText() {
      return "";
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof AnyCharRule;
   }

   @Override
   public int hashCode() {
      int hash = 7;
      return hash;
   }

   @Override
   public int length() {
      return 1;
   }
}
