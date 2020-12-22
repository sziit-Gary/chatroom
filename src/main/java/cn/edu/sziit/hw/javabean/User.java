package cn.edu.sziit.hw.javabean;

import java.io.Serializable;


public class User implements Serializable {

    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 头像
     */


    //get set
    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    //toString
    @Override
    public String toString() {
        return "javabean.User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
