package com.zetra.econsig.persistence.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: HistoricoIntegracaoBeneficioHome</p>
 * <p>Description: Classe Home do HistIntegracaoBeneficio.</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HistoricoIntegracaoBeneficioHome extends AbstractEntityHome {

    public static HistIntegracaoBeneficio findByPrimaryKey(String hibCodigo) throws FindException {
        HistIntegracaoBeneficio histIntegracaoBeneficio = new HistIntegracaoBeneficio();
        histIntegracaoBeneficio.setHibCodigo(hibCodigo);
        return find(histIntegracaoBeneficio, hibCodigo);
    }

    public static HistIntegracaoBeneficio obtemUltimoDataHistoricoIntegracaoExportacao(String csaCodigo) throws FindException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT h FROM HistIntegracaoBeneficio h where h.hibDataFim = ");
        sql.append(" ( select max(hh.hibDataFim) from HistIntegracaoBeneficio hh where hh.hibTipo = :hibTipo and hh.consignataria.csaCodigo = :csaCodigo ) ");
        sql.append("and h.hibTipo = :hibTipo and h.consignataria.csaCodigo = :csaCodigo");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("csaCodigo", csaCodigo);
        parameters.put("hibTipo", CodedValues.HIB_TIPO_EXPORTACAO);

        List<HistIntegracaoBeneficio> listaHistorico = findByQuery(sql.toString(), parameters);

        if (listaHistorico == null || listaHistorico.size() == 0) {
            return null;
        } else {
            return listaHistorico.get(0);
        }
    }

    public static HistIntegracaoBeneficio create(String csaCodigo, String usuCodigo, Date hibDataIni, Date hibDataFim, Date hibData, String hibTipo) throws CreateException {

        Session session = SessionUtil.getSession();

        try {
            HistIntegracaoBeneficio HistIntegracaoBeneficio = new HistIntegracaoBeneficio();
            HistIntegracaoBeneficio.setHibCodigo(DBHelper.getNextId());
            HistIntegracaoBeneficio.setConsignataria(session.getReference(Consignataria.class, csaCodigo));
            HistIntegracaoBeneficio.setUsuario(session.getReference(Usuario.class, usuCodigo));
            HistIntegracaoBeneficio.setHibDataIni(hibDataIni);
            HistIntegracaoBeneficio.setHibDataFim(hibDataFim);

            if (hibData == null) {
                hibData = new Date();
            }

            HistIntegracaoBeneficio.setHibData(hibData);
            HistIntegracaoBeneficio.setHibTipo(hibTipo);

            return create(HistIntegracaoBeneficio);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }
}
