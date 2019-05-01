package se.kth.moadb.haxonomysite.application.burlap;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.model.RewardFunction;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ExampleRF implements RewardFunction {
   Answer a1;
   Answer a2;

   @Override
   public double reward(State state, Action action, State state1) {

      Answer b1 = (Answer) state.get("a1"); //maybe should be state1 (sprime)??? tutorial says sprime but codes for s..
      Answer b2 = (Answer) state.get("a2");
      if (a1.equals(b1) && a2.equals(b2)) {
         return 100.;
      }
      return -1;
   }
}
