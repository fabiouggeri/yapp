/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.generation.c;

import org.uggeri.yapp.generation.AbstractParserGenerator;
import org.uggeri.yapp.generation.CodeOptimizer;
import org.uggeri.yapp.generation.MemoizeMode;
import org.uggeri.yapp.generation.ParserGenerationException;
import org.uggeri.yapp.generation.SourceFile;
import static org.uggeri.yapp.generation.c.stmt.AbstractCCodeFragment.asExpression;
import org.uggeri.yapp.generation.c.stmt.CBlockStatement;
import org.uggeri.yapp.generation.c.stmt.CFunctionCall;
import org.uggeri.yapp.generation.c.stmt.CFunctionDefinition;
import org.uggeri.yapp.generation.c.stmt.CLineComment;
import org.uggeri.yapp.generation.c.stmt.CLiteralExpression;
import org.uggeri.yapp.generation.c.stmt.CVariable;
import org.uggeri.yapp.generation.c.stmt.CVariableDeclaration;
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
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author fabio
 */
public class CParserGenerator extends AbstractParserGenerator {

   private final List<String> initParserStructValues = new ArrayList<String>();

   public CParserGenerator(final Grammar grammar, final CParserGenerationOptions options) {
      super(grammar, options);
      BOOLEAN.setName("BOOL");
      INTEGER.setName("INT32");
      STRING.setName("char *");
      NODE.setName("YAPP_NODE *");
      TIMEMILLIS.setName("clock_t");
      parserVar.setName("parser");
   }

   private String parserClassName(Grammar g) {
      return g.getGrammarName() + "Parser";
   }

   private String publicFunctionsPrefix(Grammar g) {
      return Character.toLowerCase(g.getGrammarName().charAt(0)) + g.getGrammarName().substring(1) + "Parser_";
   }

   @Override
   protected void initializeCodeGeneration() throws ParserGenerationException {
      generateParserHeader(getGrammar());
      appendSourceHeaderComment();
      getOutput().append("#include \"").append(parserClassName(getGrammar())).append(".h\"\n");
      getOutput().append("#include <time.h>\n");
      getOutput().append("#include <stdlib.h>\n");
      getOutput().append("#include <string.h>\n");
      getOutput().append("#include <stdarg.h>\n");
      createRulesConstants(getGrammar());
      getOutput().append('\n');
      getOutput().append("static ").append(parserClassName(getGrammar())).append(" s_parser_model = ").append(initParserArray()).append(";\n");
      getOutput().append('\n');
      getOutput().append("#define NODE_SET_CURRENT(c, n) c->sibling = n; c = c->sibling\n");
      declareRulesFunctions(getGrammar(), new HashSet<String>());
   }

   @Override
   protected void finalizeCodeGeneration() throws ParserGenerationException {
      getOutput().append('\n');
      getSourceFiles().add(new SourceFile(getOptions().getOutputDirectory(), parserClassName(getGrammar()) + ".c", getOutput()));
   }

   private void declareRulesFunctions(Grammar grammar, Set<String> generatedMethods) throws ParserGenerationException {
      getOutput().append('\n');
      declareGrammarRulesFunctions(grammar, generatedMethods);
      for (Grammar importedGrammar : grammar.getImportGrammars()) {
         declareRulesFunctions(importedGrammar, generatedMethods);
      }
   }

   private void declareGrammarRulesFunctions(Grammar grammar, Set<String> generatedMethods) throws ParserGenerationException {
      for (NonTerminalRule rule : grammar.getDeclaredRules()) {
         try {
            if (!generatedMethods.contains(rule.getMethodName()) && !rule.getOptions().containsKey(NonTerminalOption.FRAGMENT)) {
               getOutput().append("static BOOL ").append(rule.getMethodName()).append('(').append(parserClassName(grammar)).append(" *parser);\n");
            }
         } catch (Exception ex) {
            throw new ParserGenerationException("Error generating rule " + rule.toString(), ex);
         }
      }
   }

   private void createRulesConstants(Grammar grammar) {
      final int rulesCount;
      getOutput().append('\n');
      getOutput().append("YAPP_RULE TERMINAL_RULE = { \"Terminal\", TRUE, 0 };\n");
      rulesCount = generateRulesConstants(grammar, new HashSet<String>(), 1);
      getOutput().append('\n');
      getOutput().append("#define RULES_COUNT ").append(rulesCount).append('\n');
   }

   private int generateRulesConstants(Grammar grammar, Set<String> generatedRules, int rulesCount) {
      rulesCount = generateGrammarRulesConstants(grammar, generatedRules, rulesCount);
      for (Grammar importGrammar : grammar.getImportGrammars()) {
         rulesCount = generateRulesConstants(importGrammar, generatedRules, rulesCount);
      }
      return rulesCount;
   }

   private int generateGrammarRulesConstants(Grammar grammar, Set<String> generatedRules, int rulesCount) {
      for (NonTerminalRule rule : grammar.getDeclaredRules()) {
         if (!generatedRules.contains(rule.getRuleId())
                 && !rule.getOptions().containsKey(NonTerminalOption.FRAGMENT)) {
            generatedRules.add(rule.getRuleId());
            getOutput().append("YAPP_RULE ").append(ruleName(grammar, rule)).append(" = {\"").append(rule.getLabel()).append("\", ");
            if (rule.getOptions().containsKey(NonTerminalOption.ATOMIC)) {
               getOutput().append("TRUE, ");
            } else {
               getOutput().append("FALSE, ");
            }
            if (rule.getOptions().containsKey(NonTerminalOption.SKIP_NODE)) {
               getOutput().append("TRUE, ");
            } else {
               getOutput().append("FALSE, ");
            }
            getOutput().append(rulesCount++).append("};\n");
         }
      }
      return rulesCount;
   }

   private void generateProfilesFields() {
      if (getOptions().isProfile()) {
         getOutput().append("   YAPP_PROFILE *rulesProfiles;\n");
         initParserStructValues.add("NULL");
      }
   }

   @Override
   protected void generateProfilesFunctions() {
      if (getOptions().isProfile()) {
         getOutput().append('\n');
         getOutput().append("static void initializeRulesProfilesMap(").append(parserClassName(getGrammar())).append(" *parser) {\n");
         getOutput().append("   if (parser->rulesProfiles == NULL) {\n");
         getOutput().append("      parser->rulesProfiles = (YAPP_PROFILE*) allocMemory(parser, sizeof(YAPP_PROFILE) * RULES_COUNT);\n");
         getOutput().append("      memset(parser->rulesProfiles, 0, sizeof(YAPP_PROFILE) * RULES_COUNT);\n");
         getOutput().append("   }\n");
         getOutput().append("}\n");
      }
   }

   private void generateParserHeader(Grammar grammar) {
      StringBuilder oldOutput = getOutput();
      setOutput(new StringBuilder());
      appendSourceHeaderComment();
      getOutput().append("#ifndef ").append(parserClassName(grammar).toUpperCase()).append("_INCLUDE").append('\n');
      getOutput().append("#define ").append(parserClassName(grammar).toUpperCase()).append("_INCLUDE").append('\n');
      getOutput().append('\n');
      getOutput().append("#include \"yapp_defs.h\"\n");
      getOutput().append("#include \"yapp_node.h\"\n");
      getOutput().append("#include \"yapp_buffer.h\"\n");
      getOutput().append("#include \"yapp_rule.h\"\n");
      if (getOptions().isProfile()) {
         getOutput().append("#include \"yapp_profile.h\"\n");
      }
      getOutput().append("#include \"yapp_parser_error.h\"\n");
      getOutput().append("#include \"yapp_trace_parser.h\"\n");
      getOutput().append("#include \"yapp_mem_manager.h\"\n");
      getOutput().append('\n');
      getOutput().append("typedef struct {\n");
      getOutput().append("   INT32 index;\n");
      initParserStructValues.add("0");
      getOutput().append("   YAPP_BUFFER *buffer;\n");
      initParserStructValues.add("NULL");
      getOutput().append("   YAPP_MEM_MANAGER *memoryManager;\n");
      initParserStructValues.add("NULL");
      getOutput().append("   BOOL currentRuleIsAtomic;\n");
      initParserStructValues.add("FALSE");
      getOutput().append("   YAPP_NODE *currentNode;\n");
      initParserStructValues.add("NULL");
      getOutput().append("   YAPP_NODE *lastNodeCreated;\n");
      initParserStructValues.add("NULL");
      if (getOptions().isCatchMismatches()) {
         getOutput().append("   YAPP_RULE_LINK *tailRule;\n");
         initParserStructValues.add("NULL");
         getOutput().append("   BOOL ignoreMismatch;\n");
         initParserStructValues.add("FALSE");
         getOutput().append("   BOOL catchTerminalMismatch;\n");
         initParserStructValues.add("TRUE");
         getOutput().append("   INT32 mismatchIndex;\n");
         initParserStructValues.add("-1");
         getOutput().append("   YAPP_PARSER_ERROR *mismatches;\n");
         initParserStructValues.add("NULL");
      }
      if (getOptions().isGenerateTraceCode()) {
         getOutput().append("   YAPP_TRACE_PARSER *tracePath;\n");
         initParserStructValues.add("NULL");
         getOutput().append("   BOOL trace;\n");
         initParserStructValues.add("FALSE");
      }
      generateMemoFields(getGrammar(), new HashSet<String>(), 3);
      generateProfilesFields();
      generateLeftRecursionFields(getGrammar(), new HashSet<String>(), 3);
      getOutput().append("} ").append(parserClassName(getGrammar())).append(";\n");
      declareRulesConstants(getGrammar());
      declarePublicFunctions(getGrammar());
      getOutput().append("#endif\n");
      getSourceFiles().add(new SourceFile(getOptions().getOutputDirectory(), parserClassName(grammar) + ".h", getOutput()));
      setOutput(oldOutput);
   }

   private void declarePublicFunctions(Grammar grammar) {
      getOutput().append('\n');
      getOutput().append("extern YAPP_NODE* ").append(publicFunctionsPrefix(grammar)).append("parse(").append(parserClassName(grammar)).append(" *parser);\n");
      getOutput().append("extern ").append(parserClassName(grammar)).append("* ").append(publicFunctionsPrefix(grammar)).append("new(YAPP_BUFFER *inputBuffer);\n");
      getOutput().append("extern ").append(parserClassName(grammar)).append("* ").append(publicFunctionsPrefix(grammar)).append("newManaged(YAPP_MEM_MANAGER *memoryManager, YAPP_BUFFER *inputBuffer);\n");
      getOutput().append("extern void ").append(publicFunctionsPrefix(grammar)).append("free(").append(parserClassName(grammar)).append(" *parser);\n");
      getOutput().append("extern void ").append(publicFunctionsPrefix(getGrammar())).append("setTraceParser(").append(parserClassName(getGrammar())).append(" *parser, YAPP_TRACE_PARSER *tracePath);\n");
      getOutput().append("extern void ").append(publicFunctionsPrefix(getGrammar())).append("setTrace(").append(parserClassName(getGrammar())).append(" *parser, BOOL trace);\n");
      getOutput().append("extern YAPP_PARSER_ERROR* ").append(publicFunctionsPrefix(grammar)).append("getMismatches(").append(parserClassName(grammar)).append(" *parser);\n");
   }

   private void generateMemoFields(Grammar grammar, Set<String> generatedRules, int indent) {
      if (! getOptions().getMemoizeMode().equals(MemoizeMode.NONE)) {
         generateGrammarMemoFields(grammar, generatedRules, indent);
         for (Grammar importGrammar : grammar.getImportGrammars()) {
            generateMemoFields(importGrammar, generatedRules, indent);
         }
      }
   }

   private String arrayOfValue(String str, int count) {
      final StringBuilder sb = new StringBuilder("{");
      boolean first = true;
      for (int i = 0; i < count; i++) {
         if (first) {
            first = false;
            sb.append(str);
         } else {
            sb.append(',').append(str);
         }
      }
      sb.append('}');
      return sb.toString();
   }

   private void generateGrammarMemoFields(Grammar grammar, Set<String> generatedRules, int indent) {
      for (NonTerminalRule rule : grammar.getDeclaredRules()) {
         if (!generatedRules.contains(rule.getName()) && !rule.getOptions().containsKey(NonTerminalOption.FRAGMENT)) {
            generatedRules.add(rule.getName());
            if (isMemoizeRule(rule)) {
               if (getNumberMemorizations() > 1) {
                  indent(getOutput(), indent).append("INT32 ").append(varName(memoVarStart(rule))).append("[").append(getNumberMemorizations()).append("];\n");
                  initParserStructValues.add(arrayOfValue("-1", getNumberMemorizations()));
                  indent(getOutput(), indent).append("INT32 ").append(varName(memoVarEnd(rule))).append("[").append(getNumberMemorizations()).append("];\n");
                  initParserStructValues.add(arrayOfValue("0", getNumberMemorizations()));
                  indent(getOutput(), indent).append("YAPP_NODE *").append(varName(memoVarFirstNode(rule))).append("[").append(getNumberMemorizations()).append("];\n");
                  initParserStructValues.add(arrayOfValue("NULL", getNumberMemorizations()));
               } else {
                  indent(getOutput(), indent).append("INT32 ").append(varName(memoVarStart(rule))).append(";\n");
                  initParserStructValues.add("-1");
                  indent(getOutput(), indent).append("INT32 ").append(varName(memoVarEnd(rule))).append(";\n");
                  initParserStructValues.add("0");
                  indent(getOutput(), indent).append("YAPP_NODE *").append(varName(memoVarFirstNode(rule))).append(";\n");
                  initParserStructValues.add("NULL");
               }
            }
         }
      }
   }
   
   private void generateLeftRecursionFields(Grammar grammar, Set<String> generatedRules, int indent) {
      generateGrammarLeftRecursionFields(grammar, generatedRules, indent);
      for (Grammar importGrammar : grammar.getImportGrammars()) {
         generateLeftRecursionFields(importGrammar, generatedRules, indent);
      }
   }

   private void generateGrammarLeftRecursionFields(Grammar grammar, Set<String> generatedRules, int indent) {
      for (NonTerminalRule rule : grammar.getDeclaredRules()) {
         if (rule.hasLeftRecursion() && !generatedRules.contains(rule.getName()) && !rule.getOptions().containsKey(NonTerminalOption.FRAGMENT)) {
            generatedRules.add(rule.getName());
            indent(getOutput(), indent).append("INT32 ").append(varName(ruleLastIndexVar(rule))).append(";\n");
            initParserStructValues.add("-1");
            indent(getOutput(), indent).append("INT32 ").append(varName(ruleTryVar(rule))).append(";\n");
            initParserStructValues.add("0");
         }
      }
   }

   private CharSequence initParserArray() {
      final StringBuilder sb = new StringBuilder("{");
      boolean first = true;
      for (String value : initParserStructValues) {
         if (first) {
            first = false;
         } else {
            sb.append(", ");
         }
         sb.append(value);
      }
      sb.append('}');
      return sb;
   }

   @Override
   protected void generateParserFunctions() {
      getOutput().append('\n');
      getOutput().append(parserClassName(getGrammar())).append("* ").append(publicFunctionsPrefix(getGrammar())).append("new(YAPP_BUFFER *inputBuffer) {\n");
      getOutput().append("   ").append(parserClassName(getGrammar())).append(" *parser = (").append(parserClassName(getGrammar())).append("*) malloc(sizeof(").append(parserClassName(getGrammar())).append("));\n");
      getOutput().append("   memcpy(parser, &s_parser_model, sizeof(").append(parserClassName(getGrammar())).append("));\n");
      getOutput().append("   parser->buffer = inputBuffer;\n");
      getOutput().append("   parser->currentNode = createNode(parser, NULL, -1, -1, FALSE, FALSE);\n");
      getOutput().append("   return parser;\n");
      getOutput().append("}\n");
      getOutput().append('\n');
      getOutput().append(parserClassName(getGrammar())).append("* ").append(publicFunctionsPrefix(getGrammar())).append("newManaged(YAPP_MEM_MANAGER *memoryManager, YAPP_BUFFER *inputBuffer) {\n");
      getOutput().append("   ").append(parserClassName(getGrammar())).append(" *parser = (").append(parserClassName(getGrammar())).append("*) malloc(sizeof(").append(parserClassName(getGrammar())).append("));\n");
      getOutput().append("   memcpy(parser, &s_parser_model, sizeof(").append(parserClassName(getGrammar())).append("));\n");
      getOutput().append("   parser->memoryManager = memoryManager;\n");
      getOutput().append("   parser->buffer = inputBuffer;\n");
      getOutput().append("   parser->currentNode = createNode(parser, NULL, -1, -1, FALSE, FALSE);\n");
      getOutput().append("   return parser;\n");
      getOutput().append("}\n");
      getOutput().append('\n');
      getOutput().append("void ").append(publicFunctionsPrefix(getGrammar())).append("free(").append(parserClassName(getGrammar())).append(" *parser) {\n");
      getOutput().append("   YAPP_NODE *node;\n");
      getOutput().append("   YAPP_NODE *nextNode;\n");
      getOutput().append("   node = parser->lastNodeCreated;\n");
      getOutput().append("   while(node != NULL) {\n");
      getOutput().append("       nextNode = node->nextNode;\n");
      getOutput().append("       yapp_node_free(node);\n");
      getOutput().append("       node = nextNode;\n");
      getOutput().append("   }\n");
      getOutput().append("   free(parser);\n");
      getOutput().append("}\n");
      getOutput().append('\n');
      getOutput().append("YAPP_NODE* ").append(publicFunctionsPrefix(getGrammar())).append("parse(").append(parserClassName(getGrammar())).append(" *parser) {\n");
      if (getOptions().isProfile()) {
         getOutput().append("   initializeRulesProfilesMap(parser);\n");
      }
      getOutput().append("   if (").append(getGrammar().getMainRule().getMethodName()).append("(parser)) {\n");
      getOutput().append("      return parser->currentNode;\n");
      getOutput().append("   } else {\n");
      getOutput().append("      return NULL;\n");
      getOutput().append("   }\n");
      getOutput().append("}\n");
      getOutput().append('\n');
      getOutput().append("INT32 ").append(publicFunctionsPrefix(getGrammar())).append("rulesCount(").append(parserClassName(getGrammar())).append(" *parser) {\n");
      getOutput().append("   return RULES_COUNT;\n");
      getOutput().append("}\n");
   }

   private void declareRulesConstants(Grammar grammar) {
      getOutput().append('\n');
      getOutput().append("extern YAPP_RULE TERMINAL_RULE;\n");
      declareRulesConstants(grammar, new HashSet<String>(), 0);
   }

   private void declareRulesConstants(Grammar grammar, Set<String> generatedRules, int indent) {
      declareGrammarRulesConstants(grammar, generatedRules, indent);
      for (Grammar importGrammar : grammar.getImportGrammars()) {
         declareRulesConstants(importGrammar, generatedRules, indent);
      }
   }

   private void declareGrammarRulesConstants(Grammar grammar, Set<String> generatedRules, int indent) {
      for (NonTerminalRule rule : grammar.getDeclaredRules()) {
         if (!generatedRules.contains(rule.getRuleId())
                 && !rule.getOptions().containsKey(NonTerminalOption.FRAGMENT)) {
            generatedRules.add(rule.getRuleId());
            indent(getOutput().append('\n'), indent).append("extern YAPP_RULE ").append(ruleName(grammar, rule)).append(";\n");
         }
      }
   }

   @Override
   protected BlockStatement createBlockStatement() {
      return new CBlockStatement();
   }

   @Override
   protected Expression charSequence(Object... params) {
      Object[] paramsAux = new Object[params.length + 2];
      paramsAux[0] = parserVar;
      paramsAux[1] = params.length;
      System.arraycopy(params, 0, paramsAux, 2, params.length);
      return funCall("charSequence", paramsAux);
   }

   @Override
   protected Expression duplicateString(Object str, Object strLen, Object character) {
      return funCall("strDup", parserVar, str, strLen, character);
   }

   @Override
   protected void generateAuxiliaryFunctions() {
      generateMemoryFuntions();
      generateStringFuntions();
      generateNodeFuntions();
   }

   private void generateMemoryFuntions() {
      getOutput().append('\n');
      getOutput().append("static void * allocMemory(").append(parserClassName(getGrammar())).append(" *parser, INT32 size) {\n");
      getOutput().append("   if (parser->memoryManager != NULL) {\n");
      getOutput().append("      return (void *) yapp_mem_alloc(parser->memoryManager, size);\n");
      getOutput().append("   } else {\n");
      getOutput().append("      return malloc(size);\n");
      getOutput().append("   }\n");
      getOutput().append("}\n");
   }

   private void generateStringFuntions() {
      getOutput().append('\n');
      getOutput().append("static char * strDup(").append(parserClassName(getGrammar())).append(" *parser, char *str, INT32 strLen, char encloseChar) {\n");
      getOutput().append("   char *result;\n");
      getOutput().append("   if (encloseChar == '\\0') {\n");
      getOutput().append("      result = (char *) allocMemory(parser, sizeof(char) * (strLen + 1));\n");
      getOutput().append("      memcpy(result, str, strLen + 1);\n");
      getOutput().append("   } else {\n");
      getOutput().append("      result = (char *) allocMemory(parser, sizeof(char) * (strLen + 3));\n");
      getOutput().append("      result[0] = encloseChar;\n");
      getOutput().append("      memcpy(&result[1], str, strLen);\n");
      getOutput().append("      result[strLen + 1] = encloseChar;\n");
      getOutput().append("      result[strLen + 2] = '\\0';\n");
      getOutput().append("   }\n");
      getOutput().append("   return result;\n");
      getOutput().append("}\n");
      getOutput().append("\n");
      getOutput().append("static char * charSequence(").append(parserClassName(getGrammar())).append(" *parser, INT32 count, ...) {\n");
      getOutput().append("   char *result = (char *) allocMemory(parser, sizeof(char) * (count + 1));\n");
      getOutput().append("   INT32 i = 0;\n");
      getOutput().append("   va_list va;\n");
      getOutput().append("   va_start(va, count);\n");
      getOutput().append("   for(i = 0; i < count; i++) {\n");
      getOutput().append("      result[i] = va_arg(va, char);\n");
      getOutput().append("   }\n");
      getOutput().append("   va_end(va);\n");
      getOutput().append("   result[i] = '\\0';\n");
      getOutput().append("   return result;\n");
      getOutput().append("}\n");
   }

   private void generateNodeFuntions() {
      getOutput().append('\n');
      getOutput().append("static YAPP_NODE * createNode(").append(parserClassName(getGrammar())).append(" *parser, YAPP_RULE *rule, INT32 startIndex, INT32 endIndex, BOOL semantic, BOOL skipNode) {\n");
      getOutput().append("   YAPP_NODE *node;\n");
      getOutput().append("   if (parser->memoryManager != NULL) {\n");
      getOutput().append("      node = yapp_node_newManaged(parser->memoryManager, rule, startIndex, endIndex, semantic, skipNode);\n");
      getOutput().append("   } else {\n");
      getOutput().append("      node = yapp_node_new(rule, startIndex, endIndex, semantic, skipNode);\n");
      getOutput().append("      node->nextNode = parser->lastNodeCreated;\n");
      getOutput().append("      parser->lastNodeCreated = node;\n");
      getOutput().append("   }\n");
      getOutput().append("   return node;\n");
      getOutput().append("}\n");
   }

   @Override
   protected void generateErrorFunctions() {
      getOutput().append('\n');
      if (getOptions().isCatchMismatches()) {
         getOutput().append("YAPP_PARSER_ERROR* ").append(publicFunctionsPrefix(getGrammar())).append("getMismatches(").append(parserClassName(getGrammar())).append(" *parser) {\n");
         getOutput().append("   return parser->mismatches;\n");
         getOutput().append("}\n");
         getOutput().append('\n');
         getOutput().append("static void registerRuleMismatch(").append(parserClassName(getGrammar())).append(" *parser, INT32 currentIndex, YAPP_RULE *rule) {\n");
         getOutput().append("   if (! parser->ignoreMismatch) {\n");
         getOutput().append("      if (currentIndex > parser->mismatchIndex) {\n");
         getOutput().append("         parser->mismatchIndex = currentIndex;\n");
         getOutput().append("         parser->mismatches = NULL;\n");
         getOutput().append("         parser->mismatches = yapp_parser_error_new(currentIndex, rule, NULL, parser->tailRule);\n");
         getOutput().append("      } else if (currentIndex == parser->mismatchIndex) {\n");
         getOutput().append("         parser->mismatches = yapp_parser_error_new(currentIndex, rule, NULL, parser->tailRule);\n");
         getOutput().append("      }\n");
         getOutput().append("   }\n");
         getOutput().append("}\n");
         getOutput().append('\n');
         getOutput().append("static void registerTerminalMismatch(").append(parserClassName(getGrammar())).append(" *parser, INT32 currentIndex, char *expected) {\n");
         getOutput().append("   if (! parser->ignoreMismatch) {\n");
         getOutput().append("      if (currentIndex > parser->mismatchIndex) {\n");
         getOutput().append("         parser->mismatchIndex = currentIndex;\n");
         getOutput().append("         parser->mismatches = NULL;\n");
         getOutput().append("         parser->mismatches = yapp_parser_error_new(currentIndex, NULL, expected, parser->tailRule);\n");
         getOutput().append("      } else if (currentIndex == parser->mismatchIndex) {\n");
         getOutput().append("         parser->mismatches = yapp_parser_error_new(currentIndex, NULL, expected, parser->tailRule);\n");
         getOutput().append("      }\n");
         getOutput().append("   }\n");
         getOutput().append("}\n");
         getOutput().append('\n');
         getOutput().append("YAPP_RULE_LINK *freeTailRule(").append(parserClassName(getGrammar())).append(" *parser) {\n");
         getOutput().append("   YAPP_RULE_LINK *previous = parser->tailRule->previous;\n");
         getOutput().append("   yapp_rule_linkeFree(parser->tailRule);\n");
         getOutput().append("   return previous;\n");
         getOutput().append("}\n");
         getOutput().append('\n');
      } else {
         getOutput().append("YAPP_PARSER_ERROR* ").append(publicFunctionsPrefix(getGrammar())).append("getMismatches(").append(parserClassName(getGrammar())).append(" *parser) {\n");
         getOutput().append("   return NULL;\n");
         getOutput().append("}\n");
      }
   }

   @Override
   protected void generateTraceFunctions() {
      getOutput().append('\n');
      if (getOptions().isGenerateTraceCode()) {
         getOutput().append("void ").append(publicFunctionsPrefix(getGrammar())).append("setTraceParser(").append(parserClassName(getGrammar())).append(" *parser, YAPP_TRACE_PARSER *tracePath) {\n");
         getOutput().append("   parser->tracePath = tracePath;\n");
         getOutput().append("}\n");
         getOutput().append('\n');
         getOutput().append("void ").append(publicFunctionsPrefix(getGrammar())).append("setTrace(").append(parserClassName(getGrammar())).append(" *parser, BOOL trace) {\n");
         getOutput().append("   parser->trace = trace;\n");
         getOutput().append("}\n");
      } else {
         getOutput().append("void ").append(publicFunctionsPrefix(getGrammar())).append("setTraceParser(").append(parserClassName(getGrammar())).append(" *parser, YAPP_TRACE_PARSER *tracePath) {\n");
         getOutput().append("}\n");
         getOutput().append('\n');
         getOutput().append("void ").append(publicFunctionsPrefix(getGrammar())).append("setTrace(").append(parserClassName(getGrammar())).append(" *parser, BOOL trace) {\n");
         getOutput().append("}\n");
      }
   }

   private void appendSourceHeaderComment() {
      getOutput().append("/***************************************************\n");
      getOutput().append(" * PEG Parser - Generated By YAPP Parser Generator *\n");
      getOutput().append(" ***************************************************/\n\n");
   }

   @Override
   protected Variable var(String varName) {
      return new CVariable(varName);
   }

   @Override
   protected FunctionCall funCall(String functionName, Object... parameters) {
      Expression[] arrayAux = new Expression[parameters.length];
      for (int i = 0; i < parameters.length; i++) {
         arrayAux[i] = asExpression(parameters[i]);
      }
      return new CFunctionCall(functionName, arrayAux);
   }

   @Override
   protected LineComment lineComment(String text) {
      return new CLineComment(text);
   }

   @Override
   protected String sufixRuleName() {
      return "_rule";
   }

   @Override
   protected String ruleName(Grammar grammar, NonTerminalRule rule) {
      return grammar.getGrammarName().toUpperCase() + "_" + rule.getRuleId();
   }

   @Override
   protected Expression ruleReference(Grammar grammar, NonTerminalRule rule) {
      return new CLiteralExpression("&" + ruleName(grammar, rule));
   }

   @Override
   protected CodeOptimizer createOptimizer() {
      return new CCodeOptimizer();
   }

   @Override
   protected void setCurrentNode(Expression currentNode, Expression expr) {
      funCallStmt("NODE_SET_CURRENT", currentNode, expr);
   }

   @Override
   protected void setSibling(Variable nodeVar, Expression expr) {
      setValue(var(nodeVar.getName() + "->sibling"), expr);
   }

   @Override
   protected Expression getSibling(Variable nodeVar) {
      return var(nodeVar.getName() + "->sibling");
   }

   @Override
   protected Expression getFirstChild(Expression nodeVar) {
      return var(nodeVar + "->firstChild");
   }

   @Override
   protected void setFirstChild(Expression nodeVar, Expression expr) {
      StringBuilder sb = new StringBuilder();
      nodeVar.appendCode(sb, 0);
      sb.append("->firstChild");
      setValue(var(sb.toString()), expr);
   }

   @Override
   protected Expression timeFunCall() {
      return funCall("clock");
   }

   @Override
   protected Expression createNodeFunCall(Object rule, Object startIndex, Object index, Object semantic, Object skipNode) {
      return funCall("createNode", parserVar, rule, startIndex, index, semantic, skipNode);
   }

   @Override
   protected Expression createErrorFunCall(Object index, Object rule, Object expectedValue, Object tailRule) {
      return funCall("yapp_parser_error_new", index, rule, expectedValue, tailRule);
   }

   @Override
   protected Expression terminalRule() {
      return new CLiteralExpression<String>("&TERMINAL_RULE");
   }

   @Override
   protected String bufferMatchCharFunctionName() {
      return "yapp_buffer_matchChar";
   }

   @Override
   protected String bufferMatchCharRangeFunctionName() {
      return "yapp_buffer_matchCharRange";
   }

   @Override
   protected String bufferMatchStringFunctionName() {
      return "yapp_buffer_matchString";
   }

   @Override
   protected String bufferMatchIgnoreCaseStringFunctioName() {
      return "yapp_buffer_matchIgnoreCaseString";
   }

   @Override
   protected String bufferGetCharFunctionName() {
      return "yapp_buffer_getChar";
   }

   @Override
   protected String bufferMatchIgnoreCaseCharFunctionName() {
      return "yapp_buffer_matchIgnoreCaseChar";
   }

   @Override
   protected void incMatch(Variable profile, Expression expr) {
      funCallStmt("yapp_profile_incMatch", profile, expr);
   }

   @Override
   protected void incMemoMatch(Variable profile, Expression expr) {
      funCallStmt("yapp_profile_incMemoMatch", profile, expr);
   }

   @Override
   protected void incMismatch(Variable profile, Expression expr) {
      funCallStmt("yapp_profile_incMismatch", profile, expr);
   }

   @Override
   protected void incMemoMismatch(Variable profile, Expression expr) {
      funCallStmt("yapp_profile_incMemoMismatch", profile, expr);
   }

   @Override
   protected void setPreviousTailRule(Expression tailRule) {
      setValue(tailRule, funCall("freeTailRule", parserVar));
   }

   @Override
   protected void setNextTailRule(Expression tailRule, Expression ruleReference) {
      setValue(tailRule, funCall("yapp_rule_linkNew", ruleReference, tailRule));
   }

   @Override
   protected void traceEnterRule(Expression tracePath, Expression buffer, Expression ruleExpr, Expression index) {
      funCallStmt("yapp_trace_enterRule", tracePath, buffer, ruleExpr, index);
   }

   @Override
   protected void traceExitRule(Expression tracePath, Expression buffer, Expression ruleExpr, boolean success) {
      funCallStmt("yapp_trace_exitRule", tracePath, buffer, ruleExpr, success);
   }

   @Override
   protected Variable profileVar(Grammar grammar, NonTerminalRule rule) {
      return var("&parser->rulesProfiles[" + ruleName(grammar, rule) + ".value]");
   }

   @Override
   protected Expression charAt(Variable var, Expression index) {
      return var.get(index);
   }

   @Override
   protected void registerRuleMismatch(Expression index, Expression rule) {
      funCallStmt("registerRuleMismatch", parserVar, index, rule);
   }

   @Override
   protected void registerTerminalMismatch(Expression index, Object expectedExpr) {
      funCallStmt("registerTerimanlMismatch", parserVar, index, expectedExpr);
   }

   @Override
   protected void breakCommand() {
      stmt(new CLiteralExpression<String>("break"));
   }

   @Override
   protected FunctionDefinition defineFunction(EnumSet<FunctionDefinition.Modifier> modifiers, DataType returnType, String functionName, VariableDeclaration... args) {
      VariableDeclaration[] allArgs = new VariableDeclaration[args.length + 1];
      allArgs[0] = new CVariableDeclaration(new DataTypeImpl(parserClassName(getGrammar()) + " *"), parserVar);
      if (args.length > 0) {
         System.arraycopy(args, 0, allArgs, 1, args.length);
      }
      return new CFunctionDefinition(modifiers, returnType, functionName, allArgs);
   }
}
