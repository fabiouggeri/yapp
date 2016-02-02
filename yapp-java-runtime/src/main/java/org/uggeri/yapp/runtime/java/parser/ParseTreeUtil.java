/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.runtime.java.parser;

import org.uggeri.yapp.runtime.java.buffer.InputBuffer;
import org.uggeri.yapp.runtime.java.node.Node;
import java.util.List;

/**
 *
 * @author fabio
 */
public class ParseTreeUtil {
   
   public static void printTree(InputBuffer inputBuffer, Node node) {
      printTree(inputBuffer, node, 0);
   }
   
   private static void printTree(InputBuffer inputBuffer, Node node, int i) {
      for (int j = 0; j < i; j++) {
         System.out.print("   ");
      }
      System.out.print('[');
      System.out.print(node.getRule().getLabel());
      System.out.print("] '");
      System.out.print(node.getText(inputBuffer));
      System.out.println('\'');
      for (Node child: (List<Node>)node.getChildren()) {
         printTree(inputBuffer, child, i + 1);
      }
   }
   
   public static void printSemanticTree(InputBuffer inputBuffer, Node node) {
      printSemanticTree(inputBuffer, node, 0);
   }
   
   private static void printSemanticTree(InputBuffer inputBuffer, Node node, int i) {
      for (int j = 0; j < i; j++) {
         System.out.print("   ");
      }
      System.out.print('[');
      System.out.print(node.getRule().getLabel());
      System.out.print("] '");
      System.out.print(node.getText(inputBuffer));
      System.out.println('\'');
      for (Node child: (List<Node>)node.getSemanticChildren()) {
         printSemanticTree(inputBuffer, child, i + 1);
      }
   }
}
