package net.java.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import com.example.administrator.myapplication.greendao.DaoSession;
import com.example.administrator.myapplication.greendao.SportInfoDao;
import com.example.administrator.myapplication.greendao.dayStepDao;

@Entity
public class dayStep {
    @Id
    private long id;
    private String date;
    private int step;
    private Long sportId;
    @ToOne(joinProperty = "sportId")
    private SportInfo sportInfo;//关系表
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1669406059)
    private transient dayStepDao myDao;
    @Generated(hash = 1078273959)
    public dayStep(long id, String date, int step, Long sportId) {
        this.id = id;
        this.date = date;
        this.step = step;
        this.sportId = sportId;
    }
    @Generated(hash = 1348095957)
    public dayStep() {
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getDate() {
        return this.date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public int getStep() {
        return this.step;
    }
    public void setStep(int step) {
        this.step = step;
    }
    public Long getSportId() {
        return this.sportId;
    }
    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }
    @Generated(hash = 449419486)
    private transient Long sportInfo__resolvedKey;
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1928654323)
    public SportInfo getSportInfo() {
        Long __key = this.sportId;
        if (sportInfo__resolvedKey == null
                || !sportInfo__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            SportInfoDao targetDao = daoSession.getSportInfoDao();
            SportInfo sportInfoNew = targetDao.load(__key);
            synchronized (this) {
                sportInfo = sportInfoNew;
                sportInfo__resolvedKey = __key;
            }
        }
        return sportInfo;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1395351724)
    public void setSportInfo(SportInfo sportInfo) {
        synchronized (this) {
            this.sportInfo = sportInfo;
            sportId = sportInfo == null ? null : sportInfo.getId();
            sportInfo__resolvedKey = sportId;
        }
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1246360032)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDayStepDao() : null;
    }

}
