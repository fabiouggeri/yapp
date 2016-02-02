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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
      //new TesteExecucao().testeExecucaoJava();
      //new TesteExecucao().testeExecucaoHarbourPP();
      new TesteExecucao().testeExecucaoHarbourUnprocessed();
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
         totalTime += parseJavaFile(file, rulesProfiles);
         countLinesJavaFile(file);
      }
      return totalTime;
   }

   private void countLinesJavaFile(File file) {
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

   private void testeExecucaoJava() {
      try {
         TraceParser traceFile = new LimitedTraceFile(new File("c:\\temp\\yapp.trc"), 100 * 1024 * 1024);
         //TraceParser traceFile = new TraceStandardOutput();
         InputBuffer inputBuffer;
         JavaParser javaParser = new JavaParser();
         Node node;
         long iniParse;
         javaParser.setTraceParser(traceFile);
         javaParser.setTrace(true);
         //javax.swing.JOptionPane.showMessageDialog(null, "Tecla para continuar");
//         System.out.println("1. Mem. Livre: " + Runtime.getRuntime().freeMemory() / 1024.0 / 1024.0 + "MB");
         iniParse = System.currentTimeMillis();
         inputBuffer = new FileInputBuffer(new File("C:\\temp\\jdk-src\\sun\\java2d\\SunGraphics2D.java"));
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
}
