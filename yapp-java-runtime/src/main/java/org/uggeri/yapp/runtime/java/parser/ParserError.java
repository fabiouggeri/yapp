/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.runtime.java.parser;

import org.uggeri.yapp.runtime.java.util.NodeLink;

/**
 *
 * @author fabio
 */
public class ParserError {

   private final int index;
   
   private Rule ruleExpected = null;
   
   private String textExpected = "";
   
   private final NodeLink<Rule> ruleNode;

   public ParserError(int index, Rule rule, NodeLink<Rule> ruleNode) {
      this.index = index;
      this.ruleExpected = rule;
      this.ruleNode = ruleNode;
   }
   
   public ParserError(int index, String expected, NodeLink<Rule> ruleNode) {
      this.index = index;
      this.textExpected = expected;
      this.ruleNode = ruleNode;
   }

   public int getIndex() {
      return index;
   }

   public Rule getRuleExpected() {
      return ruleExpected;
   }

   public String getTextExpected() {
      return textExpected;
   }

   @Override
   public String toString() {
      if (ruleExpected != null) {
         return ruleExpected.toString() + " at " + index;
      } else {
         return textExpected + " at " + index;
      }
   }

   public NodeLink<Rule> getRuleNode() {
      return ruleNode;
   }
   
}
