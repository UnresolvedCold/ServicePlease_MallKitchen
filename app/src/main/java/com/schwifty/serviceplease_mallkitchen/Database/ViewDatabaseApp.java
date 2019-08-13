package com.schwifty.serviceplease_mallkitchen.Database;

import android.app.Application;

public class ViewDatabaseApp extends Application
{
    private DaoSession daoSession;

    private DaoSession resDao;

    @Override
    public void onCreate() {
        super.onCreate();

        daoSession =
                new DaoMaster(new ViewDatabaseHelper(this, "ViewsDatabase.db").getWritableDb()).newSession();

        resDao = new DaoMaster(new ViewDatabaseHelper(this,"Restaurant.db").getWritableDb()).newSession();

    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public DaoSession getResDao(){return resDao;}

    public void setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
    }
}
