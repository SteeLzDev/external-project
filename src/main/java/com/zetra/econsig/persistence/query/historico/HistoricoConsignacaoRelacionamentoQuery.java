package com.zetra.econsig.persistence.query.historico;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: HistoricoConsignacaoRelacionamentoQuery</p>
 * <p>Description: Listagem de relacionamentos de consignação para
 * exibição do histórico</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HistoricoConsignacaoRelacionamentoQuery extends HQuery {

    public AcessoSistema responsavel;

    public String adeCodigoOrigem;
    public String adeCodigoDestino;
    // Determina se está pesquisando histório de contrato arquivado
    public boolean arquivado = false;
    // Determina se deve pesquisar nas tabelas intermediárias, onde as pontas estão em tabelas distintas
    public boolean intermediario = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String dtlArquivado = ((!intermediario && arquivado) || (intermediario && !arquivado)) ? "S" : "N";

        String corpo = "select " +
                "rad.radData, " +
                "usu.usuLogin, " +
                "usuarioCsa.csaCodigo, " +
                "usuarioCse.cseCodigo, " +
                "usuarioCor.corCodigo, " +
                "usuarioOrg.orgCodigo, " +
                "usuarioSer.serCodigo, " +
                "usuarioSup.cseCodigo, " +
                "usu.usuTipoBloq, " +
                "usu.usuCodigo, " +
                "'" + CodedValues.TOC_RELACIONAMENTO_ADE + "' as TOC_CODIGO, " +
                "'" + ApplicationResourcesHelper.getMessage("rotulo.relacionamento.autorizacao", responsavel) + "' as TOC_DESCRICAO, " +
                "tnt.tntDescricao || ' - " + ApplicationResourcesHelper.getMessage("rotulo.consignacao.abreviado", responsavel) + " ' || ade.adeNumero as OCA_OBS, " +
                "str(nullif('', '')) as TMO_DESCRICAO, " +

                (!TextHelper.isNull(adeCodigoOrigem) ?
                        "'dtlTerceiro(''0'', ''' || rad.adeCodigoDestino || ''',''' || rad.adeCodigoOrigem || ''', ''" + dtlArquivado + "'')'" :
                        "'dtlTerceiro(''1'', ''' || rad.adeCodigoOrigem || ''',''' || rad.adeCodigoDestino || ''', ''" + dtlArquivado + "'')'" ) + " AS JAVASCRIPT"
                ;

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        if (!intermediario) {
            if (arquivado) {
                corpoBuilder.append(" FROM HtRelacionamentoAutorizacao rad ");
            } else {
                corpoBuilder.append(" FROM RelacionamentoAutorizacao rad ");
            }
        } else {
            if (arquivado) {
                if (!TextHelper.isNull(adeCodigoOrigem)) {
                    corpoBuilder.append(" FROM HtRelacionamentoAdeOrigem rad ");
                } else {
                    corpoBuilder.append(" FROM HtRelacionamentoAdeDestino rad ");
                }
            } else {
                if (!TextHelper.isNull(adeCodigoOrigem)) {
                    corpoBuilder.append(" FROM HtRelacionamentoAdeDestino rad ");
                } else {
                    corpoBuilder.append(" FROM HtRelacionamentoAdeOrigem rad ");
                }
            }
        }

        if (!TextHelper.isNull(adeCodigoOrigem)) {
            corpoBuilder.append(" INNER JOIN rad.autDescontoByAdeCodigoDestino ade ");
        } else {
            corpoBuilder.append(" INNER JOIN rad.autDescontoByAdeCodigoOrigem ade ");
        }

        corpoBuilder.append("INNER JOIN rad.tipoNatureza tnt ");
        corpoBuilder.append("LEFT OUTER JOIN rad.usuario usu ");
        corpoBuilder.append("LEFT JOIN usu.usuarioCsaSet usuarioCsa ");
        corpoBuilder.append("LEFT JOIN usu.usuarioCseSet usuarioCse ");
        corpoBuilder.append("LEFT JOIN usu.usuarioCorSet usuarioCor ");
        corpoBuilder.append("LEFT JOIN usu.usuarioOrgSet usuarioOrg ");
        corpoBuilder.append("LEFT JOIN usu.usuarioSerSet usuarioSer ");
        corpoBuilder.append("LEFT JOIN usu.usuarioSupSet usuarioSup ");

        if (!TextHelper.isNull(adeCodigoOrigem)) {
            corpoBuilder.append(" WHERE rad.adeCodigoOrigem ").append(criaClausulaNomeada("adeCodigoOrigem", adeCodigoOrigem));
        } else {
            corpoBuilder.append(" WHERE rad.adeCodigoDestino ").append(criaClausulaNomeada("adeCodigoDestino", adeCodigoDestino));
        }

        if (responsavel.isCsaCor()) {
            corpoBuilder.append(" AND rad.tntCodigo <> '").append(CodedValues.TNT_LEILAO_SOLICITACAO).append("'");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(adeCodigoOrigem)) {
            defineValorClausulaNomeada("adeCodigoOrigem", adeCodigoOrigem, query);
        } else {
            defineValorClausulaNomeada("adeCodigoDestino", adeCodigoDestino, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        // O nome dos campos deve ser o mesmo da query HistoricoConsignacaoOcorrenciaQuery
        // pois o resultado das duas serão agrupados para ordenação
        return new String[] {
                Columns.OCA_DATA,
                Columns.USU_LOGIN,
                Columns.UCA_CSA_CODIGO,
                Columns.UCE_CSE_CODIGO,
                Columns.UCO_COR_CODIGO,
                Columns.UOR_ORG_CODIGO,
                Columns.USE_SER_CODIGO,
                Columns.USP_CSE_CODIGO,
                Columns.USU_TIPO_BLOQ,
                Columns.USU_CODIGO,
                Columns.TOC_CODIGO,
                Columns.TOC_DESCRICAO,
                Columns.OCA_OBS,
                Columns.TMO_DESCRICAO,
                "JAVASCRIPT"
        };
    }
}
