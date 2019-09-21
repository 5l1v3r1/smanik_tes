package android.support.v7.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import java.lang.ref.WeakReference;

class TintResources extends ResourcesWrapper {
  private final WeakReference<Context> mContextRef;
  
  public TintResources(@NonNull Context paramContext, @NonNull Resources paramResources) {
    super(paramResources);
    this.mContextRef = new WeakReference(paramContext);
  }
  
  public Drawable getDrawable(int paramInt) throws Resources.NotFoundException {
    Drawable drawable = super.getDrawable(paramInt);
    Context context = (Context)this.mContextRef.get();
    if (drawable != null && context != null)
      AppCompatDrawableManager.get().tintDrawableUsingColorFilter(context, paramInt, drawable); 
    return drawable;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/TintResources.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */