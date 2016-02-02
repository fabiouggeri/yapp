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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Fabio
 */
public class LeftRecursionVerificationVisitor implements GrammarRuleVisitor {
      
   private final LinkedList<GrammarRule> path = new LinkedList<GrammarRule>();
   private boolean leftRecursion = false;
   private boolean optional;
   private final Set<NonTerminalRule> visitedRules = new HashSet<NonTerminalRule>();
   private final NonTerminalRule firstRule;

   public LeftRecursionVerificationVisitor(NonTerminalRule firstRule) {
      this.firstRule = firstRule;
   }
   
   public List<GrammarRule> getLeftRecursionPath() {
      return path;
   }
   
   @Override
   public void visitNonTerminal(ParserGenerationOptions options, NonTerminalRule rule) {
      if (visitedRules.contains(rule)) {
         leftRecursion = true;
         path.addFirst(rule);
      } else {
         visitedRules.add(rule);
         rule.getRule().visit(options, this);
         if (leftRecursion) {
            if (firstRule != rule) {
               path.addFirst(rule);
            }
         } else {
            visitedRules.remove(rule);
         }
      }
   }

   @Override
   public void visitAnd(ParserGenerationOptions options, AndRule rule) {
      for(GrammarRule subRule : rule.getRules()) {
         optional = false;
         subRule.visit(options, this);
         if (! optional || leftRecursion) {
            break;
         }
      }
   }

   @Override
   public void visitOr(ParserGenerationOptions options, OrRule rule) {
      for(GrammarRule subRule : rule.getRules()) {
         subRule.visit(options, this);
         if (leftRecursion) {
            break;
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
      optional = true;
   }

   @Override
   public void visitOneOrMore(ParserGenerationOptions options, OneOrMoreRule rule) {
      rule.getRule().visit(options, this);
   }

   @Override
   public void visitTest(ParserGenerationOptions options, TestRule rule) {
      rule.getRule().visit(options, this);
      optional = true;
   }

   @Override
   public void visitTestNot(ParserGenerationOptions options, TestNotRule rule) {
      rule.getRule().visit(options, this);
      optional = true;
   }

   @Override
   public void visitOptional(ParserGenerationOptions options, OptionalRule rule) {
      rule.getRule().visit(options, this);
      optional = true;
   }

   @Override
   public void visitEmpty(ParserGenerationOptions options, EmptyRule rule) {
   }

   @Override
   public void visitEOI(ParserGenerationOptions options, EOIRule rule) {
   }
   
}
