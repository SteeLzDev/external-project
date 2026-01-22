package com.zetra.econsig.persistence.query.margem;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaContratosIncideMargemNulaQuery</p>
 * <p>Description: Lista contratos ativos com incidência de margem cujo valor é nulo no cadastro de margem.</p>
 * <p>Margens extras são consideradas nulas quando não existir o registro na tabela.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaContratosIncideMargemNulaQuery extends HQuery {

    private final List<String> orgCodigos;
    private final List<String> estCodigos;

    public ListaContratosIncideMargemNulaQuery(List<String> orgCodigos, List<String> estCodigos) {
        this.orgCodigos = orgCodigos;
        this.estCodigos = estCodigos;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select ade.adeIncMargem, count(*) ");
        corpoBuilder.append("from AutDesconto ade ");
        corpoBuilder.append("inner join ade.registroServidor rse ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("inner join vco.convenio cnv ");
        corpoBuilder.append("inner join cnv.orgao org ");
        corpoBuilder.append("where ade.statusAutorizacaoDesconto.sadCodigo not in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_INATIVOS, "','")).append("') ");
        corpoBuilder.append("and ade.adeIncMargem <> " + CodedValues.INCIDE_MARGEM_NAO + " ");
        // margem extra é nula quando não possuir registro na taela de margem de registro servidor
        corpoBuilder.append("and (( ");
        corpoBuilder.append("ade.adeIncMargem NOT IN (" + CodedValues.INCIDE_MARGEM_SIM + "," + CodedValues.INCIDE_MARGEM_SIM_2 + "," + CodedValues.INCIDE_MARGEM_SIM_3 + ") ");
        corpoBuilder.append("and not exists (select 1 from MargemRegistroServidor mrs where mrs.margem.marCodigo = ade.adeIncMargem and mrs.registroServidor.rseCodigo = rse.rseCodigo) ");
        // cadastro de margem, do registro servidor, com valor nulo
        corpoBuilder.append(") or ( ");
        corpoBuilder.append("ade.adeIncMargem IN (" + CodedValues.INCIDE_MARGEM_SIM + "," + CodedValues.INCIDE_MARGEM_SIM_2 + "," + CodedValues.INCIDE_MARGEM_SIM_3 + ") ");
        corpoBuilder.append("and ((case when ade.adeIncMargem = " + CodedValues.INCIDE_MARGEM_SIM + " then rse.rseMargem ");
        corpoBuilder.append("           when ade.adeIncMargem = " + CodedValues.INCIDE_MARGEM_SIM_2 + " then rse.rseMargem2 ");
        corpoBuilder.append("           when ade.adeIncMargem = " + CodedValues.INCIDE_MARGEM_SIM_3 + " then rse.rseMargem3 ");
        corpoBuilder.append("           end) is null ");
        corpoBuilder.append("  or (case when ade.adeIncMargem = " + CodedValues.INCIDE_MARGEM_SIM + " then rse.rseMargemRest ");
        corpoBuilder.append("           when ade.adeIncMargem = " + CodedValues.INCIDE_MARGEM_SIM_2 + " then rse.rseMargemRest2 ");
        corpoBuilder.append("           when ade.adeIncMargem = " + CodedValues.INCIDE_MARGEM_SIM_3 + " then rse.rseMargemRest3 ");
        corpoBuilder.append("           end) is null ");
        corpoBuilder.append("))) ");

        if ((estCodigos != null) && (estCodigos.size() > 0)) {
            corpoBuilder.append(" and org.estabelecimento.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigos));
        }
        if ((orgCodigos != null) && (orgCodigos.size() > 0)) {
            corpoBuilder.append(" and org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        corpoBuilder.append(" group by ade.adeIncMargem");
        corpoBuilder.append(" order by ade.adeIncMargem");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if ((estCodigos != null) && (estCodigos.size() > 0)) {
            defineValorClausulaNomeada("estCodigo", estCodigos, query);
        }
        if ((orgCodigos != null) && (orgCodigos.size() > 0)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_INC_MARGEM,
                "QTD"
        };
    }

}
