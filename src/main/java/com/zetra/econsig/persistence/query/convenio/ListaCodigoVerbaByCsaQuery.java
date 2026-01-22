package com.zetra.econsig.persistence.query.convenio;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCodigoVerbaCsaComParametroQuery</p>
 * <p>Description: Listagem de Códigos de Verbas Ativos de uma Consignatária (Convênios Ativos),
 * para os serviços que possuem o parâmetro "tpsCodigo" com o valor setado para "pseVlr".</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCodigoVerbaByCsaQuery extends HQuery {

    public String csaCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo =
            "select distinct " +
            "cnv.cnvCodVerba, " +
            "svc.svcCodigo, " +
            "svc.svcIdentificador, " +
            "svc.svcDescricao ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append("from Servico svc ");
        corpoBuilder.append("inner join svc.convenioSet cnv ");
        corpoBuilder.append(" where cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpoBuilder.append(" and cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CNV_COD_VERBA,
                Columns.SVC_CODIGO,
                Columns.SVC_IDENTIFICADOR,
                Columns.SVC_DESCRICAO
        };
    }
}
