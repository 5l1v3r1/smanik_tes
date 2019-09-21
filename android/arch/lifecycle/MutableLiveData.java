package android.arch.lifecycle;

public class MutableLiveData<T> extends LiveData<T> {
  public void postValue(T paramT) { super.postValue(paramT); }
  
  public void setValue(T paramT) { super.setValue(paramT); }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/arch/lifecycle/MutableLiveData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */