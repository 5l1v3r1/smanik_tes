package android.support.v7.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.v7.app.AppCompatDelegate;
import java.lang.ref.WeakReference;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class VectorEnabledTintResources extends Resources {
  public static final int MAX_SDK_WHERE_REQUIRED = 20;
  
  private final WeakReference<Context> mContextRef;
  
  public VectorEnabledTintResources(@NonNull Context paramContext, @NonNull Resources paramResources) {
    super(paramResources.getAssets(), paramResources.getDisplayMetrics(), paramResources.getConfiguration());
    this.mContextRef = new WeakReference(paramContext);
  }
  
  public static boolean shouldBeUsed() { return (AppCompatDelegate.isCompatVectorFromResourcesEnabled() && Build.VERSION.SDK_INT <= 20); }
  
  public Drawable getDrawable(int paramInt) throws Resources.NotFoundException {
    Context context = (Context)this.mContextRef.get();
    return (context != null) ? AppCompatDrawableManager.get().onDrawableLoadedFromResources(context, this, paramInt) : super.getDrawable(paramInt);
  }
  
  final Drawable superGetDrawable(int paramInt) throws Resources.NotFoundException { return super.getDrawable(paramInt); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/VectorEnabledTintResources.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */