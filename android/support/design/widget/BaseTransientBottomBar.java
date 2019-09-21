package android.support.design.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.design.R;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseTransientBottomBar<B extends BaseTransientBottomBar<B>> extends Object {
  static final int ANIMATION_DURATION = 250;
  
  static final int ANIMATION_FADE_DURATION = 180;
  
  public static final int LENGTH_INDEFINITE = -2;
  
  public static final int LENGTH_LONG = 0;
  
  public static final int LENGTH_SHORT = -1;
  
  static final int MSG_DISMISS = 1;
  
  static final int MSG_SHOW = 0;
  
  private static final boolean USE_OFFSET_API;
  
  static final Handler sHandler;
  
  private final AccessibilityManager mAccessibilityManager;
  
  private List<BaseCallback<B>> mCallbacks;
  
  private final ContentViewCallback mContentViewCallback;
  
  private final Context mContext;
  
  private int mDuration;
  
  final SnackbarManager.Callback mManagerCallback = new SnackbarManager.Callback() {
      public void dismiss(int param1Int) { BaseTransientBottomBar.sHandler.sendMessage(BaseTransientBottomBar.sHandler.obtainMessage(1, param1Int, 0, BaseTransientBottomBar.this)); }
      
      public void show() { BaseTransientBottomBar.sHandler.sendMessage(BaseTransientBottomBar.sHandler.obtainMessage(0, BaseTransientBottomBar.this)); }
    };
  
  private final ViewGroup mTargetParent;
  
  final SnackbarBaseLayout mView;
  
  static  {
    boolean bool;
    if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT <= 19) {
      bool = true;
    } else {
      bool = false;
    } 
    USE_OFFSET_API = bool;
    sHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
          public boolean handleMessage(Message param1Message) {
            switch (param1Message.what) {
              default:
                return false;
              case 1:
                ((BaseTransientBottomBar)param1Message.obj).hideView(param1Message.arg1);
                return true;
              case 0:
                break;
            } 
            ((BaseTransientBottomBar)param1Message.obj).showView();
            return true;
          }
        });
  }
  
  protected BaseTransientBottomBar(@NonNull ViewGroup paramViewGroup, @NonNull View paramView, @NonNull ContentViewCallback paramContentViewCallback) {
    if (paramViewGroup == null)
      throw new IllegalArgumentException("Transient bottom bar must have non-null parent"); 
    if (paramView == null)
      throw new IllegalArgumentException("Transient bottom bar must have non-null content"); 
    if (paramContentViewCallback == null)
      throw new IllegalArgumentException("Transient bottom bar must have non-null callback"); 
    this.mTargetParent = paramViewGroup;
    this.mContentViewCallback = paramContentViewCallback;
    this.mContext = paramViewGroup.getContext();
    ThemeUtils.checkAppCompatTheme(this.mContext);
    this.mView = (SnackbarBaseLayout)LayoutInflater.from(this.mContext).inflate(R.layout.design_layout_snackbar, this.mTargetParent, false);
    this.mView.addView(paramView);
    ViewCompat.setAccessibilityLiveRegion(this.mView, 1);
    ViewCompat.setImportantForAccessibility(this.mView, 1);
    ViewCompat.setFitsSystemWindows(this.mView, true);
    ViewCompat.setOnApplyWindowInsetsListener(this.mView, new OnApplyWindowInsetsListener() {
          public WindowInsetsCompat onApplyWindowInsets(View param1View, WindowInsetsCompat param1WindowInsetsCompat) {
            param1View.setPadding(param1View.getPaddingLeft(), param1View.getPaddingTop(), param1View.getPaddingRight(), param1WindowInsetsCompat.getSystemWindowInsetBottom());
            return param1WindowInsetsCompat;
          }
        });
    this.mAccessibilityManager = (AccessibilityManager)this.mContext.getSystemService("accessibility");
  }
  
  private void animateViewOut(final int event) {
    if (Build.VERSION.SDK_INT >= 12) {
      ValueAnimator valueAnimator = new ValueAnimator();
      valueAnimator.setIntValues(new int[] { 0, this.mView.getHeight() });
      valueAnimator.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
      valueAnimator.setDuration(250L);
      valueAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator param1Animator) { BaseTransientBottomBar.this.onViewHidden(event); }
            
            public void onAnimationStart(Animator param1Animator) { BaseTransientBottomBar.this.mContentViewCallback.animateContentOut(0, 180); }
          });
      valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private int mPreviousAnimatedIntValue = 0;
            
            public void onAnimationUpdate(ValueAnimator param1ValueAnimator) {
              int i = ((Integer)param1ValueAnimator.getAnimatedValue()).intValue();
              if (USE_OFFSET_API) {
                ViewCompat.offsetTopAndBottom(BaseTransientBottomBar.this.mView, i - this.mPreviousAnimatedIntValue);
              } else {
                BaseTransientBottomBar.this.mView.setTranslationY(i);
              } 
              this.mPreviousAnimatedIntValue = i;
            }
          });
      valueAnimator.start();
      return;
    } 
    Animation animation = AnimationUtils.loadAnimation(this.mView.getContext(), R.anim.design_snackbar_out);
    animation.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
    animation.setDuration(250L);
    animation.setAnimationListener(new Animation.AnimationListener() {
          public void onAnimationEnd(Animation param1Animation) { BaseTransientBottomBar.this.onViewHidden(event); }
          
          public void onAnimationRepeat(Animation param1Animation) {}
          
          public void onAnimationStart(Animation param1Animation) {}
        });
    this.mView.startAnimation(animation);
  }
  
  @NonNull
  public B addCallback(@NonNull BaseCallback<B> paramBaseCallback) {
    if (paramBaseCallback == null)
      return (B)this; 
    if (this.mCallbacks == null)
      this.mCallbacks = new ArrayList(); 
    this.mCallbacks.add(paramBaseCallback);
    return (B)this;
  }
  
  void animateViewIn() {
    if (Build.VERSION.SDK_INT >= 12) {
      final int viewHeight = this.mView.getHeight();
      if (USE_OFFSET_API) {
        ViewCompat.offsetTopAndBottom(this.mView, i);
      } else {
        this.mView.setTranslationY(i);
      } 
      ValueAnimator valueAnimator = new ValueAnimator();
      valueAnimator.setIntValues(new int[] { i, 0 });
      valueAnimator.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
      valueAnimator.setDuration(250L);
      valueAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator param1Animator) { BaseTransientBottomBar.this.onViewShown(); }
            
            public void onAnimationStart(Animator param1Animator) { BaseTransientBottomBar.this.mContentViewCallback.animateContentIn(70, 180); }
          });
      valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private int mPreviousAnimatedIntValue = viewHeight;
            
            public void onAnimationUpdate(ValueAnimator param1ValueAnimator) {
              int i = ((Integer)param1ValueAnimator.getAnimatedValue()).intValue();
              if (USE_OFFSET_API) {
                ViewCompat.offsetTopAndBottom(BaseTransientBottomBar.this.mView, i - this.mPreviousAnimatedIntValue);
              } else {
                BaseTransientBottomBar.this.mView.setTranslationY(i);
              } 
              this.mPreviousAnimatedIntValue = i;
            }
          });
      valueAnimator.start();
      return;
    } 
    Animation animation = AnimationUtils.loadAnimation(this.mView.getContext(), R.anim.design_snackbar_in);
    animation.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
    animation.setDuration(250L);
    animation.setAnimationListener(new Animation.AnimationListener() {
          public void onAnimationEnd(Animation param1Animation) { BaseTransientBottomBar.this.onViewShown(); }
          
          public void onAnimationRepeat(Animation param1Animation) {}
          
          public void onAnimationStart(Animation param1Animation) {}
        });
    this.mView.startAnimation(animation);
  }
  
  public void dismiss() { dispatchDismiss(3); }
  
  void dispatchDismiss(int paramInt) { SnackbarManager.getInstance().dismiss(this.mManagerCallback, paramInt); }
  
  @NonNull
  public Context getContext() { return this.mContext; }
  
  public int getDuration() { return this.mDuration; }
  
  @NonNull
  public View getView() { return this.mView; }
  
  final void hideView(int paramInt) {
    if (shouldAnimate() && this.mView.getVisibility() == 0) {
      animateViewOut(paramInt);
      return;
    } 
    onViewHidden(paramInt);
  }
  
  public boolean isShown() { return SnackbarManager.getInstance().isCurrent(this.mManagerCallback); }
  
  public boolean isShownOrQueued() { return SnackbarManager.getInstance().isCurrentOrNext(this.mManagerCallback); }
  
  void onViewHidden(int paramInt) {
    SnackbarManager.getInstance().onDismissed(this.mManagerCallback);
    if (this.mCallbacks != null)
      for (int i = this.mCallbacks.size() - 1; i >= 0; i--)
        ((BaseCallback)this.mCallbacks.get(i)).onDismissed(this, paramInt);  
    if (Build.VERSION.SDK_INT < 11)
      this.mView.setVisibility(8); 
    ViewParent viewParent = this.mView.getParent();
    if (viewParent instanceof ViewGroup)
      ((ViewGroup)viewParent).removeView(this.mView); 
  }
  
  void onViewShown() {
    SnackbarManager.getInstance().onShown(this.mManagerCallback);
    if (this.mCallbacks != null)
      for (int i = this.mCallbacks.size() - 1; i >= 0; i--)
        ((BaseCallback)this.mCallbacks.get(i)).onShown(this);  
  }
  
  @NonNull
  public B removeCallback(@NonNull BaseCallback<B> paramBaseCallback) {
    if (paramBaseCallback == null)
      return (B)this; 
    if (this.mCallbacks == null)
      return (B)this; 
    this.mCallbacks.remove(paramBaseCallback);
    return (B)this;
  }
  
  @NonNull
  public B setDuration(int paramInt) {
    this.mDuration = paramInt;
    return (B)this;
  }
  
  boolean shouldAnimate() { return this.mAccessibilityManager.isEnabled() ^ true; }
  
  public void show() { SnackbarManager.getInstance().show(this.mDuration, this.mManagerCallback); }
  
  final void showView() {
    if (this.mView.getParent() == null) {
      ViewGroup.LayoutParams layoutParams = this.mView.getLayoutParams();
      if (layoutParams instanceof CoordinatorLayout.LayoutParams) {
        CoordinatorLayout.LayoutParams layoutParams1 = (CoordinatorLayout.LayoutParams)layoutParams;
        Behavior behavior = new Behavior();
        behavior.setStartAlphaSwipeDistance(0.1F);
        behavior.setEndAlphaSwipeDistance(0.6F);
        behavior.setSwipeDirection(0);
        behavior.setListener(new SwipeDismissBehavior.OnDismissListener() {
              public void onDismiss(View param1View) {
                param1View.setVisibility(8);
                BaseTransientBottomBar.this.dispatchDismiss(0);
              }
              
              public void onDragStateChanged(int param1Int) {
                switch (param1Int) {
                  default:
                    return;
                  case 1:
                  case 2:
                    SnackbarManager.getInstance().pauseTimeout(BaseTransientBottomBar.this.mManagerCallback);
                    return;
                  case 0:
                    break;
                } 
                SnackbarManager.getInstance().restoreTimeoutIfPaused(BaseTransientBottomBar.this.mManagerCallback);
              }
            });
        layoutParams1.setBehavior(behavior);
        layoutParams1.insetEdge = 80;
      } 
      this.mTargetParent.addView(this.mView);
    } 
    this.mView.setOnAttachStateChangeListener(new OnAttachStateChangeListener() {
          public void onViewAttachedToWindow(View param1View) {}
          
          public void onViewDetachedFromWindow(View param1View) {
            if (BaseTransientBottomBar.this.isShownOrQueued())
              BaseTransientBottomBar.sHandler.post(new Runnable() {
                    public void run() { BaseTransientBottomBar.null.this.this$0.onViewHidden(3); }
                  }); 
          }
        });
    if (ViewCompat.isLaidOut(this.mView)) {
      if (shouldAnimate()) {
        animateViewIn();
        return;
      } 
      onViewShown();
      return;
    } 
    this.mView.setOnLayoutChangeListener(new OnLayoutChangeListener() {
          public void onLayoutChange(View param1View, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
            BaseTransientBottomBar.this.mView.setOnLayoutChangeListener(null);
            if (BaseTransientBottomBar.this.shouldAnimate()) {
              BaseTransientBottomBar.this.animateViewIn();
              return;
            } 
            BaseTransientBottomBar.this.onViewShown();
          }
        });
  }
  
  public static abstract class BaseCallback<B> extends Object {
    public static final int DISMISS_EVENT_ACTION = 1;
    
    public static final int DISMISS_EVENT_CONSECUTIVE = 4;
    
    public static final int DISMISS_EVENT_MANUAL = 3;
    
    public static final int DISMISS_EVENT_SWIPE = 0;
    
    public static final int DISMISS_EVENT_TIMEOUT = 2;
    
    public void onDismissed(B param1B, int param1Int) {}
    
    public void onShown(B param1B) {}
    
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public static @interface DismissEvent {}
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface DismissEvent {}
  
  final class Behavior extends SwipeDismissBehavior<SnackbarBaseLayout> {
    public boolean canSwipeDismissView(View param1View) { return param1View instanceof BaseTransientBottomBar.SnackbarBaseLayout; }
    
    public boolean onInterceptTouchEvent(CoordinatorLayout param1CoordinatorLayout, BaseTransientBottomBar.SnackbarBaseLayout param1SnackbarBaseLayout, MotionEvent param1MotionEvent) {
      int i = param1MotionEvent.getActionMasked();
      if (i != 3)
        switch (i) {
          default:
            return super.onInterceptTouchEvent(param1CoordinatorLayout, param1SnackbarBaseLayout, param1MotionEvent);
          case 0:
            if (param1CoordinatorLayout.isPointInChildBounds(param1SnackbarBaseLayout, (int)param1MotionEvent.getX(), (int)param1MotionEvent.getY()))
              SnackbarManager.getInstance().pauseTimeout(BaseTransientBottomBar.this.mManagerCallback); 
          case 1:
            break;
        }  
      SnackbarManager.getInstance().restoreTimeoutIfPaused(BaseTransientBottomBar.this.mManagerCallback);
    }
  }
  
  public static interface ContentViewCallback {
    void animateContentIn(int param1Int1, int param1Int2);
    
    void animateContentOut(int param1Int1, int param1Int2);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @IntRange(from = 1L)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface Duration {}
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  static interface OnAttachStateChangeListener {
    void onViewAttachedToWindow(View param1View);
    
    void onViewDetachedFromWindow(View param1View);
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  static interface OnLayoutChangeListener {
    void onLayoutChange(View param1View, int param1Int1, int param1Int2, int param1Int3, int param1Int4);
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  static class SnackbarBaseLayout extends FrameLayout {
    private BaseTransientBottomBar.OnAttachStateChangeListener mOnAttachStateChangeListener;
    
    private BaseTransientBottomBar.OnLayoutChangeListener mOnLayoutChangeListener;
    
    SnackbarBaseLayout(Context param1Context) { this(param1Context, null); }
    
    SnackbarBaseLayout(Context param1Context, AttributeSet param1AttributeSet) {
      super(param1Context, param1AttributeSet);
      TypedArray typedArray = param1Context.obtainStyledAttributes(param1AttributeSet, R.styleable.SnackbarLayout);
      if (typedArray.hasValue(R.styleable.SnackbarLayout_elevation))
        ViewCompat.setElevation(this, typedArray.getDimensionPixelSize(R.styleable.SnackbarLayout_elevation, 0)); 
      typedArray.recycle();
      setClickable(true);
    }
    
    protected void onAttachedToWindow() {
      super.onAttachedToWindow();
      if (this.mOnAttachStateChangeListener != null)
        this.mOnAttachStateChangeListener.onViewAttachedToWindow(this); 
      ViewCompat.requestApplyInsets(this);
    }
    
    protected void onDetachedFromWindow() {
      super.onDetachedFromWindow();
      if (this.mOnAttachStateChangeListener != null)
        this.mOnAttachStateChangeListener.onViewDetachedFromWindow(this); 
    }
    
    protected void onLayout(boolean param1Boolean, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      super.onLayout(param1Boolean, param1Int1, param1Int2, param1Int3, param1Int4);
      if (this.mOnLayoutChangeListener != null)
        this.mOnLayoutChangeListener.onLayoutChange(this, param1Int1, param1Int2, param1Int3, param1Int4); 
    }
    
    void setOnAttachStateChangeListener(BaseTransientBottomBar.OnAttachStateChangeListener param1OnAttachStateChangeListener) { this.mOnAttachStateChangeListener = param1OnAttachStateChangeListener; }
    
    void setOnLayoutChangeListener(BaseTransientBottomBar.OnLayoutChangeListener param1OnLayoutChangeListener) { this.mOnLayoutChangeListener = param1OnLayoutChangeListener; }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/design/widget/BaseTransientBottomBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */