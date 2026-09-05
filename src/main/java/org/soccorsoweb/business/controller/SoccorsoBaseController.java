package org.soccorsoweb.business.controller;

import jakarta.servlet.ServletException;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.framework.controller.AbstractBaseController;

import org.soccorsoweb.data.dao.impl.UtenteDAO_MySQL;
import org.soccorsoweb.data.dao.impl.RichiestaDAO_MySQL;
import org.soccorsoweb.data.dao.impl.MaterialeDAO_MySQL;
import org.soccorsoweb.data.dao.impl.MezzoDAO_MySQL;
import org.soccorsoweb.data.dao.impl.SquadraDAO_MySQL;
import org.soccorsoweb.data.dao.impl.MissioneDAO_MySQL;
import org.soccorsoweb.data.dao.impl.AnagraficaDAO_MySQL;
import org.soccorsoweb.data.dao.impl.PatenteDAO_MySQL;
import org.soccorsoweb.data.dao.impl.AbilitaDAO_MySQL;
import org.soccorsoweb.data.dao.impl.CredenzialiDAO_MySQL;
import org.soccorsoweb.data.dao.impl.DescRichiestaDAO_MySQL;
import org.soccorsoweb.data.dao.impl.AggiornamentoDAO_MySQL;
import org.soccorsoweb.data.dao.impl.CommentoDAO_MySQL;

import org.soccorsoweb.model.Utente;

import freemarker.template.Configuration;

import org.soccorsoweb.model.Richiesta;
import org.soccorsoweb.model.Materiale;
import org.soccorsoweb.model.Mezzo;
import org.soccorsoweb.model.Squadra;
import org.soccorsoweb.model.Missione;
import org.soccorsoweb.model.Anagrafica;
import org.soccorsoweb.model.Patente;
import org.soccorsoweb.model.Abilita;
import org.soccorsoweb.model.Credenziali;
import org.soccorsoweb.model.DescRichiesta;
import org.soccorsoweb.model.Aggiornamento;
import org.soccorsoweb.model.Commento;

public abstract class SoccorsoBaseController extends AbstractBaseController {
    protected Configuration cfg;

	@Override
	public void init() throws ServletException {
		super.init();
		cfg = new Configuration(Configuration.VERSION_2_3_34);
		cfg.setServletContextForTemplateLoading(getServletContext(), "/templates");
		cfg.setDefaultEncoding("UTF-8");
	}

    @Override
    protected DataLayer createDataLayer(DataSource ds) throws ServletException {
        try {
            return new DataLayer(ds) {
                @Override
                public void init() throws DataException {
                    registerDAO(Utente.class, new UtenteDAO_MySQL(this));
                    registerDAO(Richiesta.class, new RichiestaDAO_MySQL(this));
                    registerDAO(Materiale.class, new MaterialeDAO_MySQL(this));
                    registerDAO(Mezzo.class, new MezzoDAO_MySQL(this));
                    registerDAO(Squadra.class, new SquadraDAO_MySQL(this));
                    registerDAO(Missione.class, new MissioneDAO_MySQL(this));
                    registerDAO(Anagrafica.class, new AnagraficaDAO_MySQL(this));
                    registerDAO(Patente.class, new PatenteDAO_MySQL(this));
                    registerDAO(Abilita.class, new AbilitaDAO_MySQL(this));
                    registerDAO(Credenziali.class, new CredenzialiDAO_MySQL(this));
                    registerDAO(DescRichiesta.class, new DescRichiestaDAO_MySQL(this));
                    registerDAO(Aggiornamento.class, new AggiornamentoDAO_MySQL(this));
                    registerDAO(Commento.class, new CommentoDAO_MySQL(this));
                }
            };
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }
}