/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.uggeri.yapp.generation.java.stmt;

import org.uggeri.yapp.generation.stmt.BlockStatement;
import org.uggeri.yapp.generation.stmt.CodeFragment;
import org.uggeri.yapp.generation.stmt.DataType;
import org.uggeri.yapp.generation.stmt.DoWhileStatement;
import org.uggeri.yapp.generation.stmt.ElseIfStatement;
import org.uggeri.yapp.generation.stmt.ElseStatement;
import org.uggeri.yapp.generation.stmt.Expression;
import org.uggeri.yapp.generation.stmt.ForStatement;
import org.uggeri.yapp.generation.stmt.IfStatement;
import org.uggeri.yapp.generation.stmt.Statement;
import org.uggeri.yapp.generation.stmt.SwitchStatement;
import org.uggeri.yapp.generation.stmt.VariableDeclaration;
import org.uggeri.yapp.generation.stmt.WhileStatement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author fabio
 */
public class JavaBlockStatement extends AbstractJavaCodeFragment implements BlockStatement {
   
   final private List<Statement> statements = new ArrayList<Statement>();

   @Override
   public VariableDeclaration declareVariable(final DataType type, final String name) {
      VariableDeclaration varDecl = new JavaVariableDeclaration(type, name);
      statements.add(new JavaStatementCode(varDecl));
      return varDecl;
   }

   @Override
   public VariableDeclaration declareVariable(final DataType type, final String name, final Object initialValue) {
      VariableDeclaration varDecl = new JavaVariableDeclaration(type, name, asExpression(initialValue));
      statements.add(new JavaStatementCode(varDecl));
      return varDecl;
   }

   @Override
   public void setValue(final Expression var, final Object expr) {
      statements.add(new JavaStatementCode(var.assign(asExpression(expr))));
   }

   @Override
   public void statementExpression(final Object expr) {
      statements.add(new JavaStatementCode(asExpression(expr)));
   }

   @Override
   public void returnStatement(final Object expr) {
      statements.add(new JavaReturnStatement(asExpression(expr)));
   }

   @Override
   public IfStatement ifStatement(final Object exprTest) {
      final JavaIfStatement stmt = new JavaIfStatement(asExpression(exprTest));
      statements.add(stmt);
      return stmt;
   }

   @Override
   public ElseIfStatement elseIfStatement(Object exprTest) {
      JavaElseIfStatement stmt = new JavaElseIfStatement(asExpression(exprTest));
      addStatement(stmt);
      return stmt;
   }

   @Override
   public ElseStatement elseStatement() {
      JavaElseStatement stmt = new JavaElseStatement();
      addStatement(stmt);
      return stmt;
   }

   @Override
   public DoWhileStatement doWhileStatement(final Object exprTest) {
      final JavaDoWhileStatement stmt = new JavaDoWhileStatement(asExpression(exprTest));
      statements.add(stmt);
      return stmt;
   }

   @Override
   public WhileStatement whileStatement(final Object exprTest) {
      final JavaWhileStatement stmt = new JavaWhileStatement(asExpression(exprTest));
      statements.add(stmt);
      return stmt;
   }

   @Override
   public SwitchStatement switchStatement(Object exprTest) {
      final JavaSwitchStatement stmt = new JavaSwitchStatement(asExpression(exprTest));
      statements.add(stmt);
      return stmt;
   }

   @Override
   public ForStatement forStatement(Object initExpr, Object testExpr, Object incExpr) {
      final JavaForStatement stmt = new JavaForStatement(asExpression(initExpr), asExpression(testExpr), asExpression(incExpr));
      statements.add(stmt);
      return stmt;
   }

   @Override
   public void functionCall(String functionName, Object... parameters) {
      Expression[] arrayAux = new Expression[parameters.length];
      for (int i = 0; i < parameters.length; i++) {
         arrayAux[i] = asExpression(parameters[i]);
      }
      statements.add(new JavaStatementCode(new JavaFunctionCall(functionName, arrayAux)));
   }

   @Override
   public void lineComment(String anyText) {
      final JavaLineComment stmt = new JavaLineComment(anyText);
      statements.add(stmt);
   }

   @Override
   public void blockComment(String anyText) {
      final JavaBlockComment stmt = new JavaBlockComment(anyText);
      statements.add(stmt);
   }

   @Override
   public List<Statement> listStatements() {
      return statements;
   }

   protected void addStatement(Statement stmt) {
      statements.add(stmt);
   }

   protected void clearStatements() {
      statements.clear();
   }

   @Override
   public void appendCode(StringBuilder text, int indentation) {
      text.append(" {");
      for (CodeFragment code : statements) {
         code.appendCode(text, indentation + 3);
      }
      text.append('\n');
      indent(text, indentation).append("}");
   }
}
