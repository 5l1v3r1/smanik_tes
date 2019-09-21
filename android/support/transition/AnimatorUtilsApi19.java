package android.support.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

@RequiresApi(19)
class AnimatorUtilsApi19 implements AnimatorUtilsImpl {
  public void addPauseListener(@NonNull Animator paramAnimator, @NonNull AnimatorListenerAdapter paramAnimatorListenerAdapter) { paramAnimator.addPauseListener(paramAnimatorListenerAdapter); }
  
  public void pause(@NonNull Animator paramAnimator) { paramAnimator.pause(); }
  
  public void resume(@NonNull Animator paramAnimator) { paramAnimator.resume(); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/transition/AnimatorUtilsApi19.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */