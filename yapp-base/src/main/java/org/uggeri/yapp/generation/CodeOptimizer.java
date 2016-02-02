/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.generation;

import org.uggeri.yapp.generation.stmt.BlockStatement;

/**
 *
 * @author fabio
 */
public interface CodeOptimizer {

   public void optimize(BlockStatement codeBlock);
}
