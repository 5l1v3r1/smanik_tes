package android.support.v7.widget;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class TintContextWrapper extends ContextWrapper {
  private static final Object CACHE_LOCK = new Object();
  
  private static ArrayList<WeakReference<TintContextWrapper>> sCache;
  
  private final Resources mResources;
  
  private final Resources.Theme mTheme;
  
  private TintContextWrapper(@NonNull Context paramContext) {
    super(paramContext);
    if (VectorEnabledTintResources.shouldBeUsed()) {
      this.mResources = new VectorEnabledTintResources(this, paramContext.getResources());
      this.mTheme = this.mResources.newTheme();
      this.mTheme.setTo(paramContext.getTheme());
      return;
    } 
    this.mResources = new TintResources(this, paramContext.getResources());
    this.mTheme = null;
  }
  
  private static boolean shouldWrap(@NonNull Context paramContext) {
    boolean bool2 = paramContext instanceof TintContextWrapper;
    boolean bool1 = false;
    if (!bool2 && !(paramContext.getResources() instanceof TintResources)) {
      if (paramContext.getResources() instanceof VectorEnabledTintResources)
        return false; 
      if (Build.VERSION.SDK_INT < 21 || VectorEnabledTintResources.shouldBeUsed())
        bool1 = true; 
      return bool1;
    } 
    return false;
  }
  
  public static Context wrap(@NonNull Context paramContext) {
    if (shouldWrap(paramContext))
      synchronized (CACHE_LOCK) {
        if (sCache == null) {
          sCache = new ArrayList();
        } else {
          for (int i = sCache.size() - 1;; i--) {
            if (i >= 0) {
              WeakReference weakReference = (WeakReference)sCache.get(i);
              if (weakReference == null || weakReference.get() == null)
                sCache.remove(i); 
            } else {
              for (i = sCache.size() - 1;; i--) {
                if (i >= 0) {
                  WeakReference weakReference = (WeakReference)sCache.get(i);
                  if (weakReference != null) {
                    TintContextWrapper tintContextWrapper1 = (TintContextWrapper)weakReference.get();
                  } else {
                    weakReference = null;
                  } 
                  if (weakReference != null && weakReference.getBaseContext() == paramContext)
                    return weakReference; 
                } else {
                  tintContextWrapper = new TintContextWrapper(paramContext);
                  sCache.add(new WeakReference(tintContextWrapper));
                  return tintContextWrapper;
                } 
              } 
            } 
          } 
          i--;
        } 
        TintContextWrapper tintContextWrapper = new TintContextWrapper(tintContextWrapper);
        sCache.add(new WeakReference(tintContextWrapper));
        return tintContextWrapper;
      }  
    return paramContext;
  }
  
  public AssetManager getAssets() { return this.mResources.getAssets(); }
  
  public Resources getResources() { return this.mResources; }
  
  public Resources.Theme getTheme() { return (this.mTheme == null) ? super.getTheme() : this.mTheme; }
  
  public void setTheme(int paramInt) {
    if (this.mTheme == null) {
      super.setTheme(paramInt);
      return;
    } 
    this.mTheme.applyStyle(paramInt, true);
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/TintContextWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */