package com.schwifty.serviceplease_mallkitchen.Database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

@Entity
public class ResDatabase
{
    @Id
    private Long Id;

    @Property
    private String ResId;

    @Property
    private String Secret;

    @Property
    private String nTables;

    @Generated(hash = 855319800)
    public ResDatabase(Long Id, String ResId, String Secret, String nTables) {
        this.Id = Id;
        this.ResId = ResId;
        this.Secret = Secret;
        this.nTables = nTables;
    }

    @Generated(hash = 1205263757)
    public ResDatabase() {
    }

    public Long getId() {
        return this.Id;
    }

    public void setId(Long Id) {
        this.Id = Id;
    }

    public String getResId() {
        return this.ResId;
    }

    public void setResId(String ResId) {
        this.ResId = ResId;
    }

    public String getSecret() {
        return this.Secret;
    }

    public void setSecret(String Secret) {
        this.Secret = Secret;
    }

    public String getNTables() {
        return this.nTables;
    }

    public void setNTables(String nTables) {
        this.nTables = nTables;
    }


}
