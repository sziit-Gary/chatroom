package cn.edu.sziit.hw.service;

import cn.edu.sziit.hw.javabean.User;
import cn.edu.sziit.hw.util.RedisUtils;
import cn.edu.sziit.hw.util.SwingUtils;
import cn.edu.sziit.hw.util.ToolUtils;
import com.formdev.flatlaf.FlatDarculaLaf;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 登录界面
 */
public class LoginFrame extends JFrame {
    /**
     * Jpanel容器
     */
    private static JPanel contentPane;
    /**
     * 用户名编辑框
     */
    private JTextField username_edit;
    /**
     * 密码编辑框
     */
    private JPasswordField password_edit;


    /**
     * 窗口按钮布局
     */
    public LoginFrame() {
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel banner_jLabel = new JLabel();
        banner_jLabel.setIcon(new ImageIcon("images/banner.png"));
        banner_jLabel.setBounds(0, 0, 436, 84);
        contentPane.add(banner_jLabel);

        JButton register_btn = new JButton("注册");
        register_btn.setBounds(10, 190, 97, 23);
        contentPane.add(register_btn);

        JButton login_btn = new JButton("登录");
        login_btn.setBounds(117, 190, 97, 23);
        contentPane.add(login_btn);

        JLabel username_jLabel = new JLabel("用户名");
        username_jLabel.setBounds(77, 96, 58, 15);
        contentPane.add(username_jLabel);

        JLabel password_jLabel = new JLabel("密码");
        password_jLabel.setBounds(77, 134, 58, 15);
        contentPane.add(password_jLabel);

        username_edit = new JTextField();
        username_edit.setBounds(145, 93, 116, 21);
        contentPane.add(username_edit);
        username_edit.setColumns(10);

        password_edit = new JPasswordField();
        password_edit.setBounds(145, 131, 116, 21);
        contentPane.add(password_edit);

        JButton modify_btn = new JButton("修改密码");
        modify_btn.setBounds(224, 190, 97, 23);
        contentPane.add(modify_btn);

        /**
         * 登录点击事件
         */
        login_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = username_edit.getText().trim();
                String password = new String(password_edit.getPassword());
                if (username == null || password == null) {
                    JOptionPane.showMessageDialog(null, "用户名、密码不许为空！", "用户登录", JOptionPane.INFORMATION_MESSAGE);
                }
                final int recv = RedisUtils.validUser(username, password);
                if (recv == 0) {
                    JOptionPane.showMessageDialog(null, "用户不存在，请先注册！", "用户登录", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                if (recv == 1) {
                    JOptionPane.showMessageDialog(null, "密码错误！", "用户登录", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                if (RedisUtils.isAlive(username)) {
                    JOptionPane.showMessageDialog(null, "你已经登录了，不能重复登录！", "用户登录", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                JOptionPane.showMessageDialog(null, "登录成功！", "用户登录", JOptionPane.INFORMATION_MESSAGE);
                final User user = new User();
                user.setUsername(username);
                user.setUsername(password);
                user.setImage(ToolUtils.getProfile());
                ProfileFrame.launch(user);
                dispose();
            }
        });

        /**
         * 修改密码点击事件
         */
        modify_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = username_edit.getText().trim();
                String password = new String(password_edit.getPassword());
                if (username == null || password == null) {
                    JOptionPane.showMessageDialog(null,"用户名、密码不许为空！", "修改密码", JOptionPane.INFORMATION_MESSAGE);
                }
                final int recv = RedisUtils.validUser(username, password);
                if (recv == 0) {
                    JOptionPane.showMessageDialog(null,"用户不存在，请先注册！", "修改密码", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                if (recv == 1) {
                    JOptionPane.showMessageDialog(null,"密码错误！", "修改密码", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                final String newPassword = (String) JOptionPane.showInputDialog(null, "请输入新密码", "修改密码", JOptionPane.INFORMATION_MESSAGE, null, null, null);
                System.out.println(newPassword);
                if (StringUtils.isBlank(newPassword)) {
                    JOptionPane.showMessageDialog(null,"新密码不许为空！", "修改密码", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                RedisUtils.modifyUser(username, newPassword);
            }
        });

        /**
         * 注册点击事件
         */
        register_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = username_edit.getText().trim();
                String password = new String(password_edit.getPassword());
                if (username == null || password == null) {
                    JOptionPane.showMessageDialog(null, "用户名、密码不许为空！", "注册", JOptionPane.INFORMATION_MESSAGE);
                }
                final int recv = RedisUtils.insertUser(username, password);
                if (recv == 0) {
                    JOptionPane.showMessageDialog(null, "用户已存在，请直接登录！", "注册", JOptionPane.INFORMATION_MESSAGE);
                }
                if (recv == 1) {
                    JOptionPane.showMessageDialog(null, "注册失败！", "注册", JOptionPane.INFORMATION_MESSAGE);
                }
                if (recv == 2) {
                    JOptionPane.showMessageDialog(null, "注册成功！", "注册", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });


    }

    /**
     * 对外提供的启动函数
     */
    public static void launch() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(new FlatDarculaLaf());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "UI风格设置",  JOptionPane.WARNING_MESSAGE);
                    return;
                }
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                SwingUtils.setSize(loginFrame, 0.23, 0.4);
                loginFrame.setTitle("LoginChat");
                loginFrame.setIconImage(new ImageIcon("images/qq.png").getImage());
                loginFrame.setResizable(false);
                loginFrame.setVisible(true);
            }
        });
    }
}
