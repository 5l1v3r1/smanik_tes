package android.support.v7.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.StyleRes;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.view.menu.MenuView;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;

public class ActionMenuView extends LinearLayoutCompat implements MenuBuilder.ItemInvoker, MenuView {
  static final int GENERATED_ITEM_PADDING = 4;
  
  static final int MIN_CELL_SIZE = 56;
  
  private static final String TAG = "ActionMenuView";
  
  private MenuPresenter.Callback mActionMenuPresenterCallback;
  
  private boolean mFormatItems;
  
  private int mFormatItemsWidth;
  
  private int mGeneratedItemPadding;
  
  private MenuBuilder mMenu;
  
  MenuBuilder.Callback mMenuBuilderCallback;
  
  private int mMinCellSize;
  
  OnMenuItemClickListener mOnMenuItemClickListener;
  
  private Context mPopupContext;
  
  private int mPopupTheme;
  
  private ActionMenuPresenter mPresenter;
  
  private boolean mReserveOverflow;
  
  public ActionMenuView(Context paramContext) { this(paramContext, null); }
  
  public ActionMenuView(Context paramContext, AttributeSet paramAttributeSet) {
    super(paramContext, paramAttributeSet);
    setBaselineAligned(false);
    float f = (paramContext.getResources().getDisplayMetrics()).density;
    this.mMinCellSize = (int)(56.0F * f);
    this.mGeneratedItemPadding = (int)(f * 4.0F);
    this.mPopupContext = paramContext;
    this.mPopupTheme = 0;
  }
  
  static int measureChildForCells(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    Object object;
    LayoutParams layoutParams = (LayoutParams)paramView.getLayoutParams();
    int i = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt3) - paramInt4, View.MeasureSpec.getMode(paramInt3));
    if (paramView instanceof ActionMenuItemView) {
      object = (ActionMenuItemView)paramView;
    } else {
      object = null;
    } 
    byte b2 = 0;
    if (object != null && object.hasText()) {
      paramInt3 = 1;
    } else {
      paramInt3 = 0;
    } 
    paramInt4 = 2;
    if (paramInt2 > 0 && (paramInt3 == 0 || paramInt2 >= 2)) {
      paramView.measure(View.MeasureSpec.makeMeasureSpec(paramInt2 * paramInt1, -2147483648), i);
      int k = paramView.getMeasuredWidth();
      int j = k / paramInt1;
      paramInt2 = j;
      if (k % paramInt1 != 0)
        paramInt2 = j + 1; 
      if (paramInt3 != 0 && paramInt2 < 2)
        paramInt2 = paramInt4; 
    } else {
      paramInt2 = 0;
    } 
    byte b1 = b2;
    if (!layoutParams.isOverflowButton) {
      b1 = b2;
      if (paramInt3 != 0)
        b1 = 1; 
    } 
    layoutParams.expandable = b1;
    layoutParams.cellsUsed = paramInt2;
    paramView.measure(View.MeasureSpec.makeMeasureSpec(paramInt1 * paramInt2, 1073741824), i);
    return paramInt2;
  }
  
  private void onMeasureExactFormat(int paramInt1, int paramInt2) {
    int i8 = View.MeasureSpec.getMode(paramInt2);
    paramInt1 = View.MeasureSpec.getSize(paramInt1);
    int m = View.MeasureSpec.getSize(paramInt2);
    int i = getPaddingLeft();
    int j = getPaddingRight();
    int i5 = getPaddingTop() + getPaddingBottom();
    int i6 = getChildMeasureSpec(paramInt2, i5, -2);
    int i2 = paramInt1 - i + j;
    paramInt1 = i2 / this.mMinCellSize;
    paramInt2 = this.mMinCellSize;
    if (paramInt1 == 0) {
      setMeasuredDimension(i2, 0);
      return;
    } 
    int i9 = this.mMinCellSize + i2 % paramInt2 / paramInt1;
    int i7 = getChildCount();
    byte b = 0;
    int k = 0;
    i = 0;
    int i4 = 0;
    int i3 = 0;
    j = 0;
    long l = 0L;
    paramInt2 = m;
    while (b < i7) {
      int i11;
      int i10;
      View view = getChildAt(b);
      if (view.getVisibility() == 8) {
        i10 = i4;
        i11 = i3;
      } else {
        boolean bool = view instanceof ActionMenuItemView;
        i10 = i4 + true;
        if (bool)
          view.setPadding(this.mGeneratedItemPadding, 0, this.mGeneratedItemPadding, 0); 
        LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        layoutParams.expanded = false;
        layoutParams.extraPixels = 0;
        layoutParams.cellsUsed = 0;
        layoutParams.expandable = false;
        layoutParams.leftMargin = 0;
        layoutParams.rightMargin = 0;
        if (bool && ((ActionMenuItemView)view).hasText()) {
          bool = true;
        } else {
          bool = false;
        } 
        layoutParams.preventEdgeOffset = bool;
        if (layoutParams.isOverflowButton) {
          m = 1;
        } else {
          m = paramInt1;
        } 
        i4 = measureChildForCells(view, i9, m, i6, i5);
        i11 = Math.max(i3, i4);
        m = j;
        if (layoutParams.expandable)
          m = j + 1; 
        if (layoutParams.isOverflowButton)
          i = 1; 
        paramInt1 -= i4;
        k = Math.max(k, view.getMeasuredHeight());
        if (i4 == 1) {
          long l1 = (1 << b);
          l |= l1;
          j = m;
        } else {
          j = m;
        } 
      } 
      b++;
      i4 = i10;
      i3 = i11;
    } 
    if (i != 0 && i4 == 2) {
      b = 1;
    } else {
      b = 0;
    } 
    int i1 = 0;
    i5 = paramInt1;
    int n = i7;
    m = i6;
    for (paramInt1 = i1; j > 0 && i5 > 0; paramInt1 = 1) {
      i7 = 0;
      int i10 = 0;
      i6 = Integer.MAX_VALUE;
      long l1;
      for (l1 = 0L; i7 < n; l1 = l2) {
        long l2;
        int i11;
        LayoutParams layoutParams = (LayoutParams)getChildAt(i7).getLayoutParams();
        if (!layoutParams.expandable) {
          i1 = i10;
          i11 = i6;
          l2 = l1;
        } else if (layoutParams.cellsUsed < i6) {
          i11 = layoutParams.cellsUsed;
          l2 = 1L << i7;
          i1 = 1;
        } else {
          i1 = i10;
          i11 = i6;
          l2 = l1;
          if (layoutParams.cellsUsed == i6) {
            i1 = i10 + 1;
            l2 = l1 | 1L << i7;
            i11 = i6;
          } 
        } 
        i7++;
        i10 = i1;
        i6 = i11;
      } 
      l |= l1;
      if (i10 > i5)
        break; 
      paramInt1 = 0;
      while (paramInt1 < n) {
        long l2;
        View view = getChildAt(paramInt1);
        LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        long l3 = (1 << paramInt1);
        if ((l1 & l3) == 0L) {
          i1 = i5;
          l2 = l;
          if (layoutParams.cellsUsed == i6 + 1) {
            l2 = l | l3;
            i1 = i5;
          } 
        } else {
          if (b != 0 && layoutParams.preventEdgeOffset && i5 == 1)
            view.setPadding(this.mGeneratedItemPadding + i9, 0, this.mGeneratedItemPadding, 0); 
          layoutParams.cellsUsed++;
          layoutParams.expanded = true;
          i1 = i5 - 1;
          l2 = l;
        } 
        paramInt1++;
        i5 = i1;
        l = l2;
      } 
    } 
    if (i == 0 && i4 == 1) {
      i = 1;
    } else {
      i = 0;
    } 
    if (i5 > 0 && l != 0L && (i5 < i4 - 1 || i != 0 || i3 > 1)) {
      float f = Long.bitCount(l);
      if (i == 0) {
        float f1;
        if ((l & 0x1L) != 0L) {
          f1 = f;
          if (!((LayoutParams)getChildAt(0).getLayoutParams()).preventEdgeOffset)
            f1 = f - 0.5F; 
        } else {
          f1 = f;
        } 
        i = n - 1;
        f = f1;
        if ((l & (1 << i)) != 0L) {
          f = f1;
          if (!((LayoutParams)getChildAt(i).getLayoutParams()).preventEdgeOffset)
            f = f1 - 0.5F; 
        } 
      } 
      if (f > 0.0F) {
        i = (int)((i5 * i9) / f);
      } else {
        i = 0;
      } 
      i3 = n;
      j = 0;
      while (true) {
        i1 = paramInt1;
        if (j < i3) {
          if ((l & (1 << j)) == 0L) {
            i1 = paramInt1;
          } else {
            View view = getChildAt(j);
            LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
            if (view instanceof ActionMenuItemView) {
              layoutParams.extraPixels = i;
              layoutParams.expanded = true;
              if (j == 0 && !layoutParams.preventEdgeOffset)
                layoutParams.leftMargin = -i / 2; 
            } else if (layoutParams.isOverflowButton) {
              layoutParams.extraPixels = i;
              layoutParams.expanded = true;
              layoutParams.rightMargin = -i / 2;
            } else {
              if (j != 0)
                layoutParams.leftMargin = i / 2; 
              i1 = paramInt1;
              if (j != i3 - 1) {
                layoutParams.rightMargin = i / 2;
                i1 = paramInt1;
              } 
              j++;
              paramInt1 = i1;
            } 
            i1 = 1;
          } 
        } else {
          break;
        } 
        j++;
        paramInt1 = i1;
      } 
    } else {
      i1 = paramInt1;
    } 
    paramInt1 = 0;
    if (i1 != 0)
      while (paramInt1 < n) {
        View view = getChildAt(paramInt1);
        LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        if (layoutParams.expanded)
          view.measure(View.MeasureSpec.makeMeasureSpec(layoutParams.cellsUsed * i9 + layoutParams.extraPixels, 1073741824), m); 
        paramInt1++;
      }  
    if (i8 != 1073741824)
      paramInt2 = k; 
    setMeasuredDimension(i2, paramInt2);
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams) { return (paramLayoutParams != null && paramLayoutParams instanceof LayoutParams); }
  
  public void dismissPopupMenus() {
    if (this.mPresenter != null)
      this.mPresenter.dismissPopupMenus(); 
  }
  
  public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent) { return false; }
  
  protected LayoutParams generateDefaultLayoutParams() {
    LayoutParams layoutParams = new LayoutParams(-2, -2);
    layoutParams.gravity = 16;
    return layoutParams;
  }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet) { return new LayoutParams(getContext(), paramAttributeSet); }
  
  protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams) {
    if (paramLayoutParams != null) {
      LayoutParams layoutParams;
      if (paramLayoutParams instanceof LayoutParams) {
        layoutParams = new LayoutParams((LayoutParams)paramLayoutParams);
      } else {
        layoutParams = new LayoutParams(layoutParams);
      } 
      if (layoutParams.gravity <= 0)
        layoutParams.gravity = 16; 
      return layoutParams;
    } 
    return generateDefaultLayoutParams();
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public LayoutParams generateOverflowButtonLayoutParams() {
    LayoutParams layoutParams = generateDefaultLayoutParams();
    layoutParams.isOverflowButton = true;
    return layoutParams;
  }
  
  public Menu getMenu() {
    if (this.mMenu == null) {
      ActionMenuPresenterCallback actionMenuPresenterCallback = getContext();
      this.mMenu = new MenuBuilder(actionMenuPresenterCallback);
      this.mMenu.setCallback(new MenuBuilderCallback());
      this.mPresenter = new ActionMenuPresenter(actionMenuPresenterCallback);
      this.mPresenter.setReserveOverflow(true);
      ActionMenuPresenter actionMenuPresenter = this.mPresenter;
      if (this.mActionMenuPresenterCallback != null) {
        MenuPresenter.Callback callback = this.mActionMenuPresenterCallback;
      } else {
        actionMenuPresenterCallback = new ActionMenuPresenterCallback();
      } 
      actionMenuPresenter.setCallback(actionMenuPresenterCallback);
      this.mMenu.addMenuPresenter(this.mPresenter, this.mPopupContext);
      this.mPresenter.setMenuView(this);
    } 
    return this.mMenu;
  }
  
  @Nullable
  public Drawable getOverflowIcon() {
    getMenu();
    return this.mPresenter.getOverflowIcon();
  }
  
  public int getPopupTheme() { return this.mPopupTheme; }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public int getWindowAnimations() { return 0; }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  protected boolean hasSupportDividerBeforeChildAt(int paramInt) {
    byte b = 0;
    if (paramInt == 0)
      return false; 
    View view1 = getChildAt(paramInt - 1);
    View view2 = getChildAt(paramInt);
    boolean bool1 = b;
    if (paramInt < getChildCount()) {
      bool1 = b;
      if (view1 instanceof ActionMenuChildView)
        bool1 = false | ((ActionMenuChildView)view1).needsDividerAfter(); 
    } 
    boolean bool2 = bool1;
    if (paramInt > 0) {
      bool2 = bool1;
      if (view2 instanceof ActionMenuChildView)
        bool2 = bool1 | ((ActionMenuChildView)view2).needsDividerBefore(); 
    } 
    return bool2;
  }
  
  public boolean hideOverflowMenu() { return (this.mPresenter != null && this.mPresenter.hideOverflowMenu()); }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public void initialize(MenuBuilder paramMenuBuilder) { this.mMenu = paramMenuBuilder; }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public boolean invokeItem(MenuItemImpl paramMenuItemImpl) { return this.mMenu.performItemAction(paramMenuItemImpl, 0); }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public boolean isOverflowMenuShowPending() { return (this.mPresenter != null && this.mPresenter.isOverflowMenuShowPending()); }
  
  public boolean isOverflowMenuShowing() { return (this.mPresenter != null && this.mPresenter.isOverflowMenuShowing()); }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public boolean isOverflowReserved() { return this.mReserveOverflow; }
  
  public void onConfigurationChanged(Configuration paramConfiguration) {
    super.onConfigurationChanged(paramConfiguration);
    if (this.mPresenter != null) {
      this.mPresenter.updateMenuView(false);
      if (this.mPresenter.isOverflowMenuShowing()) {
        this.mPresenter.hideOverflowMenu();
        this.mPresenter.showOverflowMenu();
      } 
    } 
  }
  
  public void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    dismissPopupMenus();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (!this.mFormatItems) {
      super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    } 
    int k = getChildCount();
    int j = (paramInt4 - paramInt2) / 2;
    int m = getDividerWidth();
    int n = paramInt3 - paramInt1;
    paramInt1 = getPaddingRight();
    paramInt2 = getPaddingLeft();
    paramBoolean = ViewUtils.isLayoutRtl(this);
    paramInt1 = n - paramInt1 - paramInt2;
    paramInt2 = 0;
    paramInt4 = 0;
    paramInt3 = 0;
    while (paramInt2 < k) {
      View view = getChildAt(paramInt2);
      if (view.getVisibility() != 8) {
        LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        if (layoutParams.isOverflowButton) {
          int i2;
          int i1 = view.getMeasuredWidth();
          paramInt4 = i1;
          if (hasSupportDividerBeforeChildAt(paramInt2))
            paramInt4 = i1 + m; 
          int i3 = view.getMeasuredHeight();
          if (paramBoolean) {
            i2 = getPaddingLeft() + layoutParams.leftMargin;
            i1 = i2 + paramInt4;
          } else {
            i1 = getWidth() - getPaddingRight() - layoutParams.rightMargin;
            i2 = i1 - paramInt4;
          } 
          int i4 = j - i3 / 2;
          view.layout(i2, i4, i1, i3 + i4);
          paramInt1 -= paramInt4;
          paramInt4 = 1;
        } else {
          paramInt1 -= view.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
          hasSupportDividerBeforeChildAt(paramInt2);
          paramInt3++;
        } 
      } 
      paramInt2++;
    } 
    if (k == 1 && paramInt4 == 0) {
      View view = getChildAt(0);
      paramInt1 = view.getMeasuredWidth();
      paramInt2 = view.getMeasuredHeight();
      paramInt3 = n / 2 - paramInt1 / 2;
      paramInt4 = j - paramInt2 / 2;
      view.layout(paramInt3, paramInt4, paramInt1 + paramInt3, paramInt2 + paramInt4);
      return;
    } 
    paramInt2 = paramInt3 - (paramInt4 ^ true);
    if (paramInt2 > 0) {
      paramInt1 /= paramInt2;
    } else {
      paramInt1 = 0;
    } 
    paramInt4 = 0;
    paramInt3 = 0;
    int i = Math.max(0, paramInt1);
    if (paramBoolean) {
      paramInt2 = getWidth() - getPaddingRight();
      paramInt1 = paramInt3;
      while (paramInt1 < k) {
        View view = getChildAt(paramInt1);
        LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        paramInt3 = paramInt2;
        if (view.getVisibility() != 8)
          if (layoutParams.isOverflowButton) {
            paramInt3 = paramInt2;
          } else {
            paramInt2 -= layoutParams.rightMargin;
            paramInt3 = view.getMeasuredWidth();
            paramInt4 = view.getMeasuredHeight();
            int i1 = j - paramInt4 / 2;
            view.layout(paramInt2 - paramInt3, i1, paramInt2, paramInt4 + i1);
            paramInt3 = paramInt2 - paramInt3 + layoutParams.leftMargin + i;
          }  
        paramInt1++;
        paramInt2 = paramInt3;
      } 
    } else {
      paramInt2 = getPaddingLeft();
      paramInt1 = paramInt4;
      while (paramInt1 < k) {
        View view = getChildAt(paramInt1);
        LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        paramInt3 = paramInt2;
        if (view.getVisibility() != 8)
          if (layoutParams.isOverflowButton) {
            paramInt3 = paramInt2;
          } else {
            paramInt2 += layoutParams.leftMargin;
            paramInt3 = view.getMeasuredWidth();
            paramInt4 = view.getMeasuredHeight();
            int i1 = j - paramInt4 / 2;
            view.layout(paramInt2, i1, paramInt2 + paramInt3, paramInt4 + i1);
            paramInt3 = paramInt2 + paramInt3 + layoutParams.rightMargin + i;
          }  
        paramInt1++;
        paramInt2 = paramInt3;
      } 
    } 
  }
  
  protected void onMeasure(int paramInt1, int paramInt2) {
    boolean bool;
    boolean bool1 = this.mFormatItems;
    if (View.MeasureSpec.getMode(paramInt1) == 1073741824) {
      bool = true;
    } else {
      bool = false;
    } 
    this.mFormatItems = bool;
    if (bool1 != this.mFormatItems)
      this.mFormatItemsWidth = 0; 
    int i = View.MeasureSpec.getSize(paramInt1);
    if (this.mFormatItems && this.mMenu != null && i != this.mFormatItemsWidth) {
      this.mFormatItemsWidth = i;
      this.mMenu.onItemsChanged(true);
    } 
    int j = getChildCount();
    if (this.mFormatItems && j > 0) {
      onMeasureExactFormat(paramInt1, paramInt2);
      return;
    } 
    for (i = 0; i < j; i++) {
      LayoutParams layoutParams = (LayoutParams)getChildAt(i).getLayoutParams();
      layoutParams.rightMargin = 0;
      layoutParams.leftMargin = 0;
    } 
    super.onMeasure(paramInt1, paramInt2);
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public MenuBuilder peekMenu() { return this.mMenu; }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public void setExpandedActionViewsExclusive(boolean paramBoolean) { this.mPresenter.setExpandedActionViewsExclusive(paramBoolean); }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public void setMenuCallbacks(MenuPresenter.Callback paramCallback, MenuBuilder.Callback paramCallback1) {
    this.mActionMenuPresenterCallback = paramCallback;
    this.mMenuBuilderCallback = paramCallback1;
  }
  
  public void setOnMenuItemClickListener(OnMenuItemClickListener paramOnMenuItemClickListener) { this.mOnMenuItemClickListener = paramOnMenuItemClickListener; }
  
  public void setOverflowIcon(@Nullable Drawable paramDrawable) {
    getMenu();
    this.mPresenter.setOverflowIcon(paramDrawable);
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public void setOverflowReserved(boolean paramBoolean) { this.mReserveOverflow = paramBoolean; }
  
  public void setPopupTheme(@StyleRes int paramInt) {
    if (this.mPopupTheme != paramInt) {
      this.mPopupTheme = paramInt;
      if (paramInt == 0) {
        this.mPopupContext = getContext();
        return;
      } 
      this.mPopupContext = new ContextThemeWrapper(getContext(), paramInt);
    } 
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public void setPresenter(ActionMenuPresenter paramActionMenuPresenter) {
    this.mPresenter = paramActionMenuPresenter;
    this.mPresenter.setMenuView(this);
  }
  
  public boolean showOverflowMenu() { return (this.mPresenter != null && this.mPresenter.showOverflowMenu()); }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static interface ActionMenuChildView {
    boolean needsDividerAfter();
    
    boolean needsDividerBefore();
  }
  
  private static class ActionMenuPresenterCallback implements MenuPresenter.Callback {
    public void onCloseMenu(MenuBuilder param1MenuBuilder, boolean param1Boolean) {}
    
    public boolean onOpenSubMenu(MenuBuilder param1MenuBuilder) { return false; }
  }
  
  public static class LayoutParams extends LinearLayoutCompat.LayoutParams {
    @ExportedProperty
    public int cellsUsed;
    
    @ExportedProperty
    public boolean expandable;
    
    boolean expanded;
    
    @ExportedProperty
    public int extraPixels;
    
    @ExportedProperty
    public boolean isOverflowButton;
    
    @ExportedProperty
    public boolean preventEdgeOffset;
    
    public LayoutParams(int param1Int1, int param1Int2) {
      super(param1Int1, param1Int2);
      this.isOverflowButton = false;
    }
    
    LayoutParams(int param1Int1, int param1Int2, boolean param1Boolean) {
      super(param1Int1, param1Int2);
      this.isOverflowButton = param1Boolean;
    }
    
    public LayoutParams(Context param1Context, AttributeSet param1AttributeSet) { super(param1Context, param1AttributeSet); }
    
    public LayoutParams(LayoutParams param1LayoutParams) {
      super(param1LayoutParams);
      this.isOverflowButton = param1LayoutParams.isOverflowButton;
    }
    
    public LayoutParams(ViewGroup.LayoutParams param1LayoutParams) { super(param1LayoutParams); }
  }
  
  private class MenuBuilderCallback implements MenuBuilder.Callback {
    public boolean onMenuItemSelected(MenuBuilder param1MenuBuilder, MenuItem param1MenuItem) { return (ActionMenuView.this.mOnMenuItemClickListener != null && ActionMenuView.this.mOnMenuItemClickListener.onMenuItemClick(param1MenuItem)); }
    
    public void onMenuModeChange(MenuBuilder param1MenuBuilder) {
      if (ActionMenuView.this.mMenuBuilderCallback != null)
        ActionMenuView.this.mMenuBuilderCallback.onMenuModeChange(param1MenuBuilder); 
    }
  }
  
  public static interface OnMenuItemClickListener {
    boolean onMenuItemClick(MenuItem param1MenuItem);
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/ActionMenuView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */