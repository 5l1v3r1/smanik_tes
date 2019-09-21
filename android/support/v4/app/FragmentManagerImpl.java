package android.support.v4.app;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.arch.lifecycle.ViewModelStore;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.util.ArraySet;
import android.support.v4.util.DebugUtils;
import android.support.v4.util.LogWriter;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

final class FragmentManagerImpl extends FragmentManager implements LayoutInflater.Factory2 {
  static final Interpolator ACCELERATE_CUBIC;
  
  static final Interpolator ACCELERATE_QUINT;
  
  static final int ANIM_DUR = 220;
  
  public static final int ANIM_STYLE_CLOSE_ENTER = 3;
  
  public static final int ANIM_STYLE_CLOSE_EXIT = 4;
  
  public static final int ANIM_STYLE_FADE_ENTER = 5;
  
  public static final int ANIM_STYLE_FADE_EXIT = 6;
  
  public static final int ANIM_STYLE_OPEN_ENTER = 1;
  
  public static final int ANIM_STYLE_OPEN_EXIT = 2;
  
  static boolean DEBUG = false;
  
  static final Interpolator DECELERATE_CUBIC;
  
  static final Interpolator DECELERATE_QUINT = new DecelerateInterpolator(2.5F);
  
  static final String TAG = "FragmentManager";
  
  static final String TARGET_REQUEST_CODE_STATE_TAG = "android:target_req_state";
  
  static final String TARGET_STATE_TAG = "android:target_state";
  
  static final String USER_VISIBLE_HINT_TAG = "android:user_visible_hint";
  
  static final String VIEW_STATE_TAG = "android:view_state";
  
  static Field sAnimationListenerField;
  
  SparseArray<Fragment> mActive;
  
  final ArrayList<Fragment> mAdded = new ArrayList();
  
  ArrayList<Integer> mAvailBackStackIndices;
  
  ArrayList<BackStackRecord> mBackStack;
  
  ArrayList<FragmentManager.OnBackStackChangedListener> mBackStackChangeListeners;
  
  ArrayList<BackStackRecord> mBackStackIndices;
  
  FragmentContainer mContainer;
  
  ArrayList<Fragment> mCreatedMenus;
  
  int mCurState = 0;
  
  boolean mDestroyed;
  
  Runnable mExecCommit = new Runnable() {
      public void run() { FragmentManagerImpl.this.execPendingActions(); }
    };
  
  boolean mExecutingActions;
  
  boolean mHavePendingDeferredStart;
  
  FragmentHostCallback mHost;
  
  private final CopyOnWriteArrayList<Pair<FragmentManager.FragmentLifecycleCallbacks, Boolean>> mLifecycleCallbacks = new CopyOnWriteArrayList();
  
  boolean mNeedMenuInvalidate;
  
  int mNextFragmentIndex = 0;
  
  String mNoTransactionsBecause;
  
  Fragment mParent;
  
  ArrayList<OpGenerator> mPendingActions;
  
  ArrayList<StartEnterTransitionListener> mPostponedTransactions;
  
  Fragment mPrimaryNav;
  
  FragmentManagerNonConfig mSavedNonConfig;
  
  SparseArray<Parcelable> mStateArray = null;
  
  Bundle mStateBundle = null;
  
  boolean mStateSaved;
  
  boolean mStopped;
  
  ArrayList<Fragment> mTmpAddedFragments;
  
  ArrayList<Boolean> mTmpIsPop;
  
  ArrayList<BackStackRecord> mTmpRecords;
  
  static  {
    DECELERATE_CUBIC = new DecelerateInterpolator(1.5F);
    ACCELERATE_QUINT = new AccelerateInterpolator(2.5F);
    ACCELERATE_CUBIC = new AccelerateInterpolator(1.5F);
  }
  
  private void addAddedFragments(ArraySet<Fragment> paramArraySet) {
    if (this.mCurState < 1)
      return; 
    int i = Math.min(this.mCurState, 4);
    int j = this.mAdded.size();
    for (byte b = 0; b < j; b++) {
      Fragment fragment = (Fragment)this.mAdded.get(b);
      if (fragment.mState < i) {
        moveToState(fragment, i, fragment.getNextAnim(), fragment.getNextTransition(), false);
        if (fragment.mView != null && !fragment.mHidden && fragment.mIsNewlyAdded)
          paramArraySet.add(fragment); 
      } 
    } 
  }
  
  private void animateRemoveFragment(@NonNull final Fragment fragment, @NonNull AnimationOrAnimator paramAnimationOrAnimator, int paramInt) {
    final View viewToAnimate = paramFragment.mView;
    final ViewGroup container = paramFragment.mContainer;
    viewGroup.startViewTransition(view);
    paramFragment.setStateAfterAnimating(paramInt);
    if (paramAnimationOrAnimator.animation != null) {
      EndViewTransitionAnimator endViewTransitionAnimator = new EndViewTransitionAnimator(paramAnimationOrAnimator.animation, viewGroup, view);
      paramFragment.setAnimatingAway(paramFragment.mView);
      endViewTransitionAnimator.setAnimationListener(new AnimationListenerWrapper(getAnimationListener(endViewTransitionAnimator)) {
            public void onAnimationEnd(Animation param1Animation) {
              super.onAnimationEnd(param1Animation);
              container.post(new Runnable() {
                    public void run() {
                      if (fragment.getAnimatingAway() != null) {
                        fragment.setAnimatingAway(null);
                        FragmentManagerImpl.null.this.this$0.moveToState(fragment, fragment.getStateAfterAnimating(), 0, 0, false);
                      } 
                    }
                  });
            }
          });
      setHWLayerAnimListenerIfAlpha(view, paramAnimationOrAnimator);
      paramFragment.mView.startAnimation(endViewTransitionAnimator);
      return;
    } 
    Animator animator = paramAnimationOrAnimator.animator;
    paramFragment.setAnimator(paramAnimationOrAnimator.animator);
    animator.addListener(new AnimatorListenerAdapter() {
          public void onAnimationEnd(Animator param1Animator) {
            container.endViewTransition(viewToAnimate);
            param1Animator = fragment.getAnimator();
            fragment.setAnimator(null);
            if (param1Animator != null && container.indexOfChild(viewToAnimate) < 0)
              FragmentManagerImpl.this.moveToState(fragment, fragment.getStateAfterAnimating(), 0, 0, false); 
          }
        });
    animator.setTarget(paramFragment.mView);
    setHWLayerAnimListenerIfAlpha(paramFragment.mView, paramAnimationOrAnimator);
    animator.start();
  }
  
  private void burpActive() {
    if (this.mActive != null)
      for (int i = this.mActive.size() - 1; i >= 0; i--) {
        if (this.mActive.valueAt(i) == null)
          this.mActive.delete(this.mActive.keyAt(i)); 
      }  
  }
  
  private void checkStateLoss() {
    if (isStateSaved())
      throw new IllegalStateException("Can not perform this action after onSaveInstanceState"); 
    if (this.mNoTransactionsBecause != null) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Can not perform this action inside of ");
      stringBuilder.append(this.mNoTransactionsBecause);
      throw new IllegalStateException(stringBuilder.toString());
    } 
  }
  
  private void cleanupExec() {
    this.mExecutingActions = false;
    this.mTmpIsPop.clear();
    this.mTmpRecords.clear();
  }
  
  private void completeExecute(BackStackRecord paramBackStackRecord, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
    if (paramBoolean1) {
      paramBackStackRecord.executePopOps(paramBoolean3);
    } else {
      paramBackStackRecord.executeOps();
    } 
    ArrayList arrayList1 = new ArrayList(1);
    ArrayList arrayList2 = new ArrayList(1);
    arrayList1.add(paramBackStackRecord);
    arrayList2.add(Boolean.valueOf(paramBoolean1));
    if (paramBoolean2)
      FragmentTransition.startTransitions(this, arrayList1, arrayList2, 0, 1, true); 
    if (paramBoolean3)
      moveToState(this.mCurState, true); 
    if (this.mActive != null) {
      int i = this.mActive.size();
      byte b;
      for (b = 0; b < i; b++) {
        Fragment fragment = (Fragment)this.mActive.valueAt(b);
        if (fragment != null && fragment.mView != null && fragment.mIsNewlyAdded && paramBackStackRecord.interactsWith(fragment.mContainerId)) {
          if (fragment.mPostponedAlpha > 0.0F)
            fragment.mView.setAlpha(fragment.mPostponedAlpha); 
          if (paramBoolean3) {
            fragment.mPostponedAlpha = 0.0F;
          } else {
            fragment.mPostponedAlpha = -1.0F;
            fragment.mIsNewlyAdded = false;
          } 
        } 
      } 
    } 
  }
  
  private void dispatchStateChange(int paramInt) {
    try {
      this.mExecutingActions = true;
      moveToState(paramInt, false);
      this.mExecutingActions = false;
      return;
    } finally {
      this.mExecutingActions = false;
    } 
  }
  
  private void endAnimatingAwayFragments() {
    int i;
    SparseArray sparseArray = this.mActive;
    byte b = 0;
    if (sparseArray == null) {
      i = 0;
    } else {
      i = this.mActive.size();
    } 
    while (b < i) {
      Fragment fragment = (Fragment)this.mActive.valueAt(b);
      if (fragment != null)
        if (fragment.getAnimatingAway() != null) {
          int j = fragment.getStateAfterAnimating();
          View view = fragment.getAnimatingAway();
          Animation animation = view.getAnimation();
          if (animation != null) {
            animation.cancel();
            view.clearAnimation();
          } 
          fragment.setAnimatingAway(null);
          moveToState(fragment, j, 0, 0, false);
        } else if (fragment.getAnimator() != null) {
          fragment.getAnimator().end();
        }  
      b++;
    } 
  }
  
  private void ensureExecReady(boolean paramBoolean) {
    if (this.mExecutingActions)
      throw new IllegalStateException("FragmentManager is already executing transactions"); 
    if (this.mHost == null)
      throw new IllegalStateException("Fragment host has been destroyed"); 
    if (Looper.myLooper() != this.mHost.getHandler().getLooper())
      throw new IllegalStateException("Must be called from main thread of fragment host"); 
    if (!paramBoolean)
      checkStateLoss(); 
    if (this.mTmpRecords == null) {
      this.mTmpRecords = new ArrayList();
      this.mTmpIsPop = new ArrayList();
    } 
    this.mExecutingActions = true;
    try {
      executePostponedTransaction(null, null);
      return;
    } finally {
      this.mExecutingActions = false;
    } 
  }
  
  private static void executeOps(ArrayList<BackStackRecord> paramArrayList1, ArrayList<Boolean> paramArrayList2, int paramInt1, int paramInt2) {
    while (paramInt1 < paramInt2) {
      BackStackRecord backStackRecord = (BackStackRecord)paramArrayList1.get(paramInt1);
      boolean bool2 = ((Boolean)paramArrayList2.get(paramInt1)).booleanValue();
      boolean bool1 = true;
      if (bool2) {
        backStackRecord.bumpBackStackNesting(-1);
        if (paramInt1 != paramInt2 - 1)
          bool1 = false; 
        backStackRecord.executePopOps(bool1);
      } else {
        backStackRecord.bumpBackStackNesting(1);
        backStackRecord.executeOps();
      } 
      paramInt1++;
    } 
  }
  
  private void executeOpsTogether(ArrayList<BackStackRecord> paramArrayList1, ArrayList<Boolean> paramArrayList2, int paramInt1, int paramInt2) {
    int i = paramInt1;
    boolean bool1 = ((BackStackRecord)paramArrayList1.get(i)).mReorderingAllowed;
    if (this.mTmpAddedFragments == null) {
      this.mTmpAddedFragments = new ArrayList();
    } else {
      this.mTmpAddedFragments.clear();
    } 
    this.mTmpAddedFragments.addAll(this.mAdded);
    Fragment fragment = getPrimaryNavigationFragment();
    int j = i;
    boolean bool = false;
    while (j < paramInt2) {
      BackStackRecord backStackRecord = (BackStackRecord)paramArrayList1.get(j);
      if (!((Boolean)paramArrayList2.get(j)).booleanValue()) {
        fragment = backStackRecord.expandOps(this.mTmpAddedFragments, fragment);
      } else {
        fragment = backStackRecord.trackAddedFragmentsInPop(this.mTmpAddedFragments, fragment);
      } 
      if (bool || backStackRecord.mAddToBackStack) {
        bool = true;
      } else {
        bool = false;
      } 
      j++;
    } 
    this.mTmpAddedFragments.clear();
    if (!bool1)
      FragmentTransition.startTransitions(this, paramArrayList1, paramArrayList2, i, paramInt2, false); 
    executeOps(paramArrayList1, paramArrayList2, paramInt1, paramInt2);
    if (bool1) {
      ArraySet arraySet = new ArraySet();
      addAddedFragments(arraySet);
      paramInt1 = postponePostponableTransactions(paramArrayList1, paramArrayList2, i, paramInt2, arraySet);
      makeRemovedFragmentsInvisible(arraySet);
    } else {
      paramInt1 = paramInt2;
    } 
    j = i;
    if (paramInt1 != i) {
      j = i;
      if (bool1) {
        FragmentTransition.startTransitions(this, paramArrayList1, paramArrayList2, i, paramInt1, true);
        moveToState(this.mCurState, true);
        j = i;
      } 
    } 
    while (j < paramInt2) {
      BackStackRecord backStackRecord = (BackStackRecord)paramArrayList1.get(j);
      if (((Boolean)paramArrayList2.get(j)).booleanValue() && backStackRecord.mIndex >= 0) {
        freeBackStackIndex(backStackRecord.mIndex);
        backStackRecord.mIndex = -1;
      } 
      backStackRecord.runOnCommitRunnables();
      j++;
    } 
    if (bool)
      reportBackStackChanged(); 
  }
  
  private void executePostponedTransaction(ArrayList<BackStackRecord> paramArrayList1, ArrayList<Boolean> paramArrayList2) { // Byte code:
    //   0: aload_0
    //   1: getfield mPostponedTransactions : Ljava/util/ArrayList;
    //   4: ifnonnull -> 12
    //   7: iconst_0
    //   8: istore_3
    //   9: goto -> 20
    //   12: aload_0
    //   13: getfield mPostponedTransactions : Ljava/util/ArrayList;
    //   16: invokevirtual size : ()I
    //   19: istore_3
    //   20: iconst_0
    //   21: istore #5
    //   23: iload_3
    //   24: istore #4
    //   26: iload #5
    //   28: istore_3
    //   29: iload_3
    //   30: iload #4
    //   32: if_icmpge -> 236
    //   35: aload_0
    //   36: getfield mPostponedTransactions : Ljava/util/ArrayList;
    //   39: iload_3
    //   40: invokevirtual get : (I)Ljava/lang/Object;
    //   43: checkcast android/support/v4/app/FragmentManagerImpl$StartEnterTransitionListener
    //   46: astore #7
    //   48: aload_1
    //   49: ifnull -> 107
    //   52: aload #7
    //   54: invokestatic access$300 : (Landroid/support/v4/app/FragmentManagerImpl$StartEnterTransitionListener;)Z
    //   57: ifne -> 107
    //   60: aload_1
    //   61: aload #7
    //   63: invokestatic access$400 : (Landroid/support/v4/app/FragmentManagerImpl$StartEnterTransitionListener;)Landroid/support/v4/app/BackStackRecord;
    //   66: invokevirtual indexOf : (Ljava/lang/Object;)I
    //   69: istore #5
    //   71: iload #5
    //   73: iconst_m1
    //   74: if_icmpeq -> 107
    //   77: aload_2
    //   78: iload #5
    //   80: invokevirtual get : (I)Ljava/lang/Object;
    //   83: checkcast java/lang/Boolean
    //   86: invokevirtual booleanValue : ()Z
    //   89: ifeq -> 107
    //   92: aload #7
    //   94: invokevirtual cancelTransaction : ()V
    //   97: iload_3
    //   98: istore #6
    //   100: iload #4
    //   102: istore #5
    //   104: goto -> 224
    //   107: aload #7
    //   109: invokevirtual isReady : ()Z
    //   112: ifne -> 150
    //   115: iload_3
    //   116: istore #6
    //   118: iload #4
    //   120: istore #5
    //   122: aload_1
    //   123: ifnull -> 224
    //   126: iload_3
    //   127: istore #6
    //   129: iload #4
    //   131: istore #5
    //   133: aload #7
    //   135: invokestatic access$400 : (Landroid/support/v4/app/FragmentManagerImpl$StartEnterTransitionListener;)Landroid/support/v4/app/BackStackRecord;
    //   138: aload_1
    //   139: iconst_0
    //   140: aload_1
    //   141: invokevirtual size : ()I
    //   144: invokevirtual interactsWith : (Ljava/util/ArrayList;II)Z
    //   147: ifeq -> 224
    //   150: aload_0
    //   151: getfield mPostponedTransactions : Ljava/util/ArrayList;
    //   154: iload_3
    //   155: invokevirtual remove : (I)Ljava/lang/Object;
    //   158: pop
    //   159: iload_3
    //   160: iconst_1
    //   161: isub
    //   162: istore #6
    //   164: iload #4
    //   166: iconst_1
    //   167: isub
    //   168: istore #5
    //   170: aload_1
    //   171: ifnull -> 219
    //   174: aload #7
    //   176: invokestatic access$300 : (Landroid/support/v4/app/FragmentManagerImpl$StartEnterTransitionListener;)Z
    //   179: ifne -> 219
    //   182: aload_1
    //   183: aload #7
    //   185: invokestatic access$400 : (Landroid/support/v4/app/FragmentManagerImpl$StartEnterTransitionListener;)Landroid/support/v4/app/BackStackRecord;
    //   188: invokevirtual indexOf : (Ljava/lang/Object;)I
    //   191: istore_3
    //   192: iload_3
    //   193: iconst_m1
    //   194: if_icmpeq -> 219
    //   197: aload_2
    //   198: iload_3
    //   199: invokevirtual get : (I)Ljava/lang/Object;
    //   202: checkcast java/lang/Boolean
    //   205: invokevirtual booleanValue : ()Z
    //   208: ifeq -> 219
    //   211: aload #7
    //   213: invokevirtual cancelTransaction : ()V
    //   216: goto -> 224
    //   219: aload #7
    //   221: invokevirtual completeTransaction : ()V
    //   224: iload #6
    //   226: iconst_1
    //   227: iadd
    //   228: istore_3
    //   229: iload #5
    //   231: istore #4
    //   233: goto -> 29
    //   236: return }
  
  private Fragment findFragmentUnder(Fragment paramFragment) {
    ViewGroup viewGroup = paramFragment.mContainer;
    View view = paramFragment.mView;
    if (viewGroup != null) {
      if (view == null)
        return null; 
      for (int i = this.mAdded.indexOf(paramFragment) - 1; i >= 0; i--) {
        paramFragment = (Fragment)this.mAdded.get(i);
        if (paramFragment.mContainer == viewGroup && paramFragment.mView != null)
          return paramFragment; 
      } 
      return null;
    } 
    return null;
  }
  
  private void forcePostponedTransactions() {
    if (this.mPostponedTransactions != null)
      while (!this.mPostponedTransactions.isEmpty())
        ((StartEnterTransitionListener)this.mPostponedTransactions.remove(0)).completeTransaction();  
  }
  
  private boolean generateOpsForPendingActions(ArrayList<BackStackRecord> paramArrayList1, ArrayList<Boolean> paramArrayList2) { // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield mPendingActions : Ljava/util/ArrayList;
    //   6: astore #6
    //   8: iconst_0
    //   9: istore_3
    //   10: aload #6
    //   12: ifnull -> 102
    //   15: aload_0
    //   16: getfield mPendingActions : Ljava/util/ArrayList;
    //   19: invokevirtual size : ()I
    //   22: ifne -> 28
    //   25: goto -> 102
    //   28: aload_0
    //   29: getfield mPendingActions : Ljava/util/ArrayList;
    //   32: invokevirtual size : ()I
    //   35: istore #4
    //   37: iconst_0
    //   38: istore #5
    //   40: iload_3
    //   41: iload #4
    //   43: if_icmpge -> 76
    //   46: iload #5
    //   48: aload_0
    //   49: getfield mPendingActions : Ljava/util/ArrayList;
    //   52: iload_3
    //   53: invokevirtual get : (I)Ljava/lang/Object;
    //   56: checkcast android/support/v4/app/FragmentManagerImpl$OpGenerator
    //   59: aload_1
    //   60: aload_2
    //   61: invokeinterface generateOps : (Ljava/util/ArrayList;Ljava/util/ArrayList;)Z
    //   66: ior
    //   67: istore #5
    //   69: iload_3
    //   70: iconst_1
    //   71: iadd
    //   72: istore_3
    //   73: goto -> 40
    //   76: aload_0
    //   77: getfield mPendingActions : Ljava/util/ArrayList;
    //   80: invokevirtual clear : ()V
    //   83: aload_0
    //   84: getfield mHost : Landroid/support/v4/app/FragmentHostCallback;
    //   87: invokevirtual getHandler : ()Landroid/os/Handler;
    //   90: aload_0
    //   91: getfield mExecCommit : Ljava/lang/Runnable;
    //   94: invokevirtual removeCallbacks : (Ljava/lang/Runnable;)V
    //   97: aload_0
    //   98: monitorexit
    //   99: iload #5
    //   101: ireturn
    //   102: aload_0
    //   103: monitorexit
    //   104: iconst_0
    //   105: ireturn
    //   106: astore_1
    //   107: aload_0
    //   108: monitorexit
    //   109: aload_1
    //   110: athrow
    // Exception table:
    //   from	to	target	type
    //   2	8	106	finally
    //   15	25	106	finally
    //   28	37	106	finally
    //   46	69	106	finally
    //   76	99	106	finally
    //   102	104	106	finally
    //   107	109	106	finally }
  
  private static Animation.AnimationListener getAnimationListener(Animation paramAnimation) {
    try {
      if (sAnimationListenerField == null) {
        sAnimationListenerField = Animation.class.getDeclaredField("mListener");
        sAnimationListenerField.setAccessible(true);
      } 
      return (Animation.AnimationListener)sAnimationListenerField.get(paramAnimation);
    } catch (NoSuchFieldException paramAnimation) {
      Log.e("FragmentManager", "No field with the name mListener is found in Animation class", paramAnimation);
    } catch (IllegalAccessException paramAnimation) {
      Log.e("FragmentManager", "Cannot access Animation's mListener field", paramAnimation);
    } 
    return null;
  }
  
  static AnimationOrAnimator makeFadeAnimation(Context paramContext, float paramFloat1, float paramFloat2) {
    AlphaAnimation alphaAnimation = new AlphaAnimation(paramFloat1, paramFloat2);
    alphaAnimation.setInterpolator(DECELERATE_CUBIC);
    alphaAnimation.setDuration(220L);
    return new AnimationOrAnimator(alphaAnimation, null);
  }
  
  static AnimationOrAnimator makeOpenCloseAnimation(Context paramContext, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) {
    AnimationSet animationSet = new AnimationSet(false);
    ScaleAnimation scaleAnimation = new ScaleAnimation(paramFloat1, paramFloat2, paramFloat1, paramFloat2, 1, 0.5F, 1, 0.5F);
    scaleAnimation.setInterpolator(DECELERATE_QUINT);
    scaleAnimation.setDuration(220L);
    animationSet.addAnimation(scaleAnimation);
    AlphaAnimation alphaAnimation = new AlphaAnimation(paramFloat3, paramFloat4);
    alphaAnimation.setInterpolator(DECELERATE_CUBIC);
    alphaAnimation.setDuration(220L);
    animationSet.addAnimation(alphaAnimation);
    return new AnimationOrAnimator(animationSet, null);
  }
  
  private void makeRemovedFragmentsInvisible(ArraySet<Fragment> paramArraySet) {
    int i = paramArraySet.size();
    for (byte b = 0; b < i; b++) {
      Fragment fragment = (Fragment)paramArraySet.valueAt(b);
      if (!fragment.mAdded) {
        View view = fragment.getView();
        fragment.mPostponedAlpha = view.getAlpha();
        view.setAlpha(0.0F);
      } 
    } 
  }
  
  static boolean modifiesAlpha(Animator paramAnimator) {
    PropertyValuesHolder[] arrayOfPropertyValuesHolder;
    if (paramAnimator == null)
      return false; 
    if (paramAnimator instanceof ValueAnimator) {
      arrayOfPropertyValuesHolder = ((ValueAnimator)paramAnimator).getValues();
      for (byte b = 0; b < arrayOfPropertyValuesHolder.length; b++) {
        if ("alpha".equals(arrayOfPropertyValuesHolder[b].getPropertyName()))
          return true; 
      } 
    } else if (arrayOfPropertyValuesHolder instanceof AnimatorSet) {
      ArrayList arrayList = ((AnimatorSet)arrayOfPropertyValuesHolder).getChildAnimations();
      for (byte b = 0; b < arrayList.size(); b++) {
        if (modifiesAlpha((Animator)arrayList.get(b)))
          return true; 
      } 
    } 
    return false;
  }
  
  static boolean modifiesAlpha(AnimationOrAnimator paramAnimationOrAnimator) {
    List list;
    if (paramAnimationOrAnimator.animation instanceof AlphaAnimation)
      return true; 
    if (paramAnimationOrAnimator.animation instanceof AnimationSet) {
      list = ((AnimationSet)paramAnimationOrAnimator.animation).getAnimations();
      for (byte b = 0; b < list.size(); b++) {
        if (list.get(b) instanceof AlphaAnimation)
          return true; 
      } 
      return false;
    } 
    return modifiesAlpha(list.animator);
  }
  
  private boolean popBackStackImmediate(String paramString, int paramInt1, int paramInt2) {
    execPendingActions();
    ensureExecReady(true);
    if (this.mPrimaryNav != null && paramInt1 < 0 && paramString == null) {
      FragmentManager fragmentManager = this.mPrimaryNav.peekChildFragmentManager();
      if (fragmentManager != null && fragmentManager.popBackStackImmediate())
        return true; 
    } 
    boolean bool = popBackStackState(this.mTmpRecords, this.mTmpIsPop, paramString, paramInt1, paramInt2);
    if (bool) {
      this.mExecutingActions = true;
      try {
        removeRedundantOperationsAndExecute(this.mTmpRecords, this.mTmpIsPop);
      } finally {
        cleanupExec();
      } 
    } 
    doPendingDeferredStart();
    burpActive();
    return bool;
  }
  
  private int postponePostponableTransactions(ArrayList<BackStackRecord> paramArrayList1, ArrayList<Boolean> paramArrayList2, int paramInt1, int paramInt2, ArraySet<Fragment> paramArraySet) {
    int i = paramInt2 - 1;
    int j;
    for (j = paramInt2; i >= paramInt1; j = k) {
      boolean bool;
      BackStackRecord backStackRecord = (BackStackRecord)paramArrayList1.get(i);
      boolean bool1 = ((Boolean)paramArrayList2.get(i)).booleanValue();
      if (backStackRecord.isPostponed() && !backStackRecord.interactsWith(paramArrayList1, i + 1, paramInt2)) {
        bool = true;
      } else {
        bool = false;
      } 
      int k = j;
      if (bool) {
        if (this.mPostponedTransactions == null)
          this.mPostponedTransactions = new ArrayList(); 
        StartEnterTransitionListener startEnterTransitionListener = new StartEnterTransitionListener(backStackRecord, bool1);
        this.mPostponedTransactions.add(startEnterTransitionListener);
        backStackRecord.setOnStartPostponedListener(startEnterTransitionListener);
        if (bool1) {
          backStackRecord.executeOps();
        } else {
          backStackRecord.executePopOps(false);
        } 
        k = j - 1;
        if (i != k) {
          paramArrayList1.remove(i);
          paramArrayList1.add(k, backStackRecord);
        } 
        addAddedFragments(paramArraySet);
      } 
      i--;
    } 
    return j;
  }
  
  private void removeRedundantOperationsAndExecute(ArrayList<BackStackRecord> paramArrayList1, ArrayList<Boolean> paramArrayList2) {
    if (paramArrayList1 != null) {
      if (paramArrayList1.isEmpty())
        return; 
      if (paramArrayList2 == null || paramArrayList1.size() != paramArrayList2.size())
        throw new IllegalStateException("Internal error with the back stack records"); 
      executePostponedTransaction(paramArrayList1, paramArrayList2);
      int i = paramArrayList1.size();
      byte b1 = 0;
      byte b2;
      for (b2 = 0; b1 < i; b2 = b3) {
        byte b4 = b1;
        byte b3 = b2;
        if (!((BackStackRecord)paramArrayList1.get(b1)).mReorderingAllowed) {
          if (b2 != b1)
            executeOpsTogether(paramArrayList1, paramArrayList2, b2, b1); 
          b2 = b1 + 1;
          b3 = b2;
          if (((Boolean)paramArrayList2.get(b1)).booleanValue())
            while (true) {
              b3 = b2;
              if (b2 < i) {
                b3 = b2;
                if (((Boolean)paramArrayList2.get(b2)).booleanValue()) {
                  b3 = b2;
                  if (!((BackStackRecord)paramArrayList1.get(b2)).mReorderingAllowed) {
                    b2++;
                    continue;
                  } 
                } 
              } 
              break;
            }  
          executeOpsTogether(paramArrayList1, paramArrayList2, b1, b3);
          b4 = b3 - 1;
        } 
        b1 = b4 + 1;
      } 
      if (b2 != i)
        executeOpsTogether(paramArrayList1, paramArrayList2, b2, i); 
      return;
    } 
  }
  
  public static int reverseTransit(int paramInt) {
    char c = ' ';
    if (paramInt != 4097) {
      if (paramInt != 4099)
        return (paramInt != 8194) ? 0 : 4097; 
      c = 'ဃ';
    } 
    return c;
  }
  
  private void scheduleCommit() { // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield mPostponedTransactions : Ljava/util/ArrayList;
    //   6: astore #4
    //   8: iconst_0
    //   9: istore_3
    //   10: aload #4
    //   12: ifnull -> 96
    //   15: aload_0
    //   16: getfield mPostponedTransactions : Ljava/util/ArrayList;
    //   19: invokevirtual isEmpty : ()Z
    //   22: ifne -> 96
    //   25: iconst_1
    //   26: istore_1
    //   27: goto -> 30
    //   30: iload_3
    //   31: istore_2
    //   32: aload_0
    //   33: getfield mPendingActions : Ljava/util/ArrayList;
    //   36: ifnull -> 101
    //   39: iload_3
    //   40: istore_2
    //   41: aload_0
    //   42: getfield mPendingActions : Ljava/util/ArrayList;
    //   45: invokevirtual size : ()I
    //   48: iconst_1
    //   49: if_icmpne -> 101
    //   52: iconst_1
    //   53: istore_2
    //   54: goto -> 101
    //   57: aload_0
    //   58: getfield mHost : Landroid/support/v4/app/FragmentHostCallback;
    //   61: invokevirtual getHandler : ()Landroid/os/Handler;
    //   64: aload_0
    //   65: getfield mExecCommit : Ljava/lang/Runnable;
    //   68: invokevirtual removeCallbacks : (Ljava/lang/Runnable;)V
    //   71: aload_0
    //   72: getfield mHost : Landroid/support/v4/app/FragmentHostCallback;
    //   75: invokevirtual getHandler : ()Landroid/os/Handler;
    //   78: aload_0
    //   79: getfield mExecCommit : Ljava/lang/Runnable;
    //   82: invokevirtual post : (Ljava/lang/Runnable;)Z
    //   85: pop
    //   86: aload_0
    //   87: monitorexit
    //   88: return
    //   89: astore #4
    //   91: aload_0
    //   92: monitorexit
    //   93: aload #4
    //   95: athrow
    //   96: iconst_0
    //   97: istore_1
    //   98: goto -> 30
    //   101: iload_1
    //   102: ifne -> 57
    //   105: iload_2
    //   106: ifeq -> 86
    //   109: goto -> 57
    // Exception table:
    //   from	to	target	type
    //   2	8	89	finally
    //   15	25	89	finally
    //   32	39	89	finally
    //   41	52	89	finally
    //   57	86	89	finally
    //   86	88	89	finally
    //   91	93	89	finally }
  
  private static void setHWLayerAnimListenerIfAlpha(View paramView, AnimationOrAnimator paramAnimationOrAnimator) {
    if (paramView != null) {
      if (paramAnimationOrAnimator == null)
        return; 
      if (shouldRunOnHWLayer(paramView, paramAnimationOrAnimator)) {
        if (paramAnimationOrAnimator.animator != null) {
          paramAnimationOrAnimator.animator.addListener(new AnimatorOnHWLayerIfNeededListener(paramView));
          return;
        } 
        Animation.AnimationListener animationListener = getAnimationListener(paramAnimationOrAnimator.animation);
        paramView.setLayerType(2, null);
        paramAnimationOrAnimator.animation.setAnimationListener(new AnimateOnHWLayerIfNeededListener(paramView, animationListener));
      } 
      return;
    } 
  }
  
  private static void setRetaining(FragmentManagerNonConfig paramFragmentManagerNonConfig) {
    if (paramFragmentManagerNonConfig == null)
      return; 
    List list2 = paramFragmentManagerNonConfig.getFragments();
    if (list2 != null) {
      Iterator iterator = list2.iterator();
      while (iterator.hasNext())
        ((Fragment)iterator.next()).mRetaining = true; 
    } 
    List list1 = paramFragmentManagerNonConfig.getChildNonConfigs();
    if (list1 != null) {
      Iterator iterator = list1.iterator();
      while (iterator.hasNext())
        setRetaining((FragmentManagerNonConfig)iterator.next()); 
    } 
  }
  
  static boolean shouldRunOnHWLayer(View paramView, AnimationOrAnimator paramAnimationOrAnimator) {
    byte b = 0;
    if (paramView != null) {
      if (paramAnimationOrAnimator == null)
        return false; 
      int i = b;
      if (Build.VERSION.SDK_INT >= 19) {
        i = b;
        if (paramView.getLayerType() == 0) {
          i = b;
          if (ViewCompat.hasOverlappingRendering(paramView)) {
            i = b;
            if (modifiesAlpha(paramAnimationOrAnimator))
              i = 1; 
          } 
        } 
      } 
      return i;
    } 
    return false;
  }
  
  private void throwException(RuntimeException paramRuntimeException) {
    Log.e("FragmentManager", paramRuntimeException.getMessage());
    Log.e("FragmentManager", "Activity state:");
    printWriter = new PrintWriter(new LogWriter("FragmentManager"));
    if (this.mHost != null) {
      try {
        this.mHost.onDump("  ", null, printWriter, new String[0]);
      } catch (Exception printWriter) {
        Log.e("FragmentManager", "Failed dumping state", printWriter);
      } 
    } else {
      try {
        dump("  ", null, printWriter, new String[0]);
      } catch (Exception printWriter) {
        Log.e("FragmentManager", "Failed dumping state", printWriter);
      } 
    } 
    throw paramRuntimeException;
  }
  
  public static int transitToStyleIndex(int paramInt, boolean paramBoolean) { return (paramInt != 4097) ? ((paramInt != 4099) ? ((paramInt != 8194) ? -1 : (paramBoolean ? 3 : 4)) : (paramBoolean ? 5 : 6)) : (paramBoolean ? 1 : 2); }
  
  void addBackStackState(BackStackRecord paramBackStackRecord) {
    if (this.mBackStack == null)
      this.mBackStack = new ArrayList(); 
    this.mBackStack.add(paramBackStackRecord);
  }
  
  public void addFragment(Fragment paramFragment, boolean paramBoolean) {
    if (DEBUG) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("add: ");
      stringBuilder.append(paramFragment);
      Log.v("FragmentManager", stringBuilder.toString());
    } 
    makeActive(paramFragment);
    if (!paramFragment.mDetached) {
      if (this.mAdded.contains(paramFragment)) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Fragment already added: ");
        stringBuilder.append(paramFragment);
        throw new IllegalStateException(stringBuilder.toString());
      } 
      synchronized (this.mAdded) {
        this.mAdded.add(paramFragment);
        paramFragment.mAdded = true;
        paramFragment.mRemoving = false;
        if (paramFragment.mView == null)
          paramFragment.mHiddenChanged = false; 
        if (paramFragment.mHasMenu && paramFragment.mMenuVisible)
          this.mNeedMenuInvalidate = true; 
        if (paramBoolean) {
          moveToState(paramFragment);
          return;
        } 
      } 
    } 
  }
  
  public void addOnBackStackChangedListener(FragmentManager.OnBackStackChangedListener paramOnBackStackChangedListener) {
    if (this.mBackStackChangeListeners == null)
      this.mBackStackChangeListeners = new ArrayList(); 
    this.mBackStackChangeListeners.add(paramOnBackStackChangedListener);
  }
  
  public int allocBackStackIndex(BackStackRecord paramBackStackRecord) { // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield mAvailBackStackIndices : Ljava/util/ArrayList;
    //   6: ifnull -> 111
    //   9: aload_0
    //   10: getfield mAvailBackStackIndices : Ljava/util/ArrayList;
    //   13: invokevirtual size : ()I
    //   16: ifgt -> 22
    //   19: goto -> 111
    //   22: aload_0
    //   23: getfield mAvailBackStackIndices : Ljava/util/ArrayList;
    //   26: aload_0
    //   27: getfield mAvailBackStackIndices : Ljava/util/ArrayList;
    //   30: invokevirtual size : ()I
    //   33: iconst_1
    //   34: isub
    //   35: invokevirtual remove : (I)Ljava/lang/Object;
    //   38: checkcast java/lang/Integer
    //   41: invokevirtual intValue : ()I
    //   44: istore_2
    //   45: getstatic android/support/v4/app/FragmentManagerImpl.DEBUG : Z
    //   48: ifeq -> 97
    //   51: new java/lang/StringBuilder
    //   54: dup
    //   55: invokespecial <init> : ()V
    //   58: astore_3
    //   59: aload_3
    //   60: ldc_w 'Adding back stack index '
    //   63: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   66: pop
    //   67: aload_3
    //   68: iload_2
    //   69: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   72: pop
    //   73: aload_3
    //   74: ldc_w ' with '
    //   77: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   80: pop
    //   81: aload_3
    //   82: aload_1
    //   83: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   86: pop
    //   87: ldc 'FragmentManager'
    //   89: aload_3
    //   90: invokevirtual toString : ()Ljava/lang/String;
    //   93: invokestatic v : (Ljava/lang/String;Ljava/lang/String;)I
    //   96: pop
    //   97: aload_0
    //   98: getfield mBackStackIndices : Ljava/util/ArrayList;
    //   101: iload_2
    //   102: aload_1
    //   103: invokevirtual set : (ILjava/lang/Object;)Ljava/lang/Object;
    //   106: pop
    //   107: aload_0
    //   108: monitorexit
    //   109: iload_2
    //   110: ireturn
    //   111: aload_0
    //   112: getfield mBackStackIndices : Ljava/util/ArrayList;
    //   115: ifnonnull -> 129
    //   118: aload_0
    //   119: new java/util/ArrayList
    //   122: dup
    //   123: invokespecial <init> : ()V
    //   126: putfield mBackStackIndices : Ljava/util/ArrayList;
    //   129: aload_0
    //   130: getfield mBackStackIndices : Ljava/util/ArrayList;
    //   133: invokevirtual size : ()I
    //   136: istore_2
    //   137: getstatic android/support/v4/app/FragmentManagerImpl.DEBUG : Z
    //   140: ifeq -> 189
    //   143: new java/lang/StringBuilder
    //   146: dup
    //   147: invokespecial <init> : ()V
    //   150: astore_3
    //   151: aload_3
    //   152: ldc_w 'Setting back stack index '
    //   155: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   158: pop
    //   159: aload_3
    //   160: iload_2
    //   161: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   164: pop
    //   165: aload_3
    //   166: ldc_w ' to '
    //   169: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   172: pop
    //   173: aload_3
    //   174: aload_1
    //   175: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   178: pop
    //   179: ldc 'FragmentManager'
    //   181: aload_3
    //   182: invokevirtual toString : ()Ljava/lang/String;
    //   185: invokestatic v : (Ljava/lang/String;Ljava/lang/String;)I
    //   188: pop
    //   189: aload_0
    //   190: getfield mBackStackIndices : Ljava/util/ArrayList;
    //   193: aload_1
    //   194: invokevirtual add : (Ljava/lang/Object;)Z
    //   197: pop
    //   198: aload_0
    //   199: monitorexit
    //   200: iload_2
    //   201: ireturn
    //   202: astore_1
    //   203: aload_0
    //   204: monitorexit
    //   205: aload_1
    //   206: athrow
    // Exception table:
    //   from	to	target	type
    //   2	19	202	finally
    //   22	97	202	finally
    //   97	109	202	finally
    //   111	129	202	finally
    //   129	189	202	finally
    //   189	200	202	finally
    //   203	205	202	finally }
  
  public void attachController(FragmentHostCallback paramFragmentHostCallback, FragmentContainer paramFragmentContainer, Fragment paramFragment) {
    if (this.mHost != null)
      throw new IllegalStateException("Already attached"); 
    this.mHost = paramFragmentHostCallback;
    this.mContainer = paramFragmentContainer;
    this.mParent = paramFragment;
  }
  
  public void attachFragment(Fragment paramFragment) {
    if (DEBUG) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("attach: ");
      stringBuilder.append(paramFragment);
      Log.v("FragmentManager", stringBuilder.toString());
    } 
    if (paramFragment.mDetached) {
      paramFragment.mDetached = false;
      if (!paramFragment.mAdded) {
        if (this.mAdded.contains(paramFragment)) {
          StringBuilder stringBuilder = new StringBuilder();
          stringBuilder.append("Fragment already added: ");
          stringBuilder.append(paramFragment);
          throw new IllegalStateException(stringBuilder.toString());
        } 
        if (DEBUG) {
          StringBuilder stringBuilder = new StringBuilder();
          stringBuilder.append("add from attach: ");
          stringBuilder.append(paramFragment);
          Log.v("FragmentManager", stringBuilder.toString());
        } 
        synchronized (this.mAdded) {
          this.mAdded.add(paramFragment);
          paramFragment.mAdded = true;
          if (paramFragment.mHasMenu && paramFragment.mMenuVisible) {
            this.mNeedMenuInvalidate = true;
            return;
          } 
        } 
      } 
    } 
  }
  
  public FragmentTransaction beginTransaction() { return new BackStackRecord(this); }
  
  void completeShowHideFragment(final Fragment fragment) {
    if (paramFragment.mView != null) {
      AnimationOrAnimator animationOrAnimator = loadAnimation(paramFragment, paramFragment.getNextTransition(), paramFragment.mHidden ^ true, paramFragment.getNextTransitionStyle());
      if (animationOrAnimator != null && animationOrAnimator.animator != null) {
        animationOrAnimator.animator.setTarget(paramFragment.mView);
        if (paramFragment.mHidden) {
          if (paramFragment.isHideReplaced()) {
            paramFragment.setHideReplaced(false);
          } else {
            final ViewGroup container = paramFragment.mContainer;
            final View animatingView = paramFragment.mView;
            viewGroup.startViewTransition(view);
            animationOrAnimator.animator.addListener(new AnimatorListenerAdapter() {
                  public void onAnimationEnd(Animator param1Animator) {
                    container.endViewTransition(animatingView);
                    param1Animator.removeListener(this);
                    if (this.val$fragment.mView != null)
                      this.val$fragment.mView.setVisibility(8); 
                  }
                });
          } 
        } else {
          paramFragment.mView.setVisibility(0);
        } 
        setHWLayerAnimListenerIfAlpha(paramFragment.mView, animationOrAnimator);
        animationOrAnimator.animator.start();
      } else {
        byte b;
        if (animationOrAnimator != null) {
          setHWLayerAnimListenerIfAlpha(paramFragment.mView, animationOrAnimator);
          paramFragment.mView.startAnimation(animationOrAnimator.animation);
          animationOrAnimator.animation.start();
        } 
        if (paramFragment.mHidden && !paramFragment.isHideReplaced()) {
          b = 8;
        } else {
          b = 0;
        } 
        paramFragment.mView.setVisibility(b);
        if (paramFragment.isHideReplaced())
          paramFragment.setHideReplaced(false); 
      } 
    } 
    if (paramFragment.mAdded && paramFragment.mHasMenu && paramFragment.mMenuVisible)
      this.mNeedMenuInvalidate = true; 
    paramFragment.mHiddenChanged = false;
    paramFragment.onHiddenChanged(paramFragment.mHidden);
  }
  
  public void detachFragment(Fragment paramFragment) {
    if (DEBUG) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("detach: ");
      stringBuilder.append(paramFragment);
      Log.v("FragmentManager", stringBuilder.toString());
    } 
    if (!paramFragment.mDetached) {
      paramFragment.mDetached = true;
      if (paramFragment.mAdded) {
        if (DEBUG) {
          StringBuilder stringBuilder = new StringBuilder();
          stringBuilder.append("remove from detach: ");
          stringBuilder.append(paramFragment);
          Log.v("FragmentManager", stringBuilder.toString());
        } 
        synchronized (this.mAdded) {
          this.mAdded.remove(paramFragment);
          if (paramFragment.mHasMenu && paramFragment.mMenuVisible)
            this.mNeedMenuInvalidate = true; 
          paramFragment.mAdded = false;
          return;
        } 
      } 
    } 
  }
  
  public void dispatchActivityCreated() {
    this.mStateSaved = false;
    this.mStopped = false;
    dispatchStateChange(2);
  }
  
  public void dispatchConfigurationChanged(Configuration paramConfiguration) {
    for (byte b = 0; b < this.mAdded.size(); b++) {
      Fragment fragment = (Fragment)this.mAdded.get(b);
      if (fragment != null)
        fragment.performConfigurationChanged(paramConfiguration); 
    } 
  }
  
  public boolean dispatchContextItemSelected(MenuItem paramMenuItem) {
    if (this.mCurState < 1)
      return false; 
    for (byte b = 0; b < this.mAdded.size(); b++) {
      Fragment fragment = (Fragment)this.mAdded.get(b);
      if (fragment != null && fragment.performContextItemSelected(paramMenuItem))
        return true; 
    } 
    return false;
  }
  
  public void dispatchCreate() {
    this.mStateSaved = false;
    this.mStopped = false;
    dispatchStateChange(1);
  }
  
  public boolean dispatchCreateOptionsMenu(Menu paramMenu, MenuInflater paramMenuInflater) {
    int i = this.mCurState;
    int j = 0;
    if (i < 1)
      return false; 
    ArrayList arrayList = null;
    i = 0;
    int k;
    for (k = 0; i < this.mAdded.size(); k = b) {
      Fragment fragment = (Fragment)this.mAdded.get(i);
      ArrayList arrayList1 = arrayList;
      byte b = k;
      if (fragment != null) {
        arrayList1 = arrayList;
        b = k;
        if (fragment.performCreateOptionsMenu(paramMenu, paramMenuInflater)) {
          arrayList1 = arrayList;
          if (arrayList == null)
            arrayList1 = new ArrayList(); 
          arrayList1.add(fragment);
          b = 1;
        } 
      } 
      i++;
      arrayList = arrayList1;
    } 
    if (this.mCreatedMenus != null)
      for (i = j; i < this.mCreatedMenus.size(); i++) {
        Fragment fragment = (Fragment)this.mCreatedMenus.get(i);
        if (arrayList == null || !arrayList.contains(fragment))
          fragment.onDestroyOptionsMenu(); 
      }  
    this.mCreatedMenus = arrayList;
    return k;
  }
  
  public void dispatchDestroy() {
    this.mDestroyed = true;
    execPendingActions();
    dispatchStateChange(0);
    this.mHost = null;
    this.mContainer = null;
    this.mParent = null;
  }
  
  public void dispatchDestroyView() { dispatchStateChange(1); }
  
  public void dispatchLowMemory() {
    for (byte b = 0; b < this.mAdded.size(); b++) {
      Fragment fragment = (Fragment)this.mAdded.get(b);
      if (fragment != null)
        fragment.performLowMemory(); 
    } 
  }
  
  public void dispatchMultiWindowModeChanged(boolean paramBoolean) {
    for (int i = this.mAdded.size() - 1; i >= 0; i--) {
      Fragment fragment = (Fragment)this.mAdded.get(i);
      if (fragment != null)
        fragment.performMultiWindowModeChanged(paramBoolean); 
    } 
  }
  
  void dispatchOnFragmentActivityCreated(Fragment paramFragment, Bundle paramBundle, boolean paramBoolean) {
    if (this.mParent != null) {
      FragmentManager fragmentManager = this.mParent.getFragmentManager();
      if (fragmentManager instanceof FragmentManagerImpl)
        ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentActivityCreated(paramFragment, paramBundle, true); 
    } 
    for (Pair pair : this.mLifecycleCallbacks) {
      if (!paramBoolean || ((Boolean)pair.second).booleanValue())
        ((FragmentManager.FragmentLifecycleCallbacks)pair.first).onFragmentActivityCreated(this, paramFragment, paramBundle); 
    } 
  }
  
  void dispatchOnFragmentAttached(Fragment paramFragment, Context paramContext, boolean paramBoolean) {
    if (this.mParent != null) {
      FragmentManager fragmentManager = this.mParent.getFragmentManager();
      if (fragmentManager instanceof FragmentManagerImpl)
        ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentAttached(paramFragment, paramContext, true); 
    } 
    for (Pair pair : this.mLifecycleCallbacks) {
      if (!paramBoolean || ((Boolean)pair.second).booleanValue())
        ((FragmentManager.FragmentLifecycleCallbacks)pair.first).onFragmentAttached(this, paramFragment, paramContext); 
    } 
  }
  
  void dispatchOnFragmentCreated(Fragment paramFragment, Bundle paramBundle, boolean paramBoolean) {
    if (this.mParent != null) {
      FragmentManager fragmentManager = this.mParent.getFragmentManager();
      if (fragmentManager instanceof FragmentManagerImpl)
        ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentCreated(paramFragment, paramBundle, true); 
    } 
    for (Pair pair : this.mLifecycleCallbacks) {
      if (!paramBoolean || ((Boolean)pair.second).booleanValue())
        ((FragmentManager.FragmentLifecycleCallbacks)pair.first).onFragmentCreated(this, paramFragment, paramBundle); 
    } 
  }
  
  void dispatchOnFragmentDestroyed(Fragment paramFragment, boolean paramBoolean) {
    if (this.mParent != null) {
      FragmentManager fragmentManager = this.mParent.getFragmentManager();
      if (fragmentManager instanceof FragmentManagerImpl)
        ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentDestroyed(paramFragment, true); 
    } 
    for (Pair pair : this.mLifecycleCallbacks) {
      if (!paramBoolean || ((Boolean)pair.second).booleanValue())
        ((FragmentManager.FragmentLifecycleCallbacks)pair.first).onFragmentDestroyed(this, paramFragment); 
    } 
  }
  
  void dispatchOnFragmentDetached(Fragment paramFragment, boolean paramBoolean) {
    if (this.mParent != null) {
      FragmentManager fragmentManager = this.mParent.getFragmentManager();
      if (fragmentManager instanceof FragmentManagerImpl)
        ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentDetached(paramFragment, true); 
    } 
    for (Pair pair : this.mLifecycleCallbacks) {
      if (!paramBoolean || ((Boolean)pair.second).booleanValue())
        ((FragmentManager.FragmentLifecycleCallbacks)pair.first).onFragmentDetached(this, paramFragment); 
    } 
  }
  
  void dispatchOnFragmentPaused(Fragment paramFragment, boolean paramBoolean) {
    if (this.mParent != null) {
      FragmentManager fragmentManager = this.mParent.getFragmentManager();
      if (fragmentManager instanceof FragmentManagerImpl)
        ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentPaused(paramFragment, true); 
    } 
    for (Pair pair : this.mLifecycleCallbacks) {
      if (!paramBoolean || ((Boolean)pair.second).booleanValue())
        ((FragmentManager.FragmentLifecycleCallbacks)pair.first).onFragmentPaused(this, paramFragment); 
    } 
  }
  
  void dispatchOnFragmentPreAttached(Fragment paramFragment, Context paramContext, boolean paramBoolean) {
    if (this.mParent != null) {
      FragmentManager fragmentManager = this.mParent.getFragmentManager();
      if (fragmentManager instanceof FragmentManagerImpl)
        ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentPreAttached(paramFragment, paramContext, true); 
    } 
    for (Pair pair : this.mLifecycleCallbacks) {
      if (!paramBoolean || ((Boolean)pair.second).booleanValue())
        ((FragmentManager.FragmentLifecycleCallbacks)pair.first).onFragmentPreAttached(this, paramFragment, paramContext); 
    } 
  }
  
  void dispatchOnFragmentPreCreated(Fragment paramFragment, Bundle paramBundle, boolean paramBoolean) {
    if (this.mParent != null) {
      FragmentManager fragmentManager = this.mParent.getFragmentManager();
      if (fragmentManager instanceof FragmentManagerImpl)
        ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentPreCreated(paramFragment, paramBundle, true); 
    } 
    for (Pair pair : this.mLifecycleCallbacks) {
      if (!paramBoolean || ((Boolean)pair.second).booleanValue())
        ((FragmentManager.FragmentLifecycleCallbacks)pair.first).onFragmentPreCreated(this, paramFragment, paramBundle); 
    } 
  }
  
  void dispatchOnFragmentResumed(Fragment paramFragment, boolean paramBoolean) {
    if (this.mParent != null) {
      FragmentManager fragmentManager = this.mParent.getFragmentManager();
      if (fragmentManager instanceof FragmentManagerImpl)
        ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentResumed(paramFragment, true); 
    } 
    for (Pair pair : this.mLifecycleCallbacks) {
      if (!paramBoolean || ((Boolean)pair.second).booleanValue())
        ((FragmentManager.FragmentLifecycleCallbacks)pair.first).onFragmentResumed(this, paramFragment); 
    } 
  }
  
  void dispatchOnFragmentSaveInstanceState(Fragment paramFragment, Bundle paramBundle, boolean paramBoolean) {
    if (this.mParent != null) {
      FragmentManager fragmentManager = this.mParent.getFragmentManager();
      if (fragmentManager instanceof FragmentManagerImpl)
        ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentSaveInstanceState(paramFragment, paramBundle, true); 
    } 
    for (Pair pair : this.mLifecycleCallbacks) {
      if (!paramBoolean || ((Boolean)pair.second).booleanValue())
        ((FragmentManager.FragmentLifecycleCallbacks)pair.first).onFragmentSaveInstanceState(this, paramFragment, paramBundle); 
    } 
  }
  
  void dispatchOnFragmentStarted(Fragment paramFragment, boolean paramBoolean) {
    if (this.mParent != null) {
      FragmentManager fragmentManager = this.mParent.getFragmentManager();
      if (fragmentManager instanceof FragmentManagerImpl)
        ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentStarted(paramFragment, true); 
    } 
    for (Pair pair : this.mLifecycleCallbacks) {
      if (!paramBoolean || ((Boolean)pair.second).booleanValue())
        ((FragmentManager.FragmentLifecycleCallbacks)pair.first).onFragmentStarted(this, paramFragment); 
    } 
  }
  
  void dispatchOnFragmentStopped(Fragment paramFragment, boolean paramBoolean) {
    if (this.mParent != null) {
      FragmentManager fragmentManager = this.mParent.getFragmentManager();
      if (fragmentManager instanceof FragmentManagerImpl)
        ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentStopped(paramFragment, true); 
    } 
    for (Pair pair : this.mLifecycleCallbacks) {
      if (!paramBoolean || ((Boolean)pair.second).booleanValue())
        ((FragmentManager.FragmentLifecycleCallbacks)pair.first).onFragmentStopped(this, paramFragment); 
    } 
  }
  
  void dispatchOnFragmentViewCreated(Fragment paramFragment, View paramView, Bundle paramBundle, boolean paramBoolean) {
    if (this.mParent != null) {
      FragmentManager fragmentManager = this.mParent.getFragmentManager();
      if (fragmentManager instanceof FragmentManagerImpl)
        ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentViewCreated(paramFragment, paramView, paramBundle, true); 
    } 
    for (Pair pair : this.mLifecycleCallbacks) {
      if (!paramBoolean || ((Boolean)pair.second).booleanValue())
        ((FragmentManager.FragmentLifecycleCallbacks)pair.first).onFragmentViewCreated(this, paramFragment, paramView, paramBundle); 
    } 
  }
  
  void dispatchOnFragmentViewDestroyed(Fragment paramFragment, boolean paramBoolean) {
    if (this.mParent != null) {
      FragmentManager fragmentManager = this.mParent.getFragmentManager();
      if (fragmentManager instanceof FragmentManagerImpl)
        ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentViewDestroyed(paramFragment, true); 
    } 
    for (Pair pair : this.mLifecycleCallbacks) {
      if (!paramBoolean || ((Boolean)pair.second).booleanValue())
        ((FragmentManager.FragmentLifecycleCallbacks)pair.first).onFragmentViewDestroyed(this, paramFragment); 
    } 
  }
  
  public boolean dispatchOptionsItemSelected(MenuItem paramMenuItem) {
    if (this.mCurState < 1)
      return false; 
    for (byte b = 0; b < this.mAdded.size(); b++) {
      Fragment fragment = (Fragment)this.mAdded.get(b);
      if (fragment != null && fragment.performOptionsItemSelected(paramMenuItem))
        return true; 
    } 
    return false;
  }
  
  public void dispatchOptionsMenuClosed(Menu paramMenu) {
    if (this.mCurState < 1)
      return; 
    for (byte b = 0; b < this.mAdded.size(); b++) {
      Fragment fragment = (Fragment)this.mAdded.get(b);
      if (fragment != null)
        fragment.performOptionsMenuClosed(paramMenu); 
    } 
  }
  
  public void dispatchPause() { dispatchStateChange(4); }
  
  public void dispatchPictureInPictureModeChanged(boolean paramBoolean) {
    for (int i = this.mAdded.size() - 1; i >= 0; i--) {
      Fragment fragment = (Fragment)this.mAdded.get(i);
      if (fragment != null)
        fragment.performPictureInPictureModeChanged(paramBoolean); 
    } 
  }
  
  public boolean dispatchPrepareOptionsMenu(Menu paramMenu) {
    int i = this.mCurState;
    byte b = 0;
    if (i < 1)
      return false; 
    int j;
    for (j = 0; b < this.mAdded.size(); j = b1) {
      Fragment fragment = (Fragment)this.mAdded.get(b);
      byte b1 = j;
      if (fragment != null) {
        b1 = j;
        if (fragment.performPrepareOptionsMenu(paramMenu))
          b1 = 1; 
      } 
      b++;
    } 
    return j;
  }
  
  public void dispatchReallyStop() { dispatchStateChange(2); }
  
  public void dispatchResume() {
    this.mStateSaved = false;
    this.mStopped = false;
    dispatchStateChange(5);
  }
  
  public void dispatchStart() {
    this.mStateSaved = false;
    this.mStopped = false;
    dispatchStateChange(4);
  }
  
  public void dispatchStop() {
    this.mStopped = true;
    dispatchStateChange(3);
  }
  
  void doPendingDeferredStart() {
    if (this.mHavePendingDeferredStart) {
      this.mHavePendingDeferredStart = false;
      startPendingDeferredFragments();
    } 
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString) { // Byte code:
    //   0: new java/lang/StringBuilder
    //   3: dup
    //   4: invokespecial <init> : ()V
    //   7: astore #8
    //   9: aload #8
    //   11: aload_1
    //   12: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   15: pop
    //   16: aload #8
    //   18: ldc_w '    '
    //   21: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   24: pop
    //   25: aload #8
    //   27: invokevirtual toString : ()Ljava/lang/String;
    //   30: astore #8
    //   32: aload_0
    //   33: getfield mActive : Landroid/util/SparseArray;
    //   36: astore #9
    //   38: iconst_0
    //   39: istore #6
    //   41: aload #9
    //   43: ifnull -> 170
    //   46: aload_0
    //   47: getfield mActive : Landroid/util/SparseArray;
    //   50: invokevirtual size : ()I
    //   53: istore #7
    //   55: iload #7
    //   57: ifle -> 170
    //   60: aload_3
    //   61: aload_1
    //   62: invokevirtual print : (Ljava/lang/String;)V
    //   65: aload_3
    //   66: ldc_w 'Active Fragments in '
    //   69: invokevirtual print : (Ljava/lang/String;)V
    //   72: aload_3
    //   73: aload_0
    //   74: invokestatic identityHashCode : (Ljava/lang/Object;)I
    //   77: invokestatic toHexString : (I)Ljava/lang/String;
    //   80: invokevirtual print : (Ljava/lang/String;)V
    //   83: aload_3
    //   84: ldc_w ':'
    //   87: invokevirtual println : (Ljava/lang/String;)V
    //   90: iconst_0
    //   91: istore #5
    //   93: iload #5
    //   95: iload #7
    //   97: if_icmpge -> 170
    //   100: aload_0
    //   101: getfield mActive : Landroid/util/SparseArray;
    //   104: iload #5
    //   106: invokevirtual valueAt : (I)Ljava/lang/Object;
    //   109: checkcast android/support/v4/app/Fragment
    //   112: astore #9
    //   114: aload_3
    //   115: aload_1
    //   116: invokevirtual print : (Ljava/lang/String;)V
    //   119: aload_3
    //   120: ldc_w '  #'
    //   123: invokevirtual print : (Ljava/lang/String;)V
    //   126: aload_3
    //   127: iload #5
    //   129: invokevirtual print : (I)V
    //   132: aload_3
    //   133: ldc_w ': '
    //   136: invokevirtual print : (Ljava/lang/String;)V
    //   139: aload_3
    //   140: aload #9
    //   142: invokevirtual println : (Ljava/lang/Object;)V
    //   145: aload #9
    //   147: ifnull -> 161
    //   150: aload #9
    //   152: aload #8
    //   154: aload_2
    //   155: aload_3
    //   156: aload #4
    //   158: invokevirtual dump : (Ljava/lang/String;Ljava/io/FileDescriptor;Ljava/io/PrintWriter;[Ljava/lang/String;)V
    //   161: iload #5
    //   163: iconst_1
    //   164: iadd
    //   165: istore #5
    //   167: goto -> 93
    //   170: aload_0
    //   171: getfield mAdded : Ljava/util/ArrayList;
    //   174: invokevirtual size : ()I
    //   177: istore #7
    //   179: iload #7
    //   181: ifle -> 263
    //   184: aload_3
    //   185: aload_1
    //   186: invokevirtual print : (Ljava/lang/String;)V
    //   189: aload_3
    //   190: ldc_w 'Added Fragments:'
    //   193: invokevirtual println : (Ljava/lang/String;)V
    //   196: iconst_0
    //   197: istore #5
    //   199: iload #5
    //   201: iload #7
    //   203: if_icmpge -> 263
    //   206: aload_0
    //   207: getfield mAdded : Ljava/util/ArrayList;
    //   210: iload #5
    //   212: invokevirtual get : (I)Ljava/lang/Object;
    //   215: checkcast android/support/v4/app/Fragment
    //   218: astore #9
    //   220: aload_3
    //   221: aload_1
    //   222: invokevirtual print : (Ljava/lang/String;)V
    //   225: aload_3
    //   226: ldc_w '  #'
    //   229: invokevirtual print : (Ljava/lang/String;)V
    //   232: aload_3
    //   233: iload #5
    //   235: invokevirtual print : (I)V
    //   238: aload_3
    //   239: ldc_w ': '
    //   242: invokevirtual print : (Ljava/lang/String;)V
    //   245: aload_3
    //   246: aload #9
    //   248: invokevirtual toString : ()Ljava/lang/String;
    //   251: invokevirtual println : (Ljava/lang/String;)V
    //   254: iload #5
    //   256: iconst_1
    //   257: iadd
    //   258: istore #5
    //   260: goto -> 199
    //   263: aload_0
    //   264: getfield mCreatedMenus : Ljava/util/ArrayList;
    //   267: ifnull -> 363
    //   270: aload_0
    //   271: getfield mCreatedMenus : Ljava/util/ArrayList;
    //   274: invokevirtual size : ()I
    //   277: istore #7
    //   279: iload #7
    //   281: ifle -> 363
    //   284: aload_3
    //   285: aload_1
    //   286: invokevirtual print : (Ljava/lang/String;)V
    //   289: aload_3
    //   290: ldc_w 'Fragments Created Menus:'
    //   293: invokevirtual println : (Ljava/lang/String;)V
    //   296: iconst_0
    //   297: istore #5
    //   299: iload #5
    //   301: iload #7
    //   303: if_icmpge -> 363
    //   306: aload_0
    //   307: getfield mCreatedMenus : Ljava/util/ArrayList;
    //   310: iload #5
    //   312: invokevirtual get : (I)Ljava/lang/Object;
    //   315: checkcast android/support/v4/app/Fragment
    //   318: astore #9
    //   320: aload_3
    //   321: aload_1
    //   322: invokevirtual print : (Ljava/lang/String;)V
    //   325: aload_3
    //   326: ldc_w '  #'
    //   329: invokevirtual print : (Ljava/lang/String;)V
    //   332: aload_3
    //   333: iload #5
    //   335: invokevirtual print : (I)V
    //   338: aload_3
    //   339: ldc_w ': '
    //   342: invokevirtual print : (Ljava/lang/String;)V
    //   345: aload_3
    //   346: aload #9
    //   348: invokevirtual toString : ()Ljava/lang/String;
    //   351: invokevirtual println : (Ljava/lang/String;)V
    //   354: iload #5
    //   356: iconst_1
    //   357: iadd
    //   358: istore #5
    //   360: goto -> 299
    //   363: aload_0
    //   364: getfield mBackStack : Ljava/util/ArrayList;
    //   367: ifnull -> 474
    //   370: aload_0
    //   371: getfield mBackStack : Ljava/util/ArrayList;
    //   374: invokevirtual size : ()I
    //   377: istore #7
    //   379: iload #7
    //   381: ifle -> 474
    //   384: aload_3
    //   385: aload_1
    //   386: invokevirtual print : (Ljava/lang/String;)V
    //   389: aload_3
    //   390: ldc_w 'Back Stack:'
    //   393: invokevirtual println : (Ljava/lang/String;)V
    //   396: iconst_0
    //   397: istore #5
    //   399: iload #5
    //   401: iload #7
    //   403: if_icmpge -> 474
    //   406: aload_0
    //   407: getfield mBackStack : Ljava/util/ArrayList;
    //   410: iload #5
    //   412: invokevirtual get : (I)Ljava/lang/Object;
    //   415: checkcast android/support/v4/app/BackStackRecord
    //   418: astore #9
    //   420: aload_3
    //   421: aload_1
    //   422: invokevirtual print : (Ljava/lang/String;)V
    //   425: aload_3
    //   426: ldc_w '  #'
    //   429: invokevirtual print : (Ljava/lang/String;)V
    //   432: aload_3
    //   433: iload #5
    //   435: invokevirtual print : (I)V
    //   438: aload_3
    //   439: ldc_w ': '
    //   442: invokevirtual print : (Ljava/lang/String;)V
    //   445: aload_3
    //   446: aload #9
    //   448: invokevirtual toString : ()Ljava/lang/String;
    //   451: invokevirtual println : (Ljava/lang/String;)V
    //   454: aload #9
    //   456: aload #8
    //   458: aload_2
    //   459: aload_3
    //   460: aload #4
    //   462: invokevirtual dump : (Ljava/lang/String;Ljava/io/FileDescriptor;Ljava/io/PrintWriter;[Ljava/lang/String;)V
    //   465: iload #5
    //   467: iconst_1
    //   468: iadd
    //   469: istore #5
    //   471: goto -> 399
    //   474: aload_0
    //   475: monitorenter
    //   476: aload_0
    //   477: getfield mBackStackIndices : Ljava/util/ArrayList;
    //   480: ifnull -> 571
    //   483: aload_0
    //   484: getfield mBackStackIndices : Ljava/util/ArrayList;
    //   487: invokevirtual size : ()I
    //   490: istore #7
    //   492: iload #7
    //   494: ifle -> 571
    //   497: aload_3
    //   498: aload_1
    //   499: invokevirtual print : (Ljava/lang/String;)V
    //   502: aload_3
    //   503: ldc_w 'Back Stack Indices:'
    //   506: invokevirtual println : (Ljava/lang/String;)V
    //   509: iconst_0
    //   510: istore #5
    //   512: iload #5
    //   514: iload #7
    //   516: if_icmpge -> 571
    //   519: aload_0
    //   520: getfield mBackStackIndices : Ljava/util/ArrayList;
    //   523: iload #5
    //   525: invokevirtual get : (I)Ljava/lang/Object;
    //   528: checkcast android/support/v4/app/BackStackRecord
    //   531: astore_2
    //   532: aload_3
    //   533: aload_1
    //   534: invokevirtual print : (Ljava/lang/String;)V
    //   537: aload_3
    //   538: ldc_w '  #'
    //   541: invokevirtual print : (Ljava/lang/String;)V
    //   544: aload_3
    //   545: iload #5
    //   547: invokevirtual print : (I)V
    //   550: aload_3
    //   551: ldc_w ': '
    //   554: invokevirtual print : (Ljava/lang/String;)V
    //   557: aload_3
    //   558: aload_2
    //   559: invokevirtual println : (Ljava/lang/Object;)V
    //   562: iload #5
    //   564: iconst_1
    //   565: iadd
    //   566: istore #5
    //   568: goto -> 512
    //   571: aload_0
    //   572: getfield mAvailBackStackIndices : Ljava/util/ArrayList;
    //   575: ifnull -> 614
    //   578: aload_0
    //   579: getfield mAvailBackStackIndices : Ljava/util/ArrayList;
    //   582: invokevirtual size : ()I
    //   585: ifle -> 614
    //   588: aload_3
    //   589: aload_1
    //   590: invokevirtual print : (Ljava/lang/String;)V
    //   593: aload_3
    //   594: ldc_w 'mAvailBackStackIndices: '
    //   597: invokevirtual print : (Ljava/lang/String;)V
    //   600: aload_3
    //   601: aload_0
    //   602: getfield mAvailBackStackIndices : Ljava/util/ArrayList;
    //   605: invokevirtual toArray : ()[Ljava/lang/Object;
    //   608: invokestatic toString : ([Ljava/lang/Object;)Ljava/lang/String;
    //   611: invokevirtual println : (Ljava/lang/String;)V
    //   614: aload_0
    //   615: monitorexit
    //   616: aload_0
    //   617: getfield mPendingActions : Ljava/util/ArrayList;
    //   620: ifnull -> 712
    //   623: aload_0
    //   624: getfield mPendingActions : Ljava/util/ArrayList;
    //   627: invokevirtual size : ()I
    //   630: istore #7
    //   632: iload #7
    //   634: ifle -> 712
    //   637: aload_3
    //   638: aload_1
    //   639: invokevirtual print : (Ljava/lang/String;)V
    //   642: aload_3
    //   643: ldc_w 'Pending Actions:'
    //   646: invokevirtual println : (Ljava/lang/String;)V
    //   649: iload #6
    //   651: istore #5
    //   653: iload #5
    //   655: iload #7
    //   657: if_icmpge -> 712
    //   660: aload_0
    //   661: getfield mPendingActions : Ljava/util/ArrayList;
    //   664: iload #5
    //   666: invokevirtual get : (I)Ljava/lang/Object;
    //   669: checkcast android/support/v4/app/FragmentManagerImpl$OpGenerator
    //   672: astore_2
    //   673: aload_3
    //   674: aload_1
    //   675: invokevirtual print : (Ljava/lang/String;)V
    //   678: aload_3
    //   679: ldc_w '  #'
    //   682: invokevirtual print : (Ljava/lang/String;)V
    //   685: aload_3
    //   686: iload #5
    //   688: invokevirtual print : (I)V
    //   691: aload_3
    //   692: ldc_w ': '
    //   695: invokevirtual print : (Ljava/lang/String;)V
    //   698: aload_3
    //   699: aload_2
    //   700: invokevirtual println : (Ljava/lang/Object;)V
    //   703: iload #5
    //   705: iconst_1
    //   706: iadd
    //   707: istore #5
    //   709: goto -> 653
    //   712: aload_3
    //   713: aload_1
    //   714: invokevirtual print : (Ljava/lang/String;)V
    //   717: aload_3
    //   718: ldc_w 'FragmentManager misc state:'
    //   721: invokevirtual println : (Ljava/lang/String;)V
    //   724: aload_3
    //   725: aload_1
    //   726: invokevirtual print : (Ljava/lang/String;)V
    //   729: aload_3
    //   730: ldc_w '  mHost='
    //   733: invokevirtual print : (Ljava/lang/String;)V
    //   736: aload_3
    //   737: aload_0
    //   738: getfield mHost : Landroid/support/v4/app/FragmentHostCallback;
    //   741: invokevirtual println : (Ljava/lang/Object;)V
    //   744: aload_3
    //   745: aload_1
    //   746: invokevirtual print : (Ljava/lang/String;)V
    //   749: aload_3
    //   750: ldc_w '  mContainer='
    //   753: invokevirtual print : (Ljava/lang/String;)V
    //   756: aload_3
    //   757: aload_0
    //   758: getfield mContainer : Landroid/support/v4/app/FragmentContainer;
    //   761: invokevirtual println : (Ljava/lang/Object;)V
    //   764: aload_0
    //   765: getfield mParent : Landroid/support/v4/app/Fragment;
    //   768: ifnull -> 791
    //   771: aload_3
    //   772: aload_1
    //   773: invokevirtual print : (Ljava/lang/String;)V
    //   776: aload_3
    //   777: ldc_w '  mParent='
    //   780: invokevirtual print : (Ljava/lang/String;)V
    //   783: aload_3
    //   784: aload_0
    //   785: getfield mParent : Landroid/support/v4/app/Fragment;
    //   788: invokevirtual println : (Ljava/lang/Object;)V
    //   791: aload_3
    //   792: aload_1
    //   793: invokevirtual print : (Ljava/lang/String;)V
    //   796: aload_3
    //   797: ldc_w '  mCurState='
    //   800: invokevirtual print : (Ljava/lang/String;)V
    //   803: aload_3
    //   804: aload_0
    //   805: getfield mCurState : I
    //   808: invokevirtual print : (I)V
    //   811: aload_3
    //   812: ldc_w ' mStateSaved='
    //   815: invokevirtual print : (Ljava/lang/String;)V
    //   818: aload_3
    //   819: aload_0
    //   820: getfield mStateSaved : Z
    //   823: invokevirtual print : (Z)V
    //   826: aload_3
    //   827: ldc_w ' mStopped='
    //   830: invokevirtual print : (Ljava/lang/String;)V
    //   833: aload_3
    //   834: aload_0
    //   835: getfield mStopped : Z
    //   838: invokevirtual print : (Z)V
    //   841: aload_3
    //   842: ldc_w ' mDestroyed='
    //   845: invokevirtual print : (Ljava/lang/String;)V
    //   848: aload_3
    //   849: aload_0
    //   850: getfield mDestroyed : Z
    //   853: invokevirtual println : (Z)V
    //   856: aload_0
    //   857: getfield mNeedMenuInvalidate : Z
    //   860: ifeq -> 883
    //   863: aload_3
    //   864: aload_1
    //   865: invokevirtual print : (Ljava/lang/String;)V
    //   868: aload_3
    //   869: ldc_w '  mNeedMenuInvalidate='
    //   872: invokevirtual print : (Ljava/lang/String;)V
    //   875: aload_3
    //   876: aload_0
    //   877: getfield mNeedMenuInvalidate : Z
    //   880: invokevirtual println : (Z)V
    //   883: aload_0
    //   884: getfield mNoTransactionsBecause : Ljava/lang/String;
    //   887: ifnull -> 910
    //   890: aload_3
    //   891: aload_1
    //   892: invokevirtual print : (Ljava/lang/String;)V
    //   895: aload_3
    //   896: ldc_w '  mNoTransactionsBecause='
    //   899: invokevirtual print : (Ljava/lang/String;)V
    //   902: aload_3
    //   903: aload_0
    //   904: getfield mNoTransactionsBecause : Ljava/lang/String;
    //   907: invokevirtual println : (Ljava/lang/String;)V
    //   910: return
    //   911: astore_1
    //   912: aload_0
    //   913: monitorexit
    //   914: aload_1
    //   915: athrow
    // Exception table:
    //   from	to	target	type
    //   476	492	911	finally
    //   497	509	911	finally
    //   519	562	911	finally
    //   571	614	911	finally
    //   614	616	911	finally
    //   912	914	911	finally }
  
  public void enqueueAction(OpGenerator paramOpGenerator, boolean paramBoolean) { // Byte code:
    //   0: iload_2
    //   1: ifne -> 8
    //   4: aload_0
    //   5: invokespecial checkStateLoss : ()V
    //   8: aload_0
    //   9: monitorenter
    //   10: aload_0
    //   11: getfield mDestroyed : Z
    //   14: ifne -> 61
    //   17: aload_0
    //   18: getfield mHost : Landroid/support/v4/app/FragmentHostCallback;
    //   21: ifnonnull -> 27
    //   24: goto -> 61
    //   27: aload_0
    //   28: getfield mPendingActions : Ljava/util/ArrayList;
    //   31: ifnonnull -> 45
    //   34: aload_0
    //   35: new java/util/ArrayList
    //   38: dup
    //   39: invokespecial <init> : ()V
    //   42: putfield mPendingActions : Ljava/util/ArrayList;
    //   45: aload_0
    //   46: getfield mPendingActions : Ljava/util/ArrayList;
    //   49: aload_1
    //   50: invokevirtual add : (Ljava/lang/Object;)Z
    //   53: pop
    //   54: aload_0
    //   55: invokespecial scheduleCommit : ()V
    //   58: aload_0
    //   59: monitorexit
    //   60: return
    //   61: iload_2
    //   62: ifeq -> 68
    //   65: aload_0
    //   66: monitorexit
    //   67: return
    //   68: new java/lang/IllegalStateException
    //   71: dup
    //   72: ldc_w 'Activity has been destroyed'
    //   75: invokespecial <init> : (Ljava/lang/String;)V
    //   78: athrow
    //   79: astore_1
    //   80: aload_0
    //   81: monitorexit
    //   82: aload_1
    //   83: athrow
    // Exception table:
    //   from	to	target	type
    //   10	24	79	finally
    //   27	45	79	finally
    //   45	60	79	finally
    //   65	67	79	finally
    //   68	79	79	finally
    //   80	82	79	finally }
  
  void ensureInflatedFragmentView(Fragment paramFragment) {
    if (paramFragment.mFromLayout && !paramFragment.mPerformedCreateView) {
      paramFragment.mView = paramFragment.performCreateView(paramFragment.performGetLayoutInflater(paramFragment.mSavedFragmentState), null, paramFragment.mSavedFragmentState);
      if (paramFragment.mView != null) {
        paramFragment.mInnerView = paramFragment.mView;
        paramFragment.mView.setSaveFromParentEnabled(false);
        if (paramFragment.mHidden)
          paramFragment.mView.setVisibility(8); 
        paramFragment.onViewCreated(paramFragment.mView, paramFragment.mSavedFragmentState);
        dispatchOnFragmentViewCreated(paramFragment, paramFragment.mView, paramFragment.mSavedFragmentState, false);
        return;
      } 
      paramFragment.mInnerView = null;
    } 
  }
  
  public boolean execPendingActions() {
    ensureExecReady(true);
    bool = false;
    while (generateOpsForPendingActions(this.mTmpRecords, this.mTmpIsPop)) {
      this.mExecutingActions = true;
      try {
        removeRedundantOperationsAndExecute(this.mTmpRecords, this.mTmpIsPop);
        cleanupExec();
      } finally {
        cleanupExec();
      } 
    } 
    doPendingDeferredStart();
    burpActive();
    return bool;
  }
  
  public void execSingleAction(OpGenerator paramOpGenerator, boolean paramBoolean) {
    if (paramBoolean && (this.mHost == null || this.mDestroyed))
      return; 
    ensureExecReady(paramBoolean);
    if (paramOpGenerator.generateOps(this.mTmpRecords, this.mTmpIsPop)) {
      this.mExecutingActions = true;
      try {
        removeRedundantOperationsAndExecute(this.mTmpRecords, this.mTmpIsPop);
      } finally {
        cleanupExec();
      } 
    } 
    doPendingDeferredStart();
    burpActive();
  }
  
  public boolean executePendingTransactions() {
    boolean bool = execPendingActions();
    forcePostponedTransactions();
    return bool;
  }
  
  public Fragment findFragmentById(int paramInt) {
    int i;
    for (i = this.mAdded.size() - 1; i >= 0; i--) {
      Fragment fragment = (Fragment)this.mAdded.get(i);
      if (fragment != null && fragment.mFragmentId == paramInt)
        return fragment; 
    } 
    if (this.mActive != null)
      for (i = this.mActive.size() - 1; i >= 0; i--) {
        Fragment fragment = (Fragment)this.mActive.valueAt(i);
        if (fragment != null && fragment.mFragmentId == paramInt)
          return fragment; 
      }  
    return null;
  }
  
  public Fragment findFragmentByTag(String paramString) {
    if (paramString != null)
      for (int i = this.mAdded.size() - 1; i >= 0; i--) {
        Fragment fragment = (Fragment)this.mAdded.get(i);
        if (fragment != null && paramString.equals(fragment.mTag))
          return fragment; 
      }  
    if (this.mActive != null && paramString != null)
      for (int i = this.mActive.size() - 1; i >= 0; i--) {
        Fragment fragment = (Fragment)this.mActive.valueAt(i);
        if (fragment != null && paramString.equals(fragment.mTag))
          return fragment; 
      }  
    return null;
  }
  
  public Fragment findFragmentByWho(String paramString) {
    if (this.mActive != null && paramString != null)
      for (int i = this.mActive.size() - 1; i >= 0; i--) {
        Fragment fragment = (Fragment)this.mActive.valueAt(i);
        if (fragment != null) {
          fragment = fragment.findFragmentByWho(paramString);
          if (fragment != null)
            return fragment; 
        } 
      }  
    return null;
  }
  
  public void freeBackStackIndex(int paramInt) { // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield mBackStackIndices : Ljava/util/ArrayList;
    //   6: iload_1
    //   7: aconst_null
    //   8: invokevirtual set : (ILjava/lang/Object;)Ljava/lang/Object;
    //   11: pop
    //   12: aload_0
    //   13: getfield mAvailBackStackIndices : Ljava/util/ArrayList;
    //   16: ifnonnull -> 30
    //   19: aload_0
    //   20: new java/util/ArrayList
    //   23: dup
    //   24: invokespecial <init> : ()V
    //   27: putfield mAvailBackStackIndices : Ljava/util/ArrayList;
    //   30: getstatic android/support/v4/app/FragmentManagerImpl.DEBUG : Z
    //   33: ifeq -> 68
    //   36: new java/lang/StringBuilder
    //   39: dup
    //   40: invokespecial <init> : ()V
    //   43: astore_2
    //   44: aload_2
    //   45: ldc_w 'Freeing back stack index '
    //   48: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   51: pop
    //   52: aload_2
    //   53: iload_1
    //   54: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   57: pop
    //   58: ldc 'FragmentManager'
    //   60: aload_2
    //   61: invokevirtual toString : ()Ljava/lang/String;
    //   64: invokestatic v : (Ljava/lang/String;Ljava/lang/String;)I
    //   67: pop
    //   68: aload_0
    //   69: getfield mAvailBackStackIndices : Ljava/util/ArrayList;
    //   72: iload_1
    //   73: invokestatic valueOf : (I)Ljava/lang/Integer;
    //   76: invokevirtual add : (Ljava/lang/Object;)Z
    //   79: pop
    //   80: aload_0
    //   81: monitorexit
    //   82: return
    //   83: astore_2
    //   84: aload_0
    //   85: monitorexit
    //   86: aload_2
    //   87: athrow
    // Exception table:
    //   from	to	target	type
    //   2	30	83	finally
    //   30	68	83	finally
    //   68	82	83	finally
    //   84	86	83	finally }
  
  int getActiveFragmentCount() { return (this.mActive == null) ? 0 : this.mActive.size(); }
  
  List<Fragment> getActiveFragments() {
    if (this.mActive == null)
      return null; 
    int i = this.mActive.size();
    ArrayList arrayList = new ArrayList(i);
    for (byte b = 0; b < i; b++)
      arrayList.add(this.mActive.valueAt(b)); 
    return arrayList;
  }
  
  public FragmentManager.BackStackEntry getBackStackEntryAt(int paramInt) { return (FragmentManager.BackStackEntry)this.mBackStack.get(paramInt); }
  
  public int getBackStackEntryCount() { return (this.mBackStack != null) ? this.mBackStack.size() : 0; }
  
  public Fragment getFragment(Bundle paramBundle, String paramString) {
    int i = paramBundle.getInt(paramString, -1);
    if (i == -1)
      return null; 
    Fragment fragment = (Fragment)this.mActive.get(i);
    if (fragment == null) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Fragment no longer exists for key ");
      stringBuilder.append(paramString);
      stringBuilder.append(": index ");
      stringBuilder.append(i);
      throwException(new IllegalStateException(stringBuilder.toString()));
    } 
    return fragment;
  }
  
  public List<Fragment> getFragments() {
    if (this.mAdded.isEmpty())
      return Collections.EMPTY_LIST; 
    synchronized (this.mAdded) {
      return (List)this.mAdded.clone();
    } 
  }
  
  LayoutInflater.Factory2 getLayoutInflaterFactory() { return this; }
  
  public Fragment getPrimaryNavigationFragment() { return this.mPrimaryNav; }
  
  public void hideFragment(Fragment paramFragment) {
    if (DEBUG) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("hide: ");
      stringBuilder.append(paramFragment);
      Log.v("FragmentManager", stringBuilder.toString());
    } 
    if (!paramFragment.mHidden) {
      paramFragment.mHidden = true;
      paramFragment.mHiddenChanged = true ^ paramFragment.mHiddenChanged;
    } 
  }
  
  public boolean isDestroyed() { return this.mDestroyed; }
  
  boolean isStateAtLeast(int paramInt) { return (this.mCurState >= paramInt); }
  
  public boolean isStateSaved() { return (this.mStateSaved || this.mStopped); }
  
  AnimationOrAnimator loadAnimation(Fragment paramFragment, int paramInt1, boolean paramBoolean, int paramInt2) {
    int i = paramFragment.getNextAnim();
    Animation animation = paramFragment.onCreateAnimation(paramInt1, paramBoolean, i);
    if (animation != null)
      return new AnimationOrAnimator(animation, null); 
    animator = paramFragment.onCreateAnimator(paramInt1, paramBoolean, i);
    if (animator != null)
      return new AnimationOrAnimator(animator, null); 
    if (i != 0) {
      boolean bool = "anim".equals(this.mHost.getContext().getResources().getResourceTypeName(i));
      byte b2 = 0;
      byte b1 = b2;
      if (bool)
        try {
          Animation animation1 = AnimationUtils.loadAnimation(this.mHost.getContext(), i);
          if (animation1 != null)
            return new AnimationOrAnimator(animation1, null); 
          b1 = 1;
        } catch (android.content.res.Resources.NotFoundException animator) {
          throw animator;
        } catch (RuntimeException animator) {
          b1 = b2;
        }  
      if (b1 == 0)
        try {
          animator = AnimatorInflater.loadAnimator(this.mHost.getContext(), i);
          if (animator != null)
            return new AnimationOrAnimator(animator, null); 
        } catch (RuntimeException animator) {
          if (bool)
            throw animator; 
          Animation animation1 = AnimationUtils.loadAnimation(this.mHost.getContext(), i);
          if (animation1 != null)
            return new AnimationOrAnimator(animation1, null); 
        }  
    } 
    if (paramInt1 == 0)
      return null; 
    paramInt1 = transitToStyleIndex(paramInt1, paramBoolean);
    if (paramInt1 < 0)
      return null; 
    switch (paramInt1) {
      default:
        paramInt1 = paramInt2;
        if (paramInt2 == 0) {
          paramInt1 = paramInt2;
          if (this.mHost.onHasWindowAnimations())
            paramInt1 = this.mHost.onGetWindowAnimations(); 
        } 
        break;
      case 6:
        return makeFadeAnimation(this.mHost.getContext(), 1.0F, 0.0F);
      case 5:
        return makeFadeAnimation(this.mHost.getContext(), 0.0F, 1.0F);
      case 4:
        return makeOpenCloseAnimation(this.mHost.getContext(), 1.0F, 1.075F, 1.0F, 0.0F);
      case 3:
        return makeOpenCloseAnimation(this.mHost.getContext(), 0.975F, 1.0F, 0.0F, 1.0F);
      case 2:
        return makeOpenCloseAnimation(this.mHost.getContext(), 1.0F, 0.975F, 1.0F, 0.0F);
      case 1:
        return makeOpenCloseAnimation(this.mHost.getContext(), 1.125F, 1.0F, 0.0F, 1.0F);
    } 
    return (paramInt1 == 0) ? null : null;
  }
  
  void makeActive(Fragment paramFragment) {
    if (paramFragment.mIndex >= 0)
      return; 
    int i = this.mNextFragmentIndex;
    this.mNextFragmentIndex = i + 1;
    paramFragment.setIndex(i, this.mParent);
    if (this.mActive == null)
      this.mActive = new SparseArray(); 
    this.mActive.put(paramFragment.mIndex, paramFragment);
    if (DEBUG) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Allocated fragment index ");
      stringBuilder.append(paramFragment);
      Log.v("FragmentManager", stringBuilder.toString());
    } 
  }
  
  void makeInactive(Fragment paramFragment) {
    if (paramFragment.mIndex < 0)
      return; 
    if (DEBUG) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Freeing fragment index ");
      stringBuilder.append(paramFragment);
      Log.v("FragmentManager", stringBuilder.toString());
    } 
    this.mActive.put(paramFragment.mIndex, null);
    paramFragment.initState();
  }
  
  void moveFragmentToExpectedState(Fragment paramFragment) {
    if (paramFragment == null)
      return; 
    int j = this.mCurState;
    int i = j;
    if (paramFragment.mRemoving)
      if (paramFragment.isInBackStack()) {
        i = Math.min(j, 1);
      } else {
        i = Math.min(j, 0);
      }  
    moveToState(paramFragment, i, paramFragment.getNextTransition(), paramFragment.getNextTransitionStyle(), false);
    if (paramFragment.mView != null) {
      Fragment fragment = findFragmentUnder(paramFragment);
      if (fragment != null) {
        View view = fragment.mView;
        ViewGroup viewGroup = paramFragment.mContainer;
        i = viewGroup.indexOfChild(view);
        j = viewGroup.indexOfChild(paramFragment.mView);
        if (j < i) {
          viewGroup.removeViewAt(j);
          viewGroup.addView(paramFragment.mView, i);
        } 
      } 
      if (paramFragment.mIsNewlyAdded && paramFragment.mContainer != null) {
        if (paramFragment.mPostponedAlpha > 0.0F)
          paramFragment.mView.setAlpha(paramFragment.mPostponedAlpha); 
        paramFragment.mPostponedAlpha = 0.0F;
        paramFragment.mIsNewlyAdded = false;
        AnimationOrAnimator animationOrAnimator = loadAnimation(paramFragment, paramFragment.getNextTransition(), true, paramFragment.getNextTransitionStyle());
        if (animationOrAnimator != null) {
          setHWLayerAnimListenerIfAlpha(paramFragment.mView, animationOrAnimator);
          if (animationOrAnimator.animation != null) {
            paramFragment.mView.startAnimation(animationOrAnimator.animation);
          } else {
            animationOrAnimator.animator.setTarget(paramFragment.mView);
            animationOrAnimator.animator.start();
          } 
        } 
      } 
    } 
    if (paramFragment.mHiddenChanged)
      completeShowHideFragment(paramFragment); 
  }
  
  void moveToState(int paramInt, boolean paramBoolean) {
    if (this.mHost == null && paramInt != 0)
      throw new IllegalStateException("No activity"); 
    if (!paramBoolean && paramInt == this.mCurState)
      return; 
    this.mCurState = paramInt;
    if (this.mActive != null) {
      int i = this.mAdded.size();
      for (paramInt = 0; paramInt < i; paramInt++)
        moveFragmentToExpectedState((Fragment)this.mAdded.get(paramInt)); 
      i = this.mActive.size();
      for (paramInt = 0; paramInt < i; paramInt++) {
        Fragment fragment = (Fragment)this.mActive.valueAt(paramInt);
        if (fragment != null && (fragment.mRemoving || fragment.mDetached) && !fragment.mIsNewlyAdded)
          moveFragmentToExpectedState(fragment); 
      } 
      startPendingDeferredFragments();
      if (this.mNeedMenuInvalidate && this.mHost != null && this.mCurState == 5) {
        this.mHost.onSupportInvalidateOptionsMenu();
        this.mNeedMenuInvalidate = false;
      } 
    } 
  }
  
  void moveToState(Fragment paramFragment) { moveToState(paramFragment, this.mCurState, 0, 0, false); }
  
  void moveToState(Fragment paramFragment, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean) { // Byte code:
    //   0: aload_1
    //   1: getfield mAdded : Z
    //   4: istore #9
    //   6: iconst_1
    //   7: istore #7
    //   9: iconst_1
    //   10: istore #8
    //   12: iload #9
    //   14: ifeq -> 30
    //   17: aload_1
    //   18: getfield mDetached : Z
    //   21: ifeq -> 27
    //   24: goto -> 30
    //   27: goto -> 44
    //   30: iload_2
    //   31: istore #6
    //   33: iload #6
    //   35: istore_2
    //   36: iload #6
    //   38: iconst_1
    //   39: if_icmple -> 44
    //   42: iconst_1
    //   43: istore_2
    //   44: iload_2
    //   45: istore #6
    //   47: aload_1
    //   48: getfield mRemoving : Z
    //   51: ifeq -> 91
    //   54: iload_2
    //   55: istore #6
    //   57: iload_2
    //   58: aload_1
    //   59: getfield mState : I
    //   62: if_icmple -> 91
    //   65: aload_1
    //   66: getfield mState : I
    //   69: ifne -> 85
    //   72: aload_1
    //   73: invokevirtual isInBackStack : ()Z
    //   76: ifeq -> 85
    //   79: iconst_1
    //   80: istore #6
    //   82: goto -> 91
    //   85: aload_1
    //   86: getfield mState : I
    //   89: istore #6
    //   91: aload_1
    //   92: getfield mDeferStart : Z
    //   95: ifeq -> 117
    //   98: aload_1
    //   99: getfield mState : I
    //   102: iconst_4
    //   103: if_icmpge -> 117
    //   106: iload #6
    //   108: iconst_3
    //   109: if_icmple -> 117
    //   112: iconst_3
    //   113: istore_2
    //   114: goto -> 120
    //   117: iload #6
    //   119: istore_2
    //   120: aload_1
    //   121: getfield mState : I
    //   124: iload_2
    //   125: if_icmpgt -> 1394
    //   128: aload_1
    //   129: getfield mFromLayout : Z
    //   132: ifeq -> 143
    //   135: aload_1
    //   136: getfield mInLayout : Z
    //   139: ifne -> 143
    //   142: return
    //   143: aload_1
    //   144: invokevirtual getAnimatingAway : ()Landroid/view/View;
    //   147: ifnonnull -> 157
    //   150: aload_1
    //   151: invokevirtual getAnimator : ()Landroid/animation/Animator;
    //   154: ifnull -> 179
    //   157: aload_1
    //   158: aconst_null
    //   159: invokevirtual setAnimatingAway : (Landroid/view/View;)V
    //   162: aload_1
    //   163: aconst_null
    //   164: invokevirtual setAnimator : (Landroid/animation/Animator;)V
    //   167: aload_0
    //   168: aload_1
    //   169: aload_1
    //   170: invokevirtual getStateAfterAnimating : ()I
    //   173: iconst_0
    //   174: iconst_0
    //   175: iconst_1
    //   176: invokevirtual moveToState : (Landroid/support/v4/app/Fragment;IIIZ)V
    //   179: iload_2
    //   180: istore #4
    //   182: iload_2
    //   183: istore #6
    //   185: iload_2
    //   186: istore #7
    //   188: iload_2
    //   189: istore_3
    //   190: aload_1
    //   191: getfield mState : I
    //   194: tableswitch default -> 228, 0 -> 234, 1 -> 769, 2 -> 1235, 3 -> 1254, 4 -> 1318
    //   228: iload_2
    //   229: istore #6
    //   231: goto -> 2050
    //   234: iload_2
    //   235: istore #4
    //   237: iload_2
    //   238: ifle -> 769
    //   241: getstatic android/support/v4/app/FragmentManagerImpl.DEBUG : Z
    //   244: ifeq -> 283
    //   247: new java/lang/StringBuilder
    //   250: dup
    //   251: invokespecial <init> : ()V
    //   254: astore #10
    //   256: aload #10
    //   258: ldc_w 'moveto CREATED: '
    //   261: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   264: pop
    //   265: aload #10
    //   267: aload_1
    //   268: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   271: pop
    //   272: ldc 'FragmentManager'
    //   274: aload #10
    //   276: invokevirtual toString : ()Ljava/lang/String;
    //   279: invokestatic v : (Ljava/lang/String;Ljava/lang/String;)I
    //   282: pop
    //   283: iload_2
    //   284: istore #4
    //   286: aload_1
    //   287: getfield mSavedFragmentState : Landroid/os/Bundle;
    //   290: ifnull -> 424
    //   293: aload_1
    //   294: getfield mSavedFragmentState : Landroid/os/Bundle;
    //   297: aload_0
    //   298: getfield mHost : Landroid/support/v4/app/FragmentHostCallback;
    //   301: invokevirtual getContext : ()Landroid/content/Context;
    //   304: invokevirtual getClassLoader : ()Ljava/lang/ClassLoader;
    //   307: invokevirtual setClassLoader : (Ljava/lang/ClassLoader;)V
    //   310: aload_1
    //   311: aload_1
    //   312: getfield mSavedFragmentState : Landroid/os/Bundle;
    //   315: ldc 'android:view_state'
    //   317: invokevirtual getSparseParcelableArray : (Ljava/lang/String;)Landroid/util/SparseArray;
    //   320: putfield mSavedViewState : Landroid/util/SparseArray;
    //   323: aload_1
    //   324: aload_0
    //   325: aload_1
    //   326: getfield mSavedFragmentState : Landroid/os/Bundle;
    //   329: ldc 'android:target_state'
    //   331: invokevirtual getFragment : (Landroid/os/Bundle;Ljava/lang/String;)Landroid/support/v4/app/Fragment;
    //   334: putfield mTarget : Landroid/support/v4/app/Fragment;
    //   337: aload_1
    //   338: getfield mTarget : Landroid/support/v4/app/Fragment;
    //   341: ifnull -> 358
    //   344: aload_1
    //   345: aload_1
    //   346: getfield mSavedFragmentState : Landroid/os/Bundle;
    //   349: ldc 'android:target_req_state'
    //   351: iconst_0
    //   352: invokevirtual getInt : (Ljava/lang/String;I)I
    //   355: putfield mTargetRequestCode : I
    //   358: aload_1
    //   359: getfield mSavedUserVisibleHint : Ljava/lang/Boolean;
    //   362: ifnull -> 384
    //   365: aload_1
    //   366: aload_1
    //   367: getfield mSavedUserVisibleHint : Ljava/lang/Boolean;
    //   370: invokevirtual booleanValue : ()Z
    //   373: putfield mUserVisibleHint : Z
    //   376: aload_1
    //   377: aconst_null
    //   378: putfield mSavedUserVisibleHint : Ljava/lang/Boolean;
    //   381: goto -> 398
    //   384: aload_1
    //   385: aload_1
    //   386: getfield mSavedFragmentState : Landroid/os/Bundle;
    //   389: ldc 'android:user_visible_hint'
    //   391: iconst_1
    //   392: invokevirtual getBoolean : (Ljava/lang/String;Z)Z
    //   395: putfield mUserVisibleHint : Z
    //   398: iload_2
    //   399: istore #4
    //   401: aload_1
    //   402: getfield mUserVisibleHint : Z
    //   405: ifne -> 424
    //   408: aload_1
    //   409: iconst_1
    //   410: putfield mDeferStart : Z
    //   413: iload_2
    //   414: istore #4
    //   416: iload_2
    //   417: iconst_3
    //   418: if_icmple -> 424
    //   421: iconst_3
    //   422: istore #4
    //   424: aload_1
    //   425: aload_0
    //   426: getfield mHost : Landroid/support/v4/app/FragmentHostCallback;
    //   429: putfield mHost : Landroid/support/v4/app/FragmentHostCallback;
    //   432: aload_1
    //   433: aload_0
    //   434: getfield mParent : Landroid/support/v4/app/Fragment;
    //   437: putfield mParentFragment : Landroid/support/v4/app/Fragment;
    //   440: aload_0
    //   441: getfield mParent : Landroid/support/v4/app/Fragment;
    //   444: ifnull -> 459
    //   447: aload_0
    //   448: getfield mParent : Landroid/support/v4/app/Fragment;
    //   451: getfield mChildFragmentManager : Landroid/support/v4/app/FragmentManagerImpl;
    //   454: astore #10
    //   456: goto -> 468
    //   459: aload_0
    //   460: getfield mHost : Landroid/support/v4/app/FragmentHostCallback;
    //   463: invokevirtual getFragmentManagerImpl : ()Landroid/support/v4/app/FragmentManagerImpl;
    //   466: astore #10
    //   468: aload_1
    //   469: aload #10
    //   471: putfield mFragmentManager : Landroid/support/v4/app/FragmentManagerImpl;
    //   474: aload_1
    //   475: getfield mTarget : Landroid/support/v4/app/Fragment;
    //   478: ifnull -> 591
    //   481: aload_0
    //   482: getfield mActive : Landroid/util/SparseArray;
    //   485: aload_1
    //   486: getfield mTarget : Landroid/support/v4/app/Fragment;
    //   489: getfield mIndex : I
    //   492: invokevirtual get : (I)Ljava/lang/Object;
    //   495: aload_1
    //   496: getfield mTarget : Landroid/support/v4/app/Fragment;
    //   499: if_acmpeq -> 568
    //   502: new java/lang/StringBuilder
    //   505: dup
    //   506: invokespecial <init> : ()V
    //   509: astore #10
    //   511: aload #10
    //   513: ldc_w 'Fragment '
    //   516: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   519: pop
    //   520: aload #10
    //   522: aload_1
    //   523: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   526: pop
    //   527: aload #10
    //   529: ldc_w ' declared target fragment '
    //   532: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   535: pop
    //   536: aload #10
    //   538: aload_1
    //   539: getfield mTarget : Landroid/support/v4/app/Fragment;
    //   542: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   545: pop
    //   546: aload #10
    //   548: ldc_w ' that does not belong to this FragmentManager!'
    //   551: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   554: pop
    //   555: new java/lang/IllegalStateException
    //   558: dup
    //   559: aload #10
    //   561: invokevirtual toString : ()Ljava/lang/String;
    //   564: invokespecial <init> : (Ljava/lang/String;)V
    //   567: athrow
    //   568: aload_1
    //   569: getfield mTarget : Landroid/support/v4/app/Fragment;
    //   572: getfield mState : I
    //   575: iconst_1
    //   576: if_icmpge -> 591
    //   579: aload_0
    //   580: aload_1
    //   581: getfield mTarget : Landroid/support/v4/app/Fragment;
    //   584: iconst_1
    //   585: iconst_0
    //   586: iconst_0
    //   587: iconst_1
    //   588: invokevirtual moveToState : (Landroid/support/v4/app/Fragment;IIIZ)V
    //   591: aload_0
    //   592: aload_1
    //   593: aload_0
    //   594: getfield mHost : Landroid/support/v4/app/FragmentHostCallback;
    //   597: invokevirtual getContext : ()Landroid/content/Context;
    //   600: iconst_0
    //   601: invokevirtual dispatchOnFragmentPreAttached : (Landroid/support/v4/app/Fragment;Landroid/content/Context;Z)V
    //   604: aload_1
    //   605: iconst_0
    //   606: putfield mCalled : Z
    //   609: aload_1
    //   610: aload_0
    //   611: getfield mHost : Landroid/support/v4/app/FragmentHostCallback;
    //   614: invokevirtual getContext : ()Landroid/content/Context;
    //   617: invokevirtual onAttach : (Landroid/content/Context;)V
    //   620: aload_1
    //   621: getfield mCalled : Z
    //   624: ifne -> 674
    //   627: new java/lang/StringBuilder
    //   630: dup
    //   631: invokespecial <init> : ()V
    //   634: astore #10
    //   636: aload #10
    //   638: ldc_w 'Fragment '
    //   641: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   644: pop
    //   645: aload #10
    //   647: aload_1
    //   648: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   651: pop
    //   652: aload #10
    //   654: ldc_w ' did not call through to super.onAttach()'
    //   657: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   660: pop
    //   661: new android/support/v4/app/SuperNotCalledException
    //   664: dup
    //   665: aload #10
    //   667: invokevirtual toString : ()Ljava/lang/String;
    //   670: invokespecial <init> : (Ljava/lang/String;)V
    //   673: athrow
    //   674: aload_1
    //   675: getfield mParentFragment : Landroid/support/v4/app/Fragment;
    //   678: ifnonnull -> 692
    //   681: aload_0
    //   682: getfield mHost : Landroid/support/v4/app/FragmentHostCallback;
    //   685: aload_1
    //   686: invokevirtual onAttachFragment : (Landroid/support/v4/app/Fragment;)V
    //   689: goto -> 700
    //   692: aload_1
    //   693: getfield mParentFragment : Landroid/support/v4/app/Fragment;
    //   696: aload_1
    //   697: invokevirtual onAttachFragment : (Landroid/support/v4/app/Fragment;)V
    //   700: aload_0
    //   701: aload_1
    //   702: aload_0
    //   703: getfield mHost : Landroid/support/v4/app/FragmentHostCallback;
    //   706: invokevirtual getContext : ()Landroid/content/Context;
    //   709: iconst_0
    //   710: invokevirtual dispatchOnFragmentAttached : (Landroid/support/v4/app/Fragment;Landroid/content/Context;Z)V
    //   713: aload_1
    //   714: getfield mIsCreated : Z
    //   717: ifne -> 751
    //   720: aload_0
    //   721: aload_1
    //   722: aload_1
    //   723: getfield mSavedFragmentState : Landroid/os/Bundle;
    //   726: iconst_0
    //   727: invokevirtual dispatchOnFragmentPreCreated : (Landroid/support/v4/app/Fragment;Landroid/os/Bundle;Z)V
    //   730: aload_1
    //   731: aload_1
    //   732: getfield mSavedFragmentState : Landroid/os/Bundle;
    //   735: invokevirtual performCreate : (Landroid/os/Bundle;)V
    //   738: aload_0
    //   739: aload_1
    //   740: aload_1
    //   741: getfield mSavedFragmentState : Landroid/os/Bundle;
    //   744: iconst_0
    //   745: invokevirtual dispatchOnFragmentCreated : (Landroid/support/v4/app/Fragment;Landroid/os/Bundle;Z)V
    //   748: goto -> 764
    //   751: aload_1
    //   752: aload_1
    //   753: getfield mSavedFragmentState : Landroid/os/Bundle;
    //   756: invokevirtual restoreChildFragmentState : (Landroid/os/Bundle;)V
    //   759: aload_1
    //   760: iconst_1
    //   761: putfield mState : I
    //   764: aload_1
    //   765: iconst_0
    //   766: putfield mRetaining : Z
    //   769: aload_0
    //   770: aload_1
    //   771: invokevirtual ensureInflatedFragmentView : (Landroid/support/v4/app/Fragment;)V
    //   774: iload #4
    //   776: istore #6
    //   778: iload #4
    //   780: iconst_1
    //   781: if_icmple -> 1235
    //   784: getstatic android/support/v4/app/FragmentManagerImpl.DEBUG : Z
    //   787: ifeq -> 826
    //   790: new java/lang/StringBuilder
    //   793: dup
    //   794: invokespecial <init> : ()V
    //   797: astore #10
    //   799: aload #10
    //   801: ldc_w 'moveto ACTIVITY_CREATED: '
    //   804: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   807: pop
    //   808: aload #10
    //   810: aload_1
    //   811: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   814: pop
    //   815: ldc 'FragmentManager'
    //   817: aload #10
    //   819: invokevirtual toString : ()Ljava/lang/String;
    //   822: invokestatic v : (Ljava/lang/String;Ljava/lang/String;)I
    //   825: pop
    //   826: aload_1
    //   827: getfield mFromLayout : Z
    //   830: ifne -> 1193
    //   833: aload_1
    //   834: getfield mContainerId : I
    //   837: ifeq -> 1042
    //   840: aload_1
    //   841: getfield mContainerId : I
    //   844: iconst_m1
    //   845: if_icmpne -> 898
    //   848: new java/lang/StringBuilder
    //   851: dup
    //   852: invokespecial <init> : ()V
    //   855: astore #10
    //   857: aload #10
    //   859: ldc_w 'Cannot create fragment '
    //   862: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   865: pop
    //   866: aload #10
    //   868: aload_1
    //   869: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   872: pop
    //   873: aload #10
    //   875: ldc_w ' for a container view with no id'
    //   878: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   881: pop
    //   882: aload_0
    //   883: new java/lang/IllegalArgumentException
    //   886: dup
    //   887: aload #10
    //   889: invokevirtual toString : ()Ljava/lang/String;
    //   892: invokespecial <init> : (Ljava/lang/String;)V
    //   895: invokespecial throwException : (Ljava/lang/RuntimeException;)V
    //   898: aload_0
    //   899: getfield mContainer : Landroid/support/v4/app/FragmentContainer;
    //   902: aload_1
    //   903: getfield mContainerId : I
    //   906: invokevirtual onFindViewById : (I)Landroid/view/View;
    //   909: checkcast android/view/ViewGroup
    //   912: astore #11
    //   914: aload #11
    //   916: astore #10
    //   918: aload #11
    //   920: ifnonnull -> 1045
    //   923: aload #11
    //   925: astore #10
    //   927: aload_1
    //   928: getfield mRestored : Z
    //   931: ifne -> 1045
    //   934: aload_1
    //   935: invokevirtual getResources : ()Landroid/content/res/Resources;
    //   938: aload_1
    //   939: getfield mContainerId : I
    //   942: invokevirtual getResourceName : (I)Ljava/lang/String;
    //   945: astore #10
    //   947: goto -> 955
    //   950: ldc_w 'unknown'
    //   953: astore #10
    //   955: new java/lang/StringBuilder
    //   958: dup
    //   959: invokespecial <init> : ()V
    //   962: astore #12
    //   964: aload #12
    //   966: ldc_w 'No view found for id 0x'
    //   969: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   972: pop
    //   973: aload #12
    //   975: aload_1
    //   976: getfield mContainerId : I
    //   979: invokestatic toHexString : (I)Ljava/lang/String;
    //   982: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   985: pop
    //   986: aload #12
    //   988: ldc_w ' ('
    //   991: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   994: pop
    //   995: aload #12
    //   997: aload #10
    //   999: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1002: pop
    //   1003: aload #12
    //   1005: ldc_w ') for fragment '
    //   1008: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1011: pop
    //   1012: aload #12
    //   1014: aload_1
    //   1015: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1018: pop
    //   1019: aload_0
    //   1020: new java/lang/IllegalArgumentException
    //   1023: dup
    //   1024: aload #12
    //   1026: invokevirtual toString : ()Ljava/lang/String;
    //   1029: invokespecial <init> : (Ljava/lang/String;)V
    //   1032: invokespecial throwException : (Ljava/lang/RuntimeException;)V
    //   1035: aload #11
    //   1037: astore #10
    //   1039: goto -> 1045
    //   1042: aconst_null
    //   1043: astore #10
    //   1045: aload_1
    //   1046: aload #10
    //   1048: putfield mContainer : Landroid/view/ViewGroup;
    //   1051: aload_1
    //   1052: aload_1
    //   1053: aload_1
    //   1054: aload_1
    //   1055: getfield mSavedFragmentState : Landroid/os/Bundle;
    //   1058: invokevirtual performGetLayoutInflater : (Landroid/os/Bundle;)Landroid/view/LayoutInflater;
    //   1061: aload #10
    //   1063: aload_1
    //   1064: getfield mSavedFragmentState : Landroid/os/Bundle;
    //   1067: invokevirtual performCreateView : (Landroid/view/LayoutInflater;Landroid/view/ViewGroup;Landroid/os/Bundle;)Landroid/view/View;
    //   1070: putfield mView : Landroid/view/View;
    //   1073: aload_1
    //   1074: getfield mView : Landroid/view/View;
    //   1077: ifnull -> 1188
    //   1080: aload_1
    //   1081: aload_1
    //   1082: getfield mView : Landroid/view/View;
    //   1085: putfield mInnerView : Landroid/view/View;
    //   1088: aload_1
    //   1089: getfield mView : Landroid/view/View;
    //   1092: iconst_0
    //   1093: invokevirtual setSaveFromParentEnabled : (Z)V
    //   1096: aload #10
    //   1098: ifnull -> 1110
    //   1101: aload #10
    //   1103: aload_1
    //   1104: getfield mView : Landroid/view/View;
    //   1107: invokevirtual addView : (Landroid/view/View;)V
    //   1110: aload_1
    //   1111: getfield mHidden : Z
    //   1114: ifeq -> 1126
    //   1117: aload_1
    //   1118: getfield mView : Landroid/view/View;
    //   1121: bipush #8
    //   1123: invokevirtual setVisibility : (I)V
    //   1126: aload_1
    //   1127: aload_1
    //   1128: getfield mView : Landroid/view/View;
    //   1131: aload_1
    //   1132: getfield mSavedFragmentState : Landroid/os/Bundle;
    //   1135: invokevirtual onViewCreated : (Landroid/view/View;Landroid/os/Bundle;)V
    //   1138: aload_0
    //   1139: aload_1
    //   1140: aload_1
    //   1141: getfield mView : Landroid/view/View;
    //   1144: aload_1
    //   1145: getfield mSavedFragmentState : Landroid/os/Bundle;
    //   1148: iconst_0
    //   1149: invokevirtual dispatchOnFragmentViewCreated : (Landroid/support/v4/app/Fragment;Landroid/view/View;Landroid/os/Bundle;Z)V
    //   1152: aload_1
    //   1153: getfield mView : Landroid/view/View;
    //   1156: invokevirtual getVisibility : ()I
    //   1159: ifne -> 1176
    //   1162: aload_1
    //   1163: getfield mContainer : Landroid/view/ViewGroup;
    //   1166: ifnull -> 1176
    //   1169: iload #8
    //   1171: istore #5
    //   1173: goto -> 1179
    //   1176: iconst_0
    //   1177: istore #5
    //   1179: aload_1
    //   1180: iload #5
    //   1182: putfield mIsNewlyAdded : Z
    //   1185: goto -> 1193
    //   1188: aload_1
    //   1189: aconst_null
    //   1190: putfield mInnerView : Landroid/view/View;
    //   1193: aload_1
    //   1194: aload_1
    //   1195: getfield mSavedFragmentState : Landroid/os/Bundle;
    //   1198: invokevirtual performActivityCreated : (Landroid/os/Bundle;)V
    //   1201: aload_0
    //   1202: aload_1
    //   1203: aload_1
    //   1204: getfield mSavedFragmentState : Landroid/os/Bundle;
    //   1207: iconst_0
    //   1208: invokevirtual dispatchOnFragmentActivityCreated : (Landroid/support/v4/app/Fragment;Landroid/os/Bundle;Z)V
    //   1211: aload_1
    //   1212: getfield mView : Landroid/view/View;
    //   1215: ifnull -> 1226
    //   1218: aload_1
    //   1219: aload_1
    //   1220: getfield mSavedFragmentState : Landroid/os/Bundle;
    //   1223: invokevirtual restoreViewState : (Landroid/os/Bundle;)V
    //   1226: aload_1
    //   1227: aconst_null
    //   1228: putfield mSavedFragmentState : Landroid/os/Bundle;
    //   1231: iload #4
    //   1233: istore #6
    //   1235: iload #6
    //   1237: istore #7
    //   1239: iload #6
    //   1241: iconst_2
    //   1242: if_icmple -> 1254
    //   1245: aload_1
    //   1246: iconst_3
    //   1247: putfield mState : I
    //   1250: iload #6
    //   1252: istore #7
    //   1254: iload #7
    //   1256: istore_3
    //   1257: iload #7
    //   1259: iconst_3
    //   1260: if_icmple -> 1318
    //   1263: getstatic android/support/v4/app/FragmentManagerImpl.DEBUG : Z
    //   1266: ifeq -> 1305
    //   1269: new java/lang/StringBuilder
    //   1272: dup
    //   1273: invokespecial <init> : ()V
    //   1276: astore #10
    //   1278: aload #10
    //   1280: ldc_w 'moveto STARTED: '
    //   1283: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1286: pop
    //   1287: aload #10
    //   1289: aload_1
    //   1290: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1293: pop
    //   1294: ldc 'FragmentManager'
    //   1296: aload #10
    //   1298: invokevirtual toString : ()Ljava/lang/String;
    //   1301: invokestatic v : (Ljava/lang/String;Ljava/lang/String;)I
    //   1304: pop
    //   1305: aload_1
    //   1306: invokevirtual performStart : ()V
    //   1309: aload_0
    //   1310: aload_1
    //   1311: iconst_0
    //   1312: invokevirtual dispatchOnFragmentStarted : (Landroid/support/v4/app/Fragment;Z)V
    //   1315: iload #7
    //   1317: istore_3
    //   1318: iload_3
    //   1319: istore #6
    //   1321: iload_3
    //   1322: iconst_4
    //   1323: if_icmple -> 2050
    //   1326: getstatic android/support/v4/app/FragmentManagerImpl.DEBUG : Z
    //   1329: ifeq -> 1368
    //   1332: new java/lang/StringBuilder
    //   1335: dup
    //   1336: invokespecial <init> : ()V
    //   1339: astore #10
    //   1341: aload #10
    //   1343: ldc_w 'moveto RESUMED: '
    //   1346: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1349: pop
    //   1350: aload #10
    //   1352: aload_1
    //   1353: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1356: pop
    //   1357: ldc 'FragmentManager'
    //   1359: aload #10
    //   1361: invokevirtual toString : ()Ljava/lang/String;
    //   1364: invokestatic v : (Ljava/lang/String;Ljava/lang/String;)I
    //   1367: pop
    //   1368: aload_1
    //   1369: invokevirtual performResume : ()V
    //   1372: aload_0
    //   1373: aload_1
    //   1374: iconst_0
    //   1375: invokevirtual dispatchOnFragmentResumed : (Landroid/support/v4/app/Fragment;Z)V
    //   1378: aload_1
    //   1379: aconst_null
    //   1380: putfield mSavedFragmentState : Landroid/os/Bundle;
    //   1383: aload_1
    //   1384: aconst_null
    //   1385: putfield mSavedViewState : Landroid/util/SparseArray;
    //   1388: iload_3
    //   1389: istore #6
    //   1391: goto -> 2050
    //   1394: iload_2
    //   1395: istore #6
    //   1397: aload_1
    //   1398: getfield mState : I
    //   1401: iload_2
    //   1402: if_icmple -> 2050
    //   1405: aload_1
    //   1406: getfield mState : I
    //   1409: tableswitch default -> 1444, 1 -> 1833, 2 -> 1615, 3 -> 1564, 4 -> 1507, 5 -> 1450
    //   1444: iload_2
    //   1445: istore #6
    //   1447: goto -> 2050
    //   1450: iload_2
    //   1451: iconst_5
    //   1452: if_icmpge -> 1507
    //   1455: getstatic android/support/v4/app/FragmentManagerImpl.DEBUG : Z
    //   1458: ifeq -> 1497
    //   1461: new java/lang/StringBuilder
    //   1464: dup
    //   1465: invokespecial <init> : ()V
    //   1468: astore #10
    //   1470: aload #10
    //   1472: ldc_w 'movefrom RESUMED: '
    //   1475: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1478: pop
    //   1479: aload #10
    //   1481: aload_1
    //   1482: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1485: pop
    //   1486: ldc 'FragmentManager'
    //   1488: aload #10
    //   1490: invokevirtual toString : ()Ljava/lang/String;
    //   1493: invokestatic v : (Ljava/lang/String;Ljava/lang/String;)I
    //   1496: pop
    //   1497: aload_1
    //   1498: invokevirtual performPause : ()V
    //   1501: aload_0
    //   1502: aload_1
    //   1503: iconst_0
    //   1504: invokevirtual dispatchOnFragmentPaused : (Landroid/support/v4/app/Fragment;Z)V
    //   1507: iload_2
    //   1508: iconst_4
    //   1509: if_icmpge -> 1564
    //   1512: getstatic android/support/v4/app/FragmentManagerImpl.DEBUG : Z
    //   1515: ifeq -> 1554
    //   1518: new java/lang/StringBuilder
    //   1521: dup
    //   1522: invokespecial <init> : ()V
    //   1525: astore #10
    //   1527: aload #10
    //   1529: ldc_w 'movefrom STARTED: '
    //   1532: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1535: pop
    //   1536: aload #10
    //   1538: aload_1
    //   1539: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1542: pop
    //   1543: ldc 'FragmentManager'
    //   1545: aload #10
    //   1547: invokevirtual toString : ()Ljava/lang/String;
    //   1550: invokestatic v : (Ljava/lang/String;Ljava/lang/String;)I
    //   1553: pop
    //   1554: aload_1
    //   1555: invokevirtual performStop : ()V
    //   1558: aload_0
    //   1559: aload_1
    //   1560: iconst_0
    //   1561: invokevirtual dispatchOnFragmentStopped : (Landroid/support/v4/app/Fragment;Z)V
    //   1564: iload_2
    //   1565: iconst_3
    //   1566: if_icmpge -> 1615
    //   1569: getstatic android/support/v4/app/FragmentManagerImpl.DEBUG : Z
    //   1572: ifeq -> 1611
    //   1575: new java/lang/StringBuilder
    //   1578: dup
    //   1579: invokespecial <init> : ()V
    //   1582: astore #10
    //   1584: aload #10
    //   1586: ldc_w 'movefrom STOPPED: '
    //   1589: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1592: pop
    //   1593: aload #10
    //   1595: aload_1
    //   1596: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1599: pop
    //   1600: ldc 'FragmentManager'
    //   1602: aload #10
    //   1604: invokevirtual toString : ()Ljava/lang/String;
    //   1607: invokestatic v : (Ljava/lang/String;Ljava/lang/String;)I
    //   1610: pop
    //   1611: aload_1
    //   1612: invokevirtual performReallyStop : ()V
    //   1615: iload_2
    //   1616: iconst_2
    //   1617: if_icmpge -> 1833
    //   1620: getstatic android/support/v4/app/FragmentManagerImpl.DEBUG : Z
    //   1623: ifeq -> 1662
    //   1626: new java/lang/StringBuilder
    //   1629: dup
    //   1630: invokespecial <init> : ()V
    //   1633: astore #10
    //   1635: aload #10
    //   1637: ldc_w 'movefrom ACTIVITY_CREATED: '
    //   1640: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1643: pop
    //   1644: aload #10
    //   1646: aload_1
    //   1647: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1650: pop
    //   1651: ldc 'FragmentManager'
    //   1653: aload #10
    //   1655: invokevirtual toString : ()Ljava/lang/String;
    //   1658: invokestatic v : (Ljava/lang/String;Ljava/lang/String;)I
    //   1661: pop
    //   1662: aload_1
    //   1663: getfield mView : Landroid/view/View;
    //   1666: ifnull -> 1692
    //   1669: aload_0
    //   1670: getfield mHost : Landroid/support/v4/app/FragmentHostCallback;
    //   1673: aload_1
    //   1674: invokevirtual onShouldSaveFragmentState : (Landroid/support/v4/app/Fragment;)Z
    //   1677: ifeq -> 1692
    //   1680: aload_1
    //   1681: getfield mSavedViewState : Landroid/util/SparseArray;
    //   1684: ifnonnull -> 1692
    //   1687: aload_0
    //   1688: aload_1
    //   1689: invokevirtual saveFragmentViewState : (Landroid/support/v4/app/Fragment;)V
    //   1692: aload_1
    //   1693: invokevirtual performDestroyView : ()V
    //   1696: aload_0
    //   1697: aload_1
    //   1698: iconst_0
    //   1699: invokevirtual dispatchOnFragmentViewDestroyed : (Landroid/support/v4/app/Fragment;Z)V
    //   1702: aload_1
    //   1703: getfield mView : Landroid/view/View;
    //   1706: ifnull -> 1813
    //   1709: aload_1
    //   1710: getfield mContainer : Landroid/view/ViewGroup;
    //   1713: ifnull -> 1813
    //   1716: aload_1
    //   1717: getfield mContainer : Landroid/view/ViewGroup;
    //   1720: aload_1
    //   1721: getfield mView : Landroid/view/View;
    //   1724: invokevirtual endViewTransition : (Landroid/view/View;)V
    //   1727: aload_1
    //   1728: getfield mView : Landroid/view/View;
    //   1731: invokevirtual clearAnimation : ()V
    //   1734: aload_0
    //   1735: getfield mCurState : I
    //   1738: ifle -> 1781
    //   1741: aload_0
    //   1742: getfield mDestroyed : Z
    //   1745: ifne -> 1781
    //   1748: aload_1
    //   1749: getfield mView : Landroid/view/View;
    //   1752: invokevirtual getVisibility : ()I
    //   1755: ifne -> 1781
    //   1758: aload_1
    //   1759: getfield mPostponedAlpha : F
    //   1762: fconst_0
    //   1763: fcmpl
    //   1764: iflt -> 1781
    //   1767: aload_0
    //   1768: aload_1
    //   1769: iload_3
    //   1770: iconst_0
    //   1771: iload #4
    //   1773: invokevirtual loadAnimation : (Landroid/support/v4/app/Fragment;IZI)Landroid/support/v4/app/FragmentManagerImpl$AnimationOrAnimator;
    //   1776: astore #10
    //   1778: goto -> 1784
    //   1781: aconst_null
    //   1782: astore #10
    //   1784: aload_1
    //   1785: fconst_0
    //   1786: putfield mPostponedAlpha : F
    //   1789: aload #10
    //   1791: ifnull -> 1802
    //   1794: aload_0
    //   1795: aload_1
    //   1796: aload #10
    //   1798: iload_2
    //   1799: invokespecial animateRemoveFragment : (Landroid/support/v4/app/Fragment;Landroid/support/v4/app/FragmentManagerImpl$AnimationOrAnimator;I)V
    //   1802: aload_1
    //   1803: getfield mContainer : Landroid/view/ViewGroup;
    //   1806: aload_1
    //   1807: getfield mView : Landroid/view/View;
    //   1810: invokevirtual removeView : (Landroid/view/View;)V
    //   1813: aload_1
    //   1814: aconst_null
    //   1815: putfield mContainer : Landroid/view/ViewGroup;
    //   1818: aload_1
    //   1819: aconst_null
    //   1820: putfield mView : Landroid/view/View;
    //   1823: aload_1
    //   1824: aconst_null
    //   1825: putfield mInnerView : Landroid/view/View;
    //   1828: aload_1
    //   1829: iconst_0
    //   1830: putfield mInLayout : Z
    //   1833: iload_2
    //   1834: istore #6
    //   1836: iload_2
    //   1837: iconst_1
    //   1838: if_icmpge -> 2050
    //   1841: aload_0
    //   1842: getfield mDestroyed : Z
    //   1845: ifeq -> 1897
    //   1848: aload_1
    //   1849: invokevirtual getAnimatingAway : ()Landroid/view/View;
    //   1852: ifnull -> 1874
    //   1855: aload_1
    //   1856: invokevirtual getAnimatingAway : ()Landroid/view/View;
    //   1859: astore #10
    //   1861: aload_1
    //   1862: aconst_null
    //   1863: invokevirtual setAnimatingAway : (Landroid/view/View;)V
    //   1866: aload #10
    //   1868: invokevirtual clearAnimation : ()V
    //   1871: goto -> 1897
    //   1874: aload_1
    //   1875: invokevirtual getAnimator : ()Landroid/animation/Animator;
    //   1878: ifnull -> 1897
    //   1881: aload_1
    //   1882: invokevirtual getAnimator : ()Landroid/animation/Animator;
    //   1885: astore #10
    //   1887: aload_1
    //   1888: aconst_null
    //   1889: invokevirtual setAnimator : (Landroid/animation/Animator;)V
    //   1892: aload #10
    //   1894: invokevirtual cancel : ()V
    //   1897: aload_1
    //   1898: invokevirtual getAnimatingAway : ()Landroid/view/View;
    //   1901: ifnonnull -> 2038
    //   1904: aload_1
    //   1905: invokevirtual getAnimator : ()Landroid/animation/Animator;
    //   1908: ifnull -> 1914
    //   1911: goto -> 2038
    //   1914: getstatic android/support/v4/app/FragmentManagerImpl.DEBUG : Z
    //   1917: ifeq -> 1956
    //   1920: new java/lang/StringBuilder
    //   1923: dup
    //   1924: invokespecial <init> : ()V
    //   1927: astore #10
    //   1929: aload #10
    //   1931: ldc_w 'movefrom CREATED: '
    //   1934: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1937: pop
    //   1938: aload #10
    //   1940: aload_1
    //   1941: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1944: pop
    //   1945: ldc 'FragmentManager'
    //   1947: aload #10
    //   1949: invokevirtual toString : ()Ljava/lang/String;
    //   1952: invokestatic v : (Ljava/lang/String;Ljava/lang/String;)I
    //   1955: pop
    //   1956: aload_1
    //   1957: getfield mRetaining : Z
    //   1960: ifne -> 1976
    //   1963: aload_1
    //   1964: invokevirtual performDestroy : ()V
    //   1967: aload_0
    //   1968: aload_1
    //   1969: iconst_0
    //   1970: invokevirtual dispatchOnFragmentDestroyed : (Landroid/support/v4/app/Fragment;Z)V
    //   1973: goto -> 1981
    //   1976: aload_1
    //   1977: iconst_0
    //   1978: putfield mState : I
    //   1981: aload_1
    //   1982: invokevirtual performDetach : ()V
    //   1985: aload_0
    //   1986: aload_1
    //   1987: iconst_0
    //   1988: invokevirtual dispatchOnFragmentDetached : (Landroid/support/v4/app/Fragment;Z)V
    //   1991: iload_2
    //   1992: istore #6
    //   1994: iload #5
    //   1996: ifne -> 2050
    //   1999: aload_1
    //   2000: getfield mRetaining : Z
    //   2003: ifne -> 2017
    //   2006: aload_0
    //   2007: aload_1
    //   2008: invokevirtual makeInactive : (Landroid/support/v4/app/Fragment;)V
    //   2011: iload_2
    //   2012: istore #6
    //   2014: goto -> 2050
    //   2017: aload_1
    //   2018: aconst_null
    //   2019: putfield mHost : Landroid/support/v4/app/FragmentHostCallback;
    //   2022: aload_1
    //   2023: aconst_null
    //   2024: putfield mParentFragment : Landroid/support/v4/app/Fragment;
    //   2027: aload_1
    //   2028: aconst_null
    //   2029: putfield mFragmentManager : Landroid/support/v4/app/FragmentManagerImpl;
    //   2032: iload_2
    //   2033: istore #6
    //   2035: goto -> 2050
    //   2038: aload_1
    //   2039: iload_2
    //   2040: invokevirtual setStateAfterAnimating : (I)V
    //   2043: iload #7
    //   2045: istore #6
    //   2047: goto -> 2050
    //   2050: aload_1
    //   2051: getfield mState : I
    //   2054: iload #6
    //   2056: if_icmpeq -> 2146
    //   2059: new java/lang/StringBuilder
    //   2062: dup
    //   2063: invokespecial <init> : ()V
    //   2066: astore #10
    //   2068: aload #10
    //   2070: ldc_w 'moveToState: Fragment state for '
    //   2073: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2076: pop
    //   2077: aload #10
    //   2079: aload_1
    //   2080: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   2083: pop
    //   2084: aload #10
    //   2086: ldc_w ' not updated inline; '
    //   2089: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2092: pop
    //   2093: aload #10
    //   2095: ldc_w 'expected state '
    //   2098: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2101: pop
    //   2102: aload #10
    //   2104: iload #6
    //   2106: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   2109: pop
    //   2110: aload #10
    //   2112: ldc_w ' found '
    //   2115: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2118: pop
    //   2119: aload #10
    //   2121: aload_1
    //   2122: getfield mState : I
    //   2125: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   2128: pop
    //   2129: ldc 'FragmentManager'
    //   2131: aload #10
    //   2133: invokevirtual toString : ()Ljava/lang/String;
    //   2136: invokestatic w : (Ljava/lang/String;Ljava/lang/String;)I
    //   2139: pop
    //   2140: aload_1
    //   2141: iload #6
    //   2143: putfield mState : I
    //   2146: return
    //   2147: astore #10
    //   2149: goto -> 950
    // Exception table:
    //   from	to	target	type
    //   934	947	2147	android/content/res/Resources$NotFoundException }
  
  public void noteStateNotSaved() {
    this.mSavedNonConfig = null;
    byte b = 0;
    this.mStateSaved = false;
    this.mStopped = false;
    int i = this.mAdded.size();
    while (b < i) {
      Fragment fragment = (Fragment)this.mAdded.get(b);
      if (fragment != null)
        fragment.noteStateNotSaved(); 
      b++;
    } 
  }
  
  public View onCreateView(View paramView, String paramString, Context paramContext, AttributeSet paramAttributeSet) {
    if (!"fragment".equals(paramString))
      return null; 
    paramString = paramAttributeSet.getAttributeValue(null, "class");
    TypedArray typedArray = paramContext.obtainStyledAttributes(paramAttributeSet, FragmentTag.Fragment);
    int i = 0;
    String str1 = paramString;
    if (paramString == null)
      str1 = typedArray.getString(0); 
    int j = typedArray.getResourceId(1, -1);
    String str2 = typedArray.getString(2);
    typedArray.recycle();
    if (!Fragment.isSupportFragmentClass(this.mHost.getContext(), str1))
      return null; 
    if (paramView != null)
      i = paramView.getId(); 
    if (i == -1 && j == -1 && str2 == null) {
      StringBuilder stringBuilder1 = new StringBuilder();
      stringBuilder1.append(paramAttributeSet.getPositionDescription());
      stringBuilder1.append(": Must specify unique android:id, android:tag, or have a parent with an id for ");
      stringBuilder1.append(str1);
      throw new IllegalArgumentException(stringBuilder1.toString());
    } 
    if (j != -1) {
      Fragment fragment1 = findFragmentById(j);
    } else {
      paramString = null;
    } 
    StringBuilder stringBuilder = paramString;
    if (paramString == null) {
      stringBuilder = paramString;
      if (str2 != null)
        stringBuilder = findFragmentByTag(str2); 
    } 
    Fragment fragment = stringBuilder;
    if (stringBuilder == null) {
      fragment = stringBuilder;
      if (i != -1)
        fragment = findFragmentById(i); 
    } 
    if (DEBUG) {
      StringBuilder stringBuilder1 = new StringBuilder();
      stringBuilder1.append("onCreateView: id=0x");
      stringBuilder1.append(Integer.toHexString(j));
      stringBuilder1.append(" fname=");
      stringBuilder1.append(str1);
      stringBuilder1.append(" existing=");
      stringBuilder1.append(fragment);
      Log.v("FragmentManager", stringBuilder1.toString());
    } 
    if (fragment == null) {
      int k;
      stringBuilder = this.mContainer.instantiate(paramContext, str1, null);
      stringBuilder.mFromLayout = true;
      if (j != 0) {
        k = j;
      } else {
        k = i;
      } 
      stringBuilder.mFragmentId = k;
      stringBuilder.mContainerId = i;
      stringBuilder.mTag = str2;
      stringBuilder.mInLayout = true;
      stringBuilder.mFragmentManager = this;
      stringBuilder.mHost = this.mHost;
      stringBuilder.onInflate(this.mHost.getContext(), paramAttributeSet, stringBuilder.mSavedFragmentState);
      addFragment(stringBuilder, true);
    } else {
      if (fragment.mInLayout) {
        StringBuilder stringBuilder1 = new StringBuilder();
        stringBuilder1.append(paramAttributeSet.getPositionDescription());
        stringBuilder1.append(": Duplicate id 0x");
        stringBuilder1.append(Integer.toHexString(j));
        stringBuilder1.append(", tag ");
        stringBuilder1.append(str2);
        stringBuilder1.append(", or parent id 0x");
        stringBuilder1.append(Integer.toHexString(i));
        stringBuilder1.append(" with another fragment for ");
        stringBuilder1.append(str1);
        throw new IllegalArgumentException(stringBuilder1.toString());
      } 
      fragment.mInLayout = true;
      fragment.mHost = this.mHost;
      if (!fragment.mRetaining)
        fragment.onInflate(this.mHost.getContext(), paramAttributeSet, fragment.mSavedFragmentState); 
      stringBuilder = fragment;
    } 
    if (this.mCurState < 1 && stringBuilder.mFromLayout) {
      moveToState(stringBuilder, 1, 0, 0, false);
    } else {
      moveToState(stringBuilder);
    } 
    if (stringBuilder.mView == null) {
      stringBuilder = new StringBuilder();
      stringBuilder.append("Fragment ");
      stringBuilder.append(str1);
      stringBuilder.append(" did not create a view.");
      throw new IllegalStateException(stringBuilder.toString());
    } 
    if (j != 0)
      stringBuilder.mView.setId(j); 
    if (stringBuilder.mView.getTag() == null)
      stringBuilder.mView.setTag(str2); 
    return stringBuilder.mView;
  }
  
  public View onCreateView(String paramString, Context paramContext, AttributeSet paramAttributeSet) { return onCreateView(null, paramString, paramContext, paramAttributeSet); }
  
  public void performPendingDeferredStart(Fragment paramFragment) {
    if (paramFragment.mDeferStart) {
      if (this.mExecutingActions) {
        this.mHavePendingDeferredStart = true;
        return;
      } 
      paramFragment.mDeferStart = false;
      moveToState(paramFragment, this.mCurState, 0, 0, false);
    } 
  }
  
  public void popBackStack() { enqueueAction(new PopBackStackState(null, -1, 0), false); }
  
  public void popBackStack(int paramInt1, int paramInt2) {
    if (paramInt1 < 0) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Bad id: ");
      stringBuilder.append(paramInt1);
      throw new IllegalArgumentException(stringBuilder.toString());
    } 
    enqueueAction(new PopBackStackState(null, paramInt1, paramInt2), false);
  }
  
  public void popBackStack(String paramString, int paramInt) { enqueueAction(new PopBackStackState(paramString, -1, paramInt), false); }
  
  public boolean popBackStackImmediate() {
    checkStateLoss();
    return popBackStackImmediate(null, -1, 0);
  }
  
  public boolean popBackStackImmediate(int paramInt1, int paramInt2) {
    checkStateLoss();
    execPendingActions();
    if (paramInt1 < 0) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Bad id: ");
      stringBuilder.append(paramInt1);
      throw new IllegalArgumentException(stringBuilder.toString());
    } 
    return popBackStackImmediate(null, paramInt1, paramInt2);
  }
  
  public boolean popBackStackImmediate(String paramString, int paramInt) {
    checkStateLoss();
    return popBackStackImmediate(paramString, -1, paramInt);
  }
  
  boolean popBackStackState(ArrayList<BackStackRecord> paramArrayList1, ArrayList<Boolean> paramArrayList2, String paramString, int paramInt1, int paramInt2) {
    byte b;
    if (this.mBackStack == null)
      return false; 
    if (paramString == null && paramInt1 < 0 && (paramInt2 & true) == 0) {
      paramInt1 = this.mBackStack.size() - 1;
      if (paramInt1 < 0)
        return false; 
      paramArrayList1.add(this.mBackStack.remove(paramInt1));
      paramArrayList2.add(Boolean.valueOf(true));
      return true;
    } 
    if (paramString != null || paramInt1 >= 0) {
      int i;
      for (i = this.mBackStack.size() - 1; i >= 0; i--) {
        BackStackRecord backStackRecord = (BackStackRecord)this.mBackStack.get(i);
        if ((paramString != null && paramString.equals(backStackRecord.getName())) || (paramInt1 >= 0 && paramInt1 == backStackRecord.mIndex))
          break; 
      } 
      if (i < 0)
        return false; 
      b = i;
      if ((paramInt2 & true) != 0)
        for (paramInt2 = i - 1;; paramInt2--) {
          b = paramInt2;
          if (paramInt2 >= 0) {
            BackStackRecord backStackRecord = (BackStackRecord)this.mBackStack.get(paramInt2);
            if (paramString == null || !paramString.equals(backStackRecord.getName())) {
              b = paramInt2;
              if (paramInt1 >= 0) {
                b = paramInt2;
                if (paramInt1 == backStackRecord.mIndex)
                  continue; 
              } 
              break;
            } 
            continue;
          } 
          break;
        }  
    } else {
      b = -1;
    } 
    if (b == this.mBackStack.size() - 1)
      return false; 
    for (paramInt1 = this.mBackStack.size() - 1; paramInt1 > b; paramInt1--) {
      paramArrayList1.add(this.mBackStack.remove(paramInt1));
      paramArrayList2.add(Boolean.valueOf(true));
    } 
    return true;
  }
  
  public void putFragment(Bundle paramBundle, String paramString, Fragment paramFragment) {
    if (paramFragment.mIndex < 0) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Fragment ");
      stringBuilder.append(paramFragment);
      stringBuilder.append(" is not currently in the FragmentManager");
      throwException(new IllegalStateException(stringBuilder.toString()));
    } 
    paramBundle.putInt(paramString, paramFragment.mIndex);
  }
  
  public void registerFragmentLifecycleCallbacks(FragmentManager.FragmentLifecycleCallbacks paramFragmentLifecycleCallbacks, boolean paramBoolean) { this.mLifecycleCallbacks.add(new Pair(paramFragmentLifecycleCallbacks, Boolean.valueOf(paramBoolean))); }
  
  public void removeFragment(Fragment paramFragment) {
    if (DEBUG) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("remove: ");
      stringBuilder.append(paramFragment);
      stringBuilder.append(" nesting=");
      stringBuilder.append(paramFragment.mBackStackNesting);
      Log.v("FragmentManager", stringBuilder.toString());
    } 
    boolean bool = paramFragment.isInBackStack();
    if (!paramFragment.mDetached || bool ^ true)
      synchronized (this.mAdded) {
        this.mAdded.remove(paramFragment);
        if (paramFragment.mHasMenu && paramFragment.mMenuVisible)
          this.mNeedMenuInvalidate = true; 
        paramFragment.mAdded = false;
        paramFragment.mRemoving = true;
        return;
      }  
  }
  
  public void removeOnBackStackChangedListener(FragmentManager.OnBackStackChangedListener paramOnBackStackChangedListener) {
    if (this.mBackStackChangeListeners != null)
      this.mBackStackChangeListeners.remove(paramOnBackStackChangedListener); 
  }
  
  void reportBackStackChanged() {
    if (this.mBackStackChangeListeners != null)
      for (byte b = 0; b < this.mBackStackChangeListeners.size(); b++)
        ((FragmentManager.OnBackStackChangedListener)this.mBackStackChangeListeners.get(b)).onBackStackChanged();  
  }
  
  void restoreAllState(Parcelable paramParcelable, FragmentManagerNonConfig paramFragmentManagerNonConfig) {
    Parcelable parcelable;
    if (paramParcelable == null)
      return; 
    FragmentManagerState fragmentManagerState = (FragmentManagerState)paramParcelable;
    if (fragmentManagerState.mActive == null)
      return; 
    if (paramFragmentManagerNonConfig != null) {
      boolean bool;
      List list3 = paramFragmentManagerNonConfig.getFragments();
      List list1 = paramFragmentManagerNonConfig.getChildNonConfigs();
      List list2 = paramFragmentManagerNonConfig.getViewModelStores();
      if (list3 != null) {
        bool = list3.size();
      } else {
        bool = false;
      } 
      byte b = 0;
      while (true) {
        parcelable = list1;
        List list = list2;
        if (b < bool) {
          Fragment fragment = (Fragment)list3.get(b);
          if (DEBUG) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("restoreAllState: re-attaching retained ");
            stringBuilder.append(fragment);
            Log.v("FragmentManager", stringBuilder.toString());
          } 
          byte b1;
          for (b1 = 0; b1 < fragmentManagerState.mActive.length && (fragmentManagerState.mActive[b1]).mIndex != fragment.mIndex; b1++);
          if (b1 == fragmentManagerState.mActive.length) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Could not find active fragment with index ");
            stringBuilder.append(fragment.mIndex);
            throwException(new IllegalStateException(stringBuilder.toString()));
          } 
          FragmentState fragmentState = fragmentManagerState.mActive[b1];
          fragmentState.mInstance = fragment;
          fragment.mSavedViewState = null;
          fragment.mBackStackNesting = 0;
          fragment.mInLayout = false;
          fragment.mAdded = false;
          fragment.mTarget = null;
          if (fragmentState.mSavedFragmentState != null) {
            fragmentState.mSavedFragmentState.setClassLoader(this.mHost.getContext().getClassLoader());
            fragment.mSavedViewState = fragmentState.mSavedFragmentState.getSparseParcelableArray("android:view_state");
            fragment.mSavedFragmentState = fragmentState.mSavedFragmentState;
          } 
          b++;
          continue;
        } 
        break;
      } 
    } else {
      parcelable = null;
      paramParcelable = parcelable;
    } 
    this.mActive = new SparseArray(fragmentManagerState.mActive.length);
    int i;
    for (i = 0; i < fragmentManagerState.mActive.length; i++) {
      FragmentState fragmentState = fragmentManagerState.mActive[i];
      if (fragmentState != null) {
        ViewModelStore viewModelStore;
        FragmentManagerNonConfig fragmentManagerNonConfig;
        if (parcelable != null && i < parcelable.size()) {
          fragmentManagerNonConfig = (FragmentManagerNonConfig)parcelable.get(i);
        } else {
          fragmentManagerNonConfig = null;
        } 
        if (paramParcelable != null && i < paramParcelable.size()) {
          viewModelStore = (ViewModelStore)paramParcelable.get(i);
        } else {
          viewModelStore = null;
        } 
        Fragment fragment = fragmentState.instantiate(this.mHost, this.mContainer, this.mParent, fragmentManagerNonConfig, viewModelStore);
        if (DEBUG) {
          StringBuilder stringBuilder = new StringBuilder();
          stringBuilder.append("restoreAllState: active #");
          stringBuilder.append(i);
          stringBuilder.append(": ");
          stringBuilder.append(fragment);
          Log.v("FragmentManager", stringBuilder.toString());
        } 
        this.mActive.put(fragment.mIndex, fragment);
        fragmentState.mInstance = null;
      } 
    } 
    if (paramFragmentManagerNonConfig != null) {
      List list = paramFragmentManagerNonConfig.getFragments();
      if (list != null) {
        i = list.size();
      } else {
        i = 0;
      } 
      byte b;
      for (b = 0; b < i; b++) {
        Fragment fragment = (Fragment)list.get(b);
        if (fragment.mTargetIndex >= 0) {
          fragment.mTarget = (Fragment)this.mActive.get(fragment.mTargetIndex);
          if (fragment.mTarget == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Re-attaching retained fragment ");
            stringBuilder.append(fragment);
            stringBuilder.append(" target no longer exists: ");
            stringBuilder.append(fragment.mTargetIndex);
            Log.w("FragmentManager", stringBuilder.toString());
          } 
        } 
      } 
    } 
    this.mAdded.clear();
    if (fragmentManagerState.mAdded != null) {
      i = 0;
      while (i < fragmentManagerState.mAdded.length) {
        null = (Fragment)this.mActive.get(fragmentManagerState.mAdded[i]);
        if (null == null) {
          StringBuilder stringBuilder = new StringBuilder();
          stringBuilder.append("No instantiated fragment for index #");
          stringBuilder.append(fragmentManagerState.mAdded[i]);
          throwException(new IllegalStateException(stringBuilder.toString()));
        } 
        null.mAdded = true;
        if (DEBUG) {
          StringBuilder stringBuilder = new StringBuilder();
          stringBuilder.append("restoreAllState: added #");
          stringBuilder.append(i);
          stringBuilder.append(": ");
          stringBuilder.append(null);
          Log.v("FragmentManager", stringBuilder.toString());
        } 
        if (this.mAdded.contains(null))
          throw new IllegalStateException("Already added!"); 
        synchronized (this.mAdded) {
          this.mAdded.add(null);
          i++;
        } 
      } 
    } 
    if (fragmentManagerState.mBackStack != null) {
      this.mBackStack = new ArrayList(fragmentManagerState.mBackStack.length);
      for (i = 0; i < fragmentManagerState.mBackStack.length; i++) {
        BackStackRecord backStackRecord = fragmentManagerState.mBackStack[i].instantiate(this);
        if (DEBUG) {
          StringBuilder stringBuilder = new StringBuilder();
          stringBuilder.append("restoreAllState: back stack #");
          stringBuilder.append(i);
          stringBuilder.append(" (index ");
          stringBuilder.append(backStackRecord.mIndex);
          stringBuilder.append("): ");
          stringBuilder.append(backStackRecord);
          Log.v("FragmentManager", stringBuilder.toString());
          PrintWriter printWriter = new PrintWriter(new LogWriter("FragmentManager"));
          backStackRecord.dump("  ", printWriter, false);
          printWriter.close();
        } 
        this.mBackStack.add(backStackRecord);
        if (backStackRecord.mIndex >= 0)
          setBackStackIndex(backStackRecord.mIndex, backStackRecord); 
      } 
    } else {
      this.mBackStack = null;
    } 
    if (fragmentManagerState.mPrimaryNavActiveIndex >= 0)
      this.mPrimaryNav = (Fragment)this.mActive.get(fragmentManagerState.mPrimaryNavActiveIndex); 
    this.mNextFragmentIndex = fragmentManagerState.mNextFragmentIndex;
  }
  
  FragmentManagerNonConfig retainNonConfig() {
    setRetaining(this.mSavedNonConfig);
    return this.mSavedNonConfig;
  }
  
  Parcelable saveAllState() {
    forcePostponedTransactions();
    endAnimatingAwayFragments();
    execPendingActions();
    this.mStateSaved = true;
    BackStackState[] arrayOfBackStackState = null;
    this.mSavedNonConfig = null;
    if (this.mActive != null) {
      Object object;
      if (this.mActive.size() <= 0)
        return null; 
      int j = this.mActive.size();
      FragmentState[] arrayOfFragmentState = new FragmentState[j];
      byte b2 = 0;
      byte b1 = 0;
      int i = 0;
      while (b1 < j) {
        object = (Fragment)this.mActive.valueAt(b1);
        if (object != null) {
          if (object.mIndex < 0) {
            StringBuilder stringBuilder1 = new StringBuilder();
            stringBuilder1.append("Failure saving state: active ");
            stringBuilder1.append(object);
            stringBuilder1.append(" has cleared index: ");
            stringBuilder1.append(object.mIndex);
            throwException(new IllegalStateException(stringBuilder1.toString()));
          } 
          FragmentState fragmentState = new FragmentState(object);
          arrayOfFragmentState[b1] = fragmentState;
          if (object.mState > 0 && fragmentState.mSavedFragmentState == null) {
            fragmentState.mSavedFragmentState = saveFragmentBasicState(object);
            if (object.mTarget != null) {
              if (object.mTarget.mIndex < 0) {
                StringBuilder stringBuilder1 = new StringBuilder();
                stringBuilder1.append("Failure saving state: ");
                stringBuilder1.append(object);
                stringBuilder1.append(" has target not in fragment manager: ");
                stringBuilder1.append(object.mTarget);
                throwException(new IllegalStateException(stringBuilder1.toString()));
              } 
              if (fragmentState.mSavedFragmentState == null)
                fragmentState.mSavedFragmentState = new Bundle(); 
              putFragment(fragmentState.mSavedFragmentState, "android:target_state", object.mTarget);
              if (object.mTargetRequestCode != 0)
                fragmentState.mSavedFragmentState.putInt("android:target_req_state", object.mTargetRequestCode); 
            } 
          } else {
            fragmentState.mSavedFragmentState = object.mSavedFragmentState;
          } 
          if (DEBUG) {
            StringBuilder stringBuilder1 = new StringBuilder();
            stringBuilder1.append("Saved state of ");
            stringBuilder1.append(object);
            stringBuilder1.append(": ");
            stringBuilder1.append(fragmentState.mSavedFragmentState);
            Log.v("FragmentManager", stringBuilder1.toString());
          } 
          i = 1;
        } 
        b1++;
      } 
      if (!i) {
        if (DEBUG)
          Log.v("FragmentManager", "saveAllState: no fragments!"); 
        return null;
      } 
      i = this.mAdded.size();
      if (i > 0) {
        int[] arrayOfInt = new int[i];
        b1 = 0;
        while (true) {
          object = arrayOfInt;
          if (b1 < i) {
            arrayOfInt[b1] = ((Fragment)this.mAdded.get(b1)).mIndex;
            if (arrayOfInt[b1] < 0) {
              StringBuilder stringBuilder1 = new StringBuilder();
              stringBuilder1.append("Failure saving state: active ");
              stringBuilder1.append(this.mAdded.get(b1));
              stringBuilder1.append(" has cleared index: ");
              stringBuilder1.append(arrayOfInt[b1]);
              throwException(new IllegalStateException(stringBuilder1.toString()));
            } 
            if (DEBUG) {
              StringBuilder stringBuilder1 = new StringBuilder();
              stringBuilder1.append("saveAllState: adding fragment #");
              stringBuilder1.append(b1);
              stringBuilder1.append(": ");
              stringBuilder1.append(this.mAdded.get(b1));
              Log.v("FragmentManager", stringBuilder1.toString());
            } 
            b1++;
            continue;
          } 
          break;
        } 
      } else {
        object = null;
      } 
      StringBuilder stringBuilder = arrayOfBackStackState;
      if (this.mBackStack != null) {
        i = this.mBackStack.size();
        stringBuilder = arrayOfBackStackState;
        if (i > 0) {
          arrayOfBackStackState = new BackStackState[i];
          b1 = b2;
          while (true) {
            stringBuilder = arrayOfBackStackState;
            if (b1 < i) {
              arrayOfBackStackState[b1] = new BackStackState((BackStackRecord)this.mBackStack.get(b1));
              if (DEBUG) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("saveAllState: adding back stack #");
                stringBuilder.append(b1);
                stringBuilder.append(": ");
                stringBuilder.append(this.mBackStack.get(b1));
                Log.v("FragmentManager", stringBuilder.toString());
              } 
              b1++;
              continue;
            } 
            break;
          } 
        } 
      } 
      arrayOfBackStackState = new FragmentManagerState();
      arrayOfBackStackState.mActive = arrayOfFragmentState;
      arrayOfBackStackState.mAdded = object;
      arrayOfBackStackState.mBackStack = stringBuilder;
      if (this.mPrimaryNav != null)
        arrayOfBackStackState.mPrimaryNavActiveIndex = this.mPrimaryNav.mIndex; 
      arrayOfBackStackState.mNextFragmentIndex = this.mNextFragmentIndex;
      saveNonConfig();
      return arrayOfBackStackState;
    } 
    return null;
  }
  
  Bundle saveFragmentBasicState(Fragment paramFragment) {
    if (this.mStateBundle == null)
      this.mStateBundle = new Bundle(); 
    paramFragment.performSaveInstanceState(this.mStateBundle);
    dispatchOnFragmentSaveInstanceState(paramFragment, this.mStateBundle, false);
    if (!this.mStateBundle.isEmpty()) {
      bundle2 = this.mStateBundle;
      this.mStateBundle = null;
    } else {
      bundle2 = null;
    } 
    if (paramFragment.mView != null)
      saveFragmentViewState(paramFragment); 
    Bundle bundle1 = bundle2;
    if (paramFragment.mSavedViewState != null) {
      bundle1 = bundle2;
      if (bundle2 == null)
        bundle1 = new Bundle(); 
      bundle1.putSparseParcelableArray("android:view_state", paramFragment.mSavedViewState);
    } 
    Bundle bundle2 = bundle1;
    if (!paramFragment.mUserVisibleHint) {
      bundle2 = bundle1;
      if (bundle1 == null)
        bundle2 = new Bundle(); 
      bundle2.putBoolean("android:user_visible_hint", paramFragment.mUserVisibleHint);
    } 
    return bundle2;
  }
  
  public Fragment.SavedState saveFragmentInstanceState(Fragment paramFragment) {
    if (paramFragment.mIndex < 0) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Fragment ");
      stringBuilder.append(paramFragment);
      stringBuilder.append(" is not currently in the FragmentManager");
      throwException(new IllegalStateException(stringBuilder.toString()));
    } 
    int i = paramFragment.mState;
    Fragment fragment = null;
    if (i > 0) {
      Fragment.SavedState savedState;
      Bundle bundle = saveFragmentBasicState(paramFragment);
      paramFragment = fragment;
      if (bundle != null)
        savedState = new Fragment.SavedState(bundle); 
      return savedState;
    } 
    return null;
  }
  
  void saveFragmentViewState(Fragment paramFragment) {
    if (paramFragment.mInnerView == null)
      return; 
    if (this.mStateArray == null) {
      this.mStateArray = new SparseArray();
    } else {
      this.mStateArray.clear();
    } 
    paramFragment.mInnerView.saveHierarchyState(this.mStateArray);
    if (this.mStateArray.size() > 0) {
      paramFragment.mSavedViewState = this.mStateArray;
      this.mStateArray = null;
    } 
  }
  
  void saveNonConfig() {
    List list3;
    List list2;
    List list1;
    if (this.mActive != null) {
      StringBuilder stringBuilder1 = null;
      StringBuilder stringBuilder3 = stringBuilder1;
      StringBuilder stringBuilder2 = stringBuilder3;
      byte b = 0;
      while (true) {
        list3 = stringBuilder1;
        list2 = stringBuilder3;
        list1 = stringBuilder2;
        if (b < this.mActive.size()) {
          ArrayList arrayList4;
          Fragment fragment = (Fragment)this.mActive.valueAt(b);
          list2 = stringBuilder1;
          list3 = stringBuilder3;
          ArrayList arrayList5 = stringBuilder2;
          if (fragment != null) {
            FragmentManagerNonConfig fragmentManagerNonConfig;
            ArrayList arrayList7;
            ArrayList arrayList6;
            list1 = stringBuilder1;
            if (fragment.mRetainInstance) {
              byte b1;
              list2 = stringBuilder1;
              if (stringBuilder1 == null)
                list2 = new ArrayList(); 
              list2.add(fragment);
              if (fragment.mTarget != null) {
                b1 = fragment.mTarget.mIndex;
              } else {
                b1 = -1;
              } 
              fragment.mTargetIndex = b1;
              list1 = list2;
              if (DEBUG) {
                stringBuilder1 = new StringBuilder();
                stringBuilder1.append("retainNonConfig: keeping retained ");
                stringBuilder1.append(fragment);
                Log.v("FragmentManager", stringBuilder1.toString());
                list1 = list2;
              } 
            } 
            if (fragment.mChildFragmentManager != null) {
              fragment.mChildFragmentManager.saveNonConfig();
              fragmentManagerNonConfig = fragment.mChildFragmentManager.mSavedNonConfig;
            } else {
              fragmentManagerNonConfig = fragment.mChildNonConfig;
            } 
            stringBuilder1 = stringBuilder3;
            if (stringBuilder3 == null) {
              stringBuilder1 = stringBuilder3;
              if (fragmentManagerNonConfig != null) {
                arrayList7 = new ArrayList(this.mActive.size());
                byte b1 = 0;
                while (true) {
                  arrayList6 = arrayList7;
                  if (b1 < b) {
                    arrayList7.add(null);
                    b1++;
                    continue;
                  } 
                  break;
                } 
              } 
            } 
            if (arrayList6 != null)
              arrayList6.add(fragmentManagerNonConfig); 
            stringBuilder3 = stringBuilder2;
            if (stringBuilder2 == null) {
              stringBuilder3 = stringBuilder2;
              if (fragment.mViewModelStore != null) {
                ArrayList arrayList = new ArrayList(this.mActive.size());
                byte b1 = 0;
                while (true) {
                  arrayList7 = arrayList;
                  if (b1 < b) {
                    arrayList.add(null);
                    b1++;
                    continue;
                  } 
                  break;
                } 
              } 
            } 
            arrayList4 = list1;
            list3 = arrayList6;
            arrayList5 = arrayList7;
            if (arrayList7 != null) {
              arrayList7.add(fragment.mViewModelStore);
              arrayList5 = arrayList7;
              list3 = arrayList6;
              arrayList4 = list1;
            } 
          } 
          b++;
          ArrayList arrayList1 = arrayList4;
          ArrayList arrayList3 = list3;
          ArrayList arrayList2 = arrayList5;
          continue;
        } 
        break;
      } 
    } else {
      list3 = null;
      Object object = list3;
      list1 = object;
      list2 = object;
    } 
    if (list3 == null && list2 == null && list1 == null) {
      this.mSavedNonConfig = null;
      return;
    } 
    this.mSavedNonConfig = new FragmentManagerNonConfig(list3, list2, list1);
  }
  
  public void setBackStackIndex(int paramInt, BackStackRecord paramBackStackRecord) { // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield mBackStackIndices : Ljava/util/ArrayList;
    //   6: ifnonnull -> 20
    //   9: aload_0
    //   10: new java/util/ArrayList
    //   13: dup
    //   14: invokespecial <init> : ()V
    //   17: putfield mBackStackIndices : Ljava/util/ArrayList;
    //   20: aload_0
    //   21: getfield mBackStackIndices : Ljava/util/ArrayList;
    //   24: invokevirtual size : ()I
    //   27: istore #4
    //   29: iload #4
    //   31: istore_3
    //   32: iload_1
    //   33: iload #4
    //   35: if_icmpge -> 109
    //   38: getstatic android/support/v4/app/FragmentManagerImpl.DEBUG : Z
    //   41: ifeq -> 96
    //   44: new java/lang/StringBuilder
    //   47: dup
    //   48: invokespecial <init> : ()V
    //   51: astore #5
    //   53: aload #5
    //   55: ldc_w 'Setting back stack index '
    //   58: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   61: pop
    //   62: aload #5
    //   64: iload_1
    //   65: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   68: pop
    //   69: aload #5
    //   71: ldc_w ' to '
    //   74: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   77: pop
    //   78: aload #5
    //   80: aload_2
    //   81: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   84: pop
    //   85: ldc 'FragmentManager'
    //   87: aload #5
    //   89: invokevirtual toString : ()Ljava/lang/String;
    //   92: invokestatic v : (Ljava/lang/String;Ljava/lang/String;)I
    //   95: pop
    //   96: aload_0
    //   97: getfield mBackStackIndices : Ljava/util/ArrayList;
    //   100: iload_1
    //   101: aload_2
    //   102: invokevirtual set : (ILjava/lang/Object;)Ljava/lang/Object;
    //   105: pop
    //   106: goto -> 269
    //   109: iload_3
    //   110: iload_1
    //   111: if_icmpge -> 202
    //   114: aload_0
    //   115: getfield mBackStackIndices : Ljava/util/ArrayList;
    //   118: aconst_null
    //   119: invokevirtual add : (Ljava/lang/Object;)Z
    //   122: pop
    //   123: aload_0
    //   124: getfield mAvailBackStackIndices : Ljava/util/ArrayList;
    //   127: ifnonnull -> 141
    //   130: aload_0
    //   131: new java/util/ArrayList
    //   134: dup
    //   135: invokespecial <init> : ()V
    //   138: putfield mAvailBackStackIndices : Ljava/util/ArrayList;
    //   141: getstatic android/support/v4/app/FragmentManagerImpl.DEBUG : Z
    //   144: ifeq -> 183
    //   147: new java/lang/StringBuilder
    //   150: dup
    //   151: invokespecial <init> : ()V
    //   154: astore #5
    //   156: aload #5
    //   158: ldc_w 'Adding available back stack index '
    //   161: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   164: pop
    //   165: aload #5
    //   167: iload_3
    //   168: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   171: pop
    //   172: ldc 'FragmentManager'
    //   174: aload #5
    //   176: invokevirtual toString : ()Ljava/lang/String;
    //   179: invokestatic v : (Ljava/lang/String;Ljava/lang/String;)I
    //   182: pop
    //   183: aload_0
    //   184: getfield mAvailBackStackIndices : Ljava/util/ArrayList;
    //   187: iload_3
    //   188: invokestatic valueOf : (I)Ljava/lang/Integer;
    //   191: invokevirtual add : (Ljava/lang/Object;)Z
    //   194: pop
    //   195: iload_3
    //   196: iconst_1
    //   197: iadd
    //   198: istore_3
    //   199: goto -> 109
    //   202: getstatic android/support/v4/app/FragmentManagerImpl.DEBUG : Z
    //   205: ifeq -> 260
    //   208: new java/lang/StringBuilder
    //   211: dup
    //   212: invokespecial <init> : ()V
    //   215: astore #5
    //   217: aload #5
    //   219: ldc_w 'Adding back stack index '
    //   222: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   225: pop
    //   226: aload #5
    //   228: iload_1
    //   229: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   232: pop
    //   233: aload #5
    //   235: ldc_w ' with '
    //   238: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   241: pop
    //   242: aload #5
    //   244: aload_2
    //   245: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   248: pop
    //   249: ldc 'FragmentManager'
    //   251: aload #5
    //   253: invokevirtual toString : ()Ljava/lang/String;
    //   256: invokestatic v : (Ljava/lang/String;Ljava/lang/String;)I
    //   259: pop
    //   260: aload_0
    //   261: getfield mBackStackIndices : Ljava/util/ArrayList;
    //   264: aload_2
    //   265: invokevirtual add : (Ljava/lang/Object;)Z
    //   268: pop
    //   269: aload_0
    //   270: monitorexit
    //   271: return
    //   272: astore_2
    //   273: aload_0
    //   274: monitorexit
    //   275: aload_2
    //   276: athrow
    // Exception table:
    //   from	to	target	type
    //   2	20	272	finally
    //   20	29	272	finally
    //   38	96	272	finally
    //   96	106	272	finally
    //   114	141	272	finally
    //   141	183	272	finally
    //   183	195	272	finally
    //   202	260	272	finally
    //   260	269	272	finally
    //   269	271	272	finally
    //   273	275	272	finally }
  
  public void setPrimaryNavigationFragment(Fragment paramFragment) {
    if (paramFragment != null && (this.mActive.get(paramFragment.mIndex) != paramFragment || (paramFragment.mHost != null && paramFragment.getFragmentManager() != this))) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Fragment ");
      stringBuilder.append(paramFragment);
      stringBuilder.append(" is not an active fragment of FragmentManager ");
      stringBuilder.append(this);
      throw new IllegalArgumentException(stringBuilder.toString());
    } 
    this.mPrimaryNav = paramFragment;
  }
  
  public void showFragment(Fragment paramFragment) {
    if (DEBUG) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("show: ");
      stringBuilder.append(paramFragment);
      Log.v("FragmentManager", stringBuilder.toString());
    } 
    if (paramFragment.mHidden) {
      paramFragment.mHidden = false;
      paramFragment.mHiddenChanged ^= true;
    } 
  }
  
  void startPendingDeferredFragments() {
    if (this.mActive == null)
      return; 
    for (byte b = 0; b < this.mActive.size(); b++) {
      Fragment fragment = (Fragment)this.mActive.valueAt(b);
      if (fragment != null)
        performPendingDeferredStart(fragment); 
    } 
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(128);
    stringBuilder.append("FragmentManager{");
    stringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    stringBuilder.append(" in ");
    if (this.mParent != null) {
      DebugUtils.buildShortClassTag(this.mParent, stringBuilder);
    } else {
      DebugUtils.buildShortClassTag(this.mHost, stringBuilder);
    } 
    stringBuilder.append("}}");
    return stringBuilder.toString();
  }
  
  public void unregisterFragmentLifecycleCallbacks(FragmentManager.FragmentLifecycleCallbacks paramFragmentLifecycleCallbacks) { // Byte code:
    //   0: aload_0
    //   1: getfield mLifecycleCallbacks : Ljava/util/concurrent/CopyOnWriteArrayList;
    //   4: astore #4
    //   6: aload #4
    //   8: monitorenter
    //   9: iconst_0
    //   10: istore_2
    //   11: aload_0
    //   12: getfield mLifecycleCallbacks : Ljava/util/concurrent/CopyOnWriteArrayList;
    //   15: invokevirtual size : ()I
    //   18: istore_3
    //   19: iload_2
    //   20: iload_3
    //   21: if_icmpge -> 54
    //   24: aload_0
    //   25: getfield mLifecycleCallbacks : Ljava/util/concurrent/CopyOnWriteArrayList;
    //   28: iload_2
    //   29: invokevirtual get : (I)Ljava/lang/Object;
    //   32: checkcast android/support/v4/util/Pair
    //   35: getfield first : Ljava/lang/Object;
    //   38: aload_1
    //   39: if_acmpne -> 64
    //   42: aload_0
    //   43: getfield mLifecycleCallbacks : Ljava/util/concurrent/CopyOnWriteArrayList;
    //   46: iload_2
    //   47: invokevirtual remove : (I)Ljava/lang/Object;
    //   50: pop
    //   51: goto -> 54
    //   54: aload #4
    //   56: monitorexit
    //   57: return
    //   58: astore_1
    //   59: aload #4
    //   61: monitorexit
    //   62: aload_1
    //   63: athrow
    //   64: iload_2
    //   65: iconst_1
    //   66: iadd
    //   67: istore_2
    //   68: goto -> 19
    // Exception table:
    //   from	to	target	type
    //   11	19	58	finally
    //   24	51	58	finally
    //   54	57	58	finally
    //   59	62	58	finally }
  
  private static class AnimateOnHWLayerIfNeededListener extends AnimationListenerWrapper {
    View mView;
    
    AnimateOnHWLayerIfNeededListener(View param1View, Animation.AnimationListener param1AnimationListener) {
      super(param1AnimationListener, null);
      this.mView = param1View;
    }
    
    @CallSuper
    public void onAnimationEnd(Animation param1Animation) {
      if (ViewCompat.isAttachedToWindow(this.mView) || Build.VERSION.SDK_INT >= 24) {
        this.mView.post(new Runnable() {
              public void run() { FragmentManagerImpl.AnimateOnHWLayerIfNeededListener.this.mView.setLayerType(0, null); }
            });
      } else {
        this.mView.setLayerType(0, null);
      } 
      super.onAnimationEnd(param1Animation);
    }
  }
  
  class null implements Runnable {
    null() {}
    
    public void run() { this.this$0.mView.setLayerType(0, null); }
  }
  
  private static class AnimationListenerWrapper implements Animation.AnimationListener {
    private final Animation.AnimationListener mWrapped;
    
    private AnimationListenerWrapper(Animation.AnimationListener param1AnimationListener) { this.mWrapped = param1AnimationListener; }
    
    @CallSuper
    public void onAnimationEnd(Animation param1Animation) {
      if (this.mWrapped != null)
        this.mWrapped.onAnimationEnd(param1Animation); 
    }
    
    @CallSuper
    public void onAnimationRepeat(Animation param1Animation) {
      if (this.mWrapped != null)
        this.mWrapped.onAnimationRepeat(param1Animation); 
    }
    
    @CallSuper
    public void onAnimationStart(Animation param1Animation) {
      if (this.mWrapped != null)
        this.mWrapped.onAnimationStart(param1Animation); 
    }
  }
  
  private static class AnimationOrAnimator {
    public final Animation animation = null;
    
    public final Animator animator;
    
    private AnimationOrAnimator(Animator param1Animator) {
      this.animator = param1Animator;
      if (param1Animator == null)
        throw new IllegalStateException("Animator cannot be null"); 
    }
    
    private AnimationOrAnimator(Animation param1Animation) {
      this.animator = null;
      if (param1Animation == null)
        throw new IllegalStateException("Animation cannot be null"); 
    }
  }
  
  private static class AnimatorOnHWLayerIfNeededListener extends AnimatorListenerAdapter {
    View mView;
    
    AnimatorOnHWLayerIfNeededListener(View param1View) { this.mView = param1View; }
    
    public void onAnimationEnd(Animator param1Animator) {
      this.mView.setLayerType(0, null);
      param1Animator.removeListener(this);
    }
    
    public void onAnimationStart(Animator param1Animator) { this.mView.setLayerType(2, null); }
  }
  
  private static class EndViewTransitionAnimator extends AnimationSet implements Runnable {
    private final View mChild;
    
    private boolean mEnded;
    
    private final ViewGroup mParent;
    
    private boolean mTransitionEnded;
    
    EndViewTransitionAnimator(@NonNull Animation param1Animation, @NonNull ViewGroup param1ViewGroup, @NonNull View param1View) {
      super(false);
      this.mParent = param1ViewGroup;
      this.mChild = param1View;
      addAnimation(param1Animation);
    }
    
    public boolean getTransformation(long param1Long, Transformation param1Transformation) {
      if (this.mEnded)
        return this.mTransitionEnded ^ true; 
      if (!super.getTransformation(param1Long, param1Transformation)) {
        this.mEnded = true;
        OneShotPreDrawListener.add(this.mParent, this);
      } 
      return true;
    }
    
    public boolean getTransformation(long param1Long, Transformation param1Transformation, float param1Float) {
      if (this.mEnded)
        return this.mTransitionEnded ^ true; 
      if (!super.getTransformation(param1Long, param1Transformation, param1Float)) {
        this.mEnded = true;
        OneShotPreDrawListener.add(this.mParent, this);
      } 
      return true;
    }
    
    public void run() {
      this.mParent.endViewTransition(this.mChild);
      this.mTransitionEnded = true;
    }
  }
  
  static class FragmentTag {
    public static final int[] Fragment = { 16842755, 16842960, 16842961 };
    
    public static final int Fragment_id = 1;
    
    public static final int Fragment_name = 0;
    
    public static final int Fragment_tag = 2;
  }
  
  static interface OpGenerator {
    boolean generateOps(ArrayList<BackStackRecord> param1ArrayList1, ArrayList<Boolean> param1ArrayList2);
  }
  
  private class PopBackStackState implements OpGenerator {
    final int mFlags;
    
    final int mId;
    
    final String mName;
    
    PopBackStackState(String param1String, int param1Int1, int param1Int2) {
      this.mName = param1String;
      this.mId = param1Int1;
      this.mFlags = param1Int2;
    }
    
    public boolean generateOps(ArrayList<BackStackRecord> param1ArrayList1, ArrayList<Boolean> param1ArrayList2) {
      if (FragmentManagerImpl.this.mPrimaryNav != null && this.mId < 0 && this.mName == null) {
        FragmentManager fragmentManager = FragmentManagerImpl.this.mPrimaryNav.peekChildFragmentManager();
        if (fragmentManager != null && fragmentManager.popBackStackImmediate())
          return false; 
      } 
      return FragmentManagerImpl.this.popBackStackState(param1ArrayList1, param1ArrayList2, this.mName, this.mId, this.mFlags);
    }
  }
  
  static class StartEnterTransitionListener implements Fragment.OnStartEnterTransitionListener {
    private final boolean mIsBack;
    
    private int mNumPostponed;
    
    private final BackStackRecord mRecord;
    
    StartEnterTransitionListener(BackStackRecord param1BackStackRecord, boolean param1Boolean) {
      this.mIsBack = param1Boolean;
      this.mRecord = param1BackStackRecord;
    }
    
    public void cancelTransaction() { this.mRecord.mManager.completeExecute(this.mRecord, this.mIsBack, false, false); }
    
    public void completeTransaction() {
      int i = this.mNumPostponed;
      byte b = 0;
      if (i > 0) {
        i = 1;
      } else {
        i = 0;
      } 
      FragmentManagerImpl fragmentManagerImpl = this.mRecord.mManager;
      int j = fragmentManagerImpl.mAdded.size();
      while (b < j) {
        Fragment fragment = (Fragment)fragmentManagerImpl.mAdded.get(b);
        fragment.setOnStartEnterTransitionListener(null);
        if (i != 0 && fragment.isPostponed())
          fragment.startPostponedEnterTransition(); 
        b++;
      } 
      this.mRecord.mManager.completeExecute(this.mRecord, this.mIsBack, i ^ true, true);
    }
    
    public boolean isReady() { return (this.mNumPostponed == 0); }
    
    public void onStartEnterTransition() {
      this.mNumPostponed--;
      if (this.mNumPostponed != 0)
        return; 
      this.mRecord.mManager.scheduleCommit();
    }
    
    public void startListening() { this.mNumPostponed++; }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/app/FragmentManagerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */