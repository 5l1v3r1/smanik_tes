package android.support.v4.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;
import java.util.List;
import java.util.Map;

public abstract class SharedElementCallback {
  private static final String BUNDLE_SNAPSHOT_BITMAP = "sharedElement:snapshot:bitmap";
  
  private static final String BUNDLE_SNAPSHOT_IMAGE_MATRIX = "sharedElement:snapshot:imageMatrix";
  
  private static final String BUNDLE_SNAPSHOT_IMAGE_SCALETYPE = "sharedElement:snapshot:imageScaleType";
  
  private static int MAX_IMAGE_SIZE = 1048576;
  
  private Matrix mTempMatrix;
  
  static  {
  
  }
  
  private static Bitmap createDrawableBitmap(Drawable paramDrawable) {
    int i = paramDrawable.getIntrinsicWidth();
    int j = paramDrawable.getIntrinsicHeight();
    if (i <= 0 || j <= 0)
      return null; 
    float f = Math.min(1.0F, MAX_IMAGE_SIZE / (i * j));
    if (paramDrawable instanceof BitmapDrawable && f == 1.0F)
      return ((BitmapDrawable)paramDrawable).getBitmap(); 
    i = (int)(i * f);
    j = (int)(j * f);
    Bitmap bitmap = Bitmap.createBitmap(i, j, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    Rect rect = paramDrawable.getBounds();
    int k = rect.left;
    int m = rect.top;
    int n = rect.right;
    int i1 = rect.bottom;
    paramDrawable.setBounds(0, 0, i, j);
    paramDrawable.draw(canvas);
    paramDrawable.setBounds(k, m, n, i1);
    return bitmap;
  }
  
  public Parcelable onCaptureSharedElementSnapshot(View paramView, Matrix paramMatrix, RectF paramRectF) {
    float[] arrayOfFloat;
    Bundle bundle;
    if (paramView instanceof ImageView) {
      ImageView imageView = (ImageView)paramView;
      Drawable drawable1 = imageView.getDrawable();
      Drawable drawable2 = imageView.getBackground();
      if (drawable1 != null && drawable2 == null) {
        Bitmap bitmap1 = createDrawableBitmap(drawable1);
        if (bitmap1 != null) {
          bundle = new Bundle();
          bundle.putParcelable("sharedElement:snapshot:bitmap", bitmap1);
          bundle.putString("sharedElement:snapshot:imageScaleType", imageView.getScaleType().toString());
          if (imageView.getScaleType() == ImageView.ScaleType.MATRIX) {
            paramMatrix = imageView.getImageMatrix();
            arrayOfFloat = new float[9];
            paramMatrix.getValues(arrayOfFloat);
            bundle.putFloatArray("sharedElement:snapshot:imageMatrix", arrayOfFloat);
          } 
          return bundle;
        } 
      } 
    } 
    int j = Math.round(arrayOfFloat.width());
    int i = Math.round(arrayOfFloat.height());
    Object object = null;
    Bitmap bitmap = object;
    if (j > 0) {
      bitmap = object;
      if (i > 0) {
        float f = Math.min(1.0F, MAX_IMAGE_SIZE / (j * i));
        j = (int)(j * f);
        i = (int)(i * f);
        if (this.mTempMatrix == null)
          this.mTempMatrix = new Matrix(); 
        this.mTempMatrix.set(paramMatrix);
        this.mTempMatrix.postTranslate(-arrayOfFloat.left, -arrayOfFloat.top);
        this.mTempMatrix.postScale(f, f);
        bitmap = Bitmap.createBitmap(j, i, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.concat(this.mTempMatrix);
        bundle.draw(canvas);
      } 
    } 
    return bitmap;
  }
  
  public View onCreateSnapshotView(Context paramContext, Parcelable paramParcelable) {
    float[] arrayOfFloat;
    ImageView imageView1;
    boolean bool = paramParcelable instanceof Bundle;
    ImageView imageView2 = null;
    if (bool) {
      arrayOfFloat = (Bundle)paramParcelable;
      imageView2 = (Bitmap)arrayOfFloat.getParcelable("sharedElement:snapshot:bitmap");
      if (imageView2 == null)
        return null; 
      imageView1 = new ImageView(paramContext);
      imageView1.setImageBitmap(imageView2);
      imageView1.setScaleType(ImageView.ScaleType.valueOf(arrayOfFloat.getString("sharedElement:snapshot:imageScaleType")));
      ImageView imageView = imageView1;
      if (imageView1.getScaleType() == ImageView.ScaleType.MATRIX) {
        arrayOfFloat = arrayOfFloat.getFloatArray("sharedElement:snapshot:imageMatrix");
        Matrix matrix = new Matrix();
        matrix.setValues(arrayOfFloat);
        imageView1.setImageMatrix(matrix);
        return imageView1;
      } 
    } else if (arrayOfFloat instanceof Bitmap) {
      Bitmap bitmap = (Bitmap)arrayOfFloat;
      imageView2 = new ImageView(imageView1);
      imageView2.setImageBitmap(bitmap);
    } 
    return imageView2;
  }
  
  public void onMapSharedElements(List<String> paramList, Map<String, View> paramMap) {}
  
  public void onRejectSharedElements(List<View> paramList) {}
  
  public void onSharedElementEnd(List<String> paramList1, List<View> paramList2, List<View> paramList3) {}
  
  public void onSharedElementStart(List<String> paramList1, List<View> paramList2, List<View> paramList3) {}
  
  public void onSharedElementsArrived(List<String> paramList1, List<View> paramList2, OnSharedElementsReadyListener paramOnSharedElementsReadyListener) { paramOnSharedElementsReadyListener.onSharedElementsReady(); }
  
  public static interface OnSharedElementsReadyListener {
    void onSharedElementsReady();
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/app/SharedElementCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */