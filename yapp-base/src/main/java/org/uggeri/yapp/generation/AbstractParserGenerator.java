/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.generation;

import org.uggeri.yapp.generation.visitors.LeftRecursionVerificationVisitor;
import org.uggeri.yapp.generation.visitors.FollowersLiteralsAppenderVisitor;
import org.uggeri.yapp.generation.visitors.PartialLiteralsBreakerVisitor;
import org.uggeri.yapp.generation.stmt.BlockStatement;
import org.uggeri.yapp.generation.stmt.DataType;
import org.uggeri.yapp.generation.stmt.Expression;
import org.uggeri.yapp.generation.stmt.FunctionCall;
import org.uggeri.yapp.generation.stmt.FunctionDefinition;
import org.uggeri.yapp.generation.stmt.FunctionDefinition.Modifier;
import org.uggeri.yapp.generation.stmt.IndexedExpression;
import org.uggeri.yapp.generation.stmt.LineComment;
import org.uggeri.yapp.generation.stmt.SwitchStatement;
import org.uggeri.yapp.generation.stmt.Variable;
import org.uggeri.yapp.generation.stmt.VariableDeclaration;
import org.uggeri.yapp.generation.visitors.FirstCharsVisitor;
import org.uggeri.yapp.generation.visitors.MemorizationRulesDefiner;
import org.uggeri.yapp.grammar.Grammar;
import org.uggeri.yapp.grammar.GrammarRuleVisitor;
import org.uggeri.yapp.grammar.rules.AndRule;
import org.uggeri.yapp.grammar.rules.AnyCharRule;
import org.uggeri.yapp.grammar.rules.CharRangeRule;
import org.uggeri.yapp.grammar.rules.CharRule;
import org.uggeri.yapp.grammar.rules.EOIRule;
import org.uggeri.yapp.grammar.rules.EmptyRule;
import org.uggeri.yapp.grammar.rules.GrammarRule;
import org.uggeri.yapp.grammar.rules.IgnoreCaseCharRule;
import org.uggeri.yapp.grammar.rules.IgnoreCaseStringRule;
import org.uggeri.yapp.grammar.rules.NonTerminalOption;
import org.uggeri.yapp.grammar.rules.NonTerminalRule;
import org.uggeri.yapp.grammar.rules.OneOrMoreRule;
import org.uggeri.yapp.grammar.rules.OptionalRule;
import org.uggeri.yapp.grammar.rules.OrRule;
import org.uggeri.yapp.grammar.rules.StringRule;
import org.uggeri.yapp.grammar.rules.TestNotRule;
import org.uggeri.yapp.grammar.rules.TestRule;
import org.uggeri.yapp.grammar.rules.ZeroOrMoreRule;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;
import org.uggeri.yapp.grammar.rules.ComposedGrammarRule;
import org.uggeri.yapp.grammar.rules.SimpleGrammarRule;

/**
 *
 * @author fabio 
 * TODO: verificar recursao a esquerda -> OK - Testar melhor 
 * TODO: verificar possibilidade de reordenar regras de acordo com a possibilidade de consumo de cadeias mais longas 
 * TODO: Gerar erro em regras opcoes redundantes de literais. ex.: ("e" | "E"), ('end' | 'endif' | 'end') 
 * TODO: Agrupar regras OR que comecam com literais. Ex.: ('AAA' S)|('BBB' S)|R => (('AAA'|'BBB') S) | R 
 * TODO: Tratar erros de sintaxe para tentar prosseguir com o parser. Ex.: "incluir" terminais esperados e nao encontrados,
 * remover terminais nao esperados
 */
public abstract class AbstractParserGenerator implements ParserGenerator, GrammarRuleVisitor {

   private static final String CURRENT_RULE_IS_ATOMIC_VAR_NAME = "currentRuleIsAtomic";

   private static final String INDEX_VAR_NAME = "index";

   private static final String TRACE_VAR_NAME = "trace";

   private static final String TRACE_PATH_VAR_NAME = "tracePath";

   private static final String BUFFER_VAR_NAME = "buffer";

   private static final String IGNORE_MISMATCH_VAR_NAME = "ignoreMismatch";

   private static final String CATCH_TERMINAL_MISMATCH_VAR_NAME = "catchTerminalMismatch";

   private static final String CURRENT_NODE_VAR_NAME = "currentNode";

   private static final String TAIL_RULE_VAR_NAME = "tailRule";

   protected static final String MEMO_INDEX_VAR_NAME = "memoIndex";

   protected static final String LAST_NODE_VAR_NAME = "lastNode";

   protected static final String LAST_INDEX_VAR_NAME = "lastRuleIndex";

   protected static final String LAST_TRY_VAR_NAME = "lastRuleTry";

   protected static final String START_INDEX_VAR_NAME = "startIndex";

   protected static final String MATCH_VAR_NAME = "match";

   protected static final String LAST_RULE_ATOMIC_VAR_NAME = "lastRuleIsAtomic";

   protected static final String START_TIME_VAR_NAME = "startTimeMillis";

   protected static final String OLD_CATCH_TERM_MISMATCH_VAR_NAME = "oldCatchTerminalMismatch";

   protected static final String OLD_IGNORE_MISMATCH_VAR_NAME = "oldIgnoreMismatch";

   protected static final String STR_VAR_NAME = "str";

   protected static final String STR_LEN_VAR_NAME = "strLen";

   protected static final String MIN_LEN_VAR_NAME = "minLen";

   protected static final String CHAR_INI_VAR_NAME = "charIni";

   protected static final String CHAR_END_VAR_NAME = "charEnd";

   private final List<NonTerminalRule> followLiterals = new ArrayList<NonTerminalRule>();

   private final Grammar grammar;

   private final ParserGenerationOptions options;

   private final Deque<BlockStatement> statements = new LinkedList<BlockStatement>();

   private final List<SourceFile> sourceFiles = new ArrayList<SourceFile>();

   private StringBuilder output = new StringBuilder(32768);

   private int numberMemorizations = 1;

   private int auxVarNumber;

   private boolean onlyTest = false;

   private boolean switchOption = false;

   private boolean inSwitch = false;

   private boolean atomicRule = false;

   protected final DataType CHAR = new DataTypeImpl("char");

   protected final DataType STRING = new DataTypeImpl("String");

   protected final DataType INTEGER = new DataTypeImpl("int");

   protected final DataType TIMEMILLIS = new DataTypeImpl("long");

   protected final DataType NODE = new DataTypeImpl("Node");

   protected final DataType BOOLEAN = new DataTypeImpl("boolean");

   protected final EnumSet<Modifier> FUNCTION_SCOPE = EnumSet.of(Modifier.PROTECTED);

   protected Variable parserVar;

   protected Variable startIndexVar;

   protected Variable matchVar;

   protected Expression currentRuleIsAtomicVar;

   protected Expression indexVar;

   protected Expression traceVar;

   protected Expression tracePathVar;

   protected Expression bufferVar;

   protected Expression ignoreMismatchVar;

   protected Expression catchTerminalMismatchVar;

   protected Expression currentNodeVar;

   protected Expression tailRuleVar;

   protected Variable oldIgnoreMismatchVar;

   protected Variable oldCatchTerminalMismatchVar;

   protected Variable lastRuleIsAtomicVar;

   protected Variable lastNodeVar;

   protected Variable memoIndexVar;

   protected Variable startTimeVar;

   private Variable strVar;

   private Variable strLenVar;

   private Variable minLenVar;

   private Variable charIniVar;

   private Variable charEndVar;

   private FunctionDefinition eoiFunction;

   private FunctionDefinition eoiTestFunction;

   private FunctionDefinition anyCharMatcherFunction;

   private FunctionDefinition anyCharTestFunction;

   private FunctionDefinition charRangeMatcherFunction;

   private FunctionDefinition charRangeTestFunction;

   private FunctionDefinition ignoreCaseStringMatcherFunction;

   private FunctionDefinition partialIgnoreCaseStringMatcherFunction;

   private FunctionDefinition ignoreCaseStringTestFunction;

   private FunctionDefinition partialIgnoreCaseStringTestFunction;

   private FunctionDefinition stringMatcherFunction;

   private FunctionDefinition partialStringMatcherFunction;

   private FunctionDefinition stringTestFunction;

   private FunctionDefinition partialStringTestFunction;

   private FunctionDefinition ignoreCaseCharMatcherFunction;

   private FunctionDefinition ignoreCaseCharTestFunction;

   private FunctionDefinition charMatcherFunction;

   private FunctionDefinition charTestFunction;

   private NonTerminalRule currentNonTerminalRule = null;

   private int currentOrOption = 0;

   public AbstractParserGenerator(Grammar grammar, ParserGenerationOptions options) {
      this.grammar = grammar;
      this.options = options;
      createVariables();
      initializeFunctions();
   }

   private void initializeFunctions() {
      eoiFunction = defineFunction(functionScope(), BOOLEAN, "eoi");
      eoiTestFunction = defineFunction(functionScope(), BOOLEAN, "eoiTest");
      anyCharMatcherFunction = defineFunction(functionScope(), BOOLEAN, "anyCharMatcher");
      anyCharTestFunction = defineFunction(functionScope(), BOOLEAN, "anyCharTest");
      charRangeMatcherFunction = defineFunction(functionScope(), BOOLEAN, "charRangeMatcher", charIniVar.declare(CHAR), charEndVar.declare(CHAR));
      charRangeTestFunction = defineFunction(functionScope(), BOOLEAN, "charRangeTest", charIniVar.declare(CHAR), charEndVar.declare(CHAR));
      ignoreCaseStringMatcherFunction = defineFunction(functionScope(), BOOLEAN, "ignoreCaseStringMatcher", strVar.declare(STRING), strLenVar.declare(INTEGER));
      partialIgnoreCaseStringMatcherFunction = defineFunction(functionScope(), BOOLEAN, "partialIgnoreCaseStringMatcher", strVar.declare(STRING), strLenVar.declare(INTEGER), minLenVar.declare(INTEGER));
      ignoreCaseStringTestFunction = defineFunction(functionScope(), BOOLEAN, "ignoreCaseStringTest", strVar.declare(STRING), strLenVar.declare(INTEGER));
      partialIgnoreCaseStringTestFunction = defineFunction(functionScope(), BOOLEAN, "partialIgnoreCaseStringTest", strVar.declare(STRING), strLenVar.declare(INTEGER), minLenVar.declare(INTEGER));
      stringMatcherFunction = defineFunction(functionScope(), BOOLEAN, "stringMatcher", strVar.declare(STRING), strLenVar.declare(INTEGER));
      partialStringMatcherFunction = defineFunction(functionScope(), BOOLEAN, "partialStringMatcher", strVar.declare(STRING), strLenVar.declare(INTEGER), minLenVar.declare(INTEGER));
      stringTestFunction = defineFunction(functionScope(), BOOLEAN, "stringTest", strVar.declare(STRING), strLenVar.declare(INTEGER));
      partialStringTestFunction = defineFunction(functionScope(), BOOLEAN, "partialStringTest", strVar.declare(STRING), strLenVar.declare(INTEGER), minLenVar.declare(INTEGER));
      ignoreCaseCharMatcherFunction = defineFunction(functionScope(), BOOLEAN, "ignoreCaseCharMatcher", var("c").declare(CHAR));
      ignoreCaseCharTestFunction = defineFunction(functionScope(), BOOLEAN, "ignoreCaseCharTest", var("c").declare(CHAR));
      charMatcherFunction = defineFunction(functionScope(), BOOLEAN, "charMatcher", var("c").declare(CHAR));
      charTestFunction = defineFunction(functionScope(), BOOLEAN, "charTest", var("c").declare(CHAR));
   }

   private void createVariables() {
      parserVar = var("this");
      startIndexVar = var(START_INDEX_VAR_NAME);
      matchVar = var(MATCH_VAR_NAME);
      currentRuleIsAtomicVar = parserVar.member(var(CURRENT_RULE_IS_ATOMIC_VAR_NAME));
      indexVar = parserVar.member(var(INDEX_VAR_NAME));
      traceVar = parserVar.member(var(TRACE_VAR_NAME));
      tracePathVar = parserVar.member(var(TRACE_PATH_VAR_NAME));
      bufferVar = parserVar.member(var(BUFFER_VAR_NAME));
      ignoreMismatchVar = parserVar.member(var(IGNORE_MISMATCH_VAR_NAME));
      catchTerminalMismatchVar = parserVar.member(var(CATCH_TERMINAL_MISMATCH_VAR_NAME));
      currentNodeVar = parserVar.member(var(CURRENT_NODE_VAR_NAME));
      tailRuleVar = parserVar.member(var(TAIL_RULE_VAR_NAME));
      oldIgnoreMismatchVar = var(OLD_IGNORE_MISMATCH_VAR_NAME);
      oldCatchTerminalMismatchVar = var(OLD_CATCH_TERM_MISMATCH_VAR_NAME);
      lastRuleIsAtomicVar = var(LAST_RULE_ATOMIC_VAR_NAME);
      lastNodeVar = var(LAST_NODE_VAR_NAME);
      memoIndexVar = var(MEMO_INDEX_VAR_NAME);
      startTimeVar = var(START_TIME_VAR_NAME);
      strVar = var(STR_VAR_NAME);
      strLenVar = var(STR_LEN_VAR_NAME);
      minLenVar = var(MIN_LEN_VAR_NAME);
      charIniVar = var(CHAR_INI_VAR_NAME);
      charEndVar = var(CHAR_END_VAR_NAME);
   }

   @Override
   public void generateParser() throws ParserGenerationException {
      validateOptions();
      validateGrammar(grammar);
      prepareRules(grammar);
      initializeCodeGeneration();
      generateAuxiliaryFunctions();
      generateTerminalFunctions();
      generateProfilesFunctions();
      generateErrorFunctions();
      generateTraceFunctions();
      generateParserFunctions();
      generateRulesFunctions(grammar, new HashSet<String>());
      finalizeCodeGeneration();
      saveSourceCode();
   }

   @Override
   public void validate() throws ParserGenerationException {
      validateGrammar(grammar);
   }

   protected abstract void initializeCodeGeneration() throws ParserGenerationException;

   protected abstract void generateAuxiliaryFunctions() throws ParserGenerationException;

   protected abstract void generateProfilesFunctions() throws ParserGenerationException;

   protected abstract void generateErrorFunctions() throws ParserGenerationException;

   protected abstract void generateTraceFunctions() throws ParserGenerationException;

   protected abstract void generateParserFunctions() throws ParserGenerationException;

   protected abstract void finalizeCodeGeneration() throws ParserGenerationException;

   protected abstract String sufixRuleName();

   protected abstract Variable var(String varName);

   protected abstract FunctionCall funCall(final String functionName, final Object... parameters);

   protected abstract LineComment lineComment(String text);

   protected abstract BlockStatement createBlockStatement();

   protected abstract String ruleName(Grammar grammar, NonTerminalRule rule);

   protected abstract Expression ruleReference(Grammar grammar, NonTerminalRule rule);

   protected abstract CodeOptimizer createOptimizer();

   protected abstract void setSibling(Variable nodeVar, Expression expr);

   protected abstract void setFirstChild(Expression nodeVar, Expression expr);

   protected abstract Expression getSibling(Variable nodeVar);

   protected abstract Expression getFirstChild(Expression nodeVar);

   protected abstract Expression timeFunCall();

   protected abstract Expression createNodeFunCall(Object rule, Object startIndex, Object index, Object semantic, Object skiped);

   protected abstract Expression createErrorFunCall(Object index, Object rule, Object expectedValue, Object tailRule);

   protected abstract void traceEnterRule(Expression tracePath, Expression buffer, Expression ruleExpr, Expression index);

   protected abstract void traceExitRule(Expression tracePath, Expression buffer, Expression ruleExpr, boolean success);

   protected abstract void setCurrentNode(Expression currentNode, Expression expr);

   protected abstract Expression terminalRule();

   protected abstract void incMatch(Variable profile, Expression expr);

   protected abstract void incMemoMatch(Variable profile, Expression expr);

   protected abstract void incMismatch(Variable profile, Expression expr);

   protected abstract void incMemoMismatch(Variable profile, Expression expr);

   protected abstract Variable profileVar(Grammar grammar, NonTerminalRule rule);

   protected abstract void setPreviousTailRule(Expression tailRule);

   protected abstract void setNextTailRule(Expression tailRule, Expression ruleReference);

   protected abstract Expression charSequence(Object... params);

   protected abstract Expression duplicateString(Object str, Object strLen, Object character);

   protected abstract Expression charAt(Variable strVar, Expression var);

   protected abstract void registerRuleMismatch(Expression index, Expression rule);

   protected abstract void registerTerminalMismatch(Expression index, Object expectedExpr);

   protected abstract void breakCommand();

   protected abstract FunctionDefinition defineFunction(EnumSet<Modifier> modifiers, DataType returnType, String functionName, VariableDeclaration... args);

   protected abstract String bufferGetCharFunctionName();

   protected abstract String bufferMatchCharFunctionName();

   protected abstract String bufferMatchIgnoreCaseCharFunctionName();

   protected abstract String bufferMatchCharRangeFunctionName();

   protected abstract String bufferMatchIgnoreCaseStringFunctioName();

   protected abstract String bufferMatchStringFunctionName();

   protected Grammar getGrammar() {
      return grammar;
   }

   protected ParserGenerationOptions getOptions() {
      return options;
   }

   protected List<SourceFile> getSourceFiles() {
      return sourceFiles;
   }

   public int getNumberMemorizations() {
      return numberMemorizations;
   }

   public void setNumberMemorizations(int numberMemorizations) {
      this.numberMemorizations = numberMemorizations;
   }

   protected StringBuilder getOutput() {
      return output;
   }

   protected void setOutput(StringBuilder output) {
      this.output = output;
   }

   private String removeEscapes(final String str) {
      return str.replace("\n", "\\n").replace("\r", "\\r").replace("\f", "\\f").replace("\t", "\\t");
   }

   protected StringBuilder indent(StringBuilder sb, int indent) {
      for (int i = 0; i < indent; i++) {
         sb.append(' ');
      }
      return sb;
   }

   protected boolean isMemoizeRule(NonTerminalRule rule) {
      return getOptions().getMemoizeMode().equals(MemoizeMode.ALL)
              || (rule.getOptions().containsKey(NonTerminalOption.MEMOIZE) && !getOptions().getMemoizeMode().equals(MemoizeMode.NONE));
   }

   private void generateRulesFunctions(Grammar grammar, Set<String> created) throws ParserGenerationException {
      generateGrammarRulesFunctions(grammar, created);
   }

   private void generateGrammarRulesFunctions(Grammar grammar, Set<String> generatedFunctions) throws ParserGenerationException {
      for (NonTerminalRule rule : grammar.getDeclaredRules()) {
         try {
            if (!generatedFunctions.contains(rule.getMethodName()) && !rule.getOptions().containsKey(NonTerminalOption.FRAGMENT)) {
               generatedFunctions.add(rule.getMethodName());
               generateRuleFunction(grammar, rule);
            }
         } catch (Exception ex) {
            throw new ParserGenerationException("Error generating rule " + rule.toString(), ex);
         }
      }
   }

   private void generateRuleFunction(final Grammar grammar, final NonTerminalRule rule) {
      boolean skipNode = rule.getOptions().containsKey(NonTerminalOption.SKIP_NODE);
      final boolean memoize;
      final boolean syntaxOnly = rule.getOptions().containsKey(NonTerminalOption.SYNTAX_ONLY);
      final boolean catchMismatch = rule.getOptions().containsKey(NonTerminalOption.CATCH_MISMATCH);
      auxVarNumber = 1;

      currentNonTerminalRule = rule;
      atomicRule = rule.getOptions().containsKey(NonTerminalOption.ATOMIC);
      memoize = isMemoizeRule(rule);
      output.append('\n');
      lineComment(rule.getName() + " : " + removeEscapes(rule.getRule().toString())).appendCode(output, getInitialIndentation());
      beginFunction(defineFunction(functionScope(), BOOLEAN, rule.getMethodName()));
      declareLocalVariables(grammar, rule, memoize, syntaxOnly, catchMismatch);
      traceCodeEnterRule(duplicateString(rule.getName(), rule.getName().length(), null));
      memoizationCode(grammar, rule, memoize, syntaxOnly, skipNode, catchMismatch);
      if (atomicRule) {
         setValue(currentRuleIsAtomicVar, true);
      }
      if (rule.hasLeftRecursion()) {
         currentOrOption = 1;
         ifStmt(ruleLastIndexVar(rule).equal(indexVar));
         stmt(ruleTryVar(rule).preInc());
         elseStmt();
         setValue(ruleLastIndexVar(rule), indexVar);
         setValue(ruleTryVar(rule), 0);
         endIf();
      } else {
         currentOrOption = 0;
      }
      if (!isTest(rule.getRule())) {
         setValue(startIndexVar, indexVar);
      }
      rule.getRule().visit(getOptions(), this);
      ifStmt(matchVar);
      matchExitRule(grammar, rule, memoize, skipNode, syntaxOnly, catchMismatch);
      elseStmt();
      mismatchExitRule(grammar, rule, memoize, syntaxOnly, catchMismatch);
      endIf();
      createOptimizer().optimize(getStatements().peek());
      endFunction();
   }

   protected EnumSet<Modifier> functionScope() {
      return FUNCTION_SCOPE;
   }

   protected void traceCodeEnterRule(Expression ruleExpr) {
      if (getOptions().isGenerateTraceCode()) {
         ifStmt(traceVar);
         traceEnterRule(tracePathVar, bufferVar, ruleExpr, indexVar);
         endIf();
      }
   }

   protected void traceCodeExitRule(boolean success) {
      if (getOptions().isGenerateTraceCode()) {
         ifStmt(traceVar);
         traceExitRule(tracePathVar, bufferVar, indexVar, success);
         endIf();
      }
   }

   private void captureErrorCodeExitRule(final Grammar grammar, NonTerminalRule rule, boolean syntaxOnly, boolean catchMismatch, final boolean match) {
      if (getOptions().isCatchMismatches()) {
         if (match) {
            if (atomicRule || syntaxOnly || catchMismatch) {
               if (syntaxOnly || catchMismatch) {
                  setValue(ignoreMismatchVar, oldIgnoreMismatchVar);
               }
               setValue(catchTerminalMismatchVar, oldCatchTerminalMismatchVar);
            }
         } else /* memoize */ if (atomicRule) {
            if (catchMismatch) {
               setValue(ignoreMismatchVar, oldIgnoreMismatchVar);
               catchMismatchCode(ruleReference(grammar, rule));
            } else if (syntaxOnly) {
               setValue(ignoreMismatchVar, oldIgnoreMismatchVar);
            } else {
               catchMismatchCode(ruleReference(grammar, rule));
            }
            setValue(catchTerminalMismatchVar, oldCatchTerminalMismatchVar);
         } else if (syntaxOnly || catchMismatch) {
            setValue(ignoreMismatchVar, oldIgnoreMismatchVar);
            setValue(catchTerminalMismatchVar, oldCatchTerminalMismatchVar);
            if (catchMismatch) {
               catchMismatchCode(ruleReference(grammar, rule));
            }
         }
         setPreviousTailRule(tailRuleVar);
      }
   }

   protected String varName(Expression expr) {
      if (expr instanceof IndexedExpression) {
         if (((IndexedExpression) expr).getArrayExpression() instanceof Variable) {
            return ((Variable) ((IndexedExpression) expr).getArrayExpression()).getName();
         }
      } else if (expr instanceof Variable) {
         return ((Variable) expr).getName();
      }
      return "";
   }

   protected Expression memoVarStart(final NonTerminalRule rule) {
      if (getNumberMemorizations() > 1) {
         return var(rule.getMethodName() + "MemoStart").get(memoIndexVar);
      } else {
         return var(rule.getMethodName() + "MemoStart");
      }
   }

   protected Expression memoVarEnd(final NonTerminalRule rule) {
      if (getNumberMemorizations() > 1) {
         return var(rule.getMethodName() + "MemoEnd").get(memoIndexVar);
      } else {
         return var(rule.getMethodName() + "MemoEnd");
      }
   }

   protected Expression memoVarFirstNode(final NonTerminalRule rule) {
      if (getNumberMemorizations() > 1) {
         return var(rule.getMethodName() + "MemoFirstNode").get(memoIndexVar);
      } else {
         return var(rule.getMethodName() + "MemoFirstNode");
      }
   }

   protected Expression ruleLastIndexVar(final NonTerminalRule rule) {
      return var(rule.getMethodName() + "LastIndex");
   }

   protected Expression ruleTryVar(final NonTerminalRule rule) {
      return var(rule.getMethodName() + "Try");
   }

   private void memoizationCode(final Grammar grammar, NonTerminalRule rule, boolean memoize, final boolean syntaxOnly, final boolean skipNode, final boolean catchMismatch) {
      if (memoize && !isTest(rule.getRule())) {
         ifStmt(parserVar.member(memoVarStart(rule)).equal(indexVar));
         ifStmt(parserVar.member(memoVarStart(rule)).lessOrEqual(parserVar.member(memoVarEnd(rule))));
         memorizedMatchExitRule(grammar, rule, syntaxOnly, skipNode, catchMismatch);
         elseStmt();
         memorizedMismatchExitRule(grammar, rule, syntaxOnly, catchMismatch);
         endIf();
         endIf();
      }
   }

   private void profileCodeExitRule(final Grammar grammar, final NonTerminalRule rule, final boolean memo, final boolean match) {
      if (getOptions().isProfile()) {
         if (memo) {
            if (match) {
               incMemoMatch(profileVar(grammar, rule), timeFunCall().minus(startTimeVar));
            } else {
               incMemoMismatch(profileVar(grammar, rule), timeFunCall().minus(startTimeVar));
            }
         } else if (match) {
            incMatch(profileVar(grammar, rule), timeFunCall().minus(startTimeVar));
         } else {
            incMismatch(profileVar(grammar, rule), timeFunCall().minus(startTimeVar));
         }
      }
   }

   private void declareLocalVariables(final Grammar grammar, final NonTerminalRule rule, final boolean memoize, final boolean syntaxOnly, final boolean catchMismatch) {
      if (rule.hasLeftRecursion()) {
         declareVarStmt(INTEGER, LAST_INDEX_VAR_NAME, ruleLastIndexVar(rule));
         declareVarStmt(INTEGER, LAST_TRY_VAR_NAME, ruleTryVar(rule));
      }
      if (!isTest(rule.getRule())) {
         declareVarStmt(NODE, LAST_NODE_VAR_NAME, currentNodeVar);
         declareVarStmt(INTEGER, START_INDEX_VAR_NAME);
      }
      if (memoize) {
         if (getNumberMemorizations() > 1) {
            declareVarStmt(INTEGER, MEMO_INDEX_VAR_NAME, indexVar.mod(getNumberMemorizations()));
         }
      }
      declareVarStmt(BOOLEAN, MATCH_VAR_NAME);
      if (atomicRule) {
         declareVarStmt(BOOLEAN, LAST_RULE_ATOMIC_VAR_NAME, currentRuleIsAtomicVar);
      }
      if (getOptions().isProfile()) {
         declareVarStmt(TIMEMILLIS, START_TIME_VAR_NAME, timeFunCall());
      }
      if (getOptions().isCatchMismatches()) {
         if (atomicRule || syntaxOnly || catchMismatch) {
            declareVarStmt(BOOLEAN, OLD_CATCH_TERM_MISMATCH_VAR_NAME, catchTerminalMismatchVar);
            if (syntaxOnly || catchMismatch) {
               declareVarStmt(BOOLEAN, OLD_IGNORE_MISMATCH_VAR_NAME, ignoreMismatchVar);
               setValue(ignoreMismatchVar, true);
            }
            setValue(catchTerminalMismatchVar, false);
         }
         setNextTailRule(tailRuleVar, ruleReference(grammar, rule));
      }
   }

   private void memorizedMatchExitRule(final Grammar grammar, final NonTerminalRule rule, final boolean syntaxOnly, final boolean skipNode, final boolean catchMismatch) {
      setValue(indexVar, parserVar.member(memoVarEnd(rule)));
      if (!isTest(rule.getRule())) {
         ifStmt(not(currentRuleIsAtomicVar));
         setValue(currentNodeVar, createNodeFunCall(ruleReference(grammar, rule), parserVar.member(memoVarStart(rule)), parserVar.member(memoVarEnd(rule)), !syntaxOnly, skipNode));
         setSibling(lastNodeVar, currentNodeVar);
         ifStmt(parserVar.member(memoVarFirstNode(rule)).diff(null));
         setFirstChild(currentNodeVar, getFirstChild(parserVar.member(memoVarFirstNode(rule))));
         endIf();
         endIf();
      }
      traceCodeExitRule(true);
      profileCodeExitRule(grammar, rule, true, true);
      captureErrorCodeExitRule(grammar, rule, syntaxOnly, catchMismatch, true);
      returnStmt(true);
   }

   private void matchExitRule(final Grammar grammar, final NonTerminalRule rule, final boolean memoize, final boolean skipNode, final boolean syntaxOnly, final boolean catchMismatch) {
      if (atomicRule) {
         setValue(currentRuleIsAtomicVar, lastRuleIsAtomicVar);
      }
      if (!isTest(rule.getRule())) {
         if (memoize) {
            setValue(parserVar.member(memoVarStart(rule)), startIndexVar);
            setValue(parserVar.member(memoVarEnd(rule)), indexVar);
            ifStmt(currentRuleIsAtomicVar);
            setValue(parserVar.member(memoVarFirstNode(rule)), null);
            elseStmt();
         } else {
            ifStmt(not(currentRuleIsAtomicVar));
         }
         setValue(currentNodeVar, createNodeFunCall(ruleReference(grammar, rule), startIndexVar, indexVar, !syntaxOnly, skipNode));
         /* quanto o noh eh atomico, nao possui subnos */
         if (!atomicRule) {
            setFirstChild(currentNodeVar, getSibling(lastNodeVar));
         }
         setSibling(lastNodeVar, currentNodeVar);
         if (memoize) {
            setValue(parserVar.member(memoVarFirstNode(rule)), currentNodeVar);
         }
         endIf();
      }
      traceCodeExitRule(true);
      profileCodeExitRule(grammar, rule, false, true);
      captureErrorCodeExitRule(grammar, rule, syntaxOnly, catchMismatch, true);
      if (rule.hasLeftRecursion()) {
         setValue(ruleLastIndexVar(rule), var(LAST_INDEX_VAR_NAME));
         setValue(ruleTryVar(rule), var(LAST_TRY_VAR_NAME));
      }
      returnStmt(true);
   }

   private void memorizedMismatchExitRule(final Grammar grammar, final NonTerminalRule rule, final boolean syntaxOnly, final boolean catchMismatch) {
      traceCodeExitRule(false);
      profileCodeExitRule(grammar, rule, true, false);
      captureErrorCodeExitRule(grammar, rule, syntaxOnly, catchMismatch, false);
      returnStmt(false);
   }

   private void mismatchExitRule(final Grammar grammar, final NonTerminalRule rule, final boolean memoize, final boolean syntaxOnly, final boolean catchMismatch) {
      if (atomicRule) {
         setValue(currentRuleIsAtomicVar, lastRuleIsAtomicVar);
      }
      captureErrorCodeExitRule(grammar, rule, syntaxOnly, catchMismatch, false);
      if (!isTest(rule.getRule())) {
         if (memoize) {
            setValue(parserVar.member(memoVarStart(rule)), startIndexVar);
            setValue(parserVar.member(memoVarEnd(rule)), -1);
            setValue(parserVar.member(memoVarFirstNode(rule)), null);
         }
         setValue(indexVar, startIndexVar);
         setSibling(lastNodeVar, null);
         setValue(currentNodeVar, lastNodeVar);
      }
      traceCodeExitRule(false);
      profileCodeExitRule(grammar, rule, false, false);
      if (rule.hasLeftRecursion()) {
         setValue(ruleLastIndexVar(rule), var(LAST_INDEX_VAR_NAME));
         setValue(ruleTryVar(rule), var(LAST_TRY_VAR_NAME));
      }
      returnStmt(false);
   }

   private void ruleComment(GrammarRule rule) {
      getStatements().peek().lineComment(" " + removeEscapes(rule.toString()));
   }

   @Override
   public void visitChar(ParserGenerationOptions options, CharRule rule) {
      ruleComment(rule);
      if (switchOption) {
         ifStmt(matchVar.assign(bufferVar.member(funCall(bufferMatchCharFunctionName(), indexVar, rule.getCharacter()))));
         stmt(indexVar.preInc());
         endIf();
      } else if (onlyTest) {
         setValue(matchVar, bufferVar.member(funCall(bufferMatchCharFunctionName(), indexVar, rule.getCharacter())));
      } else {
         setValue(matchVar, parserVar.member(charMatcherFunction.call(rule.getCharacter())));
      }
   }

   @Override
   public void visitCharIgnoreCase(ParserGenerationOptions options, IgnoreCaseCharRule rule) {
      ruleComment(rule);
      if (switchOption) {
         ifStmt(matchVar.assign(bufferVar.member(funCall(bufferMatchIgnoreCaseCharFunctionName(), indexVar, rule.getCharacter()))));
         stmt(indexVar.preInc());
         endIf();
      } else if (onlyTest) {
         setValue(matchVar, bufferVar.member(funCall(bufferMatchIgnoreCaseCharFunctionName(), indexVar, rule.getCharacter())));
      } else {
         setValue(matchVar, parserVar.member(ignoreCaseCharMatcherFunction.call(rule.getCharacter())));
      }
   }

   @Override
   public void visitCharRange(ParserGenerationOptions options, CharRangeRule rule) {
      ruleComment(rule);
      if (switchOption) {
         ifStmt(matchVar.assign(bufferVar.member(funCall(bufferMatchCharRangeFunctionName(), indexVar, rule.getStart(), rule.getEnd()))));
         stmt(indexVar.preInc());
         endIf();
      } else if (onlyTest) {
         setValue(matchVar, bufferVar.member(funCall(bufferMatchCharRangeFunctionName(), indexVar, rule.getStart(), rule.getEnd())));
      } else {
         setValue(matchVar, parserVar.member(charRangeMatcherFunction.call(rule.getStart(), rule.getEnd())));
      }
   }

   @Override
   public void visitAnyChar(ParserGenerationOptions options, AnyCharRule rule) {
      ruleComment(rule);
      if (switchOption) {
         ifStmt(matchVar.assign(bufferVar.member(funCall(bufferGetCharFunctionName(), indexVar)).diff('\0')));
         stmt(indexVar.preInc());
         endIf();
      } else if (onlyTest) {
         setValue(matchVar, bufferVar.member(funCall(bufferGetCharFunctionName(), indexVar)).diff('\0'));
      } else {
         setValue(matchVar, parserVar.member(anyCharMatcherFunction.call()));
      }
   }

   @Override
   public void visitString(ParserGenerationOptions options, StringRule rule) {
      final String text = escapeString(rule.getText());
      ruleComment(rule);
      if (switchOption) {
         ifStmt(matchVar.assign(parserVar.member(stringTestFunction.call(text, text.length()))));
         stmt(indexVar.plusEqual(rule.getText().length()));
         endIf();
      } else if (rule.getPartialMatchLength() == 0) {
         if (onlyTest) {
            setValue(matchVar, parserVar.member(stringTestFunction.call(text, text.length())));
         } else {
            setValue(matchVar, parserVar.member(stringMatcherFunction.call(text, text.length())));
         }
      } else if (onlyTest) {
         setValue(matchVar, parserVar.member(partialStringTestFunction.call(text, text.length(), rule.getPartialMatchLength())));
      } else {
         setValue(matchVar, parserVar.member(partialStringMatcherFunction.call(text, text.length(), rule.getPartialMatchLength())));
      }
   }

   @Override
   public void visitStringIgnoreCase(ParserGenerationOptions options, IgnoreCaseStringRule rule) {
      final String text = escapeString(rule.getText());
      ruleComment(rule);
      if (switchOption) {
         ifStmt(matchVar.assign(parserVar.member(ignoreCaseStringTestFunction.call(text, text.length()))));
         stmt(indexVar.plusEqual(rule.getText().length()));
         endIf();
      } else if (rule.getPartialMatchLength() == 0) {
         if (onlyTest) {
            setValue(matchVar, parserVar.member(ignoreCaseStringTestFunction.call(text, text.length())));
         } else {
            setValue(matchVar, parserVar.member(ignoreCaseStringMatcherFunction.call(text, text.length())));
         }
      } else if (onlyTest) {
         setValue(matchVar, parserVar.member(partialIgnoreCaseStringTestFunction.call(text, text.length(), rule.getPartialMatchLength())));
      } else {
         setValue(matchVar, parserVar.member(partialIgnoreCaseStringMatcherFunction.call(text, text.length(), rule.getPartialMatchLength())));
      }
   }

   @Override
   public void visitNonTerminal(ParserGenerationOptions options, NonTerminalRule rule) {
      if (rule.getOptions().containsKey(NonTerminalOption.FRAGMENT)) {
         rule.getRule().visit(options, this);
      } else {
         ruleComment(rule);
         if (currentOrOption > 0 && currentNonTerminalRule.hasLeftRecursion()) {
            setValue(matchVar, ruleTryVar(currentNonTerminalRule).less(currentOrOption).and(parserVar.member(funCall(rule.getMethodName()))));
         } else {
            setValue(matchVar, parserVar.member(funCall(rule.getMethodName())));
         }
      }
   }

   private void appendAndRule(final Iterator<GrammarRule> it) {
      it.next().visit(getOptions(), this);
      currentOrOption = 0;
      if (it.hasNext()) {
         ifStmt(matchVar);
         appendAndRule(it);
         endIf();
      }
   }

   @Override
   public void visitAnd(ParserGenerationOptions options, AndRule rule) {
      final Iterator<GrammarRule> it = rule.getRules().iterator();
      final int oldCurrentOrOption = currentOrOption;
      ruleComment(rule);
      if (it.hasNext()) {
         appendAndRule(it);
      }
      currentOrOption = oldCurrentOrOption;
   }

   private void appendOrRule(final Iterator<GrammarRule> it, final Variable auxIndexVar, final Variable auxNodeVar) {
      it.next().visit(getOptions(), this);
      if (it.hasNext()) {
         ifStmt(not(matchVar));
         if (currentOrOption > 0) {
            ++currentOrOption;
         }
         setValue(indexVar, auxIndexVar);
         setSibling(auxNodeVar, null);
         setValue(currentNodeVar, auxNodeVar);
         appendOrRule(it, auxIndexVar, auxNodeVar);
         endIf();
      } else {
         ifStmt(not(matchVar));
         setValue(indexVar, auxIndexVar);
         setSibling(auxNodeVar, null);
         setValue(currentNodeVar, auxNodeVar);
         endIf();
      }     
   }

   @Override
   public void visitOr(final ParserGenerationOptions options, final OrRule rule) {
      ruleComment(rule);
      if (canGroupOrLiterals(rule)) {
         final LiteralRulesGroup groupRules = groupLiteralsByFirstChar(rule.getRules());
         if (!groupRules.hasMultipleOptions()) {
            orLiteralsSingleStart(groupRules);
         } else {
            orLiteralsMultipleStart(groupRules);
         }
      } else if (canGroupOrNonTerminals(rule)) {
         final LiteralRulesGroup groupRules = groupRulesByFirstChar(rule.getRules());
         Variable auxNodeVar = declareVarStmt(NODE, "lastNode_" + auxVarNumber, currentNodeVar).getVariable();
         Variable auxIndexVar = declareVarStmt(INTEGER, "lastIndex_" + auxVarNumber++, indexVar).getVariable();
         groupRules.groupCommons();
         switchByFirstChar(groupRules, auxIndexVar, auxNodeVar);
      } else {
         final Iterator<GrammarRule> it = rule.getRules().iterator();
         Variable auxNodeVar = declareVarStmt(NODE, "lastNode_" + auxVarNumber, currentNodeVar).getVariable();
         Variable auxIndexVar = declareVarStmt(INTEGER, "lastIndex_" + auxVarNumber++, indexVar).getVariable();
         if (it.hasNext()) {
            appendOrRule(it, auxIndexVar, auxNodeVar);
         }
      }
   }

   private boolean canGroupOrNonTerminals(OrRule rule) {
      for (GrammarRule r : rule.getRules()) {
         if (!(r instanceof NonTerminalRule)) {
            return false;
         }
      }
      return true;
   }

   private void switchByFirstChar(final LiteralRulesGroup groupRules, final Variable auxIndexVar, final Variable auxNodeVar) {
      final SwitchStatement switchStatement;
      Iterator<GrammarRule> it;

      switchStatement = getStatements().peek().switchStatement(bufferVar.member(funCall(bufferGetCharFunctionName(), indexVar)));
      for (Map.Entry<CharSet, List<GrammarRule>> entry : groupRules.groupedRules.entrySet()) {
         if (!entry.getKey().isEmpty()) {
            it = entry.getValue().iterator();
            getStatements().push(switchStatement.switchOption(entry.getKey().characters));
            if (it.hasNext()) {
               appendOrRule(it, auxIndexVar, auxNodeVar);
            }
            breakCommand();
            getStatements().pop();
         }
      }
      List<GrammarRule> defaultRules = groupRules.groupedRules.get(new CharSet());
      if (defaultRules != null && !defaultRules.isEmpty()) {
         getStatements().push(switchStatement.switchOption(null));
         it = defaultRules.iterator();
         if (it.hasNext()) {
            appendOrRule(it, auxIndexVar, auxNodeVar);
         }
         getStatements().pop();
      } else {
         switchStatement.switchOption(null).setValue(matchVar, groupRules.acceptEmpty);
      }
   }

   private LiteralRulesGroup groupRulesByFirstChar(List<GrammarRule> rules) {
      final LiteralRulesGroup rulesGroup = new LiteralRulesGroup();
      final List<GrammarRule> anyCharRules = new ArrayList<GrammarRule>();

      for (GrammarRule r : rules) {
         Set<Character> charSet;
         FirstCharsVisitor visitor = new FirstCharsVisitor();
         r.visit(options, visitor);
         charSet = visitor.getCharSet();
         if (charSet.size() == 1 && charSet.contains('\0')) {
            anyCharRules.add(r);
         } else {
            for (char c : charSet) {
               final CharSet cp = new CharSet(c);
               List<GrammarRule> rulesList = rulesGroup.groupedRules.get(cp);
               if (rulesList == null) {
                  rulesList = new ArrayList<GrammarRule>();
                  rulesGroup.groupedRules.put(cp, rulesList);
               }
               rulesList.add(r);
            }
         }
      }
      if (!anyCharRules.isEmpty()) {
         for (Entry<CharSet, List<GrammarRule>> entry : rulesGroup.groupedRules.entrySet()) {
            entry.getValue().addAll(anyCharRules);
         }
         rulesGroup.groupedRules.put(new CharSet(), anyCharRules);
      }
      return rulesGroup;
   }

   private void orLiteralsMultipleStart(final LiteralRulesGroup groupRules) {
      final Variable auxStartIndexVar;
      final SwitchStatement switchStatement;
      final boolean lastSwitchCommand;
      if ((!inSwitch && !atomicRule) || onlyTest) {
         auxStartIndexVar = declareVarStmt(INTEGER, "startIndex_" + auxVarNumber++, indexVar).getVariable();
      } else {
         auxStartIndexVar = null;
      }
      lastSwitchCommand = inSwitch;
      inSwitch = true;
      switchStatement = getStatements().peek().switchStatement(bufferVar.member(funCall(bufferGetCharFunctionName(), indexVar)));
      for (Map.Entry<CharSet, List<GrammarRule>> entry : groupRules.groupedRules.entrySet()) {
         getStatements().push(switchStatement.switchOption(entry.getKey().characters));
         stmt(indexVar.preInc());
         continueLiteralMatchBlock(entry, groupRules.acceptEmpty);
         breakCommand();
         getStatements().pop();
      }
      switchStatement.switchOption(null).setValue(matchVar, groupRules.acceptEmpty);
      inSwitch = lastSwitchCommand;
      finishLiteralMatchBlock(auxStartIndexVar);
   }

   private void orLiteralsSingleStart(final LiteralRulesGroup groupRules) {
      final Variable auxStartIndexVar;
      if ((!inSwitch && !atomicRule) || onlyTest) {
         auxStartIndexVar = declareVarStmt(INTEGER, "startIndex_" + auxVarNumber++, indexVar).getVariable();
      } else {
         auxStartIndexVar = null;
      }
      for (Map.Entry<CharSet, List<GrammarRule>> entry : groupRules.groupedRules.entrySet()) {
         final boolean lastSwitchCommand = inSwitch;
         Expression condition = null;
         inSwitch = true;
         for (char c : entry.getKey().characters) {
            if (condition == null) {
               condition = bufferVar.member(funCall(bufferMatchCharFunctionName(), indexVar, c));
            } else {
               condition = condition.or(bufferVar.member(funCall(bufferMatchCharFunctionName(), indexVar, c)));
            }
         }
         setValue(matchVar, condition);
         ifStmt(matchVar);
         stmt(indexVar.preInc());
         continueLiteralMatchBlock(entry, groupRules.acceptEmpty);
         if (groupRules.acceptEmpty) {
            elseStmt();
            setValue(matchVar, true);
         }
         endIf();
         inSwitch = lastSwitchCommand;
         finishLiteralMatchBlock(auxStartIndexVar);
      }
   }

   private boolean canGroupOrLiterals(final OrRule rule) {
      for (GrammarRule r : rule.getRules()) {
         if ((!r.isTerminal() && !(r instanceof EmptyRule)) || r instanceof AnyCharRule || r instanceof CharRangeRule || isPartialStringMatch(r)) {
            return false;
         }
      }
      return true;
   }

   private void finishLiteralMatchBlock(final Variable auxStartIndexVar) {
      if (onlyTest) {
         setValue(indexVar, auxStartIndexVar);
      } else if (!inSwitch && !atomicRule) {
         ifStmt(not(matchVar));
         setValue(indexVar, auxStartIndexVar);
         elseIfStmt(not(currentRuleIsAtomicVar));
         setCurrentNode(currentNodeVar, createNodeFunCall(terminalRule(), auxStartIndexVar, indexVar, false, false));
         endIf();
      }
   }

   private void continueLiteralMatchBlock(Map.Entry<CharSet, List<GrammarRule>> entry, boolean containsEmpty) {
      if (entry.getValue().size() > 1) {
         new OrRule(entry.getValue()).visit(getOptions(), this);
      } else {
         final boolean lastSwitchOption = switchOption;
         switchOption = true;
         entry.getValue().get(0).visit(getOptions(), this);
         switchOption = lastSwitchOption;
      }
   }

   @Override
   public void visitZeroOrMore(ParserGenerationOptions options, ZeroOrMoreRule rule) {
      final int oldCurrentOrOption = currentOrOption;
      final Variable auxNodeVar;
      final Variable auxIndexVar;
      currentOrOption = 0;
      ruleComment(rule);
      auxNodeVar = declareVarStmt(NODE, "lastNode_" + auxVarNumber).getVariable();
      auxIndexVar = declareVarStmt(INTEGER, "lastIndex_" + auxVarNumber++).getVariable();
      doWhileStmt(matchVar);
      setValue(auxNodeVar, currentNodeVar);
      setValue(auxIndexVar, indexVar);
      rule.getRule().visit(options, this);
      endDo();
      setSibling(auxNodeVar, null);
      setValue(currentNodeVar, auxNodeVar);
      setValue(indexVar, auxIndexVar);
      setValue(matchVar, true);
      currentOrOption = oldCurrentOrOption;
   }

   @Override
   public void visitOneOrMore(ParserGenerationOptions options, OneOrMoreRule rule) {
      final int oldCurrentOrOption = currentOrOption;
      final Variable auxNodeVar;
      final Variable auxIndexVar;
      currentOrOption = 0;
      ruleComment(rule);
      auxNodeVar = declareVarStmt(NODE, "lastNode_" + auxVarNumber, currentNodeVar).getVariable();
      auxIndexVar = declareVarStmt(INTEGER, "lastIndex_" + auxVarNumber++, indexVar).getVariable();
      rule.getRule().visit(options, this);
      ifStmt(matchVar);
      doWhileStmt(matchVar);
      setValue(auxNodeVar, currentNodeVar);
      setValue(auxIndexVar, indexVar);
      rule.getRule().visit(options, this);
      endDo();
      setSibling(auxNodeVar, null);
      setValue(currentNodeVar, auxNodeVar);
      setValue(indexVar, auxIndexVar);
      setValue(matchVar, true);
      elseStmt();
      setSibling(auxNodeVar, null);
      setValue(currentNodeVar, auxNodeVar);
      setValue(indexVar, auxIndexVar);
      endIf();
      currentOrOption = oldCurrentOrOption;
   }

   @Override
   public void visitOptional(ParserGenerationOptions options, OptionalRule rule) {
      final int oldCurrentOrOption = currentOrOption;
      final Variable auxNodeVar;
      final Variable auxIndexVar;
      currentOrOption = 0;
      ruleComment(rule);
      auxNodeVar = declareVarStmt(NODE, "lastNode_" + auxVarNumber, currentNodeVar).getVariable();
      auxIndexVar = declareVarStmt(INTEGER, "lastIndex_" + auxVarNumber++, indexVar).getVariable();
      rule.getRule().visit(options, this);
      ifStmt(not(matchVar));
      setSibling(auxNodeVar, null);
      setValue(currentNodeVar, auxNodeVar);
      setValue(indexVar, auxIndexVar);
      setValue(matchVar, true);
      endIf();
      currentOrOption = oldCurrentOrOption;
   }

   @Override
   public void visitEOI(ParserGenerationOptions options, EOIRule rule) {
      ruleComment(rule);
      setValue(matchVar, parserVar.member(eoiFunction.call()));
   }

   @Override
   public void visitTest(ParserGenerationOptions options, TestRule rule) {
      final TestVisitor testVisitor = new TestVisitor();
      final int oldCurrentOrOption = currentOrOption;
      currentOrOption = 0;
      ruleComment(rule);
      rule.getRule().visit(options, testVisitor);
      if (testVisitor.isOnlyTest()) {
         final boolean oldOnlyTest = onlyTest;
         onlyTest = true;
         rule.getRule().visit(options, this);
         onlyTest = oldOnlyTest;
      } else {
         final Variable auxNodeVar = declareVarStmt(NODE, "lastNode_" + auxVarNumber, currentNodeVar).getVariable();
         final Variable auxIndexVar = declareVarStmt(INTEGER, "lastIndex_" + auxVarNumber, indexVar).getVariable();
         final Variable auxAtomicVar = declareVarStmt(BOOLEAN, "lastAtomic_" + auxVarNumber++, currentRuleIsAtomicVar).getVariable();
         rule.getRule().visit(options, this);
         setValue(currentRuleIsAtomicVar, auxAtomicVar);
         setValue(indexVar, auxIndexVar);
         setSibling(auxNodeVar, null);
         setValue(currentNodeVar, auxNodeVar);
      }
      currentOrOption = oldCurrentOrOption;
   }

   @Override
   public void visitTestNot(ParserGenerationOptions options, TestNotRule rule) {
      final TestVisitor testVisitor = new TestVisitor();
      final int oldCurrentOrOption = currentOrOption;
      currentOrOption = 0;
      ruleComment(rule);
      rule.getRule().visit(options, testVisitor);
      if (testVisitor.isOnlyTest()) {
         final boolean oldOnlyTest = onlyTest;
         onlyTest = true;
         rule.getRule().visit(options, this);
         setValue(matchVar, not(matchVar));
         onlyTest = oldOnlyTest;
      } else {
         final Variable auxNodeVar = declareVarStmt(NODE, "lastNode_" + auxVarNumber, currentNodeVar).getVariable();
         final Variable auxIndexVar = declareVarStmt(INTEGER, "lastIndex_" + auxVarNumber, indexVar).getVariable();
         final Variable auxAtomicVar = declareVarStmt(BOOLEAN, "lastAtomic_" + auxVarNumber++, currentRuleIsAtomicVar).getVariable();
         rule.getRule().visit(options, this);
         setValue(currentRuleIsAtomicVar, auxAtomicVar);
         setValue(indexVar, auxIndexVar);
         setSibling(auxNodeVar, null);
         setValue(currentNodeVar, auxNodeVar);
         setValue(matchVar, not(matchVar));
      }
      currentOrOption = oldCurrentOrOption;
   }

   @Override
   public void visitEmpty(ParserGenerationOptions options, EmptyRule rule) {
      ruleComment(rule);
      setValue(matchVar, true);
   }

   private void saveSourceCode() throws ParserGenerationException {
      for (SourceFile sourceFile : getSourceFiles()) {
         BufferedWriter writer = null;
         if (sourceFile.exists()) {
            if (options.isOverwriteTargetFiles()) {
               if (!sourceFile.delete()) {
                  throw new ParserGenerationException("File '" + sourceFile.getPath() + "' already exists and could not remove it.");
               }
            } else {
               throw new ParserGenerationException("File '" + sourceFile.getPath() + "' already exists.");
            }
         }
         try {
            writer = new BufferedWriter(new FileWriter(sourceFile));
            writer.append(sourceFile.getContent());
            writer.flush();
            writer.close();
         } catch (IOException ex) {
            throw new ParserGenerationException("Error creating target file '" + sourceFile.getPath() + "'.", ex);
         } finally {
            try {
               if (writer != null) {
                  writer.close();
               }
            } catch (IOException ex) {
               throw new ParserGenerationException("Error closing target file '" + sourceFile.getPath() + "'.", ex);
            }
         }
      }
   }

   protected void validateOptions() throws ParserGenerationException {
      if (options.getOutputDirectory() == null) {
         throw new ParserGenerationException("Output directory not defined!");
      }
      if (options.getOutputDirectory().exists()) {
         if (!options.getOutputDirectory().isDirectory()) {
            throw new ParserGenerationException("'" + options.getOutputDirectory().getPath() + "' is not a directory!");
         }
      } else {
         throw new ParserGenerationException("Output directory '" + options.getOutputDirectory().getPath() + "' not exists!");
      }
   }

   protected void validateGrammar(Grammar grammar) throws ParserGenerationException {
      for (NonTerminalRule rule : grammar.getRules()) {
         if (rule.getRule() == null) {
            throw new ParserGenerationException("Rule '" + rule.getName() + "' without production defined.");
         }
      }
      for (NonTerminalRule rule : grammar.getDeclaredRules()) {
         if (rule.getRule() == null) {
            throw new ParserGenerationException("Rule '" + rule.getName() + "' without production defined.");
         }
         if (rule.getOptions().containsKey(NonTerminalOption.SKIP_NODE)) {
            if (rule.getOptions().containsKey(NonTerminalOption.ATOMIC)) {
               throw new ParserGenerationException("Rule '" + rule.getName() + "' contains both @SkipNode and @Atomic options. They are mutually exclusive!");
            }
            if (rule.getOptions().containsKey(NonTerminalOption.SYNTAX_ONLY)) {
               throw new ParserGenerationException("Rule '" + rule.getName() + "' contains both @SkipNode and @SyntaxOnly options. They are mutually exclusive!");
            }
         }
         if (rule.getOptions().containsKey(NonTerminalOption.FRAGMENT) && rule.getOptions().containsKey(NonTerminalOption.SYNTAX_ONLY)) {
            throw new ParserGenerationException("Rule '" + rule.getName() + "' contains both @SyntaxOnly and @Fragment options. They are mutually exclusive!");
         }
         if (options.getLeftRecursionStrategy().equals(LeftRecursionStrategy.GENERATE_ERROR)) {
            List<GrammarRule> recursivePath = leftRecursionPath(rule);
            if (!recursivePath.isEmpty()) {
               throw new ParserGenerationException("Rule '" + rule.getName() + "' contains left recursion: " + recursivePath.toString());
            }
         } else if (options.getLeftRecursionStrategy().equals(LeftRecursionStrategy.ACCEPT)) {
            rule.setLeftRecursion(!leftRecursionPath(rule).isEmpty());
         }
      }
   }

   /* Verifica se a regra tem recursao a esquerda, retornando o caminho que gera a recursao */
   private List<GrammarRule> leftRecursionPath(NonTerminalRule rule) {
      LeftRecursionVerificationVisitor visitor = new LeftRecursionVerificationVisitor(rule);
      rule.visit(options, visitor);
      return visitor.getLeftRecursionPath();
   }

   /**
    * Constroi o padrao a ser verificado para definir quais literais esta regra deve seguir. Se a regra comecar com ! eh uma negacao
    * do padrao especificado.
    */
   private void setFollowPattern(final NonTerminalRule rule, final String followPattern) {
      if (followPattern.startsWith("!")) {
         rule.setFollowIfMatch(false);
         rule.setPatternToFollow(Pattern.compile(followPattern.substring(1)));
      } else {
         rule.setFollowIfMatch(true);
         rule.setPatternToFollow(Pattern.compile(followPattern));
      }
   }

   private void breakPartialLiterals(Grammar grammar) {
      GrammarRuleVisitor visitor = new PartialLiteralsBreakerVisitor();
      for (NonTerminalRule rule : grammar.getDeclaredRules()) {
         rule.visit(options, visitor);
      }
   }

   private void appendFollowLiterals(Grammar grammar) {
      GrammarRuleVisitor visitor = new FollowersLiteralsAppenderVisitor(followLiterals);
      for (NonTerminalRule rule : grammar.getDeclaredRules()) {
         rule.visit(options, visitor);
      }
   }

   protected void prepareRules(Grammar grammar) throws ParserGenerationException {
      /* Add MEMOIZE option to rules that can be result in better performance  */
      if (getOptions().getMemoizeMode().equals(MemoizeMode.AUTO)) {
         final MemorizationRulesDefiner memorizationDefiner = new MemorizationRulesDefiner();
         memorizationDefiner.memorizeRules(options, grammar.getMainRule());
      }
      for (NonTerminalRule rule : grammar.getDeclaredRules()) {
         final String alias = rule.getOptions().get(NonTerminalOption.RULE_NAME);
         /* Verifica se eh uma regra que sera incluida apos os literais de outras regras */
         if (rule.getOptions().containsKey(NonTerminalOption.FOLLOW_LITERALS)) {
            final String followPattern = rule.getOptions().get(NonTerminalOption.FOLLOW_LITERALS).trim();
            if (!followPattern.isEmpty()) {
               setFollowPattern(rule, followPattern);
            }
            followLiterals.add(rule);
         }
         /* Configura o nome que o metodo da regra tera */
         rule.setMethodName(rule.getName().substring(0, 1).toLowerCase() + rule.getName().substring(1) + sufixRuleName());
         rule.setRuleId(ruleId(rule, alias));
         if (alias != null) {
            rule.setLabel(alias);
         }
      }
      appendFollowLiterals(grammar);
      breakPartialLiterals(grammar);
   }

   protected String ruleId(NonTerminalRule rule, String alias) {
      String ruleName;
      StringBuilder sb = new StringBuilder();
      char lastChar = '_';
      if (alias != null) {
         ruleName = alias;
      } else {
         ruleName = rule.getName();
      }
      for (char c : ruleName.toCharArray()) {
         if (Character.isUpperCase(c) && lastChar != '_' && !Character.isUpperCase(lastChar)) {
            sb.append('_');
         }
         sb.append(Character.toUpperCase(c));
         lastChar = c;
      }
      return sb.toString();
   }

   public static String escapeChar(final char c) {
      switch (c) {
         case '\0':
            return "\\0";
         case '\n':
            return "\\n";
         case '\r':
            return "\\r";
         case '\t':
            return "\\t";
         case '\b':
            return "\\b";
         case '\f':
            return "\\f";
         case '\'':
            return "\\'";
         case '"':
            return "\\\"";
         case '\\':
            return "\\\\";
      }
      return Character.toString(c);
   }

   public static String escapeString(final String str) {
      final StringBuilder sb = new StringBuilder(str.length());
      for (int i = 0; i < str.length(); i++) {
         final char c = str.charAt(i);
         switch (c) {
            case '\0':
               sb.append("\\0");
               break;
            case '\n':
               sb.append("\\n");
               break;
            case '\r':
               sb.append("\\r");
               break;
            case '\t':
               sb.append("\\t");
               break;
            case '\b':
               sb.append("\\b");
               break;
            case '\f':
               sb.append("\\f");
               break;
//            case '\'':
//               sb.append("\\'");
//               break;
            case '"':
               sb.append("\\\"");
               break;
            case '\\':
               sb.append("\\\\");
               break;
            default:
               sb.append(c);
               break;
         }
      }
      return sb.toString();
   }

   private int ruleLength(GrammarRule rule) {
      if (rule instanceof StringRule) {
         return ((StringRule) rule).getText().length();
      } else if (rule instanceof IgnoreCaseStringRule) {
         return ((IgnoreCaseStringRule) rule).getText().length();
      } else if (rule instanceof CharRule) {
         return 1;
      } else if (rule instanceof IgnoreCaseCharRule) {
         return 1;
      } else if (rule instanceof AnyCharRule) {
         return 1;
      }
      return 0;
   }

   private List<GrammarRule> getGroupOfChar(final LiteralRulesGroup rulesGroup, final CharSet charPooler) {
      List<GrammarRule> rulesList = rulesGroup.groupedRules.get(charPooler);
      if (rulesList == null) {
         rulesList = new ArrayList<GrammarRule>();
         rulesGroup.groupedRules.put(charPooler, rulesList);
      }
      return rulesList;
   }

   private LiteralRulesGroup groupLiteralsByFirstChar(List<GrammarRule> rules) {
      final LiteralRulesGroup rulesGroup = new LiteralRulesGroup();
      boolean defaultMatch = false;

      for (GrammarRule r : rules) {
         List<GrammarRule> groupedRules;
         if (r instanceof CharRule) {
            final CharSet charPooler = new CharSet(((CharRule) r).getCharacter());
            addEmptyRule(getGroupOfChar(rulesGroup, charPooler));
         } else if (r instanceof IgnoreCaseCharRule) {
            final CharSet charPooler = new CharSet(Character.toLowerCase(((IgnoreCaseCharRule) r).getCharacter()),
                    Character.toUpperCase(((IgnoreCaseCharRule) r).getCharacter()));
            addEmptyRule(getGroupOfChar(rulesGroup, charPooler));
         } else if (r instanceof StringRule) {
            final CharSet charPooler = new CharSet(((StringRule) r).getText().charAt(0));
            groupedRules = getGroupOfChar(rulesGroup, charPooler);
            if (((StringRule) r).getText().length() > 2) {
               groupedRules.add(new StringRule(((StringRule) r).getText().substring(1)));
            } else if (((StringRule) r).getText().length() == 2) {
               groupedRules.add(new CharRule(((StringRule) r).getText().charAt(1)));
            } else {
               addEmptyRule(groupedRules);
            }

         } else if (r instanceof IgnoreCaseStringRule) {

            final CharSet charPooler = new CharSet(Character.toLowerCase(((IgnoreCaseStringRule) r).getText().charAt(0)),
                    Character.toUpperCase(((IgnoreCaseStringRule) r).getText().charAt(0)));
            groupedRules = getGroupOfChar(rulesGroup, charPooler);
            if (((IgnoreCaseStringRule) r).getText().length() > 2) {
               groupedRules.add(new IgnoreCaseStringRule(((IgnoreCaseStringRule) r).getText().substring(1)));
            } else if (((IgnoreCaseStringRule) r).getText().length() == 2) {
               groupedRules.add(new IgnoreCaseCharRule(((IgnoreCaseStringRule) r).getText().charAt(1)));
            } else {
               addEmptyRule(groupedRules);
            }

         } else if (r instanceof EmptyRule) {
            defaultMatch = true;
         }
      }
      rulesGroup.acceptEmpty = defaultMatch;

      for (List<GrammarRule> groupedRules : rulesGroup.groupedRules.values()) {
         Collections.sort(groupedRules, new Comparator<GrammarRule>() {

            @Override
            public int compare(GrammarRule o1, GrammarRule o2) {
               return ruleLength(o2) - ruleLength(o1);
            }
         });
      }
      return rulesGroup;
   }

   private void addEmptyRule(List<GrammarRule> groupedRules) {
      for (GrammarRule r : groupedRules) {
         if (r instanceof EmptyRule) {
            return;
         }
      }
      groupedRules.add(new EmptyRule());
   }

   protected boolean isPartialStringMatch(GrammarRule rule) {
      if (rule instanceof StringRule) {
         return ((StringRule) rule).getPartialMatchLength() > 0;
      } else if (rule instanceof IgnoreCaseStringRule) {
         return ((IgnoreCaseStringRule) rule).getPartialMatchLength() > 0;
      }
      return false;
   }

   private void catchMismatchCode(final Expression ruleReference) {
      if (getOptions().isCatchMismatches()) {
         registerRuleMismatch(indexVar, ruleReference);
      }
   }

   protected Expression not(Expression expr) {
      return expr.not();
   }

   protected void funCallStmt(final String functionName, final Object... parameters) {
      statements.peek().functionCall(functionName, parameters);
   }

   protected void stmt(final Object expr) {
      statements.peek().statementExpression(expr);
   }

   protected void returnStmt(final Object expr) {
      statements.peek().returnStatement(expr);
   }

   protected void forStmt(final Object initExpr, final Object testExpr, final Object incExpr) {
      statements.push(statements.peek().forStatement(initExpr, testExpr, incExpr));
   }

   protected void ifStmt(final Object exprTest) {
      statements.push(statements.peek().ifStatement(exprTest));
   }

   protected void elseIfStmt(final Object exprTest) {
      statements.pop();
      statements.push(statements.peek().elseIfStatement(exprTest));
   }

   protected void doWhileStmt(final Object exprTest) {
      statements.push(statements.peek().doWhileStatement(exprTest));
   }

   protected void elseStmt() {
      statements.pop();
      statements.push(statements.peek().elseStatement());
   }

   protected void endFor() {
      statements.pop();
   }

   protected void endIf() {
      statements.pop();
   }

   protected void endDo() {
      statements.pop();
   }

   protected void beginFunction(FunctionDefinition funDef) {
      statements.push(funDef);
   }

   protected void endFunction() {
      statements.pop().appendCode(output, getInitialIndentation());
   }

   protected VariableDeclaration declareVarStmt(final DataType type, final String varName, final Object exprInit) {
      return statements.peek().declareVariable(type, varName, exprInit);
   }

   protected VariableDeclaration declareVarStmt(final DataType type, final String varName) {
      return statements.peek().declareVariable(type, varName);
   }

   protected void setValue(final Expression left, final Object right) {
      statements.peek().setValue(left, right);
   }

   public Deque<BlockStatement> getStatements() {
      return statements;
   }

   private void generateTerminalFunctions() {
      generateCharMatcher();
      generateIgnoreCaseCharMatcher();
      generateStringMatcher();
      generatePartialStringMatcher();
      generateIgnoreCaseStringMatcher();
      generatePartialIgnoreCaseStringMatcher();
      generateCharRangeMatcher();
      generateAnyCharMatcher();
      generateEOIMatcher();
      generateCharTest();
      generateIgnoreCaseCharTest();
      generateStringTest();
      generatePartialStringTest();
      generateIgnoreCaseStringTest();
      generatePartialIgnoreCaseStringTest();
      generateCharRangeTest();
      generateAnyCharTest();
      generateEOITest();
   }

   private void terminalErrorHandlingCode(final Object expectedExpr) {
      if (getOptions().isCatchMismatches()) {
         ifStmt(catchTerminalMismatchVar.and(not(ignoreMismatchVar).and(not(currentRuleIsAtomicVar))));
         registerTerminalMismatch(indexVar, expectedExpr);
         endIf();
      }
   }

   private void generateEOIMatcher() {
      getOutput().append('\n');
      beginFunction(eoiFunction);
      traceCodeEnterRule(duplicateString("EOI", 3, null));
      ifStmt(bufferVar.member(funCall(bufferGetCharFunctionName(), indexVar)).equal('\0'));
      traceCodeExitRule(true);
      returnStmt(true);
      endIf();
      terminalErrorHandlingCode(duplicateString("EOI", 3, null));
      traceCodeExitRule(false);
      returnStmt(false);
      endFunction();
   }

   private void generateEOITest() {
      getOutput().append('\n');
      beginFunction(eoiTestFunction);
      returnStmt(bufferVar.member(funCall(bufferGetCharFunctionName(), indexVar)).equal('\0'));
      endFunction();
   }

   private void generateAnyCharMatcher() {
      getOutput().append('\n');
      beginFunction(anyCharMatcherFunction);
      traceCodeEnterRule(duplicateString("ANY", 3, null));
      ifStmt(bufferVar.member(funCall(bufferGetCharFunctionName(), indexVar)).diff('\0'));
      ifStmt(not(currentRuleIsAtomicVar));
      setCurrentNode(currentNodeVar, createNodeFunCall(terminalRule(), indexVar, indexVar.plus(1), false, false));
      endIf();
      stmt(indexVar.preInc());
      traceCodeExitRule(true);
      returnStmt(true);
      endIf();
      terminalErrorHandlingCode(duplicateString("ANY", 3, null));
      traceCodeExitRule(false);
      returnStmt(false);
      endFunction();
   }

   private void generateAnyCharTest() {
      getOutput().append('\n');
      beginFunction(anyCharTestFunction);
      returnStmt(bufferVar.member(funCall(bufferGetCharFunctionName(), indexVar)).diff('\0'));
      endFunction();
   }

   private void generateCharRangeMatcher() {
      getOutput().append('\n');
      beginFunction(charRangeMatcherFunction);
      traceCodeEnterRule(charSequence('[', charIniVar, '-', charEndVar, ']'));
      ifStmt(bufferVar.member(funCall(bufferMatchCharRangeFunctionName(), indexVar, charIniVar, charEndVar)));
      ifStmt(not(currentRuleIsAtomicVar));
      setCurrentNode(currentNodeVar, createNodeFunCall(terminalRule(), indexVar, indexVar.plus(1), false, false));
      endIf();
      stmt(indexVar.preInc());
      traceCodeExitRule(true);
      returnStmt(true);
      endIf();
      terminalErrorHandlingCode(charSequence('[', charIniVar, '-', charEndVar, ']'));
      traceCodeExitRule(false);
      returnStmt(false);
      endFunction();
   }

   private void generateCharRangeTest() {
      getOutput().append('\n');
      beginFunction(charRangeTestFunction);
      returnStmt(bufferVar.member(funCall(bufferMatchCharRangeFunctionName(), indexVar, charIniVar, charEndVar)));
      endFunction();
   }

   private void generateIgnoreCaseStringMatcher() {
      getOutput().append('\n');
      beginFunction(ignoreCaseStringMatcherFunction);
      traceCodeEnterRule(duplicateString(strVar, strLenVar, '\''));
      ifStmt(bufferVar.member(funCall(bufferMatchIgnoreCaseStringFunctioName(), indexVar, strVar, strLenVar)));
      ifStmt(not(currentRuleIsAtomicVar));
      setCurrentNode(currentNodeVar, createNodeFunCall(terminalRule(), indexVar, indexVar.plus(strLenVar), false, false));
      endIf();
      stmt(indexVar.plusEqual(strLenVar));
      traceCodeExitRule(true);
      returnStmt(true);
      endIf();
      terminalErrorHandlingCode(duplicateString(strVar, strLenVar, '\''));
      traceCodeExitRule(false);
      returnStmt(false);
      endFunction();
   }

   private void generatePartialIgnoreCaseStringMatcher() {
      getOutput().append('\n');
      beginFunction(partialIgnoreCaseStringMatcherFunction);
      traceCodeEnterRule(duplicateString(strVar, strLenVar, '\''));
      declareVarStmt(INTEGER, "i");
      forStmt(var("i").assign(0), var("i").less(strLenVar), var("i").posInc());
      ifStmt(not(bufferVar.member(funCall(bufferMatchIgnoreCaseCharFunctionName(), indexVar.plus(var("i")), charAt(strVar, var("i"))))));
      ifStmt(var("i").less(minLenVar));
      terminalErrorHandlingCode(duplicateString(strVar, strLenVar, '\''));
      traceCodeExitRule(false);
      returnStmt(false);
      elseStmt();
      breakCommand();
      endIf();
      endIf();
      endFor();
      ifStmt(not(currentRuleIsAtomicVar));
      setCurrentNode(currentNodeVar, createNodeFunCall(terminalRule(), indexVar, indexVar.plus(var("i")), false, false));
      endIf();
      stmt(indexVar.plusEqual(var("i")));
      traceCodeExitRule(true);
      returnStmt(true);
      endFunction();
   }

   private void generateIgnoreCaseStringTest() {
      getOutput().append('\n');
      beginFunction(ignoreCaseStringTestFunction);
      returnStmt(bufferVar.member(funCall(bufferMatchIgnoreCaseStringFunctioName(), indexVar, strVar, strLenVar)));
      endFunction();
   }

   private void generatePartialIgnoreCaseStringTest() {
      getOutput().append('\n');
      beginFunction(partialIgnoreCaseStringTestFunction);
      traceCodeEnterRule(duplicateString(strVar, strLenVar, '\''));
      declareVarStmt(INTEGER, "i");
      forStmt(var("i").assign(0), var("i").less(strLenVar), var("i").posInc());
      ifStmt(not(bufferVar.member(funCall(bufferMatchIgnoreCaseCharFunctionName(), indexVar.plus(var("i")), charAt(strVar, var("i"))))));
      ifStmt(var("i").less(minLenVar));
      returnStmt(false);
      elseStmt();
      breakCommand();
      endIf();
      endIf();
      endFor();
      returnStmt(true);
      endFunction();
   }

   private void generateStringMatcher() {
      getOutput().append('\n');
      beginFunction(stringMatcherFunction);
      traceCodeEnterRule(duplicateString(strVar, strLenVar, '\''));
      ifStmt(bufferVar.member(funCall(bufferMatchStringFunctionName(), indexVar, strVar, strLenVar)));
      ifStmt(not(currentRuleIsAtomicVar));
      setCurrentNode(currentNodeVar, createNodeFunCall(terminalRule(), indexVar, indexVar.plus(strLenVar), false, false));
      endIf();
      stmt(indexVar.plusEqual(strLenVar));
      traceCodeExitRule(true);
      returnStmt(true);
      endIf();
      terminalErrorHandlingCode(duplicateString(strVar, strLenVar, '\''));
      traceCodeExitRule(false);
      returnStmt(false);
      endFunction();
   }

   private void generatePartialStringMatcher() {
      getOutput().append('\n');
      beginFunction(partialStringMatcherFunction);
      traceCodeEnterRule(duplicateString(strVar, strLenVar, '\''));
      declareVarStmt(INTEGER, "i");
      forStmt(var("i").assign(0), var("i").less(strLenVar), var("i").posInc());
      ifStmt(not(bufferVar.member(funCall(bufferMatchCharFunctionName(), indexVar.plus(var("i")), charAt(strVar, var("i"))))));
      ifStmt(var("i").less(minLenVar));
      terminalErrorHandlingCode(duplicateString(strVar, strLenVar, '\''));
      traceCodeExitRule(false);
      returnStmt(false);
      elseStmt();
      breakCommand();
      endIf();
      endIf();
      endFor();
      ifStmt(not(currentRuleIsAtomicVar));
      setCurrentNode(currentNodeVar, createNodeFunCall(terminalRule(), indexVar, indexVar.plus(var("i")), false, false));
      endIf();
      stmt(indexVar.plusEqual(var("i")));
      traceCodeExitRule(true);
      returnStmt(true);
      endFunction();
   }

   private void generateStringTest() {
      getOutput().append('\n');
      beginFunction(stringTestFunction);
      returnStmt(bufferVar.member(funCall(bufferMatchStringFunctionName(), indexVar, strVar, strLenVar)));
      endFunction();
   }

   private void generatePartialStringTest() {
      getOutput().append('\n');
      beginFunction(partialStringTestFunction);
      traceCodeEnterRule(duplicateString(strVar, strLenVar, '\''));
      declareVarStmt(INTEGER, "i");
      forStmt(var("i").assign(0), var("i").less(strLenVar), var("i").posInc());
      ifStmt(not(bufferVar.member(funCall(bufferMatchCharFunctionName(), indexVar.plus(var("i")), charAt(strVar, var("i"))))));
      ifStmt(var("i").less(minLenVar));
      returnStmt(false);
      elseStmt();
      breakCommand();
      endIf();
      endIf();
      endFor();
      returnStmt(true);
      endFunction();
   }

   private void generateIgnoreCaseCharMatcher() {
      getOutput().append('\n');
      beginFunction(ignoreCaseCharMatcherFunction);
      traceCodeEnterRule(charSequence('\'', var("c"), '\''));
      ifStmt(bufferVar.member(funCall(bufferMatchIgnoreCaseCharFunctionName(), indexVar, var("c"))));
      ifStmt(not(currentRuleIsAtomicVar));
      setCurrentNode(currentNodeVar, createNodeFunCall(terminalRule(), indexVar, indexVar.plus(1), false, false));
      endIf();
      stmt(indexVar.preInc());
      traceCodeExitRule(true);
      returnStmt(true);
      endIf();
      terminalErrorHandlingCode(charSequence('\'', var("c"), '\''));
      traceCodeExitRule(false);
      returnStmt(false);
      endFunction();
   }

   private void generateIgnoreCaseCharTest() {
      getOutput().append('\n');
      beginFunction(ignoreCaseCharTestFunction);
      returnStmt(bufferVar.member(funCall(bufferMatchIgnoreCaseCharFunctionName(), indexVar, var("c"))));
      endFunction();
   }

   private void generateCharMatcher() {
      getOutput().append('\n');
      beginFunction(charMatcherFunction);
      traceCodeEnterRule(charSequence('\'', var("c"), '\''));
      ifStmt(bufferVar.member(funCall(bufferMatchCharFunctionName(), indexVar, var("c"))));
      ifStmt(not(currentRuleIsAtomicVar));
      setCurrentNode(currentNodeVar, createNodeFunCall(terminalRule(), indexVar, indexVar.plus(1), false, false));
      endIf();
      stmt(indexVar.preInc());
      traceCodeExitRule(true);
      returnStmt(true);
      endIf();
      terminalErrorHandlingCode(charSequence('\'', var("c"), '\''));
      traceCodeExitRule(false);
      returnStmt(false);
      endFunction();
   }

   private void generateCharTest() {
      getOutput().append('\n');
      beginFunction(charTestFunction);
      returnStmt(bufferVar.member(funCall(bufferGetCharFunctionName(), indexVar)).equal(var("c")));
      endFunction();
   }

   protected int getInitialIndentation() {
      return 0;
   }

   // x : x | y;
   private boolean isTest(GrammarRule rule, Map<GrammarRule, Boolean> verifiedRules) {
      if (!verifiedRules.containsKey(rule)) {
         verifiedRules.put(rule, false);
         if (rule instanceof ComposedGrammarRule) {
            final List<GrammarRule> rules = ((ComposedGrammarRule) rule).getRules();
            for (GrammarRule r : rules) {
               if (!isTest(r, verifiedRules)) {
                  return false;
               }
            }
            verifiedRules.put(rule, true);
            return true;
         } else if (rule instanceof SimpleGrammarRule) {
            final boolean test = isTest(((SimpleGrammarRule) rule).getRule(), verifiedRules);
            verifiedRules.put(rule, test);
            return test;
         } else if (rule instanceof EmptyRule) {
            verifiedRules.put(rule, true);
            return true;
         } else if (rule instanceof TestRule) {
            verifiedRules.put(rule, true);
            return true;
         } else if (rule instanceof TestNotRule) {
            verifiedRules.put(rule, true);
            return true;
         } else if (rule instanceof EOIRule) {
            verifiedRules.put(rule, true);
            return true;
         }
         return false;
      } else {
         return verifiedRules.get(rule);
      }
   }

   protected boolean isTest(GrammarRule rule) {
      return isTest(rule, new HashMap<GrammarRule, Boolean>());
   }

   private static class CharSet {

      public final Set<Character> characters = new HashSet<Character>();

      public CharSet(final char... chars) {
         for (char c : chars) {
            characters.add(c);
         }
      }

      @Override
      public boolean equals(Object other) {
         if (other instanceof CharSet) {
            return characters.equals(((CharSet) other).characters);
         }
         return false;
      }

      @Override
      public int hashCode() {
         int hash = 7;
         hash = 67 * hash + (this.characters != null ? this.characters.hashCode() : 0);
         return hash;
      }

      @Override
      public String toString() {
         final StringBuilder sb = new StringBuilder();
         for (Character c : characters) {
            if (sb.length() > 0) {
               sb.append(", ");
            }
            sb.append(c);
         }
         return sb.toString();
      }

      private boolean isEmpty() {
         return characters.isEmpty();
      }
   }

   private static class LiteralRulesGroup {

      public Map<CharSet, List<GrammarRule>> groupedRules = new HashMap<CharSet, List<GrammarRule>>();

      public boolean acceptEmpty = false;

      public boolean hasMultipleOptions() {
         return groupedRules.size() > 1;
      }

      public void groupCommons() {
         final Map<CharSet, List<GrammarRule>> commonRules = new HashMap<CharSet, List<GrammarRule>>();
         for (Entry<CharSet, List<GrammarRule>> source : groupedRules.entrySet()) {
            boolean found = false;
            for (Entry<CharSet, List<GrammarRule>> target : commonRules.entrySet()) {
               if (source.getValue().equals(target.getValue())) {
                  target.getKey().characters.addAll(source.getKey().characters);
                  found = true;
               }
            }
            if (!found) {
               commonRules.put(source.getKey(), source.getValue());
            }
         }
         groupedRules = commonRules;
      }
   }

   protected static class TestVisitor implements GrammarRuleVisitor {

      private boolean onlyTest;

      private boolean onlyTerminal;

      private final Set<GrammarRule> visitedRules = new HashSet<GrammarRule>();

      public TestVisitor() {
         onlyTest = true;
         onlyTerminal = true;
      }

      public boolean isOnlyTest() {
         return onlyTest;
      }

      public boolean isOnlyTerminal() {
         return onlyTerminal;
      }

      @Override
      public void visitNonTerminal(ParserGenerationOptions options, NonTerminalRule rule) {
         if (!visitedRules.contains(rule)) {
            visitedRules.add(rule);
            rule.getRule().visit(options, this);
         }
         onlyTerminal = false;
      }

      @Override
      public void visitAnd(ParserGenerationOptions options, AndRule rule) {
         for (GrammarRule child : rule.getRules()) {
            child.visit(options, this);
         }
         onlyTest = false;
      }

      @Override
      public void visitOr(ParserGenerationOptions options, OrRule rule) {
         for (GrammarRule child : rule.getRules()) {
            child.visit(options, this);
         }
      }

      @Override
      public void visitChar(ParserGenerationOptions options, CharRule rule) {
      }

      @Override
      public void visitCharIgnoreCase(ParserGenerationOptions options, IgnoreCaseCharRule rule) {
      }

      @Override
      public void visitCharRange(ParserGenerationOptions options, CharRangeRule rule) {
      }

      @Override
      public void visitAnyChar(ParserGenerationOptions options, AnyCharRule rule) {
      }

      @Override
      public void visitString(ParserGenerationOptions options, StringRule rule) {
      }

      @Override
      public void visitStringIgnoreCase(ParserGenerationOptions options, IgnoreCaseStringRule rule) {
      }

      @Override
      public void visitZeroOrMore(ParserGenerationOptions options, ZeroOrMoreRule rule) {
         onlyTest = false;
         rule.getRule().visit(options, this);
      }

      @Override
      public void visitOneOrMore(ParserGenerationOptions options, OneOrMoreRule rule) {
         onlyTest = false;
         rule.getRule().visit(options, this);
      }

      @Override
      public void visitTest(ParserGenerationOptions options, TestRule rule) {
         rule.getRule().visit(options, this);
      }

      @Override
      public void visitTestNot(ParserGenerationOptions options, TestNotRule rule) {
         rule.getRule().visit(options, this);
      }

      @Override
      public void visitOptional(ParserGenerationOptions options, OptionalRule rule) {
         onlyTest = false;
      }

      @Override
      public void visitEmpty(ParserGenerationOptions options, EmptyRule rule) {
      }

      @Override
      public void visitEOI(ParserGenerationOptions options, EOIRule rule) {
      }
   }

   protected static class DataTypeImpl implements DataType {

      private String type;

      public DataTypeImpl(String type) {
         this.type = type;
      }

      @Override
      public String getName() {
         return type;
      }

      @Override
      public void setName(String type) {
         this.type = type;
      }

   }
}
