package android.support.v7.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {
  private static final int[] ATTRS = { 16843284 };
  
  public static final int HORIZONTAL = 0;
  
  private static final String TAG = "DividerItem";
  
  public static final int VERTICAL = 1;
  
  private final Rect mBounds = new Rect();
  
  private Drawable mDivider;
  
  private int mOrientation;
  
  public DividerItemDecoration(Context paramContext, int paramInt) {
    TypedArray typedArray = paramContext.obtainStyledAttributes(ATTRS);
    this.mDivider = typedArray.getDrawable(0);
    if (this.mDivider == null)
      Log.w("DividerItem", "@android:attr/listDivider was not set in the theme used for this DividerItemDecoration. Please set that attribute all call setDrawable()"); 
    typedArray.recycle();
    setOrientation(paramInt);
  }
  
  private void drawHorizontal(Canvas paramCanvas, RecyclerView paramRecyclerView) {
    int i;
    byte b1;
    paramCanvas.save();
    boolean bool = paramRecyclerView.getClipToPadding();
    byte b2 = 0;
    if (bool) {
      b1 = paramRecyclerView.getPaddingTop();
      i = paramRecyclerView.getHeight() - paramRecyclerView.getPaddingBottom();
      paramCanvas.clipRect(paramRecyclerView.getPaddingLeft(), b1, paramRecyclerView.getWidth() - paramRecyclerView.getPaddingRight(), i);
    } else {
      i = paramRecyclerView.getHeight();
      b1 = 0;
    } 
    int j = paramRecyclerView.getChildCount();
    while (b2 < j) {
      View view = paramRecyclerView.getChildAt(b2);
      paramRecyclerView.getLayoutManager().getDecoratedBoundsWithMargins(view, this.mBounds);
      int k = this.mBounds.right + Math.round(view.getTranslationX());
      int m = this.mDivider.getIntrinsicWidth();
      this.mDivider.setBounds(k - m, b1, k, i);
      this.mDivider.draw(paramCanvas);
      b2++;
    } 
    paramCanvas.restore();
  }
  
  private void drawVertical(Canvas paramCanvas, RecyclerView paramRecyclerView) {
    int i;
    byte b1;
    paramCanvas.save();
    boolean bool = paramRecyclerView.getClipToPadding();
    byte b2 = 0;
    if (bool) {
      b1 = paramRecyclerView.getPaddingLeft();
      i = paramRecyclerView.getWidth() - paramRecyclerView.getPaddingRight();
      paramCanvas.clipRect(b1, paramRecyclerView.getPaddingTop(), i, paramRecyclerView.getHeight() - paramRecyclerView.getPaddingBottom());
    } else {
      i = paramRecyclerView.getWidth();
      b1 = 0;
    } 
    int j = paramRecyclerView.getChildCount();
    while (b2 < j) {
      View view = paramRecyclerView.getChildAt(b2);
      paramRecyclerView.getDecoratedBoundsWithMargins(view, this.mBounds);
      int k = this.mBounds.bottom + Math.round(view.getTranslationY());
      int m = this.mDivider.getIntrinsicHeight();
      this.mDivider.setBounds(b1, k - m, i, k);
      this.mDivider.draw(paramCanvas);
      b2++;
    } 
    paramCanvas.restore();
  }
  
  public void getItemOffsets(Rect paramRect, View paramView, RecyclerView paramRecyclerView, RecyclerView.State paramState) {
    if (this.mDivider == null) {
      paramRect.set(0, 0, 0, 0);
      return;
    } 
    if (this.mOrientation == 1) {
      paramRect.set(0, 0, 0, this.mDivider.getIntrinsicHeight());
      return;
    } 
    paramRect.set(0, 0, this.mDivider.getIntrinsicWidth(), 0);
  }
  
  public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.State paramState) {
    if (paramRecyclerView.getLayoutManager() != null) {
      if (this.mDivider == null)
        return; 
      if (this.mOrientation == 1) {
        drawVertical(paramCanvas, paramRecyclerView);
        return;
      } 
      drawHorizontal(paramCanvas, paramRecyclerView);
      return;
    } 
  }
  
  public void setDrawable(@NonNull Drawable paramDrawable) {
    if (paramDrawable == null)
      throw new IllegalArgumentException("Drawable cannot be null."); 
    this.mDivider = paramDrawable;
  }
  
  public void setOrientation(int paramInt) {
    if (paramInt != 0 && paramInt != 1)
      throw new IllegalArgumentException("Invalid orientation. It should be either HORIZONTAL or VERTICAL"); 
    this.mOrientation = paramInt;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/DividerItemDecoration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */