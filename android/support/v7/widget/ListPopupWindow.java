package android.support.v7.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.StyleRes;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.appcompat.R;
import android.support.v7.view.menu.ShowableListMenu;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import java.lang.reflect.Method;

public class ListPopupWindow implements ShowableListMenu {
  private static final boolean DEBUG = false;
  
  static final int EXPAND_LIST_TIMEOUT = 250;
  
  public static final int INPUT_METHOD_FROM_FOCUSABLE = 0;
  
  public static final int INPUT_METHOD_NEEDED = 1;
  
  public static final int INPUT_METHOD_NOT_NEEDED = 2;
  
  public static final int MATCH_PARENT = -1;
  
  public static final int POSITION_PROMPT_ABOVE = 0;
  
  public static final int POSITION_PROMPT_BELOW = 1;
  
  private static final String TAG = "ListPopupWindow";
  
  public static final int WRAP_CONTENT = -2;
  
  private static Method sClipToWindowEnabledMethod;
  
  private static Method sGetMaxAvailableHeightMethod;
  
  private static Method sSetEpicenterBoundsMethod;
  
  private ListAdapter mAdapter;
  
  private Context mContext;
  
  private boolean mDropDownAlwaysVisible = false;
  
  private View mDropDownAnchorView;
  
  private int mDropDownGravity = 0;
  
  private int mDropDownHeight = -2;
  
  private int mDropDownHorizontalOffset;
  
  DropDownListView mDropDownList;
  
  private Drawable mDropDownListHighlight;
  
  private int mDropDownVerticalOffset;
  
  private boolean mDropDownVerticalOffsetSet;
  
  private int mDropDownWidth = -2;
  
  private int mDropDownWindowLayoutType = 1002;
  
  private Rect mEpicenterBounds;
  
  private boolean mForceIgnoreOutsideTouch = false;
  
  final Handler mHandler;
  
  private final ListSelectorHider mHideSelector = new ListSelectorHider();
  
  private boolean mIsAnimatedFromAnchor = true;
  
  private AdapterView.OnItemClickListener mItemClickListener;
  
  private AdapterView.OnItemSelectedListener mItemSelectedListener;
  
  int mListItemExpandMaximum = Integer.MAX_VALUE;
  
  private boolean mModal;
  
  private DataSetObserver mObserver;
  
  private boolean mOverlapAnchor;
  
  private boolean mOverlapAnchorSet;
  
  PopupWindow mPopup;
  
  private int mPromptPosition = 0;
  
  private View mPromptView;
  
  final ResizePopupRunnable mResizePopupRunnable = new ResizePopupRunnable();
  
  private final PopupScrollListener mScrollListener = new PopupScrollListener();
  
  private Runnable mShowDropDownRunnable;
  
  private final Rect mTempRect = new Rect();
  
  private final PopupTouchInterceptor mTouchInterceptor = new PopupTouchInterceptor();
  
  static  {
    try {
      sClipToWindowEnabledMethod = PopupWindow.class.getDeclaredMethod("setClipToScreenEnabled", new Class[] { boolean.class });
    } catch (NoSuchMethodException noSuchMethodException) {
      Log.i("ListPopupWindow", "Could not find method setClipToScreenEnabled() on PopupWindow. Oh well.");
    } 
    try {
      sGetMaxAvailableHeightMethod = PopupWindow.class.getDeclaredMethod("getMaxAvailableHeight", new Class[] { View.class, int.class, boolean.class });
    } catch (NoSuchMethodException noSuchMethodException) {
      Log.i("ListPopupWindow", "Could not find method getMaxAvailableHeight(View, int, boolean) on PopupWindow. Oh well.");
    } 
    try {
      sSetEpicenterBoundsMethod = PopupWindow.class.getDeclaredMethod("setEpicenterBounds", new Class[] { Rect.class });
      return;
    } catch (NoSuchMethodException noSuchMethodException) {
      Log.i("ListPopupWindow", "Could not find method setEpicenterBounds(Rect) on PopupWindow. Oh well.");
    } 
  }
  
  public ListPopupWindow(@NonNull Context paramContext) { this(paramContext, null, R.attr.listPopupWindowStyle); }
  
  public ListPopupWindow(@NonNull Context paramContext, @Nullable AttributeSet paramAttributeSet) { this(paramContext, paramAttributeSet, R.attr.listPopupWindowStyle); }
  
  public ListPopupWindow(@NonNull Context paramContext, @Nullable AttributeSet paramAttributeSet, @AttrRes int paramInt) { this(paramContext, paramAttributeSet, paramInt, 0); }
  
  public ListPopupWindow(@NonNull Context paramContext, @Nullable AttributeSet paramAttributeSet, @AttrRes int paramInt1, @StyleRes int paramInt2) {
    this.mContext = paramContext;
    this.mHandler = new Handler(paramContext.getMainLooper());
    TypedArray typedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ListPopupWindow, paramInt1, paramInt2);
    this.mDropDownHorizontalOffset = typedArray.getDimensionPixelOffset(R.styleable.ListPopupWindow_android_dropDownHorizontalOffset, 0);
    this.mDropDownVerticalOffset = typedArray.getDimensionPixelOffset(R.styleable.ListPopupWindow_android_dropDownVerticalOffset, 0);
    if (this.mDropDownVerticalOffset != 0)
      this.mDropDownVerticalOffsetSet = true; 
    typedArray.recycle();
    this.mPopup = new AppCompatPopupWindow(paramContext, paramAttributeSet, paramInt1, paramInt2);
    this.mPopup.setInputMethodMode(1);
  }
  
  private int buildDropDown() {
    int k;
    int i;
    DropDownListView dropDownListView = this.mDropDownList;
    boolean bool = true;
    if (dropDownListView == null) {
      LinearLayout.LayoutParams layoutParams1 = this.mContext;
      this.mShowDropDownRunnable = new Runnable() {
          public void run() {
            View view = ListPopupWindow.this.getAnchorView();
            if (view != null && view.getWindowToken() != null)
              ListPopupWindow.this.show(); 
          }
        };
      this.mDropDownList = createDropDownListView(layoutParams1, this.mModal ^ true);
      if (this.mDropDownListHighlight != null)
        this.mDropDownList.setSelector(this.mDropDownListHighlight); 
      this.mDropDownList.setAdapter(this.mAdapter);
      this.mDropDownList.setOnItemClickListener(this.mItemClickListener);
      this.mDropDownList.setFocusable(true);
      this.mDropDownList.setFocusableInTouchMode(true);
      this.mDropDownList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> param1AdapterView, View param1View, int param1Int, long param1Long) {
              if (param1Int != -1) {
                DropDownListView dropDownListView = ListPopupWindow.this.mDropDownList;
                if (dropDownListView != null)
                  dropDownListView.setListSelectionHidden(false); 
              } 
            }
            
            public void onNothingSelected(AdapterView<?> param1AdapterView) {}
          });
      this.mDropDownList.setOnScrollListener(this.mScrollListener);
      if (this.mItemSelectedListener != null)
        this.mDropDownList.setOnItemSelectedListener(this.mItemSelectedListener); 
      LinearLayout.LayoutParams layoutParams2 = this.mDropDownList;
      View view = this.mPromptView;
      if (view != null) {
        StringBuilder stringBuilder;
        byte b;
        LinearLayout linearLayout = new LinearLayout(layoutParams1);
        linearLayout.setOrientation(1);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, 0, 1.0F);
        switch (this.mPromptPosition) {
          default:
            stringBuilder = new StringBuilder();
            stringBuilder.append("Invalid hint position ");
            stringBuilder.append(this.mPromptPosition);
            Log.e("ListPopupWindow", stringBuilder.toString());
            break;
          case 1:
            linearLayout.addView(stringBuilder, layoutParams);
            linearLayout.addView(view);
            break;
          case 0:
            linearLayout.addView(view);
            linearLayout.addView(stringBuilder, layoutParams);
            break;
        } 
        if (this.mDropDownWidth >= 0) {
          i = this.mDropDownWidth;
          b = -2147483648;
        } else {
          i = 0;
          b = 0;
        } 
        view.measure(View.MeasureSpec.makeMeasureSpec(i, b), 0);
        layoutParams2 = (LinearLayout.LayoutParams)view.getLayoutParams();
        i = view.getMeasuredHeight() + layoutParams2.topMargin + layoutParams2.bottomMargin;
      } else {
        i = 0;
        layoutParams1 = layoutParams2;
      } 
      this.mPopup.setContentView(layoutParams1);
    } else {
      ViewGroup viewGroup = (ViewGroup)this.mPopup.getContentView();
      View view = this.mPromptView;
      if (view != null) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)view.getLayoutParams();
        i = view.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
      } else {
        i = 0;
      } 
    } 
    Drawable drawable = this.mPopup.getBackground();
    if (drawable != null) {
      drawable.getPadding(this.mTempRect);
      int n = this.mTempRect.top + this.mTempRect.bottom;
      k = n;
      if (!this.mDropDownVerticalOffsetSet) {
        this.mDropDownVerticalOffset = -this.mTempRect.top;
        k = n;
      } 
    } else {
      this.mTempRect.setEmpty();
      k = 0;
    } 
    if (this.mPopup.getInputMethodMode() != 2)
      bool = false; 
    int m = getMaxAvailableHeight(getAnchorView(), this.mDropDownVerticalOffset, bool);
    if (this.mDropDownAlwaysVisible || this.mDropDownHeight == -1)
      return m + k; 
    switch (this.mDropDownWidth) {
      default:
        j = View.MeasureSpec.makeMeasureSpec(this.mDropDownWidth, 1073741824);
        break;
      case -1:
        j = View.MeasureSpec.makeMeasureSpec((this.mContext.getResources().getDisplayMetrics()).widthPixels - this.mTempRect.left + this.mTempRect.right, 1073741824);
        break;
      case -2:
        j = View.MeasureSpec.makeMeasureSpec((this.mContext.getResources().getDisplayMetrics()).widthPixels - this.mTempRect.left + this.mTempRect.right, -2147483648);
        break;
    } 
    m = this.mDropDownList.measureHeightOfChildrenCompat(j, 0, -1, m - i, -1);
    int j = i;
    if (m > 0)
      j = i + k + this.mDropDownList.getPaddingTop() + this.mDropDownList.getPaddingBottom(); 
    return m + j;
  }
  
  private int getMaxAvailableHeight(View paramView, int paramInt, boolean paramBoolean) {
    if (sGetMaxAvailableHeightMethod != null)
      try {
        return ((Integer)sGetMaxAvailableHeightMethod.invoke(this.mPopup, new Object[] { paramView, Integer.valueOf(paramInt), Boolean.valueOf(paramBoolean) })).intValue();
      } catch (Exception exception) {
        Log.i("ListPopupWindow", "Could not call getMaxAvailableHeightMethod(View, int, boolean) on PopupWindow. Using the public version.");
      }  
    return this.mPopup.getMaxAvailableHeight(paramView, paramInt);
  }
  
  private static boolean isConfirmKey(int paramInt) { return (paramInt == 66 || paramInt == 23); }
  
  private void removePromptView() {
    if (this.mPromptView != null) {
      ViewParent viewParent = this.mPromptView.getParent();
      if (viewParent instanceof ViewGroup)
        ((ViewGroup)viewParent).removeView(this.mPromptView); 
    } 
  }
  
  private void setPopupClipToScreenEnabled(boolean paramBoolean) {
    if (sClipToWindowEnabledMethod != null)
      try {
        sClipToWindowEnabledMethod.invoke(this.mPopup, new Object[] { Boolean.valueOf(paramBoolean) });
        return;
      } catch (Exception exception) {
        Log.i("ListPopupWindow", "Could not call setClipToScreenEnabled() on PopupWindow. Oh well.");
      }  
  }
  
  public void clearListSelection() {
    DropDownListView dropDownListView = this.mDropDownList;
    if (dropDownListView != null) {
      dropDownListView.setListSelectionHidden(true);
      dropDownListView.requestLayout();
    } 
  }
  
  public View.OnTouchListener createDragToOpenListener(View paramView) { return new ForwardingListener(paramView) {
        public ListPopupWindow getPopup() { return ListPopupWindow.this; }
      }; }
  
  @NonNull
  DropDownListView createDropDownListView(Context paramContext, boolean paramBoolean) { return new DropDownListView(paramContext, paramBoolean); }
  
  public void dismiss() {
    this.mPopup.dismiss();
    removePromptView();
    this.mPopup.setContentView(null);
    this.mDropDownList = null;
    this.mHandler.removeCallbacks(this.mResizePopupRunnable);
  }
  
  @Nullable
  public View getAnchorView() { return this.mDropDownAnchorView; }
  
  @StyleRes
  public int getAnimationStyle() { return this.mPopup.getAnimationStyle(); }
  
  @Nullable
  public Drawable getBackground() { return this.mPopup.getBackground(); }
  
  public int getHeight() { return this.mDropDownHeight; }
  
  public int getHorizontalOffset() { return this.mDropDownHorizontalOffset; }
  
  public int getInputMethodMode() { return this.mPopup.getInputMethodMode(); }
  
  @Nullable
  public ListView getListView() { return this.mDropDownList; }
  
  public int getPromptPosition() { return this.mPromptPosition; }
  
  @Nullable
  public Object getSelectedItem() { return !isShowing() ? null : this.mDropDownList.getSelectedItem(); }
  
  public long getSelectedItemId() { return !isShowing() ? Float.MIN_VALUE : this.mDropDownList.getSelectedItemId(); }
  
  public int getSelectedItemPosition() { return !isShowing() ? -1 : this.mDropDownList.getSelectedItemPosition(); }
  
  @Nullable
  public View getSelectedView() { return !isShowing() ? null : this.mDropDownList.getSelectedView(); }
  
  public int getSoftInputMode() { return this.mPopup.getSoftInputMode(); }
  
  public int getVerticalOffset() { return !this.mDropDownVerticalOffsetSet ? 0 : this.mDropDownVerticalOffset; }
  
  public int getWidth() { return this.mDropDownWidth; }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public boolean isDropDownAlwaysVisible() { return this.mDropDownAlwaysVisible; }
  
  public boolean isInputMethodNotNeeded() { return (this.mPopup.getInputMethodMode() == 2); }
  
  public boolean isModal() { return this.mModal; }
  
  public boolean isShowing() { return this.mPopup.isShowing(); }
  
  public boolean onKeyDown(int paramInt, @NonNull KeyEvent paramKeyEvent) {
    if (isShowing() && paramInt != 62 && (this.mDropDownList.getSelectedItemPosition() >= 0 || !isConfirmKey(paramInt))) {
      int j;
      int k = this.mDropDownList.getSelectedItemPosition();
      boolean bool = this.mPopup.isAboveAnchor() ^ true;
      ListAdapter listAdapter = this.mAdapter;
      int i = Integer.MAX_VALUE;
      if (listAdapter != null) {
        boolean bool1 = listAdapter.areAllItemsEnabled();
        if (bool1) {
          i = 0;
        } else {
          i = this.mDropDownList.lookForSelectablePosition(0, true);
        } 
        if (bool1) {
          j = listAdapter.getCount() - 1;
        } else {
          j = this.mDropDownList.lookForSelectablePosition(listAdapter.getCount() - 1, false);
        } 
      } else {
        j = Integer.MIN_VALUE;
      } 
      if ((bool && paramInt == 19 && k <= i) || (!bool && paramInt == 20 && k >= j)) {
        clearListSelection();
        this.mPopup.setInputMethodMode(1);
        show();
        return true;
      } 
      this.mDropDownList.setListSelectionHidden(false);
      if (this.mDropDownList.onKeyDown(paramInt, paramKeyEvent)) {
        this.mPopup.setInputMethodMode(2);
        this.mDropDownList.requestFocusFromTouch();
        show();
        if (paramInt != 23 && paramInt != 66)
          switch (paramInt) {
            default:
              return false;
            case 19:
            case 20:
              break;
          }  
        return true;
      } 
      if (bool && paramInt == 20) {
        if (k == j)
          return true; 
      } else if (!bool && paramInt == 19 && k == i) {
        return true;
      } 
    } 
    return false;
  }
  
  public boolean onKeyPreIme(int paramInt, @NonNull KeyEvent paramKeyEvent) {
    if (paramInt == 4 && isShowing()) {
      KeyEvent.DispatcherState dispatcherState = this.mDropDownAnchorView;
      if (paramKeyEvent.getAction() == 0 && paramKeyEvent.getRepeatCount() == 0) {
        dispatcherState = dispatcherState.getKeyDispatcherState();
        if (dispatcherState != null)
          dispatcherState.startTracking(paramKeyEvent, this); 
        return true;
      } 
      if (paramKeyEvent.getAction() == 1) {
        dispatcherState = dispatcherState.getKeyDispatcherState();
        if (dispatcherState != null)
          dispatcherState.handleUpEvent(paramKeyEvent); 
        if (paramKeyEvent.isTracking() && !paramKeyEvent.isCanceled()) {
          dismiss();
          return true;
        } 
      } 
    } 
    return false;
  }
  
  public boolean onKeyUp(int paramInt, @NonNull KeyEvent paramKeyEvent) {
    if (isShowing() && this.mDropDownList.getSelectedItemPosition() >= 0) {
      boolean bool = this.mDropDownList.onKeyUp(paramInt, paramKeyEvent);
      if (bool && isConfirmKey(paramInt))
        dismiss(); 
      return bool;
    } 
    return false;
  }
  
  public boolean performItemClick(int paramInt) {
    if (isShowing()) {
      if (this.mItemClickListener != null) {
        DropDownListView dropDownListView = this.mDropDownList;
        View view = dropDownListView.getChildAt(paramInt - dropDownListView.getFirstVisiblePosition());
        ListAdapter listAdapter = dropDownListView.getAdapter();
        this.mItemClickListener.onItemClick(dropDownListView, view, paramInt, listAdapter.getItemId(paramInt));
      } 
      return true;
    } 
    return false;
  }
  
  public void postShow() { this.mHandler.post(this.mShowDropDownRunnable); }
  
  public void setAdapter(@Nullable ListAdapter paramListAdapter) {
    if (this.mObserver == null) {
      this.mObserver = new PopupDataSetObserver();
    } else if (this.mAdapter != null) {
      this.mAdapter.unregisterDataSetObserver(this.mObserver);
    } 
    this.mAdapter = paramListAdapter;
    if (paramListAdapter != null)
      paramListAdapter.registerDataSetObserver(this.mObserver); 
    if (this.mDropDownList != null)
      this.mDropDownList.setAdapter(this.mAdapter); 
  }
  
  public void setAnchorView(@Nullable View paramView) { this.mDropDownAnchorView = paramView; }
  
  public void setAnimationStyle(@StyleRes int paramInt) { this.mPopup.setAnimationStyle(paramInt); }
  
  public void setBackgroundDrawable(@Nullable Drawable paramDrawable) { this.mPopup.setBackgroundDrawable(paramDrawable); }
  
  public void setContentWidth(int paramInt) {
    Drawable drawable = this.mPopup.getBackground();
    if (drawable != null) {
      drawable.getPadding(this.mTempRect);
      this.mDropDownWidth = this.mTempRect.left + this.mTempRect.right + paramInt;
      return;
    } 
    setWidth(paramInt);
  }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public void setDropDownAlwaysVisible(boolean paramBoolean) { this.mDropDownAlwaysVisible = paramBoolean; }
  
  public void setDropDownGravity(int paramInt) { this.mDropDownGravity = paramInt; }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public void setEpicenterBounds(Rect paramRect) { this.mEpicenterBounds = paramRect; }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public void setForceIgnoreOutsideTouch(boolean paramBoolean) { this.mForceIgnoreOutsideTouch = paramBoolean; }
  
  public void setHeight(int paramInt) {
    if (paramInt < 0 && -2 != paramInt && -1 != paramInt)
      throw new IllegalArgumentException("Invalid height. Must be a positive value, MATCH_PARENT, or WRAP_CONTENT."); 
    this.mDropDownHeight = paramInt;
  }
  
  public void setHorizontalOffset(int paramInt) { this.mDropDownHorizontalOffset = paramInt; }
  
  public void setInputMethodMode(int paramInt) { this.mPopup.setInputMethodMode(paramInt); }
  
  void setListItemExpandMax(int paramInt) { this.mListItemExpandMaximum = paramInt; }
  
  public void setListSelector(Drawable paramDrawable) { this.mDropDownListHighlight = paramDrawable; }
  
  public void setModal(boolean paramBoolean) {
    this.mModal = paramBoolean;
    this.mPopup.setFocusable(paramBoolean);
  }
  
  public void setOnDismissListener(@Nullable PopupWindow.OnDismissListener paramOnDismissListener) { this.mPopup.setOnDismissListener(paramOnDismissListener); }
  
  public void setOnItemClickListener(@Nullable AdapterView.OnItemClickListener paramOnItemClickListener) { this.mItemClickListener = paramOnItemClickListener; }
  
  public void setOnItemSelectedListener(@Nullable AdapterView.OnItemSelectedListener paramOnItemSelectedListener) { this.mItemSelectedListener = paramOnItemSelectedListener; }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public void setOverlapAnchor(boolean paramBoolean) {
    this.mOverlapAnchorSet = true;
    this.mOverlapAnchor = paramBoolean;
  }
  
  public void setPromptPosition(int paramInt) { this.mPromptPosition = paramInt; }
  
  public void setPromptView(@Nullable View paramView) {
    boolean bool = isShowing();
    if (bool)
      removePromptView(); 
    this.mPromptView = paramView;
    if (bool)
      show(); 
  }
  
  public void setSelection(int paramInt) {
    DropDownListView dropDownListView = this.mDropDownList;
    if (isShowing() && dropDownListView != null) {
      dropDownListView.setListSelectionHidden(false);
      dropDownListView.setSelection(paramInt);
      if (dropDownListView.getChoiceMode() != 0)
        dropDownListView.setItemChecked(paramInt, true); 
    } 
  }
  
  public void setSoftInputMode(int paramInt) { this.mPopup.setSoftInputMode(paramInt); }
  
  public void setVerticalOffset(int paramInt) {
    this.mDropDownVerticalOffset = paramInt;
    this.mDropDownVerticalOffsetSet = true;
  }
  
  public void setWidth(int paramInt) { this.mDropDownWidth = paramInt; }
  
  public void setWindowLayoutType(int paramInt) { this.mDropDownWindowLayoutType = paramInt; }
  
  public void show() {
    int i;
    int j = buildDropDown();
    boolean bool2 = isInputMethodNotNeeded();
    PopupWindowCompat.setWindowLayoutType(this.mPopup, this.mDropDownWindowLayoutType);
    boolean bool3 = this.mPopup.isShowing();
    boolean bool1 = true;
    if (bool3) {
      if (!ViewCompat.isAttachedToWindow(getAnchorView()))
        return; 
      if (this.mDropDownWidth == -1) {
        i = -1;
      } else if (this.mDropDownWidth == -2) {
        i = getAnchorView().getWidth();
      } else {
        i = this.mDropDownWidth;
      } 
      if (this.mDropDownHeight == -1) {
        if (!bool2)
          j = -1; 
        if (bool2) {
          byte b;
          PopupWindow popupWindow2 = this.mPopup;
          if (this.mDropDownWidth == -1) {
            b = -1;
          } else {
            b = 0;
          } 
          popupWindow2.setWidth(b);
          this.mPopup.setHeight(0);
        } else {
          byte b;
          PopupWindow popupWindow2 = this.mPopup;
          if (this.mDropDownWidth == -1) {
            b = -1;
          } else {
            b = 0;
          } 
          popupWindow2.setWidth(b);
          this.mPopup.setHeight(-1);
        } 
      } else if (this.mDropDownHeight != -2) {
        j = this.mDropDownHeight;
      } 
      PopupWindow popupWindow1 = this.mPopup;
      if (this.mForceIgnoreOutsideTouch || this.mDropDownAlwaysVisible)
        bool1 = false; 
      popupWindow1.setOutsideTouchable(bool1);
      popupWindow1 = this.mPopup;
      View view = getAnchorView();
      int k = this.mDropDownHorizontalOffset;
      int m = this.mDropDownVerticalOffset;
      if (i < 0)
        i = -1; 
      if (j < 0)
        j = -1; 
      popupWindow1.update(view, k, m, i, j);
      return;
    } 
    if (this.mDropDownWidth == -1) {
      i = -1;
    } else if (this.mDropDownWidth == -2) {
      i = getAnchorView().getWidth();
    } else {
      i = this.mDropDownWidth;
    } 
    if (this.mDropDownHeight == -1) {
      j = -1;
    } else if (this.mDropDownHeight != -2) {
      j = this.mDropDownHeight;
    } 
    this.mPopup.setWidth(i);
    this.mPopup.setHeight(j);
    setPopupClipToScreenEnabled(true);
    popupWindow = this.mPopup;
    if (!this.mForceIgnoreOutsideTouch && !this.mDropDownAlwaysVisible) {
      bool1 = true;
    } else {
      bool1 = false;
    } 
    popupWindow.setOutsideTouchable(bool1);
    this.mPopup.setTouchInterceptor(this.mTouchInterceptor);
    if (this.mOverlapAnchorSet)
      PopupWindowCompat.setOverlapAnchor(this.mPopup, this.mOverlapAnchor); 
    if (sSetEpicenterBoundsMethod != null)
      try {
        sSetEpicenterBoundsMethod.invoke(this.mPopup, new Object[] { this.mEpicenterBounds });
      } catch (Exception popupWindow) {
        Log.e("ListPopupWindow", "Could not invoke setEpicenterBounds on PopupWindow", popupWindow);
      }  
    PopupWindowCompat.showAsDropDown(this.mPopup, getAnchorView(), this.mDropDownHorizontalOffset, this.mDropDownVerticalOffset, this.mDropDownGravity);
    this.mDropDownList.setSelection(-1);
    if (!this.mModal || this.mDropDownList.isInTouchMode())
      clearListSelection(); 
    if (!this.mModal)
      this.mHandler.post(this.mHideSelector); 
  }
  
  private class ListSelectorHider implements Runnable {
    public void run() { ListPopupWindow.this.clearListSelection(); }
  }
  
  private class PopupDataSetObserver extends DataSetObserver {
    public void onChanged() {
      if (ListPopupWindow.this.isShowing())
        ListPopupWindow.this.show(); 
    }
    
    public void onInvalidated() { ListPopupWindow.this.dismiss(); }
  }
  
  private class PopupScrollListener implements AbsListView.OnScrollListener {
    public void onScroll(AbsListView param1AbsListView, int param1Int1, int param1Int2, int param1Int3) {}
    
    public void onScrollStateChanged(AbsListView param1AbsListView, int param1Int) {
      if (param1Int == 1 && !ListPopupWindow.this.isInputMethodNotNeeded() && ListPopupWindow.this.mPopup.getContentView() != null) {
        ListPopupWindow.this.mHandler.removeCallbacks(ListPopupWindow.this.mResizePopupRunnable);
        ListPopupWindow.this.mResizePopupRunnable.run();
      } 
    }
  }
  
  private class PopupTouchInterceptor implements View.OnTouchListener {
    public boolean onTouch(View param1View, MotionEvent param1MotionEvent) {
      int i = param1MotionEvent.getAction();
      int j = (int)param1MotionEvent.getX();
      int k = (int)param1MotionEvent.getY();
      if (i == 0 && ListPopupWindow.this.mPopup != null && ListPopupWindow.this.mPopup.isShowing() && j >= 0 && j < ListPopupWindow.this.mPopup.getWidth() && k >= 0 && k < ListPopupWindow.this.mPopup.getHeight()) {
        ListPopupWindow.this.mHandler.postDelayed(ListPopupWindow.this.mResizePopupRunnable, 250L);
      } else if (i == 1) {
        ListPopupWindow.this.mHandler.removeCallbacks(ListPopupWindow.this.mResizePopupRunnable);
      } 
      return false;
    }
  }
  
  private class ResizePopupRunnable implements Runnable {
    public void run() {
      if (ListPopupWindow.this.mDropDownList != null && ViewCompat.isAttachedToWindow(ListPopupWindow.this.mDropDownList) && ListPopupWindow.this.mDropDownList.getCount() > ListPopupWindow.this.mDropDownList.getChildCount() && ListPopupWindow.this.mDropDownList.getChildCount() <= ListPopupWindow.this.mListItemExpandMaximum) {
        ListPopupWindow.this.mPopup.setInputMethodMode(2);
        ListPopupWindow.this.show();
      } 
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/ListPopupWindow.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */