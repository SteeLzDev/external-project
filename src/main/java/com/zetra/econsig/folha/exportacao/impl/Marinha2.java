package com.zetra.econsig.folha.exportacao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.delegate.PeriodoDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.exception.MargemControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: Marinha</p>
 * <p>Description: Implementações específicas para a Marinha.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class Marinha2 extends ExportaMovimentoBase {
    private static final long serialVersionUID = -4659425105740912412L;

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Marinha2.class);

    // Quantidade máxima de tentativas de preservação de parcela
    private final int LIMITE_PRESERVACAO = 6;

    // Determina se os contratos da natureza especial devem ser enviados mesmo que o servidor não tenha margem.
    protected boolean enviaContratosCompulsorios;

    // Determina se envia os contratos caso o servidor esteja bloqueado (status ou serviço/convênio).
    protected boolean enviaContratosSerBloqueados;

    // Determina se envia os contratos de pensionistas independente da margem
    protected boolean enviaContratosPensionistasSemMargem;

    // Determina retira do estoque as consignações após a exportação (muda para deferido) ou se deixa na situação 16 (Estoque Mensal)
    protected boolean retiraDoEstoquePosExportacao;

    // Ativa Thread.sleep entre Querys pesadas (Bug que ocorreu a bastante tempo no MySQL que não "via" as tabelas criadas pela classe)
    protected boolean sleepBetweenQuerys;

    public Marinha2() {
        sleepBetweenQuerys = true;
        enviaContratosCompulsorios = true;
        enviaContratosSerBloqueados = true;
        enviaContratosPensionistasSemMargem = false;
        retiraDoEstoquePosExportacao = true;
    }

    @Override
    public void posCriacaoTabelas(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        super.posCriacaoTabelas(parametrosExportacao, responsavel);

        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            final StringBuilder query = new StringBuilder();
            query.append("drop table if exists tb_tmp_exp_saldo_periodo");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create table tb_tmp_exp_saldo_periodo (");
            query.append("rse_codigo varchar(32) NOT NULL, ");
            query.append("rse_matricula varchar(20) NOT NULL, ");
            query.append("rse_margem decimal(13,2), ");
            query.append("rse_margem_rest decimal(13,2), ");
            query.append("saldo decimal(13,2), ");
            query.append("rse_margem_2 decimal(13,2), ");
            query.append("rse_margem_rest_2 decimal(13,2), ");
            query.append("saldo_2 decimal(13,2), ");
            query.append("rse_margem_3 decimal(13,2), ");
            query.append("rse_margem_rest_3 decimal(13,2), ");
            query.append("saldo_3 decimal(13,2), ");
            query.append("key ix_rse (rse_codigo)");
            query.append(")");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("drop table if exists tb_tmp_exp_estoque");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create table tb_tmp_exp_estoque (");
            query.append("rse_codigo varchar(32) NOT NULL, ");
            query.append("rse_matricula varchar(20) NOT NULL, ");
            query.append("rse_margem decimal(13,2), ");
            query.append("rse_margem_rest decimal(13,2), ");
            query.append("estoque decimal(13,2), ");
            query.append("qtde int(11), ");
            query.append("rse_margem_2 decimal(13,2), ");
            query.append("rse_margem_rest_2 decimal(13,2), ");
            query.append("estoque_2 decimal(13,2), ");
            query.append("qtde_2 int(11), ");
            query.append("rse_margem_3 decimal(13,2), ");
            query.append("rse_margem_rest_3 decimal(13,2), ");
            query.append("estoque_3 decimal(13,2), ");
            query.append("qtde_3 int(11), ");
            query.append("usado char(1), ");
            query.append("key ix_rse (rse_codigo)");
            query.append(")");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("drop table if exists tb_tmp_exp_saldo_alteracao_");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create table tb_tmp_exp_saldo_alteracao_ (");
            query.append("rse_codigo varchar(32) NOT NULL, ");
            query.append("rse_matricula varchar(20) NOT NULL, ");
            query.append("rse_margem decimal(13,2), ");
            query.append("rse_margem_rest decimal(13,2), ");
            query.append("saldo_alteracao decimal(13,2), ");
            query.append("rse_margem_2 decimal(13,2), ");
            query.append("rse_margem_rest_2 decimal(13,2), ");
            query.append("saldo_alteracao_2 decimal(13,2), ");
            query.append("rse_margem_3 decimal(13,2), ");
            query.append("rse_margem_rest_3 decimal(13,2), ");
            query.append("saldo_alteracao_3 decimal(13,2), ");
            query.append("usado char(1), ");
            query.append("key ix_rse (rse_codigo)");
            query.append(")");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("drop table if exists tb_tmp_exp_saldo_alteracao");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create table tb_tmp_exp_saldo_alteracao (");
            query.append("rse_codigo varchar(32) NOT NULL, ");
            query.append("rse_matricula varchar(20) NOT NULL, ");
            query.append("rse_margem decimal(13,2), ");
            query.append("rse_margem_rest decimal(13,2), ");
            query.append("saldo_alteracao decimal(13,2), ");
            query.append("rse_margem_2 decimal(13,2), ");
            query.append("rse_margem_rest_2 decimal(13,2), ");
            query.append("saldo_alteracao_2 decimal(13,2), ");
            query.append("rse_margem_3 decimal(13,2), ");
            query.append("rse_margem_rest_3 decimal(13,2), ");
            query.append("saldo_alteracao_3 decimal(13,2), ");
            query.append("usado char(1), ");
            query.append("key ix_rse (rse_codigo)");
            query.append(")");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("drop table if exists tb_tmp_exp_saldo_alteracao_rejeitada_");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create table tb_tmp_exp_saldo_alteracao_rejeitada_ ( ");
            query.append("rse_codigo varchar(32) NOT NULL, ");
            query.append("rse_matricula varchar(20) NOT NULL, ");
            query.append("rse_margem decimal(13,2), ");
            query.append("rse_margem_rest decimal(13,2), ");
            query.append("saldo_alteracao decimal(13,2), ");
            query.append("rse_margem_2 decimal(13,2), ");
            query.append("rse_margem_rest_2 decimal(13,2), ");
            query.append("saldo_alteracao_2 decimal(13,2), ");
            query.append("rse_margem_3 decimal(13,2), ");
            query.append("rse_margem_rest_3 decimal(13,2), ");
            query.append("saldo_alteracao_3 decimal(13,2), ");
            query.append("usado char(1), ");
            query.append("key ix_rse (rse_codigo)");
            query.append(")");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("drop table if exists tb_tmp_exp_saldo_alteracao_rejeitada");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create table tb_tmp_exp_saldo_alteracao_rejeitada ( ");
            query.append("rse_codigo varchar(32) NOT NULL, ");
            query.append("rse_matricula varchar(20) NOT NULL, ");
            query.append("rse_margem decimal(13,2), ");
            query.append("rse_margem_rest decimal(13,2), ");
            query.append("saldo_alteracao decimal(13,2), ");
            query.append("rse_margem_2 decimal(13,2), ");
            query.append("rse_margem_rest_2 decimal(13,2), ");
            query.append("saldo_alteracao_2 decimal(13,2), ");
            query.append("rse_margem_3 decimal(13,2), ");
            query.append("rse_margem_rest_3 decimal(13,2), ");
            query.append("saldo_alteracao_3 decimal(13,2), ");
            query.append("usado char(1), ");
            query.append("key ix_rse (rse_codigo)");
            query.append(")");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("drop table if exists tb_tmp_saldo_suspensao_judicial");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create table tb_tmp_saldo_suspensao_judicial (");
            query.append("rse_codigo varchar(32) NOT NULL, ");
            query.append("rse_matricula varchar(20) NOT NULL, ");
            query.append("rse_margem decimal(13,2), ");
            query.append("rse_margem_rest decimal(13,2), ");
            query.append("saldo_suspensao decimal(13,2), ");
            query.append("qtde int(11), ");
            query.append("rse_margem_2 decimal(13,2), ");
            query.append("rse_margem_rest_2 decimal(13,2), ");
            query.append("saldo_suspensao_2 decimal(13,2), ");
            query.append("qtde_2 int(11), ");
            query.append("rse_margem_3 decimal(13,2), ");
            query.append("rse_margem_rest_3 decimal(13,2), ");
            query.append("saldo_suspensao_3 decimal(13,2), ");
            query.append("qtde_3 int(11), ");
            query.append("usado char(1), ");
            query.append("key ix_rse (rse_codigo)");
            query.append(")");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("drop table if exists tb_tmp_exp_relancamentos");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create table tb_tmp_exp_relancamentos (");
            query.append("ade_codigo varchar(32), ");
            query.append("ade_numero bigint(20), ");
            query.append("ade_vlr decimal(13,2), ");
            query.append("ade_inc_margem smallint(6), ");
            query.append("ade_data datetime, ");
            query.append("ade_ano_mes_ini date, ");
            query.append("ade_ano_mes_fim date, ");
            query.append("ade_ano_mes_ini_ref date, ");
            query.append("ade_ano_mes_fim_ref date, ");
            query.append("sad_codigo varchar(32), ");
            query.append("rse_matricula varchar(20), ");
            query.append("rse_margem decimal(13,2), ");
            query.append("margem_rest decimal(13,2), ");
            query.append("svc_prioridade int(11), ");
            query.append("cnv_prioridade int(11), ");
            query.append("nse_codigo varchar(32), ");
            query.append("enviar char(1), ");
            query.append("tem_oca char(1), ");
            query.append("pode_reimplantar char(1), ");
            query.append("reimplante_alteracao char(1), ");
            query.append("key ix_ade (ade_codigo)");
            query.append(")");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            // Tabelas para controle da regra de 6 tentativas para desconto
            // Precisamos de uma tabela de controle de tentativas, então só criamos se não existir.
            query.setLength(0);
            query.append("SELECT count(*) FROM tb_tmp_contr_regra_tentativa");
            LOG.debug(query.toString());
            int total = 0;
            try {
                total = Optional.ofNullable(jdbc.queryForObject(query.toString(), queryParams, Integer.class)).orElse(0);
            } catch (final DataAccessException ex) {
                total = 0;
            }

            if (total == 0) {
                query.setLength(0);
                query.append("drop table if exists tb_tmp_contr_regra_tentativa");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                query.setLength(0);
                query.append("CREATE TABLE tb_tmp_contr_regra_tentativa ( ");
                query.append("ade_codigo varchar(32) NOT NULL, ");
                query.append("num_tentativa int DEFAULT 0, ");
                query.append("PRIMARY KEY (ade_codigo) ");
                query.append(")");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);
            }

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public void preProcessaAutorizacoes(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        final List<String> orgCodigos = parametrosExportacao.getOrgCodigos();
        final List<String> estCodigos = parametrosExportacao.getEstCodigos();
        final List<String> verbas = parametrosExportacao.getVerbas();
        final String acao = parametrosExportacao.getAcao();

        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        final String defaultPreservacao = (String) ParamSist.getInstance().getParamOrDefault(CodedValues.TPC_DEFAULT_PARAM_SVC_PRESERVA_PRD, CodedValues.TPC_NAO, responsavel);
        queryParams.addValue("defaultPreservacao", defaultPreservacao);

        try {
            // Obtém o período atual
            final PeriodoDelegate perDelegate = new PeriodoDelegate();
            final TransferObject periodoExportacao = perDelegate.obtemPeriodoExportacaoDistinto(orgCodigos, estCodigos, responsavel);
            final String periodo = DateHelper.format((java.util.Date) periodoExportacao.getAttribute(Columns.PEX_PERIODO), "yyyy-MM-dd");
            LOG.debug("periodoAtual=" + periodo);
            // Período anterior será sempre = periodoAtual - 1
            final Calendar calPeriodoAnterior = Calendar.getInstance();
            calPeriodoAnterior.setTime((java.util.Date) periodoExportacao.getAttribute(Columns.PEX_PERIODO));
            calPeriodoAnterior.add(Calendar.MONTH, -1);
            final String periodoAnterior = DateHelper.format(calPeriodoAnterior.getTime(), "yyyy-MM-dd");
            LOG.debug("periodoAnterior=" + periodoAnterior);

            // ALTERA OCORRÊNCIAS
            alteraOcorrencias(periodoAnterior);

            if ((acao == null) || acao.equals(ParametrosExportacao.AcaoEnum.EXPORTAR.getCodigo())) {
                // CÁLCULO DA MARGEM RESTANTE NO DIA DO CORTE
                calculaMargemRestExportacao();

                // COLOCA EM ESTOQUE CONTRATOS CUJO SERVIDOR ESTAVA COM MARGEM RESTANTE NEGATIVA NO DIA DO CORTE
                moveParaEstoqueMargemNegativa();

                // CANCELA ALTERAÇÕES DE CONTRATOS COM MARGEM RESTANTE NEGATIVA NO DIA DO CORTE
                cancelaAlteracaoMargemNegativa();

                // PREPARA LISTA DE ADES CANDIDATAS PARA RELANÇAMENTO
                selectCandidatasRelancamento(orgCodigos, estCodigos, verbas, periodo, responsavel);

                String query = "";

                if (enviaContratosPensionistasSemMargem) {
                    // 04/05/2010: CONTRATOS DE PENSIONISTAS DEVEM SER ENVIADOS DE QUALQUER FORMA, OU SEJA
                    // NÃO DEVEM PASSAR PELA ROTINA DE VALIDAÇÃO DE MARGEM NO MOVIMENTO
                    query = "update tb_tmp_exp_relancamentos tmp "
                            + "inner join tb_aut_desconto ade on (tmp.ade_codigo = ade.ade_codigo) "
                            + "inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) "
                            + "set tmp.enviar = 'S' "
                            + "where rse.rse_tipo like 'PENSIONISTA%' "
                            + "and pode_reimplantar = 'S'"
                            ;
                    LOG.debug(query);
                    jdbc.update(query, queryParams);
                }

                // DETERMINA QUAIS CANDIDATOS A RELANÇAMENTO CABEM NAS MARGENS E CONFIGURA O ENVIO = S
                final String[] adeIncMargens = {"1", "2", "3"};
                for (final String adeIncMargen : adeIncMargens) {
                    defineContratosRelancamentoPorMargem(adeIncMargen, responsavel);
                }

                // Marca contratos do período para envio, caso seja determinado.
                if (enviaContratosCompulsorios) {
                    query = "update tb_tmp_exp_relancamentos set enviar = 'S' where nse_codigo = '" + CodedValues.NSE_COMPULSORIO + "'";
                    jdbc.update(query, queryParams);
                }

                //DESENV-18960: Se por algum motivo o contrato não foi no movimento ou não pode ser enviado, porém no próximo movimento ele teve uma alteração
                // seu contrato ainda está como deferido e a parcela não foi paga e o contrato está marcado para não ser enviado no movimento, é preciso remover a linha de alteração, pois o prazo do
                // contrato estará errado e não recálculado.
                alteraOcorrenciasAlteracao(periodoAnterior);


                // TODO: alterações parciais o sad_codigo deve ser 5
                // 4. atualizar contratos enviar = 'S'
                // 4a. Preserva parcela
                query = "update tb_aut_desconto ade "
                        + "inner join tb_tmp_exp_relancamentos tmp on (tmp.ade_codigo = ade.ade_codigo) "
                        + "set ade.sad_codigo = '4', "
                        + "ade.ade_ano_mes_ini_ref = ifnull(ade.ade_ano_mes_ini_ref, ade.ade_ano_mes_ini), "
                        + "ade.ade_ano_mes_fim_ref = ifnull(ade.ade_ano_mes_fim_ref, ade.ade_ano_mes_fim), "
                        + "ade.ade_ano_mes_ini = '" + periodo + "', "
                        + "ade.ade_prazo = ade.ade_prazo - ifnull(ade.ade_prd_pagas, 0), "
                        + "ade.ade_prd_pagas = 0, "
                        + "ade.ade_ano_mes_fim = if(ade.ade_prazo is not null, date_add('" + periodo + "', interval ade.ade_prazo - ifnull(ade.ade_prd_pagas, 0) - 1 month), null) "
                        + "where tmp.sad_codigo <> '12' "
                        + "and tmp.enviar = 'S' "
                        + "and tmp.reimplante_alteracao = 'N' "
                        // SOMENTE CONTRATOS QUE AINDA NÃO TIVERAM TODOS AS PARCELAS PAGAS
                        + "and (ade.ade_prazo is null or ade.ade_prazo > coalesce(ade.ade_prd_pagas, 0)) "
                        // E A CONSIGNATÁRIA QUER PRESERVAR PARCELA
                        + "and 'S' = (select (ifnull(psc.psc_vlr, :defaultPreservacao)) from tb_verba_convenio vco "
                        + "           inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)"
                        + "           left outer join tb_param_svc_consignataria psc on (psc.csa_codigo = cnv.csa_codigo and psc.svc_codigo = cnv.svc_codigo and psc.tps_codigo = '" + CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL + "') "
                        + "           where vco.vco_codigo = ade.vco_codigo)";
                LOG.debug(query);
                jdbc.update(query, queryParams);

                // 4b. NÃO preserva parcela
                query = "update tb_aut_desconto ade "
                        + "inner join tb_tmp_exp_relancamentos tmp on (tmp.ade_codigo = ade.ade_codigo) "
                        + "set ade.sad_codigo = '4', "
                        + "ade.ade_ano_mes_ini_ref = ifnull(ade.ade_ano_mes_ini_ref, ade.ade_ano_mes_ini), "
                        + "ade.ade_ano_mes_fim_ref = ifnull(ade.ade_ano_mes_fim_ref, ade.ade_ano_mes_fim), "
                        + "ade.ade_ano_mes_ini = '" + periodo + "', "
                        + "ade.ade_prd_pagas = 0, "
                        + "ade.ade_prazo = if(ade.ade_prazo is not null, PERIOD_DIFF(concat(year(ade.ade_ano_mes_fim), right(concat('00', month(ade.ade_ano_mes_fim)), 2)), concat(year('" + periodo + "'),  right(concat('00', month('" + periodo + "')), 2))) + 1, null) "
                        + "where tmp.sad_codigo <> '12' "
                        + "and tmp.enviar = 'S' "
                        + "and tmp.reimplante_alteracao = 'N' "
                        // SOMENTE CONTRATOS COM DATA FINAL POR VIR
                        + "and (ade.ade_ano_mes_fim is null or ade.ade_ano_mes_fim >= '" + periodo + "') "
                        // E A CONSIGNATÁRIA NÃO QUER PRESERVAR PARCELA
                        + "and 'N' = (select (ifnull(psc.psc_vlr, :defaultPreservacao)) from tb_verba_convenio vco "
                        + "           inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)"
                        + "           left outer join tb_param_svc_consignataria psc on (psc.csa_codigo = cnv.csa_codigo and psc.svc_codigo = cnv.svc_codigo and psc.tps_codigo = '" + CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL + "') "
                        + "           where vco.vco_codigo = ade.vco_codigo)";
                LOG.debug(query);
                jdbc.update(query, queryParams);

                // Estoques
                query = "update tb_aut_desconto ade "
                        + "inner join tb_tmp_exp_relancamentos tmp on (tmp.ade_codigo = ade.ade_codigo) "
                        + "set ade.sad_codigo = '4', "
                        + "ade.ade_ano_mes_ini_ref = ifnull(ade.ade_ano_mes_ini_ref, ade.ade_ano_mes_ini), "
                        + "ade.ade_ano_mes_fim_ref = ifnull(ade.ade_ano_mes_fim_ref, ade.ade_ano_mes_fim), "
                        + "ade.ade_ano_mes_ini = '" + periodo + "', "
                        + "ade.ade_prd_pagas = 0, "
                        + "ade.ade_ano_mes_fim = if(ade.ade_prazo is not null, date_add('" + periodo + "', INTERVAL (ade.ade_prazo - 1)  MONTH), null) "
                        + "where tmp.sad_codigo = '12' and tmp.enviar = 'S' and tmp.reimplante_alteracao = 'N'";
                LOG.debug(query);
                jdbc.update(query, queryParams);

                // Insere a ocorrência de reimplantação automática para os contratos
                // que serão enviados. A ocorrência é inserida para todos os contratos.
                query = "INSERT INTO tb_ocorrencia_autorizacao (OCA_CODIGO, TOC_CODIGO, ADE_CODIGO, USU_CODIGO, OCA_DATA, OCA_PERIODO, OCA_OBS) "
                        + "SELECT DISTINCT concat(lpad(ade.ade_numero, 10, '0'), '-', date_format(now(), '%Y%m%d%H%i%S')), "
                        + "'10', ade.ade_codigo, '1', '" + periodo + "', '" + periodo + "', 'REIMPLANTAÇÃO AUTOMÁTICA' "
                        + "from tb_aut_desconto ade "
                        + "inner join tb_tmp_exp_relancamentos tmp on (tmp.ade_codigo = ade.ade_codigo) "
                        + "where tmp.enviar = 'S' and tem_oca = 'N'";
                LOG.debug(query);
                jdbc.update(query, queryParams);

                // Remove ocorrência do que tinha sido relançado mas não será mais relançado este mês.
                query = "DELETE FROM tb_ocorrencia_autorizacao "
                        + "USING tb_ocorrencia_autorizacao "
                        + "inner join tb_tmp_exp_relancamentos tmp on (tmp.ade_codigo = tb_ocorrencia_autorizacao.ade_codigo) "
                        + "inner join tb_aut_desconto ade on (ade.ade_codigo = tb_ocorrencia_autorizacao.ade_codigo) "
                        + "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
                        + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
                        + "inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) "
                        + "where tmp.enviar = 'N' and tem_oca = 'S' "
                        + "and tb_ocorrencia_autorizacao.toc_codigo = '10' and tb_ocorrencia_autorizacao.usu_codigo = '1' and oca_data > pex_data_ini ";
                LOG.debug(query);
                jdbc.update(query, queryParams);
            }
        } catch (final DataAccessException | PeriodoException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    protected void defineContratosRelancamentoPorMargem(String adeIncMargen, AcessoSistema responsavel) throws DataAccessException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            // Lista de ADEs candidatas a reimplantação
            String query = "select * from tb_tmp_exp_relancamentos "
                         + "where pode_reimplantar = 'S' "
                         + "and enviar = 'N' "
                         + "and ade_inc_margem = '" + adeIncMargen + "' "
                         + "order by rse_matricula, svc_prioridade, cnv_prioridade, ade_ano_mes_ini_ref, ade_numero";
            LOG.debug(query);
            final List<Map<String, Object>> resultSet = jdbc.queryForList(query, queryParams);

            String matriculaAtual;
            String matricula = "";
            String queryMargem;
            boolean continuarTestandoMatricula = true;
            BigDecimal adeVlr;
            String adeCodigo = null;
            BigDecimal margemRest = new BigDecimal("0");
            BigDecimal margemRestOrig = new BigDecimal("0");
            BigDecimal margem = new BigDecimal("0");
            final List<String> adeCodigos = new ArrayList<>();

            for (final Map<String, Object> row : resultSet) {
                matriculaAtual = row.get("RSE_MATRICULA").toString();
                margem = (BigDecimal) row.get("RSE_MARGEM");
                margemRestOrig = (BigDecimal) row.get("MARGEM_REST");
                adeVlr = (BigDecimal) row.get("ADE_VLR");
                adeCodigo = (String) row.get("ADE_CODIGO");

                if (!matriculaAtual.equals(matricula)) {
                    // Seleciona todos os candidatos a relancamento, pois todos eles
                    // estão comprometendo a margem, logo devem ser considerados
                    // no cálculo.
                    queryMargem = "select sum(ade_vlr) as total from tb_tmp_exp_relancamentos where rse_matricula = :matriculaAtual and ade_inc_margem = '" + adeIncMargen + "'";
                    queryParams.addValue("matriculaAtual", matriculaAtual);
                    final BigDecimal sumAdeVlr = jdbc.queryForObject(queryMargem, queryParams, BigDecimal.class);

                    matricula = matriculaAtual;
                    margemRest = (BigDecimal) row.get("MARGEM_REST");
                    continuarTestandoMatricula = true;
                    // margem restante mais o valor relativo a todos os candidatos a relançamento
                    if (sumAdeVlr != null) {
                        margemRest = margemRest.add(sumAdeVlr);
                    }
                }

                // Quando um contrato da lista de candidatos a reimplante não cabe na margem
                // o teste para a matrícula em questão deve parar para evitar que contratos
                // de menor prioridade, mas que caberiam na margem, sejam enviados.
                if (continuarTestandoMatricula) {
                    // Selecionar quais contratos podem ser enviados (margem_rest - ade_vlr >= 0) atualizando margem_rest local
                    if ((margemRest.subtract(adeVlr).doubleValue() >= 0) &&
                            ((margem.doubleValue() > 0) || (margemRestOrig.doubleValue() > 0))) {
                        adeCodigos.add(adeCodigo);
                        margemRest = margemRest.subtract(adeVlr);
                    } else {
                        continuarTestandoMatricula = false;
                    }
                }
            }

            LOG.debug("Quantidade de contratos reimplantados = " + adeCodigos.size());
            if (adeCodigos.size() > 0) {
                // 3. marcar estes contratos com enviar = 'S'
                query = "update tb_tmp_exp_relancamentos set enviar = 'S' where ade_codigo in (:adeCodigos)";
                queryParams.addValue("adeCodigos", adeCodigos);
                jdbc.update(query, queryParams);
            }
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    protected void alteraOcorrencias(String periodoAnterior) throws DataAccessException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("periodoAnterior", periodoAnterior);
        String query;

        final String param = (String) ParamSist.getInstance().getParam(CodedValues.TPC_EXPORTA_LIQCANC_NAO_PAGAS, null);
        final boolean exportaLiqCancNaoPagas = CodedValues.TPC_SIM.equals(param);

        if (!exportaLiqCancNaoPagas) {
            // ITEM 15 ALTERA AS OCORRENCIAS DE LIQUIDAÇÃO DE CONTRATO SEM PARCELAS PAGAS PARA OCORRENCIA DE CANCELAMENTO
            query = "update tb_aut_desconto ade "
                    + "inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade.ade_codigo) "
                    + "inner join tb_parcela_desconto prd USE INDEX (PRD_DATA_DESCONTO_IDX) on (prd.ade_codigo = ade.ade_codigo) "
                    + "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
                    + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
                    + "inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) "
                    + "set toc_codigo = '35', oca_obs = 'LIQUIDAÇÃO NÃO EXPORTADA POR SER SEM EFEITO' "
                    + "where spd_codigo = '5' "
                    + "and prd_data_desconto = :periodoAnterior " // -- último desconto
                    + "and oca_data between PEX_DATA_INI and PEX_DATA_FIM " // -- após o último corte e antes do corte atual
                    + "and toc_codigo = '6'  "
                    + "and ade_vlr_folha is null";
            LOG.debug("ITEM 15: " + query);
            jdbc.update(query, queryParams);

            // ITEM 16 ALTERA AS OCORRENCIAS DE LIQUIDAÇÃO DOS CONTRATOS SEM RETORNO ADE_VLR_FOLHA NULO
            query = "update tb_aut_desconto ade "
                    + "inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade.ade_codigo) "
                    + "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
                    + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
                    + "inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) "
                    + "set toc_codigo = '35', oca_obs = 'LIQUIDAÇÃO NÃO EXPORTADA POR SER SEM EFEITO' "
                    + "where oca_data between PEX_DATA_INI and PEX_DATA_FIM " // -- após o último corte e antes do corte atual
                    + "and sad_codigo = '8' "
                    + "and toc_codigo = '6' "
                    + "and ade_vlr_folha is null ";
            LOG.debug("ITEM 16: " + query);
            jdbc.update(query, queryParams);
        }

        // ITEM 17 ALTERA OCORRENCIA DE ESTOQUES LIQUIDADOS PARA OCORRENCIA DE CANCELAMENTO: resolvido pelo ITEM 16

        // ITEM 18 ALTERA AS OCORRENCIAS DE ALTERAÇÃO DOS CONTRATOS SEM RETORNO ADE_VLR_FOLHA NULO
        query = "update tb_aut_desconto ade "
                + "inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade.ade_codigo) "
                + "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
                + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
                + "inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) "
                + "left outer join tb_parcela_desconto prd on (prd.ade_codigo = ade.ade_codigo and PRD_DATA_DESCONTO = :periodoAnterior) "
                + "set toc_codigo = '3' " // -- Transforma em 3 (Informação) apenas pois o reimplante (se for o caso) irá tratar da reinclusão com o novo valor
                + "where "
                + "oca_data between PEX_DATA_INI and PEX_DATA_FIM " // -- após o último corte e antes do corte atual
                + "and sad_codigo = '5' "
                + "and toc_codigo = '14'  "
                + "and (prd.prd_numero is null or spd_codigo = '5') ";
        LOG.debug("ITEM 18: " + query);
        jdbc.update(query, queryParams);

        // ITEM 19 ALTERA OCORRENCIA DE LIQUIDAÇÃO  PARA OCORRENCIA DE CANCELAMENTO DE CONTRATOS LIQUIDADOS NO MÊS EM QUE SÃO CONCLUÍDOS
        query = "update tb_aut_desconto ade "
                + "inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade.ade_codigo) "
                + "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
                + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
                + "inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) "
                + "set toc_codigo = '35', oca_obs = 'LIQUIDAÇÃO NÃO EXPORTADA POR SER SEM EFEITO' "
                + "where ade_inc_margem in ('1','2','3') "
                + "and toc_codigo = '6' "
                + "and ade.ade_cod_reg = '5' "
                + "and oca_data between PEX_DATA_INI and PEX_DATA_FIM " // -- após o último corte e antes do corte atual
                + "and ifnull(nullif(ade_ano_mes_fim_folha, '0000-00-00'), ade_ano_mes_fim) = :periodoAnterior " // -- com final no mês anterior
                + "and sad_codigo = '8'";
        LOG.debug("ITEM 19: " + query);
        jdbc.update(query, queryParams);

        // ITEM 34 ALTERA AS OCORRENCIAS DE CANCELAMENTO DE CONTRATO COM ÚLTIMA PARCELA PAGA PARA OCORRENCIA DE LIQUIDAÇÃO
        query = "update tb_aut_desconto ade "
                + "inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade.ade_codigo) "
                + "inner join tb_parcela_desconto prd USE INDEX (PRD_DATA_DESCONTO_IDX) on (prd.ade_codigo = ade.ade_codigo) "
                + "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
                + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
                + "inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) "
                + "set toc_codigo = '6', oca_obs = 'PTF: LIQUIDAÇÃO DE CONSIGNAÇÃO.', sad_codigo = '8' "
                + "where spd_codigo = '6' "
                + "and ade_inc_margem in ('1','2','3') "
                + "and prd_data_desconto = :periodoAnterior " // -- último desconto
                + "and oca_data between PEX_DATA_INI and PEX_DATA_FIM " // -- após o último corte e antes do corte atual
                + "and ifnull(nullif(ade_ano_mes_fim_folha, '0000-00-00'), ade_ano_mes_fim) > :periodoAnterior " // -- com final após o mês anterior
                + "and toc_codigo = '7' and  ade_vlr_folha is not null ";
        LOG.debug("ITEM 34: " + query);
        jdbc.update(query, queryParams);
    }

    // CÁLCULO DA MARGEM RESTANTE NO DIA DO CORTE
    protected void calculaMargemRestExportacao() throws DataAccessException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        // Recupera o parametro de sistema que informa se a folha possui carencia para a conclusão dos contratos.
        final Object obj = ParamSist.getInstance().getParam(CodedValues.TPC_CARENCIA_CONCLUSAO_FOLHA, null);
        int carenciaFolha = 0;
        if ((obj != null) && !"".equals(obj.toString())) {
            carenciaFolha = Integer.parseInt(obj.toString());
        }

        String query = "delete from tb_tmp_exp_saldo_periodo";
        LOG.debug(query);
        jdbc.update(query, queryParams);
        // calcula a movimentacao no periodo
        query = "insert into tb_tmp_exp_saldo_periodo (rse_codigo, rse_matricula, rse_margem, rse_margem_rest, saldo, rse_margem_2, rse_margem_rest_2, saldo_2, rse_margem_3, rse_margem_rest_3, saldo_3) "
                + "select rse.rse_codigo, rse_matricula, rse_margem, rse_margem_rest,  "
                + "  sum(if(ade_inc_margem = 1,  "
                + "         ifnull(ade_vlr_folha, ade_vlr) * case "
                + "           when toc_codigo = '6' then if((ade_vlr_folha is null and ade_data < pex_data_ini) or (ade_prd_pagas >= ade_prazo and date_add(ade_ano_mes_fim, interval " + carenciaFolha + " month) < pex_data_ini), 0, 1) "
                + "           when toc_codigo = '7' then if((ade_vlr_folha is null and ade_data < pex_data_ini) or (ade_prd_pagas >= ade_prazo and date_add(ade_ano_mes_fim, interval " + carenciaFolha + " month) < pex_data_ini), 0, 1) "
                + "           when toc_codigo = '35' then if((ade_vlr_folha is null and ade_data < pex_data_ini) or (ade_prd_pagas >= ade_prazo and date_add(ade_ano_mes_fim, interval " + carenciaFolha + " month) < pex_data_ini), 0, 1) "
                + "           when toc_codigo = '4' and sad_codigo <> '16' and ade.usu_codigo <> '1' then -1 "
                + "           else 0 end, 0)) as saldo, "
                + "  rse_margem_2, rse_margem_rest_2, "
                + "  sum(if(ade_inc_margem = 2, "
                + "         ifnull(ade_vlr_folha, ade_vlr) * case "
                + "           when toc_codigo = '6' then if((ade_vlr_folha is null and ade_data < pex_data_ini) or (ade_prd_pagas >= ade_prazo and date_add(ade_ano_mes_fim, interval " + carenciaFolha + " month) < pex_data_ini), 0, 1) "
                + "           when toc_codigo = '7' then if((ade_vlr_folha is null and ade_data < pex_data_ini) or (ade_prd_pagas >= ade_prazo and date_add(ade_ano_mes_fim, interval " + carenciaFolha + " month) < pex_data_ini), 0, 1) "
                + "           when toc_codigo = '35' then if((ade_vlr_folha is null and ade_data < pex_data_ini) or (ade_prd_pagas >= ade_prazo and date_add(ade_ano_mes_fim, interval " + carenciaFolha + " month) < pex_data_ini), 0, 1) "
                + "           when toc_codigo = '4' and sad_codigo <> '16' and ade.usu_codigo <> '1' then -1 "
                + "           else 0 end, 0)) as saldo_2, "
                + "  rse_margem_3, rse_margem_rest_3, "
                + "  sum(if(ade_inc_margem = 3, "
                + "         ifnull(ade_vlr_folha, ade_vlr) * case "
                + "           when toc_codigo = '6' then if((ade_vlr_folha is null and ade_data < pex_data_ini) or (ade_prd_pagas >= ade_prazo and date_add(ade_ano_mes_fim, interval " + carenciaFolha + " month) < pex_data_ini), 0, 1) "
                + "           when toc_codigo = '7' then if((ade_vlr_folha is null and ade_data < pex_data_ini) or (ade_prd_pagas >= ade_prazo and date_add(ade_ano_mes_fim, interval " + carenciaFolha + " month) < pex_data_ini), 0, 1) "
                + "           when toc_codigo = '35' then if((ade_vlr_folha is null and ade_data < pex_data_ini) or (ade_prd_pagas >= ade_prazo and date_add(ade_ano_mes_fim, interval " + carenciaFolha + " month) < pex_data_ini), 0, 1) "
                + "           when toc_codigo = '4' and sad_codigo <> '16' and ade.usu_codigo <> '1' then -1 "
                + "           else 0 end, 0)) as saldo_3 "
                + "from tb_aut_desconto ade "
                + "inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade.ade_codigo) "
                + "inner join tb_registro_servidor rse on (rse.rse_codigo = ade.rse_codigo) "
                + "inner join tb_periodo_exportacao pex on (pex.org_codigo = rse.org_codigo) "
                + "where ade_inc_margem in ('1','2','3') "
                + "and oca_data between pex_data_ini and pex_data_fim "
                + "group by rse_codigo ";
        LOG.debug(query);
        jdbc.update(query, queryParams);

        /* ************************************************************************************************************** */
        /* Calcula o estoque
         * Lembrete: A condição ade_vlr_folha is null é necessária para excluir os
         * casos STATUS = Bloqueado/Suspenso da soma.
         * Duas condições podem ser consideradas para a seleção de contratos deferidos:
         * Quando a data inicial é menor que o período atual ou existe ocorrência de reimplante,
         * isso porque pode ser uma reexportação, nesse caso a primeira condição falha.
         */
        query = "delete from tb_tmp_exp_estoque";
        LOG.debug(query);
        jdbc.update(query, queryParams);
        query = "insert into tb_tmp_exp_estoque (rse_codigo, rse_matricula, rse_margem, rse_margem_rest, estoque, qtde, rse_margem_2, rse_margem_rest_2, estoque_2, qtde_2, rse_margem_3, rse_margem_rest_3, estoque_3, qtde_3, usado) "
                + "select ade.rse_codigo, rse_matricula,  "
                + "rse_margem, rse_margem_rest, sum(if(ade_inc_margem = 1, ifnull(ade_vlr_folha, ade_vlr), 0)) estoque, count(*) as qtde, "
                + "rse_margem_2, rse_margem_rest_2, sum(if(ade_inc_margem = 2,ifnull(ade_vlr_folha, ade_vlr), 0)) estoque_2, count(*) as qtde_2, "
                + "rse_margem_3, rse_margem_rest_3, sum(if(ade_inc_margem = 3, ifnull(ade_vlr_folha, ade_vlr), 0)) estoque_3, count(*) as qtde_3, "
                + "'N' as usado "
                + "from tb_aut_desconto ade "
                + "inner join tb_registro_servidor rse on (rse.rse_codigo = ade.rse_codigo) "
                + "inner join tb_periodo_exportacao pex on (pex.org_codigo = rse.org_codigo) "
                + "left outer join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade.ade_codigo "
                + "   and oca.toc_codigo = '10' and oca.oca_data between pex.pex_data_ini and pex.pex_data_fim) "
                + "where ade_inc_margem in ('1','2','3') "
                + "and ((((sad_codigo = '4' and (oca.oca_codigo is not null or ade.ade_ano_mes_ini < pex.pex_periodo)) or sad_codigo = '12' or sad_codigo = '5') and ade_vlr_folha is null) or sad_codigo = '16') "
                + "group by rse.rse_codigo, rse_matricula ";
        LOG.debug(query);
        jdbc.update(query, queryParams);

        // inclui os estoques no saldo
        query = "update tb_tmp_exp_saldo_periodo tmp "
                + "inner join tb_tmp_exp_estoque est on (est.rse_codigo = tmp.rse_codigo) "
                + "set saldo = saldo - estoque, saldo_2 = saldo_2 - estoque_2, saldo_3 = saldo_3 - estoque_3, usado = 'S' ";
        LOG.debug(query);
        jdbc.update(query, queryParams);
        query = "insert into tb_tmp_exp_saldo_periodo "
                + "select rse_codigo, rse_matricula, rse_margem, rse_margem_rest, -estoque, rse_margem_2, rse_margem_rest_2, -estoque_2, rse_margem_3, rse_margem_rest_3, -estoque_3 from tb_tmp_exp_estoque where usado = 'N'";
        LOG.debug(query);
        jdbc.update(query, queryParams);


        /* ************************************************************************************************************** */
        // calcula as alterações
        query = "delete from tb_tmp_exp_saldo_alteracao_";
        LOG.debug(query);
        jdbc.update(query, queryParams);
        query = "insert into tb_tmp_exp_saldo_alteracao_ (rse_codigo, rse_matricula, rse_margem, rse_margem_rest, saldo_alteracao, rse_margem_2, rse_margem_rest_2, saldo_alteracao_2, rse_margem_3, rse_margem_rest_3, saldo_alteracao_3, usado) "
                + "select distinct ade.rse_codigo, rse_matricula, "
                + "rse_margem, rse_margem_rest, if(ade_inc_margem = 1, (ade_vlr_folha - ade_vlr), 0) as saldo_alteracao, "
                + "rse_margem_2, rse_margem_rest_2, if(ade_inc_margem = 2, (ade_vlr_folha - ade_vlr), 0) as saldo_alteracao_2, "
                + "rse_margem_3, rse_margem_rest_3, if(ade_inc_margem = 3, (ade_vlr_folha - ade_vlr), 0) as saldo_alteracao_3, "
                + "'N' as usado "
                + "from tb_aut_desconto ade "
                + "inner join tb_registro_servidor rse on (rse.rse_codigo = ade.rse_codigo) "
                + "inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade.ade_codigo) "
                + "inner join tb_periodo_exportacao pex on (pex.org_codigo = rse.org_codigo) "
                + "where ade_inc_margem in ('1','2','3') "
                + "and toc_codigo = '14' "
                + "and oca_data between pex_data_ini and pex_data_fim "
                + "and sad_codigo in ('5', '16') and ade_vlr_folha is not null ";
        LOG.debug(query);
        jdbc.update(query, queryParams);

        query = "delete from tb_tmp_exp_saldo_alteracao";
        LOG.debug(query);
        jdbc.update(query, queryParams);
        query = "insert into tb_tmp_exp_saldo_alteracao (rse_codigo, rse_matricula, rse_margem, rse_margem_rest, saldo_alteracao, rse_margem_2, rse_margem_rest_2, saldo_alteracao_2, rse_margem_3, rse_margem_rest_3, saldo_alteracao_3, usado) "
                + "select rse_codigo, rse_matricula, "
                + "rse_margem, rse_margem_rest, sum(saldo_alteracao) as saldo_alteracao, "
                + "rse_margem_2, rse_margem_rest_2, sum(saldo_alteracao_2) as saldo_alteracao_2, "
                + "rse_margem_3, rse_margem_rest_3, sum(saldo_alteracao_3) as saldo_alteracao_3, "
                + "usado "
                + "from tb_tmp_exp_saldo_alteracao_ group by rse_codigo ";
        LOG.debug(query);
        jdbc.update(query, queryParams);

        // inclui as alterações no saldo
        query = "update tb_tmp_exp_saldo_periodo tmp "
                + "inner join tb_tmp_exp_saldo_alteracao alt on (alt.rse_codigo = tmp.rse_codigo) "
                + "set saldo = saldo + saldo_alteracao, saldo_2 = saldo_2 + saldo_alteracao_2, saldo_3 = saldo_3 + saldo_alteracao_3, usado = 'S' ";
        LOG.debug(query);
        jdbc.update(query, queryParams);
        query = "insert into tb_tmp_exp_saldo_periodo "
                + "select rse_codigo, rse_matricula, rse_margem, rse_margem_rest, saldo_alteracao, rse_margem_2, rse_margem_rest_2, saldo_alteracao_2, rse_margem_3, rse_margem_rest_3, saldo_alteracao_3 from tb_tmp_exp_saldo_alteracao where usado = 'N'";
        LOG.debug(query);
        jdbc.update(query, queryParams);

        /* ************************************************************************************************************** */
        // calcula as alterações rejeitadas
        query = "delete from tb_tmp_exp_saldo_alteracao_rejeitada_;";
        LOG.debug(query);
        jdbc.update(query, queryParams);
        query = "insert into tb_tmp_exp_saldo_alteracao_rejeitada_ (rse_codigo, rse_matricula, rse_margem, rse_margem_rest, saldo_alteracao, rse_margem_2, rse_margem_rest_2, saldo_alteracao_2, rse_margem_3, rse_margem_rest_3, saldo_alteracao_3, usado) "
                + "select distinct ade.rse_codigo, rse_matricula, "
                + "rse_margem, rse_margem_rest, if(ade_inc_margem = 1, (ade_vlr_folha - ade_vlr), 0) as saldo_alteracao, "
                + "rse_margem_2, rse_margem_rest_2, if(ade_inc_margem = 2, (ade_vlr_folha - ade_vlr), 0) as saldo_alteracao_2, "
                + "rse_margem_3, rse_margem_rest_3, if(ade_inc_margem = 3, (ade_vlr_folha - ade_vlr), 0) as saldo_alteracao_3, "
                +  "'N' as usado "
                + "from tb_aut_desconto ade "
                + "inner join tb_registro_servidor rse on (rse.rse_codigo = ade.rse_codigo) "
                + "inner join tb_periodo_exportacao pex on (pex.org_codigo = rse.org_codigo) "
                + "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
                + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
                + "left outer join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade.ade_codigo and toc_codigo = '14' and oca_data >= pex_data_ini) "
                + "left outer join tb_param_sist_consignante tpc on (tpc.tpc_codigo = '" + CodedValues.TPC_DEFAULT_PARAM_SVC_REIMPLANTE + "') "
                + "left outer join tb_param_svc_consignataria psc on (psc.svc_codigo = cnv.svc_codigo and psc.csa_codigo = cnv.csa_codigo and psc.tps_codigo = '35') "
                + "where ade_inc_margem in ('1','2','3') "
                + "and ifnull(psc.psc_vlr, ifnull(tpc.psi_vlr, 'N')) = 'S' " // Somente para CSAs que reimplantam automaticamente.
                + "and oca.ade_codigo is null " // Não tem ocorrência de alteração no período. Não testa por oca_data between pex_data_ini and pex_data_fim, pois pode haver alteração realizada após o corte, que tmb deve ser considerada.
                + "and sad_codigo = '5' "
                + "and ade_vlr_folha is not null "
                + "and ade_vlr_folha <> ade_vlr ";
        LOG.debug(query);
        jdbc.update(query, queryParams);

        query = "delete from tb_tmp_exp_saldo_alteracao_rejeitada";
        LOG.debug(query);
        jdbc.update(query, queryParams);
        query = "insert into tb_tmp_exp_saldo_alteracao_rejeitada (rse_codigo, rse_matricula, rse_margem, rse_margem_rest, saldo_alteracao, rse_margem_2, rse_margem_rest_2, saldo_alteracao_2, rse_margem_3, rse_margem_rest_3, saldo_alteracao_3, usado) "
                + "select rse_codigo, rse_matricula, "
                + "rse_margem, rse_margem_rest, sum(saldo_alteracao) as saldo_alteracao, "
                + "rse_margem_2, rse_margem_rest_2, sum(saldo_alteracao_2) as saldo_alteracao_2, "
                + "rse_margem_3, rse_margem_rest_3, sum(saldo_alteracao_3) as saldo_alteracao_3, "
                + "usado "
                + "from tb_tmp_exp_saldo_alteracao_rejeitada_ group by rse_codigo ";
        LOG.debug(query);
        jdbc.update(query, queryParams);

        // inclui as alterações rejeitadas no saldo
        query = "update tb_tmp_exp_saldo_periodo tmp "
                + "inner join tb_tmp_exp_saldo_alteracao_rejeitada alt on (alt.rse_codigo = tmp.rse_codigo) "
                + "set saldo = saldo + saldo_alteracao, saldo_2 = saldo_2 + saldo_alteracao_2, saldo_3 = saldo_3 + saldo_alteracao_3, usado = 'S' ";
        LOG.debug(query);
        jdbc.update(query, queryParams);
        query = "insert into tb_tmp_exp_saldo_periodo "
                + "select rse_codigo, rse_matricula, rse_margem, rse_margem_rest, saldo_alteracao, rse_margem_2, rse_margem_rest_2, saldo_alteracao_2, rse_margem_3, rse_margem_rest_3, saldo_alteracao_3 from tb_tmp_exp_saldo_alteracao_rejeitada where usado = 'N'";
        LOG.debug(query);
        jdbc.update(query, queryParams);

        /* ************************************************************************************************************** */
        /* Calcula o saldo de suspensões judiciais com liberação de margem:
         * contratos suspensos que foram pagos no último retorno, com ocorrência de liquidação no período atual de exportação,
         * que não estão incidindo na margem, porém o serviço incide, são contratos que foram suspensos com liberação da margem
         * em alteração avançada e que a atual margem folha está abatendo, devendo somar ao saldo de margem do período.
         */
        query = "delete from tb_tmp_saldo_suspensao_judicial";
        LOG.debug(query);
        jdbc.update(query, queryParams);
        query = "insert into tb_tmp_saldo_suspensao_judicial (rse_codigo, rse_matricula, rse_margem, rse_margem_rest, saldo_suspensao, qtde, rse_margem_2, rse_margem_rest_2, saldo_suspensao_2, qtde_2, rse_margem_3, rse_margem_rest_3, saldo_suspensao_3, qtde_3, usado) "
                + "select "
                + "  rse.rse_codigo, rse.rse_matricula, "
                + "  rse.rse_margem,   rse.rse_margem_rest,   sum(if(pse.pse_vlr = '1', ade.ade_vlr_folha, 0)) saldo_suspensao,   count(*) as qtde,   "
                + "  rse.rse_margem_2, rse.rse_margem_rest_2, sum(if(pse.pse_vlr = '2', ade.ade_vlr_folha, 0)) saldo_suspensao_2, count(*) as qtde_2, "
                + "  rse.rse_margem_3, rse.rse_margem_rest_3, sum(if(pse.pse_vlr = '3', ade.ade_vlr_folha, 0)) saldo_suspensao_3, count(*) as qtde_3, "
                + "  'N' as usado "
                + "from tb_registro_servidor rse "
                + "inner join tb_aut_desconto ade on (rse.rse_codigo = ade.rse_codigo) "
                + "inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) "
                + "inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) "
                + "inner join tb_param_svc_consignante pse on (cnv.svc_codigo = pse.svc_codigo and pse.tps_codigo = '" + CodedValues.TPS_INCIDE_MARGEM + "') "
                + "inner join tb_periodo_exportacao pex on (cnv.org_codigo = pex.org_codigo) "
                + "where ade.sad_codigo = '" + CodedValues.SAD_SUSPENSA_CSE + "' "
                + "  and ade.ade_paga = 'S' "
                + "  and coalesce(ade.ade_vlr_folha, 0) > 0 "
                + "  and exists ( "
                + "    select 1 from tb_ocorrencia_autorizacao oca "
                + "    where ade.ade_codigo = oca.ade_codigo "
                + "      and oca.toc_codigo = '" + CodedValues.TOC_TARIF_LIQUIDACAO + "' "
                + "      and oca.oca_data between pex.pex_data_ini and pex.pex_data_fim "
                + "  ) "
                + "  and ade.ade_inc_margem = 0 "
                + "  and pse.pse_vlr <> '0' "
                + "group by rse.rse_codigo"
                ;
        LOG.debug(query);
        jdbc.update(query, queryParams);

        // inclui as suspensões judiciais no saldo do período
        query = "update tb_tmp_exp_saldo_periodo tmp "
                + "inner join tb_tmp_saldo_suspensao_judicial suj on (suj.rse_codigo = tmp.rse_codigo) "
                + "set saldo = saldo + saldo_suspensao, saldo_2 = saldo_2 + saldo_suspensao_2, saldo_3 = saldo_3 + saldo_suspensao_3, usado = 'S' ";
        LOG.debug(query);
        jdbc.update(query, queryParams);
        query = "insert into tb_tmp_exp_saldo_periodo "
                + "select rse_codigo, rse_matricula, rse_margem, rse_margem_rest, saldo_suspensao, rse_margem_2, rse_margem_rest_2, saldo_suspensao_2, rse_margem_3, rse_margem_rest_3, saldo_suspensao_3 from tb_tmp_saldo_suspensao_judicial where usado = 'N'";
        LOG.debug(query);
        jdbc.update(query, queryParams);
    }

    // COLOCA EM ESTOQUE CONTRATOS CUJO SERVIDOR ESTAVA COM MARGEM RESTANTE NEGATIVA NO DIA DO CORTE
    protected void moveParaEstoqueMargemNegativa() throws DataAccessException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        /*
         * Esta rotina move para estoque:
         * - Contratos realizados com a margem residual existente entre o dia de corte
         *   e o retorno posterior, mas que segundo a margem recebida pelo retorno,
         *   não podem mais ser enviados.
         * Altera temporariamente o status desses contratos para "Estoque mensal".
         */

        // Altera a situação das ADE para ESTOQUE MENSAL
        String query = "update tb_aut_desconto ade "
                + "inner join tb_registro_servidor rse on (rse.rse_codigo = ade.rse_codigo) "
                + "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
                + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
                + "inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) "
                + "left outer join tb_tmp_exp_saldo_periodo tmp on (rse.rse_codigo = tmp.rse_codigo) "
                + "set sad_codigo = '16' "
                + "where "
                + "if(tmp.rse_matricula is null, rse.rse_margem_rest, tmp.rse_margem + saldo) < 0 "
                + "and ade_inc_margem = 1 "
                + "and sad_codigo = '4' "
                + "and ade.ade_ano_mes_ini = pex.pex_periodo";
        LOG.debug("PARA ESTOQUE (margem 1): " + query);
        jdbc.update(query, queryParams);

        query = "update tb_aut_desconto ade "
                + "inner join tb_registro_servidor rse on (rse.rse_codigo = ade.rse_codigo) "
                + "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
                + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
                + "inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) "
                + "left outer join tb_tmp_exp_saldo_periodo tmp on (rse.rse_codigo = tmp.rse_codigo) "
                + "set sad_codigo = '16' "
                + "where "
                + "if(tmp.rse_matricula is null, rse.rse_margem_rest_2, tmp.rse_margem_2 + saldo_2) < 0 "
                + "and ade_inc_margem = 2 "
                + "and sad_codigo = '4' "
                + "and ade.ade_ano_mes_ini = pex.pex_periodo";
        LOG.debug("PARA ESTOQUE (margem 2): " + query);
        jdbc.update(query, queryParams);

        query = "update tb_aut_desconto ade "
                + "inner join tb_registro_servidor rse on (rse.rse_codigo = ade.rse_codigo) "
                + "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
                + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
                + "inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) "
                + "left outer join tb_tmp_exp_saldo_periodo tmp on (rse.rse_codigo = tmp.rse_codigo) "
                + "set sad_codigo = '16' "
                + "where "
                + "if(tmp.rse_matricula is null, rse.rse_margem_rest_3, tmp.rse_margem_3 + saldo_3) < 0 "
                + "and ade_inc_margem = 3 "
                + "and sad_codigo = '4' "
                + "and ade.ade_ano_mes_ini = pex.pex_periodo";
        LOG.debug("PARA ESTOQUE (margem 3): " + query);
        jdbc.update(query, queryParams);
    }

    // CANCELA ALTERAÇÕES DE CONTRATOS COM MARGEM RESTANTE NEGATIVA NO DIA DO CORTE
    protected void cancelaAlteracaoMargemNegativa() throws DataAccessException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        /*
         * Muda o tipo de ocorrência de alteração para informação dos casos
         * De contratos alterados de servidores cujo saldo de movimento não é comportado
         * pela margem.
         */
        String query = "update tb_ocorrencia_autorizacao oca "
                + "inner join tb_aut_desconto ade on (ade.ade_codigo = oca.ade_codigo) "
                + "inner join tb_registro_servidor rse on (rse.rse_codigo = ade.rse_codigo) "
                + "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
                + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
                + "inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) "
                + "left outer join tb_tmp_exp_saldo_periodo tmp on (rse.rse_codigo = tmp.rse_codigo) "
                + "set oca.toc_codigo = '3' " // Transforma alteração '14' em informação '3'
                + "where "
                + "if(tmp.rse_matricula is null, rse.rse_margem_rest, tmp.rse_margem + saldo) < 0 "
                + "and ade_inc_margem = 1 "
                + "and oca.toc_codigo = '14' "
                + "and sad_codigo = '5' "
                + "and ade.ade_vlr_folha < ade.ade_vlr " // Somente alterações que aumentam o valor
                + "and oca.oca_data between pex.pex_data_ini and pex.pex_data_fim";
        LOG.debug("ALTERAÇÃO CANCELADA (margem 1): " + query);
        jdbc.update(query, queryParams);

        query = "update tb_ocorrencia_autorizacao oca "
                + "inner join tb_aut_desconto ade on (ade.ade_codigo = oca.ade_codigo) "
                + "inner join tb_registro_servidor rse on (rse.rse_codigo = ade.rse_codigo) "
                + "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
                + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
                + "inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) "
                + "left outer join tb_tmp_exp_saldo_periodo tmp on (rse.rse_codigo = tmp.rse_codigo) "
                + "set oca.toc_codigo = '3' " // Transforma alteração '14' em informação '3'
                + "where "
                + "if(tmp.rse_matricula is null, rse.rse_margem_rest_2, tmp.rse_margem_2 + saldo_2) < 0 "
                + "and ade_inc_margem = 2 "
                + "and oca.toc_codigo = '14' "
                + "and sad_codigo = '5' "
                + "and ade.ade_vlr_folha < ade.ade_vlr " // Somente alterações que aumentam o valor
                + "and oca.oca_data between pex.pex_data_ini and pex.pex_data_fim";
        LOG.debug("ALTERAÇÃO CANCELADA (margem 2): " + query);
        jdbc.update(query, queryParams);

        query = "update tb_ocorrencia_autorizacao oca "
                + "inner join tb_aut_desconto ade on (ade.ade_codigo = oca.ade_codigo) "
                + "inner join tb_registro_servidor rse on (rse.rse_codigo = ade.rse_codigo) "
                + "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
                + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
                + "inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) "
                + "left outer join tb_tmp_exp_saldo_periodo tmp on (rse.rse_codigo = tmp.rse_codigo) "
                + "set oca.toc_codigo = '3' " // Transforma alteração '14' em informação '3'
                + "where "
                + "if(tmp.rse_matricula is null, rse.rse_margem_rest_3, tmp.rse_margem_3 + saldo_3) < 0 "
                + "and ade_inc_margem = 3 "
                + "and oca.toc_codigo = '14' "
                + "and sad_codigo = '5' "
                + "and ade.ade_vlr_folha < ade.ade_vlr " // Somente alterações que aumentam o valor
                + "and oca.oca_data between pex.pex_data_ini and pex.pex_data_fim";
        LOG.debug("ALTERAÇÃO CANCELADA (margem 3): " + query);
        jdbc.update(query, queryParams);
    }

    protected void selectCandidatasRelancamento(List<String> orgCodigos, List<String> estCodigos,
            List<String> verbas, String periodo, AcessoSistema responsavel) throws DataAccessException, ExportaMovimentoException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        final String defaultReimplante = (String) ParamSist.getInstance().getParamOrDefault(CodedValues.TPC_DEFAULT_PARAM_SVC_REIMPLANTE, CodedValues.TPC_NAO, responsavel);
        final String defaultPreservacao = (String) ParamSist.getInstance().getParamOrDefault(CodedValues.TPC_DEFAULT_PARAM_SVC_PRESERVA_PRD, CodedValues.TPC_NAO, responsavel);

        queryParams.addValue("defaultReimplante", defaultReimplante);
        queryParams.addValue("defaultPreservacao", defaultPreservacao);

        final StringBuilder complemento = new StringBuilder();

		if ((orgCodigos != null) && (orgCodigos.size() > 0)) {
			complemento.append(" and cnv.org_codigo in (:orgCodigos) ");
            queryParams.addValue("orgCodigos", orgCodigos);
		}
		if ((estCodigos != null) && (estCodigos.size() > 0)) {
			complemento.append(" and org.est_codigo in (:estCodigos) ");
            queryParams.addValue("estCodigos", estCodigos);
		}
		if ((verbas != null) && (verbas.size() > 0)) {
			complemento.append(" and cnv.cnv_cod_verba in (:verbas) ");
            queryParams.addValue("verbas", verbas);
		}

        String query = "delete from tb_tmp_exp_relancamentos";
        LOG.debug(query);
        jdbc.update(query, queryParams);
        /*
         * As ADEs candidatas a relançamento são:
         * - Aquelas que estão em estoque (12)
         * - Aquelas que estão deferidas ou em andamento (4, 5) cujo ade_vlr_folha is null, pois
         * contratos não pagos por motivo STATUS = Bloqueado/Suspenso (ade_vlr_folha not null)
         * não devem ser relançados. Seleciona apenas contratos com ade_ano_mes_ini < pex_periodo,
         * para não buscar ADEs novas.
         * - Aquelas que estão em andamento (5) cujo ade_vlr_folha <> ade_valor e que
         * não têm ocorrência de alteração, ou seja, alterações rejeitadas pela folha ou
         * alterações que não cabiam na margem.
         *
         * OBS: O campo pode_reimplantar é preenchido de acordo com a seguinte regra:
         * Se o contrato foi totalmente pago, não reimplanta. Caso contrário verifica se
         * o convênio tem reimplante automático, ou houve uma alteração no período atual,
         * ou houve uma inclusão no período atual. Os dois últimos casos asseguram que
         * uma alteração ou um novo contrato que foram diretamente para estoque
         * sejam exportados, se couberem na margem. Contratos que já estejam abertos e
         * que tenham ade_ano_mes_fim anterior ao periodo atual não devem ser reimplantados.
         * Lembrete: Alterações do período atual que não cabiam na margem
         * já foram canceladas (toc_codigo = '14' passado para toc_codigo = '3').
         *
         * OBS 2: Precisamos selecionar tmb os contratos que não podem ser reimplantados,
         * pois eles tmb decrementam a margem disponível e os valores desses contratos
         * devem ser considerados no cálculo de quais contratos podem ser reimplantados.
         */
        query = "insert into tb_tmp_exp_relancamentos (ade_codigo, sad_codigo, ade_vlr, rse_matricula, ade_inc_margem, rse_margem, margem_rest, enviar, svc_prioridade, cnv_prioridade, nse_codigo, ade_data, ade_ano_mes_ini, ade_ano_mes_fim, ade_numero, ade_ano_mes_ini_ref, ade_ano_mes_fim_ref, tem_oca, pode_reimplantar, reimplante_alteracao) " + "select distinct ade.ade_codigo, sad_codigo, ade_vlr - ifnull(ade_vlr_folha, 0) as ade_vlr, rse.rse_matricula, " + "ade_inc_margem, case " + " when ade_inc_margem = '1' then rse.rse_margem " + " when ade_inc_margem = '2' then rse.rse_margem_2 " + " when ade_inc_margem = '3' then rse.rse_margem_3 " + "end as RSE_MARGEM, case " + " when ade_inc_margem = '1' then if(tmp.rse_matricula is null, rse.rse_margem_rest, tmp.rse_margem + saldo) " + " when ade_inc_margem = '2' then if(tmp.rse_matricula is null, rse.rse_margem_rest_2, tmp.rse_margem_2 + saldo_2) " + " when ade_inc_margem = '3' then if(tmp.rse_matricula is null, rse.rse_margem_rest_3, tmp.rse_margem_3 + saldo_3) " + "end as MARGEM_REST, " + "'N' as enviar, " + "coalesce(svc_prioridade, 999999) + 0 as svc_prioridade, coalesce(cnv_prioridade, 999999) as cnv_prioridade, " + "svc.nse_codigo, " + "ade_data, ade_ano_mes_ini, ade_ano_mes_fim, ade_numero, ade_ano_mes_ini_ref, ade_ano_mes_fim_ref, " + "if(oca10.oca_codigo is null, 'N', 'S') AS tem_oca, " + "if(ifnull(psc.psc_vlr, :defaultReimplante) = 'S' or oca14.oca_codigo is not null or oca4.oca_codigo is not null, 'S', 'N') as pode_reimplantar, " + "if(ade.sad_codigo = '5' and ade.ade_vlr_folha <> ade.ade_vlr and oca14.oca_codigo is null, 'S', 'N') as reimplante_alteracao " + "from tb_aut_desconto ade " + "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) " + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) " + "inner join tb_servico svc on (svc.svc_codigo = cnv.svc_codigo) " + "inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) " + "inner join tb_orgao org on (org.org_codigo = cnv.org_codigo) " + "inner join tb_registro_servidor rse on (rse.rse_codigo = ade.rse_codigo) " + "inner join tb_consignataria csa on (csa.csa_codigo = cnv.csa_codigo) " + (enviaContratosSerBloqueados ? ""
                : "left outer join tb_param_convenio_registro_ser pcr on (rse.rse_codigo = pcr.rse_codigo and cnv.cnv_codigo = pcr.cnv_codigo and pcr.pcr_vlr = '0' and pcr.tps_codigo = '" + CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO + "') "
                + "left outer join tb_param_servico_registro_ser psr on (rse.rse_codigo = psr.rse_codigo and cnv.svc_codigo = psr.svc_codigo and psr.psr_vlr = '0' and psr.tps_codigo = '" + CodedValues.TPS_NUM_CONTRATOS_POR_SERVICO + "') "
                + "left outer join tb_param_nse_registro_ser pnr on (rse.rse_codigo = pnr.rse_codigo and svc.nse_codigo = pnr.nse_codigo and pnr.pnr_vlr = '0' and pnr.tps_codigo = '" + CodedValues.TPS_NUM_CONTRATOS_POR_NATUREZA_SERVICO + "') "
                ) + "left outer join tb_param_svc_consignataria psc on (cnv.svc_codigo = psc.svc_codigo and cnv.csa_codigo = psc.csa_codigo and psc.tps_codigo = '35' and (psc.psc_ativo = '1' or psc.psc_ativo is null)) " + "left outer join tb_param_svc_consignataria psc1 on (cnv.svc_codigo = psc1.svc_codigo and cnv.csa_codigo = psc1.csa_codigo and psc1.tps_codigo = '36' and (psc1.psc_ativo = '1' or psc1.psc_ativo is null)) " + "left outer join tb_ocorrencia_autorizacao oca10 on (ade.ade_codigo = oca10.ade_codigo and oca10.toc_codigo = '10' and oca10.oca_data between pex.pex_data_ini and pex.pex_data_fim) " + "left outer join tb_ocorrencia_autorizacao oca14 on (ade.ade_codigo = oca14.ade_codigo and oca14.toc_codigo = '14' and oca14.oca_data between pex.pex_data_ini and pex.pex_data_fim) " + "left outer join tb_ocorrencia_autorizacao oca4  on (ade.ade_codigo = oca4.ade_codigo  and oca4.toc_codigo = '4'   and oca4.oca_data  between pex.pex_data_ini and pex.pex_data_fim) " + "left outer join tb_tmp_exp_saldo_periodo tmp on (rse.rse_codigo = tmp.rse_codigo) " + "where " + "((sad_codigo in ('" + CodedValues.SAD_ESTOQUE + "', '" + CodedValues.SAD_ESTOQUE_MENSAL + "')) or " + " (sad_codigo in ('" + CodedValues.SAD_DEFERIDA + "', '" + CodedValues.SAD_EMANDAMENTO + "') and ade_vlr_folha is null and ade.ade_ano_mes_ini < pex.pex_periodo) or " + " (sad_codigo = '" + CodedValues.SAD_EMANDAMENTO + "' and ade_vlr_folha is not null and ade_vlr_folha <> ade_vlr and oca14.oca_codigo is null)) " + "and ade.ade_ano_mes_ini <= pex.pex_periodo " + "and ade_int_folha = '" + CodedValues.INTEGRA_FOLHA_SIM + "' " + "and srs_codigo = '" + CodedValues.SRS_ATIVO + "' " + "and ((ifnull(psc1.psc_vlr, :defaultPreservacao) = 'N' and (ade.ade_ano_mes_fim >= '" + periodo + "' or ade_ano_mes_fim is null)) or ifnull(psc1.psc_vlr, :defaultPreservacao) = 'S') " + complemento.append(enviaContratosSerBloqueados ? ""
                : " and pcr.pcr_vlr is null "
                + " and psr.psr_vlr is null "
                + " and pnr.pnr_vlr is null ").toString()
                ;
        LOG.debug(query);
        jdbc.update(query, queryParams);
        encerrarAdePreservacaoFinal(periodo, defaultPreservacao, responsavel);
    }

    /**
     * Encerra os contratos que preservam parcelas, porém a quantidade de tentativas de preservação
     * sem desconto já alcançou o limite, que são X parcelas após o prazo final do contrato.
     * @param stat
     * @param periodo
     * @param defaultPreservacao
     * @param responsavel
     * @throws DataAccessException
     * @throws ExportaMovimentoException
     */
    protected void encerrarAdePreservacaoFinal(String periodo, String defaultPreservacao, AcessoSistema responsavel) throws DataAccessException, ExportaMovimentoException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("defaultPreservacao", defaultPreservacao);

        final String novoSadCodigo = CodedValues.SAD_INDEFERIDA;


        // Conclui os contratos que pode ser reimplantados e preservam mas já esgotaram as tentativas
        String query = "update tb_aut_desconto ade "
                        + "inner join tb_tmp_exp_relancamentos tmp on (tmp.ade_codigo = ade.ade_codigo) "
                        + "set ade.sad_codigo = '" + novoSadCodigo + "', "
                        + "tmp.pode_reimplantar = 'N' "
                        + "where tmp.sad_codigo <> '12' "
                        + "and tmp.reimplante_alteracao = 'N' "
                        + "and ade.ade_prazo is not null "
                        // CONTRATOS SÃO PRESERVADOS SOMENTE ATÉ O LIMITE DE N TENTATIVAS APÓS O "PRAZO FINAL"
                        // DO CONTRATO. DEPOIS SÃO CONCLUÍDOS, COM STATUS "3", QUE NA MARINHA É "ENCERRADO POR EXCLUSÃO"
                        + "and (date_add(coalesce((select max(prd.prd_data_desconto) from tb_parcela_desconto prd "
                        + "where prd.ade_codigo = ade.ade_codigo and spd_codigo = '6'), ade.ade_ano_mes_ini), "
                        + "interval (coalesce(ade.ade_prazo, 9999) - coalesce(ade.ade_prd_pagas, 0) + " + LIMITE_PRESERVACAO + ") month) < '" + periodo + "') "
                        // E A CONSIGNATÁRIA QUER PRESERVAR PARCELA
                        + "and 'S' = (select (ifnull(psc.psc_vlr, :defaultPreservacao)) from tb_verba_convenio vco "
                        + "           inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
                        + "           left outer join tb_param_svc_consignataria psc on (psc.csa_codigo = cnv.csa_codigo and psc.svc_codigo = cnv.svc_codigo and psc.tps_codigo = '" + CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL + "') "
                        + "           where vco.vco_codigo = ade.vco_codigo)";
        LOG.debug(query);
        int rows = jdbc.update(query, queryParams);
        LOG.debug("Linhas afetadas: " + rows);

        // Insere ocorrência de conclusão para os contratos que não podem mais ser reimplantados
        query = "INSERT INTO tb_ocorrencia_autorizacao (OCA_CODIGO, TOC_CODIGO, ADE_CODIGO, USU_CODIGO, OCA_DATA, OCA_PERIODO, OCA_OBS) "
                + "SELECT DISTINCT concat(lpad(ade.ade_numero, 10, '0'), '-', date_format(now(), '%Y%m%d%H%i%S')), "
                + "'" + CodedValues.TOC_CONCLUSAO_SEM_DESCONTO + "', ade.ade_codigo, '1', '" + periodo + "', '" + periodo + "', 'ENCERRADO POR EXCLUSÃO' "
                + "from tb_aut_desconto ade "
                + "inner join tb_tmp_exp_relancamentos tmp on (tmp.ade_codigo = ade.ade_codigo) "
                + "where ade.sad_codigo = '" + novoSadCodigo + "' "
                + "and tmp.pode_reimplantar = 'N' ";
        LOG.debug(query);
        rows = jdbc.update(query, queryParams);
        LOG.debug("Linhas afetadas: " + rows);

        // Obtém o código dos registros servidores para o cálculo de margem parcial
        query = "select distinct ade.rse_codigo "
                + "from tb_aut_desconto ade "
                + "inner join tb_tmp_exp_relancamentos tmp on (tmp.ade_codigo = ade.ade_codigo) "
                + "where ade.sad_codigo = '" + novoSadCodigo + "' "
                + "and tmp.pode_reimplantar = 'N' ";
        LOG.debug(query);
        final List<String> rseCodigos = jdbc.queryForList(query, queryParams, String.class);

        if (!rseCodigos.isEmpty()) {
            // Recalcula margem dos servidores que tiveram contratos concluídos,
            // sem histórico, já que é feito na rotina de exportação
            try {
                final ServidorDelegate serDelegate = new ServidorDelegate();
                serDelegate.recalculaMargem("RSE", rseCodigos, responsavel);

                // Recalcula o período para o de exportação
                final PeriodoDelegate perDelegate = new PeriodoDelegate();
                perDelegate.obtemPeriodoExpMovimento(null, null, true, responsavel);
            } catch (MargemControllerException | PeriodoException ex) {
                throw new ExportaMovimentoException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    @Override
    public void posProcessaParcelas(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        final List<String> orgCodigos = parametrosExportacao.getOrgCodigos();
        final List<String> estCodigos = parametrosExportacao.getEstCodigos();
        try {

            final StringBuilder query = new StringBuilder();

            // Obtém o período atual
            final PeriodoDelegate perDelegate = new PeriodoDelegate();
            final TransferObject periodoExportacao = perDelegate.obtemPeriodoExportacaoDistinto(orgCodigos, estCodigos, responsavel);
            final String periodo = DateHelper.format((java.util.Date) periodoExportacao.getAttribute(Columns.PEX_PERIODO), "yyyy-MM-dd");
            LOG.debug("periodoAtual=" + periodo);

            query.append("update tb_aut_desconto ade ");
            query.append("inner join tb_parcela_desconto_periodo pdp on (pdp.ade_codigo = ade.ade_codigo) ");
            query.append("set spd_codigo = '").append(CodedValues.SPD_EMPROCESSAMENTO).append("' ");
            query.append("where ");
            query.append("prd_data_desconto = '").append(periodo).append("' ");
            query.append("and spd_codigo = '").append(CodedValues.SPD_EMABERTO).append("'");
            LOG.debug(query.toString());
            final int rows = jdbc.update(query.toString(), queryParams);
            LOG.debug("Linhas afetadas: " + rows);

        } catch (final DataAccessException | PeriodoException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void posProcessaAutorizacoes(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        if (retiraDoEstoquePosExportacao) {
            try {
                final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
                final MapSqlParameterSource queryParams = new MapSqlParameterSource();

                final String query = "update tb_aut_desconto ade set sad_codigo = '4' where sad_codigo = '16'";
                LOG.debug("RETIRA DO ESTOQUE: " + query);
                jdbc.update(query, queryParams);
            } catch (final DataAccessException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ExportaMovimentoException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        if (!enviaContratosSerBloqueados) {
            try {
                // Remove da tabela de exportação contratos de servidores bloqueados
                LOG.debug("Marinha.removerContratosServidoresBloqueados: " + DateHelper.getSystemDatetime());
                removerContratosServidoresBloqueados(true);
                LOG.debug("fim - Marinha.removerContratosServidoresBloqueados: " + DateHelper.getSystemDatetime());

            } catch (final DataAccessException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
            }
        }
    }


    protected void alteraOcorrenciasAlteracao(String periodoAnterior) throws DataAccessException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("periodoAnterior", periodoAnterior);

        final String query = "update tb_aut_desconto ade "
                    + "inner join tb_tmp_exp_relancamentos tmp on (tmp.ade_codigo = ade.ade_codigo) "
                    + "inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade.ade_codigo) "
                    + "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
                    + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
                    + "inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) "
                    + "left outer join tb_parcela_desconto prd on (prd.ade_codigo = ade.ade_codigo and PRD_DATA_DESCONTO = :periodoAnterior) "
                    + "set toc_codigo = '3' " // -- Transforma em 3 (Informação) para não ser enviado no movimento
                    + "where "
                    + "oca_data between PEX_DATA_INI and PEX_DATA_FIM " // -- após o último corte e antes do corte atual
                    + "and ade.sad_codigo = '4' "
                    + "and toc_codigo = '14'  "
                    + "and tmp.enviar = 'N'  "
                    + "and (prd.prd_numero is null or spd_codigo = '5') ";
            LOG.debug("OCORRENCIA ALTERAÇÃO CONTRATOS DE RELANÇAMENTO DEFERIDOS : " + query);
            jdbc.update(query, queryParams);
    }
}
