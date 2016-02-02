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
import org.uggeri.yapp.grammar.rules.NonTerminalRule;
import org.uggeri.yapp.grammar.rules.OneOrMoreRule;
import org.uggeri.yapp.grammar.rules.OptionalRule;
import org.uggeri.yapp.grammar.rules.OrRule;
import org.uggeri.yapp.grammar.rules.StringRule;
import org.uggeri.yapp.grammar.rules.TestNotRule;
import org.uggeri.yapp.grammar.rules.TestRule;
import org.uggeri.yapp.grammar.rules.ZeroOrMoreRule;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author fabio
 */
public class PartialLiteralsBreakerVisitor implements GrammarRuleVisitor {

   private boolean visiting = false;

   @Override
   public void visitNonTerminal(ParserGenerationOptions options, NonTerminalRule rule) {
      /* Teste para executar somente quando nao for uma chamada da propria classe */
      if (! visiting) {
         visiting = true;
         rule.getRule().visit(options, this);
         visiting = false;
      }
   }

   @Override
   public void visitAnd(ParserGenerationOptions options, AndRule rule) {
      final List<GrammarRule> rules = rule.getRules();
      for (int i = 0; i < rules.size(); i++) {
         final GrammarRule r = rules.get(i);
         if (r instanceof StringRule) {
            if (((StringRule)r).getPartialMatchLength() > 0) {
               final int minLen = ((StringRule)r).getPartialMatchLength();
               final int strLen = ((StringRule)r).getText().length();
               final List<GrammarRule> subRules = new ArrayList<GrammarRule>();
               for (int j = strLen; j >= minLen; j--) {
                  subRules.add(new StringRule(((StringRule)r).getText().substring(0, j)));
               }
               rules.set(i, new OrRule(subRules));
            }
         } else if (r instanceof IgnoreCaseStringRule) {
            if (((IgnoreCaseStringRule)r).getPartialMatchLength() > 0) {
               final int minLen = ((IgnoreCaseStringRule)r).getPartialMatchLength();
               final int strLen = ((IgnoreCaseStringRule)r).getText().length();
               final List<GrammarRule> subRules = new ArrayList<GrammarRule>();
               for (int j = strLen; j >= minLen; j--) {
                  subRules.add(new IgnoreCaseStringRule(((IgnoreCaseStringRule)r).getText().substring(0, j)));
               }
               rules.set(i, new OrRule(subRules));
            }
            
         } else {
            r.visit(options, this);
         }
      }
   }

   @Override
   public void visitOr(ParserGenerationOptions options, OrRule rule) {
      final List<GrammarRule> rules = rule.getRules();
      for (int i = rules.size() - 1; i >= 0; i--) {
         final GrammarRule r = rules.get(i);
         if (r instanceof StringRule) {
            if (((StringRule)r).getPartialMatchLength() > 0) {
               final int minLen = ((StringRule)r).getPartialMatchLength();
               final int strLen = ((StringRule)r).getText().length();
               rules.remove(i);
               for (int j = minLen; j <= strLen; j++) {
                  rules.add(i, new StringRule(((StringRule)r).getText().substring(0, j)));
               }
            }
         } else if (r instanceof IgnoreCaseStringRule) {
            if (((IgnoreCaseStringRule)r).getPartialMatchLength() > 0) {
               final int minLen = ((IgnoreCaseStringRule)r).getPartialMatchLength();
               final int strLen = ((IgnoreCaseStringRule)r).getText().length();
               rules.remove(i);
               for (int j = minLen; j <= strLen; j++) {
                  rules.add(i, new IgnoreCaseStringRule(((IgnoreCaseStringRule)r).getText().substring(0, j)));
               }
            }
         } else {
            r.visit(options, this);
         }
      }
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
