package se.kth.moadb.haxonomysite.application.burlap;

import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.StateTransitionProb;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.action.UniversalActionType;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.model.RewardFunction;
import burlap.mdp.singleagent.model.statemodel.FullStateModel;
import se.kth.moadb.haxonomysite.domain.Reply;

import java.util.ArrayList;
import java.util.List;

public class WorldGenerator implements DomainGenerator {
   public RewardFunction rf;
   public TerminalFunction tf;
   @Override
   public SADomain generateDomain() {

      SADomain dom = new SADomain();

      //dom.addActionTypes(new AskActionType());
      dom.addActionTypes(
            new UniversalActionType(new AskAction(1)),
            new UniversalActionType(new AskAction(2))
      );

      WorldStateModel smodel = new WorldStateModel();
      RewardFunction rf = new ExampleRF(new Answer(1, Reply.UNKNOWN), new Answer(2, Reply.UNKNOWN));
      this.rf = rf;
      TerminalFunction tf = new ExampleTF(new Answer(1, Reply.YES), new Answer(2, Reply.YES));
      this.tf = tf;

      dom.setModel(new FactoredModel(smodel, rf, tf));
      return dom;
   }


   protected class WorldStateModel implements FullStateModel {

      @Override
      public List<StateTransitionProb> stateTransitions(State state, Action action) {

         MarkovGridState ms = (MarkovGridState) state;
         Answer cura1 = ms.a1;
         Answer cura2 = ms.a2;

         List<StateTransitionProb> tps = new ArrayList<>();

         MarkovGridState ns1 = (MarkovGridState) ms.copy();
         ns1.a1.setState(Reply.YES);
         tps.add(new StateTransitionProb(ns1, 0.5));

         MarkovGridState ns2 = (MarkovGridState) ms.copy();
         ns2.a1.setState(Reply.NO);
         tps.add(new StateTransitionProb(ns2, 0.5));

         MarkovGridState ns3 = (MarkovGridState) ms.copy();
         ns3.a2.setState(Reply.YES);
         tps.add(new StateTransitionProb(ns3, 0.5));

         MarkovGridState ns4 = (MarkovGridState) ms.copy();
         ns3.a2.setState(Reply.NO);
         tps.add(new StateTransitionProb(ns4, 0.5));

         return tps;
      }

      @Override
      public State sample(State state, Action action) {
         state = state.copy();
         MarkovGridState ms = (MarkovGridState) state;
         Answer cura1 = ms.a1;
         Answer cura2 = ms.a2;

         AskAction a = (AskAction) action;
         int questionId = a.questionId;

         double prob = 0.5;
         double r = Math.random();

         if (questionId == 1) {
            if (r < prob) {
               cura1.setState(Reply.YES);
            } else {
               cura1.setState(Reply.NO);
            }
         } else {
            if (r < prob) {
               cura1.setState(Reply.YES);
            } else {
               cura1.setState(Reply.NO);
            }
         }

         return ms;
      }
   }
}
