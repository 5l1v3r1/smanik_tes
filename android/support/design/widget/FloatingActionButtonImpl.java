package android.support.design.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.R;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;

@RequiresApi(14)
class FloatingActionButtonImpl {
  static final Interpolator ANIM_INTERPOLATOR = AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR;
  
  static final int ANIM_STATE_HIDING = 1;
  
  static final int ANIM_STATE_NONE = 0;
  
  static final int ANIM_STATE_SHOWING = 2;
  
  static final int[] EMPTY_STATE_SET;
  
  static final int[] ENABLED_STATE_SET;
  
  static final int[] FOCUSED_ENABLED_STATE_SET;
  
  static final long PRESSED_ANIM_DELAY = 100L;
  
  static final long PRESSED_ANIM_DURATION = 100L;
  
  static final int[] PRESSED_ENABLED_STATE_SET = { 16842919, 16842910 };
  
  static final int SHOW_HIDE_ANIM_DURATION = 200;
  
  int mAnimState = 0;
  
  CircularBorderDrawable mBorderDrawable;
  
  Drawable mContentBackground;
  
  float mElevation;
  
  private ViewTreeObserver.OnPreDrawListener mPreDrawListener;
  
  float mPressedTranslationZ;
  
  Drawable mRippleDrawable;
  
  private float mRotation;
  
  ShadowDrawableWrapper mShadowDrawable;
  
  final ShadowViewDelegate mShadowViewDelegate;
  
  Drawable mShapeDrawable;
  
  private final StateListAnimator mStateListAnimator;
  
  private final Rect mTmpRect = new Rect();
  
  final VisibilityAwareImageButton mView;
  
  static  {
    FOCUSED_ENABLED_STATE_SET = new int[] { 16842908, 16842910 };
    ENABLED_STATE_SET = new int[] { 16842910 };
    EMPTY_STATE_SET = new int[0];
  }
  
  FloatingActionButtonImpl(VisibilityAwareImageButton paramVisibilityAwareImageButton, ShadowViewDelegate paramShadowViewDelegate) {
    this.mView = paramVisibilityAwareImageButton;
    this.mShadowViewDelegate = paramShadowViewDelegate;
    this.mStateListAnimator = new StateListAnimator();
    this.mStateListAnimator.addState(PRESSED_ENABLED_STATE_SET, createAnimator(new ElevateToTranslationZAnimation()));
    this.mStateListAnimator.addState(FOCUSED_ENABLED_STATE_SET, createAnimator(new ElevateToTranslationZAnimation()));
    this.mStateListAnimator.addState(ENABLED_STATE_SET, createAnimator(new ResetElevationAnimation()));
    this.mStateListAnimator.addState(EMPTY_STATE_SET, createAnimator(new DisabledElevationAnimation()));
    this.mRotation = this.mView.getRotation();
  }
  
  private ValueAnimator createAnimator(@NonNull ShadowAnimatorImpl paramShadowAnimatorImpl) {
    ValueAnimator valueAnimator = new ValueAnimator();
    valueAnimator.setInterpolator(ANIM_INTERPOLATOR);
    valueAnimator.setDuration(100L);
    valueAnimator.addListener(paramShadowAnimatorImpl);
    valueAnimator.addUpdateListener(paramShadowAnimatorImpl);
    valueAnimator.setFloatValues(new float[] { 0.0F, 1.0F });
    return valueAnimator;
  }
  
  private static ColorStateList createColorStateList(int paramInt) { return new ColorStateList(new int[][] { FOCUSED_ENABLED_STATE_SET, PRESSED_ENABLED_STATE_SET, {} }, new int[] { paramInt, paramInt, 0 }); }
  
  private void ensurePreDrawListener() {
    if (this.mPreDrawListener == null)
      this.mPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
          public boolean onPreDraw() {
            FloatingActionButtonImpl.this.onPreDraw();
            return true;
          }
        }; 
  }
  
  private boolean shouldAnimateVisibilityChange() { return (ViewCompat.isLaidOut(this.mView) && !this.mView.isInEditMode()); }
  
  private void updateFromViewRotation() {
    if (Build.VERSION.SDK_INT == 19)
      if (this.mRotation % 90.0F != 0.0F) {
        if (this.mView.getLayerType() != 1)
          this.mView.setLayerType(1, null); 
      } else if (this.mView.getLayerType() != 0) {
        this.mView.setLayerType(0, null);
      }  
    if (this.mShadowDrawable != null)
      this.mShadowDrawable.setRotation(-this.mRotation); 
    if (this.mBorderDrawable != null)
      this.mBorderDrawable.setRotation(-this.mRotation); 
  }
  
  CircularBorderDrawable createBorderDrawable(int paramInt, ColorStateList paramColorStateList) {
    Context context = this.mView.getContext();
    CircularBorderDrawable circularBorderDrawable = newCircularDrawable();
    circularBorderDrawable.setGradientColors(ContextCompat.getColor(context, R.color.design_fab_stroke_top_outer_color), ContextCompat.getColor(context, R.color.design_fab_stroke_top_inner_color), ContextCompat.getColor(context, R.color.design_fab_stroke_end_inner_color), ContextCompat.getColor(context, R.color.design_fab_stroke_end_outer_color));
    circularBorderDrawable.setBorderWidth(paramInt);
    circularBorderDrawable.setBorderTint(paramColorStateList);
    return circularBorderDrawable;
  }
  
  GradientDrawable createShapeDrawable() {
    GradientDrawable gradientDrawable = newGradientDrawableForShape();
    gradientDrawable.setShape(1);
    gradientDrawable.setColor(-1);
    return gradientDrawable;
  }
  
  final Drawable getContentBackground() { return this.mContentBackground; }
  
  float getElevation() { return this.mElevation; }
  
  void getPadding(Rect paramRect) { this.mShadowDrawable.getPadding(paramRect); }
  
  void hide(@Nullable final InternalVisibilityChangedListener listener, final boolean fromUser) {
    byte b;
    if (isOrWillBeHidden())
      return; 
    this.mView.animate().cancel();
    if (shouldAnimateVisibilityChange()) {
      this.mAnimState = 1;
      this.mView.animate().scaleX(0.0F).scaleY(0.0F).alpha(0.0F).setDuration(200L).setInterpolator(AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR).setListener(new AnimatorListenerAdapter() {
            private boolean mCancelled;
            
            public void onAnimationCancel(Animator param1Animator) { this.mCancelled = true; }
            
            public void onAnimationEnd(Animator param1Animator) {
              FloatingActionButtonImpl.this.mAnimState = 0;
              if (!this.mCancelled) {
                byte b;
                VisibilityAwareImageButton visibilityAwareImageButton = FloatingActionButtonImpl.this.mView;
                if (fromUser) {
                  b = 8;
                } else {
                  b = 4;
                } 
                visibilityAwareImageButton.internalSetVisibility(b, fromUser);
                if (listener != null)
                  listener.onHidden(); 
              } 
            }
            
            public void onAnimationStart(Animator param1Animator) {
              FloatingActionButtonImpl.this.mView.internalSetVisibility(0, fromUser);
              this.mCancelled = false;
            }
          });
      return;
    } 
    VisibilityAwareImageButton visibilityAwareImageButton = this.mView;
    if (paramBoolean) {
      b = 8;
    } else {
      b = 4;
    } 
    visibilityAwareImageButton.internalSetVisibility(b, paramBoolean);
    if (paramInternalVisibilityChangedListener != null)
      paramInternalVisibilityChangedListener.onHidden(); 
  }
  
  boolean isOrWillBeHidden() {
    int i = this.mView.getVisibility();
    boolean bool1 = false;
    boolean bool = false;
    if (i == 0) {
      if (this.mAnimState == 1)
        bool = true; 
      return bool;
    } 
    bool = bool1;
    if (this.mAnimState != 2)
      bool = true; 
    return bool;
  }
  
  boolean isOrWillBeShown() {
    int i = this.mView.getVisibility();
    boolean bool1 = false;
    boolean bool = false;
    if (i != 0) {
      if (this.mAnimState == 2)
        bool = true; 
      return bool;
    } 
    bool = bool1;
    if (this.mAnimState != 1)
      bool = true; 
    return bool;
  }
  
  void jumpDrawableToCurrentState() { this.mStateListAnimator.jumpToCurrentState(); }
  
  CircularBorderDrawable newCircularDrawable() { return new CircularBorderDrawable(); }
  
  GradientDrawable newGradientDrawableForShape() { return new GradientDrawable(); }
  
  void onAttachedToWindow() {
    if (requirePreDrawListener()) {
      ensurePreDrawListener();
      this.mView.getViewTreeObserver().addOnPreDrawListener(this.mPreDrawListener);
    } 
  }
  
  void onCompatShadowChanged() {}
  
  void onDetachedFromWindow() {
    if (this.mPreDrawListener != null) {
      this.mView.getViewTreeObserver().removeOnPreDrawListener(this.mPreDrawListener);
      this.mPreDrawListener = null;
    } 
  }
  
  void onDrawableStateChanged(int[] paramArrayOfInt) { this.mStateListAnimator.setState(paramArrayOfInt); }
  
  void onElevationsChanged(float paramFloat1, float paramFloat2) {
    if (this.mShadowDrawable != null) {
      this.mShadowDrawable.setShadowSize(paramFloat1, this.mPressedTranslationZ + paramFloat1);
      updatePadding();
    } 
  }
  
  void onPaddingUpdated(Rect paramRect) {}
  
  void onPreDraw() {
    float f = this.mView.getRotation();
    if (this.mRotation != f) {
      this.mRotation = f;
      updateFromViewRotation();
    } 
  }
  
  boolean requirePreDrawListener() { return true; }
  
  void setBackgroundDrawable(ColorStateList paramColorStateList, PorterDuff.Mode paramMode, int paramInt1, int paramInt2) {
    Drawable[] arrayOfDrawable;
    this.mShapeDrawable = DrawableCompat.wrap(createShapeDrawable());
    DrawableCompat.setTintList(this.mShapeDrawable, paramColorStateList);
    if (paramMode != null)
      DrawableCompat.setTintMode(this.mShapeDrawable, paramMode); 
    this.mRippleDrawable = DrawableCompat.wrap(createShapeDrawable());
    DrawableCompat.setTintList(this.mRippleDrawable, createColorStateList(paramInt1));
    if (paramInt2 > 0) {
      this.mBorderDrawable = createBorderDrawable(paramInt2, paramColorStateList);
      arrayOfDrawable = new Drawable[3];
      arrayOfDrawable[0] = this.mBorderDrawable;
      arrayOfDrawable[1] = this.mShapeDrawable;
      arrayOfDrawable[2] = this.mRippleDrawable;
    } else {
      this.mBorderDrawable = null;
      arrayOfDrawable = new Drawable[2];
      arrayOfDrawable[0] = this.mShapeDrawable;
      arrayOfDrawable[1] = this.mRippleDrawable;
    } 
    this.mContentBackground = new LayerDrawable(arrayOfDrawable);
    this.mShadowDrawable = new ShadowDrawableWrapper(this.mView.getContext(), this.mContentBackground, this.mShadowViewDelegate.getRadius(), this.mElevation, this.mElevation + this.mPressedTranslationZ);
    this.mShadowDrawable.setAddPaddingForCorners(false);
    this.mShadowViewDelegate.setBackgroundDrawable(this.mShadowDrawable);
  }
  
  void setBackgroundTintList(ColorStateList paramColorStateList) {
    if (this.mShapeDrawable != null)
      DrawableCompat.setTintList(this.mShapeDrawable, paramColorStateList); 
    if (this.mBorderDrawable != null)
      this.mBorderDrawable.setBorderTint(paramColorStateList); 
  }
  
  void setBackgroundTintMode(PorterDuff.Mode paramMode) {
    if (this.mShapeDrawable != null)
      DrawableCompat.setTintMode(this.mShapeDrawable, paramMode); 
  }
  
  final void setElevation(float paramFloat) {
    if (this.mElevation != paramFloat) {
      this.mElevation = paramFloat;
      onElevationsChanged(paramFloat, this.mPressedTranslationZ);
    } 
  }
  
  final void setPressedTranslationZ(float paramFloat) {
    if (this.mPressedTranslationZ != paramFloat) {
      this.mPressedTranslationZ = paramFloat;
      onElevationsChanged(this.mElevation, paramFloat);
    } 
  }
  
  void setRippleColor(int paramInt) {
    if (this.mRippleDrawable != null)
      DrawableCompat.setTintList(this.mRippleDrawable, createColorStateList(paramInt)); 
  }
  
  void show(@Nullable final InternalVisibilityChangedListener listener, final boolean fromUser) {
    if (isOrWillBeShown())
      return; 
    this.mView.animate().cancel();
    if (shouldAnimateVisibilityChange()) {
      this.mAnimState = 2;
      if (this.mView.getVisibility() != 0) {
        this.mView.setAlpha(0.0F);
        this.mView.setScaleY(0.0F);
        this.mView.setScaleX(0.0F);
      } 
      this.mView.animate().scaleX(1.0F).scaleY(1.0F).alpha(1.0F).setDuration(200L).setInterpolator(AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR).setListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator param1Animator) {
              FloatingActionButtonImpl.this.mAnimState = 0;
              if (listener != null)
                listener.onShown(); 
            }
            
            public void onAnimationStart(Animator param1Animator) { FloatingActionButtonImpl.this.mView.internalSetVisibility(0, fromUser); }
          });
      return;
    } 
    this.mView.internalSetVisibility(0, paramBoolean);
    this.mView.setAlpha(1.0F);
    this.mView.setScaleY(1.0F);
    this.mView.setScaleX(1.0F);
    if (paramInternalVisibilityChangedListener != null)
      paramInternalVisibilityChangedListener.onShown(); 
  }
  
  final void updatePadding() {
    Rect rect = this.mTmpRect;
    getPadding(rect);
    onPaddingUpdated(rect);
    this.mShadowViewDelegate.setShadowPadding(rect.left, rect.top, rect.right, rect.bottom);
  }
  
  private class DisabledElevationAnimation extends ShadowAnimatorImpl {
    DisabledElevationAnimation() { super(FloatingActionButtonImpl.this, null); }
    
    protected float getTargetShadowSize() { return 0.0F; }
  }
  
  private class ElevateToTranslationZAnimation extends ShadowAnimatorImpl {
    ElevateToTranslationZAnimation() { super(FloatingActionButtonImpl.this, null); }
    
    protected float getTargetShadowSize() { return FloatingActionButtonImpl.this.mElevation + FloatingActionButtonImpl.this.mPressedTranslationZ; }
  }
  
  static interface InternalVisibilityChangedListener {
    void onHidden();
    
    void onShown();
  }
  
  private class ResetElevationAnimation extends ShadowAnimatorImpl {
    ResetElevationAnimation() { super(FloatingActionButtonImpl.this, null); }
    
    protected float getTargetShadowSize() { return FloatingActionButtonImpl.this.mElevation; }
  }
  
  private abstract class ShadowAnimatorImpl extends AnimatorListenerAdapter implements ValueAnimator.AnimatorUpdateListener {
    private float mShadowSizeEnd;
    
    private float mShadowSizeStart;
    
    private boolean mValidValues;
    
    private ShadowAnimatorImpl() {}
    
    protected abstract float getTargetShadowSize();
    
    public void onAnimationEnd(Animator param1Animator) {
      FloatingActionButtonImpl.this.mShadowDrawable.setShadowSize(this.mShadowSizeEnd);
      this.mValidValues = false;
    }
    
    public void onAnimationUpdate(ValueAnimator param1ValueAnimator) {
      if (!this.mValidValues) {
        this.mShadowSizeStart = FloatingActionButtonImpl.this.mShadowDrawable.getShadowSize();
        this.mShadowSizeEnd = getTargetShadowSize();
        this.mValidValues = true;
      } 
      FloatingActionButtonImpl.this.mShadowDrawable.setShadowSize(this.mShadowSizeStart + (this.mShadowSizeEnd - this.mShadowSizeStart) * param1ValueAnimator.getAnimatedFraction());
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/design/widget/FloatingActionButtonImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */