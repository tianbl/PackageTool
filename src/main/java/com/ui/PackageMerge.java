package com.ui;

import com.business.Merge;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by baolei on 2015/12/19.
 */
public class PackageMerge extends JPanel implements ActionListener {
    private JLabel label47;
    private JTextField versionText47;
    private JLabel label30;
    private JTextField versionText30;
    private JLabel labelCRC;
    private JTextField textCRC;
    private JLabel program30;
    private JLabel program47;

    private JLabel targetLabel;
    private JTextField targetVersion;

    private JLabel devTypeLabel;
    private JTextField devType;


    private JButton fileChoser47;
    private JButton fileChoser30;
    private JButton merge;
    private JButton clearMsg;
    private String titleFile47 = "选择47升级包";
    private String titleFile30 = "选择30升级包";
    private String titleCRC = "CRC16-CCITT";
    private String titleMerge = "合并升级包";
    private String titleClear = "清除";

    private String absolutePath30;
    private String absolutePath47;

    private JFileChooser jfc;

    private static PackageMerge instance;

    public static PackageMerge getInstance() {
        if (null == instance) {
            instance = new PackageMerge();
        }
        return instance;
    }

    private PackageMerge() {
        MainFrame.showMssage("确保ES47LE和ES30LE升级包命名中包含‘47’和‘30’\n");
        {
            clearMsg = new JButton(titleClear);
            clearMsg.setBounds(320, 10, 120, 30);
            clearMsg.addActionListener(this);
            this.add(clearMsg);
        }
        {
            int x = 5;
            int y = 50;
            devTypeLabel = new JLabel("设备类型");
            devType = new JTextField("FFFF190000000000");
            devTypeLabel.setBounds(5, 10, 150, 30);
            devType.setBounds(120, 10, 200, 30);

            label30 = new JLabel("输入30升级版版本号");
            label47 = new JLabel("输入47升级版版本号");
            labelCRC = new JLabel(titleCRC);

            versionText30 = new JTextField();
            versionText47 = new JTextField();
            textCRC = new JTextField();

            fileChoser47 = new JButton(titleFile47);
            fileChoser30 = new JButton(titleFile30);
            program47 = new JLabel();
            program30 = new JLabel();

            label47.setBounds(5, y, 150, 30);
            label30.setBounds(5, y + 40, 150, 30);
            labelCRC.setBounds(5, 120, 150, 30);

            versionText47.setBounds(120, y, 200, 30);
            versionText30.setBounds(120, y + 40, 200, 30);
            textCRC.setBounds(120, 120, 200, 30);

            fileChoser47.setBounds(320, y, 120, 30);
            fileChoser30.setBounds(320, y + 40, 120, 30);
            program47.setBounds(440, y, 100, 30);
            program30.setBounds(440, y + 40, 100, 30);
            fileChoser47.addActionListener(this);
            fileChoser30.addActionListener(this);

            targetLabel = new JLabel("合并后版本号");
            targetVersion = new JTextField();
            targetLabel.setBounds(5, y + 80, 150, 30);
            targetVersion.setBounds(120, y + 80, 200, 30);

            this.add(devTypeLabel);
            this.add(devType);
            this.add(targetLabel);
            this.add(targetVersion);
            this.add(program30);
            this.add(program47);
        }
        {
            merge = new JButton(titleMerge);
            merge.setBounds(320, 130, 120, 30);
            merge.addActionListener(this);
        }
        this.setLayout(null);
        this.add(label30);
        this.add(label47);
        this.add(versionText30);
        this.add(versionText47);
        this.add(fileChoser30);
        this.add(fileChoser47);
        this.add(merge);
    }

    public void actionPerformed(ActionEvent e) {
        String buttonName = e.getActionCommand();
        if (titleClear.equals(buttonName)) {
            MainFrame.clearShow();
        } else if (titleMerge.equals(buttonName)) {
            boolean bool30 = null == absolutePath30 || "".equals(absolutePath30);
            boolean bool47 = (null == absolutePath47 || "".equals(absolutePath47));
            if(versionText30.getText().length()!=24||versionText47.getText().length()!=24
                    ||targetVersion.getText().length()!=24){
                int i = JOptionPane.showConfirmDialog(MainFrame.getInstance(),
                        "版本号不是24位重新输入", "提示", JOptionPane.DEFAULT_OPTION);
                return;
            }
            if (bool30 || bool47) {
                int i = JOptionPane.showConfirmDialog(MainFrame.getInstance(),
                        "请选择升级包!", "提示", JOptionPane.DEFAULT_OPTION);
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new Merge().mergeUpdateFile(absolutePath30, absolutePath47);
                    }
                }).start();
            }
        } else {
            if (null == jfc) {
                jfc = new JFileChooser();
            }
            jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            jfc.showDialog(new JLabel(), "选择");
            File file = jfc.getSelectedFile();

            if (titleFile30.equals(buttonName)) {
                absolutePath30 = file.getAbsolutePath();
                program30.setText(file.getName());
                if(file.getName().contains("47")){
                    JOptionPane.showConfirmDialog(MainFrame.getInstance(),
                            "请在此处选择30升级包", "提示", JOptionPane.DEFAULT_OPTION);
                    absolutePath30 = null;
                    program30.setText("");
                }
                MainFrame.showMssageln("选择30升级包:" + absolutePath30);
            } else if (titleFile47.equals(buttonName)) {
                absolutePath47 = file.getAbsolutePath();
                program47.setText(file.getName());
                if(file.getName().contains("30")){
                    JOptionPane.showConfirmDialog(MainFrame.getInstance(),
                            "请在此处选择47升级包", "提示", JOptionPane.DEFAULT_OPTION);
                    absolutePath47 = null;
                    program47.setText("");
                }
                MainFrame.showMssageln("选择47升级包:" + absolutePath47);
            }
        }
    }

    public String getVersionOfTarget() {
        return targetVersion.getText();
    }

    public String getVersionOf30() {
        return versionText30.getText();
    }

    public String getVersionOf47() {
        return versionText47.getText();
    }

    public String getAbsolutePath30() {
        return absolutePath30;
    }

    public void setAbsolutePath30(String absolutePath30) {
        this.absolutePath30 = absolutePath30;
    }

    public String getAbsolutePath47() {
        return absolutePath47;
    }

    public void setAbsolutePath47(String absolutePath47) {
        this.absolutePath47 = absolutePath47;
    }

    public String getDeviceType(){
        return devType.getText();
    }
}