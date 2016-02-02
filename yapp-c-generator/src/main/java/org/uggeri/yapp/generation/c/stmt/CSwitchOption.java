/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.generation.c.stmt;

import org.uggeri.yapp.generation.stmt.CodeFragment;
import org.uggeri.yapp.generation.stmt.SwitchOption;

/**
 *
 * @author fabio
 */
public class CSwitchOption extends CBlockStatement implements SwitchOption {

   private final Object[] literalValues;

   public CSwitchOption(Object... literalValues) {
      this.literalValues = literalValues;
   }
   
   @Override
   public void appendCode(StringBuilder text, int indentation) {
      if (literalValues != null && literalValues.length > 0) {
         for (final Object option : literalValues) {
            text.append('\n');
            indent(text, indentation).append("case ");
            asExpression(option).appendCode(text, 0);
            text.append(":");
         }
      } else {
         text.append('\n');
         indent(text, indentation).append("default:");
      }
      for (CodeFragment code : listStatements()) {
         code.appendCode(text, indentation + 3);
      }
   }
   
}
