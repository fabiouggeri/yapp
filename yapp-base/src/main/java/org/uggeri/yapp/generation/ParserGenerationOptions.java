/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.generation;

import java.io.File;

/**
 *
 * @author fabio
 */
public interface ParserGenerationOptions {

   public String getPackageName();
   
   public File getOutputDirectory();
   
   public boolean isOverwriteTargetFiles();

   public boolean isGenerateTraceCode();
   
   public MemoizeMode getMemoizeMode();
   
   public boolean isProfile();

   public boolean isCatchMismatches();

   public boolean isGenerateSingleFileSource();

   public LeftRecursionStrategy getLeftRecursionStrategy();
}
