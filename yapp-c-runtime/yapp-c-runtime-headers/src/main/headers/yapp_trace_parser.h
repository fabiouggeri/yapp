#ifndef _YAPP_TRACE_PARSER_

#define _YAPP_TRACE_PARSER_

#include "yapp_defs.h"
#include "yapp_buffer.h"

typedef void ( * TRACE_ENTER_RULE )(YAPP_BUFFER *inputBuffer, char *ruleName, INT32 index);
typedef void ( * TRACE_EXIT_RULE )(YAPP_BUFFER *inputBuffer, INT32 index, BOOL success);

typedef struct _YAPP_TRACE_PARSER {
   TRACE_ENTER_RULE enterRule;
   TRACE_EXIT_RULE exitRule;
} YAPP_TRACE_PARSER;

void yapp_trace_enterRule(YAPP_TRACE_PARSER *trace, YAPP_BUFFER *inputBuffer, char *ruleName, INT32 index);
void yapp_trace_exitRule(YAPP_TRACE_PARSER *trace, YAPP_BUFFER *inputBuffer, INT32 index, BOOL success);

#endif