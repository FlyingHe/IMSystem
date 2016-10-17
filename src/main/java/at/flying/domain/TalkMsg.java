package at.flying.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

/**
 * Created by FlyingHe on 2016/9/16.
 */
public class TalkMsg {
    private Long id;
    private Long from;
    private Long to;
    private String content;
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public Long getTo() {
        return to;
    }

    public void setTo(Long to) {
        this.to = to;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
