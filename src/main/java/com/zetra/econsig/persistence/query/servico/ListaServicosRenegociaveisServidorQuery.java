package com.zetra.econsig.persistence.query.servico;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaServicosRenegociaveisServidorQuery</p>
 * <p>Description: Listagem de serviços com coeficientes ativos para os quais o servidor pode solicitar renegociação.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaServicosRenegociaveisServidorQuery extends HQuery {

    private static final String TRUE = "1";
    public String orgCodigo;
    public String csaCodigo;
    public String svcCodigo;
    public String nseCodigo;
    public boolean ativos = true;
    public Short cftDia;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        final String corpo =
                "select svc.svcCodigo, " +
                        "   svc.svcIdentificador, " +
                        "   svc.svcDescricao ";

        final StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append("from Servico svc ");
        corpoBuilder.append(" INNER JOIN svc.convenioSet cnv ");
        corpoBuilder.append(" INNER JOIN cnv.consignataria csa ");
        corpoBuilder.append(" INNER JOIN cnv.orgao org ");
        corpoBuilder.append(" INNER JOIN svc.prazoSet prz");
        corpoBuilder.append(" INNER JOIN prz.prazoConsignatariaSet pzc");
        corpoBuilder.append(" INNER JOIN pzc.coeficienteAtivoSet cft");
        corpoBuilder.append(" INNER JOIN svc.paramSvcConsignanteSet pse WITH ");
        corpoBuilder.append("pse.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_PERMITE_SERVIDOR_SOLICITAR).append("'");
        corpoBuilder.append(" where ");

        corpoBuilder.append(" exists (select 1 from RelacionamentoServico rsv where rsv.servicoBySvcCodigoOrigem.svcCodigo = svc.svcCodigo)");

        corpoBuilder.append(" and pse.pseVlr = '").append(TRUE).append("'");

        if (ativos) {
            corpoBuilder.append(" and cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
            corpoBuilder.append(" and (svc.svcAtivo IS NULL OR svc.svcAtivo = ").append(CodedValues.STS_ATIVO).append(")");
            corpoBuilder.append(" and (csa.csaAtivo IS NULL OR csa.csaAtivo = ").append(CodedValues.STS_ATIVO).append(")");
            corpoBuilder.append(" and (org.orgAtivo IS NULL OR org.orgAtivo = ").append(CodedValues.STS_ATIVO).append(")");
        }

        corpoBuilder.append(" AND (prz.przAtivo = ").append(CodedValues.STS_ATIVO);
        corpoBuilder.append(" OR prz.przAtivo IS NULL)");
        corpoBuilder.append(" AND (pzc.przCsaAtivo = ").append(CodedValues.STS_ATIVO);
        corpoBuilder.append(" OR pzc.przCsaAtivo IS NULL)");

        // Taxas ativas (data inicial anterior e final superior a atual)
        corpoBuilder.append(" AND cft.cftDataIniVig <= current_date()");
        corpoBuilder.append(" AND (cft.cftDataFimVig > current_date() OR cft.cftDataFimVig IS NULL)");

        // Com valor maior que zero
        corpoBuilder.append(" AND cft.cftVlr > 0.000000");

        // Para o dia atual ou para o mês
        corpoBuilder.append(" AND (cft.cftDia ").append(criaClausulaNomeada("cftDia", cftDia));
        corpoBuilder.append(" OR cft.cftDia = 0)");

        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" and org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }
        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(nseCodigo)) {
            corpoBuilder.append(" and svc.naturezaServico.nseCodigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        }
        if (!TextHelper.isNull(svcCodigo)) {
            corpoBuilder.append(" and svc.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }

        corpoBuilder.append(" GROUP BY svc.svcCodigo, svc.svcDescricao, svc.svcIdentificador");
        corpoBuilder.append(" ORDER BY svc.svcDescricao");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
        if (!TextHelper.isNull(nseCodigo)) {
            defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
        }
        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }
        if (!TextHelper.isNull(cftDia)) {
            defineValorClausulaNomeada("cftDia", cftDia, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SVC_CODIGO,
                Columns.SVC_IDENTIFICADOR,
                Columns.SVC_DESCRICAO
        };
    }
}

