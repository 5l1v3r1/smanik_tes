package android.support.v7.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

public abstract class SnapHelper extends RecyclerView.OnFlingListener {
  static final float MILLISECONDS_PER_INCH = 100.0F;
  
  private Scroller mGravityScroller;
  
  RecyclerView mRecyclerView;
  
  private final RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
      boolean mScrolled = false;
      
      public void onScrollStateChanged(RecyclerView param1RecyclerView, int param1Int) {
        super.onScrollStateChanged(param1RecyclerView, param1Int);
        if (param1Int == 0 && this.mScrolled) {
          this.mScrolled = false;
          SnapHelper.this.snapToTargetExistingView();
        } 
      }
      
      public void onScrolled(RecyclerView param1RecyclerView, int param1Int1, int param1Int2) {
        if (param1Int1 != 0 || param1Int2 != 0)
          this.mScrolled = true; 
      }
    };
  
  private void destroyCallbacks() {
    this.mRecyclerView.removeOnScrollListener(this.mScrollListener);
    this.mRecyclerView.setOnFlingListener(null);
  }
  
  private void setupCallbacks() {
    if (this.mRecyclerView.getOnFlingListener() != null)
      throw new IllegalStateException("An instance of OnFlingListener already set."); 
    this.mRecyclerView.addOnScrollListener(this.mScrollListener);
    this.mRecyclerView.setOnFlingListener(this);
  }
  
  private boolean snapFromFling(@NonNull RecyclerView.LayoutManager paramLayoutManager, int paramInt1, int paramInt2) {
    if (!(paramLayoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider))
      return false; 
    RecyclerView.SmoothScroller smoothScroller = createScroller(paramLayoutManager);
    if (smoothScroller == null)
      return false; 
    paramInt1 = findTargetSnapPosition(paramLayoutManager, paramInt1, paramInt2);
    if (paramInt1 == -1)
      return false; 
    smoothScroller.setTargetPosition(paramInt1);
    paramLayoutManager.startSmoothScroll(smoothScroller);
    return true;
  }
  
  public void attachToRecyclerView(@Nullable RecyclerView paramRecyclerView) throws IllegalStateException {
    if (this.mRecyclerView == paramRecyclerView)
      return; 
    if (this.mRecyclerView != null)
      destroyCallbacks(); 
    this.mRecyclerView = paramRecyclerView;
    if (this.mRecyclerView != null) {
      setupCallbacks();
      this.mGravityScroller = new Scroller(this.mRecyclerView.getContext(), new DecelerateInterpolator());
      snapToTargetExistingView();
    } 
  }
  
  @Nullable
  public abstract int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager paramLayoutManager, @NonNull View paramView);
  
  public int[] calculateScrollDistance(int paramInt1, int paramInt2) {
    this.mGravityScroller.fling(0, 0, paramInt1, paramInt2, -2147483648, 2147483647, -2147483648, 2147483647);
    return new int[] { this.mGravityScroller.getFinalX(), this.mGravityScroller.getFinalY() };
  }
  
  @Nullable
  protected RecyclerView.SmoothScroller createScroller(RecyclerView.LayoutManager paramLayoutManager) { return createSnapScroller(paramLayoutManager); }
  
  @Deprecated
  @Nullable
  protected LinearSmoothScroller createSnapScroller(RecyclerView.LayoutManager paramLayoutManager) { return !(paramLayoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider) ? null : new LinearSmoothScroller(this.mRecyclerView.getContext()) {
        protected float calculateSpeedPerPixel(DisplayMetrics param1DisplayMetrics) { return 100.0F / param1DisplayMetrics.densityDpi; }
        
        protected void onTargetFound(View param1View, RecyclerView.State param1State, RecyclerView.SmoothScroller.Action param1Action) {
          if (SnapHelper.this.mRecyclerView == null)
            return; 
          int[] arrayOfInt = SnapHelper.this.calculateDistanceToFinalSnap(SnapHelper.this.mRecyclerView.getLayoutManager(), param1View);
          int i = arrayOfInt[0];
          int j = arrayOfInt[1];
          int k = calculateTimeForDeceleration(Math.max(Math.abs(i), Math.abs(j)));
          if (k > 0)
            param1Action.update(i, j, k, this.mDecelerateInterpolator); 
        }
      }; }
  
  @Nullable
  public abstract View findSnapView(RecyclerView.LayoutManager paramLayoutManager);
  
  public abstract int findTargetSnapPosition(RecyclerView.LayoutManager paramLayoutManager, int paramInt1, int paramInt2);
  
  public boolean onFling(int paramInt1, int paramInt2) { // Byte code:
    //   0: aload_0
    //   1: getfield mRecyclerView : Landroid/support/v7/widget/RecyclerView;
    //   4: invokevirtual getLayoutManager : ()Landroid/support/v7/widget/RecyclerView$LayoutManager;
    //   7: astore #6
    //   9: iconst_0
    //   10: istore #5
    //   12: aload #6
    //   14: ifnonnull -> 19
    //   17: iconst_0
    //   18: ireturn
    //   19: aload_0
    //   20: getfield mRecyclerView : Landroid/support/v7/widget/RecyclerView;
    //   23: invokevirtual getAdapter : ()Landroid/support/v7/widget/RecyclerView$Adapter;
    //   26: ifnonnull -> 31
    //   29: iconst_0
    //   30: ireturn
    //   31: aload_0
    //   32: getfield mRecyclerView : Landroid/support/v7/widget/RecyclerView;
    //   35: invokevirtual getMinFlingVelocity : ()I
    //   38: istore_3
    //   39: iload_2
    //   40: invokestatic abs : (I)I
    //   43: iload_3
    //   44: if_icmpgt -> 59
    //   47: iload #5
    //   49: istore #4
    //   51: iload_1
    //   52: invokestatic abs : (I)I
    //   55: iload_3
    //   56: if_icmple -> 77
    //   59: iload #5
    //   61: istore #4
    //   63: aload_0
    //   64: aload #6
    //   66: iload_1
    //   67: iload_2
    //   68: invokespecial snapFromFling : (Landroid/support/v7/widget/RecyclerView$LayoutManager;II)Z
    //   71: ifeq -> 77
    //   74: iconst_1
    //   75: istore #4
    //   77: iload #4
    //   79: ireturn }
  
  void snapToTargetExistingView() {
    if (this.mRecyclerView == null)
      return; 
    RecyclerView.LayoutManager layoutManager = this.mRecyclerView.getLayoutManager();
    if (layoutManager == null)
      return; 
    View view = findSnapView(layoutManager);
    if (view == null)
      return; 
    int[] arrayOfInt = calculateDistanceToFinalSnap(layoutManager, view);
    if (arrayOfInt[0] != 0 || arrayOfInt[1] != 0)
      this.mRecyclerView.smoothScrollBy(arrayOfInt[0], arrayOfInt[1]); 
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/SnapHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */