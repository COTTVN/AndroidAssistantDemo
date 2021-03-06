package com.example.administrator.myapplication.greendao;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.SqlUtils;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import net.java.entity.SportInfo;

import net.java.entity.dayStep;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "DAY_STEP".
*/
public class dayStepDao extends AbstractDao<dayStep, Long> {

    public static final String TABLENAME = "DAY_STEP";

    /**
     * Properties of entity dayStep.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, long.class, "id", true, "_id");
        public final static Property Date = new Property(1, String.class, "date", false, "DATE");
        public final static Property Step = new Property(2, int.class, "step", false, "STEP");
        public final static Property SportId = new Property(3, Long.class, "sportId", false, "SPORT_ID");
    }

    private DaoSession daoSession;


    public dayStepDao(DaoConfig config) {
        super(config);
    }
    
    public dayStepDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"DAY_STEP\" (" + //
                "\"_id\" INTEGER PRIMARY KEY NOT NULL ," + // 0: id
                "\"DATE\" TEXT," + // 1: date
                "\"STEP\" INTEGER NOT NULL ," + // 2: step
                "\"SPORT_ID\" INTEGER);"); // 3: sportId
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"DAY_STEP\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, dayStep entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
 
        String date = entity.getDate();
        if (date != null) {
            stmt.bindString(2, date);
        }
        stmt.bindLong(3, entity.getStep());
 
        Long sportId = entity.getSportId();
        if (sportId != null) {
            stmt.bindLong(4, sportId);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, dayStep entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
 
        String date = entity.getDate();
        if (date != null) {
            stmt.bindString(2, date);
        }
        stmt.bindLong(3, entity.getStep());
 
        Long sportId = entity.getSportId();
        if (sportId != null) {
            stmt.bindLong(4, sportId);
        }
    }

    @Override
    protected final void attachEntity(dayStep entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    @Override
    public dayStep readEntity(Cursor cursor, int offset) {
        dayStep entity = new dayStep( //
            cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // date
            cursor.getInt(offset + 2), // step
            cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3) // sportId
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, dayStep entity, int offset) {
        entity.setId(cursor.getLong(offset + 0));
        entity.setDate(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setStep(cursor.getInt(offset + 2));
        entity.setSportId(cursor.isNull(offset + 3) ? null : cursor.getLong(offset + 3));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(dayStep entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(dayStep entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(dayStep entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getSportInfoDao().getAllColumns());
            builder.append(" FROM DAY_STEP T");
            builder.append(" LEFT JOIN SPORT_INFO T0 ON T.\"SPORT_ID\"=T0.\"_id\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected dayStep loadCurrentDeep(Cursor cursor, boolean lock) {
        dayStep entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        SportInfo sportInfo = loadCurrentOther(daoSession.getSportInfoDao(), cursor, offset);
        entity.setSportInfo(sportInfo);

        return entity;    
    }

    public dayStep loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<dayStep> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<dayStep> list = new ArrayList<dayStep>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<dayStep> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<dayStep> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
