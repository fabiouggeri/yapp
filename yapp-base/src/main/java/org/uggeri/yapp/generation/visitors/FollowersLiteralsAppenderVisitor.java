/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.generation.visitors;

import org.uggeri.yapp.generation.ParserGenerationOptions;
import org.uggeri.yapp.grammar.GrammarRuleVisitor;
import org.uggeri.yapp.grammar.rules.AndRule;
import org.uggeri.yapp.grammar.rules.AnyCharRule;
import org.uggeri.yapp.grammar.rules.CharRangeRule;
import org.uggeri.yapp.grammar.rules.CharRule;
import org.uggeri.yapp.grammar.rules.ComposedGrammarRule;
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
import org.uggeri.yapp.grammar.rules.SimpleGrammarRule;
import org.uggeri.yapp.grammar.rules.StringRule;
import org.uggeri.yapp.grammar.rules.TerminalRule;
import org.uggeri.yapp.grammar.rules.TestNotRule;
import org.uggeri.yapp.grammar.rules.TestRule;
import org.uggeri.yapp.grammar.rules.ZeroOrMoreRule;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author fabio
 */
public class FollowersLiteralsAppenderVisitor implements GrammarRuleVisitor {

   private final List<NonTerminalRule> followLiterals;
   
   private boolean visiting = false;
   
   private GrammarRule replacementRule = null;

   public FollowersLiteralsAppenderVisitor(List<NonTerminalRule> followLiterals) {
      this.followLiterals = followLiterals;
   }

   @Override
   public void visitNonTerminal(ParserGenerationOptions options, NonTerminalRule rule) {
      /* Teste para executar somente quando nao for uma chamada da propria classe */
      if (! visiting) {
         visiting = true;
         if (!rule.getOptions().containsKey(NonTerminalOption.ATOMIC)
                 && !rule.getOptions().containsKey(NonTerminalOption.NOT_EXTEND_LITERALS)
                 && !rule.getOptions().containsKey(NonTerminalOption.FOLLOW_LITERALS)
                 && !rule.getOptions().containsKey(NonTerminalOption.FRAGMENT)) {
            if (rule.getRule().isTerminal()) {
               List<GrammarRule> andRules = new ArrayList<GrammarRule>(followLiterals.size() + 1);
               andRules.add(rule.getRule());
               for (NonTerminalRule followRule : followLiterals) {
                  if (FollowersLiteralsAppenderVisitor.terminalMatchPattern(followRule, rule.getRule())) {
                     andRules.add(followRule);
                  }
               }
               /* Modifica somente se adicionou ao menos uma regra*/
               if (andRules.size() > 1) {
                  rule.setRule(new AndRule(andRules));
               }
            } else {
               visitSubrule(rule, options);
            }
         }
         visiting = false;
      }
   }

   @Override
   public void visitAnd(ParserGenerationOptions options, AndRule rule) {
      int i = 0;
      while (i < rule.getRules().size()) {
         GrammarRule subrule = rule.getRules().get(i);
         if (subrule.isTerminal()) {
            for (NonTerminalRule followRule : followLiterals) {
               if (terminalMatchPattern(followRule, subrule)) {
                  rule.getRules().add(++i, followRule);
               }
            }
         } else {
            visitSubrule(rule, i, options);
         }
         ++i;
      }
   }

   @Override
   public void visitOr(ParserGenerationOptions options, OrRule rule) {
      List<RuleFollowers> followers = new ArrayList<RuleFollowers>();
      for (int i = 0; i < rule.getRules().size(); i++) {
         GrammarRule subrule = rule.getRules().get(i);
         if (subrule.isTerminal()) {
            RuleFollowers ruleFollowers = new RuleFollowers(i, subrule);
            followers.add(ruleFollowers);
            for (NonTerminalRule followRule : followLiterals) {
               if (terminalMatchPattern(followRule, subrule)) {
                  ruleFollowers.followers.add(followRule);
               }
            }
            /* Se nao foi adicionada nenhuma regra, entao remove da lista */
            if (ruleFollowers.followers.isEmpty()) {
               followers.remove(followers.size() - 1);
            }
         }         
      }
      /* Se todas as regras tem as mesmas seguidoras, entao cria uma nova regra no formato (op1|op2|op3|...)follow1 follow2...*/
      if (allRulesHaveSameFollowers(rule.getRules(), followers)) {
         final List<GrammarRule> subrules = new ArrayList<GrammarRule>();
         subrules.add(rule);
         subrules.addAll(followers.get(0).followers);
         replacementRule = new AndRule(subrules);
      } else {
         for (int i = 0; i < rule.getRules().size(); i++) {
            GrammarRule subrule = rule.getRules().get(i);
            if (subrule.isTerminal()) {
               List<GrammarRule> andRules = new ArrayList<GrammarRule>(followLiterals.size() + 1);
               andRules.add(subrule);
               for (NonTerminalRule followRule : followLiterals) {
                  if (terminalMatchPattern(followRule, subrule)) {
                     andRules.add(followRule);
                  }
               }
               /* Modifica somente se adicionou ao menos uma regra*/
               if (andRules.size() > 1) {
                  rule.getRules().set(i, new AndRule(andRules));
               }
            } else {
               visitSubrule(rule, i, options);
            }
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
      if (rule.getRule().isTerminal()) {
         List<GrammarRule> andRules = new ArrayList<GrammarRule>(followLiterals.size() + 1);
         andRules.add(rule.getRule());
         for (NonTerminalRule followRule : followLiterals) {
            if (terminalMatchPattern(followRule, rule.getRule())) {
               andRules.add(followRule);
            }
         }
         /* Modifica somente se adicionou ao menos uma regra*/
         if (andRules.size() > 1) {
            rule.setRule(new AndRule(andRules));
         }
      } else {
         visitSubrule(rule, options);
      }
   }

   @Override
   public void visitOneOrMore(ParserGenerationOptions options, OneOrMoreRule rule) {
      if (rule.getRule().isTerminal()) {
         List<GrammarRule> andRules = new ArrayList<GrammarRule>(followLiterals.size() + 1);
         andRules.add(rule.getRule());
         for (NonTerminalRule followRule : followLiterals) {
            if (terminalMatchPattern(followRule, rule.getRule())) {
               andRules.add(followRule);
            }
         }
         /* Modifica somente se adicionou ao menos uma regra*/
         if (andRules.size() > 1) {
            rule.setRule(new AndRule(andRules));
         }
      } else {
         visitSubrule(rule, options);
      }
   }

   @Override
   public void visitTest(ParserGenerationOptions options, TestRule rule) {
      visitSubrule(rule, options);
   }

   @Override
   public void visitTestNot(ParserGenerationOptions options, TestNotRule rule) {
      visitSubrule(rule, options);
   }

   @Override
   public void visitOptional(ParserGenerationOptions options, OptionalRule rule) {
      if (rule.getRule().isTerminal()) {
         List<GrammarRule> andRules = new ArrayList<GrammarRule>(followLiterals.size() + 1);
         andRules.add(rule.getRule());
         for (NonTerminalRule followRule : followLiterals) {
            if (terminalMatchPattern(followRule, rule.getRule())) {
               andRules.add(followRule);
            }
         }
         /* Modifica somente se adicionou ao menos uma regra*/
         if (andRules.size() > 1) {
            rule.setRule(new AndRule(andRules));
         }
      } else {
         visitSubrule(rule, options);
      }
   }

   @Override
   public void visitEmpty(ParserGenerationOptions options, EmptyRule rule) {
   }

   @Override
   public void visitEOI(ParserGenerationOptions options, EOIRule rule) {
   }

   public static boolean terminalMatchPattern(NonTerminalRule followRule, GrammarRule terminalRule) {
      if (followRule.getPatternToFollow() != null) {
         if (followRule.isFollowIfMatch()) {
            return followRule.getPatternToFollow().matcher(((TerminalRule)terminalRule).getText()).matches();
         } else {
            return ! followRule.getPatternToFollow().matcher(((TerminalRule)terminalRule).getText()).matches();
         }
      }
      return true;
   }

   private boolean allRulesHaveSameFollowers(List<GrammarRule> rules, List<RuleFollowers> followers) {
      if (followers.size() > 0 && followers.size() == rules.size()) {
         List<NonTerminalRule> firstRulefollowers = followers.get(0).followers;
         for (int i = 1; i < followers.size(); i++) {
            List<NonTerminalRule> currentFollowers = followers.get(i).followers;
            if (! firstRulefollowers.equals(currentFollowers)) {
               return false;
            }
         }
         return true;
      }
      return false;
   }

   private void visitSubrule(ComposedGrammarRule rule, int index, ParserGenerationOptions options) {
      rule.getRules().get(index).visit(options, this);
      if (replacementRule != null) {
         rule.getRules().set(index, replacementRule);
         replacementRule = null;
      }
   }

   private void visitSubrule(SimpleGrammarRule rule, ParserGenerationOptions options) {
      rule.getRule().visit(options, this);
      if (replacementRule != null) {
         rule.setRule(replacementRule);
         replacementRule = null;
      }
   }
   
   private class RuleFollowers {
      
      final int index;
      
      final GrammarRule rule;
      
      List<NonTerminalRule> followers = new ArrayList<NonTerminalRule>();

      public RuleFollowers(int index, GrammarRule rule) {
         this.index = index;
         this.rule = rule;
      }
   }
}
