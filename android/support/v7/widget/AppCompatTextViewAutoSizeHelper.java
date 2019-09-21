package android.support.v7.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v7.appcompat.R;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

class AppCompatTextViewAutoSizeHelper {
  private static final int DEFAULT_AUTO_SIZE_GRANULARITY_IN_PX = 1;
  
  private static final int DEFAULT_AUTO_SIZE_MAX_TEXT_SIZE_IN_SP = 112;
  
  private static final int DEFAULT_AUTO_SIZE_MIN_TEXT_SIZE_IN_SP = 12;
  
  private static final String TAG = "ACTVAutoSizeHelper";
  
  private static final RectF TEMP_RECTF = new RectF();
  
  static final float UNSET_AUTO_SIZE_UNIFORM_CONFIGURATION_VALUE = -1.0F;
  
  private static final int VERY_WIDE = 1048576;
  
  private static ConcurrentHashMap<String, Method> sTextViewMethodByNameCache = new ConcurrentHashMap();
  
  private float mAutoSizeMaxTextSizeInPx = -1.0F;
  
  private float mAutoSizeMinTextSizeInPx = -1.0F;
  
  private float mAutoSizeStepGranularityInPx = -1.0F;
  
  private int[] mAutoSizeTextSizesInPx = new int[0];
  
  private int mAutoSizeTextType = 0;
  
  private final Context mContext;
  
  private boolean mHasPresetAutoSizeValues = false;
  
  private boolean mNeedsAutoSizeText = false;
  
  private TextPaint mTempTextPaint;
  
  private final TextView mTextView;
  
  AppCompatTextViewAutoSizeHelper(TextView paramTextView) {
    this.mTextView = paramTextView;
    this.mContext = this.mTextView.getContext();
  }
  
  private int[] cleanupAutoSizePresetSizes(int[] paramArrayOfInt) {
    int i = paramArrayOfInt.length;
    if (i == 0)
      return paramArrayOfInt; 
    Arrays.sort(paramArrayOfInt);
    ArrayList arrayList = new ArrayList();
    byte b2 = 0;
    byte b1;
    for (b1 = 0; b1 < i; b1++) {
      int j = paramArrayOfInt[b1];
      if (j > 0 && Collections.binarySearch(arrayList, Integer.valueOf(j)) < 0)
        arrayList.add(Integer.valueOf(j)); 
    } 
    if (i == arrayList.size())
      return paramArrayOfInt; 
    i = arrayList.size();
    paramArrayOfInt = new int[i];
    for (b1 = b2; b1 < i; b1++)
      paramArrayOfInt[b1] = ((Integer)arrayList.get(b1)).intValue(); 
    return paramArrayOfInt;
  }
  
  private void clearAutoSizeConfiguration() {
    this.mAutoSizeTextType = 0;
    this.mAutoSizeMinTextSizeInPx = -1.0F;
    this.mAutoSizeMaxTextSizeInPx = -1.0F;
    this.mAutoSizeStepGranularityInPx = -1.0F;
    this.mAutoSizeTextSizesInPx = new int[0];
    this.mNeedsAutoSizeText = false;
  }
  
  @RequiresApi(23)
  private StaticLayout createStaticLayoutForMeasuring(CharSequence paramCharSequence, Layout.Alignment paramAlignment, int paramInt1, int paramInt2) {
    TextDirectionHeuristic textDirectionHeuristic = (TextDirectionHeuristic)invokeAndReturnWithDefault(this.mTextView, "getTextDirectionHeuristic", TextDirectionHeuristics.FIRSTSTRONG_LTR);
    StaticLayout.Builder builder = StaticLayout.Builder.obtain(paramCharSequence, 0, paramCharSequence.length(), this.mTempTextPaint, paramInt1).setAlignment(paramAlignment).setLineSpacing(this.mTextView.getLineSpacingExtra(), this.mTextView.getLineSpacingMultiplier()).setIncludePad(this.mTextView.getIncludeFontPadding()).setBreakStrategy(this.mTextView.getBreakStrategy()).setHyphenationFrequency(this.mTextView.getHyphenationFrequency());
    paramInt1 = paramInt2;
    if (paramInt2 == -1)
      paramInt1 = Integer.MAX_VALUE; 
    return builder.setMaxLines(paramInt1).setTextDirection(textDirectionHeuristic).build();
  }
  
  private StaticLayout createStaticLayoutForMeasuringPre23(CharSequence paramCharSequence, Layout.Alignment paramAlignment, int paramInt) {
    boolean bool;
    float f2;
    float f1;
    if (Build.VERSION.SDK_INT >= 16) {
      f1 = this.mTextView.getLineSpacingMultiplier();
      f2 = this.mTextView.getLineSpacingExtra();
      bool = this.mTextView.getIncludeFontPadding();
    } else {
      f1 = ((Float)invokeAndReturnWithDefault(this.mTextView, "getLineSpacingMultiplier", Float.valueOf(1.0F))).floatValue();
      f2 = ((Float)invokeAndReturnWithDefault(this.mTextView, "getLineSpacingExtra", Float.valueOf(0.0F))).floatValue();
      bool = ((Boolean)invokeAndReturnWithDefault(this.mTextView, "getIncludeFontPadding", Boolean.valueOf(true))).booleanValue();
    } 
    return new StaticLayout(paramCharSequence, this.mTempTextPaint, paramInt, paramAlignment, f1, f2, bool);
  }
  
  private int findLargestTextSizeWhichFits(RectF paramRectF) {
    int i = this.mAutoSizeTextSizesInPx.length;
    if (i == 0)
      throw new IllegalStateException("No available text sizes to choose from."); 
    int k = i - 1;
    i = 1;
    int j = 0;
    while (i <= k) {
      int m = (i + k) / 2;
      if (suggestedSizeFitsInSpace(this.mAutoSizeTextSizesInPx[m], paramRectF)) {
        j = i;
        i = m + 1;
        continue;
      } 
      j = m - 1;
      k = j;
    } 
    return this.mAutoSizeTextSizesInPx[j];
  }
  
  @Nullable
  private Method getTextViewMethod(@NonNull String paramString) {
    try {
      Method method2 = (Method)sTextViewMethodByNameCache.get(paramString);
      Method method1 = method2;
      if (method2 == null) {
        method2 = TextView.class.getDeclaredMethod(paramString, new Class[0]);
        method1 = method2;
        if (method2 != null) {
          method2.setAccessible(true);
          sTextViewMethodByNameCache.put(paramString, method2);
          method1 = method2;
        } 
      } 
      return method1;
    } catch (Exception exception) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Failed to retrieve TextView#");
      stringBuilder.append(paramString);
      stringBuilder.append("() method");
      Log.w("ACTVAutoSizeHelper", stringBuilder.toString(), exception);
      return null;
    } 
  }
  
  private <T> T invokeAndReturnWithDefault(@NonNull Object paramObject, @NonNull String paramString, @NonNull T paramT) {
    try {
      return (T)getTextViewMethod(paramString).invoke(paramObject, new Object[0]);
    } catch (Exception paramObject) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Failed to invoke TextView#");
      stringBuilder.append(paramString);
      stringBuilder.append("() method");
      Log.w("ACTVAutoSizeHelper", stringBuilder.toString(), paramObject);
      return paramT;
    } finally {}
    throw paramObject;
  }
  
  private void setRawTextSize(float paramFloat) {
    if (paramFloat != this.mTextView.getPaint().getTextSize()) {
      boolean bool;
      this.mTextView.getPaint().setTextSize(paramFloat);
      if (Build.VERSION.SDK_INT >= 18) {
        bool = this.mTextView.isInLayout();
      } else {
        bool = false;
      } 
      if (this.mTextView.getLayout() != null) {
        this.mNeedsAutoSizeText = false;
        try {
          Method method = getTextViewMethod("nullLayouts");
          if (method != null)
            method.invoke(this.mTextView, new Object[0]); 
        } catch (Exception exception) {
          Log.w("ACTVAutoSizeHelper", "Failed to invoke TextView#nullLayouts() method", exception);
        } 
        if (!bool) {
          this.mTextView.requestLayout();
        } else {
          this.mTextView.forceLayout();
        } 
        this.mTextView.invalidate();
      } 
    } 
  }
  
  private boolean setupAutoSizeText() {
    boolean bool = supportsAutoSizeText();
    byte b = 0;
    if (bool && this.mAutoSizeTextType == 1) {
      if (!this.mHasPresetAutoSizeValues || this.mAutoSizeTextSizesInPx.length == 0) {
        float f = Math.round(this.mAutoSizeMinTextSizeInPx);
        byte b1 = 1;
        while (Math.round(this.mAutoSizeStepGranularityInPx + f) <= Math.round(this.mAutoSizeMaxTextSizeInPx)) {
          b1++;
          f += this.mAutoSizeStepGranularityInPx;
        } 
        int[] arrayOfInt = new int[b1];
        f = this.mAutoSizeMinTextSizeInPx;
        while (b < b1) {
          arrayOfInt[b] = Math.round(f);
          f += this.mAutoSizeStepGranularityInPx;
          b++;
        } 
        this.mAutoSizeTextSizesInPx = cleanupAutoSizePresetSizes(arrayOfInt);
      } 
      this.mNeedsAutoSizeText = true;
    } else {
      this.mNeedsAutoSizeText = false;
    } 
    return this.mNeedsAutoSizeText;
  }
  
  private void setupAutoSizeUniformPresetSizes(TypedArray paramTypedArray) {
    int i = paramTypedArray.length();
    int[] arrayOfInt = new int[i];
    if (i > 0) {
      for (byte b = 0; b < i; b++)
        arrayOfInt[b] = paramTypedArray.getDimensionPixelSize(b, -1); 
      this.mAutoSizeTextSizesInPx = cleanupAutoSizePresetSizes(arrayOfInt);
      setupAutoSizeUniformPresetSizesConfiguration();
    } 
  }
  
  private boolean setupAutoSizeUniformPresetSizesConfiguration() {
    boolean bool;
    int i = this.mAutoSizeTextSizesInPx.length;
    if (i > 0) {
      bool = true;
    } else {
      bool = false;
    } 
    this.mHasPresetAutoSizeValues = bool;
    if (this.mHasPresetAutoSizeValues) {
      this.mAutoSizeTextType = 1;
      this.mAutoSizeMinTextSizeInPx = this.mAutoSizeTextSizesInPx[0];
      this.mAutoSizeMaxTextSizeInPx = this.mAutoSizeTextSizesInPx[i - 1];
      this.mAutoSizeStepGranularityInPx = -1.0F;
    } 
    return this.mHasPresetAutoSizeValues;
  }
  
  private boolean suggestedSizeFitsInSpace(int paramInt, RectF paramRectF) {
    byte b;
    CharSequence charSequence2 = this.mTextView.getText();
    TransformationMethod transformationMethod = this.mTextView.getTransformationMethod();
    CharSequence charSequence1 = charSequence2;
    if (transformationMethod != null) {
      CharSequence charSequence = transformationMethod.getTransformation(charSequence2, this.mTextView);
      charSequence1 = charSequence2;
      if (charSequence != null)
        charSequence1 = charSequence; 
    } 
    if (Build.VERSION.SDK_INT >= 16) {
      b = this.mTextView.getMaxLines();
    } else {
      b = -1;
    } 
    if (this.mTempTextPaint == null) {
      this.mTempTextPaint = new TextPaint();
    } else {
      this.mTempTextPaint.reset();
    } 
    this.mTempTextPaint.set(this.mTextView.getPaint());
    this.mTempTextPaint.setTextSize(paramInt);
    StaticLayout staticLayout = (Layout.Alignment)invokeAndReturnWithDefault(this.mTextView, "getLayoutAlignment", Layout.Alignment.ALIGN_NORMAL);
    if (Build.VERSION.SDK_INT >= 23) {
      staticLayout = createStaticLayoutForMeasuring(charSequence1, staticLayout, Math.round(paramRectF.right), b);
    } else {
      staticLayout = createStaticLayoutForMeasuringPre23(charSequence1, staticLayout, Math.round(paramRectF.right));
    } 
    return (b != -1 && (staticLayout.getLineCount() > b || staticLayout.getLineEnd(staticLayout.getLineCount() - 1) != charSequence1.length())) ? false : (!(staticLayout.getHeight() > paramRectF.bottom));
  }
  
  private boolean supportsAutoSizeText() { return !(this.mTextView instanceof AppCompatEditText); }
  
  private void validateAndSetAutoSizeTextTypeUniformConfiguration(float paramFloat1, float paramFloat2, float paramFloat3) throws IllegalArgumentException {
    if (paramFloat1 <= 0.0F) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Minimum auto-size text size (");
      stringBuilder.append(paramFloat1);
      stringBuilder.append("px) is less or equal to (0px)");
      throw new IllegalArgumentException(stringBuilder.toString());
    } 
    if (paramFloat2 <= paramFloat1) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Maximum auto-size text size (");
      stringBuilder.append(paramFloat2);
      stringBuilder.append("px) is less or equal to minimum auto-size ");
      stringBuilder.append("text size (");
      stringBuilder.append(paramFloat1);
      stringBuilder.append("px)");
      throw new IllegalArgumentException(stringBuilder.toString());
    } 
    if (paramFloat3 <= 0.0F) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("The auto-size step granularity (");
      stringBuilder.append(paramFloat3);
      stringBuilder.append("px) is less or equal to (0px)");
      throw new IllegalArgumentException(stringBuilder.toString());
    } 
    this.mAutoSizeTextType = 1;
    this.mAutoSizeMinTextSizeInPx = paramFloat1;
    this.mAutoSizeMaxTextSizeInPx = paramFloat2;
    this.mAutoSizeStepGranularityInPx = paramFloat3;
    this.mHasPresetAutoSizeValues = false;
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  void autoSizeText() {
    if (!isAutoSizeEnabled())
      return; 
    if (this.mNeedsAutoSizeText)
      if (this.mTextView.getMeasuredHeight() > 0) {
        int i;
        if (this.mTextView.getMeasuredWidth() <= 0)
          return; 
        if (((Boolean)invokeAndReturnWithDefault(this.mTextView, "getHorizontallyScrolling", Boolean.valueOf(false))).booleanValue()) {
          i = 1048576;
        } else {
          i = this.mTextView.getMeasuredWidth() - this.mTextView.getTotalPaddingLeft() - this.mTextView.getTotalPaddingRight();
        } 
        int j = this.mTextView.getHeight() - this.mTextView.getCompoundPaddingBottom() - this.mTextView.getCompoundPaddingTop();
        if (i > 0) {
          if (j <= 0)
            return; 
          synchronized (TEMP_RECTF) {
            TEMP_RECTF.setEmpty();
            TEMP_RECTF.right = i;
            TEMP_RECTF.bottom = j;
            float f = findLargestTextSizeWhichFits(TEMP_RECTF);
            if (f != this.mTextView.getTextSize())
              setTextSizeInternal(0, f); 
          } 
        } else {
          return;
        } 
      } else {
        return;
      }  
    this.mNeedsAutoSizeText = true;
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  int getAutoSizeMaxTextSize() { return Math.round(this.mAutoSizeMaxTextSizeInPx); }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  int getAutoSizeMinTextSize() { return Math.round(this.mAutoSizeMinTextSizeInPx); }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  int getAutoSizeStepGranularity() { return Math.round(this.mAutoSizeStepGranularityInPx); }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  int[] getAutoSizeTextAvailableSizes() { return this.mAutoSizeTextSizesInPx; }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  int getAutoSizeTextType() { return this.mAutoSizeTextType; }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  boolean isAutoSizeEnabled() { return (supportsAutoSizeText() && this.mAutoSizeTextType != 0); }
  
  void loadFromAttributes(AttributeSet paramAttributeSet, int paramInt) {
    float f3;
    float f2;
    float f1;
    TypedArray typedArray = this.mContext.obtainStyledAttributes(paramAttributeSet, R.styleable.AppCompatTextView, paramInt, 0);
    if (typedArray.hasValue(R.styleable.AppCompatTextView_autoSizeTextType))
      this.mAutoSizeTextType = typedArray.getInt(R.styleable.AppCompatTextView_autoSizeTextType, 0); 
    if (typedArray.hasValue(R.styleable.AppCompatTextView_autoSizeStepGranularity)) {
      f1 = typedArray.getDimension(R.styleable.AppCompatTextView_autoSizeStepGranularity, -1.0F);
    } else {
      f1 = -1.0F;
    } 
    if (typedArray.hasValue(R.styleable.AppCompatTextView_autoSizeMinTextSize)) {
      f2 = typedArray.getDimension(R.styleable.AppCompatTextView_autoSizeMinTextSize, -1.0F);
    } else {
      f2 = -1.0F;
    } 
    if (typedArray.hasValue(R.styleable.AppCompatTextView_autoSizeMaxTextSize)) {
      f3 = typedArray.getDimension(R.styleable.AppCompatTextView_autoSizeMaxTextSize, -1.0F);
    } else {
      f3 = -1.0F;
    } 
    if (typedArray.hasValue(R.styleable.AppCompatTextView_autoSizePresetSizes)) {
      paramInt = typedArray.getResourceId(R.styleable.AppCompatTextView_autoSizePresetSizes, 0);
      if (paramInt > 0) {
        TypedArray typedArray1 = typedArray.getResources().obtainTypedArray(paramInt);
        setupAutoSizeUniformPresetSizes(typedArray1);
        typedArray1.recycle();
      } 
    } 
    typedArray.recycle();
    if (supportsAutoSizeText()) {
      if (this.mAutoSizeTextType == 1) {
        if (!this.mHasPresetAutoSizeValues) {
          DisplayMetrics displayMetrics = this.mContext.getResources().getDisplayMetrics();
          float f = f2;
          if (f2 == -1.0F)
            f = TypedValue.applyDimension(2, 12.0F, displayMetrics); 
          f2 = f3;
          if (f3 == -1.0F)
            f2 = TypedValue.applyDimension(2, 112.0F, displayMetrics); 
          f3 = f1;
          if (f1 == -1.0F)
            f3 = 1.0F; 
          validateAndSetAutoSizeTextTypeUniformConfiguration(f, f2, f3);
        } 
        setupAutoSizeText();
        return;
      } 
    } else {
      this.mAutoSizeTextType = 0;
    } 
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  void setAutoSizeTextTypeUniformWithConfiguration(int paramInt1, int paramInt2, int paramInt3, int paramInt4) throws IllegalArgumentException {
    if (supportsAutoSizeText()) {
      DisplayMetrics displayMetrics = this.mContext.getResources().getDisplayMetrics();
      validateAndSetAutoSizeTextTypeUniformConfiguration(TypedValue.applyDimension(paramInt4, paramInt1, displayMetrics), TypedValue.applyDimension(paramInt4, paramInt2, displayMetrics), TypedValue.applyDimension(paramInt4, paramInt3, displayMetrics));
      if (setupAutoSizeText())
        autoSizeText(); 
    } 
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  void setAutoSizeTextTypeUniformWithPresetSizes(@NonNull int[] paramArrayOfInt, int paramInt) throws IllegalArgumentException {
    if (supportsAutoSizeText()) {
      int i = paramArrayOfInt.length;
      byte b = 0;
      if (i > 0) {
        int[] arrayOfInt1;
        int[] arrayOfInt2 = new int[i];
        if (paramInt == 0) {
          arrayOfInt1 = Arrays.copyOf(paramArrayOfInt, i);
        } else {
          DisplayMetrics displayMetrics = this.mContext.getResources().getDisplayMetrics();
          while (true) {
            arrayOfInt1 = arrayOfInt2;
            if (b < i) {
              arrayOfInt2[b] = Math.round(TypedValue.applyDimension(paramInt, paramArrayOfInt[b], displayMetrics));
              b++;
              continue;
            } 
            break;
          } 
        } 
        this.mAutoSizeTextSizesInPx = cleanupAutoSizePresetSizes(arrayOfInt1);
        if (!setupAutoSizeUniformPresetSizesConfiguration()) {
          StringBuilder stringBuilder = new StringBuilder();
          stringBuilder.append("None of the preset sizes is valid: ");
          stringBuilder.append(Arrays.toString(paramArrayOfInt));
          throw new IllegalArgumentException(stringBuilder.toString());
        } 
      } else {
        this.mHasPresetAutoSizeValues = false;
      } 
      if (setupAutoSizeText())
        autoSizeText(); 
    } 
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  void setAutoSizeTextTypeWithDefaults(int paramInt) {
    if (supportsAutoSizeText()) {
      StringBuilder stringBuilder;
      DisplayMetrics displayMetrics;
      switch (paramInt) {
        default:
          stringBuilder = new StringBuilder();
          stringBuilder.append("Unknown auto-size text type: ");
          stringBuilder.append(paramInt);
          throw new IllegalArgumentException(stringBuilder.toString());
        case 1:
          displayMetrics = this.mContext.getResources().getDisplayMetrics();
          validateAndSetAutoSizeTextTypeUniformConfiguration(TypedValue.applyDimension(2, 12.0F, displayMetrics), TypedValue.applyDimension(2, 112.0F, displayMetrics), 1.0F);
          if (setupAutoSizeText()) {
            autoSizeText();
            return;
          } 
          return;
        case 0:
          break;
      } 
      clearAutoSizeConfiguration();
    } 
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  void setTextSizeInternal(int paramInt, float paramFloat) {
    Resources resources;
    if (this.mContext == null) {
      resources = Resources.getSystem();
    } else {
      resources = this.mContext.getResources();
    } 
    setRawTextSize(TypedValue.applyDimension(paramInt, paramFloat, resources.getDisplayMetrics()));
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/AppCompatTextViewAutoSizeHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */