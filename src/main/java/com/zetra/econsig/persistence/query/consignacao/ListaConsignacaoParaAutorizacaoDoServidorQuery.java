package com.zetra.econsig.persistence.query.consignacao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignacaoParaAutorizacaoDoServidorQuery</p>
 * <p>Description: Listagem de consignações de um registro servidor para autorização individual
 * de cada consignação</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignacaoParaAutorizacaoDoServidorQuery extends HQuery {

    public String rseCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                "ade.adeCodigo, " +
                "ade.adeNumero, " +
                "ade.adeTipoVlr, " +
                "ade.adeVlr, " +
                "ade.adeData, " +
                "ade.adeAnoMesIni, " +
                "ade.adeAnoMesFim, " +
                "ade.adePrazo, " +
                "ade.adePrdPagas, " +
                "ade.adeCodReg, " +
                "ade.adeIndice, " +
                "ade.adePeriodicidade, " +
                "ade.adeIncMargem, " +
                "ade.adeVlrLiquido, " +
                "sad.sadCodigo, " +
                "sad.sadDescricao, " +
                "cnv.cnvCodigo, " +
                "cnv.cnvCodVerba, " +
                "svc.svcCodigo, " +
                "svc.svcIdentificador, " +
                "svc.svcDescricao, " +
                "csa.csaCodigo, " +
                "csa.csaIdentificador, " +
                "csa.csaNome, " +
                "dad.dadValor "
                ;

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append("from AutDesconto ade ");
        corpoBuilder.append("inner join ade.statusAutorizacaoDesconto sad ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("inner join vco.convenio cnv ");
        corpoBuilder.append("inner join cnv.consignataria csa ");
        corpoBuilder.append("inner join cnv.servico svc ");
        corpoBuilder.append("left join ade.dadosAutorizacaoDescontoSet dad with dad.tipoDadoAdicional.tdaCodigo = :tdaCodigo ");

        corpoBuilder.append(" where ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpoBuilder.append(" and ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigo", CodedValues.SAD_CODIGOS_ATIVOS));

        corpoBuilder.append(" order by ade.adeData, ade.adeNumero ");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("tdaCodigo", CodedValues.TDA_AUTORIZA_DESCONTO, query);
        defineValorClausulaNomeada("sadCodigo", CodedValues.SAD_CODIGOS_ATIVOS, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
                Columns.ADE_NUMERO,
                Columns.ADE_TIPO_VLR,
                Columns.ADE_VLR,
                Columns.ADE_DATA,
                Columns.ADE_ANO_MES_INI,
                Columns.ADE_ANO_MES_FIM,
                Columns.ADE_PRAZO,
                Columns.ADE_PRD_PAGAS,
                Columns.ADE_COD_REG,
                Columns.ADE_INDICE,
                Columns.ADE_PERIODICIDADE,
                Columns.ADE_INC_MARGEM,
                Columns.ADE_VLR_LIQUIDO,
                Columns.SAD_CODIGO,
                Columns.SAD_DESCRICAO,
                Columns.CNV_CODIGO,
                Columns.CNV_COD_VERBA,
                Columns.SVC_CODIGO,
                Columns.SVC_IDENTIFICADOR,
                Columns.SVC_DESCRICAO,
                Columns.CSA_CODIGO,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME,
                CodedValues.TDA_AUTORIZA_DESCONTO
        };
    }
}
