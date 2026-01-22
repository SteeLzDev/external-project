package com.zetra.econsig.persistence.query.lote;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusBlocoProcessamentoEnum;

/**
 * <p>Title: ListarBlocosProcessamentoLoteQuery</p>
 * <p>Description: Lista os blocos de processamento de lote</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarBlocosProcessamentoLoteQuery extends HQuery  {

    private final String cplArquivoEconsig;
    private final StatusBlocoProcessamentoEnum status;
    private final String csaCodigo;

    public ListarBlocosProcessamentoLoteQuery(String cplArquivoEconsig, StatusBlocoProcessamentoEnum status, String csaCodigo) {
        this.cplArquivoEconsig = cplArquivoEconsig;
        this.status = status;
        this.csaCodigo = csaCodigo;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT ");
        corpoBuilder.append("  bpl.consignataria.csaCodigo, ");
        corpoBuilder.append("  bpl.bplNumLinha, ");
        corpoBuilder.append("  bpl.bplLinha, ");
        corpoBuilder.append("  bpl.bplCampos, ");
        corpoBuilder.append("  bpl.bplCritica ");
        corpoBuilder.append("FROM BlocoProcessamentoLote bpl ");
        corpoBuilder.append("WHERE bpl.cplArquivoEconsig = :cplArquivoEconsig ");

        if (status != null) {
            corpoBuilder.append("AND bpl.sbpCodigo = :sbpCodigo ");
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND bpl.consignataria.csaCodigo = :csaCodigo ");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("cplArquivoEconsig", cplArquivoEconsig, query);

        if (status != null) {
            defineValorClausulaNomeada("sbpCodigo", status.getCodigo(), query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSA_CODIGO,
                Columns.BPL_NUM_LINHA,
                Columns.BPL_LINHA,
                Columns.BPL_CAMPOS,
                Columns.BPL_CRITICA,
        };
    }
}
