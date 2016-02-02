/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.runtime.java.parser;

import org.uggeri.yapp.runtime.java.node.Node;
import org.uggeri.yapp.runtime.java.node.NodeVisitor;

/**
 *
 * @author fabio
 */
public class ParseTreeWalker {

   public void walk(Node<?> node, NodeVisitor visitor) {
      Node<?> nextNode = node.getFirstChild();
      node.getRule().enterRule(visitor, node);
      while (nextNode != null) {
         walk(nextNode, visitor);
         nextNode = nextNode.getSibling();
      }
      node.getRule().exitRule(visitor, node);
   }
}
