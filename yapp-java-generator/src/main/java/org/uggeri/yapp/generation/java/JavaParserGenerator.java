/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.generation.java;

import org.uggeri.yapp.generation.AbstractParserGenerator;
import org.uggeri.yapp.generation.CodeOptimizer;
import org.uggeri.yapp.generation.MemoizeMode;
import org.uggeri.yapp.generation.ParserGenerationException;
import org.uggeri.yapp.generation.SourceFile;
import org.uggeri.yapp.generation.java.stmt.AbstractJavaCodeFragment;
import static org.uggeri.yapp.generation.java.stmt.AbstractJavaCodeFragment.asExpression;
import org.uggeri.yapp.generation.java.stmt.JavaBlockStatement;
import org.uggeri.yapp.generation.java.stmt.JavaFunctionCall;
import org.uggeri.yapp.generation.java.stmt.JavaFunctionDefinition;
import org.uggeri.yapp.generation.java.stmt.JavaLineComment;
import org.uggeri.yapp.generation.java.stmt.JavaLiteralExpression;
import org.uggeri.yapp.generation.java.stmt.JavaVariable;
import org.uggeri.yapp.generation.stmt.BlockStatement;
import org.uggeri.yapp.generation.stmt.DataType;
import org.uggeri.yapp.generation.stmt.Expression;
import org.uggeri.yapp.generation.stmt.FunctionCall;
import org.uggeri.yapp.generation.stmt.FunctionDefinition;
import org.uggeri.yapp.generation.stmt.LineComment;
import org.uggeri.yapp.generation.stmt.Variable;
import org.uggeri.yapp.generation.stmt.VariableDeclaration;
import org.uggeri.yapp.grammar.Grammar;
import org.uggeri.yapp.grammar.rules.NonTerminalOption;
import org.uggeri.yapp.grammar.rules.NonTerminalRule;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author fabio
 */
public class JavaParserGenerator extends AbstractParserGenerator {

   public JavaParserGenerator(final Grammar grammar, final JavaParserGenerationOptions options) {
      super(grammar, options);
   }

   @Override
   protected void validateOptions() throws ParserGenerationException {
      super.validateOptions();
      if (getOptions().getPackageName() == null || getOptions().getPackageName().isEmpty()) {
         throw new ParserGenerationException("Package not informed!");
      }
   }

   private String parserClassName(Grammar g) {
      return g.getGrammarName() + "Parser";
   }

   private String visitorClassName(Grammar g) {
      return g.getGrammarName() + "Visitor";
   }

   private String rulesEnumName(Grammar g) {
      return g.getGrammarName() + "RuleType";
   }

   @Override
   protected String sufixRuleName() {
      return "$Rule";
   }

   @Override
   protected void initializeCodeGeneration() throws ParserGenerationException {
      appendSourceHeaderComment();
      getOutput().append("package ").append(getOptions().getPackageName()).append(";\n");
      getOutput().append('\n');
      appendImports();
      getOutput().append('\n');
      getOutput().append("public class ").append(parserClassName(getGrammar())).append(" implements Parser {\n");
      getOutput().append('\n');
      getOutput().append("   private int index = 0;\n");
      getOutput().append('\n');
      getOutput().append("   private InputBuffer buffer;\n");
      getOutput().append('\n');
      getOutput().append("   private boolean currentRuleIsAtomic = false;\n");
      getOutput().append('\n');
      getOutput().append("   private Node currentNode = new NodeImpl(null, 0, 0, false, false);\n");
      generateMemoFields(getGrammar(), new HashSet<String>(), 3);
      generateProfileFields(getGrammar(), new HashSet<String>(), 3);
      generateLeftRecursionFields(getGrammar(), new HashSet<String>(), 3);
      generateAbstractVisitor(getGrammar());
      generateRulesEnum(getGrammar());
   }

   @Override
   protected void finalizeCodeGeneration() throws ParserGenerationException {
      getOutput().append("\n}\n");
      getSourceFiles().add(new SourceFile(getOptions().getOutputDirectory(), parserClassName(getGrammar()) + ".java", getOutput()));
   }


   @Override
   protected void generateProfilesFunctions() {
      if (getOptions().isProfile()) {
         final Set<String> mappedRules = new HashSet<String>();
         getOutput().append('\n');
         getOutput().append("   private Map<Rule, RuleProfile> rulesProfiles = null;\n");
         getOutput().append('\n');
         getOutput().append("   private void initializeRulesProfilesMap() {\n");
         getOutput().append("      if (rulesProfiles == null) {\n");
         getOutput().append("         rulesProfiles = new HashMap<Rule, RuleProfile>();\n");
         getOutput().append("      }\n");
         mapGrammarRulesProfiles(getGrammar(), mappedRules);
         getOutput().append("   }\n");
      }
      getOutput().append('\n');
      getOutput().append("   @Override\n");
      getOutput().append("   public Map<Rule, RuleProfile> getProfilesMap() {\n");
      if (getOptions().isProfile()) {
         getOutput().append("      return rulesProfiles;\n");
      } else {
         getOutput().append("      return Collections.emptyMap();\n");
      }
      getOutput().append("   }\n");
      getOutput().append('\n');
      getOutput().append("   @Override\n");
      getOutput().append("   public void setProfilesMap(Map<Rule, RuleProfile> profilesMap) {\n");
      if (getOptions().isProfile()) {
         getOutput().append("      this.rulesProfiles = profilesMap;\n");
      }
      getOutput().append("   }\n");
   }

   private void mapGrammarRulesProfiles(Grammar grammar, Set<String> mappedRules) {
      for (NonTerminalRule rule : grammar.getDeclaredRules()) {
         if (! mappedRules.contains(rule.getRuleId()) &&
                 ! rule.getOptions().containsKey(NonTerminalOption.FRAGMENT)) {
            final String profileVarName = profileFieldName(rule);
            getOutput().append("      ").append(profileVarName).append(" = rulesProfiles.get(").append(rulesEnumName(grammar)).append('.').append(rule.getRuleId()).append(");\n");
            getOutput().append("      if (").append(profileVarName).append(" == null) {\n");
            getOutput().append("         ").append(profileVarName).append(" = new RuleProfile(").append(rulesEnumName(grammar)).append('.').append(rule.getRuleId()).append(");\n");
            getOutput().append("         rulesProfiles.put(").append(rulesEnumName(grammar)).append('.').append(rule.getRuleId()).append(", ").append(profileVarName).append(");\n");
            getOutput().append("      }\n");
            mappedRules.add(rule.getMethodName());
         }
      }
   }

   private void appendImports() {
      if (getOptions().isGenerateSingleFileSource()) {
         getOutput().append("import org.uggeri.yapp.runtime.java.node.NodeVisitor;\n");
      }
      getOutput().append("import org.uggeri.yapp.runtime.java.parser.Parser;\n");
      getOutput().append("import org.uggeri.yapp.runtime.java.parser.Rule;\n");
      getOutput().append("import org.uggeri.yapp.runtime.java.parser.RuleProfile;\n");
      getOutput().append("import org.uggeri.yapp.runtime.java.buffer.InputBuffer;\n");
      getOutput().append("import org.uggeri.yapp.runtime.java.node.Node;\n");
      getOutput().append("import org.uggeri.yapp.runtime.java.node.NodeImpl;\n");
      getOutput().append("import org.uggeri.yapp.runtime.java.parser.ParserError;\n");
      getOutput().append("import org.uggeri.yapp.runtime.java.trace.TraceParser;\n");
      getOutput().append("import java.util.Collections;\n");
      getOutput().append("import java.util.Collection;\n");
      getOutput().append("import java.util.Arrays;\n");
      if (getOptions().isProfile()) {
         getOutput().append("import java.util.HashMap;\n");
      }
      getOutput().append("import java.util.Map;\n");
      if (getOptions().isCatchMismatches()) {
         getOutput().append("import org.uggeri.yapp.runtime.java.util.NodeLink;\n");
         getOutput().append("import org.uggeri.yapp.runtime.java.util.ResizableList;\n");
      }
   }

   @Override
   protected void generateParserFunctions() {
      getOutput().append('\n');
      getOutput().append("   private Node lastChild(Node node) {\n");
      getOutput().append("      Node child = node.getFirstChild();\n");
      getOutput().append("      if (child != null) {\n");
      getOutput().append("         while (child.getSibling() != null) {\n");
      getOutput().append("            child = child.getSibling();\n");
      getOutput().append("         }\n");
      getOutput().append("      }\n");
      getOutput().append("      return child;\n");
      getOutput().append("   }\n");
      getOutput().append("\n");
      getOutput().append("   private Node removeNode(Node parent, Node left, Node node) {\n");
      getOutput().append("      if (node.getFirstChild() != null) {\n");
      getOutput().append("         if (node.getSibling() != null) {\n");
      getOutput().append("            lastChild(node).setSibling(node.getSibling());\n");
      getOutput().append("         }\n");
      getOutput().append("         if (left == null) {\n");
      getOutput().append("            parent.setFirstChild(node.getFirstChild());\n");
      getOutput().append("         } else {\n");
      getOutput().append("            left.setSibling(node.getFirstChild());\n");
      getOutput().append("         }\n");
      getOutput().append("         return node.getFirstChild();\n");
      getOutput().append("      } else if (left == null) {\n");
      getOutput().append("         parent.setFirstChild(node.getSibling());\n");
      getOutput().append("      } else {\n");
      getOutput().append("         left.setSibling(node.getSibling());\n");
      getOutput().append("      }\n");
      getOutput().append("      return node.getSibling();\n");
      getOutput().append("   }\n");
      getOutput().append("\n");
      getOutput().append("   private void removeSkipedNodes(Node node) {\n");
      getOutput().append("      if (node != null) {\n");
      getOutput().append("         Node leftNode = null;\n");
      getOutput().append("         Node child = node.getFirstChild();\n");
      getOutput().append("         while (child != null) {\n");
      getOutput().append("            if (child.isSkiped()) {\n");
      getOutput().append("               child = removeNode(node, leftNode, child);\n");
      getOutput().append("            } else {\n");
      getOutput().append("               removeSkipedNodes(child);\n");
      getOutput().append("               leftNode = child;\n");
      getOutput().append("               child = child.getSibling();\n");
      getOutput().append("            }\n");
      getOutput().append("         }\n");
      getOutput().append("      }\n");
      getOutput().append("   }\n");
      getOutput().append("\n");
      getOutput().append("   @Override\n");
      getOutput().append("   public Node parse(InputBuffer inputBuffer) {\n");
      getOutput().append("      buffer = inputBuffer;\n");
      if (getOptions().isProfile()) {
         getOutput().append("      initializeRulesProfilesMap();\n");
      }
      getOutput().append("      if (").append(getGrammar().getMainRule().getMethodName()).append("()) {\n");
      getOutput().append("         removeSkipedNodes(currentNode);\n");
      getOutput().append("         return currentNode;\n");
      getOutput().append("      } else {\n");
      getOutput().append("         return null;\n");
      getOutput().append("      }\n");
      getOutput().append("   }\n");
   }

   private void generateVisitorMethods(Grammar grammar, Set<String> generatedMethods, int indent) {
      for (NonTerminalRule rule : grammar.getDeclaredRules()) {
         if (!generatedMethods.contains(rule.getLabel()) && !rule.getOptions().containsKey(NonTerminalOption.FRAGMENT)) {
            generatedMethods.add(rule.getLabel());
            getOutput().append('\n');
            indent(getOutput().append('\n'), indent).append("public void enter").append(rule.getLabel()).append("(Node node) {}");
            indent(getOutput().append('\n'), indent).append("public void exit").append(rule.getLabel()).append("(Node node) {}");
         }
      }

   }

   private void generateAbstractVisitor(Grammar grammar) {
      if (getOptions().isGenerateSingleFileSource()) {
         getOutput().append('\n');
         getOutput().append("   public abstract static class ").append(visitorClassName(grammar)).append(" implements NodeVisitor {");
         generateGrammarVisitorMethods(grammar, new HashSet<String>(), 6);
         getOutput().append("   }\n");
      } else {
         final StringBuilder oldOutput = getOutput();
         setOutput(new StringBuilder());
         appendSourceHeaderComment();
         getOutput().append("package ").append(getOptions().getPackageName()).append(";\n");
         getOutput().append('\n');
         getOutput().append("import org.uggeri.yapp.runtime.java.node.Node;\n");
         getOutput().append("import org.uggeri.yapp.runtime.java.node.NodeVisitor;\n");
         getOutput().append('\n');
         getOutput().append("public abstract class ").append(visitorClassName(grammar)).append(" implements NodeVisitor {");
         generateGrammarVisitorMethods(grammar, new HashSet<String>(), 3);
         getOutput().append("}\n");
         getSourceFiles().add(new SourceFile(getOptions().getOutputDirectory(), visitorClassName(grammar) + ".java", getOutput()));
         setOutput(oldOutput);
      }
   }

   private void generateGrammarVisitorMethods(Grammar grammar, Set<String> generatedMethods, int indent) {
      generateVisitorMethods(grammar, generatedMethods, indent);
   }

   private boolean generateEnums(Grammar mainGrammar, Grammar grammar, boolean first, Set<String> generatedRules, int indent) {
      for (NonTerminalRule rule : grammar.getDeclaredRules()) {
         if (! generatedRules.contains(rule.getRuleId()) &&
                 ! rule.getOptions().containsKey(NonTerminalOption.FRAGMENT)) {
            generatedRules.add(rule.getRuleId());
            if (first) {
               first = false;
            } else {
               getOutput().append(',');
            }
            indent(getOutput().append('\n'), indent).append(rule.getRuleId()).append(" {");
            indent(getOutput().append('\n'), indent).append("   @Override");
            indent(getOutput().append('\n'), indent).append("   public String getLabel() {");
            indent(getOutput().append('\n'), indent).append("      return \"").append(rule.getLabel()).append("\";");
            indent(getOutput().append('\n'), indent).append("   }");
            getOutput().append('\n');
            indent(getOutput().append('\n'), indent).append("   @Override");
            indent(getOutput().append('\n'), indent).append("   public boolean isAtomic() {");
            indent(getOutput().append('\n'), indent).append("      return ").append(rule.getOptions().containsKey(NonTerminalOption.ATOMIC)).append(';');
            indent(getOutput().append('\n'), indent).append("   }");
            getOutput().append('\n');
            indent(getOutput().append('\n'), indent).append("   @Override");
            indent(getOutput().append('\n'), indent).append("   public boolean isSkiped() {");
            indent(getOutput().append('\n'), indent).append("      return ").append(rule.getOptions().containsKey(NonTerminalOption.SKIP_NODE)).append(';');
            indent(getOutput().append('\n'), indent).append("   }");
            getOutput().append('\n');
            indent(getOutput().append('\n'), indent).append("   @Override");
            indent(getOutput().append('\n'), indent).append("   public void enterRule(").append(visitorClassName(mainGrammar)).append(" visitor, Node node) {");
            indent(getOutput().append('\n'), indent).append("      visitor.enter").append(rule.getLabel()).append("(node);");
            indent(getOutput().append('\n'), indent).append("   }");
            getOutput().append('\n');
            indent(getOutput().append('\n'), indent).append("   @Override");
            indent(getOutput().append('\n'), indent).append("   public void exitRule(").append(visitorClassName(mainGrammar)).append(" visitor, Node node) {");
            indent(getOutput().append('\n'), indent).append("      visitor.exit").append(rule.getLabel()).append("(node);");
            indent(getOutput().append('\n'), indent).append("   }");
            indent(getOutput().append('\n'), indent).append("}");
         }
      }
      return first;
   }

   private void generateRulesEnum(Grammar grammar) {
      if (getOptions().isGenerateSingleFileSource()) {
         getOutput().append('\n');
         getOutput().append("   public static enum ").append(rulesEnumName(grammar)).append(" implements Rule<").append(visitorClassName(grammar)).append("> {");
         generateGrammarEnums(grammar, grammar, true, new HashSet<String>(), 6);
         getOutput().append(";\n\n");
         getOutput().append("      @Override\n");
         getOutput().append("      public int getValue() {\n");
         getOutput().append("         return ordinal();\n");
         getOutput().append("      }\n");
         getOutput().append("   }\n");
      } else {
         final StringBuilder oldOutput = getOutput();
         setOutput(new StringBuilder());
         appendSourceHeaderComment();
         getOutput().append("package ").append(getOptions().getPackageName()).append(";\n");
         getOutput().append('\n');
         getOutput().append("import org.uggeri.yapp.runtime.java.node.Node;\n");
         getOutput().append("import org.uggeri.yapp.runtime.java.parser.Rule;\n");
         getOutput().append('\n');
         getOutput().append("public enum ").append(rulesEnumName(grammar)).append(" implements Rule<").append(visitorClassName(grammar)).append("> {");
         generateGrammarEnums(grammar, grammar, true, new HashSet<String>(), 3);
         getOutput().append(";\n\n");
         getOutput().append("   @Override\n");
         getOutput().append("   public int getValue() {\n");
         getOutput().append("      return ordinal();\n");
         getOutput().append("   }\n");
         getOutput().append("}\n");
         getSourceFiles().add(new SourceFile(getOptions().getOutputDirectory(), rulesEnumName(grammar) + ".java", getOutput()));
         setOutput(oldOutput);
      }
   }

   private boolean generateGrammarEnums(Grammar mainGrammar, Grammar grammar, boolean first, Set<String> generatedRules, int indent) {
      return generateEnums(mainGrammar, grammar, first, generatedRules, indent);
   }

   private void generateMemoFields(Grammar grammar, Set<String> generatedRules, int indent) {
      if (! getOptions().getMemoizeMode().equals(MemoizeMode.NONE)) {
         generateGrammarMemoFields(grammar, generatedRules, indent);
      }
   }

   private void generateGrammarMemoFields(Grammar grammar, Set<String> generatedRules, int indent) {
      for (NonTerminalRule rule : grammar.getDeclaredRules()) {
         if (!generatedRules.contains(rule.getName()) && !rule.getOptions().containsKey(NonTerminalOption.FRAGMENT)) {
            generatedRules.add(rule.getName());
            if (isMemoizeRule(rule)) {
               declareRuleMemoFields(rule, indent);
            }
         }
      }
   }

   private void declareRuleMemoFields(final NonTerminalRule rule, final int indent) {
      if (getNumberMemorizations() > 1) {
         indent(getOutput().append('\n'), indent).append("private final int[] ").append(varName(memoVarStart(rule))).append(" = newArrayInt(").append(getNumberMemorizations()).append(");");
         indent(getOutput().append('\n'), indent).append("private final int[] ").append(varName(memoVarEnd(rule))).append(" = new int[").append(getNumberMemorizations()).append("];");
         indent(getOutput().append('\n'), indent).append("private final Node[] ").append(varName(memoVarFirstNode(rule))).append(" = new Node[").append(getNumberMemorizations()).append("];");
      } else {
         indent(getOutput().append('\n'), indent).append("private int ").append(varName(memoVarStart(rule))).append(" = -1;");
         indent(getOutput().append('\n'), indent).append("private int ").append(varName(memoVarEnd(rule))).append(';');
         indent(getOutput().append('\n'), indent).append("private Node ").append(varName(memoVarFirstNode(rule))).append(';');
      }
   }

   private void generateProfileFields(Grammar grammar, Set<String> generatedRules, int indent) {
      if (getOptions().isProfile()) {
         generateGrammarProfileFields(grammar, generatedRules, indent);
      }
   }

   private void generateGrammarProfileFields(Grammar grammar, Set<String> generatedRules, int indent) {
      for (NonTerminalRule rule : grammar.getDeclaredRules()) {
         if (!generatedRules.contains(rule.getName()) && !rule.getOptions().containsKey(NonTerminalOption.FRAGMENT)) {
            generatedRules.add(rule.getName());
            getOutput().append('\n');
            indent(getOutput(), indent).append("private RuleProfile ").append(profileFieldName(rule)).append(";");
         }
      }
   }

   private String profileFieldName(NonTerminalRule rule) {
      return rule.getMethodName() + "Profile";
   }

   private void generateLeftRecursionFields(Grammar grammar, Set<String> generatedRules, int indent) {
      generateGrammarLeftRecursionFields(grammar, generatedRules, indent);
   }

   private void generateGrammarLeftRecursionFields(Grammar grammar, Set<String> generatedRules, int indent) {
      for (NonTerminalRule rule : grammar.getDeclaredRules()) {
         if (rule.hasLeftRecursion() && !generatedRules.contains(rule.getName()) && !rule.getOptions().containsKey(NonTerminalOption.FRAGMENT)) {
            generatedRules.add(rule.getName());
            declareRuleLeftRecursionFields(rule, indent);
         }
      }
   }

   private void declareRuleLeftRecursionFields(final NonTerminalRule rule, final int indent) {
      indent(getOutput().append('\n'), indent).append("private int ").append(varName(ruleLastIndexVar(rule))).append(" = -1;");
      indent(getOutput().append('\n'), indent).append("private int ").append(varName(ruleTryVar(rule))).append(" = 0;");
   }

   @Override
   protected void generateAuxiliaryFunctions() {
      generateCreateIntegerArray();
   }

   private void generateCreateIntegerArray() {
      getOutput().append("\n\n");
      getOutput().append("   private int[] newArrayInt(final int size) {\n");
      getOutput().append("      final int[] array = new int[size];\n");
      getOutput().append("      Arrays.fill(array, -1);\n");
      getOutput().append("      return array;\n");
      getOutput().append("   }");
   }

   @Override
   protected void generateErrorFunctions() {
      getOutput().append('\n');
      if (getOptions().isCatchMismatches()) {
         getOutput().append("   private NodeLink<Rule> tailRule = null;\n");
         getOutput().append('\n');
         getOutput().append("   private boolean ignoreMismatch = false;\n");
         getOutput().append('\n');
         getOutput().append("   private boolean catchTerminalMismatch = true;\n");
         getOutput().append('\n');
         getOutput().append("   private int mismatchIndex = -1;\n");
         getOutput().append('\n');
         getOutput().append("   private final ResizableList<ParserError> mismatches = new ResizableList<ParserError>();\n");
         getOutput().append('\n');
         getOutput().append("   @Override\n");
         getOutput().append("   public Collection<ParserError> getMismatches() {\n");
         getOutput().append("      return mismatches;\n");
         getOutput().append("   }\n");
         getOutput().append('\n');
         getOutput().append("   private void registerRuleMismatch(final int currentIndex, final ").append(rulesEnumName(getGrammar())).append(" rule) {\n");
         getOutput().append("      if (! ignoreMismatch) {\n");
         getOutput().append("         if (currentIndex > mismatchIndex) {\n");
         getOutput().append("            mismatchIndex = currentIndex;\n");
         getOutput().append("            mismatches.setSize(0);\n");
         getOutput().append("            mismatches.add(new ParserError(currentIndex, rule, tailRule));\n");
         getOutput().append("         } else if (currentIndex == mismatchIndex) {\n");
         getOutput().append("            mismatches.add(new ParserError(currentIndex, rule, tailRule));\n");
         getOutput().append("         }\n");
         getOutput().append("      }\n");
         getOutput().append("   }\n");
         getOutput().append('\n');
         getOutput().append("   private void registerTerminalMismatch(final int currentIndex, final String expected) {\n");
         getOutput().append("      if (! ignoreMismatch) {\n");
         getOutput().append("         if (currentIndex > mismatchIndex) {\n");
         getOutput().append("            mismatchIndex = currentIndex;\n");
         getOutput().append("            mismatches.setSize(0);\n");
         getOutput().append("            mismatches.add(new ParserError(currentIndex, expected, tailRule));\n");
         getOutput().append("         } else if (currentIndex == mismatchIndex) {\n");
         getOutput().append("            mismatches.add(new ParserError(currentIndex, expected, tailRule));\n");
         getOutput().append("         }\n");
         getOutput().append("      }\n");
         getOutput().append("   }\n");
          getOutput().append('\n');
      } else {
         getOutput().append("   @Override\n");
         getOutput().append("   public Collection<ParserError> getMismatches() {\n");
         getOutput().append("      return Collections.emptyList();\n");
         getOutput().append("   }\n");
      }
   }

   @Override
   protected void generateTraceFunctions() {
      getOutput().append('\n');
      if (getOptions().isGenerateTraceCode()) {
         getOutput().append("   private TraceParser tracePath;\n");
         getOutput().append('\n');
         getOutput().append("   private boolean trace = false;\n");
         getOutput().append('\n');
         getOutput().append("   @Override\n");
         getOutput().append("   public void setTraceParser(TraceParser tracePath) {\n");
         getOutput().append("      this.tracePath = tracePath;\n");
         getOutput().append("   }\n");
         getOutput().append('\n');
         getOutput().append("   @Override\n");
         getOutput().append("   public void setTrace(boolean trace) {\n");
         getOutput().append("      this.trace = trace;\n");
         getOutput().append("   }\n");
      } else {
         getOutput().append("   @Override\n");
         getOutput().append("   public void setTraceParser(TraceParser tracePath) {\n");
         getOutput().append("   }\n");
         getOutput().append('\n');
         getOutput().append("   @Override\n");
         getOutput().append("   public void setTrace(boolean trace) {\n");
         getOutput().append("   }\n");
      }
   }

   private void appendSourceHeaderComment() {
      getOutput().append("/***************************************************\n");
      getOutput().append(" * PEG Parser - Generated By YAPP Parser Generator *\n");
      getOutput().append(" ***************************************************/\n\n");
   }

   @Override
   protected Variable var(String varName) {
      return new JavaVariable(varName);
   }

   @Override
   protected FunctionCall funCall(String functionName, Object... parameters) {
      Expression[] arrayAux = new Expression[parameters.length];
      for (int i = 0; i < parameters.length; i++) {
         arrayAux[i] = AbstractJavaCodeFragment.asExpression(parameters[i]);
      }
      return new JavaFunctionCall(functionName, arrayAux);
   }

   @Override
   protected LineComment lineComment(String text) {
      return new JavaLineComment(text);
   }

   @Override
   protected BlockStatement createBlockStatement() {
      return new JavaBlockStatement();
   }

   @Override
   protected String ruleName(Grammar grammar, NonTerminalRule rule) {
      return rule.getRuleId();
   }

   @Override
   protected Expression ruleReference(Grammar grammar, NonTerminalRule rule) {
      return new JavaLiteralExpression(rulesEnumName(grammar) + "." + ruleName(grammar, rule));
   }

   @Override
   protected CodeOptimizer createOptimizer() {
      return new JavaCodeOptimizer();
   }

   @Override
   protected void setSibling(Variable nodeVar, Expression expr) {
      funCallStmt(nodeVar.getName() + ".setSibling", expr);
   }

   @Override
   protected Expression getSibling(Variable nodeVar) {
      return funCall(nodeVar.getName() + ".getSibling");
   }

   @Override
   protected Expression getFirstChild(Expression nodeVar) {
      return funCall(nodeVar + ".getFirstChild");
   }

   @Override
   protected Expression timeFunCall() {
      return funCall("System.currentTimeMillis");
   }

   @Override
   protected Expression createNodeFunCall(Object rule, Object startIndex, Object index, Object semantic, Object skiped) {
      return funCall("new NodeImpl", rule, startIndex, index, semantic, skiped);
   }

   @Override
   protected Expression createErrorFunCall(Object index, Object rule, Object expectedValue, Object tailRule) {
      if (rule != null) {
         return funCall("new ParserError", index, rule, tailRule);
      } else {
         return funCall("new ParserError", index, expectedValue, tailRule);
      }
   }

   @Override
   protected Expression terminalRule() {
      return new JavaLiteralExpression<String>("Rule.TERMINAL");
   }

   @Override
   protected void incMatch(Variable profile, Expression expr) {
      profile.member(funCall("incMatchCount", expr));
   }

   @Override
   protected void incMemoMatch(Variable profile, Expression expr) {
      profile.member(funCall("incMemoMatchCount", expr));
   }

   @Override
   protected void incMismatch(Variable profile, Expression expr) {
      profile.member(funCall("incMismatchCount", expr));
   }

   @Override
   protected void incMemoMismatch(Variable profile, Expression expr) {
      profile.member(funCall("incMemoMismatchCount", expr));
   }

   @Override
   protected Variable profileVar(Grammar grammar, NonTerminalRule rule) {
      return var(profileFieldName(rule));
   }

   @Override
   protected Expression charSequence(Object... params) {
      Expression expr = new JavaLiteralExpression<String>("\"\"");
      for (Object param : params) {
         expr = expr.plus(asExpression(param));
      }
      return expr;
   }

   @Override
   protected Expression duplicateString(Object str, Object strLen, Object character) {
      if (character != null) {
         return asExpression(character).plus(asExpression(str).plus(asExpression(character)));
      } else {
         return asExpression(str);
      }
   }

   @Override
   protected Expression charAt(Variable strVar, Expression index) {
      return strVar.member(funCall("charAt", index));
   }

   @Override
   protected void registerRuleMismatch(Expression index, Expression rule) {
      funCallStmt("registerRuleMismatch", index, rule);
   }

   @Override
   protected void registerTerminalMismatch(Expression index, Object expectedExpr) {
      funCallStmt("registerTerminalMismatch", index, expectedExpr);
   }

   @Override
   protected void breakCommand() {
      stmt(new JavaLiteralExpression<String>("break"));
   }

   @Override
   protected int getInitialIndentation() {
      return 3;
   }

   @Override
   protected void setFirstChild(Expression nodeVar, Expression expr) {
      stmt(nodeVar.member(funCall("setFirstChild", expr)));
   }

   @Override
   protected void traceEnterRule(Expression tracePath, Expression buffer, Expression ruleExpr, Expression index) {
      stmt(tracePath.member(funCall("enterRule", buffer, ruleExpr, index)));
   }

   @Override
   protected void traceExitRule(Expression tracePath, Expression buffer, Expression ruleExpr, boolean success) {
      stmt(tracePath.member(funCall("exitRule", buffer, ruleExpr, success)));
   }

   @Override
   protected void setCurrentNode(Expression currentNode, Expression expr) {
      stmt(currentNode.member(funCall("setSibling", expr)));
      setValue(currentNode, currentNode.member(funCall("getSibling")));
   }

   @Override
   protected void setPreviousTailRule(Expression tailRule) {
      setValue(tailRule, tailRule.member(funCall("getPrevious")));
   }

   @Override
   protected void setNextTailRule(Expression tailRule, Expression ruleReference) {
     setValue(tailRule, funCall("new NodeLink<Rule>", ruleReference, tailRule));
   }

   @Override
   protected FunctionDefinition defineFunction(EnumSet<FunctionDefinition.Modifier> modifiers, DataType returnType, String functionName, VariableDeclaration... args) {
      return new JavaFunctionDefinition(modifiers, returnType, functionName, args);
   }

   @Override
   protected String bufferGetCharFunctionName() {
      return "getChar";
   }

   @Override
   protected String bufferMatchCharFunctionName() {
      return "matchChar";
   }

   @Override
   protected String bufferMatchIgnoreCaseCharFunctionName() {
      return "matchIgnoreCaseChar";
   }

   @Override
   protected String bufferMatchCharRangeFunctionName() {
      return "matchCharRange";
   }

   @Override
   protected String bufferMatchIgnoreCaseStringFunctioName() {
      return "matchIgnoreCaseString";
   }

   @Override
   protected String bufferMatchStringFunctionName() {
      return "matchString";
   }
}
