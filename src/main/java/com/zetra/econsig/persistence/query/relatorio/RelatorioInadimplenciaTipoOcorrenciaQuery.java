package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;

/**
 * <p> Title: RelatorioInadimplenciaTipoOcorrenciaQuery</p>
 * <p> Description: Monta relatório de inadimplência</p>
 * <p> Copyright: Copyright (c) 2014 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioInadimplenciaTipoOcorrenciaQuery extends ReportHQuery {
    public String prdDtDesconto;
    public String csaCodigo;
    public String csaProjetoInadimplencia;
    public String naturezaServico;
    public String tocCodigo;
    public boolean count;

    public AcessoSistema responsavel;

    @Override
    public void setCriterios(TransferObject criterio) {
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append(" select ");

        if (count) {
            corpoBuilder.append(" count(*) as total ");
        } else {
            corpoBuilder.append(" sum(ade.adeVlr * (ade.adePrazo - coalesce(ade.adePrdPagas, 0))) as valor ");
        }

        corpoBuilder.append(" from AutDesconto ade ");

        corpoBuilder.append(" inner join ade.verbaConvenio vco ");
        corpoBuilder.append(" inner join vco.convenio cnv ");
        corpoBuilder.append(" inner join cnv.consignataria csa ");

        if (!TextHelper.isNull(naturezaServico)) {
            corpoBuilder.append(" inner join cnv.servico svc ");
        }

        if (!TextHelper.isNull(tocCodigo)) {
            corpoBuilder.append(" inner join ade.ocorrenciaAutorizacaoSet oca ");
            corpoBuilder.append(" inner join oca.tipoOcorrencia toc ");
        }

        corpoBuilder.append(" inner join ade.parcelaDescontoSet prd ");

        corpoBuilder.append(" where csa.csaProjetoInadimplencia ").append(criaClausulaNomeada("csaProjetoInadimplencia", csaProjetoInadimplencia));
        corpoBuilder.append(" and prd.prdDataDesconto ").append(criaClausulaNomeada("prdDtDesconto", prdDtDesconto));

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(naturezaServico)) {
            corpoBuilder.append(" and svc.naturezaServico.nseCodigo ").append(criaClausulaNomeada("naturezaServico", naturezaServico));
        }

        if (!TextHelper.isNull(tocCodigo)) {
            corpoBuilder.append(" and toc.tocCodigo ").append(criaClausulaNomeada("tocCodigo", tocCodigo));
        }

        Query<Object[]> queryInst = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("csaProjetoInadimplencia", csaProjetoInadimplencia, queryInst);
        defineValorClausulaNomeada("prdDtDesconto", parseDateString(prdDtDesconto), queryInst);

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, queryInst);
        }

        if (!TextHelper.isNull(naturezaServico)) {
            defineValorClausulaNomeada("naturezaServico", naturezaServico, queryInst);
        }

        if (!TextHelper.isNull(tocCodigo)) {
            defineValorClausulaNomeada("tocCodigo", tocCodigo, queryInst);
        }

        return queryInst;
    }

    @Override
    protected String[] getFields() {
        String[] retorno = null;

        if (count) {
            retorno = new String[] { "total" };
        } else {
            retorno = new String[] { "valor" };
        }

        return retorno;
    }
}
