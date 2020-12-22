package cn.edu.sziit.hw.service;

import cn.edu.sziit.hw.javabean.Info;
import cn.edu.sziit.hw.javabean.User;
import cn.edu.sziit.hw.util.RedisUtils;
import cn.edu.sziit.hw.util.SwingUtils;
import cn.edu.sziit.hw.util.ToolUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.formdev.flatlaf.FlatDarculaLaf;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhupengcheng
 * @date 2020/6/19 15:46
 */
public class ProfileFrame extends JFrame {
    /**
     * 客户端实体
     */
    private static User user;
    /**
     * 轻量级组件容器
     */
    private JPanel contentPanel;
    /**
     * 连接的服务器ip
     */
    private JTextField host_edit;
    /**
     * 连接的服务器port
     */
    private JTextField port_edit;
    /**
     * 文本输出流
     */
    private PrintWriter printWriter;
    /**
     * 字符缓存输入流
     */
    private BufferedReader bufferedReader;
    /**
     * 聊天框
     */
    private JTextArea textArea;
    /**
     * 客户端套接字
     */
    private Socket socket;
    /**
     * 在线人数编辑框
     */
    private JTextField count_edit;
    /**
     * 系统时钟编辑框
     */
    private JTextField time_edit;
    /**
     * 显示在线用户的列表
     */
    JList list;
    /**
     * 接收服务器回复的在线人数，线程安全
     */
    private static AtomicInteger counts = new AtomicInteger(0);
    /**
     * 私聊用户名
     */
    private String secretUser;
    /**
     * 私聊用户名编辑框
     */
    private JTextField secret_edit;

    /**
     * 对外提供的启动函数
     * @param u 用户实体
     */
    public static void launch(User u) {
        user = u;
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "UI风格设置", JOptionPane.WARNING_MESSAGE);
            return;
        }
        final ProfileFrame profileFrame = new ProfileFrame();
        profileFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        SwingUtils.setSize(profileFrame, 0.5, 1);
        profileFrame.setTitle(user.getUsername());
        profileFrame.setIconImage(new ImageIcon("images/qq.png").getImage());
        profileFrame.setResizable(false);
        profileFrame.setVisible(true);
    }

    public ProfileFrame() {
        contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPanel);
        contentPanel.setLayout(null);

        JPanel banner = new JPanel();
        banner.setBounds(0, 10, 446, 142);
        contentPanel.add(banner);
        banner.setLayout(null);

        JLabel profile_jLabel = new JLabel();
        profile_jLabel.setBounds(10, 5, 147, 114);
        profile_jLabel.setIcon(SwingUtils.scaleImageIcon(new ImageIcon(user.getImage())
                , profile_jLabel.getSize().getWidth()
                , profile_jLabel.getSize().getHeight()));
        banner.add(profile_jLabel);

        JLabel name_jLabel = new JLabel("姓名：");
        name_jLabel.setBounds(167, 10, 58, 36);
        banner.add(name_jLabel);

        JLabel username_jLabel = new JLabel(user.getUsername());
        username_jLabel.setBounds(167, 56, 91, 36);
        banner.add(username_jLabel);

        JPanel content_jPanel = new JPanel();
        content_jPanel.setBounds(0, 161, 446, 85);
        content_jPanel.setBackground(new Color(36, 36, 36, 242));
        contentPanel.add(content_jPanel);

        JButton profile_btn = new JButton("更换头像");
        profile_btn.setBounds(167, 109, 97, 23);
        banner.add(profile_btn);

        JLabel host_jLabel = new JLabel("服务器ip");
        host_jLabel.setBounds(10, 2, 59, 33);
        content_jPanel.add(host_jLabel);

        host_edit = new JTextField();
        host_edit.setBounds(80, 3, 109, 31);
        host_edit.setText("localhost");
        host_edit.setEnabled(false);
        content_jPanel.add(host_edit);
        host_edit.setColumns(10);

        JLabel port_jLabel = new JLabel("服务器port");
        port_jLabel.setBounds(203, 1, 68, 35);
        content_jPanel.add(port_jLabel);

        port_edit = new JTextField();
        port_edit.setBounds(280, 3, 109, 31);
        content_jPanel.add(port_edit);
        port_edit.setColumns(10);

        JButton connect_btn = new JButton("连接服务器");
        connect_btn.setBounds(55, 34, 110, 4);
        content_jPanel.add(connect_btn);

        JButton clear_btn = new JButton("清空内容");
        clear_btn.setBounds(182, 33, 110, 42);
        content_jPanel.add(clear_btn);

        textArea = new JTextArea();
        textArea.setBounds(10, 256, 426, 368);
        contentPanel.add(textArea);

        final JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setBounds(10, 256, 426, 368);
        jScrollPane.setViewportView(textArea);
        contentPanel.add(jScrollPane);

        JTextArea send_textArea = new JTextArea();
        send_textArea.setBounds(10, 652, 333, 55);
        contentPanel.add(send_textArea);

        final JScrollPane send_scroll = new JScrollPane();
        send_scroll.setBounds(10, 652, 333, 55);
        send_scroll.setViewportView(send_textArea);
        contentPanel.add(send_scroll);

        JButton broadcast_btn = new JButton("广播发送");
        broadcast_btn.setBounds(227, 726, 116, 38);
        contentPanel.add(broadcast_btn);

        JButton doc_btn = new JButton();
        doc_btn.setBounds(10, 726, 30, 30);
        doc_btn.setIcon(SwingUtils.scaleImageIcon(new ImageIcon("images/file.png")
                , doc_btn.getSize().getWidth()
                , doc_btn.getSize().getHeight()));
        contentPanel.add(doc_btn);

        JLabel count_jLabel = new JLabel("系统当前人数");
        count_jLabel.setBounds(456, 10, 83, 38);
        contentPanel.add(count_jLabel);

        count_edit = new JTextField();
        count_edit.setBounds(549, 10, 97, 38);
        contentPanel.add(count_edit);
        count_edit.setText("0");
        count_edit.setEnabled(false);
        count_edit.setColumns(10);

        JLabel time_jLabel = new JLabel("当前系统时间");
        time_jLabel.setBounds(456, 58, 83, 38);
        contentPanel.add(time_jLabel);

        time_edit = new JTextField();
        time_edit.setColumns(10);
        time_edit.setBounds(549, 58, 97, 38);
        time_edit.setEnabled(false);
        contentPanel.add(time_edit);

        list = new JList(new Object[]{"用户上线中..."});
        list.setVisibleRowCount(3);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setBounds(486, 106, 258, 140);
        list.setVisible(true);
        contentPanel.add(list);

        JTextArea secretSend_textArea = new JTextArea();
        secretSend_textArea.setBounds(472, 326, 272, 55);
        contentPanel.add(secretSend_textArea);


        final JScrollPane secret_scroll = new JScrollPane();
        secret_scroll.setViewportView(secretSend_textArea);
        secret_scroll.setBounds(472, 326, 272, 55);
        contentPanel.add(secret_scroll);

        JLabel secret_jLabel = new JLabel("私聊对象：");
        secret_jLabel.setBounds(472, 272, 83, 32);
        contentPanel.add(secret_jLabel);

        secret_edit = new JTextField();
        secret_edit.setBounds(565, 272, 97, 32);
        secret_edit.setEnabled(false);
        contentPanel.add(secret_edit);
        secret_edit.setColumns(10);

        JButton secretFile_btn = new JButton();
        secretFile_btn.setBounds(471, 402, 32, 32);
        secretFile_btn.setIcon(SwingUtils.scaleImageIcon(new ImageIcon("images/secret.png")
                , secretFile_btn.getSize().getWidth()
                , secretFile_btn.getSize().getHeight()));
        contentPanel.add(secretFile_btn);

        JButton secret_btn = new JButton("私聊发送");
        secret_btn.setBounds(631, 399, 111, 38);
        contentPanel.add(secret_btn);

        /**
         * 私聊内容发送
         */
        secret_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sendData = secretSend_textArea.getText();
                if (StringUtils.isBlank(sendData)) {
                    JOptionPane.showMessageDialog(null, "发送内容不能为空！", "发送私聊内容", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (secretUser == null) {
                    JOptionPane.showMessageDialog(null, "未选择私聊对象！", "发送私聊内容", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                // 报文头为2号，代表客户端请求发送私聊信息
                final Info info = new Info(9);
                info.setContent(sendData);
                info.setSource(user.getUsername());
                info.setDest(secretUser);
                printWriter.println(JSON.toJSONString(info));
                list.setEnabled(true);
            }
        });

        /**
         * 私聊传文件
         */
        secretFile_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                jFileChooser.setDialogTitle("打开");
                final int state = jFileChooser.showOpenDialog(null);
                if (state == JFileChooser.APPROVE_OPTION) {
                    try {
                        final File selectedFile = jFileChooser.getSelectedFile();
                        final String fileContent = new String(ToolUtils.codeFile(selectedFile), StandardCharsets.UTF_8);
                        // 私聊传输文件，报文头为10
                        final Info info = new Info(10);
                        info.setContent(fileContent);
                        info.setSource(user.getUsername());
                        info.setFileName(selectedFile.getName());
                        if (secretUser == null) {
                            JOptionPane.showMessageDialog(null, "未选择私聊对象！", "发送私聊文件", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        info.setDest(secretUser);
                        printWriter.println(JSON.toJSONString(info));
                        list.setEnabled(true);
                    } catch (IOException ioException) {
                        JOptionPane.showMessageDialog(null, ioException.getMessage(), "发送私聊文件", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }
        });

        /**
         * 选择私聊用户点击事件
         */
        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                final Object value = list.getSelectedValue();
                if (value != null) {
                    final int recv = JOptionPane.showConfirmDialog(null, user.getUsername() + "，你要选择" + value + "为私聊对象吗", "选择好友私聊", JOptionPane.YES_NO_OPTION);
                    // 选择成功
                    if (recv == 0) {
                        if (user.getUsername().equals(value)) {
                            JOptionPane.showMessageDialog(null, "不能选择你自己", "选择私聊对象", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        secretUser = (String) value;
                        secret_edit.setText(secretUser);
                        list.setEnabled(false);
                    }
                }
            }
        });

        /**
         * 定时执行，查询在线人数
         */
        final ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (printWriter != null) {
                    // 空报头即可
                    final Info info = new Info(4);
                    printWriter.println(JSON.toJSONString(info));
                }
                count_edit.setText(String.valueOf(counts.get()));
            }
        };
        new Timer(3000, actionListener).start();

        /**
         * 定时执行，更新在线用户列表
         */
        final ActionListener actionListener1 = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (printWriter != null) {
                    final Set aliveUsers = RedisUtils.getAliveUsers();
                    final DefaultListModel<String> defaultListModel = new DefaultListModel<>();
                    for (Object aliveUser : aliveUsers) {
                        defaultListModel.addElement((String) aliveUser);
                        list.setModel(defaultListModel);
                    }
                }
            }
        };
        new Timer(5000, actionListener1).start();


        /**
         * 实时更新时间
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    time_edit.setText(ToolUtils.getRealTimeByHour());
                }
            }
        }).start();


        /**
         * 上传文件点击事件
         */
        doc_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                jFileChooser.setDialogTitle("打开");
                final int state = jFileChooser.showOpenDialog(null);
                if (state == JFileChooser.APPROVE_OPTION) {
                    try {
                        final File selectedFile = jFileChooser.getSelectedFile();
                        final String fileContent = new String(ToolUtils.codeFile(selectedFile), StandardCharsets.UTF_8);
                        // 传输文件
                        final Info info = new Info(3);
                        info.setContent(fileContent);
                        info.setSource(user.getUsername());
                        info.setFileName(selectedFile.getName());
                        printWriter.println(JSON.toJSONString(info));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                        return;
                    }
                }

            }
        });

        /**
         * 广播消息点击事件
         */
        broadcast_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sendData = send_textArea.getText();
                if (StringUtils.isBlank(sendData)) {
                    JOptionPane.showMessageDialog(null, "发送消息不能为空！", "广播消息", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                if (socket == null || socket.isClosed()) {
                    JOptionPane.showMessageDialog(null, "你已掉线，请重新登陆！", "广播消息", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                // JSON格式发送
                final Info info = new Info(1);
                info.setContent(sendData);
                info.setSource(user.getUsername());
                printWriter.println(JSON.toJSONString(info));
            }
        });


        /**
         * 连接服务器点击事件
         */
        connect_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String host = host_edit.getText().trim();
                String port = port_edit.getText().trim();
                if (StringUtils.isBlank(host) || StringUtils.isBlank(port)) {
                    JOptionPane.showMessageDialog(null, "ip和port禁止为空！", "连接服务器", JOptionPane.INFORMATION_MESSAGE);
                }
                try {
                    socket = new Socket(host, Integer.parseInt(port));
                    if (socket.isConnected()) {
                        connect_btn.setEnabled(false);
                    }
                    bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                    // json格式传输二进制流
                    final Info info = new Info(0);
                    info.setSource(user.getUsername());
                    printWriter.println(JSON.toJSONString(info));

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String recv = null;
                            while (socket.isConnected()) {
                                try {
                                    while ((recv = bufferedReader.readLine()) != null) {
                                        final JSONObject jsonObject = JSON.parseObject(recv);
                                        final int type = jsonObject.getIntValue("type");
                                        final String content = jsonObject.getString("content");
                                        System.out.println("服务器发来消息，类型：" + type + "，内容" + content);
                                        // 服务器反馈信息
                                        if (type == 6) {
                                            textArea.append(content + "\n");
                                        }
                                        // 服务器返回系统人数
                                        if (type == 5) {
                                            counts.set(Integer.parseInt(content));
                                        }
                                        // 服务器转发私聊文件
                                        if (type == 11) {
                                            final String source = jsonObject.getString("source");
                                            final String fileContent = jsonObject.getString("content");
                                            final String fileName = jsonObject.getString("fileName");
                                            ToolUtils.decodeFile(fileContent, FileSystemView.getFileSystemView().getHomeDirectory() + "\\" + ToolUtils.getRealTimeByHour("HH_mm_ss") + "_" + source + "_" + fileName);
                                            System.out.println("接收" + source + "私聊文件" + fileContent);
                                        }
                                    }
                                } catch (IOException ioException) {
                                    JOptionPane.showMessageDialog(null, "连接服务器失败，请稍候重连！", "服务器启动", JOptionPane.INFORMATION_MESSAGE);
                                    return;
                                }
                            }
                        }
                    }).start();
                } catch (IOException ioException) {
                    JOptionPane.showMessageDialog(null, "连接服务器失败，请稍候重连！", "服务器启动", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }
        });

        /**
         * 清空聊天区点击事件
         */
        clear_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.setText("");
            }
        });

        /**
         * 更换头像点击事件
         */
        profile_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFileChooser jFileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                final FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter("仅照片", "jpg", "gif", "png");
                jFileChooser.setFileFilter(fileNameExtensionFilter);
                final int recv = jFileChooser.showOpenDialog(null);
                if (recv == JFileChooser.APPROVE_OPTION) {
                    final File selectedFile = jFileChooser.getSelectedFile();
                    profile_jLabel.setIcon(SwingUtils.scaleImageIcon(new ImageIcon(selectedFile.getAbsolutePath())
                            , profile_jLabel.getSize().getWidth()
                            , profile_jLabel.getSize().getHeight()));
                }
            }
        });
    }


}
