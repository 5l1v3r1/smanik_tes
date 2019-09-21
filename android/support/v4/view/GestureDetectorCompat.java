package android.support.v4.view;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

public final class GestureDetectorCompat {
  private final GestureDetectorCompatImpl mImpl;
  
  public GestureDetectorCompat(Context paramContext, GestureDetector.OnGestureListener paramOnGestureListener) { this(paramContext, paramOnGestureListener, null); }
  
  public GestureDetectorCompat(Context paramContext, GestureDetector.OnGestureListener paramOnGestureListener, Handler paramHandler) {
    if (Build.VERSION.SDK_INT > 17) {
      this.mImpl = new GestureDetectorCompatImplJellybeanMr2(paramContext, paramOnGestureListener, paramHandler);
      return;
    } 
    this.mImpl = new GestureDetectorCompatImplBase(paramContext, paramOnGestureListener, paramHandler);
  }
  
  public boolean isLongpressEnabled() { return this.mImpl.isLongpressEnabled(); }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent) { return this.mImpl.onTouchEvent(paramMotionEvent); }
  
  public void setIsLongpressEnabled(boolean paramBoolean) { this.mImpl.setIsLongpressEnabled(paramBoolean); }
  
  public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener paramOnDoubleTapListener) { this.mImpl.setOnDoubleTapListener(paramOnDoubleTapListener); }
  
  static interface GestureDetectorCompatImpl {
    boolean isLongpressEnabled();
    
    boolean onTouchEvent(MotionEvent param1MotionEvent);
    
    void setIsLongpressEnabled(boolean param1Boolean);
    
    void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener param1OnDoubleTapListener);
  }
  
  static class GestureDetectorCompatImplBase implements GestureDetectorCompatImpl {
    private static final int DOUBLE_TAP_TIMEOUT;
    
    private static final int LONGPRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();
    
    private static final int LONG_PRESS = 2;
    
    private static final int SHOW_PRESS = 1;
    
    private static final int TAP = 3;
    
    private static final int TAP_TIMEOUT = ViewConfiguration.getTapTimeout();
    
    private boolean mAlwaysInBiggerTapRegion;
    
    private boolean mAlwaysInTapRegion;
    
    MotionEvent mCurrentDownEvent;
    
    boolean mDeferConfirmSingleTap;
    
    GestureDetector.OnDoubleTapListener mDoubleTapListener;
    
    private int mDoubleTapSlopSquare;
    
    private float mDownFocusX;
    
    private float mDownFocusY;
    
    private final Handler mHandler;
    
    private boolean mInLongPress;
    
    private boolean mIsDoubleTapping;
    
    private boolean mIsLongpressEnabled;
    
    private float mLastFocusX;
    
    private float mLastFocusY;
    
    final GestureDetector.OnGestureListener mListener;
    
    private int mMaximumFlingVelocity;
    
    private int mMinimumFlingVelocity;
    
    private MotionEvent mPreviousUpEvent;
    
    boolean mStillDown;
    
    private int mTouchSlopSquare;
    
    private VelocityTracker mVelocityTracker;
    
    static  {
      DOUBLE_TAP_TIMEOUT = ViewConfiguration.getDoubleTapTimeout();
    }
    
    GestureDetectorCompatImplBase(Context param1Context, GestureDetector.OnGestureListener param1OnGestureListener, Handler param1Handler) {
      if (param1Handler != null) {
        this.mHandler = new GestureHandler(param1Handler);
      } else {
        this.mHandler = new GestureHandler();
      } 
      this.mListener = param1OnGestureListener;
      if (param1OnGestureListener instanceof GestureDetector.OnDoubleTapListener)
        setOnDoubleTapListener((GestureDetector.OnDoubleTapListener)param1OnGestureListener); 
      init(param1Context);
    }
    
    private void cancel() {
      this.mHandler.removeMessages(1);
      this.mHandler.removeMessages(2);
      this.mHandler.removeMessages(3);
      this.mVelocityTracker.recycle();
      this.mVelocityTracker = null;
      this.mIsDoubleTapping = false;
      this.mStillDown = false;
      this.mAlwaysInTapRegion = false;
      this.mAlwaysInBiggerTapRegion = false;
      this.mDeferConfirmSingleTap = false;
      if (this.mInLongPress)
        this.mInLongPress = false; 
    }
    
    private void cancelTaps() {
      this.mHandler.removeMessages(1);
      this.mHandler.removeMessages(2);
      this.mHandler.removeMessages(3);
      this.mIsDoubleTapping = false;
      this.mAlwaysInTapRegion = false;
      this.mAlwaysInBiggerTapRegion = false;
      this.mDeferConfirmSingleTap = false;
      if (this.mInLongPress)
        this.mInLongPress = false; 
    }
    
    private void init(Context param1Context) {
      if (param1Context == null)
        throw new IllegalArgumentException("Context must not be null"); 
      if (this.mListener == null)
        throw new IllegalArgumentException("OnGestureListener must not be null"); 
      this.mIsLongpressEnabled = true;
      ViewConfiguration viewConfiguration = ViewConfiguration.get(param1Context);
      int i = viewConfiguration.getScaledTouchSlop();
      int j = viewConfiguration.getScaledDoubleTapSlop();
      this.mMinimumFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
      this.mMaximumFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
      this.mTouchSlopSquare = i * i;
      this.mDoubleTapSlopSquare = j * j;
    }
    
    private boolean isConsideredDoubleTap(MotionEvent param1MotionEvent1, MotionEvent param1MotionEvent2, MotionEvent param1MotionEvent3) {
      boolean bool2 = this.mAlwaysInBiggerTapRegion;
      boolean bool1 = false;
      if (!bool2)
        return false; 
      if (param1MotionEvent3.getEventTime() - param1MotionEvent2.getEventTime() > DOUBLE_TAP_TIMEOUT)
        return false; 
      int i = (int)param1MotionEvent1.getX() - (int)param1MotionEvent3.getX();
      int j = (int)param1MotionEvent1.getY() - (int)param1MotionEvent3.getY();
      if (i * i + j * j < this.mDoubleTapSlopSquare)
        bool1 = true; 
      return bool1;
    }
    
    void dispatchLongPress() {
      this.mHandler.removeMessages(3);
      this.mDeferConfirmSingleTap = false;
      this.mInLongPress = true;
      this.mListener.onLongPress(this.mCurrentDownEvent);
    }
    
    public boolean isLongpressEnabled() { return this.mIsLongpressEnabled; }
    
    public boolean onTouchEvent(MotionEvent param1MotionEvent) { // Byte code:
      //   0: aload_1
      //   1: invokevirtual getAction : ()I
      //   4: istore #6
      //   6: aload_0
      //   7: getfield mVelocityTracker : Landroid/view/VelocityTracker;
      //   10: ifnonnull -> 20
      //   13: aload_0
      //   14: invokestatic obtain : ()Landroid/view/VelocityTracker;
      //   17: putfield mVelocityTracker : Landroid/view/VelocityTracker;
      //   20: aload_0
      //   21: getfield mVelocityTracker : Landroid/view/VelocityTracker;
      //   24: aload_1
      //   25: invokevirtual addMovement : (Landroid/view/MotionEvent;)V
      //   28: iload #6
      //   30: sipush #255
      //   33: iand
      //   34: istore #10
      //   36: iconst_0
      //   37: istore #12
      //   39: iload #10
      //   41: bipush #6
      //   43: if_icmpne -> 52
      //   46: iconst_1
      //   47: istore #6
      //   49: goto -> 55
      //   52: iconst_0
      //   53: istore #6
      //   55: iload #6
      //   57: ifeq -> 69
      //   60: aload_1
      //   61: invokevirtual getActionIndex : ()I
      //   64: istore #7
      //   66: goto -> 72
      //   69: iconst_m1
      //   70: istore #7
      //   72: aload_1
      //   73: invokevirtual getPointerCount : ()I
      //   76: istore #9
      //   78: iconst_0
      //   79: istore #8
      //   81: fconst_0
      //   82: fstore_3
      //   83: fconst_0
      //   84: fstore_2
      //   85: iload #8
      //   87: iload #9
      //   89: if_icmpge -> 129
      //   92: iload #7
      //   94: iload #8
      //   96: if_icmpne -> 102
      //   99: goto -> 120
      //   102: fload_3
      //   103: aload_1
      //   104: iload #8
      //   106: invokevirtual getX : (I)F
      //   109: fadd
      //   110: fstore_3
      //   111: fload_2
      //   112: aload_1
      //   113: iload #8
      //   115: invokevirtual getY : (I)F
      //   118: fadd
      //   119: fstore_2
      //   120: iload #8
      //   122: iconst_1
      //   123: iadd
      //   124: istore #8
      //   126: goto -> 85
      //   129: iload #6
      //   131: ifeq -> 143
      //   134: iload #9
      //   136: iconst_1
      //   137: isub
      //   138: istore #6
      //   140: goto -> 147
      //   143: iload #9
      //   145: istore #6
      //   147: iload #6
      //   149: i2f
      //   150: fstore #4
      //   152: fload_3
      //   153: fload #4
      //   155: fdiv
      //   156: fstore_3
      //   157: fload_2
      //   158: fload #4
      //   160: fdiv
      //   161: fstore_2
      //   162: iload #10
      //   164: tableswitch default -> 208, 0 -> 900, 1 -> 628, 2 -> 389, 3 -> 383, 4 -> 208, 5 -> 357, 6 -> 210
      //   208: iconst_0
      //   209: ireturn
      //   210: aload_0
      //   211: fload_3
      //   212: putfield mLastFocusX : F
      //   215: aload_0
      //   216: fload_3
      //   217: putfield mDownFocusX : F
      //   220: aload_0
      //   221: fload_2
      //   222: putfield mLastFocusY : F
      //   225: aload_0
      //   226: fload_2
      //   227: putfield mDownFocusY : F
      //   230: aload_0
      //   231: getfield mVelocityTracker : Landroid/view/VelocityTracker;
      //   234: sipush #1000
      //   237: aload_0
      //   238: getfield mMaximumFlingVelocity : I
      //   241: i2f
      //   242: invokevirtual computeCurrentVelocity : (IF)V
      //   245: aload_1
      //   246: invokevirtual getActionIndex : ()I
      //   249: istore #7
      //   251: aload_1
      //   252: iload #7
      //   254: invokevirtual getPointerId : (I)I
      //   257: istore #6
      //   259: aload_0
      //   260: getfield mVelocityTracker : Landroid/view/VelocityTracker;
      //   263: iload #6
      //   265: invokevirtual getXVelocity : (I)F
      //   268: fstore_2
      //   269: aload_0
      //   270: getfield mVelocityTracker : Landroid/view/VelocityTracker;
      //   273: iload #6
      //   275: invokevirtual getYVelocity : (I)F
      //   278: fstore_3
      //   279: iconst_0
      //   280: istore #6
      //   282: iload #12
      //   284: istore #11
      //   286: iload #6
      //   288: iload #9
      //   290: if_icmpge -> 1161
      //   293: iload #6
      //   295: iload #7
      //   297: if_icmpne -> 303
      //   300: goto -> 348
      //   303: aload_1
      //   304: iload #6
      //   306: invokevirtual getPointerId : (I)I
      //   309: istore #8
      //   311: aload_0
      //   312: getfield mVelocityTracker : Landroid/view/VelocityTracker;
      //   315: iload #8
      //   317: invokevirtual getXVelocity : (I)F
      //   320: fload_2
      //   321: fmul
      //   322: aload_0
      //   323: getfield mVelocityTracker : Landroid/view/VelocityTracker;
      //   326: iload #8
      //   328: invokevirtual getYVelocity : (I)F
      //   331: fload_3
      //   332: fmul
      //   333: fadd
      //   334: fconst_0
      //   335: fcmpg
      //   336: ifge -> 348
      //   339: aload_0
      //   340: getfield mVelocityTracker : Landroid/view/VelocityTracker;
      //   343: invokevirtual clear : ()V
      //   346: iconst_0
      //   347: ireturn
      //   348: iload #6
      //   350: iconst_1
      //   351: iadd
      //   352: istore #6
      //   354: goto -> 282
      //   357: aload_0
      //   358: fload_3
      //   359: putfield mLastFocusX : F
      //   362: aload_0
      //   363: fload_3
      //   364: putfield mDownFocusX : F
      //   367: aload_0
      //   368: fload_2
      //   369: putfield mLastFocusY : F
      //   372: aload_0
      //   373: fload_2
      //   374: putfield mDownFocusY : F
      //   377: aload_0
      //   378: invokespecial cancelTaps : ()V
      //   381: iconst_0
      //   382: ireturn
      //   383: aload_0
      //   384: invokespecial cancel : ()V
      //   387: iconst_0
      //   388: ireturn
      //   389: aload_0
      //   390: getfield mInLongPress : Z
      //   393: ifeq -> 398
      //   396: iconst_0
      //   397: ireturn
      //   398: aload_0
      //   399: getfield mLastFocusX : F
      //   402: fload_3
      //   403: fsub
      //   404: fstore #4
      //   406: aload_0
      //   407: getfield mLastFocusY : F
      //   410: fload_2
      //   411: fsub
      //   412: fstore #5
      //   414: aload_0
      //   415: getfield mIsDoubleTapping : Z
      //   418: ifeq -> 434
      //   421: iconst_0
      //   422: aload_0
      //   423: getfield mDoubleTapListener : Landroid/view/GestureDetector$OnDoubleTapListener;
      //   426: aload_1
      //   427: invokeinterface onDoubleTapEvent : (Landroid/view/MotionEvent;)Z
      //   432: ior
      //   433: ireturn
      //   434: aload_0
      //   435: getfield mAlwaysInTapRegion : Z
      //   438: ifeq -> 571
      //   441: fload_3
      //   442: aload_0
      //   443: getfield mDownFocusX : F
      //   446: fsub
      //   447: f2i
      //   448: istore #6
      //   450: fload_2
      //   451: aload_0
      //   452: getfield mDownFocusY : F
      //   455: fsub
      //   456: f2i
      //   457: istore #7
      //   459: iload #6
      //   461: iload #6
      //   463: imul
      //   464: iload #7
      //   466: iload #7
      //   468: imul
      //   469: iadd
      //   470: istore #6
      //   472: iload #6
      //   474: aload_0
      //   475: getfield mTouchSlopSquare : I
      //   478: if_icmple -> 543
      //   481: aload_0
      //   482: getfield mListener : Landroid/view/GestureDetector$OnGestureListener;
      //   485: aload_0
      //   486: getfield mCurrentDownEvent : Landroid/view/MotionEvent;
      //   489: aload_1
      //   490: fload #4
      //   492: fload #5
      //   494: invokeinterface onScroll : (Landroid/view/MotionEvent;Landroid/view/MotionEvent;FF)Z
      //   499: istore #11
      //   501: aload_0
      //   502: fload_3
      //   503: putfield mLastFocusX : F
      //   506: aload_0
      //   507: fload_2
      //   508: putfield mLastFocusY : F
      //   511: aload_0
      //   512: iconst_0
      //   513: putfield mAlwaysInTapRegion : Z
      //   516: aload_0
      //   517: getfield mHandler : Landroid/os/Handler;
      //   520: iconst_3
      //   521: invokevirtual removeMessages : (I)V
      //   524: aload_0
      //   525: getfield mHandler : Landroid/os/Handler;
      //   528: iconst_1
      //   529: invokevirtual removeMessages : (I)V
      //   532: aload_0
      //   533: getfield mHandler : Landroid/os/Handler;
      //   536: iconst_2
      //   537: invokevirtual removeMessages : (I)V
      //   540: goto -> 546
      //   543: iconst_0
      //   544: istore #11
      //   546: iload #11
      //   548: istore #12
      //   550: iload #6
      //   552: aload_0
      //   553: getfield mTouchSlopSquare : I
      //   556: if_icmple -> 897
      //   559: aload_0
      //   560: iconst_0
      //   561: putfield mAlwaysInBiggerTapRegion : Z
      //   564: iload #11
      //   566: istore #12
      //   568: goto -> 897
      //   571: fload #4
      //   573: invokestatic abs : (F)F
      //   576: fconst_1
      //   577: fcmpl
      //   578: ifge -> 595
      //   581: iload #12
      //   583: istore #11
      //   585: fload #5
      //   587: invokestatic abs : (F)F
      //   590: fconst_1
      //   591: fcmpl
      //   592: iflt -> 1161
      //   595: aload_0
      //   596: getfield mListener : Landroid/view/GestureDetector$OnGestureListener;
      //   599: aload_0
      //   600: getfield mCurrentDownEvent : Landroid/view/MotionEvent;
      //   603: aload_1
      //   604: fload #4
      //   606: fload #5
      //   608: invokeinterface onScroll : (Landroid/view/MotionEvent;Landroid/view/MotionEvent;FF)Z
      //   613: istore #11
      //   615: aload_0
      //   616: fload_3
      //   617: putfield mLastFocusX : F
      //   620: aload_0
      //   621: fload_2
      //   622: putfield mLastFocusY : F
      //   625: iload #11
      //   627: ireturn
      //   628: aload_0
      //   629: iconst_0
      //   630: putfield mStillDown : Z
      //   633: aload_1
      //   634: invokestatic obtain : (Landroid/view/MotionEvent;)Landroid/view/MotionEvent;
      //   637: astore #13
      //   639: aload_0
      //   640: getfield mIsDoubleTapping : Z
      //   643: ifeq -> 663
      //   646: aload_0
      //   647: getfield mDoubleTapListener : Landroid/view/GestureDetector$OnDoubleTapListener;
      //   650: aload_1
      //   651: invokeinterface onDoubleTapEvent : (Landroid/view/MotionEvent;)Z
      //   656: iconst_0
      //   657: ior
      //   658: istore #11
      //   660: goto -> 828
      //   663: aload_0
      //   664: getfield mInLongPress : Z
      //   667: ifeq -> 686
      //   670: aload_0
      //   671: getfield mHandler : Landroid/os/Handler;
      //   674: iconst_3
      //   675: invokevirtual removeMessages : (I)V
      //   678: aload_0
      //   679: iconst_0
      //   680: putfield mInLongPress : Z
      //   683: goto -> 804
      //   686: aload_0
      //   687: getfield mAlwaysInTapRegion : Z
      //   690: ifeq -> 733
      //   693: aload_0
      //   694: getfield mListener : Landroid/view/GestureDetector$OnGestureListener;
      //   697: aload_1
      //   698: invokeinterface onSingleTapUp : (Landroid/view/MotionEvent;)Z
      //   703: istore #11
      //   705: aload_0
      //   706: getfield mDeferConfirmSingleTap : Z
      //   709: ifeq -> 730
      //   712: aload_0
      //   713: getfield mDoubleTapListener : Landroid/view/GestureDetector$OnDoubleTapListener;
      //   716: ifnull -> 730
      //   719: aload_0
      //   720: getfield mDoubleTapListener : Landroid/view/GestureDetector$OnDoubleTapListener;
      //   723: aload_1
      //   724: invokeinterface onSingleTapConfirmed : (Landroid/view/MotionEvent;)Z
      //   729: pop
      //   730: goto -> 828
      //   733: aload_0
      //   734: getfield mVelocityTracker : Landroid/view/VelocityTracker;
      //   737: astore #14
      //   739: aload_1
      //   740: iconst_0
      //   741: invokevirtual getPointerId : (I)I
      //   744: istore #6
      //   746: aload #14
      //   748: sipush #1000
      //   751: aload_0
      //   752: getfield mMaximumFlingVelocity : I
      //   755: i2f
      //   756: invokevirtual computeCurrentVelocity : (IF)V
      //   759: aload #14
      //   761: iload #6
      //   763: invokevirtual getYVelocity : (I)F
      //   766: fstore_2
      //   767: aload #14
      //   769: iload #6
      //   771: invokevirtual getXVelocity : (I)F
      //   774: fstore_3
      //   775: fload_2
      //   776: invokestatic abs : (F)F
      //   779: aload_0
      //   780: getfield mMinimumFlingVelocity : I
      //   783: i2f
      //   784: fcmpl
      //   785: ifgt -> 810
      //   788: fload_3
      //   789: invokestatic abs : (F)F
      //   792: aload_0
      //   793: getfield mMinimumFlingVelocity : I
      //   796: i2f
      //   797: fcmpl
      //   798: ifle -> 804
      //   801: goto -> 810
      //   804: iconst_0
      //   805: istore #11
      //   807: goto -> 828
      //   810: aload_0
      //   811: getfield mListener : Landroid/view/GestureDetector$OnGestureListener;
      //   814: aload_0
      //   815: getfield mCurrentDownEvent : Landroid/view/MotionEvent;
      //   818: aload_1
      //   819: fload_3
      //   820: fload_2
      //   821: invokeinterface onFling : (Landroid/view/MotionEvent;Landroid/view/MotionEvent;FF)Z
      //   826: istore #11
      //   828: aload_0
      //   829: getfield mPreviousUpEvent : Landroid/view/MotionEvent;
      //   832: ifnull -> 842
      //   835: aload_0
      //   836: getfield mPreviousUpEvent : Landroid/view/MotionEvent;
      //   839: invokevirtual recycle : ()V
      //   842: aload_0
      //   843: aload #13
      //   845: putfield mPreviousUpEvent : Landroid/view/MotionEvent;
      //   848: aload_0
      //   849: getfield mVelocityTracker : Landroid/view/VelocityTracker;
      //   852: ifnull -> 867
      //   855: aload_0
      //   856: getfield mVelocityTracker : Landroid/view/VelocityTracker;
      //   859: invokevirtual recycle : ()V
      //   862: aload_0
      //   863: aconst_null
      //   864: putfield mVelocityTracker : Landroid/view/VelocityTracker;
      //   867: aload_0
      //   868: iconst_0
      //   869: putfield mIsDoubleTapping : Z
      //   872: aload_0
      //   873: iconst_0
      //   874: putfield mDeferConfirmSingleTap : Z
      //   877: aload_0
      //   878: getfield mHandler : Landroid/os/Handler;
      //   881: iconst_1
      //   882: invokevirtual removeMessages : (I)V
      //   885: aload_0
      //   886: getfield mHandler : Landroid/os/Handler;
      //   889: iconst_2
      //   890: invokevirtual removeMessages : (I)V
      //   893: iload #11
      //   895: istore #12
      //   897: iload #12
      //   899: ireturn
      //   900: aload_0
      //   901: getfield mDoubleTapListener : Landroid/view/GestureDetector$OnDoubleTapListener;
      //   904: ifnull -> 1014
      //   907: aload_0
      //   908: getfield mHandler : Landroid/os/Handler;
      //   911: iconst_3
      //   912: invokevirtual hasMessages : (I)Z
      //   915: istore #11
      //   917: iload #11
      //   919: ifeq -> 930
      //   922: aload_0
      //   923: getfield mHandler : Landroid/os/Handler;
      //   926: iconst_3
      //   927: invokevirtual removeMessages : (I)V
      //   930: aload_0
      //   931: getfield mCurrentDownEvent : Landroid/view/MotionEvent;
      //   934: ifnull -> 1001
      //   937: aload_0
      //   938: getfield mPreviousUpEvent : Landroid/view/MotionEvent;
      //   941: ifnull -> 1001
      //   944: iload #11
      //   946: ifeq -> 1001
      //   949: aload_0
      //   950: aload_0
      //   951: getfield mCurrentDownEvent : Landroid/view/MotionEvent;
      //   954: aload_0
      //   955: getfield mPreviousUpEvent : Landroid/view/MotionEvent;
      //   958: aload_1
      //   959: invokespecial isConsideredDoubleTap : (Landroid/view/MotionEvent;Landroid/view/MotionEvent;Landroid/view/MotionEvent;)Z
      //   962: ifeq -> 1001
      //   965: aload_0
      //   966: iconst_1
      //   967: putfield mIsDoubleTapping : Z
      //   970: aload_0
      //   971: getfield mDoubleTapListener : Landroid/view/GestureDetector$OnDoubleTapListener;
      //   974: aload_0
      //   975: getfield mCurrentDownEvent : Landroid/view/MotionEvent;
      //   978: invokeinterface onDoubleTap : (Landroid/view/MotionEvent;)Z
      //   983: iconst_0
      //   984: ior
      //   985: aload_0
      //   986: getfield mDoubleTapListener : Landroid/view/GestureDetector$OnDoubleTapListener;
      //   989: aload_1
      //   990: invokeinterface onDoubleTapEvent : (Landroid/view/MotionEvent;)Z
      //   995: ior
      //   996: istore #6
      //   998: goto -> 1017
      //   1001: aload_0
      //   1002: getfield mHandler : Landroid/os/Handler;
      //   1005: iconst_3
      //   1006: getstatic android/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase.DOUBLE_TAP_TIMEOUT : I
      //   1009: i2l
      //   1010: invokevirtual sendEmptyMessageDelayed : (IJ)Z
      //   1013: pop
      //   1014: iconst_0
      //   1015: istore #6
      //   1017: aload_0
      //   1018: fload_3
      //   1019: putfield mLastFocusX : F
      //   1022: aload_0
      //   1023: fload_3
      //   1024: putfield mDownFocusX : F
      //   1027: aload_0
      //   1028: fload_2
      //   1029: putfield mLastFocusY : F
      //   1032: aload_0
      //   1033: fload_2
      //   1034: putfield mDownFocusY : F
      //   1037: aload_0
      //   1038: getfield mCurrentDownEvent : Landroid/view/MotionEvent;
      //   1041: ifnull -> 1051
      //   1044: aload_0
      //   1045: getfield mCurrentDownEvent : Landroid/view/MotionEvent;
      //   1048: invokevirtual recycle : ()V
      //   1051: aload_0
      //   1052: aload_1
      //   1053: invokestatic obtain : (Landroid/view/MotionEvent;)Landroid/view/MotionEvent;
      //   1056: putfield mCurrentDownEvent : Landroid/view/MotionEvent;
      //   1059: aload_0
      //   1060: iconst_1
      //   1061: putfield mAlwaysInTapRegion : Z
      //   1064: aload_0
      //   1065: iconst_1
      //   1066: putfield mAlwaysInBiggerTapRegion : Z
      //   1069: aload_0
      //   1070: iconst_1
      //   1071: putfield mStillDown : Z
      //   1074: aload_0
      //   1075: iconst_0
      //   1076: putfield mInLongPress : Z
      //   1079: aload_0
      //   1080: iconst_0
      //   1081: putfield mDeferConfirmSingleTap : Z
      //   1084: aload_0
      //   1085: getfield mIsLongpressEnabled : Z
      //   1088: ifeq -> 1125
      //   1091: aload_0
      //   1092: getfield mHandler : Landroid/os/Handler;
      //   1095: iconst_2
      //   1096: invokevirtual removeMessages : (I)V
      //   1099: aload_0
      //   1100: getfield mHandler : Landroid/os/Handler;
      //   1103: iconst_2
      //   1104: aload_0
      //   1105: getfield mCurrentDownEvent : Landroid/view/MotionEvent;
      //   1108: invokevirtual getDownTime : ()J
      //   1111: getstatic android/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase.TAP_TIMEOUT : I
      //   1114: i2l
      //   1115: ladd
      //   1116: getstatic android/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase.LONGPRESS_TIMEOUT : I
      //   1119: i2l
      //   1120: ladd
      //   1121: invokevirtual sendEmptyMessageAtTime : (IJ)Z
      //   1124: pop
      //   1125: aload_0
      //   1126: getfield mHandler : Landroid/os/Handler;
      //   1129: iconst_1
      //   1130: aload_0
      //   1131: getfield mCurrentDownEvent : Landroid/view/MotionEvent;
      //   1134: invokevirtual getDownTime : ()J
      //   1137: getstatic android/support/v4/view/GestureDetectorCompat$GestureDetectorCompatImplBase.TAP_TIMEOUT : I
      //   1140: i2l
      //   1141: ladd
      //   1142: invokevirtual sendEmptyMessageAtTime : (IJ)Z
      //   1145: pop
      //   1146: iload #6
      //   1148: aload_0
      //   1149: getfield mListener : Landroid/view/GestureDetector$OnGestureListener;
      //   1152: aload_1
      //   1153: invokeinterface onDown : (Landroid/view/MotionEvent;)Z
      //   1158: ior
      //   1159: istore #11
      //   1161: iload #11
      //   1163: ireturn }
    
    public void setIsLongpressEnabled(boolean param1Boolean) { this.mIsLongpressEnabled = param1Boolean; }
    
    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener param1OnDoubleTapListener) { this.mDoubleTapListener = param1OnDoubleTapListener; }
    
    private class GestureHandler extends Handler {
      GestureHandler() {}
      
      GestureHandler(Handler param2Handler) { super(param2Handler.getLooper()); }
      
      public void handleMessage(Message param2Message) {
        StringBuilder stringBuilder;
        switch (param2Message.what) {
          default:
            stringBuilder = new StringBuilder();
            stringBuilder.append("Unknown message ");
            stringBuilder.append(param2Message);
            throw new RuntimeException(stringBuilder.toString());
          case 3:
            if (GestureDetectorCompat.GestureDetectorCompatImplBase.this.mDoubleTapListener != null) {
              if (!GestureDetectorCompat.GestureDetectorCompatImplBase.this.mStillDown) {
                GestureDetectorCompat.GestureDetectorCompatImplBase.this.mDoubleTapListener.onSingleTapConfirmed(GestureDetectorCompat.GestureDetectorCompatImplBase.this.mCurrentDownEvent);
                return;
              } 
              GestureDetectorCompat.GestureDetectorCompatImplBase.this.mDeferConfirmSingleTap = true;
              return;
            } 
            return;
          case 2:
            GestureDetectorCompat.GestureDetectorCompatImplBase.this.dispatchLongPress();
            return;
          case 1:
            break;
        } 
        GestureDetectorCompat.GestureDetectorCompatImplBase.this.mListener.onShowPress(GestureDetectorCompat.GestureDetectorCompatImplBase.this.mCurrentDownEvent);
      }
    }
  }
  
  private class GestureHandler extends Handler {
    GestureHandler() {}
    
    GestureHandler(Handler param1Handler) { super(param1Handler.getLooper()); }
    
    public void handleMessage(Message param1Message) {
      StringBuilder stringBuilder;
      switch (param1Message.what) {
        default:
          stringBuilder = new StringBuilder();
          stringBuilder.append("Unknown message ");
          stringBuilder.append(param1Message);
          throw new RuntimeException(stringBuilder.toString());
        case 3:
          if (this.this$0.mDoubleTapListener != null) {
            if (!this.this$0.mStillDown) {
              this.this$0.mDoubleTapListener.onSingleTapConfirmed(this.this$0.mCurrentDownEvent);
              return;
            } 
            this.this$0.mDeferConfirmSingleTap = true;
            return;
          } 
          return;
        case 2:
          this.this$0.dispatchLongPress();
          return;
        case 1:
          break;
      } 
      this.this$0.mListener.onShowPress(this.this$0.mCurrentDownEvent);
    }
  }
  
  static class GestureDetectorCompatImplJellybeanMr2 implements GestureDetectorCompatImpl {
    private final GestureDetector mDetector;
    
    GestureDetectorCompatImplJellybeanMr2(Context param1Context, GestureDetector.OnGestureListener param1OnGestureListener, Handler param1Handler) { this.mDetector = new GestureDetector(param1Context, param1OnGestureListener, param1Handler); }
    
    public boolean isLongpressEnabled() { return this.mDetector.isLongpressEnabled(); }
    
    public boolean onTouchEvent(MotionEvent param1MotionEvent) { return this.mDetector.onTouchEvent(param1MotionEvent); }
    
    public void setIsLongpressEnabled(boolean param1Boolean) { this.mDetector.setIsLongpressEnabled(param1Boolean); }
    
    public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener param1OnDoubleTapListener) { this.mDetector.setOnDoubleTapListener(param1OnDoubleTapListener); }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/view/GestureDetectorCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */