package android.support.v7.widget;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Shader;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.support.v4.graphics.drawable.WrappedDrawable;
import android.util.AttributeSet;
import android.widget.ProgressBar;

class AppCompatProgressBarHelper {
  private static final int[] TINT_ATTRS = { 16843067, 16843068 };
  
  private Bitmap mSampleTile;
  
  private final ProgressBar mView;
  
  AppCompatProgressBarHelper(ProgressBar paramProgressBar) { this.mView = paramProgressBar; }
  
  private Shape getDrawableShape() { return new RoundRectShape(new float[] { 5.0F, 5.0F, 5.0F, 5.0F, 5.0F, 5.0F, 5.0F, 5.0F }, null, null); }
  
  private Drawable tileify(Drawable paramDrawable, boolean paramBoolean) {
    BitmapDrawable bitmapDrawable;
    if (paramDrawable instanceof WrappedDrawable) {
      WrappedDrawable wrappedDrawable = (WrappedDrawable)paramDrawable;
      Drawable drawable = wrappedDrawable.getWrappedDrawable();
      if (drawable != null) {
        wrappedDrawable.setWrappedDrawable(tileify(drawable, paramBoolean));
        return paramDrawable;
      } 
    } else {
      if (paramDrawable instanceof LayerDrawable) {
        bitmapDrawable = (LayerDrawable)paramDrawable;
        int i = bitmapDrawable.getNumberOfLayers();
        Drawable[] arrayOfDrawable = new Drawable[i];
        byte b2 = 0;
        byte b1;
        for (b1 = 0; b1 < i; b1++) {
          int j = bitmapDrawable.getId(b1);
          Drawable drawable = bitmapDrawable.getDrawable(b1);
          if (j == 16908301 || j == 16908303) {
            paramBoolean = true;
          } else {
            paramBoolean = false;
          } 
          arrayOfDrawable[b1] = tileify(drawable, paramBoolean);
        } 
        LayerDrawable layerDrawable = new LayerDrawable(arrayOfDrawable);
        for (b1 = b2; b1 < i; b1++)
          layerDrawable.setId(b1, bitmapDrawable.getId(b1)); 
        return layerDrawable;
      } 
      if (bitmapDrawable instanceof BitmapDrawable) {
        bitmapDrawable = (BitmapDrawable)bitmapDrawable;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        if (this.mSampleTile == null)
          this.mSampleTile = bitmap; 
        ShapeDrawable shapeDrawable = new ShapeDrawable(getDrawableShape());
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
        shapeDrawable.getPaint().setShader(bitmapShader);
        shapeDrawable.getPaint().setColorFilter(bitmapDrawable.getPaint().getColorFilter());
        return paramBoolean ? new ClipDrawable(shapeDrawable, 3, 1) : shapeDrawable;
      } 
    } 
    return bitmapDrawable;
  }
  
  private Drawable tileifyIndeterminate(Drawable paramDrawable) {
    AnimationDrawable animationDrawable = paramDrawable;
    if (paramDrawable instanceof AnimationDrawable) {
      AnimationDrawable animationDrawable1 = (AnimationDrawable)paramDrawable;
      int i = animationDrawable1.getNumberOfFrames();
      animationDrawable = new AnimationDrawable();
      animationDrawable.setOneShot(animationDrawable1.isOneShot());
      for (byte b = 0; b < i; b++) {
        Drawable drawable = tileify(animationDrawable1.getFrame(b), true);
        drawable.setLevel(10000);
        animationDrawable.addFrame(drawable, animationDrawable1.getDuration(b));
      } 
      animationDrawable.setLevel(10000);
    } 
    return animationDrawable;
  }
  
  Bitmap getSampleTime() { return this.mSampleTile; }
  
  void loadFromAttributes(AttributeSet paramAttributeSet, int paramInt) {
    TintTypedArray tintTypedArray = TintTypedArray.obtainStyledAttributes(this.mView.getContext(), paramAttributeSet, TINT_ATTRS, paramInt, 0);
    Drawable drawable = tintTypedArray.getDrawableIfKnown(0);
    if (drawable != null)
      this.mView.setIndeterminateDrawable(tileifyIndeterminate(drawable)); 
    drawable = tintTypedArray.getDrawableIfKnown(1);
    if (drawable != null)
      this.mView.setProgressDrawable(tileify(drawable, false)); 
    tintTypedArray.recycle();
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/AppCompatProgressBarHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */