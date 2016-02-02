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
public interface SingleOperandExpression extends Expression {
   public enum Operator {
      PRE_INC,
      PRE_DEC,
      POS_INC,
      POS_DEC,
      NEGATE
   }
   
   public Expression getExpression();
   
   public Operator getOperator();
   
}
