package android.support.v4.graphics;

import android.graphics.Path;
import android.support.annotation.RestrictTo;
import android.util.Log;
import java.util.ArrayList;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class PathParser {
  private static final String LOGTAG = "PathParser";
  
  private static void addNode(ArrayList<PathDataNode> paramArrayList, char paramChar, float[] paramArrayOfFloat) { paramArrayList.add(new PathDataNode(paramChar, paramArrayOfFloat)); }
  
  public static boolean canMorph(PathDataNode[] paramArrayOfPathDataNode1, PathDataNode[] paramArrayOfPathDataNode2) {
    if (paramArrayOfPathDataNode1 != null) {
      if (paramArrayOfPathDataNode2 == null)
        return false; 
      if (paramArrayOfPathDataNode1.length != paramArrayOfPathDataNode2.length)
        return false; 
      byte b = 0;
      while (b < paramArrayOfPathDataNode1.length) {
        if ((paramArrayOfPathDataNode1[b]).mType == (paramArrayOfPathDataNode2[b]).mType) {
          if ((paramArrayOfPathDataNode1[b]).mParams.length != (paramArrayOfPathDataNode2[b]).mParams.length)
            return false; 
          b++;
          continue;
        } 
        return false;
      } 
      return true;
    } 
    return false;
  }
  
  static float[] copyOfRange(float[] paramArrayOfFloat, int paramInt1, int paramInt2) {
    if (paramInt1 > paramInt2)
      throw new IllegalArgumentException(); 
    int i = paramArrayOfFloat.length;
    if (paramInt1 < 0 || paramInt1 > i)
      throw new ArrayIndexOutOfBoundsException(); 
    paramInt2 -= paramInt1;
    i = Math.min(paramInt2, i - paramInt1);
    float[] arrayOfFloat = new float[paramInt2];
    System.arraycopy(paramArrayOfFloat, paramInt1, arrayOfFloat, 0, i);
    return arrayOfFloat;
  }
  
  public static PathDataNode[] createNodesFromPathData(String paramString) {
    if (paramString == null)
      return null; 
    ArrayList arrayList = new ArrayList();
    int j = 1;
    int i = 0;
    while (j < paramString.length()) {
      j = nextStart(paramString, j);
      String str = paramString.substring(i, j).trim();
      if (str.length() > 0) {
        float[] arrayOfFloat = getFloats(str);
        addNode(arrayList, str.charAt(0), arrayOfFloat);
      } 
      i = j;
      j++;
    } 
    if (j - i == 1 && i < paramString.length())
      addNode(arrayList, paramString.charAt(i), new float[0]); 
    return (PathDataNode[])arrayList.toArray(new PathDataNode[arrayList.size()]);
  }
  
  public static Path createPathFromPathData(String paramString) {
    path = new Path();
    PathDataNode[] arrayOfPathDataNode = createNodesFromPathData(paramString);
    if (arrayOfPathDataNode != null)
      try {
        PathDataNode.nodesToPath(arrayOfPathDataNode, path);
        return path;
      } catch (RuntimeException path) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Error in parsing ");
        stringBuilder.append(paramString);
        throw new RuntimeException(stringBuilder.toString(), path);
      }  
    return null;
  }
  
  public static PathDataNode[] deepCopyNodes(PathDataNode[] paramArrayOfPathDataNode) {
    if (paramArrayOfPathDataNode == null)
      return null; 
    PathDataNode[] arrayOfPathDataNode = new PathDataNode[paramArrayOfPathDataNode.length];
    for (byte b = 0; b < paramArrayOfPathDataNode.length; b++)
      arrayOfPathDataNode[b] = new PathDataNode(paramArrayOfPathDataNode[b]); 
    return arrayOfPathDataNode;
  }
  
  private static void extract(String paramString, int paramInt, ExtractFloatResult paramExtractFloatResult) {
    paramExtractFloatResult.mEndWithNegOrDot = false;
    int i = paramInt;
    boolean bool1 = false;
    boolean bool3 = false;
    boolean bool2 = false;
    while (i < paramString.length()) {
      char c = paramString.charAt(i);
      if (c != ' ') {
        if (c != 'E' && c != 'e') {
          switch (c) {
            default:
              bool1 = false;
              break;
            case '.':
              if (!bool3) {
                bool1 = false;
                bool3 = true;
                break;
              } 
              paramExtractFloatResult.mEndWithNegOrDot = true;
            case '-':
            
            case ',':
              bool1 = false;
              bool2 = true;
              break;
          } 
        } else {
          bool1 = true;
        } 
        if (bool2)
          break; 
        continue;
      } 
      i++;
    } 
    paramExtractFloatResult.mEndPosition = i;
  }
  
  private static float[] getFloats(String paramString) {
    if (paramString.charAt(0) == 'z' || paramString.charAt(0) == 'Z')
      return new float[0]; 
    try {
      float[] arrayOfFloat = new float[paramString.length()];
      ExtractFloatResult extractFloatResult = new ExtractFloatResult();
      int j = paramString.length();
      int i = 1;
      for (byte b = 0;; b = b1) {
        int k;
        byte b1;
        if (i < j) {
          extract(paramString, i, extractFloatResult);
          k = extractFloatResult.mEndPosition;
          b1 = b;
          if (i < k) {
            arrayOfFloat[b] = Float.parseFloat(paramString.substring(i, k));
            b1 = b + true;
          } 
          if (extractFloatResult.mEndWithNegOrDot) {
            i = k;
            b = b1;
            continue;
          } 
        } else {
          return copyOfRange(arrayOfFloat, 0, b);
        } 
        i = k + 1;
      } 
    } catch (NumberFormatException numberFormatException) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("error in parsing \"");
      stringBuilder.append(paramString);
      stringBuilder.append("\"");
      throw new RuntimeException(stringBuilder.toString(), numberFormatException);
    } 
  }
  
  private static int nextStart(String paramString, int paramInt) {
    while (paramInt < paramString.length()) {
      char c = paramString.charAt(paramInt);
      if (((c - 'A') * (c - 'Z') <= '\000' || (c - 'a') * (c - 'z') <= '\000') && c != 'e' && c != 'E')
        return paramInt; 
      paramInt++;
    } 
    return paramInt;
  }
  
  public static void updateNodes(PathDataNode[] paramArrayOfPathDataNode1, PathDataNode[] paramArrayOfPathDataNode2) {
    for (byte b = 0; b < paramArrayOfPathDataNode2.length; b++) {
      (paramArrayOfPathDataNode1[b]).mType = (paramArrayOfPathDataNode2[b]).mType;
      for (byte b1 = 0; b1 < (paramArrayOfPathDataNode2[b]).mParams.length; b1++)
        (paramArrayOfPathDataNode1[b]).mParams[b1] = (paramArrayOfPathDataNode2[b]).mParams[b1]; 
    } 
  }
  
  private static class ExtractFloatResult {
    int mEndPosition;
    
    boolean mEndWithNegOrDot;
  }
  
  public static class PathDataNode {
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public float[] mParams;
    
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public char mType;
    
    PathDataNode(char param1Char, float[] param1ArrayOfFloat) {
      this.mType = param1Char;
      this.mParams = param1ArrayOfFloat;
    }
    
    PathDataNode(PathDataNode param1PathDataNode) {
      this.mType = param1PathDataNode.mType;
      this.mParams = PathParser.copyOfRange(param1PathDataNode.mParams, 0, param1PathDataNode.mParams.length);
    }
    
    private static void addCommand(Path param1Path, float[] param1ArrayOfFloat1, char param1Char1, char param1Char2, float[] param1ArrayOfFloat2) {
      char c1;
      float f7 = param1ArrayOfFloat1[0];
      float f8 = param1ArrayOfFloat1[1];
      float f9 = param1ArrayOfFloat1[2];
      float f10 = param1ArrayOfFloat1[3];
      float f6 = param1ArrayOfFloat1[4];
      float f5 = param1ArrayOfFloat1[5];
      float f1 = f7;
      float f2 = f8;
      float f3 = f9;
      float f4 = f10;
      switch (param1Char2) {
        default:
          f4 = f10;
          f3 = f9;
          f2 = f8;
          f1 = f7;
        case 'L':
        case 'M':
        case 'T':
        case 'l':
        case 'm':
        case 't':
          c1 = '\002';
          break;
        case 'Z':
        case 'z':
          param1Path.close();
          param1Path.moveTo(f6, f5);
          f1 = f6;
          f3 = f1;
          f2 = f5;
          f4 = f2;
        case 'Q':
        case 'S':
        case 'q':
        case 's':
          c1 = '\004';
          f1 = f7;
          f2 = f8;
          f3 = f9;
          f4 = f10;
          break;
        case 'H':
        case 'V':
        case 'h':
        case 'v':
          c1 = '\001';
          f1 = f7;
          f2 = f8;
          f3 = f9;
          f4 = f10;
          break;
        case 'C':
        case 'c':
          c1 = '\006';
          f1 = f7;
          f2 = f8;
          f3 = f9;
          f4 = f10;
          break;
        case 'A':
        case 'a':
          c1 = '\007';
          f4 = f10;
          f3 = f9;
          f2 = f8;
          f1 = f7;
          break;
      } 
      f7 = f2;
      char c = Character.MIN_VALUE;
      char c2 = param1Char1;
      param1Char1 = c;
      f2 = f1;
      f1 = f7;
      while (param1Char1 < param1ArrayOfFloat2.length) {
        boolean bool2;
        boolean bool1;
        char c4;
        char c3;
        f8 = 0.0F;
        f7 = 0.0F;
        switch (param1Char2) {
          case 'v':
            c2 = param1Char1 + Character.MIN_VALUE;
            param1Path.rLineTo(0.0F, param1ArrayOfFloat2[c2]);
            f1 += param1ArrayOfFloat2[c2];
            break;
          case 't':
            if (c2 == 'q' || c2 == 't' || c2 == 'Q' || c2 == 'T') {
              f3 = f2 - f3;
              f4 = f1 - f4;
            } else {
              f4 = 0.0F;
              f3 = f7;
            } 
            c2 = param1Char1 + Character.MIN_VALUE;
            f7 = param1ArrayOfFloat2[c2];
            c = param1Char1 + '\001';
            param1Path.rQuadTo(f3, f4, f7, param1ArrayOfFloat2[c]);
            f7 = f2 + param1ArrayOfFloat2[c2];
            f8 = f1 + param1ArrayOfFloat2[c];
            f4 += f1;
            f3 += f2;
            f1 = f8;
            f2 = f7;
            break;
          case 's':
            if (c2 == 'c' || c2 == 's' || c2 == 'C' || c2 == 'S') {
              f4 = f1 - f4;
              f3 = f2 - f3;
            } else {
              f4 = 0.0F;
              f3 = f8;
            } 
            c2 = param1Char1 + Character.MIN_VALUE;
            f7 = param1ArrayOfFloat2[c2];
            c = param1Char1 + '\001';
            f8 = param1ArrayOfFloat2[c];
            c3 = param1Char1 + '\002';
            f9 = param1ArrayOfFloat2[c3];
            c4 = param1Char1 + '\003';
            param1Path.rCubicTo(f3, f4, f7, f8, f9, param1ArrayOfFloat2[c4]);
            f3 = param1ArrayOfFloat2[c2] + f2;
            f4 = param1ArrayOfFloat2[c] + f1;
            f2 += param1ArrayOfFloat2[c3];
            f1 += param1ArrayOfFloat2[c4];
            break;
          case 'q':
            c2 = param1Char1 + Character.MIN_VALUE;
            f3 = param1ArrayOfFloat2[c2];
            c = param1Char1 + '\001';
            f4 = param1ArrayOfFloat2[c];
            c3 = param1Char1 + '\002';
            f7 = param1ArrayOfFloat2[c3];
            c4 = param1Char1 + '\003';
            param1Path.rQuadTo(f3, f4, f7, param1ArrayOfFloat2[c4]);
            f3 = param1ArrayOfFloat2[c2] + f2;
            f4 = param1ArrayOfFloat2[c] + f1;
            f2 += param1ArrayOfFloat2[c3];
            f1 += param1ArrayOfFloat2[c4];
            break;
          case 'm':
            c2 = param1Char1 + Character.MIN_VALUE;
            f2 += param1ArrayOfFloat2[c2];
            c = param1Char1 + '\001';
            f1 += param1ArrayOfFloat2[c];
            if (param1Char1 > '\000') {
              param1Path.rLineTo(param1ArrayOfFloat2[c2], param1ArrayOfFloat2[c]);
              break;
            } 
            param1Path.rMoveTo(param1ArrayOfFloat2[c2], param1ArrayOfFloat2[c]);
            f5 = f1;
            f6 = f2;
            break;
          case 'l':
            c2 = param1Char1 + Character.MIN_VALUE;
            f7 = param1ArrayOfFloat2[c2];
            c = param1Char1 + '\001';
            param1Path.rLineTo(f7, param1ArrayOfFloat2[c]);
            f2 += param1ArrayOfFloat2[c2];
            f1 += param1ArrayOfFloat2[c];
            break;
          case 'h':
            c2 = param1Char1 + Character.MIN_VALUE;
            param1Path.rLineTo(param1ArrayOfFloat2[c2], 0.0F);
            f2 += param1ArrayOfFloat2[c2];
            break;
          case 'c':
            f3 = param1ArrayOfFloat2[param1Char1 + Character.MIN_VALUE];
            f4 = param1ArrayOfFloat2[param1Char1 + '\001'];
            c2 = param1Char1 + '\002';
            f7 = param1ArrayOfFloat2[c2];
            c = param1Char1 + '\003';
            f8 = param1ArrayOfFloat2[c];
            c3 = param1Char1 + '\004';
            f9 = param1ArrayOfFloat2[c3];
            c4 = param1Char1 + '\005';
            param1Path.rCubicTo(f3, f4, f7, f8, f9, param1ArrayOfFloat2[c4]);
            f3 = param1ArrayOfFloat2[c2] + f2;
            f4 = param1ArrayOfFloat2[c] + f1;
            f2 += param1ArrayOfFloat2[c3];
            f1 += param1ArrayOfFloat2[c4];
            break;
          case 'a':
            c2 = param1Char1 + '\005';
            f3 = param1ArrayOfFloat2[c2];
            c = param1Char1 + '\006';
            f4 = param1ArrayOfFloat2[c];
            f7 = param1ArrayOfFloat2[param1Char1 + Character.MIN_VALUE];
            f8 = param1ArrayOfFloat2[param1Char1 + '\001'];
            f9 = param1ArrayOfFloat2[param1Char1 + '\002'];
            if (param1ArrayOfFloat2[param1Char1 + '\003'] != 0.0F) {
              bool1 = true;
            } else {
              bool1 = false;
            } 
            if (param1ArrayOfFloat2[param1Char1 + '\004'] != 0.0F) {
              bool2 = true;
            } else {
              bool2 = false;
            } 
            drawArc(param1Path, f2, f1, f3 + f2, f4 + f1, f7, f8, f9, bool1, bool2);
            f2 += param1ArrayOfFloat2[c2];
            f1 += param1ArrayOfFloat2[c];
            f4 = f1;
            f3 = f2;
            break;
          case 'V':
            c2 = param1Char1 + Character.MIN_VALUE;
            param1Path.lineTo(f2, param1ArrayOfFloat2[c2]);
            f1 = param1ArrayOfFloat2[c2];
            break;
          case 'T':
            f7 = f1;
            f8 = f2;
            c = param1Char1;
          case 'S':
            c = param1Char1;
            if (c2 == 'c' || c2 == 's' || c2 == 'C' || c2 == 'S') {
              f1 = f1 * 2.0F - f4;
              f3 = f2 * 2.0F - f3;
              f2 = f1;
              f1 = f3;
            } else {
              f3 = f1;
              f1 = f2;
              f2 = f3;
            } 
            c2 = c + Character.MIN_VALUE;
            f3 = param1ArrayOfFloat2[c2];
            c3 = c + '\001';
            f4 = param1ArrayOfFloat2[c3];
            c4 = c + '\002';
            f7 = param1ArrayOfFloat2[c4];
            c += '\003';
            param1Path.cubicTo(f1, f2, f3, f4, f7, param1ArrayOfFloat2[c]);
            f4 = param1ArrayOfFloat2[c2];
            f3 = param1ArrayOfFloat2[c3];
            f2 = param1ArrayOfFloat2[c4];
            f1 = param1ArrayOfFloat2[c];
            f7 = f3;
            f3 = f4;
            f4 = f7;
            break;
          case 'Q':
            c2 = param1Char1;
            c = c2 + Character.MIN_VALUE;
            f1 = param1ArrayOfFloat2[c];
            c3 = c2 + '\001';
            f2 = param1ArrayOfFloat2[c3];
            c4 = c2 + '\002';
            f3 = param1ArrayOfFloat2[c4];
            c2 += '\003';
            param1Path.quadTo(f1, f2, f3, param1ArrayOfFloat2[c2]);
            f4 = param1ArrayOfFloat2[c];
            f3 = param1ArrayOfFloat2[c3];
            f2 = param1ArrayOfFloat2[c4];
            f1 = param1ArrayOfFloat2[c2];
            f7 = f3;
            f3 = f4;
            f4 = f7;
            break;
          case 'M':
            c2 = param1Char1;
            c = c2 + Character.MIN_VALUE;
            f2 = param1ArrayOfFloat2[c];
            c3 = c2 + '\001';
            f1 = param1ArrayOfFloat2[c3];
            if (c2 > '\000') {
              param1Path.lineTo(param1ArrayOfFloat2[c], param1ArrayOfFloat2[c3]);
              break;
            } 
            param1Path.moveTo(param1ArrayOfFloat2[c], param1ArrayOfFloat2[c3]);
            f5 = f1;
            f6 = f2;
            break;
          case 'L':
            c2 = param1Char1;
            c = c2 + Character.MIN_VALUE;
            f1 = param1ArrayOfFloat2[c];
            param1Path.lineTo(f1, param1ArrayOfFloat2[++c2]);
            f2 = param1ArrayOfFloat2[c];
            f1 = param1ArrayOfFloat2[c2];
            break;
          case 'H':
            c2 = param1Char1 + Character.MIN_VALUE;
            param1Path.lineTo(param1ArrayOfFloat2[c2], f1);
            f2 = param1ArrayOfFloat2[c2];
            break;
          case 'C':
            c2 = param1Char1;
            f1 = param1ArrayOfFloat2[c2 + Character.MIN_VALUE];
            f2 = param1ArrayOfFloat2[c2 + '\001'];
            c = c2 + '\002';
            f3 = param1ArrayOfFloat2[c];
            c3 = c2 + '\003';
            f4 = param1ArrayOfFloat2[c3];
            c4 = c2 + '\004';
            f7 = param1ArrayOfFloat2[c4];
            c2 += '\005';
            param1Path.cubicTo(f1, f2, f3, f4, f7, param1ArrayOfFloat2[c2]);
            f2 = param1ArrayOfFloat2[c4];
            f1 = param1ArrayOfFloat2[c2];
            f3 = param1ArrayOfFloat2[c];
            f4 = param1ArrayOfFloat2[c3];
            break;
          case 'A':
            c2 = param1Char1;
            c = c2 + '\005';
            f3 = param1ArrayOfFloat2[c];
            c3 = c2 + '\006';
            f4 = param1ArrayOfFloat2[c3];
            f7 = param1ArrayOfFloat2[c2 + Character.MIN_VALUE];
            f8 = param1ArrayOfFloat2[c2 + '\001'];
            f9 = param1ArrayOfFloat2[c2 + '\002'];
            if (param1ArrayOfFloat2[c2 + '\003'] != 0.0F) {
              bool1 = true;
            } else {
              bool1 = false;
            } 
            if (param1ArrayOfFloat2[c2 + '\004'] != 0.0F) {
              bool2 = true;
            } else {
              bool2 = false;
            } 
            drawArc(param1Path, f2, f1, f3, f4, f7, f8, f9, bool1, bool2);
            f2 = param1ArrayOfFloat2[c];
            f1 = param1ArrayOfFloat2[c3];
            f4 = f1;
            f3 = f2;
            break;
        } 
        continue;
        param1Char1 += c1;
        c2 = param1Char2;
      } 
      param1ArrayOfFloat1[0] = f2;
      param1ArrayOfFloat1[1] = f1;
      param1ArrayOfFloat1[2] = f3;
      param1ArrayOfFloat1[3] = f4;
      param1ArrayOfFloat1[4] = f6;
      param1ArrayOfFloat1[5] = f5;
    }
    
    private static void arcToBezier(Path param1Path, double param1Double1, double param1Double2, double param1Double3, double param1Double4, double param1Double5, double param1Double6, double param1Double7, double param1Double8, double param1Double9) {
      int i = (int)Math.ceil(Math.abs(param1Double9 * 4.0D / Math.PI));
      double d5 = Math.cos(param1Double7);
      double d7 = Math.sin(param1Double7);
      double d1 = Math.cos(param1Double8);
      double d2 = Math.sin(param1Double8);
      param1Double7 = -param1Double3;
      double d9 = param1Double7 * d5;
      double d10 = param1Double4 * d7;
      param1Double7 *= d7;
      double d6 = param1Double4 * d5;
      double d8 = param1Double9 / i;
      byte b = 0;
      double d3 = d2 * param1Double7 + d1 * d6;
      d1 = d9 * d2 - d10 * d1;
      d2 = param1Double6;
      param1Double9 = param1Double5;
      double d4 = param1Double8;
      param1Double4 = d7;
      param1Double5 = d5;
      param1Double8 = param1Double7;
      param1Double7 = d8;
      param1Double6 = d6;
      while (true) {
        d6 = param1Double3;
        if (b < i) {
          d5 = d4 + param1Double7;
          d8 = Math.sin(d5);
          double d12 = Math.cos(d5);
          d7 = param1Double1 + d6 * param1Double5 * d12 - d10 * d8;
          double d11 = param1Double2 + d6 * param1Double4 * d12 + param1Double6 * d8;
          d6 = d9 * d8 - d10 * d12;
          d8 = d8 * param1Double8 + d12 * param1Double6;
          d4 = d5 - d4;
          d12 = Math.tan(d4 / 2.0D);
          d4 = Math.sin(d4) * (Math.sqrt(d12 * 3.0D * d12 + 4.0D) - 1.0D) / 3.0D;
          param1Path.rLineTo(0.0F, 0.0F);
          param1Path.cubicTo((float)(param1Double9 + d1 * d4), (float)(d2 + d3 * d4), (float)(d7 - d4 * d6), (float)(d11 - d4 * d8), (float)d7, (float)d11);
          b++;
          d2 = d11;
          param1Double9 = d7;
          d3 = d8;
          d1 = d6;
          d4 = d5;
          continue;
        } 
        break;
      } 
    }
    
    private static void drawArc(Path param1Path, float param1Float1, float param1Float2, float param1Float3, float param1Float4, float param1Float5, float param1Float6, float param1Float7, boolean param1Boolean1, boolean param1Boolean2) {
      double d5 = Math.toRadians(param1Float7);
      double d6 = Math.cos(d5);
      double d7 = Math.sin(d5);
      double d8 = param1Float1;
      double d9 = param1Float2;
      double d10 = param1Float5;
      double d1 = (d8 * d6 + d9 * d7) / d10;
      double d2 = -param1Float1;
      double d11 = param1Float6;
      double d4 = (d2 * d7 + d9 * d6) / d11;
      double d3 = param1Float3;
      d2 = param1Float4;
      double d12 = (d3 * d6 + d2 * d7) / d10;
      double d13 = (-param1Float3 * d7 + d2 * d6) / d11;
      double d15 = d1 - d12;
      double d14 = d4 - d13;
      d3 = (d1 + d12) / 2.0D;
      d2 = (d4 + d13) / 2.0D;
      double d16 = d15 * d15 + d14 * d14;
      if (d16 == 0.0D) {
        Log.w("PathParser", " Points are coincident");
        return;
      } 
      double d17 = 1.0D / d16 - 0.25D;
      if (d17 < 0.0D) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Points are too far apart ");
        stringBuilder.append(d16);
        Log.w("PathParser", stringBuilder.toString());
        float f = (float)(Math.sqrt(d16) / 1.99999D);
        drawArc(param1Path, param1Float1, param1Float2, param1Float3, param1Float4, param1Float5 * f, param1Float6 * f, param1Float7, param1Boolean1, param1Boolean2);
        return;
      } 
      d16 = Math.sqrt(d17);
      d15 *= d16;
      d14 = d16 * d14;
      if (param1Boolean1 == param1Boolean2) {
        d3 -= d14;
        d2 += d15;
      } else {
        d3 += d14;
        d2 -= d15;
      } 
      d14 = Math.atan2(d4 - d2, d1 - d3);
      d4 = Math.atan2(d13 - d2, d12 - d3) - d14;
      if (d4 >= 0.0D) {
        param1Boolean1 = true;
      } else {
        param1Boolean1 = false;
      } 
      d1 = d4;
      if (param1Boolean2 != param1Boolean1)
        if (d4 > 0.0D) {
          d1 = d4 - 6.283185307179586D;
        } else {
          d1 = d4 + 6.283185307179586D;
        }  
      d3 *= d10;
      d2 *= d11;
      arcToBezier(param1Path, d3 * d6 - d2 * d7, d3 * d7 + d2 * d6, d10, d11, d8, d9, d5, d14, d1);
    }
    
    public static void nodesToPath(PathDataNode[] param1ArrayOfPathDataNode, Path param1Path) {
      float[] arrayOfFloat = new float[6];
      char c = 'm';
      for (byte b = 0; b < param1ArrayOfPathDataNode.length; b++) {
        addCommand(param1Path, arrayOfFloat, c, (param1ArrayOfPathDataNode[b]).mType, (param1ArrayOfPathDataNode[b]).mParams);
        c = (param1ArrayOfPathDataNode[b]).mType;
      } 
    }
    
    public void interpolatePathDataNode(PathDataNode param1PathDataNode1, PathDataNode param1PathDataNode2, float param1Float) {
      byte b;
      for (b = 0; b < param1PathDataNode1.mParams.length; b++)
        this.mParams[b] = param1PathDataNode1.mParams[b] * (1.0F - param1Float) + param1PathDataNode2.mParams[b] * param1Float; 
    }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/support/v4/graphics/PathParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */