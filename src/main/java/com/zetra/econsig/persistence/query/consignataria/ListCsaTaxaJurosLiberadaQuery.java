package com.zetra.econsig.persistence.query.consignataria;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class ListCsaTaxaJurosLiberadaQuery extends HQuery {

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

        corpoBuilder.append(" FROM DefinicaoTaxaJuros dtj");
        corpoBuilder.append(" INNER JOIN dtj.consignataria csa");
        corpoBuilder.append(" INNER JOIN dtj.servico svc");
        corpoBuilder.append(" INNER JOIN svc.paramSvcConsignanteSet pse");

        // Possui um convênio ativo entre o SVC e CSA
        corpoBuilder.append(" WHERE EXISTS (SELECT 1");
        corpoBuilder.append(" FROM Convenio cnv");
        corpoBuilder.append(" WHERE cnv.consignataria.csaCodigo = csa.csaCodigo");
        corpoBuilder.append(" AND cnv.servico.svcCodigo = svc.svcCodigo");
        corpoBuilder.append(" AND cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        corpoBuilder.append(")");

        // Que o parâmetro de serviço de dias de vigênca do CET esteja preenchido
        corpoBuilder.append(" AND pse.tpsCodigo = '").append(CodedValues.TPS_DIAS_VIGENCIA_CET).append("'");
        corpoBuilder.append(" AND nullif(trim(pse.pseVlr), '') IS NOT NULL");
        corpoBuilder.append(" AND (date_diff(dtj.dtjDataVigenciaIni, data_corrente()) = 0)");

        // Define os valores para os parâmetros nomeados
        return instanciarQuery(session, corpoBuilder.toString());
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
