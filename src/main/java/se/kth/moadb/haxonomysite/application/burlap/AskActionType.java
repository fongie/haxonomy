package se.kth.moadb.haxonomysite.application.burlap;

import burlap.mdp.core.action.Action;
import burlap.mdp.core.action.ActionType;
import burlap.mdp.core.state.State;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class AskActionType  implements ActionType {
   String name;
   Action a1;
   Action a2;

   public AskActionType() {
      this.name = "askAction";
      a1 = new AskAction(1);
      a2 = new AskAction(2);
   }

   @Override
   public String typeName() {
      return name;
   }

   @Override
   public Action associatedAction(String s) {
      if (s.equals("a1"))
         return a1;
      else if (s.equals("a2"))
         return a2;
      else throw new UnsupportedOperationException(s);
   }

   @Override
   public List<Action> allApplicableActions(State state) {
      return ImmutableList.of(a1,a2);
   }
}
