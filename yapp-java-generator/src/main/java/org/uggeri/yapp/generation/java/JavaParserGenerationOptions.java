/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.generation.java;

import org.uggeri.yapp.generation.LeftRecursionStrategy;
import org.uggeri.yapp.generation.MemoizeMode;
import org.uggeri.yapp.generation.ParserGenerationOptions;
import java.io.File;

/**
 *
 * @author fabio
 */
public class JavaParserGenerationOptions implements ParserGenerationOptions {

   private File outputDirectory;
   private String packageName;
   private boolean overwriteTargetFile = true;
   private boolean generateTraceCode = false;
   private MemoizeMode memoizeMode = MemoizeMode.ALL;
   private boolean profile = false;
   private boolean catchMismatches = true;
   private boolean generateSingleFileSource = false;
   private LeftRecursionStrategy leftRecursionStrategy = LeftRecursionStrategy.GENERATE_ERROR;

   public JavaParserGenerationOptions(File outputDirectory, String packageName) {
      this.outputDirectory = outputDirectory;
      this.packageName = packageName;
   }
   
   @Override
   public String getPackageName() {
      return packageName;
   }

   public void setPackageName(String packageName) {
      this.packageName = packageName;
   }

   @Override
   public File getOutputDirectory() {
      return outputDirectory;
   }

   public void setOutputDirectory(File outputDirectory) {
      this.outputDirectory = outputDirectory;
   }

   @Override
   public boolean isOverwriteTargetFiles() {
      return overwriteTargetFile;
   }

   public void setOverwriteTargetFile(boolean overwriteTargetFile) {
      this.overwriteTargetFile = overwriteTargetFile;
   }

   @Override
   public boolean isGenerateTraceCode() {
      return generateTraceCode;
   }

   public void setGenerateTraceCode(boolean generateTraceCode) {
      this.generateTraceCode = generateTraceCode;
   }

   public void setMemoizeMode(MemoizeMode mode) {
      this.memoizeMode = mode;
   }

   @Override
   public MemoizeMode getMemoizeMode() {
      return memoizeMode;
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
      return generateSingleFileSource;
   }

   public void setGenerateSingleFileSource(boolean generateSingleFileSource) {
      this.generateSingleFileSource = generateSingleFileSource;
   }

   @Override
   public LeftRecursionStrategy getLeftRecursionStrategy() {
      return leftRecursionStrategy;
   }

   public void setLeftRecursionStrategy(LeftRecursionStrategy leftRecursionStrategy) {
      this.leftRecursionStrategy = leftRecursionStrategy;
   }
}
