package android.support.constraint.solver.widgets;

import android.support.constraint.solver.Cache;
import android.support.constraint.solver.LinearSystem;
import android.support.constraint.solver.Metrics;
import android.support.constraint.solver.SolverVariable;
import java.util.ArrayList;

public class ConstraintWidget {
  protected static final int ANCHOR_BASELINE = 4;
  
  protected static final int ANCHOR_BOTTOM = 3;
  
  protected static final int ANCHOR_LEFT = 0;
  
  protected static final int ANCHOR_RIGHT = 1;
  
  protected static final int ANCHOR_TOP = 2;
  
  private static final boolean AUTOTAG_CENTER = false;
  
  public static final int CHAIN_PACKED = 2;
  
  public static final int CHAIN_SPREAD = 0;
  
  public static final int CHAIN_SPREAD_INSIDE = 1;
  
  public static float DEFAULT_BIAS = 0.5F;
  
  static final int DIMENSION_HORIZONTAL = 0;
  
  static final int DIMENSION_VERTICAL = 1;
  
  protected static final int DIRECT = 2;
  
  public static final int GONE = 8;
  
  public static final int HORIZONTAL = 0;
  
  public static final int INVISIBLE = 4;
  
  public static final int MATCH_CONSTRAINT_PERCENT = 2;
  
  public static final int MATCH_CONSTRAINT_RATIO = 3;
  
  public static final int MATCH_CONSTRAINT_SPREAD = 0;
  
  public static final int MATCH_CONSTRAINT_WRAP = 1;
  
  protected static final int SOLVER = 1;
  
  public static final int UNKNOWN = -1;
  
  public static final int VERTICAL = 1;
  
  public static final int VISIBLE = 0;
  
  private static final int WRAP = -2;
  
  protected ArrayList<ConstraintAnchor> mAnchors = new ArrayList();
  
  ConstraintAnchor mBaseline = new ConstraintAnchor(this, ConstraintAnchor.Type.BASELINE);
  
  int mBaselineDistance = 0;
  
  ConstraintAnchor mBottom = new ConstraintAnchor(this, ConstraintAnchor.Type.BOTTOM);
  
  boolean mBottomHasCentered;
  
  ConstraintAnchor mCenter = new ConstraintAnchor(this, ConstraintAnchor.Type.CENTER);
  
  ConstraintAnchor mCenterX = new ConstraintAnchor(this, ConstraintAnchor.Type.CENTER_X);
  
  ConstraintAnchor mCenterY = new ConstraintAnchor(this, ConstraintAnchor.Type.CENTER_Y);
  
  private float mCircleConstraintAngle = 0.0F;
  
  private Object mCompanionWidget;
  
  private int mContainerItemSkip = 0;
  
  private String mDebugName = null;
  
  protected float mDimensionRatio = 0.0F;
  
  protected int mDimensionRatioSide = -1;
  
  int mDistToBottom;
  
  int mDistToLeft;
  
  int mDistToRight;
  
  int mDistToTop;
  
  private int mDrawHeight = 0;
  
  private int mDrawWidth = 0;
  
  private int mDrawX = 0;
  
  private int mDrawY = 0;
  
  int mHeight = 0;
  
  float mHorizontalBiasPercent = DEFAULT_BIAS;
  
  boolean mHorizontalChainFixedPosition;
  
  int mHorizontalChainStyle = 0;
  
  ConstraintWidget mHorizontalNextWidget = null;
  
  public int mHorizontalResolution = -1;
  
  boolean mHorizontalWrapVisited;
  
  boolean mIsHeightWrapContent;
  
  boolean mIsWidthWrapContent;
  
  ConstraintAnchor mLeft = new ConstraintAnchor(this, ConstraintAnchor.Type.LEFT);
  
  boolean mLeftHasCentered;
  
  protected ConstraintAnchor[] mListAnchors = { this.mLeft, this.mRight, this.mTop, this.mBottom, this.mBaseline, this.mCenter };
  
  protected DimensionBehaviour[] mListDimensionBehaviors = { DimensionBehaviour.FIXED, DimensionBehaviour.FIXED };
  
  protected ConstraintWidget[] mListNextMatchConstraintsWidget = { null, null };
  
  protected ConstraintWidget[] mListNextVisibleWidget = { null, null };
  
  int mMatchConstraintDefaultHeight = 0;
  
  int mMatchConstraintDefaultWidth = 0;
  
  int mMatchConstraintMaxHeight = 0;
  
  int mMatchConstraintMaxWidth = 0;
  
  int mMatchConstraintMinHeight = 0;
  
  int mMatchConstraintMinWidth = 0;
  
  float mMatchConstraintPercentHeight = 1.0F;
  
  float mMatchConstraintPercentWidth = 1.0F;
  
  private int[] mMaxDimension = { Integer.MAX_VALUE, Integer.MAX_VALUE };
  
  protected int mMinHeight;
  
  protected int mMinWidth;
  
  protected int mOffsetX = 0;
  
  protected int mOffsetY = 0;
  
  ConstraintWidget mParent = null;
  
  ResolutionDimension mResolutionHeight;
  
  ResolutionDimension mResolutionWidth;
  
  float mResolvedDimensionRatio = 1.0F;
  
  int mResolvedDimensionRatioSide = -1;
  
  ConstraintAnchor mRight = new ConstraintAnchor(this, ConstraintAnchor.Type.RIGHT);
  
  boolean mRightHasCentered;
  
  ConstraintAnchor mTop = new ConstraintAnchor(this, ConstraintAnchor.Type.TOP);
  
  boolean mTopHasCentered;
  
  private String mType = null;
  
  float mVerticalBiasPercent = DEFAULT_BIAS;
  
  boolean mVerticalChainFixedPosition;
  
  int mVerticalChainStyle = 0;
  
  ConstraintWidget mVerticalNextWidget = null;
  
  public int mVerticalResolution = -1;
  
  boolean mVerticalWrapVisited;
  
  private int mVisibility = 0;
  
  float[] mWeight = { 0.0F, 0.0F };
  
  int mWidth = 0;
  
  private int mWrapHeight;
  
  private int mWrapWidth;
  
  protected int mX = 0;
  
  protected int mY = 0;
  
  static  {
  
  }
  
  public ConstraintWidget() { addAnchors(); }
  
  public ConstraintWidget(int paramInt1, int paramInt2) { this(0, 0, paramInt1, paramInt2); }
  
  public ConstraintWidget(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.mX = paramInt1;
    this.mY = paramInt2;
    this.mWidth = paramInt3;
    this.mHeight = paramInt4;
    addAnchors();
    forceUpdateDrawPosition();
  }
  
  private void addAnchors() {
    this.mAnchors.add(this.mLeft);
    this.mAnchors.add(this.mTop);
    this.mAnchors.add(this.mRight);
    this.mAnchors.add(this.mBottom);
    this.mAnchors.add(this.mCenterX);
    this.mAnchors.add(this.mCenterY);
    this.mAnchors.add(this.mCenter);
    this.mAnchors.add(this.mBaseline);
  }
  
  private void applyConstraints(LinearSystem paramLinearSystem, boolean paramBoolean1, SolverVariable paramSolverVariable1, SolverVariable paramSolverVariable2, DimensionBehaviour paramDimensionBehaviour, boolean paramBoolean2, ConstraintAnchor paramConstraintAnchor1, ConstraintAnchor paramConstraintAnchor2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat1, boolean paramBoolean3, boolean paramBoolean4, int paramInt5, int paramInt6, int paramInt7, float paramFloat2, boolean paramBoolean5) {
    Metrics metrics;
    SolverVariable solverVariable4 = paramLinearSystem.createObjectVariable(paramConstraintAnchor1);
    SolverVariable solverVariable2 = paramLinearSystem.createObjectVariable(paramConstraintAnchor2);
    SolverVariable solverVariable3 = paramLinearSystem.createObjectVariable(paramConstraintAnchor1.getTarget());
    SolverVariable solverVariable5 = paramLinearSystem.createObjectVariable(paramConstraintAnchor2.getTarget());
    if (paramLinearSystem.graphOptimizer && (paramConstraintAnchor1.getResolutionNode()).state == 1 && (paramConstraintAnchor2.getResolutionNode()).state == 1) {
      if (LinearSystem.getMetrics() != null) {
        metrics = LinearSystem.getMetrics();
        metrics.resolvedWidgets++;
      } 
      paramConstraintAnchor1.getResolutionNode().addResolvedValue(paramLinearSystem);
      paramConstraintAnchor2.getResolutionNode().addResolvedValue(paramLinearSystem);
      if (!paramBoolean4 && paramBoolean1)
        paramLinearSystem.addGreaterThan(paramSolverVariable2, solverVariable2, 0, 6); 
      return;
    } 
    if (LinearSystem.getMetrics() != null) {
      Metrics metrics1 = LinearSystem.getMetrics();
      metrics1.nonresolvedWidgets++;
    } 
    boolean bool2 = paramConstraintAnchor1.isConnected();
    boolean bool1 = paramConstraintAnchor2.isConnected();
    boolean bool3 = this.mCenter.isConnected();
    if (bool2) {
      b = 1;
    } else {
      b = 0;
    } 
    int i = b;
    if (bool1)
      i = b + true; 
    byte b = i;
    if (bool3)
      b = i + 1; 
    if (paramBoolean3) {
      i = 3;
    } else {
      i = paramInt5;
    } 
    switch (paramDimensionBehaviour) {
      default:
        paramInt5 = 0;
        break;
      case MATCH_CONSTRAINT:
        paramInt5 = 1;
        break;
    } 
    if (this.mVisibility == 8) {
      paramInt2 = 0;
      paramInt5 = 0;
    } 
    if (paramBoolean5)
      if (!bool2 && !bool1 && !bool3) {
        paramLinearSystem.addEquality(solverVariable4, paramInt1);
      } else if (bool2 && !bool1) {
        paramLinearSystem.addEquality(solverVariable4, solverVariable3, paramConstraintAnchor1.getMargin(), 6);
      }  
    SolverVariable solverVariable1 = solverVariable3;
    if (paramInt5 == 0) {
      if (paramBoolean2) {
        solverVariable3 = solverVariable2;
        paramLinearSystem.addEquality(solverVariable3, solverVariable4, 0, 3);
        if (paramInt3 > 0)
          paramLinearSystem.addGreaterThan(solverVariable3, solverVariable4, paramInt3, 6); 
        if (paramInt4 < Integer.MAX_VALUE)
          paramLinearSystem.addLowerThan(solverVariable3, solverVariable4, paramInt4, 6); 
      } else {
        paramLinearSystem.addEquality(solverVariable2, solverVariable4, paramInt2, 6);
      } 
      paramInt2 = paramInt5;
    } else {
      SolverVariable solverVariable = solverVariable2;
      if (paramInt6 == -2) {
        paramInt1 = paramInt2;
      } else {
        paramInt1 = paramInt6;
      } 
      paramInt4 = paramInt7;
      if (paramInt7 == -2)
        paramInt4 = paramInt2; 
      if (paramInt1 > 0) {
        if (paramBoolean1) {
          paramLinearSystem.addGreaterThan(solverVariable, solverVariable4, paramInt1, 6);
        } else {
          paramLinearSystem.addGreaterThan(solverVariable, solverVariable4, paramInt1, 6);
        } 
        paramInt2 = Math.max(paramInt2, paramInt1);
      } 
      int j = paramInt2;
      if (paramInt4 > 0) {
        if (paramBoolean1) {
          paramLinearSystem.addLowerThan(solverVariable, solverVariable4, paramInt4, 1);
        } else {
          paramLinearSystem.addLowerThan(solverVariable, solverVariable4, paramInt4, 6);
        } 
        j = Math.min(paramInt2, paramInt4);
      } 
      if (i == 1) {
        if (paramBoolean1) {
          paramLinearSystem.addEquality(solverVariable, solverVariable4, j, 6);
        } else if (paramBoolean4) {
          paramLinearSystem.addEquality(solverVariable, solverVariable4, j, 4);
        } else {
          paramLinearSystem.addEquality(solverVariable, solverVariable4, j, 1);
        } 
      } else if (i == 2) {
        SolverVariable solverVariable6;
        if (paramConstraintAnchor1.getType() == ConstraintAnchor.Type.TOP || paramConstraintAnchor1.getType() == ConstraintAnchor.Type.BOTTOM) {
          solverVariable3 = paramLinearSystem.createObjectVariable(this.mParent.getAnchor(ConstraintAnchor.Type.TOP));
          solverVariable6 = paramLinearSystem.createObjectVariable(this.mParent.getAnchor(ConstraintAnchor.Type.BOTTOM));
        } else {
          solverVariable3 = paramLinearSystem.createObjectVariable(this.mParent.getAnchor(ConstraintAnchor.Type.LEFT));
          solverVariable6 = paramLinearSystem.createObjectVariable(this.mParent.getAnchor(ConstraintAnchor.Type.RIGHT));
        } 
        paramLinearSystem.addConstraint(paramLinearSystem.createRow().createRowDimensionRatio(solverVariable, solverVariable4, solverVariable6, solverVariable3, paramFloat2));
        paramInt5 = 0;
      } 
      paramInt6 = paramInt1;
      paramInt7 = paramInt4;
      paramInt2 = paramInt5;
      if (paramInt5 != 0) {
        paramInt6 = paramInt1;
        paramInt7 = paramInt4;
        paramInt2 = paramInt5;
        if (b != 2) {
          paramInt6 = paramInt1;
          paramInt7 = paramInt4;
          paramInt2 = paramInt5;
          if (!paramBoolean3) {
            paramInt5 = Math.max(paramInt1, j);
            paramInt2 = paramInt5;
            if (paramInt4 > 0)
              paramInt2 = Math.min(paramInt4, paramInt5); 
            paramLinearSystem.addEquality(solverVariable, solverVariable4, paramInt2, 6);
            paramInt2 = 0;
            paramInt7 = paramInt4;
            paramInt6 = paramInt1;
          } 
        } 
      } 
    } 
    if (!paramBoolean5 || paramBoolean4) {
      if (b < 2 && paramBoolean1) {
        paramLinearSystem.addGreaterThan(solverVariable4, metrics, 0, 6);
        paramLinearSystem.addGreaterThan(paramSolverVariable2, solverVariable2, 0, 6);
      } 
      return;
    } 
    if (!bool2 && !bool1 && !bool3) {
      if (paramBoolean1)
        paramLinearSystem.addGreaterThan(paramSolverVariable2, solverVariable2, 0, 5); 
    } else if (bool2 && !bool1) {
      if (paramBoolean1)
        paramLinearSystem.addGreaterThan(paramSolverVariable2, solverVariable2, 0, 5); 
    } else if (!bool2 && bool1) {
      paramLinearSystem.addEquality(solverVariable2, solverVariable5, -paramConstraintAnchor2.getMargin(), 6);
      if (paramBoolean1)
        paramLinearSystem.addGreaterThan(solverVariable4, metrics, 0, 5); 
    } else {
      paramInt4 = 1;
      paramInt5 = 1;
      if (bool2 && bool1) {
        if (paramInt2 != 0) {
          if (paramBoolean1 && paramInt3 == 0)
            paramLinearSystem.addGreaterThan(solverVariable2, solverVariable4, 0, 6); 
          if (i == 0) {
            if (paramInt7 > 0 || paramInt6 > 0) {
              paramInt2 = 4;
              paramInt1 = 1;
            } else {
              paramInt2 = 6;
              paramInt1 = 0;
            } 
            paramLinearSystem.addEquality(solverVariable4, solverVariable1, paramConstraintAnchor1.getMargin(), paramInt2);
            paramLinearSystem.addEquality(solverVariable2, solverVariable5, -paramConstraintAnchor2.getMargin(), paramInt2);
            if (paramInt7 > 0 || paramInt6 > 0) {
              paramInt2 = 1;
            } else {
              paramInt2 = 0;
            } 
            paramInt3 = paramInt1;
            paramInt1 = paramInt2;
            paramInt2 = 5;
          } else {
            if (i == 1) {
              paramInt2 = 6;
            } else if (i == 3) {
              if (!paramBoolean3) {
                paramInt1 = 6;
              } else {
                paramInt1 = 4;
              } 
              paramLinearSystem.addEquality(solverVariable4, solverVariable1, paramConstraintAnchor1.getMargin(), paramInt1);
              paramLinearSystem.addEquality(solverVariable2, solverVariable5, -paramConstraintAnchor2.getMargin(), paramInt1);
              paramInt2 = 5;
            } else {
              paramInt1 = 0;
              paramInt2 = 5;
              paramInt3 = 0;
            } 
            paramInt3 = 1;
            paramInt1 = paramInt4;
          } 
        } else {
          paramInt1 = paramInt5;
          if (paramBoolean1) {
            paramLinearSystem.addGreaterThan(solverVariable4, solverVariable1, paramConstraintAnchor1.getMargin(), 5);
            paramLinearSystem.addLowerThan(solverVariable2, solverVariable5, -paramConstraintAnchor2.getMargin(), 5);
            paramInt1 = paramInt5;
          } 
          paramInt2 = 5;
          paramInt3 = 0;
        } 
        if (paramInt1 != 0)
          paramLinearSystem.addCentering(solverVariable4, solverVariable1, paramConstraintAnchor1.getMargin(), paramFloat1, solverVariable5, solverVariable2, paramConstraintAnchor2.getMargin(), paramInt2); 
        if (paramInt3 != 0) {
          paramLinearSystem.addGreaterThan(solverVariable4, solverVariable1, paramConstraintAnchor1.getMargin(), 6);
          paramLinearSystem.addLowerThan(solverVariable2, solverVariable5, -paramConstraintAnchor2.getMargin(), 6);
        } 
        if (paramBoolean1)
          paramLinearSystem.addGreaterThan(solverVariable4, metrics, 0, 6); 
      } 
    } 
    if (paramBoolean1)
      paramLinearSystem.addGreaterThan(paramSolverVariable2, solverVariable2, 0, 6); 
  }
  
  public void addToSolver(LinearSystem paramLinearSystem) { // Byte code:
    //   0: aload_0
    //   1: astore #18
    //   3: aload_1
    //   4: aload #18
    //   6: getfield mLeft : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   9: invokevirtual createObjectVariable : (Ljava/lang/Object;)Landroid/support/constraint/solver/SolverVariable;
    //   12: astore #20
    //   14: aload_1
    //   15: aload #18
    //   17: getfield mRight : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   20: invokevirtual createObjectVariable : (Ljava/lang/Object;)Landroid/support/constraint/solver/SolverVariable;
    //   23: astore #15
    //   25: aload_1
    //   26: aload #18
    //   28: getfield mTop : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   31: invokevirtual createObjectVariable : (Ljava/lang/Object;)Landroid/support/constraint/solver/SolverVariable;
    //   34: astore #19
    //   36: aload_1
    //   37: aload #18
    //   39: getfield mBottom : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   42: invokevirtual createObjectVariable : (Ljava/lang/Object;)Landroid/support/constraint/solver/SolverVariable;
    //   45: astore #16
    //   47: aload_1
    //   48: aload #18
    //   50: getfield mBaseline : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   53: invokevirtual createObjectVariable : (Ljava/lang/Object;)Landroid/support/constraint/solver/SolverVariable;
    //   56: astore #17
    //   58: aload #18
    //   60: getfield mParent : Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   63: ifnull -> 447
    //   66: aload #18
    //   68: getfield mParent : Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   71: ifnull -> 96
    //   74: aload #18
    //   76: getfield mParent : Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   79: getfield mListDimensionBehaviors : [Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   82: iconst_0
    //   83: aaload
    //   84: getstatic android/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour.WRAP_CONTENT : Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   87: if_acmpne -> 96
    //   90: iconst_1
    //   91: istore #6
    //   93: goto -> 99
    //   96: iconst_0
    //   97: istore #6
    //   99: aload #18
    //   101: getfield mParent : Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   104: ifnull -> 129
    //   107: aload #18
    //   109: getfield mParent : Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   112: getfield mListDimensionBehaviors : [Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   115: iconst_1
    //   116: aaload
    //   117: getstatic android/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour.WRAP_CONTENT : Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   120: if_acmpne -> 129
    //   123: iconst_1
    //   124: istore #7
    //   126: goto -> 132
    //   129: iconst_0
    //   130: istore #7
    //   132: aload #18
    //   134: getfield mLeft : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   137: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   140: ifnull -> 162
    //   143: aload #18
    //   145: getfield mLeft : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   148: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   151: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   154: aload #18
    //   156: getfield mLeft : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   159: if_acmpeq -> 192
    //   162: aload #18
    //   164: getfield mRight : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   167: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   170: ifnull -> 212
    //   173: aload #18
    //   175: getfield mRight : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   178: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   181: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   184: aload #18
    //   186: getfield mRight : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   189: if_acmpne -> 212
    //   192: aload #18
    //   194: getfield mParent : Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   197: checkcast android/support/constraint/solver/widgets/ConstraintWidgetContainer
    //   200: aload #18
    //   202: iconst_0
    //   203: invokevirtual addChain : (Landroid/support/constraint/solver/widgets/ConstraintWidget;I)V
    //   206: iconst_1
    //   207: istore #8
    //   209: goto -> 215
    //   212: iconst_0
    //   213: istore #8
    //   215: aload #18
    //   217: getfield mTop : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   220: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   223: ifnull -> 245
    //   226: aload #18
    //   228: getfield mTop : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   231: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   234: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   237: aload #18
    //   239: getfield mTop : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   242: if_acmpeq -> 275
    //   245: aload #18
    //   247: getfield mBottom : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   250: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   253: ifnull -> 295
    //   256: aload #18
    //   258: getfield mBottom : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   261: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   264: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   267: aload #18
    //   269: getfield mBottom : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   272: if_acmpne -> 295
    //   275: aload #18
    //   277: getfield mParent : Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   280: checkcast android/support/constraint/solver/widgets/ConstraintWidgetContainer
    //   283: aload #18
    //   285: iconst_1
    //   286: invokevirtual addChain : (Landroid/support/constraint/solver/widgets/ConstraintWidget;I)V
    //   289: iconst_1
    //   290: istore #9
    //   292: goto -> 298
    //   295: iconst_0
    //   296: istore #9
    //   298: iload #6
    //   300: ifeq -> 355
    //   303: aload #18
    //   305: getfield mVisibility : I
    //   308: bipush #8
    //   310: if_icmpeq -> 355
    //   313: aload #18
    //   315: getfield mLeft : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   318: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   321: ifnonnull -> 355
    //   324: aload #18
    //   326: getfield mRight : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   329: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   332: ifnonnull -> 355
    //   335: aload_1
    //   336: aload_1
    //   337: aload #18
    //   339: getfield mParent : Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   342: getfield mRight : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   345: invokevirtual createObjectVariable : (Ljava/lang/Object;)Landroid/support/constraint/solver/SolverVariable;
    //   348: aload #15
    //   350: iconst_0
    //   351: iconst_1
    //   352: invokevirtual addGreaterThan : (Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;II)V
    //   355: iload #7
    //   357: ifeq -> 420
    //   360: aload #18
    //   362: getfield mVisibility : I
    //   365: bipush #8
    //   367: if_icmpeq -> 420
    //   370: aload #18
    //   372: getfield mTop : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   375: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   378: ifnonnull -> 420
    //   381: aload #18
    //   383: getfield mBottom : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   386: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   389: ifnonnull -> 420
    //   392: aload #18
    //   394: getfield mBaseline : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   397: ifnonnull -> 420
    //   400: aload_1
    //   401: aload_1
    //   402: aload #18
    //   404: getfield mParent : Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   407: getfield mBottom : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   410: invokevirtual createObjectVariable : (Ljava/lang/Object;)Landroid/support/constraint/solver/SolverVariable;
    //   413: aload #16
    //   415: iconst_0
    //   416: iconst_1
    //   417: invokevirtual addGreaterThan : (Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;II)V
    //   420: iload #8
    //   422: istore #10
    //   424: iload #9
    //   426: istore #11
    //   428: iload #6
    //   430: istore #8
    //   432: iload #7
    //   434: istore #6
    //   436: iload #10
    //   438: istore #9
    //   440: iload #11
    //   442: istore #7
    //   444: goto -> 459
    //   447: iconst_0
    //   448: istore #8
    //   450: iconst_0
    //   451: istore #6
    //   453: iconst_0
    //   454: istore #9
    //   456: iconst_0
    //   457: istore #7
    //   459: aload #18
    //   461: getfield mWidth : I
    //   464: istore_3
    //   465: iload_3
    //   466: istore_2
    //   467: iload_3
    //   468: aload #18
    //   470: getfield mMinWidth : I
    //   473: if_icmpge -> 482
    //   476: aload #18
    //   478: getfield mMinWidth : I
    //   481: istore_2
    //   482: aload #18
    //   484: getfield mHeight : I
    //   487: istore #4
    //   489: iload #4
    //   491: istore_3
    //   492: iload #4
    //   494: aload #18
    //   496: getfield mMinHeight : I
    //   499: if_icmpge -> 508
    //   502: aload #18
    //   504: getfield mMinHeight : I
    //   507: istore_3
    //   508: aload #18
    //   510: getfield mListDimensionBehaviors : [Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   513: iconst_0
    //   514: aaload
    //   515: getstatic android/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour.MATCH_CONSTRAINT : Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   518: if_acmpeq -> 527
    //   521: iconst_1
    //   522: istore #10
    //   524: goto -> 530
    //   527: iconst_0
    //   528: istore #10
    //   530: aload #18
    //   532: getfield mListDimensionBehaviors : [Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   535: iconst_1
    //   536: aaload
    //   537: getstatic android/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour.MATCH_CONSTRAINT : Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   540: if_acmpeq -> 549
    //   543: iconst_1
    //   544: istore #11
    //   546: goto -> 552
    //   549: iconst_0
    //   550: istore #11
    //   552: aload #18
    //   554: aload #18
    //   556: getfield mDimensionRatioSide : I
    //   559: putfield mResolvedDimensionRatioSide : I
    //   562: aload #18
    //   564: aload #18
    //   566: getfield mDimensionRatio : F
    //   569: putfield mResolvedDimensionRatio : F
    //   572: aload #18
    //   574: getfield mDimensionRatio : F
    //   577: fconst_0
    //   578: fcmpl
    //   579: ifle -> 754
    //   582: aload #18
    //   584: getfield mVisibility : I
    //   587: bipush #8
    //   589: if_icmpeq -> 754
    //   592: aload #18
    //   594: getfield mListDimensionBehaviors : [Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   597: iconst_0
    //   598: aaload
    //   599: getstatic android/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour.MATCH_CONSTRAINT : Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   602: if_acmpne -> 637
    //   605: aload #18
    //   607: getfield mListDimensionBehaviors : [Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   610: iconst_1
    //   611: aaload
    //   612: getstatic android/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour.MATCH_CONSTRAINT : Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   615: if_acmpne -> 637
    //   618: aload #18
    //   620: iload #8
    //   622: iload #6
    //   624: iload #10
    //   626: iload #11
    //   628: invokevirtual setupDimensionRatio : (ZZZZ)V
    //   631: iload_2
    //   632: istore #4
    //   634: goto -> 740
    //   637: aload #18
    //   639: getfield mListDimensionBehaviors : [Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   642: iconst_0
    //   643: aaload
    //   644: getstatic android/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour.MATCH_CONSTRAINT : Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   647: if_acmpne -> 674
    //   650: aload #18
    //   652: iconst_0
    //   653: putfield mResolvedDimensionRatioSide : I
    //   656: aload #18
    //   658: getfield mResolvedDimensionRatio : F
    //   661: aload #18
    //   663: getfield mHeight : I
    //   666: i2f
    //   667: fmul
    //   668: f2i
    //   669: istore #4
    //   671: goto -> 740
    //   674: iload_2
    //   675: istore #4
    //   677: aload #18
    //   679: getfield mListDimensionBehaviors : [Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   682: iconst_1
    //   683: aaload
    //   684: getstatic android/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour.MATCH_CONSTRAINT : Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   687: if_acmpne -> 740
    //   690: aload #18
    //   692: iconst_1
    //   693: putfield mResolvedDimensionRatioSide : I
    //   696: aload #18
    //   698: getfield mDimensionRatioSide : I
    //   701: iconst_m1
    //   702: if_icmpne -> 717
    //   705: aload #18
    //   707: fconst_1
    //   708: aload #18
    //   710: getfield mResolvedDimensionRatio : F
    //   713: fdiv
    //   714: putfield mResolvedDimensionRatio : F
    //   717: aload #18
    //   719: getfield mResolvedDimensionRatio : F
    //   722: aload #18
    //   724: getfield mWidth : I
    //   727: i2f
    //   728: fmul
    //   729: f2i
    //   730: istore #4
    //   732: iload_2
    //   733: istore_3
    //   734: iload #4
    //   736: istore_2
    //   737: goto -> 745
    //   740: iload_3
    //   741: istore_2
    //   742: iload #4
    //   744: istore_3
    //   745: iconst_1
    //   746: istore #4
    //   748: iload_2
    //   749: istore #5
    //   751: goto -> 762
    //   754: iconst_0
    //   755: istore #4
    //   757: iload_3
    //   758: istore #5
    //   760: iload_2
    //   761: istore_3
    //   762: iload #4
    //   764: ifeq -> 790
    //   767: aload #18
    //   769: getfield mResolvedDimensionRatioSide : I
    //   772: ifeq -> 784
    //   775: aload #18
    //   777: getfield mResolvedDimensionRatioSide : I
    //   780: iconst_m1
    //   781: if_icmpne -> 790
    //   784: iconst_1
    //   785: istore #10
    //   787: goto -> 793
    //   790: iconst_0
    //   791: istore #10
    //   793: aload #18
    //   795: getfield mListDimensionBehaviors : [Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   798: iconst_0
    //   799: aaload
    //   800: getstatic android/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour.WRAP_CONTENT : Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   803: if_acmpne -> 820
    //   806: aload #18
    //   808: instanceof android/support/constraint/solver/widgets/ConstraintWidgetContainer
    //   811: ifeq -> 820
    //   814: iconst_1
    //   815: istore #11
    //   817: goto -> 823
    //   820: iconst_0
    //   821: istore #11
    //   823: aload #18
    //   825: getfield mCenter : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   828: invokevirtual isConnected : ()Z
    //   831: iconst_1
    //   832: ixor
    //   833: istore #12
    //   835: aload #18
    //   837: getfield mHorizontalResolution : I
    //   840: iconst_2
    //   841: if_icmpeq -> 983
    //   844: aload #18
    //   846: getfield mParent : Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   849: ifnull -> 869
    //   852: aload_1
    //   853: aload #18
    //   855: getfield mParent : Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   858: getfield mRight : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   861: invokevirtual createObjectVariable : (Ljava/lang/Object;)Landroid/support/constraint/solver/SolverVariable;
    //   864: astore #13
    //   866: goto -> 872
    //   869: aconst_null
    //   870: astore #13
    //   872: aload #18
    //   874: getfield mParent : Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   877: ifnull -> 897
    //   880: aload_1
    //   881: aload #18
    //   883: getfield mParent : Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   886: getfield mLeft : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   889: invokevirtual createObjectVariable : (Ljava/lang/Object;)Landroid/support/constraint/solver/SolverVariable;
    //   892: astore #14
    //   894: goto -> 900
    //   897: aconst_null
    //   898: astore #14
    //   900: aload #18
    //   902: aload_1
    //   903: iload #8
    //   905: aload #14
    //   907: aload #13
    //   909: aload #18
    //   911: getfield mListDimensionBehaviors : [Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   914: iconst_0
    //   915: aaload
    //   916: iload #11
    //   918: aload #18
    //   920: getfield mLeft : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   923: aload #18
    //   925: getfield mRight : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   928: aload #18
    //   930: getfield mX : I
    //   933: iload_3
    //   934: aload #18
    //   936: getfield mMinWidth : I
    //   939: aload #18
    //   941: getfield mMaxDimension : [I
    //   944: iconst_0
    //   945: iaload
    //   946: aload #18
    //   948: getfield mHorizontalBiasPercent : F
    //   951: iload #10
    //   953: iload #9
    //   955: aload #18
    //   957: getfield mMatchConstraintDefaultWidth : I
    //   960: aload #18
    //   962: getfield mMatchConstraintMinWidth : I
    //   965: aload #18
    //   967: getfield mMatchConstraintMaxWidth : I
    //   970: aload #18
    //   972: getfield mMatchConstraintPercentWidth : F
    //   975: iload #12
    //   977: invokespecial applyConstraints : (Landroid/support/constraint/solver/LinearSystem;ZLandroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;ZLandroid/support/constraint/solver/widgets/ConstraintAnchor;Landroid/support/constraint/solver/widgets/ConstraintAnchor;IIIIFZZIIIFZ)V
    //   980: goto -> 983
    //   983: aload #19
    //   985: astore #13
    //   987: aload #17
    //   989: astore #14
    //   991: aload_0
    //   992: astore #17
    //   994: aload #17
    //   996: getfield mVerticalResolution : I
    //   999: iconst_2
    //   1000: if_icmpne -> 1004
    //   1003: return
    //   1004: aload #17
    //   1006: getfield mListDimensionBehaviors : [Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   1009: iconst_1
    //   1010: aaload
    //   1011: getstatic android/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour.WRAP_CONTENT : Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   1014: if_acmpne -> 1031
    //   1017: aload #17
    //   1019: instanceof android/support/constraint/solver/widgets/ConstraintWidgetContainer
    //   1022: ifeq -> 1031
    //   1025: iconst_1
    //   1026: istore #8
    //   1028: goto -> 1034
    //   1031: iconst_0
    //   1032: istore #8
    //   1034: iload #4
    //   1036: ifeq -> 1063
    //   1039: aload #17
    //   1041: getfield mResolvedDimensionRatioSide : I
    //   1044: iconst_1
    //   1045: if_icmpeq -> 1057
    //   1048: aload #17
    //   1050: getfield mResolvedDimensionRatioSide : I
    //   1053: iconst_m1
    //   1054: if_icmpne -> 1063
    //   1057: iconst_1
    //   1058: istore #9
    //   1060: goto -> 1066
    //   1063: iconst_0
    //   1064: istore #9
    //   1066: aload #17
    //   1068: getfield mBaselineDistance : I
    //   1071: ifle -> 1164
    //   1074: aload #17
    //   1076: getfield mBaseline : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1079: invokevirtual getResolutionNode : ()Landroid/support/constraint/solver/widgets/ResolutionAnchor;
    //   1082: getfield state : I
    //   1085: iconst_1
    //   1086: if_icmpne -> 1104
    //   1089: aload #17
    //   1091: getfield mBaseline : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1094: invokevirtual getResolutionNode : ()Landroid/support/constraint/solver/widgets/ResolutionAnchor;
    //   1097: aload_1
    //   1098: invokevirtual addResolvedValue : (Landroid/support/constraint/solver/LinearSystem;)V
    //   1101: goto -> 1164
    //   1104: aload_1
    //   1105: astore #18
    //   1107: aload #18
    //   1109: aload #14
    //   1111: aload #13
    //   1113: aload_0
    //   1114: invokevirtual getBaselineDistance : ()I
    //   1117: bipush #6
    //   1119: invokevirtual addEquality : (Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;II)Landroid/support/constraint/solver/ArrayRow;
    //   1122: pop
    //   1123: aload #17
    //   1125: getfield mBaseline : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1128: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1131: ifnull -> 1164
    //   1134: aload #18
    //   1136: aload #14
    //   1138: aload #18
    //   1140: aload #17
    //   1142: getfield mBaseline : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1145: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1148: invokevirtual createObjectVariable : (Ljava/lang/Object;)Landroid/support/constraint/solver/SolverVariable;
    //   1151: iconst_0
    //   1152: bipush #6
    //   1154: invokevirtual addEquality : (Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;II)Landroid/support/constraint/solver/ArrayRow;
    //   1157: pop
    //   1158: iconst_0
    //   1159: istore #10
    //   1161: goto -> 1168
    //   1164: iload #12
    //   1166: istore #10
    //   1168: aload_1
    //   1169: astore #19
    //   1171: aload #13
    //   1173: astore #18
    //   1175: aload #17
    //   1177: getfield mParent : Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   1180: ifnull -> 1201
    //   1183: aload #19
    //   1185: aload #17
    //   1187: getfield mParent : Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   1190: getfield mBottom : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1193: invokevirtual createObjectVariable : (Ljava/lang/Object;)Landroid/support/constraint/solver/SolverVariable;
    //   1196: astore #13
    //   1198: goto -> 1204
    //   1201: aconst_null
    //   1202: astore #13
    //   1204: aload #17
    //   1206: getfield mParent : Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   1209: ifnull -> 1230
    //   1212: aload #19
    //   1214: aload #17
    //   1216: getfield mParent : Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   1219: getfield mTop : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1222: invokevirtual createObjectVariable : (Ljava/lang/Object;)Landroid/support/constraint/solver/SolverVariable;
    //   1225: astore #14
    //   1227: goto -> 1233
    //   1230: aconst_null
    //   1231: astore #14
    //   1233: aload #17
    //   1235: aload #19
    //   1237: iload #6
    //   1239: aload #14
    //   1241: aload #13
    //   1243: aload #17
    //   1245: getfield mListDimensionBehaviors : [Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   1248: iconst_1
    //   1249: aaload
    //   1250: iload #8
    //   1252: aload #17
    //   1254: getfield mTop : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1257: aload #17
    //   1259: getfield mBottom : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1262: aload #17
    //   1264: getfield mY : I
    //   1267: iload #5
    //   1269: aload #17
    //   1271: getfield mMinHeight : I
    //   1274: aload #17
    //   1276: getfield mMaxDimension : [I
    //   1279: iconst_1
    //   1280: iaload
    //   1281: aload #17
    //   1283: getfield mVerticalBiasPercent : F
    //   1286: iload #9
    //   1288: iload #7
    //   1290: aload #17
    //   1292: getfield mMatchConstraintDefaultHeight : I
    //   1295: aload #17
    //   1297: getfield mMatchConstraintMinHeight : I
    //   1300: aload #17
    //   1302: getfield mMatchConstraintMaxHeight : I
    //   1305: aload #17
    //   1307: getfield mMatchConstraintPercentHeight : F
    //   1310: iload #10
    //   1312: invokespecial applyConstraints : (Landroid/support/constraint/solver/LinearSystem;ZLandroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;ZLandroid/support/constraint/solver/widgets/ConstraintAnchor;Landroid/support/constraint/solver/widgets/ConstraintAnchor;IIIIFZZIIIFZ)V
    //   1315: iload #4
    //   1317: ifeq -> 1376
    //   1320: aload_0
    //   1321: astore #13
    //   1323: aload #13
    //   1325: getfield mResolvedDimensionRatioSide : I
    //   1328: iconst_1
    //   1329: if_icmpne -> 1354
    //   1332: aload_1
    //   1333: aload #16
    //   1335: aload #18
    //   1337: aload #15
    //   1339: aload #20
    //   1341: aload #13
    //   1343: getfield mResolvedDimensionRatio : F
    //   1346: bipush #6
    //   1348: invokevirtual addRatio : (Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;FI)V
    //   1351: goto -> 1376
    //   1354: aload_1
    //   1355: aload #15
    //   1357: aload #20
    //   1359: aload #16
    //   1361: aload #18
    //   1363: aload #13
    //   1365: getfield mResolvedDimensionRatio : F
    //   1368: bipush #6
    //   1370: invokevirtual addRatio : (Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;FI)V
    //   1373: goto -> 1376
    //   1376: aload_0
    //   1377: astore #13
    //   1379: aload #13
    //   1381: getfield mCenter : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1384: invokevirtual isConnected : ()Z
    //   1387: ifeq -> 1429
    //   1390: aload_1
    //   1391: aload #13
    //   1393: aload #13
    //   1395: getfield mCenter : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1398: invokevirtual getTarget : ()Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1401: invokevirtual getOwner : ()Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   1404: aload #13
    //   1406: getfield mCircleConstraintAngle : F
    //   1409: ldc_w 90.0
    //   1412: fadd
    //   1413: f2d
    //   1414: invokestatic toRadians : (D)D
    //   1417: d2f
    //   1418: aload #13
    //   1420: getfield mCenter : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1423: invokevirtual getMargin : ()I
    //   1426: invokevirtual addCenterPoint : (Landroid/support/constraint/solver/widgets/ConstraintWidget;Landroid/support/constraint/solver/widgets/ConstraintWidget;FI)V
    //   1429: return }
  
  public boolean allowedInBarrier() { return (this.mVisibility != 8); }
  
  public void analyze(int paramInt) { Optimizer.analyze(paramInt, this); }
  
  public void connect(ConstraintAnchor.Type paramType1, ConstraintWidget paramConstraintWidget, ConstraintAnchor.Type paramType2) { connect(paramType1, paramConstraintWidget, paramType2, 0, ConstraintAnchor.Strength.STRONG); }
  
  public void connect(ConstraintAnchor.Type paramType1, ConstraintWidget paramConstraintWidget, ConstraintAnchor.Type paramType2, int paramInt) { connect(paramType1, paramConstraintWidget, paramType2, paramInt, ConstraintAnchor.Strength.STRONG); }
  
  public void connect(ConstraintAnchor.Type paramType1, ConstraintWidget paramConstraintWidget, ConstraintAnchor.Type paramType2, int paramInt, ConstraintAnchor.Strength paramStrength) { connect(paramType1, paramConstraintWidget, paramType2, paramInt, paramStrength, 0); }
  
  public void connect(ConstraintAnchor.Type paramType1, ConstraintWidget paramConstraintWidget, ConstraintAnchor.Type paramType2, int paramInt1, ConstraintAnchor.Strength paramStrength, int paramInt2) {
    ConstraintAnchor constraintAnchor2;
    ConstraintAnchor constraintAnchor1;
    ConstraintAnchor.Type type = ConstraintAnchor.Type.CENTER;
    int i = 0;
    if (paramType1 == type) {
      if (paramType2 == ConstraintAnchor.Type.CENTER) {
        constraintAnchor1 = getAnchor(ConstraintAnchor.Type.LEFT);
        constraintAnchor2 = getAnchor(ConstraintAnchor.Type.RIGHT);
        ConstraintAnchor constraintAnchor3 = getAnchor(ConstraintAnchor.Type.TOP);
        ConstraintAnchor constraintAnchor4 = getAnchor(ConstraintAnchor.Type.BOTTOM);
        i = 1;
        if ((constraintAnchor1 != null && constraintAnchor1.isConnected()) || (constraintAnchor2 != null && constraintAnchor2.isConnected())) {
          paramInt1 = 0;
        } else {
          connect(ConstraintAnchor.Type.LEFT, paramConstraintWidget, ConstraintAnchor.Type.LEFT, 0, paramStrength, paramInt2);
          connect(ConstraintAnchor.Type.RIGHT, paramConstraintWidget, ConstraintAnchor.Type.RIGHT, 0, paramStrength, paramInt2);
          paramInt1 = 1;
        } 
        if ((constraintAnchor3 != null && constraintAnchor3.isConnected()) || (constraintAnchor4 != null && constraintAnchor4.isConnected())) {
          i = 0;
        } else {
          connect(ConstraintAnchor.Type.TOP, paramConstraintWidget, ConstraintAnchor.Type.TOP, 0, paramStrength, paramInt2);
          connect(ConstraintAnchor.Type.BOTTOM, paramConstraintWidget, ConstraintAnchor.Type.BOTTOM, 0, paramStrength, paramInt2);
        } 
        if (paramInt1 != 0 && i) {
          getAnchor(ConstraintAnchor.Type.CENTER).connect(paramConstraintWidget.getAnchor(ConstraintAnchor.Type.CENTER), 0, paramInt2);
          return;
        } 
        if (paramInt1 != 0) {
          getAnchor(ConstraintAnchor.Type.CENTER_X).connect(paramConstraintWidget.getAnchor(ConstraintAnchor.Type.CENTER_X), 0, paramInt2);
          return;
        } 
        if (i) {
          getAnchor(ConstraintAnchor.Type.CENTER_Y).connect(paramConstraintWidget.getAnchor(ConstraintAnchor.Type.CENTER_Y), 0, paramInt2);
          return;
        } 
      } else {
        if (constraintAnchor2 == ConstraintAnchor.Type.LEFT || constraintAnchor2 == ConstraintAnchor.Type.RIGHT) {
          connect(ConstraintAnchor.Type.LEFT, paramConstraintWidget, constraintAnchor2, 0, paramStrength, paramInt2);
          connect(ConstraintAnchor.Type.RIGHT, paramConstraintWidget, constraintAnchor2, 0, paramStrength, paramInt2);
          getAnchor(ConstraintAnchor.Type.CENTER).connect(paramConstraintWidget.getAnchor(constraintAnchor2), 0, paramInt2);
          return;
        } 
        if (constraintAnchor2 == ConstraintAnchor.Type.TOP || constraintAnchor2 == ConstraintAnchor.Type.BOTTOM) {
          connect(ConstraintAnchor.Type.TOP, paramConstraintWidget, constraintAnchor2, 0, paramStrength, paramInt2);
          connect(ConstraintAnchor.Type.BOTTOM, paramConstraintWidget, constraintAnchor2, 0, paramStrength, paramInt2);
          getAnchor(ConstraintAnchor.Type.CENTER).connect(paramConstraintWidget.getAnchor(constraintAnchor2), 0, paramInt2);
          return;
        } 
      } 
    } else {
      if (constraintAnchor1 == ConstraintAnchor.Type.CENTER_X && (constraintAnchor2 == ConstraintAnchor.Type.LEFT || constraintAnchor2 == ConstraintAnchor.Type.RIGHT)) {
        constraintAnchor1 = getAnchor(ConstraintAnchor.Type.LEFT);
        constraintAnchor3 = paramConstraintWidget.getAnchor(constraintAnchor2);
        constraintAnchor2 = getAnchor(ConstraintAnchor.Type.RIGHT);
        constraintAnchor1.connect(constraintAnchor3, 0, paramInt2);
        constraintAnchor2.connect(constraintAnchor3, 0, paramInt2);
        getAnchor(ConstraintAnchor.Type.CENTER_X).connect(constraintAnchor3, 0, paramInt2);
        return;
      } 
      if (constraintAnchor1 == ConstraintAnchor.Type.CENTER_Y && (constraintAnchor2 == ConstraintAnchor.Type.TOP || constraintAnchor2 == ConstraintAnchor.Type.BOTTOM)) {
        constraintAnchor1 = constraintAnchor3.getAnchor(constraintAnchor2);
        getAnchor(ConstraintAnchor.Type.TOP).connect(constraintAnchor1, 0, paramInt2);
        getAnchor(ConstraintAnchor.Type.BOTTOM).connect(constraintAnchor1, 0, paramInt2);
        getAnchor(ConstraintAnchor.Type.CENTER_Y).connect(constraintAnchor1, 0, paramInt2);
        return;
      } 
      if (constraintAnchor1 == ConstraintAnchor.Type.CENTER_X && constraintAnchor2 == ConstraintAnchor.Type.CENTER_X) {
        getAnchor(ConstraintAnchor.Type.LEFT).connect(constraintAnchor3.getAnchor(ConstraintAnchor.Type.LEFT), 0, paramInt2);
        getAnchor(ConstraintAnchor.Type.RIGHT).connect(constraintAnchor3.getAnchor(ConstraintAnchor.Type.RIGHT), 0, paramInt2);
        getAnchor(ConstraintAnchor.Type.CENTER_X).connect(constraintAnchor3.getAnchor(constraintAnchor2), 0, paramInt2);
        return;
      } 
      if (constraintAnchor1 == ConstraintAnchor.Type.CENTER_Y && constraintAnchor2 == ConstraintAnchor.Type.CENTER_Y) {
        getAnchor(ConstraintAnchor.Type.TOP).connect(constraintAnchor3.getAnchor(ConstraintAnchor.Type.TOP), 0, paramInt2);
        getAnchor(ConstraintAnchor.Type.BOTTOM).connect(constraintAnchor3.getAnchor(ConstraintAnchor.Type.BOTTOM), 0, paramInt2);
        getAnchor(ConstraintAnchor.Type.CENTER_Y).connect(constraintAnchor3.getAnchor(constraintAnchor2), 0, paramInt2);
        return;
      } 
      ConstraintAnchor constraintAnchor4 = getAnchor(constraintAnchor1);
      ConstraintAnchor constraintAnchor3 = constraintAnchor3.getAnchor(constraintAnchor2);
      if (constraintAnchor4.isValidConnection(constraintAnchor3)) {
        if (constraintAnchor1 == ConstraintAnchor.Type.BASELINE) {
          constraintAnchor1 = getAnchor(ConstraintAnchor.Type.TOP);
          constraintAnchor2 = getAnchor(ConstraintAnchor.Type.BOTTOM);
          if (constraintAnchor1 != null)
            constraintAnchor1.reset(); 
          paramInt1 = i;
          if (constraintAnchor2 != null) {
            constraintAnchor2.reset();
            paramInt1 = i;
          } 
        } else if (constraintAnchor1 == ConstraintAnchor.Type.TOP || constraintAnchor1 == ConstraintAnchor.Type.BOTTOM) {
          constraintAnchor2 = getAnchor(ConstraintAnchor.Type.BASELINE);
          if (constraintAnchor2 != null)
            constraintAnchor2.reset(); 
          constraintAnchor2 = getAnchor(ConstraintAnchor.Type.CENTER);
          if (constraintAnchor2.getTarget() != constraintAnchor3)
            constraintAnchor2.reset(); 
          constraintAnchor1 = getAnchor(constraintAnchor1).getOpposite();
          constraintAnchor2 = getAnchor(ConstraintAnchor.Type.CENTER_Y);
          if (constraintAnchor2.isConnected()) {
            constraintAnchor1.reset();
            constraintAnchor2.reset();
          } 
        } else if (constraintAnchor1 == ConstraintAnchor.Type.LEFT || constraintAnchor1 == ConstraintAnchor.Type.RIGHT) {
          constraintAnchor2 = getAnchor(ConstraintAnchor.Type.CENTER);
          if (constraintAnchor2.getTarget() != constraintAnchor3)
            constraintAnchor2.reset(); 
          constraintAnchor1 = getAnchor(constraintAnchor1).getOpposite();
          constraintAnchor2 = getAnchor(ConstraintAnchor.Type.CENTER_X);
          if (constraintAnchor2.isConnected()) {
            constraintAnchor1.reset();
            constraintAnchor2.reset();
          } 
        } 
        constraintAnchor4.connect(constraintAnchor3, paramInt1, paramStrength, paramInt2);
        constraintAnchor3.getOwner().connectedTo(constraintAnchor4.getOwner());
      } 
    } 
  }
  
  public void connect(ConstraintAnchor paramConstraintAnchor1, ConstraintAnchor paramConstraintAnchor2, int paramInt) { connect(paramConstraintAnchor1, paramConstraintAnchor2, paramInt, ConstraintAnchor.Strength.STRONG, 0); }
  
  public void connect(ConstraintAnchor paramConstraintAnchor1, ConstraintAnchor paramConstraintAnchor2, int paramInt1, int paramInt2) { connect(paramConstraintAnchor1, paramConstraintAnchor2, paramInt1, ConstraintAnchor.Strength.STRONG, paramInt2); }
  
  public void connect(ConstraintAnchor paramConstraintAnchor1, ConstraintAnchor paramConstraintAnchor2, int paramInt1, ConstraintAnchor.Strength paramStrength, int paramInt2) {
    if (paramConstraintAnchor1.getOwner() == this)
      connect(paramConstraintAnchor1.getType(), paramConstraintAnchor2.getOwner(), paramConstraintAnchor2.getType(), paramInt1, paramStrength, paramInt2); 
  }
  
  public void connectCircularConstraint(ConstraintWidget paramConstraintWidget, float paramFloat, int paramInt) {
    immediateConnect(ConstraintAnchor.Type.CENTER, paramConstraintWidget, ConstraintAnchor.Type.CENTER, paramInt, 0);
    this.mCircleConstraintAngle = paramFloat;
  }
  
  public void connectedTo(ConstraintWidget paramConstraintWidget) {}
  
  public void disconnectUnlockedWidget(ConstraintWidget paramConstraintWidget) {
    ArrayList arrayList = getAnchors();
    int i = arrayList.size();
    for (byte b = 0; b < i; b++) {
      ConstraintAnchor constraintAnchor = (ConstraintAnchor)arrayList.get(b);
      if (constraintAnchor.isConnected() && constraintAnchor.getTarget().getOwner() == paramConstraintWidget && constraintAnchor.getConnectionCreator() == 2)
        constraintAnchor.reset(); 
    } 
  }
  
  public void disconnectWidget(ConstraintWidget paramConstraintWidget) {
    ArrayList arrayList = getAnchors();
    int i = arrayList.size();
    for (byte b = 0; b < i; b++) {
      ConstraintAnchor constraintAnchor = (ConstraintAnchor)arrayList.get(b);
      if (constraintAnchor.isConnected() && constraintAnchor.getTarget().getOwner() == paramConstraintWidget)
        constraintAnchor.reset(); 
    } 
  }
  
  public void forceUpdateDrawPosition() {
    int i = this.mX;
    int j = this.mY;
    int k = this.mX;
    int m = this.mWidth;
    int n = this.mY;
    int i1 = this.mHeight;
    this.mDrawX = i;
    this.mDrawY = j;
    this.mDrawWidth = k + m - i;
    this.mDrawHeight = n + i1 - j;
  }
  
  public ConstraintAnchor getAnchor(ConstraintAnchor.Type paramType) {
    switch (paramType) {
      default:
        throw new AssertionError(paramType.name());
      case null:
        return null;
      case null:
        return this.mCenterY;
      case null:
        return this.mCenterX;
      case null:
        return this.mCenter;
      case null:
        return this.mBaseline;
      case MATCH_CONSTRAINT:
        return this.mBottom;
      case MATCH_PARENT:
        return this.mRight;
      case WRAP_CONTENT:
        return this.mTop;
      case FIXED:
        break;
    } 
    return this.mLeft;
  }
  
  public ArrayList<ConstraintAnchor> getAnchors() { return this.mAnchors; }
  
  public int getBaselineDistance() { return this.mBaselineDistance; }
  
  public int getBottom() { return getY() + this.mHeight; }
  
  public Object getCompanionWidget() { return this.mCompanionWidget; }
  
  public int getContainerItemSkip() { return this.mContainerItemSkip; }
  
  public String getDebugName() { return this.mDebugName; }
  
  public float getDimensionRatio() { return this.mDimensionRatio; }
  
  public int getDimensionRatioSide() { return this.mDimensionRatioSide; }
  
  public int getDrawBottom() { return getDrawY() + this.mDrawHeight; }
  
  public int getDrawHeight() { return this.mDrawHeight; }
  
  public int getDrawRight() { return getDrawX() + this.mDrawWidth; }
  
  public int getDrawWidth() { return this.mDrawWidth; }
  
  public int getDrawX() { return this.mDrawX + this.mOffsetX; }
  
  public int getDrawY() { return this.mDrawY + this.mOffsetY; }
  
  public int getHeight() { return (this.mVisibility == 8) ? 0 : this.mHeight; }
  
  public float getHorizontalBiasPercent() { return this.mHorizontalBiasPercent; }
  
  public ConstraintWidget getHorizontalChainControlWidget() {
    ConstraintWidget constraintWidget;
    if (isInHorizontalChain()) {
      ConstraintWidget constraintWidget1 = this;
      ConstraintWidget constraintWidget2 = null;
      while (true) {
        constraintWidget = constraintWidget2;
        if (constraintWidget2 == null) {
          constraintWidget = constraintWidget2;
          if (constraintWidget1 != null) {
            ConstraintAnchor constraintAnchor;
            ConstraintWidget constraintWidget3;
            constraintWidget = constraintWidget1.getAnchor(ConstraintAnchor.Type.LEFT);
            if (constraintWidget == null) {
              constraintWidget = null;
            } else {
              constraintWidget = constraintWidget.getTarget();
            } 
            if (constraintWidget == null) {
              constraintWidget = null;
            } else {
              constraintWidget3 = constraintWidget.getOwner();
            } 
            if (constraintWidget3 == getParent())
              return constraintWidget1; 
            if (constraintWidget3 == null) {
              constraintAnchor = null;
            } else {
              constraintAnchor = constraintWidget3.getAnchor(ConstraintAnchor.Type.RIGHT).getTarget();
            } 
            if (constraintAnchor != null && constraintAnchor.getOwner() != constraintWidget1) {
              constraintWidget2 = constraintWidget1;
              continue;
            } 
            constraintWidget1 = constraintWidget3;
            continue;
          } 
        } 
        break;
      } 
    } else {
      constraintWidget = null;
    } 
    return constraintWidget;
  }
  
  public int getHorizontalChainStyle() { return this.mHorizontalChainStyle; }
  
  public DimensionBehaviour getHorizontalDimensionBehaviour() { return this.mListDimensionBehaviors[0]; }
  
  public int getInternalDrawBottom() { return this.mDrawY + this.mDrawHeight; }
  
  public int getInternalDrawRight() { return this.mDrawX + this.mDrawWidth; }
  
  int getInternalDrawX() { return this.mDrawX; }
  
  int getInternalDrawY() { return this.mDrawY; }
  
  public int getLeft() { return getX(); }
  
  public int getMaxHeight() { return this.mMaxDimension[1]; }
  
  public int getMaxWidth() { return this.mMaxDimension[0]; }
  
  public int getMinHeight() { return this.mMinHeight; }
  
  public int getMinWidth() { return this.mMinWidth; }
  
  public int getOptimizerWrapHeight() {
    int i = this.mHeight;
    int j = i;
    if (this.mListDimensionBehaviors[true] == DimensionBehaviour.MATCH_CONSTRAINT) {
      if (this.mMatchConstraintDefaultHeight == 1) {
        i = Math.max(this.mMatchConstraintMinHeight, i);
      } else if (this.mMatchConstraintMinHeight > 0) {
        i = this.mMatchConstraintMinHeight;
        this.mHeight = i;
      } else {
        i = 0;
      } 
      j = i;
      if (this.mMatchConstraintMaxHeight > 0) {
        j = i;
        if (this.mMatchConstraintMaxHeight < i)
          j = this.mMatchConstraintMaxHeight; 
      } 
    } 
    return j;
  }
  
  public int getOptimizerWrapWidth() {
    int i = this.mWidth;
    int j = i;
    if (this.mListDimensionBehaviors[false] == DimensionBehaviour.MATCH_CONSTRAINT) {
      if (this.mMatchConstraintDefaultWidth == 1) {
        i = Math.max(this.mMatchConstraintMinWidth, i);
      } else if (this.mMatchConstraintMinWidth > 0) {
        i = this.mMatchConstraintMinWidth;
        this.mWidth = i;
      } else {
        i = 0;
      } 
      j = i;
      if (this.mMatchConstraintMaxWidth > 0) {
        j = i;
        if (this.mMatchConstraintMaxWidth < i)
          j = this.mMatchConstraintMaxWidth; 
      } 
    } 
    return j;
  }
  
  public ConstraintWidget getParent() { return this.mParent; }
  
  public ResolutionDimension getResolutionHeight() {
    if (this.mResolutionHeight == null)
      this.mResolutionHeight = new ResolutionDimension(); 
    return this.mResolutionHeight;
  }
  
  public ResolutionDimension getResolutionWidth() {
    if (this.mResolutionWidth == null)
      this.mResolutionWidth = new ResolutionDimension(); 
    return this.mResolutionWidth;
  }
  
  public int getRight() { return getX() + this.mWidth; }
  
  public WidgetContainer getRootWidgetContainer() {
    ConstraintWidget constraintWidget;
    for (constraintWidget = this; constraintWidget.getParent() != null; constraintWidget = constraintWidget.getParent());
    return (constraintWidget instanceof WidgetContainer) ? (WidgetContainer)constraintWidget : null;
  }
  
  protected int getRootX() { return this.mX + this.mOffsetX; }
  
  protected int getRootY() { return this.mY + this.mOffsetY; }
  
  public int getTop() { return getY(); }
  
  public String getType() { return this.mType; }
  
  public float getVerticalBiasPercent() { return this.mVerticalBiasPercent; }
  
  public ConstraintWidget getVerticalChainControlWidget() {
    ConstraintWidget constraintWidget;
    if (isInVerticalChain()) {
      ConstraintWidget constraintWidget1 = this;
      ConstraintWidget constraintWidget2 = null;
      while (true) {
        constraintWidget = constraintWidget2;
        if (constraintWidget2 == null) {
          constraintWidget = constraintWidget2;
          if (constraintWidget1 != null) {
            ConstraintAnchor constraintAnchor;
            ConstraintWidget constraintWidget3;
            constraintWidget = constraintWidget1.getAnchor(ConstraintAnchor.Type.TOP);
            if (constraintWidget == null) {
              constraintWidget = null;
            } else {
              constraintWidget = constraintWidget.getTarget();
            } 
            if (constraintWidget == null) {
              constraintWidget = null;
            } else {
              constraintWidget3 = constraintWidget.getOwner();
            } 
            if (constraintWidget3 == getParent())
              return constraintWidget1; 
            if (constraintWidget3 == null) {
              constraintAnchor = null;
            } else {
              constraintAnchor = constraintWidget3.getAnchor(ConstraintAnchor.Type.BOTTOM).getTarget();
            } 
            if (constraintAnchor != null && constraintAnchor.getOwner() != constraintWidget1) {
              constraintWidget2 = constraintWidget1;
              continue;
            } 
            constraintWidget1 = constraintWidget3;
            continue;
          } 
        } 
        break;
      } 
    } else {
      constraintWidget = null;
    } 
    return constraintWidget;
  }
  
  public int getVerticalChainStyle() { return this.mVerticalChainStyle; }
  
  public DimensionBehaviour getVerticalDimensionBehaviour() { return this.mListDimensionBehaviors[1]; }
  
  public int getVisibility() { return this.mVisibility; }
  
  public int getWidth() { return (this.mVisibility == 8) ? 0 : this.mWidth; }
  
  public int getWrapHeight() { return this.mWrapHeight; }
  
  public int getWrapWidth() { return this.mWrapWidth; }
  
  public int getX() { return this.mX; }
  
  public int getY() { return this.mY; }
  
  public boolean hasAncestor(ConstraintWidget paramConstraintWidget) {
    ConstraintWidget constraintWidget2 = getParent();
    if (constraintWidget2 == paramConstraintWidget)
      return true; 
    ConstraintWidget constraintWidget1 = constraintWidget2;
    if (constraintWidget2 == paramConstraintWidget.getParent())
      return false; 
    while (constraintWidget1 != null) {
      if (constraintWidget1 == paramConstraintWidget)
        return true; 
      if (constraintWidget1 == paramConstraintWidget.getParent())
        return true; 
      constraintWidget1 = constraintWidget1.getParent();
    } 
    return false;
  }
  
  public boolean hasBaseline() { return (this.mBaselineDistance > 0); }
  
  public void immediateConnect(ConstraintAnchor.Type paramType1, ConstraintWidget paramConstraintWidget, ConstraintAnchor.Type paramType2, int paramInt1, int paramInt2) { getAnchor(paramType1).connect(paramConstraintWidget.getAnchor(paramType2), paramInt1, paramInt2, ConstraintAnchor.Strength.STRONG, 0, true); }
  
  public boolean isFullyResolved() { return ((this.mLeft.getResolutionNode()).state == 1 && (this.mRight.getResolutionNode()).state == 1 && (this.mTop.getResolutionNode()).state == 1 && (this.mBottom.getResolutionNode()).state == 1); }
  
  public boolean isHeightWrapContent() { return this.mIsHeightWrapContent; }
  
  public boolean isInHorizontalChain() { return ((this.mLeft.mTarget != null && this.mLeft.mTarget.mTarget == this.mLeft) || (this.mRight.mTarget != null && this.mRight.mTarget.mTarget == this.mRight)); }
  
  public boolean isInVerticalChain() { return ((this.mTop.mTarget != null && this.mTop.mTarget.mTarget == this.mTop) || (this.mBottom.mTarget != null && this.mBottom.mTarget.mTarget == this.mBottom)); }
  
  public boolean isInsideConstraintLayout() {
    ConstraintWidget constraintWidget2 = getParent();
    ConstraintWidget constraintWidget1 = constraintWidget2;
    if (constraintWidget2 == null)
      return false; 
    while (constraintWidget1 != null) {
      if (constraintWidget1 instanceof ConstraintWidgetContainer)
        return true; 
      constraintWidget1 = constraintWidget1.getParent();
    } 
    return false;
  }
  
  public boolean isRoot() { return (this.mParent == null); }
  
  public boolean isRootContainer() { return (this instanceof ConstraintWidgetContainer && (this.mParent == null || !(this.mParent instanceof ConstraintWidgetContainer))); }
  
  public boolean isSpreadHeight() { return (this.mMatchConstraintDefaultHeight == 0 && this.mDimensionRatio == 0.0F && this.mMatchConstraintMinHeight == 0 && this.mMatchConstraintMaxHeight == 0 && this.mListDimensionBehaviors[true] == DimensionBehaviour.MATCH_CONSTRAINT); }
  
  public boolean isSpreadWidth() {
    int i = this.mMatchConstraintDefaultWidth;
    byte b = 0;
    int j = b;
    if (i == 0) {
      j = b;
      if (this.mDimensionRatio == 0.0F) {
        j = b;
        if (this.mMatchConstraintMinWidth == 0) {
          j = b;
          if (this.mMatchConstraintMaxWidth == 0) {
            j = b;
            if (this.mListDimensionBehaviors[false] == DimensionBehaviour.MATCH_CONSTRAINT)
              j = 1; 
          } 
        } 
      } 
    } 
    return j;
  }
  
  public boolean isWidthWrapContent() { return this.mIsWidthWrapContent; }
  
  public void reset() {
    this.mLeft.reset();
    this.mTop.reset();
    this.mRight.reset();
    this.mBottom.reset();
    this.mBaseline.reset();
    this.mCenterX.reset();
    this.mCenterY.reset();
    this.mCenter.reset();
    this.mParent = null;
    this.mCircleConstraintAngle = 0.0F;
    this.mWidth = 0;
    this.mHeight = 0;
    this.mDimensionRatio = 0.0F;
    this.mDimensionRatioSide = -1;
    this.mX = 0;
    this.mY = 0;
    this.mDrawX = 0;
    this.mDrawY = 0;
    this.mDrawWidth = 0;
    this.mDrawHeight = 0;
    this.mOffsetX = 0;
    this.mOffsetY = 0;
    this.mBaselineDistance = 0;
    this.mMinWidth = 0;
    this.mMinHeight = 0;
    this.mWrapWidth = 0;
    this.mWrapHeight = 0;
    this.mHorizontalBiasPercent = DEFAULT_BIAS;
    this.mVerticalBiasPercent = DEFAULT_BIAS;
    this.mListDimensionBehaviors[0] = DimensionBehaviour.FIXED;
    this.mListDimensionBehaviors[1] = DimensionBehaviour.FIXED;
    this.mCompanionWidget = null;
    this.mContainerItemSkip = 0;
    this.mVisibility = 0;
    this.mType = null;
    this.mHorizontalWrapVisited = false;
    this.mVerticalWrapVisited = false;
    this.mHorizontalChainStyle = 0;
    this.mVerticalChainStyle = 0;
    this.mHorizontalChainFixedPosition = false;
    this.mVerticalChainFixedPosition = false;
    this.mWeight[0] = 0.0F;
    this.mWeight[1] = 0.0F;
    this.mHorizontalResolution = -1;
    this.mVerticalResolution = -1;
    this.mMaxDimension[0] = Integer.MAX_VALUE;
    this.mMaxDimension[1] = Integer.MAX_VALUE;
    this.mMatchConstraintDefaultWidth = 0;
    this.mMatchConstraintDefaultHeight = 0;
    this.mMatchConstraintPercentWidth = 1.0F;
    this.mMatchConstraintPercentHeight = 1.0F;
    this.mMatchConstraintMaxWidth = Integer.MAX_VALUE;
    this.mMatchConstraintMaxHeight = Integer.MAX_VALUE;
    this.mMatchConstraintMinWidth = 0;
    this.mMatchConstraintMinHeight = 0;
    this.mResolvedDimensionRatioSide = -1;
    this.mResolvedDimensionRatio = 1.0F;
    if (this.mResolutionWidth != null)
      this.mResolutionWidth.reset(); 
    if (this.mResolutionHeight != null)
      this.mResolutionHeight.reset(); 
  }
  
  public void resetAllConstraints() {
    resetAnchors();
    setVerticalBiasPercent(DEFAULT_BIAS);
    setHorizontalBiasPercent(DEFAULT_BIAS);
    if (this instanceof ConstraintWidgetContainer)
      return; 
    if (getHorizontalDimensionBehaviour() == DimensionBehaviour.MATCH_CONSTRAINT)
      if (getWidth() == getWrapWidth()) {
        setHorizontalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);
      } else if (getWidth() > getMinWidth()) {
        setHorizontalDimensionBehaviour(DimensionBehaviour.FIXED);
      }  
    if (getVerticalDimensionBehaviour() == DimensionBehaviour.MATCH_CONSTRAINT) {
      if (getHeight() == getWrapHeight()) {
        setVerticalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);
        return;
      } 
      if (getHeight() > getMinHeight())
        setVerticalDimensionBehaviour(DimensionBehaviour.FIXED); 
    } 
  }
  
  public void resetAnchor(ConstraintAnchor paramConstraintAnchor) {
    if (getParent() != null && getParent() instanceof ConstraintWidgetContainer && ((ConstraintWidgetContainer)getParent()).handlesInternalConstraints())
      return; 
    ConstraintAnchor constraintAnchor1 = getAnchor(ConstraintAnchor.Type.LEFT);
    ConstraintAnchor constraintAnchor2 = getAnchor(ConstraintAnchor.Type.RIGHT);
    ConstraintAnchor constraintAnchor3 = getAnchor(ConstraintAnchor.Type.TOP);
    ConstraintAnchor constraintAnchor4 = getAnchor(ConstraintAnchor.Type.BOTTOM);
    ConstraintAnchor constraintAnchor5 = getAnchor(ConstraintAnchor.Type.CENTER);
    ConstraintAnchor constraintAnchor6 = getAnchor(ConstraintAnchor.Type.CENTER_X);
    ConstraintAnchor constraintAnchor7 = getAnchor(ConstraintAnchor.Type.CENTER_Y);
    if (paramConstraintAnchor == constraintAnchor5) {
      if (constraintAnchor1.isConnected() && constraintAnchor2.isConnected() && constraintAnchor1.getTarget() == constraintAnchor2.getTarget()) {
        constraintAnchor1.reset();
        constraintAnchor2.reset();
      } 
      if (constraintAnchor3.isConnected() && constraintAnchor4.isConnected() && constraintAnchor3.getTarget() == constraintAnchor4.getTarget()) {
        constraintAnchor3.reset();
        constraintAnchor4.reset();
      } 
      this.mHorizontalBiasPercent = 0.5F;
      this.mVerticalBiasPercent = 0.5F;
    } else if (paramConstraintAnchor == constraintAnchor6) {
      if (constraintAnchor1.isConnected() && constraintAnchor2.isConnected() && constraintAnchor1.getTarget().getOwner() == constraintAnchor2.getTarget().getOwner()) {
        constraintAnchor1.reset();
        constraintAnchor2.reset();
      } 
      this.mHorizontalBiasPercent = 0.5F;
    } else if (paramConstraintAnchor == constraintAnchor7) {
      if (constraintAnchor3.isConnected() && constraintAnchor4.isConnected() && constraintAnchor3.getTarget().getOwner() == constraintAnchor4.getTarget().getOwner()) {
        constraintAnchor3.reset();
        constraintAnchor4.reset();
      } 
      this.mVerticalBiasPercent = 0.5F;
    } else if (paramConstraintAnchor == constraintAnchor1 || paramConstraintAnchor == constraintAnchor2) {
      if (constraintAnchor1.isConnected() && constraintAnchor1.getTarget() == constraintAnchor2.getTarget())
        constraintAnchor5.reset(); 
    } else if ((paramConstraintAnchor == constraintAnchor3 || paramConstraintAnchor == constraintAnchor4) && constraintAnchor3.isConnected() && constraintAnchor3.getTarget() == constraintAnchor4.getTarget()) {
      constraintAnchor5.reset();
    } 
    paramConstraintAnchor.reset();
  }
  
  public void resetAnchors() {
    ConstraintWidget constraintWidget = getParent();
    if (constraintWidget != null && constraintWidget instanceof ConstraintWidgetContainer && ((ConstraintWidgetContainer)getParent()).handlesInternalConstraints())
      return; 
    byte b = 0;
    int i = this.mAnchors.size();
    while (b < i) {
      ((ConstraintAnchor)this.mAnchors.get(b)).reset();
      b++;
    } 
  }
  
  public void resetAnchors(int paramInt) {
    ConstraintWidget constraintWidget = getParent();
    if (constraintWidget != null && constraintWidget instanceof ConstraintWidgetContainer && ((ConstraintWidgetContainer)getParent()).handlesInternalConstraints())
      return; 
    byte b = 0;
    int i = this.mAnchors.size();
    while (b < i) {
      ConstraintAnchor constraintAnchor = (ConstraintAnchor)this.mAnchors.get(b);
      if (paramInt == constraintAnchor.getConnectionCreator()) {
        if (constraintAnchor.isVerticalAnchor()) {
          setVerticalBiasPercent(DEFAULT_BIAS);
        } else {
          setHorizontalBiasPercent(DEFAULT_BIAS);
        } 
        constraintAnchor.reset();
      } 
      b++;
    } 
  }
  
  public void resetResolutionNodes() {
    for (byte b = 0; b < 6; b++)
      this.mListAnchors[b].getResolutionNode().reset(); 
  }
  
  public void resetSolverVariables(Cache paramCache) {
    this.mLeft.resetSolverVariable(paramCache);
    this.mTop.resetSolverVariable(paramCache);
    this.mRight.resetSolverVariable(paramCache);
    this.mBottom.resetSolverVariable(paramCache);
    this.mBaseline.resetSolverVariable(paramCache);
    this.mCenter.resetSolverVariable(paramCache);
    this.mCenterX.resetSolverVariable(paramCache);
    this.mCenterY.resetSolverVariable(paramCache);
  }
  
  public void resolve() {}
  
  public void setBaselineDistance(int paramInt) { this.mBaselineDistance = paramInt; }
  
  public void setCompanionWidget(Object paramObject) { this.mCompanionWidget = paramObject; }
  
  public void setContainerItemSkip(int paramInt) {
    if (paramInt >= 0) {
      this.mContainerItemSkip = paramInt;
      return;
    } 
    this.mContainerItemSkip = 0;
  }
  
  public void setDebugName(String paramString) { this.mDebugName = paramString; }
  
  public void setDebugSolverName(LinearSystem paramLinearSystem, String paramString) {
    this.mDebugName = paramString;
    SolverVariable solverVariable4 = paramLinearSystem.createObjectVariable(this.mLeft);
    SolverVariable solverVariable3 = paramLinearSystem.createObjectVariable(this.mTop);
    SolverVariable solverVariable2 = paramLinearSystem.createObjectVariable(this.mRight);
    SolverVariable solverVariable1 = paramLinearSystem.createObjectVariable(this.mBottom);
    StringBuilder stringBuilder4 = new StringBuilder();
    stringBuilder4.append(paramString);
    stringBuilder4.append(".left");
    solverVariable4.setName(stringBuilder4.toString());
    StringBuilder stringBuilder3 = new StringBuilder();
    stringBuilder3.append(paramString);
    stringBuilder3.append(".top");
    solverVariable3.setName(stringBuilder3.toString());
    StringBuilder stringBuilder2 = new StringBuilder();
    stringBuilder2.append(paramString);
    stringBuilder2.append(".right");
    solverVariable2.setName(stringBuilder2.toString());
    StringBuilder stringBuilder1 = new StringBuilder();
    stringBuilder1.append(paramString);
    stringBuilder1.append(".bottom");
    solverVariable1.setName(stringBuilder1.toString());
    if (this.mBaselineDistance > 0) {
      SolverVariable solverVariable = paramLinearSystem.createObjectVariable(this.mBaseline);
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(paramString);
      stringBuilder.append(".baseline");
      solverVariable.setName(stringBuilder.toString());
    } 
  }
  
  public void setDimension(int paramInt1, int paramInt2) {
    this.mWidth = paramInt1;
    if (this.mWidth < this.mMinWidth)
      this.mWidth = this.mMinWidth; 
    this.mHeight = paramInt2;
    if (this.mHeight < this.mMinHeight)
      this.mHeight = this.mMinHeight; 
  }
  
  public void setDimensionRatio(float paramFloat, int paramInt) {
    this.mDimensionRatio = paramFloat;
    this.mDimensionRatioSide = paramInt;
  }
  
  public void setDimensionRatio(String paramString) { // Byte code:
    //   0: aload_1
    //   1: ifnull -> 261
    //   4: aload_1
    //   5: invokevirtual length : ()I
    //   8: ifne -> 14
    //   11: goto -> 261
    //   14: iconst_m1
    //   15: istore #6
    //   17: aload_1
    //   18: invokevirtual length : ()I
    //   21: istore #8
    //   23: aload_1
    //   24: bipush #44
    //   26: invokevirtual indexOf : (I)I
    //   29: istore #9
    //   31: iconst_0
    //   32: istore #7
    //   34: iload #6
    //   36: istore #4
    //   38: iload #7
    //   40: istore #5
    //   42: iload #9
    //   44: ifle -> 114
    //   47: iload #6
    //   49: istore #4
    //   51: iload #7
    //   53: istore #5
    //   55: iload #9
    //   57: iload #8
    //   59: iconst_1
    //   60: isub
    //   61: if_icmpge -> 114
    //   64: aload_1
    //   65: iconst_0
    //   66: iload #9
    //   68: invokevirtual substring : (II)Ljava/lang/String;
    //   71: astore #10
    //   73: aload #10
    //   75: ldc_w 'W'
    //   78: invokevirtual equalsIgnoreCase : (Ljava/lang/String;)Z
    //   81: ifeq -> 90
    //   84: iconst_0
    //   85: istore #4
    //   87: goto -> 108
    //   90: iload #6
    //   92: istore #4
    //   94: aload #10
    //   96: ldc_w 'H'
    //   99: invokevirtual equalsIgnoreCase : (Ljava/lang/String;)Z
    //   102: ifeq -> 108
    //   105: iconst_1
    //   106: istore #4
    //   108: iload #9
    //   110: iconst_1
    //   111: iadd
    //   112: istore #5
    //   114: aload_1
    //   115: bipush #58
    //   117: invokevirtual indexOf : (I)I
    //   120: istore #6
    //   122: iload #6
    //   124: iflt -> 219
    //   127: iload #6
    //   129: iload #8
    //   131: iconst_1
    //   132: isub
    //   133: if_icmpge -> 219
    //   136: aload_1
    //   137: iload #5
    //   139: iload #6
    //   141: invokevirtual substring : (II)Ljava/lang/String;
    //   144: astore #10
    //   146: aload_1
    //   147: iload #6
    //   149: iconst_1
    //   150: iadd
    //   151: invokevirtual substring : (I)Ljava/lang/String;
    //   154: astore_1
    //   155: aload #10
    //   157: invokevirtual length : ()I
    //   160: ifle -> 241
    //   163: aload_1
    //   164: invokevirtual length : ()I
    //   167: ifle -> 241
    //   170: aload #10
    //   172: invokestatic parseFloat : (Ljava/lang/String;)F
    //   175: fstore_2
    //   176: aload_1
    //   177: invokestatic parseFloat : (Ljava/lang/String;)F
    //   180: fstore_3
    //   181: fload_2
    //   182: fconst_0
    //   183: fcmpl
    //   184: ifle -> 241
    //   187: fload_3
    //   188: fconst_0
    //   189: fcmpl
    //   190: ifle -> 241
    //   193: iload #4
    //   195: iconst_1
    //   196: if_icmpne -> 209
    //   199: fload_3
    //   200: fload_2
    //   201: fdiv
    //   202: invokestatic abs : (F)F
    //   205: fstore_2
    //   206: goto -> 243
    //   209: fload_2
    //   210: fload_3
    //   211: fdiv
    //   212: invokestatic abs : (F)F
    //   215: fstore_2
    //   216: goto -> 243
    //   219: aload_1
    //   220: iload #5
    //   222: invokevirtual substring : (I)Ljava/lang/String;
    //   225: astore_1
    //   226: aload_1
    //   227: invokevirtual length : ()I
    //   230: ifle -> 241
    //   233: aload_1
    //   234: invokestatic parseFloat : (Ljava/lang/String;)F
    //   237: fstore_2
    //   238: goto -> 243
    //   241: fconst_0
    //   242: fstore_2
    //   243: fload_2
    //   244: fconst_0
    //   245: fcmpl
    //   246: ifle -> 260
    //   249: aload_0
    //   250: fload_2
    //   251: putfield mDimensionRatio : F
    //   254: aload_0
    //   255: iload #4
    //   257: putfield mDimensionRatioSide : I
    //   260: return
    //   261: aload_0
    //   262: fconst_0
    //   263: putfield mDimensionRatio : F
    //   266: return
    //   267: astore_1
    //   268: goto -> 241
    // Exception table:
    //   from	to	target	type
    //   170	181	267	java/lang/NumberFormatException
    //   199	206	267	java/lang/NumberFormatException
    //   209	216	267	java/lang/NumberFormatException
    //   233	238	267	java/lang/NumberFormatException }
  
  public void setDrawHeight(int paramInt) { this.mDrawHeight = paramInt; }
  
  public void setDrawOrigin(int paramInt1, int paramInt2) {
    this.mDrawX = paramInt1 - this.mOffsetX;
    this.mDrawY = paramInt2 - this.mOffsetY;
    this.mX = this.mDrawX;
    this.mY = this.mDrawY;
  }
  
  public void setDrawWidth(int paramInt) { this.mDrawWidth = paramInt; }
  
  public void setDrawX(int paramInt) {
    this.mDrawX = paramInt - this.mOffsetX;
    this.mX = this.mDrawX;
  }
  
  public void setDrawY(int paramInt) {
    this.mDrawY = paramInt - this.mOffsetY;
    this.mY = this.mDrawY;
  }
  
  public void setFrame(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = paramInt3 - paramInt1;
    paramInt3 = paramInt4 - paramInt2;
    this.mX = paramInt1;
    this.mY = paramInt2;
    if (this.mVisibility == 8) {
      this.mWidth = 0;
      this.mHeight = 0;
      return;
    } 
    paramInt1 = i;
    if (this.mListDimensionBehaviors[false] == DimensionBehaviour.FIXED) {
      paramInt1 = i;
      if (i < this.mWidth)
        paramInt1 = this.mWidth; 
    } 
    paramInt2 = paramInt3;
    if (this.mListDimensionBehaviors[true] == DimensionBehaviour.FIXED) {
      paramInt2 = paramInt3;
      if (paramInt3 < this.mHeight)
        paramInt2 = this.mHeight; 
    } 
    this.mWidth = paramInt1;
    this.mHeight = paramInt2;
    if (this.mHeight < this.mMinHeight)
      this.mHeight = this.mMinHeight; 
    if (this.mWidth < this.mMinWidth)
      this.mWidth = this.mMinWidth; 
  }
  
  public void setGoneMargin(ConstraintAnchor.Type paramType, int paramInt) {
    switch (paramType) {
      default:
        return;
      case MATCH_CONSTRAINT:
        this.mBottom.mGoneMargin = paramInt;
        return;
      case MATCH_PARENT:
        this.mRight.mGoneMargin = paramInt;
        return;
      case WRAP_CONTENT:
        this.mTop.mGoneMargin = paramInt;
        return;
      case FIXED:
        break;
    } 
    this.mLeft.mGoneMargin = paramInt;
  }
  
  public void setHeight(int paramInt) {
    this.mHeight = paramInt;
    if (this.mHeight < this.mMinHeight)
      this.mHeight = this.mMinHeight; 
  }
  
  public void setHeightWrapContent(boolean paramBoolean) { this.mIsHeightWrapContent = paramBoolean; }
  
  public void setHorizontalBiasPercent(float paramFloat) { this.mHorizontalBiasPercent = paramFloat; }
  
  public void setHorizontalChainStyle(int paramInt) { this.mHorizontalChainStyle = paramInt; }
  
  public void setHorizontalDimension(int paramInt1, int paramInt2) {
    this.mX = paramInt1;
    this.mWidth = paramInt2 - paramInt1;
    if (this.mWidth < this.mMinWidth)
      this.mWidth = this.mMinWidth; 
  }
  
  public void setHorizontalDimensionBehaviour(DimensionBehaviour paramDimensionBehaviour) {
    this.mListDimensionBehaviors[0] = paramDimensionBehaviour;
    if (paramDimensionBehaviour == DimensionBehaviour.WRAP_CONTENT)
      setWidth(this.mWrapWidth); 
  }
  
  public void setHorizontalMatchStyle(int paramInt1, int paramInt2, int paramInt3, float paramFloat) {
    this.mMatchConstraintDefaultWidth = paramInt1;
    this.mMatchConstraintMinWidth = paramInt2;
    this.mMatchConstraintMaxWidth = paramInt3;
    this.mMatchConstraintPercentWidth = paramFloat;
    if (paramFloat < 1.0F && this.mMatchConstraintDefaultWidth == 0)
      this.mMatchConstraintDefaultWidth = 2; 
  }
  
  public void setHorizontalWeight(float paramFloat) { this.mWeight[0] = paramFloat; }
  
  public void setMaxHeight(int paramInt) { this.mMaxDimension[1] = paramInt; }
  
  public void setMaxWidth(int paramInt) { this.mMaxDimension[0] = paramInt; }
  
  public void setMinHeight(int paramInt) {
    if (paramInt < 0) {
      this.mMinHeight = 0;
      return;
    } 
    this.mMinHeight = paramInt;
  }
  
  public void setMinWidth(int paramInt) {
    if (paramInt < 0) {
      this.mMinWidth = 0;
      return;
    } 
    this.mMinWidth = paramInt;
  }
  
  public void setOffset(int paramInt1, int paramInt2) {
    this.mOffsetX = paramInt1;
    this.mOffsetY = paramInt2;
  }
  
  public void setOrigin(int paramInt1, int paramInt2) {
    this.mX = paramInt1;
    this.mY = paramInt2;
  }
  
  public void setParent(ConstraintWidget paramConstraintWidget) { this.mParent = paramConstraintWidget; }
  
  public void setType(String paramString) { this.mType = paramString; }
  
  public void setVerticalBiasPercent(float paramFloat) { this.mVerticalBiasPercent = paramFloat; }
  
  public void setVerticalChainStyle(int paramInt) { this.mVerticalChainStyle = paramInt; }
  
  public void setVerticalDimension(int paramInt1, int paramInt2) {
    this.mY = paramInt1;
    this.mHeight = paramInt2 - paramInt1;
    if (this.mHeight < this.mMinHeight)
      this.mHeight = this.mMinHeight; 
  }
  
  public void setVerticalDimensionBehaviour(DimensionBehaviour paramDimensionBehaviour) {
    this.mListDimensionBehaviors[1] = paramDimensionBehaviour;
    if (paramDimensionBehaviour == DimensionBehaviour.WRAP_CONTENT)
      setHeight(this.mWrapHeight); 
  }
  
  public void setVerticalMatchStyle(int paramInt1, int paramInt2, int paramInt3, float paramFloat) {
    this.mMatchConstraintDefaultHeight = paramInt1;
    this.mMatchConstraintMinHeight = paramInt2;
    this.mMatchConstraintMaxHeight = paramInt3;
    this.mMatchConstraintPercentHeight = paramFloat;
    if (paramFloat < 1.0F && this.mMatchConstraintDefaultHeight == 0)
      this.mMatchConstraintDefaultHeight = 2; 
  }
  
  public void setVerticalWeight(float paramFloat) { this.mWeight[1] = paramFloat; }
  
  public void setVisibility(int paramInt) { this.mVisibility = paramInt; }
  
  public void setWidth(int paramInt) {
    this.mWidth = paramInt;
    if (this.mWidth < this.mMinWidth)
      this.mWidth = this.mMinWidth; 
  }
  
  public void setWidthWrapContent(boolean paramBoolean) { this.mIsWidthWrapContent = paramBoolean; }
  
  public void setWrapHeight(int paramInt) { this.mWrapHeight = paramInt; }
  
  public void setWrapWidth(int paramInt) { this.mWrapWidth = paramInt; }
  
  public void setX(int paramInt) { this.mX = paramInt; }
  
  public void setY(int paramInt) { this.mY = paramInt; }
  
  public void setupDimensionRatio(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4) {
    if (this.mMatchConstraintDefaultWidth == 0)
      this.mMatchConstraintDefaultWidth = 3; 
    if (this.mMatchConstraintDefaultHeight == 0)
      this.mMatchConstraintDefaultHeight = 3; 
    if (this.mResolvedDimensionRatioSide == -1)
      if (paramBoolean3 && !paramBoolean4) {
        this.mResolvedDimensionRatioSide = 0;
      } else if (!paramBoolean3 && paramBoolean4) {
        this.mResolvedDimensionRatioSide = 1;
        if (this.mDimensionRatioSide == -1)
          this.mResolvedDimensionRatio = 1.0F / this.mResolvedDimensionRatio; 
      }  
    if (this.mResolvedDimensionRatioSide == 0 && (!this.mTop.isConnected() || !this.mBottom.isConnected())) {
      this.mResolvedDimensionRatioSide = 1;
    } else if (this.mResolvedDimensionRatioSide == 1 && (!this.mLeft.isConnected() || !this.mRight.isConnected())) {
      this.mResolvedDimensionRatioSide = 0;
    } 
    if (this.mResolvedDimensionRatioSide == -1 && (!this.mTop.isConnected() || !this.mBottom.isConnected() || !this.mLeft.isConnected() || !this.mRight.isConnected()))
      if (this.mTop.isConnected() && this.mBottom.isConnected()) {
        this.mResolvedDimensionRatioSide = 0;
      } else if (this.mLeft.isConnected() && this.mRight.isConnected()) {
        this.mResolvedDimensionRatio = 1.0F / this.mResolvedDimensionRatio;
        this.mResolvedDimensionRatioSide = 1;
      }  
    if (this.mResolvedDimensionRatioSide == -1)
      if (paramBoolean1 && !paramBoolean2) {
        this.mResolvedDimensionRatioSide = 0;
      } else if (!paramBoolean1 && paramBoolean2) {
        this.mResolvedDimensionRatio = 1.0F / this.mResolvedDimensionRatio;
        this.mResolvedDimensionRatioSide = 1;
      }  
    if (this.mResolvedDimensionRatioSide == -1) {
      if (this.mMatchConstraintMinWidth > 0 && this.mMatchConstraintMinHeight == 0) {
        this.mResolvedDimensionRatioSide = 0;
        return;
      } 
      if (this.mMatchConstraintMinWidth == 0 && this.mMatchConstraintMinHeight > 0) {
        this.mResolvedDimensionRatio = 1.0F / this.mResolvedDimensionRatio;
        this.mResolvedDimensionRatioSide = 1;
        return;
      } 
      this.mResolvedDimensionRatio = 1.0F / this.mResolvedDimensionRatio;
      this.mResolvedDimensionRatioSide = 1;
    } 
  }
  
  public String toString() {
    String str;
    StringBuilder stringBuilder = new StringBuilder();
    if (this.mType != null) {
      StringBuilder stringBuilder1 = new StringBuilder();
      stringBuilder1.append("type: ");
      stringBuilder1.append(this.mType);
      stringBuilder1.append(" ");
      str = stringBuilder1.toString();
    } else {
      str = "";
    } 
    stringBuilder.append(str);
    if (this.mDebugName != null) {
      StringBuilder stringBuilder1 = new StringBuilder();
      stringBuilder1.append("id: ");
      stringBuilder1.append(this.mDebugName);
      stringBuilder1.append(" ");
      String str1 = stringBuilder1.toString();
    } else {
      str = "";
    } 
    stringBuilder.append(str);
    stringBuilder.append("(");
    stringBuilder.append(this.mX);
    stringBuilder.append(", ");
    stringBuilder.append(this.mY);
    stringBuilder.append(") - (");
    stringBuilder.append(this.mWidth);
    stringBuilder.append(" x ");
    stringBuilder.append(this.mHeight);
    stringBuilder.append(") wrap: (");
    stringBuilder.append(this.mWrapWidth);
    stringBuilder.append(" x ");
    stringBuilder.append(this.mWrapHeight);
    stringBuilder.append(")");
    return stringBuilder.toString();
  }
  
  public void updateDrawPosition() {
    int i = this.mX;
    int j = this.mY;
    int k = this.mX;
    int m = this.mWidth;
    int n = this.mY;
    int i1 = this.mHeight;
    this.mDrawX = i;
    this.mDrawY = j;
    this.mDrawWidth = k + m - i;
    this.mDrawHeight = n + i1 - j;
  }
  
  public void updateFromSolver(LinearSystem paramLinearSystem) { setFrame(paramLinearSystem.getObjectVariableValue(this.mLeft), paramLinearSystem.getObjectVariableValue(this.mTop), paramLinearSystem.getObjectVariableValue(this.mRight), paramLinearSystem.getObjectVariableValue(this.mBottom)); }
  
  public void updateResolutionNodes() {
    for (byte b = 0; b < 6; b++)
      this.mListAnchors[b].getResolutionNode().update(); 
  }
  
  public enum ContentAlignment {
    BEGIN, BOTTOM, END, LEFT, MIDDLE, RIGHT, TOP, VERTICAL_MIDDLE;
    
    static  {
      END = new ContentAlignment("END", 2);
      TOP = new ContentAlignment("TOP", 3);
      VERTICAL_MIDDLE = new ContentAlignment("VERTICAL_MIDDLE", 4);
      BOTTOM = new ContentAlignment("BOTTOM", 5);
      LEFT = new ContentAlignment("LEFT", 6);
      RIGHT = new ContentAlignment("RIGHT", 7);
      $VALUES = new ContentAlignment[] { BEGIN, MIDDLE, END, TOP, VERTICAL_MIDDLE, BOTTOM, LEFT, RIGHT };
    }
  }
  
  public enum DimensionBehaviour {
    FIXED, MATCH_CONSTRAINT, MATCH_PARENT, WRAP_CONTENT;
    
    static  {
      MATCH_CONSTRAINT = new DimensionBehaviour("MATCH_CONSTRAINT", 2);
      MATCH_PARENT = new DimensionBehaviour("MATCH_PARENT", 3);
      $VALUES = new DimensionBehaviour[] { FIXED, WRAP_CONTENT, MATCH_CONSTRAINT, MATCH_PARENT };
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/constraint/solver/widgets/ConstraintWidget.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */