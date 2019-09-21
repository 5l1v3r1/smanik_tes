package android.support.constraint.solver.widgets;

import android.support.constraint.solver.Cache;
import java.util.ArrayList;

public class WidgetContainer extends ConstraintWidget {
  protected ArrayList<ConstraintWidget> mChildren = new ArrayList();
  
  public WidgetContainer() {}
  
  public WidgetContainer(int paramInt1, int paramInt2) { super(paramInt1, paramInt2); }
  
  public WidgetContainer(int paramInt1, int paramInt2, int paramInt3, int paramInt4) { super(paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public static Rectangle getBounds(ArrayList<ConstraintWidget> paramArrayList) {
    Rectangle rectangle = new Rectangle();
    if (paramArrayList.size() == 0)
      return rectangle; 
    int n = paramArrayList.size();
    int m = Integer.MAX_VALUE;
    byte b = 0;
    int k = Integer.MAX_VALUE;
    int j = 0;
    int i;
    for (i = 0; b < n; i = i4) {
      ConstraintWidget constraintWidget = (ConstraintWidget)paramArrayList.get(b);
      int i1 = m;
      if (constraintWidget.getX() < m)
        i1 = constraintWidget.getX(); 
      int i2 = k;
      if (constraintWidget.getY() < k)
        i2 = constraintWidget.getY(); 
      int i3 = j;
      if (constraintWidget.getRight() > j)
        i3 = constraintWidget.getRight(); 
      int i4 = i;
      if (constraintWidget.getBottom() > i)
        i4 = constraintWidget.getBottom(); 
      b++;
      m = i1;
      k = i2;
      j = i3;
    } 
    rectangle.setBounds(m, k, j - m, i - k);
    return rectangle;
  }
  
  public void add(ConstraintWidget paramConstraintWidget) {
    this.mChildren.add(paramConstraintWidget);
    if (paramConstraintWidget.getParent() != null)
      ((WidgetContainer)paramConstraintWidget.getParent()).remove(paramConstraintWidget); 
    paramConstraintWidget.setParent(this);
  }
  
  public ConstraintWidget findWidget(float paramFloat1, float paramFloat2) { // Byte code:
    //   0: aload_0
    //   1: invokevirtual getDrawX : ()I
    //   4: istore_3
    //   5: aload_0
    //   6: invokevirtual getDrawY : ()I
    //   9: istore #4
    //   11: aload_0
    //   12: invokevirtual getWidth : ()I
    //   15: istore #5
    //   17: aload_0
    //   18: invokevirtual getHeight : ()I
    //   21: istore #6
    //   23: fload_1
    //   24: iload_3
    //   25: i2f
    //   26: fcmpl
    //   27: iflt -> 65
    //   30: fload_1
    //   31: iload #5
    //   33: iload_3
    //   34: iadd
    //   35: i2f
    //   36: fcmpg
    //   37: ifgt -> 65
    //   40: fload_2
    //   41: iload #4
    //   43: i2f
    //   44: fcmpl
    //   45: iflt -> 65
    //   48: fload_2
    //   49: iload #6
    //   51: iload #4
    //   53: iadd
    //   54: i2f
    //   55: fcmpg
    //   56: ifgt -> 65
    //   59: aload_0
    //   60: astore #9
    //   62: goto -> 68
    //   65: aconst_null
    //   66: astore #9
    //   68: iconst_0
    //   69: istore_3
    //   70: aload_0
    //   71: getfield mChildren : Ljava/util/ArrayList;
    //   74: invokevirtual size : ()I
    //   77: istore #4
    //   79: iload_3
    //   80: iload #4
    //   82: if_icmpge -> 235
    //   85: aload_0
    //   86: getfield mChildren : Ljava/util/ArrayList;
    //   89: iload_3
    //   90: invokevirtual get : (I)Ljava/lang/Object;
    //   93: checkcast android/support/constraint/solver/widgets/ConstraintWidget
    //   96: astore #11
    //   98: aload #11
    //   100: instanceof android/support/constraint/solver/widgets/WidgetContainer
    //   103: ifeq -> 134
    //   106: aload #11
    //   108: checkcast android/support/constraint/solver/widgets/WidgetContainer
    //   111: fload_1
    //   112: fload_2
    //   113: invokevirtual findWidget : (FF)Landroid/support/constraint/solver/widgets/ConstraintWidget;
    //   116: astore #11
    //   118: aload #9
    //   120: astore #10
    //   122: aload #11
    //   124: ifnull -> 224
    //   127: aload #11
    //   129: astore #9
    //   131: goto -> 220
    //   134: aload #11
    //   136: invokevirtual getDrawX : ()I
    //   139: istore #5
    //   141: aload #11
    //   143: invokevirtual getDrawY : ()I
    //   146: istore #6
    //   148: aload #11
    //   150: invokevirtual getWidth : ()I
    //   153: istore #7
    //   155: aload #11
    //   157: invokevirtual getHeight : ()I
    //   160: istore #8
    //   162: aload #9
    //   164: astore #10
    //   166: fload_1
    //   167: iload #5
    //   169: i2f
    //   170: fcmpl
    //   171: iflt -> 224
    //   174: aload #9
    //   176: astore #10
    //   178: fload_1
    //   179: iload #7
    //   181: iload #5
    //   183: iadd
    //   184: i2f
    //   185: fcmpg
    //   186: ifgt -> 224
    //   189: aload #9
    //   191: astore #10
    //   193: fload_2
    //   194: iload #6
    //   196: i2f
    //   197: fcmpl
    //   198: iflt -> 224
    //   201: aload #9
    //   203: astore #10
    //   205: fload_2
    //   206: iload #8
    //   208: iload #6
    //   210: iadd
    //   211: i2f
    //   212: fcmpg
    //   213: ifgt -> 224
    //   216: aload #11
    //   218: astore #9
    //   220: aload #9
    //   222: astore #10
    //   224: iload_3
    //   225: iconst_1
    //   226: iadd
    //   227: istore_3
    //   228: aload #10
    //   230: astore #9
    //   232: goto -> 79
    //   235: aload #9
    //   237: areturn }
  
  public ArrayList<ConstraintWidget> findWidgets(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    ArrayList arrayList = new ArrayList();
    Rectangle rectangle = new Rectangle();
    rectangle.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
    paramInt2 = this.mChildren.size();
    for (paramInt1 = 0; paramInt1 < paramInt2; paramInt1++) {
      ConstraintWidget constraintWidget = (ConstraintWidget)this.mChildren.get(paramInt1);
      Rectangle rectangle1 = new Rectangle();
      rectangle1.setBounds(constraintWidget.getDrawX(), constraintWidget.getDrawY(), constraintWidget.getWidth(), constraintWidget.getHeight());
      if (rectangle.intersects(rectangle1))
        arrayList.add(constraintWidget); 
    } 
    return arrayList;
  }
  
  public ArrayList<ConstraintWidget> getChildren() { return this.mChildren; }
  
  public ConstraintWidgetContainer getRootConstraintContainer() {
    ConstraintWidgetContainer constraintWidgetContainer;
    ConstraintWidget constraintWidget = getParent();
    if (this instanceof ConstraintWidgetContainer) {
      constraintWidgetContainer = (ConstraintWidgetContainer)this;
    } else {
      constraintWidgetContainer = null;
    } 
    while (constraintWidget != null) {
      ConstraintWidget constraintWidget1 = constraintWidget.getParent();
      if (constraintWidget instanceof ConstraintWidgetContainer)
        constraintWidgetContainer = (ConstraintWidgetContainer)constraintWidget; 
      constraintWidget = constraintWidget1;
    } 
    return constraintWidgetContainer;
  }
  
  public void layout() {
    updateDrawPosition();
    if (this.mChildren == null)
      return; 
    int i = this.mChildren.size();
    for (byte b = 0; b < i; b++) {
      ConstraintWidget constraintWidget = (ConstraintWidget)this.mChildren.get(b);
      if (constraintWidget instanceof WidgetContainer)
        ((WidgetContainer)constraintWidget).layout(); 
    } 
  }
  
  public void remove(ConstraintWidget paramConstraintWidget) {
    this.mChildren.remove(paramConstraintWidget);
    paramConstraintWidget.setParent(null);
  }
  
  public void removeAllChildren() { this.mChildren.clear(); }
  
  public void reset() {
    this.mChildren.clear();
    super.reset();
  }
  
  public void resetSolverVariables(Cache paramCache) {
    super.resetSolverVariables(paramCache);
    int i = this.mChildren.size();
    for (byte b = 0; b < i; b++)
      ((ConstraintWidget)this.mChildren.get(b)).resetSolverVariables(paramCache); 
  }
  
  public void setOffset(int paramInt1, int paramInt2) {
    super.setOffset(paramInt1, paramInt2);
    paramInt2 = this.mChildren.size();
    for (paramInt1 = 0; paramInt1 < paramInt2; paramInt1++)
      ((ConstraintWidget)this.mChildren.get(paramInt1)).setOffset(getRootX(), getRootY()); 
  }
  
  public void updateDrawPosition() {
    super.updateDrawPosition();
    if (this.mChildren == null)
      return; 
    int i = this.mChildren.size();
    for (byte b = 0; b < i; b++) {
      ConstraintWidget constraintWidget = (ConstraintWidget)this.mChildren.get(b);
      constraintWidget.setOffset(getDrawX(), getDrawY());
      if (!(constraintWidget instanceof ConstraintWidgetContainer))
        constraintWidget.updateDrawPosition(); 
    } 
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/constraint/solver/widgets/WidgetContainer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */