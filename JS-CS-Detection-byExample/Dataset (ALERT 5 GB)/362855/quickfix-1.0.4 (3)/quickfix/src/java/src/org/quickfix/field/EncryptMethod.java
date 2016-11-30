package org.quickfix.field; 
import org.quickfix.IntField; 
import java.util.Date; 

public class EncryptMethod extends IntField 
{ 
public static final char NONE_OTHER = '0'; 
public static final char PKCS = '1'; 
public static final char DES = '2'; 
public static final char PKCSDES = '3'; 
public static final char PGPDES = '4'; 
public static final char PGPDESMD5 = '5'; 
public static final char PEMDESMD5 = '6'; 

  public EncryptMethod() 
  { 
    super(98);
  } 
  public EncryptMethod(int data) 
  { 
    super(98, data);
  } 
} 
