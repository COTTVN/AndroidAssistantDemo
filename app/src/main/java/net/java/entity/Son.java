package net.java.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Son {
    @Id
    private Long id;

    private String name;

    private long fatherID ;

    @Generated(hash = 1324802427)
    public Son(Long id, String name, long fatherID) {
        this.id = id;
        this.name = name;
        this.fatherID = fatherID;
    }

    @Generated(hash = 1259336981)
    public Son() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getFatherID() {
        return this.fatherID;
    }

    public void setFatherID(long fatherID) {
        this.fatherID = fatherID;
    }
}
