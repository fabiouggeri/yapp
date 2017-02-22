/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.generation.stmt;

import java.util.List;

/**
 *
 * @author fabio
 */
public interface BlockStatement extends Statement {
   
   public VariableDeclaration declareVariable(final DataType dataType, final String name);   
   public VariableDeclaration declareVariable(final DataType dataType, final String name, final Object initialValue);
   public void incVar(final Expression var);
   public void decVar(final Expression var);
   public void setValue(final Expression left, final Object right);
   public void statementExpression(final Object expr);
   public void returnStatement(final Object expr);
   public void lineComment(final String any);
   public void blockComment(final String any);
   public IfStatement ifStatement(final Object exprTest);
   public ElseIfStatement elseIfStatement(final Object exprTest);
   public ElseStatement elseStatement();
   public DoWhileStatement doWhileStatement(final Object exprTest);
   public WhileStatement whileStatement(final Object exprTest);
   public SwitchStatement switchStatement(final Object exprTest);
   public List<Statement> listStatements();
   public ForStatement forStatement(Object initExpr, Object testExpr, Object incExpr);
   public void functionCall(String functionName, Object... parameters);
   
}
