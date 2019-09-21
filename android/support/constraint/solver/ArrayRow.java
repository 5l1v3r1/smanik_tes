package android.support.constraint.solver;

public class ArrayRow implements LinearSystem.Row {
  private static final boolean DEBUG = false;
  
  float constantValue = 0.0F;
  
  boolean isSimpleDefinition = false;
  
  boolean used = false;
  
  SolverVariable variable = null;
  
  public final ArrayLinkedVariables variables;
  
  public ArrayRow(Cache paramCache) { this.variables = new ArrayLinkedVariables(this, paramCache); }
  
  public ArrayRow addError(LinearSystem paramLinearSystem, int paramInt) {
    this.variables.put(paramLinearSystem.createErrorVariable(paramInt, "ep"), 1.0F);
    this.variables.put(paramLinearSystem.createErrorVariable(paramInt, "em"), -1.0F);
    return this;
  }
  
  public void addError(SolverVariable paramSolverVariable) {
    int i = paramSolverVariable.strength;
    float f = 1.0F;
    if (i != 1)
      if (paramSolverVariable.strength == 2) {
        f = 1000.0F;
      } else if (paramSolverVariable.strength == 3) {
        f = 1000000.0F;
      } else if (paramSolverVariable.strength == 4) {
        f = 1.0E9F;
      } else if (paramSolverVariable.strength == 5) {
        f = 1.0E12F;
      }  
    this.variables.put(paramSolverVariable, f);
  }
  
  ArrayRow addSingleError(SolverVariable paramSolverVariable, int paramInt) {
    this.variables.put(paramSolverVariable, paramInt);
    return this;
  }
  
  boolean chooseSubject(LinearSystem paramLinearSystem) {
    boolean bool;
    SolverVariable solverVariable = this.variables.chooseSubject(paramLinearSystem);
    if (solverVariable == null) {
      bool = true;
    } else {
      pivot(solverVariable);
      bool = false;
    } 
    if (this.variables.currentSize == 0)
      this.isSimpleDefinition = true; 
    return bool;
  }
  
  public void clear() {
    this.variables.clear();
    this.variable = null;
    this.constantValue = 0.0F;
  }
  
  ArrayRow createRowCentering(SolverVariable paramSolverVariable1, SolverVariable paramSolverVariable2, int paramInt1, float paramFloat, SolverVariable paramSolverVariable3, SolverVariable paramSolverVariable4, int paramInt2) {
    if (paramSolverVariable2 == paramSolverVariable3) {
      this.variables.put(paramSolverVariable1, 1.0F);
      this.variables.put(paramSolverVariable4, 1.0F);
      this.variables.put(paramSolverVariable2, -2.0F);
      return this;
    } 
    if (paramFloat == 0.5F) {
      this.variables.put(paramSolverVariable1, 1.0F);
      this.variables.put(paramSolverVariable2, -1.0F);
      this.variables.put(paramSolverVariable3, -1.0F);
      this.variables.put(paramSolverVariable4, 1.0F);
      if (paramInt1 > 0 || paramInt2 > 0) {
        this.constantValue = (-paramInt1 + paramInt2);
        return this;
      } 
    } else {
      if (paramFloat <= 0.0F) {
        this.variables.put(paramSolverVariable1, -1.0F);
        this.variables.put(paramSolverVariable2, 1.0F);
        this.constantValue = paramInt1;
        return this;
      } 
      if (paramFloat >= 1.0F) {
        this.variables.put(paramSolverVariable3, -1.0F);
        this.variables.put(paramSolverVariable4, 1.0F);
        this.constantValue = paramInt2;
        return this;
      } 
      ArrayLinkedVariables arrayLinkedVariables = this.variables;
      float f = 1.0F - paramFloat;
      arrayLinkedVariables.put(paramSolverVariable1, f * 1.0F);
      this.variables.put(paramSolverVariable2, f * -1.0F);
      this.variables.put(paramSolverVariable3, -1.0F * paramFloat);
      this.variables.put(paramSolverVariable4, 1.0F * paramFloat);
      if (paramInt1 > 0 || paramInt2 > 0)
        this.constantValue = -paramInt1 * f + paramInt2 * paramFloat; 
    } 
    return this;
  }
  
  ArrayRow createRowDefinition(SolverVariable paramSolverVariable, int paramInt) {
    this.variable = paramSolverVariable;
    float f = paramInt;
    paramSolverVariable.computedValue = f;
    this.constantValue = f;
    this.isSimpleDefinition = true;
    return this;
  }
  
  ArrayRow createRowDimensionPercent(SolverVariable paramSolverVariable1, SolverVariable paramSolverVariable2, SolverVariable paramSolverVariable3, float paramFloat) {
    this.variables.put(paramSolverVariable1, -1.0F);
    this.variables.put(paramSolverVariable2, 1.0F - paramFloat);
    this.variables.put(paramSolverVariable3, paramFloat);
    return this;
  }
  
  public ArrayRow createRowDimensionRatio(SolverVariable paramSolverVariable1, SolverVariable paramSolverVariable2, SolverVariable paramSolverVariable3, SolverVariable paramSolverVariable4, float paramFloat) {
    this.variables.put(paramSolverVariable1, -1.0F);
    this.variables.put(paramSolverVariable2, 1.0F);
    this.variables.put(paramSolverVariable3, paramFloat);
    this.variables.put(paramSolverVariable4, -paramFloat);
    return this;
  }
  
  public ArrayRow createRowEqualDimension(float paramFloat1, float paramFloat2, float paramFloat3, SolverVariable paramSolverVariable1, int paramInt1, SolverVariable paramSolverVariable2, int paramInt2, SolverVariable paramSolverVariable3, int paramInt3, SolverVariable paramSolverVariable4, int paramInt4) {
    if (paramFloat2 == 0.0F || paramFloat1 == paramFloat3) {
      this.constantValue = (-paramInt1 - paramInt2 + paramInt3 + paramInt4);
      this.variables.put(paramSolverVariable1, 1.0F);
      this.variables.put(paramSolverVariable2, -1.0F);
      this.variables.put(paramSolverVariable4, 1.0F);
      this.variables.put(paramSolverVariable3, -1.0F);
      return this;
    } 
    paramFloat1 = paramFloat1 / paramFloat2 / paramFloat3 / paramFloat2;
    this.constantValue = (-paramInt1 - paramInt2) + paramInt3 * paramFloat1 + paramInt4 * paramFloat1;
    this.variables.put(paramSolverVariable1, 1.0F);
    this.variables.put(paramSolverVariable2, -1.0F);
    this.variables.put(paramSolverVariable4, paramFloat1);
    this.variables.put(paramSolverVariable3, -paramFloat1);
    return this;
  }
  
  public ArrayRow createRowEqualMatchDimensions(float paramFloat1, float paramFloat2, float paramFloat3, SolverVariable paramSolverVariable1, SolverVariable paramSolverVariable2, SolverVariable paramSolverVariable3, SolverVariable paramSolverVariable4) {
    if (paramFloat2 == 0.0F || paramFloat1 == paramFloat3) {
      this.constantValue = 0.0F;
      this.variables.put(paramSolverVariable1, 1.0F);
      this.variables.put(paramSolverVariable2, -1.0F);
      this.variables.put(paramSolverVariable4, 1.0F);
      this.variables.put(paramSolverVariable3, -1.0F);
      return this;
    } 
    paramFloat1 = paramFloat1 / paramFloat2 / paramFloat3 / paramFloat2;
    this.constantValue = 0.0F;
    this.variables.put(paramSolverVariable1, 1.0F);
    this.variables.put(paramSolverVariable2, -1.0F);
    this.variables.put(paramSolverVariable4, paramFloat1);
    this.variables.put(paramSolverVariable3, -paramFloat1);
    return this;
  }
  
  public ArrayRow createRowEquals(SolverVariable paramSolverVariable, int paramInt) {
    if (paramInt < 0) {
      this.constantValue = (paramInt * -1);
      this.variables.put(paramSolverVariable, 1.0F);
      return this;
    } 
    this.constantValue = paramInt;
    this.variables.put(paramSolverVariable, -1.0F);
    return this;
  }
  
  public ArrayRow createRowEquals(SolverVariable paramSolverVariable1, SolverVariable paramSolverVariable2, int paramInt) {
    byte b = 0;
    int i = 0;
    if (paramInt != 0) {
      b = i;
      i = paramInt;
      if (paramInt < 0) {
        i = paramInt * -1;
        b = 1;
      } 
      this.constantValue = i;
    } 
    if (b == 0) {
      this.variables.put(paramSolverVariable1, -1.0F);
      this.variables.put(paramSolverVariable2, 1.0F);
      return this;
    } 
    this.variables.put(paramSolverVariable1, 1.0F);
    this.variables.put(paramSolverVariable2, -1.0F);
    return this;
  }
  
  public ArrayRow createRowGreaterThan(SolverVariable paramSolverVariable1, int paramInt, SolverVariable paramSolverVariable2) {
    this.constantValue = paramInt;
    this.variables.put(paramSolverVariable1, -1.0F);
    return this;
  }
  
  public ArrayRow createRowGreaterThan(SolverVariable paramSolverVariable1, SolverVariable paramSolverVariable2, SolverVariable paramSolverVariable3, int paramInt) {
    byte b = 0;
    int i = 0;
    if (paramInt != 0) {
      b = i;
      i = paramInt;
      if (paramInt < 0) {
        i = paramInt * -1;
        b = 1;
      } 
      this.constantValue = i;
    } 
    if (b == 0) {
      this.variables.put(paramSolverVariable1, -1.0F);
      this.variables.put(paramSolverVariable2, 1.0F);
      this.variables.put(paramSolverVariable3, 1.0F);
      return this;
    } 
    this.variables.put(paramSolverVariable1, 1.0F);
    this.variables.put(paramSolverVariable2, -1.0F);
    this.variables.put(paramSolverVariable3, -1.0F);
    return this;
  }
  
  public ArrayRow createRowLowerThan(SolverVariable paramSolverVariable1, SolverVariable paramSolverVariable2, SolverVariable paramSolverVariable3, int paramInt) {
    byte b = 0;
    int i = 0;
    if (paramInt != 0) {
      b = i;
      i = paramInt;
      if (paramInt < 0) {
        i = paramInt * -1;
        b = 1;
      } 
      this.constantValue = i;
    } 
    if (b == 0) {
      this.variables.put(paramSolverVariable1, -1.0F);
      this.variables.put(paramSolverVariable2, 1.0F);
      this.variables.put(paramSolverVariable3, -1.0F);
      return this;
    } 
    this.variables.put(paramSolverVariable1, 1.0F);
    this.variables.put(paramSolverVariable2, -1.0F);
    this.variables.put(paramSolverVariable3, 1.0F);
    return this;
  }
  
  public ArrayRow createRowWithAngle(SolverVariable paramSolverVariable1, SolverVariable paramSolverVariable2, SolverVariable paramSolverVariable3, SolverVariable paramSolverVariable4, float paramFloat) {
    this.variables.put(paramSolverVariable3, 0.5F);
    this.variables.put(paramSolverVariable4, 0.5F);
    this.variables.put(paramSolverVariable1, -0.5F);
    this.variables.put(paramSolverVariable2, -0.5F);
    this.constantValue = -paramFloat;
    return this;
  }
  
  void ensurePositiveConstant() {
    if (this.constantValue < 0.0F) {
      this.constantValue *= -1.0F;
      this.variables.invert();
    } 
  }
  
  public SolverVariable getKey() { return this.variable; }
  
  public SolverVariable getPivotCandidate(LinearSystem paramLinearSystem, boolean[] paramArrayOfBoolean) { return this.variables.getPivotCandidate(paramArrayOfBoolean, null); }
  
  boolean hasKeyVariable() { return (this.variable != null && (this.variable.mType == SolverVariable.Type.UNRESTRICTED || this.constantValue >= 0.0F)); }
  
  boolean hasVariable(SolverVariable paramSolverVariable) { return this.variables.containsKey(paramSolverVariable); }
  
  public void initFromRow(LinearSystem.Row paramRow) {
    if (paramRow instanceof ArrayRow) {
      paramRow = (ArrayRow)paramRow;
      this.variable = null;
      this.variables.clear();
      for (byte b = 0; b < paramRow.variables.currentSize; b++) {
        SolverVariable solverVariable = paramRow.variables.getVariable(b);
        float f = paramRow.variables.getVariableValue(b);
        this.variables.add(solverVariable, f, true);
      } 
    } 
  }
  
  public boolean isEmpty() { return (this.variable == null && this.constantValue == 0.0F && this.variables.currentSize == 0); }
  
  SolverVariable pickPivot(SolverVariable paramSolverVariable) { return this.variables.getPivotCandidate(null, paramSolverVariable); }
  
  void pivot(SolverVariable paramSolverVariable) {
    if (this.variable != null) {
      this.variables.put(this.variable, -1.0F);
      this.variable = null;
    } 
    float f = this.variables.remove(paramSolverVariable, true) * -1.0F;
    this.variable = paramSolverVariable;
    if (f == 1.0F)
      return; 
    this.constantValue /= f;
    this.variables.divideByAmount(f);
  }
  
  public void reset() {
    this.variable = null;
    this.variables.clear();
    this.constantValue = 0.0F;
    this.isSimpleDefinition = false;
  }
  
  int sizeInBytes() {
    boolean bool;
    if (this.variable != null) {
      bool = true;
    } else {
      bool = false;
    } 
    return bool + 4 + 4 + this.variables.sizeInBytes();
  }
  
  String toReadableString() {
    boolean bool;
    if (this.variable == null) {
      StringBuilder stringBuilder1 = new StringBuilder();
      stringBuilder1.append("");
      stringBuilder1.append("0");
      str1 = stringBuilder1.toString();
    } else {
      StringBuilder stringBuilder1 = new StringBuilder();
      stringBuilder1.append("");
      stringBuilder1.append(this.variable);
      str1 = stringBuilder1.toString();
    } 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(str1);
    stringBuilder.append(" = ");
    String str1 = stringBuilder.toString();
    float f = this.constantValue;
    byte b = 0;
    if (f != 0.0F) {
      stringBuilder = new StringBuilder();
      stringBuilder.append(str1);
      stringBuilder.append(this.constantValue);
      str1 = stringBuilder.toString();
      bool = true;
    } else {
      bool = false;
    } 
    int i = this.variables.currentSize;
    while (b < i) {
      SolverVariable solverVariable = this.variables.getVariable(b);
      if (solverVariable != null) {
        float f1 = this.variables.getVariableValue(b);
        if (f1 != 0.0F) {
          String str3;
          String str4 = solverVariable.toString();
          if (!bool) {
            str3 = str1;
            f = f1;
            if (f1 < 0.0F) {
              StringBuilder stringBuilder1 = new StringBuilder();
              stringBuilder1.append(str1);
              stringBuilder1.append("- ");
              String str = stringBuilder1.toString();
              f = f1 * -1.0F;
            } 
          } else if (f1 > 0.0F) {
            StringBuilder stringBuilder1 = new StringBuilder();
            stringBuilder1.append(str1);
            stringBuilder1.append(" + ");
            str3 = stringBuilder1.toString();
            f = f1;
          } else {
            StringBuilder stringBuilder1 = new StringBuilder();
            stringBuilder1.append(str1);
            stringBuilder1.append(" - ");
            str3 = stringBuilder1.toString();
            f = f1 * -1.0F;
          } 
          if (f == 1.0F) {
            StringBuilder stringBuilder1 = new StringBuilder();
            stringBuilder1.append(str3);
            stringBuilder1.append(str4);
            String str = stringBuilder1.toString();
          } else {
            StringBuilder stringBuilder1 = new StringBuilder();
            stringBuilder1.append(str3);
            stringBuilder1.append(f);
            stringBuilder1.append(" ");
            stringBuilder1.append(str4);
            str1 = stringBuilder1.toString();
          } 
          bool = true;
        } 
      } 
      b++;
    } 
    String str2 = str1;
    if (!bool) {
      StringBuilder stringBuilder1 = new StringBuilder();
      stringBuilder1.append(str1);
      stringBuilder1.append("0.0");
      str2 = stringBuilder1.toString();
    } 
    return str2;
  }
  
  public String toString() { return toReadableString(); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/constraint/solver/ArrayRow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */