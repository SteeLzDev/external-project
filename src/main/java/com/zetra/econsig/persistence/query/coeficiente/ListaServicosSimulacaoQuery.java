package com.zetra.econsig.persistence.query.coeficiente;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaServicosSimulacaoQuery</p>
 * <p>Description: Listagem de Serviços que podem ser simulados.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaServicosSimulacaoQuery extends HQuery {

    public String csaCodigo;
    public String svcCodigo;
    public String orgCodigo;
    public short dia;
    public boolean usaDefinicaoTaxaJuros;
    public String corCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select svc.svcCodigo, svc.svcDescricao, svc.svcIdentificador, svc.naturezaServico.nseCodigo");
        corpoBuilder.append(" FROM Servico svc");
        corpoBuilder.append(" WHERE (svc.svcAtivo = ").append(CodedValues.STS_ATIVO);
        corpoBuilder.append(" OR svc.svcAtivo IS NULL)");

        if (usaDefinicaoTaxaJuros) {
            corpoBuilder.append(" AND (EXISTS (");
            corpoBuilder.append(" SELECT 1 FROM svc.definicaoTaxaJurosSet dtj");
            corpoBuilder.append(" WHERE (dtj.dtjDataVigenciaFim IS NULL)");
            corpoBuilder.append(")");
            corpoBuilder.append(" OR ");
        } else {
            corpoBuilder.append(" AND ");
        }

        corpoBuilder.append(" EXISTS (");
        corpoBuilder.append(" SELECT 1 FROM svc.prazoSet prz");
        corpoBuilder.append(" INNER JOIN prz.prazoConsignatariaSet pzc");
        corpoBuilder.append(" INNER JOIN pzc.coeficienteAtivoSet cft");
        corpoBuilder.append(" INNER JOIN pzc.consignataria csa");
        corpoBuilder.append(" INNER JOIN csa.convenioSet cnv");
        if (!TextHelper.isNull(corCodigo)) {
            corpoBuilder.append(" INNER JOIN cnv.correspondenteConvenioSet corCnv");
        }
        corpoBuilder.append(" WHERE svc.svcCodigo = cnv.servico.svcCodigo");
        if (!TextHelper.isNull(corCodigo)) {
            corpoBuilder.append(" AND corCnv.correspondente.corCodigo ").append(criaClausulaNomeada("corCodigo", corCodigo));
            corpoBuilder.append(" AND corCnv.statusConvenio.scvCodigo = '").append(CodedValues.STS_ATIVO).append("'");
        } else {
            corpoBuilder.append(" AND cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        }
        corpoBuilder.append(" AND (csa.csaAtivo = ").append(CodedValues.STS_ATIVO);
        corpoBuilder.append(" OR csa.csaAtivo IS NULL)");
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
        corpoBuilder.append(" AND (cft.cftDia ").append(criaClausulaNomeada("cftDia", dia));
        corpoBuilder.append(" OR cft.cftDia = 0)");


        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" AND cnv.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }
        corpoBuilder.append(")");

        if (usaDefinicaoTaxaJuros) {
            corpoBuilder.append(")");
        }

        if (!TextHelper.isNull(svcCodigo)) {
            corpoBuilder.append(" AND svc.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }

        corpoBuilder.append(" ORDER BY svc.svcDescricao");

        // Define os valores para os parâmetros nomeados
        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("cftDia", dia, query);

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }
        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }
        if (!TextHelper.isNull(corCodigo)) {
            defineValorClausulaNomeada("corCodigo", corCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SVC_CODIGO,
                Columns.SVC_DESCRICAO,
                Columns.SVC_IDENTIFICADOR,
                Columns.SVC_NSE_CODIGO
        };
    }
}
