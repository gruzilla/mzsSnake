package snake.util;

import java.io.*;
import java.security.*;

/**
 * Utility class that provides methods to create a hash for a list of files.
 * @author Thomas Scheller, Markus Karolus
 */

public class Hash
{
  /**
   * Use MessageDigest class to create a hash from the list of files.
   * @param fileList list of files
   * @param algo name of the hash algorithm
   * @return hashcode as a byte array
   * @throws Exception
   */
  private static byte[] messageDigest(String[] fileList, String algo) throws Exception
  {
    MessageDigest messagedigest = MessageDigest.getInstance(algo);
    byte md[] = new byte[8192];

    for (int i = 0; i < fileList.length; i++)
    {
      FileInputStream in = new FileInputStream(fileList[i]);
      int len = 1;
      while (len > 0)
      {
        len = in.read(md, 0, md.length);
        if (len > 0)
        {
          messagedigest.update(md, 0, len);
        }
      }
      in.close();
    }
    return messagedigest.digest();
  }

  /**
   * Convert a byte array into a hex string.
   * @param bytes byte[]
   * @return String
   */
  public static String toHexString(byte bytes[])
  {
    StringBuffer retString = new StringBuffer();
    for (int i = 0; i < bytes.length; ++i)
    {
      retString.append(
          Integer.toHexString(0x0100 + (bytes[i] & 0x00FF)).substring(1));
    }
    return retString.toString();
  }

  /**
   * Convert a hex string into a byte array.
   * @param hexString String
   * @return byte[]
   */
  public static byte[] toBinaryArray(String hexString)
  {
    byte[] newBuffer = new byte[hexString.length() / 2];
    for (int i = 0; i < newBuffer.length; i++)
    {
      newBuffer[i] = (byte) Integer.parseInt(hexString.substring(2 * i, 2 * i + 2), 16);
    }
    return newBuffer;
  }

  /**
   * Make MD5 hash from a list of files.
   * @param fileList list of files
   * @return hashcode as a string
   * @throws Exception
   */
  public static String getDigestStr(String[] fileList) throws Exception
  {
    byte[] digest = messageDigest(fileList, "MD5");
    String result = toHexString(digest);
    return result;
  }

  /**
   * Make MD5 hash from a list of files.
   * @param fileList list of files
   * @return hashcode as a byte array
   * @throws Exception
   */
  public static byte[] getDigest(String[] fileList) throws Exception
  {
    byte[] digest = messageDigest(fileList, "MD5");
    return digest;
  }
}
