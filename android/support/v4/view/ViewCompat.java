package android.support.v4.view;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.res.ColorStateList;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeProviderCompat;
import android.util.Log;
import android.view.Display;
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeProvider;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ViewCompat {
  public static final int ACCESSIBILITY_LIVE_REGION_ASSERTIVE = 2;
  
  public static final int ACCESSIBILITY_LIVE_REGION_NONE = 0;
  
  public static final int ACCESSIBILITY_LIVE_REGION_POLITE = 1;
  
  static final ViewCompatBaseImpl IMPL;
  
  public static final int IMPORTANT_FOR_ACCESSIBILITY_AUTO = 0;
  
  public static final int IMPORTANT_FOR_ACCESSIBILITY_NO = 2;
  
  public static final int IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS = 4;
  
  public static final int IMPORTANT_FOR_ACCESSIBILITY_YES = 1;
  
  @Deprecated
  public static final int LAYER_TYPE_HARDWARE = 2;
  
  @Deprecated
  public static final int LAYER_TYPE_NONE = 0;
  
  @Deprecated
  public static final int LAYER_TYPE_SOFTWARE = 1;
  
  public static final int LAYOUT_DIRECTION_INHERIT = 2;
  
  public static final int LAYOUT_DIRECTION_LOCALE = 3;
  
  public static final int LAYOUT_DIRECTION_LTR = 0;
  
  public static final int LAYOUT_DIRECTION_RTL = 1;
  
  @Deprecated
  public static final int MEASURED_HEIGHT_STATE_SHIFT = 16;
  
  @Deprecated
  public static final int MEASURED_SIZE_MASK = 16777215;
  
  @Deprecated
  public static final int MEASURED_STATE_MASK = -16777216;
  
  @Deprecated
  public static final int MEASURED_STATE_TOO_SMALL = 16777216;
  
  @Deprecated
  public static final int OVER_SCROLL_ALWAYS = 0;
  
  @Deprecated
  public static final int OVER_SCROLL_IF_CONTENT_SCROLLS = 1;
  
  @Deprecated
  public static final int OVER_SCROLL_NEVER = 2;
  
  public static final int SCROLL_AXIS_HORIZONTAL = 1;
  
  public static final int SCROLL_AXIS_NONE = 0;
  
  public static final int SCROLL_AXIS_VERTICAL = 2;
  
  public static final int SCROLL_INDICATOR_BOTTOM = 2;
  
  public static final int SCROLL_INDICATOR_END = 32;
  
  public static final int SCROLL_INDICATOR_LEFT = 4;
  
  public static final int SCROLL_INDICATOR_RIGHT = 8;
  
  public static final int SCROLL_INDICATOR_START = 16;
  
  public static final int SCROLL_INDICATOR_TOP = 1;
  
  private static final String TAG = "ViewCompat";
  
  public static final int TYPE_NON_TOUCH = 1;
  
  public static final int TYPE_TOUCH = 0;
  
  static  {
    if (Build.VERSION.SDK_INT >= 26) {
      IMPL = new ViewCompatApi26Impl();
      return;
    } 
    if (Build.VERSION.SDK_INT >= 24) {
      IMPL = new ViewCompatApi24Impl();
      return;
    } 
    if (Build.VERSION.SDK_INT >= 23) {
      IMPL = new ViewCompatApi23Impl();
      return;
    } 
    if (Build.VERSION.SDK_INT >= 21) {
      IMPL = new ViewCompatApi21Impl();
      return;
    } 
    if (Build.VERSION.SDK_INT >= 19) {
      IMPL = new ViewCompatApi19Impl();
      return;
    } 
    if (Build.VERSION.SDK_INT >= 18) {
      IMPL = new ViewCompatApi18Impl();
      return;
    } 
    if (Build.VERSION.SDK_INT >= 17) {
      IMPL = new ViewCompatApi17Impl();
      return;
    } 
    if (Build.VERSION.SDK_INT >= 16) {
      IMPL = new ViewCompatApi16Impl();
      return;
    } 
    if (Build.VERSION.SDK_INT >= 15) {
      IMPL = new ViewCompatApi15Impl();
      return;
    } 
    IMPL = new ViewCompatBaseImpl();
  }
  
  public static void addKeyboardNavigationClusters(@NonNull View paramView, @NonNull Collection<View> paramCollection, int paramInt) { IMPL.addKeyboardNavigationClusters(paramView, paramCollection, paramInt); }
  
  public static ViewPropertyAnimatorCompat animate(View paramView) { return IMPL.animate(paramView); }
  
  @Deprecated
  public static boolean canScrollHorizontally(View paramView, int paramInt) { return paramView.canScrollHorizontally(paramInt); }
  
  @Deprecated
  public static boolean canScrollVertically(View paramView, int paramInt) { return paramView.canScrollVertically(paramInt); }
  
  public static void cancelDragAndDrop(View paramView) { IMPL.cancelDragAndDrop(paramView); }
  
  @Deprecated
  public static int combineMeasuredStates(int paramInt1, int paramInt2) { return View.combineMeasuredStates(paramInt1, paramInt2); }
  
  public static WindowInsetsCompat dispatchApplyWindowInsets(View paramView, WindowInsetsCompat paramWindowInsetsCompat) { return IMPL.dispatchApplyWindowInsets(paramView, paramWindowInsetsCompat); }
  
  public static void dispatchFinishTemporaryDetach(View paramView) { IMPL.dispatchFinishTemporaryDetach(paramView); }
  
  public static boolean dispatchNestedFling(@NonNull View paramView, float paramFloat1, float paramFloat2, boolean paramBoolean) { return IMPL.dispatchNestedFling(paramView, paramFloat1, paramFloat2, paramBoolean); }
  
  public static boolean dispatchNestedPreFling(@NonNull View paramView, float paramFloat1, float paramFloat2) { return IMPL.dispatchNestedPreFling(paramView, paramFloat1, paramFloat2); }
  
  public static boolean dispatchNestedPreScroll(@NonNull View paramView, int paramInt1, int paramInt2, @Nullable int[] paramArrayOfInt1, @Nullable int[] paramArrayOfInt2) { return IMPL.dispatchNestedPreScroll(paramView, paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2); }
  
  public static boolean dispatchNestedPreScroll(@NonNull View paramView, int paramInt1, int paramInt2, @Nullable int[] paramArrayOfInt1, @Nullable int[] paramArrayOfInt2, int paramInt3) { return (paramView instanceof NestedScrollingChild2) ? ((NestedScrollingChild2)paramView).dispatchNestedPreScroll(paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2, paramInt3) : ((paramInt3 == 0) ? IMPL.dispatchNestedPreScroll(paramView, paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2) : 0); }
  
  public static boolean dispatchNestedScroll(@NonNull View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, @Nullable int[] paramArrayOfInt) { return IMPL.dispatchNestedScroll(paramView, paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt); }
  
  public static boolean dispatchNestedScroll(@NonNull View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, @Nullable int[] paramArrayOfInt, int paramInt5) { return (paramView instanceof NestedScrollingChild2) ? ((NestedScrollingChild2)paramView).dispatchNestedScroll(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt, paramInt5) : ((paramInt5 == 0) ? IMPL.dispatchNestedScroll(paramView, paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt) : 0); }
  
  public static void dispatchStartTemporaryDetach(View paramView) { IMPL.dispatchStartTemporaryDetach(paramView); }
  
  public static int generateViewId() { return IMPL.generateViewId(); }
  
  public static int getAccessibilityLiveRegion(View paramView) { return IMPL.getAccessibilityLiveRegion(paramView); }
  
  public static AccessibilityNodeProviderCompat getAccessibilityNodeProvider(View paramView) { return IMPL.getAccessibilityNodeProvider(paramView); }
  
  @Deprecated
  public static float getAlpha(View paramView) { return paramView.getAlpha(); }
  
  public static ColorStateList getBackgroundTintList(View paramView) { return IMPL.getBackgroundTintList(paramView); }
  
  public static PorterDuff.Mode getBackgroundTintMode(View paramView) { return IMPL.getBackgroundTintMode(paramView); }
  
  public static Rect getClipBounds(View paramView) { return IMPL.getClipBounds(paramView); }
  
  public static Display getDisplay(@NonNull View paramView) { return IMPL.getDisplay(paramView); }
  
  public static float getElevation(View paramView) { return IMPL.getElevation(paramView); }
  
  public static boolean getFitsSystemWindows(View paramView) { return IMPL.getFitsSystemWindows(paramView); }
  
  public static int getImportantForAccessibility(View paramView) { return IMPL.getImportantForAccessibility(paramView); }
  
  public static int getImportantForAutofill(@NonNull View paramView) { return IMPL.getImportantForAutofill(paramView); }
  
  public static int getLabelFor(View paramView) { return IMPL.getLabelFor(paramView); }
  
  @Deprecated
  public static int getLayerType(View paramView) { return paramView.getLayerType(); }
  
  public static int getLayoutDirection(View paramView) { return IMPL.getLayoutDirection(paramView); }
  
  @Deprecated
  @Nullable
  public static Matrix getMatrix(View paramView) { return paramView.getMatrix(); }
  
  @Deprecated
  public static int getMeasuredHeightAndState(View paramView) { return paramView.getMeasuredHeightAndState(); }
  
  @Deprecated
  public static int getMeasuredState(View paramView) { return paramView.getMeasuredState(); }
  
  @Deprecated
  public static int getMeasuredWidthAndState(View paramView) { return paramView.getMeasuredWidthAndState(); }
  
  public static int getMinimumHeight(View paramView) { return IMPL.getMinimumHeight(paramView); }
  
  public static int getMinimumWidth(View paramView) { return IMPL.getMinimumWidth(paramView); }
  
  public static int getNextClusterForwardId(@NonNull View paramView) { return IMPL.getNextClusterForwardId(paramView); }
  
  @Deprecated
  public static int getOverScrollMode(View paramView) { return paramView.getOverScrollMode(); }
  
  public static int getPaddingEnd(View paramView) { return IMPL.getPaddingEnd(paramView); }
  
  public static int getPaddingStart(View paramView) { return IMPL.getPaddingStart(paramView); }
  
  public static ViewParent getParentForAccessibility(View paramView) { return IMPL.getParentForAccessibility(paramView); }
  
  @Deprecated
  public static float getPivotX(View paramView) { return paramView.getPivotX(); }
  
  @Deprecated
  public static float getPivotY(View paramView) { return paramView.getPivotY(); }
  
  @Deprecated
  public static float getRotation(View paramView) { return paramView.getRotation(); }
  
  @Deprecated
  public static float getRotationX(View paramView) { return paramView.getRotationX(); }
  
  @Deprecated
  public static float getRotationY(View paramView) { return paramView.getRotationY(); }
  
  @Deprecated
  public static float getScaleX(View paramView) { return paramView.getScaleX(); }
  
  @Deprecated
  public static float getScaleY(View paramView) { return paramView.getScaleY(); }
  
  public static int getScrollIndicators(@NonNull View paramView) { return IMPL.getScrollIndicators(paramView); }
  
  public static String getTransitionName(View paramView) { return IMPL.getTransitionName(paramView); }
  
  @Deprecated
  public static float getTranslationX(View paramView) { return paramView.getTranslationX(); }
  
  @Deprecated
  public static float getTranslationY(View paramView) { return paramView.getTranslationY(); }
  
  public static float getTranslationZ(View paramView) { return IMPL.getTranslationZ(paramView); }
  
  public static int getWindowSystemUiVisibility(View paramView) { return IMPL.getWindowSystemUiVisibility(paramView); }
  
  @Deprecated
  public static float getX(View paramView) { return paramView.getX(); }
  
  @Deprecated
  public static float getY(View paramView) { return paramView.getY(); }
  
  public static float getZ(View paramView) { return IMPL.getZ(paramView); }
  
  public static boolean hasAccessibilityDelegate(View paramView) { return IMPL.hasAccessibilityDelegate(paramView); }
  
  public static boolean hasExplicitFocusable(@NonNull View paramView) { return IMPL.hasExplicitFocusable(paramView); }
  
  public static boolean hasNestedScrollingParent(@NonNull View paramView) { return IMPL.hasNestedScrollingParent(paramView); }
  
  public static boolean hasNestedScrollingParent(@NonNull View paramView, int paramInt) {
    if (paramView instanceof NestedScrollingChild2) {
      ((NestedScrollingChild2)paramView).hasNestedScrollingParent(paramInt);
    } else if (paramInt == 0) {
      return IMPL.hasNestedScrollingParent(paramView);
    } 
    return false;
  }
  
  public static boolean hasOnClickListeners(View paramView) { return IMPL.hasOnClickListeners(paramView); }
  
  public static boolean hasOverlappingRendering(View paramView) { return IMPL.hasOverlappingRendering(paramView); }
  
  public static boolean hasTransientState(View paramView) { return IMPL.hasTransientState(paramView); }
  
  public static boolean isAttachedToWindow(View paramView) { return IMPL.isAttachedToWindow(paramView); }
  
  public static boolean isFocusedByDefault(@NonNull View paramView) { return IMPL.isFocusedByDefault(paramView); }
  
  public static boolean isImportantForAccessibility(View paramView) { return IMPL.isImportantForAccessibility(paramView); }
  
  public static boolean isImportantForAutofill(@NonNull View paramView) { return IMPL.isImportantForAutofill(paramView); }
  
  public static boolean isInLayout(View paramView) { return IMPL.isInLayout(paramView); }
  
  public static boolean isKeyboardNavigationCluster(@NonNull View paramView) { return IMPL.isKeyboardNavigationCluster(paramView); }
  
  public static boolean isLaidOut(View paramView) { return IMPL.isLaidOut(paramView); }
  
  public static boolean isLayoutDirectionResolved(View paramView) { return IMPL.isLayoutDirectionResolved(paramView); }
  
  public static boolean isNestedScrollingEnabled(@NonNull View paramView) { return IMPL.isNestedScrollingEnabled(paramView); }
  
  @Deprecated
  public static boolean isOpaque(View paramView) { return paramView.isOpaque(); }
  
  public static boolean isPaddingRelative(View paramView) { return IMPL.isPaddingRelative(paramView); }
  
  @Deprecated
  public static void jumpDrawablesToCurrentState(View paramView) { paramView.jumpDrawablesToCurrentState(); }
  
  public static View keyboardNavigationClusterSearch(@NonNull View paramView1, View paramView2, int paramInt) { return IMPL.keyboardNavigationClusterSearch(paramView1, paramView2, paramInt); }
  
  public static void offsetLeftAndRight(View paramView, int paramInt) { IMPL.offsetLeftAndRight(paramView, paramInt); }
  
  public static void offsetTopAndBottom(View paramView, int paramInt) { IMPL.offsetTopAndBottom(paramView, paramInt); }
  
  public static WindowInsetsCompat onApplyWindowInsets(View paramView, WindowInsetsCompat paramWindowInsetsCompat) { return IMPL.onApplyWindowInsets(paramView, paramWindowInsetsCompat); }
  
  @Deprecated
  public static void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent) { paramView.onInitializeAccessibilityEvent(paramAccessibilityEvent); }
  
  public static void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat) { IMPL.onInitializeAccessibilityNodeInfo(paramView, paramAccessibilityNodeInfoCompat); }
  
  @Deprecated
  public static void onPopulateAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent) { paramView.onPopulateAccessibilityEvent(paramAccessibilityEvent); }
  
  public static boolean performAccessibilityAction(View paramView, int paramInt, Bundle paramBundle) { return IMPL.performAccessibilityAction(paramView, paramInt, paramBundle); }
  
  public static void postInvalidateOnAnimation(View paramView) { IMPL.postInvalidateOnAnimation(paramView); }
  
  public static void postInvalidateOnAnimation(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { IMPL.postInvalidateOnAnimation(paramView, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public static void postOnAnimation(View paramView, Runnable paramRunnable) { IMPL.postOnAnimation(paramView, paramRunnable); }
  
  public static void postOnAnimationDelayed(View paramView, Runnable paramRunnable, long paramLong) { IMPL.postOnAnimationDelayed(paramView, paramRunnable, paramLong); }
  
  public static void requestApplyInsets(View paramView) { IMPL.requestApplyInsets(paramView); }
  
  @NonNull
  public static <T extends View> T requireViewById(@NonNull View paramView, @IdRes int paramInt) {
    paramView = paramView.findViewById(paramInt);
    if (paramView == null)
      throw new IllegalArgumentException("ID does not reference a View inside this View"); 
    return (T)paramView;
  }
  
  @Deprecated
  public static int resolveSizeAndState(int paramInt1, int paramInt2, int paramInt3) { return View.resolveSizeAndState(paramInt1, paramInt2, paramInt3); }
  
  public static boolean restoreDefaultFocus(@NonNull View paramView) { return IMPL.restoreDefaultFocus(paramView); }
  
  public static void setAccessibilityDelegate(View paramView, AccessibilityDelegateCompat paramAccessibilityDelegateCompat) { IMPL.setAccessibilityDelegate(paramView, paramAccessibilityDelegateCompat); }
  
  public static void setAccessibilityLiveRegion(View paramView, int paramInt) { IMPL.setAccessibilityLiveRegion(paramView, paramInt); }
  
  @Deprecated
  public static void setActivated(View paramView, boolean paramBoolean) { paramView.setActivated(paramBoolean); }
  
  @Deprecated
  public static void setAlpha(View paramView, @FloatRange(from = 0.0D, to = 1.0D) float paramFloat) { paramView.setAlpha(paramFloat); }
  
  public static void setAutofillHints(@NonNull View paramView, @Nullable String... paramVarArgs) { IMPL.setAutofillHints(paramView, paramVarArgs); }
  
  public static void setBackground(View paramView, Drawable paramDrawable) { IMPL.setBackground(paramView, paramDrawable); }
  
  public static void setBackgroundTintList(View paramView, ColorStateList paramColorStateList) { IMPL.setBackgroundTintList(paramView, paramColorStateList); }
  
  public static void setBackgroundTintMode(View paramView, PorterDuff.Mode paramMode) { IMPL.setBackgroundTintMode(paramView, paramMode); }
  
  @Deprecated
  public static void setChildrenDrawingOrderEnabled(ViewGroup paramViewGroup, boolean paramBoolean) { IMPL.setChildrenDrawingOrderEnabled(paramViewGroup, paramBoolean); }
  
  public static void setClipBounds(View paramView, Rect paramRect) { IMPL.setClipBounds(paramView, paramRect); }
  
  public static void setElevation(View paramView, float paramFloat) { IMPL.setElevation(paramView, paramFloat); }
  
  @Deprecated
  public static void setFitsSystemWindows(View paramView, boolean paramBoolean) { paramView.setFitsSystemWindows(paramBoolean); }
  
  public static void setFocusedByDefault(@NonNull View paramView, boolean paramBoolean) { IMPL.setFocusedByDefault(paramView, paramBoolean); }
  
  public static void setHasTransientState(View paramView, boolean paramBoolean) { IMPL.setHasTransientState(paramView, paramBoolean); }
  
  public static void setImportantForAccessibility(View paramView, int paramInt) { IMPL.setImportantForAccessibility(paramView, paramInt); }
  
  public static void setImportantForAutofill(@NonNull View paramView, int paramInt) { IMPL.setImportantForAutofill(paramView, paramInt); }
  
  public static void setKeyboardNavigationCluster(@NonNull View paramView, boolean paramBoolean) { IMPL.setKeyboardNavigationCluster(paramView, paramBoolean); }
  
  public static void setLabelFor(View paramView, @IdRes int paramInt) { IMPL.setLabelFor(paramView, paramInt); }
  
  public static void setLayerPaint(View paramView, Paint paramPaint) { IMPL.setLayerPaint(paramView, paramPaint); }
  
  @Deprecated
  public static void setLayerType(View paramView, int paramInt, Paint paramPaint) { paramView.setLayerType(paramInt, paramPaint); }
  
  public static void setLayoutDirection(View paramView, int paramInt) { IMPL.setLayoutDirection(paramView, paramInt); }
  
  public static void setNestedScrollingEnabled(@NonNull View paramView, boolean paramBoolean) { IMPL.setNestedScrollingEnabled(paramView, paramBoolean); }
  
  public static void setNextClusterForwardId(@NonNull View paramView, int paramInt) { IMPL.setNextClusterForwardId(paramView, paramInt); }
  
  public static void setOnApplyWindowInsetsListener(View paramView, OnApplyWindowInsetsListener paramOnApplyWindowInsetsListener) { IMPL.setOnApplyWindowInsetsListener(paramView, paramOnApplyWindowInsetsListener); }
  
  @Deprecated
  public static void setOverScrollMode(View paramView, int paramInt) { paramView.setOverScrollMode(paramInt); }
  
  public static void setPaddingRelative(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { IMPL.setPaddingRelative(paramView, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  @Deprecated
  public static void setPivotX(View paramView, float paramFloat) { paramView.setPivotX(paramFloat); }
  
  @Deprecated
  public static void setPivotY(View paramView, float paramFloat) { paramView.setPivotY(paramFloat); }
  
  public static void setPointerIcon(@NonNull View paramView, PointerIconCompat paramPointerIconCompat) { IMPL.setPointerIcon(paramView, paramPointerIconCompat); }
  
  @Deprecated
  public static void setRotation(View paramView, float paramFloat) { paramView.setRotation(paramFloat); }
  
  @Deprecated
  public static void setRotationX(View paramView, float paramFloat) { paramView.setRotationX(paramFloat); }
  
  @Deprecated
  public static void setRotationY(View paramView, float paramFloat) { paramView.setRotationY(paramFloat); }
  
  @Deprecated
  public static void setSaveFromParentEnabled(View paramView, boolean paramBoolean) { paramView.setSaveFromParentEnabled(paramBoolean); }
  
  @Deprecated
  public static void setScaleX(View paramView, float paramFloat) { paramView.setScaleX(paramFloat); }
  
  @Deprecated
  public static void setScaleY(View paramView, float paramFloat) { paramView.setScaleY(paramFloat); }
  
  public static void setScrollIndicators(@NonNull View paramView, int paramInt) { IMPL.setScrollIndicators(paramView, paramInt); }
  
  public static void setScrollIndicators(@NonNull View paramView, int paramInt1, int paramInt2) { IMPL.setScrollIndicators(paramView, paramInt1, paramInt2); }
  
  public static void setTooltipText(@NonNull View paramView, @Nullable CharSequence paramCharSequence) { IMPL.setTooltipText(paramView, paramCharSequence); }
  
  public static void setTransitionName(View paramView, String paramString) { IMPL.setTransitionName(paramView, paramString); }
  
  @Deprecated
  public static void setTranslationX(View paramView, float paramFloat) { paramView.setTranslationX(paramFloat); }
  
  @Deprecated
  public static void setTranslationY(View paramView, float paramFloat) { paramView.setTranslationY(paramFloat); }
  
  public static void setTranslationZ(View paramView, float paramFloat) { IMPL.setTranslationZ(paramView, paramFloat); }
  
  @Deprecated
  public static void setX(View paramView, float paramFloat) { paramView.setX(paramFloat); }
  
  @Deprecated
  public static void setY(View paramView, float paramFloat) { paramView.setY(paramFloat); }
  
  public static void setZ(View paramView, float paramFloat) { IMPL.setZ(paramView, paramFloat); }
  
  public static boolean startDragAndDrop(View paramView, ClipData paramClipData, View.DragShadowBuilder paramDragShadowBuilder, Object paramObject, int paramInt) { return IMPL.startDragAndDrop(paramView, paramClipData, paramDragShadowBuilder, paramObject, paramInt); }
  
  public static boolean startNestedScroll(@NonNull View paramView, int paramInt) { return IMPL.startNestedScroll(paramView, paramInt); }
  
  public static boolean startNestedScroll(@NonNull View paramView, int paramInt1, int paramInt2) { return (paramView instanceof NestedScrollingChild2) ? ((NestedScrollingChild2)paramView).startNestedScroll(paramInt1, paramInt2) : ((paramInt2 == 0) ? IMPL.startNestedScroll(paramView, paramInt1) : 0); }
  
  public static void stopNestedScroll(@NonNull View paramView) { IMPL.stopNestedScroll(paramView); }
  
  public static void stopNestedScroll(@NonNull View paramView, int paramInt) {
    if (paramView instanceof NestedScrollingChild2) {
      ((NestedScrollingChild2)paramView).stopNestedScroll(paramInt);
      return;
    } 
    if (paramInt == 0)
      IMPL.stopNestedScroll(paramView); 
  }
  
  public static void updateDragShadow(View paramView, View.DragShadowBuilder paramDragShadowBuilder) { IMPL.updateDragShadow(paramView, paramDragShadowBuilder); }
  
  @Retention(RetentionPolicy.SOURCE)
  private static @interface AccessibilityLiveRegion {}
  
  @Retention(RetentionPolicy.SOURCE)
  @TargetApi(26)
  private static @interface AutofillImportance {}
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface FocusDirection {}
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface FocusRealDirection {}
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface FocusRelativeDirection {}
  
  @Retention(RetentionPolicy.SOURCE)
  private static @interface ImportantForAccessibility {}
  
  @Retention(RetentionPolicy.SOURCE)
  private static @interface LayerType {}
  
  @Retention(RetentionPolicy.SOURCE)
  private static @interface LayoutDirectionMode {}
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface NestedScrollType {}
  
  @Retention(RetentionPolicy.SOURCE)
  private static @interface OverScroll {}
  
  @Retention(RetentionPolicy.SOURCE)
  private static @interface ResolvedLayoutDirectionMode {}
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface ScrollAxis {}
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface ScrollIndicators {}
  
  @RequiresApi(15)
  static class ViewCompatApi15Impl extends ViewCompatBaseImpl {
    public boolean hasOnClickListeners(View param1View) { return param1View.hasOnClickListeners(); }
  }
  
  @RequiresApi(16)
  static class ViewCompatApi16Impl extends ViewCompatApi15Impl {
    public AccessibilityNodeProviderCompat getAccessibilityNodeProvider(View param1View) {
      AccessibilityNodeProvider accessibilityNodeProvider = param1View.getAccessibilityNodeProvider();
      return (accessibilityNodeProvider != null) ? new AccessibilityNodeProviderCompat(accessibilityNodeProvider) : null;
    }
    
    public boolean getFitsSystemWindows(View param1View) { return param1View.getFitsSystemWindows(); }
    
    public int getImportantForAccessibility(View param1View) { return param1View.getImportantForAccessibility(); }
    
    public int getMinimumHeight(View param1View) { return param1View.getMinimumHeight(); }
    
    public int getMinimumWidth(View param1View) { return param1View.getMinimumWidth(); }
    
    public ViewParent getParentForAccessibility(View param1View) { return param1View.getParentForAccessibility(); }
    
    public boolean hasOverlappingRendering(View param1View) { return param1View.hasOverlappingRendering(); }
    
    public boolean hasTransientState(View param1View) { return param1View.hasTransientState(); }
    
    public boolean performAccessibilityAction(View param1View, int param1Int, Bundle param1Bundle) { return param1View.performAccessibilityAction(param1Int, param1Bundle); }
    
    public void postInvalidateOnAnimation(View param1View) { param1View.postInvalidateOnAnimation(); }
    
    public void postInvalidateOnAnimation(View param1View, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { param1View.postInvalidateOnAnimation(param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void postOnAnimation(View param1View, Runnable param1Runnable) { param1View.postOnAnimation(param1Runnable); }
    
    public void postOnAnimationDelayed(View param1View, Runnable param1Runnable, long param1Long) { param1View.postOnAnimationDelayed(param1Runnable, param1Long); }
    
    public void requestApplyInsets(View param1View) { param1View.requestFitSystemWindows(); }
    
    public void setBackground(View param1View, Drawable param1Drawable) { param1View.setBackground(param1Drawable); }
    
    public void setHasTransientState(View param1View, boolean param1Boolean) { param1View.setHasTransientState(param1Boolean); }
    
    public void setImportantForAccessibility(View param1View, int param1Int) {
      int i = param1Int;
      if (param1Int == 4)
        i = 2; 
      param1View.setImportantForAccessibility(i);
    }
  }
  
  @RequiresApi(17)
  static class ViewCompatApi17Impl extends ViewCompatApi16Impl {
    public int generateViewId() { return View.generateViewId(); }
    
    public Display getDisplay(View param1View) { return param1View.getDisplay(); }
    
    public int getLabelFor(View param1View) { return param1View.getLabelFor(); }
    
    public int getLayoutDirection(View param1View) { return param1View.getLayoutDirection(); }
    
    public int getPaddingEnd(View param1View) { return param1View.getPaddingEnd(); }
    
    public int getPaddingStart(View param1View) { return param1View.getPaddingStart(); }
    
    public int getWindowSystemUiVisibility(View param1View) { return param1View.getWindowSystemUiVisibility(); }
    
    public boolean isPaddingRelative(View param1View) { return param1View.isPaddingRelative(); }
    
    public void setLabelFor(View param1View, int param1Int) { param1View.setLabelFor(param1Int); }
    
    public void setLayerPaint(View param1View, Paint param1Paint) { param1View.setLayerPaint(param1Paint); }
    
    public void setLayoutDirection(View param1View, int param1Int) { param1View.setLayoutDirection(param1Int); }
    
    public void setPaddingRelative(View param1View, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { param1View.setPaddingRelative(param1Int1, param1Int2, param1Int3, param1Int4); }
  }
  
  @RequiresApi(18)
  static class ViewCompatApi18Impl extends ViewCompatApi17Impl {
    public Rect getClipBounds(View param1View) { return param1View.getClipBounds(); }
    
    public boolean isInLayout(View param1View) { return param1View.isInLayout(); }
    
    public void setClipBounds(View param1View, Rect param1Rect) { param1View.setClipBounds(param1Rect); }
  }
  
  @RequiresApi(19)
  static class ViewCompatApi19Impl extends ViewCompatApi18Impl {
    public int getAccessibilityLiveRegion(View param1View) { return param1View.getAccessibilityLiveRegion(); }
    
    public boolean isAttachedToWindow(View param1View) { return param1View.isAttachedToWindow(); }
    
    public boolean isLaidOut(View param1View) { return param1View.isLaidOut(); }
    
    public boolean isLayoutDirectionResolved(View param1View) { return param1View.isLayoutDirectionResolved(); }
    
    public void setAccessibilityLiveRegion(View param1View, int param1Int) { param1View.setAccessibilityLiveRegion(param1Int); }
    
    public void setImportantForAccessibility(View param1View, int param1Int) { param1View.setImportantForAccessibility(param1Int); }
  }
  
  @RequiresApi(21)
  static class ViewCompatApi21Impl extends ViewCompatApi19Impl {
    private static ThreadLocal<Rect> sThreadLocalRect;
    
    private static Rect getEmptyTempRect() {
      if (sThreadLocalRect == null)
        sThreadLocalRect = new ThreadLocal(); 
      Rect rect2 = (Rect)sThreadLocalRect.get();
      Rect rect1 = rect2;
      if (rect2 == null) {
        rect1 = new Rect();
        sThreadLocalRect.set(rect1);
      } 
      rect1.setEmpty();
      return rect1;
    }
    
    public WindowInsetsCompat dispatchApplyWindowInsets(View param1View, WindowInsetsCompat param1WindowInsetsCompat) {
      WindowInsets windowInsets2 = (WindowInsets)WindowInsetsCompat.unwrap(param1WindowInsetsCompat);
      WindowInsets windowInsets3 = param1View.dispatchApplyWindowInsets(windowInsets2);
      WindowInsets windowInsets1 = windowInsets2;
      if (windowInsets3 != windowInsets2)
        windowInsets1 = new WindowInsets(windowInsets3); 
      return WindowInsetsCompat.wrap(windowInsets1);
    }
    
    public boolean dispatchNestedFling(View param1View, float param1Float1, float param1Float2, boolean param1Boolean) { return param1View.dispatchNestedFling(param1Float1, param1Float2, param1Boolean); }
    
    public boolean dispatchNestedPreFling(View param1View, float param1Float1, float param1Float2) { return param1View.dispatchNestedPreFling(param1Float1, param1Float2); }
    
    public boolean dispatchNestedPreScroll(View param1View, int param1Int1, int param1Int2, int[] param1ArrayOfInt1, int[] param1ArrayOfInt2) { return param1View.dispatchNestedPreScroll(param1Int1, param1Int2, param1ArrayOfInt1, param1ArrayOfInt2); }
    
    public boolean dispatchNestedScroll(View param1View, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int[] param1ArrayOfInt) { return param1View.dispatchNestedScroll(param1Int1, param1Int2, param1Int3, param1Int4, param1ArrayOfInt); }
    
    public ColorStateList getBackgroundTintList(View param1View) { return param1View.getBackgroundTintList(); }
    
    public PorterDuff.Mode getBackgroundTintMode(View param1View) { return param1View.getBackgroundTintMode(); }
    
    public float getElevation(View param1View) { return param1View.getElevation(); }
    
    public String getTransitionName(View param1View) { return param1View.getTransitionName(); }
    
    public float getTranslationZ(View param1View) { return param1View.getTranslationZ(); }
    
    public float getZ(View param1View) { return param1View.getZ(); }
    
    public boolean hasNestedScrollingParent(View param1View) { return param1View.hasNestedScrollingParent(); }
    
    public boolean isImportantForAccessibility(View param1View) { return param1View.isImportantForAccessibility(); }
    
    public boolean isNestedScrollingEnabled(View param1View) { return param1View.isNestedScrollingEnabled(); }
    
    public void offsetLeftAndRight(View param1View, int param1Int) {
      boolean bool;
      Rect rect = getEmptyTempRect();
      ViewParent viewParent = param1View.getParent();
      if (viewParent instanceof View) {
        View view = (View)viewParent;
        rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        bool = rect.intersects(param1View.getLeft(), param1View.getTop(), param1View.getRight(), param1View.getBottom()) ^ true;
      } else {
        bool = false;
      } 
      super.offsetLeftAndRight(param1View, param1Int);
      if (bool && rect.intersect(param1View.getLeft(), param1View.getTop(), param1View.getRight(), param1View.getBottom()))
        ((View)viewParent).invalidate(rect); 
    }
    
    public void offsetTopAndBottom(View param1View, int param1Int) {
      boolean bool;
      Rect rect = getEmptyTempRect();
      ViewParent viewParent = param1View.getParent();
      if (viewParent instanceof View) {
        View view = (View)viewParent;
        rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        bool = rect.intersects(param1View.getLeft(), param1View.getTop(), param1View.getRight(), param1View.getBottom()) ^ true;
      } else {
        bool = false;
      } 
      super.offsetTopAndBottom(param1View, param1Int);
      if (bool && rect.intersect(param1View.getLeft(), param1View.getTop(), param1View.getRight(), param1View.getBottom()))
        ((View)viewParent).invalidate(rect); 
    }
    
    public WindowInsetsCompat onApplyWindowInsets(View param1View, WindowInsetsCompat param1WindowInsetsCompat) {
      WindowInsets windowInsets2 = (WindowInsets)WindowInsetsCompat.unwrap(param1WindowInsetsCompat);
      WindowInsets windowInsets3 = param1View.onApplyWindowInsets(windowInsets2);
      WindowInsets windowInsets1 = windowInsets2;
      if (windowInsets3 != windowInsets2)
        windowInsets1 = new WindowInsets(windowInsets3); 
      return WindowInsetsCompat.wrap(windowInsets1);
    }
    
    public void requestApplyInsets(View param1View) { param1View.requestApplyInsets(); }
    
    public void setBackgroundTintList(View param1View, ColorStateList param1ColorStateList) {
      param1View.setBackgroundTintList(param1ColorStateList);
      if (Build.VERSION.SDK_INT == 21) {
        boolean bool;
        Drawable drawable = param1View.getBackground();
        if (param1View.getBackgroundTintList() != null || param1View.getBackgroundTintMode() != null) {
          bool = true;
        } else {
          bool = false;
        } 
        if (drawable != null && bool) {
          if (drawable.isStateful())
            drawable.setState(param1View.getDrawableState()); 
          param1View.setBackground(drawable);
        } 
      } 
    }
    
    public void setBackgroundTintMode(View param1View, PorterDuff.Mode param1Mode) {
      param1View.setBackgroundTintMode(param1Mode);
      if (Build.VERSION.SDK_INT == 21) {
        boolean bool;
        Drawable drawable = param1View.getBackground();
        if (param1View.getBackgroundTintList() != null || param1View.getBackgroundTintMode() != null) {
          bool = true;
        } else {
          bool = false;
        } 
        if (drawable != null && bool) {
          if (drawable.isStateful())
            drawable.setState(param1View.getDrawableState()); 
          param1View.setBackground(drawable);
        } 
      } 
    }
    
    public void setElevation(View param1View, float param1Float) { param1View.setElevation(param1Float); }
    
    public void setNestedScrollingEnabled(View param1View, boolean param1Boolean) { param1View.setNestedScrollingEnabled(param1Boolean); }
    
    public void setOnApplyWindowInsetsListener(View param1View, final OnApplyWindowInsetsListener listener) {
      if (param1OnApplyWindowInsetsListener == null) {
        param1View.setOnApplyWindowInsetsListener(null);
        return;
      } 
      param1View.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            public WindowInsets onApplyWindowInsets(View param2View, WindowInsets param2WindowInsets) {
              WindowInsetsCompat windowInsetsCompat;
              return (WindowInsets)(windowInsetsCompat = WindowInsetsCompat.wrap(param2WindowInsets)).unwrap(listener.onApplyWindowInsets(param2View, windowInsetsCompat));
            }
          });
    }
    
    public void setTransitionName(View param1View, String param1String) { param1View.setTransitionName(param1String); }
    
    public void setTranslationZ(View param1View, float param1Float) { param1View.setTranslationZ(param1Float); }
    
    public void setZ(View param1View, float param1Float) { param1View.setZ(param1Float); }
    
    public boolean startNestedScroll(View param1View, int param1Int) { return param1View.startNestedScroll(param1Int); }
    
    public void stopNestedScroll(View param1View) { param1View.stopNestedScroll(); }
  }
  
  class null implements View.OnApplyWindowInsetsListener {
    public WindowInsets onApplyWindowInsets(View param1View, WindowInsets param1WindowInsets) {
      WindowInsetsCompat windowInsetsCompat;
      return (WindowInsets)(windowInsetsCompat = WindowInsetsCompat.wrap(param1WindowInsets)).unwrap(listener.onApplyWindowInsets(param1View, windowInsetsCompat));
    }
  }
  
  @RequiresApi(23)
  static class ViewCompatApi23Impl extends ViewCompatApi21Impl {
    public int getScrollIndicators(View param1View) { return param1View.getScrollIndicators(); }
    
    public void offsetLeftAndRight(View param1View, int param1Int) { param1View.offsetLeftAndRight(param1Int); }
    
    public void offsetTopAndBottom(View param1View, int param1Int) { param1View.offsetTopAndBottom(param1Int); }
    
    public void setScrollIndicators(View param1View, int param1Int) { param1View.setScrollIndicators(param1Int); }
    
    public void setScrollIndicators(View param1View, int param1Int1, int param1Int2) { param1View.setScrollIndicators(param1Int1, param1Int2); }
  }
  
  @RequiresApi(24)
  static class ViewCompatApi24Impl extends ViewCompatApi23Impl {
    public void cancelDragAndDrop(View param1View) { param1View.cancelDragAndDrop(); }
    
    public void dispatchFinishTemporaryDetach(View param1View) { param1View.dispatchFinishTemporaryDetach(); }
    
    public void dispatchStartTemporaryDetach(View param1View) { param1View.dispatchStartTemporaryDetach(); }
    
    public void setPointerIcon(View param1View, PointerIconCompat param1PointerIconCompat) {
      if (param1PointerIconCompat != null) {
        Object object = param1PointerIconCompat.getPointerIcon();
      } else {
        param1PointerIconCompat = null;
      } 
      param1View.setPointerIcon((PointerIcon)param1PointerIconCompat);
    }
    
    public boolean startDragAndDrop(View param1View, ClipData param1ClipData, View.DragShadowBuilder param1DragShadowBuilder, Object param1Object, int param1Int) { return param1View.startDragAndDrop(param1ClipData, param1DragShadowBuilder, param1Object, param1Int); }
    
    public void updateDragShadow(View param1View, View.DragShadowBuilder param1DragShadowBuilder) { param1View.updateDragShadow(param1DragShadowBuilder); }
  }
  
  @RequiresApi(26)
  static class ViewCompatApi26Impl extends ViewCompatApi24Impl {
    public void addKeyboardNavigationClusters(@NonNull View param1View, @NonNull Collection<View> param1Collection, int param1Int) { param1View.addKeyboardNavigationClusters(param1Collection, param1Int); }
    
    public int getImportantForAutofill(@NonNull View param1View) { return param1View.getImportantForAutofill(); }
    
    public int getNextClusterForwardId(@NonNull View param1View) { return param1View.getNextClusterForwardId(); }
    
    public boolean hasExplicitFocusable(@NonNull View param1View) { return param1View.hasExplicitFocusable(); }
    
    public boolean isFocusedByDefault(@NonNull View param1View) { return param1View.isFocusedByDefault(); }
    
    public boolean isImportantForAutofill(@NonNull View param1View) { return param1View.isImportantForAutofill(); }
    
    public boolean isKeyboardNavigationCluster(@NonNull View param1View) { return param1View.isKeyboardNavigationCluster(); }
    
    public View keyboardNavigationClusterSearch(@NonNull View param1View1, View param1View2, int param1Int) { return param1View1.keyboardNavigationClusterSearch(param1View2, param1Int); }
    
    public boolean restoreDefaultFocus(@NonNull View param1View) { return param1View.restoreDefaultFocus(); }
    
    public void setAutofillHints(@NonNull View param1View, @Nullable String... param1VarArgs) { param1View.setAutofillHints(param1VarArgs); }
    
    public void setFocusedByDefault(@NonNull View param1View, boolean param1Boolean) { param1View.setFocusedByDefault(param1Boolean); }
    
    public void setImportantForAutofill(@NonNull View param1View, int param1Int) { param1View.setImportantForAutofill(param1Int); }
    
    public void setKeyboardNavigationCluster(@NonNull View param1View, boolean param1Boolean) { param1View.setKeyboardNavigationCluster(param1Boolean); }
    
    public void setNextClusterForwardId(@NonNull View param1View, int param1Int) { param1View.setNextClusterForwardId(param1Int); }
    
    public void setTooltipText(View param1View, CharSequence param1CharSequence) { param1View.setTooltipText(param1CharSequence); }
  }
  
  static class ViewCompatBaseImpl {
    static boolean sAccessibilityDelegateCheckFailed;
    
    static Field sAccessibilityDelegateField;
    
    private static Method sChildrenDrawingOrderMethod;
    
    private static Field sMinHeightField;
    
    private static boolean sMinHeightFieldFetched;
    
    private static Field sMinWidthField;
    
    private static boolean sMinWidthFieldFetched;
    
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    
    private static WeakHashMap<View, String> sTransitionNameMap;
    
    private Method mDispatchFinishTemporaryDetach;
    
    private Method mDispatchStartTemporaryDetach;
    
    private boolean mTempDetachBound;
    
    WeakHashMap<View, ViewPropertyAnimatorCompat> mViewPropertyAnimatorCompatMap = null;
    
    private void bindTempDetach() {
      try {
        this.mDispatchStartTemporaryDetach = View.class.getDeclaredMethod("dispatchStartTemporaryDetach", new Class[0]);
        this.mDispatchFinishTemporaryDetach = View.class.getDeclaredMethod("dispatchFinishTemporaryDetach", new Class[0]);
      } catch (NoSuchMethodException noSuchMethodException) {
        Log.e("ViewCompat", "Couldn't find method", noSuchMethodException);
      } 
      this.mTempDetachBound = true;
    }
    
    private static void tickleInvalidationFlag(View param1View) {
      float f = param1View.getTranslationY();
      param1View.setTranslationY(1.0F + f);
      param1View.setTranslationY(f);
    }
    
    public void addKeyboardNavigationClusters(@NonNull View param1View, @NonNull Collection<View> param1Collection, int param1Int) {}
    
    public ViewPropertyAnimatorCompat animate(View param1View) {
      if (this.mViewPropertyAnimatorCompatMap == null)
        this.mViewPropertyAnimatorCompatMap = new WeakHashMap(); 
      ViewPropertyAnimatorCompat viewPropertyAnimatorCompat2 = (ViewPropertyAnimatorCompat)this.mViewPropertyAnimatorCompatMap.get(param1View);
      ViewPropertyAnimatorCompat viewPropertyAnimatorCompat1 = viewPropertyAnimatorCompat2;
      if (viewPropertyAnimatorCompat2 == null) {
        viewPropertyAnimatorCompat1 = new ViewPropertyAnimatorCompat(param1View);
        this.mViewPropertyAnimatorCompatMap.put(param1View, viewPropertyAnimatorCompat1);
      } 
      return viewPropertyAnimatorCompat1;
    }
    
    public void cancelDragAndDrop(View param1View) {}
    
    public WindowInsetsCompat dispatchApplyWindowInsets(View param1View, WindowInsetsCompat param1WindowInsetsCompat) { return param1WindowInsetsCompat; }
    
    public void dispatchFinishTemporaryDetach(View param1View) {
      if (!this.mTempDetachBound)
        bindTempDetach(); 
      if (this.mDispatchFinishTemporaryDetach != null)
        try {
          this.mDispatchFinishTemporaryDetach.invoke(param1View, new Object[0]);
          return;
        } catch (Exception param1View) {
          Log.d("ViewCompat", "Error calling dispatchFinishTemporaryDetach", param1View);
          return;
        }  
      param1View.onFinishTemporaryDetach();
    }
    
    public boolean dispatchNestedFling(View param1View, float param1Float1, float param1Float2, boolean param1Boolean) { return (param1View instanceof NestedScrollingChild) ? ((NestedScrollingChild)param1View).dispatchNestedFling(param1Float1, param1Float2, param1Boolean) : 0; }
    
    public boolean dispatchNestedPreFling(View param1View, float param1Float1, float param1Float2) { return (param1View instanceof NestedScrollingChild) ? ((NestedScrollingChild)param1View).dispatchNestedPreFling(param1Float1, param1Float2) : 0; }
    
    public boolean dispatchNestedPreScroll(View param1View, int param1Int1, int param1Int2, int[] param1ArrayOfInt1, int[] param1ArrayOfInt2) { return (param1View instanceof NestedScrollingChild) ? ((NestedScrollingChild)param1View).dispatchNestedPreScroll(param1Int1, param1Int2, param1ArrayOfInt1, param1ArrayOfInt2) : 0; }
    
    public boolean dispatchNestedScroll(View param1View, int param1Int1, int param1Int2, int param1Int3, int param1Int4, int[] param1ArrayOfInt) { return (param1View instanceof NestedScrollingChild) ? ((NestedScrollingChild)param1View).dispatchNestedScroll(param1Int1, param1Int2, param1Int3, param1Int4, param1ArrayOfInt) : 0; }
    
    public void dispatchStartTemporaryDetach(View param1View) {
      if (!this.mTempDetachBound)
        bindTempDetach(); 
      if (this.mDispatchStartTemporaryDetach != null)
        try {
          this.mDispatchStartTemporaryDetach.invoke(param1View, new Object[0]);
          return;
        } catch (Exception param1View) {
          Log.d("ViewCompat", "Error calling dispatchStartTemporaryDetach", param1View);
          return;
        }  
      param1View.onStartTemporaryDetach();
    }
    
    public int generateViewId() {
      int j;
      int i;
      do {
        j = sNextGeneratedId.get();
        int k = j + 1;
        i = k;
        if (k <= 16777215)
          continue; 
        i = 1;
      } while (!sNextGeneratedId.compareAndSet(j, i));
      return j;
    }
    
    public int getAccessibilityLiveRegion(View param1View) { return 0; }
    
    public AccessibilityNodeProviderCompat getAccessibilityNodeProvider(View param1View) { return null; }
    
    public ColorStateList getBackgroundTintList(View param1View) { return (param1View instanceof TintableBackgroundView) ? ((TintableBackgroundView)param1View).getSupportBackgroundTintList() : null; }
    
    public PorterDuff.Mode getBackgroundTintMode(View param1View) { return (param1View instanceof TintableBackgroundView) ? ((TintableBackgroundView)param1View).getSupportBackgroundTintMode() : null; }
    
    public Rect getClipBounds(View param1View) { return null; }
    
    public Display getDisplay(View param1View) { return isAttachedToWindow(param1View) ? ((WindowManager)param1View.getContext().getSystemService("window")).getDefaultDisplay() : null; }
    
    public float getElevation(View param1View) { return 0.0F; }
    
    public boolean getFitsSystemWindows(View param1View) { return false; }
    
    long getFrameTime() { return ValueAnimator.getFrameDelay(); }
    
    public int getImportantForAccessibility(View param1View) { return 0; }
    
    @TargetApi(26)
    public int getImportantForAutofill(@NonNull View param1View) { return 0; }
    
    public int getLabelFor(View param1View) { return 0; }
    
    public int getLayoutDirection(View param1View) { return 0; }
    
    public int getMinimumHeight(View param1View) {
      if (!sMinHeightFieldFetched) {
        try {
          sMinHeightField = View.class.getDeclaredField("mMinHeight");
          sMinHeightField.setAccessible(true);
        } catch (NoSuchFieldException noSuchFieldException) {}
        sMinHeightFieldFetched = true;
      } 
      if (sMinHeightField != null)
        try {
          return ((Integer)sMinHeightField.get(param1View)).intValue();
        } catch (Exception param1View) {} 
      return 0;
    }
    
    public int getMinimumWidth(View param1View) {
      if (!sMinWidthFieldFetched) {
        try {
          sMinWidthField = View.class.getDeclaredField("mMinWidth");
          sMinWidthField.setAccessible(true);
        } catch (NoSuchFieldException noSuchFieldException) {}
        sMinWidthFieldFetched = true;
      } 
      if (sMinWidthField != null)
        try {
          return ((Integer)sMinWidthField.get(param1View)).intValue();
        } catch (Exception param1View) {} 
      return 0;
    }
    
    public int getNextClusterForwardId(@NonNull View param1View) { return -1; }
    
    public int getPaddingEnd(View param1View) { return param1View.getPaddingRight(); }
    
    public int getPaddingStart(View param1View) { return param1View.getPaddingLeft(); }
    
    public ViewParent getParentForAccessibility(View param1View) { return param1View.getParent(); }
    
    public int getScrollIndicators(View param1View) { return 0; }
    
    public String getTransitionName(View param1View) { return (sTransitionNameMap == null) ? null : (String)sTransitionNameMap.get(param1View); }
    
    public float getTranslationZ(View param1View) { return 0.0F; }
    
    public int getWindowSystemUiVisibility(View param1View) { return 0; }
    
    public float getZ(View param1View) { return getTranslationZ(param1View) + getElevation(param1View); }
    
    public boolean hasAccessibilityDelegate(View param1View) { // Byte code:
      //   0: getstatic android/support/v4/view/ViewCompat$ViewCompatBaseImpl.sAccessibilityDelegateCheckFailed : Z
      //   3: istore_3
      //   4: iconst_0
      //   5: istore_2
      //   6: iload_3
      //   7: ifeq -> 12
      //   10: iconst_0
      //   11: ireturn
      //   12: getstatic android/support/v4/view/ViewCompat$ViewCompatBaseImpl.sAccessibilityDelegateField : Ljava/lang/reflect/Field;
      //   15: ifnonnull -> 45
      //   18: ldc android/view/View
      //   20: ldc_w 'mAccessibilityDelegate'
      //   23: invokevirtual getDeclaredField : (Ljava/lang/String;)Ljava/lang/reflect/Field;
      //   26: putstatic android/support/v4/view/ViewCompat$ViewCompatBaseImpl.sAccessibilityDelegateField : Ljava/lang/reflect/Field;
      //   29: getstatic android/support/v4/view/ViewCompat$ViewCompatBaseImpl.sAccessibilityDelegateField : Ljava/lang/reflect/Field;
      //   32: iconst_1
      //   33: invokevirtual setAccessible : (Z)V
      //   36: goto -> 45
      //   39: iconst_1
      //   40: putstatic android/support/v4/view/ViewCompat$ViewCompatBaseImpl.sAccessibilityDelegateCheckFailed : Z
      //   43: iconst_0
      //   44: ireturn
      //   45: getstatic android/support/v4/view/ViewCompat$ViewCompatBaseImpl.sAccessibilityDelegateField : Ljava/lang/reflect/Field;
      //   48: aload_1
      //   49: invokevirtual get : (Ljava/lang/Object;)Ljava/lang/Object;
      //   52: astore_1
      //   53: aload_1
      //   54: ifnull -> 59
      //   57: iconst_1
      //   58: istore_2
      //   59: iload_2
      //   60: ireturn
      //   61: iconst_1
      //   62: putstatic android/support/v4/view/ViewCompat$ViewCompatBaseImpl.sAccessibilityDelegateCheckFailed : Z
      //   65: iconst_0
      //   66: ireturn
      //   67: astore_1
      //   68: goto -> 39
      //   71: astore_1
      //   72: goto -> 61
      // Exception table:
      //   from	to	target	type
      //   18	36	67	java/lang/Throwable
      //   45	53	71	java/lang/Throwable }
    
    public boolean hasExplicitFocusable(@NonNull View param1View) { return param1View.hasFocusable(); }
    
    public boolean hasNestedScrollingParent(View param1View) { return (param1View instanceof NestedScrollingChild) ? ((NestedScrollingChild)param1View).hasNestedScrollingParent() : 0; }
    
    public boolean hasOnClickListeners(View param1View) { return false; }
    
    public boolean hasOverlappingRendering(View param1View) { return true; }
    
    public boolean hasTransientState(View param1View) { return false; }
    
    public boolean isAttachedToWindow(View param1View) { return (param1View.getWindowToken() != null); }
    
    public boolean isFocusedByDefault(@NonNull View param1View) { return false; }
    
    public boolean isImportantForAccessibility(View param1View) { return true; }
    
    public boolean isImportantForAutofill(@NonNull View param1View) { return true; }
    
    public boolean isInLayout(View param1View) { return false; }
    
    public boolean isKeyboardNavigationCluster(@NonNull View param1View) { return false; }
    
    public boolean isLaidOut(View param1View) { return (param1View.getWidth() > 0 && param1View.getHeight() > 0); }
    
    public boolean isLayoutDirectionResolved(View param1View) { return false; }
    
    public boolean isNestedScrollingEnabled(View param1View) { return (param1View instanceof NestedScrollingChild) ? ((NestedScrollingChild)param1View).isNestedScrollingEnabled() : 0; }
    
    public boolean isPaddingRelative(View param1View) { return false; }
    
    public View keyboardNavigationClusterSearch(@NonNull View param1View1, View param1View2, int param1Int) { return null; }
    
    public void offsetLeftAndRight(View param1View, int param1Int) {
      param1View.offsetLeftAndRight(param1Int);
      if (param1View.getVisibility() == 0) {
        tickleInvalidationFlag(param1View);
        ViewParent viewParent = param1View.getParent();
        if (viewParent instanceof View)
          tickleInvalidationFlag((View)viewParent); 
      } 
    }
    
    public void offsetTopAndBottom(View param1View, int param1Int) {
      param1View.offsetTopAndBottom(param1Int);
      if (param1View.getVisibility() == 0) {
        tickleInvalidationFlag(param1View);
        ViewParent viewParent = param1View.getParent();
        if (viewParent instanceof View)
          tickleInvalidationFlag((View)viewParent); 
      } 
    }
    
    public WindowInsetsCompat onApplyWindowInsets(View param1View, WindowInsetsCompat param1WindowInsetsCompat) { return param1WindowInsetsCompat; }
    
    public void onInitializeAccessibilityNodeInfo(View param1View, AccessibilityNodeInfoCompat param1AccessibilityNodeInfoCompat) { param1View.onInitializeAccessibilityNodeInfo(param1AccessibilityNodeInfoCompat.unwrap()); }
    
    public boolean performAccessibilityAction(View param1View, int param1Int, Bundle param1Bundle) { return false; }
    
    public void postInvalidateOnAnimation(View param1View) { param1View.postInvalidate(); }
    
    public void postInvalidateOnAnimation(View param1View, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { param1View.postInvalidate(param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void postOnAnimation(View param1View, Runnable param1Runnable) { param1View.postDelayed(param1Runnable, getFrameTime()); }
    
    public void postOnAnimationDelayed(View param1View, Runnable param1Runnable, long param1Long) { param1View.postDelayed(param1Runnable, getFrameTime() + param1Long); }
    
    public void requestApplyInsets(View param1View) {}
    
    public boolean restoreDefaultFocus(@NonNull View param1View) { return param1View.requestFocus(); }
    
    public void setAccessibilityDelegate(View param1View, @Nullable AccessibilityDelegateCompat param1AccessibilityDelegateCompat) {
      View.AccessibilityDelegate accessibilityDelegate;
      if (param1AccessibilityDelegateCompat == null) {
        param1AccessibilityDelegateCompat = null;
      } else {
        accessibilityDelegate = param1AccessibilityDelegateCompat.getBridge();
      } 
      param1View.setAccessibilityDelegate(accessibilityDelegate);
    }
    
    public void setAccessibilityLiveRegion(View param1View, int param1Int) {}
    
    public void setAutofillHints(@NonNull View param1View, @Nullable String... param1VarArgs) {}
    
    public void setBackground(View param1View, Drawable param1Drawable) { param1View.setBackgroundDrawable(param1Drawable); }
    
    public void setBackgroundTintList(View param1View, ColorStateList param1ColorStateList) {
      if (param1View instanceof TintableBackgroundView)
        ((TintableBackgroundView)param1View).setSupportBackgroundTintList(param1ColorStateList); 
    }
    
    public void setBackgroundTintMode(View param1View, PorterDuff.Mode param1Mode) {
      if (param1View instanceof TintableBackgroundView)
        ((TintableBackgroundView)param1View).setSupportBackgroundTintMode(param1Mode); 
    }
    
    public void setChildrenDrawingOrderEnabled(ViewGroup param1ViewGroup, boolean param1Boolean) {
      if (sChildrenDrawingOrderMethod == null) {
        try {
          sChildrenDrawingOrderMethod = ViewGroup.class.getDeclaredMethod("setChildrenDrawingOrderEnabled", new Class[] { boolean.class });
        } catch (NoSuchMethodException noSuchMethodException) {
          Log.e("ViewCompat", "Unable to find childrenDrawingOrderEnabled", noSuchMethodException);
        } 
        sChildrenDrawingOrderMethod.setAccessible(true);
      } 
      try {
        sChildrenDrawingOrderMethod.invoke(param1ViewGroup, new Object[] { Boolean.valueOf(param1Boolean) });
        return;
      } catch (IllegalAccessException param1ViewGroup) {
        Log.e("ViewCompat", "Unable to invoke childrenDrawingOrderEnabled", param1ViewGroup);
        return;
      } catch (IllegalArgumentException param1ViewGroup) {
        Log.e("ViewCompat", "Unable to invoke childrenDrawingOrderEnabled", param1ViewGroup);
        return;
      } catch (InvocationTargetException param1ViewGroup) {
        Log.e("ViewCompat", "Unable to invoke childrenDrawingOrderEnabled", param1ViewGroup);
        return;
      } 
    }
    
    public void setClipBounds(View param1View, Rect param1Rect) {}
    
    public void setElevation(View param1View, float param1Float) {}
    
    public void setFocusedByDefault(@NonNull View param1View, boolean param1Boolean) {}
    
    public void setHasTransientState(View param1View, boolean param1Boolean) {}
    
    public void setImportantForAccessibility(View param1View, int param1Int) {}
    
    public void setImportantForAutofill(@NonNull View param1View, int param1Int) {}
    
    public void setKeyboardNavigationCluster(@NonNull View param1View, boolean param1Boolean) {}
    
    public void setLabelFor(View param1View, int param1Int) {}
    
    public void setLayerPaint(View param1View, Paint param1Paint) {
      param1View.setLayerType(param1View.getLayerType(), param1Paint);
      param1View.invalidate();
    }
    
    public void setLayoutDirection(View param1View, int param1Int) {}
    
    public void setNestedScrollingEnabled(View param1View, boolean param1Boolean) {
      if (param1View instanceof NestedScrollingChild)
        ((NestedScrollingChild)param1View).setNestedScrollingEnabled(param1Boolean); 
    }
    
    public void setNextClusterForwardId(@NonNull View param1View, int param1Int) {}
    
    public void setOnApplyWindowInsetsListener(View param1View, OnApplyWindowInsetsListener param1OnApplyWindowInsetsListener) {}
    
    public void setPaddingRelative(View param1View, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { param1View.setPadding(param1Int1, param1Int2, param1Int3, param1Int4); }
    
    public void setPointerIcon(View param1View, PointerIconCompat param1PointerIconCompat) {}
    
    public void setScrollIndicators(View param1View, int param1Int) {}
    
    public void setScrollIndicators(View param1View, int param1Int1, int param1Int2) {}
    
    public void setTooltipText(View param1View, CharSequence param1CharSequence) {}
    
    public void setTransitionName(View param1View, String param1String) {
      if (sTransitionNameMap == null)
        sTransitionNameMap = new WeakHashMap(); 
      sTransitionNameMap.put(param1View, param1String);
    }
    
    public void setTranslationZ(View param1View, float param1Float) {}
    
    public void setZ(View param1View, float param1Float) {}
    
    public boolean startDragAndDrop(View param1View, ClipData param1ClipData, View.DragShadowBuilder param1DragShadowBuilder, Object param1Object, int param1Int) { return param1View.startDrag(param1ClipData, param1DragShadowBuilder, param1Object, param1Int); }
    
    public boolean startNestedScroll(View param1View, int param1Int) { return (param1View instanceof NestedScrollingChild) ? ((NestedScrollingChild)param1View).startNestedScroll(param1Int) : 0; }
    
    public void stopNestedScroll(View param1View) {
      if (param1View instanceof NestedScrollingChild)
        ((NestedScrollingChild)param1View).stopNestedScroll(); 
    }
    
    public void updateDragShadow(View param1View, View.DragShadowBuilder param1DragShadowBuilder) {}
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/view/ViewCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */