package android.support.v7.view.menu;

import android.content.Context;
import android.support.annotation.RestrictTo;
import android.support.v7.widget.TintTypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public final class ExpandedMenuView extends ListView implements MenuBuilder.ItemInvoker, MenuView, AdapterView.OnItemClickListener {
  private static final int[] TINT_ATTRS = { 16842964, 16843049 };
  
  private int mAnimations;
  
  private MenuBuilder mMenu;
  
  public ExpandedMenuView(Context paramContext, AttributeSet paramAttributeSet) { this(paramContext, paramAttributeSet, 16842868); }
  
  public ExpandedMenuView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
    super(paramContext, paramAttributeSet);
    setOnItemClickListener(this);
    TintTypedArray tintTypedArray = TintTypedArray.obtainStyledAttributes(paramContext, paramAttributeSet, TINT_ATTRS, paramInt, 0);
    if (tintTypedArray.hasValue(0))
      setBackgroundDrawable(tintTypedArray.getDrawable(0)); 
    if (tintTypedArray.hasValue(1))
      setDivider(tintTypedArray.getDrawable(1)); 
    tintTypedArray.recycle();
  }
  
  public int getWindowAnimations() { return this.mAnimations; }
  
  public void initialize(MenuBuilder paramMenuBuilder) { this.mMenu = paramMenuBuilder; }
  
  public boolean invokeItem(MenuItemImpl paramMenuItemImpl) { return this.mMenu.performItemAction(paramMenuItemImpl, 0); }
  
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    setChildrenDrawingCacheEnabled(false);
  }
  
  public void onItemClick(AdapterView paramAdapterView, View paramView, int paramInt, long paramLong) { invokeItem((MenuItemImpl)getAdapter().getItem(paramInt)); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/view/menu/ExpandedMenuView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */