package com.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by fby on 2015/9/16.
 */
public class AES {
    private static final String KEY = "eastsoft updata ";
    /**
     * 加密
     * @param content  需要加密的内容
     * @param password 加密密码
     * @return
     */
    public static byte[] encrypt2(byte[] password, byte[] content) {
        try {
            SecretKeySpec key = new SecretKeySpec(password, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");

            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(content);
            return result; // 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * Convert hex string to byte[]
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * Convert char to byte
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
    /**
     * 生成4字节16进制随机数
     * Convert char to byte
     *
     * @return String
     */
    public static String getIVCloud() {
        int randomNum = (int) (Math.random() * 2147483647);
        String hexNum = Integer.toHexString(randomNum).toUpperCase();
        char[] aa = hexNum.toCharArray();
        int count = 8 - aa.length;
        String zeroSum = "";
        for (int i = 0; i < count; i++) {
            zeroSum = zeroSum + "0";
        }
        String result = hexNum + zeroSum;
        return result;
    }

    /**
     * 获得encryptedDATA和MIC
     * @param IV1664
     * @param IVCloud
     * @param data
     * @return data +mic
     */
    public static byte[] getDataAndMic(String IV1664, String IVCloud, byte[] data) throws UnsupportedEncodingException {
        String IVIndex = IV1664 + IVCloud;
        byte[] TEMP = encrypt2(KEY.getBytes("UTF-8"), "ES updata IVtemp".getBytes("UTF-8"));
        byte[] IVZero16 = encrypt2(TEMP, "ES updata IVzero".getBytes("UTF-8"));
        String ivzero16 = parseByte2HexStr(IVZero16);
        byte[] IVZero8 = new byte[8];
        System.arraycopy(IVZero16, 0, IVZero8, 0, 8);
        String IVZero = parseByte2HexStr(IVZero8);
        System.out.println("Ivzero="+IVZero);
        byte[] IV = encrypt2(KEY.getBytes("UTF-8"), hexStringToBytes(IVZero + IVIndex));
        byte[] Block1 = data;
        byte[] B1 = new byte[16];
        if (data.length > 16) {
            int length;
            if (Block1.length % 16 == 0) {
                length = Block1.length / 16;
            } else {
                length = Block1.length / 16 + 1;
            }
            for (int i = 0; i < length; i++) {
                byte[] tmp = new byte[16];
                for (int j = 0; j < 16; j++) {
                    if (j + i * 16 >= Block1.length) {
                        break;
                    } else {
                        tmp[j] = data[j + i * 16];
                    }
                }
                if (i == 0) {
                    for (int k = 0; k < 16; k++) {
                        B1[k] = tmp[k];
                    }
                } else {
                    for (int k = 0; k < 16; k++) {
                        B1[k] = (byte) (B1[k] ^ tmp[k]);
                    }
                }
            }
        }
        String nons = parseByte2HexStr(IV);
        String non = nons.substring(0, 26);
        String BO = "09" + non + "0010";
        byte[] bo = hexStringToBytes(BO);
        byte[] XO = encrypt2(KEY.getBytes("UTF-8"), bo);
        byte[] B1XO = new byte[16];
        for (int i = 0; i <= 15; i++) {
            B1XO[i] = (byte) (XO[i] ^ B1[i]);
        }
        byte[] X1 = encrypt2(KEY.getBytes("UTF-8"), B1XO);
        String nonceCMIC = "01" + non + "0000";
        byte[] CMIC = encrypt2(KEY.getBytes("UTF-8"), hexStringToBytes(nonceCMIC));
        String nonceCBlock1 = "01" + non + "0001";
        byte[] CBlock1 = encrypt2(KEY.getBytes("UTF-8"), hexStringToBytes(nonceCBlock1));
        byte[] encryptedDATA = new byte[data.length];
        if (data.length > 16) {
            int length;
            if (Block1.length % 16 == 0) {
                length = Block1.length / 16;
            } else {
                length = Block1.length / 16 + 1;
            }
            for (int i = 0; i <= length; i++) {
                for (int j = 0; j < 16; j++) {
                    if (j + i * 16 >= Block1.length) {
                        break;
                    }
                    encryptedDATA[j + i * 16] = (byte) (data[j + i * 16] ^ CBlock1[j]);
                }
            }
        } else {
            for (int i = 0; i < Block1.length; i++) {
                encryptedDATA[i] = (byte) (data[i] ^ CBlock1[i]);
            }
        }
        byte[] MIC = new byte[4];
        for (int i = 0; i < 4; i++) {
            MIC[i] = (byte) (CMIC[i] ^ X1[i]);
        }
        byte[] result = new byte[encryptedDATA.length + MIC.length];
        System.arraycopy(encryptedDATA, 0, result, 0, encryptedDATA.length);
        System.arraycopy(MIC, 0, result, encryptedDATA.length, MIC.length);
        return result;
    }
}
