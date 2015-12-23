package com.business;

import com.CRC;
import com.ui.MainFrame;
import com.ui.PackageMerge;
import com.util.AES;
import com.util.ToolUtil;

import javax.swing.*;
import java.io.*;

/**
 * Created by baolei on 2015/12/21.
 */
public class Merge {

    private PackageMerge packageMerge;

    private String version30;
    private String version47;
    private String versionTarget;
    private String deviceType;
    private int length30;
    private int length47;


    public Merge() {
        packageMerge = PackageMerge.getInstance();
        version30 = packageMerge.getVersionOf30();
        version47 = packageMerge.getVersionOf47();
        versionTarget = packageMerge.getVersionOfTarget();
        deviceType = packageMerge.getDeviceType();
    }

    public boolean mergeUpdateFile(String absolutePath30, String absolutePath47) {
        MainFrame.showMssageln("合并ES30LE和ES47LE升级包...");
        String targetVersion = packageMerge.getVersionOfTarget();
        if (null == targetVersion || "".equals(targetVersion)) {
            int i = JOptionPane.showConfirmDialog(MainFrame.getInstance(),
                    "合并后的包版本号不能为空？", "提示", JOptionPane.DEFAULT_OPTION);
            return false;
        }

        //计算附件信息
        byte[] extraMsg = getExtraMessage(absolutePath30, absolutePath47);
        if (null == extraMsg) {
            return false;
        }
        //MainFrame.showMssageln("计算得到128字节附加信息:\n"+AES.parseByte2HexStr(extraMsg));

        String targetPath = ToolUtil.getNowPath() + "\\"+System.currentTimeMillis()+".bin";
        try {
            MainFrame.showMssageln("生成中间文件:" + targetPath);
            byte[] btOut = new byte[128];
            int fileSize = 128;
            File file = new File(targetPath);
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
            outputStream.write(extraMsg);
            String[] filePaths = {absolutePath47, absolutePath30};
            for (String filePath : filePaths) {
                InputStream inputStream = new FileInputStream(new File(filePath));
                while (inputStream.available() > 0) {
                    fileSize += 128;
                    if (inputStream.available() < 128) {
                        int len = inputStream.available();
                        inputStream.read(btOut);
                        for (; len < btOut.length; len++) {
                            btOut[len] = (byte) 0xff;
                        }
                        outputStream.write(btOut);
                    } else {
                        inputStream.read(btOut);
                        outputStream.write(btOut);
                    }
                }
                inputStream.close();
            }
            MainFrame.showMssage("\n");
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            int i = JOptionPane.showConfirmDialog(MainFrame.getInstance(),
                    "程序运行异常!", "提示", JOptionPane.DEFAULT_OPTION);
            return false;
        }

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //生成最终合并包
        MainFrame.showMssageln("合并升级包...");
        byte[] outBytes = getPackageHeader(targetPath);
        if (outBytes == null) {
            MainFrame.showMssageln("生成信息头失败!");
            return false;
        }
        try {
            String path = ToolUtil.getNowPath() + "\\" + deviceType + ".bin";
            File file = new File(path);
            OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
            InputStream inputStream = new BufferedInputStream(new FileInputStream(new File(targetPath)));
            out.write(outBytes);
            outBytes = new byte[128];
            while (inputStream.available() > 0) {
                int available = inputStream.available();
                if (available < 128) {
                    inputStream.read(outBytes);
                    for (; available < outBytes.length; available++) {
                        outBytes[available] = (byte) 0xff;
                    }
                } else {
                    inputStream.read(outBytes);
                }
                out.write(outBytes);
            }
            inputStream.close();
            out.flush();
            out.close();
            MainFrame.showMssageln("升级包合并完成");
        } catch (Exception e) {
            e.printStackTrace();
            int i = JOptionPane.showConfirmDialog(MainFrame.getInstance(),
                    "程序运行异常!", "提示", JOptionPane.DEFAULT_OPTION);
            return false;
        }
        return true;
    }

    public byte[] getPackageHeader(String filePath) {
        byte[] header = null;
        if (null != versionTarget && !"".equals(versionTarget)) {
            int index = 0;
            int crc = calcFileCrc(filePath);
            int fileSize = fileByteCount(filePath);
            String[] supply = {"0","00","000","0000"};
            int supplyIndex=0;
            String crcStr=Integer.toHexString(crc);
            String fileSizeStr=Integer.toHexString(fileSize);
//            if(Integer.toHexString(crc).length()<4){
//                supplyIndex = 3-Integer.toHexString(crc).length();
//                crcStr = supply[supplyIndex]+Integer.toHexString(crc);
//            }
//            if(Integer.toHexString(fileSize).length()<4){
//                supplyIndex = 3-Integer.toHexString(fileSize).length();
//                fileSizeStr = supply[supplyIndex]+Integer.toHexString(fileSize);
//            }
            String[] strs = {"device type:" + deviceType,
                    "soft ver:" + versionTarget,
                    "file crc:" + crcStr,
                    "file size:" + fileSizeStr,
                    "program start:"};
            byte[][] tmpbt = new byte[strs.length][];
            int headerLen = 0;
            for (int i = 0; i < strs.length; i++) {
                tmpbt[i] = strs[i].getBytes();
                headerLen += tmpbt[i].length;
            }
            header = new byte[headerLen+tmpbt.length-1];
            index = 0;
            for (int i = 0; i < tmpbt.length; i++) {
                if(i>0){
                    header[index++] = '\0';
                }
                for (byte b : tmpbt[i]) {
                    header[index++] = b;
                }
            }

            MainFrame.showMssageln("生成包头信息:" + new String(header));
        }
        return header;
    }

    /**
     * 计算附加信息
     *
     * @param absolutePath30
     * @param absolutePath47
     * @return
     */
    public byte[] getExtraMessage(String absolutePath30, String absolutePath47) {
        byte[] extraMsg = new byte[128];
        int index = 0;
        String[] filePaths = {absolutePath47, absolutePath30};
        String[] version = {version47, version30};
        String[] name = {"ES47LE", " ES30LE"};
        String[] supply = {"","0","00","000","0000"};
        int supplyIndex=0;
        for (int i = 0; i < filePaths.length; i++) {
            if (null != version[i] && !"".equals(version[i])) {
                StringBuffer sb = new StringBuffer();
                //处理版本号
                byte[] bt = version[i].getBytes();
                for (byte b : bt) {
                    extraMsg[index++] = b;
                }
                sb.append(version[i]);

                //处理文件长度
                int len = fileByteCount(filePaths[i]);
                if (len % 128 != 0) {
                    len = 128 - (len % 128) + len;
                }
                if(Integer.toHexString(len).length()<4){
                    supplyIndex = 4-Integer.toHexString(len).length();
                }
                bt = AES.hexStringToBytes(supply[supplyIndex]+Integer.toHexString(len));
                for (byte b : bt) {
                    extraMsg[index++] = b;
                }
                sb.append(AES.parseByte2HexStr(bt));

                //计算crc校验码
                int crc = calcFileCrc(filePaths[i]);
                if(Integer.toHexString(crc).length()<4){
                    supplyIndex = 4-Integer.toHexString(crc).length();
                }
                bt = AES.hexStringToBytes(supply[supplyIndex]+Integer.toHexString(crc));
                for (byte b : bt) {
                    extraMsg[index++] = b;
                }
                sb.append(AES.parseByte2HexStr(bt));
                MainFrame.showMssageln("生成" + name[i] + "的附加信息" + (i + 1) + ":" + sb.toString());
                MainFrame.showMssageln("128字节附加信息:" + new String(extraMsg));
            } else {
                int c = JOptionPane.showConfirmDialog(MainFrame.getInstance(),
                        "待升级文件版本号不能为空？", "提示", JOptionPane.DEFAULT_OPTION);
                return null;
            }
        }
        for (; index < extraMsg.length; ) {
            extraMsg[index++] = (byte) 0xFF;
        }
        return extraMsg;
    }

    public int calcFileCrc(String filePath) {
        int crc = 0;
        CRC calcCrc = new CRC();
        try {
            byte[] bytes = new byte[128];
            File file = new File(filePath);
            InputStream inputStream = new FileInputStream(file);
            while (inputStream.available() > 0) {
                if (inputStream.available() < 128) {
                    bytes = new byte[inputStream.available()];
                    inputStream.read(bytes);
                    for(int i=inputStream.available();i<bytes.length;i++){
                        bytes[i] = (byte) 0xff;
                    }
                } else {
                    inputStream.read(bytes);
                }
                crc = calcCrc.calcCrc16(bytes, crc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return crc;
    }

    public int fileByteCount(String filPath) {
        int fileSize = 0;
        try {
            File file = new File(filPath);
            InputStream inputStream = new FileInputStream(file);
            fileSize = inputStream.available();
            return fileSize;
        } catch (Exception e) {
            e.printStackTrace();
            return fileSize;
        }
    }
}
