package com.zetra.econsig.persistence.query.beneficios.subsidio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarTabelaCalculoSubsidioQuery</p>
 * <p>Description: Listagem de regras para cálculo de subsídio</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarTabelaCalculoSubsidioQuery extends HQuery {

    public String tipoEntidade;
    public List<String> entCodigos;
    public boolean simulacao = false;
    public String orgCodigo;
    public String benCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT ");
        corpoBuilder.append("clb.clbCodigo, ");
        corpoBuilder.append("clb.beneficio.benCodigo, ");
        corpoBuilder.append("clb.tipoBeneficiario.tibCodigo, ");
        corpoBuilder.append("clb.grauParentesco.grpCodigo, ");
        corpoBuilder.append("clb.motivoDependencia.mdeCodigo, ");
        corpoBuilder.append("clb.orgao.orgCodigo, ");
        corpoBuilder.append("clb.clbValorMensalidade, ");
        corpoBuilder.append("clb.clbValorSubsidio, ");
        corpoBuilder.append("clb.clbFaixaEtariaIni, ");
        corpoBuilder.append("clb.clbFaixaEtariaFim, ");
        corpoBuilder.append("clb.clbFaixaSalarialIni, ");
        corpoBuilder.append("clb.clbFaixaSalarialFim ");
        corpoBuilder.append("FROM CalculoBeneficio clb ");
        corpoBuilder.append("WHERE clb.clbValorSubsidio IS NOT NULL ");
        corpoBuilder.append("AND clb.clbVigenciaIni <= current_date ");
        corpoBuilder.append("AND (clb.clbVigenciaFim IS NULL OR clb.clbVigenciaFim >= current_date) ");

        if (tipoEntidade != null && entCodigos != null && !entCodigos.isEmpty()) {
            corpoBuilder.append(" AND (clb.orgao.orgCodigo IS NULL OR ");
            if (tipoEntidade.equals("RSE")) {
                corpoBuilder.append("clb.orgao.orgCodigo IN (select rse.orgao.orgCodigo from RegistroServidor rse where rse.rseCodigo ").append(criaClausulaNomeada("entCodigos", entCodigos)).append(") ");
            } else if (tipoEntidade.equals("ORG")) {
                corpoBuilder.append("clb.orgao.orgCodigo ").append(criaClausulaNomeada("entCodigos", entCodigos));
            } else if (tipoEntidade.equals("EST")) {
                corpoBuilder.append("clb.orgao.orgCodigo IN (select org.orgCodigo from Orgao org where org.estabelecimento.estCodigo ").append(criaClausulaNomeada("entCodigos", entCodigos)).append(") ");
            } else {
                corpoBuilder.append("1=1");
            }
            corpoBuilder.append(")");
        }

        if(simulacao) {
            corpoBuilder.append(" AND clb.orgao.orgCodigo = :orgCodigo AND clb.beneficio.benCodigo = :benCodigo ");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (tipoEntidade != null && entCodigos != null && !entCodigos.isEmpty()) {
            defineValorClausulaNomeada("entCodigos", entCodigos, query);
        }

        if(simulacao) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
            defineValorClausulaNomeada("benCodigo", benCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CLB_CODIGO,
                Columns.BEN_CODIGO,
                Columns.TIB_CODIGO,
                Columns.GRP_CODIGO,
                Columns.MDE_CODIGO,
                Columns.ORG_CODIGO,
                Columns.CLB_VALOR_MENSALIDADE,
                Columns.CLB_VALOR_SUBSIDIO,
                Columns.CLB_FAIXA_ETARIA_INI,
                Columns.CLB_FAIXA_ETARIA_FIM,
                Columns.CLB_FAIXA_SALARIAL_INI,
                Columns.CLB_FAIXA_SALARIAL_FIM,
        };
    }
}
