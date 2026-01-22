package com.zetra.econsig.persistence.query.arquivo;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaHistoricoArquivoQuery</p>
 * <p>Description: retorna registros da tabela tb_historico_arquivo</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaHistoricoArquivoQuery extends HQuery {

    public Date dataInicial;
    public Date dataFinal;
    public List<String> tarCodigo;
    public Date periodo;
    public String tipoEntidade;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select ");
        corpoBuilder.append("   har.harCodigo, ");
        corpoBuilder.append("   har.usuario.usuCodigo, ");
        corpoBuilder.append("   har.tipoArquivo.tarCodigo, ");
        corpoBuilder.append("   har.harNomeArquivo, ");
        corpoBuilder.append("   har.harQtdLinhas, ");
        corpoBuilder.append("   har.harResultadoProc, ");
        corpoBuilder.append("   har.harDataProc, ");
        corpoBuilder.append("   har.harPeriodo, ");
        corpoBuilder.append("   har.harObs ");
        corpoBuilder.append(" from HistoricoArquivo har ");
        if (!TextHelper.isNull(tipoEntidade)) {
            switch (tipoEntidade){
                case AcessoSistema.ENTIDADE_CSE :
                    corpoBuilder.append(" inner join har.historicoArquivoCseSet ");
                    break;
                case AcessoSistema.ENTIDADE_ORG :
                    corpoBuilder.append(" inner join har.historicoArquivoOrgSet ");
                    break;
                case AcessoSistema.ENTIDADE_EST :
                    corpoBuilder.append(" inner join har.historicoArquivoEstSet ");
                    break;
            }
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
                Columns.HAR_OBS
                };
    }
}
