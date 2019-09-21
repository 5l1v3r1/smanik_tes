package android.support.constraint.solver;

import java.io.PrintStream;
import java.util.Arrays;

public class ArrayLinkedVariables {
  private static final boolean DEBUG = false;
  
  private static final boolean FULL_NEW_CHECK = false;
  
  private static final int NONE = -1;
  
  private int ROW_SIZE = 8;
  
  private SolverVariable candidate = null;
  
  int currentSize = 0;
  
  private int[] mArrayIndices = new int[this.ROW_SIZE];
  
  private int[] mArrayNextIndices = new int[this.ROW_SIZE];
  
  private float[] mArrayValues = new float[this.ROW_SIZE];
  
  private final Cache mCache;
  
  private boolean mDidFillOnce = false;
  
  private int mHead = -1;
  
  private int mLast = -1;
  
  private final ArrayRow mRow;
  
  ArrayLinkedVariables(ArrayRow paramArrayRow, Cache paramCache) {
    this.mRow = paramArrayRow;
    this.mCache = paramCache;
  }
  
  private boolean isNew(SolverVariable paramSolverVariable, LinearSystem paramLinearSystem) { return (paramSolverVariable.usageInRowCount <= 1); }
  
  final void add(SolverVariable paramSolverVariable, float paramFloat, boolean paramBoolean) {
    if (paramFloat == 0.0F)
      return; 
    if (this.mHead == -1) {
      this.mHead = 0;
      this.mArrayValues[this.mHead] = paramFloat;
      this.mArrayIndices[this.mHead] = paramSolverVariable.id;
      this.mArrayNextIndices[this.mHead] = -1;
      paramSolverVariable.usageInRowCount++;
      paramSolverVariable.addToRow(this.mRow);
      this.currentSize++;
      if (!this.mDidFillOnce) {
        this.mLast++;
        if (this.mLast >= this.mArrayIndices.length) {
          this.mDidFillOnce = true;
          this.mLast = this.mArrayIndices.length - 1;
        } 
      } 
      return;
    } 
    int i = this.mHead;
    int j = 0;
    int k = -1;
    while (i != -1 && j < this.currentSize) {
      if (this.mArrayIndices[i] == paramSolverVariable.id) {
        float[] arrayOfFloat = this.mArrayValues;
        arrayOfFloat[i] = arrayOfFloat[i] + paramFloat;
        if (this.mArrayValues[i] == 0.0F) {
          if (i == this.mHead) {
            this.mHead = this.mArrayNextIndices[i];
          } else {
            this.mArrayNextIndices[k] = this.mArrayNextIndices[i];
          } 
          if (paramBoolean)
            paramSolverVariable.removeFromRow(this.mRow); 
          if (this.mDidFillOnce)
            this.mLast = i; 
          paramSolverVariable.usageInRowCount--;
          this.currentSize--;
        } 
        return;
      } 
      if (this.mArrayIndices[i] < paramSolverVariable.id)
        k = i; 
      i = this.mArrayNextIndices[i];
      j++;
    } 
    i = this.mLast + 1;
    if (this.mDidFillOnce)
      if (this.mArrayIndices[this.mLast] == -1) {
        i = this.mLast;
      } else {
        i = this.mArrayIndices.length;
      }  
    j = i;
    if (i >= this.mArrayIndices.length) {
      j = i;
      if (this.currentSize < this.mArrayIndices.length) {
        int m = 0;
        while (true) {
          j = i;
          if (m < this.mArrayIndices.length) {
            if (this.mArrayIndices[m] == -1) {
              j = m;
              break;
            } 
            m++;
            continue;
          } 
          break;
        } 
      } 
    } 
    i = j;
    if (j >= this.mArrayIndices.length) {
      i = this.mArrayIndices.length;
      this.ROW_SIZE *= 2;
      this.mDidFillOnce = false;
      this.mLast = i - 1;
      this.mArrayValues = Arrays.copyOf(this.mArrayValues, this.ROW_SIZE);
      this.mArrayIndices = Arrays.copyOf(this.mArrayIndices, this.ROW_SIZE);
      this.mArrayNextIndices = Arrays.copyOf(this.mArrayNextIndices, this.ROW_SIZE);
    } 
    this.mArrayIndices[i] = paramSolverVariable.id;
    this.mArrayValues[i] = paramFloat;
    if (k != -1) {
      this.mArrayNextIndices[i] = this.mArrayNextIndices[k];
      this.mArrayNextIndices[k] = i;
    } else {
      this.mArrayNextIndices[i] = this.mHead;
      this.mHead = i;
    } 
    paramSolverVariable.usageInRowCount++;
    paramSolverVariable.addToRow(this.mRow);
    this.currentSize++;
    if (!this.mDidFillOnce)
      this.mLast++; 
    if (this.mLast >= this.mArrayIndices.length) {
      this.mDidFillOnce = true;
      this.mLast = this.mArrayIndices.length - 1;
    } 
  }
  
  SolverVariable chooseSubject(LinearSystem paramLinearSystem) { // Byte code:
    //   0: aload_0
    //   1: getfield mHead : I
    //   4: istore #8
    //   6: aconst_null
    //   7: astore #17
    //   9: iconst_0
    //   10: istore #7
    //   12: aconst_null
    //   13: astore #16
    //   15: fconst_0
    //   16: fstore #6
    //   18: iconst_0
    //   19: istore #12
    //   21: fconst_0
    //   22: fstore #5
    //   24: iconst_0
    //   25: istore #11
    //   27: iload #8
    //   29: iconst_m1
    //   30: if_icmpeq -> 550
    //   33: iload #7
    //   35: aload_0
    //   36: getfield currentSize : I
    //   39: if_icmpge -> 550
    //   42: aload_0
    //   43: getfield mArrayValues : [F
    //   46: iload #8
    //   48: faload
    //   49: fstore_3
    //   50: aload_0
    //   51: getfield mCache : Landroid/support/constraint/solver/Cache;
    //   54: getfield mIndexedVariables : [Landroid/support/constraint/solver/SolverVariable;
    //   57: aload_0
    //   58: getfield mArrayIndices : [I
    //   61: iload #8
    //   63: iaload
    //   64: aaload
    //   65: astore #13
    //   67: fload_3
    //   68: fconst_0
    //   69: fcmpg
    //   70: ifge -> 104
    //   73: fload_3
    //   74: fstore_2
    //   75: fload_3
    //   76: ldc -0.001
    //   78: fcmpl
    //   79: ifle -> 133
    //   82: aload_0
    //   83: getfield mArrayValues : [F
    //   86: iload #8
    //   88: fconst_0
    //   89: fastore
    //   90: aload #13
    //   92: aload_0
    //   93: getfield mRow : Landroid/support/constraint/solver/ArrayRow;
    //   96: invokevirtual removeFromRow : (Landroid/support/constraint/solver/ArrayRow;)V
    //   99: fconst_0
    //   100: fstore_2
    //   101: goto -> 133
    //   104: fload_3
    //   105: fstore_2
    //   106: fload_3
    //   107: ldc 0.001
    //   109: fcmpg
    //   110: ifge -> 133
    //   113: aload_0
    //   114: getfield mArrayValues : [F
    //   117: iload #8
    //   119: fconst_0
    //   120: fastore
    //   121: aload #13
    //   123: aload_0
    //   124: getfield mRow : Landroid/support/constraint/solver/ArrayRow;
    //   127: invokevirtual removeFromRow : (Landroid/support/constraint/solver/ArrayRow;)V
    //   130: goto -> 99
    //   133: aload #17
    //   135: astore #14
    //   137: aload #16
    //   139: astore #15
    //   141: fload #6
    //   143: fstore_3
    //   144: iload #12
    //   146: istore #9
    //   148: fload #5
    //   150: fstore #4
    //   152: iload #11
    //   154: istore #10
    //   156: fload_2
    //   157: fconst_0
    //   158: fcmpl
    //   159: ifeq -> 509
    //   162: aload #13
    //   164: getfield mType : Landroid/support/constraint/solver/SolverVariable$Type;
    //   167: getstatic android/support/constraint/solver/SolverVariable$Type.UNRESTRICTED : Landroid/support/constraint/solver/SolverVariable$Type;
    //   170: if_acmpne -> 312
    //   173: aload #17
    //   175: ifnonnull -> 208
    //   178: aload_0
    //   179: aload #13
    //   181: aload_1
    //   182: invokespecial isNew : (Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/LinearSystem;)Z
    //   185: istore #9
    //   187: aload #13
    //   189: astore #14
    //   191: aload #16
    //   193: astore #15
    //   195: fload_2
    //   196: fstore_3
    //   197: fload #5
    //   199: fstore #4
    //   201: iload #11
    //   203: istore #10
    //   205: goto -> 509
    //   208: fload #6
    //   210: fload_2
    //   211: fcmpl
    //   212: ifle -> 227
    //   215: aload_0
    //   216: aload #13
    //   218: aload_1
    //   219: invokespecial isNew : (Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/LinearSystem;)Z
    //   222: istore #9
    //   224: goto -> 187
    //   227: aload #17
    //   229: astore #14
    //   231: aload #16
    //   233: astore #15
    //   235: fload #6
    //   237: fstore_3
    //   238: iload #12
    //   240: istore #9
    //   242: fload #5
    //   244: fstore #4
    //   246: iload #11
    //   248: istore #10
    //   250: iload #12
    //   252: ifne -> 509
    //   255: aload #17
    //   257: astore #14
    //   259: aload #16
    //   261: astore #15
    //   263: fload #6
    //   265: fstore_3
    //   266: iload #12
    //   268: istore #9
    //   270: fload #5
    //   272: fstore #4
    //   274: iload #11
    //   276: istore #10
    //   278: aload_0
    //   279: aload #13
    //   281: aload_1
    //   282: invokespecial isNew : (Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/LinearSystem;)Z
    //   285: ifeq -> 509
    //   288: iconst_1
    //   289: istore #9
    //   291: aload #13
    //   293: astore #14
    //   295: aload #16
    //   297: astore #15
    //   299: fload_2
    //   300: fstore_3
    //   301: fload #5
    //   303: fstore #4
    //   305: iload #11
    //   307: istore #10
    //   309: goto -> 509
    //   312: aload #17
    //   314: astore #14
    //   316: aload #16
    //   318: astore #15
    //   320: fload #6
    //   322: fstore_3
    //   323: iload #12
    //   325: istore #9
    //   327: fload #5
    //   329: fstore #4
    //   331: iload #11
    //   333: istore #10
    //   335: aload #17
    //   337: ifnonnull -> 509
    //   340: aload #17
    //   342: astore #14
    //   344: aload #16
    //   346: astore #15
    //   348: fload #6
    //   350: fstore_3
    //   351: iload #12
    //   353: istore #9
    //   355: fload #5
    //   357: fstore #4
    //   359: iload #11
    //   361: istore #10
    //   363: fload_2
    //   364: fconst_0
    //   365: fcmpg
    //   366: ifge -> 509
    //   369: aload #16
    //   371: ifnonnull -> 408
    //   374: aload_0
    //   375: aload #13
    //   377: aload_1
    //   378: invokespecial isNew : (Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/LinearSystem;)Z
    //   381: istore #9
    //   383: iload #9
    //   385: istore #10
    //   387: aload #17
    //   389: astore #14
    //   391: aload #13
    //   393: astore #15
    //   395: fload #6
    //   397: fstore_3
    //   398: iload #12
    //   400: istore #9
    //   402: fload_2
    //   403: fstore #4
    //   405: goto -> 509
    //   408: fload #5
    //   410: fload_2
    //   411: fcmpl
    //   412: ifle -> 427
    //   415: aload_0
    //   416: aload #13
    //   418: aload_1
    //   419: invokespecial isNew : (Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/LinearSystem;)Z
    //   422: istore #9
    //   424: goto -> 383
    //   427: aload #17
    //   429: astore #14
    //   431: aload #16
    //   433: astore #15
    //   435: fload #6
    //   437: fstore_3
    //   438: iload #12
    //   440: istore #9
    //   442: fload #5
    //   444: fstore #4
    //   446: iload #11
    //   448: istore #10
    //   450: iload #11
    //   452: ifne -> 509
    //   455: aload #17
    //   457: astore #14
    //   459: aload #16
    //   461: astore #15
    //   463: fload #6
    //   465: fstore_3
    //   466: iload #12
    //   468: istore #9
    //   470: fload #5
    //   472: fstore #4
    //   474: iload #11
    //   476: istore #10
    //   478: aload_0
    //   479: aload #13
    //   481: aload_1
    //   482: invokespecial isNew : (Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/LinearSystem;)Z
    //   485: ifeq -> 509
    //   488: iconst_1
    //   489: istore #10
    //   491: fload_2
    //   492: fstore #4
    //   494: iload #12
    //   496: istore #9
    //   498: fload #6
    //   500: fstore_3
    //   501: aload #13
    //   503: astore #15
    //   505: aload #17
    //   507: astore #14
    //   509: aload_0
    //   510: getfield mArrayNextIndices : [I
    //   513: iload #8
    //   515: iaload
    //   516: istore #8
    //   518: iload #7
    //   520: iconst_1
    //   521: iadd
    //   522: istore #7
    //   524: aload #14
    //   526: astore #17
    //   528: aload #15
    //   530: astore #16
    //   532: fload_3
    //   533: fstore #6
    //   535: iload #9
    //   537: istore #12
    //   539: fload #4
    //   541: fstore #5
    //   543: iload #10
    //   545: istore #11
    //   547: goto -> 27
    //   550: aload #17
    //   552: ifnull -> 558
    //   555: aload #17
    //   557: areturn
    //   558: aload #16
    //   560: areturn }
  
  public final void clear() {
    int i = this.mHead;
    for (byte b = 0; i != -1 && b < this.currentSize; b++) {
      SolverVariable solverVariable = this.mCache.mIndexedVariables[this.mArrayIndices[i]];
      if (solverVariable != null)
        solverVariable.removeFromRow(this.mRow); 
      i = this.mArrayNextIndices[i];
    } 
    this.mHead = -1;
    this.mLast = -1;
    this.mDidFillOnce = false;
    this.currentSize = 0;
  }
  
  final boolean containsKey(SolverVariable paramSolverVariable) {
    if (this.mHead == -1)
      return false; 
    int i = this.mHead;
    for (byte b = 0; i != -1 && b < this.currentSize; b++) {
      if (this.mArrayIndices[i] == paramSolverVariable.id)
        return true; 
      i = this.mArrayNextIndices[i];
    } 
    return false;
  }
  
  public void display() {
    int i = this.currentSize;
    System.out.print("{ ");
    for (byte b = 0; b < i; b++) {
      SolverVariable solverVariable = getVariable(b);
      if (solverVariable != null) {
        PrintStream printStream = System.out;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(solverVariable);
        stringBuilder.append(" = ");
        stringBuilder.append(getVariableValue(b));
        stringBuilder.append(" ");
        printStream.print(stringBuilder.toString());
      } 
    } 
    System.out.println(" }");
  }
  
  void divideByAmount(float paramFloat) {
    int i = this.mHead;
    for (byte b = 0; i != -1 && b < this.currentSize; b++) {
      float[] arrayOfFloat = this.mArrayValues;
      arrayOfFloat[i] = arrayOfFloat[i] / paramFloat;
      i = this.mArrayNextIndices[i];
    } 
  }
  
  public final float get(SolverVariable paramSolverVariable) {
    int i = this.mHead;
    for (byte b = 0; i != -1 && b < this.currentSize; b++) {
      if (this.mArrayIndices[i] == paramSolverVariable.id)
        return this.mArrayValues[i]; 
      i = this.mArrayNextIndices[i];
    } 
    return 0.0F;
  }
  
  SolverVariable getPivotCandidate() { // Byte code:
    //   0: aload_0
    //   1: getfield candidate : Landroid/support/constraint/solver/SolverVariable;
    //   4: ifnonnull -> 101
    //   7: aload_0
    //   8: getfield mHead : I
    //   11: istore_2
    //   12: iconst_0
    //   13: istore_1
    //   14: aconst_null
    //   15: astore_3
    //   16: iload_2
    //   17: iconst_m1
    //   18: if_icmpeq -> 99
    //   21: iload_1
    //   22: aload_0
    //   23: getfield currentSize : I
    //   26: if_icmpge -> 99
    //   29: aload_3
    //   30: astore #4
    //   32: aload_0
    //   33: getfield mArrayValues : [F
    //   36: iload_2
    //   37: faload
    //   38: fconst_0
    //   39: fcmpg
    //   40: ifge -> 82
    //   43: aload_0
    //   44: getfield mCache : Landroid/support/constraint/solver/Cache;
    //   47: getfield mIndexedVariables : [Landroid/support/constraint/solver/SolverVariable;
    //   50: aload_0
    //   51: getfield mArrayIndices : [I
    //   54: iload_2
    //   55: iaload
    //   56: aaload
    //   57: astore #5
    //   59: aload_3
    //   60: ifnull -> 78
    //   63: aload_3
    //   64: astore #4
    //   66: aload_3
    //   67: getfield strength : I
    //   70: aload #5
    //   72: getfield strength : I
    //   75: if_icmpge -> 82
    //   78: aload #5
    //   80: astore #4
    //   82: aload_0
    //   83: getfield mArrayNextIndices : [I
    //   86: iload_2
    //   87: iaload
    //   88: istore_2
    //   89: iload_1
    //   90: iconst_1
    //   91: iadd
    //   92: istore_1
    //   93: aload #4
    //   95: astore_3
    //   96: goto -> 16
    //   99: aload_3
    //   100: areturn
    //   101: aload_0
    //   102: getfield candidate : Landroid/support/constraint/solver/SolverVariable;
    //   105: areturn }
  
  SolverVariable getPivotCandidate(boolean[] paramArrayOfBoolean, SolverVariable paramSolverVariable) { // Byte code:
    //   0: aload_0
    //   1: getfield mHead : I
    //   4: istore #7
    //   6: iconst_0
    //   7: istore #6
    //   9: aconst_null
    //   10: astore #8
    //   12: fconst_0
    //   13: fstore_3
    //   14: iload #7
    //   16: iconst_m1
    //   17: if_icmpeq -> 184
    //   20: iload #6
    //   22: aload_0
    //   23: getfield currentSize : I
    //   26: if_icmpge -> 184
    //   29: aload #8
    //   31: astore #9
    //   33: fload_3
    //   34: fstore #4
    //   36: aload_0
    //   37: getfield mArrayValues : [F
    //   40: iload #7
    //   42: faload
    //   43: fconst_0
    //   44: fcmpg
    //   45: ifge -> 159
    //   48: aload_0
    //   49: getfield mCache : Landroid/support/constraint/solver/Cache;
    //   52: getfield mIndexedVariables : [Landroid/support/constraint/solver/SolverVariable;
    //   55: aload_0
    //   56: getfield mArrayIndices : [I
    //   59: iload #7
    //   61: iaload
    //   62: aaload
    //   63: astore #10
    //   65: aload_1
    //   66: ifnull -> 86
    //   69: aload #8
    //   71: astore #9
    //   73: fload_3
    //   74: fstore #4
    //   76: aload_1
    //   77: aload #10
    //   79: getfield id : I
    //   82: baload
    //   83: ifne -> 159
    //   86: aload #8
    //   88: astore #9
    //   90: fload_3
    //   91: fstore #4
    //   93: aload #10
    //   95: aload_2
    //   96: if_acmpeq -> 159
    //   99: aload #10
    //   101: getfield mType : Landroid/support/constraint/solver/SolverVariable$Type;
    //   104: getstatic android/support/constraint/solver/SolverVariable$Type.SLACK : Landroid/support/constraint/solver/SolverVariable$Type;
    //   107: if_acmpeq -> 128
    //   110: aload #8
    //   112: astore #9
    //   114: fload_3
    //   115: fstore #4
    //   117: aload #10
    //   119: getfield mType : Landroid/support/constraint/solver/SolverVariable$Type;
    //   122: getstatic android/support/constraint/solver/SolverVariable$Type.ERROR : Landroid/support/constraint/solver/SolverVariable$Type;
    //   125: if_acmpne -> 159
    //   128: aload_0
    //   129: getfield mArrayValues : [F
    //   132: iload #7
    //   134: faload
    //   135: fstore #5
    //   137: aload #8
    //   139: astore #9
    //   141: fload_3
    //   142: fstore #4
    //   144: fload #5
    //   146: fload_3
    //   147: fcmpg
    //   148: ifge -> 159
    //   151: aload #10
    //   153: astore #9
    //   155: fload #5
    //   157: fstore #4
    //   159: aload_0
    //   160: getfield mArrayNextIndices : [I
    //   163: iload #7
    //   165: iaload
    //   166: istore #7
    //   168: iload #6
    //   170: iconst_1
    //   171: iadd
    //   172: istore #6
    //   174: aload #9
    //   176: astore #8
    //   178: fload #4
    //   180: fstore_3
    //   181: goto -> 14
    //   184: aload #8
    //   186: areturn }
  
  final SolverVariable getVariable(int paramInt) {
    int i = this.mHead;
    for (byte b = 0; i != -1 && b < this.currentSize; b++) {
      if (b == paramInt)
        return this.mCache.mIndexedVariables[this.mArrayIndices[i]]; 
      i = this.mArrayNextIndices[i];
    } 
    return null;
  }
  
  final float getVariableValue(int paramInt) {
    int i = this.mHead;
    for (byte b = 0; i != -1 && b < this.currentSize; b++) {
      if (b == paramInt)
        return this.mArrayValues[i]; 
      i = this.mArrayNextIndices[i];
    } 
    return 0.0F;
  }
  
  boolean hasAtLeastOnePositiveVariable() {
    int i = this.mHead;
    for (byte b = 0; i != -1 && b < this.currentSize; b++) {
      if (this.mArrayValues[i] > 0.0F)
        return true; 
      i = this.mArrayNextIndices[i];
    } 
    return false;
  }
  
  void invert() {
    int i = this.mHead;
    for (byte b = 0; i != -1 && b < this.currentSize; b++) {
      float[] arrayOfFloat = this.mArrayValues;
      arrayOfFloat[i] = arrayOfFloat[i] * -1.0F;
      i = this.mArrayNextIndices[i];
    } 
  }
  
  public final void put(SolverVariable paramSolverVariable, float paramFloat) {
    if (paramFloat == 0.0F) {
      remove(paramSolverVariable, true);
      return;
    } 
    if (this.mHead == -1) {
      this.mHead = 0;
      this.mArrayValues[this.mHead] = paramFloat;
      this.mArrayIndices[this.mHead] = paramSolverVariable.id;
      this.mArrayNextIndices[this.mHead] = -1;
      paramSolverVariable.usageInRowCount++;
      paramSolverVariable.addToRow(this.mRow);
      this.currentSize++;
      if (!this.mDidFillOnce) {
        this.mLast++;
        if (this.mLast >= this.mArrayIndices.length) {
          this.mDidFillOnce = true;
          this.mLast = this.mArrayIndices.length - 1;
        } 
      } 
      return;
    } 
    int i = this.mHead;
    int j = 0;
    int k = -1;
    while (i != -1 && j < this.currentSize) {
      if (this.mArrayIndices[i] == paramSolverVariable.id) {
        this.mArrayValues[i] = paramFloat;
        return;
      } 
      if (this.mArrayIndices[i] < paramSolverVariable.id)
        k = i; 
      i = this.mArrayNextIndices[i];
      j++;
    } 
    i = this.mLast + 1;
    if (this.mDidFillOnce)
      if (this.mArrayIndices[this.mLast] == -1) {
        i = this.mLast;
      } else {
        i = this.mArrayIndices.length;
      }  
    j = i;
    if (i >= this.mArrayIndices.length) {
      j = i;
      if (this.currentSize < this.mArrayIndices.length) {
        int m = 0;
        while (true) {
          j = i;
          if (m < this.mArrayIndices.length) {
            if (this.mArrayIndices[m] == -1) {
              j = m;
              break;
            } 
            m++;
            continue;
          } 
          break;
        } 
      } 
    } 
    i = j;
    if (j >= this.mArrayIndices.length) {
      i = this.mArrayIndices.length;
      this.ROW_SIZE *= 2;
      this.mDidFillOnce = false;
      this.mLast = i - 1;
      this.mArrayValues = Arrays.copyOf(this.mArrayValues, this.ROW_SIZE);
      this.mArrayIndices = Arrays.copyOf(this.mArrayIndices, this.ROW_SIZE);
      this.mArrayNextIndices = Arrays.copyOf(this.mArrayNextIndices, this.ROW_SIZE);
    } 
    this.mArrayIndices[i] = paramSolverVariable.id;
    this.mArrayValues[i] = paramFloat;
    if (k != -1) {
      this.mArrayNextIndices[i] = this.mArrayNextIndices[k];
      this.mArrayNextIndices[k] = i;
    } else {
      this.mArrayNextIndices[i] = this.mHead;
      this.mHead = i;
    } 
    paramSolverVariable.usageInRowCount++;
    paramSolverVariable.addToRow(this.mRow);
    this.currentSize++;
    if (!this.mDidFillOnce)
      this.mLast++; 
    if (this.currentSize >= this.mArrayIndices.length)
      this.mDidFillOnce = true; 
    if (this.mLast >= this.mArrayIndices.length) {
      this.mDidFillOnce = true;
      this.mLast = this.mArrayIndices.length - 1;
    } 
  }
  
  public final float remove(SolverVariable paramSolverVariable, boolean paramBoolean) {
    if (this.candidate == paramSolverVariable)
      this.candidate = null; 
    if (this.mHead == -1)
      return 0.0F; 
    int i = this.mHead;
    byte b = 0;
    int j = -1;
    while (i != -1 && b < this.currentSize) {
      if (this.mArrayIndices[i] == paramSolverVariable.id) {
        if (i == this.mHead) {
          this.mHead = this.mArrayNextIndices[i];
        } else {
          this.mArrayNextIndices[j] = this.mArrayNextIndices[i];
        } 
        if (paramBoolean)
          paramSolverVariable.removeFromRow(this.mRow); 
        paramSolverVariable.usageInRowCount--;
        this.currentSize--;
        this.mArrayIndices[i] = -1;
        if (this.mDidFillOnce)
          this.mLast = i; 
        return this.mArrayValues[i];
      } 
      int k = this.mArrayNextIndices[i];
      b++;
      j = i;
      i = k;
    } 
    return 0.0F;
  }
  
  int sizeInBytes() { return this.mArrayIndices.length * 4 * 3 + 0 + 36; }
  
  public String toString() {
    String str = "";
    int i = this.mHead;
    for (byte b = 0; i != -1 && b < this.currentSize; b++) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(str);
      stringBuilder.append(" -> ");
      str = stringBuilder.toString();
      stringBuilder = new StringBuilder();
      stringBuilder.append(str);
      stringBuilder.append(this.mArrayValues[i]);
      stringBuilder.append(" : ");
      str = stringBuilder.toString();
      stringBuilder = new StringBuilder();
      stringBuilder.append(str);
      stringBuilder.append(this.mCache.mIndexedVariables[this.mArrayIndices[i]]);
      str = stringBuilder.toString();
      i = this.mArrayNextIndices[i];
    } 
    return str;
  }
  
  final void updateFromRow(ArrayRow paramArrayRow1, ArrayRow paramArrayRow2, boolean paramBoolean) {
    int i = this.mHead;
    label22: while (true) {
      int j;
      for (j = 0; i != -1 && j < this.currentSize; j++) {
        if (this.mArrayIndices[i] == paramArrayRow2.variable.id) {
          float f = this.mArrayValues[i];
          remove(paramArrayRow2.variable, paramBoolean);
          ArrayLinkedVariables arrayLinkedVariables = (ArrayLinkedVariables)paramArrayRow2.variables;
          j = arrayLinkedVariables.mHead;
          for (i = 0; j != -1 && i < arrayLinkedVariables.currentSize; i++) {
            add(this.mCache.mIndexedVariables[arrayLinkedVariables.mArrayIndices[j]], arrayLinkedVariables.mArrayValues[j] * f, paramBoolean);
            j = arrayLinkedVariables.mArrayNextIndices[j];
          } 
          paramArrayRow1.constantValue += paramArrayRow2.constantValue * f;
          if (paramBoolean)
            paramArrayRow2.variable.removeFromRow(paramArrayRow1); 
          i = this.mHead;
          continue label22;
        } 
        i = this.mArrayNextIndices[i];
      } 
      break;
    } 
  }
  
  void updateFromSystem(ArrayRow paramArrayRow, ArrayRow[] paramArrayOfArrayRow) {
    int i = this.mHead;
    label22: while (true) {
      int j;
      for (j = 0; i != -1 && j < this.currentSize; j++) {
        SolverVariable solverVariable = this.mCache.mIndexedVariables[this.mArrayIndices[i]];
        if (solverVariable.definitionId != -1) {
          float f = this.mArrayValues[i];
          remove(solverVariable, true);
          ArrayRow arrayRow = paramArrayOfArrayRow[solverVariable.definitionId];
          if (!arrayRow.isSimpleDefinition) {
            ArrayLinkedVariables arrayLinkedVariables = (ArrayLinkedVariables)arrayRow.variables;
            j = arrayLinkedVariables.mHead;
            for (i = 0; j != -1 && i < arrayLinkedVariables.currentSize; i++) {
              add(this.mCache.mIndexedVariables[arrayLinkedVariables.mArrayIndices[j]], arrayLinkedVariables.mArrayValues[j] * f, true);
              j = arrayLinkedVariables.mArrayNextIndices[j];
            } 
          } 
          paramArrayRow.constantValue += arrayRow.constantValue * f;
          arrayRow.variable.removeFromRow(paramArrayRow);
          i = this.mHead;
          continue label22;
        } 
        i = this.mArrayNextIndices[i];
      } 
      break;
    } 
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/constraint/solver/ArrayLinkedVariables.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */