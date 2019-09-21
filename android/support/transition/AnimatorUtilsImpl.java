package android.support.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.annotation.NonNull;

interface AnimatorUtilsImpl {
  void addPauseListener(@NonNull Animator paramAnimator, @NonNull AnimatorListenerAdapter paramAnimatorListenerAdapter);
  
  void pause(@NonNull Animator paramAnimator);
  
  void resume(@NonNull Animator paramAnimator);
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/transition/AnimatorUtilsImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */