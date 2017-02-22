/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.generation.c.stmt;

import org.uggeri.yapp.generation.stmt.Expression;
import org.uggeri.yapp.generation.stmt.SingleOperandExpression;

/**
 *
 * @author fabio
 */
public class CSingleOperandExpression extends CExpression implements SingleOperandExpression {
   
   private final Operator operator;

   private final Expression expression;

   public CSingleOperandExpression(Operator operator, Expression expression) {
      this.operator = operator;
      this.expression = expression;
   }
   
   @Override
   public Operator getOperator() {
      return operator;
   }

   @Override
   public Expression getExpression() {
      return expression;
   }
   
   @Override
   public void appendCode(StringBuilder text, int indentation) {
      indent(text, indentation);
      switch(operator) {
         case NEGATE:
            text.append("! ");
            break;
         case PRE_INC:
            text.append("++");
            break;
         case PRE_DEC:
            text.append("--");
            break;
      }
      getExpression().appendCode(text, 0);
      switch(operator) {
         case POS_INC:
            text.append("++");
            break;
         case POS_DEC:
            text.append("--");
            break;
      }
   }

   @Override
   public boolean containsVariableReference(String varName) {
      return getExpression().containsVariableReference(varName);
   }
}
