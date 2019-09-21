package android.support.v7.app;

import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.LongSparseArray;
import java.lang.reflect.Field;
import java.util.Map;

class ResourcesFlusher {
  private static final String TAG = "ResourcesFlusher";
  
  private static Field sDrawableCacheField;
  
  private static boolean sDrawableCacheFieldFetched;
  
  private static Field sResourcesImplField;
  
  private static boolean sResourcesImplFieldFetched;
  
  private static Class sThemedResourceCacheClazz;
  
  private static boolean sThemedResourceCacheClazzFetched;
  
  private static Field sThemedResourceCache_mUnthemedEntriesField;
  
  private static boolean sThemedResourceCache_mUnthemedEntriesFieldFetched;
  
  static boolean flush(@NonNull Resources paramResources) { return (Build.VERSION.SDK_INT >= 24) ? flushNougats(paramResources) : ((Build.VERSION.SDK_INT >= 23) ? flushMarshmallows(paramResources) : ((Build.VERSION.SDK_INT >= 21) ? flushLollipops(paramResources) : 0)); }
  
  @RequiresApi(21)
  private static boolean flushLollipops(@NonNull Resources paramResources) {
    if (!sDrawableCacheFieldFetched) {
      try {
        sDrawableCacheField = Resources.class.getDeclaredField("mDrawableCache");
        sDrawableCacheField.setAccessible(true);
      } catch (NoSuchFieldException noSuchFieldException) {
        Log.e("ResourcesFlusher", "Could not retrieve Resources#mDrawableCache field", noSuchFieldException);
      } 
      sDrawableCacheFieldFetched = true;
    } 
    if (sDrawableCacheField != null) {
      try {
        Map map = (Map)sDrawableCacheField.get(paramResources);
      } catch (IllegalAccessException paramResources) {
        Log.e("ResourcesFlusher", "Could not retrieve value from Resources#mDrawableCache", paramResources);
        paramResources = null;
      } 
      if (paramResources != null) {
        paramResources.clear();
        return true;
      } 
    } 
    return false;
  }
  
  @RequiresApi(23)
  private static boolean flushMarshmallows(@NonNull Resources paramResources) {
    if (!sDrawableCacheFieldFetched) {
      try {
        sDrawableCacheField = Resources.class.getDeclaredField("mDrawableCache");
        sDrawableCacheField.setAccessible(true);
      } catch (NoSuchFieldException noSuchFieldException) {
        Log.e("ResourcesFlusher", "Could not retrieve Resources#mDrawableCache field", noSuchFieldException);
      } 
      sDrawableCacheFieldFetched = true;
    } 
    if (sDrawableCacheField != null) {
      try {
        Object object = sDrawableCacheField.get(paramResources);
      } catch (IllegalAccessException paramResources) {
        Log.e("ResourcesFlusher", "Could not retrieve value from Resources#mDrawableCache", paramResources);
        paramResources = null;
      } 
      byte b = 0;
      if (paramResources == null)
        return false; 
      int i = b;
      if (paramResources != null) {
        i = b;
        if (flushThemedResourcesCache(paramResources))
          i = 1; 
      } 
      return i;
    } 
    paramResources = null;
  }
  
  @RequiresApi(24)
  private static boolean flushNougats(@NonNull Resources paramResources) {
    if (!sResourcesImplFieldFetched) {
      try {
        sResourcesImplField = Resources.class.getDeclaredField("mResourcesImpl");
        sResourcesImplField.setAccessible(true);
      } catch (NoSuchFieldException noSuchFieldException) {
        Log.e("ResourcesFlusher", "Could not retrieve Resources#mResourcesImpl field", noSuchFieldException);
      } 
      sResourcesImplFieldFetched = true;
    } 
    if (sResourcesImplField == null)
      return false; 
    try {
      Object object = sResourcesImplField.get(paramResources);
    } catch (IllegalAccessException paramResources) {
      Log.e("ResourcesFlusher", "Could not retrieve value from Resources#mResourcesImpl", paramResources);
      paramResources = null;
    } 
    if (paramResources == null)
      return false; 
    if (!sDrawableCacheFieldFetched) {
      try {
        sDrawableCacheField = paramResources.getClass().getDeclaredField("mDrawableCache");
        sDrawableCacheField.setAccessible(true);
      } catch (NoSuchFieldException noSuchFieldException) {
        Log.e("ResourcesFlusher", "Could not retrieve ResourcesImpl#mDrawableCache field", noSuchFieldException);
      } 
      sDrawableCacheFieldFetched = true;
    } 
    if (sDrawableCacheField != null) {
      try {
        Object object = sDrawableCacheField.get(paramResources);
      } catch (IllegalAccessException paramResources) {
        Log.e("ResourcesFlusher", "Could not retrieve value from ResourcesImpl#mDrawableCache", paramResources);
        paramResources = null;
      } 
      return (paramResources != null && flushThemedResourcesCache(paramResources));
    } 
    paramResources = null;
  }
  
  @RequiresApi(16)
  private static boolean flushThemedResourcesCache(@NonNull Object paramObject) {
    if (!sThemedResourceCacheClazzFetched) {
      try {
        sThemedResourceCacheClazz = Class.forName("android.content.res.ThemedResourceCache");
      } catch (ClassNotFoundException classNotFoundException) {
        Log.e("ResourcesFlusher", "Could not find ThemedResourceCache class", classNotFoundException);
      } 
      sThemedResourceCacheClazzFetched = true;
    } 
    if (sThemedResourceCacheClazz == null)
      return false; 
    if (!sThemedResourceCache_mUnthemedEntriesFieldFetched) {
      try {
        sThemedResourceCache_mUnthemedEntriesField = sThemedResourceCacheClazz.getDeclaredField("mUnthemedEntries");
        sThemedResourceCache_mUnthemedEntriesField.setAccessible(true);
      } catch (NoSuchFieldException noSuchFieldException) {
        Log.e("ResourcesFlusher", "Could not retrieve ThemedResourceCache#mUnthemedEntries field", noSuchFieldException);
      } 
      sThemedResourceCache_mUnthemedEntriesFieldFetched = true;
    } 
    if (sThemedResourceCache_mUnthemedEntriesField == null)
      return false; 
    try {
      paramObject = (LongSparseArray)sThemedResourceCache_mUnthemedEntriesField.get(paramObject);
    } catch (IllegalAccessException paramObject) {
      Log.e("ResourcesFlusher", "Could not retrieve value from ThemedResourceCache#mUnthemedEntries", paramObject);
      paramObject = null;
    } 
    if (paramObject != null) {
      paramObject.clear();
      return true;
    } 
    return false;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/app/ResourcesFlusher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */