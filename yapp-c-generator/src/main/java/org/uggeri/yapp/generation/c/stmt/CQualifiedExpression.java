/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.generation.c.stmt;

import org.uggeri.yapp.generation.stmt.Expression;
import org.uggeri.yapp.generation.stmt.QualifiedExpression;

/**
 *
 * @author fabio
 */
public class CQualifiedExpression extends CExpression implements QualifiedExpression {
   
   private final Expression leftExpression;
   
   private final Expression rightExpression;

   public CQualifiedExpression(Expression leftExpression, Expression rightExpression) {
      this.leftExpression = leftExpression;
      this.rightExpression = rightExpression;
   }

   @Override
   public void appendCode(StringBuilder text, int indentation) {
      indent(text, indentation);
      leftExpression.appendCode(text, 0);
      text.append("->");
      rightExpression.appendCode(text, 0);
   }

   @Override
   public boolean containsVariableReference(String varName) {
      return leftExpression.containsVariableReference(varName) || rightExpression.containsVariableReference(varName);
   }
}
