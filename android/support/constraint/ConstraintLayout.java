package android.support.constraint;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.constraint.solver.Metrics;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.support.constraint.solver.widgets.ConstraintWidget;
import android.support.constraint.solver.widgets.ConstraintWidgetContainer;
import android.support.constraint.solver.widgets.Guideline;
import android.support.constraint.solver.widgets.ResolutionAnchor;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.HashMap;

public class ConstraintLayout extends ViewGroup {
  static final boolean ALLOWS_EMBEDDED = false;
  
  private static final boolean DEBUG = false;
  
  public static final int DESIGN_INFO_ID = 0;
  
  private static final String TAG = "ConstraintLayout";
  
  private static final boolean USE_CONSTRAINTS_HELPER = true;
  
  public static final String VERSION = "ConstraintLayout-1.1.0";
  
  SparseArray<View> mChildrenByIds = new SparseArray();
  
  private ArrayList<ConstraintHelper> mConstraintHelpers = new ArrayList(4);
  
  private ConstraintSet mConstraintSet = null;
  
  private int mConstraintSetId = -1;
  
  private HashMap<String, Integer> mDesignIds = new HashMap();
  
  private boolean mDirtyHierarchy = true;
  
  private int mLastMeasureHeight = -1;
  
  int mLastMeasureHeightMode = 0;
  
  int mLastMeasureHeightSize = -1;
  
  private int mLastMeasureWidth = -1;
  
  int mLastMeasureWidthMode = 0;
  
  int mLastMeasureWidthSize = -1;
  
  ConstraintWidgetContainer mLayoutWidget = new ConstraintWidgetContainer();
  
  private int mMaxHeight = Integer.MAX_VALUE;
  
  private int mMaxWidth = Integer.MAX_VALUE;
  
  private Metrics mMetrics;
  
  private int mMinHeight = 0;
  
  private int mMinWidth = 0;
  
  private int mOptimizationLevel = 3;
  
  private final ArrayList<ConstraintWidget> mVariableDimensionsWidgets = new ArrayList(100);
  
  public ConstraintLayout(Context paramContext) {
    super(paramContext);
    init(null);
  }
  
  public ConstraintLayout(Context paramContext, AttributeSet paramAttributeSet) {
    super(paramContext, paramAttributeSet);
    init(paramAttributeSet);
  }
  
  public ConstraintLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
    super(paramContext, paramAttributeSet, paramInt);
    init(paramAttributeSet);
  }
  
  private final ConstraintWidget getTargetWidget(int paramInt) {
    if (paramInt == 0)
      return this.mLayoutWidget; 
    View view = (View)this.mChildrenByIds.get(paramInt);
    return (view == this) ? this.mLayoutWidget : ((view == null) ? null : ((LayoutParams)view.getLayoutParams()).widget);
  }
  
  private void init(AttributeSet paramAttributeSet) {
    this.mLayoutWidget.setCompanionWidget(this);
    this.mChildrenByIds.put(getId(), this);
    this.mConstraintSet = null;
    if (paramAttributeSet != null) {
      TypedArray typedArray = getContext().obtainStyledAttributes(paramAttributeSet, R.styleable.ConstraintLayout_Layout);
      int i = typedArray.getIndexCount();
      byte b = 0;
      while (true) {
        if (b < i) {
          int j = typedArray.getIndex(b);
          if (j == R.styleable.ConstraintLayout_Layout_android_minWidth) {
            this.mMinWidth = typedArray.getDimensionPixelOffset(j, this.mMinWidth);
          } else if (j == R.styleable.ConstraintLayout_Layout_android_minHeight) {
            this.mMinHeight = typedArray.getDimensionPixelOffset(j, this.mMinHeight);
          } else if (j == R.styleable.ConstraintLayout_Layout_android_maxWidth) {
            this.mMaxWidth = typedArray.getDimensionPixelOffset(j, this.mMaxWidth);
          } else if (j == R.styleable.ConstraintLayout_Layout_android_maxHeight) {
            this.mMaxHeight = typedArray.getDimensionPixelOffset(j, this.mMaxHeight);
          } else if (j == R.styleable.ConstraintLayout_Layout_layout_optimizationLevel) {
            this.mOptimizationLevel = typedArray.getInt(j, this.mOptimizationLevel);
          } else if (j == R.styleable.ConstraintLayout_Layout_constraintSet) {
            j = typedArray.getResourceId(j, 0);
            try {
              this.mConstraintSet = new ConstraintSet();
              this.mConstraintSet.load(getContext(), j);
            } catch (android.content.res.Resources.NotFoundException notFoundException) {
              this.mConstraintSet = null;
            } 
            this.mConstraintSetId = j;
          } 
          b++;
          continue;
        } 
        typedArray.recycle();
        this.mLayoutWidget.setOptimizationLevel(this.mOptimizationLevel);
        return;
      } 
    } 
    this.mLayoutWidget.setOptimizationLevel(this.mOptimizationLevel);
  }
  
  private void internalMeasureChildren(int paramInt1, int paramInt2) {
    int i = getPaddingTop() + getPaddingBottom();
    int j = getPaddingLeft() + getPaddingRight();
    int k = getChildCount();
    byte b = 0;
    while (true) {
      int m = paramInt1;
      ConstraintLayout constraintLayout = this;
      if (b < k) {
        View view = constraintLayout.getChildAt(b);
        if (view.getVisibility() != 8) {
          LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
          ConstraintWidget constraintWidget = layoutParams.widget;
          if (!layoutParams.isGuideline && !layoutParams.isHelper) {
            int i2;
            int i1;
            int n;
            constraintWidget.setVisibility(view.getVisibility());
            int i3 = layoutParams.width;
            int i4 = layoutParams.height;
            if (layoutParams.horizontalDimensionFixed || layoutParams.verticalDimensionFixed || (!layoutParams.horizontalDimensionFixed && layoutParams.matchConstraintDefaultWidth == 1) || layoutParams.width == -1 || (!layoutParams.verticalDimensionFixed && (layoutParams.matchConstraintDefaultHeight == 1 || layoutParams.height == -1))) {
              n = 1;
            } else {
              n = 0;
            } 
            if (n) {
              boolean bool;
              if (i3 == 0) {
                i1 = getChildMeasureSpec(m, j, -2);
                n = 1;
              } else if (i3 == -1) {
                i1 = getChildMeasureSpec(m, j, -1);
                n = 0;
              } else {
                if (i3 == -2) {
                  n = 1;
                } else {
                  n = 0;
                } 
                i1 = getChildMeasureSpec(m, j, i3);
              } 
              if (i4 == 0) {
                i2 = getChildMeasureSpec(paramInt2, i, -2);
                m = 1;
              } else if (i4 == -1) {
                i2 = getChildMeasureSpec(paramInt2, i, -1);
                m = 0;
              } else {
                if (i4 == -2) {
                  m = 1;
                } else {
                  m = 0;
                } 
                i2 = getChildMeasureSpec(paramInt2, i, i4);
              } 
              view.measure(i1, i2);
              if (constraintLayout.mMetrics != null) {
                Metrics metrics = constraintLayout.mMetrics;
                metrics.measures++;
              } 
              if (i3 == -2) {
                bool = true;
              } else {
                bool = false;
              } 
              constraintWidget.setWidthWrapContent(bool);
              if (i4 == -2) {
                bool = true;
              } else {
                bool = false;
              } 
              constraintWidget.setHeightWrapContent(bool);
              i1 = view.getMeasuredWidth();
              i2 = view.getMeasuredHeight();
            } else {
              n = 0;
              m = 0;
              i2 = i4;
              i1 = i3;
            } 
            constraintWidget.setWidth(i1);
            constraintWidget.setHeight(i2);
            if (n)
              constraintWidget.setWrapWidth(i1); 
            if (m != 0)
              constraintWidget.setWrapHeight(i2); 
            if (layoutParams.needsBaseline) {
              n = view.getBaseline();
              if (n != -1)
                constraintWidget.setBaselineDistance(n); 
            } 
          } 
        } 
        b++;
        continue;
      } 
      break;
    } 
  }
  
  private void internalMeasureDimensions(int paramInt1, int paramInt2) {
    ConstraintLayout constraintLayout = this;
    int k = getPaddingTop() + getPaddingBottom();
    int j = getPaddingLeft() + getPaddingRight();
    int i = getChildCount();
    int m = 0;
    while (true) {
      long l = 1L;
      if (m < i) {
        View view = constraintLayout.getChildAt(m);
        if (view.getVisibility() != 8) {
          LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
          ConstraintWidget constraintWidget = layoutParams.widget;
          if (!layoutParams.isGuideline && !layoutParams.isHelper) {
            constraintWidget.setVisibility(view.getVisibility());
            int i1 = layoutParams.width;
            int i2 = layoutParams.height;
            if (i1 == 0 || i2 == 0) {
              constraintWidget.getResolutionWidth().invalidate();
              constraintWidget.getResolutionHeight().invalidate();
            } else {
              boolean bool1;
              boolean bool;
              int i3;
              if (i1 == -2) {
                i3 = 1;
              } else {
                i3 = 0;
              } 
              int i4 = getChildMeasureSpec(paramInt1, j, i1);
              if (i2 == -2) {
                bool = true;
              } else {
                bool = false;
              } 
              view.measure(i4, getChildMeasureSpec(paramInt2, k, i2));
              if (constraintLayout.mMetrics != null) {
                Metrics metrics = constraintLayout.mMetrics;
                metrics.measures++;
              } 
              if (i1 == -2) {
                bool1 = true;
              } else {
                bool1 = false;
              } 
              constraintWidget.setWidthWrapContent(bool1);
              if (i2 == -2) {
                bool1 = true;
              } else {
                bool1 = false;
              } 
              constraintWidget.setHeightWrapContent(bool1);
              i1 = view.getMeasuredWidth();
              i2 = view.getMeasuredHeight();
              constraintWidget.setWidth(i1);
              constraintWidget.setHeight(i2);
              if (i3)
                constraintWidget.setWrapWidth(i1); 
              if (bool)
                constraintWidget.setWrapHeight(i2); 
              if (layoutParams.needsBaseline) {
                i3 = view.getBaseline();
                if (i3 != -1)
                  constraintWidget.setBaselineDistance(i3); 
              } 
              if (layoutParams.horizontalDimensionFixed && layoutParams.verticalDimensionFixed) {
                constraintWidget.getResolutionWidth().resolve(i1);
                constraintWidget.getResolutionHeight().resolve(i2);
              } 
            } 
          } 
        } 
        m++;
        continue;
      } 
      break;
    } 
    constraintLayout.mLayoutWidget.solveGraph();
    int n = i;
    byte b = 0;
    i = j;
    m = k;
    while (true) {
      int i1 = paramInt1;
      constraintLayout = this;
      if (b < n) {
        View view = constraintLayout.getChildAt(b);
        if (view.getVisibility() != 8) {
          LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
          ConstraintWidget constraintWidget = layoutParams.widget;
          if (!layoutParams.isGuideline && !layoutParams.isHelper) {
            constraintWidget.setVisibility(view.getVisibility());
            int i3 = layoutParams.width;
            int i2 = layoutParams.height;
            if (i3 == 0 || i2 == 0) {
              boolean bool;
              int i4;
              ResolutionAnchor resolutionAnchor1 = constraintWidget.getAnchor(ConstraintAnchor.Type.LEFT).getResolutionNode();
              ResolutionAnchor resolutionAnchor2 = constraintWidget.getAnchor(ConstraintAnchor.Type.RIGHT).getResolutionNode();
              if (constraintWidget.getAnchor(ConstraintAnchor.Type.LEFT).getTarget() != null && constraintWidget.getAnchor(ConstraintAnchor.Type.RIGHT).getTarget() != null) {
                i4 = 1;
              } else {
                i4 = 0;
              } 
              ResolutionAnchor resolutionAnchor3 = constraintWidget.getAnchor(ConstraintAnchor.Type.TOP).getResolutionNode();
              ResolutionAnchor resolutionAnchor4 = constraintWidget.getAnchor(ConstraintAnchor.Type.BOTTOM).getResolutionNode();
              if (constraintWidget.getAnchor(ConstraintAnchor.Type.TOP).getTarget() != null && constraintWidget.getAnchor(ConstraintAnchor.Type.BOTTOM).getTarget() != null) {
                bool = true;
              } else {
                bool = false;
              } 
              if (i3 == 0 && i2 == 0 && i4 && bool) {
                long l = 1L;
              } else {
                if (constraintLayout.mLayoutWidget.getHorizontalDimensionBehaviour() != ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                  k = 1;
                } else {
                  k = 0;
                } 
                if (constraintLayout.mLayoutWidget.getVerticalDimensionBehaviour() != ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                  j = 1;
                } else {
                  j = 0;
                } 
                if (k == 0)
                  constraintWidget.getResolutionWidth().invalidate(); 
                if (j == 0)
                  constraintWidget.getResolutionHeight().invalidate(); 
                if (i3 == 0) {
                  if (k != 0 && constraintWidget.isSpreadWidth() && i4 && resolutionAnchor1.isResolved() && resolutionAnchor2.isResolved()) {
                    i4 = (int)(resolutionAnchor2.getResolvedValue() - resolutionAnchor1.getResolvedValue());
                    constraintWidget.getResolutionWidth().resolve(i4);
                    i1 = getChildMeasureSpec(i1, i, i4);
                  } else {
                    i1 = getChildMeasureSpec(i1, i, -2);
                    k = 1;
                    i4 = 0;
                    int i6 = i;
                  } 
                } else {
                  int i6 = i;
                  if (i3 == -1) {
                    i1 = getChildMeasureSpec(i1, i6, -1);
                    i4 = i3;
                  } else {
                    if (i3 == -2) {
                      i4 = 1;
                    } else {
                      i4 = 0;
                    } 
                    i6 = getChildMeasureSpec(i1, i6, i3);
                    i1 = k;
                    k = i4;
                    i4 = i1;
                    i1 = i6;
                    i6 = i;
                  } 
                } 
                int i5 = k;
                k = 0;
                i3 = i4;
                i4 = i5;
                i5 = i;
              } 
            } 
          } 
        } 
      } else {
        break;
      } 
      b++;
    } 
  }
  
  private void setChildrenConstraints() { throw new RuntimeException("d2j fail translate: java.lang.RuntimeException: can not merge Z and I\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.copyTypes(TypeTransformer.java:311)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.fixTypes(TypeTransformer.java:226)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:207)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n"); }
  
  private void setSelfDimensionBehaviour(int paramInt1, int paramInt2) { // Byte code:
    //   0: iload_1
    //   1: invokestatic getMode : (I)I
    //   4: istore #6
    //   6: iload_1
    //   7: invokestatic getSize : (I)I
    //   10: istore_1
    //   11: iload_2
    //   12: invokestatic getMode : (I)I
    //   15: istore_3
    //   16: iload_2
    //   17: invokestatic getSize : (I)I
    //   20: istore_2
    //   21: aload_0
    //   22: invokevirtual getPaddingTop : ()I
    //   25: istore #4
    //   27: aload_0
    //   28: invokevirtual getPaddingBottom : ()I
    //   31: istore #5
    //   33: aload_0
    //   34: invokevirtual getPaddingLeft : ()I
    //   37: istore #7
    //   39: aload_0
    //   40: invokevirtual getPaddingRight : ()I
    //   43: istore #8
    //   45: getstatic android/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour.FIXED : Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   48: astore #9
    //   50: getstatic android/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour.FIXED : Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   53: astore #10
    //   55: aload_0
    //   56: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
    //   59: pop
    //   60: iload #6
    //   62: ldc_w -2147483648
    //   65: if_icmpeq -> 112
    //   68: iload #6
    //   70: ifeq -> 104
    //   73: iload #6
    //   75: ldc_w 1073741824
    //   78: if_icmpeq -> 86
    //   81: iconst_0
    //   82: istore_1
    //   83: goto -> 117
    //   86: aload_0
    //   87: getfield mMaxWidth : I
    //   90: iload_1
    //   91: invokestatic min : (II)I
    //   94: iload #7
    //   96: iload #8
    //   98: iadd
    //   99: isub
    //   100: istore_1
    //   101: goto -> 117
    //   104: getstatic android/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour.WRAP_CONTENT : Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   107: astore #9
    //   109: goto -> 81
    //   112: getstatic android/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour.WRAP_CONTENT : Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   115: astore #9
    //   117: iload_3
    //   118: ldc_w -2147483648
    //   121: if_icmpeq -> 166
    //   124: iload_3
    //   125: ifeq -> 158
    //   128: iload_3
    //   129: ldc_w 1073741824
    //   132: if_icmpeq -> 140
    //   135: iconst_0
    //   136: istore_2
    //   137: goto -> 171
    //   140: aload_0
    //   141: getfield mMaxHeight : I
    //   144: iload_2
    //   145: invokestatic min : (II)I
    //   148: iload #4
    //   150: iload #5
    //   152: iadd
    //   153: isub
    //   154: istore_2
    //   155: goto -> 171
    //   158: getstatic android/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour.WRAP_CONTENT : Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   161: astore #10
    //   163: goto -> 135
    //   166: getstatic android/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour.WRAP_CONTENT : Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   169: astore #10
    //   171: aload_0
    //   172: getfield mLayoutWidget : Landroid/support/constraint/solver/widgets/ConstraintWidgetContainer;
    //   175: iconst_0
    //   176: invokevirtual setMinWidth : (I)V
    //   179: aload_0
    //   180: getfield mLayoutWidget : Landroid/support/constraint/solver/widgets/ConstraintWidgetContainer;
    //   183: iconst_0
    //   184: invokevirtual setMinHeight : (I)V
    //   187: aload_0
    //   188: getfield mLayoutWidget : Landroid/support/constraint/solver/widgets/ConstraintWidgetContainer;
    //   191: aload #9
    //   193: invokevirtual setHorizontalDimensionBehaviour : (Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;)V
    //   196: aload_0
    //   197: getfield mLayoutWidget : Landroid/support/constraint/solver/widgets/ConstraintWidgetContainer;
    //   200: iload_1
    //   201: invokevirtual setWidth : (I)V
    //   204: aload_0
    //   205: getfield mLayoutWidget : Landroid/support/constraint/solver/widgets/ConstraintWidgetContainer;
    //   208: aload #10
    //   210: invokevirtual setVerticalDimensionBehaviour : (Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;)V
    //   213: aload_0
    //   214: getfield mLayoutWidget : Landroid/support/constraint/solver/widgets/ConstraintWidgetContainer;
    //   217: iload_2
    //   218: invokevirtual setHeight : (I)V
    //   221: aload_0
    //   222: getfield mLayoutWidget : Landroid/support/constraint/solver/widgets/ConstraintWidgetContainer;
    //   225: aload_0
    //   226: getfield mMinWidth : I
    //   229: aload_0
    //   230: invokevirtual getPaddingLeft : ()I
    //   233: isub
    //   234: aload_0
    //   235: invokevirtual getPaddingRight : ()I
    //   238: isub
    //   239: invokevirtual setMinWidth : (I)V
    //   242: aload_0
    //   243: getfield mLayoutWidget : Landroid/support/constraint/solver/widgets/ConstraintWidgetContainer;
    //   246: aload_0
    //   247: getfield mMinHeight : I
    //   250: aload_0
    //   251: invokevirtual getPaddingTop : ()I
    //   254: isub
    //   255: aload_0
    //   256: invokevirtual getPaddingBottom : ()I
    //   259: isub
    //   260: invokevirtual setMinHeight : (I)V
    //   263: return }
  
  private void updateHierarchy() {
    boolean bool1;
    int i = getChildCount();
    boolean bool2 = false;
    byte b = 0;
    while (true) {
      bool1 = bool2;
      if (b < i) {
        if (getChildAt(b).isLayoutRequested()) {
          bool1 = true;
          break;
        } 
        b++;
        continue;
      } 
      break;
    } 
    if (bool1) {
      this.mVariableDimensionsWidgets.clear();
      setChildrenConstraints();
    } 
  }
  
  private void updatePostMeasures() {
    int i = getChildCount();
    byte b2 = 0;
    byte b1;
    for (b1 = 0; b1 < i; b1++) {
      View view = getChildAt(b1);
      if (view instanceof Placeholder)
        ((Placeholder)view).updatePostMeasure(this); 
    } 
    i = this.mConstraintHelpers.size();
    if (i > 0)
      for (b1 = b2; b1 < i; b1++)
        ((ConstraintHelper)this.mConstraintHelpers.get(b1)).updatePostMeasure(this);  
  }
  
  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams) {
    super.addView(paramView, paramInt, paramLayoutParams);
    if (Build.VERSION.SDK_INT < 14)
      onViewAdded(paramView); 
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams) { return paramLayoutParams instanceof LayoutParams; }
  
  public void dispatchDraw(Canvas paramCanvas) {
    super.dispatchDraw(paramCanvas);
    if (isInEditMode()) {
      int i = getChildCount();
      float f1 = getWidth();
      float f2 = getHeight();
      byte b;
      for (b = 0; b < i; b++) {
        View view = getChildAt(b);
        if (view.getVisibility() != 8) {
          Object object = view.getTag();
          if (object != null && object instanceof String) {
            String[] arrayOfString = ((String)object).split(",");
            if (arrayOfString.length == 4) {
              int k = Integer.parseInt(arrayOfString[0]);
              int n = Integer.parseInt(arrayOfString[1]);
              int m = Integer.parseInt(arrayOfString[2]);
              int j = Integer.parseInt(arrayOfString[3]);
              k = (int)(k / 1080.0F * f1);
              n = (int)(n / 1920.0F * f2);
              m = (int)(m / 1080.0F * f1);
              j = (int)(j / 1920.0F * f2);
              Paint paint = new Paint();
              paint.setColor(-65536);
              float f3 = k;
              float f4 = n;
              float f5 = (k + m);
              paramCanvas.drawLine(f3, f4, f5, f4, paint);
              float f6 = (n + j);
              paramCanvas.drawLine(f5, f4, f5, f6, paint);
              paramCanvas.drawLine(f5, f6, f3, f6, paint);
              paramCanvas.drawLine(f3, f6, f3, f4, paint);
              paint.setColor(-16711936);
              paramCanvas.drawLine(f3, f4, f5, f6, paint);
              paramCanvas.drawLine(f3, f6, f5, f4, paint);
            } 
          } 
        } 
      } 
    } 
  }
  
  public void fillMetrics(Metrics paramMetrics) {
    this.mMetrics = paramMetrics;
    this.mLayoutWidget.fillMetrics(paramMetrics);
  }
  
  protected LayoutParams generateDefaultLayoutParams() { return new LayoutParams(-2, -2); }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet) { return new LayoutParams(getContext(), paramAttributeSet); }
  
  protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams) { return new LayoutParams(paramLayoutParams); }
  
  public Object getDesignInformation(int paramInt, Object paramObject) {
    if (paramInt == 0 && paramObject instanceof String) {
      paramObject = (String)paramObject;
      if (this.mDesignIds != null && this.mDesignIds.containsKey(paramObject))
        return this.mDesignIds.get(paramObject); 
    } 
    return null;
  }
  
  public int getMaxHeight() { return this.mMaxHeight; }
  
  public int getMaxWidth() { return this.mMaxWidth; }
  
  public int getMinHeight() { return this.mMinHeight; }
  
  public int getMinWidth() { return this.mMinWidth; }
  
  public int getOptimizationLevel() { return this.mLayoutWidget.getOptimizationLevel(); }
  
  public View getViewById(int paramInt) { return (View)this.mChildrenByIds.get(paramInt); }
  
  public final ConstraintWidget getViewWidget(View paramView) { return (paramView == this) ? this.mLayoutWidget : ((paramView == null) ? null : ((LayoutParams)paramView.getLayoutParams()).widget); }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    paramInt3 = getChildCount();
    paramBoolean = isInEditMode();
    paramInt2 = 0;
    for (paramInt1 = 0; paramInt1 < paramInt3; paramInt1++) {
      View view = getChildAt(paramInt1);
      LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
      ConstraintWidget constraintWidget = layoutParams.widget;
      if ((view.getVisibility() != 8 || layoutParams.isGuideline || layoutParams.isHelper || paramBoolean) && !layoutParams.isInPlaceholder) {
        paramInt4 = constraintWidget.getDrawX();
        int i = constraintWidget.getDrawY();
        int j = constraintWidget.getWidth() + paramInt4;
        int k = constraintWidget.getHeight() + i;
        view.layout(paramInt4, i, j, k);
        if (view instanceof Placeholder) {
          view = ((Placeholder)view).getContent();
          if (view != null) {
            view.setVisibility(0);
            view.layout(paramInt4, i, j, k);
          } 
        } 
      } 
    } 
    paramInt3 = this.mConstraintHelpers.size();
    if (paramInt3 > 0)
      for (paramInt1 = paramInt2; paramInt1 < paramInt3; paramInt1++)
        ((ConstraintHelper)this.mConstraintHelpers.get(paramInt1)).updatePostLayout(this);  
  }
  
  protected void onMeasure(int paramInt1, int paramInt2) {
    boolean bool;
    int i1;
    System.currentTimeMillis();
    int j = View.MeasureSpec.getMode(paramInt1);
    int k = View.MeasureSpec.getSize(paramInt1);
    int m = View.MeasureSpec.getMode(paramInt2);
    int n = View.MeasureSpec.getSize(paramInt2);
    if (this.mLastMeasureWidth != -1)
      i = this.mLastMeasureHeight; 
    if (j == 1073741824 && m == 1073741824 && k == this.mLastMeasureWidth)
      i = this.mLastMeasureHeight; 
    if (j == this.mLastMeasureWidthMode && m == this.mLastMeasureHeightMode) {
      i = 1;
    } else {
      i = 0;
    } 
    if (i && k == this.mLastMeasureWidthSize)
      i1 = this.mLastMeasureHeightSize; 
    if (i && j == Integer.MIN_VALUE && m == 1073741824 && k >= this.mLastMeasureWidth)
      i1 = this.mLastMeasureHeight; 
    if (i && j == 1073741824 && m == Integer.MIN_VALUE && k == this.mLastMeasureWidth)
      i = this.mLastMeasureHeight; 
    this.mLastMeasureWidthMode = j;
    this.mLastMeasureHeightMode = m;
    this.mLastMeasureWidthSize = k;
    this.mLastMeasureHeightSize = n;
    int i = getPaddingLeft();
    j = getPaddingTop();
    this.mLayoutWidget.setX(i);
    this.mLayoutWidget.setY(j);
    this.mLayoutWidget.setMaxWidth(this.mMaxWidth);
    this.mLayoutWidget.setMaxHeight(this.mMaxHeight);
    if (Build.VERSION.SDK_INT >= 17) {
      boolean bool1;
      ConstraintWidgetContainer constraintWidgetContainer = this.mLayoutWidget;
      if (getLayoutDirection() == 1) {
        bool1 = true;
      } else {
        bool1 = false;
      } 
      constraintWidgetContainer.setRtl(bool1);
    } 
    setSelfDimensionBehaviour(paramInt1, paramInt2);
    int i3 = this.mLayoutWidget.getWidth();
    int i4 = this.mLayoutWidget.getHeight();
    if (this.mDirtyHierarchy) {
      this.mDirtyHierarchy = false;
      updateHierarchy();
    } 
    if ((this.mOptimizationLevel & 0x8) == 8) {
      bool = true;
    } else {
      bool = false;
    } 
    if (bool) {
      this.mLayoutWidget.preOptimize();
      this.mLayoutWidget.optimizeForDimensions(i3, i4);
      internalMeasureDimensions(paramInt1, paramInt2);
    } else {
      internalMeasureChildren(paramInt1, paramInt2);
    } 
    updatePostMeasures();
    if (getChildCount() > 0)
      solveLinearSystem("First pass"); 
    int i2 = this.mVariableDimensionsWidgets.size();
    int i5 = j + getPaddingBottom();
    m = i + getPaddingRight();
    if (i2 > 0) {
      boolean bool2;
      boolean bool1;
      if (this.mLayoutWidget.getHorizontalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
        bool1 = true;
      } else {
        bool1 = false;
      } 
      if (this.mLayoutWidget.getVerticalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
        bool2 = true;
      } else {
        bool2 = false;
      } 
      k = Math.max(this.mLayoutWidget.getWidth(), this.mMinWidth);
      i = Math.max(this.mLayoutWidget.getHeight(), this.mMinHeight);
      byte b = 0;
      n = 0;
      j = 0;
      while (b < i2) {
        ConstraintWidget constraintWidget = (ConstraintWidget)this.mVariableDimensionsWidgets.get(b);
        View view = (View)constraintWidget.getCompanionWidget();
        if (view != null) {
          LayoutParams layoutParams = (LayoutParams)view.getLayoutParams();
          if (!layoutParams.isHelper && !layoutParams.isGuideline && view.getVisibility() != 8 && (!bool || !constraintWidget.getResolutionWidth().isResolved() || !constraintWidget.getResolutionHeight().isResolved())) {
            if (layoutParams.width == -2 && layoutParams.horizontalDimensionFixed) {
              i1 = getChildMeasureSpec(paramInt1, m, layoutParams.width);
            } else {
              i1 = View.MeasureSpec.makeMeasureSpec(constraintWidget.getWidth(), 1073741824);
            } 
            if (layoutParams.height == -2 && layoutParams.verticalDimensionFixed) {
              i6 = getChildMeasureSpec(paramInt2, i5, layoutParams.height);
            } else {
              i6 = View.MeasureSpec.makeMeasureSpec(constraintWidget.getHeight(), 1073741824);
            } 
            view.measure(i1, i6);
            if (this.mMetrics != null) {
              Metrics metrics = this.mMetrics;
              metrics.additionalMeasures++;
            } 
            int i7 = view.getMeasuredWidth();
            int i6 = view.getMeasuredHeight();
            i1 = n;
            n = k;
            if (i7 != constraintWidget.getWidth()) {
              constraintWidget.setWidth(i7);
              if (bool)
                constraintWidget.getResolutionWidth().resolve(i7); 
              n = k;
              if (bool1) {
                n = k;
                if (constraintWidget.getRight() > k)
                  n = Math.max(k, constraintWidget.getRight() + constraintWidget.getAnchor(ConstraintAnchor.Type.RIGHT).getMargin()); 
              } 
              i1 = 1;
            } 
            if (i6 != constraintWidget.getHeight()) {
              constraintWidget.setHeight(i6);
              if (bool)
                constraintWidget.getResolutionHeight().resolve(i6); 
              if (bool2) {
                i1 = constraintWidget.getBottom();
                k = i;
                if (i1 > k)
                  i = Math.max(k, constraintWidget.getBottom() + constraintWidget.getAnchor(ConstraintAnchor.Type.BOTTOM).getMargin()); 
              } 
              i1 = 1;
            } 
            if (layoutParams.needsBaseline) {
              i6 = view.getBaseline();
              k = i1;
              if (i6 != -1) {
                k = i1;
                if (i6 != constraintWidget.getBaselineDistance()) {
                  constraintWidget.setBaselineDistance(i6);
                  k = 1;
                } 
              } 
            } else {
              k = i1;
            } 
            if (Build.VERSION.SDK_INT >= 11)
              j = combineMeasuredStates(j, view.getMeasuredState()); 
            i1 = n;
            continue;
          } 
        } 
        i1 = k;
        k = n;
        continue;
        b++;
        n = k;
        k = i1;
      } 
      i1 = j;
      if (n != 0) {
        this.mLayoutWidget.setWidth(i3);
        this.mLayoutWidget.setHeight(i4);
        if (bool)
          this.mLayoutWidget.solveGraph(); 
        solveLinearSystem("2nd pass");
        if (this.mLayoutWidget.getWidth() < k) {
          this.mLayoutWidget.setWidth(k);
          j = 1;
        } else {
          j = 0;
        } 
        if (this.mLayoutWidget.getHeight() < i) {
          this.mLayoutWidget.setHeight(i);
          j = 1;
        } 
        if (j != 0)
          solveLinearSystem("3rd pass"); 
      } 
      k = 0;
      while (true) {
        i = i1;
        j = m;
        if (k < i2) {
          ConstraintWidget constraintWidget = (ConstraintWidget)this.mVariableDimensionsWidgets.get(k);
          View view = (View)constraintWidget.getCompanionWidget();
          if (view != null && (view.getMeasuredWidth() != constraintWidget.getWidth() || view.getMeasuredHeight() != constraintWidget.getHeight())) {
            view.measure(View.MeasureSpec.makeMeasureSpec(constraintWidget.getWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(constraintWidget.getHeight(), 1073741824));
            if (this.mMetrics != null) {
              Metrics metrics = this.mMetrics;
              metrics.additionalMeasures++;
            } 
          } 
          k++;
          continue;
        } 
        break;
      } 
    } else {
      j = m;
      i = 0;
    } 
    j = this.mLayoutWidget.getWidth() + j;
    k = this.mLayoutWidget.getHeight() + i5;
    if (Build.VERSION.SDK_INT >= 11) {
      paramInt1 = resolveSizeAndState(j, paramInt1, i);
      i = resolveSizeAndState(k, paramInt2, i << 16);
      paramInt2 = Math.min(this.mMaxWidth, paramInt1 & 0xFFFFFF);
      i = Math.min(this.mMaxHeight, i & 0xFFFFFF);
      paramInt1 = paramInt2;
      if (this.mLayoutWidget.isWidthMeasuredTooSmall())
        paramInt1 = paramInt2 | 0x1000000; 
      paramInt2 = i;
      if (this.mLayoutWidget.isHeightMeasuredTooSmall())
        paramInt2 = i | 0x1000000; 
      setMeasuredDimension(paramInt1, paramInt2);
      this.mLastMeasureWidth = paramInt1;
      this.mLastMeasureHeight = paramInt2;
      return;
    } 
    setMeasuredDimension(j, k);
    this.mLastMeasureWidth = j;
    this.mLastMeasureHeight = k;
  }
  
  public void onViewAdded(View paramView) {
    if (Build.VERSION.SDK_INT >= 14)
      super.onViewAdded(paramView); 
    ConstraintWidget constraintWidget = getViewWidget(paramView);
    if (paramView instanceof Guideline && !(constraintWidget instanceof Guideline)) {
      LayoutParams layoutParams = (LayoutParams)paramView.getLayoutParams();
      layoutParams.widget = new Guideline();
      layoutParams.isGuideline = true;
      ((Guideline)layoutParams.widget).setOrientation(layoutParams.orientation);
    } 
    if (paramView instanceof ConstraintHelper) {
      ConstraintHelper constraintHelper = (ConstraintHelper)paramView;
      constraintHelper.validateParams();
      ((LayoutParams)paramView.getLayoutParams()).isHelper = true;
      if (!this.mConstraintHelpers.contains(constraintHelper))
        this.mConstraintHelpers.add(constraintHelper); 
    } 
    this.mChildrenByIds.put(paramView.getId(), paramView);
    this.mDirtyHierarchy = true;
  }
  
  public void onViewRemoved(View paramView) {
    if (Build.VERSION.SDK_INT >= 14)
      super.onViewRemoved(paramView); 
    this.mChildrenByIds.remove(paramView.getId());
    ConstraintWidget constraintWidget = getViewWidget(paramView);
    this.mLayoutWidget.remove(constraintWidget);
    this.mConstraintHelpers.remove(paramView);
    this.mVariableDimensionsWidgets.remove(constraintWidget);
    this.mDirtyHierarchy = true;
  }
  
  public void removeView(View paramView) {
    super.removeView(paramView);
    if (Build.VERSION.SDK_INT < 14)
      onViewRemoved(paramView); 
  }
  
  public void requestLayout() {
    super.requestLayout();
    this.mDirtyHierarchy = true;
    this.mLastMeasureWidth = -1;
    this.mLastMeasureHeight = -1;
    this.mLastMeasureWidthSize = -1;
    this.mLastMeasureHeightSize = -1;
    this.mLastMeasureWidthMode = 0;
    this.mLastMeasureHeightMode = 0;
  }
  
  public void setConstraintSet(ConstraintSet paramConstraintSet) { this.mConstraintSet = paramConstraintSet; }
  
  public void setDesignInformation(int paramInt, Object paramObject1, Object paramObject2) {
    if (paramInt == 0 && paramObject1 instanceof String && paramObject2 instanceof Integer) {
      if (this.mDesignIds == null)
        this.mDesignIds = new HashMap(); 
      String str = (String)paramObject1;
      paramInt = str.indexOf("/");
      paramObject1 = str;
      if (paramInt != -1)
        paramObject1 = str.substring(paramInt + 1); 
      paramInt = ((Integer)paramObject2).intValue();
      this.mDesignIds.put(paramObject1, Integer.valueOf(paramInt));
    } 
  }
  
  public void setId(int paramInt) {
    this.mChildrenByIds.remove(getId());
    super.setId(paramInt);
    this.mChildrenByIds.put(getId(), this);
  }
  
  public void setMaxHeight(int paramInt) {
    if (paramInt == this.mMaxHeight)
      return; 
    this.mMaxHeight = paramInt;
    requestLayout();
  }
  
  public void setMaxWidth(int paramInt) {
    if (paramInt == this.mMaxWidth)
      return; 
    this.mMaxWidth = paramInt;
    requestLayout();
  }
  
  public void setMinHeight(int paramInt) {
    if (paramInt == this.mMinHeight)
      return; 
    this.mMinHeight = paramInt;
    requestLayout();
  }
  
  public void setMinWidth(int paramInt) {
    if (paramInt == this.mMinWidth)
      return; 
    this.mMinWidth = paramInt;
    requestLayout();
  }
  
  public void setOptimizationLevel(int paramInt) { this.mLayoutWidget.setOptimizationLevel(paramInt); }
  
  public boolean shouldDelayChildPressedState() { return false; }
  
  protected void solveLinearSystem(String paramString) {
    this.mLayoutWidget.layout();
    if (this.mMetrics != null) {
      Metrics metrics = this.mMetrics;
      metrics.resolutions++;
    } 
  }
  
  public static class LayoutParams extends ViewGroup.MarginLayoutParams {
    public static final int BASELINE = 5;
    
    public static final int BOTTOM = 4;
    
    public static final int CHAIN_PACKED = 2;
    
    public static final int CHAIN_SPREAD = 0;
    
    public static final int CHAIN_SPREAD_INSIDE = 1;
    
    public static final int END = 7;
    
    public static final int HORIZONTAL = 0;
    
    public static final int LEFT = 1;
    
    public static final int MATCH_CONSTRAINT = 0;
    
    public static final int MATCH_CONSTRAINT_PERCENT = 2;
    
    public static final int MATCH_CONSTRAINT_SPREAD = 0;
    
    public static final int MATCH_CONSTRAINT_WRAP = 1;
    
    public static final int PARENT_ID = 0;
    
    public static final int RIGHT = 2;
    
    public static final int START = 6;
    
    public static final int TOP = 3;
    
    public static final int UNSET = -1;
    
    public static final int VERTICAL = 1;
    
    public int baselineToBaseline = -1;
    
    public int bottomToBottom = -1;
    
    public int bottomToTop = -1;
    
    public float circleAngle = 0.0F;
    
    public int circleConstraint = -1;
    
    public int circleRadius = 0;
    
    public boolean constrainedHeight = false;
    
    public boolean constrainedWidth = false;
    
    public String dimensionRatio = null;
    
    int dimensionRatioSide = 1;
    
    float dimensionRatioValue = 0.0F;
    
    public int editorAbsoluteX = -1;
    
    public int editorAbsoluteY = -1;
    
    public int endToEnd = -1;
    
    public int endToStart = -1;
    
    public int goneBottomMargin = -1;
    
    public int goneEndMargin = -1;
    
    public int goneLeftMargin = -1;
    
    public int goneRightMargin = -1;
    
    public int goneStartMargin = -1;
    
    public int goneTopMargin = -1;
    
    public int guideBegin = -1;
    
    public int guideEnd = -1;
    
    public float guidePercent = -1.0F;
    
    public boolean helped = false;
    
    public float horizontalBias = 0.5F;
    
    public int horizontalChainStyle = 0;
    
    boolean horizontalDimensionFixed = true;
    
    public float horizontalWeight = 0.0F;
    
    boolean isGuideline = false;
    
    boolean isHelper = false;
    
    boolean isInPlaceholder = false;
    
    public int leftToLeft = -1;
    
    public int leftToRight = -1;
    
    public int matchConstraintDefaultHeight = 0;
    
    public int matchConstraintDefaultWidth = 0;
    
    public int matchConstraintMaxHeight = 0;
    
    public int matchConstraintMaxWidth = 0;
    
    public int matchConstraintMinHeight = 0;
    
    public int matchConstraintMinWidth = 0;
    
    public float matchConstraintPercentHeight = 1.0F;
    
    public float matchConstraintPercentWidth = 1.0F;
    
    boolean needsBaseline = false;
    
    public int orientation = -1;
    
    int resolveGoneLeftMargin = -1;
    
    int resolveGoneRightMargin = -1;
    
    int resolvedGuideBegin;
    
    int resolvedGuideEnd;
    
    float resolvedGuidePercent;
    
    float resolvedHorizontalBias = 0.5F;
    
    int resolvedLeftToLeft = -1;
    
    int resolvedLeftToRight = -1;
    
    int resolvedRightToLeft = -1;
    
    int resolvedRightToRight = -1;
    
    public int rightToLeft = -1;
    
    public int rightToRight = -1;
    
    public int startToEnd = -1;
    
    public int startToStart = -1;
    
    public int topToBottom = -1;
    
    public int topToTop = -1;
    
    public float verticalBias = 0.5F;
    
    public int verticalChainStyle = 0;
    
    boolean verticalDimensionFixed = true;
    
    public float verticalWeight = 0.0F;
    
    ConstraintWidget widget = new ConstraintWidget();
    
    public LayoutParams(int param1Int1, int param1Int2) { super(param1Int1, param1Int2); }
    
    public LayoutParams(Context param1Context, AttributeSet param1AttributeSet) { // Byte code:
      //   0: aload_0
      //   1: aload_1
      //   2: aload_2
      //   3: invokespecial <init> : (Landroid/content/Context;Landroid/util/AttributeSet;)V
      //   6: aload_0
      //   7: iconst_m1
      //   8: putfield guideBegin : I
      //   11: aload_0
      //   12: iconst_m1
      //   13: putfield guideEnd : I
      //   16: aload_0
      //   17: ldc -1.0
      //   19: putfield guidePercent : F
      //   22: aload_0
      //   23: iconst_m1
      //   24: putfield leftToLeft : I
      //   27: aload_0
      //   28: iconst_m1
      //   29: putfield leftToRight : I
      //   32: aload_0
      //   33: iconst_m1
      //   34: putfield rightToLeft : I
      //   37: aload_0
      //   38: iconst_m1
      //   39: putfield rightToRight : I
      //   42: aload_0
      //   43: iconst_m1
      //   44: putfield topToTop : I
      //   47: aload_0
      //   48: iconst_m1
      //   49: putfield topToBottom : I
      //   52: aload_0
      //   53: iconst_m1
      //   54: putfield bottomToTop : I
      //   57: aload_0
      //   58: iconst_m1
      //   59: putfield bottomToBottom : I
      //   62: aload_0
      //   63: iconst_m1
      //   64: putfield baselineToBaseline : I
      //   67: aload_0
      //   68: iconst_m1
      //   69: putfield circleConstraint : I
      //   72: aload_0
      //   73: iconst_0
      //   74: putfield circleRadius : I
      //   77: aload_0
      //   78: fconst_0
      //   79: putfield circleAngle : F
      //   82: aload_0
      //   83: iconst_m1
      //   84: putfield startToEnd : I
      //   87: aload_0
      //   88: iconst_m1
      //   89: putfield startToStart : I
      //   92: aload_0
      //   93: iconst_m1
      //   94: putfield endToStart : I
      //   97: aload_0
      //   98: iconst_m1
      //   99: putfield endToEnd : I
      //   102: aload_0
      //   103: iconst_m1
      //   104: putfield goneLeftMargin : I
      //   107: aload_0
      //   108: iconst_m1
      //   109: putfield goneTopMargin : I
      //   112: aload_0
      //   113: iconst_m1
      //   114: putfield goneRightMargin : I
      //   117: aload_0
      //   118: iconst_m1
      //   119: putfield goneBottomMargin : I
      //   122: aload_0
      //   123: iconst_m1
      //   124: putfield goneStartMargin : I
      //   127: aload_0
      //   128: iconst_m1
      //   129: putfield goneEndMargin : I
      //   132: aload_0
      //   133: ldc 0.5
      //   135: putfield horizontalBias : F
      //   138: aload_0
      //   139: ldc 0.5
      //   141: putfield verticalBias : F
      //   144: aload_0
      //   145: aconst_null
      //   146: putfield dimensionRatio : Ljava/lang/String;
      //   149: aload_0
      //   150: fconst_0
      //   151: putfield dimensionRatioValue : F
      //   154: aload_0
      //   155: iconst_1
      //   156: putfield dimensionRatioSide : I
      //   159: aload_0
      //   160: fconst_0
      //   161: putfield horizontalWeight : F
      //   164: aload_0
      //   165: fconst_0
      //   166: putfield verticalWeight : F
      //   169: aload_0
      //   170: iconst_0
      //   171: putfield horizontalChainStyle : I
      //   174: aload_0
      //   175: iconst_0
      //   176: putfield verticalChainStyle : I
      //   179: aload_0
      //   180: iconst_0
      //   181: putfield matchConstraintDefaultWidth : I
      //   184: aload_0
      //   185: iconst_0
      //   186: putfield matchConstraintDefaultHeight : I
      //   189: aload_0
      //   190: iconst_0
      //   191: putfield matchConstraintMinWidth : I
      //   194: aload_0
      //   195: iconst_0
      //   196: putfield matchConstraintMinHeight : I
      //   199: aload_0
      //   200: iconst_0
      //   201: putfield matchConstraintMaxWidth : I
      //   204: aload_0
      //   205: iconst_0
      //   206: putfield matchConstraintMaxHeight : I
      //   209: aload_0
      //   210: fconst_1
      //   211: putfield matchConstraintPercentWidth : F
      //   214: aload_0
      //   215: fconst_1
      //   216: putfield matchConstraintPercentHeight : F
      //   219: aload_0
      //   220: iconst_m1
      //   221: putfield editorAbsoluteX : I
      //   224: aload_0
      //   225: iconst_m1
      //   226: putfield editorAbsoluteY : I
      //   229: aload_0
      //   230: iconst_m1
      //   231: putfield orientation : I
      //   234: aload_0
      //   235: iconst_0
      //   236: putfield constrainedWidth : Z
      //   239: aload_0
      //   240: iconst_0
      //   241: putfield constrainedHeight : Z
      //   244: aload_0
      //   245: iconst_1
      //   246: putfield horizontalDimensionFixed : Z
      //   249: aload_0
      //   250: iconst_1
      //   251: putfield verticalDimensionFixed : Z
      //   254: aload_0
      //   255: iconst_0
      //   256: putfield needsBaseline : Z
      //   259: aload_0
      //   260: iconst_0
      //   261: putfield isGuideline : Z
      //   264: aload_0
      //   265: iconst_0
      //   266: putfield isHelper : Z
      //   269: aload_0
      //   270: iconst_0
      //   271: putfield isInPlaceholder : Z
      //   274: aload_0
      //   275: iconst_m1
      //   276: putfield resolvedLeftToLeft : I
      //   279: aload_0
      //   280: iconst_m1
      //   281: putfield resolvedLeftToRight : I
      //   284: aload_0
      //   285: iconst_m1
      //   286: putfield resolvedRightToLeft : I
      //   289: aload_0
      //   290: iconst_m1
      //   291: putfield resolvedRightToRight : I
      //   294: aload_0
      //   295: iconst_m1
      //   296: putfield resolveGoneLeftMargin : I
      //   299: aload_0
      //   300: iconst_m1
      //   301: putfield resolveGoneRightMargin : I
      //   304: aload_0
      //   305: ldc 0.5
      //   307: putfield resolvedHorizontalBias : F
      //   310: aload_0
      //   311: new android/support/constraint/solver/widgets/ConstraintWidget
      //   314: dup
      //   315: invokespecial <init> : ()V
      //   318: putfield widget : Landroid/support/constraint/solver/widgets/ConstraintWidget;
      //   321: aload_0
      //   322: iconst_0
      //   323: putfield helped : Z
      //   326: aload_1
      //   327: aload_2
      //   328: getstatic android/support/constraint/R$styleable.ConstraintLayout_Layout : [I
      //   331: invokevirtual obtainStyledAttributes : (Landroid/util/AttributeSet;[I)Landroid/content/res/TypedArray;
      //   334: astore_1
      //   335: aload_1
      //   336: invokevirtual getIndexCount : ()I
      //   339: istore #7
      //   341: iconst_0
      //   342: istore #5
      //   344: iload #5
      //   346: iload #7
      //   348: if_icmpge -> 2031
      //   351: aload_1
      //   352: iload #5
      //   354: invokevirtual getIndex : (I)I
      //   357: istore #6
      //   359: getstatic android/support/constraint/ConstraintLayout$LayoutParams$Table.map : Landroid/util/SparseIntArray;
      //   362: iload #6
      //   364: invokevirtual get : (I)I
      //   367: tableswitch default -> 584, 0 -> 2022, 1 -> 2008, 2 -> 1972, 3 -> 1955, 4 -> 1909, 5 -> 1892, 6 -> 1875, 7 -> 1858, 8 -> 1822, 9 -> 1786, 10 -> 1750, 11 -> 1714, 12 -> 1678, 13 -> 1642, 14 -> 1606, 15 -> 1570, 16 -> 1534, 17 -> 1498, 18 -> 1462, 19 -> 1426, 20 -> 1390, 21 -> 1373, 22 -> 1356, 23 -> 1339, 24 -> 1322, 25 -> 1305, 26 -> 1288, 27 -> 1271, 28 -> 1254, 29 -> 1237, 30 -> 1220, 31 -> 1188, 32 -> 1156, 33 -> 1115, 34 -> 1074, 35 -> 1053, 36 -> 1012, 37 -> 971, 38 -> 950, 39 -> 2022, 40 -> 2022, 41 -> 2022, 42 -> 2022, 43 -> 584, 44 -> 677, 45 -> 663, 46 -> 649, 47 -> 635, 48 -> 621, 49 -> 604, 50 -> 587
      //   584: goto -> 2022
      //   587: aload_0
      //   588: aload_1
      //   589: iload #6
      //   591: aload_0
      //   592: getfield editorAbsoluteY : I
      //   595: invokevirtual getDimensionPixelOffset : (II)I
      //   598: putfield editorAbsoluteY : I
      //   601: goto -> 2022
      //   604: aload_0
      //   605: aload_1
      //   606: iload #6
      //   608: aload_0
      //   609: getfield editorAbsoluteX : I
      //   612: invokevirtual getDimensionPixelOffset : (II)I
      //   615: putfield editorAbsoluteX : I
      //   618: goto -> 2022
      //   621: aload_0
      //   622: aload_1
      //   623: iload #6
      //   625: iconst_0
      //   626: invokevirtual getInt : (II)I
      //   629: putfield verticalChainStyle : I
      //   632: goto -> 2022
      //   635: aload_0
      //   636: aload_1
      //   637: iload #6
      //   639: iconst_0
      //   640: invokevirtual getInt : (II)I
      //   643: putfield horizontalChainStyle : I
      //   646: goto -> 2022
      //   649: aload_0
      //   650: aload_1
      //   651: iload #6
      //   653: fconst_0
      //   654: invokevirtual getFloat : (IF)F
      //   657: putfield verticalWeight : F
      //   660: goto -> 2022
      //   663: aload_0
      //   664: aload_1
      //   665: iload #6
      //   667: fconst_0
      //   668: invokevirtual getFloat : (IF)F
      //   671: putfield horizontalWeight : F
      //   674: goto -> 2022
      //   677: aload_0
      //   678: aload_1
      //   679: iload #6
      //   681: invokevirtual getString : (I)Ljava/lang/String;
      //   684: putfield dimensionRatio : Ljava/lang/String;
      //   687: aload_0
      //   688: ldc_w NaN
      //   691: putfield dimensionRatioValue : F
      //   694: aload_0
      //   695: iconst_m1
      //   696: putfield dimensionRatioSide : I
      //   699: aload_0
      //   700: getfield dimensionRatio : Ljava/lang/String;
      //   703: ifnull -> 2022
      //   706: aload_0
      //   707: getfield dimensionRatio : Ljava/lang/String;
      //   710: invokevirtual length : ()I
      //   713: istore #8
      //   715: aload_0
      //   716: getfield dimensionRatio : Ljava/lang/String;
      //   719: bipush #44
      //   721: invokevirtual indexOf : (I)I
      //   724: istore #6
      //   726: iload #6
      //   728: ifle -> 793
      //   731: iload #6
      //   733: iload #8
      //   735: iconst_1
      //   736: isub
      //   737: if_icmpge -> 793
      //   740: aload_0
      //   741: getfield dimensionRatio : Ljava/lang/String;
      //   744: iconst_0
      //   745: iload #6
      //   747: invokevirtual substring : (II)Ljava/lang/String;
      //   750: astore_2
      //   751: aload_2
      //   752: ldc_w 'W'
      //   755: invokevirtual equalsIgnoreCase : (Ljava/lang/String;)Z
      //   758: ifeq -> 769
      //   761: aload_0
      //   762: iconst_0
      //   763: putfield dimensionRatioSide : I
      //   766: goto -> 784
      //   769: aload_2
      //   770: ldc_w 'H'
      //   773: invokevirtual equalsIgnoreCase : (Ljava/lang/String;)Z
      //   776: ifeq -> 784
      //   779: aload_0
      //   780: iconst_1
      //   781: putfield dimensionRatioSide : I
      //   784: iload #6
      //   786: iconst_1
      //   787: iadd
      //   788: istore #6
      //   790: goto -> 796
      //   793: iconst_0
      //   794: istore #6
      //   796: aload_0
      //   797: getfield dimensionRatio : Ljava/lang/String;
      //   800: bipush #58
      //   802: invokevirtual indexOf : (I)I
      //   805: istore #9
      //   807: iload #9
      //   809: iflt -> 922
      //   812: iload #9
      //   814: iload #8
      //   816: iconst_1
      //   817: isub
      //   818: if_icmpge -> 922
      //   821: aload_0
      //   822: getfield dimensionRatio : Ljava/lang/String;
      //   825: iload #6
      //   827: iload #9
      //   829: invokevirtual substring : (II)Ljava/lang/String;
      //   832: astore_2
      //   833: aload_0
      //   834: getfield dimensionRatio : Ljava/lang/String;
      //   837: iload #9
      //   839: iconst_1
      //   840: iadd
      //   841: invokevirtual substring : (I)Ljava/lang/String;
      //   844: astore #10
      //   846: aload_2
      //   847: invokevirtual length : ()I
      //   850: ifle -> 2022
      //   853: aload #10
      //   855: invokevirtual length : ()I
      //   858: ifle -> 2022
      //   861: aload_2
      //   862: invokestatic parseFloat : (Ljava/lang/String;)F
      //   865: fstore_3
      //   866: aload #10
      //   868: invokestatic parseFloat : (Ljava/lang/String;)F
      //   871: fstore #4
      //   873: fload_3
      //   874: fconst_0
      //   875: fcmpl
      //   876: ifle -> 2022
      //   879: fload #4
      //   881: fconst_0
      //   882: fcmpl
      //   883: ifle -> 2022
      //   886: aload_0
      //   887: getfield dimensionRatioSide : I
      //   890: iconst_1
      //   891: if_icmpne -> 908
      //   894: aload_0
      //   895: fload #4
      //   897: fload_3
      //   898: fdiv
      //   899: invokestatic abs : (F)F
      //   902: putfield dimensionRatioValue : F
      //   905: goto -> 2022
      //   908: aload_0
      //   909: fload_3
      //   910: fload #4
      //   912: fdiv
      //   913: invokestatic abs : (F)F
      //   916: putfield dimensionRatioValue : F
      //   919: goto -> 2022
      //   922: aload_0
      //   923: getfield dimensionRatio : Ljava/lang/String;
      //   926: iload #6
      //   928: invokevirtual substring : (I)Ljava/lang/String;
      //   931: astore_2
      //   932: aload_2
      //   933: invokevirtual length : ()I
      //   936: ifle -> 2022
      //   939: aload_0
      //   940: aload_2
      //   941: invokestatic parseFloat : (Ljava/lang/String;)F
      //   944: putfield dimensionRatioValue : F
      //   947: goto -> 2022
      //   950: aload_0
      //   951: fconst_0
      //   952: aload_1
      //   953: iload #6
      //   955: aload_0
      //   956: getfield matchConstraintPercentHeight : F
      //   959: invokevirtual getFloat : (IF)F
      //   962: invokestatic max : (FF)F
      //   965: putfield matchConstraintPercentHeight : F
      //   968: goto -> 2022
      //   971: aload_0
      //   972: aload_1
      //   973: iload #6
      //   975: aload_0
      //   976: getfield matchConstraintMaxHeight : I
      //   979: invokevirtual getDimensionPixelSize : (II)I
      //   982: putfield matchConstraintMaxHeight : I
      //   985: goto -> 2022
      //   988: aload_1
      //   989: iload #6
      //   991: aload_0
      //   992: getfield matchConstraintMaxHeight : I
      //   995: invokevirtual getInt : (II)I
      //   998: bipush #-2
      //   1000: if_icmpne -> 2022
      //   1003: aload_0
      //   1004: bipush #-2
      //   1006: putfield matchConstraintMaxHeight : I
      //   1009: goto -> 2022
      //   1012: aload_0
      //   1013: aload_1
      //   1014: iload #6
      //   1016: aload_0
      //   1017: getfield matchConstraintMinHeight : I
      //   1020: invokevirtual getDimensionPixelSize : (II)I
      //   1023: putfield matchConstraintMinHeight : I
      //   1026: goto -> 2022
      //   1029: aload_1
      //   1030: iload #6
      //   1032: aload_0
      //   1033: getfield matchConstraintMinHeight : I
      //   1036: invokevirtual getInt : (II)I
      //   1039: bipush #-2
      //   1041: if_icmpne -> 2022
      //   1044: aload_0
      //   1045: bipush #-2
      //   1047: putfield matchConstraintMinHeight : I
      //   1050: goto -> 2022
      //   1053: aload_0
      //   1054: fconst_0
      //   1055: aload_1
      //   1056: iload #6
      //   1058: aload_0
      //   1059: getfield matchConstraintPercentWidth : F
      //   1062: invokevirtual getFloat : (IF)F
      //   1065: invokestatic max : (FF)F
      //   1068: putfield matchConstraintPercentWidth : F
      //   1071: goto -> 2022
      //   1074: aload_0
      //   1075: aload_1
      //   1076: iload #6
      //   1078: aload_0
      //   1079: getfield matchConstraintMaxWidth : I
      //   1082: invokevirtual getDimensionPixelSize : (II)I
      //   1085: putfield matchConstraintMaxWidth : I
      //   1088: goto -> 2022
      //   1091: aload_1
      //   1092: iload #6
      //   1094: aload_0
      //   1095: getfield matchConstraintMaxWidth : I
      //   1098: invokevirtual getInt : (II)I
      //   1101: bipush #-2
      //   1103: if_icmpne -> 2022
      //   1106: aload_0
      //   1107: bipush #-2
      //   1109: putfield matchConstraintMaxWidth : I
      //   1112: goto -> 2022
      //   1115: aload_0
      //   1116: aload_1
      //   1117: iload #6
      //   1119: aload_0
      //   1120: getfield matchConstraintMinWidth : I
      //   1123: invokevirtual getDimensionPixelSize : (II)I
      //   1126: putfield matchConstraintMinWidth : I
      //   1129: goto -> 2022
      //   1132: aload_1
      //   1133: iload #6
      //   1135: aload_0
      //   1136: getfield matchConstraintMinWidth : I
      //   1139: invokevirtual getInt : (II)I
      //   1142: bipush #-2
      //   1144: if_icmpne -> 2022
      //   1147: aload_0
      //   1148: bipush #-2
      //   1150: putfield matchConstraintMinWidth : I
      //   1153: goto -> 2022
      //   1156: aload_0
      //   1157: aload_1
      //   1158: iload #6
      //   1160: iconst_0
      //   1161: invokevirtual getInt : (II)I
      //   1164: putfield matchConstraintDefaultHeight : I
      //   1167: aload_0
      //   1168: getfield matchConstraintDefaultHeight : I
      //   1171: iconst_1
      //   1172: if_icmpne -> 2022
      //   1175: ldc_w 'ConstraintLayout'
      //   1178: ldc_w 'layout_constraintHeight_default="wrap" is deprecated.\\nUse layout_height="WRAP_CONTENT" and layout_constrainedHeight="true" instead.'
      //   1181: invokestatic e : (Ljava/lang/String;Ljava/lang/String;)I
      //   1184: pop
      //   1185: goto -> 2022
      //   1188: aload_0
      //   1189: aload_1
      //   1190: iload #6
      //   1192: iconst_0
      //   1193: invokevirtual getInt : (II)I
      //   1196: putfield matchConstraintDefaultWidth : I
      //   1199: aload_0
      //   1200: getfield matchConstraintDefaultWidth : I
      //   1203: iconst_1
      //   1204: if_icmpne -> 2022
      //   1207: ldc_w 'ConstraintLayout'
      //   1210: ldc_w 'layout_constraintWidth_default="wrap" is deprecated.\\nUse layout_width="WRAP_CONTENT" and layout_constrainedWidth="true" instead.'
      //   1213: invokestatic e : (Ljava/lang/String;Ljava/lang/String;)I
      //   1216: pop
      //   1217: goto -> 2022
      //   1220: aload_0
      //   1221: aload_1
      //   1222: iload #6
      //   1224: aload_0
      //   1225: getfield verticalBias : F
      //   1228: invokevirtual getFloat : (IF)F
      //   1231: putfield verticalBias : F
      //   1234: goto -> 2022
      //   1237: aload_0
      //   1238: aload_1
      //   1239: iload #6
      //   1241: aload_0
      //   1242: getfield horizontalBias : F
      //   1245: invokevirtual getFloat : (IF)F
      //   1248: putfield horizontalBias : F
      //   1251: goto -> 2022
      //   1254: aload_0
      //   1255: aload_1
      //   1256: iload #6
      //   1258: aload_0
      //   1259: getfield constrainedHeight : Z
      //   1262: invokevirtual getBoolean : (IZ)Z
      //   1265: putfield constrainedHeight : Z
      //   1268: goto -> 2022
      //   1271: aload_0
      //   1272: aload_1
      //   1273: iload #6
      //   1275: aload_0
      //   1276: getfield constrainedWidth : Z
      //   1279: invokevirtual getBoolean : (IZ)Z
      //   1282: putfield constrainedWidth : Z
      //   1285: goto -> 2022
      //   1288: aload_0
      //   1289: aload_1
      //   1290: iload #6
      //   1292: aload_0
      //   1293: getfield goneEndMargin : I
      //   1296: invokevirtual getDimensionPixelSize : (II)I
      //   1299: putfield goneEndMargin : I
      //   1302: goto -> 2022
      //   1305: aload_0
      //   1306: aload_1
      //   1307: iload #6
      //   1309: aload_0
      //   1310: getfield goneStartMargin : I
      //   1313: invokevirtual getDimensionPixelSize : (II)I
      //   1316: putfield goneStartMargin : I
      //   1319: goto -> 2022
      //   1322: aload_0
      //   1323: aload_1
      //   1324: iload #6
      //   1326: aload_0
      //   1327: getfield goneBottomMargin : I
      //   1330: invokevirtual getDimensionPixelSize : (II)I
      //   1333: putfield goneBottomMargin : I
      //   1336: goto -> 2022
      //   1339: aload_0
      //   1340: aload_1
      //   1341: iload #6
      //   1343: aload_0
      //   1344: getfield goneRightMargin : I
      //   1347: invokevirtual getDimensionPixelSize : (II)I
      //   1350: putfield goneRightMargin : I
      //   1353: goto -> 2022
      //   1356: aload_0
      //   1357: aload_1
      //   1358: iload #6
      //   1360: aload_0
      //   1361: getfield goneTopMargin : I
      //   1364: invokevirtual getDimensionPixelSize : (II)I
      //   1367: putfield goneTopMargin : I
      //   1370: goto -> 2022
      //   1373: aload_0
      //   1374: aload_1
      //   1375: iload #6
      //   1377: aload_0
      //   1378: getfield goneLeftMargin : I
      //   1381: invokevirtual getDimensionPixelSize : (II)I
      //   1384: putfield goneLeftMargin : I
      //   1387: goto -> 2022
      //   1390: aload_0
      //   1391: aload_1
      //   1392: iload #6
      //   1394: aload_0
      //   1395: getfield endToEnd : I
      //   1398: invokevirtual getResourceId : (II)I
      //   1401: putfield endToEnd : I
      //   1404: aload_0
      //   1405: getfield endToEnd : I
      //   1408: iconst_m1
      //   1409: if_icmpne -> 2022
      //   1412: aload_0
      //   1413: aload_1
      //   1414: iload #6
      //   1416: iconst_m1
      //   1417: invokevirtual getInt : (II)I
      //   1420: putfield endToEnd : I
      //   1423: goto -> 2022
      //   1426: aload_0
      //   1427: aload_1
      //   1428: iload #6
      //   1430: aload_0
      //   1431: getfield endToStart : I
      //   1434: invokevirtual getResourceId : (II)I
      //   1437: putfield endToStart : I
      //   1440: aload_0
      //   1441: getfield endToStart : I
      //   1444: iconst_m1
      //   1445: if_icmpne -> 2022
      //   1448: aload_0
      //   1449: aload_1
      //   1450: iload #6
      //   1452: iconst_m1
      //   1453: invokevirtual getInt : (II)I
      //   1456: putfield endToStart : I
      //   1459: goto -> 2022
      //   1462: aload_0
      //   1463: aload_1
      //   1464: iload #6
      //   1466: aload_0
      //   1467: getfield startToStart : I
      //   1470: invokevirtual getResourceId : (II)I
      //   1473: putfield startToStart : I
      //   1476: aload_0
      //   1477: getfield startToStart : I
      //   1480: iconst_m1
      //   1481: if_icmpne -> 2022
      //   1484: aload_0
      //   1485: aload_1
      //   1486: iload #6
      //   1488: iconst_m1
      //   1489: invokevirtual getInt : (II)I
      //   1492: putfield startToStart : I
      //   1495: goto -> 2022
      //   1498: aload_0
      //   1499: aload_1
      //   1500: iload #6
      //   1502: aload_0
      //   1503: getfield startToEnd : I
      //   1506: invokevirtual getResourceId : (II)I
      //   1509: putfield startToEnd : I
      //   1512: aload_0
      //   1513: getfield startToEnd : I
      //   1516: iconst_m1
      //   1517: if_icmpne -> 2022
      //   1520: aload_0
      //   1521: aload_1
      //   1522: iload #6
      //   1524: iconst_m1
      //   1525: invokevirtual getInt : (II)I
      //   1528: putfield startToEnd : I
      //   1531: goto -> 2022
      //   1534: aload_0
      //   1535: aload_1
      //   1536: iload #6
      //   1538: aload_0
      //   1539: getfield baselineToBaseline : I
      //   1542: invokevirtual getResourceId : (II)I
      //   1545: putfield baselineToBaseline : I
      //   1548: aload_0
      //   1549: getfield baselineToBaseline : I
      //   1552: iconst_m1
      //   1553: if_icmpne -> 2022
      //   1556: aload_0
      //   1557: aload_1
      //   1558: iload #6
      //   1560: iconst_m1
      //   1561: invokevirtual getInt : (II)I
      //   1564: putfield baselineToBaseline : I
      //   1567: goto -> 2022
      //   1570: aload_0
      //   1571: aload_1
      //   1572: iload #6
      //   1574: aload_0
      //   1575: getfield bottomToBottom : I
      //   1578: invokevirtual getResourceId : (II)I
      //   1581: putfield bottomToBottom : I
      //   1584: aload_0
      //   1585: getfield bottomToBottom : I
      //   1588: iconst_m1
      //   1589: if_icmpne -> 2022
      //   1592: aload_0
      //   1593: aload_1
      //   1594: iload #6
      //   1596: iconst_m1
      //   1597: invokevirtual getInt : (II)I
      //   1600: putfield bottomToBottom : I
      //   1603: goto -> 2022
      //   1606: aload_0
      //   1607: aload_1
      //   1608: iload #6
      //   1610: aload_0
      //   1611: getfield bottomToTop : I
      //   1614: invokevirtual getResourceId : (II)I
      //   1617: putfield bottomToTop : I
      //   1620: aload_0
      //   1621: getfield bottomToTop : I
      //   1624: iconst_m1
      //   1625: if_icmpne -> 2022
      //   1628: aload_0
      //   1629: aload_1
      //   1630: iload #6
      //   1632: iconst_m1
      //   1633: invokevirtual getInt : (II)I
      //   1636: putfield bottomToTop : I
      //   1639: goto -> 2022
      //   1642: aload_0
      //   1643: aload_1
      //   1644: iload #6
      //   1646: aload_0
      //   1647: getfield topToBottom : I
      //   1650: invokevirtual getResourceId : (II)I
      //   1653: putfield topToBottom : I
      //   1656: aload_0
      //   1657: getfield topToBottom : I
      //   1660: iconst_m1
      //   1661: if_icmpne -> 2022
      //   1664: aload_0
      //   1665: aload_1
      //   1666: iload #6
      //   1668: iconst_m1
      //   1669: invokevirtual getInt : (II)I
      //   1672: putfield topToBottom : I
      //   1675: goto -> 2022
      //   1678: aload_0
      //   1679: aload_1
      //   1680: iload #6
      //   1682: aload_0
      //   1683: getfield topToTop : I
      //   1686: invokevirtual getResourceId : (II)I
      //   1689: putfield topToTop : I
      //   1692: aload_0
      //   1693: getfield topToTop : I
      //   1696: iconst_m1
      //   1697: if_icmpne -> 2022
      //   1700: aload_0
      //   1701: aload_1
      //   1702: iload #6
      //   1704: iconst_m1
      //   1705: invokevirtual getInt : (II)I
      //   1708: putfield topToTop : I
      //   1711: goto -> 2022
      //   1714: aload_0
      //   1715: aload_1
      //   1716: iload #6
      //   1718: aload_0
      //   1719: getfield rightToRight : I
      //   1722: invokevirtual getResourceId : (II)I
      //   1725: putfield rightToRight : I
      //   1728: aload_0
      //   1729: getfield rightToRight : I
      //   1732: iconst_m1
      //   1733: if_icmpne -> 2022
      //   1736: aload_0
      //   1737: aload_1
      //   1738: iload #6
      //   1740: iconst_m1
      //   1741: invokevirtual getInt : (II)I
      //   1744: putfield rightToRight : I
      //   1747: goto -> 2022
      //   1750: aload_0
      //   1751: aload_1
      //   1752: iload #6
      //   1754: aload_0
      //   1755: getfield rightToLeft : I
      //   1758: invokevirtual getResourceId : (II)I
      //   1761: putfield rightToLeft : I
      //   1764: aload_0
      //   1765: getfield rightToLeft : I
      //   1768: iconst_m1
      //   1769: if_icmpne -> 2022
      //   1772: aload_0
      //   1773: aload_1
      //   1774: iload #6
      //   1776: iconst_m1
      //   1777: invokevirtual getInt : (II)I
      //   1780: putfield rightToLeft : I
      //   1783: goto -> 2022
      //   1786: aload_0
      //   1787: aload_1
      //   1788: iload #6
      //   1790: aload_0
      //   1791: getfield leftToRight : I
      //   1794: invokevirtual getResourceId : (II)I
      //   1797: putfield leftToRight : I
      //   1800: aload_0
      //   1801: getfield leftToRight : I
      //   1804: iconst_m1
      //   1805: if_icmpne -> 2022
      //   1808: aload_0
      //   1809: aload_1
      //   1810: iload #6
      //   1812: iconst_m1
      //   1813: invokevirtual getInt : (II)I
      //   1816: putfield leftToRight : I
      //   1819: goto -> 2022
      //   1822: aload_0
      //   1823: aload_1
      //   1824: iload #6
      //   1826: aload_0
      //   1827: getfield leftToLeft : I
      //   1830: invokevirtual getResourceId : (II)I
      //   1833: putfield leftToLeft : I
      //   1836: aload_0
      //   1837: getfield leftToLeft : I
      //   1840: iconst_m1
      //   1841: if_icmpne -> 2022
      //   1844: aload_0
      //   1845: aload_1
      //   1846: iload #6
      //   1848: iconst_m1
      //   1849: invokevirtual getInt : (II)I
      //   1852: putfield leftToLeft : I
      //   1855: goto -> 2022
      //   1858: aload_0
      //   1859: aload_1
      //   1860: iload #6
      //   1862: aload_0
      //   1863: getfield guidePercent : F
      //   1866: invokevirtual getFloat : (IF)F
      //   1869: putfield guidePercent : F
      //   1872: goto -> 2022
      //   1875: aload_0
      //   1876: aload_1
      //   1877: iload #6
      //   1879: aload_0
      //   1880: getfield guideEnd : I
      //   1883: invokevirtual getDimensionPixelOffset : (II)I
      //   1886: putfield guideEnd : I
      //   1889: goto -> 2022
      //   1892: aload_0
      //   1893: aload_1
      //   1894: iload #6
      //   1896: aload_0
      //   1897: getfield guideBegin : I
      //   1900: invokevirtual getDimensionPixelOffset : (II)I
      //   1903: putfield guideBegin : I
      //   1906: goto -> 2022
      //   1909: aload_0
      //   1910: aload_1
      //   1911: iload #6
      //   1913: aload_0
      //   1914: getfield circleAngle : F
      //   1917: invokevirtual getFloat : (IF)F
      //   1920: ldc_w 360.0
      //   1923: frem
      //   1924: putfield circleAngle : F
      //   1927: aload_0
      //   1928: getfield circleAngle : F
      //   1931: fconst_0
      //   1932: fcmpg
      //   1933: ifge -> 2022
      //   1936: aload_0
      //   1937: ldc_w 360.0
      //   1940: aload_0
      //   1941: getfield circleAngle : F
      //   1944: fsub
      //   1945: ldc_w 360.0
      //   1948: frem
      //   1949: putfield circleAngle : F
      //   1952: goto -> 2022
      //   1955: aload_0
      //   1956: aload_1
      //   1957: iload #6
      //   1959: aload_0
      //   1960: getfield circleRadius : I
      //   1963: invokevirtual getDimensionPixelSize : (II)I
      //   1966: putfield circleRadius : I
      //   1969: goto -> 2022
      //   1972: aload_0
      //   1973: aload_1
      //   1974: iload #6
      //   1976: aload_0
      //   1977: getfield circleConstraint : I
      //   1980: invokevirtual getResourceId : (II)I
      //   1983: putfield circleConstraint : I
      //   1986: aload_0
      //   1987: getfield circleConstraint : I
      //   1990: iconst_m1
      //   1991: if_icmpne -> 2022
      //   1994: aload_0
      //   1995: aload_1
      //   1996: iload #6
      //   1998: iconst_m1
      //   1999: invokevirtual getInt : (II)I
      //   2002: putfield circleConstraint : I
      //   2005: goto -> 2022
      //   2008: aload_0
      //   2009: aload_1
      //   2010: iload #6
      //   2012: aload_0
      //   2013: getfield orientation : I
      //   2016: invokevirtual getInt : (II)I
      //   2019: putfield orientation : I
      //   2022: iload #5
      //   2024: iconst_1
      //   2025: iadd
      //   2026: istore #5
      //   2028: goto -> 344
      //   2031: aload_1
      //   2032: invokevirtual recycle : ()V
      //   2035: aload_0
      //   2036: invokevirtual validate : ()V
      //   2039: return
      //   2040: astore_2
      //   2041: goto -> 2022
      //   2044: astore_2
      //   2045: goto -> 988
      //   2048: astore_2
      //   2049: goto -> 1029
      //   2052: astore_2
      //   2053: goto -> 1091
      //   2056: astore_2
      //   2057: goto -> 1132
      // Exception table:
      //   from	to	target	type
      //   861	873	2040	java/lang/NumberFormatException
      //   886	905	2040	java/lang/NumberFormatException
      //   908	919	2040	java/lang/NumberFormatException
      //   939	947	2040	java/lang/NumberFormatException
      //   971	985	2044	java/lang/Exception
      //   1012	1026	2048	java/lang/Exception
      //   1074	1088	2052	java/lang/Exception
      //   1115	1129	2056	java/lang/Exception }
    
    public LayoutParams(LayoutParams param1LayoutParams) {
      super(param1LayoutParams);
      this.guideBegin = param1LayoutParams.guideBegin;
      this.guideEnd = param1LayoutParams.guideEnd;
      this.guidePercent = param1LayoutParams.guidePercent;
      this.leftToLeft = param1LayoutParams.leftToLeft;
      this.leftToRight = param1LayoutParams.leftToRight;
      this.rightToLeft = param1LayoutParams.rightToLeft;
      this.rightToRight = param1LayoutParams.rightToRight;
      this.topToTop = param1LayoutParams.topToTop;
      this.topToBottom = param1LayoutParams.topToBottom;
      this.bottomToTop = param1LayoutParams.bottomToTop;
      this.bottomToBottom = param1LayoutParams.bottomToBottom;
      this.baselineToBaseline = param1LayoutParams.baselineToBaseline;
      this.circleConstraint = param1LayoutParams.circleConstraint;
      this.circleRadius = param1LayoutParams.circleRadius;
      this.circleAngle = param1LayoutParams.circleAngle;
      this.startToEnd = param1LayoutParams.startToEnd;
      this.startToStart = param1LayoutParams.startToStart;
      this.endToStart = param1LayoutParams.endToStart;
      this.endToEnd = param1LayoutParams.endToEnd;
      this.goneLeftMargin = param1LayoutParams.goneLeftMargin;
      this.goneTopMargin = param1LayoutParams.goneTopMargin;
      this.goneRightMargin = param1LayoutParams.goneRightMargin;
      this.goneBottomMargin = param1LayoutParams.goneBottomMargin;
      this.goneStartMargin = param1LayoutParams.goneStartMargin;
      this.goneEndMargin = param1LayoutParams.goneEndMargin;
      this.horizontalBias = param1LayoutParams.horizontalBias;
      this.verticalBias = param1LayoutParams.verticalBias;
      this.dimensionRatio = param1LayoutParams.dimensionRatio;
      this.dimensionRatioValue = param1LayoutParams.dimensionRatioValue;
      this.dimensionRatioSide = param1LayoutParams.dimensionRatioSide;
      this.horizontalWeight = param1LayoutParams.horizontalWeight;
      this.verticalWeight = param1LayoutParams.verticalWeight;
      this.horizontalChainStyle = param1LayoutParams.horizontalChainStyle;
      this.verticalChainStyle = param1LayoutParams.verticalChainStyle;
      this.constrainedWidth = param1LayoutParams.constrainedWidth;
      this.constrainedHeight = param1LayoutParams.constrainedHeight;
      this.matchConstraintDefaultWidth = param1LayoutParams.matchConstraintDefaultWidth;
      this.matchConstraintDefaultHeight = param1LayoutParams.matchConstraintDefaultHeight;
      this.matchConstraintMinWidth = param1LayoutParams.matchConstraintMinWidth;
      this.matchConstraintMaxWidth = param1LayoutParams.matchConstraintMaxWidth;
      this.matchConstraintMinHeight = param1LayoutParams.matchConstraintMinHeight;
      this.matchConstraintMaxHeight = param1LayoutParams.matchConstraintMaxHeight;
      this.matchConstraintPercentWidth = param1LayoutParams.matchConstraintPercentWidth;
      this.matchConstraintPercentHeight = param1LayoutParams.matchConstraintPercentHeight;
      this.editorAbsoluteX = param1LayoutParams.editorAbsoluteX;
      this.editorAbsoluteY = param1LayoutParams.editorAbsoluteY;
      this.orientation = param1LayoutParams.orientation;
      this.horizontalDimensionFixed = param1LayoutParams.horizontalDimensionFixed;
      this.verticalDimensionFixed = param1LayoutParams.verticalDimensionFixed;
      this.needsBaseline = param1LayoutParams.needsBaseline;
      this.isGuideline = param1LayoutParams.isGuideline;
      this.resolvedLeftToLeft = param1LayoutParams.resolvedLeftToLeft;
      this.resolvedLeftToRight = param1LayoutParams.resolvedLeftToRight;
      this.resolvedRightToLeft = param1LayoutParams.resolvedRightToLeft;
      this.resolvedRightToRight = param1LayoutParams.resolvedRightToRight;
      this.resolveGoneLeftMargin = param1LayoutParams.resolveGoneLeftMargin;
      this.resolveGoneRightMargin = param1LayoutParams.resolveGoneRightMargin;
      this.resolvedHorizontalBias = param1LayoutParams.resolvedHorizontalBias;
      this.widget = param1LayoutParams.widget;
    }
    
    public LayoutParams(ViewGroup.LayoutParams param1LayoutParams) { super(param1LayoutParams); }
    
    public void reset() {
      if (this.widget != null)
        this.widget.reset(); 
    }
    
    @TargetApi(17)
    public void resolveLayoutDirection(int param1Int) { // Byte code:
      //   0: aload_0
      //   1: getfield leftMargin : I
      //   4: istore_3
      //   5: aload_0
      //   6: getfield rightMargin : I
      //   9: istore #4
      //   11: aload_0
      //   12: iload_1
      //   13: invokespecial resolveLayoutDirection : (I)V
      //   16: aload_0
      //   17: iconst_m1
      //   18: putfield resolvedRightToLeft : I
      //   21: aload_0
      //   22: iconst_m1
      //   23: putfield resolvedRightToRight : I
      //   26: aload_0
      //   27: iconst_m1
      //   28: putfield resolvedLeftToLeft : I
      //   31: aload_0
      //   32: iconst_m1
      //   33: putfield resolvedLeftToRight : I
      //   36: aload_0
      //   37: iconst_m1
      //   38: putfield resolveGoneLeftMargin : I
      //   41: aload_0
      //   42: iconst_m1
      //   43: putfield resolveGoneRightMargin : I
      //   46: aload_0
      //   47: aload_0
      //   48: getfield goneLeftMargin : I
      //   51: putfield resolveGoneLeftMargin : I
      //   54: aload_0
      //   55: aload_0
      //   56: getfield goneRightMargin : I
      //   59: putfield resolveGoneRightMargin : I
      //   62: aload_0
      //   63: aload_0
      //   64: getfield horizontalBias : F
      //   67: putfield resolvedHorizontalBias : F
      //   70: aload_0
      //   71: aload_0
      //   72: getfield guideBegin : I
      //   75: putfield resolvedGuideBegin : I
      //   78: aload_0
      //   79: aload_0
      //   80: getfield guideEnd : I
      //   83: putfield resolvedGuideEnd : I
      //   86: aload_0
      //   87: aload_0
      //   88: getfield guidePercent : F
      //   91: putfield resolvedGuidePercent : F
      //   94: aload_0
      //   95: invokevirtual getLayoutDirection : ()I
      //   98: istore_1
      //   99: iconst_0
      //   100: istore_2
      //   101: iconst_1
      //   102: iload_1
      //   103: if_icmpne -> 111
      //   106: iconst_1
      //   107: istore_1
      //   108: goto -> 113
      //   111: iconst_0
      //   112: istore_1
      //   113: iload_1
      //   114: ifeq -> 349
      //   117: aload_0
      //   118: getfield startToEnd : I
      //   121: iconst_m1
      //   122: if_icmpeq -> 138
      //   125: aload_0
      //   126: aload_0
      //   127: getfield startToEnd : I
      //   130: putfield resolvedRightToLeft : I
      //   133: iconst_1
      //   134: istore_1
      //   135: goto -> 159
      //   138: iload_2
      //   139: istore_1
      //   140: aload_0
      //   141: getfield startToStart : I
      //   144: iconst_m1
      //   145: if_icmpeq -> 159
      //   148: aload_0
      //   149: aload_0
      //   150: getfield startToStart : I
      //   153: putfield resolvedRightToRight : I
      //   156: goto -> 133
      //   159: aload_0
      //   160: getfield endToStart : I
      //   163: iconst_m1
      //   164: if_icmpeq -> 177
      //   167: aload_0
      //   168: aload_0
      //   169: getfield endToStart : I
      //   172: putfield resolvedLeftToRight : I
      //   175: iconst_1
      //   176: istore_1
      //   177: aload_0
      //   178: getfield endToEnd : I
      //   181: iconst_m1
      //   182: if_icmpeq -> 195
      //   185: aload_0
      //   186: aload_0
      //   187: getfield endToEnd : I
      //   190: putfield resolvedLeftToLeft : I
      //   193: iconst_1
      //   194: istore_1
      //   195: aload_0
      //   196: getfield goneStartMargin : I
      //   199: iconst_m1
      //   200: if_icmpeq -> 211
      //   203: aload_0
      //   204: aload_0
      //   205: getfield goneStartMargin : I
      //   208: putfield resolveGoneRightMargin : I
      //   211: aload_0
      //   212: getfield goneEndMargin : I
      //   215: iconst_m1
      //   216: if_icmpeq -> 227
      //   219: aload_0
      //   220: aload_0
      //   221: getfield goneEndMargin : I
      //   224: putfield resolveGoneLeftMargin : I
      //   227: iload_1
      //   228: ifeq -> 241
      //   231: aload_0
      //   232: fconst_1
      //   233: aload_0
      //   234: getfield horizontalBias : F
      //   237: fsub
      //   238: putfield resolvedHorizontalBias : F
      //   241: aload_0
      //   242: getfield isGuideline : Z
      //   245: ifeq -> 445
      //   248: aload_0
      //   249: getfield orientation : I
      //   252: iconst_1
      //   253: if_icmpne -> 445
      //   256: aload_0
      //   257: getfield guidePercent : F
      //   260: ldc -1.0
      //   262: fcmpl
      //   263: ifeq -> 289
      //   266: aload_0
      //   267: fconst_1
      //   268: aload_0
      //   269: getfield guidePercent : F
      //   272: fsub
      //   273: putfield resolvedGuidePercent : F
      //   276: aload_0
      //   277: iconst_m1
      //   278: putfield resolvedGuideBegin : I
      //   281: aload_0
      //   282: iconst_m1
      //   283: putfield resolvedGuideEnd : I
      //   286: goto -> 445
      //   289: aload_0
      //   290: getfield guideBegin : I
      //   293: iconst_m1
      //   294: if_icmpeq -> 319
      //   297: aload_0
      //   298: aload_0
      //   299: getfield guideBegin : I
      //   302: putfield resolvedGuideEnd : I
      //   305: aload_0
      //   306: iconst_m1
      //   307: putfield resolvedGuideBegin : I
      //   310: aload_0
      //   311: ldc -1.0
      //   313: putfield resolvedGuidePercent : F
      //   316: goto -> 445
      //   319: aload_0
      //   320: getfield guideEnd : I
      //   323: iconst_m1
      //   324: if_icmpeq -> 445
      //   327: aload_0
      //   328: aload_0
      //   329: getfield guideEnd : I
      //   332: putfield resolvedGuideBegin : I
      //   335: aload_0
      //   336: iconst_m1
      //   337: putfield resolvedGuideEnd : I
      //   340: aload_0
      //   341: ldc -1.0
      //   343: putfield resolvedGuidePercent : F
      //   346: goto -> 445
      //   349: aload_0
      //   350: getfield startToEnd : I
      //   353: iconst_m1
      //   354: if_icmpeq -> 365
      //   357: aload_0
      //   358: aload_0
      //   359: getfield startToEnd : I
      //   362: putfield resolvedLeftToRight : I
      //   365: aload_0
      //   366: getfield startToStart : I
      //   369: iconst_m1
      //   370: if_icmpeq -> 381
      //   373: aload_0
      //   374: aload_0
      //   375: getfield startToStart : I
      //   378: putfield resolvedLeftToLeft : I
      //   381: aload_0
      //   382: getfield endToStart : I
      //   385: iconst_m1
      //   386: if_icmpeq -> 397
      //   389: aload_0
      //   390: aload_0
      //   391: getfield endToStart : I
      //   394: putfield resolvedRightToLeft : I
      //   397: aload_0
      //   398: getfield endToEnd : I
      //   401: iconst_m1
      //   402: if_icmpeq -> 413
      //   405: aload_0
      //   406: aload_0
      //   407: getfield endToEnd : I
      //   410: putfield resolvedRightToRight : I
      //   413: aload_0
      //   414: getfield goneStartMargin : I
      //   417: iconst_m1
      //   418: if_icmpeq -> 429
      //   421: aload_0
      //   422: aload_0
      //   423: getfield goneStartMargin : I
      //   426: putfield resolveGoneLeftMargin : I
      //   429: aload_0
      //   430: getfield goneEndMargin : I
      //   433: iconst_m1
      //   434: if_icmpeq -> 445
      //   437: aload_0
      //   438: aload_0
      //   439: getfield goneEndMargin : I
      //   442: putfield resolveGoneRightMargin : I
      //   445: aload_0
      //   446: getfield endToStart : I
      //   449: iconst_m1
      //   450: if_icmpne -> 613
      //   453: aload_0
      //   454: getfield endToEnd : I
      //   457: iconst_m1
      //   458: if_icmpne -> 613
      //   461: aload_0
      //   462: getfield startToStart : I
      //   465: iconst_m1
      //   466: if_icmpne -> 613
      //   469: aload_0
      //   470: getfield startToEnd : I
      //   473: iconst_m1
      //   474: if_icmpne -> 613
      //   477: aload_0
      //   478: getfield rightToLeft : I
      //   481: iconst_m1
      //   482: if_icmpeq -> 514
      //   485: aload_0
      //   486: aload_0
      //   487: getfield rightToLeft : I
      //   490: putfield resolvedRightToLeft : I
      //   493: aload_0
      //   494: getfield rightMargin : I
      //   497: ifgt -> 548
      //   500: iload #4
      //   502: ifle -> 548
      //   505: aload_0
      //   506: iload #4
      //   508: putfield rightMargin : I
      //   511: goto -> 548
      //   514: aload_0
      //   515: getfield rightToRight : I
      //   518: iconst_m1
      //   519: if_icmpeq -> 548
      //   522: aload_0
      //   523: aload_0
      //   524: getfield rightToRight : I
      //   527: putfield resolvedRightToRight : I
      //   530: aload_0
      //   531: getfield rightMargin : I
      //   534: ifgt -> 548
      //   537: iload #4
      //   539: ifle -> 548
      //   542: aload_0
      //   543: iload #4
      //   545: putfield rightMargin : I
      //   548: aload_0
      //   549: getfield leftToLeft : I
      //   552: iconst_m1
      //   553: if_icmpeq -> 581
      //   556: aload_0
      //   557: aload_0
      //   558: getfield leftToLeft : I
      //   561: putfield resolvedLeftToLeft : I
      //   564: aload_0
      //   565: getfield leftMargin : I
      //   568: ifgt -> 613
      //   571: iload_3
      //   572: ifle -> 613
      //   575: aload_0
      //   576: iload_3
      //   577: putfield leftMargin : I
      //   580: return
      //   581: aload_0
      //   582: getfield leftToRight : I
      //   585: iconst_m1
      //   586: if_icmpeq -> 613
      //   589: aload_0
      //   590: aload_0
      //   591: getfield leftToRight : I
      //   594: putfield resolvedLeftToRight : I
      //   597: aload_0
      //   598: getfield leftMargin : I
      //   601: ifgt -> 613
      //   604: iload_3
      //   605: ifle -> 613
      //   608: aload_0
      //   609: iload_3
      //   610: putfield leftMargin : I
      //   613: return }
    
    public void validate() {
      this.isGuideline = false;
      this.horizontalDimensionFixed = true;
      this.verticalDimensionFixed = true;
      if (this.width == -2 && this.constrainedWidth) {
        this.horizontalDimensionFixed = false;
        this.matchConstraintDefaultWidth = 1;
      } 
      if (this.height == -2 && this.constrainedHeight) {
        this.verticalDimensionFixed = false;
        this.matchConstraintDefaultHeight = 1;
      } 
      if (this.width == 0 || this.width == -1) {
        this.horizontalDimensionFixed = false;
        if (this.width == 0 && this.matchConstraintDefaultWidth == 1) {
          this.width = -2;
          this.constrainedWidth = true;
        } 
      } 
      if (this.height == 0 || this.height == -1) {
        this.verticalDimensionFixed = false;
        if (this.height == 0 && this.matchConstraintDefaultHeight == 1) {
          this.height = -2;
          this.constrainedHeight = true;
        } 
      } 
      if (this.guidePercent != -1.0F || this.guideBegin != -1 || this.guideEnd != -1) {
        this.isGuideline = true;
        this.horizontalDimensionFixed = true;
        this.verticalDimensionFixed = true;
        if (!(this.widget instanceof Guideline))
          this.widget = new Guideline(); 
        ((Guideline)this.widget).setOrientation(this.orientation);
      } 
    }
    
    private static class Table {
      public static final int ANDROID_ORIENTATION = 1;
      
      public static final int LAYOUT_CONSTRAINED_HEIGHT = 28;
      
      public static final int LAYOUT_CONSTRAINED_WIDTH = 27;
      
      public static final int LAYOUT_CONSTRAINT_BASELINE_CREATOR = 43;
      
      public static final int LAYOUT_CONSTRAINT_BASELINE_TO_BASELINE_OF = 16;
      
      public static final int LAYOUT_CONSTRAINT_BOTTOM_CREATOR = 42;
      
      public static final int LAYOUT_CONSTRAINT_BOTTOM_TO_BOTTOM_OF = 15;
      
      public static final int LAYOUT_CONSTRAINT_BOTTOM_TO_TOP_OF = 14;
      
      public static final int LAYOUT_CONSTRAINT_CIRCLE = 2;
      
      public static final int LAYOUT_CONSTRAINT_CIRCLE_ANGLE = 4;
      
      public static final int LAYOUT_CONSTRAINT_CIRCLE_RADIUS = 3;
      
      public static final int LAYOUT_CONSTRAINT_DIMENSION_RATIO = 44;
      
      public static final int LAYOUT_CONSTRAINT_END_TO_END_OF = 20;
      
      public static final int LAYOUT_CONSTRAINT_END_TO_START_OF = 19;
      
      public static final int LAYOUT_CONSTRAINT_GUIDE_BEGIN = 5;
      
      public static final int LAYOUT_CONSTRAINT_GUIDE_END = 6;
      
      public static final int LAYOUT_CONSTRAINT_GUIDE_PERCENT = 7;
      
      public static final int LAYOUT_CONSTRAINT_HEIGHT_DEFAULT = 32;
      
      public static final int LAYOUT_CONSTRAINT_HEIGHT_MAX = 37;
      
      public static final int LAYOUT_CONSTRAINT_HEIGHT_MIN = 36;
      
      public static final int LAYOUT_CONSTRAINT_HEIGHT_PERCENT = 38;
      
      public static final int LAYOUT_CONSTRAINT_HORIZONTAL_BIAS = 29;
      
      public static final int LAYOUT_CONSTRAINT_HORIZONTAL_CHAINSTYLE = 47;
      
      public static final int LAYOUT_CONSTRAINT_HORIZONTAL_WEIGHT = 45;
      
      public static final int LAYOUT_CONSTRAINT_LEFT_CREATOR = 39;
      
      public static final int LAYOUT_CONSTRAINT_LEFT_TO_LEFT_OF = 8;
      
      public static final int LAYOUT_CONSTRAINT_LEFT_TO_RIGHT_OF = 9;
      
      public static final int LAYOUT_CONSTRAINT_RIGHT_CREATOR = 41;
      
      public static final int LAYOUT_CONSTRAINT_RIGHT_TO_LEFT_OF = 10;
      
      public static final int LAYOUT_CONSTRAINT_RIGHT_TO_RIGHT_OF = 11;
      
      public static final int LAYOUT_CONSTRAINT_START_TO_END_OF = 17;
      
      public static final int LAYOUT_CONSTRAINT_START_TO_START_OF = 18;
      
      public static final int LAYOUT_CONSTRAINT_TOP_CREATOR = 40;
      
      public static final int LAYOUT_CONSTRAINT_TOP_TO_BOTTOM_OF = 13;
      
      public static final int LAYOUT_CONSTRAINT_TOP_TO_TOP_OF = 12;
      
      public static final int LAYOUT_CONSTRAINT_VERTICAL_BIAS = 30;
      
      public static final int LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE = 48;
      
      public static final int LAYOUT_CONSTRAINT_VERTICAL_WEIGHT = 46;
      
      public static final int LAYOUT_CONSTRAINT_WIDTH_DEFAULT = 31;
      
      public static final int LAYOUT_CONSTRAINT_WIDTH_MAX = 34;
      
      public static final int LAYOUT_CONSTRAINT_WIDTH_MIN = 33;
      
      public static final int LAYOUT_CONSTRAINT_WIDTH_PERCENT = 35;
      
      public static final int LAYOUT_EDITOR_ABSOLUTEX = 49;
      
      public static final int LAYOUT_EDITOR_ABSOLUTEY = 50;
      
      public static final int LAYOUT_GONE_MARGIN_BOTTOM = 24;
      
      public static final int LAYOUT_GONE_MARGIN_END = 26;
      
      public static final int LAYOUT_GONE_MARGIN_LEFT = 21;
      
      public static final int LAYOUT_GONE_MARGIN_RIGHT = 23;
      
      public static final int LAYOUT_GONE_MARGIN_START = 25;
      
      public static final int LAYOUT_GONE_MARGIN_TOP = 22;
      
      public static final int UNUSED = 0;
      
      public static final SparseIntArray map = new SparseIntArray();
      
      static  {
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintLeft_toLeftOf, 8);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintLeft_toRightOf, 9);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintRight_toLeftOf, 10);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintRight_toRightOf, 11);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintTop_toTopOf, 12);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintTop_toBottomOf, 13);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintBottom_toTopOf, 14);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintBottom_toBottomOf, 15);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintBaseline_toBaselineOf, 16);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintCircle, 2);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintCircleRadius, 3);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintCircleAngle, 4);
        map.append(R.styleable.ConstraintLayout_Layout_layout_editor_absoluteX, 49);
        map.append(R.styleable.ConstraintLayout_Layout_layout_editor_absoluteY, 50);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintGuide_begin, 5);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintGuide_end, 6);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintGuide_percent, 7);
        map.append(R.styleable.ConstraintLayout_Layout_android_orientation, 1);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintStart_toEndOf, 17);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintStart_toStartOf, 18);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintEnd_toStartOf, 19);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintEnd_toEndOf, 20);
        map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginLeft, 21);
        map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginTop, 22);
        map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginRight, 23);
        map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginBottom, 24);
        map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginStart, 25);
        map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginEnd, 26);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHorizontal_bias, 29);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintVertical_bias, 30);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintDimensionRatio, 44);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHorizontal_weight, 45);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintVertical_weight, 46);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHorizontal_chainStyle, 47);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintVertical_chainStyle, 48);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constrainedWidth, 27);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constrainedHeight, 28);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintWidth_default, 31);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHeight_default, 32);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintWidth_min, 33);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintWidth_max, 34);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintWidth_percent, 35);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHeight_min, 36);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHeight_max, 37);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHeight_percent, 38);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintLeft_creator, 39);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintTop_creator, 40);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintRight_creator, 41);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintBottom_creator, 42);
        map.append(R.styleable.ConstraintLayout_Layout_layout_constraintBaseline_creator, 43);
      }
    }
  }
  
  private static class Table {
    public static final int ANDROID_ORIENTATION = 1;
    
    public static final int LAYOUT_CONSTRAINED_HEIGHT = 28;
    
    public static final int LAYOUT_CONSTRAINED_WIDTH = 27;
    
    public static final int LAYOUT_CONSTRAINT_BASELINE_CREATOR = 43;
    
    public static final int LAYOUT_CONSTRAINT_BASELINE_TO_BASELINE_OF = 16;
    
    public static final int LAYOUT_CONSTRAINT_BOTTOM_CREATOR = 42;
    
    public static final int LAYOUT_CONSTRAINT_BOTTOM_TO_BOTTOM_OF = 15;
    
    public static final int LAYOUT_CONSTRAINT_BOTTOM_TO_TOP_OF = 14;
    
    public static final int LAYOUT_CONSTRAINT_CIRCLE = 2;
    
    public static final int LAYOUT_CONSTRAINT_CIRCLE_ANGLE = 4;
    
    public static final int LAYOUT_CONSTRAINT_CIRCLE_RADIUS = 3;
    
    public static final int LAYOUT_CONSTRAINT_DIMENSION_RATIO = 44;
    
    public static final int LAYOUT_CONSTRAINT_END_TO_END_OF = 20;
    
    public static final int LAYOUT_CONSTRAINT_END_TO_START_OF = 19;
    
    public static final int LAYOUT_CONSTRAINT_GUIDE_BEGIN = 5;
    
    public static final int LAYOUT_CONSTRAINT_GUIDE_END = 6;
    
    public static final int LAYOUT_CONSTRAINT_GUIDE_PERCENT = 7;
    
    public static final int LAYOUT_CONSTRAINT_HEIGHT_DEFAULT = 32;
    
    public static final int LAYOUT_CONSTRAINT_HEIGHT_MAX = 37;
    
    public static final int LAYOUT_CONSTRAINT_HEIGHT_MIN = 36;
    
    public static final int LAYOUT_CONSTRAINT_HEIGHT_PERCENT = 38;
    
    public static final int LAYOUT_CONSTRAINT_HORIZONTAL_BIAS = 29;
    
    public static final int LAYOUT_CONSTRAINT_HORIZONTAL_CHAINSTYLE = 47;
    
    public static final int LAYOUT_CONSTRAINT_HORIZONTAL_WEIGHT = 45;
    
    public static final int LAYOUT_CONSTRAINT_LEFT_CREATOR = 39;
    
    public static final int LAYOUT_CONSTRAINT_LEFT_TO_LEFT_OF = 8;
    
    public static final int LAYOUT_CONSTRAINT_LEFT_TO_RIGHT_OF = 9;
    
    public static final int LAYOUT_CONSTRAINT_RIGHT_CREATOR = 41;
    
    public static final int LAYOUT_CONSTRAINT_RIGHT_TO_LEFT_OF = 10;
    
    public static final int LAYOUT_CONSTRAINT_RIGHT_TO_RIGHT_OF = 11;
    
    public static final int LAYOUT_CONSTRAINT_START_TO_END_OF = 17;
    
    public static final int LAYOUT_CONSTRAINT_START_TO_START_OF = 18;
    
    public static final int LAYOUT_CONSTRAINT_TOP_CREATOR = 40;
    
    public static final int LAYOUT_CONSTRAINT_TOP_TO_BOTTOM_OF = 13;
    
    public static final int LAYOUT_CONSTRAINT_TOP_TO_TOP_OF = 12;
    
    public static final int LAYOUT_CONSTRAINT_VERTICAL_BIAS = 30;
    
    public static final int LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE = 48;
    
    public static final int LAYOUT_CONSTRAINT_VERTICAL_WEIGHT = 46;
    
    public static final int LAYOUT_CONSTRAINT_WIDTH_DEFAULT = 31;
    
    public static final int LAYOUT_CONSTRAINT_WIDTH_MAX = 34;
    
    public static final int LAYOUT_CONSTRAINT_WIDTH_MIN = 33;
    
    public static final int LAYOUT_CONSTRAINT_WIDTH_PERCENT = 35;
    
    public static final int LAYOUT_EDITOR_ABSOLUTEX = 49;
    
    public static final int LAYOUT_EDITOR_ABSOLUTEY = 50;
    
    public static final int LAYOUT_GONE_MARGIN_BOTTOM = 24;
    
    public static final int LAYOUT_GONE_MARGIN_END = 26;
    
    public static final int LAYOUT_GONE_MARGIN_LEFT = 21;
    
    public static final int LAYOUT_GONE_MARGIN_RIGHT = 23;
    
    public static final int LAYOUT_GONE_MARGIN_START = 25;
    
    public static final int LAYOUT_GONE_MARGIN_TOP = 22;
    
    public static final int UNUSED = 0;
    
    public static final SparseIntArray map = new SparseIntArray();
    
    static  {
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintLeft_toLeftOf, 8);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintLeft_toRightOf, 9);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintRight_toLeftOf, 10);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintRight_toRightOf, 11);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintTop_toTopOf, 12);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintTop_toBottomOf, 13);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintBottom_toTopOf, 14);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintBottom_toBottomOf, 15);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintBaseline_toBaselineOf, 16);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintCircle, 2);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintCircleRadius, 3);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintCircleAngle, 4);
      map.append(R.styleable.ConstraintLayout_Layout_layout_editor_absoluteX, 49);
      map.append(R.styleable.ConstraintLayout_Layout_layout_editor_absoluteY, 50);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintGuide_begin, 5);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintGuide_end, 6);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintGuide_percent, 7);
      map.append(R.styleable.ConstraintLayout_Layout_android_orientation, 1);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintStart_toEndOf, 17);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintStart_toStartOf, 18);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintEnd_toStartOf, 19);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintEnd_toEndOf, 20);
      map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginLeft, 21);
      map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginTop, 22);
      map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginRight, 23);
      map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginBottom, 24);
      map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginStart, 25);
      map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginEnd, 26);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHorizontal_bias, 29);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintVertical_bias, 30);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintDimensionRatio, 44);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHorizontal_weight, 45);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintVertical_weight, 46);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHorizontal_chainStyle, 47);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintVertical_chainStyle, 48);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constrainedWidth, 27);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constrainedHeight, 28);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintWidth_default, 31);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHeight_default, 32);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintWidth_min, 33);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintWidth_max, 34);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintWidth_percent, 35);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHeight_min, 36);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHeight_max, 37);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHeight_percent, 38);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintLeft_creator, 39);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintTop_creator, 40);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintRight_creator, 41);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintBottom_creator, 42);
      map.append(R.styleable.ConstraintLayout_Layout_layout_constraintBaseline_creator, 43);
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/constraint/ConstraintLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */