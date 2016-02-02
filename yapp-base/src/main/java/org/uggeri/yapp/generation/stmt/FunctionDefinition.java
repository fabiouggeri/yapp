/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.generation.stmt;

import java.util.EnumSet;

/**
 *
 * @author fabio
 */
public interface FunctionDefinition extends BlockStatement {

   public enum Modifier {
      PUBLIC,
      PRIVATE,
      STATIC,
      FINAL,
      PROTECTED
   }
   
   public EnumSet<Modifier> getModifiers();
           
   public String getName();
   
   public DataType getReturnDataType();
   
   public VariableDeclaration[] getArguments();
   
   public FunctionCall call(Object... params);
}
