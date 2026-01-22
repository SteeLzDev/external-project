package com.zetra.econsig.persistence.query.beneficios.contrato;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarContratosMensalidadeBeneficioQuery</p>
 * <p>Description: Lista contratos de mensalidade de benefícios de uma operadora pela carteirinha do beneficiário e tipo de lançamento</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaAdesPorCbePorTntPorSadQuery extends HQuery {

    public String cbeCodigo;
    public List<String> tntCodigo;
    public List<String> sadCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT DISTINCT aut.adeCodigo, dad34.dadValor, dad35.dadValor FROM AutDesconto aut ");
        corpoBuilder.append("INNER JOIN aut.tipoLancamento tla ");
        corpoBuilder.append("INNER JOIN tla.tipoNatureza tnt ");
        corpoBuilder.append("INNER JOIN aut.statusAutorizacaoDesconto sad ");
        corpoBuilder.append("INNER JOIN aut.contratoBeneficio cbe ");
        corpoBuilder.append("LEFT JOIN aut.dadosAutorizacaoDescontoSet dad34 WITH dad34.tipoDadoAdicional.tdaCodigo = '").append(CodedValues.TDA_BEN_ADESAO_PLANO_EX_FUNCIONARIO).append("'");
        corpoBuilder.append("LEFT JOIN aut.dadosAutorizacaoDescontoSet dad35 WITH dad35.tipoDadoAdicional.tdaCodigo = '").append(CodedValues.TDA_BEN_PERIODO_CONTRIBUICAO_PLANO).append("'");
        corpoBuilder.append("WHERE 1 = 1 ");


        if (!TextHelper.isNull(cbeCodigo)) {
            corpoBuilder.append(" AND cbe.cbeCodigo ").append(criaClausulaNomeada("cbeCodigo", cbeCodigo));
        }

        if (!TextHelper.isNull(sadCodigo)) {
            corpoBuilder.append(" AND sad.sadCodigo ").append(criaClausulaNomeada("sadCodigo", sadCodigo));
        }

        if (!TextHelper.isNull(tntCodigo)) {
            corpoBuilder.append(" AND tnt.tntCodigo ").append(criaClausulaNomeada("tntCodigo", tntCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(sadCodigo)) {
            defineValorClausulaNomeada("sadCodigo", sadCodigo, query);
        }

        if (!TextHelper.isNull(tntCodigo)) {
            defineValorClausulaNomeada("tntCodigo", tntCodigo, query);
        }

        if (!TextHelper.isNull(cbeCodigo)) {
            defineValorClausulaNomeada("cbeCodigo", cbeCodigo, query);
        }
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
                "DAD_VALOR_34",
                "DAD_VALOR_35"
                };
    }
}
