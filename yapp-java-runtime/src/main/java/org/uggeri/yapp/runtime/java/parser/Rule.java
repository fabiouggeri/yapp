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
 * @param <T>
 */
public interface Rule<T extends NodeVisitor> {

   public String getLabel();

   public int getValue();

   public boolean isAtomic();

   public boolean isSkiped();

   public void enterRule(T visitor, Node node);

   public void exitRule(T visitor, Node node);

}
