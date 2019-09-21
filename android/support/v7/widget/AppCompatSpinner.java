package android.support.v7.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.view.TintableBackgroundView;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.view.menu.ShowableListMenu;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.ThemedSpinnerAdapter;

public class AppCompatSpinner extends Spinner implements TintableBackgroundView {
  private static final int[] ATTRS_ANDROID_SPINNERMODE = { 16843505 };
  
  private static final int MAX_ITEMS_MEASURED = 15;
  
  private static final int MODE_DIALOG = 0;
  
  private static final int MODE_DROPDOWN = 1;
  
  private static final int MODE_THEME = -1;
  
  private static final String TAG = "AppCompatSpinner";
  
  private final AppCompatBackgroundHelper mBackgroundTintHelper;
  
  private int mDropDownWidth;
  
  private ForwardingListener mForwardingListener;
  
  private DropdownPopup mPopup;
  
  private final Context mPopupContext;
  
  private final boolean mPopupSet;
  
  private SpinnerAdapter mTempAdapter;
  
  private final Rect mTempRect;
  
  public AppCompatSpinner(Context paramContext) { this(paramContext, null); }
  
  public AppCompatSpinner(Context paramContext, int paramInt) { this(paramContext, null, R.attr.spinnerStyle, paramInt); }
  
  public AppCompatSpinner(Context paramContext, AttributeSet paramAttributeSet) { this(paramContext, paramAttributeSet, R.attr.spinnerStyle); }
  
  public AppCompatSpinner(Context paramContext, AttributeSet paramAttributeSet, int paramInt) { this(paramContext, paramAttributeSet, paramInt, -1); }
  
  public AppCompatSpinner(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2) { this(paramContext, paramAttributeSet, paramInt1, paramInt2, null); }
  
  public AppCompatSpinner(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2, Resources.Theme paramTheme) { // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: aload_2
    //   3: iload_3
    //   4: invokespecial <init> : (Landroid/content/Context;Landroid/util/AttributeSet;I)V
    //   7: aload_0
    //   8: new android/graphics/Rect
    //   11: dup
    //   12: invokespecial <init> : ()V
    //   15: putfield mTempRect : Landroid/graphics/Rect;
    //   18: aload_1
    //   19: aload_2
    //   20: getstatic android/support/v7/appcompat/R$styleable.Spinner : [I
    //   23: iload_3
    //   24: iconst_0
    //   25: invokestatic obtainStyledAttributes : (Landroid/content/Context;Landroid/util/AttributeSet;[III)Landroid/support/v7/widget/TintTypedArray;
    //   28: astore #10
    //   30: aload_0
    //   31: new android/support/v7/widget/AppCompatBackgroundHelper
    //   34: dup
    //   35: aload_0
    //   36: invokespecial <init> : (Landroid/view/View;)V
    //   39: putfield mBackgroundTintHelper : Landroid/support/v7/widget/AppCompatBackgroundHelper;
    //   42: aload #5
    //   44: ifnull -> 64
    //   47: aload_0
    //   48: new android/support/v7/view/ContextThemeWrapper
    //   51: dup
    //   52: aload_1
    //   53: aload #5
    //   55: invokespecial <init> : (Landroid/content/Context;Landroid/content/res/Resources$Theme;)V
    //   58: putfield mPopupContext : Landroid/content/Context;
    //   61: goto -> 120
    //   64: aload #10
    //   66: getstatic android/support/v7/appcompat/R$styleable.Spinner_popupTheme : I
    //   69: iconst_0
    //   70: invokevirtual getResourceId : (II)I
    //   73: istore #6
    //   75: iload #6
    //   77: ifeq -> 97
    //   80: aload_0
    //   81: new android/support/v7/view/ContextThemeWrapper
    //   84: dup
    //   85: aload_1
    //   86: iload #6
    //   88: invokespecial <init> : (Landroid/content/Context;I)V
    //   91: putfield mPopupContext : Landroid/content/Context;
    //   94: goto -> 120
    //   97: getstatic android/os/Build$VERSION.SDK_INT : I
    //   100: bipush #23
    //   102: if_icmpge -> 111
    //   105: aload_1
    //   106: astore #5
    //   108: goto -> 114
    //   111: aconst_null
    //   112: astore #5
    //   114: aload_0
    //   115: aload #5
    //   117: putfield mPopupContext : Landroid/content/Context;
    //   120: aload_0
    //   121: getfield mPopupContext : Landroid/content/Context;
    //   124: ifnull -> 363
    //   127: iload #4
    //   129: istore #7
    //   131: iload #4
    //   133: iconst_m1
    //   134: if_icmpne -> 260
    //   137: aload_1
    //   138: aload_2
    //   139: getstatic android/support/v7/widget/AppCompatSpinner.ATTRS_ANDROID_SPINNERMODE : [I
    //   142: iload_3
    //   143: iconst_0
    //   144: invokevirtual obtainStyledAttributes : (Landroid/util/AttributeSet;[III)Landroid/content/res/TypedArray;
    //   147: astore #5
    //   149: iload #4
    //   151: istore #6
    //   153: aload #5
    //   155: astore #8
    //   157: aload #5
    //   159: iconst_0
    //   160: invokevirtual hasValue : (I)Z
    //   163: ifeq -> 179
    //   166: aload #5
    //   168: astore #8
    //   170: aload #5
    //   172: iconst_0
    //   173: iconst_0
    //   174: invokevirtual getInt : (II)I
    //   177: istore #6
    //   179: iload #6
    //   181: istore #7
    //   183: aload #5
    //   185: ifnull -> 260
    //   188: iload #6
    //   190: istore #4
    //   192: aload #5
    //   194: invokevirtual recycle : ()V
    //   197: iload #4
    //   199: istore #7
    //   201: goto -> 260
    //   204: astore #9
    //   206: goto -> 221
    //   209: astore_1
    //   210: aconst_null
    //   211: astore #8
    //   213: goto -> 248
    //   216: astore #9
    //   218: aconst_null
    //   219: astore #5
    //   221: aload #5
    //   223: astore #8
    //   225: ldc 'AppCompatSpinner'
    //   227: ldc 'Could not read android:spinnerMode'
    //   229: aload #9
    //   231: invokestatic i : (Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   234: pop
    //   235: iload #4
    //   237: istore #7
    //   239: aload #5
    //   241: ifnull -> 260
    //   244: goto -> 192
    //   247: astore_1
    //   248: aload #8
    //   250: ifnull -> 258
    //   253: aload #8
    //   255: invokevirtual recycle : ()V
    //   258: aload_1
    //   259: athrow
    //   260: iload #7
    //   262: iconst_1
    //   263: if_icmpne -> 363
    //   266: new android/support/v7/widget/AppCompatSpinner$DropdownPopup
    //   269: dup
    //   270: aload_0
    //   271: aload_0
    //   272: getfield mPopupContext : Landroid/content/Context;
    //   275: aload_2
    //   276: iload_3
    //   277: invokespecial <init> : (Landroid/support/v7/widget/AppCompatSpinner;Landroid/content/Context;Landroid/util/AttributeSet;I)V
    //   280: astore #5
    //   282: aload_0
    //   283: getfield mPopupContext : Landroid/content/Context;
    //   286: aload_2
    //   287: getstatic android/support/v7/appcompat/R$styleable.Spinner : [I
    //   290: iload_3
    //   291: iconst_0
    //   292: invokestatic obtainStyledAttributes : (Landroid/content/Context;Landroid/util/AttributeSet;[III)Landroid/support/v7/widget/TintTypedArray;
    //   295: astore #8
    //   297: aload_0
    //   298: aload #8
    //   300: getstatic android/support/v7/appcompat/R$styleable.Spinner_android_dropDownWidth : I
    //   303: bipush #-2
    //   305: invokevirtual getLayoutDimension : (II)I
    //   308: putfield mDropDownWidth : I
    //   311: aload #5
    //   313: aload #8
    //   315: getstatic android/support/v7/appcompat/R$styleable.Spinner_android_popupBackground : I
    //   318: invokevirtual getDrawable : (I)Landroid/graphics/drawable/Drawable;
    //   321: invokevirtual setBackgroundDrawable : (Landroid/graphics/drawable/Drawable;)V
    //   324: aload #5
    //   326: aload #10
    //   328: getstatic android/support/v7/appcompat/R$styleable.Spinner_android_prompt : I
    //   331: invokevirtual getString : (I)Ljava/lang/String;
    //   334: invokevirtual setPromptText : (Ljava/lang/CharSequence;)V
    //   337: aload #8
    //   339: invokevirtual recycle : ()V
    //   342: aload_0
    //   343: aload #5
    //   345: putfield mPopup : Landroid/support/v7/widget/AppCompatSpinner$DropdownPopup;
    //   348: aload_0
    //   349: new android/support/v7/widget/AppCompatSpinner$1
    //   352: dup
    //   353: aload_0
    //   354: aload_0
    //   355: aload #5
    //   357: invokespecial <init> : (Landroid/support/v7/widget/AppCompatSpinner;Landroid/view/View;Landroid/support/v7/widget/AppCompatSpinner$DropdownPopup;)V
    //   360: putfield mForwardingListener : Landroid/support/v7/widget/ForwardingListener;
    //   363: aload #10
    //   365: getstatic android/support/v7/appcompat/R$styleable.Spinner_android_entries : I
    //   368: invokevirtual getTextArray : (I)[Ljava/lang/CharSequence;
    //   371: astore #5
    //   373: aload #5
    //   375: ifnull -> 403
    //   378: new android/widget/ArrayAdapter
    //   381: dup
    //   382: aload_1
    //   383: ldc 17367048
    //   385: aload #5
    //   387: invokespecial <init> : (Landroid/content/Context;I[Ljava/lang/Object;)V
    //   390: astore_1
    //   391: aload_1
    //   392: getstatic android/support/v7/appcompat/R$layout.support_simple_spinner_dropdown_item : I
    //   395: invokevirtual setDropDownViewResource : (I)V
    //   398: aload_0
    //   399: aload_1
    //   400: invokevirtual setAdapter : (Landroid/widget/SpinnerAdapter;)V
    //   403: aload #10
    //   405: invokevirtual recycle : ()V
    //   408: aload_0
    //   409: iconst_1
    //   410: putfield mPopupSet : Z
    //   413: aload_0
    //   414: getfield mTempAdapter : Landroid/widget/SpinnerAdapter;
    //   417: ifnull -> 433
    //   420: aload_0
    //   421: aload_0
    //   422: getfield mTempAdapter : Landroid/widget/SpinnerAdapter;
    //   425: invokevirtual setAdapter : (Landroid/widget/SpinnerAdapter;)V
    //   428: aload_0
    //   429: aconst_null
    //   430: putfield mTempAdapter : Landroid/widget/SpinnerAdapter;
    //   433: aload_0
    //   434: getfield mBackgroundTintHelper : Landroid/support/v7/widget/AppCompatBackgroundHelper;
    //   437: aload_2
    //   438: iload_3
    //   439: invokevirtual loadFromAttributes : (Landroid/util/AttributeSet;I)V
    //   442: return
    // Exception table:
    //   from	to	target	type
    //   137	149	216	java/lang/Exception
    //   137	149	209	finally
    //   157	166	204	java/lang/Exception
    //   157	166	247	finally
    //   170	179	204	java/lang/Exception
    //   170	179	247	finally
    //   225	235	247	finally }
  
  int compatMeasureContentWidth(SpinnerAdapter paramSpinnerAdapter, Drawable paramDrawable) {
    int k = 0;
    if (paramSpinnerAdapter == null)
      return 0; 
    int m = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 0);
    int n = View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 0);
    int i = Math.max(0, getSelectedItemPosition());
    int i1 = Math.min(paramSpinnerAdapter.getCount(), i + 15);
    int j = Math.max(0, i - 15 - i1 - i);
    View view = null;
    i = 0;
    while (j < i1) {
      int i3 = paramSpinnerAdapter.getItemViewType(j);
      int i2 = k;
      if (i3 != k) {
        view = null;
        i2 = i3;
      } 
      view = paramSpinnerAdapter.getView(j, view, this);
      if (view.getLayoutParams() == null)
        view.setLayoutParams(new ViewGroup.LayoutParams(-2, -2)); 
      view.measure(m, n);
      i = Math.max(i, view.getMeasuredWidth());
      j++;
      k = i2;
    } 
    j = i;
    if (paramDrawable != null) {
      paramDrawable.getPadding(this.mTempRect);
      j = i + this.mTempRect.left + this.mTempRect.right;
    } 
    return j;
  }
  
  protected void drawableStateChanged() {
    super.drawableStateChanged();
    if (this.mBackgroundTintHelper != null)
      this.mBackgroundTintHelper.applySupportBackgroundTint(); 
  }
  
  public int getDropDownHorizontalOffset() { return (this.mPopup != null) ? this.mPopup.getHorizontalOffset() : ((Build.VERSION.SDK_INT >= 16) ? super.getDropDownHorizontalOffset() : 0); }
  
  public int getDropDownVerticalOffset() { return (this.mPopup != null) ? this.mPopup.getVerticalOffset() : ((Build.VERSION.SDK_INT >= 16) ? super.getDropDownVerticalOffset() : 0); }
  
  public int getDropDownWidth() { return (this.mPopup != null) ? this.mDropDownWidth : ((Build.VERSION.SDK_INT >= 16) ? super.getDropDownWidth() : 0); }
  
  public Drawable getPopupBackground() { return (this.mPopup != null) ? this.mPopup.getBackground() : ((Build.VERSION.SDK_INT >= 16) ? super.getPopupBackground() : null); }
  
  public Context getPopupContext() { return (this.mPopup != null) ? this.mPopupContext : ((Build.VERSION.SDK_INT >= 23) ? super.getPopupContext() : null); }
  
  public CharSequence getPrompt() { return (this.mPopup != null) ? this.mPopup.getHintText() : super.getPrompt(); }
  
  @Nullable
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public ColorStateList getSupportBackgroundTintList() { return (this.mBackgroundTintHelper != null) ? this.mBackgroundTintHelper.getSupportBackgroundTintList() : null; }
  
  @Nullable
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public PorterDuff.Mode getSupportBackgroundTintMode() { return (this.mBackgroundTintHelper != null) ? this.mBackgroundTintHelper.getSupportBackgroundTintMode() : null; }
  
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    if (this.mPopup != null && this.mPopup.isShowing())
      this.mPopup.dismiss(); 
  }
  
  protected void onMeasure(int paramInt1, int paramInt2) {
    super.onMeasure(paramInt1, paramInt2);
    if (this.mPopup != null && View.MeasureSpec.getMode(paramInt1) == Integer.MIN_VALUE)
      setMeasuredDimension(Math.min(Math.max(getMeasuredWidth(), compatMeasureContentWidth(getAdapter(), getBackground())), View.MeasureSpec.getSize(paramInt1)), getMeasuredHeight()); 
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent) { return (this.mForwardingListener != null && this.mForwardingListener.onTouch(this, paramMotionEvent)) ? true : super.onTouchEvent(paramMotionEvent); }
  
  public boolean performClick() {
    if (this.mPopup != null) {
      if (!this.mPopup.isShowing())
        this.mPopup.show(); 
      return true;
    } 
    return super.performClick();
  }
  
  public void setAdapter(SpinnerAdapter paramSpinnerAdapter) {
    if (!this.mPopupSet) {
      this.mTempAdapter = paramSpinnerAdapter;
      return;
    } 
    super.setAdapter(paramSpinnerAdapter);
    if (this.mPopup != null) {
      Context context;
      if (this.mPopupContext == null) {
        context = getContext();
      } else {
        context = this.mPopupContext;
      } 
      this.mPopup.setAdapter(new DropDownAdapter(paramSpinnerAdapter, context.getTheme()));
    } 
  }
  
  public void setBackgroundDrawable(Drawable paramDrawable) {
    super.setBackgroundDrawable(paramDrawable);
    if (this.mBackgroundTintHelper != null)
      this.mBackgroundTintHelper.onSetBackgroundDrawable(paramDrawable); 
  }
  
  public void setBackgroundResource(@DrawableRes int paramInt) {
    super.setBackgroundResource(paramInt);
    if (this.mBackgroundTintHelper != null)
      this.mBackgroundTintHelper.onSetBackgroundResource(paramInt); 
  }
  
  public void setDropDownHorizontalOffset(int paramInt) {
    if (this.mPopup != null) {
      this.mPopup.setHorizontalOffset(paramInt);
      return;
    } 
    if (Build.VERSION.SDK_INT >= 16)
      super.setDropDownHorizontalOffset(paramInt); 
  }
  
  public void setDropDownVerticalOffset(int paramInt) {
    if (this.mPopup != null) {
      this.mPopup.setVerticalOffset(paramInt);
      return;
    } 
    if (Build.VERSION.SDK_INT >= 16)
      super.setDropDownVerticalOffset(paramInt); 
  }
  
  public void setDropDownWidth(int paramInt) {
    if (this.mPopup != null) {
      this.mDropDownWidth = paramInt;
      return;
    } 
    if (Build.VERSION.SDK_INT >= 16)
      super.setDropDownWidth(paramInt); 
  }
  
  public void setPopupBackgroundDrawable(Drawable paramDrawable) {
    if (this.mPopup != null) {
      this.mPopup.setBackgroundDrawable(paramDrawable);
      return;
    } 
    if (Build.VERSION.SDK_INT >= 16)
      super.setPopupBackgroundDrawable(paramDrawable); 
  }
  
  public void setPopupBackgroundResource(@DrawableRes int paramInt) { setPopupBackgroundDrawable(AppCompatResources.getDrawable(getPopupContext(), paramInt)); }
  
  public void setPrompt(CharSequence paramCharSequence) {
    if (this.mPopup != null) {
      this.mPopup.setPromptText(paramCharSequence);
      return;
    } 
    super.setPrompt(paramCharSequence);
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public void setSupportBackgroundTintList(@Nullable ColorStateList paramColorStateList) {
    if (this.mBackgroundTintHelper != null)
      this.mBackgroundTintHelper.setSupportBackgroundTintList(paramColorStateList); 
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public void setSupportBackgroundTintMode(@Nullable PorterDuff.Mode paramMode) {
    if (this.mBackgroundTintHelper != null)
      this.mBackgroundTintHelper.setSupportBackgroundTintMode(paramMode); 
  }
  
  private static class DropDownAdapter implements ListAdapter, SpinnerAdapter {
    private SpinnerAdapter mAdapter;
    
    private ListAdapter mListAdapter;
    
    public DropDownAdapter(@Nullable SpinnerAdapter param1SpinnerAdapter, @Nullable Resources.Theme param1Theme) {
      this.mAdapter = param1SpinnerAdapter;
      if (param1SpinnerAdapter instanceof ListAdapter)
        this.mListAdapter = (ListAdapter)param1SpinnerAdapter; 
      if (param1Theme != null) {
        ThemedSpinnerAdapter themedSpinnerAdapter;
        if (Build.VERSION.SDK_INT >= 23 && param1SpinnerAdapter instanceof ThemedSpinnerAdapter) {
          themedSpinnerAdapter = (ThemedSpinnerAdapter)param1SpinnerAdapter;
          if (themedSpinnerAdapter.getDropDownViewTheme() != param1Theme) {
            themedSpinnerAdapter.setDropDownViewTheme(param1Theme);
            return;
          } 
        } else if (themedSpinnerAdapter instanceof ThemedSpinnerAdapter) {
          ThemedSpinnerAdapter themedSpinnerAdapter1 = (ThemedSpinnerAdapter)themedSpinnerAdapter;
          if (themedSpinnerAdapter1.getDropDownViewTheme() == null)
            themedSpinnerAdapter1.setDropDownViewTheme(param1Theme); 
        } 
      } 
    }
    
    public boolean areAllItemsEnabled() {
      ListAdapter listAdapter = this.mListAdapter;
      return (listAdapter != null) ? listAdapter.areAllItemsEnabled() : 1;
    }
    
    public int getCount() { return (this.mAdapter == null) ? 0 : this.mAdapter.getCount(); }
    
    public View getDropDownView(int param1Int, View param1View, ViewGroup param1ViewGroup) { return (this.mAdapter == null) ? null : this.mAdapter.getDropDownView(param1Int, param1View, param1ViewGroup); }
    
    public Object getItem(int param1Int) { return (this.mAdapter == null) ? null : this.mAdapter.getItem(param1Int); }
    
    public long getItemId(int param1Int) { return (this.mAdapter == null) ? -1L : this.mAdapter.getItemId(param1Int); }
    
    public int getItemViewType(int param1Int) { return 0; }
    
    public View getView(int param1Int, View param1View, ViewGroup param1ViewGroup) { return getDropDownView(param1Int, param1View, param1ViewGroup); }
    
    public int getViewTypeCount() { return 1; }
    
    public boolean hasStableIds() { return (this.mAdapter != null && this.mAdapter.hasStableIds()); }
    
    public boolean isEmpty() { return (getCount() == 0); }
    
    public boolean isEnabled(int param1Int) {
      ListAdapter listAdapter = this.mListAdapter;
      return (listAdapter != null) ? listAdapter.isEnabled(param1Int) : 1;
    }
    
    public void registerDataSetObserver(DataSetObserver param1DataSetObserver) {
      if (this.mAdapter != null)
        this.mAdapter.registerDataSetObserver(param1DataSetObserver); 
    }
    
    public void unregisterDataSetObserver(DataSetObserver param1DataSetObserver) {
      if (this.mAdapter != null)
        this.mAdapter.unregisterDataSetObserver(param1DataSetObserver); 
    }
  }
  
  private class DropdownPopup extends ListPopupWindow {
    ListAdapter mAdapter;
    
    private CharSequence mHintText;
    
    private final Rect mVisibleRect = new Rect();
    
    public DropdownPopup(Context param1Context, AttributeSet param1AttributeSet, int param1Int) {
      super(param1Context, param1AttributeSet, param1Int);
      setAnchorView(this$0);
      setModal(true);
      setPromptPosition(0);
      setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> param2AdapterView, View param2View, int param2Int, long param2Long) {
              AppCompatSpinner.DropdownPopup.this.this$0.setSelection(param2Int);
              if (AppCompatSpinner.DropdownPopup.this.this$0.getOnItemClickListener() != null)
                AppCompatSpinner.DropdownPopup.this.this$0.performItemClick(param2View, param2Int, AppCompatSpinner.DropdownPopup.this.mAdapter.getItemId(param2Int)); 
              AppCompatSpinner.DropdownPopup.this.dismiss();
            }
          });
    }
    
    void computeContentWidth() {
      Drawable drawable = getBackground();
      int i = 0;
      if (drawable != null) {
        drawable.getPadding(AppCompatSpinner.this.mTempRect);
        if (ViewUtils.isLayoutRtl(AppCompatSpinner.this)) {
          i = this.this$0.mTempRect.right;
        } else {
          i = -this.this$0.mTempRect.left;
        } 
      } else {
        Rect rect = AppCompatSpinner.this.mTempRect;
        this.this$0.mTempRect.right = 0;
        rect.left = 0;
      } 
      int j = AppCompatSpinner.this.getPaddingLeft();
      int k = AppCompatSpinner.this.getPaddingRight();
      int m = AppCompatSpinner.this.getWidth();
      if (AppCompatSpinner.this.mDropDownWidth == -2) {
        int i1 = AppCompatSpinner.this.compatMeasureContentWidth((SpinnerAdapter)this.mAdapter, getBackground());
        int i2 = (this.this$0.getContext().getResources().getDisplayMetrics()).widthPixels - this.this$0.mTempRect.left - this.this$0.mTempRect.right;
        int n = i1;
        if (i1 > i2)
          n = i2; 
        setContentWidth(Math.max(n, m - j - k));
      } else if (AppCompatSpinner.this.mDropDownWidth == -1) {
        setContentWidth(m - j - k);
      } else {
        setContentWidth(AppCompatSpinner.this.mDropDownWidth);
      } 
      if (ViewUtils.isLayoutRtl(AppCompatSpinner.this)) {
        i += m - k - getWidth();
      } else {
        i += j;
      } 
      setHorizontalOffset(i);
    }
    
    public CharSequence getHintText() { return this.mHintText; }
    
    boolean isVisibleToUser(View param1View) { return (ViewCompat.isAttachedToWindow(param1View) && param1View.getGlobalVisibleRect(this.mVisibleRect)); }
    
    public void setAdapter(ListAdapter param1ListAdapter) {
      super.setAdapter(param1ListAdapter);
      this.mAdapter = param1ListAdapter;
    }
    
    public void setPromptText(CharSequence param1CharSequence) { this.mHintText = param1CharSequence; }
    
    public void show() {
      boolean bool = isShowing();
      computeContentWidth();
      setInputMethodMode(2);
      super.show();
      getListView().setChoiceMode(1);
      setSelection(AppCompatSpinner.this.getSelectedItemPosition());
      if (bool)
        return; 
      ViewTreeObserver viewTreeObserver = AppCompatSpinner.this.getViewTreeObserver();
      if (viewTreeObserver != null) {
        final ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
              if (!AppCompatSpinner.DropdownPopup.this.isVisibleToUser(AppCompatSpinner.DropdownPopup.this.this$0)) {
                AppCompatSpinner.DropdownPopup.this.dismiss();
                return;
              } 
              AppCompatSpinner.DropdownPopup.this.computeContentWidth();
              AppCompatSpinner.DropdownPopup.this.show();
            }
          };
        viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener);
        setOnDismissListener(new PopupWindow.OnDismissListener() {
              public void onDismiss() {
                ViewTreeObserver viewTreeObserver = AppCompatSpinner.DropdownPopup.this.this$0.getViewTreeObserver();
                if (viewTreeObserver != null)
                  viewTreeObserver.removeGlobalOnLayoutListener(layoutListener); 
              }
            });
      } 
    }
  }
  
  class null implements AdapterView.OnItemClickListener {
    public void onItemClick(AdapterView<?> param1AdapterView, View param1View, int param1Int, long param1Long) {
      this.this$1.this$0.setSelection(param1Int);
      if (this.this$1.this$0.getOnItemClickListener() != null)
        this.this$1.this$0.performItemClick(param1View, param1Int, this.this$1.mAdapter.getItemId(param1Int)); 
      this.this$1.dismiss();
    }
  }
  
  class null implements ViewTreeObserver.OnGlobalLayoutListener {
    null() {}
    
    public void onGlobalLayout() {
      if (!this.this$1.isVisibleToUser(this.this$1.this$0)) {
        this.this$1.dismiss();
        return;
      } 
      this.this$1.computeContentWidth();
      this.this$1.show();
    }
  }
  
  class null implements PopupWindow.OnDismissListener {
    public void onDismiss() {
      ViewTreeObserver viewTreeObserver = this.this$1.this$0.getViewTreeObserver();
      if (viewTreeObserver != null)
        viewTreeObserver.removeGlobalOnLayoutListener(layoutListener); 
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/AppCompatSpinner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */