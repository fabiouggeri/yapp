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
public interface Expression extends CodeFragment {
   
   public boolean containsVariableReference(final String varName);
   
   public Expression assign(Object expr);
   
   public Expression equal(Object expr);
   
   public Expression less(Object expr);
   
   public Expression greater(Object expr);
   
   public Expression lessOrEqual(Object expr);
   
   public Expression greaterOrEqual(Object expr);
   
   public Expression diff(Object expr);
   
   public Expression plus(Object expr);
   
   public Expression minus(Object expr);
   
   public Expression mult(Object expr);
   
   public Expression div(Object expr);
   
   public Expression mod(Object expr);
   
   public Expression plusEqual(Object expr);
   
   public Expression minusEqual(Object expr);
   
   public Expression multEqual(Object expr);
   
   public Expression divEqual(Object expr);
   
   public Expression modEqual(Object expr);
   
   public Expression and(Object expr);
   
   public Expression or(Object expr);

   public Expression preInc();
   
   public Expression preDec();
   
   public Expression posInc();
   
   public Expression posDec();
   
   public Expression not();

   public Expression member(Object expr);
   
   public Expression get(Object... expr);
}
