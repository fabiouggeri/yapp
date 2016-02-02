#include <stdlib.h>
#ifdef _MSC_VER
#include <string.h>
#else
#include <strings.h>
#endif
#include "yapp_parser_error.h"

YAPP_PARSER_ERROR *yapp_parser_error_new(INT32 currentIndex, YAPP_RULE *rule, char *text, YAPP_RULE_LINK *tailRule) {
   YAPP_PARSER_ERROR *error = (YAPP_PARSER_ERROR *) malloc(sizeof(YAPP_PARSER_ERROR));
   error->index = currentIndex;
   error->ruleExpected = rule;
   error->textExpected = text;
   error->ruleNode = tailRule;
   return error;
}

void yapp_parser_error_free(YAPP_PARSER_ERROR *error) {
   if (error->textExpected != NULL) {
      free(error->textExpected);
   }
   free(error);
}
