/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.generation.c.stmt;

import org.uggeri.yapp.generation.stmt.IndexedExpression;
import org.uggeri.yapp.generation.stmt.Expression;

/**
 *
 * @author fabio
 */
public class CIndexedExpression extends CExpression implements IndexedExpression {
   
   private final Expression expr;
   private final Expression[] indexes;

   public CIndexedExpression(Expression expr, Expression... indexes) {
      this.expr = expr;
      this.indexes = indexes;
   }

   @Override
   public Expression[] getIndexes() {
      return indexes;
   }

   @Override
   public Expression getArrayExpression() {
      return expr;
   }

   @Override
   public void appendCode(StringBuilder text, int indentation) {
      boolean first = true;
      indent(text, indentation);
      expr.appendCode(text, 0);
      text.append('[');
      for (Expression index : indexes) {
         if (first) {
            first = false;
         } else {
            text.append(", ");
         }
         index.appendCode(text, 0);
      }
      text.append(']');
   }; 

   @Override
   public boolean containsVariableReference(String varName) {
      if (expr.containsVariableReference(varName)) {
         return true;
      }
      for (Expression index : indexes) {
         if (index.containsVariableReference(varName)) {
            return true;
         }
      }
      return false;
   }   
}
