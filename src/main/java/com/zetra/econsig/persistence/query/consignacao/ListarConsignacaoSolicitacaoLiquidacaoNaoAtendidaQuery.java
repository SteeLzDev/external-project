package com.zetra.econsig.persistence.query.consignacao;
import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;

/**
 * <p>Title: ListarConsignacaoSolicitacaoLiquidacaoNaoAtendidaQuery</p>
 * <p>Description: Lista solicitação de consignações com solicitações
 *  de liquidação não atendida.</p>
 * <p>Copyright: Copyright (c) 2021</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author: ricardo.magno $
 * $Revision: $
 * $Date: $
 */
public class ListarConsignacaoSolicitacaoLiquidacaoNaoAtendidaQuery extends HQuery {

    public String csaCodigo;
    public boolean count;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String[] ssoCodigo = {StatusSolicitacaoEnum.PENDENTE.getCodigo()};
        String[] tisCodigo = {TipoSolicitacaoEnum.SOLICITACAO_LIQUIDACAO_CONTRATO.getCodigo()};
        String[] sadCodigo = {CodedValues.SAD_AGUARD_LIQUIDACAO};
        String corpo = "";

        if (count) {
            corpo = "select count(*) as total ";
        } else {
            corpo = "select "
            	  +  "ade.adeCodigo, "
                  +  "ade.adeNumero, "
                  +  "ade.adeIndice, "
                  +  "ade.adeTipoVlr, "
                  +  "ade.adeVlr, "
                  +  "ade.adeIdentificador, "
                  +  "ade.adePrazo, "
                  +  "ade.adeData, "
                  +  "ade.adePrdPagas, "
                  +  "cnv.cnvCodVerba, "
                  +  "svc.svcDescricao, "
                  +  "usu.usuLogin, "
                  +  "usu.usuCodigo, "
                  +  "usu.usuTipoBloq, "
                  +  "sad.sadDescricao "
                  ;
        }


        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" FROM AutDesconto ade ");
        corpoBuilder.append(" INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append(" INNER JOIN vco.convenio cnv ");
        corpoBuilder.append(" INNER JOIN cnv.servico svc ");
        corpoBuilder.append(" INNER JOIN cnv.consignataria csa ");
        corpoBuilder.append(" INNER JOIN ade.statusAutorizacaoDesconto sad ");
        corpoBuilder.append(" INNER JOIN ade.solicitacaoAutorizacaoSet soa ");
        corpoBuilder.append(" INNER JOIN soa.usuario usu ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioCseSet usuarioCse ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioSupSet usuarioSup ");
        corpoBuilder.append(" WHERE csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpoBuilder.append(" AND soa.tipoSolicitacao.tisCodigo ").append(criaClausulaNomeada("tisCodigo", tisCodigo));
        corpoBuilder.append(" AND soa.statusSolicitacao.ssoCodigo ").append(criaClausulaNomeada("ssoCodigo", ssoCodigo));
        corpoBuilder.append(" AND sad.sadCodigo ").append(criaClausulaNomeada("sadCodigo", sadCodigo));
        corpoBuilder.append(" AND EXISTS (SELECT 1 FROM OcorrenciaAutorizacao oca WHERE ade.adeCodigo = oca.autDesconto.adeCodigo AND oca.tipoOcorrencia.tocCodigo = '").append(CodedValues.TOC_SOLICITAR_LIQUIDACAO_CONSIGNACAO).append(" ')");
        corpoBuilder.append(" AND soa.soaDataValidade is not null ");
        corpoBuilder.append(" AND soa.soaDataValidade < current_date() ");
        corpoBuilder.append(" AND (usuarioCse.usuCodigo is not null or usuarioSup.usuCodigo is not null)");
        corpoBuilder.append(" ORDER BY soa.soaData DESC ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("tisCodigo", tisCodigo, query);
        defineValorClausulaNomeada("ssoCodigo", ssoCodigo, query);
        defineValorClausulaNomeada("sadCodigo", sadCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
        		Columns.ADE_CODIGO,
        		Columns.ADE_NUMERO,
        		Columns.ADE_INDICE,
                Columns.ADE_TIPO_VLR,
                Columns.ADE_VLR,
                Columns.ADE_IDENTIFICADOR,
                Columns.ADE_PRAZO,
                Columns.ADE_DATA,
                Columns.ADE_PRD_PAGAS,
                Columns.CNV_COD_VERBA,
                Columns.SVC_DESCRICAO,
                Columns.USU_LOGIN,
                Columns.USU_CODIGO,
                Columns.USU_TIPO_BLOQ,
                Columns.SAD_DESCRICAO
        };
    }
}




