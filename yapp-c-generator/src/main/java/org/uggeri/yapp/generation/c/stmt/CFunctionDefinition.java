/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.generation.c.stmt;

import org.uggeri.yapp.generation.stmt.DataType;
import org.uggeri.yapp.generation.stmt.Expression;
import org.uggeri.yapp.generation.stmt.FunctionCall;
import org.uggeri.yapp.generation.stmt.FunctionDefinition;
import static org.uggeri.yapp.generation.stmt.FunctionDefinition.Modifier.PRIVATE;
import org.uggeri.yapp.generation.stmt.VariableDeclaration;
import java.util.EnumSet;

/**
 *
 * @author fabio
 */
public class CFunctionDefinition extends CBlockStatement implements FunctionDefinition {

   private final EnumSet<Modifier> modifiers;

   private final String name;

   private final DataType returnDataType;

   private final VariableDeclaration[] arguments;

   public CFunctionDefinition(EnumSet<Modifier> modifiers, DataType returnDataType, String functionName, VariableDeclaration... arguments) {
      this.modifiers = modifiers;
      this.name = functionName;
      this.returnDataType = returnDataType;
      this.arguments = arguments;
   }
   
   public CFunctionDefinition(EnumSet<Modifier> modifiers, String functionName, VariableDeclaration... arguments) {
      this(modifiers, null, functionName, arguments);
   }

   @Override
   public EnumSet<Modifier> getModifiers() {
      return modifiers;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public DataType getReturnDataType() {
      return returnDataType;
   }

   @Override
   public VariableDeclaration[] getArguments() {
      return arguments;
   }

   @Override
   public FunctionCall call(Object... parameters) {
      Expression[] arrayAux = new Expression[parameters.length];
      for (int i = 0; i < parameters.length; i++) {
         arrayAux[i] = asExpression(parameters[i]);
      }
      return new CFunctionCall(name, arrayAux);
   }

   @Override
   public void appendCode(StringBuilder text, int indentation) {
      boolean first = true;
      text.append('\n');
      indent(text, indentation);
      for (Modifier modifier : modifiers) {
         switch (modifier) {
            case PRIVATE:
               text.append("static ");
               break;
            case PROTECTED:
               text.append("static ");
               break;
            case STATIC:
               text.append("static ");
               break;
         }
      }
      if (returnDataType != null) {
         text.append(returnDataType.getName()).append(' ');
      } else {
         text.append("void ");
      }
      text.append(name).append('(');
      for (VariableDeclaration arg : arguments) {
         if (first) {
            first = false;
         } else {
            text.append(", ");
         }
         arg.appendCode(text, 0);
      }
      text.append(')');
      super.appendCode(text, indentation);
   }
}
