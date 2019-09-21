package android.support.constraint.solver;

public class GoalRow extends ArrayRow {
  public GoalRow(Cache paramCache) { super(paramCache); }
  
  public void addError(SolverVariable paramSolverVariable) {
    super.addError(paramSolverVariable);
    paramSolverVariable.usageInRowCount--;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/constraint/solver/GoalRow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */