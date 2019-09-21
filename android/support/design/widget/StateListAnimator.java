package android.support.design.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.util.StateSet;
import java.util.ArrayList;

final class StateListAnimator {
  private final Animator.AnimatorListener mAnimationListener = new AnimatorListenerAdapter() {
      public void onAnimationEnd(Animator param1Animator) {
        if (StateListAnimator.this.mRunningAnimator == param1Animator)
          StateListAnimator.this.mRunningAnimator = null; 
      }
    };
  
  private Tuple mLastMatch = null;
  
  ValueAnimator mRunningAnimator = null;
  
  private final ArrayList<Tuple> mTuples = new ArrayList();
  
  private void cancel() {
    if (this.mRunningAnimator != null) {
      this.mRunningAnimator.cancel();
      this.mRunningAnimator = null;
    } 
  }
  
  private void start(Tuple paramTuple) {
    this.mRunningAnimator = paramTuple.mAnimator;
    this.mRunningAnimator.start();
  }
  
  public void addState(int[] paramArrayOfInt, ValueAnimator paramValueAnimator) {
    Tuple tuple = new Tuple(paramArrayOfInt, paramValueAnimator);
    paramValueAnimator.addListener(this.mAnimationListener);
    this.mTuples.add(tuple);
  }
  
  public void jumpToCurrentState() {
    if (this.mRunningAnimator != null) {
      this.mRunningAnimator.end();
      this.mRunningAnimator = null;
    } 
  }
  
  void setState(int[] paramArrayOfInt) {
    int i = this.mTuples.size();
    byte b = 0;
    while (true) {
      if (b < i) {
        Tuple tuple = (Tuple)this.mTuples.get(b);
        if (StateSet.stateSetMatches(tuple.mSpecs, paramArrayOfInt)) {
          Tuple tuple1 = tuple;
          break;
        } 
        b++;
        continue;
      } 
      paramArrayOfInt = null;
      break;
    } 
    if (paramArrayOfInt == this.mLastMatch)
      return; 
    if (this.mLastMatch != null)
      cancel(); 
    this.mLastMatch = paramArrayOfInt;
    if (paramArrayOfInt != null)
      start(paramArrayOfInt); 
  }
  
  static class Tuple {
    final ValueAnimator mAnimator;
    
    final int[] mSpecs;
    
    Tuple(int[] param1ArrayOfInt, ValueAnimator param1ValueAnimator) {
      this.mSpecs = param1ArrayOfInt;
      this.mAnimator = param1ValueAnimator;
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/design/widget/StateListAnimator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */