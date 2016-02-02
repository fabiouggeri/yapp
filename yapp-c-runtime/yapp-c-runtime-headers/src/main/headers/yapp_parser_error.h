#ifndef _YAPP_PARSER_ERROR_

#define _YAPP_PARSER_ERROR_

#include "yapp_defs.h"
#include "yapp_rule.h"

typedef struct _YAPP_RULE_NODE {
   YAPP_RULE *rule;
   struct _YAPP_RULE_NODE *next;
} YAPP_RULE_NODE;

typedef struct _YAPP_PARSER_ERROR {
   INT32 index;
   YAPP_RULE *ruleExpected;
   char *textExpected;
   YAPP_RULE_LINK *ruleNode;
} YAPP_PARSER_ERROR;

extern YAPP_PARSER_ERROR *yapp_parser_error_new(INT32 currentIndex, YAPP_RULE *rule, char *text, YAPP_RULE_LINK *tailRule);
extern void yapp_parser_error_free(YAPP_PARSER_ERROR *error);

#endif

