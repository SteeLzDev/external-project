package com.zetra.econsig.persistence.query.extrato;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.servico.NaturezaRelSvc;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaExtratoMargemContratosEncerradosQuery</p>
 * <p>Description: Extrato de Margem: </p>
 * PASSO 6) SE NAO CONTROLA MARGEM, SUBTRAI DA MARGEM USADA OS CONTRATOS LIQUIDADOS/CONCLUIDOS NO PERIODO
 * DE ACORDO COM OS PARAMETROS DE SISTEMA
 * OBS: SÓ PARA SISTEMAS QUE NÃO CONTROLAM MARGEM (TPC 23 = 'S')
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaExtratoMargemContratosEncerradosQuery extends HQuery {

    private final String rseCodigo;
    private final Date ultPeriodo;
    private final Date dataFimUltPeriodo;
    private Integer carenciaFolha;

    public ListaExtratoMargemContratosEncerradosQuery(String rseCodigo, Date ultPeriodo, Date dataFimUltPeriodo) {
        this.rseCodigo = rseCodigo;
        this.ultPeriodo = ultPeriodo;
        this.dataFimUltPeriodo = dataFimUltPeriodo;

        final Object paramCarenciaFolha = ParamSist.getInstance().getParam(CodedValues.TPC_CARENCIA_CONCLUSAO_FOLHA, AcessoSistema.getAcessoUsuarioSistema());
        if (!TextHelper.isNull(paramCarenciaFolha)) {
            carenciaFolha = Integer.valueOf(paramCarenciaFolha.toString());
        }
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        boolean usouPeriodo = false;
        final StringBuilder sql = new StringBuilder();

        sql.append("SELECT ");
        sql.append("'6' as TIPO, ");
        sql.append("(case when ade.adeTipoVlr = 'P' then coalesce(ade.adeVlrFolha, ade.adeVlr) else ade.adeVlr end) * -1, ");
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
        sql.append(" INNER JOIN ade.ocorrenciaAutorizacaoSet oca ");
        sql.append("   WITH oca.ocaData > :dataFimPeriodo");

        sql.append(" WHERE ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        sql.append(" AND (coalesce(ade.adeIncMargem, 1) <> 0)");
        sql.append(" AND (coalesce(ade.adePrdPagas, 0) > 0)");

        // Se não libera margem conclusão de contratos (default NAO), verifica se o contrato ainda teria prazo para desconto
        if (!ParamSist.paramEquals(CodedValues.TPC_LIB_MARGEM_CONCLUSAO_CONTRATO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            sql.append(" AND (coalesce(ade.adePrazo, 999999999) > coalesce(ade.adePrdPagas, 0)");
            sql.append("   OR add_month(ade.adeAnoMesFim, coalesce(ade.adeCarenciaFinal, 0) + " + carenciaFolha + ") > :periodo)");
            usouPeriodo = true;
        }

        // Se não libera margem contrato liquidado não pago (default NAO), verifica se valor folha maior que zero
        if (!ParamSist.paramEquals(CodedValues.TPC_LIB_MARGEM_LIQ_CONTRATO_NAO_PAGO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            sql.append(" AND (coalesce(ade.adeVlrFolha, 0.00) > 0)");
        }

        // Se não zera margem usada (default NAO) adiciona na query cláusula para não retornar nada.
        if (!ParamSist.paramEquals(CodedValues.TPC_ZERA_MARGEM_USADA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            sql.append(" AND (1 = 2)");
        }

        // Contrato deve estar liquidado ou ...
        sql.append(" AND (ade.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_LIQUIDADA).append("'");
        if (ParamSist.paramEquals(CodedValues.TPC_LIB_MARGEM_CONCLUSAO_CONTRATO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            // Se libera margem para contratos concluidos (default NAO), o contrato também pode estar concluído ...
            sql.append("  OR (ade.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_CONCLUIDO).append("'");

            // desde que, se não libera margem para contratos concluídos de férias,
            // o contrato não tenha parcela paga futura e ...
            if (ParamSist.paramEquals(CodedValues.TPC_LIB_MARGEM_CONCLUSAO_CONTRATO_FERIAS, CodedValues.TPC_NAO, AcessoSistema.getAcessoUsuarioSistema())) {
                sql.append(" AND NOT EXISTS (SELECT 1 FROM ade.parcelaDescontoSet prd ");
                sql.append(" where prd.statusParcelaDesconto.spdCodigo = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("' ");
                sql.append(" and prd.prdDataDesconto > :periodo)");
                usouPeriodo = true;
            }

            // se tem saldo de parcelas, não tenha um contrato de saldo
            // ainda aberto ligado ao contrato atual.
            if (NaturezaRelSvc.getInstance().exists(CodedValues.TNT_SALDO_PARCELAS)) {
                sql.append(" AND NOT EXISTS (SELECT 1 FROM ade.verbaConvenio vco ");
                sql.append("INNER JOIN vco.convenio cnv ");
                sql.append("INNER JOIN cnv.servico.relacionamentoServicoByOrigemSet rsv WITH rsv.tipoNatureza.tntCodigo = '" + CodedValues.TNT_SALDO_PARCELAS + "'");
                sql.append("INNER JOIN rsv.servicoBySvcCodigoDestino.convenioSet cnvSaldo ");
                sql.append("INNER JOIN cnvSaldo.verbaConvenioSet vcoSaldo ");
                sql.append("INNER JOIN vcoSaldo.autDescontoSet adeSaldo ");
                sql.append("WHERE cnv.consignataria.csaCodigo = cnvSaldo.consignataria.csa_codigo ");
                sql.append("AND cnv.orgao.orgCodigo = cnvSaldo.orgao.orgCodigo)");
                sql.append("AND adeSaldo.registroServidor.rseCodigo = ade.registroServidor.rseCodigo ");
                sql.append("AND adeSaldo.adeIndice = ade.adeIndice ");
                sql.append("AND adeSaldo.statusAutorizacaoDesconto.sadCodigo in ('").append(CodedValues.SAD_DEFERIDA);
                sql.append("', '").append(CodedValues.SAD_EMANDAMENTO).append("') ");
                sql.append(")");
            }

            sql.append(")");
        }
        sql.append(")");

        // Ocorrência de liquidação ou de conclusão, caso libere margem para contratos concluídos (default NAO)
        sql.append(" AND (oca.tipoOcorrencia.tocCodigo = '").append(CodedValues.TOC_TARIF_LIQUIDACAO).append("'");
        if (ParamSist.paramEquals(CodedValues.TPC_LIB_MARGEM_CONCLUSAO_CONTRATO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            sql.append("  OR oca.tipoOcorrencia.tocCodigo = '").append(CodedValues.TOC_CONCLUSAO_CONTRATO).append("'");
        }
        sql.append(")");

        // Agrupamos por código do contrato para que se houver mais de uma ocorrência de liquidação, o contrato não seja contabilizado
        // mais de uma vez em cenário por exemplo de suspensão e liquidação no mesmo período.
        sql.append(" GROUP BY ade.adeCodigo ");

        // Ordena pela data de inclusão dos contratos que serão listados
        sql.append(" ORDER BY ade.adeData");

        final Query<Object[]> query = instanciarQuery(session, sql.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("dataFimPeriodo", dataFimUltPeriodo, query);
        if (usouPeriodo) {
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
