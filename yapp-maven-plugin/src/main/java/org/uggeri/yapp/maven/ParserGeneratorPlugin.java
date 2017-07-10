/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.maven;

import java.io.File;
import java.io.FilenameFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.uggeri.yapp.generation.MemoizeMode;
import org.uggeri.yapp.generation.ParserGenerationException;
import org.uggeri.yapp.generation.java.JavaParserGenerationOptions;
import org.uggeri.yapp.generation.java.JavaParserGenerator;
import org.uggeri.yapp.grammar.Grammar;
import org.uggeri.yapp.grammar.GrammarException;
import org.uggeri.yapp.grammar.GrammarLoader;

/**
 *
 * @author fabio_uggeri
 */
@Mojo(name = "generate-parsers", requiresProject = true, defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class ParserGeneratorPlugin extends AbstractMojo {

   @Parameter(name = "sourceDir", defaultValue = "${basedir}/src/main/yapp")
   private File sourceDir;

   @Parameter(name = "outputDir", required = true, defaultValue = "${project.build.directory}/generated-sources/yapp")
   private File outputDir;

   @Parameter(name = "pkg", defaultValue = "\"\"")
   private String pkg;

   @Parameter(name = "trace", defaultValue = "false")
   private boolean trace;

   @Parameter(name = "profile", defaultValue = "false")
   private boolean profile;

   @Parameter(name = "memoize", defaultValue = "AUTO")
   private MemoizeMode memoize;

   @Parameter(name = "overwrite", defaultValue = "true")
   private boolean overwrite;

   @Parameter(name = "catchMismatches", defaultValue = "false")
   private boolean catchMismatches;

   @Parameter(name = "generateSingleFile", defaultValue = "false")
   private boolean generateSingleFile;

   @Parameter(property = "project", required = true, readonly = true)
   protected MavenProject project;

   @Override
   public void execute() throws MojoExecutionException, MojoFailureException {
      try {
         final File[] grammars = sourceDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
               return FileUtils.getExtension(name).equalsIgnoreCase("gy");
            }
         });
         if (grammars != null) {
            for (File g : grammars) {
               generateParser(g);
            }
            project.addCompileSourceRoot(outputDir.getPath());
         }
      } catch (ParserGenerationException ex) {
         throw new MojoExecutionException("Error generating parser sources", ex);
      } catch (GrammarException ex) {
         throw new MojoExecutionException("Error reading grammar", ex);
      }
   }

   private void generateParser(final File grammarFile) throws GrammarException, ParserGenerationException {
      final GrammarLoader grammarLoader = new GrammarLoader();
      final Grammar grammar = grammarLoader.loadGrammar(grammarFile);
      final JavaParserGenerationOptions options = new JavaParserGenerationOptions(getOutputDir(), pkg);
      options.setGenerateTraceCode(trace);
      options.setProfile(profile);
      options.setCatchMismatches(catchMismatches);
      options.setMemoizeMode(memoize);
      options.setOverwriteTargetFile(overwrite);
      options.setGenerateSingleFileSource(generateSingleFile);
      new JavaParserGenerator(grammar, options).generateParser();
   }

   private File getOutputDir() {
      if (pkg != null && ! pkg.trim().isEmpty()) {
         final File out = new File(outputDir, pkg.replace('.', File.separatorChar));
         out.mkdirs();
         return out;
      } else {
         return outputDir;
      }
   }

}
