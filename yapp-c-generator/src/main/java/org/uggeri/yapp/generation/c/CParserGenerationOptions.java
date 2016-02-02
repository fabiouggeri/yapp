/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.generation.c;

import org.uggeri.yapp.generation.LeftRecursionStrategy;
import org.uggeri.yapp.generation.MemoizeMode;
import org.uggeri.yapp.generation.ParserGenerationOptions;
import java.io.File;

/**
 *
 * @author fabio
 */
public class CParserGenerationOptions implements ParserGenerationOptions {
   
   private final File outputDir;
   private boolean catchMismatches = true;
   private boolean profile = false;
   private MemoizeMode memoizeMode = MemoizeMode.EXPLICIT;
   private boolean generateTraceCode = false;
   private boolean overwriteTargetFiles = true;
   private LeftRecursionStrategy leftRecursionStrategy = LeftRecursionStrategy.GENERATE_ERROR;

   public CParserGenerationOptions(File outputDir) {
      this.outputDir = outputDir;
   }

   @Override
   public String getPackageName() {
      return "";
   }

   @Override
   public File getOutputDirectory() {
      return outputDir;
   }

   @Override
   public boolean isOverwriteTargetFiles() {
      return overwriteTargetFiles;
   }

   public void setOverwriteTargetFiles(boolean overwriteTargetFiles) {
      this.overwriteTargetFiles = overwriteTargetFiles;
   }

   @Override
   public boolean isGenerateTraceCode() {
      return generateTraceCode;
   }

   public void setGenerateTraceCode(boolean generateTraceCode) {
      this.generateTraceCode = generateTraceCode;
   }

   @Override
   public MemoizeMode getMemoizeMode() {
      return memoizeMode;
   }

   public void setMemoizeMode(MemoizeMode memoizeMode) {
      this.memoizeMode = memoizeMode;
   }

   @Override
   public boolean isProfile() {
      return profile;
   }

   public void setProfile(boolean profile) {
      this.profile = profile;
   }

   @Override
   public boolean isCatchMismatches() {
      return catchMismatches;
   }

   public void setCatchMismatches(boolean catchMismatches) {
      this.catchMismatches = catchMismatches;
   }

   @Override
   public boolean isGenerateSingleFileSource() {
      return false;
   }

   @Override
   public LeftRecursionStrategy getLeftRecursionStrategy() {
      return leftRecursionStrategy;
   }

   public void setLeftRecursionStrategy(LeftRecursionStrategy leftRecursionStrategy) {
      this.leftRecursionStrategy = leftRecursionStrategy;
   }
}
