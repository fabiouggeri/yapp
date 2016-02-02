#ifndef _YAPP_RULE_

#define _YAPP_RULE_

#include "yapp_defs.h"

typedef struct _YAPP_RULE {
   char  *label;
   BOOL  atomic;
   BOOL  skiped;
   INT32 value;
} YAPP_RULE;

typedef struct _YAPP_RULE_LINK {
   YAPP_RULE *rule;
   struct _YAPP_RULE_LINK *previous;
} YAPP_RULE_LINK;

extern YAPP_RULE *yapp_rule_new();
extern void yapp_rule_free(YAPP_RULE *rule);
extern char *yapp_rule_getLabel(YAPP_RULE *rule);
extern BOOL yapp_rule_isAtomic(YAPP_RULE *rule);
extern BOOL yapp_rule_isSkiped(YAPP_RULE *rule);
extern INT32 yapp_rule_getValue(YAPP_RULE *rule);

extern YAPP_RULE_LINK *yapp_rule_linkNew(YAPP_RULE *rule, YAPP_RULE_LINK *previousRuleLink);
extern void yapp_rule_linkeFree(YAPP_RULE_LINK *link);

#endif
