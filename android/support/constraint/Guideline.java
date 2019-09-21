package android.support.constraint;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class Guideline extends View {
  public Guideline(Context paramContext) {
    super(paramContext);
    super.setVisibility(8);
  }
  
  public Guideline(Context paramContext, AttributeSet paramAttributeSet) {
    super(paramContext, paramAttributeSet);
    super.setVisibility(8);
  }
  
  public Guideline(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
    super(paramContext, paramAttributeSet, paramInt);
    super.setVisibility(8);
  }
  
  public Guideline(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2) {
    super(paramContext, paramAttributeSet, paramInt1);
    super.setVisibility(8);
  }
  
  public void draw(Canvas paramCanvas) {}
  
  protected void onMeasure(int paramInt1, int paramInt2) { setMeasuredDimension(0, 0); }
  
  public void setGuidelineBegin(int paramInt) {
    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)getLayoutParams();
    layoutParams.guideBegin = paramInt;
    setLayoutParams(layoutParams);
  }
  
  public void setGuidelineEnd(int paramInt) {
    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)getLayoutParams();
    layoutParams.guideEnd = paramInt;
    setLayoutParams(layoutParams);
  }
  
  public void setGuidelinePercent(float paramFloat) {
    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)getLayoutParams();
    layoutParams.guidePercent = paramFloat;
    setLayoutParams(layoutParams);
  }
  
  public void setVisibility(int paramInt) {}
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/constraint/Guideline.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */