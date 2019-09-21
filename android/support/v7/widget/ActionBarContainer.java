package android.support.v7.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.RestrictTo;
import android.support.v7.appcompat.R;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class ActionBarContainer extends FrameLayout {
  private View mActionBarView;
  
  Drawable mBackground;
  
  private View mContextView;
  
  private int mHeight;
  
  boolean mIsSplit;
  
  boolean mIsStacked;
  
  private boolean mIsTransitioning;
  
  Drawable mSplitBackground;
  
  Drawable mStackedBackground;
  
  private View mTabContainer;
  
  public ActionBarContainer(Context paramContext) { this(paramContext, null); }
  
  public ActionBarContainer(Context paramContext, AttributeSet paramAttributeSet) { // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: aload_2
    //   3: invokespecial <init> : (Landroid/content/Context;Landroid/util/AttributeSet;)V
    //   6: getstatic android/os/Build$VERSION.SDK_INT : I
    //   9: bipush #21
    //   11: if_icmplt -> 27
    //   14: new android/support/v7/widget/ActionBarBackgroundDrawableV21
    //   17: dup
    //   18: aload_0
    //   19: invokespecial <init> : (Landroid/support/v7/widget/ActionBarContainer;)V
    //   22: astore #5
    //   24: goto -> 37
    //   27: new android/support/v7/widget/ActionBarBackgroundDrawable
    //   30: dup
    //   31: aload_0
    //   32: invokespecial <init> : (Landroid/support/v7/widget/ActionBarContainer;)V
    //   35: astore #5
    //   37: aload_0
    //   38: aload #5
    //   40: invokestatic setBackground : (Landroid/view/View;Landroid/graphics/drawable/Drawable;)V
    //   43: aload_1
    //   44: aload_2
    //   45: getstatic android/support/v7/appcompat/R$styleable.ActionBar : [I
    //   48: invokevirtual obtainStyledAttributes : (Landroid/util/AttributeSet;[I)Landroid/content/res/TypedArray;
    //   51: astore_1
    //   52: aload_0
    //   53: aload_1
    //   54: getstatic android/support/v7/appcompat/R$styleable.ActionBar_background : I
    //   57: invokevirtual getDrawable : (I)Landroid/graphics/drawable/Drawable;
    //   60: putfield mBackground : Landroid/graphics/drawable/Drawable;
    //   63: aload_0
    //   64: aload_1
    //   65: getstatic android/support/v7/appcompat/R$styleable.ActionBar_backgroundStacked : I
    //   68: invokevirtual getDrawable : (I)Landroid/graphics/drawable/Drawable;
    //   71: putfield mStackedBackground : Landroid/graphics/drawable/Drawable;
    //   74: aload_0
    //   75: aload_1
    //   76: getstatic android/support/v7/appcompat/R$styleable.ActionBar_height : I
    //   79: iconst_m1
    //   80: invokevirtual getDimensionPixelSize : (II)I
    //   83: putfield mHeight : I
    //   86: aload_0
    //   87: invokevirtual getId : ()I
    //   90: getstatic android/support/v7/appcompat/R$id.split_action_bar : I
    //   93: if_icmpne -> 112
    //   96: aload_0
    //   97: iconst_1
    //   98: putfield mIsSplit : Z
    //   101: aload_0
    //   102: aload_1
    //   103: getstatic android/support/v7/appcompat/R$styleable.ActionBar_backgroundSplit : I
    //   106: invokevirtual getDrawable : (I)Landroid/graphics/drawable/Drawable;
    //   109: putfield mSplitBackground : Landroid/graphics/drawable/Drawable;
    //   112: aload_1
    //   113: invokevirtual recycle : ()V
    //   116: aload_0
    //   117: getfield mIsSplit : Z
    //   120: istore_3
    //   121: iconst_0
    //   122: istore #4
    //   124: iload_3
    //   125: ifeq -> 143
    //   128: iload #4
    //   130: istore_3
    //   131: aload_0
    //   132: getfield mSplitBackground : Landroid/graphics/drawable/Drawable;
    //   135: ifnonnull -> 166
    //   138: iconst_1
    //   139: istore_3
    //   140: goto -> 166
    //   143: iload #4
    //   145: istore_3
    //   146: aload_0
    //   147: getfield mBackground : Landroid/graphics/drawable/Drawable;
    //   150: ifnonnull -> 166
    //   153: iload #4
    //   155: istore_3
    //   156: aload_0
    //   157: getfield mStackedBackground : Landroid/graphics/drawable/Drawable;
    //   160: ifnonnull -> 166
    //   163: goto -> 138
    //   166: aload_0
    //   167: iload_3
    //   168: invokevirtual setWillNotDraw : (Z)V
    //   171: return }
  
  private int getMeasuredHeightWithMargins(View paramView) {
    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)paramView.getLayoutParams();
    return paramView.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
  }
  
  private boolean isCollapsed(View paramView) { return (paramView == null || paramView.getVisibility() == 8 || paramView.getMeasuredHeight() == 0); }
  
  protected void drawableStateChanged() {
    super.drawableStateChanged();
    if (this.mBackground != null && this.mBackground.isStateful())
      this.mBackground.setState(getDrawableState()); 
    if (this.mStackedBackground != null && this.mStackedBackground.isStateful())
      this.mStackedBackground.setState(getDrawableState()); 
    if (this.mSplitBackground != null && this.mSplitBackground.isStateful())
      this.mSplitBackground.setState(getDrawableState()); 
  }
  
  public View getTabContainer() { return this.mTabContainer; }
  
  public void jumpDrawablesToCurrentState() {
    super.jumpDrawablesToCurrentState();
    if (this.mBackground != null)
      this.mBackground.jumpToCurrentState(); 
    if (this.mStackedBackground != null)
      this.mStackedBackground.jumpToCurrentState(); 
    if (this.mSplitBackground != null)
      this.mSplitBackground.jumpToCurrentState(); 
  }
  
  public void onFinishInflate() {
    super.onFinishInflate();
    this.mActionBarView = findViewById(R.id.action_bar);
    this.mContextView = findViewById(R.id.action_context_bar);
  }
  
  public boolean onHoverEvent(MotionEvent paramMotionEvent) {
    super.onHoverEvent(paramMotionEvent);
    return true;
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent) { return (this.mIsTransitioning || super.onInterceptTouchEvent(paramMotionEvent)); }
  
  public void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    View view = this.mTabContainer;
    paramInt2 = 1;
    paramInt4 = 0;
    if (view != null && view.getVisibility() != 8) {
      paramBoolean = true;
    } else {
      paramBoolean = false;
    } 
    if (view != null && view.getVisibility() != 8) {
      int i = getMeasuredHeight();
      FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)view.getLayoutParams();
      view.layout(paramInt1, i - view.getMeasuredHeight() - layoutParams.bottomMargin, paramInt3, i - layoutParams.bottomMargin);
    } 
    if (this.mIsSplit) {
      if (this.mSplitBackground != null) {
        this.mSplitBackground.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
        paramInt1 = paramInt2;
      } else {
        paramInt1 = 0;
      } 
    } else {
      paramInt1 = paramInt4;
      if (this.mBackground != null) {
        if (this.mActionBarView.getVisibility() == 0) {
          this.mBackground.setBounds(this.mActionBarView.getLeft(), this.mActionBarView.getTop(), this.mActionBarView.getRight(), this.mActionBarView.getBottom());
        } else if (this.mContextView != null && this.mContextView.getVisibility() == 0) {
          this.mBackground.setBounds(this.mContextView.getLeft(), this.mContextView.getTop(), this.mContextView.getRight(), this.mContextView.getBottom());
        } else {
          this.mBackground.setBounds(0, 0, 0, 0);
        } 
        paramInt1 = 1;
      } 
      this.mIsStacked = paramBoolean;
      if (paramBoolean && this.mStackedBackground != null) {
        this.mStackedBackground.setBounds(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
        paramInt1 = paramInt2;
      } 
    } 
    if (paramInt1 != 0)
      invalidate(); 
  }
  
  public void onMeasure(int paramInt1, int paramInt2) {
    int i = paramInt2;
    if (this.mActionBarView == null) {
      i = paramInt2;
      if (View.MeasureSpec.getMode(paramInt2) == Integer.MIN_VALUE) {
        i = paramInt2;
        if (this.mHeight >= 0)
          i = View.MeasureSpec.makeMeasureSpec(Math.min(this.mHeight, View.MeasureSpec.getSize(paramInt2)), -2147483648); 
      } 
    } 
    super.onMeasure(paramInt1, i);
    if (this.mActionBarView == null)
      return; 
    paramInt2 = View.MeasureSpec.getMode(i);
    if (this.mTabContainer != null && this.mTabContainer.getVisibility() != 8 && paramInt2 != 1073741824) {
      if (!isCollapsed(this.mActionBarView)) {
        paramInt1 = getMeasuredHeightWithMargins(this.mActionBarView);
      } else if (!isCollapsed(this.mContextView)) {
        paramInt1 = getMeasuredHeightWithMargins(this.mContextView);
      } else {
        paramInt1 = 0;
      } 
      if (paramInt2 == Integer.MIN_VALUE) {
        paramInt2 = View.MeasureSpec.getSize(i);
      } else {
        paramInt2 = Integer.MAX_VALUE;
      } 
      setMeasuredDimension(getMeasuredWidth(), Math.min(paramInt1 + getMeasuredHeightWithMargins(this.mTabContainer), paramInt2));
    } 
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent) {
    super.onTouchEvent(paramMotionEvent);
    return true;
  }
  
  public void setPrimaryBackground(Drawable paramDrawable) { // Byte code:
    //   0: aload_0
    //   1: getfield mBackground : Landroid/graphics/drawable/Drawable;
    //   4: ifnull -> 23
    //   7: aload_0
    //   8: getfield mBackground : Landroid/graphics/drawable/Drawable;
    //   11: aconst_null
    //   12: invokevirtual setCallback : (Landroid/graphics/drawable/Drawable$Callback;)V
    //   15: aload_0
    //   16: aload_0
    //   17: getfield mBackground : Landroid/graphics/drawable/Drawable;
    //   20: invokevirtual unscheduleDrawable : (Landroid/graphics/drawable/Drawable;)V
    //   23: aload_0
    //   24: aload_1
    //   25: putfield mBackground : Landroid/graphics/drawable/Drawable;
    //   28: aload_1
    //   29: ifnull -> 79
    //   32: aload_1
    //   33: aload_0
    //   34: invokevirtual setCallback : (Landroid/graphics/drawable/Drawable$Callback;)V
    //   37: aload_0
    //   38: getfield mActionBarView : Landroid/view/View;
    //   41: ifnull -> 79
    //   44: aload_0
    //   45: getfield mBackground : Landroid/graphics/drawable/Drawable;
    //   48: aload_0
    //   49: getfield mActionBarView : Landroid/view/View;
    //   52: invokevirtual getLeft : ()I
    //   55: aload_0
    //   56: getfield mActionBarView : Landroid/view/View;
    //   59: invokevirtual getTop : ()I
    //   62: aload_0
    //   63: getfield mActionBarView : Landroid/view/View;
    //   66: invokevirtual getRight : ()I
    //   69: aload_0
    //   70: getfield mActionBarView : Landroid/view/View;
    //   73: invokevirtual getBottom : ()I
    //   76: invokevirtual setBounds : (IIII)V
    //   79: aload_0
    //   80: getfield mIsSplit : Z
    //   83: istore_2
    //   84: iconst_0
    //   85: istore_3
    //   86: iload_2
    //   87: ifeq -> 104
    //   90: iload_3
    //   91: istore_2
    //   92: aload_0
    //   93: getfield mSplitBackground : Landroid/graphics/drawable/Drawable;
    //   96: ifnonnull -> 125
    //   99: iconst_1
    //   100: istore_2
    //   101: goto -> 125
    //   104: iload_3
    //   105: istore_2
    //   106: aload_0
    //   107: getfield mBackground : Landroid/graphics/drawable/Drawable;
    //   110: ifnonnull -> 125
    //   113: iload_3
    //   114: istore_2
    //   115: aload_0
    //   116: getfield mStackedBackground : Landroid/graphics/drawable/Drawable;
    //   119: ifnonnull -> 125
    //   122: goto -> 99
    //   125: aload_0
    //   126: iload_2
    //   127: invokevirtual setWillNotDraw : (Z)V
    //   130: aload_0
    //   131: invokevirtual invalidate : ()V
    //   134: return }
  
  public void setSplitBackground(Drawable paramDrawable) { // Byte code:
    //   0: aload_0
    //   1: getfield mSplitBackground : Landroid/graphics/drawable/Drawable;
    //   4: ifnull -> 23
    //   7: aload_0
    //   8: getfield mSplitBackground : Landroid/graphics/drawable/Drawable;
    //   11: aconst_null
    //   12: invokevirtual setCallback : (Landroid/graphics/drawable/Drawable$Callback;)V
    //   15: aload_0
    //   16: aload_0
    //   17: getfield mSplitBackground : Landroid/graphics/drawable/Drawable;
    //   20: invokevirtual unscheduleDrawable : (Landroid/graphics/drawable/Drawable;)V
    //   23: aload_0
    //   24: aload_1
    //   25: putfield mSplitBackground : Landroid/graphics/drawable/Drawable;
    //   28: iconst_0
    //   29: istore_3
    //   30: aload_1
    //   31: ifnull -> 70
    //   34: aload_1
    //   35: aload_0
    //   36: invokevirtual setCallback : (Landroid/graphics/drawable/Drawable$Callback;)V
    //   39: aload_0
    //   40: getfield mIsSplit : Z
    //   43: ifeq -> 70
    //   46: aload_0
    //   47: getfield mSplitBackground : Landroid/graphics/drawable/Drawable;
    //   50: ifnull -> 70
    //   53: aload_0
    //   54: getfield mSplitBackground : Landroid/graphics/drawable/Drawable;
    //   57: iconst_0
    //   58: iconst_0
    //   59: aload_0
    //   60: invokevirtual getMeasuredWidth : ()I
    //   63: aload_0
    //   64: invokevirtual getMeasuredHeight : ()I
    //   67: invokevirtual setBounds : (IIII)V
    //   70: aload_0
    //   71: getfield mIsSplit : Z
    //   74: ifeq -> 91
    //   77: iload_3
    //   78: istore_2
    //   79: aload_0
    //   80: getfield mSplitBackground : Landroid/graphics/drawable/Drawable;
    //   83: ifnonnull -> 112
    //   86: iconst_1
    //   87: istore_2
    //   88: goto -> 112
    //   91: iload_3
    //   92: istore_2
    //   93: aload_0
    //   94: getfield mBackground : Landroid/graphics/drawable/Drawable;
    //   97: ifnonnull -> 112
    //   100: iload_3
    //   101: istore_2
    //   102: aload_0
    //   103: getfield mStackedBackground : Landroid/graphics/drawable/Drawable;
    //   106: ifnonnull -> 112
    //   109: goto -> 86
    //   112: aload_0
    //   113: iload_2
    //   114: invokevirtual setWillNotDraw : (Z)V
    //   117: aload_0
    //   118: invokevirtual invalidate : ()V
    //   121: return }
  
  public void setStackedBackground(Drawable paramDrawable) { // Byte code:
    //   0: aload_0
    //   1: getfield mStackedBackground : Landroid/graphics/drawable/Drawable;
    //   4: ifnull -> 23
    //   7: aload_0
    //   8: getfield mStackedBackground : Landroid/graphics/drawable/Drawable;
    //   11: aconst_null
    //   12: invokevirtual setCallback : (Landroid/graphics/drawable/Drawable$Callback;)V
    //   15: aload_0
    //   16: aload_0
    //   17: getfield mStackedBackground : Landroid/graphics/drawable/Drawable;
    //   20: invokevirtual unscheduleDrawable : (Landroid/graphics/drawable/Drawable;)V
    //   23: aload_0
    //   24: aload_1
    //   25: putfield mStackedBackground : Landroid/graphics/drawable/Drawable;
    //   28: aload_1
    //   29: ifnull -> 86
    //   32: aload_1
    //   33: aload_0
    //   34: invokevirtual setCallback : (Landroid/graphics/drawable/Drawable$Callback;)V
    //   37: aload_0
    //   38: getfield mIsStacked : Z
    //   41: ifeq -> 86
    //   44: aload_0
    //   45: getfield mStackedBackground : Landroid/graphics/drawable/Drawable;
    //   48: ifnull -> 86
    //   51: aload_0
    //   52: getfield mStackedBackground : Landroid/graphics/drawable/Drawable;
    //   55: aload_0
    //   56: getfield mTabContainer : Landroid/view/View;
    //   59: invokevirtual getLeft : ()I
    //   62: aload_0
    //   63: getfield mTabContainer : Landroid/view/View;
    //   66: invokevirtual getTop : ()I
    //   69: aload_0
    //   70: getfield mTabContainer : Landroid/view/View;
    //   73: invokevirtual getRight : ()I
    //   76: aload_0
    //   77: getfield mTabContainer : Landroid/view/View;
    //   80: invokevirtual getBottom : ()I
    //   83: invokevirtual setBounds : (IIII)V
    //   86: aload_0
    //   87: getfield mIsSplit : Z
    //   90: istore_2
    //   91: iconst_0
    //   92: istore_3
    //   93: iload_2
    //   94: ifeq -> 111
    //   97: iload_3
    //   98: istore_2
    //   99: aload_0
    //   100: getfield mSplitBackground : Landroid/graphics/drawable/Drawable;
    //   103: ifnonnull -> 132
    //   106: iconst_1
    //   107: istore_2
    //   108: goto -> 132
    //   111: iload_3
    //   112: istore_2
    //   113: aload_0
    //   114: getfield mBackground : Landroid/graphics/drawable/Drawable;
    //   117: ifnonnull -> 132
    //   120: iload_3
    //   121: istore_2
    //   122: aload_0
    //   123: getfield mStackedBackground : Landroid/graphics/drawable/Drawable;
    //   126: ifnonnull -> 132
    //   129: goto -> 106
    //   132: aload_0
    //   133: iload_2
    //   134: invokevirtual setWillNotDraw : (Z)V
    //   137: aload_0
    //   138: invokevirtual invalidate : ()V
    //   141: return }
  
  public void setTabContainer(ScrollingTabContainerView paramScrollingTabContainerView) {
    if (this.mTabContainer != null)
      removeView(this.mTabContainer); 
    this.mTabContainer = paramScrollingTabContainerView;
    if (paramScrollingTabContainerView != null) {
      addView(paramScrollingTabContainerView);
      ViewGroup.LayoutParams layoutParams = paramScrollingTabContainerView.getLayoutParams();
      layoutParams.width = -1;
      layoutParams.height = -2;
      paramScrollingTabContainerView.setAllowCollapse(false);
    } 
  }
  
  public void setTransitioning(boolean paramBoolean) {
    int i;
    this.mIsTransitioning = paramBoolean;
    if (paramBoolean) {
      i = 393216;
    } else {
      i = 262144;
    } 
    setDescendantFocusability(i);
  }
  
  public void setVisibility(int paramInt) {
    boolean bool;
    super.setVisibility(paramInt);
    if (paramInt == 0) {
      bool = true;
    } else {
      bool = false;
    } 
    if (this.mBackground != null)
      this.mBackground.setVisible(bool, false); 
    if (this.mStackedBackground != null)
      this.mStackedBackground.setVisible(bool, false); 
    if (this.mSplitBackground != null)
      this.mSplitBackground.setVisible(bool, false); 
  }
  
  public ActionMode startActionModeForChild(View paramView, ActionMode.Callback paramCallback) { return null; }
  
  public ActionMode startActionModeForChild(View paramView, ActionMode.Callback paramCallback, int paramInt) { return (paramInt != 0) ? super.startActionModeForChild(paramView, paramCallback, paramInt) : null; }
  
  protected boolean verifyDrawable(Drawable paramDrawable) { return ((paramDrawable == this.mBackground && !this.mIsSplit) || (paramDrawable == this.mStackedBackground && this.mIsStacked) || (paramDrawable == this.mSplitBackground && this.mIsSplit) || super.verifyDrawable(paramDrawable)); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/ActionBarContainer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */