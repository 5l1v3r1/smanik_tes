package android.support.transition;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v4.view.ViewCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

@RequiresApi(14)
class ViewOverlayApi14 implements ViewOverlayImpl {
  protected OverlayViewGroup mOverlayViewGroup;
  
  ViewOverlayApi14(Context paramContext, ViewGroup paramViewGroup, View paramView) { this.mOverlayViewGroup = new OverlayViewGroup(paramContext, paramViewGroup, paramView, this); }
  
  static ViewOverlayApi14 createFrom(View paramView) {
    ViewGroup viewGroup = getContentView(paramView);
    if (viewGroup != null) {
      int i = viewGroup.getChildCount();
      for (byte b = 0; b < i; b++) {
        View view = viewGroup.getChildAt(b);
        if (view instanceof OverlayViewGroup)
          return ((OverlayViewGroup)view).mViewOverlay; 
      } 
      return new ViewGroupOverlayApi14(viewGroup.getContext(), viewGroup, paramView);
    } 
    return null;
  }
  
  static ViewGroup getContentView(View paramView) {
    while (paramView != null) {
      if (paramView.getId() == 16908290 && paramView instanceof ViewGroup)
        return (ViewGroup)paramView; 
      if (paramView.getParent() instanceof ViewGroup)
        ViewGroup viewGroup = (ViewGroup)paramView.getParent(); 
    } 
    return null;
  }
  
  public void add(@NonNull Drawable paramDrawable) { this.mOverlayViewGroup.add(paramDrawable); }
  
  public void clear() { this.mOverlayViewGroup.clear(); }
  
  ViewGroup getOverlayView() { return this.mOverlayViewGroup; }
  
  boolean isEmpty() { return this.mOverlayViewGroup.isEmpty(); }
  
  public void remove(@NonNull Drawable paramDrawable) { this.mOverlayViewGroup.remove(paramDrawable); }
  
  static class OverlayViewGroup extends ViewGroup {
    static Method sInvalidateChildInParentFastMethod;
    
    ArrayList<Drawable> mDrawables = null;
    
    ViewGroup mHostView;
    
    View mRequestingView;
    
    ViewOverlayApi14 mViewOverlay;
    
    static  {
      try {
        sInvalidateChildInParentFastMethod = ViewGroup.class.getDeclaredMethod("invalidateChildInParentFast", new Class[] { int.class, int.class, Rect.class });
        return;
      } catch (NoSuchMethodException noSuchMethodException) {
        return;
      } 
    }
    
    OverlayViewGroup(Context param1Context, ViewGroup param1ViewGroup, View param1View, ViewOverlayApi14 param1ViewOverlayApi14) {
      super(param1Context);
      this.mHostView = param1ViewGroup;
      this.mRequestingView = param1View;
      setRight(param1ViewGroup.getWidth());
      setBottom(param1ViewGroup.getHeight());
      param1ViewGroup.addView(this);
      this.mViewOverlay = param1ViewOverlayApi14;
    }
    
    private void getOffset(int[] param1ArrayOfInt) {
      int[] arrayOfInt1 = new int[2];
      int[] arrayOfInt2 = new int[2];
      this.mHostView.getLocationOnScreen(arrayOfInt1);
      this.mRequestingView.getLocationOnScreen(arrayOfInt2);
      param1ArrayOfInt[0] = arrayOfInt2[0] - arrayOfInt1[0];
      param1ArrayOfInt[1] = arrayOfInt2[1] - arrayOfInt1[1];
    }
    
    public void add(Drawable param1Drawable) {
      if (this.mDrawables == null)
        this.mDrawables = new ArrayList(); 
      if (!this.mDrawables.contains(param1Drawable)) {
        this.mDrawables.add(param1Drawable);
        invalidate(param1Drawable.getBounds());
        param1Drawable.setCallback(this);
      } 
    }
    
    public void add(View param1View) {
      if (param1View.getParent() instanceof ViewGroup) {
        ViewGroup viewGroup = (ViewGroup)param1View.getParent();
        if (viewGroup != this.mHostView && viewGroup.getParent() != null && ViewCompat.isAttachedToWindow(viewGroup)) {
          int[] arrayOfInt1 = new int[2];
          int[] arrayOfInt2 = new int[2];
          viewGroup.getLocationOnScreen(arrayOfInt1);
          this.mHostView.getLocationOnScreen(arrayOfInt2);
          ViewCompat.offsetLeftAndRight(param1View, arrayOfInt1[0] - arrayOfInt2[0]);
          ViewCompat.offsetTopAndBottom(param1View, arrayOfInt1[1] - arrayOfInt2[1]);
        } 
        viewGroup.removeView(param1View);
        if (param1View.getParent() != null)
          viewGroup.removeView(param1View); 
      } 
      addView(param1View, getChildCount() - 1);
    }
    
    public void clear() {
      removeAllViews();
      if (this.mDrawables != null)
        this.mDrawables.clear(); 
    }
    
    protected void dispatchDraw(Canvas param1Canvas) {
      int i;
      int[] arrayOfInt1 = new int[2];
      int[] arrayOfInt2 = new int[2];
      this.mHostView.getLocationOnScreen(arrayOfInt1);
      this.mRequestingView.getLocationOnScreen(arrayOfInt2);
      byte b = 0;
      param1Canvas.translate((arrayOfInt2[0] - arrayOfInt1[0]), (arrayOfInt2[1] - arrayOfInt1[1]));
      param1Canvas.clipRect(new Rect(0, 0, this.mRequestingView.getWidth(), this.mRequestingView.getHeight()));
      super.dispatchDraw(param1Canvas);
      if (this.mDrawables == null) {
        i = 0;
      } else {
        i = this.mDrawables.size();
      } 
      while (b < i) {
        ((Drawable)this.mDrawables.get(b)).draw(param1Canvas);
        b++;
      } 
    }
    
    public boolean dispatchTouchEvent(MotionEvent param1MotionEvent) { return false; }
    
    public void invalidateChildFast(View param1View, Rect param1Rect) {
      if (this.mHostView != null) {
        int i = param1View.getLeft();
        int j = param1View.getTop();
        int[] arrayOfInt = new int[2];
        getOffset(arrayOfInt);
        param1Rect.offset(i + arrayOfInt[0], j + arrayOfInt[1]);
        this.mHostView.invalidate(param1Rect);
      } 
    }
    
    public ViewParent invalidateChildInParent(int[] param1ArrayOfInt, Rect param1Rect) {
      if (this.mHostView != null) {
        param1Rect.offset(param1ArrayOfInt[0], param1ArrayOfInt[1]);
        if (this.mHostView instanceof ViewGroup) {
          param1ArrayOfInt[0] = 0;
          param1ArrayOfInt[1] = 0;
          int[] arrayOfInt = new int[2];
          getOffset(arrayOfInt);
          param1Rect.offset(arrayOfInt[0], arrayOfInt[1]);
          return super.invalidateChildInParent(param1ArrayOfInt, param1Rect);
        } 
        invalidate(param1Rect);
      } 
      return null;
    }
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    protected ViewParent invalidateChildInParentFast(int param1Int1, int param1Int2, Rect param1Rect) {
      if (this.mHostView instanceof ViewGroup && sInvalidateChildInParentFastMethod != null)
        try {
          getOffset(new int[2]);
          sInvalidateChildInParentFastMethod.invoke(this.mHostView, new Object[] { Integer.valueOf(param1Int1), Integer.valueOf(param1Int2), param1Rect });
        } catch (IllegalAccessException param1Rect) {
          param1Rect.printStackTrace();
        } catch (InvocationTargetException param1Rect) {
          param1Rect.printStackTrace();
        }  
      return null;
    }
    
    public void invalidateDrawable(@NonNull Drawable param1Drawable) { invalidate(param1Drawable.getBounds()); }
    
    boolean isEmpty() { return (getChildCount() == 0 && (this.mDrawables == null || this.mDrawables.size() == 0)); }
    
    protected void onLayout(boolean param1Boolean, int param1Int1, int param1Int2, int param1Int3, int param1Int4) {}
    
    public void remove(Drawable param1Drawable) {
      if (this.mDrawables != null) {
        this.mDrawables.remove(param1Drawable);
        invalidate(param1Drawable.getBounds());
        param1Drawable.setCallback(null);
      } 
    }
    
    public void remove(View param1View) {
      removeView(param1View);
      if (isEmpty())
        this.mHostView.removeView(this); 
    }
    
    protected boolean verifyDrawable(@NonNull Drawable param1Drawable) { return (super.verifyDrawable(param1Drawable) || (this.mDrawables != null && this.mDrawables.contains(param1Drawable))); }
    
    static class TouchInterceptor extends View {
      TouchInterceptor(Context param2Context) { super(param2Context); }
    }
  }
  
  static class TouchInterceptor extends View {
    TouchInterceptor(Context param1Context) { super(param1Context); }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/transition/ViewOverlayApi14.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */