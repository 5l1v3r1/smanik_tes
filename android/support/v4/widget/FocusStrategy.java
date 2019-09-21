package android.support.v4.widget;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

class FocusStrategy {
  private static boolean beamBeats(int paramInt, @NonNull Rect paramRect1, @NonNull Rect paramRect2, @NonNull Rect paramRect3) {
    boolean bool = beamsOverlap(paramInt, paramRect1, paramRect2);
    return !beamsOverlap(paramInt, paramRect1, paramRect3) ? (!bool ? false : (!isToDirectionOf(paramInt, paramRect1, paramRect3) ? true : ((paramInt != 17) ? ((paramInt == 66) ? true : ((majorAxisDistance(paramInt, paramRect1, paramRect2) < majorAxisDistanceToFarEdge(paramInt, paramRect1, paramRect3)))) : true))) : false;
  }
  
  private static boolean beamsOverlap(int paramInt, @NonNull Rect paramRect1, @NonNull Rect paramRect2) { // Byte code:
    //   0: iconst_0
    //   1: istore #5
    //   3: iconst_0
    //   4: istore #4
    //   6: iload_0
    //   7: bipush #17
    //   9: if_icmpeq -> 73
    //   12: iload_0
    //   13: bipush #33
    //   15: if_icmpeq -> 41
    //   18: iload_0
    //   19: bipush #66
    //   21: if_icmpeq -> 73
    //   24: iload_0
    //   25: sipush #130
    //   28: if_icmpeq -> 41
    //   31: new java/lang/IllegalArgumentException
    //   34: dup
    //   35: ldc 'direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.'
    //   37: invokespecial <init> : (Ljava/lang/String;)V
    //   40: athrow
    //   41: iload #4
    //   43: istore_3
    //   44: aload_2
    //   45: getfield right : I
    //   48: aload_1
    //   49: getfield left : I
    //   52: if_icmplt -> 71
    //   55: iload #4
    //   57: istore_3
    //   58: aload_2
    //   59: getfield left : I
    //   62: aload_1
    //   63: getfield right : I
    //   66: if_icmpgt -> 71
    //   69: iconst_1
    //   70: istore_3
    //   71: iload_3
    //   72: ireturn
    //   73: iload #5
    //   75: istore_3
    //   76: aload_2
    //   77: getfield bottom : I
    //   80: aload_1
    //   81: getfield top : I
    //   84: if_icmplt -> 103
    //   87: iload #5
    //   89: istore_3
    //   90: aload_2
    //   91: getfield top : I
    //   94: aload_1
    //   95: getfield bottom : I
    //   98: if_icmpgt -> 103
    //   101: iconst_1
    //   102: istore_3
    //   103: iload_3
    //   104: ireturn }
  
  public static <L, T> T findNextFocusInAbsoluteDirection(@NonNull L paramL, @NonNull CollectionAdapter<L, T> paramCollectionAdapter, @NonNull BoundsAdapter<T> paramBoundsAdapter, @Nullable T paramT, @NonNull Rect paramRect, int paramInt) {
    Rect rect1 = new Rect(paramRect);
    byte b = 0;
    if (paramInt != 17) {
      if (paramInt != 33) {
        if (paramInt != 66) {
          if (paramInt != 130)
            throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}."); 
          rect1.offset(0, -(paramRect.height() + 1));
        } else {
          rect1.offset(-(paramRect.width() + 1), 0);
        } 
      } else {
        rect1.offset(0, paramRect.height() + 1);
      } 
    } else {
      rect1.offset(paramRect.width() + 1, 0);
    } 
    Object object = null;
    int i = paramCollectionAdapter.size(paramL);
    Rect rect2 = new Rect();
    while (b < i) {
      Object object1 = paramCollectionAdapter.get(paramL, b);
      if (object1 != paramT) {
        paramBoundsAdapter.obtainBounds(object1, rect2);
        if (isBetterCandidate(paramInt, paramRect, rect2, rect1)) {
          rect1.set(rect2);
          object = object1;
        } 
      } 
      b++;
    } 
    return (T)object;
  }
  
  public static <L, T> T findNextFocusInRelativeDirection(@NonNull L paramL, @NonNull CollectionAdapter<L, T> paramCollectionAdapter, @NonNull BoundsAdapter<T> paramBoundsAdapter, @Nullable T paramT, int paramInt, boolean paramBoolean1, boolean paramBoolean2) {
    int i = paramCollectionAdapter.size(paramL);
    ArrayList arrayList = new ArrayList(i);
    byte b;
    for (b = 0; b < i; b++)
      arrayList.add(paramCollectionAdapter.get(paramL, b)); 
    Collections.sort(arrayList, new SequentialComparator(paramBoolean1, paramBoundsAdapter));
    switch (paramInt) {
      default:
        throw new IllegalArgumentException("direction must be one of {FOCUS_FORWARD, FOCUS_BACKWARD}.");
      case 2:
        return (T)getNextFocusable(paramT, arrayList, paramBoolean2);
      case 1:
        break;
    } 
    return (T)getPreviousFocusable(paramT, arrayList, paramBoolean2);
  }
  
  private static <T> T getNextFocusable(T paramT, ArrayList<T> paramArrayList, boolean paramBoolean) {
    int i;
    int j = paramArrayList.size();
    if (paramT == null) {
      i = -1;
    } else {
      i = paramArrayList.lastIndexOf(paramT);
    } 
    return (++i < j) ? (T)paramArrayList.get(i) : ((paramBoolean && j > 0) ? (T)paramArrayList.get(0) : null);
  }
  
  private static <T> T getPreviousFocusable(T paramT, ArrayList<T> paramArrayList, boolean paramBoolean) {
    int i;
    int j = paramArrayList.size();
    if (paramT == null) {
      i = j;
    } else {
      i = paramArrayList.indexOf(paramT);
    } 
    return (--i >= 0) ? (T)paramArrayList.get(i) : ((paramBoolean && j > 0) ? (T)paramArrayList.get(j - 1) : null);
  }
  
  private static int getWeightedDistanceFor(int paramInt1, int paramInt2) { return paramInt1 * 13 * paramInt1 + paramInt2 * paramInt2; }
  
  private static boolean isBetterCandidate(int paramInt, @NonNull Rect paramRect1, @NonNull Rect paramRect2, @NonNull Rect paramRect3) {
    boolean bool2 = isCandidate(paramRect1, paramRect2, paramInt);
    boolean bool1 = false;
    if (!bool2)
      return false; 
    if (!isCandidate(paramRect1, paramRect3, paramInt))
      return true; 
    if (beamBeats(paramInt, paramRect1, paramRect2, paramRect3))
      return true; 
    if (beamBeats(paramInt, paramRect1, paramRect3, paramRect2))
      return false; 
    if (getWeightedDistanceFor(majorAxisDistance(paramInt, paramRect1, paramRect2), minorAxisDistance(paramInt, paramRect1, paramRect2)) < getWeightedDistanceFor(majorAxisDistance(paramInt, paramRect1, paramRect3), minorAxisDistance(paramInt, paramRect1, paramRect3)))
      bool1 = true; 
    return bool1;
  }
  
  private static boolean isCandidate(@NonNull Rect paramRect1, @NonNull Rect paramRect2, int paramInt) { // Byte code:
    //   0: iconst_0
    //   1: istore #5
    //   3: iconst_0
    //   4: istore #6
    //   6: iconst_0
    //   7: istore #7
    //   9: iconst_0
    //   10: istore #4
    //   12: iload_2
    //   13: bipush #17
    //   15: if_icmpeq -> 176
    //   18: iload_2
    //   19: bipush #33
    //   21: if_icmpeq -> 133
    //   24: iload_2
    //   25: bipush #66
    //   27: if_icmpeq -> 90
    //   30: iload_2
    //   31: sipush #130
    //   34: if_icmpeq -> 47
    //   37: new java/lang/IllegalArgumentException
    //   40: dup
    //   41: ldc 'direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.'
    //   43: invokespecial <init> : (Ljava/lang/String;)V
    //   46: athrow
    //   47: aload_0
    //   48: getfield top : I
    //   51: aload_1
    //   52: getfield top : I
    //   55: if_icmplt -> 72
    //   58: iload #4
    //   60: istore_3
    //   61: aload_0
    //   62: getfield bottom : I
    //   65: aload_1
    //   66: getfield top : I
    //   69: if_icmpgt -> 88
    //   72: iload #4
    //   74: istore_3
    //   75: aload_0
    //   76: getfield bottom : I
    //   79: aload_1
    //   80: getfield bottom : I
    //   83: if_icmpge -> 88
    //   86: iconst_1
    //   87: istore_3
    //   88: iload_3
    //   89: ireturn
    //   90: aload_0
    //   91: getfield left : I
    //   94: aload_1
    //   95: getfield left : I
    //   98: if_icmplt -> 115
    //   101: iload #5
    //   103: istore_3
    //   104: aload_0
    //   105: getfield right : I
    //   108: aload_1
    //   109: getfield left : I
    //   112: if_icmpgt -> 131
    //   115: iload #5
    //   117: istore_3
    //   118: aload_0
    //   119: getfield right : I
    //   122: aload_1
    //   123: getfield right : I
    //   126: if_icmpge -> 131
    //   129: iconst_1
    //   130: istore_3
    //   131: iload_3
    //   132: ireturn
    //   133: aload_0
    //   134: getfield bottom : I
    //   137: aload_1
    //   138: getfield bottom : I
    //   141: if_icmpgt -> 158
    //   144: iload #6
    //   146: istore_3
    //   147: aload_0
    //   148: getfield top : I
    //   151: aload_1
    //   152: getfield bottom : I
    //   155: if_icmplt -> 174
    //   158: iload #6
    //   160: istore_3
    //   161: aload_0
    //   162: getfield top : I
    //   165: aload_1
    //   166: getfield top : I
    //   169: if_icmple -> 174
    //   172: iconst_1
    //   173: istore_3
    //   174: iload_3
    //   175: ireturn
    //   176: aload_0
    //   177: getfield right : I
    //   180: aload_1
    //   181: getfield right : I
    //   184: if_icmpgt -> 201
    //   187: iload #7
    //   189: istore_3
    //   190: aload_0
    //   191: getfield left : I
    //   194: aload_1
    //   195: getfield right : I
    //   198: if_icmplt -> 217
    //   201: iload #7
    //   203: istore_3
    //   204: aload_0
    //   205: getfield left : I
    //   208: aload_1
    //   209: getfield left : I
    //   212: if_icmple -> 217
    //   215: iconst_1
    //   216: istore_3
    //   217: iload_3
    //   218: ireturn }
  
  private static boolean isToDirectionOf(int paramInt, @NonNull Rect paramRect1, @NonNull Rect paramRect2) {
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    boolean bool = false;
    if (paramInt != 17) {
      if (paramInt != 33) {
        if (paramInt != 66) {
          if (paramInt != 130)
            throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}."); 
          if (paramRect1.bottom <= paramRect2.top)
            bool = true; 
          return bool;
        } 
        bool = bool1;
        if (paramRect1.right <= paramRect2.left)
          bool = true; 
        return bool;
      } 
      bool = bool2;
      if (paramRect1.top >= paramRect2.bottom)
        bool = true; 
      return bool;
    } 
    bool = bool3;
    if (paramRect1.left >= paramRect2.right)
      bool = true; 
    return bool;
  }
  
  private static int majorAxisDistance(int paramInt, @NonNull Rect paramRect1, @NonNull Rect paramRect2) { return Math.max(0, majorAxisDistanceRaw(paramInt, paramRect1, paramRect2)); }
  
  private static int majorAxisDistanceRaw(int paramInt, @NonNull Rect paramRect1, @NonNull Rect paramRect2) {
    if (paramInt != 17) {
      if (paramInt != 33) {
        if (paramInt != 66) {
          if (paramInt != 130)
            throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}."); 
          return paramRect2.top - paramRect1.bottom;
        } 
        return paramRect2.left - paramRect1.right;
      } 
      return paramRect1.top - paramRect2.bottom;
    } 
    return paramRect1.left - paramRect2.right;
  }
  
  private static int majorAxisDistanceToFarEdge(int paramInt, @NonNull Rect paramRect1, @NonNull Rect paramRect2) { return Math.max(1, majorAxisDistanceToFarEdgeRaw(paramInt, paramRect1, paramRect2)); }
  
  private static int majorAxisDistanceToFarEdgeRaw(int paramInt, @NonNull Rect paramRect1, @NonNull Rect paramRect2) {
    if (paramInt != 17) {
      if (paramInt != 33) {
        if (paramInt != 66) {
          if (paramInt != 130)
            throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}."); 
          return paramRect2.bottom - paramRect1.bottom;
        } 
        return paramRect2.right - paramRect1.right;
      } 
      return paramRect1.top - paramRect2.top;
    } 
    return paramRect1.left - paramRect2.left;
  }
  
  private static int minorAxisDistance(int paramInt, @NonNull Rect paramRect1, @NonNull Rect paramRect2) {
    if (paramInt != 17)
      if (paramInt != 33) {
        if (paramInt != 66) {
          if (paramInt != 130)
            throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}."); 
          return Math.abs(paramRect1.left + paramRect1.width() / 2 - paramRect2.left + paramRect2.width() / 2);
        } 
      } else {
        return Math.abs(paramRect1.left + paramRect1.width() / 2 - paramRect2.left + paramRect2.width() / 2);
      }  
    return Math.abs(paramRect1.top + paramRect1.height() / 2 - paramRect2.top + paramRect2.height() / 2);
  }
  
  public static interface BoundsAdapter<T> {
    void obtainBounds(T param1T, Rect param1Rect);
  }
  
  public static interface CollectionAdapter<T, V> {
    V get(T param1T, int param1Int);
    
    int size(T param1T);
  }
  
  private static class SequentialComparator<T> extends Object implements Comparator<T> {
    private final FocusStrategy.BoundsAdapter<T> mAdapter;
    
    private final boolean mIsLayoutRtl;
    
    private final Rect mTemp1 = new Rect();
    
    private final Rect mTemp2 = new Rect();
    
    SequentialComparator(boolean param1Boolean, FocusStrategy.BoundsAdapter<T> param1BoundsAdapter) {
      this.mIsLayoutRtl = param1Boolean;
      this.mAdapter = param1BoundsAdapter;
    }
    
    public int compare(T param1T1, T param1T2) {
      Rect rect1 = this.mTemp1;
      Rect rect2 = this.mTemp2;
      this.mAdapter.obtainBounds(param1T1, rect1);
      this.mAdapter.obtainBounds(param1T2, rect2);
      int i = rect1.top;
      int j = rect2.top;
      byte b = -1;
      if (i < j)
        return -1; 
      if (rect1.top > rect2.top)
        return 1; 
      if (rect1.left < rect2.left) {
        if (this.mIsLayoutRtl)
          b = 1; 
        return b;
      } 
      if (rect1.left > rect2.left)
        return this.mIsLayoutRtl ? -1 : 1; 
      if (rect1.bottom < rect2.bottom)
        return -1; 
      if (rect1.bottom > rect2.bottom)
        return 1; 
      if (rect1.right < rect2.right) {
        if (this.mIsLayoutRtl)
          b = 1; 
        return b;
      } 
      return (rect1.right > rect2.right) ? (this.mIsLayoutRtl ? -1 : 1) : 0;
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/widget/FocusStrategy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */