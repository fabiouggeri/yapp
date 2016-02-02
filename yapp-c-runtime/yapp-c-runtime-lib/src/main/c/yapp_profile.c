#include <stdlib.h>
#include "yapp_profile.h"

YAPP_PROFILE* yapp_profile_new() {
   return (YAPP_PROFILE*)malloc(sizeof(YAPP_PROFILE));
}

void yapp_profile_free(YAPP_PROFILE *profile) {
   free(profile);
}

YAPP_RULE *yapp_profile_getRule(YAPP_PROFILE *profile) {
   return profile->rule;
}

INT64 yapp_profile_getMemoMatchCount(YAPP_PROFILE *profile) {
   return profile->memoMatchCount;
}

INT64 yapp_profile_getMemoMatchTime(YAPP_PROFILE *profile) {
   return profile->memoMatchTime;
}

INT64 yapp_profile_getMemoMismatchCount(YAPP_PROFILE *profile) {
   return profile->memoMismatchCount;
}

INT64 yapp_profile_getMemoMismatchTime(YAPP_PROFILE *profile) {
   return profile->memoMismatchTime;
}

INT64 yapp_profile_getMatchCount(YAPP_PROFILE *profile) {
   return profile->matchCount;
}

INT64 yapp_profile_getMatchTime(YAPP_PROFILE *profile) {
   return profile->matchTime;
}

INT64 yapp_profile_getMismatchCount(YAPP_PROFILE *profile) {
   return profile->mismatchTime;
}

INT64 yapp_profile_getMismatchTime(YAPP_PROFILE *profile) {
   return profile->mismatchTime;
}

void yapp_profile_incMemoMatch(YAPP_PROFILE *profile, INT64 timeMillis) {
   ++profile->memoMatchCount;
   profile->memoMatchTime += timeMillis;
}

void yapp_profile_incMemoMismatch(YAPP_PROFILE *profile, INT64 timeMillis) {
   ++profile->memoMismatchCount;
   profile->memoMismatchTime += timeMillis;
}

void yapp_profile_incMatch(YAPP_PROFILE *profile, INT64 timeMillis) {
   ++profile->matchCount;
   profile->matchTime += timeMillis;
}

void yapp_profile_incMismatch(YAPP_PROFILE *profile, INT64 timeMillis) {
   ++profile->mismatchCount;
   profile->mismatchTime += timeMillis;
}
