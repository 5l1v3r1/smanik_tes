package android.support.v4.print;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.support.annotation.RequiresApi;
import android.util.Log;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class PrintHelper {
  public static final int COLOR_MODE_COLOR = 2;
  
  public static final int COLOR_MODE_MONOCHROME = 1;
  
  public static final int ORIENTATION_LANDSCAPE = 1;
  
  public static final int ORIENTATION_PORTRAIT = 2;
  
  public static final int SCALE_MODE_FILL = 2;
  
  public static final int SCALE_MODE_FIT = 1;
  
  private final PrintHelperVersionImpl mImpl;
  
  public PrintHelper(Context paramContext) {
    if (Build.VERSION.SDK_INT >= 24) {
      this.mImpl = new PrintHelperApi24(paramContext);
      return;
    } 
    if (Build.VERSION.SDK_INT >= 23) {
      this.mImpl = new PrintHelperApi23(paramContext);
      return;
    } 
    if (Build.VERSION.SDK_INT >= 20) {
      this.mImpl = new PrintHelperApi20(paramContext);
      return;
    } 
    if (Build.VERSION.SDK_INT >= 19) {
      this.mImpl = new PrintHelperApi19(paramContext);
      return;
    } 
    this.mImpl = new PrintHelperStub(null);
  }
  
  public static boolean systemSupportsPrint() { return (Build.VERSION.SDK_INT >= 19); }
  
  public int getColorMode() { return this.mImpl.getColorMode(); }
  
  public int getOrientation() { return this.mImpl.getOrientation(); }
  
  public int getScaleMode() { return this.mImpl.getScaleMode(); }
  
  public void printBitmap(String paramString, Bitmap paramBitmap) { this.mImpl.printBitmap(paramString, paramBitmap, null); }
  
  public void printBitmap(String paramString, Bitmap paramBitmap, OnPrintFinishCallback paramOnPrintFinishCallback) { this.mImpl.printBitmap(paramString, paramBitmap, paramOnPrintFinishCallback); }
  
  public void printBitmap(String paramString, Uri paramUri) throws FileNotFoundException { this.mImpl.printBitmap(paramString, paramUri, null); }
  
  public void printBitmap(String paramString, Uri paramUri, OnPrintFinishCallback paramOnPrintFinishCallback) throws FileNotFoundException { this.mImpl.printBitmap(paramString, paramUri, paramOnPrintFinishCallback); }
  
  public void setColorMode(int paramInt) { this.mImpl.setColorMode(paramInt); }
  
  public void setOrientation(int paramInt) { this.mImpl.setOrientation(paramInt); }
  
  public void setScaleMode(int paramInt) { this.mImpl.setScaleMode(paramInt); }
  
  @Retention(RetentionPolicy.SOURCE)
  private static @interface ColorMode {}
  
  public static interface OnPrintFinishCallback {
    void onFinish();
  }
  
  @Retention(RetentionPolicy.SOURCE)
  private static @interface Orientation {}
  
  @RequiresApi(19)
  private static class PrintHelperApi19 implements PrintHelperVersionImpl {
    private static final String LOG_TAG = "PrintHelperApi19";
    
    private static final int MAX_PRINT_SIZE = 3500;
    
    int mColorMode = 2;
    
    final Context mContext;
    
    BitmapFactory.Options mDecodeOptions = null;
    
    protected boolean mIsMinMarginsHandlingCorrect = true;
    
    private final Object mLock = new Object();
    
    int mOrientation;
    
    protected boolean mPrintActivityRespectsOrientation = true;
    
    int mScaleMode = 2;
    
    PrintHelperApi19(Context param1Context) { this.mContext = param1Context; }
    
    private Bitmap convertBitmapForColorMode(Bitmap param1Bitmap, int param1Int) {
      if (param1Int != 1)
        return param1Bitmap; 
      Bitmap bitmap = Bitmap.createBitmap(param1Bitmap.getWidth(), param1Bitmap.getHeight(), Bitmap.Config.ARGB_8888);
      Canvas canvas = new Canvas(bitmap);
      Paint paint = new Paint();
      ColorMatrix colorMatrix = new ColorMatrix();
      colorMatrix.setSaturation(0.0F);
      paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
      canvas.drawBitmap(param1Bitmap, 0.0F, 0.0F, paint);
      canvas.setBitmap(null);
      return bitmap;
    }
    
    private Matrix getMatrix(int param1Int1, int param1Int2, RectF param1RectF, int param1Int3) {
      Matrix matrix = new Matrix();
      float f1 = param1RectF.width();
      float f2 = param1Int1;
      f1 /= f2;
      if (param1Int3 == 2) {
        f1 = Math.max(f1, param1RectF.height() / param1Int2);
      } else {
        f1 = Math.min(f1, param1RectF.height() / param1Int2);
      } 
      matrix.postScale(f1, f1);
      matrix.postTranslate((param1RectF.width() - f2 * f1) / 2.0F, (param1RectF.height() - param1Int2 * f1) / 2.0F);
      return matrix;
    }
    
    private static boolean isPortrait(Bitmap param1Bitmap) { return (param1Bitmap.getWidth() <= param1Bitmap.getHeight()); }
    
    private Bitmap loadBitmap(Uri param1Uri, BitmapFactory.Options param1Options) throws FileNotFoundException {
      if (param1Uri == null || this.mContext == null)
        throw new IllegalArgumentException("bad argument to loadBitmap"); 
      inputStream2 = null;
      try {
        inputStream = this.mContext.getContentResolver().openInputStream(param1Uri);
      } finally {
        param1Uri = null;
      } 
      if (inputStream1 != null)
        try {
          inputStream1.close();
        } catch (IOException inputStream1) {
          Log.w("PrintHelperApi19", "close fail ", inputStream1);
        }  
      throw param1Uri;
    }
    
    private Bitmap loadConstrainedBitmap(Uri param1Uri) throws FileNotFoundException {
      if (param1Uri == null || this.mContext == null)
        throw new IllegalArgumentException("bad argument to getScaledBitmap"); 
      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inJustDecodeBounds = true;
      loadBitmap(param1Uri, options);
      int i = options.outWidth;
      int j = options.outHeight;
      if (i > 0) {
        if (j <= 0)
          return null; 
        int m = Math.max(i, j);
        int k;
        for (k = 1; m > 3500; k <<= true)
          m >>>= 1; 
        if (k) {
          if (Math.min(i, j) / k <= 0)
            return null; 
          synchronized (this.mLock) {
            this.mDecodeOptions = new BitmapFactory.Options();
            this.mDecodeOptions.inMutable = true;
            this.mDecodeOptions.inSampleSize = k;
            BitmapFactory.Options options1 = this.mDecodeOptions;
            try {
              null = loadBitmap(param1Uri, options1);
            } finally {
              null = null;
            } 
          } 
        } 
        return null;
      } 
      return null;
    }
    
    private void writeBitmap(final PrintAttributes attributes, final int fittingMode, final Bitmap bitmap, final ParcelFileDescriptor fileDescriptor, final CancellationSignal cancellationSignal, final PrintDocumentAdapter.WriteResultCallback writeResultCallback) {
      final PrintAttributes pdfAttributes;
      if (this.mIsMinMarginsHandlingCorrect) {
        printAttributes = param1PrintAttributes;
      } else {
        printAttributes = copyAttributes(param1PrintAttributes).setMinMargins(new PrintAttributes.Margins(0, 0, 0, 0)).build();
      } 
      (new AsyncTask<Void, Void, Throwable>() {
          protected Throwable doInBackground(Void... param2VarArgs) {
            try {
              if (cancellationSignal.isCanceled())
                return null; 
              printedPdfDocument = new PrintedPdfDocument(PrintHelper.PrintHelperApi19.this.mContext, pdfAttributes);
              bitmap = PrintHelper.PrintHelperApi19.this.convertBitmapForColorMode(bitmap, pdfAttributes.getColorMode());
              boolean bool = cancellationSignal.isCanceled();
              if (bool)
                return null; 
              try {
                RectF rectF;
                PdfDocument.Page page = printedPdfDocument.startPage(1);
                if (PrintHelper.PrintHelperApi19.this.mIsMinMarginsHandlingCorrect) {
                  rectF = new RectF(page.getInfo().getContentRect());
                } else {
                  PrintedPdfDocument printedPdfDocument1 = new PrintedPdfDocument(PrintHelper.PrintHelperApi19.this.mContext, attributes);
                  PdfDocument.Page page1 = printedPdfDocument1.startPage(1);
                  rectF = new RectF(page1.getInfo().getContentRect());
                  printedPdfDocument1.finishPage(page1);
                  printedPdfDocument1.close();
                } 
                Matrix matrix = PrintHelper.PrintHelperApi19.this.getMatrix(bitmap.getWidth(), bitmap.getHeight(), rectF, fittingMode);
                if (!PrintHelper.PrintHelperApi19.this.mIsMinMarginsHandlingCorrect) {
                  matrix.postTranslate(rectF.left, rectF.top);
                  page.getCanvas().clipRect(rectF);
                } 
                page.getCanvas().drawBitmap(bitmap, matrix, null);
                printedPdfDocument.finishPage(page);
                bool = cancellationSignal.isCanceled();
                if (bool)
                  return null; 
                printedPdfDocument.writeTo(new FileOutputStream(fileDescriptor.getFileDescriptor()));
              } finally {
                printedPdfDocument.close();
                parcelFileDescriptor = fileDescriptor;
                if (parcelFileDescriptor != null)
                  try {
                    fileDescriptor.close();
                  } catch (IOException parcelFileDescriptor) {} 
                if (bitmap != bitmap)
                  bitmap.recycle(); 
              } 
            } catch (Throwable param2VarArgs) {
              return null;
            } 
          }
          
          protected void onPostExecute(Throwable param2Throwable) {
            if (cancellationSignal.isCanceled()) {
              writeResultCallback.onWriteCancelled();
              return;
            } 
            if (param2Throwable == null) {
              writeResultCallback.onWriteFinished(new PageRange[] { PageRange.ALL_PAGES });
              return;
            } 
            Log.e("PrintHelperApi19", "Error writing printed content", param2Throwable);
            writeResultCallback.onWriteFailed(null);
          }
        }).execute(new Void[0]);
    }
    
    protected PrintAttributes.Builder copyAttributes(PrintAttributes param1PrintAttributes) {
      PrintAttributes.Builder builder = (new PrintAttributes.Builder()).setMediaSize(param1PrintAttributes.getMediaSize()).setResolution(param1PrintAttributes.getResolution()).setMinMargins(param1PrintAttributes.getMinMargins());
      if (param1PrintAttributes.getColorMode() != 0)
        builder.setColorMode(param1PrintAttributes.getColorMode()); 
      return builder;
    }
    
    public int getColorMode() { return this.mColorMode; }
    
    public int getOrientation() { return (this.mOrientation == 0) ? 1 : this.mOrientation; }
    
    public int getScaleMode() { return this.mScaleMode; }
    
    public void printBitmap(final String jobName, final Bitmap bitmap, final PrintHelper.OnPrintFinishCallback callback) {
      PrintAttributes.MediaSize mediaSize;
      if (param1Bitmap == null)
        return; 
      final int fittingMode = this.mScaleMode;
      PrintManager printManager = (PrintManager)this.mContext.getSystemService("print");
      if (isPortrait(param1Bitmap)) {
        mediaSize = PrintAttributes.MediaSize.UNKNOWN_PORTRAIT;
      } else {
        mediaSize = PrintAttributes.MediaSize.UNKNOWN_LANDSCAPE;
      } 
      PrintAttributes printAttributes = (new PrintAttributes.Builder()).setMediaSize(mediaSize).setColorMode(this.mColorMode).build();
      printManager.print(param1String, new PrintDocumentAdapter() {
            private PrintAttributes mAttributes;
            
            public void onFinish() {
              if (callback != null)
                callback.onFinish(); 
            }
            
            public void onLayout(PrintAttributes param2PrintAttributes1, PrintAttributes param2PrintAttributes2, CancellationSignal param2CancellationSignal, LayoutResultCallback param2LayoutResultCallback, Bundle param2Bundle) {
              this.mAttributes = param2PrintAttributes2;
              param2LayoutResultCallback.onLayoutFinished((new PrintDocumentInfo.Builder(jobName)).setContentType(1).setPageCount(1).build(), param2PrintAttributes2.equals(param2PrintAttributes1) ^ true);
            }
            
            public void onWrite(PageRange[] param2ArrayOfPageRange, ParcelFileDescriptor param2ParcelFileDescriptor, CancellationSignal param2CancellationSignal, WriteResultCallback param2WriteResultCallback) { PrintHelper.PrintHelperApi19.this.writeBitmap(this.mAttributes, fittingMode, bitmap, param2ParcelFileDescriptor, param2CancellationSignal, param2WriteResultCallback); }
          }printAttributes);
    }
    
    public void printBitmap(final String jobName, final Uri imageFile, final PrintHelper.OnPrintFinishCallback callback) throws FileNotFoundException {
      PrintDocumentAdapter printDocumentAdapter = new PrintDocumentAdapter() {
          private PrintAttributes mAttributes;
          
          Bitmap mBitmap = null;
          
          AsyncTask<Uri, Boolean, Bitmap> mLoadBitmap;
          
          private void cancelLoad() {
            synchronized (PrintHelper.PrintHelperApi19.this.mLock) {
              if (PrintHelper.PrintHelperApi19.this.mDecodeOptions != null) {
                PrintHelper.PrintHelperApi19.this.mDecodeOptions.requestCancelDecode();
                PrintHelper.PrintHelperApi19.this.mDecodeOptions = null;
              } 
              return;
            } 
          }
          
          public void onFinish() {
            super.onFinish();
            cancelLoad();
            if (this.mLoadBitmap != null)
              this.mLoadBitmap.cancel(true); 
            if (callback != null)
              callback.onFinish(); 
            if (this.mBitmap != null) {
              this.mBitmap.recycle();
              this.mBitmap = null;
            } 
          }
          
          public void onLayout(PrintAttributes param2PrintAttributes1, PrintAttributes param2PrintAttributes2, CancellationSignal param2CancellationSignal, LayoutResultCallback param2LayoutResultCallback, Bundle param2Bundle) { // Byte code:
            //   0: aload_0
            //   1: monitorenter
            //   2: aload_0
            //   3: aload_2
            //   4: putfield mAttributes : Landroid/print/PrintAttributes;
            //   7: aload_0
            //   8: monitorexit
            //   9: aload_3
            //   10: invokevirtual isCanceled : ()Z
            //   13: ifeq -> 22
            //   16: aload #4
            //   18: invokevirtual onLayoutCancelled : ()V
            //   21: return
            //   22: aload_0
            //   23: getfield mBitmap : Landroid/graphics/Bitmap;
            //   26: ifnull -> 64
            //   29: aload #4
            //   31: new android/print/PrintDocumentInfo$Builder
            //   34: dup
            //   35: aload_0
            //   36: getfield val$jobName : Ljava/lang/String;
            //   39: invokespecial <init> : (Ljava/lang/String;)V
            //   42: iconst_1
            //   43: invokevirtual setContentType : (I)Landroid/print/PrintDocumentInfo$Builder;
            //   46: iconst_1
            //   47: invokevirtual setPageCount : (I)Landroid/print/PrintDocumentInfo$Builder;
            //   50: invokevirtual build : ()Landroid/print/PrintDocumentInfo;
            //   53: aload_2
            //   54: aload_1
            //   55: invokevirtual equals : (Ljava/lang/Object;)Z
            //   58: iconst_1
            //   59: ixor
            //   60: invokevirtual onLayoutFinished : (Landroid/print/PrintDocumentInfo;Z)V
            //   63: return
            //   64: aload_0
            //   65: new android/support/v4/print/PrintHelper$PrintHelperApi19$3$1
            //   68: dup
            //   69: aload_0
            //   70: aload_3
            //   71: aload_2
            //   72: aload_1
            //   73: aload #4
            //   75: invokespecial <init> : (Landroid/support/v4/print/PrintHelper$PrintHelperApi19$3;Landroid/os/CancellationSignal;Landroid/print/PrintAttributes;Landroid/print/PrintAttributes;Landroid/print/PrintDocumentAdapter$LayoutResultCallback;)V
            //   78: iconst_0
            //   79: anewarray android/net/Uri
            //   82: invokevirtual execute : ([Ljava/lang/Object;)Landroid/os/AsyncTask;
            //   85: putfield mLoadBitmap : Landroid/os/AsyncTask;
            //   88: return
            //   89: astore_1
            //   90: aload_0
            //   91: monitorexit
            //   92: aload_1
            //   93: athrow
            // Exception table:
            //   from	to	target	type
            //   2	9	89	finally
            //   90	92	89	finally }
          
          public void onWrite(PageRange[] param2ArrayOfPageRange, ParcelFileDescriptor param2ParcelFileDescriptor, CancellationSignal param2CancellationSignal, WriteResultCallback param2WriteResultCallback) { PrintHelper.PrintHelperApi19.this.writeBitmap(this.mAttributes, fittingMode, this.mBitmap, param2ParcelFileDescriptor, param2CancellationSignal, param2WriteResultCallback); }
        };
      PrintManager printManager = (PrintManager)this.mContext.getSystemService("print");
      PrintAttributes.Builder builder = new PrintAttributes.Builder();
      builder.setColorMode(this.mColorMode);
      if (this.mOrientation == 1 || this.mOrientation == 0) {
        builder.setMediaSize(PrintAttributes.MediaSize.UNKNOWN_LANDSCAPE);
      } else if (this.mOrientation == 2) {
        builder.setMediaSize(PrintAttributes.MediaSize.UNKNOWN_PORTRAIT);
      } 
      printManager.print(param1String, printDocumentAdapter, builder.build());
    }
    
    public void setColorMode(int param1Int) { this.mColorMode = param1Int; }
    
    public void setOrientation(int param1Int) { this.mOrientation = param1Int; }
    
    public void setScaleMode(int param1Int) { this.mScaleMode = param1Int; }
  }
  
  class null extends PrintDocumentAdapter {
    private PrintAttributes mAttributes;
    
    public void onFinish() {
      if (callback != null)
        callback.onFinish(); 
    }
    
    public void onLayout(PrintAttributes param1PrintAttributes1, PrintAttributes param1PrintAttributes2, CancellationSignal param1CancellationSignal, PrintDocumentAdapter.LayoutResultCallback param1LayoutResultCallback, Bundle param1Bundle) {
      this.mAttributes = param1PrintAttributes2;
      param1LayoutResultCallback.onLayoutFinished((new PrintDocumentInfo.Builder(jobName)).setContentType(1).setPageCount(1).build(), param1PrintAttributes2.equals(param1PrintAttributes1) ^ true);
    }
    
    public void onWrite(PageRange[] param1ArrayOfPageRange, ParcelFileDescriptor param1ParcelFileDescriptor, CancellationSignal param1CancellationSignal, PrintDocumentAdapter.WriteResultCallback param1WriteResultCallback) { this.this$0.writeBitmap(this.mAttributes, fittingMode, bitmap, param1ParcelFileDescriptor, param1CancellationSignal, param1WriteResultCallback); }
  }
  
  class null extends AsyncTask<Void, Void, Throwable> {
    protected Throwable doInBackground(Void... param1VarArgs) {
      try {
        if (cancellationSignal.isCanceled())
          return null; 
        printedPdfDocument = new PrintedPdfDocument(this.this$0.mContext, pdfAttributes);
        bitmap = this.this$0.convertBitmapForColorMode(bitmap, pdfAttributes.getColorMode());
        boolean bool = cancellationSignal.isCanceled();
        if (bool)
          return null; 
        try {
          RectF rectF;
          PdfDocument.Page page = printedPdfDocument.startPage(1);
          if (this.this$0.mIsMinMarginsHandlingCorrect) {
            rectF = new RectF(page.getInfo().getContentRect());
          } else {
            PrintedPdfDocument printedPdfDocument1 = new PrintedPdfDocument(this.this$0.mContext, attributes);
            PdfDocument.Page page1 = printedPdfDocument1.startPage(1);
            rectF = new RectF(page1.getInfo().getContentRect());
            printedPdfDocument1.finishPage(page1);
            printedPdfDocument1.close();
          } 
          Matrix matrix = this.this$0.getMatrix(bitmap.getWidth(), bitmap.getHeight(), rectF, fittingMode);
          if (!this.this$0.mIsMinMarginsHandlingCorrect) {
            matrix.postTranslate(rectF.left, rectF.top);
            page.getCanvas().clipRect(rectF);
          } 
          page.getCanvas().drawBitmap(bitmap, matrix, null);
          printedPdfDocument.finishPage(page);
          bool = cancellationSignal.isCanceled();
          if (bool)
            return null; 
          printedPdfDocument.writeTo(new FileOutputStream(fileDescriptor.getFileDescriptor()));
        } finally {
          printedPdfDocument.close();
          parcelFileDescriptor = fileDescriptor;
          if (parcelFileDescriptor != null)
            try {
              fileDescriptor.close();
            } catch (IOException parcelFileDescriptor) {} 
          if (bitmap != bitmap)
            bitmap.recycle(); 
        } 
      } catch (Throwable param1VarArgs) {
        return null;
      } 
    }
    
    protected void onPostExecute(Throwable param1Throwable) {
      if (cancellationSignal.isCanceled()) {
        writeResultCallback.onWriteCancelled();
        return;
      } 
      if (param1Throwable == null) {
        writeResultCallback.onWriteFinished(new PageRange[] { PageRange.ALL_PAGES });
        return;
      } 
      Log.e("PrintHelperApi19", "Error writing printed content", param1Throwable);
      writeResultCallback.onWriteFailed(null);
    }
  }
  
  class null extends PrintDocumentAdapter {
    private PrintAttributes mAttributes;
    
    Bitmap mBitmap = null;
    
    AsyncTask<Uri, Boolean, Bitmap> mLoadBitmap;
    
    private void cancelLoad() {
      synchronized (this.this$0.mLock) {
        if (this.this$0.mDecodeOptions != null) {
          this.this$0.mDecodeOptions.requestCancelDecode();
          this.this$0.mDecodeOptions = null;
        } 
        return;
      } 
    }
    
    public void onFinish() {
      super.onFinish();
      cancelLoad();
      if (this.mLoadBitmap != null)
        this.mLoadBitmap.cancel(true); 
      if (callback != null)
        callback.onFinish(); 
      if (this.mBitmap != null) {
        this.mBitmap.recycle();
        this.mBitmap = null;
      } 
    }
    
    public void onLayout(PrintAttributes param1PrintAttributes1, PrintAttributes param1PrintAttributes2, CancellationSignal param1CancellationSignal, PrintDocumentAdapter.LayoutResultCallback param1LayoutResultCallback, Bundle param1Bundle) { // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: aload_2
      //   4: putfield mAttributes : Landroid/print/PrintAttributes;
      //   7: aload_0
      //   8: monitorexit
      //   9: aload_3
      //   10: invokevirtual isCanceled : ()Z
      //   13: ifeq -> 22
      //   16: aload #4
      //   18: invokevirtual onLayoutCancelled : ()V
      //   21: return
      //   22: aload_0
      //   23: getfield mBitmap : Landroid/graphics/Bitmap;
      //   26: ifnull -> 64
      //   29: aload #4
      //   31: new android/print/PrintDocumentInfo$Builder
      //   34: dup
      //   35: aload_0
      //   36: getfield val$jobName : Ljava/lang/String;
      //   39: invokespecial <init> : (Ljava/lang/String;)V
      //   42: iconst_1
      //   43: invokevirtual setContentType : (I)Landroid/print/PrintDocumentInfo$Builder;
      //   46: iconst_1
      //   47: invokevirtual setPageCount : (I)Landroid/print/PrintDocumentInfo$Builder;
      //   50: invokevirtual build : ()Landroid/print/PrintDocumentInfo;
      //   53: aload_2
      //   54: aload_1
      //   55: invokevirtual equals : (Ljava/lang/Object;)Z
      //   58: iconst_1
      //   59: ixor
      //   60: invokevirtual onLayoutFinished : (Landroid/print/PrintDocumentInfo;Z)V
      //   63: return
      //   64: aload_0
      //   65: new android/support/v4/print/PrintHelper$PrintHelperApi19$3$1
      //   68: dup
      //   69: aload_0
      //   70: aload_3
      //   71: aload_2
      //   72: aload_1
      //   73: aload #4
      //   75: invokespecial <init> : (Landroid/support/v4/print/PrintHelper$PrintHelperApi19$3;Landroid/os/CancellationSignal;Landroid/print/PrintAttributes;Landroid/print/PrintAttributes;Landroid/print/PrintDocumentAdapter$LayoutResultCallback;)V
      //   78: iconst_0
      //   79: anewarray android/net/Uri
      //   82: invokevirtual execute : ([Ljava/lang/Object;)Landroid/os/AsyncTask;
      //   85: putfield mLoadBitmap : Landroid/os/AsyncTask;
      //   88: return
      //   89: astore_1
      //   90: aload_0
      //   91: monitorexit
      //   92: aload_1
      //   93: athrow
      // Exception table:
      //   from	to	target	type
      //   2	9	89	finally
      //   90	92	89	finally }
    
    public void onWrite(PageRange[] param1ArrayOfPageRange, ParcelFileDescriptor param1ParcelFileDescriptor, CancellationSignal param1CancellationSignal, PrintDocumentAdapter.WriteResultCallback param1WriteResultCallback) { this.this$0.writeBitmap(this.mAttributes, fittingMode, this.mBitmap, param1ParcelFileDescriptor, param1CancellationSignal, param1WriteResultCallback); }
  }
  
  class null extends AsyncTask<Uri, Boolean, Bitmap> {
    protected Bitmap doInBackground(Uri... param1VarArgs) {
      try {
        return this.this$1.this$0.loadConstrainedBitmap(this.this$1.val$imageFile);
      } catch (FileNotFoundException param1VarArgs) {
        return null;
      } 
    }
    
    protected void onCancelled(Bitmap param1Bitmap) {
      layoutResultCallback.onLayoutCancelled();
      this.this$1.mLoadBitmap = null;
    }
    
    protected void onPostExecute(Bitmap param1Bitmap) { // Byte code:
      //   0: aload_0
      //   1: aload_1
      //   2: invokespecial onPostExecute : (Ljava/lang/Object;)V
      //   5: aload_1
      //   6: astore_3
      //   7: aload_1
      //   8: ifnull -> 116
      //   11: aload_0
      //   12: getfield this$1 : Landroid/support/v4/print/PrintHelper$PrintHelperApi19$3;
      //   15: getfield this$0 : Landroid/support/v4/print/PrintHelper$PrintHelperApi19;
      //   18: getfield mPrintActivityRespectsOrientation : Z
      //   21: ifeq -> 39
      //   24: aload_1
      //   25: astore_3
      //   26: aload_0
      //   27: getfield this$1 : Landroid/support/v4/print/PrintHelper$PrintHelperApi19$3;
      //   30: getfield this$0 : Landroid/support/v4/print/PrintHelper$PrintHelperApi19;
      //   33: getfield mOrientation : I
      //   36: ifne -> 116
      //   39: aload_0
      //   40: monitorenter
      //   41: aload_0
      //   42: getfield this$1 : Landroid/support/v4/print/PrintHelper$PrintHelperApi19$3;
      //   45: invokestatic access$500 : (Landroid/support/v4/print/PrintHelper$PrintHelperApi19$3;)Landroid/print/PrintAttributes;
      //   48: invokevirtual getMediaSize : ()Landroid/print/PrintAttributes$MediaSize;
      //   51: astore #4
      //   53: aload_0
      //   54: monitorexit
      //   55: aload_1
      //   56: astore_3
      //   57: aload #4
      //   59: ifnull -> 116
      //   62: aload_1
      //   63: astore_3
      //   64: aload #4
      //   66: invokevirtual isPortrait : ()Z
      //   69: aload_1
      //   70: invokestatic access$600 : (Landroid/graphics/Bitmap;)Z
      //   73: if_icmpeq -> 116
      //   76: new android/graphics/Matrix
      //   79: dup
      //   80: invokespecial <init> : ()V
      //   83: astore_3
      //   84: aload_3
      //   85: ldc 90.0
      //   87: invokevirtual postRotate : (F)Z
      //   90: pop
      //   91: aload_1
      //   92: iconst_0
      //   93: iconst_0
      //   94: aload_1
      //   95: invokevirtual getWidth : ()I
      //   98: aload_1
      //   99: invokevirtual getHeight : ()I
      //   102: aload_3
      //   103: iconst_1
      //   104: invokestatic createBitmap : (Landroid/graphics/Bitmap;IIIILandroid/graphics/Matrix;Z)Landroid/graphics/Bitmap;
      //   107: astore_3
      //   108: goto -> 116
      //   111: astore_1
      //   112: aload_0
      //   113: monitorexit
      //   114: aload_1
      //   115: athrow
      //   116: aload_0
      //   117: getfield this$1 : Landroid/support/v4/print/PrintHelper$PrintHelperApi19$3;
      //   120: aload_3
      //   121: putfield mBitmap : Landroid/graphics/Bitmap;
      //   124: aload_3
      //   125: ifnull -> 180
      //   128: new android/print/PrintDocumentInfo$Builder
      //   131: dup
      //   132: aload_0
      //   133: getfield this$1 : Landroid/support/v4/print/PrintHelper$PrintHelperApi19$3;
      //   136: getfield val$jobName : Ljava/lang/String;
      //   139: invokespecial <init> : (Ljava/lang/String;)V
      //   142: iconst_1
      //   143: invokevirtual setContentType : (I)Landroid/print/PrintDocumentInfo$Builder;
      //   146: iconst_1
      //   147: invokevirtual setPageCount : (I)Landroid/print/PrintDocumentInfo$Builder;
      //   150: invokevirtual build : ()Landroid/print/PrintDocumentInfo;
      //   153: astore_1
      //   154: aload_0
      //   155: getfield val$newPrintAttributes : Landroid/print/PrintAttributes;
      //   158: aload_0
      //   159: getfield val$oldPrintAttributes : Landroid/print/PrintAttributes;
      //   162: invokevirtual equals : (Ljava/lang/Object;)Z
      //   165: istore_2
      //   166: aload_0
      //   167: getfield val$layoutResultCallback : Landroid/print/PrintDocumentAdapter$LayoutResultCallback;
      //   170: aload_1
      //   171: iconst_1
      //   172: iload_2
      //   173: ixor
      //   174: invokevirtual onLayoutFinished : (Landroid/print/PrintDocumentInfo;Z)V
      //   177: goto -> 188
      //   180: aload_0
      //   181: getfield val$layoutResultCallback : Landroid/print/PrintDocumentAdapter$LayoutResultCallback;
      //   184: aconst_null
      //   185: invokevirtual onLayoutFailed : (Ljava/lang/CharSequence;)V
      //   188: aload_0
      //   189: getfield this$1 : Landroid/support/v4/print/PrintHelper$PrintHelperApi19$3;
      //   192: aconst_null
      //   193: putfield mLoadBitmap : Landroid/os/AsyncTask;
      //   196: return
      // Exception table:
      //   from	to	target	type
      //   41	55	111	finally
      //   112	114	111	finally }
    
    protected void onPreExecute() { cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
            public void onCancel() {
              PrintHelper.PrintHelperApi19.null.null.this.this$1.cancelLoad();
              PrintHelper.PrintHelperApi19.null.null.this.cancel(false);
            }
          }); }
  }
  
  class null implements CancellationSignal.OnCancelListener {
    public void onCancel() {
      this.this$2.this$1.cancelLoad();
      this.this$2.cancel(false);
    }
  }
  
  @RequiresApi(20)
  private static class PrintHelperApi20 extends PrintHelperApi19 {
    PrintHelperApi20(Context param1Context) { super(param1Context); }
  }
  
  @RequiresApi(23)
  private static class PrintHelperApi23 extends PrintHelperApi20 {
    PrintHelperApi23(Context param1Context) { super(param1Context); }
    
    protected PrintAttributes.Builder copyAttributes(PrintAttributes param1PrintAttributes) {
      PrintAttributes.Builder builder = super.copyAttributes(param1PrintAttributes);
      if (param1PrintAttributes.getDuplexMode() != 0)
        builder.setDuplexMode(param1PrintAttributes.getDuplexMode()); 
      return builder;
    }
  }
  
  @RequiresApi(24)
  private static class PrintHelperApi24 extends PrintHelperApi23 {
    PrintHelperApi24(Context param1Context) { super(param1Context); }
  }
  
  private static final class PrintHelperStub implements PrintHelperVersionImpl {
    int mColorMode = 2;
    
    int mOrientation = 1;
    
    int mScaleMode = 2;
    
    private PrintHelperStub() {}
    
    public int getColorMode() { return this.mColorMode; }
    
    public int getOrientation() { return this.mOrientation; }
    
    public int getScaleMode() { return this.mScaleMode; }
    
    public void printBitmap(String param1String, Bitmap param1Bitmap, PrintHelper.OnPrintFinishCallback param1OnPrintFinishCallback) {}
    
    public void printBitmap(String param1String, Uri param1Uri, PrintHelper.OnPrintFinishCallback param1OnPrintFinishCallback) throws FileNotFoundException {}
    
    public void setColorMode(int param1Int) { this.mColorMode = param1Int; }
    
    public void setOrientation(int param1Int) { this.mOrientation = param1Int; }
    
    public void setScaleMode(int param1Int) { this.mScaleMode = param1Int; }
  }
  
  static interface PrintHelperVersionImpl {
    int getColorMode();
    
    int getOrientation();
    
    int getScaleMode();
    
    void printBitmap(String param1String, Bitmap param1Bitmap, PrintHelper.OnPrintFinishCallback param1OnPrintFinishCallback);
    
    void printBitmap(String param1String, Uri param1Uri, PrintHelper.OnPrintFinishCallback param1OnPrintFinishCallback) throws FileNotFoundException;
    
    void setColorMode(int param1Int);
    
    void setOrientation(int param1Int);
    
    void setScaleMode(int param1Int);
  }
  
  @Retention(RetentionPolicy.SOURCE)
  private static @interface ScaleMode {}
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/print/PrintHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */