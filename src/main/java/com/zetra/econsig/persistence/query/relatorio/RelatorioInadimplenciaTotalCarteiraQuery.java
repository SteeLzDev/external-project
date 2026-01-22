package com.zetra.econsig.persistence.query.relatorio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
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
public class RelatorioInadimplenciaTotalCarteiraQuery extends ReportHQuery {
    public String prdDtDesconto;
    public String csaCodigo;
    public String csaProjetoInadimplencia;
    public List<String> spdCodigos;
    public String naturezaServico;
    public List<String> sadCodigos;
    public boolean count;
    public List<String> srsCodigos;
    public AcessoSistema responsavel;

    @Override
    public void setCriterios(TransferObject criterio) {
        prdDtDesconto = (String) criterio.getAttribute(Columns.PRD_DATA_DESCONTO);
        csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
        csaProjetoInadimplencia = (String) criterio.getAttribute(Columns.CSA_PROJETO_INADIMPLENCIA);
        spdCodigos = (List<String>) criterio.getAttribute(Columns.SPD_CODIGO);
        naturezaServico = (String) criterio.getAttribute(Columns.NSE_CODIGO);
        sadCodigos = (List<String>) criterio.getAttribute(Columns.ADE_SAD_CODIGO);
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append(" select ");

        if (count) {
            corpoBuilder.append(" count(*) as total ");
            if (!TextHelper.isNull(csaCodigo)) {
                corpoBuilder.append(" , csa.csaNome as csaNome ");
            }

            if (srsCodigos != null && !srsCodigos.isEmpty()) {
                corpoBuilder.append(" , srs.srsDescricao as statusRegistroServidor ");
            }
        } else {
            corpoBuilder.append(" sum(ade.adeVlr * (ade.adePrazo - coalesce(ade.adePrdPagas, 0))) as valor ");
            if (!TextHelper.isNull(csaCodigo)) {
                corpoBuilder.append(" , csa.csaNome as csaNome");
            }

            if (srsCodigos != null && !srsCodigos.isEmpty()) {
                corpoBuilder.append(" , srs.srsDescricao as statusRegistroServidor ");
            }
        }

        corpoBuilder.append(" from AutDesconto ade ");

        corpoBuilder.append(" inner join ade.verbaConvenio vco ");
        corpoBuilder.append(" inner join vco.convenio cnv ");
        corpoBuilder.append(" inner join cnv.consignataria csa ");

        if (!count && !TextHelper.isNull(naturezaServico)) {
            corpoBuilder.append(" inner join cnv.servico svc ");
        }

        if (srsCodigos != null && !srsCodigos.isEmpty()) {
            corpoBuilder.append(" inner join ade.registroServidor rse ");
            corpoBuilder.append(" inner join rse.statusRegistroServidor srs ");
        }

        corpoBuilder.append(" inner join ade.parcelaDescontoSet prd ");

        corpoBuilder.append(" where csa.csaProjetoInadimplencia ").append(criaClausulaNomeada("csaProjetoInadimplencia", csaProjetoInadimplencia));
        corpoBuilder.append(" and prd.prdDataDesconto ").append(criaClausulaNomeada("prdDtDesconto", prdDtDesconto));

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (spdCodigos != null && !spdCodigos.isEmpty()) {
            corpoBuilder.append(" and prd.statusParcelaDesconto.spdCodigo ").append(criaClausulaNomeada("spdCodigos", spdCodigos));
        }

        if (!count && !TextHelper.isNull(naturezaServico)) {
            corpoBuilder.append(" and svc.naturezaServico.nseCodigo ").append(criaClausulaNomeada("naturezaServico", naturezaServico));
        }

        if (sadCodigos != null && !sadCodigos.isEmpty()) {
            corpoBuilder.append(" and ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));
        }

        if (srsCodigos != null && !srsCodigos.isEmpty()) {
            corpoBuilder.append(" and srs.srsCodigo ").append(criaClausulaNomeada("srsCodigo", srsCodigos));
        }

        Query<Object[]> queryInst = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("csaProjetoInadimplencia", csaProjetoInadimplencia, queryInst);
        defineValorClausulaNomeada("prdDtDesconto", parseDateString(prdDtDesconto), queryInst);

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, queryInst);
        }

        if (spdCodigos != null && !spdCodigos.isEmpty()) {
            defineValorClausulaNomeada("spdCodigos", spdCodigos, queryInst);
        }

        if (!count && !TextHelper.isNull(naturezaServico)) {
            defineValorClausulaNomeada("naturezaServico", naturezaServico, queryInst);
        }

        if (sadCodigos != null && !sadCodigos.isEmpty()) {
            defineValorClausulaNomeada("sadCodigos", sadCodigos, queryInst);
        }

        if (srsCodigos != null && !srsCodigos.isEmpty()) {
            defineValorClausulaNomeada("srsCodigo", srsCodigos, queryInst);
        }

        return queryInst;
    }

    @Override
    protected String[] getFields() {
        String[] retorno = null;

        if (TextHelper.isNull(csaCodigo) && count) {
            retorno = new String[] { "total" };
        } else if (!TextHelper.isNull(csaCodigo) && count) {
            if (srsCodigos != null && !srsCodigos.isEmpty()) {
                retorno = new String[] { "total", "csaNome", "statusRegistroServidor" };
            } else {
                retorno = new String[] { "total", "csaNome" };
            }
        } else if (!TextHelper.isNull(csaCodigo)) {
            if (srsCodigos != null && !srsCodigos.isEmpty()) {
                retorno = new String[] { "valor", "csaNome", "statusRegistroServidor" };
            } else {
                retorno = new String[] { "valor", "csaNome" };
            }
        } else {
            retorno = new String[] { "valor" };
        }

        return retorno;
    }
}
