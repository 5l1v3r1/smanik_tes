package android.support.v7.widget;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.support.annotation.RestrictTo;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.appcompat.R;
import android.support.v7.view.menu.ShowableListMenu;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class ActivityChooserView extends ViewGroup implements ActivityChooserModel.ActivityChooserModelClient {
  private static final String LOG_TAG = "ActivityChooserView";
  
  private final View mActivityChooserContent;
  
  private final Drawable mActivityChooserContentBackground;
  
  final ActivityChooserViewAdapter mAdapter;
  
  private final Callbacks mCallbacks;
  
  private int mDefaultActionButtonContentDescription;
  
  final FrameLayout mDefaultActivityButton;
  
  private final ImageView mDefaultActivityButtonImage;
  
  final FrameLayout mExpandActivityOverflowButton;
  
  private final ImageView mExpandActivityOverflowButtonImage;
  
  int mInitialActivityCount = 4;
  
  private boolean mIsAttachedToWindow;
  
  boolean mIsSelectingDefaultActivity;
  
  private final int mListPopupMaxWidth;
  
  private ListPopupWindow mListPopupWindow;
  
  final DataSetObserver mModelDataSetObserver = new DataSetObserver() {
      public void onChanged() {
        super.onChanged();
        ActivityChooserView.this.mAdapter.notifyDataSetChanged();
      }
      
      public void onInvalidated() {
        super.onInvalidated();
        ActivityChooserView.this.mAdapter.notifyDataSetInvalidated();
      }
    };
  
  PopupWindow.OnDismissListener mOnDismissListener;
  
  private final ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
      public void onGlobalLayout() {
        if (ActivityChooserView.this.isShowingPopup()) {
          if (!ActivityChooserView.this.isShown()) {
            ActivityChooserView.this.getListPopupWindow().dismiss();
            return;
          } 
          ActivityChooserView.this.getListPopupWindow().show();
          if (ActivityChooserView.this.mProvider != null)
            ActivityChooserView.this.mProvider.subUiVisibilityChanged(true); 
        } 
      }
    };
  
  ActionProvider mProvider;
  
  public ActivityChooserView(Context paramContext) { this(paramContext, null); }
  
  public ActivityChooserView(Context paramContext, AttributeSet paramAttributeSet) { this(paramContext, paramAttributeSet, 0); }
  
  public ActivityChooserView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
    super(paramContext, paramAttributeSet, paramInt);
    TypedArray typedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ActivityChooserView, paramInt, 0);
    this.mInitialActivityCount = typedArray.getInt(R.styleable.ActivityChooserView_initialActivityCount, 4);
    Drawable drawable = typedArray.getDrawable(R.styleable.ActivityChooserView_expandActivityOverflowButtonDrawable);
    typedArray.recycle();
    LayoutInflater.from(getContext()).inflate(R.layout.abc_activity_chooser_view, this, true);
    this.mCallbacks = new Callbacks();
    this.mActivityChooserContent = findViewById(R.id.activity_chooser_view_content);
    this.mActivityChooserContentBackground = this.mActivityChooserContent.getBackground();
    this.mDefaultActivityButton = (FrameLayout)findViewById(R.id.default_activity_button);
    this.mDefaultActivityButton.setOnClickListener(this.mCallbacks);
    this.mDefaultActivityButton.setOnLongClickListener(this.mCallbacks);
    this.mDefaultActivityButtonImage = (ImageView)this.mDefaultActivityButton.findViewById(R.id.image);
    FrameLayout frameLayout = (FrameLayout)findViewById(R.id.expand_activities_button);
    frameLayout.setOnClickListener(this.mCallbacks);
    frameLayout.setAccessibilityDelegate(new View.AccessibilityDelegate() {
          public void onInitializeAccessibilityNodeInfo(View param1View, AccessibilityNodeInfo param1AccessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(param1View, param1AccessibilityNodeInfo);
            AccessibilityNodeInfoCompat.wrap(param1AccessibilityNodeInfo).setCanOpenPopup(true);
          }
        });
    frameLayout.setOnTouchListener(new ForwardingListener(frameLayout) {
          public ShowableListMenu getPopup() { return ActivityChooserView.this.getListPopupWindow(); }
          
          protected boolean onForwardingStarted() {
            ActivityChooserView.this.showPopup();
            return true;
          }
          
          protected boolean onForwardingStopped() {
            ActivityChooserView.this.dismissPopup();
            return true;
          }
        });
    this.mExpandActivityOverflowButton = frameLayout;
    this.mExpandActivityOverflowButtonImage = (ImageView)frameLayout.findViewById(R.id.image);
    this.mExpandActivityOverflowButtonImage.setImageDrawable(drawable);
    this.mAdapter = new ActivityChooserViewAdapter();
    this.mAdapter.registerDataSetObserver(new DataSetObserver() {
          public void onChanged() {
            super.onChanged();
            ActivityChooserView.this.updateAppearance();
          }
        });
    Resources resources = paramContext.getResources();
    this.mListPopupMaxWidth = Math.max((resources.getDisplayMetrics()).widthPixels / 2, resources.getDimensionPixelSize(R.dimen.abc_config_prefDialogWidth));
  }
  
  public boolean dismissPopup() {
    if (isShowingPopup()) {
      getListPopupWindow().dismiss();
      ViewTreeObserver viewTreeObserver = getViewTreeObserver();
      if (viewTreeObserver.isAlive())
        viewTreeObserver.removeGlobalOnLayoutListener(this.mOnGlobalLayoutListener); 
    } 
    return true;
  }
  
  public ActivityChooserModel getDataModel() { return this.mAdapter.getDataModel(); }
  
  ListPopupWindow getListPopupWindow() {
    if (this.mListPopupWindow == null) {
      this.mListPopupWindow = new ListPopupWindow(getContext());
      this.mListPopupWindow.setAdapter(this.mAdapter);
      this.mListPopupWindow.setAnchorView(this);
      this.mListPopupWindow.setModal(true);
      this.mListPopupWindow.setOnItemClickListener(this.mCallbacks);
      this.mListPopupWindow.setOnDismissListener(this.mCallbacks);
    } 
    return this.mListPopupWindow;
  }
  
  public boolean isShowingPopup() { return getListPopupWindow().isShowing(); }
  
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    ActivityChooserModel activityChooserModel = this.mAdapter.getDataModel();
    if (activityChooserModel != null)
      activityChooserModel.registerObserver(this.mModelDataSetObserver); 
    this.mIsAttachedToWindow = true;
  }
  
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    ActivityChooserModel activityChooserModel = this.mAdapter.getDataModel();
    if (activityChooserModel != null)
      activityChooserModel.unregisterObserver(this.mModelDataSetObserver); 
    ViewTreeObserver viewTreeObserver = getViewTreeObserver();
    if (viewTreeObserver.isAlive())
      viewTreeObserver.removeGlobalOnLayoutListener(this.mOnGlobalLayoutListener); 
    if (isShowingPopup())
      dismissPopup(); 
    this.mIsAttachedToWindow = false;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.mActivityChooserContent.layout(0, 0, paramInt3 - paramInt1, paramInt4 - paramInt2);
    if (!isShowingPopup())
      dismissPopup(); 
  }
  
  protected void onMeasure(int paramInt1, int paramInt2) {
    View view = this.mActivityChooserContent;
    int i = paramInt2;
    if (this.mDefaultActivityButton.getVisibility() != 0)
      i = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt2), 1073741824); 
    measureChild(view, paramInt1, i);
    setMeasuredDimension(view.getMeasuredWidth(), view.getMeasuredHeight());
  }
  
  public void setActivityChooserModel(ActivityChooserModel paramActivityChooserModel) {
    this.mAdapter.setDataModel(paramActivityChooserModel);
    if (isShowingPopup()) {
      dismissPopup();
      showPopup();
    } 
  }
  
  public void setDefaultActionButtonContentDescription(int paramInt) { this.mDefaultActionButtonContentDescription = paramInt; }
  
  public void setExpandActivityOverflowButtonContentDescription(int paramInt) {
    String str = getContext().getString(paramInt);
    this.mExpandActivityOverflowButtonImage.setContentDescription(str);
  }
  
  public void setExpandActivityOverflowButtonDrawable(Drawable paramDrawable) { this.mExpandActivityOverflowButtonImage.setImageDrawable(paramDrawable); }
  
  public void setInitialActivityCount(int paramInt) { this.mInitialActivityCount = paramInt; }
  
  public void setOnDismissListener(PopupWindow.OnDismissListener paramOnDismissListener) { this.mOnDismissListener = paramOnDismissListener; }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public void setProvider(ActionProvider paramActionProvider) { this.mProvider = paramActionProvider; }
  
  public boolean showPopup() {
    if (!isShowingPopup()) {
      if (!this.mIsAttachedToWindow)
        return false; 
      this.mIsSelectingDefaultActivity = false;
      showPopupUnchecked(this.mInitialActivityCount);
      return true;
    } 
    return false;
  }
  
  void showPopupUnchecked(int paramInt) { throw new RuntimeException("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.useAs(TypeTransformer.java:868)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.enexpr(TypeTransformer.java:668)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:719)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.s1stmt(TypeTransformer.java:810)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.sxStmt(TypeTransformer.java:840)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:206)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n"); }
  
  void updateAppearance() {
    if (this.mAdapter.getCount() > 0) {
      this.mExpandActivityOverflowButton.setEnabled(true);
    } else {
      this.mExpandActivityOverflowButton.setEnabled(false);
    } 
    int i = this.mAdapter.getActivityCount();
    int j = this.mAdapter.getHistorySize();
    if (i == 1 || (i > 1 && j > 0)) {
      this.mDefaultActivityButton.setVisibility(0);
      ResolveInfo resolveInfo = this.mAdapter.getDefaultActivity();
      PackageManager packageManager = getContext().getPackageManager();
      this.mDefaultActivityButtonImage.setImageDrawable(resolveInfo.loadIcon(packageManager));
      if (this.mDefaultActionButtonContentDescription != 0) {
        CharSequence charSequence = resolveInfo.loadLabel(packageManager);
        charSequence = getContext().getString(this.mDefaultActionButtonContentDescription, new Object[] { charSequence });
        this.mDefaultActivityButton.setContentDescription(charSequence);
      } 
    } else {
      this.mDefaultActivityButton.setVisibility(8);
    } 
    if (this.mDefaultActivityButton.getVisibility() == 0) {
      this.mActivityChooserContent.setBackgroundDrawable(this.mActivityChooserContentBackground);
      return;
    } 
    this.mActivityChooserContent.setBackgroundDrawable(null);
  }
  
  private class ActivityChooserViewAdapter extends BaseAdapter {
    private static final int ITEM_VIEW_TYPE_ACTIVITY = 0;
    
    private static final int ITEM_VIEW_TYPE_COUNT = 3;
    
    private static final int ITEM_VIEW_TYPE_FOOTER = 1;
    
    public static final int MAX_ACTIVITY_COUNT_DEFAULT = 4;
    
    public static final int MAX_ACTIVITY_COUNT_UNLIMITED = 2147483647;
    
    private ActivityChooserModel mDataModel;
    
    private boolean mHighlightDefaultActivity;
    
    private int mMaxActivityCount = 4;
    
    private boolean mShowDefaultActivity;
    
    private boolean mShowFooterView;
    
    public int getActivityCount() { return this.mDataModel.getActivityCount(); }
    
    public int getCount() {
      int j = this.mDataModel.getActivityCount();
      int i = j;
      if (!this.mShowDefaultActivity) {
        i = j;
        if (this.mDataModel.getDefaultActivity() != null)
          i = j - 1; 
      } 
      j = Math.min(i, this.mMaxActivityCount);
      i = j;
      if (this.mShowFooterView)
        i = j + 1; 
      return i;
    }
    
    public ActivityChooserModel getDataModel() { return this.mDataModel; }
    
    public ResolveInfo getDefaultActivity() { return this.mDataModel.getDefaultActivity(); }
    
    public int getHistorySize() { return this.mDataModel.getHistorySize(); }
    
    public Object getItem(int param1Int) {
      switch (getItemViewType(param1Int)) {
        default:
          throw new IllegalArgumentException();
        case 1:
          return null;
        case 0:
          break;
      } 
      int i = param1Int;
      if (!this.mShowDefaultActivity) {
        i = param1Int;
        if (this.mDataModel.getDefaultActivity() != null)
          i = param1Int + 1; 
      } 
      return this.mDataModel.getActivity(i);
    }
    
    public long getItemId(int param1Int) { return param1Int; }
    
    public int getItemViewType(int param1Int) { return (this.mShowFooterView && param1Int == getCount() - 1) ? 1 : 0; }
    
    public boolean getShowDefaultActivity() { return this.mShowDefaultActivity; }
    
    public View getView(int param1Int, View param1View, ViewGroup param1ViewGroup) { // Byte code:
      //   0: aload_0
      //   1: iload_1
      //   2: invokevirtual getItemViewType : (I)I
      //   5: tableswitch default -> 28, 0 -> 107, 1 -> 36
      //   28: new java/lang/IllegalArgumentException
      //   31: dup
      //   32: invokespecial <init> : ()V
      //   35: athrow
      //   36: aload_2
      //   37: ifnull -> 51
      //   40: aload_2
      //   41: astore #4
      //   43: aload_2
      //   44: invokevirtual getId : ()I
      //   47: iconst_1
      //   48: if_icmpeq -> 104
      //   51: aload_0
      //   52: getfield this$0 : Landroid/support/v7/widget/ActivityChooserView;
      //   55: invokevirtual getContext : ()Landroid/content/Context;
      //   58: invokestatic from : (Landroid/content/Context;)Landroid/view/LayoutInflater;
      //   61: getstatic android/support/v7/appcompat/R$layout.abc_activity_chooser_view_list_item : I
      //   64: aload_3
      //   65: iconst_0
      //   66: invokevirtual inflate : (ILandroid/view/ViewGroup;Z)Landroid/view/View;
      //   69: astore #4
      //   71: aload #4
      //   73: iconst_1
      //   74: invokevirtual setId : (I)V
      //   77: aload #4
      //   79: getstatic android/support/v7/appcompat/R$id.title : I
      //   82: invokevirtual findViewById : (I)Landroid/view/View;
      //   85: checkcast android/widget/TextView
      //   88: aload_0
      //   89: getfield this$0 : Landroid/support/v7/widget/ActivityChooserView;
      //   92: invokevirtual getContext : ()Landroid/content/Context;
      //   95: getstatic android/support/v7/appcompat/R$string.abc_activity_chooser_view_see_all : I
      //   98: invokevirtual getString : (I)Ljava/lang/String;
      //   101: invokevirtual setText : (Ljava/lang/CharSequence;)V
      //   104: aload #4
      //   106: areturn
      //   107: aload_2
      //   108: ifnull -> 124
      //   111: aload_2
      //   112: astore #4
      //   114: aload_2
      //   115: invokevirtual getId : ()I
      //   118: getstatic android/support/v7/appcompat/R$id.list_item : I
      //   121: if_icmpeq -> 144
      //   124: aload_0
      //   125: getfield this$0 : Landroid/support/v7/widget/ActivityChooserView;
      //   128: invokevirtual getContext : ()Landroid/content/Context;
      //   131: invokestatic from : (Landroid/content/Context;)Landroid/view/LayoutInflater;
      //   134: getstatic android/support/v7/appcompat/R$layout.abc_activity_chooser_view_list_item : I
      //   137: aload_3
      //   138: iconst_0
      //   139: invokevirtual inflate : (ILandroid/view/ViewGroup;Z)Landroid/view/View;
      //   142: astore #4
      //   144: aload_0
      //   145: getfield this$0 : Landroid/support/v7/widget/ActivityChooserView;
      //   148: invokevirtual getContext : ()Landroid/content/Context;
      //   151: invokevirtual getPackageManager : ()Landroid/content/pm/PackageManager;
      //   154: astore_2
      //   155: aload #4
      //   157: getstatic android/support/v7/appcompat/R$id.icon : I
      //   160: invokevirtual findViewById : (I)Landroid/view/View;
      //   163: checkcast android/widget/ImageView
      //   166: astore_3
      //   167: aload_0
      //   168: iload_1
      //   169: invokevirtual getItem : (I)Ljava/lang/Object;
      //   172: checkcast android/content/pm/ResolveInfo
      //   175: astore #5
      //   177: aload_3
      //   178: aload #5
      //   180: aload_2
      //   181: invokevirtual loadIcon : (Landroid/content/pm/PackageManager;)Landroid/graphics/drawable/Drawable;
      //   184: invokevirtual setImageDrawable : (Landroid/graphics/drawable/Drawable;)V
      //   187: aload #4
      //   189: getstatic android/support/v7/appcompat/R$id.title : I
      //   192: invokevirtual findViewById : (I)Landroid/view/View;
      //   195: checkcast android/widget/TextView
      //   198: aload #5
      //   200: aload_2
      //   201: invokevirtual loadLabel : (Landroid/content/pm/PackageManager;)Ljava/lang/CharSequence;
      //   204: invokevirtual setText : (Ljava/lang/CharSequence;)V
      //   207: aload_0
      //   208: getfield mShowDefaultActivity : Z
      //   211: ifeq -> 234
      //   214: iload_1
      //   215: ifne -> 234
      //   218: aload_0
      //   219: getfield mHighlightDefaultActivity : Z
      //   222: ifeq -> 234
      //   225: aload #4
      //   227: iconst_1
      //   228: invokevirtual setActivated : (Z)V
      //   231: aload #4
      //   233: areturn
      //   234: aload #4
      //   236: iconst_0
      //   237: invokevirtual setActivated : (Z)V
      //   240: aload #4
      //   242: areturn }
    
    public int getViewTypeCount() { return 3; }
    
    public int measureContentWidth() {
      int j = this.mMaxActivityCount;
      this.mMaxActivityCount = Integer.MAX_VALUE;
      byte b = 0;
      int k = View.MeasureSpec.makeMeasureSpec(0, 0);
      int m = View.MeasureSpec.makeMeasureSpec(0, 0);
      int n = getCount();
      View view = null;
      int i = 0;
      while (b < n) {
        view = getView(b, view, null);
        view.measure(k, m);
        i = Math.max(i, view.getMeasuredWidth());
        b++;
      } 
      this.mMaxActivityCount = j;
      return i;
    }
    
    public void setDataModel(ActivityChooserModel param1ActivityChooserModel) {
      ActivityChooserModel activityChooserModel = ActivityChooserView.this.mAdapter.getDataModel();
      if (activityChooserModel != null && ActivityChooserView.this.isShown())
        activityChooserModel.unregisterObserver(ActivityChooserView.this.mModelDataSetObserver); 
      this.mDataModel = param1ActivityChooserModel;
      if (param1ActivityChooserModel != null && ActivityChooserView.this.isShown())
        param1ActivityChooserModel.registerObserver(ActivityChooserView.this.mModelDataSetObserver); 
      notifyDataSetChanged();
    }
    
    public void setMaxActivityCount(int param1Int) {
      if (this.mMaxActivityCount != param1Int) {
        this.mMaxActivityCount = param1Int;
        notifyDataSetChanged();
      } 
    }
    
    public void setShowDefaultActivity(boolean param1Boolean1, boolean param1Boolean2) {
      if (this.mShowDefaultActivity != param1Boolean1 || this.mHighlightDefaultActivity != param1Boolean2) {
        this.mShowDefaultActivity = param1Boolean1;
        this.mHighlightDefaultActivity = param1Boolean2;
        notifyDataSetChanged();
      } 
    }
    
    public void setShowFooterView(boolean param1Boolean) {
      if (this.mShowFooterView != param1Boolean) {
        this.mShowFooterView = param1Boolean;
        notifyDataSetChanged();
      } 
    }
  }
  
  private class Callbacks implements AdapterView.OnItemClickListener, View.OnClickListener, View.OnLongClickListener, PopupWindow.OnDismissListener {
    private void notifyOnDismissListener() {
      if (ActivityChooserView.this.mOnDismissListener != null)
        ActivityChooserView.this.mOnDismissListener.onDismiss(); 
    }
    
    public void onClick(View param1View) {
      Intent intent;
      if (param1View == ActivityChooserView.this.mDefaultActivityButton) {
        ActivityChooserView.this.dismissPopup();
        ResolveInfo resolveInfo = ActivityChooserView.this.mAdapter.getDefaultActivity();
        int i = ActivityChooserView.this.mAdapter.getDataModel().getActivityIndex(resolveInfo);
        intent = ActivityChooserView.this.mAdapter.getDataModel().chooseActivity(i);
        if (intent != null) {
          intent.addFlags(524288);
          ActivityChooserView.this.getContext().startActivity(intent);
          return;
        } 
      } else {
        if (intent == ActivityChooserView.this.mExpandActivityOverflowButton) {
          ActivityChooserView.this.mIsSelectingDefaultActivity = false;
          ActivityChooserView.this.showPopupUnchecked(ActivityChooserView.this.mInitialActivityCount);
          return;
        } 
        throw new IllegalArgumentException();
      } 
    }
    
    public void onDismiss() {
      notifyOnDismissListener();
      if (ActivityChooserView.this.mProvider != null)
        ActivityChooserView.this.mProvider.subUiVisibilityChanged(false); 
    }
    
    public void onItemClick(AdapterView<?> param1AdapterView, View param1View, int param1Int, long param1Long) {
      switch (((ActivityChooserView.ActivityChooserViewAdapter)param1AdapterView.getAdapter()).getItemViewType(param1Int)) {
        default:
          throw new IllegalArgumentException();
        case 1:
          ActivityChooserView.this.showPopupUnchecked(2147483647);
          return;
        case 0:
          break;
      } 
      ActivityChooserView.this.dismissPopup();
      if (ActivityChooserView.this.mIsSelectingDefaultActivity) {
        if (param1Int > 0) {
          ActivityChooserView.this.mAdapter.getDataModel().setDefaultActivity(param1Int);
          return;
        } 
      } else {
        if (!ActivityChooserView.this.mAdapter.getShowDefaultActivity())
          param1Int++; 
        Intent intent = ActivityChooserView.this.mAdapter.getDataModel().chooseActivity(param1Int);
        if (intent != null) {
          intent.addFlags(524288);
          ActivityChooserView.this.getContext().startActivity(intent);
        } 
      } 
    }
    
    public boolean onLongClick(View param1View) {
      if (param1View == ActivityChooserView.this.mDefaultActivityButton) {
        if (ActivityChooserView.this.mAdapter.getCount() > 0) {
          ActivityChooserView.this.mIsSelectingDefaultActivity = true;
          ActivityChooserView.this.showPopupUnchecked(ActivityChooserView.this.mInitialActivityCount);
        } 
        return true;
      } 
      throw new IllegalArgumentException();
    }
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public static class InnerLayout extends LinearLayout {
    private static final int[] TINT_ATTRS = { 16842964 };
    
    public InnerLayout(Context param1Context, AttributeSet param1AttributeSet) {
      super(param1Context, param1AttributeSet);
      TintTypedArray tintTypedArray = TintTypedArray.obtainStyledAttributes(param1Context, param1AttributeSet, TINT_ATTRS);
      setBackgroundDrawable(tintTypedArray.getDrawable(0));
      tintTypedArray.recycle();
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/ActivityChooserView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */