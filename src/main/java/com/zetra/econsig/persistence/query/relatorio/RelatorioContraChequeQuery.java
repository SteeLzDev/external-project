package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p> Title: RelatorioConsignacoesQuery</p>
 * <p> Description: Relatório de contra chaque por período.</p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioContraChequeQuery extends ReportHQuery {
    public String periodo;
    public String rseCodigo;

    public AcessoSistema responsavel;

    @Override
    public void setCriterios(TransferObject criterio) {
        periodo = (String) criterio.getAttribute("PERIODO");
        rseCodigo = (String) criterio.getAttribute("RSE_CODIGO");

    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select ccq.ccqTexto as contracheque, ccqDataCarga as dataCarga from ContrachequeRegistroSer ccq where ");
        corpoBuilder.append(" ccq.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpoBuilder.append(" and ccq.ccqPeriodo ").append(criaClausulaNomeada("ccqPeriodo", periodo));

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("ccqPeriodo", parseDateString(periodo), query);

        return query;
    }


    @Override
    protected String[] getFields() {
        return new String[] {
                "contracheque",
                "dataCarga"
        };
    }

}
