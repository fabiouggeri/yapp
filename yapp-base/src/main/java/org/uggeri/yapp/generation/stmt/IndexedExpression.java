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
public interface IndexedExpression extends Expression {
   
   public Expression getArrayExpression();
   
   public Expression[] getIndexes();
}
