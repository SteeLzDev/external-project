package com.zetra.econsig.persistence.query.parametro;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaParamMensagemSolicitacaoOutroSvcQuery</p>
 * <p>Description: Listagem de parâmetro de serviço TPS_MSG_EXIBIR_SOLICITACAO_SERVIDOR_OFERTA_OUTRO_SVC
 * na solicitação de consignação.</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaParamMensagemSolicitacaoOutroSvcQuery extends HQuery {

    public String csaCodigo;
    public String svcCodigo;
    public short prazo;
    public short dia;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT cft.cftCodigo, cft.cftVlr, svc.svcCodigo, svc.svcDescricao, psc.pscVlr ");
        corpoBuilder.append("FROM ParamSvcConsignataria psc ");
        corpoBuilder.append("INNER JOIN psc.servico svc ");
        corpoBuilder.append("INNER JOIN psc.consignataria csa ");
        corpoBuilder.append("INNER JOIN svc.prazoSet prz ");
        corpoBuilder.append("INNER JOIN prz.prazoConsignatariaSet pzc ");
        corpoBuilder.append("INNER JOIN pzc.coeficienteAtivoSet cft ");

        // Parâmetro de serviço TPS_MSG_EXIBIR_SOLICITACAO_SERVIDOR_OFERTA_OUTRO_SVC cadastrado com valor não vazip
        corpoBuilder.append("WHERE psc.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_MSG_EXIBIR_SOLICITACAO_SERVIDOR_OFERTA_OUTRO_SVC).append("'");
        corpoBuilder.append(" AND NULLIF(TRIM(psc.pscVlr), '') IS NOT NULL ");

        // Serviço ativo e da mesma natureza do serviço passado, diferente do serviço passado
        corpoBuilder.append(" AND (svc.svcAtivo = ").append(CodedValues.STS_ATIVO).append(" OR svc.svcAtivo IS NULL) ");
        corpoBuilder.append(" AND svc.svcCodigo <> :svcCodigo");
        corpoBuilder.append(" AND svc.naturezaServico.nseCodigo = (");
        corpoBuilder.append("  select svcOrigem.naturezaServico.nseCodigo from Servico svcOrigem");
        corpoBuilder.append("  where svcOrigem.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        corpoBuilder.append(") ");

        // Da consignatária informada
        corpoBuilder.append(" AND pzc.consignataria.csaCodigo = csa.csaCodigo ");
        corpoBuilder.append(" AND csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));

        // De prazo ativo, para o serviço e para a consignatária
        corpoBuilder.append(" AND (prz.przAtivo = ").append(CodedValues.STS_ATIVO).append(" OR prz.przAtivo IS NULL) ");
        corpoBuilder.append(" AND (pzc.przCsaAtivo = ").append(CodedValues.STS_ATIVO).append(" OR pzc.przCsaAtivo IS NULL) ");

        // Para o prazo selecionado pelo servidor
        corpoBuilder.append(" AND prz.przVlr ").append(criaClausulaNomeada("przVlr", prazo));

        // Para coeficiente cadastrado e ativo
        corpoBuilder.append(" AND (cft.cftVlr > 0)");
        corpoBuilder.append(" AND (cft.cftDia ").append(criaClausulaNomeada("cftDia", dia)).append(" OR cft.cftDia = 0)");
        corpoBuilder.append(" AND (cft.cftDataIniVig <= current_date()) ");
        corpoBuilder.append(" AND (cft.cftDataFimVig >= current_date() OR cft.cftDataFimVig IS NULL) ");

        corpoBuilder.append(" ORDER BY cft.cftVlr");

        // Define os valores para os parâmetros nomeados
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("przVlr", prazo, query);
        defineValorClausulaNomeada("cftDia", dia, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CFT_CODIGO,
                Columns.CFT_VLR,
                Columns.SVC_CODIGO,
                Columns.SVC_DESCRICAO,
                Columns.PSC_VLR
        };
    }
}
