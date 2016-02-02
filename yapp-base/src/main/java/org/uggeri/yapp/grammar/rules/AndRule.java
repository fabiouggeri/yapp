/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.grammar.rules;

import org.uggeri.yapp.generation.ParserGenerationOptions;
import org.uggeri.yapp.grammar.GrammarRuleVisitor;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author fabio
 */
public class AndRule implements GrammarRule, ComposedGrammarRule {

   private List<GrammarRule> rules;

   public AndRule(GrammarRule... rules) {
      this.rules = Arrays.asList(rules);
   }

   public AndRule(List<GrammarRule> rules) {
      this.rules = rules;
   }

   @Override
   public void visit(ParserGenerationOptions options, GrammarRuleVisitor visitor) {
      visitor.visitAnd(options, this);
   }

   @Override
   public List<GrammarRule> getRules() {
      return rules;
   }

   @Override
   public void setRules(List<GrammarRule> rules) {
      this.rules = rules;
   }

   @Override
   public String toString() {
      if (rules != null) {
         StringBuilder sb = new StringBuilder("(");
         for (GrammarRule rule : rules) {
            if (sb.length() > 1) {
               sb.append(' ');
            }
            sb.append(rule.toString());
         }
         return sb.append(")").toString();
      }
      return "";
   }

   @Override
   public boolean isTerminal() {
      return false;
   }

   @Override
   public boolean isTest() {
      for (GrammarRule rule : rules) {
         if (! rule.isTest()) {
            return false;
         }
      }
      return true;
   }

   @Override
   public boolean canBeEmpty() {
      for (GrammarRule r : rules) {
         if (! r.canBeEmpty()) {
            return false;
         }
      }
      return true;
   }
}
