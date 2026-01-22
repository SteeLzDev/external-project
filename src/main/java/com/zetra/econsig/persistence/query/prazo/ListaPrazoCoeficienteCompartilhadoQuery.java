package com.zetra.econsig.persistence.query.prazo;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaPrazoCoeficienteCompartilhadoQuery</p>
 * <p>Description: Listagem os coeficientes do serviço de origem do relacionamento de compartilhamento de taxas do serviço dado.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaPrazoCoeficienteCompartilhadoQuery extends HQuery {

    public String svcCodigoDestino;
    public String csaCodigo;
    public String orgCodigo;
    public int dia;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = null;

        if ((svcCodigoDestino != null) && (csaCodigo != null)) {
            corpo = "select distinct " +
                    "cnv.cnvCodigo, " +
                    "prz.przVlr";

        } else if (svcCodigoDestino != null) {
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
        corpoBuilder.append(" INNER JOIN svc.relacionamentoServicoByDestinoSet rel");
        corpoBuilder.append(" INNER JOIN rel.servicoBySvcCodigoOrigem svcOrigem");
        corpoBuilder.append(" INNER JOIN rel.tipoNatureza tnt");
        corpoBuilder.append(" INNER JOIN svcOrigem.prazoSet prz");
        corpoBuilder.append(" INNER JOIN prz.prazoConsignatariaSet pzc");
        corpoBuilder.append(" INNER JOIN pzc.consignataria csa");
        corpoBuilder.append(" INNER JOIN pzc.coeficienteAtivoSet cft");

        if (orgCodigo != null) {
            corpoBuilder.append(" INNER JOIN svc.convenioSet cnv");
        }

        corpoBuilder.append(" WHERE (cft.cftDataIniVig <= current_date())");
        corpoBuilder.append(" AND tnt.tntCodigo = '").append(CodedValues.TNT_COMPARTILHA_CADASTRO_TAXAS).append("'");
        corpoBuilder.append(" AND (cft.cftDataFimVig >= current_date() OR cft.cftDataFimVig IS NULL)");
        corpoBuilder.append(" AND (svc.svcAtivo = ").append(CodedValues.STS_ATIVO).append(" OR svc.svcAtivo IS NULL)");
        corpoBuilder.append(" AND (csa.csaAtivo = ").append(CodedValues.STS_ATIVO).append(" OR csa.csaAtivo IS NULL)");
        corpoBuilder.append(" AND (prz.przAtivo = ").append(CodedValues.STS_ATIVO).append(" OR prz.przAtivo IS NULL)");
        corpoBuilder.append(" AND (pzc.przCsaAtivo = ").append(CodedValues.STS_ATIVO).append(" OR pzc.przCsaAtivo IS NULL)");

        if (svcCodigoDestino != null) {
            corpoBuilder.append(" AND svc.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigoDestino));
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
        corpoBuilder.append(" AND (cft.cftDia = ").append(dia);
        corpoBuilder.append(" OR cft.cftDia = 0)");

        if (svcCodigoDestino != null) {
            corpoBuilder.append(" ORDER BY prz.przVlr");

        } else if (csaCodigo != null) {
            corpoBuilder.append(" ORDER BY svc.svcIdentificador, prz.przVlr");

        } else {
            corpoBuilder.append(" ORDER BY svc.svcIdentificador");
        }

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (svcCodigoDestino != null) {
            defineValorClausulaNomeada("svcCodigo", svcCodigoDestino, query);
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
        if ((svcCodigoDestino != null) && (csaCodigo != null)) {
            return new String[] {
                    Columns.CNV_CODIGO,
                    Columns.PRZ_VLR};

        } else if (svcCodigoDestino != null) {
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
