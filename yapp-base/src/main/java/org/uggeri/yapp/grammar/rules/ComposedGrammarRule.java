/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.grammar.rules;

import java.util.List;

/**
 *
 * @author Fabio
 */
public interface ComposedGrammarRule {
   
   public List<GrammarRule> getRules();
   
   public void setRules(List<GrammarRule> rules);
}
