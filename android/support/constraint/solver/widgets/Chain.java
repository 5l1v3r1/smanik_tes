package android.support.constraint.solver.widgets;

import android.support.constraint.solver.LinearSystem;

class Chain {
  private static final boolean DEBUG = false;
  
  static void applyChainConstraints(ConstraintWidgetContainer paramConstraintWidgetContainer, LinearSystem paramLinearSystem, int paramInt) {
    ConstraintWidget[] arrayOfConstraintWidget;
    int i;
    byte b1;
    byte b2 = 0;
    if (paramInt == 0) {
      i = paramConstraintWidgetContainer.mHorizontalChainsSize;
      arrayOfConstraintWidget = paramConstraintWidgetContainer.mHorizontalChainsArray;
      b1 = 0;
    } else {
      b1 = 2;
      i = paramConstraintWidgetContainer.mVerticalChainsSize;
      arrayOfConstraintWidget = paramConstraintWidgetContainer.mVerticalChainsArray;
    } 
    while (b2 < i) {
      ConstraintWidget constraintWidget = arrayOfConstraintWidget[b2];
      if (paramConstraintWidgetContainer.optimizeFor(4)) {
        if (!Optimizer.applyChainOptimized(paramConstraintWidgetContainer, paramLinearSystem, paramInt, b1, constraintWidget))
          applyChainConstraints(paramConstraintWidgetContainer, paramLinearSystem, paramInt, b1, constraintWidget); 
      } else {
        applyChainConstraints(paramConstraintWidgetContainer, paramLinearSystem, paramInt, b1, constraintWidget);
      } 
      b2++;
    } 
  }
  
  static void applyChainConstraints(ConstraintWidgetContainer paramConstraintWidgetContainer, LinearSystem paramLinearSystem, int paramInt1, int paramInt2, ConstraintWidget paramConstraintWidget) { // Byte code:
    //   0: aload_0
    //   1: getfield mListDimensionBehaviors : [Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   4: iload_2
    //   5: aaload
    //   6: getstatic android/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour.WRAP_CONTENT : Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   9: if_acmpne -> 18
    //   12: iconst_1
    //   13: istore #12
    //   15: goto -> 21
    //   18: iconst_0
    //   19: istore #12
    //   21: iload_2
    //   22: ifne -> 130
    //   25: aload_0
    //   26: invokevirtual isRtl : ()Z
    //   29: ifeq -> 130
    //   32: aload #4
    //   34: astore #17
    //   36: iconst_0
    //   37: istore #8
    //   39: aload #17
    //   41: astore #16
    //   43: iload #8
    //   45: ifne -> 134
    //   48: aload #17
    //   50: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   53: iload_3
    //   54: iconst_1
    //   55: iadd
    //   56: aaload
    //   57: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   60: astore #16
    //   62: aload #16
    //   64: ifnull -> 109
    //   67: aload #16
    //   69: getfield mOwner : Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   72: astore #18
    //   74: aload #18
    //   76: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   79: iload_3
    //   80: aaload
    //   81: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   84: ifnull -> 109
    //   87: aload #18
    //   89: astore #16
    //   91: aload #18
    //   93: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   96: iload_3
    //   97: aaload
    //   98: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   101: getfield mOwner : Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   104: aload #17
    //   106: if_acmpeq -> 112
    //   109: aconst_null
    //   110: astore #16
    //   112: aload #16
    //   114: ifnull -> 124
    //   117: aload #16
    //   119: astore #17
    //   121: goto -> 39
    //   124: iconst_1
    //   125: istore #8
    //   127: goto -> 39
    //   130: aload #4
    //   132: astore #16
    //   134: iload_2
    //   135: ifne -> 246
    //   138: aload #16
    //   140: getfield mHorizontalChainStyle : I
    //   143: ifne -> 152
    //   146: iconst_1
    //   147: istore #8
    //   149: goto -> 155
    //   152: iconst_0
    //   153: istore #8
    //   155: aload #16
    //   157: getfield mHorizontalChainStyle : I
    //   160: iconst_1
    //   161: if_icmpne -> 170
    //   164: iconst_1
    //   165: istore #9
    //   167: goto -> 173
    //   170: iconst_0
    //   171: istore #9
    //   173: iload #8
    //   175: istore #10
    //   177: iload #9
    //   179: istore #11
    //   181: aload #16
    //   183: getfield mHorizontalChainStyle : I
    //   186: iconst_2
    //   187: if_icmpne -> 204
    //   190: iconst_1
    //   191: istore #13
    //   193: iload #8
    //   195: istore #10
    //   197: iload #9
    //   199: istore #11
    //   201: goto -> 207
    //   204: iconst_0
    //   205: istore #13
    //   207: aload #4
    //   209: astore #20
    //   211: aconst_null
    //   212: astore #23
    //   214: aload #23
    //   216: astore #19
    //   218: aload #19
    //   220: astore #17
    //   222: aload #17
    //   224: astore #18
    //   226: iconst_0
    //   227: istore #8
    //   229: iconst_0
    //   230: istore #9
    //   232: fconst_0
    //   233: fstore #5
    //   235: aload #16
    //   237: astore #22
    //   239: aload #20
    //   241: astore #16
    //   243: goto -> 301
    //   246: aload #16
    //   248: getfield mVerticalChainStyle : I
    //   251: ifne -> 260
    //   254: iconst_1
    //   255: istore #8
    //   257: goto -> 263
    //   260: iconst_0
    //   261: istore #8
    //   263: aload #16
    //   265: getfield mVerticalChainStyle : I
    //   268: iconst_1
    //   269: if_icmpne -> 278
    //   272: iconst_1
    //   273: istore #9
    //   275: goto -> 281
    //   278: iconst_0
    //   279: istore #9
    //   281: iload #8
    //   283: istore #10
    //   285: iload #9
    //   287: istore #11
    //   289: aload #16
    //   291: getfield mVerticalChainStyle : I
    //   294: iconst_2
    //   295: if_icmpne -> 204
    //   298: goto -> 190
    //   301: iload #8
    //   303: ifne -> 814
    //   306: aload #16
    //   308: getfield mListNextVisibleWidget : [Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   311: iload_2
    //   312: aconst_null
    //   313: aastore
    //   314: aload #17
    //   316: astore #21
    //   318: aload #18
    //   320: astore #20
    //   322: aload #16
    //   324: invokevirtual getVisibility : ()I
    //   327: bipush #8
    //   329: if_icmpeq -> 363
    //   332: aload #18
    //   334: ifnull -> 346
    //   337: aload #18
    //   339: getfield mListNextVisibleWidget : [Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   342: iload_2
    //   343: aload #16
    //   345: aastore
    //   346: aload #17
    //   348: astore #21
    //   350: aload #17
    //   352: ifnonnull -> 359
    //   355: aload #16
    //   357: astore #21
    //   359: aload #16
    //   361: astore #20
    //   363: aload #16
    //   365: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   368: iload_3
    //   369: aaload
    //   370: astore #17
    //   372: aload #17
    //   374: invokevirtual getMargin : ()I
    //   377: istore #15
    //   379: aload #17
    //   381: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   384: ifnull -> 424
    //   387: aload #16
    //   389: aload #4
    //   391: if_acmpeq -> 424
    //   394: iload #15
    //   396: istore #14
    //   398: aload #16
    //   400: invokevirtual getVisibility : ()I
    //   403: bipush #8
    //   405: if_icmpeq -> 428
    //   408: iload #15
    //   410: aload #17
    //   412: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   415: invokevirtual getMargin : ()I
    //   418: iadd
    //   419: istore #14
    //   421: goto -> 428
    //   424: iload #15
    //   426: istore #14
    //   428: iload #13
    //   430: ifeq -> 454
    //   433: aload #16
    //   435: aload #4
    //   437: if_acmpeq -> 454
    //   440: aload #16
    //   442: aload #21
    //   444: if_acmpeq -> 454
    //   447: bipush #6
    //   449: istore #15
    //   451: goto -> 457
    //   454: iconst_1
    //   455: istore #15
    //   457: aload #16
    //   459: aload #21
    //   461: if_acmpne -> 487
    //   464: aload_1
    //   465: aload #17
    //   467: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   470: aload #17
    //   472: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   475: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   478: iload #14
    //   480: iconst_5
    //   481: invokevirtual addGreaterThan : (Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;II)V
    //   484: goto -> 508
    //   487: aload_1
    //   488: aload #17
    //   490: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   493: aload #17
    //   495: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   498: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   501: iload #14
    //   503: bipush #6
    //   505: invokevirtual addGreaterThan : (Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;II)V
    //   508: aload_1
    //   509: aload #17
    //   511: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   514: aload #17
    //   516: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   519: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   522: iload #14
    //   524: iload #15
    //   526: invokevirtual addEquality : (Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;II)Landroid/support/constraint/solver/ArrayRow;
    //   529: pop
    //   530: aload #16
    //   532: getfield mListNextMatchConstraintsWidget : [Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   535: iload_2
    //   536: aconst_null
    //   537: aastore
    //   538: aload #23
    //   540: astore #24
    //   542: iload #9
    //   544: istore #14
    //   546: aload #19
    //   548: astore #18
    //   550: fload #5
    //   552: fstore #6
    //   554: aload #16
    //   556: invokevirtual getVisibility : ()I
    //   559: bipush #8
    //   561: if_icmpeq -> 674
    //   564: aload #23
    //   566: astore #24
    //   568: iload #9
    //   570: istore #14
    //   572: aload #19
    //   574: astore #18
    //   576: fload #5
    //   578: fstore #6
    //   580: aload #16
    //   582: getfield mListDimensionBehaviors : [Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   585: iload_2
    //   586: aaload
    //   587: getstatic android/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour.MATCH_CONSTRAINT : Landroid/support/constraint/solver/widgets/ConstraintWidget$DimensionBehaviour;
    //   590: if_acmpne -> 674
    //   593: iload #9
    //   595: iconst_1
    //   596: iadd
    //   597: istore #14
    //   599: fload #5
    //   601: aload #16
    //   603: getfield mWeight : [F
    //   606: iload_2
    //   607: faload
    //   608: fadd
    //   609: fstore #6
    //   611: aload #19
    //   613: ifnonnull -> 623
    //   616: aload #16
    //   618: astore #19
    //   620: goto -> 632
    //   623: aload #23
    //   625: getfield mListNextMatchConstraintsWidget : [Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   628: iload_2
    //   629: aload #16
    //   631: aastore
    //   632: iload #12
    //   634: ifeq -> 666
    //   637: aload_1
    //   638: aload #16
    //   640: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   643: iload_3
    //   644: iconst_1
    //   645: iadd
    //   646: aaload
    //   647: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   650: aload #16
    //   652: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   655: iload_3
    //   656: aaload
    //   657: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   660: iconst_0
    //   661: bipush #6
    //   663: invokevirtual addGreaterThan : (Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;II)V
    //   666: aload #16
    //   668: astore #24
    //   670: aload #19
    //   672: astore #18
    //   674: iload #12
    //   676: ifeq -> 708
    //   679: aload_1
    //   680: aload #16
    //   682: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   685: iload_3
    //   686: aaload
    //   687: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   690: aload_0
    //   691: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   694: iload_3
    //   695: aaload
    //   696: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   699: iconst_0
    //   700: bipush #6
    //   702: invokevirtual addGreaterThan : (Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;II)V
    //   705: goto -> 708
    //   708: aload #16
    //   710: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   713: iload_3
    //   714: iconst_1
    //   715: iadd
    //   716: aaload
    //   717: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   720: astore #17
    //   722: aload #17
    //   724: ifnull -> 769
    //   727: aload #17
    //   729: getfield mOwner : Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   732: astore #19
    //   734: aload #19
    //   736: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   739: iload_3
    //   740: aaload
    //   741: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   744: ifnull -> 769
    //   747: aload #19
    //   749: astore #17
    //   751: aload #19
    //   753: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   756: iload_3
    //   757: aaload
    //   758: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   761: getfield mOwner : Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   764: aload #16
    //   766: if_acmpeq -> 772
    //   769: aconst_null
    //   770: astore #17
    //   772: aload #17
    //   774: ifnull -> 784
    //   777: aload #17
    //   779: astore #16
    //   781: goto -> 787
    //   784: iconst_1
    //   785: istore #8
    //   787: aload #21
    //   789: astore #17
    //   791: aload #24
    //   793: astore #23
    //   795: iload #14
    //   797: istore #9
    //   799: aload #18
    //   801: astore #19
    //   803: aload #20
    //   805: astore #18
    //   807: fload #6
    //   809: fstore #5
    //   811: goto -> 301
    //   814: aload #18
    //   816: ifnull -> 885
    //   819: aload #16
    //   821: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   824: astore #20
    //   826: iload_3
    //   827: iconst_1
    //   828: iadd
    //   829: istore #8
    //   831: aload #20
    //   833: iload #8
    //   835: aaload
    //   836: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   839: ifnull -> 885
    //   842: aload #18
    //   844: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   847: iload #8
    //   849: aaload
    //   850: astore #20
    //   852: aload_1
    //   853: aload #20
    //   855: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   858: aload #16
    //   860: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   863: iload #8
    //   865: aaload
    //   866: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   869: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   872: aload #20
    //   874: invokevirtual getMargin : ()I
    //   877: ineg
    //   878: iconst_5
    //   879: invokevirtual addLowerThan : (Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;II)V
    //   882: goto -> 885
    //   885: iload #12
    //   887: ifeq -> 935
    //   890: aload_0
    //   891: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   894: astore_0
    //   895: iload_3
    //   896: iconst_1
    //   897: iadd
    //   898: istore #8
    //   900: aload_1
    //   901: aload_0
    //   902: iload #8
    //   904: aaload
    //   905: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   908: aload #16
    //   910: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   913: iload #8
    //   915: aaload
    //   916: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   919: aload #16
    //   921: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   924: iload #8
    //   926: aaload
    //   927: invokevirtual getMargin : ()I
    //   930: bipush #6
    //   932: invokevirtual addGreaterThan : (Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;II)V
    //   935: iload #9
    //   937: ifle -> 1141
    //   940: aload #19
    //   942: ifnull -> 1141
    //   945: aload #19
    //   947: getfield mListNextMatchConstraintsWidget : [Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   950: iload_2
    //   951: aaload
    //   952: astore_0
    //   953: aload_0
    //   954: ifnull -> 1135
    //   957: aload #19
    //   959: getfield mWeight : [F
    //   962: iload_2
    //   963: faload
    //   964: fstore #6
    //   966: aload_0
    //   967: getfield mWeight : [F
    //   970: iload_2
    //   971: faload
    //   972: fstore #7
    //   974: aload #19
    //   976: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   979: iload_3
    //   980: aaload
    //   981: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   984: astore #20
    //   986: aload #19
    //   988: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   991: astore #21
    //   993: iload_3
    //   994: iconst_1
    //   995: iadd
    //   996: istore #8
    //   998: aload #21
    //   1000: iload #8
    //   1002: aaload
    //   1003: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   1006: astore #21
    //   1008: aload_0
    //   1009: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1012: iload_3
    //   1013: aaload
    //   1014: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   1017: astore #23
    //   1019: aload_0
    //   1020: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1023: iload #8
    //   1025: aaload
    //   1026: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   1029: astore #24
    //   1031: iload_2
    //   1032: ifne -> 1051
    //   1035: aload #19
    //   1037: getfield mMatchConstraintDefaultWidth : I
    //   1040: istore #8
    //   1042: aload_0
    //   1043: getfield mMatchConstraintDefaultWidth : I
    //   1046: istore #9
    //   1048: goto -> 1064
    //   1051: aload #19
    //   1053: getfield mMatchConstraintDefaultHeight : I
    //   1056: istore #8
    //   1058: aload_0
    //   1059: getfield mMatchConstraintDefaultHeight : I
    //   1062: istore #9
    //   1064: iload #8
    //   1066: ifeq -> 1075
    //   1069: iload #8
    //   1071: iconst_3
    //   1072: if_icmpne -> 1089
    //   1075: iload #9
    //   1077: ifeq -> 1095
    //   1080: iload #9
    //   1082: iconst_3
    //   1083: if_icmpne -> 1089
    //   1086: goto -> 1095
    //   1089: iconst_0
    //   1090: istore #8
    //   1092: goto -> 1098
    //   1095: iconst_1
    //   1096: istore #8
    //   1098: iload #8
    //   1100: ifeq -> 1135
    //   1103: aload_1
    //   1104: invokevirtual createRow : ()Landroid/support/constraint/solver/ArrayRow;
    //   1107: astore #19
    //   1109: aload #19
    //   1111: fload #6
    //   1113: fload #5
    //   1115: fload #7
    //   1117: aload #20
    //   1119: aload #21
    //   1121: aload #23
    //   1123: aload #24
    //   1125: invokevirtual createRowEqualMatchDimensions : (FFFLandroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;)Landroid/support/constraint/solver/ArrayRow;
    //   1128: pop
    //   1129: aload_1
    //   1130: aload #19
    //   1132: invokevirtual addConstraint : (Landroid/support/constraint/solver/ArrayRow;)V
    //   1135: aload_0
    //   1136: astore #19
    //   1138: goto -> 940
    //   1141: aload #17
    //   1143: ifnull -> 1379
    //   1146: aload #17
    //   1148: aload #18
    //   1150: if_acmpeq -> 1158
    //   1153: iload #13
    //   1155: ifeq -> 1379
    //   1158: aload #4
    //   1160: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1163: iload_3
    //   1164: aaload
    //   1165: astore #20
    //   1167: aload #16
    //   1169: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1172: astore_0
    //   1173: iload_3
    //   1174: iconst_1
    //   1175: iadd
    //   1176: istore #8
    //   1178: aload_0
    //   1179: iload #8
    //   1181: aaload
    //   1182: astore #21
    //   1184: aload #4
    //   1186: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1189: iload_3
    //   1190: aaload
    //   1191: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1194: ifnull -> 1215
    //   1197: aload #4
    //   1199: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1202: iload_3
    //   1203: aaload
    //   1204: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1207: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   1210: astore #4
    //   1212: goto -> 1218
    //   1215: aconst_null
    //   1216: astore #4
    //   1218: aload #16
    //   1220: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1223: iload #8
    //   1225: aaload
    //   1226: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1229: ifnull -> 1251
    //   1232: aload #16
    //   1234: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1237: iload #8
    //   1239: aaload
    //   1240: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1243: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   1246: astore #19
    //   1248: goto -> 1254
    //   1251: aconst_null
    //   1252: astore #19
    //   1254: aload #17
    //   1256: aload #18
    //   1258: if_acmpne -> 1280
    //   1261: aload #17
    //   1263: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1266: iload_3
    //   1267: aaload
    //   1268: astore #20
    //   1270: aload #17
    //   1272: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1275: iload #8
    //   1277: aaload
    //   1278: astore #21
    //   1280: aload #4
    //   1282: ifnull -> 1367
    //   1285: aload #19
    //   1287: ifnull -> 1367
    //   1290: iload_2
    //   1291: ifne -> 1304
    //   1294: aload #22
    //   1296: getfield mHorizontalBiasPercent : F
    //   1299: fstore #5
    //   1301: goto -> 1311
    //   1304: aload #22
    //   1306: getfield mVerticalBiasPercent : F
    //   1309: fstore #5
    //   1311: aload #20
    //   1313: invokevirtual getMargin : ()I
    //   1316: istore_2
    //   1317: aload #18
    //   1319: astore_0
    //   1320: aload #18
    //   1322: ifnonnull -> 1328
    //   1325: aload #16
    //   1327: astore_0
    //   1328: aload_0
    //   1329: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1332: iload #8
    //   1334: aaload
    //   1335: invokevirtual getMargin : ()I
    //   1338: istore #8
    //   1340: aload_1
    //   1341: aload #20
    //   1343: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   1346: aload #4
    //   1348: iload_2
    //   1349: fload #5
    //   1351: aload #19
    //   1353: aload #21
    //   1355: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   1358: iload #8
    //   1360: iconst_5
    //   1361: invokevirtual addCentering : (Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;IFLandroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;II)V
    //   1364: goto -> 1370
    //   1367: aload #18
    //   1369: astore_0
    //   1370: aload_0
    //   1371: astore #19
    //   1373: aload #17
    //   1375: astore_0
    //   1376: goto -> 2377
    //   1379: iload #10
    //   1381: ifeq -> 1840
    //   1384: aload #17
    //   1386: ifnull -> 1840
    //   1389: aload #17
    //   1391: astore_0
    //   1392: aload_0
    //   1393: astore #20
    //   1395: aload #17
    //   1397: astore #19
    //   1399: aload #20
    //   1401: astore #17
    //   1403: aload_0
    //   1404: astore #22
    //   1406: aload #17
    //   1408: ifnull -> 1830
    //   1411: aload #17
    //   1413: getfield mListNextVisibleWidget : [Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   1416: iload_2
    //   1417: aaload
    //   1418: astore #21
    //   1420: aload #21
    //   1422: ifnonnull -> 1441
    //   1425: aload #17
    //   1427: aload #18
    //   1429: if_acmpne -> 1435
    //   1432: goto -> 1441
    //   1435: aload #21
    //   1437: astore_0
    //   1438: goto -> 1820
    //   1441: aload #17
    //   1443: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1446: iload_3
    //   1447: aaload
    //   1448: astore #23
    //   1450: aload #23
    //   1452: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   1455: astore #26
    //   1457: aload #23
    //   1459: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1462: ifnull -> 1478
    //   1465: aload #23
    //   1467: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1470: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   1473: astore #20
    //   1475: goto -> 1481
    //   1478: aconst_null
    //   1479: astore #20
    //   1481: aload #22
    //   1483: aload #17
    //   1485: if_acmpeq -> 1504
    //   1488: aload #22
    //   1490: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1493: iload_3
    //   1494: iconst_1
    //   1495: iadd
    //   1496: aaload
    //   1497: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   1500: astore_0
    //   1501: goto -> 1556
    //   1504: aload #20
    //   1506: astore_0
    //   1507: aload #17
    //   1509: aload #19
    //   1511: if_acmpne -> 1556
    //   1514: aload #20
    //   1516: astore_0
    //   1517: aload #22
    //   1519: aload #17
    //   1521: if_acmpne -> 1556
    //   1524: aload #4
    //   1526: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1529: iload_3
    //   1530: aaload
    //   1531: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1534: ifnull -> 1554
    //   1537: aload #4
    //   1539: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1542: iload_3
    //   1543: aaload
    //   1544: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1547: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   1550: astore_0
    //   1551: goto -> 1556
    //   1554: aconst_null
    //   1555: astore_0
    //   1556: aload #23
    //   1558: invokevirtual getMargin : ()I
    //   1561: istore #12
    //   1563: aload #17
    //   1565: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1568: astore #20
    //   1570: iload_3
    //   1571: iconst_1
    //   1572: iadd
    //   1573: istore #13
    //   1575: aload #20
    //   1577: iload #13
    //   1579: aaload
    //   1580: invokevirtual getMargin : ()I
    //   1583: istore #9
    //   1585: aload #21
    //   1587: ifnull -> 1633
    //   1590: aload #21
    //   1592: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1595: iload_3
    //   1596: aaload
    //   1597: astore #24
    //   1599: aload #24
    //   1601: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   1604: astore #23
    //   1606: aload #24
    //   1608: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1611: ifnull -> 1627
    //   1614: aload #24
    //   1616: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1619: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   1622: astore #20
    //   1624: goto -> 1685
    //   1627: aconst_null
    //   1628: astore #20
    //   1630: goto -> 1685
    //   1633: aload #16
    //   1635: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1638: iload #13
    //   1640: aaload
    //   1641: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1644: astore #24
    //   1646: aload #24
    //   1648: ifnull -> 1661
    //   1651: aload #24
    //   1653: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   1656: astore #20
    //   1658: goto -> 1664
    //   1661: aconst_null
    //   1662: astore #20
    //   1664: aload #17
    //   1666: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1669: iload #13
    //   1671: aaload
    //   1672: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   1675: astore #25
    //   1677: aload #20
    //   1679: astore #23
    //   1681: aload #25
    //   1683: astore #20
    //   1685: iload #9
    //   1687: istore #8
    //   1689: aload #24
    //   1691: ifnull -> 1704
    //   1694: iload #9
    //   1696: aload #24
    //   1698: invokevirtual getMargin : ()I
    //   1701: iadd
    //   1702: istore #8
    //   1704: iload #12
    //   1706: istore #9
    //   1708: aload #22
    //   1710: ifnull -> 1729
    //   1713: iload #12
    //   1715: aload #22
    //   1717: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1720: iload #13
    //   1722: aaload
    //   1723: invokevirtual getMargin : ()I
    //   1726: iadd
    //   1727: istore #9
    //   1729: aload #26
    //   1731: ifnull -> 1817
    //   1734: aload_0
    //   1735: ifnull -> 1817
    //   1738: aload #23
    //   1740: ifnull -> 1817
    //   1743: aload #20
    //   1745: ifnull -> 1817
    //   1748: aload #17
    //   1750: aload #19
    //   1752: if_acmpne -> 1770
    //   1755: aload #19
    //   1757: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1760: iload_3
    //   1761: aaload
    //   1762: invokevirtual getMargin : ()I
    //   1765: istore #9
    //   1767: goto -> 1770
    //   1770: aload #17
    //   1772: aload #18
    //   1774: if_acmpne -> 1793
    //   1777: aload #18
    //   1779: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1782: iload #13
    //   1784: aaload
    //   1785: invokevirtual getMargin : ()I
    //   1788: istore #8
    //   1790: goto -> 1793
    //   1793: aload_1
    //   1794: aload #26
    //   1796: aload_0
    //   1797: iload #9
    //   1799: ldc 0.5
    //   1801: aload #23
    //   1803: aload #20
    //   1805: iload #8
    //   1807: iconst_4
    //   1808: invokevirtual addCentering : (Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;IFLandroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;II)V
    //   1811: aload #21
    //   1813: astore_0
    //   1814: goto -> 1820
    //   1817: aload #21
    //   1819: astore_0
    //   1820: aload #17
    //   1822: astore #22
    //   1824: aload_0
    //   1825: astore #17
    //   1827: goto -> 1406
    //   1830: aload #19
    //   1832: astore_0
    //   1833: aload #18
    //   1835: astore #19
    //   1837: goto -> 1376
    //   1840: aload #17
    //   1842: astore_0
    //   1843: aload #18
    //   1845: astore #19
    //   1847: iload #11
    //   1849: ifeq -> 1376
    //   1852: aload #17
    //   1854: astore_0
    //   1855: aload #18
    //   1857: astore #19
    //   1859: aload #17
    //   1861: ifnull -> 1376
    //   1864: aload #17
    //   1866: astore_0
    //   1867: aload_0
    //   1868: astore #19
    //   1870: aload_0
    //   1871: astore #21
    //   1873: aload #19
    //   1875: astore #20
    //   1877: aload #20
    //   1879: ifnull -> 2196
    //   1882: aload #20
    //   1884: getfield mListNextVisibleWidget : [Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   1887: iload_2
    //   1888: aaload
    //   1889: astore_0
    //   1890: aload #20
    //   1892: aload #17
    //   1894: if_acmpeq -> 2186
    //   1897: aload #20
    //   1899: aload #18
    //   1901: if_acmpeq -> 2186
    //   1904: aload_0
    //   1905: ifnull -> 2186
    //   1908: aload_0
    //   1909: aload #18
    //   1911: if_acmpne -> 1919
    //   1914: aconst_null
    //   1915: astore_0
    //   1916: goto -> 1919
    //   1919: aload #20
    //   1921: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1924: iload_3
    //   1925: aaload
    //   1926: astore #19
    //   1928: aload #19
    //   1930: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   1933: astore #25
    //   1935: aload #19
    //   1937: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1940: ifnull -> 1953
    //   1943: aload #19
    //   1945: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1948: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   1951: astore #22
    //   1953: aload #21
    //   1955: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1958: astore #22
    //   1960: iload_3
    //   1961: iconst_1
    //   1962: iadd
    //   1963: istore #13
    //   1965: aload #22
    //   1967: iload #13
    //   1969: aaload
    //   1970: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   1973: astore #26
    //   1975: aload #19
    //   1977: invokevirtual getMargin : ()I
    //   1980: istore #12
    //   1982: aload #20
    //   1984: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   1987: iload #13
    //   1989: aaload
    //   1990: invokevirtual getMargin : ()I
    //   1993: istore #9
    //   1995: aload_0
    //   1996: ifnull -> 2041
    //   1999: aload_0
    //   2000: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   2003: iload_3
    //   2004: aaload
    //   2005: astore #22
    //   2007: aload #22
    //   2009: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   2012: astore #23
    //   2014: aload #22
    //   2016: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   2019: ifnull -> 2035
    //   2022: aload #22
    //   2024: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   2027: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   2030: astore #19
    //   2032: goto -> 2097
    //   2035: aconst_null
    //   2036: astore #19
    //   2038: goto -> 2097
    //   2041: aload #20
    //   2043: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   2046: iload #13
    //   2048: aaload
    //   2049: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   2052: astore #24
    //   2054: aload #24
    //   2056: ifnull -> 2069
    //   2059: aload #24
    //   2061: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   2064: astore #19
    //   2066: goto -> 2072
    //   2069: aconst_null
    //   2070: astore #19
    //   2072: aload #20
    //   2074: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   2077: iload #13
    //   2079: aaload
    //   2080: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   2083: astore #22
    //   2085: aload #19
    //   2087: astore #23
    //   2089: aload #22
    //   2091: astore #19
    //   2093: aload #24
    //   2095: astore #22
    //   2097: iload #9
    //   2099: istore #8
    //   2101: aload #22
    //   2103: ifnull -> 2116
    //   2106: iload #9
    //   2108: aload #22
    //   2110: invokevirtual getMargin : ()I
    //   2113: iadd
    //   2114: istore #8
    //   2116: iload #12
    //   2118: istore #9
    //   2120: aload #21
    //   2122: ifnull -> 2141
    //   2125: iload #12
    //   2127: aload #21
    //   2129: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   2132: iload #13
    //   2134: aaload
    //   2135: invokevirtual getMargin : ()I
    //   2138: iadd
    //   2139: istore #9
    //   2141: aload #25
    //   2143: ifnull -> 2183
    //   2146: aload #26
    //   2148: ifnull -> 2183
    //   2151: aload #23
    //   2153: ifnull -> 2183
    //   2156: aload #19
    //   2158: ifnull -> 2183
    //   2161: aload_1
    //   2162: aload #25
    //   2164: aload #26
    //   2166: iload #9
    //   2168: ldc 0.5
    //   2170: aload #23
    //   2172: aload #19
    //   2174: iload #8
    //   2176: iconst_4
    //   2177: invokevirtual addCentering : (Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;IFLandroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;II)V
    //   2180: goto -> 2183
    //   2183: goto -> 2186
    //   2186: aload #20
    //   2188: astore #21
    //   2190: aload_0
    //   2191: astore #19
    //   2193: goto -> 1873
    //   2196: aload #17
    //   2198: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   2201: iload_3
    //   2202: aaload
    //   2203: astore_0
    //   2204: aload #4
    //   2206: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   2209: iload_3
    //   2210: aaload
    //   2211: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   2214: astore #19
    //   2216: aload #18
    //   2218: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   2221: astore #4
    //   2223: iload_3
    //   2224: iconst_1
    //   2225: iadd
    //   2226: istore_2
    //   2227: aload #4
    //   2229: iload_2
    //   2230: aaload
    //   2231: astore #4
    //   2233: aload #16
    //   2235: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   2238: iload_2
    //   2239: aaload
    //   2240: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   2243: astore #20
    //   2245: aload #19
    //   2247: ifnull -> 2322
    //   2250: aload #17
    //   2252: aload #18
    //   2254: if_acmpeq -> 2279
    //   2257: aload_1
    //   2258: aload_0
    //   2259: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   2262: aload #19
    //   2264: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   2267: aload_0
    //   2268: invokevirtual getMargin : ()I
    //   2271: iconst_5
    //   2272: invokevirtual addEquality : (Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;II)Landroid/support/constraint/solver/ArrayRow;
    //   2275: pop
    //   2276: goto -> 2322
    //   2279: aload #20
    //   2281: ifnull -> 2322
    //   2284: aload_1
    //   2285: aload_0
    //   2286: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   2289: aload #19
    //   2291: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   2294: aload_0
    //   2295: invokevirtual getMargin : ()I
    //   2298: ldc 0.5
    //   2300: aload #4
    //   2302: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   2305: aload #20
    //   2307: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   2310: aload #4
    //   2312: invokevirtual getMargin : ()I
    //   2315: iconst_5
    //   2316: invokevirtual addCentering : (Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;IFLandroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;II)V
    //   2319: goto -> 2322
    //   2322: aload #17
    //   2324: astore_0
    //   2325: aload #18
    //   2327: astore #19
    //   2329: aload #20
    //   2331: ifnull -> 2377
    //   2334: aload #17
    //   2336: astore_0
    //   2337: aload #18
    //   2339: astore #19
    //   2341: aload #17
    //   2343: aload #18
    //   2345: if_acmpeq -> 2377
    //   2348: aload_1
    //   2349: aload #4
    //   2351: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   2354: aload #20
    //   2356: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   2359: aload #4
    //   2361: invokevirtual getMargin : ()I
    //   2364: ineg
    //   2365: iconst_5
    //   2366: invokevirtual addEquality : (Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;II)Landroid/support/constraint/solver/ArrayRow;
    //   2369: pop
    //   2370: aload #18
    //   2372: astore #19
    //   2374: aload #17
    //   2376: astore_0
    //   2377: iload #10
    //   2379: ifne -> 2387
    //   2382: iload #11
    //   2384: ifeq -> 2546
    //   2387: aload_0
    //   2388: ifnull -> 2546
    //   2391: aload_0
    //   2392: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   2395: iload_3
    //   2396: aaload
    //   2397: astore #18
    //   2399: aload #19
    //   2401: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   2404: astore #4
    //   2406: iload_3
    //   2407: iconst_1
    //   2408: iadd
    //   2409: istore_2
    //   2410: aload #4
    //   2412: iload_2
    //   2413: aaload
    //   2414: astore #20
    //   2416: aload #18
    //   2418: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   2421: ifnull -> 2437
    //   2424: aload #18
    //   2426: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   2429: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   2432: astore #4
    //   2434: goto -> 2440
    //   2437: aconst_null
    //   2438: astore #4
    //   2440: aload #20
    //   2442: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   2445: ifnull -> 2461
    //   2448: aload #20
    //   2450: getfield mTarget : Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   2453: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   2456: astore #17
    //   2458: goto -> 2464
    //   2461: aconst_null
    //   2462: astore #17
    //   2464: aload_0
    //   2465: aload #19
    //   2467: if_acmpne -> 2486
    //   2470: aload_0
    //   2471: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   2474: iload_3
    //   2475: aaload
    //   2476: astore #18
    //   2478: aload_0
    //   2479: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   2482: iload_2
    //   2483: aaload
    //   2484: astore #20
    //   2486: aload #4
    //   2488: ifnull -> 2546
    //   2491: aload #17
    //   2493: ifnull -> 2546
    //   2496: aload #18
    //   2498: invokevirtual getMargin : ()I
    //   2501: istore_3
    //   2502: aload #19
    //   2504: astore_0
    //   2505: aload #19
    //   2507: ifnonnull -> 2513
    //   2510: aload #16
    //   2512: astore_0
    //   2513: aload_0
    //   2514: getfield mListAnchors : [Landroid/support/constraint/solver/widgets/ConstraintAnchor;
    //   2517: iload_2
    //   2518: aaload
    //   2519: invokevirtual getMargin : ()I
    //   2522: istore_2
    //   2523: aload_1
    //   2524: aload #18
    //   2526: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   2529: aload #4
    //   2531: iload_3
    //   2532: ldc 0.5
    //   2534: aload #17
    //   2536: aload #20
    //   2538: getfield mSolverVariable : Landroid/support/constraint/solver/SolverVariable;
    //   2541: iload_2
    //   2542: iconst_5
    //   2543: invokevirtual addCentering : (Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;IFLandroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/SolverVariable;II)V
    //   2546: return }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/constraint/solver/widgets/Chain.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */