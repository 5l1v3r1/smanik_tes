package com.blogspot.scqq.b0x.Model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class SerialNumber {
  @ColumnInfo(name = "namaKey")
  private String namaKey;
  
  @PrimaryKey(autoGenerate = true)
  private int serialNumberID;
  
  @ColumnInfo(name = "valueKey")
  private String valueKey;
  
  public SerialNumber() {}
  
  public SerialNumber(int paramInt, String paramString1, String paramString2) {
    this.serialNumberID = paramInt;
    this.namaKey = paramString1;
    this.valueKey = paramString2;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof SerialNumber))
      return false; 
    paramObject = (SerialNumber)paramObject;
    return (this.serialNumberID != paramObject.serialNumberID) ? false : ((this.namaKey != null) ? this.namaKey.equals(paramObject.namaKey) : ((paramObject.namaKey == null) ? 1 : 0));
  }
  
  public String getNamaKey() { return this.namaKey; }
  
  public int getSerialNumberID() { return this.serialNumberID; }
  
  public String getValueKey() { return this.valueKey; }
  
  public int hashCode() {
    int i;
    int j = this.serialNumberID;
    if (this.namaKey != null) {
      i = this.namaKey.hashCode();
    } else {
      i = 0;
    } 
    return j * 31 + i;
  }
  
  public void setNamaKey(String paramString) { this.namaKey = paramString; }
  
  public void setSerialNumberID(int paramInt) { this.serialNumberID = paramInt; }
  
  public void setValueKey(String paramString) { this.valueKey = paramString; }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("SerialNumber{serialNumberID=");
    stringBuilder.append(this.serialNumberID);
    stringBuilder.append(", namaKey='");
    stringBuilder.append(this.namaKey);
    stringBuilder.append('\'');
    stringBuilder.append(", valueKey='");
    stringBuilder.append(this.valueKey);
    stringBuilder.append('\'');
    stringBuilder.append('}');
    return stringBuilder.toString();
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/com/blogspot/scqq/b0x/Model/SerialNumber.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */