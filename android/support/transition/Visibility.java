package android.support.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.content.res.TypedArrayUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class Visibility extends Transition {
  public static final int MODE_IN = 1;
  
  public static final int MODE_OUT = 2;
  
  private static final String PROPNAME_PARENT = "android:visibility:parent";
  
  private static final String PROPNAME_SCREEN_LOCATION = "android:visibility:screenLocation";
  
  static final String PROPNAME_VISIBILITY = "android:visibility:visibility";
  
  private static final String[] sTransitionProperties = { "android:visibility:visibility", "android:visibility:parent" };
  
  private int mMode = 3;
  
  public Visibility() {}
  
  public Visibility(Context paramContext, AttributeSet paramAttributeSet) {
    super(paramContext, paramAttributeSet);
    TypedArray typedArray = paramContext.obtainStyledAttributes(paramAttributeSet, Styleable.VISIBILITY_TRANSITION);
    int i = TypedArrayUtils.getNamedInt(typedArray, (XmlResourceParser)paramAttributeSet, "transitionVisibilityMode", 0, 0);
    typedArray.recycle();
    if (i != 0)
      setMode(i); 
  }
  
  private void captureValues(TransitionValues paramTransitionValues) {
    int i = paramTransitionValues.view.getVisibility();
    paramTransitionValues.values.put("android:visibility:visibility", Integer.valueOf(i));
    paramTransitionValues.values.put("android:visibility:parent", paramTransitionValues.view.getParent());
    int[] arrayOfInt = new int[2];
    paramTransitionValues.view.getLocationOnScreen(arrayOfInt);
    paramTransitionValues.values.put("android:visibility:screenLocation", arrayOfInt);
  }
  
  private VisibilityInfo getVisibilityChangeInfo(TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2) {
    VisibilityInfo visibilityInfo = new VisibilityInfo(null);
    visibilityInfo.mVisibilityChange = false;
    visibilityInfo.mFadeIn = false;
    if (paramTransitionValues1 != null && paramTransitionValues1.values.containsKey("android:visibility:visibility")) {
      visibilityInfo.mStartVisibility = ((Integer)paramTransitionValues1.values.get("android:visibility:visibility")).intValue();
      visibilityInfo.mStartParent = (ViewGroup)paramTransitionValues1.values.get("android:visibility:parent");
    } else {
      visibilityInfo.mStartVisibility = -1;
      visibilityInfo.mStartParent = null;
    } 
    if (paramTransitionValues2 != null && paramTransitionValues2.values.containsKey("android:visibility:visibility")) {
      visibilityInfo.mEndVisibility = ((Integer)paramTransitionValues2.values.get("android:visibility:visibility")).intValue();
      visibilityInfo.mEndParent = (ViewGroup)paramTransitionValues2.values.get("android:visibility:parent");
    } else {
      visibilityInfo.mEndVisibility = -1;
      visibilityInfo.mEndParent = null;
    } 
    if (paramTransitionValues1 != null && paramTransitionValues2 != null) {
      if (visibilityInfo.mStartVisibility == visibilityInfo.mEndVisibility && visibilityInfo.mStartParent == visibilityInfo.mEndParent)
        return visibilityInfo; 
      if (visibilityInfo.mStartVisibility != visibilityInfo.mEndVisibility) {
        if (visibilityInfo.mStartVisibility == 0) {
          visibilityInfo.mFadeIn = false;
          visibilityInfo.mVisibilityChange = true;
          return visibilityInfo;
        } 
        if (visibilityInfo.mEndVisibility == 0) {
          visibilityInfo.mFadeIn = true;
          visibilityInfo.mVisibilityChange = true;
          return visibilityInfo;
        } 
      } else {
        if (visibilityInfo.mEndParent == null) {
          visibilityInfo.mFadeIn = false;
          visibilityInfo.mVisibilityChange = true;
          return visibilityInfo;
        } 
        if (visibilityInfo.mStartParent == null) {
          visibilityInfo.mFadeIn = true;
          visibilityInfo.mVisibilityChange = true;
          return visibilityInfo;
        } 
      } 
    } else {
      if (paramTransitionValues1 == null && visibilityInfo.mEndVisibility == 0) {
        visibilityInfo.mFadeIn = true;
        visibilityInfo.mVisibilityChange = true;
        return visibilityInfo;
      } 
      if (paramTransitionValues2 == null && visibilityInfo.mStartVisibility == 0) {
        visibilityInfo.mFadeIn = false;
        visibilityInfo.mVisibilityChange = true;
      } 
    } 
    return visibilityInfo;
  }
  
  public void captureEndValues(@NonNull TransitionValues paramTransitionValues) { captureValues(paramTransitionValues); }
  
  public void captureStartValues(@NonNull TransitionValues paramTransitionValues) { captureValues(paramTransitionValues); }
  
  @Nullable
  public Animator createAnimator(@NonNull ViewGroup paramViewGroup, @Nullable TransitionValues paramTransitionValues1, @Nullable TransitionValues paramTransitionValues2) {
    VisibilityInfo visibilityInfo = getVisibilityChangeInfo(paramTransitionValues1, paramTransitionValues2);
    return (visibilityInfo.mVisibilityChange && (visibilityInfo.mStartParent != null || visibilityInfo.mEndParent != null)) ? (visibilityInfo.mFadeIn ? onAppear(paramViewGroup, paramTransitionValues1, visibilityInfo.mStartVisibility, paramTransitionValues2, visibilityInfo.mEndVisibility) : onDisappear(paramViewGroup, paramTransitionValues1, visibilityInfo.mStartVisibility, paramTransitionValues2, visibilityInfo.mEndVisibility)) : null;
  }
  
  public int getMode() { return this.mMode; }
  
  @Nullable
  public String[] getTransitionProperties() { return sTransitionProperties; }
  
  public boolean isTransitionRequired(TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2) {
    byte b = 0;
    if (paramTransitionValues1 == null && paramTransitionValues2 == null)
      return false; 
    if (paramTransitionValues1 != null && paramTransitionValues2 != null && paramTransitionValues2.values.containsKey("android:visibility:visibility") != paramTransitionValues1.values.containsKey("android:visibility:visibility"))
      return false; 
    VisibilityInfo visibilityInfo = getVisibilityChangeInfo(paramTransitionValues1, paramTransitionValues2);
    int i = b;
    if (visibilityInfo.mVisibilityChange) {
      if (visibilityInfo.mStartVisibility != 0) {
        i = b;
        return (visibilityInfo.mEndVisibility == 0) ? true : i;
      } 
    } else {
      return i;
    } 
    return true;
  }
  
  public boolean isVisible(TransitionValues paramTransitionValues) {
    byte b = 0;
    if (paramTransitionValues == null)
      return false; 
    int i = ((Integer)paramTransitionValues.values.get("android:visibility:visibility")).intValue();
    View view = (View)paramTransitionValues.values.get("android:visibility:parent");
    int j = b;
    if (i == 0) {
      j = b;
      if (view != null)
        j = 1; 
    } 
    return j;
  }
  
  public Animator onAppear(ViewGroup paramViewGroup, TransitionValues paramTransitionValues1, int paramInt1, TransitionValues paramTransitionValues2, int paramInt2) {
    if ((this.mMode & true) == 1) {
      if (paramTransitionValues2 == null)
        return null; 
      if (paramTransitionValues1 == null) {
        View view = (View)paramTransitionValues2.view.getParent();
        if ((getVisibilityChangeInfo(getMatchedTransitionValues(view, false), getTransitionValues(view, false))).mVisibilityChange)
          return null; 
      } 
      return onAppear(paramViewGroup, paramTransitionValues2.view, paramTransitionValues1, paramTransitionValues2);
    } 
    return null;
  }
  
  public Animator onAppear(ViewGroup paramViewGroup, View paramView, TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2) { return null; }
  
  public Animator onDisappear(ViewGroup paramViewGroup, TransitionValues paramTransitionValues1, int paramInt1, TransitionValues paramTransitionValues2, int paramInt2) { // Byte code:
    //   0: aload_0
    //   1: getfield mMode : I
    //   4: iconst_2
    //   5: iand
    //   6: iconst_2
    //   7: if_icmpeq -> 12
    //   10: aconst_null
    //   11: areturn
    //   12: aload_2
    //   13: ifnull -> 25
    //   16: aload_2
    //   17: getfield view : Landroid/view/View;
    //   20: astore #8
    //   22: goto -> 28
    //   25: aconst_null
    //   26: astore #8
    //   28: aload #4
    //   30: ifnull -> 43
    //   33: aload #4
    //   35: getfield view : Landroid/view/View;
    //   38: astore #7
    //   40: goto -> 46
    //   43: aconst_null
    //   44: astore #7
    //   46: aload #7
    //   48: ifnull -> 88
    //   51: aload #7
    //   53: invokevirtual getParent : ()Landroid/view/ViewParent;
    //   56: ifnonnull -> 62
    //   59: goto -> 88
    //   62: iload #5
    //   64: iconst_4
    //   65: if_icmpne -> 71
    //   68: goto -> 82
    //   71: aload #8
    //   73: astore #6
    //   75: aload #8
    //   77: aload #7
    //   79: if_acmpne -> 97
    //   82: aconst_null
    //   83: astore #6
    //   85: goto -> 235
    //   88: aload #7
    //   90: ifnull -> 103
    //   93: aload #7
    //   95: astore #6
    //   97: aconst_null
    //   98: astore #7
    //   100: goto -> 235
    //   103: aload #8
    //   105: ifnull -> 228
    //   108: aload #8
    //   110: invokevirtual getParent : ()Landroid/view/ViewParent;
    //   113: ifnonnull -> 123
    //   116: aload #8
    //   118: astore #6
    //   120: goto -> 97
    //   123: aload #8
    //   125: invokevirtual getParent : ()Landroid/view/ViewParent;
    //   128: instanceof android/view/View
    //   131: ifeq -> 228
    //   134: aload #8
    //   136: invokevirtual getParent : ()Landroid/view/ViewParent;
    //   139: checkcast android/view/View
    //   142: astore #6
    //   144: aload_0
    //   145: aload_0
    //   146: aload #6
    //   148: iconst_1
    //   149: invokevirtual getTransitionValues : (Landroid/view/View;Z)Landroid/support/transition/TransitionValues;
    //   152: aload_0
    //   153: aload #6
    //   155: iconst_1
    //   156: invokevirtual getMatchedTransitionValues : (Landroid/view/View;Z)Landroid/support/transition/TransitionValues;
    //   159: invokespecial getVisibilityChangeInfo : (Landroid/support/transition/TransitionValues;Landroid/support/transition/TransitionValues;)Landroid/support/transition/Visibility$VisibilityInfo;
    //   162: getfield mVisibilityChange : Z
    //   165: ifne -> 181
    //   168: aload_1
    //   169: aload #8
    //   171: aload #6
    //   173: invokestatic copyViewImage : (Landroid/view/ViewGroup;Landroid/view/View;Landroid/view/View;)Landroid/view/View;
    //   176: astore #6
    //   178: goto -> 97
    //   181: aload #6
    //   183: invokevirtual getParent : ()Landroid/view/ViewParent;
    //   186: ifnonnull -> 222
    //   189: aload #6
    //   191: invokevirtual getId : ()I
    //   194: istore_3
    //   195: iload_3
    //   196: iconst_m1
    //   197: if_icmpeq -> 222
    //   200: aload_1
    //   201: iload_3
    //   202: invokevirtual findViewById : (I)Landroid/view/View;
    //   205: ifnull -> 222
    //   208: aload_0
    //   209: getfield mCanRemoveViews : Z
    //   212: ifeq -> 222
    //   215: aload #8
    //   217: astore #6
    //   219: goto -> 97
    //   222: aconst_null
    //   223: astore #6
    //   225: goto -> 97
    //   228: aconst_null
    //   229: astore #6
    //   231: aload #6
    //   233: astore #7
    //   235: aload #6
    //   237: ifnull -> 376
    //   240: aload_2
    //   241: ifnull -> 376
    //   244: aload_2
    //   245: getfield values : Ljava/util/Map;
    //   248: ldc 'android:visibility:screenLocation'
    //   250: invokeinterface get : (Ljava/lang/Object;)Ljava/lang/Object;
    //   255: checkcast [I
    //   258: astore #7
    //   260: aload #7
    //   262: iconst_0
    //   263: iaload
    //   264: istore_3
    //   265: aload #7
    //   267: iconst_1
    //   268: iaload
    //   269: istore #5
    //   271: iconst_2
    //   272: newarray int
    //   274: astore #7
    //   276: aload_1
    //   277: aload #7
    //   279: invokevirtual getLocationOnScreen : ([I)V
    //   282: aload #6
    //   284: iload_3
    //   285: aload #7
    //   287: iconst_0
    //   288: iaload
    //   289: isub
    //   290: aload #6
    //   292: invokevirtual getLeft : ()I
    //   295: isub
    //   296: invokevirtual offsetLeftAndRight : (I)V
    //   299: aload #6
    //   301: iload #5
    //   303: aload #7
    //   305: iconst_1
    //   306: iaload
    //   307: isub
    //   308: aload #6
    //   310: invokevirtual getTop : ()I
    //   313: isub
    //   314: invokevirtual offsetTopAndBottom : (I)V
    //   317: aload_1
    //   318: invokestatic getOverlay : (Landroid/view/ViewGroup;)Landroid/support/transition/ViewGroupOverlayImpl;
    //   321: astore #7
    //   323: aload #7
    //   325: aload #6
    //   327: invokeinterface add : (Landroid/view/View;)V
    //   332: aload_0
    //   333: aload_1
    //   334: aload #6
    //   336: aload_2
    //   337: aload #4
    //   339: invokevirtual onDisappear : (Landroid/view/ViewGroup;Landroid/view/View;Landroid/support/transition/TransitionValues;Landroid/support/transition/TransitionValues;)Landroid/animation/Animator;
    //   342: astore_1
    //   343: aload_1
    //   344: ifnonnull -> 358
    //   347: aload #7
    //   349: aload #6
    //   351: invokeinterface remove : (Landroid/view/View;)V
    //   356: aload_1
    //   357: areturn
    //   358: aload_1
    //   359: new android/support/transition/Visibility$1
    //   362: dup
    //   363: aload_0
    //   364: aload #7
    //   366: aload #6
    //   368: invokespecial <init> : (Landroid/support/transition/Visibility;Landroid/support/transition/ViewGroupOverlayImpl;Landroid/view/View;)V
    //   371: invokevirtual addListener : (Landroid/animation/Animator$AnimatorListener;)V
    //   374: aload_1
    //   375: areturn
    //   376: aload #7
    //   378: ifnull -> 447
    //   381: aload #7
    //   383: invokevirtual getVisibility : ()I
    //   386: istore_3
    //   387: aload #7
    //   389: iconst_0
    //   390: invokestatic setTransitionVisibility : (Landroid/view/View;I)V
    //   393: aload_0
    //   394: aload_1
    //   395: aload #7
    //   397: aload_2
    //   398: aload #4
    //   400: invokevirtual onDisappear : (Landroid/view/ViewGroup;Landroid/view/View;Landroid/support/transition/TransitionValues;Landroid/support/transition/TransitionValues;)Landroid/animation/Animator;
    //   403: astore_1
    //   404: aload_1
    //   405: ifnull -> 439
    //   408: new android/support/transition/Visibility$DisappearListener
    //   411: dup
    //   412: aload #7
    //   414: iload #5
    //   416: iconst_1
    //   417: invokespecial <init> : (Landroid/view/View;IZ)V
    //   420: astore_2
    //   421: aload_1
    //   422: aload_2
    //   423: invokevirtual addListener : (Landroid/animation/Animator$AnimatorListener;)V
    //   426: aload_1
    //   427: aload_2
    //   428: invokestatic addPauseListener : (Landroid/animation/Animator;Landroid/animation/AnimatorListenerAdapter;)V
    //   431: aload_0
    //   432: aload_2
    //   433: invokevirtual addListener : (Landroid/support/transition/Transition$TransitionListener;)Landroid/support/transition/Transition;
    //   436: pop
    //   437: aload_1
    //   438: areturn
    //   439: aload #7
    //   441: iload_3
    //   442: invokestatic setTransitionVisibility : (Landroid/view/View;I)V
    //   445: aload_1
    //   446: areturn
    //   447: aconst_null
    //   448: areturn }
  
  public Animator onDisappear(ViewGroup paramViewGroup, View paramView, TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2) { return null; }
  
  public void setMode(int paramInt) {
    if ((paramInt & 0xFFFFFFFC) != 0)
      throw new IllegalArgumentException("Only MODE_IN and MODE_OUT flags are allowed"); 
    this.mMode = paramInt;
  }
  
  private static class DisappearListener extends AnimatorListenerAdapter implements Transition.TransitionListener, AnimatorUtilsApi14.AnimatorPauseListenerCompat {
    boolean mCanceled = false;
    
    private final int mFinalVisibility;
    
    private boolean mLayoutSuppressed;
    
    private final ViewGroup mParent;
    
    private final boolean mSuppressLayout;
    
    private final View mView;
    
    DisappearListener(View param1View, int param1Int, boolean param1Boolean) {
      this.mView = param1View;
      this.mFinalVisibility = param1Int;
      this.mParent = (ViewGroup)param1View.getParent();
      this.mSuppressLayout = param1Boolean;
      suppressLayout(true);
    }
    
    private void hideViewWhenNotCanceled() {
      if (!this.mCanceled) {
        ViewUtils.setTransitionVisibility(this.mView, this.mFinalVisibility);
        if (this.mParent != null)
          this.mParent.invalidate(); 
      } 
      suppressLayout(false);
    }
    
    private void suppressLayout(boolean param1Boolean) {
      if (this.mSuppressLayout && this.mLayoutSuppressed != param1Boolean && this.mParent != null) {
        this.mLayoutSuppressed = param1Boolean;
        ViewGroupUtils.suppressLayout(this.mParent, param1Boolean);
      } 
    }
    
    public void onAnimationCancel(Animator param1Animator) { this.mCanceled = true; }
    
    public void onAnimationEnd(Animator param1Animator) { hideViewWhenNotCanceled(); }
    
    public void onAnimationPause(Animator param1Animator) {
      if (!this.mCanceled)
        ViewUtils.setTransitionVisibility(this.mView, this.mFinalVisibility); 
    }
    
    public void onAnimationRepeat(Animator param1Animator) {}
    
    public void onAnimationResume(Animator param1Animator) {
      if (!this.mCanceled)
        ViewUtils.setTransitionVisibility(this.mView, 0); 
    }
    
    public void onAnimationStart(Animator param1Animator) {}
    
    public void onTransitionCancel(@NonNull Transition param1Transition) {}
    
    public void onTransitionEnd(@NonNull Transition param1Transition) {
      hideViewWhenNotCanceled();
      param1Transition.removeListener(this);
    }
    
    public void onTransitionPause(@NonNull Transition param1Transition) { suppressLayout(false); }
    
    public void onTransitionResume(@NonNull Transition param1Transition) { suppressLayout(true); }
    
    public void onTransitionStart(@NonNull Transition param1Transition) {}
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface Mode {}
  
  private static class VisibilityInfo {
    ViewGroup mEndParent;
    
    int mEndVisibility;
    
    boolean mFadeIn;
    
    ViewGroup mStartParent;
    
    int mStartVisibility;
    
    boolean mVisibilityChange;
    
    private VisibilityInfo() {}
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/transition/Visibility.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */