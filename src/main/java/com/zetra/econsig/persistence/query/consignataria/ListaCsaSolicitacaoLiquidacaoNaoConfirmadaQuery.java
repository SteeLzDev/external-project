package com.zetra.econsig.persistence.query.consignataria;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;

/**
 * <p>Title: ListaCsaSolicitacaoLIquidacaoNaoConfirmadaQuery</p>
 * <p>Description: Lista Consignatárias que não confirmaram a solicitação de liquidação</p>
 * <p>Copyright: Copyright (c) 2021</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco $
 * $Revision:  $
 * $Date: $
 */
public class ListaCsaSolicitacaoLiquidacaoNaoConfirmadaQuery extends HQuery {

    public int diasBloqueioNaoConfirmacao;
    public String csaCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "SELECT csa.csaCodigo," +
                       "csa.csaAtivo," +
                       "csa.ncaCodigo," +
                       "soa.soaCodigo," +
                       "soa.soaDataValidade";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" FROM AutDesconto ade");
        corpoBuilder.append(" JOIN ade.solicitacaoAutorizacaoSet soa");
        corpoBuilder.append(" JOIN ade.verbaConvenio vco");
        corpoBuilder.append(" JOIN vco.convenio cnv");
        corpoBuilder.append(" JOIN cnv.consignataria csa");
        corpoBuilder.append(" JOIN soa.usuario usu ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioCseSet usuarioCse ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioSupSet usuarioSup ");
        corpoBuilder.append(" WHERE 1=1 ");
        corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo ='").append(CodedValues.SAD_AGUARD_LIQUIDACAO).append("'");
        corpoBuilder.append(" AND EXISTS (select 1 from OcorrenciaAutorizacao oca where ade.adeCodigo = oca.autDesconto.adeCodigo and oca.tipoOcorrencia.tocCodigo ='").append(CodedValues.TOC_SOLICITAR_LIQUIDACAO_CONSIGNACAO).append("')");
        corpoBuilder.append(" AND soa.tipoSolicitacao.tisCodigo='").append(TipoSolicitacaoEnum.SOLICITACAO_LIQUIDACAO_CONTRATO.getCodigo()).append("'");
        corpoBuilder.append(" AND soa.statusSolicitacao.ssoCodigo ='").append(StatusSolicitacaoEnum.PENDENTE.getCodigo()).append("'");
        corpoBuilder.append(" AND (add_day(soa.soaData,:diasBloqueioNaoConfirmacao) < current_timestamp() OR soa.soaDataValidade < current_timestamp())");
        corpoBuilder.append(" AND (usuarioCse.usuario.usuCodigo is not null or usuarioSup.usuario.usuCodigo is not null)");

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("diasBloqueioNaoConfirmacao", diasBloqueioNaoConfirmacao, query);

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
        		Columns.CSA_CODIGO,
        		Columns.CSA_ATIVO,
        		Columns.NCA_CODIGO,
        		Columns.SOA_CODIGO,
        		Columns.SOA_DATA_VALIDADE
        };
    }
}
