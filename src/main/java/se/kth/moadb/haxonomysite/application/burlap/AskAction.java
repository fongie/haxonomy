package se.kth.moadb.haxonomysite.application.burlap;

import burlap.mdp.core.action.Action;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AskAction implements Action {
   int questionId;

   @Override
   public String actionName() {
      return "" + questionId;
   }

   @Override
   public Action copy() {
      return new AskAction(questionId);
   }
}
