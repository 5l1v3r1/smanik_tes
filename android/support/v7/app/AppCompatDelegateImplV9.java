package android.support.v7.app;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NavUtils;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v7.appcompat.R;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.view.menu.ListMenuPresenter;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPresenter;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.ActionBarContextView;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.ContentFrameLayout;
import android.support.v7.widget.DecorContentParent;
import android.support.v7.widget.FitWindowsViewGroup;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.VectorEnabledTintResources;
import android.support.v7.widget.ViewUtils;
import android.text.TextUtils;
import android.util.AndroidRuntimeException;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import org.xmlpull.v1.XmlPullParser;

@RequiresApi(14)
class AppCompatDelegateImplV9 extends AppCompatDelegateImplBase implements MenuBuilder.Callback, LayoutInflater.Factory2 {
  private static final boolean IS_PRE_LOLLIPOP;
  
  private ActionMenuPresenterCallback mActionMenuPresenterCallback;
  
  ActionMode mActionMode;
  
  PopupWindow mActionModePopup;
  
  ActionBarContextView mActionModeView;
  
  private AppCompatViewInflater mAppCompatViewInflater;
  
  private boolean mClosingActionMenu;
  
  private DecorContentParent mDecorContentParent;
  
  private boolean mEnableDefaultActionBarUp;
  
  ViewPropertyAnimatorCompat mFadeAnim = null;
  
  private boolean mFeatureIndeterminateProgress;
  
  private boolean mFeatureProgress;
  
  int mInvalidatePanelMenuFeatures;
  
  boolean mInvalidatePanelMenuPosted;
  
  private final Runnable mInvalidatePanelMenuRunnable = new Runnable() {
      public void run() {
        if ((AppCompatDelegateImplV9.this.mInvalidatePanelMenuFeatures & true) != 0)
          AppCompatDelegateImplV9.this.doInvalidatePanelMenu(0); 
        if ((AppCompatDelegateImplV9.this.mInvalidatePanelMenuFeatures & 0x1000) != 0)
          AppCompatDelegateImplV9.this.doInvalidatePanelMenu(108); 
        AppCompatDelegateImplV9.this.mInvalidatePanelMenuPosted = false;
        AppCompatDelegateImplV9.this.mInvalidatePanelMenuFeatures = 0;
      }
    };
  
  private boolean mLongPressBackDown;
  
  private PanelMenuPresenterCallback mPanelMenuPresenterCallback;
  
  private PanelFeatureState[] mPanels;
  
  private PanelFeatureState mPreparedPanel;
  
  Runnable mShowActionModePopup;
  
  private View mStatusGuard;
  
  private ViewGroup mSubDecor;
  
  private boolean mSubDecorInstalled;
  
  private Rect mTempRect1;
  
  private Rect mTempRect2;
  
  private TextView mTitleView;
  
  static  {
    boolean bool;
    if (Build.VERSION.SDK_INT < 21) {
      bool = true;
    } else {
      bool = false;
    } 
    IS_PRE_LOLLIPOP = bool;
  }
  
  AppCompatDelegateImplV9(Context paramContext, Window paramWindow, AppCompatCallback paramAppCompatCallback) { super(paramContext, paramWindow, paramAppCompatCallback); }
  
  private void applyFixedSizeWindow() {
    ContentFrameLayout contentFrameLayout = (ContentFrameLayout)this.mSubDecor.findViewById(16908290);
    View view = this.mWindow.getDecorView();
    contentFrameLayout.setDecorPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
    TypedArray typedArray = this.mContext.obtainStyledAttributes(R.styleable.AppCompatTheme);
    typedArray.getValue(R.styleable.AppCompatTheme_windowMinWidthMajor, contentFrameLayout.getMinWidthMajor());
    typedArray.getValue(R.styleable.AppCompatTheme_windowMinWidthMinor, contentFrameLayout.getMinWidthMinor());
    if (typedArray.hasValue(R.styleable.AppCompatTheme_windowFixedWidthMajor))
      typedArray.getValue(R.styleable.AppCompatTheme_windowFixedWidthMajor, contentFrameLayout.getFixedWidthMajor()); 
    if (typedArray.hasValue(R.styleable.AppCompatTheme_windowFixedWidthMinor))
      typedArray.getValue(R.styleable.AppCompatTheme_windowFixedWidthMinor, contentFrameLayout.getFixedWidthMinor()); 
    if (typedArray.hasValue(R.styleable.AppCompatTheme_windowFixedHeightMajor))
      typedArray.getValue(R.styleable.AppCompatTheme_windowFixedHeightMajor, contentFrameLayout.getFixedHeightMajor()); 
    if (typedArray.hasValue(R.styleable.AppCompatTheme_windowFixedHeightMinor))
      typedArray.getValue(R.styleable.AppCompatTheme_windowFixedHeightMinor, contentFrameLayout.getFixedHeightMinor()); 
    typedArray.recycle();
    contentFrameLayout.requestLayout();
  }
  
  private ViewGroup createSubDecor() {
    TypedArray typedArray = this.mContext.obtainStyledAttributes(R.styleable.AppCompatTheme);
    if (!typedArray.hasValue(R.styleable.AppCompatTheme_windowActionBar)) {
      typedArray.recycle();
      throw new IllegalStateException("You need to use a Theme.AppCompat theme (or descendant) with this activity.");
    } 
    if (typedArray.getBoolean(R.styleable.AppCompatTheme_windowNoTitle, false)) {
      requestWindowFeature(1);
    } else if (typedArray.getBoolean(R.styleable.AppCompatTheme_windowActionBar, false)) {
      requestWindowFeature(108);
    } 
    if (typedArray.getBoolean(R.styleable.AppCompatTheme_windowActionBarOverlay, false))
      requestWindowFeature(109); 
    if (typedArray.getBoolean(R.styleable.AppCompatTheme_windowActionModeOverlay, false))
      requestWindowFeature(10); 
    this.mIsFloating = typedArray.getBoolean(R.styleable.AppCompatTheme_android_windowIsFloating, false);
    typedArray.recycle();
    this.mWindow.getDecorView();
    StringBuilder stringBuilder = LayoutInflater.from(this.mContext);
    if (!this.mWindowNoTitle) {
      if (this.mIsFloating) {
        ViewGroup viewGroup1 = (ViewGroup)stringBuilder.inflate(R.layout.abc_dialog_title_material, null);
        this.mOverlayActionBar = false;
        this.mHasActionBar = false;
      } else if (this.mHasActionBar) {
        Context context = new TypedValue();
        this.mContext.getTheme().resolveAttribute(R.attr.actionBarTheme, context, true);
        if (context.resourceId != 0) {
          ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(this.mContext, context.resourceId);
        } else {
          context = this.mContext;
        } 
        ViewGroup viewGroup2 = (ViewGroup)LayoutInflater.from(context).inflate(R.layout.abc_screen_toolbar, null);
        this.mDecorContentParent = (DecorContentParent)viewGroup2.findViewById(R.id.decor_content_parent);
        this.mDecorContentParent.setWindowCallback(getWindowCallback());
        if (this.mOverlayActionBar)
          this.mDecorContentParent.initFeature(109); 
        if (this.mFeatureProgress)
          this.mDecorContentParent.initFeature(2); 
        ViewGroup viewGroup1 = viewGroup2;
        if (this.mFeatureIndeterminateProgress) {
          this.mDecorContentParent.initFeature(5);
          viewGroup1 = viewGroup2;
        } 
      } else {
        stringBuilder = null;
      } 
    } else {
      if (this.mOverlayActionMode) {
        stringBuilder = (ViewGroup)stringBuilder.inflate(R.layout.abc_screen_simple_overlay_action_mode, null);
      } else {
        stringBuilder = (ViewGroup)stringBuilder.inflate(R.layout.abc_screen_simple, null);
      } 
      if (Build.VERSION.SDK_INT >= 21) {
        ViewCompat.setOnApplyWindowInsetsListener(stringBuilder, new OnApplyWindowInsetsListener() {
              public WindowInsetsCompat onApplyWindowInsets(View param1View, WindowInsetsCompat param1WindowInsetsCompat) {
                int i = param1WindowInsetsCompat.getSystemWindowInsetTop();
                int j = AppCompatDelegateImplV9.this.updateStatusGuard(i);
                WindowInsetsCompat windowInsetsCompat = param1WindowInsetsCompat;
                if (i != j)
                  windowInsetsCompat = param1WindowInsetsCompat.replaceSystemWindowInsets(param1WindowInsetsCompat.getSystemWindowInsetLeft(), j, param1WindowInsetsCompat.getSystemWindowInsetRight(), param1WindowInsetsCompat.getSystemWindowInsetBottom()); 
                return ViewCompat.onApplyWindowInsets(param1View, windowInsetsCompat);
              }
            });
      } else {
        ((FitWindowsViewGroup)stringBuilder).setOnFitSystemWindowsListener(new FitWindowsViewGroup.OnFitSystemWindowsListener() {
              public void onFitSystemWindows(Rect param1Rect) { param1Rect.top = AppCompatDelegateImplV9.this.updateStatusGuard(param1Rect.top); }
            });
      } 
    } 
    if (stringBuilder == null) {
      stringBuilder = new StringBuilder();
      stringBuilder.append("AppCompat does not support the current theme features: { windowActionBar: ");
      stringBuilder.append(this.mHasActionBar);
      stringBuilder.append(", windowActionBarOverlay: ");
      stringBuilder.append(this.mOverlayActionBar);
      stringBuilder.append(", android:windowIsFloating: ");
      stringBuilder.append(this.mIsFloating);
      stringBuilder.append(", windowActionModeOverlay: ");
      stringBuilder.append(this.mOverlayActionMode);
      stringBuilder.append(", windowNoTitle: ");
      stringBuilder.append(this.mWindowNoTitle);
      stringBuilder.append(" }");
      throw new IllegalArgumentException(stringBuilder.toString());
    } 
    if (this.mDecorContentParent == null)
      this.mTitleView = (TextView)stringBuilder.findViewById(R.id.title); 
    ViewUtils.makeOptionalFitsSystemWindows(stringBuilder);
    ContentFrameLayout contentFrameLayout = (ContentFrameLayout)stringBuilder.findViewById(R.id.action_bar_activity_content);
    ViewGroup viewGroup = (ViewGroup)this.mWindow.findViewById(16908290);
    if (viewGroup != null) {
      while (viewGroup.getChildCount() > 0) {
        View view = viewGroup.getChildAt(0);
        viewGroup.removeViewAt(0);
        contentFrameLayout.addView(view);
      } 
      viewGroup.setId(-1);
      contentFrameLayout.setId(16908290);
      if (viewGroup instanceof FrameLayout)
        ((FrameLayout)viewGroup).setForeground(null); 
    } 
    this.mWindow.setContentView(stringBuilder);
    contentFrameLayout.setAttachListener(new ContentFrameLayout.OnAttachListener() {
          public void onAttachedFromWindow() {}
          
          public void onDetachedFromWindow() { AppCompatDelegateImplV9.this.dismissPopups(); }
        });
    return stringBuilder;
  }
  
  private void ensureSubDecor() {
    if (!this.mSubDecorInstalled) {
      this.mSubDecor = createSubDecor();
      CharSequence charSequence = getTitle();
      if (!TextUtils.isEmpty(charSequence))
        onTitleChanged(charSequence); 
      applyFixedSizeWindow();
      onSubDecorInstalled(this.mSubDecor);
      this.mSubDecorInstalled = true;
      PanelFeatureState panelFeatureState = getPanelState(0, false);
      if (!isDestroyed() && (panelFeatureState == null || panelFeatureState.menu == null))
        invalidatePanelMenu(108); 
    } 
  }
  
  private boolean initializePanelContent(PanelFeatureState paramPanelFeatureState) {
    if (paramPanelFeatureState.createdPanelView != null) {
      paramPanelFeatureState.shownPanelView = paramPanelFeatureState.createdPanelView;
      return true;
    } 
    if (paramPanelFeatureState.menu == null)
      return false; 
    if (this.mPanelMenuPresenterCallback == null)
      this.mPanelMenuPresenterCallback = new PanelMenuPresenterCallback(); 
    paramPanelFeatureState.shownPanelView = (View)paramPanelFeatureState.getListMenuView(this.mPanelMenuPresenterCallback);
    return (paramPanelFeatureState.shownPanelView != null);
  }
  
  private boolean initializePanelDecor(PanelFeatureState paramPanelFeatureState) {
    paramPanelFeatureState.setStyle(getActionBarThemedContext());
    paramPanelFeatureState.decorView = new ListMenuDecorView(paramPanelFeatureState.listPresenterContext);
    paramPanelFeatureState.gravity = 81;
    return true;
  }
  
  private boolean initializePanelMenu(PanelFeatureState paramPanelFeatureState) { // Byte code:
    //   0: aload_0
    //   1: getfield mContext : Landroid/content/Context;
    //   4: astore #4
    //   6: aload_1
    //   7: getfield featureId : I
    //   10: ifeq -> 25
    //   13: aload #4
    //   15: astore_2
    //   16: aload_1
    //   17: getfield featureId : I
    //   20: bipush #108
    //   22: if_icmpne -> 191
    //   25: aload #4
    //   27: astore_2
    //   28: aload_0
    //   29: getfield mDecorContentParent : Landroid/support/v7/widget/DecorContentParent;
    //   32: ifnull -> 191
    //   35: new android/util/TypedValue
    //   38: dup
    //   39: invokespecial <init> : ()V
    //   42: astore #5
    //   44: aload #4
    //   46: invokevirtual getTheme : ()Landroid/content/res/Resources$Theme;
    //   49: astore #6
    //   51: aload #6
    //   53: getstatic android/support/v7/appcompat/R$attr.actionBarTheme : I
    //   56: aload #5
    //   58: iconst_1
    //   59: invokevirtual resolveAttribute : (ILandroid/util/TypedValue;Z)Z
    //   62: pop
    //   63: aconst_null
    //   64: astore_2
    //   65: aload #5
    //   67: getfield resourceId : I
    //   70: ifeq -> 112
    //   73: aload #4
    //   75: invokevirtual getResources : ()Landroid/content/res/Resources;
    //   78: invokevirtual newTheme : ()Landroid/content/res/Resources$Theme;
    //   81: astore_2
    //   82: aload_2
    //   83: aload #6
    //   85: invokevirtual setTo : (Landroid/content/res/Resources$Theme;)V
    //   88: aload_2
    //   89: aload #5
    //   91: getfield resourceId : I
    //   94: iconst_1
    //   95: invokevirtual applyStyle : (IZ)V
    //   98: aload_2
    //   99: getstatic android/support/v7/appcompat/R$attr.actionBarWidgetTheme : I
    //   102: aload #5
    //   104: iconst_1
    //   105: invokevirtual resolveAttribute : (ILandroid/util/TypedValue;Z)Z
    //   108: pop
    //   109: goto -> 124
    //   112: aload #6
    //   114: getstatic android/support/v7/appcompat/R$attr.actionBarWidgetTheme : I
    //   117: aload #5
    //   119: iconst_1
    //   120: invokevirtual resolveAttribute : (ILandroid/util/TypedValue;Z)Z
    //   123: pop
    //   124: aload_2
    //   125: astore_3
    //   126: aload #5
    //   128: getfield resourceId : I
    //   131: ifeq -> 165
    //   134: aload_2
    //   135: astore_3
    //   136: aload_2
    //   137: ifnonnull -> 155
    //   140: aload #4
    //   142: invokevirtual getResources : ()Landroid/content/res/Resources;
    //   145: invokevirtual newTheme : ()Landroid/content/res/Resources$Theme;
    //   148: astore_3
    //   149: aload_3
    //   150: aload #6
    //   152: invokevirtual setTo : (Landroid/content/res/Resources$Theme;)V
    //   155: aload_3
    //   156: aload #5
    //   158: getfield resourceId : I
    //   161: iconst_1
    //   162: invokevirtual applyStyle : (IZ)V
    //   165: aload #4
    //   167: astore_2
    //   168: aload_3
    //   169: ifnull -> 191
    //   172: new android/support/v7/view/ContextThemeWrapper
    //   175: dup
    //   176: aload #4
    //   178: iconst_0
    //   179: invokespecial <init> : (Landroid/content/Context;I)V
    //   182: astore_2
    //   183: aload_2
    //   184: invokevirtual getTheme : ()Landroid/content/res/Resources$Theme;
    //   187: aload_3
    //   188: invokevirtual setTo : (Landroid/content/res/Resources$Theme;)V
    //   191: new android/support/v7/view/menu/MenuBuilder
    //   194: dup
    //   195: aload_2
    //   196: invokespecial <init> : (Landroid/content/Context;)V
    //   199: astore_2
    //   200: aload_2
    //   201: aload_0
    //   202: invokevirtual setCallback : (Landroid/support/v7/view/menu/MenuBuilder$Callback;)V
    //   205: aload_1
    //   206: aload_2
    //   207: invokevirtual setMenu : (Landroid/support/v7/view/menu/MenuBuilder;)V
    //   210: iconst_1
    //   211: ireturn }
  
  private void invalidatePanelMenu(int paramInt) {
    this.mInvalidatePanelMenuFeatures = 1 << paramInt | this.mInvalidatePanelMenuFeatures;
    if (!this.mInvalidatePanelMenuPosted) {
      ViewCompat.postOnAnimation(this.mWindow.getDecorView(), this.mInvalidatePanelMenuRunnable);
      this.mInvalidatePanelMenuPosted = true;
    } 
  }
  
  private boolean onKeyDownPanel(int paramInt, KeyEvent paramKeyEvent) {
    if (paramKeyEvent.getRepeatCount() == 0) {
      PanelFeatureState panelFeatureState = getPanelState(paramInt, true);
      if (!panelFeatureState.isOpen)
        return preparePanel(panelFeatureState, paramKeyEvent); 
    } 
    return false;
  }
  
  private boolean onKeyUpPanel(int paramInt, KeyEvent paramKeyEvent) { // Byte code:
    //   0: aload_0
    //   1: getfield mActionMode : Landroid/support/v7/view/ActionMode;
    //   4: ifnull -> 9
    //   7: iconst_0
    //   8: ireturn
    //   9: aload_0
    //   10: iload_1
    //   11: iconst_1
    //   12: invokevirtual getPanelState : (IZ)Landroid/support/v7/app/AppCompatDelegateImplV9$PanelFeatureState;
    //   15: astore #4
    //   17: iload_1
    //   18: ifne -> 108
    //   21: aload_0
    //   22: getfield mDecorContentParent : Landroid/support/v7/widget/DecorContentParent;
    //   25: ifnull -> 108
    //   28: aload_0
    //   29: getfield mDecorContentParent : Landroid/support/v7/widget/DecorContentParent;
    //   32: invokeinterface canShowOverflowMenu : ()Z
    //   37: ifeq -> 108
    //   40: aload_0
    //   41: getfield mContext : Landroid/content/Context;
    //   44: invokestatic get : (Landroid/content/Context;)Landroid/view/ViewConfiguration;
    //   47: invokevirtual hasPermanentMenuKey : ()Z
    //   50: ifne -> 108
    //   53: aload_0
    //   54: getfield mDecorContentParent : Landroid/support/v7/widget/DecorContentParent;
    //   57: invokeinterface isOverflowMenuShowing : ()Z
    //   62: ifne -> 95
    //   65: aload_0
    //   66: invokevirtual isDestroyed : ()Z
    //   69: ifne -> 178
    //   72: aload_0
    //   73: aload #4
    //   75: aload_2
    //   76: invokespecial preparePanel : (Landroid/support/v7/app/AppCompatDelegateImplV9$PanelFeatureState;Landroid/view/KeyEvent;)Z
    //   79: ifeq -> 178
    //   82: aload_0
    //   83: getfield mDecorContentParent : Landroid/support/v7/widget/DecorContentParent;
    //   86: invokeinterface showOverflowMenu : ()Z
    //   91: istore_3
    //   92: goto -> 196
    //   95: aload_0
    //   96: getfield mDecorContentParent : Landroid/support/v7/widget/DecorContentParent;
    //   99: invokeinterface hideOverflowMenu : ()Z
    //   104: istore_3
    //   105: goto -> 196
    //   108: aload #4
    //   110: getfield isOpen : Z
    //   113: ifne -> 183
    //   116: aload #4
    //   118: getfield isHandled : Z
    //   121: ifeq -> 127
    //   124: goto -> 183
    //   127: aload #4
    //   129: getfield isPrepared : Z
    //   132: ifeq -> 178
    //   135: aload #4
    //   137: getfield refreshMenuContent : Z
    //   140: ifeq -> 160
    //   143: aload #4
    //   145: iconst_0
    //   146: putfield isPrepared : Z
    //   149: aload_0
    //   150: aload #4
    //   152: aload_2
    //   153: invokespecial preparePanel : (Landroid/support/v7/app/AppCompatDelegateImplV9$PanelFeatureState;Landroid/view/KeyEvent;)Z
    //   156: istore_3
    //   157: goto -> 162
    //   160: iconst_1
    //   161: istore_3
    //   162: iload_3
    //   163: ifeq -> 178
    //   166: aload_0
    //   167: aload #4
    //   169: aload_2
    //   170: invokespecial openPanel : (Landroid/support/v7/app/AppCompatDelegateImplV9$PanelFeatureState;Landroid/view/KeyEvent;)V
    //   173: iconst_1
    //   174: istore_3
    //   175: goto -> 196
    //   178: iconst_0
    //   179: istore_3
    //   180: goto -> 196
    //   183: aload #4
    //   185: getfield isOpen : Z
    //   188: istore_3
    //   189: aload_0
    //   190: aload #4
    //   192: iconst_1
    //   193: invokevirtual closePanel : (Landroid/support/v7/app/AppCompatDelegateImplV9$PanelFeatureState;Z)V
    //   196: iload_3
    //   197: ifeq -> 235
    //   200: aload_0
    //   201: getfield mContext : Landroid/content/Context;
    //   204: ldc_w 'audio'
    //   207: invokevirtual getSystemService : (Ljava/lang/String;)Ljava/lang/Object;
    //   210: checkcast android/media/AudioManager
    //   213: astore_2
    //   214: aload_2
    //   215: ifnull -> 225
    //   218: aload_2
    //   219: iconst_0
    //   220: invokevirtual playSoundEffect : (I)V
    //   223: iload_3
    //   224: ireturn
    //   225: ldc_w 'AppCompatDelegate'
    //   228: ldc_w 'Couldn't get audio manager'
    //   231: invokestatic w : (Ljava/lang/String;Ljava/lang/String;)I
    //   234: pop
    //   235: iload_3
    //   236: ireturn }
  
  private void openPanel(PanelFeatureState paramPanelFeatureState, KeyEvent paramKeyEvent) { // Byte code:
    //   0: aload_1
    //   1: getfield isOpen : Z
    //   4: ifne -> 409
    //   7: aload_0
    //   8: invokevirtual isDestroyed : ()Z
    //   11: ifeq -> 15
    //   14: return
    //   15: aload_1
    //   16: getfield featureId : I
    //   19: ifne -> 54
    //   22: aload_0
    //   23: getfield mContext : Landroid/content/Context;
    //   26: invokevirtual getResources : ()Landroid/content/res/Resources;
    //   29: invokevirtual getConfiguration : ()Landroid/content/res/Configuration;
    //   32: getfield screenLayout : I
    //   35: bipush #15
    //   37: iand
    //   38: iconst_4
    //   39: if_icmpne -> 47
    //   42: iconst_1
    //   43: istore_3
    //   44: goto -> 49
    //   47: iconst_0
    //   48: istore_3
    //   49: iload_3
    //   50: ifeq -> 54
    //   53: return
    //   54: aload_0
    //   55: invokevirtual getWindowCallback : ()Landroid/view/Window$Callback;
    //   58: astore #4
    //   60: aload #4
    //   62: ifnull -> 90
    //   65: aload #4
    //   67: aload_1
    //   68: getfield featureId : I
    //   71: aload_1
    //   72: getfield menu : Landroid/support/v7/view/menu/MenuBuilder;
    //   75: invokeinterface onMenuOpened : (ILandroid/view/Menu;)Z
    //   80: ifne -> 90
    //   83: aload_0
    //   84: aload_1
    //   85: iconst_1
    //   86: invokevirtual closePanel : (Landroid/support/v7/app/AppCompatDelegateImplV9$PanelFeatureState;Z)V
    //   89: return
    //   90: aload_0
    //   91: getfield mContext : Landroid/content/Context;
    //   94: ldc_w 'window'
    //   97: invokevirtual getSystemService : (Ljava/lang/String;)Ljava/lang/Object;
    //   100: checkcast android/view/WindowManager
    //   103: astore #5
    //   105: aload #5
    //   107: ifnonnull -> 111
    //   110: return
    //   111: aload_0
    //   112: aload_1
    //   113: aload_2
    //   114: invokespecial preparePanel : (Landroid/support/v7/app/AppCompatDelegateImplV9$PanelFeatureState;Landroid/view/KeyEvent;)Z
    //   117: ifne -> 121
    //   120: return
    //   121: aload_1
    //   122: getfield decorView : Landroid/view/ViewGroup;
    //   125: ifnull -> 170
    //   128: aload_1
    //   129: getfield refreshDecorView : Z
    //   132: ifeq -> 138
    //   135: goto -> 170
    //   138: aload_1
    //   139: getfield createdPanelView : Landroid/view/View;
    //   142: ifnull -> 339
    //   145: aload_1
    //   146: getfield createdPanelView : Landroid/view/View;
    //   149: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
    //   152: astore_2
    //   153: aload_2
    //   154: ifnull -> 339
    //   157: aload_2
    //   158: getfield width : I
    //   161: iconst_m1
    //   162: if_icmpne -> 339
    //   165: iconst_m1
    //   166: istore_3
    //   167: goto -> 342
    //   170: aload_1
    //   171: getfield decorView : Landroid/view/ViewGroup;
    //   174: ifnonnull -> 193
    //   177: aload_0
    //   178: aload_1
    //   179: invokespecial initializePanelDecor : (Landroid/support/v7/app/AppCompatDelegateImplV9$PanelFeatureState;)Z
    //   182: ifeq -> 192
    //   185: aload_1
    //   186: getfield decorView : Landroid/view/ViewGroup;
    //   189: ifnonnull -> 217
    //   192: return
    //   193: aload_1
    //   194: getfield refreshDecorView : Z
    //   197: ifeq -> 217
    //   200: aload_1
    //   201: getfield decorView : Landroid/view/ViewGroup;
    //   204: invokevirtual getChildCount : ()I
    //   207: ifle -> 217
    //   210: aload_1
    //   211: getfield decorView : Landroid/view/ViewGroup;
    //   214: invokevirtual removeAllViews : ()V
    //   217: aload_0
    //   218: aload_1
    //   219: invokespecial initializePanelContent : (Landroid/support/v7/app/AppCompatDelegateImplV9$PanelFeatureState;)Z
    //   222: ifeq -> 408
    //   225: aload_1
    //   226: invokevirtual hasPanelItems : ()Z
    //   229: ifne -> 233
    //   232: return
    //   233: aload_1
    //   234: getfield shownPanelView : Landroid/view/View;
    //   237: invokevirtual getLayoutParams : ()Landroid/view/ViewGroup$LayoutParams;
    //   240: astore #4
    //   242: aload #4
    //   244: astore_2
    //   245: aload #4
    //   247: ifnonnull -> 262
    //   250: new android/view/ViewGroup$LayoutParams
    //   253: dup
    //   254: bipush #-2
    //   256: bipush #-2
    //   258: invokespecial <init> : (II)V
    //   261: astore_2
    //   262: aload_1
    //   263: getfield background : I
    //   266: istore_3
    //   267: aload_1
    //   268: getfield decorView : Landroid/view/ViewGroup;
    //   271: iload_3
    //   272: invokevirtual setBackgroundResource : (I)V
    //   275: aload_1
    //   276: getfield shownPanelView : Landroid/view/View;
    //   279: invokevirtual getParent : ()Landroid/view/ViewParent;
    //   282: astore #4
    //   284: aload #4
    //   286: ifnull -> 309
    //   289: aload #4
    //   291: instanceof android/view/ViewGroup
    //   294: ifeq -> 309
    //   297: aload #4
    //   299: checkcast android/view/ViewGroup
    //   302: aload_1
    //   303: getfield shownPanelView : Landroid/view/View;
    //   306: invokevirtual removeView : (Landroid/view/View;)V
    //   309: aload_1
    //   310: getfield decorView : Landroid/view/ViewGroup;
    //   313: aload_1
    //   314: getfield shownPanelView : Landroid/view/View;
    //   317: aload_2
    //   318: invokevirtual addView : (Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   321: aload_1
    //   322: getfield shownPanelView : Landroid/view/View;
    //   325: invokevirtual hasFocus : ()Z
    //   328: ifne -> 339
    //   331: aload_1
    //   332: getfield shownPanelView : Landroid/view/View;
    //   335: invokevirtual requestFocus : ()Z
    //   338: pop
    //   339: bipush #-2
    //   341: istore_3
    //   342: aload_1
    //   343: iconst_0
    //   344: putfield isHandled : Z
    //   347: new android/view/WindowManager$LayoutParams
    //   350: dup
    //   351: iload_3
    //   352: bipush #-2
    //   354: aload_1
    //   355: getfield x : I
    //   358: aload_1
    //   359: getfield y : I
    //   362: sipush #1002
    //   365: ldc_w 8519680
    //   368: bipush #-3
    //   370: invokespecial <init> : (IIIIIII)V
    //   373: astore_2
    //   374: aload_2
    //   375: aload_1
    //   376: getfield gravity : I
    //   379: putfield gravity : I
    //   382: aload_2
    //   383: aload_1
    //   384: getfield windowAnimations : I
    //   387: putfield windowAnimations : I
    //   390: aload #5
    //   392: aload_1
    //   393: getfield decorView : Landroid/view/ViewGroup;
    //   396: aload_2
    //   397: invokeinterface addView : (Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   402: aload_1
    //   403: iconst_1
    //   404: putfield isOpen : Z
    //   407: return
    //   408: return
    //   409: return }
  
  private boolean performPanelShortcut(PanelFeatureState paramPanelFeatureState, int paramInt1, KeyEvent paramKeyEvent, int paramInt2) { // Byte code:
    //   0: aload_3
    //   1: invokevirtual isSystem : ()Z
    //   4: istore #5
    //   6: iconst_0
    //   7: istore #6
    //   9: iload #5
    //   11: ifeq -> 16
    //   14: iconst_0
    //   15: ireturn
    //   16: aload_1
    //   17: getfield isPrepared : Z
    //   20: ifne -> 36
    //   23: iload #6
    //   25: istore #5
    //   27: aload_0
    //   28: aload_1
    //   29: aload_3
    //   30: invokespecial preparePanel : (Landroid/support/v7/app/AppCompatDelegateImplV9$PanelFeatureState;Landroid/view/KeyEvent;)Z
    //   33: ifeq -> 60
    //   36: iload #6
    //   38: istore #5
    //   40: aload_1
    //   41: getfield menu : Landroid/support/v7/view/menu/MenuBuilder;
    //   44: ifnull -> 60
    //   47: aload_1
    //   48: getfield menu : Landroid/support/v7/view/menu/MenuBuilder;
    //   51: iload_2
    //   52: aload_3
    //   53: iload #4
    //   55: invokevirtual performShortcut : (ILandroid/view/KeyEvent;I)Z
    //   58: istore #5
    //   60: iload #5
    //   62: ifeq -> 85
    //   65: iload #4
    //   67: iconst_1
    //   68: iand
    //   69: ifne -> 85
    //   72: aload_0
    //   73: getfield mDecorContentParent : Landroid/support/v7/widget/DecorContentParent;
    //   76: ifnonnull -> 85
    //   79: aload_0
    //   80: aload_1
    //   81: iconst_1
    //   82: invokevirtual closePanel : (Landroid/support/v7/app/AppCompatDelegateImplV9$PanelFeatureState;Z)V
    //   85: iload #5
    //   87: ireturn }
  
  private boolean preparePanel(PanelFeatureState paramPanelFeatureState, KeyEvent paramKeyEvent) {
    int i;
    if (isDestroyed())
      return false; 
    if (paramPanelFeatureState.isPrepared)
      return true; 
    if (this.mPreparedPanel != null && this.mPreparedPanel != paramPanelFeatureState)
      closePanel(this.mPreparedPanel, false); 
    Window.Callback callback = getWindowCallback();
    if (callback != null)
      paramPanelFeatureState.createdPanelView = callback.onCreatePanelView(paramPanelFeatureState.featureId); 
    if (paramPanelFeatureState.featureId == 0 || paramPanelFeatureState.featureId == 108) {
      i = 1;
    } else {
      i = 0;
    } 
    if (i && this.mDecorContentParent != null)
      this.mDecorContentParent.setMenuPrepared(); 
    if (paramPanelFeatureState.createdPanelView == null && (!i || !(peekSupportActionBar() instanceof ToolbarActionBar))) {
      boolean bool;
      if (paramPanelFeatureState.menu == null || paramPanelFeatureState.refreshMenuContent) {
        if (paramPanelFeatureState.menu == null && (!initializePanelMenu(paramPanelFeatureState) || paramPanelFeatureState.menu == null))
          return false; 
        if (i && this.mDecorContentParent != null) {
          if (this.mActionMenuPresenterCallback == null)
            this.mActionMenuPresenterCallback = new ActionMenuPresenterCallback(); 
          this.mDecorContentParent.setMenu(paramPanelFeatureState.menu, this.mActionMenuPresenterCallback);
        } 
        paramPanelFeatureState.menu.stopDispatchingItemsChanged();
        if (!callback.onCreatePanelMenu(paramPanelFeatureState.featureId, paramPanelFeatureState.menu)) {
          paramPanelFeatureState.setMenu(null);
          if (i && this.mDecorContentParent != null)
            this.mDecorContentParent.setMenu(null, this.mActionMenuPresenterCallback); 
          return false;
        } 
        paramPanelFeatureState.refreshMenuContent = false;
      } 
      paramPanelFeatureState.menu.stopDispatchingItemsChanged();
      if (paramPanelFeatureState.frozenActionViewState != null) {
        paramPanelFeatureState.menu.restoreActionViewStates(paramPanelFeatureState.frozenActionViewState);
        paramPanelFeatureState.frozenActionViewState = null;
      } 
      if (!callback.onPreparePanel(0, paramPanelFeatureState.createdPanelView, paramPanelFeatureState.menu)) {
        if (i && this.mDecorContentParent != null)
          this.mDecorContentParent.setMenu(null, this.mActionMenuPresenterCallback); 
        paramPanelFeatureState.menu.startDispatchingItemsChanged();
        return false;
      } 
      if (paramKeyEvent != null) {
        i = paramKeyEvent.getDeviceId();
      } else {
        i = -1;
      } 
      if (KeyCharacterMap.load(i).getKeyboardType() != 1) {
        bool = true;
      } else {
        bool = false;
      } 
      paramPanelFeatureState.qwertyMode = bool;
      paramPanelFeatureState.menu.setQwertyMode(paramPanelFeatureState.qwertyMode);
      paramPanelFeatureState.menu.startDispatchingItemsChanged();
    } 
    paramPanelFeatureState.isPrepared = true;
    paramPanelFeatureState.isHandled = false;
    this.mPreparedPanel = paramPanelFeatureState;
    return true;
  }
  
  private void reopenMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean) {
    if (this.mDecorContentParent != null && this.mDecorContentParent.canShowOverflowMenu() && (!ViewConfiguration.get(this.mContext).hasPermanentMenuKey() || this.mDecorContentParent.isOverflowMenuShowPending())) {
      Window.Callback callback = getWindowCallback();
      if (!this.mDecorContentParent.isOverflowMenuShowing() || !paramBoolean) {
        if (callback != null && !isDestroyed()) {
          if (this.mInvalidatePanelMenuPosted && (this.mInvalidatePanelMenuFeatures & true) != 0) {
            this.mWindow.getDecorView().removeCallbacks(this.mInvalidatePanelMenuRunnable);
            this.mInvalidatePanelMenuRunnable.run();
          } 
          PanelFeatureState panelFeatureState1 = getPanelState(0, true);
          if (panelFeatureState1.menu != null && !panelFeatureState1.refreshMenuContent && callback.onPreparePanel(0, panelFeatureState1.createdPanelView, panelFeatureState1.menu)) {
            callback.onMenuOpened(108, panelFeatureState1.menu);
            this.mDecorContentParent.showOverflowMenu();
          } 
        } 
        return;
      } 
      this.mDecorContentParent.hideOverflowMenu();
      if (!isDestroyed()) {
        callback.onPanelClosed(108, (getPanelState(0, true)).menu);
        return;
      } 
      return;
    } 
    PanelFeatureState panelFeatureState = getPanelState(0, true);
    panelFeatureState.refreshDecorView = true;
    closePanel(panelFeatureState, false);
    openPanel(panelFeatureState, null);
  }
  
  private int sanitizeWindowFeatureId(int paramInt) {
    if (paramInt == 8) {
      Log.i("AppCompatDelegate", "You should now use the AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR id when requesting this feature.");
      return 108;
    } 
    if (paramInt == 9) {
      Log.i("AppCompatDelegate", "You should now use the AppCompatDelegate.FEATURE_SUPPORT_ACTION_BAR_OVERLAY id when requesting this feature.");
      return 109;
    } 
    return paramInt;
  }
  
  private boolean shouldInheritContext(ViewParent paramViewParent) {
    if (paramViewParent == null)
      return false; 
    View view = this.mWindow.getDecorView();
    while (true) {
      if (paramViewParent == null)
        return true; 
      if (paramViewParent != view && paramViewParent instanceof View) {
        if (ViewCompat.isAttachedToWindow((View)paramViewParent))
          return false; 
        paramViewParent = paramViewParent.getParent();
        continue;
      } 
      break;
    } 
    return false;
  }
  
  private void throwFeatureRequestIfSubDecorInstalled() {
    if (this.mSubDecorInstalled)
      throw new AndroidRuntimeException("Window feature must be requested before adding content"); 
  }
  
  public void addContentView(View paramView, ViewGroup.LayoutParams paramLayoutParams) {
    ensureSubDecor();
    ((ViewGroup)this.mSubDecor.findViewById(16908290)).addView(paramView, paramLayoutParams);
    this.mOriginalWindowCallback.onContentChanged();
  }
  
  View callActivityOnCreateView(View paramView, String paramString, Context paramContext, AttributeSet paramAttributeSet) {
    if (this.mOriginalWindowCallback instanceof LayoutInflater.Factory) {
      paramView = ((LayoutInflater.Factory)this.mOriginalWindowCallback).onCreateView(paramString, paramContext, paramAttributeSet);
      if (paramView != null)
        return paramView; 
    } 
    return null;
  }
  
  void callOnPanelClosed(int paramInt, PanelFeatureState paramPanelFeatureState, Menu paramMenu) {
    PanelFeatureState panelFeatureState = paramPanelFeatureState;
    MenuBuilder menuBuilder = paramMenu;
    if (paramMenu == null) {
      PanelFeatureState panelFeatureState1 = paramPanelFeatureState;
      if (paramPanelFeatureState == null) {
        panelFeatureState1 = paramPanelFeatureState;
        if (paramInt >= 0) {
          panelFeatureState1 = paramPanelFeatureState;
          if (paramInt < this.mPanels.length)
            panelFeatureState1 = this.mPanels[paramInt]; 
        } 
      } 
      panelFeatureState = panelFeatureState1;
      menuBuilder = paramMenu;
      if (panelFeatureState1 != null) {
        menuBuilder = panelFeatureState1.menu;
        panelFeatureState = panelFeatureState1;
      } 
    } 
    if (panelFeatureState != null && !panelFeatureState.isOpen)
      return; 
    if (!isDestroyed())
      this.mOriginalWindowCallback.onPanelClosed(paramInt, menuBuilder); 
  }
  
  void checkCloseActionMenu(MenuBuilder paramMenuBuilder) {
    if (this.mClosingActionMenu)
      return; 
    this.mClosingActionMenu = true;
    this.mDecorContentParent.dismissPopups();
    Window.Callback callback = getWindowCallback();
    if (callback != null && !isDestroyed())
      callback.onPanelClosed(108, paramMenuBuilder); 
    this.mClosingActionMenu = false;
  }
  
  void closePanel(int paramInt) { closePanel(getPanelState(paramInt, true), true); }
  
  void closePanel(PanelFeatureState paramPanelFeatureState, boolean paramBoolean) {
    if (paramBoolean && paramPanelFeatureState.featureId == 0 && this.mDecorContentParent != null && this.mDecorContentParent.isOverflowMenuShowing()) {
      checkCloseActionMenu(paramPanelFeatureState.menu);
      return;
    } 
    WindowManager windowManager = (WindowManager)this.mContext.getSystemService("window");
    if (windowManager != null && paramPanelFeatureState.isOpen && paramPanelFeatureState.decorView != null) {
      windowManager.removeView(paramPanelFeatureState.decorView);
      if (paramBoolean)
        callOnPanelClosed(paramPanelFeatureState.featureId, paramPanelFeatureState, null); 
    } 
    paramPanelFeatureState.isPrepared = false;
    paramPanelFeatureState.isHandled = false;
    paramPanelFeatureState.isOpen = false;
    paramPanelFeatureState.shownPanelView = null;
    paramPanelFeatureState.refreshDecorView = true;
    if (this.mPreparedPanel == paramPanelFeatureState)
      this.mPreparedPanel = null; 
  }
  
  public View createView(View paramView, String paramString, @NonNull Context paramContext, @NonNull AttributeSet paramAttributeSet) {
    AppCompatViewInflater appCompatViewInflater = this.mAppCompatViewInflater;
    boolean bool = false;
    if (appCompatViewInflater == null) {
      String str = this.mContext.obtainStyledAttributes(R.styleable.AppCompatTheme).getString(R.styleable.AppCompatTheme_viewInflaterClass);
      if (str == null || AppCompatViewInflater.class.getName().equals(str)) {
        this.mAppCompatViewInflater = new AppCompatViewInflater();
      } else {
        try {
          this.mAppCompatViewInflater = (AppCompatViewInflater)Class.forName(str).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (Throwable throwable) {
          StringBuilder stringBuilder = new StringBuilder();
          stringBuilder.append("Failed to instantiate custom view inflater ");
          stringBuilder.append(str);
          stringBuilder.append(". Falling back to default.");
          Log.i("AppCompatDelegate", stringBuilder.toString(), throwable);
          this.mAppCompatViewInflater = new AppCompatViewInflater();
        } 
      } 
    } 
    if (IS_PRE_LOLLIPOP) {
      if (paramAttributeSet instanceof XmlPullParser) {
        if (((XmlPullParser)paramAttributeSet).getDepth() > 1)
          bool = true; 
      } else {
        bool = shouldInheritContext((ViewParent)paramView);
      } 
    } else {
      bool = false;
    } 
    return this.mAppCompatViewInflater.createView(paramView, paramString, paramContext, paramAttributeSet, bool, IS_PRE_LOLLIPOP, true, VectorEnabledTintResources.shouldBeUsed());
  }
  
  void dismissPopups() {
    if (this.mDecorContentParent != null)
      this.mDecorContentParent.dismissPopups(); 
    if (this.mActionModePopup != null) {
      this.mWindow.getDecorView().removeCallbacks(this.mShowActionModePopup);
      if (this.mActionModePopup.isShowing())
        try {
          this.mActionModePopup.dismiss();
        } catch (IllegalArgumentException illegalArgumentException) {} 
      this.mActionModePopup = null;
    } 
    endOnGoingFadeAnimation();
    PanelFeatureState panelFeatureState = getPanelState(0, false);
    if (panelFeatureState != null && panelFeatureState.menu != null)
      panelFeatureState.menu.close(); 
  }
  
  boolean dispatchKeyEvent(KeyEvent paramKeyEvent) {
    int i = paramKeyEvent.getKeyCode();
    boolean bool = true;
    if (i == 82 && this.mOriginalWindowCallback.dispatchKeyEvent(paramKeyEvent))
      return true; 
    i = paramKeyEvent.getKeyCode();
    if (paramKeyEvent.getAction() != 0)
      bool = false; 
    return bool ? onKeyDown(i, paramKeyEvent) : onKeyUp(i, paramKeyEvent);
  }
  
  void doInvalidatePanelMenu(int paramInt) {
    PanelFeatureState panelFeatureState = getPanelState(paramInt, true);
    if (panelFeatureState.menu != null) {
      Bundle bundle = new Bundle();
      panelFeatureState.menu.saveActionViewStates(bundle);
      if (bundle.size() > 0)
        panelFeatureState.frozenActionViewState = bundle; 
      panelFeatureState.menu.stopDispatchingItemsChanged();
      panelFeatureState.menu.clear();
    } 
    panelFeatureState.refreshMenuContent = true;
    panelFeatureState.refreshDecorView = true;
    if ((paramInt == 108 || paramInt == 0) && this.mDecorContentParent != null) {
      panelFeatureState = getPanelState(0, false);
      if (panelFeatureState != null) {
        panelFeatureState.isPrepared = false;
        preparePanel(panelFeatureState, null);
      } 
    } 
  }
  
  void endOnGoingFadeAnimation() {
    if (this.mFadeAnim != null)
      this.mFadeAnim.cancel(); 
  }
  
  PanelFeatureState findMenuPanel(Menu paramMenu) {
    boolean bool;
    PanelFeatureState[] arrayOfPanelFeatureState = this.mPanels;
    byte b = 0;
    if (arrayOfPanelFeatureState != null) {
      bool = arrayOfPanelFeatureState.length;
    } else {
      bool = false;
    } 
    while (b < bool) {
      PanelFeatureState panelFeatureState = arrayOfPanelFeatureState[b];
      if (panelFeatureState != null && panelFeatureState.menu == paramMenu)
        return panelFeatureState; 
      b++;
    } 
    return null;
  }
  
  @Nullable
  public <T extends View> T findViewById(@IdRes int paramInt) {
    ensureSubDecor();
    return (T)this.mWindow.findViewById(paramInt);
  }
  
  protected PanelFeatureState getPanelState(int paramInt, boolean paramBoolean) { // Byte code:
    //   0: aload_0
    //   1: getfield mPanels : [Landroid/support/v7/app/AppCompatDelegateImplV9$PanelFeatureState;
    //   4: astore #4
    //   6: aload #4
    //   8: ifnull -> 21
    //   11: aload #4
    //   13: astore_3
    //   14: aload #4
    //   16: arraylength
    //   17: iload_1
    //   18: if_icmpgt -> 49
    //   21: iload_1
    //   22: iconst_1
    //   23: iadd
    //   24: anewarray android/support/v7/app/AppCompatDelegateImplV9$PanelFeatureState
    //   27: astore_3
    //   28: aload #4
    //   30: ifnull -> 44
    //   33: aload #4
    //   35: iconst_0
    //   36: aload_3
    //   37: iconst_0
    //   38: aload #4
    //   40: arraylength
    //   41: invokestatic arraycopy : (Ljava/lang/Object;ILjava/lang/Object;II)V
    //   44: aload_0
    //   45: aload_3
    //   46: putfield mPanels : [Landroid/support/v7/app/AppCompatDelegateImplV9$PanelFeatureState;
    //   49: aload_3
    //   50: iload_1
    //   51: aaload
    //   52: astore #5
    //   54: aload #5
    //   56: astore #4
    //   58: aload #5
    //   60: ifnonnull -> 78
    //   63: new android/support/v7/app/AppCompatDelegateImplV9$PanelFeatureState
    //   66: dup
    //   67: iload_1
    //   68: invokespecial <init> : (I)V
    //   71: astore #4
    //   73: aload_3
    //   74: iload_1
    //   75: aload #4
    //   77: aastore
    //   78: aload #4
    //   80: areturn }
  
  ViewGroup getSubDecor() { return this.mSubDecor; }
  
  public boolean hasWindowFeature(int paramInt) {
    switch (sanitizeWindowFeatureId(paramInt)) {
      default:
        return false;
      case 109:
        return this.mOverlayActionBar;
      case 108:
        return this.mHasActionBar;
      case 10:
        return this.mOverlayActionMode;
      case 5:
        return this.mFeatureIndeterminateProgress;
      case 2:
        return this.mFeatureProgress;
      case 1:
        break;
    } 
    return this.mWindowNoTitle;
  }
  
  public void initWindowDecorActionBar() {
    ensureSubDecor();
    if (this.mHasActionBar) {
      if (this.mActionBar != null)
        return; 
      if (this.mOriginalWindowCallback instanceof Activity) {
        this.mActionBar = new WindowDecorActionBar((Activity)this.mOriginalWindowCallback, this.mOverlayActionBar);
      } else if (this.mOriginalWindowCallback instanceof Dialog) {
        this.mActionBar = new WindowDecorActionBar((Dialog)this.mOriginalWindowCallback);
      } 
      if (this.mActionBar != null)
        this.mActionBar.setDefaultDisplayHomeAsUpEnabled(this.mEnableDefaultActionBarUp); 
      return;
    } 
  }
  
  public void installViewFactory() {
    LayoutInflater layoutInflater = LayoutInflater.from(this.mContext);
    if (layoutInflater.getFactory() == null) {
      LayoutInflaterCompat.setFactory2(layoutInflater, this);
      return;
    } 
    if (!(layoutInflater.getFactory2() instanceof AppCompatDelegateImplV9))
      Log.i("AppCompatDelegate", "The Activity's LayoutInflater already has a Factory installed so we can not install AppCompat's"); 
  }
  
  public void invalidateOptionsMenu() {
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null && actionBar.invalidateOptionsMenu())
      return; 
    invalidatePanelMenu(0);
  }
  
  boolean onBackPressed() {
    if (this.mActionMode != null) {
      this.mActionMode.finish();
      return true;
    } 
    ActionBar actionBar = getSupportActionBar();
    return (actionBar != null && actionBar.collapseActionView());
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration) {
    if (this.mHasActionBar && this.mSubDecorInstalled) {
      ActionBar actionBar = getSupportActionBar();
      if (actionBar != null)
        actionBar.onConfigurationChanged(paramConfiguration); 
    } 
    AppCompatDrawableManager.get().onConfigurationChanged(this.mContext);
    applyDayNight();
  }
  
  public void onCreate(Bundle paramBundle) {
    if (this.mOriginalWindowCallback instanceof Activity && NavUtils.getParentActivityName((Activity)this.mOriginalWindowCallback) != null) {
      ActionBar actionBar = peekSupportActionBar();
      if (actionBar == null) {
        this.mEnableDefaultActionBarUp = true;
        return;
      } 
      actionBar.setDefaultDisplayHomeAsUpEnabled(true);
    } 
  }
  
  public final View onCreateView(View paramView, String paramString, Context paramContext, AttributeSet paramAttributeSet) {
    View view = callActivityOnCreateView(paramView, paramString, paramContext, paramAttributeSet);
    return (view != null) ? view : createView(paramView, paramString, paramContext, paramAttributeSet);
  }
  
  public View onCreateView(String paramString, Context paramContext, AttributeSet paramAttributeSet) { return onCreateView(null, paramString, paramContext, paramAttributeSet); }
  
  public void onDestroy() {
    if (this.mInvalidatePanelMenuPosted)
      this.mWindow.getDecorView().removeCallbacks(this.mInvalidatePanelMenuRunnable); 
    super.onDestroy();
    if (this.mActionBar != null)
      this.mActionBar.onDestroy(); 
  }
  
  boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
    boolean bool = true;
    if (paramInt != 4) {
      if (paramInt != 82)
        return false; 
      onKeyDownPanel(0, paramKeyEvent);
      return true;
    } 
    if ((paramKeyEvent.getFlags() & 0x80) == 0)
      bool = false; 
    this.mLongPressBackDown = bool;
    return false;
  }
  
  boolean onKeyShortcut(int paramInt, KeyEvent paramKeyEvent) {
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null && actionBar.onKeyShortcut(paramInt, paramKeyEvent))
      return true; 
    if (this.mPreparedPanel != null && performPanelShortcut(this.mPreparedPanel, paramKeyEvent.getKeyCode(), paramKeyEvent, 1)) {
      if (this.mPreparedPanel != null)
        this.mPreparedPanel.isHandled = true; 
      return true;
    } 
    if (this.mPreparedPanel == null) {
      PanelFeatureState panelFeatureState = getPanelState(0, true);
      preparePanel(panelFeatureState, paramKeyEvent);
      boolean bool = performPanelShortcut(panelFeatureState, paramKeyEvent.getKeyCode(), paramKeyEvent, 1);
      panelFeatureState.isPrepared = false;
      if (bool)
        return true; 
    } 
    return false;
  }
  
  boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent) {
    if (paramInt != 4) {
      if (paramInt != 82)
        return false; 
      onKeyUpPanel(0, paramKeyEvent);
      return true;
    } 
    boolean bool = this.mLongPressBackDown;
    this.mLongPressBackDown = false;
    PanelFeatureState panelFeatureState = getPanelState(0, false);
    if (panelFeatureState != null && panelFeatureState.isOpen) {
      if (!bool)
        closePanel(panelFeatureState, true); 
      return true;
    } 
    return onBackPressed();
  }
  
  public boolean onMenuItemSelected(MenuBuilder paramMenuBuilder, MenuItem paramMenuItem) {
    Window.Callback callback = getWindowCallback();
    if (callback != null && !isDestroyed()) {
      PanelFeatureState panelFeatureState = findMenuPanel(paramMenuBuilder.getRootMenu());
      if (panelFeatureState != null)
        return callback.onMenuItemSelected(panelFeatureState.featureId, paramMenuItem); 
    } 
    return false;
  }
  
  public void onMenuModeChange(MenuBuilder paramMenuBuilder) { reopenMenu(paramMenuBuilder, true); }
  
  boolean onMenuOpened(int paramInt, Menu paramMenu) {
    if (paramInt == 108) {
      ActionBar actionBar = getSupportActionBar();
      if (actionBar != null)
        actionBar.dispatchMenuVisibilityChanged(true); 
      return true;
    } 
    return false;
  }
  
  void onPanelClosed(int paramInt, Menu paramMenu) {
    if (paramInt == 108) {
      ActionBar actionBar = getSupportActionBar();
      if (actionBar != null) {
        actionBar.dispatchMenuVisibilityChanged(false);
        return;
      } 
    } else if (paramInt == 0) {
      PanelFeatureState panelFeatureState = getPanelState(paramInt, true);
      if (panelFeatureState.isOpen)
        closePanel(panelFeatureState, false); 
    } 
  }
  
  public void onPostCreate(Bundle paramBundle) { ensureSubDecor(); }
  
  public void onPostResume() {
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null)
      actionBar.setShowHideAnimationEnabled(true); 
  }
  
  public void onStop() {
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null)
      actionBar.setShowHideAnimationEnabled(false); 
  }
  
  void onSubDecorInstalled(ViewGroup paramViewGroup) {}
  
  void onTitleChanged(CharSequence paramCharSequence) {
    if (this.mDecorContentParent != null) {
      this.mDecorContentParent.setWindowTitle(paramCharSequence);
      return;
    } 
    if (peekSupportActionBar() != null) {
      peekSupportActionBar().setWindowTitle(paramCharSequence);
      return;
    } 
    if (this.mTitleView != null)
      this.mTitleView.setText(paramCharSequence); 
  }
  
  public boolean requestWindowFeature(int paramInt) {
    paramInt = sanitizeWindowFeatureId(paramInt);
    if (this.mWindowNoTitle && paramInt == 108)
      return false; 
    if (this.mHasActionBar && paramInt == 1)
      this.mHasActionBar = false; 
    switch (paramInt) {
      default:
        return this.mWindow.requestFeature(paramInt);
      case 109:
        throwFeatureRequestIfSubDecorInstalled();
        this.mOverlayActionBar = true;
        return true;
      case 108:
        throwFeatureRequestIfSubDecorInstalled();
        this.mHasActionBar = true;
        return true;
      case 10:
        throwFeatureRequestIfSubDecorInstalled();
        this.mOverlayActionMode = true;
        return true;
      case 5:
        throwFeatureRequestIfSubDecorInstalled();
        this.mFeatureIndeterminateProgress = true;
        return true;
      case 2:
        throwFeatureRequestIfSubDecorInstalled();
        this.mFeatureProgress = true;
        return true;
      case 1:
        break;
    } 
    throwFeatureRequestIfSubDecorInstalled();
    this.mWindowNoTitle = true;
    return true;
  }
  
  public void setContentView(int paramInt) {
    ensureSubDecor();
    ViewGroup viewGroup = (ViewGroup)this.mSubDecor.findViewById(16908290);
    viewGroup.removeAllViews();
    LayoutInflater.from(this.mContext).inflate(paramInt, viewGroup);
    this.mOriginalWindowCallback.onContentChanged();
  }
  
  public void setContentView(View paramView) {
    ensureSubDecor();
    ViewGroup viewGroup = (ViewGroup)this.mSubDecor.findViewById(16908290);
    viewGroup.removeAllViews();
    viewGroup.addView(paramView);
    this.mOriginalWindowCallback.onContentChanged();
  }
  
  public void setContentView(View paramView, ViewGroup.LayoutParams paramLayoutParams) {
    ensureSubDecor();
    ViewGroup viewGroup = (ViewGroup)this.mSubDecor.findViewById(16908290);
    viewGroup.removeAllViews();
    viewGroup.addView(paramView, paramLayoutParams);
    this.mOriginalWindowCallback.onContentChanged();
  }
  
  public void setSupportActionBar(Toolbar paramToolbar) {
    if (!(this.mOriginalWindowCallback instanceof Activity))
      return; 
    ActionBar actionBar = getSupportActionBar();
    if (actionBar instanceof WindowDecorActionBar)
      throw new IllegalStateException("This Activity already has an action bar supplied by the window decor. Do not request Window.FEATURE_SUPPORT_ACTION_BAR and set windowActionBar to false in your theme to use a Toolbar instead."); 
    this.mMenuInflater = null;
    if (actionBar != null)
      actionBar.onDestroy(); 
    if (paramToolbar != null) {
      ToolbarActionBar toolbarActionBar = new ToolbarActionBar(paramToolbar, ((Activity)this.mOriginalWindowCallback).getTitle(), this.mAppCompatWindowCallback);
      this.mActionBar = toolbarActionBar;
      this.mWindow.setCallback(toolbarActionBar.getWrappedWindowCallback());
    } else {
      this.mActionBar = null;
      this.mWindow.setCallback(this.mAppCompatWindowCallback);
    } 
    invalidateOptionsMenu();
  }
  
  final boolean shouldAnimateActionModeView() { return (this.mSubDecorInstalled && this.mSubDecor != null && ViewCompat.isLaidOut(this.mSubDecor)); }
  
  public ActionMode startSupportActionMode(@NonNull ActionMode.Callback paramCallback) {
    if (paramCallback == null)
      throw new IllegalArgumentException("ActionMode callback can not be null."); 
    if (this.mActionMode != null)
      this.mActionMode.finish(); 
    paramCallback = new ActionModeCallbackWrapperV9(paramCallback);
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      this.mActionMode = actionBar.startActionMode(paramCallback);
      if (this.mActionMode != null && this.mAppCompatCallback != null)
        this.mAppCompatCallback.onSupportActionModeStarted(this.mActionMode); 
    } 
    if (this.mActionMode == null)
      this.mActionMode = startSupportActionModeFromWindow(paramCallback); 
    return this.mActionMode;
  }
  
  ActionMode startSupportActionModeFromWindow(@NonNull ActionMode.Callback paramCallback) { // Byte code:
    //   0: aload_0
    //   1: invokevirtual endOnGoingFadeAnimation : ()V
    //   4: aload_0
    //   5: getfield mActionMode : Landroid/support/v7/view/ActionMode;
    //   8: ifnull -> 18
    //   11: aload_0
    //   12: getfield mActionMode : Landroid/support/v7/view/ActionMode;
    //   15: invokevirtual finish : ()V
    //   18: aload_1
    //   19: astore #4
    //   21: aload_1
    //   22: instanceof android/support/v7/app/AppCompatDelegateImplV9$ActionModeCallbackWrapperV9
    //   25: ifne -> 39
    //   28: new android/support/v7/app/AppCompatDelegateImplV9$ActionModeCallbackWrapperV9
    //   31: dup
    //   32: aload_0
    //   33: aload_1
    //   34: invokespecial <init> : (Landroid/support/v7/app/AppCompatDelegateImplV9;Landroid/support/v7/view/ActionMode$Callback;)V
    //   37: astore #4
    //   39: aload_0
    //   40: getfield mAppCompatCallback : Landroid/support/v7/app/AppCompatCallback;
    //   43: ifnull -> 68
    //   46: aload_0
    //   47: invokevirtual isDestroyed : ()Z
    //   50: ifne -> 68
    //   53: aload_0
    //   54: getfield mAppCompatCallback : Landroid/support/v7/app/AppCompatCallback;
    //   57: aload #4
    //   59: invokeinterface onWindowStartingSupportActionMode : (Landroid/support/v7/view/ActionMode$Callback;)Landroid/support/v7/view/ActionMode;
    //   64: astore_1
    //   65: goto -> 70
    //   68: aconst_null
    //   69: astore_1
    //   70: aload_1
    //   71: ifnull -> 82
    //   74: aload_0
    //   75: aload_1
    //   76: putfield mActionMode : Landroid/support/v7/view/ActionMode;
    //   79: goto -> 572
    //   82: aload_0
    //   83: getfield mActionModeView : Landroid/support/v7/widget/ActionBarContextView;
    //   86: astore_1
    //   87: iconst_1
    //   88: istore_3
    //   89: aload_1
    //   90: ifnonnull -> 352
    //   93: aload_0
    //   94: getfield mIsFloating : Z
    //   97: ifeq -> 312
    //   100: new android/util/TypedValue
    //   103: dup
    //   104: invokespecial <init> : ()V
    //   107: astore #5
    //   109: aload_0
    //   110: getfield mContext : Landroid/content/Context;
    //   113: invokevirtual getTheme : ()Landroid/content/res/Resources$Theme;
    //   116: astore_1
    //   117: aload_1
    //   118: getstatic android/support/v7/appcompat/R$attr.actionBarTheme : I
    //   121: aload #5
    //   123: iconst_1
    //   124: invokevirtual resolveAttribute : (ILandroid/util/TypedValue;Z)Z
    //   127: pop
    //   128: aload #5
    //   130: getfield resourceId : I
    //   133: ifeq -> 190
    //   136: aload_0
    //   137: getfield mContext : Landroid/content/Context;
    //   140: invokevirtual getResources : ()Landroid/content/res/Resources;
    //   143: invokevirtual newTheme : ()Landroid/content/res/Resources$Theme;
    //   146: astore #6
    //   148: aload #6
    //   150: aload_1
    //   151: invokevirtual setTo : (Landroid/content/res/Resources$Theme;)V
    //   154: aload #6
    //   156: aload #5
    //   158: getfield resourceId : I
    //   161: iconst_1
    //   162: invokevirtual applyStyle : (IZ)V
    //   165: new android/support/v7/view/ContextThemeWrapper
    //   168: dup
    //   169: aload_0
    //   170: getfield mContext : Landroid/content/Context;
    //   173: iconst_0
    //   174: invokespecial <init> : (Landroid/content/Context;I)V
    //   177: astore_1
    //   178: aload_1
    //   179: invokevirtual getTheme : ()Landroid/content/res/Resources$Theme;
    //   182: aload #6
    //   184: invokevirtual setTo : (Landroid/content/res/Resources$Theme;)V
    //   187: goto -> 195
    //   190: aload_0
    //   191: getfield mContext : Landroid/content/Context;
    //   194: astore_1
    //   195: aload_0
    //   196: new android/support/v7/widget/ActionBarContextView
    //   199: dup
    //   200: aload_1
    //   201: invokespecial <init> : (Landroid/content/Context;)V
    //   204: putfield mActionModeView : Landroid/support/v7/widget/ActionBarContextView;
    //   207: aload_0
    //   208: new android/widget/PopupWindow
    //   211: dup
    //   212: aload_1
    //   213: aconst_null
    //   214: getstatic android/support/v7/appcompat/R$attr.actionModePopupWindowStyle : I
    //   217: invokespecial <init> : (Landroid/content/Context;Landroid/util/AttributeSet;I)V
    //   220: putfield mActionModePopup : Landroid/widget/PopupWindow;
    //   223: aload_0
    //   224: getfield mActionModePopup : Landroid/widget/PopupWindow;
    //   227: iconst_2
    //   228: invokestatic setWindowLayoutType : (Landroid/widget/PopupWindow;I)V
    //   231: aload_0
    //   232: getfield mActionModePopup : Landroid/widget/PopupWindow;
    //   235: aload_0
    //   236: getfield mActionModeView : Landroid/support/v7/widget/ActionBarContextView;
    //   239: invokevirtual setContentView : (Landroid/view/View;)V
    //   242: aload_0
    //   243: getfield mActionModePopup : Landroid/widget/PopupWindow;
    //   246: iconst_m1
    //   247: invokevirtual setWidth : (I)V
    //   250: aload_1
    //   251: invokevirtual getTheme : ()Landroid/content/res/Resources$Theme;
    //   254: getstatic android/support/v7/appcompat/R$attr.actionBarSize : I
    //   257: aload #5
    //   259: iconst_1
    //   260: invokevirtual resolveAttribute : (ILandroid/util/TypedValue;Z)Z
    //   263: pop
    //   264: aload #5
    //   266: getfield data : I
    //   269: aload_1
    //   270: invokevirtual getResources : ()Landroid/content/res/Resources;
    //   273: invokevirtual getDisplayMetrics : ()Landroid/util/DisplayMetrics;
    //   276: invokestatic complexToDimensionPixelSize : (ILandroid/util/DisplayMetrics;)I
    //   279: istore_2
    //   280: aload_0
    //   281: getfield mActionModeView : Landroid/support/v7/widget/ActionBarContextView;
    //   284: iload_2
    //   285: invokevirtual setContentHeight : (I)V
    //   288: aload_0
    //   289: getfield mActionModePopup : Landroid/widget/PopupWindow;
    //   292: bipush #-2
    //   294: invokevirtual setHeight : (I)V
    //   297: aload_0
    //   298: new android/support/v7/app/AppCompatDelegateImplV9$5
    //   301: dup
    //   302: aload_0
    //   303: invokespecial <init> : (Landroid/support/v7/app/AppCompatDelegateImplV9;)V
    //   306: putfield mShowActionModePopup : Ljava/lang/Runnable;
    //   309: goto -> 352
    //   312: aload_0
    //   313: getfield mSubDecor : Landroid/view/ViewGroup;
    //   316: getstatic android/support/v7/appcompat/R$id.action_mode_bar_stub : I
    //   319: invokevirtual findViewById : (I)Landroid/view/View;
    //   322: checkcast android/support/v7/widget/ViewStubCompat
    //   325: astore_1
    //   326: aload_1
    //   327: ifnull -> 352
    //   330: aload_1
    //   331: aload_0
    //   332: invokevirtual getActionBarThemedContext : ()Landroid/content/Context;
    //   335: invokestatic from : (Landroid/content/Context;)Landroid/view/LayoutInflater;
    //   338: invokevirtual setLayoutInflater : (Landroid/view/LayoutInflater;)V
    //   341: aload_0
    //   342: aload_1
    //   343: invokevirtual inflate : ()Landroid/view/View;
    //   346: checkcast android/support/v7/widget/ActionBarContextView
    //   349: putfield mActionModeView : Landroid/support/v7/widget/ActionBarContextView;
    //   352: aload_0
    //   353: getfield mActionModeView : Landroid/support/v7/widget/ActionBarContextView;
    //   356: ifnull -> 572
    //   359: aload_0
    //   360: invokevirtual endOnGoingFadeAnimation : ()V
    //   363: aload_0
    //   364: getfield mActionModeView : Landroid/support/v7/widget/ActionBarContextView;
    //   367: invokevirtual killMode : ()V
    //   370: aload_0
    //   371: getfield mActionModeView : Landroid/support/v7/widget/ActionBarContextView;
    //   374: invokevirtual getContext : ()Landroid/content/Context;
    //   377: astore_1
    //   378: aload_0
    //   379: getfield mActionModeView : Landroid/support/v7/widget/ActionBarContextView;
    //   382: astore #5
    //   384: aload_0
    //   385: getfield mActionModePopup : Landroid/widget/PopupWindow;
    //   388: ifnonnull -> 394
    //   391: goto -> 396
    //   394: iconst_0
    //   395: istore_3
    //   396: new android/support/v7/view/StandaloneActionMode
    //   399: dup
    //   400: aload_1
    //   401: aload #5
    //   403: aload #4
    //   405: iload_3
    //   406: invokespecial <init> : (Landroid/content/Context;Landroid/support/v7/widget/ActionBarContextView;Landroid/support/v7/view/ActionMode$Callback;Z)V
    //   409: astore_1
    //   410: aload #4
    //   412: aload_1
    //   413: aload_1
    //   414: invokevirtual getMenu : ()Landroid/view/Menu;
    //   417: invokeinterface onCreateActionMode : (Landroid/support/v7/view/ActionMode;Landroid/view/Menu;)Z
    //   422: ifeq -> 567
    //   425: aload_1
    //   426: invokevirtual invalidate : ()V
    //   429: aload_0
    //   430: getfield mActionModeView : Landroid/support/v7/widget/ActionBarContextView;
    //   433: aload_1
    //   434: invokevirtual initForMode : (Landroid/support/v7/view/ActionMode;)V
    //   437: aload_0
    //   438: aload_1
    //   439: putfield mActionMode : Landroid/support/v7/view/ActionMode;
    //   442: aload_0
    //   443: invokevirtual shouldAnimateActionModeView : ()Z
    //   446: ifeq -> 491
    //   449: aload_0
    //   450: getfield mActionModeView : Landroid/support/v7/widget/ActionBarContextView;
    //   453: fconst_0
    //   454: invokevirtual setAlpha : (F)V
    //   457: aload_0
    //   458: aload_0
    //   459: getfield mActionModeView : Landroid/support/v7/widget/ActionBarContextView;
    //   462: invokestatic animate : (Landroid/view/View;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    //   465: fconst_1
    //   466: invokevirtual alpha : (F)Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    //   469: putfield mFadeAnim : Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    //   472: aload_0
    //   473: getfield mFadeAnim : Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    //   476: new android/support/v7/app/AppCompatDelegateImplV9$6
    //   479: dup
    //   480: aload_0
    //   481: invokespecial <init> : (Landroid/support/v7/app/AppCompatDelegateImplV9;)V
    //   484: invokevirtual setListener : (Landroid/support/v4/view/ViewPropertyAnimatorListener;)Landroid/support/v4/view/ViewPropertyAnimatorCompat;
    //   487: pop
    //   488: goto -> 542
    //   491: aload_0
    //   492: getfield mActionModeView : Landroid/support/v7/widget/ActionBarContextView;
    //   495: fconst_1
    //   496: invokevirtual setAlpha : (F)V
    //   499: aload_0
    //   500: getfield mActionModeView : Landroid/support/v7/widget/ActionBarContextView;
    //   503: iconst_0
    //   504: invokevirtual setVisibility : (I)V
    //   507: aload_0
    //   508: getfield mActionModeView : Landroid/support/v7/widget/ActionBarContextView;
    //   511: bipush #32
    //   513: invokevirtual sendAccessibilityEvent : (I)V
    //   516: aload_0
    //   517: getfield mActionModeView : Landroid/support/v7/widget/ActionBarContextView;
    //   520: invokevirtual getParent : ()Landroid/view/ViewParent;
    //   523: instanceof android/view/View
    //   526: ifeq -> 542
    //   529: aload_0
    //   530: getfield mActionModeView : Landroid/support/v7/widget/ActionBarContextView;
    //   533: invokevirtual getParent : ()Landroid/view/ViewParent;
    //   536: checkcast android/view/View
    //   539: invokestatic requestApplyInsets : (Landroid/view/View;)V
    //   542: aload_0
    //   543: getfield mActionModePopup : Landroid/widget/PopupWindow;
    //   546: ifnull -> 572
    //   549: aload_0
    //   550: getfield mWindow : Landroid/view/Window;
    //   553: invokevirtual getDecorView : ()Landroid/view/View;
    //   556: aload_0
    //   557: getfield mShowActionModePopup : Ljava/lang/Runnable;
    //   560: invokevirtual post : (Ljava/lang/Runnable;)Z
    //   563: pop
    //   564: goto -> 572
    //   567: aload_0
    //   568: aconst_null
    //   569: putfield mActionMode : Landroid/support/v7/view/ActionMode;
    //   572: aload_0
    //   573: getfield mActionMode : Landroid/support/v7/view/ActionMode;
    //   576: ifnull -> 599
    //   579: aload_0
    //   580: getfield mAppCompatCallback : Landroid/support/v7/app/AppCompatCallback;
    //   583: ifnull -> 599
    //   586: aload_0
    //   587: getfield mAppCompatCallback : Landroid/support/v7/app/AppCompatCallback;
    //   590: aload_0
    //   591: getfield mActionMode : Landroid/support/v7/view/ActionMode;
    //   594: invokeinterface onSupportActionModeStarted : (Landroid/support/v7/view/ActionMode;)V
    //   599: aload_0
    //   600: getfield mActionMode : Landroid/support/v7/view/ActionMode;
    //   603: areturn
    //   604: astore_1
    //   605: goto -> 68
    // Exception table:
    //   from	to	target	type
    //   53	65	604	java/lang/AbstractMethodError }
  
  int updateStatusGuard(int paramInt) {
    boolean bool1;
    ActionBarContextView actionBarContextView = this.mActionModeView;
    boolean bool2 = false;
    if (actionBarContextView != null && this.mActionModeView.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
      boolean bool;
      int i;
      byte b1;
      ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams)this.mActionModeView.getLayoutParams();
      boolean bool3 = this.mActionModeView.isShown();
      byte b2 = 1;
      if (bool3) {
        byte b;
        if (this.mTempRect1 == null) {
          this.mTempRect1 = new Rect();
          this.mTempRect2 = new Rect();
        } 
        Rect rect1 = this.mTempRect1;
        Rect rect2 = this.mTempRect2;
        rect1.set(0, paramInt, 0, 0);
        ViewUtils.computeFitSystemWindows(this.mSubDecor, rect1, rect2);
        if (rect2.top == 0) {
          b1 = paramInt;
        } else {
          b1 = 0;
        } 
        if (marginLayoutParams.topMargin != b1) {
          marginLayoutParams.topMargin = paramInt;
          if (this.mStatusGuard == null) {
            this.mStatusGuard = new View(this.mContext);
            this.mStatusGuard.setBackgroundColor(this.mContext.getResources().getColor(R.color.abc_input_method_navigation_guard));
            this.mSubDecor.addView(this.mStatusGuard, -1, new ViewGroup.LayoutParams(-1, paramInt));
          } else {
            ViewGroup.LayoutParams layoutParams = this.mStatusGuard.getLayoutParams();
            if (layoutParams.height != paramInt) {
              layoutParams.height = paramInt;
              this.mStatusGuard.setLayoutParams(layoutParams);
            } 
          } 
          b = 1;
        } else {
          b = 0;
        } 
        if (this.mStatusGuard == null)
          b2 = 0; 
        bool = b;
        b1 = b2;
        i = paramInt;
        if (!this.mOverlayActionMode) {
          bool = b;
          b1 = b2;
          i = paramInt;
          if (b2 != 0) {
            i = 0;
            bool = b;
            b1 = b2;
          } 
        } 
      } else {
        boolean bool4;
        if (marginLayoutParams.topMargin != 0) {
          marginLayoutParams.topMargin = 0;
          bool4 = true;
        } else {
          bool4 = false;
        } 
        b1 = 0;
        i = paramInt;
        bool = bool4;
      } 
      bool1 = b1;
      paramInt = i;
      if (bool) {
        this.mActionModeView.setLayoutParams(marginLayoutParams);
        bool1 = b1;
        paramInt = i;
      } 
    } else {
      bool1 = false;
    } 
    if (this.mStatusGuard != null) {
      byte b;
      View view = this.mStatusGuard;
      if (bool1) {
        b = bool2;
      } else {
        b = 8;
      } 
      view.setVisibility(b);
    } 
    return paramInt;
  }
  
  private final class ActionMenuPresenterCallback implements MenuPresenter.Callback {
    public void onCloseMenu(MenuBuilder param1MenuBuilder, boolean param1Boolean) { AppCompatDelegateImplV9.this.checkCloseActionMenu(param1MenuBuilder); }
    
    public boolean onOpenSubMenu(MenuBuilder param1MenuBuilder) {
      Window.Callback callback = AppCompatDelegateImplV9.this.getWindowCallback();
      if (callback != null)
        callback.onMenuOpened(108, param1MenuBuilder); 
      return true;
    }
  }
  
  class ActionModeCallbackWrapperV9 implements ActionMode.Callback {
    private ActionMode.Callback mWrapped;
    
    public ActionModeCallbackWrapperV9(ActionMode.Callback param1Callback) { this.mWrapped = param1Callback; }
    
    public boolean onActionItemClicked(ActionMode param1ActionMode, MenuItem param1MenuItem) { return this.mWrapped.onActionItemClicked(param1ActionMode, param1MenuItem); }
    
    public boolean onCreateActionMode(ActionMode param1ActionMode, Menu param1Menu) { return this.mWrapped.onCreateActionMode(param1ActionMode, param1Menu); }
    
    public void onDestroyActionMode(ActionMode param1ActionMode) {
      this.mWrapped.onDestroyActionMode(param1ActionMode);
      if (AppCompatDelegateImplV9.this.mActionModePopup != null)
        AppCompatDelegateImplV9.this.mWindow.getDecorView().removeCallbacks(AppCompatDelegateImplV9.this.mShowActionModePopup); 
      if (AppCompatDelegateImplV9.this.mActionModeView != null) {
        AppCompatDelegateImplV9.this.endOnGoingFadeAnimation();
        AppCompatDelegateImplV9.this.mFadeAnim = ViewCompat.animate(AppCompatDelegateImplV9.this.mActionModeView).alpha(0.0F);
        AppCompatDelegateImplV9.this.mFadeAnim.setListener(new ViewPropertyAnimatorListenerAdapter() {
              public void onAnimationEnd(View param2View) {
                AppCompatDelegateImplV9.this.mActionModeView.setVisibility(8);
                if (AppCompatDelegateImplV9.this.mActionModePopup != null) {
                  AppCompatDelegateImplV9.this.mActionModePopup.dismiss();
                } else if (AppCompatDelegateImplV9.this.mActionModeView.getParent() instanceof View) {
                  ViewCompat.requestApplyInsets((View)AppCompatDelegateImplV9.this.mActionModeView.getParent());
                } 
                AppCompatDelegateImplV9.this.mActionModeView.removeAllViews();
                AppCompatDelegateImplV9.this.mFadeAnim.setListener(null);
                AppCompatDelegateImplV9.this.mFadeAnim = null;
              }
            });
      } 
      if (AppCompatDelegateImplV9.this.mAppCompatCallback != null)
        AppCompatDelegateImplV9.this.mAppCompatCallback.onSupportActionModeFinished(AppCompatDelegateImplV9.this.mActionMode); 
      AppCompatDelegateImplV9.this.mActionMode = null;
    }
    
    public boolean onPrepareActionMode(ActionMode param1ActionMode, Menu param1Menu) { return this.mWrapped.onPrepareActionMode(param1ActionMode, param1Menu); }
  }
  
  class null extends ViewPropertyAnimatorListenerAdapter {
    null() {}
    
    public void onAnimationEnd(View param1View) {
      AppCompatDelegateImplV9.this.mActionModeView.setVisibility(8);
      if (AppCompatDelegateImplV9.this.mActionModePopup != null) {
        AppCompatDelegateImplV9.this.mActionModePopup.dismiss();
      } else if (AppCompatDelegateImplV9.this.mActionModeView.getParent() instanceof View) {
        ViewCompat.requestApplyInsets((View)AppCompatDelegateImplV9.this.mActionModeView.getParent());
      } 
      AppCompatDelegateImplV9.this.mActionModeView.removeAllViews();
      AppCompatDelegateImplV9.this.mFadeAnim.setListener(null);
      AppCompatDelegateImplV9.this.mFadeAnim = null;
    }
  }
  
  private class ListMenuDecorView extends ContentFrameLayout {
    public ListMenuDecorView(Context param1Context) { super(param1Context); }
    
    private boolean isOutOfBounds(int param1Int1, int param1Int2) { return (param1Int1 < -5 || param1Int2 < -5 || param1Int1 > getWidth() + 5 || param1Int2 > getHeight() + 5); }
    
    public boolean dispatchKeyEvent(KeyEvent param1KeyEvent) { return (AppCompatDelegateImplV9.this.dispatchKeyEvent(param1KeyEvent) || super.dispatchKeyEvent(param1KeyEvent)); }
    
    public boolean onInterceptTouchEvent(MotionEvent param1MotionEvent) {
      if (param1MotionEvent.getAction() == 0 && isOutOfBounds((int)param1MotionEvent.getX(), (int)param1MotionEvent.getY())) {
        AppCompatDelegateImplV9.this.closePanel(0);
        return true;
      } 
      return super.onInterceptTouchEvent(param1MotionEvent);
    }
    
    public void setBackgroundResource(int param1Int) { setBackgroundDrawable(AppCompatResources.getDrawable(getContext(), param1Int)); }
  }
  
  protected static final class PanelFeatureState {
    int background;
    
    View createdPanelView;
    
    ViewGroup decorView;
    
    int featureId;
    
    Bundle frozenActionViewState;
    
    Bundle frozenMenuState;
    
    int gravity;
    
    boolean isHandled;
    
    boolean isOpen;
    
    boolean isPrepared;
    
    ListMenuPresenter listMenuPresenter;
    
    Context listPresenterContext;
    
    MenuBuilder menu;
    
    public boolean qwertyMode;
    
    boolean refreshDecorView;
    
    boolean refreshMenuContent;
    
    View shownPanelView;
    
    boolean wasLastOpen;
    
    int windowAnimations;
    
    int x;
    
    int y;
    
    PanelFeatureState(int param1Int) {
      this.featureId = param1Int;
      this.refreshDecorView = false;
    }
    
    void applyFrozenState() {
      if (this.menu != null && this.frozenMenuState != null) {
        this.menu.restorePresenterStates(this.frozenMenuState);
        this.frozenMenuState = null;
      } 
    }
    
    public void clearMenuPresenters() {
      if (this.menu != null)
        this.menu.removeMenuPresenter(this.listMenuPresenter); 
      this.listMenuPresenter = null;
    }
    
    MenuView getListMenuView(MenuPresenter.Callback param1Callback) {
      if (this.menu == null)
        return null; 
      if (this.listMenuPresenter == null) {
        this.listMenuPresenter = new ListMenuPresenter(this.listPresenterContext, R.layout.abc_list_menu_item_layout);
        this.listMenuPresenter.setCallback(param1Callback);
        this.menu.addMenuPresenter(this.listMenuPresenter);
      } 
      return this.listMenuPresenter.getMenuView(this.decorView);
    }
    
    public boolean hasPanelItems() {
      View view = this.shownPanelView;
      boolean bool = false;
      if (view == null)
        return false; 
      if (this.createdPanelView != null)
        return true; 
      if (this.listMenuPresenter.getAdapter().getCount() > 0)
        bool = true; 
      return bool;
    }
    
    void onRestoreInstanceState(Parcelable param1Parcelable) {
      param1Parcelable = (SavedState)param1Parcelable;
      this.featureId = param1Parcelable.featureId;
      this.wasLastOpen = param1Parcelable.isOpen;
      this.frozenMenuState = param1Parcelable.menuState;
      this.shownPanelView = null;
      this.decorView = null;
    }
    
    Parcelable onSaveInstanceState() {
      SavedState savedState = new SavedState();
      savedState.featureId = this.featureId;
      savedState.isOpen = this.isOpen;
      if (this.menu != null) {
        savedState.menuState = new Bundle();
        this.menu.savePresenterStates(savedState.menuState);
      } 
      return savedState;
    }
    
    void setMenu(MenuBuilder param1MenuBuilder) {
      if (param1MenuBuilder == this.menu)
        return; 
      if (this.menu != null)
        this.menu.removeMenuPresenter(this.listMenuPresenter); 
      this.menu = param1MenuBuilder;
      if (param1MenuBuilder != null && this.listMenuPresenter != null)
        param1MenuBuilder.addMenuPresenter(this.listMenuPresenter); 
    }
    
    void setStyle(Context param1Context) {
      TypedValue typedValue = new TypedValue();
      Resources.Theme theme = param1Context.getResources().newTheme();
      theme.setTo(param1Context.getTheme());
      theme.resolveAttribute(R.attr.actionBarPopupTheme, typedValue, true);
      if (typedValue.resourceId != 0)
        theme.applyStyle(typedValue.resourceId, true); 
      theme.resolveAttribute(R.attr.panelMenuListTheme, typedValue, true);
      if (typedValue.resourceId != 0) {
        theme.applyStyle(typedValue.resourceId, true);
      } else {
        theme.applyStyle(R.style.Theme_AppCompat_CompactMenu, true);
      } 
      ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(param1Context, 0);
      contextThemeWrapper.getTheme().setTo(theme);
      this.listPresenterContext = contextThemeWrapper;
      TypedArray typedArray = contextThemeWrapper.obtainStyledAttributes(R.styleable.AppCompatTheme);
      this.background = typedArray.getResourceId(R.styleable.AppCompatTheme_panelBackground, 0);
      this.windowAnimations = typedArray.getResourceId(R.styleable.AppCompatTheme_android_windowAnimationStyle, 0);
      typedArray.recycle();
    }
    
    private static class SavedState implements Parcelable {
      public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() {
          public AppCompatDelegateImplV9.PanelFeatureState.SavedState createFromParcel(Parcel param3Parcel) { return AppCompatDelegateImplV9.PanelFeatureState.SavedState.readFromParcel(param3Parcel, null); }
          
          public AppCompatDelegateImplV9.PanelFeatureState.SavedState createFromParcel(Parcel param3Parcel, ClassLoader param3ClassLoader) { return AppCompatDelegateImplV9.PanelFeatureState.SavedState.readFromParcel(param3Parcel, param3ClassLoader); }
          
          public AppCompatDelegateImplV9.PanelFeatureState.SavedState[] newArray(int param3Int) { return new AppCompatDelegateImplV9.PanelFeatureState.SavedState[param3Int]; }
        };
      
      int featureId;
      
      boolean isOpen;
      
      Bundle menuState;
      
      static SavedState readFromParcel(Parcel param2Parcel, ClassLoader param2ClassLoader) {
        SavedState savedState = new SavedState();
        savedState.featureId = param2Parcel.readInt();
        int i = param2Parcel.readInt();
        boolean bool = true;
        if (i != 1)
          bool = false; 
        savedState.isOpen = bool;
        if (savedState.isOpen)
          savedState.menuState = param2Parcel.readBundle(param2ClassLoader); 
        return savedState;
      }
      
      public int describeContents() { return 0; }
      
      public void writeToParcel(Parcel param2Parcel, int param2Int) { throw new RuntimeException("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.provideAs(TypeTransformer.java:780)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.e1expr(TypeTransformer.java:496)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:713)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.enexpr(TypeTransformer.java:698)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:719)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.s1stmt(TypeTransformer.java:810)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.sxStmt(TypeTransformer.java:840)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:206)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n"); }
    }
    
    static final class null extends Object implements Parcelable.ClassLoaderCreator<SavedState> {
      public AppCompatDelegateImplV9.PanelFeatureState.SavedState createFromParcel(Parcel param2Parcel) { return AppCompatDelegateImplV9.PanelFeatureState.SavedState.readFromParcel(param2Parcel, null); }
      
      public AppCompatDelegateImplV9.PanelFeatureState.SavedState createFromParcel(Parcel param2Parcel, ClassLoader param2ClassLoader) { return AppCompatDelegateImplV9.PanelFeatureState.SavedState.readFromParcel(param2Parcel, param2ClassLoader); }
      
      public AppCompatDelegateImplV9.PanelFeatureState.SavedState[] newArray(int param2Int) { return new AppCompatDelegateImplV9.PanelFeatureState.SavedState[param2Int]; }
    }
  }
  
  private static class SavedState implements Parcelable {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() {
        public AppCompatDelegateImplV9.PanelFeatureState.SavedState createFromParcel(Parcel param3Parcel) { return AppCompatDelegateImplV9.PanelFeatureState.SavedState.readFromParcel(param3Parcel, null); }
        
        public AppCompatDelegateImplV9.PanelFeatureState.SavedState createFromParcel(Parcel param3Parcel, ClassLoader param3ClassLoader) { return AppCompatDelegateImplV9.PanelFeatureState.SavedState.readFromParcel(param3Parcel, param3ClassLoader); }
        
        public AppCompatDelegateImplV9.PanelFeatureState.SavedState[] newArray(int param3Int) { return new AppCompatDelegateImplV9.PanelFeatureState.SavedState[param3Int]; }
      };
    
    int featureId;
    
    boolean isOpen;
    
    Bundle menuState;
    
    static SavedState readFromParcel(Parcel param1Parcel, ClassLoader param1ClassLoader) {
      SavedState savedState = new SavedState();
      savedState.featureId = param1Parcel.readInt();
      int i = param1Parcel.readInt();
      boolean bool = true;
      if (i != 1)
        bool = false; 
      savedState.isOpen = bool;
      if (savedState.isOpen)
        savedState.menuState = param1Parcel.readBundle(param1ClassLoader); 
      return savedState;
    }
    
    public int describeContents() { return 0; }
    
    public void writeToParcel(Parcel param1Parcel, int param1Int) { throw new RuntimeException("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.provideAs(TypeTransformer.java:780)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.e1expr(TypeTransformer.java:496)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:713)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.enexpr(TypeTransformer.java:698)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:719)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.s1stmt(TypeTransformer.java:810)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.sxStmt(TypeTransformer.java:840)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:206)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n"); }
  }
  
  static final class null extends Object implements Parcelable.ClassLoaderCreator<PanelFeatureState.SavedState> {
    public AppCompatDelegateImplV9.PanelFeatureState.SavedState createFromParcel(Parcel param1Parcel) { return AppCompatDelegateImplV9.PanelFeatureState.SavedState.readFromParcel(param1Parcel, null); }
    
    public AppCompatDelegateImplV9.PanelFeatureState.SavedState createFromParcel(Parcel param1Parcel, ClassLoader param1ClassLoader) { return AppCompatDelegateImplV9.PanelFeatureState.SavedState.readFromParcel(param1Parcel, param1ClassLoader); }
    
    public AppCompatDelegateImplV9.PanelFeatureState.SavedState[] newArray(int param1Int) { return new AppCompatDelegateImplV9.PanelFeatureState.SavedState[param1Int]; }
  }
  
  private final class PanelMenuPresenterCallback implements MenuPresenter.Callback {
    public void onCloseMenu(MenuBuilder param1MenuBuilder, boolean param1Boolean) {
      boolean bool;
      MenuBuilder menuBuilder = param1MenuBuilder.getRootMenu();
      if (menuBuilder != param1MenuBuilder) {
        bool = true;
      } else {
        bool = false;
      } 
      AppCompatDelegateImplV9 appCompatDelegateImplV9 = AppCompatDelegateImplV9.this;
      if (bool)
        param1MenuBuilder = menuBuilder; 
      AppCompatDelegateImplV9.PanelFeatureState panelFeatureState = appCompatDelegateImplV9.findMenuPanel(param1MenuBuilder);
      if (panelFeatureState != null) {
        if (bool) {
          AppCompatDelegateImplV9.this.callOnPanelClosed(panelFeatureState.featureId, panelFeatureState, menuBuilder);
          AppCompatDelegateImplV9.this.closePanel(panelFeatureState, true);
          return;
        } 
        AppCompatDelegateImplV9.this.closePanel(panelFeatureState, param1Boolean);
      } 
    }
    
    public boolean onOpenSubMenu(MenuBuilder param1MenuBuilder) {
      if (param1MenuBuilder == null && AppCompatDelegateImplV9.this.mHasActionBar) {
        Window.Callback callback = AppCompatDelegateImplV9.this.getWindowCallback();
        if (callback != null && !AppCompatDelegateImplV9.this.isDestroyed())
          callback.onMenuOpened(108, param1MenuBuilder); 
      } 
      return true;
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/app/AppCompatDelegateImplV9.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */