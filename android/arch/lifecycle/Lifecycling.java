package android.arch.lifecycle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class Lifecycling {
  private static final int GENERATED_CALLBACK = 2;
  
  private static final int REFLECTIVE_CALLBACK = 1;
  
  private static Map<Class, Integer> sCallbackCache = new HashMap();
  
  private static Map<Class, List<Constructor<? extends GeneratedAdapter>>> sClassToAdapters = new HashMap();
  
  private static GeneratedAdapter createGeneratedAdapter(Constructor<? extends GeneratedAdapter> paramConstructor, Object paramObject) {
    try {
      return (GeneratedAdapter)paramConstructor.newInstance(new Object[] { paramObject });
    } catch (IllegalAccessException paramConstructor) {
      throw new RuntimeException(paramConstructor);
    } catch (InstantiationException paramConstructor) {
      throw new RuntimeException(paramConstructor);
    } catch (InvocationTargetException paramConstructor) {
      throw new RuntimeException(paramConstructor);
    } 
  }
  
  @Nullable
  private static Constructor<? extends GeneratedAdapter> generatedConstructor(Class<?> paramClass) {
    try {
      String str1 = paramClass.getPackage();
      String str2 = paramClass.getCanonicalName();
      if (str1 != null) {
        String str = str1.getName();
      } else {
        str1 = "";
      } 
      if (!str1.isEmpty())
        str2 = str2.substring(str1.length() + 1); 
      str2 = getAdapterName(str2);
      if (str1.isEmpty()) {
        str1 = str2;
      } else {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(str1);
        stringBuilder.append(".");
        stringBuilder.append(str2);
        str1 = stringBuilder.toString();
      } 
      Constructor constructor = Class.forName(str1).getDeclaredConstructor(new Class[] { paramClass });
      if (!constructor.isAccessible())
        constructor.setAccessible(true); 
      return constructor;
    } catch (ClassNotFoundException paramClass) {
      return null;
    } catch (NoSuchMethodException paramClass) {
      throw new RuntimeException(paramClass);
    } 
  }
  
  public static String getAdapterName(String paramString) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(paramString.replace(".", "_"));
    stringBuilder.append("_LifecycleAdapter");
    return stringBuilder.toString();
  }
  
  @NonNull
  static GenericLifecycleObserver getCallback(Object paramObject) {
    if (paramObject instanceof FullLifecycleObserver)
      return new FullLifecycleObserverAdapter((FullLifecycleObserver)paramObject); 
    if (paramObject instanceof GenericLifecycleObserver)
      return (GenericLifecycleObserver)paramObject; 
    Class clazz = paramObject.getClass();
    if (getObserverConstructorType(clazz) == 2) {
      List list = (List)sClassToAdapters.get(clazz);
      int i = list.size();
      byte b = 0;
      if (i == 1)
        return new SingleGeneratedAdapterObserver(createGeneratedAdapter((Constructor)list.get(0), paramObject)); 
      GeneratedAdapter[] arrayOfGeneratedAdapter = new GeneratedAdapter[list.size()];
      while (b < list.size()) {
        arrayOfGeneratedAdapter[b] = createGeneratedAdapter((Constructor)list.get(b), paramObject);
        b++;
      } 
      return new CompositeGeneratedAdaptersObserver(arrayOfGeneratedAdapter);
    } 
    return new ReflectiveGenericLifecycleObserver(paramObject);
  }
  
  private static int getObserverConstructorType(Class<?> paramClass) {
    if (sCallbackCache.containsKey(paramClass))
      return ((Integer)sCallbackCache.get(paramClass)).intValue(); 
    int i = resolveObserverCallbackType(paramClass);
    sCallbackCache.put(paramClass, Integer.valueOf(i));
    return i;
  }
  
  private static boolean isLifecycleParent(Class<?> paramClass) { return (paramClass != null && LifecycleObserver.class.isAssignableFrom(paramClass)); }
  
  private static int resolveObserverCallbackType(Class<?> paramClass) {
    if (paramClass.getCanonicalName() == null)
      return 1; 
    ArrayList arrayList = generatedConstructor(paramClass);
    if (arrayList != null) {
      sClassToAdapters.put(paramClass, Collections.singletonList(arrayList));
      return 2;
    } 
    if (ClassesInfoCache.sInstance.hasLifecycleMethods(paramClass))
      return 1; 
    Class clazz = paramClass.getSuperclass();
    arrayList = null;
    if (isLifecycleParent(clazz)) {
      if (getObserverConstructorType(clazz) == 1)
        return 1; 
      arrayList = new ArrayList((Collection)sClassToAdapters.get(clazz));
    } 
    for (Class clazz1 : paramClass.getInterfaces()) {
      if (isLifecycleParent(clazz1)) {
        if (getObserverConstructorType(clazz1) == 1)
          return 1; 
        ArrayList arrayList1 = arrayList;
        if (arrayList == null)
          arrayList1 = new ArrayList(); 
        arrayList1.addAll((Collection)sClassToAdapters.get(clazz1));
        arrayList = arrayList1;
      } 
    } 
    if (arrayList != null) {
      sClassToAdapters.put(paramClass, arrayList);
      return 2;
    } 
    return 1;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/arch/lifecycle/Lifecycling.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */