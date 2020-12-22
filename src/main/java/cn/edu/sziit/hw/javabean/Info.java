package cn.edu.sziit.hw.javabean;

import java.io.Serializable;
import java.util.List;


/**
 * 服务器和客户端之间的消息协议
 * 0-CS第一次握手信息
 * 1-client请求广播信息
 * 2-保留
 * 3-client请求上传文件
 * 4-client查询在线人数
 * 5-server返回系统在线人数
 * 6-server返回反馈消息
 * 7-client查询在线用户列表
 * 8-server返回在线用户列表
 * 9-client私聊发送信息
 * 10-client私聊发送文件
 * 11-server转发私聊文件
 */
public class Info implements Serializable {

    /**
     * 消息类型
     */
    private int type;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 消息发送者用户名
     */
    private String source;
    /**
     * 消息接收者用户名
     */
    private String dest;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 附件
     */
    private List attach;


    //get set
    public Info(int type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List getAttach() {
        return attach;
    }

    public void setAttach(List onlineUsers) {
        this.attach = onlineUsers;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public int getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSource() {
        return source;
    }

    public String getDest() {
        return dest;
    }

    public String getFileName() {
        return fileName;
    }


    //toString
    @Override
    public String toString() {
        return "Info{" +
                "type=" + type +
                ", content='" + content + '\'' +
                ", source='" + source + '\'' +
                ", dest='" + dest + '\'' +
                ", fileName='" + fileName + '\'' +
                ", attach=" + attach +
                '}';
    }
}
