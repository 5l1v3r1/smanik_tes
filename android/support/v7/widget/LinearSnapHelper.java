package android.support.v7.widget;

import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

public class LinearSnapHelper extends SnapHelper {
  private static final float INVALID_DISTANCE = 1.0F;
  
  @Nullable
  private OrientationHelper mHorizontalHelper;
  
  @Nullable
  private OrientationHelper mVerticalHelper;
  
  private float computeDistancePerChild(RecyclerView.LayoutManager paramLayoutManager, OrientationHelper paramOrientationHelper) {
    int k = paramLayoutManager.getChildCount();
    if (k == 0)
      return 1.0F; 
    byte b = 0;
    View view1 = null;
    int i = Integer.MAX_VALUE;
    View view2 = null;
    int j;
    for (j = Integer.MIN_VALUE; b < k; j = n) {
      View view4;
      int n;
      View view3 = paramLayoutManager.getChildAt(b);
      int m = paramLayoutManager.getPosition(view3);
      if (m == -1) {
        view4 = view1;
        n = j;
      } else {
        int i1 = i;
        if (m < i) {
          view1 = view3;
          i1 = m;
        } 
        view4 = view1;
        i = i1;
        n = j;
        if (m > j) {
          view2 = view3;
          n = m;
          i = i1;
          view4 = view1;
        } 
      } 
      b++;
      view1 = view4;
    } 
    if (view1 != null) {
      if (view2 == null)
        return 1.0F; 
      int m = Math.min(paramOrientationHelper.getDecoratedStart(view1), paramOrientationHelper.getDecoratedStart(view2));
      m = Math.max(paramOrientationHelper.getDecoratedEnd(view1), paramOrientationHelper.getDecoratedEnd(view2)) - m;
      return (m == 0) ? 1.0F : (m * 1.0F / (j - i + 1));
    } 
    return 1.0F;
  }
  
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
  
  private int estimateNextPositionDiffForFling(RecyclerView.LayoutManager paramLayoutManager, OrientationHelper paramOrientationHelper, int paramInt1, int paramInt2) {
    int[] arrayOfInt = calculateScrollDistance(paramInt1, paramInt2);
    float f = computeDistancePerChild(paramLayoutManager, paramOrientationHelper);
    if (f <= 0.0F)
      return 0; 
    if (Math.abs(arrayOfInt[0]) > Math.abs(arrayOfInt[1])) {
      paramInt1 = arrayOfInt[0];
    } else {
      paramInt1 = arrayOfInt[1];
    } 
    return Math.round(paramInt1 / f);
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
  
  public View findSnapView(RecyclerView.LayoutManager paramLayoutManager) { return paramLayoutManager.canScrollVertically() ? findCenterView(paramLayoutManager, getVerticalHelper(paramLayoutManager)) : (paramLayoutManager.canScrollHorizontally() ? findCenterView(paramLayoutManager, getHorizontalHelper(paramLayoutManager)) : null); }
  
  public int findTargetSnapPosition(RecyclerView.LayoutManager paramLayoutManager, int paramInt1, int paramInt2) {
    if (!(paramLayoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider))
      return -1; 
    int j = paramLayoutManager.getItemCount();
    if (j == 0)
      return -1; 
    View view = findSnapView(paramLayoutManager);
    if (view == null)
      return -1; 
    int k = paramLayoutManager.getPosition(view);
    if (k == -1)
      return -1; 
    RecyclerView.SmoothScroller.ScrollVectorProvider scrollVectorProvider = (RecyclerView.SmoothScroller.ScrollVectorProvider)paramLayoutManager;
    int i = j - 1;
    PointF pointF = scrollVectorProvider.computeScrollVectorForPosition(i);
    if (pointF == null)
      return -1; 
    if (paramLayoutManager.canScrollHorizontally()) {
      int m = estimateNextPositionDiffForFling(paramLayoutManager, getHorizontalHelper(paramLayoutManager), paramInt1, 0);
      paramInt1 = m;
      if (pointF.x < 0.0F)
        paramInt1 = -m; 
    } else {
      paramInt1 = 0;
    } 
    if (paramLayoutManager.canScrollVertically()) {
      int m = estimateNextPositionDiffForFling(paramLayoutManager, getVerticalHelper(paramLayoutManager), 0, paramInt2);
      paramInt2 = m;
      if (pointF.y < 0.0F)
        paramInt2 = -m; 
    } else {
      paramInt2 = 0;
    } 
    if (paramLayoutManager.canScrollVertically())
      paramInt1 = paramInt2; 
    if (paramInt1 == 0)
      return -1; 
    paramInt2 = k + paramInt1;
    paramInt1 = paramInt2;
    if (paramInt2 < 0)
      paramInt1 = 0; 
    paramInt2 = paramInt1;
    if (paramInt1 >= j)
      paramInt2 = i; 
    return paramInt2;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/LinearSnapHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */