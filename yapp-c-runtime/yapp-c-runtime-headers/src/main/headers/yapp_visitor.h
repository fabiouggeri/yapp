#ifndef _YAPP_VISITOR_

#define _YAPP_VISITOR_

#include "yapp_defs.h"
#include "yapp_node.h"
#include "yapp_rule.h"

typedef void ( * RULE_VISITOR )( YAPP_NODE * );
typedef void ( * RULE_VISITOR )( YAPP_NODE * );

typedef struct _YAPP_VISITOR {
   INT32 rulesCount;
   RULE_VISITOR rulesVisitor[1];
} YAPP_VISITOR;

YAPP_VISITOR* yapp_visitor_new(INT32 rulesCount);

void yapp_visitor_free(YAPP_VISITOR *visitor);

void yapp_visitor_enterRule(YAPP_VISITOR *visitor, YAPP_NODE *node);

void yapp_visitor_exitRule(YAPP_VISITOR *visitor, YAPP_NODE *node);

void yapp_visitor_setEnterRule(YAPP_VISITOR *visitor, YAPP_RULE *rule, RULE_VISITOR enterVisitor);

void yapp_visitor_setExitRule(YAPP_VISITOR *visitor, YAPP_RULE *rule, RULE_VISITOR exitVisitor);

void yapp_visitor_treeWalker(YAPP_VISITOR *visitor, YAPP_NODE *node);

#endif