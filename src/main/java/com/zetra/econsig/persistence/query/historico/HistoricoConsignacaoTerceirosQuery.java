package com.zetra.econsig.persistence.query.historico;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: HistoricoConsignacaoTerceirosQuery</p>
 * <p>Description: Listagem de ocorrências de consignação fruto de uma compra de terceiros.
 * Exibe apenas as ocorrências feitas após a data da compra.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HistoricoConsignacaoTerceirosQuery extends HQuery {

    public String adeCodigoOrigem;
    public String adeCodigoDestino;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        List<String> tocCodigos = new ArrayList<String>();
        tocCodigos.add(CodedValues.TOC_AVISO);
        tocCodigos.add(CodedValues.TOC_ERRO);
        tocCodigos.add(CodedValues.TOC_INFORMACAO);
        tocCodigos.add(CodedValues.TOC_CONCLUSAO_CONTRATO);
        tocCodigos.add(CodedValues.TOC_PAGAMENTO_SALDO_DEVEDOR);
        tocCodigos.add(CodedValues.TOC_PAGAMENTO_REJEITADO_SALDO_DEVEDOR);
        tocCodigos.add(CodedValues.TOC_REJEICAO_PAGAMENTO_SALDO_DEVEDOR);
        tocCodigos.add(CodedValues.TOC_INFORMACAO_SALDO_DEVEDOR);
        tocCodigos.add(CodedValues.TOC_SALDO_DEVEDOR_RECALCULADO);
        tocCodigos.add(CodedValues.TOC_RECALCULO_SALDO_DEVEDOR);
        tocCodigos.add(CodedValues.TOC_SALDO_DEVEDOR_APROVADO_SERVIDOR);
        tocCodigos.add(CodedValues.TOC_SALDO_DEVEDOR_REJEITADO_SERVIDOR);


        String corpo = "select " +
                       "oca.ocaData," +
                       "oca.ocaIpAcesso," +
                       "usuarioCsa.csaCodigo, " +
                       "usuarioCse.cseCodigo, " +
                       "usuarioCor.corCodigo, " +
                       "usuarioOrg.orgCodigo, " +
                       "usuarioSer.serCodigo, " +
                       "usuarioSup.cseCodigo, " +
                       "usu.usuLogin, " +
                       "usu.usuTipoBloq, " +
                       "usu.usuCodigo, " +
                       "toc.tocCodigo, " +
                       "toc.tocDescricao, " +
                       "oca.ocaObs, " +
                       "tmo.tmoDescricao ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" FROM OcorrenciaAutorizacao oca ");
        corpoBuilder.append(" INNER JOIN oca.autDesconto ade ");
        corpoBuilder.append(" INNER JOIN ade.relacionamentoAutorizacaoByAdeCodigoOrigemSet rad ");
        corpoBuilder.append(" INNER JOIN oca.usuario usu ");
        corpoBuilder.append(" INNER JOIN oca.tipoOcorrencia toc ");
        corpoBuilder.append(" LEFT OUTER JOIN oca.tipoMotivoOperacao tmo ");
        corpoBuilder.append("LEFT JOIN usu.usuarioCsaSet usuarioCsa ");
        corpoBuilder.append("LEFT JOIN usu.usuarioCseSet usuarioCse ");
        corpoBuilder.append("LEFT JOIN usu.usuarioCorSet usuarioCor ");
        corpoBuilder.append("LEFT JOIN usu.usuarioOrgSet usuarioOrg ");
        corpoBuilder.append("LEFT JOIN usu.usuarioSerSet usuarioSer ");
        corpoBuilder.append("LEFT JOIN usu.usuarioSupSet usuarioSup ");
        corpoBuilder.append(" WHERE rad.adeCodigoOrigem ").append(criaClausulaNomeada("adeCodigoOrigem", adeCodigoOrigem));
        corpoBuilder.append(" AND rad.adeCodigoDestino ").append(criaClausulaNomeada("adeCodigoDestino", adeCodigoDestino));
        corpoBuilder.append(" AND toc.tocCodigo ").append(criaClausulaNomeada("tocCodigo", tocCodigos));
        corpoBuilder.append(" AND oca.ocaData >= add_second(rad.radData, -5)");
        corpoBuilder.append(" ORDER BY oca.ocaData DESC");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("adeCodigoOrigem", adeCodigoOrigem, query);
        defineValorClausulaNomeada("adeCodigoDestino", adeCodigoDestino, query);
        defineValorClausulaNomeada("tocCodigo", tocCodigos, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.OCA_DATA,
                Columns.OCA_IP_ACESSO,
                Columns.UCA_CSA_CODIGO,
                Columns.UCE_CSE_CODIGO,
                Columns.UCO_COR_CODIGO,
                Columns.UOR_ORG_CODIGO,
                Columns.USE_SER_CODIGO,
                Columns.USP_CSE_CODIGO,
                Columns.USU_LOGIN,
                Columns.USU_TIPO_BLOQ,
                Columns.USU_CODIGO,
                Columns.TOC_CODIGO,
                Columns.TOC_DESCRICAO,
                Columns.OCA_OBS,
                Columns.TMO_DESCRICAO
        };
    }
}
