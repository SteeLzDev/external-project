package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.compra.ListaAcompanhamentoCompraQuery;
import com.zetra.econsig.values.Columns;

import java.util.List;

/**
 * <p>Title: RelatorioCompraContratoQuery</p>
 * <p>Description: Query Relatório de Acompanhamento de Compra de Contrato</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioCompraContratoQuery extends ReportHQuery {
    private String csaCodigo;
    private String corCodigo;
    private List<String> orgCodigo;

    public TransferObject parametrosTO;

    public AcessoSistema responsavel;

    @Override
    public void setCriterios(TransferObject criterio) {
        csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
        corCodigo = (String) criterio.getAttribute(Columns.COR_CODIGO);
        orgCodigo = (List<String>) criterio.getAttribute(Columns.ORG_CODIGO);
        parametrosTO = criterio;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        ListaAcompanhamentoCompraQuery queryListaAcompanhamentoCompra = new ListaAcompanhamentoCompraQuery();
        queryListaAcompanhamentoCompra.parametrosTO = parametrosTO;
        queryListaAcompanhamentoCompra.csaCodigo = csaCodigo;
        queryListaAcompanhamentoCompra.corCodigo = corCodigo;
        queryListaAcompanhamentoCompra.orgCodigos = orgCodigo;
        queryListaAcompanhamentoCompra.orderByCsaNome = true;
        queryListaAcompanhamentoCompra.responsavel = responsavel;
        Query<Object[]> queryPreparada = queryListaAcompanhamentoCompra.preparar(session);
        setQueryString(queryPreparada);
        return queryPreparada;
    }
}
