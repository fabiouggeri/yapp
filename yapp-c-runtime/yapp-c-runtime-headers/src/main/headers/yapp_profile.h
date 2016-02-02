#ifndef _YAPP_PROFILE_

#define _YAPP_PROFILE_

#include "yapp_defs.h"
#include "yapp_rule.h"

typedef struct _YAPP_PROFILE {
   YAPP_RULE *rule;
   INT64 memoMatchCount;
   INT64 memoMatchTime; 
   INT64 memoMismatchCount; 
   INT64 memoMismatchTime; 
   INT64 matchCount; 
   INT64 matchTime; 
   INT64 mismatchCount; 
   INT64 mismatchTime; 
} YAPP_PROFILE;

YAPP_PROFILE* yapp_profile_new();
void yapp_profile_free(YAPP_PROFILE *profile);
YAPP_RULE *yapp_profile_getRule(YAPP_PROFILE *profile);
INT64 yapp_profile_getMemoMatchCount(YAPP_PROFILE *profile);
INT64 yapp_profile_getMemoMatchTime(YAPP_PROFILE *profile);
INT64 yapp_profile_getMemoMismatchCount(YAPP_PROFILE *profile);
INT64 yapp_profile_getMemoMismatchTime(YAPP_PROFILE *profile);
INT64 yapp_profile_getMatchCount(YAPP_PROFILE *profile);
INT64 yapp_profile_getMatchTime(YAPP_PROFILE *profile);
INT64 yapp_profile_getMismatchCount(YAPP_PROFILE *profile);
INT64 yapp_profile_getMismatchTime(YAPP_PROFILE *profile);
void yapp_profile_incMemoMatch(YAPP_PROFILE *profile, INT64 timeMillis);
void yapp_profile_incMemoMismatch(YAPP_PROFILE *profile, INT64 timeMillis);
void yapp_profile_incMatch(YAPP_PROFILE *profile, INT64 timeMillis);
void yapp_profile_incMismatch(YAPP_PROFILE *profile, INT64 timeMillis);

#endif