package com.zetra.econsig.persistence.query.prazo;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaPrazoCoeficienteQuery</p>
 * <p>Description: Seleciona os prazos que possuem coeficientes cadastrados.
 * Se svcCodigo é nulo, então retorna os prazos que possuem coeficientes
 * para todos os serviços. Se csaCodigo é nulo, então retorna todos os
 * prazos com coeficientes cadastrados para um serviço específico.
 * Se ambos são nulos, retorna os serviços que possuem prazos e que possuem
 * coeficientes cadastrados. Se orgCodigo é nulo não verifica se o convênio
 * também está ativo.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaPrazoCoeficienteQuery extends HQuery {

    public String svcCodigo;
    public String csaCodigo;
    public String orgCodigo;
    public int dia;
    public boolean validaBloqSerCnvCsa = true;
    public boolean validaLimitePrazo = false;
    public boolean validaPrazoRenegociacao = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = null;

        if ((svcCodigo != null) && (csaCodigo != null)) {
            corpo = "select distinct " +
                    "cnv.cnvCodigo, " +
                    "prz.przVlr";

        } else if (svcCodigo != null) {
            corpo = "select distinct " +
                    "svc.svcCodigo, " +
                    "prz.przVlr";

        } else if (csaCodigo != null) {
            if (orgCodigo != null) {
                corpo = "select " +
                        "prz.przVlr, " +
                        "svc.svcCodigo, " +
                        "svc.svcDescricao, " +
                        "svc.svcIdentificador, " +
                        "cnv.cnvCodigo";
            } else {
                corpo = "select " +
                        "prz.przVlr, " +
                        "svc.svcCodigo, " +
                        "svc.svcDescricao, " +
                        "svc.svcIdentificador";
            }
        } else {
            corpo = "select distinct " +
                    "svc.svcCodigo, " +
                    "svc.svcDescricao, " +
                    "svc.svcIdentificador";
        }

        final StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" FROM Servico svc");
        corpoBuilder.append(" INNER JOIN svc.prazoSet prz");
        corpoBuilder.append(" INNER JOIN prz.prazoConsignatariaSet pzc");
        corpoBuilder.append(" INNER JOIN pzc.consignataria csa");
        corpoBuilder.append(" INNER JOIN pzc.coeficienteAtivoSet cft");

        if (orgCodigo != null) {
            corpoBuilder.append(" INNER JOIN svc.convenioSet cnv");
        }

        String tpsMaxPrazo = null;
        if (validaPrazoRenegociacao) {
            tpsMaxPrazo = CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_PORTABILIDADE;
        } else if (validaLimitePrazo) {
            tpsMaxPrazo = CodedValues.TPS_MAX_PRAZO;
        }
        if (validaLimitePrazo || validaPrazoRenegociacao) {
            corpoBuilder.append(" LEFT OUTER JOIN svc.paramSvcConsignanteSet pseMaxPrazo ");
            corpoBuilder.append(" WITH pseMaxPrazo.tipoParamSvc.tpsCodigo = '").append(tpsMaxPrazo).append("'");
        }

        corpoBuilder.append(" WHERE (cft.cftDataIniVig <= current_date())");
        corpoBuilder.append(" AND (cft.cftDataFimVig >= current_date() OR cft.cftDataFimVig IS NULL)");
        corpoBuilder.append(" AND (svc.svcAtivo = ").append(CodedValues.STS_ATIVO).append(" OR svc.svcAtivo IS NULL)");

        if (validaBloqSerCnvCsa) {
            corpoBuilder.append(" AND (csa.csaAtivo = ").append(CodedValues.STS_ATIVO).append(" OR csa.csaAtivo IS NULL)");
        }

        corpoBuilder.append(" AND (prz.przAtivo = ").append(CodedValues.STS_ATIVO).append(" OR prz.przAtivo IS NULL)");
        corpoBuilder.append(" AND (pzc.przCsaAtivo = ").append(CodedValues.STS_ATIVO).append(" OR pzc.przCsaAtivo IS NULL)");

        if (svcCodigo != null) {
            corpoBuilder.append(" AND svc.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }
        if (csaCodigo != null) {
            corpoBuilder.append(" AND csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        if (orgCodigo != null) {
            corpoBuilder.append(" AND cnv.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
            corpoBuilder.append(" AND cnv.consignataria.csaCodigo = csa.csaCodigo ");
            corpoBuilder.append(" AND cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        }
        corpoBuilder.append(" AND cft.cftVlr > 0.000000 ");
        corpoBuilder.append(" AND (cft.cftDia ").append(criaClausulaNomeada("dia", dia));
        corpoBuilder.append(" OR cft.cftDia = 0)");

        if (validaLimitePrazo || validaPrazoRenegociacao) {
            corpoBuilder.append(" AND (NULLIF(TRIM(pseMaxPrazo.pseVlr), '') IS NULL OR prz.przVlr <= TO_NUMERIC(COALESCE(NULLIF(TRIM(pseMaxPrazo.pseVlr), ''), '0'))) ");
        }

        if (svcCodigo != null) {
            corpoBuilder.append(" ORDER BY prz.przVlr");

        } else if (csaCodigo != null) {
            corpoBuilder.append(" ORDER BY svc.svcIdentificador, prz.przVlr");

        } else {
            corpoBuilder.append(" ORDER BY svc.svcIdentificador");
        }

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("dia", (short)dia, query);

        if (svcCodigo != null) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }
        if (csaCodigo != null) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
        if (orgCodigo != null) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        if ((svcCodigo != null) && (csaCodigo != null)) {
            return new String[] {
                    Columns.CNV_CODIGO,
                    Columns.PRZ_VLR};

        } else if (svcCodigo != null) {
            return new String[] {
                    Columns.SVC_CODIGO,
                    Columns.PRZ_VLR};

        } else if (csaCodigo != null) {
            if (orgCodigo != null) {
                return new String[] {
                        Columns.PRZ_VLR,
                        Columns.SVC_CODIGO,
                        Columns.SVC_DESCRICAO,
                        Columns.SVC_IDENTIFICADOR,
                        Columns.CNV_CODIGO};
            } else {
                return new String[] {
                        Columns.PRZ_VLR,
                        Columns.SVC_CODIGO,
                        Columns.SVC_DESCRICAO,
                        Columns.SVC_IDENTIFICADOR};
            }
        } else {
            return new String[] {
                    Columns.SVC_CODIGO,
                    Columns.SVC_DESCRICAO,
                    Columns.SVC_IDENTIFICADOR};
        }
    }
}
