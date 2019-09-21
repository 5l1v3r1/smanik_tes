package android.support.constraint;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.constraint.solver.widgets.Barrier;
import android.util.AttributeSet;

public class Barrier extends ConstraintHelper {
  public static final int BOTTOM = 3;
  
  public static final int END = 6;
  
  public static final int LEFT = 0;
  
  public static final int RIGHT = 1;
  
  public static final int START = 5;
  
  public static final int TOP = 2;
  
  private Barrier mBarrier;
  
  private int mIndicatedType = 0;
  
  private int mResolvedType = 0;
  
  public Barrier(Context paramContext) {
    super(paramContext);
    setVisibility(8);
  }
  
  public Barrier(Context paramContext, AttributeSet paramAttributeSet) {
    super(paramContext, paramAttributeSet);
    setVisibility(8);
  }
  
  public Barrier(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
    super(paramContext, paramAttributeSet, paramInt);
    setVisibility(8);
  }
  
  public int getType() { return this.mIndicatedType; }
  
  protected void init(AttributeSet paramAttributeSet) {
    super.init(paramAttributeSet);
    this.mBarrier = new Barrier();
    if (paramAttributeSet != null) {
      TypedArray typedArray = getContext().obtainStyledAttributes(paramAttributeSet, R.styleable.ConstraintLayout_Layout);
      int i = typedArray.getIndexCount();
      for (byte b = 0; b < i; b++) {
        int j = typedArray.getIndex(b);
        if (j == R.styleable.ConstraintLayout_Layout_barrierDirection) {
          setType(typedArray.getInt(j, 0));
        } else if (j == R.styleable.ConstraintLayout_Layout_barrierAllowsGoneWidgets) {
          this.mBarrier.setAllowsGoneWidget(typedArray.getBoolean(j, true));
        } 
      } 
    } 
    this.mHelperWidget = this.mBarrier;
    validateParams();
  }
  
  public void setType(int paramInt) {
    this.mIndicatedType = paramInt;
    this.mResolvedType = paramInt;
    if (Build.VERSION.SDK_INT < 17) {
      if (this.mIndicatedType == 5) {
        this.mResolvedType = 0;
      } else if (this.mIndicatedType == 6) {
        this.mResolvedType = 1;
      } 
    } else {
      if (1 == getResources().getConfiguration().getLayoutDirection()) {
        paramInt = 1;
      } else {
        paramInt = 0;
      } 
      if (paramInt != 0) {
        if (this.mIndicatedType == 5) {
          this.mResolvedType = 1;
        } else if (this.mIndicatedType == 6) {
          this.mResolvedType = 0;
        } 
      } else if (this.mIndicatedType == 5) {
        this.mResolvedType = 0;
      } else if (this.mIndicatedType == 6) {
        this.mResolvedType = 1;
      } 
    } 
    this.mBarrier.setBarrierType(this.mResolvedType);
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/constraint/Barrier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */