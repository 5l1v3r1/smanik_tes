package android.support.design.widget;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.math.MathUtils;
import android.support.v4.text.TextDirectionHeuristicCompat;
import android.support.v4.text.TextDirectionHeuristicsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R;
import android.support.v7.widget.TintTypedArray;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Interpolator;

final class CollapsingTextHelper {
  private static final boolean DEBUG_DRAW = false;
  
  private static final Paint DEBUG_DRAW_PAINT;
  
  private static final boolean USE_SCALING_TEXTURE;
  
  private boolean mBoundsChanged;
  
  private final Rect mCollapsedBounds;
  
  private float mCollapsedDrawX;
  
  private float mCollapsedDrawY;
  
  private int mCollapsedShadowColor;
  
  private float mCollapsedShadowDx;
  
  private float mCollapsedShadowDy;
  
  private float mCollapsedShadowRadius;
  
  private ColorStateList mCollapsedTextColor;
  
  private int mCollapsedTextGravity = 16;
  
  private float mCollapsedTextSize = 15.0F;
  
  private Typeface mCollapsedTypeface;
  
  private final RectF mCurrentBounds;
  
  private float mCurrentDrawX;
  
  private float mCurrentDrawY;
  
  private float mCurrentTextSize;
  
  private Typeface mCurrentTypeface;
  
  private boolean mDrawTitle;
  
  private final Rect mExpandedBounds;
  
  private float mExpandedDrawX;
  
  private float mExpandedDrawY;
  
  private float mExpandedFraction;
  
  private int mExpandedShadowColor;
  
  private float mExpandedShadowDx;
  
  private float mExpandedShadowDy;
  
  private float mExpandedShadowRadius;
  
  private ColorStateList mExpandedTextColor;
  
  private int mExpandedTextGravity = 16;
  
  private float mExpandedTextSize = 15.0F;
  
  private Bitmap mExpandedTitleTexture;
  
  private Typeface mExpandedTypeface;
  
  private boolean mIsRtl;
  
  private Interpolator mPositionInterpolator;
  
  private float mScale;
  
  private int[] mState;
  
  private CharSequence mText;
  
  private final TextPaint mTextPaint;
  
  private Interpolator mTextSizeInterpolator;
  
  private CharSequence mTextToDraw;
  
  private float mTextureAscent;
  
  private float mTextureDescent;
  
  private Paint mTexturePaint;
  
  private boolean mUseTexture;
  
  private final View mView;
  
  static  {
    boolean bool;
    if (Build.VERSION.SDK_INT < 18) {
      bool = true;
    } else {
      bool = false;
    } 
    USE_SCALING_TEXTURE = bool;
    if (DEBUG_DRAW_PAINT != null) {
      DEBUG_DRAW_PAINT.setAntiAlias(true);
      DEBUG_DRAW_PAINT.setColor(-65281);
    } 
  }
  
  public CollapsingTextHelper(View paramView) {
    this.mView = paramView;
    this.mTextPaint = new TextPaint(129);
    this.mCollapsedBounds = new Rect();
    this.mExpandedBounds = new Rect();
    this.mCurrentBounds = new RectF();
  }
  
  private boolean areTypefacesDifferent(Typeface paramTypeface1, Typeface paramTypeface2) { return ((paramTypeface1 != null && !paramTypeface1.equals(paramTypeface2)) || (paramTypeface1 == null && paramTypeface2 != null)); }
  
  private static int blendColors(int paramInt1, int paramInt2, float paramFloat) {
    float f1 = 1.0F - paramFloat;
    float f2 = Color.alpha(paramInt1);
    float f3 = Color.alpha(paramInt2);
    float f4 = Color.red(paramInt1);
    float f5 = Color.red(paramInt2);
    float f6 = Color.green(paramInt1);
    float f7 = Color.green(paramInt2);
    float f8 = Color.blue(paramInt1);
    float f9 = Color.blue(paramInt2);
    return Color.argb((int)(f2 * f1 + f3 * paramFloat), (int)(f4 * f1 + f5 * paramFloat), (int)(f6 * f1 + f7 * paramFloat), (int)(f8 * f1 + f9 * paramFloat));
  }
  
  private void calculateBaseOffsets() { throw new RuntimeException("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.provideAs(TypeTransformer.java:780)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.e1expr(TypeTransformer.java:496)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:713)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.enexpr(TypeTransformer.java:698)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:719)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.s2stmt(TypeTransformer.java:820)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.sxStmt(TypeTransformer.java:843)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:206)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n"); }
  
  private void calculateCurrentOffsets() { calculateOffsets(this.mExpandedFraction); }
  
  private boolean calculateIsRtl(CharSequence paramCharSequence) {
    TextDirectionHeuristicCompat textDirectionHeuristicCompat;
    int i = ViewCompat.getLayoutDirection(this.mView);
    boolean bool = true;
    if (i != 1)
      bool = false; 
    if (bool) {
      textDirectionHeuristicCompat = TextDirectionHeuristicsCompat.FIRSTSTRONG_RTL;
    } else {
      textDirectionHeuristicCompat = TextDirectionHeuristicsCompat.FIRSTSTRONG_LTR;
    } 
    return textDirectionHeuristicCompat.isRtl(paramCharSequence, 0, paramCharSequence.length());
  }
  
  private void calculateOffsets(float paramFloat) {
    interpolateBounds(paramFloat);
    this.mCurrentDrawX = lerp(this.mExpandedDrawX, this.mCollapsedDrawX, paramFloat, this.mPositionInterpolator);
    this.mCurrentDrawY = lerp(this.mExpandedDrawY, this.mCollapsedDrawY, paramFloat, this.mPositionInterpolator);
    setInterpolatedTextSize(lerp(this.mExpandedTextSize, this.mCollapsedTextSize, paramFloat, this.mTextSizeInterpolator));
    if (this.mCollapsedTextColor != this.mExpandedTextColor) {
      this.mTextPaint.setColor(blendColors(getCurrentExpandedTextColor(), getCurrentCollapsedTextColor(), paramFloat));
    } else {
      this.mTextPaint.setColor(getCurrentCollapsedTextColor());
    } 
    this.mTextPaint.setShadowLayer(lerp(this.mExpandedShadowRadius, this.mCollapsedShadowRadius, paramFloat, null), lerp(this.mExpandedShadowDx, this.mCollapsedShadowDx, paramFloat, null), lerp(this.mExpandedShadowDy, this.mCollapsedShadowDy, paramFloat, null), blendColors(this.mExpandedShadowColor, this.mCollapsedShadowColor, paramFloat));
    ViewCompat.postInvalidateOnAnimation(this.mView);
  }
  
  private void calculateUsingTextSize(float paramFloat) {
    byte b1;
    float f1;
    if (this.mText == null)
      return; 
    float f2 = this.mCollapsedBounds.width();
    float f3 = this.mExpandedBounds.width();
    boolean bool2 = isClose(paramFloat, this.mCollapsedTextSize);
    boolean bool1 = true;
    if (bool2) {
      f1 = this.mCollapsedTextSize;
      this.mScale = 1.0F;
      if (areTypefacesDifferent(this.mCurrentTypeface, this.mCollapsedTypeface)) {
        this.mCurrentTypeface = this.mCollapsedTypeface;
        b1 = 1;
      } else {
        b1 = 0;
      } 
      paramFloat = f2;
    } else {
      f1 = this.mExpandedTextSize;
      if (areTypefacesDifferent(this.mCurrentTypeface, this.mExpandedTypeface)) {
        this.mCurrentTypeface = this.mExpandedTypeface;
        b1 = 1;
      } else {
        b1 = 0;
      } 
      if (isClose(paramFloat, this.mExpandedTextSize)) {
        this.mScale = 1.0F;
      } else {
        this.mScale = paramFloat / this.mExpandedTextSize;
      } 
      paramFloat = this.mCollapsedTextSize / this.mExpandedTextSize;
      if (f3 * paramFloat > f2) {
        paramFloat = Math.min(f2 / paramFloat, f3);
      } else {
        paramFloat = f3;
      } 
    } 
    byte b2 = b1;
    if (paramFloat > 0.0F) {
      if (this.mCurrentTextSize != f1 || this.mBoundsChanged || b1) {
        b1 = 1;
      } else {
        b1 = 0;
      } 
      this.mCurrentTextSize = f1;
      this.mBoundsChanged = false;
      b2 = b1;
    } 
    if (this.mTextToDraw == null || b2 != 0) {
      this.mTextPaint.setTextSize(this.mCurrentTextSize);
      this.mTextPaint.setTypeface(this.mCurrentTypeface);
      TextPaint textPaint = this.mTextPaint;
      if (this.mScale == 1.0F)
        bool1 = false; 
      textPaint.setLinearText(bool1);
      CharSequence charSequence = TextUtils.ellipsize(this.mText, this.mTextPaint, paramFloat, TextUtils.TruncateAt.END);
      if (!TextUtils.equals(charSequence, this.mTextToDraw)) {
        this.mTextToDraw = charSequence;
        this.mIsRtl = calculateIsRtl(this.mTextToDraw);
      } 
    } 
  }
  
  private void clearTexture() {
    if (this.mExpandedTitleTexture != null) {
      this.mExpandedTitleTexture.recycle();
      this.mExpandedTitleTexture = null;
    } 
  }
  
  private void ensureExpandedTexture() {
    if (this.mExpandedTitleTexture == null && !this.mExpandedBounds.isEmpty()) {
      if (TextUtils.isEmpty(this.mTextToDraw))
        return; 
      calculateOffsets(0.0F);
      this.mTextureAscent = this.mTextPaint.ascent();
      this.mTextureDescent = this.mTextPaint.descent();
      int i = Math.round(this.mTextPaint.measureText(this.mTextToDraw, 0, this.mTextToDraw.length()));
      int j = Math.round(this.mTextureDescent - this.mTextureAscent);
      if (i > 0) {
        if (j <= 0)
          return; 
        this.mExpandedTitleTexture = Bitmap.createBitmap(i, j, Bitmap.Config.ARGB_8888);
        (new Canvas(this.mExpandedTitleTexture)).drawText(this.mTextToDraw, 0, this.mTextToDraw.length(), 0.0F, j - this.mTextPaint.descent(), this.mTextPaint);
        if (this.mTexturePaint == null)
          this.mTexturePaint = new Paint(3); 
        return;
      } 
      return;
    } 
  }
  
  @ColorInt
  private int getCurrentCollapsedTextColor() { return (this.mState != null) ? this.mCollapsedTextColor.getColorForState(this.mState, 0) : this.mCollapsedTextColor.getDefaultColor(); }
  
  @ColorInt
  private int getCurrentExpandedTextColor() { return (this.mState != null) ? this.mExpandedTextColor.getColorForState(this.mState, 0) : this.mExpandedTextColor.getDefaultColor(); }
  
  private void interpolateBounds(float paramFloat) {
    this.mCurrentBounds.left = lerp(this.mExpandedBounds.left, this.mCollapsedBounds.left, paramFloat, this.mPositionInterpolator);
    this.mCurrentBounds.top = lerp(this.mExpandedDrawY, this.mCollapsedDrawY, paramFloat, this.mPositionInterpolator);
    this.mCurrentBounds.right = lerp(this.mExpandedBounds.right, this.mCollapsedBounds.right, paramFloat, this.mPositionInterpolator);
    this.mCurrentBounds.bottom = lerp(this.mExpandedBounds.bottom, this.mCollapsedBounds.bottom, paramFloat, this.mPositionInterpolator);
  }
  
  private static boolean isClose(float paramFloat1, float paramFloat2) { return (Math.abs(paramFloat1 - paramFloat2) < 0.001F); }
  
  private static float lerp(float paramFloat1, float paramFloat2, float paramFloat3, Interpolator paramInterpolator) {
    float f = paramFloat3;
    if (paramInterpolator != null)
      f = paramInterpolator.getInterpolation(paramFloat3); 
    return AnimationUtils.lerp(paramFloat1, paramFloat2, f);
  }
  
  private Typeface readFontFamilyTypeface(int paramInt) {
    typedArray = this.mView.getContext().obtainStyledAttributes(paramInt, new int[] { 16843692 });
    try {
      String str = typedArray.getString(0);
      if (str != null)
        return Typeface.create(str, 0); 
      return null;
    } finally {
      typedArray.recycle();
    } 
  }
  
  private static boolean rectEquals(Rect paramRect, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return (paramRect.left == paramInt1 && paramRect.top == paramInt2 && paramRect.right == paramInt3 && paramRect.bottom == paramInt4); }
  
  private void setInterpolatedTextSize(float paramFloat) {
    boolean bool;
    calculateUsingTextSize(paramFloat);
    if (USE_SCALING_TEXTURE && this.mScale != 1.0F) {
      bool = true;
    } else {
      bool = false;
    } 
    this.mUseTexture = bool;
    if (this.mUseTexture)
      ensureExpandedTexture(); 
    ViewCompat.postInvalidateOnAnimation(this.mView);
  }
  
  public void draw(Canvas paramCanvas) {
    int i = paramCanvas.save();
    if (this.mTextToDraw != null && this.mDrawTitle) {
      boolean bool;
      float f1;
      float f4 = this.mCurrentDrawX;
      float f3 = this.mCurrentDrawY;
      if (this.mUseTexture && this.mExpandedTitleTexture != null) {
        bool = true;
      } else {
        bool = false;
      } 
      if (bool) {
        f1 = this.mTextureAscent * this.mScale;
        float f = this.mTextureDescent;
        f = this.mScale;
      } else {
        f1 = this.mTextPaint.ascent() * this.mScale;
        this.mTextPaint.descent();
        float f = this.mScale;
      } 
      float f2 = f3;
      if (bool)
        f2 = f3 + f1; 
      if (this.mScale != 1.0F)
        paramCanvas.scale(this.mScale, this.mScale, f4, f2); 
      if (bool) {
        paramCanvas.drawBitmap(this.mExpandedTitleTexture, f4, f2, this.mTexturePaint);
      } else {
        paramCanvas.drawText(this.mTextToDraw, 0, this.mTextToDraw.length(), f4, f2, this.mTextPaint);
      } 
    } 
    paramCanvas.restoreToCount(i);
  }
  
  ColorStateList getCollapsedTextColor() { return this.mCollapsedTextColor; }
  
  int getCollapsedTextGravity() { return this.mCollapsedTextGravity; }
  
  float getCollapsedTextSize() { return this.mCollapsedTextSize; }
  
  Typeface getCollapsedTypeface() { return (this.mCollapsedTypeface != null) ? this.mCollapsedTypeface : Typeface.DEFAULT; }
  
  ColorStateList getExpandedTextColor() { return this.mExpandedTextColor; }
  
  int getExpandedTextGravity() { return this.mExpandedTextGravity; }
  
  float getExpandedTextSize() { return this.mExpandedTextSize; }
  
  Typeface getExpandedTypeface() { return (this.mExpandedTypeface != null) ? this.mExpandedTypeface : Typeface.DEFAULT; }
  
  float getExpansionFraction() { return this.mExpandedFraction; }
  
  CharSequence getText() { return this.mText; }
  
  final boolean isStateful() { return ((this.mCollapsedTextColor != null && this.mCollapsedTextColor.isStateful()) || (this.mExpandedTextColor != null && this.mExpandedTextColor.isStateful())); }
  
  void onBoundsChanged() {
    boolean bool;
    if (this.mCollapsedBounds.width() > 0 && this.mCollapsedBounds.height() > 0 && this.mExpandedBounds.width() > 0 && this.mExpandedBounds.height() > 0) {
      bool = true;
    } else {
      bool = false;
    } 
    this.mDrawTitle = bool;
  }
  
  public void recalculate() {
    if (this.mView.getHeight() > 0 && this.mView.getWidth() > 0) {
      calculateBaseOffsets();
      calculateCurrentOffsets();
    } 
  }
  
  void setCollapsedBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (!rectEquals(this.mCollapsedBounds, paramInt1, paramInt2, paramInt3, paramInt4)) {
      this.mCollapsedBounds.set(paramInt1, paramInt2, paramInt3, paramInt4);
      this.mBoundsChanged = true;
      onBoundsChanged();
    } 
  }
  
  void setCollapsedTextAppearance(int paramInt) {
    TintTypedArray tintTypedArray = TintTypedArray.obtainStyledAttributes(this.mView.getContext(), paramInt, R.styleable.TextAppearance);
    if (tintTypedArray.hasValue(R.styleable.TextAppearance_android_textColor))
      this.mCollapsedTextColor = tintTypedArray.getColorStateList(R.styleable.TextAppearance_android_textColor); 
    if (tintTypedArray.hasValue(R.styleable.TextAppearance_android_textSize))
      this.mCollapsedTextSize = tintTypedArray.getDimensionPixelSize(R.styleable.TextAppearance_android_textSize, (int)this.mCollapsedTextSize); 
    this.mCollapsedShadowColor = tintTypedArray.getInt(R.styleable.TextAppearance_android_shadowColor, 0);
    this.mCollapsedShadowDx = tintTypedArray.getFloat(R.styleable.TextAppearance_android_shadowDx, 0.0F);
    this.mCollapsedShadowDy = tintTypedArray.getFloat(R.styleable.TextAppearance_android_shadowDy, 0.0F);
    this.mCollapsedShadowRadius = tintTypedArray.getFloat(R.styleable.TextAppearance_android_shadowRadius, 0.0F);
    tintTypedArray.recycle();
    if (Build.VERSION.SDK_INT >= 16)
      this.mCollapsedTypeface = readFontFamilyTypeface(paramInt); 
    recalculate();
  }
  
  void setCollapsedTextColor(ColorStateList paramColorStateList) {
    if (this.mCollapsedTextColor != paramColorStateList) {
      this.mCollapsedTextColor = paramColorStateList;
      recalculate();
    } 
  }
  
  void setCollapsedTextGravity(int paramInt) {
    if (this.mCollapsedTextGravity != paramInt) {
      this.mCollapsedTextGravity = paramInt;
      recalculate();
    } 
  }
  
  void setCollapsedTextSize(float paramFloat) {
    if (this.mCollapsedTextSize != paramFloat) {
      this.mCollapsedTextSize = paramFloat;
      recalculate();
    } 
  }
  
  void setCollapsedTypeface(Typeface paramTypeface) {
    if (areTypefacesDifferent(this.mCollapsedTypeface, paramTypeface)) {
      this.mCollapsedTypeface = paramTypeface;
      recalculate();
    } 
  }
  
  void setExpandedBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    if (!rectEquals(this.mExpandedBounds, paramInt1, paramInt2, paramInt3, paramInt4)) {
      this.mExpandedBounds.set(paramInt1, paramInt2, paramInt3, paramInt4);
      this.mBoundsChanged = true;
      onBoundsChanged();
    } 
  }
  
  void setExpandedTextAppearance(int paramInt) {
    TintTypedArray tintTypedArray = TintTypedArray.obtainStyledAttributes(this.mView.getContext(), paramInt, R.styleable.TextAppearance);
    if (tintTypedArray.hasValue(R.styleable.TextAppearance_android_textColor))
      this.mExpandedTextColor = tintTypedArray.getColorStateList(R.styleable.TextAppearance_android_textColor); 
    if (tintTypedArray.hasValue(R.styleable.TextAppearance_android_textSize))
      this.mExpandedTextSize = tintTypedArray.getDimensionPixelSize(R.styleable.TextAppearance_android_textSize, (int)this.mExpandedTextSize); 
    this.mExpandedShadowColor = tintTypedArray.getInt(R.styleable.TextAppearance_android_shadowColor, 0);
    this.mExpandedShadowDx = tintTypedArray.getFloat(R.styleable.TextAppearance_android_shadowDx, 0.0F);
    this.mExpandedShadowDy = tintTypedArray.getFloat(R.styleable.TextAppearance_android_shadowDy, 0.0F);
    this.mExpandedShadowRadius = tintTypedArray.getFloat(R.styleable.TextAppearance_android_shadowRadius, 0.0F);
    tintTypedArray.recycle();
    if (Build.VERSION.SDK_INT >= 16)
      this.mExpandedTypeface = readFontFamilyTypeface(paramInt); 
    recalculate();
  }
  
  void setExpandedTextColor(ColorStateList paramColorStateList) {
    if (this.mExpandedTextColor != paramColorStateList) {
      this.mExpandedTextColor = paramColorStateList;
      recalculate();
    } 
  }
  
  void setExpandedTextGravity(int paramInt) {
    if (this.mExpandedTextGravity != paramInt) {
      this.mExpandedTextGravity = paramInt;
      recalculate();
    } 
  }
  
  void setExpandedTextSize(float paramFloat) {
    if (this.mExpandedTextSize != paramFloat) {
      this.mExpandedTextSize = paramFloat;
      recalculate();
    } 
  }
  
  void setExpandedTypeface(Typeface paramTypeface) {
    if (areTypefacesDifferent(this.mExpandedTypeface, paramTypeface)) {
      this.mExpandedTypeface = paramTypeface;
      recalculate();
    } 
  }
  
  void setExpansionFraction(float paramFloat) {
    paramFloat = MathUtils.clamp(paramFloat, 0.0F, 1.0F);
    if (paramFloat != this.mExpandedFraction) {
      this.mExpandedFraction = paramFloat;
      calculateCurrentOffsets();
    } 
  }
  
  void setPositionInterpolator(Interpolator paramInterpolator) {
    this.mPositionInterpolator = paramInterpolator;
    recalculate();
  }
  
  final boolean setState(int[] paramArrayOfInt) {
    this.mState = paramArrayOfInt;
    if (isStateful()) {
      recalculate();
      return true;
    } 
    return false;
  }
  
  void setText(CharSequence paramCharSequence) {
    if (paramCharSequence == null || !paramCharSequence.equals(this.mText)) {
      this.mText = paramCharSequence;
      this.mTextToDraw = null;
      clearTexture();
      recalculate();
    } 
  }
  
  void setTextSizeInterpolator(Interpolator paramInterpolator) {
    this.mTextSizeInterpolator = paramInterpolator;
    recalculate();
  }
  
  void setTypefaces(Typeface paramTypeface) {
    this.mExpandedTypeface = paramTypeface;
    this.mCollapsedTypeface = paramTypeface;
    recalculate();
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/design/widget/CollapsingTextHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */