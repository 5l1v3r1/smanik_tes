package android.support.v7.view.menu;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.internal.view.SupportMenuItem;
import android.support.v4.view.ActionProvider;
import android.support.v7.content.res.AppCompatResources;
import android.util.Log;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewDebug.CapturedViewProperty;
import android.widget.LinearLayout;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public final class MenuItemImpl implements SupportMenuItem {
  private static final int CHECKABLE = 1;
  
  private static final int CHECKED = 2;
  
  private static final int ENABLED = 16;
  
  private static final int EXCLUSIVE = 4;
  
  private static final int HIDDEN = 8;
  
  private static final int IS_ACTION = 32;
  
  static final int NO_ICON = 0;
  
  private static final int SHOW_AS_ACTION_MASK = 3;
  
  private static final String TAG = "MenuItemImpl";
  
  private static String sDeleteShortcutLabel;
  
  private static String sEnterShortcutLabel;
  
  private static String sPrependShortcutLabel;
  
  private static String sSpaceShortcutLabel;
  
  private ActionProvider mActionProvider;
  
  private View mActionView;
  
  private final int mCategoryOrder;
  
  private MenuItem.OnMenuItemClickListener mClickListener;
  
  private CharSequence mContentDescription;
  
  private int mFlags = 16;
  
  private final int mGroup;
  
  private boolean mHasIconTint = false;
  
  private boolean mHasIconTintMode = false;
  
  private Drawable mIconDrawable;
  
  private int mIconResId = 0;
  
  private ColorStateList mIconTintList = null;
  
  private PorterDuff.Mode mIconTintMode = null;
  
  private final int mId;
  
  private Intent mIntent;
  
  private boolean mIsActionViewExpanded = false;
  
  private Runnable mItemCallback;
  
  MenuBuilder mMenu;
  
  private ContextMenu.ContextMenuInfo mMenuInfo;
  
  private boolean mNeedToApplyIconTint = false;
  
  private MenuItem.OnActionExpandListener mOnActionExpandListener;
  
  private final int mOrdering;
  
  private char mShortcutAlphabeticChar;
  
  private int mShortcutAlphabeticModifiers = 4096;
  
  private char mShortcutNumericChar;
  
  private int mShortcutNumericModifiers = 4096;
  
  private int mShowAsAction = 0;
  
  private SubMenuBuilder mSubMenu;
  
  private CharSequence mTitle;
  
  private CharSequence mTitleCondensed;
  
  private CharSequence mTooltipText;
  
  MenuItemImpl(MenuBuilder paramMenuBuilder, int paramInt1, int paramInt2, int paramInt3, int paramInt4, CharSequence paramCharSequence, int paramInt5) {
    this.mMenu = paramMenuBuilder;
    this.mId = paramInt2;
    this.mGroup = paramInt1;
    this.mCategoryOrder = paramInt3;
    this.mOrdering = paramInt4;
    this.mTitle = paramCharSequence;
    this.mShowAsAction = paramInt5;
  }
  
  private Drawable applyIconTintIfNecessary(Drawable paramDrawable) { // Byte code:
    //   0: aload_1
    //   1: astore_2
    //   2: aload_1
    //   3: ifnull -> 74
    //   6: aload_1
    //   7: astore_2
    //   8: aload_0
    //   9: getfield mNeedToApplyIconTint : Z
    //   12: ifeq -> 74
    //   15: aload_0
    //   16: getfield mHasIconTint : Z
    //   19: ifne -> 31
    //   22: aload_1
    //   23: astore_2
    //   24: aload_0
    //   25: getfield mHasIconTintMode : Z
    //   28: ifeq -> 74
    //   31: aload_1
    //   32: invokestatic wrap : (Landroid/graphics/drawable/Drawable;)Landroid/graphics/drawable/Drawable;
    //   35: invokevirtual mutate : ()Landroid/graphics/drawable/Drawable;
    //   38: astore_2
    //   39: aload_0
    //   40: getfield mHasIconTint : Z
    //   43: ifeq -> 54
    //   46: aload_2
    //   47: aload_0
    //   48: getfield mIconTintList : Landroid/content/res/ColorStateList;
    //   51: invokestatic setTintList : (Landroid/graphics/drawable/Drawable;Landroid/content/res/ColorStateList;)V
    //   54: aload_0
    //   55: getfield mHasIconTintMode : Z
    //   58: ifeq -> 69
    //   61: aload_2
    //   62: aload_0
    //   63: getfield mIconTintMode : Landroid/graphics/PorterDuff$Mode;
    //   66: invokestatic setTintMode : (Landroid/graphics/drawable/Drawable;Landroid/graphics/PorterDuff$Mode;)V
    //   69: aload_0
    //   70: iconst_0
    //   71: putfield mNeedToApplyIconTint : Z
    //   74: aload_2
    //   75: areturn }
  
  public void actionFormatChanged() { this.mMenu.onItemActionRequestChanged(this); }
  
  public boolean collapseActionView() { return ((this.mShowAsAction & 0x8) == 0) ? false : ((this.mActionView == null) ? true : ((this.mOnActionExpandListener == null || this.mOnActionExpandListener.onMenuItemActionCollapse(this)) ? this.mMenu.collapseItemActionView(this) : 0)); }
  
  public boolean expandActionView() { return !hasCollapsibleActionView() ? false : ((this.mOnActionExpandListener == null || this.mOnActionExpandListener.onMenuItemActionExpand(this)) ? this.mMenu.expandItemActionView(this) : 0); }
  
  public ActionProvider getActionProvider() { throw new UnsupportedOperationException("This is not supported, use MenuItemCompat.getActionProvider()"); }
  
  public View getActionView() {
    if (this.mActionView != null)
      return this.mActionView; 
    if (this.mActionProvider != null) {
      this.mActionView = this.mActionProvider.onCreateActionView(this);
      return this.mActionView;
    } 
    return null;
  }
  
  public int getAlphabeticModifiers() { return this.mShortcutAlphabeticModifiers; }
  
  public char getAlphabeticShortcut() { return this.mShortcutAlphabeticChar; }
  
  Runnable getCallback() { return this.mItemCallback; }
  
  public CharSequence getContentDescription() { return this.mContentDescription; }
  
  public int getGroupId() { return this.mGroup; }
  
  public Drawable getIcon() {
    if (this.mIconDrawable != null)
      return applyIconTintIfNecessary(this.mIconDrawable); 
    if (this.mIconResId != 0) {
      Drawable drawable = AppCompatResources.getDrawable(this.mMenu.getContext(), this.mIconResId);
      this.mIconResId = 0;
      this.mIconDrawable = drawable;
      return applyIconTintIfNecessary(drawable);
    } 
    return null;
  }
  
  public ColorStateList getIconTintList() { return this.mIconTintList; }
  
  public PorterDuff.Mode getIconTintMode() { return this.mIconTintMode; }
  
  public Intent getIntent() { return this.mIntent; }
  
  @CapturedViewProperty
  public int getItemId() { return this.mId; }
  
  public ContextMenu.ContextMenuInfo getMenuInfo() { return this.mMenuInfo; }
  
  public int getNumericModifiers() { return this.mShortcutNumericModifiers; }
  
  public char getNumericShortcut() { return this.mShortcutNumericChar; }
  
  public int getOrder() { return this.mCategoryOrder; }
  
  public int getOrdering() { return this.mOrdering; }
  
  char getShortcut() { return this.mMenu.isQwertyMode() ? this.mShortcutAlphabeticChar : this.mShortcutNumericChar; }
  
  String getShortcutLabel() {
    char c = getShortcut();
    if (c == '\000')
      return ""; 
    StringBuilder stringBuilder = new StringBuilder(sPrependShortcutLabel);
    if (c != '\b') {
      if (c != '\n') {
        if (c != ' ') {
          stringBuilder.append(c);
        } else {
          stringBuilder.append(sSpaceShortcutLabel);
        } 
      } else {
        stringBuilder.append(sEnterShortcutLabel);
      } 
    } else {
      stringBuilder.append(sDeleteShortcutLabel);
    } 
    return stringBuilder.toString();
  }
  
  public SubMenu getSubMenu() { return this.mSubMenu; }
  
  public ActionProvider getSupportActionProvider() { return this.mActionProvider; }
  
  @CapturedViewProperty
  public CharSequence getTitle() { return this.mTitle; }
  
  public CharSequence getTitleCondensed() {
    CharSequence charSequence;
    if (this.mTitleCondensed != null) {
      charSequence = this.mTitleCondensed;
    } else {
      charSequence = this.mTitle;
    } 
    return (Build.VERSION.SDK_INT < 18 && charSequence != null && !(charSequence instanceof String)) ? charSequence.toString() : charSequence;
  }
  
  CharSequence getTitleForItemView(MenuView.ItemView paramItemView) { return (paramItemView != null && paramItemView.prefersCondensedTitle()) ? getTitleCondensed() : getTitle(); }
  
  public CharSequence getTooltipText() { return this.mTooltipText; }
  
  public boolean hasCollapsibleActionView() {
    int i = this.mShowAsAction;
    boolean bool = false;
    if ((i & 0x8) != 0) {
      if (this.mActionView == null && this.mActionProvider != null)
        this.mActionView = this.mActionProvider.onCreateActionView(this); 
      if (this.mActionView != null)
        bool = true; 
      return bool;
    } 
    return false;
  }
  
  public boolean hasSubMenu() { return (this.mSubMenu != null); }
  
  public boolean invoke() {
    if (this.mClickListener != null && this.mClickListener.onMenuItemClick(this))
      return true; 
    if (this.mMenu.dispatchMenuItemSelected(this.mMenu, this))
      return true; 
    if (this.mItemCallback != null) {
      this.mItemCallback.run();
      return true;
    } 
    if (this.mIntent != null)
      try {
        this.mMenu.getContext().startActivity(this.mIntent);
        return true;
      } catch (ActivityNotFoundException activityNotFoundException) {
        Log.e("MenuItemImpl", "Can't find activity to handle intent; ignoring", activityNotFoundException);
      }  
    return (this.mActionProvider != null && this.mActionProvider.onPerformDefaultAction());
  }
  
  public boolean isActionButton() { return ((this.mFlags & 0x20) == 32); }
  
  public boolean isActionViewExpanded() { return this.mIsActionViewExpanded; }
  
  public boolean isCheckable() { return ((this.mFlags & true) == 1); }
  
  public boolean isChecked() { return ((this.mFlags & 0x2) == 2); }
  
  public boolean isEnabled() { return ((this.mFlags & 0x10) != 0); }
  
  public boolean isExclusiveCheckable() { return ((this.mFlags & 0x4) != 0); }
  
  public boolean isVisible() {
    ActionProvider actionProvider = this.mActionProvider;
    boolean bool = false;
    byte b = 0;
    if (actionProvider != null && this.mActionProvider.overridesItemVisibility()) {
      bool = b;
      if ((this.mFlags & 0x8) == 0) {
        bool = b;
        if (this.mActionProvider.isVisible())
          bool = true; 
      } 
      return bool;
    } 
    if ((this.mFlags & 0x8) == 0)
      bool = true; 
    return bool;
  }
  
  public boolean requestsActionButton() { return ((this.mShowAsAction & true) == 1); }
  
  public boolean requiresActionButton() { return ((this.mShowAsAction & 0x2) == 2); }
  
  public MenuItem setActionProvider(ActionProvider paramActionProvider) { throw new UnsupportedOperationException("This is not supported, use MenuItemCompat.setActionProvider()"); }
  
  public SupportMenuItem setActionView(int paramInt) {
    Context context = this.mMenu.getContext();
    setActionView(LayoutInflater.from(context).inflate(paramInt, new LinearLayout(context), false));
    return this;
  }
  
  public SupportMenuItem setActionView(View paramView) {
    this.mActionView = paramView;
    this.mActionProvider = null;
    if (paramView != null && paramView.getId() == -1 && this.mId > 0)
      paramView.setId(this.mId); 
    this.mMenu.onItemActionRequestChanged(this);
    return this;
  }
  
  public void setActionViewExpanded(boolean paramBoolean) {
    this.mIsActionViewExpanded = paramBoolean;
    this.mMenu.onItemsChanged(false);
  }
  
  public MenuItem setAlphabeticShortcut(char paramChar) {
    if (this.mShortcutAlphabeticChar == paramChar)
      return this; 
    this.mShortcutAlphabeticChar = Character.toLowerCase(paramChar);
    this.mMenu.onItemsChanged(false);
    return this;
  }
  
  public MenuItem setAlphabeticShortcut(char paramChar, int paramInt) {
    if (this.mShortcutAlphabeticChar == paramChar && this.mShortcutAlphabeticModifiers == paramInt)
      return this; 
    this.mShortcutAlphabeticChar = Character.toLowerCase(paramChar);
    this.mShortcutAlphabeticModifiers = KeyEvent.normalizeMetaState(paramInt);
    this.mMenu.onItemsChanged(false);
    return this;
  }
  
  public MenuItem setCallback(Runnable paramRunnable) {
    this.mItemCallback = paramRunnable;
    return this;
  }
  
  public MenuItem setCheckable(boolean paramBoolean) {
    int i = this.mFlags;
    this.mFlags = paramBoolean | this.mFlags & 0xFFFFFFFE;
    if (i != this.mFlags)
      this.mMenu.onItemsChanged(false); 
    return this;
  }
  
  public MenuItem setChecked(boolean paramBoolean) {
    if ((this.mFlags & 0x4) != 0) {
      this.mMenu.setExclusiveItemChecked(this);
      return this;
    } 
    setCheckedInt(paramBoolean);
    return this;
  }
  
  void setCheckedInt(boolean paramBoolean) {
    int i;
    int j = this.mFlags;
    int k = this.mFlags;
    if (paramBoolean) {
      i = 2;
    } else {
      i = 0;
    } 
    this.mFlags = i | k & 0xFFFFFFFD;
    if (j != this.mFlags)
      this.mMenu.onItemsChanged(false); 
  }
  
  public SupportMenuItem setContentDescription(CharSequence paramCharSequence) {
    this.mContentDescription = paramCharSequence;
    this.mMenu.onItemsChanged(false);
    return this;
  }
  
  public MenuItem setEnabled(boolean paramBoolean) {
    if (paramBoolean) {
      this.mFlags |= 0x10;
    } else {
      this.mFlags &= 0xFFFFFFEF;
    } 
    this.mMenu.onItemsChanged(false);
    return this;
  }
  
  public void setExclusiveCheckable(boolean paramBoolean) {
    int i;
    int j = this.mFlags;
    if (paramBoolean) {
      i = 4;
    } else {
      i = 0;
    } 
    this.mFlags = i | j & 0xFFFFFFFB;
  }
  
  public MenuItem setIcon(int paramInt) {
    this.mIconDrawable = null;
    this.mIconResId = paramInt;
    this.mNeedToApplyIconTint = true;
    this.mMenu.onItemsChanged(false);
    return this;
  }
  
  public MenuItem setIcon(Drawable paramDrawable) {
    this.mIconResId = 0;
    this.mIconDrawable = paramDrawable;
    this.mNeedToApplyIconTint = true;
    this.mMenu.onItemsChanged(false);
    return this;
  }
  
  public MenuItem setIconTintList(@Nullable ColorStateList paramColorStateList) {
    this.mIconTintList = paramColorStateList;
    this.mHasIconTint = true;
    this.mNeedToApplyIconTint = true;
    this.mMenu.onItemsChanged(false);
    return this;
  }
  
  public MenuItem setIconTintMode(PorterDuff.Mode paramMode) {
    this.mIconTintMode = paramMode;
    this.mHasIconTintMode = true;
    this.mNeedToApplyIconTint = true;
    this.mMenu.onItemsChanged(false);
    return this;
  }
  
  public MenuItem setIntent(Intent paramIntent) {
    this.mIntent = paramIntent;
    return this;
  }
  
  public void setIsActionButton(boolean paramBoolean) {
    if (paramBoolean) {
      this.mFlags |= 0x20;
      return;
    } 
    this.mFlags &= 0xFFFFFFDF;
  }
  
  void setMenuInfo(ContextMenu.ContextMenuInfo paramContextMenuInfo) { this.mMenuInfo = paramContextMenuInfo; }
  
  public MenuItem setNumericShortcut(char paramChar) {
    if (this.mShortcutNumericChar == paramChar)
      return this; 
    this.mShortcutNumericChar = paramChar;
    this.mMenu.onItemsChanged(false);
    return this;
  }
  
  public MenuItem setNumericShortcut(char paramChar, int paramInt) {
    if (this.mShortcutNumericChar == paramChar && this.mShortcutNumericModifiers == paramInt)
      return this; 
    this.mShortcutNumericChar = paramChar;
    this.mShortcutNumericModifiers = KeyEvent.normalizeMetaState(paramInt);
    this.mMenu.onItemsChanged(false);
    return this;
  }
  
  public MenuItem setOnActionExpandListener(MenuItem.OnActionExpandListener paramOnActionExpandListener) {
    this.mOnActionExpandListener = paramOnActionExpandListener;
    return this;
  }
  
  public MenuItem setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener paramOnMenuItemClickListener) {
    this.mClickListener = paramOnMenuItemClickListener;
    return this;
  }
  
  public MenuItem setShortcut(char paramChar1, char paramChar2) {
    this.mShortcutNumericChar = paramChar1;
    this.mShortcutAlphabeticChar = Character.toLowerCase(paramChar2);
    this.mMenu.onItemsChanged(false);
    return this;
  }
  
  public MenuItem setShortcut(char paramChar1, char paramChar2, int paramInt1, int paramInt2) {
    this.mShortcutNumericChar = paramChar1;
    this.mShortcutNumericModifiers = KeyEvent.normalizeMetaState(paramInt1);
    this.mShortcutAlphabeticChar = Character.toLowerCase(paramChar2);
    this.mShortcutAlphabeticModifiers = KeyEvent.normalizeMetaState(paramInt2);
    this.mMenu.onItemsChanged(false);
    return this;
  }
  
  public void setShowAsAction(int paramInt) {
    switch (paramInt & 0x3) {
      default:
        throw new IllegalArgumentException("SHOW_AS_ACTION_ALWAYS, SHOW_AS_ACTION_IF_ROOM, and SHOW_AS_ACTION_NEVER are mutually exclusive.");
      case 0:
      case 1:
      case 2:
        break;
    } 
    this.mShowAsAction = paramInt;
    this.mMenu.onItemActionRequestChanged(this);
  }
  
  public SupportMenuItem setShowAsActionFlags(int paramInt) {
    setShowAsAction(paramInt);
    return this;
  }
  
  public void setSubMenu(SubMenuBuilder paramSubMenuBuilder) {
    this.mSubMenu = paramSubMenuBuilder;
    paramSubMenuBuilder.setHeaderTitle(getTitle());
  }
  
  public SupportMenuItem setSupportActionProvider(ActionProvider paramActionProvider) {
    if (this.mActionProvider != null)
      this.mActionProvider.reset(); 
    this.mActionView = null;
    this.mActionProvider = paramActionProvider;
    this.mMenu.onItemsChanged(true);
    if (this.mActionProvider != null)
      this.mActionProvider.setVisibilityListener(new ActionProvider.VisibilityListener() {
            public void onActionProviderVisibilityChanged(boolean param1Boolean) { MenuItemImpl.this.mMenu.onItemVisibleChanged(MenuItemImpl.this); }
          }); 
    return this;
  }
  
  public MenuItem setTitle(int paramInt) { return setTitle(this.mMenu.getContext().getString(paramInt)); }
  
  public MenuItem setTitle(CharSequence paramCharSequence) {
    this.mTitle = paramCharSequence;
    this.mMenu.onItemsChanged(false);
    if (this.mSubMenu != null)
      this.mSubMenu.setHeaderTitle(paramCharSequence); 
    return this;
  }
  
  public MenuItem setTitleCondensed(CharSequence paramCharSequence) {
    this.mTitleCondensed = paramCharSequence;
    if (paramCharSequence == null)
      paramCharSequence = this.mTitle; 
    this.mMenu.onItemsChanged(false);
    return this;
  }
  
  public SupportMenuItem setTooltipText(CharSequence paramCharSequence) {
    this.mTooltipText = paramCharSequence;
    this.mMenu.onItemsChanged(false);
    return this;
  }
  
  public MenuItem setVisible(boolean paramBoolean) {
    if (setVisibleInt(paramBoolean))
      this.mMenu.onItemVisibleChanged(this); 
    return this;
  }
  
  boolean setVisibleInt(boolean paramBoolean) {
    int i;
    int j = this.mFlags;
    int k = this.mFlags;
    boolean bool = false;
    if (paramBoolean) {
      i = 0;
    } else {
      i = 8;
    } 
    this.mFlags = i | k & 0xFFFFFFF7;
    paramBoolean = bool;
    if (j != this.mFlags)
      paramBoolean = true; 
    return paramBoolean;
  }
  
  public boolean shouldShowIcon() { return this.mMenu.getOptionalIconsVisible(); }
  
  boolean shouldShowShortcut() { return (this.mMenu.isShortcutsVisible() && getShortcut() != '\000'); }
  
  public boolean showsTextAsAction() { return ((this.mShowAsAction & 0x4) == 4); }
  
  public String toString() { return (this.mTitle != null) ? this.mTitle.toString() : null; }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/view/menu/MenuItemImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */