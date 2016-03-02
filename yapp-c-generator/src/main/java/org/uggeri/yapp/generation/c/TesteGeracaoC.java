/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.generation.c;

import org.uggeri.yapp.generation.MemoizeMode;
import org.uggeri.yapp.generation.ParserGenerationException;
import org.uggeri.yapp.generation.ParserGenerator;
import org.uggeri.yapp.grammar.Grammar;
import org.uggeri.yapp.grammar.GrammarException;
import org.uggeri.yapp.grammar.GrammarLoader;
import org.uggeri.yapp.grammar.GrammarSyntaxException;
import java.io.File;

/**
 *
 * @author fabio
 */
public class TesteGeracaoC {

   public static void main(String[] args) {
      new TesteGeracaoC().testeGeracaoC();
   }

   private void testeGeracaoC() {
      try {
         CParserGenerationOptions options;
         ParserGenerator pg;
         GrammarLoader gr;
         Grammar g;
         gr = new GrammarLoader();
         g = gr.loadGrammar(new File("E:\\desenvolvimento\\yapp\\yapp-java-runtime\\src\\main\\java\\org\\uggeri\\yapp\\runtime\\java\\test\\Java.gy"));
         options = new CParserGenerationOptions(new File("E:\\desenvolvimento\\yapp\\yapp-c-runtime\\yapp-c-runtime-lib\\src\\test\\c"));
         options.setGenerateTraceCode(false);
         options.setProfile(false);
         options.setCatchMismatches(false);
         //options.setMemoizeMode(MemoizeMode.ALL);
         options.setMemoizeMode(MemoizeMode.AUTO);
         //options.setGenerateSingleFileSource(true);
         pg = new CParserGenerator(g, options);
         pg.generateParser();
      } catch (GrammarSyntaxException ex) {
         System.out.println("Error at: " + ex.getLine() + "," + ex.getColumn());
         ex.printStackTrace(System.out);
      } catch (GrammarException ex) {
         ex.printStackTrace(System.out);
      } catch (ParserGenerationException ex) {
         ex.printStackTrace(System.out);
      }
   }
}
