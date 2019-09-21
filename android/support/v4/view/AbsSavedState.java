package android.support.v4.view;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public abstract class AbsSavedState implements Parcelable {
  public static final Parcelable.Creator<AbsSavedState> CREATOR;
  
  public static final AbsSavedState EMPTY_STATE = new AbsSavedState() {
    
    };
  
  private final Parcelable mSuperState;
  
  static  {
    CREATOR = new Parcelable.ClassLoaderCreator<AbsSavedState>() {
        public AbsSavedState createFromParcel(Parcel param1Parcel) { return createFromParcel(param1Parcel, null); }
        
        public AbsSavedState createFromParcel(Parcel param1Parcel, ClassLoader param1ClassLoader) {
          if (param1Parcel.readParcelable(param1ClassLoader) != null)
            throw new IllegalStateException("superState must be null"); 
          return AbsSavedState.EMPTY_STATE;
        }
        
        public AbsSavedState[] newArray(int param1Int) { return new AbsSavedState[param1Int]; }
      };
  }
  
  private AbsSavedState() { this.mSuperState = null; }
  
  protected AbsSavedState(@NonNull Parcel paramParcel) { this(paramParcel, null); }
  
  protected AbsSavedState(@NonNull Parcel paramParcel, @Nullable ClassLoader paramClassLoader) {
    Parcelable parcelable = paramParcel.readParcelable(paramClassLoader);
    if (parcelable == null)
      parcelable = EMPTY_STATE; 
    this.mSuperState = parcelable;
  }
  
  protected AbsSavedState(@NonNull Parcelable paramParcelable) {
    if (paramParcelable == null)
      throw new IllegalArgumentException("superState must not be null"); 
    if (paramParcelable == EMPTY_STATE)
      paramParcelable = null; 
    this.mSuperState = paramParcelable;
  }
  
  public int describeContents() { return 0; }
  
  @Nullable
  public final Parcelable getSuperState() { return this.mSuperState; }
  
  public void writeToParcel(Parcel paramParcel, int paramInt) { paramParcel.writeParcelable(this.mSuperState, paramInt); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/view/AbsSavedState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */