package se.kth.moadb.haxonomysite.application.burlap;

import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.State;
import burlap.mdp.core.state.UnknownKeyException;
import burlap.mdp.core.state.annotations.DeepCopyState;
import com.google.common.collect.ImmutableList;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//@Component
@DeepCopyState
@NoArgsConstructor
@Data
public class MarkovGridState implements MutableState {

   Answer a1;
   Answer a2;

   public MarkovGridState(Answer a1, Answer a2) {
      this.a1 = a1;
      this.a2 = a2;
   }

   @Override
   public MutableState set(Object variableKey, Object value) {
      if (variableKey.equals("a1")) {
         this.a1 = (Answer) value;
      } else if (variableKey.equals("a2")) {
         this.a2 = (Answer) value;
      } else {
         throw new UnknownKeyException(variableKey);
      }
      return this;
   }

   @Override
   public List<Object> variableKeys() {
      return ImmutableList.of("a1", "a2");
   }

   @Override
   public Object get(Object o) {
      if (o.equals("a1"))
         return a1;
      else if (o.equals("a2"))
         return a2;
      throw new UnknownKeyException(o);
   }

   @Override
   public State copy() {
      return new MarkovGridState(new Answer(a1.getTermId(), a1.getState()),new Answer(a2.getTermId(), a1.getState()));
   }

   @Override
   public String toString() {
      return "(" + a1.getState() + ", " + a2.getState() + ")";
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof MarkovGridState))
         return false;

      MarkovGridState a = (MarkovGridState) o;
      return a1.equals(a.a1) && a2.equals(a.a2);
   }
}
