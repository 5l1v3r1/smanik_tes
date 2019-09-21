package android.support.v7.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Observable;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.support.v4.os.TraceCompat;
import android.support.v4.util.Preconditions;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild2;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.recyclerview.R;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.FocusFinder;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Interpolator;
import android.widget.EdgeEffect;
import android.widget.OverScroller;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecyclerView extends ViewGroup implements ScrollingView, NestedScrollingChild2 {
  static final boolean ALLOW_SIZE_IN_UNSPECIFIED_SPEC;
  
  private static final boolean ALLOW_THREAD_GAP_WORK;
  
  private static final int[] CLIP_TO_PADDING_ATTR;
  
  static final boolean DEBUG = false;
  
  static final int DEFAULT_ORIENTATION = 1;
  
  static final boolean DISPATCH_TEMP_DETACH = false;
  
  private static final boolean FORCE_ABS_FOCUS_SEARCH_DIRECTION;
  
  static final boolean FORCE_INVALIDATE_DISPLAY_LIST;
  
  static final long FOREVER_NS = 9223372036854775807L;
  
  public static final int HORIZONTAL = 0;
  
  private static final boolean IGNORE_DETACHED_FOCUSED_CHILD;
  
  private static final int INVALID_POINTER = -1;
  
  public static final int INVALID_TYPE = -1;
  
  private static final Class<?>[] LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE;
  
  static final int MAX_SCROLL_DURATION = 2000;
  
  private static final int[] NESTED_SCROLLING_ATTRS;
  
  public static final long NO_ID = -1L;
  
  public static final int NO_POSITION = -1;
  
  static final boolean POST_UPDATES_ON_ANIMATION;
  
  public static final int SCROLL_STATE_DRAGGING = 1;
  
  public static final int SCROLL_STATE_IDLE = 0;
  
  public static final int SCROLL_STATE_SETTLING = 2;
  
  static final String TAG = "RecyclerView";
  
  public static final int TOUCH_SLOP_DEFAULT = 0;
  
  public static final int TOUCH_SLOP_PAGING = 1;
  
  static final String TRACE_BIND_VIEW_TAG = "RV OnBindView";
  
  static final String TRACE_CREATE_VIEW_TAG = "RV CreateView";
  
  private static final String TRACE_HANDLE_ADAPTER_UPDATES_TAG = "RV PartialInvalidate";
  
  static final String TRACE_NESTED_PREFETCH_TAG = "RV Nested Prefetch";
  
  private static final String TRACE_ON_DATA_SET_CHANGE_LAYOUT_TAG = "RV FullInvalidate";
  
  private static final String TRACE_ON_LAYOUT_TAG = "RV OnLayout";
  
  static final String TRACE_PREFETCH_TAG = "RV Prefetch";
  
  static final String TRACE_SCROLL_TAG = "RV Scroll";
  
  static final boolean VERBOSE_TRACING = false;
  
  public static final int VERTICAL = 1;
  
  static final Interpolator sQuinticInterpolator;
  
  RecyclerViewAccessibilityDelegate mAccessibilityDelegate;
  
  private final AccessibilityManager mAccessibilityManager;
  
  private OnItemTouchListener mActiveOnItemTouchListener;
  
  Adapter mAdapter;
  
  AdapterHelper mAdapterHelper;
  
  boolean mAdapterUpdateDuringMeasure;
  
  private EdgeEffect mBottomGlow;
  
  private ChildDrawingOrderCallback mChildDrawingOrderCallback;
  
  ChildHelper mChildHelper;
  
  boolean mClipToPadding;
  
  boolean mDataSetHasChangedAfterLayout = false;
  
  boolean mDispatchItemsChangedEvent = false;
  
  private int mDispatchScrollCounter = 0;
  
  private int mEatenAccessibilityChangeFlags;
  
  @NonNull
  private EdgeEffectFactory mEdgeEffectFactory = new EdgeEffectFactory();
  
  boolean mEnableFastScroller;
  
  @VisibleForTesting
  boolean mFirstLayoutComplete;
  
  GapWorker mGapWorker;
  
  boolean mHasFixedSize;
  
  private boolean mIgnoreMotionEventTillDown;
  
  private int mInitialTouchX;
  
  private int mInitialTouchY;
  
  private int mInterceptRequestLayoutDepth = 0;
  
  boolean mIsAttached;
  
  ItemAnimator mItemAnimator = new DefaultItemAnimator();
  
  private ItemAnimator.ItemAnimatorListener mItemAnimatorListener;
  
  private Runnable mItemAnimatorRunner;
  
  final ArrayList<ItemDecoration> mItemDecorations = new ArrayList();
  
  boolean mItemsAddedOrRemoved;
  
  boolean mItemsChanged;
  
  private int mLastTouchX;
  
  private int mLastTouchY;
  
  @VisibleForTesting
  LayoutManager mLayout;
  
  boolean mLayoutFrozen;
  
  private int mLayoutOrScrollCounter = 0;
  
  boolean mLayoutWasDefered;
  
  private EdgeEffect mLeftGlow;
  
  private final int mMaxFlingVelocity;
  
  private final int mMinFlingVelocity;
  
  private final int[] mMinMaxLayoutPositions;
  
  private final int[] mNestedOffsets;
  
  private final RecyclerViewDataObserver mObserver = new RecyclerViewDataObserver();
  
  private List<OnChildAttachStateChangeListener> mOnChildAttachStateListeners;
  
  private OnFlingListener mOnFlingListener;
  
  private final ArrayList<OnItemTouchListener> mOnItemTouchListeners = new ArrayList();
  
  @VisibleForTesting
  final List<ViewHolder> mPendingAccessibilityImportanceChange;
  
  private SavedState mPendingSavedState;
  
  boolean mPostedAnimatorRunner;
  
  GapWorker.LayoutPrefetchRegistryImpl mPrefetchRegistry;
  
  private boolean mPreserveFocusAfterLayout;
  
  final Recycler mRecycler = new Recycler();
  
  RecyclerListener mRecyclerListener;
  
  private EdgeEffect mRightGlow;
  
  private float mScaledHorizontalScrollFactor = Float.MIN_VALUE;
  
  private float mScaledVerticalScrollFactor = Float.MIN_VALUE;
  
  private final int[] mScrollConsumed;
  
  private OnScrollListener mScrollListener;
  
  private List<OnScrollListener> mScrollListeners;
  
  private final int[] mScrollOffset;
  
  private int mScrollPointerId = -1;
  
  private int mScrollState = 0;
  
  private NestedScrollingChildHelper mScrollingChildHelper;
  
  final State mState;
  
  final Rect mTempRect = new Rect();
  
  private final Rect mTempRect2 = new Rect();
  
  final RectF mTempRectF = new RectF();
  
  private EdgeEffect mTopGlow;
  
  private int mTouchSlop;
  
  final Runnable mUpdateChildViewsRunnable = new Runnable() {
      public void run() {
        if (RecyclerView.this.mFirstLayoutComplete) {
          if (RecyclerView.this.isLayoutRequested())
            return; 
          if (!RecyclerView.this.mIsAttached) {
            RecyclerView.this.requestLayout();
            return;
          } 
          if (RecyclerView.this.mLayoutFrozen) {
            RecyclerView.this.mLayoutWasDefered = true;
            return;
          } 
          RecyclerView.this.consumePendingUpdateOperations();
          return;
        } 
      }
    };
  
  private VelocityTracker mVelocityTracker;
  
  final ViewFlinger mViewFlinger;
  
  private final ViewInfoStore.ProcessCallback mViewInfoProcessCallback;
  
  final ViewInfoStore mViewInfoStore = new ViewInfoStore();
  
  static  {
    boolean bool;
    NESTED_SCROLLING_ATTRS = new int[] { 16843830 };
    CLIP_TO_PADDING_ATTR = new int[] { 16842987 };
    if (Build.VERSION.SDK_INT == 18 || Build.VERSION.SDK_INT == 19 || Build.VERSION.SDK_INT == 20) {
      bool = true;
    } else {
      bool = false;
    } 
    FORCE_INVALIDATE_DISPLAY_LIST = bool;
    if (Build.VERSION.SDK_INT >= 23) {
      bool = true;
    } else {
      bool = false;
    } 
    ALLOW_SIZE_IN_UNSPECIFIED_SPEC = bool;
    if (Build.VERSION.SDK_INT >= 16) {
      bool = true;
    } else {
      bool = false;
    } 
    POST_UPDATES_ON_ANIMATION = bool;
    if (Build.VERSION.SDK_INT >= 21) {
      bool = true;
    } else {
      bool = false;
    } 
    ALLOW_THREAD_GAP_WORK = bool;
    if (Build.VERSION.SDK_INT <= 15) {
      bool = true;
    } else {
      bool = false;
    } 
    FORCE_ABS_FOCUS_SEARCH_DIRECTION = bool;
    if (Build.VERSION.SDK_INT <= 15) {
      bool = true;
    } else {
      bool = false;
    } 
    IGNORE_DETACHED_FOCUSED_CHILD = bool;
    LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE = new Class[] { Context.class, AttributeSet.class, int.class, int.class };
    sQuinticInterpolator = new Interpolator() {
        public float getInterpolation(float param1Float) {
          param1Float--;
          return param1Float * param1Float * param1Float * param1Float * param1Float + 1.0F;
        }
      };
  }
  
  public RecyclerView(Context paramContext) { this(paramContext, null); }
  
  public RecyclerView(Context paramContext, @Nullable AttributeSet paramAttributeSet) { this(paramContext, paramAttributeSet, 0); }
  
  public RecyclerView(Context paramContext, @Nullable AttributeSet paramAttributeSet, int paramInt) {
    super(paramContext, paramAttributeSet, paramInt);
    boolean bool1 = true;
    this.mPreserveFocusAfterLayout = true;
    this.mViewFlinger = new ViewFlinger();
    if (ALLOW_THREAD_GAP_WORK) {
      typedArray = new GapWorker.LayoutPrefetchRegistryImpl();
    } else {
      typedArray = null;
    } 
    this.mPrefetchRegistry = typedArray;
    this.mState = new State();
    this.mItemsAddedOrRemoved = false;
    this.mItemsChanged = false;
    this.mItemAnimatorListener = new ItemAnimatorRestoreListener();
    this.mPostedAnimatorRunner = false;
    this.mMinMaxLayoutPositions = new int[2];
    this.mScrollOffset = new int[2];
    this.mScrollConsumed = new int[2];
    this.mNestedOffsets = new int[2];
    this.mPendingAccessibilityImportanceChange = new ArrayList();
    this.mItemAnimatorRunner = new Runnable() {
        public void run() {
          if (RecyclerView.this.mItemAnimator != null)
            RecyclerView.this.mItemAnimator.runPendingAnimations(); 
          RecyclerView.this.mPostedAnimatorRunner = false;
        }
      };
    this.mViewInfoProcessCallback = new ViewInfoStore.ProcessCallback() {
        public void processAppeared(RecyclerView.ViewHolder param1ViewHolder, RecyclerView.ItemAnimator.ItemHolderInfo param1ItemHolderInfo1, RecyclerView.ItemAnimator.ItemHolderInfo param1ItemHolderInfo2) { RecyclerView.this.animateAppearance(param1ViewHolder, param1ItemHolderInfo1, param1ItemHolderInfo2); }
        
        public void processDisappeared(RecyclerView.ViewHolder param1ViewHolder, @NonNull RecyclerView.ItemAnimator.ItemHolderInfo param1ItemHolderInfo1, @Nullable RecyclerView.ItemAnimator.ItemHolderInfo param1ItemHolderInfo2) {
          RecyclerView.this.mRecycler.unscrapView(param1ViewHolder);
          RecyclerView.this.animateDisappearance(param1ViewHolder, param1ItemHolderInfo1, param1ItemHolderInfo2);
        }
        
        public void processPersistent(RecyclerView.ViewHolder param1ViewHolder, @NonNull RecyclerView.ItemAnimator.ItemHolderInfo param1ItemHolderInfo1, @NonNull RecyclerView.ItemAnimator.ItemHolderInfo param1ItemHolderInfo2) {
          param1ViewHolder.setIsRecyclable(false);
          if (RecyclerView.this.mDataSetHasChangedAfterLayout) {
            if (RecyclerView.this.mItemAnimator.animateChange(param1ViewHolder, param1ViewHolder, param1ItemHolderInfo1, param1ItemHolderInfo2)) {
              RecyclerView.this.postAnimationRunner();
              return;
            } 
          } else if (RecyclerView.this.mItemAnimator.animatePersistence(param1ViewHolder, param1ItemHolderInfo1, param1ItemHolderInfo2)) {
            RecyclerView.this.postAnimationRunner();
          } 
        }
        
        public void unused(RecyclerView.ViewHolder param1ViewHolder) { RecyclerView.this.mLayout.removeAndRecycleView(param1ViewHolder.itemView, RecyclerView.this.mRecycler); }
      };
    if (paramAttributeSet != null) {
      typedArray = paramContext.obtainStyledAttributes(paramAttributeSet, CLIP_TO_PADDING_ATTR, paramInt, 0);
      this.mClipToPadding = typedArray.getBoolean(0, true);
      typedArray.recycle();
    } else {
      this.mClipToPadding = true;
    } 
    setScrollContainer(true);
    setFocusableInTouchMode(true);
    ViewConfiguration viewConfiguration = ViewConfiguration.get(paramContext);
    this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
    this.mScaledHorizontalScrollFactor = ViewConfigurationCompat.getScaledHorizontalScrollFactor(viewConfiguration, paramContext);
    this.mScaledVerticalScrollFactor = ViewConfigurationCompat.getScaledVerticalScrollFactor(viewConfiguration, paramContext);
    this.mMinFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
    this.mMaxFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
    if (getOverScrollMode() == 2) {
      bool = true;
    } else {
      bool = false;
    } 
    setWillNotDraw(bool);
    this.mItemAnimator.setListener(this.mItemAnimatorListener);
    initAdapterManager();
    initChildrenHelper();
    if (ViewCompat.getImportantForAccessibility(this) == 0)
      ViewCompat.setImportantForAccessibility(this, 1); 
    this.mAccessibilityManager = (AccessibilityManager)getContext().getSystemService("accessibility");
    setAccessibilityDelegateCompat(new RecyclerViewAccessibilityDelegate(this));
    if (paramAttributeSet != null) {
      TypedArray typedArray1 = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.RecyclerView, paramInt, 0);
      String str = typedArray1.getString(R.styleable.RecyclerView_layoutManager);
      if (typedArray1.getInt(R.styleable.RecyclerView_android_descendantFocusability, -1) == -1)
        setDescendantFocusability(262144); 
      this.mEnableFastScroller = typedArray1.getBoolean(R.styleable.RecyclerView_fastScrollEnabled, false);
      if (this.mEnableFastScroller)
        initFastScroller((StateListDrawable)typedArray1.getDrawable(R.styleable.RecyclerView_fastScrollVerticalThumbDrawable), typedArray1.getDrawable(R.styleable.RecyclerView_fastScrollVerticalTrackDrawable), (StateListDrawable)typedArray1.getDrawable(R.styleable.RecyclerView_fastScrollHorizontalThumbDrawable), typedArray1.getDrawable(R.styleable.RecyclerView_fastScrollHorizontalTrackDrawable)); 
      typedArray1.recycle();
      createLayoutManager(paramContext, str, paramAttributeSet, paramInt, 0);
      bool = bool1;
      if (Build.VERSION.SDK_INT >= 21) {
        TypedArray typedArray2 = paramContext.obtainStyledAttributes(paramAttributeSet, NESTED_SCROLLING_ATTRS, paramInt, 0);
        bool = typedArray2.getBoolean(0, true);
        typedArray2.recycle();
      } 
    } else {
      setDescendantFocusability(262144);
      bool = bool1;
    } 
    setNestedScrollingEnabled(bool);
  }
  
  private void addAnimatingView(ViewHolder paramViewHolder) {
    boolean bool;
    View view = paramViewHolder.itemView;
    if (view.getParent() == this) {
      bool = true;
    } else {
      bool = false;
    } 
    this.mRecycler.unscrapView(getChildViewHolder(view));
    if (paramViewHolder.isTmpDetached()) {
      this.mChildHelper.attachViewToParent(view, -1, view.getLayoutParams(), true);
      return;
    } 
    if (!bool) {
      this.mChildHelper.addView(view, true);
      return;
    } 
    this.mChildHelper.hide(view);
  }
  
  private void animateChange(@NonNull ViewHolder paramViewHolder1, @NonNull ViewHolder paramViewHolder2, @NonNull ItemAnimator.ItemHolderInfo paramItemHolderInfo1, @NonNull ItemAnimator.ItemHolderInfo paramItemHolderInfo2, boolean paramBoolean1, boolean paramBoolean2) {
    paramViewHolder1.setIsRecyclable(false);
    if (paramBoolean1)
      addAnimatingView(paramViewHolder1); 
    if (paramViewHolder1 != paramViewHolder2) {
      if (paramBoolean2)
        addAnimatingView(paramViewHolder2); 
      paramViewHolder1.mShadowedHolder = paramViewHolder2;
      addAnimatingView(paramViewHolder1);
      this.mRecycler.unscrapView(paramViewHolder1);
      paramViewHolder2.setIsRecyclable(false);
      paramViewHolder2.mShadowingHolder = paramViewHolder1;
    } 
    if (this.mItemAnimator.animateChange(paramViewHolder1, paramViewHolder2, paramItemHolderInfo1, paramItemHolderInfo2))
      postAnimationRunner(); 
  }
  
  private void cancelTouch() {
    resetTouch();
    setScrollState(0);
  }
  
  static void clearNestedRecyclerViewIfNotNested(@NonNull ViewHolder paramViewHolder) {
    if (paramViewHolder.mNestedRecyclerView != null) {
      View view = (View)paramViewHolder.mNestedRecyclerView.get();
      while (view != null) {
        if (view == paramViewHolder.itemView)
          return; 
        ViewParent viewParent = view.getParent();
        if (viewParent instanceof View) {
          View view1 = (View)viewParent;
          continue;
        } 
        viewParent = null;
      } 
      paramViewHolder.mNestedRecyclerView = null;
    } 
  }
  
  private void createLayoutManager(Context paramContext, String paramString, AttributeSet paramAttributeSet, int paramInt1, int paramInt2) {
    if (paramString != null) {
      paramString = paramString.trim();
      if (!paramString.isEmpty()) {
        String str = getFullClassName(paramContext, paramString);
        try {
          StringBuilder stringBuilder;
          if (isInEditMode()) {
            classLoader = getClass().getClassLoader();
          } else {
            classLoader = paramContext.getClassLoader();
          } 
          Class clazz = classLoader.loadClass(str).asSubclass(LayoutManager.class);
          Context context = null;
          try {
            Constructor constructor = clazz.getConstructor(LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE);
            stringBuilder = new Object[] { paramContext, paramAttributeSet, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) };
          } catch (NoSuchMethodException paramContext) {
            try {
              Constructor constructor = clazz.getConstructor(new Class[0]);
              paramContext = context;
              constructor.setAccessible(true);
              setLayoutManager((LayoutManager)constructor.newInstance(paramContext));
              return;
            } catch (NoSuchMethodException classLoader) {
              classLoader.initCause(paramContext);
              stringBuilder = new StringBuilder();
              stringBuilder.append(paramAttributeSet.getPositionDescription());
              stringBuilder.append(": Error creating LayoutManager ");
              stringBuilder.append(str);
              throw new IllegalStateException(stringBuilder.toString(), classLoader);
            } 
          } 
          classLoader.setAccessible(true);
          setLayoutManager((LayoutManager)classLoader.newInstance(stringBuilder));
          return;
        } catch (ClassNotFoundException paramContext) {
          StringBuilder stringBuilder = new StringBuilder();
          stringBuilder.append(paramAttributeSet.getPositionDescription());
          stringBuilder.append(": Unable to find LayoutManager ");
          stringBuilder.append(str);
          throw new IllegalStateException(stringBuilder.toString(), paramContext);
        } catch (InvocationTargetException paramContext) {
          StringBuilder stringBuilder = new StringBuilder();
          stringBuilder.append(paramAttributeSet.getPositionDescription());
          stringBuilder.append(": Could not instantiate the LayoutManager: ");
          stringBuilder.append(str);
          throw new IllegalStateException(stringBuilder.toString(), paramContext);
        } catch (InstantiationException paramContext) {
          StringBuilder stringBuilder = new StringBuilder();
          stringBuilder.append(paramAttributeSet.getPositionDescription());
          stringBuilder.append(": Could not instantiate the LayoutManager: ");
          stringBuilder.append(str);
          throw new IllegalStateException(stringBuilder.toString(), paramContext);
        } catch (IllegalAccessException paramContext) {
          StringBuilder stringBuilder = new StringBuilder();
          stringBuilder.append(paramAttributeSet.getPositionDescription());
          stringBuilder.append(": Cannot access non-public constructor ");
          stringBuilder.append(str);
          throw new IllegalStateException(stringBuilder.toString(), paramContext);
        } catch (ClassCastException paramContext) {
          StringBuilder stringBuilder = new StringBuilder();
          stringBuilder.append(paramAttributeSet.getPositionDescription());
          stringBuilder.append(": Class is not a LayoutManager ");
          stringBuilder.append(str);
          throw new IllegalStateException(stringBuilder.toString(), paramContext);
        } 
      } 
    } 
  }
  
  private boolean didChildRangeChange(int paramInt1, int paramInt2) {
    findMinMaxChildLayoutPositions(this.mMinMaxLayoutPositions);
    int[] arrayOfInt = this.mMinMaxLayoutPositions;
    boolean bool = false;
    if (arrayOfInt[0] != paramInt1 || this.mMinMaxLayoutPositions[1] != paramInt2)
      bool = true; 
    return bool;
  }
  
  private void dispatchContentChangedIfNecessary() {
    int i = this.mEatenAccessibilityChangeFlags;
    this.mEatenAccessibilityChangeFlags = 0;
    if (i != 0 && isAccessibilityEnabled()) {
      AccessibilityEvent accessibilityEvent = AccessibilityEvent.obtain();
      accessibilityEvent.setEventType(2048);
      AccessibilityEventCompat.setContentChangeTypes(accessibilityEvent, i);
      sendAccessibilityEventUnchecked(accessibilityEvent);
    } 
  }
  
  private void dispatchLayoutStep1() {
    State state = this.mState;
    boolean bool = true;
    state.assertLayoutStep(1);
    fillRemainingScrollValues(this.mState);
    this.mState.mIsMeasuring = false;
    startInterceptRequestLayout();
    this.mViewInfoStore.clear();
    onEnterLayoutOrScroll();
    processAdapterUpdatesAndSetAnimationFlags();
    saveFocusInfo();
    state = this.mState;
    if (!this.mState.mRunSimpleAnimations || !this.mItemsChanged)
      bool = false; 
    state.mTrackOldChangeHolders = bool;
    this.mItemsChanged = false;
    this.mItemsAddedOrRemoved = false;
    this.mState.mInPreLayout = this.mState.mRunPredictiveAnimations;
    this.mState.mItemCount = this.mAdapter.getItemCount();
    findMinMaxChildLayoutPositions(this.mMinMaxLayoutPositions);
    if (this.mState.mRunSimpleAnimations) {
      int i = this.mChildHelper.getChildCount();
      for (byte b = 0; b < i; b++) {
        ViewHolder viewHolder = getChildViewHolderInt(this.mChildHelper.getChildAt(b));
        if (!viewHolder.shouldIgnore() && (!viewHolder.isInvalid() || this.mAdapter.hasStableIds())) {
          ItemAnimator.ItemHolderInfo itemHolderInfo = this.mItemAnimator.recordPreLayoutInformation(this.mState, viewHolder, ItemAnimator.buildAdapterChangeFlagsForAnimations(viewHolder), viewHolder.getUnmodifiedPayloads());
          this.mViewInfoStore.addToPreLayout(viewHolder, itemHolderInfo);
          if (this.mState.mTrackOldChangeHolders && viewHolder.isUpdated() && !viewHolder.isRemoved() && !viewHolder.shouldIgnore() && !viewHolder.isInvalid()) {
            long l = getChangedHolderKey(viewHolder);
            this.mViewInfoStore.addToOldChangeHolders(l, viewHolder);
          } 
        } 
      } 
    } 
    if (this.mState.mRunPredictiveAnimations) {
      saveOldPositions();
      bool = this.mState.mStructureChanged;
      this.mState.mStructureChanged = false;
      this.mLayout.onLayoutChildren(this.mRecycler, this.mState);
      this.mState.mStructureChanged = bool;
      for (byte b = 0; b < this.mChildHelper.getChildCount(); b++) {
        ViewHolder viewHolder = getChildViewHolderInt(this.mChildHelper.getChildAt(b));
        if (!viewHolder.shouldIgnore() && !this.mViewInfoStore.isInPreLayout(viewHolder)) {
          int j = ItemAnimator.buildAdapterChangeFlagsForAnimations(viewHolder);
          bool = viewHolder.hasAnyOfTheFlags(8192);
          int i = j;
          if (!bool)
            i = j | 0x1000; 
          ItemAnimator.ItemHolderInfo itemHolderInfo = this.mItemAnimator.recordPreLayoutInformation(this.mState, viewHolder, i, viewHolder.getUnmodifiedPayloads());
          if (bool) {
            recordAnimationInfoIfBouncedHiddenView(viewHolder, itemHolderInfo);
          } else {
            this.mViewInfoStore.addToAppearedInPreLayoutHolders(viewHolder, itemHolderInfo);
          } 
        } 
      } 
      clearOldPositions();
    } else {
      clearOldPositions();
    } 
    onExitLayoutOrScroll();
    stopInterceptRequestLayout(false);
    this.mState.mLayoutStep = 2;
  }
  
  private void dispatchLayoutStep2() {
    boolean bool;
    startInterceptRequestLayout();
    onEnterLayoutOrScroll();
    this.mState.assertLayoutStep(6);
    this.mAdapterHelper.consumeUpdatesInOnePass();
    this.mState.mItemCount = this.mAdapter.getItemCount();
    this.mState.mDeletedInvisibleItemCountSincePreviousLayout = 0;
    this.mState.mInPreLayout = false;
    this.mLayout.onLayoutChildren(this.mRecycler, this.mState);
    this.mState.mStructureChanged = false;
    this.mPendingSavedState = null;
    State state = this.mState;
    if (this.mState.mRunSimpleAnimations && this.mItemAnimator != null) {
      bool = true;
    } else {
      bool = false;
    } 
    state.mRunSimpleAnimations = bool;
    this.mState.mLayoutStep = 4;
    onExitLayoutOrScroll();
    stopInterceptRequestLayout(false);
  }
  
  private void dispatchLayoutStep3() {
    this.mState.assertLayoutStep(4);
    startInterceptRequestLayout();
    onEnterLayoutOrScroll();
    this.mState.mLayoutStep = 1;
    if (this.mState.mRunSimpleAnimations) {
      for (int i = this.mChildHelper.getChildCount() - 1; i >= 0; i--) {
        ViewHolder viewHolder = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
        if (!viewHolder.shouldIgnore()) {
          long l = getChangedHolderKey(viewHolder);
          ItemAnimator.ItemHolderInfo itemHolderInfo = this.mItemAnimator.recordPostLayoutInformation(this.mState, viewHolder);
          ViewHolder viewHolder1 = this.mViewInfoStore.getFromOldChangeHolders(l);
          if (viewHolder1 != null && !viewHolder1.shouldIgnore()) {
            boolean bool1 = this.mViewInfoStore.isDisappearing(viewHolder1);
            boolean bool2 = this.mViewInfoStore.isDisappearing(viewHolder);
            if (bool1 && viewHolder1 == viewHolder) {
              this.mViewInfoStore.addToPostLayout(viewHolder, itemHolderInfo);
            } else {
              ItemAnimator.ItemHolderInfo itemHolderInfo1 = this.mViewInfoStore.popFromPreLayout(viewHolder1);
              this.mViewInfoStore.addToPostLayout(viewHolder, itemHolderInfo);
              itemHolderInfo = this.mViewInfoStore.popFromPostLayout(viewHolder);
              if (itemHolderInfo1 == null) {
                handleMissingPreInfoForChangeError(l, viewHolder, viewHolder1);
              } else {
                animateChange(viewHolder1, viewHolder, itemHolderInfo1, itemHolderInfo, bool1, bool2);
              } 
            } 
          } else {
            this.mViewInfoStore.addToPostLayout(viewHolder, itemHolderInfo);
          } 
        } 
      } 
      this.mViewInfoStore.process(this.mViewInfoProcessCallback);
    } 
    this.mLayout.removeAndRecycleScrapInt(this.mRecycler);
    this.mState.mPreviousLayoutItemCount = this.mState.mItemCount;
    this.mDataSetHasChangedAfterLayout = false;
    this.mDispatchItemsChangedEvent = false;
    this.mState.mRunSimpleAnimations = false;
    this.mState.mRunPredictiveAnimations = false;
    this.mLayout.mRequestedSimpleAnimations = false;
    if (this.mRecycler.mChangedScrap != null)
      this.mRecycler.mChangedScrap.clear(); 
    if (this.mLayout.mPrefetchMaxObservedInInitialPrefetch) {
      this.mLayout.mPrefetchMaxCountObserved = 0;
      this.mLayout.mPrefetchMaxObservedInInitialPrefetch = false;
      this.mRecycler.updateViewCacheSize();
    } 
    this.mLayout.onLayoutCompleted(this.mState);
    onExitLayoutOrScroll();
    stopInterceptRequestLayout(false);
    this.mViewInfoStore.clear();
    if (didChildRangeChange(this.mMinMaxLayoutPositions[0], this.mMinMaxLayoutPositions[1]))
      dispatchOnScrolled(0, 0); 
    recoverFocusFromState();
    resetFocusInfo();
  }
  
  private boolean dispatchOnItemTouch(MotionEvent paramMotionEvent) {
    int i = paramMotionEvent.getAction();
    if (this.mActiveOnItemTouchListener != null)
      if (i == 0) {
        this.mActiveOnItemTouchListener = null;
      } else {
        this.mActiveOnItemTouchListener.onTouchEvent(this, paramMotionEvent);
        if (i == 3 || i == 1)
          this.mActiveOnItemTouchListener = null; 
        return true;
      }  
    if (i != 0) {
      int j = this.mOnItemTouchListeners.size();
      for (i = 0; i < j; i++) {
        OnItemTouchListener onItemTouchListener = (OnItemTouchListener)this.mOnItemTouchListeners.get(i);
        if (onItemTouchListener.onInterceptTouchEvent(this, paramMotionEvent)) {
          this.mActiveOnItemTouchListener = onItemTouchListener;
          return true;
        } 
      } 
    } 
    return false;
  }
  
  private boolean dispatchOnItemTouchIntercept(MotionEvent paramMotionEvent) {
    int i = paramMotionEvent.getAction();
    if (i == 3 || i == 0)
      this.mActiveOnItemTouchListener = null; 
    int j = this.mOnItemTouchListeners.size();
    for (byte b = 0; b < j; b++) {
      OnItemTouchListener onItemTouchListener = (OnItemTouchListener)this.mOnItemTouchListeners.get(b);
      if (onItemTouchListener.onInterceptTouchEvent(this, paramMotionEvent) && i != 3) {
        this.mActiveOnItemTouchListener = onItemTouchListener;
        return true;
      } 
    } 
    return false;
  }
  
  private void findMinMaxChildLayoutPositions(int[] paramArrayOfInt) {
    int k = this.mChildHelper.getChildCount();
    if (k == 0) {
      paramArrayOfInt[0] = -1;
      paramArrayOfInt[1] = -1;
      return;
    } 
    byte b = 0;
    int i = Integer.MAX_VALUE;
    int j;
    for (j = Integer.MIN_VALUE; b < k; j = m) {
      int m;
      ViewHolder viewHolder = getChildViewHolderInt(this.mChildHelper.getChildAt(b));
      if (viewHolder.shouldIgnore()) {
        m = j;
      } else {
        int i1 = viewHolder.getLayoutPosition();
        int n = i;
        if (i1 < i)
          n = i1; 
        i = n;
        m = j;
        if (i1 > j) {
          m = i1;
          i = n;
        } 
      } 
      b++;
    } 
    paramArrayOfInt[0] = i;
    paramArrayOfInt[1] = j;
  }
  
  @Nullable
  static RecyclerView findNestedRecyclerView(@NonNull View paramView) {
    if (!(paramView instanceof ViewGroup))
      return null; 
    if (paramView instanceof RecyclerView)
      return (RecyclerView)paramView; 
    ViewGroup viewGroup = (ViewGroup)paramView;
    int i = viewGroup.getChildCount();
    for (byte b = 0; b < i; b++) {
      RecyclerView recyclerView = findNestedRecyclerView(viewGroup.getChildAt(b));
      if (recyclerView != null)
        return recyclerView; 
    } 
    return null;
  }
  
  @Nullable
  private View findNextViewToFocus() {
    if (this.mState.mFocusedItemPosition != -1) {
      i = this.mState.mFocusedItemPosition;
    } else {
      i = 0;
    } 
    int j = this.mState.getItemCount();
    for (byte b = i; b < j; b++) {
      ViewHolder viewHolder = findViewHolderForAdapterPosition(b);
      if (viewHolder == null)
        break; 
      if (viewHolder.itemView.hasFocusable())
        return viewHolder.itemView; 
    } 
    for (int i = Math.min(j, i) - 1; i >= 0; i--) {
      ViewHolder viewHolder = findViewHolderForAdapterPosition(i);
      if (viewHolder == null)
        return null; 
      if (viewHolder.itemView.hasFocusable())
        return viewHolder.itemView; 
    } 
    return null;
  }
  
  static ViewHolder getChildViewHolderInt(View paramView) { return (paramView == null) ? null : ((LayoutParams)paramView.getLayoutParams()).mViewHolder; }
  
  static void getDecoratedBoundsWithMarginsInt(View paramView, Rect paramRect) {
    LayoutParams layoutParams = (LayoutParams)paramView.getLayoutParams();
    Rect rect = layoutParams.mDecorInsets;
    paramRect.set(paramView.getLeft() - rect.left - layoutParams.leftMargin, paramView.getTop() - rect.top - layoutParams.topMargin, paramView.getRight() + rect.right + layoutParams.rightMargin, paramView.getBottom() + rect.bottom + layoutParams.bottomMargin);
  }
  
  private int getDeepestFocusedViewWithId(View paramView) {
    int i = paramView.getId();
    while (!paramView.isFocused() && paramView instanceof ViewGroup && paramView.hasFocus()) {
      View view = ((ViewGroup)paramView).getFocusedChild();
      paramView = view;
      if (view.getId() != -1) {
        i = view.getId();
        paramView = view;
      } 
    } 
    return i;
  }
  
  private String getFullClassName(Context paramContext, String paramString) {
    if (paramString.charAt(0) == '.') {
      StringBuilder stringBuilder1 = new StringBuilder();
      stringBuilder1.append(paramContext.getPackageName());
      stringBuilder1.append(paramString);
      return stringBuilder1.toString();
    } 
    if (paramString.contains("."))
      return paramString; 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(RecyclerView.class.getPackage().getName());
    stringBuilder.append('.');
    stringBuilder.append(paramString);
    return stringBuilder.toString();
  }
  
  private NestedScrollingChildHelper getScrollingChildHelper() {
    if (this.mScrollingChildHelper == null)
      this.mScrollingChildHelper = new NestedScrollingChildHelper(this); 
    return this.mScrollingChildHelper;
  }
  
  private void handleMissingPreInfoForChangeError(long paramLong, ViewHolder paramViewHolder1, ViewHolder paramViewHolder2) {
    StringBuilder stringBuilder1;
    int i = this.mChildHelper.getChildCount();
    byte b;
    for (b = 0; b < i; b++) {
      ViewHolder viewHolder = getChildViewHolderInt(this.mChildHelper.getChildAt(b));
      if (viewHolder != paramViewHolder1 && getChangedHolderKey(viewHolder) == paramLong) {
        if (this.mAdapter != null && this.mAdapter.hasStableIds()) {
          StringBuilder stringBuilder = new StringBuilder();
          stringBuilder.append("Two different ViewHolders have the same stable ID. Stable IDs in your adapter MUST BE unique and SHOULD NOT change.\n ViewHolder 1:");
          stringBuilder.append(viewHolder);
          stringBuilder.append(" \n View Holder 2:");
          stringBuilder.append(paramViewHolder1);
          stringBuilder.append(exceptionLabel());
          throw new IllegalStateException(stringBuilder.toString());
        } 
        stringBuilder1 = new StringBuilder();
        stringBuilder1.append("Two different ViewHolders have the same change ID. This might happen due to inconsistent Adapter update events or if the LayoutManager lays out the same View multiple times.\n ViewHolder 1:");
        stringBuilder1.append(viewHolder);
        stringBuilder1.append(" \n View Holder 2:");
        stringBuilder1.append(paramViewHolder1);
        stringBuilder1.append(exceptionLabel());
        throw new IllegalStateException(stringBuilder1.toString());
      } 
    } 
    StringBuilder stringBuilder2 = new StringBuilder();
    stringBuilder2.append("Problem while matching changed view holders with the newones. The pre-layout information for the change holder ");
    stringBuilder2.append(stringBuilder1);
    stringBuilder2.append(" cannot be found but it is necessary for ");
    stringBuilder2.append(paramViewHolder1);
    stringBuilder2.append(exceptionLabel());
    Log.e("RecyclerView", stringBuilder2.toString());
  }
  
  private boolean hasUpdatedView() {
    int i = this.mChildHelper.getChildCount();
    for (byte b = 0; b < i; b++) {
      ViewHolder viewHolder = getChildViewHolderInt(this.mChildHelper.getChildAt(b));
      if (viewHolder != null && !viewHolder.shouldIgnore() && viewHolder.isUpdated())
        return true; 
    } 
    return false;
  }
  
  private void initChildrenHelper() { this.mChildHelper = new ChildHelper(new ChildHelper.Callback(this) {
          public void addView(View param1View, int param1Int) {
            RecyclerView.this.addView(param1View, param1Int);
            RecyclerView.this.dispatchChildAttached(param1View);
          }
          
          public void attachViewToParent(View param1View, int param1Int, ViewGroup.LayoutParams param1LayoutParams) {
            StringBuilder stringBuilder;
            RecyclerView.ViewHolder viewHolder = RecyclerView.getChildViewHolderInt(param1View);
            if (viewHolder != null) {
              if (!viewHolder.isTmpDetached() && !viewHolder.shouldIgnore()) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("Called attach on a child which is not detached: ");
                stringBuilder.append(viewHolder);
                stringBuilder.append(RecyclerView.this.exceptionLabel());
                throw new IllegalArgumentException(stringBuilder.toString());
              } 
              viewHolder.clearTmpDetachFlag();
            } 
            RecyclerView.this.attachViewToParent(stringBuilder, param1Int, param1LayoutParams);
          }
          
          public void detachViewFromParent(int param1Int) {
            View view = getChildAt(param1Int);
            if (view != null) {
              RecyclerView.ViewHolder viewHolder = RecyclerView.getChildViewHolderInt(view);
              if (viewHolder != null) {
                if (viewHolder.isTmpDetached() && !viewHolder.shouldIgnore()) {
                  StringBuilder stringBuilder = new StringBuilder();
                  stringBuilder.append("called detach on an already detached child ");
                  stringBuilder.append(viewHolder);
                  stringBuilder.append(RecyclerView.this.exceptionLabel());
                  throw new IllegalArgumentException(stringBuilder.toString());
                } 
                viewHolder.addFlags(256);
              } 
            } 
            RecyclerView.this.detachViewFromParent(param1Int);
          }
          
          public View getChildAt(int param1Int) { return RecyclerView.this.getChildAt(param1Int); }
          
          public int getChildCount() { return RecyclerView.this.getChildCount(); }
          
          public RecyclerView.ViewHolder getChildViewHolder(View param1View) { return RecyclerView.getChildViewHolderInt(param1View); }
          
          public int indexOfChild(View param1View) { return RecyclerView.this.indexOfChild(param1View); }
          
          public void onEnteredHiddenState(View param1View) {
            RecyclerView.ViewHolder viewHolder = RecyclerView.getChildViewHolderInt(param1View);
            if (viewHolder != null)
              viewHolder.onEnteredHiddenState(RecyclerView.this); 
          }
          
          public void onLeftHiddenState(View param1View) {
            RecyclerView.ViewHolder viewHolder = RecyclerView.getChildViewHolderInt(param1View);
            if (viewHolder != null)
              viewHolder.onLeftHiddenState(RecyclerView.this); 
          }
          
          public void removeAllViews() {
            int i = getChildCount();
            for (byte b = 0; b < i; b++) {
              View view = getChildAt(b);
              RecyclerView.this.dispatchChildDetached(view);
              view.clearAnimation();
            } 
            RecyclerView.this.removeAllViews();
          }
          
          public void removeViewAt(int param1Int) {
            View view = RecyclerView.this.getChildAt(param1Int);
            if (view != null) {
              RecyclerView.this.dispatchChildDetached(view);
              view.clearAnimation();
            } 
            RecyclerView.this.removeViewAt(param1Int);
          }
        }); }
  
  private boolean isPreferredNextFocus(View paramView1, View paramView2, int paramInt) {
    byte b2 = 0;
    int i = 0;
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    byte b1 = 0;
    if (paramView2 != null) {
      int k;
      if (paramView2 == this)
        return false; 
      if (findContainingItemView(paramView2) == null)
        return false; 
      if (paramView1 == null)
        return true; 
      if (findContainingItemView(paramView1) == null)
        return true; 
      this.mTempRect.set(0, 0, paramView1.getWidth(), paramView1.getHeight());
      this.mTempRect2.set(0, 0, paramView2.getWidth(), paramView2.getHeight());
      offsetDescendantRectToMyCoords(paramView1, this.mTempRect);
      offsetDescendantRectToMyCoords(paramView2, this.mTempRect2);
      int j = this.mLayout.getLayoutDirection();
      byte b = -1;
      if (j == 1) {
        k = -1;
      } else {
        k = 1;
      } 
      if ((this.mTempRect.left < this.mTempRect2.left || this.mTempRect.right <= this.mTempRect2.left) && this.mTempRect.right < this.mTempRect2.right) {
        j = 1;
      } else if ((this.mTempRect.right > this.mTempRect2.right || this.mTempRect.left >= this.mTempRect2.right) && this.mTempRect.left > this.mTempRect2.left) {
        j = -1;
      } else {
        j = 0;
      } 
      if ((this.mTempRect.top < this.mTempRect2.top || this.mTempRect.bottom <= this.mTempRect2.top) && this.mTempRect.bottom < this.mTempRect2.bottom) {
        b = 1;
      } else if ((this.mTempRect.bottom <= this.mTempRect2.bottom && this.mTempRect.top < this.mTempRect2.bottom) || this.mTempRect.top <= this.mTempRect2.top) {
        b = 0;
      } 
      if (paramInt != 17) {
        if (paramInt != 33) {
          if (paramInt != 66) {
            if (paramInt != 130) {
              StringBuilder stringBuilder;
              switch (paramInt) {
                default:
                  stringBuilder = new StringBuilder();
                  stringBuilder.append("Invalid direction: ");
                  stringBuilder.append(paramInt);
                  stringBuilder.append(exceptionLabel());
                  throw new IllegalArgumentException(stringBuilder.toString());
                case 2:
                  if (b <= 0) {
                    i = b1;
                    if (b == 0) {
                      i = b1;
                      if (j * k >= 0)
                        return true; 
                    } 
                    return i;
                  } 
                  return true;
                case 1:
                  break;
              } 
              if (b >= 0) {
                int m = b2;
                if (b == 0) {
                  m = b2;
                  if (j * k <= 0)
                    return true; 
                } 
                return m;
              } 
            } else {
              boolean bool;
              if (b > 0)
                bool = true; 
              return bool;
            } 
          } else {
            boolean bool = bool1;
            if (j > 0)
              bool = true; 
            return bool;
          } 
        } else {
          boolean bool = bool2;
          if (b < 0)
            bool = true; 
          return bool;
        } 
      } else {
        boolean bool = bool3;
        if (j < 0)
          bool = true; 
        return bool;
      } 
    } else {
      return false;
    } 
    return true;
  }
  
  private void onPointerUp(MotionEvent paramMotionEvent) {
    int i = paramMotionEvent.getActionIndex();
    if (paramMotionEvent.getPointerId(i) == this.mScrollPointerId) {
      if (i == 0) {
        i = 1;
      } else {
        i = 0;
      } 
      this.mScrollPointerId = paramMotionEvent.getPointerId(i);
      int j = (int)(paramMotionEvent.getX(i) + 0.5F);
      this.mLastTouchX = j;
      this.mInitialTouchX = j;
      i = (int)(paramMotionEvent.getY(i) + 0.5F);
      this.mLastTouchY = i;
      this.mInitialTouchY = i;
    } 
  }
  
  private boolean predictiveItemAnimationsEnabled() { return (this.mItemAnimator != null && this.mLayout.supportsPredictiveItemAnimations()); }
  
  private void processAdapterUpdatesAndSetAnimationFlags() {
    boolean bool1;
    if (this.mDataSetHasChangedAfterLayout) {
      this.mAdapterHelper.reset();
      if (this.mDispatchItemsChangedEvent)
        this.mLayout.onItemsChanged(this); 
    } 
    if (predictiveItemAnimationsEnabled()) {
      this.mAdapterHelper.preProcess();
    } else {
      this.mAdapterHelper.consumeUpdatesInOnePass();
    } 
    boolean bool = this.mItemsAddedOrRemoved;
    boolean bool2 = true;
    if (bool || this.mItemsChanged) {
      bool1 = true;
    } else {
      bool1 = false;
    } 
    State state = this.mState;
    if (this.mFirstLayoutComplete && this.mItemAnimator != null && (this.mDataSetHasChangedAfterLayout || bool1 || this.mLayout.mRequestedSimpleAnimations) && (!this.mDataSetHasChangedAfterLayout || this.mAdapter.hasStableIds())) {
      bool = true;
    } else {
      bool = false;
    } 
    state.mRunSimpleAnimations = bool;
    state = this.mState;
    if (this.mState.mRunSimpleAnimations && bool1 && !this.mDataSetHasChangedAfterLayout && predictiveItemAnimationsEnabled()) {
      bool = bool2;
    } else {
      bool = false;
    } 
    state.mRunPredictiveAnimations = bool;
  }
  
  private void pullGlows(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) { // Byte code:
    //   0: iconst_1
    //   1: istore #6
    //   3: fload_2
    //   4: fconst_0
    //   5: fcmpg
    //   6: ifge -> 43
    //   9: aload_0
    //   10: invokevirtual ensureLeftGlow : ()V
    //   13: aload_0
    //   14: getfield mLeftGlow : Landroid/widget/EdgeEffect;
    //   17: fload_2
    //   18: fneg
    //   19: aload_0
    //   20: invokevirtual getWidth : ()I
    //   23: i2f
    //   24: fdiv
    //   25: fconst_1
    //   26: fload_3
    //   27: aload_0
    //   28: invokevirtual getHeight : ()I
    //   31: i2f
    //   32: fdiv
    //   33: fsub
    //   34: invokestatic onPull : (Landroid/widget/EdgeEffect;FF)V
    //   37: iconst_1
    //   38: istore #5
    //   40: goto -> 80
    //   43: fload_2
    //   44: fconst_0
    //   45: fcmpl
    //   46: ifle -> 77
    //   49: aload_0
    //   50: invokevirtual ensureRightGlow : ()V
    //   53: aload_0
    //   54: getfield mRightGlow : Landroid/widget/EdgeEffect;
    //   57: fload_2
    //   58: aload_0
    //   59: invokevirtual getWidth : ()I
    //   62: i2f
    //   63: fdiv
    //   64: fload_3
    //   65: aload_0
    //   66: invokevirtual getHeight : ()I
    //   69: i2f
    //   70: fdiv
    //   71: invokestatic onPull : (Landroid/widget/EdgeEffect;FF)V
    //   74: goto -> 37
    //   77: iconst_0
    //   78: istore #5
    //   80: fload #4
    //   82: fconst_0
    //   83: fcmpg
    //   84: ifge -> 121
    //   87: aload_0
    //   88: invokevirtual ensureTopGlow : ()V
    //   91: aload_0
    //   92: getfield mTopGlow : Landroid/widget/EdgeEffect;
    //   95: fload #4
    //   97: fneg
    //   98: aload_0
    //   99: invokevirtual getHeight : ()I
    //   102: i2f
    //   103: fdiv
    //   104: fload_1
    //   105: aload_0
    //   106: invokevirtual getWidth : ()I
    //   109: i2f
    //   110: fdiv
    //   111: invokestatic onPull : (Landroid/widget/EdgeEffect;FF)V
    //   114: iload #6
    //   116: istore #5
    //   118: goto -> 163
    //   121: fload #4
    //   123: fconst_0
    //   124: fcmpl
    //   125: ifle -> 163
    //   128: aload_0
    //   129: invokevirtual ensureBottomGlow : ()V
    //   132: aload_0
    //   133: getfield mBottomGlow : Landroid/widget/EdgeEffect;
    //   136: fload #4
    //   138: aload_0
    //   139: invokevirtual getHeight : ()I
    //   142: i2f
    //   143: fdiv
    //   144: fconst_1
    //   145: fload_1
    //   146: aload_0
    //   147: invokevirtual getWidth : ()I
    //   150: i2f
    //   151: fdiv
    //   152: fsub
    //   153: invokestatic onPull : (Landroid/widget/EdgeEffect;FF)V
    //   156: iload #6
    //   158: istore #5
    //   160: goto -> 163
    //   163: iload #5
    //   165: ifne -> 181
    //   168: fload_2
    //   169: fconst_0
    //   170: fcmpl
    //   171: ifne -> 181
    //   174: fload #4
    //   176: fconst_0
    //   177: fcmpl
    //   178: ifeq -> 185
    //   181: aload_0
    //   182: invokestatic postInvalidateOnAnimation : (Landroid/view/View;)V
    //   185: return }
  
  private void recoverFocusFromState() {
    if (this.mPreserveFocusAfterLayout && this.mAdapter != null && hasFocus() && getDescendantFocusability() != 393216) {
      View view1;
      if (getDescendantFocusability() == 131072 && isFocused())
        return; 
      if (!isFocused()) {
        view1 = getFocusedChild();
        if (IGNORE_DETACHED_FOCUSED_CHILD && (view1.getParent() == null || !view1.hasFocus())) {
          if (this.mChildHelper.getChildCount() == 0) {
            requestFocus();
            return;
          } 
        } else if (!this.mChildHelper.isHidden(view1)) {
          return;
        } 
      } 
      long l = this.mState.mFocusedItemId;
      View view2 = null;
      if (l != -1L && this.mAdapter.hasStableIds()) {
        view1 = findViewHolderForItemId(this.mState.mFocusedItemId);
      } else {
        view1 = null;
      } 
      if (view1 == null || this.mChildHelper.isHidden(view1.itemView) || !view1.itemView.hasFocusable()) {
        view1 = view2;
        if (this.mChildHelper.getChildCount() > 0)
          view1 = findNextViewToFocus(); 
      } else {
        view1 = view1.itemView;
      } 
      if (view1 != null) {
        view2 = view1;
        if (this.mState.mFocusedSubChildId != -1L) {
          View view = view1.findViewById(this.mState.mFocusedSubChildId);
          view2 = view1;
          if (view != null) {
            view2 = view1;
            if (view.isFocusable())
              view2 = view; 
          } 
        } 
        view2.requestFocus();
      } 
      return;
    } 
  }
  
  private void releaseGlows() {
    if (this.mLeftGlow != null) {
      this.mLeftGlow.onRelease();
      bool2 = this.mLeftGlow.isFinished();
    } else {
      bool2 = false;
    } 
    boolean bool1 = bool2;
    if (this.mTopGlow != null) {
      this.mTopGlow.onRelease();
      bool1 = bool2 | this.mTopGlow.isFinished();
    } 
    boolean bool2 = bool1;
    if (this.mRightGlow != null) {
      this.mRightGlow.onRelease();
      bool2 = bool1 | this.mRightGlow.isFinished();
    } 
    bool1 = bool2;
    if (this.mBottomGlow != null) {
      this.mBottomGlow.onRelease();
      bool1 = bool2 | this.mBottomGlow.isFinished();
    } 
    if (bool1)
      ViewCompat.postInvalidateOnAnimation(this); 
  }
  
  private void requestChildOnScreen(@NonNull View paramView1, @Nullable View paramView2) {
    View view;
    boolean bool1;
    if (paramView2 != null) {
      view = paramView2;
    } else {
      view = paramView1;
    } 
    this.mTempRect.set(0, 0, view.getWidth(), view.getHeight());
    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
    if (layoutParams instanceof LayoutParams) {
      LayoutParams layoutParams1 = (LayoutParams)layoutParams;
      if (!layoutParams1.mInsetsDirty) {
        Rect rect1 = layoutParams1.mDecorInsets;
        Rect rect2 = this.mTempRect;
        rect2.left -= rect1.left;
        rect2 = this.mTempRect;
        rect2.right += rect1.right;
        rect2 = this.mTempRect;
        rect2.top -= rect1.top;
        rect2 = this.mTempRect;
        rect2.bottom += rect1.bottom;
      } 
    } 
    if (paramView2 != null) {
      offsetDescendantRectToMyCoords(paramView2, this.mTempRect);
      offsetRectIntoDescendantCoords(paramView1, this.mTempRect);
    } 
    LayoutManager layoutManager = this.mLayout;
    Rect rect = this.mTempRect;
    boolean bool2 = this.mFirstLayoutComplete;
    if (paramView2 == null) {
      bool1 = true;
    } else {
      bool1 = false;
    } 
    layoutManager.requestChildRectangleOnScreen(this, paramView1, rect, bool2 ^ true, bool1);
  }
  
  private void resetFocusInfo() {
    this.mState.mFocusedItemId = -1L;
    this.mState.mFocusedItemPosition = -1;
    this.mState.mFocusedSubChildId = -1;
  }
  
  private void resetTouch() {
    if (this.mVelocityTracker != null)
      this.mVelocityTracker.clear(); 
    stopNestedScroll(0);
    releaseGlows();
  }
  
  private void saveFocusInfo() {
    ViewHolder viewHolder;
    long l;
    int i;
    boolean bool = this.mPreserveFocusAfterLayout;
    State state = null;
    if (bool && hasFocus() && this.mAdapter != null) {
      viewHolder = getFocusedChild();
    } else {
      viewHolder = null;
    } 
    if (viewHolder == null) {
      viewHolder = state;
    } else {
      viewHolder = findContainingViewHolder(viewHolder);
    } 
    if (viewHolder == null) {
      resetFocusInfo();
      return;
    } 
    state = this.mState;
    if (this.mAdapter.hasStableIds()) {
      l = viewHolder.getItemId();
    } else {
      l = -1L;
    } 
    state.mFocusedItemId = l;
    state = this.mState;
    if (this.mDataSetHasChangedAfterLayout) {
      i = -1;
    } else if (viewHolder.isRemoved()) {
      i = viewHolder.mOldPosition;
    } else {
      i = viewHolder.getAdapterPosition();
    } 
    state.mFocusedItemPosition = i;
    this.mState.mFocusedSubChildId = getDeepestFocusedViewWithId(viewHolder.itemView);
  }
  
  private void setAdapterInternal(Adapter paramAdapter, boolean paramBoolean1, boolean paramBoolean2) {
    if (this.mAdapter != null) {
      this.mAdapter.unregisterAdapterDataObserver(this.mObserver);
      this.mAdapter.onDetachedFromRecyclerView(this);
    } 
    if (!paramBoolean1 || paramBoolean2)
      removeAndRecycleViews(); 
    this.mAdapterHelper.reset();
    Adapter adapter = this.mAdapter;
    this.mAdapter = paramAdapter;
    if (paramAdapter != null) {
      paramAdapter.registerAdapterDataObserver(this.mObserver);
      paramAdapter.onAttachedToRecyclerView(this);
    } 
    if (this.mLayout != null)
      this.mLayout.onAdapterChanged(adapter, this.mAdapter); 
    this.mRecycler.onAdapterChanged(adapter, this.mAdapter, paramBoolean1);
    this.mState.mStructureChanged = true;
  }
  
  private void stopScrollersInternal() {
    this.mViewFlinger.stop();
    if (this.mLayout != null)
      this.mLayout.stopSmoothScroller(); 
  }
  
  void absorbGlows(int paramInt1, int paramInt2) {
    if (paramInt1 < 0) {
      ensureLeftGlow();
      this.mLeftGlow.onAbsorb(-paramInt1);
    } else if (paramInt1 > 0) {
      ensureRightGlow();
      this.mRightGlow.onAbsorb(paramInt1);
    } 
    if (paramInt2 < 0) {
      ensureTopGlow();
      this.mTopGlow.onAbsorb(-paramInt2);
    } else if (paramInt2 > 0) {
      ensureBottomGlow();
      this.mBottomGlow.onAbsorb(paramInt2);
    } 
    if (paramInt1 != 0 || paramInt2 != 0)
      ViewCompat.postInvalidateOnAnimation(this); 
  }
  
  public void addFocusables(ArrayList<View> paramArrayList, int paramInt1, int paramInt2) {
    if (this.mLayout == null || !this.mLayout.onAddFocusables(this, paramArrayList, paramInt1, paramInt2))
      super.addFocusables(paramArrayList, paramInt1, paramInt2); 
  }
  
  public void addItemDecoration(ItemDecoration paramItemDecoration) { addItemDecoration(paramItemDecoration, -1); }
  
  public void addItemDecoration(ItemDecoration paramItemDecoration, int paramInt) {
    if (this.mLayout != null)
      this.mLayout.assertNotInLayoutOrScroll("Cannot add item decoration during a scroll  or layout"); 
    if (this.mItemDecorations.isEmpty())
      setWillNotDraw(false); 
    if (paramInt < 0) {
      this.mItemDecorations.add(paramItemDecoration);
    } else {
      this.mItemDecorations.add(paramInt, paramItemDecoration);
    } 
    markItemDecorInsetsDirty();
    requestLayout();
  }
  
  public void addOnChildAttachStateChangeListener(OnChildAttachStateChangeListener paramOnChildAttachStateChangeListener) {
    if (this.mOnChildAttachStateListeners == null)
      this.mOnChildAttachStateListeners = new ArrayList(); 
    this.mOnChildAttachStateListeners.add(paramOnChildAttachStateChangeListener);
  }
  
  public void addOnItemTouchListener(OnItemTouchListener paramOnItemTouchListener) { this.mOnItemTouchListeners.add(paramOnItemTouchListener); }
  
  public void addOnScrollListener(OnScrollListener paramOnScrollListener) {
    if (this.mScrollListeners == null)
      this.mScrollListeners = new ArrayList(); 
    this.mScrollListeners.add(paramOnScrollListener);
  }
  
  void animateAppearance(@NonNull ViewHolder paramViewHolder, @Nullable ItemAnimator.ItemHolderInfo paramItemHolderInfo1, @NonNull ItemAnimator.ItemHolderInfo paramItemHolderInfo2) {
    paramViewHolder.setIsRecyclable(false);
    if (this.mItemAnimator.animateAppearance(paramViewHolder, paramItemHolderInfo1, paramItemHolderInfo2))
      postAnimationRunner(); 
  }
  
  void animateDisappearance(@NonNull ViewHolder paramViewHolder, @NonNull ItemAnimator.ItemHolderInfo paramItemHolderInfo1, @Nullable ItemAnimator.ItemHolderInfo paramItemHolderInfo2) {
    addAnimatingView(paramViewHolder);
    paramViewHolder.setIsRecyclable(false);
    if (this.mItemAnimator.animateDisappearance(paramViewHolder, paramItemHolderInfo1, paramItemHolderInfo2))
      postAnimationRunner(); 
  }
  
  void assertInLayoutOrScroll(String paramString) {
    if (!isComputingLayout()) {
      StringBuilder stringBuilder1;
      if (paramString == null) {
        stringBuilder1 = new StringBuilder();
        stringBuilder1.append("Cannot call this method unless RecyclerView is computing a layout or scrolling");
        stringBuilder1.append(exceptionLabel());
        throw new IllegalStateException(stringBuilder1.toString());
      } 
      StringBuilder stringBuilder2 = new StringBuilder();
      stringBuilder2.append(stringBuilder1);
      stringBuilder2.append(exceptionLabel());
      throw new IllegalStateException(stringBuilder2.toString());
    } 
  }
  
  void assertNotInLayoutOrScroll(String paramString) {
    if (isComputingLayout()) {
      StringBuilder stringBuilder;
      if (paramString == null) {
        stringBuilder = new StringBuilder();
        stringBuilder.append("Cannot call this method while RecyclerView is computing a layout or scrolling");
        stringBuilder.append(exceptionLabel());
        throw new IllegalStateException(stringBuilder.toString());
      } 
      throw new IllegalStateException(stringBuilder);
    } 
    if (this.mDispatchScrollCounter > 0) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("");
      stringBuilder.append(exceptionLabel());
      Log.w("RecyclerView", "Cannot call this method in a scroll callback. Scroll callbacks mightbe run during a measure & layout pass where you cannot change theRecyclerView data. Any method call that might change the structureof the RecyclerView or the adapter contents should be postponed tothe next frame.", new IllegalStateException(stringBuilder.toString()));
    } 
  }
  
  boolean canReuseUpdatedViewHolder(ViewHolder paramViewHolder) { return (this.mItemAnimator == null || this.mItemAnimator.canReuseUpdatedViewHolder(paramViewHolder, paramViewHolder.getUnmodifiedPayloads())); }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams) { return (paramLayoutParams instanceof LayoutParams && this.mLayout.checkLayoutParams((LayoutParams)paramLayoutParams)); }
  
  void clearOldPositions() {
    int i = this.mChildHelper.getUnfilteredChildCount();
    for (byte b = 0; b < i; b++) {
      ViewHolder viewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(b));
      if (!viewHolder.shouldIgnore())
        viewHolder.clearOldPosition(); 
    } 
    this.mRecycler.clearOldPositions();
  }
  
  public void clearOnChildAttachStateChangeListeners() {
    if (this.mOnChildAttachStateListeners != null)
      this.mOnChildAttachStateListeners.clear(); 
  }
  
  public void clearOnScrollListeners() {
    if (this.mScrollListeners != null)
      this.mScrollListeners.clear(); 
  }
  
  public int computeHorizontalScrollExtent() {
    LayoutManager layoutManager = this.mLayout;
    int i = 0;
    if (layoutManager == null)
      return 0; 
    if (this.mLayout.canScrollHorizontally())
      i = this.mLayout.computeHorizontalScrollExtent(this.mState); 
    return i;
  }
  
  public int computeHorizontalScrollOffset() {
    LayoutManager layoutManager = this.mLayout;
    int i = 0;
    if (layoutManager == null)
      return 0; 
    if (this.mLayout.canScrollHorizontally())
      i = this.mLayout.computeHorizontalScrollOffset(this.mState); 
    return i;
  }
  
  public int computeHorizontalScrollRange() {
    LayoutManager layoutManager = this.mLayout;
    int i = 0;
    if (layoutManager == null)
      return 0; 
    if (this.mLayout.canScrollHorizontally())
      i = this.mLayout.computeHorizontalScrollRange(this.mState); 
    return i;
  }
  
  public int computeVerticalScrollExtent() {
    LayoutManager layoutManager = this.mLayout;
    int i = 0;
    if (layoutManager == null)
      return 0; 
    if (this.mLayout.canScrollVertically())
      i = this.mLayout.computeVerticalScrollExtent(this.mState); 
    return i;
  }
  
  public int computeVerticalScrollOffset() {
    LayoutManager layoutManager = this.mLayout;
    int i = 0;
    if (layoutManager == null)
      return 0; 
    if (this.mLayout.canScrollVertically())
      i = this.mLayout.computeVerticalScrollOffset(this.mState); 
    return i;
  }
  
  public int computeVerticalScrollRange() {
    LayoutManager layoutManager = this.mLayout;
    int i = 0;
    if (layoutManager == null)
      return 0; 
    if (this.mLayout.canScrollVertically())
      i = this.mLayout.computeVerticalScrollRange(this.mState); 
    return i;
  }
  
  void considerReleasingGlowsOnScroll(int paramInt1, int paramInt2) {
    if (this.mLeftGlow != null && !this.mLeftGlow.isFinished() && paramInt1 > 0) {
      this.mLeftGlow.onRelease();
      b2 = this.mLeftGlow.isFinished();
    } else {
      b2 = 0;
    } 
    byte b1 = b2;
    if (this.mRightGlow != null) {
      b1 = b2;
      if (!this.mRightGlow.isFinished()) {
        b1 = b2;
        if (paramInt1 < 0) {
          this.mRightGlow.onRelease();
          b1 = b2 | this.mRightGlow.isFinished();
        } 
      } 
    } 
    byte b2 = b1;
    if (this.mTopGlow != null) {
      b2 = b1;
      if (!this.mTopGlow.isFinished()) {
        b2 = b1;
        if (paramInt2 > 0) {
          this.mTopGlow.onRelease();
          b2 = b1 | this.mTopGlow.isFinished();
        } 
      } 
    } 
    b1 = b2;
    if (this.mBottomGlow != null) {
      b1 = b2;
      if (!this.mBottomGlow.isFinished()) {
        b1 = b2;
        if (paramInt2 < 0) {
          this.mBottomGlow.onRelease();
          b1 = b2 | this.mBottomGlow.isFinished();
        } 
      } 
    } 
    if (b1 != 0)
      ViewCompat.postInvalidateOnAnimation(this); 
  }
  
  void consumePendingUpdateOperations() {
    if (!this.mFirstLayoutComplete || this.mDataSetHasChangedAfterLayout) {
      TraceCompat.beginSection("RV FullInvalidate");
      dispatchLayout();
      TraceCompat.endSection();
      return;
    } 
    if (!this.mAdapterHelper.hasPendingUpdates())
      return; 
    if (this.mAdapterHelper.hasAnyUpdateTypes(4) && !this.mAdapterHelper.hasAnyUpdateTypes(11)) {
      TraceCompat.beginSection("RV PartialInvalidate");
      startInterceptRequestLayout();
      onEnterLayoutOrScroll();
      this.mAdapterHelper.preProcess();
      if (!this.mLayoutWasDefered)
        if (hasUpdatedView()) {
          dispatchLayout();
        } else {
          this.mAdapterHelper.consumePostponedUpdates();
        }  
      stopInterceptRequestLayout(true);
      onExitLayoutOrScroll();
      TraceCompat.endSection();
      return;
    } 
    if (this.mAdapterHelper.hasPendingUpdates()) {
      TraceCompat.beginSection("RV FullInvalidate");
      dispatchLayout();
      TraceCompat.endSection();
    } 
  }
  
  void defaultOnMeasure(int paramInt1, int paramInt2) { setMeasuredDimension(LayoutManager.chooseSize(paramInt1, getPaddingLeft() + getPaddingRight(), ViewCompat.getMinimumWidth(this)), LayoutManager.chooseSize(paramInt2, getPaddingTop() + getPaddingBottom(), ViewCompat.getMinimumHeight(this))); }
  
  void dispatchChildAttached(View paramView) {
    ViewHolder viewHolder = getChildViewHolderInt(paramView);
    onChildAttachedToWindow(paramView);
    if (this.mAdapter != null && viewHolder != null)
      this.mAdapter.onViewAttachedToWindow(viewHolder); 
    if (this.mOnChildAttachStateListeners != null)
      for (int i = this.mOnChildAttachStateListeners.size() - 1; i >= 0; i--)
        ((OnChildAttachStateChangeListener)this.mOnChildAttachStateListeners.get(i)).onChildViewAttachedToWindow(paramView);  
  }
  
  void dispatchChildDetached(View paramView) {
    ViewHolder viewHolder = getChildViewHolderInt(paramView);
    onChildDetachedFromWindow(paramView);
    if (this.mAdapter != null && viewHolder != null)
      this.mAdapter.onViewDetachedFromWindow(viewHolder); 
    if (this.mOnChildAttachStateListeners != null)
      for (int i = this.mOnChildAttachStateListeners.size() - 1; i >= 0; i--)
        ((OnChildAttachStateChangeListener)this.mOnChildAttachStateListeners.get(i)).onChildViewDetachedFromWindow(paramView);  
  }
  
  void dispatchLayout() {
    if (this.mAdapter == null) {
      Log.e("RecyclerView", "No adapter attached; skipping layout");
      return;
    } 
    if (this.mLayout == null) {
      Log.e("RecyclerView", "No layout manager attached; skipping layout");
      return;
    } 
    this.mState.mIsMeasuring = false;
    if (this.mState.mLayoutStep == 1) {
      dispatchLayoutStep1();
      this.mLayout.setExactMeasureSpecsFrom(this);
      dispatchLayoutStep2();
    } else if (this.mAdapterHelper.hasUpdates() || this.mLayout.getWidth() != getWidth() || this.mLayout.getHeight() != getHeight()) {
      this.mLayout.setExactMeasureSpecsFrom(this);
      dispatchLayoutStep2();
    } else {
      this.mLayout.setExactMeasureSpecsFrom(this);
    } 
    dispatchLayoutStep3();
  }
  
  public boolean dispatchNestedFling(float paramFloat1, float paramFloat2, boolean paramBoolean) { return getScrollingChildHelper().dispatchNestedFling(paramFloat1, paramFloat2, paramBoolean); }
  
  public boolean dispatchNestedPreFling(float paramFloat1, float paramFloat2) { return getScrollingChildHelper().dispatchNestedPreFling(paramFloat1, paramFloat2); }
  
  public boolean dispatchNestedPreScroll(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2) { return getScrollingChildHelper().dispatchNestedPreScroll(paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2); }
  
  public boolean dispatchNestedPreScroll(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt3) { return getScrollingChildHelper().dispatchNestedPreScroll(paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2, paramInt3); }
  
  public boolean dispatchNestedScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt) { return getScrollingChildHelper().dispatchNestedScroll(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt); }
  
  public boolean dispatchNestedScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt, int paramInt5) { return getScrollingChildHelper().dispatchNestedScroll(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt, paramInt5); }
  
  void dispatchOnScrollStateChanged(int paramInt) {
    if (this.mLayout != null)
      this.mLayout.onScrollStateChanged(paramInt); 
    onScrollStateChanged(paramInt);
    if (this.mScrollListener != null)
      this.mScrollListener.onScrollStateChanged(this, paramInt); 
    if (this.mScrollListeners != null)
      for (int i = this.mScrollListeners.size() - 1; i >= 0; i--)
        ((OnScrollListener)this.mScrollListeners.get(i)).onScrollStateChanged(this, paramInt);  
  }
  
  void dispatchOnScrolled(int paramInt1, int paramInt2) {
    this.mDispatchScrollCounter++;
    int i = getScrollX();
    int j = getScrollY();
    onScrollChanged(i, j, i, j);
    onScrolled(paramInt1, paramInt2);
    if (this.mScrollListener != null)
      this.mScrollListener.onScrolled(this, paramInt1, paramInt2); 
    if (this.mScrollListeners != null)
      for (i = this.mScrollListeners.size() - 1; i >= 0; i--)
        ((OnScrollListener)this.mScrollListeners.get(i)).onScrolled(this, paramInt1, paramInt2);  
    this.mDispatchScrollCounter--;
  }
  
  void dispatchPendingImportantForAccessibilityChanges() {
    for (int i = this.mPendingAccessibilityImportanceChange.size() - 1; i >= 0; i--) {
      ViewHolder viewHolder = (ViewHolder)this.mPendingAccessibilityImportanceChange.get(i);
      if (viewHolder.itemView.getParent() == this && !viewHolder.shouldIgnore()) {
        int j = viewHolder.mPendingAccessibilityState;
        if (j != -1) {
          ViewCompat.setImportantForAccessibility(viewHolder.itemView, j);
          viewHolder.mPendingAccessibilityState = -1;
        } 
      } 
    } 
    this.mPendingAccessibilityImportanceChange.clear();
  }
  
  protected void dispatchRestoreInstanceState(SparseArray<Parcelable> paramSparseArray) { dispatchThawSelfOnly(paramSparseArray); }
  
  protected void dispatchSaveInstanceState(SparseArray<Parcelable> paramSparseArray) { dispatchFreezeSelfOnly(paramSparseArray); }
  
  public void draw(Canvas paramCanvas) {
    super.draw(paramCanvas);
    int j = this.mItemDecorations.size();
    int k = 0;
    int i;
    for (i = 0; i < j; i++)
      ((ItemDecoration)this.mItemDecorations.get(i)).onDrawOver(paramCanvas, this, this.mState); 
    if (this.mLeftGlow != null && !this.mLeftGlow.isFinished()) {
      int m = paramCanvas.save();
      if (this.mClipToPadding) {
        i = getPaddingBottom();
      } else {
        i = 0;
      } 
      paramCanvas.rotate(270.0F);
      paramCanvas.translate((-getHeight() + i), 0.0F);
      if (this.mLeftGlow != null && this.mLeftGlow.draw(paramCanvas)) {
        j = 1;
      } else {
        j = 0;
      } 
      paramCanvas.restoreToCount(m);
    } else {
      j = 0;
    } 
    i = j;
    if (this.mTopGlow != null) {
      i = j;
      if (!this.mTopGlow.isFinished()) {
        int m = paramCanvas.save();
        if (this.mClipToPadding)
          paramCanvas.translate(getPaddingLeft(), getPaddingTop()); 
        if (this.mTopGlow != null && this.mTopGlow.draw(paramCanvas)) {
          i = 1;
        } else {
          i = 0;
        } 
        i = j | i;
        paramCanvas.restoreToCount(m);
      } 
    } 
    j = i;
    if (this.mRightGlow != null) {
      j = i;
      if (!this.mRightGlow.isFinished()) {
        int m = paramCanvas.save();
        int n = getWidth();
        if (this.mClipToPadding) {
          j = getPaddingTop();
        } else {
          j = 0;
        } 
        paramCanvas.rotate(90.0F);
        paramCanvas.translate(-j, -n);
        if (this.mRightGlow != null && this.mRightGlow.draw(paramCanvas)) {
          j = 1;
        } else {
          j = 0;
        } 
        j = i | j;
        paramCanvas.restoreToCount(m);
      } 
    } 
    if (this.mBottomGlow != null && !this.mBottomGlow.isFinished()) {
      int m = paramCanvas.save();
      paramCanvas.rotate(180.0F);
      if (this.mClipToPadding) {
        paramCanvas.translate((-getWidth() + getPaddingRight()), (-getHeight() + getPaddingBottom()));
      } else {
        paramCanvas.translate(-getWidth(), -getHeight());
      } 
      i = k;
      if (this.mBottomGlow != null) {
        i = k;
        if (this.mBottomGlow.draw(paramCanvas))
          i = 1; 
      } 
      i |= j;
      paramCanvas.restoreToCount(m);
    } else {
      i = j;
    } 
    j = i;
    if (i == 0) {
      j = i;
      if (this.mItemAnimator != null) {
        j = i;
        if (this.mItemDecorations.size() > 0) {
          j = i;
          if (this.mItemAnimator.isRunning())
            j = 1; 
        } 
      } 
    } 
    if (j != 0)
      ViewCompat.postInvalidateOnAnimation(this); 
  }
  
  public boolean drawChild(Canvas paramCanvas, View paramView, long paramLong) { return super.drawChild(paramCanvas, paramView, paramLong); }
  
  void ensureBottomGlow() {
    if (this.mBottomGlow != null)
      return; 
    this.mBottomGlow = this.mEdgeEffectFactory.createEdgeEffect(this, 3);
    if (this.mClipToPadding) {
      this.mBottomGlow.setSize(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), getMeasuredHeight() - getPaddingTop() - getPaddingBottom());
      return;
    } 
    this.mBottomGlow.setSize(getMeasuredWidth(), getMeasuredHeight());
  }
  
  void ensureLeftGlow() {
    if (this.mLeftGlow != null)
      return; 
    this.mLeftGlow = this.mEdgeEffectFactory.createEdgeEffect(this, 0);
    if (this.mClipToPadding) {
      this.mLeftGlow.setSize(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), getMeasuredWidth() - getPaddingLeft() - getPaddingRight());
      return;
    } 
    this.mLeftGlow.setSize(getMeasuredHeight(), getMeasuredWidth());
  }
  
  void ensureRightGlow() {
    if (this.mRightGlow != null)
      return; 
    this.mRightGlow = this.mEdgeEffectFactory.createEdgeEffect(this, 2);
    if (this.mClipToPadding) {
      this.mRightGlow.setSize(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), getMeasuredWidth() - getPaddingLeft() - getPaddingRight());
      return;
    } 
    this.mRightGlow.setSize(getMeasuredHeight(), getMeasuredWidth());
  }
  
  void ensureTopGlow() {
    if (this.mTopGlow != null)
      return; 
    this.mTopGlow = this.mEdgeEffectFactory.createEdgeEffect(this, 1);
    if (this.mClipToPadding) {
      this.mTopGlow.setSize(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), getMeasuredHeight() - getPaddingTop() - getPaddingBottom());
      return;
    } 
    this.mTopGlow.setSize(getMeasuredWidth(), getMeasuredHeight());
  }
  
  String exceptionLabel() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(" ");
    stringBuilder.append(toString());
    stringBuilder.append(", adapter:");
    stringBuilder.append(this.mAdapter);
    stringBuilder.append(", layout:");
    stringBuilder.append(this.mLayout);
    stringBuilder.append(", context:");
    stringBuilder.append(getContext());
    return stringBuilder.toString();
  }
  
  final void fillRemainingScrollValues(State paramState) {
    if (getScrollState() == 2) {
      OverScroller overScroller = this.mViewFlinger.mScroller;
      paramState.mRemainingScrollHorizontal = overScroller.getFinalX() - overScroller.getCurrX();
      paramState.mRemainingScrollVertical = overScroller.getFinalY() - overScroller.getCurrY();
      return;
    } 
    paramState.mRemainingScrollHorizontal = 0;
    paramState.mRemainingScrollVertical = 0;
  }
  
  public View findChildViewUnder(float paramFloat1, float paramFloat2) {
    int i;
    for (i = this.mChildHelper.getChildCount() - 1; i >= 0; i--) {
      View view = this.mChildHelper.getChildAt(i);
      float f1 = view.getTranslationX();
      float f2 = view.getTranslationY();
      if (paramFloat1 >= view.getLeft() + f1 && paramFloat1 <= view.getRight() + f1 && paramFloat2 >= view.getTop() + f2 && paramFloat2 <= view.getBottom() + f2)
        return view; 
    } 
    return null;
  }
  
  @Nullable
  public View findContainingItemView(View paramView) {
    ViewParent viewParent2 = paramView.getParent();
    View view = paramView;
    ViewParent viewParent1;
    for (viewParent1 = viewParent2; viewParent1 != null && viewParent1 != this && viewParent1 instanceof View; viewParent1 = view.getParent())
      view = (View)viewParent1; 
    return (viewParent1 == this) ? view : null;
  }
  
  @Nullable
  public ViewHolder findContainingViewHolder(View paramView) {
    paramView = findContainingItemView(paramView);
    return (paramView == null) ? null : getChildViewHolder(paramView);
  }
  
  public ViewHolder findViewHolderForAdapterPosition(int paramInt) {
    boolean bool = this.mDataSetHasChangedAfterLayout;
    ViewHolder viewHolder = null;
    if (bool)
      return null; 
    int i = this.mChildHelper.getUnfilteredChildCount();
    byte b = 0;
    while (b < i) {
      ViewHolder viewHolder2 = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(b));
      ViewHolder viewHolder1 = viewHolder;
      if (viewHolder2 != null) {
        viewHolder1 = viewHolder;
        if (!viewHolder2.isRemoved()) {
          viewHolder1 = viewHolder;
          if (getAdapterPositionFor(viewHolder2) == paramInt)
            if (this.mChildHelper.isHidden(viewHolder2.itemView)) {
              viewHolder1 = viewHolder2;
            } else {
              return viewHolder2;
            }  
        } 
      } 
      b++;
      viewHolder = viewHolder1;
    } 
    return viewHolder;
  }
  
  public ViewHolder findViewHolderForItemId(long paramLong) {
    Adapter adapter2 = this.mAdapter;
    Adapter adapter1 = null;
    if (adapter2 != null) {
      ViewHolder viewHolder;
      if (!this.mAdapter.hasStableIds())
        return null; 
      int i = this.mChildHelper.getUnfilteredChildCount();
      byte b = 0;
      while (b < i) {
        ViewHolder viewHolder1;
        ViewHolder viewHolder2 = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(b));
        adapter2 = adapter1;
        if (viewHolder2 != null) {
          adapter2 = adapter1;
          if (!viewHolder2.isRemoved()) {
            adapter2 = adapter1;
            if (viewHolder2.getItemId() == paramLong)
              if (this.mChildHelper.isHidden(viewHolder2.itemView)) {
                viewHolder1 = viewHolder2;
              } else {
                return viewHolder2;
              }  
          } 
        } 
        b++;
        viewHolder = viewHolder1;
      } 
      return viewHolder;
    } 
    return null;
  }
  
  public ViewHolder findViewHolderForLayoutPosition(int paramInt) { return findViewHolderForPosition(paramInt, false); }
  
  @Deprecated
  public ViewHolder findViewHolderForPosition(int paramInt) { return findViewHolderForPosition(paramInt, false); }
  
  ViewHolder findViewHolderForPosition(int paramInt, boolean paramBoolean) {
    int i = this.mChildHelper.getUnfilteredChildCount();
    Object object = null;
    byte b = 0;
    while (b < i) {
      ViewHolder viewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(b));
      Object object1 = object;
      if (viewHolder != null) {
        object1 = object;
        if (!viewHolder.isRemoved()) {
          if (paramBoolean) {
            if (viewHolder.mPosition != paramInt) {
              object1 = object;
              continue;
            } 
          } else if (viewHolder.getLayoutPosition() != paramInt) {
            object1 = object;
            continue;
          } 
          if (this.mChildHelper.isHidden(viewHolder.itemView)) {
            object1 = viewHolder;
          } else {
            return viewHolder;
          } 
        } 
      } 
      continue;
      b++;
      object = SYNTHETIC_LOCAL_VARIABLE_6;
    } 
    return object;
  }
  
  public boolean fling(int paramInt1, int paramInt2) { // Byte code:
    //   0: aload_0
    //   1: getfield mLayout : Landroid/support/v7/widget/RecyclerView$LayoutManager;
    //   4: astore #11
    //   6: iconst_0
    //   7: istore #7
    //   9: aload #11
    //   11: ifnonnull -> 25
    //   14: ldc 'RecyclerView'
    //   16: ldc_w 'Cannot fling without a LayoutManager set. Call setLayoutManager with a non-null argument.'
    //   19: invokestatic e : (Ljava/lang/String;Ljava/lang/String;)I
    //   22: pop
    //   23: iconst_0
    //   24: ireturn
    //   25: aload_0
    //   26: getfield mLayoutFrozen : Z
    //   29: ifeq -> 34
    //   32: iconst_0
    //   33: ireturn
    //   34: aload_0
    //   35: getfield mLayout : Landroid/support/v7/widget/RecyclerView$LayoutManager;
    //   38: invokevirtual canScrollHorizontally : ()Z
    //   41: istore #9
    //   43: aload_0
    //   44: getfield mLayout : Landroid/support/v7/widget/RecyclerView$LayoutManager;
    //   47: invokevirtual canScrollVertically : ()Z
    //   50: istore #10
    //   52: iload #9
    //   54: ifeq -> 71
    //   57: iload_1
    //   58: istore #5
    //   60: iload_1
    //   61: invokestatic abs : (I)I
    //   64: aload_0
    //   65: getfield mMinFlingVelocity : I
    //   68: if_icmpge -> 74
    //   71: iconst_0
    //   72: istore #5
    //   74: iload #10
    //   76: ifeq -> 93
    //   79: iload_2
    //   80: istore #6
    //   82: iload_2
    //   83: invokestatic abs : (I)I
    //   86: aload_0
    //   87: getfield mMinFlingVelocity : I
    //   90: if_icmpge -> 96
    //   93: iconst_0
    //   94: istore #6
    //   96: iload #5
    //   98: ifne -> 108
    //   101: iload #6
    //   103: ifne -> 108
    //   106: iconst_0
    //   107: ireturn
    //   108: iload #5
    //   110: i2f
    //   111: fstore_3
    //   112: iload #6
    //   114: i2f
    //   115: fstore #4
    //   117: aload_0
    //   118: fload_3
    //   119: fload #4
    //   121: invokevirtual dispatchNestedPreFling : (FF)Z
    //   124: ifne -> 262
    //   127: iload #9
    //   129: ifne -> 146
    //   132: iload #10
    //   134: ifeq -> 140
    //   137: goto -> 146
    //   140: iconst_0
    //   141: istore #8
    //   143: goto -> 149
    //   146: iconst_1
    //   147: istore #8
    //   149: aload_0
    //   150: fload_3
    //   151: fload #4
    //   153: iload #8
    //   155: invokevirtual dispatchNestedFling : (FFZ)Z
    //   158: pop
    //   159: aload_0
    //   160: getfield mOnFlingListener : Landroid/support/v7/widget/RecyclerView$OnFlingListener;
    //   163: ifnull -> 182
    //   166: aload_0
    //   167: getfield mOnFlingListener : Landroid/support/v7/widget/RecyclerView$OnFlingListener;
    //   170: iload #5
    //   172: iload #6
    //   174: invokevirtual onFling : (II)Z
    //   177: ifeq -> 182
    //   180: iconst_1
    //   181: ireturn
    //   182: iload #8
    //   184: ifeq -> 262
    //   187: iload #7
    //   189: istore_1
    //   190: iload #9
    //   192: ifeq -> 197
    //   195: iconst_1
    //   196: istore_1
    //   197: iload_1
    //   198: istore_2
    //   199: iload #10
    //   201: ifeq -> 208
    //   204: iload_1
    //   205: iconst_2
    //   206: ior
    //   207: istore_2
    //   208: aload_0
    //   209: iload_2
    //   210: iconst_1
    //   211: invokevirtual startNestedScroll : (II)Z
    //   214: pop
    //   215: aload_0
    //   216: getfield mMaxFlingVelocity : I
    //   219: ineg
    //   220: iload #5
    //   222: aload_0
    //   223: getfield mMaxFlingVelocity : I
    //   226: invokestatic min : (II)I
    //   229: invokestatic max : (II)I
    //   232: istore_1
    //   233: aload_0
    //   234: getfield mMaxFlingVelocity : I
    //   237: ineg
    //   238: iload #6
    //   240: aload_0
    //   241: getfield mMaxFlingVelocity : I
    //   244: invokestatic min : (II)I
    //   247: invokestatic max : (II)I
    //   250: istore_2
    //   251: aload_0
    //   252: getfield mViewFlinger : Landroid/support/v7/widget/RecyclerView$ViewFlinger;
    //   255: iload_1
    //   256: iload_2
    //   257: invokevirtual fling : (II)V
    //   260: iconst_1
    //   261: ireturn
    //   262: iconst_0
    //   263: ireturn }
  
  public View focusSearch(View paramView, int paramInt) {
    int i;
    View view2 = this.mLayout.onInterceptFocusSearch(paramView, paramInt);
    if (view2 != null)
      return view2; 
    if (this.mAdapter != null && this.mLayout != null && !isComputingLayout() && !this.mLayoutFrozen) {
      i = 1;
    } else {
      i = 0;
    } 
    View view1 = FocusFinder.getInstance();
    if (i && (paramInt == 2 || paramInt == 1)) {
      if (this.mLayout.canScrollVertically()) {
        byte b1;
        int k;
        if (paramInt == 2) {
          k = 130;
        } else {
          k = 33;
        } 
        if (view1.findNextFocus(this, paramView, k) == null) {
          b1 = 1;
        } else {
          b1 = 0;
        } 
        i = b1;
        if (FORCE_ABS_FOCUS_SEARCH_DIRECTION) {
          paramInt = k;
          i = b1;
        } 
      } else {
        i = 0;
      } 
      byte b = i;
      int j = paramInt;
      if (i == 0) {
        b = i;
        j = paramInt;
        if (this.mLayout.canScrollHorizontally()) {
          byte b1;
          if (this.mLayout.getLayoutDirection() == 1) {
            i = 1;
          } else {
            i = 0;
          } 
          if (paramInt == 2) {
            b1 = 1;
          } else {
            b1 = 0;
          } 
          if ((i ^ b1) != 0) {
            i = 66;
          } else {
            i = 17;
          } 
          if (view1.findNextFocus(this, paramView, i) == null) {
            b1 = 1;
          } else {
            b1 = 0;
          } 
          b = b1;
          j = paramInt;
          if (FORCE_ABS_FOCUS_SEARCH_DIRECTION) {
            j = i;
            b = b1;
          } 
        } 
      } 
      if (b != 0) {
        consumePendingUpdateOperations();
        if (findContainingItemView(paramView) == null)
          return null; 
        startInterceptRequestLayout();
        this.mLayout.onFocusSearchFailed(paramView, j, this.mRecycler, this.mState);
        stopInterceptRequestLayout(false);
      } 
      view1 = view1.findNextFocus(this, paramView, j);
      paramInt = j;
    } else {
      view1 = view1.findNextFocus(this, paramView, paramInt);
      if (view1 == null && i != 0) {
        consumePendingUpdateOperations();
        if (findContainingItemView(paramView) == null)
          return null; 
        startInterceptRequestLayout();
        view1 = this.mLayout.onFocusSearchFailed(paramView, paramInt, this.mRecycler, this.mState);
        stopInterceptRequestLayout(false);
      } 
    } 
    if (view1 != null && !view1.hasFocusable()) {
      if (getFocusedChild() == null)
        return super.focusSearch(paramView, paramInt); 
      requestChildOnScreen(view1, null);
      return paramView;
    } 
    return isPreferredNextFocus(paramView, view1, paramInt) ? view1 : super.focusSearch(paramView, paramInt);
  }
  
  protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
    if (this.mLayout == null) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("RecyclerView has no LayoutManager");
      stringBuilder.append(exceptionLabel());
      throw new IllegalStateException(stringBuilder.toString());
    } 
    return this.mLayout.generateDefaultLayoutParams();
  }
  
  public ViewGroup.LayoutParams generateLayoutParams(AttributeSet paramAttributeSet) {
    StringBuilder stringBuilder;
    if (this.mLayout == null) {
      stringBuilder = new StringBuilder();
      stringBuilder.append("RecyclerView has no LayoutManager");
      stringBuilder.append(exceptionLabel());
      throw new IllegalStateException(stringBuilder.toString());
    } 
    return this.mLayout.generateLayoutParams(getContext(), stringBuilder);
  }
  
  protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams) {
    StringBuilder stringBuilder;
    if (this.mLayout == null) {
      stringBuilder = new StringBuilder();
      stringBuilder.append("RecyclerView has no LayoutManager");
      stringBuilder.append(exceptionLabel());
      throw new IllegalStateException(stringBuilder.toString());
    } 
    return this.mLayout.generateLayoutParams(stringBuilder);
  }
  
  public Adapter getAdapter() { return this.mAdapter; }
  
  int getAdapterPositionFor(ViewHolder paramViewHolder) { return (paramViewHolder.hasAnyOfTheFlags(524) || !paramViewHolder.isBound()) ? -1 : this.mAdapterHelper.applyPendingUpdatesToPosition(paramViewHolder.mPosition); }
  
  public int getBaseline() { return (this.mLayout != null) ? this.mLayout.getBaseline() : super.getBaseline(); }
  
  long getChangedHolderKey(ViewHolder paramViewHolder) { return this.mAdapter.hasStableIds() ? paramViewHolder.getItemId() : paramViewHolder.mPosition; }
  
  public int getChildAdapterPosition(View paramView) {
    ViewHolder viewHolder = getChildViewHolderInt(paramView);
    return (viewHolder != null) ? viewHolder.getAdapterPosition() : -1;
  }
  
  protected int getChildDrawingOrder(int paramInt1, int paramInt2) { return (this.mChildDrawingOrderCallback == null) ? super.getChildDrawingOrder(paramInt1, paramInt2) : this.mChildDrawingOrderCallback.onGetChildDrawingOrder(paramInt1, paramInt2); }
  
  public long getChildItemId(View paramView) {
    Adapter adapter = this.mAdapter;
    long l = -1L;
    if (adapter != null) {
      if (!this.mAdapter.hasStableIds())
        return -1L; 
      ViewHolder viewHolder = getChildViewHolderInt(paramView);
      if (viewHolder != null)
        l = viewHolder.getItemId(); 
      return l;
    } 
    return -1L;
  }
  
  public int getChildLayoutPosition(View paramView) {
    ViewHolder viewHolder = getChildViewHolderInt(paramView);
    return (viewHolder != null) ? viewHolder.getLayoutPosition() : -1;
  }
  
  @Deprecated
  public int getChildPosition(View paramView) { return getChildAdapterPosition(paramView); }
  
  public ViewHolder getChildViewHolder(View paramView) {
    ViewParent viewParent = paramView.getParent();
    if (viewParent != null && viewParent != this) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("View ");
      stringBuilder.append(paramView);
      stringBuilder.append(" is not a direct child of ");
      stringBuilder.append(this);
      throw new IllegalArgumentException(stringBuilder.toString());
    } 
    return getChildViewHolderInt(paramView);
  }
  
  public boolean getClipToPadding() { return this.mClipToPadding; }
  
  public RecyclerViewAccessibilityDelegate getCompatAccessibilityDelegate() { return this.mAccessibilityDelegate; }
  
  public void getDecoratedBoundsWithMargins(View paramView, Rect paramRect) { getDecoratedBoundsWithMarginsInt(paramView, paramRect); }
  
  public EdgeEffectFactory getEdgeEffectFactory() { return this.mEdgeEffectFactory; }
  
  public ItemAnimator getItemAnimator() { return this.mItemAnimator; }
  
  Rect getItemDecorInsetsForChild(View paramView) {
    LayoutParams layoutParams = (LayoutParams)paramView.getLayoutParams();
    if (!layoutParams.mInsetsDirty)
      return layoutParams.mDecorInsets; 
    if (this.mState.isPreLayout() && (layoutParams.isItemChanged() || layoutParams.isViewInvalid()))
      return layoutParams.mDecorInsets; 
    Rect rect = layoutParams.mDecorInsets;
    rect.set(0, 0, 0, 0);
    int i = this.mItemDecorations.size();
    for (byte b = 0; b < i; b++) {
      this.mTempRect.set(0, 0, 0, 0);
      ((ItemDecoration)this.mItemDecorations.get(b)).getItemOffsets(this.mTempRect, paramView, this, this.mState);
      rect.left += this.mTempRect.left;
      rect.top += this.mTempRect.top;
      rect.right += this.mTempRect.right;
      rect.bottom += this.mTempRect.bottom;
    } 
    layoutParams.mInsetsDirty = false;
    return rect;
  }
  
  public ItemDecoration getItemDecorationAt(int paramInt) {
    int i = getItemDecorationCount();
    if (paramInt < 0 || paramInt >= i) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(paramInt);
      stringBuilder.append(" is an invalid index for size ");
      stringBuilder.append(i);
      throw new IndexOutOfBoundsException(stringBuilder.toString());
    } 
    return (ItemDecoration)this.mItemDecorations.get(paramInt);
  }
  
  public int getItemDecorationCount() { return this.mItemDecorations.size(); }
  
  public LayoutManager getLayoutManager() { return this.mLayout; }
  
  public int getMaxFlingVelocity() { return this.mMaxFlingVelocity; }
  
  public int getMinFlingVelocity() { return this.mMinFlingVelocity; }
  
  long getNanoTime() { return ALLOW_THREAD_GAP_WORK ? System.nanoTime() : 0L; }
  
  @Nullable
  public OnFlingListener getOnFlingListener() { return this.mOnFlingListener; }
  
  public boolean getPreserveFocusAfterLayout() { return this.mPreserveFocusAfterLayout; }
  
  public RecycledViewPool getRecycledViewPool() { return this.mRecycler.getRecycledViewPool(); }
  
  public int getScrollState() { return this.mScrollState; }
  
  public boolean hasFixedSize() { return this.mHasFixedSize; }
  
  public boolean hasNestedScrollingParent() { return getScrollingChildHelper().hasNestedScrollingParent(); }
  
  public boolean hasNestedScrollingParent(int paramInt) { return getScrollingChildHelper().hasNestedScrollingParent(paramInt); }
  
  public boolean hasPendingAdapterUpdates() { return (!this.mFirstLayoutComplete || this.mDataSetHasChangedAfterLayout || this.mAdapterHelper.hasPendingUpdates()); }
  
  void initAdapterManager() { this.mAdapterHelper = new AdapterHelper(new AdapterHelper.Callback(this) {
          void dispatchUpdate(AdapterHelper.UpdateOp param1UpdateOp) {
            int i = param1UpdateOp.cmd;
            if (i != 4) {
              if (i != 8) {
                switch (i) {
                  default:
                    return;
                  case 2:
                    RecyclerView.this.mLayout.onItemsRemoved(RecyclerView.this, param1UpdateOp.positionStart, param1UpdateOp.itemCount);
                    return;
                  case 1:
                    break;
                } 
                RecyclerView.this.mLayout.onItemsAdded(RecyclerView.this, param1UpdateOp.positionStart, param1UpdateOp.itemCount);
                return;
              } 
              RecyclerView.this.mLayout.onItemsMoved(RecyclerView.this, param1UpdateOp.positionStart, param1UpdateOp.itemCount, 1);
              return;
            } 
            RecyclerView.this.mLayout.onItemsUpdated(RecyclerView.this, param1UpdateOp.positionStart, param1UpdateOp.itemCount, param1UpdateOp.payload);
          }
          
          public RecyclerView.ViewHolder findViewHolder(int param1Int) {
            RecyclerView.ViewHolder viewHolder = RecyclerView.this.findViewHolderForPosition(param1Int, true);
            return (viewHolder == null) ? null : (RecyclerView.this.mChildHelper.isHidden(viewHolder.itemView) ? null : viewHolder);
          }
          
          public void markViewHoldersUpdated(int param1Int1, int param1Int2, Object param1Object) {
            RecyclerView.this.viewRangeUpdate(param1Int1, param1Int2, param1Object);
            RecyclerView.this.mItemsChanged = true;
          }
          
          public void offsetPositionsForAdd(int param1Int1, int param1Int2) {
            RecyclerView.this.offsetPositionRecordsForInsert(param1Int1, param1Int2);
            RecyclerView.this.mItemsAddedOrRemoved = true;
          }
          
          public void offsetPositionsForMove(int param1Int1, int param1Int2) {
            RecyclerView.this.offsetPositionRecordsForMove(param1Int1, param1Int2);
            RecyclerView.this.mItemsAddedOrRemoved = true;
          }
          
          public void offsetPositionsForRemovingInvisible(int param1Int1, int param1Int2) {
            RecyclerView.this.offsetPositionRecordsForRemove(param1Int1, param1Int2, true);
            RecyclerView.this.mItemsAddedOrRemoved = true;
            RecyclerView.State state = RecyclerView.this.mState;
            state.mDeletedInvisibleItemCountSincePreviousLayout += param1Int2;
          }
          
          public void offsetPositionsForRemovingLaidOutOrNewView(int param1Int1, int param1Int2) {
            RecyclerView.this.offsetPositionRecordsForRemove(param1Int1, param1Int2, false);
            RecyclerView.this.mItemsAddedOrRemoved = true;
          }
          
          public void onDispatchFirstPass(AdapterHelper.UpdateOp param1UpdateOp) { dispatchUpdate(param1UpdateOp); }
          
          public void onDispatchSecondPass(AdapterHelper.UpdateOp param1UpdateOp) { dispatchUpdate(param1UpdateOp); }
        }); }
  
  @VisibleForTesting
  void initFastScroller(StateListDrawable paramStateListDrawable1, Drawable paramDrawable1, StateListDrawable paramStateListDrawable2, Drawable paramDrawable2) {
    StringBuilder stringBuilder;
    if (paramStateListDrawable1 == null || paramDrawable1 == null || paramStateListDrawable2 == null || paramDrawable2 == null) {
      stringBuilder = new StringBuilder();
      stringBuilder.append("Trying to set fast scroller without both required drawables.");
      stringBuilder.append(exceptionLabel());
      throw new IllegalArgumentException(stringBuilder.toString());
    } 
    Resources resources = getContext().getResources();
    new FastScroller(this, stringBuilder, paramDrawable1, paramStateListDrawable2, paramDrawable2, resources.getDimensionPixelSize(R.dimen.fastscroll_default_thickness), resources.getDimensionPixelSize(R.dimen.fastscroll_minimum_range), resources.getDimensionPixelOffset(R.dimen.fastscroll_margin));
  }
  
  void invalidateGlows() {
    this.mBottomGlow = null;
    this.mTopGlow = null;
    this.mRightGlow = null;
    this.mLeftGlow = null;
  }
  
  public void invalidateItemDecorations() {
    if (this.mItemDecorations.size() == 0)
      return; 
    if (this.mLayout != null)
      this.mLayout.assertNotInLayoutOrScroll("Cannot invalidate item decorations during a scroll or layout"); 
    markItemDecorInsetsDirty();
    requestLayout();
  }
  
  boolean isAccessibilityEnabled() { return (this.mAccessibilityManager != null && this.mAccessibilityManager.isEnabled()); }
  
  public boolean isAnimating() { return (this.mItemAnimator != null && this.mItemAnimator.isRunning()); }
  
  public boolean isAttachedToWindow() { return this.mIsAttached; }
  
  public boolean isComputingLayout() { return (this.mLayoutOrScrollCounter > 0); }
  
  public boolean isLayoutFrozen() { return this.mLayoutFrozen; }
  
  public boolean isNestedScrollingEnabled() { return getScrollingChildHelper().isNestedScrollingEnabled(); }
  
  void jumpToPositionForSmoothScroller(int paramInt) {
    if (this.mLayout == null)
      return; 
    this.mLayout.scrollToPosition(paramInt);
    awakenScrollBars();
  }
  
  void markItemDecorInsetsDirty() {
    int i = this.mChildHelper.getUnfilteredChildCount();
    for (byte b = 0; b < i; b++)
      ((LayoutParams)this.mChildHelper.getUnfilteredChildAt(b).getLayoutParams()).mInsetsDirty = true; 
    this.mRecycler.markItemDecorInsetsDirty();
  }
  
  void markKnownViewsInvalid() {
    int i = this.mChildHelper.getUnfilteredChildCount();
    for (byte b = 0; b < i; b++) {
      ViewHolder viewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(b));
      if (viewHolder != null && !viewHolder.shouldIgnore())
        viewHolder.addFlags(6); 
    } 
    markItemDecorInsetsDirty();
    this.mRecycler.markKnownViewsInvalid();
  }
  
  public void offsetChildrenHorizontal(int paramInt) {
    int i = this.mChildHelper.getChildCount();
    for (byte b = 0; b < i; b++)
      this.mChildHelper.getChildAt(b).offsetLeftAndRight(paramInt); 
  }
  
  public void offsetChildrenVertical(int paramInt) {
    int i = this.mChildHelper.getChildCount();
    for (byte b = 0; b < i; b++)
      this.mChildHelper.getChildAt(b).offsetTopAndBottom(paramInt); 
  }
  
  void offsetPositionRecordsForInsert(int paramInt1, int paramInt2) {
    int i = this.mChildHelper.getUnfilteredChildCount();
    for (byte b = 0; b < i; b++) {
      ViewHolder viewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(b));
      if (viewHolder != null && !viewHolder.shouldIgnore() && viewHolder.mPosition >= paramInt1) {
        viewHolder.offsetPosition(paramInt2, false);
        this.mState.mStructureChanged = true;
      } 
    } 
    this.mRecycler.offsetPositionRecordsForInsert(paramInt1, paramInt2);
    requestLayout();
  }
  
  void offsetPositionRecordsForMove(int paramInt1, int paramInt2) {
    byte b1;
    int j;
    int i;
    int k = this.mChildHelper.getUnfilteredChildCount();
    if (paramInt1 < paramInt2) {
      i = paramInt1;
      j = paramInt2;
      b1 = -1;
    } else {
      j = paramInt1;
      i = paramInt2;
      b1 = 1;
    } 
    byte b2;
    for (b2 = 0; b2 < k; b2++) {
      ViewHolder viewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(b2));
      if (viewHolder != null && viewHolder.mPosition >= i && viewHolder.mPosition <= j) {
        if (viewHolder.mPosition == paramInt1) {
          viewHolder.offsetPosition(paramInt2 - paramInt1, false);
        } else {
          viewHolder.offsetPosition(b1, false);
        } 
        this.mState.mStructureChanged = true;
      } 
    } 
    this.mRecycler.offsetPositionRecordsForMove(paramInt1, paramInt2);
    requestLayout();
  }
  
  void offsetPositionRecordsForRemove(int paramInt1, int paramInt2, boolean paramBoolean) {
    int i = this.mChildHelper.getUnfilteredChildCount();
    byte b;
    for (b = 0; b < i; b++) {
      ViewHolder viewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(b));
      if (viewHolder != null && !viewHolder.shouldIgnore())
        if (viewHolder.mPosition >= paramInt1 + paramInt2) {
          viewHolder.offsetPosition(-paramInt2, paramBoolean);
          this.mState.mStructureChanged = true;
        } else if (viewHolder.mPosition >= paramInt1) {
          viewHolder.flagRemovedAndOffsetPosition(paramInt1 - 1, -paramInt2, paramBoolean);
          this.mState.mStructureChanged = true;
        }  
    } 
    this.mRecycler.offsetPositionRecordsForRemove(paramInt1, paramInt2, paramBoolean);
    requestLayout();
  }
  
  protected void onAttachedToWindow() { // Byte code:
    //   0: aload_0
    //   1: invokespecial onAttachedToWindow : ()V
    //   4: aload_0
    //   5: iconst_0
    //   6: putfield mLayoutOrScrollCounter : I
    //   9: iconst_1
    //   10: istore_2
    //   11: aload_0
    //   12: iconst_1
    //   13: putfield mIsAttached : Z
    //   16: aload_0
    //   17: getfield mFirstLayoutComplete : Z
    //   20: ifeq -> 33
    //   23: aload_0
    //   24: invokevirtual isLayoutRequested : ()Z
    //   27: ifne -> 33
    //   30: goto -> 35
    //   33: iconst_0
    //   34: istore_2
    //   35: aload_0
    //   36: iload_2
    //   37: putfield mFirstLayoutComplete : Z
    //   40: aload_0
    //   41: getfield mLayout : Landroid/support/v7/widget/RecyclerView$LayoutManager;
    //   44: ifnull -> 55
    //   47: aload_0
    //   48: getfield mLayout : Landroid/support/v7/widget/RecyclerView$LayoutManager;
    //   51: aload_0
    //   52: invokevirtual dispatchAttachedToWindow : (Landroid/support/v7/widget/RecyclerView;)V
    //   55: aload_0
    //   56: iconst_0
    //   57: putfield mPostedAnimatorRunner : Z
    //   60: getstatic android/support/v7/widget/RecyclerView.ALLOW_THREAD_GAP_WORK : Z
    //   63: ifeq -> 164
    //   66: aload_0
    //   67: getstatic android/support/v7/widget/GapWorker.sGapWorker : Ljava/lang/ThreadLocal;
    //   70: invokevirtual get : ()Ljava/lang/Object;
    //   73: checkcast android/support/v7/widget/GapWorker
    //   76: putfield mGapWorker : Landroid/support/v7/widget/GapWorker;
    //   79: aload_0
    //   80: getfield mGapWorker : Landroid/support/v7/widget/GapWorker;
    //   83: ifnonnull -> 156
    //   86: aload_0
    //   87: new android/support/v7/widget/GapWorker
    //   90: dup
    //   91: invokespecial <init> : ()V
    //   94: putfield mGapWorker : Landroid/support/v7/widget/GapWorker;
    //   97: aload_0
    //   98: invokestatic getDisplay : (Landroid/view/View;)Landroid/view/Display;
    //   101: astore_3
    //   102: aload_0
    //   103: invokevirtual isInEditMode : ()Z
    //   106: ifne -> 129
    //   109: aload_3
    //   110: ifnull -> 129
    //   113: aload_3
    //   114: invokevirtual getRefreshRate : ()F
    //   117: fstore_1
    //   118: fload_1
    //   119: ldc_w 30.0
    //   122: fcmpl
    //   123: iflt -> 129
    //   126: goto -> 133
    //   129: ldc_w 60.0
    //   132: fstore_1
    //   133: aload_0
    //   134: getfield mGapWorker : Landroid/support/v7/widget/GapWorker;
    //   137: ldc_w 1.0E9
    //   140: fload_1
    //   141: fdiv
    //   142: f2l
    //   143: putfield mFrameIntervalNs : J
    //   146: getstatic android/support/v7/widget/GapWorker.sGapWorker : Ljava/lang/ThreadLocal;
    //   149: aload_0
    //   150: getfield mGapWorker : Landroid/support/v7/widget/GapWorker;
    //   153: invokevirtual set : (Ljava/lang/Object;)V
    //   156: aload_0
    //   157: getfield mGapWorker : Landroid/support/v7/widget/GapWorker;
    //   160: aload_0
    //   161: invokevirtual add : (Landroid/support/v7/widget/RecyclerView;)V
    //   164: return }
  
  public void onChildAttachedToWindow(View paramView) {}
  
  public void onChildDetachedFromWindow(View paramView) {}
  
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    if (this.mItemAnimator != null)
      this.mItemAnimator.endAnimations(); 
    stopScroll();
    this.mIsAttached = false;
    if (this.mLayout != null)
      this.mLayout.dispatchDetachedFromWindow(this, this.mRecycler); 
    this.mPendingAccessibilityImportanceChange.clear();
    removeCallbacks(this.mItemAnimatorRunner);
    this.mViewInfoStore.onDetach();
    if (ALLOW_THREAD_GAP_WORK && this.mGapWorker != null) {
      this.mGapWorker.remove(this);
      this.mGapWorker = null;
    } 
  }
  
  public void onDraw(Canvas paramCanvas) {
    super.onDraw(paramCanvas);
    int i = this.mItemDecorations.size();
    for (byte b = 0; b < i; b++)
      ((ItemDecoration)this.mItemDecorations.get(b)).onDraw(paramCanvas, this, this.mState); 
  }
  
  void onEnterLayoutOrScroll() { this.mLayoutOrScrollCounter++; }
  
  void onExitLayoutOrScroll() { onExitLayoutOrScroll(true); }
  
  void onExitLayoutOrScroll(boolean paramBoolean) {
    this.mLayoutOrScrollCounter--;
    if (this.mLayoutOrScrollCounter < 1) {
      this.mLayoutOrScrollCounter = 0;
      if (paramBoolean) {
        dispatchContentChangedIfNecessary();
        dispatchPendingImportantForAccessibilityChanges();
      } 
    } 
  }
  
  public boolean onGenericMotionEvent(MotionEvent paramMotionEvent) { // Byte code:
    //   0: aload_0
    //   1: getfield mLayout : Landroid/support/v7/widget/RecyclerView$LayoutManager;
    //   4: ifnonnull -> 9
    //   7: iconst_0
    //   8: ireturn
    //   9: aload_0
    //   10: getfield mLayoutFrozen : Z
    //   13: ifeq -> 18
    //   16: iconst_0
    //   17: ireturn
    //   18: aload_1
    //   19: invokevirtual getAction : ()I
    //   22: bipush #8
    //   24: if_icmpne -> 172
    //   27: aload_1
    //   28: invokevirtual getSource : ()I
    //   31: iconst_2
    //   32: iand
    //   33: ifeq -> 87
    //   36: aload_0
    //   37: getfield mLayout : Landroid/support/v7/widget/RecyclerView$LayoutManager;
    //   40: invokevirtual canScrollVertically : ()Z
    //   43: ifeq -> 57
    //   46: aload_1
    //   47: bipush #9
    //   49: invokevirtual getAxisValue : (I)F
    //   52: fneg
    //   53: fstore_3
    //   54: goto -> 59
    //   57: fconst_0
    //   58: fstore_3
    //   59: fload_3
    //   60: fstore_2
    //   61: aload_0
    //   62: getfield mLayout : Landroid/support/v7/widget/RecyclerView$LayoutManager;
    //   65: invokevirtual canScrollHorizontally : ()Z
    //   68: ifeq -> 138
    //   71: aload_1
    //   72: bipush #10
    //   74: invokevirtual getAxisValue : (I)F
    //   77: fstore #4
    //   79: fload_3
    //   80: fstore_2
    //   81: fload #4
    //   83: fstore_3
    //   84: goto -> 140
    //   87: aload_1
    //   88: invokevirtual getSource : ()I
    //   91: ldc_w 4194304
    //   94: iand
    //   95: ifeq -> 136
    //   98: aload_1
    //   99: bipush #26
    //   101: invokevirtual getAxisValue : (I)F
    //   104: fstore_3
    //   105: aload_0
    //   106: getfield mLayout : Landroid/support/v7/widget/RecyclerView$LayoutManager;
    //   109: invokevirtual canScrollVertically : ()Z
    //   112: ifeq -> 121
    //   115: fload_3
    //   116: fneg
    //   117: fstore_2
    //   118: goto -> 138
    //   121: aload_0
    //   122: getfield mLayout : Landroid/support/v7/widget/RecyclerView$LayoutManager;
    //   125: invokevirtual canScrollHorizontally : ()Z
    //   128: ifeq -> 136
    //   131: fconst_0
    //   132: fstore_2
    //   133: goto -> 140
    //   136: fconst_0
    //   137: fstore_2
    //   138: fconst_0
    //   139: fstore_3
    //   140: fload_2
    //   141: fconst_0
    //   142: fcmpl
    //   143: ifne -> 152
    //   146: fload_3
    //   147: fconst_0
    //   148: fcmpl
    //   149: ifeq -> 172
    //   152: aload_0
    //   153: fload_3
    //   154: aload_0
    //   155: getfield mScaledHorizontalScrollFactor : F
    //   158: fmul
    //   159: f2i
    //   160: fload_2
    //   161: aload_0
    //   162: getfield mScaledVerticalScrollFactor : F
    //   165: fmul
    //   166: f2i
    //   167: aload_1
    //   168: invokevirtual scrollByInternal : (IILandroid/view/MotionEvent;)Z
    //   171: pop
    //   172: iconst_0
    //   173: ireturn }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent) {
    int k;
    int[] arrayOfInt;
    StringBuilder stringBuilder;
    boolean bool2 = this.mLayoutFrozen;
    boolean bool1 = false;
    if (bool2)
      return false; 
    if (dispatchOnItemTouchIntercept(paramMotionEvent)) {
      cancelTouch();
      return true;
    } 
    if (this.mLayout == null)
      return false; 
    bool2 = this.mLayout.canScrollHorizontally();
    boolean bool3 = this.mLayout.canScrollVertically();
    if (this.mVelocityTracker == null)
      this.mVelocityTracker = VelocityTracker.obtain(); 
    this.mVelocityTracker.addMovement(paramMotionEvent);
    int j = paramMotionEvent.getActionMasked();
    int i = paramMotionEvent.getActionIndex();
    switch (j) {
      case 6:
        onPointerUp(paramMotionEvent);
        break;
      case 5:
        this.mScrollPointerId = paramMotionEvent.getPointerId(i);
        j = (int)(paramMotionEvent.getX(i) + 0.5F);
        this.mLastTouchX = j;
        this.mInitialTouchX = j;
        i = (int)(paramMotionEvent.getY(i) + 0.5F);
        this.mLastTouchY = i;
        this.mInitialTouchY = i;
        break;
      case 3:
        cancelTouch();
        break;
      case 2:
        j = paramMotionEvent.findPointerIndex(this.mScrollPointerId);
        if (j < 0) {
          stringBuilder = new StringBuilder();
          stringBuilder.append("Error processing scroll; pointer index for id ");
          stringBuilder.append(this.mScrollPointerId);
          stringBuilder.append(" not found. Did any MotionEvents get skipped?");
          Log.e("RecyclerView", stringBuilder.toString());
          return false;
        } 
        i = (int)(stringBuilder.getX(j) + 0.5F);
        k = (int)(stringBuilder.getY(j) + 0.5F);
        if (this.mScrollState != 1) {
          j = this.mInitialTouchX;
          int m = this.mInitialTouchY;
          if (bool2 && Math.abs(i - j) > this.mTouchSlop) {
            this.mLastTouchX = i;
            i = 1;
          } else {
            i = 0;
          } 
          j = i;
          if (bool3) {
            j = i;
            if (Math.abs(k - m) > this.mTouchSlop) {
              this.mLastTouchY = k;
              j = 1;
            } 
          } 
          if (j != 0)
            setScrollState(1); 
        } 
        break;
      case 1:
        this.mVelocityTracker.clear();
        stopNestedScroll(0);
        break;
      case 0:
        if (this.mIgnoreMotionEventTillDown)
          this.mIgnoreMotionEventTillDown = false; 
        this.mScrollPointerId = stringBuilder.getPointerId(0);
        i = (int)(stringBuilder.getX() + 0.5F);
        this.mLastTouchX = i;
        this.mInitialTouchX = i;
        i = (int)(stringBuilder.getY() + 0.5F);
        this.mLastTouchY = i;
        this.mInitialTouchY = i;
        if (this.mScrollState == 2) {
          getParent().requestDisallowInterceptTouchEvent(true);
          setScrollState(1);
        } 
        arrayOfInt = this.mNestedOffsets;
        this.mNestedOffsets[1] = 0;
        arrayOfInt[0] = 0;
        if (bool2) {
          i = 1;
        } else {
          i = 0;
        } 
        j = i;
        if (bool3)
          j = i | 0x2; 
        startNestedScroll(j, 0);
        break;
    } 
    if (this.mScrollState == 1)
      bool1 = true; 
    return bool1;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    TraceCompat.beginSection("RV OnLayout");
    dispatchLayout();
    TraceCompat.endSection();
    this.mFirstLayoutComplete = true;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2) {
    if (this.mLayout == null) {
      defaultOnMeasure(paramInt1, paramInt2);
      return;
    } 
    boolean bool = this.mLayout.isAutoMeasureEnabled();
    byte b = 0;
    if (bool) {
      int i = View.MeasureSpec.getMode(paramInt1);
      int j = View.MeasureSpec.getMode(paramInt2);
      this.mLayout.onMeasure(this.mRecycler, this.mState, paramInt1, paramInt2);
      byte b1 = b;
      if (i == 1073741824) {
        b1 = b;
        if (j == 1073741824)
          b1 = 1; 
      } 
      if (b1 == 0) {
        if (this.mAdapter == null)
          return; 
        if (this.mState.mLayoutStep == 1)
          dispatchLayoutStep1(); 
        this.mLayout.setMeasureSpecs(paramInt1, paramInt2);
        this.mState.mIsMeasuring = true;
        dispatchLayoutStep2();
        this.mLayout.setMeasuredDimensionFromChildren(paramInt1, paramInt2);
        if (this.mLayout.shouldMeasureTwice()) {
          this.mLayout.setMeasureSpecs(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
          this.mState.mIsMeasuring = true;
          dispatchLayoutStep2();
          this.mLayout.setMeasuredDimensionFromChildren(paramInt1, paramInt2);
          return;
        } 
      } else {
        return;
      } 
    } else {
      if (this.mHasFixedSize) {
        this.mLayout.onMeasure(this.mRecycler, this.mState, paramInt1, paramInt2);
        return;
      } 
      if (this.mAdapterUpdateDuringMeasure) {
        startInterceptRequestLayout();
        onEnterLayoutOrScroll();
        processAdapterUpdatesAndSetAnimationFlags();
        onExitLayoutOrScroll();
        if (this.mState.mRunPredictiveAnimations) {
          this.mState.mInPreLayout = true;
        } else {
          this.mAdapterHelper.consumeUpdatesInOnePass();
          this.mState.mInPreLayout = false;
        } 
        this.mAdapterUpdateDuringMeasure = false;
        stopInterceptRequestLayout(false);
      } else if (this.mState.mRunPredictiveAnimations) {
        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
        return;
      } 
      if (this.mAdapter != null) {
        this.mState.mItemCount = this.mAdapter.getItemCount();
      } else {
        this.mState.mItemCount = 0;
      } 
      startInterceptRequestLayout();
      this.mLayout.onMeasure(this.mRecycler, this.mState, paramInt1, paramInt2);
      stopInterceptRequestLayout(false);
      this.mState.mInPreLayout = false;
    } 
  }
  
  protected boolean onRequestFocusInDescendants(int paramInt, Rect paramRect) { return isComputingLayout() ? false : super.onRequestFocusInDescendants(paramInt, paramRect); }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable) {
    if (!(paramParcelable instanceof SavedState)) {
      super.onRestoreInstanceState(paramParcelable);
      return;
    } 
    this.mPendingSavedState = (SavedState)paramParcelable;
    super.onRestoreInstanceState(this.mPendingSavedState.getSuperState());
    if (this.mLayout != null && this.mPendingSavedState.mLayoutState != null)
      this.mLayout.onRestoreInstanceState(this.mPendingSavedState.mLayoutState); 
  }
  
  protected Parcelable onSaveInstanceState() {
    SavedState savedState = new SavedState(super.onSaveInstanceState());
    if (this.mPendingSavedState != null) {
      savedState.copyFrom(this.mPendingSavedState);
      return savedState;
    } 
    if (this.mLayout != null) {
      savedState.mLayoutState = this.mLayout.onSaveInstanceState();
      return savedState;
    } 
    savedState.mLayoutState = null;
    return savedState;
  }
  
  public void onScrollStateChanged(int paramInt) {}
  
  public void onScrolled(int paramInt1, int paramInt2) {}
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if (paramInt1 != paramInt3 || paramInt2 != paramInt4)
      invalidateGlows(); 
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent) { // Byte code:
    //   0: aload_0
    //   1: getfield mLayoutFrozen : Z
    //   4: istore #13
    //   6: iconst_0
    //   7: istore #10
    //   9: iload #13
    //   11: ifne -> 1006
    //   14: aload_0
    //   15: getfield mIgnoreMotionEventTillDown : Z
    //   18: ifeq -> 23
    //   21: iconst_0
    //   22: ireturn
    //   23: aload_0
    //   24: aload_1
    //   25: invokespecial dispatchOnItemTouch : (Landroid/view/MotionEvent;)Z
    //   28: ifeq -> 37
    //   31: aload_0
    //   32: invokespecial cancelTouch : ()V
    //   35: iconst_1
    //   36: ireturn
    //   37: aload_0
    //   38: getfield mLayout : Landroid/support/v7/widget/RecyclerView$LayoutManager;
    //   41: ifnonnull -> 46
    //   44: iconst_0
    //   45: ireturn
    //   46: aload_0
    //   47: getfield mLayout : Landroid/support/v7/widget/RecyclerView$LayoutManager;
    //   50: invokevirtual canScrollHorizontally : ()Z
    //   53: istore #13
    //   55: aload_0
    //   56: getfield mLayout : Landroid/support/v7/widget/RecyclerView$LayoutManager;
    //   59: invokevirtual canScrollVertically : ()Z
    //   62: istore #14
    //   64: aload_0
    //   65: getfield mVelocityTracker : Landroid/view/VelocityTracker;
    //   68: ifnonnull -> 78
    //   71: aload_0
    //   72: invokestatic obtain : ()Landroid/view/VelocityTracker;
    //   75: putfield mVelocityTracker : Landroid/view/VelocityTracker;
    //   78: aload_1
    //   79: invokestatic obtain : (Landroid/view/MotionEvent;)Landroid/view/MotionEvent;
    //   82: astore #15
    //   84: aload_1
    //   85: invokevirtual getActionMasked : ()I
    //   88: istore #5
    //   90: aload_1
    //   91: invokevirtual getActionIndex : ()I
    //   94: istore #4
    //   96: iload #5
    //   98: ifne -> 119
    //   101: aload_0
    //   102: getfield mNestedOffsets : [I
    //   105: astore #16
    //   107: aload_0
    //   108: getfield mNestedOffsets : [I
    //   111: iconst_1
    //   112: iconst_0
    //   113: iastore
    //   114: aload #16
    //   116: iconst_0
    //   117: iconst_0
    //   118: iastore
    //   119: aload #15
    //   121: aload_0
    //   122: getfield mNestedOffsets : [I
    //   125: iconst_0
    //   126: iaload
    //   127: i2f
    //   128: aload_0
    //   129: getfield mNestedOffsets : [I
    //   132: iconst_1
    //   133: iaload
    //   134: i2f
    //   135: invokevirtual offsetLocation : (FF)V
    //   138: iload #5
    //   140: tableswitch default -> 184, 0 -> 889, 1 -> 781, 2 -> 281, 3 -> 270, 4 -> 184, 5 -> 203, 6 -> 191
    //   184: iload #10
    //   186: istore #4
    //   188: goto -> 985
    //   191: aload_0
    //   192: aload_1
    //   193: invokespecial onPointerUp : (Landroid/view/MotionEvent;)V
    //   196: iload #10
    //   198: istore #4
    //   200: goto -> 985
    //   203: aload_0
    //   204: aload_1
    //   205: iload #4
    //   207: invokevirtual getPointerId : (I)I
    //   210: putfield mScrollPointerId : I
    //   213: aload_1
    //   214: iload #4
    //   216: invokevirtual getX : (I)F
    //   219: ldc_w 0.5
    //   222: fadd
    //   223: f2i
    //   224: istore #5
    //   226: aload_0
    //   227: iload #5
    //   229: putfield mLastTouchX : I
    //   232: aload_0
    //   233: iload #5
    //   235: putfield mInitialTouchX : I
    //   238: aload_1
    //   239: iload #4
    //   241: invokevirtual getY : (I)F
    //   244: ldc_w 0.5
    //   247: fadd
    //   248: f2i
    //   249: istore #4
    //   251: aload_0
    //   252: iload #4
    //   254: putfield mLastTouchY : I
    //   257: aload_0
    //   258: iload #4
    //   260: putfield mInitialTouchY : I
    //   263: iload #10
    //   265: istore #4
    //   267: goto -> 985
    //   270: aload_0
    //   271: invokespecial cancelTouch : ()V
    //   274: iload #10
    //   276: istore #4
    //   278: goto -> 985
    //   281: aload_1
    //   282: aload_0
    //   283: getfield mScrollPointerId : I
    //   286: invokevirtual findPointerIndex : (I)I
    //   289: istore #4
    //   291: iload #4
    //   293: ifge -> 341
    //   296: new java/lang/StringBuilder
    //   299: dup
    //   300: invokespecial <init> : ()V
    //   303: astore_1
    //   304: aload_1
    //   305: ldc_w 'Error processing scroll; pointer index for id '
    //   308: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   311: pop
    //   312: aload_1
    //   313: aload_0
    //   314: getfield mScrollPointerId : I
    //   317: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   320: pop
    //   321: aload_1
    //   322: ldc_w ' not found. Did any MotionEvents get skipped?'
    //   325: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   328: pop
    //   329: ldc 'RecyclerView'
    //   331: aload_1
    //   332: invokevirtual toString : ()Ljava/lang/String;
    //   335: invokestatic e : (Ljava/lang/String;Ljava/lang/String;)I
    //   338: pop
    //   339: iconst_0
    //   340: ireturn
    //   341: aload_1
    //   342: iload #4
    //   344: invokevirtual getX : (I)F
    //   347: ldc_w 0.5
    //   350: fadd
    //   351: f2i
    //   352: istore #11
    //   354: aload_1
    //   355: iload #4
    //   357: invokevirtual getY : (I)F
    //   360: ldc_w 0.5
    //   363: fadd
    //   364: f2i
    //   365: istore #12
    //   367: aload_0
    //   368: getfield mLastTouchX : I
    //   371: iload #11
    //   373: isub
    //   374: istore #7
    //   376: aload_0
    //   377: getfield mLastTouchY : I
    //   380: iload #12
    //   382: isub
    //   383: istore #6
    //   385: iload #7
    //   387: istore #5
    //   389: iload #6
    //   391: istore #4
    //   393: aload_0
    //   394: iload #7
    //   396: iload #6
    //   398: aload_0
    //   399: getfield mScrollConsumed : [I
    //   402: aload_0
    //   403: getfield mScrollOffset : [I
    //   406: iconst_0
    //   407: invokevirtual dispatchNestedPreScroll : (II[I[II)Z
    //   410: ifeq -> 490
    //   413: iload #7
    //   415: aload_0
    //   416: getfield mScrollConsumed : [I
    //   419: iconst_0
    //   420: iaload
    //   421: isub
    //   422: istore #5
    //   424: iload #6
    //   426: aload_0
    //   427: getfield mScrollConsumed : [I
    //   430: iconst_1
    //   431: iaload
    //   432: isub
    //   433: istore #4
    //   435: aload #15
    //   437: aload_0
    //   438: getfield mScrollOffset : [I
    //   441: iconst_0
    //   442: iaload
    //   443: i2f
    //   444: aload_0
    //   445: getfield mScrollOffset : [I
    //   448: iconst_1
    //   449: iaload
    //   450: i2f
    //   451: invokevirtual offsetLocation : (FF)V
    //   454: aload_0
    //   455: getfield mNestedOffsets : [I
    //   458: astore_1
    //   459: aload_1
    //   460: iconst_0
    //   461: aload_1
    //   462: iconst_0
    //   463: iaload
    //   464: aload_0
    //   465: getfield mScrollOffset : [I
    //   468: iconst_0
    //   469: iaload
    //   470: iadd
    //   471: iastore
    //   472: aload_0
    //   473: getfield mNestedOffsets : [I
    //   476: astore_1
    //   477: aload_1
    //   478: iconst_1
    //   479: aload_1
    //   480: iconst_1
    //   481: iaload
    //   482: aload_0
    //   483: getfield mScrollOffset : [I
    //   486: iconst_1
    //   487: iaload
    //   488: iadd
    //   489: iastore
    //   490: iload #5
    //   492: istore #6
    //   494: iload #4
    //   496: istore #7
    //   498: aload_0
    //   499: getfield mScrollState : I
    //   502: iconst_1
    //   503: if_icmpeq -> 646
    //   506: iload #13
    //   508: ifeq -> 555
    //   511: iload #5
    //   513: invokestatic abs : (I)I
    //   516: aload_0
    //   517: getfield mTouchSlop : I
    //   520: if_icmple -> 555
    //   523: iload #5
    //   525: ifle -> 540
    //   528: iload #5
    //   530: aload_0
    //   531: getfield mTouchSlop : I
    //   534: isub
    //   535: istore #5
    //   537: goto -> 549
    //   540: iload #5
    //   542: aload_0
    //   543: getfield mTouchSlop : I
    //   546: iadd
    //   547: istore #5
    //   549: iconst_1
    //   550: istore #6
    //   552: goto -> 558
    //   555: iconst_0
    //   556: istore #6
    //   558: iload #6
    //   560: istore #9
    //   562: iload #4
    //   564: istore #8
    //   566: iload #14
    //   568: ifeq -> 620
    //   571: iload #6
    //   573: istore #9
    //   575: iload #4
    //   577: istore #8
    //   579: iload #4
    //   581: invokestatic abs : (I)I
    //   584: aload_0
    //   585: getfield mTouchSlop : I
    //   588: if_icmple -> 620
    //   591: iload #4
    //   593: ifle -> 608
    //   596: iload #4
    //   598: aload_0
    //   599: getfield mTouchSlop : I
    //   602: isub
    //   603: istore #8
    //   605: goto -> 617
    //   608: iload #4
    //   610: aload_0
    //   611: getfield mTouchSlop : I
    //   614: iadd
    //   615: istore #8
    //   617: iconst_1
    //   618: istore #9
    //   620: iload #5
    //   622: istore #6
    //   624: iload #8
    //   626: istore #7
    //   628: iload #9
    //   630: ifeq -> 646
    //   633: aload_0
    //   634: iconst_1
    //   635: invokevirtual setScrollState : (I)V
    //   638: iload #8
    //   640: istore #7
    //   642: iload #5
    //   644: istore #6
    //   646: iload #10
    //   648: istore #4
    //   650: aload_0
    //   651: getfield mScrollState : I
    //   654: iconst_1
    //   655: if_icmpne -> 985
    //   658: aload_0
    //   659: iload #11
    //   661: aload_0
    //   662: getfield mScrollOffset : [I
    //   665: iconst_0
    //   666: iaload
    //   667: isub
    //   668: putfield mLastTouchX : I
    //   671: aload_0
    //   672: iload #12
    //   674: aload_0
    //   675: getfield mScrollOffset : [I
    //   678: iconst_1
    //   679: iaload
    //   680: isub
    //   681: putfield mLastTouchY : I
    //   684: iload #13
    //   686: ifeq -> 696
    //   689: iload #6
    //   691: istore #4
    //   693: goto -> 699
    //   696: iconst_0
    //   697: istore #4
    //   699: iload #14
    //   701: ifeq -> 711
    //   704: iload #7
    //   706: istore #5
    //   708: goto -> 714
    //   711: iconst_0
    //   712: istore #5
    //   714: aload_0
    //   715: iload #4
    //   717: iload #5
    //   719: aload #15
    //   721: invokevirtual scrollByInternal : (IILandroid/view/MotionEvent;)Z
    //   724: ifeq -> 737
    //   727: aload_0
    //   728: invokevirtual getParent : ()Landroid/view/ViewParent;
    //   731: iconst_1
    //   732: invokeinterface requestDisallowInterceptTouchEvent : (Z)V
    //   737: iload #10
    //   739: istore #4
    //   741: aload_0
    //   742: getfield mGapWorker : Landroid/support/v7/widget/GapWorker;
    //   745: ifnull -> 985
    //   748: iload #6
    //   750: ifne -> 762
    //   753: iload #10
    //   755: istore #4
    //   757: iload #7
    //   759: ifeq -> 985
    //   762: aload_0
    //   763: getfield mGapWorker : Landroid/support/v7/widget/GapWorker;
    //   766: aload_0
    //   767: iload #6
    //   769: iload #7
    //   771: invokevirtual postFromTraversal : (Landroid/support/v7/widget/RecyclerView;II)V
    //   774: iload #10
    //   776: istore #4
    //   778: goto -> 985
    //   781: aload_0
    //   782: getfield mVelocityTracker : Landroid/view/VelocityTracker;
    //   785: aload #15
    //   787: invokevirtual addMovement : (Landroid/view/MotionEvent;)V
    //   790: aload_0
    //   791: getfield mVelocityTracker : Landroid/view/VelocityTracker;
    //   794: sipush #1000
    //   797: aload_0
    //   798: getfield mMaxFlingVelocity : I
    //   801: i2f
    //   802: invokevirtual computeCurrentVelocity : (IF)V
    //   805: iload #13
    //   807: ifeq -> 826
    //   810: aload_0
    //   811: getfield mVelocityTracker : Landroid/view/VelocityTracker;
    //   814: aload_0
    //   815: getfield mScrollPointerId : I
    //   818: invokevirtual getXVelocity : (I)F
    //   821: fneg
    //   822: fstore_2
    //   823: goto -> 828
    //   826: fconst_0
    //   827: fstore_2
    //   828: iload #14
    //   830: ifeq -> 849
    //   833: aload_0
    //   834: getfield mVelocityTracker : Landroid/view/VelocityTracker;
    //   837: aload_0
    //   838: getfield mScrollPointerId : I
    //   841: invokevirtual getYVelocity : (I)F
    //   844: fneg
    //   845: fstore_3
    //   846: goto -> 851
    //   849: fconst_0
    //   850: fstore_3
    //   851: fload_2
    //   852: fconst_0
    //   853: fcmpl
    //   854: ifne -> 863
    //   857: fload_3
    //   858: fconst_0
    //   859: fcmpl
    //   860: ifeq -> 874
    //   863: aload_0
    //   864: fload_2
    //   865: f2i
    //   866: fload_3
    //   867: f2i
    //   868: invokevirtual fling : (II)Z
    //   871: ifne -> 879
    //   874: aload_0
    //   875: iconst_0
    //   876: invokevirtual setScrollState : (I)V
    //   879: aload_0
    //   880: invokespecial resetTouch : ()V
    //   883: iconst_1
    //   884: istore #4
    //   886: goto -> 985
    //   889: aload_0
    //   890: aload_1
    //   891: iconst_0
    //   892: invokevirtual getPointerId : (I)I
    //   895: putfield mScrollPointerId : I
    //   898: aload_1
    //   899: invokevirtual getX : ()F
    //   902: ldc_w 0.5
    //   905: fadd
    //   906: f2i
    //   907: istore #4
    //   909: aload_0
    //   910: iload #4
    //   912: putfield mLastTouchX : I
    //   915: aload_0
    //   916: iload #4
    //   918: putfield mInitialTouchX : I
    //   921: aload_1
    //   922: invokevirtual getY : ()F
    //   925: ldc_w 0.5
    //   928: fadd
    //   929: f2i
    //   930: istore #4
    //   932: aload_0
    //   933: iload #4
    //   935: putfield mLastTouchY : I
    //   938: aload_0
    //   939: iload #4
    //   941: putfield mInitialTouchY : I
    //   944: iload #13
    //   946: ifeq -> 955
    //   949: iconst_1
    //   950: istore #4
    //   952: goto -> 958
    //   955: iconst_0
    //   956: istore #4
    //   958: iload #4
    //   960: istore #5
    //   962: iload #14
    //   964: ifeq -> 973
    //   967: iload #4
    //   969: iconst_2
    //   970: ior
    //   971: istore #5
    //   973: aload_0
    //   974: iload #5
    //   976: iconst_0
    //   977: invokevirtual startNestedScroll : (II)Z
    //   980: pop
    //   981: iload #10
    //   983: istore #4
    //   985: iload #4
    //   987: ifne -> 999
    //   990: aload_0
    //   991: getfield mVelocityTracker : Landroid/view/VelocityTracker;
    //   994: aload #15
    //   996: invokevirtual addMovement : (Landroid/view/MotionEvent;)V
    //   999: aload #15
    //   1001: invokevirtual recycle : ()V
    //   1004: iconst_1
    //   1005: ireturn
    //   1006: iconst_0
    //   1007: ireturn }
  
  void postAnimationRunner() {
    if (!this.mPostedAnimatorRunner && this.mIsAttached) {
      ViewCompat.postOnAnimation(this, this.mItemAnimatorRunner);
      this.mPostedAnimatorRunner = true;
    } 
  }
  
  void processDataSetCompletelyChanged(boolean paramBoolean) {
    this.mDispatchItemsChangedEvent = paramBoolean | this.mDispatchItemsChangedEvent;
    this.mDataSetHasChangedAfterLayout = true;
    markKnownViewsInvalid();
  }
  
  void recordAnimationInfoIfBouncedHiddenView(ViewHolder paramViewHolder, ItemAnimator.ItemHolderInfo paramItemHolderInfo) {
    paramViewHolder.setFlags(0, 8192);
    if (this.mState.mTrackOldChangeHolders && paramViewHolder.isUpdated() && !paramViewHolder.isRemoved() && !paramViewHolder.shouldIgnore()) {
      long l = getChangedHolderKey(paramViewHolder);
      this.mViewInfoStore.addToOldChangeHolders(l, paramViewHolder);
    } 
    this.mViewInfoStore.addToPreLayout(paramViewHolder, paramItemHolderInfo);
  }
  
  void removeAndRecycleViews() {
    if (this.mItemAnimator != null)
      this.mItemAnimator.endAnimations(); 
    if (this.mLayout != null) {
      this.mLayout.removeAndRecycleAllViews(this.mRecycler);
      this.mLayout.removeAndRecycleScrapInt(this.mRecycler);
    } 
    this.mRecycler.clear();
  }
  
  boolean removeAnimatingView(View paramView) {
    startInterceptRequestLayout();
    boolean bool = this.mChildHelper.removeViewIfHidden(paramView);
    if (bool) {
      ViewHolder viewHolder = getChildViewHolderInt(paramView);
      this.mRecycler.unscrapView(viewHolder);
      this.mRecycler.recycleViewHolderInternal(viewHolder);
    } 
    stopInterceptRequestLayout(bool ^ true);
    return bool;
  }
  
  protected void removeDetachedView(View paramView, boolean paramBoolean) {
    StringBuilder stringBuilder;
    ViewHolder viewHolder = getChildViewHolderInt(paramView);
    if (viewHolder != null)
      if (viewHolder.isTmpDetached()) {
        viewHolder.clearTmpDetachFlag();
      } else if (!viewHolder.shouldIgnore()) {
        stringBuilder = new StringBuilder();
        stringBuilder.append("Called removeDetachedView with a view which is not flagged as tmp detached.");
        stringBuilder.append(viewHolder);
        stringBuilder.append(exceptionLabel());
        throw new IllegalArgumentException(stringBuilder.toString());
      }  
    stringBuilder.clearAnimation();
    dispatchChildDetached(stringBuilder);
    super.removeDetachedView(stringBuilder, paramBoolean);
  }
  
  public void removeItemDecoration(ItemDecoration paramItemDecoration) {
    if (this.mLayout != null)
      this.mLayout.assertNotInLayoutOrScroll("Cannot remove item decoration during a scroll  or layout"); 
    this.mItemDecorations.remove(paramItemDecoration);
    if (this.mItemDecorations.isEmpty()) {
      boolean bool;
      if (getOverScrollMode() == 2) {
        bool = true;
      } else {
        bool = false;
      } 
      setWillNotDraw(bool);
    } 
    markItemDecorInsetsDirty();
    requestLayout();
  }
  
  public void removeItemDecorationAt(int paramInt) {
    int i = getItemDecorationCount();
    if (paramInt < 0 || paramInt >= i) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(paramInt);
      stringBuilder.append(" is an invalid index for size ");
      stringBuilder.append(i);
      throw new IndexOutOfBoundsException(stringBuilder.toString());
    } 
    removeItemDecoration(getItemDecorationAt(paramInt));
  }
  
  public void removeOnChildAttachStateChangeListener(OnChildAttachStateChangeListener paramOnChildAttachStateChangeListener) {
    if (this.mOnChildAttachStateListeners == null)
      return; 
    this.mOnChildAttachStateListeners.remove(paramOnChildAttachStateChangeListener);
  }
  
  public void removeOnItemTouchListener(OnItemTouchListener paramOnItemTouchListener) {
    this.mOnItemTouchListeners.remove(paramOnItemTouchListener);
    if (this.mActiveOnItemTouchListener == paramOnItemTouchListener)
      this.mActiveOnItemTouchListener = null; 
  }
  
  public void removeOnScrollListener(OnScrollListener paramOnScrollListener) {
    if (this.mScrollListeners != null)
      this.mScrollListeners.remove(paramOnScrollListener); 
  }
  
  void repositionShadowingViews() {
    int i = this.mChildHelper.getChildCount();
    for (byte b = 0; b < i; b++) {
      View view = this.mChildHelper.getChildAt(b);
      ViewHolder viewHolder = getChildViewHolder(view);
      if (viewHolder != null && viewHolder.mShadowingHolder != null) {
        View view1 = viewHolder.mShadowingHolder.itemView;
        int j = view.getLeft();
        int k = view.getTop();
        if (j != view1.getLeft() || k != view1.getTop())
          view1.layout(j, k, view1.getWidth() + j, view1.getHeight() + k); 
      } 
    } 
  }
  
  public void requestChildFocus(View paramView1, View paramView2) {
    if (!this.mLayout.onRequestChildFocus(this, this.mState, paramView1, paramView2) && paramView2 != null)
      requestChildOnScreen(paramView1, paramView2); 
    super.requestChildFocus(paramView1, paramView2);
  }
  
  public boolean requestChildRectangleOnScreen(View paramView, Rect paramRect, boolean paramBoolean) { return this.mLayout.requestChildRectangleOnScreen(this, paramView, paramRect, paramBoolean); }
  
  public void requestDisallowInterceptTouchEvent(boolean paramBoolean) {
    int i = this.mOnItemTouchListeners.size();
    for (byte b = 0; b < i; b++)
      ((OnItemTouchListener)this.mOnItemTouchListeners.get(b)).onRequestDisallowInterceptTouchEvent(paramBoolean); 
    super.requestDisallowInterceptTouchEvent(paramBoolean);
  }
  
  public void requestLayout() {
    if (this.mInterceptRequestLayoutDepth == 0 && !this.mLayoutFrozen) {
      super.requestLayout();
      return;
    } 
    this.mLayoutWasDefered = true;
  }
  
  void saveOldPositions() {
    int i = this.mChildHelper.getUnfilteredChildCount();
    for (byte b = 0; b < i; b++) {
      ViewHolder viewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(b));
      if (!viewHolder.shouldIgnore())
        viewHolder.saveOldPosition(); 
    } 
  }
  
  public void scrollBy(int paramInt1, int paramInt2) {
    if (this.mLayout == null) {
      Log.e("RecyclerView", "Cannot scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
      return;
    } 
    if (this.mLayoutFrozen)
      return; 
    boolean bool1 = this.mLayout.canScrollHorizontally();
    boolean bool2 = this.mLayout.canScrollVertically();
    if (bool1 || bool2) {
      if (!bool1)
        paramInt1 = 0; 
      if (!bool2)
        paramInt2 = 0; 
      scrollByInternal(paramInt1, paramInt2, null);
    } 
  }
  
  boolean scrollByInternal(int paramInt1, int paramInt2, MotionEvent paramMotionEvent) {
    byte b4;
    byte b3;
    byte b2;
    byte b1;
    int[] arrayOfInt;
    consumePendingUpdateOperations();
    Adapter adapter = this.mAdapter;
    boolean bool = false;
    if (adapter != null) {
      startInterceptRequestLayout();
      onEnterLayoutOrScroll();
      TraceCompat.beginSection("RV Scroll");
      fillRemainingScrollValues(this.mState);
      if (paramInt1 != 0) {
        b2 = this.mLayout.scrollHorizontallyBy(paramInt1, this.mRecycler, this.mState);
        b1 = paramInt1 - b2;
      } else {
        b2 = 0;
        b1 = 0;
      } 
      if (paramInt2 != 0) {
        b4 = this.mLayout.scrollVerticallyBy(paramInt2, this.mRecycler, this.mState);
        b3 = paramInt2 - b4;
      } else {
        b4 = 0;
        b3 = 0;
      } 
      TraceCompat.endSection();
      repositionShadowingViews();
      onExitLayoutOrScroll();
      stopInterceptRequestLayout(false);
    } else {
      b2 = 0;
      b1 = 0;
      b4 = 0;
      b3 = 0;
    } 
    if (!this.mItemDecorations.isEmpty())
      invalidate(); 
    if (dispatchNestedScroll(b2, b4, b1, b3, this.mScrollOffset, 0)) {
      this.mLastTouchX -= this.mScrollOffset[0];
      this.mLastTouchY -= this.mScrollOffset[1];
      if (paramMotionEvent != null)
        paramMotionEvent.offsetLocation(this.mScrollOffset[0], this.mScrollOffset[1]); 
      arrayOfInt = this.mNestedOffsets;
      arrayOfInt[0] = arrayOfInt[0] + this.mScrollOffset[0];
      arrayOfInt = this.mNestedOffsets;
      arrayOfInt[1] = arrayOfInt[1] + this.mScrollOffset[1];
    } else if (getOverScrollMode() != 2) {
      if (arrayOfInt != null && !MotionEventCompat.isFromSource(arrayOfInt, 8194))
        pullGlows(arrayOfInt.getX(), b1, arrayOfInt.getY(), b3); 
      considerReleasingGlowsOnScroll(paramInt1, paramInt2);
    } 
    if (b2 != 0 || b4 != 0)
      dispatchOnScrolled(b2, b4); 
    if (!awakenScrollBars())
      invalidate(); 
    if (b2 != 0 || b4 != 0)
      bool = true; 
    return bool;
  }
  
  public void scrollTo(int paramInt1, int paramInt2) { Log.w("RecyclerView", "RecyclerView does not support scrolling to an absolute position. Use scrollToPosition instead"); }
  
  public void scrollToPosition(int paramInt) {
    if (this.mLayoutFrozen)
      return; 
    stopScroll();
    if (this.mLayout == null) {
      Log.e("RecyclerView", "Cannot scroll to position a LayoutManager set. Call setLayoutManager with a non-null argument.");
      return;
    } 
    this.mLayout.scrollToPosition(paramInt);
    awakenScrollBars();
  }
  
  public void sendAccessibilityEventUnchecked(AccessibilityEvent paramAccessibilityEvent) {
    if (shouldDeferAccessibilityEvent(paramAccessibilityEvent))
      return; 
    super.sendAccessibilityEventUnchecked(paramAccessibilityEvent);
  }
  
  public void setAccessibilityDelegateCompat(RecyclerViewAccessibilityDelegate paramRecyclerViewAccessibilityDelegate) {
    this.mAccessibilityDelegate = paramRecyclerViewAccessibilityDelegate;
    ViewCompat.setAccessibilityDelegate(this, this.mAccessibilityDelegate);
  }
  
  public void setAdapter(Adapter paramAdapter) {
    setLayoutFrozen(false);
    setAdapterInternal(paramAdapter, false, true);
    processDataSetCompletelyChanged(false);
    requestLayout();
  }
  
  public void setChildDrawingOrderCallback(ChildDrawingOrderCallback paramChildDrawingOrderCallback) {
    boolean bool;
    if (paramChildDrawingOrderCallback == this.mChildDrawingOrderCallback)
      return; 
    this.mChildDrawingOrderCallback = paramChildDrawingOrderCallback;
    if (this.mChildDrawingOrderCallback != null) {
      bool = true;
    } else {
      bool = false;
    } 
    setChildrenDrawingOrderEnabled(bool);
  }
  
  @VisibleForTesting
  boolean setChildImportantForAccessibilityInternal(ViewHolder paramViewHolder, int paramInt) {
    if (isComputingLayout()) {
      paramViewHolder.mPendingAccessibilityState = paramInt;
      this.mPendingAccessibilityImportanceChange.add(paramViewHolder);
      return false;
    } 
    ViewCompat.setImportantForAccessibility(paramViewHolder.itemView, paramInt);
    return true;
  }
  
  public void setClipToPadding(boolean paramBoolean) {
    if (paramBoolean != this.mClipToPadding)
      invalidateGlows(); 
    this.mClipToPadding = paramBoolean;
    super.setClipToPadding(paramBoolean);
    if (this.mFirstLayoutComplete)
      requestLayout(); 
  }
  
  public void setEdgeEffectFactory(@NonNull EdgeEffectFactory paramEdgeEffectFactory) {
    Preconditions.checkNotNull(paramEdgeEffectFactory);
    this.mEdgeEffectFactory = paramEdgeEffectFactory;
    invalidateGlows();
  }
  
  public void setHasFixedSize(boolean paramBoolean) { this.mHasFixedSize = paramBoolean; }
  
  public void setItemAnimator(ItemAnimator paramItemAnimator) {
    if (this.mItemAnimator != null) {
      this.mItemAnimator.endAnimations();
      this.mItemAnimator.setListener(null);
    } 
    this.mItemAnimator = paramItemAnimator;
    if (this.mItemAnimator != null)
      this.mItemAnimator.setListener(this.mItemAnimatorListener); 
  }
  
  public void setItemViewCacheSize(int paramInt) { this.mRecycler.setViewCacheSize(paramInt); }
  
  public void setLayoutFrozen(boolean paramBoolean) {
    if (paramBoolean != this.mLayoutFrozen) {
      assertNotInLayoutOrScroll("Do not setLayoutFrozen in layout or scroll");
      if (!paramBoolean) {
        this.mLayoutFrozen = false;
        if (this.mLayoutWasDefered && this.mLayout != null && this.mAdapter != null)
          requestLayout(); 
        this.mLayoutWasDefered = false;
        return;
      } 
      long l = SystemClock.uptimeMillis();
      onTouchEvent(MotionEvent.obtain(l, l, 3, 0.0F, 0.0F, 0));
      this.mLayoutFrozen = true;
      this.mIgnoreMotionEventTillDown = true;
      stopScroll();
    } 
  }
  
  public void setLayoutManager(LayoutManager paramLayoutManager) {
    if (paramLayoutManager == this.mLayout)
      return; 
    stopScroll();
    if (this.mLayout != null) {
      if (this.mItemAnimator != null)
        this.mItemAnimator.endAnimations(); 
      this.mLayout.removeAndRecycleAllViews(this.mRecycler);
      this.mLayout.removeAndRecycleScrapInt(this.mRecycler);
      this.mRecycler.clear();
      if (this.mIsAttached)
        this.mLayout.dispatchDetachedFromWindow(this, this.mRecycler); 
      this.mLayout.setRecyclerView(null);
      this.mLayout = null;
    } else {
      this.mRecycler.clear();
    } 
    this.mChildHelper.removeAllViewsUnfiltered();
    this.mLayout = paramLayoutManager;
    if (paramLayoutManager != null) {
      if (paramLayoutManager.mRecyclerView != null) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("LayoutManager ");
        stringBuilder.append(paramLayoutManager);
        stringBuilder.append(" is already attached to a RecyclerView:");
        stringBuilder.append(paramLayoutManager.mRecyclerView.exceptionLabel());
        throw new IllegalArgumentException(stringBuilder.toString());
      } 
      this.mLayout.setRecyclerView(this);
      if (this.mIsAttached)
        this.mLayout.dispatchAttachedToWindow(this); 
    } 
    this.mRecycler.updateViewCacheSize();
    requestLayout();
  }
  
  public void setNestedScrollingEnabled(boolean paramBoolean) { getScrollingChildHelper().setNestedScrollingEnabled(paramBoolean); }
  
  public void setOnFlingListener(@Nullable OnFlingListener paramOnFlingListener) { this.mOnFlingListener = paramOnFlingListener; }
  
  @Deprecated
  public void setOnScrollListener(OnScrollListener paramOnScrollListener) { this.mScrollListener = paramOnScrollListener; }
  
  public void setPreserveFocusAfterLayout(boolean paramBoolean) { this.mPreserveFocusAfterLayout = paramBoolean; }
  
  public void setRecycledViewPool(RecycledViewPool paramRecycledViewPool) { this.mRecycler.setRecycledViewPool(paramRecycledViewPool); }
  
  public void setRecyclerListener(RecyclerListener paramRecyclerListener) { this.mRecyclerListener = paramRecyclerListener; }
  
  void setScrollState(int paramInt) {
    if (paramInt == this.mScrollState)
      return; 
    this.mScrollState = paramInt;
    if (paramInt != 2)
      stopScrollersInternal(); 
    dispatchOnScrollStateChanged(paramInt);
  }
  
  public void setScrollingTouchSlop(int paramInt) {
    StringBuilder stringBuilder;
    ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
    switch (paramInt) {
      default:
        stringBuilder = new StringBuilder();
        stringBuilder.append("setScrollingTouchSlop(): bad argument constant ");
        stringBuilder.append(paramInt);
        stringBuilder.append("; using default value");
        Log.w("RecyclerView", stringBuilder.toString());
        break;
      case 1:
        this.mTouchSlop = viewConfiguration.getScaledPagingTouchSlop();
        return;
      case 0:
        break;
    } 
    this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
  }
  
  public void setViewCacheExtension(ViewCacheExtension paramViewCacheExtension) { this.mRecycler.setViewCacheExtension(paramViewCacheExtension); }
  
  boolean shouldDeferAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent) {
    if (isComputingLayout()) {
      boolean bool;
      if (paramAccessibilityEvent != null) {
        bool = AccessibilityEventCompat.getContentChangeTypes(paramAccessibilityEvent);
      } else {
        bool = false;
      } 
      int i = bool;
      if (!bool)
        i = 0; 
      this.mEatenAccessibilityChangeFlags = i | this.mEatenAccessibilityChangeFlags;
      return true;
    } 
    return false;
  }
  
  public void smoothScrollBy(int paramInt1, int paramInt2) { smoothScrollBy(paramInt1, paramInt2, null); }
  
  public void smoothScrollBy(int paramInt1, int paramInt2, Interpolator paramInterpolator) {
    if (this.mLayout == null) {
      Log.e("RecyclerView", "Cannot smooth scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
      return;
    } 
    if (this.mLayoutFrozen)
      return; 
    if (!this.mLayout.canScrollHorizontally())
      paramInt1 = 0; 
    if (!this.mLayout.canScrollVertically())
      paramInt2 = 0; 
    if (paramInt1 != 0 || paramInt2 != 0)
      this.mViewFlinger.smoothScrollBy(paramInt1, paramInt2, paramInterpolator); 
  }
  
  public void smoothScrollToPosition(int paramInt) {
    if (this.mLayoutFrozen)
      return; 
    if (this.mLayout == null) {
      Log.e("RecyclerView", "Cannot smooth scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
      return;
    } 
    this.mLayout.smoothScrollToPosition(this, this.mState, paramInt);
  }
  
  void startInterceptRequestLayout() {
    this.mInterceptRequestLayoutDepth++;
    if (this.mInterceptRequestLayoutDepth == 1 && !this.mLayoutFrozen)
      this.mLayoutWasDefered = false; 
  }
  
  public boolean startNestedScroll(int paramInt) { return getScrollingChildHelper().startNestedScroll(paramInt); }
  
  public boolean startNestedScroll(int paramInt1, int paramInt2) { return getScrollingChildHelper().startNestedScroll(paramInt1, paramInt2); }
  
  void stopInterceptRequestLayout(boolean paramBoolean) {
    if (this.mInterceptRequestLayoutDepth < 1)
      this.mInterceptRequestLayoutDepth = 1; 
    if (!paramBoolean && !this.mLayoutFrozen)
      this.mLayoutWasDefered = false; 
    if (this.mInterceptRequestLayoutDepth == 1) {
      if (paramBoolean && this.mLayoutWasDefered && !this.mLayoutFrozen && this.mLayout != null && this.mAdapter != null)
        dispatchLayout(); 
      if (!this.mLayoutFrozen)
        this.mLayoutWasDefered = false; 
    } 
    this.mInterceptRequestLayoutDepth--;
  }
  
  public void stopNestedScroll() { getScrollingChildHelper().stopNestedScroll(); }
  
  public void stopNestedScroll(int paramInt) { getScrollingChildHelper().stopNestedScroll(paramInt); }
  
  public void stopScroll() {
    setScrollState(0);
    stopScrollersInternal();
  }
  
  public void swapAdapter(Adapter paramAdapter, boolean paramBoolean) {
    setLayoutFrozen(false);
    setAdapterInternal(paramAdapter, true, paramBoolean);
    processDataSetCompletelyChanged(true);
    requestLayout();
  }
  
  void viewRangeUpdate(int paramInt1, int paramInt2, Object paramObject) {
    int i = this.mChildHelper.getUnfilteredChildCount();
    byte b;
    for (b = 0; b < i; b++) {
      View view = this.mChildHelper.getUnfilteredChildAt(b);
      ViewHolder viewHolder = getChildViewHolderInt(view);
      if (viewHolder != null && !viewHolder.shouldIgnore() && viewHolder.mPosition >= paramInt1 && viewHolder.mPosition < paramInt1 + paramInt2) {
        viewHolder.addFlags(2);
        viewHolder.addChangePayload(paramObject);
        ((LayoutParams)view.getLayoutParams()).mInsetsDirty = true;
      } 
    } 
    this.mRecycler.viewRangeUpdate(paramInt1, paramInt2);
  }
  
  public static abstract class Adapter<VH extends ViewHolder> extends Object {
    private boolean mHasStableIds = false;
    
    private final RecyclerView.AdapterDataObservable mObservable = new RecyclerView.AdapterDataObservable();
    
    public final void bindViewHolder(@NonNull VH param1VH, int param1Int) {
      param1VH.mPosition = param1Int;
      if (hasStableIds())
        param1VH.mItemId = getItemId(param1Int); 
      param1VH.setFlags(1, 519);
      TraceCompat.beginSection("RV OnBindView");
      onBindViewHolder(param1VH, param1Int, param1VH.getUnmodifiedPayloads());
      param1VH.clearPayload();
      param1VH = (VH)param1VH.itemView.getLayoutParams();
      if (param1VH instanceof RecyclerView.LayoutParams)
        ((RecyclerView.LayoutParams)param1VH).mInsetsDirty = true; 
      TraceCompat.endSection();
    }
    
    public final VH createViewHolder(@NonNull ViewGroup param1ViewGroup, int param1Int) {
      try {
        TraceCompat.beginSection("RV CreateView");
        viewHolder = onCreateViewHolder(param1ViewGroup, param1Int);
        if (viewHolder.itemView.getParent() != null)
          throw new IllegalStateException("ViewHolder views must not be attached when created. Ensure that you are not passing 'true' to the attachToRoot parameter of LayoutInflater.inflate(..., boolean attachToRoot)"); 
        viewHolder.mItemViewType = param1Int;
        return (VH)viewHolder;
      } finally {
        TraceCompat.endSection();
      } 
    }
    
    public abstract int getItemCount();
    
    public long getItemId(int param1Int) { return -1L; }
    
    public int getItemViewType(int param1Int) { return 0; }
    
    public final boolean hasObservers() { return this.mObservable.hasObservers(); }
    
    public final boolean hasStableIds() { return this.mHasStableIds; }
    
    public final void notifyDataSetChanged() { this.mObservable.notifyChanged(); }
    
    public final void notifyItemChanged(int param1Int) { this.mObservable.notifyItemRangeChanged(param1Int, 1); }
    
    public final void notifyItemChanged(int param1Int, @Nullable Object param1Object) { this.mObservable.notifyItemRangeChanged(param1Int, 1, param1Object); }
    
    public final void notifyItemInserted(int param1Int) { this.mObservable.notifyItemRangeInserted(param1Int, 1); }
    
    public final void notifyItemMoved(int param1Int1, int param1Int2) { this.mObservable.notifyItemMoved(param1Int1, param1Int2); }
    
    public final void notifyItemRangeChanged(int param1Int1, int param1Int2) { this.mObservable.notifyItemRangeChanged(param1Int1, param1Int2); }
    
    public final void notifyItemRangeChanged(int param1Int1, int param1Int2, @Nullable Object param1Object) { this.mObservable.notifyItemRangeChanged(param1Int1, param1Int2, param1Object); }
    
    public final void notifyItemRangeInserted(int param1Int1, int param1Int2) { this.mObservable.notifyItemRangeInserted(param1Int1, param1Int2); }
    
    public final void notifyItemRangeRemoved(int param1Int1, int param1Int2) { this.mObservable.notifyItemRangeRemoved(param1Int1, param1Int2); }
    
    public final void notifyItemRemoved(int param1Int) { this.mObservable.notifyItemRangeRemoved(param1Int, 1); }
    
    public void onAttachedToRecyclerView(@NonNull RecyclerView param1RecyclerView) {}
    
    public abstract void onBindViewHolder(@NonNull VH param1VH, int param1Int);
    
    public void onBindViewHolder(@NonNull VH param1VH, int param1Int, @NonNull List<Object> param1List) { onBindViewHolder(param1VH, param1Int); }
    
    @NonNull
    public abstract VH onCreateViewHolder(@NonNull ViewGroup param1ViewGroup, int param1Int);
    
    public void onDetachedFromRecyclerView(@NonNull RecyclerView param1RecyclerView) {}
    
    public boolean onFailedToRecycleView(@NonNull VH param1VH) { return false; }
    
    public void onViewAttachedToWindow(@NonNull VH param1VH) {}
    
    public void onViewDetachedFromWindow(@NonNull VH param1VH) {}
    
    public void onViewRecycled(@NonNull VH param1VH) {}
    
    public void registerAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver param1AdapterDataObserver) { this.mObservable.registerObserver(param1AdapterDataObserver); }
    
    public void setHasStableIds(boolean param1Boolean) {
      if (hasObservers())
        throw new IllegalStateException("Cannot change whether this adapter has stable IDs while the adapter has registered observers."); 
      this.mHasStableIds = param1Boolean;
    }
    
    public void unregisterAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver param1AdapterDataObserver) { this.mObservable.unregisterObserver(param1AdapterDataObserver); }
  }
  
  static class AdapterDataObservable extends Observable<AdapterDataObserver> {
    public boolean hasObservers() { return this.mObservers.isEmpty() ^ true; }
    
    public void notifyChanged() {
      for (int i = this.mObservers.size() - 1; i >= 0; i--)
        ((RecyclerView.AdapterDataObserver)this.mObservers.get(i)).onChanged(); 
    }
    
    public void notifyItemMoved(int param1Int1, int param1Int2) {
      for (int i = this.mObservers.size() - 1; i >= 0; i--)
        ((RecyclerView.AdapterDataObserver)this.mObservers.get(i)).onItemRangeMoved(param1Int1, param1Int2, 1); 
    }
    
    public void notifyItemRangeChanged(int param1Int1, int param1Int2) { notifyItemRangeChanged(param1Int1, param1Int2, null); }
    
    public void notifyItemRangeChanged(int param1Int1, int param1Int2, @Nullable Object param1Object) {
      int i;
      for (i = this.mObservers.size() - 1; i >= 0; i--)
        ((RecyclerView.AdapterDataObserver)this.mObservers.get(i)).onItemRangeChanged(param1Int1, param1Int2, param1Object); 
    }
    
    public void notifyItemRangeInserted(int param1Int1, int param1Int2) {
      for (int i = this.mObservers.size() - 1; i >= 0; i--)
        ((RecyclerView.AdapterDataObserver)this.mObservers.get(i)).onItemRangeInserted(param1Int1, param1Int2); 
    }
    
    public void notifyItemRangeRemoved(int param1Int1, int param1Int2) {
      for (int i = this.mObservers.size() - 1; i >= 0; i--)
        ((RecyclerView.AdapterDataObserver)this.mObservers.get(i)).onItemRangeRemoved(param1Int1, param1Int2); 
    }
  }
  
  public static abstract class AdapterDataObserver {
    public void onChanged() {}
    
    public void onItemRangeChanged(int param1Int1, int param1Int2) {}
    
    public void onItemRangeChanged(int param1Int1, int param1Int2, @Nullable Object param1Object) { onItemRangeChanged(param1Int1, param1Int2); }
    
    public void onItemRangeInserted(int param1Int1, int param1Int2) {}
    
    public void onItemRangeMoved(int param1Int1, int param1Int2, int param1Int3) {}
    
    public void onItemRangeRemoved(int param1Int1, int param1Int2) {}
  }
  
  public static interface ChildDrawingOrderCallback {
    int onGetChildDrawingOrder(int param1Int1, int param1Int2);
  }
  
  public static class EdgeEffectFactory {
    public static final int DIRECTION_BOTTOM = 3;
    
    public static final int DIRECTION_LEFT = 0;
    
    public static final int DIRECTION_RIGHT = 2;
    
    public static final int DIRECTION_TOP = 1;
    
    @NonNull
    protected EdgeEffect createEdgeEffect(RecyclerView param1RecyclerView, int param1Int) { return new EdgeEffect(param1RecyclerView.getContext()); }
    
    @Retention(RetentionPolicy.SOURCE)
    public static @interface EdgeDirection {}
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface EdgeDirection {}
  
  public static abstract class ItemAnimator {
    public static final int FLAG_APPEARED_IN_PRE_LAYOUT = 4096;
    
    public static final int FLAG_CHANGED = 2;
    
    public static final int FLAG_INVALIDATED = 4;
    
    public static final int FLAG_MOVED = 2048;
    
    public static final int FLAG_REMOVED = 8;
    
    private long mAddDuration = 120L;
    
    private long mChangeDuration = 250L;
    
    private ArrayList<ItemAnimatorFinishedListener> mFinishedListeners = new ArrayList();
    
    private ItemAnimatorListener mListener = null;
    
    private long mMoveDuration = 250L;
    
    private long mRemoveDuration = 120L;
    
    static int buildAdapterChangeFlagsForAnimations(RecyclerView.ViewHolder param1ViewHolder) {
      int j = param1ViewHolder.mFlags & 0xE;
      if (param1ViewHolder.isInvalid())
        return 4; 
      int i = j;
      if ((j & 0x4) == 0) {
        int k = param1ViewHolder.getOldPosition();
        int m = param1ViewHolder.getAdapterPosition();
        i = j;
        if (k != -1) {
          i = j;
          if (m != -1) {
            i = j;
            if (k != m)
              i = j | 0x800; 
          } 
        } 
      } 
      return i;
    }
    
    public abstract boolean animateAppearance(@NonNull RecyclerView.ViewHolder param1ViewHolder, @Nullable ItemHolderInfo param1ItemHolderInfo1, @NonNull ItemHolderInfo param1ItemHolderInfo2);
    
    public abstract boolean animateChange(@NonNull RecyclerView.ViewHolder param1ViewHolder1, @NonNull RecyclerView.ViewHolder param1ViewHolder2, @NonNull ItemHolderInfo param1ItemHolderInfo1, @NonNull ItemHolderInfo param1ItemHolderInfo2);
    
    public abstract boolean animateDisappearance(@NonNull RecyclerView.ViewHolder param1ViewHolder, @NonNull ItemHolderInfo param1ItemHolderInfo1, @Nullable ItemHolderInfo param1ItemHolderInfo2);
    
    public abstract boolean animatePersistence(@NonNull RecyclerView.ViewHolder param1ViewHolder, @NonNull ItemHolderInfo param1ItemHolderInfo1, @NonNull ItemHolderInfo param1ItemHolderInfo2);
    
    public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder param1ViewHolder) { return true; }
    
    public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder param1ViewHolder, @NonNull List<Object> param1List) { return canReuseUpdatedViewHolder(param1ViewHolder); }
    
    public final void dispatchAnimationFinished(RecyclerView.ViewHolder param1ViewHolder) {
      onAnimationFinished(param1ViewHolder);
      if (this.mListener != null)
        this.mListener.onAnimationFinished(param1ViewHolder); 
    }
    
    public final void dispatchAnimationStarted(RecyclerView.ViewHolder param1ViewHolder) { onAnimationStarted(param1ViewHolder); }
    
    public final void dispatchAnimationsFinished() {
      int i = this.mFinishedListeners.size();
      for (byte b = 0; b < i; b++)
        ((ItemAnimatorFinishedListener)this.mFinishedListeners.get(b)).onAnimationsFinished(); 
      this.mFinishedListeners.clear();
    }
    
    public abstract void endAnimation(RecyclerView.ViewHolder param1ViewHolder);
    
    public abstract void endAnimations();
    
    public long getAddDuration() { return this.mAddDuration; }
    
    public long getChangeDuration() { return this.mChangeDuration; }
    
    public long getMoveDuration() { return this.mMoveDuration; }
    
    public long getRemoveDuration() { return this.mRemoveDuration; }
    
    public abstract boolean isRunning();
    
    public final boolean isRunning(ItemAnimatorFinishedListener param1ItemAnimatorFinishedListener) {
      boolean bool = isRunning();
      if (param1ItemAnimatorFinishedListener != null) {
        if (!bool) {
          param1ItemAnimatorFinishedListener.onAnimationsFinished();
          return bool;
        } 
        this.mFinishedListeners.add(param1ItemAnimatorFinishedListener);
      } 
      return bool;
    }
    
    public ItemHolderInfo obtainHolderInfo() { return new ItemHolderInfo(); }
    
    public void onAnimationFinished(RecyclerView.ViewHolder param1ViewHolder) {}
    
    public void onAnimationStarted(RecyclerView.ViewHolder param1ViewHolder) {}
    
    @NonNull
    public ItemHolderInfo recordPostLayoutInformation(@NonNull RecyclerView.State param1State, @NonNull RecyclerView.ViewHolder param1ViewHolder) { return obtainHolderInfo().setFrom(param1ViewHolder); }
    
    @NonNull
    public ItemHolderInfo recordPreLayoutInformation(@NonNull RecyclerView.State param1State, @NonNull RecyclerView.ViewHolder param1ViewHolder, int param1Int, @NonNull List<Object> param1List) { return obtainHolderInfo().setFrom(param1ViewHolder); }
    
    public abstract void runPendingAnimations();
    
    public void setAddDuration(long param1Long) { this.mAddDuration = param1Long; }
    
    public void setChangeDuration(long param1Long) { this.mChangeDuration = param1Long; }
    
    void setListener(ItemAnimatorListener param1ItemAnimatorListener) { this.mListener = param1ItemAnimatorListener; }
    
    public void setMoveDuration(long param1Long) { this.mMoveDuration = param1Long; }
    
    public void setRemoveDuration(long param1Long) { this.mRemoveDuration = param1Long; }
    
    @Retention(RetentionPolicy.SOURCE)
    public static @interface AdapterChanges {}
    
    public static interface ItemAnimatorFinishedListener {
      void onAnimationsFinished();
    }
    
    static interface ItemAnimatorListener {
      void onAnimationFinished(RecyclerView.ViewHolder param2ViewHolder);
    }
    
    public static class ItemHolderInfo {
      public int bottom;
      
      public int changeFlags;
      
      public int left;
      
      public int right;
      
      public int top;
      
      public ItemHolderInfo setFrom(RecyclerView.ViewHolder param2ViewHolder) { return setFrom(param2ViewHolder, 0); }
      
      public ItemHolderInfo setFrom(RecyclerView.ViewHolder param2ViewHolder, int param2Int) {
        View view = param2ViewHolder.itemView;
        this.left = view.getLeft();
        this.top = view.getTop();
        this.right = view.getRight();
        this.bottom = view.getBottom();
        return this;
      }
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface AdapterChanges {}
  
  public static interface ItemAnimatorFinishedListener {
    void onAnimationsFinished();
  }
  
  static interface ItemAnimatorListener {
    void onAnimationFinished(RecyclerView.ViewHolder param1ViewHolder);
  }
  
  public static class ItemHolderInfo {
    public int bottom;
    
    public int changeFlags;
    
    public int left;
    
    public int right;
    
    public int top;
    
    public ItemHolderInfo setFrom(RecyclerView.ViewHolder param1ViewHolder) { return setFrom(param1ViewHolder, 0); }
    
    public ItemHolderInfo setFrom(RecyclerView.ViewHolder param1ViewHolder, int param1Int) {
      View view = param1ViewHolder.itemView;
      this.left = view.getLeft();
      this.top = view.getTop();
      this.right = view.getRight();
      this.bottom = view.getBottom();
      return this;
    }
  }
  
  private class ItemAnimatorRestoreListener implements ItemAnimator.ItemAnimatorListener {
    public void onAnimationFinished(RecyclerView.ViewHolder param1ViewHolder) {
      param1ViewHolder.setIsRecyclable(true);
      if (param1ViewHolder.mShadowedHolder != null && param1ViewHolder.mShadowingHolder == null)
        param1ViewHolder.mShadowedHolder = null; 
      if (!param1ViewHolder.shouldBeKeptAsChild() && !RecyclerView.this.removeAnimatingView(param1ViewHolder.itemView) && param1ViewHolder.isTmpDetached())
        RecyclerView.this.removeDetachedView(param1ViewHolder.itemView, false); 
    }
  }
  
  public static abstract class ItemDecoration {
    @Deprecated
    public void getItemOffsets(Rect param1Rect, int param1Int, RecyclerView param1RecyclerView) { param1Rect.set(0, 0, 0, 0); }
    
    public void getItemOffsets(Rect param1Rect, View param1View, RecyclerView param1RecyclerView, RecyclerView.State param1State) { getItemOffsets(param1Rect, ((RecyclerView.LayoutParams)param1View.getLayoutParams()).getViewLayoutPosition(), param1RecyclerView); }
    
    @Deprecated
    public void onDraw(Canvas param1Canvas, RecyclerView param1RecyclerView) {}
    
    public void onDraw(Canvas param1Canvas, RecyclerView param1RecyclerView, RecyclerView.State param1State) { onDraw(param1Canvas, param1RecyclerView); }
    
    @Deprecated
    public void onDrawOver(Canvas param1Canvas, RecyclerView param1RecyclerView) {}
    
    public void onDrawOver(Canvas param1Canvas, RecyclerView param1RecyclerView, RecyclerView.State param1State) { onDrawOver(param1Canvas, param1RecyclerView); }
  }
  
  public static abstract class LayoutManager {
    boolean mAutoMeasure = false;
    
    ChildHelper mChildHelper;
    
    private int mHeight;
    
    private int mHeightMode;
    
    ViewBoundsCheck mHorizontalBoundCheck = new ViewBoundsCheck(this.mHorizontalBoundCheckCallback);
    
    private final ViewBoundsCheck.Callback mHorizontalBoundCheckCallback = new ViewBoundsCheck.Callback() {
        public View getChildAt(int param2Int) { return RecyclerView.LayoutManager.this.getChildAt(param2Int); }
        
        public int getChildCount() { return RecyclerView.LayoutManager.this.getChildCount(); }
        
        public int getChildEnd(View param2View) {
          RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)param2View.getLayoutParams();
          return RecyclerView.LayoutManager.this.getDecoratedRight(param2View) + layoutParams.rightMargin;
        }
        
        public int getChildStart(View param2View) {
          RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)param2View.getLayoutParams();
          return RecyclerView.LayoutManager.this.getDecoratedLeft(param2View) - layoutParams.leftMargin;
        }
        
        public View getParent() { return RecyclerView.LayoutManager.this.mRecyclerView; }
        
        public int getParentEnd() { return RecyclerView.LayoutManager.this.getWidth() - RecyclerView.LayoutManager.this.getPaddingRight(); }
        
        public int getParentStart() { return RecyclerView.LayoutManager.this.getPaddingLeft(); }
      };
    
    boolean mIsAttachedToWindow = false;
    
    private boolean mItemPrefetchEnabled = true;
    
    private boolean mMeasurementCacheEnabled = true;
    
    int mPrefetchMaxCountObserved;
    
    boolean mPrefetchMaxObservedInInitialPrefetch;
    
    RecyclerView mRecyclerView;
    
    boolean mRequestedSimpleAnimations = false;
    
    @Nullable
    RecyclerView.SmoothScroller mSmoothScroller;
    
    ViewBoundsCheck mVerticalBoundCheck = new ViewBoundsCheck(this.mVerticalBoundCheckCallback);
    
    private final ViewBoundsCheck.Callback mVerticalBoundCheckCallback = new ViewBoundsCheck.Callback() {
        public View getChildAt(int param2Int) { return RecyclerView.LayoutManager.this.getChildAt(param2Int); }
        
        public int getChildCount() { return RecyclerView.LayoutManager.this.getChildCount(); }
        
        public int getChildEnd(View param2View) {
          RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)param2View.getLayoutParams();
          return RecyclerView.LayoutManager.this.getDecoratedBottom(param2View) + layoutParams.bottomMargin;
        }
        
        public int getChildStart(View param2View) {
          RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)param2View.getLayoutParams();
          return RecyclerView.LayoutManager.this.getDecoratedTop(param2View) - layoutParams.topMargin;
        }
        
        public View getParent() { return RecyclerView.LayoutManager.this.mRecyclerView; }
        
        public int getParentEnd() { return RecyclerView.LayoutManager.this.getHeight() - RecyclerView.LayoutManager.this.getPaddingBottom(); }
        
        public int getParentStart() { return RecyclerView.LayoutManager.this.getPaddingTop(); }
      };
    
    private int mWidth;
    
    private int mWidthMode;
    
    private void addViewInt(View param1View, int param1Int, boolean param1Boolean) {
      StringBuilder stringBuilder = RecyclerView.getChildViewHolderInt(param1View);
      if (param1Boolean || stringBuilder.isRemoved()) {
        this.mRecyclerView.mViewInfoStore.addToDisappearedInLayout(stringBuilder);
      } else {
        this.mRecyclerView.mViewInfoStore.removeFromDisappearedInLayout(stringBuilder);
      } 
      RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)param1View.getLayoutParams();
      if (stringBuilder.wasReturnedFromScrap() || stringBuilder.isScrap()) {
        if (stringBuilder.isScrap()) {
          stringBuilder.unScrap();
        } else {
          stringBuilder.clearReturnedFromScrapFlag();
        } 
        this.mChildHelper.attachViewToParent(param1View, param1Int, param1View.getLayoutParams(), false);
      } else if (param1View.getParent() == this.mRecyclerView) {
        int j = this.mChildHelper.indexOfChild(param1View);
        int i = param1Int;
        if (param1Int == -1)
          i = this.mChildHelper.getChildCount(); 
        if (j == -1) {
          stringBuilder = new StringBuilder();
          stringBuilder.append("Added View has RecyclerView as parent but view is not a real child. Unfiltered index:");
          stringBuilder.append(this.mRecyclerView.indexOfChild(param1View));
          stringBuilder.append(this.mRecyclerView.exceptionLabel());
          throw new IllegalStateException(stringBuilder.toString());
        } 
        if (j != i)
          this.mRecyclerView.mLayout.moveView(j, i); 
      } else {
        this.mChildHelper.addView(param1View, param1Int, false);
        layoutParams.mInsetsDirty = true;
        if (this.mSmoothScroller != null && this.mSmoothScroller.isRunning())
          this.mSmoothScroller.onChildAttachedToWindow(param1View); 
      } 
      if (layoutParams.mPendingInvalidate) {
        stringBuilder.itemView.invalidate();
        layoutParams.mPendingInvalidate = false;
      } 
    }
    
    public static int chooseSize(int param1Int1, int param1Int2, int param1Int3) {
      int i = View.MeasureSpec.getMode(param1Int1);
      param1Int1 = View.MeasureSpec.getSize(param1Int1);
      return (i != Integer.MIN_VALUE) ? ((i != 1073741824) ? Math.max(param1Int2, param1Int3) : param1Int1) : Math.min(param1Int1, Math.max(param1Int2, param1Int3));
    }
    
    private void detachViewInternal(int param1Int, View param1View) { this.mChildHelper.detachViewFromParent(param1Int); }
    
    public static int getChildMeasureSpec(int param1Int1, int param1Int2, int param1Int3, int param1Int4, boolean param1Boolean) { // Byte code:
      //   0: iconst_0
      //   1: istore #6
      //   3: iconst_0
      //   4: iload_0
      //   5: iload_2
      //   6: isub
      //   7: invokestatic max : (II)I
      //   10: istore #5
      //   12: iload #4
      //   14: ifeq -> 67
      //   17: iload_3
      //   18: iflt -> 29
      //   21: iload_3
      //   22: istore_2
      //   23: ldc 1073741824
      //   25: istore_0
      //   26: goto -> 125
      //   29: iload_3
      //   30: iconst_m1
      //   31: if_icmpne -> 120
      //   34: iload_1
      //   35: ldc -2147483648
      //   37: if_icmpeq -> 57
      //   40: iload_1
      //   41: ifeq -> 50
      //   44: iload_1
      //   45: ldc 1073741824
      //   47: if_icmpeq -> 57
      //   50: iconst_0
      //   51: istore_1
      //   52: iconst_0
      //   53: istore_0
      //   54: goto -> 60
      //   57: iload #5
      //   59: istore_0
      //   60: iload_0
      //   61: istore_2
      //   62: iload_1
      //   63: istore_0
      //   64: goto -> 125
      //   67: iload_3
      //   68: iflt -> 74
      //   71: goto -> 21
      //   74: iload_3
      //   75: iconst_m1
      //   76: if_icmpne -> 87
      //   79: iload_1
      //   80: istore_0
      //   81: iload #5
      //   83: istore_2
      //   84: goto -> 125
      //   87: iload_3
      //   88: bipush #-2
      //   90: if_icmpne -> 120
      //   93: iload_1
      //   94: ldc -2147483648
      //   96: if_icmpeq -> 111
      //   99: iload #5
      //   101: istore_2
      //   102: iload #6
      //   104: istore_0
      //   105: iload_1
      //   106: ldc 1073741824
      //   108: if_icmpne -> 125
      //   111: ldc -2147483648
      //   113: istore_0
      //   114: iload #5
      //   116: istore_2
      //   117: goto -> 125
      //   120: iconst_0
      //   121: istore_2
      //   122: iload #6
      //   124: istore_0
      //   125: iload_2
      //   126: iload_0
      //   127: invokestatic makeMeasureSpec : (II)I
      //   130: ireturn }
    
    @Deprecated
    public static int getChildMeasureSpec(int param1Int1, int param1Int2, int param1Int3, boolean param1Boolean) { // Byte code:
      //   0: iconst_0
      //   1: istore #4
      //   3: iconst_0
      //   4: iload_0
      //   5: iload_1
      //   6: isub
      //   7: invokestatic max : (II)I
      //   10: istore_0
      //   11: iload_3
      //   12: ifeq -> 35
      //   15: iload_2
      //   16: iflt -> 27
      //   19: iload_2
      //   20: istore_0
      //   21: ldc 1073741824
      //   23: istore_1
      //   24: goto -> 59
      //   27: iconst_0
      //   28: istore_0
      //   29: iload #4
      //   31: istore_1
      //   32: goto -> 59
      //   35: iload_2
      //   36: iflt -> 42
      //   39: goto -> 19
      //   42: iload_2
      //   43: iconst_m1
      //   44: if_icmpne -> 50
      //   47: goto -> 21
      //   50: iload_2
      //   51: bipush #-2
      //   53: if_icmpne -> 27
      //   56: ldc -2147483648
      //   58: istore_1
      //   59: iload_0
      //   60: iload_1
      //   61: invokestatic makeMeasureSpec : (II)I
      //   64: ireturn }
    
    private int[] getChildRectangleOnScreenScrollAmount(RecyclerView param1RecyclerView, View param1View, Rect param1Rect, boolean param1Boolean) {
      int i = getPaddingLeft();
      int j = getPaddingTop();
      int k = getWidth();
      int i5 = getPaddingRight();
      int n = getHeight();
      int i1 = getPaddingBottom();
      int i6 = param1View.getLeft() + param1Rect.left - param1View.getScrollX();
      int i2 = param1View.getTop() + param1Rect.top - param1View.getScrollY();
      int i7 = param1Rect.width();
      int i3 = param1Rect.height();
      int i4 = i6 - i;
      i = Math.min(0, i4);
      int m = i2 - j;
      j = Math.min(0, m);
      i5 = i7 + i6 - k - i5;
      k = Math.max(0, i5);
      n = Math.max(0, i3 + i2 - n - i1);
      if (getLayoutDirection() == 1) {
        if (k != 0) {
          i = k;
        } else {
          i = Math.max(i, i5);
        } 
      } else if (i == 0) {
        i = Math.min(i4, k);
      } 
      if (j == 0)
        j = Math.min(m, n); 
      return new int[] { i, j };
    }
    
    public static Properties getProperties(Context param1Context, AttributeSet param1AttributeSet, int param1Int1, int param1Int2) {
      Properties properties = new Properties();
      TypedArray typedArray = param1Context.obtainStyledAttributes(param1AttributeSet, R.styleable.RecyclerView, param1Int1, param1Int2);
      properties.orientation = typedArray.getInt(R.styleable.RecyclerView_android_orientation, 1);
      properties.spanCount = typedArray.getInt(R.styleable.RecyclerView_spanCount, 1);
      properties.reverseLayout = typedArray.getBoolean(R.styleable.RecyclerView_reverseLayout, false);
      properties.stackFromEnd = typedArray.getBoolean(R.styleable.RecyclerView_stackFromEnd, false);
      typedArray.recycle();
      return properties;
    }
    
    private boolean isFocusedChildVisibleAfterScrolling(RecyclerView param1RecyclerView, int param1Int1, int param1Int2) {
      View view = param1RecyclerView.getFocusedChild();
      if (view == null)
        return false; 
      int i = getPaddingLeft();
      int j = getPaddingTop();
      int k = getWidth();
      int m = getPaddingRight();
      int n = getHeight();
      int i1 = getPaddingBottom();
      Rect rect = this.mRecyclerView.mTempRect;
      getDecoratedBoundsWithMargins(view, rect);
      return (rect.left - param1Int1 < k - m && rect.right - param1Int1 > i && rect.top - param1Int2 < n - i1) ? (!(rect.bottom - param1Int2 <= j)) : false;
    }
    
    private static boolean isMeasurementUpToDate(int param1Int1, int param1Int2, int param1Int3) {
      int i = View.MeasureSpec.getMode(param1Int2);
      param1Int2 = View.MeasureSpec.getSize(param1Int2);
      boolean bool1 = false;
      boolean bool = false;
      if (param1Int3 > 0 && param1Int1 != param1Int3)
        return false; 
      if (i != Integer.MIN_VALUE) {
        if (i != 0) {
          if (i != 1073741824)
            return false; 
          if (param1Int2 == param1Int1)
            bool = true; 
          return bool;
        } 
        return true;
      } 
      bool = bool1;
      if (param1Int2 >= param1Int1)
        bool = true; 
      return bool;
    }
    
    private void onSmoothScrollerStopped(RecyclerView.SmoothScroller param1SmoothScroller) {
      if (this.mSmoothScroller == param1SmoothScroller)
        this.mSmoothScroller = null; 
    }
    
    private void scrapOrRecycleView(RecyclerView.Recycler param1Recycler, int param1Int, View param1View) {
      RecyclerView.ViewHolder viewHolder = RecyclerView.getChildViewHolderInt(param1View);
      if (viewHolder.shouldIgnore())
        return; 
      if (viewHolder.isInvalid() && !viewHolder.isRemoved() && !this.mRecyclerView.mAdapter.hasStableIds()) {
        removeViewAt(param1Int);
        param1Recycler.recycleViewHolderInternal(viewHolder);
        return;
      } 
      detachViewAt(param1Int);
      param1Recycler.scrapView(param1View);
      this.mRecyclerView.mViewInfoStore.onViewDetached(viewHolder);
    }
    
    public void addDisappearingView(View param1View) { addDisappearingView(param1View, -1); }
    
    public void addDisappearingView(View param1View, int param1Int) { addViewInt(param1View, param1Int, true); }
    
    public void addView(View param1View) { addView(param1View, -1); }
    
    public void addView(View param1View, int param1Int) { addViewInt(param1View, param1Int, false); }
    
    public void assertInLayoutOrScroll(String param1String) {
      if (this.mRecyclerView != null)
        this.mRecyclerView.assertInLayoutOrScroll(param1String); 
    }
    
    public void assertNotInLayoutOrScroll(String param1String) {
      if (this.mRecyclerView != null)
        this.mRecyclerView.assertNotInLayoutOrScroll(param1String); 
    }
    
    public void attachView(View param1View) { attachView(param1View, -1); }
    
    public void attachView(View param1View, int param1Int) { attachView(param1View, param1Int, (RecyclerView.LayoutParams)param1View.getLayoutParams()); }
    
    public void attachView(View param1View, int param1Int, RecyclerView.LayoutParams param1LayoutParams) {
      RecyclerView.ViewHolder viewHolder = RecyclerView.getChildViewHolderInt(param1View);
      if (viewHolder.isRemoved()) {
        this.mRecyclerView.mViewInfoStore.addToDisappearedInLayout(viewHolder);
      } else {
        this.mRecyclerView.mViewInfoStore.removeFromDisappearedInLayout(viewHolder);
      } 
      this.mChildHelper.attachViewToParent(param1View, param1Int, param1LayoutParams, viewHolder.isRemoved());
    }
    
    public void calculateItemDecorationsForChild(View param1View, Rect param1Rect) {
      if (this.mRecyclerView == null) {
        param1Rect.set(0, 0, 0, 0);
        return;
      } 
      param1Rect.set(this.mRecyclerView.getItemDecorInsetsForChild(param1View));
    }
    
    public boolean canScrollHorizontally() { return false; }
    
    public boolean canScrollVertically() { return false; }
    
    public boolean checkLayoutParams(RecyclerView.LayoutParams param1LayoutParams) { return (param1LayoutParams != null); }
    
    public void collectAdjacentPrefetchPositions(int param1Int1, int param1Int2, RecyclerView.State param1State, LayoutPrefetchRegistry param1LayoutPrefetchRegistry) {}
    
    public void collectInitialPrefetchPositions(int param1Int, LayoutPrefetchRegistry param1LayoutPrefetchRegistry) {}
    
    public int computeHorizontalScrollExtent(RecyclerView.State param1State) { return 0; }
    
    public int computeHorizontalScrollOffset(RecyclerView.State param1State) { return 0; }
    
    public int computeHorizontalScrollRange(RecyclerView.State param1State) { return 0; }
    
    public int computeVerticalScrollExtent(RecyclerView.State param1State) { return 0; }
    
    public int computeVerticalScrollOffset(RecyclerView.State param1State) { return 0; }
    
    public int computeVerticalScrollRange(RecyclerView.State param1State) { return 0; }
    
    public void detachAndScrapAttachedViews(RecyclerView.Recycler param1Recycler) {
      for (int i = getChildCount() - 1; i >= 0; i--)
        scrapOrRecycleView(param1Recycler, i, getChildAt(i)); 
    }
    
    public void detachAndScrapView(View param1View, RecyclerView.Recycler param1Recycler) { scrapOrRecycleView(param1Recycler, this.mChildHelper.indexOfChild(param1View), param1View); }
    
    public void detachAndScrapViewAt(int param1Int, RecyclerView.Recycler param1Recycler) { scrapOrRecycleView(param1Recycler, param1Int, getChildAt(param1Int)); }
    
    public void detachView(View param1View) {
      int i = this.mChildHelper.indexOfChild(param1View);
      if (i >= 0)
        detachViewInternal(i, param1View); 
    }
    
    public void detachViewAt(int param1Int) { detachViewInternal(param1Int, getChildAt(param1Int)); }
    
    void dispatchAttachedToWindow(RecyclerView param1RecyclerView) {
      this.mIsAttachedToWindow = true;
      onAttachedToWindow(param1RecyclerView);
    }
    
    void dispatchDetachedFromWindow(RecyclerView param1RecyclerView, RecyclerView.Recycler param1Recycler) {
      this.mIsAttachedToWindow = false;
      onDetachedFromWindow(param1RecyclerView, param1Recycler);
    }
    
    public void endAnimation(View param1View) {
      if (this.mRecyclerView.mItemAnimator != null)
        this.mRecyclerView.mItemAnimator.endAnimation(RecyclerView.getChildViewHolderInt(param1View)); 
    }
    
    @Nullable
    public View findContainingItemView(View param1View) {
      if (this.mRecyclerView == null)
        return null; 
      param1View = this.mRecyclerView.findContainingItemView(param1View);
      return (param1View == null) ? null : (this.mChildHelper.isHidden(param1View) ? null : param1View);
    }
    
    public View findViewByPosition(int param1Int) {
      int i = getChildCount();
      for (byte b = 0; b < i; b++) {
        View view = getChildAt(b);
        RecyclerView.ViewHolder viewHolder = RecyclerView.getChildViewHolderInt(view);
        if (viewHolder != null && viewHolder.getLayoutPosition() == param1Int && !viewHolder.shouldIgnore() && (this.mRecyclerView.mState.isPreLayout() || !viewHolder.isRemoved()))
          return view; 
      } 
      return null;
    }
    
    public abstract RecyclerView.LayoutParams generateDefaultLayoutParams();
    
    public RecyclerView.LayoutParams generateLayoutParams(Context param1Context, AttributeSet param1AttributeSet) { return new RecyclerView.LayoutParams(param1Context, param1AttributeSet); }
    
    public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams param1LayoutParams) { return (param1LayoutParams instanceof RecyclerView.LayoutParams) ? new RecyclerView.LayoutParams((RecyclerView.LayoutParams)param1LayoutParams) : ((param1LayoutParams instanceof ViewGroup.MarginLayoutParams) ? new RecyclerView.LayoutParams((ViewGroup.MarginLayoutParams)param1LayoutParams) : new RecyclerView.LayoutParams(param1LayoutParams)); }
    
    public int getBaseline() { return -1; }
    
    public int getBottomDecorationHeight(View param1View) { return ((RecyclerView.LayoutParams)param1View.getLayoutParams()).mDecorInsets.bottom; }
    
    public View getChildAt(int param1Int) { return (this.mChildHelper != null) ? this.mChildHelper.getChildAt(param1Int) : null; }
    
    public int getChildCount() { return (this.mChildHelper != null) ? this.mChildHelper.getChildCount() : 0; }
    
    public boolean getClipToPadding() { return (this.mRecyclerView != null && this.mRecyclerView.mClipToPadding); }
    
    public int getColumnCountForAccessibility(RecyclerView.Recycler param1Recycler, RecyclerView.State param1State) {
      RecyclerView recyclerView = this.mRecyclerView;
      int i = 1;
      if (recyclerView != null) {
        if (this.mRecyclerView.mAdapter == null)
          return 1; 
        if (canScrollHorizontally())
          i = this.mRecyclerView.mAdapter.getItemCount(); 
        return i;
      } 
      return 1;
    }
    
    public int getDecoratedBottom(View param1View) { return param1View.getBottom() + getBottomDecorationHeight(param1View); }
    
    public void getDecoratedBoundsWithMargins(View param1View, Rect param1Rect) { RecyclerView.getDecoratedBoundsWithMarginsInt(param1View, param1Rect); }
    
    public int getDecoratedLeft(View param1View) { return param1View.getLeft() - getLeftDecorationWidth(param1View); }
    
    public int getDecoratedMeasuredHeight(View param1View) {
      Rect rect = ((RecyclerView.LayoutParams)param1View.getLayoutParams()).mDecorInsets;
      return param1View.getMeasuredHeight() + rect.top + rect.bottom;
    }
    
    public int getDecoratedMeasuredWidth(View param1View) {
      Rect rect = ((RecyclerView.LayoutParams)param1View.getLayoutParams()).mDecorInsets;
      return param1View.getMeasuredWidth() + rect.left + rect.right;
    }
    
    public int getDecoratedRight(View param1View) { return param1View.getRight() + getRightDecorationWidth(param1View); }
    
    public int getDecoratedTop(View param1View) { return param1View.getTop() - getTopDecorationHeight(param1View); }
    
    public View getFocusedChild() {
      if (this.mRecyclerView == null)
        return null; 
      View view = this.mRecyclerView.getFocusedChild();
      return (view != null) ? (this.mChildHelper.isHidden(view) ? null : view) : null;
    }
    
    public int getHeight() { return this.mHeight; }
    
    public int getHeightMode() { return this.mHeightMode; }
    
    public int getItemCount() {
      Object object;
      if (this.mRecyclerView != null) {
        object = this.mRecyclerView.getAdapter();
      } else {
        object = null;
      } 
      return (object != null) ? object.getItemCount() : 0;
    }
    
    public int getItemViewType(View param1View) { return RecyclerView.getChildViewHolderInt(param1View).getItemViewType(); }
    
    public int getLayoutDirection() { return ViewCompat.getLayoutDirection(this.mRecyclerView); }
    
    public int getLeftDecorationWidth(View param1View) { return ((RecyclerView.LayoutParams)param1View.getLayoutParams()).mDecorInsets.left; }
    
    public int getMinimumHeight() { return ViewCompat.getMinimumHeight(this.mRecyclerView); }
    
    public int getMinimumWidth() { return ViewCompat.getMinimumWidth(this.mRecyclerView); }
    
    public int getPaddingBottom() { return (this.mRecyclerView != null) ? this.mRecyclerView.getPaddingBottom() : 0; }
    
    public int getPaddingEnd() { return (this.mRecyclerView != null) ? ViewCompat.getPaddingEnd(this.mRecyclerView) : 0; }
    
    public int getPaddingLeft() { return (this.mRecyclerView != null) ? this.mRecyclerView.getPaddingLeft() : 0; }
    
    public int getPaddingRight() { return (this.mRecyclerView != null) ? this.mRecyclerView.getPaddingRight() : 0; }
    
    public int getPaddingStart() { return (this.mRecyclerView != null) ? ViewCompat.getPaddingStart(this.mRecyclerView) : 0; }
    
    public int getPaddingTop() { return (this.mRecyclerView != null) ? this.mRecyclerView.getPaddingTop() : 0; }
    
    public int getPosition(View param1View) { return ((RecyclerView.LayoutParams)param1View.getLayoutParams()).getViewLayoutPosition(); }
    
    public int getRightDecorationWidth(View param1View) { return ((RecyclerView.LayoutParams)param1View.getLayoutParams()).mDecorInsets.right; }
    
    public int getRowCountForAccessibility(RecyclerView.Recycler param1Recycler, RecyclerView.State param1State) {
      RecyclerView recyclerView = this.mRecyclerView;
      int i = 1;
      if (recyclerView != null) {
        if (this.mRecyclerView.mAdapter == null)
          return 1; 
        if (canScrollVertically())
          i = this.mRecyclerView.mAdapter.getItemCount(); 
        return i;
      } 
      return 1;
    }
    
    public int getSelectionModeForAccessibility(RecyclerView.Recycler param1Recycler, RecyclerView.State param1State) { return 0; }
    
    public int getTopDecorationHeight(View param1View) { return ((RecyclerView.LayoutParams)param1View.getLayoutParams()).mDecorInsets.top; }
    
    public void getTransformedBoundingBox(View param1View, boolean param1Boolean, Rect param1Rect) {
      if (param1Boolean) {
        Rect rect = ((RecyclerView.LayoutParams)param1View.getLayoutParams()).mDecorInsets;
        param1Rect.set(-rect.left, -rect.top, param1View.getWidth() + rect.right, param1View.getHeight() + rect.bottom);
      } else {
        param1Rect.set(0, 0, param1View.getWidth(), param1View.getHeight());
      } 
      if (this.mRecyclerView != null) {
        Matrix matrix = param1View.getMatrix();
        if (matrix != null && !matrix.isIdentity()) {
          RectF rectF = this.mRecyclerView.mTempRectF;
          rectF.set(param1Rect);
          matrix.mapRect(rectF);
          param1Rect.set((int)Math.floor(rectF.left), (int)Math.floor(rectF.top), (int)Math.ceil(rectF.right), (int)Math.ceil(rectF.bottom));
        } 
      } 
      param1Rect.offset(param1View.getLeft(), param1View.getTop());
    }
    
    public int getWidth() { return this.mWidth; }
    
    public int getWidthMode() { return this.mWidthMode; }
    
    boolean hasFlexibleChildInBothOrientations() {
      int i = getChildCount();
      for (byte b = 0; b < i; b++) {
        ViewGroup.LayoutParams layoutParams = getChildAt(b).getLayoutParams();
        if (layoutParams.width < 0 && layoutParams.height < 0)
          return true; 
      } 
      return false;
    }
    
    public boolean hasFocus() { return (this.mRecyclerView != null && this.mRecyclerView.hasFocus()); }
    
    public void ignoreView(View param1View) {
      StringBuilder stringBuilder;
      if (param1View.getParent() != this.mRecyclerView || this.mRecyclerView.indexOfChild(param1View) == -1) {
        stringBuilder = new StringBuilder();
        stringBuilder.append("View should be fully attached to be ignored");
        stringBuilder.append(this.mRecyclerView.exceptionLabel());
        throw new IllegalArgumentException(stringBuilder.toString());
      } 
      RecyclerView.ViewHolder viewHolder = RecyclerView.getChildViewHolderInt(stringBuilder);
      viewHolder.addFlags(128);
      this.mRecyclerView.mViewInfoStore.removeViewHolder(viewHolder);
    }
    
    public boolean isAttachedToWindow() { return this.mIsAttachedToWindow; }
    
    public boolean isAutoMeasureEnabled() { return this.mAutoMeasure; }
    
    public boolean isFocused() { return (this.mRecyclerView != null && this.mRecyclerView.isFocused()); }
    
    public final boolean isItemPrefetchEnabled() { return this.mItemPrefetchEnabled; }
    
    public boolean isLayoutHierarchical(RecyclerView.Recycler param1Recycler, RecyclerView.State param1State) { return false; }
    
    public boolean isMeasurementCacheEnabled() { return this.mMeasurementCacheEnabled; }
    
    public boolean isSmoothScrolling() { return (this.mSmoothScroller != null && this.mSmoothScroller.isRunning()); }
    
    public boolean isViewPartiallyVisible(@NonNull View param1View, boolean param1Boolean1, boolean param1Boolean2) {
      if (this.mHorizontalBoundCheck.isViewWithinBoundFlags(param1View, 24579) && this.mVerticalBoundCheck.isViewWithinBoundFlags(param1View, 24579)) {
        param1Boolean2 = true;
      } else {
        param1Boolean2 = false;
      } 
      return param1Boolean1 ? param1Boolean2 : (param1Boolean2 ^ true);
    }
    
    public void layoutDecorated(View param1View, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      Rect rect = ((RecyclerView.LayoutParams)param1View.getLayoutParams()).mDecorInsets;
      param1View.layout(param1Int1 + rect.left, param1Int2 + rect.top, param1Int3 - rect.right, param1Int4 - rect.bottom);
    }
    
    public void layoutDecoratedWithMargins(View param1View, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)param1View.getLayoutParams();
      Rect rect = layoutParams.mDecorInsets;
      param1View.layout(param1Int1 + rect.left + layoutParams.leftMargin, param1Int2 + rect.top + layoutParams.topMargin, param1Int3 - rect.right - layoutParams.rightMargin, param1Int4 - rect.bottom - layoutParams.bottomMargin);
    }
    
    public void measureChild(View param1View, int param1Int1, int param1Int2) {
      RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)param1View.getLayoutParams();
      Rect rect = this.mRecyclerView.getItemDecorInsetsForChild(param1View);
      int k = rect.left;
      int m = rect.right;
      int i = rect.top;
      int j = rect.bottom;
      param1Int1 = getChildMeasureSpec(getWidth(), getWidthMode(), getPaddingLeft() + getPaddingRight() + param1Int1 + k + m, layoutParams.width, canScrollHorizontally());
      param1Int2 = getChildMeasureSpec(getHeight(), getHeightMode(), getPaddingTop() + getPaddingBottom() + param1Int2 + i + j, layoutParams.height, canScrollVertically());
      if (shouldMeasureChild(param1View, param1Int1, param1Int2, layoutParams))
        param1View.measure(param1Int1, param1Int2); 
    }
    
    public void measureChildWithMargins(View param1View, int param1Int1, int param1Int2) {
      RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)param1View.getLayoutParams();
      Rect rect = this.mRecyclerView.getItemDecorInsetsForChild(param1View);
      int k = rect.left;
      int m = rect.right;
      int i = rect.top;
      int j = rect.bottom;
      param1Int1 = getChildMeasureSpec(getWidth(), getWidthMode(), getPaddingLeft() + getPaddingRight() + layoutParams.leftMargin + layoutParams.rightMargin + param1Int1 + k + m, layoutParams.width, canScrollHorizontally());
      param1Int2 = getChildMeasureSpec(getHeight(), getHeightMode(), getPaddingTop() + getPaddingBottom() + layoutParams.topMargin + layoutParams.bottomMargin + param1Int2 + i + j, layoutParams.height, canScrollVertically());
      if (shouldMeasureChild(param1View, param1Int1, param1Int2, layoutParams))
        param1View.measure(param1Int1, param1Int2); 
    }
    
    public void moveView(int param1Int1, int param1Int2) {
      StringBuilder stringBuilder = getChildAt(param1Int1);
      if (stringBuilder == null) {
        stringBuilder = new StringBuilder();
        stringBuilder.append("Cannot move a child from non-existing index:");
        stringBuilder.append(param1Int1);
        stringBuilder.append(this.mRecyclerView.toString());
        throw new IllegalArgumentException(stringBuilder.toString());
      } 
      detachViewAt(param1Int1);
      attachView(stringBuilder, param1Int2);
    }
    
    public void offsetChildrenHorizontal(int param1Int) {
      if (this.mRecyclerView != null)
        this.mRecyclerView.offsetChildrenHorizontal(param1Int); 
    }
    
    public void offsetChildrenVertical(int param1Int) {
      if (this.mRecyclerView != null)
        this.mRecyclerView.offsetChildrenVertical(param1Int); 
    }
    
    public void onAdapterChanged(RecyclerView.Adapter param1Adapter1, RecyclerView.Adapter param1Adapter2) {}
    
    public boolean onAddFocusables(RecyclerView param1RecyclerView, ArrayList<View> param1ArrayList, int param1Int1, int param1Int2) { return false; }
    
    @CallSuper
    public void onAttachedToWindow(RecyclerView param1RecyclerView) {}
    
    @Deprecated
    public void onDetachedFromWindow(RecyclerView param1RecyclerView) {}
    
    @CallSuper
    public void onDetachedFromWindow(RecyclerView param1RecyclerView, RecyclerView.Recycler param1Recycler) { onDetachedFromWindow(param1RecyclerView); }
    
    @Nullable
    public View onFocusSearchFailed(View param1View, int param1Int, RecyclerView.Recycler param1Recycler, RecyclerView.State param1State) { return null; }
    
    public void onInitializeAccessibilityEvent(RecyclerView.Recycler param1Recycler, RecyclerView.State param1State, AccessibilityEvent param1AccessibilityEvent) {
      if (this.mRecyclerView != null) {
        if (param1AccessibilityEvent == null)
          return; 
        RecyclerView recyclerView = this.mRecyclerView;
        byte b = 1;
        int i = b;
        if (!recyclerView.canScrollVertically(1)) {
          i = b;
          if (!this.mRecyclerView.canScrollVertically(-1)) {
            i = b;
            if (!this.mRecyclerView.canScrollHorizontally(-1))
              if (this.mRecyclerView.canScrollHorizontally(1)) {
                i = b;
              } else {
                i = 0;
              }  
          } 
        } 
        param1AccessibilityEvent.setScrollable(i);
        if (this.mRecyclerView.mAdapter != null)
          param1AccessibilityEvent.setItemCount(this.mRecyclerView.mAdapter.getItemCount()); 
        return;
      } 
    }
    
    public void onInitializeAccessibilityEvent(AccessibilityEvent param1AccessibilityEvent) { onInitializeAccessibilityEvent(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, param1AccessibilityEvent); }
    
    void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfoCompat param1AccessibilityNodeInfoCompat) { onInitializeAccessibilityNodeInfo(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, param1AccessibilityNodeInfoCompat); }
    
    public void onInitializeAccessibilityNodeInfo(RecyclerView.Recycler param1Recycler, RecyclerView.State param1State, AccessibilityNodeInfoCompat param1AccessibilityNodeInfoCompat) {
      if (this.mRecyclerView.canScrollVertically(-1) || this.mRecyclerView.canScrollHorizontally(-1)) {
        param1AccessibilityNodeInfoCompat.addAction(8192);
        param1AccessibilityNodeInfoCompat.setScrollable(true);
      } 
      if (this.mRecyclerView.canScrollVertically(1) || this.mRecyclerView.canScrollHorizontally(1)) {
        param1AccessibilityNodeInfoCompat.addAction(4096);
        param1AccessibilityNodeInfoCompat.setScrollable(true);
      } 
      param1AccessibilityNodeInfoCompat.setCollectionInfo(AccessibilityNodeInfoCompat.CollectionInfoCompat.obtain(getRowCountForAccessibility(param1Recycler, param1State), getColumnCountForAccessibility(param1Recycler, param1State), isLayoutHierarchical(param1Recycler, param1State), getSelectionModeForAccessibility(param1Recycler, param1State)));
    }
    
    public void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler param1Recycler, RecyclerView.State param1State, View param1View, AccessibilityNodeInfoCompat param1AccessibilityNodeInfoCompat) {
      byte b2;
      byte b1;
      if (canScrollVertically()) {
        b1 = getPosition(param1View);
      } else {
        b1 = 0;
      } 
      if (canScrollHorizontally()) {
        b2 = getPosition(param1View);
      } else {
        b2 = 0;
      } 
      param1AccessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(b1, 1, b2, 1, false, false));
    }
    
    void onInitializeAccessibilityNodeInfoForItem(View param1View, AccessibilityNodeInfoCompat param1AccessibilityNodeInfoCompat) {
      RecyclerView.ViewHolder viewHolder = RecyclerView.getChildViewHolderInt(param1View);
      if (viewHolder != null && !viewHolder.isRemoved() && !this.mChildHelper.isHidden(viewHolder.itemView))
        onInitializeAccessibilityNodeInfoForItem(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, param1View, param1AccessibilityNodeInfoCompat); 
    }
    
    public View onInterceptFocusSearch(View param1View, int param1Int) { return null; }
    
    public void onItemsAdded(RecyclerView param1RecyclerView, int param1Int1, int param1Int2) {}
    
    public void onItemsChanged(RecyclerView param1RecyclerView) {}
    
    public void onItemsMoved(RecyclerView param1RecyclerView, int param1Int1, int param1Int2, int param1Int3) {}
    
    public void onItemsRemoved(RecyclerView param1RecyclerView, int param1Int1, int param1Int2) {}
    
    public void onItemsUpdated(RecyclerView param1RecyclerView, int param1Int1, int param1Int2) {}
    
    public void onItemsUpdated(RecyclerView param1RecyclerView, int param1Int1, int param1Int2, Object param1Object) { onItemsUpdated(param1RecyclerView, param1Int1, param1Int2); }
    
    public void onLayoutChildren(RecyclerView.Recycler param1Recycler, RecyclerView.State param1State) { Log.e("RecyclerView", "You must override onLayoutChildren(Recycler recycler, State state) "); }
    
    public void onLayoutCompleted(RecyclerView.State param1State) {}
    
    public void onMeasure(RecyclerView.Recycler param1Recycler, RecyclerView.State param1State, int param1Int1, int param1Int2) { this.mRecyclerView.defaultOnMeasure(param1Int1, param1Int2); }
    
    public boolean onRequestChildFocus(RecyclerView param1RecyclerView, RecyclerView.State param1State, View param1View1, View param1View2) { return onRequestChildFocus(param1RecyclerView, param1View1, param1View2); }
    
    @Deprecated
    public boolean onRequestChildFocus(RecyclerView param1RecyclerView, View param1View1, View param1View2) { return (isSmoothScrolling() || param1RecyclerView.isComputingLayout()); }
    
    public void onRestoreInstanceState(Parcelable param1Parcelable) {}
    
    public Parcelable onSaveInstanceState() { return null; }
    
    public void onScrollStateChanged(int param1Int) {}
    
    boolean performAccessibilityAction(int param1Int, Bundle param1Bundle) { return performAccessibilityAction(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, param1Int, param1Bundle); }
    
    public boolean performAccessibilityAction(RecyclerView.Recycler param1Recycler, RecyclerView.State param1State, int param1Int, Bundle param1Bundle) { // Byte code:
      //   0: aload_0
      //   1: getfield mRecyclerView : Landroid/support/v7/widget/RecyclerView;
      //   4: ifnonnull -> 9
      //   7: iconst_0
      //   8: ireturn
      //   9: iload_3
      //   10: sipush #4096
      //   13: if_icmpeq -> 102
      //   16: iload_3
      //   17: sipush #8192
      //   20: if_icmpeq -> 31
      //   23: iconst_0
      //   24: istore_3
      //   25: iconst_0
      //   26: istore #6
      //   28: goto -> 168
      //   31: aload_0
      //   32: getfield mRecyclerView : Landroid/support/v7/widget/RecyclerView;
      //   35: iconst_m1
      //   36: invokevirtual canScrollVertically : (I)Z
      //   39: ifeq -> 62
      //   42: aload_0
      //   43: invokevirtual getHeight : ()I
      //   46: aload_0
      //   47: invokevirtual getPaddingTop : ()I
      //   50: isub
      //   51: aload_0
      //   52: invokevirtual getPaddingBottom : ()I
      //   55: isub
      //   56: ineg
      //   57: istore #5
      //   59: goto -> 65
      //   62: iconst_0
      //   63: istore #5
      //   65: iload #5
      //   67: istore_3
      //   68: aload_0
      //   69: getfield mRecyclerView : Landroid/support/v7/widget/RecyclerView;
      //   72: iconst_m1
      //   73: invokevirtual canScrollHorizontally : (I)Z
      //   76: ifeq -> 25
      //   79: aload_0
      //   80: invokevirtual getWidth : ()I
      //   83: aload_0
      //   84: invokevirtual getPaddingLeft : ()I
      //   87: isub
      //   88: aload_0
      //   89: invokevirtual getPaddingRight : ()I
      //   92: isub
      //   93: ineg
      //   94: istore #6
      //   96: iload #5
      //   98: istore_3
      //   99: goto -> 168
      //   102: aload_0
      //   103: getfield mRecyclerView : Landroid/support/v7/widget/RecyclerView;
      //   106: iconst_1
      //   107: invokevirtual canScrollVertically : (I)Z
      //   110: ifeq -> 132
      //   113: aload_0
      //   114: invokevirtual getHeight : ()I
      //   117: aload_0
      //   118: invokevirtual getPaddingTop : ()I
      //   121: isub
      //   122: aload_0
      //   123: invokevirtual getPaddingBottom : ()I
      //   126: isub
      //   127: istore #5
      //   129: goto -> 135
      //   132: iconst_0
      //   133: istore #5
      //   135: iload #5
      //   137: istore_3
      //   138: aload_0
      //   139: getfield mRecyclerView : Landroid/support/v7/widget/RecyclerView;
      //   142: iconst_1
      //   143: invokevirtual canScrollHorizontally : (I)Z
      //   146: ifeq -> 25
      //   149: aload_0
      //   150: invokevirtual getWidth : ()I
      //   153: aload_0
      //   154: invokevirtual getPaddingLeft : ()I
      //   157: isub
      //   158: aload_0
      //   159: invokevirtual getPaddingRight : ()I
      //   162: isub
      //   163: istore #6
      //   165: iload #5
      //   167: istore_3
      //   168: iload_3
      //   169: ifne -> 179
      //   172: iload #6
      //   174: ifne -> 179
      //   177: iconst_0
      //   178: ireturn
      //   179: aload_0
      //   180: getfield mRecyclerView : Landroid/support/v7/widget/RecyclerView;
      //   183: iload #6
      //   185: iload_3
      //   186: invokevirtual scrollBy : (II)V
      //   189: iconst_1
      //   190: ireturn }
    
    public boolean performAccessibilityActionForItem(RecyclerView.Recycler param1Recycler, RecyclerView.State param1State, View param1View, int param1Int, Bundle param1Bundle) { return false; }
    
    boolean performAccessibilityActionForItem(View param1View, int param1Int, Bundle param1Bundle) { return performAccessibilityActionForItem(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, param1View, param1Int, param1Bundle); }
    
    public void postOnAnimation(Runnable param1Runnable) {
      if (this.mRecyclerView != null)
        ViewCompat.postOnAnimation(this.mRecyclerView, param1Runnable); 
    }
    
    public void removeAllViews() {
      for (int i = getChildCount() - 1; i >= 0; i--)
        this.mChildHelper.removeViewAt(i); 
    }
    
    public void removeAndRecycleAllViews(RecyclerView.Recycler param1Recycler) {
      for (int i = getChildCount() - 1; i >= 0; i--) {
        if (!RecyclerView.getChildViewHolderInt(getChildAt(i)).shouldIgnore())
          removeAndRecycleViewAt(i, param1Recycler); 
      } 
    }
    
    void removeAndRecycleScrapInt(RecyclerView.Recycler param1Recycler) {
      int j = param1Recycler.getScrapCount();
      for (int i = j - 1; i >= 0; i--) {
        View view = param1Recycler.getScrapViewAt(i);
        RecyclerView.ViewHolder viewHolder = RecyclerView.getChildViewHolderInt(view);
        if (!viewHolder.shouldIgnore()) {
          viewHolder.setIsRecyclable(false);
          if (viewHolder.isTmpDetached())
            this.mRecyclerView.removeDetachedView(view, false); 
          if (this.mRecyclerView.mItemAnimator != null)
            this.mRecyclerView.mItemAnimator.endAnimation(viewHolder); 
          viewHolder.setIsRecyclable(true);
          param1Recycler.quickRecycleScrapView(view);
        } 
      } 
      param1Recycler.clearScrap();
      if (j > 0)
        this.mRecyclerView.invalidate(); 
    }
    
    public void removeAndRecycleView(View param1View, RecyclerView.Recycler param1Recycler) {
      removeView(param1View);
      param1Recycler.recycleView(param1View);
    }
    
    public void removeAndRecycleViewAt(int param1Int, RecyclerView.Recycler param1Recycler) {
      View view = getChildAt(param1Int);
      removeViewAt(param1Int);
      param1Recycler.recycleView(view);
    }
    
    public boolean removeCallbacks(Runnable param1Runnable) { return (this.mRecyclerView != null) ? this.mRecyclerView.removeCallbacks(param1Runnable) : 0; }
    
    public void removeDetachedView(View param1View) { this.mRecyclerView.removeDetachedView(param1View, false); }
    
    public void removeView(View param1View) { this.mChildHelper.removeView(param1View); }
    
    public void removeViewAt(int param1Int) {
      if (getChildAt(param1Int) != null)
        this.mChildHelper.removeViewAt(param1Int); 
    }
    
    public boolean requestChildRectangleOnScreen(RecyclerView param1RecyclerView, View param1View, Rect param1Rect, boolean param1Boolean) { return requestChildRectangleOnScreen(param1RecyclerView, param1View, param1Rect, param1Boolean, false); }
    
    public boolean requestChildRectangleOnScreen(RecyclerView param1RecyclerView, View param1View, Rect param1Rect, boolean param1Boolean1, boolean param1Boolean2) {
      int[] arrayOfInt = getChildRectangleOnScreenScrollAmount(param1RecyclerView, param1View, param1Rect, param1Boolean1);
      int i = arrayOfInt[0];
      int j = arrayOfInt[1];
      if ((!param1Boolean2 || isFocusedChildVisibleAfterScrolling(param1RecyclerView, i, j)) && (i != 0 || j != 0)) {
        if (param1Boolean1) {
          param1RecyclerView.scrollBy(i, j);
          return true;
        } 
        param1RecyclerView.smoothScrollBy(i, j);
        return true;
      } 
      return false;
    }
    
    public void requestLayout() {
      if (this.mRecyclerView != null)
        this.mRecyclerView.requestLayout(); 
    }
    
    public void requestSimpleAnimationsInNextLayout() { this.mRequestedSimpleAnimations = true; }
    
    public int scrollHorizontallyBy(int param1Int, RecyclerView.Recycler param1Recycler, RecyclerView.State param1State) { return 0; }
    
    public void scrollToPosition(int param1Int) {}
    
    public int scrollVerticallyBy(int param1Int, RecyclerView.Recycler param1Recycler, RecyclerView.State param1State) { return 0; }
    
    @Deprecated
    public void setAutoMeasureEnabled(boolean param1Boolean) { this.mAutoMeasure = param1Boolean; }
    
    void setExactMeasureSpecsFrom(RecyclerView param1RecyclerView) { setMeasureSpecs(View.MeasureSpec.makeMeasureSpec(param1RecyclerView.getWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(param1RecyclerView.getHeight(), 1073741824)); }
    
    public final void setItemPrefetchEnabled(boolean param1Boolean) {
      if (param1Boolean != this.mItemPrefetchEnabled) {
        this.mItemPrefetchEnabled = param1Boolean;
        this.mPrefetchMaxCountObserved = 0;
        if (this.mRecyclerView != null)
          this.mRecyclerView.mRecycler.updateViewCacheSize(); 
      } 
    }
    
    void setMeasureSpecs(int param1Int1, int param1Int2) {
      this.mWidth = View.MeasureSpec.getSize(param1Int1);
      this.mWidthMode = View.MeasureSpec.getMode(param1Int1);
      if (this.mWidthMode == 0 && !RecyclerView.ALLOW_SIZE_IN_UNSPECIFIED_SPEC)
        this.mWidth = 0; 
      this.mHeight = View.MeasureSpec.getSize(param1Int2);
      this.mHeightMode = View.MeasureSpec.getMode(param1Int2);
      if (this.mHeightMode == 0 && !RecyclerView.ALLOW_SIZE_IN_UNSPECIFIED_SPEC)
        this.mHeight = 0; 
    }
    
    public void setMeasuredDimension(int param1Int1, int param1Int2) { this.mRecyclerView.setMeasuredDimension(param1Int1, param1Int2); }
    
    public void setMeasuredDimension(Rect param1Rect, int param1Int1, int param1Int2) {
      int i = param1Rect.width();
      int j = getPaddingLeft();
      int k = getPaddingRight();
      int m = param1Rect.height();
      int n = getPaddingTop();
      int i1 = getPaddingBottom();
      setMeasuredDimension(chooseSize(param1Int1, i + j + k, getMinimumWidth()), chooseSize(param1Int2, m + n + i1, getMinimumHeight()));
    }
    
    void setMeasuredDimensionFromChildren(int param1Int1, int param1Int2) {
      int n = getChildCount();
      if (n == 0) {
        this.mRecyclerView.defaultOnMeasure(param1Int1, param1Int2);
        return;
      } 
      byte b = 0;
      int m = Integer.MAX_VALUE;
      int j = Integer.MAX_VALUE;
      int k = Integer.MIN_VALUE;
      int i;
      for (i = Integer.MIN_VALUE; b < n; i = i3) {
        View view = getChildAt(b);
        Rect rect = this.mRecyclerView.mTempRect;
        getDecoratedBoundsWithMargins(view, rect);
        int i1 = m;
        if (rect.left < m)
          i1 = rect.left; 
        int i2 = k;
        if (rect.right > k)
          i2 = rect.right; 
        k = j;
        if (rect.top < j)
          k = rect.top; 
        int i3 = i;
        if (rect.bottom > i)
          i3 = rect.bottom; 
        b++;
        j = k;
        m = i1;
        k = i2;
      } 
      this.mRecyclerView.mTempRect.set(m, j, k, i);
      setMeasuredDimension(this.mRecyclerView.mTempRect, param1Int1, param1Int2);
    }
    
    public void setMeasurementCacheEnabled(boolean param1Boolean) { this.mMeasurementCacheEnabled = param1Boolean; }
    
    void setRecyclerView(RecyclerView param1RecyclerView) {
      if (param1RecyclerView == null) {
        this.mRecyclerView = null;
        this.mChildHelper = null;
        this.mWidth = 0;
        this.mHeight = 0;
      } else {
        this.mRecyclerView = param1RecyclerView;
        this.mChildHelper = param1RecyclerView.mChildHelper;
        this.mWidth = param1RecyclerView.getWidth();
        this.mHeight = param1RecyclerView.getHeight();
      } 
      this.mWidthMode = 1073741824;
      this.mHeightMode = 1073741824;
    }
    
    boolean shouldMeasureChild(View param1View, int param1Int1, int param1Int2, RecyclerView.LayoutParams param1LayoutParams) { return (param1View.isLayoutRequested() || !this.mMeasurementCacheEnabled || !isMeasurementUpToDate(param1View.getWidth(), param1Int1, param1LayoutParams.width) || !isMeasurementUpToDate(param1View.getHeight(), param1Int2, param1LayoutParams.height)); }
    
    boolean shouldMeasureTwice() { return false; }
    
    boolean shouldReMeasureChild(View param1View, int param1Int1, int param1Int2, RecyclerView.LayoutParams param1LayoutParams) { return (!this.mMeasurementCacheEnabled || !isMeasurementUpToDate(param1View.getMeasuredWidth(), param1Int1, param1LayoutParams.width) || !isMeasurementUpToDate(param1View.getMeasuredHeight(), param1Int2, param1LayoutParams.height)); }
    
    public void smoothScrollToPosition(RecyclerView param1RecyclerView, RecyclerView.State param1State, int param1Int) { Log.e("RecyclerView", "You must override smoothScrollToPosition to support smooth scrolling"); }
    
    public void startSmoothScroll(RecyclerView.SmoothScroller param1SmoothScroller) {
      if (this.mSmoothScroller != null && param1SmoothScroller != this.mSmoothScroller && this.mSmoothScroller.isRunning())
        this.mSmoothScroller.stop(); 
      this.mSmoothScroller = param1SmoothScroller;
      this.mSmoothScroller.start(this.mRecyclerView, this);
    }
    
    public void stopIgnoringView(View param1View) {
      RecyclerView.ViewHolder viewHolder = RecyclerView.getChildViewHolderInt(param1View);
      viewHolder.stopIgnoring();
      viewHolder.resetInternal();
      viewHolder.addFlags(4);
    }
    
    void stopSmoothScroller() {
      if (this.mSmoothScroller != null)
        this.mSmoothScroller.stop(); 
    }
    
    public boolean supportsPredictiveItemAnimations() { return false; }
    
    public static interface LayoutPrefetchRegistry {
      void addPosition(int param2Int1, int param2Int2);
    }
    
    public static class Properties {
      public int orientation;
      
      public boolean reverseLayout;
      
      public int spanCount;
      
      public boolean stackFromEnd;
    }
  }
  
  class null implements ViewBoundsCheck.Callback {
    null() {}
    
    public View getChildAt(int param1Int) { return this.this$0.getChildAt(param1Int); }
    
    public int getChildCount() { return this.this$0.getChildCount(); }
    
    public int getChildEnd(View param1View) {
      RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)param1View.getLayoutParams();
      return this.this$0.getDecoratedRight(param1View) + layoutParams.rightMargin;
    }
    
    public int getChildStart(View param1View) {
      RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)param1View.getLayoutParams();
      return this.this$0.getDecoratedLeft(param1View) - layoutParams.leftMargin;
    }
    
    public View getParent() { return this.this$0.mRecyclerView; }
    
    public int getParentEnd() { return this.this$0.getWidth() - this.this$0.getPaddingRight(); }
    
    public int getParentStart() { return this.this$0.getPaddingLeft(); }
  }
  
  class null implements ViewBoundsCheck.Callback {
    null() {}
    
    public View getChildAt(int param1Int) { return this.this$0.getChildAt(param1Int); }
    
    public int getChildCount() { return this.this$0.getChildCount(); }
    
    public int getChildEnd(View param1View) {
      RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)param1View.getLayoutParams();
      return this.this$0.getDecoratedBottom(param1View) + layoutParams.bottomMargin;
    }
    
    public int getChildStart(View param1View) {
      RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)param1View.getLayoutParams();
      return this.this$0.getDecoratedTop(param1View) - layoutParams.topMargin;
    }
    
    public View getParent() { return this.this$0.mRecyclerView; }
    
    public int getParentEnd() { return this.this$0.getHeight() - this.this$0.getPaddingBottom(); }
    
    public int getParentStart() { return this.this$0.getPaddingTop(); }
  }
  
  public static interface LayoutPrefetchRegistry {
    void addPosition(int param1Int1, int param1Int2);
  }
  
  public static class Properties {
    public int orientation;
    
    public boolean reverseLayout;
    
    public int spanCount;
    
    public boolean stackFromEnd;
  }
  
  public static class LayoutParams extends ViewGroup.MarginLayoutParams {
    final Rect mDecorInsets = new Rect();
    
    boolean mInsetsDirty = true;
    
    boolean mPendingInvalidate = false;
    
    RecyclerView.ViewHolder mViewHolder;
    
    public LayoutParams(int param1Int1, int param1Int2) { super(param1Int1, param1Int2); }
    
    public LayoutParams(Context param1Context, AttributeSet param1AttributeSet) { super(param1Context, param1AttributeSet); }
    
    public LayoutParams(LayoutParams param1LayoutParams) { super(param1LayoutParams); }
    
    public LayoutParams(ViewGroup.LayoutParams param1LayoutParams) { super(param1LayoutParams); }
    
    public LayoutParams(ViewGroup.MarginLayoutParams param1MarginLayoutParams) { super(param1MarginLayoutParams); }
    
    public int getViewAdapterPosition() { return this.mViewHolder.getAdapterPosition(); }
    
    public int getViewLayoutPosition() { return this.mViewHolder.getLayoutPosition(); }
    
    @Deprecated
    public int getViewPosition() { return this.mViewHolder.getPosition(); }
    
    public boolean isItemChanged() { return this.mViewHolder.isUpdated(); }
    
    public boolean isItemRemoved() { return this.mViewHolder.isRemoved(); }
    
    public boolean isViewInvalid() { return this.mViewHolder.isInvalid(); }
    
    public boolean viewNeedsUpdate() { return this.mViewHolder.needsUpdate(); }
  }
  
  public static interface OnChildAttachStateChangeListener {
    void onChildViewAttachedToWindow(View param1View);
    
    void onChildViewDetachedFromWindow(View param1View);
  }
  
  public static abstract class OnFlingListener {
    public abstract boolean onFling(int param1Int1, int param1Int2);
  }
  
  public static interface OnItemTouchListener {
    boolean onInterceptTouchEvent(RecyclerView param1RecyclerView, MotionEvent param1MotionEvent);
    
    void onRequestDisallowInterceptTouchEvent(boolean param1Boolean);
    
    void onTouchEvent(RecyclerView param1RecyclerView, MotionEvent param1MotionEvent);
  }
  
  public static abstract class OnScrollListener {
    public void onScrollStateChanged(RecyclerView param1RecyclerView, int param1Int) {}
    
    public void onScrolled(RecyclerView param1RecyclerView, int param1Int1, int param1Int2) {}
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface Orientation {}
  
  public static class RecycledViewPool {
    private static final int DEFAULT_MAX_SCRAP = 5;
    
    private int mAttachCount = 0;
    
    SparseArray<ScrapData> mScrap = new SparseArray();
    
    private ScrapData getScrapDataForType(int param1Int) {
      ScrapData scrapData2 = (ScrapData)this.mScrap.get(param1Int);
      ScrapData scrapData1 = scrapData2;
      if (scrapData2 == null) {
        scrapData1 = new ScrapData();
        this.mScrap.put(param1Int, scrapData1);
      } 
      return scrapData1;
    }
    
    void attach(RecyclerView.Adapter param1Adapter) { this.mAttachCount++; }
    
    public void clear() {
      for (byte b = 0; b < this.mScrap.size(); b++)
        ((ScrapData)this.mScrap.valueAt(b)).mScrapHeap.clear(); 
    }
    
    void detach() { this.mAttachCount--; }
    
    void factorInBindTime(int param1Int, long param1Long) {
      ScrapData scrapData = getScrapDataForType(param1Int);
      scrapData.mBindRunningAverageNs = runningAverage(scrapData.mBindRunningAverageNs, param1Long);
    }
    
    void factorInCreateTime(int param1Int, long param1Long) {
      ScrapData scrapData = getScrapDataForType(param1Int);
      scrapData.mCreateRunningAverageNs = runningAverage(scrapData.mCreateRunningAverageNs, param1Long);
    }
    
    @Nullable
    public RecyclerView.ViewHolder getRecycledView(int param1Int) {
      ScrapData scrapData = (ScrapData)this.mScrap.get(param1Int);
      if (scrapData != null && !scrapData.mScrapHeap.isEmpty()) {
        ArrayList arrayList = scrapData.mScrapHeap;
        return (RecyclerView.ViewHolder)arrayList.remove(arrayList.size() - 1);
      } 
      return null;
    }
    
    public int getRecycledViewCount(int param1Int) { return (getScrapDataForType(param1Int)).mScrapHeap.size(); }
    
    void onAdapterChanged(RecyclerView.Adapter param1Adapter1, RecyclerView.Adapter param1Adapter2, boolean param1Boolean) {
      if (param1Adapter1 != null)
        detach(); 
      if (!param1Boolean && this.mAttachCount == 0)
        clear(); 
      if (param1Adapter2 != null)
        attach(param1Adapter2); 
    }
    
    public void putRecycledView(RecyclerView.ViewHolder param1ViewHolder) {
      int i = param1ViewHolder.getItemViewType();
      ArrayList arrayList = (getScrapDataForType(i)).mScrapHeap;
      if (((ScrapData)this.mScrap.get(i)).mMaxScrap <= arrayList.size())
        return; 
      param1ViewHolder.resetInternal();
      arrayList.add(param1ViewHolder);
    }
    
    long runningAverage(long param1Long1, long param1Long2) { return (param1Long1 == 0L) ? param1Long2 : (param1Long1 / 4L * 3L + param1Long2 / 4L); }
    
    public void setMaxRecycledViews(int param1Int1, int param1Int2) {
      ScrapData scrapData = getScrapDataForType(param1Int1);
      scrapData.mMaxScrap = param1Int2;
      ArrayList arrayList = scrapData.mScrapHeap;
      while (arrayList.size() > param1Int2)
        arrayList.remove(arrayList.size() - 1); 
    }
    
    int size() {
      byte b = 0;
      int i;
      for (i = 0; b < this.mScrap.size(); i = j) {
        ArrayList arrayList = ((ScrapData)this.mScrap.valueAt(b)).mScrapHeap;
        int j = i;
        if (arrayList != null)
          j = i + arrayList.size(); 
        b++;
      } 
      return i;
    }
    
    boolean willBindInTime(int param1Int, long param1Long1, long param1Long2) {
      long l = (getScrapDataForType(param1Int)).mBindRunningAverageNs;
      return (l == 0L || param1Long1 + l < param1Long2);
    }
    
    boolean willCreateInTime(int param1Int, long param1Long1, long param1Long2) {
      long l = (getScrapDataForType(param1Int)).mCreateRunningAverageNs;
      return (l == 0L || param1Long1 + l < param1Long2);
    }
    
    static class ScrapData {
      long mBindRunningAverageNs = 0L;
      
      long mCreateRunningAverageNs = 0L;
      
      int mMaxScrap = 5;
      
      final ArrayList<RecyclerView.ViewHolder> mScrapHeap = new ArrayList();
    }
  }
  
  static class ScrapData {
    long mBindRunningAverageNs = 0L;
    
    long mCreateRunningAverageNs = 0L;
    
    int mMaxScrap = 5;
    
    final ArrayList<RecyclerView.ViewHolder> mScrapHeap = new ArrayList();
  }
  
  public final class Recycler {
    static final int DEFAULT_CACHE_SIZE = 2;
    
    final ArrayList<RecyclerView.ViewHolder> mAttachedScrap = new ArrayList();
    
    final ArrayList<RecyclerView.ViewHolder> mCachedViews = new ArrayList();
    
    ArrayList<RecyclerView.ViewHolder> mChangedScrap = null;
    
    RecyclerView.RecycledViewPool mRecyclerPool;
    
    private int mRequestedCacheMax = 2;
    
    private final List<RecyclerView.ViewHolder> mUnmodifiableAttachedScrap = Collections.unmodifiableList(this.mAttachedScrap);
    
    private RecyclerView.ViewCacheExtension mViewCacheExtension;
    
    int mViewCacheMax = 2;
    
    private void attachAccessibilityDelegateOnBind(RecyclerView.ViewHolder param1ViewHolder) {
      if (RecyclerView.this.isAccessibilityEnabled()) {
        View view = param1ViewHolder.itemView;
        if (ViewCompat.getImportantForAccessibility(view) == 0)
          ViewCompat.setImportantForAccessibility(view, 1); 
        if (!ViewCompat.hasAccessibilityDelegate(view)) {
          param1ViewHolder.addFlags(16384);
          ViewCompat.setAccessibilityDelegate(view, RecyclerView.this.mAccessibilityDelegate.getItemDelegate());
        } 
      } 
    }
    
    private void invalidateDisplayListInt(RecyclerView.ViewHolder param1ViewHolder) {
      if (param1ViewHolder.itemView instanceof ViewGroup)
        invalidateDisplayListInt((ViewGroup)param1ViewHolder.itemView, false); 
    }
    
    private void invalidateDisplayListInt(ViewGroup param1ViewGroup, boolean param1Boolean) {
      int i;
      for (i = param1ViewGroup.getChildCount() - 1; i >= 0; i--) {
        View view = param1ViewGroup.getChildAt(i);
        if (view instanceof ViewGroup)
          invalidateDisplayListInt((ViewGroup)view, true); 
      } 
      if (!param1Boolean)
        return; 
      if (param1ViewGroup.getVisibility() == 4) {
        param1ViewGroup.setVisibility(0);
        param1ViewGroup.setVisibility(4);
        return;
      } 
      i = param1ViewGroup.getVisibility();
      param1ViewGroup.setVisibility(4);
      param1ViewGroup.setVisibility(i);
    }
    
    private boolean tryBindViewHolderByDeadline(RecyclerView.ViewHolder param1ViewHolder, int param1Int1, int param1Int2, long param1Long) {
      param1ViewHolder.mOwnerRecyclerView = RecyclerView.this;
      int i = param1ViewHolder.getItemViewType();
      long l = RecyclerView.this.getNanoTime();
      if (param1Long != Float.MAX_VALUE && !this.mRecyclerPool.willBindInTime(i, l, param1Long))
        return false; 
      RecyclerView.this.mAdapter.bindViewHolder(param1ViewHolder, param1Int1);
      param1Long = RecyclerView.this.getNanoTime();
      this.mRecyclerPool.factorInBindTime(param1ViewHolder.getItemViewType(), param1Long - l);
      attachAccessibilityDelegateOnBind(param1ViewHolder);
      if (RecyclerView.this.mState.isPreLayout())
        param1ViewHolder.mPreLayoutPosition = param1Int2; 
      return true;
    }
    
    void addViewHolderToRecycledViewPool(RecyclerView.ViewHolder param1ViewHolder, boolean param1Boolean) {
      RecyclerView.clearNestedRecyclerViewIfNotNested(param1ViewHolder);
      if (param1ViewHolder.hasAnyOfTheFlags(16384)) {
        param1ViewHolder.setFlags(0, 16384);
        ViewCompat.setAccessibilityDelegate(param1ViewHolder.itemView, null);
      } 
      if (param1Boolean)
        dispatchViewRecycled(param1ViewHolder); 
      param1ViewHolder.mOwnerRecyclerView = null;
      getRecycledViewPool().putRecycledView(param1ViewHolder);
    }
    
    public void bindViewToPosition(View param1View, int param1Int) {
      RecyclerView.ViewHolder viewHolder = RecyclerView.getChildViewHolderInt(param1View);
      if (viewHolder == null) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("The view does not have a ViewHolder. You cannot pass arbitrary views to this method, they should be created by the Adapter");
        stringBuilder.append(RecyclerView.this.exceptionLabel());
        throw new IllegalArgumentException(stringBuilder.toString());
      } 
      int i = RecyclerView.this.mAdapterHelper.findPositionOffset(param1Int);
      if (i < 0 || i >= RecyclerView.this.mAdapter.getItemCount()) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Inconsistency detected. Invalid item position ");
        stringBuilder.append(param1Int);
        stringBuilder.append("(offset:");
        stringBuilder.append(i);
        stringBuilder.append(").");
        stringBuilder.append("state:");
        stringBuilder.append(RecyclerView.this.mState.getItemCount());
        stringBuilder.append(RecyclerView.this.exceptionLabel());
        throw new IndexOutOfBoundsException(stringBuilder.toString());
      } 
      tryBindViewHolderByDeadline(viewHolder, i, param1Int, Float.MAX_VALUE);
      RecyclerView.LayoutParams layoutParams = viewHolder.itemView.getLayoutParams();
      if (layoutParams == null) {
        layoutParams = (RecyclerView.LayoutParams)RecyclerView.this.generateDefaultLayoutParams();
        viewHolder.itemView.setLayoutParams(layoutParams);
      } else if (!RecyclerView.this.checkLayoutParams(layoutParams)) {
        layoutParams = (RecyclerView.LayoutParams)RecyclerView.this.generateLayoutParams(layoutParams);
        viewHolder.itemView.setLayoutParams(layoutParams);
      } else {
        layoutParams = (RecyclerView.LayoutParams)layoutParams;
      } 
      boolean bool = true;
      layoutParams.mInsetsDirty = true;
      layoutParams.mViewHolder = viewHolder;
      if (viewHolder.itemView.getParent() != null)
        bool = false; 
      layoutParams.mPendingInvalidate = bool;
    }
    
    public void clear() {
      this.mAttachedScrap.clear();
      recycleAndClearCachedViews();
    }
    
    void clearOldPositions() {
      int i = this.mCachedViews.size();
      byte b2 = 0;
      byte b1;
      for (b1 = 0; b1 < i; b1++)
        ((RecyclerView.ViewHolder)this.mCachedViews.get(b1)).clearOldPosition(); 
      i = this.mAttachedScrap.size();
      for (b1 = 0; b1 < i; b1++)
        ((RecyclerView.ViewHolder)this.mAttachedScrap.get(b1)).clearOldPosition(); 
      if (this.mChangedScrap != null) {
        i = this.mChangedScrap.size();
        for (b1 = b2; b1 < i; b1++)
          ((RecyclerView.ViewHolder)this.mChangedScrap.get(b1)).clearOldPosition(); 
      } 
    }
    
    void clearScrap() {
      this.mAttachedScrap.clear();
      if (this.mChangedScrap != null)
        this.mChangedScrap.clear(); 
    }
    
    public int convertPreLayoutPositionToPostLayout(int param1Int) {
      if (param1Int < 0 || param1Int >= RecyclerView.this.mState.getItemCount()) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("invalid position ");
        stringBuilder.append(param1Int);
        stringBuilder.append(". State ");
        stringBuilder.append("item count is ");
        stringBuilder.append(RecyclerView.this.mState.getItemCount());
        stringBuilder.append(RecyclerView.this.exceptionLabel());
        throw new IndexOutOfBoundsException(stringBuilder.toString());
      } 
      return !RecyclerView.this.mState.isPreLayout() ? param1Int : RecyclerView.this.mAdapterHelper.findPositionOffset(param1Int);
    }
    
    void dispatchViewRecycled(RecyclerView.ViewHolder param1ViewHolder) {
      if (RecyclerView.this.mRecyclerListener != null)
        RecyclerView.this.mRecyclerListener.onViewRecycled(param1ViewHolder); 
      if (RecyclerView.this.mAdapter != null)
        RecyclerView.this.mAdapter.onViewRecycled(param1ViewHolder); 
      if (RecyclerView.this.mState != null)
        RecyclerView.this.mViewInfoStore.removeViewHolder(param1ViewHolder); 
    }
    
    RecyclerView.ViewHolder getChangedScrapViewForPosition(int param1Int) {
      if (this.mChangedScrap != null) {
        int j = this.mChangedScrap.size();
        if (j == 0)
          return null; 
        int i = 0;
        for (byte b = 0; b < j; b++) {
          RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder)this.mChangedScrap.get(b);
          if (!viewHolder.wasReturnedFromScrap() && viewHolder.getLayoutPosition() == param1Int) {
            viewHolder.addFlags(32);
            return viewHolder;
          } 
        } 
        if (RecyclerView.this.mAdapter.hasStableIds()) {
          param1Int = RecyclerView.this.mAdapterHelper.findPositionOffset(param1Int);
          if (param1Int > 0 && param1Int < RecyclerView.this.mAdapter.getItemCount()) {
            long l = RecyclerView.this.mAdapter.getItemId(param1Int);
            for (param1Int = i; param1Int < j; param1Int++) {
              RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder)this.mChangedScrap.get(param1Int);
              if (!viewHolder.wasReturnedFromScrap() && viewHolder.getItemId() == l) {
                viewHolder.addFlags(32);
                return viewHolder;
              } 
            } 
          } 
        } 
        return null;
      } 
      return null;
    }
    
    RecyclerView.RecycledViewPool getRecycledViewPool() {
      if (this.mRecyclerPool == null)
        this.mRecyclerPool = new RecyclerView.RecycledViewPool(); 
      return this.mRecyclerPool;
    }
    
    int getScrapCount() { return this.mAttachedScrap.size(); }
    
    public List<RecyclerView.ViewHolder> getScrapList() { return this.mUnmodifiableAttachedScrap; }
    
    RecyclerView.ViewHolder getScrapOrCachedViewForId(long param1Long, int param1Int, boolean param1Boolean) {
      int i;
      for (i = this.mAttachedScrap.size() - 1; i >= 0; i--) {
        RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder)this.mAttachedScrap.get(i);
        if (viewHolder.getItemId() == param1Long && !viewHolder.wasReturnedFromScrap()) {
          if (param1Int == viewHolder.getItemViewType()) {
            viewHolder.addFlags(32);
            if (viewHolder.isRemoved() && !RecyclerView.this.mState.isPreLayout())
              viewHolder.setFlags(2, 14); 
            return viewHolder;
          } 
          if (!param1Boolean) {
            this.mAttachedScrap.remove(i);
            RecyclerView.this.removeDetachedView(viewHolder.itemView, false);
            quickRecycleScrapView(viewHolder.itemView);
          } 
        } 
      } 
      for (i = this.mCachedViews.size() - 1; i >= 0; i--) {
        RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder)this.mCachedViews.get(i);
        if (viewHolder.getItemId() == param1Long) {
          if (param1Int == viewHolder.getItemViewType()) {
            if (!param1Boolean)
              this.mCachedViews.remove(i); 
            return viewHolder;
          } 
          if (!param1Boolean) {
            recycleCachedViewAt(i);
            return null;
          } 
        } 
      } 
      return null;
    }
    
    RecyclerView.ViewHolder getScrapOrHiddenOrCachedHolderForPosition(int param1Int, boolean param1Boolean) {
      int i = this.mAttachedScrap.size();
      byte b2 = 0;
      byte b1;
      for (b1 = 0; b1 < i; b1++) {
        RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder)this.mAttachedScrap.get(b1);
        if (!viewHolder.wasReturnedFromScrap() && viewHolder.getLayoutPosition() == param1Int && !viewHolder.isInvalid() && (this.this$0.mState.mInPreLayout || !viewHolder.isRemoved())) {
          viewHolder.addFlags(32);
          return viewHolder;
        } 
      } 
      if (!param1Boolean) {
        View view = RecyclerView.this.mChildHelper.findHiddenNonRemovedView(param1Int);
        if (view != null) {
          StringBuilder stringBuilder;
          RecyclerView.ViewHolder viewHolder = RecyclerView.getChildViewHolderInt(view);
          RecyclerView.this.mChildHelper.unhide(view);
          param1Int = RecyclerView.this.mChildHelper.indexOfChild(view);
          if (param1Int == -1) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("layout index should not be -1 after unhiding a view:");
            stringBuilder.append(viewHolder);
            stringBuilder.append(RecyclerView.this.exceptionLabel());
            throw new IllegalStateException(stringBuilder.toString());
          } 
          RecyclerView.this.mChildHelper.detachViewFromParent(param1Int);
          scrapView(stringBuilder);
          viewHolder.addFlags(8224);
          return viewHolder;
        } 
      } 
      i = this.mCachedViews.size();
      for (b1 = b2; b1 < i; b1++) {
        RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder)this.mCachedViews.get(b1);
        if (!viewHolder.isInvalid() && viewHolder.getLayoutPosition() == param1Int) {
          if (!param1Boolean)
            this.mCachedViews.remove(b1); 
          return viewHolder;
        } 
      } 
      return null;
    }
    
    View getScrapViewAt(int param1Int) { return ((RecyclerView.ViewHolder)this.mAttachedScrap.get(param1Int)).itemView; }
    
    public View getViewForPosition(int param1Int) { return getViewForPosition(param1Int, false); }
    
    View getViewForPosition(int param1Int, boolean param1Boolean) { return (tryGetViewHolderForPositionByDeadline(param1Int, param1Boolean, Float.MAX_VALUE)).itemView; }
    
    void markItemDecorInsetsDirty() {
      int i = this.mCachedViews.size();
      for (byte b = 0; b < i; b++) {
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)((RecyclerView.ViewHolder)this.mCachedViews.get(b)).itemView.getLayoutParams();
        if (layoutParams != null)
          layoutParams.mInsetsDirty = true; 
      } 
    }
    
    void markKnownViewsInvalid() {
      int i = this.mCachedViews.size();
      for (byte b = 0; b < i; b++) {
        RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder)this.mCachedViews.get(b);
        if (viewHolder != null) {
          viewHolder.addFlags(6);
          viewHolder.addChangePayload(null);
        } 
      } 
      if (RecyclerView.this.mAdapter == null || !RecyclerView.this.mAdapter.hasStableIds())
        recycleAndClearCachedViews(); 
    }
    
    void offsetPositionRecordsForInsert(int param1Int1, int param1Int2) {
      int i = this.mCachedViews.size();
      for (byte b = 0; b < i; b++) {
        RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder)this.mCachedViews.get(b);
        if (viewHolder != null && viewHolder.mPosition >= param1Int1)
          viewHolder.offsetPosition(param1Int2, true); 
      } 
    }
    
    void offsetPositionRecordsForMove(int param1Int1, int param1Int2) {
      byte b1;
      int j;
      int i;
      if (param1Int1 < param1Int2) {
        i = param1Int1;
        j = param1Int2;
        b1 = -1;
      } else {
        j = param1Int1;
        i = param1Int2;
        b1 = 1;
      } 
      int k = this.mCachedViews.size();
      byte b2;
      for (b2 = 0; b2 < k; b2++) {
        RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder)this.mCachedViews.get(b2);
        if (viewHolder != null && viewHolder.mPosition >= i && viewHolder.mPosition <= j)
          if (viewHolder.mPosition == param1Int1) {
            viewHolder.offsetPosition(param1Int2 - param1Int1, false);
          } else {
            viewHolder.offsetPosition(b1, false);
          }  
      } 
    }
    
    void offsetPositionRecordsForRemove(int param1Int1, int param1Int2, boolean param1Boolean) {
      int i;
      for (i = this.mCachedViews.size() - 1; i >= 0; i--) {
        RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder)this.mCachedViews.get(i);
        if (viewHolder != null)
          if (viewHolder.mPosition >= param1Int1 + param1Int2) {
            viewHolder.offsetPosition(-param1Int2, param1Boolean);
          } else if (viewHolder.mPosition >= param1Int1) {
            viewHolder.addFlags(8);
            recycleCachedViewAt(i);
          }  
      } 
    }
    
    void onAdapterChanged(RecyclerView.Adapter param1Adapter1, RecyclerView.Adapter param1Adapter2, boolean param1Boolean) {
      clear();
      getRecycledViewPool().onAdapterChanged(param1Adapter1, param1Adapter2, param1Boolean);
    }
    
    void quickRecycleScrapView(View param1View) {
      RecyclerView.ViewHolder viewHolder;
      (viewHolder = RecyclerView.getChildViewHolderInt(param1View)).access$1002(viewHolder, null);
      RecyclerView.ViewHolder.access$1102(viewHolder, false);
      viewHolder.clearReturnedFromScrapFlag();
      recycleViewHolderInternal(viewHolder);
    }
    
    void recycleAndClearCachedViews() {
      for (int i = this.mCachedViews.size() - 1; i >= 0; i--)
        recycleCachedViewAt(i); 
      this.mCachedViews.clear();
      if (ALLOW_THREAD_GAP_WORK)
        RecyclerView.this.mPrefetchRegistry.clearPrefetchPositions(); 
    }
    
    void recycleCachedViewAt(int param1Int) {
      addViewHolderToRecycledViewPool((RecyclerView.ViewHolder)this.mCachedViews.get(param1Int), true);
      this.mCachedViews.remove(param1Int);
    }
    
    public void recycleView(View param1View) {
      RecyclerView.ViewHolder viewHolder = RecyclerView.getChildViewHolderInt(param1View);
      if (viewHolder.isTmpDetached())
        RecyclerView.this.removeDetachedView(param1View, false); 
      if (viewHolder.isScrap()) {
        viewHolder.unScrap();
      } else if (viewHolder.wasReturnedFromScrap()) {
        viewHolder.clearReturnedFromScrapFlag();
      } 
      recycleViewHolderInternal(viewHolder);
    }
    
    void recycleViewHolderInternal(RecyclerView.ViewHolder param1ViewHolder) {
      int j;
      int i;
      StringBuilder stringBuilder;
      boolean bool2 = param1ViewHolder.isScrap();
      boolean bool1 = false;
      int k = 0;
      if (bool2 || param1ViewHolder.itemView.getParent() != null) {
        StringBuilder stringBuilder1 = new StringBuilder();
        stringBuilder1.append("Scrapped or attached views may not be recycled. isScrap:");
        stringBuilder1.append(param1ViewHolder.isScrap());
        stringBuilder1.append(" isAttached:");
        if (param1ViewHolder.itemView.getParent() != null)
          bool1 = true; 
        stringBuilder1.append(bool1);
        stringBuilder1.append(RecyclerView.this.exceptionLabel());
        throw new IllegalArgumentException(stringBuilder1.toString());
      } 
      if (param1ViewHolder.isTmpDetached()) {
        StringBuilder stringBuilder1 = new StringBuilder();
        stringBuilder1.append("Tmp detached view should be removed from RecyclerView before it can be recycled: ");
        stringBuilder1.append(param1ViewHolder);
        stringBuilder1.append(RecyclerView.this.exceptionLabel());
        throw new IllegalArgumentException(stringBuilder1.toString());
      } 
      if (param1ViewHolder.shouldIgnore()) {
        stringBuilder = new StringBuilder();
        stringBuilder.append("Trying to recycle an ignored view holder. You should first call stopIgnoringView(view) before calling recycle.");
        stringBuilder.append(RecyclerView.this.exceptionLabel());
        throw new IllegalArgumentException(stringBuilder.toString());
      } 
      bool1 = stringBuilder.doesTransientStatePreventRecycling();
      if (RecyclerView.this.mAdapter != null && bool1 && RecyclerView.this.mAdapter.onFailedToRecycleView(stringBuilder)) {
        i = 1;
      } else {
        i = 0;
      } 
      if (i || stringBuilder.isRecyclable()) {
        if (this.mViewCacheMax > 0 && !stringBuilder.hasAnyOfTheFlags(526)) {
          int m = this.mCachedViews.size();
          i = m;
          if (m >= this.mViewCacheMax) {
            i = m;
            if (m > 0) {
              recycleCachedViewAt(0);
              i = m - 1;
            } 
          } 
          m = i;
          if (ALLOW_THREAD_GAP_WORK) {
            m = i;
            if (i > 0) {
              m = i;
              if (!RecyclerView.this.mPrefetchRegistry.lastPrefetchIncludedPosition(stringBuilder.mPosition)) {
                while (--i >= 0) {
                  m = ((RecyclerView.ViewHolder)this.mCachedViews.get(i)).mPosition;
                  if (!RecyclerView.this.mPrefetchRegistry.lastPrefetchIncludedPosition(m))
                    break; 
                  i--;
                } 
                m = i + 1;
              } 
            } 
          } 
          this.mCachedViews.add(m, stringBuilder);
          i = 1;
        } else {
          i = 0;
        } 
        j = k;
        k = i;
        if (i == 0) {
          addViewHolderToRecycledViewPool(stringBuilder, true);
          j = 1;
          k = i;
        } 
      } else {
        i = 0;
        j = k;
        k = i;
      } 
      RecyclerView.this.mViewInfoStore.removeViewHolder(stringBuilder);
      if (k == 0 && j == 0 && bool1)
        stringBuilder.mOwnerRecyclerView = null; 
    }
    
    void recycleViewInternal(View param1View) { recycleViewHolderInternal(RecyclerView.getChildViewHolderInt(param1View)); }
    
    void scrapView(View param1View) {
      StringBuilder stringBuilder = RecyclerView.getChildViewHolderInt(param1View);
      if (stringBuilder.hasAnyOfTheFlags(12) || !stringBuilder.isUpdated() || RecyclerView.this.canReuseUpdatedViewHolder(stringBuilder)) {
        if (stringBuilder.isInvalid() && !stringBuilder.isRemoved() && !RecyclerView.this.mAdapter.hasStableIds()) {
          stringBuilder = new StringBuilder();
          stringBuilder.append("Called scrap view with an invalid view. Invalid views cannot be reused from scrap, they should rebound from recycler pool.");
          stringBuilder.append(RecyclerView.this.exceptionLabel());
          throw new IllegalArgumentException(stringBuilder.toString());
        } 
        stringBuilder.setScrapContainer(this, false);
        this.mAttachedScrap.add(stringBuilder);
        return;
      } 
      if (this.mChangedScrap == null)
        this.mChangedScrap = new ArrayList(); 
      stringBuilder.setScrapContainer(this, true);
      this.mChangedScrap.add(stringBuilder);
    }
    
    void setRecycledViewPool(RecyclerView.RecycledViewPool param1RecycledViewPool) {
      if (this.mRecyclerPool != null)
        this.mRecyclerPool.detach(); 
      this.mRecyclerPool = param1RecycledViewPool;
      if (param1RecycledViewPool != null)
        this.mRecyclerPool.attach(RecyclerView.this.getAdapter()); 
    }
    
    void setViewCacheExtension(RecyclerView.ViewCacheExtension param1ViewCacheExtension) { this.mViewCacheExtension = param1ViewCacheExtension; }
    
    public void setViewCacheSize(int param1Int) {
      this.mRequestedCacheMax = param1Int;
      updateViewCacheSize();
    }
    
    @Nullable
    RecyclerView.ViewHolder tryGetViewHolderForPositionByDeadline(int param1Int, boolean param1Boolean, long param1Long) { // Byte code:
      //   0: iload_1
      //   1: iflt -> 1073
      //   4: iload_1
      //   5: aload_0
      //   6: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   9: getfield mState : Landroid/support/v7/widget/RecyclerView$State;
      //   12: invokevirtual getItemCount : ()I
      //   15: if_icmplt -> 21
      //   18: goto -> 1073
      //   21: aload_0
      //   22: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   25: getfield mState : Landroid/support/v7/widget/RecyclerView$State;
      //   28: invokevirtual isPreLayout : ()Z
      //   31: istore #10
      //   33: iconst_1
      //   34: istore #9
      //   36: iload #10
      //   38: ifeq -> 63
      //   41: aload_0
      //   42: iload_1
      //   43: invokevirtual getChangedScrapViewForPosition : (I)Landroid/support/v7/widget/RecyclerView$ViewHolder;
      //   46: astore #16
      //   48: aload #16
      //   50: astore #15
      //   52: aload #16
      //   54: ifnull -> 66
      //   57: iconst_1
      //   58: istore #6
      //   60: goto -> 73
      //   63: aconst_null
      //   64: astore #15
      //   66: iconst_0
      //   67: istore #6
      //   69: aload #15
      //   71: astore #16
      //   73: aload #16
      //   75: astore #15
      //   77: iload #6
      //   79: istore #5
      //   81: aload #16
      //   83: ifnonnull -> 191
      //   86: aload_0
      //   87: iload_1
      //   88: iload_2
      //   89: invokevirtual getScrapOrHiddenOrCachedHolderForPosition : (IZ)Landroid/support/v7/widget/RecyclerView$ViewHolder;
      //   92: astore #16
      //   94: aload #16
      //   96: astore #15
      //   98: iload #6
      //   100: istore #5
      //   102: aload #16
      //   104: ifnull -> 191
      //   107: aload_0
      //   108: aload #16
      //   110: invokevirtual validateViewHolderForOffsetPosition : (Landroid/support/v7/widget/RecyclerView$ViewHolder;)Z
      //   113: ifne -> 184
      //   116: iload_2
      //   117: ifne -> 174
      //   120: aload #16
      //   122: iconst_4
      //   123: invokevirtual addFlags : (I)V
      //   126: aload #16
      //   128: invokevirtual isScrap : ()Z
      //   131: ifeq -> 155
      //   134: aload_0
      //   135: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   138: aload #16
      //   140: getfield itemView : Landroid/view/View;
      //   143: iconst_0
      //   144: invokevirtual removeDetachedView : (Landroid/view/View;Z)V
      //   147: aload #16
      //   149: invokevirtual unScrap : ()V
      //   152: goto -> 168
      //   155: aload #16
      //   157: invokevirtual wasReturnedFromScrap : ()Z
      //   160: ifeq -> 168
      //   163: aload #16
      //   165: invokevirtual clearReturnedFromScrapFlag : ()V
      //   168: aload_0
      //   169: aload #16
      //   171: invokevirtual recycleViewHolderInternal : (Landroid/support/v7/widget/RecyclerView$ViewHolder;)V
      //   174: aconst_null
      //   175: astore #15
      //   177: iload #6
      //   179: istore #5
      //   181: goto -> 191
      //   184: iconst_1
      //   185: istore #5
      //   187: aload #16
      //   189: astore #15
      //   191: aload #15
      //   193: astore #17
      //   195: iload #5
      //   197: istore #7
      //   199: aload #15
      //   201: ifnonnull -> 763
      //   204: aload_0
      //   205: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   208: getfield mAdapterHelper : Landroid/support/v7/widget/AdapterHelper;
      //   211: iload_1
      //   212: invokevirtual findPositionOffset : (I)I
      //   215: istore #7
      //   217: iload #7
      //   219: iflt -> 661
      //   222: iload #7
      //   224: aload_0
      //   225: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   228: getfield mAdapter : Landroid/support/v7/widget/RecyclerView$Adapter;
      //   231: invokevirtual getItemCount : ()I
      //   234: if_icmplt -> 240
      //   237: goto -> 661
      //   240: aload_0
      //   241: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   244: getfield mAdapter : Landroid/support/v7/widget/RecyclerView$Adapter;
      //   247: iload #7
      //   249: invokevirtual getItemViewType : (I)I
      //   252: istore #8
      //   254: aload #15
      //   256: astore #16
      //   258: iload #5
      //   260: istore #6
      //   262: aload_0
      //   263: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   266: getfield mAdapter : Landroid/support/v7/widget/RecyclerView$Adapter;
      //   269: invokevirtual hasStableIds : ()Z
      //   272: ifeq -> 323
      //   275: aload_0
      //   276: aload_0
      //   277: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   280: getfield mAdapter : Landroid/support/v7/widget/RecyclerView$Adapter;
      //   283: iload #7
      //   285: invokevirtual getItemId : (I)J
      //   288: iload #8
      //   290: iload_2
      //   291: invokevirtual getScrapOrCachedViewForId : (JIZ)Landroid/support/v7/widget/RecyclerView$ViewHolder;
      //   294: astore #15
      //   296: aload #15
      //   298: astore #16
      //   300: iload #5
      //   302: istore #6
      //   304: aload #15
      //   306: ifnull -> 323
      //   309: aload #15
      //   311: iload #7
      //   313: putfield mPosition : I
      //   316: iconst_1
      //   317: istore #6
      //   319: aload #15
      //   321: astore #16
      //   323: aload #16
      //   325: astore #15
      //   327: aload #16
      //   329: ifnonnull -> 481
      //   332: aload #16
      //   334: astore #15
      //   336: aload_0
      //   337: getfield mViewCacheExtension : Landroid/support/v7/widget/RecyclerView$ViewCacheExtension;
      //   340: ifnull -> 481
      //   343: aload_0
      //   344: getfield mViewCacheExtension : Landroid/support/v7/widget/RecyclerView$ViewCacheExtension;
      //   347: aload_0
      //   348: iload_1
      //   349: iload #8
      //   351: invokevirtual getViewForPositionAndType : (Landroid/support/v7/widget/RecyclerView$Recycler;II)Landroid/view/View;
      //   354: astore #17
      //   356: aload #16
      //   358: astore #15
      //   360: aload #17
      //   362: ifnull -> 481
      //   365: aload_0
      //   366: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   369: aload #17
      //   371: invokevirtual getChildViewHolder : (Landroid/view/View;)Landroid/support/v7/widget/RecyclerView$ViewHolder;
      //   374: astore #16
      //   376: aload #16
      //   378: ifnonnull -> 425
      //   381: new java/lang/StringBuilder
      //   384: dup
      //   385: invokespecial <init> : ()V
      //   388: astore #15
      //   390: aload #15
      //   392: ldc_w 'getViewForPositionAndType returned a view which does not have a ViewHolder'
      //   395: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   398: pop
      //   399: aload #15
      //   401: aload_0
      //   402: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   405: invokevirtual exceptionLabel : ()Ljava/lang/String;
      //   408: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   411: pop
      //   412: new java/lang/IllegalArgumentException
      //   415: dup
      //   416: aload #15
      //   418: invokevirtual toString : ()Ljava/lang/String;
      //   421: invokespecial <init> : (Ljava/lang/String;)V
      //   424: athrow
      //   425: aload #16
      //   427: astore #15
      //   429: aload #16
      //   431: invokevirtual shouldIgnore : ()Z
      //   434: ifeq -> 481
      //   437: new java/lang/StringBuilder
      //   440: dup
      //   441: invokespecial <init> : ()V
      //   444: astore #15
      //   446: aload #15
      //   448: ldc_w 'getViewForPositionAndType returned a view that is ignored. You must call stopIgnoring before returning this view.'
      //   451: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   454: pop
      //   455: aload #15
      //   457: aload_0
      //   458: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   461: invokevirtual exceptionLabel : ()Ljava/lang/String;
      //   464: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   467: pop
      //   468: new java/lang/IllegalArgumentException
      //   471: dup
      //   472: aload #15
      //   474: invokevirtual toString : ()Ljava/lang/String;
      //   477: invokespecial <init> : (Ljava/lang/String;)V
      //   480: athrow
      //   481: aload #15
      //   483: astore #16
      //   485: aload #15
      //   487: ifnonnull -> 535
      //   490: aload_0
      //   491: invokevirtual getRecycledViewPool : ()Landroid/support/v7/widget/RecyclerView$RecycledViewPool;
      //   494: iload #8
      //   496: invokevirtual getRecycledView : (I)Landroid/support/v7/widget/RecyclerView$ViewHolder;
      //   499: astore #15
      //   501: aload #15
      //   503: astore #16
      //   505: aload #15
      //   507: ifnull -> 535
      //   510: aload #15
      //   512: invokevirtual resetInternal : ()V
      //   515: aload #15
      //   517: astore #16
      //   519: getstatic android/support/v7/widget/RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST : Z
      //   522: ifeq -> 535
      //   525: aload_0
      //   526: aload #15
      //   528: invokespecial invalidateDisplayListInt : (Landroid/support/v7/widget/RecyclerView$ViewHolder;)V
      //   531: aload #15
      //   533: astore #16
      //   535: aload #16
      //   537: astore #17
      //   539: iload #6
      //   541: istore #7
      //   543: aload #16
      //   545: ifnonnull -> 763
      //   548: aload_0
      //   549: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   552: invokevirtual getNanoTime : ()J
      //   555: lstore #11
      //   557: lload_3
      //   558: ldc2_w 9223372036854775807
      //   561: lcmp
      //   562: ifeq -> 582
      //   565: aload_0
      //   566: getfield mRecyclerPool : Landroid/support/v7/widget/RecyclerView$RecycledViewPool;
      //   569: iload #8
      //   571: lload #11
      //   573: lload_3
      //   574: invokevirtual willCreateInTime : (IJJ)Z
      //   577: ifne -> 582
      //   580: aconst_null
      //   581: areturn
      //   582: aload_0
      //   583: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   586: getfield mAdapter : Landroid/support/v7/widget/RecyclerView$Adapter;
      //   589: aload_0
      //   590: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   593: iload #8
      //   595: invokevirtual createViewHolder : (Landroid/view/ViewGroup;I)Landroid/support/v7/widget/RecyclerView$ViewHolder;
      //   598: astore #16
      //   600: invokestatic access$800 : ()Z
      //   603: ifeq -> 635
      //   606: aload #16
      //   608: getfield itemView : Landroid/view/View;
      //   611: invokestatic findNestedRecyclerView : (Landroid/view/View;)Landroid/support/v7/widget/RecyclerView;
      //   614: astore #15
      //   616: aload #15
      //   618: ifnull -> 635
      //   621: aload #16
      //   623: new java/lang/ref/WeakReference
      //   626: dup
      //   627: aload #15
      //   629: invokespecial <init> : (Ljava/lang/Object;)V
      //   632: putfield mNestedRecyclerView : Ljava/lang/ref/WeakReference;
      //   635: aload_0
      //   636: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   639: invokevirtual getNanoTime : ()J
      //   642: lstore #13
      //   644: aload_0
      //   645: getfield mRecyclerPool : Landroid/support/v7/widget/RecyclerView$RecycledViewPool;
      //   648: iload #8
      //   650: lload #13
      //   652: lload #11
      //   654: lsub
      //   655: invokevirtual factorInCreateTime : (IJ)V
      //   658: goto -> 771
      //   661: new java/lang/StringBuilder
      //   664: dup
      //   665: invokespecial <init> : ()V
      //   668: astore #15
      //   670: aload #15
      //   672: ldc_w 'Inconsistency detected. Invalid item position '
      //   675: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   678: pop
      //   679: aload #15
      //   681: iload_1
      //   682: invokevirtual append : (I)Ljava/lang/StringBuilder;
      //   685: pop
      //   686: aload #15
      //   688: ldc_w '(offset:'
      //   691: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   694: pop
      //   695: aload #15
      //   697: iload #7
      //   699: invokevirtual append : (I)Ljava/lang/StringBuilder;
      //   702: pop
      //   703: aload #15
      //   705: ldc_w ').'
      //   708: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   711: pop
      //   712: aload #15
      //   714: ldc_w 'state:'
      //   717: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   720: pop
      //   721: aload #15
      //   723: aload_0
      //   724: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   727: getfield mState : Landroid/support/v7/widget/RecyclerView$State;
      //   730: invokevirtual getItemCount : ()I
      //   733: invokevirtual append : (I)Ljava/lang/StringBuilder;
      //   736: pop
      //   737: aload #15
      //   739: aload_0
      //   740: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   743: invokevirtual exceptionLabel : ()Ljava/lang/String;
      //   746: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   749: pop
      //   750: new java/lang/IndexOutOfBoundsException
      //   753: dup
      //   754: aload #15
      //   756: invokevirtual toString : ()Ljava/lang/String;
      //   759: invokespecial <init> : (Ljava/lang/String;)V
      //   762: athrow
      //   763: aload #17
      //   765: astore #16
      //   767: iload #7
      //   769: istore #6
      //   771: iload #6
      //   773: ifeq -> 872
      //   776: aload_0
      //   777: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   780: getfield mState : Landroid/support/v7/widget/RecyclerView$State;
      //   783: invokevirtual isPreLayout : ()Z
      //   786: ifne -> 872
      //   789: aload #16
      //   791: sipush #8192
      //   794: invokevirtual hasAnyOfTheFlags : (I)Z
      //   797: ifeq -> 872
      //   800: aload #16
      //   802: iconst_0
      //   803: sipush #8192
      //   806: invokevirtual setFlags : (II)V
      //   809: aload_0
      //   810: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   813: getfield mState : Landroid/support/v7/widget/RecyclerView$State;
      //   816: getfield mRunSimpleAnimations : Z
      //   819: ifeq -> 872
      //   822: aload #16
      //   824: invokestatic buildAdapterChangeFlagsForAnimations : (Landroid/support/v7/widget/RecyclerView$ViewHolder;)I
      //   827: istore #5
      //   829: aload_0
      //   830: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   833: getfield mItemAnimator : Landroid/support/v7/widget/RecyclerView$ItemAnimator;
      //   836: aload_0
      //   837: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   840: getfield mState : Landroid/support/v7/widget/RecyclerView$State;
      //   843: aload #16
      //   845: iload #5
      //   847: sipush #4096
      //   850: ior
      //   851: aload #16
      //   853: invokevirtual getUnmodifiedPayloads : ()Ljava/util/List;
      //   856: invokevirtual recordPreLayoutInformation : (Landroid/support/v7/widget/RecyclerView$State;Landroid/support/v7/widget/RecyclerView$ViewHolder;ILjava/util/List;)Landroid/support/v7/widget/RecyclerView$ItemAnimator$ItemHolderInfo;
      //   859: astore #15
      //   861: aload_0
      //   862: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   865: aload #16
      //   867: aload #15
      //   869: invokevirtual recordAnimationInfoIfBouncedHiddenView : (Landroid/support/v7/widget/RecyclerView$ViewHolder;Landroid/support/v7/widget/RecyclerView$ItemAnimator$ItemHolderInfo;)V
      //   872: aload_0
      //   873: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   876: getfield mState : Landroid/support/v7/widget/RecyclerView$State;
      //   879: invokevirtual isPreLayout : ()Z
      //   882: ifeq -> 902
      //   885: aload #16
      //   887: invokevirtual isBound : ()Z
      //   890: ifeq -> 902
      //   893: aload #16
      //   895: iload_1
      //   896: putfield mPreLayoutPosition : I
      //   899: goto -> 929
      //   902: aload #16
      //   904: invokevirtual isBound : ()Z
      //   907: ifeq -> 934
      //   910: aload #16
      //   912: invokevirtual needsUpdate : ()Z
      //   915: ifne -> 934
      //   918: aload #16
      //   920: invokevirtual isInvalid : ()Z
      //   923: ifeq -> 929
      //   926: goto -> 934
      //   929: iconst_0
      //   930: istore_2
      //   931: goto -> 954
      //   934: aload_0
      //   935: aload #16
      //   937: aload_0
      //   938: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   941: getfield mAdapterHelper : Landroid/support/v7/widget/AdapterHelper;
      //   944: iload_1
      //   945: invokevirtual findPositionOffset : (I)I
      //   948: iload_1
      //   949: lload_3
      //   950: invokespecial tryBindViewHolderByDeadline : (Landroid/support/v7/widget/RecyclerView$ViewHolder;IIJ)Z
      //   953: istore_2
      //   954: aload #16
      //   956: getfield itemView : Landroid/view/View;
      //   959: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
      //   962: astore #15
      //   964: aload #15
      //   966: ifnonnull -> 994
      //   969: aload_0
      //   970: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   973: invokevirtual generateDefaultLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
      //   976: checkcast android/support/v7/widget/RecyclerView$LayoutParams
      //   979: astore #15
      //   981: aload #16
      //   983: getfield itemView : Landroid/view/View;
      //   986: aload #15
      //   988: invokevirtual setLayoutParams : (Landroid/view/ViewGroup$LayoutParams;)V
      //   991: goto -> 1040
      //   994: aload_0
      //   995: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   998: aload #15
      //   1000: invokevirtual checkLayoutParams : (Landroid/view/ViewGroup$LayoutParams;)Z
      //   1003: ifne -> 1033
      //   1006: aload_0
      //   1007: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   1010: aload #15
      //   1012: invokevirtual generateLayoutParams : (Landroid/view/ViewGroup$LayoutParams;)Landroid/view/ViewGroup$LayoutParams;
      //   1015: checkcast android/support/v7/widget/RecyclerView$LayoutParams
      //   1018: astore #15
      //   1020: aload #16
      //   1022: getfield itemView : Landroid/view/View;
      //   1025: aload #15
      //   1027: invokevirtual setLayoutParams : (Landroid/view/ViewGroup$LayoutParams;)V
      //   1030: goto -> 1040
      //   1033: aload #15
      //   1035: checkcast android/support/v7/widget/RecyclerView$LayoutParams
      //   1038: astore #15
      //   1040: aload #15
      //   1042: aload #16
      //   1044: putfield mViewHolder : Landroid/support/v7/widget/RecyclerView$ViewHolder;
      //   1047: iload #6
      //   1049: ifeq -> 1062
      //   1052: iload_2
      //   1053: ifeq -> 1062
      //   1056: iload #9
      //   1058: istore_2
      //   1059: goto -> 1064
      //   1062: iconst_0
      //   1063: istore_2
      //   1064: aload #15
      //   1066: iload_2
      //   1067: putfield mPendingInvalidate : Z
      //   1070: aload #16
      //   1072: areturn
      //   1073: new java/lang/StringBuilder
      //   1076: dup
      //   1077: invokespecial <init> : ()V
      //   1080: astore #15
      //   1082: aload #15
      //   1084: ldc_w 'Invalid item position '
      //   1087: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1090: pop
      //   1091: aload #15
      //   1093: iload_1
      //   1094: invokevirtual append : (I)Ljava/lang/StringBuilder;
      //   1097: pop
      //   1098: aload #15
      //   1100: ldc_w '('
      //   1103: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1106: pop
      //   1107: aload #15
      //   1109: iload_1
      //   1110: invokevirtual append : (I)Ljava/lang/StringBuilder;
      //   1113: pop
      //   1114: aload #15
      //   1116: ldc_w '). Item count:'
      //   1119: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1122: pop
      //   1123: aload #15
      //   1125: aload_0
      //   1126: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   1129: getfield mState : Landroid/support/v7/widget/RecyclerView$State;
      //   1132: invokevirtual getItemCount : ()I
      //   1135: invokevirtual append : (I)Ljava/lang/StringBuilder;
      //   1138: pop
      //   1139: aload #15
      //   1141: aload_0
      //   1142: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   1145: invokevirtual exceptionLabel : ()Ljava/lang/String;
      //   1148: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   1151: pop
      //   1152: new java/lang/IndexOutOfBoundsException
      //   1155: dup
      //   1156: aload #15
      //   1158: invokevirtual toString : ()Ljava/lang/String;
      //   1161: invokespecial <init> : (Ljava/lang/String;)V
      //   1164: athrow }
    
    void unscrapView(RecyclerView.ViewHolder param1ViewHolder) {
      if (param1ViewHolder.mInChangeScrap) {
        this.mChangedScrap.remove(param1ViewHolder);
      } else {
        this.mAttachedScrap.remove(param1ViewHolder);
      } 
      RecyclerView.ViewHolder.access$1002(param1ViewHolder, null);
      RecyclerView.ViewHolder.access$1102(param1ViewHolder, false);
      param1ViewHolder.clearReturnedFromScrapFlag();
    }
    
    void updateViewCacheSize() {
      if (RecyclerView.this.mLayout != null) {
        i = this.this$0.mLayout.mPrefetchMaxCountObserved;
      } else {
        i = 0;
      } 
      this.mViewCacheMax = this.mRequestedCacheMax + i;
      for (int i = this.mCachedViews.size() - 1; i >= 0 && this.mCachedViews.size() > this.mViewCacheMax; i--)
        recycleCachedViewAt(i); 
    }
    
    boolean validateViewHolderForOffsetPosition(RecyclerView.ViewHolder param1ViewHolder) {
      if (param1ViewHolder.isRemoved())
        return RecyclerView.this.mState.isPreLayout(); 
      if (param1ViewHolder.mPosition < 0 || param1ViewHolder.mPosition >= RecyclerView.this.mAdapter.getItemCount()) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Inconsistency detected. Invalid view holder adapter position");
        stringBuilder.append(param1ViewHolder);
        stringBuilder.append(RecyclerView.this.exceptionLabel());
        throw new IndexOutOfBoundsException(stringBuilder.toString());
      } 
      boolean bool2 = RecyclerView.this.mState.isPreLayout();
      boolean bool1 = false;
      if (!bool2 && RecyclerView.this.mAdapter.getItemViewType(param1ViewHolder.mPosition) != param1ViewHolder.getItemViewType())
        return false; 
      if (RecyclerView.this.mAdapter.hasStableIds()) {
        if (param1ViewHolder.getItemId() == RecyclerView.this.mAdapter.getItemId(param1ViewHolder.mPosition))
          bool1 = true; 
        return bool1;
      } 
      return true;
    }
    
    void viewRangeUpdate(int param1Int1, int param1Int2) {
      for (int i = this.mCachedViews.size() - 1; i >= 0; i--) {
        RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder)this.mCachedViews.get(i);
        if (viewHolder != null) {
          int j = viewHolder.mPosition;
          if (j >= param1Int1 && j < param1Int2 + param1Int1) {
            viewHolder.addFlags(2);
            recycleCachedViewAt(i);
          } 
        } 
      } 
    }
  }
  
  public static interface RecyclerListener {
    void onViewRecycled(RecyclerView.ViewHolder param1ViewHolder);
  }
  
  private class RecyclerViewDataObserver extends AdapterDataObserver {
    public void onChanged() {
      RecyclerView.this.assertNotInLayoutOrScroll(null);
      this.this$0.mState.mStructureChanged = true;
      RecyclerView.this.processDataSetCompletelyChanged(true);
      if (!RecyclerView.this.mAdapterHelper.hasPendingUpdates())
        RecyclerView.this.requestLayout(); 
    }
    
    public void onItemRangeChanged(int param1Int1, int param1Int2, Object param1Object) {
      RecyclerView.this.assertNotInLayoutOrScroll(null);
      if (RecyclerView.this.mAdapterHelper.onItemRangeChanged(param1Int1, param1Int2, param1Object))
        triggerUpdateProcessor(); 
    }
    
    public void onItemRangeInserted(int param1Int1, int param1Int2) {
      RecyclerView.this.assertNotInLayoutOrScroll(null);
      if (RecyclerView.this.mAdapterHelper.onItemRangeInserted(param1Int1, param1Int2))
        triggerUpdateProcessor(); 
    }
    
    public void onItemRangeMoved(int param1Int1, int param1Int2, int param1Int3) {
      RecyclerView.this.assertNotInLayoutOrScroll(null);
      if (RecyclerView.this.mAdapterHelper.onItemRangeMoved(param1Int1, param1Int2, param1Int3))
        triggerUpdateProcessor(); 
    }
    
    public void onItemRangeRemoved(int param1Int1, int param1Int2) {
      RecyclerView.this.assertNotInLayoutOrScroll(null);
      if (RecyclerView.this.mAdapterHelper.onItemRangeRemoved(param1Int1, param1Int2))
        triggerUpdateProcessor(); 
    }
    
    void triggerUpdateProcessor() {
      if (RecyclerView.POST_UPDATES_ON_ANIMATION && RecyclerView.this.mHasFixedSize && RecyclerView.this.mIsAttached) {
        ViewCompat.postOnAnimation(RecyclerView.this, RecyclerView.this.mUpdateChildViewsRunnable);
        return;
      } 
      RecyclerView.this.mAdapterUpdateDuringMeasure = true;
      RecyclerView.this.requestLayout();
    }
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static class SavedState extends AbsSavedState {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() {
        public RecyclerView.SavedState createFromParcel(Parcel param2Parcel) { return new RecyclerView.SavedState(param2Parcel, null); }
        
        public RecyclerView.SavedState createFromParcel(Parcel param2Parcel, ClassLoader param2ClassLoader) { return new RecyclerView.SavedState(param2Parcel, param2ClassLoader); }
        
        public RecyclerView.SavedState[] newArray(int param2Int) { return new RecyclerView.SavedState[param2Int]; }
      };
    
    Parcelable mLayoutState;
    
    SavedState(Parcel param1Parcel, ClassLoader param1ClassLoader) {
      super(param1Parcel, param1ClassLoader);
      if (param1ClassLoader == null)
        param1ClassLoader = RecyclerView.LayoutManager.class.getClassLoader(); 
      this.mLayoutState = param1Parcel.readParcelable(param1ClassLoader);
    }
    
    SavedState(Parcelable param1Parcelable) { super(param1Parcelable); }
    
    void copyFrom(SavedState param1SavedState) { this.mLayoutState = param1SavedState.mLayoutState; }
    
    public void writeToParcel(Parcel param1Parcel, int param1Int) {
      super.writeToParcel(param1Parcel, param1Int);
      param1Parcel.writeParcelable(this.mLayoutState, 0);
    }
  }
  
  static final class null extends Object implements Parcelable.ClassLoaderCreator<SavedState> {
    public RecyclerView.SavedState createFromParcel(Parcel param1Parcel) { return new RecyclerView.SavedState(param1Parcel, null); }
    
    public RecyclerView.SavedState createFromParcel(Parcel param1Parcel, ClassLoader param1ClassLoader) { return new RecyclerView.SavedState(param1Parcel, param1ClassLoader); }
    
    public RecyclerView.SavedState[] newArray(int param1Int) { return new RecyclerView.SavedState[param1Int]; }
  }
  
  public static class SimpleOnItemTouchListener implements OnItemTouchListener {
    public boolean onInterceptTouchEvent(RecyclerView param1RecyclerView, MotionEvent param1MotionEvent) { return false; }
    
    public void onRequestDisallowInterceptTouchEvent(boolean param1Boolean) {}
    
    public void onTouchEvent(RecyclerView param1RecyclerView, MotionEvent param1MotionEvent) {}
  }
  
  public static abstract class SmoothScroller {
    private RecyclerView.LayoutManager mLayoutManager;
    
    private boolean mPendingInitialRun;
    
    private RecyclerView mRecyclerView;
    
    private final Action mRecyclingAction = new Action(0, 0);
    
    private boolean mRunning;
    
    private int mTargetPosition = -1;
    
    private View mTargetView;
    
    private void onAnimation(int param1Int1, int param1Int2) {
      RecyclerView recyclerView = this.mRecyclerView;
      if (!this.mRunning || this.mTargetPosition == -1 || recyclerView == null)
        stop(); 
      this.mPendingInitialRun = false;
      if (this.mTargetView != null)
        if (getChildPosition(this.mTargetView) == this.mTargetPosition) {
          onTargetFound(this.mTargetView, recyclerView.mState, this.mRecyclingAction);
          this.mRecyclingAction.runIfNecessary(recyclerView);
          stop();
        } else {
          Log.e("RecyclerView", "Passed over target position while smooth scrolling.");
          this.mTargetView = null;
        }  
      if (this.mRunning) {
        onSeekTargetStep(param1Int1, param1Int2, recyclerView.mState, this.mRecyclingAction);
        boolean bool = this.mRecyclingAction.hasJumpTarget();
        this.mRecyclingAction.runIfNecessary(recyclerView);
        if (bool) {
          if (this.mRunning) {
            this.mPendingInitialRun = true;
            recyclerView.mViewFlinger.postOnAnimation();
            return;
          } 
          stop();
        } 
      } 
    }
    
    public View findViewByPosition(int param1Int) { return this.mRecyclerView.mLayout.findViewByPosition(param1Int); }
    
    public int getChildCount() { return this.mRecyclerView.mLayout.getChildCount(); }
    
    public int getChildPosition(View param1View) { return this.mRecyclerView.getChildLayoutPosition(param1View); }
    
    @Nullable
    public RecyclerView.LayoutManager getLayoutManager() { return this.mLayoutManager; }
    
    public int getTargetPosition() { return this.mTargetPosition; }
    
    @Deprecated
    public void instantScrollToPosition(int param1Int) { this.mRecyclerView.scrollToPosition(param1Int); }
    
    public boolean isPendingInitialRun() { return this.mPendingInitialRun; }
    
    public boolean isRunning() { return this.mRunning; }
    
    protected void normalize(PointF param1PointF) {
      float f = (float)Math.sqrt((param1PointF.x * param1PointF.x + param1PointF.y * param1PointF.y));
      param1PointF.x /= f;
      param1PointF.y /= f;
    }
    
    protected void onChildAttachedToWindow(View param1View) {
      if (getChildPosition(param1View) == getTargetPosition())
        this.mTargetView = param1View; 
    }
    
    protected abstract void onSeekTargetStep(int param1Int1, int param1Int2, RecyclerView.State param1State, Action param1Action);
    
    protected abstract void onStart();
    
    protected abstract void onStop();
    
    protected abstract void onTargetFound(View param1View, RecyclerView.State param1State, Action param1Action);
    
    public void setTargetPosition(int param1Int) { this.mTargetPosition = param1Int; }
    
    void start(RecyclerView param1RecyclerView, RecyclerView.LayoutManager param1LayoutManager) {
      this.mRecyclerView = param1RecyclerView;
      this.mLayoutManager = param1LayoutManager;
      if (this.mTargetPosition == -1)
        throw new IllegalArgumentException("Invalid target position"); 
      RecyclerView.State.access$1302(this.mRecyclerView.mState, this.mTargetPosition);
      this.mRunning = true;
      this.mPendingInitialRun = true;
      this.mTargetView = findViewByPosition(getTargetPosition());
      onStart();
      this.mRecyclerView.mViewFlinger.postOnAnimation();
    }
    
    protected final void stop() {
      if (!this.mRunning)
        return; 
      this.mRunning = false;
      onStop();
      RecyclerView.State.access$1302(this.mRecyclerView.mState, -1);
      this.mTargetView = null;
      this.mTargetPosition = -1;
      this.mPendingInitialRun = false;
      this.mLayoutManager.onSmoothScrollerStopped(this);
      this.mLayoutManager = null;
      this.mRecyclerView = null;
    }
    
    public static class Action {
      public static final int UNDEFINED_DURATION = -2147483648;
      
      private boolean mChanged = false;
      
      private int mConsecutiveUpdates = 0;
      
      private int mDuration;
      
      private int mDx;
      
      private int mDy;
      
      private Interpolator mInterpolator;
      
      private int mJumpToPosition = -1;
      
      public Action(int param2Int1, int param2Int2) { this(param2Int1, param2Int2, -2147483648, null); }
      
      public Action(int param2Int1, int param2Int2, int param2Int3) { this(param2Int1, param2Int2, param2Int3, null); }
      
      public Action(int param2Int1, int param2Int2, int param2Int3, Interpolator param2Interpolator) {
        this.mDx = param2Int1;
        this.mDy = param2Int2;
        this.mDuration = param2Int3;
        this.mInterpolator = param2Interpolator;
      }
      
      private void validate() {
        if (this.mInterpolator != null && this.mDuration < 1)
          throw new IllegalStateException("If you provide an interpolator, you must set a positive duration"); 
        if (this.mDuration < 1)
          throw new IllegalStateException("Scroll duration must be a positive number"); 
      }
      
      public int getDuration() { return this.mDuration; }
      
      public int getDx() { return this.mDx; }
      
      public int getDy() { return this.mDy; }
      
      public Interpolator getInterpolator() { return this.mInterpolator; }
      
      boolean hasJumpTarget() { return (this.mJumpToPosition >= 0); }
      
      public void jumpTo(int param2Int) { this.mJumpToPosition = param2Int; }
      
      void runIfNecessary(RecyclerView param2RecyclerView) {
        if (this.mJumpToPosition >= 0) {
          int i = this.mJumpToPosition;
          this.mJumpToPosition = -1;
          param2RecyclerView.jumpToPositionForSmoothScroller(i);
          this.mChanged = false;
          return;
        } 
        if (this.mChanged) {
          validate();
          if (this.mInterpolator == null) {
            if (this.mDuration == Integer.MIN_VALUE) {
              param2RecyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy);
            } else {
              param2RecyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy, this.mDuration);
            } 
          } else {
            param2RecyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy, this.mDuration, this.mInterpolator);
          } 
          this.mConsecutiveUpdates++;
          if (this.mConsecutiveUpdates > 10)
            Log.e("RecyclerView", "Smooth Scroll action is being updated too frequently. Make sure you are not changing it unless necessary"); 
          this.mChanged = false;
          return;
        } 
        this.mConsecutiveUpdates = 0;
      }
      
      public void setDuration(int param2Int) {
        this.mChanged = true;
        this.mDuration = param2Int;
      }
      
      public void setDx(int param2Int) {
        this.mChanged = true;
        this.mDx = param2Int;
      }
      
      public void setDy(int param2Int) {
        this.mChanged = true;
        this.mDy = param2Int;
      }
      
      public void setInterpolator(Interpolator param2Interpolator) {
        this.mChanged = true;
        this.mInterpolator = param2Interpolator;
      }
      
      public void update(int param2Int1, int param2Int2, int param2Int3, Interpolator param2Interpolator) {
        this.mDx = param2Int1;
        this.mDy = param2Int2;
        this.mDuration = param2Int3;
        this.mInterpolator = param2Interpolator;
        this.mChanged = true;
      }
    }
    
    public static interface ScrollVectorProvider {
      PointF computeScrollVectorForPosition(int param2Int);
    }
  }
  
  public static class Action {
    public static final int UNDEFINED_DURATION = -2147483648;
    
    private boolean mChanged = false;
    
    private int mConsecutiveUpdates = 0;
    
    private int mDuration;
    
    private int mDx;
    
    private int mDy;
    
    private Interpolator mInterpolator;
    
    private int mJumpToPosition = -1;
    
    public Action(int param1Int1, int param1Int2) { this(param1Int1, param1Int2, -2147483648, null); }
    
    public Action(int param1Int1, int param1Int2, int param1Int3) { this(param1Int1, param1Int2, param1Int3, null); }
    
    public Action(int param1Int1, int param1Int2, int param1Int3, Interpolator param1Interpolator) {
      this.mDx = param1Int1;
      this.mDy = param1Int2;
      this.mDuration = param1Int3;
      this.mInterpolator = param1Interpolator;
    }
    
    private void validate() {
      if (this.mInterpolator != null && this.mDuration < 1)
        throw new IllegalStateException("If you provide an interpolator, you must set a positive duration"); 
      if (this.mDuration < 1)
        throw new IllegalStateException("Scroll duration must be a positive number"); 
    }
    
    public int getDuration() { return this.mDuration; }
    
    public int getDx() { return this.mDx; }
    
    public int getDy() { return this.mDy; }
    
    public Interpolator getInterpolator() { return this.mInterpolator; }
    
    boolean hasJumpTarget() { return (this.mJumpToPosition >= 0); }
    
    public void jumpTo(int param1Int) { this.mJumpToPosition = param1Int; }
    
    void runIfNecessary(RecyclerView param1RecyclerView) {
      if (this.mJumpToPosition >= 0) {
        int i = this.mJumpToPosition;
        this.mJumpToPosition = -1;
        param1RecyclerView.jumpToPositionForSmoothScroller(i);
        this.mChanged = false;
        return;
      } 
      if (this.mChanged) {
        validate();
        if (this.mInterpolator == null) {
          if (this.mDuration == Integer.MIN_VALUE) {
            param1RecyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy);
          } else {
            param1RecyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy, this.mDuration);
          } 
        } else {
          param1RecyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy, this.mDuration, this.mInterpolator);
        } 
        this.mConsecutiveUpdates++;
        if (this.mConsecutiveUpdates > 10)
          Log.e("RecyclerView", "Smooth Scroll action is being updated too frequently. Make sure you are not changing it unless necessary"); 
        this.mChanged = false;
        return;
      } 
      this.mConsecutiveUpdates = 0;
    }
    
    public void setDuration(int param1Int) {
      this.mChanged = true;
      this.mDuration = param1Int;
    }
    
    public void setDx(int param1Int) {
      this.mChanged = true;
      this.mDx = param1Int;
    }
    
    public void setDy(int param1Int) {
      this.mChanged = true;
      this.mDy = param1Int;
    }
    
    public void setInterpolator(Interpolator param1Interpolator) {
      this.mChanged = true;
      this.mInterpolator = param1Interpolator;
    }
    
    public void update(int param1Int1, int param1Int2, int param1Int3, Interpolator param1Interpolator) {
      this.mDx = param1Int1;
      this.mDy = param1Int2;
      this.mDuration = param1Int3;
      this.mInterpolator = param1Interpolator;
      this.mChanged = true;
    }
  }
  
  public static interface ScrollVectorProvider {
    PointF computeScrollVectorForPosition(int param1Int);
  }
  
  public static class State {
    static final int STEP_ANIMATIONS = 4;
    
    static final int STEP_LAYOUT = 2;
    
    static final int STEP_START = 1;
    
    private SparseArray<Object> mData;
    
    int mDeletedInvisibleItemCountSincePreviousLayout = 0;
    
    long mFocusedItemId;
    
    int mFocusedItemPosition;
    
    int mFocusedSubChildId;
    
    boolean mInPreLayout = false;
    
    boolean mIsMeasuring = false;
    
    int mItemCount = 0;
    
    int mLayoutStep = 1;
    
    int mPreviousLayoutItemCount = 0;
    
    int mRemainingScrollHorizontal;
    
    int mRemainingScrollVertical;
    
    boolean mRunPredictiveAnimations = false;
    
    boolean mRunSimpleAnimations = false;
    
    boolean mStructureChanged = false;
    
    private int mTargetPosition = -1;
    
    boolean mTrackOldChangeHolders = false;
    
    void assertLayoutStep(int param1Int) {
      if ((this.mLayoutStep & param1Int) == 0) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Layout state should be one of ");
        stringBuilder.append(Integer.toBinaryString(param1Int));
        stringBuilder.append(" but it is ");
        stringBuilder.append(Integer.toBinaryString(this.mLayoutStep));
        throw new IllegalStateException(stringBuilder.toString());
      } 
    }
    
    public boolean didStructureChange() { return this.mStructureChanged; }
    
    public <T> T get(int param1Int) { return (this.mData == null) ? null : (T)this.mData.get(param1Int); }
    
    public int getItemCount() { return this.mInPreLayout ? (this.mPreviousLayoutItemCount - this.mDeletedInvisibleItemCountSincePreviousLayout) : this.mItemCount; }
    
    public int getRemainingScrollHorizontal() { return this.mRemainingScrollHorizontal; }
    
    public int getRemainingScrollVertical() { return this.mRemainingScrollVertical; }
    
    public int getTargetScrollPosition() { return this.mTargetPosition; }
    
    public boolean hasTargetScrollPosition() { return (this.mTargetPosition != -1); }
    
    public boolean isMeasuring() { return this.mIsMeasuring; }
    
    public boolean isPreLayout() { return this.mInPreLayout; }
    
    void prepareForNestedPrefetch(RecyclerView.Adapter param1Adapter) {
      this.mLayoutStep = 1;
      this.mItemCount = param1Adapter.getItemCount();
      this.mInPreLayout = false;
      this.mTrackOldChangeHolders = false;
      this.mIsMeasuring = false;
    }
    
    public void put(int param1Int, Object param1Object) {
      if (this.mData == null)
        this.mData = new SparseArray(); 
      this.mData.put(param1Int, param1Object);
    }
    
    public void remove(int param1Int) {
      if (this.mData == null)
        return; 
      this.mData.remove(param1Int);
    }
    
    State reset() {
      this.mTargetPosition = -1;
      if (this.mData != null)
        this.mData.clear(); 
      this.mItemCount = 0;
      this.mStructureChanged = false;
      this.mIsMeasuring = false;
      return this;
    }
    
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("State{mTargetPosition=");
      stringBuilder.append(this.mTargetPosition);
      stringBuilder.append(", mData=");
      stringBuilder.append(this.mData);
      stringBuilder.append(", mItemCount=");
      stringBuilder.append(this.mItemCount);
      stringBuilder.append(", mIsMeasuring=");
      stringBuilder.append(this.mIsMeasuring);
      stringBuilder.append(", mPreviousLayoutItemCount=");
      stringBuilder.append(this.mPreviousLayoutItemCount);
      stringBuilder.append(", mDeletedInvisibleItemCountSincePreviousLayout=");
      stringBuilder.append(this.mDeletedInvisibleItemCountSincePreviousLayout);
      stringBuilder.append(", mStructureChanged=");
      stringBuilder.append(this.mStructureChanged);
      stringBuilder.append(", mInPreLayout=");
      stringBuilder.append(this.mInPreLayout);
      stringBuilder.append(", mRunSimpleAnimations=");
      stringBuilder.append(this.mRunSimpleAnimations);
      stringBuilder.append(", mRunPredictiveAnimations=");
      stringBuilder.append(this.mRunPredictiveAnimations);
      stringBuilder.append('}');
      return stringBuilder.toString();
    }
    
    public boolean willRunPredictiveAnimations() { return this.mRunPredictiveAnimations; }
    
    public boolean willRunSimpleAnimations() { return this.mRunSimpleAnimations; }
    
    @Retention(RetentionPolicy.SOURCE)
    static @interface LayoutState {}
  }
  
  @Retention(RetentionPolicy.SOURCE)
  static @interface LayoutState {}
  
  public static abstract class ViewCacheExtension {
    public abstract View getViewForPositionAndType(RecyclerView.Recycler param1Recycler, int param1Int1, int param1Int2);
  }
  
  class ViewFlinger implements Runnable {
    private boolean mEatRunOnAnimationRequest = false;
    
    Interpolator mInterpolator = RecyclerView.sQuinticInterpolator;
    
    private int mLastFlingX;
    
    private int mLastFlingY;
    
    private boolean mReSchedulePostAnimationCallback = false;
    
    private OverScroller mScroller;
    
    ViewFlinger() { this.mScroller = new OverScroller(this$0.getContext(), RecyclerView.sQuinticInterpolator); }
    
    private int computeScrollDuration(int param1Int1, int param1Int2, int param1Int3, int param1Int4) {
      boolean bool;
      int i = Math.abs(param1Int1);
      int j = Math.abs(param1Int2);
      if (i > j) {
        bool = true;
      } else {
        bool = false;
      } 
      param1Int3 = (int)Math.sqrt((param1Int3 * param1Int3 + param1Int4 * param1Int4));
      param1Int2 = (int)Math.sqrt((param1Int1 * param1Int1 + param1Int2 * param1Int2));
      if (bool) {
        param1Int1 = RecyclerView.this.getWidth();
      } else {
        param1Int1 = RecyclerView.this.getHeight();
      } 
      param1Int4 = param1Int1 / 2;
      float f2 = param1Int2;
      float f1 = param1Int1;
      float f3 = Math.min(1.0F, f2 * 1.0F / f1);
      f2 = param1Int4;
      f3 = distanceInfluenceForSnapDuration(f3);
      if (param1Int3 > 0) {
        param1Int1 = Math.round(Math.abs((f2 + f3 * f2) / param1Int3) * 1000.0F) * 4;
      } else {
        if (bool) {
          param1Int1 = i;
        } else {
          param1Int1 = j;
        } 
        param1Int1 = (int)((param1Int1 / f1 + 1.0F) * 300.0F);
      } 
      return Math.min(param1Int1, 2000);
    }
    
    private void disableRunOnAnimationRequests() {
      this.mReSchedulePostAnimationCallback = false;
      this.mEatRunOnAnimationRequest = true;
    }
    
    private float distanceInfluenceForSnapDuration(float param1Float) { return (float)Math.sin(((param1Float - 0.5F) * 0.47123894F)); }
    
    private void enableRunOnAnimationRequests() {
      this.mEatRunOnAnimationRequest = false;
      if (this.mReSchedulePostAnimationCallback)
        postOnAnimation(); 
    }
    
    public void fling(int param1Int1, int param1Int2) {
      RecyclerView.this.setScrollState(2);
      this.mLastFlingY = 0;
      this.mLastFlingX = 0;
      this.mScroller.fling(0, 0, param1Int1, param1Int2, -2147483648, 2147483647, -2147483648, 2147483647);
      postOnAnimation();
    }
    
    void postOnAnimation() {
      if (this.mEatRunOnAnimationRequest) {
        this.mReSchedulePostAnimationCallback = true;
        return;
      } 
      RecyclerView.this.removeCallbacks(this);
      ViewCompat.postOnAnimation(RecyclerView.this, this);
    }
    
    public void run() { // Byte code:
      //   0: aload_0
      //   1: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   4: getfield mLayout : Landroid/support/v7/widget/RecyclerView$LayoutManager;
      //   7: ifnonnull -> 15
      //   10: aload_0
      //   11: invokevirtual stop : ()V
      //   14: return
      //   15: aload_0
      //   16: invokespecial disableRunOnAnimationRequests : ()V
      //   19: aload_0
      //   20: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   23: invokevirtual consumePendingUpdateOperations : ()V
      //   26: aload_0
      //   27: getfield mScroller : Landroid/widget/OverScroller;
      //   30: astore #13
      //   32: aload_0
      //   33: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   36: getfield mLayout : Landroid/support/v7/widget/RecyclerView$LayoutManager;
      //   39: getfield mSmoothScroller : Landroid/support/v7/widget/RecyclerView$SmoothScroller;
      //   42: astore #14
      //   44: aload #13
      //   46: invokevirtual computeScrollOffset : ()Z
      //   49: ifeq -> 911
      //   52: aload_0
      //   53: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   56: invokestatic access$500 : (Landroid/support/v7/widget/RecyclerView;)[I
      //   59: astore #15
      //   61: aload #13
      //   63: invokevirtual getCurrX : ()I
      //   66: istore #11
      //   68: aload #13
      //   70: invokevirtual getCurrY : ()I
      //   73: istore #12
      //   75: iload #11
      //   77: aload_0
      //   78: getfield mLastFlingX : I
      //   81: isub
      //   82: istore_2
      //   83: iload #12
      //   85: aload_0
      //   86: getfield mLastFlingY : I
      //   89: isub
      //   90: istore_1
      //   91: aload_0
      //   92: iload #11
      //   94: putfield mLastFlingX : I
      //   97: aload_0
      //   98: iload #12
      //   100: putfield mLastFlingY : I
      //   103: iload_2
      //   104: istore #6
      //   106: iload_1
      //   107: istore #5
      //   109: aload_0
      //   110: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   113: iload_2
      //   114: iload_1
      //   115: aload #15
      //   117: aconst_null
      //   118: iconst_1
      //   119: invokevirtual dispatchNestedPreScroll : (II[I[II)Z
      //   122: ifeq -> 141
      //   125: iload_2
      //   126: aload #15
      //   128: iconst_0
      //   129: iaload
      //   130: isub
      //   131: istore #6
      //   133: iload_1
      //   134: aload #15
      //   136: iconst_1
      //   137: iaload
      //   138: isub
      //   139: istore #5
      //   141: aload_0
      //   142: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   145: getfield mAdapter : Landroid/support/v7/widget/RecyclerView$Adapter;
      //   148: ifnull -> 476
      //   151: aload_0
      //   152: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   155: invokevirtual startInterceptRequestLayout : ()V
      //   158: aload_0
      //   159: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   162: invokevirtual onEnterLayoutOrScroll : ()V
      //   165: ldc 'RV Scroll'
      //   167: invokestatic beginSection : (Ljava/lang/String;)V
      //   170: aload_0
      //   171: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   174: aload_0
      //   175: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   178: getfield mState : Landroid/support/v7/widget/RecyclerView$State;
      //   181: invokevirtual fillRemainingScrollValues : (Landroid/support/v7/widget/RecyclerView$State;)V
      //   184: iload #6
      //   186: ifeq -> 224
      //   189: aload_0
      //   190: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   193: getfield mLayout : Landroid/support/v7/widget/RecyclerView$LayoutManager;
      //   196: iload #6
      //   198: aload_0
      //   199: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   202: getfield mRecycler : Landroid/support/v7/widget/RecyclerView$Recycler;
      //   205: aload_0
      //   206: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   209: getfield mState : Landroid/support/v7/widget/RecyclerView$State;
      //   212: invokevirtual scrollHorizontallyBy : (ILandroid/support/v7/widget/RecyclerView$Recycler;Landroid/support/v7/widget/RecyclerView$State;)I
      //   215: istore_1
      //   216: iload #6
      //   218: iload_1
      //   219: isub
      //   220: istore_2
      //   221: goto -> 228
      //   224: iconst_0
      //   225: istore_1
      //   226: iconst_0
      //   227: istore_2
      //   228: iload #5
      //   230: ifeq -> 269
      //   233: aload_0
      //   234: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   237: getfield mLayout : Landroid/support/v7/widget/RecyclerView$LayoutManager;
      //   240: iload #5
      //   242: aload_0
      //   243: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   246: getfield mRecycler : Landroid/support/v7/widget/RecyclerView$Recycler;
      //   249: aload_0
      //   250: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   253: getfield mState : Landroid/support/v7/widget/RecyclerView$State;
      //   256: invokevirtual scrollVerticallyBy : (ILandroid/support/v7/widget/RecyclerView$Recycler;Landroid/support/v7/widget/RecyclerView$State;)I
      //   259: istore_3
      //   260: iload #5
      //   262: iload_3
      //   263: isub
      //   264: istore #4
      //   266: goto -> 274
      //   269: iconst_0
      //   270: istore_3
      //   271: iconst_0
      //   272: istore #4
      //   274: invokestatic endSection : ()V
      //   277: aload_0
      //   278: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   281: invokevirtual repositionShadowingViews : ()V
      //   284: aload_0
      //   285: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   288: invokevirtual onExitLayoutOrScroll : ()V
      //   291: aload_0
      //   292: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   295: iconst_0
      //   296: invokevirtual stopInterceptRequestLayout : (Z)V
      //   299: iload_1
      //   300: istore #7
      //   302: iload_2
      //   303: istore #10
      //   305: iload_3
      //   306: istore #9
      //   308: iload #4
      //   310: istore #8
      //   312: aload #14
      //   314: ifnull -> 488
      //   317: iload_1
      //   318: istore #7
      //   320: iload_2
      //   321: istore #10
      //   323: iload_3
      //   324: istore #9
      //   326: iload #4
      //   328: istore #8
      //   330: aload #14
      //   332: invokevirtual isPendingInitialRun : ()Z
      //   335: ifne -> 488
      //   338: iload_1
      //   339: istore #7
      //   341: iload_2
      //   342: istore #10
      //   344: iload_3
      //   345: istore #9
      //   347: iload #4
      //   349: istore #8
      //   351: aload #14
      //   353: invokevirtual isRunning : ()Z
      //   356: ifeq -> 488
      //   359: aload_0
      //   360: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   363: getfield mState : Landroid/support/v7/widget/RecyclerView$State;
      //   366: invokevirtual getItemCount : ()I
      //   369: istore #7
      //   371: iload #7
      //   373: ifne -> 397
      //   376: aload #14
      //   378: invokevirtual stop : ()V
      //   381: iload_1
      //   382: istore #7
      //   384: iload_2
      //   385: istore #10
      //   387: iload_3
      //   388: istore #9
      //   390: iload #4
      //   392: istore #8
      //   394: goto -> 488
      //   397: aload #14
      //   399: invokevirtual getTargetPosition : ()I
      //   402: iload #7
      //   404: if_icmplt -> 446
      //   407: aload #14
      //   409: iload #7
      //   411: iconst_1
      //   412: isub
      //   413: invokevirtual setTargetPosition : (I)V
      //   416: aload #14
      //   418: iload #6
      //   420: iload_2
      //   421: isub
      //   422: iload #5
      //   424: iload #4
      //   426: isub
      //   427: invokestatic access$600 : (Landroid/support/v7/widget/RecyclerView$SmoothScroller;II)V
      //   430: iload_1
      //   431: istore #7
      //   433: iload_2
      //   434: istore #10
      //   436: iload_3
      //   437: istore #9
      //   439: iload #4
      //   441: istore #8
      //   443: goto -> 488
      //   446: aload #14
      //   448: iload #6
      //   450: iload_2
      //   451: isub
      //   452: iload #5
      //   454: iload #4
      //   456: isub
      //   457: invokestatic access$600 : (Landroid/support/v7/widget/RecyclerView$SmoothScroller;II)V
      //   460: iload_1
      //   461: istore #7
      //   463: iload_2
      //   464: istore #10
      //   466: iload_3
      //   467: istore #9
      //   469: iload #4
      //   471: istore #8
      //   473: goto -> 488
      //   476: iconst_0
      //   477: istore #7
      //   479: iconst_0
      //   480: istore #10
      //   482: iconst_0
      //   483: istore #9
      //   485: iconst_0
      //   486: istore #8
      //   488: aload_0
      //   489: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   492: getfield mItemDecorations : Ljava/util/ArrayList;
      //   495: invokevirtual isEmpty : ()Z
      //   498: ifne -> 508
      //   501: aload_0
      //   502: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   505: invokevirtual invalidate : ()V
      //   508: aload_0
      //   509: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   512: invokevirtual getOverScrollMode : ()I
      //   515: iconst_2
      //   516: if_icmpeq -> 530
      //   519: aload_0
      //   520: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   523: iload #6
      //   525: iload #5
      //   527: invokevirtual considerReleasingGlowsOnScroll : (II)V
      //   530: aload_0
      //   531: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   534: iload #7
      //   536: iload #9
      //   538: iload #10
      //   540: iload #8
      //   542: aconst_null
      //   543: iconst_1
      //   544: invokevirtual dispatchNestedScroll : (IIII[II)Z
      //   547: ifne -> 688
      //   550: iload #10
      //   552: ifne -> 560
      //   555: iload #8
      //   557: ifeq -> 688
      //   560: aload #13
      //   562: invokevirtual getCurrVelocity : ()F
      //   565: f2i
      //   566: istore_2
      //   567: iload #10
      //   569: iload #11
      //   571: if_icmpeq -> 595
      //   574: iload #10
      //   576: ifge -> 585
      //   579: iload_2
      //   580: ineg
      //   581: istore_1
      //   582: goto -> 597
      //   585: iload #10
      //   587: ifle -> 595
      //   590: iload_2
      //   591: istore_1
      //   592: goto -> 597
      //   595: iconst_0
      //   596: istore_1
      //   597: iload #8
      //   599: iload #12
      //   601: if_icmpeq -> 623
      //   604: iload #8
      //   606: ifge -> 615
      //   609: iload_2
      //   610: ineg
      //   611: istore_2
      //   612: goto -> 625
      //   615: iload #8
      //   617: ifle -> 623
      //   620: goto -> 625
      //   623: iconst_0
      //   624: istore_2
      //   625: aload_0
      //   626: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   629: invokevirtual getOverScrollMode : ()I
      //   632: iconst_2
      //   633: if_icmpeq -> 645
      //   636: aload_0
      //   637: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   640: iload_1
      //   641: iload_2
      //   642: invokevirtual absorbGlows : (II)V
      //   645: iload_1
      //   646: ifne -> 664
      //   649: iload #10
      //   651: iload #11
      //   653: if_icmpeq -> 664
      //   656: aload #13
      //   658: invokevirtual getFinalX : ()I
      //   661: ifne -> 688
      //   664: iload_2
      //   665: ifne -> 683
      //   668: iload #8
      //   670: iload #12
      //   672: if_icmpeq -> 683
      //   675: aload #13
      //   677: invokevirtual getFinalY : ()I
      //   680: ifne -> 688
      //   683: aload #13
      //   685: invokevirtual abortAnimation : ()V
      //   688: iload #7
      //   690: ifne -> 698
      //   693: iload #9
      //   695: ifeq -> 709
      //   698: aload_0
      //   699: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   702: iload #7
      //   704: iload #9
      //   706: invokevirtual dispatchOnScrolled : (II)V
      //   709: aload_0
      //   710: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   713: invokestatic access$700 : (Landroid/support/v7/widget/RecyclerView;)Z
      //   716: ifne -> 726
      //   719: aload_0
      //   720: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   723: invokevirtual invalidate : ()V
      //   726: iload #5
      //   728: ifeq -> 756
      //   731: aload_0
      //   732: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   735: getfield mLayout : Landroid/support/v7/widget/RecyclerView$LayoutManager;
      //   738: invokevirtual canScrollVertically : ()Z
      //   741: ifeq -> 756
      //   744: iload #9
      //   746: iload #5
      //   748: if_icmpne -> 756
      //   751: iconst_1
      //   752: istore_1
      //   753: goto -> 758
      //   756: iconst_0
      //   757: istore_1
      //   758: iload #6
      //   760: ifeq -> 788
      //   763: aload_0
      //   764: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   767: getfield mLayout : Landroid/support/v7/widget/RecyclerView$LayoutManager;
      //   770: invokevirtual canScrollHorizontally : ()Z
      //   773: ifeq -> 788
      //   776: iload #7
      //   778: iload #6
      //   780: if_icmpne -> 788
      //   783: iconst_1
      //   784: istore_2
      //   785: goto -> 790
      //   788: iconst_0
      //   789: istore_2
      //   790: iload #6
      //   792: ifne -> 800
      //   795: iload #5
      //   797: ifeq -> 816
      //   800: iload_2
      //   801: ifne -> 816
      //   804: iload_1
      //   805: ifeq -> 811
      //   808: goto -> 816
      //   811: iconst_0
      //   812: istore_1
      //   813: goto -> 818
      //   816: iconst_1
      //   817: istore_1
      //   818: aload #13
      //   820: invokevirtual isFinished : ()Z
      //   823: ifne -> 879
      //   826: iload_1
      //   827: ifne -> 844
      //   830: aload_0
      //   831: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   834: iconst_1
      //   835: invokevirtual hasNestedScrollingParent : (I)Z
      //   838: ifne -> 844
      //   841: goto -> 879
      //   844: aload_0
      //   845: invokevirtual postOnAnimation : ()V
      //   848: aload_0
      //   849: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   852: getfield mGapWorker : Landroid/support/v7/widget/GapWorker;
      //   855: ifnull -> 911
      //   858: aload_0
      //   859: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   862: getfield mGapWorker : Landroid/support/v7/widget/GapWorker;
      //   865: aload_0
      //   866: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   869: iload #6
      //   871: iload #5
      //   873: invokevirtual postFromTraversal : (Landroid/support/v7/widget/RecyclerView;II)V
      //   876: goto -> 911
      //   879: aload_0
      //   880: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   883: iconst_0
      //   884: invokevirtual setScrollState : (I)V
      //   887: invokestatic access$800 : ()Z
      //   890: ifeq -> 903
      //   893: aload_0
      //   894: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   897: getfield mPrefetchRegistry : Landroid/support/v7/widget/GapWorker$LayoutPrefetchRegistryImpl;
      //   900: invokevirtual clearPrefetchPositions : ()V
      //   903: aload_0
      //   904: getfield this$0 : Landroid/support/v7/widget/RecyclerView;
      //   907: iconst_1
      //   908: invokevirtual stopNestedScroll : (I)V
      //   911: aload #14
      //   913: ifnull -> 943
      //   916: aload #14
      //   918: invokevirtual isPendingInitialRun : ()Z
      //   921: ifeq -> 931
      //   924: aload #14
      //   926: iconst_0
      //   927: iconst_0
      //   928: invokestatic access$600 : (Landroid/support/v7/widget/RecyclerView$SmoothScroller;II)V
      //   931: aload_0
      //   932: getfield mReSchedulePostAnimationCallback : Z
      //   935: ifne -> 943
      //   938: aload #14
      //   940: invokevirtual stop : ()V
      //   943: aload_0
      //   944: invokespecial enableRunOnAnimationRequests : ()V
      //   947: return }
    
    public void smoothScrollBy(int param1Int1, int param1Int2) { smoothScrollBy(param1Int1, param1Int2, 0, 0); }
    
    public void smoothScrollBy(int param1Int1, int param1Int2, int param1Int3) { smoothScrollBy(param1Int1, param1Int2, param1Int3, RecyclerView.sQuinticInterpolator); }
    
    public void smoothScrollBy(int param1Int1, int param1Int2, int param1Int3, int param1Int4) { smoothScrollBy(param1Int1, param1Int2, computeScrollDuration(param1Int1, param1Int2, param1Int3, param1Int4)); }
    
    public void smoothScrollBy(int param1Int1, int param1Int2, int param1Int3, Interpolator param1Interpolator) {
      if (this.mInterpolator != param1Interpolator) {
        this.mInterpolator = param1Interpolator;
        this.mScroller = new OverScroller(RecyclerView.this.getContext(), param1Interpolator);
      } 
      RecyclerView.this.setScrollState(2);
      this.mLastFlingY = 0;
      this.mLastFlingX = 0;
      this.mScroller.startScroll(0, 0, param1Int1, param1Int2, param1Int3);
      if (Build.VERSION.SDK_INT < 23)
        this.mScroller.computeScrollOffset(); 
      postOnAnimation();
    }
    
    public void smoothScrollBy(int param1Int1, int param1Int2, Interpolator param1Interpolator) {
      int i = computeScrollDuration(param1Int1, param1Int2, 0, 0);
      Interpolator interpolator = param1Interpolator;
      if (param1Interpolator == null)
        interpolator = RecyclerView.sQuinticInterpolator; 
      smoothScrollBy(param1Int1, param1Int2, i, interpolator);
    }
    
    public void stop() {
      RecyclerView.this.removeCallbacks(this);
      this.mScroller.abortAnimation();
    }
  }
  
  public static abstract class ViewHolder {
    static final int FLAG_ADAPTER_FULLUPDATE = 1024;
    
    static final int FLAG_ADAPTER_POSITION_UNKNOWN = 512;
    
    static final int FLAG_APPEARED_IN_PRE_LAYOUT = 4096;
    
    static final int FLAG_BOUNCED_FROM_HIDDEN_LIST = 8192;
    
    static final int FLAG_BOUND = 1;
    
    static final int FLAG_IGNORE = 128;
    
    static final int FLAG_INVALID = 4;
    
    static final int FLAG_MOVED = 2048;
    
    static final int FLAG_NOT_RECYCLABLE = 16;
    
    static final int FLAG_REMOVED = 8;
    
    static final int FLAG_RETURNED_FROM_SCRAP = 32;
    
    static final int FLAG_SET_A11Y_ITEM_DELEGATE = 16384;
    
    static final int FLAG_TMP_DETACHED = 256;
    
    static final int FLAG_UPDATE = 2;
    
    private static final List<Object> FULLUPDATE_PAYLOADS = Collections.EMPTY_LIST;
    
    static final int PENDING_ACCESSIBILITY_STATE_NOT_SET = -1;
    
    public final View itemView;
    
    private int mFlags;
    
    private boolean mInChangeScrap = false;
    
    private int mIsRecyclableCount = 0;
    
    long mItemId = -1L;
    
    int mItemViewType = -1;
    
    WeakReference<RecyclerView> mNestedRecyclerView;
    
    int mOldPosition = -1;
    
    RecyclerView mOwnerRecyclerView;
    
    List<Object> mPayloads = null;
    
    @VisibleForTesting
    int mPendingAccessibilityState = -1;
    
    int mPosition = -1;
    
    int mPreLayoutPosition = -1;
    
    private RecyclerView.Recycler mScrapContainer = null;
    
    ViewHolder mShadowedHolder = null;
    
    ViewHolder mShadowingHolder = null;
    
    List<Object> mUnmodifiedPayloads = null;
    
    private int mWasImportantForAccessibilityBeforeHidden = 0;
    
    public ViewHolder(View param1View) {
      if (param1View == null)
        throw new IllegalArgumentException("itemView may not be null"); 
      this.itemView = param1View;
    }
    
    private void createPayloadsIfNeeded() {
      if (this.mPayloads == null) {
        this.mPayloads = new ArrayList();
        this.mUnmodifiedPayloads = Collections.unmodifiableList(this.mPayloads);
      } 
    }
    
    private boolean doesTransientStatePreventRecycling() { return ((this.mFlags & 0x10) == 0 && ViewCompat.hasTransientState(this.itemView)); }
    
    private void onEnteredHiddenState(RecyclerView param1RecyclerView) {
      if (this.mPendingAccessibilityState != -1) {
        this.mWasImportantForAccessibilityBeforeHidden = this.mPendingAccessibilityState;
      } else {
        this.mWasImportantForAccessibilityBeforeHidden = ViewCompat.getImportantForAccessibility(this.itemView);
      } 
      param1RecyclerView.setChildImportantForAccessibilityInternal(this, 4);
    }
    
    private void onLeftHiddenState(RecyclerView param1RecyclerView) {
      param1RecyclerView.setChildImportantForAccessibilityInternal(this, this.mWasImportantForAccessibilityBeforeHidden);
      this.mWasImportantForAccessibilityBeforeHidden = 0;
    }
    
    private boolean shouldBeKeptAsChild() { return ((this.mFlags & 0x10) != 0); }
    
    void addChangePayload(Object param1Object) {
      if (param1Object == null) {
        addFlags(1024);
        return;
      } 
      if ((0x400 & this.mFlags) == 0) {
        createPayloadsIfNeeded();
        this.mPayloads.add(param1Object);
      } 
    }
    
    void addFlags(int param1Int) { this.mFlags = param1Int | this.mFlags; }
    
    void clearOldPosition() {
      this.mOldPosition = -1;
      this.mPreLayoutPosition = -1;
    }
    
    void clearPayload() {
      if (this.mPayloads != null)
        this.mPayloads.clear(); 
      this.mFlags &= 0xFFFFFBFF;
    }
    
    void clearReturnedFromScrapFlag() { this.mFlags &= 0xFFFFFFDF; }
    
    void clearTmpDetachFlag() { this.mFlags &= 0xFFFFFEFF; }
    
    void flagRemovedAndOffsetPosition(int param1Int1, int param1Int2, boolean param1Boolean) {
      addFlags(8);
      offsetPosition(param1Int2, param1Boolean);
      this.mPosition = param1Int1;
    }
    
    public final int getAdapterPosition() { return (this.mOwnerRecyclerView == null) ? -1 : this.mOwnerRecyclerView.getAdapterPositionFor(this); }
    
    public final long getItemId() { return this.mItemId; }
    
    public final int getItemViewType() { return this.mItemViewType; }
    
    public final int getLayoutPosition() { return (this.mPreLayoutPosition == -1) ? this.mPosition : this.mPreLayoutPosition; }
    
    public final int getOldPosition() { return this.mOldPosition; }
    
    @Deprecated
    public final int getPosition() { return (this.mPreLayoutPosition == -1) ? this.mPosition : this.mPreLayoutPosition; }
    
    List<Object> getUnmodifiedPayloads() { return ((this.mFlags & 0x400) == 0) ? ((this.mPayloads == null || this.mPayloads.size() == 0) ? FULLUPDATE_PAYLOADS : this.mUnmodifiedPayloads) : FULLUPDATE_PAYLOADS; }
    
    boolean hasAnyOfTheFlags(int param1Int) { return ((param1Int & this.mFlags) != 0); }
    
    boolean isAdapterPositionUnknown() { return ((this.mFlags & 0x200) != 0 || isInvalid()); }
    
    boolean isBound() { return ((this.mFlags & true) != 0); }
    
    boolean isInvalid() { return ((this.mFlags & 0x4) != 0); }
    
    public final boolean isRecyclable() { return ((this.mFlags & 0x10) == 0 && !ViewCompat.hasTransientState(this.itemView)); }
    
    boolean isRemoved() { return ((this.mFlags & 0x8) != 0); }
    
    boolean isScrap() { return (this.mScrapContainer != null); }
    
    boolean isTmpDetached() { return ((this.mFlags & 0x100) != 0); }
    
    boolean isUpdated() { return ((this.mFlags & 0x2) != 0); }
    
    boolean needsUpdate() { return ((this.mFlags & 0x2) != 0); }
    
    void offsetPosition(int param1Int, boolean param1Boolean) {
      if (this.mOldPosition == -1)
        this.mOldPosition = this.mPosition; 
      if (this.mPreLayoutPosition == -1)
        this.mPreLayoutPosition = this.mPosition; 
      if (param1Boolean)
        this.mPreLayoutPosition += param1Int; 
      this.mPosition += param1Int;
      if (this.itemView.getLayoutParams() != null)
        ((RecyclerView.LayoutParams)this.itemView.getLayoutParams()).mInsetsDirty = true; 
    }
    
    void resetInternal() {
      this.mFlags = 0;
      this.mPosition = -1;
      this.mOldPosition = -1;
      this.mItemId = -1L;
      this.mPreLayoutPosition = -1;
      this.mIsRecyclableCount = 0;
      this.mShadowedHolder = null;
      this.mShadowingHolder = null;
      clearPayload();
      this.mWasImportantForAccessibilityBeforeHidden = 0;
      this.mPendingAccessibilityState = -1;
      RecyclerView.clearNestedRecyclerViewIfNotNested(this);
    }
    
    void saveOldPosition() {
      if (this.mOldPosition == -1)
        this.mOldPosition = this.mPosition; 
    }
    
    void setFlags(int param1Int1, int param1Int2) { this.mFlags = param1Int1 & param1Int2 | this.mFlags & (param1Int2 ^ 0xFFFFFFFF); }
    
    public final void setIsRecyclable(boolean param1Boolean) {
      int i;
      if (param1Boolean) {
        i = this.mIsRecyclableCount - 1;
      } else {
        i = this.mIsRecyclableCount + 1;
      } 
      this.mIsRecyclableCount = i;
      if (this.mIsRecyclableCount < 0) {
        this.mIsRecyclableCount = 0;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("isRecyclable decremented below 0: unmatched pair of setIsRecyable() calls for ");
        stringBuilder.append(this);
        Log.e("View", stringBuilder.toString());
        return;
      } 
      if (!param1Boolean && this.mIsRecyclableCount == 1) {
        this.mFlags |= 0x10;
        return;
      } 
      if (param1Boolean && this.mIsRecyclableCount == 0)
        this.mFlags &= 0xFFFFFFEF; 
    }
    
    void setScrapContainer(RecyclerView.Recycler param1Recycler, boolean param1Boolean) {
      this.mScrapContainer = param1Recycler;
      this.mInChangeScrap = param1Boolean;
    }
    
    boolean shouldIgnore() { return ((this.mFlags & 0x80) != 0); }
    
    void stopIgnoring() { this.mFlags &= 0xFFFFFF7F; }
    
    public String toString() {
      StringBuilder stringBuilder1 = new StringBuilder();
      stringBuilder1.append("ViewHolder{");
      stringBuilder1.append(Integer.toHexString(hashCode()));
      stringBuilder1.append(" position=");
      stringBuilder1.append(this.mPosition);
      stringBuilder1.append(" id=");
      stringBuilder1.append(this.mItemId);
      stringBuilder1.append(", oldPos=");
      stringBuilder1.append(this.mOldPosition);
      stringBuilder1.append(", pLpos:");
      stringBuilder1.append(this.mPreLayoutPosition);
      StringBuilder stringBuilder2 = new StringBuilder(stringBuilder1.toString());
      if (isScrap()) {
        String str;
        stringBuilder2.append(" scrap ");
        if (this.mInChangeScrap) {
          str = "[changeScrap]";
        } else {
          str = "[attachedScrap]";
        } 
        stringBuilder2.append(str);
      } 
      if (isInvalid())
        stringBuilder2.append(" invalid"); 
      if (!isBound())
        stringBuilder2.append(" unbound"); 
      if (needsUpdate())
        stringBuilder2.append(" update"); 
      if (isRemoved())
        stringBuilder2.append(" removed"); 
      if (shouldIgnore())
        stringBuilder2.append(" ignored"); 
      if (isTmpDetached())
        stringBuilder2.append(" tmpDetached"); 
      if (!isRecyclable()) {
        stringBuilder1 = new StringBuilder();
        stringBuilder1.append(" not recyclable(");
        stringBuilder1.append(this.mIsRecyclableCount);
        stringBuilder1.append(")");
        stringBuilder2.append(stringBuilder1.toString());
      } 
      if (isAdapterPositionUnknown())
        stringBuilder2.append(" undefined adapter position"); 
      if (this.itemView.getParent() == null)
        stringBuilder2.append(" no parent"); 
      stringBuilder2.append("}");
      return stringBuilder2.toString();
    }
    
    void unScrap() { this.mScrapContainer.unscrapView(this); }
    
    boolean wasReturnedFromScrap() { return ((this.mFlags & 0x20) != 0); }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/RecyclerView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */