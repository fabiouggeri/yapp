/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.runtime.java.node;

import org.uggeri.yapp.runtime.java.buffer.InputBuffer;
import org.uggeri.yapp.runtime.java.parser.Rule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author fabio
 * @param <T>
 */
public class NodeImpl<T> implements Node<T> {

   private final Rule rule;

   private T value = null;

   private final boolean semantic;

   private final boolean skiped;

   private int startIndex;

   private int endIndex;

   public Node<T> firstChild = null;

   public Node<T> sibling = null;

   public NodeImpl(Rule rule, int startIndex, int endIndex, boolean semantic, boolean skiped) {
      this.rule = rule;
      this.semantic = semantic;
      this.skiped = skiped;
      this.startIndex = startIndex;
      this.endIndex = endIndex;
   }

   @Override
   public Rule getRule() {
      return rule;
   }

   @Override
   public T getValue() {
      return value;
   }

   @Override
   public void setValue(T value) {
      this.value = value;
   }

   @Override
   public int getStartIndex() {
      return startIndex;
   }


   @Override
   public void setStartIndex(int index) {
      this.startIndex = index;
   }

   @Override
   public int getEndIndex() {
      return endIndex;
   }

   @Override
   public void setEndIndex(int index) {
      this.endIndex = index;
   }

   @Override
   public CharSequence getText(InputBuffer buffer) {
      return buffer.getText(startIndex, endIndex);
   }

   @Override
   public void addChild(Node<T> node) {
      if (firstChild == null) {
         firstChild = node;
      } else {
         lastChild().setSibling(node);
      }
   }

   private Node<T> lastChild() {
      Node<T> lastChild = firstChild;
      while (lastChild.getSibling() != null) {
         lastChild = lastChild.getSibling();
      }
      return lastChild;
   }

   @Override
   public Iterator<Node<T>> iterator() {
      return new NodeIterator<T>(this);
   }

   @Override
   public void addChildrenNodes(Node<T> node) {
      Node<T> lastChild = lastChild();
      for (Node<T> child : node.children()) {
         if (lastChild == null) {
            lastChild = child;
         } else {
            lastChild.setSibling(child);
            lastChild = child;
         }
      }
   }

   @Override
   public List<Node<T>> getChildren() {
      if (firstChild == null) {
         return Collections.emptyList();
      } else {
         final List<Node<T>> nodeList = new ArrayList<Node<T>>();
         Node<T> nextNode = firstChild;
         while (nextNode != null) {
            nodeList.add(nextNode);
            nextNode = nextNode.getSibling();
         }
         return nodeList;
      }
   }

   @Override
   public List<Node<T>> getSemanticChildren() {
      if (firstChild == null) {
         return Collections.emptyList();
      } else {
         final List<Node<T>> nodeList = new ArrayList<Node<T>>();
         Node<T> nextNode = firstChild;
         while (nextNode != null) {
            if (nextNode.isSemantic()) {
               nodeList.add(nextNode);
            }
            nextNode = nextNode.getSibling();
         }
         return nodeList;
      }
   }

   @Override
   public boolean isSemantic() {
      return semantic;
   }

   @Override
   public boolean isSkiped() {
      return skiped;
   }

   public void enterNode(NodeVisitor visitor) {
      rule.enterRule(visitor, this);
   }

   public void exitNode(NodeVisitor visitor) {
      rule.exitRule(visitor, this);
   }

   @Override
   public String toString() {
      return rule.getLabel() + "(" + startIndex + ":" + endIndex + ")";
   }

   @Override
   public Node<T> getFirstChild() {
      return firstChild;
   }

   @Override
   public void setFirstChild(Node<T> node) {
      this.firstChild = node;
   }

   @Override
   public void setSibling(Node<T> node) {
      this.sibling = node;
   }

   @Override
   public Node<T> getSibling() {
      return sibling;
   }

   @Override
   public Iterable<Node<T>> children() {
      return firstChild;
   }

   private static class NodeIterator<T> implements Iterator<Node<T>> {

      private Node<T> nextNode;

      public NodeIterator(Node<T> currentNode) {
         this.nextNode = currentNode;
      }

      @Override
      public boolean hasNext() {
         return nextNode != null;
      }

      @Override
      public Node<T> next() {
         final Node<T> currNode = nextNode;
         nextNode = nextNode.getSibling();
         return currNode;
      }

   }
}
