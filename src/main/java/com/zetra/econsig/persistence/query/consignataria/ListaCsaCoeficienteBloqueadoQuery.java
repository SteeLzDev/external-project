package com.zetra.econsig.persistence.query.consignataria;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCsaCoeficienteBloqueadoQuery</p>
 * <p>Description: Listagem de consignatárias que possuem coeficientes bloqueados.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCsaCoeficienteBloqueadoQuery extends HQuery {

    private final String tpsCodigo = CodedValues.TPS_DIAS_VIGENCIA_CET;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append(" SELECT distinct csa.csaCodigo, ");
        corpoBuilder.append(" csa.csaIdentificador, ");
        corpoBuilder.append(" csa.csaNome, ");
        corpoBuilder.append(" csa.csaNomeAbrev, ");
        corpoBuilder.append(" csa.csaAtivo, ");
        corpoBuilder.append(" csa.csaEmail, ");
        corpoBuilder.append(" csa.csaResponsavel, ");
        corpoBuilder.append(" csa.csaResponsavel2, ");
        corpoBuilder.append(" csa.csaResponsavel3 ");

        corpoBuilder.append(" FROM Coeficiente cft");
        corpoBuilder.append(" INNER JOIN cft.prazoConsignataria pzc");
        corpoBuilder.append(" INNER JOIN pzc.prazo prz");
        corpoBuilder.append(" INNER JOIN pzc.consignataria csa");
        corpoBuilder.append(" WHERE 1=1 ");

        corpoBuilder.append(" AND exists (select 1 from Convenio cnv");
        corpoBuilder.append(" inner join cnv.servico svc ");
        corpoBuilder.append(" inner join svc.paramSvcConsignanteSet pse ");
        corpoBuilder.append(" where cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        corpoBuilder.append(" and pse.tpsCodigo ").append(criaClausulaNomeada("tpsCodigo", tpsCodigo));
        corpoBuilder.append(" and pse.pseVlr is not NULL");
        corpoBuilder.append(" and cnv.consignataria.csaCodigo = csa.csaCodigo");
        corpoBuilder.append(" and cnv.servico.svcCodigo = prz.servico.svcCodigo");
        corpoBuilder.append(")");

        corpoBuilder.append(" AND not exists (select 1 from CoeficienteAtivo cfa");
        corpoBuilder.append(" where cfa.prazoConsignataria.przCsaCodigo = cft.prazoConsignataria.przCsaCodigo");
        corpoBuilder.append(")");

        corpoBuilder.append(" AND pzc.przCsaAtivo = ").append(CodedValues.STS_ATIVO);
        corpoBuilder.append(" AND prz.przAtivo = ").append(CodedValues.STS_ATIVO);

        // Listar a cada 2 dias, sendo a data atual for múltiplo de 2
        Integer dia = DateHelper.getDay(DateHelper.getSystemDate());
        Integer diaDivisivelPor2 = dia % 2;
        corpoBuilder.append(" AND (:diaDivisivelPor2 = 0)");

        // Define os valores para os parâmetros nomeados
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("tpsCodigo", tpsCodigo, query);

        defineValorClausulaNomeada("diaDivisivelPor2", diaDivisivelPor2, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSA_CODIGO,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME,
                Columns.CSA_NOME_ABREV,
                Columns.CSA_ATIVO,
                Columns.CSA_EMAIL,
                Columns.CSA_RESPONSAVEL,
                Columns.CSA_RESPONSAVEL_2,
                Columns.CSA_RESPONSAVEL_3
        };
    }
}
