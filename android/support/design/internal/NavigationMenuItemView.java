package android.support.design.internal;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.RestrictTo;
import android.support.design.R;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.appcompat.R;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.TooltipCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class NavigationMenuItemView extends ForegroundLinearLayout implements MenuView.ItemView {
  private static final int[] CHECKED_STATE_SET = { 16842912 };
  
  private final AccessibilityDelegateCompat mAccessibilityDelegate = new AccessibilityDelegateCompat() {
      public void onInitializeAccessibilityNodeInfo(View param1View, AccessibilityNodeInfoCompat param1AccessibilityNodeInfoCompat) {
        super.onInitializeAccessibilityNodeInfo(param1View, param1AccessibilityNodeInfoCompat);
        param1AccessibilityNodeInfoCompat.setCheckable(NavigationMenuItemView.this.mCheckable);
      }
    };
  
  private FrameLayout mActionArea;
  
  boolean mCheckable;
  
  private Drawable mEmptyDrawable;
  
  private boolean mHasIconTintList;
  
  private final int mIconSize;
  
  private ColorStateList mIconTintList;
  
  private MenuItemImpl mItemData;
  
  private boolean mNeedsEmptyIcon;
  
  private final CheckedTextView mTextView;
  
  public NavigationMenuItemView(Context paramContext) { this(paramContext, null); }
  
  public NavigationMenuItemView(Context paramContext, AttributeSet paramAttributeSet) { this(paramContext, paramAttributeSet, 0); }
  
  public NavigationMenuItemView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
    super(paramContext, paramAttributeSet, paramInt);
    setOrientation(0);
    LayoutInflater.from(paramContext).inflate(R.layout.design_navigation_menu_item, this, true);
    this.mIconSize = paramContext.getResources().getDimensionPixelSize(R.dimen.design_navigation_icon_size);
    this.mTextView = (CheckedTextView)findViewById(R.id.design_menu_item_text);
    this.mTextView.setDuplicateParentStateEnabled(true);
    ViewCompat.setAccessibilityDelegate(this.mTextView, this.mAccessibilityDelegate);
  }
  
  private void adjustAppearance() {
    if (shouldExpandActionArea()) {
      this.mTextView.setVisibility(8);
      if (this.mActionArea != null) {
        LinearLayoutCompat.LayoutParams layoutParams = (LinearLayoutCompat.LayoutParams)this.mActionArea.getLayoutParams();
        layoutParams.width = -1;
        this.mActionArea.setLayoutParams(layoutParams);
        return;
      } 
    } else {
      this.mTextView.setVisibility(0);
      if (this.mActionArea != null) {
        LinearLayoutCompat.LayoutParams layoutParams = (LinearLayoutCompat.LayoutParams)this.mActionArea.getLayoutParams();
        layoutParams.width = -2;
        this.mActionArea.setLayoutParams(layoutParams);
      } 
    } 
  }
  
  private StateListDrawable createDefaultBackground() {
    TypedValue typedValue = new TypedValue();
    if (getContext().getTheme().resolveAttribute(R.attr.colorControlHighlight, typedValue, true)) {
      StateListDrawable stateListDrawable = new StateListDrawable();
      stateListDrawable.addState(CHECKED_STATE_SET, new ColorDrawable(typedValue.data));
      stateListDrawable.addState(EMPTY_STATE_SET, new ColorDrawable(0));
      return stateListDrawable;
    } 
    return null;
  }
  
  private void setActionView(View paramView) {
    if (paramView != null) {
      if (this.mActionArea == null)
        this.mActionArea = (FrameLayout)((ViewStub)findViewById(R.id.design_menu_item_action_area_stub)).inflate(); 
      this.mActionArea.removeAllViews();
      this.mActionArea.addView(paramView);
    } 
  }
  
  private boolean shouldExpandActionArea() { return (this.mItemData.getTitle() == null && this.mItemData.getIcon() == null && this.mItemData.getActionView() != null); }
  
  public MenuItemImpl getItemData() { return this.mItemData; }
  
  public void initialize(MenuItemImpl paramMenuItemImpl, int paramInt) {
    this.mItemData = paramMenuItemImpl;
    if (paramMenuItemImpl.isVisible()) {
      paramInt = 0;
    } else {
      paramInt = 8;
    } 
    setVisibility(paramInt);
    if (getBackground() == null)
      ViewCompat.setBackground(this, createDefaultBackground()); 
    setCheckable(paramMenuItemImpl.isCheckable());
    setChecked(paramMenuItemImpl.isChecked());
    setEnabled(paramMenuItemImpl.isEnabled());
    setTitle(paramMenuItemImpl.getTitle());
    setIcon(paramMenuItemImpl.getIcon());
    setActionView(paramMenuItemImpl.getActionView());
    setContentDescription(paramMenuItemImpl.getContentDescription());
    TooltipCompat.setTooltipText(this, paramMenuItemImpl.getTooltipText());
    adjustAppearance();
  }
  
  protected int[] onCreateDrawableState(int paramInt) {
    int[] arrayOfInt = super.onCreateDrawableState(paramInt + 1);
    if (this.mItemData != null && this.mItemData.isCheckable() && this.mItemData.isChecked())
      mergeDrawableStates(arrayOfInt, CHECKED_STATE_SET); 
    return arrayOfInt;
  }
  
  public boolean prefersCondensedTitle() { return false; }
  
  public void recycle() {
    if (this.mActionArea != null)
      this.mActionArea.removeAllViews(); 
    this.mTextView.setCompoundDrawables(null, null, null, null);
  }
  
  public void setCheckable(boolean paramBoolean) {
    refreshDrawableState();
    if (this.mCheckable != paramBoolean) {
      this.mCheckable = paramBoolean;
      this.mAccessibilityDelegate.sendAccessibilityEvent(this.mTextView, 2048);
    } 
  }
  
  public void setChecked(boolean paramBoolean) {
    refreshDrawableState();
    this.mTextView.setChecked(paramBoolean);
  }
  
  public void setIcon(Drawable paramDrawable) {
    if (paramDrawable != null) {
      Drawable drawable = paramDrawable;
      if (this.mHasIconTintList) {
        Drawable.ConstantState constantState = paramDrawable.getConstantState();
        if (constantState != null)
          paramDrawable = constantState.newDrawable(); 
        drawable = DrawableCompat.wrap(paramDrawable).mutate();
        DrawableCompat.setTintList(drawable, this.mIconTintList);
      } 
      drawable.setBounds(0, 0, this.mIconSize, this.mIconSize);
      paramDrawable = drawable;
    } else if (this.mNeedsEmptyIcon) {
      if (this.mEmptyDrawable == null) {
        this.mEmptyDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.navigation_empty_icon, getContext().getTheme());
        if (this.mEmptyDrawable != null)
          this.mEmptyDrawable.setBounds(0, 0, this.mIconSize, this.mIconSize); 
      } 
      paramDrawable = this.mEmptyDrawable;
    } 
    TextViewCompat.setCompoundDrawablesRelative(this.mTextView, paramDrawable, null, null, null);
  }
  
  void setIconTintList(ColorStateList paramColorStateList) {
    boolean bool;
    this.mIconTintList = paramColorStateList;
    if (this.mIconTintList != null) {
      bool = true;
    } else {
      bool = false;
    } 
    this.mHasIconTintList = bool;
    if (this.mItemData != null)
      setIcon(this.mItemData.getIcon()); 
  }
  
  public void setNeedsEmptyIcon(boolean paramBoolean) { this.mNeedsEmptyIcon = paramBoolean; }
  
  public void setShortcut(boolean paramBoolean, char paramChar) {}
  
  public void setTextAppearance(int paramInt) { TextViewCompat.setTextAppearance(this.mTextView, paramInt); }
  
  public void setTextColor(ColorStateList paramColorStateList) { this.mTextView.setTextColor(paramColorStateList); }
  
  public void setTitle(CharSequence paramCharSequence) { this.mTextView.setText(paramCharSequence); }
  
  public boolean showsIcon() { return true; }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/design/internal/NavigationMenuItemView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */