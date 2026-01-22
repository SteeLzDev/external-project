package com.zetra.econsig.values;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: Columns</p>
 * <p>Description: Contï¿½m o nome das colunas das tabelas da base de dados</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class Columns {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Columns.class);

    public static final String TB_ARQUIVO_MOVIMENTO             = "tb_arquivo_movimento";
    public static final String ARM_SITUACAO                     = TB_ARQUIVO_MOVIMENTO + ".arm_situacao";

    public static final String TB_ARQUIVO_MOVIMENTO_VALIDACAO   = "tb_arquivo_movimento_validacao";
    public static final String AMV_OPERACAO                     = TB_ARQUIVO_MOVIMENTO_VALIDACAO + ".amv_operacao";
    public static final String AMV_ORG_IDENTIFICADOR            = TB_ARQUIVO_MOVIMENTO_VALIDACAO + ".org_identificador";
    public static final String AMV_EST_IDENTIFICADOR            = TB_ARQUIVO_MOVIMENTO_VALIDACAO + ".est_identificador";
    public static final String AMV_CSA_IDENTIFICADOR            = TB_ARQUIVO_MOVIMENTO_VALIDACAO + ".csa_identificador";
    public static final String AMV_SVC_IDENTIFICADOR            = TB_ARQUIVO_MOVIMENTO_VALIDACAO + ".svc_identificador";
    public static final String AMV_CNV_COD_VERBA                = TB_ARQUIVO_MOVIMENTO_VALIDACAO + ".cnv_cod_verba";
    public static final String AMV_CNV_CODIGO                   = TB_ARQUIVO_MOVIMENTO_VALIDACAO + ".cnv_codigo";
    public static final String AMV_SER_NOME                     = TB_ARQUIVO_MOVIMENTO_VALIDACAO + ".ser_nome";
    public static final String AMV_SER_CPF                      = TB_ARQUIVO_MOVIMENTO_VALIDACAO + ".ser_cpf";
    public static final String AMV_RSE_MATRICULA                = TB_ARQUIVO_MOVIMENTO_VALIDACAO + ".rse_matricula";
    public static final String AMV_RSE_MATRICULA_INST           = TB_ARQUIVO_MOVIMENTO_VALIDACAO + ".rse_matricula_inst";
    public static final String AMV_RSE_CODIGO                   = TB_ARQUIVO_MOVIMENTO_VALIDACAO + ".rse_codigo";
    public static final String AMV_PERIODO                      = TB_ARQUIVO_MOVIMENTO_VALIDACAO + ".amv_periodo";
    public static final String AMV_COMPETENCIA                  = TB_ARQUIVO_MOVIMENTO_VALIDACAO + ".amv_competencia";
    public static final String AMV_DATA                         = TB_ARQUIVO_MOVIMENTO_VALIDACAO + ".amv_data";
    public static final String AMV_PEX_PERIODO                  = TB_ARQUIVO_MOVIMENTO_VALIDACAO + ".pex_periodo";
    public static final String AMV_PEX_PERIODO_ANT              = TB_ARQUIVO_MOVIMENTO_VALIDACAO + ".pex_periodo_ant";
    public static final String AMV_ADE_INDICE                   = TB_ARQUIVO_MOVIMENTO_VALIDACAO + ".ade_indice";
    public static final String AMV_ADE_NUMERO                   = TB_ARQUIVO_MOVIMENTO_VALIDACAO + ".ade_numero";
    public static final String AMV_ADE_PRAZO                    = TB_ARQUIVO_MOVIMENTO_VALIDACAO + ".ade_prazo";
    public static final String AMV_ADE_VLR                      = TB_ARQUIVO_MOVIMENTO_VALIDACAO + ".ade_vlr";
    public static final String AMV_ADE_TIPO_VLR                 = TB_ARQUIVO_MOVIMENTO_VALIDACAO + ".ade_tipo_vlr";
    public static final String AMV_ADE_VLR_FOLHA                = TB_ARQUIVO_MOVIMENTO_VALIDACAO + ".ade_vlr_folha";
    public static final String AMV_ADE_DATA                     = TB_ARQUIVO_MOVIMENTO_VALIDACAO + ".ade_data";
    public static final String AMV_ADE_DATA_REF                 = TB_ARQUIVO_MOVIMENTO_VALIDACAO + ".ade_data_ref";
    public static final String AMV_ADE_ANO_MES_INI              = TB_ARQUIVO_MOVIMENTO_VALIDACAO + ".ade_ano_mes_ini";
    public static final String AMV_ADE_ANO_MES_FIM              = TB_ARQUIVO_MOVIMENTO_VALIDACAO + ".ade_ano_mes_fim";
    public static final String AMV_ADE_ANO_MES_INI_FOLHA        = TB_ARQUIVO_MOVIMENTO_VALIDACAO + ".ade_ano_mes_ini_folha";
    public static final String AMV_ADE_ANO_MES_FIM_FOLHA        = TB_ARQUIVO_MOVIMENTO_VALIDACAO + ".ade_ano_mes_fim_folha";
    public static final String AMV_ADE_ANO_MES_INI_REF          = TB_ARQUIVO_MOVIMENTO_VALIDACAO + ".ade_ano_mes_ini_ref";
    public static final String AMV_ADE_ANO_MES_FIM_REF          = TB_ARQUIVO_MOVIMENTO_VALIDACAO + ".ade_ano_mes_fim_ref";
    public static final String AMV_ADE_COD_REG                  = TB_ARQUIVO_MOVIMENTO_VALIDACAO + ".ade_cod_reg";

    public static final String TB_ARQUIVO_FATURAMENTO_BENEFICIO = "tb_arquivo_faturamento_ben";
    public static final String AFB_CODIGO                       = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".afb_codigo";
    public static final String AFB_FAT_CODIGO                   = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".fat_codigo";
    public static final String AFB_ADE_CODIGO                   = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".ade_codigo";
    public static final String AFB_TLA_CODIGO                   = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".tla_codigo";
    public static final String AFB_RSE_MATRICULA                = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".rse_matricula";
    public static final String AFB_CBE_NUMERO                   = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".cbe_numero";
    public static final String AFB_CBE_VALOR_TOTAL              = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".cbe_valor_total";
    public static final String AFB_CBE_DATA_INCLUSAO            = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".cbe_data_inclusao";
    public static final String AFB_BEN_CODIGO_REGISTRO          = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".ben_codigo_registro";
    public static final String AFB_BEN_CODIGO_CONTRATO          = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".ben_codigo_contrato";
    public static final String AFB_BFC_CPF                      = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".bfc_cpf";
    public static final String AFB_BFC_CELULAR                  = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".bfc_celular";
    public static final String AFB_BFC_ORDEM_DEPENDENCIA        = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".bfc_ordem_dependencia";
    public static final String AFB_BFC_NOME                     = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".bfc_nome";
    public static final String AFB_ENS_CEP                      = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".ens_cep";
    public static final String AFB_ENS_LOGRADOURO               = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".ens_logradouro";
    public static final String AFB_ENS_NUMERO                   = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".ens_numero";
    public static final String AFB_ENS_COMPLEMENTO              = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".ens_complemento";
    public static final String AFB_ENS_BAIRRO                   = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".ens_bairro";
    public static final String AFB_ENS_MUNICIPIO                = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".ens_municipio";
    public static final String AFB_ENS_UF                       = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".ens_uf";
    public static final String AFB_ENS_CODIGO_MUNICIPIO         = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".ens_codigo_municipio";
    public static final String AFB_PRD_VLR_PREVISTO             = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".prd_vlr_previsto";
    public static final String AFB_ADE_ANO_MES_INI              = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".ade_ano_mes_ini";
    public static final String AFB_CNV_COD_VERBA                = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".cnv_cod_verba";
    public static final String AFB_RSE_MATRICULA_INST           = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".rse_matricula_inst";
    public static final String AFB_NUMERO_LOTE                  = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".afb_numero_lote";
    public static final String AFB_ITEM_LOTE                    = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".afb_item_lote";
    public static final String AFB_VALOR_SUBSIDIO               = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".afb_valor_subsidio";
    public static final String AFB_VALOR_REALIZADO              = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".afb_valor_realizado";
    public static final String AFB_VALOR_NAO_REALIZADO          = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".afb_valor_nao_realizado";
    public static final String AFB_VALOR_TOTAL                  = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".afb_valor_total";
    public static final String AFB_CODIGO_FUNDO_REPASSE         = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".afb_codigo_fundo_repasse";
    public static final String AFB_DESCRICAO_FUNDO_REPASSE      = TB_ARQUIVO_FATURAMENTO_BENEFICIO + ".afb_descricao_fundo_repasse";

    public static final String TB_ARQUIVO_PREVIA_OPERADORA      = "tb_arquivo_previa_operadora";
    public static final String APO_CODIGO                       = TB_ARQUIVO_PREVIA_OPERADORA + ".apo_codigo";
    public static final String APO_CSA_CODIGO                   = TB_ARQUIVO_PREVIA_OPERADORA + ".csa_codigo";
    public static final String APO_NOME_ARQUIVO                 = TB_ARQUIVO_PREVIA_OPERADORA + ".apo_nome_arquivo";
    public static final String APO_OPERACAO                     = TB_ARQUIVO_PREVIA_OPERADORA + ".apo_operacao";
    public static final String APO_PERIODO_FATURAMENTO          = TB_ARQUIVO_PREVIA_OPERADORA + ".apo_periodo_faturamento";
    public static final String APO_DATA_INCLUSAO                = TB_ARQUIVO_PREVIA_OPERADORA + ".apo_data_inclusao";
    public static final String APO_DATA_EXCLUSAO                = TB_ARQUIVO_PREVIA_OPERADORA + ".apo_data_exclusao";
    public static final String APO_CBE_NUMERO                   = TB_ARQUIVO_PREVIA_OPERADORA + ".cbe_numero";
    public static final String APO_BEN_CODIGO_REGISTRO          = TB_ARQUIVO_PREVIA_OPERADORA + ".ben_codigo_registro";
    public static final String APO_RSE_MATRICULA                = TB_ARQUIVO_PREVIA_OPERADORA + ".rse_matricula";
    public static final String APO_BEN_CODIGO_CONTRATO          = TB_ARQUIVO_PREVIA_OPERADORA + ".ben_codigo_contrato";
    public static final String APO_VALOR_DEBITO                 = TB_ARQUIVO_PREVIA_OPERADORA + ".apo_valor_debito";
    public static final String APO_TIPO_LANCAMENTO              = TB_ARQUIVO_PREVIA_OPERADORA + ".apo_tipo_lancamento";
    public static final String APO_REAJUSTE_FAIXA_ETARIA        = TB_ARQUIVO_PREVIA_OPERADORA + ".apo_reajuste_faixa_etaria";
    public static final String APO_REAJUSTE_ANUAL               = TB_ARQUIVO_PREVIA_OPERADORA + ".apo_reajuste_anual";
    public static final String APO_NUMERO_LOTE                  = TB_ARQUIVO_PREVIA_OPERADORA + ".apo_numero_lote";
    public static final String APO_ITEM_LOTE                    = TB_ARQUIVO_PREVIA_OPERADORA + ".apo_item_lote";
    public static final String APO_VALOR_SUBSIDIO               = TB_ARQUIVO_PREVIA_OPERADORA + ".apo_valor_subsidio";
    public static final String APO_VALOR_REALIZADO              = TB_ARQUIVO_PREVIA_OPERADORA + ".apo_valor_realizado";
    public static final String APO_VALOR_NAO_REALIZADO          = TB_ARQUIVO_PREVIA_OPERADORA + ".apo_valor_nao_realizado";
    public static final String APO_VALOR_TOTAL                  = TB_ARQUIVO_PREVIA_OPERADORA + ".apo_valor_total";
    public static final String APO_PERIODO_COBRANCA             = TB_ARQUIVO_PREVIA_OPERADORA + ".apo_periodo_cobranca";

    public static final String TB_ATENDIMENTO                   = "tb_atendimento";
    public static final String ATE_CODIGO                       = TB_ATENDIMENTO + ".ate_codigo";
    public static final String ATE_USU_CODIGO                   = TB_ATENDIMENTO + ".usu_codigo";
    public static final String ATE_NOME_USUARIO                 = TB_ATENDIMENTO + ".ate_nome_usuario";
    public static final String ATE_EMAIL_USUARIO                = TB_ATENDIMENTO + ".ate_email_usuario";
    public static final String ATE_DATA_INICIO                  = TB_ATENDIMENTO + ".ate_data_inicio";
    public static final String ATE_DATA_ULT_MENSAGEM            = TB_ATENDIMENTO + ".ate_data_ult_mensagem";
    public static final String ATE_ID_SESSAO                    = TB_ATENDIMENTO + ".ate_id_sessao";
    public static final String ATE_IP_ACESSO                    = TB_ATENDIMENTO + ".ate_ip_acesso";

    public static final String TB_ATENDIMENTO_MENSAGEM          = "tb_atendimento_mensagem";
    public static final String AME_ATE_CODIGO                   = TB_ATENDIMENTO_MENSAGEM + ".ate_codigo";
    public static final String AME_SEQUENCIA                    = TB_ATENDIMENTO_MENSAGEM + ".ame_sequencia";
    public static final String AME_DATA                         = TB_ATENDIMENTO_MENSAGEM + ".ame_data";
    public static final String AME_TEXTO                        = TB_ATENDIMENTO_MENSAGEM + ".ame_texto";
    public static final String AME_BOT                          = TB_ATENDIMENTO_MENSAGEM + ".ame_bot";

    public static final String TB_AUTORIZACAO_DESCONTO          = "tb_aut_desconto";
    public static final String ADE_CODIGO                       = TB_AUTORIZACAO_DESCONTO + ".ade_codigo";
    public static final String ADE_SAD_CODIGO                   = TB_AUTORIZACAO_DESCONTO + ".sad_codigo";
    public static final String ADE_VCO_CODIGO                   = TB_AUTORIZACAO_DESCONTO + ".vco_codigo";
    public static final String ADE_COR_CODIGO                   = TB_AUTORIZACAO_DESCONTO + ".cor_codigo";
    public static final String ADE_RSE_CODIGO                   = TB_AUTORIZACAO_DESCONTO + ".rse_codigo";
    public static final String ADE_USU_CODIGO                   = TB_AUTORIZACAO_DESCONTO + ".usu_codigo";
    public static final String ADE_DATA                         = TB_AUTORIZACAO_DESCONTO + ".ade_data";
    public static final String ADE_VLR                          = TB_AUTORIZACAO_DESCONTO + ".ade_vlr";
    public static final String ADE_VLR_REF                      = TB_AUTORIZACAO_DESCONTO + ".ade_vlr_ref";
    public static final String ADE_PRAZO                        = TB_AUTORIZACAO_DESCONTO + ".ade_prazo";
    public static final String ADE_PRAZO_REF                    = TB_AUTORIZACAO_DESCONTO + ".ade_prazo_ref";
    public static final String ADE_ANO_MES_INI                  = TB_AUTORIZACAO_DESCONTO + ".ade_ano_mes_ini";
    public static final String ADE_ANO_MES_FIM                  = TB_AUTORIZACAO_DESCONTO + ".ade_ano_mes_fim";
    public static final String ADE_IDENTIFICADOR                = TB_AUTORIZACAO_DESCONTO + ".ade_identificador";
    public static final String ADE_NUMERO                       = TB_AUTORIZACAO_DESCONTO + ".ade_numero";
    public static final String ADE_TIPO_VLR                     = TB_AUTORIZACAO_DESCONTO + ".ade_tipo_vlr";
    public static final String ADE_INT_FOLHA                    = TB_AUTORIZACAO_DESCONTO + ".ade_int_folha";
    public static final String ADE_INC_MARGEM                   = TB_AUTORIZACAO_DESCONTO + ".ade_inc_margem";
    public static final String ADE_PRD_PAGAS                    = TB_AUTORIZACAO_DESCONTO + ".ade_prd_pagas";
    public static final String ADE_PRD_PAGAS_TOTAL              = TB_AUTORIZACAO_DESCONTO + ".ade_prd_pagas_total";
    public static final String ADE_INDICE                       = TB_AUTORIZACAO_DESCONTO + ".ade_indice";
    public static final String ADE_VLR_TAC                      = TB_AUTORIZACAO_DESCONTO + ".ade_vlr_tac";
    public static final String ADE_VLR_IOF                      = TB_AUTORIZACAO_DESCONTO + ".ade_vlr_iof";
    public static final String ADE_VLR_LIQUIDO                  = TB_AUTORIZACAO_DESCONTO + ".ade_vlr_liquido";
    public static final String ADE_VLR_MENS_VINC                = TB_AUTORIZACAO_DESCONTO + ".ade_vlr_mens_vinc";
    public static final String ADE_DATA_HORA_OCORRENCIA         = TB_AUTORIZACAO_DESCONTO + ".ade_data_hora_ocorrencia";
    public static final String ADE_VLR_SEG_PRESTAMISTA          = TB_AUTORIZACAO_DESCONTO + ".ade_vlr_seg_prestamista";
    public static final String ADE_INDICE_EXP                   = TB_AUTORIZACAO_DESCONTO + ".ade_indice_exp";
    public static final String ADE_DATA_REF                     = TB_AUTORIZACAO_DESCONTO + ".ade_data_ref";
    public static final String ADE_ANO_MES_INI_FOLHA            = TB_AUTORIZACAO_DESCONTO + ".ade_ano_mes_ini_folha";
    public static final String ADE_ANO_MES_FIM_FOLHA            = TB_AUTORIZACAO_DESCONTO + ".ade_ano_mes_fim_folha";
    public static final String ADE_VLR_FOLHA                    = TB_AUTORIZACAO_DESCONTO + ".ade_vlr_folha";
    public static final String ADE_PRAZO_FOLHA                  = TB_AUTORIZACAO_DESCONTO + ".ade_prazo_folha";
    public static final String ADE_ANO_MES_INI_REF              = TB_AUTORIZACAO_DESCONTO + ".ade_ano_mes_ini_ref";
    public static final String ADE_ANO_MES_FIM_REF              = TB_AUTORIZACAO_DESCONTO + ".ade_ano_mes_fim_ref";
    public static final String ADE_CARENCIA_FINAL               = TB_AUTORIZACAO_DESCONTO + ".ade_carencia_final";
    public static final String ADE_COD_REG                      = TB_AUTORIZACAO_DESCONTO + ".ade_cod_reg";
    public static final String ADE_TAXA_JUROS                   = TB_AUTORIZACAO_DESCONTO + ".ade_taxa_juros";
    public static final String ADE_PAGA                         = TB_AUTORIZACAO_DESCONTO + ".ade_paga";
    public static final String ADE_BANCO                        = TB_AUTORIZACAO_DESCONTO + ".ade_banco";
    public static final String ADE_AGENCIA                      = TB_AUTORIZACAO_DESCONTO + ".ade_agencia";
    public static final String ADE_CONTA                        = TB_AUTORIZACAO_DESCONTO + ".ade_conta";
    public static final String ADE_AGENCIA_DV                   = TB_AUTORIZACAO_DESCONTO + ".ade_agencia_dv";
    public static final String ADE_CONTA_DV                     = TB_AUTORIZACAO_DESCONTO + ".ade_conta_dv";
    public static final String ADE_VLR_SDO_MOV                  = TB_AUTORIZACAO_DESCONTO + ".ade_vlr_sdo_mov";
    public static final String ADE_VLR_SDO_RET                  = TB_AUTORIZACAO_DESCONTO + ".ade_vlr_sdo_ret";
    public static final String ADE_PODE_CONFIRMAR               = TB_AUTORIZACAO_DESCONTO + ".ade_pode_confirmar";
    public static final String ADE_CARENCIA                     = TB_AUTORIZACAO_DESCONTO + ".ade_carencia";
    public static final String ADE_TIPO_TAXA                    = TB_AUTORIZACAO_DESCONTO + ".ade_tipo_taxa";
    public static final String ADE_VLR_PERCENTUAL               = TB_AUTORIZACAO_DESCONTO + ".ade_vlr_percentual";
    public static final String ADE_DATA_ULT_CONCILIACAO         = TB_AUTORIZACAO_DESCONTO + ".ade_data_ult_conciliacao";
    public static final String ADE_MNE_CODIGO                   = TB_AUTORIZACAO_DESCONTO + ".mne_codigo";
    public static final String ADE_PERIODICIDADE                = TB_AUTORIZACAO_DESCONTO + ".ade_periodicidade";
    public static final String ADE_VLR_DEVIDO                   = TB_AUTORIZACAO_DESCONTO + ".ade_vlr_devido";
    public static final String ADE_DATA_CONFIRMACAO             = TB_AUTORIZACAO_DESCONTO + ".ade_data_confirmacao";
    public static final String ADE_DATA_DEFERIMENTO             = TB_AUTORIZACAO_DESCONTO + ".ade_data_deferimento";
    public static final String ADE_DATA_EXCLUSAO                = TB_AUTORIZACAO_DESCONTO + ".ade_data_exclusao";
    public static final String ADE_VLR_PARCELA_FOLHA            = TB_AUTORIZACAO_DESCONTO + ".ade_vlr_parcela_folha";
    public static final String ADE_CID_CODIGO                   = TB_AUTORIZACAO_DESCONTO + ".cid_codigo";
    public static final String ADE_EXPORTACAO                   = TB_AUTORIZACAO_DESCONTO + ".ade_exportacao";
    public static final String ADE_DATA_STATUS                  = TB_AUTORIZACAO_DESCONTO + ".ade_data_status";
    public static final String ADE_CBE_CODIGO                   = TB_AUTORIZACAO_DESCONTO + ".cbe_codigo";
    public static final String ADE_TLA_CODIGO                   = TB_AUTORIZACAO_DESCONTO + ".tla_codigo";
    public static final String ADE_DATA_REATIVACAO_AUTOMATICA   = TB_AUTORIZACAO_DESCONTO + ".ade_data_reativacao_automatica";
    public static final String ADE_ULT_PERIODO_EXPORTACAO       = TB_AUTORIZACAO_DESCONTO + ".ade_ult_periodo_exportacao";
    public static final String ADE_DATA_NOTIFICACAO_CSE         = TB_AUTORIZACAO_DESCONTO + ".ade_data_notificacao_cse";
    public static final String ADE_DATA_LIBERACAO_VALOR         = TB_AUTORIZACAO_DESCONTO + ".ade_data_liberacao_valor";

    public static final String TB_BLOQUEIO_POSTO_CSA_SVC        = "tb_bloqueio_posto_csa_svc";
    public static final String BPC_CSA_CODIGO                   = TB_BLOQUEIO_POSTO_CSA_SVC + ".csa_codigo";
    public static final String BPC_SVC_CODIGO                   = TB_BLOQUEIO_POSTO_CSA_SVC + ".svc_codigo";
    public static final String BPC_POS_CODIGO                   = TB_BLOQUEIO_POSTO_CSA_SVC + ".pos_codigo";
    public static final String BPC_BLOQ_SOLICITACAO             = TB_BLOQUEIO_POSTO_CSA_SVC + ".bpc_bloq_solicitacao";
    public static final String BPC_BLOQ_RESERVA                 = TB_BLOQUEIO_POSTO_CSA_SVC + ".bpc_bloq_reserva";

    public static final String TB_BLOQUEIO_REPASSE_FUNCAO       = "tb_bloqueio_repasse_funcao";
    public static final String BRF_PAP_CODIGO_ORIGEM            = TB_BLOQUEIO_REPASSE_FUNCAO + ".pap_codigo_origem";
    public static final String BRF_PAP_CODIGO_DESTINO           = TB_BLOQUEIO_REPASSE_FUNCAO + ".pap_codigo_destino";
    public static final String BRF_FUN_CODIGO                   = TB_BLOQUEIO_REPASSE_FUNCAO + ".fun_codigo";

    public static final String TB_BLOQUEIO_RSE_FUN              = "tb_bloqueio_rse_fun";
    public static final String BRS_RSE_CODIGO                   = TB_BLOQUEIO_RSE_FUN + ".rse_codigo";
    public static final String BRS_FUN_CODIGO                   = TB_BLOQUEIO_RSE_FUN + ".fun_codigo";
    public static final String BRS_DATA_LIMITE                  = TB_BLOQUEIO_RSE_FUN + ".brs_data_limite";

    public static final String TB_BOLETO_SERVIDOR               = "tb_boleto_servidor";
    public static final String BOS_CODIGO                       = TB_BOLETO_SERVIDOR + ".bos_codigo";
    public static final String BOS_SER_CODIGO                   = TB_BOLETO_SERVIDOR + ".ser_codigo";
    public static final String BOS_CSA_CODIGO                   = TB_BOLETO_SERVIDOR + ".csa_codigo";
    public static final String BOS_USU_CODIGO                   = TB_BOLETO_SERVIDOR + ".usu_codigo";
    public static final String BOS_ARQ_CODIGO                   = TB_BOLETO_SERVIDOR + ".arq_codigo";
    public static final String BOS_DATA_UPLOAD                  = TB_BOLETO_SERVIDOR + ".bos_data_upload";
    public static final String BOS_DATA_DOWNLOAD                = TB_BOLETO_SERVIDOR + ".bos_data_download";
    public static final String BOS_DATA_EXCLUSAO                = TB_BOLETO_SERVIDOR + ".bos_data_exclusao";

    public static final String TB_CALENDARIO                    = "tb_calendario";
    public static final String CAL_DATA                         = TB_CALENDARIO + ".cal_data";
    public static final String CAL_DESCRICAO                    = TB_CALENDARIO + ".cal_descricao";
    public static final String CAL_DIA_UTIL                     = TB_CALENDARIO + ".cal_dia_util";

    public static final String TB_CALENDARIO_BASE               = "tb_calendario_base";
    public static final String CAB_DATA                         = TB_CALENDARIO_BASE + ".cab_data";
    public static final String CAB_DESCRICAO                    = TB_CALENDARIO_BASE + ".cab_descricao";
    public static final String CAB_DIA_UTIL                     = TB_CALENDARIO_BASE + ".cab_dia_util";

    public static final String TB_CALENDARIO_FOLHA_CSE          = "tb_calendario_folha_cse";
    public static final String CFC_CSE_CODIGO                   = TB_CALENDARIO_FOLHA_CSE + ".cse_codigo";
    public static final String CFC_PERIODO                      = TB_CALENDARIO_FOLHA_CSE + ".cfc_periodo";
    public static final String CFC_DIA_CORTE                    = TB_CALENDARIO_FOLHA_CSE + ".cfc_dia_corte";
    public static final String CFC_DATA_INI                     = TB_CALENDARIO_FOLHA_CSE + ".cfc_data_ini";
    public static final String CFC_DATA_FIM                     = TB_CALENDARIO_FOLHA_CSE + ".cfc_data_fim";
    public static final String CFC_DATA_FIM_AJUSTES             = TB_CALENDARIO_FOLHA_CSE + ".cfc_data_fim_ajustes";
    public static final String CFC_APENAS_REDUCOES              = TB_CALENDARIO_FOLHA_CSE + ".cfc_apenas_reducoes";
    public static final String CFC_DATA_PREVISTA_RETORNO        = TB_CALENDARIO_FOLHA_CSE + ".cfc_data_prevista_retorno";
    public static final String CFC_DATA_INI_FISCAL              = TB_CALENDARIO_FOLHA_CSE + ".cfc_data_ini_fiscal";
    public static final String CFC_DATA_FIM_FISCAL              = TB_CALENDARIO_FOLHA_CSE + ".cfc_data_fim_fiscal";
    public static final String CFC_NUM_PERIODO                  = TB_CALENDARIO_FOLHA_CSE + ".cfc_num_periodo";

    public static final String TB_CALENDARIO_FOLHA_EST          = "tb_calendario_folha_est";
    public static final String CFE_EST_CODIGO                   = TB_CALENDARIO_FOLHA_EST + ".est_codigo";
    public static final String CFE_PERIODO                      = TB_CALENDARIO_FOLHA_EST + ".cfe_periodo";
    public static final String CFE_DIA_CORTE                    = TB_CALENDARIO_FOLHA_EST + ".cfe_dia_corte";
    public static final String CFE_DATA_INI                     = TB_CALENDARIO_FOLHA_EST + ".cfe_data_ini";
    public static final String CFE_DATA_FIM                     = TB_CALENDARIO_FOLHA_EST + ".cfe_data_fim";
    public static final String CFE_DATA_FIM_AJUSTES             = TB_CALENDARIO_FOLHA_EST + ".cfe_data_fim_ajustes";
    public static final String CFE_APENAS_REDUCOES              = TB_CALENDARIO_FOLHA_EST + ".cfe_apenas_reducoes";
    public static final String CFE_DATA_PREVISTA_RETORNO        = TB_CALENDARIO_FOLHA_EST + ".cfe_data_prevista_retorno";
    public static final String CFE_DATA_INI_FISCAL              = TB_CALENDARIO_FOLHA_EST + ".cfe_data_ini_fiscal";
    public static final String CFE_DATA_FIM_FISCAL              = TB_CALENDARIO_FOLHA_EST + ".cfe_data_fim_fiscal";
    public static final String CFE_NUM_PERIODO                  = TB_CALENDARIO_FOLHA_EST + ".cfe_num_periodo";

    public static final String TB_CALENDARIO_FOLHA_ORG          = "tb_calendario_folha_org";
    public static final String CFO_ORG_CODIGO                   = TB_CALENDARIO_FOLHA_ORG + ".org_codigo";
    public static final String CFO_PERIODO                      = TB_CALENDARIO_FOLHA_ORG + ".cfo_periodo";
    public static final String CFO_DIA_CORTE                    = TB_CALENDARIO_FOLHA_ORG + ".cfo_dia_corte";
    public static final String CFO_DATA_INI                     = TB_CALENDARIO_FOLHA_ORG + ".cfo_data_ini";
    public static final String CFO_DATA_FIM                     = TB_CALENDARIO_FOLHA_ORG + ".cfo_data_fim";
    public static final String CFO_DATA_FIM_AJUSTES             = TB_CALENDARIO_FOLHA_ORG + ".cfo_data_fim_ajustes";
    public static final String CFO_APENAS_REDUCOES              = TB_CALENDARIO_FOLHA_ORG + ".cfo_apenas_reducoes";
    public static final String CFO_DATA_PREVISTA_RETORNO        = TB_CALENDARIO_FOLHA_ORG + ".cfo_data_prevista_retorno";
    public static final String CFO_DATA_INI_FISCAL              = TB_CALENDARIO_FOLHA_ORG + ".cfo_data_ini_fiscal";
    public static final String CFO_DATA_FIM_FISCAL              = TB_CALENDARIO_FOLHA_ORG + ".cfo_data_fim_fiscal";
    public static final String CFO_NUM_PERIODO                  = TB_CALENDARIO_FOLHA_ORG + ".cfo_num_periodo";

    public static final String TB_CARGO_REGISTRO_SERVIDOR       = "tb_cargo_registro_servidor";
    public static final String CRS_CODIGO                       = TB_CARGO_REGISTRO_SERVIDOR + ".crs_codigo";
    public static final String CRS_DESCRICAO                    = TB_CARGO_REGISTRO_SERVIDOR + ".crs_descricao";
    public static final String CRS_IDENTIFICADOR                = TB_CARGO_REGISTRO_SERVIDOR + ".crs_identificador";
    public static final String CRS_VLR_DESC_MAX                 = TB_CARGO_REGISTRO_SERVIDOR + ".crs_vlr_desc_max";
    public static final String CRS_VLR_REFERENCIA               = TB_CARGO_REGISTRO_SERVIDOR + ".crs_vlr_referencia";

    public static final String TB_CASAMENTO_MARGEM              = "tb_casamento_margem";
    public static final String CAM_GRUPO                        = TB_CASAMENTO_MARGEM + ".cam_grupo";
    public static final String CAM_MAR_CODIGO                   = TB_CASAMENTO_MARGEM + ".mar_codigo";
    public static final String CAM_TIPO                         = TB_CASAMENTO_MARGEM + ".cam_tipo";
    public static final String CAM_SEQUENCIA                    = TB_CASAMENTO_MARGEM + ".cam_sequencia";

    public static final String TB_CEP                           = "tb_cep";
    public static final String CEP_CODIGO                       = TB_CEP+ ".cep_codigo";
    public static final String CEP_LOGRADOURO                   = TB_CEP+ ".cep_logradouro";
    public static final String CEP_BAIRRO                       = TB_CEP+ ".cep_bairro";
    public static final String CEP_CIDADE                       = TB_CEP+ ".cep_cidade";
    public static final String CEP_ESTADO                       = TB_CEP+ ".cep_estado";
    public static final String CEP_ESTADO_SIGLA                 = TB_CEP+ ".cep_estado_sigla";

    public static final String TB_CHAVE_CRIPTOGRAFIA_ARQUIVO    = "tb_chave_criptografia_arquivo";
    public static final String CAA_PAP_CODIGO                   = TB_CHAVE_CRIPTOGRAFIA_ARQUIVO + ".pap_codigo";
    public static final String CAA_TAR_CODIGO                   = TB_CHAVE_CRIPTOGRAFIA_ARQUIVO + ".tar_codigo";
    public static final String CAA_CODIGO_ENT                   = TB_CHAVE_CRIPTOGRAFIA_ARQUIVO + ".caa_codigo_ent";
    public static final String CAA_CHAVE                        = TB_CHAVE_CRIPTOGRAFIA_ARQUIVO + ".caa_chave";
    public static final String CAA_DATA                         = TB_CHAVE_CRIPTOGRAFIA_ARQUIVO + ".caa_data";

    public static final String TB_COEFICIENTE                   = "tb_coeficiente";
    public static final String CFT_CODIGO                       = TB_COEFICIENTE + ".cft_codigo";
    public static final String CFT_PRZ_CSA_CODIGO               = TB_COEFICIENTE + ".prz_csa_codigo";
    public static final String CFT_DIA                          = TB_COEFICIENTE + ".cft_dia";
    public static final String CFT_VLR                          = TB_COEFICIENTE + ".cft_vlr";
    public static final String CFT_DATA_INI_VIG                 = TB_COEFICIENTE + ".cft_data_ini_vig";
    public static final String CFT_DATA_FIM_VIG                 = TB_COEFICIENTE + ".cft_data_fim_vig";
    public static final String CFT_DATA_CADASTRO                = TB_COEFICIENTE + ".cft_data_cadastro";
    public static final String CFT_VLR_REF                      = TB_COEFICIENTE + ".cft_vlr_ref";
    public static final String CFT_VLR_MINIMO                   = TB_COEFICIENTE + ".cft_vlr_minimo";

    public static final String TB_COEFICIENTE_ATIVO             = "tb_coeficiente_ativo";
    public static final String CFA_CODIGO                       = TB_COEFICIENTE_ATIVO + ".cft_codigo";
    public static final String CFA_PRZ_CSA_CODIGO               = TB_COEFICIENTE_ATIVO + ".prz_csa_codigo";
    public static final String CFA_DIA                          = TB_COEFICIENTE_ATIVO + ".cft_dia";
    public static final String CFA_VLR                          = TB_COEFICIENTE_ATIVO + ".cft_vlr";
    public static final String CFA_DATA_INI_VIG                 = TB_COEFICIENTE_ATIVO + ".cft_data_ini_vig";
    public static final String CFA_DATA_FIM_VIG                 = TB_COEFICIENTE_ATIVO + ".cft_data_fim_vig";
    public static final String CFA_DATA_CADASTRO                = TB_COEFICIENTE_ATIVO + ".cft_data_cadastro";
    public static final String CFA_VLR_REF                      = TB_COEFICIENTE_ATIVO + ".cft_vlr_ref";
    public static final String CFA_VLR_MINIMO                   = TB_COEFICIENTE_ATIVO + ".cft_vlr_minimo";

    public static final String TB_COEFICIENTE_CORRECAO          = "tb_coeficiente_correcao";
    public static final String CCR_TCC_CODIGO                   = TB_COEFICIENTE_CORRECAO + ".tcc_codigo";
    public static final String CCR_VLR                          = TB_COEFICIENTE_CORRECAO + ".ccr_vlr";
    public static final String CCR_MES                          = TB_COEFICIENTE_CORRECAO + ".ccr_mes";
    public static final String CCR_ANO                          = TB_COEFICIENTE_CORRECAO + ".ccr_ano";
    public static final String CCR_VLR_ACUMULADO                = TB_COEFICIENTE_CORRECAO + ".ccr_vlr_acumulado";

    public static final String TB_TIPO_COEFICIENTE_CORRECAO     = "tb_tipo_coeficiente_correcao";
    public static final String TCC_CODIGO                       = TB_TIPO_COEFICIENTE_CORRECAO + ".tcc_codigo";
    public static final String TCC_DESCRICAO                    = TB_TIPO_COEFICIENTE_CORRECAO + ".tcc_descricao";
    public static final String TCC_FORMA_CALC                   = TB_TIPO_COEFICIENTE_CORRECAO + ".tcc_forma_calc";

    public static final String TB_COEFICIENTE_DESCONTO          = "tb_coeficiente_desconto";
    public static final String CDE_CODIGO                       = TB_COEFICIENTE_DESCONTO + ".cde_codigo";
    public static final String CDE_ADE_CODIGO                   = TB_COEFICIENTE_DESCONTO + ".ade_codigo";
    public static final String CDE_CFT_CODIGO                   = TB_COEFICIENTE_DESCONTO + ".cft_codigo";
    public static final String CDE_VLR_LIBERADO                 = TB_COEFICIENTE_DESCONTO + ".cde_vlr_liberado";
    public static final String CDE_VLR_LIBERADO_CALC            = TB_COEFICIENTE_DESCONTO + ".cde_vlr_liberado_calc";
    public static final String CDE_TXT_CONTATO                  = TB_COEFICIENTE_DESCONTO + ".cde_txt_contato";
    public static final String CDE_RANKING                      = TB_COEFICIENTE_DESCONTO + ".cde_ranking";
    public static final String CDE_FORMA_CRED                   = TB_COEFICIENTE_DESCONTO + ".cde_forma_cred";
    public static final String CDE_BCO_CODIGO                   = TB_COEFICIENTE_DESCONTO + ".bco_codigo";
    public static final String CDE_AGENCIA_CRED                 = TB_COEFICIENTE_DESCONTO + ".cde_agencia_cred";
    public static final String CDE_AGENCIA_DV_CRED              = TB_COEFICIENTE_DESCONTO + ".cde_agencia_dv_cred";
    public static final String CDE_CONTA_CRED                   = TB_COEFICIENTE_DESCONTO + ".cde_conta_cred";
    public static final String CDE_CONTA_DV_CRED                = TB_COEFICIENTE_DESCONTO + ".cde_conta_dv_cred";
    public static final String CDE_NOME_IND                     = TB_COEFICIENTE_DESCONTO + ".cde_nome_ind";
    public static final String CDE_CPF_IND                      = TB_COEFICIENTE_DESCONTO + ".cde_cpf_ind";
    public static final String CDE_VLR_TAC                      = TB_COEFICIENTE_DESCONTO + ".cde_vlr_tac";
    public static final String CDE_VLR_IOF                      = TB_COEFICIENTE_DESCONTO + ".cde_vlr_iof";
    public static final String CDE_VLR_MENS_VINC                = TB_COEFICIENTE_DESCONTO + ".cde_vlr_mens_vinc";

    public static final String TB_OCORRENCIA_COEFICIENTE        = "tb_ocorrencia_coeficiente";
    public static final String OCF_CODIGO                       = TB_OCORRENCIA_COEFICIENTE + ".ocf_codigo";
    public static final String OCF_SVC_CODIGO                   = TB_OCORRENCIA_COEFICIENTE + ".svc_codigo";
    public static final String OCF_CSA_CODIGO                   = TB_OCORRENCIA_COEFICIENTE + ".csa_codigo";
    public static final String OCF_USU_CODIGO                   = TB_OCORRENCIA_COEFICIENTE + ".usu_codigo";
    public static final String OCF_TOC_CODIGO                   = TB_OCORRENCIA_COEFICIENTE + ".toc_codigo";
    public static final String OCF_DATA                         = TB_OCORRENCIA_COEFICIENTE + ".ocf_data";
    public static final String OCF_DATA_INICIO_VIG              = TB_OCORRENCIA_COEFICIENTE + ".ocf_data_ini_vig";
    public static final String OCF_DATA_FIM_VIG                 = TB_OCORRENCIA_COEFICIENTE + ".ocf_data_fim_vig";
    public static final String OCF_OBS                          = TB_OCORRENCIA_COEFICIENTE + ".ocf_obs";
    public static final String OCF_IP_ACESSO                    = TB_OCORRENCIA_COEFICIENTE + ".ocf_ip_acesso";

    public static final String TB_COMP_MARGEM                   = "tb_comp_margem";
    public static final String CMA_CODIGO                       = TB_COMP_MARGEM + ".cma_codigo";
    public static final String CMA_RSE_CODIGO                   = TB_COMP_MARGEM + ".rse_codigo";
    public static final String CMA_VCT_CODIGO                   = TB_COMP_MARGEM + ".vct_codigo";
    public static final String CMA_VLR                          = TB_COMP_MARGEM + ".cma_vlr";
    public static final String CMA_QUANTIDADE                   = TB_COMP_MARGEM + ".cma_quantidade";
    public static final String CMA_VRS_CODIGO                   = TB_COMP_MARGEM + ".vrs_codigo";
    public static final String CMA_CRS_CODIGO                   = TB_COMP_MARGEM + ".crs_codigo";
    public static final String CMA_VINCULO                      = TB_COMP_MARGEM + ".cma_vinculo";

    public static final String TB_CONSIGNANTE                   = "tb_consignante";
    public static final String CSE_CODIGO                       = TB_CONSIGNANTE + ".cse_codigo";
    public static final String CSE_IDENTIFICADOR                = TB_CONSIGNANTE + ".cse_identificador";
    public static final String CSE_NOME                         = TB_CONSIGNANTE + ".cse_nome";
    public static final String CSE_CNPJ                         = TB_CONSIGNANTE + ".cse_cnpj";
    public static final String CSE_EMAIL                        = TB_CONSIGNANTE + ".cse_email";
    public static final String CSE_EMAIL_FOLHA                  = TB_CONSIGNANTE + ".cse_email_folha";
    public static final String CSE_RESPONSAVEL                  = TB_CONSIGNANTE + ".cse_responsavel";
    public static final String CSE_RESPONSAVEL_2                = TB_CONSIGNANTE + ".cse_responsavel_2";
    public static final String CSE_RESPONSAVEL_3                = TB_CONSIGNANTE + ".cse_responsavel_3";
    public static final String CSE_RESP_CARGO                   = TB_CONSIGNANTE + ".cse_resp_cargo";
    public static final String CSE_RESP_CARGO_2                 = TB_CONSIGNANTE + ".cse_resp_cargo_2";
    public static final String CSE_RESP_CARGO_3                 = TB_CONSIGNANTE + ".cse_resp_cargo_3";
    public static final String CSE_RESP_TELEFONE                = TB_CONSIGNANTE + ".cse_resp_telefone";
    public static final String CSE_RESP_TELEFONE_2              = TB_CONSIGNANTE + ".cse_resp_telefone_2";
    public static final String CSE_RESP_TELEFONE_3              = TB_CONSIGNANTE + ".cse_resp_telefone_3";
    public static final String CSE_LOGRADOURO                   = TB_CONSIGNANTE + ".cse_logradouro";
    public static final String CSE_NRO                          = TB_CONSIGNANTE + ".cse_nro";
    public static final String CSE_COMPL                        = TB_CONSIGNANTE + ".cse_compl";
    public static final String CSE_BAIRRO                       = TB_CONSIGNANTE + ".cse_bairro";
    public static final String CSE_CIDADE                       = TB_CONSIGNANTE + ".cse_cidade";
    public static final String CSE_UF                           = TB_CONSIGNANTE + ".cse_uf";
    public static final String CSE_CEP                          = TB_CONSIGNANTE + ".cse_cep";
    public static final String CSE_TEL                          = TB_CONSIGNANTE + ".cse_tel";
    public static final String CSE_FAX                          = TB_CONSIGNANTE + ".cse_fax";
    public static final String CSE_ATIVO                        = TB_CONSIGNANTE + ".cse_ativo";
    public static final String CSE_LICENCA                      = TB_CONSIGNANTE + ".cse_licenca";
    public static final String CSE_IP_ACESSO                    = TB_CONSIGNANTE + ".cse_ip_acesso";
    public static final String CSE_DDNS_ACESSO                  = TB_CONSIGNANTE + ".cse_ddns_acesso";
    public static final String CSE_IDENTIFICADOR_INTERNO        = TB_CONSIGNANTE + ".cse_identificador_interno";
    public static final String CSE_DATA_COBRANCA                = TB_CONSIGNANTE + ".cse_data_cobranca";
    public static final String CSE_CERTIFICADO_CENTRALIZADOR    = TB_CONSIGNANTE + ".cse_certificado_centralizador";
    public static final String CSE_CERTIFICADO_CENTRAL_MOBILE   = TB_CONSIGNANTE + ".cse_certificado_central_mobile";
    public static final String CSE_SISTEMA_FOLHA                = TB_CONSIGNANTE + ".cse_sistema_folha";
    public static final String CSE_BCO_CODIGO                   = TB_CONSIGNANTE + ".bco_codigo";
    // Mantido por compatibilidade, serï¿½ removido no futuro quando o protocolo entre Centralizador e eConsig usar somente a versï¿½o com CA
    public static final String CSE_RSA_PUBLIC_KEY_CENTRALIZADOR = TB_CONSIGNANTE + ".cse_rsa_public_key_centralizador";
    public static final String CSE_RSA_MODULUS_CENTRALIZADOR    = TB_CONSIGNANTE + ".cse_rsa_modulus_centralizador";
    public static final String CSE_TCE_CODIGO                   = TB_CONSIGNANTE + ".tce_codigo";
    public static final String CSE_FOLHA                        = TB_CONSIGNANTE + ".cse_folha";
    public static final String CSE_EMAIL_VALIDAR_SERVIDOR       = TB_CONSIGNANTE + ".cse_email_validar_servidor";
    public static final String CSE_PROJETO_INADIMPLENCIA        = TB_CONSIGNANTE + ".cse_projeto_inadimplencia";
    public static final String CSE_DATA_ATUALIZACAO_CADASTRAL   = TB_CONSIGNANTE + ".cse_data_atualizacao_cadastral";

    public static final String TB_CONSIGNATARIA                 = "tb_consignataria";
    public static final String CSA_CODIGO                       = TB_CONSIGNATARIA + ".csa_codigo";
    public static final String CSA_IDENTIFICADOR                = TB_CONSIGNATARIA + ".csa_identificador";
    public static final String CSA_NOME                         = TB_CONSIGNATARIA + ".csa_nome";
    public static final String CSA_CNPJ                         = TB_CONSIGNATARIA + ".csa_cnpj";
    public static final String CSA_CNPJ_CTA                     = TB_CONSIGNATARIA + ".csa_cnpj_cta";
    public static final String CSA_EMAIL                        = TB_CONSIGNATARIA + ".csa_email";
    public static final String CSA_RESPONSAVEL                  = TB_CONSIGNATARIA + ".csa_responsavel";
    public static final String CSA_RESPONSAVEL_2                = TB_CONSIGNATARIA + ".csa_responsavel_2";
    public static final String CSA_RESPONSAVEL_3                = TB_CONSIGNATARIA + ".csa_responsavel_3";
    public static final String CSA_RESP_CARGO                   = TB_CONSIGNATARIA + ".csa_resp_cargo";
    public static final String CSA_RESP_CARGO_2                 = TB_CONSIGNATARIA + ".csa_resp_cargo_2";
    public static final String CSA_RESP_CARGO_3                 = TB_CONSIGNATARIA + ".csa_resp_cargo_3";
    public static final String CSA_RESP_TELEFONE                = TB_CONSIGNATARIA + ".csa_resp_telefone";
    public static final String CSA_RESP_TELEFONE_2              = TB_CONSIGNATARIA + ".csa_resp_telefone_2";
    public static final String CSA_RESP_TELEFONE_3              = TB_CONSIGNATARIA + ".csa_resp_telefone_3";
    public static final String CSA_LOGRADOURO                   = TB_CONSIGNATARIA + ".csa_logradouro";
    public static final String CSA_NRO                          = TB_CONSIGNATARIA + ".csa_nro";
    public static final String CSA_COMPL                        = TB_CONSIGNATARIA + ".csa_compl";
    public static final String CSA_BAIRRO                       = TB_CONSIGNATARIA + ".csa_bairro";
    public static final String CSA_CIDADE                       = TB_CONSIGNATARIA + ".csa_cidade";
    public static final String CSA_UF                           = TB_CONSIGNATARIA + ".csa_uf";
    public static final String CSA_CEP                          = TB_CONSIGNATARIA + ".csa_cep";
    public static final String CSA_TEL                          = TB_CONSIGNATARIA + ".csa_tel";
    public static final String CSA_FAX                          = TB_CONSIGNATARIA + ".csa_fax";
    public static final String CSA_NRO_BCO                      = TB_CONSIGNATARIA + ".csa_nro_bco";
    public static final String CSA_NRO_CTA                      = TB_CONSIGNATARIA + ".csa_nro_cta";
    public static final String CSA_NRO_AGE                      = TB_CONSIGNATARIA + ".csa_nro_age";
    public static final String CSA_DIG_CTA                      = TB_CONSIGNATARIA + ".csa_dig_cta";
    public static final String CSA_ATIVO                        = TB_CONSIGNATARIA + ".csa_ativo";
    public static final String CSA_TXT_CONTATO                  = TB_CONSIGNATARIA + ".csa_txt_contato";
    public static final String CSA_NOME_ABREV                   = TB_CONSIGNATARIA + ".csa_nome_abrev";
    public static final String CSA_CONTATO                      = TB_CONSIGNATARIA + ".csa_contato";
    public static final String CSA_CONTATO_TEL                  = TB_CONSIGNATARIA + ".csa_contato_tel";
    public static final String CSA_ENDERECO_2                   = TB_CONSIGNATARIA + ".csa_endereco_2";
    public static final String CSA_TGC_CODIGO                   = TB_CONSIGNATARIA + ".tgc_codigo";
    public static final String CSA_IDENTIFICADOR_INTERNO        = TB_CONSIGNATARIA + ".csa_identificador_interno";
    public static final String CSA_DATA_EXPIRACAO               = TB_CONSIGNATARIA + ".csa_data_expiracao";
    public static final String CSA_DATA_EXPIRACAO_CADASTRAL     = TB_CONSIGNATARIA + ".csa_data_expiracao_cadastral";
    public static final String CSA_NRO_CONTRATO                 = TB_CONSIGNATARIA + ".csa_nro_contrato";
    public static final String CSA_IP_ACESSO                    = TB_CONSIGNATARIA + ".csa_ip_acesso";
    public static final String CSA_DDNS_ACESSO                  = TB_CONSIGNATARIA + ".csa_ddns_acesso";
    public static final String CSA_EXIGE_ENDERECO_ACESSO        = TB_CONSIGNATARIA + ".csa_exige_endereco_acesso";
    public static final String CSA_UNIDADE_ORGANIZACIONAL       = TB_CONSIGNATARIA + ".csa_unidade_organizacional";
    public static final String CSA_NRO_CONTRATO_ZETRA           = TB_CONSIGNATARIA + ".csa_nro_contrato_zetra";
    public static final String CSA_NCA_NATUREZA                 = TB_CONSIGNATARIA + ".nca_codigo";
    public static final String CSA_PROJETO_INADIMPLENCIA        = TB_CONSIGNATARIA + ".csa_projeto_inadimplencia";
    public static final String CSA_EMAIL_EXPIRACAO              = TB_CONSIGNATARIA + ".csa_email_expiracao";
    public static final String CSA_INSTRUCAO_ANEXO              = TB_CONSIGNATARIA + ".csa_instrucao_anexo";
    public static final String CSA_PERMITE_INCLUIR_ADE          = TB_CONSIGNATARIA + ".csa_permite_incluir_ade";
    public static final String CSA_DATA_ATUALIZACAO_CADASTRAL   = TB_CONSIGNATARIA + ".csa_data_atualizacao_cadastral";
    public static final String CSA_CODIGO_ANS                   = TB_CONSIGNATARIA + ".csa_codigo_ans";
    public static final String CSA_TMB_CODIGO                   = TB_CONSIGNATARIA + ".tmb_codigo";
    public static final String CSA_EMAIL_PROJ_INADIMPLENCIA     = TB_CONSIGNATARIA + ".csa_email_proj_inadimplencia";
    public static final String CSA_DATA_DESBLOQ_AUTOMATICO      = TB_CONSIGNATARIA + ".csa_data_desbloq_automatico";
    public static final String CSA_EMAIL_DESBLOQUEIO            = TB_CONSIGNATARIA + ".csa_email_desbloqueio";
    public static final String CSA_DATA_INICIO_CONTRATO         = TB_CONSIGNATARIA + ".csa_data_ini_contrato";
    public static final String CSA_DATA_RENOVACAO_CONTRATO      = TB_CONSIGNATARIA + ".csa_data_renovacao_contrato";
    public static final String CSA_NUMERO_PROCESSO_CONTRATO     = TB_CONSIGNATARIA + ".csa_num_processo";
    public static final String CSA_OBS_CONTRATO                 = TB_CONSIGNATARIA + ".csa_obs_contrato";
    public static final String CSA_PERMITE_API                  = TB_CONSIGNATARIA + ".csa_permite_api";
    public static final String CSA_WHATSAPP                     = TB_CONSIGNATARIA + ".csa_whatsapp";
    public static final String CSA_EMAIL_CONTATO                = TB_CONSIGNATARIA + ".csa_email_contato";
    public static final String CSA_CONSULTA_MARGEM_SEM_SENHA    = TB_CONSIGNATARIA + ".csa_consulta_margem_sem_senha";
    public static final String CSA_EMAIL_NOTIFICACAO_RCO        = TB_CONSIGNATARIA + ".csa_email_notificacao_rco";


    public static final String TB_CONSIGNATARIA_PERMITE_TDA     = "tb_consignataria_permite_tda";
    public static final String CPT_CSA_CODIGO                   = TB_CONSIGNATARIA_PERMITE_TDA + ".csa_codigo";
    public static final String CPT_TDA_CODIGO                   = TB_CONSIGNATARIA_PERMITE_TDA + ".tda_codigo";
    public static final String CPT_EXIBE                        = TB_CONSIGNATARIA_PERMITE_TDA + ".cpt_exibe";

    public static final String TB_CONSULTA_MDX                  = "tb_consulta_mdx";
    public static final String MDX_CODIGO                       = TB_CONSULTA_MDX + ".mdx_codigo";
    public static final String MDX_NOME                         = TB_CONSULTA_MDX + ".mdx_nome";
    public static final String MDX_DATA                         = TB_CONSULTA_MDX + ".mdx_data";
    public static final String MDX_ATIVO                        = TB_CONSULTA_MDX + ".mdx_ativo";
    public static final String MDX_QUERY                        = TB_CONSULTA_MDX + ".mdx_query";
    public static final String MDX_EXIBE_CSA                    = TB_CONSULTA_MDX + ".mdx_exibe_csa";
    public static final String MDX_CSA_CODIGO                   = TB_CONSULTA_MDX + ".csa_codigo";

    public static final String TB_CONTRACHEQUE_REGISTRO_SERVIDOR = "tb_contracheque_registro_ser";
    public static final String CCQ_RSE_CODIGO                    = TB_CONTRACHEQUE_REGISTRO_SERVIDOR + ".rse_codigo";
    public static final String CCQ_PERIODO                       = TB_CONTRACHEQUE_REGISTRO_SERVIDOR + ".ccq_periodo";
    public static final String CCQ_DATA_CARGA                    = TB_CONTRACHEQUE_REGISTRO_SERVIDOR + ".ccq_data_carga";
    public static final String CCQ_TEXTO                         = TB_CONTRACHEQUE_REGISTRO_SERVIDOR + ".ccq_texto";

    public static final String TB_CONVENIO                      = "tb_convenio";
    public static final String CNV_CODIGO                       = TB_CONVENIO + ".cnv_codigo";
    public static final String CNV_ORG_CODIGO                   = TB_CONVENIO + ".org_codigo";
    public static final String CNV_SCV_CODIGO                   = TB_CONVENIO + ".scv_codigo";
    public static final String CNV_SVC_CODIGO                   = TB_CONVENIO + ".svc_codigo";
    public static final String CNV_CSA_CODIGO                   = TB_CONVENIO + ".csa_codigo";
    public static final String CNV_VCE_CODIGO                   = TB_CONVENIO + ".vce_codigo";
    public static final String CNV_IDENTIFICADOR                = TB_CONVENIO + ".cnv_identificador";
    public static final String CNV_DESCRICAO                    = TB_CONVENIO + ".cnv_descricao";
    public static final String CNV_DATA_INI                     = TB_CONVENIO + ".cnv_data_ini";
    public static final String CNV_DATA_FIM                     = TB_CONVENIO + ".cnv_data_fim";
    public static final String CNV_COD_VERBA                    = TB_CONVENIO + ".cnv_cod_verba";
    public static final String CNV_COD_VERBA_REF                = TB_CONVENIO + ".cnv_cod_verba_ref";
    public static final String CNV_CONSOLIDA_DESCONTOS          = TB_CONVENIO + ".cnv_consolida_descontos";
    public static final String CNV_PRIORIDADE                   = TB_CONVENIO + ".cnv_prioridade";
    public static final String CNV_COD_VERBA_FERIAS             = TB_CONVENIO + ".cnv_cod_verba_ferias";
    public static final String CNV_COD_VERBA_DIRF               = TB_CONVENIO + ".cnv_cod_verba_dirf";

    public static final String TB_CORRESPONDENTE                = "tb_correspondente";
    public static final String COR_CODIGO                       = TB_CORRESPONDENTE + ".cor_codigo";
    public static final String COR_CSA_CODIGO                   = TB_CORRESPONDENTE + ".csa_codigo";
    public static final String COR_IDENTIFICADOR                = TB_CORRESPONDENTE + ".cor_identificador";
    public static final String COR_NOME                         = TB_CORRESPONDENTE + ".cor_nome";
    public static final String COR_EMAIL                        = TB_CORRESPONDENTE + ".cor_email";
    public static final String COR_RESPONSAVEL                  = TB_CORRESPONDENTE + ".cor_responsavel";
    public static final String COR_RESPONSAVEL_2                = TB_CORRESPONDENTE + ".cor_responsavel_2";
    public static final String COR_RESPONSAVEL_3                = TB_CORRESPONDENTE + ".cor_responsavel_3";
    public static final String COR_RESP_CARGO                   = TB_CORRESPONDENTE + ".cor_resp_cargo";
    public static final String COR_RESP_CARGO_2                 = TB_CORRESPONDENTE + ".cor_resp_cargo_2";
    public static final String COR_RESP_CARGO_3                 = TB_CORRESPONDENTE + ".cor_resp_cargo_3";
    public static final String COR_RESP_TELEFONE                = TB_CORRESPONDENTE + ".cor_resp_telefone";
    public static final String COR_RESP_TELEFONE_2              = TB_CORRESPONDENTE + ".cor_resp_telefone_2";
    public static final String COR_RESP_TELEFONE_3              = TB_CORRESPONDENTE + ".cor_resp_telefone_3";
    public static final String COR_LOGRADOURO                   = TB_CORRESPONDENTE + ".cor_logradouro";
    public static final String COR_NRO                          = TB_CORRESPONDENTE + ".cor_nro";
    public static final String COR_COMPL                        = TB_CORRESPONDENTE + ".cor_compl";
    public static final String COR_BAIRRO                       = TB_CORRESPONDENTE + ".cor_bairro";
    public static final String COR_CIDADE                       = TB_CORRESPONDENTE + ".cor_cidade";
    public static final String COR_UF                           = TB_CORRESPONDENTE + ".cor_uf";
    public static final String COR_CEP                          = TB_CORRESPONDENTE + ".cor_cep";
    public static final String COR_TEL                          = TB_CORRESPONDENTE + ".cor_tel";
    public static final String COR_FAX                          = TB_CORRESPONDENTE + ".cor_fax";
    public static final String COR_ATIVO                        = TB_CORRESPONDENTE + ".cor_ativo";
    public static final String COR_CNPJ                         = TB_CORRESPONDENTE + ".cor_cnpj";
    public static final String COR_IDENTIFICADOR_ANTIGO         = TB_CORRESPONDENTE + ".cor_identificador_antigo";
    public static final String COR_IP_ACESSO                    = TB_CORRESPONDENTE + ".cor_ip_acesso";
    public static final String COR_DDNS_ACESSO                  = TB_CORRESPONDENTE + ".cor_ddns_acesso";
    public static final String COR_EXIGE_ENDERECO_ACESSO        = TB_CORRESPONDENTE + ".cor_exige_endereco_acesso";
    public static final String COR_ECO_CODIGO                   = TB_CORRESPONDENTE + ".eco_codigo";


    public static final String TB_CORRESPONDENTE_CONVENIO       = "tb_correspondente_convenio";
    public static final String CRC_COR_CODIGO                   = TB_CORRESPONDENTE_CONVENIO + ".cor_codigo";
    public static final String CRC_CNV_CODIGO                   = TB_CORRESPONDENTE_CONVENIO + ".cnv_codigo";
    public static final String CRC_SCV_CODIGO                   = TB_CORRESPONDENTE_CONVENIO + ".scv_codigo";

    public static final String TB_DADOS_AUTORIZACAO_DESCONTO    = "tb_dados_autorizacao_desconto";
    public static final String DAD_ADE_CODIGO                   = TB_DADOS_AUTORIZACAO_DESCONTO + ".ade_codigo";
    public static final String DAD_TDA_CODIGO                   = TB_DADOS_AUTORIZACAO_DESCONTO + ".tda_codigo";
    public static final String DAD_VALOR                        = TB_DADOS_AUTORIZACAO_DESCONTO + ".dad_valor";

    public static final String TB_OCORRENCIA_DADOS_ADE          = "tb_ocorrencia_dados_ade";
    public static final String ODA_CODIGO                       = TB_OCORRENCIA_DADOS_ADE + ".oda_codigo";
    public static final String ODA_TOC_CODIGO                   = TB_OCORRENCIA_DADOS_ADE + ".toc_codigo";
    public static final String ODA_TDA_CODIGO                   = TB_OCORRENCIA_DADOS_ADE + ".tda_codigo";
    public static final String ODA_ADE_CODIGO                   = TB_OCORRENCIA_DADOS_ADE + ".ade_codigo";
    public static final String ODA_USU_CODIGO                   = TB_OCORRENCIA_DADOS_ADE + ".usu_codigo";
    public static final String ODA_DATA                         = TB_OCORRENCIA_DADOS_ADE + ".oda_data";
    public static final String ODA_OBS                          = TB_OCORRENCIA_DADOS_ADE + ".oda_obs";
    public static final String ODA_VALOR_ANT                    = TB_OCORRENCIA_DADOS_ADE + ".oda_valor_ant";
    public static final String ODA_VALOR_NOVO                   = TB_OCORRENCIA_DADOS_ADE + ".oda_valor_novo";
    public static final String ODA_IP_ACESSO                    = TB_OCORRENCIA_DADOS_ADE + ".oda_ip_acesso";

    public static final String TB_DADOS_SERVIDOR                = "tb_dados_servidor";
    public static final String DAS_SER_CODIGO                   = TB_DADOS_SERVIDOR + ".ser_codigo";
    public static final String DAS_TDA_CODIGO                   = TB_DADOS_SERVIDOR + ".tda_codigo";
    public static final String DAS_VALOR                        = TB_DADOS_SERVIDOR + ".das_valor";

    public static final String TB_OCORRENCIA_DADOS_SERVIDOR     = "tb_ocorrencia_dados_servidor";
    public static final String ODS_CODIGO                       = TB_OCORRENCIA_DADOS_SERVIDOR + ".ods_codigo";
    public static final String ODS_TOC_CODIGO                   = TB_OCORRENCIA_DADOS_SERVIDOR + ".toc_codigo";
    public static final String ODS_TDA_CODIGO                   = TB_OCORRENCIA_DADOS_SERVIDOR + ".tda_codigo";
    public static final String ODS_SER_CODIGO                   = TB_OCORRENCIA_DADOS_SERVIDOR + ".ser_codigo";
    public static final String ODS_USU_CODIGO                   = TB_OCORRENCIA_DADOS_SERVIDOR + ".usu_codigo";
    public static final String ODS_DATA                         = TB_OCORRENCIA_DADOS_SERVIDOR + ".ods_data";
    public static final String ODS_OBS                          = TB_OCORRENCIA_DADOS_SERVIDOR + ".ods_obs";
    public static final String ODS_VALOR_ANT                    = TB_OCORRENCIA_DADOS_SERVIDOR + ".ods_valor_ant";
    public static final String ODS_VALOR_NOVO                   = TB_OCORRENCIA_DADOS_SERVIDOR + ".ods_valor_novo";
    public static final String ODS_IP_ACESSO                    = TB_OCORRENCIA_DADOS_SERVIDOR + ".ods_ip_acesso";

    public static final String TB_DECISAO_JUDICIAL              = "tb_decisao_judicial";
    public static final String DJU_CODIGO                       = TB_DECISAO_JUDICIAL + ".dju_codigo";
    public static final String DJU_OCA_CODIGO                   = TB_DECISAO_JUDICIAL + ".oca_codigo";
    public static final String DJU_TJU_CODIGO                   = TB_DECISAO_JUDICIAL + ".tju_codigo";
    public static final String DJU_CID_CODIGO                   = TB_DECISAO_JUDICIAL + ".cid_codigo";
    public static final String DJU_NUM_PROCESSO                 = TB_DECISAO_JUDICIAL + ".dju_num_processo";
    public static final String DJU_DATA                         = TB_DECISAO_JUDICIAL + ".dju_data";
    public static final String DJU_TEXTO                        = TB_DECISAO_JUDICIAL + ".dju_texto";
    public static final String DJU_DATA_REVOGACAO               = TB_DECISAO_JUDICIAL + ".dju_data_revogacao";

    public static final String TB_DESPESA_COMUM                 = "tb_despesa_comum";
    public static final String DEC_CODIGO                       = TB_DESPESA_COMUM + ".dec_codigo";
    public static final String DEC_ECH_CODIGO                   = TB_DESPESA_COMUM + ".ech_codigo";
    public static final String DEC_PLA_CODIGO                   = TB_DESPESA_COMUM + ".pla_codigo";
    public static final String DEC_POS_CODIGO                   = TB_DESPESA_COMUM + ".pos_codigo";
    public static final String DEC_SDC_CODIGO                   = TB_DESPESA_COMUM + ".sdc_codigo";
    public static final String DEC_VALOR                        = TB_DESPESA_COMUM + ".dec_valor";
    public static final String DEC_VALOR_RATEIO                 = TB_DESPESA_COMUM + ".dec_valor_rateio";
    public static final String DEC_PRAZO                        = TB_DESPESA_COMUM + ".dec_prazo";
    public static final String DEC_DATA                         = TB_DESPESA_COMUM + ".dec_data";
    public static final String DEC_DATA_INI                     = TB_DESPESA_COMUM + ".dec_data_ini";
    public static final String DEC_DATA_FIM                     = TB_DESPESA_COMUM + ".dec_data_fim";
    public static final String DEC_IDENTIFICADOR                = TB_DESPESA_COMUM + ".dec_identificador";

    public static final String TB_DESPESA_INDIVIDUAL            = "tb_despesa_individual";
    public static final String DEI_ADE_CODIGO                   = TB_DESPESA_INDIVIDUAL + ".ade_codigo";
    public static final String DEI_PLA_CODIGO                   = TB_DESPESA_INDIVIDUAL + ".pla_codigo";
    public static final String DEI_PRM_CODIGO                   = TB_DESPESA_INDIVIDUAL + ".prm_codigo";
    public static final String DEI_DEC_CODIGO                   = TB_DESPESA_INDIVIDUAL + ".dec_codigo";

    public static final String TB_ESTABELECIMENTO               = "tb_estabelecimento";
    public static final String EST_CODIGO                       = TB_ESTABELECIMENTO + ".est_codigo";
    public static final String EST_CSE_CODIGO                   = TB_ESTABELECIMENTO + ".cse_codigo";
    public static final String EST_IDENTIFICADOR                = TB_ESTABELECIMENTO + ".est_identificador";
    public static final String EST_NOME                         = TB_ESTABELECIMENTO + ".est_nome";
    public static final String EST_CNPJ                         = TB_ESTABELECIMENTO + ".est_cnpj";
    public static final String EST_EMAIL                        = TB_ESTABELECIMENTO + ".est_email";
    public static final String EST_RESPONSAVEL                  = TB_ESTABELECIMENTO + ".est_responsavel";
    public static final String EST_RESPONSAVEL_2                = TB_ESTABELECIMENTO + ".est_responsavel_2";
    public static final String EST_RESPONSAVEL_3                = TB_ESTABELECIMENTO + ".est_responsavel_3";
    public static final String EST_RESP_CARGO                   = TB_ESTABELECIMENTO + ".est_resp_cargo";
    public static final String EST_RESP_CARGO_2                 = TB_ESTABELECIMENTO + ".est_resp_cargo_2";
    public static final String EST_RESP_CARGO_3                 = TB_ESTABELECIMENTO + ".est_resp_cargo_3";
    public static final String EST_RESP_TELEFONE                = TB_ESTABELECIMENTO + ".est_resp_telefone";
    public static final String EST_RESP_TELEFONE_2              = TB_ESTABELECIMENTO + ".est_resp_telefone_2";
    public static final String EST_RESP_TELEFONE_3              = TB_ESTABELECIMENTO + ".est_resp_telefone_3";
    public static final String EST_LOGRADOURO                   = TB_ESTABELECIMENTO + ".est_logradouro";
    public static final String EST_NRO                          = TB_ESTABELECIMENTO + ".est_nro";
    public static final String EST_COMPL                        = TB_ESTABELECIMENTO + ".est_compl";
    public static final String EST_BAIRRO                       = TB_ESTABELECIMENTO + ".est_bairro";
    public static final String EST_CIDADE                       = TB_ESTABELECIMENTO + ".est_cidade";
    public static final String EST_UF                           = TB_ESTABELECIMENTO + ".est_uf";
    public static final String EST_CEP                          = TB_ESTABELECIMENTO + ".est_cep";
    public static final String EST_TEL                          = TB_ESTABELECIMENTO + ".est_tel";
    public static final String EST_FAX                          = TB_ESTABELECIMENTO + ".est_fax";
    public static final String EST_ATIVO                        = TB_ESTABELECIMENTO + ".est_ativo";
    public static final String EST_NOME_ABREV                   = TB_ESTABELECIMENTO + ".est_nome_abrev";
    public static final String EST_FOLHA                        = TB_ESTABELECIMENTO + ".est_folha";

    public static final String TB_ESTADO_CIVIL                  = "tb_estado_civil";
    public static final String EST_CIVIL_CODIGO                 = TB_ESTADO_CIVIL + ".est_cvl_codigo";
    public static final String EST_CIVIL_DESCRICAO              = TB_ESTADO_CIVIL + ".est_cvl_descricao";

    public static final String TB_GRUPO_FUNCAO                  = "tb_grupo_funcao";
    public static final String GRF_CODIGO                       = TB_GRUPO_FUNCAO + ".grf_codigo";
    public static final String GRF_DESCRICAO                    = TB_GRUPO_FUNCAO + ".grf_descricao";

    public static final String TB_GRUPO_PARAM_SIST_CSE          = "tb_grupo_param_sist_cse";
    public static final String GPS_CODIGO                       = TB_GRUPO_PARAM_SIST_CSE + ".gps_codigo";
    public static final String GPS_DESCRICAO                    = TB_GRUPO_PARAM_SIST_CSE + ".gps_descricao";

    public static final String TB_FUNCAO                        = "tb_funcao";
    public static final String FUN_CODIGO                       = TB_FUNCAO + ".fun_codigo";
    public static final String FUN_GRF_CODIGO                   = TB_FUNCAO + ".grf_codigo";
    public static final String FUN_DESCRICAO                    = TB_FUNCAO + ".fun_descricao";
    public static final String FUN_PERMITE_BLOQUEIO             = TB_FUNCAO + ".fun_permite_bloqueio";
    public static final String FUN_EXIGE_TMO                    = TB_FUNCAO + ".fun_exige_tmo";
    public static final String FUN_EXIGE_SEGUNDA_SENHA_CSE      = TB_FUNCAO + ".fun_exige_segunda_senha_cse";
    public static final String FUN_EXIGE_SEGUNDA_SENHA_SUP      = TB_FUNCAO + ".fun_exige_segunda_senha_sup";
    public static final String FUN_EXIGE_SEGUNDA_SENHA_ORG      = TB_FUNCAO + ".fun_exige_segunda_senha_org";
    public static final String FUN_EXIGE_SEGUNDA_SENHA_CSA      = TB_FUNCAO + ".fun_exige_segunda_senha_csa";
    public static final String FUN_EXIGE_SEGUNDA_SENHA_COR      = TB_FUNCAO + ".fun_exige_segunda_senha_cor";
    public static final String FUN_EXIGE_SEGUNDA_SENHA_SER      = TB_FUNCAO + ".fun_exige_segunda_senha_ser";
    public static final String FUN_AUDITAVEL                    = TB_FUNCAO + ".fun_auditavel";
    public static final String FUN_RESTRITA_NCA                 = TB_FUNCAO + ".fun_restrita_nca";

    public static final String TB_FUNCAO_PERFIL                 = "tb_funcao_perfil";
    public static final String FP_FUN_CODIGO                    = TB_FUNCAO_PERFIL + ".fun_codigo";
    public static final String FP_PER_CODIGO                    = TB_FUNCAO_PERFIL + ".per_codigo";

    public static final String TB_FUNCAO_PERFIL_COR             = "tb_funcao_perfil_cor";
    public static final String FP_COR_COR_CODIGO                = TB_FUNCAO_PERFIL_COR + ".cor_codigo";
    public static final String FP_COR_USU_CODIGO                = TB_FUNCAO_PERFIL_COR + ".usu_codigo";
    public static final String FP_COR_FUN_CODIGO                = TB_FUNCAO_PERFIL_COR + ".fun_codigo";

    public static final String TB_FUNCAO_PERFIL_CSA             = "tb_funcao_perfil_csa";
    public static final String FP_CSA_CSA_CODIGO                = TB_FUNCAO_PERFIL_CSA + ".csa_codigo";
    public static final String FP_CSA_USU_CODIGO                = TB_FUNCAO_PERFIL_CSA + ".usu_codigo";
    public static final String FP_CSA_FUN_CODIGO                = TB_FUNCAO_PERFIL_CSA + ".fun_codigo";

    public static final String TB_FUNCAO_PERFIL_CSE             = "tb_funcao_perfil_cse";
    public static final String FP_CSE_CSE_CODIGO                = TB_FUNCAO_PERFIL_CSE + ".cse_codigo";
    public static final String FP_CSE_USU_CODIGO                = TB_FUNCAO_PERFIL_CSE + ".usu_codigo";
    public static final String FP_CSE_FUN_CODIGO                = TB_FUNCAO_PERFIL_CSE + ".fun_codigo";

    public static final String TB_FUNCAO_PERFIL_MASTER_NCA      = "tb_funcao_perfil_master_nca";
    public static final String FPM_NCA_CODIGO                   = TB_FUNCAO_PERFIL_MASTER_NCA + ".nca_codigo";
    public static final String FPM_FUN_CODIGO                   = TB_FUNCAO_PERFIL_MASTER_NCA + ".fun_codigo";

    public static final String TB_FUNCAO_PERFIL_ORG             = "tb_funcao_perfil_org";
    public static final String FP_ORG_ORG_CODIGO                = TB_FUNCAO_PERFIL_ORG + ".org_codigo";
    public static final String FP_ORG_USU_CODIGO                = TB_FUNCAO_PERFIL_ORG + ".usu_codigo";
    public static final String FP_ORG_FUN_CODIGO                = TB_FUNCAO_PERFIL_ORG + ".fun_codigo";

    public static final String TB_FUNCAO_PERFIL_SUP             = "tb_funcao_perfil_sup";
    public static final String FP_SUP_CSE_CODIGO                = TB_FUNCAO_PERFIL_SUP + ".cse_codigo";
    public static final String FP_SUP_USU_CODIGO                = TB_FUNCAO_PERFIL_SUP + ".usu_codigo";
    public static final String FP_SUP_FUN_CODIGO                = TB_FUNCAO_PERFIL_SUP + ".fun_codigo";

    public static final String TB_FUNCAO_PERMITIDA_NCA          = "tb_funcao_permitida_nca";
    public static final String FPN_NCA_CODIGO                   = TB_FUNCAO_PERMITIDA_NCA + ".nca_codigo";
    public static final String FPN_FUN_CODIGO                   = TB_FUNCAO_PERMITIDA_NCA + ".fun_codigo";

    public static final String TB_FUNCAO_SENSIVEL_CSA           = "tb_funcao_sensivel_csa";
    public static final String FSC_CSA_CODIGO                   = TB_FUNCAO_SENSIVEL_CSA + ".csa_codigo";
    public static final String FSC_FUN_CODIGO                   = TB_FUNCAO_SENSIVEL_CSA + ".fun_codigo";
    public static final String FSC_VALOR                        = TB_FUNCAO_SENSIVEL_CSA + ".fsc_valor";

    public static final String TB_BLOQUEIO_USU_FUN_SVC          = "tb_bloqueio_usu_fun_svc";
    public static final String BUF_USU_CODIGO                   = TB_BLOQUEIO_USU_FUN_SVC + ".usu_codigo";
    public static final String BUF_FUN_CODIGO                   = TB_BLOQUEIO_USU_FUN_SVC + ".fun_codigo";
    public static final String BUF_SVC_CODIGO                   = TB_BLOQUEIO_USU_FUN_SVC + ".svc_codigo";

    public static final String TB_HISTORICO_EXPORTACAO          = "tb_historico_exportacao";
    public static final String HIE_CODIGO                       = TB_HISTORICO_EXPORTACAO + ".hie_codigo";
    public static final String HIE_USU_CODIGO                   = TB_HISTORICO_EXPORTACAO + ".usu_codigo";
    public static final String HIE_ORG_CODIGO                   = TB_HISTORICO_EXPORTACAO + ".org_codigo";
    public static final String HIE_DATA_INI                     = TB_HISTORICO_EXPORTACAO + ".hie_data_ini";
    public static final String HIE_DATA_FIM                     = TB_HISTORICO_EXPORTACAO + ".hie_data_fim";
    public static final String HIE_DATA                         = TB_HISTORICO_EXPORTACAO + ".hie_data";
    public static final String HIE_PERIODO                      = TB_HISTORICO_EXPORTACAO + ".hie_periodo";
    public static final String HIE_DATA_INI_EXP                 = TB_HISTORICO_EXPORTACAO + ".hie_data_inicio_exp";
    public static final String HIE_DATA_FIM_EXP                 = TB_HISTORICO_EXPORTACAO + ".hie_data_fim_exp";

    public static final String TB_HISTORICO_MARGEM_PERIODO      = "tb_historico_margem_periodo";
    public static final String HMP_CODIGO                       = TB_HISTORICO_MARGEM_PERIODO + ".hmp_codigo";
    public static final String HMP_RSE_CODIGO                   = TB_HISTORICO_MARGEM_PERIODO + ".rse_codigo";
    public static final String HMP_MAR_CODIGO                   = TB_HISTORICO_MARGEM_PERIODO + ".mar_codigo";
    public static final String HMP_DATA                         = TB_HISTORICO_MARGEM_PERIODO + ".hmp_data";
    public static final String HMP_OPERACAO                     = TB_HISTORICO_MARGEM_PERIODO + ".hmp_operacao";
    public static final String HMP_MARGEM_ANTES                 = TB_HISTORICO_MARGEM_PERIODO + ".hmp_margem_antes";
    public static final String HMP_MARGEM_DEPOIS                = TB_HISTORICO_MARGEM_PERIODO + ".hmp_margem_depois";

    public static final String TB_HISTORICO_MARGEM_REGISTRO_SERVIDOR = "tb_historico_margem_rse";
    public static final String HMR_CODIGO                       = TB_HISTORICO_MARGEM_REGISTRO_SERVIDOR + ".hmr_codigo";
    public static final String HMR_RSE_CODIGO                   = TB_HISTORICO_MARGEM_REGISTRO_SERVIDOR + ".rse_codigo";
    public static final String HMR_MAR_CODIGO                   = TB_HISTORICO_MARGEM_REGISTRO_SERVIDOR + ".mar_codigo";
    public static final String HMR_OCA_CODIGO                   = TB_HISTORICO_MARGEM_REGISTRO_SERVIDOR + ".oca_codigo";
    public static final String HMR_OCA_CODIGO_HT                = TB_HISTORICO_MARGEM_REGISTRO_SERVIDOR + ".oca_codigo_ht";
    public static final String HMR_DATA                         = TB_HISTORICO_MARGEM_REGISTRO_SERVIDOR + ".hmr_data";
    public static final String HMR_OPERACAO                     = TB_HISTORICO_MARGEM_REGISTRO_SERVIDOR + ".hmr_operacao";
    public static final String HMR_MARGEM_ANTES                 = TB_HISTORICO_MARGEM_REGISTRO_SERVIDOR + ".hmr_margem_antes";
    public static final String HMR_MARGEM_DEPOIS                = TB_HISTORICO_MARGEM_REGISTRO_SERVIDOR + ".hmr_margem_depois";

    public static final String TB_HISTORICO_MARGEM_FOLHA        = "tb_historico_margem_folha";
    public static final String HMA_RSE_CODIGO                   = TB_HISTORICO_MARGEM_FOLHA + ".rse_codigo";
    public static final String HMA_MAR_CODIGO                   = TB_HISTORICO_MARGEM_FOLHA + ".mar_codigo";
    public static final String HMA_PERIODO                      = TB_HISTORICO_MARGEM_FOLHA + ".hma_periodo";
    public static final String HMA_DATA                         = TB_HISTORICO_MARGEM_FOLHA + ".hma_data";
    public static final String HMA_MARGEM_FOLHA                 = TB_HISTORICO_MARGEM_FOLHA + ".hma_margem_folha";

    public static final String TB_HISTORICO_MOV_FIN             = "tb_historico_mov_fin";
    public static final String HMF_CNV_CODIGO                   = TB_HISTORICO_MOV_FIN + ".cnv_codigo";
    public static final String HMF_PERIODO                      = TB_HISTORICO_MOV_FIN + ".hmf_periodo";
    public static final String HMF_OPERACAO                     = TB_HISTORICO_MOV_FIN + ".hmf_operacao";
    public static final String HMF_QTD                          = TB_HISTORICO_MOV_FIN + ".hmf_qtd";
    public static final String HMF_VALOR                        = TB_HISTORICO_MOV_FIN + ".hmf_valor";

    public static final String TB_HISTORICO_CONCLUSAO_RETORNO   = "tb_historico_conclusao_retorno";
    public static final String HCR_ORG_CODIGO                   = TB_HISTORICO_CONCLUSAO_RETORNO + ".org_codigo";
    public static final String HCR_DATA_INICIO                  = TB_HISTORICO_CONCLUSAO_RETORNO + ".hcr_data_inicio";
    public static final String HCR_DATA_FIM                     = TB_HISTORICO_CONCLUSAO_RETORNO + ".hcr_data_fim";
    public static final String HCR_PERIODO                      = TB_HISTORICO_CONCLUSAO_RETORNO + ".hcr_periodo";
    public static final String HCR_CHAVE_HIST_MARGEM            = TB_HISTORICO_CONCLUSAO_RETORNO + ".hcr_chave_hist_margem";
    public static final String HCR_DESFEITO                     = TB_HISTORICO_CONCLUSAO_RETORNO + ".hcr_desfeito";

    public static final String TB_HISTORICO_CONSULTA_MARGEM     = "tb_historico_consulta_margem";
    public static final String HCM_CODIGO                       = TB_HISTORICO_CONSULTA_MARGEM + ".hcm_codigo";
    public static final String HCM_USU_CODIGO                   = TB_HISTORICO_CONSULTA_MARGEM + ".usu_codigo";
    public static final String HCM_RSE_CODIGO                   = TB_HISTORICO_CONSULTA_MARGEM + ".rse_codigo";
    public static final String HCM_DATA                         = TB_HISTORICO_CONSULTA_MARGEM + ".hcm_data";
    public static final String HCM_TEM_MARGEM                   = TB_HISTORICO_CONSULTA_MARGEM + ".hcm_tem_margem";
    public static final String HCM_CANAL                        = TB_HISTORICO_CONSULTA_MARGEM + ".hcm_canal";

    public static final String TB_HISTORICO_PROCESSAMENTO       = "tb_historico_processamento";
    public static final String HPR_CODIGO                       = TB_HISTORICO_PROCESSAMENTO + ".hpr_codigo";
    public static final String HPR_ORG_CODIGO                   = TB_HISTORICO_PROCESSAMENTO + ".org_codigo";
    public static final String HPR_EST_CODIGO                   = TB_HISTORICO_PROCESSAMENTO + ".est_codigo";
    public static final String HPR_PERIODO                      = TB_HISTORICO_PROCESSAMENTO + ".hpr_periodo";
    public static final String HPR_DATA_INI                     = TB_HISTORICO_PROCESSAMENTO + ".hpr_data_ini";
    public static final String HPR_DATA_FIM                     = TB_HISTORICO_PROCESSAMENTO + ".hpr_data_fim";
    public static final String HPR_ARQUIVO_MARGEM               = TB_HISTORICO_PROCESSAMENTO + ".hpr_arquivo_margem";
    public static final String HPR_CONF_ENTRADA_MARGEM          = TB_HISTORICO_PROCESSAMENTO + ".hpr_conf_entrada_margem";
    public static final String HPR_CONF_TRADUTOR_MARGEM         = TB_HISTORICO_PROCESSAMENTO + ".hpr_conf_tradutor_margem";
    public static final String HPR_LINHAS_ARQUIVO_MARGEM        = TB_HISTORICO_PROCESSAMENTO + ".hpr_linhas_arquivo_margem";
    public static final String HPR_ARQUIVO_RETORNO              = TB_HISTORICO_PROCESSAMENTO + ".hpr_arquivo_retorno";
    public static final String HPR_CONF_ENTRADA_RETORNO         = TB_HISTORICO_PROCESSAMENTO + ".hpr_conf_entrada_retorno";
    public static final String HPR_CONF_TRADUTOR_RETORNO        = TB_HISTORICO_PROCESSAMENTO + ".hpr_conf_tradutor_retorno";
    public static final String HPR_LINHAS_ARQUIVO_RETORNO       = TB_HISTORICO_PROCESSAMENTO + ".hpr_linhas_arquivo_retorno";
    public static final String HPR_CHAVE_IDENTIFICACAO          = TB_HISTORICO_PROCESSAMENTO + ".hpr_chave_identificacao";
    public static final String HPR_ORDEM_EXC_CAMPOS_CHAVE       = TB_HISTORICO_PROCESSAMENTO + ".hpr_ordem_exc_campos_chave";

    public static final String TB_HISTORICO_STATUS_ADE          = "tb_historico_status_ade";
    public static final String HSA_ADE_CODIGO                   = TB_HISTORICO_STATUS_ADE + ".ade_codigo";
    public static final String SAD_CODIGO_ANTERIOR              = TB_HISTORICO_STATUS_ADE + ".sad_codigo_anterior";
    public static final String SAD_CODIGO_NOVO                  = TB_HISTORICO_STATUS_ADE + ".sad_codigo_novo";
    public static final String HSA_DATA                         = TB_HISTORICO_STATUS_ADE + ".hsa_data";

    public static final String TB_IGNORA_INCONSISTENCIA_ADE     = "tb_ignora_inconsistencia_ade";
    public static final String IIA_ADE_CODIGO                   = TB_IGNORA_INCONSISTENCIA_ADE + ".ade_codigo";
    public static final String IIA_ITEM                         = TB_IGNORA_INCONSISTENCIA_ADE + ".iia_item";
    public static final String IIA_DATA                         = TB_IGNORA_INCONSISTENCIA_ADE + ".iia_data";
    public static final String IIA_OBS                          = TB_IGNORA_INCONSISTENCIA_ADE + ".iia_obs";
    public static final String IIA_USUARIO                      = TB_IGNORA_INCONSISTENCIA_ADE + ".iia_usuario";
    public static final String IIA_PERMANENTE                   = TB_IGNORA_INCONSISTENCIA_ADE + ".iia_permanente";

    public static final String TB_LOG                           = "tb_log";
    public static final String LOG_TLO_CODIGO                   = TB_LOG + ".tlo_codigo";
    public static final String LOG_TEN_CODIGO                   = TB_LOG + ".ten_codigo";
    public static final String LOG_USU_CODIGO                   = TB_LOG + ".usu_codigo";
    public static final String LOG_FUN_CODIGO                   = TB_LOG + ".fun_codigo";
    public static final String LOG_DATA                         = TB_LOG + ".log_data";
    public static final String LOG_OBS                          = TB_LOG + ".log_obs";
    public static final String LOG_IP                           = TB_LOG + ".log_ip";
    public static final String LOG_COD_ENTIDADE_00              = TB_LOG + ".log_cod_ent_00";
    public static final String LOG_COD_ENTIDADE_01              = TB_LOG + ".log_cod_ent_01";
    public static final String LOG_COD_ENTIDADE_02              = TB_LOG + ".log_cod_ent_02";
    public static final String LOG_COD_ENTIDADE_03              = TB_LOG + ".log_cod_ent_03";
    public static final String LOG_COD_ENTIDADE_04              = TB_LOG + ".log_cod_ent_04";
    public static final String LOG_COD_ENTIDADE_05              = TB_LOG + ".log_cod_ent_05";
    public static final String LOG_COD_ENTIDADE_06              = TB_LOG + ".log_cod_ent_06";
    public static final String LOG_COD_ENTIDADE_07              = TB_LOG + ".log_cod_ent_07";
    public static final String LOG_COD_ENTIDADE_08              = TB_LOG + ".log_cod_ent_08";
    public static final String LOG_COD_ENTIDADE_09              = TB_LOG + ".log_cod_ent_09";
    public static final String LOG_COD_ENTIDADE_10              = TB_LOG + ".log_cod_ent_10";
    public static final String LOG_CANAL                        = TB_LOG + ".log_canal";
    public static final String LOG_PORTA                        = TB_LOG + ".log_porta";

    public static final String TB_MARGEM                        = "tb_margem";
    public static final String MAR_CODIGO                       = TB_MARGEM + ".mar_codigo";
    public static final String MAR_CODIGO_PAI                   = TB_MARGEM + ".mar_codigo_pai";
    public static final String MAR_DESCRICAO                    = TB_MARGEM + ".mar_descricao";
    public static final String MAR_SEQUENCIA                    = TB_MARGEM + ".mar_sequencia";
    public static final String MAR_EXIBE_CSE                    = TB_MARGEM + ".mar_exibe_cse";
    public static final String MAR_EXIBE_ORG                    = TB_MARGEM + ".mar_exibe_org";
    public static final String MAR_EXIBE_SER                    = TB_MARGEM + ".mar_exibe_ser";
    public static final String MAR_EXIBE_CSA                    = TB_MARGEM + ".mar_exibe_csa";
    public static final String MAR_EXIBE_COR                    = TB_MARGEM + ".mar_exibe_cor";
    public static final String MAR_EXIBE_SUP                    = TB_MARGEM + ".mar_exibe_sup";
    public static final String MAR_TIPO_VLR                     = TB_MARGEM + ".mar_tipo_vlr";
    public static final String MAR_PORCENTAGEM                  = TB_MARGEM + ".mar_porcentagem";
    public static final String MAR_EXIBE_ALT_MULT_CONTRATOS     = TB_MARGEM + ".mar_exibe_alt_mult_contratos";

    public static final String TB_MARGEM_REGISTRO_SERVIDOR      = "tb_margem_registro_servidor";
    public static final String MRS_MAR_CODIGO                   = TB_MARGEM_REGISTRO_SERVIDOR + ".mar_codigo";
    public static final String MRS_RSE_CODIGO                   = TB_MARGEM_REGISTRO_SERVIDOR + ".rse_codigo";
    public static final String MRS_MARGEM                       = TB_MARGEM_REGISTRO_SERVIDOR + ".mrs_margem";
    public static final String MRS_MARGEM_REST                  = TB_MARGEM_REGISTRO_SERVIDOR + ".mrs_margem_rest";
    public static final String MRS_MARGEM_USADA                 = TB_MARGEM_REGISTRO_SERVIDOR + ".mrs_margem_usada";
    public static final String MRS_PRAZO_MAX                    = TB_MARGEM_REGISTRO_SERVIDOR + ".mrs_prazo_max";
    public static final String MRS_MEDIA_MARGEM                 = TB_MARGEM_REGISTRO_SERVIDOR + ".mrs_media_margem";
    public static final String MAR_COD_ADEQUACAO                = TB_MARGEM_REGISTRO_SERVIDOR + ".mar_codigo_adequacao";

    public static final String TB_MENSAGEM                      = "tb_mensagem";
    public static final String MEN_CODIGO                       = TB_MENSAGEM + ".men_codigo";
    public static final String MEN_USU_CODIGO                   = TB_MENSAGEM + ".usu_codigo";
    public static final String MEN_FUN_CODIGO                   = TB_MENSAGEM + ".fun_codigo";
    public static final String MEN_TITULO                       = TB_MENSAGEM + ".men_titulo";
    public static final String MEN_TEXTO                        = TB_MENSAGEM + ".men_texto";
    public static final String MEN_DATA                         = TB_MENSAGEM + ".men_data";
    public static final String MEN_SEQUENCIA                    = TB_MENSAGEM + ".men_sequencia";
    public static final String MEN_EXIBE_CSE                    = TB_MENSAGEM + ".men_exibe_cse";
    public static final String MEN_EXIBE_ORG                    = TB_MENSAGEM + ".men_exibe_org";
    public static final String MEN_EXIBE_CSA                    = TB_MENSAGEM + ".men_exibe_csa";
    public static final String MEN_EXIBE_COR                    = TB_MENSAGEM + ".men_exibe_cor";
    public static final String MEN_EXIBE_SER                    = TB_MENSAGEM + ".men_exibe_ser";
    public static final String MEN_EXIBE_SUP                    = TB_MENSAGEM + ".men_exibe_sup";
    public static final String MEN_HTML                         = TB_MENSAGEM + ".men_html";
    public static final String MEN_EXIGE_LEITURA                = TB_MENSAGEM + ".men_exige_leitura";
    public static final String MEN_PERMITE_LER_DEPOIS           = TB_MENSAGEM + ".men_permite_ler_depois";
    public static final String MEN_NOTIFICAR_CSE_LEITURA        = TB_MENSAGEM + ".men_notificar_cse_leitura";
    public static final String MEN_BLOQ_CSA_SEM_LEITURA         = TB_MENSAGEM + ".men_bloq_csa_sem_leitura";
    public static final String MEN_PUBLICA                      = TB_MENSAGEM + ".men_publica";
    public static final String MEN_LIDA_INDIVIDUALMENTE			= TB_MENSAGEM + ".men_lida_individualmente";
    public static final String MEN_PUSH_NOTIFICATION_SER	    = TB_MENSAGEM + ".men_push_notification_ser";

    public static final String TB_MENSAGEM_CSA                  = "tb_mensagem_csa";
    public static final String MEN_CSA_MEN_CODIGO               = TB_MENSAGEM_CSA + ".men_codigo";
    public static final String MEN_CSA_CSA_CODIGO               = TB_MENSAGEM_CSA + ".csa_codigo";

    public static final String TB_TIPO_JUSTICA                  = "tb_tipo_justica";
    public static final String TJU_CODIGO                       = TB_TIPO_JUSTICA + ".tju_codigo";
    public static final String TJU_DESCRICAO                    = TB_TIPO_JUSTICA + ".tju_descricao";

    public static final String TB_TIPO_MOTIVO_BLOQUEIO          = "tb_tipo_motivo_bloqueio";
    public static final String TMB_CODIGO                       = TB_TIPO_MOTIVO_BLOQUEIO + ".tmb_codigo";
    public static final String TMB_DESCRICAO                    = TB_TIPO_MOTIVO_BLOQUEIO + ".tmb_descricao";

    public static final String TB_TIPO_MOTIVO_NAO_EXPORTACAO    = "tb_tipo_motivo_nao_exportacao";
    public static final String MNE_CODIGO                       = TB_TIPO_MOTIVO_NAO_EXPORTACAO + ".mne_codigo";
    public static final String MNE_DESCRICAO                    = TB_TIPO_MOTIVO_NAO_EXPORTACAO + ".mne_descricao";

    public static final String TB_LEITURA_COMUNICACAO_USUARIO   = "tb_leitura_comunicacao_usuario";
    public static final String LCU_CMN_CODIGO                   = TB_LEITURA_COMUNICACAO_USUARIO + ".cmn_codigo";
    public static final String LCU_USU_CODIGO                   = TB_LEITURA_COMUNICACAO_USUARIO + ".usu_codigo";
    public static final String LCU_DATA                         = TB_LEITURA_COMUNICACAO_USUARIO + ".lcu_data";

    public static final String TB_LEITURA_MENSAGEM_USUARIO      = "tb_leitura_mensagem_usuario";
    public static final String LMU_MEN_CODIGO                   = TB_LEITURA_MENSAGEM_USUARIO + ".men_codigo";
    public static final String LMU_USU_CODIGO                   = TB_LEITURA_MENSAGEM_USUARIO + ".usu_codigo";
    public static final String LMU_DATA                         = TB_LEITURA_MENSAGEM_USUARIO + ".lmu_data";

    public static final String TB_LEITURA_TERMO_USUARIO         = "tb_leitura_termo_usuario";
    public static final String LTU_CODIGO                       = TB_LEITURA_TERMO_USUARIO + ".ltu_codigo";
    public static final String LTU_USU_CODIGO                   = TB_LEITURA_TERMO_USUARIO + ".usu_codigo";
    public static final String LTU_TAD_CODIGO                   = TB_LEITURA_TERMO_USUARIO + ".tad_codigo";
    public static final String LTU_DATA                         = TB_LEITURA_TERMO_USUARIO + ".ltu_data";
    public static final String LTU_TERMO_ACEITO                 = TB_LEITURA_TERMO_USUARIO + ".ltu_termo_aceito";
    public static final String LTU_CANAL                        = TB_LEITURA_TERMO_USUARIO + ".ltu_canal";
    public static final String LTU_IP_ACESSO                    = TB_LEITURA_TERMO_USUARIO + ".ltu_ip_acesso";
    public static final String LTU_PORTA                        = TB_LEITURA_TERMO_USUARIO + ".ltu_porta";
    public static final String LTU_OBS                          = TB_LEITURA_TERMO_USUARIO + ".ltu_obs";
    public static final String LTU_VERSAO_TERMO                 = TB_LEITURA_TERMO_USUARIO + ".ltu_versao_termo";

    public static final String TB_MODELO_EMAIL                  = "tb_modelo_email";
    public static final String MEM_CODIGO                       = TB_MODELO_EMAIL + ".mem_codigo";
    public static final String MEM_TITULO                       = TB_MODELO_EMAIL + ".mem_titulo";
    public static final String MEM_TEXTO                        = TB_MODELO_EMAIL + ".mem_texto";

    public static final String TB_NIVEL_SEGURANCA               = "tb_nivel_seguranca";
    public static final String NSG_CODIGO                       = TB_NIVEL_SEGURANCA + ".nsg_codigo";
    public static final String NSG_DESCRICAO                    = TB_NIVEL_SEGURANCA + ".nsg_descricao";

    public static final String TB_NIVEL_SEGURANCA_PARAM_SIST    = "tb_nivel_seguranca_param_sist";
    public static final String NSP_NSG_CODIGO                   = TB_NIVEL_SEGURANCA_PARAM_SIST + ".nsg_codigo";
    public static final String NSP_TPC_CODIGO                   = TB_NIVEL_SEGURANCA_PARAM_SIST + ".tpc_codigo";
    public static final String NSP_VLR_ESPERADO                 = TB_NIVEL_SEGURANCA_PARAM_SIST + ".nsp_vlr_esperado";

    public static final String TB_NOTIFICACAO_DISPOSITIVO       = "tb_notificacao_dispositivo";
    public static final String NDI_CODIGO                       = TB_NOTIFICACAO_DISPOSITIVO + ".ndi_codigo";
    public static final String NDI_USU_CODIGO_OPERADOR          = TB_NOTIFICACAO_DISPOSITIVO + ".usu_codigo_operador";
    public static final String NDI_USU_CODIGO_DESTINATARIO      = TB_NOTIFICACAO_DISPOSITIVO + ".usu_codigo_destinatario";
    public static final String NDI_FUN_CODIGO                   = TB_NOTIFICACAO_DISPOSITIVO + ".fun_codigo";
    public static final String NDI_TNO_CODIGO                   = TB_NOTIFICACAO_DISPOSITIVO + ".tno_codigo";
    public static final String NDI_TEXTO                        = TB_NOTIFICACAO_DISPOSITIVO + ".ndi_texto";
    public static final String NDI_DATA                         = TB_NOTIFICACAO_DISPOSITIVO + ".ndi_data";
    public static final String NDI_DATA_ENVIO                   = TB_NOTIFICACAO_DISPOSITIVO + ".ndi_data_envio";
    public static final String NDI_ATIVO                        = TB_NOTIFICACAO_DISPOSITIVO + ".ndi_ativo";

    public static final String TB_NOTIFICACAO_EMAIL             = "tb_notificacao_email";
    public static final String NEM_CODIGO                       = TB_NOTIFICACAO_EMAIL + ".nem_codigo";
    public static final String NEM_TNO_CODIGO                   = TB_NOTIFICACAO_EMAIL + ".tno_codigo";
    public static final String NEM_FUN_CODIGO                   = TB_NOTIFICACAO_EMAIL + ".fun_codigo";
    public static final String NEM_USU_CODIGO                   = TB_NOTIFICACAO_EMAIL + ".usu_codigo";
    public static final String NEM_TITULO                       = TB_NOTIFICACAO_EMAIL + ".nem_titulo";
    public static final String NEM_TEXTO                        = TB_NOTIFICACAO_EMAIL + ".nem_texto";
    public static final String NEM_DESTINATARIO                 = TB_NOTIFICACAO_EMAIL + ".nem_destinatario";
    public static final String NEM_DATA                         = TB_NOTIFICACAO_EMAIL + ".nem_data";
    public static final String NEM_DATA_ENVIO                   = TB_NOTIFICACAO_EMAIL + ".nem_data_envio";

    public static final String TB_OCORRENCIA_AUTORIZACAO        = "tb_ocorrencia_autorizacao";
    public static final String OCA_CODIGO                       = TB_OCORRENCIA_AUTORIZACAO + ".oca_codigo";
    public static final String OCA_TOC_CODIGO                   = TB_OCORRENCIA_AUTORIZACAO + ".toc_codigo";
    public static final String OCA_ADE_CODIGO                   = TB_OCORRENCIA_AUTORIZACAO + ".ade_codigo";
    public static final String OCA_USU_CODIGO                   = TB_OCORRENCIA_AUTORIZACAO + ".usu_codigo";
    public static final String OCA_DATA                         = TB_OCORRENCIA_AUTORIZACAO + ".oca_data";
    public static final String OCA_PERIODO                      = TB_OCORRENCIA_AUTORIZACAO + ".oca_periodo";
    public static final String OCA_OBS                          = TB_OCORRENCIA_AUTORIZACAO + ".oca_obs";
    public static final String OCA_TMO_CODIGO                   = TB_OCORRENCIA_AUTORIZACAO + ".tmo_codigo";
    public static final String OCA_ADE_VLR_ANT                  = TB_OCORRENCIA_AUTORIZACAO + ".oca_ade_vlr_ant";
    public static final String OCA_ADE_VLR_NOVO                 = TB_OCORRENCIA_AUTORIZACAO + ".oca_ade_vlr_novo";
    public static final String OCA_IP_ACESSO                    = TB_OCORRENCIA_AUTORIZACAO + ".oca_ip_acesso";

    public static final String TB_OCORRENCIA_PARCELA            = "tb_ocorrencia_parcela";
    public static final String OCP_CODIGO                       = TB_OCORRENCIA_PARCELA + ".ocp_codigo";
    public static final String OCP_TOC_CODIGO                   = TB_OCORRENCIA_PARCELA + ".toc_codigo";
    public static final String OCP_USU_CODIGO                   = TB_OCORRENCIA_PARCELA + ".usu_codigo";
    public static final String OCP_PRD_CODIGO                   = TB_OCORRENCIA_PARCELA + ".prd_codigo";
    public static final String OCP_DATA                         = TB_OCORRENCIA_PARCELA + ".ocp_data";
    public static final String OCP_OBS                          = TB_OCORRENCIA_PARCELA + ".ocp_obs";

    public static final String TB_OCORRENCIA_PARCELA_PERIODO    = "tb_ocorrencia_parcela_periodo";
    public static final String OPP_CODIGO                       = TB_OCORRENCIA_PARCELA_PERIODO + ".ocp_codigo";
    public static final String OPP_TOC_CODIGO                   = TB_OCORRENCIA_PARCELA_PERIODO + ".toc_codigo";
    public static final String OPP_USU_CODIGO                   = TB_OCORRENCIA_PARCELA_PERIODO + ".usu_codigo";
    public static final String OPP_PRD_CODIGO                   = TB_OCORRENCIA_PARCELA_PERIODO + ".prd_codigo";
    public static final String OPP_DATA                         = TB_OCORRENCIA_PARCELA_PERIODO + ".ocp_data";
    public static final String OPP_OBS                          = TB_OCORRENCIA_PARCELA_PERIODO + ".ocp_obs";

    public static final String TB_OCORRENCIA_CONSIGNANTE        = "tb_ocorrencia_consignante";
    public static final String OCE_CODIGO                       = TB_OCORRENCIA_CONSIGNANTE + ".oce_codigo";
    public static final String OCE_CSE_CODIGO                   = TB_OCORRENCIA_CONSIGNANTE + ".cse_codigo";
    public static final String OCE_TOC_CODIGO                   = TB_OCORRENCIA_CONSIGNANTE + ".toc_codigo";
    public static final String OCE_USU_CODIGO                   = TB_OCORRENCIA_CONSIGNANTE + ".usu_codigo";
    public static final String OCE_DATA                         = TB_OCORRENCIA_CONSIGNANTE + ".oce_data";
    public static final String OCE_OBS                          = TB_OCORRENCIA_CONSIGNANTE + ".oce_obs";
    public static final String OCE_IP_ACESSO                    = TB_OCORRENCIA_CONSIGNANTE + ".oce_ip_acesso";

    public static final String TB_OCORRENCIA_CONSIGNATARIA      = "tb_ocorrencia_consignataria";
    public static final String OCC_CODIGO                       = TB_OCORRENCIA_CONSIGNATARIA + ".occ_codigo";
    public static final String OCC_CSA_CODIGO                   = TB_OCORRENCIA_CONSIGNATARIA + ".csa_codigo";
    public static final String OCC_USU_CODIGO                   = TB_OCORRENCIA_CONSIGNATARIA + ".usu_codigo";
    public static final String OCC_TOC_CODIGO                   = TB_OCORRENCIA_CONSIGNATARIA + ".toc_codigo";
    public static final String OCC_OBS                          = TB_OCORRENCIA_CONSIGNATARIA + ".occ_obs";
    public static final String OCC_DATA                         = TB_OCORRENCIA_CONSIGNATARIA + ".occ_data";
    public static final String OCC_IP_ACESSO                    = TB_OCORRENCIA_CONSIGNATARIA + ".occ_ip_acesso";
    public static final String OCC_TPE_CODIGO                   = TB_OCORRENCIA_CONSIGNATARIA + ".tpe_codigo";
    public static final String OCC_TMO_CODIGO                   = TB_OCORRENCIA_CONSIGNATARIA + ".tmo_codigo";

    public static final String TB_OCORRENCIA_ORGAO              = "tb_ocorrencia_orgao";
    public static final String OOR_CODIGO                       = TB_OCORRENCIA_ORGAO + ".oor_codigo";
    public static final String OOR_ORG_CODIGO                   = TB_OCORRENCIA_ORGAO + ".org_codigo";
    public static final String OOR_TOC_CODIGO                   = TB_OCORRENCIA_ORGAO + ".toc_codigo";
    public static final String OOR_TMO_CODIGO                   = TB_OCORRENCIA_ORGAO + ".tmo_codigo";
    public static final String OOR_USU_CODIGO                   = TB_OCORRENCIA_ORGAO + ".usu_codigo";
    public static final String OOR_DATA                         = TB_OCORRENCIA_ORGAO + ".oor_data";
    public static final String OOR_OBS                          = TB_OCORRENCIA_ORGAO + ".oor_obs";
    public static final String OOR_IP_ACESSO                    = TB_OCORRENCIA_ORGAO + ".oor_ip_acesso";

    public static final String TB_OCORRENCIA_USUARIO            = "tb_ocorrencia_usuario";
    public static final String OUS_CODIGO                       = TB_OCORRENCIA_USUARIO + ".ous_codigo";
    public static final String OUS_TOC_CODIGO                   = TB_OCORRENCIA_USUARIO + ".toc_codigo";
    public static final String OUS_USU_CODIGO                   = TB_OCORRENCIA_USUARIO + ".usu_codigo";
    public static final String OUS_OUS_USU_CODIGO               = TB_OCORRENCIA_USUARIO + ".ous_usu_codigo";
    public static final String OUS_DATA                         = TB_OCORRENCIA_USUARIO + ".ous_data";
    public static final String OUS_OBS                          = TB_OCORRENCIA_USUARIO + ".ous_obs";
    public static final String OUS_IP_ACESSO                    = TB_OCORRENCIA_USUARIO + ".ous_ip_acesso";
    public static final String OUS_TMO_CODIGO                   = TB_OCORRENCIA_USUARIO + ".tmo_codigo";

    public static final String TB_ORGAO                         = "tb_orgao";
    public static final String ORG_CODIGO                       = TB_ORGAO + ".org_codigo";
    public static final String ORG_EST_CODIGO                   = TB_ORGAO + ".est_codigo";
    public static final String ORG_IDENTIFICADOR                = TB_ORGAO + ".org_identificador";
    public static final String ORG_NOME                         = TB_ORGAO + ".org_nome";
    public static final String ORG_EMAIL                        = TB_ORGAO + ".org_email";
    public static final String ORG_EMAIL_FOLHA                  = TB_ORGAO + ".org_email_folha";
    public static final String ORG_RESPONSAVEL                  = TB_ORGAO + ".org_responsavel";
    public static final String ORG_RESPONSAVEL_2                = TB_ORGAO + ".org_responsavel_2";
    public static final String ORG_RESPONSAVEL_3                = TB_ORGAO + ".org_responsavel_3";
    public static final String ORG_RESP_CARGO                   = TB_ORGAO + ".org_resp_cargo";
    public static final String ORG_RESP_CARGO_2                 = TB_ORGAO + ".org_resp_cargo_2";
    public static final String ORG_RESP_CARGO_3                 = TB_ORGAO + ".org_resp_cargo_3";
    public static final String ORG_RESP_TELEFONE                = TB_ORGAO + ".org_resp_telefone";
    public static final String ORG_RESP_TELEFONE_2              = TB_ORGAO + ".org_resp_telefone_2";
    public static final String ORG_RESP_TELEFONE_3              = TB_ORGAO + ".org_resp_telefone_3";
    public static final String ORG_LOGRADOURO                   = TB_ORGAO + ".org_logradouro";
    public static final String ORG_NRO                          = TB_ORGAO + ".org_nro";
    public static final String ORG_COMPL                        = TB_ORGAO + ".org_compl";
    public static final String ORG_BAIRRO                       = TB_ORGAO + ".org_bairro";
    public static final String ORG_CIDADE                       = TB_ORGAO + ".org_cidade";
    public static final String ORG_UF                           = TB_ORGAO + ".org_uf";
    public static final String ORG_CEP                          = TB_ORGAO + ".org_cep";
    public static final String ORG_TEL                          = TB_ORGAO + ".org_tel";
    public static final String ORG_FAX                          = TB_ORGAO + ".org_fax";
    //  public static final String ORG_DIA_CORTE                    = TB_ORGAO + ".org_dia_corte";
    public static final String ORG_ATIVO                        = TB_ORGAO + ".org_ativo";
    public static final String ORG_CNPJ                         = TB_ORGAO + ".org_cnpj";
    public static final String ORG_NOME_ABREV                   = TB_ORGAO + ".org_nome_abrev";
    //  public static final String ORG_CARENCIA                     = TB_ORGAO + ".org_carencia";
    public static final String ORG_DIA_REPASSE                  = TB_ORGAO + ".org_dia_repasse";
    public static final String ORG_IP_ACESSO                    = TB_ORGAO + ".org_ip_acesso";
    public static final String ORG_DDNS_ACESSO                  = TB_ORGAO + ".org_ddns_acesso";
    public static final String ORG_FOLHA                        = TB_ORGAO + ".org_folha";
    public static final String ORG_EMAIL_VALIDAR_SERVIDOR       = TB_ORGAO + ".org_email_validar_servidor";
    public static final String ORG_IDENTIFICADOR_BENEFICIO      = TB_ORGAO + ".org_identificador_beneficio";

    public static final String TB_OPERACAO_LIBERA_MARGEM        = "tb_operacao_libera_margem";
    public static final String OLM_CODIGO                       = TB_OPERACAO_LIBERA_MARGEM + ".olm_codigo";
    public static final String OLM_USU_CODIGO                   = TB_OPERACAO_LIBERA_MARGEM + ".usu_codigo";
    public static final String OLM_RSE_CODIGO                   = TB_OPERACAO_LIBERA_MARGEM + ".rse_codigo";
    public static final String OLM_CSA_CODIGO                   = TB_OPERACAO_LIBERA_MARGEM + ".csa_codigo";
    public static final String OLM_DATA                         = TB_OPERACAO_LIBERA_MARGEM + ".olm_data";
    public static final String OLM_IP_ACESSO                    = TB_OPERACAO_LIBERA_MARGEM + ".olm_ip_acesso";
    public static final String OLM_BLOQUEIO                     = TB_OPERACAO_LIBERA_MARGEM + ".olm_bloqueio";
    public static final String OLM_CONFIRMADA                   = TB_OPERACAO_LIBERA_MARGEM + ".olm_confirmada";
    public static final String OLM_ADE_CODIGO                   = TB_OPERACAO_LIBERA_MARGEM + ".ade_codigo";

    public static final String TB_OPERACAO_NAO_CONFIRMADA       = "tb_operacao_nao_confirmada";
    public static final String ONC_CODIGO                       = TB_OPERACAO_NAO_CONFIRMADA + ".onc_codigo";
    public static final String ONC_ACR_CODIGO                   = TB_OPERACAO_NAO_CONFIRMADA + ".acr_codigo";
    public static final String ONC_USU_CODIGO                   = TB_OPERACAO_NAO_CONFIRMADA + ".usu_codigo";
    public static final String ONC_IP_ACESSO                    = TB_OPERACAO_NAO_CONFIRMADA + ".onc_ip_acesso";
    public static final String ONC_DETALHE                      = TB_OPERACAO_NAO_CONFIRMADA + ".onc_detalhe";
    public static final String ONC_PARAMETROS                   = TB_OPERACAO_NAO_CONFIRMADA + ".onc_parametros";
    public static final String ONC_DATA                         = TB_OPERACAO_NAO_CONFIRMADA + ".onc_data";
    public static final String ONC_RSE_CODIGO                   = TB_OPERACAO_NAO_CONFIRMADA + ".rse_codigo";

    public static final String TB_PADRAO_REGISTRO_SERVIDOR      = "tb_padrao_registro_servidor";
    public static final String PRS_CODIGO                       = TB_PADRAO_REGISTRO_SERVIDOR + ".prs_codigo";
    public static final String PRS_IDENTIFICADOR                = TB_PADRAO_REGISTRO_SERVIDOR + ".prs_identificador";
    public static final String PRS_DESCRICAO                    = TB_PADRAO_REGISTRO_SERVIDOR + ".prs_descricao";

    public static final String TB_POSTO_REGISTRO_SERVIDOR      = "tb_posto_registro_servidor";
    public static final String POS_CODIGO                      = TB_POSTO_REGISTRO_SERVIDOR + ".pos_codigo";
    public static final String POS_DESCRICAO                   = TB_POSTO_REGISTRO_SERVIDOR + ".pos_descricao";
    public static final String POS_IDENTIFICADOR               = TB_POSTO_REGISTRO_SERVIDOR + ".pos_identificador";
    public static final String POS_PERC_TAXA_USO               = TB_POSTO_REGISTRO_SERVIDOR + ".pos_perc_tx_uso";
    public static final String POS_PERC_TAXA_USO_COND          = TB_POSTO_REGISTRO_SERVIDOR + ".pos_perc_tx_uso_cond";
    public static final String POS_VALOR_SOLDO                 = TB_POSTO_REGISTRO_SERVIDOR + ".pos_vlr_soldo";

    public static final String TB_TIPO_REGISTRO_SERVIDOR       = "tb_tipo_registro_servidor";
    public static final String TRS_CODIGO                      = TB_TIPO_REGISTRO_SERVIDOR + ".trs_codigo";
    public static final String TRS_DESCRICAO                   = TB_TIPO_REGISTRO_SERVIDOR + ".trs_descricao";

    public static final String TB_CAPACIDADE_REGISTRO_SERVIDOR = "tb_capacidade_registro_ser";
    public static final String CAP_CODIGO                      = TB_CAPACIDADE_REGISTRO_SERVIDOR + ".cap_codigo";
    public static final String CAP_DESCRICAO                   = TB_CAPACIDADE_REGISTRO_SERVIDOR + ".cap_descricao";

    public static final String TB_PAPEL                         = "tb_papel";
    public static final String PAP_CODIGO                       = TB_PAPEL + ".pap_codigo";
    public static final String PAP_DESCRICAO                    = TB_PAPEL + ".pap_descricao";

    public static final String TB_PAPEL_FUNCAO                  = "tb_papel_funcao";
    public static final String PF_PAP_CODIGO                    = TB_PAPEL_FUNCAO + ".pap_codigo";
    public static final String PF_FUN_CODIGO                    = TB_PAPEL_FUNCAO + ".fun_codigo";

    public static final String TB_PARAM_CNV_REGISTRO_SERVIDOR   = "tb_param_convenio_registro_ser";
    public static final String PCR_TPS_CODIGO                   = TB_PARAM_CNV_REGISTRO_SERVIDOR + ".tps_codigo";
    public static final String PCR_CNV_CODIGO                   = TB_PARAM_CNV_REGISTRO_SERVIDOR + ".cnv_codigo";
    public static final String PCR_RSE_CODIGO                   = TB_PARAM_CNV_REGISTRO_SERVIDOR + ".rse_codigo";
    public static final String PCR_VLR                          = TB_PARAM_CNV_REGISTRO_SERVIDOR + ".pcr_vlr";
    public static final String PCR_VLR_SER                      = TB_PARAM_CNV_REGISTRO_SERVIDOR + ".pcr_vlr_ser";
    public static final String PCR_VLR_CSA                      = TB_PARAM_CNV_REGISTRO_SERVIDOR + ".pcr_vlr_csa";
    public static final String PCR_VLR_CSE                      = TB_PARAM_CNV_REGISTRO_SERVIDOR + ".pcr_vlr_cse";
    public static final String PCR_OBS                          = TB_PARAM_CNV_REGISTRO_SERVIDOR + ".pcr_obs";
    public static final String PCR_DATA_CADASTRO                = TB_PARAM_CNV_REGISTRO_SERVIDOR + ".pcr_data_cadastro";

    public static final String TB_PARAM_SIST_CONSIGNANTE        = "tb_param_sist_consignante";
    public static final String PSI_TPC_CODIGO                   = TB_PARAM_SIST_CONSIGNANTE + ".tpc_codigo";
    public static final String PSI_CSE_CODIGO                   = TB_PARAM_SIST_CONSIGNANTE + ".cse_codigo";
    public static final String PSI_VLR                          = TB_PARAM_SIST_CONSIGNANTE + ".psi_vlr";

    public static final String TB_PARAM_ORGAO                   = "tb_param_orgao";
    public static final String PAO_ORG_CODIGO                   = TB_PARAM_ORGAO + ".org_codigo";
    public static final String PAO_TAO_CODIGO                   = TB_PARAM_ORGAO + ".tao_codigo";
    public static final String PAO_VLR                          = TB_PARAM_ORGAO + ".pao_vlr";

    public static final String TB_PARAM_SERVICO_AUTORIZACAO_DESCONTO = "tb_param_servico_autorizacao";
    public static final String PSAD_PSC_CODIGO                       = TB_PARAM_SERVICO_AUTORIZACAO_DESCONTO + ".psc_codigo";
    public static final String PSAD_ADE_CODIGO                       = TB_PARAM_SERVICO_AUTORIZACAO_DESCONTO + ".ade_codigo";

    public static final String TB_PARAM_SERVICO_REGISTRO_SERVIDOR = "tb_param_servico_registro_ser";
    public static final String PSR_TPS_CODIGO                     = TB_PARAM_SERVICO_REGISTRO_SERVIDOR + ".tps_codigo";
    public static final String PSR_SVC_CODIGO                     = TB_PARAM_SERVICO_REGISTRO_SERVIDOR + ".svc_codigo";
    public static final String PSR_RSE_CODIGO                     = TB_PARAM_SERVICO_REGISTRO_SERVIDOR + ".rse_codigo";
    public static final String PSR_VLR                            = TB_PARAM_SERVICO_REGISTRO_SERVIDOR + ".psr_vlr";
    public static final String PSR_OBS                            = TB_PARAM_SERVICO_REGISTRO_SERVIDOR + ".psr_obs";
    public static final String PSR_ALTERADO_PELO_SERVIDOR         = TB_PARAM_SERVICO_REGISTRO_SERVIDOR + ".psr_alterado_pelo_servidor";
    public static final String PSR_DATA_CADASTRO                  = TB_PARAM_SERVICO_REGISTRO_SERVIDOR + ".psr_data_cadastro";

    public static final String TB_PARAM_CSA_REGISTRO_SERVIDOR 	  = "tb_param_csa_registro_ser";
    public static final String PRC_CSA_CODIGO                     = TB_PARAM_CSA_REGISTRO_SERVIDOR + ".csa_codigo";
    public static final String PRC_RSE_CODIGO                     = TB_PARAM_CSA_REGISTRO_SERVIDOR + ".rse_codigo";
    public static final String PRC_TPA_CODIGO                     = TB_PARAM_CSA_REGISTRO_SERVIDOR + ".tpa_codigo";
    public static final String PRC_DATA_CADASTRO                  = TB_PARAM_CSA_REGISTRO_SERVIDOR + ".prc_data_cadastro";
    public static final String PRC_VLR                            = TB_PARAM_CSA_REGISTRO_SERVIDOR + ".prc_vlr";
    public static final String PRC_OBS                            = TB_PARAM_CSA_REGISTRO_SERVIDOR + ".prc_obs";

    public static final String TB_PARAM_NSE_REGISTRO_SERVIDOR   = "tb_param_nse_registro_ser";
    public static final String PNR_TPS_CODIGO                   = TB_PARAM_NSE_REGISTRO_SERVIDOR + ".tps_codigo";
    public static final String PNR_NSE_CODIGO                   = TB_PARAM_NSE_REGISTRO_SERVIDOR + ".nse_codigo";
    public static final String PNR_RSE_CODIGO                   = TB_PARAM_NSE_REGISTRO_SERVIDOR + ".rse_codigo";
    public static final String PNR_VLR                          = TB_PARAM_NSE_REGISTRO_SERVIDOR + ".pnr_vlr";
    public static final String PNR_OBS                          = TB_PARAM_NSE_REGISTRO_SERVIDOR + ".pnr_obs";
    public static final String PNR_ALTERADO_PELO_SERVIDOR       = TB_PARAM_NSE_REGISTRO_SERVIDOR + ".pnr_alterado_pelo_servidor";
    public static final String PNR_DATA_CADASTRO                = TB_PARAM_NSE_REGISTRO_SERVIDOR + ".pnr_data_cadastro";

    public static final String TB_PARAM_SVC_CONSIGNANTE         = "tb_param_svc_consignante";
    public static final String PSE_CODIGO                       = TB_PARAM_SVC_CONSIGNANTE + ".pse_codigo";
    public static final String PSE_SVC_CODIGO                   = TB_PARAM_SVC_CONSIGNANTE + ".svc_codigo";
    public static final String PSE_TPS_CODIGO                   = TB_PARAM_SVC_CONSIGNANTE + ".tps_codigo";
    public static final String PSE_CSE_CODIGO                   = TB_PARAM_SVC_CONSIGNANTE + ".cse_codigo";
    public static final String PSE_VLR                          = TB_PARAM_SVC_CONSIGNANTE + ".pse_vlr";
    public static final String PSE_VLR_REF                      = TB_PARAM_SVC_CONSIGNANTE + ".pse_vlr_ref";

    public static final String TB_PARAM_SVC_CONSIGNATARIA       = "tb_param_svc_consignataria";
    public static final String PSC_CODIGO                       = TB_PARAM_SVC_CONSIGNATARIA + ".psc_codigo";
    public static final String PSC_TPS_CODIGO                   = TB_PARAM_SVC_CONSIGNATARIA + ".tps_codigo";
    public static final String PSC_DATA_INI_VIG                 = TB_PARAM_SVC_CONSIGNATARIA + ".psc_data_ini_vig";
    public static final String PSC_DATA_FIM_VIG                 = TB_PARAM_SVC_CONSIGNATARIA + ".psc_data_fim_vig";
    public static final String PSC_ATIVO                        = TB_PARAM_SVC_CONSIGNATARIA + ".psc_ativo";
    public static final String PSC_VLR                          = TB_PARAM_SVC_CONSIGNATARIA + ".psc_vlr";
    public static final String PSC_VLR_REF                      = TB_PARAM_SVC_CONSIGNATARIA + ".psc_vlr_ref";
    public static final String PSC_SVC_CODIGO                   = TB_PARAM_SVC_CONSIGNATARIA + ".svc_codigo";
    public static final String PSC_CSA_CODIGO                   = TB_PARAM_SVC_CONSIGNATARIA + ".csa_codigo";

    public static final String TB_PARAM_SVC_CORRESPONDENTE      = "tb_param_svc_correspondente";
    public static final String PSO_CODIGO                       = TB_PARAM_SVC_CORRESPONDENTE + ".pso_codigo";
    public static final String PSO_TPS_CODIGO                   = TB_PARAM_SVC_CORRESPONDENTE + ".tps_codigo";
    public static final String PSO_DATA_INI_VIG                 = TB_PARAM_SVC_CORRESPONDENTE + ".pso_data_ini_vig";
    public static final String PSO_DATA_FIM_VIG                 = TB_PARAM_SVC_CORRESPONDENTE + ".pso_data_fim_vig";
    public static final String PSO_ATIVO                        = TB_PARAM_SVC_CORRESPONDENTE + ".pso_ativo";
    public static final String PSO_VLR                          = TB_PARAM_SVC_CORRESPONDENTE + ".pso_vlr";
    public static final String PSO_VLR_REF                      = TB_PARAM_SVC_CORRESPONDENTE + ".pso_vlr_ref";
    public static final String PSO_SVC_CODIGO                   = TB_PARAM_SVC_CORRESPONDENTE + ".svc_codigo";
    public static final String PSO_COR_CODIGO                   = TB_PARAM_SVC_CORRESPONDENTE + ".cor_codigo";

    public static final String TB_PARAM_POSTO_CSA_SVC           = "tb_param_posto_csa_svc";
    public static final String PSP_TPS_CODIGO                   = TB_PARAM_POSTO_CSA_SVC + ".tps_codigo";
    public static final String PSP_POS_CODIGO                   = TB_PARAM_POSTO_CSA_SVC + ".pos_codigo";
    public static final String PSP_CSA_CODIGO                   = TB_PARAM_POSTO_CSA_SVC + ".csa_codigo";
    public static final String PSP_SVC_CODIGO                   = TB_PARAM_POSTO_CSA_SVC + ".svc_codigo";
    public static final String PSP_PPO_VALOR                    = TB_PARAM_POSTO_CSA_SVC + ".ppo_vlr";

    public static final String TB_PARAM_TARIF_ADMIN             = "tb_param_tarif_admin";
    public static final String PTF_CODIGO                       = TB_PARAM_TARIF_ADMIN + ".ptf_codigo";
    public static final String PTF_CNV_CODIGO                   = TB_PARAM_TARIF_ADMIN + ".cnv_codigo";
    public static final String PTF_DATA_INI_VIG                 = TB_PARAM_TARIF_ADMIN + ".ptf_data_ini_vig";
    public static final String PTF_DATA_FIM_VIG                 = TB_PARAM_TARIF_ADMIN + ".ptf_data_fim_vig";
    public static final String PTF_ATIVO                        = TB_PARAM_TARIF_ADMIN + ".ptf_ativo";
    public static final String PTF_TIP_VLR                      = TB_PARAM_TARIF_ADMIN + ".ptf_tip_vlr";
    public static final String PTF_VLR                          = TB_PARAM_TARIF_ADMIN + ".ptf_vlr";

    public static final String TB_PARAM_TARIF_CONSIGNANTE       = "tb_param_tarif_consignante";
    public static final String PCV_CODIGO                       = TB_PARAM_TARIF_CONSIGNANTE + ".pcv_codigo";
    public static final String PCV_CSE_CODIGO                   = TB_PARAM_TARIF_CONSIGNANTE + ".cse_codigo";
    public static final String PCV_SVC_CODIGO                   = TB_PARAM_TARIF_CONSIGNANTE + ".svc_codigo";
    public static final String PCV_TPT_CODIGO                   = TB_PARAM_TARIF_CONSIGNANTE + ".tpt_codigo";
    public static final String PCV_DATA_INI_VIG                 = TB_PARAM_TARIF_CONSIGNANTE + ".pcv_data_ini_vig";
    public static final String PCV_DATA_FIM_VIG                 = TB_PARAM_TARIF_CONSIGNANTE + ".pcv_data_fim_vig";
    public static final String PCV_ATIVO                        = TB_PARAM_TARIF_CONSIGNANTE + ".pcv_ativo";
    public static final String PCV_VLR                          = TB_PARAM_TARIF_CONSIGNANTE + ".pcv_vlr";
    public static final String PCV_BASE_CALC                    = TB_PARAM_TARIF_CONSIGNANTE + ".pcv_base_calc";
    public static final String PCV_FORMA_CALC                   = TB_PARAM_TARIF_CONSIGNANTE + ".pcv_forma_calc";
    public static final String PCV_DECIMAIS                     = TB_PARAM_TARIF_CONSIGNANTE + ".pcv_decimais";
    public static final String PCV_VLR_INI                      = TB_PARAM_TARIF_CONSIGNANTE + ".pcv_vlr_ini";
    public static final String PCV_VLR_FIM                      = TB_PARAM_TARIF_CONSIGNANTE + ".pcv_vlr_fim";

    public static final String TB_PARCELA_DESCONTO              = "tb_parcela_desconto";
    public static final String PRD_CODIGO                       = TB_PARCELA_DESCONTO + ".prd_codigo";
    public static final String PRD_ADE_CODIGO                   = TB_PARCELA_DESCONTO + ".ade_codigo";
    public static final String PRD_SPD_CODIGO                   = TB_PARCELA_DESCONTO + ".spd_codigo";
    public static final String PRD_NUMERO                       = TB_PARCELA_DESCONTO + ".prd_numero";
    public static final String PRD_DATA_DESCONTO                = TB_PARCELA_DESCONTO + ".prd_data_desconto";
    public static final String PRD_VLR_PREVISTO                 = TB_PARCELA_DESCONTO + ".prd_vlr_previsto";
    public static final String PRD_VLR_REALIZADO                = TB_PARCELA_DESCONTO + ".prd_vlr_realizado";
    public static final String PRD_DATA_REALIZADO               = TB_PARCELA_DESCONTO + ".prd_data_realizado";
    public static final String PRD_TDE_CODIGO                   = TB_PARCELA_DESCONTO + ".tde_codigo";
    public static final String PRD_MNE_CODIGO                   = TB_PARCELA_DESCONTO + ".mne_codigo";

    public static final String TB_PARCELA_DESCONTO_PERIODO      = "tb_parcela_desconto_periodo";
    public static final String PDP_CODIGO                       = TB_PARCELA_DESCONTO_PERIODO + ".prd_codigo";
    public static final String PDP_ADE_CODIGO                   = TB_PARCELA_DESCONTO_PERIODO + ".ade_codigo";
    public static final String PDP_SPD_CODIGO                   = TB_PARCELA_DESCONTO_PERIODO + ".spd_codigo";
    public static final String PDP_NUMERO                       = TB_PARCELA_DESCONTO_PERIODO + ".prd_numero";
    public static final String PDP_DATA_DESCONTO                = TB_PARCELA_DESCONTO_PERIODO + ".prd_data_desconto";
    public static final String PDP_VLR_PREVISTO                 = TB_PARCELA_DESCONTO_PERIODO + ".prd_vlr_previsto";
    public static final String PDP_VLR_REALIZADO                = TB_PARCELA_DESCONTO_PERIODO + ".prd_vlr_realizado";
    public static final String PDP_DATA_REALIZADO               = TB_PARCELA_DESCONTO_PERIODO + ".prd_data_realizado";
    public static final String PDP_TDE_CODIGO                   = TB_PARCELA_DESCONTO_PERIODO + ".tde_codigo";
    public static final String PDP_MNE_CODIGO                   = TB_PARCELA_DESCONTO_PERIODO + ".mne_codigo";

    public static final String TB_PERIODO_EXPORTACAO            = "tb_periodo_exportacao";
    public static final String PEX_ORG_CODIGO                   = TB_PERIODO_EXPORTACAO + ".org_codigo";
    public static final String PEX_DIA_CORTE                    = TB_PERIODO_EXPORTACAO + ".pex_dia_corte";
    public static final String PEX_DATA_INI                     = TB_PERIODO_EXPORTACAO + ".pex_data_ini";
    public static final String PEX_DATA_FIM                     = TB_PERIODO_EXPORTACAO + ".pex_data_fim";
    public static final String PEX_PERIODO                      = TB_PERIODO_EXPORTACAO + ".pex_periodo";
    public static final String PEX_PERIODO_ANT                  = TB_PERIODO_EXPORTACAO + ".pex_periodo_ant";
    public static final String PEX_PERIODO_POS                  = TB_PERIODO_EXPORTACAO + ".pex_periodo_pos";
    public static final String PEX_SEQUENCIA                    = TB_PERIODO_EXPORTACAO + ".pex_sequencia";
    public static final String PEX_NUM_PERIODO                  = TB_PERIODO_EXPORTACAO + ".pex_num_periodo";

    public static final String TB_PERFIL                        = "tb_perfil";
    public static final String PER_CODIGO                       = TB_PERFIL + ".per_codigo";
    public static final String PER_PAP_CODIGO                   = TB_PERFIL + ".pap_codigo";
    public static final String PER_DESCRICAO                    = TB_PERFIL + ".per_descricao";
    public static final String PER_VISIVEL                      = TB_PERFIL + ".per_visivel";
    public static final String PER_DATA_EXPIRACAO               = TB_PERFIL + ".per_data_expiracao";
    public static final String PER_ENT_ALTERA                   = TB_PERFIL + ".per_ent_altera";
    public static final String PER_AUTO_DESBLOQUEIO             = TB_PERFIL + ".per_auto_desbloqueio";
    public static final String PER_IP_ACESSO                    = TB_PERFIL + ".per_ip_acesso";
    public static final String PER_DDNS_ACESSO                  = TB_PERFIL + ".per_ddns_acesso";

    public static final String TB_PERFIL_CSE                    = "tb_perfil_cse";
    public static final String PCE_PER_CODIGO                   = TB_PERFIL_CSE + ".per_codigo";
    public static final String PCE_CSE_CODIGO                   = TB_PERFIL_CSE + ".cse_codigo";
    public static final String PCE_ATIVO                        = TB_PERFIL_CSE + ".pce_ativo";

    public static final String TB_PERFIL_CSA                    = "tb_perfil_csa";
    public static final String PCA_PER_CODIGO                   = TB_PERFIL_CSA + ".per_codigo";
    public static final String PCA_CSA_CODIGO                   = TB_PERFIL_CSA + ".csa_codigo";
    public static final String PCA_ATIVO                        = TB_PERFIL_CSA + ".pca_ativo";

    public static final String TB_PERFIL_COR                    = "tb_perfil_cor";
    public static final String PCO_PER_CODIGO                   = TB_PERFIL_COR + ".per_codigo";
    public static final String PCO_COR_CODIGO                   = TB_PERFIL_COR + ".cor_codigo";
    public static final String PCO_ATIVO                        = TB_PERFIL_COR + ".pco_ativo";

    public static final String TB_PERFIL_ORG                    = "tb_perfil_org";
    public static final String POR_PER_CODIGO                   = TB_PERFIL_ORG + ".per_codigo";
    public static final String POR_ORG_CODIGO                   = TB_PERFIL_ORG + ".org_codigo";
    public static final String POR_ATIVO                        = TB_PERFIL_ORG + ".por_ativo";

    public static final String TB_PERFIL_SUP                    = "tb_perfil_sup";
    public static final String PSU_CSE_CODIGO                   = TB_PERFIL_SUP + ".cse_codigo";
    public static final String PSU_PER_CODIGO                   = TB_PERFIL_SUP + ".per_codigo";
    public static final String PSU_ATIVO                        = TB_PERFIL_SUP + ".psu_ativo";

    public static final String TB_PERFIL_USUARIO                = "tb_perfil_usuario";
    public static final String UPE_PER_CODIGO                   = TB_PERFIL_USUARIO + ".per_codigo";
    public static final String UPE_USU_CODIGO                   = TB_PERFIL_USUARIO + ".usu_codigo";

    public static final String TB_PERGUNTA_DADOS_CADASTRAIS     = "tb_pergunta_dados_cadastrais";
    public static final String PDC_GRUPO                        = TB_PERGUNTA_DADOS_CADASTRAIS + ".pdc_grupo";
    public static final String PDC_NUMERO                       = TB_PERGUNTA_DADOS_CADASTRAIS + ".pdc_numero";
    public static final String PDC_STATUS                       = TB_PERGUNTA_DADOS_CADASTRAIS + ".pdc_status";
    public static final String PDC_TEXTO                        = TB_PERGUNTA_DADOS_CADASTRAIS + ".pdc_texto";
    public static final String PDC_CAMPO                        = TB_PERGUNTA_DADOS_CADASTRAIS + ".pdc_campo";

    public static final String TB_PERMISSIONARIO                = "tb_permissionario";
    public static final String PRM_CODIGO                       = TB_PERMISSIONARIO + ".prm_codigo";
    public static final String PRM_RSE_CODIGO                   = TB_PERMISSIONARIO + ".rse_codigo";
    public static final String PRM_CSA_CODIGO                   = TB_PERMISSIONARIO + ".csa_codigo";
    public static final String PRM_ECH_CODIGO                   = TB_PERMISSIONARIO + ".ech_codigo";
    public static final String PRM_TELEFONE                     = TB_PERMISSIONARIO + ".prm_telefone";
    public static final String PRM_EMAIL                        = TB_PERMISSIONARIO + ".prm_email";
    public static final String PRM_COMPL_ENDERECO               = TB_PERMISSIONARIO + ".prm_compl_endereco";
    public static final String PRM_DATA_CADASTRO                = TB_PERMISSIONARIO + ".prm_data_cadastro";
    public static final String PRM_DATA_OCUPACAO                = TB_PERMISSIONARIO + ".prm_data_ocupacao";
    public static final String PRM_DATA_DESOCUPACAO             = TB_PERMISSIONARIO + ".prm_data_desocupacao";
    public static final String PRM_EM_TRANSFERENCIA             = TB_PERMISSIONARIO + ".prm_em_transferencia";
    public static final String PRM_ATIVO                        = TB_PERMISSIONARIO + ".prm_ativo";

    public static final String TB_PRAZO                         = "tb_prazo";
    public static final String PRZ_CODIGO                       = TB_PRAZO + ".prz_codigo";
    public static final String PRZ_SVC_CODIGO                   = TB_PRAZO + ".svc_codigo";
    public static final String PRZ_VLR                          = TB_PRAZO + ".prz_vlr";
    public static final String PRZ_ATIVO                        = TB_PRAZO + ".prz_ativo";

    public static final String TB_PRAZO_CONSIGNATARIA           = "tb_prazo_consignataria";
    public static final String PZC_CODIGO                       = TB_PRAZO_CONSIGNATARIA + ".prz_csa_codigo";
    public static final String PZC_CSA_CODIGO                   = TB_PRAZO_CONSIGNATARIA + ".csa_codigo";
    public static final String PZC_PRZ_CODIGO                   = TB_PRAZO_CONSIGNATARIA + ".prz_codigo";
    public static final String PZC_ATIVO                        = TB_PRAZO_CONSIGNATARIA + ".prz_csa_ativo";

    public static final String TB_PROTOCOLO_SENHA_AUTORIZACAO   = "tb_protocolo_senha_autorizacao";
    public static final String PSA_CODIGO                       = TB_PROTOCOLO_SENHA_AUTORIZACAO + ".psa_codigo";
    public static final String PSA_USU_CODIGO_AFETADO           = TB_PROTOCOLO_SENHA_AUTORIZACAO + ".usu_codigo_afetado";
    public static final String PSA_USU_CODIGO_RESPONSAVEL       = TB_PROTOCOLO_SENHA_AUTORIZACAO + ".usu_codigo_responsavel";
    public static final String PSA_DATA                         = TB_PROTOCOLO_SENHA_AUTORIZACAO + ".psa_data";

    public static final String TB_REGRA_LIMITE_OPERACAO    = "tb_regra_limite_operacao";
    public static final String RLO_CODIGO                  = TB_REGRA_LIMITE_OPERACAO + ".rlo_codigo";
    public static final String RLO_USU_CODIGO              = TB_REGRA_LIMITE_OPERACAO + ".usu_codigo";
    public static final String RLO_DATA_CADASTRO           = TB_REGRA_LIMITE_OPERACAO + ".rlo_data_cadastro";
    public static final String RLO_DATA_VIGENCIA_INI       = TB_REGRA_LIMITE_OPERACAO + ".rlo_data_vigencia_ini";
    public static final String RLO_DATA_VIGENCIA_FIM       = TB_REGRA_LIMITE_OPERACAO + ".rlo_data_vigencia_fim";
    public static final String RLO_FAIXA_ETARIA_INI        = TB_REGRA_LIMITE_OPERACAO + ".rlo_faixa_etaria_ini";
    public static final String RLO_FAIXA_ETARIA_FIM        = TB_REGRA_LIMITE_OPERACAO + ".rlo_faixa_etaria_fim";
    public static final String RLO_FAIXA_TEMPO_SERVICO_INI = TB_REGRA_LIMITE_OPERACAO + ".rlo_faixa_tempo_servico_ini";
    public static final String RLO_FAIXA_TEMPO_SERVICO_FIM = TB_REGRA_LIMITE_OPERACAO + ".rlo_faixa_tempo_servico_fim";
    public static final String RLO_FAIXA_SALARIO_INI       = TB_REGRA_LIMITE_OPERACAO + ".rlo_faixa_salario_ini";
    public static final String RLO_FAIXA_SALARIO_FIM       = TB_REGRA_LIMITE_OPERACAO + ".rlo_faixa_salario_fim";
    public static final String RLO_FAIXA_MARGEM_FOLHA_INI  = TB_REGRA_LIMITE_OPERACAO + ".rlo_faixa_margem_folha_ini";
    public static final String RLO_FAIXA_MARGEM_FOLHA_FIM  = TB_REGRA_LIMITE_OPERACAO + ".rlo_faixa_margem_folha_fim";
    public static final String RLO_PADRAO_MATRICULA        = TB_REGRA_LIMITE_OPERACAO + ".rlo_padrao_matricula";
    public static final String RLO_PADRAO_CATEGORIA        = TB_REGRA_LIMITE_OPERACAO + ".rlo_padrao_categoria";
    public static final String RLO_PADRAO_VERBA            = TB_REGRA_LIMITE_OPERACAO + ".rlo_padrao_verba";
    public static final String RLO_PADRAO_VERBA_REF        = TB_REGRA_LIMITE_OPERACAO + ".rlo_padrao_verba_ref";
    public static final String RLO_MENSAGEM_ERRO           = TB_REGRA_LIMITE_OPERACAO + ".rlo_mensagem_erro";
    public static final String RLO_LIMITE_QUANTIDADE       = TB_REGRA_LIMITE_OPERACAO + ".rlo_limite_quantidade";
    public static final String RLO_LIMITE_DATA_FIM_ADE     = TB_REGRA_LIMITE_OPERACAO + ".rlo_limite_data_fim_ade";
    public static final String RLO_LIMITE_PRAZO            = TB_REGRA_LIMITE_OPERACAO + ".rlo_limite_prazo";
    public static final String RLO_LIMITE_VALOR_PARCELA    = TB_REGRA_LIMITE_OPERACAO + ".rlo_limite_valor_parcela";
    public static final String RLO_LIMITE_VALOR_LIBERADO   = TB_REGRA_LIMITE_OPERACAO + ".rlo_limite_valor_liberado";
    public static final String RLO_LIMITE_CAPITAL_DEVIDO   = TB_REGRA_LIMITE_OPERACAO + ".rlo_limite_capital_devido";
    public static final String RLO_EST_CODIGO              = TB_REGRA_LIMITE_OPERACAO + ".est_codigo";
    public static final String RLO_ORG_CODIGO              = TB_REGRA_LIMITE_OPERACAO + ".org_codigo";
    public static final String RLO_SBO_CODIGO              = TB_REGRA_LIMITE_OPERACAO + ".sbo_codigo";
    public static final String RLO_UNI_CODIGO              = TB_REGRA_LIMITE_OPERACAO + ".uni_codigo";
    public static final String RLO_SVC_CODIGO              = TB_REGRA_LIMITE_OPERACAO + ".svc_codigo";
    public static final String RLO_NSE_CODIGO              = TB_REGRA_LIMITE_OPERACAO + ".nse_codigo";
    public static final String RLO_NCA_CODIGO              = TB_REGRA_LIMITE_OPERACAO + ".nca_codigo";
    public static final String RLO_CSA_CODIGO              = TB_REGRA_LIMITE_OPERACAO + ".csa_codigo";
    public static final String RLO_COR_CODIGO              = TB_REGRA_LIMITE_OPERACAO + ".cor_codigo";
    public static final String RLO_CRS_CODIGO              = TB_REGRA_LIMITE_OPERACAO + ".crs_codigo";
    public static final String RLO_CAP_CODIGO              = TB_REGRA_LIMITE_OPERACAO + ".cap_codigo";
    public static final String RLO_PRS_CODIGO              = TB_REGRA_LIMITE_OPERACAO + ".prs_codigo";
    public static final String RLO_POS_CODIGO              = TB_REGRA_LIMITE_OPERACAO + ".pos_codigo";
    public static final String RLO_SRS_CODIGO              = TB_REGRA_LIMITE_OPERACAO + ".srs_codigo";
    public static final String RLO_TRS_CODIGO              = TB_REGRA_LIMITE_OPERACAO + ".trs_codigo";
    public static final String RLO_VRS_CODIGO              = TB_REGRA_LIMITE_OPERACAO + ".vrs_codigo";
    public static final String RLO_FUN_CODIGO              = TB_REGRA_LIMITE_OPERACAO + ".fun_codigo";

    public static final String TB_REGRA_VALIDACAO_MOVIMENTO     = "tb_regra_validacao_movimento";
    public static final String RVM_CODIGO                       = TB_REGRA_VALIDACAO_MOVIMENTO + ".rvm_codigo";
    public static final String RVM_IDENTIFICADOR                = TB_REGRA_VALIDACAO_MOVIMENTO + ".rvm_identificador";
    public static final String RVM_DESCRICAO                    = TB_REGRA_VALIDACAO_MOVIMENTO + ".rvm_descricao";
    public static final String RVM_ATIVO                        = TB_REGRA_VALIDACAO_MOVIMENTO + ".rvm_ativo";
    public static final String RVM_JAVA_CLASS_NAME              = TB_REGRA_VALIDACAO_MOVIMENTO + ".rvm_java_class_name";
    public static final String RVM_SEQUENCIA                    = TB_REGRA_VALIDACAO_MOVIMENTO + ".rvm_sequencia";
    public static final String RVM_INVALIDA_MOVIMENTO           = TB_REGRA_VALIDACAO_MOVIMENTO + ".rvm_invalida_movimento";
    public static final String RVM_LIMITE_ERRO                  = TB_REGRA_VALIDACAO_MOVIMENTO + ".rvm_limite_erro";
    public static final String RVM_LIMITE_AVISO                 = TB_REGRA_VALIDACAO_MOVIMENTO + ".rvm_limite_aviso";

    public static final String TB_REGISTRO_SERVIDOR             = "tb_registro_servidor";
    public static final String RSE_CODIGO                       = TB_REGISTRO_SERVIDOR + ".rse_codigo";
    public static final String RSE_SER_CODIGO                   = TB_REGISTRO_SERVIDOR + ".ser_codigo";
    public static final String RSE_ORG_CODIGO                   = TB_REGISTRO_SERVIDOR + ".org_codigo";
    public static final String RSE_MAR_CODIGO                   = TB_REGISTRO_SERVIDOR + ".mar_codigo";
    public static final String RSE_MATRICULA                    = TB_REGISTRO_SERVIDOR + ".rse_matricula";
    public static final String RSE_MARGEM                       = TB_REGISTRO_SERVIDOR + ".rse_margem";
    public static final String RSE_MARGEM_REST                  = TB_REGISTRO_SERVIDOR + ".rse_margem_rest";
    public static final String RSE_MARGEM_USADA                 = TB_REGISTRO_SERVIDOR + ".rse_margem_usada";
    public static final String RSE_MEDIA_MARGEM                 = TB_REGISTRO_SERVIDOR + ".rse_media_margem";
    public static final String RSE_TIPO                         = TB_REGISTRO_SERVIDOR + ".rse_tipo";
    public static final String RSE_PRAZO                        = TB_REGISTRO_SERVIDOR + ".rse_prazo";
    public static final String RSE_DATA_ADMISSAO                = TB_REGISTRO_SERVIDOR + ".rse_data_admissao";
    public static final String RSE_BCO_CODIGO                   = TB_REGISTRO_SERVIDOR + ".bco_codigo";
    public static final String RSE_AGENCIA_SAL                  = TB_REGISTRO_SERVIDOR + ".rse_agencia_sal";
    public static final String RSE_AGENCIA_DV_SAL               = TB_REGISTRO_SERVIDOR + ".rse_agencia_dv_sal";
    public static final String RSE_CONTA_SAL                    = TB_REGISTRO_SERVIDOR + ".rse_conta_sal";
    public static final String RSE_CONTA_DV_SAL                 = TB_REGISTRO_SERVIDOR + ".rse_conta_dv_sal";
    public static final String RSE_MARGEM_2                     = TB_REGISTRO_SERVIDOR + ".rse_margem_2";
    public static final String RSE_MARGEM_REST_2                = TB_REGISTRO_SERVIDOR + ".rse_margem_rest_2";
    public static final String RSE_MARGEM_USADA_2               = TB_REGISTRO_SERVIDOR + ".rse_margem_usada_2";
    public static final String RSE_MEDIA_MARGEM_2               = TB_REGISTRO_SERVIDOR + ".rse_media_margem_2";
    public static final String RSE_MARGEM_3                     = TB_REGISTRO_SERVIDOR + ".rse_margem_3";
    public static final String RSE_MARGEM_REST_3                = TB_REGISTRO_SERVIDOR + ".rse_margem_rest_3";
    public static final String RSE_MARGEM_USADA_3               = TB_REGISTRO_SERVIDOR + ".rse_margem_usada_3";
    public static final String RSE_MEDIA_MARGEM_3               = TB_REGISTRO_SERVIDOR + ".rse_media_margem_3";
    public static final String RSE_SALARIO                      = TB_REGISTRO_SERVIDOR + ".rse_salario";
    public static final String RSE_PROVENTOS                    = TB_REGISTRO_SERVIDOR + ".rse_proventos";
    public static final String RSE_DESCONTOS_FACU               = TB_REGISTRO_SERVIDOR + ".rse_descontos_facu";
    public static final String RSE_DESCONTOS_COMP               = TB_REGISTRO_SERVIDOR + ".rse_descontos_comp";
    public static final String RSE_OUTROS_DESCONTOS             = TB_REGISTRO_SERVIDOR + ".rse_outros_descontos";
    public static final String RSE_DATA_CARGA                   = TB_REGISTRO_SERVIDOR + ".rse_data_carga";
    public static final String RSE_ASSOCIADO                    = TB_REGISTRO_SERVIDOR + ".rse_associado";
    public static final String RSE_DATA_CTC                     = TB_REGISTRO_SERVIDOR + ".rse_data_ctc";
    public static final String RSE_MATRICULA_INST               = TB_REGISTRO_SERVIDOR + ".rse_matricula_inst";
    public static final String RSE_CLT                          = TB_REGISTRO_SERVIDOR + ".rse_clt";
    public static final String RSE_VRS_CODIGO                   = TB_REGISTRO_SERVIDOR + ".vrs_codigo";
    public static final String RSE_BANCO_SAL                    = TB_REGISTRO_SERVIDOR + ".rse_banco_sal";
    public static final String RSE_CRS_CODIGO                   = TB_REGISTRO_SERVIDOR + ".crs_codigo";
    public static final String RSE_PRS_CODIGO                   = TB_REGISTRO_SERVIDOR + ".prs_codigo";
    public static final String RSE_SBO_CODIGO                   = TB_REGISTRO_SERVIDOR + ".sbo_codigo";
    public static final String RSE_UNI_CODIGO                   = TB_REGISTRO_SERVIDOR + ".uni_codigo";
    public static final String RSE_OBS                          = TB_REGISTRO_SERVIDOR + ".rse_obs";
    public static final String RSE_PARAM_QTD_ADE_DEFAULT        = TB_REGISTRO_SERVIDOR + ".rse_param_qtd_ade_default";
    public static final String RSE_SRS_CODIGO                   = TB_REGISTRO_SERVIDOR + ".srs_codigo";
    public static final String RSE_POS_CODIGO                   = TB_REGISTRO_SERVIDOR + ".pos_codigo";
    public static final String RSE_TRS_CODIGO                   = TB_REGISTRO_SERVIDOR + ".trs_codigo";
    public static final String RSE_ESTABILIZADO                 = TB_REGISTRO_SERVIDOR + ".rse_estabilizado";
    public static final String RSE_DATA_FIM_ENGAJAMENTO         = TB_REGISTRO_SERVIDOR + ".rse_data_fim_engajamento";
    public static final String RSE_DATA_LIMITE_PERMANENCIA      = TB_REGISTRO_SERVIDOR + ".rse_data_limite_permanencia";
    public static final String RSE_CAP_CODIGO                   = TB_REGISTRO_SERVIDOR + ".cap_codigo";
    public static final String RSE_BANCO_SAL_2                  = TB_REGISTRO_SERVIDOR + ".rse_banco_sal_2";
    public static final String RSE_AGENCIA_SAL_2                = TB_REGISTRO_SERVIDOR + ".rse_agencia_sal_2";
    public static final String RSE_AGENCIA_DV_SAL_2             = TB_REGISTRO_SERVIDOR + ".rse_agencia_dv_sal_2";
    public static final String RSE_CONTA_SAL_2                  = TB_REGISTRO_SERVIDOR + ".rse_conta_sal_2";
    public static final String RSE_CONTA_DV_SAL_2               = TB_REGISTRO_SERVIDOR + ".rse_conta_dv_sal_2";
    public static final String RSE_USU_CODIGO                   = TB_REGISTRO_SERVIDOR + ".usu_codigo";
    public static final String RSE_DATA_ALTERACAO               = TB_REGISTRO_SERVIDOR + ".rse_data_alteracao";
    public static final String RSE_BASE_CALCULO                 = TB_REGISTRO_SERVIDOR + ".rse_base_calculo";
    public static final String RSE_AUDITORIA_TOTAL              = TB_REGISTRO_SERVIDOR + ".rse_auditoria_total";
    public static final String RSE_MUNICIPIO_LOTACAO            = TB_REGISTRO_SERVIDOR + ".rse_municipio_lotacao";
    public static final String RSE_BENEFICIARIO_FINAN_DV_CART   = TB_REGISTRO_SERVIDOR + ".rse_beneficiario_finan_dv_cart";
    public static final String RSE_PRACA                        = TB_REGISTRO_SERVIDOR + ".rse_praca";
    public static final String RSE_PEDIDO_DEMISSAO              = TB_REGISTRO_SERVIDOR + ".rse_pedido_demissao";
    public static final String RSE_DATA_SAIDA                   = TB_REGISTRO_SERVIDOR + ".rse_data_saida";
    public static final String RSE_DATA_ULT_SALARIO             = TB_REGISTRO_SERVIDOR + ".rse_data_ult_salario";
    public static final String RSE_DATA_RETORNO                 = TB_REGISTRO_SERVIDOR + ".rse_data_retorno";
    public static final String RSE_PONTUACAO                    = TB_REGISTRO_SERVIDOR + ".rse_pontuacao";
    public static final String RSE_MOTIVO_BLOQUEIO              = TB_REGISTRO_SERVIDOR + ".rse_motivo_bloqueio";
    public static final String RSE_MOTIVO_FALTA_MARGEM          = TB_REGISTRO_SERVIDOR + ".rse_motivo_falta_margem";

    public static final String TB_REGISTRO_SER_OCULTO_CSA       = "tb_registro_ser_oculto_csa";
    public static final String ROC_RSE_CODIGO                   = TB_REGISTRO_SERVIDOR + ".rse_codigo";
    public static final String ROC_CSA_CODIGO                   = TB_REGISTRO_SERVIDOR + ".csa_codigo";

    public static final String TB_RESULTADO_REGRA_VALIDACAO_MOVIMENTO = "tb_resultado_regra_valid_mov";
    public static final String RRV_RVA_CODIGO                   = TB_RESULTADO_REGRA_VALIDACAO_MOVIMENTO + ".rva_codigo";
    public static final String RRV_RVM_CODIGO                   = TB_RESULTADO_REGRA_VALIDACAO_MOVIMENTO + ".rvm_codigo";
    public static final String RRV_RESULTADO                    = TB_RESULTADO_REGRA_VALIDACAO_MOVIMENTO + ".rrv_resultado";
    public static final String RRV_VALOR_ENCONTRADO             = TB_RESULTADO_REGRA_VALIDACAO_MOVIMENTO + ".rrv_valor_encontrado";

    public static final String TB_RESULTADO_VALIDACAO_MOVIMENTO = "tb_resultado_validacao_mov";
    public static final String RVA_CODIGO                       = TB_RESULTADO_VALIDACAO_MOVIMENTO + ".rva_codigo";
    public static final String RVA_USU_CODIGO                   = TB_RESULTADO_VALIDACAO_MOVIMENTO + ".usu_codigo";
    public static final String RVA_NOME_ARQUIVO                 = TB_RESULTADO_VALIDACAO_MOVIMENTO + ".rva_nome_arquivo";
    public static final String RVA_PERIODO                      = TB_RESULTADO_VALIDACAO_MOVIMENTO + ".rva_periodo";
    public static final String RVA_RESULTADO                    = TB_RESULTADO_VALIDACAO_MOVIMENTO + ".rva_resultado";
    public static final String RVA_ACEITE                       = TB_RESULTADO_VALIDACAO_MOVIMENTO + ".rva_aceite";
    public static final String RVA_DATA_PROCESSO                = TB_RESULTADO_VALIDACAO_MOVIMENTO + ".rva_data_processo";
    public static final String RVA_DATA_ACEITE                  = TB_RESULTADO_VALIDACAO_MOVIMENTO + ".rva_data_aceite";

    public static final String TB_SALDO_DEVEDOR                 = "tb_saldo_devedor";
    public static final String SDV_ADE_CODIGO                   = TB_SALDO_DEVEDOR + ".ade_codigo";
    public static final String SDV_VALOR                        = TB_SALDO_DEVEDOR + ".sdv_valor";
    public static final String SDV_VALOR_COM_DESCONTO           = TB_SALDO_DEVEDOR + ".sdv_valor_com_desconto";
    public static final String SDV_BCO_CODIGO                   = TB_SALDO_DEVEDOR + ".bco_codigo";
    public static final String SDV_AGENCIA                      = TB_SALDO_DEVEDOR + ".sdv_agencia";
    public static final String SDV_CONTA                        = TB_SALDO_DEVEDOR + ".sdv_conta";
    public static final String SDV_DATA_MOD                     = TB_SALDO_DEVEDOR + ".sdv_data_mod";
    public static final String SDV_USU_CODIGO                   = TB_SALDO_DEVEDOR + ".usu_codigo";
    public static final String SDV_NOME_FAVORECIDO              = TB_SALDO_DEVEDOR + ".sdv_nome_favorecido";
    public static final String SDV_CNPJ                         = TB_SALDO_DEVEDOR + ".sdv_cnpj";
    public static final String SDV_NUMERO_CONTRATO              = TB_SALDO_DEVEDOR + ".sdv_numero_contrato";
    public static final String SDV_LINK_BOLETO_QUITACAO         = TB_SALDO_DEVEDOR + ".sdv_link_boleto_quitacao";
    public static final String SDV_DATA_VALIDADE                = TB_SALDO_DEVEDOR + ".sdv_data_validade";

    public static final String TB_SERVICO                       = "tb_servico";
    public static final String SVC_CODIGO                       = TB_SERVICO + ".svc_codigo";
    public static final String SVC_IDENTIFICADOR                = TB_SERVICO + ".svc_identificador";
    public static final String SVC_DESCRICAO                    = TB_SERVICO + ".svc_descricao";
    public static final String SVC_ATIVO                        = TB_SERVICO + ".svc_ativo";
    public static final String SVC_PRIORIDADE                   = TB_SERVICO + ".svc_prioridade";
    public static final String SVC_OBS                          = TB_SERVICO + ".svc_obs";
    public static final String SVC_TGS_CODIGO                   = TB_SERVICO + ".tgs_codigo";
    public static final String SVC_NSE_CODIGO                   = TB_SERVICO + ".nse_codigo";

    public static final String TB_SERVICO_PERMITE_TDA           = "tb_servico_permite_tda";
    public static final String SPT_SVC_CODIGO                   = TB_SERVICO_PERMITE_TDA + ".svc_codigo";
    public static final String SPT_TDA_CODIGO                   = TB_SERVICO_PERMITE_TDA + ".tda_codigo";
    public static final String SPT_EXIBE                        = TB_SERVICO_PERMITE_TDA + ".spt_exibe";

    public static final String TB_SERVIDOR                      = "tb_servidor";
    public static final String SER_CODIGO                       = TB_SERVIDOR + ".ser_codigo";
    public static final String SER_CPF                          = TB_SERVIDOR + ".ser_cpf";
    public static final String SER_DATA_NASC                    = TB_SERVIDOR + ".ser_data_nasc";
    public static final String SER_NOME_MAE                     = TB_SERVIDOR + ".ser_nome_mae";
    public static final String SER_NOME_PAI                     = TB_SERVIDOR + ".ser_nome_pai";
    public static final String SER_NOME                         = TB_SERVIDOR + ".ser_nome";
    public static final String SER_PRIMEIRO_NOME                = TB_SERVIDOR + ".ser_primeiro_nome";
    public static final String SER_NOME_MEIO                    = TB_SERVIDOR + ".ser_nome_meio";
    public static final String SER_ULTIMO_NOME                  = TB_SERVIDOR + ".ser_ultimo_nome";
    public static final String SER_TITULACAO                    = TB_SERVIDOR + ".ser_titulacao";
    public static final String SER_SEXO                         = TB_SERVIDOR + ".ser_sexo";
    public static final String SER_EST_CIVIL                    = TB_SERVIDOR + ".ser_est_civil";
    public static final String SER_NACIONALIDADE                = TB_SERVIDOR + ".ser_nacionalidade";
    public static final String SER_NRO_IDT                      = TB_SERVIDOR + ".ser_nro_idt";
    public static final String SER_CART_PROF                    = TB_SERVIDOR + ".ser_cart_prof";
    public static final String SER_PIS                          = TB_SERVIDOR + ".ser_pis";
    public static final String SER_END                          = TB_SERVIDOR + ".ser_end";
    public static final String SER_BAIRRO                       = TB_SERVIDOR + ".ser_bairro";
    public static final String SER_CIDADE                       = TB_SERVIDOR + ".ser_cidade";
    public static final String SER_COMPL                        = TB_SERVIDOR + ".ser_compl";
    public static final String SER_NRO                          = TB_SERVIDOR + ".ser_nro";
    public static final String SER_CEP                          = TB_SERVIDOR + ".ser_cep";
    public static final String SER_UF                           = TB_SERVIDOR + ".ser_uf";
    public static final String SER_TEL                          = TB_SERVIDOR + ".ser_tel";
    public static final String SER_CELULAR                      = TB_SERVIDOR + ".ser_celular";
    public static final String SER_EMAIL                        = TB_SERVIDOR + ".ser_email";
    public static final String SER_EMISSOR_IDT                  = TB_SERVIDOR + ".ser_emissor_idt";
    public static final String SER_UF_IDT                       = TB_SERVIDOR + ".ser_uf_idt";
    public static final String SER_DATA_IDT                     = TB_SERVIDOR + ".ser_data_idt";
    public static final String SER_CID_NASC                     = TB_SERVIDOR + ".ser_cid_nasc";
    public static final String SER_UF_NASC                      = TB_SERVIDOR + ".ser_uf_nasc";
    public static final String SER_NOME_CONJUGE                 = TB_SERVIDOR + ".ser_nome_conjuge";
    public static final String SER_USU_CODIGO                   = TB_SERVIDOR + ".usu_codigo";
    public static final String SER_DATA_ALTERACAO               = TB_SERVIDOR + ".ser_data_alteracao";
    public static final String SER_DEFICIENTE_VISUAL            = TB_SERVIDOR + ".ser_deficiente_visual";
    public static final String SER_ACESSA_HOST_A_HOST           = TB_SERVIDOR + ".ser_acessa_host_a_host";
    public static final String SER_QTD_FILHOS                   = TB_SERVIDOR + ".ser_qtd_filhos";
    public static final String SER_THA_CODIGO                   = TB_SERVIDOR + ".tha_codigo";
    public static final String SER_NES_CODIGO                   = TB_SERVIDOR + ".nes_codigo";
    public static final String SER_SSE_CODIGO                   = TB_SERVIDOR + ".sse_codigo";
    public static final String SER_DISPENSA_DIGITAL             = TB_SERVIDOR + ".ser_dispensa_digital";
    public static final String SER_DATA_IDENTIFICACAO_PESSOAL   = TB_SERVIDOR + ".ser_data_identificacao_pessoal";
    public static final String SER_DATA_VALIDACAO_EMAIL         = TB_SERVIDOR + ".ser_data_validacao_email";
    public static final String SER_PERMITE_ALTERAR_EMAIL        = TB_SERVIDOR + ".ser_permite_alterar_email";

    public static final String TB_STATUS_ADE                    = "tb_status_autorizacao_desconto";
    public static final String SAD_CODIGO                       = TB_STATUS_ADE + ".sad_codigo";
    public static final String SAD_DESCRICAO                    = TB_STATUS_ADE + ".sad_descricao";
    public static final String SAD_SEQUENCIA                    = TB_STATUS_ADE + ".sad_sequencia";

    public static final String TB_STATUS_DESPESA_COMUM          = "tb_status_despesa_comum";
    public static final String SDC_CODIGO                       = TB_STATUS_DESPESA_COMUM + ".sdc_codigo";
    public static final String SDC_DESCRICAO                    = TB_STATUS_DESPESA_COMUM + ".sdc_descricao";

    public static final String TB_STATUS_PRD                    = "tb_status_parcela_desconto";
    public static final String SPD_CODIGO                       = TB_STATUS_PRD + ".spd_codigo";
    public static final String SPD_DESCRICAO                    = TB_STATUS_PRD + ".spd_descricao";

    public static final String TB_STATUS_CNV                    = "tb_status_convenio";
    public static final String SCV_CODIGO                       = TB_STATUS_CNV + ".scv_codigo";
    public static final String SCV_DESCRICAO                    = TB_STATUS_CNV + ".scv_descricao";

    public static final String TB_STATUS_LOGIN                  = "tb_status_login";
    public static final String STU_CODIGO                       = TB_STATUS_LOGIN + ".stu_codigo";
    public static final String STU_DESCRICAO                    = TB_STATUS_LOGIN + ".stu_descricao";

    public static final String TB_STATUS_REGISTRO_SERVIDOR      = "tb_status_registro_servidor";
    public static final String SRS_CODIGO                       = TB_STATUS_REGISTRO_SERVIDOR + ".srs_codigo";
    public static final String SRS_DESCRICAO                    = TB_STATUS_REGISTRO_SERVIDOR + ".srs_descricao";

    public static final String TB_SUB_ORGAO                     = "tb_sub_orgao";
    public static final String SBO_ORG_CODIGO                   = TB_SUB_ORGAO + ".org_codigo";
    public static final String SBO_CODIGO                       = TB_SUB_ORGAO + ".sbo_codigo";
    public static final String SBO_IDENTIFICADOR                = TB_SUB_ORGAO + ".sbo_identificador";
    public static final String SBO_DESCRICAO                    = TB_SUB_ORGAO + ".sbo_descricao";

    public static final String TB_TERMO_ADESAO                  = "tb_termo_adesao";
    public static final String TAD_CODIGO                       = TB_TERMO_ADESAO + ".tad_codigo";
    public static final String TAD_USU_CODIGO                   = TB_TERMO_ADESAO + ".usu_codigo";
    public static final String TAD_FUN_CODIGO                   = TB_TERMO_ADESAO + ".fun_codigo";
    public static final String TAD_TITULO                       = TB_TERMO_ADESAO + ".tad_titulo";
    public static final String TAD_TEXTO                        = TB_TERMO_ADESAO + ".tad_texto";
    public static final String TAD_DATA                         = TB_TERMO_ADESAO + ".tad_data";
    public static final String TAD_SEQUENCIA                    = TB_TERMO_ADESAO + ".tad_sequencia";
    public static final String TAD_EXIBE_CSE                    = TB_TERMO_ADESAO + ".tad_exibe_cse";
    public static final String TAD_EXIBE_ORG                    = TB_TERMO_ADESAO + ".tad_exibe_org";
    public static final String TAD_EXIBE_CSA                    = TB_TERMO_ADESAO + ".tad_exibe_csa";
    public static final String TAD_EXIBE_COR                    = TB_TERMO_ADESAO + ".tad_exibe_cor";
    public static final String TAD_EXIBE_SER                    = TB_TERMO_ADESAO + ".tad_exibe_ser";
    public static final String TAD_EXIBE_SUP                    = TB_TERMO_ADESAO + ".tad_exibe_sup";
    public static final String TAD_HTML                         = TB_TERMO_ADESAO + ".tad_html";
    public static final String TAD_PERMITE_RECUSAR              = TB_TERMO_ADESAO + ".tad_permite_recusar";
    public static final String TAD_PERMITE_LER_DEPOIS           = TB_TERMO_ADESAO + ".tad_permite_ler_depois";
    public static final String TAD_VERSAO_TERMO                 = TB_TERMO_ADESAO + ".tad_versao_termo";
    public static final String TAD_ENVIA_API_CONSENTIMENTO      = TB_TERMO_ADESAO + ".rad_envia_api_consentimento";
    public static final String TAD_CLASSE_ACAO				    = TB_TERMO_ADESAO + ".tad_classe_acao";
    public static final String TAD_EXIBE_APOS_LEITURA		    = TB_TERMO_ADESAO + ".tad_exibe_apos_leitura";

    public static final String TB_TERMO_ADESAO_SERVICO          = "tb_termo_adesao_servico";
    public static final String TAS_CSA_CODIGO                   = TB_TERMO_ADESAO_SERVICO + ".csa_codigo";
    public static final String TAS_SVC_CODIGO                   = TB_TERMO_ADESAO_SERVICO + ".svc_codigo";
    public static final String TAS_TEXTO                        = TB_TERMO_ADESAO_SERVICO + ".ter_ads_texto";

    public static final String TB_TIPO_DADO_ADICIONAL           = "tb_tipo_dado_adicional";
    public static final String TDA_CODIGO                       = TB_TIPO_DADO_ADICIONAL + ".tda_codigo";
    public static final String TDA_DESCRICAO                    = TB_TIPO_DADO_ADICIONAL + ".tda_descricao";
    public static final String TDA_EXPORTA                      = TB_TIPO_DADO_ADICIONAL + ".tda_exporta";
    public static final String TDA_SUP_CONSULTA                 = TB_TIPO_DADO_ADICIONAL + ".tda_sup_consulta";
    public static final String TDA_CSE_CONSULTA                 = TB_TIPO_DADO_ADICIONAL + ".tda_cse_consulta";
    public static final String TDA_CSA_CONSULTA                 = TB_TIPO_DADO_ADICIONAL + ".tda_csa_consulta";
    public static final String TDA_SER_CONSULTA                 = TB_TIPO_DADO_ADICIONAL + ".tda_ser_consulta";
    public static final String TDA_SUP_ALTERA                   = TB_TIPO_DADO_ADICIONAL + ".tda_sup_altera";
    public static final String TDA_CSE_ALTERA                   = TB_TIPO_DADO_ADICIONAL + ".tda_cse_altera";
    public static final String TDA_CSA_ALTERA                   = TB_TIPO_DADO_ADICIONAL + ".tda_csa_altera";
    public static final String TDA_SER_ALTERA                   = TB_TIPO_DADO_ADICIONAL + ".tda_ser_altera";
    public static final String TDA_DOMINIO                      = TB_TIPO_DADO_ADICIONAL + ".tda_dominio";
    public static final String TDA_TEN_CODIGO                   = TB_TIPO_DADO_ADICIONAL + ".ten_codigo";
    public static final String TDA_ORDENACAO                    = TB_TIPO_DADO_ADICIONAL + ".tda_ordenacao";

    public static final String TB_TIPO_GRUPO_CONSIGNATARIA      = "tb_tipo_grupo_consignataria";
    public static final String TGC_CODIGO                       = TB_TIPO_GRUPO_CONSIGNATARIA + ".tgc_codigo";
    public static final String TGC_IDENTIFICADOR                = TB_TIPO_GRUPO_CONSIGNATARIA + ".tgc_identificador";
    public static final String TGC_DESCRICAO                    = TB_TIPO_GRUPO_CONSIGNATARIA + ".tgc_descricao";

    public static final String TB_TIPO_DESCONTO                 = "tb_tipo_desconto";
    public static final String TDE_CODIGO                       = TB_TIPO_DESCONTO + ".tde_codigo";
    public static final String TDE_DESCRICAO                    = TB_TIPO_DESCONTO + ".tde_descricao";
    public static final String TDE_ACA_CODIGO                   = TB_TIPO_DESCONTO + ".aca_codigo";

    public static final String TB_TIPO_ENTIDADE                 = "tb_tipo_entidade";
    public static final String TEN_CODIGO                       = TB_TIPO_ENTIDADE + ".ten_codigo";
    public static final String TEN_DESCRICAO                    = TB_TIPO_ENTIDADE + ".ten_descricao";
    public static final String TEN_AUDITORIA                    = TB_TIPO_ENTIDADE + ".ten_auditoria";
    public static final String TEN_TITULO                       = TB_TIPO_ENTIDADE + ".ten_titulo";
    public static final String TEN_CAMPO_ENT_00                 = TB_TIPO_ENTIDADE + ".ten_campo_ent_00";
    public static final String TEN_CAMPO_ENT_01                 = TB_TIPO_ENTIDADE + ".ten_campo_ent_01";
    public static final String TEN_CAMPO_ENT_02                 = TB_TIPO_ENTIDADE + ".ten_campo_ent_02";
    public static final String TEN_CAMPO_ENT_03                 = TB_TIPO_ENTIDADE + ".ten_campo_ent_03";
    public static final String TEN_CAMPO_ENT_04                 = TB_TIPO_ENTIDADE + ".ten_campo_ent_04";
    public static final String TEN_CAMPO_ENT_05                 = TB_TIPO_ENTIDADE + ".ten_campo_ent_05";
    public static final String TEN_CAMPO_ENT_06                 = TB_TIPO_ENTIDADE + ".ten_campo_ent_06";
    public static final String TEN_CAMPO_ENT_07                 = TB_TIPO_ENTIDADE + ".ten_campo_ent_07";
    public static final String TEN_CAMPO_ENT_08                 = TB_TIPO_ENTIDADE + ".ten_campo_ent_08";
    public static final String TEN_CAMPO_ENT_09                 = TB_TIPO_ENTIDADE + ".ten_campo_ent_09";
    public static final String TEN_CAMPO_ENT_10                 = TB_TIPO_ENTIDADE + ".ten_campo_ent_10";

    public static final String TB_TIPO_LOG                      = "tb_tipo_log";
    public static final String TLO_CODIGO                       = TB_TIPO_LOG + ".tlo_codigo";
    public static final String TLO_DESCRICAO                    = TB_TIPO_LOG + ".tlo_descricao";

    public static final String TB_TIPO_OCORRENCIA               = "tb_tipo_ocorrencia";
    public static final String TOC_CODIGO                       = TB_TIPO_OCORRENCIA + ".toc_codigo";
    public static final String TOC_DESCRICAO                    = TB_TIPO_OCORRENCIA + ".toc_descricao";

    public static final String TB_TIPO_PARAM_SIST_CSE           = "tb_tipo_param_sist_consignante";
    public static final String TPC_CODIGO                       = TB_TIPO_PARAM_SIST_CSE + ".tpc_codigo";
    public static final String TPC_DESCRICAO                    = TB_TIPO_PARAM_SIST_CSE + ".tpc_descricao";
    public static final String TPC_DOMINIO                      = TB_TIPO_PARAM_SIST_CSE + ".tpc_dominio";
    public static final String TPC_CSE_ALTERA                   = TB_TIPO_PARAM_SIST_CSE + ".tpc_cse_altera";
    public static final String TPC_CSE_CONSULTA                 = TB_TIPO_PARAM_SIST_CSE + ".tpc_cse_consulta";
    public static final String TPC_SUP_ALTERA                   = TB_TIPO_PARAM_SIST_CSE + ".tpc_sup_altera";
    public static final String TPC_SUP_CONSULTA                 = TB_TIPO_PARAM_SIST_CSE + ".tpc_sup_consulta";
    public static final String TPC_VLR_DEFAULT                  = TB_TIPO_PARAM_SIST_CSE + ".tpc_vlr_default";
    public static final String TPC_GPS_CODIGO                   = TB_TIPO_PARAM_SIST_CSE + ".gps_codigo";

    public static final String TB_TIPO_PARAM_ORGAO              = "tb_tipo_param_orgao";
    public static final String TAO_CODIGO                       = TB_TIPO_PARAM_ORGAO + ".tao_codigo";
    public static final String TAO_DESCRICAO                    = TB_TIPO_PARAM_ORGAO + ".tao_descricao";
    public static final String TAO_DOMINIO                      = TB_TIPO_PARAM_ORGAO + ".tao_dominio";
    public static final String TAO_VLR_DEFAULT                  = TB_TIPO_PARAM_ORGAO + ".tao_vlr_default";
    public static final String TAO_SUP_ALTERA                   = TB_TIPO_PARAM_ORGAO + ".tao_sup_altera";
    public static final String TAO_SUP_CONSULTA                 = TB_TIPO_PARAM_ORGAO + ".tao_sup_consulta";
    public static final String TAO_CSE_ALTERA                   = TB_TIPO_PARAM_ORGAO + ".tao_cse_altera";
    public static final String TAO_CSE_CONSULTA                 = TB_TIPO_PARAM_ORGAO + ".tao_cse_consulta";
    public static final String TAO_ORG_ALTERA                   = TB_TIPO_PARAM_ORGAO + ".tao_org_altera";
    public static final String TAO_ORG_CONSULTA                 = TB_TIPO_PARAM_ORGAO + ".tao_org_consulta";

    public static final String TB_TIPO_PARAM_CSA                = "tb_tipo_param_consignataria";
    public static final String TPA_CODIGO                       = TB_TIPO_PARAM_CSA + ".tpa_codigo";
    public static final String TPA_DESCRICAO                    = TB_TIPO_PARAM_CSA + ".tpa_descricao";
    public static final String TPA_DOMINIO                      = TB_TIPO_PARAM_CSA + ".tpa_dominio";
    public static final String TPA_CSE_ALTERA                   = TB_TIPO_PARAM_CSA + ".tpa_cse_altera";
    public static final String TPA_CSA_ALTERA                   = TB_TIPO_PARAM_CSA + ".tpa_csa_altera";
    public static final String TPA_SUP_ALTERA                   = TB_TIPO_PARAM_CSA + ".tpa_sup_altera";

    public static final String TB_PARAM_CONSIGNATARIA           = "tb_param_consignataria";
    public static final String PCS_TPA_CODIGO                   = TB_PARAM_CONSIGNATARIA + ".tpa_codigo";
    public static final String PCS_CSA_CODIGO                   = TB_PARAM_CONSIGNATARIA + ".csa_codigo";
    public static final String PCS_VLR                          = TB_PARAM_CONSIGNATARIA + ".pcs_vlr";

    public static final String TB_TIPO_PARAM_SVC                = "tb_tipo_param_svc";
    public static final String TPS_CODIGO                       = TB_TIPO_PARAM_SVC + ".tps_codigo";
    public static final String TPS_DESCRICAO                    = TB_TIPO_PARAM_SVC + ".tps_descricao";
    public static final String TPS_CSE_ALTERA                   = TB_TIPO_PARAM_SVC + ".tps_cse_altera";
    public static final String TPS_CSA_ALTERA                   = TB_TIPO_PARAM_SVC + ".tps_csa_altera";
    public static final String TPS_SUP_ALTERA                   = TB_TIPO_PARAM_SVC + ".tps_sup_altera";
    public static final String TPS_PODE_SOBREPOR_RSE            = TB_TIPO_PARAM_SVC + ".tps_pode_sobrepor_rse";

    public static final String TB_TIPO_GRUPO_SVC                = "tb_tipo_grupo_svc";
    public static final String TGS_CODIGO                       = TB_TIPO_GRUPO_SVC + ".tgs_codigo";
    public static final String TGS_GRUPO                        = TB_TIPO_GRUPO_SVC + ".tgs_grupo";
    public static final String TGS_QUANTIDADE                   = TB_TIPO_GRUPO_SVC + ".tgs_quantidade";
    public static final String TGS_QUANTIDADE_POR_CSA           = TB_TIPO_GRUPO_SVC + ".tgs_quantidade_por_csa";
    public static final String TGS_IDENTIFICADOR                = TB_TIPO_GRUPO_SVC + ".tgs_identificador";

    public static final String TB_NATUREZA_SERVICO              = "tb_natureza_servico";
    public static final String NSE_CODIGO                       = TB_NATUREZA_SERVICO + ".nse_codigo";
    public static final String NSE_DESCRICAO                    = TB_NATUREZA_SERVICO + ".nse_descricao";
    public static final String NSE_TRANSFERIR_ADE               = TB_NATUREZA_SERVICO + ".nse_transferir_ade";
    public static final String NSE_ORDEM_BENEFICIO              = TB_NATUREZA_SERVICO + ".nse_ordem_beneficio";
    public static final String NSE_IMAGEM                       = TB_NATUREZA_SERVICO + ".nse_imagem";
    public static final String NSE_CODIGO_PAI                   = TB_NATUREZA_SERVICO + ".nse_codigo_pai";
    public static final String NSE_DESCRICAO_PORTAL             = TB_NATUREZA_SERVICO + ".nse_descricao_portal";
    public static final String NSE_TITULO_DETALHE_TOPO          = TB_NATUREZA_SERVICO + ".nse_titulo_detalhe_topo";
    public static final String NSE_TEXTO_DETALHE_TOPO           = TB_NATUREZA_SERVICO + ".nse_texto_detalhe_topo";
    public static final String NSE_TITULO_DETALHE_RODAPE        = TB_NATUREZA_SERVICO + ".nse_titulo_detalhe_rodape";
    public static final String NSE_TEXTO_DETALHE_RODAPE         = TB_NATUREZA_SERVICO + ".nse_texto_detalhe_rodape";
    public static final String NSE_TITULO_CAROUSEL_PROVEDOR     = TB_NATUREZA_SERVICO + ".nse_titulo_carousel_provedor";
    public static final String NSE_RETEM_VERBA                  = TB_NATUREZA_SERVICO + ".nse_retem_verba";

    public static final String TB_TIPO_MOTIVO_OPERACAO          = "tb_tipo_motivo_operacao";
    public static final String TMO_CODIGO                       = TB_TIPO_MOTIVO_OPERACAO + ".tmo_codigo";
    public static final String TMO_TEN_CODIGO                   = TB_TIPO_MOTIVO_OPERACAO + ".ten_codigo";
    public static final String TMO_ACA_CODIGO                   = TB_TIPO_MOTIVO_OPERACAO + ".aca_codigo";
    public static final String TMO_DESCRICAO                    = TB_TIPO_MOTIVO_OPERACAO + ".tmo_descricao";
    public static final String TMO_IDENTIFICADOR                = TB_TIPO_MOTIVO_OPERACAO + ".tmo_identificador";
    public static final String TMO_ATIVO                        = TB_TIPO_MOTIVO_OPERACAO + ".tmo_ativo";
    public static final String TMO_EXIGE_OBS                    = TB_TIPO_MOTIVO_OPERACAO + ".tmo_exige_obs";
    public static final String TMO_DECISAO_JUDICIAL             = TB_TIPO_MOTIVO_OPERACAO + ".tmo_decisao_judicial";

    public static final String TB_TIPO_PENALIDADE               = "tb_tipo_penalidade";
    public static final String TPE_CODIGO                       = TB_TIPO_PENALIDADE + ".tpe_codigo";
    public static final String TPE_DESCRICAO                    = TB_TIPO_PENALIDADE + ".tpe_descricao";
    public static final String TPE_PRAZO_PENALIDADE             = TB_TIPO_PENALIDADE + ".tpe_prazo_penalidade";

    public static final String TB_TIPO_MOTIVO_RECLAMACAO        = "tb_tipo_motivo_reclamacao";
    public static final String TMR_CODIGO                       = TB_TIPO_MOTIVO_RECLAMACAO + ".tmr_codigo";
    public static final String TMR_DESCRICAO                    = TB_TIPO_MOTIVO_RECLAMACAO + ".tmr_descricao";

    public static final String TB_UNIDADE                       = "tb_unidade";
    public static final String UNI_CODIGO                       = TB_UNIDADE + ".uni_codigo";
    public static final String UNI_SBO_CODIGO                   = TB_UNIDADE + ".sbo_codigo";
    public static final String UNI_IDENTIFICADOR                = TB_UNIDADE + ".uni_identificador";
    public static final String UNI_DESCRICAO                    = TB_UNIDADE + ".uni_descricao";

    public static final String TB_ACAO                          = "tb_acao";
    public static final String ACA_CODIGO                       = TB_ACAO + ".aca_codigo";
    public static final String ACA_DESCRICAO                    = TB_ACAO + ".aca_descricao";

    public static final String TB_TIPO_PARAM_TARIF_CSE          = "tb_tipo_param_tarif_cse";
    public static final String TPT_CODIGO                       = TB_TIPO_PARAM_TARIF_CSE + ".tpt_codigo";
    public static final String TPT_DESCRICAO                    = TB_TIPO_PARAM_TARIF_CSE + ".tpt_descricao";
    public static final String TPT_TIPO_INTERFACE               = TB_TIPO_PARAM_TARIF_CSE + ".tpt_tipo_interface";
    public static final String TPT_AJUDA                        = TB_TIPO_PARAM_TARIF_CSE + ".tpt_ajuda";

    public static final String TB_TIPO_NATUREZA                 = "tb_tipo_natureza";
    public static final String TNT_CODIGO                       = TB_TIPO_NATUREZA + ".tnt_codigo";
    public static final String TNT_DESCRICAO                    = TB_TIPO_NATUREZA + ".tnt_descricao";
    public static final String TNT_CSE_ALTERA                   = TB_TIPO_NATUREZA + ".tnt_cse_altera";
    public static final String TNT_SUP_ALTERA                   = TB_TIPO_NATUREZA + ".tnt_sup_altera";

    public static final String TB_TIPO_NOTIFICACAO              = "tb_tipo_notificacao";
    public static final String TNO_CODIGO                       = TB_TIPO_NOTIFICACAO + ".tno_codigo";
    public static final String TNO_DESCRICAO                    = TB_TIPO_NOTIFICACAO + ".tno_descricao";
    public static final String TNO_ENVIO                        = TB_TIPO_NOTIFICACAO + ".tno_envio";

    public static final String TB_USUARIO                       = "tb_usuario";
    public static final String USU_CODIGO                       = TB_USUARIO + ".usu_codigo";
    public static final String USU_STU_CODIGO                   = TB_USUARIO + ".stu_codigo";
    public static final String USU_DATA_CAD                     = TB_USUARIO + ".usu_data_cad";
    public static final String USU_LOGIN                        = TB_USUARIO + ".usu_login";
    public static final String USU_SENHA                        = TB_USUARIO + ".usu_senha";
    public static final String USU_NOME                         = TB_USUARIO + ".usu_nome";
    public static final String USU_EMAIL                        = TB_USUARIO + ".usu_email";
    public static final String USU_TEL                          = TB_USUARIO + ".usu_tel";
    public static final String USU_DICA_SENHA                   = TB_USUARIO + ".usu_dica_senha";
    public static final String USU_TIPO_BLOQ                    = TB_USUARIO + ".usu_tipo_bloq";
    public static final String USU_DATA_EXP_SENHA               = TB_USUARIO + ".usu_data_exp_senha";
    public static final String USU_IP_ACESSO                    = TB_USUARIO + ".usu_ip_acesso";
    public static final String USU_DDNS_ACESSO                  = TB_USUARIO + ".usu_ddns_acesso";
    public static final String USU_CPF                          = TB_USUARIO + ".usu_cpf";
    public static final String USU_CENTRALIZADOR                = TB_USUARIO + ".usu_centralizador";
    public static final String USU_VISIVEL                      = TB_USUARIO + ".usu_visivel";
    public static final String USU_EXIGE_CERTIFICADO            = TB_USUARIO + ".usu_exige_certificado";
    public static final String USU_SENHA_2                      = TB_USUARIO + ".usu_senha_2";
    public static final String USU_DATA_EXP_SENHA_2             = TB_USUARIO + ".usu_data_exp_senha_2";
    public static final String USU_OPERACOES_SENHA_2            = TB_USUARIO + ".usu_operacoes_senha_2";
    public static final String USU_DATA_ULT_ACESSO              = TB_USUARIO + ".usu_data_ult_acesso";
    public static final String USU_MATRICULA_INST               = TB_USUARIO + ".usu_matricula_inst";
    public static final String USU_CHAVE_RECUPERAR_SENHA        = TB_USUARIO + ".usu_chave_recuperar_senha";
    public static final String USU_NOVA_SENHA                   = TB_USUARIO + ".usu_nova_senha";
    public static final String USU_DATA_FIM_VIG                 = TB_USUARIO + ".usu_data_fim_vig";
    public static final String USU_DEFICIENTE_VISUAL            = TB_USUARIO + ".usu_deficiente_visual";
    public static final String USU_DATA_REC_SENHA               = TB_USUARIO + ".usu_data_rec_senha";
    public static final String USU_CHAVE_VALIDACAO_TOTP         = TB_USUARIO + ".usu_chave_validacao_totp";
    public static final String USU_PERMITE_VALIDACAO_TOTP       = TB_USUARIO + ".usu_permite_validacao_totp";
    public static final String USU_OPERACOES_VALIDACAO_TOTP     = TB_USUARIO + ".usu_operacoes_validacao_totp";
    public static final String USU_OTP_CODIGO                   = TB_USUARIO + ".usu_otp_codigo";
    public static final String USU_OTP_CHAVE_SEGURANCA          = TB_USUARIO + ".usu_otp_chave_seguranca";
    public static final String USU_OTP_DATA_CADASTRO            = TB_USUARIO + ".usu_otp_data_cadastro";
    public static final String USU_SENHA_APP                    = TB_USUARIO + ".usu_senha_app";
    public static final String USU_QTD_CONSULTAS_MARGEM         = TB_USUARIO + ".usu_qtd_consultas_margem";
    public static final String USU_AUTENTICA_SSO                = TB_USUARIO + ".usu_autentica_sso";
    public static final String USU_DATA_EXP_SENHA_APP           = TB_USUARIO + ".usu_data_exp_senha_app";
    public static final String USU_CHAVE_VALIDACAO_EMAIL        = TB_USUARIO + ".usu_chave_validacao_email";
    public static final String USU_DATA_VALIDACAO_EMAIL         = TB_USUARIO + ".usu_data_validacao_email";
    public static final String USU_AUTORIZA_EMAIL_MARKETING     = TB_USUARIO + ".usu_autoriza_email_marketing";

    public static final String TB_USUARIO_COR                   = "tb_usuario_cor";
    public static final String UCO_USU_CODIGO                   = TB_USUARIO_COR + ".usu_codigo";
    public static final String UCO_COR_CODIGO                   = TB_USUARIO_COR + ".cor_codigo";

    public static final String TB_USUARIO_CSA                   = "tb_usuario_csa";
    public static final String UCA_USU_CODIGO                   = TB_USUARIO_CSA + ".usu_codigo";
    public static final String UCA_CSA_CODIGO                   = TB_USUARIO_CSA + ".csa_codigo";

    public static final String TB_USUARIO_CSE                   = "tb_usuario_cse";
    public static final String UCE_USU_CODIGO                   = TB_USUARIO_CSE + ".usu_codigo";
    public static final String UCE_CSE_CODIGO                   = TB_USUARIO_CSE + ".cse_codigo";

    public static final String TB_USUARIO_ORG                   = "tb_usuario_org";
    public static final String UOR_USU_CODIGO                   = TB_USUARIO_ORG + ".usu_codigo";
    public static final String UOR_ORG_CODIGO                   = TB_USUARIO_ORG + ".org_codigo";

    public static final String TB_USUARIO_SER                   = "tb_usuario_ser";
    public static final String USE_USU_CODIGO                   = TB_USUARIO_SER + ".usu_codigo";
    public static final String USE_SER_CODIGO                   = TB_USUARIO_SER + ".ser_codigo";

    public static final String TB_USUARIO_SUP                   = "tb_usuario_sup";
    public static final String USP_CSE_CODIGO                   = TB_USUARIO_SUP + ".cse_codigo";
    public static final String USP_USU_CODIGO                   = TB_USUARIO_SUP + ".usu_codigo";

    public static final String TB_VERBA_CONVENIO                = "tb_verba_convenio";
    public static final String VCO_CODIGO                       = TB_VERBA_CONVENIO + ".vco_codigo";
    public static final String VCO_CNV_CODIGO                   = TB_VERBA_CONVENIO + ".cnv_codigo";
    public static final String VCO_DATA_INI                     = TB_VERBA_CONVENIO + ".vco_data_ini";
    public static final String VCO_DATA_FIM                     = TB_VERBA_CONVENIO + ".vco_data_fim";
    public static final String VCO_ATIVO                        = TB_VERBA_CONVENIO + ".vco_ativo";
    public static final String VCO_VLR_VERBA                    = TB_VERBA_CONVENIO + ".vco_vlr_verba";
    public static final String VCO_VLR_VERBA_REST               = TB_VERBA_CONVENIO + ".vco_vlr_verba_rest";

    public static final String TB_STATUS_VERBA_RESCISORIA       = "tb_status_verba_rescisoria";
    public static final String SVR_CODIGO                       = TB_STATUS_VERBA_RESCISORIA + ".svr_codigo";
    public static final String SVR_DESCRICAO                    = TB_STATUS_VERBA_RESCISORIA + ".svr_descricao";

    public static final String TB_VERBA_RESCISORIA_RSE          = "tb_verba_rescisoria_rse";
    public static final String VRR_CODIGO                       = TB_VERBA_RESCISORIA_RSE + ".vrr_codigo";
    public static final String VRR_RSE_CODIGO                   = TB_VERBA_RESCISORIA_RSE + ".rse_codigo";
    public static final String VRR_SVR_CODIGO                   = TB_VERBA_RESCISORIA_RSE + ".svr_codigo";
    public static final String VRR_DATA_INI                     = TB_VERBA_RESCISORIA_RSE + ".vrr_data_ini";
    public static final String VRR_DATA_FIM                     = TB_VERBA_RESCISORIA_RSE + ".vrr_data_fim";
    public static final String VRR_DATA_ULT_ATUALIZACAO         = TB_VERBA_RESCISORIA_RSE + ".vrr_data_ult_atualizacao";
    public static final String VRR_VALOR                        = TB_VERBA_RESCISORIA_RSE + ".vrr_valor";
    public static final String VRR_PROCESSADO                   = TB_VERBA_RESCISORIA_RSE + ".vrr_processado";

    public static final String TB_VENCIMENTO                    = "tb_vencimento";
    public static final String VCT_CODIGO                       = TB_VENCIMENTO + ".vct_codigo";
    public static final String VCT_IDENTIFICADOR                = TB_VENCIMENTO + ".vct_identificador";
    public static final String VCT_DESCRICAO                    = TB_VENCIMENTO + ".vct_descricao";

    public static final String TB_INDICE                        = "tb_indice";
    public static final String IND_CODIGO                       = TB_INDICE + ".ind_codigo";
    public static final String IND_DESCRICAO                    = TB_INDICE + ".ind_descricao";
    public static final String IND_SVC_CODIGO                   = TB_INDICE + ".svc_codigo";
    public static final String IND_CSA_CODIGO                   = TB_INDICE + ".csa_codigo";

    public static final String TB_BANCO                         = "tb_banco";
    public static final String BCO_CODIGO                       = TB_BANCO + ".bco_codigo";
    public static final String BCO_DESCRICAO                    = TB_BANCO + ".bco_descricao";
    public static final String BCO_IDENTIFICADOR                = TB_BANCO + ".bco_identificador";
    public static final String BCO_ATIVO                        = TB_BANCO + ".bco_ativo";
    public static final String BCO_FOLHA                        = TB_BANCO + ".bco_folha";

    public static final String TB_RELACIONAMENTO_SERVICO        = "tb_relacionamento_servico";
    public static final String RSV_CODIGO                       = TB_RELACIONAMENTO_SERVICO + ".rel_svc_codigo";
    public static final String RSV_TNT_CODIGO                   = TB_RELACIONAMENTO_SERVICO + ".tnt_codigo";
    public static final String RSV_SVC_CODIGO_ORIGEM            = TB_RELACIONAMENTO_SERVICO + ".svc_codigo_origem";
    public static final String RSV_SVC_CODIGO_DESTINO           = TB_RELACIONAMENTO_SERVICO + ".svc_codigo_destino";

    public static final String TB_RELACIONAMENTO_REGISTRO_SER   = "tb_relacionamento_registro_ser";
    public static final String RRE_USU_CODIGO                   = TB_RELACIONAMENTO_REGISTRO_SER + ".usu_codigo";
    public static final String RRE_TNT_CODIGO                   = TB_RELACIONAMENTO_REGISTRO_SER + ".tnt_codigo";
    public static final String RRE_SVC_CODIGO_ORIGEM            = TB_RELACIONAMENTO_REGISTRO_SER + ".rse_codigo_origem";
    public static final String RRE_SVC_CODIGO_DESTINO           = TB_RELACIONAMENTO_REGISTRO_SER + ".rse_codigo_destino";
    public static final String RRE_DATA                         = TB_RELACIONAMENTO_REGISTRO_SER + ".rre_data";

    public static final String TB_RELACIONAMENTO_AUTORIZACAO    = "tb_relacionamento_autorizacao";
    public static final String RAD_TNT_CODIGO                   = TB_RELACIONAMENTO_AUTORIZACAO + ".tnt_codigo";
    public static final String RAD_ADE_CODIGO_ORIGEM            = TB_RELACIONAMENTO_AUTORIZACAO + ".ade_codigo_origem";
    public static final String RAD_ADE_CODIGO_DESTINO           = TB_RELACIONAMENTO_AUTORIZACAO + ".ade_codigo_destino";
    public static final String RAD_USU_CODIGO                   = TB_RELACIONAMENTO_AUTORIZACAO + ".usu_codigo";
    public static final String RAD_STC_CODIGO                   = TB_RELACIONAMENTO_AUTORIZACAO + ".stc_codigo";
    public static final String RAD_CSA_CODIGO_ORIGEM            = TB_RELACIONAMENTO_AUTORIZACAO + ".csa_codigo_origem";
    public static final String RAD_CSA_CODIGO_DESTINO           = TB_RELACIONAMENTO_AUTORIZACAO + ".csa_codigo_destino";
    public static final String RAD_DATA                         = TB_RELACIONAMENTO_AUTORIZACAO + ".rad_data";
    public static final String RAD_DATA_REF_INF_SALDO           = TB_RELACIONAMENTO_AUTORIZACAO + ".rad_data_ref_inf_saldo";
    public static final String RAD_DATA_INF_SALDO               = TB_RELACIONAMENTO_AUTORIZACAO + ".rad_data_inf_saldo";
    public static final String RAD_DATA_REF_APR_SALDO           = TB_RELACIONAMENTO_AUTORIZACAO + ".rad_data_ref_apr_saldo";
    public static final String RAD_DATA_APR_SALDO               = TB_RELACIONAMENTO_AUTORIZACAO + ".rad_data_apr_saldo";
    public static final String RAD_DATA_REF_PGT_SALDO           = TB_RELACIONAMENTO_AUTORIZACAO + ".rad_data_ref_pgt_saldo";
    public static final String RAD_DATA_PGT_SALDO               = TB_RELACIONAMENTO_AUTORIZACAO + ".rad_data_pgt_saldo";
    public static final String RAD_DATA_REF_LIQUIDACAO          = TB_RELACIONAMENTO_AUTORIZACAO + ".rad_data_ref_liquidacao";
    public static final String RAD_DATA_LIQUIDACAO              = TB_RELACIONAMENTO_AUTORIZACAO + ".rad_data_liquidacao";

    public static final String TB_VINCULO_REGISTRO_SERVIDOR     = "tb_vinculo_registro_servidor";
    public static final String VRS_CODIGO                       = TB_VINCULO_REGISTRO_SERVIDOR + ".vrs_codigo";
    public static final String VRS_IDENTIFICADOR                = TB_VINCULO_REGISTRO_SERVIDOR + ".vrs_identificador";
    public static final String VRS_DESCRICAO                    = TB_VINCULO_REGISTRO_SERVIDOR + ".vrs_descricao";
    public static final String VRS_ATIVO                        = TB_VINCULO_REGISTRO_SERVIDOR + ".vrs_ativo";

    public static final String TB_CONVENIO_VINCULO_REGISTRO     = "tb_convenio_vinculo_registro";
    public static final String CVR_SVC_CODIGO                   = TB_CONVENIO_VINCULO_REGISTRO + ".svc_codigo";
    public static final String CVR_VRS_CODIGO                   = TB_CONVENIO_VINCULO_REGISTRO + ".vrs_codigo";
    public static final String CVR_CSA_CODIGO                   = TB_CONVENIO_VINCULO_REGISTRO + ".csa_codigo";

    public static final String TB_ARQUIVO_RETORNO				 = "tb_arquivo_retorno";
    public static final String ART_NOME_ARQUIVO                  = TB_ARQUIVO_RETORNO + ".nome_arquivo";
    public static final String ART_ID_LINHA                      = TB_ARQUIVO_RETORNO + ".id_linha";
    public static final String ART_CNV_COD_VERBA                 = TB_ARQUIVO_RETORNO + ".cnv_cod_verba";
    public static final String ART_EST_IDENTIFICADOR             = TB_ARQUIVO_RETORNO + ".est_identificador";
    public static final String ART_ORG_IDENTIFICADOR             = TB_ARQUIVO_RETORNO + ".org_identificador";
    public static final String ART_CSA_IDENTIFICADOR             = TB_ARQUIVO_RETORNO + ".csa_identificador";
    public static final String ART_SVC_IDENTIFICADOR             = TB_ARQUIVO_RETORNO + ".svc_identificador";
    public static final String ART_ANO_MES_DESCONTO              = TB_ARQUIVO_RETORNO + ".ano_mes_desconto";
    public static final String ART_PRD_VLR_REALIZADO             = TB_ARQUIVO_RETORNO + ".prd_vlr_realizado";
    public static final String ART_PRD_DATA_REALIZADO            = TB_ARQUIVO_RETORNO + ".prd_data_realizado";
    public static final String ART_ADE_DATA                      = TB_ARQUIVO_RETORNO + ".ade_data";
    public static final String ART_ADE_NUMERO                    = TB_ARQUIVO_RETORNO + ".ade_numero";
    public static final String ART_ADE_INDICE                    = TB_ARQUIVO_RETORNO + ".ade_indice";
    public static final String ART_ADE_COD_REG                   = TB_ARQUIVO_RETORNO + ".ade_cod_reg";
    public static final String ART_ADE_ANO_MES_INI               = TB_ARQUIVO_RETORNO + ".ade_ano_mes_ini";
    public static final String ART_ADE_ANO_MES_FIM               = TB_ARQUIVO_RETORNO + ".ade_ano_mes_fim";
    public static final String ART_ADE_PRD_PAGAS                 = TB_ARQUIVO_RETORNO + ".ade_prd_pagas";
    public static final String ART_ADE_PRAZO                     = TB_ARQUIVO_RETORNO + ".ade_prazo";
    public static final String ART_ADE_CARENCIA                  = TB_ARQUIVO_RETORNO + ".ade_carencia";
    public static final String ART_OCP_OBS                       = TB_ARQUIVO_RETORNO + ".ocp_obs";
    public static final String ART_SPD_CODIGO                    = TB_ARQUIVO_RETORNO + ".spd_codigo";
    public static final String ART_QUITACAO                      = TB_ARQUIVO_RETORNO + ".quitacao";
    public static final String ART_TIPO_ENVIO                    = TB_ARQUIVO_RETORNO + ".tipo_envio";
    public static final String ART_TDE_CODIGO                    = TB_ARQUIVO_RETORNO + ".tde_codigo";
    public static final String ART_RSE_MATRICULA                 = TB_ARQUIVO_RETORNO + ".rse_matricula";
    public static final String ART_SER_NOME                      = TB_ARQUIVO_RETORNO + ".ser_nome";
    public static final String ART_SER_CPF                       = TB_ARQUIVO_RETORNO + ".ser_cpf";
    public static final String ART_MAPEADA                       = TB_ARQUIVO_RETORNO + ".mapeada";
    public static final String ART_PROCESSADA                    = TB_ARQUIVO_RETORNO + ".processada";
    public static final String ART_PODE_PAGAR_CONSOLIDACAO_EXATA = TB_ARQUIVO_RETORNO + ".pode_pagar_consolidacao_exata";
    public static final String ART_LINHA                         = TB_ARQUIVO_RETORNO + ".linha";
    public static final String ART_FERIAS                        = TB_ARQUIVO_RETORNO + ".art_ferias";

    public static final String TB_ARQUIVO_RETORNO_PARCELA        = "tb_arquivo_retorno_parcela";
    public static final String ARP_NOME_ARQUIVO                  = TB_ARQUIVO_RETORNO_PARCELA + ".nome_arquivo";
    public static final String ARP_ID_LINHA                      = TB_ARQUIVO_RETORNO_PARCELA + ".id_linha";
    public static final String ARP_ADE_CODIGO                    = TB_ARQUIVO_RETORNO_PARCELA + ".ade_codigo";
    public static final String ARP_PRD_NUMERO                    = TB_ARQUIVO_RETORNO_PARCELA + ".prd_numero";
    public static final String ARP_PRD_DATA_DESCONTO             = TB_ARQUIVO_RETORNO_PARCELA + ".prd_data_desconto";

    public static final String TB_ACESSO_RECURSO                = "tb_acesso_recurso";
    public static final String ACR_CODIGO                       = TB_ACESSO_RECURSO + ".acr_codigo";
    public static final String ACR_PAP_CODIGO                   = TB_ACESSO_RECURSO + ".pap_codigo";
    public static final String ACR_FUN_CODIGO                   = TB_ACESSO_RECURSO + ".fun_codigo";
    public static final String ACR_RECURSO                      = TB_ACESSO_RECURSO + ".acr_recurso";
    public static final String ACR_PARAMETRO                    = TB_ACESSO_RECURSO + ".acr_parametro";
    public static final String ACR_SESSAO                       = TB_ACESSO_RECURSO + ".acr_sessao";
    public static final String ACR_OPERACAO                     = TB_ACESSO_RECURSO + ".acr_operacao";
    public static final String ACR_BLOQUEIO                     = TB_ACESSO_RECURSO + ".acr_bloqueio";
    public static final String ACR_ATIVO                        = TB_ACESSO_RECURSO + ".acr_ativo";
    public static final String ACR_FIM_FLUXO                    = TB_ACESSO_RECURSO + ".acr_fim_fluxo";
    public static final String ACR_ITM_CODIGO                   = TB_ACESSO_RECURSO + ".itm_codigo";
    public static final String ACR_METODO_HTTP                  = TB_ACESSO_RECURSO + ".acr_metodo_http";

    public static final String TB_FAQ                           = "tb_faq";
    public static final String FAQ_CODIGO                       = TB_FAQ + ".faq_codigo";
    public static final String FAQ_USU_CODIGO                   = TB_FAQ + ".usu_codigo";
    public static final String FAQ_TITULO_1                     = TB_FAQ + ".faq_titulo_1";
    public static final String FAQ_TITULO_2                     = TB_FAQ + ".faq_titulo_2";
    public static final String FAQ_TEXTO                        = TB_FAQ + ".faq_texto";
    public static final String FAQ_DATA                         = TB_FAQ + ".faq_data";
    public static final String FAQ_SEQUENCIA                    = TB_FAQ + ".faq_sequencia";
    public static final String FAQ_EXIBE_CSE                    = TB_FAQ + ".faq_exibe_cse";
    public static final String FAQ_EXIBE_ORG                    = TB_FAQ + ".faq_exibe_org";
    public static final String FAQ_EXIBE_CSA                    = TB_FAQ + ".faq_exibe_csa";
    public static final String FAQ_EXIBE_COR                    = TB_FAQ + ".faq_exibe_cor";
    public static final String FAQ_EXIBE_SER                    = TB_FAQ + ".faq_exibe_ser";
    public static final String FAQ_HTML                         = TB_FAQ + ".faq_html";
    public static final String FAQ_EXIBE_SUP                    = TB_FAQ + ".faq_exibe_sup";
    public static final String FAQ_EXIBE_MOBILE                 = TB_FAQ + ".faq_exibe_mobile";
    public static final String FAQ_CAF_CODIGO                   = TB_FAQ + ".caf_codigo";

    public static final String TB_AGENDAMENTO                   = "tb_agendamento";
    public static final String AGD_CODIGO                       = TB_AGENDAMENTO + ".agd_codigo";
    public static final String AGD_TAG_CODIGO                   = TB_AGENDAMENTO + ".tag_codigo";
    public static final String AGD_SAG_CODIGO                   = TB_AGENDAMENTO + ".sag_codigo";
    public static final String AGD_USU_CODIGO                   = TB_AGENDAMENTO + ".usu_codigo";
    public static final String AGD_REL_CODIGO                   = TB_AGENDAMENTO + ".rel_codigo";
    public static final String AGD_DESCRICAO                    = TB_AGENDAMENTO + ".agd_descricao";
    public static final String AGD_JAVA_CLASS_NAME              = TB_AGENDAMENTO + ".agd_java_class_name";
    public static final String AGD_DATA_CADASTRO                = TB_AGENDAMENTO + ".agd_data_cadastro";
    public static final String AGD_DATA_PREVISTA                = TB_AGENDAMENTO + ".agd_data_prevista";

    public static final String TB_OCORRENCIA_AGENDAMENTO        = "tb_ocorrencia_agendamento";
    public static final String OAG_CODIGO                       = TB_OCORRENCIA_AGENDAMENTO + ".oag_codigo";
    public static final String OAG_AGD_CODIGO                   = TB_OCORRENCIA_AGENDAMENTO + ".agd_codigo";
    public static final String OAG_USU_CODIGO                   = TB_OCORRENCIA_AGENDAMENTO + ".usu_codigo";
    public static final String OAG_TOC_CODIGO                   = TB_OCORRENCIA_AGENDAMENTO + ".toc_codigo";
    public static final String OAG_DATA_INICIO                  = TB_OCORRENCIA_AGENDAMENTO + ".oag_data_inicio";
    public static final String OAG_DATA_FIM                     = TB_OCORRENCIA_AGENDAMENTO + ".oag_data_fim";
    public static final String OAG_OBS                          = TB_OCORRENCIA_AGENDAMENTO + ".oag_obs";
    public static final String OAG_IP_ACESSO                    = TB_OCORRENCIA_AGENDAMENTO + ".oag_ip_acesso";

    public static final String TB_STATUS_AGENDAMENTO            = "tb_status_agendamento";
    public static final String SAG_CODIGO                       = TB_STATUS_AGENDAMENTO + ".sag_codigo";
    public static final String SAG_DESCRICAO                    = TB_STATUS_AGENDAMENTO + ".sag_descricao";

    public static final String TB_PARAMETRO_AGENDAMENTO         = "tb_parametro_agendamento";
    public static final String PAG_CODIGO                       = TB_PARAMETRO_AGENDAMENTO + ".pag_codigo";
    public static final String PAG_AGD_CODIGO                   = TB_PARAMETRO_AGENDAMENTO + ".agd_codigo";
    public static final String PAG_NOME                         = TB_PARAMETRO_AGENDAMENTO + ".pag_nome";
    public static final String PAG_VALOR                        = TB_PARAMETRO_AGENDAMENTO + ".pag_valor";

    public static final String TB_TIPO_AGENDAMENTO              = "tb_tipo_agendamento";
    public static final String TAG_CODIGO                       = TB_TIPO_AGENDAMENTO + ".tag_codigo";
    public static final String TAG_DESCRICAO                    = TB_TIPO_AGENDAMENTO + ".tag_descricao";

    public static final String TB_ANEXO_AUTORIZACAO_DESCONTO    = "tb_anexo_autorizacao_desconto";
    public static final String AAD_ADE_CODIGO                   = TB_ANEXO_AUTORIZACAO_DESCONTO + ".ade_codigo";
    public static final String AAD_NOME                         = TB_ANEXO_AUTORIZACAO_DESCONTO + ".aad_nome";
    public static final String AAD_USU_CODIGO                   = TB_ANEXO_AUTORIZACAO_DESCONTO + ".usu_codigo";
    public static final String AAD_TAR_CODIGO                   = TB_ANEXO_AUTORIZACAO_DESCONTO + ".tar_codigo";
    public static final String AAD_DESCRICAO                    = TB_ANEXO_AUTORIZACAO_DESCONTO + ".aad_descricao";
    public static final String AAD_ATIVO                        = TB_ANEXO_AUTORIZACAO_DESCONTO + ".aad_ativo";
    public static final String AAD_DATA                         = TB_ANEXO_AUTORIZACAO_DESCONTO + ".aad_data";
    public static final String AAD_IP_ACESSO                    = TB_ANEXO_AUTORIZACAO_DESCONTO + ".aad_ip_acesso";
    public static final String AAD_PERIODO                      = TB_ANEXO_AUTORIZACAO_DESCONTO + ".aad_periodo";

    public static final String TB_EMPRESA_CORRESPONDENTE        = "tb_empresa_correspondente";
    public static final String ECO_CODIGO                       = TB_EMPRESA_CORRESPONDENTE + ".eco_codigo";
    public static final String ECO_IDENTIFICADOR                = TB_EMPRESA_CORRESPONDENTE + ".eco_identificador";
    public static final String ECO_NOME                         = TB_EMPRESA_CORRESPONDENTE + ".eco_nome";
    public static final String ECO_ATIVO                        = TB_EMPRESA_CORRESPONDENTE + ".eco_ativo";
    public static final String ECO_CNPJ                         = TB_EMPRESA_CORRESPONDENTE + ".eco_cnpj";
    public static final String ECO_EMAIL                        = TB_EMPRESA_CORRESPONDENTE + ".eco_email";
    public static final String ECO_RESPONSAVEL                  = TB_EMPRESA_CORRESPONDENTE + ".eco_responsavel";
    public static final String ECO_RESP_CARGO                   = TB_EMPRESA_CORRESPONDENTE + ".eco_resp_cargo";
    public static final String ECO_RESP_TELEFONE                = TB_EMPRESA_CORRESPONDENTE + ".eco_resp_telefone";
    public static final String ECO_RESPONSAVEL_2                = TB_EMPRESA_CORRESPONDENTE + ".eco_responsavel_2";
    public static final String ECO_RESP_CARGO_2                 = TB_EMPRESA_CORRESPONDENTE + ".eco_resp_cargo_2";
    public static final String ECO_RESP_TELEFONE_2              = TB_EMPRESA_CORRESPONDENTE + ".eco_resp_telefone_2";
    public static final String ECO_RESPONSAVEL_3                = TB_EMPRESA_CORRESPONDENTE + ".eco_responsavel_3";
    public static final String ECO_RESP_CARGO_3                 = TB_EMPRESA_CORRESPONDENTE + ".eco_resp_cargo_3";
    public static final String ECO_RESP_TELEFONE_3              = TB_EMPRESA_CORRESPONDENTE + ".eco_resp_telefone_3";
    public static final String ECO_LOGRADOURO                   = TB_EMPRESA_CORRESPONDENTE + ".eco_logradouro";
    public static final String ECO_NRO                          = TB_EMPRESA_CORRESPONDENTE + ".eco_nro";
    public static final String ECO_COMPL                        = TB_EMPRESA_CORRESPONDENTE + ".eco_compl";
    public static final String ECO_BAIRRO                       = TB_EMPRESA_CORRESPONDENTE + ".eco_bairro";
    public static final String ECO_CIDADE                       = TB_EMPRESA_CORRESPONDENTE + ".eco_cidade";
    public static final String ECO_UF                           = TB_EMPRESA_CORRESPONDENTE + ".eco_uf";
    public static final String ECO_CEP                          = TB_EMPRESA_CORRESPONDENTE + ".eco_cep";
    public static final String ECO_TEL                          = TB_EMPRESA_CORRESPONDENTE + ".eco_tel";
    public static final String ECO_FAX                          = TB_EMPRESA_CORRESPONDENTE + ".eco_fax";

    public static final String TB_OCORRENCIA_REGISTRO_SERVIDOR  = "tb_ocorrencia_registro_ser";
    public static final String ORS_CODIGO                       = TB_OCORRENCIA_REGISTRO_SERVIDOR + ".ors_codigo";
    public static final String ORS_TOC_CODIGO                   = TB_OCORRENCIA_REGISTRO_SERVIDOR + ".toc_codigo";
    public static final String ORS_RSE_CODIGO                   = TB_OCORRENCIA_REGISTRO_SERVIDOR + ".rse_codigo";
    public static final String ORS_USU_CODIGO                   = TB_OCORRENCIA_REGISTRO_SERVIDOR + ".usu_codigo";
    public static final String ORS_DATA                         = TB_OCORRENCIA_REGISTRO_SERVIDOR + ".ors_data";
    public static final String ORS_OBS                          = TB_OCORRENCIA_REGISTRO_SERVIDOR + ".ors_obs";
    public static final String ORS_IP_ACESSO                    = TB_OCORRENCIA_REGISTRO_SERVIDOR + ".ors_ip_acesso";
    public static final String ORS_TMO_CODIGO                   = TB_OCORRENCIA_REGISTRO_SERVIDOR + ".tmo_codigo";

    public static final String TB_OCORRENCIA_SERVIDOR           = "tb_ocorrencia_servidor";
    public static final String OCS_CODIGO                       = TB_OCORRENCIA_SERVIDOR + ".ocs_codigo";
    public static final String OCS_TOC_CODIGO                   = TB_OCORRENCIA_SERVIDOR + ".toc_codigo";
    public static final String OCS_SER_CODIGO                   = TB_OCORRENCIA_SERVIDOR + ".ser_codigo";
    public static final String OCS_USU_CODIGO                   = TB_OCORRENCIA_SERVIDOR + ".usu_codigo";
    public static final String OCS_DATA                         = TB_OCORRENCIA_SERVIDOR + ".ocs_data";
    public static final String OCS_OBS                          = TB_OCORRENCIA_SERVIDOR + ".ocs_obs";
    public static final String OCS_IP_ACESSO                    = TB_OCORRENCIA_SERVIDOR + ".ocs_ip_acesso";

    public static final String TB_TRANSFERENCIA_MARGEM          = "tb_transferencia_margem";
    public static final String TRM_MAR_CODIGO_ORIGEM            = TB_TRANSFERENCIA_MARGEM + ".mar_codigo_origem";
    public static final String TRM_MAR_CODIGO_DESTINO           = TB_TRANSFERENCIA_MARGEM + ".mar_codigo_destino";
    public static final String TRM_PAP_CODIGO                   = TB_TRANSFERENCIA_MARGEM + ".pap_codigo";
    public static final String TRM_APENAS_TOTAL                 = TB_TRANSFERENCIA_MARGEM + ".trm_apenas_total";

    public static final String TB_AJUDA                         = "tb_ajuda";
    public static final String AJU_ACR_CODIGO                   = TB_AJUDA + ".acr_codigo";
    public static final String AJU_USU_CODIGO                   = TB_AJUDA + ".usu_codigo";
    public static final String AJU_TITULO                       = TB_AJUDA + ".aju_titulo";
    public static final String AJU_TEXTO                        = TB_AJUDA + ".aju_texto";
    public static final String AJU_ATIVO                        = TB_AJUDA + ".aju_ativo";
    public static final String AJU_DATA_ALTERACAO               = TB_AJUDA + ".aju_data_alteracao";
    public static final String AJU_SEQUENCIA                    = TB_AJUDA + ".aju_sequencia";
    public static final String AJU_HTML                         = TB_AJUDA + ".aju_html";

    public static final String TB_SENHA_ANTERIOR                = "tb_senha_anterior";
    public static final String SEA_USU_CODIGO                   = TB_SENHA_ANTERIOR + ".usu_codigo";
    public static final String SEA_SENHA                        = TB_SENHA_ANTERIOR + ".sea_senha";
    public static final String SEA_DATA                         = TB_SENHA_ANTERIOR + ".sea_data";

    public static final String TB_SENHA_AUTORIZACAO_SERVIDOR    = "tb_senha_autorizacao_servidor";
    public static final String SAS_USU_CODIGO                   = TB_SENHA_AUTORIZACAO_SERVIDOR + ".usu_codigo";
    public static final String SAS_DATA_CRIACAO                 = TB_SENHA_AUTORIZACAO_SERVIDOR + ".sas_data_criacao";
    public static final String SAS_DATA_EXPIRACAO               = TB_SENHA_AUTORIZACAO_SERVIDOR + ".sas_data_expiracao";
    public static final String SAS_SENHA                        = TB_SENHA_AUTORIZACAO_SERVIDOR + ".sas_senha";
    public static final String SAS_QTD_OPERACOES                = TB_SENHA_AUTORIZACAO_SERVIDOR + ".sas_qtd_operacoes";

    public static final String TB_TIPO_ARQUIVO                  = "tb_tipo_arquivo";
    public static final String TAR_CODIGO                       = TB_TIPO_ARQUIVO + ".tar_codigo";
    public static final String TAR_DESCRICAO                    = TB_TIPO_ARQUIVO + ".tar_descricao";
    public static final String TAR_QTD_DIAS_LIMPEZA             = TB_TIPO_ARQUIVO + ".tar_qtd_dias_limpeza";
    public static final String TAR_UPLOAD_SUP                   = TB_TIPO_ARQUIVO + ".tar_upload_sup";
    public static final String TAR_UPLOAD_CSE                   = TB_TIPO_ARQUIVO + ".tar_upload_cse";
    public static final String TAR_UPLOAD_ORG                   = TB_TIPO_ARQUIVO + ".tar_upload_org";
    public static final String TAR_UPLOAD_CSA                   = TB_TIPO_ARQUIVO + ".tar_upload_csa";
    public static final String TAR_UPLOAD_COR                   = TB_TIPO_ARQUIVO + ".tar_upload_cor";
    public static final String TAR_UPLOAD_SER                   = TB_TIPO_ARQUIVO + ".tar_upload_ser";
    public static final String TAR_NOTIFICACAO_UPLOAD           = TB_TIPO_ARQUIVO + ".tar_notificacao_upload";

    public static final String TB_HISTORICO_ARQUIVO             = "tb_historico_arquivo";
    public static final String HAR_CODIGO                       = TB_HISTORICO_ARQUIVO + ".har_codigo";
    public static final String HAR_USU_CODIGO                   = TB_HISTORICO_ARQUIVO + ".usu_codigo";
    public static final String HAR_TAR_CODIGO                   = TB_HISTORICO_ARQUIVO + ".tar_codigo";
    public static final String HAR_NOME_ARQUIVO                 = TB_HISTORICO_ARQUIVO + ".har_nome_arquivo";
    public static final String HAR_QTD_LINHAS                   = TB_HISTORICO_ARQUIVO + ".har_qtd_linhas";
    public static final String HAR_RESULTADO_PROC               = TB_HISTORICO_ARQUIVO + ".har_resultado_proc";
    public static final String HAR_DATA_PROC                    = TB_HISTORICO_ARQUIVO + ".har_data_proc";
    public static final String HAR_PERIODO                      = TB_HISTORICO_ARQUIVO + ".har_periodo";
    public static final String HAR_OBS                          = TB_HISTORICO_ARQUIVO + ".har_obs";
    public static final String HAR_FUN_CODIGO                   = TB_HISTORICO_ARQUIVO + ".fun_codigo";

    public static final String TB_HISTORICO_ARQUIVO_CSE         = "tb_historico_arquivo_cse";
    public static final String HCE_HAR_CODIGO                   = TB_HISTORICO_ARQUIVO_CSE + ".har_codigo";
    public static final String HCE_CSE_CODIGO                   = TB_HISTORICO_ARQUIVO_CSE + ".cse_codigo";

    public static final String TB_HISTORICO_ARQUIVO_ORG         = "tb_historico_arquivo_org";
    public static final String HOR_HAR_CODIGO                   = TB_HISTORICO_ARQUIVO_ORG + ".har_codigo";
    public static final String HOR_ORG_CODIGO                   = TB_HISTORICO_ARQUIVO_ORG + ".org_codigo";

    public static final String TB_HISTORICO_ARQUIVO_CSA         = "tb_historico_arquivo_csa";
    public static final String HCA_HAR_CODIGO                   = TB_HISTORICO_ARQUIVO_CSA + ".har_codigo";
    public static final String HCA_CSA_CODIGO                   = TB_HISTORICO_ARQUIVO_CSA + ".csa_codigo";

    public static final String TB_HISTORICO_ARQUIVO_COR         = "tb_historico_arquivo_cor";
    public static final String HCO_HAR_CODIGO                   = TB_HISTORICO_ARQUIVO_COR + ".har_codigo";
    public static final String HCO_COR_CODIGO                   = TB_HISTORICO_ARQUIVO_COR + ".cor_codigo";

    public static final String TB_HISTORICO_ARQUIVAMENTO_LOG    = "tb_historico_arquivamento_log";
    public static final String HAL_NOME_TABELA                  = TB_HISTORICO_ARQUIVAMENTO_LOG + ".hal_nome_tabela";
    public static final String HAL_DATA                         = TB_HISTORICO_ARQUIVAMENTO_LOG + ".hal_data";
    public static final String HAL_DATA_INI_LOG                 = TB_HISTORICO_ARQUIVAMENTO_LOG + ".hal_data_ini_log";
    public static final String HAL_DATA_FIM_LOG                 = TB_HISTORICO_ARQUIVAMENTO_LOG + ".hal_data_fim_log";
    public static final String HAL_QTD_REGISTROS                = TB_HISTORICO_ARQUIVAMENTO_LOG + ".hal_qtd_registros";

    public static final String TB_HISTORICO_OCORRENCIA_ADE      = "tb_historico_ocorrencia_ade";
    public static final String HOA_CODIGO                       = TB_HISTORICO_OCORRENCIA_ADE + ".hoa_codigo";
    public static final String HOA_OCA_CODIGO                   = TB_HISTORICO_OCORRENCIA_ADE + ".oca_codigo";
    public static final String HOA_USU_CODIGO                   = TB_HISTORICO_OCORRENCIA_ADE + ".usu_codigo";
    public static final String HOA_DATA                         = TB_HISTORICO_OCORRENCIA_ADE + ".hoa_data";
    public static final String HOA_IP_ACESSO                    = TB_HISTORICO_OCORRENCIA_ADE + ".hoa_ip_acesso";
    public static final String HOA_OBS                          = TB_HISTORICO_OCORRENCIA_ADE + ".hoa_obs";

    public static final String TB_REGRA_VALIDACAO_AMBIENTE      = "tb_regra_validacao_ambiente";
    public static final String REA_CODIGO                       = TB_REGRA_VALIDACAO_AMBIENTE + ".rea_codigo";
    public static final String REA_DESCRICAO                    = TB_REGRA_VALIDACAO_AMBIENTE + ".rea_descricao";
    public static final String REA_ATIVO                        = TB_REGRA_VALIDACAO_AMBIENTE + ".rea_ativo";
    public static final String REA_DATA_CADASTRO                = TB_REGRA_VALIDACAO_AMBIENTE + ".rea_data_cadastro";
    public static final String REA_JAVA_CLASS_NAME              = TB_REGRA_VALIDACAO_AMBIENTE + ".rea_java_class_name";
    public static final String REA_SEQUENCIA                    = TB_REGRA_VALIDACAO_AMBIENTE + ".rea_sequencia";
    public static final String REA_BLOQUEIA_SISTEMA             = TB_REGRA_VALIDACAO_AMBIENTE + ".rea_bloqueia_sistema";

    public static final String TB_MENU                          = "tb_menu";
    public static final String MNU_CODIGO                       = TB_MENU + ".mnu_codigo";
    public static final String MNU_DESCRICAO                    = TB_MENU + ".mnu_descricao";
    public static final String MNU_ATIVO                        = TB_MENU + ".mnu_ativo";
    public static final String MNU_SEQUENCIA                    = TB_MENU + ".mnu_sequencia";
    public static final String MNU_IMAGEM                       = TB_MENU + ".mnu_imagem";

    public static final String TB_ITEM_MENU                     = "tb_item_menu";
    public static final String ITM_CODIGO                       = TB_ITEM_MENU + ".itm_codigo";
    public static final String ITM_MNU_CODIGO                   = TB_ITEM_MENU + ".mnu_codigo";
    public static final String ITM_TEX_CHAVE                    = TB_ITEM_MENU + ".tex_chave";
    public static final String ITM_CODIGO_PAI                   = TB_ITEM_MENU + ".itm_codigo_pai";
    public static final String ITM_DESCRICAO                    = TB_ITEM_MENU + ".itm_descricao";
    public static final String ITM_ATIVO                        = TB_ITEM_MENU + ".itm_ativo";
    public static final String ITM_SEQUENCIA                    = TB_ITEM_MENU + ".itm_sequencia";
    public static final String ITM_SEPARADOR                    = TB_ITEM_MENU + ".itm_separador";
    public static final String ITM_CENTRALIZADOR                = TB_ITEM_MENU + ".itm_centralizador";
    public static final String ITM_IMAGEM                       = TB_ITEM_MENU + ".itm_imagem";

    public static final String TB_IMAGEM_SERVIDOR               = "tb_imagem_servidor";
    public static final String IMS_CPF                          = TB_IMAGEM_SERVIDOR + ".ims_cpf";
    public static final String IMS_NOME_ARQUIVO                 = TB_IMAGEM_SERVIDOR + ".ims_nome_arquivo";

    public static final String TB_ITEM_MENU_FAVORITO            = "tb_item_menu_favorito";
    public static final String IMF_USU_CODIGO                   = TB_ITEM_MENU_FAVORITO + ".usu_codigo";
    public static final String IMF_ITM_CODIGO                   = TB_ITEM_MENU_FAVORITO + ".itm_codigo";
    public static final String IMF_DATA                         = TB_ITEM_MENU_FAVORITO + ".imf_data";
    public static final String IMF_SEQUENCIA                    = TB_ITEM_MENU_FAVORITO + ".imf_sequencia";

    public static final String TB_RELATORIO                     = "tb_relatorio";
    public static final String REL_CODIGO                       = TB_RELATORIO + ".rel_codigo";
    public static final String REL_FUN_CODIGO                   = TB_RELATORIO + ".fun_codigo";
    public static final String REL_TAG_CODIGO                   = TB_RELATORIO + ".tag_codigo";
    public static final String REL_TITULO                       = TB_RELATORIO + ".rel_titulo";
    public static final String REL_ATIVO                        = TB_RELATORIO + ".rel_ativo";
    public static final String REL_AGENDADO                     = TB_RELATORIO + ".rel_agendado";
    public static final String REL_CLASSE_RELATORIO             = TB_RELATORIO + ".rel_classe_relatorio";
    public static final String REL_CLASSE_PROCESSO              = TB_RELATORIO + ".rel_classe_processo ";
    public static final String REL_CLASSE_AGENDAMENTO           = TB_RELATORIO + ".rel_classe_agendamento";
    public static final String REL_TEMPLATE_JASPER              = TB_RELATORIO + ".rel_template_jasper";
    public static final String REL_TEMPLATE_DINAMICO            = TB_RELATORIO + ".rel_template_dinamico";
    public static final String REL_TEMPLATE_SUBRELATORIO        = TB_RELATORIO + ".rel_template_subrelatorio";
    public static final String REL_TEMPLATE_SQL                 = TB_RELATORIO + ".rel_template_sql";
    public static final String REL_QTD_DIAS_LIMPEZA             = TB_RELATORIO + ".rel_qtd_dias_limpeza";
    public static final String REL_CUSTOMIZADO                  = TB_RELATORIO + ".rel_customizado";
    public static final String REL_AGRUPAMENTO                  = TB_RELATORIO + ".rel_agrupamento";

    public static final String TB_SUB_RELATORIO                  = "tb_sub_relatorio";
    public static final String SRE_CODIGO                       = TB_SUB_RELATORIO + ".sre_codigo";
    public static final String SRE_REL_CODIGO                   = TB_SUB_RELATORIO + ".rel_codigo";
    public static final String SRE_TEMPLATE_JASPER              = TB_SUB_RELATORIO + ".sre_template_jasper";
    public static final String SRE_NOME_PARAMETRO               = TB_SUB_RELATORIO + ".sre_nome_parametro";
    public static final String SRE_TEMPLATE_SQL                 = TB_SUB_RELATORIO + ".sre_template_sql";

    public static final String TB_TIPO_FILTRO_RELATORIO         = "tb_tipo_filtro_relatorio";
    public static final String TFR_CODIGO                       = TB_TIPO_FILTRO_RELATORIO + ".tfr_codigo";
    public static final String TFR_DESCRICAO                    = TB_TIPO_FILTRO_RELATORIO + ".tfr_descricao";
    public static final String TFR_RECURSO                      = TB_TIPO_FILTRO_RELATORIO + ".tfr_recurso";
    public static final String TFR_EXIBE_EDICAO                 = TB_TIPO_FILTRO_RELATORIO + ".tfr_exibe_edicao";

    public static final String TB_RELATORIO_FILTRO              = "tb_relatorio_filtro";
    public static final String RFI_REL_CODIGO                   = TB_RELATORIO_FILTRO + ".rel_codigo";
    public static final String RFI_TFR_CODIGO                   = TB_RELATORIO_FILTRO + ".tfr_codigo";
    public static final String RFI_EXIBE_CSE                    = TB_RELATORIO_FILTRO + ".rfi_exibe_cse";
    public static final String RFI_EXIBE_CSA                    = TB_RELATORIO_FILTRO + ".rfi_exibe_csa";
    public static final String RFI_EXIBE_COR                    = TB_RELATORIO_FILTRO + ".rfi_exibe_cor";
    public static final String RFI_EXIBE_ORG                    = TB_RELATORIO_FILTRO + ".rfi_exibe_org";
    public static final String RFI_EXIBE_SER                    = TB_RELATORIO_FILTRO + ".rfi_exibe_ser";
    public static final String RFI_EXIBE_SUP                    = TB_RELATORIO_FILTRO + ".rfi_exibe_sup";
    public static final String RFI_SEQUENCIA                    = TB_RELATORIO_FILTRO + ".rfi_sequencia";
    public static final String RFI_PARAMETRO                    = TB_RELATORIO_FILTRO + ".rfi_parametro";

    public static final String TB_COMUNICACAO                   = "tb_comunicacao";
    public static final String CMN_CODIGO                       = TB_COMUNICACAO + ".cmn_codigo";
    public static final String CMN_NUMERO                       = TB_COMUNICACAO + ".cmn_numero";
    public static final String CMN_CODIGO_PAI                   = TB_COMUNICACAO + ".cmn_codigo_pai";
    public static final String CMN_USU_CODIGO                   = TB_COMUNICACAO + ".usu_codigo";
    public static final String CMN_PENDENCIA                    = TB_COMUNICACAO + ".cmn_pendencia";
    public static final String CMN_DATA                         = TB_COMUNICACAO + ".cmn_data";
    public static final String CMN_TEXTO                        = TB_COMUNICACAO + ".cmn_texto";
    public static final String CMN_IP_ACESSO                    = TB_COMUNICACAO + ".cmn_ip_acesso";
    public static final String CMN_ALERTA_EMAIL                 = TB_COMUNICACAO + ".cmn_alerta_email";
    public static final String CMN_COPIA_EMAIL_SMS              = TB_COMUNICACAO + ".cmn_copia_email_sms";
    public static final String CMN_ASC_CODIGO                   = TB_COMUNICACAO + ".asc_codigo";
    public static final String CMN_ADE_CODIGO                   = TB_COMUNICACAO + ".ade_codigo";

    public static final String TB_ASSUNTO_COMUNICACAO           = "tb_assunto_comunicacao";
    public static final String ASC_CODIGO                       = TB_ASSUNTO_COMUNICACAO + ".asc_codigo";
    public static final String ASC_DESCRICAO                    = TB_ASSUNTO_COMUNICACAO + ".asc_descricao";
    public static final String ASC_ATIVO                        = TB_ASSUNTO_COMUNICACAO + ".asc_ativo";
    public static final String ASC_CONSIGNACAO                  = TB_ASSUNTO_COMUNICACAO + ".asc_consignacao";

    public static final String TB_COMUNICACAO_CSA               = "tb_comunicacao_csa";
    public static final String CMC_CMN_CODIGO                   = TB_COMUNICACAO_CSA + ".cmn_codigo";
    public static final String CMC_CSA_CODIGO                   = TB_COMUNICACAO_CSA + ".csa_codigo";
    public static final String CMC_DESTINATARIO                 = TB_COMUNICACAO_CSA + ".cmc_destinatario";

    public static final String TB_COMUNICACAO_CSE               = "tb_comunicacao_cse";
    public static final String CME_CMN_CODIGO                   = TB_COMUNICACAO_CSE + ".cmn_codigo";
    public static final String CME_CSE_CODIGO                   = TB_COMUNICACAO_CSE + ".cse_codigo";
    public static final String CME_DESTINATARIO                 = TB_COMUNICACAO_CSE + ".cme_destinatario";

    public static final String TB_COMUNICACAO_ORG               = "tb_comunicacao_org";
    public static final String CMO_CMN_CODIGO                   = TB_COMUNICACAO_ORG + ".cmn_codigo";
    public static final String CMO_ORG_CODIGO                   = TB_COMUNICACAO_ORG + ".org_codigo";
    public static final String CMO_DESTINATARIO                 = TB_COMUNICACAO_ORG + ".cmo_destinatario";

    public static final String TB_COMUNICACAO_SER               = "tb_comunicacao_ser";
    public static final String CMS_CMN_CODIGO                   = TB_COMUNICACAO_SER + ".cmn_codigo";
    public static final String CMS_SER_CODIGO                   = TB_COMUNICACAO_SER + ".ser_codigo";
    public static final String CMS_RSE_CODIGO                   = TB_COMUNICACAO_SER + ".rse_codigo";
    public static final String CMS_DESTINATARIO                 = TB_COMUNICACAO_SER + ".cms_destinatario";

    public static final String TB_COMUNICACAO_PERMITIDA         = "tb_comunicacao_permitida";
    public static final String CMN_PER_PAP_CODIGO_REMETENTE     = TB_COMUNICACAO_PERMITIDA + ".pap_codigo_remetente";
    public static final String CMN_PER_PAP_CODIGO_DESTINATARIO  = TB_COMUNICACAO_PERMITIDA + ".pap_codigo_destinatario";

    public static final String TB_STATUS_COMPRA                 = "tb_status_compra";
    public static final String STC_CODIGO                       = TB_STATUS_COMPRA + ".stc_codigo";
    public static final String STC_DESCRICAO                    = TB_STATUS_COMPRA + ".stc_descricao";

    public static final String TB_AUDITORIA_COR                 = "tb_auditoria_cor";
    public static final String ACO_CODIGO                       = TB_AUDITORIA_COR + ".aco_codigo";
    public static final String ACO_COR_CODIGO                   = TB_AUDITORIA_COR + ".cor_codigo";
    public static final String ACO_TLO_CODIGO                   = TB_AUDITORIA_COR + ".tlo_codigo";
    public static final String ACO_USU_CODIGO                   = TB_AUDITORIA_COR + ".usu_codigo";
    public static final String ACO_FUN_CODIGO                   = TB_AUDITORIA_COR + ".fun_codigo";
    public static final String ACO_TEN_CODIGO                   = TB_AUDITORIA_COR + ".ten_codigo";
    public static final String ACO_AUDITADO                     = TB_AUDITORIA_COR + ".aco_auditado";
    public static final String ACO_DATA                         = TB_AUDITORIA_COR + ".aco_data";
    public static final String ACO_IP                           = TB_AUDITORIA_COR + ".aco_ip";
    public static final String ACO_OBS                          = TB_AUDITORIA_COR + ".aco_obs";
    public static final String ACO_DATA_AUDITORIA               = TB_AUDITORIA_COR + ".aco_data_auditoria";
    public static final String ACO_USU_CODIGO_AUDITOR           = TB_AUDITORIA_COR + ".usu_codigo_auditor";

    public static final String TB_AUDITORIA_CSA                 = "tb_auditoria_csa";
    public static final String ACS_CODIGO                       = TB_AUDITORIA_CSA + ".acs_codigo";
    public static final String ACS_CSA_CODIGO                   = TB_AUDITORIA_CSA + ".csa_codigo";
    public static final String ACS_TLO_CODIGO                   = TB_AUDITORIA_CSA + ".tlo_codigo";
    public static final String ACS_USU_CODIGO                   = TB_AUDITORIA_CSA + ".usu_codigo";
    public static final String ACS_FUN_CODIGO                   = TB_AUDITORIA_CSA + ".fun_codigo";
    public static final String ACS_TEN_CODIGO                   = TB_AUDITORIA_CSA + ".ten_codigo";
    public static final String ACS_AUDITADO                     = TB_AUDITORIA_CSA + ".acs_auditado";
    public static final String ACS_DATA                         = TB_AUDITORIA_CSA + ".acs_data";
    public static final String ACS_IP                           = TB_AUDITORIA_CSA + ".acs_ip";
    public static final String ACS_OBS                          = TB_AUDITORIA_CSA + ".acs_obs";
    public static final String ACS_DATA_AUDITORIA               = TB_AUDITORIA_CSA + ".acs_data_auditoria";
    public static final String ACS_USU_CODIGO_AUDITOR           = TB_AUDITORIA_CSA + ".usu_codigo_auditor";

    public static final String TB_AUDITORIA_CSE                 = "tb_auditoria_cse";
    public static final String ACE_CODIGO                       = TB_AUDITORIA_CSE + ".ace_codigo";
    public static final String ACE_CSE_CODIGO                   = TB_AUDITORIA_CSE + ".cse_codigo";
    public static final String ACE_TLO_CODIGO                   = TB_AUDITORIA_CSE + ".tlo_codigo";
    public static final String ACE_USU_CODIGO                   = TB_AUDITORIA_CSE + ".usu_codigo";
    public static final String ACE_FUN_CODIGO                   = TB_AUDITORIA_CSE + ".fun_codigo";
    public static final String ACE_TEN_CODIGO                   = TB_AUDITORIA_CSE + ".ten_codigo";
    public static final String ACE_AUDITADO                     = TB_AUDITORIA_CSE + ".ace_auditado";
    public static final String ACE_DATA                         = TB_AUDITORIA_CSE + ".ace_data";
    public static final String ACE_IP                           = TB_AUDITORIA_CSE + ".ace_ip";
    public static final String ACE_OBS                          = TB_AUDITORIA_CSE + ".ace_obs";
    public static final String ACE_DATA_AUDITORIA               = TB_AUDITORIA_CSE + ".ace_data_auditoria";
    public static final String ACE_USU_CODIGO_AUDITOR           = TB_AUDITORIA_CSE + ".usu_codigo_auditor";

    public static final String TB_AUDITORIA_ORG                 = "tb_auditoria_org";
    public static final String AOR_CODIGO                       = TB_AUDITORIA_ORG + ".aor_codigo";
    public static final String AOR_ORG_CODIGO                   = TB_AUDITORIA_ORG + ".org_codigo";
    public static final String AOR_TLO_CODIGO                   = TB_AUDITORIA_ORG + ".tlo_codigo";
    public static final String AOR_USU_CODIGO                   = TB_AUDITORIA_ORG + ".usu_codigo";
    public static final String AOR_FUN_CODIGO                   = TB_AUDITORIA_ORG + ".fun_codigo";
    public static final String AOR_TEN_CODIGO                   = TB_AUDITORIA_ORG + ".ten_codigo";
    public static final String AOR_AUDITADO                     = TB_AUDITORIA_ORG + ".aor_auditado";
    public static final String AOR_DATA                         = TB_AUDITORIA_ORG + ".aor_data";
    public static final String AOR_IP                           = TB_AUDITORIA_ORG + ".aor_ip";
    public static final String AOR_OBS                          = TB_AUDITORIA_ORG + ".aor_obs";
    public static final String AOR_DATA_AUDITORIA               = TB_AUDITORIA_ORG + ".aor_data_auditoria";
    public static final String AOR_USU_CODIGO_AUDITOR           = TB_AUDITORIA_ORG + ".usu_codigo_auditor";

    public static final String TB_AUDITORIA_SUP                 = "tb_auditoria_sup";
    public static final String ASU_CODIGO                       = TB_AUDITORIA_SUP + ".asu_codigo";
    public static final String ASU_CSE_CODIGO                   = TB_AUDITORIA_SUP + ".cse_codigo";
    public static final String ASU_TLO_CODIGO                   = TB_AUDITORIA_SUP + ".tlo_codigo";
    public static final String ASU_USU_CODIGO                   = TB_AUDITORIA_SUP + ".usu_codigo";
    public static final String ASU_FUN_CODIGO                   = TB_AUDITORIA_SUP + ".fun_codigo";
    public static final String ASU_TEN_CODIGO                   = TB_AUDITORIA_SUP + ".ten_codigo";
    public static final String ASU_AUDITADO                     = TB_AUDITORIA_SUP + ".asu_auditado";
    public static final String ASU_DATA                         = TB_AUDITORIA_SUP + ".asu_data";
    public static final String ASU_IP                           = TB_AUDITORIA_SUP + ".asu_ip";
    public static final String ASU_OBS                          = TB_AUDITORIA_SUP + ".asu_obs";
    public static final String ASU_DATA_AUDITORIA               = TB_AUDITORIA_SUP + ".asu_data_auditoria";
    public static final String ASU_USU_CODIGO_AUDITOR           = TB_AUDITORIA_SUP + ".usu_codigo_auditor";

    public static final String TB_REGRA_RESTRICAO_ACESSO        = "tb_regra_restricao_acesso";
    public static final String RRA_CODIGO                       = TB_REGRA_RESTRICAO_ACESSO + ".rra_codigo";
    public static final String RRA_DESCRICAO                    = TB_REGRA_RESTRICAO_ACESSO + ".rra_descricao";
    public static final String RRA_DIA_SEMANA                   = TB_REGRA_RESTRICAO_ACESSO + ".rra_dia_semana";
    public static final String RRA_DATA                         = TB_REGRA_RESTRICAO_ACESSO + ".rra_data";
    public static final String RRA_DIAS_UTEIS                   = TB_REGRA_RESTRICAO_ACESSO + ".rra_dias_uteis";
    public static final String RRA_HORA_INICIO                  = TB_REGRA_RESTRICAO_ACESSO + ".rra_hora_inicio";
    public static final String RRA_HORA_FIM                     = TB_REGRA_RESTRICAO_ACESSO + ".rra_hora_fim";
    public static final String RRA_FUN_CODIGO                   = TB_REGRA_RESTRICAO_ACESSO + ".fun_codigo";
    public static final String RRA_PAP_CODIGO                   = TB_REGRA_RESTRICAO_ACESSO + ".pap_codigo";

    public static final String TB_REGRA_RESTRICAO_ACESSO_CSA    = "tb_regra_restricao_acesso_csa";
    public static final String RCA_RRA_CODIGO                   = TB_REGRA_RESTRICAO_ACESSO_CSA + ".rra_codigo";
    public static final String RCA_CSA_CODIGO                   = TB_REGRA_RESTRICAO_ACESSO_CSA + ".csa_codigo";

    public static final String TB_NATUREZA_CONSIGNATARIA        = "tb_natureza_consignataria";
    public static final String NCA_CODIGO                       = TB_NATUREZA_CONSIGNATARIA + ".nca_codigo";
    public static final String NCA_DESCRICAO                    = TB_NATUREZA_CONSIGNATARIA + ".nca_descricao";
    public static final String NCA_EXIBE_SER                   = TB_NATUREZA_CONSIGNATARIA + ".nca_exibe_ser";

    public static final String TB_PLANO                         = "tb_plano";
    public static final String PLA_CODIGO                       = TB_PLANO + ".pla_codigo";
    public static final String PLA_SVC_CODIGO                   = TB_PLANO + ".svc_codigo";
    public static final String PLA_CSA_CODIGO                   = TB_PLANO + ".csa_codigo";
    public static final String PLA_NPL_CODIGO                   = TB_PLANO + ".npl_codigo";
    public static final String PLA_IDENTIFICADOR                = TB_PLANO + ".pla_identificador";
    public static final String PLA_DESCRICAO                    = TB_PLANO + ".pla_descricao";
    public static final String PLA_ATIVO                        = TB_PLANO + ".pla_ativo";

    public static final String TB_NATUREAZA_PLANO               = "tb_natureza_plano";
    public static final String NPL_CODIGO                       = TB_NATUREAZA_PLANO + ".npl_codigo";
    public static final String NPL_DESCRICAO                    = TB_NATUREAZA_PLANO + ".npl_descricao";

    public static final String TB_ENDERECO_CONJUNTO_HABITACIONAL= "tb_endereco_conj_habitacional";
    public static final String ECH_CODIGO						= TB_ENDERECO_CONJUNTO_HABITACIONAL + ".ech_codigo";
    public static final String ECH_CSA_CODIGO					= TB_ENDERECO_CONJUNTO_HABITACIONAL + ".csa_codigo";
    public static final String ECH_IDENTIFICADOR				= TB_ENDERECO_CONJUNTO_HABITACIONAL + ".ech_identificador";
    public static final String ECH_DESCRICAO					= TB_ENDERECO_CONJUNTO_HABITACIONAL + ".ech_descricao";
    public static final String ECH_CONDOMINIO					= TB_ENDERECO_CONJUNTO_HABITACIONAL + ".ech_condominio";
    public static final String ECH_QTD_UNIDADES					= TB_ENDERECO_CONJUNTO_HABITACIONAL + ".ech_qtd_unidades";

    public static final String TB_PARAMETRO_PLANO               = "tb_parametro_plano";
    public static final String PPL_TPP_CODIGO                   = TB_PARAMETRO_PLANO + ".tpp_codigo";
    public static final String PPL_PLA_CODIGO                   = TB_PARAMETRO_PLANO + ".pla_codigo";
    public static final String PPL_VALOR                        = TB_PARAMETRO_PLANO + ".ppl_valor";
    public static final String PPL_DATA                         = TB_PARAMETRO_PLANO + ".ppl_data";

    public static final String TB_TIPO_PARAMETRO_PLANO          = "tb_tipo_parametro_plano";
    public static final String TPP_DOMINIO                      = TB_TIPO_PARAMETRO_PLANO + ".tpp_dominio";
    public static final String TPP_DESCRICAO                    = TB_TIPO_PARAMETRO_PLANO + ".tpp_descricao";
    public static final String TPP_CODIGO                       = TB_TIPO_PARAMETRO_PLANO + ".tpp_codigo";
    public static final String TPP_VLR_DEFAULT                  = TB_TIPO_PARAMETRO_PLANO + ".tpp_vlr_default";
    public static final String TPP_CSA_ALTERA                   = TB_TIPO_PARAMETRO_PLANO + ".tpp_csa_altera";

    public static final String TB_OCORRENCIA_DESPESA_COMUM      = "tb_ocorrencia_despesa_comum";
    public static final String ODC_CODIGO                       = TB_OCORRENCIA_DESPESA_COMUM + ".odc_codigo";
    public static final String ODC_DEC_CODIGO                   = TB_OCORRENCIA_DESPESA_COMUM + ".dec_codigo";
    public static final String ODC_TOC_CODIGO                   = TB_OCORRENCIA_DESPESA_COMUM + ".toc_codigo";
    public static final String ODC_USU_CODIGO                   = TB_OCORRENCIA_DESPESA_COMUM + ".usu_codigo";
    public static final String ODC_DATA                         = TB_OCORRENCIA_DESPESA_COMUM + ".odc_data";
    public static final String ODC_IP_ACESSO                    = TB_OCORRENCIA_DESPESA_COMUM + ".odc_ip_acesso";
    public static final String ODC_OBS                          = TB_OCORRENCIA_DESPESA_COMUM + ".odc_obs";

    public static final String TB_OCORRENCIA_DESP_INDIVIDUAL    = "tb_ocorrencia_desp_individual";
    public static final String ODI_CODIGO                       = TB_OCORRENCIA_DESP_INDIVIDUAL + ".odi_codigo";
    public static final String ODI_ADE_CODIGO                   = TB_OCORRENCIA_DESP_INDIVIDUAL + ".ade_codigo";
    public static final String ODI_TOC_CODIGO                   = TB_OCORRENCIA_DESP_INDIVIDUAL + ".toc_codigo";
    public static final String ODI_USU_CODIGO                   = TB_OCORRENCIA_DESP_INDIVIDUAL + ".usu_codigo";
    public static final String ODI_DATA                         = TB_OCORRENCIA_DESP_INDIVIDUAL + ".odi_data";
    public static final String ODI_IP_ACESSO                    = TB_OCORRENCIA_DESP_INDIVIDUAL + ".odi_ip_acesso";
    public static final String ODI_OBS                          = TB_OCORRENCIA_DESP_INDIVIDUAL + ".odi_obs";

    public static final String TB_OCORRENCIA_PERMISSIONARIO     = "tb_ocorrencia_permissionario";
    public static final String OPE_CODIGO                       = TB_OCORRENCIA_PERMISSIONARIO + ".ope_codigo";
    public static final String OPE_PRM_CODIGO					= TB_OCORRENCIA_PERMISSIONARIO + ".prm_codigo";
    public static final String OPE_TOC_CODIGO					= TB_OCORRENCIA_PERMISSIONARIO + ".toc_codigo";
    public static final String OPE_USU_CODIGO					= TB_OCORRENCIA_PERMISSIONARIO + ".usu_codigo";
    public static final String OPE_DATA							= TB_OCORRENCIA_PERMISSIONARIO + ".ope_data";
    public static final String OPE_IP_ACESSO					= TB_OCORRENCIA_PERMISSIONARIO + ".ope_ip_acesso";
    public static final String OPE_OBS							= TB_OCORRENCIA_PERMISSIONARIO + ".ope_obs";

    public static final String TB_ANEXO_COMUNICACAO             = "tb_anexo_comunicacao";
    public static final String ACM_CMN_CODIGO                   = TB_ANEXO_COMUNICACAO + ".cmn_codigo";
    public static final String ACM_NOME                         = TB_ANEXO_COMUNICACAO + ".acm_nome";
    public static final String ACM_USU_CODIGO                   = TB_ANEXO_COMUNICACAO + ".usu_codigo";
    public static final String ACM_TAR_CODIGO                   = TB_ANEXO_COMUNICACAO + ".tar_codigo";
    public static final String ACM_ATIVO                        = TB_ANEXO_COMUNICACAO + ".acm_ativo";
    public static final String ACM_DATA                         = TB_ANEXO_COMUNICACAO + ".acm_data";
    public static final String ACM_DESCRICAO                    = TB_ANEXO_COMUNICACAO + ".acm_descricao";

    public static final String TB_SOLICITACAO_SUPORTE           = "tb_solicitacao_suporte";
    public static final String SOS_CODIGO                       = TB_SOLICITACAO_SUPORTE + ".sos_codigo";
    public static final String SOS_CHAVE                        = TB_SOLICITACAO_SUPORTE + ".sos_chave";
    public static final String SOS_SUMARIO                      = TB_SOLICITACAO_SUPORTE + ".sos_sumario";
    public static final String SOS_DATA_CADASTRO                = TB_SOLICITACAO_SUPORTE + ".sos_data_cadastro";
    public static final String SOS_SLA_INDICATOR                = TB_SOLICITACAO_SUPORTE + ".sos_sla";
    public static final String SOS_PRIORIDADE                   = TB_SOLICITACAO_SUPORTE + ".sos_prioridade";
    public static final String SOS_USU_CODIGO                   = TB_SOLICITACAO_SUPORTE + ".usu_codigo";
    public static final String SOS_CLIENTE_TRANSIENTE           = TB_SOLICITACAO_SUPORTE + ".sos_cliente";
    public static final String SOS_SISTEMA_TRANSIENTE           = TB_SOLICITACAO_SUPORTE + ".sos_sistema";
    public static final String SOS_PAPEL_TRANSIENTE             = TB_SOLICITACAO_SUPORTE + ".sos_papel";
    public static final String SOS_MOTIVO_TRANSIENTE            = TB_SOLICITACAO_SUPORTE + ".sos_motivo";
    public static final String SOS_SERVICO_TRANSIENTE           = TB_SOLICITACAO_SUPORTE + ".sos_servico";
    public static final String SOS_COMENTARIO_TRANSIENTE        = TB_SOLICITACAO_SUPORTE + ".sos_comentario";
    public static final String SOS_ATENDIMENTO_TRANSIENTE       = TB_SOLICITACAO_SUPORTE + ".sos_atendimento";
    public static final String SOS_TIPO_ID_TRANSIENTE           = TB_SOLICITACAO_SUPORTE + ".sos_tipo_id";
    public static final String SOS_SLA_INDICATOR_ID_TRANSIENTE  = TB_SOLICITACAO_SUPORTE + ".sos_sla_id";
    public static final String SOS_PRIORIDADE_ID_TRANSIENTE     = TB_SOLICITACAO_SUPORTE + ".sos_prioridade_id";
    public static final String SOS_PROJETO_ID_TRANSIENTE        = TB_SOLICITACAO_SUPORTE + ".sos_projeto_id";
    public static final String SOS_DESCRICAO_TRANSIENTE         = TB_SOLICITACAO_SUPORTE + ".sos_descricao";
    public static final String SOS_EMAIL_TRANSIENTE             = TB_SOLICITACAO_SUPORTE + ".sos_email";
    public static final String SOS_DESCSA_TRANSIENTE            = TB_SOLICITACAO_SUPORTE + ".sos_descsa";
    public static final String SOS_LOGIN_ECONSIG_TRANSIENTE     = TB_SOLICITACAO_SUPORTE + ".sos_login_econsig";
    public static final String SOS_STATUS_TRANSIENTE            = TB_SOLICITACAO_SUPORTE + ".sos_status";
    public static final String SOS_DATA_ATUALIZACAO_TRANSIENTE  = TB_SOLICITACAO_SUPORTE + ".sos_data_atualizacao";
    public static final String SOS_DATA_RESOLUCAO_TRANSIENTE    = TB_SOLICITACAO_SUPORTE + ".sos_data_resolucao";
    public static final String SOS_RESPONSAVEL_TRANSIENTE       = TB_SOLICITACAO_SUPORTE + ".sos_responsavel";
    public static final String SOS_SOLUCAO_TRANSIENTE           = TB_SOLICITACAO_SUPORTE + ".sos_solucao";
    public static final String SOS_TELEFONE_TRANSIENTE          = TB_SOLICITACAO_SUPORTE + ".sos_telefone";
    public static final String SOS_MATRICULA                    = TB_SOLICITACAO_SUPORTE + ".sos_matricula";
    public static final String SOS_ARQUIVO                      = TB_SOLICITACAO_SUPORTE + ".sos_arquivo";
    public static final String SOS_USUARIO_SUPORTE              = TB_SOLICITACAO_SUPORTE + ".sos_usuario_suporte";
    public static final String SOS_TOTEM                        = TB_SOLICITACAO_SUPORTE + ".sos_totem";
    public static final String SOS_EMAIL_USUARIO_SUPORTE        = TB_SOLICITACAO_SUPORTE + ".sos_email_usuario_suporte";

    public static final String TB_TIPO_PARAM_VALIDACAO_ARQ      = "tb_tipo_param_validacao_arq";
    public static final String TVA_CODIGO                       = TB_TIPO_PARAM_VALIDACAO_ARQ + ".tva_codigo";
    public static final String TVA_CHAVE                        = TB_TIPO_PARAM_VALIDACAO_ARQ + ".tva_chave";
    public static final String TVA_DESCRICAO                    = TB_TIPO_PARAM_VALIDACAO_ARQ + ".tva_descricao";

    public static final String TB_PARAM_VALIDACAO_ARQ_CSE       = "tb_param_validacao_arq_cse";
    public static final String VAC_TVA_CODIGO                   = TB_PARAM_VALIDACAO_ARQ_CSE + ".tva_codigo";
    public static final String VAC_CSE_CODIGO                   = TB_PARAM_VALIDACAO_ARQ_CSE + ".cse_codigo";
    public static final String VAC_VALOR                        = TB_PARAM_VALIDACAO_ARQ_CSE + ".vac_valor";

    public static final String TB_PARAM_VALIDACAO_ARQ_ORG       = "tb_param_validacao_arq_org";
    public static final String VAO_TVA_CODIGO                   = TB_PARAM_VALIDACAO_ARQ_ORG + ".tva_codigo";
    public static final String VAO_ORG_CODIGO                   = TB_PARAM_VALIDACAO_ARQ_ORG + ".org_codigo";
    public static final String VAO_VALOR                        = TB_PARAM_VALIDACAO_ARQ_ORG + ".vao_valor";

    public static final String TB_HISTORICO_MEDIA_MARGEM        = "tb_historico_media_margem";
    public static final String HMM_HPM_CODIGO                   = TB_HISTORICO_MEDIA_MARGEM + ".hpm_codigo";
    public static final String HMM_MAR_CODIGO                   = TB_HISTORICO_MEDIA_MARGEM + ".mar_codigo";
    public static final String HMM_MEDIA_MARGEM_ANTES           = TB_HISTORICO_MEDIA_MARGEM + ".hmm_media_margem_antes";
    public static final String HMM_MEDIA_MARGEM_DEPOIS          = TB_HISTORICO_MEDIA_MARGEM + ".hmm_media_margem_depois";

    public static final String TB_HISTORICO_PROC_MARGEM         = "tb_historico_proc_margem";
    public static final String HPM_CODIGO                       = TB_HISTORICO_PROC_MARGEM + ".hpm_codigo";
    public static final String HPM_USU_CODIGO                   = TB_HISTORICO_PROC_MARGEM + ".usu_codigo";
    public static final String HPM_PERIODO                      = TB_HISTORICO_PROC_MARGEM + ".hpm_periodo";
    public static final String HPM_DATA_PROC                    = TB_HISTORICO_PROC_MARGEM + ".hpm_data_proc";
    public static final String HPM_QTD_SERVIDORES_ANTES         = TB_HISTORICO_PROC_MARGEM + ".hpm_qtd_servidores_antes";
    public static final String HPM_QTD_SERVIDORES_DEPOIS        = TB_HISTORICO_PROC_MARGEM + ".hpm_qtd_servidores_depois";

    public static final String TB_HISTORICO_PROC_MARGEM_CSE     = "tb_historico_proc_margem_cse";
    public static final String HPC_HPM_CODIGO                   = TB_HISTORICO_PROC_MARGEM_CSE + ".hpm_codigo";
    public static final String HPC_CSE_CODIGO                   = TB_HISTORICO_PROC_MARGEM_CSE + ".cse_codigo";

    public static final String TB_HISTORICO_PROC_MARGEM_EST     = "tb_historico_proc_margem_est";
    public static final String HPE_HPM_CODIGO                   = TB_HISTORICO_PROC_MARGEM_EST + ".hpm_codigo";
    public static final String HPE_EST_CODIGO                   = TB_HISTORICO_PROC_MARGEM_EST + ".est_codigo";

    public static final String TB_HISTORICO_PROC_MARGEM_ORG     = "tb_historico_proc_margem_org";
    public static final String HPO_HPM_CODIGO                   = TB_HISTORICO_PROC_MARGEM_ORG + ".hpm_codigo";
    public static final String HPO_ORG_CODIGO                   = TB_HISTORICO_PROC_MARGEM_ORG + ".org_codigo";

    public static final String TB_LIMITE_TAXA_JUROS             = "tb_limite_taxa_juros";
    public static final String LTJ_CODIGO                       = TB_LIMITE_TAXA_JUROS + ".ltj_codigo";
    public static final String LTJ_SVC_CODIGO                   = TB_LIMITE_TAXA_JUROS + ".svc_codigo";
    public static final String LTJ_PRAZO_REF                    = TB_LIMITE_TAXA_JUROS + ".ltj_prazo_ref";
    public static final String LTJ_JUROS_MAX                    = TB_LIMITE_TAXA_JUROS + ".ltj_juros_max";
    public static final String LTJ_VLR_REF                      = TB_LIMITE_TAXA_JUROS + ".ltj_vlr_ref";

    public static final String TB_RECLAMACAO_REGISTRO_SERVIDOR  = "tb_reclamacao_registro_ser";
    public static final String RRS_CODIGO                       = TB_RECLAMACAO_REGISTRO_SERVIDOR + ".rrs_codigo";
    public static final String RRS_RSE_CODIGO                   = TB_RECLAMACAO_REGISTRO_SERVIDOR + ".rse_codigo";
    public static final String RRS_CSA_CODIGO                   = TB_RECLAMACAO_REGISTRO_SERVIDOR + ".csa_codigo";
    public static final String RRS_DATA                         = TB_RECLAMACAO_REGISTRO_SERVIDOR + ".rrs_data";
    public static final String RRS_TEXTO                        = TB_RECLAMACAO_REGISTRO_SERVIDOR + ".rrs_texto";
    public static final String RRS_IP_ACESSO                    = TB_RECLAMACAO_REGISTRO_SERVIDOR + ".rrs_ip_acesso";

    public static final String TB_STATUS_SOLICITACAO            = "tb_status_solicitacao";
    public static final String SSO_CODIGO                       = TB_STATUS_SOLICITACAO + ".sso_codigo";
    public static final String SSO_DESCRICAO                    = TB_STATUS_SOLICITACAO + ".sso_descricao";

    public static final String TB_TIPO_SOLICITACAO              = "tb_tipo_solicitacao";
    public static final String TIS_CODIGO                       = TB_TIPO_SOLICITACAO + ".tis_codigo";
    public static final String TIS_DESCRICAO                    = TB_TIPO_SOLICITACAO + ".tis_descricao";

    public static final String TB_SOLICITACAO_AUTORIZACAO       = "tb_solicitacao_autorizacao";
    public static final String SOA_CODIGO                       = TB_SOLICITACAO_AUTORIZACAO + ".soa_codigo";
    public static final String SOA_ADE_CODIGO                   = TB_SOLICITACAO_AUTORIZACAO + ".ade_codigo";
    public static final String SOA_USU_CODIGO                   = TB_SOLICITACAO_AUTORIZACAO + ".usu_codigo";
    public static final String SOA_TIS_CODIGO                   = TB_SOLICITACAO_AUTORIZACAO + ".tis_codigo";
    public static final String SOA_SSO_CODIGO                   = TB_SOLICITACAO_AUTORIZACAO + ".sso_codigo";
    public static final String SOA_DATA                         = TB_SOLICITACAO_AUTORIZACAO + ".soa_data";
    public static final String SOA_DATA_VALIDADE                = TB_SOLICITACAO_AUTORIZACAO + ".soa_data_validade";
    public static final String SOA_DATA_RESPOSTA                = TB_SOLICITACAO_AUTORIZACAO + ".soa_data_resposta";
    public static final String SOA_OBS                          = TB_SOLICITACAO_AUTORIZACAO + ".soa_obs";
    public static final String SOA_OSO_CODIGO                   = TB_SOLICITACAO_AUTORIZACAO + ".oso_codigo";

    public static final String TB_STATUS_PROPOSTA               = "tb_status_proposta";
    public static final String STP_CODIGO                       = TB_STATUS_PROPOSTA + ".stp_codigo";
    public static final String STP_DESCRICAO                    = TB_STATUS_PROPOSTA + ".stp_descricao";

    public static final String TB_PROPOSTA_PAGAMENTO_DIVIDA     = "tb_proposta_pagamento_divida";
    public static final String PPD_CODIGO                       = TB_PROPOSTA_PAGAMENTO_DIVIDA + ".ppd_codigo";
    public static final String PPD_ADE_CODIGO                   = TB_PROPOSTA_PAGAMENTO_DIVIDA + ".ade_codigo";
    public static final String PPD_CSA_CODIGO                   = TB_PROPOSTA_PAGAMENTO_DIVIDA + ".csa_codigo";
    public static final String PPD_USU_CODIGO                   = TB_PROPOSTA_PAGAMENTO_DIVIDA + ".usu_codigo";
    public static final String PPD_STP_CODIGO                   = TB_PROPOSTA_PAGAMENTO_DIVIDA + ".stp_codigo";
    public static final String PPD_NUMERO                       = TB_PROPOSTA_PAGAMENTO_DIVIDA + ".ppd_numero";
    public static final String PPD_VALOR_DIVIDA                 = TB_PROPOSTA_PAGAMENTO_DIVIDA + ".ppd_valor_divida";
    public static final String PPD_VALOR_PARCELA                = TB_PROPOSTA_PAGAMENTO_DIVIDA + ".ppd_valor_parcela";
    public static final String PPD_PRAZO                        = TB_PROPOSTA_PAGAMENTO_DIVIDA + ".ppd_prazo";
    public static final String PPD_TAXA_JUROS                   = TB_PROPOSTA_PAGAMENTO_DIVIDA + ".ppd_taxa_juros";
    public static final String PPD_DATA_CADASTRO                = TB_PROPOSTA_PAGAMENTO_DIVIDA + ".ppd_data_cadastro";
    public static final String PPD_DATA_VALIDADE                = TB_PROPOSTA_PAGAMENTO_DIVIDA + ".ppd_data_validade";

    public static final String TB_PROPOSTA_LEILAO_SOLICITACAO   = "tb_proposta_leilao_solicitacao";
    public static final String PLS_CODIGO                       = TB_PROPOSTA_LEILAO_SOLICITACAO + ".pls_codigo";
    public static final String PLS_ADE_CODIGO                   = TB_PROPOSTA_LEILAO_SOLICITACAO + ".ade_codigo";
    public static final String PLS_STP_CODIGO                   = TB_PROPOSTA_LEILAO_SOLICITACAO + ".stp_codigo";
    public static final String PLS_USU_CODIGO                   = TB_PROPOSTA_LEILAO_SOLICITACAO + ".usu_codigo";
    public static final String PLS_CSA_CODIGO                   = TB_PROPOSTA_LEILAO_SOLICITACAO + ".csa_codigo";
    public static final String PLS_SVC_CODIGO                   = TB_PROPOSTA_LEILAO_SOLICITACAO + ".svc_codigo";
    public static final String PLS_NUMERO                       = TB_PROPOSTA_LEILAO_SOLICITACAO + ".pls_numero";
    public static final String PLS_VALOR_LIBERADO               = TB_PROPOSTA_LEILAO_SOLICITACAO + ".pls_valor_liberado";
    public static final String PLS_VALOR_PARCELA                = TB_PROPOSTA_LEILAO_SOLICITACAO + ".pls_valor_parcela";
    public static final String PLS_PRAZO                        = TB_PROPOSTA_LEILAO_SOLICITACAO + ".pls_prazo";
    public static final String PLS_TAXA_JUROS                   = TB_PROPOSTA_LEILAO_SOLICITACAO + ".pls_taxa_juros";
    public static final String PLS_DATA_CADASTRO                = TB_PROPOSTA_LEILAO_SOLICITACAO + ".pls_data_cadastro";
    public static final String PLS_DATA_VALIDADE                = TB_PROPOSTA_LEILAO_SOLICITACAO + ".pls_data_validade";
    public static final String PLS_OFERTA_AUT_DECREMENTO        = TB_PROPOSTA_LEILAO_SOLICITACAO + ".pls_oferta_aut_decremento";
    public static final String PLS_OFERTA_AUT_TAXA_MIN          = TB_PROPOSTA_LEILAO_SOLICITACAO + ".pls_oferta_aut_taxa_min";
    public static final String PLS_OFERTA_AUT_EMAIL             = TB_PROPOSTA_LEILAO_SOLICITACAO + ".pls_oferta_aut_email";
    public static final String PLS_TXT_CONTATO_CSA              = TB_PROPOSTA_LEILAO_SOLICITACAO + ".pls_txt_contato_csa";

    public static final String TB_TIPO_BASE_CALCULO             = "tb_tipo_base_calculo";
    public static final String TBC_CODIGO                       = TB_TIPO_BASE_CALCULO + ".tbc_codigo";
    public static final String TBC_DESCRICAO                    = TB_TIPO_BASE_CALCULO + ".tbc_descricao";

    public static final String TB_BASE_CALC_REGISTRO_SERVIDOR   = "tb_base_calc_registro_servidor";
    public static final String BCS_TBC_CODIG                    = TB_BASE_CALC_REGISTRO_SERVIDOR + ".tbc_codigo";
    public static final String BCS_RSE_CODIG                    = TB_BASE_CALC_REGISTRO_SERVIDOR + ".rse_codigo";
    public static final String BCS_VALOR                        = TB_BASE_CALC_REGISTRO_SERVIDOR + ".bcs_valor";

    public static final String TB_TEXTO_SISTEMA                 = "tb_texto_sistema";
    public static final String TEX_CHAVE                        = TB_TEXTO_SISTEMA + ".tex_chave";
    public static final String TEX_TEXTO                        = TB_TEXTO_SISTEMA + ".tex_texto";
    public static final String TEX_DATA_ALTERACAO               = TB_TEXTO_SISTEMA + ".tex_data_alteracao";

    public static final String TB_CAMPO_SISTEMA                 = "tb_campo_sistema";
    public static final String CAS_CHAVE                        = TB_CAMPO_SISTEMA + ".cas_chave";
    public static final String CAS_VALOR                        = TB_CAMPO_SISTEMA + ".cas_valor";

    public static final String TB_RECURSO_SISTEMA               = "tb_recurso_sistema";
    public static final String RES_CHAVE                        = TB_RECURSO_SISTEMA + ".res_chave";
    public static final String RES_CONTEUDO                     = TB_RECURSO_SISTEMA + ".res_conteudo";

    public static final String TB_PARAM_SENHA_EXTERNA           = "tb_param_senha_externa";
    public static final String PSX_CHAVE                        = TB_PARAM_SENHA_EXTERNA + ".psx_chave";
    public static final String PSX_VALOR                        = TB_PARAM_SENHA_EXTERNA + ".psx_valor";

    public static final String TB_UF                            = "tb_uf";
    public static final String UF_COD                           = TB_UF + ".uf_cod";
    public static final String UF_NOME                          = TB_UF + ".uf_nome";

    public static final String TB_CIDADE                        = "tb_cidade";
    public static final String CID_CODIGO                       = TB_CIDADE + ".cid_codigo";
    public static final String CID_UF_CODIGO                    = TB_CIDADE + ".uf_cod";
    public static final String CID_NOME                         = TB_CIDADE + ".cid_nome";
    public static final String CID_DDD                          = TB_CIDADE + ".cid_ddd";
    public static final String CID_CODIGO_IBGE                  = TB_CIDADE + ".cid_codigo_ibge";

    public static final String TB_USUARIO_CHAVE_DISPOSITIVO     = "tb_usuario_chave_dispositivo";
    public static final String UCD_USU_CODIGO                   = TB_USUARIO_CHAVE_DISPOSITIVO + ".usu_codigo";
    public static final String UCD_TOKEN                        = TB_USUARIO_CHAVE_DISPOSITIVO + ".ucd_token";
    public static final String UCD_DATA_CRIACAO                 = TB_USUARIO_CHAVE_DISPOSITIVO + ".ucd_data_criacao";
    public static final String UCD_DATA_UTILIZACAO              = TB_USUARIO_CHAVE_DISPOSITIVO + ".ucd_data_utilizacao";
    public static final String UCD_TDI_CODIGO                   = TB_USUARIO_CHAVE_DISPOSITIVO + ".tdi_codigo";

    public static final String TB_FILTRO_LEILAO_SOLICITACAO     = "tb_filtro_leilao_solicitacao";
    public static final String FLS_CODIGO                       = TB_FILTRO_LEILAO_SOLICITACAO + ".fls_codigo";
    public static final String FLS_POS_CODIGO                   = TB_FILTRO_LEILAO_SOLICITACAO + ".pos_codigo";
    public static final String FLS_CID_CODIGO                   = TB_FILTRO_LEILAO_SOLICITACAO + ".cid_codigo";
    public static final String FLS_USU_CODIGO                   = TB_FILTRO_LEILAO_SOLICITACAO + ".usu_codigo";
    public static final String FLS_DESCRICAO                    = TB_FILTRO_LEILAO_SOLICITACAO + ".fls_descricao";
    public static final String FLS_DATA                         = TB_FILTRO_LEILAO_SOLICITACAO + ".fls_data";
    public static final String FLS_EMAIL_NOTIFICACAO            = TB_FILTRO_LEILAO_SOLICITACAO + ".fls_email_notificacao";
    public static final String FLS_DATA_ABERTURA_INI            = TB_FILTRO_LEILAO_SOLICITACAO + ".fls_data_abertura_ini";
    public static final String FLS_DATA_ABERTURA_FIM            = TB_FILTRO_LEILAO_SOLICITACAO + ".fls_data_abertura_fim";
    public static final String FLS_HORAS_ENCERRAMENTO           = TB_FILTRO_LEILAO_SOLICITACAO + ".fls_horas_encerramento";
    public static final String FLS_PONTUACAO_MIN                = TB_FILTRO_LEILAO_SOLICITACAO + ".fls_pontuacao_min";
    public static final String FLS_MARGEM_LIVRE_MAX             = TB_FILTRO_LEILAO_SOLICITACAO + ".fls_margem_livre_max";
    public static final String FLS_TIPO_PESQUISA                = TB_FILTRO_LEILAO_SOLICITACAO + ".fls_tipo_pesquisa";
    public static final String FLS_MATRICULA                    = TB_FILTRO_LEILAO_SOLICITACAO + ".fls_matricula";
    public static final String FLS_CPF                          = TB_FILTRO_LEILAO_SOLICITACAO + ".fls_cpf";
    public static final String FLS_ANALISE_RISCO                = TB_FILTRO_LEILAO_SOLICITACAO + ".fls_analise_risco";

    public static final String TB_ENDERECO_ACESSO_FUNCAO        = "tb_endereco_acesso_funcao";
    public static final String EAF_IP_ACESSO                    = TB_ENDERECO_ACESSO_FUNCAO + ".eaf_ip_acesso";
    public static final String EAF_DDNS_ACESSO                  = TB_ENDERECO_ACESSO_FUNCAO + ".eaf_ddns_acesso";

    public static final String TB_OCORRENCIA_PARAM_SIST_CSE     = "tb_ocorrencia_param_sist_cse";
    public static final String OPS_CODIGO                       = TB_OCORRENCIA_PARAM_SIST_CSE + ".ops_codigo";
    public static final String OPS_DATA                         = TB_OCORRENCIA_PARAM_SIST_CSE + ".ops_data";
    public static final String OPS_OBS                          = TB_OCORRENCIA_PARAM_SIST_CSE + ".ops_obs";
    public static final String OPS_IP_ACESSO                    = TB_OCORRENCIA_PARAM_SIST_CSE + ".ops_ip_acesso";

    public static final String TB_ANALISE_RISCO_REGISTRO_SERVIDOR  = "tb_analise_risco_registro_ser";
    public static final String ARR_CODIGO                          = TB_ANALISE_RISCO_REGISTRO_SERVIDOR + ".arr_codigo";
    public static final String ARR_USU_CODIGO                      = TB_ANALISE_RISCO_REGISTRO_SERVIDOR + ".usu_codigo";
    public static final String ARR_RSE_CODIGO                      = TB_ANALISE_RISCO_REGISTRO_SERVIDOR + ".rse_codigo";
    public static final String ARR_CSA_CODIGO                      = TB_ANALISE_RISCO_REGISTRO_SERVIDOR + ".csa_codigo";
    public static final String ARR_RISCO                           = TB_ANALISE_RISCO_REGISTRO_SERVIDOR + ".arr_risco";
    public static final String ARR_DATA                            = TB_ANALISE_RISCO_REGISTRO_SERVIDOR + ".arr_data";

    public static final String TB_NATUREZA_EDITAVEL_NSE            = "tb_natureza_editavel_nse";
    public static final String NEN_TNT_CODIGO                      = TB_NATUREZA_EDITAVEL_NSE + ".tnt_codigo";
    public static final String NEN_NSE_CODIGO                      = TB_NATUREZA_EDITAVEL_NSE + ".nse_codigo";

    public static final String TB_TIPO_HABITACAO                   = "tb_tipo_habitacao";
    public static final String THA_CODIGO                          = TB_TIPO_HABITACAO + ".tha_codigo";
    public static final String THA_IDENTIFICADOR                   = TB_TIPO_HABITACAO + ".tha_identificador";
    public static final String THA_DESCRICAO                       = TB_TIPO_HABITACAO + ".tha_descricao";

    public static final String TB_NIVEL_ESCOLARIDADE                = "tb_nivel_escolaridade";
    public static final String NES_CODIGO                          = TB_NIVEL_ESCOLARIDADE + ".nes_codigo";
    public static final String NES_IDENTIFICADOR                   = TB_NIVEL_ESCOLARIDADE + ".nes_identificador";
    public static final String NES_DESCRICAO                       = TB_NIVEL_ESCOLARIDADE + ".nes_descricao";

    public static final String TB_BENEFICIARIO                     = "tb_beneficiario";
    public static final String BFC_CODIGO                          = TB_BENEFICIARIO + ".bfc_codigo";
    public static final String BFC_TIB_CODIGO                      = TB_BENEFICIARIO + ".tib_codigo";
    public static final String BFC_MDE_CODIGO                      = TB_BENEFICIARIO + ".mde_codigo";
    public static final String BFC_SER_CODIGO                      = TB_BENEFICIARIO + ".ser_codigo";
    public static final String BFC_ORDEM_DEPENDENCIA               = TB_BENEFICIARIO + ".bfc_ordem_dependencia";
    public static final String BFC_NOME                            = TB_BENEFICIARIO + ".bfc_nome";
    public static final String BFC_CPF                             = TB_BENEFICIARIO + ".bfc_cpf";
    public static final String BFC_RG                              = TB_BENEFICIARIO + ".bfc_rg";
    public static final String BFC_SEXO                            = TB_BENEFICIARIO + ".bfc_sexo";
    public static final String BFC_TELEFONE                        = TB_BENEFICIARIO + ".bfc_telefone";
    public static final String BFC_NOME_MAE                        = TB_BENEFICIARIO + ".bfc_nome_mae";
    public static final String BFC_GRP_CODIGO                      = TB_BENEFICIARIO + ".grp_codigo";
    public static final String BFC_DATA_NASCIMENTO                 = TB_BENEFICIARIO + ".bfc_data_nascimento";
    public static final String BFC_ESTADO_CIVIL                    = TB_BENEFICIARIO + ".bfc_estado_civil";
    public static final String BFC_SUBSIDIO_CONCEDIDO              = TB_BENEFICIARIO + ".bfc_subsidio_concedido";
    public static final String BFC_SUBSIDIO_CONCEDIDO_MOTIVO       = TB_BENEFICIARIO + ".bfc_subsidio_concedido_motivo";
    public static final String BFC_CELULAR                         = TB_BENEFICIARIO + ".bfc_celular";
    public static final String BFC_EXCECAO_DEPENDENCIA_INI         = TB_BENEFICIARIO + ".bfc_excecao_dependencia_ini";
    public static final String BFC_EXCECAO_DEPENDENCIA_FIM         = TB_BENEFICIARIO + ".bfc_excecao_dependencia_fim";
    public static final String BFC_SBE_CODIGO                      = TB_BENEFICIARIO + ".sbe_codigo";
    public static final String BFC_NAC_CODIGO                      = TB_BENEFICIARIO + ".nac_codigo";
    public static final String BFC_DATA_CASAMENTO                  = TB_BENEFICIARIO + ".bfc_data_casamento";
    public static final String BFC_DATA_OBITO                      = TB_BENEFICIARIO + ".bfc_data_obito";
    public static final String BFC_IDENTIFICADOR                   = TB_BENEFICIARIO + ".bfc_identificador";
    public static final String BFC_CLASSIFICACAO                   = TB_BENEFICIARIO + ".bfc_classificacao";
    public static final String BFC_RSE_CODIGO                      = TB_BENEFICIARIO + ".rse_codigo";

    public static final String TB_BENEFICIO                        = "tb_beneficio";
    public static final String BEN_CODIGO                          = TB_BENEFICIO + ".ben_codigo";
    public static final String BEN_CSA_CODIGO                      = TB_BENEFICIO + ".csa_codigo";
    public static final String BEN_COR_CODIGO                      = TB_BENEFICIO + ".cor_codigo";
    public static final String BEN_NSE_CODIGO                      = TB_BENEFICIO + ".nse_codigo";
    public static final String BEN_DESCRICAO                       = TB_BENEFICIO + ".ben_descricao";
    public static final String BEN_CODIGO_PLANO                    = TB_BENEFICIO + ".ben_codigo_plano";
    public static final String BEN_CODIGO_REGISTRO                 = TB_BENEFICIO + ".ben_codigo_registro";
    public static final String BEN_CODIGO_CONTRATO                 = TB_BENEFICIO + ".ben_codigo_contrato";
    public static final String BEN_CATEGORIA                       = TB_BENEFICIO + ".ben_categoria";
    public static final String BEN_MBE_CODIGO                      = TB_BENEFICIO + ".mbe_codigo";
    public static final String BEN_ATIVO                           = TB_BENEFICIO + ".ben_ativo";
    public static final String BEN_TEXTO_COR                       = TB_BENEFICIO + ".ben_texto_cor";
    public static final String BEN_IMAGEM_BENEFICIO                = TB_BENEFICIO + ".ben_imagem_beneficio";
    public static final String BEN_LINK_BENEFICIO                  = TB_BENEFICIO + ".ben_link_beneficio";
    public static final String BEN_TEXTO_LINK_BENEFICIO            = TB_BENEFICIO + ".ben_texto_link_beneficio";


    public static final String TB_CONTRATO_BENEFICIO               = "tb_contrato_beneficio";
    public static final String CBE_CODIGO                          = TB_CONTRATO_BENEFICIO + ".cbe_codigo";
    public static final String CBE_BFC_CODIGO                      = TB_CONTRATO_BENEFICIO + ".bfc_codigo";
    public static final String CBE_BEN_CODIGO                      = TB_CONTRATO_BENEFICIO + ".ben_codigo";
    public static final String CBE_NUMERO                          = TB_CONTRATO_BENEFICIO + ".cbe_numero";
    public static final String CBE_DATA_INCLUSAO                   = TB_CONTRATO_BENEFICIO + ".cbe_data_inclusao";
    public static final String CBE_DATA_INICIO_VIGENCIA            = TB_CONTRATO_BENEFICIO + ".cbe_data_inicio_vigencia";
    public static final String CBE_ITEM_LOTE                       = TB_CONTRATO_BENEFICIO + ".cbe_item_lote";
    public static final String CBE_NUMERO_LOTE                     = TB_CONTRATO_BENEFICIO + ".cbe_numero_lote";
    public static final String CBE_VALOR_TOTAL                     = TB_CONTRATO_BENEFICIO + ".cbe_valor_total";
    public static final String CBE_VALOR_SUBSIDIO                  = TB_CONTRATO_BENEFICIO + ".cbe_valor_subsidio";
    public static final String CBE_DATA_FIM_VIGENCIA               = TB_CONTRATO_BENEFICIO + ".cbe_data_fim_vigencia";
    public static final String CBE_SCB_CODIGO                      = TB_CONTRATO_BENEFICIO + ".sbc_codigo";
    public static final String CBE_DATA_CANCELAMENTO               = TB_CONTRATO_BENEFICIO + ".cbe_data_cancelamento";

    public static final String TB_ENDERECO_SERVIDOR                = "tb_endereco_servidor";
    public static final String ENS_CODIGO                          =  TB_ENDERECO_SERVIDOR + ".ens_codigo";
    public static final String ENS_SER_CODIGO                      =  TB_ENDERECO_SERVIDOR + ".ser_codigo";
    public static final String ENS_TIE_CODIGO                      =  TB_ENDERECO_SERVIDOR + ".tie_codigo";
    public static final String ENS_LOGRADOURO                      =  TB_ENDERECO_SERVIDOR + ".ens_logradouro";
    public static final String ENS_NUMERO                          =  TB_ENDERECO_SERVIDOR + ".ens_numero";
    public static final String ENS_COMPLEMENTO                     =  TB_ENDERECO_SERVIDOR + ".ens_complemento";
    public static final String ENS_BAIRRO                          =  TB_ENDERECO_SERVIDOR + ".ens_bairro";
    public static final String ENS_MUNICIPIO                       =  TB_ENDERECO_SERVIDOR + ".ens_municipio";
    public static final String ENS_UF                              =  TB_ENDERECO_SERVIDOR + ".ens_uf";
    public static final String ENS_CEP                             =  TB_ENDERECO_SERVIDOR + ".ens_cep";
    public static final String ENS_ATIVO                           =  TB_ENDERECO_SERVIDOR + ".ens_ativo";
    public static final String ENS_CODIGO_MUNICIPIO                =  TB_ENDERECO_SERVIDOR + ".ens_codigo_municipio";

    public static final String TB_TIPO_ENDERECO_SERVIDOR           = "tb_tipo_endereco";
    public static final String TIE_CODIGO                          = TB_TIPO_ENDERECO_SERVIDOR + ".tie_codigo";
    public static final String TIE_DESCRICAO                       = TB_TIPO_ENDERECO_SERVIDOR + ".tie_descricao";

    public static final String TB_TIPO_LANCAMENTO                  = "tb_tipo_lancamento";
    public static final String TLA_CODIGO                          = TB_TIPO_LANCAMENTO + ".tla_codigo";
    public static final String TLA_TNT_CODIGO                      = TB_TIPO_LANCAMENTO + ".tnt_codigo";
    public static final String TLA_NSE_CODIGO                      = TB_TIPO_LANCAMENTO + ".nse_codigo";
    public static final String TLA_CODIGO_PAI                      = TB_TIPO_LANCAMENTO + ".tla_codigo_pai";
    public static final String TLA_DESCRICAO                       = TB_TIPO_LANCAMENTO + ".tla_descricao";

    public static final String TB_CALCULO_BENEFICIO                = "tb_calculo_beneficio";
    public static final String CLB_CODIGO                          = TB_CALCULO_BENEFICIO + ".clb_codigo";
    public static final String CLB_TIB_CODIGO                      = TB_CALCULO_BENEFICIO + ".tib_codigo";
    public static final String CLB_ORG_CODIGO                      = TB_CALCULO_BENEFICIO + ".org_codigo";
    public static final String CLB_BEN_CODIGO                      = TB_CALCULO_BENEFICIO + ".ben_codigo";
    public static final String CLB_VIGENCIA_INI                    = TB_CALCULO_BENEFICIO + ".clb_vigencia_ini";
    public static final String CLB_VIGENCIA_FIM                    = TB_CALCULO_BENEFICIO + ".clb_vigencia_fim";
    public static final String CLB_VALOR_MENSALIDADE               = TB_CALCULO_BENEFICIO + ".clb_valor_mensalidade";
    public static final String CLB_VALOR_SUBSIDIO                  = TB_CALCULO_BENEFICIO + ".clb_valor_subsidio";
    public static final String CLB_FAIXA_ETARIA_INI                = TB_CALCULO_BENEFICIO + ".clb_faixa_etaria_ini";
    public static final String CLB_FAIXA_ETARIA_FIM                = TB_CALCULO_BENEFICIO + ".clb_faixa_etaria_fim";
    public static final String CLB_FAIXA_SALARIAL_INI              = TB_CALCULO_BENEFICIO + ".clb_faixa_salarial_ini";
    public static final String CLB_FAIXA_SALARIAL_FIM              = TB_CALCULO_BENEFICIO + ".clb_faixa_salarial_fim";

    public static final String TB_TIPO_BENEFICIARIO                = "tb_tipo_beneficiario";
    public static final String TIB_CODIGO                          = TB_TIPO_BENEFICIARIO + ".tib_codigo";
    public static final String TIB_DESCRICAO                       = TB_TIPO_BENEFICIARIO + ".tib_descricao";

    public static final String TB_MOTIVO_DEPENDENCIA               = "tb_motivo_dependencia";
    public static final String MDE_CODIGO                          = TB_MOTIVO_DEPENDENCIA + ".mde_codigo";
    public static final String MDE_DESCRICAO                       = TB_MOTIVO_DEPENDENCIA + ".mde_descricao";

    public static final String TB_OCORRENCIA_BENEFICIARIO          = "tb_ocorrencia_beneficiario";
    public static final String OBE_CODIGO                          = TB_OCORRENCIA_BENEFICIARIO + ".obe_codigo";
    public static final String OBE_TOC_CODIGO                      = TB_OCORRENCIA_BENEFICIARIO + ".toc_codigo";
    public static final String OBE_USU_CODIGO                      = TB_OCORRENCIA_BENEFICIARIO + ".usu_codigo";
    public static final String OBE_BFC_CODIGO                      = TB_OCORRENCIA_BENEFICIARIO + ".bfc_codigo";
    public static final String OBE_TMO_CODIGO                      = TB_OCORRENCIA_BENEFICIARIO + ".tmo_codigo";
    public static final String OBE_DATA                            = TB_OCORRENCIA_BENEFICIARIO + ".obe_data";
    public static final String OBE_OBS                             = TB_OCORRENCIA_BENEFICIARIO + ".obe_obs";
    public static final String OBE_IP_ACESSO                       = TB_OCORRENCIA_BENEFICIARIO + ".obe_ip_acesso";

    public static final String TB_OCORRENCIA_CTT_BENEFICIO         = "tb_ocorrencia_ctt_beneficio";
    public static final String OCB_CODIGO                          = TB_OCORRENCIA_CTT_BENEFICIO + ".ocb_codigo";
    public static final String OCB_TOC_CODIGO                      = TB_OCORRENCIA_CTT_BENEFICIO + ".toc_codigo";
    public static final String OCB_USU_CODIGO                      = TB_OCORRENCIA_CTT_BENEFICIO + ".usu_codigo";
    public static final String OCB_CBE_CODIGO                      = TB_OCORRENCIA_CTT_BENEFICIO + ".cbe_codigo";
    public static final String OCB_TMO_CODIGO                      = TB_OCORRENCIA_CTT_BENEFICIO + ".tmo_codigo";
    public static final String OCB_DATA                            = TB_OCORRENCIA_CTT_BENEFICIO + ".ocb_data";
    public static final String OCB_OBS                             = TB_OCORRENCIA_CTT_BENEFICIO + ".ocb_obs";
    public static final String OCB_IP_ACESSO                       = TB_OCORRENCIA_CTT_BENEFICIO + ".ocb_ip_acesso";

    public static final String TB_OCORRENCIA_ENDERECO_SER          = "tb_ocorrencia_endereco_ser";
    public static final String OES_CODIGO                          = TB_OCORRENCIA_ENDERECO_SER + ".oes_codigo";
    public static final String OES_TOC_CODIGO                      = TB_OCORRENCIA_ENDERECO_SER + ".toc_codigo";
    public static final String OES_USU_CODIGO                      = TB_OCORRENCIA_ENDERECO_SER + ".usu_codigo";
    public static final String OES_ENS_CODIGO                      = TB_OCORRENCIA_ENDERECO_SER + ".ens_codigo";
    public static final String OES_TMO_CODIGO                      = TB_OCORRENCIA_ENDERECO_SER + ".tmo_codigo";
    public static final String OES_DATA                            = TB_OCORRENCIA_ENDERECO_SER + ".oes_data";
    public static final String OES_OBS                             = TB_OCORRENCIA_ENDERECO_SER + ".oes_obs";
    public static final String OES_IP_ACESSO                       = TB_OCORRENCIA_ENDERECO_SER + ".oes_ip_acesso";

    public static final String TB_STATUS_SERVIDOR                  = "tb_status_servidor";
    public static final String SSE_CODIGO                          = TB_STATUS_SERVIDOR + ".sse_codigo";
    public static final String SSE_DESCRICAO                       = TB_STATUS_SERVIDOR + ".sse_descricao";

    public static final String TB_MEMORIA_CALCULO_SUBSIDIO = "tb_memoria_calculo_subsidio";
    public static final String MCS_CODIGO                  = TB_MEMORIA_CALCULO_SUBSIDIO + ".mcs_codigo";
    public static final String MCS_CBE_CODIGO              = TB_MEMORIA_CALCULO_SUBSIDIO + ".cbe_codigo";
    public static final String MCS_DATA                    = TB_MEMORIA_CALCULO_SUBSIDIO + ".mcs_data";
    public static final String MCS_VALOR_BENEFICIO         = TB_MEMORIA_CALCULO_SUBSIDIO + ".mcs_valor_beneficio";
    public static final String MCS_VALOR_SUBSIDIO          = TB_MEMORIA_CALCULO_SUBSIDIO + ".mcs_valor_subsidio";
    public static final String MCS_OBS                     = TB_MEMORIA_CALCULO_SUBSIDIO + ".mcs_obs";

    public static final String TB_OCORRENCIA_SERVICO = "tb_ocorrencia_servico";
    public static final String OSE_CODIGO            = TB_OCORRENCIA_SERVICO + ".ose_codigo";
    public static final String OSE_SVC_CODIGO        = TB_OCORRENCIA_SERVICO + ".svc_codigo";
    public static final String OSE_USU_CODIGO        = TB_OCORRENCIA_SERVICO + ".usu_codigo";
    public static final String OSE_TOC_CODIGO        = TB_OCORRENCIA_SERVICO + ".toc_codigo";
    public static final String OSE_TMO_CODIGO        = TB_OCORRENCIA_SERVICO + ".tmo_codigo";
    public static final String OSE_DATA              = TB_OCORRENCIA_SERVICO + ".ose_data";
    public static final String OSE_OBS               = TB_OCORRENCIA_SERVICO + ".ose_obs";
    public static final String OSE_IP_ACESSO         = TB_OCORRENCIA_SERVICO + ".ose_ip_acesso";

    public static final String TB_OCORRENCIA_CONVENIO = "tb_ocorrencia_convenio";
    public static final String OCO_CODIGO            = TB_OCORRENCIA_CONVENIO + ".oco_codigo";
    public static final String OCO_CNV_CODIGO        = TB_OCORRENCIA_CONVENIO + ".cnv_codigo";
    public static final String OCO_USU_CODIGO        = TB_OCORRENCIA_CONVENIO + ".usu_codigo";
    public static final String OCO_TOC_CODIGO        = TB_OCORRENCIA_CONVENIO + ".toc_codigo";
    public static final String OCO_TMO_CODIGO        = TB_OCORRENCIA_CONVENIO + ".tmo_codigo";
    public static final String OCO_DATA              = TB_OCORRENCIA_CONVENIO + ".oco_data";
    public static final String OCO_OBS               = TB_OCORRENCIA_CONVENIO + ".oco_obs";
    public static final String OCO_IP_ACESSO         = TB_OCORRENCIA_CONVENIO + ".oco_ip_acesso";

    public static final String TB_GRAU_PARENTESCO    = "tb_grau_parentesco";
    public static final String GRP_CODIGO            = TB_GRAU_PARENTESCO + ".grp_codigo";
    public static final String GRP_DESCRICAO         = TB_GRAU_PARENTESCO + ".grp_descricao";

    public static final String TB_ANEXO_BENEFICIARIO = "tb_anexo_beneficiario";
    public static final String ABF_BFC_CODIGO        = TB_ANEXO_BENEFICIARIO + ".bfc_codigo";
    public static final String ABF_USU_CODIGO        = TB_ANEXO_BENEFICIARIO + ".usu_codigo";
    public static final String ABF_TAR_CODIGO        = TB_ANEXO_BENEFICIARIO + ".tar_codigo";
    public static final String ABF_NOME              = TB_ANEXO_BENEFICIARIO + ".abf_nome";
    public static final String ABF_DESCRICAO         = TB_ANEXO_BENEFICIARIO + ".abf_descricao";
    public static final String ABF_ATIVO             = TB_ANEXO_BENEFICIARIO + ".abf_ativo";
    public static final String ABF_DATA              = TB_ANEXO_BENEFICIARIO + ".abf_data";
    public static final String ABF_DATA_VALIDADE     = TB_ANEXO_BENEFICIARIO + ".abf_data_validade";
    public static final String ABF_IP_ACESSO         = TB_ANEXO_BENEFICIARIO + ".abf_ip_acesso";

    public static final String TB_CALENDARIO_BENEFICIO_CSE = "tb_calendario_beneficio_cse";
    public static final String CBC_CSE_CODIGO              = TB_CALENDARIO_BENEFICIO_CSE + ".cse_codigo";
    public static final String CBC_PERIODO                 = TB_CALENDARIO_BENEFICIO_CSE + ".cbc_periodo";
    public static final String CBC_DIA_CORTE               = TB_CALENDARIO_BENEFICIO_CSE + ".cbc_dia_corte";
    public static final String CBC_DATA_INI                = TB_CALENDARIO_BENEFICIO_CSE + ".cbc_data_ini";
    public static final String CBC_DATA_FIM                = TB_CALENDARIO_BENEFICIO_CSE + ".cbc_data_fim";

    public static final String TB_PERIODO_BENEFICIO        = "tb_periodo_beneficio";
    public static final String PBE_ORG_CODIGO              = TB_PERIODO_BENEFICIO + ".org_codigo";
    public static final String PBE_DIA_CORTE               = TB_PERIODO_BENEFICIO + ".pbe_dia_corte";
    public static final String PBE_DATA_INI                = TB_PERIODO_BENEFICIO + ".pbe_data_ini";
    public static final String PBE_DATA_FIM                = TB_PERIODO_BENEFICIO + ".pbe_data_fim";
    public static final String PBE_PERIODO                 = TB_PERIODO_BENEFICIO + ".pbe_periodo";
    public static final String PBE_PERIODO_ANT             = TB_PERIODO_BENEFICIO + ".pbe_periodo_ant";
    public static final String PBE_PERIODO_POS             = TB_PERIODO_BENEFICIO + ".pbe_periodo_pos";
    public static final String PBE_SEQUENCIA               = TB_PERIODO_BENEFICIO + ".pbe_sequencia";

    public static final String TB_STATUS_BENEFICIARIO      = "tb_status_beneficiario";
    public static final String SBE_CODIGO                  = TB_STATUS_BENEFICIARIO + ".sbe_codigo";
    public static final String SBE_DESCRICAO               = TB_STATUS_BENEFICIARIO + ".sbe_descricao";

    public static final String TB_STATUS_CONTRATO_BENEFICIO = "tb_status_contrato_beneficio";
    public static final String SCB_CODIGO                   = TB_STATUS_CONTRATO_BENEFICIO + ".scb_codigo";
    public static final String SCB_DESCRICAO                = TB_STATUS_CONTRATO_BENEFICIO + ".scb_descricao";

    public static final String TB_HIST_INTEGRACAO_BENEFICIO = "tb_hist_integracao_beneficio";
    public static final String HIB_CODIGO                   = TB_HIST_INTEGRACAO_BENEFICIO + ".hib_codigo";
    public static final String HIB_CSA_CODIGO               = TB_HIST_INTEGRACAO_BENEFICIO + ".CSA_CODIGO";
    public static final String HIB_USU_CODIGO               = TB_HIST_INTEGRACAO_BENEFICIO + ".usu_codigo";
    public static final String HIB_PERIODO                  = TB_HIST_INTEGRACAO_BENEFICIO + ".hib_periodo";
    public static final String HIB_DATA_INI                 = TB_HIST_INTEGRACAO_BENEFICIO + ".hib_data_ini";
    public static final String HIB_DATA_FIM                 = TB_HIST_INTEGRACAO_BENEFICIO + ".hib_data_fim";
    public static final String HIB_DATA                     = TB_HIST_INTEGRACAO_BENEFICIO + ".hib_data";
    public static final String HIB_TIPO                     = TB_HIST_INTEGRACAO_BENEFICIO + ".hib_tipo";

    public static final String TB_NACIONALIDADE             = "tb_nacionalidade";
    public static final String NAC_CODIGO                   = TB_NACIONALIDADE + ".nac_codigo";
    public static final String NAC_DESCRICAO                = TB_NACIONALIDADE + ".nac_descricao";

    public static final String TB_BENEFICIO_SERVICO         = "tb_beneficio_servico";
    public static final String BSE_BEN_CODIGO               = TB_BENEFICIO_SERVICO + ".ben_codigo";
    public static final String BSE_SVC_CODIGO               = TB_BENEFICIO_SERVICO + ".svc_codigo";
    public static final String BSE_TIB_CODIGO               = TB_BENEFICIO_SERVICO + ".tib_codigo";
    public static final String BSE_ORDEM                    = TB_BENEFICIO_SERVICO + ".bse_ordem";

    public static final String TB_FATURAMENTO_SERVICO 		= "tb_faturamento_servico";
    public static final String FAT_CODIGO 					= TB_FATURAMENTO_SERVICO + ".fat_codigo";
    public static final String FAT_CSA_CODIGO				= TB_FATURAMENTO_SERVICO + ".csa_codigo";
    public static final String FAT_PERIODO					= TB_FATURAMENTO_SERVICO + ".fat_periodo";
    public static final String FAT_DATA						= TB_FATURAMENTO_SERVICO + ".fat_data";

    public static final String TB_AJUDA_RECURSO				= "tb_ajuda_recurso";
    public static final String AJR_CODIGO					= TB_AJUDA_RECURSO + ".ajr_codigo";
    public static final String AJR_ACR_CODIGO				= TB_AJUDA_RECURSO + ".arc_recurso";
    public static final String AJR_ELEMENTO					= TB_AJUDA_RECURSO + ".acr_elemento";
    public static final String AJR_SEQUENCIA				= TB_AJUDA_RECURSO + ".acr_sequencia";
    public static final String AJR_POSICAO					= TB_AJUDA_RECURSO + ".acr_posicao";
    public static final String AJR_TEXTO					= TB_AJUDA_RECURSO + ".acr_texto";

    public static final String TB_ACESSO_USUARIO			= "tb_acesso_usuario";
    public static final String ACU_ACR_CODIGO				= TB_ACESSO_USUARIO + ".acr_codigo";
    public static final String ACU_USU_CODIGO				= TB_ACESSO_USUARIO + ".usu_codigo";
    public static final String ACU_USU_NUMERO_ACESSO		= TB_ACESSO_USUARIO + ".acu_numero_acesso";

    public static final String TB_ARQUIVO                   = "tb_arquivo";
    public static final String ARQ_CODIGO                   = TB_ARQUIVO + ".arq_codigo";
    public static final String ARQ_TAR_CODIGO               = TB_ARQUIVO + ".tar_codigo";
    public static final String ARQ_CONTEUDO                 = TB_ARQUIVO + ".arq_conteudo";

    public static final String TB_ARQUIVO_SER               = "tb_arquivo_ser";
    public static final String ASE_SER_CODIGO               = TB_ARQUIVO_SER + ".ser_codigo";
    public static final String ASE_ARQ_CODIGO               = TB_ARQUIVO_SER + ".arq_codigo";
    public static final String ASE_USU_CODIGO               = TB_ARQUIVO_SER + ".usu_codigo";
    public static final String ASE_DATA_CRIACAO             = TB_ARQUIVO_SER + ".ase_data_criacao";
    public static final String ASE_NOME                     = TB_ARQUIVO_SER + ".ase_nome";
    public static final String ASE_IP_ACESSO                = TB_ARQUIVO_SER + ".ase_ip_acesso";

    public static final String TB_DIRF_SERVIDOR             = "tb_dirf_servidor";
    public static final String DIS_SER_CODIGO               = TB_DIRF_SERVIDOR + ".SER_CODIGO";
    public static final String DIS_ANO_CALENDARIO           = TB_DIRF_SERVIDOR + ".DIS_ANO_CALENDARIO";
    public static final String DIS_DATA_CARGA               = TB_DIRF_SERVIDOR + ".DIS_DATA_CARGA";
    public static final String DIS_ARQ_CODIGO               = TB_DIRF_SERVIDOR + ".ARQ_CODIGO";

    public static final String TB_DEFINICAO_TAXA_JUROS      = "tb_definicao_taxa_juros";
    public static final String DTJ_CODIGO               	= TB_DEFINICAO_TAXA_JUROS + ".DTJ_CODIGO";
    public static final String DTJ_CONSIGNATARIA            = TB_DEFINICAO_TAXA_JUROS + ".CSA_CODIGO";
    public static final String DTJ_ORG_CODIGO               = TB_DEFINICAO_TAXA_JUROS + ".ORG_CODIGO";
    public static final String DTJ_SVC_CODIGO               = TB_DEFINICAO_TAXA_JUROS + ".svc_codigo";
    public static final String DTJ_FUN_CODIGO               = TB_DEFINICAO_TAXA_JUROS + ".fun_codigo";
    public static final String DTJ_FAIXA_ETARIA_INI         = TB_DEFINICAO_TAXA_JUROS + ".DTJ_FAIXA_ETARIA_INI";
    public static final String DTJ_FAIXA_ETARIA_FIM         = TB_DEFINICAO_TAXA_JUROS + ".DTJ_FAIXA_ETARIA_FIM";
    public static final String DTJ_FAIXA_TEMP_SERVICO_INI   = TB_DEFINICAO_TAXA_JUROS + ".DTJ_FAIXA_TEMP_SERVICO_INI";
    public static final String DTJ_FAIXA_TEMP_SERVICO_FIM   = TB_DEFINICAO_TAXA_JUROS + ".DTJ_FAIXA_TEMP_SERVICO_FIM";
    public static final String DTJ_FAIXA_SALARIO_INI        = TB_DEFINICAO_TAXA_JUROS + ".DTJ_FAIXA_SALARIO_INI";
    public static final String DTJ_FAIXA_SALARIO_FIM        = TB_DEFINICAO_TAXA_JUROS + ".DTJ_FAIXA_SALARIO_FIM";
    public static final String DTJ_FAIXA_MARGEM_INI         = TB_DEFINICAO_TAXA_JUROS + ".DTJ_FAIXA_MARGEM_INI";
    public static final String DTJ_FAIXA_MARGEM_FIM         = TB_DEFINICAO_TAXA_JUROS + ".DTJ_FAIXA_MARGEM_FIM";
    public static final String DTJ_FAIXA_VALOR_TOTAL_INI    = TB_DEFINICAO_TAXA_JUROS + ".DTJ_FAIXA_VALOR_TOTAL_INI";
    public static final String DTJ_FAIXA_VALOR_TOTAL_FIM    = TB_DEFINICAO_TAXA_JUROS + ".DTJ_FAIXA_VALOR_TOTAL_FIM";
    public static final String DTJ_FAIXA_VALOR_CONTRATO_INI = TB_DEFINICAO_TAXA_JUROS + ".DTJ_FAIXA_VALOR_CONTRATO_INI";
    public static final String DTJ_FAIXA_VALOR_CONTRATO_FIM = TB_DEFINICAO_TAXA_JUROS + ".DTJ_FAIXA_VALOR_CONTRATO_FIM";
    public static final String DTJ_FAIXA_PRAZO_INI          = TB_DEFINICAO_TAXA_JUROS + ".DTJ_FAIXA_PRAZO_INI";
    public static final String DTJ_FAIXA_PRAZO_FIM          = TB_DEFINICAO_TAXA_JUROS + ".DTJ_FAIXA_PRAZO_FIM";
    public static final String DTJ_TAXA_JUROS               = TB_DEFINICAO_TAXA_JUROS + ".DTJ_TAXA_JUROS";
    public static final String DTJ_TAXA_JUROS_MINIMA        = TB_DEFINICAO_TAXA_JUROS + ".DTJ_TAXA_JUROS_MINIMA";
    public static final String DTJ_DATA_VIGENCIA_INI        = TB_DEFINICAO_TAXA_JUROS + ".DTJ_DATA_VIGENCIA_INI";
    public static final String DTJ_DATA_VIGENCIA_FIM        = TB_DEFINICAO_TAXA_JUROS + ".DTJ_DATA_VIGENCIA_FIM";
    public static final String DTJ_DATA_CADASTRO            = TB_DEFINICAO_TAXA_JUROS + ".DTJ_DATA_CADASTRO";

    public static final String TB_ENDERECO_CONSIGNATARIA           = "tb_endereco_consignataria";
    public static final String ENC_CODIGO                          =  TB_ENDERECO_CONSIGNATARIA + ".enc_codigo";
    public static final String ENC_CSA_CODIGO                      =  TB_ENDERECO_CONSIGNATARIA + ".csa_codigo";
    public static final String ENC_TIE_CODIGO                      =  TB_ENDERECO_CONSIGNATARIA + ".tie_codigo";
    public static final String ENC_LOGRADOURO                      =  TB_ENDERECO_CONSIGNATARIA + ".enc_logradouro";
    public static final String ENC_NUMERO                          =  TB_ENDERECO_CONSIGNATARIA + ".enc_numero";
    public static final String ENC_COMPLEMENTO                     =  TB_ENDERECO_CONSIGNATARIA + ".enc_complemento";
    public static final String ENC_BAIRRO                          =  TB_ENDERECO_CONSIGNATARIA + ".enc_bairro";
    public static final String ENC_MUNICIPIO                       =  TB_ENDERECO_CONSIGNATARIA + ".enc_municipio";
    public static final String ENC_UF                              =  TB_ENDERECO_CONSIGNATARIA + ".enc_uf";
    public static final String ENC_CEP                             =  TB_ENDERECO_CONSIGNATARIA + ".enc_cep";
    public static final String ENC_LATITUDE                        =  TB_ENDERECO_CONSIGNATARIA + ".enc_latitude";
    public static final String ENC_LONGITUDE                       =  TB_ENDERECO_CONSIGNATARIA + ".enc_longitude";

    public static final String TB_ENDERECO_CORRESPONDENTE          = "tb_endereco_correspondente";
    public static final String ECR_CODIGO                          =  TB_ENDERECO_CORRESPONDENTE + ".ecr_codigo";
    public static final String ECR_COR_CODIGO                      =  TB_ENDERECO_CORRESPONDENTE + ".cor_codigo";
    public static final String ECR_TIE_CODIGO                      =  TB_ENDERECO_CORRESPONDENTE + ".tie_codigo";
    public static final String ECR_LOGRADOURO                      =  TB_ENDERECO_CORRESPONDENTE + ".ecr_logradouro";
    public static final String ECR_NUMERO                          =  TB_ENDERECO_CORRESPONDENTE + ".ecr_numero";
    public static final String ECR_COMPLEMENTO                     =  TB_ENDERECO_CORRESPONDENTE + ".ecr_complemento";
    public static final String ECR_BAIRRO                          =  TB_ENDERECO_CORRESPONDENTE + ".ecr_bairro";
    public static final String ECR_MUNICIPIO                       =  TB_ENDERECO_CORRESPONDENTE + ".ecr_municipio";
    public static final String ECR_UF                              =  TB_ENDERECO_CORRESPONDENTE + ".ecr_uf";
    public static final String ECR_CEP                             =  TB_ENDERECO_CORRESPONDENTE + ".ecr_cep";
    public static final String ECR_LATITUDE                        =  TB_ENDERECO_CORRESPONDENTE + ".ecr_latitude";
    public static final String ECR_LONGITUDE                       =  TB_ENDERECO_CORRESPONDENTE + ".ecr_longitude";

    public static final String TB_MODALIDADE_BENEFICIO             = "tb_modalidade_beneficio";
    public static final String MBE_CODIGO                          =  TB_MODALIDADE_BENEFICIO + ".mbe_codigo";
    public static final String MBE_DESCRICAO                       =  TB_MODALIDADE_BENEFICIO + ".mbe_descricao";

    public static final String TB_PALAVRA_CHAVE                    = "tb_palavra_chave";
    public static final String PCH_CODIGO                          =  TB_PALAVRA_CHAVE + ".pch_codigo";
    public static final String PCH_PALAVRA                         =  TB_PALAVRA_CHAVE + ".pch_palavra";

    public static final String TB_PALAVRA_CHAVE_BENEFICIO          = "tb_palavra_chave_beneficio";
    public static final String PB_BEN_CODIGO                       =  TB_PALAVRA_CHAVE_BENEFICIO + ".ben_codigo";
    public static final String PB_PCH_CODIGO                       =  TB_PALAVRA_CHAVE_BENEFICIO + ".pch_codigo";

    public static final String TB_PROVEDOR_BENEFICIO               = "tb_provedor_beneficio";
    public static final String PRO_CODIGO                          =  TB_PROVEDOR_BENEFICIO + ".pro_codigo";
    public static final String PRO_CSA_CODIGO                      =  TB_PROVEDOR_BENEFICIO + ".csa_codigo";
    public static final String PRO_NSE_CODIGO                      =  TB_PROVEDOR_BENEFICIO + ".nse_codigo";
    public static final String PRO_TITULO_DETALHE_TOPO             =  TB_PROVEDOR_BENEFICIO + ".pro_titulo_detalhe_topo";
    public static final String PRO_TEXTO_DETALHE_TOPO              =  TB_PROVEDOR_BENEFICIO + ".pro_titulo_detalhe_rodape";
    public static final String PRO_TITULO_DETALHE_RODAPE           =  TB_PROVEDOR_BENEFICIO + ".pro_texto_detalhe_topo";
    public static final String PRO_TEXTO_DETALHE_RODAPE            =  TB_PROVEDOR_BENEFICIO + ".pro_texto_detalhe_rodape";
    public static final String PRO_TITULO_LISTA_BENEFICIO          =  TB_PROVEDOR_BENEFICIO + ".pro_titulo_lista_beneficio";
    public static final String PRO_TEXTO_CARD_BENEFICIO            =  TB_PROVEDOR_BENEFICIO + ".pro_texto_card_beneficio";
    public static final String PRO_LINK_BENEFICIO                  =  TB_PROVEDOR_BENEFICIO + ".pro_link_beneficio";
    public static final String PRO_IMAGEM_BENEFICIO                =  TB_PROVEDOR_BENEFICIO + ".pro_imagem_beneficio";
    public static final String PRO_COR_CODIGO                      =  TB_PROVEDOR_BENEFICIO + ".cor_codigo";
    public static final String PRO_AGRUPA                          =  TB_PROVEDOR_BENEFICIO + ".pro_agrupa";

    public static final String TB_BLOCO_PROCESSAMENTO = "tb_bloco_processamento";
    public static final String BPR_CODIGO             = TB_BLOCO_PROCESSAMENTO + ".bpr_codigo";
    public static final String BPR_TBP_CODIGO         = TB_BLOCO_PROCESSAMENTO + ".tbp_codigo";
    public static final String BPR_SBP_CODIGO         = TB_BLOCO_PROCESSAMENTO + ".sbp_codigo";
    public static final String BPR_CNV_CODIGO         = TB_BLOCO_PROCESSAMENTO + ".cnv_codigo";
    public static final String BPR_EST_CODIGO         = TB_BLOCO_PROCESSAMENTO + ".est_codigo";
    public static final String BPR_ORG_CODIGO         = TB_BLOCO_PROCESSAMENTO + ".org_codigo";
    public static final String BPR_RSE_CODIGO         = TB_BLOCO_PROCESSAMENTO + ".rse_codigo";
    public static final String BPR_PERIODO            = TB_BLOCO_PROCESSAMENTO + ".bpr_periodo";
    public static final String BPR_DATA_INCLUSAO      = TB_BLOCO_PROCESSAMENTO + ".bpr_data_inclusao";
    public static final String BPR_DATA_PROCESSAMENTO = TB_BLOCO_PROCESSAMENTO + ".bpr_data_processamento";
    public static final String BPR_ORDEM_EXECUCAO     = TB_BLOCO_PROCESSAMENTO + ".bpr_ordem_execucao";
    public static final String BPR_MENSAGEM           = TB_BLOCO_PROCESSAMENTO + ".bpr_mensagem";
    public static final String BPR_LINHA              = TB_BLOCO_PROCESSAMENTO + ".bpr_linha";
    public static final String BPR_NUM_LINHA          = TB_BLOCO_PROCESSAMENTO + ".bpr_num_linha";
    public static final String BPR_CAMPOS             = TB_BLOCO_PROCESSAMENTO + ".bpr_campos";
    public static final String BPR_CNV_COD_VERBA      = TB_BLOCO_PROCESSAMENTO + ".cnv_cod_verba";
    public static final String BPR_SVC_IDENTIFICADOR  = TB_BLOCO_PROCESSAMENTO + ".svc_identificador";
    public static final String BPR_CSA_IDENTIFICADOR  = TB_BLOCO_PROCESSAMENTO + ".csa_identificador";
    public static final String BPR_EST_IDENTIFICADOR  = TB_BLOCO_PROCESSAMENTO + ".est_identificador";
    public static final String BPR_ORG_IDENTIFICADOR  = TB_BLOCO_PROCESSAMENTO + ".org_identificador";
    public static final String BPR_RSE_MATRICULA      = TB_BLOCO_PROCESSAMENTO + ".rse_matricula";
    public static final String BPR_SER_CPF            = TB_BLOCO_PROCESSAMENTO + ".ser_cpf";
    public static final String BPR_ADE_NUMERO         = TB_BLOCO_PROCESSAMENTO + ".ade_numero";
    public static final String BPR_ADE_INDICE         = TB_BLOCO_PROCESSAMENTO + ".ade_indice";

    public static final String TB_BLOCO_PROCESSAMENTO_LOTE = "tb_bloco_processamento_lote";
    public static final String BPL_CPL_ARQUIVO_ECONSIG     = TB_BLOCO_PROCESSAMENTO_LOTE + ".cpl_arquivo_econsig";
    public static final String BPL_NUM_LINHA               = TB_BLOCO_PROCESSAMENTO_LOTE + ".bpl_num_linha";
    public static final String BPL_CSA_CODIGO              = TB_BLOCO_PROCESSAMENTO_LOTE + ".csa_codigo";
    public static final String BPL_SBP_CODIGO              = TB_BLOCO_PROCESSAMENTO_LOTE + ".sbp_codigo";
    public static final String BPL_PERIODO                 = TB_BLOCO_PROCESSAMENTO_LOTE + ".bpl_periodo";
    public static final String BPL_DATA_INCLUSAO           = TB_BLOCO_PROCESSAMENTO_LOTE + ".bpl_data_inclusao";
    public static final String BPL_DATA_PROCESSAMENTO      = TB_BLOCO_PROCESSAMENTO_LOTE + ".bpl_data_processamento";
    public static final String BPL_LINHA                   = TB_BLOCO_PROCESSAMENTO_LOTE + ".bpl_linha";
    public static final String BPL_CAMPOS                  = TB_BLOCO_PROCESSAMENTO_LOTE + ".bpl_campos";
    public static final String BPL_CRITICA                 = TB_BLOCO_PROCESSAMENTO_LOTE + ".bpl_critica";

    public static final String TB_CONTROLE_PROCESSAMENTO_LOTE = "tb_controle_processamento_lote";
    public static final String CPL_ARQUIVO_ECONSIG            = TB_CONTROLE_PROCESSAMENTO_LOTE + ".cpl_arquivo_econsig";
    public static final String CPL_ARQUIVO_CENTRALIZADOR      = TB_CONTROLE_PROCESSAMENTO_LOTE + ".cpl_arquivo_centralizador";
    public static final String CPL_ARQUIVO_CRITICA            = TB_CONTROLE_PROCESSAMENTO_LOTE + ".cpl_arquivo_critica";
    public static final String CPL_STATUS                     = TB_CONTROLE_PROCESSAMENTO_LOTE + ".cpl_status";
    public static final String CPL_CANAL                      = TB_CONTROLE_PROCESSAMENTO_LOTE + ".cpl_canal";
    public static final String CPL_DATA                       = TB_CONTROLE_PROCESSAMENTO_LOTE + ".cpl_data";
    public static final String CPL_PARAMETROS                 = TB_CONTROLE_PROCESSAMENTO_LOTE + ".cpl_parametros";
    public static final String CPL_USU_CODIGO                 = TB_CONTROLE_PROCESSAMENTO_LOTE + ".usu_codigo";

    public static final String TB_STATUS_BLOCO_PROCESSAMENTO = "tb_status_bloco_processamento";
    public static final String SBP_CODIGO                    = TB_STATUS_BLOCO_PROCESSAMENTO + ".sbp_codigo";
    public static final String SBP_DESCRICAO                 = TB_STATUS_BLOCO_PROCESSAMENTO + ".sbp_descricao";

    public static final String TB_TIPO_BLOCO_PROCESSAMENTO   = "tb_tipo_bloco_processamento";
    public static final String TBP_CODIGO                    = TB_TIPO_BLOCO_PROCESSAMENTO + ".tbp_codigo";
    public static final String TBP_DESCRICAO                 = TB_TIPO_BLOCO_PROCESSAMENTO + ".tbp_descricao";

    public static final String TB_FATURAMENTO_BENEFICIO_NF = "tb_faturamento_beneficio_nf";
    public static final String FNF_CODIGO = TB_FATURAMENTO_BENEFICIO_NF + ".FNF_CODIGO";
    public static final String FNF_FAT_CODIGO = TB_FATURAMENTO_BENEFICIO_NF + ".FAT_CODIGO";
    public static final String FNF_TNF_CODIGO = TB_FATURAMENTO_BENEFICIO_NF + ".TNF_CODIGO";
    public static final String FNF_CODIGO_CONTRATO = TB_FATURAMENTO_BENEFICIO_NF + ".FNF_CODIGO_CONTRATO";
    public static final String FNF_NUMERO_NF = TB_FATURAMENTO_BENEFICIO_NF + ".FNF_NUMERO_NF";
    public static final String FNF_NUMERO_TITULO = TB_FATURAMENTO_BENEFICIO_NF + ".FNF_NUMERO_TITULO";
    public static final String FNF_VALOR_BRUTO = TB_FATURAMENTO_BENEFICIO_NF + ".FNF_VALOR_BRUTO";
    public static final String FNF_VALOR_ISS = TB_FATURAMENTO_BENEFICIO_NF + ".FNF_VALOR_ISS";
    public static final String FNF_VALOR_IR = TB_FATURAMENTO_BENEFICIO_NF + ".FNF_VALOR_IR";
    public static final String FNF_VALOR_PIS_COFINS = TB_FATURAMENTO_BENEFICIO_NF + ".FNF_VALOR_PIS_COFINS";
    public static final String FNF_VALOR_LIQUIDO = TB_FATURAMENTO_BENEFICIO_NF + ".FNF_VALOR_LIQUIDO";
    public static final String FNF_DATA_GERACAO = TB_FATURAMENTO_BENEFICIO_NF + ".FNF_DATA_GERACAO";
    public static final String FNF_DATA_VENCIMENTO = TB_FATURAMENTO_BENEFICIO_NF + ".FNF_DATA_VENCIMENTO";

    public static final String TB_TIPO_NOTA_FISCAL = "tb_tipo_nota_fiscal";
    public static final String TNF_CODIGO = TB_TIPO_NOTA_FISCAL + ".TNF_CODIGO";
    public static final String TNF_DESCRICAO = TB_TIPO_NOTA_FISCAL + ".TNF_DESCRICAO";

    public static final String TB_OCORRENCIA_PERFIL             = "tb_ocorrencia_perfil";
    public static final String OPR_CODIGO                       = TB_OCORRENCIA_USUARIO + ".opr_codigo";
    public static final String OPR_PER_CODIGO                   = TB_OCORRENCIA_USUARIO + ".per_codigo";
    public static final String OPR_USU_CODIGO                   = TB_OCORRENCIA_USUARIO + ".usu_codigo";
    public static final String OPR_TOC_CODIGO                   = TB_OCORRENCIA_USUARIO + ".toc_codigo";
    public static final String OPR_TMO_CODIGO                   = TB_OCORRENCIA_USUARIO + ".tmo_codigo";
    public static final String OPR_DATA                         = TB_OCORRENCIA_USUARIO + ".opr_data";
    public static final String OPR_OBS                          = TB_OCORRENCIA_USUARIO + ".opr_obs";
    public static final String OPR_IP_ACESSO                    = TB_OCORRENCIA_USUARIO + ".opr_ip_acesso";

    public static final String TB_AVALIACAO_FAQ                 = "tb_avaliacao_faq";
    public static final String AVF_CODIGO                       = TB_AVALIACAO_FAQ + ".avf_codigo";
    public static final String AVF_USU_CODIGO                   = TB_AVALIACAO_FAQ + ".usu_codigo";
    public static final String AVF_FAQ_CODIGO                   = TB_AVALIACAO_FAQ + ".faq_codigo";
    public static final String AVF_NOTA                         = TB_AVALIACAO_FAQ + ".avf_nota";
    public static final String AVF_DATA                         = TB_AVALIACAO_FAQ + ".avf_data";
    public static final String AVF_COMENTARIO                   = TB_AVALIACAO_FAQ + ".avf_comentario";

    public static final String TB_BANNER_PUBLICIDADE            = "tb_banner_publicidade";
    public static final String BPU_CODIGO                       = TB_BANNER_PUBLICIDADE + ".bpu_codigo";
    public static final String BPU_ARQ_CODIGO                   = TB_BANNER_PUBLICIDADE + ".arq_codigo";
    public static final String BPU_NSE_CODIGO                   = TB_BANNER_PUBLICIDADE + ".nse_codigo";
    public static final String BPU_DESCRICAO                    = TB_BANNER_PUBLICIDADE + ".nse_descricao";
    public static final String BPU_URL_SAIDA                    = TB_BANNER_PUBLICIDADE + ".bpu_url_saida";
    public static final String BPU_ORDEM                        = TB_BANNER_PUBLICIDADE + ".bpu_ordem";
    public static final String BPU_EXIBE_MOBILE                 = TB_BANNER_PUBLICIDADE + ".bpu_exibe_mobile";
    public static final String BPU_DATA                         = TB_BANNER_PUBLICIDADE + ".bpu_data";

    public static final String TB_ARQUIVO_RSE               = "tb_arquivo_rse";
    public static final String ARS_RSE_CODIGO               = TB_ARQUIVO_RSE + ".rse_codigo";
    public static final String ARS_ARQ_CODIGO               = TB_ARQUIVO_RSE + ".arq_codigo";
    public static final String ARS_USU_CODIGO               = TB_ARQUIVO_RSE + ".usu_codigo";
    public static final String ARS_DATA_CRIACAO             = TB_ARQUIVO_RSE + ".ars_data_criacao";
    public static final String ARS_NOME                     = TB_ARQUIVO_RSE + ".ars_nome";
    public static final String ARS_IP_ACESSO                = TB_ARQUIVO_RSE + ".ars_ip_acesso";

    public static final String TB_ARQUIVO_MENSAGEM          = "tb_arquivo_mensagem";
    public static final String AMN_MEN_CODIGO               = TB_ARQUIVO_MENSAGEM + ".men_codigo";
    public static final String AMN_ARQ_CODIGO               = TB_ARQUIVO_MENSAGEM + ".arq_codigo";
    public static final String AMN_USU_CODIGO               = TB_ARQUIVO_MENSAGEM + ".usu_codigo";
    public static final String AMN_DATA_CRIACAO             = TB_ARQUIVO_MENSAGEM + ".amn_data_criacao";
    public static final String AMN_NOME                     = TB_ARQUIVO_MENSAGEM + ".amn_nome";
    public static final String AMN_IP_ACESSO                = TB_ARQUIVO_MENSAGEM + ".amn_ip_acesso";

    public static final String TB_CATEGORIA_FAQ             = "tb_categoria_faq";
    public static final String CAF_CODIGO                   = TB_CATEGORIA_FAQ + ".caf_codigo";
    public static final String CAF_DESCRICAO                = TB_CATEGORIA_FAQ + ".caf_descricao";

    public static final String TB_HISTORICO_LOGIN 			= "tb_historico_login";
    public static final String HLO_CODIGO 					= TB_HISTORICO_LOGIN + ".hlo_codigo";
    public static final String HLO_USU_CODIGO 				= TB_HISTORICO_LOGIN + ".usu_codigo";
    public static final String HLO_DATA 					= TB_HISTORICO_LOGIN + ".hlo_data";
    public static final String HLO_CANAL 					= TB_HISTORICO_LOGIN + ".hlo_canal";

    public static final String TB_ORIGEM_SOLICITACAO        = "tb_origem_solicitacao";
    public static final String OSO_CODIGO                   = TB_ORIGEM_SOLICITACAO + ".oso_codigo";
    public static final String OSO_DESCRICAO                = TB_ORIGEM_SOLICITACAO + ".oso_descricao";

    public static final String TB_CAMPO_USUARIO				= "tb_campo_usuario";
    public static final String CAU_USU_CODIGO				= TB_CAMPO_USUARIO + ".usu_codigo";
    public static final String CAU_CHAVE					= TB_CAMPO_USUARIO + ".cau_chave";
    public static final String CAU_VALOR					= TB_CAMPO_USUARIO + ".cau_valor";

    public static final String TB_CREDENCIAMENTO_CSA        = "tb_credenciamento_csa";
    public static final String CRE_CODIGO                   = TB_CREDENCIAMENTO_CSA + ".cre_codigo";
    public static final String CRE_CSA_CODIGO               = TB_CREDENCIAMENTO_CSA + ".csa_codigo";
    public static final String CRE_SCR_CODIGO               = TB_CREDENCIAMENTO_CSA + ".scr_codigo";
    public static final String CRE_DATA_INI                 = TB_CREDENCIAMENTO_CSA + ".cre_data_ini";
    public static final String CRE_DATA_FIM                 = TB_CREDENCIAMENTO_CSA + ".cre_data_fim";

    public static final String TB_STATUS_CREDENCIAMENTO     = "tb_status_credenciamento";
    public static final String SCR_CODIGO                   = TB_ORIGEM_SOLICITACAO + ".scr_codigo";
    public static final String SCR_DESCRICAO                = TB_ORIGEM_SOLICITACAO + ".scr_descricao";

    public static final String TB_LIMITE_MARGEM_CSA_ORG     = "tb_limite_margem_csa_org";
    public static final String LMC_MAR_CODIGO               = TB_LIMITE_MARGEM_CSA_ORG + ".mar_codigo";
    public static final String LMC_CSA_CODIGO               = TB_LIMITE_MARGEM_CSA_ORG + ".csa_codigo";
    public static final String LMC_ORG_CODIGO               = TB_LIMITE_MARGEM_CSA_ORG + ".org_codigo";
    public static final String LMC_VALOR                    = TB_LIMITE_MARGEM_CSA_ORG + ".lmc_valor";
    public static final String LMC_DATA                     = TB_LIMITE_MARGEM_CSA_ORG + ".lmc_data";

    public static final String TB_DESTINATARIO_EMAIL        = "tb_destinatario_email";
    public static final String DTE_FUN_CODIGO               = TB_DESTINATARIO_EMAIL + ".fun_codigo";
    public static final String DTE_PAP_CODIGO_OPERADOR      = TB_DESTINATARIO_EMAIL + ".pap_codigo_operador";
    public static final String DTE_PAP_CODIGO_DESTINATARIO  = TB_DESTINATARIO_EMAIL + ".pap_codigo_destinatario";

    public static final String TB_DESTINATARIO_EMAIL_CSA    = "tb_destinatario_email_csa";
    public static final String DEM_FUN_CODIGO               = TB_DESTINATARIO_EMAIL_CSA + ".fun_codigo";
    public static final String DEM_PAP_CODIGO               = TB_DESTINATARIO_EMAIL_CSA + ".pap_codigo";
    public static final String DEM_CSA_CODIGO               = TB_DESTINATARIO_EMAIL_CSA + ".csa_codigo";
    public static final String DEM_RECEBER                  = TB_DESTINATARIO_EMAIL_CSA + ".dem_receber";
    public static final String DEM_EMAIL                    = TB_DESTINATARIO_EMAIL_CSA + ".dem_email";

    public static final String TB_DESTINATARIO_EMAIL_CSA_SVC = "tb_destinatario_email_csa_svc";
    public static final String DCS_FUN_CODIGO                = TB_DESTINATARIO_EMAIL_CSA_SVC + ".fun_codigo";
    public static final String DCS_PAP_CODIGO                = TB_DESTINATARIO_EMAIL_CSA_SVC + ".pap_codigo";
    public static final String DCS_CSA_CODIGO                = TB_DESTINATARIO_EMAIL_CSA_SVC + ".csa_codigo";
    public static final String DCS_SVC_CODIGO                = TB_DESTINATARIO_EMAIL_CSA_SVC + ".svc_codigo";

    public static final String TB_DESTINATARIO_EMAIL_SER    = "tb_destinatario_email_ser";
    public static final String DES_FUN_CODIGO               = TB_DESTINATARIO_EMAIL_SER + ".fun_codigo";
    public static final String DES_PAP_CODIGO               = TB_DESTINATARIO_EMAIL_SER + ".pap_codigo";
    public static final String DES_CSA_CODIGO               = TB_DESTINATARIO_EMAIL_SER + ".ser_codigo";
    public static final String DES_RECEBER                  = TB_DESTINATARIO_EMAIL_SER + ".des_receber";

    public static final String TB_ANEXO_CONSIGNATARIA       = "tb_anexo_consignataria";
    public static final String AXC_AXC_CODIGO               = TB_ANEXO_CONSIGNATARIA + ".axc_codigo";
    public static final String AXC_CSA_CODIGO               = TB_ANEXO_CONSIGNATARIA + ".csa_codigo";
    public static final String AXC_TAR_CODIGO               = TB_ANEXO_CONSIGNATARIA + ".tar_codigo";
    public static final String AXC_USU_CODIGO               = TB_ANEXO_CONSIGNATARIA + ".usu_codigo";
    public static final String AXC_AXC_NOME                 = TB_ANEXO_CONSIGNATARIA + ".axc_nome";
    public static final String AXC_AXC_ATIVO                = TB_ANEXO_CONSIGNATARIA + ".axc_ativo";
    public static final String AXC_AXC_DATA                 = TB_ANEXO_CONSIGNATARIA + ".axc_data";
    public static final String AXC_AXC_IP_ACESSO            = TB_ANEXO_CONSIGNATARIA + ".axc_ip_acesso";

    public static final String TB_INFORMACAO_CSA_SERVIDOR   = "tb_informacao_csa_servidor";
    public static final String ICS_CODIGO                   = TB_INFORMACAO_CSA_SERVIDOR + ".ics_codigo";
    public static final String ICS_VALOR                    = TB_INFORMACAO_CSA_SERVIDOR + ".ics_valor";
    public static final String ICS_DATA                     = TB_INFORMACAO_CSA_SERVIDOR + ".ics_data";
    public static final String ICS_USU_CODIGO               = TB_INFORMACAO_CSA_SERVIDOR + ".usu_codigo";
    public static final String ICS_SER_CODIGO               = TB_INFORMACAO_CSA_SERVIDOR + ".ser_codigo";
    public static final String ICS_CSA_CODIGO               = TB_INFORMACAO_CSA_SERVIDOR + ".csa_codigo";
    public static final String ICS_IP_ACESSO                = TB_INFORMACAO_CSA_SERVIDOR + ".ics_ip_acesso";

    public static final String TB_DESTINATARIO_EMAIL_CSE    = "tb_destinatario_email_cse";
    public static final String DEE_FUN_CODIGO               = TB_DESTINATARIO_EMAIL_CSE + ".fun_codigo";
    public static final String DEE_PAP_CODIGO               = TB_DESTINATARIO_EMAIL_CSE + ".pap_codigo";
    public static final String DEE_CSE_CODIGO               = TB_DESTINATARIO_EMAIL_CSE + ".cse_codigo";
    public static final String DEE_RECEBER                  = TB_DESTINATARIO_EMAIL_CSE + ".dee_receber";
    public static final String DEE_EMAIL                    = TB_DESTINATARIO_EMAIL_CSE + ".dee_email";

    public static final String TB_SALDO_DEVEDOR_SERVIDOR    = "tb_saldo_devedor_rse";
    public static final String SDR_RSE_CODIGO               = TB_SALDO_DEVEDOR_SERVIDOR + ".rse_codigo";
    public static final String SDR_VALOR                    = TB_SALDO_DEVEDOR_SERVIDOR + ".sdr_valor";
    public static final String SDR_CSA_CODIGO               = TB_SALDO_DEVEDOR_SERVIDOR + ".csa_codigo";
    public static final String SDR_DATA                     = TB_SALDO_DEVEDOR_SERVIDOR + ".sdr_data";

    public static final String TB_TIPO_PARAM_PONTUACAO = "tb_tipo_param_pontuacao";
    public static final String TPO_CODIGO              = TB_TIPO_PARAM_PONTUACAO + ".tpo_codigo";
    public static final String TPO_DESCRICAO           = TB_TIPO_PARAM_PONTUACAO + ".tpo_descricao";

    public static final String TB_PARAM_PONTUACAO_RSE = "tb_param_pontuacao_rse";
    public static final String PPO_CODIGO             = TB_PARAM_PONTUACAO_RSE + ".ppo_codigo";
    public static final String PPO_TPO_CODIGO         = TB_PARAM_PONTUACAO_RSE + ".tpo_codigo";
    public static final String PPO_PONTUACAO          = TB_PARAM_PONTUACAO_RSE + ".ppo_pontuacao";
    public static final String PPO_LIM_INFERIOR       = TB_PARAM_PONTUACAO_RSE + ".ppo_lim_inferior";
    public static final String PPO_LIM_SUPERIOR       = TB_PARAM_PONTUACAO_RSE + ".ppo_lim_superior";

    public static final String TB_PARAM_PONTUACAO_RSE_CSA = "tb_param_pontuacao_rse_csa";
    public static final String PPR_CODIGO                 = TB_PARAM_PONTUACAO_RSE_CSA + ".ppr_codigo";
    public static final String PPR_CSA_CODIGO             = TB_PARAM_PONTUACAO_RSE_CSA + ".csa_codigo";
    public static final String PPR_TPO_CODIGO             = TB_PARAM_PONTUACAO_RSE_CSA + ".tpo_codigo";
    public static final String PPR_NSE_CODIGO             = TB_PARAM_PONTUACAO_RSE_CSA + ".nse_codigo";
    public static final String PPR_PONTUACAO              = TB_PARAM_PONTUACAO_RSE_CSA + ".ppr_pontuacao";
    public static final String PPR_LIM_INFERIOR           = TB_PARAM_PONTUACAO_RSE_CSA + ".ppr_lim_inferior";
    public static final String PPR_LIM_SUPERIOR           = TB_PARAM_PONTUACAO_RSE_CSA + ".ppr_lim_superior";

    public static final String TB_PONTUACAO_RSE_CSA = "tb_pontuacao_rse_csa";
    public static final String PON_CSA_CODIGO       = TB_PONTUACAO_RSE_CSA + ".csa_codigo";
    public static final String PON_RSE_CODIGO       = TB_PONTUACAO_RSE_CSA + ".rse_codigo";
    public static final String PON_VLR              = TB_PONTUACAO_RSE_CSA + ".pon_vlr";
    public static final String PON_DATA             = TB_PONTUACAO_RSE_CSA + ".pon_data";

    public static final String TB_REGRA_CONVENIO = "tb_regra_convenio";
    public static final String RCO_CODIGO         = TB_REGRA_CONVENIO + ".rco_codigo";
    public static final String RCO_CAMPO_CODIGO   = TB_REGRA_CONVENIO + ".rco_campo_codigo";
    public static final String RCO_CAMPO_NOME     = TB_REGRA_CONVENIO + ".rco_campo_nome";
    public static final String RCO_CAMPO_VALOR    = TB_REGRA_CONVENIO + ".rco_campo_valor";
    public static final String RCO_CSA_CODIGO     = TB_REGRA_CONVENIO + ".csa_codigo";
    public static final String RCO_SVC_CODIGO     = TB_REGRA_CONVENIO + ".svc_codigo";
    public static final String RCO_ORG_CODIGO     = TB_REGRA_CONVENIO + ".org_codigo";
    public static final String RCO_MAR_CODIGO     = TB_REGRA_CONVENIO + ".mar_codigo";

    public static final String TB_PERFIL_CONSIGNADO_CSA = "tb_perfil_consignado_csa";
    public static final String PCC_CODIGO               = TB_PERFIL_CONSIGNADO_CSA + ".pcc_codigo";
    public static final String PCC_CSA_CODIGO           = TB_PERFIL_CONSIGNADO_CSA + ".csa_codigo";
    public static final String PCC_PONTUACAO_INFERIOR   = TB_PERFIL_CONSIGNADO_CSA + ".pcc_pontuacao_inferior";
    public static final String PCC_PONTUACAO_SUPERIOR   = TB_PERFIL_CONSIGNADO_CSA + ".pcc_pontuacao_superior";
    public static final String PCC_PERFIL               = TB_PERFIL_CONSIGNADO_CSA + ".pcc_perfil";

    public static final String TB_CONTROLE_DOCUMENTO_MARGEM = "tb_controle_documento_margem";
    public static final String CDM_CODIGO                   = TB_CONTROLE_DOCUMENTO_MARGEM + ".cdm_codigo";
    public static final String CDM_RSECODIGO                = TB_CONTROLE_DOCUMENTO_MARGEM + ".rse_codigo";
    public static final String CDM_LOCAL_ARQUIVO            = TB_CONTROLE_DOCUMENTO_MARGEM + ".cdm_local_arquivo";
    public static final String CDM_CODIGO_AUTH              = TB_CONTROLE_DOCUMENTO_MARGEM + ".cdm_codigo_auth";
    public static final String CDM_DATA                     = TB_CONTROLE_DOCUMENTO_MARGEM + ".cdm_data";

    public static final String TB_MODELO_TERMO_ADITIVO  = "tb_modelo_termo_aditivo";
    public static final String MTA_CODIGO               = TB_MODELO_TERMO_ADITIVO + ".mta_codigo";
    public static final String MTA_DESCRICAO            = TB_MODELO_TERMO_ADITIVO + ".mta_descricao";
    public static final String MTA_TEXTO                = TB_MODELO_TERMO_ADITIVO + ".mta_texto";

    public static final String TB_MODELO_TERMO_TAG  = "tb_modelo_termo_tag";
    public static final String MTT_CODIGO               = TB_MODELO_TERMO_TAG + ".mtt_codigo";
    public static final String MTA_MTT_CODIGO           = TB_MODELO_TERMO_TAG + ".mta_codigo";
    public static final String MTT_TAG                  = TB_MODELO_TERMO_TAG + ".mtt_tag";
    public static final String MTT_VALOR                = TB_MODELO_TERMO_TAG + ".mtt_valor";

    public static final String TB_CREDITO_TRABALHADOR = "tb_credito_trabalhador";
    public static final String CRT_CODIGO = TB_CREDITO_TRABALHADOR + ".crt_codigo";
    public static final String CRT_IF_CONCESSORA_COD = TB_CREDITO_TRABALHADOR + ".crt_if_concessora_cod";
    public static final String CRT_IF_CONCESSORA_DESC = TB_CREDITO_TRABALHADOR + ".crt_if_concessora_desc";
    public static final String CRT_CONTRATO = TB_CREDITO_TRABALHADOR + ".crt_contrato";
    public static final String CRT_CPF = TB_CREDITO_TRABALHADOR + ".crt_cpf";
    public static final String CRT_MATRICULA = TB_CREDITO_TRABALHADOR + ".crt_matricula";
    public static final String CRT_INSC_EMPREGADOR_COD = TB_CREDITO_TRABALHADOR + ".crt_insc_empregador_cod";
    public static final String CRT_INSC_EMPREGADOR_DESC = TB_CREDITO_TRABALHADOR + ".crt_insc_empregador_desc";
    public static final String CRT_NUM_INSC_EMPREGADOR = TB_CREDITO_TRABALHADOR + ".crt_num_insc_empregador";
    public static final String CRT_NOME_TRABALHADOR = TB_CREDITO_TRABALHADOR + ".crt_nome_trabalhador";
    public static final String CRT_NOME_EMPREGADOR = TB_CREDITO_TRABALHADOR + ".crt_nome_empregador";
    public static final String CRT_DATA_INICIO_CONTRATO = TB_CREDITO_TRABALHADOR + ".crt_data_inicio_contrato";
    public static final String CRT_DATA_FIM_CONTRATO = TB_CREDITO_TRABALHADOR + ".crt_data_fim_contrato";
    public static final String CRT_COMPETENCIA_INI_DESCONTO = TB_CREDITO_TRABALHADOR + ".crt_competencia_ini_desconto";
    public static final String CRT_COMPETENCIA_FIM_DESCONTO = TB_CREDITO_TRABALHADOR + ".crt_competencia_fim_desconto";
    public static final String CRT_TOTAL_PARCELAS = TB_CREDITO_TRABALHADOR + ".crt_total_parcelas";
    public static final String CRT_VALOR_PARCELA = TB_CREDITO_TRABALHADOR + ".crt_valor_parcela";
    public static final String CRT_VALOR_EMPRESTIMO = TB_CREDITO_TRABALHADOR + ".crt_valor_emprestimo";
    public static final String CRT_VALOR_LIBERADO = TB_CREDITO_TRABALHADOR + ".crt_valor_liberador";
    public static final String CRT_QTD_PAGAMENTOS = TB_CREDITO_TRABALHADOR + ".crt_qtd_pagamentos";
    public static final String CRT_QTD_ESCRITURACOES = TB_CREDITO_TRABALHADOR + ".crt_qtd_escrituracoes";
    public static final String CRT_CATEGORIA_TRABALHADOR_COD = TB_CREDITO_TRABALHADOR + ".crt_categoria_trabalhador_cod";
    public static final String CRT_CATEGORIA_TRABALHADOR_DESC = TB_CREDITO_TRABALHADOR + ".crt_categoria_trabalhador_desc";
    public static final String CRT_COMPETENCIA = TB_CREDITO_TRABALHADOR + ".crt_competencia";
    public static final String CRT_INSC_ESTABELECIMENTO_COD = TB_CREDITO_TRABALHADOR + ".crt_insc_estabelecimento_cod";
    public static final String CRT_INSC_ESTABELECIMENTO_DESC = TB_CREDITO_TRABALHADOR + ".crt_insc_estabelecimento_desc";
    public static final String CRT_NUM_INSC_ESTABELECIMENTO = TB_CREDITO_TRABALHADOR + ".crt_num_insc_estabelecimento";
    public static final String CRT_DATA_ADMISSAO = TB_CREDITO_TRABALHADOR + ".crt_data_admissao";
    public static final String CRT_OBS = TB_CREDITO_TRABALHADOR + ".crt_obs";

    public static final String TB_STATUS_CREDITO_TRABALHADOR = "tb_status_credito_trabalhador";
    public static final String SCT_CODIGO = TB_STATUS_CREDITO_TRABALHADOR + ".sct_codigo";
    public static final String SCT_DESCRICAO = TB_STATUS_CREDITO_TRABALHADOR + ".sct_descricao";

    public static final String TB_FORMULARIO_PESQUISA = "tb_form_pesquisa";
    public static final String FPE_CODIGO = TB_FORMULARIO_PESQUISA + ".fpe_codigo";
    public static final String FPE_NOME = TB_FORMULARIO_PESQUISA + ".fpe_nome";
    public static final String FPE_BLOQUEIA_SISTEMA = TB_FORMULARIO_PESQUISA + ".fpe_bloqueia_sistema";
    public static final String FPE_DT_CRIACAO = TB_FORMULARIO_PESQUISA + ".fpe_dt_criacao";
    public static final String FPE_DT_FIM = TB_FORMULARIO_PESQUISA + ".fpe_dt_fim";
    public static final String FPE_PUBLICADO = TB_FORMULARIO_PESQUISA + ".fpe_publicado";
    public static final String FPE_JSON = TB_FORMULARIO_PESQUISA + ".fpe_json";

    public static final String TB_FORMULARIO_PESQUISA_RESPOSTA = "tb_form_pesquisa_resposta";
    public static final String FPR_CODIGO = TB_FORMULARIO_PESQUISA_RESPOSTA + ".fpr_codigo";
    public static final String FPR_FPE_CODIGO = TB_FORMULARIO_PESQUISA_RESPOSTA + ".fpe_codigo";
    public static final String FPR_USU_CODIGO = TB_FORMULARIO_PESQUISA_RESPOSTA + ".usu_codigo";
    public static final String FPR_DT_CRIACAO = TB_FORMULARIO_PESQUISA_RESPOSTA + ".dt_criacao";
    public static final String FPR_JSON = TB_FORMULARIO_PESQUISA_RESPOSTA + ".fpr_json";

    public static final String TB_DASHBOARD_FLEX    = "tb_dashboard_flex";
    public static final String DFL_CODIGO           = TB_DASHBOARD_FLEX + ".dfl_codigo";
    public static final String DFL_PAP_CODIGO       = TB_DASHBOARD_FLEX + ".pap_codigo";
    public static final String DFL_FUN_CODIGO       = TB_DASHBOARD_FLEX + ".fun_codigo";
    public static final String DFL_NOME             = TB_DASHBOARD_FLEX + ".dfl_nome";
    public static final String DFL_COMPARTILHAMENTO = TB_DASHBOARD_FLEX + ".dfl_compartilhamento";
    public static final String DFL_ATIVO            = TB_DASHBOARD_FLEX + ".dfl_ativo";

    public static final String TB_DASHBOARD_FLEX_CONSULTA = "tb_dashboard_flex_consulta";
    public static final String DFO_CODIGO                 = TB_DASHBOARD_FLEX_CONSULTA + ".dfo_codigo";
    public static final String DFO_DFL_CODIGO             = TB_DASHBOARD_FLEX_CONSULTA + ".dfl_codigo";
    public static final String DFO_TITULO                 = TB_DASHBOARD_FLEX_CONSULTA + ".dfo_titulo";
    public static final String DFO_INDEX                  = TB_DASHBOARD_FLEX_CONSULTA + ".dfo_index";
    public static final String DFO_TIPO_INDEX             = TB_DASHBOARD_FLEX_CONSULTA + ".dfo_tipo_index";
    public static final String DFO_USA_TOOLBAR            = TB_DASHBOARD_FLEX_CONSULTA + ".dfo_usa_toolbar";
    public static final String DFO_SLICE                  = TB_DASHBOARD_FLEX_CONSULTA + ".dfo_slice";
    public static final String DFO_ATIVO                  = TB_DASHBOARD_FLEX_CONSULTA + ".dfo_ativo";

    public static final String TB_DASHBOARD_FLEX_TOOLBAR = "tb_dashboard_flex_toolbar";
    public static final String DFT_CODIGO        = TB_DASHBOARD_FLEX_TOOLBAR + ".dft_codigo";
    public static final String DFT_DFO_CODIGO    = TB_DASHBOARD_FLEX_TOOLBAR + ".dfo_codigo";
    public static final String DFT_ITEM          = TB_DASHBOARD_FLEX_TOOLBAR + ".dft_item";

    public static final String TB_CONSULTA_MARGEM_SEM_SENHA = "tb_consulta_margem_sem_senha";
    public static final String CSS_CODIGO        			= TB_CONSULTA_MARGEM_SEM_SENHA + ".css_codigo";
    public static final String CSS_RSE_CODIGO 		  		= TB_CONSULTA_MARGEM_SEM_SENHA + ".rse_codigo";
    public static final String CSS_CSA_CODIGO          		= TB_CONSULTA_MARGEM_SEM_SENHA + ".csa_codigo";
    public static final String CSS_DATA_INI          		= TB_CONSULTA_MARGEM_SEM_SENHA + ".css_data_ini";
    public static final String CSS_DATA_FIM          		= TB_CONSULTA_MARGEM_SEM_SENHA + ".css_data_fim";
    public static final String CSS_DATA_REVOGACAO_SUP       = TB_CONSULTA_MARGEM_SEM_SENHA + ".css_data_revogacao_sup";
    public static final String CSS_DATA_REVOGACAO_SER       = TB_CONSULTA_MARGEM_SEM_SENHA + ".css_data_revogacao_ser";
    public static final String CSS_DATA_ALERTA          	= TB_CONSULTA_MARGEM_SEM_SENHA + ".css_data_alerta";

    public static final Map <String, List<String>> TABELAS_AUDITORIA = new HashMap<>();

    static {
        List<String> colunas_tabela = new ArrayList<>();

        TABELAS_AUDITORIA.put(TB_AUDITORIA_COR, colunas_tabela);
        colunas_tabela.add(ACO_COR_CODIGO);
        colunas_tabela.add(ACO_TLO_CODIGO);
        colunas_tabela.add(ACO_USU_CODIGO);
        colunas_tabela.add(ACO_FUN_CODIGO);
        colunas_tabela.add(ACO_TEN_CODIGO);
        colunas_tabela.add(ACO_AUDITADO);
        colunas_tabela.add(ACO_DATA);
        colunas_tabela.add(ACO_IP);
        colunas_tabela.add(ACO_OBS);

        colunas_tabela =  new ArrayList<>();
        TABELAS_AUDITORIA.put(TB_AUDITORIA_CSA, colunas_tabela);
        colunas_tabela.add(ACS_CSA_CODIGO);
        colunas_tabela.add(ACS_TLO_CODIGO);
        colunas_tabela.add(ACS_USU_CODIGO);
        colunas_tabela.add(ACS_FUN_CODIGO);
        colunas_tabela.add(ACS_TEN_CODIGO);
        colunas_tabela.add(ACS_AUDITADO);
        colunas_tabela.add(ACS_DATA);
        colunas_tabela.add(ACS_IP);
        colunas_tabela.add(ACS_OBS);

        colunas_tabela =  new ArrayList<>();
        TABELAS_AUDITORIA.put(TB_AUDITORIA_CSE, colunas_tabela);
        colunas_tabela.add(ACE_CSE_CODIGO);
        colunas_tabela.add(ACE_TLO_CODIGO);
        colunas_tabela.add(ACE_USU_CODIGO);
        colunas_tabela.add(ACE_FUN_CODIGO);
        colunas_tabela.add(ACE_TEN_CODIGO);
        colunas_tabela.add(ACE_AUDITADO);
        colunas_tabela.add(ACE_DATA);
        colunas_tabela.add(ACE_IP);
        colunas_tabela.add(ACE_OBS);

        colunas_tabela =  new ArrayList<>();
        TABELAS_AUDITORIA.put(TB_AUDITORIA_ORG, colunas_tabela);
        colunas_tabela.add(AOR_ORG_CODIGO);
        colunas_tabela.add(AOR_TLO_CODIGO);
        colunas_tabela.add(AOR_USU_CODIGO);
        colunas_tabela.add(AOR_FUN_CODIGO);
        colunas_tabela.add(AOR_TEN_CODIGO);
        colunas_tabela.add(AOR_AUDITADO);
        colunas_tabela.add(AOR_DATA);
        colunas_tabela.add(AOR_IP);
        colunas_tabela.add(AOR_OBS);

        colunas_tabela =  new ArrayList<>();
        TABELAS_AUDITORIA.put(TB_AUDITORIA_SUP, colunas_tabela);
        colunas_tabela.add(ASU_CSE_CODIGO);
        colunas_tabela.add(ASU_TLO_CODIGO);
        colunas_tabela.add(ASU_USU_CODIGO);
        colunas_tabela.add(ASU_FUN_CODIGO);
        colunas_tabela.add(ASU_TEN_CODIGO);
        colunas_tabela.add(ASU_AUDITADO);
        colunas_tabela.add(ASU_DATA);
        colunas_tabela.add(ASU_IP);
        colunas_tabela.add(ASU_OBS);
    }

    public static final boolean isColumn(String nome) {
        try {
            if (nome.indexOf('.') == -1) {
                return false;
            }

            final Field[] campos = new Columns().getClass().getFields();
            for (final Field campo : campos) {
                if (campo.get(null).equals(nome)) {
                    return true;
                }
            }
            return false;
        } catch (final Exception ex) {
            return false;
        }
    }

    public static final boolean isTable(String nome) {
        try {
            if (nome.indexOf('.') != -1) {
                return false;
            }

            final Field[] campos = new Columns().getClass().getFields();
            for (final Field campo : campos) {
                if (campo.get(null).equals(nome)) {
                    return true;
                }
            }
            return false;
        } catch (final Exception ex) {
            return false;
        }
    }

    public static final String getColumnName(String nome) {
        final int indice = nome.indexOf('.');
        if (indice == -1) {
            return nome;
        } else {
            return nome.substring(indice + 1);
        }
    }

    public static final String getFullColumnName(String coluna) throws Exception {
        String nome = null, valor = null;
        try {
            coluna = coluna.toLowerCase();
            final Field[] campos = new Columns().getClass().getFields();
            for (final Field campo : campos) {
                valor = campo.get(null).toString();
                if (valor.indexOf(coluna) != -1) {
                    if (nome == null) {
                        nome = valor;
                    } else {
                        throw new Exception(ApplicationResourcesHelper.getMessage("mensagem.nome.coluna.ambiguo", (AcessoSistema) null));
                    }
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException | SecurityException ex) {
            nome = null;
        }
        return nome;
    }

    public static final List<String> getColumnsOfTable(String tableName, String... ignoredColumns) {
        final List<String> columnsOfTable = new ArrayList<>();
        try {
            final Field[] fields = new Columns().getClass().getFields();
            for (final Field field : fields) {
                final String fieldValue = field.get(null).toString();
                if (fieldValue.startsWith(tableName + ".")) {
                    boolean ignored = false;
                    if ((ignoredColumns != null) && (ignoredColumns.length > 0)) {
                        for (final String ignoredColumn : ignoredColumns) {
                            if (ignoredColumn.equals(fieldValue)) {
                                ignored = true;
                                break;
                            }
                        }
                    }
                    if (!ignored) {
                        columnsOfTable.add(fieldValue.substring(fieldValue.indexOf('.') + 1));
                    }
                }
            }
        } catch (IllegalArgumentException | IllegalAccessException | SecurityException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return columnsOfTable;
    }

    public static final String getColumnLabel(String chave) {
        return ApplicationResourcesHelper.getMessage(chave, null);
    }
}
