package android.support.v7.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;

public class PagerSnapHelper extends SnapHelper {
  private static final int MAX_SCROLL_ON_FLING_DURATION = 100;
  
  @Nullable
  private OrientationHelper mHorizontalHelper;
  
  @Nullable
  private OrientationHelper mVerticalHelper;
  
  private int distanceToCenter(@NonNull RecyclerView.LayoutManager paramLayoutManager, @NonNull View paramView, OrientationHelper paramOrientationHelper) {
    int i;
    int j = paramOrientationHelper.getDecoratedStart(paramView);
    int k = paramOrientationHelper.getDecoratedMeasurement(paramView) / 2;
    if (paramLayoutManager.getClipToPadding()) {
      i = paramOrientationHelper.getStartAfterPadding() + paramOrientationHelper.getTotalSpace() / 2;
    } else {
      i = paramOrientationHelper.getEnd() / 2;
    } 
    return j + k - i;
  }
  
  @Nullable
  private View findCenterView(RecyclerView.LayoutManager paramLayoutManager, OrientationHelper paramOrientationHelper) {
    int i;
    int k = paramLayoutManager.getChildCount();
    View view = null;
    if (k == 0)
      return null; 
    if (paramLayoutManager.getClipToPadding()) {
      i = paramOrientationHelper.getStartAfterPadding() + paramOrientationHelper.getTotalSpace() / 2;
    } else {
      i = paramOrientationHelper.getEnd() / 2;
    } 
    int j = Integer.MAX_VALUE;
    byte b = 0;
    while (b < k) {
      View view1 = paramLayoutManager.getChildAt(b);
      int n = Math.abs(paramOrientationHelper.getDecoratedStart(view1) + paramOrientationHelper.getDecoratedMeasurement(view1) / 2 - i);
      int m = j;
      if (n < j) {
        view = view1;
        m = n;
      } 
      b++;
      j = m;
    } 
    return view;
  }
  
  @Nullable
  private View findStartView(RecyclerView.LayoutManager paramLayoutManager, OrientationHelper paramOrientationHelper) {
    int j = paramLayoutManager.getChildCount();
    View view = null;
    if (j == 0)
      return null; 
    int i = Integer.MAX_VALUE;
    byte b = 0;
    while (b < j) {
      View view1 = paramLayoutManager.getChildAt(b);
      int m = paramOrientationHelper.getDecoratedStart(view1);
      int k = i;
      if (m < i) {
        view = view1;
        k = m;
      } 
      b++;
      i = k;
    } 
    return view;
  }
  
  @NonNull
  private OrientationHelper getHorizontalHelper(@NonNull RecyclerView.LayoutManager paramLayoutManager) {
    if (this.mHorizontalHelper == null || this.mHorizontalHelper.mLayoutManager != paramLayoutManager)
      this.mHorizontalHelper = OrientationHelper.createHorizontalHelper(paramLayoutManager); 
    return this.mHorizontalHelper;
  }
  
  @NonNull
  private OrientationHelper getVerticalHelper(@NonNull RecyclerView.LayoutManager paramLayoutManager) {
    if (this.mVerticalHelper == null || this.mVerticalHelper.mLayoutManager != paramLayoutManager)
      this.mVerticalHelper = OrientationHelper.createVerticalHelper(paramLayoutManager); 
    return this.mVerticalHelper;
  }
  
  @Nullable
  public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager paramLayoutManager, @NonNull View paramView) {
    int[] arrayOfInt = new int[2];
    if (paramLayoutManager.canScrollHorizontally()) {
      arrayOfInt[0] = distanceToCenter(paramLayoutManager, paramView, getHorizontalHelper(paramLayoutManager));
    } else {
      arrayOfInt[0] = 0;
    } 
    if (paramLayoutManager.canScrollVertically()) {
      arrayOfInt[1] = distanceToCenter(paramLayoutManager, paramView, getVerticalHelper(paramLayoutManager));
      return arrayOfInt;
    } 
    arrayOfInt[1] = 0;
    return arrayOfInt;
  }
  
  protected LinearSmoothScroller createSnapScroller(RecyclerView.LayoutManager paramLayoutManager) { return !(paramLayoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider) ? null : new LinearSmoothScroller(this.mRecyclerView.getContext()) {
        protected float calculateSpeedPerPixel(DisplayMetrics param1DisplayMetrics) { return 100.0F / param1DisplayMetrics.densityDpi; }
        
        protected int calculateTimeForScrolling(int param1Int) { return Math.min(100, super.calculateTimeForScrolling(param1Int)); }
        
        protected void onTargetFound(View param1View, RecyclerView.State param1State, RecyclerView.SmoothScroller.Action param1Action) {
          int[] arrayOfInt = PagerSnapHelper.this.calculateDistanceToFinalSnap(PagerSnapHelper.this.mRecyclerView.getLayoutManager(), param1View);
          int i = arrayOfInt[0];
          int j = arrayOfInt[1];
          int k = calculateTimeForDeceleration(Math.max(Math.abs(i), Math.abs(j)));
          if (k > 0)
            param1Action.update(i, j, k, this.mDecelerateInterpolator); 
        }
      }; }
  
  @Nullable
  public View findSnapView(RecyclerView.LayoutManager paramLayoutManager) { return paramLayoutManager.canScrollVertically() ? findCenterView(paramLayoutManager, getVerticalHelper(paramLayoutManager)) : (paramLayoutManager.canScrollHorizontally() ? findCenterView(paramLayoutManager, getHorizontalHelper(paramLayoutManager)) : null); }
  
  public int findTargetSnapPosition(RecyclerView.LayoutManager paramLayoutManager, int paramInt1, int paramInt2) { // Byte code:
    //   0: aload_1
    //   1: invokevirtual getItemCount : ()I
    //   4: istore #6
    //   6: iload #6
    //   8: ifne -> 13
    //   11: iconst_m1
    //   12: ireturn
    //   13: aconst_null
    //   14: astore #8
    //   16: aload_1
    //   17: invokevirtual canScrollVertically : ()Z
    //   20: ifeq -> 38
    //   23: aload_0
    //   24: aload_1
    //   25: aload_0
    //   26: aload_1
    //   27: invokespecial getVerticalHelper : (Landroid/support/v7/widget/RecyclerView$LayoutManager;)Landroid/support/v7/widget/OrientationHelper;
    //   30: invokespecial findStartView : (Landroid/support/v7/widget/RecyclerView$LayoutManager;Landroid/support/v7/widget/OrientationHelper;)Landroid/view/View;
    //   33: astore #8
    //   35: goto -> 57
    //   38: aload_1
    //   39: invokevirtual canScrollHorizontally : ()Z
    //   42: ifeq -> 57
    //   45: aload_0
    //   46: aload_1
    //   47: aload_0
    //   48: aload_1
    //   49: invokespecial getHorizontalHelper : (Landroid/support/v7/widget/RecyclerView$LayoutManager;)Landroid/support/v7/widget/OrientationHelper;
    //   52: invokespecial findStartView : (Landroid/support/v7/widget/RecyclerView$LayoutManager;Landroid/support/v7/widget/OrientationHelper;)Landroid/view/View;
    //   55: astore #8
    //   57: aload #8
    //   59: ifnonnull -> 64
    //   62: iconst_m1
    //   63: ireturn
    //   64: aload_1
    //   65: aload #8
    //   67: invokevirtual getPosition : (Landroid/view/View;)I
    //   70: istore #4
    //   72: iload #4
    //   74: iconst_m1
    //   75: if_icmpne -> 80
    //   78: iconst_m1
    //   79: ireturn
    //   80: aload_1
    //   81: invokevirtual canScrollHorizontally : ()Z
    //   84: istore #7
    //   86: iconst_0
    //   87: istore #5
    //   89: iload #7
    //   91: ifeq -> 108
    //   94: iload_2
    //   95: ifle -> 103
    //   98: iconst_1
    //   99: istore_2
    //   100: goto -> 115
    //   103: iconst_0
    //   104: istore_2
    //   105: goto -> 115
    //   108: iload_3
    //   109: ifle -> 103
    //   112: goto -> 98
    //   115: iload #5
    //   117: istore_3
    //   118: aload_1
    //   119: instanceof android/support/v7/widget/RecyclerView$SmoothScroller$ScrollVectorProvider
    //   122: ifeq -> 169
    //   125: aload_1
    //   126: checkcast android/support/v7/widget/RecyclerView$SmoothScroller$ScrollVectorProvider
    //   129: iload #6
    //   131: iconst_1
    //   132: isub
    //   133: invokeinterface computeScrollVectorForPosition : (I)Landroid/graphics/PointF;
    //   138: astore_1
    //   139: iload #5
    //   141: istore_3
    //   142: aload_1
    //   143: ifnull -> 169
    //   146: aload_1
    //   147: getfield x : F
    //   150: fconst_0
    //   151: fcmpg
    //   152: iflt -> 167
    //   155: iload #5
    //   157: istore_3
    //   158: aload_1
    //   159: getfield y : F
    //   162: fconst_0
    //   163: fcmpg
    //   164: ifge -> 169
    //   167: iconst_1
    //   168: istore_3
    //   169: iload_3
    //   170: ifeq -> 185
    //   173: iload #4
    //   175: istore_3
    //   176: iload_2
    //   177: ifeq -> 197
    //   180: iload #4
    //   182: iconst_1
    //   183: isub
    //   184: ireturn
    //   185: iload #4
    //   187: istore_3
    //   188: iload_2
    //   189: ifeq -> 197
    //   192: iload #4
    //   194: iconst_1
    //   195: iadd
    //   196: istore_3
    //   197: iload_3
    //   198: ireturn }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/PagerSnapHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */