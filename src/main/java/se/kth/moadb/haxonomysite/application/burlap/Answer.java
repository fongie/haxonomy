package se.kth.moadb.haxonomysite.application.burlap;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Answer {
   private int termId;
   private String state;

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof Answer))
         return false;

      Answer a = (Answer) o;
      return termId == a.getTermId() && state.equals(a.getState());
   }
}
