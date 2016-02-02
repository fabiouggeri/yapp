/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uggeri.yapp.runtime.java.parser;

import org.uggeri.yapp.runtime.java.trace.TraceParser;
import org.uggeri.yapp.runtime.java.buffer.InputBuffer;
import org.uggeri.yapp.runtime.java.node.Node;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author fabio
 */
public interface Parser {
   
   public Node parse(InputBuffer inputBuffer);
   
   public void setTraceParser(TraceParser tracePath);
   
   public void setTrace(boolean trace);
   
   public Map<Rule, RuleProfile> getProfilesMap();

   public void setProfilesMap(Map<Rule, RuleProfile> profileMap);

   public Collection<ParserError> getMismatches();
}
