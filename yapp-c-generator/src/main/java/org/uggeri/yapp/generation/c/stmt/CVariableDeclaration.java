/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.generation.c.stmt;

import org.uggeri.yapp.generation.stmt.DataType;
import org.uggeri.yapp.generation.stmt.Expression;
import org.uggeri.yapp.generation.stmt.Variable;
import org.uggeri.yapp.generation.stmt.VariableDeclaration;

/**
 *
 * @author fabio
 */
public class CVariableDeclaration extends AbstractCCodeFragment implements VariableDeclaration {

   private final DataType type;
   private final Variable variable;
   private final Expression initializationExpression;

   public CVariableDeclaration(DataType type, Variable var, Expression initializationExpression) {
      this.type = type;
      this.variable = var;
      this.initializationExpression = initializationExpression;
   }
   
   public CVariableDeclaration(DataType type, String name, Expression initializationExpression) {
      this(type, new CVariable(name), initializationExpression);
   }
   
   public CVariableDeclaration(DataType type, String name) {
      this(type, name, null);
   }

   public CVariableDeclaration(DataType type, Variable var) {
      this(type, var, null);
   }

   @Override
   public DataType getDataType() {
      return type;
   }

   @Override
   public Variable getVariable() {
      return variable;
   }

   @Override
   public Expression getInitializationExpression() {
      return initializationExpression;
   }
   
   @Override
   public void appendCode(StringBuilder text, int indentation) {
      indent(text, indentation).append(type.getName()).append(' ');
      variable.appendCode(text, 0);
      if (initializationExpression != null) {
         text.append(" = ");
         initializationExpression.appendCode(text, 0);
      }
   }
}