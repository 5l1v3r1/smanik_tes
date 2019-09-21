package android.support.transition;

import android.animation.TypeEvaluator;

class FloatArrayEvaluator extends Object implements TypeEvaluator<float[]> {
  private float[] mArray;
  
  FloatArrayEvaluator(float[] paramArrayOfFloat) { this.mArray = paramArrayOfFloat; }
  
  public float[] evaluate(float paramFloat, float[] paramArrayOfFloat1, float[] paramArrayOfFloat2) {
    float[] arrayOfFloat2 = this.mArray;
    float[] arrayOfFloat1 = arrayOfFloat2;
    if (arrayOfFloat2 == null)
      arrayOfFloat1 = new float[paramArrayOfFloat1.length]; 
    byte b;
    for (b = 0; b < arrayOfFloat1.length; b++) {
      float f = paramArrayOfFloat1[b];
      arrayOfFloat1[b] = f + (paramArrayOfFloat2[b] - f) * paramFloat;
    } 
    return arrayOfFloat1;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/transition/FloatArrayEvaluator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */