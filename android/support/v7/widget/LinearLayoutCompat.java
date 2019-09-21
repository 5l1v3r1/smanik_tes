package android.support.v7.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.RestrictTo;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class LinearLayoutCompat extends ViewGroup {
  public static final int HORIZONTAL = 0;
  
  private static final int INDEX_BOTTOM = 2;
  
  private static final int INDEX_CENTER_VERTICAL = 0;
  
  private static final int INDEX_FILL = 3;
  
  private static final int INDEX_TOP = 1;
  
  public static final int SHOW_DIVIDER_BEGINNING = 1;
  
  public static final int SHOW_DIVIDER_END = 4;
  
  public static final int SHOW_DIVIDER_MIDDLE = 2;
  
  public static final int SHOW_DIVIDER_NONE = 0;
  
  public static final int VERTICAL = 1;
  
  private static final int VERTICAL_GRAVITY_COUNT = 4;
  
  private boolean mBaselineAligned = true;
  
  private int mBaselineAlignedChildIndex = -1;
  
  private int mBaselineChildTop = 0;
  
  private Drawable mDivider;
  
  private int mDividerHeight;
  
  private int mDividerPadding;
  
  private int mDividerWidth;
  
  private int mGravity = 8388659;
  
  private int[] mMaxAscent;
  
  private int[] mMaxDescent;
  
  private int mOrientation;
  
  private int mShowDividers;
  
  private int mTotalLength;
  
  private boolean mUseLargestChild;
  
  private float mWeightSum;
  
  public LinearLayoutCompat(Context paramContext) { this(paramContext, null); }
  
  public LinearLayoutCompat(Context paramContext, AttributeSet paramAttributeSet) { this(paramContext, paramAttributeSet, 0); }
  
  public LinearLayoutCompat(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
    super(paramContext, paramAttributeSet, paramInt);
    TintTypedArray tintTypedArray = TintTypedArray.obtainStyledAttributes(paramContext, paramAttributeSet, R.styleable.LinearLayoutCompat, paramInt, 0);
    paramInt = tintTypedArray.getInt(R.styleable.LinearLayoutCompat_android_orientation, -1);
    if (paramInt >= 0)
      setOrientation(paramInt); 
    paramInt = tintTypedArray.getInt(R.styleable.LinearLayoutCompat_android_gravity, -1);
    if (paramInt >= 0)
      setGravity(paramInt); 
    boolean bool = tintTypedArray.getBoolean(R.styleable.LinearLayoutCompat_android_baselineAligned, true);
    if (!bool)
      setBaselineAligned(bool); 
    this.mWeightSum = tintTypedArray.getFloat(R.styleable.LinearLayoutCompat_android_weightSum, -1.0F);
    this.mBaselineAlignedChildIndex = tintTypedArray.getInt(R.styleable.LinearLayoutCompat_android_baselineAlignedChildIndex, -1);
    this.mUseLargestChild = tintTypedArray.getBoolean(R.styleable.LinearLayoutCompat_measureWithLargestChild, false);
    setDividerDrawable(tintTypedArray.getDrawable(R.styleable.LinearLayoutCompat_divider));
    this.mShowDividers = tintTypedArray.getInt(R.styleable.LinearLayoutCompat_showDividers, 0);
    this.mDividerPadding = tintTypedArray.getDimensionPixelSize(R.styleable.LinearLayoutCompat_dividerPadding, 0);
    tintTypedArray.recycle();
  }
  
  private void forceUniformHeight(int paramInt1, int paramInt2) {
    int i = View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824);
    for (byte b = 0; b < paramInt1; b++) {
      View view = getVirtualChildAt(b);
      if (view.getVisibility() != 8) {
        LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        if (layoutParams.height == -1) {
          int j = layoutParams.width;
          layoutParams.width = view.getMeasuredWidth();
          measureChildWithMargins(view, paramInt2, 0, i, 0);
          layoutParams.width = j;
        } 
      } 
    } 
  }
  
  private void forceUniformWidth(int paramInt1, int paramInt2) {
    int i = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824);
    for (byte b = 0; b < paramInt1; b++) {
      View view = getVirtualChildAt(b);
      if (view.getVisibility() != 8) {
        LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        if (layoutParams.width == -1) {
          int j = layoutParams.height;
          layoutParams.height = view.getMeasuredHeight();
          measureChildWithMargins(view, i, 0, paramInt2, 0);
          layoutParams.height = j;
        } 
      } 
    } 
  }
  
  private void setChildFrame(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { paramView.layout(paramInt1, paramInt2, paramInt3 + paramInt1, paramInt4 + paramInt2); }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams) { return paramLayoutParams instanceof LayoutParams; }
  
  void drawDividersHorizontal(Canvas paramCanvas) {
    int j = getVirtualChildCount();
    boolean bool = ViewUtils.isLayoutRtl(this);
    int i;
    for (i = 0; i < j; i++) {
      View view = getVirtualChildAt(i);
      if (view != null && view.getVisibility() != 8 && hasDividerBeforeChildAt(i)) {
        int k;
        LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        if (bool) {
          k = view.getRight() + layoutParams.rightMargin;
        } else {
          k = view.getLeft() - layoutParams.leftMargin - this.mDividerWidth;
        } 
        drawVerticalDivider(paramCanvas, k);
      } 
    } 
    if (hasDividerBeforeChildAt(j)) {
      View view = getVirtualChildAt(j - 1);
      if (view == null) {
        if (bool) {
          i = getPaddingLeft();
        } else {
          i = getWidth() - getPaddingRight() - this.mDividerWidth;
        } 
      } else {
        LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        if (bool) {
          i = view.getLeft() - layoutParams.leftMargin - this.mDividerWidth;
        } else {
          i = view.getRight() + layoutParams.rightMargin;
        } 
      } 
      drawVerticalDivider(paramCanvas, i);
    } 
  }
  
  void drawDividersVertical(Canvas paramCanvas) {
    int j = getVirtualChildCount();
    int i;
    for (i = 0; i < j; i++) {
      View view = getVirtualChildAt(i);
      if (view != null && view.getVisibility() != 8 && hasDividerBeforeChildAt(i)) {
        LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        drawHorizontalDivider(paramCanvas, view.getTop() - layoutParams.topMargin - this.mDividerHeight);
      } 
    } 
    if (hasDividerBeforeChildAt(j)) {
      View view = getVirtualChildAt(j - 1);
      if (view == null) {
        i = getHeight() - getPaddingBottom() - this.mDividerHeight;
      } else {
        LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        i = view.getBottom() + layoutParams.bottomMargin;
      } 
      drawHorizontalDivider(paramCanvas, i);
    } 
  }
  
  void drawHorizontalDivider(Canvas paramCanvas, int paramInt) {
    this.mDivider.setBounds(getPaddingLeft() + this.mDividerPadding, paramInt, getWidth() - getPaddingRight() - this.mDividerPadding, this.mDividerHeight + paramInt);
    this.mDivider.draw(paramCanvas);
  }
  
  void drawVerticalDivider(Canvas paramCanvas, int paramInt) {
    this.mDivider.setBounds(paramInt, getPaddingTop() + this.mDividerPadding, this.mDividerWidth + paramInt, getHeight() - getPaddingBottom() - this.mDividerPadding);
    this.mDivider.draw(paramCanvas);
  }
  
  protected LayoutParams generateDefaultLayoutParams() { return (this.mOrientation == 0) ? new LayoutParams(-2, -2) : ((this.mOrientation == 1) ? new LayoutParams(-1, -2) : null); }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet) { return new LayoutParams(getContext(), paramAttributeSet); }
  
  protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams) { return new LayoutParams(paramLayoutParams); }
  
  public int getBaseline() {
    if (this.mBaselineAlignedChildIndex < 0)
      return super.getBaseline(); 
    if (getChildCount() <= this.mBaselineAlignedChildIndex)
      throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout set to an index that is out of bounds."); 
    View view = getChildAt(this.mBaselineAlignedChildIndex);
    int k = view.getBaseline();
    if (k == -1) {
      if (this.mBaselineAlignedChildIndex == 0)
        return -1; 
      throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout points to a View that doesn't know how to get its baseline.");
    } 
    int j = this.mBaselineChildTop;
    int i = j;
    if (this.mOrientation == 1) {
      int m = this.mGravity & 0x70;
      i = j;
      if (m != 48)
        if (m != 16) {
          if (m != 80) {
            i = j;
          } else {
            i = getBottom() - getTop() - getPaddingBottom() - this.mTotalLength;
          } 
        } else {
          i = j + (getBottom() - getTop() - getPaddingTop() - getPaddingBottom() - this.mTotalLength) / 2;
        }  
    } 
    return i + ((LayoutParams)view.getLayoutParams()).topMargin + k;
  }
  
  public int getBaselineAlignedChildIndex() { return this.mBaselineAlignedChildIndex; }
  
  int getChildrenSkipCount(View paramView, int paramInt) { return 0; }
  
  public Drawable getDividerDrawable() { return this.mDivider; }
  
  public int getDividerPadding() { return this.mDividerPadding; }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public int getDividerWidth() { return this.mDividerWidth; }
  
  public int getGravity() { return this.mGravity; }
  
  int getLocationOffset(View paramView) { return 0; }
  
  int getNextLocationOffset(View paramView) { return 0; }
  
  public int getOrientation() { return this.mOrientation; }
  
  public int getShowDividers() { return this.mShowDividers; }
  
  View getVirtualChildAt(int paramInt) { return getChildAt(paramInt); }
  
  int getVirtualChildCount() { return getChildCount(); }
  
  public float getWeightSum() { return this.mWeightSum; }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY})
  protected boolean hasDividerBeforeChildAt(int paramInt) {
    boolean bool1 = false;
    boolean bool = false;
    if (paramInt == 0) {
      if ((this.mShowDividers & true) != 0)
        bool = true; 
      return bool;
    } 
    if (paramInt == getChildCount()) {
      bool = bool1;
      if ((this.mShowDividers & 0x4) != 0)
        bool = true; 
      return bool;
    } 
    if ((this.mShowDividers & 0x2) != 0) {
      while (--paramInt >= 0) {
        if (getChildAt(paramInt).getVisibility() != 8)
          return true; 
        paramInt--;
      } 
      return false;
    } 
    return false;
  }
  
  public boolean isBaselineAligned() { return this.mBaselineAligned; }
  
  public boolean isMeasureWithLargestChildEnabled() { return this.mUseLargestChild; }
  
  void layoutHorizontal(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int j;
    int i;
    boolean bool1 = ViewUtils.isLayoutRtl(this);
    int m = getPaddingTop();
    int n = paramInt4 - paramInt2;
    int i1 = getPaddingBottom();
    int i2 = getPaddingBottom();
    int k = getVirtualChildCount();
    paramInt4 = this.mGravity;
    paramInt2 = this.mGravity & 0x70;
    boolean bool2 = this.mBaselineAligned;
    int[] arrayOfInt1 = this.mMaxAscent;
    int[] arrayOfInt2 = this.mMaxDescent;
    paramInt4 = GravityCompat.getAbsoluteGravity(paramInt4 & 0x800007, ViewCompat.getLayoutDirection(this));
    if (paramInt4 != 1) {
      if (paramInt4 != 5) {
        paramInt1 = getPaddingLeft();
      } else {
        paramInt1 = getPaddingLeft() + paramInt3 - paramInt1 - this.mTotalLength;
      } 
    } else {
      paramInt4 = getPaddingLeft();
      paramInt1 = (paramInt3 - paramInt1 - this.mTotalLength) / 2 + paramInt4;
    } 
    if (bool1) {
      i = k - 1;
      j = -1;
    } else {
      i = 0;
      j = 1;
    } 
    paramInt4 = 0;
    paramInt3 = m;
    while (paramInt4 < k) {
      int i3 = i + j * paramInt4;
      View view = getVirtualChildAt(i3);
      if (view == null) {
        paramInt1 += measureNullChild(i3);
      } else if (view.getVisibility() != 8) {
        int i7 = view.getMeasuredWidth();
        int i8 = view.getMeasuredHeight();
        LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
        if (bool2 && layoutParams.height != -1) {
          i5 = view.getBaseline();
        } else {
          i5 = -1;
        } 
        int i6 = layoutParams.gravity;
        int i4 = i6;
        if (i6 < 0)
          i4 = paramInt2; 
        i4 &= 0x70;
        if (i4 != 16) {
          if (i4 != 48) {
            if (i4 != 80) {
              i4 = paramInt3;
            } else {
              i6 = n - i1 - i8 - layoutParams.bottomMargin;
              i4 = i6;
              if (i5 != -1) {
                i4 = view.getMeasuredHeight();
                i4 = i6 - arrayOfInt2[2] - i4 - i5;
              } 
            } 
          } else {
            i4 = layoutParams.topMargin + paramInt3;
            if (i5 != -1)
              i4 += arrayOfInt1[1] - i5; 
          } 
        } else {
          i4 = (n - m - i2 - i8) / 2 + paramInt3 + layoutParams.topMargin - layoutParams.bottomMargin;
        } 
        int i5 = paramInt1;
        if (hasDividerBeforeChildAt(i3))
          i5 = paramInt1 + this.mDividerWidth; 
        paramInt1 = layoutParams.leftMargin + i5;
        setChildFrame(view, paramInt1 + getLocationOffset(view), i4, i7, i8);
        i4 = layoutParams.rightMargin;
        i5 = getNextLocationOffset(view);
        paramInt4 += getChildrenSkipCount(view, i3);
        paramInt1 += i7 + i4 + i5;
      } 
      paramInt4++;
    } 
  }
  
  void layoutVertical(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = getPaddingLeft();
    int j = paramInt3 - paramInt1;
    int k = getPaddingRight();
    int m = getPaddingRight();
    int n = getVirtualChildCount();
    paramInt1 = this.mGravity & 0x70;
    int i1 = this.mGravity;
    if (paramInt1 != 16) {
      if (paramInt1 != 80) {
        paramInt1 = getPaddingTop();
      } else {
        paramInt1 = getPaddingTop() + paramInt4 - paramInt2 - this.mTotalLength;
      } 
    } else {
      paramInt1 = getPaddingTop();
      paramInt1 = (paramInt4 - paramInt2 - this.mTotalLength) / 2 + paramInt1;
    } 
    paramInt2 = 0;
    while (paramInt2 < n) {
      View view = getVirtualChildAt(paramInt2);
      if (view == null) {
        paramInt3 = paramInt1 + measureNullChild(paramInt2);
        paramInt4 = paramInt2;
      } else {
        paramInt3 = paramInt1;
        paramInt4 = paramInt2;
        if (view.getVisibility() != 8) {
          int i3 = view.getMeasuredWidth();
          int i2 = view.getMeasuredHeight();
          LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
          paramInt4 = layoutParams.gravity;
          paramInt3 = paramInt4;
          if (paramInt4 < 0)
            paramInt3 = i1 & 0x800007; 
          paramInt3 = GravityCompat.getAbsoluteGravity(paramInt3, ViewCompat.getLayoutDirection(this)) & 0x7;
          if (paramInt3 != 1) {
            if (paramInt3 != 5) {
              paramInt3 = layoutParams.leftMargin + i;
            } else {
              paramInt3 = j - k - i3 - layoutParams.rightMargin;
            } 
          } else {
            paramInt3 = (j - i - m - i3) / 2 + i + layoutParams.leftMargin - layoutParams.rightMargin;
          } 
          paramInt4 = paramInt1;
          if (hasDividerBeforeChildAt(paramInt2))
            paramInt4 = paramInt1 + this.mDividerHeight; 
          paramInt1 = paramInt4 + layoutParams.topMargin;
          setChildFrame(view, paramInt3, paramInt1 + getLocationOffset(view), i3, i2);
          paramInt3 = layoutParams.bottomMargin;
          i3 = getNextLocationOffset(view);
          paramInt4 = paramInt2 + getChildrenSkipCount(view, paramInt2);
          paramInt3 = paramInt1 + i2 + paramInt3 + i3;
        } 
      } 
      paramInt2 = paramInt4 + 1;
      paramInt1 = paramInt3;
    } 
  }
  
  void measureChildBeforeLayout(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { measureChildWithMargins(paramView, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  void measureHorizontal(int paramInt1, int paramInt2) { // Byte code:
    //   0: aload_0
    //   1: iconst_0
    //   2: putfield mTotalLength : I
    //   5: aload_0
    //   6: invokevirtual getVirtualChildCount : ()I
    //   9: istore #19
    //   11: iload_1
    //   12: invokestatic getMode : (I)I
    //   15: istore #21
    //   17: iload_2
    //   18: invokestatic getMode : (I)I
    //   21: istore #20
    //   23: aload_0
    //   24: getfield mMaxAscent : [I
    //   27: ifnull -> 37
    //   30: aload_0
    //   31: getfield mMaxDescent : [I
    //   34: ifnonnull -> 51
    //   37: aload_0
    //   38: iconst_4
    //   39: newarray int
    //   41: putfield mMaxAscent : [I
    //   44: aload_0
    //   45: iconst_4
    //   46: newarray int
    //   48: putfield mMaxDescent : [I
    //   51: aload_0
    //   52: getfield mMaxAscent : [I
    //   55: astore #28
    //   57: aload_0
    //   58: getfield mMaxDescent : [I
    //   61: astore #25
    //   63: aload #28
    //   65: iconst_3
    //   66: iconst_m1
    //   67: iastore
    //   68: aload #28
    //   70: iconst_2
    //   71: iconst_m1
    //   72: iastore
    //   73: aload #28
    //   75: iconst_1
    //   76: iconst_m1
    //   77: iastore
    //   78: aload #28
    //   80: iconst_0
    //   81: iconst_m1
    //   82: iastore
    //   83: aload #25
    //   85: iconst_3
    //   86: iconst_m1
    //   87: iastore
    //   88: aload #25
    //   90: iconst_2
    //   91: iconst_m1
    //   92: iastore
    //   93: aload #25
    //   95: iconst_1
    //   96: iconst_m1
    //   97: iastore
    //   98: aload #25
    //   100: iconst_0
    //   101: iconst_m1
    //   102: iastore
    //   103: aload_0
    //   104: getfield mBaselineAligned : Z
    //   107: istore #23
    //   109: aload_0
    //   110: getfield mUseLargestChild : Z
    //   113: istore #24
    //   115: iload #21
    //   117: ldc 1073741824
    //   119: if_icmpne -> 128
    //   122: iconst_1
    //   123: istore #15
    //   125: goto -> 131
    //   128: iconst_0
    //   129: istore #15
    //   131: fconst_0
    //   132: fstore_3
    //   133: iconst_0
    //   134: istore #8
    //   136: iconst_0
    //   137: istore #7
    //   139: iconst_0
    //   140: istore #11
    //   142: iconst_0
    //   143: istore #12
    //   145: iconst_0
    //   146: istore #6
    //   148: iconst_0
    //   149: istore #10
    //   151: iconst_0
    //   152: istore #13
    //   154: iconst_1
    //   155: istore #5
    //   157: iconst_0
    //   158: istore #9
    //   160: iload #8
    //   162: iload #19
    //   164: if_icmpge -> 856
    //   167: aload_0
    //   168: iload #8
    //   170: invokevirtual getVirtualChildAt : (I)Landroid/view/View;
    //   173: astore #26
    //   175: aload #26
    //   177: ifnonnull -> 198
    //   180: aload_0
    //   181: aload_0
    //   182: getfield mTotalLength : I
    //   185: aload_0
    //   186: iload #8
    //   188: invokevirtual measureNullChild : (I)I
    //   191: iadd
    //   192: putfield mTotalLength : I
    //   195: goto -> 847
    //   198: aload #26
    //   200: invokevirtual getVisibility : ()I
    //   203: bipush #8
    //   205: if_icmpne -> 224
    //   208: iload #8
    //   210: aload_0
    //   211: aload #26
    //   213: iload #8
    //   215: invokevirtual getChildrenSkipCount : (Landroid/view/View;I)I
    //   218: iadd
    //   219: istore #8
    //   221: goto -> 195
    //   224: aload_0
    //   225: iload #8
    //   227: invokevirtual hasDividerBeforeChildAt : (I)Z
    //   230: ifeq -> 246
    //   233: aload_0
    //   234: aload_0
    //   235: getfield mTotalLength : I
    //   238: aload_0
    //   239: getfield mDividerWidth : I
    //   242: iadd
    //   243: putfield mTotalLength : I
    //   246: aload #26
    //   248: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
    //   251: checkcast android/support/v7/widget/LinearLayoutCompat$LayoutParams
    //   254: astore #29
    //   256: fload_3
    //   257: aload #29
    //   259: getfield weight : F
    //   262: fadd
    //   263: fstore_3
    //   264: iload #21
    //   266: ldc 1073741824
    //   268: if_icmpne -> 380
    //   271: aload #29
    //   273: getfield width : I
    //   276: ifne -> 380
    //   279: aload #29
    //   281: getfield weight : F
    //   284: fconst_0
    //   285: fcmpl
    //   286: ifle -> 380
    //   289: iload #15
    //   291: ifeq -> 317
    //   294: aload_0
    //   295: aload_0
    //   296: getfield mTotalLength : I
    //   299: aload #29
    //   301: getfield leftMargin : I
    //   304: aload #29
    //   306: getfield rightMargin : I
    //   309: iadd
    //   310: iadd
    //   311: putfield mTotalLength : I
    //   314: goto -> 346
    //   317: aload_0
    //   318: getfield mTotalLength : I
    //   321: istore #14
    //   323: aload_0
    //   324: iload #14
    //   326: aload #29
    //   328: getfield leftMargin : I
    //   331: iload #14
    //   333: iadd
    //   334: aload #29
    //   336: getfield rightMargin : I
    //   339: iadd
    //   340: invokestatic max : (II)I
    //   343: putfield mTotalLength : I
    //   346: iload #23
    //   348: ifeq -> 374
    //   351: iconst_0
    //   352: iconst_0
    //   353: invokestatic makeMeasureSpec : (II)I
    //   356: istore #14
    //   358: aload #26
    //   360: iload #14
    //   362: iload #14
    //   364: invokevirtual measure : (II)V
    //   367: iload #7
    //   369: istore #14
    //   371: goto -> 568
    //   374: iconst_1
    //   375: istore #12
    //   377: goto -> 572
    //   380: aload #29
    //   382: getfield width : I
    //   385: ifne -> 411
    //   388: aload #29
    //   390: getfield weight : F
    //   393: fconst_0
    //   394: fcmpl
    //   395: ifle -> 411
    //   398: aload #29
    //   400: bipush #-2
    //   402: putfield width : I
    //   405: iconst_0
    //   406: istore #14
    //   408: goto -> 416
    //   411: ldc_w -2147483648
    //   414: istore #14
    //   416: fload_3
    //   417: fconst_0
    //   418: fcmpl
    //   419: ifne -> 431
    //   422: aload_0
    //   423: getfield mTotalLength : I
    //   426: istore #16
    //   428: goto -> 434
    //   431: iconst_0
    //   432: istore #16
    //   434: aload #26
    //   436: astore #27
    //   438: aload_0
    //   439: aload #26
    //   441: iload #8
    //   443: iload_1
    //   444: iload #16
    //   446: iload_2
    //   447: iconst_0
    //   448: invokevirtual measureChildBeforeLayout : (Landroid/view/View;IIIII)V
    //   451: iload #14
    //   453: ldc_w -2147483648
    //   456: if_icmpeq -> 466
    //   459: aload #29
    //   461: iload #14
    //   463: putfield width : I
    //   466: aload #27
    //   468: invokevirtual getMeasuredWidth : ()I
    //   471: istore #16
    //   473: iload #15
    //   475: ifeq -> 511
    //   478: aload_0
    //   479: aload_0
    //   480: getfield mTotalLength : I
    //   483: aload #29
    //   485: getfield leftMargin : I
    //   488: iload #16
    //   490: iadd
    //   491: aload #29
    //   493: getfield rightMargin : I
    //   496: iadd
    //   497: aload_0
    //   498: aload #27
    //   500: invokevirtual getNextLocationOffset : (Landroid/view/View;)I
    //   503: iadd
    //   504: iadd
    //   505: putfield mTotalLength : I
    //   508: goto -> 550
    //   511: aload_0
    //   512: getfield mTotalLength : I
    //   515: istore #14
    //   517: aload_0
    //   518: iload #14
    //   520: iload #14
    //   522: iload #16
    //   524: iadd
    //   525: aload #29
    //   527: getfield leftMargin : I
    //   530: iadd
    //   531: aload #29
    //   533: getfield rightMargin : I
    //   536: iadd
    //   537: aload_0
    //   538: aload #27
    //   540: invokevirtual getNextLocationOffset : (Landroid/view/View;)I
    //   543: iadd
    //   544: invokestatic max : (II)I
    //   547: putfield mTotalLength : I
    //   550: iload #7
    //   552: istore #14
    //   554: iload #24
    //   556: ifeq -> 568
    //   559: iload #16
    //   561: iload #7
    //   563: invokestatic max : (II)I
    //   566: istore #14
    //   568: iload #14
    //   570: istore #7
    //   572: iload #8
    //   574: istore #17
    //   576: iload #20
    //   578: ldc 1073741824
    //   580: if_icmpeq -> 601
    //   583: aload #29
    //   585: getfield height : I
    //   588: iconst_m1
    //   589: if_icmpne -> 601
    //   592: iconst_1
    //   593: istore #8
    //   595: iconst_1
    //   596: istore #9
    //   598: goto -> 604
    //   601: iconst_0
    //   602: istore #8
    //   604: aload #29
    //   606: getfield topMargin : I
    //   609: aload #29
    //   611: getfield bottomMargin : I
    //   614: iadd
    //   615: istore #14
    //   617: aload #26
    //   619: invokevirtual getMeasuredHeight : ()I
    //   622: iload #14
    //   624: iadd
    //   625: istore #16
    //   627: iload #13
    //   629: aload #26
    //   631: invokevirtual getMeasuredState : ()I
    //   634: invokestatic combineMeasuredStates : (II)I
    //   637: istore #18
    //   639: iload #23
    //   641: ifeq -> 728
    //   644: aload #26
    //   646: invokevirtual getBaseline : ()I
    //   649: istore #22
    //   651: iload #22
    //   653: iconst_m1
    //   654: if_icmpeq -> 728
    //   657: aload #29
    //   659: getfield gravity : I
    //   662: ifge -> 674
    //   665: aload_0
    //   666: getfield mGravity : I
    //   669: istore #13
    //   671: goto -> 681
    //   674: aload #29
    //   676: getfield gravity : I
    //   679: istore #13
    //   681: iload #13
    //   683: bipush #112
    //   685: iand
    //   686: iconst_4
    //   687: ishr
    //   688: bipush #-2
    //   690: iand
    //   691: iconst_1
    //   692: ishr
    //   693: istore #13
    //   695: aload #28
    //   697: iload #13
    //   699: aload #28
    //   701: iload #13
    //   703: iaload
    //   704: iload #22
    //   706: invokestatic max : (II)I
    //   709: iastore
    //   710: aload #25
    //   712: iload #13
    //   714: aload #25
    //   716: iload #13
    //   718: iaload
    //   719: iload #16
    //   721: iload #22
    //   723: isub
    //   724: invokestatic max : (II)I
    //   727: iastore
    //   728: iload #11
    //   730: iload #16
    //   732: invokestatic max : (II)I
    //   735: istore #11
    //   737: iload #5
    //   739: ifeq -> 757
    //   742: aload #29
    //   744: getfield height : I
    //   747: iconst_m1
    //   748: if_icmpne -> 757
    //   751: iconst_1
    //   752: istore #5
    //   754: goto -> 760
    //   757: iconst_0
    //   758: istore #5
    //   760: aload #29
    //   762: getfield weight : F
    //   765: fconst_0
    //   766: fcmpl
    //   767: ifle -> 797
    //   770: iload #8
    //   772: ifeq -> 778
    //   775: goto -> 785
    //   778: iload #16
    //   780: istore #14
    //   782: goto -> 775
    //   785: iload #10
    //   787: iload #14
    //   789: invokestatic max : (II)I
    //   792: istore #8
    //   794: goto -> 822
    //   797: iload #8
    //   799: ifeq -> 806
    //   802: iload #14
    //   804: istore #16
    //   806: iload #6
    //   808: iload #16
    //   810: invokestatic max : (II)I
    //   813: istore #6
    //   815: iload #10
    //   817: istore #8
    //   819: goto -> 794
    //   822: aload_0
    //   823: aload #26
    //   825: iload #17
    //   827: invokevirtual getChildrenSkipCount : (Landroid/view/View;I)I
    //   830: iload #17
    //   832: iadd
    //   833: istore #14
    //   835: iload #18
    //   837: istore #13
    //   839: iload #8
    //   841: istore #10
    //   843: iload #14
    //   845: istore #8
    //   847: iload #8
    //   849: iconst_1
    //   850: iadd
    //   851: istore #8
    //   853: goto -> 160
    //   856: iload #11
    //   858: istore #8
    //   860: aload_0
    //   861: getfield mTotalLength : I
    //   864: ifle -> 889
    //   867: aload_0
    //   868: iload #19
    //   870: invokevirtual hasDividerBeforeChildAt : (I)Z
    //   873: ifeq -> 889
    //   876: aload_0
    //   877: aload_0
    //   878: getfield mTotalLength : I
    //   881: aload_0
    //   882: getfield mDividerWidth : I
    //   885: iadd
    //   886: putfield mTotalLength : I
    //   889: aload #28
    //   891: iconst_1
    //   892: iaload
    //   893: iconst_m1
    //   894: if_icmpne -> 925
    //   897: aload #28
    //   899: iconst_0
    //   900: iaload
    //   901: iconst_m1
    //   902: if_icmpne -> 925
    //   905: aload #28
    //   907: iconst_2
    //   908: iaload
    //   909: iconst_m1
    //   910: if_icmpne -> 925
    //   913: iload #8
    //   915: istore #11
    //   917: aload #28
    //   919: iconst_3
    //   920: iaload
    //   921: iconst_m1
    //   922: if_icmpeq -> 983
    //   925: iload #8
    //   927: aload #28
    //   929: iconst_3
    //   930: iaload
    //   931: aload #28
    //   933: iconst_0
    //   934: iaload
    //   935: aload #28
    //   937: iconst_1
    //   938: iaload
    //   939: aload #28
    //   941: iconst_2
    //   942: iaload
    //   943: invokestatic max : (II)I
    //   946: invokestatic max : (II)I
    //   949: invokestatic max : (II)I
    //   952: aload #25
    //   954: iconst_3
    //   955: iaload
    //   956: aload #25
    //   958: iconst_0
    //   959: iaload
    //   960: aload #25
    //   962: iconst_1
    //   963: iaload
    //   964: aload #25
    //   966: iconst_2
    //   967: iaload
    //   968: invokestatic max : (II)I
    //   971: invokestatic max : (II)I
    //   974: invokestatic max : (II)I
    //   977: iadd
    //   978: invokestatic max : (II)I
    //   981: istore #11
    //   983: iload #24
    //   985: ifeq -> 1169
    //   988: iload #21
    //   990: ldc_w -2147483648
    //   993: if_icmpeq -> 1001
    //   996: iload #21
    //   998: ifne -> 1169
    //   1001: aload_0
    //   1002: iconst_0
    //   1003: putfield mTotalLength : I
    //   1006: iconst_0
    //   1007: istore #8
    //   1009: iload #8
    //   1011: iload #19
    //   1013: if_icmpge -> 1169
    //   1016: aload_0
    //   1017: iload #8
    //   1019: invokevirtual getVirtualChildAt : (I)Landroid/view/View;
    //   1022: astore #26
    //   1024: aload #26
    //   1026: ifnonnull -> 1047
    //   1029: aload_0
    //   1030: aload_0
    //   1031: getfield mTotalLength : I
    //   1034: aload_0
    //   1035: iload #8
    //   1037: invokevirtual measureNullChild : (I)I
    //   1040: iadd
    //   1041: putfield mTotalLength : I
    //   1044: goto -> 1070
    //   1047: aload #26
    //   1049: invokevirtual getVisibility : ()I
    //   1052: bipush #8
    //   1054: if_icmpne -> 1073
    //   1057: iload #8
    //   1059: aload_0
    //   1060: aload #26
    //   1062: iload #8
    //   1064: invokevirtual getChildrenSkipCount : (Landroid/view/View;I)I
    //   1067: iadd
    //   1068: istore #8
    //   1070: goto -> 1160
    //   1073: aload #26
    //   1075: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
    //   1078: checkcast android/support/v7/widget/LinearLayoutCompat$LayoutParams
    //   1081: astore #27
    //   1083: iload #15
    //   1085: ifeq -> 1121
    //   1088: aload_0
    //   1089: aload_0
    //   1090: getfield mTotalLength : I
    //   1093: aload #27
    //   1095: getfield leftMargin : I
    //   1098: iload #7
    //   1100: iadd
    //   1101: aload #27
    //   1103: getfield rightMargin : I
    //   1106: iadd
    //   1107: aload_0
    //   1108: aload #26
    //   1110: invokevirtual getNextLocationOffset : (Landroid/view/View;)I
    //   1113: iadd
    //   1114: iadd
    //   1115: putfield mTotalLength : I
    //   1118: goto -> 1070
    //   1121: aload_0
    //   1122: getfield mTotalLength : I
    //   1125: istore #14
    //   1127: aload_0
    //   1128: iload #14
    //   1130: iload #14
    //   1132: iload #7
    //   1134: iadd
    //   1135: aload #27
    //   1137: getfield leftMargin : I
    //   1140: iadd
    //   1141: aload #27
    //   1143: getfield rightMargin : I
    //   1146: iadd
    //   1147: aload_0
    //   1148: aload #26
    //   1150: invokevirtual getNextLocationOffset : (Landroid/view/View;)I
    //   1153: iadd
    //   1154: invokestatic max : (II)I
    //   1157: putfield mTotalLength : I
    //   1160: iload #8
    //   1162: iconst_1
    //   1163: iadd
    //   1164: istore #8
    //   1166: goto -> 1009
    //   1169: aload_0
    //   1170: aload_0
    //   1171: getfield mTotalLength : I
    //   1174: aload_0
    //   1175: invokevirtual getPaddingLeft : ()I
    //   1178: aload_0
    //   1179: invokevirtual getPaddingRight : ()I
    //   1182: iadd
    //   1183: iadd
    //   1184: putfield mTotalLength : I
    //   1187: aload_0
    //   1188: getfield mTotalLength : I
    //   1191: aload_0
    //   1192: invokevirtual getSuggestedMinimumWidth : ()I
    //   1195: invokestatic max : (II)I
    //   1198: iload_1
    //   1199: iconst_0
    //   1200: invokestatic resolveSizeAndState : (III)I
    //   1203: istore #17
    //   1205: ldc_w 16777215
    //   1208: iload #17
    //   1210: iand
    //   1211: aload_0
    //   1212: getfield mTotalLength : I
    //   1215: isub
    //   1216: istore #14
    //   1218: iload #12
    //   1220: ifne -> 1352
    //   1223: iload #14
    //   1225: ifeq -> 1237
    //   1228: fload_3
    //   1229: fconst_0
    //   1230: fcmpl
    //   1231: ifle -> 1237
    //   1234: goto -> 1352
    //   1237: iload #6
    //   1239: iload #10
    //   1241: invokestatic max : (II)I
    //   1244: istore #8
    //   1246: iload #24
    //   1248: ifeq -> 1341
    //   1251: iload #21
    //   1253: ldc 1073741824
    //   1255: if_icmpeq -> 1341
    //   1258: iconst_0
    //   1259: istore #6
    //   1261: iload #6
    //   1263: iload #19
    //   1265: if_icmpge -> 1341
    //   1268: aload_0
    //   1269: iload #6
    //   1271: invokevirtual getVirtualChildAt : (I)Landroid/view/View;
    //   1274: astore #25
    //   1276: aload #25
    //   1278: ifnull -> 1332
    //   1281: aload #25
    //   1283: invokevirtual getVisibility : ()I
    //   1286: bipush #8
    //   1288: if_icmpne -> 1294
    //   1291: goto -> 1332
    //   1294: aload #25
    //   1296: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
    //   1299: checkcast android/support/v7/widget/LinearLayoutCompat$LayoutParams
    //   1302: getfield weight : F
    //   1305: fconst_0
    //   1306: fcmpl
    //   1307: ifle -> 1332
    //   1310: aload #25
    //   1312: iload #7
    //   1314: ldc 1073741824
    //   1316: invokestatic makeMeasureSpec : (II)I
    //   1319: aload #25
    //   1321: invokevirtual getMeasuredHeight : ()I
    //   1324: ldc 1073741824
    //   1326: invokestatic makeMeasureSpec : (II)I
    //   1329: invokevirtual measure : (II)V
    //   1332: iload #6
    //   1334: iconst_1
    //   1335: iadd
    //   1336: istore #6
    //   1338: goto -> 1261
    //   1341: iload #8
    //   1343: istore #6
    //   1345: iload #5
    //   1347: istore #7
    //   1349: goto -> 2077
    //   1352: aload_0
    //   1353: getfield mWeightSum : F
    //   1356: fconst_0
    //   1357: fcmpl
    //   1358: ifle -> 1366
    //   1361: aload_0
    //   1362: getfield mWeightSum : F
    //   1365: fstore_3
    //   1366: aload #28
    //   1368: iconst_3
    //   1369: iconst_m1
    //   1370: iastore
    //   1371: aload #28
    //   1373: iconst_2
    //   1374: iconst_m1
    //   1375: iastore
    //   1376: aload #28
    //   1378: iconst_1
    //   1379: iconst_m1
    //   1380: iastore
    //   1381: aload #28
    //   1383: iconst_0
    //   1384: iconst_m1
    //   1385: iastore
    //   1386: aload #25
    //   1388: iconst_3
    //   1389: iconst_m1
    //   1390: iastore
    //   1391: aload #25
    //   1393: iconst_2
    //   1394: iconst_m1
    //   1395: iastore
    //   1396: aload #25
    //   1398: iconst_1
    //   1399: iconst_m1
    //   1400: iastore
    //   1401: aload #25
    //   1403: iconst_0
    //   1404: iconst_m1
    //   1405: iastore
    //   1406: aload_0
    //   1407: iconst_0
    //   1408: putfield mTotalLength : I
    //   1411: iconst_m1
    //   1412: istore #10
    //   1414: iconst_0
    //   1415: istore #12
    //   1417: iload #5
    //   1419: istore #7
    //   1421: iload #6
    //   1423: istore #8
    //   1425: iload #13
    //   1427: istore #5
    //   1429: iload #14
    //   1431: istore #6
    //   1433: iload #12
    //   1435: iload #19
    //   1437: if_icmpge -> 1953
    //   1440: aload_0
    //   1441: iload #12
    //   1443: invokevirtual getVirtualChildAt : (I)Landroid/view/View;
    //   1446: astore #26
    //   1448: aload #26
    //   1450: ifnull -> 1944
    //   1453: aload #26
    //   1455: invokevirtual getVisibility : ()I
    //   1458: bipush #8
    //   1460: if_icmpne -> 1466
    //   1463: goto -> 1944
    //   1466: aload #26
    //   1468: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
    //   1471: checkcast android/support/v7/widget/LinearLayoutCompat$LayoutParams
    //   1474: astore #27
    //   1476: aload #27
    //   1478: getfield weight : F
    //   1481: fstore #4
    //   1483: fload #4
    //   1485: fconst_0
    //   1486: fcmpl
    //   1487: ifle -> 1650
    //   1490: iload #6
    //   1492: i2f
    //   1493: fload #4
    //   1495: fmul
    //   1496: fload_3
    //   1497: fdiv
    //   1498: f2i
    //   1499: istore #13
    //   1501: iload_2
    //   1502: aload_0
    //   1503: invokevirtual getPaddingTop : ()I
    //   1506: aload_0
    //   1507: invokevirtual getPaddingBottom : ()I
    //   1510: iadd
    //   1511: aload #27
    //   1513: getfield topMargin : I
    //   1516: iadd
    //   1517: aload #27
    //   1519: getfield bottomMargin : I
    //   1522: iadd
    //   1523: aload #27
    //   1525: getfield height : I
    //   1528: invokestatic getChildMeasureSpec : (III)I
    //   1531: istore #16
    //   1533: aload #27
    //   1535: getfield width : I
    //   1538: ifne -> 1583
    //   1541: iload #21
    //   1543: ldc 1073741824
    //   1545: if_icmpeq -> 1551
    //   1548: goto -> 1583
    //   1551: iload #13
    //   1553: ifle -> 1563
    //   1556: iload #13
    //   1558: istore #11
    //   1560: goto -> 1566
    //   1563: iconst_0
    //   1564: istore #11
    //   1566: aload #26
    //   1568: iload #11
    //   1570: ldc 1073741824
    //   1572: invokestatic makeMeasureSpec : (II)I
    //   1575: iload #16
    //   1577: invokevirtual measure : (II)V
    //   1580: goto -> 1619
    //   1583: aload #26
    //   1585: invokevirtual getMeasuredWidth : ()I
    //   1588: iload #13
    //   1590: iadd
    //   1591: istore #14
    //   1593: iload #14
    //   1595: istore #11
    //   1597: iload #14
    //   1599: ifge -> 1605
    //   1602: iconst_0
    //   1603: istore #11
    //   1605: aload #26
    //   1607: iload #11
    //   1609: ldc 1073741824
    //   1611: invokestatic makeMeasureSpec : (II)I
    //   1614: iload #16
    //   1616: invokevirtual measure : (II)V
    //   1619: iload #5
    //   1621: aload #26
    //   1623: invokevirtual getMeasuredState : ()I
    //   1626: ldc_w -16777216
    //   1629: iand
    //   1630: invokestatic combineMeasuredStates : (II)I
    //   1633: istore #5
    //   1635: fload_3
    //   1636: fload #4
    //   1638: fsub
    //   1639: fstore_3
    //   1640: iload #6
    //   1642: iload #13
    //   1644: isub
    //   1645: istore #6
    //   1647: goto -> 1650
    //   1650: iload #15
    //   1652: ifeq -> 1691
    //   1655: aload_0
    //   1656: aload_0
    //   1657: getfield mTotalLength : I
    //   1660: aload #26
    //   1662: invokevirtual getMeasuredWidth : ()I
    //   1665: aload #27
    //   1667: getfield leftMargin : I
    //   1670: iadd
    //   1671: aload #27
    //   1673: getfield rightMargin : I
    //   1676: iadd
    //   1677: aload_0
    //   1678: aload #26
    //   1680: invokevirtual getNextLocationOffset : (Landroid/view/View;)I
    //   1683: iadd
    //   1684: iadd
    //   1685: putfield mTotalLength : I
    //   1688: goto -> 1736
    //   1691: aload_0
    //   1692: getfield mTotalLength : I
    //   1695: istore #11
    //   1697: aload_0
    //   1698: iload #11
    //   1700: aload #26
    //   1702: invokevirtual getMeasuredWidth : ()I
    //   1705: iload #11
    //   1707: iadd
    //   1708: aload #27
    //   1710: getfield leftMargin : I
    //   1713: iadd
    //   1714: aload #27
    //   1716: getfield rightMargin : I
    //   1719: iadd
    //   1720: aload_0
    //   1721: aload #26
    //   1723: invokevirtual getNextLocationOffset : (Landroid/view/View;)I
    //   1726: iadd
    //   1727: invokestatic max : (II)I
    //   1730: putfield mTotalLength : I
    //   1733: goto -> 1688
    //   1736: iload #20
    //   1738: ldc 1073741824
    //   1740: if_icmpeq -> 1758
    //   1743: aload #27
    //   1745: getfield height : I
    //   1748: iconst_m1
    //   1749: if_icmpne -> 1758
    //   1752: iconst_1
    //   1753: istore #11
    //   1755: goto -> 1761
    //   1758: iconst_0
    //   1759: istore #11
    //   1761: aload #27
    //   1763: getfield topMargin : I
    //   1766: aload #27
    //   1768: getfield bottomMargin : I
    //   1771: iadd
    //   1772: istore #16
    //   1774: aload #26
    //   1776: invokevirtual getMeasuredHeight : ()I
    //   1779: iload #16
    //   1781: iadd
    //   1782: istore #14
    //   1784: iload #10
    //   1786: iload #14
    //   1788: invokestatic max : (II)I
    //   1791: istore #13
    //   1793: iload #11
    //   1795: ifeq -> 1805
    //   1798: iload #16
    //   1800: istore #10
    //   1802: goto -> 1809
    //   1805: iload #14
    //   1807: istore #10
    //   1809: iload #8
    //   1811: iload #10
    //   1813: invokestatic max : (II)I
    //   1816: istore #10
    //   1818: iload #7
    //   1820: ifeq -> 1838
    //   1823: aload #27
    //   1825: getfield height : I
    //   1828: iconst_m1
    //   1829: if_icmpne -> 1838
    //   1832: iconst_1
    //   1833: istore #7
    //   1835: goto -> 1841
    //   1838: iconst_0
    //   1839: istore #7
    //   1841: iload #23
    //   1843: ifeq -> 1933
    //   1846: aload #26
    //   1848: invokevirtual getBaseline : ()I
    //   1851: istore #11
    //   1853: iload #11
    //   1855: iconst_m1
    //   1856: if_icmpeq -> 1933
    //   1859: aload #27
    //   1861: getfield gravity : I
    //   1864: ifge -> 1876
    //   1867: aload_0
    //   1868: getfield mGravity : I
    //   1871: istore #8
    //   1873: goto -> 1883
    //   1876: aload #27
    //   1878: getfield gravity : I
    //   1881: istore #8
    //   1883: iload #8
    //   1885: bipush #112
    //   1887: iand
    //   1888: iconst_4
    //   1889: ishr
    //   1890: bipush #-2
    //   1892: iand
    //   1893: iconst_1
    //   1894: ishr
    //   1895: istore #8
    //   1897: aload #28
    //   1899: iload #8
    //   1901: aload #28
    //   1903: iload #8
    //   1905: iaload
    //   1906: iload #11
    //   1908: invokestatic max : (II)I
    //   1911: iastore
    //   1912: aload #25
    //   1914: iload #8
    //   1916: aload #25
    //   1918: iload #8
    //   1920: iaload
    //   1921: iload #14
    //   1923: iload #11
    //   1925: isub
    //   1926: invokestatic max : (II)I
    //   1929: iastore
    //   1930: goto -> 1933
    //   1933: iload #10
    //   1935: istore #8
    //   1937: iload #13
    //   1939: istore #10
    //   1941: goto -> 1944
    //   1944: iload #12
    //   1946: iconst_1
    //   1947: iadd
    //   1948: istore #12
    //   1950: goto -> 1433
    //   1953: aload_0
    //   1954: aload_0
    //   1955: getfield mTotalLength : I
    //   1958: aload_0
    //   1959: invokevirtual getPaddingLeft : ()I
    //   1962: aload_0
    //   1963: invokevirtual getPaddingRight : ()I
    //   1966: iadd
    //   1967: iadd
    //   1968: putfield mTotalLength : I
    //   1971: aload #28
    //   1973: iconst_1
    //   1974: iaload
    //   1975: iconst_m1
    //   1976: if_icmpne -> 2007
    //   1979: aload #28
    //   1981: iconst_0
    //   1982: iaload
    //   1983: iconst_m1
    //   1984: if_icmpne -> 2007
    //   1987: aload #28
    //   1989: iconst_2
    //   1990: iaload
    //   1991: iconst_m1
    //   1992: if_icmpne -> 2007
    //   1995: iload #10
    //   1997: istore #6
    //   1999: aload #28
    //   2001: iconst_3
    //   2002: iaload
    //   2003: iconst_m1
    //   2004: if_icmpeq -> 2065
    //   2007: iload #10
    //   2009: aload #28
    //   2011: iconst_3
    //   2012: iaload
    //   2013: aload #28
    //   2015: iconst_0
    //   2016: iaload
    //   2017: aload #28
    //   2019: iconst_1
    //   2020: iaload
    //   2021: aload #28
    //   2023: iconst_2
    //   2024: iaload
    //   2025: invokestatic max : (II)I
    //   2028: invokestatic max : (II)I
    //   2031: invokestatic max : (II)I
    //   2034: aload #25
    //   2036: iconst_3
    //   2037: iaload
    //   2038: aload #25
    //   2040: iconst_0
    //   2041: iaload
    //   2042: aload #25
    //   2044: iconst_1
    //   2045: iaload
    //   2046: aload #25
    //   2048: iconst_2
    //   2049: iaload
    //   2050: invokestatic max : (II)I
    //   2053: invokestatic max : (II)I
    //   2056: invokestatic max : (II)I
    //   2059: iadd
    //   2060: invokestatic max : (II)I
    //   2063: istore #6
    //   2065: iload #5
    //   2067: istore #13
    //   2069: iload #6
    //   2071: istore #11
    //   2073: iload #8
    //   2075: istore #6
    //   2077: iload #7
    //   2079: ifne -> 2092
    //   2082: iload #20
    //   2084: ldc 1073741824
    //   2086: if_icmpeq -> 2092
    //   2089: goto -> 2096
    //   2092: iload #11
    //   2094: istore #6
    //   2096: aload_0
    //   2097: iload #17
    //   2099: ldc_w -16777216
    //   2102: iload #13
    //   2104: iand
    //   2105: ior
    //   2106: iload #6
    //   2108: aload_0
    //   2109: invokevirtual getPaddingTop : ()I
    //   2112: aload_0
    //   2113: invokevirtual getPaddingBottom : ()I
    //   2116: iadd
    //   2117: iadd
    //   2118: aload_0
    //   2119: invokevirtual getSuggestedMinimumHeight : ()I
    //   2122: invokestatic max : (II)I
    //   2125: iload_2
    //   2126: iload #13
    //   2128: bipush #16
    //   2130: ishl
    //   2131: invokestatic resolveSizeAndState : (III)I
    //   2134: invokevirtual setMeasuredDimension : (II)V
    //   2137: iload #9
    //   2139: ifeq -> 2149
    //   2142: aload_0
    //   2143: iload #19
    //   2145: iload_1
    //   2146: invokespecial forceUniformHeight : (II)V
    //   2149: return }
  
  int measureNullChild(int paramInt) { return 0; }
  
  void measureVertical(int paramInt1, int paramInt2) { // Byte code:
    //   0: aload_0
    //   1: iconst_0
    //   2: putfield mTotalLength : I
    //   5: aload_0
    //   6: invokevirtual getVirtualChildCount : ()I
    //   9: istore #13
    //   11: iload_1
    //   12: invokestatic getMode : (I)I
    //   15: istore #20
    //   17: iload_2
    //   18: invokestatic getMode : (I)I
    //   21: istore #6
    //   23: aload_0
    //   24: getfield mBaselineAlignedChildIndex : I
    //   27: istore #21
    //   29: aload_0
    //   30: getfield mUseLargestChild : Z
    //   33: istore #22
    //   35: fconst_0
    //   36: fstore_3
    //   37: iconst_0
    //   38: istore #10
    //   40: iconst_0
    //   41: istore #15
    //   43: iconst_0
    //   44: istore #9
    //   46: iconst_0
    //   47: istore #5
    //   49: iconst_0
    //   50: istore #8
    //   52: iconst_0
    //   53: istore #11
    //   55: iconst_0
    //   56: istore #14
    //   58: iconst_1
    //   59: istore #7
    //   61: iconst_0
    //   62: istore #12
    //   64: iload #11
    //   66: iload #13
    //   68: if_icmpge -> 643
    //   71: aload_0
    //   72: iload #11
    //   74: invokevirtual getVirtualChildAt : (I)Landroid/view/View;
    //   77: astore #23
    //   79: aload #23
    //   81: ifnonnull -> 102
    //   84: aload_0
    //   85: aload_0
    //   86: getfield mTotalLength : I
    //   89: aload_0
    //   90: iload #11
    //   92: invokevirtual measureNullChild : (I)I
    //   95: iadd
    //   96: putfield mTotalLength : I
    //   99: goto -> 634
    //   102: aload #23
    //   104: invokevirtual getVisibility : ()I
    //   107: bipush #8
    //   109: if_icmpne -> 128
    //   112: iload #11
    //   114: aload_0
    //   115: aload #23
    //   117: iload #11
    //   119: invokevirtual getChildrenSkipCount : (Landroid/view/View;I)I
    //   122: iadd
    //   123: istore #11
    //   125: goto -> 634
    //   128: aload_0
    //   129: iload #11
    //   131: invokevirtual hasDividerBeforeChildAt : (I)Z
    //   134: ifeq -> 150
    //   137: aload_0
    //   138: aload_0
    //   139: getfield mTotalLength : I
    //   142: aload_0
    //   143: getfield mDividerHeight : I
    //   146: iadd
    //   147: putfield mTotalLength : I
    //   150: aload #23
    //   152: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
    //   155: checkcast android/support/v7/widget/LinearLayoutCompat$LayoutParams
    //   158: astore #25
    //   160: fload_3
    //   161: aload #25
    //   163: getfield weight : F
    //   166: fadd
    //   167: fstore_3
    //   168: iload #6
    //   170: ldc 1073741824
    //   172: if_icmpne -> 228
    //   175: aload #25
    //   177: getfield height : I
    //   180: ifne -> 228
    //   183: aload #25
    //   185: getfield weight : F
    //   188: fconst_0
    //   189: fcmpl
    //   190: ifle -> 228
    //   193: aload_0
    //   194: getfield mTotalLength : I
    //   197: istore #14
    //   199: aload_0
    //   200: iload #14
    //   202: aload #25
    //   204: getfield topMargin : I
    //   207: iload #14
    //   209: iadd
    //   210: aload #25
    //   212: getfield bottomMargin : I
    //   215: iadd
    //   216: invokestatic max : (II)I
    //   219: putfield mTotalLength : I
    //   222: iconst_1
    //   223: istore #14
    //   225: goto -> 377
    //   228: aload #25
    //   230: getfield height : I
    //   233: ifne -> 259
    //   236: aload #25
    //   238: getfield weight : F
    //   241: fconst_0
    //   242: fcmpl
    //   243: ifle -> 259
    //   246: aload #25
    //   248: bipush #-2
    //   250: putfield height : I
    //   253: iconst_0
    //   254: istore #16
    //   256: goto -> 264
    //   259: ldc_w -2147483648
    //   262: istore #16
    //   264: fload_3
    //   265: fconst_0
    //   266: fcmpl
    //   267: ifne -> 279
    //   270: aload_0
    //   271: getfield mTotalLength : I
    //   274: istore #17
    //   276: goto -> 282
    //   279: iconst_0
    //   280: istore #17
    //   282: aload #23
    //   284: astore #24
    //   286: aload_0
    //   287: aload #23
    //   289: iload #11
    //   291: iload_1
    //   292: iconst_0
    //   293: iload_2
    //   294: iload #17
    //   296: invokevirtual measureChildBeforeLayout : (Landroid/view/View;IIIII)V
    //   299: iload #16
    //   301: ldc_w -2147483648
    //   304: if_icmpeq -> 314
    //   307: aload #25
    //   309: iload #16
    //   311: putfield height : I
    //   314: aload #24
    //   316: invokevirtual getMeasuredHeight : ()I
    //   319: istore #16
    //   321: aload_0
    //   322: getfield mTotalLength : I
    //   325: istore #17
    //   327: aload_0
    //   328: iload #17
    //   330: iload #17
    //   332: iload #16
    //   334: iadd
    //   335: aload #25
    //   337: getfield topMargin : I
    //   340: iadd
    //   341: aload #25
    //   343: getfield bottomMargin : I
    //   346: iadd
    //   347: aload_0
    //   348: aload #24
    //   350: invokevirtual getNextLocationOffset : (Landroid/view/View;)I
    //   353: iadd
    //   354: invokestatic max : (II)I
    //   357: putfield mTotalLength : I
    //   360: iload #22
    //   362: ifeq -> 377
    //   365: iload #16
    //   367: iload #9
    //   369: invokestatic max : (II)I
    //   372: istore #9
    //   374: goto -> 377
    //   377: iload #5
    //   379: istore #16
    //   381: iload #11
    //   383: istore #19
    //   385: iload #21
    //   387: iflt -> 407
    //   390: iload #21
    //   392: iload #19
    //   394: iconst_1
    //   395: iadd
    //   396: if_icmpne -> 407
    //   399: aload_0
    //   400: aload_0
    //   401: getfield mTotalLength : I
    //   404: putfield mBaselineChildTop : I
    //   407: iload #19
    //   409: iload #21
    //   411: if_icmpge -> 435
    //   414: aload #25
    //   416: getfield weight : F
    //   419: fconst_0
    //   420: fcmpl
    //   421: ifle -> 435
    //   424: new java/lang/RuntimeException
    //   427: dup
    //   428: ldc_w 'A child of LinearLayout with index less than mBaselineAlignedChildIndex has weight > 0, which won't work.  Either remove the weight, or don't set mBaselineAlignedChildIndex.'
    //   431: invokespecial <init> : (Ljava/lang/String;)V
    //   434: athrow
    //   435: iload #20
    //   437: ldc 1073741824
    //   439: if_icmpeq -> 460
    //   442: aload #25
    //   444: getfield width : I
    //   447: iconst_m1
    //   448: if_icmpne -> 460
    //   451: iconst_1
    //   452: istore #11
    //   454: iconst_1
    //   455: istore #12
    //   457: goto -> 463
    //   460: iconst_0
    //   461: istore #11
    //   463: aload #25
    //   465: getfield leftMargin : I
    //   468: aload #25
    //   470: getfield rightMargin : I
    //   473: iadd
    //   474: istore #17
    //   476: aload #23
    //   478: invokevirtual getMeasuredWidth : ()I
    //   481: iload #17
    //   483: iadd
    //   484: istore #18
    //   486: iload #15
    //   488: iload #18
    //   490: invokestatic max : (II)I
    //   493: istore #15
    //   495: iload #10
    //   497: aload #23
    //   499: invokevirtual getMeasuredState : ()I
    //   502: invokestatic combineMeasuredStates : (II)I
    //   505: istore #10
    //   507: iload #7
    //   509: ifeq -> 527
    //   512: aload #25
    //   514: getfield width : I
    //   517: iconst_m1
    //   518: if_icmpne -> 527
    //   521: iconst_1
    //   522: istore #5
    //   524: goto -> 530
    //   527: iconst_0
    //   528: istore #5
    //   530: aload #25
    //   532: getfield weight : F
    //   535: fconst_0
    //   536: fcmpl
    //   537: ifle -> 575
    //   540: iload #11
    //   542: ifeq -> 548
    //   545: goto -> 555
    //   548: iload #18
    //   550: istore #17
    //   552: goto -> 545
    //   555: iload #16
    //   557: iload #17
    //   559: invokestatic max : (II)I
    //   562: istore #11
    //   564: iload #8
    //   566: istore #7
    //   568: iload #11
    //   570: istore #8
    //   572: goto -> 597
    //   575: iload #11
    //   577: ifeq -> 584
    //   580: iload #17
    //   582: istore #18
    //   584: iload #8
    //   586: iload #18
    //   588: invokestatic max : (II)I
    //   591: istore #7
    //   593: iload #16
    //   595: istore #8
    //   597: aload_0
    //   598: aload #23
    //   600: iload #19
    //   602: invokevirtual getChildrenSkipCount : (Landroid/view/View;I)I
    //   605: istore #16
    //   607: iload #5
    //   609: istore #11
    //   611: iload #8
    //   613: istore #5
    //   615: iload #16
    //   617: iload #19
    //   619: iadd
    //   620: istore #16
    //   622: iload #7
    //   624: istore #8
    //   626: iload #11
    //   628: istore #7
    //   630: iload #16
    //   632: istore #11
    //   634: iload #11
    //   636: iconst_1
    //   637: iadd
    //   638: istore #11
    //   640: goto -> 64
    //   643: iload #10
    //   645: istore #16
    //   647: iload #15
    //   649: istore #10
    //   651: aload_0
    //   652: getfield mTotalLength : I
    //   655: ifle -> 683
    //   658: aload_0
    //   659: iload #13
    //   661: invokevirtual hasDividerBeforeChildAt : (I)Z
    //   664: ifeq -> 683
    //   667: aload_0
    //   668: aload_0
    //   669: getfield mTotalLength : I
    //   672: aload_0
    //   673: getfield mDividerHeight : I
    //   676: iadd
    //   677: putfield mTotalLength : I
    //   680: goto -> 683
    //   683: iload #13
    //   685: istore #15
    //   687: iload #22
    //   689: ifeq -> 857
    //   692: iload #6
    //   694: istore #11
    //   696: iload #11
    //   698: ldc_w -2147483648
    //   701: if_icmpeq -> 723
    //   704: iload #10
    //   706: istore #13
    //   708: iload #11
    //   710: ifne -> 716
    //   713: goto -> 723
    //   716: iload #13
    //   718: istore #10
    //   720: goto -> 857
    //   723: aload_0
    //   724: iconst_0
    //   725: putfield mTotalLength : I
    //   728: iconst_0
    //   729: istore #11
    //   731: iload #10
    //   733: istore #13
    //   735: iload #11
    //   737: iload #15
    //   739: if_icmpge -> 716
    //   742: aload_0
    //   743: iload #11
    //   745: invokevirtual getVirtualChildAt : (I)Landroid/view/View;
    //   748: astore #23
    //   750: aload #23
    //   752: ifnonnull -> 773
    //   755: aload_0
    //   756: aload_0
    //   757: getfield mTotalLength : I
    //   760: aload_0
    //   761: iload #11
    //   763: invokevirtual measureNullChild : (I)I
    //   766: iadd
    //   767: putfield mTotalLength : I
    //   770: goto -> 796
    //   773: aload #23
    //   775: invokevirtual getVisibility : ()I
    //   778: bipush #8
    //   780: if_icmpne -> 799
    //   783: iload #11
    //   785: aload_0
    //   786: aload #23
    //   788: iload #11
    //   790: invokevirtual getChildrenSkipCount : (Landroid/view/View;I)I
    //   793: iadd
    //   794: istore #11
    //   796: goto -> 848
    //   799: aload #23
    //   801: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
    //   804: checkcast android/support/v7/widget/LinearLayoutCompat$LayoutParams
    //   807: astore #24
    //   809: aload_0
    //   810: getfield mTotalLength : I
    //   813: istore #13
    //   815: aload_0
    //   816: iload #13
    //   818: iload #13
    //   820: iload #9
    //   822: iadd
    //   823: aload #24
    //   825: getfield topMargin : I
    //   828: iadd
    //   829: aload #24
    //   831: getfield bottomMargin : I
    //   834: iadd
    //   835: aload_0
    //   836: aload #23
    //   838: invokevirtual getNextLocationOffset : (Landroid/view/View;)I
    //   841: iadd
    //   842: invokestatic max : (II)I
    //   845: putfield mTotalLength : I
    //   848: iload #11
    //   850: iconst_1
    //   851: iadd
    //   852: istore #11
    //   854: goto -> 731
    //   857: iload #6
    //   859: istore #13
    //   861: aload_0
    //   862: aload_0
    //   863: getfield mTotalLength : I
    //   866: aload_0
    //   867: invokevirtual getPaddingTop : ()I
    //   870: aload_0
    //   871: invokevirtual getPaddingBottom : ()I
    //   874: iadd
    //   875: iadd
    //   876: putfield mTotalLength : I
    //   879: aload_0
    //   880: getfield mTotalLength : I
    //   883: aload_0
    //   884: invokevirtual getSuggestedMinimumHeight : ()I
    //   887: invokestatic max : (II)I
    //   890: iload_2
    //   891: iconst_0
    //   892: invokestatic resolveSizeAndState : (III)I
    //   895: istore #17
    //   897: ldc_w 16777215
    //   900: iload #17
    //   902: iand
    //   903: aload_0
    //   904: getfield mTotalLength : I
    //   907: isub
    //   908: istore #6
    //   910: iload #14
    //   912: ifne -> 1044
    //   915: iload #6
    //   917: ifeq -> 929
    //   920: fload_3
    //   921: fconst_0
    //   922: fcmpl
    //   923: ifle -> 929
    //   926: goto -> 1044
    //   929: iload #8
    //   931: iload #5
    //   933: invokestatic max : (II)I
    //   936: istore #6
    //   938: iload #22
    //   940: ifeq -> 1033
    //   943: iload #13
    //   945: ldc 1073741824
    //   947: if_icmpeq -> 1033
    //   950: iconst_0
    //   951: istore #5
    //   953: iload #5
    //   955: iload #15
    //   957: if_icmpge -> 1033
    //   960: aload_0
    //   961: iload #5
    //   963: invokevirtual getVirtualChildAt : (I)Landroid/view/View;
    //   966: astore #23
    //   968: aload #23
    //   970: ifnull -> 1024
    //   973: aload #23
    //   975: invokevirtual getVisibility : ()I
    //   978: bipush #8
    //   980: if_icmpne -> 986
    //   983: goto -> 1024
    //   986: aload #23
    //   988: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
    //   991: checkcast android/support/v7/widget/LinearLayoutCompat$LayoutParams
    //   994: getfield weight : F
    //   997: fconst_0
    //   998: fcmpl
    //   999: ifle -> 1024
    //   1002: aload #23
    //   1004: aload #23
    //   1006: invokevirtual getMeasuredWidth : ()I
    //   1009: ldc 1073741824
    //   1011: invokestatic makeMeasureSpec : (II)I
    //   1014: iload #9
    //   1016: ldc 1073741824
    //   1018: invokestatic makeMeasureSpec : (II)I
    //   1021: invokevirtual measure : (II)V
    //   1024: iload #5
    //   1026: iconst_1
    //   1027: iadd
    //   1028: istore #5
    //   1030: goto -> 953
    //   1033: iload #10
    //   1035: istore #8
    //   1037: iload #6
    //   1039: istore #5
    //   1041: goto -> 1520
    //   1044: aload_0
    //   1045: getfield mWeightSum : F
    //   1048: fconst_0
    //   1049: fcmpl
    //   1050: ifle -> 1058
    //   1053: aload_0
    //   1054: getfield mWeightSum : F
    //   1057: fstore_3
    //   1058: aload_0
    //   1059: iconst_0
    //   1060: putfield mTotalLength : I
    //   1063: iload #6
    //   1065: istore #9
    //   1067: iconst_0
    //   1068: istore #11
    //   1070: iload #8
    //   1072: istore #6
    //   1074: iload #10
    //   1076: istore #8
    //   1078: iload #16
    //   1080: istore #5
    //   1082: iload #11
    //   1084: istore #10
    //   1086: iload #10
    //   1088: iload #15
    //   1090: if_icmpge -> 1494
    //   1093: aload_0
    //   1094: iload #10
    //   1096: invokevirtual getVirtualChildAt : (I)Landroid/view/View;
    //   1099: astore #23
    //   1101: aload #23
    //   1103: invokevirtual getVisibility : ()I
    //   1106: bipush #8
    //   1108: if_icmpne -> 1114
    //   1111: goto -> 1485
    //   1114: aload #23
    //   1116: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
    //   1119: checkcast android/support/v7/widget/LinearLayoutCompat$LayoutParams
    //   1122: astore #24
    //   1124: aload #24
    //   1126: getfield weight : F
    //   1129: fstore #4
    //   1131: fload #4
    //   1133: fconst_0
    //   1134: fcmpl
    //   1135: ifle -> 1322
    //   1138: iload #9
    //   1140: i2f
    //   1141: fload #4
    //   1143: fmul
    //   1144: fload_3
    //   1145: fdiv
    //   1146: f2i
    //   1147: istore #14
    //   1149: aload_0
    //   1150: invokevirtual getPaddingLeft : ()I
    //   1153: istore #16
    //   1155: aload_0
    //   1156: invokevirtual getPaddingRight : ()I
    //   1159: istore #18
    //   1161: iload #9
    //   1163: iload #14
    //   1165: isub
    //   1166: istore #11
    //   1168: aload #24
    //   1170: getfield leftMargin : I
    //   1173: istore #9
    //   1175: aload #24
    //   1177: getfield rightMargin : I
    //   1180: istore #19
    //   1182: aload #24
    //   1184: getfield width : I
    //   1187: istore #21
    //   1189: fload_3
    //   1190: fload #4
    //   1192: fsub
    //   1193: fstore_3
    //   1194: iload_1
    //   1195: iload #16
    //   1197: iload #18
    //   1199: iadd
    //   1200: iload #9
    //   1202: iadd
    //   1203: iload #19
    //   1205: iadd
    //   1206: iload #21
    //   1208: invokestatic getChildMeasureSpec : (III)I
    //   1211: istore #16
    //   1213: aload #24
    //   1215: getfield height : I
    //   1218: ifne -> 1263
    //   1221: iload #13
    //   1223: ldc 1073741824
    //   1225: if_icmpeq -> 1231
    //   1228: goto -> 1263
    //   1231: iload #14
    //   1233: ifle -> 1243
    //   1236: iload #14
    //   1238: istore #9
    //   1240: goto -> 1246
    //   1243: iconst_0
    //   1244: istore #9
    //   1246: aload #23
    //   1248: iload #16
    //   1250: iload #9
    //   1252: ldc 1073741824
    //   1254: invokestatic makeMeasureSpec : (II)I
    //   1257: invokevirtual measure : (II)V
    //   1260: goto -> 1299
    //   1263: aload #23
    //   1265: invokevirtual getMeasuredHeight : ()I
    //   1268: iload #14
    //   1270: iadd
    //   1271: istore #14
    //   1273: iload #14
    //   1275: istore #9
    //   1277: iload #14
    //   1279: ifge -> 1285
    //   1282: iconst_0
    //   1283: istore #9
    //   1285: aload #23
    //   1287: iload #16
    //   1289: iload #9
    //   1291: ldc 1073741824
    //   1293: invokestatic makeMeasureSpec : (II)I
    //   1296: invokevirtual measure : (II)V
    //   1299: iload #5
    //   1301: aload #23
    //   1303: invokevirtual getMeasuredState : ()I
    //   1306: sipush #-256
    //   1309: iand
    //   1310: invokestatic combineMeasuredStates : (II)I
    //   1313: istore #5
    //   1315: iload #11
    //   1317: istore #9
    //   1319: goto -> 1322
    //   1322: aload #24
    //   1324: getfield leftMargin : I
    //   1327: aload #24
    //   1329: getfield rightMargin : I
    //   1332: iadd
    //   1333: istore #14
    //   1335: aload #23
    //   1337: invokevirtual getMeasuredWidth : ()I
    //   1340: iload #14
    //   1342: iadd
    //   1343: istore #16
    //   1345: iload #8
    //   1347: iload #16
    //   1349: invokestatic max : (II)I
    //   1352: istore #11
    //   1354: iload #20
    //   1356: ldc 1073741824
    //   1358: if_icmpeq -> 1376
    //   1361: aload #24
    //   1363: getfield width : I
    //   1366: iconst_m1
    //   1367: if_icmpne -> 1376
    //   1370: iconst_1
    //   1371: istore #8
    //   1373: goto -> 1379
    //   1376: iconst_0
    //   1377: istore #8
    //   1379: iload #8
    //   1381: ifeq -> 1391
    //   1384: iload #14
    //   1386: istore #8
    //   1388: goto -> 1395
    //   1391: iload #16
    //   1393: istore #8
    //   1395: iload #6
    //   1397: iload #8
    //   1399: invokestatic max : (II)I
    //   1402: istore #14
    //   1404: iload #7
    //   1406: ifeq -> 1424
    //   1409: aload #24
    //   1411: getfield width : I
    //   1414: iconst_m1
    //   1415: if_icmpne -> 1424
    //   1418: iconst_1
    //   1419: istore #6
    //   1421: goto -> 1427
    //   1424: iconst_0
    //   1425: istore #6
    //   1427: aload_0
    //   1428: getfield mTotalLength : I
    //   1431: istore #7
    //   1433: aload_0
    //   1434: iload #7
    //   1436: aload #23
    //   1438: invokevirtual getMeasuredHeight : ()I
    //   1441: iload #7
    //   1443: iadd
    //   1444: aload #24
    //   1446: getfield topMargin : I
    //   1449: iadd
    //   1450: aload #24
    //   1452: getfield bottomMargin : I
    //   1455: iadd
    //   1456: aload_0
    //   1457: aload #23
    //   1459: invokevirtual getNextLocationOffset : (Landroid/view/View;)I
    //   1462: iadd
    //   1463: invokestatic max : (II)I
    //   1466: putfield mTotalLength : I
    //   1469: iload #11
    //   1471: istore #8
    //   1473: iload #14
    //   1475: istore #11
    //   1477: iload #6
    //   1479: istore #7
    //   1481: iload #11
    //   1483: istore #6
    //   1485: iload #10
    //   1487: iconst_1
    //   1488: iadd
    //   1489: istore #10
    //   1491: goto -> 1086
    //   1494: aload_0
    //   1495: aload_0
    //   1496: getfield mTotalLength : I
    //   1499: aload_0
    //   1500: invokevirtual getPaddingTop : ()I
    //   1503: aload_0
    //   1504: invokevirtual getPaddingBottom : ()I
    //   1507: iadd
    //   1508: iadd
    //   1509: putfield mTotalLength : I
    //   1512: iload #5
    //   1514: istore #16
    //   1516: iload #6
    //   1518: istore #5
    //   1520: iload #7
    //   1522: ifne -> 1535
    //   1525: iload #20
    //   1527: ldc 1073741824
    //   1529: if_icmpeq -> 1535
    //   1532: goto -> 1539
    //   1535: iload #8
    //   1537: istore #5
    //   1539: aload_0
    //   1540: iload #5
    //   1542: aload_0
    //   1543: invokevirtual getPaddingLeft : ()I
    //   1546: aload_0
    //   1547: invokevirtual getPaddingRight : ()I
    //   1550: iadd
    //   1551: iadd
    //   1552: aload_0
    //   1553: invokevirtual getSuggestedMinimumWidth : ()I
    //   1556: invokestatic max : (II)I
    //   1559: iload_1
    //   1560: iload #16
    //   1562: invokestatic resolveSizeAndState : (III)I
    //   1565: iload #17
    //   1567: invokevirtual setMeasuredDimension : (II)V
    //   1570: iload #12
    //   1572: ifeq -> 1582
    //   1575: aload_0
    //   1576: iload #15
    //   1578: iload_2
    //   1579: invokespecial forceUniformWidth : (II)V
    //   1582: return }
  
  protected void onDraw(Canvas paramCanvas) {
    if (this.mDivider == null)
      return; 
    if (this.mOrientation == 1) {
      drawDividersVertical(paramCanvas);
      return;
    } 
    drawDividersHorizontal(paramCanvas);
  }
  
  public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent) {
    super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
    paramAccessibilityEvent.setClassName(LinearLayoutCompat.class.getName());
  }
  
  public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo) {
    super.onInitializeAccessibilityNodeInfo(paramAccessibilityNodeInfo);
    paramAccessibilityNodeInfo.setClassName(LinearLayoutCompat.class.getName());
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (this.mOrientation == 1) {
      layoutVertical(paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    } 
    layoutHorizontal(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2) {
    if (this.mOrientation == 1) {
      measureVertical(paramInt1, paramInt2);
      return;
    } 
    measureHorizontal(paramInt1, paramInt2);
  }
  
  public void setBaselineAligned(boolean paramBoolean) { this.mBaselineAligned = paramBoolean; }
  
  public void setBaselineAlignedChildIndex(int paramInt) {
    if (paramInt < 0 || paramInt >= getChildCount()) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("base aligned child index out of range (0, ");
      stringBuilder.append(getChildCount());
      stringBuilder.append(")");
      throw new IllegalArgumentException(stringBuilder.toString());
    } 
    this.mBaselineAlignedChildIndex = paramInt;
  }
  
  public void setDividerDrawable(Drawable paramDrawable) {
    if (paramDrawable == this.mDivider)
      return; 
    this.mDivider = paramDrawable;
    boolean bool = false;
    if (paramDrawable != null) {
      this.mDividerWidth = paramDrawable.getIntrinsicWidth();
      this.mDividerHeight = paramDrawable.getIntrinsicHeight();
    } else {
      this.mDividerWidth = 0;
      this.mDividerHeight = 0;
    } 
    if (paramDrawable == null)
      bool = true; 
    setWillNotDraw(bool);
    requestLayout();
  }
  
  public void setDividerPadding(int paramInt) { this.mDividerPadding = paramInt; }
  
  public void setGravity(int paramInt) {
    if (this.mGravity != paramInt) {
      int i = paramInt;
      if ((0x800007 & paramInt) == 0)
        i = paramInt | 0x800003; 
      paramInt = i;
      if ((i & 0x70) == 0)
        paramInt = i | 0x30; 
      this.mGravity = paramInt;
      requestLayout();
    } 
  }
  
  public void setHorizontalGravity(int paramInt) {
    paramInt &= 0x800007;
    if ((0x800007 & this.mGravity) != paramInt) {
      this.mGravity = paramInt | this.mGravity & 0xFF7FFFF8;
      requestLayout();
    } 
  }
  
  public void setMeasureWithLargestChildEnabled(boolean paramBoolean) { this.mUseLargestChild = paramBoolean; }
  
  public void setOrientation(int paramInt) {
    if (this.mOrientation != paramInt) {
      this.mOrientation = paramInt;
      requestLayout();
    } 
  }
  
  public void setShowDividers(int paramInt) {
    if (paramInt != this.mShowDividers)
      requestLayout(); 
    this.mShowDividers = paramInt;
  }
  
  public void setVerticalGravity(int paramInt) {
    paramInt &= 0x70;
    if ((this.mGravity & 0x70) != paramInt) {
      this.mGravity = paramInt | this.mGravity & 0xFFFFFF8F;
      requestLayout();
    } 
  }
  
  public void setWeightSum(float paramFloat) { this.mWeightSum = Math.max(0.0F, paramFloat); }
  
  public boolean shouldDelayChildPressedState() { return false; }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface DividerMode {}
  
  public static class LayoutParams extends ViewGroup.MarginLayoutParams {
    public int gravity = -1;
    
    public float weight;
    
    public LayoutParams(int param1Int1, int param1Int2) {
      super(param1Int1, param1Int2);
      this.weight = 0.0F;
    }
    
    public LayoutParams(int param1Int1, int param1Int2, float param1Float) {
      super(param1Int1, param1Int2);
      this.weight = param1Float;
    }
    
    public LayoutParams(Context param1Context, AttributeSet param1AttributeSet) {
      super(param1Context, param1AttributeSet);
      TypedArray typedArray = param1Context.obtainStyledAttributes(param1AttributeSet, R.styleable.LinearLayoutCompat_Layout);
      this.weight = typedArray.getFloat(R.styleable.LinearLayoutCompat_Layout_android_layout_weight, 0.0F);
      this.gravity = typedArray.getInt(R.styleable.LinearLayoutCompat_Layout_android_layout_gravity, -1);
      typedArray.recycle();
    }
    
    public LayoutParams(LayoutParams param1LayoutParams) {
      super(param1LayoutParams);
      this.weight = param1LayoutParams.weight;
      this.gravity = param1LayoutParams.gravity;
    }
    
    public LayoutParams(ViewGroup.LayoutParams param1LayoutParams) { super(param1LayoutParams); }
    
    public LayoutParams(ViewGroup.MarginLayoutParams param1MarginLayoutParams) { super(param1MarginLayoutParams); }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface OrientationMode {}
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/LinearLayoutCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */