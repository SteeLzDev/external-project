package com.zetra.econsig.persistence.query.beneficios.beneficiario;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarMotivoDependenciaQuery</p>
 * <p>Description: Listagem de motivo dependência.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaTipoArquivoByTarCodigoQuery extends HQuery {

    public List<String> tarCodigos = new ArrayList<String>();

    public ListaTipoArquivoByTarCodigoQuery() {
    }

    public ListaTipoArquivoByTarCodigoQuery(List<String> tarCodigos) {
        this.tarCodigos = tarCodigos;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                "tar.tarCodigo, " +
                "tar.tarDescricao, " +
                "tar.tarQtdDiasLimpeza " +
                "from TipoArquivo tar " +
                "where tar.tarCodigo in (:tarCodigos) ";

        Query<Object[]> query = instanciarQuery(session, corpo);

        if (!tarCodigos.isEmpty()) {
            defineValorClausulaNomeada("tarCodigos", tarCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TAR_CODIGO,
                Columns.TAR_DESCRICAO,
                Columns.TAR_QTD_DIAS_LIMPEZA
        };
    }
}