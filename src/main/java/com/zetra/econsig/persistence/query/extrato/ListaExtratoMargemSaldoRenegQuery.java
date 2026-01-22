package com.zetra.econsig.persistence.query.extrato;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaExtratoMargemSaldoRenegQuery</p>
 * <p>Description: Extrato de Margem: </p>
 * PASSO 2) VE A DIFERENCA ENTRE OS CONTRATOS AGUARD. LIQUIDACAO E OS AGUARD. CONFIRMACAO
 * DOS PROCESSOS DE RENEGOCIACAO E COMPRA. NAO INCLUI OS LIQUIDADOS E CONCLUIDOS, POIS
 * JA ESTARAO LIBERANDO O VALOR DA MARGEM DO SERVIDOR.
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaExtratoMargemSaldoRenegQuery extends HQuery {

    private final String rseCodigo;

    public ListaExtratoMargemSaldoRenegQuery(String rseCodigo) {
        this.rseCodigo = rseCodigo;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final StringBuilder sql = new StringBuilder();

        sql.append("SELECT ");
        sql.append("'2' as TIPO, ");
        sql.append("max_value(adeDestino.adeVlr - sum(adeOrigem.adeVlr), 0.00), ");
        sql.append("adeDestino.adeCodigo, ");
        sql.append("adeDestino.adeData, ");
        sql.append("adeDestino.adeNumero, ");
        sql.append("adeDestino.adeVlr, ");
        sql.append("adeDestino.adeVlrFolha, ");
        sql.append("adeDestino.adeTipoVlr, ");
        sql.append("to_short(coalesce(pse3.pseVlr, '1')), ");
        sql.append("sad.sadDescricao, ");
        sql.append("csaDestino.csaIdentificador, ");
        sql.append("csaDestino.csaNome, ");
        sql.append("csaDestino.csaNomeAbrev ");

        if(ParamSist.getBoolParamSist(CodedValues.TPC_EXIBE_CONTRATOS_NAO_INCIDEM_MARGEM_EXTRATO_MARGEM, AcessoSistema.getAcessoUsuarioSistema())) {
            sql.append(", svcDestino.svcDescricao, ");
            sql.append("cnvDestino.cnvCodVerba ");
        }

        sql.append(" FROM RelacionamentoAutorizacao rad ");
        sql.append(" INNER JOIN rad.autDescontoByAdeCodigoOrigem adeOrigem ");
        sql.append(" INNER JOIN rad.autDescontoByAdeCodigoDestino adeDestino ");
        sql.append(" INNER JOIN adeDestino.statusAutorizacaoDesconto sad ");
        sql.append(" INNER JOIN adeDestino.verbaConvenio vcoDestino ");
        sql.append(" INNER JOIN vcoDestino.convenio cnvDestino ");
        sql.append(" INNER JOIN cnvDestino.servico svcDestino ");
        sql.append(" INNER JOIN cnvDestino.consignataria csaDestino ");
        sql.append(" LEFT OUTER JOIN svcDestino.paramSvcConsignanteSet pse3 ");
        sql.append(" WITH pse3.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_INCIDE_MARGEM).append("' ");

        sql.append(" WHERE adeDestino.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        sql.append(" AND (adeOrigem.statusAutorizacaoDesconto.sadCodigo IN ('").append(CodedValues.SAD_AGUARD_LIQUIDACAO);
        sql.append("','").append(CodedValues.SAD_AGUARD_LIQUI_COMPRA).append("')) ");

        sql.append(" AND (adeDestino.statusAutorizacaoDesconto.sadCodigo IN ('").append(CodedValues.SAD_AGUARD_CONF);
        sql.append("','").append(CodedValues.SAD_AGUARD_DEFER).append("')) ");

        sql.append(" AND (rad.tipoNatureza.tntCodigo IN ('").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO);
        sql.append("','").append(CodedValues.TNT_CONTROLE_COMPRA).append("')) ");

        sql.append(" AND (coalesce(adeOrigem.adeIncMargem, 1) <> 0)");
        sql.append(" AND (adeDestino.adeIncMargem = 0)");

        sql.append(" GROUP BY ");
        sql.append("adeDestino.adeCodigo, ");
        sql.append("adeDestino.adeData, ");
        sql.append("adeDestino.adeNumero, ");
        sql.append("adeDestino.adeVlr, ");
        sql.append("adeDestino.adeVlrFolha, ");
        sql.append("adeDestino.adeTipoVlr, ");
        sql.append("pse3.pseVlr, ");
        sql.append("sad.sadDescricao, ");
        sql.append("csaDestino.csaIdentificador, ");
        sql.append("csaDestino.csaNome, ");
        sql.append("csaDestino.csaNomeAbrev ");

        sql.append(" HAVING max_value(adeDestino.adeVlr - sum(adeOrigem.adeVlr), 0.00) > 0 ");

        // Ordena pela data de inclusão dos contratos que serão listados
        sql.append(" ORDER BY adeDestino.adeData");

        final Query<Object[]> query = instanciarQuery(session, sql.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        if(!ParamSist.getBoolParamSist(CodedValues.TPC_EXIBE_CONTRATOS_NAO_INCIDEM_MARGEM_EXTRATO_MARGEM, AcessoSistema.getAcessoUsuarioSistema())) {
            return new String[] {
                                 "TIPO",
                                 "MARGEM_USADA",
                                 Columns.ADE_CODIGO,
                                 Columns.ADE_DATA,
                                 Columns.ADE_NUMERO,
                                 Columns.ADE_VLR,
                                 Columns.ADE_VLR_FOLHA,
                                 Columns.ADE_TIPO_VLR,
                                 Columns.ADE_INC_MARGEM,
                                 Columns.SAD_DESCRICAO,
                                 Columns.CSA_IDENTIFICADOR,
                                 Columns.CSA_NOME,
                                 Columns.CSA_NOME_ABREV
                         };
        } else {
            return new String[] {
                                 "TIPO",
                                 "MARGEM_USADA",
                                 Columns.ADE_CODIGO,
                                 Columns.ADE_DATA,
                                 Columns.ADE_NUMERO,
                                 Columns.ADE_VLR,
                                 Columns.ADE_VLR_FOLHA,
                                 Columns.ADE_TIPO_VLR,
                                 Columns.ADE_INC_MARGEM,
                                 Columns.SAD_DESCRICAO,
                                 Columns.CSA_IDENTIFICADOR,
                                 Columns.CSA_NOME,
                                 Columns.CSA_NOME_ABREV,
                                 Columns.SVC_DESCRICAO,
                                 Columns.CNV_COD_VERBA
                         };
        }
    }
}
