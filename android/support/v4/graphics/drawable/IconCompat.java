package android.support.v4.graphics.drawable;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.ContextCompat;

public class IconCompat {
  private static final float ADAPTIVE_ICON_INSET_FACTOR = 0.25F;
  
  private static final int AMBIENT_SHADOW_ALPHA = 30;
  
  private static final float BLUR_FACTOR = 0.010416667F;
  
  private static final float DEFAULT_VIEW_PORT_SCALE = 0.6666667F;
  
  private static final float ICON_DIAMETER_FACTOR = 0.9166667F;
  
  private static final int KEY_SHADOW_ALPHA = 61;
  
  private static final float KEY_SHADOW_OFFSET_FACTOR = 0.020833334F;
  
  private static final int TYPE_ADAPTIVE_BITMAP = 5;
  
  private static final int TYPE_BITMAP = 1;
  
  private static final int TYPE_DATA = 3;
  
  private static final int TYPE_RESOURCE = 2;
  
  private static final int TYPE_URI = 4;
  
  private int mInt1;
  
  private int mInt2;
  
  private Object mObj1;
  
  private final int mType;
  
  private IconCompat(int paramInt) { this.mType = paramInt; }
  
  @VisibleForTesting
  static Bitmap createLegacyIconFromAdaptiveIcon(Bitmap paramBitmap, boolean paramBoolean) {
    int i = (int)(Math.min(paramBitmap.getWidth(), paramBitmap.getHeight()) * 0.6666667F);
    Bitmap bitmap = Bitmap.createBitmap(i, i, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    Paint paint = new Paint(3);
    float f1 = i;
    float f2 = 0.5F * f1;
    float f3 = 0.9166667F * f2;
    if (paramBoolean) {
      float f = 0.010416667F * f1;
      paint.setColor(0);
      paint.setShadowLayer(f, 0.0F, f1 * 0.020833334F, 1023410176);
      canvas.drawCircle(f2, f2, f3, paint);
      paint.setShadowLayer(f, 0.0F, 0.0F, 503316480);
      canvas.drawCircle(f2, f2, f3, paint);
      paint.clearShadowLayer();
    } 
    paint.setColor(-16777216);
    BitmapShader bitmapShader = new BitmapShader(paramBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    Matrix matrix = new Matrix();
    matrix.setTranslate((-(paramBitmap.getWidth() - i) / 2), (-(paramBitmap.getHeight() - i) / 2));
    bitmapShader.setLocalMatrix(matrix);
    paint.setShader(bitmapShader);
    canvas.drawCircle(f2, f2, f3, paint);
    canvas.setBitmap(null);
    return bitmap;
  }
  
  public static IconCompat createWithAdaptiveBitmap(Bitmap paramBitmap) {
    if (paramBitmap == null)
      throw new IllegalArgumentException("Bitmap must not be null."); 
    IconCompat iconCompat = new IconCompat(5);
    iconCompat.mObj1 = paramBitmap;
    return iconCompat;
  }
  
  public static IconCompat createWithBitmap(Bitmap paramBitmap) {
    if (paramBitmap == null)
      throw new IllegalArgumentException("Bitmap must not be null."); 
    IconCompat iconCompat = new IconCompat(1);
    iconCompat.mObj1 = paramBitmap;
    return iconCompat;
  }
  
  public static IconCompat createWithContentUri(Uri paramUri) {
    if (paramUri == null)
      throw new IllegalArgumentException("Uri must not be null."); 
    return createWithContentUri(paramUri.toString());
  }
  
  public static IconCompat createWithContentUri(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException("Uri must not be null."); 
    IconCompat iconCompat = new IconCompat(4);
    iconCompat.mObj1 = paramString;
    return iconCompat;
  }
  
  public static IconCompat createWithData(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (paramArrayOfByte == null)
      throw new IllegalArgumentException("Data must not be null."); 
    IconCompat iconCompat = new IconCompat(3);
    iconCompat.mObj1 = paramArrayOfByte;
    iconCompat.mInt1 = paramInt1;
    iconCompat.mInt2 = paramInt2;
    return iconCompat;
  }
  
  public static IconCompat createWithResource(Context paramContext, @DrawableRes int paramInt) {
    if (paramContext == null)
      throw new IllegalArgumentException("Context must not be null."); 
    IconCompat iconCompat = new IconCompat(2);
    iconCompat.mInt1 = paramInt;
    iconCompat.mObj1 = paramContext;
    return iconCompat;
  }
  
  @Deprecated
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public void addToShortcutIntent(@NonNull Intent paramIntent) { addToShortcutIntent(paramIntent, null); }
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public void addToShortcutIntent(@NonNull Intent paramIntent, @Nullable Drawable paramDrawable) {
    Bitmap bitmap;
    int i = this.mType;
    if (i != 5) {
      Drawable drawable;
      Bitmap bitmap1;
      switch (i) {
        default:
          throw new IllegalArgumentException("Icon type not supported for intent shortcuts");
        case 2:
          if (paramDrawable == null) {
            paramIntent.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", Intent.ShortcutIconResource.fromContext((Context)this.mObj1, this.mInt1));
            return;
          } 
          bitmap = (Context)this.mObj1;
          drawable = ContextCompat.getDrawable(bitmap, this.mInt1);
          if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            i = ((ActivityManager)bitmap.getSystemService("activity")).getLauncherLargeIconSize();
            Bitmap bitmap2 = Bitmap.createBitmap(i, i, Bitmap.Config.ARGB_8888);
          } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
          } 
          drawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
          drawable.draw(new Canvas(bitmap));
          break;
        case 1:
          bitmap1 = (Bitmap)this.mObj1;
          bitmap = bitmap1;
          if (paramDrawable != null)
            bitmap = bitmap1.copy(bitmap1.getConfig(), true); 
          break;
      } 
    } else {
      bitmap = createLegacyIconFromAdaptiveIcon((Bitmap)this.mObj1, true);
    } 
    if (paramDrawable != null) {
      i = bitmap.getWidth();
      int j = bitmap.getHeight();
      paramDrawable.setBounds(i / 2, j / 2, i, j);
      paramDrawable.draw(new Canvas(bitmap));
    } 
    paramIntent.putExtra("android.intent.extra.shortcut.ICON", bitmap);
  }
  
  @RequiresApi(23)
  public Icon toIcon() {
    switch (this.mType) {
      default:
        throw new IllegalArgumentException("Unknown type");
      case 5:
        return (Build.VERSION.SDK_INT >= 26) ? Icon.createWithAdaptiveBitmap((Bitmap)this.mObj1) : Icon.createWithBitmap(createLegacyIconFromAdaptiveIcon((Bitmap)this.mObj1, false));
      case 4:
        return Icon.createWithContentUri((String)this.mObj1);
      case 3:
        return Icon.createWithData((byte[])this.mObj1, this.mInt1, this.mInt2);
      case 2:
        return Icon.createWithResource((Context)this.mObj1, this.mInt1);
      case 1:
        break;
    } 
    return Icon.createWithBitmap((Bitmap)this.mObj1);
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/graphics/drawable/IconCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */