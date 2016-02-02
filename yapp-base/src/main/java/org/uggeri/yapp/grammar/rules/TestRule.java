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
public class TestRule implements GrammarRule, SimpleGrammarRule {

   private GrammarRule rule;

   public TestRule(GrammarRule rule) {
      this.rule = rule;
   }

   @Override
   public void visit(ParserGenerationOptions options, GrammarRuleVisitor visitor) {
      visitor.visitTest(options, this);
   }

   @Override
   public GrammarRule getRule() {
      return rule;
   }

   @Override
   public String toString() {
      return rule + "&";
   }

   @Override
   public boolean isTerminal() {
      return false;
   }

   @Override
   public boolean isTest() {
      return true;
   }

   @Override
   public void setRule(GrammarRule rule) {
      this.rule = rule;
   }

   @Override
   public boolean canBeEmpty() {
      return true;
   }
}
