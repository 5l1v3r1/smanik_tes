package android.support.v7.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.view.WindowCallbackWrapper;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.widget.DecorToolbar;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.ToolbarWidgetWrapper;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.SpinnerAdapter;
import java.util.ArrayList;

class ToolbarActionBar extends ActionBar {
  DecorToolbar mDecorToolbar;
  
  private boolean mLastMenuVisibility;
  
  private boolean mMenuCallbackSet;
  
  private final Toolbar.OnMenuItemClickListener mMenuClicker = new Toolbar.OnMenuItemClickListener() {
      public boolean onMenuItemClick(MenuItem param1MenuItem) { return ToolbarActionBar.this.mWindowCallback.onMenuItemSelected(0, param1MenuItem); }
    };
  
  private final Runnable mMenuInvalidator = new Runnable() {
      public void run() { ToolbarActionBar.this.populateOptionsMenu(); }
    };
  
  private ArrayList<ActionBar.OnMenuVisibilityListener> mMenuVisibilityListeners = new ArrayList();
  
  boolean mToolbarMenuPrepared;
  
  Window.Callback mWindowCallback;
  
  ToolbarActionBar(Toolbar paramToolbar, CharSequence paramCharSequence, Window.Callback paramCallback) {
    this.mDecorToolbar = new ToolbarWidgetWrapper(paramToolbar, false);
    this.mWindowCallback = new ToolbarCallbackWrapper(paramCallback);
    this.mDecorToolbar.setWindowCallback(this.mWindowCallback);
    paramToolbar.setOnMenuItemClickListener(this.mMenuClicker);
    this.mDecorToolbar.setWindowTitle(paramCharSequence);
  }
  
  private Menu getMenu() {
    if (!this.mMenuCallbackSet) {
      this.mDecorToolbar.setMenuCallbacks(new ActionMenuPresenterCallback(), new MenuBuilderCallback());
      this.mMenuCallbackSet = true;
    } 
    return this.mDecorToolbar.getMenu();
  }
  
  public void addOnMenuVisibilityListener(ActionBar.OnMenuVisibilityListener paramOnMenuVisibilityListener) { this.mMenuVisibilityListeners.add(paramOnMenuVisibilityListener); }
  
  public void addTab(ActionBar.Tab paramTab) { throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars"); }
  
  public void addTab(ActionBar.Tab paramTab, int paramInt) { throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars"); }
  
  public void addTab(ActionBar.Tab paramTab, int paramInt, boolean paramBoolean) { throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars"); }
  
  public void addTab(ActionBar.Tab paramTab, boolean paramBoolean) { throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars"); }
  
  public boolean closeOptionsMenu() { return this.mDecorToolbar.hideOverflowMenu(); }
  
  public boolean collapseActionView() {
    if (this.mDecorToolbar.hasExpandedActionView()) {
      this.mDecorToolbar.collapseActionView();
      return true;
    } 
    return false;
  }
  
  public void dispatchMenuVisibilityChanged(boolean paramBoolean) {
    if (paramBoolean == this.mLastMenuVisibility)
      return; 
    this.mLastMenuVisibility = paramBoolean;
    int i = this.mMenuVisibilityListeners.size();
    for (byte b = 0; b < i; b++)
      ((ActionBar.OnMenuVisibilityListener)this.mMenuVisibilityListeners.get(b)).onMenuVisibilityChanged(paramBoolean); 
  }
  
  public View getCustomView() { return this.mDecorToolbar.getCustomView(); }
  
  public int getDisplayOptions() { return this.mDecorToolbar.getDisplayOptions(); }
  
  public float getElevation() { return ViewCompat.getElevation(this.mDecorToolbar.getViewGroup()); }
  
  public int getHeight() { return this.mDecorToolbar.getHeight(); }
  
  public int getNavigationItemCount() { return 0; }
  
  public int getNavigationMode() { return 0; }
  
  public int getSelectedNavigationIndex() { return -1; }
  
  public ActionBar.Tab getSelectedTab() { throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars"); }
  
  public CharSequence getSubtitle() { return this.mDecorToolbar.getSubtitle(); }
  
  public ActionBar.Tab getTabAt(int paramInt) { throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars"); }
  
  public int getTabCount() { return 0; }
  
  public Context getThemedContext() { return this.mDecorToolbar.getContext(); }
  
  public CharSequence getTitle() { return this.mDecorToolbar.getTitle(); }
  
  public Window.Callback getWrappedWindowCallback() { return this.mWindowCallback; }
  
  public void hide() { this.mDecorToolbar.setVisibility(8); }
  
  public boolean invalidateOptionsMenu() {
    this.mDecorToolbar.getViewGroup().removeCallbacks(this.mMenuInvalidator);
    ViewCompat.postOnAnimation(this.mDecorToolbar.getViewGroup(), this.mMenuInvalidator);
    return true;
  }
  
  public boolean isShowing() { return (this.mDecorToolbar.getVisibility() == 0); }
  
  public boolean isTitleTruncated() { return super.isTitleTruncated(); }
  
  public ActionBar.Tab newTab() { throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars"); }
  
  public void onConfigurationChanged(Configuration paramConfiguration) { super.onConfigurationChanged(paramConfiguration); }
  
  void onDestroy() { this.mDecorToolbar.getViewGroup().removeCallbacks(this.mMenuInvalidator); }
  
  public boolean onKeyShortcut(int paramInt, KeyEvent paramKeyEvent) {
    Menu menu = getMenu();
    if (menu != null) {
      if (paramKeyEvent != null) {
        i = paramKeyEvent.getDeviceId();
      } else {
        i = -1;
      } 
      int i = KeyCharacterMap.load(i).getKeyboardType();
      boolean bool = true;
      if (i == 1)
        bool = false; 
      menu.setQwertyMode(bool);
      return menu.performShortcut(paramInt, paramKeyEvent, 0);
    } 
    return false;
  }
  
  public boolean onMenuKeyEvent(KeyEvent paramKeyEvent) {
    if (paramKeyEvent.getAction() == 1)
      openOptionsMenu(); 
    return true;
  }
  
  public boolean openOptionsMenu() { return this.mDecorToolbar.showOverflowMenu(); }
  
  void populateOptionsMenu() {
    null = getMenu();
    if (null instanceof MenuBuilder) {
      object = (MenuBuilder)null;
    } else {
      object = null;
    } 
    if (object != null)
      object.stopDispatchingItemsChanged(); 
    try {
      null.clear();
      if (!this.mWindowCallback.onCreatePanelMenu(0, null) || !this.mWindowCallback.onPreparePanel(0, null, null))
        null.clear(); 
      return;
    } finally {
      if (object != null)
        object.startDispatchingItemsChanged(); 
    } 
  }
  
  public void removeAllTabs() { throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars"); }
  
  public void removeOnMenuVisibilityListener(ActionBar.OnMenuVisibilityListener paramOnMenuVisibilityListener) { this.mMenuVisibilityListeners.remove(paramOnMenuVisibilityListener); }
  
  public void removeTab(ActionBar.Tab paramTab) { throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars"); }
  
  public void removeTabAt(int paramInt) { throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars"); }
  
  public boolean requestFocus() {
    ViewGroup viewGroup = this.mDecorToolbar.getViewGroup();
    if (viewGroup != null && !viewGroup.hasFocus()) {
      viewGroup.requestFocus();
      return true;
    } 
    return false;
  }
  
  public void selectTab(ActionBar.Tab paramTab) { throw new UnsupportedOperationException("Tabs are not supported in toolbar action bars"); }
  
  public void setBackgroundDrawable(@Nullable Drawable paramDrawable) { this.mDecorToolbar.setBackgroundDrawable(paramDrawable); }
  
  public void setCustomView(int paramInt) { setCustomView(LayoutInflater.from(this.mDecorToolbar.getContext()).inflate(paramInt, this.mDecorToolbar.getViewGroup(), false)); }
  
  public void setCustomView(View paramView) { setCustomView(paramView, new ActionBar.LayoutParams(-2, -2)); }
  
  public void setCustomView(View paramView, ActionBar.LayoutParams paramLayoutParams) {
    if (paramView != null)
      paramView.setLayoutParams(paramLayoutParams); 
    this.mDecorToolbar.setCustomView(paramView);
  }
  
  public void setDefaultDisplayHomeAsUpEnabled(boolean paramBoolean) {}
  
  public void setDisplayHomeAsUpEnabled(boolean paramBoolean) {
    byte b;
    if (paramBoolean) {
      b = 4;
    } else {
      b = 0;
    } 
    setDisplayOptions(b, 4);
  }
  
  @SuppressLint({"WrongConstant"})
  public void setDisplayOptions(int paramInt) { setDisplayOptions(paramInt, -1); }
  
  public void setDisplayOptions(int paramInt1, int paramInt2) {
    int i = this.mDecorToolbar.getDisplayOptions();
    this.mDecorToolbar.setDisplayOptions(paramInt1 & paramInt2 | (paramInt2 ^ 0xFFFFFFFF) & i);
  }
  
  public void setDisplayShowCustomEnabled(boolean paramBoolean) {
    byte b;
    if (paramBoolean) {
      b = 16;
    } else {
      b = 0;
    } 
    setDisplayOptions(b, 16);
  }
  
  public void setDisplayShowHomeEnabled(boolean paramBoolean) {
    byte b;
    if (paramBoolean) {
      b = 2;
    } else {
      b = 0;
    } 
    setDisplayOptions(b, 2);
  }
  
  public void setDisplayShowTitleEnabled(boolean paramBoolean) {
    byte b;
    if (paramBoolean) {
      b = 8;
    } else {
      b = 0;
    } 
    setDisplayOptions(b, 8);
  }
  
  public void setDisplayUseLogoEnabled(boolean paramBoolean) { throw new RuntimeException("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.copyTypes(TypeTransformer.java:311)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.fixTypes(TypeTransformer.java:226)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:207)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n"); }
  
  public void setElevation(float paramFloat) { ViewCompat.setElevation(this.mDecorToolbar.getViewGroup(), paramFloat); }
  
  public void setHomeActionContentDescription(int paramInt) { this.mDecorToolbar.setNavigationContentDescription(paramInt); }
  
  public void setHomeActionContentDescription(CharSequence paramCharSequence) { this.mDecorToolbar.setNavigationContentDescription(paramCharSequence); }
  
  public void setHomeAsUpIndicator(int paramInt) { this.mDecorToolbar.setNavigationIcon(paramInt); }
  
  public void setHomeAsUpIndicator(Drawable paramDrawable) { this.mDecorToolbar.setNavigationIcon(paramDrawable); }
  
  public void setHomeButtonEnabled(boolean paramBoolean) {}
  
  public void setIcon(int paramInt) { this.mDecorToolbar.setIcon(paramInt); }
  
  public void setIcon(Drawable paramDrawable) { this.mDecorToolbar.setIcon(paramDrawable); }
  
  public void setListNavigationCallbacks(SpinnerAdapter paramSpinnerAdapter, ActionBar.OnNavigationListener paramOnNavigationListener) { this.mDecorToolbar.setDropdownParams(paramSpinnerAdapter, new NavItemSelectedListener(paramOnNavigationListener)); }
  
  public void setLogo(int paramInt) { this.mDecorToolbar.setLogo(paramInt); }
  
  public void setLogo(Drawable paramDrawable) { this.mDecorToolbar.setLogo(paramDrawable); }
  
  public void setNavigationMode(int paramInt) {
    if (paramInt == 2)
      throw new IllegalArgumentException("Tabs not supported in this configuration"); 
    this.mDecorToolbar.setNavigationMode(paramInt);
  }
  
  public void setSelectedNavigationItem(int paramInt) {
    if (this.mDecorToolbar.getNavigationMode() != 1)
      throw new IllegalStateException("setSelectedNavigationIndex not valid for current navigation mode"); 
    this.mDecorToolbar.setDropdownSelectedPosition(paramInt);
  }
  
  public void setShowHideAnimationEnabled(boolean paramBoolean) {}
  
  public void setSplitBackgroundDrawable(Drawable paramDrawable) {}
  
  public void setStackedBackgroundDrawable(Drawable paramDrawable) {}
  
  public void setSubtitle(int paramInt) {
    CharSequence charSequence;
    DecorToolbar decorToolbar = this.mDecorToolbar;
    if (paramInt != 0) {
      charSequence = this.mDecorToolbar.getContext().getText(paramInt);
    } else {
      charSequence = null;
    } 
    decorToolbar.setSubtitle(charSequence);
  }
  
  public void setSubtitle(CharSequence paramCharSequence) { this.mDecorToolbar.setSubtitle(paramCharSequence); }
  
  public void setTitle(int paramInt) {
    CharSequence charSequence;
    DecorToolbar decorToolbar = this.mDecorToolbar;
    if (paramInt != 0) {
      charSequence = this.mDecorToolbar.getContext().getText(paramInt);
    } else {
      charSequence = null;
    } 
    decorToolbar.setTitle(charSequence);
  }
  
  public void setTitle(CharSequence paramCharSequence) { this.mDecorToolbar.setTitle(paramCharSequence); }
  
  public void setWindowTitle(CharSequence paramCharSequence) { this.mDecorToolbar.setWindowTitle(paramCharSequence); }
  
  public void show() { this.mDecorToolbar.setVisibility(0); }
  
  private final class ActionMenuPresenterCallback implements MenuPresenter.Callback {
    private boolean mClosingActionMenu;
    
    public void onCloseMenu(MenuBuilder param1MenuBuilder, boolean param1Boolean) {
      if (this.mClosingActionMenu)
        return; 
      this.mClosingActionMenu = true;
      ToolbarActionBar.this.mDecorToolbar.dismissPopupMenus();
      if (ToolbarActionBar.this.mWindowCallback != null)
        ToolbarActionBar.this.mWindowCallback.onPanelClosed(108, param1MenuBuilder); 
      this.mClosingActionMenu = false;
    }
    
    public boolean onOpenSubMenu(MenuBuilder param1MenuBuilder) {
      if (ToolbarActionBar.this.mWindowCallback != null) {
        ToolbarActionBar.this.mWindowCallback.onMenuOpened(108, param1MenuBuilder);
        return true;
      } 
      return false;
    }
  }
  
  private final class MenuBuilderCallback implements MenuBuilder.Callback {
    public boolean onMenuItemSelected(MenuBuilder param1MenuBuilder, MenuItem param1MenuItem) { return false; }
    
    public void onMenuModeChange(MenuBuilder param1MenuBuilder) {
      if (ToolbarActionBar.this.mWindowCallback != null) {
        if (ToolbarActionBar.this.mDecorToolbar.isOverflowMenuShowing()) {
          ToolbarActionBar.this.mWindowCallback.onPanelClosed(108, param1MenuBuilder);
          return;
        } 
        if (ToolbarActionBar.this.mWindowCallback.onPreparePanel(0, null, param1MenuBuilder))
          ToolbarActionBar.this.mWindowCallback.onMenuOpened(108, param1MenuBuilder); 
      } 
    }
  }
  
  private class ToolbarCallbackWrapper extends WindowCallbackWrapper {
    public ToolbarCallbackWrapper(Window.Callback param1Callback) { super(param1Callback); }
    
    public View onCreatePanelView(int param1Int) { return (param1Int == 0) ? new View(ToolbarActionBar.this.mDecorToolbar.getContext()) : super.onCreatePanelView(param1Int); }
    
    public boolean onPreparePanel(int param1Int, View param1View, Menu param1Menu) {
      boolean bool = super.onPreparePanel(param1Int, param1View, param1Menu);
      if (bool && !ToolbarActionBar.this.mToolbarMenuPrepared) {
        ToolbarActionBar.this.mDecorToolbar.setMenuPrepared();
        ToolbarActionBar.this.mToolbarMenuPrepared = true;
      } 
      return bool;
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/app/ToolbarActionBar.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */