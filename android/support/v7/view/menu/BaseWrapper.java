package android.support.v7.view.menu;

class BaseWrapper<T> extends Object {
  final T mWrappedObject;
  
  BaseWrapper(T paramT) {
    if (paramT == null)
      throw new IllegalArgumentException("Wrapped Object can not be null."); 
    this.mWrappedObject = paramT;
  }
  
  public T getWrappedObject() { return (T)this.mWrappedObject; }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/view/menu/BaseWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */