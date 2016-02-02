/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.generation;

import org.uggeri.yapp.generation.stmt.BlockStatement;
import org.uggeri.yapp.generation.stmt.LineComment;
import org.uggeri.yapp.generation.stmt.ConditionalBlockStatement;
import org.uggeri.yapp.generation.stmt.DoWhileStatement;
import org.uggeri.yapp.generation.stmt.DoubleOperandExpression;
import org.uggeri.yapp.generation.stmt.ElseIfStatement;
import org.uggeri.yapp.generation.stmt.ElseStatement;
import org.uggeri.yapp.generation.stmt.StatementCode;
import org.uggeri.yapp.generation.stmt.Expression;
import org.uggeri.yapp.generation.stmt.IfStatement;
import org.uggeri.yapp.generation.stmt.LiteralExpression;
import org.uggeri.yapp.generation.stmt.ReturnStatement;
import org.uggeri.yapp.generation.stmt.SingleOperandExpression;
import org.uggeri.yapp.generation.stmt.Statement;
import org.uggeri.yapp.generation.stmt.SwitchOption;
import org.uggeri.yapp.generation.stmt.SwitchStatement;
import org.uggeri.yapp.generation.stmt.Variable;
import org.uggeri.yapp.generation.stmt.VariableDeclaration;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author fabio
 */
public abstract class AbstractCodeOptimizer implements CodeOptimizer {

   private final Deque<Set<Variable>> localVariables = new LinkedList<Set<Variable>>();

   @Override
   public void optimize(BlockStatement codeBlock) {
      removeUselessCode(codeBlock, 0);
   }
   
   private boolean isVariableDeclaration(Statement code) {
      return code instanceof StatementCode && ((StatementCode) code).getCode() instanceof VariableDeclaration;
   }

   private void removeUselessCode(final BlockStatement codeBlock, final int level) {
      List<Statement> codeList = codeBlock.listStatements();
      int index;
      do {
         blockEnter();
         for (index = 0; index < codeList.size(); index++) {
            final Statement code = codeList.get(index);
            if (isVariableDeclaration(code)) {
               localVariableDeclaration((VariableDeclaration) ((StatementCode)code).getCode());
            } else if (removeUnusedAssignment(codeList, index, code, level)) {
               break;
            } else if (removeUnnecessaryTest(codeList, index, code)) {
               break;
            } else if (removeUnusedVariable(codeList, index, code)) {
               break;
            }
         }
      } while (index < codeList.size());
      for (final Statement code : codeList) {
         if (code instanceof BlockStatement) {
            removeUselessCode((BlockStatement) code, level + 1);
         }
      }
      blockExit();
   }

   private void blockExit() {
      localVariables.pop().clear();
   }

   private void blockEnter() {
      localVariables.push(new HashSet<Variable>());
   }

   private boolean isVariableAssignment(Statement code) {
      if (code instanceof StatementCode && ((StatementCode)code).getCode() instanceof Expression) {
         Expression exp = (Expression)((StatementCode)code).getCode();
         return exp instanceof DoubleOperandExpression && ((DoubleOperandExpression)exp).getLeftExpression() instanceof Variable;
      }
      return false;
   }
   
   private Variable getVariable(Statement code) {
      return (Variable)((DoubleOperandExpression)((StatementCode)code).getCode()).getLeftExpression();
   }
   
   private Expression getAssignedExpr(Statement code) {
      return ((DoubleOperandExpression)((StatementCode)code).getCode()).getRightExpression();
   }
   
   private boolean isVariableUsed(Statement code, Variable var) {
      /* Comentarios nao referenciam variaveis */
      if (code instanceof LineComment) {
         return false;
      } else if (code instanceof VariableDeclaration) {
         /* Declaracao de variaveis somente referenciam a variavel se houver uma 
          inicializacao e a variavel em questao fizer parte da expressao. */
         return ((VariableDeclaration) code).getInitializationExpression() != null
                 && ((VariableDeclaration) code).getInitializationExpression().containsVariableReference(var.getName());
      } else if (code instanceof StatementCode && ((StatementCode) code).getCode() instanceof Expression) {
         /* Se for uma expressao verifica se o nome da variavel esta contida nela */
         return ((Expression)((StatementCode) code).getCode()).containsVariableReference(var.getName());

         /* Se for um bloco de codigo, verifica cada um de seus elementos */
      } else if (code instanceof BlockStatement) {
         if (code instanceof ConditionalBlockStatement) {
            if (((ConditionalBlockStatement) code).getTestExpression().containsVariableReference(var.getName())) {
               return true;
            }
         }
         for (Statement c : ((BlockStatement) code).listStatements()) {
            if (isVariableUsed(c, var)) {
               return true;
            }
         }
         return false;

         /* Se for uma atribuicao a variavel, entao verifica se a expressao contem o nome da variavel em questao */
      } else if (isVariableAssignment(code)) {
         return getVariable(code).equals(var) || getAssignedExpr(code).containsVariableReference(var.getName());

      } else if (code instanceof ReturnStatement) {
         return ((ReturnStatement)code).getExpression().containsVariableReference(var.getName());
         
      } else if (code instanceof SwitchStatement) {
         for (SwitchOption o : ((SwitchStatement) code).listOptions()) {
            if (isVariableUsed(o, var)) {
               return true;
            }
         }
         return false;
      }
      return true;
   }

   private boolean isNextStatementAssignVar(List<Statement> codeList, int index, Variable var) {
      if (++index < codeList.size()) {
         Statement code;
         /* Proxima instrucao eh uma atribuicao pra mesma variavel */
         do {
            code = codeList.get(index++);
         } while (!isVariableUsed(code, var) && index < codeList.size());
         /* Eh uma atribuicao? */
         if (isVariableAssignment(code)) {
            if (getVariable(code).equals(var) && ! getAssignedExpr(code).containsVariableReference(var.getName())) {
               return true;
            }
         } else if (code instanceof DoWhileStatement) {
            return isNextStatementAssignVar(((DoWhileStatement) code).listStatements(), 0, var);
         }
      }
      return false;
   }

   /* Extrai o nome da variavel de um teste simples tal como: (match) OU (! match) */
   private Variable getVarNameAffirmationTest(ConditionalBlockStatement codeBlock) {
      if (codeBlock.getTestExpression() instanceof Variable) {
         return (Variable) codeBlock.getTestExpression();
      } else {
         return null;
      }
   }

   /* Extrai o nome da variavel de um teste simples tal como: (match) OU (! match) */
   private Variable getVarNameNegationTest(ConditionalBlockStatement codeBlock) {
      if (codeBlock.getTestExpression() instanceof SingleOperandExpression) {
         if (((SingleOperandExpression) codeBlock.getTestExpression()).getOperator().equals(SingleOperandExpression.Operator.NEGATE) && 
                 ((SingleOperandExpression) codeBlock.getTestExpression()).getExpression() instanceof Variable) {
            return (Variable) ((SingleOperandExpression) codeBlock.getTestExpression()).getExpression();
         }
      }
      return null;
   }

   private Expression lastAssignedLiteral(List<Statement> codeList, int index, Variable var) {
      while (--index >= 0) {
         final Statement code = codeList.get(index);
         /* Verifica se eh uma atribuicao a variavel */
         if (isVariableAssignment(code)) {
            /* Se for, verifica se esta atribuindo a veriavel sendo pesquisada */
            if (getVariable(code).equals(var)) {
               /* Caso positivo, verifica se esta atribuindo uma constante */
               if (trueValue().equals(getAssignedExpr(code)) || falseValue().equals(getAssignedExpr(code))) {
                  return getAssignedExpr(code);
               }
               break;
            }
            /* Se for um bloco de codigo retorna null, pois podera haver alguma alteracao a essa variavel dentro do bloco.
             OBS: Isso pode ser evoluido para verificar se ha alguma atribuicao... */
         } else if (code instanceof BlockStatement) {
            break;
         }
      }
      return null;
   }

   private boolean isElseOrElseIf(List<Statement> codeList, int index) {
      if (index < codeList.size()) {
         final Statement code = codeList.get(index);
         if (code instanceof ElseIfStatement || code instanceof ElseStatement) {
            return true;
         }
      }
      return false;
   }

   private boolean removeUnnecessaryTest(List<Statement> codeList, int index, Statement code) {
      if (index > 0 && code instanceof IfStatement) {
         Variable var = getVarNameAffirmationTest(((ConditionalBlockStatement) code));
         if (var != null) {
            final Expression lastAssignedLiteral = lastAssignedLiteral(codeList, index, var);
            if (trueValue().equals(lastAssignedLiteral)) {
               do {
                  codeList.remove(index);
               } while (isElseOrElseIf(codeList, index));
               codeList.addAll(index, ((BlockStatement) code).listStatements());
               return true;
            } else if (falseValue().equals(lastAssignedLiteral)) {
               codeList.remove(index);
               if (isElseOrElseIf(codeList, index)) {
                  final Statement nextCode = codeList.get(index);
                  if (nextCode instanceof ElseStatement) {
                     codeList.remove(index);
                     codeList.addAll(index, ((BlockStatement) nextCode).listStatements());
                  } else if (nextCode instanceof ElseIfStatement) {
                     final IfStatement newCode = createIfStatement(((ElseIfStatement) nextCode).getTestExpression());
                     newCode.listStatements().addAll(((BlockStatement) nextCode).listStatements());
                     codeList.set(index, newCode);
                  }
               }
               return true;
            }
         } else {
            var = getVarNameNegationTest(((ConditionalBlockStatement) code));
            if (var != null) {
               final Expression lastAssignedLiteral = lastAssignedLiteral(codeList, index, var);
               if (falseValue().equals(lastAssignedLiteral)) {
                  do {
                     codeList.remove(index);
                  } while (isElseOrElseIf(codeList, index));
                  codeList.addAll(index, ((IfStatement) code).listStatements());
                  return true;
               } else if (trueValue().equals(lastAssignedLiteral)) {
                  codeList.remove(index);
                  if (isElseOrElseIf(codeList, index)) {
                     final Statement nextCode = codeList.get(index);
                     if (nextCode instanceof ElseStatement) {
                        codeList.remove(index);
                        codeList.addAll(index, ((BlockStatement) nextCode).listStatements());
                     } else if (nextCode instanceof ElseIfStatement) {
                        final IfStatement newCode = createIfStatement(((ElseIfStatement) nextCode).getTestExpression());
                        newCode.listStatements().addAll(((BlockStatement) nextCode).listStatements());
                        codeList.set(index, newCode);
                     }
                  }
                  return true;
               }
            }
         }
      }
      return false;
   }

   private boolean removeUnusedAssignment(List<Statement> codeList, int index, Statement code, int level) {
      if (isVariableAssignment(code)) {
         if (isLocalVariable(getVariable(code))) {
            if (isNextStatementAssignVar(codeList, index, getVariable(code))) {
               if (isLiteral(getAssignedExpr(code))) {
                  codeList.remove(index);
               } else {
                  codeList.set(index, createExecutionStatement(getAssignedExpr(code)));
               }
               return true;
            } else if (level == 0 && !isVariableUsedForward(codeList, index, getVariable(code))) {
               codeList.remove(index);
               return true;
            }
         }
      }
      return false;
   }

   private boolean isVariableUsedForward(List<Statement> codeList, int index, Variable var) {
      while (++index < codeList.size()) {
         final Statement code = codeList.get(index);
         if (isVariableUsed(code, var)) {
            return true;
         }
      }
      return false;
   }

   private boolean removeUnusedVariable(List<Statement> codeList, int index, Statement code) {
      if (code instanceof VariableDeclaration) {
         if (!isVariableUsedForward(codeList, index, ((VariableDeclaration) code).getVariable())) {
            codeList.remove(index);
            return true;
         }
      }
      return false;
   }

   private boolean isLiteral(Expression expr) {
      return trueValue().equals(expr) || falseValue().equals(expr);
   }

   private void localVariableDeclaration(VariableDeclaration varDeclaration) {
      localVariables.peek().add(varDeclaration.getVariable());
   }

   private boolean isLocalVariable(final Variable var) {
      Iterator<Set<Variable>> it = localVariables.iterator();
      while (it.hasNext()) {
         if (it.next().contains(var)) {
            return true;
         }
      }
      return false;
   }

   protected abstract IfStatement createIfStatement(Expression expr);

   protected abstract StatementCode createExecutionStatement(Expression expr);

   protected abstract LiteralExpression trueValue();

   protected abstract LiteralExpression falseValue();
}
