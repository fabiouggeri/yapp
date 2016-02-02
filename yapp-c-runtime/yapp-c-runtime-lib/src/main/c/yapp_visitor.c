#include <stdlib.h>
#include "yapp_visitor.h"

static void yapp_visitor_dummy(YAPP_NODE *node) {
}

YAPP_VISITOR* yapp_visitor_new(INT32 rulesCount) {
   YAPP_VISITOR* visitor = (YAPP_VISITOR *) malloc(sizeof(YAPP_VISITOR) + (sizeof(RULE_VISITOR) * rulesCount * 2));
   visitor->rulesCount = rulesCount;
   memset(visitor->rulesVisitor, yapp_visitor_dummy, rulesCount * 2);
   return visitor;
}

void yapp_visitor_free(YAPP_VISITOR *visitor) {
   free(visitor);
}

void yapp_visitor_enterRule(YAPP_VISITOR *visitor, YAPP_NODE *node) {
   visitor->rulesVisitor[node->rule->value - 1](node);
}

void yapp_visitor_exitRule(YAPP_VISITOR *visitor, YAPP_NODE *node) {
   visitor->rulesVisitor[node->rule->value - 1 + visitor->rulesCount](node);
}

void yapp_visitor_setEnterRule(YAPP_VISITOR *visitor, YAPP_RULE *rule, RULE_VISITOR enterVisitor) {
   visitor->rulesVisitor[rule->value - 1] = enterVisitor;
}

void yapp_visitor_setExitRule(YAPP_VISITOR *visitor, YAPP_RULE *rule, RULE_VISITOR exitVisitor) {
   visitor->rulesVisitor[rule->value - 1] = exitVisitor;
}

void yapp_visitor_treeWalker(YAPP_VISITOR *visitor, YAPP_NODE *node) {
   YAPP_NODE *child = node->firstChild;
   yapp_visitor_enterRule(visitor, node);
   while (child != NULL) {
      yapp_visitor_treeWalker(child, visitor);
      child = child->sibling;
   }
   yapp_visitor_exitRule(visitor, node);
}