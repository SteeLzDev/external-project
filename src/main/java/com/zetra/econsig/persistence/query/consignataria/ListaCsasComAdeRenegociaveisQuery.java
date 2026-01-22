package com.zetra.econsig.persistence.query.consignataria;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCsasComAdeRenegociaveisQuery</p>
 * <p>Description: Listagem de consignatárias que possuem contratos passíveis de renegociação.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCsasComAdeRenegociaveisQuery extends HQuery {

    public String svcCodigo;
    public String rseCodigo;
    public String orgCodigo;
    public List<String> sadCodigos;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select csa.csaCodigo, csa.csaIdentificador, csa.csaNome, csa.csaNomeAbrev, csa.csaAtivo ");
        corpoBuilder.append(" from Consignataria csa ");
        corpoBuilder.append(" inner join csa.convenioSet cnv ");
        corpoBuilder.append(" inner join cnv.verbaConvenioSet vco ");
        corpoBuilder.append(" inner join vco.autDescontoSet ade ");
        corpoBuilder.append(" inner join ade.registroServidor rse ");
        corpoBuilder.append(" inner join cnv.servico svc ");
        corpoBuilder.append(" where rse.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        corpoBuilder.append(" and exists (select 1 from svc.relacionamentoServicoByDestinoSet rsv ");
        corpoBuilder.append(" where rsv.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_RENEGOCIACAO).append("'");
        corpoBuilder.append(" and rsv.servicoBySvcCodigoDestino.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        corpoBuilder.append(" ) ");

        if ((sadCodigos != null) && (sadCodigos.size() > 0)) {
            corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));
        }

        corpoBuilder.append(" and cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        corpoBuilder.append(" and csa.csaAtivo = ").append(CodedValues.STS_ATIVO).append("");

        corpoBuilder.append(" group by csa.csaCodigo, csa.csaIdentificador, csa.csaNome, csa.csaNomeAbrev, csa.csaAtivo ");
        corpoBuilder.append(" order by csa.csaNome ");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("svcCodigo", svcCodigo, query);

        if ((sadCodigos != null) && !sadCodigos.isEmpty()) {
            defineValorClausulaNomeada("sadCodigos", sadCodigos, query);
        }

        return query;
    }
    @Override
    protected String[] getFields() {
        final String[] fields = new String[] {
                Columns.CSA_CODIGO,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME,
                Columns.CSA_NOME_ABREV,
                Columns.CSA_ATIVO
        };

        return fields;
    }
}
