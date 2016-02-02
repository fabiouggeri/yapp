/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.generation.java.stmt;

import org.uggeri.yapp.generation.stmt.DoubleOperandExpression;
import static org.uggeri.yapp.generation.stmt.DoubleOperandExpression.Operator.AND;
import static org.uggeri.yapp.generation.stmt.DoubleOperandExpression.Operator.ASSIGN;
import static org.uggeri.yapp.generation.stmt.DoubleOperandExpression.Operator.DIFFERENT;
import static org.uggeri.yapp.generation.stmt.DoubleOperandExpression.Operator.DIV;
import static org.uggeri.yapp.generation.stmt.DoubleOperandExpression.Operator.DIV_EQUAL;
import static org.uggeri.yapp.generation.stmt.DoubleOperandExpression.Operator.EQUAL;
import static org.uggeri.yapp.generation.stmt.DoubleOperandExpression.Operator.GREATER;
import static org.uggeri.yapp.generation.stmt.DoubleOperandExpression.Operator.GREATER_OR_EQUAL;
import static org.uggeri.yapp.generation.stmt.DoubleOperandExpression.Operator.LESS;
import static org.uggeri.yapp.generation.stmt.DoubleOperandExpression.Operator.LESS_OR_EQUAL;
import static org.uggeri.yapp.generation.stmt.DoubleOperandExpression.Operator.MINUS;
import static org.uggeri.yapp.generation.stmt.DoubleOperandExpression.Operator.MINUS_EQUAL;
import static org.uggeri.yapp.generation.stmt.DoubleOperandExpression.Operator.MOD;
import static org.uggeri.yapp.generation.stmt.DoubleOperandExpression.Operator.MOD_EQUAL;
import static org.uggeri.yapp.generation.stmt.DoubleOperandExpression.Operator.MULT;
import static org.uggeri.yapp.generation.stmt.DoubleOperandExpression.Operator.MULT_EQUAL;
import static org.uggeri.yapp.generation.stmt.DoubleOperandExpression.Operator.OR;
import static org.uggeri.yapp.generation.stmt.DoubleOperandExpression.Operator.PLUS;
import static org.uggeri.yapp.generation.stmt.DoubleOperandExpression.Operator.PLUS_EQUAL;
import org.uggeri.yapp.generation.stmt.Expression;

/**
 *
 * @author fabio
 */
public class JavaDoubleExpressionOperator extends JavaExpression implements DoubleOperandExpression {
 
   private Expression leftExpression;
   
   private Operator operator;
   
   private Expression rightExpression;

   public JavaDoubleExpressionOperator(Expression leftExpression, Operator operator, Expression rightExpression) {
      this.leftExpression = leftExpression;
      this.operator = operator;
      this.rightExpression = rightExpression;
   }

   @Override
   public void appendCode(StringBuilder text, int indentation) {
      indent(text, indentation);
      leftExpression.appendCode(text, 0);
      switch(operator) {
         case ASSIGN:
            text.append(" = ");
            break;
         case EQUAL:
            text.append(" == ");
            break;
         case GREATER:
            text.append(" > ");
            break;
         case GREATER_OR_EQUAL:
            text.append(" >= ");
            break;
         case LESS:
            text.append(" < ");
            break;
         case LESS_OR_EQUAL:
            text.append(" <= ");
            break;
         case DIFFERENT:
            text.append(" != ");
            break;
         case MINUS:
            text.append(" - ");
            break;
         case PLUS:
            text.append(" + ");
            break;
         case DIV:
            text.append(" / ");
            break;
         case MOD:
            text.append(" % ");
            break;
         case MULT:
            text.append(" * ");
            break;
         case MINUS_EQUAL:
            text.append(" -= ");
            break;
         case PLUS_EQUAL:
            text.append(" += ");
            break;
         case DIV_EQUAL:
            text.append(" /= ");
            break;
         case MOD_EQUAL:
            text.append(" %= ");
            break;
         case MULT_EQUAL:
            text.append(" *= ");
            break;
         case AND:
            text.append(" && ");
            break;
         case OR:
            text.append(" || ");
            break;
      }
      rightExpression.appendCode(text, 0);
   }

   @Override
   public Expression getLeftExpression() {
      return leftExpression;
   }

   @Override
   public Operator getOperator() {
      return operator;
   }

   @Override
   public Expression getRightExpression() {
      return rightExpression;
   }

   @Override
   public boolean containsVariableReference(String varName) {
      return leftExpression.containsVariableReference(varName) || rightExpression.containsVariableReference(varName);
   }
}
