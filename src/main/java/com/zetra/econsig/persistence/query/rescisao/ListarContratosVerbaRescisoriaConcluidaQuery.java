package com.zetra.econsig.persistence.query.rescisao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusVerbaRescisoriaEnum;

/**
 * <p>Title: ListarContratosVerbaRescisoriaConcluidaQuery</p>
 * <p>Description: Listar contratos que foram criados para reter valor da verba rescisória</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarContratosVerbaRescisoriaConcluidaQuery extends HQuery  {

    public String vrrCodigo;

    public ListarContratosVerbaRescisoriaConcluidaQuery() {
        super();
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        // lista de status de autorização que permitem solicitação de saldo
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT ade.adeCodigo,");
        corpoBuilder.append(" ade.adeNumero, ");
        corpoBuilder.append(" ade.adeVlr, ");
        corpoBuilder.append(" csa.csaNomeAbrev, ");
        corpoBuilder.append(" svc.svcDescricao ");
        corpoBuilder.append("FROM VerbaRescisoriaRse vrr ");
        corpoBuilder.append("INNER JOIN vrr.registroServidor rse ");
        corpoBuilder.append("INNER JOIN rse.servidor ser ");
        corpoBuilder.append("INNER JOIN rse.autDescontoSet ade ");
        corpoBuilder.append("INNER JOIN ade.relacionamentoAutorizacaoByAdeCodigoDestinoSet rad ");
        corpoBuilder.append("      WITH rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_VERBA_RESCISORIA).append("' ");
        corpoBuilder.append("INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append("INNER JOIN vco.convenio cnv ");
        corpoBuilder.append("INNER JOIN cnv.consignataria csa ");
        corpoBuilder.append("INNER JOIN cnv.servico svc ");
        corpoBuilder.append("WHERE vrr.vrrCodigo ").append(criaClausulaNomeada("vrrCodigo", vrrCodigo));
        corpoBuilder.append("AND vrr.statusVerbaRescisoria.svrCodigo = '").append(StatusVerbaRescisoriaEnum.CONCLUIDO.getCodigo()).append("' ");
        corpoBuilder.append("ORDER BY ade.adeNumero ");
        
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("vrrCodigo", vrrCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
                Columns.ADE_NUMERO,
                Columns.ADE_VLR,
                Columns.CSA_NOME_ABREV,
                Columns.SVC_DESCRICAO
        };
    }
}
