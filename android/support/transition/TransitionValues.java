package android.support.transition;

import android.view.View;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TransitionValues {
  final ArrayList<Transition> mTargetedTransitions = new ArrayList();
  
  public final Map<String, Object> values = new HashMap();
  
  public View view;
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof TransitionValues) {
      View view1 = this.view;
      paramObject = (TransitionValues)paramObject;
      if (view1 == paramObject.view && this.values.equals(paramObject.values))
        return true; 
    } 
    return false;
  }
  
  public int hashCode() { return this.view.hashCode() * 31 + this.values.hashCode(); }
  
  public String toString() {
    StringBuilder stringBuilder1 = new StringBuilder();
    stringBuilder1.append("TransitionValues@");
    stringBuilder1.append(Integer.toHexString(hashCode()));
    stringBuilder1.append(":\n");
    String str = stringBuilder1.toString();
    StringBuilder stringBuilder2 = new StringBuilder();
    stringBuilder2.append(str);
    stringBuilder2.append("    view = ");
    stringBuilder2.append(this.view);
    stringBuilder2.append("\n");
    str = stringBuilder2.toString();
    stringBuilder2 = new StringBuilder();
    stringBuilder2.append(str);
    stringBuilder2.append("    values:");
    str = stringBuilder2.toString();
    for (String str1 : this.values.keySet()) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(str);
      stringBuilder.append("    ");
      stringBuilder.append(str1);
      stringBuilder.append(": ");
      stringBuilder.append(this.values.get(str1));
      stringBuilder.append("\n");
      str = stringBuilder.toString();
    } 
    return str;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/transition/TransitionValues.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */