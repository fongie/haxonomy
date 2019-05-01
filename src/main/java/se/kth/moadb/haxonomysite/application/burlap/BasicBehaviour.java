package se.kth.moadb.haxonomysite.application.burlap;

import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.singleagent.planning.deterministic.DeterministicPlanner;
import burlap.behavior.singleagent.planning.deterministic.uninformed.bfs.BFS;
import burlap.domain.singleagent.gridworld.GridWorldVisualizer;
import burlap.mdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.mdp.auxiliary.stateconditiontest.TFGoalCondition;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.model.RewardFunction;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import burlap.visualizer.Visualizer;
import se.kth.moadb.haxonomysite.domain.Reply;

public class BasicBehaviour {

   WorldGenerator wg;
   SADomain domain;
   RewardFunction rf;
   TerminalFunction tf;
   StateConditionTest goalCondition;
   State initialState;
   HashableStateFactory hashingFactory;
   SimulatedEnvironment env;

   public BasicBehaviour() {
      wg = new WorldGenerator();
      domain = wg.generateDomain();
      rf = wg.rf;
      tf = wg.tf;
      goalCondition = new TFGoalCondition(tf);

      initialState = new MarkovGridState(new Answer(1, Reply.UNKNOWN), new Answer(2, Reply.UNKNOWN));

      hashingFactory = new SimpleHashableStateFactory();

      env = new SimulatedEnvironment(domain, initialState);
   }

   public void QLearningExample(String out) {
      LearningAgent agent = new QLearning(domain, 0.7, hashingFactory, 0., 0.5);

      for (int i = 0; i < 1000; i++) {
         Episode e = agent.runLearningEpisode(env);

         e.write("out/q1_" + i);
         System.err.println(e.toString());

         env.resetEnvironment();
      }
   }
}


