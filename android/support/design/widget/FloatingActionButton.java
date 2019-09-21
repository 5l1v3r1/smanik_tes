package android.support.design.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.support.design.R;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewGroupUtils;
import android.support.v7.widget.AppCompatImageHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

@DefaultBehavior(FloatingActionButton.Behavior.class)
public class FloatingActionButton extends VisibilityAwareImageButton {
  private static final int AUTO_MINI_LARGEST_SCREEN_WIDTH = 470;
  
  private static final String LOG_TAG = "FloatingActionButton";
  
  public static final int NO_CUSTOM_SIZE = 0;
  
  public static final int SIZE_AUTO = -1;
  
  public static final int SIZE_MINI = 1;
  
  public static final int SIZE_NORMAL = 0;
  
  private ColorStateList mBackgroundTint;
  
  private PorterDuff.Mode mBackgroundTintMode;
  
  private int mBorderWidth;
  
  boolean mCompatPadding;
  
  private int mCustomSize;
  
  private AppCompatImageHelper mImageHelper;
  
  int mImagePadding;
  
  private FloatingActionButtonImpl mImpl;
  
  private int mMaxImageSize;
  
  private int mRippleColor;
  
  final Rect mShadowPadding = new Rect();
  
  private int mSize;
  
  private final Rect mTouchArea = new Rect();
  
  public FloatingActionButton(Context paramContext) { this(paramContext, null); }
  
  public FloatingActionButton(Context paramContext, AttributeSet paramAttributeSet) { this(paramContext, paramAttributeSet, 0); }
  
  public FloatingActionButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
    super(paramContext, paramAttributeSet, paramInt);
    ThemeUtils.checkAppCompatTheme(paramContext);
    TypedArray typedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.FloatingActionButton, paramInt, R.style.Widget_Design_FloatingActionButton);
    this.mBackgroundTint = typedArray.getColorStateList(R.styleable.FloatingActionButton_backgroundTint);
    this.mBackgroundTintMode = ViewUtils.parseTintMode(typedArray.getInt(R.styleable.FloatingActionButton_backgroundTintMode, -1), null);
    this.mRippleColor = typedArray.getColor(R.styleable.FloatingActionButton_rippleColor, 0);
    this.mSize = typedArray.getInt(R.styleable.FloatingActionButton_fabSize, -1);
    this.mCustomSize = typedArray.getDimensionPixelSize(R.styleable.FloatingActionButton_fabCustomSize, 0);
    this.mBorderWidth = typedArray.getDimensionPixelSize(R.styleable.FloatingActionButton_borderWidth, 0);
    float f1 = typedArray.getDimension(R.styleable.FloatingActionButton_elevation, 0.0F);
    float f2 = typedArray.getDimension(R.styleable.FloatingActionButton_pressedTranslationZ, 0.0F);
    this.mCompatPadding = typedArray.getBoolean(R.styleable.FloatingActionButton_useCompatPadding, false);
    typedArray.recycle();
    this.mImageHelper = new AppCompatImageHelper(this);
    this.mImageHelper.loadFromAttributes(paramAttributeSet, paramInt);
    this.mMaxImageSize = (int)getResources().getDimension(R.dimen.design_fab_image_size);
    getImpl().setBackgroundDrawable(this.mBackgroundTint, this.mBackgroundTintMode, this.mRippleColor, this.mBorderWidth);
    getImpl().setElevation(f1);
    getImpl().setPressedTranslationZ(f2);
  }
  
  private FloatingActionButtonImpl createImpl() { return (Build.VERSION.SDK_INT >= 21) ? new FloatingActionButtonLollipop(this, new ShadowDelegateImpl(this)) : new FloatingActionButtonImpl(this, new ShadowDelegateImpl(this)); }
  
  private FloatingActionButtonImpl getImpl() {
    if (this.mImpl == null)
      this.mImpl = createImpl(); 
    return this.mImpl;
  }
  
  private int getSizeDimension(int paramInt) {
    Resources resources = getResources();
    return (this.mCustomSize != 0) ? this.mCustomSize : ((paramInt != -1) ? ((paramInt != 1) ? resources.getDimensionPixelSize(R.dimen.design_fab_size_normal) : resources.getDimensionPixelSize(R.dimen.design_fab_size_mini)) : ((Math.max((resources.getConfiguration()).screenWidthDp, (resources.getConfiguration()).screenHeightDp) < 470) ? getSizeDimension(1) : getSizeDimension(0)));
  }
  
  private static int resolveAdjustedSize(int paramInt1, int paramInt2) {
    int i = View.MeasureSpec.getMode(paramInt2);
    int j = View.MeasureSpec.getSize(paramInt2);
    if (i != Integer.MIN_VALUE) {
      paramInt2 = paramInt1;
      if (i != 0)
        return (i != 1073741824) ? paramInt1 : j; 
    } else {
      paramInt2 = Math.min(paramInt1, j);
    } 
    return paramInt2;
  }
  
  @Nullable
  private FloatingActionButtonImpl.InternalVisibilityChangedListener wrapOnVisibilityChangedListener(@Nullable final OnVisibilityChangedListener listener) { return (paramOnVisibilityChangedListener == null) ? null : new FloatingActionButtonImpl.InternalVisibilityChangedListener() {
        public void onHidden() { listener.onHidden(FloatingActionButton.this); }
        
        public void onShown() { listener.onShown(FloatingActionButton.this); }
      }; }
  
  protected void drawableStateChanged() {
    super.drawableStateChanged();
    getImpl().onDrawableStateChanged(getDrawableState());
  }
  
  @Nullable
  public ColorStateList getBackgroundTintList() { return this.mBackgroundTint; }
  
  @Nullable
  public PorterDuff.Mode getBackgroundTintMode() { return this.mBackgroundTintMode; }
  
  public float getCompatElevation() { return getImpl().getElevation(); }
  
  @NonNull
  public Drawable getContentBackground() { return getImpl().getContentBackground(); }
  
  public boolean getContentRect(@NonNull Rect paramRect) {
    if (ViewCompat.isLaidOut(this)) {
      paramRect.set(0, 0, getWidth(), getHeight());
      paramRect.left += this.mShadowPadding.left;
      paramRect.top += this.mShadowPadding.top;
      paramRect.right -= this.mShadowPadding.right;
      paramRect.bottom -= this.mShadowPadding.bottom;
      return true;
    } 
    return false;
  }
  
  public int getCustomSize() { return this.mCustomSize; }
  
  @ColorInt
  public int getRippleColor() { return this.mRippleColor; }
  
  public int getSize() { return this.mSize; }
  
  int getSizeDimension() { return getSizeDimension(this.mSize); }
  
  public boolean getUseCompatPadding() { return this.mCompatPadding; }
  
  public void hide() { hide(null); }
  
  public void hide(@Nullable OnVisibilityChangedListener paramOnVisibilityChangedListener) { hide(paramOnVisibilityChangedListener, true); }
  
  void hide(@Nullable OnVisibilityChangedListener paramOnVisibilityChangedListener, boolean paramBoolean) { getImpl().hide(wrapOnVisibilityChangedListener(paramOnVisibilityChangedListener), paramBoolean); }
  
  public void jumpDrawablesToCurrentState() {
    super.jumpDrawablesToCurrentState();
    getImpl().jumpDrawableToCurrentState();
  }
  
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    getImpl().onAttachedToWindow();
  }
  
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    getImpl().onDetachedFromWindow();
  }
  
  protected void onMeasure(int paramInt1, int paramInt2) {
    int i = getSizeDimension();
    this.mImagePadding = (i - this.mMaxImageSize) / 2;
    getImpl().updatePadding();
    paramInt1 = Math.min(resolveAdjustedSize(i, paramInt1), resolveAdjustedSize(i, paramInt2));
    setMeasuredDimension(this.mShadowPadding.left + paramInt1 + this.mShadowPadding.right, paramInt1 + this.mShadowPadding.top + this.mShadowPadding.bottom);
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent) { return (paramMotionEvent.getAction() == 0 && getContentRect(this.mTouchArea) && !this.mTouchArea.contains((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY())) ? false : super.onTouchEvent(paramMotionEvent); }
  
  public void setBackgroundColor(int paramInt) { Log.i("FloatingActionButton", "Setting a custom background is not supported."); }
  
  public void setBackgroundDrawable(Drawable paramDrawable) { Log.i("FloatingActionButton", "Setting a custom background is not supported."); }
  
  public void setBackgroundResource(int paramInt) { Log.i("FloatingActionButton", "Setting a custom background is not supported."); }
  
  public void setBackgroundTintList(@Nullable ColorStateList paramColorStateList) {
    if (this.mBackgroundTint != paramColorStateList) {
      this.mBackgroundTint = paramColorStateList;
      getImpl().setBackgroundTintList(paramColorStateList);
    } 
  }
  
  public void setBackgroundTintMode(@Nullable PorterDuff.Mode paramMode) {
    if (this.mBackgroundTintMode != paramMode) {
      this.mBackgroundTintMode = paramMode;
      getImpl().setBackgroundTintMode(paramMode);
    } 
  }
  
  public void setCompatElevation(float paramFloat) { getImpl().setElevation(paramFloat); }
  
  public void setCustomSize(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("Custom size should be non-negative."); 
    this.mCustomSize = paramInt;
  }
  
  public void setImageResource(@DrawableRes int paramInt) { this.mImageHelper.setImageResource(paramInt); }
  
  public void setRippleColor(@ColorInt int paramInt) {
    if (this.mRippleColor != paramInt) {
      this.mRippleColor = paramInt;
      getImpl().setRippleColor(paramInt);
    } 
  }
  
  public void setSize(int paramInt) {
    if (paramInt != this.mSize) {
      this.mSize = paramInt;
      requestLayout();
    } 
  }
  
  public void setUseCompatPadding(boolean paramBoolean) {
    if (this.mCompatPadding != paramBoolean) {
      this.mCompatPadding = paramBoolean;
      getImpl().onCompatShadowChanged();
    } 
  }
  
  public void show() { show(null); }
  
  public void show(@Nullable OnVisibilityChangedListener paramOnVisibilityChangedListener) { show(paramOnVisibilityChangedListener, true); }
  
  void show(OnVisibilityChangedListener paramOnVisibilityChangedListener, boolean paramBoolean) { getImpl().show(wrapOnVisibilityChangedListener(paramOnVisibilityChangedListener), paramBoolean); }
  
  public static class Behavior extends CoordinatorLayout.Behavior<FloatingActionButton> {
    private static final boolean AUTO_HIDE_DEFAULT = true;
    
    private boolean mAutoHideEnabled;
    
    private FloatingActionButton.OnVisibilityChangedListener mInternalAutoHideListener;
    
    private Rect mTmpRect;
    
    public Behavior() { this.mAutoHideEnabled = true; }
    
    public Behavior(Context param1Context, AttributeSet param1AttributeSet) {
      super(param1Context, param1AttributeSet);
      TypedArray typedArray = param1Context.obtainStyledAttributes(param1AttributeSet, R.styleable.FloatingActionButton_Behavior_Layout);
      this.mAutoHideEnabled = typedArray.getBoolean(R.styleable.FloatingActionButton_Behavior_Layout_behavior_autoHide, true);
      typedArray.recycle();
    }
    
    private static boolean isBottomSheet(@NonNull View param1View) {
      ViewGroup.LayoutParams layoutParams = param1View.getLayoutParams();
      return (layoutParams instanceof CoordinatorLayout.LayoutParams) ? (((CoordinatorLayout.LayoutParams)layoutParams).getBehavior() instanceof BottomSheetBehavior) : 0;
    }
    
    private void offsetIfNeeded(CoordinatorLayout param1CoordinatorLayout, FloatingActionButton param1FloatingActionButton) {
      Rect rect = param1FloatingActionButton.mShadowPadding;
      if (rect != null && rect.centerX() > 0 && rect.centerY() > 0) {
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams)param1FloatingActionButton.getLayoutParams();
        int i = param1FloatingActionButton.getRight();
        int k = param1CoordinatorLayout.getWidth();
        int m = layoutParams.rightMargin;
        int j = 0;
        if (i >= k - m) {
          i = rect.right;
        } else if (param1FloatingActionButton.getLeft() <= layoutParams.leftMargin) {
          i = -rect.left;
        } else {
          i = 0;
        } 
        if (param1FloatingActionButton.getBottom() >= param1CoordinatorLayout.getHeight() - layoutParams.bottomMargin) {
          j = rect.bottom;
        } else if (param1FloatingActionButton.getTop() <= layoutParams.topMargin) {
          j = -rect.top;
        } 
        if (j != 0)
          ViewCompat.offsetTopAndBottom(param1FloatingActionButton, j); 
        if (i != 0)
          ViewCompat.offsetLeftAndRight(param1FloatingActionButton, i); 
      } 
    }
    
    private boolean shouldUpdateVisibility(View param1View, FloatingActionButton param1FloatingActionButton) {
      CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams)param1FloatingActionButton.getLayoutParams();
      return !this.mAutoHideEnabled ? false : ((layoutParams.getAnchorId() != param1View.getId()) ? false : (!(param1FloatingActionButton.getUserSetVisibility() != 0)));
    }
    
    private boolean updateFabVisibilityForAppBarLayout(CoordinatorLayout param1CoordinatorLayout, AppBarLayout param1AppBarLayout, FloatingActionButton param1FloatingActionButton) {
      if (!shouldUpdateVisibility(param1AppBarLayout, param1FloatingActionButton))
        return false; 
      if (this.mTmpRect == null)
        this.mTmpRect = new Rect(); 
      Rect rect = this.mTmpRect;
      ViewGroupUtils.getDescendantRect(param1CoordinatorLayout, param1AppBarLayout, rect);
      if (rect.bottom <= param1AppBarLayout.getMinimumHeightForVisibleOverlappingContent()) {
        param1FloatingActionButton.hide(this.mInternalAutoHideListener, false);
      } else {
        param1FloatingActionButton.show(this.mInternalAutoHideListener, false);
      } 
      return true;
    }
    
    private boolean updateFabVisibilityForBottomSheet(View param1View, FloatingActionButton param1FloatingActionButton) {
      if (!shouldUpdateVisibility(param1View, param1FloatingActionButton))
        return false; 
      CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams)param1FloatingActionButton.getLayoutParams();
      if (param1View.getTop() < param1FloatingActionButton.getHeight() / 2 + layoutParams.topMargin) {
        param1FloatingActionButton.hide(this.mInternalAutoHideListener, false);
      } else {
        param1FloatingActionButton.show(this.mInternalAutoHideListener, false);
      } 
      return true;
    }
    
    public boolean getInsetDodgeRect(@NonNull CoordinatorLayout param1CoordinatorLayout, @NonNull FloatingActionButton param1FloatingActionButton, @NonNull Rect param1Rect) {
      Rect rect = param1FloatingActionButton.mShadowPadding;
      param1Rect.set(param1FloatingActionButton.getLeft() + rect.left, param1FloatingActionButton.getTop() + rect.top, param1FloatingActionButton.getRight() - rect.right, param1FloatingActionButton.getBottom() - rect.bottom);
      return true;
    }
    
    public boolean isAutoHideEnabled() { return this.mAutoHideEnabled; }
    
    public void onAttachedToLayoutParams(@NonNull CoordinatorLayout.LayoutParams param1LayoutParams) {
      if (param1LayoutParams.dodgeInsetEdges == 0)
        param1LayoutParams.dodgeInsetEdges = 80; 
    }
    
    public boolean onDependentViewChanged(CoordinatorLayout param1CoordinatorLayout, FloatingActionButton param1FloatingActionButton, View param1View) {
      if (param1View instanceof AppBarLayout) {
        updateFabVisibilityForAppBarLayout(param1CoordinatorLayout, (AppBarLayout)param1View, param1FloatingActionButton);
      } else if (isBottomSheet(param1View)) {
        updateFabVisibilityForBottomSheet(param1View, param1FloatingActionButton);
      } 
      return false;
    }
    
    public boolean onLayoutChild(CoordinatorLayout param1CoordinatorLayout, FloatingActionButton param1FloatingActionButton, int param1Int) {
      List list = param1CoordinatorLayout.getDependencies(param1FloatingActionButton);
      int i = list.size();
      byte b;
      for (b = 0; b < i; b++) {
        View view = (View)list.get(b);
        if ((view instanceof AppBarLayout) ? updateFabVisibilityForAppBarLayout(param1CoordinatorLayout, (AppBarLayout)view, param1FloatingActionButton) : (isBottomSheet(view) && updateFabVisibilityForBottomSheet(view, param1FloatingActionButton)))
          break; 
      } 
      param1CoordinatorLayout.onLayoutChild(param1FloatingActionButton, param1Int);
      offsetIfNeeded(param1CoordinatorLayout, param1FloatingActionButton);
      return true;
    }
    
    public void setAutoHideEnabled(boolean param1Boolean) { this.mAutoHideEnabled = param1Boolean; }
    
    @VisibleForTesting
    void setInternalAutoHideListener(FloatingActionButton.OnVisibilityChangedListener param1OnVisibilityChangedListener) { this.mInternalAutoHideListener = param1OnVisibilityChangedListener; }
  }
  
  public static abstract class OnVisibilityChangedListener {
    public void onHidden(FloatingActionButton param1FloatingActionButton) {}
    
    public void onShown(FloatingActionButton param1FloatingActionButton) {}
  }
  
  private class ShadowDelegateImpl implements ShadowViewDelegate {
    public float getRadius() { return FloatingActionButton.this.getSizeDimension() / 2.0F; }
    
    public boolean isCompatPaddingEnabled() { return FloatingActionButton.this.mCompatPadding; }
    
    public void setBackgroundDrawable(Drawable param1Drawable) { FloatingActionButton.this.setBackgroundDrawable(param1Drawable); }
    
    public void setShadowPadding(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      FloatingActionButton.this.mShadowPadding.set(param1Int1, param1Int2, param1Int3, param1Int4);
      FloatingActionButton.this.setPadding(param1Int1 + FloatingActionButton.this.mImagePadding, param1Int2 + FloatingActionButton.this.mImagePadding, param1Int3 + FloatingActionButton.this.mImagePadding, param1Int4 + FloatingActionButton.this.mImagePadding);
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface Size {}
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/design/widget/FloatingActionButton.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */