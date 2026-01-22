package com.zetra.econsig.persistence.query.folha;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarHistoricoProcessamentoQuery</p>
 * <p>Description: Listar o histórico de processamento.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarHistoricoProcessamentoQuery extends HQuery {

    public List<String> estCodigos;
    public List<String> orgCodigos;
    public Date hprPeriodo;
    public boolean orderDesc = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select hpr.hprCodigo, ");
        corpoBuilder.append(" hpr.hprPeriodo, ");
        corpoBuilder.append(" hpr.hprDataIni, ");
        corpoBuilder.append(" hpr.hprDataFim, ");
        corpoBuilder.append(" hpr.hprArquivoMargem, ");
        corpoBuilder.append(" hpr.hprConfEntradaMargem, ");
        corpoBuilder.append(" hpr.hprConfTradutorMargem, ");
        corpoBuilder.append(" hpr.hprLinhasArquivoMargem, ");
        corpoBuilder.append(" hpr.hprArquivoRetorno, ");
        corpoBuilder.append(" hpr.hprConfEntradaRetorno, ");
        corpoBuilder.append(" hpr.hprConfTradutorRetorno, ");
        corpoBuilder.append(" hpr.hprLinhasArquivoRetorno, ");
        corpoBuilder.append(" hpr.hprChaveIdentificacao, ");
        corpoBuilder.append(" hpr.hprOrdemExcCamposChave ");
        corpoBuilder.append(" from HistoricoProcessamento hpr ");

        corpoBuilder.append(" where 1=1 ");

        if (hprPeriodo != null) {
            corpoBuilder.append(" and hpr.hprPeriodo ").append(criaClausulaNomeada("hprPeriodo", hprPeriodo));
        }

        if (estCodigos != null && !estCodigos.isEmpty()) {
            corpoBuilder.append(" and hpr.estabelecimento.estCodigo ").append(criaClausulaNomeada("estCodigos", estCodigos));
        } else if (orgCodigos != null && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" and hpr.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigos", orgCodigos));
        }

        if (orderDesc) {
            corpoBuilder.append(" order by hpr.hprPeriodo desc ");
        } else {
            corpoBuilder.append(" order by hpr.hprPeriodo, hpr.hprDataIni ");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (hprPeriodo != null) {
            defineValorClausulaNomeada("hprPeriodo", hprPeriodo, query);
        }

        if (estCodigos != null && !estCodigos.isEmpty()) {
            defineValorClausulaNomeada("estCodigos", estCodigos, query);
        } else if (orgCodigos != null && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigos", orgCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.HPR_CODIGO,
                Columns.HPR_PERIODO,
                Columns.HPR_DATA_INI,
                Columns.HPR_DATA_FIM,
                Columns.HPR_ARQUIVO_MARGEM,
                Columns.HPR_CONF_ENTRADA_MARGEM,
                Columns.HPR_CONF_TRADUTOR_MARGEM,
                Columns.HPR_LINHAS_ARQUIVO_MARGEM,
                Columns.HPR_ARQUIVO_RETORNO,
                Columns.HPR_CONF_ENTRADA_RETORNO,
                Columns.HPR_CONF_TRADUTOR_RETORNO,
                Columns.HPR_LINHAS_ARQUIVO_RETORNO,
                Columns.HPR_CHAVE_IDENTIFICACAO,
                Columns.HPR_ORDEM_EXC_CAMPOS_CHAVE
        };
    }
}
