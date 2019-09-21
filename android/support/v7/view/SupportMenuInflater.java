package android.support.v7.view;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.PorterDuff;
import android.support.annotation.LayoutRes;
import android.support.annotation.RestrictTo;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.appcompat.R;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuItemWrapperICS;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class SupportMenuInflater extends MenuInflater {
  static final Class<?>[] ACTION_PROVIDER_CONSTRUCTOR_SIGNATURE;
  
  static final Class<?>[] ACTION_VIEW_CONSTRUCTOR_SIGNATURE = { Context.class };
  
  static final String LOG_TAG = "SupportMenuInflater";
  
  static final int NO_ID = 0;
  
  private static final String XML_GROUP = "group";
  
  private static final String XML_ITEM = "item";
  
  private static final String XML_MENU = "menu";
  
  final Object[] mActionProviderConstructorArguments;
  
  final Object[] mActionViewConstructorArguments;
  
  Context mContext;
  
  private Object mRealOwner;
  
  static  {
    ACTION_PROVIDER_CONSTRUCTOR_SIGNATURE = ACTION_VIEW_CONSTRUCTOR_SIGNATURE;
  }
  
  public SupportMenuInflater(Context paramContext) {
    super(paramContext);
    this.mContext = paramContext;
    this.mActionViewConstructorArguments = new Object[] { paramContext };
    this.mActionProviderConstructorArguments = this.mActionViewConstructorArguments;
  }
  
  private Object findRealOwner(Object paramObject) { return (paramObject instanceof android.app.Activity) ? paramObject : ((paramObject instanceof ContextWrapper) ? findRealOwner(((ContextWrapper)paramObject).getBaseContext()) : paramObject); }
  
  private void parseMenu(XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Menu paramMenu) throws XmlPullParserException, IOException {
    StringBuilder stringBuilder;
    MenuState menuState = new MenuState(paramMenu);
    int i = paramXmlPullParser.getEventType();
    while (true) {
      if (i == 2) {
        String str = paramXmlPullParser.getName();
        if (str.equals("menu")) {
          i = paramXmlPullParser.next();
          break;
        } 
        stringBuilder = new StringBuilder();
        stringBuilder.append("Expecting menu, got ");
        stringBuilder.append(str);
        throw new RuntimeException(stringBuilder.toString());
      } 
      int m = stringBuilder.next();
      i = m;
      if (m == 1) {
        i = m;
        break;
      } 
    } 
    Menu menu = null;
    byte b = 0;
    int j = 0;
    int k = i;
    while (!b) {
      String str;
      byte b1;
      Menu menu1;
      switch (k) {
        default:
          i = j;
          paramMenu = menu;
          b1 = b;
          break;
        case 3:
          str = stringBuilder.getName();
          if (j != 0 && str.equals(menu)) {
            paramMenu = null;
            i = 0;
            b1 = b;
            break;
          } 
          if (str.equals("group")) {
            menuState.resetGroup();
            i = j;
            paramMenu = menu;
            b1 = b;
            break;
          } 
          if (str.equals("item")) {
            i = j;
            paramMenu = menu;
            b1 = b;
            if (!menuState.hasAddedItem()) {
              if (menuState.itemActionProvider != null && menuState.itemActionProvider.hasSubMenu()) {
                menuState.addSubMenuItem();
                i = j;
                paramMenu = menu;
                b1 = b;
                break;
              } 
              menuState.addItem();
              i = j;
              paramMenu = menu;
              b1 = b;
            } 
            break;
          } 
          i = j;
          paramMenu = menu;
          b1 = b;
          if (str.equals("menu")) {
            b1 = 1;
            i = j;
            paramMenu = menu;
          } 
          break;
        case 2:
          if (j != 0) {
            i = j;
            paramMenu = menu;
            b1 = b;
            break;
          } 
          menu1 = stringBuilder.getName();
          if (menu1.equals("group")) {
            menuState.readGroup(paramAttributeSet);
            i = j;
            menu1 = menu;
            b1 = b;
            break;
          } 
          if (menu1.equals("item")) {
            menuState.readItem(paramAttributeSet);
            i = j;
            menu1 = menu;
            b1 = b;
            break;
          } 
          if (menu1.equals("menu")) {
            parseMenu(stringBuilder, paramAttributeSet, menuState.addSubMenuItem());
            i = j;
            menu1 = menu;
            b1 = b;
            break;
          } 
          i = 1;
          b1 = b;
          break;
        case 1:
          throw new RuntimeException("Unexpected end of document");
      } 
      k = stringBuilder.next();
      j = i;
      menu = menu1;
      b = b1;
    } 
  }
  
  Object getRealOwner() {
    if (this.mRealOwner == null)
      this.mRealOwner = findRealOwner(this.mContext); 
    return this.mRealOwner;
  }
  
  public void inflate(@LayoutRes int paramInt, Menu paramMenu) {
    if (!(paramMenu instanceof android.support.v4.internal.view.SupportMenu)) {
      super.inflate(paramInt, paramMenu);
      return;
    } 
    XmlResourceParser xmlResourceParser2 = null;
    xmlResourceParser3 = null;
    xmlResourceParser1 = null;
    try {
      xmlResourceParser = this.mContext.getResources().getLayout(paramInt);
    } catch (XmlPullParserException paramMenu) {
    
    } catch (IOException paramMenu) {
      xmlResourceParser1 = xmlResourceParser2;
      throw new InflateException("Error inflating menu XML", paramMenu);
    } finally {
      if (xmlResourceParser1 != null)
        xmlResourceParser1.close(); 
    } 
    throw new InflateException("Error inflating menu XML", paramMenu);
  }
  
  private static class InflatedOnMenuItemClickListener implements MenuItem.OnMenuItemClickListener {
    private static final Class<?>[] PARAM_TYPES = { MenuItem.class };
    
    private Method mMethod;
    
    private Object mRealOwner;
    
    public InflatedOnMenuItemClickListener(Object param1Object, String param1String) {
      this.mRealOwner = param1Object;
      Class clazz = param1Object.getClass();
      try {
        this.mMethod = clazz.getMethod(param1String, PARAM_TYPES);
        return;
      } catch (Exception param1Object) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Couldn't resolve menu item onClick handler ");
        stringBuilder.append(param1String);
        stringBuilder.append(" in class ");
        stringBuilder.append(clazz.getName());
        InflateException inflateException = new InflateException(stringBuilder.toString());
        inflateException.initCause(param1Object);
        throw inflateException;
      } 
    }
    
    public boolean onMenuItemClick(MenuItem param1MenuItem) {
      try {
        if (this.mMethod.getReturnType() == boolean.class)
          return ((Boolean)this.mMethod.invoke(this.mRealOwner, new Object[] { param1MenuItem })).booleanValue(); 
        this.mMethod.invoke(this.mRealOwner, new Object[] { param1MenuItem });
        return true;
      } catch (Exception param1MenuItem) {
        throw new RuntimeException(param1MenuItem);
      } 
    }
  }
  
  private class MenuState {
    private static final int defaultGroupId = 0;
    
    private static final int defaultItemCategory = 0;
    
    private static final int defaultItemCheckable = 0;
    
    private static final boolean defaultItemChecked = false;
    
    private static final boolean defaultItemEnabled = true;
    
    private static final int defaultItemId = 0;
    
    private static final int defaultItemOrder = 0;
    
    private static final boolean defaultItemVisible = true;
    
    private int groupCategory;
    
    private int groupCheckable;
    
    private boolean groupEnabled;
    
    private int groupId;
    
    private int groupOrder;
    
    private boolean groupVisible;
    
    ActionProvider itemActionProvider;
    
    private String itemActionProviderClassName;
    
    private String itemActionViewClassName;
    
    private int itemActionViewLayout;
    
    private boolean itemAdded;
    
    private int itemAlphabeticModifiers;
    
    private char itemAlphabeticShortcut;
    
    private int itemCategoryOrder;
    
    private int itemCheckable;
    
    private boolean itemChecked;
    
    private CharSequence itemContentDescription;
    
    private boolean itemEnabled;
    
    private int itemIconResId;
    
    private ColorStateList itemIconTintList = null;
    
    private PorterDuff.Mode itemIconTintMode = null;
    
    private int itemId;
    
    private String itemListenerMethodName;
    
    private int itemNumericModifiers;
    
    private char itemNumericShortcut;
    
    private int itemShowAsAction;
    
    private CharSequence itemTitle;
    
    private CharSequence itemTitleCondensed;
    
    private CharSequence itemTooltipText;
    
    private boolean itemVisible;
    
    private Menu menu;
    
    public MenuState(Menu param1Menu) {
      this.menu = param1Menu;
      resetGroup();
    }
    
    private char getShortcut(String param1String) { return (param1String == null) ? Character.MIN_VALUE : param1String.charAt(0); }
    
    private <T> T newInstance(String param1String, Class<?>[] param1ArrayOfClass, Object[] param1ArrayOfObject) {
      try {
        Constructor constructor = SupportMenuInflater.this.mContext.getClassLoader().loadClass(param1String).getConstructor(param1ArrayOfClass);
        constructor.setAccessible(true);
        return (T)constructor.newInstance(param1ArrayOfObject);
      } catch (Exception param1ArrayOfClass) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Cannot instantiate class: ");
        stringBuilder.append(param1String);
        Log.w("SupportMenuInflater", stringBuilder.toString(), param1ArrayOfClass);
        return null;
      } 
    }
    
    private void setItem(MenuItem param1MenuItem) {
      MenuItem menuItem = param1MenuItem.setChecked(this.itemChecked).setVisible(this.itemVisible).setEnabled(this.itemEnabled);
      int i = this.itemCheckable;
      boolean bool = false;
      if (i >= 1) {
        bool1 = true;
      } else {
        bool1 = false;
      } 
      menuItem.setCheckable(bool1).setTitleCondensed(this.itemTitleCondensed).setIcon(this.itemIconResId);
      if (this.itemShowAsAction >= 0)
        param1MenuItem.setShowAsAction(this.itemShowAsAction); 
      if (this.itemListenerMethodName != null) {
        if (SupportMenuInflater.this.mContext.isRestricted())
          throw new IllegalStateException("The android:onClick attribute cannot be used within a restricted context"); 
        param1MenuItem.setOnMenuItemClickListener(new SupportMenuInflater.InflatedOnMenuItemClickListener(SupportMenuInflater.this.getRealOwner(), this.itemListenerMethodName));
      } 
      boolean bool1 = param1MenuItem instanceof MenuItemImpl;
      if (bool1)
        MenuItemImpl menuItemImpl = (MenuItemImpl)param1MenuItem; 
      if (this.itemCheckable >= 2)
        if (bool1) {
          ((MenuItemImpl)param1MenuItem).setExclusiveCheckable(true);
        } else if (param1MenuItem instanceof MenuItemWrapperICS) {
          ((MenuItemWrapperICS)param1MenuItem).setExclusiveCheckable(true);
        }  
      if (this.itemActionViewClassName != null) {
        param1MenuItem.setActionView((View)newInstance(this.itemActionViewClassName, SupportMenuInflater.ACTION_VIEW_CONSTRUCTOR_SIGNATURE, SupportMenuInflater.this.mActionViewConstructorArguments));
        bool = true;
      } 
      if (this.itemActionViewLayout > 0)
        if (!bool) {
          param1MenuItem.setActionView(this.itemActionViewLayout);
        } else {
          Log.w("SupportMenuInflater", "Ignoring attribute 'itemActionViewLayout'. Action view already specified.");
        }  
      if (this.itemActionProvider != null)
        MenuItemCompat.setActionProvider(param1MenuItem, this.itemActionProvider); 
      MenuItemCompat.setContentDescription(param1MenuItem, this.itemContentDescription);
      MenuItemCompat.setTooltipText(param1MenuItem, this.itemTooltipText);
      MenuItemCompat.setAlphabeticShortcut(param1MenuItem, this.itemAlphabeticShortcut, this.itemAlphabeticModifiers);
      MenuItemCompat.setNumericShortcut(param1MenuItem, this.itemNumericShortcut, this.itemNumericModifiers);
      if (this.itemIconTintMode != null)
        MenuItemCompat.setIconTintMode(param1MenuItem, this.itemIconTintMode); 
      if (this.itemIconTintList != null)
        MenuItemCompat.setIconTintList(param1MenuItem, this.itemIconTintList); 
    }
    
    public void addItem() {
      this.itemAdded = true;
      setItem(this.menu.add(this.groupId, this.itemId, this.itemCategoryOrder, this.itemTitle));
    }
    
    public SubMenu addSubMenuItem() {
      this.itemAdded = true;
      SubMenu subMenu = this.menu.addSubMenu(this.groupId, this.itemId, this.itemCategoryOrder, this.itemTitle);
      setItem(subMenu.getItem());
      return subMenu;
    }
    
    public boolean hasAddedItem() { return this.itemAdded; }
    
    public void readGroup(AttributeSet param1AttributeSet) {
      TypedArray typedArray = SupportMenuInflater.this.mContext.obtainStyledAttributes(param1AttributeSet, R.styleable.MenuGroup);
      this.groupId = typedArray.getResourceId(R.styleable.MenuGroup_android_id, 0);
      this.groupCategory = typedArray.getInt(R.styleable.MenuGroup_android_menuCategory, 0);
      this.groupOrder = typedArray.getInt(R.styleable.MenuGroup_android_orderInCategory, 0);
      this.groupCheckable = typedArray.getInt(R.styleable.MenuGroup_android_checkableBehavior, 0);
      this.groupVisible = typedArray.getBoolean(R.styleable.MenuGroup_android_visible, true);
      this.groupEnabled = typedArray.getBoolean(R.styleable.MenuGroup_android_enabled, true);
      typedArray.recycle();
    }
    
    public void readItem(AttributeSet param1AttributeSet) { throw new RuntimeException("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.copyTypes(TypeTransformer.java:311)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.fixTypes(TypeTransformer.java:226)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:207)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n"); }
    
    public void resetGroup() {
      this.groupId = 0;
      this.groupCategory = 0;
      this.groupOrder = 0;
      this.groupCheckable = 0;
      this.groupVisible = true;
      this.groupEnabled = true;
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v7/view/SupportMenuInflater.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */