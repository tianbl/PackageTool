package com.util;

import java.io.*;

public class ToolUtil {
    public static boolean isFileExist(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    public static boolean deleteFileByFilename(String filename) {
        File file = new File(filename);
        boolean isdeltet = false;
        if (!file.exists()) {
            return false;
        }
        if (file.isFile()) {
            return file.delete();
        }
        return isdeltet;
    }

    public static void copyFile(String original, String target) throws Exception {
        FileInputStream in = new FileInputStream(new File(original));
        FileOutputStream out = new FileOutputStream(new File(target));
        byte[] buff = new byte[512];
        int n = 0;
        while ((n = in.read(buff)) != -1) {
            out.write(buff, 0, n);
        }
        out.flush();
        in.close();
        out.close();
    }

    @SuppressWarnings("resource")
    public static String fileToHexString(int gid) throws IOException {
        String outPath = "out";
        String filepath = outPath + "/" + gid + ".jks";
        File file = new File(filepath);
        DataInputStream din = new DataInputStream(new FileInputStream(file));
        StringBuilder hexData = new StringBuilder();
        byte temp = 0;
        for (int i = 0; i < file.length(); i++) {
            temp = din.readByte();
            hexData.append(String.format("%02X", temp));
        }
        return hexData.toString();
    }

    public static String calculateTime(String fileName) {
        String dir = System.getProperty("user.dir");
        dir = dir + "\\" + fileName;
        File zip = new File(dir);
        if (zip.exists()) {
            long l = zip.length();
            long time = (l / 1024 / 400) * 3 / 2 + 5;
            return Long.toString(time);
        } else {
            return null;
        }
    }

    public static String bytes2HexString(byte[] b) {
        if (b == null) {
            return null;
        }
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    public static byte[] hexString2ByteArray(String str) {
        str = str.replaceAll(" ", "");
        byte[] result = new byte[str.length() / 2];
        for (int i = 0, j = 0; j < str.length(); i++) {
            result[i] = (byte) Integer.parseInt(str.substring(j, j + 2), 16);
            j += 2;
        }
        return result;
    }

    public static byte[] int2Byte(int i) {
        byte[] result = new byte[2];
        result[0] = (byte) ((i >> 8) & 0xFF);
        result[1] = (byte) (i & 0xFF);
        return result;
    }

    public static byte makeCs(byte b[]) {
        int cs = 0;
        for (int i = 0; i < b.length; i++) {
            cs += b[i] & 0xFF;
        }
        return (byte) cs;
    }

    /**
     * ��ȡtxt�ļ�������
     *
     * @param file ��Ҫ��ȡ���ļ�����
     * @return �����ļ�����
     */
    public static String txt2String(String fileName) {
        String result = "";
        try {
            //BufferedReader br = new BufferedReader(new FileReader(file));//����һ��BufferedReader������ȡ�ļ�
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "GBK"));
            String s = null;
            while ((s = br.readLine()) != null) {//ʹ��readLine������һ�ζ�һ��
                result = result + "\n" + s;
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static boolean writeFile(String content, String fileName) {
        FileWriter fw;
        if (content == null || "".endsWith(content)) {
            System.out.println("---------------------------------------------Content is null");
            return false;
        }
        try {
            fw = new FileWriter(fileName);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.flush();
            bw.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getNowPath() {
        File directory = new File(".");
        try {
            return directory.getCanonicalPath();
        } catch (Exception exp) {
            exp.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        writeFile("asdfasdf", "route_ver.ini");
    }
}
