/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.runtime.java;

import org.uggeri.yapp.runtime.java.buffer.FileInputBuffer;
import org.uggeri.yapp.runtime.java.buffer.InputBuffer;
import org.uggeri.yapp.runtime.java.node.Node;
import org.uggeri.yapp.runtime.java.parser.ParserError;
import org.uggeri.yapp.runtime.java.parser.Rule;
import org.uggeri.yapp.runtime.java.parser.RuleProfile;
import org.uggeri.yapp.runtime.java.test.HarbourPPParser;
import org.uggeri.yapp.runtime.java.test.HarbourUnprocessedParser;
import org.uggeri.yapp.runtime.java.trace.TraceParser;
import org.uggeri.yapp.runtime.java.trace.TraceStandardOutput;
import org.uggeri.yapp.runtime.java.test.JavaParser;
import org.uggeri.yapp.runtime.java.trace.LimitedTraceFile;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.uggeri.yapp.grammar.rules.AndRule;
import org.uggeri.yapp.grammar.rules.CharRangeRule;
import org.uggeri.yapp.grammar.rules.CharRule;
import org.uggeri.yapp.grammar.rules.GrammarRule;
import org.uggeri.yapp.grammar.rules.IgnoreCaseStringRule;
import org.uggeri.yapp.grammar.rules.NonTerminalRule;
import org.uggeri.yapp.grammar.rules.OneOrMoreRule;
import org.uggeri.yapp.grammar.rules.OptionalRule;
import org.uggeri.yapp.grammar.rules.OrRule;
import org.uggeri.yapp.grammar.rules.StringRule;
import org.uggeri.yapp.grammar.rules.TestNotRule;
import org.uggeri.yapp.grammar.rules.TestRule;
import org.uggeri.yapp.grammar.rules.ZeroOrMoreRule;
import org.uggeri.yapp.runtime.java.parser.ParseTreeWalker;
import org.uggeri.yapp.runtime.java.test.JavaVisitor;
import org.uggeri.yapp.runtime.java.test.OracleScriptParser;

/**
 *
 * @author fabio
 */
public class TesteExecucao {

   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {
      //char c = '\u005cn';
      //new TesteExecucao().testesDiversos();
      //new TesteExecucao().testeParseJavaSources();
      new TesteExecucao().testeParsePlSqlSources();
      //new TesteExecucao().testeExecucaoPlSql();
      //new TesteExecucao().testeExecucaoJava();
      //new TesteExecucao().testeExecucaoHarbourPP();
      //new TesteExecucao().testeExecucaoHarbourUnprocessed();
      //new TesteExecucao().testeExecucao();
   }

   private boolean testBoolean() {
      return true;
   }

   private int[] newArrayInt(final int size) {
      final int[] array = new int[size];
      Arrays.fill(array, -1);
      return array;
   }

   private long count = 0;

   private void testesDiversos() {
      long start = System.currentTimeMillis();
      for (int i = 0; i < 10000000; i++) {
         int[] array = newArrayInt(1000);
         //int[] array = new int[1000];
//         int[] array = new int[] {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
//                                  -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
//                                  -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
//                                  -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
//                                  -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
//                                  -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
//                                  -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
//                                  -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
//                                  -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,
//                                  -1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,};
//         if (testBoolean() && testBoolean()) {
//            ++count;
//         }
//         boolean match = false;
//         teste:
//         {
//            teste1:
//            {
//               match = true;
//               break teste;
//            }
//         }
//         teste:
//         if (match) {
//            ++count;
//         }
      }
      System.out.println("Elapsed : " + (System.currentTimeMillis() - start) + "ms");
      System.out.println("Total   : " + count);
//      long iniParse;
//      NodeLink[] array;
//      System.out.println("1. Mem. Livre: " + Runtime.getRuntime().freeMemory() / 1024);
//      array = new NodeLink[614400];
//      for (int i = 0; i < 614400; i++) {
//         array[i] = new NodeLink(array, i == 0 ? null : array[i - 1]);
//      }
//      System.out.println("2. Mem. Livre: " + Runtime.getRuntime().freeMemory() / 1024);
//      iniParse = System.currentTimeMillis();
//      for (int i = 0; i < 1000000000; i++) {
//         Object o = array[i % 614400];
//      }
//      System.out.println("Tempo : " + (System.currentTimeMillis() - iniParse));
//      System.out.println("3. Mem. Livre: " + Runtime.getRuntime().freeMemory() / 1024);
   }

   private int linesCount = 0;
   private long totalSize = 0;

   private void testeParseJavaSources() {
      final long initTime = System.currentTimeMillis();
      final File dirScan = new File("C:\\temp\\jdk-src");
      final long totalTime;
      final Map<Rule, RuleProfile> rulesProfiles = new HashMap<Rule, RuleProfile>();
      totalTime = scanSources(dirScan, 0, rulesProfiles);
      System.out.println("Parse time: " + totalTime + "ms");
      System.out.println("Total time: " + (System.currentTimeMillis() - initTime) + "ms");
      System.out.println("Total Mem: " + (Runtime.getRuntime().totalMemory()) / 1024 + "KB");
      System.out.println("Total Lines: " + linesCount);
      System.out.println("Total Size: " + (totalSize / 1024) + " KB");
      printProfile(rulesProfiles);
   }

   private long scanSources(File file, long totalTime, final Map<Rule, RuleProfile> rulesProfiles) {
      if (file.isDirectory()) {
         for (File child : file.listFiles()) {
            totalTime += scanSources(child, 0, rulesProfiles);
         }
      } else if (file.getName().toLowerCase().endsWith(".java")) {
         //if (file.getName().equalsIgnoreCase("ParserTokenManager.java")) {
         totalTime += parseJavaFile(file, rulesProfiles);
         countFileLines(file);
         //}
      }
      return totalTime;
   }

   private void countFileLines(File file) {
      BufferedReader br = null;
      try {
         br = new BufferedReader(new FileReader(file));
         totalSize += file.length();
         while (br.readLine() != null) {
            ++linesCount;
         }
      } catch (FileNotFoundException ex) {
      } catch (IOException ex) {
      } finally {
         try {
            if (br != null) {
               br.close();
            }
         } catch (IOException ex) {
         }
      }
   }

   private long parseJavaFile(File file, Map<Rule, RuleProfile> profilesMap) {
      try {
         final JavaParser javaParser = new JavaParser();
         final InputBuffer inputBuffer = new FileInputBuffer(file);
         final Node node;
         final long iniParse = System.currentTimeMillis();
         javaParser.setProfilesMap(profilesMap);
         node = javaParser.parse(inputBuffer);
         if (node == null) {
            System.out.println("Erro parsing " + file.getAbsolutePath() + " at " + javaParser.getMismatches());
         }
         return System.currentTimeMillis() - iniParse;
      } catch (IOException ex) {
         ex.printStackTrace(System.out);
      }
      return 0L;
   }
   
   private void testeParsePlSqlSources() {
      final long initTime = System.currentTimeMillis();
      final File dirScan = new File("E:\\desenvolvimento\\sicredi");
      final long totalTime;
      final Map<Rule, RuleProfile> rulesProfiles = new HashMap<Rule, RuleProfile>();
      totalTime = scanPlSqlSources(dirScan, 0, rulesProfiles);
      System.out.println("Parse time: " + totalTime + "ms");
      System.out.println("Total time: " + (System.currentTimeMillis() - initTime) + "ms");
      System.out.println("Total Mem: " + (Runtime.getRuntime().totalMemory()) / 1024 + "KB");
      System.out.println("Total Lines: " + linesCount);
      System.out.println("Total Size: " + (totalSize / 1024) + " KB");
      printProfile(rulesProfiles);
   }

   private long scanPlSqlSources(File file, long totalTime, final Map<Rule, RuleProfile> rulesProfiles) {
      final String fileName = file.getName().toLowerCase();
      if (file.isDirectory()) {
         for (File child : file.listFiles()) {
            totalTime += scanPlSqlSources(child, 0, rulesProfiles);
         }
      } else if (fileName.endsWith(".pkb") ||
              fileName.endsWith(".pks") ||
              fileName.endsWith(".pkg") ||
              fileName.endsWith(".fnc") ||
              fileName.endsWith(".prc")) {
         //if (file.getName().equalsIgnoreCase("ParserTokenManager.java")) {
         totalTime += parsePlSqlFile(file, rulesProfiles);
         countFileLines(file);
         //}
      }
      return totalTime;
   }

   private long parsePlSqlFile(File file, Map<Rule, RuleProfile> profilesMap) {
      try {
         final OracleScriptParser parser = new OracleScriptParser();
         final InputBuffer inputBuffer = new FileInputBuffer(file);
         final Node node;
         final long iniParse;
         System.out.print("Parsing " + file.getAbsolutePath() + "...");
         iniParse = System.currentTimeMillis();
         parser.setProfilesMap(profilesMap);
         node = parser.parse(inputBuffer);
         if (node != null) {
            System.out.println("OK");
         } else {
            System.out.println("Error at " + parser.getMismatches());
         }
         return System.currentTimeMillis() - iniParse;
      } catch (IOException ex) {
         ex.printStackTrace(System.out);
      }
      return 0L;
   }

   // 
   private void testeExecucaoPlSql() {
      try {
         TraceParser traceFile = new LimitedTraceFile(new File("c:\\temp\\yapp.trc"), 100 * 1024 * 1024);
         //TraceParser traceFile = new TraceStandardOutput();
         InputBuffer inputBuffer;
         OracleScriptParser parser = new OracleScriptParser();
         Node node;
         long iniParse;
         parser.setTraceParser(traceFile);
         parser.setTrace(true);
         //javax.swing.JOptionPane.showMessageDialog(null, "Tecla para continuar");
//         System.out.println("1. Mem. Livre: " + Runtime.getRuntime().freeMemory() / 1024.0 / 1024.0 + "MB");
         iniParse = System.currentTimeMillis();
         // inputBuffer = new FileInputBuffer(new File("E:\\desenvolvimento\\sicredi\\infra-2.0.5\\05-construcao\\infra-plsql\\src\\main\\plsql\\pkgl_infra_util.pkb"));
         inputBuffer = new FileInputBuffer(new File("E:\\desenvolvimento\\sicredi\\ccrp\\src\\plsql\\pkgl_ccrp_publica.pkb"));
//         System.out.println("File size: " + inputBuffer.length());
//         System.out.println("2. Mem. Livre: " + Runtime.getRuntime().freeMemory() / 1024.0 / 1024.0 + "MB");
         node = parser.parse(inputBuffer);
         System.out.println("Tempo : " + (System.currentTimeMillis() - iniParse));
         traceFile.close();
         //javax.swing.JOptionPane.showMessageDialog(null, "Fim");
         //printProfile(parser.getProfilesMap());
//         System.out.println("3. Mem. Livre: " + Runtime.getRuntime().freeMemory() / 1024.0 / 1024.0 + "MB");
         if (node != null) {
            //printTree(inputBuffer, node, 0);
//            System.out.println("==============================");
            //printSemanticTree(inputBuffer, node, 0);
            //new ParseTreeWalker().walk(node, new JavaVisitor(inputBuffer));
         } else {
            System.out.println("Index error: " + parser.getMismatches());
         }
      } catch (IOException ex) {
         ex.printStackTrace(System.out);
      }
   }

   private void testeExecucaoJava() {
      try {
         TraceParser traceFile = new LimitedTraceFile(new File("c:\\temp\\yapp.trc"), 100 * 1024 * 1024);
         //TraceParser traceFile = new TraceStandardOutput();
         InputBuffer inputBuffer;
         JavaParser javaParser = new JavaParser();
         Node node;
         long iniParse;
         //javaParser.setTraceParser(traceFile);
         //javaParser.setTrace(true);
         //javax.swing.JOptionPane.showMessageDialog(null, "Tecla para continuar");
//         System.out.println("1. Mem. Livre: " + Runtime.getRuntime().freeMemory() / 1024.0 / 1024.0 + "MB");
         iniParse = System.currentTimeMillis();
         //inputBuffer = new FileInputBuffer(new File("C:\\temp\\plsql\\SqlGrammar.java"));
         inputBuffer = new FileInputBuffer(new File("C:\\temp\\plsql\\PlSqlGrammar.java"));
         //inputBuffer = new FileInputBuffer(new File("C:\\temp\\jdk-src\\com\\sun\\tools\\doclets\\formats\\html\\SingleIndexWriter.java"));
         //inputBuffer = new FileInputBuffer(new File("C:\\temp\\jdk-src\\com\\sun\\xml\\internal\\rngom\\parse\\compact\\CompactSyntax.java"));
//         System.out.println("File size: " + inputBuffer.length());
//         System.out.println("2. Mem. Livre: " + Runtime.getRuntime().freeMemory() / 1024.0 / 1024.0 + "MB");
         node = javaParser.parse(inputBuffer);
         System.out.println("Tempo : " + (System.currentTimeMillis() - iniParse));
         traceFile.close();
         //javax.swing.JOptionPane.showMessageDialog(null, "Fim");
         //printProfile(javaParser.getProfilesMap());
//         System.out.println("3. Mem. Livre: " + Runtime.getRuntime().freeMemory() / 1024.0 / 1024.0 + "MB");
         if (node != null) {
//            printTree(inputBuffer, node, 0);
//            System.out.println("==============================");
            //printSemanticTree(inputBuffer, node, 0);
            //new ParseTreeWalker().walk(node, new JavaVisitor(inputBuffer));
            new ParseTreeWalker().walk(node, new GrammarConverterVisitor(inputBuffer));
         } else {
            System.out.println("Index error: " + javaParser.getMismatches());
         }
      } catch (IOException ex) {
         ex.printStackTrace(System.out);
      }
   }

   private void testeExecucaoHarbourPP() {
      try {
         //TraceParser traceFile = new TraceFile(new File("c:\\temp\\yapp.trc"));
         TraceParser traceFile = new TraceStandardOutput();
         InputBuffer inputBuffer;
         HarbourPPParser parser;
         Node node;
         long iniParse = System.currentTimeMillis();
         parser = new HarbourPPParser();
         //javaParser.setTraceParser(traceFile);
         //javaParser.setTrace(true);
         //javax.swing.JOptionPane.showMessageDialog(null, "Tecla para continuar");
         inputBuffer = new FileInputBuffer(new File("C:\\temp\\rtbam.prg"));
         node = parser.parse(inputBuffer);
         System.out.println("Tempo : " + (System.currentTimeMillis() - iniParse));
         traceFile.close();
         //javax.swing.JOptionPane.showMessageDialog(null, "Fim");
         //printTree(inputBuffer, node, 0);
         //System.out.println("==============================");
         printProfile(parser.getProfilesMap());
         //printSemanticTree(inputBuffer, node, 0);
         //new ParseTreeWalker().walk(node, new HarbourPPVisitor(inputBuffer));
      } catch (IOException ex) {
         ex.printStackTrace(System.out);
      }
   }

   private void testeExecucaoHarbourUnprocessed() {
      try {
         //TraceParser traceFile = new TraceFile(new File("c:\\temp\\yapp.trc"));
         TraceParser traceFile = new LimitedTraceFile(new File("c:\\temp\\yapp.trc"), 108428800);
         //TraceParser traceFile = new TraceStandardOutput();
         InputBuffer inputBuffer;
         HarbourUnprocessedParser parser;
         Node node;
         long iniParse = System.currentTimeMillis();
         parser = new HarbourUnprocessedParser();
         parser.setTraceParser(traceFile);
         parser.setTrace(true);
         //javax.swing.JOptionPane.showMessageDialog(null, "Tecla para continuar");
         inputBuffer = new FileInputBuffer(new File("W:\\Sicredi\\sistemas\\legado\\ccr1-9.50\\libsiret\\RTDBAB2.prg"));
         //inputBuffer = new FileInputBuffer(new File("W:\\Sicredi\\sistemas\\legado\\ccr1-9.50\\libsiret\\ccr1stru.prg"));
         //inputBuffer = new FileInputBuffer(new File("c:\\Temp\\teste.prg"));
         node = parser.parse(inputBuffer);
         //removeSkipedNodes(node);
         if (node == null) {
            System.out.println("Falha em: " + (System.currentTimeMillis() - iniParse));
            for (ParserError error : parser.getMismatches()) {
               System.out.println("Error at " + error.getIndex() + ". Expecting " + error.getTextExpected() + " found " + error.getRuleNode());
            }
         } else {
            System.out.println("Sucesso em: " + (System.currentTimeMillis() - iniParse));
         }
         traceFile.close();
         //javax.swing.JOptionPane.showMessageDialog(null, "Fim");
         //printTree(inputBuffer, node, 0);
         //System.out.println("==============================");
         //printProfile(parser.getProfilesMap());
         //printSemanticTree(inputBuffer, node, 0);
         //new ParseTreeWalker().walk(node, new HarbourUnprocessedVisitor(inputBuffer));
      } catch (IOException ex) {
         ex.printStackTrace(System.out);
      }
   }

   private void printTree(InputBuffer inputBuffer, Node node, int i) {
      Node child;
      printNode(i, node, inputBuffer);
      child = node.getFirstChild();
      while (child != null) {
         printTree(inputBuffer, child, i + 1);
         child = child.getSibling();
      }
   }

   private void printNode(int i, Node node, InputBuffer inputBuffer) {
      for (int j = 0; j < i; j++) {
         System.out.print("   ");
      }
      System.out.print('[');
      System.out.print(node.getRule().getLabel());
      if (node.isSkiped()) {
         System.out.print(":skip");
      }
      // System.out.print("#" + node.hashCode());
      System.out.print("] : '");
      System.out.print(formatText(node.getText(inputBuffer)));
      System.out.println('\'');
   }

   private void printSemanticTree(InputBuffer inputBuffer, Node node, int i) {
      if (node.isSemantic()) {
         Node child;
         printNode(i, node, inputBuffer);
         child = node.getFirstChild();
         while (child != null) {
            printSemanticTree(inputBuffer, child, i + 1);
            child = child.getSibling();
         }
      }
   }

   private CharSequence formatText(CharSequence text) {
      StringBuilder sb = new StringBuilder((int) (text.length() * 1.1));
      for (int i = 0; i < text.length(); i++) {
         final char c = text.charAt(i);
         switch (c) {
            case '\n':
               sb.append("\\n");
               break;
            case '\r':
               sb.append("\\r");
               break;
            case '\t':
               sb.append("\\t");
               break;
            case '\f':
               sb.append("\\f");
               break;
            default:
               sb.append(c);
               break;
         }
      }
      return sb;
   }

   private void printProfile(Map<Rule, RuleProfile> profilesMap) {
      NumberFormat nf = NumberFormat.getIntegerInstance();
      nf.setGroupingUsed(true);
      System.out.println("Rule                             Memo Match           Memo Missmatch       Match                Missmatch          ");
      System.out.println("-------------------------------- -------------------- -------------------- -------------------- --------------------");
      for (RuleProfile profile : profilesMap.values()) {
         System.out.printf("%s %s %s %s %s\n",
                 rightPad(profile.getRule().getLabel(), 32, ' '),
                 rightPad(nf.format(profile.getMemoMatchCount()) + "(" + nf.format(profile.getMemoMatchTimeMillis()) + "ms)", 20, ' '),
                 rightPad(nf.format(profile.getMemoMismatchCount()) + "(" + nf.format(profile.getMemoMismatchTimeMillis()) + "ms)", 20, ' '),
                 rightPad(nf.format(profile.getMatchCount()) + "(" + nf.format(profile.getMatchTimeMillis()) + "ms)", 20, ' '),
                 rightPad(nf.format(profile.getMismatchCount()) + "(" + nf.format(profile.getMismatchTimeMillis()) + "ms)", 20, ' '));
      }
   }

   public String repeat(char c, int n) {
      if (n > 0) {
         char[] array = new char[n];
         Arrays.fill(array, c);
         return String.valueOf(array);
      } else {
         return "";
      }
   }

   public String rightPad(String s, int size, char c) {
      if (s.length() < size) {
         return s + repeat(c, size - s.length());
      } else if (s.length() > size) {
         return s.substring(0, size);
      } else {
         return s;
      }
   }

   private class SkipedNodesRemover {

      private InputBuffer inputBuffer;

      private Node root;

      private Node rightSibling = null;

      public SkipedNodesRemover(InputBuffer inputBuffer, Node root) {
         this.inputBuffer = inputBuffer;
         this.root = root;
      }

      public Node buildTree() {
         removeSkipedNodes(root);
         return rightSibling;
      }

      private void removeSkipedNodes(Node node) {
         if (node != null) {
            removeSkipedNodes(node.getSibling());
            if (node.isSkiped()) {
               removeSkipedNodes(node.getFirstChild());
            } else {
               node.setSibling(rightSibling);
               rightSibling = null;
               removeSkipedNodes(node.getFirstChild());
               node.setFirstChild(rightSibling);
               rightSibling = node;
            }
         }
      }
   }

//   private void removeSkipedNodes(Node node, InputBuffer inputBuffer) {
//      new SkipedNodesRemover(inputBuffer, node).buildTree();
//   }
   private Node lastChild(Node node) {
      Node child = node.getFirstChild();
      if (child != null) {
         while (child.getSibling() != null) {
            child = child.getSibling();
         }
      }
      return child;
   }

   private Node removeNode(Node parent, Node left, Node node) {
      if (node.getFirstChild() != null) {
         if (node.getSibling() != null) {
            lastChild(node).setSibling(node.getSibling());
         }
         if (left == null) {
            parent.setFirstChild(node.getFirstChild());
         } else {
            left.setSibling(node.getFirstChild());
         }
         return node.getFirstChild();
      } else if (left == null) {
         parent.setFirstChild(node.getSibling());
      } else {
         left.setSibling(node.getSibling());
      }
      return node.getSibling();
   }

   private void removeSkipedNodes(Node node) {
      if (node != null) {
         Node leftNode = null;
         Node child = node.getFirstChild();
         while (child != null) {
            if (child.isSkiped()) {
               child = removeNode(node, leftNode, child);
            } else {
               removeSkipedNodes(child);
               leftNode = child;
               child = child.getSibling();
            }
         }
      }
   }

   class GrammarConverterVisitor extends JavaVisitor {

      private InputBuffer inputBuffer;

      private Deque<List<GrammarRule>> rules = new ArrayDeque<List<GrammarRule>>();

      private boolean ruleDeclaration = false;

      public GrammarConverterVisitor(InputBuffer InputBuffer) {
         this.inputBuffer = InputBuffer;
      }

      @Override
      public void enterMethodDeclaration(Node node) {
         List<Node> children = node.getSemanticChildren();
         Node signatureNode = children.get(1);
         String methodReturn;
         children = signatureNode.getSemanticChildren();
         methodReturn = children.get(0).getText(inputBuffer).toString().trim();
         if (methodReturn.equalsIgnoreCase("Rule")) {
            rules.push(new ArrayList<GrammarRule>());
            ruleDeclaration = true;
         }
      }

      @Override
      public void exitMethodDeclaration(Node node) {
         if (ruleDeclaration) {
            List<Node> children = node.getSemanticChildren();
            Node signatureNode = children.get(1);
            String methodReturn;
            ruleDeclaration = false;
            children = signatureNode.getSemanticChildren();
            methodReturn = children.get(0).getText(inputBuffer).toString().trim();
            if (methodReturn.equalsIgnoreCase("Rule")) {
               final String ruleName = children.get(1).getText(inputBuffer).toString();
               final NonTerminalRule rule = new NonTerminalRule(ruleName, rules.pop().get(0));
               System.out.println(rule + " : " + rule.getRule() + ";");
               System.out.println();
            }
            rules.clear();
         }
      }

      @Override
      public void enterMethodCall(Node node) {
         if (ruleDeclaration) {
            rules.push(new ArrayList<GrammarRule>());
         }
      }

      @Override
      public void exitMethodCall(Node node) {
         if (ruleDeclaration) {
            final List<Node> children = node.getSemanticChildren();
            final String methodName = children.get(0).getText(inputBuffer).toString();
            if ("Sequence".equals(methodName)) {
               final GrammarRule andRule = new AndRule(rules.pop());
               rules.peek().add(andRule);
            } else if ("ZeroOrMore".equals(methodName)) {
               final List<GrammarRule> rulesList = rules.pop();
               if (rulesList.size() > 1) {
                  rules.peek().add(new ZeroOrMoreRule(new AndRule(rulesList)));
               } else if (!rulesList.isEmpty()) {
                  rules.peek().add(new ZeroOrMoreRule(rulesList.get(0)));
               }
            } else if ("OneOrMore".equals(methodName)) {
               final List<GrammarRule> rulesList = rules.pop();
               if (rulesList.size() > 1) {
                  rules.peek().add(new OneOrMoreRule(new AndRule(rulesList)));
               } else if (!rulesList.isEmpty()) {
                  rules.peek().add(new OneOrMoreRule(rulesList.get(0)));
               }
            } else if ("FirstOf".equals(methodName)) {
               final GrammarRule orRule = new OrRule(rules.pop());
               rules.peek().add(orRule);
            } else if ("CharRange".equals(methodName)) {
               final List<GrammarRule> rulesList = rules.pop();
               rules.peek().add(new CharRangeRule(((CharRule) rulesList.get(0)).getCharacter(), ((CharRule) rulesList.get(1)).getCharacter()));
            } else if ("Optional".equals(methodName) || "OptionalLabel".equals(methodName)) {
               final List<GrammarRule> rulesList = rules.pop();
               if (rulesList.size() > 1) {
                  rules.peek().add(new OptionalRule(new AndRule(rulesList)));
               } else if (!rulesList.isEmpty()) {
                  rules.peek().add(new OptionalRule(rulesList.get(0)));
               }
            } else if ("Test".equals(methodName)) {
               final List<GrammarRule> rulesList = rules.pop();
               if (rulesList.size() > 1) {
                  rules.peek().add(new TestRule(new AndRule(rulesList)));
               } else if (!rulesList.isEmpty()) {
                  rules.peek().add(new TestRule(rulesList.get(0)));
               }
            } else if ("AnyOf".equals(methodName)) {
               final List<GrammarRule> rulesList = rules.pop();
               final List<GrammarRule> list = toCharRuleList(((IgnoreCaseStringRule) rulesList.get(0)).getText());
               rules.peek().add(new OrRule(list));
            } else if ("TestNot".equals(methodName)) {
               final List<GrammarRule> rulesList = rules.pop();
               if (rulesList.size() > 1) {
                  rules.peek().add(new TestNotRule(new AndRule(rulesList)));
               } else if (!rulesList.isEmpty()) {
                  rules.peek().add(new TestNotRule(rulesList.get(0)));
               }
            } else if ("Keyword".equals(methodName)
                    || "Ch".equals(methodName)
                    || "String".equals(methodName)) {
               final GrammarRule rule = rules.pop().get(0);
               rules.peek().add(rule);
            } else if ("skipNode".equals(methodName)
                    || "Operator".equals(methodName)
                    || "label".equals(methodName)
                    || "suppressSubnodes".equals(methodName)) {
               final List<GrammarRule> rulesList = rules.pop();
               if (rulesList.size() > 1) {
                  rules.peek().add(new AndRule(rulesList));
               } else if (rulesList.size() == 1) {
                  rules.peek().add(rulesList.get(0));
               }
            } else {
               rules.pop();
               rules.peek().add(new NonTerminalRule(methodName, null));
            }
         }
      }

      @Override
      public void exitStringLiteral(Node node) {
         if (ruleDeclaration && !rules.isEmpty()) {
            final String str = node.getText(inputBuffer).toString();
            rules.peek().add(new IgnoreCaseStringRule(str.substring(1, str.length() - 1)));
         }
      }

      @Override
      public void exitCharLiteral(Node node) {
         if (ruleDeclaration) {
            final String str = node.getText(inputBuffer).toString();
            final List<Character> list = toCharList(str.substring(1, str.length() - 1));
            rules.peek().add(new CharRule(list.get(0)));
         }
      }

      @Override
      public void enterIdentifier(Node node) {
         if (ruleDeclaration) {
            final String id = node.getText(inputBuffer).toString();
            if (id.equals("STAR")) {
               rules.peek().add(new CharRule('*'));
            } else if (id.equals("ARROBA")) {
               rules.peek().add(new CharRule('@'));
            } else if (id.equals("EXP")) {
               rules.peek().add(new StringRule("**"));
            } else if (id.equals("DIV")) {
               rules.peek().add(new CharRule('/'));
            } else if (id.equals("PLUS")) {
               rules.peek().add(new CharRule('+'));
            } else if (id.equals("MINUS")) {
               rules.peek().add(new CharRule('-'));
            } else if (id.equals("CONCAT")) {
               rules.peek().add(new AndRule(new CharRule('|'), new CharRule('|')));
            } else if (id.equals("MOD")) {
               rules.peek().add(new StringRule("mod"));
            } else if (id.equals("PRIOR")) {
               rules.peek().add(new StringRule("prior"));
            } else if (id.equals("CONNECT_BY_ROOT")) {
               rules.peek().add(new StringRule("connect_by_root"));
            } else if (id.equals("EQUAL")) {
               rules.peek().add(new AndRule(new CharRule('='), new TestNotRule(new CharRule('>'))));
            } else if (id.equals("DIFFERENT")) {
               rules.peek().add(new OrRule(new AndRule(new CharRule('<'), new CharRule('>')),
                       new AndRule(new CharRule('!'), new CharRule('=')),
                       new AndRule(new CharRule('^'), new CharRule('=')),
                       new AndRule(new CharRule('~'), new CharRule('='))));
            } else if (id.equals("GREATER_EQUAL")) {
               rules.peek().add(new AndRule(new CharRule('>'), new CharRule('=')));
            } else if (id.equals("LESS_EQUAL")) {
               rules.peek().add(new AndRule(new CharRule('<'), new CharRule('=')));
            } else if (id.equals("GREATER")) {
               rules.peek().add(new AndRule(new CharRule('>'), new TestNotRule(new CharRule('='))));
            } else if (id.equals("LESS")) {
               rules.peek().add(new AndRule(new CharRule('<'), new TestNotRule(new OrRule(new CharRule('='), new CharRule('>')))));
            } else if (id.equals("SINGLE_QUOTE")) {
               rules.peek().add(new CharRule('\''));
            } else if (id.equals("NAMED_OPERATOR")) {
               rules.peek().add(new AndRule(new CharRule('='), new CharRule('>')));
            } else if (id.equals("TRUE")) {
               rules.peek().add(new AndRule(new IgnoreCaseStringRule("true"), new TestNotRule(new NonTerminalRule("IdentifierChar", null))));
            } else if (id.equals("FALSE")) {
               rules.peek().add(new AndRule(new IgnoreCaseStringRule("false"), new TestNotRule(new NonTerminalRule("IdentifierChar", null))));
            } else if (id.equals("DOT_DOT")) {
               rules.peek().add(new IgnoreCaseStringRule(".."));
            } else if (id.equals("ASSIGN")) {
               rules.peek().add(new IgnoreCaseStringRule(":="));
            } else if (id.equals("LABEL_START")) {
               rules.peek().add(new IgnoreCaseStringRule("<<"));
            } else if (id.equals("LABEL_END")) {
               rules.peek().add(new IgnoreCaseStringRule(">>"));
            } else if (id.equals("NAMED_PARAMETER")) {
               rules.peek().add(new IgnoreCaseStringRule("=>"));
            }
         }
      }

      private List<GrammarRule> toCharRuleList(String str) {
         final List<GrammarRule> list = new ArrayList<GrammarRule>();
         final List<Character> charList = toCharList(str);
         for (char c : charList) {
            list.add(new CharRule(c));
         }
         return list;
      }

      private List<Character> toCharList(String str) {
         final List<Character> list = new ArrayList<Character>();
         int i = 0;
         while (i < str.length()) {
            final int j = i + 1;
            if (str.charAt(i) == '\\' && j < str.length()) {
               switch (str.charAt(j)) {
                  case '\\':
                     list.add('\\');
                     i += 2;
                     break;
                  case 'n':
                     list.add('\n');
                     i += 2;
                     break;
                  case 'r':
                     list.add('\r');
                     i += 2;
                     break;
                  case 't':
                     list.add('\t');
                     i += 2;
                     break;
                  case 'b':
                     list.add('\b');
                     i += 2;
                     break;
                  case 'f':
                     list.add('\f');
                     i += 2;
                     break;
                  case '\'':
                     list.add('\'');
                     i += 2;
                     break;
                  case '"':
                     list.add('"');
                     i += 2;
                     break;
                  default:
                     list.add('\\');
                     ++i;
                     break;
               }
            } else {
               list.add(str.charAt(i++));
            }
         }
         return list;
      }
   }
}
