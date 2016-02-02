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
public interface GrammarRule {
   
   public void visit(ParserGenerationOptions options, GrammarRuleVisitor visitor);
   
   public boolean isTerminal();

   public boolean isTest();
   
   public boolean canBeEmpty();
}
