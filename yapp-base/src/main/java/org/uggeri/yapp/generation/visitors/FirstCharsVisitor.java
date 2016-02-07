/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.generation.visitors;

import java.util.ArrayList;
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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.uggeri.yapp.grammar.rules.TerminalRule;

/**
 *
 * @author fabio
 */
public class FirstCharsVisitor implements GrammarRuleVisitor {

   final private Set<TerminalRule> rulesSet = new HashSet<TerminalRule>();

   private boolean optional = false;

   private boolean redundantRemoved = false;

   public TerminalRule[] getFirstLiterals() {
      final TerminalRule[] result;
      int index = 0;
      removeRedundantRules();
      result = new TerminalRule[rulesSet.size()];
      for (TerminalRule t : rulesSet) {
         result[index++] = t;
      }
      return result;
   }

   public Set<Character> getCharSet() {
      final Set<Character> charSet = new HashSet<Character>();
      removeRedundantRules();
      for (TerminalRule r : rulesSet) {
         if (r instanceof CharRule) {
            charSet.add(((CharRule) r).getCharacter());
         } else if (r instanceof IgnoreCaseCharRule) {
            charSet.add(Character.toLowerCase(((IgnoreCaseCharRule) r).getCharacter()));
            charSet.add(Character.toLowerCase(((IgnoreCaseCharRule) r).getCharacter()));
         } else if (r instanceof CharRangeRule) {
            for (char c = ((CharRangeRule) r).getStart(); c <= ((CharRangeRule) r).getEnd(); c++) {
               charSet.add(c);
            }
         }
      }
      return charSet;
   }

   private void removeRedundantRules() {
      if (!redundantRemoved) {
         final List<TerminalRule> rules = new ArrayList<TerminalRule>(rulesSet.size());
         final List<CharRangeRule> charRangeRules = new ArrayList<CharRangeRule>();
         redundantRemoved = true;
         for (TerminalRule sourceRule : rulesSet) {
            boolean addRule = true;
            for (TerminalRule targetRule : rules) {
               if (targetRule != sourceRule && ruleMatch(targetRule, sourceRule)) {
                  addRule = false;
                  break;
               }
            }
            if (addRule) {
               if (sourceRule instanceof CharRangeRule) {
                  charRangeRules.add((CharRangeRule) sourceRule);
               }
               rules.add(sourceRule);
            }
         }
         Iterator<TerminalRule> it = rules.iterator();
         while (it.hasNext()) {
            TerminalRule rule = it.next();
            if (hasCharRangeMatch(rule, charRangeRules)) {
               it.remove();
            }
         }
         rulesSet.clear();
         rulesSet.addAll(rules);
      }
   }

   private boolean hasCharRangeMatch(TerminalRule rule, List<CharRangeRule> charRangeRules) {
      if (rule instanceof IgnoreCaseCharRule) {
         boolean found = false;
         char lowerChar = Character.toLowerCase(((IgnoreCaseCharRule) rule).getCharacter());
         char upperChar = Character.toUpperCase(((IgnoreCaseCharRule) rule).getCharacter());
         for (CharRangeRule rangeRule : charRangeRules) {
            if (lowerChar >= rangeRule.getStart() && lowerChar <= rangeRule.getEnd()) {
               found = true;
               break;
            }
         }
         if (found) {
            for (CharRangeRule rangeRule : charRangeRules) {
               if (upperChar >= rangeRule.getStart() && upperChar <= rangeRule.getEnd()) {
                  return true;
               }
            }
         }
      } else if (rule instanceof CharRule) {
         for (CharRangeRule rangeRule : charRangeRules) {
            if (((CharRule) rule).getCharacter() >= rangeRule.getStart()
               && ((CharRule) rule).getCharacter() <= rangeRule.getEnd()) {
               return true;
            }
         }
      }
      return false;
   }

   private boolean ruleMatch(TerminalRule targetRule, TerminalRule sourceRule) {
      if (targetRule instanceof CharRangeRule) {
         if (sourceRule instanceof CharRangeRule) {
            return ((CharRangeRule) sourceRule).getStart() >= ((CharRangeRule) targetRule).getStart() && ((CharRangeRule) sourceRule).getStart() <= ((CharRangeRule) targetRule).getStart();
         } else if (sourceRule instanceof IgnoreCaseCharRule) {
            return Character.toLowerCase(((IgnoreCaseCharRule) sourceRule).getCharacter()) >= ((CharRangeRule) targetRule).getStart() && Character.toLowerCase(((IgnoreCaseCharRule) sourceRule).getCharacter()) <= ((CharRangeRule) targetRule).getEnd()
               && Character.toUpperCase(((IgnoreCaseCharRule) sourceRule).getCharacter()) >= ((CharRangeRule) targetRule).getStart() && Character.toUpperCase(((IgnoreCaseCharRule) sourceRule).getCharacter()) <= ((CharRangeRule) targetRule).getEnd();
         } else if (sourceRule instanceof CharRule) {
            return ((CharRule) sourceRule).getCharacter() >= ((CharRangeRule) targetRule).getStart() && ((CharRule) sourceRule).getCharacter() <= ((CharRangeRule) targetRule).getEnd();
         }
      } else if (targetRule instanceof IgnoreCaseCharRule) {
         if (sourceRule instanceof IgnoreCaseCharRule) {
            return Character.toLowerCase(((IgnoreCaseCharRule) targetRule).getCharacter()) == Character.toLowerCase(((IgnoreCaseCharRule) sourceRule).getCharacter());
         } else if (sourceRule instanceof CharRule) {
            return Character.toLowerCase(((IgnoreCaseCharRule) targetRule).getCharacter()) == ((CharRule) sourceRule).getCharacter()
               || Character.toUpperCase(((IgnoreCaseCharRule) targetRule).getCharacter()) == ((CharRule) sourceRule).getCharacter();
         }
      } else if (targetRule instanceof CharRule) {
         if (sourceRule instanceof CharRule) {
            return ((CharRule) targetRule).getCharacter() == ((CharRule) sourceRule).getCharacter();
         }
      }
      return false;
   }

   @Override
   public void visitNonTerminal(ParserGenerationOptions options, NonTerminalRule rule) {
      rule.getRule().visit(options, this);
   }

   @Override
   public void visitAnd(ParserGenerationOptions options, AndRule rule) {
      boolean oldOptional = optional;
      int index = 0;
      do {
         optional = false;
         rule.getRules().get(index++).visit(options, this);
      } while (optional && index < rule.getRules().size());
      optional = oldOptional;
   }

   @Override
   public void visitOr(ParserGenerationOptions options, OrRule rule) {
      for (GrammarRule child : rule.getRules()) {
         child.visit(options, this);
      }
   }

   @Override
   public void visitChar(ParserGenerationOptions options, CharRule rule) {
      rulesSet.add(rule);
   }

   @Override
   public void visitCharIgnoreCase(ParserGenerationOptions options, IgnoreCaseCharRule rule) {
      rulesSet.add(rule);
   }

   @Override
   public void visitCharRange(ParserGenerationOptions options, CharRangeRule rule) {
      rulesSet.add(rule);
   }

   @Override
   public void visitAnyChar(ParserGenerationOptions options, AnyCharRule rule) {
   }

   @Override
   public void visitString(ParserGenerationOptions options, StringRule rule) {
      rulesSet.add(new CharRule(rule.getText().charAt(0)));
   }

   @Override
   public void visitStringIgnoreCase(ParserGenerationOptions options, IgnoreCaseStringRule rule) {
      rulesSet.add(new IgnoreCaseCharRule(rule.getText().charAt(0)));
   }

   @Override
   public void visitZeroOrMore(ParserGenerationOptions options, ZeroOrMoreRule rule) {
      optional = true;
      rule.getRule().visit(options, this);
   }

   @Override
   public void visitOneOrMore(ParserGenerationOptions options, OneOrMoreRule rule) {
      rule.getRule().visit(options, this);
   }

   @Override
   public void visitTest(ParserGenerationOptions options, TestRule rule) {
      optional = true;
   }

   @Override
   public void visitTestNot(ParserGenerationOptions options, TestNotRule rule) {
      optional = true;
   }

   @Override
   public void visitOptional(ParserGenerationOptions options, OptionalRule rule) {
      optional = true;
      rule.getRule().visit(options, this);
   }

   @Override
   public void visitEmpty(ParserGenerationOptions options, EmptyRule rule) {
   }

   @Override
   public void visitEOI(ParserGenerationOptions options, EOIRule rule) {
   }
}
