package com.zetra.econsig.persistence.query.convenio;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCnvsAtualizarVerbaBySvcQuery</p>
 * <p>Description: Listagem dos convênios que não possuem verba cadastrada
 * e que devem ser atualizados com um novo código de verba baseado no identificador do serviço relacionado ao convênio
 * desde que exista contrato(s) ativo(s) para o convênio que será atualizado.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCnvsAtualizarVerbaBySvcQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        List<String> sadCodigosAtivos = new ArrayList<String>();
        sadCodigosAtivos.add(CodedValues.NOT_EQUAL_KEY);
        sadCodigosAtivos.addAll(CodedValues.SAD_CODIGOS_INATIVOS);

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select distinct ");
        corpoBuilder.append("cnv.cnvCodigo, svc.svcIdentificador ");
        corpoBuilder.append("from AutDesconto ade ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("inner join vco.convenio cnv ");
        corpoBuilder.append("inner join cnv.servico svc ");
        corpoBuilder.append("where cnv.cnvCodVerba ").append(criaClausulaNomeada("", CodedValues.IS_NULL_KEY));
        corpoBuilder.append(" and ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigosAtivos", sadCodigosAtivos));

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("sadCodigosAtivos", sadCodigosAtivos, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CNV_CODIGO,
                Columns.SVC_IDENTIFICADOR
        };
    }

}
