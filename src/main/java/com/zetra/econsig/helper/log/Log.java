package com.zetra.econsig.helper.log;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.values.CodedValues;

import java.util.List;

/**
 * <p>Title: Log</p>
 * <p>Description: Interface que define as constantes de Log</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class Log {

    // NIVEL SEVERIDADE
    public static final String LOG_SEVERIDADE_BAIXA = "1";
    public static final String LOG_SEVERIDADE_MEDIA = "2";
    public static final String LOG_SEVERIDADE_ALTA  = "3";

    // TIPO DE LOG
    public static final String LOG_ERRO_SEGURANCA   = "1";
    public static final String LOG_INFORMACAO       = "2";
    public static final String LOG_AVISO            = "3";
    public static final String LOG_LOGIN_ERRO       = "4";
    public static final String LOG_LOGIN_SUCESSO    = "5";
    public static final String LOG_LOGOUT           = "6";
    public static final String LOG_ERRO             = "7";
    public static final String LOG_DIGITAL_INVALIDA = "8";
    public static final String LOG_CONSULTA = "9";

    // TIPO DE ENTIDADES
    public static final String GERAL                                = "0";
    public static final String CONSIGNANTE                          = "1";
    public static final String ESTABELECIMENTO                      = "2";
    public static final String ORGAO                                = "3";
    public static final String CONSIGNATARIA                        = "4";
    public static final String CORRESPONDENTE                       = "5";
    public static final String SERVIDOR                             = "6";
    public static final String REGISTRO_SERVIDOR                    = "7";
    public static final String USUARIO                              = "8";
    public static final String USUARIO_SER                          = "9";
    public static final String SERVICO                              = "10";
    public static final String CONVENIO                             = "11";
    public static final String VERBA_CONVENIO                       = "12";
    public static final String FUNCAO                               = "13";
    public static final String FUNCAO_PERFIL                        = "14";
    public static final String PERFIL                               = "15";
    public static final String PARAM_TARIF_CSE                      = "16";
    public static final String PARAM_SVC_CSE                        = "17";
    public static final String PARAM_SIST_CSE                       = "18";
    public static final String AUTORIZACAO                          = "19";
    public static final String PARCELA                              = "20";
    public static final String PRAZO                                = "21";
    public static final String PRAZO_CONSIGNATARIA                  = "22";
    public static final String COEFICIENTE                          = "23";
    public static final String COEFICIENTE_DESCONTO                 = "24";
    public static final String OCORRENCIA_ADE                       = "25";
    public static final String OCORRENCIA_PRD                       = "26";
    public static final String STATUS_AUTORIZACAO                   = "27";
    public static final String PARAM_SVC_CSA                        = "28";
    public static final String PARAM_SVC_AUT_DESCONTO               = "29";
    public static final String CARGO                                = "30";
    public static final String ACAO                                 = "31";
    public static final String PARAM_SIST_CSA                       = "32";
    public static final String BLOQUEIO_FUNCAO_USU_SVC              = "33";
    public static final String PADRAO                               = "34";
    public static final String SUB_ORGAO                            = "35";
    public static final String UNIDADE                              = "36";
    public static final String SALDO_DEVEDOR                        = "37";
    public static final String ARQUIVO                              = "38";
    public static final String OCORRENCIA_USU                       = "39";
    public static final String RESULTADO_VALIDACAO_MOVIMENTO        = "40";
    public static final String RESULTADO_REGRA_VALIDACAO_MOVIMENTO  = "41";
    public static final String SENHA_SERVIDOR                       = "42";
    public static final String COEFICIENTE_CORRECAO                 = "43";
    public static final String DADOS_AUTORIZACAO_DESCONTO           = "44";
    public static final String MENSAGEM                             = "45";
    public static final String GRUPO_CONSIGNATARIA                  = "46";
    public static final String OCORRENCIA_CONSIGNATARIA             = "47";
    public static final String GRUPO_SERVICO                        = "48";
    public static final String CORRESPONDENTE_CONVENIO              = "49";
    public static final String TIPO_MOTIVO_OPERACAO                 = "50";
    public static final String INDICE                               = "51";
    public static final String CALENDARIO                           = "52";
    public static final String MARGEM                               = "53";
    public static final String TIPO_PENALIDADE                      = "54";
    public static final String HISTORICO_MARGEM                     = "55";
    public static final String HISTORICO_CONCLUSAO_RETORNO          = "56";
    public static final String AGENDAMENTO                          = "57";
    public static final String OCORRENCIA_AGENDAMENTO               = "58";
    public static final String PARAMETRO_AGENDAMENTO                = "59";
    public static final String CERTIFICADO                          = "60";
    public static final String EMPRESA_CORRESPONDENTE               = "61";
    public static final String AJUDA                                = "62";
    public static final String COEFICIENTE_ATIVO                    = "63";
    public static final String MENU                                 = "64";
    public static final String ITEM_MENU                            = "65";
    public static final String RELACIONAMENTO_SERVICO               = "66";
    public static final String COMUNICACAO                          = "67";
    public static final String LEITURA_COMUNICACAO                  = "68";
    public static final String CONSULTA_MDX                         = "69";
    public static final String PARAM_CNV_REGISTRO_SERVIDOR          = "70";
    public static final String PARAM_SVC_REGISTRO_SERVIDOR          = "71";
    public static final String CNV_VINCULO_REGISTRO_SERVIDOR        = "72";
    public static final String TRANSFERENCIA_AUTORIZACAO            = "73";
    public static final String RELACIONAMENTO_AUTORIZACAO           = "74";
    public static final String SISTEMA                              = "75";
    public static final String FOLHA                                = "76";
    public static final String ACESSO_RECURSO                       = "77";
    public static final String RESTRICAO_ACESSO                     = "78";
    public static final String RELATORIO                            = "79";
    public static final String PLANO_DESCONTO                       = "80";
    public static final String ENDERECO_CONJUNTO_HABITACIONAL       = "81";
    public static final String PERMISSIONARIO                       = "82";
    public static final String POSTO                                = "83";
    public static final String DESPESA_INDIVIDUAL                   = "84";
    public static final String DESPESA_COMUM                        = "85";
    public static final String JIRA                                 = "86";
    public static final String LIMITE_TAXA_JUROS                    = "87";
    public static final String CALENDARIO_FOLHA_CSE                 = "88";
    public static final String CALENDARIO_FOLHA_EST                 = "89";
    public static final String CALENDARIO_FOLHA_ORG                 = "90";
    public static final String RECLAMACAO                           = "91";
    public static final String TIPO_MOTIVO_RECLAMACAO               = "92";
    public static final String PROPOSTA_PAGAMENTO_DIVIDA            = "93";
    public static final String CALENDARIO_BASE                      = "94";
    public static final String PROPOSTA_LEILAO_SOLICITACAO          = "95";
    public static final String PROTOCOLO_SENHA_AUTORIZACAO          = "96";
    public static final String PARAM_NSE_REGISTRO_SERVIDOR          = "97";
    public static final String PROTOCOLO_CADASTRO_EMAIL             = "98";
    public static final String NOTIFICACAO_DISPOSITIVO              = "99";
    public static final String ANALISE_RISCO_SERVIDOR               = "100";
    public static final String NOTIFICACAO_EMAIL                    = "101";
    public static final String BENEFICIO                            = "102";
    public static final String MODELO_EMAIL                         = "103";
    public static final String CONTRATO_BENEFICIO                   = "104";
    public static final String CALCULO_BENEFICIO                    = "105";
    public static final String TEXTO_SISTEMA                        = "106";
    public static final String BENEFICIARIO                         = "107";
    public static final String ANEXO_BENEFICIARIO                   = "108";
    public static final String ENDERECO_SERVIDOR                    = "109";
    public static final String DADOS_SERVIDOR                       = "110";
    public static final String DEFINICAO_REGRA_TAXA_JUROS           = "111";
    public static final String BOLETO_SERVIDOR                      = "112";
    public static final String DISPENSA_VALIDACAO_DIGITAL           = "113";
    public static final String OPERACAO_FILA_AUTORIZACAO            = "114";
    public static final String PARAM_ORGAO                          = "115";
    public static final String ATENDIMENTO_CHATBOT                  = "116";
    public static final String ENDERECO_CONSIGNATARIA               = "117";
    public static final String PARAM_SVC_COR                        = "118";
    public static final String CONTRATO_BENEFICIO_TITULAR           = "119";
    public static final String CONTRATO_BENEFICIO_DEPENDENTE        = "120";
    public static final String CONTRATO_BENEFICIO_AGREGADO          = "121";
    public static final String ENDERECO_CORRESPONDENTE              = "122";
    public static final String SOLICITACAO_LIQUIDACAO               = "124";
    public static final String CANC_SOLICITACAO_LIQUIDACAO          = "125";
    public static final String COMPOSICAO_MARGEM                    = "126";
    public static final String BLOQUEIO_POSTO_CSA_SVC               = "127";
    public static final String PARAM_CSA_REGISTRO_SERVIDOR          = "128";
    public static final String CONFIGURACAO_ENVIO_EMAIL_CSA         = "129";
    public static final String CONFIGURACAO_ENVIO_EMAIL_CSE         = "130";
    public static final String CONFIGURACAO_ENVIO_EMAIL_SER         = "131";
    public static final String REGRA_LIMITE_OPERACAO                = "132";
    public static final String CONFIGURACAO_ENVIO_EMAIL_CSA_SVC	    = "133";
    public static final String AUTORIZACAO_CSA_OPERAR_SEM_SENHA	    = "134";
    public static final String REVOGACAO_CSA_OPERAR_SEM_SENHA	    = "135";

    // OPERAÇÕES GERAIS
    public static final String CREATE = "1";
    public static final String UPDATE = "2";
    public static final String DELETE = "3";
    public static final String SELECT = "4";
    public static final String FIND   = "5";
    public static final String LOCK   = "6";
    public static final String UNLOCK = "7";

    // OPERAÇÕES SOBRE CONSIGNAÇÕES
    public static final String RESERVAR_MARGEM         = "8";
    public static final String CANCELAR_CONSIGNACAO    = "9";
    public static final String CONFIRMAR_CONSIGNACAO   = "10";
    public static final String DEFERIR_CONSIGNACAO     = "11";
    public static final String INDEFERIR_CONSIGNACAO   = "12";
    public static final String LIQUIDAR_CONSIGNACAO    = "13";
    public static final String DESLIQUIDAR_CONSIGNACAO = "14";
    public static final String REATIVAR_CONSIGNACAO    = "15";
    public static final String SUSPENDER_CONSIGNACAO   = "16";
    public static final String RENEGOCIAR_CONTRATO     = "17";
    public static final String ALTERAR_CONSIGNACAO     = "18";
    public static final String AUTORIZAR_RESERVA       = "19";
    public static final String REIMPLANTAR_CONSIGNACAO = "30";
    public static final String CANCELAR_RENEGOCIACAO   = "38";
    public static final String LIBERAR_ESTOQUE         = "46";
    public static final String DESCANCELAR_CONSIGNACAO = "48";
    public static final String ENVIAR_RESUMO_CONSIGNACAO = "58";
    public static final String SIMULAR_CONSIGNACAO     = "79";
    public static final String ENCERRAR_CONSIGNACAO    = "126";
    public static final String REABRIR_CONSIGNACAO     = "127";

    // OPERAÇÕES ESPECÍFICAS
    public static final String IMPORTACAO_MARGEM       = "20";
    public static final String EXPORTACAO_MOVIMENTO    = "21";
    public static final String IMPORTACAO_RETORNO      = "22";
    public static final String LST_EXP_MOV_FIN         = "23";
    public static final String PRC_EXP_MOV_FIN         = "24";
    public static final String GERAR_RELATORIO         = "25";
    public static final String LOGOUT                  = "26";
    public static final String LOGIN                   = "27";
    public static final String VRF_LOGIN               = "28";
    public static final String SERVIDORES_TRANSFERIDOS = "29";
    public static final String VALIDAR_CERTIFICADO     = "37";
    public static final String AUTORIZA_OP_2A_SENHA    = "39";
    public static final String IMP_ARQ_CONTRACHEQUES   = "40";
    public static final String IMPORTACAO_ARQ_LOTE     = "41";
    public static final String VALIDACAO_ARQ_LOTE      = "42";
    public static final String IMP_ARQ_CONCILIACAO     = "43";
    public static final String GERAR_NOVA_SENHA        = "44";
    public static final String ATIVAR_NOVA_SENHA       = "45";
    public static final String COMENTARIO_IMP_RETORNO  = "47";
    public static final String IMPORTACAO_FALECIDO     = "49";
    public static final String IMP_INCONSISTENCIA      = "50";
    public static final String IMP_ARQ_ADEQUACAO       = "52";
    public static final String VALID_ARQ_ADEQUACAO     = "53";
    public static final String IMP_DESLIGADO_BLOQUEADO = "54";
    public static final String VALIDA_DESLIGADO_BLOQUEADO = "55";
    public static final String IMP_SALDO_DEVEDOR       = "56";
    public static final String IMP_RESCISAO             = "57";
    public static final String PROPOSTA_REFINANCIAMENTO_SALDO_DEVEDOR  = "123";
    public static final String TERMO_ACEITE_DOWNLOAD_ARQUIVO_DADOS_SENSIVEIS  = "129";

    // OPERAÇÕES SOBRE ARQUIVOS
    public static final String UPLOAD_FILE             = "31";
    public static final String DOWNLOAD_FILE           = "32";
    public static final String DELETE_FILE             = "33";
    public static final String CONVERT_FILE            = "34";
    public static final String ACTIVATE_FILE           = "35";
    public static final String DEACTIVATE_FILE         = "36";
    public static final String SEND_FILE               = "51";

    public static final List<String> ALTERA_CONSIGNACAO = List.of(
            Log.BENEFICIARIO,
            Log.AUTORIZACAO,
            Log.SERVIDOR
    );

    /**
     * Retorna a descrição da operação identificada pelo código
     * passado por parâmetro.
     * @param operacao : Código da operação
     * @return : a descrição da operação, usada para gravar registro na
     * tabela de log.
     */
    public static String getOperacao(String operacao, AcessoSistema responsavel) {
        return ApplicationResourcesHelper.getMessage("rotulo.log.operacoes." + operacao, responsavel);
    }

    public static boolean ignorarLogConsultarServidor(AcessoSistema responsavel) {
        return (responsavel != null) && CodedValues.FUN_EXP_MOV_FINANCEIRO.equals(responsavel.getFunCodigo());
    }
}
