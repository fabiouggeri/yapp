/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.generation.visitors;

import org.uggeri.yapp.generation.ParserGenerationOptions;
import org.uggeri.yapp.grammar.GrammarRuleVisitor;
import org.uggeri.yapp.grammar.rules.AndRule;
import org.uggeri.yapp.grammar.rules.AnyCharRule;
import org.uggeri.yapp.grammar.rules.CharRangeRule;
import org.uggeri.yapp.grammar.rules.CharRule;
import org.uggeri.yapp.grammar.rules.EOIRule;
import org.uggeri.yapp.grammar.rules.EmptyRule;
import org.uggeri.yapp.grammar.rules.GrammarRule;
import org.uggeri.yapp.grammar.rules.IgnoreCaseCharRule;
import org.uggeri.yapp.grammar.rules.IgnoreCaseStringRule;
import org.uggeri.yapp.grammar.rules.NonTerminalOption;
import org.uggeri.yapp.grammar.rules.NonTerminalRule;
import org.uggeri.yapp.grammar.rules.OneOrMoreRule;
import org.uggeri.yapp.grammar.rules.OptionalRule;
import org.uggeri.yapp.grammar.rules.OrRule;
import org.uggeri.yapp.grammar.rules.StringRule;
import org.uggeri.yapp.grammar.rules.TestNotRule;
import org.uggeri.yapp.grammar.rules.TestRule;
import org.uggeri.yapp.grammar.rules.ZeroOrMoreRule;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author fabio_uggeri
 */
public class MemorizationRulesDefiner implements GrammarRuleVisitor {

   private boolean memorize = false;

   private final Map<NonTerminalRule, Integer> visitedRules = new HashMap<NonTerminalRule, Integer>();

   public void memorizeRules(ParserGenerationOptions options, NonTerminalRule rule) {
      this.memorize = false;
      visitedRules.clear();
      rule.visit(options, this);
      for (Entry<NonTerminalRule, Integer> entry : visitedRules.entrySet()) {
         if (entry.getValue() > 1 && ! entry.getKey().getOptions().containsKey(NonTerminalOption.MEMOIZE)) {
            entry.getKey().addOption(NonTerminalOption.MEMOIZE);
         }
      }
   }

   @Override
   public void visitNonTerminal(ParserGenerationOptions options, NonTerminalRule rule) {
      Integer count = visitedRules.get(rule);
      if (count == null) {
         visitedRules.put(rule, 1);
         rule.removeOption(NonTerminalOption.MEMOIZE);
         if (memorize && !rule.getOptions().containsKey(NonTerminalOption.MEMOIZE)) {
            rule.addOption(NonTerminalOption.MEMOIZE);
         }
         rule.getRule().visit(options, this);
      } else {
         visitedRules.put(rule, count + 1);
      }
   }

   @Override
   public void visitAnd(ParserGenerationOptions options, AndRule rule) {
      boolean oldMemorize = memorize;
      memorize = false;
      for (GrammarRule r : rule.getRules()) {
         r.visit(options, this);
      }
      memorize = oldMemorize;
   }

   @Override
   public void visitOr(ParserGenerationOptions options, OrRule rule) {
      boolean oldMemorize = memorize;
      memorize = true;
      for (GrammarRule r : rule.getRules()) {
         r.visit(options, this);
      }
      memorize = oldMemorize;
   }

   @Override
   public void visitChar(ParserGenerationOptions options, CharRule rule) {
   }

   @Override
   public void visitCharIgnoreCase(ParserGenerationOptions options, IgnoreCaseCharRule rule) {
   }

   @Override
   public void visitCharRange(ParserGenerationOptions options, CharRangeRule rule) {
   }

   @Override
   public void visitAnyChar(ParserGenerationOptions options, AnyCharRule rule) {
   }

   @Override
   public void visitString(ParserGenerationOptions options, StringRule rule) {
   }

   @Override
   public void visitStringIgnoreCase(ParserGenerationOptions options, IgnoreCaseStringRule rule) {
   }

   @Override
   public void visitZeroOrMore(ParserGenerationOptions options, ZeroOrMoreRule rule) {
      rule.getRule().visit(options, this);
   }

   @Override
   public void visitOneOrMore(ParserGenerationOptions options, OneOrMoreRule rule) {
      rule.getRule().visit(options, this);
   }

   @Override
   public void visitTest(ParserGenerationOptions options, TestRule rule) {
      rule.getRule().visit(options, this);
   }

   @Override
   public void visitTestNot(ParserGenerationOptions options, TestNotRule rule) {
      rule.getRule().visit(options, this);
   }

   @Override
   public void visitOptional(ParserGenerationOptions options, OptionalRule rule) {
      rule.getRule().visit(options, this);
   }

   @Override
   public void visitEmpty(ParserGenerationOptions options, EmptyRule rule) {
   }

   @Override
   public void visitEOI(ParserGenerationOptions options, EOIRule rule) {
   }

}
