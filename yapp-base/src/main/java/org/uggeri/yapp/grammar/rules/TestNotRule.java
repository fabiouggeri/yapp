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
public class TestNotRule implements GrammarRule, SimpleGrammarRule {

   private GrammarRule rule;

   public TestNotRule(GrammarRule rule) {
      this.rule = rule;
   }

   @Override
   public void visit(ParserGenerationOptions options, GrammarRuleVisitor visitor) {
      visitor.visitTestNot(options, this);
   }

   @Override
   public GrammarRule getRule() {
      return rule;
   }

   @Override
   public String toString() {
      return rule + "!";
   }

   @Override
   public boolean isTerminal() {
      return false;
   }

   @Override
   public void setRule(GrammarRule rule) {
      this.rule = rule;
   }
}
