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
public class RelatorioInadimplenciaStatusRegistroServidorQuery extends ReportHQuery {
    public String prdDtDesconto;
    public String csaCodigo;
    public String csaProjetoInadimplencia;
    public String naturezaServico;
    public String srsCodigo;
    public AcessoSistema responsavel;

    @Override
    public void setCriterios(TransferObject criterio) {
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append(" select ");

        corpoBuilder.append(" count(*) as total ");

        corpoBuilder.append(" from AutDesconto ade ");

        corpoBuilder.append(" inner join ade.verbaConvenio vco ");
        corpoBuilder.append(" inner join vco.convenio cnv ");
        corpoBuilder.append(" inner join cnv.consignataria csa ");

        corpoBuilder.append(" inner join ade.parcelaDescontoSet prd ");

        corpoBuilder.append(" inner join ade.registroServidor rse ");
        corpoBuilder.append(" inner join rse.statusRegistroServidor srs ");

        if (!TextHelper.isNull(naturezaServico)) {
            corpoBuilder.append(" inner join cnv.servico svc ");
        }

        corpoBuilder.append(" where csa.csaProjetoInadimplencia ").append(criaClausulaNomeada("csaProjetoInadimplencia", csaProjetoInadimplencia));
        corpoBuilder.append(" and prd.prdDataDesconto ").append(criaClausulaNomeada("prdDtDesconto", prdDtDesconto));

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(naturezaServico)) {
            corpoBuilder.append(" and svc.naturezaServico.nseCodigo ").append(criaClausulaNomeada("naturezaServico", naturezaServico));
        }

        if (!TextHelper.isNull(srsCodigo)) {
            corpoBuilder.append(" and srs.srsCodigo ").append(criaClausulaNomeada("srsCodigo", srsCodigo));
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

        if (!TextHelper.isNull(srsCodigo)) {
            defineValorClausulaNomeada("srsCodigo", srsCodigo, queryInst);
        }

        return queryInst;
    }

    @Override
    protected String[] getFields() {
        return new String[] { "total" };
    }
}
