package com.zetra.econsig.persistence.query.servidor;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaRegistrosServidoresQuery</p>
 * <p>Description: Lista registros servidores de acordo com os filtros passados.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaSerParaArquivamentoQuery extends HQuery {

    public List<String> orgCodigos;
    public List<String> estCodigos;
    public AcessoSistema responsavel;

    public ListaSerParaArquivamentoQuery() {
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        List<String> srsCodigos = new ArrayList<>(CodedValues.SRS_INATIVOS);

        Object objQtdDiasArquivarSerExcluido = ParamSist.getInstance().getParam(CodedValues.TPC_QTD_DIAS_ARQUIVAR_SERVIDOR_EXCLUIDO_IMPORTACAO_MARGEM, responsavel);
        int qtdDiasArquivarSerExcluido = TextHelper.parseIntErrorSafe(objQtdDiasArquivarSerExcluido, 0);

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append(" SELECT ");
        corpoBuilder.append(" rse.rseCodigo, ");
        corpoBuilder.append(" ser.serCodigo ");
        corpoBuilder.append(" FROM RegistroServidor rse");
        corpoBuilder.append(" INNER JOIN rse.servidor ser");
        corpoBuilder.append(" INNER JOIN rse.orgao org");
        corpoBuilder.append(" INNER JOIN org.estabelecimento est");
        corpoBuilder.append(" INNER JOIN rse.statusRegistroServidor srs");

        /**
         * 4) Listar os registros servidores excluídos ou falecidos, que não possuem consignações, ativas ou arquivadas,
         * em que a maior data da ocorrência de tipo 67 ou o campo RSE_DATA_CARGA seja anterior ao prazo em dias do parâmetro do item 1.
         */
        corpoBuilder.append(" WHERE srs.srsCodigo ").append(criaClausulaNomeada("srsCodigos", srsCodigos));
        corpoBuilder.append(" AND NOT EXISTS (SELECT ade.adeCodigo FROM AutDesconto ade WHERE ade.registroServidor.rseCodigo = rse.rseCodigo)");
        corpoBuilder.append(" AND NOT EXISTS (SELECT hta.adeCodigo FROM HtAutDesconto hta WHERE hta.registroServidor.rseCodigo = rse.rseCodigo)");
        corpoBuilder.append(" AND (to_days(current_date) - to_days(COALESCE((");
        corpoBuilder.append(" SELECT MAX(ors.orsData) FROM OcorrenciaRegistroSer ors");
        corpoBuilder.append(" WHERE ors.registroServidor.rseCodigo = rse.rseCodigo and ors.tipoOcorrencia.tocCodigo = '").append(CodedValues.TOC_RSE_EXCLUSAO_POR_CARGA_MARGEM).append("' ");
        corpoBuilder.append("), rse.rseDataCarga)) >= to_numeric(:qtdDiasArquivarSerExcluido) ");
        corpoBuilder.append(" OR (not exists (SELECT 1 FROM OcorrenciaRegistroSer ors ");
        corpoBuilder.append(" WHERE ors.registroServidor.rseCodigo = rse.rseCodigo and ors.tipoOcorrencia.tocCodigo = '").append(CodedValues.TOC_RSE_EXCLUSAO_POR_CARGA_MARGEM).append("') ");
        corpoBuilder.append(" AND rse.rseDataCarga IS NULL)) ");

        if (estCodigos != null && !estCodigos.isEmpty()) {
            corpoBuilder.append(" AND est.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigos));
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" AND org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("srsCodigos", srsCodigos, query);
        defineValorClausulaNomeada("qtdDiasArquivarSerExcluido", qtdDiasArquivarSerExcluido, query);

        if (estCodigos != null && !estCodigos.isEmpty()) {
            defineValorClausulaNomeada("estCodigo", estCodigos, query);
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSE_CODIGO,
                Columns.SER_CODIGO
        };
    }
}
