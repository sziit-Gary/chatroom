package cn.edu.sziit.hw.starter;

import cn.edu.sziit.hw.javabean.Info;
import cn.edu.sziit.hw.util.RedisUtils;
import cn.edu.sziit.hw.util.SwingUtils;
import cn.edu.sziit.hw.util.ToolUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class Server extends JFrame {
    /**
     * 项目根路径
     */
    private static final String rootPath = "D:\\WorkSpace\\chatroom\\";
    /**
     * 服务器端口
     */
    private static final int PORT = 8888;
    /**
     * 保存在线用户信息，用户名-文本输出流
     */
    private static HashMap userMap = new HashMap<String, PrintWriter>();
    /**
     * 轻量级容器组件
     */
    private JPanel contentPane;
    /**
     * 服务器ip编辑框
     */
    private JTextField host_edit;
    /**
     * 服务器port编辑框
     */
    private JTextField port_edit;
    /**
     * 聊天框
     */
    private static JTextArea textArea;
    /**
     * 服务器套接字
     */
    private static ServerSocket serverSocket;
    /**
     * 线程安全计数器，统计在线人数
     */
    private static AtomicInteger userCount = new AtomicInteger(0);


    public Server() {
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel host_jLabel = new JLabel("服务器ip");
        host_jLabel.setBounds(10, 10, 70, 30);
        contentPane.add(host_jLabel);

        host_edit = new JTextField();
        host_edit.setBounds(98, 9, 97, 33);
        host_edit.setText("localhost");
        host_edit.setEnabled(false);
        contentPane.add(host_edit);
        host_edit.setColumns(10);

        JLabel port_jLabel = new JLabel("服务器port");
        port_jLabel.setBounds(222, 10, 70, 30);
        contentPane.add(port_jLabel);

        port_edit = new JTextField();
        port_edit.setBounds(302, 10, 87, 30);
        contentPane.add(port_edit);
        port_edit.setColumns(10);

        JButton start_btn = new JButton("开启服务");
        start_btn.setBounds(20, 50, 97, 23);
        contentPane.add(start_btn);

        textArea = new JTextArea();
        textArea.setBounds(10, 83, 744, 303);
        contentPane.add(textArea);

        final JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setBounds(10, 83, 744, 303);
        jScrollPane.setViewportView(textArea);
        contentPane.add(jScrollPane);

        JButton close_btn = new JButton("关闭服务");
        close_btn.setBounds(127, 50, 97, 23);
        contentPane.add(close_btn);

        JButton clear_btn = new JButton("清空内容");
        clear_btn.setBounds(234, 50, 97, 23);
        contentPane.add(clear_btn);

        /**
         * 关闭服务器点击事件
         */
        clear_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: 2020/6/26
            }
        });

        /**
         * 清空聊天框点击事件
         */
        clear_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.setText(" ");
            }
        });

        /**
         * 开启服务器点击事件
         */
        start_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            serverSocket = new ServerSocket(PORT);
                            if (! serverSocket.isClosed()) {
                                start_btn.setEnabled(false);
                                textArea.append(ToolUtils.getRealTime() + "，服务器开启成功\n");
                            }
                            while (true) {
                                Socket accept = serverSocket.accept();
                                new SingleUser(accept).start();
                            }
                        } catch (IOException ioException) {
                            JOptionPane.showMessageDialog(null, ioException.getMessage(), "服务器开启", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }).start();
            }
        });

    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(new FlatDarkLaf());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "UI风格设置", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                final Server server = new Server();
                server.setDefaultCloseOperation(EXIT_ON_CLOSE);
                server.setTitle("服务器程序");
                SwingUtils.setSize(server, 0.55, 0.5);
                server.setResizable(false);
                server.setVisible(true);
            }
        });
    }


    /**
     * 服务器转发客户端广播消息
     * @param sendData 广播内容
     * @param sender 发送者
    */
    private static void broadcast(String sendData, String sender) {
        final Collection values = userMap.values();
        for (Object value : values) {
            PrintWriter printWriter = (PrintWriter) value;
            // 回复
            final Info info = new Info(6);
            info.setContent(ToolUtils.getRealTime() + "，用户" + sender + "，广播了消息：" + sendData);
            info.setSource(sender);
            printWriter.println(JSON.toJSONString(info));
        }
    }

    /**
     * 用户私聊发送文件
     * @param source 发送用户名
     * @param fileContent 文件内容
     * @param dest 私聊用户名
     * @param fileName 文件名
     */
    private static void transferDoc(String source, String fileContent, String dest, String fileName) {
        final PrintWriter destWriter = (PrintWriter) userMap.get(dest);
        final PrintWriter sourceWriter = (PrintWriter) userMap.get(source);
        if (destWriter == null) {
            if (sourceWriter != null) {
                final Info info1 = new Info(6);
                info1.setContent(ToolUtils.getRealTime() + "，你私聊发送文件失败，用户" + dest + "已下线");
                sourceWriter.println(JSON.toJSONString(info1));
                textArea.append(ToolUtils.getRealTime() + "，用户" + source + "和" + dest + "私聊文件失败\n");
            }
        }
        else {
            final Info info = new Info(11);
            info.setDest(dest);
            info.setContent(fileContent);
            info.setSource(source);
            info.setFileName(fileName);
            destWriter.println(JSON.toJSONString(info));
            if (sourceWriter != null) {
                final Info info1 = new Info(6);
                info1.setContent(ToolUtils.getRealTime() + "，你私聊发送文件给用户" + dest + "成功");
                sourceWriter.println(JSON.toJSONString(info1));
                textArea.append(ToolUtils.getRealTime() + "，用户" + source + "和" + dest + "私聊文件成功\n");
            }
        }
    }

    /**
     * 用户私聊发送数据
     * @param sendData 私聊内容
     * @param dest 私聊用户名
     * @param username 发送用户名
     */
    private static void transferData(String username, String sendData, String dest) {
        final PrintWriter sourceWriter = (PrintWriter) userMap.get(username);
        final PrintWriter destWriter = (PrintWriter) userMap.get(dest);
        // 私聊对象已下线
        if (destWriter == null) {
            // 私聊发起人在线
            if (sourceWriter != null) {
                final Info info = new Info(6);
                info.setContent(ToolUtils.getRealTime() + "，抱歉，用户" + dest + "，已下线，请稍候重发");
                sourceWriter.println(JSON.toJSONString(info));
                textArea.append(ToolUtils.getRealTime() + "，用户" + username + "和" + dest + "私聊失败\n");
            }
        }
        // 私聊对象在线
        else {
            final Info info = new Info(6);
            info.setContent(ToolUtils.getRealTime() + "，用户" + username + "，私聊你如下内容：" + sendData);
            destWriter.println(JSON.toJSONString(info));
            textArea.append(ToolUtils.getRealTime() + "，用户" + username + "和" + dest + "私聊成功\n");
            // 私聊发起人在线
            if (sourceWriter != null) {
                final Info info1 = new Info(6);
                info1.setContent(ToolUtils.getRealTime() + "，你刚刚私聊给用户" + dest + "已发送成功");
                sourceWriter.println(JSON.toJSONString(info1));
            }
        }
    }


    /**
     * 内部静态类，负责与单独的客户端线程的通信工作
     */
    private static class SingleUser extends Thread {
        /**
         * 服务端与客户端对应的套接字
         */
        private Socket socket;
        /**
         * 缓冲字符输入流
         */
        private BufferedReader bufferedReader;
        /**
         * 文本输出流
         */
        private PrintWriter printWriter;
        /**
         * 用户名
         */
        private String id;

        public SingleUser(Socket socket) {
            this.socket = socket;
            // 在线人数+1
            userCount.incrementAndGet();
        }

        @Override
        public void run() {
            try {
                printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String recv = null;
                while (socket.isConnected()) {
                    while ( (recv = bufferedReader.readLine()) != null ) {
                        final JSONObject jsonObject = JSON.parseObject(recv);
                        final int type = jsonObject.getIntValue("type");
                        // 客户端、服务器第一次握手
                        if (type == 0) {
                            final String username = jsonObject.getString("source");
                            id = username;
                            System.out.println("-----------------------");
                            System.out.println(id);
                            userMap.put(username, printWriter);
                            // 添加在线用户进缓存
                            RedisUtils.addAliveUser(id);
                            textArea.append(ToolUtils.getRealTime() + "，用户" + username + "连接成功\n");
                            // 回复
                            final Info info = new Info(6);
                            info.setContent(ToolUtils.getRealTime() + "，用户" + username + "连接成功");
                            printWriter.println(JSON.toJSONString(info));
                        }
                        // 客户端请求广播消息
                        if (type == 1) {
                            final String content = jsonObject.getString("content");
                            final String username = jsonObject.getString("source");
                            broadcast(content, username);
                            textArea.append(ToolUtils.getRealTime() + "，用户" + username + "广播了消息\n");
                        }
                        // 客户端上传文件
                        if (type == 3) {
                            final String content = jsonObject.getString("content");
                            final String username = jsonObject.getString("source");
                            final String fileName = (String) jsonObject.get("fileName");
                            // 服务器保存文件制定路径
                            String path = rootPath + "files\\" + username + "\\" + ToolUtils.getRealTime("YY_MM_dd_HH_mm_ss") + "_" + fileName;
                            System.out.println(path);
                            ToolUtils.decodeFile(content, path);
                            textArea.append(ToolUtils.getRealTime() + "，用户" + username + "上传了文件\n");
                            // 回复
                            final Info info = new Info(6);
                            info.setContent(ToolUtils.getRealTime() + "，用户" + username + "上传了文件" + fileName);
                            printWriter.println(JSON.toJSONString(info));
                        }
                        // 客户端查询在线人数
                        if (type == 4) {
                            // 回复
                            final Info info = new Info(5);
                            info.setContent(String.valueOf(userCount.get()));
                            printWriter.println(JSON.toJSONString(info));
                        }
                        // 客户端请求转发私聊信息
                        if (type == 9) {
                            final String content = jsonObject.getString("content");
                            final String dest = jsonObject.getString("dest");
                            final String source = jsonObject.getString("source");
                            transferData(source, content, dest);
                        }
                        // 客户端私聊发送文件
                        if (type == 10) {
                            final String content = jsonObject.getString("content");
                            final String dest = jsonObject.getString("dest");
                            final String source = jsonObject.getString("source");
                            final String fileName = (String) jsonObject.get("fileName");
                            transferDoc(source, content, dest, fileName);
                        }
                    }
                }
            } catch (IOException e) {
                // 不给出提示框，直接结束方便finally直接执行
                return;
            } finally {
                // 删除用户缓存
                userMap.remove(id);
                RedisUtils.delAliveUser(id);
                // 在线人数-1
                userCount.decrementAndGet();
            }
        }
    }
}
