package android.support.v7.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.appcompat.R;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewStub;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import java.lang.ref.WeakReference;

class AlertController {
  ListAdapter mAdapter;
  
  private int mAlertDialogLayout;
  
  private final View.OnClickListener mButtonHandler = new View.OnClickListener() {
      public void onClick(View param1View) {
        Message message;
        if (param1View == AlertController.this.mButtonPositive && AlertController.this.mButtonPositiveMessage != null) {
          message = Message.obtain(AlertController.this.mButtonPositiveMessage);
        } else if (message == AlertController.this.mButtonNegative && AlertController.this.mButtonNegativeMessage != null) {
          message = Message.obtain(AlertController.this.mButtonNegativeMessage);
        } else if (message == AlertController.this.mButtonNeutral && AlertController.this.mButtonNeutralMessage != null) {
          message = Message.obtain(AlertController.this.mButtonNeutralMessage);
        } else {
          message = null;
        } 
        if (message != null)
          message.sendToTarget(); 
        AlertController.this.mHandler.obtainMessage(1, AlertController.this.mDialog).sendToTarget();
      }
    };
  
  private final int mButtonIconDimen;
  
  Button mButtonNegative;
  
  private Drawable mButtonNegativeIcon;
  
  Message mButtonNegativeMessage;
  
  private CharSequence mButtonNegativeText;
  
  Button mButtonNeutral;
  
  private Drawable mButtonNeutralIcon;
  
  Message mButtonNeutralMessage;
  
  private CharSequence mButtonNeutralText;
  
  private int mButtonPanelLayoutHint = 0;
  
  private int mButtonPanelSideLayout;
  
  Button mButtonPositive;
  
  private Drawable mButtonPositiveIcon;
  
  Message mButtonPositiveMessage;
  
  private CharSequence mButtonPositiveText;
  
  int mCheckedItem = -1;
  
  private final Context mContext;
  
  private View mCustomTitleView;
  
  final AppCompatDialog mDialog;
  
  Handler mHandler;
  
  private Drawable mIcon;
  
  private int mIconId = 0;
  
  private ImageView mIconView;
  
  int mListItemLayout;
  
  int mListLayout;
  
  ListView mListView;
  
  private CharSequence mMessage;
  
  private TextView mMessageView;
  
  int mMultiChoiceItemLayout;
  
  NestedScrollView mScrollView;
  
  private boolean mShowTitle;
  
  int mSingleChoiceItemLayout;
  
  private CharSequence mTitle;
  
  private TextView mTitleView;
  
  private View mView;
  
  private int mViewLayoutResId;
  
  private int mViewSpacingBottom;
  
  private int mViewSpacingLeft;
  
  private int mViewSpacingRight;
  
  private boolean mViewSpacingSpecified = false;
  
  private int mViewSpacingTop;
  
  private final Window mWindow;
  
  public AlertController(Context paramContext, AppCompatDialog paramAppCompatDialog, Window paramWindow) {
    this.mContext = paramContext;
    this.mDialog = paramAppCompatDialog;
    this.mWindow = paramWindow;
    this.mHandler = new ButtonHandler(paramAppCompatDialog);
    TypedArray typedArray = paramContext.obtainStyledAttributes(null, R.styleable.AlertDialog, R.attr.alertDialogStyle, 0);
    this.mAlertDialogLayout = typedArray.getResourceId(R.styleable.AlertDialog_android_layout, 0);
    this.mButtonPanelSideLayout = typedArray.getResourceId(R.styleable.AlertDialog_buttonPanelSideLayout, 0);
    this.mListLayout = typedArray.getResourceId(R.styleable.AlertDialog_listLayout, 0);
    this.mMultiChoiceItemLayout = typedArray.getResourceId(R.styleable.AlertDialog_multiChoiceItemLayout, 0);
    this.mSingleChoiceItemLayout = typedArray.getResourceId(R.styleable.AlertDialog_singleChoiceItemLayout, 0);
    this.mListItemLayout = typedArray.getResourceId(R.styleable.AlertDialog_listItemLayout, 0);
    this.mShowTitle = typedArray.getBoolean(R.styleable.AlertDialog_showTitle, true);
    this.mButtonIconDimen = typedArray.getDimensionPixelSize(R.styleable.AlertDialog_buttonIconDimen, 0);
    typedArray.recycle();
    paramAppCompatDialog.supportRequestWindowFeature(1);
  }
  
  static boolean canTextInput(View paramView) {
    if (paramView.onCheckIsTextEditor())
      return true; 
    if (!(paramView instanceof ViewGroup))
      return false; 
    ViewGroup viewGroup = (ViewGroup)paramView;
    int i = viewGroup.getChildCount();
    while (i > 0) {
      int j = i - 1;
      i = j;
      if (canTextInput(viewGroup.getChildAt(j)))
        return true; 
    } 
    return false;
  }
  
  private void centerButton(Button paramButton) {
    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)paramButton.getLayoutParams();
    layoutParams.gravity = 1;
    layoutParams.weight = 0.5F;
    paramButton.setLayoutParams(layoutParams);
  }
  
  static void manageScrollIndicators(View paramView1, View paramView2, View paramView3) {
    byte b = 4;
    if (paramView2 != null) {
      byte b1;
      if (paramView1.canScrollVertically(-1)) {
        b1 = 0;
      } else {
        b1 = 4;
      } 
      paramView2.setVisibility(b1);
    } 
    if (paramView3 != null) {
      byte b1 = b;
      if (paramView1.canScrollVertically(1))
        b1 = 0; 
      paramView3.setVisibility(b1);
    } 
  }
  
  @Nullable
  private ViewGroup resolvePanel(@Nullable View paramView1, @Nullable View paramView2) {
    if (paramView1 == null) {
      paramView1 = paramView2;
      if (paramView2 instanceof ViewStub)
        paramView1 = ((ViewStub)paramView2).inflate(); 
      return (ViewGroup)paramView1;
    } 
    if (paramView2 != null) {
      ViewParent viewParent = paramView2.getParent();
      if (viewParent instanceof ViewGroup)
        ((ViewGroup)viewParent).removeView(paramView2); 
    } 
    paramView2 = paramView1;
    if (paramView1 instanceof ViewStub)
      paramView2 = ((ViewStub)paramView1).inflate(); 
    return (ViewGroup)paramView2;
  }
  
  private int selectContentView() { return (this.mButtonPanelSideLayout == 0) ? this.mAlertDialogLayout : ((this.mButtonPanelLayoutHint == 1) ? this.mButtonPanelSideLayout : this.mAlertDialogLayout); }
  
  private void setScrollIndicators(ViewGroup paramViewGroup, final View top, int paramInt1, int paramInt2) {
    View view2 = this.mWindow.findViewById(R.id.scrollIndicatorUp);
    final View bottom = this.mWindow.findViewById(R.id.scrollIndicatorDown);
    if (Build.VERSION.SDK_INT >= 23) {
      ViewCompat.setScrollIndicators(paramView, paramInt1, paramInt2);
      if (view2 != null)
        paramViewGroup.removeView(view2); 
      if (view1 != null) {
        paramViewGroup.removeView(view1);
        return;
      } 
    } else {
      View view = null;
      paramView = view2;
      if (view2 != null) {
        paramView = view2;
        if ((paramInt1 & true) == 0) {
          paramViewGroup.removeView(view2);
          paramView = null;
        } 
      } 
      if (view1 != null && (paramInt1 & 0x2) == 0) {
        paramViewGroup.removeView(view1);
        view1 = view;
      } 
      if (paramView != null || view1 != null) {
        if (this.mMessage != null) {
          this.mScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                public void onScrollChange(NestedScrollView param1NestedScrollView, int param1Int1, int param1Int2, int param1Int3, int param1Int4) { AlertController.manageScrollIndicators(param1NestedScrollView, top, bottom); }
              });
          this.mScrollView.post(new Runnable() {
                public void run() { AlertController.manageScrollIndicators(AlertController.this.mScrollView, top, bottom); }
              });
          return;
        } 
        if (this.mListView != null) {
          this.mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                public void onScroll(AbsListView param1AbsListView, int param1Int1, int param1Int2, int param1Int3) { AlertController.manageScrollIndicators(param1AbsListView, top, bottom); }
                
                public void onScrollStateChanged(AbsListView param1AbsListView, int param1Int) {}
              });
          this.mListView.post(new Runnable() {
                public void run() { AlertController.manageScrollIndicators(AlertController.this.mListView, top, bottom); }
              });
          return;
        } 
        if (paramView != null)
          paramViewGroup.removeView(paramView); 
        if (view1 != null)
          paramViewGroup.removeView(view1); 
      } 
    } 
  }
  
  private void setupButtons(ViewGroup paramViewGroup) {
    byte b1;
    this.mButtonPositive = (Button)paramViewGroup.findViewById(16908313);
    this.mButtonPositive.setOnClickListener(this.mButtonHandler);
    boolean bool = TextUtils.isEmpty(this.mButtonPositiveText);
    byte b2 = 1;
    if (bool && this.mButtonPositiveIcon == null) {
      this.mButtonPositive.setVisibility(8);
      b1 = 0;
    } else {
      this.mButtonPositive.setText(this.mButtonPositiveText);
      if (this.mButtonPositiveIcon != null) {
        this.mButtonPositiveIcon.setBounds(0, 0, this.mButtonIconDimen, this.mButtonIconDimen);
        this.mButtonPositive.setCompoundDrawables(this.mButtonPositiveIcon, null, null, null);
      } 
      this.mButtonPositive.setVisibility(0);
      b1 = 1;
    } 
    this.mButtonNegative = (Button)paramViewGroup.findViewById(16908314);
    this.mButtonNegative.setOnClickListener(this.mButtonHandler);
    if (TextUtils.isEmpty(this.mButtonNegativeText) && this.mButtonNegativeIcon == null) {
      this.mButtonNegative.setVisibility(8);
    } else {
      this.mButtonNegative.setText(this.mButtonNegativeText);
      if (this.mButtonNegativeIcon != null) {
        this.mButtonNegativeIcon.setBounds(0, 0, this.mButtonIconDimen, this.mButtonIconDimen);
        this.mButtonNegative.setCompoundDrawables(this.mButtonNegativeIcon, null, null, null);
      } 
      this.mButtonNegative.setVisibility(0);
      b1 |= 0x2;
    } 
    this.mButtonNeutral = (Button)paramViewGroup.findViewById(16908315);
    this.mButtonNeutral.setOnClickListener(this.mButtonHandler);
    if (TextUtils.isEmpty(this.mButtonNeutralText) && this.mButtonNeutralIcon == null) {
      this.mButtonNeutral.setVisibility(8);
    } else {
      this.mButtonNeutral.setText(this.mButtonNeutralText);
      if (this.mButtonPositiveIcon != null) {
        this.mButtonPositiveIcon.setBounds(0, 0, this.mButtonIconDimen, this.mButtonIconDimen);
        this.mButtonPositive.setCompoundDrawables(this.mButtonPositiveIcon, null, null, null);
      } 
      this.mButtonNeutral.setVisibility(0);
      b1 |= 0x4;
    } 
    if (shouldCenterSingleButton(this.mContext))
      if (b1 == 1) {
        centerButton(this.mButtonPositive);
      } else if (b1 == 2) {
        centerButton(this.mButtonNegative);
      } else if (b1 == 4) {
        centerButton(this.mButtonNeutral);
      }  
    if (b1 != 0) {
      b1 = b2;
    } else {
      b1 = 0;
    } 
    if (b1 == 0)
      paramViewGroup.setVisibility(8); 
  }
  
  private void setupContent(ViewGroup paramViewGroup) {
    this.mScrollView = (NestedScrollView)this.mWindow.findViewById(R.id.scrollView);
    this.mScrollView.setFocusable(false);
    this.mScrollView.setNestedScrollingEnabled(false);
    this.mMessageView = (TextView)paramViewGroup.findViewById(16908299);
    if (this.mMessageView == null)
      return; 
    if (this.mMessage != null) {
      this.mMessageView.setText(this.mMessage);
      return;
    } 
    this.mMessageView.setVisibility(8);
    this.mScrollView.removeView(this.mMessageView);
    if (this.mListView != null) {
      paramViewGroup = (ViewGroup)this.mScrollView.getParent();
      int i = paramViewGroup.indexOfChild(this.mScrollView);
      paramViewGroup.removeViewAt(i);
      paramViewGroup.addView(this.mListView, i, new ViewGroup.LayoutParams(-1, -1));
      return;
    } 
    paramViewGroup.setVisibility(8);
  }
  
  private void setupCustomContent(ViewGroup paramViewGroup) {
    View view = this.mView;
    boolean bool = false;
    if (view != null) {
      view = this.mView;
    } else if (this.mViewLayoutResId != 0) {
      view = LayoutInflater.from(this.mContext).inflate(this.mViewLayoutResId, paramViewGroup, false);
    } else {
      view = null;
    } 
    if (view != null)
      bool = true; 
    if (!bool || !canTextInput(view))
      this.mWindow.setFlags(131072, 131072); 
    if (bool) {
      FrameLayout frameLayout = (FrameLayout)this.mWindow.findViewById(R.id.custom);
      frameLayout.addView(view, new ViewGroup.LayoutParams(-1, -1));
      if (this.mViewSpacingSpecified)
        frameLayout.setPadding(this.mViewSpacingLeft, this.mViewSpacingTop, this.mViewSpacingRight, this.mViewSpacingBottom); 
      if (this.mListView != null) {
        ((LinearLayoutCompat.LayoutParams)paramViewGroup.getLayoutParams()).weight = 0.0F;
        return;
      } 
    } else {
      paramViewGroup.setVisibility(8);
    } 
  }
  
  private void setupTitle(ViewGroup paramViewGroup) {
    if (this.mCustomTitleView != null) {
      ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-1, -2);
      paramViewGroup.addView(this.mCustomTitleView, 0, layoutParams);
      this.mWindow.findViewById(R.id.title_template).setVisibility(8);
      return;
    } 
    this.mIconView = (ImageView)this.mWindow.findViewById(16908294);
    if (TextUtils.isEmpty(this.mTitle) ^ true && this.mShowTitle) {
      this.mTitleView = (TextView)this.mWindow.findViewById(R.id.alertTitle);
      this.mTitleView.setText(this.mTitle);
      if (this.mIconId != 0) {
        this.mIconView.setImageResource(this.mIconId);
        return;
      } 
      if (this.mIcon != null) {
        this.mIconView.setImageDrawable(this.mIcon);
        return;
      } 
      this.mTitleView.setPadding(this.mIconView.getPaddingLeft(), this.mIconView.getPaddingTop(), this.mIconView.getPaddingRight(), this.mIconView.getPaddingBottom());
      this.mIconView.setVisibility(8);
      return;
    } 
    this.mWindow.findViewById(R.id.title_template).setVisibility(8);
    this.mIconView.setVisibility(8);
    paramViewGroup.setVisibility(8);
  }
  
  private void setupView() {
    boolean bool2;
    boolean bool1;
    int i;
    View view4 = this.mWindow.findViewById(R.id.parentPanel);
    View view3 = view4.findViewById(R.id.topPanel);
    View view2 = view4.findViewById(R.id.contentPanel);
    View view1 = view4.findViewById(R.id.buttonPanel);
    ViewGroup viewGroup4 = (ViewGroup)view4.findViewById(R.id.customPanel);
    setupCustomContent(viewGroup4);
    View view7 = viewGroup4.findViewById(R.id.topPanel);
    View view6 = viewGroup4.findViewById(R.id.contentPanel);
    View view5 = viewGroup4.findViewById(R.id.buttonPanel);
    ViewGroup viewGroup3 = resolvePanel(view7, view3);
    ViewGroup viewGroup2 = resolvePanel(view6, view2);
    ViewGroup viewGroup1 = resolvePanel(view5, view1);
    setupContent(viewGroup2);
    setupButtons(viewGroup1);
    setupTitle(viewGroup3);
    byte b = 0;
    if (viewGroup4 != null && viewGroup4.getVisibility() != 8) {
      i = 1;
    } else {
      i = 0;
    } 
    if (viewGroup3 != null && viewGroup3.getVisibility() != 8) {
      bool1 = true;
    } else {
      bool1 = false;
    } 
    if (viewGroup1 != null && viewGroup1.getVisibility() != 8) {
      bool2 = true;
    } else {
      bool2 = false;
    } 
    if (!bool2 && viewGroup2 != null) {
      View view = viewGroup2.findViewById(R.id.textSpacerNoButtons);
      if (view != null)
        view.setVisibility(0); 
    } 
    if (bool1) {
      View view;
      if (this.mScrollView != null)
        this.mScrollView.setClipToPadding(true); 
      viewGroup1 = null;
      if (this.mMessage != null || this.mListView != null)
        view = viewGroup3.findViewById(R.id.titleDividerNoCustom); 
      if (view != null)
        view.setVisibility(0); 
    } else if (viewGroup2 != null) {
      View view = viewGroup2.findViewById(R.id.textSpacerNoTitle);
      if (view != null)
        view.setVisibility(0); 
    } 
    if (this.mListView instanceof RecycleListView)
      ((RecycleListView)this.mListView).setHasDecor(bool1, bool2); 
    if (!i) {
      NestedScrollView nestedScrollView;
      if (this.mListView != null) {
        nestedScrollView = this.mListView;
      } else {
        nestedScrollView = this.mScrollView;
      } 
      if (nestedScrollView != null) {
        i = b;
        if (bool2)
          i = 2; 
        setScrollIndicators(viewGroup2, nestedScrollView, bool1 | i, 3);
      } 
    } 
    ListView listView = this.mListView;
    if (listView != null && this.mAdapter != null) {
      listView.setAdapter(this.mAdapter);
      i = this.mCheckedItem;
      if (i > -1) {
        listView.setItemChecked(i, true);
        listView.setSelection(i);
      } 
    } 
  }
  
  private static boolean shouldCenterSingleButton(Context paramContext) {
    TypedValue typedValue = new TypedValue();
    paramContext.getTheme().resolveAttribute(R.attr.alertDialogCenterButtons, typedValue, true);
    return (typedValue.data != 0);
  }
  
  public Button getButton(int paramInt) {
    switch (paramInt) {
      default:
        return null;
      case -1:
        return this.mButtonPositive;
      case -2:
        return this.mButtonNegative;
      case -3:
        break;
    } 
    return this.mButtonNeutral;
  }
  
  public int getIconAttributeResId(int paramInt) {
    TypedValue typedValue = new TypedValue();
    this.mContext.getTheme().resolveAttribute(paramInt, typedValue, true);
    return typedValue.resourceId;
  }
  
  public ListView getListView() { return this.mListView; }
  
  public void installContent() {
    int i = selectContentView();
    this.mDialog.setContentView(i);
    setupView();
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) { return (this.mScrollView != null && this.mScrollView.executeKeyEvent(paramKeyEvent)); }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent) { return (this.mScrollView != null && this.mScrollView.executeKeyEvent(paramKeyEvent)); }
  
  public void setButton(int paramInt, CharSequence paramCharSequence, DialogInterface.OnClickListener paramOnClickListener, Message paramMessage, Drawable paramDrawable) {
    Message message = paramMessage;
    if (paramMessage == null) {
      message = paramMessage;
      if (paramOnClickListener != null)
        message = this.mHandler.obtainMessage(paramInt, paramOnClickListener); 
    } 
    switch (paramInt) {
      default:
        throw new IllegalArgumentException("Button does not exist");
      case -1:
        this.mButtonPositiveText = paramCharSequence;
        this.mButtonPositiveMessage = message;
        this.mButtonPositiveIcon = paramDrawable;
        return;
      case -2:
        this.mButtonNegativeText = paramCharSequence;
        this.mButtonNegativeMessage = message;
        this.mButtonNegativeIcon = paramDrawable;
        return;
      case -3:
        break;
    } 
    this.mButtonNeutralText = paramCharSequence;
    this.mButtonNeutralMessage = message;
    this.mButtonNeutralIcon = paramDrawable;
  }
  
  public void setButtonPanelLayoutHint(int paramInt) { this.mButtonPanelLayoutHint = paramInt; }
  
  public void setCustomTitle(View paramView) { this.mCustomTitleView = paramView; }
  
  public void setIcon(int paramInt) {
    this.mIcon = null;
    this.mIconId = paramInt;
    if (this.mIconView != null) {
      if (paramInt != 0) {
        this.mIconView.setVisibility(0);
        this.mIconView.setImageResource(this.mIconId);
        return;
      } 
      this.mIconView.setVisibility(8);
    } 
  }
  
  public void setIcon(Drawable paramDrawable) {
    this.mIcon = paramDrawable;
    this.mIconId = 0;
    if (this.mIconView != null) {
      if (paramDrawable != null) {
        this.mIconView.setVisibility(0);
        this.mIconView.setImageDrawable(paramDrawable);
        return;
      } 
      this.mIconView.setVisibility(8);
    } 
  }
  
  public void setMessage(CharSequence paramCharSequence) {
    this.mMessage = paramCharSequence;
    if (this.mMessageView != null)
      this.mMessageView.setText(paramCharSequence); 
  }
  
  public void setTitle(CharSequence paramCharSequence) {
    this.mTitle = paramCharSequence;
    if (this.mTitleView != null)
      this.mTitleView.setText(paramCharSequence); 
  }
  
  public void setView(int paramInt) {
    this.mView = null;
    this.mViewLayoutResId = paramInt;
    this.mViewSpacingSpecified = false;
  }
  
  public void setView(View paramView) {
    this.mView = paramView;
    this.mViewLayoutResId = 0;
    this.mViewSpacingSpecified = false;
  }
  
  public void setView(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.mView = paramView;
    this.mViewLayoutResId = 0;
    this.mViewSpacingSpecified = true;
    this.mViewSpacingLeft = paramInt1;
    this.mViewSpacingTop = paramInt2;
    this.mViewSpacingRight = paramInt3;
    this.mViewSpacingBottom = paramInt4;
  }
  
  public static class AlertParams {
    public ListAdapter mAdapter;
    
    public boolean mCancelable;
    
    public int mCheckedItem = -1;
    
    public boolean[] mCheckedItems;
    
    public final Context mContext;
    
    public Cursor mCursor;
    
    public View mCustomTitleView;
    
    public boolean mForceInverseBackground;
    
    public Drawable mIcon;
    
    public int mIconAttrId = 0;
    
    public int mIconId = 0;
    
    public final LayoutInflater mInflater;
    
    public String mIsCheckedColumn;
    
    public boolean mIsMultiChoice;
    
    public boolean mIsSingleChoice;
    
    public CharSequence[] mItems;
    
    public String mLabelColumn;
    
    public CharSequence mMessage;
    
    public Drawable mNegativeButtonIcon;
    
    public DialogInterface.OnClickListener mNegativeButtonListener;
    
    public CharSequence mNegativeButtonText;
    
    public Drawable mNeutralButtonIcon;
    
    public DialogInterface.OnClickListener mNeutralButtonListener;
    
    public CharSequence mNeutralButtonText;
    
    public DialogInterface.OnCancelListener mOnCancelListener;
    
    public DialogInterface.OnMultiChoiceClickListener mOnCheckboxClickListener;
    
    public DialogInterface.OnClickListener mOnClickListener;
    
    public DialogInterface.OnDismissListener mOnDismissListener;
    
    public AdapterView.OnItemSelectedListener mOnItemSelectedListener;
    
    public DialogInterface.OnKeyListener mOnKeyListener;
    
    public OnPrepareListViewListener mOnPrepareListViewListener;
    
    public Drawable mPositiveButtonIcon;
    
    public DialogInterface.OnClickListener mPositiveButtonListener;
    
    public CharSequence mPositiveButtonText;
    
    public boolean mRecycleOnMeasure = true;
    
    public CharSequence mTitle;
    
    public View mView;
    
    public int mViewLayoutResId;
    
    public int mViewSpacingBottom;
    
    public int mViewSpacingLeft;
    
    public int mViewSpacingRight;
    
    public boolean mViewSpacingSpecified = false;
    
    public int mViewSpacingTop;
    
    public AlertParams(Context param1Context) {
      this.mContext = param1Context;
      this.mCancelable = true;
      this.mInflater = (LayoutInflater)param1Context.getSystemService("layout_inflater");
    }
    
    private void createListView(final AlertController dialog) {
      AlertController.CheckedItemAdapter checkedItemAdapter;
      final AlertController.RecycleListView listView = (AlertController.RecycleListView)this.mInflater.inflate(param1AlertController.mListLayout, null);
      if (this.mIsMultiChoice) {
        if (this.mCursor == null) {
          checkedItemAdapter = new ArrayAdapter<CharSequence>(this.mContext, param1AlertController.mMultiChoiceItemLayout, 16908308, this.mItems) {
              public View getView(int param2Int, View param2View, ViewGroup param2ViewGroup) {
                param2View = super.getView(param2Int, param2View, param2ViewGroup);
                if (AlertController.AlertParams.this.mCheckedItems != null && AlertController.AlertParams.this.mCheckedItems[param2Int])
                  listView.setItemChecked(param2Int, true); 
                return param2View;
              }
            };
        } else {
          checkedItemAdapter = new CursorAdapter(this.mContext, this.mCursor, false) {
              private final int mIsCheckedIndex;
              
              private final int mLabelIndex;
              
              public void bindView(View param2View, Context param2Context, Cursor param2Cursor) {
                ((CheckedTextView)param2View.findViewById(16908308)).setText(param2Cursor.getString(this.mLabelIndex));
                AlertController.RecycleListView recycleListView = listView;
                int i = param2Cursor.getPosition();
                int j = param2Cursor.getInt(this.mIsCheckedIndex);
                boolean bool = true;
                if (j != 1)
                  bool = false; 
                recycleListView.setItemChecked(i, bool);
              }
              
              public View newView(Context param2Context, Cursor param2Cursor, ViewGroup param2ViewGroup) { return AlertController.AlertParams.this.mInflater.inflate(this.val$dialog.mMultiChoiceItemLayout, param2ViewGroup, false); }
            };
        } 
      } else {
        int i;
        if (this.mIsSingleChoice) {
          i = param1AlertController.mSingleChoiceItemLayout;
        } else {
          i = param1AlertController.mListItemLayout;
        } 
        if (this.mCursor != null) {
          checkedItemAdapter = new SimpleCursorAdapter(this.mContext, i, this.mCursor, new String[] { this.mLabelColumn }, new int[] { 16908308 });
        } else if (this.mAdapter != null) {
          checkedItemAdapter = this.mAdapter;
        } else {
          checkedItemAdapter = new AlertController.CheckedItemAdapter(this.mContext, i, 16908308, this.mItems);
        } 
      } 
      if (this.mOnPrepareListViewListener != null)
        this.mOnPrepareListViewListener.onPrepareListView(recycleListView); 
      param1AlertController.mAdapter = checkedItemAdapter;
      param1AlertController.mCheckedItem = this.mCheckedItem;
      if (this.mOnClickListener != null) {
        recycleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
              public void onItemClick(AdapterView<?> param2AdapterView, View param2View, int param2Int, long param2Long) {
                AlertController.AlertParams.this.mOnClickListener.onClick(this.val$dialog.mDialog, param2Int);
                if (!AlertController.AlertParams.this.mIsSingleChoice)
                  this.val$dialog.mDialog.dismiss(); 
              }
            });
      } else if (this.mOnCheckboxClickListener != null) {
        recycleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
              public void onItemClick(AdapterView<?> param2AdapterView, View param2View, int param2Int, long param2Long) {
                if (AlertController.AlertParams.this.mCheckedItems != null)
                  AlertController.AlertParams.this.mCheckedItems[param2Int] = listView.isItemChecked(param2Int); 
                AlertController.AlertParams.this.mOnCheckboxClickListener.onClick(this.val$dialog.mDialog, param2Int, listView.isItemChecked(param2Int));
              }
            });
      } 
      if (this.mOnItemSelectedListener != null)
        recycleListView.setOnItemSelectedListener(this.mOnItemSelectedListener); 
      if (this.mIsSingleChoice) {
        recycleListView.setChoiceMode(1);
      } else if (this.mIsMultiChoice) {
        recycleListView.setChoiceMode(2);
      } 
      param1AlertController.mListView = recycleListView;
    }
    
    public void apply(AlertController param1AlertController) {
      if (this.mCustomTitleView != null) {
        param1AlertController.setCustomTitle(this.mCustomTitleView);
      } else {
        if (this.mTitle != null)
          param1AlertController.setTitle(this.mTitle); 
        if (this.mIcon != null)
          param1AlertController.setIcon(this.mIcon); 
        if (this.mIconId != 0)
          param1AlertController.setIcon(this.mIconId); 
        if (this.mIconAttrId != 0)
          param1AlertController.setIcon(param1AlertController.getIconAttributeResId(this.mIconAttrId)); 
      } 
      if (this.mMessage != null)
        param1AlertController.setMessage(this.mMessage); 
      if (this.mPositiveButtonText != null || this.mPositiveButtonIcon != null)
        param1AlertController.setButton(-1, this.mPositiveButtonText, this.mPositiveButtonListener, null, this.mPositiveButtonIcon); 
      if (this.mNegativeButtonText != null || this.mNegativeButtonIcon != null)
        param1AlertController.setButton(-2, this.mNegativeButtonText, this.mNegativeButtonListener, null, this.mNegativeButtonIcon); 
      if (this.mNeutralButtonText != null || this.mNeutralButtonIcon != null)
        param1AlertController.setButton(-3, this.mNeutralButtonText, this.mNeutralButtonListener, null, this.mNeutralButtonIcon); 
      if (this.mItems != null || this.mCursor != null || this.mAdapter != null)
        createListView(param1AlertController); 
      if (this.mView != null) {
        if (this.mViewSpacingSpecified) {
          param1AlertController.setView(this.mView, this.mViewSpacingLeft, this.mViewSpacingTop, this.mViewSpacingRight, this.mViewSpacingBottom);
          return;
        } 
        param1AlertController.setView(this.mView);
        return;
      } 
      if (this.mViewLayoutResId != 0)
        param1AlertController.setView(this.mViewLayoutResId); 
    }
    
    public static interface OnPrepareListViewListener {
      void onPrepareListView(ListView param2ListView);
    }
  }
  
  class null extends ArrayAdapter<CharSequence> {
    null(Context param1Context, int param1Int1, int param1Int2, CharSequence[] param1ArrayOfCharSequence) { super(param1Context, param1Int1, param1Int2, param1ArrayOfCharSequence); }
    
    public View getView(int param1Int, View param1View, ViewGroup param1ViewGroup) {
      param1View = super.getView(param1Int, param1View, param1ViewGroup);
      if (this.this$0.mCheckedItems != null && this.this$0.mCheckedItems[param1Int])
        listView.setItemChecked(param1Int, true); 
      return param1View;
    }
  }
  
  class null extends CursorAdapter {
    private final int mIsCheckedIndex;
    
    private final int mLabelIndex;
    
    null(Context param1Context, Cursor param1Cursor, boolean param1Boolean) {
      super(param1Context, param1Cursor, param1Boolean);
      Cursor cursor = getCursor();
      this.mLabelIndex = cursor.getColumnIndexOrThrow(this.this$0.mLabelColumn);
      this.mIsCheckedIndex = cursor.getColumnIndexOrThrow(this.this$0.mIsCheckedColumn);
    }
    
    public void bindView(View param1View, Context param1Context, Cursor param1Cursor) {
      ((CheckedTextView)param1View.findViewById(16908308)).setText(param1Cursor.getString(this.mLabelIndex));
      AlertController.RecycleListView recycleListView = listView;
      int i = param1Cursor.getPosition();
      int j = param1Cursor.getInt(this.mIsCheckedIndex);
      boolean bool = true;
      if (j != 1)
        bool = false; 
      recycleListView.setItemChecked(i, bool);
    }
    
    public View newView(Context param1Context, Cursor param1Cursor, ViewGroup param1ViewGroup) { return this.this$0.mInflater.inflate(this.val$dialog.mMultiChoiceItemLayout, param1ViewGroup, false); }
  }
  
  class null implements AdapterView.OnItemClickListener {
    public void onItemClick(AdapterView<?> param1AdapterView, View param1View, int param1Int, long param1Long) {
      this.this$0.mOnClickListener.onClick(this.val$dialog.mDialog, param1Int);
      if (!this.this$0.mIsSingleChoice)
        this.val$dialog.mDialog.dismiss(); 
    }
  }
  
  class null implements AdapterView.OnItemClickListener {
    public void onItemClick(AdapterView<?> param1AdapterView, View param1View, int param1Int, long param1Long) {
      if (this.this$0.mCheckedItems != null)
        this.this$0.mCheckedItems[param1Int] = listView.isItemChecked(param1Int); 
      this.this$0.mOnCheckboxClickListener.onClick(this.val$dialog.mDialog, param1Int, listView.isItemChecked(param1Int));
    }
  }
  
  public static interface OnPrepareListViewListener {
    void onPrepareListView(ListView param1ListView);
  }
  
  private static final class ButtonHandler extends Handler {
    private static final int MSG_DISMISS_DIALOG = 1;
    
    private WeakReference<DialogInterface> mDialog;
    
    public ButtonHandler(DialogInterface param1DialogInterface) { this.mDialog = new WeakReference(param1DialogInterface); }
    
    public void handleMessage(Message param1Message) {
      int i = param1Message.what;
      if (i != 1) {
        switch (i) {
          default:
            return;
          case -3:
          case -2:
          case -1:
            break;
        } 
        ((DialogInterface.OnClickListener)param1Message.obj).onClick((DialogInterface)this.mDialog.get(), param1Message.what);
        return;
      } 
      ((DialogInterface)param1Message.obj).dismiss();
    }
  }
  
  private static class CheckedItemAdapter extends ArrayAdapter<CharSequence> {
    public CheckedItemAdapter(Context param1Context, int param1Int1, int param1Int2, CharSequence[] param1ArrayOfCharSequence) { super(param1Context, param1Int1, param1Int2, param1ArrayOfCharSequence); }
    
    public long getItemId(int param1Int) { return param1Int; }
    
    public boolean hasStableIds() { return true; }
  }
  
  public static class RecycleListView extends ListView {
    private final int mPaddingBottomNoButtons;
    
    private final int mPaddingTopNoTitle;
    
    public RecycleListView(Context param1Context) { this(param1Context, null); }
    
    public RecycleListView(Context param1Context, AttributeSet param1AttributeSet) {
      super(param1Context, param1AttributeSet);
      TypedArray typedArray = param1Context.obtainStyledAttributes(param1AttributeSet, R.styleable.RecycleListView);
      this.mPaddingBottomNoButtons = typedArray.getDimensionPixelOffset(R.styleable.RecycleListView_paddingBottomNoButtons, -1);
      this.mPaddingTopNoTitle = typedArray.getDimensionPixelOffset(R.styleable.RecycleListView_paddingTopNoTitle, -1);
    }
    
    public void setHasDecor(boolean param1Boolean1, boolean param1Boolean2) {
      if (!param1Boolean2 || !param1Boolean1) {
        int j;
        int i;
        int k = getPaddingLeft();
        if (param1Boolean1) {
          i = getPaddingTop();
        } else {
          i = this.mPaddingTopNoTitle;
        } 
        int m = getPaddingRight();
        if (param1Boolean2) {
          j = getPaddingBottom();
        } else {
          j = this.mPaddingBottomNoButtons;
        } 
        setPadding(k, i, m, j);
      } 
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/app/AlertController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */