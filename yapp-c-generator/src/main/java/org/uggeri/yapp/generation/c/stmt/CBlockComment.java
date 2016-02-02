/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.generation.c.stmt;

import org.uggeri.yapp.generation.stmt.BlockComment;
import org.uggeri.yapp.generation.stmt.Statement;

/**
 *
 * @author fabio
 */
public class CBlockComment extends AbstractCCodeFragment implements BlockComment, Statement {

   private final String comment;

   public CBlockComment(String comment) {
      this.comment = comment;
   }
   
   @Override
   public void appendCode(StringBuilder text, int indentation) {
      text.append('\n');
      indent(text, indentation).append("/*").append(comment).append("*/");
   }
   
}
