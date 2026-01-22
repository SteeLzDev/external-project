package com.zetra.econsig.persistence.query.historico;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: HistoricoConsignacaoOcorrenciaQuery</p>
 * <p>Description: Listagem de ocorrências de consignação para
 * exibição do histórico</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HistoricoConsignacaoOcorrenciaQuery extends HQuery {

    public String adeCodigo;
    public List<String> tocCodigos;
    public boolean mostraTodoHistorico = false;
    public boolean arquivado = false;
    public AcessoSistema responsavel;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        List<String> tocCodigos = new ArrayList<>();

        if (!mostraTodoHistorico) {
            if (this.tocCodigos == null || this.tocCodigos.isEmpty()) {
                tocCodigos.add(CodedValues.TOC_AVISO);
                tocCodigos.add(CodedValues.TOC_ERRO);
                tocCodigos.add(CodedValues.TOC_INFORMACAO);
                tocCodigos.add(CodedValues.TOC_ALTERACAO_CONTRATO);
                tocCodigos.add(CodedValues.TOC_ALTERACAO_INCLUSAO_CONTRATO);
                tocCodigos.add(CodedValues.TOC_CONCLUSAO_CONTRATO);
                tocCodigos.add(CodedValues.TOC_CONCLUSAO_SEM_DESCONTO);
                tocCodigos.add(CodedValues.TOC_CORRECAO_SALDO_DEVEDOR);
                tocCodigos.add(CodedValues.TOC_SOLICITACAO_SALDO_DEVEDOR);
                tocCodigos.add(CodedValues.TOC_SOLICITACAO_SALDO_DEVEDOR_LIQUIDACAO);
                tocCodigos.add(CodedValues.TOC_AUTORIZACAO_VIA_SENHA_SERVIDOR);
                tocCodigos.add(CodedValues.TOC_PAGAMENTO_SALDO_DEVEDOR);
                tocCodigos.add(CodedValues.TOC_PAGAMENTO_REJEITADO_SALDO_DEVEDOR);
                tocCodigos.add(CodedValues.TOC_REJEICAO_PAGAMENTO_SALDO_DEVEDOR);
                tocCodigos.add(CodedValues.TOC_INFORMACAO_SALDO_DEVEDOR);
                tocCodigos.add(CodedValues.TOC_SALDO_DEVEDOR_RECALCULADO);
                tocCodigos.add(CodedValues.TOC_RECALCULO_SALDO_DEVEDOR);
                tocCodigos.add(CodedValues.TOC_SALDO_DEVEDOR_APROVADO_SERVIDOR);
                tocCodigos.add(CodedValues.TOC_SALDO_DEVEDOR_REJEITADO_SERVIDOR);
                tocCodigos.add(CodedValues.TOC_AUTORIZACAO_OP_SEGUNDO_USUARIO);
                tocCodigos.add(CodedValues.TOC_OPERACAO_HOST_A_HOST);
                tocCodigos.add(CodedValues.TOC_SOLICITACAO_SALDO_DEVEDOR_EXCLUSAO);
                tocCodigos.add(CodedValues.TOC_SUSPENSAO_DESCONTO_FOLHA);
                tocCodigos.add(CodedValues.TOC_MOTIVO_NAO_CONCRETIZACAO_LEILAO);
                tocCodigos.add(CodedValues.TOC_CONSIGNACAO_NOTIFICADA_CSE);
                tocCodigos.add(CodedValues.TOC_DATA_VALOR_CONSIGNACAO_LIBERADO_SER);
                tocCodigos.add(CodedValues.TOC_CONFIRMACAO_LIQUIDACAO_ADE);
                tocCodigos.add(CodedValues.TOC_REIMPLANTE_CONSIGNACAO_NOVA_ADE);
            } else {
                tocCodigos = this.tocCodigos;
            }
        }

        String corpo = "select " +
                       "oca.usuCodigo, " +
                       "oca.ocaCodigo, " +
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
                       "oca.ocaPeriodo, " +
                       "tmo.tmoDescricao, " +
                       "tju.tjuDescricao, " +
                       "cid.uf.ufCod, " +
                       "cid.cidNome, " +
                       "dju.djuNumProcesso, " +
                       "dju.djuData, " +
                       "dju.djuTexto, " +
                       "( CASE WHEN EXISTS ( SELECT 1 ";
			           corpo += arquivado ? " FROM HtHistoricoOcorrenciaAde hoa  " : " FROM HistoricoOcorrenciaAde hoa ";
			           corpo += "WHERE oca.ocaCodigo = hoa.ocaCodigo ) " +
			                    "THEN 'S' " +
			                    "ELSE 'N' END ) AS EXISTE_HISTORICO, " +
			                    "(CASE WHEN usu.usuCodigo != '1' THEN 'SIM' ELSE '' END) AS PODE_EDITAR ";

        final StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(arquivado ? "FROM HtOcorrenciaAutorizacao oca " : "FROM OcorrenciaAutorizacao oca ");
        corpoBuilder.append(" INNER JOIN oca.usuario usu ");
        corpoBuilder.append(" INNER JOIN oca.tipoOcorrencia toc ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioCsaSet usuarioCsa ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioCseSet usuarioCse ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioCorSet usuarioCor ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioOrgSet usuarioOrg ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioSerSet usuarioSer ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioSupSet usuarioSup ");
        corpoBuilder.append(" LEFT OUTER JOIN oca.tipoMotivoOperacao tmo ");

        corpoBuilder.append(" LEFT OUTER JOIN oca.decisaoJudicialSet dju ");
        corpoBuilder.append(" LEFT OUTER JOIN dju.tipoJustica tju ");
        corpoBuilder.append(" LEFT OUTER JOIN dju.cidade cid ");

        corpoBuilder.append(" WHERE oca.autDesconto.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));

        if (!mostraTodoHistorico) {
            corpoBuilder.append(" AND toc.tocCodigo ").append(criaClausulaNomeada("tocCodigo", tocCodigos));
        }

        if (responsavel != null && (responsavel.isCsa() || responsavel.isSer())) {
            corpoBuilder.append(" AND (toc.tocCodigo <> '").append(CodedValues.TOC_MOTIVO_NAO_CONCRETIZACAO_LEILAO)
                        .append("' OR (toc.tocCodigo = '").append(CodedValues.TOC_MOTIVO_NAO_CONCRETIZACAO_LEILAO).append("' AND ");
            if (responsavel.isSer()) {
                corpoBuilder.append(" oca.usuario.usuCodigo ").append(criaClausulaNomeada("usuCodigo", responsavel.getUsuCodigo())).append(")) ");
            } else {
                corpoBuilder.append(" usuarioCsa.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", responsavel.getCsaCodigo())).append(")) ");
            }
        }

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);
        if (!mostraTodoHistorico) {
            defineValorClausulaNomeada("tocCodigo", tocCodigos, query);
        }
        if (responsavel != null && (responsavel.isCsa() || responsavel.isSer())) {
            corpoBuilder.append(" AND (toc.tocCodigo <> '").append(CodedValues.TOC_MOTIVO_NAO_CONCRETIZACAO_LEILAO)
                        .append("' OR (toc.tocCodigo = '").append(CodedValues.TOC_MOTIVO_NAO_CONCRETIZACAO_LEILAO).append("' AND ");
            if (responsavel.isSer()) {
                defineValorClausulaNomeada("usuCodigo", responsavel.getUsuCodigo(), query);
            } else {
                defineValorClausulaNomeada("csaCodigo", responsavel.getCsaCodigo(), query);
            }
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.USU_CODIGO,
                Columns.OCA_CODIGO,
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
                Columns.OCA_PERIODO,
                Columns.TMO_DESCRICAO,
                Columns.TJU_DESCRICAO,
                Columns.CID_UF_CODIGO,
                Columns.CID_NOME,
                Columns.DJU_NUM_PROCESSO,
                Columns.DJU_DATA,
                Columns.DJU_TEXTO,
                "EXISTE_HISTORICO",
                "PODE_EDITAR"
        };
    }
}
