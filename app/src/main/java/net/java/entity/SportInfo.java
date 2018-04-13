package net.java.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class SportInfo {
    @Id
    private Long id;
    private String message;
    @Generated(hash = 1137934895)
    public SportInfo(Long id, String message) {
        this.id = id;
        this.message = message;
    }
    @Generated(hash = 1254911132)
    public SportInfo() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getMessage() {
        return this.message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
