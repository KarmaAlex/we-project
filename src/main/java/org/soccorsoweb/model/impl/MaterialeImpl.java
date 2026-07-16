package org.soccorsoweb.model.impl;

import org.soccorsoweb.data.DataItemImpl;
import org.soccorsoweb.model.Materiale;

public class MaterialeImpl extends DataItemImpl<Integer> implements Materiale {

    private String nome;
    private String desc;
    private String codMat;
    private Integer missioneKey; // null se non assegnato a nessuna missione
    private long version;

    public MaterialeImpl() {
        super();
        this.nome = "";
        this.desc = "";
        this.codMat = "";
        this.missioneKey = null;
        this.version = 0L;
    }

    @Override
    public String getNome() {
        return nome;
    }

    @Override
    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    @Override
    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String getCodMat() {
        return codMat;
    }

    @Override
    public void setCodMat(String codMat) {
        this.codMat = codMat;
    }

    @Override
    public Integer getMissioneKey() {
        return missioneKey;
    }

    @Override
    public void setMissioneKey(Integer missioneKey) {
        this.missioneKey = missioneKey;
    }

    @Override
    public boolean isAssegnato() {
        return this.missioneKey != null;
    }

    @Override
    public long getVersion() {
        return version;
    }

    @Override
    public void setVersion(long version) {
        this.version = version;
    }
}