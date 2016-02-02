/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.generation.java.stmt;

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
import org.uggeri.yapp.generation.stmt.SingleOperandExpression;

/**
 *
 * @author fabio
 */
public abstract class JavaExpression extends AbstractJavaCodeFragment implements Expression {

   @Override
   public Expression assign(Object expr) {
      return new JavaDoubleExpressionOperator(this, ASSIGN, asExpression(expr));
   }

   @Override
   public Expression equal(Object expr) {
      return new JavaDoubleExpressionOperator(this, EQUAL, asExpression(expr));
   }

   @Override
   public Expression less(Object expr) {
      return new JavaDoubleExpressionOperator(this, LESS, asExpression(expr));
   }

   @Override
   public Expression greater(Object expr) {
      return new JavaDoubleExpressionOperator(this, GREATER, asExpression(expr));
   }

   @Override
   public Expression lessOrEqual(Object expr) {
      return new JavaDoubleExpressionOperator(this, LESS_OR_EQUAL, asExpression(expr));
   }

   @Override
   public Expression greaterOrEqual(Object expr) {
      return new JavaDoubleExpressionOperator(this, GREATER_OR_EQUAL, asExpression(expr));
   }

   @Override
   public Expression diff(Object expr) {
      return new JavaDoubleExpressionOperator(this, DIFFERENT, asExpression(expr));
   }

   @Override
   public Expression plus(Object expr) {
      return new JavaDoubleExpressionOperator(this, PLUS, asExpression(expr));
   }

   @Override
   public Expression minus(Object expr) {
      return new JavaDoubleExpressionOperator(this, MINUS, asExpression(expr));
   }

   @Override
   public Expression mult(Object expr) {
      return new JavaDoubleExpressionOperator(this, MULT, asExpression(expr));
   }

   @Override
   public Expression div(Object expr) {
      return new JavaDoubleExpressionOperator(this, DIV, asExpression(expr));
   }

   @Override
   public Expression mod(Object expr) {
      return new JavaDoubleExpressionOperator(this, MOD, asExpression(expr));
   }

   @Override
   public Expression plusEqual(Object expr) {
      return new JavaDoubleExpressionOperator(this, PLUS_EQUAL, asExpression(expr));
   }

   @Override
   public Expression minusEqual(Object expr) {
      return new JavaDoubleExpressionOperator(this, MINUS_EQUAL, asExpression(expr));
   }

   @Override
   public Expression multEqual(Object expr) {
      return new JavaDoubleExpressionOperator(this, MULT_EQUAL, asExpression(expr));
   }

   @Override
   public Expression divEqual(Object expr) {
      return new JavaDoubleExpressionOperator(this, DIV_EQUAL, asExpression(expr));
   }

   @Override
   public Expression modEqual(Object expr) {
      return new JavaDoubleExpressionOperator(this, MOD_EQUAL, asExpression(expr));
   }

   @Override
   public Expression and(Object expr) {
      return new JavaDoubleExpressionOperator(this, AND, asExpression(expr));
   }

   @Override
   public Expression or(Object expr) {
      return new JavaDoubleExpressionOperator(this, OR, asExpression(expr));
   }

   @Override
   public Expression not() {
      return new JavaSingleOperandExpression(SingleOperandExpression.Operator.NEGATE, this);
   }

   @Override
   public Expression preInc() {
      return new JavaSingleOperandExpression(SingleOperandExpression.Operator.PRE_INC, this);
   }

   @Override
   public Expression preDec() {
      return new JavaSingleOperandExpression(SingleOperandExpression.Operator.PRE_DEC, this);
   }

   @Override
   public Expression posInc() {
      return new JavaSingleOperandExpression(SingleOperandExpression.Operator.POS_INC, this);
   }

   @Override
   public Expression posDec() {
      return new JavaSingleOperandExpression(SingleOperandExpression.Operator.POS_DEC, this);
   }

   @Override
   public Expression member(Object expr) {
      return new JavaQualifiedExpression(this, asExpression(expr));
   }

   @Override
   public Expression get(Object... expr) {
      Expression[] indexes = new Expression[expr.length];
      for (int i = 0; i < expr.length; i++) {
         indexes[i] = asExpression(expr[i]);
      }
      return new JavaIndexedExpression(this, indexes);
   }
}
