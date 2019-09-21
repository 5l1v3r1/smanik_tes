package android.support.constraint.solver.widgets;

import android.support.constraint.solver.Cache;
import android.support.constraint.solver.SolverVariable;
import java.util.ArrayList;
import java.util.HashSet;

public class ConstraintAnchor {
  private static final boolean ALLOW_BINARY = false;
  
  public static final int AUTO_CONSTRAINT_CREATOR = 2;
  
  public static final int SCOUT_CREATOR = 1;
  
  private static final int UNSET_GONE_MARGIN = -1;
  
  public static final int USER_CREATOR = 0;
  
  private int mConnectionCreator = 0;
  
  private ConnectionType mConnectionType = ConnectionType.RELAXED;
  
  int mGoneMargin = -1;
  
  public int mMargin = 0;
  
  final ConstraintWidget mOwner;
  
  private ResolutionAnchor mResolutionAnchor = new ResolutionAnchor(this);
  
  SolverVariable mSolverVariable;
  
  private Strength mStrength = Strength.NONE;
  
  ConstraintAnchor mTarget;
  
  final Type mType;
  
  public ConstraintAnchor(ConstraintWidget paramConstraintWidget, Type paramType) {
    this.mOwner = paramConstraintWidget;
    this.mType = paramType;
  }
  
  private boolean isConnectionToMe(ConstraintWidget paramConstraintWidget, HashSet<ConstraintWidget> paramHashSet) {
    if (paramHashSet.contains(paramConstraintWidget))
      return false; 
    paramHashSet.add(paramConstraintWidget);
    if (paramConstraintWidget == getOwner())
      return true; 
    ArrayList arrayList = paramConstraintWidget.getAnchors();
    int i = arrayList.size();
    for (byte b = 0; b < i; b++) {
      ConstraintAnchor constraintAnchor = (ConstraintAnchor)arrayList.get(b);
      if (constraintAnchor.isSimilarDimensionConnection(this) && constraintAnchor.isConnected() && isConnectionToMe(constraintAnchor.getTarget().getOwner(), paramHashSet))
        return true; 
    } 
    return false;
  }
  
  public boolean connect(ConstraintAnchor paramConstraintAnchor, int paramInt) { return connect(paramConstraintAnchor, paramInt, -1, Strength.STRONG, 0, false); }
  
  public boolean connect(ConstraintAnchor paramConstraintAnchor, int paramInt1, int paramInt2) { return connect(paramConstraintAnchor, paramInt1, -1, Strength.STRONG, paramInt2, false); }
  
  public boolean connect(ConstraintAnchor paramConstraintAnchor, int paramInt1, int paramInt2, Strength paramStrength, int paramInt3, boolean paramBoolean) {
    if (paramConstraintAnchor == null) {
      this.mTarget = null;
      this.mMargin = 0;
      this.mGoneMargin = -1;
      this.mStrength = Strength.NONE;
      this.mConnectionCreator = 2;
      return true;
    } 
    if (!paramBoolean && !isValidConnection(paramConstraintAnchor))
      return false; 
    this.mTarget = paramConstraintAnchor;
    if (paramInt1 > 0) {
      this.mMargin = paramInt1;
    } else {
      this.mMargin = 0;
    } 
    this.mGoneMargin = paramInt2;
    this.mStrength = paramStrength;
    this.mConnectionCreator = paramInt3;
    return true;
  }
  
  public boolean connect(ConstraintAnchor paramConstraintAnchor, int paramInt1, Strength paramStrength, int paramInt2) { return connect(paramConstraintAnchor, paramInt1, -1, paramStrength, paramInt2, false); }
  
  public int getConnectionCreator() { return this.mConnectionCreator; }
  
  public ConnectionType getConnectionType() { return this.mConnectionType; }
  
  public int getMargin() { return (this.mOwner.getVisibility() == 8) ? 0 : ((this.mGoneMargin > -1 && this.mTarget != null && this.mTarget.mOwner.getVisibility() == 8) ? this.mGoneMargin : this.mMargin); }
  
  public final ConstraintAnchor getOpposite() {
    switch (this.mType) {
      default:
        throw new AssertionError(this.mType.name());
      case BOTTOM:
        return this.mOwner.mTop;
      case TOP:
        return this.mOwner.mBottom;
      case RIGHT:
        return this.mOwner.mLeft;
      case LEFT:
        return this.mOwner.mRight;
      case CENTER:
      case BASELINE:
      case CENTER_X:
      case CENTER_Y:
      case null:
        break;
    } 
    return null;
  }
  
  public ConstraintWidget getOwner() { return this.mOwner; }
  
  public int getPriorityLevel() {
    switch (this.mType) {
      default:
        throw new AssertionError(this.mType.name());
      case null:
        return 0;
      case CENTER_Y:
        return 0;
      case CENTER_X:
        return 0;
      case BASELINE:
        return 1;
      case BOTTOM:
        return 2;
      case TOP:
        return 2;
      case RIGHT:
        return 2;
      case LEFT:
        return 2;
      case CENTER:
        break;
    } 
    return 2;
  }
  
  public ResolutionAnchor getResolutionNode() { return this.mResolutionAnchor; }
  
  public int getSnapPriorityLevel() {
    switch (this.mType) {
      default:
        throw new AssertionError(this.mType.name());
      case null:
        return 0;
      case CENTER_Y:
        return 1;
      case CENTER_X:
        return 0;
      case BASELINE:
        return 2;
      case BOTTOM:
        return 0;
      case TOP:
        return 0;
      case RIGHT:
        return 1;
      case LEFT:
        return 1;
      case CENTER:
        break;
    } 
    return 3;
  }
  
  public SolverVariable getSolverVariable() { return this.mSolverVariable; }
  
  public Strength getStrength() { return this.mStrength; }
  
  public ConstraintAnchor getTarget() { return this.mTarget; }
  
  public Type getType() { return this.mType; }
  
  public boolean isConnected() { return (this.mTarget != null); }
  
  public boolean isConnectionAllowed(ConstraintWidget paramConstraintWidget) {
    if (isConnectionToMe(paramConstraintWidget, new HashSet()))
      return false; 
    ConstraintWidget constraintWidget = getOwner().getParent();
    return (constraintWidget == paramConstraintWidget) ? true : ((paramConstraintWidget.getParent() == constraintWidget));
  }
  
  public boolean isConnectionAllowed(ConstraintWidget paramConstraintWidget, ConstraintAnchor paramConstraintAnchor) { return isConnectionAllowed(paramConstraintWidget); }
  
  public boolean isSideAnchor() {
    switch (this.mType) {
      default:
        throw new AssertionError(this.mType.name());
      case LEFT:
      case RIGHT:
      case TOP:
      case BOTTOM:
        return true;
      case CENTER:
      case BASELINE:
      case CENTER_X:
      case CENTER_Y:
      case null:
        break;
    } 
    return false;
  }
  
  public boolean isSimilarDimensionConnection(ConstraintAnchor paramConstraintAnchor) {
    int i;
    int j;
    Type type1 = paramConstraintAnchor.getType();
    Type type2 = this.mType;
    byte b2 = 1;
    byte b1 = 1;
    if (type1 == type2)
      return true; 
    switch (this.mType) {
      default:
        throw new AssertionError(this.mType.name());
      case null:
        return false;
      case TOP:
      case BOTTOM:
      case BASELINE:
      case CENTER_Y:
        j = b1;
        if (type1 != Type.TOP) {
          j = b1;
          if (type1 != Type.BOTTOM) {
            j = b1;
            if (type1 != Type.CENTER_Y) {
              if (type1 == Type.BASELINE)
                return true; 
              j = 0;
            } 
          } 
        } 
        return j;
      case LEFT:
      case RIGHT:
      case CENTER_X:
        i = b2;
        if (type1 != Type.LEFT) {
          i = b2;
          if (type1 != Type.RIGHT) {
            if (type1 == Type.CENTER_X)
              return true; 
            i = 0;
          } 
        } 
        return i;
      case CENTER:
        break;
    } 
    return (type1 != Type.BASELINE);
  }
  
  public boolean isSnapCompatibleWith(ConstraintAnchor paramConstraintAnchor) {
    int i;
    if (this.mType == Type.CENTER)
      return false; 
    if (this.mType == paramConstraintAnchor.getType())
      return true; 
    switch (this.mType) {
      default:
        throw new AssertionError(this.mType.name());
      case CENTER_Y:
        switch (paramConstraintAnchor.getType()) {
          default:
            return false;
          case BOTTOM:
            return true;
          case TOP:
            break;
        } 
        return true;
      case CENTER_X:
        switch (paramConstraintAnchor.getType()) {
          default:
            return false;
          case RIGHT:
            return true;
          case LEFT:
            break;
        } 
        return true;
      case BOTTOM:
        i = null.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[paramConstraintAnchor.getType().ordinal()];
        return (i != 4) ? (!(i != 8)) : true;
      case TOP:
        i = null.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[paramConstraintAnchor.getType().ordinal()];
        return (i != 5) ? (!(i != 8)) : true;
      case RIGHT:
        i = null.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[paramConstraintAnchor.getType().ordinal()];
        return (i != 2) ? (!(i != 7)) : true;
      case LEFT:
        i = null.$SwitchMap$android$support$constraint$solver$widgets$ConstraintAnchor$Type[paramConstraintAnchor.getType().ordinal()];
        return (i != 3) ? (!(i != 7)) : true;
      case CENTER:
      case BASELINE:
      case null:
        break;
    } 
    return false;
  }
  
  public boolean isValidConnection(ConstraintAnchor paramConstraintAnchor) {
    int i;
    byte b;
    int j = 0;
    if (paramConstraintAnchor == null)
      return false; 
    Type type = paramConstraintAnchor.getType();
    if (type == this.mType)
      return !(this.mType == Type.BASELINE && (!paramConstraintAnchor.getOwner().hasBaseline() || !getOwner().hasBaseline())); 
    switch (this.mType) {
      default:
        throw new AssertionError(this.mType.name());
      case BASELINE:
      case CENTER_X:
      case CENTER_Y:
      case null:
        return false;
      case TOP:
      case BOTTOM:
        if (type == Type.TOP || type == Type.BOTTOM) {
          b = 1;
        } else {
          b = 0;
        } 
        j = b;
        return (paramConstraintAnchor.getOwner() instanceof Guideline) ? ((b != 0 || type == Type.CENTER_Y)) : j;
      case LEFT:
      case RIGHT:
        if (type == Type.LEFT || type == Type.RIGHT) {
          b = 1;
        } else {
          b = 0;
        } 
        i = b;
        return (paramConstraintAnchor.getOwner() instanceof Guideline) ? ((b != 0 || type == Type.CENTER_X)) : i;
      case CENTER:
        break;
    } 
    boolean bool = i;
    if (type != Type.BASELINE) {
      int k = i;
      if (type != Type.CENTER_X) {
        int m = i;
        if (type != Type.CENTER_Y)
          bool = true; 
      } 
    } 
    return bool;
  }
  
  public boolean isVerticalAnchor() {
    switch (this.mType) {
      default:
        throw new AssertionError(this.mType.name());
      case TOP:
      case BOTTOM:
      case BASELINE:
      case CENTER_Y:
      case null:
        return true;
      case CENTER:
      case LEFT:
      case RIGHT:
      case CENTER_X:
        break;
    } 
    return false;
  }
  
  public void reset() {
    this.mTarget = null;
    this.mMargin = 0;
    this.mGoneMargin = -1;
    this.mStrength = Strength.STRONG;
    this.mConnectionCreator = 0;
    this.mConnectionType = ConnectionType.RELAXED;
    this.mResolutionAnchor.reset();
  }
  
  public void resetSolverVariable(Cache paramCache) {
    if (this.mSolverVariable == null) {
      this.mSolverVariable = new SolverVariable(SolverVariable.Type.UNRESTRICTED, null);
      return;
    } 
    this.mSolverVariable.reset();
  }
  
  public void setConnectionCreator(int paramInt) { this.mConnectionCreator = paramInt; }
  
  public void setConnectionType(ConnectionType paramConnectionType) { this.mConnectionType = paramConnectionType; }
  
  public void setGoneMargin(int paramInt) {
    if (isConnected())
      this.mGoneMargin = paramInt; 
  }
  
  public void setMargin(int paramInt) {
    if (isConnected())
      this.mMargin = paramInt; 
  }
  
  public void setStrength(Strength paramStrength) {
    if (isConnected())
      this.mStrength = paramStrength; 
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(this.mOwner.getDebugName());
    stringBuilder.append(":");
    stringBuilder.append(this.mType.toString());
    return stringBuilder.toString();
  }
  
  public enum ConnectionType {
    RELAXED, STRICT;
    
    static  {
      $VALUES = new ConnectionType[] { RELAXED, STRICT };
    }
  }
  
  public enum Strength {
    NONE, STRONG, WEAK;
    
    static  {
      $VALUES = new Strength[] { NONE, STRONG, WEAK };
    }
  }
  
  public enum Type {
    NONE, RIGHT, TOP, BASELINE, BOTTOM, CENTER, CENTER_X, CENTER_Y, LEFT;
    
    static  {
      LEFT = new Type("LEFT", 1);
      TOP = new Type("TOP", 2);
      RIGHT = new Type("RIGHT", 3);
      BOTTOM = new Type("BOTTOM", 4);
      BASELINE = new Type("BASELINE", 5);
      CENTER = new Type("CENTER", 6);
      CENTER_X = new Type("CENTER_X", 7);
      CENTER_Y = new Type("CENTER_Y", 8);
      $VALUES = new Type[] { NONE, LEFT, TOP, RIGHT, BOTTOM, BASELINE, CENTER, CENTER_X, CENTER_Y };
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/constraint/solver/widgets/ConstraintAnchor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */