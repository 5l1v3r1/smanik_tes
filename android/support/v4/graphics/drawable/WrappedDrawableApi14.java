package android.support.v4.graphics.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

class WrappedDrawableApi14 extends Drawable implements Drawable.Callback, WrappedDrawable, TintAwareDrawable {
  static final PorterDuff.Mode DEFAULT_TINT_MODE = PorterDuff.Mode.SRC_IN;
  
  private boolean mColorFilterSet;
  
  private int mCurrentColor;
  
  private PorterDuff.Mode mCurrentMode;
  
  Drawable mDrawable;
  
  private boolean mMutated;
  
  DrawableWrapperState mState = mutateConstantState();
  
  WrappedDrawableApi14(@Nullable Drawable paramDrawable) { setWrappedDrawable(paramDrawable); }
  
  WrappedDrawableApi14(@NonNull DrawableWrapperState paramDrawableWrapperState, @Nullable Resources paramResources) { updateLocalState(paramResources); }
  
  private void updateLocalState(@Nullable Resources paramResources) {
    if (this.mState != null && this.mState.mDrawableState != null)
      setWrappedDrawable(this.mState.mDrawableState.newDrawable(paramResources)); 
  }
  
  private boolean updateTint(int[] paramArrayOfInt) {
    if (!isCompatTintEnabled())
      return false; 
    ColorStateList colorStateList = this.mState.mTint;
    PorterDuff.Mode mode = this.mState.mTintMode;
    if (colorStateList != null && mode != null) {
      int i = colorStateList.getColorForState(paramArrayOfInt, colorStateList.getDefaultColor());
      if (!this.mColorFilterSet || i != this.mCurrentColor || mode != this.mCurrentMode) {
        setColorFilter(i, mode);
        this.mCurrentColor = i;
        this.mCurrentMode = mode;
        this.mColorFilterSet = true;
        return true;
      } 
    } else {
      this.mColorFilterSet = false;
      clearColorFilter();
    } 
    return false;
  }
  
  public void draw(@NonNull Canvas paramCanvas) { this.mDrawable.draw(paramCanvas); }
  
  public int getChangingConfigurations() {
    int i;
    int j = super.getChangingConfigurations();
    if (this.mState != null) {
      i = this.mState.getChangingConfigurations();
    } else {
      i = 0;
    } 
    return j | i | this.mDrawable.getChangingConfigurations();
  }
  
  @Nullable
  public Drawable.ConstantState getConstantState() {
    if (this.mState != null && this.mState.canConstantState()) {
      this.mState.mChangingConfigurations = getChangingConfigurations();
      return this.mState;
    } 
    return null;
  }
  
  @NonNull
  public Drawable getCurrent() { return this.mDrawable.getCurrent(); }
  
  public int getIntrinsicHeight() { return this.mDrawable.getIntrinsicHeight(); }
  
  public int getIntrinsicWidth() { return this.mDrawable.getIntrinsicWidth(); }
  
  public int getMinimumHeight() { return this.mDrawable.getMinimumHeight(); }
  
  public int getMinimumWidth() { return this.mDrawable.getMinimumWidth(); }
  
  public int getOpacity() { return this.mDrawable.getOpacity(); }
  
  public boolean getPadding(@NonNull Rect paramRect) { return this.mDrawable.getPadding(paramRect); }
  
  @NonNull
  public int[] getState() { return this.mDrawable.getState(); }
  
  public Region getTransparentRegion() { return this.mDrawable.getTransparentRegion(); }
  
  public final Drawable getWrappedDrawable() { return this.mDrawable; }
  
  public void invalidateDrawable(@NonNull Drawable paramDrawable) { invalidateSelf(); }
  
  protected boolean isCompatTintEnabled() { return true; }
  
  public boolean isStateful() {
    Object object;
    if (isCompatTintEnabled() && this.mState != null) {
      object = this.mState.mTint;
    } else {
      object = null;
    } 
    return ((object != null && object.isStateful()) || this.mDrawable.isStateful());
  }
  
  public void jumpToCurrentState() { this.mDrawable.jumpToCurrentState(); }
  
  @NonNull
  public Drawable mutate() {
    if (!this.mMutated && super.mutate() == this) {
      this.mState = mutateConstantState();
      if (this.mDrawable != null)
        this.mDrawable.mutate(); 
      if (this.mState != null) {
        Object object;
        DrawableWrapperState drawableWrapperState = this.mState;
        if (this.mDrawable != null) {
          object = this.mDrawable.getConstantState();
        } else {
          object = null;
        } 
        drawableWrapperState.mDrawableState = object;
      } 
      this.mMutated = true;
    } 
    return this;
  }
  
  @NonNull
  DrawableWrapperState mutateConstantState() { return new DrawableWrapperStateBase(this.mState, null); }
  
  protected void onBoundsChange(Rect paramRect) {
    if (this.mDrawable != null)
      this.mDrawable.setBounds(paramRect); 
  }
  
  protected boolean onLevelChange(int paramInt) { return this.mDrawable.setLevel(paramInt); }
  
  public void scheduleDrawable(@NonNull Drawable paramDrawable, @NonNull Runnable paramRunnable, long paramLong) { scheduleSelf(paramRunnable, paramLong); }
  
  public void setAlpha(int paramInt) { this.mDrawable.setAlpha(paramInt); }
  
  public void setChangingConfigurations(int paramInt) { this.mDrawable.setChangingConfigurations(paramInt); }
  
  public void setColorFilter(ColorFilter paramColorFilter) { this.mDrawable.setColorFilter(paramColorFilter); }
  
  public void setDither(boolean paramBoolean) { this.mDrawable.setDither(paramBoolean); }
  
  public void setFilterBitmap(boolean paramBoolean) { this.mDrawable.setFilterBitmap(paramBoolean); }
  
  public boolean setState(@NonNull int[] paramArrayOfInt) {
    boolean bool = this.mDrawable.setState(paramArrayOfInt);
    return (updateTint(paramArrayOfInt) || bool);
  }
  
  public void setTint(int paramInt) { setTintList(ColorStateList.valueOf(paramInt)); }
  
  public void setTintList(ColorStateList paramColorStateList) {
    this.mState.mTint = paramColorStateList;
    updateTint(getState());
  }
  
  public void setTintMode(@NonNull PorterDuff.Mode paramMode) {
    this.mState.mTintMode = paramMode;
    updateTint(getState());
  }
  
  public boolean setVisible(boolean paramBoolean1, boolean paramBoolean2) { return (super.setVisible(paramBoolean1, paramBoolean2) || this.mDrawable.setVisible(paramBoolean1, paramBoolean2)); }
  
  public final void setWrappedDrawable(Drawable paramDrawable) {
    if (this.mDrawable != null)
      this.mDrawable.setCallback(null); 
    this.mDrawable = paramDrawable;
    if (paramDrawable != null) {
      paramDrawable.setCallback(this);
      setVisible(paramDrawable.isVisible(), true);
      setState(paramDrawable.getState());
      setLevel(paramDrawable.getLevel());
      setBounds(paramDrawable.getBounds());
      if (this.mState != null)
        this.mState.mDrawableState = paramDrawable.getConstantState(); 
    } 
    invalidateSelf();
  }
  
  public void unscheduleDrawable(@NonNull Drawable paramDrawable, @NonNull Runnable paramRunnable) { unscheduleSelf(paramRunnable); }
  
  protected static abstract class DrawableWrapperState extends Drawable.ConstantState {
    int mChangingConfigurations;
    
    Drawable.ConstantState mDrawableState;
    
    ColorStateList mTint = null;
    
    PorterDuff.Mode mTintMode = WrappedDrawableApi14.DEFAULT_TINT_MODE;
    
    DrawableWrapperState(@Nullable DrawableWrapperState param1DrawableWrapperState, @Nullable Resources param1Resources) {
      if (param1DrawableWrapperState != null) {
        this.mChangingConfigurations = param1DrawableWrapperState.mChangingConfigurations;
        this.mDrawableState = param1DrawableWrapperState.mDrawableState;
        this.mTint = param1DrawableWrapperState.mTint;
        this.mTintMode = param1DrawableWrapperState.mTintMode;
      } 
    }
    
    boolean canConstantState() { return (this.mDrawableState != null); }
    
    public int getChangingConfigurations() {
      int i;
      int j = this.mChangingConfigurations;
      if (this.mDrawableState != null) {
        i = this.mDrawableState.getChangingConfigurations();
      } else {
        i = 0;
      } 
      return j | i;
    }
    
    @NonNull
    public Drawable newDrawable() { return newDrawable(null); }
    
    @NonNull
    public abstract Drawable newDrawable(@Nullable Resources param1Resources);
  }
  
  private static class DrawableWrapperStateBase extends DrawableWrapperState {
    DrawableWrapperStateBase(@Nullable WrappedDrawableApi14.DrawableWrapperState param1DrawableWrapperState, @Nullable Resources param1Resources) { super(param1DrawableWrapperState, param1Resources); }
    
    @NonNull
    public Drawable newDrawable(@Nullable Resources param1Resources) { return new WrappedDrawableApi14(this, param1Resources); }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/graphics/drawable/WrappedDrawableApi14.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */