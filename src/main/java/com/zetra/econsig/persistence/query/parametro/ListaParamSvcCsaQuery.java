package com.zetra.econsig.persistence.query.parametro;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaParamSvcCsaQuery</p>
 * <p>Description: Lista parâmetros de serviços de um Consignatária.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaParamSvcCsaQuery extends HQuery {
    public List<String> svcCodigos;
    public List<String> csaCodigos;
    public List<String> tpsCodigos;
    public boolean ativo;
    public boolean dataIniVigIndiferente;
    public String csaIdentificadorInterno;
    public String pscVlr;
    public String pscVlrDiferente = null;
    public String pscVlrRefDiferente = null;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "SELECT " +
                       "psc.tipoParamSvc.tpsDescricao, " +
                       "psc.tipoParamSvc.tpsCodigo, " +
                       "psc.pscVlr, " +
                       "psc.pscVlrRef, " +
                       "psc.pscDataIniVig, " +
                       "psc.pscDataFimVig, " +
                       "psc.consignataria.csaCodigo, " +
                       "psc.consignataria.csaIdentificadorInterno, " +
                       "psc.servico.svcCodigo, " +
                       "psc.pscCodigo, " +
                       "psc.servico.svcDescricao, " +
                       "psc.servico.svcIdentificador ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" FROM ParamSvcConsignataria psc ");
        corpoBuilder.append(" WHERE 1 = 1 ");

        if (!TextHelper.isNull(pscVlr)) {
            corpoBuilder.append(" AND psc.pscVlr ").append(criaClausulaNomeada("pscVlr", pscVlr));
        }

        if (pscVlrDiferente != null || pscVlrRefDiferente != null) {
            corpoBuilder.append(" AND ( 1 = 2 ");
            if (pscVlrDiferente != null) {
                corpoBuilder.append(" OR (psc.pscVlr ").append(criaClausulaNomeada("pscVlrDiferente", CodedValues.NOT_EQUAL_KEY + pscVlrDiferente));
                corpoBuilder.append(" OR psc.pscVlr IS NULL) ");
            }
            if (pscVlrRefDiferente != null) {
                corpoBuilder.append(" OR psc.pscVlrRef ").append(criaClausulaNomeada("pscVlrRefDiferente", CodedValues.NOT_EQUAL_KEY + pscVlrRefDiferente));
            }
            corpoBuilder.append(") ");
        }

        if (svcCodigos != null && !svcCodigos.isEmpty()) {
            corpoBuilder.append(" AND psc.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigos));
        }

        if (csaCodigos != null && !csaCodigos.isEmpty()) {
            corpoBuilder.append(" AND psc.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigos));
        }

        if (!TextHelper.isNull(csaIdentificadorInterno)) {
            corpoBuilder.append(" AND psc.consignataria.csaIdentificadorInterno ").append(criaClausulaNomeada("csaIdentificadorInterno", csaIdentificadorInterno));
        }

        if (tpsCodigos != null && !tpsCodigos.isEmpty()) {
            corpoBuilder.append(" AND psc.tipoParamSvc.tpsCodigo ").append(criaClausulaNomeada("tpsCodigo", tpsCodigos));
        }

        if (ativo) {
            corpoBuilder.append(" AND psc.pscDataIniVig <= current_date() ");
            corpoBuilder.append(" AND (psc.pscDataFimVig >= current_date() ");
            corpoBuilder.append(" OR psc.pscDataFimVig IS NULL)");
            corpoBuilder.append(" ORDER BY psc.consignataria.csaCodigo");

        } else {
            if (!dataIniVigIndiferente) {
                corpoBuilder.append(" AND psc.pscDataIniVig IS NULL");
            }
            corpoBuilder.append(" AND psc.pscDataFimVig IS NULL");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(pscVlr)) {
            defineValorClausulaNomeada("pscVlr", pscVlr, query);
        }

        if (svcCodigos != null && !svcCodigos.isEmpty()) {
            defineValorClausulaNomeada("svcCodigo", svcCodigos, query);
        }

        if (csaCodigos != null && !csaCodigos.isEmpty()) {
            defineValorClausulaNomeada("csaCodigo", csaCodigos, query);
        }

        if (!TextHelper.isNull(csaIdentificadorInterno)) {
            defineValorClausulaNomeada("csaIdentificadorInterno", csaIdentificadorInterno, query);
        }

        if (tpsCodigos != null && !tpsCodigos.isEmpty()) {
            defineValorClausulaNomeada("tpsCodigo", tpsCodigos, query);
        }

        if (pscVlrDiferente != null) {
            defineValorClausulaNomeada("pscVlrDiferente", pscVlrDiferente, query);
        }
        if (pscVlrRefDiferente != null) {
            defineValorClausulaNomeada("pscVlrRefDiferente", pscVlrRefDiferente, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TPS_DESCRICAO,
                Columns.TPS_CODIGO,
                Columns.PSC_VLR,
                Columns.PSC_VLR_REF,
                Columns.PSC_DATA_INI_VIG,
                Columns.PSC_DATA_FIM_VIG,
                Columns.PSC_CSA_CODIGO,
                Columns.CSA_IDENTIFICADOR_INTERNO,
                Columns.PSC_SVC_CODIGO,
                Columns.PSC_CODIGO,
                Columns.SVC_DESCRICAO,
                Columns.SVC_IDENTIFICADOR
        };
    }
}