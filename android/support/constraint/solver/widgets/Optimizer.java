package android.support.constraint.solver.widgets;

import android.support.constraint.solver.LinearSystem;

public class Optimizer {
  static final int FLAG_CHAIN_DANGLING = 1;
  
  static final int FLAG_RECOMPUTE_BOUNDS = 2;
  
  static final int FLAG_USE_OPTIMIZE = 0;
  
  public static final int OPTIMIZATION_BARRIER = 2;
  
  public static final int OPTIMIZATION_CHAIN = 4;
  
  public static final int OPTIMIZATION_DIMENSIONS = 8;
  
  public static final int OPTIMIZATION_DIRECT = 1;
  
  public static final int OPTIMIZATION_NONE = 0;
  
  public static final int OPTIMIZATION_RATIO = 16;
  
  public static final int OPTIMIZATION_STANDARD = 3;
  
  static boolean[] flags = new boolean[3];
  
  static void analyze(int paramInt, ConstraintWidget paramConstraintWidget) {
    paramConstraintWidget.updateResolutionNodes();
    ResolutionAnchor resolutionAnchor1 = paramConstraintWidget.mLeft.getResolutionNode();
    ResolutionAnchor resolutionAnchor2 = paramConstraintWidget.mTop.getResolutionNode();
    ResolutionAnchor resolutionAnchor3 = paramConstraintWidget.mRight.getResolutionNode();
    ResolutionAnchor resolutionAnchor4 = paramConstraintWidget.mBottom.getResolutionNode();
    if ((paramInt & 0x8) == 8) {
      paramInt = 1;
    } else {
      paramInt = 0;
    } 
    if (resolutionAnchor1.type != 4 && resolutionAnchor3.type != 4)
      if (paramConstraintWidget.mListDimensionBehaviors[false] == ConstraintWidget.DimensionBehaviour.FIXED) {
        if (paramConstraintWidget.mLeft.mTarget == null && paramConstraintWidget.mRight.mTarget == null) {
          resolutionAnchor1.setType(1);
          resolutionAnchor3.setType(1);
          if (paramInt != 0) {
            resolutionAnchor3.dependsOn(resolutionAnchor1, 1, paramConstraintWidget.getResolutionWidth());
          } else {
            resolutionAnchor3.dependsOn(resolutionAnchor1, paramConstraintWidget.getWidth());
          } 
        } else if (paramConstraintWidget.mLeft.mTarget != null && paramConstraintWidget.mRight.mTarget == null) {
          resolutionAnchor1.setType(1);
          resolutionAnchor3.setType(1);
          if (paramInt != 0) {
            resolutionAnchor3.dependsOn(resolutionAnchor1, 1, paramConstraintWidget.getResolutionWidth());
          } else {
            resolutionAnchor3.dependsOn(resolutionAnchor1, paramConstraintWidget.getWidth());
          } 
        } else if (paramConstraintWidget.mLeft.mTarget == null && paramConstraintWidget.mRight.mTarget != null) {
          resolutionAnchor1.setType(1);
          resolutionAnchor3.setType(1);
          resolutionAnchor1.dependsOn(resolutionAnchor3, -paramConstraintWidget.getWidth());
          if (paramInt != 0) {
            resolutionAnchor1.dependsOn(resolutionAnchor3, -1, paramConstraintWidget.getResolutionWidth());
          } else {
            resolutionAnchor1.dependsOn(resolutionAnchor3, -paramConstraintWidget.getWidth());
          } 
        } else if (paramConstraintWidget.mLeft.mTarget != null && paramConstraintWidget.mRight.mTarget != null) {
          resolutionAnchor1.setType(2);
          resolutionAnchor3.setType(2);
          if (paramInt != 0) {
            paramConstraintWidget.getResolutionWidth().addDependent(resolutionAnchor1);
            paramConstraintWidget.getResolutionWidth().addDependent(resolutionAnchor3);
            resolutionAnchor1.setOpposite(resolutionAnchor3, -1, paramConstraintWidget.getResolutionWidth());
            resolutionAnchor3.setOpposite(resolutionAnchor1, 1, paramConstraintWidget.getResolutionWidth());
          } else {
            resolutionAnchor1.setOpposite(resolutionAnchor3, -paramConstraintWidget.getWidth());
            resolutionAnchor3.setOpposite(resolutionAnchor1, paramConstraintWidget.getWidth());
          } 
        } 
      } else if (paramConstraintWidget.mListDimensionBehaviors[false] == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && optimizableMatchConstraint(paramConstraintWidget, 0)) {
        int i = paramConstraintWidget.getWidth();
        resolutionAnchor1.setType(1);
        resolutionAnchor3.setType(1);
        if (paramConstraintWidget.mLeft.mTarget == null && paramConstraintWidget.mRight.mTarget == null) {
          if (paramInt != 0) {
            resolutionAnchor3.dependsOn(resolutionAnchor1, 1, paramConstraintWidget.getResolutionWidth());
          } else {
            resolutionAnchor3.dependsOn(resolutionAnchor1, i);
          } 
        } else if (paramConstraintWidget.mLeft.mTarget != null && paramConstraintWidget.mRight.mTarget == null) {
          if (paramInt != 0) {
            resolutionAnchor3.dependsOn(resolutionAnchor1, 1, paramConstraintWidget.getResolutionWidth());
          } else {
            resolutionAnchor3.dependsOn(resolutionAnchor1, i);
          } 
        } else if (paramConstraintWidget.mLeft.mTarget == null && paramConstraintWidget.mRight.mTarget != null) {
          if (paramInt != 0) {
            resolutionAnchor1.dependsOn(resolutionAnchor3, -1, paramConstraintWidget.getResolutionWidth());
          } else {
            resolutionAnchor1.dependsOn(resolutionAnchor3, -i);
          } 
        } else if (paramConstraintWidget.mLeft.mTarget != null && paramConstraintWidget.mRight.mTarget != null) {
          if (paramInt != 0) {
            paramConstraintWidget.getResolutionWidth().addDependent(resolutionAnchor1);
            paramConstraintWidget.getResolutionWidth().addDependent(resolutionAnchor3);
          } 
          if (paramConstraintWidget.mDimensionRatio == 0.0F) {
            resolutionAnchor1.setType(3);
            resolutionAnchor3.setType(3);
            resolutionAnchor1.setOpposite(resolutionAnchor3, 0.0F);
            resolutionAnchor3.setOpposite(resolutionAnchor1, 0.0F);
          } else {
            resolutionAnchor1.setType(2);
            resolutionAnchor3.setType(2);
            resolutionAnchor1.setOpposite(resolutionAnchor3, -i);
            resolutionAnchor3.setOpposite(resolutionAnchor1, i);
            paramConstraintWidget.setWidth(i);
          } 
        } 
      }  
    if (resolutionAnchor2.type != 4 && resolutionAnchor4.type != 4)
      if (paramConstraintWidget.mListDimensionBehaviors[true] == ConstraintWidget.DimensionBehaviour.FIXED) {
        if (paramConstraintWidget.mTop.mTarget == null && paramConstraintWidget.mBottom.mTarget == null) {
          resolutionAnchor2.setType(1);
          resolutionAnchor4.setType(1);
          if (paramInt != 0) {
            resolutionAnchor4.dependsOn(resolutionAnchor2, 1, paramConstraintWidget.getResolutionHeight());
          } else {
            resolutionAnchor4.dependsOn(resolutionAnchor2, paramConstraintWidget.getHeight());
          } 
          if (paramConstraintWidget.mBaseline.mTarget != null) {
            paramConstraintWidget.mBaseline.getResolutionNode().setType(1);
            resolutionAnchor2.dependsOn(1, paramConstraintWidget.mBaseline.getResolutionNode(), -paramConstraintWidget.mBaselineDistance);
            return;
          } 
        } else if (paramConstraintWidget.mTop.mTarget != null && paramConstraintWidget.mBottom.mTarget == null) {
          resolutionAnchor2.setType(1);
          resolutionAnchor4.setType(1);
          if (paramInt != 0) {
            resolutionAnchor4.dependsOn(resolutionAnchor2, 1, paramConstraintWidget.getResolutionHeight());
          } else {
            resolutionAnchor4.dependsOn(resolutionAnchor2, paramConstraintWidget.getHeight());
          } 
          if (paramConstraintWidget.mBaselineDistance > 0) {
            paramConstraintWidget.mBaseline.getResolutionNode().dependsOn(1, resolutionAnchor2, paramConstraintWidget.mBaselineDistance);
            return;
          } 
        } else if (paramConstraintWidget.mTop.mTarget == null && paramConstraintWidget.mBottom.mTarget != null) {
          resolutionAnchor2.setType(1);
          resolutionAnchor4.setType(1);
          if (paramInt != 0) {
            resolutionAnchor2.dependsOn(resolutionAnchor4, -1, paramConstraintWidget.getResolutionHeight());
          } else {
            resolutionAnchor2.dependsOn(resolutionAnchor4, -paramConstraintWidget.getHeight());
          } 
          if (paramConstraintWidget.mBaselineDistance > 0) {
            paramConstraintWidget.mBaseline.getResolutionNode().dependsOn(1, resolutionAnchor2, paramConstraintWidget.mBaselineDistance);
            return;
          } 
        } else if (paramConstraintWidget.mTop.mTarget != null && paramConstraintWidget.mBottom.mTarget != null) {
          resolutionAnchor2.setType(2);
          resolutionAnchor4.setType(2);
          if (paramInt != 0) {
            resolutionAnchor2.setOpposite(resolutionAnchor4, -1, paramConstraintWidget.getResolutionHeight());
            resolutionAnchor4.setOpposite(resolutionAnchor2, 1, paramConstraintWidget.getResolutionHeight());
            paramConstraintWidget.getResolutionHeight().addDependent(resolutionAnchor2);
            paramConstraintWidget.getResolutionWidth().addDependent(resolutionAnchor4);
          } else {
            resolutionAnchor2.setOpposite(resolutionAnchor4, -paramConstraintWidget.getHeight());
            resolutionAnchor4.setOpposite(resolutionAnchor2, paramConstraintWidget.getHeight());
          } 
          if (paramConstraintWidget.mBaselineDistance > 0) {
            paramConstraintWidget.mBaseline.getResolutionNode().dependsOn(1, resolutionAnchor2, paramConstraintWidget.mBaselineDistance);
            return;
          } 
        } 
      } else if (paramConstraintWidget.mListDimensionBehaviors[true] == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && optimizableMatchConstraint(paramConstraintWidget, 1)) {
        int i = paramConstraintWidget.getHeight();
        resolutionAnchor2.setType(1);
        resolutionAnchor4.setType(1);
        if (paramConstraintWidget.mTop.mTarget == null && paramConstraintWidget.mBottom.mTarget == null) {
          if (paramInt != 0) {
            resolutionAnchor4.dependsOn(resolutionAnchor2, 1, paramConstraintWidget.getResolutionHeight());
            return;
          } 
          resolutionAnchor4.dependsOn(resolutionAnchor2, i);
          return;
        } 
        if (paramConstraintWidget.mTop.mTarget != null && paramConstraintWidget.mBottom.mTarget == null) {
          if (paramInt != 0) {
            resolutionAnchor4.dependsOn(resolutionAnchor2, 1, paramConstraintWidget.getResolutionHeight());
            return;
          } 
          resolutionAnchor4.dependsOn(resolutionAnchor2, i);
          return;
        } 
        if (paramConstraintWidget.mTop.mTarget == null && paramConstraintWidget.mBottom.mTarget != null) {
          if (paramInt != 0) {
            resolutionAnchor2.dependsOn(resolutionAnchor4, -1, paramConstraintWidget.getResolutionHeight());
            return;
          } 
          resolutionAnchor2.dependsOn(resolutionAnchor4, -i);
          return;
        } 
        if (paramConstraintWidget.mTop.mTarget != null && paramConstraintWidget.mBottom.mTarget != null) {
          if (paramInt != 0) {
            paramConstraintWidget.getResolutionHeight().addDependent(resolutionAnchor2);
            paramConstraintWidget.getResolutionWidth().addDependent(resolutionAnchor4);
          } 
          if (paramConstraintWidget.mDimensionRatio == 0.0F) {
            resolutionAnchor2.setType(3);
            resolutionAnchor4.setType(3);
            resolutionAnchor2.setOpposite(resolutionAnchor4, 0.0F);
            resolutionAnchor4.setOpposite(resolutionAnchor2, 0.0F);
            return;
          } 
          resolutionAnchor2.setType(2);
          resolutionAnchor4.setType(2);
          resolutionAnchor2.setOpposite(resolutionAnchor4, -i);
          resolutionAnchor4.setOpposite(resolutionAnchor2, i);
          paramConstraintWidget.setHeight(i);
          if (paramConstraintWidget.mBaselineDistance > 0)
            paramConstraintWidget.mBaseline.getResolutionNode().dependsOn(1, resolutionAnchor2, paramConstraintWidget.mBaselineDistance); 
        } 
      }  
  }
  
  static boolean applyChainOptimized(ConstraintWidgetContainer paramConstraintWidgetContainer, LinearSystem paramLinearSystem, int paramInt1, int paramInt2, ConstraintWidget paramConstraintWidget) { // Byte code:
    //   0: aload_0
    //   1: getfield mListDimensionBehaviors : [Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   4: iload_2
    //   5: aaload
    //   6: astore #19
    //   8: getstatic android/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour.WRAP_CONTENT : Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   11: astore #19
    //   13: iload_2
    //   14: ifne -> 117
    //   17: aload_0
    //   18: invokevirtual isRtl : ()Z
    //   21: ifeq -> 117
    //   24: aload #4
    //   26: astore_0
    //   27: iconst_0
    //   28: istore #11
    //   30: aload_0
    //   31: astore #19
    //   33: iload #11
    //   35: ifne -> 121
    //   38: aload_0
    //   39: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   42: iload_3
    //   43: iconst_1
    //   44: iadd
    //   45: aaload
    //   46: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   49: astore #19
    //   51: aload #19
    //   53: ifnull -> 97
    //   56: aload #19
    //   58: getfield mOwner : Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   61: astore #20
    //   63: aload #20
    //   65: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   68: iload_3
    //   69: aaload
    //   70: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   73: ifnull -> 97
    //   76: aload #20
    //   78: astore #19
    //   80: aload #20
    //   82: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   85: iload_3
    //   86: aaload
    //   87: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   90: getfield mOwner : Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   93: aload_0
    //   94: if_acmpeq -> 100
    //   97: aconst_null
    //   98: astore #19
    //   100: aload #19
    //   102: ifnull -> 111
    //   105: aload #19
    //   107: astore_0
    //   108: goto -> 30
    //   111: iconst_1
    //   112: istore #11
    //   114: goto -> 30
    //   117: aload #4
    //   119: astore #19
    //   121: iload_2
    //   122: ifne -> 197
    //   125: aload #19
    //   127: getfield mHorizontalChainStyle : I
    //   130: ifne -> 139
    //   133: iconst_1
    //   134: istore #11
    //   136: goto -> 142
    //   139: iconst_0
    //   140: istore #11
    //   142: aload #19
    //   144: getfield mHorizontalChainStyle : I
    //   147: iconst_1
    //   148: if_icmpne -> 157
    //   151: iconst_1
    //   152: istore #12
    //   154: goto -> 160
    //   157: iconst_0
    //   158: istore #12
    //   160: iload #11
    //   162: istore #14
    //   164: iload #12
    //   166: istore #13
    //   168: aload #19
    //   170: getfield mHorizontalChainStyle : I
    //   173: iconst_2
    //   174: if_icmpne -> 183
    //   177: iconst_1
    //   178: istore #15
    //   180: goto -> 252
    //   183: iconst_0
    //   184: istore #15
    //   186: iload #14
    //   188: istore #11
    //   190: iload #13
    //   192: istore #12
    //   194: goto -> 252
    //   197: aload #19
    //   199: getfield mVerticalChainStyle : I
    //   202: ifne -> 211
    //   205: iconst_1
    //   206: istore #11
    //   208: goto -> 214
    //   211: iconst_0
    //   212: istore #11
    //   214: aload #19
    //   216: getfield mVerticalChainStyle : I
    //   219: iconst_1
    //   220: if_icmpne -> 229
    //   223: iconst_1
    //   224: istore #12
    //   226: goto -> 232
    //   229: iconst_0
    //   230: istore #12
    //   232: iload #11
    //   234: istore #14
    //   236: iload #12
    //   238: istore #13
    //   240: aload #19
    //   242: getfield mVerticalChainStyle : I
    //   245: iconst_2
    //   246: if_icmpne -> 183
    //   249: goto -> 177
    //   252: aload #4
    //   254: astore #22
    //   256: aconst_null
    //   257: astore #25
    //   259: aload #25
    //   261: astore_0
    //   262: aload_0
    //   263: astore #19
    //   265: aload #19
    //   267: astore #20
    //   269: iconst_0
    //   270: istore #16
    //   272: iconst_0
    //   273: istore #17
    //   275: iconst_0
    //   276: istore #13
    //   278: fconst_0
    //   279: fstore #9
    //   281: fconst_0
    //   282: fstore #8
    //   284: fconst_0
    //   285: fstore #7
    //   287: aload_0
    //   288: astore #21
    //   290: aload #22
    //   292: astore_0
    //   293: iload #16
    //   295: ifne -> 738
    //   298: aload_0
    //   299: getfield mListNextVisibleWidget : [Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   302: iload_2
    //   303: aconst_null
    //   304: aastore
    //   305: aload #21
    //   307: astore #23
    //   309: aload #19
    //   311: astore #22
    //   313: iload #17
    //   315: istore #14
    //   317: fload #9
    //   319: fstore #5
    //   321: fload #8
    //   323: fstore #6
    //   325: aload_0
    //   326: invokevirtual getVisibility : ()I
    //   329: bipush #8
    //   331: if_icmpeq -> 452
    //   334: aload #21
    //   336: ifnull -> 347
    //   339: aload #21
    //   341: getfield mListNextVisibleWidget : [Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   344: iload_2
    //   345: aload_0
    //   346: aastore
    //   347: aload #19
    //   349: astore #21
    //   351: aload #19
    //   353: ifnonnull -> 359
    //   356: aload_0
    //   357: astore #21
    //   359: iload #17
    //   361: iconst_1
    //   362: iadd
    //   363: istore #14
    //   365: iload_2
    //   366: ifne -> 382
    //   369: fload #9
    //   371: aload_0
    //   372: invokevirtual getWidth : ()I
    //   375: i2f
    //   376: fadd
    //   377: fstore #6
    //   379: goto -> 392
    //   382: fload #9
    //   384: aload_0
    //   385: invokevirtual getHeight : ()I
    //   388: i2f
    //   389: fadd
    //   390: fstore #6
    //   392: fload #6
    //   394: fstore #5
    //   396: aload_0
    //   397: aload #21
    //   399: if_acmpeq -> 417
    //   402: fload #6
    //   404: aload_0
    //   405: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   408: iload_3
    //   409: aaload
    //   410: invokevirtual getMargin : ()I
    //   413: i2f
    //   414: fadd
    //   415: fstore #5
    //   417: fload #8
    //   419: aload_0
    //   420: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   423: iload_3
    //   424: aaload
    //   425: invokevirtual getMargin : ()I
    //   428: i2f
    //   429: fadd
    //   430: aload_0
    //   431: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   434: iload_3
    //   435: iconst_1
    //   436: iadd
    //   437: aaload
    //   438: invokevirtual getMargin : ()I
    //   441: i2f
    //   442: fadd
    //   443: fstore #6
    //   445: aload_0
    //   446: astore #23
    //   448: aload #21
    //   450: astore #22
    //   452: aload_0
    //   453: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   456: iload_3
    //   457: aaload
    //   458: astore #19
    //   460: aload_0
    //   461: getfield mListNextMatchConstraintsWidget : [Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   464: iload_2
    //   465: aconst_null
    //   466: aastore
    //   467: aload #25
    //   469: astore #21
    //   471: iload #13
    //   473: istore #18
    //   475: fload #7
    //   477: fstore #10
    //   479: aload #20
    //   481: astore #24
    //   483: aload_0
    //   484: invokevirtual getVisibility : ()I
    //   487: bipush #8
    //   489: if_icmpeq -> 623
    //   492: aload #25
    //   494: astore #21
    //   496: iload #13
    //   498: istore #18
    //   500: fload #7
    //   502: fstore #10
    //   504: aload #20
    //   506: astore #24
    //   508: aload_0
    //   509: getfield mListDimensionBehaviors : [Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   512: iload_2
    //   513: aaload
    //   514: getstatic android/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour.MATCH_CONSTRAINT : Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   517: if_acmpne -> 623
    //   520: iload #13
    //   522: iconst_1
    //   523: iadd
    //   524: istore #18
    //   526: iload_2
    //   527: ifne -> 555
    //   530: aload_0
    //   531: getfield mMatchConstraintDefaultWidth : I
    //   534: ifeq -> 539
    //   537: iconst_0
    //   538: ireturn
    //   539: aload_0
    //   540: getfield mMatchConstraintMinWidth : I
    //   543: ifne -> 553
    //   546: aload_0
    //   547: getfield mMatchConstraintMaxWidth : I
    //   550: ifeq -> 581
    //   553: iconst_0
    //   554: ireturn
    //   555: aload_0
    //   556: getfield mMatchConstraintDefaultHeight : I
    //   559: ifeq -> 564
    //   562: iconst_0
    //   563: ireturn
    //   564: aload_0
    //   565: getfield mMatchConstraintMinHeight : I
    //   568: ifne -> 621
    //   571: aload_0
    //   572: getfield mMatchConstraintMaxHeight : I
    //   575: ifeq -> 581
    //   578: goto -> 621
    //   581: fload #7
    //   583: aload_0
    //   584: getfield mWeight : [F
    //   587: iload_2
    //   588: faload
    //   589: fadd
    //   590: fstore #10
    //   592: aload #20
    //   594: ifnonnull -> 603
    //   597: aload_0
    //   598: astore #20
    //   600: goto -> 611
    //   603: aload #25
    //   605: getfield mListNextMatchConstraintsWidget : [Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   608: iload_2
    //   609: aload_0
    //   610: aastore
    //   611: aload_0
    //   612: astore #21
    //   614: aload #20
    //   616: astore #24
    //   618: goto -> 623
    //   621: iconst_0
    //   622: ireturn
    //   623: aload_0
    //   624: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   627: iload_3
    //   628: iconst_1
    //   629: iadd
    //   630: aaload
    //   631: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   634: astore #19
    //   636: aload #19
    //   638: ifnull -> 682
    //   641: aload #19
    //   643: getfield mOwner : Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   646: astore #20
    //   648: aload #20
    //   650: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   653: iload_3
    //   654: aaload
    //   655: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   658: ifnull -> 682
    //   661: aload #20
    //   663: astore #19
    //   665: aload #20
    //   667: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   670: iload_3
    //   671: aaload
    //   672: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   675: getfield mOwner : Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   678: aload_0
    //   679: if_acmpeq -> 685
    //   682: aconst_null
    //   683: astore #19
    //   685: aload #19
    //   687: ifnull -> 696
    //   690: aload #19
    //   692: astore_0
    //   693: goto -> 699
    //   696: iconst_1
    //   697: istore #16
    //   699: aload #21
    //   701: astore #25
    //   703: aload #23
    //   705: astore #21
    //   707: aload #22
    //   709: astore #19
    //   711: iload #14
    //   713: istore #17
    //   715: iload #18
    //   717: istore #13
    //   719: fload #5
    //   721: fstore #9
    //   723: fload #6
    //   725: fstore #8
    //   727: fload #10
    //   729: fstore #7
    //   731: aload #24
    //   733: astore #20
    //   735: goto -> 293
    //   738: aload #4
    //   740: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   743: iload_3
    //   744: aaload
    //   745: invokevirtual getResolutionNode : ()Landroid/support/constraint/solver/widgets/ResolutionAnchor;
    //   748: astore #20
    //   750: aload_0
    //   751: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   754: astore #22
    //   756: iload_3
    //   757: iconst_1
    //   758: iadd
    //   759: istore #14
    //   761: aload #22
    //   763: iload #14
    //   765: aaload
    //   766: invokevirtual getResolutionNode : ()Landroid/support/constraint/solver/widgets/ResolutionAnchor;
    //   769: astore #22
    //   771: aload #20
    //   773: getfield target : Landroid/support/constraint/solver/widgets/ResolutionAnchor;
    //   776: ifnull -> 1977
    //   779: aload #22
    //   781: getfield target : Landroid/support/constraint/solver/widgets/ResolutionAnchor;
    //   784: ifnonnull -> 790
    //   787: goto -> 1977
    //   790: aload #20
    //   792: getfield target : Landroid/support/constraint/solver/widgets/ResolutionAnchor;
    //   795: getfield state : I
    //   798: iconst_1
    //   799: if_icmpeq -> 816
    //   802: aload #22
    //   804: getfield target : Landroid/support/constraint/solver/widgets/ResolutionAnchor;
    //   807: getfield state : I
    //   810: iconst_1
    //   811: if_icmpeq -> 816
    //   814: iconst_0
    //   815: ireturn
    //   816: iload #13
    //   818: ifle -> 830
    //   821: iload #13
    //   823: iload #17
    //   825: if_icmpeq -> 830
    //   828: iconst_0
    //   829: ireturn
    //   830: iload #15
    //   832: ifne -> 854
    //   835: iload #11
    //   837: ifne -> 854
    //   840: iload #12
    //   842: ifeq -> 848
    //   845: goto -> 854
    //   848: fconst_0
    //   849: fstore #5
    //   851: goto -> 904
    //   854: aload #19
    //   856: ifnull -> 875
    //   859: aload #19
    //   861: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   864: iload_3
    //   865: aaload
    //   866: invokevirtual getMargin : ()I
    //   869: i2f
    //   870: fstore #6
    //   872: goto -> 878
    //   875: fconst_0
    //   876: fstore #6
    //   878: fload #6
    //   880: fstore #5
    //   882: aload #21
    //   884: ifnull -> 904
    //   887: fload #6
    //   889: aload #21
    //   891: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   894: iload #14
    //   896: aaload
    //   897: invokevirtual getMargin : ()I
    //   900: i2f
    //   901: fadd
    //   902: fstore #5
    //   904: aload #20
    //   906: getfield target : Landroid/support/constraint/solver/widgets/ResolutionAnchor;
    //   909: getfield resolvedOffset : F
    //   912: fstore #10
    //   914: aload #22
    //   916: getfield target : Landroid/support/constraint/solver/widgets/ResolutionAnchor;
    //   919: getfield resolvedOffset : F
    //   922: fstore #6
    //   924: fload #10
    //   926: fload #6
    //   928: fcmpg
    //   929: ifge -> 945
    //   932: fload #6
    //   934: fload #10
    //   936: fsub
    //   937: fload #9
    //   939: fsub
    //   940: fstore #6
    //   942: goto -> 955
    //   945: fload #10
    //   947: fload #6
    //   949: fsub
    //   950: fload #9
    //   952: fsub
    //   953: fstore #6
    //   955: iload #13
    //   957: ifle -> 1357
    //   960: iload #13
    //   962: iload #17
    //   964: if_icmpne -> 1357
    //   967: aload_0
    //   968: invokevirtual getParent : ()Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   971: ifnull -> 991
    //   974: aload_0
    //   975: invokevirtual getParent : ()Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   978: getfield mListDimensionBehaviors : [Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   981: iload_2
    //   982: aaload
    //   983: getstatic android/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour.WRAP_CONTENT : Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   986: if_acmpne -> 991
    //   989: iconst_0
    //   990: ireturn
    //   991: fload #6
    //   993: fload #9
    //   995: fadd
    //   996: fload #8
    //   998: fsub
    //   999: fstore #9
    //   1001: fload #9
    //   1003: fstore #6
    //   1005: iload #11
    //   1007: ifeq -> 1020
    //   1010: fload #9
    //   1012: fload #8
    //   1014: fload #5
    //   1016: fsub
    //   1017: fsub
    //   1018: fstore #6
    //   1020: aload #20
    //   1022: astore_0
    //   1023: fload #10
    //   1025: fstore #5
    //   1027: aload #19
    //   1029: astore #4
    //   1031: iload #11
    //   1033: ifeq -> 1101
    //   1036: fload #10
    //   1038: aload #19
    //   1040: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1043: iload #14
    //   1045: aaload
    //   1046: invokevirtual getMargin : ()I
    //   1049: i2f
    //   1050: fadd
    //   1051: fstore #8
    //   1053: aload #19
    //   1055: getfield mListNextVisibleWidget : [Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   1058: iload_2
    //   1059: aaload
    //   1060: astore #22
    //   1062: aload #20
    //   1064: astore_0
    //   1065: fload #8
    //   1067: fstore #5
    //   1069: aload #19
    //   1071: astore #4
    //   1073: aload #22
    //   1075: ifnull -> 1101
    //   1078: fload #8
    //   1080: aload #22
    //   1082: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1085: iload_3
    //   1086: aaload
    //   1087: invokevirtual getMargin : ()I
    //   1090: i2f
    //   1091: fadd
    //   1092: fstore #5
    //   1094: aload #19
    //   1096: astore #4
    //   1098: aload #20
    //   1100: astore_0
    //   1101: aload #4
    //   1103: ifnull -> 1355
    //   1106: getstatic android/support/constraint/solver/LinearSystem.sMetrics : Landroid/support/constraint/solver/Metrics;
    //   1109: ifnull -> 1166
    //   1112: getstatic android/support/constraint/solver/LinearSystem.sMetrics : Landroid/support/constraint/solver/Metrics;
    //   1115: astore #19
    //   1117: aload #19
    //   1119: aload #19
    //   1121: getfield nonresolvedWidgets : J
    //   1124: lconst_1
    //   1125: lsub
    //   1126: putfield nonresolvedWidgets : J
    //   1129: getstatic android/support/constraint/solver/LinearSystem.sMetrics : Landroid/support/constraint/solver/Metrics;
    //   1132: astore #19
    //   1134: aload #19
    //   1136: aload #19
    //   1138: getfield resolvedWidgets : J
    //   1141: lconst_1
    //   1142: ladd
    //   1143: putfield resolvedWidgets : J
    //   1146: getstatic android/support/constraint/solver/LinearSystem.sMetrics : Landroid/support/constraint/solver/Metrics;
    //   1149: astore #19
    //   1151: aload #19
    //   1153: aload #19
    //   1155: getfield chainConnectionResolved : J
    //   1158: lconst_1
    //   1159: ladd
    //   1160: putfield chainConnectionResolved : J
    //   1163: goto -> 1166
    //   1166: aload #4
    //   1168: getfield mListNextVisibleWidget : [Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   1171: iload_2
    //   1172: aaload
    //   1173: astore #19
    //   1175: aload #19
    //   1177: ifnonnull -> 1193
    //   1180: aload #4
    //   1182: aload #21
    //   1184: if_acmpne -> 1190
    //   1187: goto -> 1193
    //   1190: goto -> 1348
    //   1193: fload #6
    //   1195: iload #13
    //   1197: i2f
    //   1198: fdiv
    //   1199: fstore #8
    //   1201: fload #7
    //   1203: fconst_0
    //   1204: fcmpl
    //   1205: ifle -> 1223
    //   1208: aload #4
    //   1210: getfield mWeight : [F
    //   1213: iload_2
    //   1214: faload
    //   1215: fload #6
    //   1217: fmul
    //   1218: fload #7
    //   1220: fdiv
    //   1221: fstore #8
    //   1223: fload #5
    //   1225: aload #4
    //   1227: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1230: iload_3
    //   1231: aaload
    //   1232: invokevirtual getMargin : ()I
    //   1235: i2f
    //   1236: fadd
    //   1237: fstore #5
    //   1239: aload #4
    //   1241: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1244: iload_3
    //   1245: aaload
    //   1246: invokevirtual getResolutionNode : ()Landroid/support/constraint/solver/widgets/ResolutionAnchor;
    //   1249: astore #22
    //   1251: aload_0
    //   1252: astore #20
    //   1254: aload #22
    //   1256: aload #20
    //   1258: getfield resolvedTarget : Landroid/support/constraint/solver/widgets/ResolutionAnchor;
    //   1261: fload #5
    //   1263: invokevirtual resolve : (Landroid/support/constraint/solver/widgets/ResolutionAnchor;F)V
    //   1266: aload #4
    //   1268: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1271: iload #14
    //   1273: aaload
    //   1274: invokevirtual getResolutionNode : ()Landroid/support/constraint/solver/widgets/ResolutionAnchor;
    //   1277: astore #22
    //   1279: aload #20
    //   1281: getfield resolvedTarget : Landroid/support/constraint/solver/widgets/ResolutionAnchor;
    //   1284: astore #20
    //   1286: fload #5
    //   1288: fload #8
    //   1290: fadd
    //   1291: fstore #5
    //   1293: aload #22
    //   1295: aload #20
    //   1297: fload #5
    //   1299: invokevirtual resolve : (Landroid/support/constraint/solver/widgets/ResolutionAnchor;F)V
    //   1302: aload #4
    //   1304: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1307: iload_3
    //   1308: aaload
    //   1309: invokevirtual getResolutionNode : ()Landroid/support/constraint/solver/widgets/ResolutionAnchor;
    //   1312: aload_1
    //   1313: invokevirtual addResolvedValue : (Landroid/support/constraint/solver/LinearSystem;)V
    //   1316: aload #4
    //   1318: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1321: iload #14
    //   1323: aaload
    //   1324: invokevirtual getResolutionNode : ()Landroid/support/constraint/solver/widgets/ResolutionAnchor;
    //   1327: aload_1
    //   1328: invokevirtual addResolvedValue : (Landroid/support/constraint/solver/LinearSystem;)V
    //   1331: fload #5
    //   1333: aload #4
    //   1335: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1338: iload #14
    //   1340: aaload
    //   1341: invokevirtual getMargin : ()I
    //   1344: i2f
    //   1345: fadd
    //   1346: fstore #5
    //   1348: aload #19
    //   1350: astore #4
    //   1352: goto -> 1101
    //   1355: iconst_1
    //   1356: ireturn
    //   1357: fload #6
    //   1359: fload #9
    //   1361: fcmpg
    //   1362: ifge -> 1367
    //   1365: iconst_0
    //   1366: ireturn
    //   1367: iload #15
    //   1369: ifeq -> 1617
    //   1372: fload #10
    //   1374: fload #6
    //   1376: fload #5
    //   1378: fsub
    //   1379: aload #4
    //   1381: invokevirtual getHorizontalBiasPercent : ()F
    //   1384: fmul
    //   1385: fadd
    //   1386: fstore #5
    //   1388: aload #19
    //   1390: ifnull -> 1615
    //   1393: getstatic android/support/constraint/solver/LinearSystem.sMetrics : Landroid/support/constraint/solver/Metrics;
    //   1396: ifnull -> 1441
    //   1399: getstatic android/support/constraint/solver/LinearSystem.sMetrics : Landroid/support/constraint/solver/Metrics;
    //   1402: astore_0
    //   1403: aload_0
    //   1404: aload_0
    //   1405: getfield nonresolvedWidgets : J
    //   1408: lconst_1
    //   1409: lsub
    //   1410: putfield nonresolvedWidgets : J
    //   1413: getstatic android/support/constraint/solver/LinearSystem.sMetrics : Landroid/support/constraint/solver/Metrics;
    //   1416: astore_0
    //   1417: aload_0
    //   1418: aload_0
    //   1419: getfield resolvedWidgets : J
    //   1422: lconst_1
    //   1423: ladd
    //   1424: putfield resolvedWidgets : J
    //   1427: getstatic android/support/constraint/solver/LinearSystem.sMetrics : Landroid/support/constraint/solver/Metrics;
    //   1430: astore_0
    //   1431: aload_0
    //   1432: aload_0
    //   1433: getfield chainConnectionResolved : J
    //   1436: lconst_1
    //   1437: ladd
    //   1438: putfield chainConnectionResolved : J
    //   1441: aload #19
    //   1443: getfield mListNextVisibleWidget : [Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   1446: iload_2
    //   1447: aaload
    //   1448: astore_0
    //   1449: aload_0
    //   1450: ifnonnull -> 1464
    //   1453: fload #5
    //   1455: fstore #6
    //   1457: aload #19
    //   1459: aload #21
    //   1461: if_acmpne -> 1605
    //   1464: iload_2
    //   1465: ifne -> 1479
    //   1468: aload #19
    //   1470: invokevirtual getWidth : ()I
    //   1473: i2f
    //   1474: fstore #6
    //   1476: goto -> 1487
    //   1479: aload #19
    //   1481: invokevirtual getHeight : ()I
    //   1484: i2f
    //   1485: fstore #6
    //   1487: fload #5
    //   1489: aload #19
    //   1491: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1494: iload_3
    //   1495: aaload
    //   1496: invokevirtual getMargin : ()I
    //   1499: i2f
    //   1500: fadd
    //   1501: fstore #5
    //   1503: aload #19
    //   1505: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1508: iload_3
    //   1509: aaload
    //   1510: invokevirtual getResolutionNode : ()Landroid/support/constraint/solver/widgets/ResolutionAnchor;
    //   1513: aload #20
    //   1515: getfield resolvedTarget : Landroid/support/constraint/solver/widgets/ResolutionAnchor;
    //   1518: fload #5
    //   1520: invokevirtual resolve : (Landroid/support/constraint/solver/widgets/ResolutionAnchor;F)V
    //   1523: aload #19
    //   1525: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1528: iload #14
    //   1530: aaload
    //   1531: invokevirtual getResolutionNode : ()Landroid/support/constraint/solver/widgets/ResolutionAnchor;
    //   1534: astore #4
    //   1536: aload #20
    //   1538: getfield resolvedTarget : Landroid/support/constraint/solver/widgets/ResolutionAnchor;
    //   1541: astore #22
    //   1543: fload #5
    //   1545: fload #6
    //   1547: fadd
    //   1548: fstore #5
    //   1550: aload #4
    //   1552: aload #22
    //   1554: fload #5
    //   1556: invokevirtual resolve : (Landroid/support/constraint/solver/widgets/ResolutionAnchor;F)V
    //   1559: aload #19
    //   1561: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1564: iload_3
    //   1565: aaload
    //   1566: invokevirtual getResolutionNode : ()Landroid/support/constraint/solver/widgets/ResolutionAnchor;
    //   1569: aload_1
    //   1570: invokevirtual addResolvedValue : (Landroid/support/constraint/solver/LinearSystem;)V
    //   1573: aload #19
    //   1575: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1578: iload #14
    //   1580: aaload
    //   1581: invokevirtual getResolutionNode : ()Landroid/support/constraint/solver/widgets/ResolutionAnchor;
    //   1584: aload_1
    //   1585: invokevirtual addResolvedValue : (Landroid/support/constraint/solver/LinearSystem;)V
    //   1588: fload #5
    //   1590: aload #19
    //   1592: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1595: iload #14
    //   1597: aaload
    //   1598: invokevirtual getMargin : ()I
    //   1601: i2f
    //   1602: fadd
    //   1603: fstore #6
    //   1605: aload_0
    //   1606: astore #19
    //   1608: fload #6
    //   1610: fstore #5
    //   1612: goto -> 1388
    //   1615: iconst_1
    //   1616: ireturn
    //   1617: iload #11
    //   1619: ifne -> 1627
    //   1622: iload #12
    //   1624: ifeq -> 1615
    //   1627: iload #11
    //   1629: ifeq -> 1642
    //   1632: fload #6
    //   1634: fload #5
    //   1636: fsub
    //   1637: fstore #7
    //   1639: goto -> 1658
    //   1642: fload #6
    //   1644: fstore #7
    //   1646: iload #12
    //   1648: ifeq -> 1658
    //   1651: fload #6
    //   1653: fload #5
    //   1655: fsub
    //   1656: fstore #7
    //   1658: fload #7
    //   1660: iload #17
    //   1662: iconst_1
    //   1663: iadd
    //   1664: i2f
    //   1665: fdiv
    //   1666: fstore #5
    //   1668: iload #12
    //   1670: ifeq -> 1698
    //   1673: iload #17
    //   1675: iconst_1
    //   1676: if_icmple -> 1692
    //   1679: fload #7
    //   1681: iload #17
    //   1683: iconst_1
    //   1684: isub
    //   1685: i2f
    //   1686: fdiv
    //   1687: fstore #5
    //   1689: goto -> 1698
    //   1692: fload #7
    //   1694: fconst_2
    //   1695: fdiv
    //   1696: fstore #5
    //   1698: fload #10
    //   1700: fload #5
    //   1702: fadd
    //   1703: fstore #6
    //   1705: fload #6
    //   1707: fstore #7
    //   1709: iload #12
    //   1711: ifeq -> 1740
    //   1714: fload #6
    //   1716: fstore #7
    //   1718: iload #17
    //   1720: iconst_1
    //   1721: if_icmple -> 1740
    //   1724: aload #19
    //   1726: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1729: iload_3
    //   1730: aaload
    //   1731: invokevirtual getMargin : ()I
    //   1734: i2f
    //   1735: fload #10
    //   1737: fadd
    //   1738: fstore #7
    //   1740: fload #7
    //   1742: fstore #6
    //   1744: aload #19
    //   1746: astore_0
    //   1747: iload #11
    //   1749: ifeq -> 1783
    //   1752: fload #7
    //   1754: fstore #6
    //   1756: aload #19
    //   1758: astore_0
    //   1759: aload #19
    //   1761: ifnull -> 1783
    //   1764: fload #7
    //   1766: aload #19
    //   1768: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1771: iload_3
    //   1772: aaload
    //   1773: invokevirtual getMargin : ()I
    //   1776: i2f
    //   1777: fadd
    //   1778: fstore #6
    //   1780: aload #19
    //   1782: astore_0
    //   1783: aload_0
    //   1784: ifnull -> 1615
    //   1787: getstatic android/support/constraint/solver/LinearSystem.sMetrics : Landroid/support/constraint/solver/Metrics;
    //   1790: ifnull -> 1844
    //   1793: getstatic android/support/constraint/solver/LinearSystem.sMetrics : Landroid/support/constraint/solver/Metrics;
    //   1796: astore #4
    //   1798: aload #4
    //   1800: aload #4
    //   1802: getfield nonresolvedWidgets : J
    //   1805: lconst_1
    //   1806: lsub
    //   1807: putfield nonresolvedWidgets : J
    //   1810: getstatic android/support/constraint/solver/LinearSystem.sMetrics : Landroid/support/constraint/solver/Metrics;
    //   1813: astore #4
    //   1815: aload #4
    //   1817: aload #4
    //   1819: getfield resolvedWidgets : J
    //   1822: lconst_1
    //   1823: ladd
    //   1824: putfield resolvedWidgets : J
    //   1827: getstatic android/support/constraint/solver/LinearSystem.sMetrics : Landroid/support/constraint/solver/Metrics;
    //   1830: astore #4
    //   1832: aload #4
    //   1834: aload #4
    //   1836: getfield chainConnectionResolved : J
    //   1839: lconst_1
    //   1840: ladd
    //   1841: putfield chainConnectionResolved : J
    //   1844: aload_0
    //   1845: getfield mListNextVisibleWidget : [Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   1848: iload_2
    //   1849: aaload
    //   1850: astore #4
    //   1852: aload #4
    //   1854: ifnonnull -> 1867
    //   1857: fload #6
    //   1859: fstore #7
    //   1861: aload_0
    //   1862: aload #21
    //   1864: if_acmpne -> 1967
    //   1867: iload_2
    //   1868: ifne -> 1881
    //   1871: aload_0
    //   1872: invokevirtual getWidth : ()I
    //   1875: i2f
    //   1876: fstore #7
    //   1878: goto -> 1888
    //   1881: aload_0
    //   1882: invokevirtual getHeight : ()I
    //   1885: i2f
    //   1886: fstore #7
    //   1888: aload_0
    //   1889: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1892: iload_3
    //   1893: aaload
    //   1894: invokevirtual getResolutionNode : ()Landroid/support/constraint/solver/widgets/ResolutionAnchor;
    //   1897: aload #20
    //   1899: getfield resolvedTarget : Landroid/support/constraint/solver/widgets/ResolutionAnchor;
    //   1902: fload #6
    //   1904: invokevirtual resolve : (Landroid/support/constraint/solver/widgets/ResolutionAnchor;F)V
    //   1907: aload_0
    //   1908: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1911: iload #14
    //   1913: aaload
    //   1914: invokevirtual getResolutionNode : ()Landroid/support/constraint/solver/widgets/ResolutionAnchor;
    //   1917: aload #20
    //   1919: getfield resolvedTarget : Landroid/support/constraint/solver/widgets/ResolutionAnchor;
    //   1922: fload #6
    //   1924: fload #7
    //   1926: fadd
    //   1927: invokevirtual resolve : (Landroid/support/constraint/solver/widgets/ResolutionAnchor;F)V
    //   1930: aload_0
    //   1931: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1934: iload_3
    //   1935: aaload
    //   1936: invokevirtual getResolutionNode : ()Landroid/support/constraint/solver/widgets/ResolutionAnchor;
    //   1939: aload_1
    //   1940: invokevirtual addResolvedValue : (Landroid/support/constraint/solver/LinearSystem;)V
    //   1943: aload_0
    //   1944: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1947: iload #14
    //   1949: aaload
    //   1950: invokevirtual getResolutionNode : ()Landroid/support/constraint/solver/widgets/ResolutionAnchor;
    //   1953: aload_1
    //   1954: invokevirtual addResolvedValue : (Landroid/support/constraint/solver/LinearSystem;)V
    //   1957: fload #6
    //   1959: fload #7
    //   1961: fload #5
    //   1963: fadd
    //   1964: fadd
    //   1965: fstore #7
    //   1967: aload #4
    //   1969: astore_0
    //   1970: fload #7
    //   1972: fstore #6
    //   1974: goto -> 1783
    //   1977: iconst_0
    //   1978: ireturn }
  
  static void checkMatchParent(ConstraintWidgetContainer paramConstraintWidgetContainer, LinearSystem paramLinearSystem, ConstraintWidget paramConstraintWidget) {
    if (paramConstraintWidgetContainer.mListDimensionBehaviors[false] != ConstraintWidget.DimensionBehaviour.WRAP_CONTENT && paramConstraintWidget.mListDimensionBehaviors[false] == ConstraintWidget.DimensionBehaviour.MATCH_PARENT) {
      int i = paramConstraintWidget.mLeft.mMargin;
      int j = paramConstraintWidgetContainer.getWidth() - paramConstraintWidget.mRight.mMargin;
      paramConstraintWidget.mLeft.mSolverVariable = paramLinearSystem.createObjectVariable(paramConstraintWidget.mLeft);
      paramConstraintWidget.mRight.mSolverVariable = paramLinearSystem.createObjectVariable(paramConstraintWidget.mRight);
      paramLinearSystem.addEquality(paramConstraintWidget.mLeft.mSolverVariable, i);
      paramLinearSystem.addEquality(paramConstraintWidget.mRight.mSolverVariable, j);
      paramConstraintWidget.mHorizontalResolution = 2;
      paramConstraintWidget.setHorizontalDimension(i, j);
    } 
    if (paramConstraintWidgetContainer.mListDimensionBehaviors[true] != ConstraintWidget.DimensionBehaviour.WRAP_CONTENT && paramConstraintWidget.mListDimensionBehaviors[true] == ConstraintWidget.DimensionBehaviour.MATCH_PARENT) {
      int i = paramConstraintWidget.mTop.mMargin;
      int j = paramConstraintWidgetContainer.getHeight() - paramConstraintWidget.mBottom.mMargin;
      paramConstraintWidget.mTop.mSolverVariable = paramLinearSystem.createObjectVariable(paramConstraintWidget.mTop);
      paramConstraintWidget.mBottom.mSolverVariable = paramLinearSystem.createObjectVariable(paramConstraintWidget.mBottom);
      paramLinearSystem.addEquality(paramConstraintWidget.mTop.mSolverVariable, i);
      paramLinearSystem.addEquality(paramConstraintWidget.mBottom.mSolverVariable, j);
      if (paramConstraintWidget.mBaselineDistance > 0 || paramConstraintWidget.getVisibility() == 8) {
        paramConstraintWidget.mBaseline.mSolverVariable = paramLinearSystem.createObjectVariable(paramConstraintWidget.mBaseline);
        paramLinearSystem.addEquality(paramConstraintWidget.mBaseline.mSolverVariable, paramConstraintWidget.mBaselineDistance + i);
      } 
      paramConstraintWidget.mVerticalResolution = 2;
      paramConstraintWidget.setVerticalDimension(i, j);
    } 
  }
  
  private static boolean optimizableMatchConstraint(ConstraintWidget paramConstraintWidget, int paramInt) {
    ConstraintWidget.DimensionBehaviour[] arrayOfDimensionBehaviour;
    if (paramConstraintWidget.mListDimensionBehaviors[paramInt] != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT)
      return false; 
    float f = paramConstraintWidget.mDimensionRatio;
    int i = 1;
    if (f != 0.0F) {
      arrayOfDimensionBehaviour = paramConstraintWidget.mListDimensionBehaviors;
      if (paramInt == 0) {
        paramInt = i;
      } else {
        paramInt = 0;
      } 
      return (arrayOfDimensionBehaviour[paramInt] == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) ? false : false;
    } 
    if (paramInt == 0) {
      if (arrayOfDimensionBehaviour.mMatchConstraintDefaultWidth != 0)
        return false; 
      if (arrayOfDimensionBehaviour.mMatchConstraintMinWidth != 0 || arrayOfDimensionBehaviour.mMatchConstraintMaxWidth != 0)
        return false; 
    } else {
      return (arrayOfDimensionBehaviour.mMatchConstraintDefaultHeight != 0) ? false : ((arrayOfDimensionBehaviour.mMatchConstraintMinHeight == 0) ? (!(arrayOfDimensionBehaviour.mMatchConstraintMaxHeight != 0)) : false);
    } 
    return true;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/constraint/solver/widgets/Optimizer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */