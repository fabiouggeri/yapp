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
public class EmptyRule implements GrammarRule {

   @Override
   public void visit(ParserGenerationOptions options, GrammarRuleVisitor visitor) {
      visitor.visitEmpty(options, this);
   }

   @Override
   public boolean isTerminal() {
      return false;
   }

   @Override
   public String toString() {
      return "<EMPTY>";
   }
}
