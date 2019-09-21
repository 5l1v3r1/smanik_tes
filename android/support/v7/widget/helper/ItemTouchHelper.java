package android.support.v7.widget.helper;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.recyclerview.R;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import java.util.ArrayList;
import java.util.List;

public class ItemTouchHelper extends RecyclerView.ItemDecoration implements RecyclerView.OnChildAttachStateChangeListener {
  static final int ACTION_MODE_DRAG_MASK = 16711680;
  
  private static final int ACTION_MODE_IDLE_MASK = 255;
  
  static final int ACTION_MODE_SWIPE_MASK = 65280;
  
  public static final int ACTION_STATE_DRAG = 2;
  
  public static final int ACTION_STATE_IDLE = 0;
  
  public static final int ACTION_STATE_SWIPE = 1;
  
  static final int ACTIVE_POINTER_ID_NONE = -1;
  
  public static final int ANIMATION_TYPE_DRAG = 8;
  
  public static final int ANIMATION_TYPE_SWIPE_CANCEL = 4;
  
  public static final int ANIMATION_TYPE_SWIPE_SUCCESS = 2;
  
  static final boolean DEBUG = false;
  
  static final int DIRECTION_FLAG_COUNT = 8;
  
  public static final int DOWN = 2;
  
  public static final int END = 32;
  
  public static final int LEFT = 4;
  
  private static final int PIXELS_PER_SECOND = 1000;
  
  public static final int RIGHT = 8;
  
  public static final int START = 16;
  
  static final String TAG = "ItemTouchHelper";
  
  public static final int UP = 1;
  
  int mActionState = 0;
  
  int mActivePointerId = -1;
  
  Callback mCallback;
  
  private RecyclerView.ChildDrawingOrderCallback mChildDrawingOrderCallback = null;
  
  private List<Integer> mDistances;
  
  private long mDragScrollStartTimeInMs;
  
  float mDx;
  
  float mDy;
  
  GestureDetectorCompat mGestureDetector;
  
  float mInitialTouchX;
  
  float mInitialTouchY;
  
  private ItemTouchHelperGestureListener mItemTouchHelperGestureListener;
  
  float mMaxSwipeVelocity;
  
  private final RecyclerView.OnItemTouchListener mOnItemTouchListener = new RecyclerView.OnItemTouchListener() {
      public boolean onInterceptTouchEvent(RecyclerView param1RecyclerView, MotionEvent param1MotionEvent) {
        ItemTouchHelper.this.mGestureDetector.onTouchEvent(param1MotionEvent);
        int i = param1MotionEvent.getActionMasked();
        if (i == 0) {
          ItemTouchHelper.this.mActivePointerId = param1MotionEvent.getPointerId(0);
          ItemTouchHelper.this.mInitialTouchX = param1MotionEvent.getX();
          ItemTouchHelper.this.mInitialTouchY = param1MotionEvent.getY();
          ItemTouchHelper.this.obtainVelocityTracker();
          if (ItemTouchHelper.this.mSelected == null) {
            ItemTouchHelper.RecoverAnimation recoverAnimation = ItemTouchHelper.this.findAnimation(param1MotionEvent);
            if (recoverAnimation != null) {
              ItemTouchHelper itemTouchHelper = ItemTouchHelper.this;
              itemTouchHelper.mInitialTouchX -= recoverAnimation.mX;
              itemTouchHelper = ItemTouchHelper.this;
              itemTouchHelper.mInitialTouchY -= recoverAnimation.mY;
              ItemTouchHelper.this.endRecoverAnimation(recoverAnimation.mViewHolder, true);
              if (ItemTouchHelper.this.mPendingCleanup.remove(recoverAnimation.mViewHolder.itemView))
                ItemTouchHelper.this.mCallback.clearView(ItemTouchHelper.this.mRecyclerView, recoverAnimation.mViewHolder); 
              ItemTouchHelper.this.select(recoverAnimation.mViewHolder, recoverAnimation.mActionState);
              ItemTouchHelper.this.updateDxDy(param1MotionEvent, ItemTouchHelper.this.mSelectedFlags, 0);
            } 
          } 
        } else if (i == 3 || i == 1) {
          ItemTouchHelper.this.mActivePointerId = -1;
          ItemTouchHelper.this.select(null, 0);
        } else if (ItemTouchHelper.this.mActivePointerId != -1) {
          int j = param1MotionEvent.findPointerIndex(ItemTouchHelper.this.mActivePointerId);
          if (j >= 0)
            ItemTouchHelper.this.checkSelectForSwipe(i, param1MotionEvent, j); 
        } 
        if (ItemTouchHelper.this.mVelocityTracker != null)
          ItemTouchHelper.this.mVelocityTracker.addMovement(param1MotionEvent); 
        return (ItemTouchHelper.this.mSelected != null);
      }
      
      public void onRequestDisallowInterceptTouchEvent(boolean param1Boolean) {
        if (!param1Boolean)
          return; 
        ItemTouchHelper.this.select(null, 0);
      }
      
      public void onTouchEvent(RecyclerView param1RecyclerView, MotionEvent param1MotionEvent) {
        ItemTouchHelper.this.mGestureDetector.onTouchEvent(param1MotionEvent);
        if (ItemTouchHelper.this.mVelocityTracker != null)
          ItemTouchHelper.this.mVelocityTracker.addMovement(param1MotionEvent); 
        if (ItemTouchHelper.this.mActivePointerId == -1)
          return; 
        int i = param1MotionEvent.getActionMasked();
        int j = param1MotionEvent.findPointerIndex(ItemTouchHelper.this.mActivePointerId);
        if (j >= 0)
          ItemTouchHelper.this.checkSelectForSwipe(i, param1MotionEvent, j); 
        RecyclerView.ViewHolder viewHolder = ItemTouchHelper.this.mSelected;
        if (viewHolder == null)
          return; 
        byte b = 0;
        if (i != 6) {
          switch (i) {
            default:
              return;
            case 3:
              if (ItemTouchHelper.this.mVelocityTracker != null)
                ItemTouchHelper.this.mVelocityTracker.clear(); 
              break;
            case 2:
              if (j >= 0) {
                ItemTouchHelper.this.updateDxDy(param1MotionEvent, ItemTouchHelper.this.mSelectedFlags, j);
                ItemTouchHelper.this.moveIfNecessary(viewHolder);
                ItemTouchHelper.this.mRecyclerView.removeCallbacks(ItemTouchHelper.this.mScrollRunnable);
                ItemTouchHelper.this.mScrollRunnable.run();
                ItemTouchHelper.this.mRecyclerView.invalidate();
                return;
              } 
              return;
            case 1:
              break;
          } 
          ItemTouchHelper.this.select(null, 0);
          ItemTouchHelper.this.mActivePointerId = -1;
          return;
        } 
        i = param1MotionEvent.getActionIndex();
        if (param1MotionEvent.getPointerId(i) == ItemTouchHelper.this.mActivePointerId) {
          if (i == 0)
            b = 1; 
          ItemTouchHelper.this.mActivePointerId = param1MotionEvent.getPointerId(b);
          ItemTouchHelper.this.updateDxDy(param1MotionEvent, ItemTouchHelper.this.mSelectedFlags, i);
        } 
      }
    };
  
  View mOverdrawChild = null;
  
  int mOverdrawChildPosition = -1;
  
  final List<View> mPendingCleanup = new ArrayList();
  
  List<RecoverAnimation> mRecoverAnimations = new ArrayList();
  
  RecyclerView mRecyclerView;
  
  final Runnable mScrollRunnable = new Runnable() {
      public void run() {
        if (ItemTouchHelper.this.mSelected != null && ItemTouchHelper.this.scrollIfNecessary()) {
          if (ItemTouchHelper.this.mSelected != null)
            ItemTouchHelper.this.moveIfNecessary(ItemTouchHelper.this.mSelected); 
          ItemTouchHelper.this.mRecyclerView.removeCallbacks(ItemTouchHelper.this.mScrollRunnable);
          ViewCompat.postOnAnimation(ItemTouchHelper.this.mRecyclerView, this);
        } 
      }
    };
  
  RecyclerView.ViewHolder mSelected = null;
  
  int mSelectedFlags;
  
  float mSelectedStartX;
  
  float mSelectedStartY;
  
  private int mSlop;
  
  private List<RecyclerView.ViewHolder> mSwapTargets;
  
  float mSwipeEscapeVelocity;
  
  private final float[] mTmpPosition = new float[2];
  
  private Rect mTmpRect;
  
  VelocityTracker mVelocityTracker;
  
  public ItemTouchHelper(Callback paramCallback) { this.mCallback = paramCallback; }
  
  private void addChildDrawingOrderCallback() {
    if (Build.VERSION.SDK_INT >= 21)
      return; 
    if (this.mChildDrawingOrderCallback == null)
      this.mChildDrawingOrderCallback = new RecyclerView.ChildDrawingOrderCallback() {
          public int onGetChildDrawingOrder(int param1Int1, int param1Int2) {
            if (ItemTouchHelper.this.mOverdrawChild == null)
              return param1Int2; 
            int j = ItemTouchHelper.this.mOverdrawChildPosition;
            int i = j;
            if (j == -1) {
              i = ItemTouchHelper.this.mRecyclerView.indexOfChild(ItemTouchHelper.this.mOverdrawChild);
              ItemTouchHelper.this.mOverdrawChildPosition = i;
            } 
            return (param1Int2 == param1Int1 - 1) ? i : ((param1Int2 < i) ? param1Int2 : (param1Int2 + 1));
          }
        }; 
    this.mRecyclerView.setChildDrawingOrderCallback(this.mChildDrawingOrderCallback);
  }
  
  private int checkHorizontalSwipe(RecyclerView.ViewHolder paramViewHolder, int paramInt) {
    if ((paramInt & 0xC) != 0) {
      int i;
      float f1 = this.mDx;
      int j = 4;
      if (f1 > 0.0F) {
        i = 8;
      } else {
        i = 4;
      } 
      if (this.mVelocityTracker != null && this.mActivePointerId > -1) {
        this.mVelocityTracker.computeCurrentVelocity(1000, this.mCallback.getSwipeVelocityThreshold(this.mMaxSwipeVelocity));
        float f = this.mVelocityTracker.getXVelocity(this.mActivePointerId);
        f1 = this.mVelocityTracker.getYVelocity(this.mActivePointerId);
        if (f > 0.0F)
          j = 8; 
        f = Math.abs(f);
        if ((j & paramInt) != 0 && i == j && f >= this.mCallback.getSwipeEscapeVelocity(this.mSwipeEscapeVelocity) && f > Math.abs(f1))
          return j; 
      } 
      f1 = this.mRecyclerView.getWidth();
      float f2 = this.mCallback.getSwipeThreshold(paramViewHolder);
      if ((paramInt & i) != 0 && Math.abs(this.mDx) > f1 * f2)
        return i; 
    } 
    return 0;
  }
  
  private int checkVerticalSwipe(RecyclerView.ViewHolder paramViewHolder, int paramInt) {
    if ((paramInt & 0x3) != 0) {
      int i;
      float f1 = this.mDy;
      int j = 1;
      if (f1 > 0.0F) {
        i = 2;
      } else {
        i = 1;
      } 
      if (this.mVelocityTracker != null && this.mActivePointerId > -1) {
        this.mVelocityTracker.computeCurrentVelocity(1000, this.mCallback.getSwipeVelocityThreshold(this.mMaxSwipeVelocity));
        f1 = this.mVelocityTracker.getXVelocity(this.mActivePointerId);
        float f = this.mVelocityTracker.getYVelocity(this.mActivePointerId);
        if (f > 0.0F)
          j = 2; 
        f = Math.abs(f);
        if ((j & paramInt) != 0 && j == i && f >= this.mCallback.getSwipeEscapeVelocity(this.mSwipeEscapeVelocity) && f > Math.abs(f1))
          return j; 
      } 
      f1 = this.mRecyclerView.getHeight();
      float f2 = this.mCallback.getSwipeThreshold(paramViewHolder);
      if ((paramInt & i) != 0 && Math.abs(this.mDy) > f1 * f2)
        return i; 
    } 
    return 0;
  }
  
  private void destroyCallbacks() {
    this.mRecyclerView.removeItemDecoration(this);
    this.mRecyclerView.removeOnItemTouchListener(this.mOnItemTouchListener);
    this.mRecyclerView.removeOnChildAttachStateChangeListener(this);
    for (int i = this.mRecoverAnimations.size() - 1; i >= 0; i--) {
      RecoverAnimation recoverAnimation = (RecoverAnimation)this.mRecoverAnimations.get(0);
      this.mCallback.clearView(this.mRecyclerView, recoverAnimation.mViewHolder);
    } 
    this.mRecoverAnimations.clear();
    this.mOverdrawChild = null;
    this.mOverdrawChildPosition = -1;
    releaseVelocityTracker();
    stopGestureDetection();
  }
  
  private List<RecyclerView.ViewHolder> findSwapTargets(RecyclerView.ViewHolder paramViewHolder) {
    RecyclerView.ViewHolder viewHolder = paramViewHolder;
    if (this.mSwapTargets == null) {
      this.mSwapTargets = new ArrayList();
      this.mDistances = new ArrayList();
    } else {
      this.mSwapTargets.clear();
      this.mDistances.clear();
    } 
    int j = this.mCallback.getBoundingBoxMargin();
    int k = Math.round(this.mSelectedStartX + this.mDx) - j;
    int m = Math.round(this.mSelectedStartY + this.mDy) - j;
    int i = viewHolder.itemView.getWidth();
    j *= 2;
    int n = i + k + j;
    int i1 = viewHolder.itemView.getHeight() + m + j;
    int i2 = (k + n) / 2;
    int i3 = (m + i1) / 2;
    RecyclerView.LayoutManager layoutManager = this.mRecyclerView.getLayoutManager();
    int i4 = layoutManager.getChildCount();
    for (i = 0; i < i4; i++) {
      View view = layoutManager.getChildAt(i);
      if (view != paramViewHolder.itemView && view.getBottom() >= m && view.getTop() <= i1 && view.getRight() >= k && view.getLeft() <= n) {
        RecyclerView.ViewHolder viewHolder1 = this.mRecyclerView.getChildViewHolder(view);
        if (this.mCallback.canDropOver(this.mRecyclerView, this.mSelected, viewHolder1)) {
          j = Math.abs(i2 - (view.getLeft() + view.getRight()) / 2);
          int i5 = Math.abs(i3 - (view.getTop() + view.getBottom()) / 2);
          int i6 = j * j + i5 * i5;
          int i7 = this.mSwapTargets.size();
          j = 0;
          i5 = 0;
          while (j < i7 && i6 > ((Integer)this.mDistances.get(j)).intValue()) {
            i5++;
            j++;
          } 
          this.mSwapTargets.add(i5, viewHolder1);
          this.mDistances.add(i5, Integer.valueOf(i6));
        } 
      } 
    } 
    return this.mSwapTargets;
  }
  
  private RecyclerView.ViewHolder findSwipedView(MotionEvent paramMotionEvent) {
    RecyclerView.LayoutManager layoutManager = this.mRecyclerView.getLayoutManager();
    if (this.mActivePointerId == -1)
      return null; 
    int i = paramMotionEvent.findPointerIndex(this.mActivePointerId);
    float f3 = paramMotionEvent.getX(i);
    float f4 = this.mInitialTouchX;
    float f1 = paramMotionEvent.getY(i);
    float f2 = this.mInitialTouchY;
    f3 = Math.abs(f3 - f4);
    f1 = Math.abs(f1 - f2);
    if (f3 < this.mSlop && f1 < this.mSlop)
      return null; 
    if (f3 > f1 && layoutManager.canScrollHorizontally())
      return null; 
    if (f1 > f3 && layoutManager.canScrollVertically())
      return null; 
    View view = findChildView(paramMotionEvent);
    return (view == null) ? null : this.mRecyclerView.getChildViewHolder(view);
  }
  
  private void getSelectedDxDy(float[] paramArrayOfFloat) {
    if ((this.mSelectedFlags & 0xC) != 0) {
      paramArrayOfFloat[0] = this.mSelectedStartX + this.mDx - this.mSelected.itemView.getLeft();
    } else {
      paramArrayOfFloat[0] = this.mSelected.itemView.getTranslationX();
    } 
    if ((this.mSelectedFlags & 0x3) != 0) {
      paramArrayOfFloat[1] = this.mSelectedStartY + this.mDy - this.mSelected.itemView.getTop();
      return;
    } 
    paramArrayOfFloat[1] = this.mSelected.itemView.getTranslationY();
  }
  
  private static boolean hitTest(View paramView, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) { return (paramFloat1 >= paramFloat3 && paramFloat1 <= paramFloat3 + paramView.getWidth() && paramFloat2 >= paramFloat4 && paramFloat2 <= paramFloat4 + paramView.getHeight()); }
  
  private void releaseVelocityTracker() {
    if (this.mVelocityTracker != null) {
      this.mVelocityTracker.recycle();
      this.mVelocityTracker = null;
    } 
  }
  
  private void setupCallbacks() {
    this.mSlop = ViewConfiguration.get(this.mRecyclerView.getContext()).getScaledTouchSlop();
    this.mRecyclerView.addItemDecoration(this);
    this.mRecyclerView.addOnItemTouchListener(this.mOnItemTouchListener);
    this.mRecyclerView.addOnChildAttachStateChangeListener(this);
    startGestureDetection();
  }
  
  private void startGestureDetection() {
    this.mItemTouchHelperGestureListener = new ItemTouchHelperGestureListener();
    this.mGestureDetector = new GestureDetectorCompat(this.mRecyclerView.getContext(), this.mItemTouchHelperGestureListener);
  }
  
  private void stopGestureDetection() {
    if (this.mItemTouchHelperGestureListener != null) {
      this.mItemTouchHelperGestureListener.doNotReactToLongPress();
      this.mItemTouchHelperGestureListener = null;
    } 
    if (this.mGestureDetector != null)
      this.mGestureDetector = null; 
  }
  
  private int swipeIfNecessary(RecyclerView.ViewHolder paramViewHolder) {
    if (this.mActionState == 2)
      return 0; 
    int j = this.mCallback.getMovementFlags(this.mRecyclerView, paramViewHolder);
    int i = (this.mCallback.convertToAbsoluteDirection(j, ViewCompat.getLayoutDirection(this.mRecyclerView)) & 0xFF00) >> 8;
    if (i == 0)
      return 0; 
    j = (j & 0xFF00) >> 8;
    if (Math.abs(this.mDx) > Math.abs(this.mDy)) {
      int k = checkHorizontalSwipe(paramViewHolder, i);
      if (k > 0)
        return ((j & k) == 0) ? Callback.convertToRelativeDirection(k, ViewCompat.getLayoutDirection(this.mRecyclerView)) : k; 
      i = checkVerticalSwipe(paramViewHolder, i);
      if (i > 0)
        return i; 
    } else {
      int k = checkVerticalSwipe(paramViewHolder, i);
      if (k > 0)
        return k; 
      i = checkHorizontalSwipe(paramViewHolder, i);
      if (i > 0)
        return ((j & i) == 0) ? Callback.convertToRelativeDirection(i, ViewCompat.getLayoutDirection(this.mRecyclerView)) : i; 
    } 
    return 0;
  }
  
  public void attachToRecyclerView(@Nullable RecyclerView paramRecyclerView) {
    if (this.mRecyclerView == paramRecyclerView)
      return; 
    if (this.mRecyclerView != null)
      destroyCallbacks(); 
    this.mRecyclerView = paramRecyclerView;
    if (paramRecyclerView != null) {
      Resources resources = paramRecyclerView.getResources();
      this.mSwipeEscapeVelocity = resources.getDimension(R.dimen.item_touch_helper_swipe_escape_velocity);
      this.mMaxSwipeVelocity = resources.getDimension(R.dimen.item_touch_helper_swipe_escape_max_velocity);
      setupCallbacks();
    } 
  }
  
  boolean checkSelectForSwipe(int paramInt1, MotionEvent paramMotionEvent, int paramInt2) {
    if (this.mSelected == null && paramInt1 == 2 && this.mActionState != 2) {
      if (!this.mCallback.isItemViewSwipeEnabled())
        return false; 
      if (this.mRecyclerView.getScrollState() == 1)
        return false; 
      RecyclerView.ViewHolder viewHolder = findSwipedView(paramMotionEvent);
      if (viewHolder == null)
        return false; 
      paramInt1 = (this.mCallback.getAbsoluteMovementFlags(this.mRecyclerView, viewHolder) & 0xFF00) >> 8;
      if (paramInt1 == 0)
        return false; 
      float f1 = paramMotionEvent.getX(paramInt2);
      float f2 = paramMotionEvent.getY(paramInt2);
      f1 -= this.mInitialTouchX;
      f2 -= this.mInitialTouchY;
      float f3 = Math.abs(f1);
      float f4 = Math.abs(f2);
      if (f3 < this.mSlop && f4 < this.mSlop)
        return false; 
      if (f3 > f4) {
        if (f1 < 0.0F && (paramInt1 & 0x4) == 0)
          return false; 
        if (f1 > 0.0F && (paramInt1 & 0x8) == 0)
          return false; 
      } else {
        if (f2 < 0.0F && (paramInt1 & true) == 0)
          return false; 
        if (f2 > 0.0F && (paramInt1 & 0x2) == 0)
          return false; 
      } 
      this.mDy = 0.0F;
      this.mDx = 0.0F;
      this.mActivePointerId = paramMotionEvent.getPointerId(0);
      select(viewHolder, 1);
      return true;
    } 
    return false;
  }
  
  int endRecoverAnimation(RecyclerView.ViewHolder paramViewHolder, boolean paramBoolean) {
    for (int i = this.mRecoverAnimations.size() - 1; i >= 0; i--) {
      RecoverAnimation recoverAnimation = (RecoverAnimation)this.mRecoverAnimations.get(i);
      if (recoverAnimation.mViewHolder == paramViewHolder) {
        recoverAnimation.mOverridden |= paramBoolean;
        if (!recoverAnimation.mEnded)
          recoverAnimation.cancel(); 
        this.mRecoverAnimations.remove(i);
        return recoverAnimation.mAnimationType;
      } 
    } 
    return 0;
  }
  
  RecoverAnimation findAnimation(MotionEvent paramMotionEvent) {
    if (this.mRecoverAnimations.isEmpty())
      return null; 
    View view = findChildView(paramMotionEvent);
    for (int i = this.mRecoverAnimations.size() - 1; i >= 0; i--) {
      RecoverAnimation recoverAnimation = (RecoverAnimation)this.mRecoverAnimations.get(i);
      if (recoverAnimation.mViewHolder.itemView == view)
        return recoverAnimation; 
    } 
    return null;
  }
  
  View findChildView(MotionEvent paramMotionEvent) {
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getY();
    if (this.mSelected != null) {
      View view = this.mSelected.itemView;
      if (hitTest(view, f1, f2, this.mSelectedStartX + this.mDx, this.mSelectedStartY + this.mDy))
        return view; 
    } 
    int i;
    for (i = this.mRecoverAnimations.size() - 1; i >= 0; i--) {
      RecoverAnimation recoverAnimation = (RecoverAnimation)this.mRecoverAnimations.get(i);
      View view = recoverAnimation.mViewHolder.itemView;
      if (hitTest(view, f1, f2, recoverAnimation.mX, recoverAnimation.mY))
        return view; 
    } 
    return this.mRecyclerView.findChildViewUnder(f1, f2);
  }
  
  public void getItemOffsets(Rect paramRect, View paramView, RecyclerView paramRecyclerView, RecyclerView.State paramState) { paramRect.setEmpty(); }
  
  boolean hasRunningRecoverAnim() {
    int i = this.mRecoverAnimations.size();
    for (byte b = 0; b < i; b++) {
      if (!((RecoverAnimation)this.mRecoverAnimations.get(b)).mEnded)
        return true; 
    } 
    return false;
  }
  
  void moveIfNecessary(RecyclerView.ViewHolder paramViewHolder) {
    if (this.mRecyclerView.isLayoutRequested())
      return; 
    if (this.mActionState != 2)
      return; 
    float f = this.mCallback.getMoveThreshold(paramViewHolder);
    int i = (int)(this.mSelectedStartX + this.mDx);
    int j = (int)(this.mSelectedStartY + this.mDy);
    if (Math.abs(j - paramViewHolder.itemView.getTop()) < paramViewHolder.itemView.getHeight() * f && Math.abs(i - paramViewHolder.itemView.getLeft()) < paramViewHolder.itemView.getWidth() * f)
      return; 
    List list = findSwapTargets(paramViewHolder);
    if (list.size() == 0)
      return; 
    RecyclerView.ViewHolder viewHolder = this.mCallback.chooseDropTarget(paramViewHolder, list, i, j);
    if (viewHolder == null) {
      this.mSwapTargets.clear();
      this.mDistances.clear();
      return;
    } 
    int k = viewHolder.getAdapterPosition();
    int m = paramViewHolder.getAdapterPosition();
    if (this.mCallback.onMove(this.mRecyclerView, paramViewHolder, viewHolder))
      this.mCallback.onMoved(this.mRecyclerView, paramViewHolder, m, viewHolder, k, i, j); 
  }
  
  void obtainVelocityTracker() {
    if (this.mVelocityTracker != null)
      this.mVelocityTracker.recycle(); 
    this.mVelocityTracker = VelocityTracker.obtain();
  }
  
  public void onChildViewAttachedToWindow(View paramView) {}
  
  public void onChildViewDetachedFromWindow(View paramView) {
    removeChildDrawingOrderCallbackIfNecessary(paramView);
    RecyclerView.ViewHolder viewHolder = this.mRecyclerView.getChildViewHolder(paramView);
    if (viewHolder == null)
      return; 
    if (this.mSelected != null && viewHolder == this.mSelected) {
      select(null, 0);
      return;
    } 
    endRecoverAnimation(viewHolder, false);
    if (this.mPendingCleanup.remove(viewHolder.itemView))
      this.mCallback.clearView(this.mRecyclerView, viewHolder); 
  }
  
  public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.State paramState) {
    float f2;
    float f1;
    this.mOverdrawChildPosition = -1;
    if (this.mSelected != null) {
      getSelectedDxDy(this.mTmpPosition);
      f1 = this.mTmpPosition[0];
      f2 = this.mTmpPosition[1];
    } else {
      f1 = 0.0F;
      f2 = 0.0F;
    } 
    this.mCallback.onDraw(paramCanvas, paramRecyclerView, this.mSelected, this.mRecoverAnimations, this.mActionState, f1, f2);
  }
  
  public void onDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.State paramState) {
    float f2;
    float f1;
    if (this.mSelected != null) {
      getSelectedDxDy(this.mTmpPosition);
      f1 = this.mTmpPosition[0];
      f2 = this.mTmpPosition[1];
    } else {
      f1 = 0.0F;
      f2 = 0.0F;
    } 
    this.mCallback.onDrawOver(paramCanvas, paramRecyclerView, this.mSelected, this.mRecoverAnimations, this.mActionState, f1, f2);
  }
  
  void postDispatchSwipe(final RecoverAnimation anim, final int swipeDir) { this.mRecyclerView.post(new Runnable() {
          public void run() {
            if (ItemTouchHelper.this.mRecyclerView != null && ItemTouchHelper.this.mRecyclerView.isAttachedToWindow() && !this.val$anim.mOverridden && this.val$anim.mViewHolder.getAdapterPosition() != -1) {
              RecyclerView.ItemAnimator itemAnimator = ItemTouchHelper.this.mRecyclerView.getItemAnimator();
              if ((itemAnimator == null || !itemAnimator.isRunning(null)) && !ItemTouchHelper.this.hasRunningRecoverAnim()) {
                ItemTouchHelper.this.mCallback.onSwiped(this.val$anim.mViewHolder, swipeDir);
                return;
              } 
              ItemTouchHelper.this.mRecyclerView.post(this);
            } 
          }
        }); }
  
  void removeChildDrawingOrderCallbackIfNecessary(View paramView) {
    if (paramView == this.mOverdrawChild) {
      this.mOverdrawChild = null;
      if (this.mChildDrawingOrderCallback != null)
        this.mRecyclerView.setChildDrawingOrderCallback(null); 
    } 
  }
  
  boolean scrollIfNecessary() { // Byte code:
    //   0: aload_0
    //   1: getfield mSelected : Landroid/support/v7/widget/RecyclerView$ViewHolder;
    //   4: ifnonnull -> 16
    //   7: aload_0
    //   8: ldc2_w -9223372036854775808
    //   11: putfield mDragScrollStartTimeInMs : J
    //   14: iconst_0
    //   15: ireturn
    //   16: invokestatic currentTimeMillis : ()J
    //   19: lstore #6
    //   21: aload_0
    //   22: getfield mDragScrollStartTimeInMs : J
    //   25: ldc2_w -9223372036854775808
    //   28: lcmp
    //   29: ifne -> 38
    //   32: lconst_0
    //   33: lstore #4
    //   35: goto -> 47
    //   38: lload #6
    //   40: aload_0
    //   41: getfield mDragScrollStartTimeInMs : J
    //   44: lsub
    //   45: lstore #4
    //   47: aload_0
    //   48: getfield mRecyclerView : Landroid/support/v7/widget/RecyclerView;
    //   51: invokevirtual getLayoutManager : ()Landroid/support/v7/widget/RecyclerView$LayoutManager;
    //   54: astore #8
    //   56: aload_0
    //   57: getfield mTmpRect : Landroid/graphics/Rect;
    //   60: ifnonnull -> 74
    //   63: aload_0
    //   64: new android/graphics/Rect
    //   67: dup
    //   68: invokespecial <init> : ()V
    //   71: putfield mTmpRect : Landroid/graphics/Rect;
    //   74: aload #8
    //   76: aload_0
    //   77: getfield mSelected : Landroid/support/v7/widget/RecyclerView$ViewHolder;
    //   80: getfield itemView : Landroid/view/View;
    //   83: aload_0
    //   84: getfield mTmpRect : Landroid/graphics/Rect;
    //   87: invokevirtual calculateItemDecorationsForChild : (Landroid/view/View;Landroid/graphics/Rect;)V
    //   90: aload #8
    //   92: invokevirtual canScrollHorizontally : ()Z
    //   95: ifeq -> 196
    //   98: aload_0
    //   99: getfield mSelectedStartX : F
    //   102: aload_0
    //   103: getfield mDx : F
    //   106: fadd
    //   107: f2i
    //   108: istore_2
    //   109: iload_2
    //   110: aload_0
    //   111: getfield mTmpRect : Landroid/graphics/Rect;
    //   114: getfield left : I
    //   117: isub
    //   118: aload_0
    //   119: getfield mRecyclerView : Landroid/support/v7/widget/RecyclerView;
    //   122: invokevirtual getPaddingLeft : ()I
    //   125: isub
    //   126: istore_1
    //   127: aload_0
    //   128: getfield mDx : F
    //   131: fconst_0
    //   132: fcmpg
    //   133: ifge -> 143
    //   136: iload_1
    //   137: ifge -> 143
    //   140: goto -> 198
    //   143: aload_0
    //   144: getfield mDx : F
    //   147: fconst_0
    //   148: fcmpl
    //   149: ifle -> 196
    //   152: iload_2
    //   153: aload_0
    //   154: getfield mSelected : Landroid/support/v7/widget/RecyclerView$ViewHolder;
    //   157: getfield itemView : Landroid/view/View;
    //   160: invokevirtual getWidth : ()I
    //   163: iadd
    //   164: aload_0
    //   165: getfield mTmpRect : Landroid/graphics/Rect;
    //   168: getfield right : I
    //   171: iadd
    //   172: aload_0
    //   173: getfield mRecyclerView : Landroid/support/v7/widget/RecyclerView;
    //   176: invokevirtual getWidth : ()I
    //   179: aload_0
    //   180: getfield mRecyclerView : Landroid/support/v7/widget/RecyclerView;
    //   183: invokevirtual getPaddingRight : ()I
    //   186: isub
    //   187: isub
    //   188: istore_1
    //   189: iload_1
    //   190: ifle -> 196
    //   193: goto -> 198
    //   196: iconst_0
    //   197: istore_1
    //   198: aload #8
    //   200: invokevirtual canScrollVertically : ()Z
    //   203: ifeq -> 304
    //   206: aload_0
    //   207: getfield mSelectedStartY : F
    //   210: aload_0
    //   211: getfield mDy : F
    //   214: fadd
    //   215: f2i
    //   216: istore_3
    //   217: iload_3
    //   218: aload_0
    //   219: getfield mTmpRect : Landroid/graphics/Rect;
    //   222: getfield top : I
    //   225: isub
    //   226: aload_0
    //   227: getfield mRecyclerView : Landroid/support/v7/widget/RecyclerView;
    //   230: invokevirtual getPaddingTop : ()I
    //   233: isub
    //   234: istore_2
    //   235: aload_0
    //   236: getfield mDy : F
    //   239: fconst_0
    //   240: fcmpg
    //   241: ifge -> 251
    //   244: iload_2
    //   245: ifge -> 251
    //   248: goto -> 306
    //   251: aload_0
    //   252: getfield mDy : F
    //   255: fconst_0
    //   256: fcmpl
    //   257: ifle -> 304
    //   260: iload_3
    //   261: aload_0
    //   262: getfield mSelected : Landroid/support/v7/widget/RecyclerView$ViewHolder;
    //   265: getfield itemView : Landroid/view/View;
    //   268: invokevirtual getHeight : ()I
    //   271: iadd
    //   272: aload_0
    //   273: getfield mTmpRect : Landroid/graphics/Rect;
    //   276: getfield bottom : I
    //   279: iadd
    //   280: aload_0
    //   281: getfield mRecyclerView : Landroid/support/v7/widget/RecyclerView;
    //   284: invokevirtual getHeight : ()I
    //   287: aload_0
    //   288: getfield mRecyclerView : Landroid/support/v7/widget/RecyclerView;
    //   291: invokevirtual getPaddingBottom : ()I
    //   294: isub
    //   295: isub
    //   296: istore_2
    //   297: iload_2
    //   298: ifle -> 304
    //   301: goto -> 306
    //   304: iconst_0
    //   305: istore_2
    //   306: iload_1
    //   307: istore_3
    //   308: iload_1
    //   309: ifeq -> 344
    //   312: aload_0
    //   313: getfield mCallback : Landroid/support/v7/widget/helper/ItemTouchHelper$Callback;
    //   316: aload_0
    //   317: getfield mRecyclerView : Landroid/support/v7/widget/RecyclerView;
    //   320: aload_0
    //   321: getfield mSelected : Landroid/support/v7/widget/RecyclerView$ViewHolder;
    //   324: getfield itemView : Landroid/view/View;
    //   327: invokevirtual getWidth : ()I
    //   330: iload_1
    //   331: aload_0
    //   332: getfield mRecyclerView : Landroid/support/v7/widget/RecyclerView;
    //   335: invokevirtual getWidth : ()I
    //   338: lload #4
    //   340: invokevirtual interpolateOutOfBoundsScroll : (Landroid/support/v7/widget/RecyclerView;IIIJ)I
    //   343: istore_3
    //   344: iload_2
    //   345: istore_1
    //   346: iload_2
    //   347: ifeq -> 382
    //   350: aload_0
    //   351: getfield mCallback : Landroid/support/v7/widget/helper/ItemTouchHelper$Callback;
    //   354: aload_0
    //   355: getfield mRecyclerView : Landroid/support/v7/widget/RecyclerView;
    //   358: aload_0
    //   359: getfield mSelected : Landroid/support/v7/widget/RecyclerView$ViewHolder;
    //   362: getfield itemView : Landroid/view/View;
    //   365: invokevirtual getHeight : ()I
    //   368: iload_2
    //   369: aload_0
    //   370: getfield mRecyclerView : Landroid/support/v7/widget/RecyclerView;
    //   373: invokevirtual getHeight : ()I
    //   376: lload #4
    //   378: invokevirtual interpolateOutOfBoundsScroll : (Landroid/support/v7/widget/RecyclerView;IIIJ)I
    //   381: istore_1
    //   382: iload_3
    //   383: ifne -> 402
    //   386: iload_1
    //   387: ifeq -> 393
    //   390: goto -> 402
    //   393: aload_0
    //   394: ldc2_w -9223372036854775808
    //   397: putfield mDragScrollStartTimeInMs : J
    //   400: iconst_0
    //   401: ireturn
    //   402: aload_0
    //   403: getfield mDragScrollStartTimeInMs : J
    //   406: ldc2_w -9223372036854775808
    //   409: lcmp
    //   410: ifne -> 419
    //   413: aload_0
    //   414: lload #6
    //   416: putfield mDragScrollStartTimeInMs : J
    //   419: aload_0
    //   420: getfield mRecyclerView : Landroid/support/v7/widget/RecyclerView;
    //   423: iload_3
    //   424: iload_1
    //   425: invokevirtual scrollBy : (II)V
    //   428: iconst_1
    //   429: ireturn }
  
  void select(RecyclerView.ViewHolder paramViewHolder, int paramInt) {
    boolean bool;
    if (paramViewHolder == this.mSelected && paramInt == this.mActionState)
      return; 
    this.mDragScrollStartTimeInMs = Float.MIN_VALUE;
    int i = this.mActionState;
    endRecoverAnimation(paramViewHolder, true);
    this.mActionState = paramInt;
    if (paramInt == 2) {
      this.mOverdrawChild = paramViewHolder.itemView;
      addChildDrawingOrderCallback();
    } 
    if (this.mSelected != null) {
      final RecoverAnimation prevSelected = this.mSelected;
      if (recoverAnimation.itemView.getParent() != null) {
        final int swipeDir;
        float f2;
        float f1;
        if (i == 2) {
          j = 0;
        } else {
          j = swipeIfNecessary(recoverAnimation);
        } 
        releaseVelocityTracker();
        if (j != 4 && j != 8 && j != 16 && j != 32) {
          switch (j) {
            default:
              f1 = 0.0F;
              f2 = 0.0F;
            case 1:
            case 2:
              f2 = Math.signum(this.mDy) * this.mRecyclerView.getHeight();
              f1 = 0.0F;
              break;
          } 
        } else {
          f1 = Math.signum(this.mDx) * this.mRecyclerView.getWidth();
          f2 = 0.0F;
        } 
        if (i == 2) {
          bool = true;
        } else if (j > 0) {
          bool = true;
        } else {
          bool = true;
        } 
        getSelectedDxDy(this.mTmpPosition);
        float f3 = this.mTmpPosition[0];
        float f4 = this.mTmpPosition[1];
        recoverAnimation = new RecoverAnimation(recoverAnimation, bool, i, f3, f4, f1, f2) {
            public void onAnimationEnd(Animator param1Animator) {
              super.onAnimationEnd(param1Animator);
              if (this.mOverridden)
                return; 
              if (swipeDir <= 0) {
                ItemTouchHelper.this.mCallback.clearView(ItemTouchHelper.this.mRecyclerView, prevSelected);
              } else {
                ItemTouchHelper.this.mPendingCleanup.add(this.val$prevSelected.itemView);
                this.mIsPendingCleanup = true;
                if (swipeDir > 0)
                  ItemTouchHelper.this.postDispatchSwipe(this, swipeDir); 
              } 
              if (ItemTouchHelper.this.mOverdrawChild == this.val$prevSelected.itemView)
                ItemTouchHelper.this.removeChildDrawingOrderCallbackIfNecessary(this.val$prevSelected.itemView); 
            }
          };
        recoverAnimation.setDuration(this.mCallback.getAnimationDuration(this.mRecyclerView, bool, f1 - f3, f2 - f4));
        this.mRecoverAnimations.add(recoverAnimation);
        recoverAnimation.start();
        bool = true;
      } else {
        removeChildDrawingOrderCallbackIfNecessary(recoverAnimation.itemView);
        this.mCallback.clearView(this.mRecyclerView, recoverAnimation);
        bool = false;
      } 
      this.mSelected = null;
    } else {
      bool = false;
    } 
    if (paramViewHolder != null) {
      this.mSelectedFlags = (this.mCallback.getAbsoluteMovementFlags(this.mRecyclerView, paramViewHolder) & (1 << paramInt * 8 + 8) - 1) >> this.mActionState * 8;
      this.mSelectedStartX = paramViewHolder.itemView.getLeft();
      this.mSelectedStartY = paramViewHolder.itemView.getTop();
      this.mSelected = paramViewHolder;
      if (paramInt == 2)
        this.mSelected.itemView.performHapticFeedback(0); 
    } 
    boolean bool1 = false;
    ViewParent viewParent = this.mRecyclerView.getParent();
    if (viewParent != null) {
      if (this.mSelected != null)
        bool1 = true; 
      viewParent.requestDisallowInterceptTouchEvent(bool1);
    } 
    if (!bool)
      this.mRecyclerView.getLayoutManager().requestSimpleAnimationsInNextLayout(); 
    this.mCallback.onSelectedChanged(this.mSelected, this.mActionState);
    this.mRecyclerView.invalidate();
  }
  
  public void startDrag(RecyclerView.ViewHolder paramViewHolder) {
    if (!this.mCallback.hasDragFlag(this.mRecyclerView, paramViewHolder)) {
      Log.e("ItemTouchHelper", "Start drag has been called but dragging is not enabled");
      return;
    } 
    if (paramViewHolder.itemView.getParent() != this.mRecyclerView) {
      Log.e("ItemTouchHelper", "Start drag has been called with a view holder which is not a child of the RecyclerView which is controlled by this ItemTouchHelper.");
      return;
    } 
    obtainVelocityTracker();
    this.mDy = 0.0F;
    this.mDx = 0.0F;
    select(paramViewHolder, 2);
  }
  
  public void startSwipe(RecyclerView.ViewHolder paramViewHolder) {
    if (!this.mCallback.hasSwipeFlag(this.mRecyclerView, paramViewHolder)) {
      Log.e("ItemTouchHelper", "Start swipe has been called but swiping is not enabled");
      return;
    } 
    if (paramViewHolder.itemView.getParent() != this.mRecyclerView) {
      Log.e("ItemTouchHelper", "Start swipe has been called with a view holder which is not a child of the RecyclerView controlled by this ItemTouchHelper.");
      return;
    } 
    obtainVelocityTracker();
    this.mDy = 0.0F;
    this.mDx = 0.0F;
    select(paramViewHolder, 1);
  }
  
  void updateDxDy(MotionEvent paramMotionEvent, int paramInt1, int paramInt2) {
    float f1 = paramMotionEvent.getX(paramInt2);
    float f2 = paramMotionEvent.getY(paramInt2);
    this.mDx = f1 - this.mInitialTouchX;
    this.mDy = f2 - this.mInitialTouchY;
    if ((paramInt1 & 0x4) == 0)
      this.mDx = Math.max(0.0F, this.mDx); 
    if ((paramInt1 & 0x8) == 0)
      this.mDx = Math.min(0.0F, this.mDx); 
    if ((paramInt1 & true) == 0)
      this.mDy = Math.max(0.0F, this.mDy); 
    if ((paramInt1 & 0x2) == 0)
      this.mDy = Math.min(0.0F, this.mDy); 
  }
  
  public static abstract class Callback {
    private static final int ABS_HORIZONTAL_DIR_FLAGS = 789516;
    
    public static final int DEFAULT_DRAG_ANIMATION_DURATION = 200;
    
    public static final int DEFAULT_SWIPE_ANIMATION_DURATION = 250;
    
    private static final long DRAG_SCROLL_ACCELERATION_LIMIT_TIME_MS = 2000L;
    
    static final int RELATIVE_DIR_FLAGS = 3158064;
    
    private static final Interpolator sDragScrollInterpolator = new Interpolator() {
        public float getInterpolation(float param2Float) { return param2Float * param2Float * param2Float * param2Float * param2Float; }
      };
    
    private static final Interpolator sDragViewScrollCapInterpolator = new Interpolator() {
        public float getInterpolation(float param2Float) {
          param2Float--;
          return param2Float * param2Float * param2Float * param2Float * param2Float + 1.0F;
        }
      };
    
    private static final ItemTouchUIUtil sUICallback;
    
    private int mCachedMaxScrollSpeed = -1;
    
    static  {
      if (Build.VERSION.SDK_INT >= 21) {
        sUICallback = new ItemTouchUIUtilImpl.Api21Impl();
        return;
      } 
      sUICallback = new ItemTouchUIUtilImpl.BaseImpl();
    }
    
    public static int convertToRelativeDirection(int param1Int1, int param1Int2) {
      int i = param1Int1 & 0xC0C0C;
      if (i == 0)
        return param1Int1; 
      param1Int1 &= (i ^ 0xFFFFFFFF);
      if (param1Int2 == 0)
        return param1Int1 | i << 2; 
      param1Int2 = i << 1;
      return param1Int1 | 0xFFF3F3F3 & param1Int2 | (param1Int2 & 0xC0C0C) << 2;
    }
    
    public static ItemTouchUIUtil getDefaultUIUtil() { return sUICallback; }
    
    private int getMaxDragScroll(RecyclerView param1RecyclerView) {
      if (this.mCachedMaxScrollSpeed == -1)
        this.mCachedMaxScrollSpeed = param1RecyclerView.getResources().getDimensionPixelSize(R.dimen.item_touch_helper_max_drag_scroll_per_frame); 
      return this.mCachedMaxScrollSpeed;
    }
    
    public static int makeFlag(int param1Int1, int param1Int2) { return param1Int2 << param1Int1 * 8; }
    
    public static int makeMovementFlags(int param1Int1, int param1Int2) {
      int i = makeFlag(0, param1Int2 | param1Int1);
      param1Int2 = makeFlag(1, param1Int2);
      return makeFlag(2, param1Int1) | param1Int2 | i;
    }
    
    public boolean canDropOver(RecyclerView param1RecyclerView, RecyclerView.ViewHolder param1ViewHolder1, RecyclerView.ViewHolder param1ViewHolder2) { return true; }
    
    public RecyclerView.ViewHolder chooseDropTarget(RecyclerView.ViewHolder param1ViewHolder, List<RecyclerView.ViewHolder> param1List, int param1Int1, int param1Int2) {
      int j = param1ViewHolder.itemView.getWidth();
      int k = param1ViewHolder.itemView.getHeight();
      int m = param1Int1 - param1ViewHolder.itemView.getLeft();
      int n = param1Int2 - param1ViewHolder.itemView.getTop();
      int i1 = param1List.size();
      RecyclerView.ViewHolder viewHolder = null;
      int i = -1;
      byte b;
      for (b = 0; b < i1; b++) {
        RecyclerView.ViewHolder viewHolder1 = (RecyclerView.ViewHolder)param1List.get(b);
        RecyclerView.ViewHolder viewHolder2 = viewHolder;
        int i2 = i;
        if (m > 0) {
          int i3 = viewHolder1.itemView.getRight() - param1Int1 + j;
          viewHolder2 = viewHolder;
          i2 = i;
          if (i3 < 0) {
            viewHolder2 = viewHolder;
            i2 = i;
            if (viewHolder1.itemView.getRight() > param1ViewHolder.itemView.getRight()) {
              i3 = Math.abs(i3);
              viewHolder2 = viewHolder;
              i2 = i;
              if (i3 > i) {
                viewHolder2 = viewHolder1;
                i2 = i3;
              } 
            } 
          } 
        } 
        viewHolder = viewHolder2;
        i = i2;
        if (m < 0) {
          int i3 = viewHolder1.itemView.getLeft() - param1Int1;
          viewHolder = viewHolder2;
          i = i2;
          if (i3 > 0) {
            viewHolder = viewHolder2;
            i = i2;
            if (viewHolder1.itemView.getLeft() < param1ViewHolder.itemView.getLeft()) {
              i3 = Math.abs(i3);
              viewHolder = viewHolder2;
              i = i2;
              if (i3 > i2) {
                viewHolder = viewHolder1;
                i = i3;
              } 
            } 
          } 
        } 
        viewHolder2 = viewHolder;
        i2 = i;
        if (n < 0) {
          int i3 = viewHolder1.itemView.getTop() - param1Int2;
          viewHolder2 = viewHolder;
          i2 = i;
          if (i3 > 0) {
            viewHolder2 = viewHolder;
            i2 = i;
            if (viewHolder1.itemView.getTop() < param1ViewHolder.itemView.getTop()) {
              i3 = Math.abs(i3);
              viewHolder2 = viewHolder;
              i2 = i;
              if (i3 > i) {
                viewHolder2 = viewHolder1;
                i2 = i3;
              } 
            } 
          } 
        } 
        viewHolder = viewHolder2;
        i = i2;
        if (n > 0) {
          int i3 = viewHolder1.itemView.getBottom() - param1Int2 + k;
          viewHolder = viewHolder2;
          i = i2;
          if (i3 < 0) {
            viewHolder = viewHolder2;
            i = i2;
            if (viewHolder1.itemView.getBottom() > param1ViewHolder.itemView.getBottom()) {
              i3 = Math.abs(i3);
              viewHolder = viewHolder2;
              i = i2;
              if (i3 > i2) {
                i = i3;
                viewHolder = viewHolder1;
              } 
            } 
          } 
        } 
      } 
      return viewHolder;
    }
    
    public void clearView(RecyclerView param1RecyclerView, RecyclerView.ViewHolder param1ViewHolder) { sUICallback.clearView(param1ViewHolder.itemView); }
    
    public int convertToAbsoluteDirection(int param1Int1, int param1Int2) {
      int i = param1Int1 & 0x303030;
      if (i == 0)
        return param1Int1; 
      param1Int1 &= (i ^ 0xFFFFFFFF);
      if (param1Int2 == 0)
        return param1Int1 | i >> 2; 
      param1Int2 = i >> 1;
      return param1Int1 | 0xFFCFCFCF & param1Int2 | (param1Int2 & 0x303030) >> 2;
    }
    
    final int getAbsoluteMovementFlags(RecyclerView param1RecyclerView, RecyclerView.ViewHolder param1ViewHolder) { return convertToAbsoluteDirection(getMovementFlags(param1RecyclerView, param1ViewHolder), ViewCompat.getLayoutDirection(param1RecyclerView)); }
    
    public long getAnimationDuration(RecyclerView param1RecyclerView, int param1Int, float param1Float1, float param1Float2) {
      RecyclerView.ItemAnimator itemAnimator = param1RecyclerView.getItemAnimator();
      return (itemAnimator == null) ? ((param1Int == 8) ? 200L : 250L) : ((param1Int == 8) ? itemAnimator.getMoveDuration() : itemAnimator.getRemoveDuration());
    }
    
    public int getBoundingBoxMargin() { return 0; }
    
    public float getMoveThreshold(RecyclerView.ViewHolder param1ViewHolder) { return 0.5F; }
    
    public abstract int getMovementFlags(RecyclerView param1RecyclerView, RecyclerView.ViewHolder param1ViewHolder);
    
    public float getSwipeEscapeVelocity(float param1Float) { return param1Float; }
    
    public float getSwipeThreshold(RecyclerView.ViewHolder param1ViewHolder) { return 0.5F; }
    
    public float getSwipeVelocityThreshold(float param1Float) { return param1Float; }
    
    boolean hasDragFlag(RecyclerView param1RecyclerView, RecyclerView.ViewHolder param1ViewHolder) { return ((getAbsoluteMovementFlags(param1RecyclerView, param1ViewHolder) & 0xFF0000) != 0); }
    
    boolean hasSwipeFlag(RecyclerView param1RecyclerView, RecyclerView.ViewHolder param1ViewHolder) { return ((getAbsoluteMovementFlags(param1RecyclerView, param1ViewHolder) & 0xFF00) != 0); }
    
    public int interpolateOutOfBoundsScroll(RecyclerView param1RecyclerView, int param1Int1, int param1Int2, int param1Int3, long param1Long) {
      param1Int3 = getMaxDragScroll(param1RecyclerView);
      int i = Math.abs(param1Int2);
      int j = (int)Math.signum(param1Int2);
      float f2 = i;
      float f1 = 1.0F;
      f2 = Math.min(1.0F, f2 * 1.0F / param1Int1);
      param1Int1 = (int)((j * param1Int3) * sDragViewScrollCapInterpolator.getInterpolation(f2));
      if (param1Long <= 2000L)
        f1 = (float)param1Long / 2000.0F; 
      param1Int1 = (int)(param1Int1 * sDragScrollInterpolator.getInterpolation(f1));
      return (param1Int1 == 0) ? ((param1Int2 > 0) ? 1 : -1) : param1Int1;
    }
    
    public boolean isItemViewSwipeEnabled() { return true; }
    
    public boolean isLongPressDragEnabled() { return true; }
    
    public void onChildDraw(Canvas param1Canvas, RecyclerView param1RecyclerView, RecyclerView.ViewHolder param1ViewHolder, float param1Float1, float param1Float2, int param1Int, boolean param1Boolean) { sUICallback.onDraw(param1Canvas, param1RecyclerView, param1ViewHolder.itemView, param1Float1, param1Float2, param1Int, param1Boolean); }
    
    public void onChildDrawOver(Canvas param1Canvas, RecyclerView param1RecyclerView, RecyclerView.ViewHolder param1ViewHolder, float param1Float1, float param1Float2, int param1Int, boolean param1Boolean) { sUICallback.onDrawOver(param1Canvas, param1RecyclerView, param1ViewHolder.itemView, param1Float1, param1Float2, param1Int, param1Boolean); }
    
    void onDraw(Canvas param1Canvas, RecyclerView param1RecyclerView, RecyclerView.ViewHolder param1ViewHolder, List<ItemTouchHelper.RecoverAnimation> param1List, int param1Int, float param1Float1, float param1Float2) {
      int j = param1List.size();
      int i;
      for (i = 0; i < j; i++) {
        ItemTouchHelper.RecoverAnimation recoverAnimation = (ItemTouchHelper.RecoverAnimation)param1List.get(i);
        recoverAnimation.update();
        int k = param1Canvas.save();
        onChildDraw(param1Canvas, param1RecyclerView, recoverAnimation.mViewHolder, recoverAnimation.mX, recoverAnimation.mY, recoverAnimation.mActionState, false);
        param1Canvas.restoreToCount(k);
      } 
      if (param1ViewHolder != null) {
        i = param1Canvas.save();
        onChildDraw(param1Canvas, param1RecyclerView, param1ViewHolder, param1Float1, param1Float2, param1Int, true);
        param1Canvas.restoreToCount(i);
      } 
    }
    
    void onDrawOver(Canvas param1Canvas, RecyclerView param1RecyclerView, RecyclerView.ViewHolder param1ViewHolder, List<ItemTouchHelper.RecoverAnimation> param1List, int param1Int, float param1Float1, float param1Float2) {
      int k = param1List.size();
      int j = 0;
      int i;
      for (i = 0; i < k; i++) {
        ItemTouchHelper.RecoverAnimation recoverAnimation = (ItemTouchHelper.RecoverAnimation)param1List.get(i);
        int m = param1Canvas.save();
        onChildDrawOver(param1Canvas, param1RecyclerView, recoverAnimation.mViewHolder, recoverAnimation.mX, recoverAnimation.mY, recoverAnimation.mActionState, false);
        param1Canvas.restoreToCount(m);
      } 
      if (param1ViewHolder != null) {
        i = param1Canvas.save();
        onChildDrawOver(param1Canvas, param1RecyclerView, param1ViewHolder, param1Float1, param1Float2, param1Int, true);
        param1Canvas.restoreToCount(i);
      } 
      param1Int = k - 1;
      i = j;
      while (param1Int >= 0) {
        ItemTouchHelper.RecoverAnimation recoverAnimation = (ItemTouchHelper.RecoverAnimation)param1List.get(param1Int);
        if (recoverAnimation.mEnded && !recoverAnimation.mIsPendingCleanup) {
          param1List.remove(param1Int);
        } else if (!recoverAnimation.mEnded) {
          i = 1;
        } 
        param1Int--;
      } 
      if (i != 0)
        param1RecyclerView.invalidate(); 
    }
    
    public abstract boolean onMove(RecyclerView param1RecyclerView, RecyclerView.ViewHolder param1ViewHolder1, RecyclerView.ViewHolder param1ViewHolder2);
    
    public void onMoved(RecyclerView param1RecyclerView, RecyclerView.ViewHolder param1ViewHolder1, int param1Int1, RecyclerView.ViewHolder param1ViewHolder2, int param1Int2, int param1Int3, int param1Int4) {
      RecyclerView.LayoutManager layoutManager = param1RecyclerView.getLayoutManager();
      if (layoutManager instanceof ItemTouchHelper.ViewDropHandler) {
        ((ItemTouchHelper.ViewDropHandler)layoutManager).prepareForDrop(param1ViewHolder1.itemView, param1ViewHolder2.itemView, param1Int3, param1Int4);
        return;
      } 
      if (layoutManager.canScrollHorizontally()) {
        if (layoutManager.getDecoratedLeft(param1ViewHolder2.itemView) <= param1RecyclerView.getPaddingLeft())
          param1RecyclerView.scrollToPosition(param1Int2); 
        if (layoutManager.getDecoratedRight(param1ViewHolder2.itemView) >= param1RecyclerView.getWidth() - param1RecyclerView.getPaddingRight())
          param1RecyclerView.scrollToPosition(param1Int2); 
      } 
      if (layoutManager.canScrollVertically()) {
        if (layoutManager.getDecoratedTop(param1ViewHolder2.itemView) <= param1RecyclerView.getPaddingTop())
          param1RecyclerView.scrollToPosition(param1Int2); 
        if (layoutManager.getDecoratedBottom(param1ViewHolder2.itemView) >= param1RecyclerView.getHeight() - param1RecyclerView.getPaddingBottom())
          param1RecyclerView.scrollToPosition(param1Int2); 
      } 
    }
    
    public void onSelectedChanged(RecyclerView.ViewHolder param1ViewHolder, int param1Int) {
      if (param1ViewHolder != null)
        sUICallback.onSelected(param1ViewHolder.itemView); 
    }
    
    public abstract void onSwiped(RecyclerView.ViewHolder param1ViewHolder, int param1Int);
  }
  
  static final class null implements Interpolator {
    public float getInterpolation(float param1Float) { return param1Float * param1Float * param1Float * param1Float * param1Float; }
  }
  
  static final class null implements Interpolator {
    public float getInterpolation(float param1Float) {
      param1Float--;
      return param1Float * param1Float * param1Float * param1Float * param1Float + 1.0F;
    }
  }
  
  private class ItemTouchHelperGestureListener extends GestureDetector.SimpleOnGestureListener {
    private boolean mShouldReactToLongPress = true;
    
    void doNotReactToLongPress() { this.mShouldReactToLongPress = false; }
    
    public boolean onDown(MotionEvent param1MotionEvent) { return true; }
    
    public void onLongPress(MotionEvent param1MotionEvent) {
      if (!this.mShouldReactToLongPress)
        return; 
      View view = ItemTouchHelper.this.findChildView(param1MotionEvent);
      if (view != null) {
        RecyclerView.ViewHolder viewHolder = ItemTouchHelper.this.mRecyclerView.getChildViewHolder(view);
        if (viewHolder != null) {
          if (!ItemTouchHelper.this.mCallback.hasDragFlag(ItemTouchHelper.this.mRecyclerView, viewHolder))
            return; 
          if (param1MotionEvent.getPointerId(0) == ItemTouchHelper.this.mActivePointerId) {
            int i = param1MotionEvent.findPointerIndex(ItemTouchHelper.this.mActivePointerId);
            float f1 = param1MotionEvent.getX(i);
            float f2 = param1MotionEvent.getY(i);
            ItemTouchHelper.this.mInitialTouchX = f1;
            ItemTouchHelper.this.mInitialTouchY = f2;
            ItemTouchHelper itemTouchHelper = ItemTouchHelper.this;
            ItemTouchHelper.this.mDy = 0.0F;
            itemTouchHelper.mDx = 0.0F;
            if (ItemTouchHelper.this.mCallback.isLongPressDragEnabled())
              ItemTouchHelper.this.select(viewHolder, 2); 
          } 
        } 
      } 
    }
  }
  
  private static class RecoverAnimation implements Animator.AnimatorListener {
    final int mActionState;
    
    final int mAnimationType;
    
    boolean mEnded = false;
    
    private float mFraction;
    
    public boolean mIsPendingCleanup;
    
    boolean mOverridden = false;
    
    final float mStartDx;
    
    final float mStartDy;
    
    final float mTargetX;
    
    final float mTargetY;
    
    private final ValueAnimator mValueAnimator;
    
    final RecyclerView.ViewHolder mViewHolder;
    
    float mX;
    
    float mY;
    
    RecoverAnimation(RecyclerView.ViewHolder param1ViewHolder, int param1Int1, int param1Int2, float param1Float1, float param1Float2, float param1Float3, float param1Float4) {
      this.mActionState = param1Int2;
      this.mAnimationType = param1Int1;
      this.mViewHolder = param1ViewHolder;
      this.mStartDx = param1Float1;
      this.mStartDy = param1Float2;
      this.mTargetX = param1Float3;
      this.mTargetY = param1Float4;
      this.mValueAnimator = ValueAnimator.ofFloat(new float[] { 0.0F, 1.0F });
      this.mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator param2ValueAnimator) { ItemTouchHelper.RecoverAnimation.this.setFraction(param2ValueAnimator.getAnimatedFraction()); }
          });
      this.mValueAnimator.setTarget(param1ViewHolder.itemView);
      this.mValueAnimator.addListener(this);
      setFraction(0.0F);
    }
    
    public void cancel() { this.mValueAnimator.cancel(); }
    
    public void onAnimationCancel(Animator param1Animator) { setFraction(1.0F); }
    
    public void onAnimationEnd(Animator param1Animator) {
      if (!this.mEnded)
        this.mViewHolder.setIsRecyclable(true); 
      this.mEnded = true;
    }
    
    public void onAnimationRepeat(Animator param1Animator) {}
    
    public void onAnimationStart(Animator param1Animator) {}
    
    public void setDuration(long param1Long) { this.mValueAnimator.setDuration(param1Long); }
    
    public void setFraction(float param1Float) { this.mFraction = param1Float; }
    
    public void start() {
      this.mViewHolder.setIsRecyclable(false);
      this.mValueAnimator.start();
    }
    
    public void update() {
      if (this.mStartDx == this.mTargetX) {
        this.mX = this.mViewHolder.itemView.getTranslationX();
      } else {
        this.mX = this.mStartDx + this.mFraction * (this.mTargetX - this.mStartDx);
      } 
      if (this.mStartDy == this.mTargetY) {
        this.mY = this.mViewHolder.itemView.getTranslationY();
        return;
      } 
      this.mY = this.mStartDy + this.mFraction * (this.mTargetY - this.mStartDy);
    }
  }
  
  class null implements ValueAnimator.AnimatorUpdateListener {
    null() {}
    
    public void onAnimationUpdate(ValueAnimator param1ValueAnimator) { this.this$0.setFraction(param1ValueAnimator.getAnimatedFraction()); }
  }
  
  public static abstract class SimpleCallback extends Callback {
    private int mDefaultDragDirs;
    
    private int mDefaultSwipeDirs;
    
    public SimpleCallback(int param1Int1, int param1Int2) {
      this.mDefaultSwipeDirs = param1Int2;
      this.mDefaultDragDirs = param1Int1;
    }
    
    public int getDragDirs(RecyclerView param1RecyclerView, RecyclerView.ViewHolder param1ViewHolder) { return this.mDefaultDragDirs; }
    
    public int getMovementFlags(RecyclerView param1RecyclerView, RecyclerView.ViewHolder param1ViewHolder) { return makeMovementFlags(getDragDirs(param1RecyclerView, param1ViewHolder), getSwipeDirs(param1RecyclerView, param1ViewHolder)); }
    
    public int getSwipeDirs(RecyclerView param1RecyclerView, RecyclerView.ViewHolder param1ViewHolder) { return this.mDefaultSwipeDirs; }
    
    public void setDefaultDragDirs(int param1Int) { this.mDefaultDragDirs = param1Int; }
    
    public void setDefaultSwipeDirs(int param1Int) { this.mDefaultSwipeDirs = param1Int; }
  }
  
  public static interface ViewDropHandler {
    void prepareForDrop(View param1View1, View param1View2, int param1Int1, int param1Int2);
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/helper/ItemTouchHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */