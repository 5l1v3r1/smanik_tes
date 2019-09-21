package android.support.design.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.annotation.VisibleForTesting;
import android.support.design.R;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.Space;
import android.support.v4.widget.TextViewCompat;
import android.support.v4.widget.ViewGroupUtils;
import android.support.v7.appcompat.R;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DrawableUtils;
import android.support.v7.widget.TintTypedArray;
import android.support.v7.widget.WithHint;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStructure;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TextInputLayout extends LinearLayout implements WithHint {
  private static final int ANIMATION_DURATION = 200;
  
  private static final int INVALID_MAX_LENGTH = -1;
  
  private static final String LOG_TAG = "TextInputLayout";
  
  private ValueAnimator mAnimator;
  
  final CollapsingTextHelper mCollapsingTextHelper = new CollapsingTextHelper(this);
  
  boolean mCounterEnabled;
  
  private int mCounterMaxLength;
  
  private int mCounterOverflowTextAppearance;
  
  private boolean mCounterOverflowed;
  
  private int mCounterTextAppearance;
  
  private TextView mCounterView;
  
  private ColorStateList mDefaultTextColor;
  
  EditText mEditText;
  
  private CharSequence mError;
  
  private boolean mErrorEnabled;
  
  private boolean mErrorShown;
  
  private int mErrorTextAppearance;
  
  TextView mErrorView;
  
  private ColorStateList mFocusedTextColor;
  
  private boolean mHasPasswordToggleTintList;
  
  private boolean mHasPasswordToggleTintMode;
  
  private boolean mHasReconstructedEditTextBackground;
  
  private CharSequence mHint;
  
  private boolean mHintAnimationEnabled;
  
  private boolean mHintEnabled;
  
  private boolean mHintExpanded;
  
  private boolean mInDrawableStateChanged;
  
  private LinearLayout mIndicatorArea;
  
  private int mIndicatorsAdded;
  
  private final FrameLayout mInputFrame;
  
  private Drawable mOriginalEditTextEndDrawable;
  
  private CharSequence mOriginalHint;
  
  private CharSequence mPasswordToggleContentDesc;
  
  private Drawable mPasswordToggleDrawable;
  
  private Drawable mPasswordToggleDummyDrawable;
  
  private boolean mPasswordToggleEnabled;
  
  private ColorStateList mPasswordToggleTintList;
  
  private PorterDuff.Mode mPasswordToggleTintMode;
  
  private CheckableImageButton mPasswordToggleView;
  
  private boolean mPasswordToggledVisible;
  
  private boolean mRestoringSavedState;
  
  private Paint mTmpPaint;
  
  private final Rect mTmpRect = new Rect();
  
  private Typeface mTypeface;
  
  public TextInputLayout(Context paramContext) { this(paramContext, null); }
  
  public TextInputLayout(Context paramContext, AttributeSet paramAttributeSet) { this(paramContext, paramAttributeSet, 0); }
  
  public TextInputLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
    super(paramContext, paramAttributeSet);
    ThemeUtils.checkAppCompatTheme(paramContext);
    setOrientation(1);
    setWillNotDraw(false);
    setAddStatesFromChildren(true);
    this.mInputFrame = new FrameLayout(paramContext);
    this.mInputFrame.setAddStatesFromChildren(true);
    addView(this.mInputFrame);
    this.mCollapsingTextHelper.setTextSizeInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
    this.mCollapsingTextHelper.setPositionInterpolator(new AccelerateInterpolator());
    this.mCollapsingTextHelper.setCollapsedTextGravity(8388659);
    TintTypedArray tintTypedArray = TintTypedArray.obtainStyledAttributes(paramContext, paramAttributeSet, R.styleable.TextInputLayout, paramInt, R.style.Widget_Design_TextInputLayout);
    this.mHintEnabled = tintTypedArray.getBoolean(R.styleable.TextInputLayout_hintEnabled, true);
    setHint(tintTypedArray.getText(R.styleable.TextInputLayout_android_hint));
    this.mHintAnimationEnabled = tintTypedArray.getBoolean(R.styleable.TextInputLayout_hintAnimationEnabled, true);
    if (tintTypedArray.hasValue(R.styleable.TextInputLayout_android_textColorHint)) {
      ColorStateList colorStateList = tintTypedArray.getColorStateList(R.styleable.TextInputLayout_android_textColorHint);
      this.mFocusedTextColor = colorStateList;
      this.mDefaultTextColor = colorStateList;
    } 
    if (tintTypedArray.getResourceId(R.styleable.TextInputLayout_hintTextAppearance, -1) != -1)
      setHintTextAppearance(tintTypedArray.getResourceId(R.styleable.TextInputLayout_hintTextAppearance, 0)); 
    this.mErrorTextAppearance = tintTypedArray.getResourceId(R.styleable.TextInputLayout_errorTextAppearance, 0);
    boolean bool1 = tintTypedArray.getBoolean(R.styleable.TextInputLayout_errorEnabled, false);
    boolean bool2 = tintTypedArray.getBoolean(R.styleable.TextInputLayout_counterEnabled, false);
    setCounterMaxLength(tintTypedArray.getInt(R.styleable.TextInputLayout_counterMaxLength, -1));
    this.mCounterTextAppearance = tintTypedArray.getResourceId(R.styleable.TextInputLayout_counterTextAppearance, 0);
    this.mCounterOverflowTextAppearance = tintTypedArray.getResourceId(R.styleable.TextInputLayout_counterOverflowTextAppearance, 0);
    this.mPasswordToggleEnabled = tintTypedArray.getBoolean(R.styleable.TextInputLayout_passwordToggleEnabled, false);
    this.mPasswordToggleDrawable = tintTypedArray.getDrawable(R.styleable.TextInputLayout_passwordToggleDrawable);
    this.mPasswordToggleContentDesc = tintTypedArray.getText(R.styleable.TextInputLayout_passwordToggleContentDescription);
    if (tintTypedArray.hasValue(R.styleable.TextInputLayout_passwordToggleTint)) {
      this.mHasPasswordToggleTintList = true;
      this.mPasswordToggleTintList = tintTypedArray.getColorStateList(R.styleable.TextInputLayout_passwordToggleTint);
    } 
    if (tintTypedArray.hasValue(R.styleable.TextInputLayout_passwordToggleTintMode)) {
      this.mHasPasswordToggleTintMode = true;
      this.mPasswordToggleTintMode = ViewUtils.parseTintMode(tintTypedArray.getInt(R.styleable.TextInputLayout_passwordToggleTintMode, -1), null);
    } 
    tintTypedArray.recycle();
    setErrorEnabled(bool1);
    setCounterEnabled(bool2);
    applyPasswordToggleTint();
    if (ViewCompat.getImportantForAccessibility(this) == 0)
      ViewCompat.setImportantForAccessibility(this, 1); 
    ViewCompat.setAccessibilityDelegate(this, new TextInputAccessibilityDelegate());
  }
  
  private void addIndicator(TextView paramTextView, int paramInt) {
    if (this.mIndicatorArea == null) {
      this.mIndicatorArea = new LinearLayout(getContext());
      this.mIndicatorArea.setOrientation(0);
      addView(this.mIndicatorArea, -1, -2);
      Space space = new Space(getContext());
      LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, 0, 1.0F);
      this.mIndicatorArea.addView(space, layoutParams);
      if (this.mEditText != null)
        adjustIndicatorPadding(); 
    } 
    this.mIndicatorArea.setVisibility(0);
    this.mIndicatorArea.addView(paramTextView, paramInt);
    this.mIndicatorsAdded++;
  }
  
  private void adjustIndicatorPadding() { ViewCompat.setPaddingRelative(this.mIndicatorArea, ViewCompat.getPaddingStart(this.mEditText), 0, ViewCompat.getPaddingEnd(this.mEditText), this.mEditText.getPaddingBottom()); }
  
  private void applyPasswordToggleTint() {
    if (this.mPasswordToggleDrawable != null && (this.mHasPasswordToggleTintList || this.mHasPasswordToggleTintMode)) {
      this.mPasswordToggleDrawable = DrawableCompat.wrap(this.mPasswordToggleDrawable).mutate();
      if (this.mHasPasswordToggleTintList)
        DrawableCompat.setTintList(this.mPasswordToggleDrawable, this.mPasswordToggleTintList); 
      if (this.mHasPasswordToggleTintMode)
        DrawableCompat.setTintMode(this.mPasswordToggleDrawable, this.mPasswordToggleTintMode); 
      if (this.mPasswordToggleView != null && this.mPasswordToggleView.getDrawable() != this.mPasswordToggleDrawable)
        this.mPasswordToggleView.setImageDrawable(this.mPasswordToggleDrawable); 
    } 
  }
  
  private static boolean arrayContains(int[] paramArrayOfInt, int paramInt) {
    int i = paramArrayOfInt.length;
    for (byte b = 0; b < i; b++) {
      if (paramArrayOfInt[b] == paramInt)
        return true; 
    } 
    return false;
  }
  
  private void collapseHint(boolean paramBoolean) {
    if (this.mAnimator != null && this.mAnimator.isRunning())
      this.mAnimator.cancel(); 
    if (paramBoolean && this.mHintAnimationEnabled) {
      animateToExpansionFraction(1.0F);
    } else {
      this.mCollapsingTextHelper.setExpansionFraction(1.0F);
    } 
    this.mHintExpanded = false;
  }
  
  private void ensureBackgroundDrawableStateWorkaround() {
    int i = Build.VERSION.SDK_INT;
    if (i != 21 && i != 22)
      return; 
    Drawable drawable = this.mEditText.getBackground();
    if (drawable == null)
      return; 
    if (!this.mHasReconstructedEditTextBackground) {
      Drawable drawable1 = drawable.getConstantState().newDrawable();
      if (drawable instanceof DrawableContainer)
        this.mHasReconstructedEditTextBackground = DrawableUtils.setContainerConstantState((DrawableContainer)drawable, drawable1.getConstantState()); 
      if (!this.mHasReconstructedEditTextBackground) {
        ViewCompat.setBackground(this.mEditText, drawable1);
        this.mHasReconstructedEditTextBackground = true;
      } 
    } 
  }
  
  private void expandHint(boolean paramBoolean) {
    if (this.mAnimator != null && this.mAnimator.isRunning())
      this.mAnimator.cancel(); 
    if (paramBoolean && this.mHintAnimationEnabled) {
      animateToExpansionFraction(0.0F);
    } else {
      this.mCollapsingTextHelper.setExpansionFraction(0.0F);
    } 
    this.mHintExpanded = true;
  }
  
  private boolean hasPasswordTransformation() { return (this.mEditText != null && this.mEditText.getTransformationMethod() instanceof PasswordTransformationMethod); }
  
  private void passwordVisibilityToggleRequested(boolean paramBoolean) {
    if (this.mPasswordToggleEnabled) {
      int i = this.mEditText.getSelectionEnd();
      if (hasPasswordTransformation()) {
        this.mEditText.setTransformationMethod(null);
        this.mPasswordToggledVisible = true;
      } else {
        this.mEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        this.mPasswordToggledVisible = false;
      } 
      this.mPasswordToggleView.setChecked(this.mPasswordToggledVisible);
      if (paramBoolean)
        this.mPasswordToggleView.jumpDrawablesToCurrentState(); 
      this.mEditText.setSelection(i);
    } 
  }
  
  private static void recursiveSetEnabled(ViewGroup paramViewGroup, boolean paramBoolean) {
    int i = paramViewGroup.getChildCount();
    for (byte b = 0; b < i; b++) {
      View view = paramViewGroup.getChildAt(b);
      view.setEnabled(paramBoolean);
      if (view instanceof ViewGroup)
        recursiveSetEnabled((ViewGroup)view, paramBoolean); 
    } 
  }
  
  private void removeIndicator(TextView paramTextView) {
    if (this.mIndicatorArea != null) {
      this.mIndicatorArea.removeView(paramTextView);
      int i = this.mIndicatorsAdded - 1;
      this.mIndicatorsAdded = i;
      if (i == 0)
        this.mIndicatorArea.setVisibility(8); 
    } 
  }
  
  private void setEditText(EditText paramEditText) {
    if (this.mEditText != null)
      throw new IllegalArgumentException("We already have an EditText, can only have one"); 
    if (!(paramEditText instanceof TextInputEditText))
      Log.i("TextInputLayout", "EditText added is not a TextInputEditText. Please switch to using that class instead."); 
    this.mEditText = paramEditText;
    if (!hasPasswordTransformation())
      this.mCollapsingTextHelper.setTypefaces(this.mEditText.getTypeface()); 
    this.mCollapsingTextHelper.setExpandedTextSize(this.mEditText.getTextSize());
    int i = this.mEditText.getGravity();
    this.mCollapsingTextHelper.setCollapsedTextGravity(i & 0xFFFFFF8F | 0x30);
    this.mCollapsingTextHelper.setExpandedTextGravity(i);
    this.mEditText.addTextChangedListener(new TextWatcher() {
          public void afterTextChanged(Editable param1Editable) {
            TextInputLayout.this.updateLabelState(TextInputLayout.this.mRestoringSavedState ^ true);
            if (TextInputLayout.this.mCounterEnabled)
              TextInputLayout.this.updateCounter(param1Editable.length()); 
          }
          
          public void beforeTextChanged(CharSequence param1CharSequence, int param1Int1, int param1Int2, int param1Int3) {}
          
          public void onTextChanged(CharSequence param1CharSequence, int param1Int1, int param1Int2, int param1Int3) {}
        });
    if (this.mDefaultTextColor == null)
      this.mDefaultTextColor = this.mEditText.getHintTextColors(); 
    if (this.mHintEnabled && TextUtils.isEmpty(this.mHint)) {
      this.mOriginalHint = this.mEditText.getHint();
      setHint(this.mOriginalHint);
      this.mEditText.setHint(null);
    } 
    if (this.mCounterView != null)
      updateCounter(this.mEditText.getText().length()); 
    if (this.mIndicatorArea != null)
      adjustIndicatorPadding(); 
    updatePasswordToggleView();
    updateLabelState(false, true);
  }
  
  private void setError(@Nullable final CharSequence error, boolean paramBoolean) {
    this.mError = paramCharSequence;
    if (!this.mErrorEnabled) {
      if (TextUtils.isEmpty(paramCharSequence))
        return; 
      setErrorEnabled(true);
    } 
    this.mErrorShown = TextUtils.isEmpty(paramCharSequence) ^ true;
    this.mErrorView.animate().cancel();
    if (this.mErrorShown) {
      this.mErrorView.setText(paramCharSequence);
      this.mErrorView.setVisibility(0);
      if (paramBoolean) {
        if (this.mErrorView.getAlpha() == 1.0F)
          this.mErrorView.setAlpha(0.0F); 
        this.mErrorView.animate().alpha(1.0F).setDuration(200L).setInterpolator(AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR).setListener(new AnimatorListenerAdapter() {
              public void onAnimationStart(Animator param1Animator) { TextInputLayout.this.mErrorView.setVisibility(0); }
            }).start();
      } else {
        this.mErrorView.setAlpha(1.0F);
      } 
    } else if (this.mErrorView.getVisibility() == 0) {
      if (paramBoolean) {
        this.mErrorView.animate().alpha(0.0F).setDuration(200L).setInterpolator(AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR).setListener(new AnimatorListenerAdapter() {
              public void onAnimationEnd(Animator param1Animator) {
                TextInputLayout.this.mErrorView.setText(error);
                TextInputLayout.this.mErrorView.setVisibility(4);
              }
            }).start();
      } else {
        this.mErrorView.setText(paramCharSequence);
        this.mErrorView.setVisibility(4);
      } 
    } 
    updateEditTextBackground();
    updateLabelState(paramBoolean);
  }
  
  private void setHintInternal(CharSequence paramCharSequence) {
    this.mHint = paramCharSequence;
    this.mCollapsingTextHelper.setText(paramCharSequence);
  }
  
  private boolean shouldShowPasswordIcon() { return (this.mPasswordToggleEnabled && (hasPasswordTransformation() || this.mPasswordToggledVisible)); }
  
  private void updateEditTextBackground() {
    if (this.mEditText == null)
      return; 
    Drawable drawable2 = this.mEditText.getBackground();
    if (drawable2 == null)
      return; 
    ensureBackgroundDrawableStateWorkaround();
    Drawable drawable1 = drawable2;
    if (DrawableUtils.canSafelyMutateDrawable(drawable2))
      drawable1 = drawable2.mutate(); 
    if (this.mErrorShown && this.mErrorView != null) {
      drawable1.setColorFilter(AppCompatDrawableManager.getPorterDuffColorFilter(this.mErrorView.getCurrentTextColor(), PorterDuff.Mode.SRC_IN));
      return;
    } 
    if (this.mCounterOverflowed && this.mCounterView != null) {
      drawable1.setColorFilter(AppCompatDrawableManager.getPorterDuffColorFilter(this.mCounterView.getCurrentTextColor(), PorterDuff.Mode.SRC_IN));
      return;
    } 
    DrawableCompat.clearColorFilter(drawable1);
    this.mEditText.refreshDrawableState();
  }
  
  private void updateInputLayoutMargins() {
    boolean bool;
    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)this.mInputFrame.getLayoutParams();
    if (this.mHintEnabled) {
      if (this.mTmpPaint == null)
        this.mTmpPaint = new Paint(); 
      this.mTmpPaint.setTypeface(this.mCollapsingTextHelper.getCollapsedTypeface());
      this.mTmpPaint.setTextSize(this.mCollapsingTextHelper.getCollapsedTextSize());
      bool = (int)-this.mTmpPaint.ascent();
    } else {
      bool = false;
    } 
    if (bool != layoutParams.topMargin) {
      layoutParams.topMargin = bool;
      this.mInputFrame.requestLayout();
    } 
  }
  
  private void updatePasswordToggleView() {
    if (this.mEditText == null)
      return; 
    if (shouldShowPasswordIcon()) {
      if (this.mPasswordToggleView == null) {
        this.mPasswordToggleView = (CheckableImageButton)LayoutInflater.from(getContext()).inflate(R.layout.design_text_input_password_icon, this.mInputFrame, false);
        this.mPasswordToggleView.setImageDrawable(this.mPasswordToggleDrawable);
        this.mPasswordToggleView.setContentDescription(this.mPasswordToggleContentDesc);
        this.mInputFrame.addView(this.mPasswordToggleView);
        this.mPasswordToggleView.setOnClickListener(new View.OnClickListener() {
              public void onClick(View param1View) { TextInputLayout.this.passwordVisibilityToggleRequested(false); }
            });
      } 
      if (this.mEditText != null && ViewCompat.getMinimumHeight(this.mEditText) <= 0)
        this.mEditText.setMinimumHeight(ViewCompat.getMinimumHeight(this.mPasswordToggleView)); 
      this.mPasswordToggleView.setVisibility(0);
      this.mPasswordToggleView.setChecked(this.mPasswordToggledVisible);
      if (this.mPasswordToggleDummyDrawable == null)
        this.mPasswordToggleDummyDrawable = new ColorDrawable(); 
      this.mPasswordToggleDummyDrawable.setBounds(0, 0, this.mPasswordToggleView.getMeasuredWidth(), 1);
      Drawable[] arrayOfDrawable = TextViewCompat.getCompoundDrawablesRelative(this.mEditText);
      if (arrayOfDrawable[2] != this.mPasswordToggleDummyDrawable)
        this.mOriginalEditTextEndDrawable = arrayOfDrawable[2]; 
      TextViewCompat.setCompoundDrawablesRelative(this.mEditText, arrayOfDrawable[0], arrayOfDrawable[1], this.mPasswordToggleDummyDrawable, arrayOfDrawable[3]);
      this.mPasswordToggleView.setPadding(this.mEditText.getPaddingLeft(), this.mEditText.getPaddingTop(), this.mEditText.getPaddingRight(), this.mEditText.getPaddingBottom());
      return;
    } 
    if (this.mPasswordToggleView != null && this.mPasswordToggleView.getVisibility() == 0)
      this.mPasswordToggleView.setVisibility(8); 
    if (this.mPasswordToggleDummyDrawable != null) {
      Drawable[] arrayOfDrawable = TextViewCompat.getCompoundDrawablesRelative(this.mEditText);
      if (arrayOfDrawable[2] == this.mPasswordToggleDummyDrawable) {
        TextViewCompat.setCompoundDrawablesRelative(this.mEditText, arrayOfDrawable[0], arrayOfDrawable[1], this.mOriginalEditTextEndDrawable, arrayOfDrawable[3]);
        this.mPasswordToggleDummyDrawable = null;
      } 
    } 
  }
  
  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams) {
    if (paramView instanceof EditText) {
      FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(paramLayoutParams);
      layoutParams.gravity = layoutParams.gravity & 0xFFFFFF8F | 0x10;
      this.mInputFrame.addView(paramView, layoutParams);
      this.mInputFrame.setLayoutParams(paramLayoutParams);
      updateInputLayoutMargins();
      setEditText((EditText)paramView);
      return;
    } 
    super.addView(paramView, paramInt, paramLayoutParams);
  }
  
  @VisibleForTesting
  void animateToExpansionFraction(float paramFloat) {
    if (this.mCollapsingTextHelper.getExpansionFraction() == paramFloat)
      return; 
    if (this.mAnimator == null) {
      this.mAnimator = new ValueAnimator();
      this.mAnimator.setInterpolator(AnimationUtils.LINEAR_INTERPOLATOR);
      this.mAnimator.setDuration(200L);
      this.mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator param1ValueAnimator) { TextInputLayout.this.mCollapsingTextHelper.setExpansionFraction(((Float)param1ValueAnimator.getAnimatedValue()).floatValue()); }
          });
    } 
    this.mAnimator.setFloatValues(new float[] { this.mCollapsingTextHelper.getExpansionFraction(), paramFloat });
    this.mAnimator.start();
  }
  
  public void dispatchProvideAutofillStructure(ViewStructure paramViewStructure, int paramInt) {
    if (this.mOriginalHint == null || this.mEditText == null) {
      super.dispatchProvideAutofillStructure(paramViewStructure, paramInt);
      return;
    } 
    charSequence = this.mEditText.getHint();
    this.mEditText.setHint(this.mOriginalHint);
    try {
      super.dispatchProvideAutofillStructure(paramViewStructure, paramInt);
      return;
    } finally {
      this.mEditText.setHint(charSequence);
    } 
  }
  
  protected void dispatchRestoreInstanceState(SparseArray<Parcelable> paramSparseArray) {
    this.mRestoringSavedState = true;
    super.dispatchRestoreInstanceState(paramSparseArray);
    this.mRestoringSavedState = false;
  }
  
  public void draw(Canvas paramCanvas) {
    super.draw(paramCanvas);
    if (this.mHintEnabled)
      this.mCollapsingTextHelper.draw(paramCanvas); 
  }
  
  protected void drawableStateChanged() {
    boolean bool;
    if (this.mInDrawableStateChanged)
      return; 
    boolean bool1 = true;
    this.mInDrawableStateChanged = true;
    super.drawableStateChanged();
    int[] arrayOfInt = getDrawableState();
    if (!ViewCompat.isLaidOut(this) || !isEnabled())
      bool1 = false; 
    updateLabelState(bool1);
    updateEditTextBackground();
    if (this.mCollapsingTextHelper != null) {
      bool = this.mCollapsingTextHelper.setState(arrayOfInt) | false;
    } else {
      bool = false;
    } 
    if (bool)
      invalidate(); 
    this.mInDrawableStateChanged = false;
  }
  
  public int getCounterMaxLength() { return this.mCounterMaxLength; }
  
  @Nullable
  public EditText getEditText() { return this.mEditText; }
  
  @Nullable
  public CharSequence getError() { return this.mErrorEnabled ? this.mError : null; }
  
  @Nullable
  public CharSequence getHint() { return this.mHintEnabled ? this.mHint : null; }
  
  @Nullable
  public CharSequence getPasswordVisibilityToggleContentDescription() { return this.mPasswordToggleContentDesc; }
  
  @Nullable
  public Drawable getPasswordVisibilityToggleDrawable() { return this.mPasswordToggleDrawable; }
  
  @NonNull
  public Typeface getTypeface() { return this.mTypeface; }
  
  public boolean isCounterEnabled() { return this.mCounterEnabled; }
  
  public boolean isErrorEnabled() { return this.mErrorEnabled; }
  
  public boolean isHintAnimationEnabled() { return this.mHintAnimationEnabled; }
  
  public boolean isHintEnabled() { return this.mHintEnabled; }
  
  @VisibleForTesting
  final boolean isHintExpanded() { return this.mHintExpanded; }
  
  public boolean isPasswordVisibilityToggleEnabled() { return this.mPasswordToggleEnabled; }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    if (this.mHintEnabled && this.mEditText != null) {
      Rect rect = this.mTmpRect;
      ViewGroupUtils.getDescendantRect(this, this.mEditText, rect);
      paramInt1 = rect.left + this.mEditText.getCompoundPaddingLeft();
      paramInt3 = rect.right - this.mEditText.getCompoundPaddingRight();
      this.mCollapsingTextHelper.setExpandedBounds(paramInt1, rect.top + this.mEditText.getCompoundPaddingTop(), paramInt3, rect.bottom - this.mEditText.getCompoundPaddingBottom());
      this.mCollapsingTextHelper.setCollapsedBounds(paramInt1, getPaddingTop(), paramInt3, paramInt4 - paramInt2 - getPaddingBottom());
      this.mCollapsingTextHelper.recalculate();
    } 
  }
  
  protected void onMeasure(int paramInt1, int paramInt2) {
    updatePasswordToggleView();
    super.onMeasure(paramInt1, paramInt2);
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable) {
    if (!(paramParcelable instanceof SavedState)) {
      super.onRestoreInstanceState(paramParcelable);
      return;
    } 
    SavedState savedState = (SavedState)paramParcelable;
    super.onRestoreInstanceState(savedState.getSuperState());
    setError(savedState.error);
    if (savedState.isPasswordToggledVisible)
      passwordVisibilityToggleRequested(true); 
    requestLayout();
  }
  
  public Parcelable onSaveInstanceState() {
    SavedState savedState = new SavedState(super.onSaveInstanceState());
    if (this.mErrorShown)
      savedState.error = getError(); 
    savedState.isPasswordToggledVisible = this.mPasswordToggledVisible;
    return savedState;
  }
  
  public void setCounterEnabled(boolean paramBoolean) {
    if (this.mCounterEnabled != paramBoolean) {
      if (paramBoolean) {
        this.mCounterView = new AppCompatTextView(getContext());
        this.mCounterView.setId(R.id.textinput_counter);
        if (this.mTypeface != null)
          this.mCounterView.setTypeface(this.mTypeface); 
        this.mCounterView.setMaxLines(1);
        try {
          TextViewCompat.setTextAppearance(this.mCounterView, this.mCounterTextAppearance);
        } catch (Exception exception) {
          TextViewCompat.setTextAppearance(this.mCounterView, R.style.TextAppearance_AppCompat_Caption);
          this.mCounterView.setTextColor(ContextCompat.getColor(getContext(), R.color.error_color_material));
        } 
        addIndicator(this.mCounterView, -1);
        if (this.mEditText == null) {
          updateCounter(0);
        } else {
          updateCounter(this.mEditText.getText().length());
        } 
      } else {
        removeIndicator(this.mCounterView);
        this.mCounterView = null;
      } 
      this.mCounterEnabled = paramBoolean;
    } 
  }
  
  public void setCounterMaxLength(int paramInt) {
    if (this.mCounterMaxLength != paramInt) {
      if (paramInt > 0) {
        this.mCounterMaxLength = paramInt;
      } else {
        this.mCounterMaxLength = -1;
      } 
      if (this.mCounterEnabled) {
        if (this.mEditText == null) {
          paramInt = 0;
        } else {
          paramInt = this.mEditText.getText().length();
        } 
        updateCounter(paramInt);
      } 
    } 
  }
  
  public void setEnabled(boolean paramBoolean) {
    recursiveSetEnabled(this, paramBoolean);
    super.setEnabled(paramBoolean);
  }
  
  public void setError(@Nullable CharSequence paramCharSequence) {
    boolean bool;
    if (ViewCompat.isLaidOut(this) && isEnabled() && (this.mErrorView == null || !TextUtils.equals(this.mErrorView.getText(), paramCharSequence))) {
      bool = true;
    } else {
      bool = false;
    } 
    setError(paramCharSequence, bool);
  }
  
  public void setErrorEnabled(boolean paramBoolean) { // Byte code:
    //   0: aload_0
    //   1: getfield mErrorEnabled : Z
    //   4: iload_1
    //   5: if_icmpeq -> 205
    //   8: aload_0
    //   9: getfield mErrorView : Landroid/widget/TextView;
    //   12: ifnull -> 25
    //   15: aload_0
    //   16: getfield mErrorView : Landroid/widget/TextView;
    //   19: invokevirtual animate : ()Landroid/view/ViewPropertyAnimator;
    //   22: invokevirtual cancel : ()V
    //   25: iload_1
    //   26: ifeq -> 178
    //   29: aload_0
    //   30: new android/support/v7/widget/AppCompatTextView
    //   33: dup
    //   34: aload_0
    //   35: invokevirtual getContext : ()Landroid/content/Context;
    //   38: invokespecial <init> : (Landroid/content/Context;)V
    //   41: putfield mErrorView : Landroid/widget/TextView;
    //   44: aload_0
    //   45: getfield mErrorView : Landroid/widget/TextView;
    //   48: getstatic android/support/design/R$id.textinput_error : I
    //   51: invokevirtual setId : (I)V
    //   54: aload_0
    //   55: getfield mTypeface : Landroid/graphics/Typeface;
    //   58: ifnull -> 72
    //   61: aload_0
    //   62: getfield mErrorView : Landroid/widget/TextView;
    //   65: aload_0
    //   66: getfield mTypeface : Landroid/graphics/Typeface;
    //   69: invokevirtual setTypeface : (Landroid/graphics/Typeface;)V
    //   72: aload_0
    //   73: getfield mErrorView : Landroid/widget/TextView;
    //   76: aload_0
    //   77: getfield mErrorTextAppearance : I
    //   80: invokestatic setTextAppearance : (Landroid/widget/TextView;I)V
    //   83: getstatic android/os/Build$VERSION.SDK_INT : I
    //   86: bipush #23
    //   88: if_icmplt -> 112
    //   91: aload_0
    //   92: getfield mErrorView : Landroid/widget/TextView;
    //   95: invokevirtual getTextColors : ()Landroid/content/res/ColorStateList;
    //   98: invokevirtual getDefaultColor : ()I
    //   101: istore_2
    //   102: iload_2
    //   103: ldc_w -65281
    //   106: if_icmpne -> 112
    //   109: goto -> 117
    //   112: iconst_0
    //   113: istore_2
    //   114: goto -> 119
    //   117: iconst_1
    //   118: istore_2
    //   119: iload_2
    //   120: ifeq -> 150
    //   123: aload_0
    //   124: getfield mErrorView : Landroid/widget/TextView;
    //   127: getstatic android/support/v7/appcompat/R$style.TextAppearance_AppCompat_Caption : I
    //   130: invokestatic setTextAppearance : (Landroid/widget/TextView;I)V
    //   133: aload_0
    //   134: getfield mErrorView : Landroid/widget/TextView;
    //   137: aload_0
    //   138: invokevirtual getContext : ()Landroid/content/Context;
    //   141: getstatic android/support/v7/appcompat/R$color.error_color_material : I
    //   144: invokestatic getColor : (Landroid/content/Context;I)I
    //   147: invokevirtual setTextColor : (I)V
    //   150: aload_0
    //   151: getfield mErrorView : Landroid/widget/TextView;
    //   154: iconst_4
    //   155: invokevirtual setVisibility : (I)V
    //   158: aload_0
    //   159: getfield mErrorView : Landroid/widget/TextView;
    //   162: iconst_1
    //   163: invokestatic setAccessibilityLiveRegion : (Landroid/view/View;I)V
    //   166: aload_0
    //   167: aload_0
    //   168: getfield mErrorView : Landroid/widget/TextView;
    //   171: iconst_0
    //   172: invokespecial addIndicator : (Landroid/widget/TextView;I)V
    //   175: goto -> 200
    //   178: aload_0
    //   179: iconst_0
    //   180: putfield mErrorShown : Z
    //   183: aload_0
    //   184: invokespecial updateEditTextBackground : ()V
    //   187: aload_0
    //   188: aload_0
    //   189: getfield mErrorView : Landroid/widget/TextView;
    //   192: invokespecial removeIndicator : (Landroid/widget/TextView;)V
    //   195: aload_0
    //   196: aconst_null
    //   197: putfield mErrorView : Landroid/widget/TextView;
    //   200: aload_0
    //   201: iload_1
    //   202: putfield mErrorEnabled : Z
    //   205: return
    //   206: astore_3
    //   207: goto -> 117
    // Exception table:
    //   from	to	target	type
    //   72	102	206	java/lang/Exception }
  
  public void setErrorTextAppearance(@StyleRes int paramInt) {
    this.mErrorTextAppearance = paramInt;
    if (this.mErrorView != null)
      TextViewCompat.setTextAppearance(this.mErrorView, paramInt); 
  }
  
  public void setHint(@Nullable CharSequence paramCharSequence) {
    if (this.mHintEnabled) {
      setHintInternal(paramCharSequence);
      sendAccessibilityEvent(2048);
    } 
  }
  
  public void setHintAnimationEnabled(boolean paramBoolean) { this.mHintAnimationEnabled = paramBoolean; }
  
  public void setHintEnabled(boolean paramBoolean) {
    if (paramBoolean != this.mHintEnabled) {
      this.mHintEnabled = paramBoolean;
      CharSequence charSequence = this.mEditText.getHint();
      if (!this.mHintEnabled) {
        if (!TextUtils.isEmpty(this.mHint) && TextUtils.isEmpty(charSequence))
          this.mEditText.setHint(this.mHint); 
        setHintInternal(null);
      } else if (!TextUtils.isEmpty(charSequence)) {
        if (TextUtils.isEmpty(this.mHint))
          setHint(charSequence); 
        this.mEditText.setHint(null);
      } 
      if (this.mEditText != null)
        updateInputLayoutMargins(); 
    } 
  }
  
  public void setHintTextAppearance(@StyleRes int paramInt) {
    this.mCollapsingTextHelper.setCollapsedTextAppearance(paramInt);
    this.mFocusedTextColor = this.mCollapsingTextHelper.getCollapsedTextColor();
    if (this.mEditText != null) {
      updateLabelState(false);
      updateInputLayoutMargins();
    } 
  }
  
  public void setPasswordVisibilityToggleContentDescription(@StringRes int paramInt) {
    CharSequence charSequence;
    if (paramInt != 0) {
      charSequence = getResources().getText(paramInt);
    } else {
      charSequence = null;
    } 
    setPasswordVisibilityToggleContentDescription(charSequence);
  }
  
  public void setPasswordVisibilityToggleContentDescription(@Nullable CharSequence paramCharSequence) {
    this.mPasswordToggleContentDesc = paramCharSequence;
    if (this.mPasswordToggleView != null)
      this.mPasswordToggleView.setContentDescription(paramCharSequence); 
  }
  
  public void setPasswordVisibilityToggleDrawable(@DrawableRes int paramInt) {
    Drawable drawable;
    if (paramInt != 0) {
      drawable = AppCompatResources.getDrawable(getContext(), paramInt);
    } else {
      drawable = null;
    } 
    setPasswordVisibilityToggleDrawable(drawable);
  }
  
  public void setPasswordVisibilityToggleDrawable(@Nullable Drawable paramDrawable) {
    this.mPasswordToggleDrawable = paramDrawable;
    if (this.mPasswordToggleView != null)
      this.mPasswordToggleView.setImageDrawable(paramDrawable); 
  }
  
  public void setPasswordVisibilityToggleEnabled(boolean paramBoolean) {
    if (this.mPasswordToggleEnabled != paramBoolean) {
      this.mPasswordToggleEnabled = paramBoolean;
      if (!paramBoolean && this.mPasswordToggledVisible && this.mEditText != null)
        this.mEditText.setTransformationMethod(PasswordTransformationMethod.getInstance()); 
      this.mPasswordToggledVisible = false;
      updatePasswordToggleView();
    } 
  }
  
  public void setPasswordVisibilityToggleTintList(@Nullable ColorStateList paramColorStateList) {
    this.mPasswordToggleTintList = paramColorStateList;
    this.mHasPasswordToggleTintList = true;
    applyPasswordToggleTint();
  }
  
  public void setPasswordVisibilityToggleTintMode(@Nullable PorterDuff.Mode paramMode) {
    this.mPasswordToggleTintMode = paramMode;
    this.mHasPasswordToggleTintMode = true;
    applyPasswordToggleTint();
  }
  
  public void setTypeface(@Nullable Typeface paramTypeface) {
    if ((this.mTypeface != null && !this.mTypeface.equals(paramTypeface)) || (this.mTypeface == null && paramTypeface != null)) {
      this.mTypeface = paramTypeface;
      this.mCollapsingTextHelper.setTypefaces(paramTypeface);
      if (this.mCounterView != null)
        this.mCounterView.setTypeface(paramTypeface); 
      if (this.mErrorView != null)
        this.mErrorView.setTypeface(paramTypeface); 
    } 
  }
  
  void updateCounter(int paramInt) {
    boolean bool = this.mCounterOverflowed;
    if (this.mCounterMaxLength == -1) {
      this.mCounterView.setText(String.valueOf(paramInt));
      this.mCounterOverflowed = false;
    } else {
      boolean bool1;
      if (paramInt > this.mCounterMaxLength) {
        bool1 = true;
      } else {
        bool1 = false;
      } 
      this.mCounterOverflowed = bool1;
      if (bool != this.mCounterOverflowed) {
        int i;
        TextView textView = this.mCounterView;
        if (this.mCounterOverflowed) {
          i = this.mCounterOverflowTextAppearance;
        } else {
          i = this.mCounterTextAppearance;
        } 
        TextViewCompat.setTextAppearance(textView, i);
      } 
      this.mCounterView.setText(getContext().getString(R.string.character_counter_pattern, new Object[] { Integer.valueOf(paramInt), Integer.valueOf(this.mCounterMaxLength) }));
    } 
    if (this.mEditText != null && bool != this.mCounterOverflowed) {
      updateLabelState(false);
      updateEditTextBackground();
    } 
  }
  
  void updateLabelState(boolean paramBoolean) { updateLabelState(paramBoolean, false); }
  
  void updateLabelState(boolean paramBoolean1, boolean paramBoolean2) {
    boolean bool;
    boolean bool1 = isEnabled();
    if (this.mEditText != null && !TextUtils.isEmpty(this.mEditText.getText())) {
      bool = true;
    } else {
      bool = false;
    } 
    boolean bool2 = arrayContains(getDrawableState(), 16842908);
    boolean bool3 = TextUtils.isEmpty(getError());
    if (this.mDefaultTextColor != null)
      this.mCollapsingTextHelper.setExpandedTextColor(this.mDefaultTextColor); 
    if (bool1 && this.mCounterOverflowed && this.mCounterView != null) {
      this.mCollapsingTextHelper.setCollapsedTextColor(this.mCounterView.getTextColors());
    } else if (bool1 && bool2 && this.mFocusedTextColor != null) {
      this.mCollapsingTextHelper.setCollapsedTextColor(this.mFocusedTextColor);
    } else if (this.mDefaultTextColor != null) {
      this.mCollapsingTextHelper.setCollapsedTextColor(this.mDefaultTextColor);
    } 
    if (bool || (isEnabled() && (bool2 || true ^ bool3))) {
      if (paramBoolean2 || this.mHintExpanded)
        collapseHint(paramBoolean1); 
      return;
    } 
    if (paramBoolean2 || !this.mHintExpanded) {
      expandHint(paramBoolean1);
      return;
    } 
  }
  
  static class SavedState extends AbsSavedState {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() {
        public TextInputLayout.SavedState createFromParcel(Parcel param2Parcel) { return new TextInputLayout.SavedState(param2Parcel, null); }
        
        public TextInputLayout.SavedState createFromParcel(Parcel param2Parcel, ClassLoader param2ClassLoader) { return new TextInputLayout.SavedState(param2Parcel, param2ClassLoader); }
        
        public TextInputLayout.SavedState[] newArray(int param2Int) { return new TextInputLayout.SavedState[param2Int]; }
      };
    
    CharSequence error;
    
    boolean isPasswordToggledVisible;
    
    SavedState(Parcel param1Parcel, ClassLoader param1ClassLoader) {
      super(param1Parcel, param1ClassLoader);
      this.error = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(param1Parcel);
      int i = param1Parcel.readInt();
      boolean bool = true;
      if (i != 1)
        bool = false; 
      this.isPasswordToggledVisible = bool;
    }
    
    SavedState(Parcelable param1Parcelable) { super(param1Parcelable); }
    
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("TextInputLayout.SavedState{");
      stringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
      stringBuilder.append(" error=");
      stringBuilder.append(this.error);
      stringBuilder.append("}");
      return stringBuilder.toString();
    }
    
    public void writeToParcel(Parcel param1Parcel, int param1Int) { throw new RuntimeException("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.provideAs(TypeTransformer.java:780)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.e1expr(TypeTransformer.java:496)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:713)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.enexpr(TypeTransformer.java:698)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:719)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.s1stmt(TypeTransformer.java:810)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.sxStmt(TypeTransformer.java:840)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:206)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n"); }
  }
  
  static final class null extends Object implements Parcelable.ClassLoaderCreator<SavedState> {
    public TextInputLayout.SavedState createFromParcel(Parcel param1Parcel) { return new TextInputLayout.SavedState(param1Parcel, null); }
    
    public TextInputLayout.SavedState createFromParcel(Parcel param1Parcel, ClassLoader param1ClassLoader) { return new TextInputLayout.SavedState(param1Parcel, param1ClassLoader); }
    
    public TextInputLayout.SavedState[] newArray(int param1Int) { return new TextInputLayout.SavedState[param1Int]; }
  }
  
  private class TextInputAccessibilityDelegate extends AccessibilityDelegateCompat {
    public void onInitializeAccessibilityEvent(View param1View, AccessibilityEvent param1AccessibilityEvent) {
      super.onInitializeAccessibilityEvent(param1View, param1AccessibilityEvent);
      param1AccessibilityEvent.setClassName(TextInputLayout.class.getSimpleName());
    }
    
    public void onInitializeAccessibilityNodeInfo(View param1View, AccessibilityNodeInfoCompat param1AccessibilityNodeInfoCompat) {
      super.onInitializeAccessibilityNodeInfo(param1View, param1AccessibilityNodeInfoCompat);
      param1AccessibilityNodeInfoCompat.setClassName(TextInputLayout.class.getSimpleName());
      CharSequence charSequence = TextInputLayout.this.mCollapsingTextHelper.getText();
      if (!TextUtils.isEmpty(charSequence))
        param1AccessibilityNodeInfoCompat.setText(charSequence); 
      if (TextInputLayout.this.mEditText != null)
        param1AccessibilityNodeInfoCompat.setLabelFor(TextInputLayout.this.mEditText); 
      if (TextInputLayout.this.mErrorView != null) {
        charSequence = TextInputLayout.this.mErrorView.getText();
      } else {
        charSequence = null;
      } 
      if (!TextUtils.isEmpty(charSequence)) {
        param1AccessibilityNodeInfoCompat.setContentInvalid(true);
        param1AccessibilityNodeInfoCompat.setError(charSequence);
      } 
    }
    
    public void onPopulateAccessibilityEvent(View param1View, AccessibilityEvent param1AccessibilityEvent) {
      super.onPopulateAccessibilityEvent(param1View, param1AccessibilityEvent);
      CharSequence charSequence = TextInputLayout.this.mCollapsingTextHelper.getText();
      if (!TextUtils.isEmpty(charSequence))
        param1AccessibilityEvent.getText().add(charSequence); 
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/design/widget/TextInputLayout.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */