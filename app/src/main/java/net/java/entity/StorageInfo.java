package net.java.entity;

import java.io.File;

public class StorageInfo {
    private static final String TAG = StorageInfo.class.getName();

    private  String mId;
    private  int mStorageId;
    private File mPath;
    private String mPathName;
    private  String mDescription;
    private  boolean mPrimary;
    private  boolean mRemovable;
    private  boolean mEmulated;
    private  long mMtpReserveSize;
    private  boolean mAllowMassStorage;
    private long mMaxFileSize;
    private  String mFsUuid;
    private  String mState;

    public void setmId(String mId) {
        this.mId = mId;
    }

    public void setmStorageId(int mStorageId) {
        this.mStorageId = mStorageId;
    }

    public void setmPath(File mPath) {
        this.mPath = mPath;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public void setmPrimary(boolean mPrimary) {
        this.mPrimary = mPrimary;
    }

    public void setmRemovable(boolean mRemovable) {
        this.mRemovable = mRemovable;
    }

    public void setmEmulated(boolean mEmulated) {
        this.mEmulated = mEmulated;
    }

    public void setmMtpReserveSize(long mMtpReserveSize) {
        this.mMtpReserveSize = mMtpReserveSize;
    }

    public void setmAllowMassStorage(boolean mAllowMassStorage) {
        this.mAllowMassStorage = mAllowMassStorage;
    }

    public void setmMaxFileSize(long mMaxFileSize) {
        this.mMaxFileSize = mMaxFileSize;
    }

    public void setmFsUuid(String mFsUuid) {
        this.mFsUuid = mFsUuid;
    }

    public void setmState(String mState) {
        this.mState = mState;
    }

    public String getmId() {
        return mId;
    }

    public int getmStorageId() {
        return mStorageId;
    }

    public File getmPath() {
        return mPath;
    }

    public String getmDescription() {
        return mDescription;
    }

    public boolean ismPrimary() {
        return mPrimary;
    }

    public boolean ismRemovable() {
        return mRemovable;
    }

    public boolean ismEmulated() {
        return mEmulated;
    }

    public long getmMtpReserveSize() {
        return mMtpReserveSize;
    }

    public boolean ismAllowMassStorage() {
        return mAllowMassStorage;
    }

    public long getmMaxFileSize() {
        return mMaxFileSize;
    }

    public String getmFsUuid() {
        return mFsUuid;
    }

    public String getmState() {
        return mState;
    }

    public String getmPathName() {
        return mPathName;
    }

    public void setmPathName(String mPathName) {
        this.mPathName = mPathName;
    }
}
