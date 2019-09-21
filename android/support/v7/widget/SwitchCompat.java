package android.support.v7.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.text.AllCapsTransformationMethod;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.util.Property;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.CompoundButton;

public class SwitchCompat extends CompoundButton {
  private static final String ACCESSIBILITY_EVENT_CLASS_NAME = "android.widget.Switch";
  
  private static final int[] CHECKED_STATE_SET;
  
  private static final int MONOSPACE = 3;
  
  private static final int SANS = 1;
  
  private static final int SERIF = 2;
  
  private static final int THUMB_ANIMATION_DURATION = 250;
  
  private static final Property<SwitchCompat, Float> THUMB_POS = new Property<SwitchCompat, Float>(Float.class, "thumbPos") {
      public Float get(SwitchCompat param1SwitchCompat) { return Float.valueOf(param1SwitchCompat.mThumbPosition); }
      
      public void set(SwitchCompat param1SwitchCompat, Float param1Float) { param1SwitchCompat.setThumbPosition(param1Float.floatValue()); }
    };
  
  private static final int TOUCH_MODE_DOWN = 1;
  
  private static final int TOUCH_MODE_DRAGGING = 2;
  
  private static final int TOUCH_MODE_IDLE = 0;
  
  private boolean mHasThumbTint = false;
  
  private boolean mHasThumbTintMode = false;
  
  private boolean mHasTrackTint = false;
  
  private boolean mHasTrackTintMode = false;
  
  private int mMinFlingVelocity;
  
  private Layout mOffLayout;
  
  private Layout mOnLayout;
  
  ObjectAnimator mPositionAnimator;
  
  private boolean mShowText;
  
  private boolean mSplitTrack;
  
  private int mSwitchBottom;
  
  private int mSwitchHeight;
  
  private int mSwitchLeft;
  
  private int mSwitchMinWidth;
  
  private int mSwitchPadding;
  
  private int mSwitchRight;
  
  private int mSwitchTop;
  
  private TransformationMethod mSwitchTransformationMethod;
  
  private int mSwitchWidth;
  
  private final Rect mTempRect = new Rect();
  
  private ColorStateList mTextColors;
  
  private CharSequence mTextOff;
  
  private CharSequence mTextOn;
  
  private final TextPaint mTextPaint = new TextPaint(1);
  
  private Drawable mThumbDrawable;
  
  private float mThumbPosition;
  
  private int mThumbTextPadding;
  
  private ColorStateList mThumbTintList = null;
  
  private PorterDuff.Mode mThumbTintMode = null;
  
  private int mThumbWidth;
  
  private int mTouchMode;
  
  private int mTouchSlop;
  
  private float mTouchX;
  
  private float mTouchY;
  
  private Drawable mTrackDrawable;
  
  private ColorStateList mTrackTintList = null;
  
  private PorterDuff.Mode mTrackTintMode = null;
  
  private VelocityTracker mVelocityTracker = VelocityTracker.obtain();
  
  static  {
    CHECKED_STATE_SET = new int[] { 16842912 };
  }
  
  public SwitchCompat(Context paramContext) { this(paramContext, null); }
  
  public SwitchCompat(Context paramContext, AttributeSet paramAttributeSet) { this(paramContext, paramAttributeSet, R.attr.switchStyle); }
  
  public SwitchCompat(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
    super(paramContext, paramAttributeSet, paramInt);
    Resources resources = getResources();
    this.mTextPaint.density = (resources.getDisplayMetrics()).density;
    TintTypedArray tintTypedArray = TintTypedArray.obtainStyledAttributes(paramContext, paramAttributeSet, R.styleable.SwitchCompat, paramInt, 0);
    this.mThumbDrawable = tintTypedArray.getDrawable(R.styleable.SwitchCompat_android_thumb);
    if (this.mThumbDrawable != null)
      this.mThumbDrawable.setCallback(this); 
    this.mTrackDrawable = tintTypedArray.getDrawable(R.styleable.SwitchCompat_track);
    if (this.mTrackDrawable != null)
      this.mTrackDrawable.setCallback(this); 
    this.mTextOn = tintTypedArray.getText(R.styleable.SwitchCompat_android_textOn);
    this.mTextOff = tintTypedArray.getText(R.styleable.SwitchCompat_android_textOff);
    this.mShowText = tintTypedArray.getBoolean(R.styleable.SwitchCompat_showText, true);
    this.mThumbTextPadding = tintTypedArray.getDimensionPixelSize(R.styleable.SwitchCompat_thumbTextPadding, 0);
    this.mSwitchMinWidth = tintTypedArray.getDimensionPixelSize(R.styleable.SwitchCompat_switchMinWidth, 0);
    this.mSwitchPadding = tintTypedArray.getDimensionPixelSize(R.styleable.SwitchCompat_switchPadding, 0);
    this.mSplitTrack = tintTypedArray.getBoolean(R.styleable.SwitchCompat_splitTrack, false);
    ColorStateList colorStateList2 = tintTypedArray.getColorStateList(R.styleable.SwitchCompat_thumbTint);
    if (colorStateList2 != null) {
      this.mThumbTintList = colorStateList2;
      this.mHasThumbTint = true;
    } 
    PorterDuff.Mode mode2 = DrawableUtils.parseTintMode(tintTypedArray.getInt(R.styleable.SwitchCompat_thumbTintMode, -1), null);
    if (this.mThumbTintMode != mode2) {
      this.mThumbTintMode = mode2;
      this.mHasThumbTintMode = true;
    } 
    if (this.mHasThumbTint || this.mHasThumbTintMode)
      applyThumbTint(); 
    ColorStateList colorStateList1 = tintTypedArray.getColorStateList(R.styleable.SwitchCompat_trackTint);
    if (colorStateList1 != null) {
      this.mTrackTintList = colorStateList1;
      this.mHasTrackTint = true;
    } 
    PorterDuff.Mode mode1 = DrawableUtils.parseTintMode(tintTypedArray.getInt(R.styleable.SwitchCompat_trackTintMode, -1), null);
    if (this.mTrackTintMode != mode1) {
      this.mTrackTintMode = mode1;
      this.mHasTrackTintMode = true;
    } 
    if (this.mHasTrackTint || this.mHasTrackTintMode)
      applyTrackTint(); 
    paramInt = tintTypedArray.getResourceId(R.styleable.SwitchCompat_switchTextAppearance, 0);
    if (paramInt != 0)
      setSwitchTextAppearance(paramContext, paramInt); 
    tintTypedArray.recycle();
    ViewConfiguration viewConfiguration = ViewConfiguration.get(paramContext);
    this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
    this.mMinFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
    refreshDrawableState();
    setChecked(isChecked());
  }
  
  private void animateThumbToCheckedState(boolean paramBoolean) {
    float f;
    if (paramBoolean) {
      f = 1.0F;
    } else {
      f = 0.0F;
    } 
    this.mPositionAnimator = ObjectAnimator.ofFloat(this, THUMB_POS, new float[] { f });
    this.mPositionAnimator.setDuration(250L);
    if (Build.VERSION.SDK_INT >= 18)
      this.mPositionAnimator.setAutoCancel(true); 
    this.mPositionAnimator.start();
  }
  
  private void applyThumbTint() {
    if (this.mThumbDrawable != null && (this.mHasThumbTint || this.mHasThumbTintMode)) {
      this.mThumbDrawable = this.mThumbDrawable.mutate();
      if (this.mHasThumbTint)
        DrawableCompat.setTintList(this.mThumbDrawable, this.mThumbTintList); 
      if (this.mHasThumbTintMode)
        DrawableCompat.setTintMode(this.mThumbDrawable, this.mThumbTintMode); 
      if (this.mThumbDrawable.isStateful())
        this.mThumbDrawable.setState(getDrawableState()); 
    } 
  }
  
  private void applyTrackTint() {
    if (this.mTrackDrawable != null && (this.mHasTrackTint || this.mHasTrackTintMode)) {
      this.mTrackDrawable = this.mTrackDrawable.mutate();
      if (this.mHasTrackTint)
        DrawableCompat.setTintList(this.mTrackDrawable, this.mTrackTintList); 
      if (this.mHasTrackTintMode)
        DrawableCompat.setTintMode(this.mTrackDrawable, this.mTrackTintMode); 
      if (this.mTrackDrawable.isStateful())
        this.mTrackDrawable.setState(getDrawableState()); 
    } 
  }
  
  private void cancelPositionAnimator() {
    if (this.mPositionAnimator != null)
      this.mPositionAnimator.cancel(); 
  }
  
  private void cancelSuperTouch(MotionEvent paramMotionEvent) {
    paramMotionEvent = MotionEvent.obtain(paramMotionEvent);
    paramMotionEvent.setAction(3);
    super.onTouchEvent(paramMotionEvent);
    paramMotionEvent.recycle();
  }
  
  private static float constrain(float paramFloat1, float paramFloat2, float paramFloat3) {
    if (paramFloat1 < paramFloat2)
      return paramFloat2; 
    paramFloat2 = paramFloat1;
    if (paramFloat1 > paramFloat3)
      paramFloat2 = paramFloat3; 
    return paramFloat2;
  }
  
  private boolean getTargetCheckedState() { return (this.mThumbPosition > 0.5F); }
  
  private int getThumbOffset() {
    float f;
    if (ViewUtils.isLayoutRtl(this)) {
      f = 1.0F - this.mThumbPosition;
    } else {
      f = this.mThumbPosition;
    } 
    return (int)(f * getThumbScrollRange() + 0.5F);
  }
  
  private int getThumbScrollRange() {
    if (this.mTrackDrawable != null) {
      Rect rect1;
      Rect rect2 = this.mTempRect;
      this.mTrackDrawable.getPadding(rect2);
      if (this.mThumbDrawable != null) {
        rect1 = DrawableUtils.getOpticalBounds(this.mThumbDrawable);
      } else {
        rect1 = DrawableUtils.INSETS_NONE;
      } 
      return this.mSwitchWidth - this.mThumbWidth - rect2.left - rect2.right - rect1.left - rect1.right;
    } 
    return 0;
  }
  
  private boolean hitThumb(float paramFloat1, float paramFloat2) {
    Drawable drawable = this.mThumbDrawable;
    byte b = 0;
    if (drawable == null)
      return false; 
    int k = getThumbOffset();
    this.mThumbDrawable.getPadding(this.mTempRect);
    int i = this.mSwitchTop;
    int j = this.mTouchSlop;
    k = this.mSwitchLeft + k - this.mTouchSlop;
    int m = this.mThumbWidth;
    int n = this.mTempRect.left;
    int i1 = this.mTempRect.right;
    int i2 = this.mTouchSlop;
    int i3 = this.mSwitchBottom;
    int i4 = this.mTouchSlop;
    int i5 = b;
    if (paramFloat1 > k) {
      i5 = b;
      if (paramFloat1 < (m + k + n + i1 + i2)) {
        i5 = b;
        if (paramFloat2 > (i - j)) {
          i5 = b;
          if (paramFloat2 < (i3 + i4))
            i5 = 1; 
        } 
      } 
    } 
    return i5;
  }
  
  private Layout makeLayout(CharSequence paramCharSequence) {
    byte b;
    CharSequence charSequence = paramCharSequence;
    if (this.mSwitchTransformationMethod != null)
      charSequence = this.mSwitchTransformationMethod.getTransformation(paramCharSequence, this); 
    TextPaint textPaint = this.mTextPaint;
    if (charSequence != null) {
      b = (int)Math.ceil(Layout.getDesiredWidth(charSequence, this.mTextPaint));
    } else {
      b = 0;
    } 
    return new StaticLayout(charSequence, textPaint, b, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
  }
  
  private void setSwitchTypefaceByIndex(int paramInt1, int paramInt2) {
    Typeface typeface;
    switch (paramInt1) {
      default:
        typeface = null;
        break;
      case 3:
        typeface = Typeface.MONOSPACE;
        break;
      case 2:
        typeface = Typeface.SERIF;
        break;
      case 1:
        typeface = Typeface.SANS_SERIF;
        break;
    } 
    setSwitchTypeface(typeface, paramInt2);
  }
  
  private void stopDrag(MotionEvent paramMotionEvent) {
    this.mTouchMode = 0;
    int i = paramMotionEvent.getAction();
    boolean bool1 = true;
    if (i == 1 && isEnabled()) {
      i = 1;
    } else {
      i = 0;
    } 
    boolean bool2 = isChecked();
    if (i != 0) {
      this.mVelocityTracker.computeCurrentVelocity(1000);
      float f = this.mVelocityTracker.getXVelocity();
      if (Math.abs(f) > this.mMinFlingVelocity) {
        if (ViewUtils.isLayoutRtl(this) ? (f < 0.0F) : (f > 0.0F))
          bool1 = false; 
      } else {
        bool1 = getTargetCheckedState();
      } 
    } else {
      bool1 = bool2;
    } 
    if (bool1 != bool2)
      playSoundEffect(0); 
    setChecked(bool1);
    cancelSuperTouch(paramMotionEvent);
  }
  
  public void draw(Canvas paramCanvas) { // Byte code:
    //   0: aload_0
    //   1: getfield mTempRect : Landroid/graphics/Rect;
    //   4: astore #12
    //   6: aload_0
    //   7: getfield mSwitchLeft : I
    //   10: istore #5
    //   12: aload_0
    //   13: getfield mSwitchTop : I
    //   16: istore #8
    //   18: aload_0
    //   19: getfield mSwitchRight : I
    //   22: istore #6
    //   24: aload_0
    //   25: getfield mSwitchBottom : I
    //   28: istore #9
    //   30: aload_0
    //   31: invokespecial getThumbOffset : ()I
    //   34: iload #5
    //   36: iadd
    //   37: istore_3
    //   38: aload_0
    //   39: getfield mThumbDrawable : Landroid/graphics/drawable/Drawable;
    //   42: ifnull -> 57
    //   45: aload_0
    //   46: getfield mThumbDrawable : Landroid/graphics/drawable/Drawable;
    //   49: invokestatic getOpticalBounds : (Landroid/graphics/drawable/Drawable;)Landroid/graphics/Rect;
    //   52: astore #11
    //   54: goto -> 62
    //   57: getstatic android/support/v7/widget/DrawableUtils.INSETS_NONE : Landroid/graphics/Rect;
    //   60: astore #11
    //   62: iload_3
    //   63: istore_2
    //   64: aload_0
    //   65: getfield mTrackDrawable : Landroid/graphics/drawable/Drawable;
    //   68: ifnull -> 271
    //   71: aload_0
    //   72: getfield mTrackDrawable : Landroid/graphics/drawable/Drawable;
    //   75: aload #12
    //   77: invokevirtual getPadding : (Landroid/graphics/Rect;)Z
    //   80: pop
    //   81: iload_3
    //   82: aload #12
    //   84: getfield left : I
    //   87: iadd
    //   88: istore #10
    //   90: aload #11
    //   92: ifnull -> 235
    //   95: iload #5
    //   97: istore_2
    //   98: aload #11
    //   100: getfield left : I
    //   103: aload #12
    //   105: getfield left : I
    //   108: if_icmple -> 126
    //   111: iload #5
    //   113: aload #11
    //   115: getfield left : I
    //   118: aload #12
    //   120: getfield left : I
    //   123: isub
    //   124: iadd
    //   125: istore_2
    //   126: aload #11
    //   128: getfield top : I
    //   131: aload #12
    //   133: getfield top : I
    //   136: if_icmple -> 157
    //   139: aload #11
    //   141: getfield top : I
    //   144: aload #12
    //   146: getfield top : I
    //   149: isub
    //   150: iload #8
    //   152: iadd
    //   153: istore_3
    //   154: goto -> 160
    //   157: iload #8
    //   159: istore_3
    //   160: iload #6
    //   162: istore #4
    //   164: aload #11
    //   166: getfield right : I
    //   169: aload #12
    //   171: getfield right : I
    //   174: if_icmple -> 193
    //   177: iload #6
    //   179: aload #11
    //   181: getfield right : I
    //   184: aload #12
    //   186: getfield right : I
    //   189: isub
    //   190: isub
    //   191: istore #4
    //   193: iload_2
    //   194: istore #5
    //   196: iload #4
    //   198: istore #6
    //   200: iload_3
    //   201: istore #7
    //   203: aload #11
    //   205: getfield bottom : I
    //   208: aload #12
    //   210: getfield bottom : I
    //   213: if_icmple -> 239
    //   216: iload #9
    //   218: aload #11
    //   220: getfield bottom : I
    //   223: aload #12
    //   225: getfield bottom : I
    //   228: isub
    //   229: isub
    //   230: istore #7
    //   232: goto -> 255
    //   235: iload #8
    //   237: istore #7
    //   239: iload #9
    //   241: istore_2
    //   242: iload #7
    //   244: istore_3
    //   245: iload_2
    //   246: istore #7
    //   248: iload #6
    //   250: istore #4
    //   252: iload #5
    //   254: istore_2
    //   255: aload_0
    //   256: getfield mTrackDrawable : Landroid/graphics/drawable/Drawable;
    //   259: iload_2
    //   260: iload_3
    //   261: iload #4
    //   263: iload #7
    //   265: invokevirtual setBounds : (IIII)V
    //   268: iload #10
    //   270: istore_2
    //   271: aload_0
    //   272: getfield mThumbDrawable : Landroid/graphics/drawable/Drawable;
    //   275: ifnull -> 344
    //   278: aload_0
    //   279: getfield mThumbDrawable : Landroid/graphics/drawable/Drawable;
    //   282: aload #12
    //   284: invokevirtual getPadding : (Landroid/graphics/Rect;)Z
    //   287: pop
    //   288: iload_2
    //   289: aload #12
    //   291: getfield left : I
    //   294: isub
    //   295: istore_3
    //   296: iload_2
    //   297: aload_0
    //   298: getfield mThumbWidth : I
    //   301: iadd
    //   302: aload #12
    //   304: getfield right : I
    //   307: iadd
    //   308: istore_2
    //   309: aload_0
    //   310: getfield mThumbDrawable : Landroid/graphics/drawable/Drawable;
    //   313: iload_3
    //   314: iload #8
    //   316: iload_2
    //   317: iload #9
    //   319: invokevirtual setBounds : (IIII)V
    //   322: aload_0
    //   323: invokevirtual getBackground : ()Landroid/graphics/drawable/Drawable;
    //   326: astore #11
    //   328: aload #11
    //   330: ifnull -> 344
    //   333: aload #11
    //   335: iload_3
    //   336: iload #8
    //   338: iload_2
    //   339: iload #9
    //   341: invokestatic setHotspotBounds : (Landroid/graphics/drawable/Drawable;IIII)V
    //   344: aload_0
    //   345: aload_1
    //   346: invokespecial draw : (Landroid/graphics/Canvas;)V
    //   349: return }
  
  public void drawableHotspotChanged(float paramFloat1, float paramFloat2) {
    if (Build.VERSION.SDK_INT >= 21)
      super.drawableHotspotChanged(paramFloat1, paramFloat2); 
    if (this.mThumbDrawable != null)
      DrawableCompat.setHotspot(this.mThumbDrawable, paramFloat1, paramFloat2); 
    if (this.mTrackDrawable != null)
      DrawableCompat.setHotspot(this.mTrackDrawable, paramFloat1, paramFloat2); 
  }
  
  protected void drawableStateChanged() {
    super.drawableStateChanged();
    int[] arrayOfInt = getDrawableState();
    Drawable drawable = this.mThumbDrawable;
    byte b = 0;
    boolean bool1 = b;
    if (drawable != null) {
      bool1 = b;
      if (drawable.isStateful())
        bool1 = false | drawable.setState(arrayOfInt); 
    } 
    drawable = this.mTrackDrawable;
    boolean bool2 = bool1;
    if (drawable != null) {
      bool2 = bool1;
      if (drawable.isStateful())
        bool2 = bool1 | drawable.setState(arrayOfInt); 
    } 
    if (bool2)
      invalidate(); 
  }
  
  public int getCompoundPaddingLeft() {
    if (!ViewUtils.isLayoutRtl(this))
      return super.getCompoundPaddingLeft(); 
    int j = super.getCompoundPaddingLeft() + this.mSwitchWidth;
    int i = j;
    if (!TextUtils.isEmpty(getText()))
      i = j + this.mSwitchPadding; 
    return i;
  }
  
  public int getCompoundPaddingRight() {
    if (ViewUtils.isLayoutRtl(this))
      return super.getCompoundPaddingRight(); 
    int j = super.getCompoundPaddingRight() + this.mSwitchWidth;
    int i = j;
    if (!TextUtils.isEmpty(getText()))
      i = j + this.mSwitchPadding; 
    return i;
  }
  
  public boolean getShowText() { return this.mShowText; }
  
  public boolean getSplitTrack() { return this.mSplitTrack; }
  
  public int getSwitchMinWidth() { return this.mSwitchMinWidth; }
  
  public int getSwitchPadding() { return this.mSwitchPadding; }
  
  public CharSequence getTextOff() { return this.mTextOff; }
  
  public CharSequence getTextOn() { return this.mTextOn; }
  
  public Drawable getThumbDrawable() { return this.mThumbDrawable; }
  
  public int getThumbTextPadding() { return this.mThumbTextPadding; }
  
  @Nullable
  public ColorStateList getThumbTintList() { return this.mThumbTintList; }
  
  @Nullable
  public PorterDuff.Mode getThumbTintMode() { return this.mThumbTintMode; }
  
  public Drawable getTrackDrawable() { return this.mTrackDrawable; }
  
  @Nullable
  public ColorStateList getTrackTintList() { return this.mTrackTintList; }
  
  @Nullable
  public PorterDuff.Mode getTrackTintMode() { return this.mTrackTintMode; }
  
  public void jumpDrawablesToCurrentState() {
    super.jumpDrawablesToCurrentState();
    if (this.mThumbDrawable != null)
      this.mThumbDrawable.jumpToCurrentState(); 
    if (this.mTrackDrawable != null)
      this.mTrackDrawable.jumpToCurrentState(); 
    if (this.mPositionAnimator != null && this.mPositionAnimator.isStarted()) {
      this.mPositionAnimator.end();
      this.mPositionAnimator = null;
    } 
  }
  
  protected int[] onCreateDrawableState(int paramInt) {
    int[] arrayOfInt = super.onCreateDrawableState(paramInt + 1);
    if (isChecked())
      mergeDrawableStates(arrayOfInt, CHECKED_STATE_SET); 
    return arrayOfInt;
  }
  
  protected void onDraw(Canvas paramCanvas) {
    super.onDraw(paramCanvas);
    Layout layout = this.mTempRect;
    Drawable drawable2 = this.mTrackDrawable;
    if (drawable2 != null) {
      drawable2.getPadding(layout);
    } else {
      layout.setEmpty();
    } 
    int j = this.mSwitchTop;
    int k = this.mSwitchBottom;
    int m = layout.top;
    int n = layout.bottom;
    Drawable drawable1 = this.mThumbDrawable;
    if (drawable2 != null)
      if (this.mSplitTrack && drawable1 != null) {
        Rect rect = DrawableUtils.getOpticalBounds(drawable1);
        drawable1.copyBounds(layout);
        layout.left += rect.left;
        layout.right -= rect.right;
        int i1 = paramCanvas.save();
        paramCanvas.clipRect(layout, Region.Op.DIFFERENCE);
        drawable2.draw(paramCanvas);
        paramCanvas.restoreToCount(i1);
      } else {
        drawable2.draw(paramCanvas);
      }  
    int i = paramCanvas.save();
    if (drawable1 != null)
      drawable1.draw(paramCanvas); 
    if (getTargetCheckedState()) {
      Layout layout1 = this.mOnLayout;
    } else {
      layout = this.mOffLayout;
    } 
    if (layout != null) {
      int i1;
      int[] arrayOfInt = getDrawableState();
      if (this.mTextColors != null)
        this.mTextPaint.setColor(this.mTextColors.getColorForState(arrayOfInt, 0)); 
      this.mTextPaint.drawableState = arrayOfInt;
      if (drawable1 != null) {
        Rect rect = drawable1.getBounds();
        i1 = rect.left + rect.right;
      } else {
        i1 = getWidth();
      } 
      i1 /= 2;
      int i2 = layout.getWidth() / 2;
      j = (j + m + k - n) / 2;
      k = layout.getHeight() / 2;
      paramCanvas.translate((i1 - i2), (j - k));
      layout.draw(paramCanvas);
    } 
    paramCanvas.restoreToCount(i);
  }
  
  public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent) {
    super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
    paramAccessibilityEvent.setClassName("android.widget.Switch");
  }
  
  public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo) {
    CharSequence charSequence;
    super.onInitializeAccessibilityNodeInfo(paramAccessibilityNodeInfo);
    paramAccessibilityNodeInfo.setClassName("android.widget.Switch");
    if (isChecked()) {
      charSequence = this.mTextOn;
    } else {
      charSequence = this.mTextOff;
    } 
    if (!TextUtils.isEmpty(charSequence)) {
      CharSequence charSequence1 = paramAccessibilityNodeInfo.getText();
      if (TextUtils.isEmpty(charSequence1)) {
        paramAccessibilityNodeInfo.setText(charSequence);
        return;
      } 
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(charSequence1);
      stringBuilder.append(' ');
      stringBuilder.append(charSequence);
      paramAccessibilityNodeInfo.setText(stringBuilder);
    } 
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    Drawable drawable = this.mThumbDrawable;
    paramInt2 = 0;
    if (drawable != null) {
      Rect rect1 = this.mTempRect;
      if (this.mTrackDrawable != null) {
        this.mTrackDrawable.getPadding(rect1);
      } else {
        rect1.setEmpty();
      } 
      Rect rect2 = DrawableUtils.getOpticalBounds(this.mThumbDrawable);
      paramInt2 = Math.max(0, rect2.left - rect1.left);
      paramInt1 = Math.max(0, rect2.right - rect1.right);
    } else {
      paramInt1 = 0;
    } 
    if (ViewUtils.isLayoutRtl(this)) {
      paramInt3 = getPaddingLeft() + paramInt2;
      paramInt4 = this.mSwitchWidth + paramInt3 - paramInt2 - paramInt1;
    } else {
      paramInt4 = getWidth() - getPaddingRight() - paramInt1;
      paramInt3 = paramInt4 - this.mSwitchWidth + paramInt2 + paramInt1;
    } 
    paramInt1 = getGravity() & 0x70;
    if (paramInt1 != 16) {
      if (paramInt1 != 80) {
        paramInt1 = getPaddingTop();
        paramInt2 = this.mSwitchHeight + paramInt1;
      } else {
        paramInt2 = getHeight() - getPaddingBottom();
        paramInt1 = paramInt2 - this.mSwitchHeight;
      } 
    } else {
      paramInt1 = (getPaddingTop() + getHeight() - getPaddingBottom()) / 2 - this.mSwitchHeight / 2;
      paramInt2 = this.mSwitchHeight + paramInt1;
    } 
    this.mSwitchLeft = paramInt3;
    this.mSwitchTop = paramInt1;
    this.mSwitchBottom = paramInt2;
    this.mSwitchRight = paramInt4;
  }
  
  public void onMeasure(int paramInt1, int paramInt2) {
    int j;
    if (this.mShowText) {
      if (this.mOnLayout == null)
        this.mOnLayout = makeLayout(this.mTextOn); 
      if (this.mOffLayout == null)
        this.mOffLayout = makeLayout(this.mTextOff); 
    } 
    Rect rect = this.mTempRect;
    Drawable drawable = this.mThumbDrawable;
    int m = 0;
    if (drawable != null) {
      this.mThumbDrawable.getPadding(rect);
      j = this.mThumbDrawable.getIntrinsicWidth() - rect.left - rect.right;
      i = this.mThumbDrawable.getIntrinsicHeight();
    } else {
      j = 0;
      i = 0;
    } 
    if (this.mShowText) {
      k = Math.max(this.mOnLayout.getWidth(), this.mOffLayout.getWidth()) + this.mThumbTextPadding * 2;
    } else {
      k = 0;
    } 
    this.mThumbWidth = Math.max(k, j);
    if (this.mTrackDrawable != null) {
      this.mTrackDrawable.getPadding(rect);
      j = this.mTrackDrawable.getIntrinsicHeight();
    } else {
      rect.setEmpty();
      j = m;
    } 
    int i1 = rect.left;
    int n = rect.right;
    m = n;
    int k = i1;
    if (this.mThumbDrawable != null) {
      rect = DrawableUtils.getOpticalBounds(this.mThumbDrawable);
      k = Math.max(i1, rect.left);
      m = Math.max(n, rect.right);
    } 
    k = Math.max(this.mSwitchMinWidth, this.mThumbWidth * 2 + k + m);
    int i = Math.max(j, i);
    this.mSwitchWidth = k;
    this.mSwitchHeight = i;
    super.onMeasure(paramInt1, paramInt2);
    if (getMeasuredHeight() < i)
      setMeasuredDimension(getMeasuredWidthAndState(), i); 
  }
  
  public void onPopulateAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent) {
    CharSequence charSequence;
    super.onPopulateAccessibilityEvent(paramAccessibilityEvent);
    if (isChecked()) {
      charSequence = this.mTextOn;
    } else {
      charSequence = this.mTextOff;
    } 
    if (charSequence != null)
      paramAccessibilityEvent.getText().add(charSequence); 
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent) {
    int i;
    float f3;
    this.mVelocityTracker.addMovement(paramMotionEvent);
    switch (paramMotionEvent.getActionMasked()) {
      default:
        return super.onTouchEvent(paramMotionEvent);
      case 2:
        switch (this.mTouchMode) {
          case 2:
            f3 = paramMotionEvent.getX();
            i = getThumbScrollRange();
            f1 = f3 - this.mTouchX;
            if (i != 0) {
              f1 /= i;
            } else if (f1 > 0.0F) {
              f1 = 1.0F;
            } else {
              f1 = -1.0F;
            } 
            f2 = f1;
            if (ViewUtils.isLayoutRtl(this))
              f2 = -f1; 
            f1 = constrain(this.mThumbPosition + f2, 0.0F, 1.0F);
            if (f1 != this.mThumbPosition) {
              this.mTouchX = f3;
              setThumbPosition(f1);
            } 
            return true;
          case 1:
            f1 = paramMotionEvent.getX();
            f2 = paramMotionEvent.getY();
            if (Math.abs(f1 - this.mTouchX) > this.mTouchSlop || Math.abs(f2 - this.mTouchY) > this.mTouchSlop) {
              this.mTouchMode = 2;
              getParent().requestDisallowInterceptTouchEvent(true);
              this.mTouchX = f1;
              this.mTouchY = f2;
              return true;
            } 
            break;
        } 
      case 1:
      case 3:
        if (this.mTouchMode == 2) {
          stopDrag(paramMotionEvent);
          super.onTouchEvent(paramMotionEvent);
          return true;
        } 
        this.mTouchMode = 0;
        this.mVelocityTracker.clear();
      case 0:
        break;
    } 
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getY();
    if (isEnabled() && hitThumb(f1, f2)) {
      this.mTouchMode = 1;
      this.mTouchX = f1;
      this.mTouchY = f2;
    } 
  }
  
  public void setChecked(boolean paramBoolean) {
    float f;
    super.setChecked(paramBoolean);
    paramBoolean = isChecked();
    if (getWindowToken() != null && ViewCompat.isLaidOut(this)) {
      animateThumbToCheckedState(paramBoolean);
      return;
    } 
    cancelPositionAnimator();
    if (paramBoolean) {
      f = 1.0F;
    } else {
      f = 0.0F;
    } 
    setThumbPosition(f);
  }
  
  public void setShowText(boolean paramBoolean) {
    if (this.mShowText != paramBoolean) {
      this.mShowText = paramBoolean;
      requestLayout();
    } 
  }
  
  public void setSplitTrack(boolean paramBoolean) {
    this.mSplitTrack = paramBoolean;
    invalidate();
  }
  
  public void setSwitchMinWidth(int paramInt) {
    this.mSwitchMinWidth = paramInt;
    requestLayout();
  }
  
  public void setSwitchPadding(int paramInt) {
    this.mSwitchPadding = paramInt;
    requestLayout();
  }
  
  public void setSwitchTextAppearance(Context paramContext, int paramInt) {
    TintTypedArray tintTypedArray = TintTypedArray.obtainStyledAttributes(paramContext, paramInt, R.styleable.TextAppearance);
    ColorStateList colorStateList = tintTypedArray.getColorStateList(R.styleable.TextAppearance_android_textColor);
    if (colorStateList != null) {
      this.mTextColors = colorStateList;
    } else {
      this.mTextColors = getTextColors();
    } 
    paramInt = tintTypedArray.getDimensionPixelSize(R.styleable.TextAppearance_android_textSize, 0);
    if (paramInt != 0) {
      float f = paramInt;
      if (f != this.mTextPaint.getTextSize()) {
        this.mTextPaint.setTextSize(f);
        requestLayout();
      } 
    } 
    setSwitchTypefaceByIndex(tintTypedArray.getInt(R.styleable.TextAppearance_android_typeface, -1), tintTypedArray.getInt(R.styleable.TextAppearance_android_textStyle, -1));
    if (tintTypedArray.getBoolean(R.styleable.TextAppearance_textAllCaps, false)) {
      this.mSwitchTransformationMethod = new AllCapsTransformationMethod(getContext());
    } else {
      this.mSwitchTransformationMethod = null;
    } 
    tintTypedArray.recycle();
  }
  
  public void setSwitchTypeface(Typeface paramTypeface) {
    if ((this.mTextPaint.getTypeface() != null && !this.mTextPaint.getTypeface().equals(paramTypeface)) || (this.mTextPaint.getTypeface() == null && paramTypeface != null)) {
      this.mTextPaint.setTypeface(paramTypeface);
      requestLayout();
      invalidate();
    } 
  }
  
  public void setSwitchTypeface(Typeface paramTypeface, int paramInt) {
    TextPaint textPaint;
    float f = 0.0F;
    boolean bool = false;
    if (paramInt > 0) {
      boolean bool1;
      if (paramTypeface == null) {
        paramTypeface = Typeface.defaultFromStyle(paramInt);
      } else {
        paramTypeface = Typeface.create(paramTypeface, paramInt);
      } 
      setSwitchTypeface(paramTypeface);
      if (paramTypeface != null) {
        bool1 = paramTypeface.getStyle();
      } else {
        bool1 = false;
      } 
      paramInt = (bool1 ^ 0xFFFFFFFF) & paramInt;
      textPaint = this.mTextPaint;
      if ((paramInt & true) != 0)
        bool = true; 
      textPaint.setFakeBoldText(bool);
      textPaint = this.mTextPaint;
      if ((paramInt & 0x2) != 0)
        f = -0.25F; 
      textPaint.setTextSkewX(f);
      return;
    } 
    this.mTextPaint.setFakeBoldText(false);
    this.mTextPaint.setTextSkewX(0.0F);
    setSwitchTypeface(textPaint);
  }
  
  public void setTextOff(CharSequence paramCharSequence) {
    this.mTextOff = paramCharSequence;
    requestLayout();
  }
  
  public void setTextOn(CharSequence paramCharSequence) {
    this.mTextOn = paramCharSequence;
    requestLayout();
  }
  
  public void setThumbDrawable(Drawable paramDrawable) {
    if (this.mThumbDrawable != null)
      this.mThumbDrawable.setCallback(null); 
    this.mThumbDrawable = paramDrawable;
    if (paramDrawable != null)
      paramDrawable.setCallback(this); 
    requestLayout();
  }
  
  void setThumbPosition(float paramFloat) {
    this.mThumbPosition = paramFloat;
    invalidate();
  }
  
  public void setThumbResource(int paramInt) { setThumbDrawable(AppCompatResources.getDrawable(getContext(), paramInt)); }
  
  public void setThumbTextPadding(int paramInt) {
    this.mThumbTextPadding = paramInt;
    requestLayout();
  }
  
  public void setThumbTintList(@Nullable ColorStateList paramColorStateList) {
    this.mThumbTintList = paramColorStateList;
    this.mHasThumbTint = true;
    applyThumbTint();
  }
  
  public void setThumbTintMode(@Nullable PorterDuff.Mode paramMode) {
    this.mThumbTintMode = paramMode;
    this.mHasThumbTintMode = true;
    applyThumbTint();
  }
  
  public void setTrackDrawable(Drawable paramDrawable) {
    if (this.mTrackDrawable != null)
      this.mTrackDrawable.setCallback(null); 
    this.mTrackDrawable = paramDrawable;
    if (paramDrawable != null)
      paramDrawable.setCallback(this); 
    requestLayout();
  }
  
  public void setTrackResource(int paramInt) { setTrackDrawable(AppCompatResources.getDrawable(getContext(), paramInt)); }
  
  public void setTrackTintList(@Nullable ColorStateList paramColorStateList) {
    this.mTrackTintList = paramColorStateList;
    this.mHasTrackTint = true;
    applyTrackTint();
  }
  
  public void setTrackTintMode(@Nullable PorterDuff.Mode paramMode) {
    this.mTrackTintMode = paramMode;
    this.mHasTrackTintMode = true;
    applyTrackTint();
  }
  
  public void toggle() { setChecked(isChecked() ^ true); }
  
  protected boolean verifyDrawable(Drawable paramDrawable) { return (super.verifyDrawable(paramDrawable) || paramDrawable == this.mThumbDrawable || paramDrawable == this.mTrackDrawable); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/SwitchCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */