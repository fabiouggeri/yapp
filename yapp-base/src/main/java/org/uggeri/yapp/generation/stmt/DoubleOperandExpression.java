/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.generation.stmt;

/**
 *
 * @author fabio
 */
public interface DoubleOperandExpression extends Expression{
   
   public enum Operator {
      ASSIGN,
      EQUAL, 
      LESS,
      GREATER,
      LESS_OR_EQUAL,
      GREATER_OR_EQUAL,
      DIFFERENT,
      MINUS, 
      PLUS,
      MULT,
      DIV,
      MOD,
      MINUS_EQUAL, 
      PLUS_EQUAL,
      MULT_EQUAL,
      DIV_EQUAL,
      MOD_EQUAL,
      AND,
      OR
   }
   
   public Expression getLeftExpression();
   
   public Operator getOperator();
   
   public Expression getRightExpression();
}
