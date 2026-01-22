package com.zetra.econsig.persistence.query.extrato;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaExtratoMargemVlrFolhaPercentualQuery</p>
 * <p>Description: Extrato de Margem: </p>
 * PASSO 4) ADICIONA A MARGEM USADA A DIFERENCA ENTRE O VALOR PAGO PELA FOLHA E O VALOR
 * DOS CONTRATOS DO TIPO PERCENTUAL, SOMENTE OS QUE FORAM PAGOS NO ULTIMO RETORNO
 * OBS: SÓ PARA SISTEMAS QUE CONTROLAM MARGEM (TPC 23 != 'S')
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaExtratoMargemVlrFolhaPercentualQuery extends HQuery {

    private final String rseCodigo;
    private final Date ultPeriodo;

    public ListaExtratoMargemVlrFolhaPercentualQuery(String rseCodigo, Date ultPeriodo) {
        this.rseCodigo = rseCodigo;
        this.ultPeriodo = ultPeriodo;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final boolean subtrairValorPagoQQPeriodo = ParamSist.paramEquals(CodedValues.TPC_SUBTRAIR_VALOR_PAGO_ADE_PERCENTUAL_INDEPENDENTE_PERIODO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

        final StringBuilder sql = new StringBuilder();

        sql.append("SELECT ");
        sql.append("'4' as TIPO, ");
        sql.append("prd.prdVlrRealizado - ade.adeVlr, ");
        sql.append("ade.adeCodigo, ");
        sql.append("ade.adeData, ");
        sql.append("ade.adeNumero, ");
        sql.append("ade.adeVlr, ");
        sql.append("ade.adeVlrFolha, ");
        sql.append("ade.adeTipoVlr, ");
        sql.append("coalesce(ade.adeIncMargem, 1), ");
        sql.append("sad.sadDescricao, ");
        sql.append("csa.csaIdentificador, ");
        sql.append("csa.csaNome, ");
        sql.append("csa.csaNomeAbrev ");

        if(ParamSist.getBoolParamSist(CodedValues.TPC_EXIBE_CONTRATOS_NAO_INCIDEM_MARGEM_EXTRATO_MARGEM, AcessoSistema.getAcessoUsuarioSistema())) {
            sql.append(", svc.svcDescricao, ");
            sql.append("cnv.cnvCodVerba ");
        }

        sql.append(" FROM AutDesconto ade ");
        sql.append(" INNER JOIN ade.verbaConvenio vco ");
        sql.append(" INNER JOIN vco.convenio cnv ");
        sql.append(" INNER JOIN cnv.servico svc ");
        sql.append(" INNER JOIN cnv.consignataria csa ");
        sql.append(" INNER JOIN ade.statusAutorizacaoDesconto sad ");

        sql.append(" INNER JOIN ade.parcelaDescontoSet prd ");
        sql.append("   WITH prd.statusParcelaDesconto.spdCodigo = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("'");

        if (!subtrairValorPagoQQPeriodo) {
            sql.append(" AND prd.prdDataDesconto = :periodo");
        }

        sql.append(" WHERE ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        sql.append(" AND (coalesce(ade.adeIncMargem, 1) <> 0)");
        sql.append(" AND (ade.statusAutorizacaoDesconto.sadCodigo NOT IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_INATIVOS, "','")).append("'))");
        sql.append(" AND (ade.adeTipoVlr = '").append(CodedValues.TIPO_VLR_PERCENTUAL).append("')");

        if (subtrairValorPagoQQPeriodo) {
            sql.append(" AND NOT EXISTS (");
            sql.append(" SELECT 1 FROM ParcelaDesconto prd2");
            sql.append(" WHERE prd.autDesconto.adeCodigo = prd2.autDesconto.adeCodigo");
            sql.append(" AND prd2.statusParcelaDesconto.spdCodigo = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("'");
            sql.append(" AND prd2.prdDataDesconto > prd.prdDataDesconto");
            sql.append(") ");
        }

        // Se zera margem usada (default NAO) adiciona na query cláusula para não retornar nada.
        if (ParamSist.paramEquals(CodedValues.TPC_ZERA_MARGEM_USADA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            sql.append(" AND (1 = 2)");
        }

        // Ordena pela data de inclusão dos contratos que serão listados
        sql.append(" ORDER BY ade.adeData");

        final Query<Object[]> query = instanciarQuery(session, sql.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        if (!subtrairValorPagoQQPeriodo) {
            defineValorClausulaNomeada("periodo", ultPeriodo, query);
        }

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
