package android.support.constraint.solver.widgets;

import java.util.HashSet;
import java.util.Iterator;

public class ResolutionNode {
  public static final int REMOVED = 2;
  
  public static final int RESOLVED = 1;
  
  public static final int UNRESOLVED = 0;
  
  HashSet<ResolutionNode> dependents = new HashSet(2);
  
  int state = 0;
  
  public void addDependent(ResolutionNode paramResolutionNode) { this.dependents.add(paramResolutionNode); }
  
  public void didResolve() {
    this.state = 1;
    Iterator iterator = this.dependents.iterator();
    while (iterator.hasNext())
      ((ResolutionNode)iterator.next()).resolve(); 
  }
  
  public void invalidate() {
    this.state = 0;
    Iterator iterator = this.dependents.iterator();
    while (iterator.hasNext())
      ((ResolutionNode)iterator.next()).invalidate(); 
  }
  
  public void invalidateAnchors() {
    if (this instanceof ResolutionAnchor)
      this.state = 0; 
    Iterator iterator = this.dependents.iterator();
    while (iterator.hasNext())
      ((ResolutionNode)iterator.next()).invalidateAnchors(); 
  }
  
  public boolean isResolved() { return (this.state == 1); }
  
  public void remove(ResolutionDimension paramResolutionDimension) {}
  
  public void reset() {
    this.state = 0;
    this.dependents.clear();
  }
  
  public void resolve() {}
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/constraint/solver/widgets/ResolutionNode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */