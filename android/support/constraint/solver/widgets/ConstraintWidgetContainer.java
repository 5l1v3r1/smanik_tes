package android.support.constraint.solver.widgets;

import android.support.constraint.solver.LinearSystem;
import android.support.constraint.solver.Metrics;
import java.util.ArrayList;
import java.util.Arrays;

public class ConstraintWidgetContainer extends WidgetContainer {
  private static final boolean DEBUG = false;
  
  static final boolean DEBUG_GRAPH = false;
  
  private static final boolean DEBUG_LAYOUT = false;
  
  private static final int MAX_ITERATIONS = 8;
  
  private static final boolean USE_SNAPSHOT = true;
  
  int mDebugSolverPassCount = 0;
  
  private boolean mHeightMeasuredTooSmall = false;
  
  ConstraintWidget[] mHorizontalChainsArray = new ConstraintWidget[4];
  
  int mHorizontalChainsSize = 0;
  
  private boolean mIsRtl = false;
  
  private int mOptimizationLevel = 3;
  
  int mPaddingBottom;
  
  int mPaddingLeft;
  
  int mPaddingRight;
  
  int mPaddingTop;
  
  private Snapshot mSnapshot;
  
  protected LinearSystem mSystem = new LinearSystem();
  
  ConstraintWidget[] mVerticalChainsArray = new ConstraintWidget[4];
  
  int mVerticalChainsSize = 0;
  
  private boolean mWidthMeasuredTooSmall = false;
  
  public ConstraintWidgetContainer() {}
  
  public ConstraintWidgetContainer(int paramInt1, int paramInt2) { super(paramInt1, paramInt2); }
  
  public ConstraintWidgetContainer(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { super(paramInt1, paramInt2, paramInt3, paramInt4); }
  
  private void addHorizontalChain(ConstraintWidget paramConstraintWidget) {
    for (byte b = 0; b < this.mHorizontalChainsSize; b++) {
      if (this.mHorizontalChainsArray[b] == paramConstraintWidget)
        return; 
    } 
    if (this.mHorizontalChainsSize + 1 >= this.mHorizontalChainsArray.length)
      this.mHorizontalChainsArray = (ConstraintWidget[])Arrays.copyOf(this.mHorizontalChainsArray, this.mHorizontalChainsArray.length * 2); 
    this.mHorizontalChainsArray[this.mHorizontalChainsSize] = paramConstraintWidget;
    this.mHorizontalChainsSize++;
  }
  
  private void addVerticalChain(ConstraintWidget paramConstraintWidget) {
    for (byte b = 0; b < this.mVerticalChainsSize; b++) {
      if (this.mVerticalChainsArray[b] == paramConstraintWidget)
        return; 
    } 
    if (this.mVerticalChainsSize + 1 >= this.mVerticalChainsArray.length)
      this.mVerticalChainsArray = (ConstraintWidget[])Arrays.copyOf(this.mVerticalChainsArray, this.mVerticalChainsArray.length * 2); 
    this.mVerticalChainsArray[this.mVerticalChainsSize] = paramConstraintWidget;
    this.mVerticalChainsSize++;
  }
  
  private void resetChains() {
    this.mHorizontalChainsSize = 0;
    this.mVerticalChainsSize = 0;
  }
  
  void addChain(ConstraintWidget paramConstraintWidget, int paramInt) {
    if (paramInt == 0) {
      while (paramConstraintWidget.mLeft.mTarget != null && paramConstraintWidget.mLeft.mTarget.mOwner.mRight.mTarget != null && paramConstraintWidget.mLeft.mTarget.mOwner.mRight.mTarget == paramConstraintWidget.mLeft && paramConstraintWidget.mLeft.mTarget.mOwner != paramConstraintWidget)
        paramConstraintWidget = paramConstraintWidget.mLeft.mTarget.mOwner; 
      addHorizontalChain(paramConstraintWidget);
      return;
    } 
    if (paramInt == 1) {
      while (paramConstraintWidget.mTop.mTarget != null && paramConstraintWidget.mTop.mTarget.mOwner.mBottom.mTarget != null && paramConstraintWidget.mTop.mTarget.mOwner.mBottom.mTarget == paramConstraintWidget.mTop && paramConstraintWidget.mTop.mTarget.mOwner != paramConstraintWidget)
        paramConstraintWidget = paramConstraintWidget.mTop.mTarget.mOwner; 
      addVerticalChain(paramConstraintWidget);
    } 
  }
  
  public boolean addChildrenToSolver(LinearSystem paramLinearSystem) {
    addToSolver(paramLinearSystem);
    int i = this.mChildren.size();
    for (byte b = 0; b < i; b++) {
      ConstraintWidget constraintWidget = (ConstraintWidget)this.mChildren.get(b);
      if (constraintWidget instanceof ConstraintWidgetContainer) {
        ConstraintWidget.DimensionBehaviour dimensionBehaviour1 = constraintWidget.mListDimensionBehaviors[0];
        ConstraintWidget.DimensionBehaviour dimensionBehaviour2 = constraintWidget.mListDimensionBehaviors[1];
        if (dimensionBehaviour1 == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT)
          constraintWidget.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED); 
        if (dimensionBehaviour2 == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT)
          constraintWidget.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED); 
        constraintWidget.addToSolver(paramLinearSystem);
        if (dimensionBehaviour1 == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT)
          constraintWidget.setHorizontalDimensionBehaviour(dimensionBehaviour1); 
        if (dimensionBehaviour2 == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT)
          constraintWidget.setVerticalDimensionBehaviour(dimensionBehaviour2); 
      } else {
        Optimizer.checkMatchParent(this, paramLinearSystem, constraintWidget);
        constraintWidget.addToSolver(paramLinearSystem);
      } 
    } 
    if (this.mHorizontalChainsSize > 0)
      Chain.applyChainConstraints(this, paramLinearSystem, 0); 
    if (this.mVerticalChainsSize > 0)
      Chain.applyChainConstraints(this, paramLinearSystem, 1); 
    return true;
  }
  
  public void analyze(int paramInt) {
    super.analyze(paramInt);
    int i = this.mChildren.size();
    for (byte b = 0; b < i; b++)
      ((ConstraintWidget)this.mChildren.get(b)).analyze(paramInt); 
  }
  
  public void fillMetrics(Metrics paramMetrics) { this.mSystem.fillMetrics(paramMetrics); }
  
  public ArrayList<Guideline> getHorizontalGuidelines() {
    ArrayList arrayList = new ArrayList();
    int i = this.mChildren.size();
    for (byte b = 0; b < i; b++) {
      ConstraintWidget constraintWidget = (ConstraintWidget)this.mChildren.get(b);
      if (constraintWidget instanceof Guideline) {
        constraintWidget = (Guideline)constraintWidget;
        if (constraintWidget.getOrientation() == 0)
          arrayList.add(constraintWidget); 
      } 
    } 
    return arrayList;
  }
  
  public int getOptimizationLevel() { return this.mOptimizationLevel; }
  
  public LinearSystem getSystem() { return this.mSystem; }
  
  public String getType() { return "ConstraintLayout"; }
  
  public ArrayList<Guideline> getVerticalGuidelines() {
    ArrayList arrayList = new ArrayList();
    int i = this.mChildren.size();
    for (byte b = 0; b < i; b++) {
      ConstraintWidget constraintWidget = (ConstraintWidget)this.mChildren.get(b);
      if (constraintWidget instanceof Guideline) {
        constraintWidget = (Guideline)constraintWidget;
        if (constraintWidget.getOrientation() == 1)
          arrayList.add(constraintWidget); 
      } 
    } 
    return arrayList;
  }
  
  public boolean handlesInternalConstraints() { return false; }
  
  public boolean isHeightMeasuredTooSmall() { return this.mHeightMeasuredTooSmall; }
  
  public boolean isRtl() { return this.mIsRtl; }
  
  public boolean isWidthMeasuredTooSmall() { return this.mWidthMeasuredTooSmall; }
  
  public void layout() { // Byte code:
    //   0: aload_0
    //   1: getfield mX : I
    //   4: istore #6
    //   6: aload_0
    //   7: getfield mY : I
    //   10: istore #7
    //   12: iconst_0
    //   13: aload_0
    //   14: invokevirtual getWidth : ()I
    //   17: invokestatic max : (II)I
    //   20: istore #8
    //   22: iconst_0
    //   23: aload_0
    //   24: invokevirtual getHeight : ()I
    //   27: invokestatic max : (II)I
    //   30: istore #9
    //   32: aload_0
    //   33: iconst_0
    //   34: putfield mWidthMeasuredTooSmall : Z
    //   37: aload_0
    //   38: iconst_0
    //   39: putfield mHeightMeasuredTooSmall : Z
    //   42: aload_0
    //   43: getfield mParent : Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   46: ifnull -> 110
    //   49: aload_0
    //   50: getfield mSnapshot : Landroid/support/constraint/solver/widgets/Snapshot;
    //   53: ifnonnull -> 68
    //   56: aload_0
    //   57: new android/support/constraint/solver/widgets/Snapshot
    //   60: dup
    //   61: aload_0
    //   62: invokespecial <init> : (Landroid/support/constraint/solver/widgets/ConstraintWidget;)V
    //   65: putfield mSnapshot : Landroid/support/constraint/solver/widgets/Snapshot;
    //   68: aload_0
    //   69: getfield mSnapshot : Landroid/support/constraint/solver/widgets/Snapshot;
    //   72: aload_0
    //   73: invokevirtual updateFrom : (Landroid/support/constraint/solver/widgets/ConstraintWidget;)V
    //   76: aload_0
    //   77: aload_0
    //   78: getfield mPaddingLeft : I
    //   81: invokevirtual setX : (I)V
    //   84: aload_0
    //   85: aload_0
    //   86: getfield mPaddingTop : I
    //   89: invokevirtual setY : (I)V
    //   92: aload_0
    //   93: invokevirtual resetAnchors : ()V
    //   96: aload_0
    //   97: aload_0
    //   98: getfield mSystem : Landroid/support/constraint/solver/LinearSystem;
    //   101: invokevirtual getCache : ()Landroid/support/constraint/solver/Cache;
    //   104: invokevirtual resetSolverVariables : (Landroid/support/constraint/solver/Cache;)V
    //   107: goto -> 120
    //   110: aload_0
    //   111: iconst_0
    //   112: putfield mX : I
    //   115: aload_0
    //   116: iconst_0
    //   117: putfield mY : I
    //   120: aload_0
    //   121: getfield mOptimizationLevel : I
    //   124: ifeq -> 155
    //   127: aload_0
    //   128: bipush #8
    //   130: invokevirtual optimizeFor : (I)Z
    //   133: ifne -> 140
    //   136: aload_0
    //   137: invokevirtual optimizeReset : ()V
    //   140: aload_0
    //   141: invokevirtual optimize : ()V
    //   144: aload_0
    //   145: getfield mSystem : Landroid/support/constraint/solver/LinearSystem;
    //   148: iconst_1
    //   149: putfield graphOptimizer : Z
    //   152: goto -> 163
    //   155: aload_0
    //   156: getfield mSystem : Landroid/support/constraint/solver/LinearSystem;
    //   159: iconst_0
    //   160: putfield graphOptimizer : Z
    //   163: aload_0
    //   164: getfield mListDimensionBehaviors : [Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   167: iconst_1
    //   168: aaload
    //   169: astore #15
    //   171: aload_0
    //   172: getfield mListDimensionBehaviors : [Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   175: iconst_0
    //   176: aaload
    //   177: astore #16
    //   179: aload_0
    //   180: invokespecial resetChains : ()V
    //   183: aload_0
    //   184: getfield mChildren : Ljava/util/ArrayList;
    //   187: invokevirtual size : ()I
    //   190: istore #10
    //   192: iconst_0
    //   193: istore_1
    //   194: iload_1
    //   195: iload #10
    //   197: if_icmpge -> 236
    //   200: aload_0
    //   201: getfield mChildren : Ljava/util/ArrayList;
    //   204: iload_1
    //   205: invokevirtual get : (I)Ljava/lang/Object;
    //   208: checkcast android/support/constraint/solver/widgets/ConstraintWidget
    //   211: astore #14
    //   213: aload #14
    //   215: instanceof android/support/constraint/solver/widgets/WidgetContainer
    //   218: ifeq -> 229
    //   221: aload #14
    //   223: checkcast android/support/constraint/solver/widgets/WidgetContainer
    //   226: invokevirtual layout : ()V
    //   229: iload_1
    //   230: iconst_1
    //   231: iadd
    //   232: istore_1
    //   233: goto -> 194
    //   236: iconst_1
    //   237: istore #11
    //   239: iconst_0
    //   240: istore_2
    //   241: iconst_0
    //   242: istore_1
    //   243: iload #11
    //   245: ifeq -> 919
    //   248: iload_2
    //   249: iconst_1
    //   250: iadd
    //   251: istore #5
    //   253: aload_0
    //   254: getfield mSystem : Landroid/support/constraint/solver/LinearSystem;
    //   257: invokevirtual reset : ()V
    //   260: aload_0
    //   261: aload_0
    //   262: getfield mSystem : Landroid/support/constraint/solver/LinearSystem;
    //   265: invokevirtual addChildrenToSolver : (Landroid/support/constraint/solver/LinearSystem;)Z
    //   268: istore #12
    //   270: iload #12
    //   272: istore #11
    //   274: iload #12
    //   276: ifeq -> 350
    //   279: aload_0
    //   280: getfield mSystem : Landroid/support/constraint/solver/LinearSystem;
    //   283: invokevirtual minimize : ()V
    //   286: iload #12
    //   288: istore #11
    //   290: goto -> 350
    //   293: astore #14
    //   295: iload #12
    //   297: istore #11
    //   299: goto -> 304
    //   302: astore #14
    //   304: aload #14
    //   306: invokevirtual printStackTrace : ()V
    //   309: getstatic java/lang/System.out : Ljava/io/PrintStream;
    //   312: astore #17
    //   314: new java/lang/StringBuilder
    //   317: dup
    //   318: invokespecial <init> : ()V
    //   321: astore #18
    //   323: aload #18
    //   325: ldc_w 'EXCEPTION : '
    //   328: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   331: pop
    //   332: aload #18
    //   334: aload #14
    //   336: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   339: pop
    //   340: aload #17
    //   342: aload #18
    //   344: invokevirtual toString : ()Ljava/lang/String;
    //   347: invokevirtual println : (Ljava/lang/String;)V
    //   350: iload #11
    //   352: ifeq -> 369
    //   355: aload_0
    //   356: aload_0
    //   357: getfield mSystem : Landroid/support/constraint/solver/LinearSystem;
    //   360: getstatic android/support/constraint/solver/widgets/Optimizer.flags : [Z
    //   363: invokevirtual updateChildrenFromSolver : (Landroid/support/constraint/solver/LinearSystem;[Z)V
    //   366: goto -> 475
    //   369: aload_0
    //   370: aload_0
    //   371: getfield mSystem : Landroid/support/constraint/solver/LinearSystem;
    //   374: invokevirtual updateFromSolver : (Landroid/support/constraint/solver/LinearSystem;)V
    //   377: iconst_0
    //   378: istore_2
    //   379: iload_2
    //   380: iload #10
    //   382: if_icmpge -> 475
    //   385: aload_0
    //   386: getfield mChildren : Ljava/util/ArrayList;
    //   389: iload_2
    //   390: invokevirtual get : (I)Ljava/lang/Object;
    //   393: checkcast android/support/constraint/solver/widgets/ConstraintWidget
    //   396: astore #14
    //   398: aload #14
    //   400: getfield mListDimensionBehaviors : [Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   403: iconst_0
    //   404: aaload
    //   405: getstatic android/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour.MATCH_CONSTRAINT : Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   408: if_acmpne -> 433
    //   411: aload #14
    //   413: invokevirtual getWidth : ()I
    //   416: aload #14
    //   418: invokevirtual getWrapWidth : ()I
    //   421: if_icmpge -> 433
    //   424: getstatic android/support/constraint/solver/widgets/Optimizer.flags : [Z
    //   427: iconst_2
    //   428: iconst_1
    //   429: bastore
    //   430: goto -> 366
    //   433: aload #14
    //   435: getfield mListDimensionBehaviors : [Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   438: iconst_1
    //   439: aaload
    //   440: getstatic android/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour.MATCH_CONSTRAINT : Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   443: if_acmpne -> 468
    //   446: aload #14
    //   448: invokevirtual getHeight : ()I
    //   451: aload #14
    //   453: invokevirtual getWrapHeight : ()I
    //   456: if_icmpge -> 468
    //   459: getstatic android/support/constraint/solver/widgets/Optimizer.flags : [Z
    //   462: iconst_2
    //   463: iconst_1
    //   464: bastore
    //   465: goto -> 475
    //   468: iload_2
    //   469: iconst_1
    //   470: iadd
    //   471: istore_2
    //   472: goto -> 379
    //   475: iload #5
    //   477: bipush #8
    //   479: if_icmpge -> 672
    //   482: getstatic android/support/constraint/solver/widgets/Optimizer.flags : [Z
    //   485: iconst_2
    //   486: baload
    //   487: ifeq -> 672
    //   490: iconst_0
    //   491: istore_3
    //   492: iconst_0
    //   493: istore #4
    //   495: iconst_0
    //   496: istore_2
    //   497: iload_3
    //   498: iload #10
    //   500: if_icmpge -> 557
    //   503: aload_0
    //   504: getfield mChildren : Ljava/util/ArrayList;
    //   507: iload_3
    //   508: invokevirtual get : (I)Ljava/lang/Object;
    //   511: checkcast android/support/constraint/solver/widgets/ConstraintWidget
    //   514: astore #14
    //   516: iload #4
    //   518: aload #14
    //   520: getfield mX : I
    //   523: aload #14
    //   525: invokevirtual getWidth : ()I
    //   528: iadd
    //   529: invokestatic max : (II)I
    //   532: istore #4
    //   534: iload_2
    //   535: aload #14
    //   537: getfield mY : I
    //   540: aload #14
    //   542: invokevirtual getHeight : ()I
    //   545: iadd
    //   546: invokestatic max : (II)I
    //   549: istore_2
    //   550: iload_3
    //   551: iconst_1
    //   552: iadd
    //   553: istore_3
    //   554: goto -> 497
    //   557: aload_0
    //   558: getfield mMinWidth : I
    //   561: iload #4
    //   563: invokestatic max : (II)I
    //   566: istore #4
    //   568: aload_0
    //   569: getfield mMinHeight : I
    //   572: iload_2
    //   573: invokestatic max : (II)I
    //   576: istore_3
    //   577: aload #16
    //   579: getstatic android/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour.WRAP_CONTENT : Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   582: if_acmpne -> 617
    //   585: aload_0
    //   586: invokevirtual getWidth : ()I
    //   589: iload #4
    //   591: if_icmpge -> 617
    //   594: aload_0
    //   595: iload #4
    //   597: invokevirtual setWidth : (I)V
    //   600: aload_0
    //   601: getfield mListDimensionBehaviors : [Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   604: iconst_0
    //   605: getstatic android/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour.WRAP_CONTENT : Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   608: aastore
    //   609: iconst_1
    //   610: istore #12
    //   612: iconst_1
    //   613: istore_2
    //   614: goto -> 622
    //   617: iconst_0
    //   618: istore #12
    //   620: iload_1
    //   621: istore_2
    //   622: iload #12
    //   624: istore #11
    //   626: iload_2
    //   627: istore_1
    //   628: aload #15
    //   630: getstatic android/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour.WRAP_CONTENT : Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   633: if_acmpne -> 675
    //   636: iload #12
    //   638: istore #11
    //   640: iload_2
    //   641: istore_1
    //   642: aload_0
    //   643: invokevirtual getHeight : ()I
    //   646: iload_3
    //   647: if_icmpge -> 675
    //   650: aload_0
    //   651: iload_3
    //   652: invokevirtual setHeight : (I)V
    //   655: aload_0
    //   656: getfield mListDimensionBehaviors : [Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   659: iconst_1
    //   660: getstatic android/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour.WRAP_CONTENT : Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   663: aastore
    //   664: iconst_1
    //   665: istore #11
    //   667: iconst_1
    //   668: istore_1
    //   669: goto -> 675
    //   672: iconst_0
    //   673: istore #11
    //   675: aload_0
    //   676: getfield mMinWidth : I
    //   679: aload_0
    //   680: invokevirtual getWidth : ()I
    //   683: invokestatic max : (II)I
    //   686: istore_2
    //   687: iload_2
    //   688: aload_0
    //   689: invokevirtual getWidth : ()I
    //   692: if_icmple -> 714
    //   695: aload_0
    //   696: iload_2
    //   697: invokevirtual setWidth : (I)V
    //   700: aload_0
    //   701: getfield mListDimensionBehaviors : [Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   704: iconst_0
    //   705: getstatic android/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour.FIXED : Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   708: aastore
    //   709: iconst_1
    //   710: istore #11
    //   712: iconst_1
    //   713: istore_1
    //   714: aload_0
    //   715: getfield mMinHeight : I
    //   718: aload_0
    //   719: invokevirtual getHeight : ()I
    //   722: invokestatic max : (II)I
    //   725: istore_2
    //   726: iload_2
    //   727: aload_0
    //   728: invokevirtual getHeight : ()I
    //   731: if_icmple -> 756
    //   734: aload_0
    //   735: iload_2
    //   736: invokevirtual setHeight : (I)V
    //   739: aload_0
    //   740: getfield mListDimensionBehaviors : [Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   743: iconst_1
    //   744: getstatic android/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour.FIXED : Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   747: aastore
    //   748: iconst_1
    //   749: istore #11
    //   751: iconst_1
    //   752: istore_1
    //   753: goto -> 756
    //   756: iload #11
    //   758: istore #13
    //   760: iload_1
    //   761: istore_3
    //   762: iload_1
    //   763: ifne -> 907
    //   766: iload #11
    //   768: istore #12
    //   770: iload_1
    //   771: istore_2
    //   772: aload_0
    //   773: getfield mListDimensionBehaviors : [Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   776: iconst_0
    //   777: aaload
    //   778: getstatic android/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour.WRAP_CONTENT : Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   781: if_acmpne -> 835
    //   784: iload #11
    //   786: istore #12
    //   788: iload_1
    //   789: istore_2
    //   790: iload #8
    //   792: ifle -> 835
    //   795: iload #11
    //   797: istore #12
    //   799: iload_1
    //   800: istore_2
    //   801: aload_0
    //   802: invokevirtual getWidth : ()I
    //   805: iload #8
    //   807: if_icmple -> 835
    //   810: aload_0
    //   811: iconst_1
    //   812: putfield mWidthMeasuredTooSmall : Z
    //   815: aload_0
    //   816: getfield mListDimensionBehaviors : [Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   819: iconst_0
    //   820: getstatic android/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour.FIXED : Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   823: aastore
    //   824: aload_0
    //   825: iload #8
    //   827: invokevirtual setWidth : (I)V
    //   830: iconst_1
    //   831: istore #12
    //   833: iconst_1
    //   834: istore_2
    //   835: iload #12
    //   837: istore #13
    //   839: iload_2
    //   840: istore_3
    //   841: aload_0
    //   842: getfield mListDimensionBehaviors : [Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   845: iconst_1
    //   846: aaload
    //   847: getstatic android/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour.WRAP_CONTENT : Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   850: if_acmpne -> 907
    //   853: iload #12
    //   855: istore #13
    //   857: iload_2
    //   858: istore_3
    //   859: iload #9
    //   861: ifle -> 907
    //   864: iload #12
    //   866: istore #13
    //   868: iload_2
    //   869: istore_3
    //   870: aload_0
    //   871: invokevirtual getHeight : ()I
    //   874: iload #9
    //   876: if_icmple -> 907
    //   879: aload_0
    //   880: iconst_1
    //   881: putfield mHeightMeasuredTooSmall : Z
    //   884: aload_0
    //   885: getfield mListDimensionBehaviors : [Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   888: iconst_1
    //   889: getstatic android/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour.FIXED : Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   892: aastore
    //   893: aload_0
    //   894: iload #9
    //   896: invokevirtual setHeight : (I)V
    //   899: iconst_1
    //   900: istore #11
    //   902: iconst_1
    //   903: istore_1
    //   904: goto -> 913
    //   907: iload #13
    //   909: istore #11
    //   911: iload_3
    //   912: istore_1
    //   913: iload #5
    //   915: istore_2
    //   916: goto -> 243
    //   919: aload_0
    //   920: getfield mParent : Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   923: ifnull -> 991
    //   926: aload_0
    //   927: getfield mMinWidth : I
    //   930: aload_0
    //   931: invokevirtual getWidth : ()I
    //   934: invokestatic max : (II)I
    //   937: istore_2
    //   938: aload_0
    //   939: getfield mMinHeight : I
    //   942: aload_0
    //   943: invokevirtual getHeight : ()I
    //   946: invokestatic max : (II)I
    //   949: istore_3
    //   950: aload_0
    //   951: getfield mSnapshot : Landroid/support/constraint/solver/widgets/Snapshot;
    //   954: aload_0
    //   955: invokevirtual applyTo : (Landroid/support/constraint/solver/widgets/ConstraintWidget;)V
    //   958: aload_0
    //   959: iload_2
    //   960: aload_0
    //   961: getfield mPaddingLeft : I
    //   964: iadd
    //   965: aload_0
    //   966: getfield mPaddingRight : I
    //   969: iadd
    //   970: invokevirtual setWidth : (I)V
    //   973: aload_0
    //   974: iload_3
    //   975: aload_0
    //   976: getfield mPaddingTop : I
    //   979: iadd
    //   980: aload_0
    //   981: getfield mPaddingBottom : I
    //   984: iadd
    //   985: invokevirtual setHeight : (I)V
    //   988: goto -> 1003
    //   991: aload_0
    //   992: iload #6
    //   994: putfield mX : I
    //   997: aload_0
    //   998: iload #7
    //   1000: putfield mY : I
    //   1003: iload_1
    //   1004: ifeq -> 1023
    //   1007: aload_0
    //   1008: getfield mListDimensionBehaviors : [Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   1011: iconst_0
    //   1012: aload #16
    //   1014: aastore
    //   1015: aload_0
    //   1016: getfield mListDimensionBehaviors : [Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   1019: iconst_1
    //   1020: aload #15
    //   1022: aastore
    //   1023: aload_0
    //   1024: aload_0
    //   1025: getfield mSystem : Landroid/support/constraint/solver/LinearSystem;
    //   1028: invokevirtual getCache : ()Landroid/support/constraint/solver/Cache;
    //   1031: invokevirtual resetSolverVariables : (Landroid/support/constraint/solver/Cache;)V
    //   1034: aload_0
    //   1035: aload_0
    //   1036: invokevirtual getRootConstraintContainer : ()Landroid/support/constraint/solver/widgets/ConstraintWidgetContainer;
    //   1039: if_acmpne -> 1046
    //   1042: aload_0
    //   1043: invokevirtual updateDrawPosition : ()V
    //   1046: return
    // Exception table:
    //   from	to	target	type
    //   253	270	302	java/lang/Exception
    //   279	286	293	java/lang/Exception }
  
  public void optimize() {
    if (!optimizeFor(8))
      analyze(this.mOptimizationLevel); 
    solveGraph();
  }
  
  public boolean optimizeFor(int paramInt) { return ((this.mOptimizationLevel & paramInt) == paramInt); }
  
  public void optimizeForDimensions(int paramInt1, int paramInt2) {
    if (this.mListDimensionBehaviors[false] != ConstraintWidget.DimensionBehaviour.WRAP_CONTENT && this.mResolutionWidth != null)
      this.mResolutionWidth.resolve(paramInt1); 
    if (this.mListDimensionBehaviors[true] != ConstraintWidget.DimensionBehaviour.WRAP_CONTENT && this.mResolutionHeight != null)
      this.mResolutionHeight.resolve(paramInt2); 
  }
  
  public void optimizeReset() {
    int i = this.mChildren.size();
    resetResolutionNodes();
    for (byte b = 0; b < i; b++)
      ((ConstraintWidget)this.mChildren.get(b)).resetResolutionNodes(); 
  }
  
  public void preOptimize() {
    optimizeReset();
    analyze(this.mOptimizationLevel);
  }
  
  public void reset() {
    this.mSystem.reset();
    this.mPaddingLeft = 0;
    this.mPaddingRight = 0;
    this.mPaddingTop = 0;
    this.mPaddingBottom = 0;
    super.reset();
  }
  
  public void resetGraph() {
    ResolutionAnchor resolutionAnchor1 = getAnchor(ConstraintAnchor.Type.LEFT).getResolutionNode();
    ResolutionAnchor resolutionAnchor2 = getAnchor(ConstraintAnchor.Type.TOP).getResolutionNode();
    resolutionAnchor1.invalidateAnchors();
    resolutionAnchor2.invalidateAnchors();
    resolutionAnchor1.resolve(null, 0.0F);
    resolutionAnchor2.resolve(null, 0.0F);
  }
  
  public void setOptimizationLevel(int paramInt) { this.mOptimizationLevel = paramInt; }
  
  public void setPadding(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.mPaddingLeft = paramInt1;
    this.mPaddingTop = paramInt2;
    this.mPaddingRight = paramInt3;
    this.mPaddingBottom = paramInt4;
  }
  
  public void setRtl(boolean paramBoolean) { this.mIsRtl = paramBoolean; }
  
  public void solveGraph() {
    ResolutionAnchor resolutionAnchor1 = getAnchor(ConstraintAnchor.Type.LEFT).getResolutionNode();
    ResolutionAnchor resolutionAnchor2 = getAnchor(ConstraintAnchor.Type.TOP).getResolutionNode();
    resolutionAnchor1.resolve(null, 0.0F);
    resolutionAnchor2.resolve(null, 0.0F);
  }
  
  public void updateChildrenFromSolver(LinearSystem paramLinearSystem, boolean[] paramArrayOfBoolean) {
    paramArrayOfBoolean[2] = false;
    updateFromSolver(paramLinearSystem);
    int i = this.mChildren.size();
    for (byte b = 0; b < i; b++) {
      ConstraintWidget constraintWidget = (ConstraintWidget)this.mChildren.get(b);
      constraintWidget.updateFromSolver(paramLinearSystem);
      if (constraintWidget.mListDimensionBehaviors[false] == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget.getWidth() < constraintWidget.getWrapWidth())
        paramArrayOfBoolean[2] = true; 
      if (constraintWidget.mListDimensionBehaviors[true] == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget.getHeight() < constraintWidget.getWrapHeight())
        paramArrayOfBoolean[2] = true; 
    } 
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/constraint/solver/widgets/ConstraintWidgetContainer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */