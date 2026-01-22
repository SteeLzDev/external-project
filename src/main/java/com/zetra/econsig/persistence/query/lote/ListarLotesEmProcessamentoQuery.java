package com.zetra.econsig.persistence.query.lote;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusBlocoProcessamentoEnum;

/**
 * <p>Title: ListarLotesEmProcessamentoQuery</p>
 * <p>Description: Lista os lotes que possuem blocos de processamento aguardando execução</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarLotesEmProcessamentoQuery extends HQuery  {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT ");
        corpoBuilder.append("  cpl.cplArquivoEconsig, ");
        corpoBuilder.append("  cpl.cplStatus, ");
        corpoBuilder.append("  cpl.cplData, ");
        corpoBuilder.append("  cpl.cplCanal, ");
        corpoBuilder.append("  cpl.cplParametros, ");
        corpoBuilder.append("  cpl.usuario.usuCodigo, ");

        corpoBuilder.append("  CASE WHEN EXISTS (");
        corpoBuilder.append("     SELECT 1 FROM cpl.blocoProcessamentoLoteSet bpl ");
        corpoBuilder.append("     WHERE bpl.sbpCodigo = :sbpCodigo ");
        corpoBuilder.append("  ) THEN 'S' ELSE 'N' END as TEM_BLOCO_PENDENTE ");

        corpoBuilder.append("FROM ControleProcessamentoLote cpl ");
        corpoBuilder.append("ORDER BY cpl.cplData ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("sbpCodigo", StatusBlocoProcessamentoEnum.AGUARD_PROCESSAMENTO.getCodigo(), query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CPL_ARQUIVO_ECONSIG,
                Columns.CPL_STATUS,
                Columns.CPL_DATA,
                Columns.CPL_CANAL,
                Columns.CPL_PARAMETROS,
                Columns.CPL_USU_CODIGO,
                "TEM_BLOCO_PENDENTE"
        };
    }
}
