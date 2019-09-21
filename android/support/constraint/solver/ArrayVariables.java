package android.support.constraint.solver;

import java.io.PrintStream;
import java.util.Arrays;

public class ArrayVariables {
  private static final boolean DEBUG = false;
  
  private int ROW_SIZE = 8;
  
  int currentSize = 0;
  
  private int[] mArrayIndices = new int[this.ROW_SIZE];
  
  private boolean[] mArrayValid = new boolean[this.ROW_SIZE];
  
  private float[] mArrayValues = new float[this.ROW_SIZE];
  
  private final Cache mCache;
  
  private final ArrayRow mRow;
  
  ArrayVariables(ArrayRow paramArrayRow, Cache paramCache) {
    this.mRow = paramArrayRow;
    this.mCache = paramCache;
  }
  
  private boolean isNew(SolverVariable paramSolverVariable, LinearSystem paramLinearSystem) { return (paramSolverVariable.mClientEquationsCount <= 1); }
  
  final void add(SolverVariable paramSolverVariable, float paramFloat, boolean paramBoolean) {
    float[] arrayOfFloat1;
    if (paramFloat == 0.0F)
      return; 
    int i;
    for (i = 0; i < this.currentSize; i++) {
      if (this.mArrayIndices[i] == paramSolverVariable.id) {
        arrayOfFloat1 = this.mArrayValues;
        arrayOfFloat1[i] = arrayOfFloat1[i] + paramFloat;
        return;
      } 
    } 
    if (this.currentSize >= this.mArrayIndices.length) {
      this.ROW_SIZE *= 2;
      this.mArrayValues = Arrays.copyOf(this.mArrayValues, this.ROW_SIZE);
      this.mArrayIndices = Arrays.copyOf(this.mArrayIndices, this.ROW_SIZE);
      this.mArrayValid = Arrays.copyOf(this.mArrayValid, this.ROW_SIZE);
    } 
    this.mArrayIndices[this.currentSize] = arrayOfFloat1.id;
    float[] arrayOfFloat2 = this.mArrayValues;
    i = this.currentSize;
    arrayOfFloat2[i] = arrayOfFloat2[i] + paramFloat;
    this.mArrayValid[this.currentSize] = true;
    arrayOfFloat1.usageInRowCount++;
    arrayOfFloat1.addToRow(this.mRow);
    if (this.mArrayValues[this.currentSize] == 0.0F) {
      arrayOfFloat1.usageInRowCount--;
      arrayOfFloat1.removeFromRow(this.mRow);
      this.mArrayValid[this.currentSize] = false;
    } 
    this.currentSize++;
  }
  
  SolverVariable chooseSubject(LinearSystem paramLinearSystem) { // Byte code:
    //   0: aconst_null
    //   1: astore #16
    //   3: aload #16
    //   5: astore #15
    //   7: iconst_0
    //   8: istore #7
    //   10: fconst_0
    //   11: fstore #6
    //   13: iconst_0
    //   14: istore #11
    //   16: fconst_0
    //   17: fstore #5
    //   19: iconst_0
    //   20: istore #10
    //   22: iload #7
    //   24: aload_0
    //   25: getfield currentSize : I
    //   28: if_icmpge -> 585
    //   31: aload_0
    //   32: getfield mArrayValid : [Z
    //   35: iload #7
    //   37: baload
    //   38: ifne -> 67
    //   41: aload #16
    //   43: astore #13
    //   45: aload #15
    //   47: astore #14
    //   49: fload #6
    //   51: fstore_3
    //   52: iload #11
    //   54: istore #8
    //   56: fload #5
    //   58: fstore #4
    //   60: iload #10
    //   62: istore #9
    //   64: goto -> 553
    //   67: aload_0
    //   68: getfield mArrayValues : [F
    //   71: iload #7
    //   73: faload
    //   74: fstore_3
    //   75: aload_0
    //   76: getfield mCache : Landroid/support/constraint/solver/Cache;
    //   79: getfield mIndexedVariables : [Landroid/support/constraint/solver/SolverVariable;
    //   82: aload_0
    //   83: getfield mArrayIndices : [I
    //   86: iload #7
    //   88: iaload
    //   89: aaload
    //   90: astore #12
    //   92: fload_3
    //   93: fconst_0
    //   94: fcmpg
    //   95: ifge -> 137
    //   98: fload_3
    //   99: fstore_2
    //   100: fload_3
    //   101: ldc -0.001
    //   103: fcmpl
    //   104: ifle -> 174
    //   107: aload_0
    //   108: getfield mArrayValues : [F
    //   111: iload #7
    //   113: fconst_0
    //   114: fastore
    //   115: aload_0
    //   116: getfield mArrayValid : [Z
    //   119: iload #7
    //   121: iconst_0
    //   122: bastore
    //   123: aload #12
    //   125: aload_0
    //   126: getfield mRow : Landroid/support/constraint/solver/ArrayRow;
    //   129: invokevirtual removeFromRow : (Landroid/support/constraint/solver/ArrayRow;)V
    //   132: fconst_0
    //   133: fstore_2
    //   134: goto -> 174
    //   137: fload_3
    //   138: fstore_2
    //   139: fload_3
    //   140: ldc 0.001
    //   142: fcmpg
    //   143: ifge -> 174
    //   146: aload_0
    //   147: getfield mArrayValues : [F
    //   150: iload #7
    //   152: fconst_0
    //   153: fastore
    //   154: aload_0
    //   155: getfield mArrayValid : [Z
    //   158: iload #7
    //   160: iconst_0
    //   161: bastore
    //   162: aload #12
    //   164: aload_0
    //   165: getfield mRow : Landroid/support/constraint/solver/ArrayRow;
    //   168: invokevirtual removeFromRow : (Landroid/support/constraint/solver/ArrayRow;)V
    //   171: goto -> 132
    //   174: fload_2
    //   175: fconst_0
    //   176: fcmpl
    //   177: ifne -> 206
    //   180: aload #16
    //   182: astore #13
    //   184: aload #15
    //   186: astore #14
    //   188: fload #6
    //   190: fstore_3
    //   191: iload #11
    //   193: istore #8
    //   195: fload #5
    //   197: fstore #4
    //   199: iload #10
    //   201: istore #9
    //   203: goto -> 553
    //   206: aload #12
    //   208: getfield mType : Landroid/support/constraint/solver/SolverVariable$Type;
    //   211: getstatic android/support/constraint/solver/SolverVariable$Type.UNRESTRICTED : Landroid/support/constraint/solver/SolverVariable$Type;
    //   214: if_acmpne -> 356
    //   217: aload #16
    //   219: ifnonnull -> 252
    //   222: aload_0
    //   223: aload #12
    //   225: aload_1
    //   226: invokespecial isNew : (Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/LinearSystem;)Z
    //   229: istore #8
    //   231: aload #12
    //   233: astore #13
    //   235: aload #15
    //   237: astore #14
    //   239: fload_2
    //   240: fstore_3
    //   241: fload #5
    //   243: fstore #4
    //   245: iload #10
    //   247: istore #9
    //   249: goto -> 553
    //   252: fload #6
    //   254: fload_2
    //   255: fcmpl
    //   256: ifle -> 271
    //   259: aload_0
    //   260: aload #12
    //   262: aload_1
    //   263: invokespecial isNew : (Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/LinearSystem;)Z
    //   266: istore #8
    //   268: goto -> 231
    //   271: aload #16
    //   273: astore #13
    //   275: aload #15
    //   277: astore #14
    //   279: fload #6
    //   281: fstore_3
    //   282: iload #11
    //   284: istore #8
    //   286: fload #5
    //   288: fstore #4
    //   290: iload #10
    //   292: istore #9
    //   294: iload #11
    //   296: ifne -> 553
    //   299: aload #16
    //   301: astore #13
    //   303: aload #15
    //   305: astore #14
    //   307: fload #6
    //   309: fstore_3
    //   310: iload #11
    //   312: istore #8
    //   314: fload #5
    //   316: fstore #4
    //   318: iload #10
    //   320: istore #9
    //   322: aload_0
    //   323: aload #12
    //   325: aload_1
    //   326: invokespecial isNew : (Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/LinearSystem;)Z
    //   329: ifeq -> 553
    //   332: iconst_1
    //   333: istore #8
    //   335: aload #12
    //   337: astore #13
    //   339: aload #15
    //   341: astore #14
    //   343: fload_2
    //   344: fstore_3
    //   345: fload #5
    //   347: fstore #4
    //   349: iload #10
    //   351: istore #9
    //   353: goto -> 553
    //   356: aload #16
    //   358: astore #13
    //   360: aload #15
    //   362: astore #14
    //   364: fload #6
    //   366: fstore_3
    //   367: iload #11
    //   369: istore #8
    //   371: fload #5
    //   373: fstore #4
    //   375: iload #10
    //   377: istore #9
    //   379: aload #16
    //   381: ifnonnull -> 553
    //   384: aload #16
    //   386: astore #13
    //   388: aload #15
    //   390: astore #14
    //   392: fload #6
    //   394: fstore_3
    //   395: iload #11
    //   397: istore #8
    //   399: fload #5
    //   401: fstore #4
    //   403: iload #10
    //   405: istore #9
    //   407: fload_2
    //   408: fconst_0
    //   409: fcmpg
    //   410: ifge -> 553
    //   413: aload #15
    //   415: ifnonnull -> 452
    //   418: aload_0
    //   419: aload #12
    //   421: aload_1
    //   422: invokespecial isNew : (Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/LinearSystem;)Z
    //   425: istore #8
    //   427: iload #8
    //   429: istore #9
    //   431: aload #16
    //   433: astore #13
    //   435: aload #12
    //   437: astore #14
    //   439: fload #6
    //   441: fstore_3
    //   442: iload #11
    //   444: istore #8
    //   446: fload_2
    //   447: fstore #4
    //   449: goto -> 553
    //   452: fload #5
    //   454: fload_2
    //   455: fcmpl
    //   456: ifle -> 471
    //   459: aload_0
    //   460: aload #12
    //   462: aload_1
    //   463: invokespecial isNew : (Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/LinearSystem;)Z
    //   466: istore #8
    //   468: goto -> 427
    //   471: aload #16
    //   473: astore #13
    //   475: aload #15
    //   477: astore #14
    //   479: fload #6
    //   481: fstore_3
    //   482: iload #11
    //   484: istore #8
    //   486: fload #5
    //   488: fstore #4
    //   490: iload #10
    //   492: istore #9
    //   494: iload #10
    //   496: ifne -> 553
    //   499: aload #16
    //   501: astore #13
    //   503: aload #15
    //   505: astore #14
    //   507: fload #6
    //   509: fstore_3
    //   510: iload #11
    //   512: istore #8
    //   514: fload #5
    //   516: fstore #4
    //   518: iload #10
    //   520: istore #9
    //   522: aload_0
    //   523: aload #12
    //   525: aload_1
    //   526: invokespecial isNew : (Landroid/support/constraint/solver/SolverVariable;Landroid/support/constraint/solver/LinearSystem;)Z
    //   529: ifeq -> 553
    //   532: iconst_1
    //   533: istore #9
    //   535: fload_2
    //   536: fstore #4
    //   538: iload #11
    //   540: istore #8
    //   542: fload #6
    //   544: fstore_3
    //   545: aload #12
    //   547: astore #14
    //   549: aload #16
    //   551: astore #13
    //   553: iload #7
    //   555: iconst_1
    //   556: iadd
    //   557: istore #7
    //   559: aload #13
    //   561: astore #16
    //   563: aload #14
    //   565: astore #15
    //   567: fload_3
    //   568: fstore #6
    //   570: iload #8
    //   572: istore #11
    //   574: fload #4
    //   576: fstore #5
    //   578: iload #9
    //   580: istore #10
    //   582: goto -> 22
    //   585: aload #16
    //   587: ifnull -> 593
    //   590: aload #16
    //   592: areturn
    //   593: aload #15
    //   595: areturn }
  
  public final void clear() {
    for (byte b = 0; b < this.currentSize; b++) {
      SolverVariable solverVariable = this.mCache.mIndexedVariables[this.mArrayIndices[b]];
      if (solverVariable != null)
        solverVariable.removeFromRow(this.mRow); 
    } 
    this.currentSize = 0;
  }
  
  final boolean containsKey(SolverVariable paramSolverVariable) {
    for (byte b = 0; b < this.currentSize; b++) {
      if (this.mArrayValid[b] && this.mArrayIndices[b] == paramSolverVariable.id)
        return true; 
    } 
    return false;
  }
  
  public void display() {
    int i = this.currentSize;
    System.out.print("{ ");
    for (byte b = 0; b < i; b++) {
      if (this.mArrayValid[b]) {
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
    } 
    System.out.println(" }");
  }
  
  void divideByAmount(float paramFloat) {
    for (byte b = 0; b < this.currentSize; b++) {
      if (this.mArrayValid[b]) {
        float[] arrayOfFloat = this.mArrayValues;
        arrayOfFloat[b] = arrayOfFloat[b] / paramFloat;
      } 
    } 
  }
  
  public final float get(SolverVariable paramSolverVariable) {
    for (byte b = 0; b < this.currentSize; b++) {
      if (this.mArrayValid[b] && this.mArrayIndices[b] == paramSolverVariable.id)
        return this.mArrayValues[b]; 
    } 
    return 0.0F;
  }
  
  SolverVariable getPivotCandidate() { // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: iconst_0
    //   3: istore_1
    //   4: iload_1
    //   5: aload_0
    //   6: getfield currentSize : I
    //   9: if_icmpge -> 82
    //   12: aload_2
    //   13: astore_3
    //   14: aload_0
    //   15: getfield mArrayValid : [Z
    //   18: iload_1
    //   19: baload
    //   20: ifeq -> 73
    //   23: aload_2
    //   24: astore_3
    //   25: aload_0
    //   26: getfield mArrayValues : [F
    //   29: iload_1
    //   30: faload
    //   31: fconst_0
    //   32: fcmpg
    //   33: ifge -> 73
    //   36: aload_0
    //   37: getfield mCache : Landroid/support/constraint/solver/Cache;
    //   40: getfield mIndexedVariables : [Landroid/support/constraint/solver/SolverVariable;
    //   43: aload_0
    //   44: getfield mArrayIndices : [I
    //   47: iload_1
    //   48: iaload
    //   49: aaload
    //   50: astore #4
    //   52: aload_2
    //   53: ifnull -> 70
    //   56: aload_2
    //   57: astore_3
    //   58: aload_2
    //   59: getfield strength : I
    //   62: aload #4
    //   64: getfield strength : I
    //   67: if_icmpge -> 73
    //   70: aload #4
    //   72: astore_3
    //   73: iload_1
    //   74: iconst_1
    //   75: iadd
    //   76: istore_1
    //   77: aload_3
    //   78: astore_2
    //   79: goto -> 4
    //   82: aload_2
    //   83: areturn }
  
  SolverVariable getPivotCandidate(boolean[] paramArrayOfBoolean, SolverVariable paramSolverVariable) { // Byte code:
    //   0: aconst_null
    //   1: astore #7
    //   3: iconst_0
    //   4: istore #6
    //   6: fconst_0
    //   7: fstore_3
    //   8: iload #6
    //   10: aload_0
    //   11: getfield currentSize : I
    //   14: if_icmpge -> 180
    //   17: aload #7
    //   19: astore #8
    //   21: fload_3
    //   22: fstore #4
    //   24: aload_0
    //   25: getfield mArrayValid : [Z
    //   28: iload #6
    //   30: baload
    //   31: ifeq -> 164
    //   34: aload #7
    //   36: astore #8
    //   38: fload_3
    //   39: fstore #4
    //   41: aload_0
    //   42: getfield mArrayValues : [F
    //   45: iload #6
    //   47: faload
    //   48: fconst_0
    //   49: fcmpg
    //   50: ifge -> 164
    //   53: aload_0
    //   54: getfield mCache : Landroid/support/constraint/solver/Cache;
    //   57: getfield mIndexedVariables : [Landroid/support/constraint/solver/SolverVariable;
    //   60: aload_0
    //   61: getfield mArrayIndices : [I
    //   64: iload #6
    //   66: iaload
    //   67: aaload
    //   68: astore #9
    //   70: aload_1
    //   71: ifnull -> 91
    //   74: aload #7
    //   76: astore #8
    //   78: fload_3
    //   79: fstore #4
    //   81: aload_1
    //   82: aload #9
    //   84: getfield id : I
    //   87: baload
    //   88: ifne -> 164
    //   91: aload #7
    //   93: astore #8
    //   95: fload_3
    //   96: fstore #4
    //   98: aload #9
    //   100: aload_2
    //   101: if_acmpeq -> 164
    //   104: aload #9
    //   106: getfield mType : Landroid/support/constraint/solver/SolverVariable$Type;
    //   109: getstatic android/support/constraint/solver/SolverVariable$Type.SLACK : Landroid/support/constraint/solver/SolverVariable$Type;
    //   112: if_acmpeq -> 133
    //   115: aload #7
    //   117: astore #8
    //   119: fload_3
    //   120: fstore #4
    //   122: aload #9
    //   124: getfield mType : Landroid/support/constraint/solver/SolverVariable$Type;
    //   127: getstatic android/support/constraint/solver/SolverVariable$Type.ERROR : Landroid/support/constraint/solver/SolverVariable$Type;
    //   130: if_acmpne -> 164
    //   133: aload_0
    //   134: getfield mArrayValues : [F
    //   137: iload #6
    //   139: faload
    //   140: fstore #5
    //   142: aload #7
    //   144: astore #8
    //   146: fload_3
    //   147: fstore #4
    //   149: fload #5
    //   151: fload_3
    //   152: fcmpg
    //   153: ifge -> 164
    //   156: aload #9
    //   158: astore #8
    //   160: fload #5
    //   162: fstore #4
    //   164: iload #6
    //   166: iconst_1
    //   167: iadd
    //   168: istore #6
    //   170: aload #8
    //   172: astore #7
    //   174: fload #4
    //   176: fstore_3
    //   177: goto -> 8
    //   180: aload #7
    //   182: areturn }
  
  final SolverVariable getVariable(int paramInt) { return (paramInt < this.currentSize) ? this.mCache.mIndexedVariables[this.mArrayIndices[paramInt]] : null; }
  
  final float getVariableValue(int paramInt) { return (paramInt < this.currentSize) ? this.mArrayValues[paramInt] : 0.0F; }
  
  boolean hasAtLeastOnePositiveVariable() {
    for (byte b = 0; b < this.currentSize; b++) {
      if (this.mArrayValid[b] && this.mArrayValues[b] > 0.0F)
        return true; 
    } 
    return false;
  }
  
  void invert() {
    for (byte b = 0; b < this.currentSize; b++) {
      if (this.mArrayValid[b]) {
        float[] arrayOfFloat = this.mArrayValues;
        arrayOfFloat[b] = arrayOfFloat[b] * -1.0F;
      } 
    } 
  }
  
  public final void put(SolverVariable paramSolverVariable, float paramFloat) {
    for (byte b = 0; b < this.currentSize; b++) {
      if (this.mArrayIndices[b] == paramSolverVariable.id) {
        this.mArrayValues[b] = paramFloat;
        if (paramFloat == 0.0F) {
          this.mArrayValid[b] = false;
          paramSolverVariable.removeFromRow(this.mRow);
        } 
        return;
      } 
    } 
    if (this.currentSize >= this.mArrayIndices.length) {
      this.ROW_SIZE *= 2;
      this.mArrayValues = Arrays.copyOf(this.mArrayValues, this.ROW_SIZE);
      this.mArrayIndices = Arrays.copyOf(this.mArrayIndices, this.ROW_SIZE);
      this.mArrayValid = Arrays.copyOf(this.mArrayValid, this.ROW_SIZE);
    } 
    this.mArrayIndices[this.currentSize] = paramSolverVariable.id;
    this.mArrayValues[this.currentSize] = paramFloat;
    this.mArrayValid[this.currentSize] = true;
    if (paramFloat == 0.0F) {
      paramSolverVariable.removeFromRow(this.mRow);
      this.mArrayValid[this.currentSize] = false;
    } 
    paramSolverVariable.usageInRowCount++;
    paramSolverVariable.addToRow(this.mRow);
    this.currentSize++;
  }
  
  public final float remove(SolverVariable paramSolverVariable, boolean paramBoolean) {
    byte b;
    for (b = 0; b < this.currentSize; b++) {
      if (this.mArrayIndices[b] == paramSolverVariable.id) {
        float f = this.mArrayValues[b];
        this.mArrayValues[b] = 0.0F;
        this.mArrayValid[b] = false;
        if (paramBoolean) {
          paramSolverVariable.usageInRowCount--;
          paramSolverVariable.removeFromRow(this.mRow);
        } 
        return f;
      } 
    } 
    return 0.0F;
  }
  
  int sizeInBytes() { return this.mArrayIndices.length * 4 * 3 + 0 + 36; }
  
  public String toString() {
    String str = "";
    for (byte b = 0; b < this.currentSize; b++) {
      if (this.mArrayValid[b] && this.mArrayValues[b] != 0.0F) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(str);
        stringBuilder.append(" -> ");
        str = stringBuilder.toString();
        stringBuilder = new StringBuilder();
        stringBuilder.append(str);
        stringBuilder.append(this.mArrayValues[b]);
        stringBuilder.append(" : ");
        str = stringBuilder.toString();
        stringBuilder = new StringBuilder();
        stringBuilder.append(str);
        stringBuilder.append(this.mCache.mIndexedVariables[this.mArrayIndices[b]]);
        str = stringBuilder.toString();
      } 
    } 
    return str;
  }
  
  final void updateFromRow(ArrayRow paramArrayRow1, ArrayRow paramArrayRow2, boolean paramBoolean) {
    byte b;
    for (b = 0; b < this.currentSize; b++) {
      if (this.mArrayValid[b] && this.mArrayIndices[b] == paramArrayRow2.variable.id) {
        float f = this.mArrayValues[b];
        if (f != 0.0F) {
          this.mArrayValues[b] = 0.0F;
          this.mArrayValid[b] = false;
          if (paramBoolean)
            paramArrayRow2.variable.removeFromRow(this.mRow); 
          ArrayVariables arrayVariables = (ArrayVariables)paramArrayRow2.variables;
          byte b1;
          for (b1 = 0; b1 < arrayVariables.currentSize; b1++)
            add(this.mCache.mIndexedVariables[arrayVariables.mArrayIndices[b1]], arrayVariables.mArrayValues[b1] * f, paramBoolean); 
          paramArrayRow1.constantValue += paramArrayRow2.constantValue * f;
          if (paramBoolean)
            paramArrayRow2.variable.removeFromRow(paramArrayRow1); 
        } 
      } 
    } 
  }
  
  void updateFromSystem(ArrayRow paramArrayRow, ArrayRow[] paramArrayOfArrayRow) {
    byte b;
    for (b = 0; b < this.currentSize; b++) {
      if (this.mArrayValid[b]) {
        SolverVariable solverVariable = this.mCache.mIndexedVariables[this.mArrayIndices[b]];
        if (solverVariable.definitionId != -1) {
          float f = this.mArrayValues[b];
          this.mArrayValues[b] = 0.0F;
          this.mArrayValid[b] = false;
          solverVariable.removeFromRow(this.mRow);
          ArrayRow arrayRow = paramArrayOfArrayRow[solverVariable.definitionId];
          if (!arrayRow.isSimpleDefinition) {
            ArrayVariables arrayVariables = (ArrayVariables)arrayRow.variables;
            byte b1;
            for (b1 = 0; b1 < arrayVariables.currentSize; b1++)
              add(this.mCache.mIndexedVariables[arrayVariables.mArrayIndices[b1]], arrayVariables.mArrayValues[b1] * f, true); 
          } 
          paramArrayRow.constantValue += arrayRow.constantValue * f;
          arrayRow.variable.removeFromRow(paramArrayRow);
        } 
      } 
    } 
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/constraint/solver/ArrayVariables.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */