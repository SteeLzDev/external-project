package com.zetra.econsig.persistence.query.consignacao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignacaoSemParcelaQuery</p>
 * <p>Description: Listagem de Consignações que não possuam parcelas geradas</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignacaoSemParcelaQuery extends HQuery {

    public List<Long> adeNumero;
    public String rseCodigo;
    public boolean count = false;
    public String orgCodigo;
    public String csaCodigo;
    public String corCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();


        if(count) {
            corpoBuilder.append("select count(*)  ");
        } else {
            corpoBuilder.append("SELECT  ");
            corpoBuilder.append("ade.adeCodigo, ");
            corpoBuilder.append("ade.adeNumero, ");
            corpoBuilder.append("ade.adeIdentificador, ");
            corpoBuilder.append("ade.adeData, ");
            corpoBuilder.append("ade.adeVlr, ");
            corpoBuilder.append("ade.adePrazo, ");
            corpoBuilder.append("ade.adePrdPagas, ");
            corpoBuilder.append("csa.csaNome, ");
            corpoBuilder.append("csa.csaCodigo, ");
            corpoBuilder.append("csa.csaNomeAbrev, ");
            corpoBuilder.append("csa.csaIdentificador, ");
            corpoBuilder.append("usu.usuLogin, ");
            corpoBuilder.append("svc.svcCodigo, ");
            corpoBuilder.append("svc.svcIdentificador, ");
            corpoBuilder.append("svc.svcDescricao, ");
            corpoBuilder.append("ser.serNome, ");
            corpoBuilder.append("ser.serCpf, ");
            corpoBuilder.append("ser.serTel, ");
            corpoBuilder.append("ser.serEmail, ");
            corpoBuilder.append("sad.sadCodigo, ");
            corpoBuilder.append("sad.sadDescricao, ");
            corpoBuilder.append("rse.rseCodigo, ");
            corpoBuilder.append("ade.adeDataNotificacaoCse, ");
            corpoBuilder.append("ade.adeDataLiberacaoValor ");
        }
        corpoBuilder.append("FROM AutDesconto ade ");
        corpoBuilder.append("INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append("INNER JOIN vco.convenio cnv ");
        corpoBuilder.append("INNER JOIN cnv.consignataria csa ");
        corpoBuilder.append("INNER JOIN ade.usuario usu ");
        corpoBuilder.append("INNER JOIN cnv.servico svc ");
        corpoBuilder.append("INNER JOIN ade.registroServidor rse ");
        corpoBuilder.append("INNER JOIN rse.servidor ser ");
        corpoBuilder.append("INNER JOIN ade.statusAutorizacaoDesconto sad ");
        if (!TextHelper.isNull(corCodigo)) {
            corpoBuilder.append("INNER JOIN cnv.correspondenteConvenioSet cor ");
        }

        corpoBuilder.append("WHERE ade.adeDataNotificacaoCse IS NOT NULL ");

        corpoBuilder.append("AND (ade.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_AGUARD_DEFER).append("' ");

        corpoBuilder.append("or (ade.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_DEFERIDA).append("' ");

        corpoBuilder.append("AND NOT EXISTS (select prd.adeCodigo from ParcelaDesconto prd where ade.adeCodigo = prd.adeCodigo))) ");

        if (adeNumero != null && !adeNumero.isEmpty()) {
            corpoBuilder.append(" AND ade.adeNumero ").append(criaClausulaNomeada("adeNumero", adeNumero));
        }

        if (!TextHelper.isNull(rseCodigo)) {
            corpoBuilder.append(" AND rse.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        }

        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" AND cnv.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND cnv.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(corCodigo)) {
            corpoBuilder.append(" AND cor.corCodigo ").append(criaClausulaNomeada("corCodigo", corCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (adeNumero != null && adeNumero.size() > 0) {
            defineValorClausulaNomeada("adeNumero", adeNumero, query);
        }

        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }

        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(corCodigo)) {
            defineValorClausulaNomeada("corCodigo", corCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
                Columns.ADE_NUMERO,
                Columns.ADE_IDENTIFICADOR,
                Columns.ADE_DATA,
                Columns.ADE_VLR,
                Columns.ADE_PRAZO,
                Columns.ADE_PRD_PAGAS,
                Columns.CSA_NOME,
                Columns.CSA_CODIGO,
                Columns.CSA_NOME_ABREV,
                Columns.CSA_IDENTIFICADOR,
                Columns.USU_LOGIN,
                Columns.SVC_CODIGO,
                Columns.SVC_IDENTIFICADOR,
                Columns.SVC_DESCRICAO,
                Columns.SER_NOME,
                Columns.SER_CPF,
                Columns.SER_TEL,
                Columns.SER_EMAIL,
                Columns.SAD_CODIGO,
                Columns.SAD_DESCRICAO,
                Columns.RSE_CODIGO,
                Columns.ADE_DATA_NOTIFICACAO_CSE,
                Columns.ADE_DATA_LIBERACAO_VALOR
        };
    }
}
