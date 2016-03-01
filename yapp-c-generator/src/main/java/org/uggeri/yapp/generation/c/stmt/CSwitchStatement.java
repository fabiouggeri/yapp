/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.generation.c.stmt;

import org.uggeri.yapp.generation.stmt.Expression;
import org.uggeri.yapp.generation.stmt.SwitchOption;
import org.uggeri.yapp.generation.stmt.SwitchStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author fabio
 */
public class CSwitchStatement extends AbstractCCodeFragment implements SwitchStatement {

   private final Expression expressionTest;
   
   private final List<SwitchOption> options = new ArrayList<SwitchOption>();

   public CSwitchStatement(Expression expressionTest) {
      this.expressionTest = expressionTest;
   }
   
   @Override
   public void appendCode(StringBuilder text, int indentation) {
      text.append('\n');
      indent(text, indentation).append("switch(");
      expressionTest.appendCode(text, 0);
      text.append(") {");
      for (SwitchOption option : options) {
         option.appendCode(text, indentation + 3);
      }
      text.append('\n');
      indent(text, indentation).append("}");
   }

   @Override
   public SwitchOption switchOption(Set<?> values) {
      final SwitchOption option = new CSwitchOption(values);
      options.add(option);
      return option;
   }

   @Override
   public List<SwitchOption> listOptions() {
      return options;
   }
}
