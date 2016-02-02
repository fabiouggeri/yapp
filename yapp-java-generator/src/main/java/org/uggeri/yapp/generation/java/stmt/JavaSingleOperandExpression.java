/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.generation.java.stmt;

import org.uggeri.yapp.generation.stmt.Expression;
import org.uggeri.yapp.generation.stmt.SingleOperandExpression;
import static org.uggeri.yapp.generation.stmt.SingleOperandExpression.Operator.NEGATE;
import static org.uggeri.yapp.generation.stmt.SingleOperandExpression.Operator.POS_DEC;
import static org.uggeri.yapp.generation.stmt.SingleOperandExpression.Operator.POS_INC;
import static org.uggeri.yapp.generation.stmt.SingleOperandExpression.Operator.PRE_DEC;
import static org.uggeri.yapp.generation.stmt.SingleOperandExpression.Operator.PRE_INC;

/**
 *
 * @author fabio
 */
public class JavaSingleOperandExpression extends JavaExpression implements SingleOperandExpression {
   
   private Operator operator;

   private Expression expression;

   public JavaSingleOperandExpression(Operator operator, Expression expression) {
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
