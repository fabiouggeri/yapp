#include <stdlib.h>
#include "yapp_rule.h"

YAPP_RULE *yapp_rule_new() {
   return (YAPP_RULE *) malloc(sizeof(YAPP_RULE));
}

void yapp_rule_free(YAPP_RULE *rule) {
   free(rule);
}

char *yapp_rule_getLabel(YAPP_RULE *rule) {
   return rule->label;
}

BOOL yapp_rule_isAtomic(YAPP_RULE *rule) {
   return rule->atomic;
}

BOOL yapp_rule_isSkiped(YAPP_RULE *rule) {
   return rule->skiped;
}

INT32 yapp_rule_getValue(YAPP_RULE *rule) {
   return rule->value;
}

YAPP_RULE_LINK *yapp_rule_linkNew(YAPP_RULE *rule, YAPP_RULE_LINK *previousRuleLink) {
   YAPP_RULE_LINK *link = (YAPP_RULE_LINK *)malloc(sizeof(YAPP_RULE_LINK));
   link->rule = rule;
   link->previous = previousRuleLink;
   return link;
}

void yapp_rule_linkeFree(YAPP_RULE_LINK *link) {
   free(link);
}
