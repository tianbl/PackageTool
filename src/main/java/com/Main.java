package com;

import com.ui.MainFrame;

/**
 * Created by baolei on 2015/12/18.
 */
public class Main {
    public static void main(String[] args){
        String lookAndFeel_now= javax.swing.UIManager.getSystemLookAndFeelClassName();
        try {
            javax.swing.UIManager.setLookAndFeel(lookAndFeel_now);
            MainFrame inst = MainFrame.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
