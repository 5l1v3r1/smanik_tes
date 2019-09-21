package android.support.v4.os;

import android.os.Parcel;
import android.os.Parcelable;

@Deprecated
public final class ParcelableCompat {
  @Deprecated
  public static <T> Parcelable.Creator<T> newCreator(ParcelableCompatCreatorCallbacks<T> paramParcelableCompatCreatorCallbacks) { return new ParcelableCompatCreatorHoneycombMR2(paramParcelableCompatCreatorCallbacks); }
  
  static class ParcelableCompatCreatorHoneycombMR2<T> extends Object implements Parcelable.ClassLoaderCreator<T> {
    private final ParcelableCompatCreatorCallbacks<T> mCallbacks;
    
    ParcelableCompatCreatorHoneycombMR2(ParcelableCompatCreatorCallbacks<T> param1ParcelableCompatCreatorCallbacks) { this.mCallbacks = param1ParcelableCompatCreatorCallbacks; }
    
    public T createFromParcel(Parcel param1Parcel) { return (T)this.mCallbacks.createFromParcel(param1Parcel, null); }
    
    public T createFromParcel(Parcel param1Parcel, ClassLoader param1ClassLoader) { return (T)this.mCallbacks.createFromParcel(param1Parcel, param1ClassLoader); }
    
    public T[] newArray(int param1Int) { return (T[])this.mCallbacks.newArray(param1Int); }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/os/ParcelableCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */