package android.support.v7.widget;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.DataSetObservable;
import android.os.AsyncTask;
import android.text.TextUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ActivityChooserModel extends DataSetObservable {
  static final String ATTRIBUTE_ACTIVITY = "activity";
  
  static final String ATTRIBUTE_TIME = "time";
  
  static final String ATTRIBUTE_WEIGHT = "weight";
  
  static final boolean DEBUG = false;
  
  private static final int DEFAULT_ACTIVITY_INFLATION = 5;
  
  private static final float DEFAULT_HISTORICAL_RECORD_WEIGHT = 1.0F;
  
  public static final String DEFAULT_HISTORY_FILE_NAME = "activity_choser_model_history.xml";
  
  public static final int DEFAULT_HISTORY_MAX_LENGTH = 50;
  
  private static final String HISTORY_FILE_EXTENSION = ".xml";
  
  private static final int INVALID_INDEX = -1;
  
  static final String LOG_TAG = "ActivityChooserModel";
  
  static final String TAG_HISTORICAL_RECORD = "historical-record";
  
  static final String TAG_HISTORICAL_RECORDS = "historical-records";
  
  private static final Map<String, ActivityChooserModel> sDataModelRegistry;
  
  private static final Object sRegistryLock = new Object();
  
  private final List<ActivityResolveInfo> mActivities = new ArrayList();
  
  private OnChooseActivityListener mActivityChoserModelPolicy;
  
  private ActivitySorter mActivitySorter = new DefaultSorter();
  
  boolean mCanReadHistoricalData = true;
  
  final Context mContext;
  
  private final List<HistoricalRecord> mHistoricalRecords = new ArrayList();
  
  private boolean mHistoricalRecordsChanged = true;
  
  final String mHistoryFileName;
  
  private int mHistoryMaxSize = 50;
  
  private final Object mInstanceLock = new Object();
  
  private Intent mIntent;
  
  private boolean mReadShareHistoryCalled = false;
  
  private boolean mReloadActivities = false;
  
  static  {
    sDataModelRegistry = new HashMap();
  }
  
  private ActivityChooserModel(Context paramContext, String paramString) {
    this.mContext = paramContext.getApplicationContext();
    if (!TextUtils.isEmpty(paramString) && !paramString.endsWith(".xml")) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(paramString);
      stringBuilder.append(".xml");
      this.mHistoryFileName = stringBuilder.toString();
      return;
    } 
    this.mHistoryFileName = paramString;
  }
  
  private boolean addHistoricalRecord(HistoricalRecord paramHistoricalRecord) {
    boolean bool = this.mHistoricalRecords.add(paramHistoricalRecord);
    if (bool) {
      this.mHistoricalRecordsChanged = true;
      pruneExcessiveHistoricalRecordsIfNeeded();
      persistHistoricalDataIfNeeded();
      sortActivitiesIfNeeded();
      notifyChanged();
    } 
    return bool;
  }
  
  private void ensureConsistentState() {
    boolean bool1 = loadActivitiesIfNeeded();
    boolean bool2 = readHistoricalDataIfNeeded();
    pruneExcessiveHistoricalRecordsIfNeeded();
    if (bool1 | bool2) {
      sortActivitiesIfNeeded();
      notifyChanged();
    } 
  }
  
  public static ActivityChooserModel get(Context paramContext, String paramString) {
    synchronized (sRegistryLock) {
      ActivityChooserModel activityChooserModel2 = (ActivityChooserModel)sDataModelRegistry.get(paramString);
      ActivityChooserModel activityChooserModel1 = activityChooserModel2;
      if (activityChooserModel2 == null) {
        activityChooserModel1 = new ActivityChooserModel(paramContext, paramString);
        sDataModelRegistry.put(paramString, activityChooserModel1);
      } 
      return activityChooserModel1;
    } 
  }
  
  private boolean loadActivitiesIfNeeded() {
    boolean bool = this.mReloadActivities;
    byte b = 0;
    if (bool && this.mIntent != null) {
      this.mReloadActivities = false;
      this.mActivities.clear();
      List list = this.mContext.getPackageManager().queryIntentActivities(this.mIntent, 0);
      int i = list.size();
      while (b < i) {
        ResolveInfo resolveInfo = (ResolveInfo)list.get(b);
        this.mActivities.add(new ActivityResolveInfo(resolveInfo));
        b++;
      } 
      return true;
    } 
    return false;
  }
  
  private void persistHistoricalDataIfNeeded() {
    if (!this.mReadShareHistoryCalled)
      throw new IllegalStateException("No preceding call to #readHistoricalData"); 
    if (!this.mHistoricalRecordsChanged)
      return; 
    this.mHistoricalRecordsChanged = false;
    if (!TextUtils.isEmpty(this.mHistoryFileName))
      (new PersistHistoryAsyncTask()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Object[] { new ArrayList(this.mHistoricalRecords), this.mHistoryFileName }); 
  }
  
  private void pruneExcessiveHistoricalRecordsIfNeeded() {
    int i = this.mHistoricalRecords.size() - this.mHistoryMaxSize;
    if (i <= 0)
      return; 
    this.mHistoricalRecordsChanged = true;
    for (byte b = 0; b < i; b++)
      HistoricalRecord historicalRecord = (HistoricalRecord)this.mHistoricalRecords.remove(0); 
  }
  
  private boolean readHistoricalDataIfNeeded() {
    if (this.mCanReadHistoricalData && this.mHistoricalRecordsChanged && !TextUtils.isEmpty(this.mHistoryFileName)) {
      this.mCanReadHistoricalData = false;
      this.mReadShareHistoryCalled = true;
      readHistoricalDataImpl();
      return true;
    } 
    return false;
  }
  
  private void readHistoricalDataImpl() { // Byte code:
    //   0: aload_0
    //   1: getfield mContext : Landroid/content/Context;
    //   4: aload_0
    //   5: getfield mHistoryFileName : Ljava/lang/String;
    //   8: invokevirtual openFileInput : (Ljava/lang/String;)Ljava/io/FileInputStream;
    //   11: astore_2
    //   12: invokestatic newPullParser : ()Lorg/xmlpull/v1/XmlPullParser;
    //   15: astore_3
    //   16: aload_3
    //   17: aload_2
    //   18: ldc_w 'UTF-8'
    //   21: invokeinterface setInput : (Ljava/io/InputStream;Ljava/lang/String;)V
    //   26: iconst_0
    //   27: istore_1
    //   28: iload_1
    //   29: iconst_1
    //   30: if_icmpeq -> 48
    //   33: iload_1
    //   34: iconst_2
    //   35: if_icmpeq -> 48
    //   38: aload_3
    //   39: invokeinterface next : ()I
    //   44: istore_1
    //   45: goto -> 28
    //   48: ldc 'historical-records'
    //   50: aload_3
    //   51: invokeinterface getName : ()Ljava/lang/String;
    //   56: invokevirtual equals : (Ljava/lang/Object;)Z
    //   59: ifne -> 73
    //   62: new org/xmlpull/v1/XmlPullParserException
    //   65: dup
    //   66: ldc_w 'Share records file does not start with historical-records tag.'
    //   69: invokespecial <init> : (Ljava/lang/String;)V
    //   72: athrow
    //   73: aload_0
    //   74: getfield mHistoricalRecords : Ljava/util/List;
    //   77: astore #4
    //   79: aload #4
    //   81: invokeinterface clear : ()V
    //   86: aload_3
    //   87: invokeinterface next : ()I
    //   92: istore_1
    //   93: iload_1
    //   94: iconst_1
    //   95: if_icmpne -> 107
    //   98: aload_2
    //   99: ifnull -> 306
    //   102: aload_2
    //   103: invokevirtual close : ()V
    //   106: return
    //   107: iload_1
    //   108: iconst_3
    //   109: if_icmpeq -> 86
    //   112: iload_1
    //   113: iconst_4
    //   114: if_icmpne -> 120
    //   117: goto -> 86
    //   120: ldc 'historical-record'
    //   122: aload_3
    //   123: invokeinterface getName : ()Ljava/lang/String;
    //   128: invokevirtual equals : (Ljava/lang/Object;)Z
    //   131: ifne -> 145
    //   134: new org/xmlpull/v1/XmlPullParserException
    //   137: dup
    //   138: ldc_w 'Share records file not well-formed.'
    //   141: invokespecial <init> : (Ljava/lang/String;)V
    //   144: athrow
    //   145: aload #4
    //   147: new android/support/v7/widget/ActivityChooserModel$HistoricalRecord
    //   150: dup
    //   151: aload_3
    //   152: aconst_null
    //   153: ldc 'activity'
    //   155: invokeinterface getAttributeValue : (Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   160: aload_3
    //   161: aconst_null
    //   162: ldc 'time'
    //   164: invokeinterface getAttributeValue : (Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   169: invokestatic parseLong : (Ljava/lang/String;)J
    //   172: aload_3
    //   173: aconst_null
    //   174: ldc 'weight'
    //   176: invokeinterface getAttributeValue : (Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   181: invokestatic parseFloat : (Ljava/lang/String;)F
    //   184: invokespecial <init> : (Ljava/lang/String;JF)V
    //   187: invokeinterface add : (Ljava/lang/Object;)Z
    //   192: pop
    //   193: goto -> 86
    //   196: astore_3
    //   197: goto -> 307
    //   200: astore_3
    //   201: getstatic android/support/v7/widget/ActivityChooserModel.LOG_TAG : Ljava/lang/String;
    //   204: astore #4
    //   206: new java/lang/StringBuilder
    //   209: dup
    //   210: invokespecial <init> : ()V
    //   213: astore #5
    //   215: aload #5
    //   217: ldc_w 'Error reading historical recrod file: '
    //   220: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   223: pop
    //   224: aload #5
    //   226: aload_0
    //   227: getfield mHistoryFileName : Ljava/lang/String;
    //   230: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   233: pop
    //   234: aload #4
    //   236: aload #5
    //   238: invokevirtual toString : ()Ljava/lang/String;
    //   241: aload_3
    //   242: invokestatic e : (Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   245: pop
    //   246: aload_2
    //   247: ifnull -> 306
    //   250: goto -> 102
    //   253: astore_3
    //   254: getstatic android/support/v7/widget/ActivityChooserModel.LOG_TAG : Ljava/lang/String;
    //   257: astore #4
    //   259: new java/lang/StringBuilder
    //   262: dup
    //   263: invokespecial <init> : ()V
    //   266: astore #5
    //   268: aload #5
    //   270: ldc_w 'Error reading historical recrod file: '
    //   273: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   276: pop
    //   277: aload #5
    //   279: aload_0
    //   280: getfield mHistoryFileName : Ljava/lang/String;
    //   283: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   286: pop
    //   287: aload #4
    //   289: aload #5
    //   291: invokevirtual toString : ()Ljava/lang/String;
    //   294: aload_3
    //   295: invokestatic e : (Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   298: pop
    //   299: aload_2
    //   300: ifnull -> 306
    //   303: goto -> 102
    //   306: return
    //   307: aload_2
    //   308: ifnull -> 315
    //   311: aload_2
    //   312: invokevirtual close : ()V
    //   315: aload_3
    //   316: athrow
    //   317: astore_2
    //   318: return
    //   319: astore_2
    //   320: return
    //   321: astore_2
    //   322: goto -> 315
    // Exception table:
    //   from	to	target	type
    //   0	12	317	java/io/FileNotFoundException
    //   12	26	253	org/xmlpull/v1/XmlPullParserException
    //   12	26	200	java/io/IOException
    //   12	26	196	finally
    //   38	45	253	org/xmlpull/v1/XmlPullParserException
    //   38	45	200	java/io/IOException
    //   38	45	196	finally
    //   48	73	253	org/xmlpull/v1/XmlPullParserException
    //   48	73	200	java/io/IOException
    //   48	73	196	finally
    //   73	86	253	org/xmlpull/v1/XmlPullParserException
    //   73	86	200	java/io/IOException
    //   73	86	196	finally
    //   86	93	253	org/xmlpull/v1/XmlPullParserException
    //   86	93	200	java/io/IOException
    //   86	93	196	finally
    //   102	106	319	java/io/IOException
    //   120	145	253	org/xmlpull/v1/XmlPullParserException
    //   120	145	200	java/io/IOException
    //   120	145	196	finally
    //   145	193	253	org/xmlpull/v1/XmlPullParserException
    //   145	193	200	java/io/IOException
    //   145	193	196	finally
    //   201	246	196	finally
    //   254	299	196	finally
    //   311	315	321	java/io/IOException }
  
  private boolean sortActivitiesIfNeeded() {
    if (this.mActivitySorter != null && this.mIntent != null && !this.mActivities.isEmpty() && !this.mHistoricalRecords.isEmpty()) {
      this.mActivitySorter.sort(this.mIntent, this.mActivities, Collections.unmodifiableList(this.mHistoricalRecords));
      return true;
    } 
    return false;
  }
  
  public Intent chooseActivity(int paramInt) {
    synchronized (this.mInstanceLock) {
      if (this.mIntent == null)
        return null; 
      ensureConsistentState();
      ActivityResolveInfo activityResolveInfo = (ActivityResolveInfo)this.mActivities.get(paramInt);
      ComponentName componentName = new ComponentName(activityResolveInfo.resolveInfo.activityInfo.packageName, activityResolveInfo.resolveInfo.activityInfo.name);
      Intent intent = new Intent(this.mIntent);
      intent.setComponent(componentName);
      if (this.mActivityChoserModelPolicy != null) {
        Intent intent1 = new Intent(intent);
        if (this.mActivityChoserModelPolicy.onChooseActivity(this, intent1))
          return null; 
      } 
      addHistoricalRecord(new HistoricalRecord(componentName, System.currentTimeMillis(), 1.0F));
      return intent;
    } 
  }
  
  public ResolveInfo getActivity(int paramInt) {
    synchronized (this.mInstanceLock) {
      ensureConsistentState();
      return ((ActivityResolveInfo)this.mActivities.get(paramInt)).resolveInfo;
    } 
  }
  
  public int getActivityCount() {
    synchronized (this.mInstanceLock) {
      ensureConsistentState();
      return this.mActivities.size();
    } 
  }
  
  public int getActivityIndex(ResolveInfo paramResolveInfo) {
    synchronized (this.mInstanceLock) {
      ensureConsistentState();
      List list = this.mActivities;
      int i = list.size();
      for (byte b = 0;; b++) {
        if (b < i) {
          if (((ActivityResolveInfo)list.get(b)).resolveInfo == paramResolveInfo)
            return b; 
        } else {
          return -1;
        } 
      } 
    } 
  }
  
  public ResolveInfo getDefaultActivity() {
    synchronized (this.mInstanceLock) {
      ensureConsistentState();
      if (!this.mActivities.isEmpty())
        return ((ActivityResolveInfo)this.mActivities.get(0)).resolveInfo; 
      return null;
    } 
  }
  
  public int getHistoryMaxSize() {
    synchronized (this.mInstanceLock) {
      return this.mHistoryMaxSize;
    } 
  }
  
  public int getHistorySize() {
    synchronized (this.mInstanceLock) {
      ensureConsistentState();
      return this.mHistoricalRecords.size();
    } 
  }
  
  public Intent getIntent() {
    synchronized (this.mInstanceLock) {
      return this.mIntent;
    } 
  }
  
  public void setActivitySorter(ActivitySorter paramActivitySorter) {
    synchronized (this.mInstanceLock) {
      if (this.mActivitySorter == paramActivitySorter)
        return; 
      this.mActivitySorter = paramActivitySorter;
      if (sortActivitiesIfNeeded())
        notifyChanged(); 
      return;
    } 
  }
  
  public void setDefaultActivity(int paramInt) {
    synchronized (this.mInstanceLock) {
      float f;
      ensureConsistentState();
      ActivityResolveInfo activityResolveInfo1 = (ActivityResolveInfo)this.mActivities.get(paramInt);
      ActivityResolveInfo activityResolveInfo2 = (ActivityResolveInfo)this.mActivities.get(0);
      if (activityResolveInfo2 != null) {
        f = activityResolveInfo2.weight - activityResolveInfo1.weight + 5.0F;
      } else {
        f = 1.0F;
      } 
      addHistoricalRecord(new HistoricalRecord(new ComponentName(activityResolveInfo1.resolveInfo.activityInfo.packageName, activityResolveInfo1.resolveInfo.activityInfo.name), System.currentTimeMillis(), f));
      return;
    } 
  }
  
  public void setHistoryMaxSize(int paramInt) {
    synchronized (this.mInstanceLock) {
      if (this.mHistoryMaxSize == paramInt)
        return; 
      this.mHistoryMaxSize = paramInt;
      pruneExcessiveHistoricalRecordsIfNeeded();
      if (sortActivitiesIfNeeded())
        notifyChanged(); 
      return;
    } 
  }
  
  public void setIntent(Intent paramIntent) {
    synchronized (this.mInstanceLock) {
      if (this.mIntent == paramIntent)
        return; 
      this.mIntent = paramIntent;
      this.mReloadActivities = true;
      ensureConsistentState();
      return;
    } 
  }
  
  public void setOnChooseActivityListener(OnChooseActivityListener paramOnChooseActivityListener) {
    synchronized (this.mInstanceLock) {
      this.mActivityChoserModelPolicy = paramOnChooseActivityListener;
      return;
    } 
  }
  
  public static interface ActivityChooserModelClient {
    void setActivityChooserModel(ActivityChooserModel param1ActivityChooserModel);
  }
  
  public static final class ActivityResolveInfo extends Object implements Comparable<ActivityResolveInfo> {
    public final ResolveInfo resolveInfo;
    
    public float weight;
    
    public ActivityResolveInfo(ResolveInfo param1ResolveInfo) { this.resolveInfo = param1ResolveInfo; }
    
    public int compareTo(ActivityResolveInfo param1ActivityResolveInfo) { return Float.floatToIntBits(param1ActivityResolveInfo.weight) - Float.floatToIntBits(this.weight); }
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      if (param1Object == null)
        return false; 
      if (getClass() != param1Object.getClass())
        return false; 
      param1Object = (ActivityResolveInfo)param1Object;
      return !(Float.floatToIntBits(this.weight) != Float.floatToIntBits(param1Object.weight));
    }
    
    public int hashCode() { return Float.floatToIntBits(this.weight) + 31; }
    
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("[");
      stringBuilder.append("resolveInfo:");
      stringBuilder.append(this.resolveInfo.toString());
      stringBuilder.append("; weight:");
      stringBuilder.append(new BigDecimal(this.weight));
      stringBuilder.append("]");
      return stringBuilder.toString();
    }
  }
  
  public static interface ActivitySorter {
    void sort(Intent param1Intent, List<ActivityChooserModel.ActivityResolveInfo> param1List1, List<ActivityChooserModel.HistoricalRecord> param1List2);
  }
  
  private static final class DefaultSorter implements ActivitySorter {
    private static final float WEIGHT_DECAY_COEFFICIENT = 0.95F;
    
    private final Map<ComponentName, ActivityChooserModel.ActivityResolveInfo> mPackageNameToActivityMap = new HashMap();
    
    public void sort(Intent param1Intent, List<ActivityChooserModel.ActivityResolveInfo> param1List1, List<ActivityChooserModel.HistoricalRecord> param1List2) {
      Map map = this.mPackageNameToActivityMap;
      map.clear();
      int j = param1List1.size();
      int i;
      for (i = 0; i < j; i++) {
        ActivityChooserModel.ActivityResolveInfo activityResolveInfo = (ActivityChooserModel.ActivityResolveInfo)param1List1.get(i);
        activityResolveInfo.weight = 0.0F;
        map.put(new ComponentName(activityResolveInfo.resolveInfo.activityInfo.packageName, activityResolveInfo.resolveInfo.activityInfo.name), activityResolveInfo);
      } 
      i = param1List2.size() - 1;
      float f;
      for (f = 1.0F; i >= 0; f = f1) {
        ActivityChooserModel.HistoricalRecord historicalRecord = (ActivityChooserModel.HistoricalRecord)param1List2.get(i);
        ActivityChooserModel.ActivityResolveInfo activityResolveInfo = (ActivityChooserModel.ActivityResolveInfo)map.get(historicalRecord.activity);
        float f1 = f;
        if (activityResolveInfo != null) {
          activityResolveInfo.weight += historicalRecord.weight * f;
          f1 = f * 0.95F;
        } 
        i--;
      } 
      Collections.sort(param1List1);
    }
  }
  
  public static final class HistoricalRecord {
    public final ComponentName activity;
    
    public final long time;
    
    public final float weight;
    
    public HistoricalRecord(ComponentName param1ComponentName, long param1Long, float param1Float) {
      this.activity = param1ComponentName;
      this.time = param1Long;
      this.weight = param1Float;
    }
    
    public HistoricalRecord(String param1String, long param1Long, float param1Float) { this(ComponentName.unflattenFromString(param1String), param1Long, param1Float); }
    
    public boolean equals(Object param1Object) {
      if (this == param1Object)
        return true; 
      if (param1Object == null)
        return false; 
      if (getClass() != param1Object.getClass())
        return false; 
      param1Object = (HistoricalRecord)param1Object;
      if (this.activity == null) {
        if (param1Object.activity != null)
          return false; 
      } else if (!this.activity.equals(param1Object.activity)) {
        return false;
      } 
      return (this.time != param1Object.time) ? false : (!(Float.floatToIntBits(this.weight) != Float.floatToIntBits(param1Object.weight)));
    }
    
    public int hashCode() {
      int i;
      if (this.activity == null) {
        i = 0;
      } else {
        i = this.activity.hashCode();
      } 
      return ((i + 31) * 31 + (int)(this.time ^ this.time >>> 32)) * 31 + Float.floatToIntBits(this.weight);
    }
    
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("[");
      stringBuilder.append("; activity:");
      stringBuilder.append(this.activity);
      stringBuilder.append("; time:");
      stringBuilder.append(this.time);
      stringBuilder.append("; weight:");
      stringBuilder.append(new BigDecimal(this.weight));
      stringBuilder.append("]");
      return stringBuilder.toString();
    }
  }
  
  public static interface OnChooseActivityListener {
    boolean onChooseActivity(ActivityChooserModel param1ActivityChooserModel, Intent param1Intent);
  }
  
  private final class PersistHistoryAsyncTask extends AsyncTask<Object, Void, Void> {
    public Void doInBackground(Object... param1VarArgs) { // Byte code:
      //   0: aload_1
      //   1: iconst_0
      //   2: aaload
      //   3: checkcast java/util/List
      //   6: astore #4
      //   8: aload_1
      //   9: iconst_1
      //   10: aaload
      //   11: checkcast java/lang/String
      //   14: astore #5
      //   16: aload_0
      //   17: getfield this$0 : Landroid/support/v7/widget/ActivityChooserModel;
      //   20: getfield mContext : Landroid/content/Context;
      //   23: aload #5
      //   25: iconst_0
      //   26: invokevirtual openFileOutput : (Ljava/lang/String;I)Ljava/io/FileOutputStream;
      //   29: astore_1
      //   30: invokestatic newSerializer : ()Lorg/xmlpull/v1/XmlSerializer;
      //   33: astore #5
      //   35: aload #5
      //   37: aload_1
      //   38: aconst_null
      //   39: invokeinterface setOutput : (Ljava/io/OutputStream;Ljava/lang/String;)V
      //   44: aload #5
      //   46: ldc 'UTF-8'
      //   48: iconst_1
      //   49: invokestatic valueOf : (Z)Ljava/lang/Boolean;
      //   52: invokeinterface startDocument : (Ljava/lang/String;Ljava/lang/Boolean;)V
      //   57: aload #5
      //   59: aconst_null
      //   60: ldc 'historical-records'
      //   62: invokeinterface startTag : (Ljava/lang/String;Ljava/lang/String;)Lorg/xmlpull/v1/XmlSerializer;
      //   67: pop
      //   68: aload #4
      //   70: invokeinterface size : ()I
      //   75: istore_3
      //   76: iconst_0
      //   77: istore_2
      //   78: iload_2
      //   79: iload_3
      //   80: if_icmpge -> 182
      //   83: aload #4
      //   85: iconst_0
      //   86: invokeinterface remove : (I)Ljava/lang/Object;
      //   91: checkcast android/support/v7/widget/ActivityChooserModel$HistoricalRecord
      //   94: astore #6
      //   96: aload #5
      //   98: aconst_null
      //   99: ldc 'historical-record'
      //   101: invokeinterface startTag : (Ljava/lang/String;Ljava/lang/String;)Lorg/xmlpull/v1/XmlSerializer;
      //   106: pop
      //   107: aload #5
      //   109: aconst_null
      //   110: ldc 'activity'
      //   112: aload #6
      //   114: getfield activity : Landroid/content/ComponentName;
      //   117: invokevirtual flattenToString : ()Ljava/lang/String;
      //   120: invokeinterface attribute : (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/xmlpull/v1/XmlSerializer;
      //   125: pop
      //   126: aload #5
      //   128: aconst_null
      //   129: ldc 'time'
      //   131: aload #6
      //   133: getfield time : J
      //   136: invokestatic valueOf : (J)Ljava/lang/String;
      //   139: invokeinterface attribute : (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/xmlpull/v1/XmlSerializer;
      //   144: pop
      //   145: aload #5
      //   147: aconst_null
      //   148: ldc 'weight'
      //   150: aload #6
      //   152: getfield weight : F
      //   155: invokestatic valueOf : (F)Ljava/lang/String;
      //   158: invokeinterface attribute : (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lorg/xmlpull/v1/XmlSerializer;
      //   163: pop
      //   164: aload #5
      //   166: aconst_null
      //   167: ldc 'historical-record'
      //   169: invokeinterface endTag : (Ljava/lang/String;Ljava/lang/String;)Lorg/xmlpull/v1/XmlSerializer;
      //   174: pop
      //   175: iload_2
      //   176: iconst_1
      //   177: iadd
      //   178: istore_2
      //   179: goto -> 78
      //   182: aload #5
      //   184: aconst_null
      //   185: ldc 'historical-records'
      //   187: invokeinterface endTag : (Ljava/lang/String;Ljava/lang/String;)Lorg/xmlpull/v1/XmlSerializer;
      //   192: pop
      //   193: aload #5
      //   195: invokeinterface endDocument : ()V
      //   200: aload_0
      //   201: getfield this$0 : Landroid/support/v7/widget/ActivityChooserModel;
      //   204: iconst_1
      //   205: putfield mCanReadHistoricalData : Z
      //   208: aload_1
      //   209: ifnull -> 418
      //   212: aload_1
      //   213: invokevirtual close : ()V
      //   216: aconst_null
      //   217: areturn
      //   218: astore #4
      //   220: goto -> 420
      //   223: astore #4
      //   225: getstatic android/support/v7/widget/ActivityChooserModel.LOG_TAG : Ljava/lang/String;
      //   228: astore #5
      //   230: new java/lang/StringBuilder
      //   233: dup
      //   234: invokespecial <init> : ()V
      //   237: astore #6
      //   239: aload #6
      //   241: ldc 'Error writing historical record file: '
      //   243: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   246: pop
      //   247: aload #6
      //   249: aload_0
      //   250: getfield this$0 : Landroid/support/v7/widget/ActivityChooserModel;
      //   253: getfield mHistoryFileName : Ljava/lang/String;
      //   256: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   259: pop
      //   260: aload #5
      //   262: aload #6
      //   264: invokevirtual toString : ()Ljava/lang/String;
      //   267: aload #4
      //   269: invokestatic e : (Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   272: pop
      //   273: aload_0
      //   274: getfield this$0 : Landroid/support/v7/widget/ActivityChooserModel;
      //   277: iconst_1
      //   278: putfield mCanReadHistoricalData : Z
      //   281: aload_1
      //   282: ifnull -> 418
      //   285: goto -> 212
      //   288: astore #4
      //   290: getstatic android/support/v7/widget/ActivityChooserModel.LOG_TAG : Ljava/lang/String;
      //   293: astore #5
      //   295: new java/lang/StringBuilder
      //   298: dup
      //   299: invokespecial <init> : ()V
      //   302: astore #6
      //   304: aload #6
      //   306: ldc 'Error writing historical record file: '
      //   308: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   311: pop
      //   312: aload #6
      //   314: aload_0
      //   315: getfield this$0 : Landroid/support/v7/widget/ActivityChooserModel;
      //   318: getfield mHistoryFileName : Ljava/lang/String;
      //   321: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   324: pop
      //   325: aload #5
      //   327: aload #6
      //   329: invokevirtual toString : ()Ljava/lang/String;
      //   332: aload #4
      //   334: invokestatic e : (Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   337: pop
      //   338: aload_0
      //   339: getfield this$0 : Landroid/support/v7/widget/ActivityChooserModel;
      //   342: iconst_1
      //   343: putfield mCanReadHistoricalData : Z
      //   346: aload_1
      //   347: ifnull -> 418
      //   350: goto -> 212
      //   353: astore #4
      //   355: getstatic android/support/v7/widget/ActivityChooserModel.LOG_TAG : Ljava/lang/String;
      //   358: astore #5
      //   360: new java/lang/StringBuilder
      //   363: dup
      //   364: invokespecial <init> : ()V
      //   367: astore #6
      //   369: aload #6
      //   371: ldc 'Error writing historical record file: '
      //   373: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   376: pop
      //   377: aload #6
      //   379: aload_0
      //   380: getfield this$0 : Landroid/support/v7/widget/ActivityChooserModel;
      //   383: getfield mHistoryFileName : Ljava/lang/String;
      //   386: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   389: pop
      //   390: aload #5
      //   392: aload #6
      //   394: invokevirtual toString : ()Ljava/lang/String;
      //   397: aload #4
      //   399: invokestatic e : (Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   402: pop
      //   403: aload_0
      //   404: getfield this$0 : Landroid/support/v7/widget/ActivityChooserModel;
      //   407: iconst_1
      //   408: putfield mCanReadHistoricalData : Z
      //   411: aload_1
      //   412: ifnull -> 418
      //   415: goto -> 212
      //   418: aconst_null
      //   419: areturn
      //   420: aload_0
      //   421: getfield this$0 : Landroid/support/v7/widget/ActivityChooserModel;
      //   424: iconst_1
      //   425: putfield mCanReadHistoricalData : Z
      //   428: aload_1
      //   429: ifnull -> 436
      //   432: aload_1
      //   433: invokevirtual close : ()V
      //   436: aload #4
      //   438: athrow
      //   439: astore_1
      //   440: getstatic android/support/v7/widget/ActivityChooserModel.LOG_TAG : Ljava/lang/String;
      //   443: astore #4
      //   445: new java/lang/StringBuilder
      //   448: dup
      //   449: invokespecial <init> : ()V
      //   452: astore #6
      //   454: aload #6
      //   456: ldc 'Error writing historical record file: '
      //   458: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   461: pop
      //   462: aload #6
      //   464: aload #5
      //   466: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   469: pop
      //   470: aload #4
      //   472: aload #6
      //   474: invokevirtual toString : ()Ljava/lang/String;
      //   477: aload_1
      //   478: invokestatic e : (Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   481: pop
      //   482: aconst_null
      //   483: areturn
      //   484: astore_1
      //   485: aconst_null
      //   486: areturn
      //   487: astore_1
      //   488: goto -> 436
      // Exception table:
      //   from	to	target	type
      //   16	30	439	java/io/FileNotFoundException
      //   35	76	353	java/lang/IllegalArgumentException
      //   35	76	288	java/lang/IllegalStateException
      //   35	76	223	java/io/IOException
      //   35	76	218	finally
      //   83	175	353	java/lang/IllegalArgumentException
      //   83	175	288	java/lang/IllegalStateException
      //   83	175	223	java/io/IOException
      //   83	175	218	finally
      //   182	200	353	java/lang/IllegalArgumentException
      //   182	200	288	java/lang/IllegalStateException
      //   182	200	223	java/io/IOException
      //   182	200	218	finally
      //   212	216	484	java/io/IOException
      //   225	273	218	finally
      //   290	338	218	finally
      //   355	403	218	finally
      //   432	436	487	java/io/IOException }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/widget/ActivityChooserModel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */