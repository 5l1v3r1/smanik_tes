package android.support.constraint.solver;

public class Cache {
  Pools.Pool<ArrayRow> arrayRowPool = new Pools.SimplePool(256);
  
  SolverVariable[] mIndexedVariables = new SolverVariable[32];
  
  Pools.Pool<SolverVariable> solverVariablePool = new Pools.SimplePool(256);
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/constraint/solver/Cache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */