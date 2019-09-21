package android.support.design.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.design.R;
import android.support.v7.widget.TintTypedArray;
import android.util.AttributeSet;
import android.view.View;

public final class TabItem extends View {
  final int mCustomLayout;
  
  final Drawable mIcon;
  
  final CharSequence mText;
  
  public TabItem(Context paramContext) { this(paramContext, null); }
  
  public TabItem(Context paramContext, AttributeSet paramAttributeSet) {
    super(paramContext, paramAttributeSet);
    TintTypedArray tintTypedArray = TintTypedArray.obtainStyledAttributes(paramContext, paramAttributeSet, R.styleable.TabItem);
    this.mText = tintTypedArray.getText(R.styleable.TabItem_android_text);
    this.mIcon = tintTypedArray.getDrawable(R.styleable.TabItem_android_icon);
    this.mCustomLayout = tintTypedArray.getResourceId(R.styleable.TabItem_android_layout, 0);
    tintTypedArray.recycle();
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/design/widget/TabItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */