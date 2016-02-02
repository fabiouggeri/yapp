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
public class EOIRule implements GrammarRule {

   @Override
   public void visit(ParserGenerationOptions options, GrammarRuleVisitor visitor) {
      visitor.visitEOI(options, this);
   }

   @Override
   public boolean isTerminal() {
      return false;
   }

   @Override
   public String toString() {
      return "<EOI>";
   }

   @Override
   public boolean isTest() {
      return true;
   }

   @Override
   public boolean canBeEmpty() {
      return true;
   }
}
