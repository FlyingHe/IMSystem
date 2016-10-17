package at.flying.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by FlyingHe on 2016/9/16.
 */
public class Talker {
    private Long id;
    private String username;
    @JsonIgnore
    private String password;
    private String headImg;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }
}
