/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.generation;

/**
 *
 * @author fabio
 */
public interface ParserGenerator {
   
   public void generateParser() throws ParserGenerationException;
   
   public void validate() throws ParserGenerationException;
}
