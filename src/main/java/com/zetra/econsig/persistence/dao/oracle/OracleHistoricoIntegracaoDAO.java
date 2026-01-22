package com.zetra.econsig.persistence.dao.oracle;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ExportaMovimento;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.parser.EscritorArquivoTexto;
import com.zetra.econsig.parser.config.HeaderTipo;
import com.zetra.econsig.persistence.dao.generic.GenericHistoricoIntegracaoDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.values.TipoMotivoNaoExportacaoEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;

/**
 * <p>Title: OracleHistoricoIntegracaoDAO</p>
 * <p>Description: Implementacao do DAO de histórico integração para o Oracle</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OracleHistoricoIntegracaoDAO extends GenericHistoricoIntegracaoDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(OracleHistoricoIntegracaoDAO.class);

    @Override
    public void criarTabelasExportacaoMovFin(List<TransferObject> tdaList) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        try {
            final StringBuilder query = new StringBuilder();
            query.append("CALL dropTableIfExists('tb_tmp_exp_inicial')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create table tb_tmp_exp_inicial (");
            query.append("rse_codigo varchar2(32), ");
            query.append("csa_codigo varchar2(32), ");
            query.append("cnv_cod_verba varchar2(32), ");
            query.append("oca_periodo date, ");
            query.append("tipo char(1) ");
            query.append(")");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create index tb_tmp_exp_inicial_IDX1 on tb_tmp_exp_inicial (rse_codigo, csa_codigo, cnv_cod_verba)");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);


            query.setLength(0);
            query.append("CALL dropTableIfExists('tb_tmp_exp_mov_fin')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create table tb_tmp_exp_mov_fin (");
            query.append("ade_codigo varchar2(32), ");
            query.append("oca_periodo date, ");
            query.append("tipo char(1) ");
            query.append(")");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create index tb_tmp_exp_mov_fin_IDX1 on tb_tmp_exp_mov_fin (ade_codigo)");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);


            query.setLength(0);
            query.append("CALL dropTableIfExists('tb_tmp_exportacao')");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create table tb_tmp_exportacao (");
            query.append("ade_codigo varchar2(32), ");
            query.append("ade_numero number(20), ");
            query.append("ade_identificador varchar2(40), ");
            query.append("ade_cod_reg char(1), ");
            query.append("ade_inc_margem number(6), ");
            query.append("ade_tipo_vlr char(1), ");
            query.append("ade_indice varchar2(32), ");
            query.append("ade_data date, ");
            query.append("ade_data_ref date, ");
            query.append("ade_data_exclusao date, ");
            query.append("ade_ano_mes_ini date, ");
            query.append("ade_ano_mes_ini_folha date, ");
            query.append("ade_ano_mes_ini_ref date, ");
            query.append("ade_ano_mes_fim date, ");
            query.append("ade_ano_mes_fim_folha date, ");
            query.append("ade_ano_mes_fim_ref date, ");
            query.append("ade_prazo_exc number(11), ");
            query.append("ade_prazo_folha number(11), ");
            query.append("ade_prazo number(11), ");
            query.append("ade_prd_pagas_exc number(11), ");
            query.append("ade_prd_pagas number(11), ");
            query.append("ade_vlr number(13,2), ");
            query.append("ade_vlr_folha number(13,2), ");
            query.append("prd_data_desconto date, ");
            query.append("prd_numero varchar2(32), ");
            query.append("sad_codigo varchar2(32), ");
            query.append("cnv_codigo varchar2(32), ");
            query.append("cnv_cod_verba_ref varchar2(40), ");
            query.append("cnv_cod_verba varchar2(32), ");
            query.append("cnv_prioridade number(11), ");
            query.append("scv_codigo varchar2(32), ");
            query.append("svc_codigo varchar2(32), ");
            query.append("svc_descricao varchar2(100), ");
            query.append("svc_identificador varchar2(40), ");
            query.append("svc_prioridade varchar2(4), ");
            query.append("csa_codigo varchar2(32), ");
            query.append("csa_identificador varchar2(40), ");
            query.append("csa_cnpj varchar2(19), ");
            query.append("est_codigo varchar2(32), ");
            query.append("est_identificador varchar2(40), ");
            query.append("est_cnpj varchar2(19), ");
            query.append("org_codigo varchar2(32), ");
            query.append("org_identificador varchar2(40), ");
            query.append("org_cnpj varchar2(19), ");
            query.append("ser_codigo varchar2(32), ");
            query.append("ser_nome varchar2(100), ");
            query.append("ser_primeiro_nome varchar2(40), ");
            query.append("ser_ultimo_nome varchar2(100), ");
            query.append("ser_nome_meio varchar2(100), ");
            query.append("ser_nome_pai varchar2(100), ");
            query.append("ser_nome_mae varchar2(100), ");
            query.append("ser_cpf varchar2(19), ");
            query.append("ser_nacionalidade varchar2(40), ");
            query.append("rse_codigo varchar2(32), ");
            query.append("rse_matricula varchar2(20), ");
            query.append("rse_matricula_inst varchar2(20), ");
            query.append("rse_tipo varchar2(255), ");
            query.append("rse_obs clob, ");
            query.append("rse_associado char(1), ");
            query.append("rse_margem number(13,2), ");
            query.append("rse_margem_rest number(13,2), ");
            query.append("rse_margem_2 number(13,2), ");
            query.append("rse_margem_rest_2 number(13,2), ");
            query.append("rse_margem_3 number(13,2), ");
            query.append("rse_margem_rest_3 number(13,2), ");
            query.append("pos_codigo varchar2(32), ");
            query.append("srs_codigo varchar2(32), ");
            query.append("trs_codigo varchar2(32), ");
            query.append("oca_periodo date, ");
            query.append("pex_periodo date, ");
            query.append("pex_periodo_ant date, ");
            query.append("pex_periodo_pos date, ");
            query.append("periodo varchar2(6), ");
            query.append("competencia varchar2(6), ");
            query.append("data date, ");
            query.append("autoriza_pgt_parcial char(1), ");
            query.append("capital_pago number(13,2), ");
            query.append("capital_devido number(13,2), ");
            query.append("saldo_devedor number(13,2), ");
            query.append("codigo_folha varchar2(40), ");
            query.append("percentual_padrao varchar2(255), ");
            query.append("consolida char(1), ");
            query.append("situacao varchar2(2) ");
            query.append(")");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create index tb_tmp_exportacao_IDX0 on tb_tmp_exportacao (consolida)");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create index tb_tmp_exportacao_IDX1 on tb_tmp_exportacao (cnv_codigo, rse_codigo, ade_indice, sad_codigo)");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create index tb_tmp_exportacao_IDX2 on tb_tmp_exportacao (sad_codigo)");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create index tb_tmp_exportacao_IDX3 on tb_tmp_exportacao (ade_codigo)");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create index tb_tmp_exportacao_IDX4 on tb_tmp_exportacao (rse_codigo, ade_inc_margem)");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);


            query.setLength(0);
            query.append("CALL dropTableIfExists('tb_tmp_exportacao_ordenada')");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create table tb_tmp_exportacao_ordenada (");
            query.append("contador number(11) not null, ");
            query.append("num_linha number(11), ");
            query.append("ade_numero number(20), ");
            query.append("ade_identificador varchar2(40), ");
            query.append("ade_cod_reg char(1), ");
            query.append("ade_inc_margem number(6), ");
            query.append("ade_tipo_vlr char(1), ");
            query.append("ade_indice varchar2(32), ");
            query.append("ade_data date, ");
            query.append("ade_data_ref date, ");
            query.append("ade_data_exclusao date, ");
            query.append("ade_ano_mes_ini date, ");
            query.append("ade_ano_mes_ini_folha date, ");
            query.append("ade_ano_mes_ini_ref date, ");
            query.append("ade_ano_mes_fim date, ");
            query.append("ade_ano_mes_fim_folha date, ");
            query.append("ade_ano_mes_fim_ref date, ");
            query.append("nro_parcelas number(11), ");
            query.append("prazo_restante number(11), ");
            query.append("ade_prazo number(11), ");
            query.append("ade_prazo_exc number(11), ");
            query.append("ade_prazo_folha number(11), ");
            query.append("ade_prd_pagas_exc number(11), ");
            query.append("ade_prd_pagas number(11), ");
            query.append("ade_vlr number(13,2), ");
            query.append("ade_vlr_folha number(13,2), ");
            query.append("valor_desconto number(13,2), ");
            query.append("valor_desconto_exc number(13,2), ");
            query.append("valor_desconto_folha number(13,2), ");
            query.append("prd_numero varchar2(32), ");
            query.append("data_desconto date, ");
            query.append("data_ini_contrato date, ");
            query.append("data_fim_contrato date, ");
            query.append("cnv_cod_verba varchar2(32), ");
            query.append("cnv_cod_verba_ref varchar2(40), ");
            query.append("cnv_prioridade number(11), ");
            query.append("svc_descricao varchar2(100), ");
            query.append("svc_identificador varchar2(40), ");
            query.append("svc_prioridade varchar2(4), ");
            query.append("csa_identificador varchar2(40), ");
            query.append("csa_cnpj varchar2(19), ");
            query.append("est_identificador varchar2(40), ");
            query.append("est_cnpj varchar2(19), ");
            query.append("org_identificador varchar2(40), ");
            query.append("org_cnpj varchar2(19), ");
            query.append("ser_nome varchar2(255), ");
            query.append("ser_primeiro_nome varchar2(40), ");
            query.append("ser_ultimo_nome varchar2(100), ");
            query.append("ser_nome_meio varchar2(100), ");
            query.append("ser_nome_pai varchar2(100), ");
            query.append("ser_nome_mae varchar2(100), ");
            query.append("ser_cpf varchar2(19), ");
            query.append("ser_nacionalidade varchar2(40), ");
            query.append("rse_matricula varchar2(20), ");
            query.append("rse_matricula_inst varchar2(20), ");
            query.append("rse_tipo varchar2(255), ");
            query.append("rse_obs clob, ");
            query.append("rse_associado char(1), ");
            query.append("rse_margem number(13,2), ");
            query.append("rse_margem_rest number(13,2), ");
            query.append("rse_margem_2 number(13,2), ");
            query.append("rse_margem_rest_2 number(13,2), ");
            query.append("rse_margem_3 number(13,2), ");
            query.append("rse_margem_rest_3 number(13,2), ");
            query.append("pos_codigo varchar2(32), ");
            query.append("srs_codigo varchar2(32), ");
            query.append("trs_codigo varchar2(32), ");
            query.append("oca_periodo date, ");
            query.append("pex_periodo date, ");
            query.append("pex_periodo_ant date, ");
            query.append("pex_periodo_pos date, ");
            query.append("periodo varchar2(6), ");
            query.append("competencia varchar2(6), ");
            query.append("data date, ");
            query.append("autoriza_pgt_parcial char(1), ");
            query.append("capital_pago number(13,2), ");
            query.append("capital_devido number(13,2), ");
            query.append("saldo_devedor number(13,2), ");
            query.append("codigo_folha varchar2(40), ");
            query.append("percentual_padrao varchar2(255), ");
            query.append(gerarClausulaDadosAutorizacao(tdaList, false, true, false, false, false, null));
            query.append("situacao varchar2(2), ");
            query.append("ord_svc_prioridade number(10,0), ");
            query.append("ord_cnv_prioridade number(10,0), ");
            query.append("ord_ade_ano_mes_ini timestamp, ");
            query.append("ord_ade_data_ref timestamp, ");
            query.append("primary key (contador) ");
            query.append(")");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create index tb_tmp_exportacao_ord_IDX0 on tb_tmp_exportacao_ordenada (ade_numero)");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create index tb_tmp_exportacao_ord_IDX1 on tb_tmp_exportacao_ordenada (pex_periodo, rse_matricula, cnv_cod_verba)");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create index tb_tmp_exportacao_ord_IDX2 on tb_tmp_exportacao_ordenada (pex_periodo, ser_cpf, cnv_cod_verba)");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            if (ParamSist.paramEquals(CodedValues.TPC_CONTRATOS_DEVEM_SER_VALIDADOS_PELA_CSE, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
                query.setLength(0);
                query.append("CALL dropTableIfExists('tb_tmp_contratos_sem_permissao')");
                LOG.trace(query.toString());
                jdbc.update(query.toString(), queryParams);

                query.setLength(0);
                query.append("CALL createTemporaryTable('tb_tmp_contratos_sem_permissao (ade_codigo varchar2(32), org_codigo varchar2(32))')");
                LOG.trace(query.toString());
                jdbc.update(query.toString(), queryParams);

                query.setLength(0);
                query.append("CALL dropTableIfExists('tb_tmp_remove_ade_nunca_enviados')");
                LOG.trace(query);
                jdbc.update(query.toString(), queryParams);

                query.setLength(0);
                query.append("CALL createTemporaryTable('tb_tmp_remove_ade_nunca_enviados (ade_codigo varchar2(32), primary key (ade_codigo))')");
                jdbc.update(query.toString(), queryParams);
            }
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Coloca em uma tabela todos os códigos de autorizações que devem
     * ser exportados. Isso é feito apenas para exportações que são
     * apenas iniciais ou mensais com envio de exclusões.
     * @param orgCodigos : códigos dos órgãos, nulo para todos
     * @param estCodigos : códigos dos estabelecimentos, nulo para todos
     * @param sadCodigos : status dos contratos que serão gerados na exportação inicial
     * @param tocCodigos : tipos de ocorrências do período que serão levadas em conta
     * @throws DAOException
     */
    @Override
    public void setAdeExportacao(List<String> orgCodigos, List<String> estCodigos, List<String> sadCodigos, List<String> tocCodigos) throws DAOException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

            final boolean consolidaMovFin = ParamSist.paramEquals(CodedValues.TPC_CONSOLIDA_DESCONTOS_MOVIMENTO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            final boolean exportaLiqIndependenteAnoMesFim = ParamSist.paramEquals(CodedValues.TPC_EXPORTA_LIQ_INDEPENDENTE_ANO_MES_FIM, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            final boolean permiteAgruparPeriodos = ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            final boolean permiteMultiplosPeriodosAbertos = ParamSist.paramEquals(CodedValues.TPC_PERMITE_DOIS_PERIODOS_EXPORTACAO_ABERTOS, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            final boolean habilitaExtensaoPeriodo = ParamSist.paramEquals(CodedValues.TPC_HABILITA_EXTENSAO_PERIODO_FOLHA_AJUSTES, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            final boolean habilitaUsoOcaPeriodo = ParamSist.paramEquals(CodedValues.TPC_HABILITA_USO_OCA_PERIODO_EXPORTACAO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            final boolean quinzenal = !PeriodoHelper.folhaMensal(AcessoSistema.getAcessoUsuarioSistema());
            final boolean enviaConclusaoFolha = ParamSist.paramEquals(CodedValues.TPC_ENVIA_CONCLUSAO_FOLHA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

            final boolean sistemaReimplanta = ParamSist.paramEquals(CodedValues.TPC_REIMPLANTACAO_AUTOMATICA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema()); // Default=N
            final boolean csaDefineReimplante = ParamSist.paramEquals(CodedValues.TPC_CSA_ALTERA_REIMPLANTACAO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema()); // Default=N
            final boolean sistemaPreserva = !ParamSist.paramEquals(CodedValues.TPC_PRESERVA_PRD_REJEITADA, CodedValues.TPC_NAO, AcessoSistema.getAcessoUsuarioSistema()); // Default=S
            final boolean csaDefinePreservacao = ParamSist.paramEquals(CodedValues.TPC_CSA_ALTERA_PRESERVA_PRD, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema()); // Default=N
            final boolean concluiSomentePgtTotal = (sistemaReimplanta && !csaDefineReimplante && sistemaPreserva && !csaDefinePreservacao);
            final boolean removeContratosNuncaEnviados = ParamSist.getBoolParamSist(CodedValues.TPC_CONTRATOS_DEVEM_SER_VALIDADOS_PELA_CSE, AcessoSistema.getAcessoUsuarioSistema());

            String complemento = "";

            if ((orgCodigos != null) && (orgCodigos.size() > 0)) {
                complemento = " and cnv.org_codigo in (:orgCodigos)";
                    queryParams.addValue("orgCodigos", orgCodigos);
            }
            if ((estCodigos != null) && (estCodigos.size() > 0)) {
                complemento = " and org.est_codigo in (:estCodigos)";
                    queryParams.addValue("estCodigos", estCodigos);
            }

            // DESENV-19274 - Remove da exportação todas liquidações que a folha não conhece, pois nenhuma parcela teve o permissão do gestor.
            if(removeContratosNuncaEnviados) {
                removeContratosLiquidadosNuncaProcessadosSemPermissaoCse();
                complemento += " and NOT EXISTS (SELECT 1 FROM tb_tmp_remove_ade_nunca_enviados tmpRemov where ade.ade_codigo = tmpRemov.ade_codigo) ";
            }

            String fields = "";
            String tableJoin = "";
            String tmpTable1 = "";
            String tmpTable2 = "";
            String tmpTable1Dfn = "";
            String tmpTable2Dfn = "";

            if (consolidaMovFin) {
                tmpTable1 = "tb_tmp_exp_inicial";
                tmpTable2 = "tb_tmp_exp_mov_fin";
                tmpTable1Dfn = "rse_codigo, csa_codigo, cnv_cod_verba, oca_periodo, tipo";
                tmpTable2Dfn = "ade_codigo, oca_periodo, tipo";

                fields = "ade.rse_codigo, cnv.csa_codigo, cnv.cnv_cod_verba";
                tableJoin = "ade.rse_codigo = tmp.rse_codigo and cnv.csa_codigo = tmp.csa_codigo and cnv.cnv_cod_verba = tmp.cnv_cod_verba";
            } else {
                tmpTable1 = "tb_tmp_exp_mov_fin";
                tmpTable1Dfn = "ade_codigo, oca_periodo, tipo";
                fields = "ade.ade_codigo";
            }

            String query = "insert into " +
                                tmpTable1 +
                                " (" +
                                tmpTable1Dfn +
                                ")"
                                // TODAS AS OCORRENCIAS DO PERIODO
                                +
                                " select " +
                                fields +
                                ", oca.oca_periodo, case when toc_codigo in ('6','7') then 'E' when toc_codigo in ('15','19') then 'C' else 'A' end as tipo" +
                                " from tb_ocorrencia_autorizacao oca" +
                                " inner join tb_aut_desconto ade on (ade.ade_codigo = oca.ade_codigo)" +
                                " inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)" +
                                " inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)" +
                                " inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)" +
                                " inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo and pex.pex_sequencia = 0)" +
                                " where sad_codigo in ('" +
                                TextHelper.join(sadCodigos, "','") +
                                "')" +
                                " and ade.ade_int_folha in (" +
                                CodedValues.INTEGRA_FOLHA_SIM +
                                ", " +
                                CodedValues.INTEGRA_FOLHA_SOMENTE_EXCLUSAO +
                                ")" +
                                " and coalesce(ade.ade_exportacao, '" +
                                CodedValues.ADE_EXPORTACAO_PERMITIDA +
                                "') <> '" +
                                CodedValues.ADE_EXPORTACAO_BLOQUEADA +
                                "'" +
                                (!habilitaExtensaoPeriodo && !habilitaUsoOcaPeriodo ? " and oca.oca_data between pex_data_ini and pex_data_fim" : " and oca.oca_periodo = pex.pex_periodo") +
                                " and toc_codigo in ('" +
                                TextHelper.join(tocCodigos, "','") +
                                "')" +
                                " and (ade_ano_mes_ini < pex_periodo or ade_ano_mes_ini_ref < pex_periodo)" +
                                complemento +
                                " group by " +
                                fields +
                                ", oca.oca_periodo, toc_codigo" +
                                " union"
                                // MAIS TODAS AS INCLUSÕES DO PERIODO (JUNTO COM AS LIQUIDADAS/CANCELADAS/SUSPENSAS APÓS O CORTE)
                                +
                                " select " +
                                fields +
                                ", ade.ade_ano_mes_ini as oca_periodo, 'A' as tipo" +
                                " from tb_aut_desconto ade" +
                                " inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)" +
                                " inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)" +
                                " inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)" +
                                " inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo)" +
                                " left outer join tb_ocorrencia_autorizacao oca on (ade.ade_codigo = oca.ade_codigo and oca.toc_codigo in ('" +
                                CodedValues.TOC_TARIF_LIQUIDACAO +
                                "','" +
                                CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO +
                                "','" +
                                CodedValues.TOC_SUSPENSAO_CONTRATO +
                                "')" +
                                " and (oca.oca_data > pex.pex_data_fim OR oca.oca_periodo > pex.pex_periodo))" +
                                " where ade.ade_ano_mes_ini = pex_periodo" +
                                " and ade.ade_int_folha = " +
                                CodedValues.INTEGRA_FOLHA_SIM +
                                " and coalesce(ade.ade_exportacao, '" +
                                CodedValues.ADE_EXPORTACAO_PERMITIDA +
                                "') <> '" +
                                CodedValues.ADE_EXPORTACAO_BLOQUEADA +
                                "'" +
                                " and (ade.sad_codigo in ('" +
                                CodedValues.SAD_DEFERIDA +
                                "', '" +
                                CodedValues.SAD_EMANDAMENTO +
                                "', '" +
                                CodedValues.SAD_AGUARD_LIQUIDACAO +
                                "', '" +
                                CodedValues.SAD_AGUARD_LIQUI_COMPRA +
                                "'";
                  if(ParamSist.getBoolParamSist(CodedValues.TPC_ENVIA_CONTRATOS_CARENCIA_MOV_FIN, AcessoSistema.getAcessoUsuarioSistema())) {
                      query +=", '" + CodedValues.SAD_LIQUIDADA + "'," + "'" + CodedValues.SAD_CANCELADA+ "'";
                  }
                  query +=") OR"
                  + " (ade.sad_codigo in ('" + CodedValues.SAD_CANCELADA + "','" + CodedValues.SAD_LIQUIDADA + "','"
                  + CodedValues.SAD_SUSPENSA + "','" + CodedValues.SAD_SUSPENSA_CSE + "')"
                  + " and oca.oca_codigo is not null))"
                  + complemento
                  + " group by " + fields + ", ade.ade_ano_mes_ini"
                  ;

            // Cenários de conclusão para folha:
            // 1. O contrato já está concluído e possui ocorrência de conclusão no período (item anterior)
            // 2. O contrato ainda está aberto, não possui ocorrência e deve ser concluído em algum dos períodos agrupados (prazo = pagas + qtd_prd_em_processamento)
            // 3. O contrato ainda está aberto, não possui ocorrência e deve ser concluído no período atual visto terem outros períodos aguardando retorno (prazo = pagas)

            if ((permiteAgruparPeriodos || permiteMultiplosPeriodosAbertos) && enviaConclusaoFolha) {
                // Se permite agrupamento de períodos, inclui na tabela de contratos a serem exportados
                // os abertos que irão concluir em algum dos períodos do agrupamento
                // Caso o sistema faça reimplante com preservação de parcelas, verificar se a quantidade
                // de parcelas em processamento, somadas àquelas geradas na exportação atual, sendo integralmente
                // pagas, o total de pagas alcance o prazo.

                query += " union"
                       + " select " + fields + ", pex.pex_periodo as oca_periodo, 'C' as tipo"
                       + " from tb_aut_desconto ade"
                       + " inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)"
                       + " inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)"
                       + " inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)"
                       + " inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo)"
                       + " where ade.ade_ano_mes_fim is not null"
                       + " and ade.ade_int_folha in (" + CodedValues.INTEGRA_FOLHA_SIM + ", " + CodedValues.INTEGRA_FOLHA_SOMENTE_EXCLUSAO + ")"
                       + " and coalesce(ade.ade_exportacao, '" + CodedValues.ADE_EXPORTACAO_PERMITIDA + "') <> '" + CodedValues.ADE_EXPORTACAO_BLOQUEADA + "'"
                       + " and ade.sad_codigo in ('" + TextHelper.join(CodedValues.SAD_CODIGOS_ABERTOS_EXPORTACAO, "','") + "')"

                       + (quinzenal
                       ? (concluiSomentePgtTotal
                       ? " and coalesce(ade.ade_prazo, 99999) = coalesce(ade.ade_prd_pagas, 0) + ("
                       + " select count(*) from tb_parcela_desconto_periodo prd where ade.ade_codigo = prd.ade_codigo and prd.spd_codigo in ('" + CodedValues.SPD_EMPROCESSAMENTO + "','" + CodedValues.SPD_SEM_RETORNO + "','" + CodedValues.SPD_AGUARD_PROCESSAMENTO + "')"
                       + " and prd.prd_data_desconto >= (select min(pex2.pex_periodo) from tb_periodo_exportacao pex2 where pex.org_codigo = pex2.org_codigo))"
                       + " and pex.pex_periodo = ("
                       + " select cal2.periodo"
                       + " from tb_tmp_calendario_quinzenal cal1"
                       + " inner join tb_tmp_calendario_quinzenal cal2 on (cal2.org_codigo = cal1.org_codigo)"
                       + " where cal1.org_codigo = org.org_codigo and cal1.periodo = ("
                       + " select max(prd.prd_data_desconto) from tb_parcela_desconto_periodo prd where ade.ade_codigo = prd.ade_codigo and prd.spd_codigo in ('" + CodedValues.SPD_EMPROCESSAMENTO + "','" + CodedValues.SPD_SEM_RETORNO + "','" + CodedValues.SPD_AGUARD_PROCESSAMENTO + "'))"
                       + " and cal2.sequencia = cal1.sequencia + 1)"

                       : " and pex.pex_periodo = ("
                       + " select cal2.periodo"
                       + " from tb_tmp_calendario_quinzenal cal1"
                       + " inner join tb_tmp_calendario_quinzenal cal2 on (cal2.org_codigo = cal1.org_codigo)"
                       + " where cal1.org_codigo = org.org_codigo and cal1.periodo = ade.ade_ano_mes_fim and cal2.sequencia = cal1.sequencia + 1)"
                       )
                       : " and pex.pex_periodo = add_months(ade.ade_ano_mes_fim, 1)"
                       )

                       + complemento
                       + " group by " + fields + ", pex.pex_periodo";

                if (ParamSist.paramEquals(CodedValues.TPC_EXPORTAR_ADE_SOMENTE_DO_PERIODO_BASE, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
                    query += " union"
                            + " select " + fields + ", pex.pex_periodo as oca_periodo, 'C' as tipo"
                            + " from tb_aut_desconto ade "
                            + " inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
                            + " inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
                            + " inner join tb_orgao org on (org.org_codigo = cnv.org_codigo) "
                            + " inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) "
                            + " where ade.ade_ano_mes_fim is not null "
                            + " and ade.ade_int_folha in (" + CodedValues.INTEGRA_FOLHA_SIM + ", " + CodedValues.INTEGRA_FOLHA_SOMENTE_EXCLUSAO + ")"
                            + " and coalesce(ade.ade_exportacao, '" + CodedValues.ADE_EXPORTACAO_PERMITIDA + "') <> '" + CodedValues.ADE_EXPORTACAO_BLOQUEADA + "'"
                            + " and ade.sad_codigo in ('" + TextHelper.join(CodedValues.SAD_CODIGOS_ABERTOS_EXPORTACAO, "','") + "')"
                            + " and coalesce(ade.ade_prazo, 99999) = ade.ade_prd_pagas"
                            + " and EXISTS (select cfo_periodo "
                            + " from tb_calendario_folha_org cfo "
                            + " where cfo_periodo = pex.pex_periodo_ant "
                            + " and cfo.org_codigo = cnv.org_codigo "
                            + " and cfo_data_fim = cfo_data_ini  "
                            + " union "
                            + " select cfe_periodo "
                            + " from tb_calendario_folha_est cfe "
                            + " inner join tb_orgao org2 on (cfe.est_codigo = org2.est_codigo) "
                            + " left outer join tb_calendario_folha_org cfo on (cfe_periodo = cfo_periodo and org2.org_codigo = cfo.org_codigo) "
                            + " where cfe_periodo = pex.pex_periodo_ant "
                            + " and org2.org_codigo = cnv.org_codigo "
                            + " and cfo_periodo is null "
                            + " and cfe_data_fim = cfe_data_ini "
                            + " union "
                            + " select cfc_periodo "
                            + " from tb_calendario_folha_cse cfc "
                            + " inner join tb_estabelecimento est2 on (cfc.cse_codigo = est2.cse_codigo) "
                            + " inner join tb_orgao org2 on (est2.est_codigo = org2.est_codigo) "
                            + " left outer join tb_calendario_folha_est cfe on (cfc_periodo = cfe_periodo and est2.est_codigo = cfe.est_codigo) "
                            + " left outer join tb_calendario_folha_org cfo on (cfc_periodo = cfo_periodo and org2.org_codigo = cfo.org_codigo) "
                            + " where cfc_periodo = pex.pex_periodo_ant "
                            + " and org2.org_codigo = cnv.org_codigo "
                            + " and cfe_periodo is null "
                            + " and cfo_periodo is null "
                            + " and cfc_data_fim = cfc_data_ini)"
                            + complemento
                            + " group by " + fields + ", pex.pex_periodo";
                }
            }
            LOG.trace(query);
            int linhasAfetadas = jdbc.update(query, queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);

            if (consolidaMovFin) {
                query = "insert into " + tmpTable2 + " (" + tmpTable2Dfn + ")"
                      // CONTRATOS ABERTOS (JUNTO COM OS LIQUIDADOS/CANCELADOS/SUSPENSOS APÓS O CORTE)
                      + " select ade.ade_codigo, tmp.oca_periodo, 'A' as tipo"
                      + " from tb_aut_desconto ade"
                      + " inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)"
                      + " inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)"
                      + " inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo)"
                      + " inner join " + tmpTable1 + " tmp on (" + tableJoin + ")"
                      + " left outer join tb_ocorrencia_autorizacao oca on (ade.ade_codigo = oca.ade_codigo and oca.toc_codigo in ('"
                      + CodedValues.TOC_TARIF_LIQUIDACAO + "','"
                      + CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO + "','"
                      + CodedValues.TOC_SUSPENSAO_CONTRATO + "')"
                      + " and (oca.oca_data > pex.pex_data_fim OR oca.oca_periodo > pex.pex_periodo))"
                      + " where ade_ano_mes_ini <= pex_periodo"
                      + (concluiSomentePgtTotal
                      ? " and coalesce(ade.ade_prazo, 99999) > coalesce(ade.ade_prd_pagas, 0)"
                      : " and (ade_ano_mes_fim is null or ade_ano_mes_fim >= pex_periodo)"
                      )
                      + " and ade_int_folha = " + CodedValues.INTEGRA_FOLHA_SIM
                      + " and coalesce(ade.ade_exportacao, '" + CodedValues.ADE_EXPORTACAO_PERMITIDA + "') <> '" + CodedValues.ADE_EXPORTACAO_BLOQUEADA + "'"
                      + " and (sad_codigo in ('" + CodedValues.SAD_DEFERIDA + "', '" + CodedValues.SAD_EMANDAMENTO + "', '"
                      + CodedValues.SAD_AGUARD_LIQUIDACAO + "', '" + CodedValues.SAD_AGUARD_LIQUI_COMPRA + "') OR"
                      + " (ade.sad_codigo in ('" + CodedValues.SAD_CANCELADA + "', '" + CodedValues.SAD_LIQUIDADA + "','"
                      + CodedValues.SAD_SUSPENSA + "','" + CodedValues.SAD_SUSPENSA_CSE + "')"
                      + " and oca.oca_codigo is not null))"
                      + (!permiteAgruparPeriodos ? "" : " and tmp.oca_periodo = pex.pex_periodo")
                      + " group by ade.ade_codigo, tmp.oca_periodo"
                      + " union"
                      // LIQUIDAÇÕES/CANCELAMENTOS DO PERIODO
                      + " select ade.ade_codigo, tmp.oca_periodo, 'E' as tipo"
                      + " from tb_aut_desconto ade"
                      + " inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)"
                      + " inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)"
                      + " inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo)"
                      + " inner join " + tmpTable1 + " tmp on (" + tableJoin + ")"
                      + " inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade.ade_codigo)"
                      + (!habilitaExtensaoPeriodo && !habilitaUsoOcaPeriodo ? " where ade_ano_mes_ini < pex_periodo" : " where (ade_ano_mes_ini < pex_periodo or (ade_ano_mes_ini >= pex_periodo and ade_ano_mes_ini_ref < pex_periodo and coalesce(ade_paga, 'N') = 'S'))")
                      + (exportaLiqIndependenteAnoMesFim ? "" : " and (ade_ano_mes_fim is null or ade_ano_mes_fim >= add_months(pex_periodo, -1))")
                      + " and ((ade.sad_codigo in ('" + CodedValues.SAD_LIQUIDADA + "', '" + CodedValues.SAD_SUSPENSA + "', '" + CodedValues.SAD_SUSPENSA_CSE + "', '" + CodedValues.SAD_ESTOQUE_MENSAL + "')"
                      + " and oca.toc_codigo = '" + CodedValues.TOC_TARIF_LIQUIDACAO + "')"
                      + " or (ade.sad_codigo = '" + CodedValues.SAD_CANCELADA + "'"
                      + " and oca.toc_codigo = '" + CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO + "')"
                      + (!habilitaExtensaoPeriodo && !habilitaUsoOcaPeriodo ? ")" : ""
                      + " or (ade.sad_codigo in ('" + CodedValues.SAD_DEFERIDA + "', '" + CodedValues.SAD_EMANDAMENTO + "', '" + CodedValues.SAD_AGUARD_LIQUIDACAO + "', '" + CodedValues.SAD_AGUARD_LIQUI_COMPRA + "')"
                      + " and oca.toc_codigo = '" + CodedValues.TOC_TARIF_LIQUIDACAO + "'))")
                      + (!permiteAgruparPeriodos ? " and oca.oca_data between pex_data_ini and pex_data_fim" : " and oca.oca_periodo = pex.pex_periodo and tmp.oca_periodo = pex.pex_periodo")
                      + " and ade_int_folha in (" + CodedValues.INTEGRA_FOLHA_SIM + ", " + CodedValues.INTEGRA_FOLHA_SOMENTE_EXCLUSAO + ")"
                      + " and coalesce(ade.ade_exportacao, '" + CodedValues.ADE_EXPORTACAO_PERMITIDA + "') <> '" + CodedValues.ADE_EXPORTACAO_BLOQUEADA + "'"
                      + " group by ade.ade_codigo, tmp.oca_periodo"
                      + " union"
                      // CONCLUSÕES DO PERÍODO
                      + " select ade.ade_codigo, tmp.oca_periodo, 'C' as tipo"
                      + " from tb_aut_desconto ade"
                      + " inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)"
                      + " inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)"
                      + " inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo)"
                      + " inner join " + tmpTable1 + " tmp on (" + tableJoin + ")"
                      + " inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade.ade_codigo)"
                      + " where ade_ano_mes_ini < pex_periodo"
                      + " and ade.sad_codigo = '" + CodedValues.SAD_CONCLUIDO + "'"
                      + " and oca.toc_codigo in ('" + CodedValues.TOC_CONCLUSAO_CONTRATO + "','" + CodedValues.TOC_CONCLUSAO_SEM_DESCONTO + "')"
                      + (!permiteAgruparPeriodos ? " and oca.oca_data between pex_data_ini and pex_data_fim" : " and oca.oca_periodo = pex.pex_periodo and tmp.oca_periodo = pex.pex_periodo")
                      + " and ade_int_folha in (" + CodedValues.INTEGRA_FOLHA_SIM + ", " + CodedValues.INTEGRA_FOLHA_SOMENTE_EXCLUSAO + ")"
                      + " and coalesce(ade.ade_exportacao, '" + CodedValues.ADE_EXPORTACAO_PERMITIDA + "') <> '" + CodedValues.ADE_EXPORTACAO_BLOQUEADA + "'"
                      + " group by ade.ade_codigo, tmp.oca_periodo"
                      ;

                if ((permiteAgruparPeriodos || permiteMultiplosPeriodosAbertos) && enviaConclusaoFolha) {
                    // CONTRATOS ABERTOS PORÉM QUE IRÃO SER CONCLUÍDOS EM ALGUM DOS PERÍODOS AGRUPADOS
                    query += " union"
                           + " select ade.ade_codigo, tmp.oca_periodo, 'C' as tipo"
                           + " from tb_aut_desconto ade"
                           + " inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)"
                           + " inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)"
                           + " inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo)"
                           + " inner join " + tmpTable1 + " tmp on (" + tableJoin + ")"
                           + " where ade.ade_ano_mes_fim is not null"
                           + " and ade.ade_int_folha in (" + CodedValues.INTEGRA_FOLHA_SIM + ", " + CodedValues.INTEGRA_FOLHA_SOMENTE_EXCLUSAO + ")"
                           + " and coalesce(ade.ade_exportacao, '" + CodedValues.ADE_EXPORTACAO_PERMITIDA + "') <> '" + CodedValues.ADE_EXPORTACAO_BLOQUEADA + "'"
                           + " and ade.sad_codigo in ('" + TextHelper.join(CodedValues.SAD_CODIGOS_ABERTOS_EXPORTACAO, "','") + "')"
                           + " and tmp.oca_periodo = pex.pex_periodo"
                           + " and tmp.tipo = 'C'"

                           + (quinzenal
                           ? (concluiSomentePgtTotal
                           ? " and coalesce(ade.ade_prazo, 99999) = coalesce(ade.ade_prd_pagas, 0) + ("
                           + " select count(*) from tb_parcela_desconto_periodo prd where ade.ade_codigo = prd.ade_codigo and prd.spd_codigo in ('" + CodedValues.SPD_EMPROCESSAMENTO + "','" + CodedValues.SPD_SEM_RETORNO + "','" + CodedValues.SPD_AGUARD_PROCESSAMENTO + "')"
                           + " and prd.prd_data_desconto >= (select min(pex2.pex_periodo) from tb_periodo_exportacao pex2 where pex.org_codigo = pex2.org_codigo))"
                           + " and pex.pex_periodo = ("
                           + " select cal2.periodo"
                           + " from tb_tmp_calendario_quinzenal cal1"
                           + " inner join tb_tmp_calendario_quinzenal cal2 on (cal2.org_codigo = cal1.org_codigo)"
                           + " where cal1.org_codigo = cnv.org_codigo and cal1.periodo = ("
                           + " select max(prd.prd_data_desconto) from tb_parcela_desconto_periodo prd where ade.ade_codigo = prd.ade_codigo and prd.spd_codigo in ('" + CodedValues.SPD_EMPROCESSAMENTO + "','" + CodedValues.SPD_SEM_RETORNO + "','" + CodedValues.SPD_AGUARD_PROCESSAMENTO + "'))"
                           + " and cal2.sequencia = cal1.sequencia + 1)"

                           : " and pex.pex_periodo = ("
                           + " select cal2.periodo"
                           + " from tb_tmp_calendario_quinzenal cal1"
                           + " inner join tb_tmp_calendario_quinzenal cal2 on (cal2.org_codigo = cal1.org_codigo)"
                           + " where cal1.org_codigo = cnv.org_codigo and cal1.periodo = ade.ade_ano_mes_fim and cal2.sequencia = cal1.sequencia + 1)"
                           )
                           : " and pex.pex_periodo = add_months(ade.ade_ano_mes_fim, 1)"
                           )

                           + " group by ade.ade_codigo, tmp.oca_periodo";

                    if (ParamSist.paramEquals(CodedValues.TPC_EXPORTAR_ADE_SOMENTE_DO_PERIODO_BASE, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
                        query += " union"
                                + " select ade.ade_codigo, tmp.oca_periodo, 'C' as tipo "
                                + " from tb_aut_desconto ade "
                                + " inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
                                + " inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
                                + " inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) "
                                + " inner join " + tmpTable1 + " tmp on (" + tableJoin + ")"
                                + " where ade.ade_ano_mes_fim is not null "
                                + " and ade.ade_int_folha in (" + CodedValues.INTEGRA_FOLHA_SIM + ", " + CodedValues.INTEGRA_FOLHA_SOMENTE_EXCLUSAO + ")"
                                + " and coalesce(ade.ade_exportacao, '" + CodedValues.ADE_EXPORTACAO_PERMITIDA + "') <> '" + CodedValues.ADE_EXPORTACAO_BLOQUEADA + "'"
                                + " and ade.sad_codigo in ('" + TextHelper.join(CodedValues.SAD_CODIGOS_ABERTOS_EXPORTACAO, "','") + "')"
                                + " and tmp.oca_periodo = pex.pex_periodo "
                                + " and tmp.tipo = 'C' "
                                + " and coalesce(ade.ade_prazo, 99999) = ade.ade_prd_pagas"
                                + " and EXISTS (select cfo_periodo "
                                + " from tb_calendario_folha_org cfo "
                                + " where cfo_periodo = pex.pex_periodo_ant "
                                + " and cfo.org_codigo = cnv.org_codigo "
                                + " and cfo_data_fim = cfo_data_ini  "
                                + " union "
                                + " select cfe_periodo "
                                + " from tb_calendario_folha_est cfe "
                                + " inner join tb_orgao org2 on (cfe.est_codigo = org2.est_codigo) "
                                + " left outer join tb_calendario_folha_org cfo on (cfe_periodo = cfo_periodo and org2.org_codigo = cfo.org_codigo) "
                                + " where cfe_periodo = pex.pex_periodo_ant "
                                + " and org2.org_codigo = cnv.org_codigo "
                                + " and cfo_periodo is null "
                                + " and cfe_data_fim = cfe_data_ini "
                                + " union "
                                + " select cfc_periodo "
                                + " from tb_calendario_folha_cse cfc "
                                + " inner join tb_estabelecimento est2 on (cfc.cse_codigo = est2.cse_codigo) "
                                + " inner join tb_orgao org2 on (est2.est_codigo = org2.est_codigo) "
                                + " left outer join tb_calendario_folha_est cfe on (cfc_periodo = cfe_periodo and est2.est_codigo = cfe.est_codigo) "
                                + " left outer join tb_calendario_folha_org cfo on (cfc_periodo = cfo_periodo and org2.org_codigo = cfo.org_codigo) "
                                + " where cfc_periodo = pex.pex_periodo_ant "
                                + " and org2.org_codigo = cnv.org_codigo "
                                + " and cfe_periodo is null "
                                + " and cfo_periodo is null "
                                + " and cfc_data_fim = cfc_data_ini)"
                                + " group by ade.ade_codigo, tmp.oca_periodo";
                    }
                }
                LOG.trace(query);
                linhasAfetadas = jdbc.update(query, queryParams);
                LOG.trace("Linhas afetadas: " + linhasAfetadas);
            }
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Coloca em uma tabelas os dados sobre os contratos que devem ser
     * exportados para a folha.
     * @param orgCodigos : códigos de órgãos, nulo para todos
     * @param estCodigos : códigos de estabelecimentos, nulo para todos
     * @param verbas : códigos das verbas, nulo para todas
     * @param sadCodigos : status das autorizações que devem ser selecionadas
     * @param spdCodigos : status das parcelas que devem ser selecionadas
     * @param exportaMensal : true se a exportação é mensal
     * @throws DAOException
     */
    @Override
    public void selectExportacao(List<String> orgCodigos, List<String> estCodigos, List<String> verbas, List<String> sadCodigos, List<String> spdCodigos, boolean exportaMensal) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        try {
            final boolean consolidaMovFin = ParamSist.paramEquals(CodedValues.TPC_CONSOLIDA_DESCONTOS_MOVIMENTO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            final boolean naoEnviaAdeRseExcluido = ParamSist.paramEquals(CodedValues.TPC_ENVIA_CONTRATO_RSE_EXCLUIDO, CodedValues.TPC_NAO, AcessoSistema.getAcessoUsuarioSistema());
            final boolean permiteAgruparPeriodos = ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

            final StringBuilder complemento = new StringBuilder();
            int linhasAfetadas;

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
            if ((sadCodigos != null) && (sadCodigos.size() > 0)) {
                complemento.append(" and sad_codigo in ('").append(TextHelper.join(sadCodigos, "','")).append("') ");
            }
            if ((spdCodigos != null) && (spdCodigos.size() > 0)) {
                complemento.append(" and spd_codigo in ('").append(TextHelper.join(spdCodigos, "','")).append("') ");
            }

            String query = "insert into tb_tmp_exportacao (ser_codigo, ser_nome, ser_primeiro_nome, ser_nome_meio, ser_ultimo_nome, ser_nome_pai, ser_nome_mae, ser_cpf, ser_nacionalidade, rse_matricula, rse_matricula_inst, rse_tipo, rse_obs, rse_associado, pos_codigo, " +
                                "trs_codigo, org_identificador, est_identificador, csa_identificador, svc_identificador, svc_descricao, cnv_cod_verba, cnv_cod_verba_ref, periodo, " +
                                "competencia, data, pex_periodo, pex_periodo_ant, pex_periodo_pos, srs_codigo, org_cnpj, est_cnpj, csa_cnpj, rse_margem, rse_margem_rest, " +
                                "rse_margem_2, rse_margem_rest_2, rse_margem_3, rse_margem_rest_3, ade_numero, ade_identificador, ade_prazo, ade_prazo_exc, ade_prd_pagas, ade_prd_pagas_exc, ade_vlr, " +
                                "ade_tipo_vlr, ade_inc_margem, ade_ano_mes_ini, ade_vlr_folha, ade_prazo_folha, ade_ano_mes_ini_folha, ade_ano_mes_fim_folha, svc_prioridade, cnv_prioridade, " +
                                "ade_data_ref, ade_data_exclusao, ade_ano_mes_ini_ref, ade_ano_mes_fim_ref, ade_cod_reg, ade_ano_mes_fim, ade_data, prd_data_desconto, prd_numero, " +
                                "situacao, ade_indice, percentual_padrao, capital_devido, saldo_devedor, rse_codigo, org_codigo, est_codigo, svc_codigo, scv_codigo, csa_codigo, cnv_codigo, " +
                                "ade_codigo, sad_codigo, consolida, autoriza_pgt_parcial, oca_periodo, codigo_folha) " +
                                // dados do servidor
                                "select ser.ser_codigo, ser_nome, ser_primeiro_nome, ser_nome_meio, ser_ultimo_nome, ser_nome_pai, ser_nome_mae, ser_cpf, ser_nacionalidade, rse_matricula, rse_matricula_inst, rse_tipo, rse_obs, rse_associado, pos_codigo, trs_codigo, org_identificador, est_identificador, " +
                                // dados do convenio
                                "csa_identificador, svc_identificador, svc_descricao, cnv_cod_verba, " +
                                "coalesce(cnv_cod_verba_ref, cnv_cod_verba) as cnv_cod_verba_ref, " +
                                "to_char(PEX_PERIODO, 'yyyymm') as periodo, " +
                                "to_char(PEX_PERIODO_POS, 'yyyymm') as competencia, " +
                                "current_date as data, " +
                                "PEX_PERIODO, " +
                                "PEX_PERIODO_ANT, " +
                                "PEX_PERIODO_POS, " +
                                // dados do registro servidor
                                "srs_codigo, " +
                                // dados para empresa privada
                                "org_cnpj, est_cnpj, csa_cnpj,  " +
                                "rse_margem, rse_margem_rest,  " +
                                "rse_margem_2, rse_margem_rest_2,  " +
                                "rse_margem_3, rse_margem_rest_3,  " +
                                // dados da operação
                                "ade_numero, ade_identificador, ade_prazo, ade_prazo as ade_prazo_exc, ade_prd_pagas, ade_prd_pagas as ade_prd_pagas_exc, coalesce(prd.prd_vlr_previsto, ade.ade_vlr) AS ade_vlr, " +
                                "ade_tipo_vlr, ade_inc_margem, ade_ano_mes_ini, " +
                                // dados da folha
                                "ade_vlr_folha, ade_prazo_folha, ade_ano_mes_ini_folha, ade_ano_mes_fim_folha, " +
                                // dados de prioridade
                                "svc_prioridade, cnv_prioridade, " +
                                // dados da data de referencia
                                "ade_data_ref, ade_data_exclusao, ade_ano_mes_ini_ref, ade_ano_mes_fim_ref, ade_cod_reg, " +
                                "ade_ano_mes_fim, ade_data, prd_data_desconto, prd_numero, NULL as situacao, " +
                                // dados indice
                                "ade_indice, " +
                                // dados de percentual limite
                                "NULL as percentual_padrao, " +
                                // Valores financeiros
                                "(case when ade.ade_prazo is null then 1 else ade.ade_prazo - coalesce(ade.ade_prd_pagas, 0) end) * coalesce(ade.ade_vlr_parcela_folha, ade.ade_vlr) as capital_devido, NULL as saldo_devedor, " +
                                // dados internos
                                "rse.rse_codigo, org.org_codigo, est.est_codigo, svc.svc_codigo, scv_codigo, " +
                                "csa.csa_codigo, cnv.cnv_codigo, ade.ade_codigo, " +
                                "(case when not exists (select 1 from tb_ocorrencia_autorizacao oca where oca.ade_codigo = ade.ade_codigo and toc_codigo in ('6', '7', '84') and (oca.oca_data > pex.pex_data_fim OR oca.oca_periodo > pex.pex_periodo)) then ade.sad_codigo else '4' end) as sad_codigo, " +
                                "coalesce(cnv_consolida_descontos, psi_vlr) as consolida, " +
                                "cast(coalesce(pcs.pcs_vlr, '') as char(1)) as autoriza_pgt_parcial " +
                                (!exportaMensal ? ", tmp.oca_periodo, " : ", null as oca_periodo, ") +
                                "COALESCE(NULLIF(ORG_FOLHA, ''), COALESCE(NULLIF(EST_FOLHA, ''), NULLIF(CSE_FOLHA, ''))) as codigo_folha " +
                                "from tb_parcela_desconto_periodo prd " +
                                "inner join tb_aut_desconto ade on (ade.ade_codigo = prd.ade_codigo) " +
                                "inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) " +
                                "inner join tb_servidor ser on (rse.ser_codigo = ser.ser_codigo) " +
                                "inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) " +
                                "inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) " +
                                "inner join tb_orgao org on (cnv.org_codigo = org.org_codigo) " +
                                "inner join tb_estabelecimento est on (org.est_codigo = est.est_codigo) " +
                                "inner join tb_consignante cse on (cse.cse_codigo = est.cse_codigo) " +
                                "inner join tb_consignataria csa on (cnv.csa_codigo = csa.csa_codigo) " +
                                "inner join tb_servico svc on (cnv.svc_codigo = svc.svc_codigo) " +
                                (!exportaMensal ? "inner join tb_tmp_exp_mov_fin tmp on (tmp.ade_codigo = ade.ade_codigo) " : "") +
                                "inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) " +
                                "inner join tb_param_sist_consignante psi on (psi.cse_codigo = est.cse_codigo and psi.tpc_codigo = '" +
                                CodedValues.TPC_CONSOLIDA_DESCONTOS_MOVIMENTO +
                                "') " +
                                "left outer join tb_param_consignataria pcs on (csa.csa_codigo = pcs.csa_codigo and pcs.tpa_codigo = '" +
                                CodedValues.TPA_PERMITE_PAGAMENTO_PARCIAL +
                                "') " +
                                "where prd_data_desconto = pex_periodo " +
                                "and ade_int_folha = " +
                                CodedValues.INTEGRA_FOLHA_SIM +
                                " " +
                                (!exportaMensal && permiteAgruparPeriodos ? "and tmp.oca_periodo = pex_periodo " +
                                                                            (consolidaMovFin ? "and tmp.tipo = 'A' " : "")
                                        : "") +
                                (naoEnviaAdeRseExcluido ? "and srs_codigo NOT IN ('" +
                                                          TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '") +
                                                          "')"
                                        : "") +
                                complemento.toString();
            //DESENV-17960 - Essa lógica foi colocada aqui, pois quando o sistema é inicial ele utiliza a tb_tmp_exp_mov_fin que já tem essa regra bem definida, porém em movimento mensal é preciso ter essa
            // trava para este caso de uso onde não vai ser exportado contratos com anexo, somente se for contrato importo por papel suporte.
            if(!ParamSist.getBoolParamSist(CodedValues.TPC_EXPORTA_INCL_ALT_ADE_SEM_ANEXO_PERIODO, AcessoSistema.getAcessoUsuarioSistema()) && exportaMensal) {
                query += " and coalesce(ade.ade_exportacao, '" + CodedValues.ADE_EXPORTACAO_PERMITIDA + "') <> '" + CodedValues.ADE_EXPORTACAO_BLOQUEADA + "'";
            }

            LOG.trace(query);
            linhasAfetadas = jdbc.update(query, queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Coloca em uma tabelas os dados sobre os contratos que devem ser
     * exportados para a folha que estão em carência, dependendo do tpc_codigo 749
     * @param orgCodigos : códigos de órgãos, nulo para todos
     * @param estCodigos : códigos de estabelecimentos, nulo para todos
     * @param verbas : códigos das verbas, nulo para todas
     * @param sadCodigos : status das autorizações que devem ser selecionadas
     * @param exportaMensal : true se a exportação é mensal
     * @throws DAOException
     */
    @Override
    public void selectExportacaoFutura(List<String> orgCodigos, List<String> estCodigos, List<String> verbas, List<String> sadCodigos) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        try {
            final boolean naoEnviaAdeRseExcluido = ParamSist.paramEquals(CodedValues.TPC_ENVIA_CONTRATO_RSE_EXCLUIDO, CodedValues.TPC_NAO, AcessoSistema.getAcessoUsuarioSistema());
            final boolean permiteAgruparPeriodos = ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

            String query = "select 1 from tb_param_svc_consignante tpsc where tps_codigo = 16 and PSE_VLR >= (select tpsc2.psi_vlr from tb_param_sist_consignante tpsc2 where tpsc2.tpc_codigo = 480) " +
                                "union " +
                                "select 1 from tb_param_svc_consignataria tpsc3 where tps_codigo = 16 and PSC_VLR >= (select tpsc2.psi_vlr from tb_param_sist_consignante tpsc2 where tpsc2.tpc_codigo = 480) ";
            String complemento = "";

            if ((orgCodigos != null) && !orgCodigos.isEmpty()) {
                complemento += " and cnv.org_codigo in (:orgCodigos') ";
                queryParams.addValue("orgCodigos", orgCodigos);
            }
            if ((estCodigos != null) && !estCodigos.isEmpty()) {
                complemento += " and org.est_codigo in (:estCodigos) ";
                queryParams.addValue("estCodigos", estCodigos);
            }
            if ((verbas != null) && !verbas.isEmpty()) {
                complemento += " and cnv.cnv_cod_verba in (:verbas) ";
                queryParams.addValue("verbas", verbas);
            }
            if ((sadCodigos != null) && !sadCodigos.isEmpty()) {
                complemento += " and sad_codigo in (:sadCodigos) ";
                queryParams.addValue("sadCodigos", sadCodigos);
            }

            // verifica se existe algum serviço que tem carência superiro ao parâemtro de sistema 480
            if (!jdbc.queryForList(query.toString(), queryParams).isEmpty()) {
                throw new DAOException("mensagem.erro.carencia.maxima.superior.qtd.periodos.exportados", (AcessoSistema) null);
            }

            query = " insert into tb_tmp_exp_mov_fin (ade_codigo, oca_periodo, tipo) " +
                    " select oca.ade_codigo, oca.oca_periodo, 'I' " +
                    " from tb_ocorrencia_autorizacao oca " +
                    " inner join tb_aut_desconto ade on (ade.ade_codigo = oca.ade_codigo) " +
                    " inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) " +
                    " inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) " +
                    " inner join tb_orgao org on (org.org_codigo = cnv.org_codigo) " +
                    " inner join tb_estabelecimento est on (org.est_codigo = est.est_codigo)  " +
                    " inner join tb_consignante cse on (cse.cse_codigo = est.cse_codigo)  " +
                    " inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo and pex.pex_sequencia = 0) " +
                    " where sad_codigo in ('" + CodedValues.SAD_DEFERIDA + "') " +
                    " and ade.ade_int_folha in (" + CodedValues.INTEGRA_FOLHA_SIM + ", " + CodedValues.INTEGRA_FOLHA_SOMENTE_EXCLUSAO + ") " +
                    " and coalesce(ade.ade_exportacao, '" + CodedValues.ADE_EXPORTACAO_PERMITIDA + "') <> '" + CodedValues.ADE_EXPORTACAO_BLOQUEADA + "'" +
                    " and toc_codigo = '"+ CodedValues.TOC_TARIF_RESERVA +"' " +
                    " and oca.oca_periodo >= pex.pex_periodo " +
                    " and not exists (select 1 from tb_arquivo_movimento arm where arm.ADE_NUMERO = ade.ADE_NUMERO and arm.ORG_IDENTIFICADOR = org.ORG_IDENTIFICADOR and arm.PEX_PERIODO < PEX_PERIODO) " +
                    " and (ade_ano_mes_ini > pex_periodo or ade_ano_mes_ini_ref > pex_periodo) " +
                    " group by ade.ade_codigo, oca.oca_periodo " +
                    " union " +
                    " select oca.ade_codigo, oca.oca_periodo, case when toc_codigo in ('"+ CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO +"', '"+ CodedValues.TOC_TARIF_LIQUIDACAO +"') then 'E' else 'A' end as tipo " +
                    " from tb_ocorrencia_autorizacao oca " +
                    " inner join tb_aut_desconto ade on (ade.ade_codigo = oca.ade_codigo) " +
                    " inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) " +
                    " inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) " +
                    " inner join tb_orgao org on (org.org_codigo = cnv.org_codigo) " +
                    " inner join tb_estabelecimento est on (org.est_codigo = est.est_codigo) " +
                    " inner join tb_consignante cse on (cse.cse_codigo = est.cse_codigo) " +
                    " inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo and pex.pex_sequencia = 0) " +
                    " where sad_codigo in ('"+ CodedValues.SAD_DEFERIDA +"', '"+ CodedValues.SAD_CANCELADA +"', '"+ CodedValues.SAD_LIQUIDADA +"') " +
                    " and ade.ade_int_folha in (" + CodedValues.INTEGRA_FOLHA_SIM + ", " + CodedValues.INTEGRA_FOLHA_SOMENTE_EXCLUSAO + ") " +
                    " and coalesce(ade.ade_exportacao, '" + CodedValues.ADE_EXPORTACAO_PERMITIDA + "') <> '" + CodedValues.ADE_EXPORTACAO_BLOQUEADA + "'" +
                    " and oca.oca_data between pex_data_ini and pex_data_fim and oca.oca_periodo = pex.pex_periodo " +
                    " and exists (select 1 from tb_arquivo_movimento arm where arm.ADE_NUMERO = ade.ADE_NUMERO and arm.ORG_IDENTIFICADOR = org.ORG_IDENTIFICADOR and arm.PEX_PERIODO < pex.PEX_PERIODO) " +
                    " and toc_codigo in ('"+ CodedValues.TOC_TARIF_LIQUIDACAO +"', '"+ CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO +"', '"+ CodedValues.TOC_ALTERACAO_CONTRATO +"') " +
                    " and (ade_ano_mes_ini > pex_periodo) " +
                    complemento +
                    " group by ade.ade_codigo, oca.oca_periodo";


            LOG.trace(query);
            int linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);


            query ="insert into tb_tmp_exportacao (ser_codigo, ser_nome, ser_primeiro_nome, ser_nome_meio, ser_ultimo_nome , ser_nome_pai, ser_nome_mae, ser_cpf, ser_nacionalidade, rse_matricula, rse_matricula_inst, rse_tipo, rse_obs, rse_associado, pos_codigo, " +
                    "trs_codigo, org_identificador, est_identificador, csa_identificador, svc_identificador, svc_descricao, cnv_cod_verba, cnv_cod_verba_ref, periodo, " +
                    "competencia, data, pex_periodo, pex_periodo_ant, pex_periodo_pos, srs_codigo, org_cnpj, est_cnpj, csa_cnpj, rse_margem, rse_margem_rest, " +
                    "rse_margem_2, rse_margem_rest_2, rse_margem_3, rse_margem_rest_3, ade_numero, ade_identificador, ade_prazo, ade_prazo_exc, ade_prd_pagas, ade_prd_pagas_exc, ade_vlr, " +
                    "ade_tipo_vlr, ade_inc_margem, ade_ano_mes_ini, ade_vlr_folha, ade_prazo_folha, ade_ano_mes_ini_folha, ade_ano_mes_fim_folha, svc_prioridade, cnv_prioridade, " +
                    "ade_data_ref, ade_data_exclusao, ade_ano_mes_ini_ref, ade_ano_mes_fim_ref, ade_cod_reg, ade_ano_mes_fim, ade_data, prd_data_desconto, prd_numero, " +
                    "ade_indice, percentual_padrao, capital_devido, saldo_devedor, rse_codigo, org_codigo, est_codigo, svc_codigo, scv_codigo, csa_codigo, cnv_codigo, " +
                    "ade_codigo, sad_codigo, consolida, autoriza_pgt_parcial, oca_periodo, codigo_folha, situacao) " +
                    // dados do servidor
                    "select ser.ser_codigo, ser_nome, ser_primeiro_nome, ser_nome_meio, ser_ultimo_nome, ser_nome_pai, ser_nome_mae, ser_cpf, ser_nacionalidade, rse_matricula, rse_matricula_inst, rse_tipo, rse_obs, rse_associado, pos_codigo, trs_codigo, org_identificador, est_identificador, " +
                    // dados do convenio
                    "csa_identificador, svc_identificador, svc_descricao, cnv_cod_verba, " +
                    "coalesce(cnv_cod_verba_ref, cnv_cod_verba) as cnv_cod_verba_ref, " +
                    "date_format(ade_ano_mes_ini, '%Y%m') as periodo, " +
                    "date_format(PEX_PERIODO_POS, '%Y%m') as competencia, " +
                    "curdate() as data, " +
                    "PEX_PERIODO, " +
                    "PEX_PERIODO_ANT, " +
                    "PEX_PERIODO_POS, " +
                    // dados do registro servidor
                    "srs_codigo, " +
                    // dados para empresa privada
                    "org_cnpj, est_cnpj, csa_cnpj,  " +
                    "rse_margem, rse_margem_rest,  " +
                    "rse_margem_2, rse_margem_rest_2,  " +
                    "rse_margem_3, rse_margem_rest_3,  " +
                    // dados da operação
                    "ade_numero, ade_identificador, ade_prazo, ade_prazo as ade_prazo_exc, ade_prd_pagas, ade_prd_pagas as ade_prd_pagas_exc, ade.ade_vlr AS ade_vlr, " +
                    "ade_tipo_vlr, ade_inc_margem, ade_ano_mes_ini, " +
                    // dados da folha
                    "ade_vlr_folha, ade_prazo_folha, ade_ano_mes_ini_folha, ade_ano_mes_fim_folha, " +
                    // dados de prioridade
                    "svc_prioridade, cnv_prioridade, " +
                    // dados da data de referencia
                    "ade_data_ref, ade_data_exclusao, ade_ano_mes_ini_ref, ade_ano_mes_fim_ref, ade_cod_reg, " +
                    "ade_ano_mes_fim, ade_data, ade_ano_mes_ini, '1' as prd_numero, " +
                    // dados indice
                    "ade_indice, " +
                    // dados de percentual limite
                    "'' AS percentual_padrao, " +
                    // Valores financeiros
                    "(case when ade.ade_prazo is null then 1 else ade.ade_prazo - coalesce(ade.ade_prd_pagas, 0) end) * coalesce(ade.ade_vlr_parcela_folha, ade.ade_vlr) as capital_devido, NULL as saldo_devedor, " +
                    // dados internos
                    "rse.rse_codigo, org.org_codigo, est.est_codigo, svc.svc_codigo, scv_codigo, " +
                    "csa.csa_codigo, cnv.cnv_codigo, ade.ade_codigo, " +
                    "(case when not exists (select 1 from tb_ocorrencia_autorizacao oca where oca.ade_codigo = ade.ade_codigo and toc_codigo in ('6', '7', '84') and (oca.oca_data > pex.pex_data_fim OR oca.oca_periodo > pex.pex_periodo)) then ade.sad_codigo else '4' end) as sad_codigo, " +
                    "'N' as consolida, " +
                    "cast(coalesce(pcs.pcs_vlr, '') as char(1)) as autoriza_pgt_parcial, " +
                    "tmp.oca_periodo as oca_periodo, " +
                    "COALESCE(NULLIF(ORG_FOLHA, ''), COALESCE(NULLIF(EST_FOLHA, ''), NULLIF(CSE_FOLHA, ''))) as codigo_folha, " +
                    "coalesce(tmp.tipo, NULL) as situacao " +
                    "from tb_aut_desconto ade " +
                    "inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) " +
                    "inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) " +
                    "inner join tb_orgao org on (cnv.org_codigo = org.org_codigo) " +
                    "inner join tb_estabelecimento est on (org.est_codigo = est.est_codigo) " +
                    "inner join tb_consignante cse on (cse.cse_codigo = est.cse_codigo) " +
                    "inner join tb_consignataria csa on (cnv.csa_codigo = csa.csa_codigo) " +
                    "inner join tb_servico svc on (cnv.svc_codigo = svc.svc_codigo) " +
                    "inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) " +
                    "inner join tb_servidor ser on (rse.ser_codigo = ser.ser_codigo) " +
                    "inner join tb_tmp_exp_mov_fin tmp on (tmp.ade_codigo = ade.ade_codigo) " +
                    "inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) " +
                    "left outer join tb_param_consignataria pcs on (csa.csa_codigo = pcs.csa_codigo and pcs.tpa_codigo = '" + CodedValues.TPA_PERMITE_PAGAMENTO_PARCIAL + "') " +
                    "where ade.ADE_ANO_MES_INI > pex_periodo " +
                    "and ade_int_folha = " + CodedValues.INTEGRA_FOLHA_SIM + " " +
                    (permiteAgruparPeriodos ? "and tmp.oca_periodo = pex_periodo " : "") +
                    (naoEnviaAdeRseExcluido ? "and srs_codigo NOT IN ('" + TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '") + "')" : "") +
                    complemento;

            LOG.trace(query);
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);

            final List<String> sadCodigosEncerrados = new ArrayList<>();
            sadCodigosEncerrados.add(CodedValues.SAD_CANCELADA);
            sadCodigosEncerrados.add(CodedValues.SAD_LIQUIDADA);

            query="INSERT INTO tb_parcela_desconto_periodo" +
            " (ADE_CODIGO, PRD_NUMERO, SPD_CODIGO, PRD_DATA_DESCONTO, PRD_VLR_PREVISTO, MNE_CODIGO) " +
            " SELECT ade.ade_codigo, '1' + floor(pex.pex_sequencia / (case when ade.ade_periodicidade = 'M' then 2 else 1 end))," +
            " '"+ CodedValues.SPD_EMPROCESSAMENTO + "' AS SPD_CODIGO, pex.pex_periodo AS PRD_DATA_DESCONTO, coalesce(ade.ade_vlr_parcela_folha, ade.ade_vlr) AS PRD_VLR_PREVISTO, ade.mne_codigo" +
            " FROM tb_aut_desconto ade" +
            " INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo)" +
            " INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo)" +
            " INNER JOIN tb_orgao org ON (org.org_codigo = cnv.org_codigo)" +
            " INNER JOIN tb_periodo_exportacao pex ON (pex.org_codigo = cnv.org_codigo)" +
            " INNER JOIN tb_ocorrencia_autorizacao oca on (ade.ade_codigo = oca.ade_codigo)" +
            " LEFT OUTER JOIN tb_parcela_desconto_periodo prd ON (ade.ade_codigo = prd.ade_codigo AND prd.prd_data_desconto = pex.pex_periodo) " +
            " WHERE prd.ade_codigo IS NULL" +
            " AND ade.sad_codigo in ('" + TextHelper.join(sadCodigosEncerrados, "','") + "') " +
            " AND ade.ADE_CARENCIA > 0 and oca.toc_codigo in ('"+ CodedValues.TOC_TARIF_LIQUIDACAO +"', '"+ CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO +"') and oca.OCA_PERIODO = pex.pex_periodo" +
            "      AND EXISTS (" +
            "            select 1" +
            "            from tb_arquivo_movimento arm" +
            //RI é um tipo de inclusão específico para o sistema da GLOBO, portanto precisamos colocar ele na condição de Inclusão para ser identificado que já existe na exportação.
            "            where arm.ADE_NUMERO = ade.ADE_NUMERO and arm.ORG_IDENTIFICADOR = org.ORG_IDENTIFICADOR and arm.PEX_PERIODO < arm.ADE_ANO_MES_INI and arm.ADE_ANO_MES_INI = pex.PEX_PERIODO and ARM_SITUACAO in ('I','RI'))" ;

            if (!PeriodoHelper.folhaMensal(AcessoSistema.getAcessoUsuarioSistema())) {
                // Se for quinzenal e contrato mensal, exporta apenas as parcelas dos períodos da quinzena em que estes foram incluídos
                query+= " AND (ade.ade_periodicidade <> '" + CodedValues.PERIODICIDADE_FOLHA_MENSAL + "'" +
                " OR day(pex.pex_periodo) = day(ade.ade_ano_mes_ini))";
            }

            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Corrige contratos que foram exportados em carência na tb_tmp_exportacao
     * para não serem reexportados como Inclusões
     * @param orgCodigos : códigos de órgãos, nulo para todos
     * @param estCodigos : códigos de estabelecimentos, nulo para todos
     * @param verbas : códigos das verbas, nulo para todas
     * @param sadCodigos : status das autorizações que devem ser selecionadas
     * @param exportaMensal : true se a exportação é mensal
     * @throws DAOException
     */
    @Override
    public void corrigeContratosExportacaoEmCarencia(List<String> tocCodigos) throws DAOException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();



            /* Exclui contratos que tem comandos de inclusão no passado para o período corrente,
             * que possuam carência, enviados antes da data inicial,
             * sem ocorrencia de mudança de status no período corrente
             *  Para evitar reenvio de contratos que já foram anviados anteriormente
             */
            String query = " delete tte.* " +
                                " from tb_tmp_exportacao tte " +
                                " inner join tb_aut_desconto ade on (tte.ade_codigo = ade.ade_codigo) " +
                                " inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) " +
                                " inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) " +
                                " inner join tb_orgao org on (cnv.org_codigo = org.org_codigo) " +
                                " inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo)" +
                                " where ade.ade_carencia > 0 and " +
                                "      not exists (" +
                                "            select 1 " +
                                "            from tb_ocorrencia_autorizacao oca " +
                                "            where oca.ade_codigo = tte.ade_codigo and oca.toc_codigo in ('" +
                                TextHelper.join(tocCodigos, "','") +
                                "') and oca.OCA_PERIODO = tte.pex_periodo) " +
                                "      and exists (" +
                                "            select 1 " +
                                "            from tb_arquivo_movimento arm" +
                                "            where arm.ADE_NUMERO = ade.ADE_NUMERO and arm.ORG_IDENTIFICADOR = org.ORG_IDENTIFICADOR  and arm.PEX_PERIODO < arm.ADE_ANO_MES_INI and arm.ADE_ANO_MES_INI = pex.PEX_PERIODO and ARM_SITUACAO IN ('I','RI'))";


            LOG.trace(query);
            int linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);

            /* Atualizo contratos que tenham alterações/exclusões no período corrente mas que foram
             * enviados no passado antes da data inicial e que possuam carência
             */
            query = " update tb_tmp_exportacao tte " +
                    " inner join tb_aut_desconto ade on (tte.ade_codigo = ade.ade_codigo) " +
                    " inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) " +
                    " inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) " +
                    " inner join tb_orgao org on (cnv.org_codigo = org.org_codigo) " +
                    " inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo)" +
                    " inner join tb_ocorrencia_autorizacao oca on (ade.ade_codigo = oca.ade_codigo)" +
                    " set tte.situacao = case " +
                    "                      when toc_codigo in ('"+ CodedValues.TOC_TARIF_LIQUIDACAO +"', '"+ CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO +"') then 'E'" +
                    "                      else 'A'" +
                    "                    end" +
                    " where ade.ADE_CARENCIA > 0 and oca.toc_codigo in ('"+ CodedValues.TOC_TARIF_LIQUIDACAO +"', '"+ CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO + "', '"+ CodedValues.TOC_ALTERACAO_CONTRATO +"') and oca.OCA_PERIODO = tte.pex_periodo" +
                    "      and exists (" +
                    "            select 1" +
                    "            from tb_arquivo_movimento arm" +
                    "            where arm.ADE_NUMERO = ade.ADE_NUMERO and arm.ORG_IDENTIFICADOR = org.ORG_IDENTIFICADOR and arm.PEX_PERIODO < arm.ADE_ANO_MES_INI and arm.ADE_ANO_MES_INI = pex.PEX_PERIODO and ARM_SITUACAO = 'I')" ;


            LOG.trace(query);
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);

            final List<String> sadCodigosEncerrados = new ArrayList<>();
            sadCodigosEncerrados.add(CodedValues.SAD_CANCELADA);
            sadCodigosEncerrados.add(CodedValues.SAD_LIQUIDADA);

            query="DELETE prd.* FROM tb_parcela_desconto_periodo prd " +
                    " INNER JOIN tb_aut_desconto ade on (ade.ade_codigo = prd.ade_codigo)" +
                    " INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo)" +
                    " INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo)" +
                    " INNER JOIN tb_orgao org ON (org.org_codigo = cnv.org_codigo)" +
                    " INNER JOIN tb_periodo_exportacao pex ON (pex.org_codigo = cnv.org_codigo)" +
                    " INNER JOIN tb_ocorrencia_autorizacao oca on (ade.ade_codigo = oca.ade_codigo)" +
                    " WHERE ade.sad_codigo in ('" + TextHelper.join(sadCodigosEncerrados, "','") + "') " +
                    " AND ade.ADE_CARENCIA > 0 and oca.toc_codigo in ('"+ CodedValues.TOC_TARIF_LIQUIDACAO +"', '"+ CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO +"') and oca.OCA_PERIODO = pex.pex_periodo" +
                    "      AND EXISTS (" +
                    "            select 1" +
                    "            from tb_arquivo_movimento arm" +
                    "            where arm.ADE_NUMERO = ade.ADE_NUMERO and arm.ORG_IDENTIFICADOR = org.ORG_IDENTIFICADOR " +
                    " and arm.PEX_PERIODO < arm.ADE_ANO_MES_INI and arm.ADE_ANO_MES_INI = pex.PEX_PERIODO and ARM_SITUACAO IN ('I','RI'))" ;

            if (!PeriodoHelper.folhaMensal(AcessoSistema.getAcessoUsuarioSistema())) {
                // Se for quinzenal e contrato mensal, exporta apenas as parcelas dos períodos da quinzena em que estes foram incluídos
                query+= " AND (ade.ade_periodicidade <> '" + CodedValues.PERIODICIDADE_FOLHA_MENSAL + "'" +
                " OR day(pex.pex_periodo) = day(ade.ade_ano_mes_ini))";
            }

            LOG.trace(query);
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);


        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Adiciona na mesma tabela os contratos liquidados, cancelados ou concluidos
     * que devem ser enviados para a folha.
     * @param orgCodigos : códigos de órgãos, nulo para todos
     * @param estCodigos : códigos de estabelecimentos, nulo para todos
     * @param verbas : códigos das verbas, nulo para todas
     * @param sadCodigos : contratos que serão gerados na exportação inicial
     * @throws DAOException
     */
    @Override
    public void selectLiquidacaoExportacao(List<String> orgCodigos, List<String> estCodigos, List<String> verbas, List<String> sadCodigos) throws DAOException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
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

            final boolean permiteAgruparPeriodos = ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            final boolean habilitaExtensaoPeriodo = ParamSist.paramEquals(CodedValues.TPC_HABILITA_EXTENSAO_PERIODO_FOLHA_AJUSTES, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            final boolean suspendeDescontosBloqCompra = ParamSist.paramEquals(CodedValues.TPC_SUSPENDE_DESC_CSA_BLOQ_CONTROLE_COMPRA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            final boolean consolidaMovFin = ParamSist.paramEquals(CodedValues.TPC_CONSOLIDA_DESCONTOS_MOVIMENTO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            final boolean exportaLiqCancNaoPagas = ParamSist.paramEquals(CodedValues.TPC_EXPORTA_LIQCANC_NAO_PAGAS, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            final boolean enviaContratoRseExcluido = !ParamSist.paramEquals(CodedValues.TPC_ENVIA_CONTRATO_RSE_EXCLUIDO, CodedValues.TPC_NAO, AcessoSistema.getAcessoUsuarioSistema());
            final boolean exportaLiqIndependenteAnoMesFim = ParamSist.paramEquals(CodedValues.TPC_EXPORTA_LIQ_INDEPENDENTE_ANO_MES_FIM, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            final boolean exportaLiqIndependenteQtdPagas = !ParamSist.paramEquals(CodedValues.TPC_EXPORTA_LIQ_INDEPENDENTE_QTD_PAGAS, CodedValues.TPC_NAO, AcessoSistema.getAcessoUsuarioSistema());
            final boolean enviaConclusaoFolha = ParamSist.paramEquals(CodedValues.TPC_ENVIA_CONCLUSAO_FOLHA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            final Object paramCarenciaFolha = ParamSist.getInstance().getParam(CodedValues.TPC_CARENCIA_CONCLUSAO_FOLHA, AcessoSistema.getAcessoUsuarioSistema());
            int carenciaFolha = 0;
            if (!TextHelper.isNull(paramCarenciaFolha)) {
                carenciaFolha = Integer.parseInt(paramCarenciaFolha.toString());
            }

            LOG.debug("TPC_EXPORTA_LIQCANC_NAO_PAGAS...........: " + exportaLiqCancNaoPagas);
            LOG.debug("TPC_ENVIA_CONTRATO_RSE_EXCLUIDO.........: " + enviaContratoRseExcluido);
            LOG.debug("TPC_EXPORTA_LIQ_INDEPENDENTE_ANO_MES_FIM: " + exportaLiqIndependenteAnoMesFim);
            LOG.debug("TPC_EXPORTA_LIQ_INDEPENDENTE_QTD_PAGAS..: " + exportaLiqIndependenteQtdPagas);
            LOG.debug("TPC_ENVIA_CONCLUSAO_FOLHA...............: " + enviaConclusaoFolha);
            LOG.debug("TPC_CARENCIA_CONCLUSAO_FOLHA............: " + carenciaFolha);

            // Contratos liquidados, cancelados ou concluídos que ainda estão na folha.
            final String query = "insert into tb_tmp_exportacao (ser_codigo, ser_nome, ser_primeiro_nome, ser_nome_meio, ser_ultimo_nome, ser_nome_pai, ser_nome_mae, ser_cpf, ser_nacionalidade, rse_matricula, rse_matricula_inst, rse_tipo, rse_obs, rse_associado, pos_codigo, " +
                                "trs_codigo, org_identificador, est_identificador, csa_identificador, svc_identificador, svc_descricao, cnv_cod_verba, cnv_cod_verba_ref, periodo, " +
                                "competencia, data, pex_periodo, pex_periodo_ant, pex_periodo_pos, srs_codigo, org_cnpj, est_cnpj, csa_cnpj, rse_margem, rse_margem_rest, " +
                                "rse_margem_2, rse_margem_rest_2, rse_margem_3, rse_margem_rest_3, ade_numero, ade_identificador, ade_prazo, ade_prazo_exc, ade_prd_pagas, ade_prd_pagas_exc, ade_vlr, " +
                                "ade_tipo_vlr, ade_inc_margem, ade_ano_mes_ini, ade_vlr_folha, ade_prazo_folha, ade_ano_mes_ini_folha, ade_ano_mes_fim_folha, svc_prioridade, cnv_prioridade, " +
                                "ade_data_ref, ade_data_exclusao, ade_ano_mes_ini_ref, ade_ano_mes_fim_ref, ade_cod_reg, ade_ano_mes_fim, ade_data, prd_data_desconto, prd_numero, " +
                                "situacao, ade_indice, percentual_padrao, capital_devido, saldo_devedor, rse_codigo, org_codigo, est_codigo, svc_codigo, scv_codigo, csa_codigo, cnv_codigo, " +
                                "ade_codigo, sad_codigo, consolida, autoriza_pgt_parcial, oca_periodo, codigo_folha) " +
                                // dados do servidor
                                "select ser.ser_codigo, ser_nome, ser_primeiro_nome, ser_nome_meio, ser_ultimo_nome, ser_nome_pai, ser_nome_mae, ser_cpf, ser_nacionalidade, rse_matricula, rse_matricula_inst, rse_tipo, rse_obs, rse_associado, pos_codigo, trs_codigo, org_identificador, est_identificador, " +
                                // dados do convenio
                                "csa_identificador, svc_identificador, svc_descricao, cnv_cod_verba, " +
                                "coalesce(cnv_cod_verba_ref, cnv_cod_verba) as cnv_cod_verba_ref, " +
                                "to_char(PEX_PERIODO, 'yyyymm') as periodo, " +
                                "to_char(PEX_PERIODO_POS, 'yyyymm') as competencia, " +
                                "current_date as data, " +
                                "PEX_PERIODO, " +
                                "PEX_PERIODO_ANT, " +
                                "PEX_PERIODO_POS, " +
                                // dados do registro servidor
                                "srs_codigo, " +
                                // dados para empresa privada
                                "org_cnpj, est_cnpj, csa_cnpj,  " +
                                "rse_margem, rse_margem_rest,  " +
                                "rse_margem_2, rse_margem_rest_2,  " +
                                "rse_margem_3, rse_margem_rest_3,  " +
                                // dados da operação
                                "ade_numero, ade_identificador, 0 as ade_prazo, ade_prazo as ade_prazo_exc, 0 as ade_prd_pagas, ade_prd_pagas as ade_prd_pagas_exc, coalesce(ade.ade_vlr_parcela_folha, ade.ade_vlr), " +
                                "ade_tipo_vlr, ade_inc_margem, ade_ano_mes_ini, " +
                                // dados da folha
                                "ade_vlr_folha, ade_prazo_folha, ade_ano_mes_ini_folha, ade_ano_mes_fim_folha, " +
                                // dados de prioridade
                                "svc_prioridade, cnv_prioridade, " +
                                // dados da data de referencia
                                "ade_data_ref, ade_data_exclusao, ade_ano_mes_ini_ref, ade_ano_mes_fim_ref, ade_cod_reg, " +
                                "ade_ano_mes_fim, ade_data, PEX_PERIODO as prd_data_desconto, 0 as prd_numero, NULL as situacao, " +
                                // dados indice
                                "ade_indice, " +
                                // dados de percentual limite
                                "NULL as percentual_padrao, " +
                                // Valores financeiros
                                "0 as capital_devido, NULL as saldo_devedor, " +
                                // dados internos
                                "rse.rse_codigo, org.org_codigo, est.est_codigo, svc.svc_codigo, scv_codigo, " +
                                "csa.csa_codigo, cnv.cnv_codigo, ade.ade_codigo, " +
                                (permiteAgruparPeriodos || habilitaExtensaoPeriodo || suspendeDescontosBloqCompra ? "case when tmp.tipo = 'C' then '" +
                                                                                                                    CodedValues.SAD_CONCLUIDO +
                                                                                                                    "' when tmp.tipo = 'E' then '" +
                                                                                                                    CodedValues.SAD_LIQUIDADA +
                                                                                                                    "' else ade.sad_codigo end as sad_codigo, "
                                        : "ade.sad_codigo, ") +
                                "coalesce(cnv_consolida_descontos, '" +
                                (consolidaMovFin ? "S" : "N") +
                                "') as consolida, " +
                                "cast(coalesce(pcs.pcs_vlr, '') as char(1)) as autoriza_pgt_parcial, " +
                                "tmp.oca_periodo, " +
                                "COALESCE(NULLIF(ORG_FOLHA, ''), COALESCE(NULLIF(EST_FOLHA, ''), NULLIF(CSE_FOLHA, ''))) as codigo_folha " +
                                "from tb_aut_desconto ade " +
                                "inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) " +
                                "inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) " +
                                "inner join tb_orgao org on (cnv.org_codigo = org.org_codigo) " +
                                "inner join tb_estabelecimento est on (org.est_codigo = est.est_codigo) " +
                                "inner join tb_consignante cse on (cse.cse_codigo = est.cse_codigo) " +
                                "inner join tb_consignataria csa on (cnv.csa_codigo = csa.csa_codigo) " +
                                "inner join tb_servico svc on (cnv.svc_codigo = svc.svc_codigo) " +
                                "inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) " +
                                "inner join tb_servidor ser on (rse.ser_codigo = ser.ser_codigo) " +
                                "inner join tb_tmp_exp_mov_fin tmp on (tmp.ade_codigo = ade.ade_codigo) " +
                                "inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) " +
                                "left outer join tb_param_consignataria pcs on (csa.csa_codigo = pcs.csa_codigo and pcs.tpa_codigo = '" +
                                CodedValues.TPA_PERMITE_PAGAMENTO_PARCIAL +
                                "') " +
                                "where (sad_codigo in ('" +
                                TextHelper.join(sadCodigos, "','") +
                                "') " +
                                (permiteAgruparPeriodos || habilitaExtensaoPeriodo || suspendeDescontosBloqCompra ? "or (ade.sad_codigo in ('" +
                                                                                                                    TextHelper.join(CodedValues.SAD_CODIGOS_ABERTOS_EXPORTACAO, "','") +
                                                                                                                    "') and tmp.tipo in ('E','C'))) "
                                        : ") ") +
                                "and ade_int_folha in (" +
                                CodedValues.INTEGRA_FOLHA_SIM +
                                ", " +
                                CodedValues.INTEGRA_FOLHA_SOMENTE_EXCLUSAO +
                                ") " +
                                "and (ade_ano_mes_ini < pex_periodo or (ade_ano_mes_ini >= pex_periodo and ade_ano_mes_ini_ref < pex_periodo and coalesce(ade_paga, 'N') = 'S')) " +
                                (permiteAgruparPeriodos ? "and tmp.oca_periodo = pex_periodo " : "") +
                                (!enviaContratoRseExcluido ? "and srs_codigo NOT IN ('" +
                                                             TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '") +
                                                             "') "
                                        : "") +
                                (exportaLiqIndependenteAnoMesFim ? "" : "and (ade_ano_mes_fim is null or ade_ano_mes_fim >= add_months(pex_periodo, -1)) ") +
                                (exportaLiqIndependenteQtdPagas ? "" : "and (coalesce(ade_prazo, 999999999) > coalesce(ade_prd_pagas, 0)) ") +
                                (exportaLiqCancNaoPagas ? "" : "and ade_vlr_folha is not null ") +
                                (!enviaConclusaoFolha ? "and (coalesce(ade_prazo, 999999999) > coalesce(ade_prd_pagas, 0) " +
                                                        "or add_months(ade_ano_mes_fim, coalesce(ade_carencia_final, 0) + " +
                                                        carenciaFolha +
                                                        ") >= pex_periodo) "
                                        : "") +
                                complemento.toString();

            LOG.trace(query);
            final int linhasAfetadas = jdbc.update(query, queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    protected String geraQueryExportacao(boolean exportaMensal, List<TransferObject> tdaList, AcessoSistema responsavel) {
        final boolean quinzenal = !PeriodoHelper.folhaMensal(responsavel);
        final boolean enviaExclusoesMovMensal = ParamSist.paramEquals(CodedValues.TPC_ENVIA_EXCLUSOES_MOVIMENTO_MENSAL, CodedValues.TPC_SIM, responsavel);

        // Monta a query de exportação
        String query = null;

        if (exportaMensal) {
            query = "(SELECT " +
                    // dados do servidor
                    "ser_nome, ser_primeiro_nome, ser_nome_meio, ser_ultimo_nome, ser_nome_pai, ser_nome_mae, ser_cpf, ser_nacionalidade, rse_matricula, " +
                    "rse_matricula_inst, rse_tipo, to_char(rse_obs) as rse_obs, rse_associado, pos_codigo, trs_codigo, org_identificador, est_identificador, " +
                    "csa_identificador, svc_identificador, svc_descricao, cnv_cod_verba, cnv_cod_verba_ref, " +
                    "periodo, competencia, data, " +
                    // dados do registro servidor
                    "srs_codigo, " +
                    // dados para empresa privada
                    "org_cnpj, est_cnpj, csa_cnpj,  " +
                    "rse_margem, rse_margem_rest,  " +
                    "rse_margem_2, rse_margem_rest_2,  " +
                    "rse_margem_3, rse_margem_rest_3,  " +
                    // dados indice
                    "ade_indice, " +
                    // dados de percentual limite
                    "percentual_padrao, " +
                    // Valores financeiros do contrato
                    "capital_pago, capital_devido, saldo_devedor, " +
                    // dados da operação
                    "ade_ano_mes_ini as data_ini_contrato, " +
                    (enviaExclusoesMovMensal ? "case when sad_codigo in ('4','5','11','15') then ade_ano_mes_fim else PEX_PERIODO end" : "ade_ano_mes_fim") + " as data_fim_contrato, " +
                    (enviaExclusoesMovMensal ? "case when sad_codigo in ('4','5','11','15') then ade_prazo else 0 end" : "ade_prazo") + " as nro_parcelas, " +
                    (enviaExclusoesMovMensal ? "case when sad_codigo in ('4','5','11','15') then ade_prazo - coalesce(ade_prd_pagas, 0) else 0 end" : "ade_prazo - coalesce(ade_prd_pagas, 0)") + " as prazo_restante, " +
                    "ade_prazo_exc, " +
                    "coalesce(ade_prd_pagas, 0) as ade_prd_pagas, " +
                    "coalesce(ade_prd_pagas_exc, 0) as ade_prd_pagas_exc, " +
                    "prd_numero, " +
                    // dados da folha
                    "ade_vlr_folha, ade_prazo_folha, ade_ano_mes_ini_folha, ade_ano_mes_fim_folha, " +
                    // dados de prioridade
                    "svc_prioridade, cnv_prioridade, " +
                    "autoriza_pgt_parcial, " +
                    // dados da data de referencia
                    "ade_data_ref, ade_data_exclusao, ade_ano_mes_ini_ref, ade_ano_mes_fim_ref, ade_cod_reg," +
                    "ade_numero, ade_identificador, " + (enviaExclusoesMovMensal ? "case when sad_codigo in ('4','5','11','15') then ade_vlr else 0.00 end" : "ade_vlr") + " as valor_desconto, ade_vlr as valor_desconto_exc, ade_vlr_folha as valor_desconto_folha, " +
                    "ade_tipo_vlr, ade_inc_margem, " +
                    "PEX_PERIODO as ade_ano_mes_ini, " +
                    "PEX_PERIODO as ade_ano_mes_fim, " +
                    "PEX_PERIODO, " +
                    "PEX_PERIODO_ANT, " +
                    "PEX_PERIODO_POS, " +
                    "oca_periodo, " +
                    (enviaExclusoesMovMensal ? "case when sad_codigo in ('4','5','11','15') then 1 else 0 end" : "1") + " as ade_prazo, " +
                    "ade_data, " +
                    "prd_data_desconto as data_desconto, " +
                    "codigo_folha, ";

            query += gerarClausulaDadosAutorizacao(tdaList, false, false, false, true, false, null);

            query += (enviaExclusoesMovMensal ? "coalesce(situacao, case when sad_codigo in ('4','5','11','15') then 'I' else 'E' end) as situacao, " : "coalesce(situacao, 'I') as situacao, ") +
                     "cast(coalesce(svc_prioridade, '9999999') as number) as ord_svc_prioridade, " +
                     "cast(coalesce(cnv_prioridade, 9999999) as number) as ord_cnv_prioridade, " +
                     "coalesce(ade_ano_mes_ini_ref, ade_ano_mes_ini) as ord_ade_ano_mes_ini, " +
                     "coalesce(ade_data_ref, ade_data) as ord_ade_data_ref " +
                     " FROM tb_tmp_exportacao ";

            query += gerarClausulaDadosAutorizacao(tdaList, false, false, false, false, true, null);

            query +=
                    " WHERE consolida = 'N'" +
                    " <<CNV_COD_VERBA>><<EST_CODIGO>><<ORG_CODIGO>> " +
                    ") UNION ALL (" +
                    " SELECT " +
                    //ATENÇÃO: alguns campos estão usando função agregada min para que no group by sejam utilizada apenas os campos relevantes
                    "ser_nome, ser_primeiro_nome, ser_nome_meio, ser_ultimo_nome, ser_nome_pai, ser_nome_mae, ser_cpf, ser_nacionalidade, rse_matricula, rse_matricula_inst, rse_tipo, to_char(rse_obs) as rse_obs, rse_associado, pos_codigo, trs_codigo, org_identificador, est_identificador, " +
                    "csa_identificador, min(svc_identificador), min(svc_descricao), cnv_cod_verba, min(cnv_cod_verba_ref), " +
                    "min(periodo), min(competencia), min(data), " +
                    // dados do registro servidor
                    "srs_codigo, " +
                    // dados para empresa privada
                    "org_cnpj, est_cnpj, csa_cnpj,  " +
                    "rse_margem, rse_margem_rest,  " +
                    "rse_margem_2, rse_margem_rest_2,  " +
                    "rse_margem_3, rse_margem_rest_3,  " +
                    // dados indice
                    "min(ade_indice), " +
                    // dados de percentual limite
                    "min(percentual_padrao), " +
                    // Valores financeiros do contrato
                    "sum(capital_pago), sum(capital_devido), sum(saldo_devedor), " +
                    // dados da operação
                    "max(ade_ano_mes_ini) as data_ini_contrato, " +
                    (enviaExclusoesMovMensal ? "max(case sum(case coalesce(situacao, case when sad_codigo in ('4','5','11','15') then 'I' else 'E' end) when 'E' then 0 else 1 end) when 0 then PEX_PERIODO else ade_ano_mes_fim end)" : "max(ade_ano_mes_fim)") + " as data_fim_contrato, " +
                    (enviaExclusoesMovMensal ? "max(case sum(case coalesce(situacao, case when sad_codigo in ('4','5','11','15') then 'I' else 'E' end) when 'E' then 0 else 1 end) when 0 then 0 else ade_prazo end)" : "max(ade_prazo)") + " as nro_parcelas, " +
                    (enviaExclusoesMovMensal ? "case sum(case coalesce(situacao, case when sad_codigo in ('4','5','11','15') then 'I' else 'E' end) when 'E' then 0 else 1 end) when 0 then 0 else max(ade_prazo - coalesce(ade_prd_pagas, 0)) end" : "max(ade_prazo - coalesce(ade_prd_pagas, 0))") + " as prazo_restante, " +
                    "max(ade_prazo_exc), " +
                    "max(coalesce(ade_prd_pagas, 0)) as ade_prd_pagas, " +
                    "max(coalesce(ade_prd_pagas_exc, 0)) as ade_prd_pagas_exc, " +
                    "max(prd_numero), " +
                    // dados da folha
                    "max(ade_vlr_folha), max(ade_prazo_folha), max(ade_ano_mes_ini_folha), max(ade_ano_mes_fim_folha), " +
                    // dados de prioridade
                    "min(svc_prioridade), min(cnv_prioridade), " +
                    "min(autoriza_pgt_parcial), " +
                    // dados da data de referencia
                    "max(ade_data_ref), max(ade_data_exclusao), max(ade_ano_mes_ini_ref), max(ade_ano_mes_fim_ref), min(ade_cod_reg)," +
                    "max(ade_numero) as ade_numero, max(ade_identificador), " +
                    (enviaExclusoesMovMensal ? "sum(case when sad_codigo in ('4','5','11','15') then ade_vlr else 0.00 end)" : "sum(ade_vlr)") + " as valor_desconto, " +
                    "sum(ade_vlr) as valor_desconto_exc, sum(ade_vlr_folha) as valor_desconto_folha, " +
                    "min(ade_tipo_vlr), min(ade_inc_margem), " +
                    "PEX_PERIODO as ade_ano_mes_ini, " +
                    "PEX_PERIODO as ade_ano_mes_fim, " +
                    "PEX_PERIODO, " +
                    "PEX_PERIODO_ANT, " +
                    "PEX_PERIODO_POS, " +
                    "oca_periodo, " +
                    (enviaExclusoesMovMensal ? "case sum(case coalesce(situacao, case when sad_codigo in ('4','5','11','15') then 'I' else 'E' end) when 'E' then 0 else 1 end) when 0 then 0 else 1 end" : "1") + " as ade_prazo, " +
                    "max(ade_data) as ade_data, " +
                    "min(prd_data_desconto) as data_desconto, " +
                    "codigo_folha, ";

            query += gerarClausulaDadosAutorizacao(tdaList, false, false, false, true, false, null);

            query += (enviaExclusoesMovMensal ? "case sum(case coalesce(situacao, case when sad_codigo in ('4','5','11','15') then 'I' else 'E' end) when 'E' then 0 else 1 end) when 0 then 'E' else 'I' end as situacao, " : "coalesce(situacao, 'I') as situacao, ") +
                     "min(cast(coalesce(svc_prioridade, '9999999') as number)) as ord_svc_prioridade, " +
                     "min(cast(coalesce(cnv_prioridade, 9999999) as number)) as ord_cnv_prioridade, " +
                     "max(coalesce(ade_ano_mes_ini_ref, ade_ano_mes_ini)) as ord_ade_ano_mes_ini, " +
                     "max(coalesce(ade_data_ref, ade_data)) as ord_ade_data_ref " +
                     " FROM tb_tmp_exportacao ";

            query += gerarClausulaDadosAutorizacao(tdaList, false, false, false, false, true, null);

            query += " WHERE consolida = 'S'" +
                     " <<CNV_COD_VERBA>><<EST_CODIGO>><<ORG_CODIGO>> " +
                     " GROUP BY cnv_cod_verba, org_identificador, est_identificador, csa_identificador, org_cnpj, est_cnpj, csa_cnpj, " +
                     "ser_nome, ser_primeiro_nome, ser_nome_meio, ser_ultimo_nome, ser_nome_pai, ser_nome_mae, ser_cpf, ser_nacionalidade, rse_matricula, rse_matricula_inst, rse_tipo, to_char(rse_obs), rse_associado, pos_codigo, trs_codigo, srs_codigo, " +
                     "rse_margem, rse_margem_rest, rse_margem_2, rse_margem_rest_2, rse_margem_3, rse_margem_rest_3, " +
                     "PEX_PERIODO, PEX_PERIODO_ANT, PEX_PERIODO_POS, oca_periodo, codigo_folha, ";

            query += gerarClausulaDadosAutorizacao(tdaList, false, false, true, false, false, null);

            query += "situacao)";

        } else { // exportação inicial
            query = "(SELECT " +
                    // dados do servidor
                    "ser_nome, ser_primeiro_nome, ser_nome_meio, ser_ultimo_nome, ser_nome_pai, ser_nome_mae, ser_cpf, ser_nacionalidade, rse_matricula, rse_matricula_inst, rse_tipo, to_char(rse_obs) as rse_obs, rse_associado, pos_codigo, trs_codigo, org_identificador, est_identificador, " +
                    "csa_identificador, svc_identificador, svc_descricao, cnv_cod_verba, cnv_cod_verba_ref, " +
                    "periodo, competencia, data, " +
                    // dados do registro servidor
                    "srs_codigo, " +
                    // dados para empresa privada
                    "org_cnpj, est_cnpj, csa_cnpj,  " +
                    "rse_margem, rse_margem_rest,  " +
                    "rse_margem_2, rse_margem_rest_2,  " +
                    "rse_margem_3, rse_margem_rest_3,  " +
                    // dados indice
                    "ade_indice, " +
                    // dados de percentual limite
                    "percentual_padrao, " +
                    // Valores financeiros do contrato
                    "capital_pago, capital_devido, saldo_devedor, " +
                    // dados da operação
                    "ade_ano_mes_ini as data_ini_contrato, " +
                    "ade_ano_mes_fim as data_fim_contrato, " +
                    "ade_prazo as nro_parcelas, " +
                    "ade_prazo - coalesce(ade_prd_pagas, 0) as prazo_restante, " +
                    "ade_prazo_exc, " +
                    "coalesce(ade_prd_pagas, 0) as ade_prd_pagas, " +
                    "coalesce(ade_prd_pagas_exc, 0) as ade_prd_pagas_exc, " +
                    "prd_numero, " +
                    // dados da folha
                    "ade_vlr_folha, ade_prazo_folha, ade_ano_mes_ini_folha, ade_ano_mes_fim_folha, " +
                    // dados de prioridade
                    "svc_prioridade, cnv_prioridade, " +
                    "autoriza_pgt_parcial, " +
                    // dados da data de referencia
                    "ade_data_ref, ade_data_exclusao, ade_ano_mes_ini_ref, ade_ano_mes_fim_ref, ade_cod_reg," +
                    "ade_numero, ade_identificador, (case when sad_codigo in ('4','5','11','15') then ade_vlr else 0.00 end) as valor_desconto, " +
                    " (case when sad_codigo not in ('4','5','11','15') then ade_vlr else 0.00 end) as valor_desconto_exc, ade_vlr_folha as valor_desconto_folha," +
                    "ade_vlr, ade_tipo_vlr, ade_inc_margem, " +
                    "PEX_PERIODO as ade_ano_mes_ini, " +
                    "PEX_PERIODO, " +
                    "PEX_PERIODO_ANT, " +
                    "PEX_PERIODO_POS, " +
                    "oca_periodo, ";

            if (quinzenal) {
                query += "CASE WHEN sad_codigo in ('4','5','11','15') THEN " +
                         "COALESCE(( " +
                         "SELECT cal2.periodo FROM tb_tmp_calendario_quinzenal cal1 " +
                         "INNER JOIN tb_tmp_calendario_quinzenal cal2 ON (cal2.org_codigo = cal1.org_codigo) " +
                         "WHERE cal1.org_codigo = tb_tmp_exportacao.org_codigo and cal1.periodo = pex_periodo and cal2.sequencia = cal1.sequencia + (ade_prazo - coalesce(ade_prd_pagas, 0) - 1) " +
                         "), to_date('2999-12-31', 'yyyy-mm-dd')) " +
                         "ELSE NULL END as ade_ano_mes_fim, ";
            } else {
                query += "(case when sad_codigo in ('4','5','11','15') then coalesce(add_months(PEX_PERIODO, ade_prazo - coalesce(ade_prd_pagas, 0) - 1), to_date('2999-12-31', 'yyyy-mm-dd')) else null end) as ade_ano_mes_fim, ";
            }

            query += "(case when sad_codigo in ('4','5','11','15') then coalesce((case when ade_prazo > 99 then null else ade_prazo end) - coalesce(ade_prd_pagas, 0), 100) else null end) as ade_prazo, " +
                     "ade_data, " +
                     "prd_data_desconto as data_desconto, " +
                     "codigo_folha, ";

            query += gerarClausulaDadosAutorizacao(tdaList, false, false, false, true, false, null);

            query += "coalesce(situacao, case when sad_codigo in ('4','5','11','15') then (case when coalesce(ade_prd_pagas, 0) = 0 then 'I' else 'A' end) else 'E' end) as situacao, " +
                     "cast(coalesce(svc_prioridade, '9999999') as number) as ord_svc_prioridade, " +
                     "cast(coalesce(cnv_prioridade, 9999999) as number) as ord_cnv_prioridade, " +
                     "coalesce(ade_ano_mes_ini_ref, ade_ano_mes_ini) as ord_ade_ano_mes_ini, " +
                     "coalesce(ade_data_ref, ade_data) as ord_ade_data_ref " +
                     " FROM tb_tmp_exportacao ";

            query += gerarClausulaDadosAutorizacao(tdaList, false, false, false, false, true, null);

            query +=
                    " WHERE consolida = 'N'" +
                    " <<CNV_COD_VERBA>><<EST_CODIGO>><<ORG_CODIGO>> " +
                    ") UNION ALL (" +
                    " SELECT " +
                    // dados do servidor
                    "ser_nome, ser_primeiro_nome, ser_nome_meio, ser_ultimo_nome, ser_nome_pai, ser_nome_mae, ser_cpf, ser_nacionalidade, rse_matricula, rse_matricula_inst, rse_tipo, to_char(rse_obs) as rse_obs, rse_associado, pos_codigo, trs_codigo, org_identificador, est_identificador, " +
                    "csa_identificador, min(svc_identificador), min(svc_descricao), cnv_cod_verba, min(cnv_cod_verba_ref), " +
                    "min(periodo), min(competencia), min(data), " +
                    // dados do registro servidor
                    "srs_codigo, " +
                    // dados para empresa privada
                    "org_cnpj, est_cnpj, csa_cnpj,  " +
                    "rse_margem, rse_margem_rest,  " +
                    "rse_margem_2, rse_margem_rest_2,  " +
                    "rse_margem_3, rse_margem_rest_3,  " +
                    // dados indice
                    "min(ade_indice), " +
                    // dados de percentual limite
                    "min(percentual_padrao), " +
                    // Valores financeiros do contrato
                    "sum(capital_pago), sum(capital_devido), sum(saldo_devedor), " +
                    // dados da operação
                    "max(ade_ano_mes_ini) as data_ini_contrato, " +
                    "max(ade_ano_mes_fim) as data_fim_contrato, " +
                    "max(ade_prazo) as nro_parcelas, " +
                    "max(case when sad_codigo in ('4','5','11','15') then ade_prazo - coalesce(ade_prd_pagas, 0) else 0 end) as prazo_restante, " +
                    "max(ade_prazo_exc), " +
                    "max(coalesce(ade_prd_pagas, 0)) as ade_prd_pagas, " +
                    "max(coalesce(ade_prd_pagas_exc, 0)) as ade_prd_pagas_exc, " +
                    "max(prd_numero), " +
                    // dados da folha
                    "sum(ade_vlr_folha), max(ade_prazo_folha), min(ade_ano_mes_ini_folha), max(ade_ano_mes_fim_folha), " +
                    // dados de prioridade
                    "min(svc_prioridade), min(cnv_prioridade), " +
                    "min(autoriza_pgt_parcial), " +
                    // dados da data de referencia
                    "max(ade_data_ref), max(ade_data_exclusao), max(ade_ano_mes_ini_ref), max(ade_ano_mes_fim_ref), min(ade_cod_reg)," +
                    "max(ade_numero) as ade_numero, max(ade_identificador), sum(case when sad_codigo in ('4','5','11','15') then ade_vlr else 0.00 end) as valor_desconto, " +
                    "sum(case when sad_codigo not in ('4','5','11','15') then ade_vlr else 0.00 end) as valor_desconto_exc, sum(ade_vlr_folha) as valor_desconto_folha, " +
                    "min(ade_vlr), min(ade_tipo_vlr), min(ade_inc_margem), " +
                    "PEX_PERIODO as ade_ano_mes_ini, " +
                    "PEX_PERIODO, " +
                    "PEX_PERIODO_ANT, " +
                    "PEX_PERIODO_POS, " +
                    "oca_periodo, ";

            if (quinzenal) {
                query += "MAX( " +
                         "CASE WHEN sad_codigo in ('4','5','11','15') THEN " +
                         "COALESCE(( " +
                         "SELECT cal2.periodo FROM tb_tmp_calendario_quinzenal cal1 " +
                         "INNER JOIN tb_tmp_calendario_quinzenal cal2 ON (cal2.org_codigo = cal1.org_codigo) " +
                         "WHERE cal1.org_codigo = tb_tmp_exportacao.org_codigo and cal1.periodo = pex_periodo and cal2.sequencia = cal1.sequencia + (ade_prazo - coalesce(ade_prd_pagas, 0) - 1) " +
                         "), to_date('2999-12-31', 'yyyy-mm-dd')) " +
                         "ELSE NULL END " +
                         ") as ade_ano_mes_fim, ";
            } else {
                query += "max(case when sad_codigo in ('4','5','11','15') then coalesce(add_months(PEX_PERIODO, ade_prazo - coalesce(ade_prd_pagas, 0) - 1), to_date('2999-12-31', 'yyyy-mm-dd')) else NULL end) as ade_ano_mes_fim, ";
            }

            query += "max(case when sad_codigo in ('4','5','11','15') then coalesce(case when ade_prazo > 99 then null else ade_prazo end - coalesce(ade_prd_pagas, 0), 100) else 0 end) as ade_prazo, " +
                     "max(ade_data) as ade_data, " +
                     "min(prd_data_desconto) as data_desconto, " +
                     "codigo_folha, ";

            query += gerarClausulaDadosAutorizacao(tdaList, false, false, false, true, false, null);

            query += "coalesce(situacao, " +
                     "case when (min(ade_ano_mes_ini) = min(pex_periodo) and sum(coalesce(ade_prd_pagas, 0)) = 0 and sum(case when sad_codigo in ('4','5','11','15') then 0 else 1 end) = 0) then 'I' " +
                     "     else case when sum(case when sad_codigo in ('4','5','11','15') then 1 else 0 end) = 0 then 'E' " +
                     "     else 'A' end " +
                     "end) as situacao, " +
                     "min(cast(coalesce(svc_prioridade, '9999999') as number)) as ord_svc_prioridade, " +
                     "min(cast(coalesce(cnv_prioridade, 9999999) as number)) as ord_cnv_prioridade, " +
                     "max(coalesce(ade_ano_mes_ini_ref, ade_ano_mes_ini)) as ord_ade_ano_mes_ini, " +
                     "max(coalesce(ade_data_ref, ade_data)) as ord_ade_data_ref " +
                     " FROM tb_tmp_exportacao ";

            query += gerarClausulaDadosAutorizacao(tdaList, false, false, false, false, true, null);

            query += " WHERE consolida = 'S'" +
                     " <<CNV_COD_VERBA>><<EST_CODIGO>><<ORG_CODIGO>> " +
                     " GROUP BY cnv_cod_verba, org_identificador, est_identificador, csa_identificador, org_cnpj, est_cnpj, csa_cnpj, " +
                     "ser_nome, ser_primeiro_nome, ser_nome_meio, ser_ultimo_nome, ser_nome_pai, ser_nome_mae, ser_cpf, ser_nacionalidade, rse_matricula, rse_matricula_inst, rse_tipo, to_char(rse_obs), rse_associado, pos_codigo, trs_codigo, srs_codigo, " +
                     "rse_margem, rse_margem_rest, rse_margem_2, rse_margem_rest_2, rse_margem_3, rse_margem_rest_3, " +
                     "PEX_PERIODO, PEX_PERIODO_ANT, PEX_PERIODO_POS, oca_periodo, codigo_folha, ";

            query += gerarClausulaDadosAutorizacao(tdaList, false, false, true, false, false, null);

            query += "situacao)";

            final boolean folhaNaoAceitaAlteracao = ParamSist.paramEquals(CodedValues.TPC_FOLHA_ACEITA_ALTERACAO, CodedValues.TPC_NAO, responsavel);
            if (folhaNaoAceitaAlteracao) {
                // Se a folha não aceita comandos de alteração de contrato, então para os
                // contratos alterados devemos mandar um comando de liquidação e outro de
                // inclusão. Inserimos então no resultado da query mais uma linha para
                // os contratos alterados, com a operação igual a "I", então podemos
                // mapear a alteração para a liquidação.
                query += " UNION ALL (" +
                         " SELECT " +
                         // dados do servidor
                         "ser_nome, ser_primeiro_nome, ser_nome_meio, ser_ultimo_nome, ser_nome_pai, ser_nome_mae, ser_cpf, ser_nacionalidade, rse_matricula, rse_matricula_inst, rse_tipo, to_char(rse_obs) as rse_obs, rse_associado, pos_codigo, trs_codigo, org_identificador, est_identificador, " +
                         "csa_identificador, svc_identificador, svc_descricao, cnv_cod_verba, cnv_cod_verba_ref, " +
                         "periodo, competencia, data, " +
                         // dados do registro servidor
                         "srs_codigo, " +
                         // dados para empresa privada
                         "org_cnpj, est_cnpj, csa_cnpj,  " +
                         "rse_margem, rse_margem_rest,  " +
                         "rse_margem_2, rse_margem_rest_2,  " +
                         "rse_margem_3, rse_margem_rest_3,  " +
                         // dados indice
                         "ade_indice, " +
                         // dados de percentual limite
                         "percentual_padrao, " +
                         // Valores financeiros do contrato
                         "capital_pago, capital_devido, saldo_devedor, " +
                         // dados da operação
                         "prd_data_desconto as data_ini_contrato, " +
                         "ade_ano_mes_fim as data_fim_contrato, " +
                         "ade_prazo as nro_parcelas, " +
                         "ade_prazo - coalesce(ade_prd_pagas, 0) as prazo_restante, " +
                         "ade_prazo_exc, " +
                         "coalesce(ade_prd_pagas, 0) as ade_prd_pagas, " +
                         "coalesce(ade_prd_pagas_exc, 0) as ade_prd_pagas_exc, " +
                         "prd_numero, " +
                         // dados da folha
                         "ade_vlr_folha, ade_prazo_folha, ade_ano_mes_ini_folha, ade_ano_mes_fim_folha, " +
                         // dados de prioridade
                         "svc_prioridade, cnv_prioridade, " +
                         "autoriza_pgt_parcial, " +
                         // dados da data de referencia
                         "ade_data_ref, ade_data_exclusao, ade_ano_mes_ini_ref, ade_ano_mes_fim_ref, ade_cod_reg," +
                         "ade_numero, ade_identificador, ade_vlr as valor_desconto, 0.00 as valor_desconto_exc, ade_vlr_folha as valor_desconto_folha, " +
                         "ade_vlr, ade_tipo_vlr, ade_inc_margem, " +
                         "PEX_PERIODO as ade_ano_mes_ini, " +
                         "PEX_PERIODO, " +
                         "PEX_PERIODO_ANT, " +
                         "PEX_PERIODO_POS, " +
                         "oca_periodo, ";

                if (quinzenal) {
                    query += "COALESCE(( " +
                             "SELECT cal2.periodo FROM tb_tmp_calendario_quinzenal cal1 " +
                             "INNER JOIN tb_tmp_calendario_quinzenal cal2 ON (cal2.org_codigo = cal1.org_codigo) " +
                             "WHERE cal1.org_codigo = tb_tmp_exportacao.org_codigo and cal1.periodo = pex_periodo and cal2.sequencia = cal1.sequencia + (ade_prazo - coalesce(ade_prd_pagas, 0) - 1) " +
                             "), to_date('2999-12-31', 'yyyy-mm-dd')) as ade_ano_mes_fim, ";
                } else {
                    query += "coalesce(add_months(PEX_PERIODO, ade_prazo - coalesce(ade_prd_pagas, 0) - 1), to_date('2999-12-31', 'yyyy-mm-dd')) as ade_ano_mes_fim, ";
                }

                query += "coalesce((case when ade_prazo > 99 then null else ade_prazo end) - coalesce(ade_prd_pagas, 0), 100) as ade_prazo, " +
                         "ade_data, " +
                         "prd_data_desconto as data_desconto, " +
                         "codigo_folha, ";

                query += gerarClausulaDadosAutorizacao(tdaList, false, false, false, true, false, null);

                query += "coalesce(situacao, 'I') as situacao, " +
                         "cast(coalesce(svc_prioridade, '9999999') as number) as ord_svc_prioridade, " +
                         "cast(coalesce(cnv_prioridade, 9999999) as number) as ord_cnv_prioridade, " +
                         "coalesce(ade_ano_mes_ini_ref, ade_ano_mes_ini) as ord_ade_ano_mes_ini, " +
                         "coalesce(ade_data_ref, ade_data) as ord_ade_data_ref " +
                         " FROM tb_tmp_exportacao ";

                query += gerarClausulaDadosAutorizacao(tdaList, false, false, false, false, true, null);

                query += " WHERE consolida = 'N' AND ((sad_codigo in ('4','5','11','15') AND coalesce(ade_prd_pagas, 0) > 0) OR coalesce(situacao, 'I') = 'A') " +
                         " <<CNV_COD_VERBA>><<EST_CODIGO>><<ORG_CODIGO>>)";

                // Tratamento para exportação de movimento financeiro consolidado
                query +=
                        " UNION ALL (" +
                        " SELECT " +
                        // dados do servidor
                        "ser_nome, ser_primeiro_nome, ser_nome_meio, ser_ultimo_nome, ser_nome_pai, ser_nome_mae, ser_cpf, ser_nacionalidade, rse_matricula, rse_matricula_inst, rse_tipo, to_char(rse_obs) as rse_obs, rse_associado, pos_codigo, trs_codigo, org_identificador, est_identificador, " +
                        "csa_identificador, min(svc_identificador), min(svc_descricao), cnv_cod_verba, min(cnv_cod_verba_ref), " +
                        "min(periodo), min(competencia), min(data), " +
                        // dados do registro servidor
                        "srs_codigo, " +
                        // dados para empresa privada
                        "org_cnpj, est_cnpj, csa_cnpj,  " +
                        "rse_margem, rse_margem_rest,  " +
                        "rse_margem_2, rse_margem_rest_2,  " +
                        "rse_margem_3, rse_margem_rest_3,  " +
                        // dados indice
                        "min(ade_indice), " +
                        // dados de percentual limite
                        "min(percentual_padrao), " +
                        // Valores financeiros do contrato
                        "sum(capital_pago), sum(capital_devido), sum(saldo_devedor), " +
                        // dados da operação
                        "max(ade_ano_mes_ini) as data_ini_contrato, " +
                        "max(ade_ano_mes_fim) as data_fim_contrato, " +
                        "max(ade_prazo) as nro_parcelas, " +
                        "max(case when sad_codigo in ('4','5','11','15') then ade_prazo - coalesce(ade_prd_pagas, 0) else 0 end) as prazo_restante, " +
                        "max(ade_prazo_exc), " +
                        "max(coalesce(ade_prd_pagas, 0)) as ade_prd_pagas, " +
                        "max(coalesce(ade_prd_pagas_exc, 0)) as ade_prd_pagas_exc, " +
                        "max(prd_numero), " +
                        // dados da folha
                        "sum(ade_vlr_folha), max(ade_prazo_folha), min(ade_ano_mes_ini_folha), max(ade_ano_mes_fim_folha), " +
                        // dados de prioridade
                        "min(svc_prioridade), min(cnv_prioridade), " +
                        "min(autoriza_pgt_parcial), " +
                        // dados da data de referencia
                        "max(ade_data_ref), max(ade_data_exclusao), max(ade_ano_mes_ini_ref), max(ade_ano_mes_fim_ref), min(ade_cod_reg)," +
                        "max(ade_numero) as ade_numero, max(ade_identificador), sum(ade_vlr) as valor_desconto, " +
                        "sum(0.00) as valor_desconto_exc, sum(ade_vlr_folha) as valor_desconto_folha, " +
                        "min(ade_vlr), min(ade_tipo_vlr), min(ade_inc_margem), " +
                        "PEX_PERIODO as ade_ano_mes_ini, " +
                        "PEX_PERIODO, " +
                        "PEX_PERIODO_ANT, " +
                        "PEX_PERIODO_POS, " +
                        "oca_periodo, ";

                if (quinzenal) {
                    query += "MAX( " +
                             "COALESCE(( " +
                             "SELECT cal2.periodo FROM tb_tmp_calendario_quinzenal cal1 " +
                             "INNER JOIN tb_tmp_calendario_quinzenal cal2 ON (cal2.org_codigo = cal1.org_codigo) " +
                             "WHERE cal1.org_codigo = tb_tmp_exportacao.org_codigo and cal1.periodo = pex_periodo and cal2.sequencia = cal1.sequencia + (ade_prazo - coalesce(ade_prd_pagas, 0) - 1) " +
                             "), to_date('2999-12-31', 'yyyy-mm-dd')) " +
                             ") as ade_ano_mes_fim, ";
                } else {
                    query += "max(coalesce(add_months(PEX_PERIODO, ade_prazo - coalesce(ade_prd_pagas, 0) - 1), to_date('2999-12-31', 'yyyy-mm-dd'))) as ade_ano_mes_fim, ";
                }

                query += "max(coalesce(case when ade_prazo > 99 then null else ade_prazo end - coalesce(ade_prd_pagas, 0), 100)) as ade_prazo, " +
                         "max(ade_data) as ade_data, " +
                         "min(prd_data_desconto) as data_desconto, " +
                         "codigo_folha, ";

                query += gerarClausulaDadosAutorizacao(tdaList, false, false, false, true, false, null);

                query += "coalesce(situacao, 'I') as situacao, " +
                         "min(cast(coalesce(svc_prioridade, '9999999') as number)) as ord_svc_prioridade, " +
                         "min(cast(coalesce(cnv_prioridade, 9999999) as number)) as ord_cnv_prioridade, " +
                         "max(coalesce(ade_ano_mes_ini_ref, ade_ano_mes_ini)) as ord_ade_ano_mes_ini, " +
                         "max(coalesce(ade_data_ref, ade_data)) as ord_ade_data_ref " +
                         " FROM tb_tmp_exportacao ";

                query += gerarClausulaDadosAutorizacao(tdaList, false, false, false, false, true, null);

                query += " WHERE consolida = 'S' AND ((sad_codigo in ('4','5','11','15') AND coalesce(ade_prd_pagas, 0) > 0) OR coalesce(situacao, 'I') = 'A') " +
                         " <<CNV_COD_VERBA>><<EST_CODIGO>><<ORG_CODIGO>> " +
                         " GROUP BY cnv_cod_verba, org_identificador, est_identificador, csa_identificador, org_cnpj, est_cnpj, csa_cnpj, " +
                         "ser_nome, ser_primeiro_nome, ser_nome_meio, ser_ultimo_nome, ser_nome_pai, ser_nome_mae, ser_cpf, ser_nacionalidade, rse_matricula, rse_matricula_inst, rse_tipo, to_char(rse_obs), rse_associado, pos_codigo, trs_codigo, srs_codigo, " +
                         "rse_margem, rse_margem_rest, rse_margem_2, rse_margem_rest_2, rse_margem_3, rse_margem_rest_3, " +
                         "PEX_PERIODO, PEX_PERIODO_ANT, PEX_PERIODO_POS, oca_periodo, codigo_folha, ";

                query += gerarClausulaDadosAutorizacao(tdaList, false, false, true, false, false, null);

                query += "situacao)";
            }
        }

        return query;
    }

    /**
     * Utiliza as classes do parser para executar a geração do arquivo
     * de exportação.
     * @param sql : query a ser passada ao leitor de base de dados
     * @param nomeArqSaida : nome do arquivo txt que contém o resultado
     * @param nomeArqConfEntrada : nome do arquivo de configuração da entrada
     * @param nomeArqConfTradutor : nome do arquivo de configuração do tradutor
     * @param nomeArqConfSaida : nome do arquivo de configuração da saída
     * @param tdaList : Códigos dos dados de autorização que serão incluídos na tabela
     * @param adeNumeros : Números de ADE para filtrar a geração do arquivo
     * @param exportador : Classe específica de exportação
     * @param parametrosExportacao : Parâmetros passados na rotina de exportação
     * @param queryParams : Parâmetros para execução da query de exportação
     * @throws DAOException
     */
    @Override
    protected void gravaArquivoExportacao(String sql, String nomeArqSaida,
            String nomeArqConfEntrada,
            String nomeArqConfTradutor,
            String nomeArqConfSaida,
            List<TransferObject> tdaList,
            List<String> adeNumeros,
            ExportaMovimento exportador,
            ParametrosExportacao parametrosExportacao,
            MapSqlParameterSource queryParams) throws DAOException {

        final boolean permitePriorizarServico = ParamSist.paramEquals(CodedValues.TPC_PERMITE_PRIORIZAR_SERVICO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        final boolean permitePriorizarVerba = ParamSist.paramEquals(CodedValues.TPC_PERMITE_PRIORIZAR_VERBA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

        final EscritorArquivoTexto escritor = new EscritorArquivoTexto(nomeArqConfSaida, nomeArqSaida);
        final HeaderTipo header = escritor.getConfig().getHeader();
        final HeaderTipo footer = escritor.getConfig().getFooter();

        // Pega o parametro para ordenação dos contratos do xml de configuração
        // da saida. Dá a preferência para o do header.
        String filtro = null;
        if (header != null) {
            filtro = header.getFiltro();
        }
        if ((filtro == null) && (footer != null)) {
            filtro = footer.getFiltro();
        }

        final StringBuilder queryOrdenada = new StringBuilder(sql);
        queryOrdenada.append(" ORDER BY ");
        if ((filtro != null) && !filtro.startsWith("$")) {
            final List<String> camposFiltro = validarCamposTabelaExportacao(filtro);
            if (camposFiltro != null && !camposFiltro.isEmpty()) {
                queryOrdenada.append(TextHelper.join(camposFiltro, ",")).append(MySqlDAOFactory.SEPARADOR);
            }
        }
        final String ordem = escritor.getOrdem();
        if (ordem != null) {
            final List<String> camposOrdem = validarCamposTabelaExportacao(ordem);
            if (camposOrdem != null && !camposOrdem.isEmpty()) {
                queryOrdenada.append(TextHelper.join(camposOrdem, ",")).append(MySqlDAOFactory.SEPARADOR);
            }
        }
        if (permitePriorizarServico) {
            queryOrdenada.append("ord_svc_prioridade").append(MySqlDAOFactory.SEPARADOR);
        }
        if (permitePriorizarVerba) {
            queryOrdenada.append("ord_cnv_prioridade").append(MySqlDAOFactory.SEPARADOR);
        }
        queryOrdenada.append("ord_ade_ano_mes_ini").append(MySqlDAOFactory.SEPARADOR);
        queryOrdenada.append("ord_ade_data_ref").append(MySqlDAOFactory.SEPARADOR);
        queryOrdenada.append(Columns.getColumnName(Columns.ADE_NUMERO));

        try {
            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

            // Como configuramos o innodb_autoinc_lock_mode com o valor 2 por performance, precisamos ajustar o número de linhas , pois o contador pode acontecer
            // dele não ser sequencial e para validação do movimento este número é importante para identificação da linha no arquivo.
            final StringBuilder query = new StringBuilder();
            query.append("SELECT count(contador) FROM tb_tmp_exportacao_ordenada");
            final int rows = Optional.ofNullable(jdbc.queryForObject(query.toString(), queryParams, Integer.class)).orElse(0);
            query.setLength(0);

            // Necessário apagar os dados da tabela, pois em exportação separada por EST/ORG ou VERBA,
            // a tabela é populada múltiplas vezes para cada separação do arquivo
            query.append("delete from tb_tmp_exportacao_ordenada");
            LOG.trace(query.toString());
            int linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);

            query.setLength(0);
            query.append("INSERT INTO tb_tmp_exportacao_ordenada (contador, num_linha, ");
            query.append("ser_nome, ser_primeiro_nome, ser_nome_meio, ser_ultimo_nome, ser_nome_pai, ser_nome_mae, ser_cpf, ser_nacionalidade, ");
            query.append("rse_matricula, rse_matricula_inst, rse_tipo, rse_obs, rse_associado, pos_codigo, trs_codigo, srs_codigo, ");
            query.append("rse_margem, rse_margem_rest, rse_margem_2, rse_margem_rest_2, rse_margem_3, rse_margem_rest_3, ");
            query.append("org_identificador, est_identificador, csa_identificador, svc_identificador, svc_descricao, ");
            query.append("org_cnpj, est_cnpj, csa_cnpj, cnv_cod_verba, cnv_cod_verba_ref, svc_prioridade, cnv_prioridade, autoriza_pgt_parcial, ");
            query.append("periodo, competencia, data, pex_periodo, pex_periodo_ant, pex_periodo_pos, oca_periodo, data_desconto, ");
            query.append("ade_data_ref, ade_data_exclusao, ade_data, data_ini_contrato, data_fim_contrato, ");
            query.append("ade_ano_mes_ini, ade_ano_mes_fim, ade_ano_mes_ini_ref, ade_ano_mes_fim_ref, ade_ano_mes_ini_folha, ade_ano_mes_fim_folha, ");
            query.append("ade_indice, ade_tipo_vlr, ade_inc_margem, ade_cod_reg, percentual_padrao, capital_pago, capital_devido, saldo_devedor, ");
            query.append("prd_numero, ade_numero, ade_identificador, nro_parcelas, prazo_restante, ade_prazo, ade_prazo_folha, ade_prazo_exc, ade_prd_pagas, ade_prd_pagas_exc, ");
            query.append("ade_vlr_folha, valor_desconto, valor_desconto_exc, valor_desconto_folha, codigo_folha, ");
            query.append(gerarClausulaDadosAutorizacao(tdaList, true, false, false, false, false, null));
            query.append("situacao, ord_svc_prioridade, ord_cnv_prioridade, ord_ade_ano_mes_ini, ord_ade_data_ref) ");

            query.append("SELECT ROWNUM, ROWNUM + :rows, ");
            query.append("ser_nome, ser_primeiro_nome, ser_nome_meio, ser_ultimo_nome, ser_nome_pai, ser_nome_mae, ser_cpf, ser_nacionalidade, rse_matricula, rse_matricula_inst, ");
            query.append("rse_tipo, rse_obs, rse_associado, pos_codigo, trs_codigo, srs_codigo, ");
            query.append("TO_CHAR(rse_margem,   'fm99999999990.00'), TO_CHAR(rse_margem_rest,   'fm99999999990.00'), ");
            query.append("TO_CHAR(rse_margem_2, 'fm99999999990.00'), TO_CHAR(rse_margem_rest_2, 'fm99999999990.00'), ");
            query.append("TO_CHAR(rse_margem_3, 'fm99999999990.00'), TO_CHAR(rse_margem_rest_3, 'fm99999999990.00'), ");

            query.append("org_identificador, est_identificador, csa_identificador, svc_identificador, svc_descricao,  ");
            query.append("org_cnpj, est_cnpj, csa_cnpj, ");
            query.append("cnv_cod_verba, cnv_cod_verba_ref, svc_prioridade, cnv_prioridade, autoriza_pgt_parcial, ");

            query.append("periodo, competencia, data, pex_periodo, pex_periodo_ant, pex_periodo_pos, oca_periodo, data_desconto, ");
            query.append("ade_data_ref, ade_data_exclusao, ade_data, ");
            query.append("data_ini_contrato, data_fim_contrato, ");
            query.append("ade_ano_mes_ini, ade_ano_mes_fim, ");
            query.append("ade_ano_mes_ini_ref, ade_ano_mes_fim_ref,  ");
            query.append("ade_ano_mes_ini_folha, ade_ano_mes_fim_folha, ");

            query.append("ade_indice, ade_tipo_vlr, ade_inc_margem, ade_cod_reg, percentual_padrao,  ");
            query.append("TO_CHAR(capital_pago, 'fm99999999990.00'), ");
            query.append("TO_CHAR(capital_devido, 'fm99999999990.00'), ");
            query.append("TO_CHAR(saldo_devedor, 'fm99999999990.00'), ");
            query.append("prd_numero, ade_numero, ade_identificador, ");
            query.append("nro_parcelas, prazo_restante, ade_prazo, ade_prazo_folha, ade_prazo_exc, ade_prd_pagas, ade_prd_pagas_exc,  ");
            query.append("TO_CHAR(ade_vlr_folha, 'fm99999999990.00'), ");
            query.append("TO_CHAR(valor_desconto, 'fm99999999990.00'), ");
            query.append("TO_CHAR(valor_desconto_exc, 'fm99999999990.00'), ");
            query.append("TO_CHAR(valor_desconto_folha, 'fm99999999990.00'), ");
            query.append("codigo_folha, ");

            query.append(gerarClausulaDadosAutorizacao(tdaList, true, false, false, false, false, null));

            query.append("situacao, ord_svc_prioridade, ord_cnv_prioridade, ord_ade_ano_mes_ini, ord_ade_data_ref ");
            query.append("FROM (").append(queryOrdenada).append(")");
            LOG.trace(query.toString());
            queryParams.addValue("rows", rows);
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }

        // Se tem limite de valor máximo para envio de desconto à folha, atualiza a tabela de exportação para reduzir os valores
        // que estiverem acima do máximo permitido. Os contratos devem ser alterados para o valor que estiver informado no parâmetro de
        // serviço TPS_VALOR_MAX_ENVIO_PARA_DESCONTO_FOLHA.
        atualizaAdeVlrServicoLimiteMaxDescontoFolha();

        // Se a folha não aceita alteração, o comando de alteração é subdividido em exclusão e inclusão, e no comando de exclusão
        // devemos enviar o valor antigo do índice, e não o valor atual, caso o índice possa ser alterado
        atualizaAdeIndiceQuandoFolhaNaoAceitaAlteracao();

        // Quando o sistema permite o servidor optar por desconto parcial é necessário verificar então a opção dele para que seja ajustado
        // o valor da coluna autoriza_pgt_parcial, pois ele pode mudar de decisão e a decisão dele sobrepõe o parâmetro da consignatária de optar por
        // desconto parcial. Então, somente deve permitir desconto parcial se o parâmetro da consignatária for sim e o servidor optou pelo desconto.
        if (ParamSist.paramEquals(CodedValues.TPC_SERVIDOR_AUTORIZA_DESCONTO_PARCIAL, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            atualizaAutorizaPgtParcial();
        }

        // Grava os dados da tb_tmp_exportacao_ordenada na tb_arquivo_movimento
        gravarTabelaExportacao();

        // Chama pré geração do arquivo de movimento
        if (exportador != null) {
            try {
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.exportador.preGeraArqLote.inicio.data.arg0", (AcessoSistema) null, DateHelper.getSystemDatetime().toString()));
                exportador.preGeraArqLote(parametrosExportacao, parametrosExportacao.getResponsavel());
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.exportador.preGeraArqLote.fim.data.arg0", (AcessoSistema) null, DateHelper.getSystemDatetime().toString()));
            } catch (final ExportaMovimentoException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
            }
        }

        // Gera o arquivo através do tradutor
        traduzirArquivoExportacao(nomeArqConfEntrada, nomeArqConfTradutor, escritor, adeNumeros);
    }

    @Override
    public void atualizaAdeVlrServicoPercentual() throws DAOException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final StringBuilder query = new StringBuilder();
            query.append("update tb_tmp_exportacao tmp ");
            query.append("set tmp.ade_vlr = coalesce((");
            query.append("  select ade.ade_vlr_percentual");
            query.append("  from tb_aut_desconto ade");
            query.append("  inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo)");
            query.append("  inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo)");
            query.append("  inner join tb_param_svc_consignante pse on (cnv.svc_codigo = pse.svc_codigo");
            query.append("    and pse.tps_codigo = '").append(CodedValues.TPS_RETEM_MARGEM_SVC_PERCENTUAL).append("') ");
            query.append("  where tmp.ade_codigo = ade.ade_codigo");
            query.append("    and pse.pse_vlr = '1'");
            query.append("    and ade.ade_vlr_percentual is not null");
            query.append("), tmp.ade_vlr)");
            LOG.trace(query.toString());
            final int linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public void atualizaVlrCapitalPago() throws DAOException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final StringBuilder query = new StringBuilder();
            query.append("CALL dropTableIfExists('tb_tmp_capital_pago_historico')");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CALL createTemporaryTable('tb_tmp_capital_pago_historico (ADE_CODIGO VARCHAR2(32), VALOR NUMBER(13,2), PRIMARY KEY (ADE_CODIGO))')");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("INSERT INTO tb_tmp_capital_pago_historico (ADE_CODIGO, VALOR) ");
            query.append("SELECT prd.ADE_CODIGO, SUM(prd.PRD_VLR_REALIZADO) ");
            query.append("FROM tb_parcela_desconto prd ");
            query.append("WHERE prd.SPD_CODIGO IN ('").append(CodedValues.SPD_LIQUIDADAFOLHA).append("','").append(CodedValues.SPD_LIQUIDADAMANUAL).append("') ");
            query.append("  AND EXISTS (SELECT 1 FROM tb_tmp_exportacao tmp WHERE prd.ADE_CODIGO = tmp.ADE_CODIGO) ");
            query.append("GROUP BY prd.ADE_CODIGO ");
            LOG.trace(query);
            int linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);

            query.setLength(0);
            query.append("CALL dropTableIfExists('tb_tmp_capital_pago_periodo')");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CALL createTemporaryTable('tb_tmp_capital_pago_periodo (ADE_CODIGO VARCHAR2(32), VALOR NUMBER(13,2), PRIMARY KEY (ADE_CODIGO))')");
            LOG.trace(query);
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("INSERT INTO tb_tmp_capital_pago_periodo (ADE_CODIGO, VALOR) ");
            query.append("SELECT prd.ADE_CODIGO, SUM(COALESCE(prd.PRD_VLR_REALIZADO, prd.PRD_VLR_PREVISTO)) ");
            query.append("FROM tb_parcela_desconto_periodo prd ");
            query.append("WHERE prd.SPD_CODIGO IN ('").append(CodedValues.SPD_EMPROCESSAMENTO).append("','").append(CodedValues.SPD_LIQUIDADAFOLHA).append("','").append(CodedValues.SPD_LIQUIDADAMANUAL).append("','").append(CodedValues.SPD_SEM_RETORNO).append("') ");
            query.append("  AND EXISTS (SELECT 1 FROM tb_tmp_exportacao tmp WHERE prd.ADE_CODIGO = tmp.ADE_CODIGO) ");
            query.append("GROUP BY prd.ADE_CODIGO ");
            LOG.trace(query);
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);

            query.setLength(0);
            query.append("UPDATE tb_tmp_exportacao exp ");
            query.append("SET exp.capital_pago = ");
            query.append("COALESCE((SELECT tmp1.valor FROM tb_tmp_capital_pago_historico tmp1 WHERE exp.ADE_CODIGO = tmp1.ADE_CODIGO), 0) + ");
            query.append("COALESCE((SELECT tmp2.valor FROM tb_tmp_capital_pago_periodo tmp2 WHERE exp.ADE_CODIGO = tmp2.ADE_CODIGO), 0) ");
            LOG.trace(query);
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public void limparTabelaExportacao(List<String> orgCodigos, List<String> estCodigos, List<String> verbas, AcessoSistema responsavel) throws DAOException {
        final Object paramQtdPeriodosTbExportacao = ParamSist.getInstance().getParam(CodedValues.TPC_QTD_PERIODOS_MANTIDOS_NA_TABELA_MOVIMENTO, responsavel);
        final int qtdPeriodos = (!TextHelper.isNull(paramQtdPeriodosTbExportacao) ? Integer.parseInt(paramQtdPeriodosTbExportacao.toString()) : 0);
        if (qtdPeriodos > 0) {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final MapSqlParameterSource queryParams = new MapSqlParameterSource();
            try {

                String query = "delete from tb_arquivo_movimento where 1 = 1";

                if ((orgCodigos != null) && (orgCodigos.size() > 0)) {
                    query += " and org_identificador in (select org_identificador from tb_orgao where org_codigo in (:orgCodigos)) ";
                    queryParams.addValue("orgCodigos", orgCodigos);
                }
                if ((estCodigos != null) && (estCodigos.size() > 0)) {
                    query += " and est_identificador in (select est_identificador from tb_estabelecimento where est_codigo in (:estCodigos)) ";
                    queryParams.addValue("estCodigos", estCodigos);
                }
                if ((verbas != null) && (verbas.size() > 0)) {
                    query += " and cnv_cod_verba in (:verbas) ";
                    queryParams.addValue("verbas", verbas);
                }
                if (!PeriodoHelper.folhaMensal(responsavel)) {
                    query += " and ((select count(distinct hie.hie_periodo) from tb_historico_exportacao hie inner join tb_orgao org on (hie.org_codigo = org.org_codigo) where org.org_identificador = tb_arquivo_movimento.org_identificador and hie.hie_data_ini <> hie.hie_data_fim and hie.hie_periodo > tb_arquivo_movimento.pex_periodo) >= " + qtdPeriodos
                           + "   or pex_periodo in (select pex.pex_periodo from tb_periodo_exportacao pex inner join tb_orgao org on (pex.org_codigo = org.org_codigo) where org.org_identificador = tb_arquivo_movimento.org_identificador)) ";
                } else {
                    query += " and (pex_periodo <= ((select min(pex.pex_periodo) from tb_periodo_exportacao pex inner join tb_orgao org on (pex.org_codigo = org.org_codigo) where org.org_identificador = tb_arquivo_movimento.org_identificador) - interval '" + qtdPeriodos + "' month) "
                           + "   or pex_periodo in (select pex.pex_periodo from tb_periodo_exportacao pex inner join tb_orgao org on (pex.org_codigo = org.org_codigo) where org.org_identificador = tb_arquivo_movimento.org_identificador)) ";
                }

                LOG.trace(query);
                final int linhasAfetadas = jdbc.update(query, queryParams);
                LOG.trace("Linhas afetadas: " + linhasAfetadas);

            } catch (final DataAccessException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    @Override
    public void removeInclusaoAlteracaoSemAnexo() throws DAOException {
        final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final StringBuilder query = new StringBuilder();

            final Object paramValidaVlrEOuPrzAlteracaoSemAnexo = ParamSist.getInstance().getParam(CodedValues.TPC_VALIDA_VLR_E_OU_PRZ_EXPORTA_ALT_SEM_ANEXO, responsavel);
            Integer validaVlrEOuPrzAlteracaoSemAnexo = CodedValues.VALIDA_VLR_EXPORTA_ALT_SEM_ANEXO;
            try {
                // Caso parâmetro de sistema esteja configurado errado, padrão será validar somente alteração de valor para maior
                if (!TextHelper.isNull(paramValidaVlrEOuPrzAlteracaoSemAnexo)) {
                    validaVlrEOuPrzAlteracaoSemAnexo = Integer.parseInt(paramValidaVlrEOuPrzAlteracaoSemAnexo.toString());
                }
            } catch (final NumberFormatException e) {
                LOG.error("Parâmetro para validar valor e/ou prazo na alteração sem anexo inválido.", e);
            }

            final List<String> sadCodigos = new ArrayList<>();
            sadCodigos.add(CodedValues.SAD_DEFERIDA);
            sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
            sadCodigos.add(CodedValues.SAD_AGUARD_LIQUIDACAO);
            sadCodigos.add(CodedValues.SAD_AGUARD_LIQUI_COMPRA);

            final List<String> tocCodigos = new ArrayList<>();
            tocCodigos.add(CodedValues.TOC_TARIF_RESERVA);
            tocCodigos.add(CodedValues.TOC_ALTERACAO_CONTRATO_PARA_MAIOR);
            tocCodigos.add(CodedValues.TOC_RELANCAMENTO_SEM_ANEXO);

            final List<String> tocCodigosReimplante = new ArrayList<>();
            tocCodigosReimplante.add(CodedValues.TOC_RELANCAMENTO);
            tocCodigosReimplante.add(CodedValues.TOC_RELANCAMENTO_COM_REDUCAO_VALOR);
            tocCodigosReimplante.add(CodedValues.TOC_RELANCAMENTO_SEM_ANEXO);

            final List<String> tarCodigosPermitidos = new ArrayList<>();
            tarCodigosPermitidos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_GENERICO.getCodigo());
            tarCodigosPermitidos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_SUSPENSAO.getCodigo());
            tarCodigosPermitidos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_LIQUIDACAO.getCodigo());

            if(ParamSist.getBoolParamSist(CodedValues.TPC_CONTRATOS_DEVEM_SER_VALIDADOS_PELA_CSE, responsavel)) {
                tarCodigosPermitidos.add(TipoArquivoEnum.ARQUIVO_ANEXO_RG.getCodigo());
                tarCodigosPermitidos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_PAGAMENTO.getCodigo());
                tarCodigosPermitidos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_CONTRACHEQUE.getCodigo());
                tarCodigosPermitidos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_OUTRO.getCodigo());
            }

            // Desbloqueia os contratos para exportacao por falta de anexo
            query.append("update tb_aut_desconto ade ");
            // Contrato ativo e que não possui anexo
            query.append("set ade.ade_exportacao = '").append(CodedValues.ADE_EXPORTACAO_PERMITIDA).append("' ");
            query.append("where ade.ade_exportacao = '").append(CodedValues.ADE_EXPORTACAO_BLOQUEADA).append("' ");
            LOG.trace(query.toString());
            int rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("CALL dropTableIfExists('tb_tmp_incl_alt_sem_anexo')");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("CALL createTemporaryTable('tb_tmp_incl_alt_sem_anexo (ade_codigo varchar2(32))')");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("insert into tb_tmp_incl_alt_sem_anexo (ade_codigo) ");
            query.append("select distinct ade.ade_codigo ");
            query.append("from tb_aut_desconto ade ");
            query.append("inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
            query.append("inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("inner join tb_periodo_exportacao pex on (cnv.org_codigo = pex.org_codigo) ");
            query.append("inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade.ade_codigo) ");

            // Contrato ativo que não está sendo pago
            query.append("where ade.sad_codigo in ('").append(TextHelper.join(sadCodigos, "','")).append("') ");
            query.append("and (coalesce(ade.ade_paga, 'N') <> 'S'");

            // Ou contrato ativo que foi alterado para maior para valor e/ou prazo, dependendo da configuração do parâmetro
            if (validaVlrEOuPrzAlteracaoSemAnexo.equals(CodedValues.VALIDA_VLR_EXPORTA_ALT_SEM_ANEXO)) {
                query.append(" or (case when oca.toc_codigo='").append(CodedValues.TOC_ALTERACAO_CONTRATO_PARA_MAIOR).append("' then coalesce(oca_ade_vlr_ant,ade_vlr_folha) <  coalesce(oca_ade_vlr_novo, ade_vlr) else ade.ade_vlr_folha < ade.ade_vlr end ");
                query.append(" and ade.sad_codigo in ('").append(TextHelper.join(sadCodigos, "','")).append("')) ");
            } else if (validaVlrEOuPrzAlteracaoSemAnexo.equals(CodedValues.VALIDA_PRZ_EXPORTA_ALT_SEM_ANEXO)) {
                query.append(" or (coalesce(ade.ade_prazo_folha, ade.ade_prazo_ref) < ade.ade_prazo");
                query.append(" and ade.sad_codigo in ('").append(TextHelper.join(sadCodigos, "','")).append("')) ");
            } else if (validaVlrEOuPrzAlteracaoSemAnexo.equals(CodedValues.VALIDA_VLR_E_PRZ_EXPORTA_ALT_SEM_ANEXO)) {
                query.append(" or ((case when oca.toc_codigo='").append(CodedValues.TOC_ALTERACAO_CONTRATO_PARA_MAIOR).append("' then coalesce(oca_ade_vlr_ant,ade_vlr_folha) <  coalesce(oca_ade_vlr_novo, ade_vlr) else ade.ade_vlr_folha < ade.ade_vlr end) and coalesce(ade.ade_prazo_folha, ade.ade_prazo_ref) < ade.ade_prazo ");
                query.append(" and ade.sad_codigo in ('").append(TextHelper.join(sadCodigos, "','")).append("')) ");
            } else if (validaVlrEOuPrzAlteracaoSemAnexo.equals(CodedValues.VALIDA_VLR_OU_PRZ_EXPORTA_ALT_SEM_ANEXO)) {
                query.append(" or ((case when oca.toc_codigo='").append(CodedValues.TOC_ALTERACAO_CONTRATO_PARA_MAIOR).append("' then coalesce(oca_ade_vlr_ant,ade_vlr_folha) <  coalesce(oca_ade_vlr_novo, ade_vlr) else ade.ade_vlr_folha < ade.ade_vlr end) or coalesce(ade.ade_prazo_folha, ade.ade_prazo_ref) < ade.ade_prazo ");
                query.append(" and ade.sad_codigo in ('").append(TextHelper.join(sadCodigos, "','")).append("')) ");
            }

            query.append(") ");

            // É um contrato incluído/alterado/reimplantado no período
            query.append("and oca.oca_periodo = pex.pex_periodo ");
            query.append("and oca.toc_codigo IN ('").append(TextHelper.join(tocCodigos, "','")).append("') ");

            // Não possui anexo ativo no período compreendido pela operação e a data final do período de exportação
            query.append("and not exists ( ");
            query.append("select 1 from tb_anexo_autorizacao_desconto aad ");
            query.append("where aad.ade_codigo = ade.ade_codigo ");
            query.append("and (aad.aad_periodo = pex.pex_periodo or (aad.aad_periodo is null and aad.aad_data between least(pex.pex_data_ini, oca.oca_data) and pex.pex_data_fim)) ");
            query.append("and aad.aad_ativo = 1 ");
            query.append("and NVL(SUBSTR(aad.aad_nome, INSTR(aad.aad_nome, '.')), aad.aad_nome) in ('").append(TextHelper.join(UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_CONTRATO_INTEGRACAO, "','")).append("') ");
            // Não deve apenas possuir anexo, deve possuir os tipo de arquivos permitidos (13,16,49)
            query.append("and aad.tar_codigo in ('").append(TextHelper.join(tarCodigosPermitidos, "','")).append("') ");
            query.append(") ");

            // Não possui anexo ativo, independente do período, na consignação de provisionamento de margem
            query.append("and not exists ( ");
            query.append("select 1 from tb_relacionamento_autorizacao rad ");
            query.append("inner join tb_anexo_autorizacao_desconto aad on (rad.ade_codigo_origem = aad.ade_codigo) ");
            query.append("where rad.ade_codigo_destino = ade.ade_codigo ");
            query.append("and rad.tnt_codigo = '").append(CodedValues.TNT_CARTAO).append("' ");
            query.append("and aad.aad_ativo = 1 ");
            query.append("and NVL(SUBSTR(aad.aad_nome, INSTR(aad.aad_nome, '.')), aad.aad_nome) in ('").append(TextHelper.join(UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_CONTRATO_INTEGRACAO, "','")).append("') ");
            query.append("and aad.tar_codigo in ('").append(TextHelper.join(tarCodigosPermitidos, "','")).append("') ");
            query.append(") ");

            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            if(!ParamSist.getBoolParamSist(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL, responsavel)) {
                //DESENV-17960 - Vamos permitir os contratos que são incluídos pelo suporte mesmo que sem anexo, então iremos deletar dessa tabela esses contratos para eles serem exportados mesmo assim.
                query.setLength(0);
                query.append("delete from tb_tmp_incl_alt_sem_anexo tmp ");
                query.append("WHERE EXISTS (select ade.ade_codigo FROM tb_aut_desconto ade ");
                query.append("inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
                query.append("inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
                query.append("inner join tb_periodo_exportacao pex on (cnv.org_codigo = pex.org_codigo) ");
                query.append("inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade.ade_codigo) ");
                query.append("left join tb_usuario_sup sup on (sup.usu_codigo = ade.usu_codigo) ");
                query.append("where oca.oca_periodo = pex.pex_periodo ");
                query.append("and tmp.ade_codigo = ade.ade_codigo ");
                query.append("and ade.ade_ano_mes_ini = pex.pex_periodo ");
                query.append("and ade.sad_codigo ='").append(CodedValues.SAD_DEFERIDA).append("' ");
                query.append("and oca.toc_codigo ='").append(CodedValues.TOC_TARIF_RESERVA).append("' ");
                query.append("and sup.usu_codigo IS NOT NULL) ");

                LOG.trace(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + rows);
            }

            /**
             * Alterar os contratos que não tiveram o anexo incluído para CodedValues.ADE_EXPORTACAO_BLOQUEADA
             * e tipo de não exportação TipoMotivoNaoExportacaoEnum.OPERACAO_SEM_ANEXO_OBRIGATORIO
             */
            query.setLength(0);
            query.append("update tb_aut_desconto ade ");
            query.append("set ade.mne_codigo = '").append(TipoMotivoNaoExportacaoEnum.OPERACAO_SEM_ANEXO_OBRIGATORIO.getCodigo()).append("', ");
            query.append("ade.ade_exportacao = '").append(CodedValues.ADE_EXPORTACAO_BLOQUEADA).append("' ");
            // Contrato ativo e que não possui anexo
            query.append("where exists ( ");
            query.append("select 1 from tb_tmp_incl_alt_sem_anexo tmp ");
            query.append("where tmp.ade_codigo = ade.ade_codigo ");
            query.append(") ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Reimplanta contratos com motivo de não exportação por ausência de anexo para serem reimplantados
            query.setLength(0);
            query.append("INSERT INTO tb_ocorrencia_autorizacao (OCA_CODIGO, ADE_CODIGO, OCA_DATA, OCA_PERIODO, TOC_CODIGO, USU_CODIGO, OCA_OBS) ");
            query.append("SELECT 'I' || ");
            query.append("TO_CHAR(CURRENT_TIMESTAMP, 'yyyymmddhhmmss') || ");
            query.append("SUBSTRING(LPAD(ade_numero, 10, '0'), 1, 12) || ");
            query.append("SUBSTRING(LPAD(").append(Columns.ADE_CODIGO).append(", 7, '0'), 1, 7), ");
            query.append(Columns.ADE_CODIGO).append(", ");
            query.append("CURRENT_DATE, PEX_PERIODO_POS, ");
            query.append("'").append(CodedValues.TOC_RELANCAMENTO_SEM_ANEXO).append("', ");
            query.append("'").append(CodedValues.USU_CODIGO_SISTEMA).append("', ");
            query.append("'").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.relancamento.automatico", responsavel)).append("'");
            query.append(" FROM ").append(Columns.TB_AUTORIZACAO_DESCONTO);
            query.append(" INNER JOIN ").append(Columns.TB_VERBA_CONVENIO).append(" ON (");
            query.append(Columns.VCO_CODIGO).append(" = ").append(Columns.ADE_VCO_CODIGO).append(") ");
            query.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" ON (");
            query.append(Columns.VCO_CNV_CODIGO).append(" = ").append(Columns.CNV_CODIGO).append(") ");
            query.append(" INNER JOIN ").append(Columns.TB_PERIODO_EXPORTACAO).append(" ON (");
            query.append(Columns.CNV_ORG_CODIGO).append(" = ").append(Columns.PEX_ORG_CODIGO).append(") ");
            // É um contrato removido da exportação por falta de anexo
            query.append("WHERE EXISTS ( ");
            query.append("SELECT 1 FROM tb_tmp_incl_alt_sem_anexo tmp ");
            query.append("WHERE ").append(Columns.ADE_CODIGO).append(" = tmp.ade_codigo ");
            query.append("AND ").append(Columns.ADE_MNE_CODIGO).append(" = '").append(TipoMotivoNaoExportacaoEnum.OPERACAO_SEM_ANEXO_OBRIGATORIO.getCodigo()).append("' ");
            query.append(") ");
            // Não é um contrato reimplantado no período
            query.append("AND NOT EXISTS ( ");
            query.append("SELECT 1 from tb_ocorrencia_autorizacao oca ");
            query.append("WHERE oca.ade_codigo = ").append(Columns.ADE_CODIGO).append(" ");
            query.append("AND oca.oca_periodo = ").append(Columns.PEX_PERIODO_POS).append(" ");
            query.append("AND oca.toc_codigo IN ('").append(TextHelper.join(tocCodigosReimplante, "','")).append("') ");
            query.append(") ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void alteraInclusaoAlteracaoSemAnexoSituacaoOrigem() throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder query = new StringBuilder();

        // Desbloqueia os contratos para exportacao por falta de anexo
        query.append("update tb_aut_desconto ade ");
        // Contrato ativo e que não possui anexo
        query.append("set ade.ade_exportacao = '").append(CodedValues.ADE_EXPORTACAO_PERMITIDA).append("' ");
        query.append("where ade.ade_exportacao = '").append(CodedValues.ADE_EXPORTACAO_BLOQUEADA).append("' ");
        query.append("and exists (select 1 from tb_tmp_incl_alt_sem_anexo tmp where tmp.ade_codigo = ade.ade_codigo) ");
        LOG.trace(query.toString());
        final int rows = jdbc.update(query.toString(), queryParams);
        LOG.trace("Linhas afetadas: " + rows);
    }

    @Override
    public void atualizaAdeVlrServicoLimiteMaxDescontoFolha() throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        try {
            final StringBuilder query = new StringBuilder();

            query.append("update tb_tmp_exportacao_ordenada tmp ");
            query.append("set ");
            query.append("tmp.valor_desconto = (");
            query.append("select pse.pse_vlr ");
            query.append("from tb_servico svc ");
            query.append("inner join tb_param_svc_consignante pse on (svc.svc_codigo = pse.svc_codigo) ");
            query.append("where tmp.svc_identificador = svc.svc_identificador ");
            query.append("  and pse.tps_codigo = '").append(CodedValues.TPS_VALOR_MAX_ENVIO_PARA_DESCONTO_FOLHA).append("' ");
            query.append("), ");

            query.append("tmp.valor_desconto_exc = (");
            query.append("select pse.pse_vlr ");
            query.append("from tb_servico svc ");
            query.append("inner join tb_param_svc_consignante pse on (svc.svc_codigo = pse.svc_codigo) ");
            query.append("where tmp.svc_identificador = svc.svc_identificador ");
            query.append("  and pse.tps_codigo = '").append(CodedValues.TPS_VALOR_MAX_ENVIO_PARA_DESCONTO_FOLHA).append("' ");
            query.append(") ");

            query.append("where exists (");
            query.append("select 1 ");
            query.append("from tb_servico svc ");
            query.append("inner join tb_param_svc_consignante pse on (svc.svc_codigo = pse.svc_codigo) ");
            query.append("where tmp.svc_identificador = svc.svc_identificador ");
            query.append("  and pse.tps_codigo = '").append(CodedValues.TPS_VALOR_MAX_ENVIO_PARA_DESCONTO_FOLHA).append("' ");
            query.append("  and cast(coalesce(nullif(replace(trim(pse.pse_vlr), ',', '.'), ''), '0.00') as number(13,2)) between 0.01 and cast(tmp.valor_desconto as number(13,2)) ");
            query.append(")");

            LOG.trace(query);
            final int linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public void selectExportacaoFeriasMensal(List<String> orgCodigos, List<String> estCodigos, List<String> verbas) throws DAOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeADEValorAbaixoMinimoSvc() throws DAOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void consolidaExclusaoInclusaoComoAlteracao() throws DAOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void salvarAdePaga(List<String> orgCodigos, List<String> estCodigos) throws DAOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void recuperarAdePaga() throws DAOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void moveComandosForaPeriodoBase(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            /*
             * DESENV-9664 : mover as ocorrências que são consideradas na exportação associadas a períodos do agrupamento anterior
             * que não eram o período base, para o período base da exportação atual.
             */
            final StringBuilder query = new StringBuilder();
            query.append("update tb_ocorrencia_autorizacao oca ");
            query.append("inner join tb_aut_desconto ade on (oca.ade_codigo = ade.ade_codigo) ");
            query.append("inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
            query.append("inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("inner join tb_periodo_exportacao pexBase on (cnv.org_codigo = pexBase.org_codigo and pexBase.pex_data_fim > pexBase.pex_data_ini) ");
            query.append("set oca.oca_periodo = pexBase.pex_periodo ");
            query.append("where oca.oca_periodo in ( ");
            query.append("select cfo_periodo ");
            query.append("from tb_calendario_folha_org cfo ");
            query.append("where cfo_periodo <= pexBase.pex_periodo_ant ");
            query.append("and cfo.org_codigo = cnv.org_codigo ");
            query.append("and cfo_data_fim = cfo_data_ini  ");
            query.append("union ");
            query.append("select cfe_periodo ");
            query.append("from tb_calendario_folha_est cfe  ");
            query.append("inner join tb_orgao org2 on (cfe.est_codigo = org2.est_codigo)  ");
            query.append("left outer join tb_calendario_folha_org cfo on (cfe_periodo = cfo_periodo and org2.org_codigo = cfo.org_codigo) ");
            query.append("where cfe_periodo <= pexBase.pex_periodo_ant ");
            query.append("and org2.org_codigo = cnv.org_codigo ");
            query.append("and cfo_periodo is null ");
            query.append("and cfe_data_fim = cfe_data_ini ");
            query.append("union ");
            query.append("select cfc_periodo ");
            query.append("from tb_calendario_folha_cse cfc ");
            query.append("inner join tb_estabelecimento est2 on (cfc.cse_codigo = est2.cse_codigo) ");
            query.append("inner join tb_orgao org2 on (est2.est_codigo = org2.est_codigo)  ");
            query.append("left outer join tb_calendario_folha_est cfe on (cfc_periodo = cfe_periodo and est2.est_codigo = cfe.est_codigo) ");
            query.append("left outer join tb_calendario_folha_org cfo on (cfc_periodo = cfo_periodo and org2.org_codigo = cfo.org_codigo) ");
            query.append("where cfc_periodo <= pexBase.pex_periodo_ant ");
            query.append("and org2.org_codigo = cnv.org_codigo ");
            query.append("and cfe_periodo is null ");
            query.append("and cfo_periodo is null ");
            query.append("and cfc_data_fim = cfc_data_ini ");
            query.append(") ");
            query.append("and oca.toc_codigo in ('").append(TextHelper.join(CodedValues.TOC_CODIGOS_EXPORTACAO_INICIAL, "','")).append("') ");

            if ((estCodigos != null) && (estCodigos.size() > 0)) {
                query.append("and cnv.org_codigo in (select org.org_codigo from tb_orgao org where org.est_codigo in (:estCodigos)) ");
                queryParams.addValue("estCodigos", estCodigos);
            } else if ((orgCodigos != null) && (orgCodigos.size() > 0)) {
                query.append("and cnv.org_codigo in (:orgCodigos) ");
                queryParams.addValue("orgCodigos", orgCodigos);
            }
            LOG.trace(query);
            int linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);

            /*
             * DESENV-9664 : Mover também as ocorrências que são consideradas na exportação associadas a períodos do agrupamento atual
             * que não são o período base, para o período base da exportação seguinte.
             */
            query.setLength(0);
            query.append("update tb_ocorrencia_autorizacao oca ");
            query.append("inner join tb_aut_desconto ade on (oca.ade_codigo = ade.ade_codigo) ");
            query.append("inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
            query.append("inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("inner join tb_periodo_exportacao pexOca on (cnv.org_codigo = pexOca.org_codigo and oca.oca_periodo = pexOca.pex_periodo and pexOca.pex_data_fim = pexOca.pex_data_ini) ");
            query.append("inner join tb_periodo_exportacao pexBase on (cnv.org_codigo = pexBase.org_codigo and pexBase.pex_data_fim > pexBase.pex_data_ini) ");
            query.append("set oca.oca_periodo = (select max(pex_periodo_pos) from tb_periodo_exportacao pex where cnv.org_codigo = pex.org_codigo) ");
            query.append("where oca.toc_codigo in ('").append(TextHelper.join(CodedValues.TOC_CODIGOS_EXPORTACAO_INICIAL, "','")).append("') ");

            if ((estCodigos != null) && (estCodigos.size() > 0)) {
                query.append("and cnv.org_codigo in (select org.org_codigo from tb_orgao org where org.est_codigo in (:estCodigos)) ");
                queryParams.addValue("estCodigos", estCodigos);
            } else if ((orgCodigos != null) && (orgCodigos.size() > 0)) {
                query.append("and cnv.org_codigo in (:orgCodigos) ");
                queryParams.addValue("orgCodigos", orgCodigos);
            }
            LOG.trace(query);
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public void removeAdeExportacaoForaPeriodoBase() throws DAOException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

            /*
             * DESENV-10225 : Implementação do item 7 da DESENV-9664 (Após a seleção de candidatos à exportação,
             * remover da tabela registros que esteja associados a períodos que não são a base do agrupamento,
             * com base no campo "tb_tmp_exp_mov_fin.oca_periodo".).
             */
            final StringBuilder query = new StringBuilder();
            query.append("delete from tb_tmp_exp_mov_fin ");
            query.append("where oca_periodo in (select distinct pex_periodo from tb_periodo_exportacao where pex_data_fim = pex_data_ini) ");

            LOG.trace(query);
            final int linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public void atualizaAutorizaPgtParcial() throws DAOException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final StringBuilder query = new StringBuilder();

            query.append("update tb_tmp_exportacao_ordenada tmp ");
            query.append("inner join tb_aut_desconto ade on (ade.ade_numero = tmp.ade_numero) ");
            query.append("inner join tb_registro_servidor rse on (rse.rse_codigo = ade.rse_codigo) ");
            query.append("inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) ");
            query.append("inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("inner join tb_param_consignataria pcs on (cnv.csa_codigo = pcs.csa_codigo and pcs.tpa_codigo = '" + CodedValues.TPA_PERMITE_PAGAMENTO_PARCIAL + "' and pcs.pcs_vlr='" + CodedValues.TPA_SIM + "') ");
            query.append("set tmp.autoriza_pgt_parcial ='N' ");
            query.append("where tmp.autoriza_pgt_parcial='S' ");
            query.append("and '" + CodedValues.TOC_SER_AUTORIZA_DESC_PARCIAL + "' != (select toc_codigo from tb_ocorrencia_servidor ocs where rse.ser_codigo = ocs.ser_codigo and toc_codigo in ('" + CodedValues.TOC_SER_AUTORIZA_DESC_PARCIAL + "', '" + CodedValues.TOC_SER_NAO_AUTORIZA_DESC_PARCIAL + "') order by ocs_data desc limit 1 )");
            query.append("or not exists(select toc_codigo from tb_ocorrencia_servidor ocs where rse.ser_codigo = ocs.ser_codigo and toc_codigo in ('" + CodedValues.TOC_SER_AUTORIZA_DESC_PARCIAL + "', '" + CodedValues.TOC_SER_NAO_AUTORIZA_DESC_PARCIAL + "') order by ocs_data desc limit 1 )");

            LOG.trace(query);
            final int linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public void excluiContratosRseExcluidosExportacao() throws DAOException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final StringBuilder query = new StringBuilder();

            query.append("update tb_aut_desconto ade ");
            query.append("set ade.mne_codigo ='").append(TipoMotivoNaoExportacaoEnum.SERVIDOR_EXCLUIDO.getCodigo()).append("' ");
            query.append("where exists (select tmp.ade_numero from tb_tmp_exportacao tmp ");
            query.append("inner join tb_registro_servidor rse on (rse.rse_codigo = tmp.rse_codigo) ");
            query.append("where rse.srs_codigo IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("') ");
            query.append("and ade.ade_numero = tmp.ade_numero) ");

            LOG.trace(query);
            int linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);

            query.setLength(0);
            query.append("delete from tb_tmp_exportacao tmp ");
            query.append("WHERE EXISTS (select ade.ade_numero FROM tb_aut_desconto ade ");
            query.append("inner join tb_registro_servidor rse on (rse.rse_codigo = ade.rse_codigo) ");
            query.append("where rse.srs_codigo IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("') ");
            query.append("and ade.ade_numero = tmp.ade_numero) ");

            LOG.trace(query);
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public void removeContratosSemPermissaoCse() throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder query = new StringBuilder();

        final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

        final List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_DEFERIDA);
        sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
        sadCodigos.add(CodedValues.SAD_AGUARD_LIQUIDACAO);
        sadCodigos.add(CodedValues.SAD_AGUARD_LIQUI_COMPRA);

        final List<String> sadCodigosEncerrados = new ArrayList<>();
        sadCodigosEncerrados.add(CodedValues.SAD_CANCELADA);
        sadCodigosEncerrados.add(CodedValues.SAD_LIQUIDADA);
        sadCodigosEncerrados.add(CodedValues.SAD_SUSPENSA);
        sadCodigosEncerrados.add(CodedValues.SAD_SUSPENSA_CSE);

        final List<String> tocCodigos = new ArrayList<>();
        tocCodigos.add(CodedValues.TOC_TARIF_RESERVA);
        tocCodigos.add(CodedValues.TOC_ALTERACAO_CONTRATO_PARA_MAIOR);
        tocCodigos.add(CodedValues.TOC_RELANCAMENTO_SEM_ANEXO);
        tocCodigos.add(CodedValues.TOC_RELANCAMENTO_COM_REDUCAO_VALOR);
        tocCodigos.add(CodedValues.TOC_RELANCAMENTO);

        final List<String> tocCodigosEncerramento = new ArrayList<>();
        tocCodigosEncerramento.add(CodedValues.TOC_TARIF_LIQUIDACAO);
        tocCodigosEncerramento.add(CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO);
        tocCodigosEncerramento.add(CodedValues.TOC_SUSPENSAO_CONTRATO);

        final List<String> tocCodigosReimplante = new ArrayList<>();
        tocCodigosReimplante.add(CodedValues.TOC_RELANCAMENTO);
        tocCodigosReimplante.add(CodedValues.TOC_RELANCAMENTO_COM_REDUCAO_VALOR);
        tocCodigosReimplante.add(CodedValues.TOC_RELANCAMENTO_SEM_ANEXO);

        query.append("update tb_aut_desconto ade ");
        query.append("set ade.ade_exportacao = '").append(CodedValues.ADE_EXPORTACAO_PERMITIDA).append("' ");
        query.append("where ade.ade_exportacao = '").append(CodedValues.ADE_EXPORTACAO_BLOQUEADA).append("' ");
        query.append("and ade.mne_codigo in ").append("('" + TipoMotivoNaoExportacaoEnum.OPERACAO_SEM_PERMISSAO_GESTOR.getCodigo() + "' , '" + TipoMotivoNaoExportacaoEnum.AGUARDANDO_ANALISE_SUP.getCodigo() + "');");
        LOG.trace(query.toString());
        int rows = jdbc.update(query.toString(), queryParams);
        LOG.trace("Linhas afetadas: " + rows);

        query.setLength(0);
        query.append("insert into tb_tmp_contratos_sem_permissao (ade_codigo, org_codigo)  ");
        query.append("select distinct ade.ade_codigo, cnv.org_codigo ");
        query.append("from tb_aut_desconto ade ");
        query.append("inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
        query.append("inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
        query.append("inner join tb_periodo_exportacao pex on (cnv.org_codigo = pex.org_codigo) ");
        query.append("inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade.ade_codigo) ");
        query.append("inner join tb_solicitacao_autorizacao soa on (soa.ade_codigo = ade.ade_codigo) ");

        // Contrato que não está sendo pago
        query.append("where (ade.sad_codigo in ('").append(TextHelper.join(sadCodigos, "','")).append("') ");
        query.append(" or (ade.sad_codigo in ('").append(TextHelper.join(sadCodigosEncerrados, "','")).append("') ");
        query.append(" and exists (");
        query.append("     select 1 from tb_ocorrencia_autorizacao ocaE ");
        query.append("     where ocaE.ade_codigo = ade.ade_codigo ");
        query.append("       and ocaE.toc_codigo in ('").append(TextHelper.join(tocCodigosEncerramento, "','")).append("') ");
        query.append("       and (ocaE.oca_data > pex.pex_data_fim or ocaE.oca_periodo > pex.pex_periodo) ");
        query.append("     ) ");
        query.append("  ) ");
        query.append(") ");

        // É um contrato incluído/alterado/reimplantado no período
        query.append("and oca.oca_periodo = pex.pex_periodo ");
        // Não pode ter contrato como data inicial futura
        query.append("and ade.ade_ano_mes_ini <= pex.pex_periodo ");
        query.append("and oca.toc_codigo in ('").append(TextHelper.join(tocCodigos, "','")).append("') ");

        // Solicitação de validação de documentação ainda pendente
        query.append("AND soa.sso_codigo <> '").append(StatusSolicitacaoEnum.VALIDACAO_DOCUMENTO_APROVADA).append("' ");
        query.append("AND soa.tis_codigo = '").append(TipoSolicitacaoEnum.SOLICITACAO_DEPENDE_AUTORIZACAO.getCodigo()).append("' ");
        query.append("AND soa.soa_periodo is not null ");

        // Que não possui aprovação do gestor para exportação.
        query.append("AND NOT EXISTS (");
        query.append("    SELECT 1 ");
        query.append("    FROM tb_solicitacao_autorizacao soa1 ");
        query.append("    WHERE soa1.ade_codigo = ade.ade_codigo ");
        query.append("      AND soa1.soa_data > soa.soa_data ");
        query.append("      AND soa1.SOA_PERIODO >= soa.SOA_PERIODO ");
        query.append("      AND soa1.SOA_PERIODO  = pex.pex_periodo ");
        query.append("      AND soa1.SSO_CODIGO IN ('").append(StatusSolicitacaoEnum.VALIDACAO_DOCUMENTO_APROVADA + "' , '" + StatusSolicitacaoEnum.AGUARDANDO_ANALISE_SUP).append("') ");
        query.append("      AND soa1.tis_codigo = '").append(TipoSolicitacaoEnum.SOLICITACAO_DEPENDE_AUTORIZACAO.getCodigo()).append("' ");
        query.append(") ");

        LOG.trace(query.toString());
        rows = jdbc.update(query.toString(), queryParams);
        LOG.trace("Linhas afetadas: " + rows);


        //Contratos removidos da exportação via insert manual pela análise de suporte DESENV-20016
        query.setLength(0);
        query.append("insert into tb_tmp_contratos_sem_permissao (ade_codigo, org_codigo)  ");
        query.append("select distinct ade.ade_codigo, cnv.org_codigo ");
        query.append("from tb_aut_desconto ade ");
        query.append("inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
        query.append("inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
        query.append("inner join tb_periodo_exportacao pex on (cnv.org_codigo = pex.org_codigo) ");
        query.append("inner join tb_solicitacao_autorizacao soa on (soa.ade_codigo = ade.ade_codigo) ");

        query.append("where ade.sad_codigo in ('").append(TextHelper.join(sadCodigos, "','")).append("') ");
        query.append("AND NOT EXISTS (");
        query.append("    SELECT 1 ");
        query.append("    FROM tb_solicitacao_autorizacao soa1 ");
        query.append("    WHERE soa1.ade_codigo = ade.ade_codigo ");
        query.append("      AND soa1.soa_data > soa.soa_data ");
        query.append("      AND soa1.SOA_PERIODO >= soa.SOA_PERIODO ");
        query.append("      AND soa1.SOA_PERIODO  = pex.pex_periodo ");
        query.append("      AND soa1.SSO_CODIGO IN ('").append(StatusSolicitacaoEnum.VALIDACAO_DOCUMENTO_APROVADA).append("' , '").append(StatusSolicitacaoEnum.AGUARDANDO_ANALISE_SUP).append("') ");
        query.append("      AND soa1.tis_codigo = '").append(TipoSolicitacaoEnum.SOLICITACAO_DEPENDE_AUTORIZACAO.getCodigo()).append("' ");
        query.append(") ");

        query.append("AND NOT EXISTS (");
        query.append("    SELECT 1 ");
        query.append("    FROM tb_tmp_contratos_sem_permissao tmp ");
        query.append("    WHERE tmp.ade_codigo = ade.ade_codigo ");
        query.append(") ");

        query.append("AND soa.soa_periodo = pex.pex_periodo ");
        query.append("AND soa.sso_codigo = '").append(StatusSolicitacaoEnum.AGUARDANDO_ANALISE_SUP).append("' ");
        query.append("AND soa.tis_codigo = '").append(TipoSolicitacaoEnum.SOLICITACAO_DEPENDE_AUTORIZACAO.getCodigo()).append("' ");
        query.append("AND soa.soa_periodo is not null ");

        LOG.trace(query.toString());
        rows = jdbc.update(query.toString(), queryParams);
        LOG.trace("Linhas afetadas: " + rows);


        query.setLength(0);
        query.append("update tb_aut_desconto ade ");
        query.append("inner join tb_tmp_contratos_sem_permissao tmp on (tmp.ade_codigo = ade.ade_codigo) ");
        query.append("set ade.mne_codigo = '").append(TipoMotivoNaoExportacaoEnum.OPERACAO_SEM_PERMISSAO_GESTOR.getCodigo()).append("', ");
        query.append("ade.ade_exportacao = '").append(CodedValues.ADE_EXPORTACAO_BLOQUEADA).append("' ");
        LOG.trace(query.toString());
        rows = jdbc.update(query.toString(), queryParams);
        LOG.trace("Linhas afetadas: " + rows);

        query.setLength(0);
        query.append("update tb_aut_desconto ade ");
        query.append("inner join tb_tmp_contratos_sem_permissao tmp on (tmp.ade_codigo = ade.ade_codigo) ");
        query.append("inner join tb_solicitacao_autorizacao soa on (tmp.ade_codigo = soa.ade_codigo) ");
        query.append("set ade.mne_codigo = '").append(TipoMotivoNaoExportacaoEnum.AGUARDANDO_ANALISE_SUP.getCodigo()).append("', ");
        query.append("ade.ade_exportacao = '").append(CodedValues.ADE_EXPORTACAO_BLOQUEADA).append("' ");
        query.append("where soa.sso_codigo = '").append(StatusSolicitacaoEnum.AGUARDANDO_ANALISE_SUP).append("' ");
        LOG.trace(query.toString());
        rows = jdbc.update(query.toString(), queryParams);
        LOG.trace("Linhas afetadas: " + rows);

        // DESENV-18321: Se um contrato do período atual não for exportado e possuir anexo no período da exportação,
        // incrementar o período do anexo para o próximo período futuro.
        query.setLength(0);
        query.append("update tb_anexo_autorizacao_desconto aad ");
        query.append("set aad.aad_periodo = ( ");
        query.append("  select max(pex.pex_periodo_pos) ");
        query.append("  from tb_periodo_exportacao pex ");
        query.append("  inner join tb_tmp_contratos_sem_permissao csp on (csp.org_codigo = pex.org_codigo) ");
        query.append("  where aad.ade_codigo = csp.ade_codigo ");
        query.append(") ");
        query.append("where exists ( ");
        query.append("  select 1 ");
        query.append("  from tb_periodo_exportacao pex ");
        query.append("  inner join tb_tmp_contratos_sem_permissao csp on (csp.org_codigo = pex.org_codigo) ");
        query.append("  where aad.ade_codigo = csp.ade_codigo ");
        query.append("    and aad.aad_periodo = pex.pex_periodo ");
        query.append(") ");
        LOG.trace(query.toString());
        rows = jdbc.update(query.toString(), queryParams);
        LOG.trace("Linhas afetadas: " + rows);

        query.setLength(0);
        query.append("INSERT INTO tb_ocorrencia_autorizacao (OCA_CODIGO, ADE_CODIGO, OCA_DATA, OCA_PERIODO, TOC_CODIGO, USU_CODIGO, OCA_OBS) ");
        query.append("SELECT CONCAT('I', ");
        query.append("DATE_FORMAT(NOW(), '%y%m%d%H%i%S'), ");
        query.append("SUBSTRING(LPAD(ade_numero, 12, '0'), 1, 12), ");
        query.append("SUBSTRING(LPAD(@rownum := COALESCE(@rownum, 0) + 1, 7, '0'), 1, 7)), ");
        query.append(Columns.ADE_CODIGO).append(", ");
        query.append("NOW(), PEX_PERIODO_POS, ");
        query.append("'").append(CodedValues.TOC_RELANCAMENTO_SEM_ANEXO).append("', ");
        query.append("'").append(CodedValues.USU_CODIGO_SISTEMA).append("', ");
        query.append("'").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.relancamento.automatico", responsavel)).append("'");
        query.append(" FROM ").append(Columns.TB_AUTORIZACAO_DESCONTO);
        query.append(" INNER JOIN ").append(Columns.TB_VERBA_CONVENIO).append(" ON (");
        query.append(Columns.VCO_CODIGO).append(" = ").append(Columns.ADE_VCO_CODIGO).append(") ");
        query.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" ON (");
        query.append(Columns.VCO_CNV_CODIGO).append(" = ").append(Columns.CNV_CODIGO).append(") ");
        query.append(" INNER JOIN ").append(Columns.TB_PERIODO_EXPORTACAO).append(" ON (");
        query.append(Columns.CNV_ORG_CODIGO).append(" = ").append(Columns.PEX_ORG_CODIGO).append(") ");
        query.append("WHERE EXISTS ( ");
        query.append("SELECT 1 FROM tb_tmp_contratos_sem_permissao tmp ");
        query.append("WHERE ").append(Columns.ADE_CODIGO).append(" = tmp.ade_codigo ");
        query.append("AND ").append(Columns.ADE_MNE_CODIGO).append(" = '").append(TipoMotivoNaoExportacaoEnum.OPERACAO_SEM_PERMISSAO_GESTOR.getCodigo()).append("' ");
        query.append(") ");
        query.append("AND NOT EXISTS ( ");
        query.append("SELECT 1 from tb_ocorrencia_autorizacao oca ");
        query.append("WHERE oca.ade_codigo = ").append(Columns.ADE_CODIGO).append(" ");
        query.append("AND oca.oca_periodo = ").append(Columns.PEX_PERIODO_POS).append(" ");
        query.append("AND oca.toc_codigo IN ('").append(TextHelper.join(tocCodigosReimplante, "','")).append("') ");
        query.append(") ");
        LOG.trace(query.toString());
        rows = jdbc.update(query.toString(), queryParams);
        LOG.trace("Linhas afetadas: " + rows);
    }

    private void removeContratosLiquidadosNuncaProcessadosSemPermissaoCse() throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final List<String> sadCodigosEncerrados = new ArrayList<>();
        sadCodigosEncerrados.add(CodedValues.SAD_CANCELADA);
        sadCodigosEncerrados.add(CodedValues.SAD_LIQUIDADA);
        sadCodigosEncerrados.add(CodedValues.SAD_SUSPENSA);
        sadCodigosEncerrados.add(CodedValues.SAD_SUSPENSA_CSE);

        final List<String> tocCodigosEncerramento = new ArrayList<>();
        tocCodigosEncerramento.add(CodedValues.TOC_TARIF_LIQUIDACAO);
        tocCodigosEncerramento.add(CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO);
        tocCodigosEncerramento.add(CodedValues.TOC_SUSPENSAO_CONTRATO);

        final StringBuilder query = new StringBuilder();
        query.append("INSERT INTO tb_tmp_remove_ade_nunca_enviados ");
        query.append("select DISTINCT ade.ade_codigo ");
        query.append("FROM tb_aut_desconto ade ");
        query.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
        query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
        query.append("INNER JOIN tb_periodo_exportacao pex ON (pex.org_codigo = cnv.org_codigo) ");
        query.append("INNER JOIN tb_ocorrencia_autorizacao oca ON (ade.ade_codigo = oca.ade_codigo and oca.oca_periodo = pex.pex_periodo) ");
        query.append("WHERE ade.sad_codigo in ('").append(TextHelper.join(sadCodigosEncerrados, "','")).append("') ");
        query.append("AND oca.toc_codigo in ('").append(TextHelper.join(tocCodigosEncerramento, "','")).append("') ");

        // Existe em ambas tb_parcela_desconto e tb_parcela_desconto_periodo
        query.append("AND (NOT EXISTS (SELECT 1 FROM tb_parcela_desconto prd WHERE ade.ade_codigo = prd.ade_codigo AND (prd.mne_codigo !='7' OR prd.mne_codigo IS NULL)) ");
        query.append("AND (EXISTS (SELECT 1 FROM tb_parcela_desconto_periodo prd1 WHERE ade.ade_codigo = prd1.ade_codigo) AND NOT EXISTS (SELECT 1 FROM tb_parcela_desconto_periodo prd1 WHERE ade.ade_codigo = prd1.ade_codigo AND (prd1.mne_codigo !='7' OR prd1.mne_codigo IS NULL)) ");
        query.append(") ");

        // Existe somente na tb_parcela_desconto
        query.append("OR (NOT EXISTS (SELECT 1 FROM tb_parcela_desconto prd WHERE ade.ade_codigo = prd.ade_codigo AND (prd.mne_codigo !='7' OR prd.mne_codigo IS NULL)) ");
        query.append("AND ( NOT EXISTS (SELECT 1 FROM tb_parcela_desconto_periodo prd1 WHERE ade.ade_codigo = prd1.ade_codigo)) ");
        query.append(") ");

        // Existe somente na tb_parcela_desconto_periodo
        query.append("OR (EXISTS (SELECT 1 FROM tb_parcela_desconto_periodo prd1 WHERE ade.ade_codigo = prd1.ade_codigo) AND NOT EXISTS (SELECT 1 FROM tb_parcela_desconto_periodo prd WHERE ade.ade_codigo = prd.ade_codigo AND (prd.mne_codigo !='7' OR prd.mne_codigo IS NULL)) ");
        query.append("AND ( NOT EXISTS (SELECT 1 FROM tb_parcela_desconto prd1 WHERE ade.ade_codigo = prd1.ade_codigo)) ");
        query.append(") ");
        query.append(" ) ");        LOG.trace(query.toString());
        final int rows = jdbc.update(query.toString(), queryParams);
        LOG.trace("Linhas afetadas: " + rows);
    }
}