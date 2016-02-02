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
public class CharRangeRule extends TerminalRule {

   private final char start;

   private final char end;

   public CharRangeRule(char start, char end) {
      this.start = start;
      this.end = end;
   }

   @Override
   public void visit(ParserGenerationOptions options, GrammarRuleVisitor visitor) {
      visitor.visitCharRange(options, this);
   }

   public char getStart() {
      return start;
   }

   public char getEnd() {
      return end;
   }

   @Override
   public String getText() {
      return "";
   }

   @Override
   public String toString() {
      return "'" + start + "'-'" + end + "'";
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof CharRangeRule) {
         return ((CharRangeRule) obj).start == start && ((CharRangeRule) obj).end == end;
      }
      return false;
   }

   @Override
   public int hashCode() {
      int hash = 3;
      hash = 17 * hash + this.start;
      hash = 17 * hash + this.end;
      return hash;
   }

   @Override
   public int length() {
      return 1;
   }

   @Override
   public boolean canBeEmpty() {
      return false;
   }
}
