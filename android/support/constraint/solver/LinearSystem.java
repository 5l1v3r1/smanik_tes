package android.support.constraint.solver;

import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.support.constraint.solver.widgets.ConstraintWidget;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;

public class LinearSystem {
  private static final boolean DEBUG = false;
  
  public static final boolean FULL_DEBUG = false;
  
  private static int POOL_SIZE = 1000;
  
  public static Metrics sMetrics;
  
  private int TABLE_SIZE = 32;
  
  public boolean graphOptimizer = false;
  
  private boolean[] mAlreadyTestedCandidates = new boolean[this.TABLE_SIZE];
  
  final Cache mCache;
  
  private Row mGoal;
  
  private int mMaxColumns = this.TABLE_SIZE;
  
  private int mMaxRows = this.TABLE_SIZE;
  
  int mNumColumns = 1;
  
  int mNumRows = 0;
  
  private SolverVariable[] mPoolVariables = new SolverVariable[POOL_SIZE];
  
  private int mPoolVariablesCount = 0;
  
  ArrayRow[] mRows = null;
  
  private final Row mTempGoal;
  
  private HashMap<String, SolverVariable> mVariables = null;
  
  int mVariablesID = 0;
  
  private ArrayRow[] tempClientsCopy = new ArrayRow[this.TABLE_SIZE];
  
  static  {
  
  }
  
  public LinearSystem() {
    this.mRows = new ArrayRow[this.TABLE_SIZE];
    releaseRows();
    this.mCache = new Cache();
    this.mGoal = new GoalRow(this.mCache);
    this.mTempGoal = new ArrayRow(this.mCache);
  }
  
  private SolverVariable acquireSolverVariable(SolverVariable.Type paramType, String paramString) {
    SolverVariable solverVariable1;
    SolverVariable solverVariable2 = (SolverVariable)this.mCache.solverVariablePool.acquire();
    if (solverVariable2 == null) {
      solverVariable2 = new SolverVariable(paramType, paramString);
      solverVariable2.setType(paramType, paramString);
      solverVariable1 = solverVariable2;
    } else {
      solverVariable2.reset();
      solverVariable2.setType(solverVariable1, paramString);
      solverVariable1 = solverVariable2;
    } 
    if (this.mPoolVariablesCount >= POOL_SIZE) {
      POOL_SIZE *= 2;
      this.mPoolVariables = (SolverVariable[])Arrays.copyOf(this.mPoolVariables, POOL_SIZE);
    } 
    SolverVariable[] arrayOfSolverVariable = this.mPoolVariables;
    int i = this.mPoolVariablesCount;
    this.mPoolVariablesCount = i + 1;
    arrayOfSolverVariable[i] = solverVariable1;
    return solverVariable1;
  }
  
  private void addError(ArrayRow paramArrayRow) { paramArrayRow.addError(this, 0); }
  
  private final void addRow(ArrayRow paramArrayRow) {
    if (this.mRows[this.mNumRows] != null)
      this.mCache.arrayRowPool.release(this.mRows[this.mNumRows]); 
    this.mRows[this.mNumRows] = paramArrayRow;
    paramArrayRow.variable.definitionId = this.mNumRows;
    this.mNumRows++;
    paramArrayRow.variable.updateReferencesWithNewDefinition(paramArrayRow);
  }
  
  private void addSingleError(ArrayRow paramArrayRow, int paramInt) { addSingleError(paramArrayRow, paramInt, 0); }
  
  private void computeValues() {
    for (byte b = 0; b < this.mNumRows; b++) {
      ArrayRow arrayRow = this.mRows[b];
      arrayRow.variable.computedValue = arrayRow.constantValue;
    } 
  }
  
  public static ArrayRow createRowCentering(LinearSystem paramLinearSystem, SolverVariable paramSolverVariable1, SolverVariable paramSolverVariable2, int paramInt1, float paramFloat, SolverVariable paramSolverVariable3, SolverVariable paramSolverVariable4, int paramInt2, boolean paramBoolean) {
    ArrayRow arrayRow = paramLinearSystem.createRow();
    arrayRow.createRowCentering(paramSolverVariable1, paramSolverVariable2, paramInt1, paramFloat, paramSolverVariable3, paramSolverVariable4, paramInt2);
    if (paramBoolean)
      arrayRow.addError(paramLinearSystem, 4); 
    return arrayRow;
  }
  
  public static ArrayRow createRowDimensionPercent(LinearSystem paramLinearSystem, SolverVariable paramSolverVariable1, SolverVariable paramSolverVariable2, SolverVariable paramSolverVariable3, float paramFloat, boolean paramBoolean) {
    ArrayRow arrayRow = paramLinearSystem.createRow();
    if (paramBoolean)
      paramLinearSystem.addError(arrayRow); 
    return arrayRow.createRowDimensionPercent(paramSolverVariable1, paramSolverVariable2, paramSolverVariable3, paramFloat);
  }
  
  public static ArrayRow createRowEquals(LinearSystem paramLinearSystem, SolverVariable paramSolverVariable1, SolverVariable paramSolverVariable2, int paramInt, boolean paramBoolean) {
    ArrayRow arrayRow = paramLinearSystem.createRow();
    arrayRow.createRowEquals(paramSolverVariable1, paramSolverVariable2, paramInt);
    if (paramBoolean)
      paramLinearSystem.addSingleError(arrayRow, 1); 
    return arrayRow;
  }
  
  public static ArrayRow createRowGreaterThan(LinearSystem paramLinearSystem, SolverVariable paramSolverVariable1, SolverVariable paramSolverVariable2, int paramInt, boolean paramBoolean) {
    SolverVariable solverVariable = paramLinearSystem.createSlackVariable();
    ArrayRow arrayRow = paramLinearSystem.createRow();
    arrayRow.createRowGreaterThan(paramSolverVariable1, paramSolverVariable2, solverVariable, paramInt);
    if (paramBoolean)
      paramLinearSystem.addSingleError(arrayRow, (int)(arrayRow.variables.get(solverVariable) * -1.0F)); 
    return arrayRow;
  }
  
  public static ArrayRow createRowLowerThan(LinearSystem paramLinearSystem, SolverVariable paramSolverVariable1, SolverVariable paramSolverVariable2, int paramInt, boolean paramBoolean) {
    SolverVariable solverVariable = paramLinearSystem.createSlackVariable();
    ArrayRow arrayRow = paramLinearSystem.createRow();
    arrayRow.createRowLowerThan(paramSolverVariable1, paramSolverVariable2, solverVariable, paramInt);
    if (paramBoolean)
      paramLinearSystem.addSingleError(arrayRow, (int)(arrayRow.variables.get(solverVariable) * -1.0F)); 
    return arrayRow;
  }
  
  private SolverVariable createVariable(String paramString, SolverVariable.Type paramType) {
    if (sMetrics != null) {
      Metrics metrics = sMetrics;
      metrics.variables++;
    } 
    if (this.mNumColumns + 1 >= this.mMaxColumns)
      increaseTableSize(); 
    SolverVariable solverVariable = acquireSolverVariable(paramType, null);
    solverVariable.setName(paramString);
    this.mVariablesID++;
    this.mNumColumns++;
    solverVariable.id = this.mVariablesID;
    if (this.mVariables == null)
      this.mVariables = new HashMap(); 
    this.mVariables.put(paramString, solverVariable);
    this.mCache.mIndexedVariables[this.mVariablesID] = solverVariable;
    return solverVariable;
  }
  
  private void displayRows() {
    displaySolverVariables();
    String str = "";
    for (byte b = 0; b < this.mNumRows; b++) {
      StringBuilder stringBuilder1 = new StringBuilder();
      stringBuilder1.append(str);
      stringBuilder1.append(this.mRows[b]);
      str = stringBuilder1.toString();
      stringBuilder1 = new StringBuilder();
      stringBuilder1.append(str);
      stringBuilder1.append("\n");
      str = stringBuilder1.toString();
    } 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(str);
    stringBuilder.append(this.mGoal);
    stringBuilder.append("\n");
    str = stringBuilder.toString();
    System.out.println(str);
  }
  
  private void displaySolverVariables() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Display Rows (");
    stringBuilder.append(this.mNumRows);
    stringBuilder.append("x");
    stringBuilder.append(this.mNumColumns);
    stringBuilder.append(")\n");
    String str = stringBuilder.toString();
    System.out.println(str);
  }
  
  private int enforceBFS(Row paramRow) throws Exception {
    byte b = 0;
    while (true) {
      if (b < this.mNumRows) {
        if ((this.mRows[b]).variable.mType != SolverVariable.Type.UNRESTRICTED && (this.mRows[b]).constantValue < 0.0F) {
          b = 1;
          break;
        } 
        b++;
        continue;
      } 
      b = 0;
      break;
    } 
    if (b != 0) {
      boolean bool = false;
      for (b = 0; !bool; b = b4) {
        if (sMetrics != null) {
          Metrics metrics = sMetrics;
          metrics.bfs++;
        } 
        byte b4 = b + 1;
        byte b3 = 0;
        b = -1;
        byte b1 = -1;
        float f = Float.MAX_VALUE;
        byte b2;
        for (b2 = 0; b3 < this.mNumRows; b2 = b7) {
          byte b7;
          byte b6;
          byte b5;
          float f1;
          paramRow = this.mRows[b3];
          if (paramRow.variable.mType == SolverVariable.Type.UNRESTRICTED) {
            b5 = b;
            b6 = b1;
            f1 = f;
            b7 = b2;
          } else if (paramRow.isSimpleDefinition) {
            b5 = b;
            b6 = b1;
            f1 = f;
            b7 = b2;
          } else {
            b5 = b;
            b6 = b1;
            f1 = f;
            b7 = b2;
            if (paramRow.constantValue < 0.0F) {
              byte b8;
              for (b8 = 1;; b8++) {
                b5 = b;
                b6 = b1;
                f1 = f;
                b7 = b2;
                if (b8 < this.mNumColumns) {
                  SolverVariable solverVariable = this.mCache.mIndexedVariables[b8];
                  float f2 = paramRow.variables.get(solverVariable);
                  if (f2 <= 0.0F)
                    continue; 
                  b6 = b2;
                  b7 = 0;
                  b2 = b1;
                  b5 = b;
                  b = b7;
                  for (b1 = b6;; b1 = b6)
                    b++; 
                  b = b2;
                  b2 = b1;
                  b1 = b;
                  b = b5;
                  continue;
                } 
                break;
              } 
            } 
          } 
          b3++;
          b = b5;
          b1 = b6;
          f = f1;
        } 
        if (b != -1) {
          paramRow = this.mRows[b];
          paramRow.variable.definitionId = -1;
          if (sMetrics != null) {
            Metrics metrics = sMetrics;
            metrics.pivots++;
          } 
          paramRow.pivot(this.mCache.mIndexedVariables[b1]);
          paramRow.variable.definitionId = b;
          paramRow.variable.updateReferencesWithNewDefinition(paramRow);
        } else {
          bool = true;
        } 
      } 
      return b;
    } 
    return 0;
  }
  
  private String getDisplaySize(int paramInt) {
    paramInt *= 4;
    int i = paramInt / 1024;
    int j = i / 1024;
    if (j > 0) {
      StringBuilder stringBuilder1 = new StringBuilder();
      stringBuilder1.append("");
      stringBuilder1.append(j);
      stringBuilder1.append(" Mb");
      return stringBuilder1.toString();
    } 
    if (i > 0) {
      StringBuilder stringBuilder1 = new StringBuilder();
      stringBuilder1.append("");
      stringBuilder1.append(i);
      stringBuilder1.append(" Kb");
      return stringBuilder1.toString();
    } 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("");
    stringBuilder.append(paramInt);
    stringBuilder.append(" bytes");
    return stringBuilder.toString();
  }
  
  private String getDisplayStrength(int paramInt) { return (paramInt == 1) ? "LOW" : ((paramInt == 2) ? "MEDIUM" : ((paramInt == 3) ? "HIGH" : ((paramInt == 4) ? "HIGHEST" : ((paramInt == 5) ? "EQUALITY" : ((paramInt == 6) ? "FIXED" : "NONE"))))); }
  
  public static Metrics getMetrics() { return sMetrics; }
  
  private void increaseTableSize() {
    this.TABLE_SIZE *= 2;
    this.mRows = (ArrayRow[])Arrays.copyOf(this.mRows, this.TABLE_SIZE);
    this.mCache.mIndexedVariables = (SolverVariable[])Arrays.copyOf(this.mCache.mIndexedVariables, this.TABLE_SIZE);
    this.mAlreadyTestedCandidates = new boolean[this.TABLE_SIZE];
    this.mMaxColumns = this.TABLE_SIZE;
    this.mMaxRows = this.TABLE_SIZE;
    if (sMetrics != null) {
      Metrics metrics = sMetrics;
      metrics.tableSizeIncrease++;
      sMetrics.maxTableSize = Math.max(sMetrics.maxTableSize, this.TABLE_SIZE);
      sMetrics.lastTableSize = sMetrics.maxTableSize;
    } 
  }
  
  private final int optimize(Row paramRow, boolean paramBoolean) {
    if (sMetrics != null) {
      Metrics metrics = sMetrics;
      metrics.optimize++;
    } 
    byte b;
    for (b = 0; b < this.mNumColumns; b++)
      this.mAlreadyTestedCandidates[b] = false; 
    boolean bool = false;
    for (b = 0; !bool; b = b1) {
      if (sMetrics != null) {
        Metrics metrics = sMetrics;
        metrics.iterations++;
      } 
      byte b1 = b + 1;
      if (b1 >= this.mNumColumns * 2)
        return b1; 
      if (paramRow.getKey() != null)
        this.mAlreadyTestedCandidates[(paramRow.getKey()).id] = true; 
      SolverVariable solverVariable = paramRow.getPivotCandidate(this, this.mAlreadyTestedCandidates);
      if (solverVariable != null) {
        if (this.mAlreadyTestedCandidates[solverVariable.id])
          return b1; 
        this.mAlreadyTestedCandidates[solverVariable.id] = true;
      } 
      if (solverVariable != null) {
        b = 0;
        byte b2 = -1;
        for (float f = Float.MAX_VALUE; b < this.mNumRows; f = f1) {
          byte b3;
          float f1;
          ArrayRow arrayRow = this.mRows[b];
          if (arrayRow.variable.mType == SolverVariable.Type.UNRESTRICTED) {
            b3 = b2;
            f1 = f;
          } else if (arrayRow.isSimpleDefinition) {
            b3 = b2;
            f1 = f;
          } else {
            b3 = b2;
            f1 = f;
            if (arrayRow.hasVariable(solverVariable)) {
              float f2 = arrayRow.variables.get(solverVariable);
              b3 = b2;
              f1 = f;
              if (f2 < 0.0F) {
                f2 = -arrayRow.constantValue / f2;
                b3 = b2;
                f1 = f;
                if (f2 < f) {
                  b3 = b;
                  f1 = f2;
                } 
              } 
            } 
          } 
          b++;
          b2 = b3;
        } 
        if (b2 > -1) {
          ArrayRow arrayRow = this.mRows[b2];
          arrayRow.variable.definitionId = -1;
          if (sMetrics != null) {
            Metrics metrics = sMetrics;
            metrics.pivots++;
          } 
          arrayRow.pivot(solverVariable);
          arrayRow.variable.definitionId = b2;
          arrayRow.variable.updateReferencesWithNewDefinition(arrayRow);
          b = b1;
          continue;
        } 
      } 
      bool = true;
    } 
    return b;
  }
  
  private void releaseRows() {
    for (byte b = 0; b < this.mRows.length; b++) {
      ArrayRow arrayRow = this.mRows[b];
      if (arrayRow != null)
        this.mCache.arrayRowPool.release(arrayRow); 
      this.mRows[b] = null;
    } 
  }
  
  private final void updateRowFromVariables(ArrayRow paramArrayRow) {
    if (this.mNumRows > 0) {
      paramArrayRow.variables.updateFromSystem(paramArrayRow, this.mRows);
      if (paramArrayRow.variables.currentSize == 0)
        paramArrayRow.isSimpleDefinition = true; 
    } 
  }
  
  public void addCenterPoint(ConstraintWidget paramConstraintWidget1, ConstraintWidget paramConstraintWidget2, float paramFloat, int paramInt) {
    SolverVariable solverVariable3 = createObjectVariable(paramConstraintWidget1.getAnchor(ConstraintAnchor.Type.LEFT));
    SolverVariable solverVariable5 = createObjectVariable(paramConstraintWidget1.getAnchor(ConstraintAnchor.Type.TOP));
    SolverVariable solverVariable4 = createObjectVariable(paramConstraintWidget1.getAnchor(ConstraintAnchor.Type.RIGHT));
    SolverVariable solverVariable7 = createObjectVariable(paramConstraintWidget1.getAnchor(ConstraintAnchor.Type.BOTTOM));
    SolverVariable solverVariable1 = createObjectVariable(paramConstraintWidget2.getAnchor(ConstraintAnchor.Type.LEFT));
    SolverVariable solverVariable8 = createObjectVariable(paramConstraintWidget2.getAnchor(ConstraintAnchor.Type.TOP));
    SolverVariable solverVariable6 = createObjectVariable(paramConstraintWidget2.getAnchor(ConstraintAnchor.Type.RIGHT));
    SolverVariable solverVariable2 = createObjectVariable(paramConstraintWidget2.getAnchor(ConstraintAnchor.Type.BOTTOM));
    ArrayRow arrayRow2 = createRow();
    double d1 = paramFloat;
    double d2 = Math.sin(d1);
    double d3 = paramInt;
    arrayRow2.createRowWithAngle(solverVariable5, solverVariable7, solverVariable8, solverVariable2, (float)(d2 * d3));
    addConstraint(arrayRow2);
    ArrayRow arrayRow1 = createRow();
    arrayRow1.createRowWithAngle(solverVariable3, solverVariable4, solverVariable1, solverVariable6, (float)(Math.cos(d1) * d3));
    addConstraint(arrayRow1);
  }
  
  public void addCentering(SolverVariable paramSolverVariable1, SolverVariable paramSolverVariable2, int paramInt1, float paramFloat, SolverVariable paramSolverVariable3, SolverVariable paramSolverVariable4, int paramInt2, int paramInt3) {
    ArrayRow arrayRow = createRow();
    arrayRow.createRowCentering(paramSolverVariable1, paramSolverVariable2, paramInt1, paramFloat, paramSolverVariable3, paramSolverVariable4, paramInt2);
    if (paramInt3 != 6)
      arrayRow.addError(this, paramInt3); 
    addConstraint(arrayRow);
  }
  
  public void addConstraint(ArrayRow paramArrayRow) {
    if (paramArrayRow == null)
      return; 
    if (sMetrics != null) {
      Metrics metrics = sMetrics;
      metrics.constraints++;
      if (paramArrayRow.isSimpleDefinition) {
        metrics = sMetrics;
        metrics.simpleconstraints++;
      } 
    } 
    if (this.mNumRows + 1 >= this.mMaxRows || this.mNumColumns + 1 >= this.mMaxColumns)
      increaseTableSize(); 
    byte b1 = 0;
    byte b2 = 0;
    if (!paramArrayRow.isSimpleDefinition) {
      updateRowFromVariables(paramArrayRow);
      if (paramArrayRow.isEmpty())
        return; 
      paramArrayRow.ensurePositiveConstant();
      b1 = b2;
      if (paramArrayRow.chooseSubject(this)) {
        SolverVariable solverVariable = createExtraVariable();
        paramArrayRow.variable = solverVariable;
        addRow(paramArrayRow);
        this.mTempGoal.initFromRow(paramArrayRow);
        optimize(this.mTempGoal, true);
        if (solverVariable.definitionId == -1) {
          if (paramArrayRow.variable == solverVariable) {
            solverVariable = paramArrayRow.pickPivot(solverVariable);
            if (solverVariable != null) {
              if (sMetrics != null) {
                Metrics metrics = sMetrics;
                metrics.pivots++;
              } 
              paramArrayRow.pivot(solverVariable);
            } 
          } 
          if (!paramArrayRow.isSimpleDefinition)
            paramArrayRow.variable.updateReferencesWithNewDefinition(paramArrayRow); 
          this.mNumRows--;
        } 
        b1 = 1;
      } 
      if (!paramArrayRow.hasKeyVariable())
        return; 
    } 
    if (b1 == 0)
      addRow(paramArrayRow); 
  }
  
  public ArrayRow addEquality(SolverVariable paramSolverVariable1, SolverVariable paramSolverVariable2, int paramInt1, int paramInt2) {
    ArrayRow arrayRow = createRow();
    arrayRow.createRowEquals(paramSolverVariable1, paramSolverVariable2, paramInt1);
    if (paramInt2 != 6)
      arrayRow.addError(this, paramInt2); 
    addConstraint(arrayRow);
    return arrayRow;
  }
  
  public void addEquality(SolverVariable paramSolverVariable, int paramInt) {
    int i = paramSolverVariable.definitionId;
    if (paramSolverVariable.definitionId != -1) {
      ArrayRow arrayRow1 = this.mRows[i];
      if (arrayRow1.isSimpleDefinition) {
        arrayRow1.constantValue = paramInt;
        return;
      } 
      if (arrayRow1.variables.currentSize == 0) {
        arrayRow1.isSimpleDefinition = true;
        arrayRow1.constantValue = paramInt;
        return;
      } 
      arrayRow1 = createRow();
      arrayRow1.createRowEquals(paramSolverVariable, paramInt);
      addConstraint(arrayRow1);
      return;
    } 
    ArrayRow arrayRow = createRow();
    arrayRow.createRowDefinition(paramSolverVariable, paramInt);
    addConstraint(arrayRow);
  }
  
  public void addEquality(SolverVariable paramSolverVariable, int paramInt1, int paramInt2) {
    int i = paramSolverVariable.definitionId;
    if (paramSolverVariable.definitionId != -1) {
      ArrayRow arrayRow1 = this.mRows[i];
      if (arrayRow1.isSimpleDefinition) {
        arrayRow1.constantValue = paramInt1;
        return;
      } 
      arrayRow1 = createRow();
      arrayRow1.createRowEquals(paramSolverVariable, paramInt1);
      arrayRow1.addError(this, paramInt2);
      addConstraint(arrayRow1);
      return;
    } 
    ArrayRow arrayRow = createRow();
    arrayRow.createRowDefinition(paramSolverVariable, paramInt1);
    arrayRow.addError(this, paramInt2);
    addConstraint(arrayRow);
  }
  
  public void addGreaterBarrier(SolverVariable paramSolverVariable1, SolverVariable paramSolverVariable2, boolean paramBoolean) {
    ArrayRow arrayRow = createRow();
    SolverVariable solverVariable = createSlackVariable();
    solverVariable.strength = 0;
    arrayRow.createRowGreaterThan(paramSolverVariable1, paramSolverVariable2, solverVariable, 0);
    if (paramBoolean)
      addSingleError(arrayRow, (int)(arrayRow.variables.get(solverVariable) * -1.0F), 1); 
    addConstraint(arrayRow);
  }
  
  public void addGreaterThan(SolverVariable paramSolverVariable, int paramInt) {
    ArrayRow arrayRow = createRow();
    SolverVariable solverVariable = createSlackVariable();
    solverVariable.strength = 0;
    arrayRow.createRowGreaterThan(paramSolverVariable, paramInt, solverVariable);
    addConstraint(arrayRow);
  }
  
  public void addGreaterThan(SolverVariable paramSolverVariable1, SolverVariable paramSolverVariable2, int paramInt1, int paramInt2) {
    ArrayRow arrayRow = createRow();
    SolverVariable solverVariable = createSlackVariable();
    solverVariable.strength = 0;
    arrayRow.createRowGreaterThan(paramSolverVariable1, paramSolverVariable2, solverVariable, paramInt1);
    if (paramInt2 != 6)
      addSingleError(arrayRow, (int)(arrayRow.variables.get(solverVariable) * -1.0F), paramInt2); 
    addConstraint(arrayRow);
  }
  
  public void addLowerBarrier(SolverVariable paramSolverVariable1, SolverVariable paramSolverVariable2, boolean paramBoolean) {
    ArrayRow arrayRow = createRow();
    SolverVariable solverVariable = createSlackVariable();
    solverVariable.strength = 0;
    arrayRow.createRowLowerThan(paramSolverVariable1, paramSolverVariable2, solverVariable, 0);
    if (paramBoolean)
      addSingleError(arrayRow, (int)(arrayRow.variables.get(solverVariable) * -1.0F), 1); 
    addConstraint(arrayRow);
  }
  
  public void addLowerThan(SolverVariable paramSolverVariable1, SolverVariable paramSolverVariable2, int paramInt1, int paramInt2) {
    ArrayRow arrayRow = createRow();
    SolverVariable solverVariable = createSlackVariable();
    solverVariable.strength = 0;
    arrayRow.createRowLowerThan(paramSolverVariable1, paramSolverVariable2, solverVariable, paramInt1);
    if (paramInt2 != 6)
      addSingleError(arrayRow, (int)(arrayRow.variables.get(solverVariable) * -1.0F), paramInt2); 
    addConstraint(arrayRow);
  }
  
  public void addRatio(SolverVariable paramSolverVariable1, SolverVariable paramSolverVariable2, SolverVariable paramSolverVariable3, SolverVariable paramSolverVariable4, float paramFloat, int paramInt) {
    ArrayRow arrayRow = createRow();
    arrayRow.createRowDimensionRatio(paramSolverVariable1, paramSolverVariable2, paramSolverVariable3, paramSolverVariable4, paramFloat);
    if (paramInt != 6)
      arrayRow.addError(this, paramInt); 
    addConstraint(arrayRow);
  }
  
  void addSingleError(ArrayRow paramArrayRow, int paramInt1, int paramInt2) { paramArrayRow.addSingleError(createErrorVariable(paramInt2, null), paramInt1); }
  
  public SolverVariable createErrorVariable(int paramInt, String paramString) {
    if (sMetrics != null) {
      Metrics metrics = sMetrics;
      metrics.errors++;
    } 
    if (this.mNumColumns + 1 >= this.mMaxColumns)
      increaseTableSize(); 
    SolverVariable solverVariable = acquireSolverVariable(SolverVariable.Type.ERROR, paramString);
    this.mVariablesID++;
    this.mNumColumns++;
    solverVariable.id = this.mVariablesID;
    solverVariable.strength = paramInt;
    this.mCache.mIndexedVariables[this.mVariablesID] = solverVariable;
    this.mGoal.addError(solverVariable);
    return solverVariable;
  }
  
  public SolverVariable createExtraVariable() {
    if (sMetrics != null) {
      Metrics metrics = sMetrics;
      metrics.extravariables++;
    } 
    if (this.mNumColumns + 1 >= this.mMaxColumns)
      increaseTableSize(); 
    SolverVariable solverVariable = acquireSolverVariable(SolverVariable.Type.SLACK, null);
    this.mVariablesID++;
    this.mNumColumns++;
    solverVariable.id = this.mVariablesID;
    this.mCache.mIndexedVariables[this.mVariablesID] = solverVariable;
    return solverVariable;
  }
  
  public SolverVariable createObjectVariable(Object paramObject) {
    SolverVariable solverVariable = null;
    if (paramObject == null)
      return null; 
    if (this.mNumColumns + 1 >= this.mMaxColumns)
      increaseTableSize(); 
    if (paramObject instanceof ConstraintAnchor) {
      ConstraintAnchor constraintAnchor = (ConstraintAnchor)paramObject;
      solverVariable = constraintAnchor.getSolverVariable();
      paramObject = solverVariable;
      if (solverVariable == null) {
        constraintAnchor.resetSolverVariable(this.mCache);
        paramObject = constraintAnchor.getSolverVariable();
      } 
      if (paramObject.id != -1 && paramObject.id <= this.mVariablesID) {
        Object object = paramObject;
        if (this.mCache.mIndexedVariables[paramObject.id] == null) {
          if (paramObject.id != -1)
            paramObject.reset(); 
          this.mVariablesID++;
          this.mNumColumns++;
          paramObject.id = this.mVariablesID;
          paramObject.mType = SolverVariable.Type.UNRESTRICTED;
          this.mCache.mIndexedVariables[this.mVariablesID] = paramObject;
          return paramObject;
        } 
        return object;
      } 
    } else {
      return solverVariable;
    } 
    if (paramObject.id != -1)
      paramObject.reset(); 
    this.mVariablesID++;
    this.mNumColumns++;
    paramObject.id = this.mVariablesID;
    paramObject.mType = SolverVariable.Type.UNRESTRICTED;
    this.mCache.mIndexedVariables[this.mVariablesID] = paramObject;
    return paramObject;
  }
  
  public ArrayRow createRow() {
    ArrayRow arrayRow = (ArrayRow)this.mCache.arrayRowPool.acquire();
    if (arrayRow == null) {
      arrayRow = new ArrayRow(this.mCache);
    } else {
      arrayRow.reset();
    } 
    SolverVariable.increaseErrorId();
    return arrayRow;
  }
  
  public SolverVariable createSlackVariable() {
    if (sMetrics != null) {
      Metrics metrics = sMetrics;
      metrics.slackvariables++;
    } 
    if (this.mNumColumns + 1 >= this.mMaxColumns)
      increaseTableSize(); 
    SolverVariable solverVariable = acquireSolverVariable(SolverVariable.Type.SLACK, null);
    this.mVariablesID++;
    this.mNumColumns++;
    solverVariable.id = this.mVariablesID;
    this.mCache.mIndexedVariables[this.mVariablesID] = solverVariable;
    return solverVariable;
  }
  
  void displayReadableRows() {
    displaySolverVariables();
    String str1 = " #  ";
    for (byte b = 0; b < this.mNumRows; b++) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(str1);
      stringBuilder.append(this.mRows[b].toReadableString());
      str1 = stringBuilder.toString();
      stringBuilder = new StringBuilder();
      stringBuilder.append(str1);
      stringBuilder.append("\n #  ");
      str1 = stringBuilder.toString();
    } 
    String str2 = str1;
    if (this.mGoal != null) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(str1);
      stringBuilder.append(this.mGoal);
      stringBuilder.append("\n");
      str2 = stringBuilder.toString();
    } 
    System.out.println(str2);
  }
  
  void displaySystemInformations() {
    byte b = 0;
    int i;
    for (i = 0; b < this.TABLE_SIZE; i = k) {
      int k = i;
      if (this.mRows[b] != null)
        k = i + this.mRows[b].sizeInBytes(); 
      b++;
    } 
    b = 0;
    int j;
    for (j = 0; b < this.mNumRows; j = k) {
      int k = j;
      if (this.mRows[b] != null)
        k = j + this.mRows[b].sizeInBytes(); 
      b++;
    } 
    PrintStream printStream = System.out;
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Linear System -> Table size: ");
    stringBuilder.append(this.TABLE_SIZE);
    stringBuilder.append(" (");
    stringBuilder.append(getDisplaySize(this.TABLE_SIZE * this.TABLE_SIZE));
    stringBuilder.append(") -- row sizes: ");
    stringBuilder.append(getDisplaySize(i));
    stringBuilder.append(", actual size: ");
    stringBuilder.append(getDisplaySize(j));
    stringBuilder.append(" rows: ");
    stringBuilder.append(this.mNumRows);
    stringBuilder.append("/");
    stringBuilder.append(this.mMaxRows);
    stringBuilder.append(" cols: ");
    stringBuilder.append(this.mNumColumns);
    stringBuilder.append("/");
    stringBuilder.append(this.mMaxColumns);
    stringBuilder.append(" ");
    stringBuilder.append(0);
    stringBuilder.append(" occupied cells, ");
    stringBuilder.append(getDisplaySize(0));
    printStream.println(stringBuilder.toString());
  }
  
  public void displayVariablesReadableRows() {
    displaySolverVariables();
    String str = "";
    byte b = 0;
    while (b < this.mNumRows) {
      String str1 = str;
      if ((this.mRows[b]).variable.mType == SolverVariable.Type.UNRESTRICTED) {
        StringBuilder stringBuilder1 = new StringBuilder();
        stringBuilder1.append(str);
        stringBuilder1.append(this.mRows[b].toReadableString());
        str = stringBuilder1.toString();
        stringBuilder1 = new StringBuilder();
        stringBuilder1.append(str);
        stringBuilder1.append("\n");
        str1 = stringBuilder1.toString();
      } 
      b++;
      str = str1;
    } 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(str);
    stringBuilder.append(this.mGoal);
    stringBuilder.append("\n");
    str = stringBuilder.toString();
    System.out.println(str);
  }
  
  public void fillMetrics(Metrics paramMetrics) { sMetrics = paramMetrics; }
  
  public Cache getCache() { return this.mCache; }
  
  Row getGoal() { return this.mGoal; }
  
  public int getMemoryUsed() {
    byte b = 0;
    int i;
    for (i = 0; b < this.mNumRows; i = j) {
      int j = i;
      if (this.mRows[b] != null)
        j = i + this.mRows[b].sizeInBytes(); 
      b++;
    } 
    return i;
  }
  
  public int getNumEquations() { return this.mNumRows; }
  
  public int getNumVariables() { return this.mVariablesID; }
  
  public int getObjectVariableValue(Object paramObject) {
    paramObject = ((ConstraintAnchor)paramObject).getSolverVariable();
    return (paramObject != null) ? (int)(paramObject.computedValue + 0.5F) : 0;
  }
  
  ArrayRow getRow(int paramInt) { return this.mRows[paramInt]; }
  
  float getValueFor(String paramString) {
    SolverVariable solverVariable = getVariable(paramString, SolverVariable.Type.UNRESTRICTED);
    return (solverVariable == null) ? 0.0F : solverVariable.computedValue;
  }
  
  SolverVariable getVariable(String paramString, SolverVariable.Type paramType) {
    if (this.mVariables == null)
      this.mVariables = new HashMap(); 
    SolverVariable solverVariable2 = (SolverVariable)this.mVariables.get(paramString);
    SolverVariable solverVariable1 = solverVariable2;
    if (solverVariable2 == null)
      solverVariable1 = createVariable(paramString, paramType); 
    return solverVariable1;
  }
  
  public void minimize() {
    if (sMetrics != null) {
      Metrics metrics = sMetrics;
      metrics.minimize++;
    } 
    if (this.graphOptimizer) {
      if (sMetrics != null) {
        Metrics metrics = sMetrics;
        metrics.graphOptimizer++;
      } 
      byte b2 = 0;
      byte b1 = 0;
      while (true) {
        if (b1 < this.mNumRows) {
          if (!(this.mRows[b1]).isSimpleDefinition) {
            b1 = b2;
            break;
          } 
          b1++;
          continue;
        } 
        b1 = 1;
        break;
      } 
      if (b1 == 0) {
        minimizeGoal(this.mGoal);
        return;
      } 
      if (sMetrics != null) {
        Metrics metrics = sMetrics;
        metrics.fullySolved++;
      } 
      computeValues();
      return;
    } 
    minimizeGoal(this.mGoal);
  }
  
  void minimizeGoal(Row paramRow) throws Exception {
    if (sMetrics != null) {
      Metrics metrics = sMetrics;
      metrics.minimizeGoal++;
      sMetrics.maxVariables = Math.max(sMetrics.maxVariables, this.mNumColumns);
      sMetrics.maxRows = Math.max(sMetrics.maxRows, this.mNumRows);
    } 
    updateRowFromVariables((ArrayRow)paramRow);
    enforceBFS(paramRow);
    optimize(paramRow, false);
    computeValues();
  }
  
  public void reset() {
    byte b;
    for (b = 0; b < this.mCache.mIndexedVariables.length; b++) {
      SolverVariable solverVariable = this.mCache.mIndexedVariables[b];
      if (solverVariable != null)
        solverVariable.reset(); 
    } 
    this.mCache.solverVariablePool.releaseAll(this.mPoolVariables, this.mPoolVariablesCount);
    this.mPoolVariablesCount = 0;
    Arrays.fill(this.mCache.mIndexedVariables, null);
    if (this.mVariables != null)
      this.mVariables.clear(); 
    this.mVariablesID = 0;
    this.mGoal.clear();
    this.mNumColumns = 1;
    for (b = 0; b < this.mNumRows; b++)
      (this.mRows[b]).used = false; 
    releaseRows();
    this.mNumRows = 0;
  }
  
  static interface Row {
    void addError(SolverVariable param1SolverVariable);
    
    void clear();
    
    SolverVariable getKey();
    
    SolverVariable getPivotCandidate(LinearSystem param1LinearSystem, boolean[] param1ArrayOfBoolean);
    
    void initFromRow(Row param1Row) throws Exception;
    
    boolean isEmpty();
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/constraint/solver/LinearSystem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */