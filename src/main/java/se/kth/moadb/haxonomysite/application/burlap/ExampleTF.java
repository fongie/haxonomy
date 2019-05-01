package se.kth.moadb.haxonomysite.application.burlap;

import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.state.State;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ExampleTF implements TerminalFunction {

   Answer a1;
   Answer a2;

   @Override
   public boolean isTerminal(State state) {

      Answer b1 = (Answer) state.get("a1");
      Answer b2 = (Answer) state.get("a2");

      return a1.equals(b1) && a2.equals(b2);
   }
}
