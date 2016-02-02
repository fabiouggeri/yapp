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
import java.util.Set;

/**
 *
 * @author fabio
 */
public class FirstCharsVisitor implements GrammarRuleVisitor {

   final Set<Character> charSet = new HashSet<Character>();
   final Set<NonTerminalRule> visited = new HashSet<NonTerminalRule>();

   @Override
   public void visitNonTerminal(ParserGenerationOptions options, NonTerminalRule rule) {
      if (!visited.contains(rule)) {
         visited.add(rule);
         rule.visit(options, this);
      }
   }

   @Override
   public void visitAnd(ParserGenerationOptions options, AndRule rule) {
      for (GrammarRule r : rule.getRules()) {
         r.visit(options, this);
         if (!r.canBeEmpty()) {
            break;
         }
      }
   }

   @Override
   public void visitOr(ParserGenerationOptions options, OrRule rule) {
      for (GrammarRule r : rule.getRules()) {
         r.visit(options, this);
      }
   }

   @Override
   public void visitChar(ParserGenerationOptions options, CharRule rule) {
      charSet.add(rule.getCharacter());
   }

   @Override
   public void visitCharIgnoreCase(ParserGenerationOptions options, IgnoreCaseCharRule rule) {
      charSet.add(Character.toLowerCase(rule.getCharacter()));
      charSet.add(Character.toUpperCase(rule.getCharacter()));
   }

   @Override
   public void visitCharRange(ParserGenerationOptions options, CharRangeRule rule) {
      for (char c = rule.getStart(); c <= rule.getEnd(); c++) {
         charSet.add(c);
      }
   }

   @Override
   public void visitAnyChar(ParserGenerationOptions options, AnyCharRule rule) {
      for (char c = Character.MIN_VALUE; c <= Character.MAX_VALUE; c++) {
         charSet.add(c);
      }
   }

   @Override
   public void visitString(ParserGenerationOptions options, StringRule rule) {
      charSet.add(rule.getText().charAt(0));
   }

   @Override
   public void visitStringIgnoreCase(ParserGenerationOptions options, IgnoreCaseStringRule rule) {
      charSet.add(Character.toLowerCase(rule.getText().charAt(0)));
      charSet.add(Character.toUpperCase(rule.getText().charAt(0)));
   }

   @Override
   public void visitZeroOrMore(ParserGenerationOptions options, ZeroOrMoreRule rule) {
      rule.visit(options, this);
   }

   @Override
   public void visitOneOrMore(ParserGenerationOptions options, OneOrMoreRule rule) {
      rule.visit(options, this);
   }

   @Override
   public void visitTest(ParserGenerationOptions options, TestRule rule) {
   }

   @Override
   public void visitTestNot(ParserGenerationOptions options, TestNotRule rule) {
   }

   @Override
   public void visitOptional(ParserGenerationOptions options, OptionalRule rule) {
      rule.visit(options, this);
   }

   @Override
   public void visitEmpty(ParserGenerationOptions options, EmptyRule rule) {
   }

   @Override
   public void visitEOI(ParserGenerationOptions options, EOIRule rule) {
   }

   public Set<Character> getCharSet() {
      return charSet;
   }

}
