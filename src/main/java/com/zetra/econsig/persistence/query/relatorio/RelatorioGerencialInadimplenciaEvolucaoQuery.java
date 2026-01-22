package com.zetra.econsig.persistence.query.relatorio;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p> Title: RelatorioGerencialInadimplenciaEvolucaoQuery</p>
 * <p> Description: Consulta que retorna o percentual de inadimplência para cada período informado.</p>
 * <p> Copyright: Copyright (c) 2002-2022</p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioGerencialInadimplenciaEvolucaoQuery extends ReportHNativeQuery{
    public List<Date> periodos = null;

    public AcessoSistema responsavel;

    @Override
    public void setCriterios(TransferObject criterio) {
        periodos = (List<Date>) criterio.getAttribute("PERIODO");
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select prd.prd_data_desconto, ");
        corpoBuilder.append("sum(case when prd.spd_codigo = '").append(CodedValues.SPD_REJEITADAFOLHA).append("' then 1 else 0 end) / count(*) * 100 as PORC_EVOLUCAO_INADIMPLENCIA ");
        corpoBuilder.append("from tb_registro_servidor rse ");
        corpoBuilder.append("inner join tb_aut_desconto ade on (rse.rse_codigo = ade.rse_codigo) ");
        corpoBuilder.append("inner join tb_parcela_desconto prd on (ade.ade_codigo = prd.ade_codigo) ");
        corpoBuilder.append("where rse.srs_codigo = '").append(CodedValues.SRS_ATIVO).append("' ");
        corpoBuilder.append("and prd.prd_data_desconto ").append(criaClausulaNomeada("periodo", periodos)).append(" ");
        corpoBuilder.append("and prd.prd_data_realizado is not null ");
        corpoBuilder.append("group by prd.prd_data_desconto ");
        corpoBuilder.append("order by prd.prd_data_desconto asc ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("periodo", periodos, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PRD_DATA_DESCONTO,
                "PORC_EVOLUCAO_INADIMPLENCIA"
        };
    }
}