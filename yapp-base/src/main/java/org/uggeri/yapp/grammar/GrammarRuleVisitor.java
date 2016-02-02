/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.grammar;

import org.uggeri.yapp.generation.ParserGenerationOptions;
import org.uggeri.yapp.grammar.rules.NonTerminalRule;
import org.uggeri.yapp.grammar.rules.AnyCharRule;
import org.uggeri.yapp.grammar.rules.CharRangeRule;
import org.uggeri.yapp.grammar.rules.OneOrMoreRule;
import org.uggeri.yapp.grammar.rules.IgnoreCaseStringRule;
import org.uggeri.yapp.grammar.rules.StringRule;
import org.uggeri.yapp.grammar.rules.TestNotRule;
import org.uggeri.yapp.grammar.rules.OrRule;
import org.uggeri.yapp.grammar.rules.ZeroOrMoreRule;
import org.uggeri.yapp.grammar.rules.IgnoreCaseCharRule;
import org.uggeri.yapp.grammar.rules.TestRule;
import org.uggeri.yapp.grammar.rules.CharRule;
import org.uggeri.yapp.grammar.rules.AndRule;
import org.uggeri.yapp.grammar.rules.EOIRule;
import org.uggeri.yapp.grammar.rules.EmptyRule;
import org.uggeri.yapp.grammar.rules.OptionalRule;

/**
 *
 * @author fabio
 */
public interface GrammarRuleVisitor {
 
   public void visitNonTerminal(ParserGenerationOptions options, NonTerminalRule rule);
   public void visitAnd(ParserGenerationOptions options, AndRule rule);
   public void visitOr(ParserGenerationOptions options, OrRule rule);
   public void visitChar(ParserGenerationOptions options, CharRule rule);
   public void visitCharIgnoreCase(ParserGenerationOptions options, IgnoreCaseCharRule rule);
   public void visitCharRange(ParserGenerationOptions options, CharRangeRule rule);
   public void visitAnyChar(ParserGenerationOptions options, AnyCharRule rule);
   public void visitString(ParserGenerationOptions options, StringRule rule);
   public void visitStringIgnoreCase(ParserGenerationOptions options, IgnoreCaseStringRule rule);
   public void visitZeroOrMore(ParserGenerationOptions options, ZeroOrMoreRule rule);
   public void visitOneOrMore(ParserGenerationOptions options, OneOrMoreRule rule);
   public void visitTest(ParserGenerationOptions options, TestRule rule);
   public void visitTestNot(ParserGenerationOptions options, TestNotRule rule);
   public void visitOptional(ParserGenerationOptions options, OptionalRule rule);
   public void visitEmpty(ParserGenerationOptions options, EmptyRule rule);
   public void visitEOI(ParserGenerationOptions options, EOIRule rule);

}
