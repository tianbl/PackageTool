package com.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by baolei on 2015/12/19.
 */
public class MainFrame extends JFrame {
    private static MainFrame instance;
    private JPanel jpanel_View;	//信息显示panel
    private static JTextArea jTextArea_View;
    private JScrollPane jScrollPane_View;	//添加滚动条

    private JTabbedPane jtab;	//个功能选项卡

    public static MainFrame getInstance(){
        if(instance==null){
            instance = new MainFrame();
        }
        return instance;
    }

    private MainFrame(){
        super();
        setResizable(false);
        setBackground(Color.WHITE);
        this.setTitle("升级包ping接工具");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        initGUI();
        System.out.println("GateTestJFrame signal instance...");
    }

    private void initGUI(){
        //获取桌面尺寸
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        {	//信息输出区域设置
            jpanel_View = new JPanel();
            jpanel_View.setBorder(new TitledBorder(UIManager
                    .getBorder("TitledBorder.border"),
                    "", TitledBorder.LEFT,
                    TitledBorder.TOP, null, new Color(0, 0, 0)));
            jTextArea_View = new JTextArea();
            jTextArea_View.setEditable(false);
            jTextArea_View.setLineWrap(true);
            jScrollPane_View = new JScrollPane();
            jScrollPane_View.setViewportView(jTextArea_View);
            jpanel_View.setLayout(new GridLayout(1,1));
            jpanel_View.add(jScrollPane_View);
        }

        {	//标签页设置
            jtab = new JTabbedPane(JTabbedPane.TOP);

            PackageMerge packageMerge = PackageMerge.getInstance();
            jtab.add(packageMerge," 升级包拼接 ");
//            gateTest  = new GatewayTest();
//            jtab.add(gateTest, "  1.测试和信息设置    ");
        }
        this.setBounds((int) (screenSize.width*0.3), (int) (screenSize.height*0.2),
                (int) (screenSize.width*0.4), (int) (screenSize.height * 0.6));
        this.setLayout(new GridLayout(2,1));
        this.add(jpanel_View);
        this.add(jtab);
        this.setVisible(true);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                //System.out.println("触发windowClosing事件");
            }

            public void windowClosed(WindowEvent e) {
                //System.out.println("触发windowClosed事件");
            }
        });

    }

    public static void showMssage(String str)
    {
        jTextArea_View.append(str);
        jTextArea_View.setSelectionStart(jTextArea_View.getText().length());
    }
    public static void showMssageln(String str){
        showMssage("merge-$ "+str + "\n");
    }
    public static void clearShow(){
        jTextArea_View.setText("");
    }
}
