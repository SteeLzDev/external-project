package com.zetra.econsig.persistence.query.consignacao;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignacaoOcorrenciaPeriodoQuery</p>
 * <p>Description: Lista contratos com ocorrências dadas pelo filtro dentro de um período que pode ser configurado</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author: $
 * $Revision: $
 * $Date: $
 */
public class ListaConsignacaoOcorrenciaPeriodoQuery extends HQuery {
    public Date dataIni;

    public Date dataFim;

    public String csaCodigo;

    public String corCodigo;

    public List<String> tocCodigos;

    public boolean count;

    public Date periodoIni;

    public Date periodoFim;

    public boolean sum;
    
    public List<String> sadCodigos;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();

        if (count) {
            corpoBuilder.append("select count(*) as total ");
        } else if (sum) {
            corpoBuilder.append("select sum(ade.adeVlr) ");
        }else {
            corpoBuilder.append(" select ade.adeNumero, oca.ocaData, toc.tocCodigo, ade.adeVlr, sad.sadCodigo, ");
            corpoBuilder.append(" toc.tocDescricao, ser.serCpf, ade.adeCodigo, svc.svcCodigo, cor.corNome ");
        }

        corpoBuilder.append(" from AutDesconto ade ");
        corpoBuilder.append(" inner join ade.ocorrenciaAutorizacaoSet oca ");
        corpoBuilder.append(" inner join ade.verbaConvenio vco ");
        corpoBuilder.append(" inner join ade.statusAutorizacaoDesconto sad ");
        corpoBuilder.append(" inner join oca.tipoOcorrencia toc ");
        corpoBuilder.append(" inner join vco.convenio cnv ");
        corpoBuilder.append(" inner join ade.registroServidor.servidor ser");
        corpoBuilder.append(" inner join cnv.servico svc ");
        corpoBuilder.append(" left join ade.correspondente cor");
        corpoBuilder.append(" where 1=1 ");

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(corCodigo)) {
            corpoBuilder.append(" AND ade.correspondente.corCodigo ").append(criaClausulaNomeada("corCodigo", corCodigo));
        }

        if (tocCodigos != null && !tocCodigos.isEmpty()) {
            corpoBuilder.append(" and oca.tipoOcorrencia.tocCodigo ").append(criaClausulaNomeada("tocCodigos", tocCodigos));
        }

        if (dataIni != null && dataFim != null) {
            corpoBuilder.append(" AND oca.ocaData between :dataIni and :dataFim");
        }

        if (periodoIni != null && periodoFim != null) {
            corpoBuilder.append(" AND oca.ocaData between :periodoIni and :periodoFim");
        }
        
        if (sadCodigos != null && !sadCodigos.isEmpty()) {
            corpoBuilder.append(" and sad.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));
        }

        corpoBuilder.append(" order by oca.ocaData desc ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(corCodigo)) {
            defineValorClausulaNomeada("corCodigo", corCodigo, query);
        }

        if (tocCodigos != null && !tocCodigos.isEmpty()) {
            defineValorClausulaNomeada("tocCodigos", tocCodigos, query);
        }
        
        if (sadCodigos != null && !sadCodigos.isEmpty()) {
            defineValorClausulaNomeada("sadCodigos", sadCodigos, query);
        }

        if (dataIni != null && dataFim != null) {
            defineValorClausulaNomeada("dataIni", dataIni, query);
            defineValorClausulaNomeada("dataFim", dataFim, query);
        }

        if (periodoIni != null && periodoFim != null) {
            defineValorClausulaNomeada("periodoIni", periodoIni, query);
            defineValorClausulaNomeada("periodoFim", periodoFim, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_NUMERO,
                Columns.OCA_DATA,
                Columns.TOC_CODIGO,
                Columns.ADE_VLR,
                Columns.SAD_CODIGO,
                Columns.TOC_DESCRICAO,
                Columns.SER_CPF,
                Columns.ADE_CODIGO,
                Columns.SVC_CODIGO,
                Columns.COR_NOME
        };
    }
}
