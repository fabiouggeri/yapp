#include "yapp_trace_parser.h"

void yapp_trace_enterRule(YAPP_TRACE_PARSER *trace, YAPP_BUFFER *inputBuffer, char *ruleName, INT32 index) {
   trace->enterRule(inputBuffer, ruleName, index);
}

void yapp_trace_exitRule(YAPP_TRACE_PARSER *trace, YAPP_BUFFER *inputBuffer, INT32 index, BOOL success) {
   trace->exitRule(inputBuffer, index, success);
}
