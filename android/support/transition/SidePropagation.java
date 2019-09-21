package android.support.transition;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

public class SidePropagation extends VisibilityPropagation {
  private float mPropagationSpeed = 3.0F;
  
  private int mSide = 80;
  
  private int distance(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8) { // Byte code:
    //   0: aload_0
    //   1: getfield mSide : I
    //   4: istore #12
    //   6: iconst_1
    //   7: istore #11
    //   9: iconst_1
    //   10: istore #10
    //   12: iload #12
    //   14: ldc 8388611
    //   16: if_icmpne -> 50
    //   19: aload_1
    //   20: invokestatic getLayoutDirection : (Landroid/view/View;)I
    //   23: iconst_1
    //   24: if_icmpne -> 30
    //   27: goto -> 33
    //   30: iconst_0
    //   31: istore #10
    //   33: iload #10
    //   35: ifeq -> 44
    //   38: iconst_5
    //   39: istore #10
    //   41: goto -> 91
    //   44: iconst_3
    //   45: istore #10
    //   47: goto -> 91
    //   50: aload_0
    //   51: getfield mSide : I
    //   54: ldc 8388613
    //   56: if_icmpne -> 85
    //   59: aload_1
    //   60: invokestatic getLayoutDirection : (Landroid/view/View;)I
    //   63: iconst_1
    //   64: if_icmpne -> 74
    //   67: iload #11
    //   69: istore #10
    //   71: goto -> 77
    //   74: iconst_0
    //   75: istore #10
    //   77: iload #10
    //   79: ifeq -> 38
    //   82: goto -> 44
    //   85: aload_0
    //   86: getfield mSide : I
    //   89: istore #10
    //   91: iload #10
    //   93: iconst_3
    //   94: if_icmpeq -> 158
    //   97: iload #10
    //   99: iconst_5
    //   100: if_icmpeq -> 145
    //   103: iload #10
    //   105: bipush #48
    //   107: if_icmpeq -> 132
    //   110: iload #10
    //   112: bipush #80
    //   114: if_icmpeq -> 119
    //   117: iconst_0
    //   118: ireturn
    //   119: iload_3
    //   120: iload #7
    //   122: isub
    //   123: iload #4
    //   125: iload_2
    //   126: isub
    //   127: invokestatic abs : (I)I
    //   130: iadd
    //   131: ireturn
    //   132: iload #9
    //   134: iload_3
    //   135: isub
    //   136: iload #4
    //   138: iload_2
    //   139: isub
    //   140: invokestatic abs : (I)I
    //   143: iadd
    //   144: ireturn
    //   145: iload_2
    //   146: iload #6
    //   148: isub
    //   149: iload #5
    //   151: iload_3
    //   152: isub
    //   153: invokestatic abs : (I)I
    //   156: iadd
    //   157: ireturn
    //   158: iload #8
    //   160: iload_2
    //   161: isub
    //   162: iload #5
    //   164: iload_3
    //   165: isub
    //   166: invokestatic abs : (I)I
    //   169: iadd
    //   170: ireturn }
  
  private int getMaxDistance(ViewGroup paramViewGroup) {
    int i = this.mSide;
    return (i != 3 && i != 5 && i != 8388611 && i != 8388613) ? paramViewGroup.getHeight() : paramViewGroup.getWidth();
  }
  
  public long getStartDelay(ViewGroup paramViewGroup, Transition paramTransition, TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2) {
    int j;
    int i;
    boolean bool;
    if (paramTransitionValues1 == null && paramTransitionValues2 == null)
      return 0L; 
    Rect rect = paramTransition.getEpicenter();
    if (paramTransitionValues2 == null || getViewVisibility(paramTransitionValues1) == 0) {
      bool = true;
    } else {
      bool = true;
      paramTransitionValues1 = paramTransitionValues2;
    } 
    int k = getViewX(paramTransitionValues1);
    int m = getViewY(paramTransitionValues1);
    int[] arrayOfInt = new int[2];
    paramViewGroup.getLocationOnScreen(arrayOfInt);
    int n = arrayOfInt[0] + Math.round(paramViewGroup.getTranslationX());
    int i1 = arrayOfInt[1] + Math.round(paramViewGroup.getTranslationY());
    int i2 = n + paramViewGroup.getWidth();
    int i3 = i1 + paramViewGroup.getHeight();
    if (rect != null) {
      i = rect.centerX();
      j = rect.centerY();
    } else {
      i = (n + i2) / 2;
      j = (i1 + i3) / 2;
    } 
    float f = distance(paramViewGroup, k, m, i, j, n, i1, i2, i3) / getMaxDistance(paramViewGroup);
    long l2 = paramTransition.getDuration();
    long l1 = l2;
    if (l2 < 0L)
      l1 = 300L; 
    return Math.round((float)(l1 * bool) / this.mPropagationSpeed * f);
  }
  
  public void setPropagationSpeed(float paramFloat) {
    if (paramFloat == 0.0F)
      throw new IllegalArgumentException("propagationSpeed may not be 0"); 
    this.mPropagationSpeed = paramFloat;
  }
  
  public void setSide(int paramInt) { this.mSide = paramInt; }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/transition/SidePropagation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */