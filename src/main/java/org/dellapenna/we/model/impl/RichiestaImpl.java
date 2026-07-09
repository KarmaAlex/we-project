package org.dellapenna.we.model.impl;

import org.dellapenna.we.data.DataItemImpl;
import org.dellapenna.we.model.Richiesta;
import org.dellapenna.we.model.DescRichiesta;
import org.dellapenna.we.model.enums.StatoRichiesta;
import java.time.LocalDateTime;

public class RichiestaImpl extends DataItemImpl<Integer> implements Richiesta {

    private String nome;
    private String email;
    private String ip;
    private StatoRichiesta stato;
    private String string;
    private boolean verificato;
    private LocalDateTime data;
    private DescRichiesta descrizioneDettaglio;

    public RichiestaImpl() {
        super();
        this.nome = "";
        this.email = "";
        this.ip = "";
        this.stato = StatoRichiesta.IN_ATTESA; // Default impostato sull'enum coerente
        this.string = "";
        this.verificato = false;
        this.data = null;
        this.descrizioneDettaglio = null;
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
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getIP() {
        return ip;
    }

    @Override
    public void setIP(String ip) {
        this.ip = ip;
    }

    @Override
    public StatoRichiesta getStato() {
        return stato;
    }

    @Override
    public void setStato(StatoRichiesta stato) {
        this.stato = stato;
    }

    @Override
    public String getString() {
        return string;
    }

    @Override
    public void setString(String string) {
        this.string = string;
    }

    @Override
    public boolean isVerificato() {
        return verificato;
    }

    @Override
    public void setVerificato(boolean verificato) {
        this.verificato = verificato;
    }

    @Override
    public LocalDateTime getData() {
        return data;
    }

    @Override
    public void setData(LocalDateTime data) {
        this.data = data;
    }

    @Override
    public DescRichiesta getDescrizioneDettaglio() {
        return descrizioneDettaglio;
    }

    @Override
    public void setDescrizioneDettaglio(DescRichiesta descrizioneDettaglio) {
        this.descrizioneDettaglio = descrizioneDettaglio;
    }
}