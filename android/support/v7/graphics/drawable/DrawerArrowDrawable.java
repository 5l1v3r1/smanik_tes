package android.support.v7.graphics.drawable;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.RestrictTo;
import android.support.v7.appcompat.R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class DrawerArrowDrawable extends Drawable {
  public static final int ARROW_DIRECTION_END = 3;
  
  public static final int ARROW_DIRECTION_LEFT = 0;
  
  public static final int ARROW_DIRECTION_RIGHT = 1;
  
  public static final int ARROW_DIRECTION_START = 2;
  
  private static final float ARROW_HEAD_ANGLE = (float)Math.toRadians(45.0D);
  
  private float mArrowHeadLength;
  
  private float mArrowShaftLength;
  
  private float mBarGap;
  
  private float mBarLength;
  
  private int mDirection = 2;
  
  private float mMaxCutForBarSize;
  
  private final Paint mPaint = new Paint();
  
  private final Path mPath = new Path();
  
  private float mProgress;
  
  private final int mSize;
  
  private boolean mSpin;
  
  private boolean mVerticalMirror = false;
  
  public DrawerArrowDrawable(Context paramContext) {
    this.mPaint.setStyle(Paint.Style.STROKE);
    this.mPaint.setStrokeJoin(Paint.Join.MITER);
    this.mPaint.setStrokeCap(Paint.Cap.BUTT);
    this.mPaint.setAntiAlias(true);
    TypedArray typedArray = paramContext.getTheme().obtainStyledAttributes(null, R.styleable.DrawerArrowToggle, R.attr.drawerArrowStyle, R.style.Base_Widget_AppCompat_DrawerArrowToggle);
    setColor(typedArray.getColor(R.styleable.DrawerArrowToggle_color, 0));
    setBarThickness(typedArray.getDimension(R.styleable.DrawerArrowToggle_thickness, 0.0F));
    setSpinEnabled(typedArray.getBoolean(R.styleable.DrawerArrowToggle_spinBars, true));
    setGapSize(Math.round(typedArray.getDimension(R.styleable.DrawerArrowToggle_gapBetweenBars, 0.0F)));
    this.mSize = typedArray.getDimensionPixelSize(R.styleable.DrawerArrowToggle_drawableSize, 0);
    this.mBarLength = Math.round(typedArray.getDimension(R.styleable.DrawerArrowToggle_barLength, 0.0F));
    this.mArrowHeadLength = Math.round(typedArray.getDimension(R.styleable.DrawerArrowToggle_arrowHeadLength, 0.0F));
    this.mArrowShaftLength = typedArray.getDimension(R.styleable.DrawerArrowToggle_arrowShaftLength, 0.0F);
    typedArray.recycle();
  }
  
  private static float lerp(float paramFloat1, float paramFloat2, float paramFloat3) { return paramFloat1 + (paramFloat2 - paramFloat1) * paramFloat3; }
  
  public void draw(Canvas paramCanvas) { // Byte code:
    //   0: aload_0
    //   1: invokevirtual getBounds : ()Landroid/graphics/Rect;
    //   4: astore #17
    //   6: aload_0
    //   7: getfield mDirection : I
    //   10: istore #16
    //   12: iconst_0
    //   13: istore #15
    //   15: iconst_1
    //   16: istore #14
    //   18: iload #16
    //   20: iconst_3
    //   21: if_icmpeq -> 70
    //   24: iload #15
    //   26: istore #13
    //   28: iload #16
    //   30: tableswitch default -> 52, 0 -> 84, 1 -> 64
    //   52: iload #15
    //   54: istore #13
    //   56: aload_0
    //   57: invokestatic getLayoutDirection : (Landroid/graphics/drawable/Drawable;)I
    //   60: iconst_1
    //   61: if_icmpne -> 84
    //   64: iconst_1
    //   65: istore #13
    //   67: goto -> 84
    //   70: iload #15
    //   72: istore #13
    //   74: aload_0
    //   75: invokestatic getLayoutDirection : (Landroid/graphics/drawable/Drawable;)I
    //   78: ifne -> 84
    //   81: goto -> 64
    //   84: aload_0
    //   85: getfield mArrowHeadLength : F
    //   88: aload_0
    //   89: getfield mArrowHeadLength : F
    //   92: fmul
    //   93: fconst_2
    //   94: fmul
    //   95: f2d
    //   96: invokestatic sqrt : (D)D
    //   99: d2f
    //   100: fstore #6
    //   102: aload_0
    //   103: getfield mBarLength : F
    //   106: fload #6
    //   108: aload_0
    //   109: getfield mProgress : F
    //   112: invokestatic lerp : (FFF)F
    //   115: fstore #10
    //   117: aload_0
    //   118: getfield mBarLength : F
    //   121: aload_0
    //   122: getfield mArrowShaftLength : F
    //   125: aload_0
    //   126: getfield mProgress : F
    //   129: invokestatic lerp : (FFF)F
    //   132: fstore #8
    //   134: fconst_0
    //   135: aload_0
    //   136: getfield mMaxCutForBarSize : F
    //   139: aload_0
    //   140: getfield mProgress : F
    //   143: invokestatic lerp : (FFF)F
    //   146: invokestatic round : (F)I
    //   149: i2f
    //   150: fstore #9
    //   152: fconst_0
    //   153: getstatic android/support/v7/graphics/drawable/DrawerArrowDrawable.ARROW_HEAD_ANGLE : F
    //   156: aload_0
    //   157: getfield mProgress : F
    //   160: invokestatic lerp : (FFF)F
    //   163: fstore #11
    //   165: iload #13
    //   167: ifeq -> 176
    //   170: fconst_0
    //   171: fstore #6
    //   173: goto -> 180
    //   176: ldc -180.0
    //   178: fstore #6
    //   180: iload #13
    //   182: ifeq -> 192
    //   185: ldc 180.0
    //   187: fstore #7
    //   189: goto -> 195
    //   192: fconst_0
    //   193: fstore #7
    //   195: fload #6
    //   197: fload #7
    //   199: aload_0
    //   200: getfield mProgress : F
    //   203: invokestatic lerp : (FFF)F
    //   206: fstore #6
    //   208: fload #10
    //   210: f2d
    //   211: dstore_2
    //   212: fload #11
    //   214: f2d
    //   215: dstore #4
    //   217: dload_2
    //   218: dload #4
    //   220: invokestatic cos : (D)D
    //   223: dmul
    //   224: invokestatic round : (D)J
    //   227: l2f
    //   228: fstore #7
    //   230: dload_2
    //   231: dload #4
    //   233: invokestatic sin : (D)D
    //   236: dmul
    //   237: invokestatic round : (D)J
    //   240: l2f
    //   241: fstore #10
    //   243: aload_0
    //   244: getfield mPath : Landroid/graphics/Path;
    //   247: invokevirtual rewind : ()V
    //   250: aload_0
    //   251: getfield mBarGap : F
    //   254: aload_0
    //   255: getfield mPaint : Landroid/graphics/Paint;
    //   258: invokevirtual getStrokeWidth : ()F
    //   261: fadd
    //   262: aload_0
    //   263: getfield mMaxCutForBarSize : F
    //   266: fneg
    //   267: aload_0
    //   268: getfield mProgress : F
    //   271: invokestatic lerp : (FFF)F
    //   274: fstore #11
    //   276: fload #8
    //   278: fneg
    //   279: fconst_2
    //   280: fdiv
    //   281: fstore #12
    //   283: aload_0
    //   284: getfield mPath : Landroid/graphics/Path;
    //   287: fload #12
    //   289: fload #9
    //   291: fadd
    //   292: fconst_0
    //   293: invokevirtual moveTo : (FF)V
    //   296: aload_0
    //   297: getfield mPath : Landroid/graphics/Path;
    //   300: fload #8
    //   302: fload #9
    //   304: fconst_2
    //   305: fmul
    //   306: fsub
    //   307: fconst_0
    //   308: invokevirtual rLineTo : (FF)V
    //   311: aload_0
    //   312: getfield mPath : Landroid/graphics/Path;
    //   315: fload #12
    //   317: fload #11
    //   319: invokevirtual moveTo : (FF)V
    //   322: aload_0
    //   323: getfield mPath : Landroid/graphics/Path;
    //   326: fload #7
    //   328: fload #10
    //   330: invokevirtual rLineTo : (FF)V
    //   333: aload_0
    //   334: getfield mPath : Landroid/graphics/Path;
    //   337: fload #12
    //   339: fload #11
    //   341: fneg
    //   342: invokevirtual moveTo : (FF)V
    //   345: aload_0
    //   346: getfield mPath : Landroid/graphics/Path;
    //   349: fload #7
    //   351: fload #10
    //   353: fneg
    //   354: invokevirtual rLineTo : (FF)V
    //   357: aload_0
    //   358: getfield mPath : Landroid/graphics/Path;
    //   361: invokevirtual close : ()V
    //   364: aload_1
    //   365: invokevirtual save : ()I
    //   368: pop
    //   369: aload_0
    //   370: getfield mPaint : Landroid/graphics/Paint;
    //   373: invokevirtual getStrokeWidth : ()F
    //   376: fstore #7
    //   378: aload #17
    //   380: invokevirtual height : ()I
    //   383: i2f
    //   384: ldc_w 3.0
    //   387: fload #7
    //   389: fmul
    //   390: fsub
    //   391: aload_0
    //   392: getfield mBarGap : F
    //   395: fconst_2
    //   396: fmul
    //   397: fsub
    //   398: f2i
    //   399: iconst_4
    //   400: idiv
    //   401: iconst_2
    //   402: imul
    //   403: i2f
    //   404: fstore #8
    //   406: aload_0
    //   407: getfield mBarGap : F
    //   410: fstore #9
    //   412: aload_1
    //   413: aload #17
    //   415: invokevirtual centerX : ()I
    //   418: i2f
    //   419: fload #8
    //   421: fload #7
    //   423: ldc_w 1.5
    //   426: fmul
    //   427: fload #9
    //   429: fadd
    //   430: fadd
    //   431: invokevirtual translate : (FF)V
    //   434: aload_0
    //   435: getfield mSpin : Z
    //   438: ifeq -> 467
    //   441: aload_0
    //   442: getfield mVerticalMirror : Z
    //   445: iload #13
    //   447: ixor
    //   448: ifeq -> 454
    //   451: iconst_m1
    //   452: istore #14
    //   454: aload_1
    //   455: fload #6
    //   457: iload #14
    //   459: i2f
    //   460: fmul
    //   461: invokevirtual rotate : (F)V
    //   464: goto -> 478
    //   467: iload #13
    //   469: ifeq -> 478
    //   472: aload_1
    //   473: ldc 180.0
    //   475: invokevirtual rotate : (F)V
    //   478: aload_1
    //   479: aload_0
    //   480: getfield mPath : Landroid/graphics/Path;
    //   483: aload_0
    //   484: getfield mPaint : Landroid/graphics/Paint;
    //   487: invokevirtual drawPath : (Landroid/graphics/Path;Landroid/graphics/Paint;)V
    //   490: aload_1
    //   491: invokevirtual restore : ()V
    //   494: return }
  
  public float getArrowHeadLength() { return this.mArrowHeadLength; }
  
  public float getArrowShaftLength() { return this.mArrowShaftLength; }
  
  public float getBarLength() { return this.mBarLength; }
  
  public float getBarThickness() { return this.mPaint.getStrokeWidth(); }
  
  @ColorInt
  public int getColor() { return this.mPaint.getColor(); }
  
  public int getDirection() { return this.mDirection; }
  
  public float getGapSize() { return this.mBarGap; }
  
  public int getIntrinsicHeight() { return this.mSize; }
  
  public int getIntrinsicWidth() { return this.mSize; }
  
  public int getOpacity() { return -3; }
  
  public final Paint getPaint() { return this.mPaint; }
  
  @FloatRange(from = 0.0D, to = 1.0D)
  public float getProgress() { return this.mProgress; }
  
  public boolean isSpinEnabled() { return this.mSpin; }
  
  public void setAlpha(int paramInt) {
    if (paramInt != this.mPaint.getAlpha()) {
      this.mPaint.setAlpha(paramInt);
      invalidateSelf();
    } 
  }
  
  public void setArrowHeadLength(float paramFloat) {
    if (this.mArrowHeadLength != paramFloat) {
      this.mArrowHeadLength = paramFloat;
      invalidateSelf();
    } 
  }
  
  public void setArrowShaftLength(float paramFloat) {
    if (this.mArrowShaftLength != paramFloat) {
      this.mArrowShaftLength = paramFloat;
      invalidateSelf();
    } 
  }
  
  public void setBarLength(float paramFloat) {
    if (this.mBarLength != paramFloat) {
      this.mBarLength = paramFloat;
      invalidateSelf();
    } 
  }
  
  public void setBarThickness(float paramFloat) {
    if (this.mPaint.getStrokeWidth() != paramFloat) {
      this.mPaint.setStrokeWidth(paramFloat);
      this.mMaxCutForBarSize = (float)((paramFloat / 2.0F) * Math.cos(ARROW_HEAD_ANGLE));
      invalidateSelf();
    } 
  }
  
  public void setColor(@ColorInt int paramInt) {
    if (paramInt != this.mPaint.getColor()) {
      this.mPaint.setColor(paramInt);
      invalidateSelf();
    } 
  }
  
  public void setColorFilter(ColorFilter paramColorFilter) {
    this.mPaint.setColorFilter(paramColorFilter);
    invalidateSelf();
  }
  
  public void setDirection(int paramInt) {
    if (paramInt != this.mDirection) {
      this.mDirection = paramInt;
      invalidateSelf();
    } 
  }
  
  public void setGapSize(float paramFloat) {
    if (paramFloat != this.mBarGap) {
      this.mBarGap = paramFloat;
      invalidateSelf();
    } 
  }
  
  public void setProgress(@FloatRange(from = 0.0D, to = 1.0D) float paramFloat) {
    if (this.mProgress != paramFloat) {
      this.mProgress = paramFloat;
      invalidateSelf();
    } 
  }
  
  public void setSpinEnabled(boolean paramBoolean) {
    if (this.mSpin != paramBoolean) {
      this.mSpin = paramBoolean;
      invalidateSelf();
    } 
  }
  
  public void setVerticalMirror(boolean paramBoolean) {
    if (this.mVerticalMirror != paramBoolean) {
      this.mVerticalMirror = paramBoolean;
      invalidateSelf();
    } 
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface ArrowDirection {}
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/graphics/drawable/DrawerArrowDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */