package android.support.v7.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

public class ActionBarDrawerToggle implements DrawerLayout.DrawerListener {
  private final Delegate mActivityImpl;
  
  private final int mCloseDrawerContentDescRes;
  
  boolean mDrawerIndicatorEnabled = true;
  
  private final DrawerLayout mDrawerLayout;
  
  private boolean mDrawerSlideAnimationEnabled = true;
  
  private boolean mHasCustomUpIndicator;
  
  private Drawable mHomeAsUpIndicator;
  
  private final int mOpenDrawerContentDescRes;
  
  private DrawerArrowDrawable mSlider;
  
  View.OnClickListener mToolbarNavigationClickListener;
  
  private boolean mWarnedForDisplayHomeAsUp = false;
  
  public ActionBarDrawerToggle(Activity paramActivity, DrawerLayout paramDrawerLayout, @StringRes int paramInt1, @StringRes int paramInt2) { this(paramActivity, null, paramDrawerLayout, null, paramInt1, paramInt2); }
  
  public ActionBarDrawerToggle(Activity paramActivity, DrawerLayout paramDrawerLayout, Toolbar paramToolbar, @StringRes int paramInt1, @StringRes int paramInt2) { this(paramActivity, paramToolbar, paramDrawerLayout, null, paramInt1, paramInt2); }
  
  ActionBarDrawerToggle(Activity paramActivity, Toolbar paramToolbar, DrawerLayout paramDrawerLayout, DrawerArrowDrawable paramDrawerArrowDrawable, @StringRes int paramInt1, @StringRes int paramInt2) {
    if (paramToolbar != null) {
      this.mActivityImpl = new ToolbarCompatDelegate(paramToolbar);
      paramToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
              if (ActionBarDrawerToggle.this.mDrawerIndicatorEnabled) {
                ActionBarDrawerToggle.this.toggle();
                return;
              } 
              if (ActionBarDrawerToggle.this.mToolbarNavigationClickListener != null)
                ActionBarDrawerToggle.this.mToolbarNavigationClickListener.onClick(param1View); 
            }
          });
    } else if (paramActivity instanceof DelegateProvider) {
      this.mActivityImpl = ((DelegateProvider)paramActivity).getDrawerToggleDelegate();
    } else if (Build.VERSION.SDK_INT >= 18) {
      this.mActivityImpl = new JellybeanMr2Delegate(paramActivity);
    } else {
      this.mActivityImpl = new IcsDelegate(paramActivity);
    } 
    this.mDrawerLayout = paramDrawerLayout;
    this.mOpenDrawerContentDescRes = paramInt1;
    this.mCloseDrawerContentDescRes = paramInt2;
    if (paramDrawerArrowDrawable == null) {
      this.mSlider = new DrawerArrowDrawable(this.mActivityImpl.getActionBarThemedContext());
    } else {
      this.mSlider = paramDrawerArrowDrawable;
    } 
    this.mHomeAsUpIndicator = getThemeUpIndicator();
  }
  
  private void setPosition(float paramFloat) {
    if (paramFloat == 1.0F) {
      this.mSlider.setVerticalMirror(true);
    } else if (paramFloat == 0.0F) {
      this.mSlider.setVerticalMirror(false);
    } 
    this.mSlider.setProgress(paramFloat);
  }
  
  @NonNull
  public DrawerArrowDrawable getDrawerArrowDrawable() { return this.mSlider; }
  
  Drawable getThemeUpIndicator() { return this.mActivityImpl.getThemeUpIndicator(); }
  
  public View.OnClickListener getToolbarNavigationClickListener() { return this.mToolbarNavigationClickListener; }
  
  public boolean isDrawerIndicatorEnabled() { return this.mDrawerIndicatorEnabled; }
  
  public boolean isDrawerSlideAnimationEnabled() { return this.mDrawerSlideAnimationEnabled; }
  
  public void onConfigurationChanged(Configuration paramConfiguration) {
    if (!this.mHasCustomUpIndicator)
      this.mHomeAsUpIndicator = getThemeUpIndicator(); 
    syncState();
  }
  
  public void onDrawerClosed(View paramView) {
    setPosition(0.0F);
    if (this.mDrawerIndicatorEnabled)
      setActionBarDescription(this.mOpenDrawerContentDescRes); 
  }
  
  public void onDrawerOpened(View paramView) {
    setPosition(1.0F);
    if (this.mDrawerIndicatorEnabled)
      setActionBarDescription(this.mCloseDrawerContentDescRes); 
  }
  
  public void onDrawerSlide(View paramView, float paramFloat) {
    if (this.mDrawerSlideAnimationEnabled) {
      setPosition(Math.min(1.0F, Math.max(0.0F, paramFloat)));
      return;
    } 
    setPosition(0.0F);
  }
  
  public void onDrawerStateChanged(int paramInt) {}
  
  public boolean onOptionsItemSelected(MenuItem paramMenuItem) {
    if (paramMenuItem != null && paramMenuItem.getItemId() == 16908332 && this.mDrawerIndicatorEnabled) {
      toggle();
      return true;
    } 
    return false;
  }
  
  void setActionBarDescription(int paramInt) { this.mActivityImpl.setActionBarDescription(paramInt); }
  
  void setActionBarUpIndicator(Drawable paramDrawable, int paramInt) {
    if (!this.mWarnedForDisplayHomeAsUp && !this.mActivityImpl.isNavigationVisible()) {
      Log.w("ActionBarDrawerToggle", "DrawerToggle may not show up because NavigationIcon is not visible. You may need to call actionbar.setDisplayHomeAsUpEnabled(true);");
      this.mWarnedForDisplayHomeAsUp = true;
    } 
    this.mActivityImpl.setActionBarUpIndicator(paramDrawable, paramInt);
  }
  
  public void setDrawerArrowDrawable(@NonNull DrawerArrowDrawable paramDrawerArrowDrawable) {
    this.mSlider = paramDrawerArrowDrawable;
    syncState();
  }
  
  public void setDrawerIndicatorEnabled(boolean paramBoolean) {
    if (paramBoolean != this.mDrawerIndicatorEnabled) {
      if (paramBoolean) {
        int i;
        DrawerArrowDrawable drawerArrowDrawable = this.mSlider;
        if (this.mDrawerLayout.isDrawerOpen(8388611)) {
          i = this.mCloseDrawerContentDescRes;
        } else {
          i = this.mOpenDrawerContentDescRes;
        } 
        setActionBarUpIndicator(drawerArrowDrawable, i);
      } else {
        setActionBarUpIndicator(this.mHomeAsUpIndicator, 0);
      } 
      this.mDrawerIndicatorEnabled = paramBoolean;
    } 
  }
  
  public void setDrawerSlideAnimationEnabled(boolean paramBoolean) {
    this.mDrawerSlideAnimationEnabled = paramBoolean;
    if (!paramBoolean)
      setPosition(0.0F); 
  }
  
  public void setHomeAsUpIndicator(int paramInt) {
    Drawable drawable;
    if (paramInt != 0) {
      drawable = this.mDrawerLayout.getResources().getDrawable(paramInt);
    } else {
      drawable = null;
    } 
    setHomeAsUpIndicator(drawable);
  }
  
  public void setHomeAsUpIndicator(Drawable paramDrawable) {
    if (paramDrawable == null) {
      this.mHomeAsUpIndicator = getThemeUpIndicator();
      this.mHasCustomUpIndicator = false;
    } else {
      this.mHomeAsUpIndicator = paramDrawable;
      this.mHasCustomUpIndicator = true;
    } 
    if (!this.mDrawerIndicatorEnabled)
      setActionBarUpIndicator(this.mHomeAsUpIndicator, 0); 
  }
  
  public void setToolbarNavigationClickListener(View.OnClickListener paramOnClickListener) { this.mToolbarNavigationClickListener = paramOnClickListener; }
  
  public void syncState() {
    if (this.mDrawerLayout.isDrawerOpen(8388611)) {
      setPosition(1.0F);
    } else {
      setPosition(0.0F);
    } 
    if (this.mDrawerIndicatorEnabled) {
      int i;
      DrawerArrowDrawable drawerArrowDrawable = this.mSlider;
      if (this.mDrawerLayout.isDrawerOpen(8388611)) {
        i = this.mCloseDrawerContentDescRes;
      } else {
        i = this.mOpenDrawerContentDescRes;
      } 
      setActionBarUpIndicator(drawerArrowDrawable, i);
    } 
  }
  
  void toggle() {
    int i = this.mDrawerLayout.getDrawerLockMode(8388611);
    if (this.mDrawerLayout.isDrawerVisible(8388611) && i != 2) {
      this.mDrawerLayout.closeDrawer(8388611);
      return;
    } 
    if (i != 1)
      this.mDrawerLayout.openDrawer(8388611); 
  }
  
  public static interface Delegate {
    Context getActionBarThemedContext();
    
    Drawable getThemeUpIndicator();
    
    boolean isNavigationVisible();
    
    void setActionBarDescription(@StringRes int param1Int);
    
    void setActionBarUpIndicator(Drawable param1Drawable, @StringRes int param1Int);
  }
  
  public static interface DelegateProvider {
    @Nullable
    ActionBarDrawerToggle.Delegate getDrawerToggleDelegate();
  }
  
  private static class IcsDelegate implements Delegate {
    final Activity mActivity;
    
    ActionBarDrawerToggleHoneycomb.SetIndicatorInfo mSetIndicatorInfo;
    
    IcsDelegate(Activity param1Activity) { this.mActivity = param1Activity; }
    
    public Context getActionBarThemedContext() {
      ActionBar actionBar = this.mActivity.getActionBar();
      return (actionBar != null) ? actionBar.getThemedContext() : this.mActivity;
    }
    
    public Drawable getThemeUpIndicator() { return ActionBarDrawerToggleHoneycomb.getThemeUpIndicator(this.mActivity); }
    
    public boolean isNavigationVisible() {
      ActionBar actionBar = this.mActivity.getActionBar();
      return (actionBar != null && (actionBar.getDisplayOptions() & 0x4) != 0);
    }
    
    public void setActionBarDescription(int param1Int) { this.mSetIndicatorInfo = ActionBarDrawerToggleHoneycomb.setActionBarDescription(this.mSetIndicatorInfo, this.mActivity, param1Int); }
    
    public void setActionBarUpIndicator(Drawable param1Drawable, int param1Int) {
      ActionBar actionBar = this.mActivity.getActionBar();
      if (actionBar != null) {
        actionBar.setDisplayShowHomeEnabled(true);
        this.mSetIndicatorInfo = ActionBarDrawerToggleHoneycomb.setActionBarUpIndicator(this.mSetIndicatorInfo, this.mActivity, param1Drawable, param1Int);
        actionBar.setDisplayShowHomeEnabled(false);
      } 
    }
  }
  
  @RequiresApi(18)
  private static class JellybeanMr2Delegate implements Delegate {
    final Activity mActivity;
    
    JellybeanMr2Delegate(Activity param1Activity) { this.mActivity = param1Activity; }
    
    public Context getActionBarThemedContext() {
      ActionBar actionBar = this.mActivity.getActionBar();
      return (actionBar != null) ? actionBar.getThemedContext() : this.mActivity;
    }
    
    public Drawable getThemeUpIndicator() {
      TypedArray typedArray = getActionBarThemedContext().obtainStyledAttributes(null, new int[] { 16843531 }, 16843470, 0);
      Drawable drawable = typedArray.getDrawable(0);
      typedArray.recycle();
      return drawable;
    }
    
    public boolean isNavigationVisible() {
      ActionBar actionBar = this.mActivity.getActionBar();
      return (actionBar != null && (actionBar.getDisplayOptions() & 0x4) != 0);
    }
    
    public void setActionBarDescription(int param1Int) {
      ActionBar actionBar = this.mActivity.getActionBar();
      if (actionBar != null)
        actionBar.setHomeActionContentDescription(param1Int); 
    }
    
    public void setActionBarUpIndicator(Drawable param1Drawable, int param1Int) {
      ActionBar actionBar = this.mActivity.getActionBar();
      if (actionBar != null) {
        actionBar.setHomeAsUpIndicator(param1Drawable);
        actionBar.setHomeActionContentDescription(param1Int);
      } 
    }
  }
  
  static class ToolbarCompatDelegate implements Delegate {
    final CharSequence mDefaultContentDescription;
    
    final Drawable mDefaultUpIndicator;
    
    final Toolbar mToolbar;
    
    ToolbarCompatDelegate(Toolbar param1Toolbar) {
      this.mToolbar = param1Toolbar;
      this.mDefaultUpIndicator = param1Toolbar.getNavigationIcon();
      this.mDefaultContentDescription = param1Toolbar.getNavigationContentDescription();
    }
    
    public Context getActionBarThemedContext() { return this.mToolbar.getContext(); }
    
    public Drawable getThemeUpIndicator() { return this.mDefaultUpIndicator; }
    
    public boolean isNavigationVisible() { return true; }
    
    public void setActionBarDescription(@StringRes int param1Int) {
      if (param1Int == 0) {
        this.mToolbar.setNavigationContentDescription(this.mDefaultContentDescription);
        return;
      } 
      this.mToolbar.setNavigationContentDescription(param1Int);
    }
    
    public void setActionBarUpIndicator(Drawable param1Drawable, @StringRes int param1Int) {
      this.mToolbar.setNavigationIcon(param1Drawable);
      setActionBarDescription(param1Int);
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/app/ActionBarDrawerToggle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */