package com.zetra.econsig.persistence.query.servico;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaServicoSerQuery</p>
 * <p>Description: Listagem de Serviços que o Órgão do servidor possui Convênio</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaServicoSerQuery extends HQuery {

    public String rseCodigo;
    public String csaCodigo;
    public String nseCodigo;
    public boolean ativos;
    public boolean count = false;


    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (count) {
            corpo = "select count(*) as total ";
        } else {
            corpo = "select distinct " +
            "svc.svcCodigo, " +
            "svc.svcIdentificador, " +
            "svc.svcDescricao, " +
            "svc.svcAtivo ";
        }
        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from Convenio cnv");
        corpoBuilder.append(" inner join cnv.servico svc");
        corpoBuilder.append(" where cnv.orgao.orgCodigo = (select rse.orgao.orgCodigo from RegistroServidor rse where rse.rseCodigo ");
        corpoBuilder.append(criaClausulaNomeada("rseCodigo", rseCodigo)).append(")");

        if (ativos) {
            corpoBuilder.append(" and cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(nseCodigo)) {
            corpoBuilder.append(" and svc.naturezaServico.nseCodigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        }

        corpoBuilder.append(" order by svc.svcDescricao");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(nseCodigo)) {
            defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SVC_CODIGO,
                Columns.SVC_IDENTIFICADOR,
                Columns.SVC_DESCRICAO,
                Columns.SVC_ATIVO
        };
    }
}
