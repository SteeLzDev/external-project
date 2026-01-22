package com.zetra.econsig.persistence.query.relatorio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p> Title: RelatorioInadimplenciaTotalCarteira</p>
 * <p> Description: Monta relatório de inadimplência</p>
 * <p> Copyright: Copyright (c) 2014 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioInadimplenciaOrgaoQuery extends ReportHQuery {
    public String prdDtDesconto;
    public String csaCodigo;
    public String orgCodigo;
    public String csaProjetoInadimplencia;
    public String naturezaServico;
    public List<String> spdCodigos;
    public List<String> notOrgCodigo;
    public boolean top;
    public boolean count;
    public AcessoSistema responsavel;

    @Override
    public void setCriterios(TransferObject criterio) {
        prdDtDesconto = (String) criterio.getAttribute(Columns.PRD_DATA_DESCONTO);
        csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
        csaProjetoInadimplencia = (String) criterio.getAttribute(Columns.CSA_PROJETO_INADIMPLENCIA);
        spdCodigos = (List<String>) criterio.getAttribute(Columns.SPD_CODIGO);
        orgCodigo = (String) criterio.getAttribute(Columns.ORG_CODIGO);
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append(" select ");

        if (top) {
            corpoBuilder.append(" org.orgCodigo as orgCodigo ");
        } else {
            if (count) {
                corpoBuilder.append(" count(*) as total, ");
                if ((orgCodigo != null) && !TextHelper.isNull(orgCodigo)) {
                    corpoBuilder.append(" concatenar(concatenar(org.orgIdentificador, ' - '), org.orgNome) as orgNome ");
                } else if ((notOrgCodigo != null) && !notOrgCodigo.isEmpty()) {
                    corpoBuilder.append(" count(org.orgCodigo) as orgNome");
                }
            } else {
                if ((notOrgCodigo == null) || notOrgCodigo.isEmpty()) {
                    corpoBuilder.append(" concatenar(concatenar(org.orgIdentificador, ' - '), org.orgNome) as orgNome, ");
                } else {
                    corpoBuilder.append(" count(org.orgNome) as orgNome, ");
                }
                corpoBuilder.append(" sum(ade.adeVlr * (ade.adePrazo - coalesce(ade.adePrdPagas, 0))) as valor ");
            }
        }

        corpoBuilder.append(" from AutDesconto ade ");

        corpoBuilder.append(" inner join ade.verbaConvenio vco ");
        corpoBuilder.append(" inner join vco.convenio cnv ");
        corpoBuilder.append(" inner join cnv.consignataria csa ");

        corpoBuilder.append(" inner join ade.parcelaDescontoSet prd ");

        corpoBuilder.append(" inner join ade.registroServidor rse ");
        corpoBuilder.append(" inner join rse.orgao org ");

        if (!TextHelper.isNull(naturezaServico)) {
            corpoBuilder.append(" inner join cnv.servico svc ");
        }

        corpoBuilder.append(" where csa.csaProjetoInadimplencia ").append(criaClausulaNomeada("csaProjetoInadimplencia", csaProjetoInadimplencia));
        corpoBuilder.append(" and prd.prdDataDesconto ").append(criaClausulaNomeada("prdDtDesconto", prdDtDesconto));

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if ((spdCodigos != null) && !spdCodigos.isEmpty()) {
            corpoBuilder.append(" and prd.statusParcelaDesconto.spdCodigo ").append(criaClausulaNomeada("spdCodigos", spdCodigos));
        }

        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" and org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }

        if ((notOrgCodigo != null) && !notOrgCodigo.isEmpty()) {
            notOrgCodigo.add(CodedValues.NOT_EQUAL_KEY);
            corpoBuilder.append(" and org.orgCodigo ").append(criaClausulaNomeada("notOrgCodigo", notOrgCodigo));
        }

        if (!TextHelper.isNull(naturezaServico)) {
            corpoBuilder.append(" and svc.naturezaServico.nseCodigo ").append(criaClausulaNomeada("naturezaServico", naturezaServico));
        }

        if ((notOrgCodigo == null) || notOrgCodigo.isEmpty()) {
            corpoBuilder.append(" group by org.orgCodigo ");
        }

        corpoBuilder.append(" order by ");
        if (count) {
            corpoBuilder.append(" count(*) desc ");
        } else {
            corpoBuilder.append(" sum(ade.adeVlr * (ade.adePrazo - coalesce(ade.adePrdPagas, 0))) desc ");
        }

        final Query<Object[]> queryInst = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("csaProjetoInadimplencia", csaProjetoInadimplencia, queryInst);
        defineValorClausulaNomeada("prdDtDesconto", parseDateString(prdDtDesconto), queryInst);

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, queryInst);
        }

        if ((spdCodigos != null) && !spdCodigos.isEmpty()) {
            defineValorClausulaNomeada("spdCodigos", spdCodigos, queryInst);
        }

        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, queryInst);
        }

        if ((notOrgCodigo != null) && !notOrgCodigo.isEmpty()) {
            defineValorClausulaNomeada("notOrgCodigo", notOrgCodigo, queryInst);
        }

        if (!TextHelper.isNull(naturezaServico)) {
            defineValorClausulaNomeada("naturezaServico", naturezaServico, queryInst);
        }

        if (top) {
            queryInst.setMaxResults(9);
        }

        return queryInst;
    }

    @Override
    protected String[] getFields() {
        String[] retorno = null;

        if (top) {
            retorno = new String[] { "orgCodigo" };
        } else {
            if (count) {
                retorno = new String[] { "total", "orgNome" };
            } else {
                retorno = new String[] { "orgNome", "valor" };
            }
        }

        return retorno;
    }
}
