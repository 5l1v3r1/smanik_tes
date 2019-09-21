package android.support.v7.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class AlertDialogLayout extends LinearLayoutCompat {
  public AlertDialogLayout(@Nullable Context paramContext) { super(paramContext); }
  
  public AlertDialogLayout(@Nullable Context paramContext, @Nullable AttributeSet paramAttributeSet) { super(paramContext, paramAttributeSet); }
  
  private void forceUniformWidth(int paramInt1, int paramInt2) {
    int i = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824);
    for (byte b = 0; b < paramInt1; b++) {
      View view = getChildAt(b);
      if (view.getVisibility() != 8) {
        LinearLayoutCompat.LayoutParams layoutParams = (LinearLayoutCompat.LayoutParams)view.getLayoutParams();
        if (layoutParams.width == -1) {
          int j = layoutParams.height;
          layoutParams.height = view.getMeasuredHeight();
          measureChildWithMargins(view, i, 0, paramInt2, 0);
          layoutParams.height = j;
        } 
      } 
    } 
  }
  
  private static int resolveMinimumHeight(View paramView) {
    int i = ViewCompat.getMinimumHeight(paramView);
    if (i > 0)
      return i; 
    if (paramView instanceof ViewGroup) {
      ViewGroup viewGroup = (ViewGroup)paramView;
      if (viewGroup.getChildCount() == 1)
        return resolveMinimumHeight(viewGroup.getChildAt(0)); 
    } 
    return 0;
  }
  
  private void setChildFrame(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramView.layout(paramInt1, paramInt2, paramInt3 + paramInt1, paramInt4 + paramInt2); }
  
  private boolean tryOnMeasure(int paramInt1, int paramInt2) {
    int i2;
    int i1;
    int i5 = getChildCount();
    View view4 = null;
    View view1 = view4;
    View view2 = view1;
    int i = 0;
    View view3 = view1;
    while (i < i5) {
      view1 = getChildAt(i);
      if (view1.getVisibility() != 8) {
        j = view1.getId();
        if (j == R.id.topPanel) {
          view4 = view1;
        } else if (j == R.id.buttonPanel) {
          view3 = view1;
        } else if (j == R.id.contentPanel || j == R.id.customPanel) {
          if (view2 != null)
            return false; 
          view2 = view1;
        } else {
          return false;
        } 
      } 
      i++;
    } 
    int i7 = View.MeasureSpec.getMode(paramInt2);
    int n = View.MeasureSpec.getSize(paramInt2);
    int i6 = View.MeasureSpec.getMode(paramInt1);
    int k = getPaddingTop() + getPaddingBottom();
    if (view4 != null) {
      view4.measure(paramInt1, 0);
      k += view4.getMeasuredHeight();
      j = View.combineMeasuredStates(0, view4.getMeasuredState());
    } else {
      j = 0;
    } 
    if (view3 != null) {
      view3.measure(paramInt1, 0);
      i = resolveMinimumHeight(view3);
      i1 = view3.getMeasuredHeight() - i;
      k += i;
      j = View.combineMeasuredStates(j, view3.getMeasuredState());
    } else {
      i = 0;
      i1 = 0;
    } 
    if (view2 != null) {
      int i8;
      if (i7 == 0) {
        i8 = 0;
      } else {
        i8 = View.MeasureSpec.makeMeasureSpec(Math.max(0, n - k), i7);
      } 
      view2.measure(paramInt1, i8);
      i2 = view2.getMeasuredHeight();
      k += i2;
      j = View.combineMeasuredStates(j, view2.getMeasuredState());
    } else {
      i2 = 0;
    } 
    int i3 = n - k;
    n = j;
    int i4 = i3;
    int m = k;
    if (view3 != null) {
      i1 = Math.min(i3, i1);
      n = i3;
      m = i;
      if (i1 > 0) {
        n = i3 - i1;
        m = i + i1;
      } 
      view3.measure(paramInt1, View.MeasureSpec.makeMeasureSpec(m, 1073741824));
      m = k - i + view3.getMeasuredHeight();
      i = View.combineMeasuredStates(j, view3.getMeasuredState());
      i4 = n;
      n = i;
    } 
    int j = n;
    i = m;
    if (view2 != null) {
      j = n;
      i = m;
      if (i4 > 0) {
        view2.measure(paramInt1, View.MeasureSpec.makeMeasureSpec(i2 + i4, i7));
        i = m - i2 + view2.getMeasuredHeight();
        j = View.combineMeasuredStates(n, view2.getMeasuredState());
      } 
    } 
    k = 0;
    for (m = 0; k < i5; m = n) {
      view1 = getChildAt(k);
      n = m;
      if (view1.getVisibility() != 8)
        n = Math.max(m, view1.getMeasuredWidth()); 
      k++;
    } 
    setMeasuredDimension(View.resolveSizeAndState(m + getPaddingLeft() + getPaddingRight(), paramInt1, j), View.resolveSizeAndState(i, paramInt2, 0));
    if (i6 != 1073741824)
      forceUniformWidth(i5, paramInt2); 
    return true;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = getPaddingLeft();
    int j = paramInt3 - paramInt1;
    int k = getPaddingRight();
    int m = getPaddingRight();
    paramInt1 = getMeasuredHeight();
    int n = getChildCount();
    int i1 = getGravity();
    paramInt3 = i1 & 0x70;
    if (paramInt3 != 16) {
      if (paramInt3 != 80) {
        paramInt1 = getPaddingTop();
      } else {
        paramInt1 = getPaddingTop() + paramInt4 - paramInt2 - paramInt1;
      } 
    } else {
      paramInt3 = getPaddingTop();
      paramInt1 = (paramInt4 - paramInt2 - paramInt1) / 2 + paramInt3;
    } 
    Drawable drawable = getDividerDrawable();
    if (drawable == null) {
      paramInt3 = 0;
    } else {
      paramInt3 = drawable.getIntrinsicHeight();
    } 
    paramInt4 = 0;
    while (paramInt4 < n) {
      View view = getChildAt(paramInt4);
      paramInt2 = paramInt1;
      if (view != null) {
        paramInt2 = paramInt1;
        if (view.getVisibility() != 8) {
          int i3 = view.getMeasuredWidth();
          int i4 = view.getMeasuredHeight();
          LinearLayoutCompat.LayoutParams layoutParams = (LinearLayoutCompat.LayoutParams)view.getLayoutParams();
          int i2 = layoutParams.gravity;
          paramInt2 = i2;
          if (i2 < 0)
            paramInt2 = i1 & 0x800007; 
          paramInt2 = GravityCompat.getAbsoluteGravity(paramInt2, ViewCompat.getLayoutDirection(this)) & 0x7;
          if (paramInt2 != 1) {
            if (paramInt2 != 5) {
              paramInt2 = layoutParams.leftMargin + i;
            } else {
              paramInt2 = j - k - i3 - layoutParams.rightMargin;
            } 
          } else {
            paramInt2 = (j - i - m - i3) / 2 + i + layoutParams.leftMargin - layoutParams.rightMargin;
          } 
          i2 = paramInt1;
          if (hasDividerBeforeChildAt(paramInt4))
            i2 = paramInt1 + paramInt3; 
          paramInt1 = i2 + layoutParams.topMargin;
          setChildFrame(view, paramInt2, paramInt1, i3, i4);
          paramInt2 = paramInt1 + i4 + layoutParams.bottomMargin;
        } 
      } 
      paramInt4++;
      paramInt1 = paramInt2;
    } 
  }
  
  protected void onMeasure(int paramInt1, int paramInt2) {
    if (!tryOnMeasure(paramInt1, paramInt2))
      super.onMeasure(paramInt1, paramInt2); 
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/AlertDialogLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */