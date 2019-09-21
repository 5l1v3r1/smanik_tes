package android.support.design.widget;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.ColorUtils;

class CircularBorderDrawable extends Drawable {
  private static final float DRAW_STROKE_WIDTH_MULTIPLE = 1.3333F;
  
  private ColorStateList mBorderTint;
  
  float mBorderWidth;
  
  private int mBottomInnerStrokeColor;
  
  private int mBottomOuterStrokeColor;
  
  private int mCurrentBorderTintColor;
  
  private boolean mInvalidateShader = true;
  
  final Paint mPaint = new Paint(1);
  
  final Rect mRect = new Rect();
  
  final RectF mRectF = new RectF();
  
  private float mRotation;
  
  private int mTopInnerStrokeColor;
  
  private int mTopOuterStrokeColor;
  
  public CircularBorderDrawable() { this.mPaint.setStyle(Paint.Style.STROKE); }
  
  private Shader createGradientShader() {
    Rect rect = this.mRect;
    copyBounds(rect);
    float f1 = this.mBorderWidth / rect.height();
    int i = ColorUtils.compositeColors(this.mTopOuterStrokeColor, this.mCurrentBorderTintColor);
    int j = ColorUtils.compositeColors(this.mTopInnerStrokeColor, this.mCurrentBorderTintColor);
    int k = ColorUtils.compositeColors(ColorUtils.setAlphaComponent(this.mTopInnerStrokeColor, 0), this.mCurrentBorderTintColor);
    int m = ColorUtils.compositeColors(ColorUtils.setAlphaComponent(this.mBottomInnerStrokeColor, 0), this.mCurrentBorderTintColor);
    int n = ColorUtils.compositeColors(this.mBottomInnerStrokeColor, this.mCurrentBorderTintColor);
    int i1 = ColorUtils.compositeColors(this.mBottomOuterStrokeColor, this.mCurrentBorderTintColor);
    float f2 = rect.top;
    float f3 = rect.bottom;
    Shader.TileMode tileMode = Shader.TileMode.CLAMP;
    return new LinearGradient(0.0F, f2, 0.0F, f3, new int[] { i, j, k, m, n, i1 }, new float[] { 0.0F, f1, 0.5F, 0.5F, 1.0F - f1, 1.0F }, tileMode);
  }
  
  public void draw(Canvas paramCanvas) {
    if (this.mInvalidateShader) {
      this.mPaint.setShader(createGradientShader());
      this.mInvalidateShader = false;
    } 
    float f = this.mPaint.getStrokeWidth() / 2.0F;
    RectF rectF = this.mRectF;
    copyBounds(this.mRect);
    rectF.set(this.mRect);
    rectF.left += f;
    rectF.top += f;
    rectF.right -= f;
    rectF.bottom -= f;
    paramCanvas.save();
    paramCanvas.rotate(this.mRotation, rectF.centerX(), rectF.centerY());
    paramCanvas.drawOval(rectF, this.mPaint);
    paramCanvas.restore();
  }
  
  public int getOpacity() { return (this.mBorderWidth > 0.0F) ? -3 : -2; }
  
  public boolean getPadding(Rect paramRect) {
    int i = Math.round(this.mBorderWidth);
    paramRect.set(i, i, i, i);
    return true;
  }
  
  public boolean isStateful() { return ((this.mBorderTint != null && this.mBorderTint.isStateful()) || super.isStateful()); }
  
  protected void onBoundsChange(Rect paramRect) { this.mInvalidateShader = true; }
  
  protected boolean onStateChange(int[] paramArrayOfInt) {
    if (this.mBorderTint != null) {
      int i = this.mBorderTint.getColorForState(paramArrayOfInt, this.mCurrentBorderTintColor);
      if (i != this.mCurrentBorderTintColor) {
        this.mInvalidateShader = true;
        this.mCurrentBorderTintColor = i;
      } 
    } 
    if (this.mInvalidateShader)
      invalidateSelf(); 
    return this.mInvalidateShader;
  }
  
  public void setAlpha(int paramInt) {
    this.mPaint.setAlpha(paramInt);
    invalidateSelf();
  }
  
  void setBorderTint(ColorStateList paramColorStateList) {
    if (paramColorStateList != null)
      this.mCurrentBorderTintColor = paramColorStateList.getColorForState(getState(), this.mCurrentBorderTintColor); 
    this.mBorderTint = paramColorStateList;
    this.mInvalidateShader = true;
    invalidateSelf();
  }
  
  void setBorderWidth(float paramFloat) {
    if (this.mBorderWidth != paramFloat) {
      this.mBorderWidth = paramFloat;
      this.mPaint.setStrokeWidth(paramFloat * 1.3333F);
      this.mInvalidateShader = true;
      invalidateSelf();
    } 
  }
  
  public void setColorFilter(ColorFilter paramColorFilter) {
    this.mPaint.setColorFilter(paramColorFilter);
    invalidateSelf();
  }
  
  void setGradientColors(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.mTopOuterStrokeColor = paramInt1;
    this.mTopInnerStrokeColor = paramInt2;
    this.mBottomOuterStrokeColor = paramInt3;
    this.mBottomInnerStrokeColor = paramInt4;
  }
  
  final void setRotation(float paramFloat) {
    if (paramFloat != this.mRotation) {
      this.mRotation = paramFloat;
      invalidateSelf();
    } 
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/design/widget/CircularBorderDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */