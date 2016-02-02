/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.runtime.java.node;

import org.uggeri.yapp.runtime.java.buffer.InputBuffer;
import org.uggeri.yapp.runtime.java.parser.Rule;
import java.util.List;

/**
 *
 * @author fabio
 * @param <T>
 */
public interface Node<T> extends Iterable<Node<T>> {

   public T getValue();

   public void setValue(T value);

   public Rule getRule();

   public int getStartIndex();

   public void setStartIndex(int index);

   public int getEndIndex();

   public void setEndIndex(int index);

   public CharSequence getText(InputBuffer inputBuffer);

   public void addChildrenNodes(Node<T> node);

   public void addChild(Node<T> node);

   public List<Node<T>> getChildren();

   public List<Node<T>> getSemanticChildren();

   public boolean isSemantic();

   public boolean isSkiped();

   public void setFirstChild(Node<T> node);

   public Node<T> getFirstChild();

   public void setSibling(Node<T> node);

   public Node<T> getSibling();

   public Iterable<Node<T>> children();
}
