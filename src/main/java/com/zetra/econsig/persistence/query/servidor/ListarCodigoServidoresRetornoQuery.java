package com.zetra.econsig.persistence.query.servidor;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarCodigoServidoresRetornoQuery</p>
 * <p>Description: Classe para query da consulta de dados para montar email da notificação de servidor/funcionário de licença com data de retorno próxima</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author: ricardo.kitagawa $
 * $Revision: 23454 $
 * $Date: 2018-01-05 11:35:34 -0200 (Sex, 05 Jan 2018) $
 */
public class ListarCodigoServidoresRetornoQuery extends HQuery {

    private final List<Integer> diasParam;

    public ListarCodigoServidoresRetornoQuery(List<Integer> diasParam) {
        super();
        this.diasParam = diasParam;
    }
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select DISTINCT ");
        corpoBuilder.append("    cse.cseIdentificador,");
        corpoBuilder.append("    cse.cseNome,");
        corpoBuilder.append("    est.estIdentificador,");
        corpoBuilder.append("    est.estNome,");
        corpoBuilder.append("    org.orgIdentificador,");
        corpoBuilder.append("    org.orgNome,");
        corpoBuilder.append("    rse.rseMatricula,");
        corpoBuilder.append("    ser.serNome,");
        corpoBuilder.append("    ser.serCpf,");
        corpoBuilder.append("    csa.csaCodigo,");
        corpoBuilder.append("    csa.csaNome,");
        corpoBuilder.append("    csa.csaEmail,");
        corpoBuilder.append("    DATEDIFF(rse.rseDataRetorno, CURDATE()) as dias_retorno");
        corpoBuilder.append(" from RegistroServidor rse ");
        corpoBuilder.append(" inner join rse.servidor ser ");
        corpoBuilder.append(" inner join rse.orgao org ");
        corpoBuilder.append(" inner join org.estabelecimento est ");
        corpoBuilder.append(" inner join est.consignante cse ");
        corpoBuilder.append(" inner join rse.autDescontoSet ade ");
        corpoBuilder.append(" inner join ade.verbaConvenio vco ");
        corpoBuilder.append(" inner join vco.convenio cnv ");
        corpoBuilder.append(" inner join cnv.consignataria csa ");
        corpoBuilder.append(" where rse.statusRegistroServidor.srsCodigo ").append(criaClausulaNomeada("srsCodigo", CodedValues.SRS_BLOQUEADOS));
        corpoBuilder.append(" and ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigo", CodedValues.SAD_CODIGOS_ATIVOS));
        corpoBuilder.append(" and DATEDIFF(rse.rseDataRetorno, CURDATE())").append(criaClausulaNomeada("dtRetorno", diasParam));

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("srsCodigo", CodedValues.SRS_BLOQUEADOS, query);
        defineValorClausulaNomeada("sadCodigo", CodedValues.SAD_CODIGOS_ATIVOS, query);
        defineValorClausulaNomeada("dtRetorno", diasParam, query);
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSE_IDENTIFICADOR,
                Columns.CSE_NOME,
                Columns.EST_IDENTIFICADOR,
                Columns.EST_NOME,
                Columns.ORG_IDENTIFICADOR,
                Columns.ORG_NOME,
                Columns.RSE_MATRICULA,
                Columns.SER_NOME,
                Columns.SER_CPF,
                Columns.CSA_CODIGO,
                Columns.CSA_NOME,
                Columns.CSA_EMAIL,
                "dias_retorno"
        };
    }
}