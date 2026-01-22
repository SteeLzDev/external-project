package com.zetra.econsig.persistence.query.arquivo;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaHistoricoArquivoUploadQuery</p>
 * <p>Description: retorna registros da tabela tb_historico_arquivo</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaHistoricoArquivoUploadQuery extends HQuery {

    public Date dataInicial;
    public Date dataFinal;
    public List<String> tarCodigo;
    public Date periodo;
    public String tipoEntidade;
    public String funCodigo;
    public String orgCodigo;
    public String estCodigo;

    public String csaCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select ");
        corpoBuilder.append("   distinct har.harCodigo, ");
        corpoBuilder.append("   har.usuario.usuCodigo, ");
        corpoBuilder.append("   har.tipoArquivo.tarCodigo, ");
        corpoBuilder.append("   har.harNomeArquivo, ");
        corpoBuilder.append("   har.harQtdLinhas, ");
        corpoBuilder.append("   har.harResultadoProc, ");
        corpoBuilder.append("   har.harDataProc, ");
        corpoBuilder.append("   har.harPeriodo, ");
       	corpoBuilder.append("CASE ");
       	corpoBuilder.append("WHEN horg.orgCodigo IS NOT NULL THEN horg.orgCodigo ");
       	corpoBuilder.append("WHEN hest.estCodigo IS NOT NULL THEN hest.estCodigo ");
       	corpoBuilder.append("WHEN hcse.cseCodigo IS NOT NULL THEN hcse.cseCodigo ");
        if (AcessoSistema.ENTIDADE_CSA.equals(tipoEntidade)) {
            corpoBuilder.append("WHEN hcsa.csaCodigo IS NOT NULL THEN hcsa.csaCodigo ");
        }
       	corpoBuilder.append("ELSE '").append(CodedValues.CSE_CODIGO_SISTEMA).append("' ");
       	corpoBuilder.append("END AS CODIGO_ENTIDADE, ");
       	corpoBuilder.append("   har.funcao.funCodigo ");
        corpoBuilder.append(" from HistoricoArquivo har ");
        corpoBuilder.append(" left join har.historicoArquivoOrgSet horg ");
        corpoBuilder.append(" left join har.historicoArquivoEstSet hest ");
        corpoBuilder.append(" left join har.historicoArquivoCseSet hcse ");
        if (AcessoSistema.ENTIDADE_CSA.equals(tipoEntidade)) {
            corpoBuilder.append(" left join har.historicoArquivoCsaSet hcsa ");
        }

        if (AcessoSistema.ENTIDADE_ORG.equals(tipoEntidade)) {
        	corpoBuilder.append(" inner join horg.orgao.usuarioOrgSet ");
        }

        if (AcessoSistema.ENTIDADE_CSA.equals(tipoEntidade)) {
            corpoBuilder.append(" inner join hcsa.consignataria.usuarioCsaSet ");
        }

        corpoBuilder.append(" where 1=1 ");

        if ((dataInicial != null) && (dataFinal != null)) {
            corpoBuilder.append(" and har.harDataProc between :dataIni and :dataFim ");
        }
        if (tarCodigo != null){
            corpoBuilder.append(" and har.tipoArquivo.tarCodigo ").append(criaClausulaNomeada("tarCodigo", tarCodigo));
        }
        if (periodo != null) {
            corpoBuilder.append(" and har.harPeriodo ").append(criaClausulaNomeada("harPeriodo", periodo));
        }
        if (orgCodigo != null) {
            corpoBuilder.append(" and horg.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }
        if (estCodigo != null) {
            corpoBuilder.append(" and (hest.estabelecimento.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigo));
            corpoBuilder.append(" or horg.orgao.estabelecimento.estCodigo ").append(criaClausulaNomeada("estCodigo2", estCodigo)).append(" )");
        }
        if (AcessoSistema.ENTIDADE_CSA.equals(tipoEntidade) && !TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and hcsa.csaCodigo = :csaCodigo");
        }
        if (funCodigo != null) {
            corpoBuilder.append(" and har.funcao.funCodigo ").append(criaClausulaNomeada("funCodigo", funCodigo));
            corpoBuilder.append(" order by har.harDataProc desc");
        }

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if ((dataInicial != null) && (dataFinal != null)) {
            defineValorClausulaNomeada("dataIni", dataInicial, query);
            defineValorClausulaNomeada("dataFim", dataFinal, query);
        }
        if (tarCodigo != null){
            defineValorClausulaNomeada("tarCodigo", tarCodigo, query);
        }
        if (periodo != null) {
            defineValorClausulaNomeada("harPeriodo", periodo, query);
        }
        if (funCodigo != null) {
            defineValorClausulaNomeada("funCodigo", funCodigo, query);
            query.setMaxResults(5);
        }
        if (orgCodigo != null) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }
        if (estCodigo != null) {
            defineValorClausulaNomeada("estCodigo", estCodigo, query);
            defineValorClausulaNomeada("estCodigo2", estCodigo, query);
        }
        if (AcessoSistema.ENTIDADE_CSA.equals(tipoEntidade) && !TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        	return new String[] {
                    Columns.HAR_CODIGO,
                    Columns.HAR_USU_CODIGO,
                    Columns.HAR_TAR_CODIGO,
                    Columns.HAR_NOME_ARQUIVO,
                    Columns.HAR_QTD_LINHAS,
                    Columns.HAR_RESULTADO_PROC,
                    Columns.HAR_DATA_PROC,
                    Columns.HAR_PERIODO,
                    "CODIGO_ENTIDADE",
                    Columns.HAR_FUN_CODIGO
                    };
    }
}
