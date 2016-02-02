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
public interface Variable extends Expression {

   public String getName();
   
   public void setName(String name);

   public VariableDeclaration declare(DataType dataType);
   
   public VariableDeclaration declare(DataType dataType, Expression initialization);
}
