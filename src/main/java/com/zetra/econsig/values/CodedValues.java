
package com.zetra.econsig.values;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * <p>Title: CodedValues</p>
 * <p>Description: Constantes específicas do sistema eConsig.</p>
 * <p>Copyright: Copyright (c) 2002-2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@SuppressWarnings("java:S2386")
public interface CodedValues {

    /* Código presente no cabeçalho dos documentos XML para identificar um lote FEBRABAN */
    public static final String CODIGO_ID_FEBRABAN = "CNAB240-081";

    /* Constante timout comunicação Centralizador-eConsig (milisegundos) */
    public static final long TIMEOUT_COMUNICACAO = 15000;

    /* Constantes para criação de arquivo de critica/validadcao.*/
    public static final int TAMANHO_MSG_ERRO_DEFAULT = 100;
    public static final String COMPLEMENTO_DEFAULT = " ";

    /* Código fictío para construção do histórico de ADE */
    public static final String TOC_RELACIONAMENTO_ADE = "99999";

    /* Constantes de tipo de ocorrências: autorização de desconto, parcela, usuário, consignante, consignatária, etc. */
    public static final String TOC_AVISO                                 = "1";
    public static final String TOC_ERRO                                  = "2";
    public static final String TOC_INFORMACAO                            = "3";
    public static final String TOC_TARIF_RESERVA                         = "4";
    //public static final String TOC_TARIF_RENEGOCIACAO                  = "5";
    public static final String TOC_TARIF_LIQUIDACAO                      = "6";
    public static final String TOC_TARIF_CANCELAMENTO_CONSIGNACAO        = "7";
    //public static final String TOC_TARIF_DESCONTO_PARCELA              = "8";
    public static final String TOC_DESFEITO                              = "9";
    public static final String TOC_RELANCAMENTO                          = "10";
    //public static final String TOC_TRANSMISSAO                         = "11";
    //public static final String TOC_EMAIL                               = "12";
    public static final String TOC_RETORNO                               = "13";
    public static final String TOC_ALTERACAO_CONTRATO                    = "14";
    public static final String TOC_CONCLUSAO_CONTRATO                    = "15";
    //public static final String TOC_CODIGO_LOJA                         = "16";
    //public static final String TOC_CODIGO_OPERADOR                     = "17";
    public static final String TOC_ALTERACAO_INDICE                      = "18";
    public static final String TOC_CONCLUSAO_SEM_DESCONTO                = "19";
    public static final String TOC_CONCLUSAO_FUTURA                      = "20";
    //public static final String TOC_AGUARDANDO_LIQUIDACAO               = "21";
    public static final String TOC_BLOQUEIA_CONSIGNATARIA                = "22";
    public static final String TOC_DESBLOQUEIA_CONSIGNATARIA             = "23";
    public static final String TOC_PAGAMENTO_SALDO_DEVEDOR               = "24";
    public static final String TOC_CORRECAO_SALDO_DEVEDOR                = "25";
    public static final String TOC_INCLUSAO_USUARIO                      = "26";
    public static final String TOC_ALTERACAO_USUARIO                     = "27";
    public static final String TOC_ALTERACAO_SENHA_USUARIO               = "28";
    public static final String TOC_EXCLUSAO_USUARIO                      = "29";
    public static final String TOC_BLOQUEIO_USUARIO                      = "30";
    public static final String TOC_DESBLOQUEIO_USUARIO                   = "31";
    public static final String TOC_ALTERACAO_INCLUSAO_CONTRATO           = "32";
    public static final String TOC_ESTOQUE_MENSAL                        = "33";
    public static final String TOC_SOLICITACAO_SALDO_DEVEDOR             = "34";
    public static final String TOC_TARIF_CANCELAMENTO_RESERVA            = "35";
    public static final String TOC_SOLICITACAO_SALDO_DEVEDOR_LIQUIDACAO  = "36";
    public static final String TOC_CONSIGNATARIA_COM_PENDENCIAS_COMPRA   = "37";
    public static final String TOC_PENALIDADE                            = "38";
    public static final String TOC_INICIALIZANDO_SISTEMA                 = "39";
    public static final String TOC_SISTEMA_ATIVO                         = "40";
    public static final String TOC_SISTEMA_INDISPONIVEL                  = "41";
    public static final String TOC_EXPORTACAO_MOV_FINANCEIRO             = "42";
    public static final String TOC_IMPORTACAO_MARGEM                     = "43";
    public static final String TOC_IMPORTACAO_TRANSFERIDOS               = "44";
    public static final String TOC_IMPORTACAO_RETORNO                    = "45";
    public static final String TOC_CONCLUSAO_RETORNO                     = "46";
    public static final String TOC_CRITICA_RETORNO                       = "47";
    public static final String TOC_RETORNO_ATRASADO                      = "48";
    public static final String TOC_BACKUP_BASE_DADOS                     = "49";
    public static final String TOC_RECALCULO_MARGEM                      = "50";
    public static final String TOC_INCLUSAO_AGENDAMENTO                  = "51";
    public static final String TOC_PROCESSAMENTO_AGENDAMENTO             = "52";
    public static final String TOC_ERRO_PROCESSAMENTO_AGENDAMENTO        = "53";
    public static final String TOC_CANCELAMENTO_AGENDAMENTO              = "54";
    public static final String TOC_CONCLUSAO_AGENDAMENTO                 = "55";
    public static final String TOC_BLOQUEIO_AUTOMATICO_USUARIO           = "56";
    public static final String TOC_BLOQUEIO_USUARIO_POR_CSE              = "57";
    public static final String TOC_DEFERIMENTO_CONTRATO                  = "58";
    public static final String TOC_ALTERACAO_SENHA_AUTORIZACAO           = "59";
    public static final String TOC_UTILIZACAO_SENHA_AUTORIZACAO          = "60";
    public static final String TOC_CANCELAMENTO_SENHA_NAO_UTILIZADA      = "61";
    public static final String TOC_AUTORIZACAO_VIA_SENHA_SERVIDOR        = "62";
    public static final String TOC_REJEICAO_PAGAMENTO_SALDO_DEVEDOR      = "63";
    public static final String TOC_RSE_ALTERACAO_DADOS_CADASTRAIS        = "64";
    public static final String TOC_RSE_ALTERACAO_MARGEM                  = "65";
    public static final String TOC_RSE_TRANSFERENCIA_ENTRE_MARGENS       = "66";
    public static final String TOC_RSE_EXCLUSAO_POR_CARGA_MARGEM         = "67";
    public static final String TOC_CONSIGNATARIA_COM_PENDENCIAS_SALDO    = "68";
    public static final String TOC_ALTERACAO_PERFIL_USUARIO              = "69";
    public static final String TOC_PAGAMENTO_REJEITADO_SALDO_DEVEDOR     = "70";
    public static final String TOC_INFORMACAO_SALDO_DEVEDOR              = "71";
    public static final String TOC_CONSIGNATARIA_COM_CMN_PENDENTE        = "72";
    public static final String TOC_SALDO_DEVEDOR_RECALCULADO             = "73";
    public static final String TOC_RECALCULO_SALDO_DEVEDOR               = "74";
    public static final String TOC_AUTORIZACAO_OP_SEGUNDO_USUARIO        = "75";
    public static final String TOC_SALDO_DEVEDOR_APROVADO_SERVIDOR       = "76";
    public static final String TOC_SALDO_DEVEDOR_REJEITADO_SERVIDOR      = "77";
    public static final String TOC_GERACAO_NOVA_SENHA                    = "78";
    public static final String TOC_ATIVAR_NOVA_SENHA                     = "79";
    public static final String TOC_ATUALIZACAO_BASE_BI                   = "80";
    public static final String TOC_INCLUSAO_PERMISSIONARIO               = "81";
    public static final String TOC_ALTERACAO_PERMISSIONARIO              = "82";
    public static final String TOC_EXCLUSAO_PERMISSIONARIO               = "83";
    public static final String TOC_SUSPENSAO_CONTRATO                    = "84";
    public static final String TOC_INCLUSAO_DESPESA_INDIVIDUAL           = "85";
    public static final String TOC_ALTERACAO_AVANCADA_CONTRATO           = "86";
    public static final String TOC_INCLUSAO_DESPESA_COMUM                = "87";
    public static final String TOC_ALTERACAO_POSTO_REGISTRO_SERVIDOR     = "88";
    public static final String TOC_ALTERACAO_TIPO_REGISTRO_SERVIDOR      = "89";
    public static final String TOC_CONCLUSAO_DESPESA_COMUM               = "90";
    public static final String TOC_RSE_INCLUSAO_POR_CARGA_MARGEM         = "91";
    public static final String TOC_RSE_REATIVACAO_POR_CARGA_MARGEM       = "92";
    public static final String TOC_CANCELAMENTO_DESPESA_COMUM            = "93";
    public static final String TOC_BLOQUEIO_USUARIO_FIM_VIGENCIA         = "94";
    //public static final String TOC_SOLICITACAO_SALDO_CANCELADO         = "95";
    public static final String TOC_INCLUSAO_AVANCADA_CONTRATO            = "96";
    public static final String TOC_INDEFERIMENTO_CONTRATO                = "97";
    public static final String TOC_ALONGAMENTO_CONTRATO                  = "98";
    public static final String TOC_TRANSFERENCIA_CONTRATO                = "99";
    public static final String TOC_FALECIMENTO_RSE                       = "100";
    public static final String TOC_CSA_ADE_PAGA_ANEXO_PENDENTE_LIQ       = "101";
    public static final String TOC_RSE_INCLUSAO_MANUAL                   = "102";
    public static final String TOC_RETORNO_PARCIAL                       = "103";
    public static final String TOC_OPERACAO_HOST_A_HOST                  = "104";
    public static final String TOC_SOLICITACAO_SALDO_DEVEDOR_EXCLUSAO    = "105";
    public static final String TOC_CSA_MSG_CONF_LEITURA_NAO_LIDA         = "106";
    public static final String TOC_RELANCAMENTO_COM_REDUCAO_VALOR        = "107";
    public static final String TOC_ALTERACAO_SENHA_AUTORIZACAO_TOTEM     = "108";
    public static final String TOC_AUT_GERAR_SENHA_AUTORIZACAO_TOTEM     = "109";
    public static final String TOC_ALTERACAO_PARAM_SIST_CSE              = "110";
    public static final String TOC_ACEITACAO_TERMO_DE_USO                = "111";
    public static final String TOC_DESFAZER_RETORNO                      = "112";
    public static final String TOC_RETORNO_FERIAS                        = "113";
    public static final String TOC_RETORNO_PARCIAL_FERIAS                = "114";
    public static final String TOC_RETORNO_PARCELA_NAO_EXPORTADA         = "115";
    public static final String TOC_RETORNO_PARCELA_SEM_RETORNO           = "116";
    public static final String TOC_CADASTRO_EMAIL_SERVIDOR_TOTEM         = "117";
    public static final String TOC_AUT_CADASTRO_EMAIL_SERVIDOR_TOTEM     = "118";
    public static final String TOC_REATIVACAO_CONTRATO                   = "119";
    public static final String TOC_OPERACAO_REST                         = "120";
    public static final String TOC_SUSPENSAO_DESCONTO_FOLHA              = "121";
    public static final String TOC_CRIACAO_DADOS_ADICIONAIS              = "122";
    public static final String TOC_ALTERACAO_DADOS_ADICIONAIS            = "123";
    public static final String TOC_EXCLUSAO_DADOS_ADICIONAIS             = "124";
    public static final String TOC_INCLUSAO_OTP_USUARIO                  = "125";
    public static final String TOC_CANCELAMENTO_LEILAO_COM_PROPOSTA      = "126";
    public static final String TOC_RSE_ALTERACAO_STATUS_SERVIDOR         = "127";
    public static final String TOC_MOTIVO_NAO_CONCRETIZACAO_LEILAO       = "128";
    public static final String TOC_APRV_ANEXOS_SOLICITACAO               = "129";
    public static final String TOC_REPROVAR_ANEXOS_SOLICITACAO           = "130";
    public static final String TOC_VALIDACAO_DIGITAL                     = "131";
    public static final String TOC_RELANCAMENTO_SEM_ANEXO                = "132";
    public static final String TOC_CANCELANCAMENTO_AUT_CAD_SERVIDOR      = "133";
    public static final String TOC_INCLUSAO_BENFICIARIO                  = "134";
    public static final String TOC_ALTERACAO_BENFICIARIO                 = "135";
    public static final String TOC_INCLUSAO_ENDERECO_SERVIDOR            = "136";
    public static final String TOC_ALTERACAO_ENDERECO_SERVIDOR           = "137";
    public static final String TOC_INCLUSAO_CONTRATO_BENEFICIO           = "138";
    public static final String TOC_ALTERACAO_CONTRATO_BENEFICIO          = "139";
    public static final String TOC_ALTERACAO_SENHA_APP                   = "140";
    public static final String TOC_EXCLUSAO_CONTRATO_BENEFICIO           = "141";
    public static final String TOC_RSE_INCLUSAO_POR_CARGA_TRANSFERIDOS   = "142";
    public static final String TOC_RSE_ALTERACAO_POR_CARGA_TRANSFERIDOS  = "143";
    public static final String TOC_RSE_EXCLUSAO_POR_CARGA_TRANSFERIDOS   = "144";
    public static final String TOC_RSE_REATIVACAO_POR_CARGA_TRANSFERIDOS = "145";
    public static final String TOC_BLOQUEIO_SERVICO                      = "146";
    public static final String TOC_DESBLOQUEIO_SERVICO                   = "147";
    public static final String TOC_BLOQUEIO_CONVENIO                     = "148";
    public static final String TOC_DESBLOQUEIO_CONVENIO                  = "149";
    public static final String TOC_RSE_BLOQUEIO_VERBA                    = "150";
    public static final String TOC_RSE_BLOQUEIO_SERVICO                  = "151";
    public static final String TOC_RSE_BLOQUEIO_SERVICO_POR_NATUREZA     = "152";
    public static final String TOC_EXCLUSAO_ENDERECO_SERVIDOR            = "153";
    public static final String TOC_EXCLUSAO_BENEFICIARIO                 = "154";
    //public static final String TOC_ALTERACAO_BENEFICIARIO              = "155";
    //public static final String TOC_INCLUSAO_BENEFICIARIO               = "156";
    public static final String TOC_CONFIRMACAO_LEITURA_SERVIDOR          = "157";
    public static final String TOC_ALTERACAO_STATUS_CONTRATO_BENEFICIO   = "158";
    public static final String TOC_ALTERACAO_CONTRATO_PARA_MAIOR         = "159";
    public static final String TOC_ADE_DUPLICADO_MOTIVADO_USUARIO        = "160";
    public static final String TOC_RETIFICACAO_MOTIVO_OPERACAO           = "161";
    public static final String TOC_ACEITACAO_TERMO_DE_USO_SALARYPAY      = "162";
    public static final String TOC_ACEITACAO_POLITICA_PRIVACIDADE_SISTEMA= "163";
    public static final String TOC_ACEITACAO_TERMO_DE_USO_MOBILE         = "164";
    public static final String TOC_ACEITACAO_POLITICA_PRIVACIDADE_MOBILE = "165";
    public static final String TOC_CAD_DISPENSA_VALIDACAO_DIGITAL_SER    = "166";
    public static final String TOC_REV_DISPENSA_VALIDACAO_DIGITAL_SER    = "167";
    public static final String TOC_APROVACAO_CADASTRO_USUARIO_SER        = "168";
    public static final String TOC_VALIDACAO_EMAIL_USUARIO               = "169";
    public static final String TOC_CONFIRMACAO_EMAIL_USUARIO             = "170";
    public static final String TOC_ALTERACAO_DADOS_CADASTRAIS_ENTIDADE   = "171";
    public static final String TOC_DIVERGENCIA_CADASTRO_EMAIL_SERVIDOR   = "172";
    public static final String TOC_SOLICITACAO_CANC_CONTRATO_BENEFICIO   = "173";
    public static final String TOC_EDICAO_FLUXO_PARCELAS                 = "174";
    public static final String TOC_RSE_ALTERACAO_ORGAO_SERVIDOR          = "175";
    public static final String TOC_BLOQ_CSA_ADE_SEM_MINIMO_ANEXOS        = "176";
    public static final String TOC_BLOQ_CSA_REGISTRO_PENALIDADE_PRAZO    = "177";
    public static final String TOC_PROCESSAMENTO_INTERROMPIDO            = "178";
    public static final String TOC_MENSAGEM_CSA_PORTABILIDADE            = "179";
    public static final String TOC_SER_AUTORIZA_DESC_PARCIAL             = "180";
    public static final String TOC_SER_NAO_AUTORIZA_DESC_PARCIAL         = "181";
    public static final String TOC_ALTERA_PERFIL                         = "182";
    public static final String TOC_REVER_LEILAO_NAO_CONCRETIZADO         = "183";
    public static final String TOC_SOLICITAR_LIQUIDACAO_CONSIGNACAO      = "184";
    public static final String TOC_CANC_SOLICITAR_LIQUIDACAO_CONSIGNACAO = "185";
    public static final String TOC_BLOQUEIO_CSA_NAO_CONFIRMAR_LIQUIDACAO = "186";
    public static final String TOC_APROVACAO_CONTRATO_BENEFICIO          = "187";
    public static final String TOC_ALTERACAO_COEFICIENTE                 = "188";
    public static final String TOC_REIMPLANTE_PARCELA_MANUAL             = "189";
    public static final String TOC_PORTABILIDADE_MARGEM_NEGATIVA         = "190";
    public static final String TOC_BLOQUEIO_AUTOMATICO_SEGURANCA         = "191";
    public static final String TOC_RSE_BLOQUEIO_STATUS_MANUAL            = "192";
    public static final String TOC_RSE_DESBLOQUEIO_STATUS_MANUAL         = "193";
    public static final String TOC_BLOQ_CSA_POR_DATA_EXPIRACAO           = "194";
    public static final String TOC_CONSIGNACAO_NOTIFICADA_CSE            = "195";
    public static final String TOC_DATA_VALOR_CONSIGNACAO_LIBERADO_SER   = "196";
    public static final String TOC_SUSPENSAO_CONTRATO_PARCELA_REJEITADA  = "197";
    public static final String TOC_REATIVACAO_CONTRATO_PARCELA_REJEITADA = "198";
    public static final String TOC_CORRESPONDENTE_BLOQUEADA              = "199";
    public static final String TOC_CORRESPONDENTE_DESBLOQUEADA           = "200";
    public static final String TOC_CONFIRMACAO_MARGEM_FOLHA_RSE          = "201";
    public static final String TOC_REJEICAO_MARGEM_FOLHA_RSE             = "202";
    public static final String TOC_ALT_MULT_CONTRATO_MARGEM_LIMITE_DJ    = "203";
    public static final String TOC_DESBLOQUEIO_CONSIGNATARIA_PENDENTE    = "204";
    public static final String TOC_ACEITACAO_TERMO_DE_USO_CADASTRO_SENHA         = "205";
    public static final String TOC_ACEITACAO_POLITICA_PRIVACIDADE_CADASTRO_SENHA = "206";
    public static final String TOC_DOC_CREDENCIAMENTO_CSA_ENVIADO        = "207";
    public static final String TOC_DOC_CREDENCIAMENTO_CSA_APROVADO       = "208";
    public static final String TOC_DOC_CREDENCIAMENTO_CSA_REPROVADO      = "209";
    public static final String TOC_DOC_CREDENCIAMENTO_TERMO_PREENCHIDO   = "210";
    public static final String TOC_DOC_CREDENCIAMENTO_TERMO_ASSINADO_CSA = "211";
    public static final String TOC_DOC_CREDENCIAMENTO_FINALIZADO         = "212";
    public static final String TOC_DOC_CREDENCIAMENTO_TERMO_ASS_APROV    = "213";
    public static final String TOC_DOC_CREDENCIAMENTO_TERMO_ASS_REPROV   = "214";
    public static final String TOC_BLOQ_CSA_POR_CET_EXPIRADO             = "215";
    public static final String TOC_RSE_BLOQUEIO_CONSIGNATARIA            = "216";
    public static final String TOC_ALTERACAO_VIA_LOTE_COM_TODOS_ADES     = "217";
    public static final String TOC_CONFIRMACAO_LIQUIDACAO_ADE            = "218";
    public static final String TOC_RSE_ALTERACAO_BLOQUEIO_SERVICO_SERVIDOR           = "219";
    public static final String TOC_RSE_ALTERACAO_BLOQUEIO_SERVICO_SERVIDOR_NATUREZA  = "220";
    public static final String TOC_RSE_ALTERACAO_BLOQUEIO_VERBAS                     = "221";
    public static final String TOC_RSE_DESBLOQUEIO_SERVICO_SERVIDOR                  = "222";
    public static final String TOC_RSE_DESBLOQUEIO_SERVICO_SERVIDOR_NATUREZA         = "223";
    public static final String TOC_RSE_DESBLOQUEIO_VERBAS                            = "224";
    public static final String TOC_PGTO_SALDO_INSUFICIENTE_VERBA_RESCISORIA          = "225";
    public static final String TOC_ACEITACAO_TERMO_DOWNLOAD_DADOS_SENSIVEIS_MOBILE   = "226";
    public static final String TOC_REIMPLANTE_CONSIGNACAO_NOVA_ADE                   = "227";
    public static final String TOC_BLOQ_VARIACAO_PERCENTUAL_MARGEM_CSA               = "228";
    public static final String TOC_DESBLOQ_VARIACAO_PERCENTUAL_MARGEM_CSA            = "229";
    public static final String TOC_CONFIRMACAO_MENSAGEM_RESERVA_CATEGORIA_QUE_DEVE_EXIBIR_ALERTA = "230";
    public static final String TOC_ALERTA_PERCENTUAL_PARCELA_PAGA_ENVIADA_EMAIL      = "231";
    public static final String TOC_ALTERACAO_INCIDENCIA_MARGEM_SUSPENSAO             = "232";
    public static final String TOC_ALTERACAO_INCIDENCIA_MARGEM_REATIVACAO            = "233";
    public static final String TOC_CANCELAMENTO_RENEGOCIACAO                         = "234";
    public static final String TOC_CANCELAMENTO_RENEGOCIACAO_APOS_DATA_CORTE         = "235";
    public static final String TOC_PARCELAS_REINSERIDAS                              = "236";
    public static final String TOC_RETENCAO_MARGEM_DENTRO_PRAZO_RENEGOCIACAO         = "237";
    public static final String TOC_LIBERACAO_MARGEM_APOS_PRAZO_RENEGOCIACAO          = "238";
    public static final String TOC_LIBERACAO_MARGEM_DENTRO_PRAZO_RENEGOCIACAO        = "239";
    public static final String TOC_RENEGOCIACAO_ALTERACAO_DT_ENCERRAMENTO            = "240";
    public static final String TOC_BLOQ_SOLICITACAO_SALDO_DEVEDOR_RESCISAO_NAO_ATENDIDA   = "241";
    public static final String TOC_DESBLOQUEIO_VERBA_RSE_CSA                         = "242";
    public static final String TOC_BLOQUEIO_VINCULO                                  = "243";
    public static final String TOC_DESBLOQUEIO_VINCULO                               = "244";
    public static final String TOC_REGISTRO_PARTICIPACAO_CONSIGNACAO_RESCISAO_RSE    = "245";
    public static final String TOC_PORTABILIDADE_CARTAO    							 = "246";
    public static final String TOC_OCULTAR_REGISTRO_SER_OCULTO_CSA                   = "247";
    public static final String TOC_EXIBIR_REGISTRO_SER_OCULTO_CSA                    = "248";
    public static final String TOC_LIQUIDACAO_LEGADO_PORTAL_EMPREGA_BRASIL 	         = "249";
    public static final String TOC_SUSPENSAO_LEGADO_PORTAL_EMPREGA_BRASIL 		     = "250";
    public static final String TOC_CANCELAMENTO_LEGADO_PORTAL_EMPREGA_BRASIL 		 = "251";
    public static final String TOC_LIQUIDACAO_PORTAL_EMPREGA_BRASIL     	         = "252";
    public static final String TOC_DESLIQUIDACAO_PORTAL_EMPREGA_BRASIL     	         = "253";
    public static final String TOC_ALTERA_SERVICO_PORTAL_EMPREGA_BRASIL  	         = "254";
    public static final String TOC_ERRO_PROCESSAMENTO_PORTAL_EMPREGA_BRASIL          = "255";
    public static final String TOC_INTEGRACAO_PORTAL_EMPREGA_BRASIL                  = "256";
    public static final String TOC_PROCESSAMENTO_PORTAL_EMPREGA_BRASIL               = "257";
    public static final String TOC_SER_AUTORIZA_CSA_OPERECAR_SEM_SENHA               = "258";
    public static final String TOC_SER_REVOGA_AUTORIZACAO_CSA_OPERECAR_SEM_SENHA     = "259";
    public static final String TOC_ENVIO_MENSAGEM_SERVIDOR_PUSH_NOTIFICATION         = "260";


    /* Ocorrencias de autorizacao que podem ser visualizadas no relatorio */
    public static final List<String> TOC_CODIGOS_AUTORIZACAO = Arrays.asList(
            TOC_AVISO,
            TOC_ERRO,
            TOC_INFORMACAO,
            TOC_TARIF_RESERVA,
            TOC_TARIF_LIQUIDACAO,
            TOC_TARIF_CANCELAMENTO_CONSIGNACAO,
            TOC_TARIF_CANCELAMENTO_RESERVA,
            TOC_RELANCAMENTO,
            TOC_ALTERACAO_CONTRATO,
            TOC_CONCLUSAO_CONTRATO,
            TOC_DEFERIMENTO_CONTRATO,
            TOC_INDEFERIMENTO_CONTRATO,
            TOC_SUSPENSAO_CONTRATO,
            TOC_REATIVACAO_CONTRATO,
            TOC_PAGAMENTO_SALDO_DEVEDOR,
            TOC_CORRECAO_SALDO_DEVEDOR,
            TOC_SOLICITACAO_SALDO_DEVEDOR,
            TOC_SOLICITACAO_SALDO_DEVEDOR_LIQUIDACAO,
            TOC_INFORMACAO_SALDO_DEVEDOR,
            TOC_REJEICAO_PAGAMENTO_SALDO_DEVEDOR,
            TOC_SALDO_DEVEDOR_RECALCULADO,
            TOC_RECALCULO_SALDO_DEVEDOR,
            TOC_SUSPENSAO_DESCONTO_FOLHA,
            TOC_MOTIVO_NAO_CONCRETIZACAO_LEILAO,
            TOC_REPROVAR_ANEXOS_SOLICITACAO,
            TOC_APRV_ANEXOS_SOLICITACAO,
            TOC_VALIDACAO_DIGITAL,
            TOC_PARCELAS_REINSERIDAS
    );

    /* Ocorrencias de usuário que podem ser visualizadas no relatorio */
    public static final List<String> TOC_CODIGOS_USUARIO = Arrays.asList(
            TOC_AVISO,
            TOC_ERRO,
            TOC_INFORMACAO,
            TOC_BLOQUEIO_USUARIO,
            TOC_DESBLOQUEIO_USUARIO,
            TOC_ALTERACAO_PERFIL_USUARIO,
            TOC_INCLUSAO_USUARIO,
            TOC_ALTERACAO_USUARIO,
            TOC_EXCLUSAO_USUARIO,
            TOC_ALTERACAO_SENHA_USUARIO,
            TOC_CANCELAMENTO_SENHA_NAO_UTILIZADA,
            TOC_ALTERACAO_SENHA_AUTORIZACAO,
            TOC_ALTERACAO_SENHA_AUTORIZACAO_TOTEM,
            TOC_UTILIZACAO_SENHA_AUTORIZACAO,
            TOC_BLOQUEIO_AUTOMATICO_USUARIO,
            TOC_BLOQUEIO_USUARIO_POR_CSE,
            TOC_GERACAO_NOVA_SENHA,
            TOC_ATIVAR_NOVA_SENHA,
            TOC_BLOQUEIO_USUARIO_FIM_VIGENCIA
    );

    /* Ocorrencias de consignatária que podem ser visualizadas no relatorio */
    public static final List<String> TOC_CODIGOS_CONSIGNATARIA = Arrays.asList(
            TOC_AVISO,
            TOC_ERRO,
            TOC_INFORMACAO,
            TOC_BLOQUEIA_CONSIGNATARIA,
            TOC_DESBLOQUEIA_CONSIGNATARIA,
            TOC_CONSIGNATARIA_COM_PENDENCIAS_COMPRA,
            TOC_PENALIDADE,
            TOC_CONSIGNATARIA_COM_CMN_PENDENTE,
            TOC_CONSIGNATARIA_COM_PENDENCIAS_SALDO,
            TOC_BLOQ_CSA_REGISTRO_PENALIDADE_PRAZO
    );

    /* Ocorrencias de consignatária que podem ser visualizadas no relatorio */
    public static final List<String> TOC_CODIGOS_PENDENCIAS_CONSIGNATARIA = Arrays.asList(
            TOC_CONSIGNATARIA_COM_PENDENCIAS_COMPRA,
            TOC_CONSIGNATARIA_COM_PENDENCIAS_SALDO,
            TOC_CONSIGNATARIA_COM_CMN_PENDENTE
    );

    /* Ocorrencias de usuário que podem ser visualizadas no relatorio */
    public static final List<String> TOC_CODIGOS_REGISTRO_SERVIDOR = Arrays.asList(
            TOC_AVISO,
            TOC_ERRO,
            TOC_INFORMACAO,
            TOC_RSE_ALTERACAO_DADOS_CADASTRAIS,
            TOC_RSE_ALTERACAO_MARGEM,
            TOC_RSE_EXCLUSAO_POR_CARGA_MARGEM,
            TOC_RSE_INCLUSAO_POR_CARGA_MARGEM,
            TOC_RSE_INCLUSAO_MANUAL,
            TOC_RSE_REATIVACAO_POR_CARGA_MARGEM,
            TOC_RSE_TRANSFERENCIA_ENTRE_MARGENS,
            TOC_ALTERACAO_POSTO_REGISTRO_SERVIDOR,
            TOC_ALTERACAO_TIPO_REGISTRO_SERVIDOR,
            TOC_RSE_BLOQUEIO_STATUS_MANUAL,
            TOC_RSE_DESBLOQUEIO_STATUS_MANUAL
    );

    /* Ocorrencias de usuário que podem ser visualizadas no relatorio */
    public static final List<String> TOC_CODIGOS_RETORNO_PARCELA = Arrays.asList(
            TOC_RETORNO,
            TOC_RETORNO_PARCIAL,
            TOC_RETORNO_FERIAS,
            TOC_RETORNO_PARCIAL_FERIAS,
            TOC_RETORNO_PARCELA_NAO_EXPORTADA,
            TOC_RETORNO_PARCELA_SEM_RETORNO
    );

    /* Ocorrencias de consignação que podem são relativas a operação de exclusão pós corte */
    public static final List<String> TOC_CODIGOS_EXCLUSAO_POS_CORTE = Arrays.asList(
            TOC_TARIF_LIQUIDACAO,
            TOC_TARIF_CANCELAMENTO_CONSIGNACAO
    );

    /* Tipos de ocorrências do período que serão levadas em conta */
    public static final List<String> TOC_CODIGOS_EXPORTACAO_INICIAL = Arrays.asList(
            TOC_TARIF_LIQUIDACAO,
            TOC_TARIF_CANCELAMENTO_CONSIGNACAO,
            TOC_RELANCAMENTO,
            TOC_RELANCAMENTO_COM_REDUCAO_VALOR,
            TOC_ALTERACAO_CONTRATO,
            TOC_ALTERACAO_INCLUSAO_CONTRATO,
            TOC_CONCLUSAO_CONTRATO,
            TOC_CONCLUSAO_SEM_DESCONTO
    );

    /* Tipo de Ocorrencias de solicitação de saldo devedor */
    public static final List<String> TOC_CODIGOS_SOLICITACAO_SALDO_DEVEDOR = Arrays.asList(
                    CodedValues.TOC_SOLICITACAO_SALDO_DEVEDOR,
                    CodedValues.TOC_SOLICITACAO_SALDO_DEVEDOR_EXCLUSAO,
                    CodedValues.TOC_SOLICITACAO_SALDO_DEVEDOR_LIQUIDACAO);

    /* Constantes de status de autorização de desconto */
    public static final String SAD_SOLICITADO               = "0";
    public static final String SAD_AGUARD_CONF              = "1";
    public static final String SAD_AGUARD_DEFER             = "2";
    public static final String SAD_INDEFERIDA               = "3";
    public static final String SAD_DEFERIDA                 = "4";
    public static final String SAD_EMANDAMENTO              = "5";
    public static final String SAD_SUSPENSA                 = "6";
    public static final String SAD_CANCELADA                = "7";
    public static final String SAD_LIQUIDADA                = "8";
    public static final String SAD_CONCLUIDO                = "9";
    public static final String SAD_SUSPENSA_CSE             = "10";
    public static final String SAD_AGUARD_LIQUIDACAO        = "11";
    public static final String SAD_ESTOQUE                  = "12";
    public static final String SAD_ESTOQUE_NAO_LIBERADO     = "13";
    public static final String SAD_EMCARENCIA               = "14";
    public static final String SAD_AGUARD_LIQUI_COMPRA      = "15";
    public static final String SAD_ESTOQUE_MENSAL           = "16";
    public static final String SAD_AGUARD_MARGEM            = "17";
    public static final String SAD_ENCERRADO                = "18";

    // Status de autorizações que estão ativas no sistema,
    // ou seja, estão prendendo margem do servidor
    public static final List<String> SAD_CODIGOS_ATIVOS = Arrays.asList(
            SAD_SOLICITADO,
            SAD_AGUARD_CONF,
            SAD_AGUARD_DEFER,
            SAD_DEFERIDA,
            SAD_EMANDAMENTO,
            SAD_EMCARENCIA,
            SAD_SUSPENSA,
            SAD_SUSPENSA_CSE,
            SAD_ESTOQUE,
            SAD_ESTOQUE_MENSAL,
            SAD_ESTOQUE_NAO_LIBERADO
    );

    // Status de autorizações que estão inativas no sistema,
    // ou seja, não estão prendendo margem do servidor
    public static final List<String> SAD_CODIGOS_INATIVOS = Arrays.asList(
            SAD_INDEFERIDA,
            SAD_CANCELADA,
            SAD_LIQUIDADA,
            SAD_CONCLUIDO,
            SAD_ENCERRADO
    );

    // Acrescenta todos os status presentes em CodedValues.SAD_CODIGOS_ATIVOS
    // de menos os SAD_AGUARD_CONF e SAD_AGUARD_DEFER com acréscimo dos
    // SAD_AGUARD_LIQUIDACAO e SAD_AGUARD_LIQUI_COMPRA
    public static final List<String> SAD_CODIGOS_ATIVOS_LIMITE = Arrays.asList(
            SAD_SOLICITADO,
            SAD_DEFERIDA,
            SAD_EMANDAMENTO,
            SAD_EMCARENCIA,
            SAD_SUSPENSA,
            SAD_SUSPENSA_CSE,
            SAD_ESTOQUE,
            SAD_ESTOQUE_MENSAL,
            SAD_ESTOQUE_NAO_LIBERADO,
            SAD_AGUARD_LIQUIDACAO,
            SAD_AGUARD_LIQUI_COMPRA,
            SAD_AGUARD_MARGEM
    );

    public static final List<String> SAD_CODIGOS_ALTERACAO_AVANCADA = Arrays.asList(
            SAD_SUSPENSA_CSE,
            SAD_LIQUIDADA,
            SAD_CANCELADA,
            SAD_CONCLUIDO
    );

    // Só serão contabilizados, caso não sejam fruto de uma renegociação
    public static final List<String> SAD_CODIGOS_AGUARD_CONF = Arrays.asList(
            SAD_AGUARD_CONF,
            SAD_AGUARD_DEFER
    );

    // Status aguardando confirmação ou deferimento
    public static final List<String> SAD_CODIGOS_AGUARD_DEF = Arrays.asList(
            SAD_SOLICITADO,
            SAD_AGUARD_CONF,
            SAD_AGUARD_DEFER
    );

    // O contrato origem de compra/renegociação deve estar numa destas situações
    // para que o contrato em SAD_CODIGOS_AGUARD_CONF não seja contado
    public static final List<String> SAD_CODIGOS_AGUARD_LIQ = Arrays.asList(
            SAD_AGUARD_LIQUIDACAO,
            SAD_AGUARD_LIQUI_COMPRA
    );

    // Só serão contabilizados, caso não sejam fruto de uma renegociação
    public static final List<String> SAD_CODIGOS_SUSPENSOS = Arrays.asList(
            SAD_SUSPENSA,
            SAD_SUSPENSA_CSE
    );

    // Status dos contratos para os quais serão gerados parcelas
    public static final List<String> SAD_CODIGOS_INCLUSAO_PARCELA = Arrays.asList(
            SAD_DEFERIDA,
            SAD_EMANDAMENTO,
            SAD_ESTOQUE,
            SAD_ESTOQUE_MENSAL,
            SAD_AGUARD_LIQUIDACAO,
            SAD_AGUARD_LIQUI_COMPRA
    );

    // Status dos contratos que são exportados como abertos
    public static final List<String> SAD_CODIGOS_ABERTOS_EXPORTACAO = Arrays.asList(
            SAD_DEFERIDA,
            SAD_EMANDAMENTO,
            SAD_AGUARD_LIQUIDACAO,
            SAD_AGUARD_LIQUI_COMPRA
    );

    // Status dos contratos deferidos, em carência e em estoque quando sistema não reimplanta ou reimplanta e não preserva parcela
    public static final List<String> SAD_CODIGOS_CONCLUSAO_SEM_DESCONTO = Arrays.asList(
            SAD_DEFERIDA,
            SAD_EMCARENCIA,
            SAD_ESTOQUE_MENSAL
    );

    // Status dos contratos deferidos, em andamento, em carência e em estoque quando sistema não reimplanta ou reimplanta e não preserva parcela
    public static final List<String> SAD_CODIGOS_CONCLUSAO = Arrays.asList(
            SAD_DEFERIDA,
            SAD_EMANDAMENTO,
            SAD_EMCARENCIA,
            SAD_ESTOQUE_MENSAL
    );

    // Status dos contratos deferidos, em andamento e em estoque quando sistema não reimplanta ou reimplanta e não preserva parcela
    public static final List<String> SAD_CODIGOS_ALTERACAO_CARENCIA = Arrays.asList(
            SAD_DEFERIDA,
            SAD_EMANDAMENTO,
            SAD_ESTOQUE_MENSAL
    );

    // Status dos contratos em andamento e em carência quando sistema preserva parcela
    public static final List<String> SAD_CODIGOS_CONCLUSAO_PRESERVA_PRD = Arrays.asList(
            SAD_EMANDAMENTO,
            SAD_EMCARENCIA
    );

    // Status dos contratos que estão com parcelas pagas maior ou igual ao prazo
    public static final List<String> SAD_CODIGOS_CONCLUSAO_PAGAS_MAIOR_PRAZO = Arrays.asList(
            SAD_DEFERIDA,
            SAD_EMANDAMENTO,
            SAD_SUSPENSA,
            SAD_SUSPENSA_CSE,
            SAD_ESTOQUE,
            SAD_ESTOQUE_NAO_LIBERADO,
            SAD_EMCARENCIA,
            SAD_ESTOQUE_MENSAL
    );

    public static final List<String> SAD_CODIGOS_DEFERIDAS_OU_ANDAMENTO = List.of(
            SAD_DEFERIDA,
            SAD_EMANDAMENTO,
            SAD_SUSPENSA,
            SAD_SUSPENSA_CSE,
            SAD_ESTOQUE,
            SAD_ESTOQUE_NAO_LIBERADO,
            SAD_EMCARENCIA,
            SAD_ESTOQUE_MENSAL
    );

    // Status dos contratos deferidos e em andamento para colocar em estoque
    public static final List<String> SAD_CODIGOS_ALTERACAO_EM_ESTOQUE = Arrays.asList(
            SAD_DEFERIDA,
            SAD_EMANDAMENTO
    );

    // Status dos contratos passíveis de portabilidade/compra
    public static final List<String> SAD_CODIGOS_PORTABILIDADE = Arrays.asList(
            SAD_DEFERIDA,
            SAD_EMANDAMENTO,
            SAD_ESTOQUE,
            SAD_ESTOQUE_MENSAL
    );

    // Status dos contratos liquidados e concluídos
    public static final List<String> SAD_CODIGOS_LIQUIDADO_CONCLUIDO = Arrays.asList(
            SAD_LIQUIDADA,
            SAD_CONCLUIDO
    );

    // Status dos contratos liquidados e concluídos
    public static final List<String> SAD_CODIGOS_EXCLUIDOS_POS_CORTE = Arrays.asList(
            SAD_CANCELADA,
            SAD_LIQUIDADA
    );

    // Status dos contratos abertos
    public static final List<String> SAD_CODIGOS_ABERTOS_INCIDEM_MARGEM_BRUTA = Arrays.asList(
            SAD_SOLICITADO,
            SAD_AGUARD_CONF,
            SAD_AGUARD_DEFER,
            SAD_DEFERIDA,
            SAD_EMANDAMENTO,
            SAD_SUSPENSA,
            SAD_SUSPENSA_CSE,
            SAD_AGUARD_LIQUIDACAO,
            SAD_ESTOQUE,
            SAD_ESTOQUE_NAO_LIBERADO,
            SAD_EMCARENCIA,
            SAD_AGUARD_LIQUI_COMPRA,
            SAD_ESTOQUE_MENSAL
    );

    // Status dos contratos abertos que sempre incidem na margem líquida(vazia)
    public static final List<String> SAD_CODIGOS_ABERTOS_INCIDEM_MARGEM_LIQUIDA = Arrays.asList(
            SAD_SOLICITADO,
            SAD_AGUARD_CONF,
            SAD_AGUARD_DEFER,
            SAD_DEFERIDA,
            SAD_ESTOQUE,
            SAD_ESTOQUE_NAO_LIBERADO,
            SAD_EMCARENCIA,
            SAD_ESTOQUE_MENSAL
    );

    // Status dos contratos abertos que incidem na margem líquida(vazia) caso não estejam pagos
    public static final List<String> SAD_CODIGOS_ABERTOS_INCIDEM_MARGEM_LIQ_SE_NAO_PAGOS = Arrays.asList(
            SAD_SUSPENSA,
            SAD_SUSPENSA_CSE,
            SAD_AGUARD_LIQUIDACAO,
            SAD_AGUARD_LIQUI_COMPRA
    );

    // Status dos contratos referente a liquidação
    public static final List<String> SAD_CODIGOS_LIQUIDACAO = Arrays.asList(
            SAD_LIQUIDADA,
            SAD_AGUARD_LIQUIDACAO,
            SAD_AGUARD_LIQUI_COMPRA
    );

    // Status dos contratos que podem ser movidos para estoque na inclusão de compulsório
    public static final List<String> SAD_CODIGOS_INCLUSAO_COMPULSORIO_COM_ESTOQUE = Arrays.asList(
            SAD_DEFERIDA,
            SAD_EMANDAMENTO,
            SAD_ESTOQUE,
            SAD_ESTOQUE_NAO_LIBERADO,
            SAD_ESTOQUE_MENSAL
    );

    // Status dos contratos que podem ser movidos para estoque na inclusão de compulsório sem estoeu
    public static final List<String> SAD_CODIGOS_INCLUSAO_COMPULSORIO_SEM_ESTOQUE = Arrays.asList(
            SAD_DEFERIDA,
            SAD_EMANDAMENTO
    );

    /* Constantes de tipos de natureza */
    public static final String TNT_COMPRA                                   = "1";  // Utilizado para relacionar os serviços
    public static final String TNT_ALONGAMENTO                              = "2";  // Utilizado para relacionar os serviços, e as consignações utilizam TNT_CONTROLE_RENEGOCIACAO
    public static final String TNT_CARTAO                                   = "3";  // Utilizado para relacionar serviços de reserva e lançamento de provisionamento de margem.
    public static final String TNT_CORRECAO_SALDO                           = "4";  // Utilizado para relacionar as consignações e os serviços
    public static final String TNT_CONTROLE_COMPULSORIOS                    = "5";  // Utilizado para relacionar as consignações
    public static final String TNT_CONTROLE_RENEGOCIACAO                    = "6";  // Utilizado para relacionar as consignações
    public static final String TNT_CONTROLE_COMPRA                          = "7";  // Utilizado para relacionar as consignações
    public static final String TNT_RENEGOCIACAO                             = "8";  // Utilizado para relacionar os serviços
    public static final String TNT_SALDO_PARCELAS                           = "9";  // Utilizado para relacionar os serviços
    public static final String TNT_CONTRATO_PREEXISTENTE_LIBERA_SIMULACAO   = "10"; // Utilizado para relacionar os serviços
    public static final String TNT_COMPARTILHA_CADASTRO_TAXAS               = "11"; // Utilizado para relacionar os serviços
    public static final String TNT_CONTRATO_PREEXISTENTE_LIBERA_OPERACAO    = "12"; // Utilizado para relacionar os serviços
    public static final String TNT_UNICIDADE_CAD_INDICE                     = "13"; // Utilizado para relacionar os serviços
    public static final String TNT_CONTRATO_PREEXISTENTE_LIBERA_COMPRA      = "14"; // Utilizado para relacionar os serviços
    public static final String TNT_ALTERACAO_DECISAO_JUDICIAL               = "15"; // Utilizado para relacionar as consignações
    public static final String TNT_SERVICOS_COM_LIMITE_CAPITAL_DEVIDO       = "16"; // Utilizado para relacionar os serviços
    public static final String TNT_PERMITE_TRANSFERENCIA_ENTRE_SERVICOS     = "17"; // Utilizado para relacionar os serviços
    public static final String TNT_CONTRATO_PREEXISTENTE_REQUER_DEFERIMENTO = "18"; // Utilizado para relacionar os serviços
    public static final String TNT_FINANCIAMENTO_DIVIDA                     = "19"; // Utilizado para relacionar as consignações e os serviços
    public static final String TNT_LEILAO_SOLICITACAO                       = "20"; // Utilizado para relacionar as consignações
    public static final String TNT_REIMPLANTE_CAPITAL_DEVIDO                = "21"; // Utilizado para relacionar as consignações
    public static final String TNT_SERVICOS_CONTROLE_DESCONTO_EM_FILA       = "22"; // Utilizado para relacionar os serviços
    public static final String TNT_CONTRATO_GERADO_INSERE_ALTERA            = "23"; // Utilizado para relacionar as consignações
    public static final String TNT_TRANSFERENCIA_CONTRATO                   = "24"; // Utilizado para relacionar as consignações
    public static final String TNT_MENSALIDADE_PLANO_SAUDE                  = "25"; // Utilizado para beneficios
    public static final String TNT_MENSALIDADE_ODONTOLOGICO                 = "26"; // Utilizado para beneficios
    public static final String TNT_COPART                                   = "27"; // Utilizado para beneficios
    public static final String TNT_CREDITO_PRO_RATA_PLANO_SAUDE             = "28"; // Utilizado para beneficios
    public static final String TNT_CREDITO_PRO_ODONTOLOGICO                 = "29"; // Utilizado para beneficios
    public static final String TNT_DEBITO_PRO_RATA_PLANO_SAUDE              = "30"; // Utilizado para beneficios
    public static final String TNT_DEBITO_PRO_ODONTOLOGICO                  = "31"; // Utilizado para beneficios
    public static final String TNT_CREDITO_CREDENCIADA_PLANO_SAUDE          = "32"; // Utilizado para beneficios
    public static final String TNT_CREDITO_CREDENCIADA_ODONTOLOGICO         = "33"; // Utilizado para beneficios
    public static final String TNT_DEBITO_BOLETO_PLANO_SAUDE                = "34"; // Utilizado para beneficios
    public static final String TNT_RESIDUO_PLANO_SAUDE                      = "35"; // Utilizado para beneficios
    public static final String TNT_SUBSIDIO_PLANO_SAUDE                     = "36"; // Utilizado para beneficios
    public static final String TNT_SUBSIDIO_ODONTOLOGICO                    = "37"; // Utilizado para beneficios
    public static final String TNT_DEBITO_CREDENCIADA_PLANO_SAUDE           = "38"; // Utilizado para beneficios
    public static final String TNT_DEBITO_CREDENCIADA_ODONTOLOGICO          = "39"; // Utilizado para beneficios
    public static final String TNT_CREDITO_OPERADORA_PLANO_SAUDE            = "40"; // Utilizado para beneficios
    public static final String TNT_CREDITO_CONSIGNANTE_PLANO_SAUDE          = "41"; // Utilizado para beneficios
    public static final String TNT_DEBITO_OPERADORA_PLANO_SAUDE             = "42"; // Utilizado para beneficios
    public static final String TNT_DEBITO_CONSIGNANTE_PLANO_SAUDE           = "43"; // Utilizado para beneficios
    public static final String TNT_RELACIONAMENTO_REGISTRO_SERVIDOR         = "44"; // Utilizado para relacionar a transferência entre servidores.
    public static final String TNT_RESIDUO_ODONTOLOGICO                     = "45"; // Utilizado para beneficios
    public static final String TNT_CREDITO_OPERADORA_ODONTOLOGICO           = "46"; // Utilizado para beneficios
    public static final String TNT_CREDITO_CONSIGNANTE_ODONTOLOGICO         = "47"; // Utilizado para beneficios
    public static final String TNT_DEBITO_OPERADORA_ODONTOLOGICO            = "48"; // Utilizado para beneficios
    public static final String TNT_DEBITO_CONSIGNANTE_ODONTOLOGICO          = "49"; // Utilizado para beneficios
    public static final String TNT_SUBSIDIO_DEBITO_PRO_RATA                 = "50"; // Utilizado para beneficios
    public static final String TNT_SUBSIDIO_CREDITO_PRO_RATA                = "51"; // Utilizado para beneficios
    public static final String TNT_IMPEDIMENTO_RESERVA_SVC_DESTINO          = "52"; // Utilizado para relacionar serviços excludentes entre si para reserva de margem, caso uma delas já tenha contrato.
    public static final String TNT_CONTROLE_MIGRACAO_BENEFICIO              = "53"; // Relacionamento de migração de benefício
    public static final String TNT_VERBA_RESCISORIA                         = "54"; // Utilizado para relacionar pagamento com verba rescisoria
    public static final String TNT_SOLICITACAO_PORTABILIDADE                = "55"; // Utilizado para relacionar as consignações

    // Criando um relacionamento dos tipo de natureza relacionado ao modulo de beneficio
    public static final List<String> TNT_RELACIONAMENTO_MODULO_BENEFICIO = Arrays.asList(
            TNT_MENSALIDADE_PLANO_SAUDE,
            TNT_MENSALIDADE_ODONTOLOGICO,
            TNT_COPART,
            TNT_CREDITO_PRO_RATA_PLANO_SAUDE,
            TNT_CREDITO_PRO_ODONTOLOGICO,
            TNT_DEBITO_PRO_RATA_PLANO_SAUDE,
            TNT_DEBITO_PRO_ODONTOLOGICO,
            TNT_CREDITO_CREDENCIADA_PLANO_SAUDE,
            TNT_CREDITO_CREDENCIADA_ODONTOLOGICO,
            TNT_DEBITO_BOLETO_PLANO_SAUDE,
            TNT_RESIDUO_PLANO_SAUDE,
            TNT_SUBSIDIO_PLANO_SAUDE,
            TNT_SUBSIDIO_ODONTOLOGICO,
            TNT_DEBITO_CREDENCIADA_PLANO_SAUDE,
            TNT_DEBITO_CREDENCIADA_ODONTOLOGICO,
            TNT_CREDITO_OPERADORA_PLANO_SAUDE,
            TNT_CREDITO_CONSIGNANTE_PLANO_SAUDE,
            TNT_DEBITO_OPERADORA_PLANO_SAUDE,
            TNT_DEBITO_CONSIGNANTE_PLANO_SAUDE,
            TNT_RESIDUO_ODONTOLOGICO,
            TNT_CREDITO_OPERADORA_ODONTOLOGICO,
            TNT_CREDITO_CONSIGNANTE_ODONTOLOGICO,
            TNT_DEBITO_OPERADORA_ODONTOLOGICO,
            TNT_DEBITO_CONSIGNANTE_ODONTOLOGICO,
            TNT_SUBSIDIO_DEBITO_PRO_RATA,
            TNT_SUBSIDIO_CREDITO_PRO_RATA
    );

    // Lista dos tipo de natureza beneficio mensalidade
    public static final List<String> TNT_BENEFICIO_MENSALIDADE = Arrays.asList(
            TNT_MENSALIDADE_PLANO_SAUDE,
            TNT_MENSALIDADE_ODONTOLOGICO
    );

    // Lista dos tipo de natureza beneficio subsidio
    public static final List<String> TNT_BENEFICIO_SUBSIDIO = Arrays.asList(
            TNT_SUBSIDIO_PLANO_SAUDE,
            TNT_SUBSIDIO_ODONTOLOGICO
    );

    // Lista dos tipo de natureza de verbas manuais
    public static final List<String> TNT_BENEFICIO_VERBAS_MANUAIS = Arrays.asList(
            TNT_CREDITO_CONSIGNANTE_ODONTOLOGICO,
            TNT_CREDITO_CONSIGNANTE_PLANO_SAUDE,
            TNT_CREDITO_CREDENCIADA_ODONTOLOGICO,
            TNT_CREDITO_CREDENCIADA_PLANO_SAUDE,
            TNT_CREDITO_OPERADORA_ODONTOLOGICO,
            TNT_CREDITO_OPERADORA_PLANO_SAUDE,
            TNT_DEBITO_BOLETO_PLANO_SAUDE,
            TNT_DEBITO_CONSIGNANTE_ODONTOLOGICO,
            TNT_DEBITO_CONSIGNANTE_PLANO_SAUDE,
            TNT_DEBITO_CREDENCIADA_ODONTOLOGICO,
            TNT_DEBITO_CREDENCIADA_PLANO_SAUDE,
            TNT_DEBITO_OPERADORA_ODONTOLOGICO,
            TNT_DEBITO_OPERADORA_PLANO_SAUDE,
            TNT_RESIDUO_ODONTOLOGICO,
            TNT_RESIDUO_PLANO_SAUDE
    );

    // Listo dos tipo de Natureza que não fazem parte da fatura.
    public static final List<String> TNT_BENEFICIO_VERBAS_SEM_FATURA = Arrays.asList(
            TNT_CREDITO_CONSIGNANTE_ODONTOLOGICO,
            TNT_CREDITO_CONSIGNANTE_PLANO_SAUDE,
            TNT_CREDITO_CREDENCIADA_ODONTOLOGICO,
            TNT_CREDITO_CREDENCIADA_PLANO_SAUDE,
            TNT_CREDITO_OPERADORA_ODONTOLOGICO,
            TNT_DEBITO_BOLETO_PLANO_SAUDE,
            TNT_DEBITO_CONSIGNANTE_ODONTOLOGICO,
            TNT_DEBITO_CONSIGNANTE_PLANO_SAUDE,
            TNT_DEBITO_CREDENCIADA_ODONTOLOGICO,
            TNT_DEBITO_CREDENCIADA_PLANO_SAUDE,
            TNT_DEBITO_OPERADORA_ODONTOLOGICO,
            TNT_RESIDUO_ODONTOLOGICO,
            TNT_RESIDUO_PLANO_SAUDE
    );

    // Lista dos tipo de natureza Pro Rata
    public static final List<String> TNT_BENEFICIO_PRO_RATA = Arrays.asList(
            TNT_CREDITO_PRO_RATA_PLANO_SAUDE,
            TNT_CREDITO_PRO_ODONTOLOGICO,
            TNT_DEBITO_PRO_RATA_PLANO_SAUDE,
            TNT_DEBITO_PRO_ODONTOLOGICO
    );

    // Lista dos tipo de natureza beneficio subsidio pro rata
    public static final List<String> TNT_BENEFICIO_SUBSIDIO_PRO_RATA = Arrays.asList(
            TNT_SUBSIDIO_DEBITO_PRO_RATA,
            TNT_SUBSIDIO_CREDITO_PRO_RATA
    );

 // Lista todos os tipo de natureza beneficio subsidio
    public static final List<String> TNT_TODOS_BENEFICIO_SUBSIDIO = Arrays.asList(
            TNT_SUBSIDIO_DEBITO_PRO_RATA,
            TNT_SUBSIDIO_CREDITO_PRO_RATA,
            TNT_SUBSIDIO_PLANO_SAUDE,
            TNT_SUBSIDIO_ODONTOLOGICO
    );

    // Lista dos tipo de natureza beneficio subsidio pro rata
    public static final List<String> TNT_BENEFICIO_RESIDUO = Arrays.asList(
            TNT_RESIDUO_PLANO_SAUDE,
            TNT_RESIDUO_ODONTOLOGICO
    );

    /* Constantes de status de parcela desconto */
    public static final String SPD_EMABERTO             = "1";
    //public static final String SPD_CANCELADA          = "2";
    //public static final String SPD_SUSPENSA           = "3";
    public static final String SPD_EMPROCESSAMENTO      = "4";
    public static final String SPD_REJEITADAFOLHA       = "5";
    public static final String SPD_LIQUIDADAFOLHA       = "6";
    public static final String SPD_LIQUIDADAMANUAL      = "7";
    public static final String SPD_SEM_RETORNO          = "8";
    public static final String SPD_AGUARD_PROCESSAMENTO = "9";

    /* Constantes de status de usuário */
    public static final String STU_ATIVO = "1";
    public static final String STU_BLOQUEADO = "2";
    public static final String STU_EXCLUIDO = "3";
    public static final String STU_BLOQUEADO_POR_CSE = "4";
    public static final String STU_BLOQUEADO_AUTOMATICAMENTE = "5";
    public static final String STU_BLOQUEADO_AUSENCIA_TEMPORARIA = "6";
    public static final String STU_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA = "7";
    public static final String STU_BLOQUEADO_AUTOMATICAMENTE_FIM_VIGENCIA = "8";
    public static final String STU_AGUARD_APROVACAO_CADASTRO = "9";

    // status de usuários inativos
    public static final List<String> STU_CODIGOS_INATIVOS = Arrays.asList(
            STU_BLOQUEADO,
            STU_BLOQUEADO_POR_CSE,
            STU_BLOQUEADO_AUTOMATICAMENTE,
            STU_BLOQUEADO_AUSENCIA_TEMPORARIA,
            STU_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA,
            STU_BLOQUEADO_AUTOMATICAMENTE_FIM_VIGENCIA
    );

    /* Anexo Consignataria */
    public static final Short AXC_ATIVO = 1;
    public static final Short AXC_INATIVO = 0;

    /* Vinculo Consignataria */
    public static final Short VCS_ATIVO = 1;
    public static final Short VCS_INATIVO = 0;

    /* Constantes de status de convenio */
    public static final String SCV_ATIVO = "1";
    public static final String SCV_INATIVO = "2";
    public static final String SCV_CANCELADO = "3";

    /* Constantes de status */
    public static final Short STS_INATIVO     = 0;
    public static final Short STS_ATIVO       = 1;
    public static final Short STS_INDISP      = 2;
    public static final Short STS_DESBLOQUEIO_PENDENTE = 3;
    public static final Short STS_INATIVO_CSE = 4;
    public static final Short STS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA = 7;

    /* Constantes de status de compra */
    public static final Short STC_AGUARD_INFO_SDV   = 1;
    public static final Short STC_AGUARD_PG_SDV     = 2;
    public static final Short STC_AGUARD_LIQUIDACAO = 3;
    public static final Short STC_LIQUIDADO         = 4;
    public static final Short STC_FINALIZADO        = 5;
    public static final Short STC_CANCELADO         = 6;
    public static final Short STC_AGUARD_APRV_SDV   = 7;

    /* Valores para os parâmetros do tipo "SN". */
    public static final String TPC_SIM   = "S";
    public static final String TPC_NAO   = "N";
    public static final String TPC_DIA   = "D";
    public static final String TPC_MES   = "M";
    public static final String TPC_TODOS = "T";

    public static final String TPC_AUTORIZA_SEM_CODIGO = "A";

    public static final String TPC_SALT = "XP";

    /*Tipo de captcha avançado.*/
    public static final String TPC_R = "R";
    public static final String TPC_H = "H";

    /*Tipo de valores de correção*/
    public static final String TIPO_CCR_VLR             = "0";
    public static final String TIPO_CCR_VLR_ACUMULADO   = "1";

    /* Tipos de reimplante automático. */
    public static final String TPC_TIPO_REIMPLANTE_1 = "1";
    public static final String TPC_TIPO_REIMPLANTE_2 = "2";

    /* Tipos de critério para alongamento de contratos */
    public static final String CRITERIO_ALONGAMENTO_MARGEM_NEGATIVA   = "1";
    public static final String CRITERIO_ALONGAMENTO_PARCELA_REJEITADA = "2";
    public static final String CRITERIO_ALONGAMENTO_PARCELA_REJEITADA_E_MARGEM_NEGATIVA = "3";
    public static final String CRITERIO_ALONGAMENTO_PARCELA_REJEITADA_OU_MARGEM_NEGATIVA = "4";

    /* Tipos de parâmetro de tarifação de consignante */
    public static final String TPT_VLR_INTERVENIENCIA = "1";

    /*Código de Consignatária não promovida*/
    public static final String CSA_NAO_PROMOVIDA = "999";

    /* Tipos de dado adicional */
    public static final String TDA_TIPO_CARTAO_CREDITO                 = "1";
    public static final String TDA_NUM_CARTAO_CREDITO                  = "2";
    public static final String TDA_SDV_DATACADASTRO                    = "3";
    public static final String TDA_SDV_QTDE_PRESTACOES                 = "4";
    public static final String TDA_SDV_VALOR_VCTO1                     = "5";
    public static final String TDA_SDV_DATA_VCTO1                      = "6";
    public static final String TDA_SDV_VALOR_VCTO2                     = "7";
    public static final String TDA_SDV_DATA_VCTO2                      = "8";
    public static final String TDA_SDV_VALOR_VCTO3                     = "9";
    public static final String TDA_SDV_DATA_VCTO3                      = "10";
    public static final String TDA_SDV_TEL_SERVIDOR                    = "11";
    public static final String TDA_PERCENTUAL_MINIMO_VIGENCIA_RENEG    = "12";
    public static final String TDA_PERCENTUAL_MINIMO_VIGENCIA_COMPRA   = "13";
    public static final String TDA_IDENTIFICADOR_RENEGOCIACAO          = "14";
    public static final String TDA_CODIGO_AUTORIZACAO_SOLICITACAO      = "15";
    public static final String TDA_SDV_EMAIL_SERVIDOR                  = "16";
    public static final String TDA_VALOR_RETIDO_REVISAO_MARGEM         = "17";
    public static final String TDA_VALOR_EXCEDENTE_MARGEM_PROPORCIONAL = "18";
    public static final String TDA_MODALIDADE_OPERACAO                 = "19";
    public static final String TDA_MATRICULA_SER_NA_CSA                = "20";
    public static final String TDA_NUM_CARTAO_PLANO_SAUDE              = "21";
    public static final String TDA_CPF_DEPENDENTE_PLANO_SAUDE          = "22";
    public static final String TDA_NUM_PORTABILIDADE_CIP               = "23";
    public static final String TDA_REATIVACAO_CONTRATO                 = "24";
    public static final String TDA_SOLICITACAO_TEL_SERVIDOR            = "25";
    public static final String TDA_CONFIRMACAO_DADOS_TEL_LEILAO        = "26";
    public static final String TDA_CONFIRMACAO_DADOS_DDD_TEL_LEILAO    = "27";
    public static final String TDA_CONFIRMACAO_DADOS_EMAIL_LEILAO      = "28";
    public static final String TDA_CHAVE_ASSINATURA_DOC_SOLICITACAO    = "29";
    public static final String TDA_VALOR_DESCONTO_SEMANAL              = "30";
    public static final String TDA_MOTIVO_DEPENDENCIA                  = "31";
    public static final String TDA_DATA_INICIO_DEPENDENCIA             = "32";
    public static final String TDA_DATA_FIM_DEPENDENCIA                = "33";
    public static final String TDA_BEN_ADESAO_PLANO_EX_FUNCIONARIO     = "34";
    public static final String TDA_BEN_PERIODO_CONTRIBUICAO_PLANO      = "35";
    public static final String TDA_BENEFICIARIO_CONTRIBUIU_PARA_PLANO  = "36";
    public static final String TDA_BENEFICIARIO_VALOR_DA_CONTRIBUICAO  = "37";
    public static final String TDA_RECUSA_CONFIRMACAO_DADOS_LEILAO     = "38";
    public static final String TDA_VALOR_MONETARIO_JUROS_EMPRESTIMO    = "39";
    public static final String TDA_SOLICITACAO_EMAIL_SERVIDOR          = "40";
    public static final String TDA_SOLICITACAO_OBS_SERVIDOR            = "41";
    public static final String TDA_CONFIRMOU_AUTORIZACAO_DESCONTO      = "42";
    public static final String TDA_AUTORIZA_DESCONTO                   = "43";
    public static final String TDA_INDICE_ANTERIOR                     = "44";
    public static final String TDA_DATA_HORA_VALIDACAO_KYC             = "45";
    public static final String TDA_AFETADA_DECISAO_JUDICIAL            = "46";
    public static final String TDA_CIENTE_KYC_NAO_FINALIZADO           = "47";
    public static final String TDA_NOME_ESTABELECIMENTO_CARTAO         = "48";
    public static final String TDA_INFO_ESTABELECIMENTO_CARTAO         = "49";
    public static final String TDA_VALOR_PARCELA_ANTERIOR_ALT_MULTIPLA = "50";
    public static final String TDA_NUMERO_PROPOSTA_SEGURO              = "51";
    public static final String TDA_DATA_HORA_LEITURA_TERMO             = "52";
    public static final String TDA_IP_LEITURA_TERMO                    = "53";
    public static final String TDA_BENEFICIARIO_DEPENTENTE             = "54";
    public static final String TDA_FORMA_PAGAMENTO                     = "55";
    public static final String TDA_DATA_ESTABILIZACAO                  = "56";
    public static final String TDA_ANO_PRIMEIRO_EMPREGO                = "57";
    public static final String TDA_LICENCAS                            = "58";
    public static final String TDA_MOTIVO_LICENCAS_SOFRIDAS            = "59";
    public static final String TDA_DURACAO_LICENCAS_SOFRIDAS           = "60";
    public static final String TDA_TIPO_APOSENTADORIA_QUE_TERA         = "61";
    public static final String TDA_IDADE_DATA_ESTIMADA_APOSENTADORIA   = "62";
    public static final String TDA_TEMPO_CONTRIBUICAO_APOSENTADORIA    = "63";
    public static final String TDA_ANO_PREVISTO_APOSENTADORIA          = "64";
    public static final String TDA_SALARIO_FIXO_ANUAL_BRUTO            = "65";
    public static final String TDA_SALARIO_MENSAL_BRUTO                = "66";
    public static final String TDA_DATA_INSCRICAO_FUNDO_PENSAO         = "67";
    public static final String TDA_VALOR_PAGO_FUNDO_PENSAO             = "68";
    public static final String TDA_VALOR_PAGO_IMPOSTO_RENDA            = "69";
    public static final String TDA_VALOR_CONTRIBUICOES_SINDICAIS       = "70";
    public static final String TDA_VALOR_PENSAO_ALIMENTICIA            = "71";
    public static final String TDA_VALOR_OUTRAS_DEDUCOES_JUDICIAIS     = "72";
    public static final String TDA_VALOR_OUTRAS_CONSIGNACOES           = "73";
    public static final String TDA_VALOR_OUTROS_EMPRESTIMOS            = "74";
    public static final String TDA_SALARIO_LIQUIDO_MATRICULA           = "75";
    public static final String TDA_BANCO_CREDITO_VALOR_OUTRAS_ADE      = "76";
    public static final String TDA_DURACAO_CREDITO_VALOR_OUTRAS_ADE    = "77";
    public static final String TDA_QTDE_PARCELAS_RESTANTES_OUTRAS_ADE  = "78";
    public static final String TDA_VALOR_PARCELA_CREDITO_OUTRAS_ADE    = "79";
    public static final String TDA_SALDO_DEVEDOR_OUTRAS_ADE            = "80";
    public static final String TDA_BANCO_CREDITO_OUTROS_EMPRESTIMOS    = "81";
    public static final String TDA_SALDO_DEVEDOR_OUTROS_EMPRESTIMOS    = "82";
    public static final String TDA_VLR_REMUNERACAO_TEMPO_CONTRIBUICAO  = "83";
    public static final String TDA_NOME_FUNDO_COMPLEMENTAR             = "84";
    public static final String TDA_CUSTODIANTE_FUNDO_GARANTIA          = "85";
    public static final String TDA_TEM_ANTECIPACAO_FUNDO_GARANTIA      = "86";
    public static final String TDA_MARGEM_LIMITE_DECISAO_JUDICIAL      = "87";
    public static final String TDA_NRO_ADE_ANTERIOR_REIMPLANTE         = "88";
    public static final String TDA_DATA_NOVOS_SERVIDORES_APOSENTADOS   = "89";
    public static final String TDA_TRANSFERENCIA_TAXA                  = "90";
    public static final String TDA_TRANSFERENCIA_VALOR_CREDITADO       = "91";
    public static final String TDA_NOME_LOCADOR                        = "92";
    public static final String TDA_CPF_CNPJ_LOCADOR                    = "93";
    public static final String TDA_ENDERECO_LOCADOR                    = "94";

    /* Valores para os parâmetros do tipo "SN". */
    public static final String TDA_SIM = "S";
    public static final String TDA_NAO = "N";

    /* Valores para o TDA_FORMA_PAGAMENTO. */
    public static final String FORMA_PAGAMENTO_FOLHA = "F";
    public static final String FORMA_PAGAMENTO_BOLETO = "B";

    /* Valores para o TDA_MOTIVO_LICENCAS_SOFRIDAS. */
    public static final String TDA_LICENCAS_NAO_HOUVE = "0";
    public static final String TDA_LICENCAS_HOUVE = "1";

    /* Valores para o TDA_MOTIVO_LICENCAS_SOFRIDAS. */
    public static final String TDA_MOTIVO_LICENCAS_SOFRIDAS_DOENCA = "M";
    public static final String TDA_MOTIVO_LICENCAS_SOFRIDAS_LESAO = "I";
    public static final String TDA_MOTIVO_LICENCAS_SOFRIDAS_GRAVIDEZ = "G";
    public static final String TDA_MOTIVO_LICENCAS_SOFRIDAS_PUERPERIO = "P";
    public static final String TDA_MOTIVO_LICENCAS_SOFRIDAS_SERVICO_MILITAR = "S";
    public static final String TDA_MOTIVO_LICENCAS_SOFRIDAS_OUTRO = "A";

    /* Valores para o TDA_TIPO_APOSENTADORIA_QUE_TERA. */
    public static final String TDA_TIPO_APOSENTADORIA_PENSAO = "P";
    public static final String TDA_TIPO_APOSENTADORIA_LIQUIDACAO = "L";

    /* Valores para o TDA_CUSTODIANTE_FUNDO_GARANTIA */
    public static final String TDA_CUSTODIANTE_FUNDO_GARANTIA_ATC = "A";
    public static final String TDA_CUSTODIANTE_FUNDO_GARANTIA_INPS = "I";
    public static final String TDA_CUSTODIANTE_FUNDO_GARANTIA_COMPLEMENTAR = "C";

    /* Valores para o TDA_TEM_ANTECIPACAO_FUNDO_GARANTIA */
    public static final String TDA_TEM_ANTECIPACAO_FUNDO_GARANTIA_NAO = "0";
    public static final String TDA_TEM_ANTECIPACAO_FUNDO_GARANTIA_SIM = "1";

    /* Bases de cálculo para parâmetros de tarifação de consignante */
    public static final String PCV_BASE_PARCELA_VERBA = "1";
    public static final String PCV_BASE_PARCELA       = "2";

    /* Formas de cálculo para parâmetros de tarifação de consignante */
    public static final String PCV_FORMA_VLR_FIXO     = "1";
    public static final String PCV_FORMA_PERCENTUAL   = "2";

    // status notificação de dispositivo
    public static final Short NDI_ATIVO     = 1;
    public static final Short NDI_INATIVO   = 0;

    // Código na tabela tb_tipo_param_sist_consignante
    //public static final String TPC_PODE_MOSTRAR_MARGEM_CSA                                 = "1";
    //public static final String TPC_PERIODO_ATUAL                                           = "2";
    public static final String TPC_DIR_RAIZ_ARQUIVOS                                         = "5";
    public static final String TPC_ARQ_CONF_ENTRADA_IMP_MARGEM                               = "6";
    public static final String TPC_ARQ_CONF_SAIDA_IMP_MARGEM                                 = "7";
    public static final String TPC_ARQ_CONF_TRADUTOR_IMP_MARGEM                              = "8";
    public static final String TPC_DIA_PREVISAO_RETORNO                                      = "9";
    public static final String TPC_DIA_CORTE                                                 = "10";
    public static final String TPC_PRAZO_EXPIRACAO_SENHA_USU_CSE_ORG                         = "11";
    public static final String TPC_ARQ_CONF_ENTRADA_EXP_MOV_FIN                              = "12";
    public static final String TPC_ARQ_CONF_SAIDA_EXP_MOV_FIN                                = "13";
    public static final String TPC_ARQ_CONF_TRADUTOR_EXP_MOV_FIN                             = "14";
    public static final String TPC_ARQ_CONF_ENTRADA_IMP_RETORNO                              = "15";
    public static final String TPC_REQUER_MATRICULA_E_CPF_CSA                                = "16";
    //public static final String TPC_EST_DEFAULT                                             = "17";
    //public static final String TPC_ORG_DEFAULT                                             = "18";
    public static final String TPC_CONSOLIDA_DESCONTOS_MOVIMENTO                             = "19";
    public static final String TPC_IMP_MARGEM_TOTAL                                          = "20";
    public static final String TPC_MATRICULA_NUMERICA                                        = "21";
    public static final String TPC_TAMANHO_MATRICULA                                         = "22";
    public static final String TPC_ZERA_MARGEM_USADA                                         = "23";
    public static final String TPC_SIMULACAO_CONSIGNACAO                                     = "24";
    public static final String TPC_SER_SENHA_DEFERE_RESERVA                                  = "26";
    //public static final String TPC_MOSTRA_MARGEM_SERVIDOR                                  = "27";
    public static final String TPC_EXPORTACAO_APENAS_INICIAL                                 = "28";
    public static final String TPC_SENHA_EXTERNA                                             = "29";
    public static final String TPC_LIMITE_CONSULTAS_MARGEM                                   = "30";
    public static final String TPC_SERVIDOR_CADASTRA_SENHA                                   = "31";
    public static final String TPC_DIA_INI_FECHAMENTO_SIST                                   = "32";
    public static final String TPC_DIA_FIN_FECHAMENTO_SIST                                   = "33";
    public static final String TPC_MOSTRA_NOME_CSE_LOGIN                                     = "34";
    public static final String TPC_NOME_SISTEMA                                              = "35";
    //public static final String TPC_PODE_MOSTRAR_MARGEM_CSE                                 = "36";
    public static final String TPC_ARQ_CONF_TRADUTOR_IMP_RETORNO                             = "37";
    public static final String TPC_CANC_AUT_DIARIO_CONSIGNACOES                              = "38";
    //public static final String TPC_ADE_UNICA_POR_RSE_CSA                                   = "39";
    public static final String TPC_EXP_MOV_POR_ESTABELECIMENTO                               = "40";
    public static final String TPC_ARQ_CONF_ENTRADA_IMP_CRITICA                              = "41";
    public static final String TPC_ARQ_CONF_TRADUTOR_IMP_CRITICA                             = "42";
    public static final String TPC_CANC_AUT_DIARIO_SOLICITACOES                              = "43";
    //public static final String TPC_LOG_NIVEL_SEVERIDADE                                    = "44";
    //public static final String TPC_PARAM_CNV_POR_ORGAO                                     = "45";
    //public static final String TPC_VERSAO_EMPRESA_PRIVADA                                  = "46";
    //public static final String TPC_PODE_MOSTRAR_MARGEM_ORGAO                               = "47";
    //public static final String TPC_ATUALIZAR_SISTEMA                                       = "48";
    //public static final String TPC_INTRANET                                                = "49";
    //public static final String TPC_SINC_AUTOMATICA                                         = "50";
    //public static final String TPC_DATA_CORTE_SINC                                         = "51";
    //public static final String TPC_AVERBA_EMAIL                                            = "52";
    //public static final String TPC_DATA_CORTE_SINC_OCP                                     = "53";
    public static final String TPC_ARQ_CONF_ENTRADA_EXP_TRANSF                               = "54";
    public static final String TPC_ARQ_CONF_TRADUTOR_EXP_TRANSF                              = "55";
    public static final String TPC_GERA_ARQUIVO_TRANSFERIDOS                                 = "56";
    public static final String TPC_ARQ_CONF_SAIDA_EXP_TRANSF                                 = "57";
    public static final String TPC_ARQ_CONF_ENTRADA_IMP_TRANSF                               = "58";
    public static final String TPC_ARQ_CONF_TRADUTOR_IMP_TRANSF                              = "59";
    public static final String TPC_VALIDA_CPF_PESQ_SERVIDOR                                  = "60";
    //public static final String TPC_PAGINA_LOGIN                                            = "61";
    public static final String TPC_LIB_MARGEM_CONCLUSAO_CONTRATO                             = "62";
    public static final String TPC_FOLHA_ACEITA_ALTERACAO                                    = "63";
    public static final String TPC_PER_CAD_INF_FINANCEIRAS                                   = "64";
    //public static final String TPC_CAD_LOJA_OPERADOR                                       = "65";
    public static final String TPC_REIMPLANTACAO_AUTOMATICA                                  = "66";
    public static final String TPC_CSA_ALTERA_REIMPLANTACAO                                  = "67";
    public static final String TPC_PRESERVA_PRD_REJEITADA                                    = "68";
    public static final String TPC_CSA_ALTERA_PRESERVA_PRD                                   = "69";
    //public static final String TPC_VLR_IOF_DIA                                             = "70";
    //public static final String TPC_VLR_IOF_ANO                                             = "71";
    //public static final String TPC_IDENTIFICADOR_MAQ                                       = "72";
    public static final String TPC_REDUCAO_VLR_ADE_MARGEM_NEG                                = "73";
    //public static final String TPC_MOSTRA_MARGEM_NEG_SER                                   = "74";
    public static final String TPC_PERMITE_CAD_INDICE                                        = "75";
    //public static final String TPC_ATIVAR_CONTRATO                                         = "76";
    public static final String TPC_INDICE_NUMERICO                                           = "77";
    public static final String TPC_INDICE_REPETIDO                                           = "78";
    public static final String TPC_INDICE_PADRAO                                             = "79";
    public static final String TPC_CONSOLIDA_EXC_INC_COMO_ALT                                = "80";
    public static final String TPC_CLASSE_EXPORTADOR                                         = "81";
    public static final String TPC_DIR_IMG_SERVIDORES                                        = "82";
    //public static final String TPC_RETEM_MARGEM_SEM_RETORNO                                = "83";
    public static final String TPC_SET_PERIODO_EXP_MOV_MES                                   = "84";
    public static final String TPC_PERMITE_MSG_MARGEM_COMPROMET                              = "85";
    public static final String TPC_PRESERVA_EST_MATR_TRANSFER                                = "86";
    public static final String TPC_PESQUISA_MATRICULA_INTEIRA                                = "87";
    public static final String TPC_ATUALIZA_SENHA_EXTERNA                                    = "88";
    //public static final String TPC_PAGINA_LOGIN_SERVIDOR                                   = "89";
    public static final String TPC_ALTERA_MSG_BLOQUEIO_SISTEMA                               = "90";
    public static final String TPC_MARGEM_1_CASADA_MARGEM_3                                  = "91";
    //public static final String TPC_SALTO_DINAMICO                                          = "92";
    //public static final String TPC_BUSCA_BOLETO_EXTERNO                                    = "93";
    public static final String TPC_MSG_USUARIO_BLOQUEADO                                     = "94";
    //public static final String TPC_PRESERVA_MARGEM_ADE_SEM_RET                             = "95";
    public static final String TPC_EXPORTA_LIQCANC_NAO_PAGAS                                 = "96";
    public static final String TPC_CARENCIA_CONCLUSAO_FOLHA                                  = "97";
    public static final String TPC_PER_CAD_TAXA_JUROS                                        = "98";
    //public static final String TPC_PER_LIQ_ADE_BLOQ_SERVIDOR                               = "99";
    public static final String TPC_PERMITE_PRIORIZAR_VERBA                                   = "100";
    public static final String TPC_VLR_PADRAO_MINIMO_CONTRATO                                = "101";
    public static final String TPC_VLR_PADRAO_MAXIMO_CONTRATO                                = "102";
    public static final String TPC_SENHA_SER_ACESSAR_CONT_CSAS                               = "103";
    //public static final String TPC_VLR_MAX_RENEG_IGUAL_SOMA_CONTRATOS                      = "104";
    public static final String TPC_SENHA_SER_ACESSAR_CONS_MARGEM                             = "105";
    public static final String TPC_TAMANHO_MATRICULA_MAX                                     = "106";
    public static final String TPC_QTDE_MAX_ADE_CNV_RSE                                      = "107";
    public static final String TPC_QTDE_MAX_CSA_FAZER_CONTRATO                               = "108";
    public static final String TPC_SERVIDOR_POSSUI_MATRICULA                                 = "109";
    public static final String TPC_URL_VALIDACAO_EXTERNA_LOGIN                               = "110";
    public static final String TPC_COLOCA_ESTOQUE_ADE_NAO_PAGA                               = "111";
    //public static final String TPC_PAG_TOPO_CONSIGNANTE                                    = "112";
    //public static final String TPC_PAG_TOPO_CONSIGNATARIA                                  = "113";
    //public static final String TPC_PAG_TOPO_SERVIDOR                                       = "114";
    //public static final String TPC_PAG_LOGIN_SERVIDOR                                      = "115";
    public static final String TPC_TARIFACAO_CONSOLIDADA                                     = "116";
    public static final String TPC_UTILIZA_BLOQ_VINC_SERVIDOR                                = "118";
    //public static final String TPC_TIPO_REIMPLANTE_AUTOMATICO                              = "119";
    public static final String TPC_CSE_CONFIG_PARAM_SVC_CSA                                  = "120";
    //public static final String TPC_CONSOLIDA_MOV_FIN_VERBA                                 = "121";
    //public static final String TPC_VALIDA_JUROS_CADASTRADO                                 = "122";
    public static final String TPC_DIA_PAGTO_PRIMEIRA_PARCELA                                = "123";
    public static final String TPC_EXIGE_TIPO_MOTIVO_CANC                                    = "124";
    public static final String TPC_ARQ_CONF_SAIDA_REL_INTEGRACAO_RETORNO                     = "125";
    public static final String TPC_ARQ_CONF_TRADUTOR_REL_INTEGRACAO_RETORNO                  = "126";
    public static final String TPC_CONFIRMA_ADE_SEM_MARGEM                                   = "127";
    public static final String TPC_CONCLUI_NAO_PAGAS                                         = "128";
    public static final String TPC_CSA_ALTERA_CONCLUSAO_NAO_PAGAS                            = "129";
    public static final String TPC_PERMITE_PRIORIZAR_SERVICO                                 = "130";
    public static final String TPC_MOSTRA_COMPOSICAO_MARGEM                                  = "131";
    public static final String TPC_ENVIA_CONCLUSAO_FOLHA                                     = "132";
    public static final String TPC_DIAS_SET_NOVO_PERIODO_EXP                                 = "133";
    public static final String TPC_DEFAULT_PARAM_SVC_REIMPLANTE                              = "134";
    public static final String TPC_DEFAULT_PARAM_SVC_PRESERVA_PRD                            = "135";
    public static final String TPC_DEFAULT_PARAM_SVC_CONCLUI_NAO_PG                          = "136";
    public static final String TPC_EXIGE_OBSERVACAO_BLOQUEAR_CSA                             = "137";
    public static final String TPC_QTDE_MAX_DIAS_PERIODO_EXP                                 = "138";
    public static final String TPC_QTDE_MIN_DIAS_PERIODO_EXP                                 = "139";
    public static final String TPC_SIMULACAO_POR_TAXA_JUROS                                  = "140";
    //public static final String TPC_PODE_MOSTRAR_MARGEM_2_CSA                               = "141";
    //public static final String TPC_PODE_MOSTRAR_MARGEM_3_CSA                               = "142";
    //public static final String TPC_MOSTRA_MARGEM_NEG_SER_ORG                               = "143";
    //public static final String TPC_MOSTRA_MARGEM_NEG_SER_CSA                               = "144";
    //public static final String TPC_GERA_RELATORIO_MOV_ORGAOS                               = "145";
    public static final String TPC_ENVIA_CONTRATO_RSE_EXCLUIDO                               = "146";
    public static final String TPC_URL_CENTRALIZADOR                                         = "147";
    public static final String TPC_KEYSTORE_FILE                                             = "148";
    public static final String TPC_KEYSTORE_PASSWORD                                         = "149";
    public static final String TPC_GRUPO_CONSIGNATARIA                                       = "150";
    public static final String TPC_LIMITE_PROCESSA_LOTE                                      = "151";
    public static final String TPC_INF_MAT_CPF_EDT_CONSIGNACAO                               = "152";
    public static final String TPC_TEM_CONTROLE_DE_ESTOQUE                                   = "153";
    public static final String TPC_TEM_CONTROLE_DE_COMPULSORIOS                              = "154";
    //public static final String TPC_CONSOLIDA_RENEGOCIACAO_MOV_FIN                          = "155";
    public static final String TPC_LIMITE_CONSULTA_MARGEM_POR_USUARIO                        = "156";
    public static final String TPC_QTD_DIAS_LIMITE_CONSULTA_MARGEM                           = "157";
    public static final String TPC_IMPORTA_SEM_PROC_APENAS_SER_ATIVO                         = "158";
    public static final String TPC_IMPORTA_SEM_PROC_APENAS_CSA_ATIVA                         = "159";
    public static final String TPC_CAMPO_OBRIG_ALT_SENHA_USU_SERVIDOR                        = "160";
    public static final String TPC_SENHA_SER_ACESSAR_CONS_MARGEM_CSE                         = "161";
    public static final String TPC_REQUER_MATRICULA_E_CPF_CSE                                = "162";
    public static final String TPC_ARQ_CONF_ENTRADA_IMP_MOV_FIN                              = "163";
    public static final String TPC_ARQ_CONF_TRADUTOR_IMP_MOV_FIN                             = "164";
    public static final String TPC_ARQ_CONF_SAIDA_IMP_MOV_FIN                                = "165";
    //public static final String TPC_UTILIZA_FILTRO_COMPOSICAO_MARGEM                        = "166";
    public static final String TPC_MANTEM_ARMAZENADO_ARQ_RETORNO                             = "167";
    public static final String TPC_CSE_ORG_PWD_STRENGTH_LEVEL                                = "168";
    public static final String TPC_CSA_COR_PWD_STRENGTH_LEVEL                                = "169";
    public static final String TPC_SER_PWD_STRENGTH_LEVEL                                    = "170";
    public static final String TPC_QTDE_COLUNAS_SIMULACAO                                    = "171";
    public static final String TPC_MOSTRA_CNV_BLOQ_CONSULTA_MARGEM                           = "172";
    public static final String TPC_MARGEM_1_2_3_CASADAS                                      = "173";
    public static final String TPC_SENHA_EXP_SERVIDOR_RESERVA_MARGEM                         = "174";
    public static final String TPC_RECALCULA_MARGEM_IMP_RETORNO                              = "175";
    public static final String TPC_RECALCULA_MARGEM_CONCLUSAO_RETORNO                        = "176";
    public static final String TPC_RECALCULA_MARGEM_IMP_MARGEM                               = "177";
    public static final String TPC_RECALCULA_MARGEM_IMP_TRANSFERIDOS                         = "178";
    public static final String TPC_LIMITE_MAX_INDICE                                         = "179";
    public static final String TPC_USA_TAXA_CSA_CORRECAO_VLR_PRESENTE                        = "180";
    public static final String TPC_OBRIG_INF_TODOS_DADOS_EDT_SERVIDOR                        = "181";
    public static final String TPC_EXPORTA_LIQ_INDEPENDENTE_ANO_MES_FIM                      = "182";
    public static final String TPC_VALIDA_EXP_SENHA_SER_ACESSO_SIST                          = "183";
    public static final String TPC_EXPORTA_ADE_MENORES_MINIMO_SVC                            = "184";
    public static final String TPC_GERA_RELATORIO_REPASSE                                    = "185";
    public static final String TPC_PERMITE_IMPORTACAO_AUTOM_SEM_PROC                         = "186";
    public static final String TPC_PERMITE_EDITAR_CPF_SERVIDOR                               = "187";
    public static final String TPC_UTILIZA_CNV_COD_VERBA_REF                                 = "188";
    public static final String TPC_PERMITE_QUALQUER_PRAZO_ALTERACAO_ADE                      = "189";
    public static final String TPC_LIMPA_DIRETORIO_IMPORTACAO_RETORNO                        = "190";
    public static final String TPC_TEM_COMPARTILHAMENTO_TAXAS_POR_SVC                        = "191";
    public static final String TPC_QTDE_DIAS_LIMITE_MIN_PEX_DATA_FIM                         = "192";
    public static final String TPC_TEM_ALONGAMENTO_CONTRATO                                  = "193";
    public static final String TPC_CRITERIO_ALONGAMENTO_CONTRATO                             = "194";
    public static final String TPC_IGNORA_SERVIDORES_EXCLUIDOS                               = "195";
    //public static final String TPC_PODE_COMPRAR_APENAS_CONTRATOS_PAGOS                     = "196";
    //public static final String TPC_PERCENTUAL_MINIMO_PRD_PAGAS_COMPRA                      = "197";
    public static final String TPC_LIMITE_MENSAGEM                                           = "198";
    public static final String TPC_PERMITE_CNV_COD_VERBA_VAZIO                               = "199";
    public static final String TPC_CALCULA_SALDO_DEV_IMPORTADOS                              = "200";
    //public static final String TPC_PODE_MOSTRAR_MARGEM_EXTRA_CSA                           = "201";
    //public static final String TPC_MASCARA_MATRICULA                                       = "202";
    public static final String TPC_TAMANHO_MIN_SENHA_SERVIDOR                                = "203";
    public static final String TPC_TAMANHO_MAX_SENHA_SERVIDOR                                = "204";
    public static final String TPC_PODE_REPETIR_COD_VERBA                                    = "205";
    public static final String TPC_LIMITE_CONSULTAS_MARGEM_GLOBAL                            = "206";
    public static final String TPC_EMAIL_SUPORTE_ZETRASOFT                                   = "207";
    public static final String TPC_MENSAGEM_ERRO_LOGIN                                       = "208";
    public static final String TPC_MENSAGEM_ERRO_LOGIN_SERVIDOR                              = "209";
    public static final String TPC_MASCARA_LOGIN_SERVIDOR                                    = "210";
    public static final String TPC_CANCELA_COMPRA_CONCLUSAO_COMPRADO                         = "211";
    public static final String TPC_ALIQUOTA_ANUAL_IOF                                        = "212";
    public static final String TPC_ALIQUOTA_ADICIONAL_IOF                                    = "213";
    public static final String TPC_MASCARA_LOGIN_EXTERNO_SERVIDOR                            = "214";
    public static final String TPC_CLASSE_GERADOR_ADE_NUMERO                                 = "215";
    public static final String TPC_IGNORA_CONTRATOS_A_CONCLUIR                               = "216";
    public static final String TPC_CADASTRO_CPF_OBRIGATORIO_USUARIO                          = "217";
    public static final String TPC_MARGEM_1_CASADA_MARGEM_3_ESQUERDA                         = "218";
    public static final String TPC_MARGEM_1_2_3_CASADAS_PELA_ESQUERDA                        = "219";
    public static final String TPC_PERMITE_BLOQUEIO_ACESSO_POR_IP                            = "220";
    public static final String TPC_PERMITE_BLOQUEIO_ACESSO_POR_DNS                           = "221";
    public static final String TPC_EXIGE_CADASTRO_IP_CSA_COR                                 = "222";
    public static final String TPC_EXIGE_CADASTRO_IP_CSE_ORG                                 = "223";
    public static final String TPC_VERIFICA_CADASTRO_IP_CSA_COR                              = "224";
    public static final String TPC_VERIFICA_CADASTRO_IP_CSE_ORG                              = "225";
    //public static final String TPC_MINIMO_PRD_PAGAS_COMPRA                                 = "226";
    public static final String TPC_TEM_TERMO_ADESAO                                          = "227";
    public static final String TPC_TEM_CET                                                   = "228";
    public static final String TPC_BLOQUEIA_COMPRA_PROPRIO_CONTRATO                          = "229";
    public static final String TPC_EXIGE_MULTIPLOS_SALDOS_DEVEDORES                          = "230";
    //public static final String TPC_QTD_MESES_VIGENCIA_COMPRA                               = "231";
    public static final String TPC_REQUER_TEL_SER_SOLIC_SALDO_DEVEDOR                        = "232";
    public static final String TPC_CONTROLE_DETALHADO_PROCESSO_COMPRA                        = "233";
    public static final String TPC_USA_DIAS_UTEIS_CONTROLE_COMPRA                            = "234";
    public static final String TPC_USA_DIAS_UTEIS_CANC_AUTOMATICO_ADE                        = "235";
    public static final String TPC_DESBL_AUTOMATICO_CSA_CONTROLE_COMPRA                      = "236";
    public static final String TPC_EXIBE_CAPTCHA_TELA_LOGIN                                  = "237";
    public static final String TPC_EXIBE_CAPTCHA_TELA_LOGIN_SERVIDOR                         = "238";
    public static final String TPC_EMAIL_ALTERACAO_SENHA_USUARIO_SER                         = "239";
    public static final String TPC_MOSTRA_VARIACAO_MARGEM_CSE_ORG                            = "240";
    public static final String TPC_PERC_MAX_VAR_SER_ATIVO_CAD_MARGENS                        = "241";
    public static final String TPC_PERC_MAX_VAR_MARGEM_MAIOR_CAD_MARGENS                     = "242";
    public static final String TPC_PERMITE_PERFIL_PERSONALIZADO                              = "243";
    public static final String TPC_QTDE_MAX_CSA_POR_COMPRA                                   = "244";
    public static final String TPC_PERMITE_CRIAR_SERVIDOR_TRANSFERIDO                        = "245";
    public static final String TPC_ALTERA_COD_VERBA_COM_ADE_ATIVOS                           = "246";
    public static final String TPC_EXIBE_CONF_DADOS_SER_SIMULADOR                            = "247";
    public static final String TPC_EXIBE_BANNER_SERVIDOR                                     = "248";
    public static final String TPC_TAM_MAX_ARQ_BANNER                                        = "249";
    public static final String TPC_EXIBE_TCLD_VIRTUAL_LOGIN_SERVIDOR                         = "250";
    public static final String TPC_USA_ANO_365_DIAS_CALCULO_JUROS                            = "251";
    //public static final String TPC_COMANDO_EXTERNO_UPLOAD_ARQ_MARGEM                       = "252";
    //public static final String TPC_COMANDO_EXTERNO_UPLOAD_ARQ_RETORNO                      = "253";
    public static final String TPC_PERMITE_COMPRA_CONTRATO                                   = "254";
    //public static final String TPC_VLR_MAX_COMPRA_IGUAL_SOMA_CONTRATOS                     = "255";
    public static final String TPC_LINK_ACESSO_SISTEMA                                       = "256";
    public static final String TPC_DATA_IMPLANTACAO_SISTEMA                                  = "257";
    public static final String TPC_TELEFONE_SUPORTE_ZETRASOFT                                = "258";
    public static final String TPC_TAM_MAX_ARQ_ANEXO_CONTRATO                                = "259";
    public static final String TPC_QTE_MAX_ARQ_ANEXO_CONTRATO                                = "260";
    public static final String TPC_NUM_MAX_TENTATIVA_LOGIN                                   = "261";
    public static final String TPC_SIMULADOR_AGRUPADO_NATUREZA_SERVICO                       = "262";
    public static final String TPC_EXIBE_USUARIOS_EXCLUIDOS                                  = "263";
    public static final String TPC_EXIGE_CERTIFICADO_DIGITAL_CSA_COR                         = "264";
    public static final String TPC_PERMITE_CSA_OPTAR_CERTIF_DIGITAL                          = "265";
    public static final String TPC_EXIGE_CERTIFICADO_DIGITAL_CSE_ORG                         = "266";
    public static final String TPC_CADASTRO_EMPRESA_CORRESPONDENTE                           = "267";
    public static final String TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR                       = "268";
    public static final String TPC_DEFERIMENTO_CONSOME_SENHA_AUT_DESC                        = "269";
    public static final String TPC_LIMITE_ASSOCIACOES_POR_EMPRESA_COR                        = "270";
    public static final String TPC_SEPARA_LINHAS_MAPEAMENTO_MULTIPLO                         = "271";
    public static final String TPC_PERMITE_SENHAS_SERVIDOR_IGUAIS                            = "272";
    public static final String TPC_QTD_DIAS_VALIDADE_SENHA_AUTORIZACAO                       = "273";
    public static final String TPC_EXIBE_CONFIG_SISTEMA_TELA_PRINCIPAL                       = "274";
    public static final String TPC_CICLO_VIDA_FIXO_PROCESSO_COMPRA                           = "275";
    public static final String TPC_PERMITE_LOGIN_USU_COR_ENTIDADE_BLOQ                       = "276";
    public static final String TPC_ARQ_CONF_ENTRADA_IMP_BLOQ_SERVIDOR                        = "277";
    public static final String TPC_ARQ_CONF_TRADUTOR_IMP_BLOQ_SERVIDOR                       = "278";
    public static final String TPC_CLASSE_IMPORTADOR_RETORNO                                 = "279";
    public static final String TPC_CLASSE_IMPORTADOR_MARGEM                                  = "280";
    public static final String TPC_DESBL_AUTOMATICO_CSA_SOLICIT_SALDO                        = "282";
    public static final String TPC_USA_DIAS_UTEIS_SOLICIT_SALDO_DEVEDOR                      = "283";
    public static final String TPC_INF_SALDO_DEVEDOR_OPCIONAL_COMPRA                         = "284";
    public static final String TPC_TEM_PROCESSAMENTO_FERIAS                                  = "285";
    public static final String TPC_LIB_MARGEM_LIQ_CONTRATO_NAO_PAGO                          = "286";
    public static final String TPC_IPS_LIBERADOS_PAGINA_ADMINISTRACAO                        = "287";
    public static final String TPC_DADOS_SERVIDOR_EMAIL_ALTERACAO_SENHA                      = "288";
    public static final String TPC_LIB_MARGEM_CONCLUSAO_CONTRATO_FERIAS                      = "289";
    public static final String TPC_QTDE_SENHAS_ANT_USU_CSE_NAO_REPETE                        = "290";
    public static final String TPC_QTDE_SENHAS_ANT_USU_CSA_NAO_REPETE                        = "291";
    public static final String TPC_QTDE_SENHAS_ANT_USU_SER_NAO_REPETE                        = "292";
    public static final String TPC_QTDE_DIAS_BLOQ_USU_CSE_SEM_ACESSO                         = "293";
    public static final String TPC_QTDE_DIAS_BLOQ_USU_CSA_SEM_ACESSO                         = "294";
    public static final String TPC_QTDE_DIAS_BLOQ_USU_SER_SEM_ACESSO                         = "295";
    public static final String TPC_PERMITE_LIQ_CICLO_VIDA_FIXO_CSA_COR                       = "296";
    public static final String TPC_BLOQ_ACESSO_SIMULTANEO_USUARIO                            = "297";
    public static final String TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID                         = "298";
    public static final String TPC_IMPEDE_CPF_IGUAL_ENTRE_USU_CSA_E_SER                      = "299";
    public static final String TPC_IMPEDE_CPF_IGUAL_ENTRE_USU_CSA                            = "300";
    public static final String TPC_EXIBE_MARGEM_INT_XML_TOKEN_SER                            = "301";
    public static final String TPC_PRAZO_EXPIRACAO_SENHA_USU_CSA_COR                         = "302";
    public static final String TPC_PRAZO_EXPIRACAO_SENHA_USU_SER                             = "303";
    public static final String TPC_RANKING_ORDENACAO_SEQUENCIAL                              = "304";
    public static final String TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSE_ORG                           = "305";
    public static final String TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSA_COR                           = "306";
    //public static final String TPC_PERCENTUAL_MINIMO_VIGENCIA_COMPRA                       = "307";
    public static final String TPC_DIAS_APOS_LIQUI_PARA_RENEG_VIA_LOTE                       = "308";
    public static final String TPC_QTDE_DIAS_DIFERENCA_DATAS_RELATORIO                       = "309";
    //public static final String TPC_SER_ENVIA_TICKET_APENAS_CSA_COM_ADE                     = "310";
    public static final String TPC_CRIA_CNV_TRANSFERENCIA_SERVIDOR                           = "311";
    public static final String TPC_TRANSF_ADE_SVC_MSM_NATUREZA_TRANSF_SER                    = "312";
    public static final String TPC_SER_ENVIA_CMN_APENAS_CSA_COM_ADE                          = "313";
    public static final String TPC_QTDE_DIAS_BLOQ_CSA_CMN_SEM_RESPOSTA_SER                   = "314";
    public static final String TPC_DESBLOQ_AUT_CSA_CMN_SEM_RESPOSTA                          = "315";
    public static final String TPC_REQUER_TEL_SER_SOLIC_EMPRESTIMO                           = "316";
    public static final String TPC_EXIBE_MARGEM_SERVIDOR_TELA_PRINCIPAL                      = "317";
    public static final String TPC_USA_DIAS_UTEIS_CMN_PENDENTE                               = "318";
    public static final String TPC_TAMANHO_MIN_SENHA_AUT_SERVIDOR                            = "319";
    public static final String TPC_TAMANHO_MAX_SENHA_AUT_SERVIDOR                            = "320";
    public static final String TPC_EXIGE_TIPO_MOTIVO_OPERACAO_USUARIO                        = "321";
    public static final String TPC_EXIBE_LINK_BOLETO_SALDO_DEVEDOR                           = "322";
    public static final String TPC_SUBTRAI_PAGAMENTO_PARCIAL_MARGEM                          = "323";
    public static final String TPC_MARGEM_1_CASADA_MARGEM_3_LATERAL                          = "324";
    public static final String TPC_GERA_TABELA_LOG_PERIODICIDADE                             = "325";
    public static final String TPC_BLOQUEIA_CAD_TAXAS_COMPARTILHADAS                         = "326";
    public static final String TPC_BLOQUEIA_COMPRA_SERVIDOR_NOVO                             = "327";
    public static final String TPC_EXTENSAO_ARQUIVO_REMOVIVEL                                = "328";
    public static final String TPC_ENVIA_CONTRATOS_PAGOS_FERIAS_FOLHA                        = "329";
    public static final String TPC_DATA_ALTERACAO_SISTEMA_CFT_PARA_TAXA                      = "330";
    public static final String TPC_PERMITE_SERVIDOR_SIMULAR_SEM_MARGEM                       = "331";
    public static final String TPC_PERCENTUAL_APROX_CET_PARA_TAXA_JUROS                      = "332";
    public static final String TPC_BLOQUEIA_COMPRA_ULTIMA_PARCELA                            = "333";
    public static final String TPC_ENDERECO_ACESSO_USU_SOBREPOE_CSE_ORG                      = "334";
    public static final String TPC_IMPEDE_CPF_IGUAL_ENTRE_USU_CSA_E_CSE                      = "335";
    public static final String TPC_IMPEDE_CPF_IGUAL_ENTRE_USU_CSE                            = "336";
    public static final String TPC_BLOQUEIA_LOGIN_USU_SEM_CPF_CSE_ORG                        = "337";
    public static final String TPC_BLOQUEIA_LOGIN_USU_SEM_CPF_CSA_COR                        = "338";
    public static final String TPC_ENVIA_EMAIL_ENTIDADES_QNDO_ALTERA_ADE                     = "339";
    public static final String TPC_EXIBE_NIVEL_SEGURANCA_PAGINA_INICIAL                      = "340";
    public static final String TPC_HABILITA_MODULO_RECUPERAR_SENHA_SER                       = "341";
    //public static final String TPC_EXIGE_AUTORIZACAO_SEGUNDA_SENHA_CSE_ORG                 = "342";
    public static final String TPC_MOSTRA_VARIACAO_MARGEM_CSA_COR                            = "343";
    public static final String TPC_PERIODO_ENVIO_EMAIL_AUDITORIA_CSE_ORG                     = "344";
    public static final String TPC_PERIODO_ENVIO_EMAIL_AUDITORIA_CSA_COR                     = "345";
    public static final String TPC_CLASSE_IMPORTADOR_CONTRACHEQUES                           = "346";
    public static final String TPC_HABILITA_APROVACAO_SALDO_SERVIDOR_COMPRA                  = "347";
    public static final String TPC_GERAR_SENHA_TODOS_USU_SER_ATIVOS                          = "348";
    public static final String TPC_HABILITA_BLOQUEIO_FUNCAO_RSE                              = "349";
    public static final String TPC_BLOQUEIA_CANCELAMENTO_COMPRA_COM_REJ_PGT                  = "350";
    public static final String TPC_PERMITE_CANCEL_COMPRA_SER_APOS_INF_SALDO                  = "351";
    public static final String TPC_PERMITE_CARENCIA_DESBL_AUT_CSA_COMPRA                     = "352";
    public static final String TPC_DIAS_EXPIRACAO_NOVA_SENHA_SERVIDOR                        = "353";
    public static final String TPC_TAM_MAX_MSG_COMUNICACAO                                   = "354";
    public static final String TPC_NUM_MAX_CMN_PENDENTE_POR_CSA                              = "355";
    public static final String TPC_MAX_MSG_PENDENTE_POR_CMN                                  = "356";
    public static final String TPC_PERMITE_ALTERAR_LOGIN_SERVIDOR                            = "357";
    public static final String TPC_URL_VERSOES_ESTAVEIS                                      = "358";
    public static final String TPC_IMPEDE_EMAIL_IGUAL_ENTRE_SER_CPF                          = "359";
    public static final String TPC_QTD_OPERACOES_VALIDADE_SENHA_AUTORIZACAO                  = "360";
    public static final String TPC_SENHA_AUT_SERVIDOR_SOMENTE_NUMERICA                       = "361";
    public static final String TPC_EMAIL_ALTERACAO_SENHA_AUT_SERVIDOR                        = "362";
    public static final String TPC_IMPEDE_CELULAR_IGUAL_ENTRE_SER_CPF                        = "363";
    public static final String TPC_EXIGE_ANEXO_DSD_SALDO_SERVIDOR                            = "364";
    public static final String TPC_EMAIL_ANEXO_SALDO_DEVEDOR_SERVIDOR                        = "365";
    public static final String TPC_HABILITA_MODULO_RECUPERAR_SENHA_USU                       = "366";
    public static final String TPC_EXIGE_ANEXO_DSD_SALDO_DEVEDOR_COMPRA                      = "367";
    public static final String TPC_EXIBE_ANEXOS_SALDO_DEVEDOR_CORRESPONDENTE                 = "368";
    public static final String TPC_BLOQUEIA_COMPRA_COM_SOLICI_SALDO_LIQUID                   = "369";
    public static final String TPC_EXIBE_HISTORICO_LIQUIDACOES_ANTECIPADAS                   = "370";
    public static final String TPC_ENVIA_EMAIL_EXPIRACAO_CONSIGNATARIAS                      = "371";
    public static final String TPC_PERIODO_ENVIO_EMAIL_AUDITORIA_SUP                         = "372";
    public static final String TPC_REQUER_MUN_LOTACAO_SER_SOLIC_EMPRESTIMO                   = "373";
    public static final String TPC_CADASTRO_TELEFONE_OBRIGATORIO_USUARIO_CSA                 = "374";
    public static final String TPC_CADASTRO_EMAIL_OBRIGATORIO_USUARIO_CSA                    = "375";
    public static final String TPC_INF_SALDO_DEVEDOR_OPCIONAL_SERVIDOR                       = "376";
    public static final String TPC_EXIGE_ANEXO_BOLETO_SALDO_SERVIDOR                         = "377";
    public static final String TPC_EXIGE_ANEXO_BOLETO_SALDO_DEVEDOR_COMPRA                   = "378";
    public static final String TPC_REJEITA_PRD_SEM_RETORNO_ORG_PROCESSADOS                   = "379";
    public static final String TPC_INCLUSAO_CONSOME_SENHA_AUT_DESC                           = "380";
    public static final String TPC_USA_MULTIPLAS_SENHAS_AUTORIZACAO_SERVIDOR                 = "381";
    public static final String TPC_QTD_MAX_MULTIPLAS_SENHAS_AUTORIZACAO                      = "382";
    public static final String TPC_USA_SENHA_AUTORIZACAO_TODAS_OPERACOES                     = "383";
    public static final String TPC_VALIDA_ACESSO_IP_POR_USUARIO_OU_ENTIDADE                  = "384";
    //TODO: um refactoring pode ser feito para criar campo em tb_funcao para definir se OBS_MOTIVO_OPERACAO obrigatório
    //      ao invés de ser um param de sistema isolado
    public static final String TPC_OBS_OBRIGATORIO_SUSPENSAO_ADE                             = "385";
    public static final String TPC_SENHA_EXP_SERVIDOR_PODE_SER_USADA                         = "386";
    public static final String TPC_TAM_MAX_ARQ_ANEXO_COMUNICACAO                             = "387";
    public static final String TPC_QTE_MAX_ARQ_ANEXO_COMUNICACAO                             = "388";
    public static final String TPC_HABILITA_MODULO_SDP                                       = "389";
    public static final String TPC_RETEM_MARGEM_ADE_EM_ANDAMENTO_NAO_PAGO                    = "390";
    public static final String TPC_HABILITA_CADASTRO_AVANCADO_USU_SER                        = "391";
    public static final String TPC_HABILITA_CHAT_SERVIDOR                                    = "392";
    public static final String TPC_USA_DIAS_UTEIS_ENTRE_SOLICIT_SALDO_DEV                    = "393";
    public static final String TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN                         = "394";
    public static final String TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN_SER                     = "395";
    public static final String TPC_PERC_MAX_VAR_MARGEM_MENOR_CAD_MARGENS                     = "396";
    public static final String TPC_RENEGOCIACAO_COMPRA_MARGEM_3_NEG_CASADA                   = "397";
    public static final String TPC_INTEGRA_JIRA                                              = "398";
    public static final String TPC_ARQ_CONF_ENTRADA_REL_INTEGRACAO_RETORNO                   = "399";
    public static final String TPC_PERMITE_SELEC_ENT_UPL_ARQ_MARGEM_RETORNO                  = "400";
    public static final String TPC_INDICE_SOMENTE_AUTOMATICO                                 = "401";
    public static final String TPC_PERMITE_COMPRA_APENAS_CET_MENOR_ANTERIOR                  = "402";
    public static final String TPC_VALIDACAO_SEGURANCA_TELA_LOGIN                            = "403";
    public static final String TPC_VALIDACAO_SEGURANCA_TELA_LOGIN_SER                        = "404";
    public static final String TPC_RETEM_MARGEM_REVISAO_ACAO_SUSPENSAO                       = "405";
    public static final String TPC_SER_RELATA_RECLAMACAO_APENAS_CSA_COM_ADE                  = "406";
    public static final String TPC_TAM_MAX_MSG_RECLAMACAO                                    = "407";
    public static final String TPC_EXIBE_CET_CONTRATOS_TAXA_JUROS                            = "408";
    public static final String TPC_CONVERTE_CHARSET_ARQUIVO_UPLOAD                           = "409";
    public static final String TPC_PERMITE_DOIS_PERIODOS_EXPORTACAO_ABERTOS                  = "410";
    public static final String TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN                       = "411";
    public static final String TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN_SER                   = "412";
    public static final String TPC_MANTEM_DATA_INI_ADE_DEFERIDA_POS_CORTE                    = "413";
    public static final String TPC_PRAZO_ENVIA_EMAIL_EXPIRACAO_CSA                           = "414";
    public static final String TPC_BLOQ_TRANSF_ADE_COM_BLOQ_CNV_SVC_SERVIDOR                 = "415";
    public static final String TPC_ORDEM_PRIORIDADE_GERACAO_TRANSFERIDOS                     = "416";
    public static final String TPC_USA_DIAS_UTEIS_DEFER_AUTOMATICO_ADE                       = "417";
    public static final String TPC_PERMITE_DEFERIMENTO_TERCEIROS_PELA_CSA                    = "418";
    public static final String TPC_PERMITE_ALTERAR_ADE_NUMERO_REIMP_MANUAL                   = "419";
    public static final String TPC_POSSUI_PORTAL_SERVIDOR                                    = "420";
    public static final String TPC_EXPORTA_VLR_PERC_SVC_QUE_CALCULA_VLR_REAL                 = "421";
    public static final String TPC_PERMITE_TRANSFERENCIA_PARA_OUTROS_ORGAOS                  = "422";
    public static final String TPC_EXIGE_SENHA_OPERACOES_SENSIVEIS_CSE_ORG                   = "423";
    public static final String TPC_PERMITE_SER_ACESSO_HOST_A_HOST                            = "424";
    public static final String TPC_LIMITA_IP_ACESSO_HOST_A_HOST_SER                          = "425";
    //public static final String TPC_EXIGE_AUTORIZACAO_SEGUNDA_SENHA_SUP                     = "426";
    //public static final String TPC_EXIGE_AUTORIZACAO_SEGUNDA_SENHA_CSA_COR                 = "427";
    public static final String TPC_EXIGE_SENHA_OPERACOES_SENSIVEIS_SUP                       = "428";
    public static final String TPC_EXIGE_SENHA_OPERACOES_SENSIVEIS_CSA_COR                   = "429";
    public static final String TPC_CADASTRO_EMAIL_OBRIGATORIO_CSE_ORG_SUP                    = "430";
    public static final String TPC_BLOQUEIA_LOGIN_USU_SEM_EMAIL_CSE_ORG_SUP                  = "431";
    public static final String TPC_BLOQUEIA_LOGIN_USU_SEM_EMAIL_CSA_COR                      = "432";
    public static final String TPC_BLOQUEIA_EDICAO_EMAIL                                     = "433";
    public static final String TPC_EMAIL_REINICIALIZACAO_SENHA                               = "434";
    public static final String TPC_LIMITE_ERRO_SEGURANCA_SESSAO                              = "435";
    public static final String TPC_EXIBE_LOGIN_OUTRAS_ENTIDADES                              = "436";
    public static final String TPC_PERMITE_CADASTRO_VALIDACAO_TOTP                           = "437";
    public static final String TPC_PERMITE_ANEXO_INCLUSAO_CONTRATOS                          = "438";
    public static final String TPC_TAMANHO_MIN_SENHA_USUARIOS                                = "439";
    public static final String TPC_TAMANHO_MAX_SENHA_USUARIOS                                = "440";
    public static final String TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO                      = "441";
    public static final String TPC_MARGEM_1_PROPORCIONAL_USADO_MARGEM_3                      = "442";
    public static final String TPC_EXIBE_IP_OUTRAS_ENTIDADES                                 = "443";
    public static final String TPC_ARQ_CONF_ENTRADA_IMP_FALECIDO                             = "444";
    public static final String TPC_ARQ_CONF_TRADUTOR_IMP_FALECIDO                            = "445";
    public static final String TPC_GERAR_REL_INTEGRACAO_CSA_PARA_ORGAO                       = "446";
    public static final String TPC_BLOQUEIA_INDICE_ZERO                                      = "447";
    public static final String TPC_UTILIZA_CET_GRAVADO_INCLUSAO                              = "448";
    public static final String TPC_PERMITE_COMPRA_SEM_RESTRICAO_TAXA_MENOR                   = "449";
    public static final String TPC_PERMITE_RENEG_SEM_RESTRICAO_TAXA_MENOR                    = "450";
    public static final String TPC_ARQ_CONF_ENTRADA_REGRA_INCONSISTENCIA                     = "451";
    public static final String TPC_ARQ_CONF_TRADUTOR_REGRA_INCONSISTENCIA                    = "452";
    public static final String TPC_PERMITE_TROCAR_IP_ACESSO_MESMA_SESSAO                     = "453";
    public static final String TPC_PERMITE_BLOQ_CSA_N_LIQ_ADE_SALDO_PAGO_SER                 = "454";
    public static final String TPC_USA_DIAS_UTEIS_ENTRE_COMP_SALDO_LIQ_ADE                   = "455";
    public static final String TPC_EXIBE_CAPITAL_DEVIDO                                      = "456";
    public static final String TPC_SENHA_CONS_SERVIDOR_SOMENTE_NUMERICA                      = "457";
    public static final String TPC_EXIBE_VALOR_DISPONIVEL_CRITICA_CARTAO                     = "458";
    public static final String TPC_REQUER_MATRICULA_E_CPF_CSA_LOTE                           = "459";
    public static final String TPC_HABILITA_LIQUIDACAO_EM_DUAS_ETAPAS                        = "460";
    public static final String TPC_BLOQUEIA_DEFERIMENTO_MANUAL_CONTRATO                      = "461";
    public static final String TPC_ALERTA_DEFER_INDEFER_MANUAL_CONTRATO                      = "462";
    public static final String TPC_ENVIA_EMAIL_CSE_NOVO_CONTRATO                             = "463";
    public static final String TPC_ENVIA_EMAIL_CSE_LIQUIDAR_CONTRATO                         = "464";
    public static final String TPC_PERIODICIDADE_FOLHA                                       = "465";
    public static final String TPC_CONCLUI_ADE_NAO_INTEGRA_FOLHA                             = "466";
    public static final String TPC_EXPORTA_LIQ_INDEPENDENTE_QTD_PAGAS                        = "467";
    public static final String TPC_MANTEM_DATA_INI_FIM_FOLHA_RETORNO                         = "468";
    public static final String TPC_BLOQUEIA_DEFER_INDEFER_CPF_IGUAL                          = "469";
    public static final String TPC_AVANCA_FLUXO_COMPRA_SEM_CICLO_FIXO                        = "470";
    public static final String TPC_PERMITE_CAD_IP_REDE_INTERNA_CSA_COR                       = "471";
    public static final String TPC_GERA_RELATORIO_INTEGRACAO_XLS                             = "472";
    public static final String TPC_HABILITA_SALDO_DEVEDOR_EXCLUSAO_SERVIDOR                  = "473";
    public static final String TPC_DIAS_BLOQ_CSA_MENSAGEM_NAO_LIDA                           = "474";
    public static final String TPC_EMAIL_SERVIDOR_INFORMACAO_SALDO_CSA                       = "475";
    public static final String TPC_EMAIL_ALERTA_CSA_DATA_CORTE_ALTERADA                      = "476";
    public static final String TPC_QTD_MESES_ARQUIVAMENTO_ADE_CANCELADAS                     = "477";
    public static final String TPC_QTD_MESES_ARQUIVAMENTO_ADE_LIQUIDADAS                     = "478";
    public static final String TPC_QTD_MESES_ARQUIVAMENTO_ADE_CONCLUIDAS                     = "479";
    public static final String TPC_QTD_PERIODOS_MANTIDOS_NA_TABELA_MOVIMENTO                 = "480";
    public static final String TPC_HABILITA_REIMPLANTACAO_COM_REDUCAO_VALOR                  = "481";
    public static final String TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR                 = "482";
    public static final String TPC_MINUTOS_FECHAMENTO_LEILAO_VIA_SIMULACAO                     = "483";
    public static final String TPC_DIAS_BLOQ_SERVIDOR_COM_LEILAO_CANCELADO                   = "484";
    public static final String TPC_SOAP_ENVIA_SENHA_AUT_SERVIDOR                             = "485";
    public static final String TPC_MASCARA_NOME_LOGIN                                        = "486";
    public static final String TPC_QTD_MAX_SENHAS_AUTORIZACAO_VIA_TOTEM                      = "487";
    public static final String TPC_CARENCIA_CONCLUSAO_APENAS_COM_SDV                         = "488";
    public static final String TPC_GERA_SENHA_AUT_SER_DECLARACAO_MARGEM                      = "489";
    public static final String TPC_ARQ_CONF_ENTRADA_IMP_HISTORICO                            = "490";
    public static final String TPC_ARQ_CONF_TRADUTOR_IMP_HISTORICO                           = "491";
    public static final String TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO                       = "492";
    public static final String TPC_EXIGE_CAD_EMAIL_SER_PRIMEIRO_ACESSO_SIST                  = "493";
    public static final String TPC_EXIGE_PROTOCOLO_GERA_SENHA_AUT_HOST_HOST                  = "494";
    public static final String TPC_METODO_CALCULO_SIMULACAO                                  = "495";
    public static final String TPC_CONSULTA_MARGEM_CONSOME_SENHA_AUT_DESC                    = "496";
    public static final String TPC_QTD_MESES_EXPIRAR_MENSAGENS                               = "497";
    public static final String TPC_HABILITA_MODULO_DESCONTO_EM_FILA                          = "498";
    public static final String TPC_EXIBE_MARGEM_DISPONIVEL_CRITICA_CARTAO                    = "499";
    public static final String TPC_TEMPO_EXPIRACAO_SESSAO_CSE_ORG_SUP                        = "500";
    public static final String TPC_TEMPO_EXPIRACAO_SESSAO_CSA_COR                            = "501";
    public static final String TPC_TEMPO_EXPIRACAO_SESSAO_SER                                = "502";
    public static final String TPC_PERMITE_LIQ_CICLO_VIDA_FIXO_CSE_ORG_SUP                   = "503";
    public static final String TPC_MARGEM_LIMITE_CONTRATOS_POR_CSA                           = "504";
    public static final String TPC_DATA_TERMO_DE_USO_CSE                                     = "505";
    public static final String TPC_DATA_TERMO_DE_USO_ORG                                     = "506";
    public static final String TPC_DATA_TERMO_DE_USO_SER                                     = "507";
    public static final String TPC_DATA_TERMO_DE_USO_CSA                                     = "508";
    public static final String TPC_DATA_TERMO_DE_USO_COR                                     = "509";
    public static final String TPC_DATA_TERMO_DE_USO_SUP                                     = "510";
    public static final String TPC_USA_DIAS_UTEIS_DIA_PAGTO_PRIMEIRA_PARCELA                 = "511";
    public static final String TPC_PERMITE_AGRUPAR_PERIODOS_RETORNO                          = "512";
    public static final String TPC_PERMITE_ESCOLHER_PERIODO_EM_AGRUPAMENTO                   = "513";
    //public static final String TPC_INCLUIR_DADOS_AUTORIZACAO_EXP_MOV_FIN                   = "514";
    public static final String TPC_DIAS_LIQUIDACAO_AUTOMATICA_ADE_COMPRA                     = "515";
    public static final String TPC_INFORMA_NUM_PORTABILIDADE_CIP_COMPRA                      = "516";
    public static final String TPC_INFORMA_ANEXO_ADE_DOC_ADICIONAL_COMPRA                    = "517";
    public static final String TPC_SERVIDOR_ACESSA_EXTRATO_CONSOLIDADO                       = "518";
    public static final String TPC_INF_BANCARIA_OBRIGATORIA_CAD_EMAIL_TOTEM                  = "519";
    public static final String TPC_PERMITE_CSA_LIMITAR_USO_MARGEM_SERVIDOR                   = "520";
    public static final String TPC_TEMPO_EXPIRACAO_TOKEN_ACESSO                              = "521";
    public static final String TPC_HABILITA_EXTENSAO_PERIODO_FOLHA_AJUSTES                   = "522";
    public static final String TPC_INSERE_ALTERA_CRIA_NOVO_CONTRATO                          = "523";
    public static final String TPC_PERMITE_PERIODO_ACEITA_APENAS_REDUCOES                    = "524";
    public static final String TPC_CONSIDERA_PARCELAS_AGUARD_PROCESSAMENTO                   = "525";
    public static final String TPC_SUSPENDE_DESC_CSA_BLOQ_CONTROLE_COMPRA                    = "526";
    public static final String TPC_BLOQUEIA_ALTERACAO_ADE_ULTIMA_PARCELA                     = "527";
    public static final String TPC_NUMERO_REMETENTE_SMS                                      = "528";
    public static final String TPC_SID_CONTA_SMS                                             = "529";
    public static final String TPC_TOKEN_AUTENTICACAO_SMS                                    = "530";
    public static final String TPC_HABILITAR_EDICAO_CODIGO_FOLHA                             = "531";
    public static final String TPC_OCULTAR_CAMPOS_TAC                                        = "532";
    public static final String TPC_URL_CENTRALIZADOR_MOBILE                                  = "533";
    public static final String TPC_VALIDA_OTP_PRIMEIRO_ACESSO_USUARIO                        = "534";
    public static final String TPC_BLOQ_TRANSF_ADE_SERVIDOR_CPF_DIFERENTE                    = "535";
    public static final String TPC_TEMPO_EXPIRACAO_OTP                                       = "536";
    public static final String TPC_EXPORTA_INCL_ALT_ADE_SEM_ANEXO_PERIODO                    = "537";
    public static final String TPC_GERA_ARQ_ANEXOS_ADE_MOV_FIN                               = "538";
    public static final String TPC_METODO_ENVIO_OTP_RECUPERACAO_SENHA                        = "539";
    public static final String TPC_EXIBE_LINK_LOGIN_SERVIDOR_LOGIN_GERAL                     = "540";
    public static final String TPC_PERMITE_ESCOLHER_PERIODICIDADE_FOLHA                      = "541";
    public static final String TPC_ENVIA_EMAIL_BLOQ_DESBLOQ_SERVIDOR                         = "542";
    public static final String TPC_LST_HIST_PARCELA_ADE_TERCEIRO_COMPRA                      = "543";
    public static final String TPC_OMITE_CPF_SERVIDOR                                        = "544";
    public static final String TPC_ENVIA_EMAIL_UPLOAD_ARQ_CSE_PARA_CSA                       = "545";
    public static final String TPC_EXIGE_DETALHES_EXCL_BLOQ_SER                              = "546";
    public static final String TPC_ENVIA_EMAIL_NOTIFICACAO_CAD_SERVIDOR                      = "547";
    public static final String TPC_ENVIA_EMAIL_NOTIFICACAO_EDT_SERVIDOR                      = "548";
    public static final String TPC_QTDE_DIAS_PARA_SER_CONCRETIZAR_LEILAO                     = "549";
    public static final String TPC_QTDE_LEILOES_CANCELADOS_PARA_BLOQUEIO_SER                 = "550";
    public static final String TPC_AUTO_DESBLOQUEIO_SERVIDOR                                 = "551";
    public static final String TPC_ALTERA_STATUS_RSE_PARA_PENDENTE_NOVA_ADE                  = "552";
    public static final String TPC_PERMITE_ALTERAR_ADE_AGUARD_CONF_E_DEF                     = "553";
    public static final String TPC_HABILITA_DATA_PREVISTA_RETORNO                            = "554";
    public static final String TPC_DIAS_ANTES_NOTIFICACAO_ENVIO_ARQ_FOLHA                    = "555";
    public static final String TPC_DIAS_DEPOIS_NOTIFICACAO_ENVIO_ARQ_FOLHA                   = "556";
    public static final String TPC_TEM_VALIDACAO_DIGITAL_SERVIDOR                            = "557";
    public static final String TPC_QTDE_MAX_TENTATIVAS_VALIDACAO_DIGITAL                     = "558";
    public static final String TPC_SUFIXO_VERSAO_LEIAUTE_WEB                                 = "559";
    public static final String TPC_PADRAO_NOME_ANEXO_PERIODO                                 = "560";
    public static final String TPC_HABILITA_RISCO_SERVIDOR_CSA                               = "561";
    public static final String TPC_EXIBE_SER_PENDENTE_COM_ADE                                = "562";
    public static final String TPC_MES_REFERENCIA_MUDANCA_EXERCICIO_FISCAL                   = "563";
    public static final String TPC_HABILITA_ASSINATURA_DIGITAL_CONSIGNACAO                   = "564";
    public static final String TPC_TAM_MAX_ARQ_ANEXO_CONTRATO_ASSINATURA_DIGITAL             = "565";
    public static final String TPC_QTE_MAX_ARQ_ANEXO_CONTRATO_ASSINATURA_DIGITAL             = "566";
    public static final String TPC_VALIDA_VLR_E_OU_PRZ_EXPORTA_ALT_SEM_ANEXO                 = "567";
    public static final String TPC_TOKEN_ASSINATURA_DIGITAL                                  = "568";
    public static final String TPC_END_POINT_ASSINATURA_DIGITAL                              = "569";
    public static final String TPC_PRIORIZA_PAGAMENTO_EXATO_RETORNO                          = "570";
    public static final String TPC_RSE_DATA_RETORNO                                          = "571";
    public static final String TPC_QTDE_DIAS_CSE_VALIDA_CAD_SERVIDOR                         = "572";
    public static final String TPC_VALIDAR_QTDE_VEZES_CAPTCHA_CONSULTAR_MARGEM               = "573";
    public static final String TPC_CRIAR_NOVA_ADE_TRANSFERENCIA                              = "574";
    public static final String TPC_TAM_MAX_ARQ_UPLOAD_EM_LOTE_ANEXO                          = "575";
    public static final String TPC_EXIGE_CONFIRMA_LEITURA_TERMO_MUDANCA_EMAIL                = "576";
    public static final String TPC_PERMITE_LIQUIDAR_PARCELA_FUTURA                           = "577";
    public static final String TPC_HABILITA_MODULO_BENEFICIOS_SAUDE                          = "578";
    public static final String TPC_PERMITE_MULTIPLAS_LINHAS_RETORNO_MESMA_PRD                = "579";
    public static final String TPC_BNI_ID_PROPOSAL_PARTNER                                   = "580";
    public static final String TPC_ENVIA_EXCLUSOES_MOVIMENTO_MENSAL                          = "581";
    public static final String TPC_MAXIMO_DEPENDENTES_SUBSIDIO                               = "582";
    public static final String TPC_MAXIMO_SUBSIDIO_POR_DEPENDENTE                            = "583";
    public static final String TPC_MAXIMO_SUBSIDIO_POR_TITULAR                               = "584";
    public static final String TPC_ORDEM_PRIORIDADE_SUBSIDIO_GRUPO_FAMILIAR                  = "585";
    public static final String TPC_LIMITA_VAL_SUBSIDIO_BEN_PERCENTUAL_SAL_BASE               = "586";
    public static final String TPC_APLICA_SUBSIDIO_FAMILIA_SERVIDOR_INATIVO                  = "587";
    public static final String TPC_MENOR_FAIXA_SAL_FAMILIA_SER_INATIVO                       = "588";
    public static final String TPC_APLICA_SUBSIDIO_FAMILIA_SER_SALARIO_NULO                  = "589";
    public static final String TPC_IMPORTA_TRANSFERIDOS_SEM_CADASTRO_MARGEM                  = "590";
    public static final String TPC_TAMANHO_ANEXO_BENEFICIARIO                                = "591";
    public static final String TPC_MAXIMO_ANEXO_BENEFICIARIO                                 = "592";
    public static final String TPC_URL_ANTIVIRUS_ANEXO                                       = "593";
    public static final String TPC_BLOQUEAR_LIQUIDACAO_DIRETA_ADE_SUBSIDIO                   = "594";
    public static final String TPC_INCLUSAO_ADE_PRZ_UM_ALEM_LIMITE_CSA_SER                   = "595";
    public static final String TPC_HABILITA_VERIFICACAO_ARQUIVO_UPLOAD_ANTIVIRUS             = "596";
    public static final String TPC_CAPTCHA_AVANCADO_CHAVE_PUBLICA                            = "597";
    public static final String TPC_CAPTCHA_AVANCADO_CHAVE_PRIVADA                            = "598";
    public static final String TPC_HABILITA_AMBIENTE_DE_TESTES                               = "599";
    public static final String TPC_ARQ_CONF_INTEGRACAO_ORIENTADA_MARGEM                      = "600";
    public static final String TPC_ARQ_CONF_INTEGRACAO_ORIENTADA_RETORNO                     = "601";
    public static final String TPC_ARQ_MANUAL_INTEGRACAO_ORIENTADA                           = "602";
    public static final String TPC_ARQ_CONF_ENTRADA_EXP_OPERADORA_BENEFICIO                  = "603";
    public static final String TPC_ARQ_CONF_SAIDA_EXP_OPERADORA_BENEFICIO                    = "604";
    public static final String TPC_ARQ_CONF_TRADUTOR_EXP_OPERADORA_BENEFICIO                 = "605";
    public static final String TPC_EXCLUI_DEPENDENTE_FORA_DA_REGRA                           = "606";
    public static final String TPC_DEPENDENTES_SUBSIDIO_ILIMITADO                            = "607";
    public static final String TPC_CRIPTOGRAFA_ARQUIVOS                                      = "608";
    public static final String TPC_HABILITA_EMAIL_CONTRATOS_REJEITADOS_FOLHA                 = "609";
    public static final String TPC_HABILITA_TUTORIAL_PRIMEIRO_ACESSO                         = "610";
    public static final String TPC_EXIGE_CAD_TELEFONE_SER_PRIMEIRO_ACESSO_SIST               = "611";
    public static final String TPC_URL_EUCONSIGOMAIS                                         = "612";
    public static final String TPC_EXPORTAR_ADE_SOMENTE_DO_PERIODO_BASE                      = "613";
    public static final String TPC_ARQ_CONF_ENTRADA_IMP_ARQ_OPERADORA                        = "614";
    public static final String TPC_ARQ_CONF_TRADUTOR_IMP_ARQ_OPERADORA                       = "615";
    public static final String TPC_AUTO_DESBLOQUEIO_USUARIO_CSE_ORG                          = "616";
    public static final String TPC_AUTO_DESBLOQUEIO_USUARIO_CSA_COR                          = "617";
    public static final String TPC_AUTO_DESBLOQUEIO_USUARIO_SUP                              = "618";
    public static final String TPC_EXIBE_URL_EUCONSIGOMAIS_SERVIDOR                          = "619";
    public static final String TPC_EXIBE_URL_EUCONSIGOMAIS_SERVIDOR_XML                      = "620";
    public static final String TPC_ARQ_CONF_TRADUTOR_ARQ_FATURAMENTO_BENEFICIO               = "621";
    public static final String TPC_ARQ_CONF_SAIDA_ARQ_FATURAMENTO_BENEFICIO                  = "622";
    public static final String TPC_ARQ_CONF_TRADUTOR_ARQ_RESIDUO_FAT_BENEFICIO               = "623";
    public static final String TPC_ARQ_CONF_SAIDA_ARQ_RESIDUO_FAT_BENEFICIO                  = "624";
    public static final String TPC_PERCENTUAL_AGENCIAMENTO_CONTRATOS_BENEFICIO               = "625";
    public static final String TPC_VALIDAR_REGRA_SALARIO_FATURAMENTO_BENEFICIO               = "626";
    public static final String TPC_PASSO_A_PASSO_OPERACOES_ECONSIG                           = "627";
    public static final String TPC_ENVIA_EMAIL_ALERTA_ARQUIVOS_FOLHA_CSA                     = "628";
    public static final String TPC_USA_RECUPERACAO_SENHA_AUTO_CADASTRO_SER                   = "629";
    public static final String TPC_USA_DEFINICAO_TAXA_JUROS                                  = "630";
    public static final String TPC_HABILITA_USO_OCA_PERIODO_EXPORTACAO                       = "631";
    public static final String TPC_QTDE_DIAS_NOTIFICACAO_BLOQUEIO_INATIVIDADE                = "632";
    public static final String TPC_HABILITA_CHAT_CSE_ORG                                     = "633";
    public static final String TPC_HABILITA_CHAT_CSA_COR                                     = "634";
    public static final String TPC_HABILITA_CHAT_SER                                         = "635";
    public static final String TPC_HABILITA_ENVIO_DE_SMS_EMAIL_APOS_DEFERIMENTO_CONSIGNACAO  = "636";
    public static final String TPC_CRIA_USUARIO_MASTER_ORGAO                                 = "637";
    public static final String TPC_ENVIA_EMAIL_CRIACAO_SENHA_NOVO_USU_CSE_ORG                = "638";
    public static final String TPC_ENVIA_EMAIL_CRIACAO_SENHA_NOVO_USU_CSA_COR                = "639";
    public static final String TPC_ENVIA_EMAIL_CRIACAO_SENHA_NOVO_USU_SUP                    = "640";
    public static final String TPC_ENVIA_SMS_CODIGO_UNICO_AUTORIZACAO_CONSIGNACAO            = "641";
    public static final String TPC_PERMITE_PRE_VISUALIZAR_RELATORIOS                         = "642";
    public static final String TPC_MARGEM_ORIGINAL_EXCEDE_ATE_MARGEM_LATERAL                 = "643";
    public static final String TPC_USA_TAXA_JUROS_SALDO_INSOLUTO                             = "644";
    public static final String TPC_PRIORIZA_PAGAMENTO_PRIORIDADE_SVC_CNV_RETORNO             = "645";
    public static final String TPC_VALIDA_QTD_CONTRATOS_TRANSFERENCIA                        = "646";
    public static final String TPC_DELIMITADOR_CAMPOS_RELATORIO_TXT                          = "647";
    public static final String TPC_DELIMITADOR_CAMPOS_RELATORIO_CSV                          = "648";
    public static final String TPC_CSA_UPLOAD_BOLETO_SER_NAO_POSSUI_ADE                      = "649";
    public static final String TPC_DIAS_NOTIFICACAO_DOWNLOAD_MOV_FIN_USU_CSE                 = "650";
    public static final String TPC_MODO_NOTIFICACAO_NOVO_BOLETO_SERVIDOR                     = "651";
    public static final String TPC_LIMITE_MIN_INDICE                                         = "652";
    public static final String TPC_HABILITA_PORTAL_BENEFICIOS                                = "653";
    public static final String TPC_DIAS_REMOCAO_BOLETO_SERVIDOR                              = "654";
    public static final String TPC_TEMPO_ATUALIZACAO_MARGEM_SESSAO_SER                       = "655";
    public static final String TPC_HABILITA_EXTRATO_CSA_COR                                  = "656";
    public static final String TPC_AUTENTICACAO_SSO_CSE_ORG                                  = "657";
    public static final String TPC_AUTENTICACAO_SSO_CSA_COR                                  = "658";
    public static final String TPC_AUTENTICACAO_SSO_SUP                                      = "659";
    public static final String TPC_AUTENTICACAO_SSO_SER                                      = "660";
    public static final String TPC_URL_BASE_SERVICO_SSO                                      = "661";
    public static final String TPC_SSO_OAUTH_CLIENT_ID                                       = "662";
    public static final String TPC_SSO_OAUTH_CLIENT_SECRET                                   = "663";
    public static final String TPC_DATA_TERMO_DE_USO_SALARYPAY_CSE                           = "664";
    public static final String TPC_DATA_TERMO_DE_USO_SALARYPAY_ORG                           = "665";
    public static final String TPC_DATA_TERMO_DE_USO_SALARYPAY_SER                           = "666";
    public static final String TPC_DATA_TERMO_DE_USO_SALARYPAY_CSA                           = "667";
    public static final String TPC_DATA_TERMO_DE_USO_SALARYPAY_COR                           = "668";
    public static final String TPC_DATA_TERMO_DE_USO_SALARYPAY_SUP                           = "669";
    public static final String TPC_SSO_OAUTH_USER_ADMIN_LOGIN                                = "670";
    public static final String TPC_SSO_OAUTH_USER_ADMIN_SENHA                                = "671";
    public static final String TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA                 = "672";
    public static final String TPC_ENVIA_SMS_SERVIDOR_QNDO_ALTERA_ADE                        = "673";
    public static final String TPC_LOGIN_USU_SERVIDOR_COM_CPF                                = "674";
    public static final String TPC_DATA_POLITICA_PRIVACIDADE_CSE                             = "675";
    public static final String TPC_DATA_POLITICA_PRIVACIDADE_ORG                             = "676";
    public static final String TPC_DATA_POLITICA_PRIVACIDADE_SER                             = "677";
    public static final String TPC_DATA_POLITICA_PRIVACIDADE_CSA                             = "678";
    public static final String TPC_DATA_POLITICA_PRIVACIDADE_COR                             = "679";
    public static final String TPC_DATA_POLITICA_PRIVACIDADE_SUP                             = "680";
    public static final String TPC_PERMITE_AGENDAR_RELATORIOS_MESMO_DIA_SUP                  = "681";
    public static final String TPC_PERMITE_AGENDAR_RELATORIOS_MESMO_DIA_CSE                  = "682";
    public static final String TPC_DIAS_NOTIFICACAO_COMUNICACAO_NAO_LIDA_CSE_ORG             = "683";
    public static final String TPC_DIAS_NOTIFICACAO_COMUNICACAO_NAO_LIDA_CSA_COR             = "684";
    public static final String TPC_DIAS_NOTIFICACAO_COMUNICACAO_NAO_LIDA_SER                 = "685";
    public static final String TPC_EXIBE_VALOR_DISPONIVEL_LANCAMENTO_CARTAO                  = "686";
    public static final String TPC_HABILITA_CRIPTOGRAFIA_URL                                 = "687";
    public static final String TPC_PERC_MAX_BLOCOS_SEM_MAPEAMENTO_CONVENIO                   = "688";
    public static final String TPC_PERC_MAX_VARIACAO_PARCELAS_POR_BLOCOS                     = "689";
    public static final String TPC_EXIBE_ORG_EST_ATIVOS_AUTENTICACAO_SERVIDOR                = "690";
    public static final String TPC_SENHA_SER_OPCIONAL_CONS_MARGEM_TEM_ADE                    = "691";
    public static final String TPC_QTDE_PROCESSOS_PARALELOS_PROCESSAMENTO_FOLHA              = "692";
    public static final String TPC_URL_SERVICO_FACES_WEB                                     = "693";
    public static final String TPC_API_KEY_FACES_WEB                                         = "694";
    public static final String TPC_VALIDA_EMAIL_PAPEL_USUARIO_CSE_ORG_SUP                    = "695";
    public static final String TPC_BLOQUEIA_EDICAO_EMAIL_SERVIDOR_CAD_FOLHA                  = "696";
    public static final String TPC_VALIDA_EMAIL_PAPEL_USUARIO_CSA_COR                        = "697";
    public static final String TPC_URL_SERVICO_SALARY_WEB                                    = "698";
    public static final String TPC_INDICE_AUTOMATICO_SEQUENCIAL_TODAS_ADES                   = "699";
    public static final String TPC_IMPEDE_EMAIL_IGUAL_ENTRE_USU                              = "700";
    public static final String TPC_IMPEDE_EMAIL_IGUAL_ENTRE_USU_E_SER                        = "701";
    public static final String TPC_QTDE_DIAS_ATUALIZACAO_CADASTRAL_CSE                       = "702";
    public static final String TPC_QTDE_DIAS_ATUALIZACAO_CADASTRAL_CSA                       = "703";
    public static final String TPC_EXIGE_ATUALIZACAO_CADASTRAL_CSA_CNPJ                      = "704";
    public static final String TPC_QTDE_DIAS_ACESSO_SISTEMA_SERVIDOR_SEM_VALIDACAO_EMAIL     = "705";
    public static final String TPC_EXIBE_MENU_SOLICITAR_SERVICOS_OPERACIONAL_SERVIDOR        = "706";
    public static final String TPC_DEFINE_ESFORCO_CONVERSAO_CHARSET                          = "707";
    public static final String TPC_MOD_BENEFICIO_PERMITE_AGREGADO                            = "708";
    public static final String TPC_CONCLUI_ADE_EXPORTACAO_MOVIMENTO                          = "709";
    public static final String TPC_HABILITA_SENHA_APP                                        = "710";
    public static final String TPC_PERMITE_CANCELAR_BENEFICIO_SEM_APROVACAO                  = "711";
    public static final String TPC_URL_APLICATIVO_SER_GOOGLE_STORE                           = "712";
    public static final String TPC_URL_APLICATIVO_SER_APPLE_STORE                            = "713";
    public static final String TPC_REENVIA_BENEFICIO_CONC_CADASTRO_REATIVADO                 = "714";
    public static final String TPC_DESABILITAR_ENVIO_CADASTRO_EMAIL_MOBILE_SER               = "715";
    public static final String TPC_PERMITE_ANEXO_ALTERACAO_CONTRATOS                         = "716";
    public static final String TPC_PERMITE_SALARYPAY_SOBREPOR_SENHA_SERVIDOR                 = "717";
    public static final String TPC_PERMITE_EDITAR_FLUXO_PARCELAS_CONSIGNACAO                 = "718";
    public static final String TPC_PERMITE_ALTERAR_ADE_INDICE                                = "719";
    public static final String TPC_PERMITE_EDITAR_ORGAO_SERVIDOR                             = "720";
    public static final String TPC_BLOQUEIA_CSA_ADE_SEM_MIN_ANEXOS                           = "721";
    public static final String TPC_OMITIR_CAMPO_SENHA_OPCIONAL                               = "722";
    public static final String TPC_QTDE_DIAS_BLOQ_CSA_CMN_SEM_RESPOSTA_CSE_ORG               = "723";
    public static final String TPC_KYC_URL_JORNADA_VALIDACAO                                 = "724";
    public static final String TPC_KYC_API_URL_GETTAXSTATUS                                  = "725";
    public static final String TPC_KYC_API_CONSUMER_KEY                                      = "726";
    public static final String TPC_KYC_API_CONSUMER_SECRET                                   = "727";
    public static final String TPC_KYC_API_URL_CHECKKYC                                      = "728";
    public static final String TPC_KYC_PAN_NUMBER_SOURCE                                     = "729";
    public static final String TPC_QTD_MESES_PARA_PAGTO_PRIMEIRA_PARCELA                     = "730";
    public static final String TPC_BLOQUEIA_COMPRA_DATA_FINAL_PASSADA                        = "731";
    public static final String TPC_KYC_RSA_MODULUS                                           = "732";
    public static final String TPC_KYC_RSA_PUBLIC_EXPONENT                                   = "733";
    public static final String TPC_KYC_GETSTATUS_RESULT_KEY                                  = "734";
    public static final String TPC_KYC_GETSTATUS_RESULT_VALUE                                = "735";
    public static final String TPC_KYC_GETSTATUS_RESULT_FIELD_NAME                           = "736";
    public static final String TPC_KYC_CHECKKYC_IS_NEW_VERSION                               = "737";
    public static final String TPC_KYC_CHECKKYC_RESULT_FIELD_NAME                            = "738";
    public static final String TPC_SENHA_SER_OBRIGATORIA_EXIBIR_DADOS_CSA_COR                = "739";
    public static final String TPC_GERA_OTP_SENHA_AUTORIZACAO                                = "740";
    //public static final String TPC_DIVIDIR_COMANDO_POR_SEMANA_EXPORTACAO                   = "741";
    //public static final String TPC_COMANDOS_ACEITOS_EXPORTACAO_SEMANAL                     = "742";
    //public static final String TPC_CONSIDERA_ALTERACAO_EXPORTACAO_SEMANAL                  = "743";
    public static final String TPC_GERAR_DADOS_NOTAS_FISCAIS_PERIODO_FATURAMENTO             = "744";
    public static final String TPC_HABILITA_PROCESSAMENTO_SISTEMA_DESBLOQUEADO               = "745";
    public static final String TPC_SOLICITA_SENHA_AUTORIZACAO_OPE_REALIZADAS_SER             = "746";
    public static final String TPC_DESBL_AUTOMAT_CSA_PRAZO_PENALIDADE                        = "747";
    public static final String TPC_USA_BIBLIOTECA_NATIVA_PARA_PROCESSOS_EXTERNOS             = "748";
    public static final String TPC_ENVIA_CONTRATOS_CARENCIA_MOV_FIN                          = "749";
    public static final String TPC_CALENDARIO_FISCAL_UTILIZADO_NOME_ARQUIVO                  = "750";
    //public static final String TPC_CALENDARIO_FISCAL_UTILIZADO_CONTAGEM_SEMANAS            = "751";
    public static final String TPC_VALIDAR_QTDE_VEZES_CAPTCHA_CONSULTAR_CONSIGNACAO          = "752";
    public static final String TPC_CONSIDERAR_CONTRATOS_NAO_EXPORTADOS                       = "753";
    public static final String TPC_ARQ_CONF_ENTRADA_EXP_MOV_FIN_DIFERENCAS                   = "754";
    public static final String TPC_ARQ_CONF_SAIDA_EXP_MOV_FIN_DIFERENCAS                     = "755";
    public static final String TPC_ARQ_CONF_TRADUTOR_EXP_MOV_FIN_DIFERENCAS                  = "756";
    public static final String TPC_ARQ_CONF_ENTRADA_IMP_SALDO_DEVEDOR                        = "757";
    public static final String TPC_ARQ_CONF_TRADUTOR_IMP_SALDO_DEVEDOR                       = "758";
    public static final String TPC_RECUPERACAO_SENHA_USU_SERVIDOR_CPF                        = "759";
    //public static final String TPC_DIA_SEMANA_CALCULO_QTD_SEMANAS_PERIODO                  = "760";
    public static final String TPC_PERIODO_COM_APENAS_REDUCOES_SOMENTE_EXCLUSAO              = "761";
    //public static final String TPC_IDENTIFICACAO_SEMANA_EXPORTACAO_SEMANAL                 = "762";
    public static final String TPC_URL_PLATAFORMA_BI                                         = "763";
    public static final String TPC_QTDE_DIAS_EXPIRACAO_INF_SALDO_DEVEDOR_RESCISAO            = "764";
    public static final String TPC_EXIGE_ATUALIZACAO_DADOS_SER_PRIMEIRO_ACESSO               = "765";
    public static final String TPC_EXIBE_CHECK_EMAIL_MARKETING_TERMO_DE_USO                  = "766";
    public static final String TPC_ID_CHATBOT_USUARIO_CSE_ORG_SUP                            = "767";
    public static final String TPC_ID_CHATBOT_USUARIO_CSA_COR                                = "768";
    public static final String TPC_ID_CHATBOT_USUARIO_SER                                    = "769";
    public static final String TPC_ID_CHATBOT_PAGINA_LOGIN_USU                               = "770";
    public static final String TPC_ID_CHATBOT_PAGINA_LOGIN_SER                               = "771";
    public static final String TPC_TOTEM_URL_BANCO_DE_DADOS                                  = "772";
    public static final String TPC_TOTEM_USU_BANCO_DE_DADOS                                  = "773";
    public static final String TPC_TOTEM_PASS_BANCO_DE_DADOS                                 = "774";
    public static final String TPC_RAIO_METROS_BUSCA_END_CONSIGNATARIAS                      = "775";
    public static final String TPC_URL_SERVICO_GEOCODIFICACAO                                = "776";
    public static final String TPC_CHAVE_ACESSO_SERVICO_GEOCODIFICACAO                       = "777";
    public static final String TPC_PERMITE_LIQUIDAR_PARCELA_PGTO_PARCIAL                     = "778";
    //public static final String TPC_COMANDO_EXTERNO_EXPORTACAO_MOVIMENTO                    = "779";
    public static final String TPC_ARQ_CONF_ENTRADA_EXP_RESCISAO                             = "780";
    public static final String TPC_ARQ_CONF_SAIDA_EXP_RESCISAO                               = "781";
    public static final String TPC_ARQ_CONF_TRADUTOR_EXP_RESCISAO                            = "782";
    //public static final String TPC_RECRIA_PERMISSOES_USU_SER_CARGA_MARGEM                  = "783";
    public static final String TPC_EXIGE_CONFIRMACAO_TERMO_CONSENTIMENTO_DADOS_SERVIDOR      = "784";
    public static final String TPC_TOTEM_URL_LIMPAR_CACHE                                    = "785";
    public static final String TPC_TOTEM_CODIGO_CONSIGNANTE                                  = "786";
    public static final String TPC_CHATBOT_FALLBACK_LIMIT                                    = "787";
    public static final String TPC_CHATBOT_BUSINESS_HOURS                                    = "788";
    public static final String TPC_URL_CHAT_SUPORTE                                          = "789";
    public static final String TPC_URL_CHAT_SUPORTE_WIDGET                                   = "790";
    public static final String TPC_SERVIDOR_AUTORIZA_DESCONTO_PARCIAL                        = "791";
    public static final String TPC_PERMITE_SIMULAR_BENEFICIO_SEM_MARGEM                      = "792";
    //public static final String TPC_COMANDO_EXTERNO_CONCLUSAO_RETORNO                       = "793";
    public static final String TPC_QTDE_HORAS_USUARIO_LOGAR_APOS_DESBLOQUEIO                 = "794";
    public static final String TPC_PERMITE_SERVIDOR_CANCELAR_SOLICITACAO_LEILAO              = "795";
    public static final String TPC_EXPRESSAO_REGULAR_RETIRAR_RSE_INCLUSAO_LOTE               = "796";
    public static final String TPC_CHAVE_VAULT_INTEGRACAO_SALARYPAY_CIELO                    = "797";
    public static final String TPC_PULA_VALIDACAO_ARQUIVO_INTEGRACAO                         = "798";
    public static final String TPC_OMITIR_ORG_EST_LOGIN_SER                                  = "799";
    public static final String TPC_ZERAR_MARGEM_AO_EXCLUIR_SERVIDOR                          = "800";
    public static final String TPC_QTD_DIAS_ARQUIVAR_SERVIDOR_EXCLUIDO_IMPORTACAO_MARGEM     = "801";
    public static final String TPC_URL_WHATSAPP                                              = "802";
    public static final String TPC_NUMERO_WHATSAPP                                           = "803";
    public static final String TPC_SUBTRAIR_VALOR_PAGO_ADE_PERCENTUAL_INDEPENDENTE_PERIODO   = "804";
    public static final String TPC_DIAS_BLOQUEIO_CSA_NAO_ATENDEU_SOLICITACAO_LIQUIDACAO      = "805";
    public static final String TPC_PERMITE_CSA_ALTERAR_ADE_ESTOQUE                           = "806";
    public static final String TPC_ADE_ESTOQUE_ADEQUADA_MARGEM_ALTERAR_PARA_DEFERIDA         = "807";
    public static final String TPC_EXIGE_ANEXO_COMPROVANTE_PAGAMENTO_SALDO_SERVIDOR          = "808";
    public static final String TPC_PERMITE_INCLUSAO_ANEXO_CONFIRMAR_RESERVA                  = "809";
    public static final String TPC_ALERTA_EMAIL_EXPIRACAO_CONSIGNATARIAS                     = "810";
    public static final String TPC_EXIGE_SENHA_SER_TRANSFERENCIA_ADE_PARA_CSE                = "811";
    public static final String TPC_EXIGE_SENHA_SER_TRANSFERENCIA_ADE_PARA_CSA                = "812";
    //public static final String TPC_COMANDO_EXTERNO_GERACAO_RELATORIO_INTEGRACAO            = "813";
    public static final String TPC_PERMITE_SIMULACAO_PARCIAL_SALDO_DEVEDOR                   = "814";
    public static final String TPC_PERMITE_AGENDAR_RELATORIOS_MESMO_DIA_CSA                  = "815";
    public static final String TPC_GERAR_RELATORIOS_INTEGRACAO_COMPACTADOS                   = "816";
    public static final String TPC_URL_SERVICO_CONSULTAR_MARGEM                              = "817";
    public static final String TPC_EXIBE_MENSAGEM_TELA_LOGIN_SERVIDOR                        = "818";
    public static final String TPC_EXIBE_DATA_PREVISTA_CONCLUSAO_CONSIGNACAO                 = "819";
    public static final String TPC_REGEX_LIMITACAO_MATRICULA_TRANSFERENCIA_SERVIDOR          = "820";
    public static final String TPC_ALTERA_SENHA_TODOS_LOGINS_SERVIDOR                        = "821";
    public static final String TPC_EXIBE_MENSAGEM_TELA_LOGIN_USUARIO                         = "822";
    public static final String TPC_EXIBE_MARGENS_DEPENDENTES_PARA_CSE_ORG                    = "823";
    public static final String TPC_EXIBE_MARGENS_DEPENDENTES_PARA_CSA_COR                    = "824";
    public static final String TPC_EXIBE_MARGENS_DEPENDENTES_PARA_SUP                        = "825";
    public static final String TPC_EXIBE_MARGENS_DEPENDENTES_PARA_SER                        = "826";
    public static final String TPC_EXIBE_ADE_DATA_STATUS_CONSULTAR_CONSIGNACAO               = "827";
    public static final String TPC_TAM_MAX_ARQ_ANEXO_REGISTRO_SERVIDOR                       = "828";
    public static final String TPC_EXIBE_HISTORICO_PARCELA_ABERTO                            = "829";
    public static final String TPC_QTD_OPERACOES_LIBERACAO_MARGEM_POR_USU_ENVIA_NOTIFICACAO  = "830";
    public static final String TPC_QTD_OPERACOES_LIBERACAO_MARGEM_POR_USU_EXIGE_CAPTCHA      = "831";
    public static final String TPC_QTD_OPERACOES_LIBERACAO_MARGEM_POR_USU_EXIGE_OUTRA_SENHA  = "832";
    public static final String TPC_QTD_OPERACOES_LIBERACAO_MARGEM_POR_USU_BLOQUEIA_USUARIO   = "833";
    public static final String TPC_QTD_OPERACOES_LIBERACAO_MARGEM_POR_CSA_ENVIA_NOTIFICACAO  = "834";
    public static final String TPC_QTD_OPERACOES_LIBERACAO_MARGEM_POR_CSA_EXIGE_CAPTCHA      = "835";
    public static final String TPC_QTD_OPERACOES_LIBERACAO_MARGEM_POR_CSA_EXIGE_OUTRA_SENHA  = "836";
    public static final String TPC_QTD_OPERACOES_LIBERACAO_MARGEM_POR_CSA_BLOQUEIA_USUARIO   = "837";
    public static final String TPC_QTD_HORAS_CONTROLE_OPERACOES_LIBERACAO_MARGEM             = "838";
    public static final String TPC_HABILITAR_INTEGRACAO_SALARYPAY                            = "839";
    public static final String TPC_INTEGRAR_SALARYPAY_A_CIELO                                = "840";
    public static final String TPC_VALIDAR_KYC_FACESWEB_INTEGRACAO_SALARYPAY                 = "841";
    public static final String TPC_VALIDAR_KYC_BANK_AS_SERVICE_INTEGRACAO_SALARYPAY          = "842";
    public static final String TPC_APURACAO_AUTOMATICA_CADASTRO_FACESWEB                     = "843";
    public static final String TPC_FILTRO_VINCULO_CONSULTA_MARGEM                            = "844";
    public static final String TPC_HABILITA_CADASTRO_TAXA_JUROS_EDITAR_CET_MANUTENCAO_CSA    = "845";
    public static final String TPC_EXIBE_MAIS_CAMPOS_PESQUISAR_CONSIGNACAO                   = "846";
    public static final String TPC_EXIBE_MARGEM_SERVIDORES_BLOQUEADOS_CSE_ORG                = "847";
    public static final String TPC_EXIBE_MARGEM_SERVIDORES_BLOQUEADOS_CSA_COR                = "848";
    public static final String TPC_EXIBE_MARGEM_SERVIDORES_BLOQUEADOS_SUP                    = "849";
    public static final String TPC_EXIBE_MARGEM_SERVIDORES_BLOQUEADOS_SER                    = "850";
    // DESENV-16316 Parâmetro modificado para ser parâmetro de serviço
    //public static final String TPC_EXIGE_SENHA_SER_SUSPENDER_CANCELAR_CONSIGNACAO          = "851";
    public static final String TPC_PERMITE_PORTABILIDADE_MARGEM_NEGATIVA                     = "852";
    public static final String TPC_GERA_ARQ_ANEXOS_ADE_MOV_FIN_TODOS_PERIODO                 = "853";
    public static final String TPC_IMPRIMIR_LOG_SQLS_ROTINA_PROCESSAMENTO                    = "854";
    public static final String TPC_IGNORAR_CLASSES_AO_IMPRIMIR_LOG_SQLS_ROTINA_PROCESSAMENTO = "855";
    public static final String TPC_EXIBE_BOTAO_RESPONSAVEL_PELO_RODAPE_DA_PAGINA             = "856";
    // DESENV-16211 Parâmetro modificado para ser parâmetro de serviço
    //public static final String TPC_CONDICIONA_OPERACAO_PORTABILIDADE                       = "857";
    //public static final String TPC_CONDICIONA_OPERACAO_RENEGOCIACAO                        = "858";
    public static final String TPC_REGISTRA_HISTORICO_MARGEM_MESMO_SENDO_IGUAIS              = "859";
    // DESENV-23435 Parâmetro de sistema transformado em parâmetro de JVM
    //public static final String TPC_PATH_SCRIPT_BACKUP                                      = "860";
    //public static final String TPC_PATH_SCRIPT_RESTORE                                     = "861";
    public static final String TPC_EMAIL_NOTIFICACAO_SEGURANCA                               = "862";
    public static final String TPC_ENCERRA_CONSIGNACOES_SERVIDOR_EXCLUIDO_CARGA_MARGEM       = "863";
    public static final String TPC_REABRIR_CONSIGNACOES_ENCERRADAS_CARGA_MARGEM              = "864";
    public static final String TPC_CATEGORIA_RELATORIO_GERENCIAL_GERAL_CORRESPONDE_CARGO_RSE = "865";
    public static final String TPC_MANTEM_STATUS_RSE_BLOQUEADO_MANUALMENTE_CARGA_MARGEM      = "866";
    public static final String TPC_EXECUTAR_ACOES_POR_TIPO_DESCONTO_IMPORTACAO_RETORNO       = "867";
    public static final String TPC_PERMITE_CSA_ESCOLHER_FORMA_NUMERACAO_PARCELAS             = "868";
    public static final String TPC_PADRAO_FORMA_NUMERACAO_PARCELAS                           = "869";
    public static final String TPC_PERMITE_RESERVA_SAUDE_SEM_FLUXO_MODULO_SAUDE              = "870";
    public static final String TPC_EMAIL_ALERTA_CRIACAO_NOVO_USUARIO_CSE_ORG                 = "871";
    public static final String TPC_LIBERA_MARGEM_ENVIADA_PELA_FOLHA_CONSIG_CARENCIA          = "872";
    public static final String TPC_QTD_PERIODO_CALCULO_MEDIA_MARGEM                          = "873";
    public static final String TPC_PERC_VARIACAO_MARGEM_SERVIDOR                             = "874";
    public static final String TPC_SUSPENDER_CONTRATO_PARCELA_REJEITADA_RETORNO              = "875";
    public static final String TPC_URL_EXTERNA_SAIR_SISTEMA_SERVIDOR                         = "876";
    public static final String TPC_EXIBE_MSG_MARGEM_ADEQUEADA_DECISAO_JUDICIAL               = "877";
    public static final String TPC_QUAIS_MARGENS_SERAO_EXIBIDAS_RELATORIO_GER_GERAL          = "878";
    public static final String TPC_EXIBE_CONFIG_SIST_SERVIDOR                                = "879";
    public static final String TPC_CONTRATOS_DEVEM_SER_VALIDADOS_PELA_CSE                    = "880";
    public static final String TPC_HABILITA_MODULO_CREDENCIAMENTO_CSA                        = "881";
//  public static final String TPC_DESBL_CSA_APROVACAO_POR_SUP                               = "882";
    public static final String TPC_URL_BASE_SERVICO_CRM                                      = "883";
    public static final String TPC_MANUTENCAO_CSA_UTILIZA_CRM                                = "884";
    public static final String TPC_HABILITA_PAGINA_BOAS_VINDAS                               = "885";
    public static final String TPC_SUBTRAIR_PARCELAS_AGUARD_PROCESSAMENTO_SOL_PORTABILIDADE  = "886";
    public static final String TPC_TAM_MAX_ARQ_ANEXO_CREDENCIAMENTO                          = "887";
    public static final String TPC_REDIRECIONA_SER_CAD_SENHA                                 = "888";
    public static final String TPC_DIAS_PARA_LEITURA_DE_MENSAGEM_INDIVIDUALMENTE             = "889";
    public static final String TPC_URL_CONVERSOR_AUDIO_MP3_DOCUMENT_PDF                      = "890";
    public static final String TPC_IMPRIMIR_BOLETO_MOBILE                                    = "891";
    public static final String TPC_ATUALIZA_DADOS_SERVIDOR_CAD_SENHA                         = "892";
    public static final String TPC_DOMINIO_EMAIL_CAD_SENHA                                   = "893";
    public static final String TPC_CADASTRO_TELEFONE_OBRIGATORIO_USUARIO_CSE                 = "894";
    public static final String TPC_CADASTRO_TELEFONE_OBRIGATORIO_USUARIO_SUP                 = "895";
    public static final String TPC_EXIBE_MARGEM_CONSIGNAVEL_SERVIDOR_EDITAR_PROPOSTA_LEILAO  = "896";
    public static final String TPC_ENVIA_EMAIL_FIM_FLUXO_CAD_SENHA                           = "897";
    public static final String TPC_PERMITE_EXPORTAR_MOVIMENTO_DATA_FIM_FUTURA                = "898";
    public static final String TPC_BLOQUEIA_CSA_POR_CET_EXPIRADO                             = "899";
    public static final String TPC_LISTAGEM_ATALHOS_HOME                                     = "900";
    public static final String TPC_CONSIDERA_PARCELA_PAGA_PARCIAL_ALONGAMENTO_PRD_REJEITADA  = "901";
    public static final String TPC_BLOQUEIA_USU_INATIVIDADE_PROXIMA_AUTENTICACAO             = "902";
    public static final String TPC_LISTAR_MOTIVO_ADES_NAO_RENEGOCIAVEIS                      = "903";
    public static final String TPC_ENVIA_OTP_RECUPERACAO_SENHA_USU                           = "904";
    public static final String TPC_ENVIA_OTP_RECUPERACAO_SENHA_SERVIDOR                      = "905";
    public static final String TPC_CALCULAR_VALOR_PAGO_EXPORTACAO_MOVIMENTO                  = "906";
    public static final String TPC_LST_SERVICOS_CSA_COR_ABAIXO_LIMITE_CONTRATOS              = "907";
    public static final String TPC_TAM_MAX_UPLOAD_ARQUIVO_FOTO_SERVIDOR                      = "908";
    public static final String TPC_PERMITE_SER_BLOQUEADO_SIMULAR_SEM_CONCLUIR                = "909";
    public static final String TPC_EXPORTA_MOVIMENTO_ORGAO_AUTOMATICAMENTE                   = "910";
    public static final String TPC_PERMITE_REVOGAR_DECISAO_JUDICIAL                          = "911";
    public static final String TPC_CADASTRO_SIMPLIFICADO_ESTABELECIMENTO                     = "912";
    public static final String TPC_LOTE_CONTEM_TODAS_ADES_ATIVAS                             = "913";
    public static final String TPC_ENVIA_EMAIL_ENTIDADES_QNDO_ALTERA_PERFIL                  = "914";
    public static final String TPC_INSERE_ANEXO_CREDENCIAMENTO_CONSIGNATARIA                 = "915";
    public static final String TPC_GERA_ARQUIVO_RESCISAO                                     = "916";
    public static final String TPC_CONVERTE_AUTOMATICAMENTE_LAYOUT_RET_ORGAO_PARA_GERAL      = "917";
    public static final String TPC_ARQ_CONF_ENTRADA_RESCISAO                                 = "918";
    public static final String TPC_ARQ_CONF_TRADUTOR_RESCISAO                                = "919";
    public static final String TPC_ENVIA_EMAIL_SER_AUT_PENDENTE_SALDO_INSUF_VERBA_RESCISORIA = "920";
    public static final String TPC_EXIBE_HISTORICO_MARGEM_DETALHE_CONSIGNACAO                = "921";
    public static final String TPC_TAMANHO_MAX_ARQUIVO_ANEXADO_CSA                           = "922";
    public static final String TPC_ALTERA_MATRICULA_REGISTRO_SERVIDOR_IMP_FALECIDO           = "923";
    public static final String TPC_MAX_CONSULTA_MARGEM_SER                                   = "924";
    public static final String TPC_CONCLUI_CONTRATOS_SUSPENSOS_QUE_PASSARAM_DA_DATA_FIM      = "925";
    public static final String TPC_EXIBE_TUTORIAL_MOBILE                                     = "926";
    public static final String TPC_OMITIR_SERVIDORES_SEM_ACEITE_TERMO_DE_USO_PARA_CSA_COR    = "927";
    public static final String TPC_CONSOLIDA_DESCONTOS_RETORNO                               = "928";
    public static final String TPC_ASSINATURA_DIGITAL_CONSIGNACAO_SOMENTE_CERT_DIGITAL       = "929";
    public static final String TPC_EXIGE_RECONHECIMENTO_FACIL_SOLICITACAO_SERVIDOR           = "930";
    public static final String TPC_CLASSE_ESPECIFICA_PROCESSAMENTO_SEM_BLOQUEIO              = "931";
    public static final String TPC_H_CAPTCHA_CHAVE_PUBLICA                                   = "932";
    public static final String TPC_H_CAPTCHA_CHAVE_PRIVADA                                   = "933";
    public static final String TPC_TIPO_CAPTCHA_AVANCADO_LOGIN                               = "934";
    public static final String TPC_ENVIAR_EMAIL_APOS_PRI_ACESSO                              = "935";
    public static final String TPC_SIMULADOR_COM_CET_TIPO_OPERACAO                           = "936";
    public static final String TPC_SOLICITAR_PORTABILIDADE_COM_RANKING_CONSIGNATARIA         = "937";
    public static final String TPC_CONTRATOS_ESTOQUE_NAO_CONTABILIZAM_MARGEM                 = "938";
    public static final String TPC_URL_CONSULTAR_MARGEM_SISTEMA_EXTERNO                      = "940";
    public static final String TPC_EXIBIR_COLUNA_VLR_TOTAL_PAGAMENTO_NA_SIMULACAO            = "941";
    public static final String TPC_ADEQUAR_MARGEM_SERVIDOR_CONFORME_MARGEM_LIMITE            = "942";
    public static final String TPC_APLICAR_REGRAS_LIMITE_VARIACAO_CET_PARA_TAXA_JUROS        = "943";
    public static final String TPC_OMITIR_PROPOSTA_REFINANCIAMENTO_EDT_SLD_DEVEDOR           = "944";
    public static final String TPC_PREENCHER_VINCULO_REGISTRO_RSE_IMP_CAD_MARGEM             = "945";
    public static final String TPC_REQUER_DATA_NASC_CONS_SERVIDOR_CSA                        = "946";
    public static final String TPC_VERIFICA_CARENCIA_CONCLUSAO_APENAS_COM_SDV_PARCELAS_PAGAS = "947";
    public static final String TPC_RECONHECIMENTO_FACIAL_ACESSO_SERVIDOR                     = "948";
    public static final String TPC_COLOCAR_EM_CARENCIA_LIQUIDACAO_MANUAL_ULT_PARCELA         = "949";
    public static final String TPC_EXIBE_MARGEM_BRUTA_VARIACAO_MARGEM_CSA                    = "950";
    public static final String TPC_CONTRATOS_NAO_SELECIONADOS_PARTICIPAM_ALT_MULT_CONTRATOS  = "951";
    public static final String TPC_CLASSE_BUSCA_EMAIL_SERVIDOR_API_EXTERNA                   = "952";
    public static final String TPC_URL_BUSCA_EMAIL_SERVIDOR_API_EXTERNA                      = "953";
    public static final String TPC_ENVIAR_EMAIL_CONSULTA_MARGEM                              = "954";
    public static final String TPC_POSSIBILIDADE_INCIDIR_MARGEM_SUSPENSAO_REATIVACAO         = "955";
    public static final String TPC_QUANTIDADE_DIAS_MANTER_HIST_OCORRENCIA_AGENDAMENTO        = "956";
    public static final String TPC_CANCELAMENTO_RENEGOCIACAO_APOS_DATA_CORTE                 = "957";
    public static final String TPC_OCULTAR_INFO_COR_RELATORIO_GERENCIAL_GERAL                = "958";
    public static final String TPC_DIAS_VALIDADE_AUTORIZACAO_SERVIDOR_CON_MAR_POR_COD        = "959";
    public static final String TPC_PRENDER_MARGEM_RENEG_CONTRATRO_NOVO_MENOR_ANTIGOS         = "960";
    public static final String TPC_ENVIAR_EMAIL_SER_RESERVA_MARGEM_APOS_OCORRER_SEM_SENHA    = "961";
    public static final String TPC_DIAS_VENCIMENTO_AUTORIZACAO_SER_MARGEM                    = "962";
    public static final String TPC_REATIVAR_CONTRATO_SUSP_PRD_REJEITADA_EXIGE_CONF_GESTOR    = "963";
    public static final String TPC_CONCLUIR_CONTRATOS_APOS_GERAR_ARQ_RESCISAO                = "964";
    public static final String TPC_OCULTAR_DIA_CORTE_DIA_REPASSE_DO_RELATORIO_GERENCIAL_CSA  = "965";
    public static final String TPC_INDICE_UNICO_REGISTRO_SER_INDEPENDENTE_CONVENIO           = "966";
    public static final String TPC_QTDE_DIAS_BLOQ_CSA_SOLIC_SALDO_DEVEDOR_NAO_ATENDIDA       = "967";
    public static final String TPC_CRIA_AUTO_CONTRATO_RESCISAO_APOS_INFO_SALDO_DEVEDOR       = "968";
    public static final String TPC_ENVIA_EMAIL_SER_NOVO_CONTRATO_PAG_SALDO_DEVEDOR_RESCISAO  = "969";
    public static final String TPC_PERMITE_PORTABILIDADE_CARTAO                              = "970";
    public static final String TPC_ENVIAR_COMUNICACAO_CRIADA_EMAIL_USUARIO                   = "971";
    public static final String TPC_ENVIAR_NOTIFICAO_GESTOR_SOBRE_BLOQUEIO_CONSIGNATARIAS     = "972";
    public static final String TPC_ENVIAR_DADOS_SERVIDOR_CSA_REJEITADA_PROPOSTA_LEILAO       = "973";
    public static final String TPC_ENVIAR_EMAIL_CONSIGNATARIA_VENCEDORA_DO_LEILAO            = "974";
    public static final String TPC_ENVIAR_INFOS_CSA_NO_EMAIL_SER_PROPOSTA_LEILAO_APROVADA    = "975";
    public static final String TPC_EXIGE_SENHA_OPERACOES_SENSIVEIS_SER                       = "976";
    public static final String TPC_ENVIA_OTP_BLOQUEIO_VERBAS_SERVIDOR                        = "977";
    public static final String TPC_QUANTIDADE_MINIMA_CONTRATOS_CSA_NO_DIA_NOTIFICAR_GESTOR   = "978";
    public static final String TPC_QUANTIDADE_DIAS_NOTIFICACAO_EXPIRACAO_SENHA_CSA_COR       = "979";
    public static final String TPC_QUANTIDADE_DIAS_NOTIFICACAO_EXPIRACAO_SENHA_CSE_ORG       = "980";
    public static final String TPC_QUANTIDADE_DIAS_NOTIFICACAO_EXPIRACAO_SENHA_SER           = "981";
    public static final String TPC_ENVIAR_EMAIL_ALERTA_USUARIO_BLOQUEADO_LOGIN_MALSUCEDIDO   = "982";
    public static final String TPC_IMPEDE_CPF_IGUAL_ENTRE_USU_MESMA_CSA                      = "983";
    public static final String TPC_URL_VALIDACAO_EXTERNA_GET                                 = "984";
    public static final String TPC_HABILITAR_MODULO_PERFIL_CONSIGNADO                        = "985";
    public static final String TPC_ENCERRA_SESSAO_NAVEGACAO_NOVA_ABA                         = "986";
    public static final String TPC_EXIBE_CONTRATOS_NAO_INCIDEM_MARGEM_EXTRATO_MARGEM         = "987";
    public static final String TPC_ENVIA_SENHA_EMAIL_CRIACAO_SENHA_NOVO_USU                  = "988";
    public static final String TPC_URL_BASE_PORTAL_EMPREGA_BRASIL                            = "989";
    public static final String TPC_HABILITAR_CONTROLE_DOCUMENTO_MARGEM                       = "990";
    public static final String TPC_URL_BASE_SERVICO_CERT                                     = "991";
    public static final String TPC_CSA_PODE_CADASTRAR_CET_MININO_E_MAXIMO                    = "992";
    public static final String TPC_URL_BASE_AUTH_SERASA                                      = "993";
    public static final String TPC_URL_BASE_CONSENT_SERASA                                   = "994";
    public static final String TPC_CONSENT_SERASA_OAUTH_CLIENT_ID                            = "995";
    public static final String TPC_CONSENT_SERASA_OAUTH_CLIENT_SECRET                        = "996";
    public static final String TPC_TIPO_PERMITIDO_TOTP_USUARIO_CSE_ORG                       = "997";
    public static final String TPC_TIPO_PERMITIDO_TOTP_USUARIO_CSA_COR                       = "998";
    public static final String TPC_TIPO_PERMITIDO_TOTP_USUARIO_SUP                           = "999";
    public static final String TPC_TIPO_PERMITIDO_TOTP_USUARIO_SER                           = "1000";
    public static final String TPC_PERMITE_USUARIO_REMOVER_TOTP                              = "1001";
    public static final String TPC_ROTINA_POS_PROCESSAMENTO_PORTAL_EMPREGA_BRASIL            = "1002";
    public static final String TPC_PRIORIZA_PAG_PARCELAS_CONTRATOS_EXPORTADOS                = "1003";
    public static final String TPC_SEMPRE_EXIBIR_VALOR_MARGEM_CSE_ORG                        = "1004";
    public static final String TPC_EXIBE_CALCULO_MARGEM_PORTAL_SERVIDOR                      = "1005";
    public static final String TPC_QTD_MESES_EXPIRACAO_CADASTRO_CSA_CREDENCIAMENTO           = "1008";
    public static final String TPC_EXTENSOES_PERMITIDAS_UPLOAD_ANEXO                         = "1009";
    public static final String TPC_QUANTIDADE_PUSH_MAXIMO_ATINGIDO_MES                       = "1010";

    /* Agrega parâmetros de sistema que definem tamanho máximos de arquivos carregos no eConsig*/
    public static final List<String> TPC_CODIGOS_TAM_MAX_ARQUIVOS = Arrays.asList(
            TPC_TAM_MAX_ARQ_BANNER,
            TPC_TAM_MAX_ARQ_ANEXO_CONTRATO,
            TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSE_ORG,
            TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSA_COR,
            TPC_TAM_MAX_ARQ_ANEXO_COMUNICACAO,
            TPC_TAM_MAX_ARQ_ANEXO_CONTRATO_ASSINATURA_DIGITAL,
            TPC_TAM_MAX_ARQ_UPLOAD_EM_LOTE_ANEXO,
            TPC_TAMANHO_ANEXO_BENEFICIARIO
    );

    // Valor inicial padrão para verba_convenio.
    public static final BigDecimal VLR_INI_VCO = new BigDecimal("99999999999.99");

    /* Valores para o parâmetro TPC_DADOS_SERVIDOR_EMAIL_ALTERACAO_SENHA */
    public static final String DADOS_SERVIDOR_EMAIL_MATRICULA           = "MATRICULA";
    public static final String DADOS_SERVIDOR_EMAIL_SENHA               = "SENHA";
    public static final String DADOS_SERVIDOR_EMAIL_SEPARADOR           = ";";

    /* Valores para o parâmetro TPC_EMAIL_ALTERACAO_SENHA_AUT_SERVIDOR */
    public static final String ALTERACAO_SENHA_AUT_SER_ENVIA_EMAIL            = "1";
    public static final String ALTERACAO_SENHA_AUT_SER_EXIBE_TELA             = "2";
    public static final String ALTERACAO_SENHA_AUT_SER_EMAIL_OU_TELA          = "3";
    public static final String ALTERACAO_SENHA_AUT_SER_SMS                    = "4";
    public static final String ALTERACAO_SENHA_AUT_SER_SMS_E_EMAIL            = "5";
    public static final String ALTERACAO_SENHA_AUT_SER_EMAIL_E_TELA           = "6";
    public static final String ALTERACAO_SENHA_AUT_SER_RECONHECIMENTO_FACIAL  = "7";

    /* Valores para o parâmetro TPC_EMAIL_ANEXO_SALDO_DEVEDOR_SERVIDOR */
    public static final String ANEXO_SALDO_DEVEDOR_SERVIDOR_ENVIA_EMAIL  = "1";
    public static final String ANEXO_SALDO_DEVEDOR_SERVIDOR_EXIBE_TELA   = "2";
    public static final String ANEXO_SALDO_DEVEDOR_SERVIDOR_EMAIL_E_TELA = "3";

    /* Valores para o parâmetro TPC_DADOS_SERVIDOR_EMAIL_ALTERACAO_SENHA */
    public static final String OPERACAO_EXIGE_SEGUNDA_SENHA_NAO       = "N";
    public static final String OPERACAO_EXIGE_SEGUNDA_SENHA_SIM       = "S";
    public static final String OPERACAO_EXIGE_SEGUNDA_SENHA_PROPRIA   = "P";
    public static final String OPERACAO_ADICIONA_FILA_SEGUNDA_SENHA   = "F";

    /* Valores para o parâmetro TPC_PERIODICIDADE_FOLHA */
    public static final String PERIODICIDADE_FOLHA_MENSAL      = "M";
    public static final String PERIODICIDADE_FOLHA_QUINZENAL   = "Q";
    public static final String PERIODICIDADE_FOLHA_QUATORZENAL = "G";
    public static final String PERIODICIDADE_FOLHA_SEMANAL     = "S";

    /* Valores para o parâmetro TPC_INFORMA_NUM_PORTABILIDADE_CIP_COMPRA */
    public static final String NUM_PORTABILIDADE_CIP_COMPRA_AUSENTE     = "0";
    public static final String NUM_PORTABILIDADE_CIP_COMPRA_OPCIONAL    = "1";
    public static final String NUM_PORTABILIDADE_CIP_COMPRA_OBRIGATORIO = "2";

    /*Valores para o parâmetro TPC_REQUER_TEL_SER_SOLIC_SALDO_DEVEDOR*/
    public static final String TEL_SER_SOLIC_SALDO_DEVEDOR_AUSENTE     ="N";
    public static final String TEL_SER_SOLIC_SALDO_DEVEDOR_OPCIONAL    ="S";
    public static final String TEL_SER_SOLIC_SALDO_DEVEDOR_OBRIGATORIO ="O";

    /* Valores para o parâmetro TPC_INFORMA_ANEXO_ADE_DOC_ADICIONAL_COMPRA */
    public static final String ANEXO_ADE_DOC_ADICIONAL_COMPRA_AUSENTE     = "0";
    public static final String ANEXO_ADE_DOC_ADICIONAL_COMPRA_OPCIONAL    = "1";
    public static final String ANEXO_ADE_DOC_ADICIONAL_COMPRA_OBRIGATORIO = "2";

    /* Valores para o parâmetro TPC_INF_SALDO_DEVEDOR_OPCIONAL_COMPRA */
    public static final String INF_SALDO_DEVEDOR_COMPRA_AUSENTE     = "0";
    public static final String INF_SALDO_DEVEDOR_COMPRA_OPCIONAL    = "1";
    public static final String INF_SALDO_DEVEDOR_COMPRA_OBRIGATORIO = "2";

    /* Valores para o parâmetro TPC_VALIDA_VLR_E_OU_PRZ_EXPORTA_ALT_SEM_ANEXO */
    public static final Integer VALIDA_VLR_EXPORTA_ALT_SEM_ANEXO        = 0;
    public static final Integer VALIDA_PRZ_EXPORTA_ALT_SEM_ANEXO        = 1;
    public static final Integer VALIDA_VLR_E_PRZ_EXPORTA_ALT_SEM_ANEXO  = 2;
    public static final Integer VALIDA_VLR_OU_PRZ_EXPORTA_ALT_SEM_ANEXO = 3;

    /* Valores para o parâmetro TPC_ORDEM_PRIORIDADE_SUBSIDIO_GRUPO_FAMILIAR */
    public static final String ORDEM_PRIORIDADE_SUBSIDIO_GP_FAMILIAR_NENHUM           = "N";
    public static final String ORDEM_PRIORIDADE_SUBSIDIO_GP_FAMILIAR_DEPENDENCIA      = "O";
    public static final String ORDEM_PRIORIDADE_SUBSIDIO_GP_FAMILIAR_GRAU_PARENTESCO  = "G";

    /* Valores para o parâmetro TPC_CONSIDERA_ALTERACAO_EXPORTACAO_SEMANAL */
    // public static final String CONSIDERA_VALOR_ALTERACAO_EXPORTACAO_SEMANAL       = "1";
    // public static final String CONSIDERA_PRAZO_ALTERACAO_EXPORTACAO_SEMANAL       = "2";
    // public static final String CONSIDERA_VALOR_PRAZO_ALTERACAO_EXPORTACAO_SEMANAL = "3";

    /* Valores para o parâmetro TPC_IDENTIFICACAO_SEMANA_EXPORTACAO_SEMANAL */
    // public static final String IDENTIFICACAO_SEMANA_EXPORTACAO_SEMANAL_NUM_SEMANA   = "1";
    // public static final String IDENTIFICACAO_SEMANA_EXPORTACAO_SEMANAL_ORDEM_SEMANA = "2";

    /* Valores para o parametro */
    public static final String RESTRINGE_PORTABILIDADE_MARGEM_NEGATIVA                        = "M";
    public static final String RESTRINGE_PORTABILIDADE_PARCELA_REJEITADA                      = "P";
    public static final String RESTRINGE_PORTABILIDADE_MARGEM_NEGATIVA_E_PARCELA_REJEITADA    = "MAP";
    public static final String RESTRINGE_PORTABILIDADE_MARGEM_NEGATIVA_OU_PARCELA_REJEITADA   = "MOP";

    /* Valores para o parametro TPC_PADRAO_FORMA_NUMERACAO_PARCELAS */
    public static final String FORMA_NUMERACAO_PARCELAS_SEQUENCIAL = "1";
    public static final String FORMA_NUMERACAO_PARCELAS_MANTEM_AO_REJEITAR = "2";

    /* Valores para o parâmetro TPC_HABILITA_EMAIL_CONTRATOS_REJEITADOS_FOLHA */
    public static final String NOTIFICA_CONTRATOS_REJEITADOS_FOLHA_NAO         = "N";
    public static final String NOTIFICA_CONTRATOS_REJEITADOS_FOLHA_OPCIONAL    = "S";
    public static final String NOTIFICA_CONTRATOS_REJEITADOS_FOLHA_OBRIGATORIO = "O";

    /* Valores para o parâmetro TPC_AUTO_DESBLOQUEIO_SERVIDOR */
    public static final String AUTO_DESBLOQUEIO_SERVIDOR_DESABILITADO         = "0";
    public static final String AUTO_DESBLOQUEIO_SERVIDOR_EMAIL                = "1";
    public static final String AUTO_DESBLOQUEIO_SERVIDOR_SMS                  = "2";
    public static final String AUTO_DESBLOQUEIO_SERVIDOR_EMAIL_SMS            = "3";

    /* Valores para o parâmetro TPC_CONCLUI_CONTRATOS_SUSPENSOS_QUE_PASSARAM_DA_DATA_FIM */
    public static final String CONCLUI_CONTRATOS_SUSPENSOS_DESABILITADO         = "0";
    public static final String CONCLUI_CONTRATOS_SUSPENSOS_PELA_CSA             = "1";
    public static final String CONCLUI_CONTRATOS_SUSPENSOS_PELA_CSE             = "2";
    public static final String CONCLUI_CONTRATOS_SUSPENSOS_PELA_CSA_CSE         = "3";

    /* Tipos de parametro de orgao */
    //public static final String TAO_DIVIDIR_COMANDO_POR_SEMANA_EXPORTACAO        = "1";
    public static final String TAO_CALENDARIO_FISCAL_UTILIZADO_NOME_ARQUIVO     = "2";
    //public static final String TAO_CALENDARIO_FISCAL_UTILIZADO_CONTAGEM_SEMANAS = "3";
    //public static final String TAO_DIA_SEMANA_CALCULO_QTD_SEMANAS_PERIODO       = "4";
    //public static final String TAO_IDENTIFICACAO_SEMANA_EXPORTACAO_SEMANAL      = "5";

    /* Tipos de parâmetro de serviço. */
    public static final String TPS_TAC_FINANCIADA                                                  = "1";
    public static final String TPS_INTEGRA_FOLHA                                                   = "2";
    public static final String TPS_INCIDE_MARGEM                                                   = "3";
    public static final String TPS_TIPO_VLR                                                        = "4";
    public static final String TPS_ADE_VLR                                                         = "5";
    public static final String TPS_ALTERA_ADE_VLR                                                  = "6";
    public static final String TPS_MAX_PRAZO                                                       = "7";
    public static final String TPS_REQUER_DEFERIMENTO_RESERVAS                                     = "8";
    public static final String TPS_DIAS_DESBL_RES_NAO_CONF                                         = "9";
    public static final String TPS_DIAS_DESBL_CONSIG_NAO_DEF                                       = "10";
    public static final String TPS_DIAS_DESBL_SOLICITACAO_NAO_CONF                                 = "11";
    public static final String TPS_SER_SENHA_OBRIGATORIA_CSA                                       = "12";
    public static final String TPS_SER_SENHA_OBRIGATORIA_CSE                                       = "13";
    public static final String TPS_OP_FINANCIADA                                                   = "14";
    public static final String TPS_CARENCIA_MINIMA                                                 = "15";
    public static final String TPS_CARENCIA_MAXIMA                                                 = "16";
    public static final String TPS_VLR_LIBERADO_MINIMO                                             = "17";
    public static final String TPS_VLR_LIBERADO_MAXIMO                                             = "18";
    //public static final String TPS_VALIDA_MATRICULA_MARGEM                                       = "19";
    public static final String TPS_NUM_CONTRATOS_POR_CONVENIO                                      = "20";
    //public static final String TPS_RELACIONAMENTO                                                = "21";
    //public static final String TPS_NATUREZA_RELACIONAMENTO                                       = "22";
    public static final String TPS_VLR_LIMITE_ADE_SEM_MARGEM                                       = "23";
    //public static final String TPS_PRIV_COD_LOJ                                                  = "24";
    //public static final String TPS_PRIV_COD_PRO                                                  = "25";
    //public static final String TPS_PRIV_COD_GER                                                  = "26";
    //public static final String TPS_SEGURO                                                        = "27";
    //public static final String TPS_DADOS_CADASTRO                                                = "28";
    //public static final String TPS_PRIV_CALCULA_MARGEM                                           = "29";
    //public static final String TPS_PRIV_FATOR_CALCULO_MARGEM_A                                   = "30";
    //public static final String TPS_PRIV_FATOR_CALCULO_MARGEM_B                                   = "31";
    public static final String TPS_PERMITE_IMPORTACAO_LOTE                                         = "32";
    public static final String TPS_PERMITE_AUM_VLR_PRZ_CONSIGNACAO                                 = "33";
    //public static final String TPS_CAD_INF_FINANCEIRAS                                           = "34";
    public static final String TPS_REIMPLANTACAO_AUTOMATICA                                        = "35";
    public static final String TPS_PRESERVA_PRD_REJEITADA_REIMPL                                   = "36";
    //public static final String TPS_DIA_VENCIMENTO                                                = "37";
    //public static final String TPS_TIPO_CARENCIA                                                 = "38";
    //public static final String TPS_IOF_FINANCIADO                                                = "39";
    //public static final String TPS_PRIV_TIPO_CADASTRO                                            = "40";
    public static final String TPS_INDICE                                                          = "41";
    public static final String TPS_VLR_LIMITE_ADE_SEM_MARGEM_ALTER                                 = "42";
    public static final String TPS_CARENCIA_FINAL                                                  = "43";
    public static final String TPS_PRAZO_CARENCIA_FINAL                                            = "44";
    public static final String TPS_PERMITE_REPETIR_INDICE_CSA                                      = "45";
    //public static final String TPS_TIPO_IMPRESSAO_BOLETO                                         = "46";
    public static final String TPS_VLR_PERC_MAXIMO_PARCELA_ALONGAMENTO                             = "47";
    public static final String TPS_VLR_LIQ_TAXA_JUROS                                              = "48";
    public static final String TPS_PERMITE_CONTRATO_SUPER_SER_CSA                                  = "49";
    public static final String TPS_PRESERVA_DATA_RENEGOCIACAO                                      = "50";
    public static final String TPS_PRESERVA_DATA_MAIS_ANTIGA_RENEG                                 = "51";
    public static final String TPS_DIAS_DESBL_COMP_NAO_CONF                                        = "52";
    public static final String TPS_PRAZO_FIXO                                                      = "53";
    public static final String TPS_INF_BANCARIA_OBRIGATORIA                                        = "54";
    //public static final String TPS_VALOR_MAX_TAXA_JUROS                                          = "55";
    public static final String TPS_DATA_LIMITE_DIGIT_TAXA                                          = "56";
    public static final String TPS_DATA_ABERTURA_TAXA                                              = "57";
    public static final String TPS_INCLUI_ALTERANDO_MESMO_PERIODO                                  = "58";
    public static final String TPS_VALIDAR_TAXA_JUROS                                              = "59";
    public static final String TPS_CONCLUI_ADE_NAO_PAGA                                            = "60";
    public static final String TPS_GRUPO_SERVICO                                                   = "61";
    public static final String TPS_CONTROLA_SALDO                                                  = "62";
    public static final String TPS_CONTROLA_VLR_MAX_DESCONTO                                       = "63";
    public static final String TPS_EXIBE_CONTRATO_SERVIDOR                                         = "64";
    public static final String TPS_EXIBE_CAPITAL_DEVIDO                                            = "65";
    public static final String TPS_EXIGE_SEGURO_PRESTAMISTA                                        = "66";
    public static final String TPS_PERMITE_ALTERACAO_CONTRATOS                                     = "67";
    public static final String TPS_PERMITE_RENEGOCIACAO                                            = "68";
    public static final String TPS_QTDE_MAX_ADE_RENEGOCIACAO                                       = "69";
    public static final String TPS_EXIGE_SENHA_ALTERACAO_CONTRATOS                                 = "70";
    public static final String TPS_PERMITE_LIQUIDAR_PARCELA                                        = "71";
    //public static final String TPS_EXIGE_DATA_HORA_OCORRENCIA                                    = "72";
    public static final String TPS_BANCO_DEPOSITO_SALDO_DEVEDOR                                    = "73";
    public static final String TPS_AGENCIA_DEPOSITO_SALDO_DEVEDOR                                  = "74";
    public static final String TPS_CONTA_DEPOSITO_SALDO_DEVEDOR                                    = "75";
    public static final String TPS_EMAIL_INF_CONTRATOS_COMPRADOS                                   = "76";
    public static final String TPS_EMAIL_INF_SALDO_DEVEDOR                                         = "77";
    public static final String TPS_EMAIL_INF_PGT_SALDO_DEVEDOR                                     = "78";
    public static final String TPS_EMAIL_INF_LIQ_CONTRATO_COMPRADO                                 = "79";
    public static final String TPS_NOME_FAVORECIDO_DEPOSITO_SDV                                    = "80";
    public static final String TPS_CNPJ_FAVORECIDO_DEPOSITO_SDV                                    = "81";
    public static final String TPS_POSSUI_CORRECAO_SALDO_DEVEDOR                                   = "82";
    public static final String TPS_FORMA_CALCULO_CORRECAO_SALDO_DV                                 = "83";
    public static final String TPS_CORRECAO_SOBRE_TOTAL_SALDO_DV                                   = "84";
    public static final String TPS_CORRECAO_ENVIADA_APOS_PRINCIPAL                                 = "85";
    public static final String TPS_ADD_VALOR_TAC_VAL_TAXA_JUROS                                    = "86";
    public static final String TPS_ADD_VALOR_IOF_VAL_TAXA_JUROS                                    = "87";
    public static final String TPS_VALIDA_MENS_VINC_VAL_TAXA_JUROS                                 = "88";
    public static final String TPS_TIPO_TAC                                                        = "89";
    public static final String TPS_VALOR_MIN_TAC                                                   = "90";
    public static final String TPS_VALOR_MAX_TAC                                                   = "91";
    public static final String TPS_EXCEDENTE_MONETARIO_TX_JUROS                                    = "92";
    public static final String TPS_PRESERVA_DATA_ALTERACAO                                         = "93";
    public static final String TPS_SERVICO_COMPULSORIO                                             = "94";
    public static final String TPS_RETIRAVEL_POR_SVC_COMP_PRIORITARIO                              = "95";
    public static final String TPS_IMPORTA_CONTRATOS_SEM_PROCESSAMENTO                             = "96";
    public static final String TPS_POSSUI_CORRECAO_VALOR_PRESENTE                                  = "97";
    public static final String TPS_FORMA_CALCULO_CORRECAO_VLR_PRESENTE                             = "98";
    public static final String TPS_POSSUI_CONTROLE_TETO_DESCONTO                                   = "99";
    public static final String TPS_PERCENTUAL_MAXIMO_PERMITIDO_VLR_REF                             = "100";
    public static final String TPS_QUANTIDADE_MAXIMA_CONTRATOS_SVC                                 = "101";
    //public static final String TPS_VALOR_MAX_TAXA_JUROS_1                                        = "102";
    //public static final String TPS_VALOR_MAX_TAXA_JUROS_2                                        = "103";
    //public static final String TPS_VALOR_MAX_TAXA_JUROS_3                                        = "104";
    public static final String TPS_CALCULO_VALOR_ACUMULADO                                         = "105";
    public static final String TPS_BUSCA_BOLETO_EXTERNO                                            = "106";
    public static final String TPS_CAD_VALOR_TAC                                                   = "107";
    public static final String TPS_CAD_VALOR_IOF                                                   = "108";
    public static final String TPS_CAD_VALOR_LIQUIDO_LIBERADO                                      = "109";
    public static final String TPS_CAD_VALOR_MENSALIDADE_VINC                                      = "110";
    public static final String TPS_CNV_PODE_DEFERIR                                                = "111";
    //public static final String TPS_SER_SENHA_DEFERE_RESERVA                                      = "112";
    public static final String TPS_CALC_TAC_IOF_VALIDA_TAXA_JUROS                                  = "113";
    public static final String TPS_PER_RESTRICAO_CAD_NOVA_ADE_CNV_RSE                              = "114";
    public static final String TPS_SOMA_IOF_SIMULACAO_RESERVA                                      = "115";
    public static final String TPS_VALIDAR_DATA_NASCIMENTO_NA_RESERVA                              = "116";
    public static final String TPS_VALIDAR_INF_BANCARIA_NA_RESERVA                                 = "117";
    public static final String TPS_VLR_MINIMO_CONTRATO                                             = "118";
    public static final String TPS_VLR_MAXIMO_CONTRATO                                             = "119";
    public static final String TPS_SERVICO_TIPO_GAP                                                = "120";
    public static final String TPS_MES_INICIO_DESCONTO_GAP                                         = "121";
    public static final String TPS_PERMITE_CANCELAR_CONTRATOS                                      = "122";
    public static final String TPS_PERMITE_LIQUIDAR_CONTRATOS                                      = "123";
    public static final String TPS_PERMITE_SERVIDOR_SOLICITAR                                      = "124";
    public static final String TPS_CLASSE_JAVA_PROC_ESPECIFICO_RESERVA                             = "125";
    public static final String TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR                                 = "126";
    public static final String TPS_INCLUI_ALTERANDO_QUALQUER_PERIODO                               = "127";
    //public static final String TPS_QTD_MINIMA_PARCELAS_PERMITIDAS                                = "128";
    public static final String TPS_MSG_EXIBIR_SOLICITACAO_SERVIDOR                                 = "129";
    public static final String TPS_NUM_CONTRATOS_POR_SERVICO                                       = "130";
    public static final String TPS_ATUALIZA_ADE_VLR_NO_RETORNO                                     = "131";
    public static final String TPS_CALCULA_SALDO_SOMENTE_VINCENDO                                  = "132";
    public static final String TPS_QTD_CSA_PERMITIDAS_SIMULADOR                                    = "133";
    public static final String TPS_BLOQUEIA_RESERVA_LIMITE_SIMULADOR                               = "134";
    public static final String TPS_LIMITE_AUMENTO_VALOR_ADE                                        = "135";
    public static final String TPS_PODE_INCLUIR_NOVOS_CONTRATOS                                    = "136";
    public static final String TPS_PERMITE_DATA_RETROATIVA_IMP_LOTE                                = "137";
    public static final String TPS_EXIGE_SENHA_CONFIRMACAO_SOLICITACAO                             = "138";
    public static final String TPS_CSA_DEVE_CONTAR_PARA_LIMITE_RANKING                             = "139";
    public static final String TPS_EMAIL_INF_SOLICITACAO_SALDO_DEVEDOR                             = "140";
    //public static final String TPS_UTILIZA_SERVIDOR_COM_MAIOR_MARGEM                             = "141";
    public static final String TPS_PERCENTUAL_LIMITE_DESCONTO                                      = "142";
    public static final String TPS_PRAZO_ENTRE_SOLICITACOES_SDO_DEVEDOR                            = "143";
    public static final String TPS_EXIGE_ACEITE_TERMO_ADESAO                                       = "144";
    public static final String TPS_LIMITA_SALDO_DEVEDOR_CADASTRADO                                 = "145";
    public static final String TPS_MARGEM_ERRO_LIMITE_SALDO_DEVEDOR                                = "146";
    public static final String TPS_QTDE_MAX_ADE_COMPRA                                             = "147";
    public static final String TPS_ORDENACAO_CADASTRO_TAXAS                                        = "148";
    public static final String TPS_DIAS_INF_SALDO_DV_CONTROLE_COMPRA                               = "149";
    public static final String TPS_DIAS_INF_PGT_SALDO_CONTROLE_COMPRA                              = "150";
    public static final String TPS_DIAS_LIQUIDACAO_ADE_CONTROLE_COMPRA                             = "151";
    public static final String TPS_ACAO_PARA_NAO_INF_SALDO_DV                                      = "152";
    public static final String TPS_ACAO_PARA_NAO_INF_PGT_SALDO                                     = "153";
    public static final String TPS_ACAO_PARA_NAO_LIQUIDACAO_ADE                                    = "154";
    public static final String TPS_MINIMO_PRD_PAGAS_RENEGOCIACAO                                   = "155";
    public static final String TPS_CSA_LIMITE_SUPERIOR_TABELA_JUROS_CET                            = "156";
    public static final String TPS_DIAS_VIGENCIA_TAXAS_SUPERIOR_LIMITE                             = "157";
    public static final String TPS_TABELA_CORRECAO_CALCULO_SPREAD                                  = "158";
    public static final String TPS_PERMITE_SER_BLOQUEAR_SERVICO_VERBA                              = "159";
    public static final String TPS_NUMERAR_CONTRATOS_SERVIDOR                                      = "160";
    public static final String TPS_PRAZO_ATEND_SOLICIT_SALDO_DEVEDOR                               = "161";
    public static final String TPS_CSA_EXIGE_SERVIDOR_CORRENTISTA                                  = "162";
    public static final String TPS_VLR_MAX_RENEG_IGUAL_SOMA_CONTRATOS                              = "163";
    public static final String TPS_VLR_MAX_COMPRA_IGUAL_SOMA_CONTRATOS                             = "164";
    public static final String TPS_CLASSE_GERENCIADOR_AUTORIZACAO                                  = "165";
    public static final String TPS_PERMITE_ALTERAR_ADE_COM_BLOQUEIO                                = "166";
    public static final String TPS_EXIGE_NUMERO_CONTRATO_SALDO_DEVEDOR                             = "167";
    public static final String TPS_DESTINATARIOS_EMAILS_CONTROLE_COMPRA                            = "168";
    public static final String TPS_EXIBE_RANKING_CONFIRMACAO_RESERVA                               = "169";
    public static final String TPS_PERCENTUAL_MINIMO_PRD_PAGAS_RENEG                               = "170";
    //public static final String TPS_VALOR_MAX_TAXA_JUROS_4                                        = "171";
    //public static final String TPS_VALOR_MAX_TAXA_JUROS_5                                        = "172";
    public static final String TPS_PERCENTUAL_MINIMO_VIGENCIA_RENEG                                = "173";
    public static final String TPS_USA_CAPITAL_DEVIDO_BASE_LIMITE_SALDO                            = "174";
    public static final String TPS_PERMITE_SALDO_FORA_FAIXA_LIMITE                                 = "175";
    public static final String TPS_VLR_INTERVENIENCIA                                              = "176";
    public static final String TPS_MINIMO_PRD_PAGAS_COMPRA                                         = "177";
    public static final String TPS_PERCENTUAL_MINIMO_PRD_PAGAS_COMPRA                              = "178";
    public static final String TPS_MINIMO_VIGENCIA_COMPRA                                          = "179";
    public static final String TPS_PERCENTUAL_MINIMO_VIGENCIA_COMPRA                               = "180";
    public static final String TPS_MINIMO_VIGENCIA_RENEG                                           = "181";
    public static final String TPS_EXIGE_NRO_CONTRATO_INF_SALDO_SOLIC                              = "182";
    public static final String TPS_EXIGE_CODIGO_AUTORIZACAO_CONF_SOLIC                             = "183";
    public static final String TPS_LIMITA_SALDO_DEVEDOR_ANTERIOR_RENEG                             = "184";
    public static final String TPS_LIMITA_SALDO_DEVEDOR_ANTERIOR_COMPRA                            = "185";
    public static final String TPS_MARGEM_ERRO_LIMITE_SALDO_ANT_RENEG                              = "186";
    public static final String TPS_MARGEM_ERRO_LIMITE_SALDO_ANT_COMPRA                             = "187";
    public static final String TPS_PRAZO_DIAS_CANCELAMENTO_RENEGOCIACAO                            = "188";
    public static final String TPS_EXIGE_SENHA_SER_CANCEL_RENEGOCIACAO                             = "189";
    public static final String TPS_PERCENTUAL_MARGEM_PERMITE_SIMULADOR                             = "190";
    public static final String TPS_MASCARA_IDENTIFICADOR_ADE                                       = "191";
    public static final String TPS_LIMITA_CAPITAL_DEVIDO_A_BASE_CALCULO                            = "192";
    public static final String TPS_DIAS_APR_SALDO_DV_CONTROLE_COMPRA                               = "193";
    public static final String TPS_ACAO_PARA_NAO_APR_SALDO_DV                                      = "194";
    public static final String TPS_REJEICAO_PGT_SDV_BLOQUEIA_AMBAS_CSAS                            = "195";
    public static final String TPS_EMAIL_INF_APROVACAO_SALDO_DEVEDOR                               = "196";
    public static final String TPS_LIMITA_CAPITAL_DEVIDO_ALONGAMENTO                               = "197";
    public static final String TPS_EXIBE_INF_BANCARIA_SERVIDOR                                     = "198";
    public static final String TPS_NUM_ADE_HIST_LIQUIDACOES_ANTECIPADAS                            = "199";
    public static final String TPS_INCLUI_ALTERANDO_SOMENTE_MESMO_PRAZO                            = "200";
    public static final String TPS_IDADE_MIN_MAX_SER_SOLIC_SIMULACAO                               = "201";
    public static final String TPS_SERVIDOR_LIQUIDA_CONTRATO                                       = "202";
    public static final String TPS_DEFERE_AUT_CONTRATO_INCLUIDO_SER                                = "203";
    public static final String TPS_PERMITE_ALTERAR_VLR_LIBERADO                                    = "204";
    public static final String TPS_NUM_ADE_HIST_SUSPENSOES                                         = "205";
    public static final String TPS_USA_SENHA_CONSULTA_RESERVA_MARGEM                               = "206";
    public static final String TPS_RETEM_MARGEM_SVC_PERCENTUAL                                     = "207";
    public static final String TPS_QTE_DIAS_BLOQUEAR_RESERVA_APOS_LIQ                              = "208";
    public static final String TPS_DIAS_DEFER_AUT_CONSIG_NAO_DEFERIDAS                             = "209";
    public static final String TPS_ANEXO_INCLUSAO_CONTRATOS_OBRIGATORIO_CSE_ORG_SUP                = "210";
    public static final String TPS_PERMITE_SOLICITAR_SALDO_BENEFICIARIO                            = "211";
    public static final String TPS_PERCENTUAL_MINIMO_DESCONTO_VLR_SALDO                            = "212";
    public static final String TPS_QTD_PROPOSTAS_PAGAMENTO_PARCEL_SALDO                            = "213";
    public static final String TPS_DIAS_VALIDADE_PROPOSTAS_PGTO_SALDO                              = "214";
    public static final String TPS_DIAS_INF_PROPOSTAS_PGTO_SALDO_TERCEI                            = "215";
    public static final String TPS_PERCENTUAL_MINIMO_MANTER_VALOR_RENEG                            = "216";
    public static final String TPS_DIAS_SOLICITAR_PROPOSTAS_PGTO_SALDO                             = "217";
    public static final String TPS_BASE_CALC_RETENCAO_SVC_PERCENTUAL                               = "218";
    public static final String TPS_PRZ_MAX_RENEG_IGUAL_MAIOR_CONTRATOS                             = "219";
    public static final String TPS_PRZ_MAX_COMPRA_IGUAL_MAIOR_CONTRATOS                            = "220";
    public static final String TPS_QTD_DIAS_BLOQ_CSA_APOS_INF_SALDO_SER                            = "221";
    public static final String TPS_MAX_PRAZO_RENEGOCIACAO_PORTABILIDADE                            = "222";
    public static final String TPS_PRAZO_ATEND_SALDO_DEVEDOR_EXCLUSAO                              = "223";
    public static final String TPS_SERVICO_TRATAMENTO_ESPECIAL_MARGEM                              = "224";
    public static final String TPS_IDENTIFICADOR_ADE_OBRIGATORIO                                   = "225";
    public static final String TPS_EXIBE_BOLETO                                                    = "226";
    public static final String TPS_PERMITE_LIQUIDAR_ADE_SUSPENSA                                   = "227";
    public static final String TPS_PERMITE_CONTRATO_VALOR_NEGATIVO                                 = "228";
    public static final String TPS_LIMITA_SALDO_DEVEDOR_CAD_CSE_ORG_SUP                            = "229";
    public static final String TPS_PERCENTUAL_BASE_CALC_DESCONTO_EM_FILA                           = "230";
    public static final String TPS_BASE_CALC_DESCONTO_EM_FILA                                      = "231";
    public static final String TPS_VALOR_MINIMO_PERMITIDO_TAXA_JUROS                               = "232";
    public static final String TPS_EXIBE_MSG_RESERVA_MESMA_VERBA_CSA_COR                           = "233";
    public static final String TPS_VALIDA_TAXA_ALTERACAO_ADE_ANDAMENTO                             = "234";
    public static final String TPS_MSG_EXIBIR_INCLUSAO_ALTERACAO_ADE_CSA                           = "235";
    public static final String TPS_NUM_CONTRATOS_POR_NATUREZA_SERVICO                              = "236";
    public static final String TPS_PERCENTUAL_MARGEM_FOLHA_LIMITE_CSA                              = "237";
    public static final String TPS_EXIBE_TABELA_PRICE                                              = "238";
    public static final String TPS_OP_INSERE_ALTERA_USA_MAIOR_PRAZO                                = "239";
    public static final String TPS_MAX_PRAZO_RELATIVO_AOS_RESTANTES                                = "240";
//  public static final String TPS_SUSPENDE_ADE_CSA_BLOQ_CONTROLE_COMPRA                           = "241";
    public static final String TPS_VISUALIZA_VALOR_LIBERADO_CALC                                   = "242";
    public static final String TPS_ALTERA_ADE_COM_MARGEM_NEGATIVA                                  = "243";
    public static final String TPS_EXIBE_MARGEM_CRITICA_LOTE                                       = "244";
    public static final String TPS_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA                            = "245";
    public static final String TPS_EMAIL_NOTIFICACAO_NOVO_LEILAO                                   = "246";
    public static final String TPS_EXIBE_CIDADE_CONFIRMACAO_SOLICITACAO                            = "247";
    public static final String TPS_VALIDAR_REGRA_RENEGOCIACAO_CONTRATO_FRUTO_PORTABILIDADE         = "248";
    public static final String TPS_VALOR_MAX_ENVIO_PARA_DESCONTO_FOLHA                             = "249";
    public static final String TPS_CONCLUI_ADE_NAO_PAGA_NO_EXERCICIO                               = "250";
    public static final String TPS_EXIGE_ASSINATURA_DIGITAL_SOLICITACOES                           = "251";
    public static final String TPS_MAX_PRAZO_RENEGOCIACAO_NO_PERIODO                               = "252";
    public static final String TPS_MIN_PRAZO_INI_DESCONTO_RENEGOCIACAO                             = "253";
    public static final String TPS_PERMITE_INCLUIR_ADE_RSE_BLOQUEADO                               = "254";
    public static final String TPS_PERMITE_ALTERAR_ADE_RSE_BLOQUEADO                               = "255";
    public static final String TPS_TEM_SUBSIDIO                                                    = "256";
    public static final String TPS_ORDEM_PRIORIDADE_SUBSIDIO                                       = "257";
    public static final String TPS_PAI_MAE_TITULARES_DIVORCIADOS_SUBSIDIO                          = "258";
    public static final String TPS_TIPO_CALCULO_SUBSIDIO                                           = "259";
    public static final String TPS_AGREGADO_PODE_TER_SUBSIDIO                                      = "260";
    public static final String TPS_QTDE_SUBSIDIO_POR_NATUREZA                                      = "261";
    public static final String TPS_IDADE_MAX_DEPENDENTE_DIREITO_SUBSIDIO                           = "262";
    public static final String TPS_IDADE_MAX_DEPENDENTE_EST_SUBSIDIO                               = "263";
    public static final String TPS_EXIGENCIA_CONFIRMACAO_LEITURA_SERVIDOR                          = "264";
    public static final String TPS_PRENDE_MARGEM_LIQ_ADE_PRZ_INDET_ATE_CARGA_MARGEM                = "265";
    public static final String TPS_DATA_EXPIRACAO_CONVENIO                                         = "266";
    public static final String TPS_NUMERO_CONTRATO_CONVENIO                                        = "267";
    public static final String TPS_TEMPO_LIMITE_PARA_ADE_EM_DUPLICIDADE                            = "268";
    public static final String TPS_PERMITIR_DUPLICIDADE_WEB_MOTIVADA_USUARIO                       = "269";
    public static final String TPS_DATA_LIMITE_VIGENCIA_PAI_MAE_TITULARES_DIVORCIADOS_SUBSIDIO     = "270";
    public static final String TPS_MASCARA_NUMERO_CONTRATO_BENEFICIO                               = "271";
    public static final String TPS_MENSAGEM_PARA_SERVIDOR_APOS_SOLICITACAO_DEFERIDA                = "272";
    public static final String TPS_EMAIL_SOLICITACAO_INCLUIDA_OU_CANCELADA_PELO_SERVIDOR           = "273";
    public static final String TPS_EMAIL_NOTIF_ALTER_CODVERBA_CONVENIO_CSA                         = "274";
    public static final String TPS_EXIGE_TERMO_DE_ADESAO_RESERVAR_MARGEM_ANTES_DE_VALORES_OPERACAO = "275";
    public static final String TPS_EXIBE_CSA_LISTAGEM_SOLICITACAO                                  = "276";
    public static final String TPS_PULA_INFORMACAO_VALOR_PRAZO_FLUXO_RESERVA                       = "277";
    public static final String TPS_ATUALIZA_ADE_VLR_ALTERACAO_MARGEM                               = "278";
    public static final String TPS_EXIBIR_COMO_VALOR_FORA_DA_MARGEM                                = "279";
    public static final String TPS_SER_SENHA_OPCIONAL_RESERVA_MARGEM_TEM_ADE                       = "280";
    public static final String TPS_BLOQUEIA_INCLUSAO_ADE_MESMO_PERIODO_NSE_RSE                     = "281";
    public static final String TPS_SERVIDOR_ALTERA_CONTRATO                                        = "282";
    public static final String TPS_PERMITE_ALTERAR_ADE_RSE_EXCLUIDO                                = "283";
    public static final String TPS_QTD_ANEXOS_MIN_ADE_FEITA_POR_CSA_COR                            = "284";
    public static final String TPS_SERVIDOR_DEVE_SER_KYC_COMPLIANT                                 = "285";
    public static final String TPS_SER_SENHA_OBRIGATORIA_SER                                       = "286";
    public static final String TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO                             = "287";
    public static final String TPS_MSG_EXIBIR_SOLICITACAO_SERVIDOR_OFERTA_OUTRO_SVC                = "288";
    public static final String TPS_PERMITE_ALTERAR_COM_LIMITACAO                                   = "289";
    public static final String TPS_CONCLUI_ADE_NA_DATA_FIM_SERVIDOR_EXCLUIDO                       = "290";
    public static final String TPS_BLOQUEIA_INCLUSAO_LOTE_RSE_TIPO                                 = "291";
    public static final String TPS_DESCONSIDERAR_VALOR_APROVISIONADO_PERIODOS_PASSADOS             = "292";
    public static final String TPS_TEMPO_LIMITE_PARA_CANCELAMENTO_ULTIMA_ADE_SERVIDOR              = "293";
    public static final String TPS_CONSIDERA_DATA_INF_SALDO_LIQUIDACAO_ADE_CONTROLE_COMPRA         = "294";
    public static final String TPS_ANEXO_CONFIRMACAO_RESERVA_OBRIGATORIO                           = "295";
    public static final String TPS_RELEVANCIA_CSA_RANKING                                          = "296";
    public static final String TPS_EXIGE_SENHA_SER_SUSPENDER_CONSIGNACAO                           = "297";
    public static final String TPS_EXIGE_SENHA_SER_CANCELAR_CONSIGNACAO                            = "298";
    public static final String TPS_EXIGE_SENHA_SER_REATIVAR_CONSIGNACAO                            = "299";
    public static final String TPS_EXIGE_SENHA_SER_LIQUIDAR_CONSIGNACAO                            = "300";
    public static final String TPS_CONDICIONA_OPERACAO_PORTABILIDADE                               = "301";
//  Parametro criado de forma desnecessaria na DESENV-16211
//  public static final String TPS_CONDICIONA_OPERACAO_RENEGOCIACAO                                = "302";
    public static final String TPS_VLR_PERC_MAXIMO_PARCELA_PORTABILIDADE                           = "303";
    public static final String TPS_VLR_PERC_MAXIMO_PARCELA_RENEGOCIACAO                            = "304";
    public static final String TPS_CONFIGURAR_ID_PROPOSTA_CONVENIO                                 = "305";
    public static final String TPS_CONFIGURAR_DIA_VENCIMENTO_CONTRATO                              = "306";
    public static final String TPS_CONFIGURAR_PERIODO_COMPETENCIA_DEBITO                           = "307";
    public static final String TPS_CONFIGURAR_NUMERO_CONVENIO                                      = "308";
    public static final String TPS_CONFIGURAR_CODIGO_ADESAO                                        = "309";
    public static final String TPS_CONFIGURAR_IDADE_MAXIMA_CONTRATACAO_SEGURO                      = "310";
    public static final String TPS_FORMA_NUMERACAO_PARCELAS                                        = "311";
    public static final String TPS_PERMITE_DESCONTO_VIA_BOLETO                                     = "312";
    public static final String TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO                         = "313";
    public static final String TPS_DIAS_VIGENCIA_CET                                               = "314";
    public static final String TPS_OCULTAR_MENU_SERVIDOR                                           = "315";
    public static final String TPS_INF_BANCARIA_OBRIGATORIA_CSA                                    = "316";
    public static final String TPS_EXIGE_SENHA_SERVIDOR_LOTE                                       = "317";
    public static final String TPS_IMPEDIR_LIQUIDACAO_CONSIGNACAO                                  = "318";
    public static final String TPS_EXIBE_TEXTO_EXPLICATIVO_VALOR_PRESTACAO                         = "319";
    public static final String TPS_VALOR_SVC_FIXO_POSTO                                            = "320";
    public static final String TPS_PERMITE_INCLUSAO_ADE_SER_BLOQ_SEM_EXIGENCIA_SENHA               = "321";
    public static final String TPS_INSERIR_VIRA_ALTERAR_POR_ADE_INDENTIFICADOR_IGUAL               = "322";
    public static final String TPS_EXIGE_RECONHECIMENTO_FACIAL_SERVIDOR_SOLICITACAO                = "323";
    public static final String TPS_CONSIDERA_MARGEM_REST_RESERVA_COMPULSORIO                       = "324";
    public static final String TPS_ANEXO_INCLUSAO_CONTRATOS_OBRIGATORIO_CSA_COR                    = "325";
    public static final String TPS_ANEXO_INCLUSAO_CONTRATOS_OBRIGATORIO_SER                        = "326";
    public static final String TPS_QUANTIDADE_MINIMA_ANEXO_INCLUSAO_CONTRATOS                      = "327";
    public static final String TPS_PERMITE_CANCELAR_RENEGOCIACAO_MESMO_A_MARGEM_FICANDO_NEGATIVA   = "328";
    public static final String TPS_EXIGE_CADASTRO_TAXA_JUROS_PARA_CET                              = "329";
    public static final String TPS_PRAZO_LIMITADO_DATA_ADIMISSAO_REGISTRO_SERVIDOR                 = "330";
    public static final String TPS_QUANTIDADE_PERIODOS_SEM_LANCAMENTO_PERMITE_PORTABILIDADE        = "331";
    public static final String TPS_SOMENTE_CONTRATOS_SEM_LANCAMENTO_PERMITE_PORTABILIDADE          = "332";
    public static final String TPS_PARTICIPA_DA_CONTAGEM_DE_INCLUSAO_POR_DIA_CSA                   = "333";

    /* Parâmetros de Plano de Desconto do módulo SDP*/
    public static final String TPP_INDICE_PLANO                = "1";
    public static final String TPP_PRAZO_MAX_PLANO             = "2";
    public static final String TPP_PRAZO_FIXO_PLANO            = "3";
    public static final String TPP_VLR_FIXO_PLANO              = "4";
    public static final String TPP_VLR_PLANO                   = "5";
    public static final String TPP_TIPO_RATEIO_PLANO           = "6";
    public static final String TPP_DESCONTO_POR_POSTO          = "7";
    public static final String TPP_DESCONTO_AUTOMATICO         = "8";
    public static final String TPP_EXCLUSAO_AUTOMATICA         = "9";

    /* Constantes de Parâmetro de Plano de Desconto do módulo SDP*/
    public static final String TPP_SIM                         = "S";
    public static final String TPP_NAO                         = "N";
    public static final String PLANO_SEM_RATEIO                = "0";
    public static final String PLANO_RATEIO_POR_PERMISSIONARIO = "1";
    public static final String PLANO_RATEIO_POR_UNIDADE        = "2";
    public static final String PLANO_PRAZO_FIXO_SIM            = "1";
    public static final String PLANO_PRAZO_FIXO_NAO            = "0";
    public static final String PLANO_VALOR_ALTERAVEL           = "1";
    public static final String PLANO_VALOR_PRE_DETERMINADO     = "0";
    public static final String PLANO_DESCONTO_POR_POSTO_SIM    = "1";
    public static final String PLANO_DESCONTO_POR_POSTO_NAO    = "0";

    /* Índice usado para inclusão de despesa de taxa de uso proporcional : Fixo, pois só pode ter uma taxa de uso */
    public static final String INDICE_PADRAO_TAXA_USO_PROPORCIONAL = "99";

    /* Valores para os parâmetros de serviços que são booleanos (S/N) */
    public static final String PSE_BOOLEANO_SIM = "1";
    public static final String PSE_BOOLEANO_NAO = "0";

    /* Valores para os parâmetros de serviços por consignatária que são booleanos (S/N) */
    public static final String PSC_BOOLEANO_SIM = "S";
    public static final String PSC_BOOLEANO_NAO = "N";

    /* Valores para o parametro TPS_LIMITA_CAPITAL_DEVIDO_A_BASE_CALCULO (0/1/2) */
    public static final String PSE_VLR_SEM_LIMITACAO = "0";
    public static final String PSE_VLR_LIMITA_BASE_CALCULO_VLR_LIBERADO = "1";
    public static final String PSE_VLR_LIMITA_BASE_CALCULO_CAPITAL_DEVIDO = "2";

    /* Constante que indica o valor do parâmetro de serviço em que a senha do
       servidor não é obrigatória na reserva de margem */
    public static final String PSE_SER_SENHA_OPCIONAL = "0";

    /* Valores para o parâmetro TPS_EXIBE_CONTRATO_SERVIDOR */
    public static final String PSE_NAO_EXIBIR_CONTRATOS_SERVIDOR = "0";
    public static final String PSE_EXIBIR_TODOS_CONTRATOS_SERVIDOR = "1";
    public static final String PSE_EXIBIR_SOMENTE_CONTRATOS_ATIVOS_SERVIDOR = "2";

    /* Valores para o parâmetro TPS_PERMITE_DATA_RETROATIVA_IMP_LOTE */
    public static final String NAO_PERMITE_DATA_RETROATIVA = "0";
    public static final String PERMITE_DATA_RETROATIVA = "1";

    /* Valores para o parâmetro TPS_CALCULA_SALDO_SOMENTE_VINCENDO */
    public static final String CALCULA_SALDO_TUDO_EM_ABERTO = "0";
    public static final String CALCULA_SALDO_SOMENTE_VINCENDO = "1";
    public static final String CALCULA_SALDO_VINCENDO_SOMANDO_VENCIDAS = "2";

    /* Valores para o parâmetro TPS_CONTROLA_SALDO */
    public static final String NAO_POSSUI_CONTROLE_SALDO_DEVEDOR = "0";
    public static final String POSSUI_CONTROLE_SALDO_DEVEDOR = "1";

    /* Valores para o parâmetro TPS_CONTROLA_VLR_MAX_DESCONTO */
    public static final String NAO_CONTROLA_VLR_MAX_DESCONTO = "0";
    public static final String CONTROLA_VLR_MAX_DESCONTO_PELO_CARGO = "1";
    public static final String CONTROLA_VLR_MAX_DESCONTO_PELA_PARCELA = "2";

    /* Valores para o parâmetro TPS_POSSUI_CORRECAO_SALDO_DEVEDOR */
    public static final String NAO_POSSUI_CORRECAO_SALDO_DEVEDOR = "0";
    public static final String CORRECAO_SALDO_DEVEDOR_PROPRIO_SERVICO = "1";
    public static final String CORRECAO_SALDO_DEVEDOR_EM_OUTRO_SERVICO = "2";

    /* Valores para o parâmetro TPS_CORRECAO_SOBRE_TOTAL_SALDO_DV */
    public static final String CORRECAO_SOBRE_SALDO_PARCELAS      = "0";
    public static final String CORRECAO_SOBRE_TOTAL_SALDO_DEVEDOR = "1";

    /* Valores para o parâmetro TPS_CORRECAO_ENVIADA_APOS_PRINCIPAL */
    public static final String CORRECAO_ENVIADA_APOS_PRINCIPAL = "1";
    public static final String CORRECAO_ENVIADA_JUNTO_PRINCIPAL = "0";

    /* Valores para o parâmetro TPS_POSSUI_CONTROLE_TETO_DESCONTO */
    public static final String NAO_CONTROLA_TETO_DESCONTO        = "0";
    public static final String CONTROLA_TETO_DESCONTO_PELO_CARGO = "1";

    /* Valores para o parâmetro TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR */
    public static final String NAO_POSSUI_CADASTRO_SALDO_DEVEDOR = "0";
    public static final String USUARIO_CADASTRA_SALDO_DEVEDOR    = "1";
    public static final String SISTEMA_CALCULA_SALDO_DEVEDOR     = "2";
    public static final String CADASTRA_E_CALCULA_SALDO_DEVEDOR  = "3";

    /* Valores para o parâmetro TPS_PERMITE_DESCONTO_VIA_BOLETO */
    public static final String NAO_PERMITE_PAGAMENTO_VIA_BOLETO = "0";
    public static final String PAGAMENTO_VIA_BOLETO_OPICIONAL   = "1";
    public static final String PAGAMENTO_VIA_BOLETO_OBRIGATORIO = "2";

    /* Valores para o parâmetro TPS_ALTERA_ADE_COM_MARGEM_NEGATIVA */
    public static final String NAO_ALTERA_VLR_E_PRAZO_MARGEM_NEGATIVA                = "0";
    public static final String PERMITE_REDUZIR_MANTER_VLR_PRZ_CONTRATO_MRG_NEGATIVA  = "1";
    public static final String PERMITE_REDUZIR_VLR_AUMENTAR_PRZ_CONTRATO_MRG_NETAVIA = "2";

    /* Valores para o parâmetro TPS_EXIGE_SENHA_ALTERACAO_CONTRATOS */
    public static final String NAO_EXIGE_SENHA_ALTERACAO_CONTRATOS        = "0";
    public static final String EXIGE_SENHA_QUALQUER_ALTERACAO_CONTRATOS   = "1";
    public static final String EXIGE_SENHA_ALTERACAO_CONTRATOS_PARA_MAIOR = "2";
    public static final String EXIGE_SENHA_ALTERACAO_CAPITAL_DEVIDO_MAIOR = "3";

    /* Valores para o parâmetro TPS_DESTINATARIOS_EMAILS_CONTROLE_COMPRA */
    public static final String RECEBE_EMAIL_APENAS_CONSIGNATARIA  = "0";
    public static final String RECEBE_EMAIL_APENAS_CORRESPONDENTE = "1";
    public static final String RECEBE_EMAIL_CSA_E_COR             = "2";

    /* Valores para o parâmetro TPS_POSSUI_CONTROLE_TETO_DESCONTO */
    public static final String VALIDA_PRAZO_MAX_RELATIVO_AOS_RESTANTES = "1";
    public static final String VALIDA_PRAZO_MAX_TOTAL                  = "0";

    /* Valores para o parâmetro TPS_PERMITE_ALTERAR_ADE_COM_BLOQUEIO */
    public static final String PSE_NAO_PERMITE_ALTERAR_ADE_COM_BLOQUEIO = "0";
    public static final String PSE_PERMITE_ALTERAR_ADE_COM_BLOQUEIO = "1";
    public static final String PSE_PERMITE_ALTERAR_ADE_COM_BLOQUEIO_APENAS_REDUCAO = "2";

    /* Valores para o parâmetro TPS_EXIBE_CIDADE_CONFIRMACAO_SOLICITACAO */
    public static final String NAO_EXIBIR_CIDADE_CONFIRMACAO_SOLICITACAO = "0";
    public static final String EXIBIR_CIDADE_CONFIRMACAO_SOLICITACAO_OPCIONAL = "1";
    public static final String EXIBIR_CIDADE_CONFIRMACAO_SOLICITACAO_OBRIGATORIO = "2";
    public static final String EXIBIR_CIDADE_CONFIRMACAO_SOLICITACAO_OBRIGATORIO_LEILAO = "3";

    /* Valores para o parâmetro TPS_PERMITE_ALTERAR_ADE_RSE_BLOQUEADO */
    public static final String PSE_NAO_PERMITE_ALTERAR_ADE_RSE_BLOQUEADO = "0";
    public static final String PSE_PERMITE_ALTERAR_ADE_RSE_BLOQUEADO = "1";
    public static final String PSE_PERMITE_ALTERAR_ADE_RSE_BLOQUEADO_APENAS_REDUCAO = "2";

    /* Valores para o parâmetro TPS_TIPO_CALCULO_SUBSIDIO */
    public static final String PSE_TIPO_CALCULO_SUBSIDIO_VALOR = "V";
    public static final String PSE_TIPO_CALCULO_SUBSIDIO_PERC_SALARIO = "S";
    public static final String PSE_TIPO_CALCULO_SUBSIDIO_PERC_BENEFICIO = "B";
    public static final String PSE_TIPO_CALCULO_SUBSIDIO_PERC_DESCONTO_SALARIO = "D";

    /* Valores para o parâmetro TPS_ATUALIZA_ADE_VLR_ALTERACAO_MARGEM */
    public static final String PSE_ATUALIZA_ADE_VLR_ALTERACAO_MARGEM_NUNCA    = "0";
    public static final String PSE_ATUALIZA_ADE_VLR_ALTERACAO_MARGEM_POSITIVA = "1";
    public static final String PSE_ATUALIZA_ADE_VLR_ALTERACAO_MARGEM_NEGATIVA = "2";
    public static final String PSE_ATUALIZA_ADE_VLR_ALTERACAO_MARGEM_SEMPRE   = "3";

    /* Valores para o parâmetro TPS_PERMITE_ALTERAR_ADE_RSE_EXCLUIDO */
    public static final String PSE_NAO_PERMITE_ALTERAR_ADE_RSE_EXCLUIDO = "0";
    public static final String PSE_PERMITE_ALTERAR_ADE_RSE_EXCLUIDO_APENAS_REDUCAO = "1";

    /* Valores para o parâmetro TPS_PERMITE_AUM_VLR_PRZ_CONSIGNACAO */
    public static final String NAO_PERMITE_AUMENTAR_VLR_PRZ_CONTRATO = "0";
    public static final String PERMITE_AUMENTAR_VLR_PRZ_CONTRATO     = "1";
    public static final String PERMITE_AUMENTAR_APENAS_PRZ_CONTRATO  = "2";
    public static final String PERMITE_AUMENTAR_APENAS_VLR_CONTRATO  = "3";

    /* Tipos de parâmetro de consignatária. */
    //public static final String TPA_EXIBE_MARGEM                                            = "1";
    public static final String TPA_IMPORTA_SEM_PROCESSAMENTO                                 = "2";
    public static final String TPA_SEPARA_REPASSE_POR_VERBA                                  = "3";
    public static final String TPA_VERIFICAR_VALIDACOES_LIMITES                              = "4";
    public static final String TPA_LIMITE_CONSULTAS_MARGEM                                   = "5";
    public static final String TPA_PODE_REPETIR_COD_VERBA                                    = "6";
    public static final String TPA_SENHA_SER_ACESSAR_CONT_CSAS                               = "7";
    public static final String TPA_PERMITE_COPIA_CONVENIO_CORRESPONDENTE                     = "8";
    public static final String TPA_SENHA_SER_CONSULTAR_MARGEM                                = "9";
    public static final String TPA_EXIGE_CERTIFICADO_DIGITAL                                 = "10";
    public static final String TPA_PERMITE_USU_OPTAR_CERTIF_DIGITAL                          = "11";
    public static final String TPA_UTILIZA_PRIMEIRO_CNV_DISPONIVEL_LOTE                      = "12";
    public static final String TPA_UTILIZA_SERVIDOR_COM_MAIOR_MARGEM_LOTE                    = "13";
    public static final String TPA_UTILIZA_APENAS_CPF_SERVIDOR_LOTE                          = "14";
    public static final String TPA_RECEBE_EMAIL_ALERTA_COMUNICACAO                           = "15";
    public static final String TPA_PRE_CONFIRMA_RESERVA_COMPRA_PGT_SALDO                     = "16";
    public static final String TPA_PODE_ACESSAR_HOST_A_HOST_QQ_ENDERECO                      = "17";
    public static final String TPA_REQUER_MATRICULA_E_CPF                                    = "18";
    public static final String TPA_PERMITE_INCLUSAO_COM_SER_DUPLICADO_LOTE                   = "19";
    public static final String TPA_PERMITE_PRAZO_MAIOR_99_LOTE                               = "20";
    public static final String TPA_TENTA_INCLUIR_RESERVA_TODOS_SER_LOTE                      = "21";
    public static final String TPA_ARQ_CONF_EXP_REL_INTEGRACAO_RETORNO                       = "22";
    public static final String TPA_CONTADOR_REL_INTEGRACAO_CUSTOMIZADO                       = "23";
    public static final String TPA_SEPARAR_RELATORIO_INTEGRACAO                              = "24";
    public static final String TPA_GERAR_LOTE_SINCRONIA_CONCILIACAO                          = "25";
    public static final String TPA_GERAR_REL_INTEGRACAO_CORRESPONDENTE                       = "26";
    public static final String TPA_EXIGE_SEGUNDA_SENHA                                       = "27";
    public static final String TPA_REQUER_MATRICULA_E_CPF_HOST_A_HOST                        = "28";
    public static final String TPA_INF_BANCARIA_OBRIGATORIA_HOST_A_HOST                      = "29";
    public static final String TPA_DATA_NASCIMENTO_OBRIGATORIA_HOST_A_HOST                   = "30";
    public static final String TPA_NAO_BLOQUEIA_POR_DATA_EXPIRACAO                           = "31";
    public static final String TPA_PERMITE_PAGAMENTO_PARCIAL                                 = "32";
    public static final String TPA_PERMITE_OCUPAR_MESMO_IMOVEL_SDP                           = "33";
    public static final String TPA_INF_MODALIDADE_OPERACAO_OBRIGATORIO                       = "34";
    public static final String TPA_INFORMAR_MATRICULA_NA_CSA_OBRIGATORIO                     = "35";
    public static final String TPA_QTDE_DIAS_ENVIO_EMAIL_ALERTA_PROX_CORTE                   = "36";
    public static final String TPA_PRIORIZAR_SVC_ORIGEM_RENEGOCIACAO                         = "37";
    public static final String TPA_PERMITE_SERVIDOR_ESCOLHER_COR_SIMULACAO                   = "38";
    public static final String TPA_RECEBE_EMAIL_RELATORIO_INTEGRACAO                         = "39";
    public static final String TPA_SENHA_SER_ACESSAR_CONT_CSAS_HOST_A_HOST                   = "40";
    public static final String TPA_SENHA_SER_CONSULTAR_MARGEM_HOST_A_HOST_CSA                = "41";
    public static final String TPA_PERMITE_INC_ADE_RSE_EXCLUIDO_HOST_A_HOST                  = "42";
    public static final String TPA_UTILIZA_PRIMEIRO_CNV_DISPONIVEL_NATUREZA                  = "43";
    public static final String TPA_SENHA_SER_RESERVAR_MARGEM_HOST_A_HOST_CSA                 = "44";
    public static final String TPA_PERMITE_OFERECER_PROPOSTA_LEILAO                          = "45";
    public static final String TPA_ADE_IDENTIFICADOR_UNICO_VIA_LOTE                          = "46";
    public static final String TPA_DIA_REPASSE                                               = "47";
    public static final String TPA_SENHA_SER_CONSULTAR_MARGEM_HOST_A_HOST_COR                = "48";
    public static final String TPA_SENHA_SER_RESERVAR_MARGEM_HOST_A_HOST_COR                 = "49";
    public static final String TPA_SENHA_SER_CANCELAR_RENEG_HOST_A_HOST_CSA                  = "50";
    public static final String TPA_SENHA_SER_CANCELAR_RENEG_HOST_A_HOST_COR                  = "51";
    public static final String TPA_DATA_REFERENCIA_CALCULO_SUBSIDIO                          = "52";
    public static final String TPA_ALTERA_RSE_PRAZO_SOAP                                     = "53";
    public static final String TPA_PRAZO_MINIMO_MIGRACAO_BENEFICIO                           = "54";
    public static final String TPA_VLR_MIN_ENVIO_BOLETO_FATURAMENTO_BENEFICIO                = "55";
    public static final String TPA_KYC_EMAIL_RECEBIMENTO_NOTIFICACOES                        = "56";
    public static final String TPA_PERMITE_INC_ADE_RSE_EXCLUIDO_WEB                          = "57";
    public static final String TPA_VALIDA_SENHA_SERVIDOR_SOAP                                = "58";
    public static final String TPA_UTILIZA_SERVIDOR_COM_MENOR_MARGEM_LOTE                    = "59";
    public static final String TPA_RAIO_METROS_BUSCA_END_CONSIGNATARIAS                      = "60";
    public static final String TPA_EXIBE_DADOS_BANCARIOS                                     = "61";
    public static final String TPA_BLOQ_CONSULTA_MARGEM_VINCULO_HOST_A_HOST                  = "62";
    public static final String TPA_QTD_OPERACOES_LIBERACAO_MARGEM_POR_USU_ENVIA_NOTIFICACAO  = "63";
    public static final String TPA_QTD_OPERACOES_LIBERACAO_MARGEM_POR_USU_EXIGE_CAPTCHA      = "64";
    public static final String TPA_QTD_OPERACOES_LIBERACAO_MARGEM_POR_USU_EXIGE_OUTRA_SENHA  = "65";
    public static final String TPA_QTD_OPERACOES_LIBERACAO_MARGEM_POR_USU_BLOQUEIA_USUARIO   = "66";
    public static final String TPA_QTD_OPERACOES_LIBERACAO_MARGEM_POR_CSA_ENVIA_NOTIFICACAO  = "67";
    public static final String TPA_QTD_OPERACOES_LIBERACAO_MARGEM_POR_CSA_EXIGE_CAPTCHA      = "68";
    public static final String TPA_QTD_OPERACOES_LIBERACAO_MARGEM_POR_CSA_EXIGE_OUTRA_SENHA  = "69";
    public static final String TPA_QTD_OPERACOES_LIBERACAO_MARGEM_POR_CSA_BLOQUEIA_USUARIO   = "70";
    public static final String TPA_USUARIO_AUTENTICA_SSO                                     = "71";
    public static final String TPA_DIA_CORTE                                                 = "72";
    public static final String TPA_PERMITE_CSA_BLOQUEIO_AUTOMATICO_CONSULTAR_MARGEM_SOAP     = "73";
    public static final String TPA_PERMITE_ALTERAR_ADE_SEM_MOTIVO_OPERACAO_VIA_LOTE          = "74";
    public static final String TPA_DESBLOQUEIA_CSA_APROVACAO_POR_SUP                         = "75";
    public static final String TPA_QTD_CONTRATOS_POR_CSA                                     = "76";
    public static final String TPA_HABILITA_LIQUIDACAO_EM_DUAS_ETAPAS_CSA                    = "77";
    public static final String TPA_EXIGE_DUPLA_CONFIRMACAO_LIQUIDACAO_ADE                    = "78";
    public static final String TPA_INCL_SALDO_DEVEDOR_AUT_MODULO_RESCISAO                    = "79";
    public static final String TPA_VISUALIZA_INFO_CSA_SERVIDOR_RESERVA_MARGEM                = "80";
    public static final String TPA_INFO_VINC_BLOQ_PADRAO                                     = "81";
    public static final String TPA_PERMITE_SER_CONTATACDA_WHATSAPP_EMAIL_TELEFONE            = "82";
    public static final String TPA_CANCELA_SOLICITACAO_AO_PESQUISAR_SERVIDOR                 = "83";
    public static final String TPA_AUTORIZA_CONFIG_PERCENTUAL_VARIACAO_MARGEM                = "84";
    public static final String TPA_PERCENTUAL_VARIACAO_MENSAL_MARGEM_POR_MATRICULA           = "85";
    public static final String TPA_FUNCOES_PARA_DEFINICAO_TAXA_JUROS                         = "86";
    public static final String TPA_BLOQ_INC_ADE_MESMA_NSE_PRD_REJEITADA                      = "87";
    public static final String TPA_CATEGORIAS_PARA_EXIBIR_MENSAGEM_RESERVA_CONSULTA_MARGEM   = "88";
    public static final String TPA_MENSAGEM_EXIBIDA_CSA_COR                                  = "89";
    public static final String TPA_PESQUISA_MATRICULA_EXATA_VIA_SOAP                         = "90";
    public static final String TPA_AUTO_DESBLOQUEIO_USUARIO_CSA_COR                          = "91";
    public static final String TPA_PERCENTUAL_PARCELA_PAGA_ALERTA_OFERTA_REFINACIAMENTO      = "92";
    public static final String TPA_EMAIL_CSA_ALERTA_REFINANCIAMENTO                          = "93";
    public static final String TPA_EMAIL_CSA_NOTIFICACAO_BLOQUEIO_VARIACAO_MARGEM            = "94";
    public static final String TPA_EMAIL_CSA_NOTIFICACAO_NOVO_VINCULO                        = "95";
    public static final String TPA_PRAZO_CANCELAMENTO_RENEGOCIACAO_DIAS_UTEIS                = "96";
    public static final String TPA_RETORNA_VLR_FOLHA_E_VLR_USADO_CONSULTA_MARGEM_SOAP_WEB    = "97";
    public static final String TPA_RETORNA_ADE_NUMERO_ARQ_CRITICA_INCLUSAO                   = "98";
    public static final String TPA_ALTERA_DATA_ENCERRAMENTO_RENEGOCIACAO_PADRAO              = "99";
    public static final String TPA_BLOQUEIA_SER_COM_VARIACAO_MARGEM_MAIOR_QUE_LIMITE         = "100";
    public static final String TPA_CSA_PODE_VENDER_CONTRATO_CARTAO                           = "101";
    public static final String TPA_CSA_PODE_COMPRAR_CONTRATO_CARTAO                          = "102";
    public static final String TPA_EMAIL_CSA_NOTIFICACAO_ALTERACAO_REGRAS_CONVENIO           = "103";
    public static final String TPA_BLOQ_CONSULTA_MARGEM_SERVIDOR_COM_BLOQUEIO                = "104";
    public static final String TPA_PERMITE_VALIDACAO_TOTP                                    = "105";

    /* Valores para os parâmetros do tipo "SN". */
    public static final String TPA_SIM = "S";
    public static final String TPA_NAO = "N";

    /* Valores possíveis para o parâmetro de CSA TPA_IMPORTA_SEM_PROCESSAMENTO
     * que indica o comportamento da CSA em relação a importação de contratos sem processamento */
    public static final String IMP_SVC_DETERMINA     = "0";
    public static final String IMP_FORCA_IMPORTACAO  = "1";
    public static final String IMP_IMPEDE_IMPORTACAO = "2";

    /* Valores possíveis para o parâmetro de CSA TPA_SEPARAR_RELATORIO_INTEGRACAO
     * que indica se separar arquivo de integração */
    public static final String SEPARA_REL_INTEGRACAO_NAO                   = "N";
    public static final String SEPARA_REL_INTEGRACAO_POR_VERBA             = "V";
    public static final String SEPARA_REL_INTEGRACAO_POR_ORGAO             = "O";
    public static final String SEPARA_REL_INTEGRACAO_POR_ESTABELECIMENTO   = "E";

    /* Valores possíveis para contato à CSA */
    public static final String TPA_NAO_PERMITE_CONTATO             = "0";
    public static final String TPA_CONTATO_EMAIL                   = "1";
    public static final String TPA_CONTATO_TELEFONE                = "2";
    public static final String TPA_CONTATO_WHATSAPP                = "3";
    public static final String TPA_CONTATO_EMAIL_TELEFONE_WHATSAPP = "4";
    public static final String TPA_CONTATO_EMAIL_TELEFONE          = "5";
    public static final String TPA_CONTATO_EMAIL_WHATSAPP          = "6";
    public static final String TPA_CONTATO_TELEFONE_WHATSAPP       = "7";

    public static final List<String> TPA_CONTATOS_WHATSAPP = Arrays.asList(
            TPA_CONTATO_WHATSAPP,
            TPA_CONTATO_EMAIL_TELEFONE_WHATSAPP,
            TPA_CONTATO_EMAIL_WHATSAPP,
            TPA_CONTATO_TELEFONE_WHATSAPP
    );

    public static final List<String> TPA_CONTATOS_EMAIL = Arrays.asList(
            TPA_CONTATO_EMAIL,
            TPA_CONTATO_EMAIL_TELEFONE_WHATSAPP,
            TPA_CONTATO_EMAIL_TELEFONE,
            TPA_CONTATO_EMAIL_WHATSAPP
    );

    public static final List<String> TPA_CONTATOS_TELEFONE = Arrays.asList(
            TPA_CONTATO_TELEFONE,
            TPA_CONTATO_EMAIL_TELEFONE_WHATSAPP,
            TPA_CONTATO_EMAIL_TELEFONE,
            TPA_CONTATO_TELEFONE_WHATSAPP
    );

    /* Naturezas de Serviço */
    public static final String NSE_EMPRESTIMO         = "1";
    public static final String NSE_MENSALIDADE        = "2";
    public static final String NSE_SEGURO             = "3";
    public static final String NSE_PLANO_DE_SAUDE     = "4";
    public static final String NSE_PECULIO            = "5";
    public static final String NSE_PREVIDENCIA        = "6";
    public static final String NSE_CARTAO             = "7";
    public static final String NSE_COMPULSORIO        = "8";
    public static final String NSE_PLANO_ODONTOLOGICO = "9";
    public static final String NSE_BENEFICIO_SAUDE    = "10";
    public static final String NSE_FINANCIAMENTO      = "11";
    public static final String NSE_AUXILIO_FINANCEIRO = "12";
    public static final String NSE_OUTROS             = "99";
    public static final String NSE_SALARYPAY          = "13";

    public static final Short INCIDE_MARGEM_QQ    = -1;
    public static final Short INCIDE_MARGEM_NAO   =  0;
    public static final Short INCIDE_MARGEM_SIM   =  1;
    public static final Short INCIDE_MARGEM_SIM_2 =  2;
    public static final Short INCIDE_MARGEM_SIM_3 =  3;

    public static final Short INTEGRA_FOLHA_NAO = 0;
    public static final Short INTEGRA_FOLHA_SIM = 1;
    public static final Short INTEGRA_FOLHA_SOMENTE_EXCLUSAO = 2;

    /* Tipo de valor das autorizações */
    public static final String TIPO_VLR_FIXO         = "F";
    public static final String TIPO_VLR_PERCENTUAL   = "P";
    public static final String TIPO_VLR_TOTAL_MARGEM = "T";
    public static final String TIPO_VLR_KILOGRAMAS   = "K";

    /* Tipo de taxa das autorizações */
    public static final String TIPO_TAXA_COEFICIENTE = "0";
    public static final String TIPO_TAXA_JUROS       = "1";
    public static final String TIPO_TAXA_CET         = "2";

    /* Códigos de registro de desconto e estorno */
    public static final String COD_REG_DESCONTO = "6";
    public static final String COD_REG_ESTORNO  = "4";

    /* Status Registro Servidor - valores possiveis de srs_codigo */
    public static final String SRS_ATIVO     = "1";
    public static final String SRS_BLOQUEADO = "2";
    public static final String SRS_EXCLUIDO  = "3";
    public static final String SRS_FALECIDO  = "4";
    public static final String SRS_PENDENTE  = "5";
    public static final String SRS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA = "7";
    public static final String SRS_BLOQUEADO_AUTOMATICAMENTE_VARIACAO_MARGEM = "8";

    /* Campos do sistema - valores possíveis de cas_valor */
    public static final String CAS_SIM = "S";
    public static final String CAS_NAO = "N";
    public static final String CAS_BLOQUEADO = "B";
    public static final String CAS_OBRIGATORIO = "O";

    /* Campos do usuario - valores possíveis de cau_valor */
    public static final String CAU_SIM = "S";
    public static final String CAU_NAO = "N";

    public static final List<String> SRS_INATIVOS = Arrays.asList(
            SRS_EXCLUIDO,
            SRS_FALECIDO
    );

    public static final List<String> SRS_ATIVOS = Arrays.asList(
            SRS_ATIVO,
            SRS_BLOQUEADO,
            SRS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA,
            SRS_BLOQUEADO_AUTOMATICAMENTE_VARIACAO_MARGEM
    );

    public static final List<String> SRS_BLOQUEADOS = Arrays.asList(
            SRS_BLOQUEADO,
            SRS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA,
            SRS_BLOQUEADO_AUTOMATICAMENTE_VARIACAO_MARGEM
    );

    /* Funções internas do sistema - Mensagens para o usuário*/
    public static final String MSG_ERRO  = "MSG_ERRO";
    public static final String MSG_ALERT = "MSG_ALERT";
    public static final String MSG_ALERT_CONSULTAR_MARGEM = "MSG_ALERT_CONSULTAR_MARGEM";
    public static final String MSG_INFO  = "MSG_INFO";
    public static final String MSG_EXPIRACAO_SENHA = "EXPIRA_SENHA";

    /* Chave para a busca feita por like e não '=' - OBS: deve estar entre <> para que o filtro de XSS remova,
     * caso um usuário tente incluir a chave em algum campo de requisição. */
    public static final String LIKE_MULTIPLO = "<LIKE_MULTIPLO>";
    public static final String LIKE_UNICO = "<LIKE_UNICO>";

    /* Chave para a busca feita por '!=' e não '=' */
    public static final String NOT_EQUAL_KEY = "<NOT_EQUAL_KEY>";

    /* Chave para filtro por mais de um campo */
    public static final String AND_KEY = "<AND>";
    public static final String OR_KEY = "<OR>";
    public static final String IS_NULL_KEY = "<ISNULL>";
    public static final String IS_NOT_NULL_KEY = "<ISNOTNULL>";

    /* Valor do campo senha para quando a senha de consulta do servidor estiver cancelada. */
    public static final String USU_SENHA_SERVIDOR_CANCELADA = "CANCELADA";
    public static final String USU_SENHA_SERVIDOR_INICIAL = "SENHA";

    /* Código do usuário do sistema */
    public static final String USU_CODIGO_SISTEMA = "1";
    public static final String CSE_CODIGO_SISTEMA = "1";

    /* Código do perfil de usuário servidor */
    public static final String PER_CODIGO_SERVIDOR = "PERFIL-SERVIDOR";

    /* OPERAÇÕES DE INTEGRAÇÃO COM A FOLHA */
    public static final String INTEGRACAO_RETORNO = "1";
    public static final String INTEGRACAO_EXPORTACAO_MOV = "2";

    /* CONSTANTES PARA AS FUNÇÕES */
    public static final String FUN_CONS_CONSIGNANTE                       = "14";
    public static final String FUN_EDT_CONSIGNANTE                        = "15";
    public static final String FUN_CONS_ESTABELECIMENTOS                  = "16";
    public static final String FUN_EDT_ESTABELECIMENTOS                   = "17";
    public static final String FUN_CONS_ORGAOS                            = "18";
    public static final String FUN_EDT_ORGAOS                             = "19";
    public static final String FUN_CONS_CONSIGNATARIAS                    = "20";
    public static final String FUN_EDT_CONSIGNATARIAS                     = "21";
    public static final String FUN_CONS_SERVICOS                          = "22";
    public static final String FUN_EDT_SERVICOS                           = "23";
    public static final String FUN_CONS_USUARIOS                          = "24";
    public static final String FUN_EDT_USUARIOS                           = "25";
    public static final String FUN_CONS_CONSIGNACAO                       = "26";
    public static final String FUN_DEF_CONSIGNACAO                        = "27";
    public static final String FUN_INDF_CONSIGNACAO                       = "28";
    public static final String FUN_CANC_CONSIGNACAO                       = "29";
    public static final String FUN_SUSP_CONSIGNACAO                       = "30";
    public static final String FUN_REAT_CONSIGNACAO                       = "31";
    public static final String FUN_IMP_CAD_MARGENS                        = "32";
    public static final String FUN_EXP_MOV_FINANCEIRO                     = "33";
    public static final String FUN_IMP_RET_INTEGRACAO                     = "34";
    public static final String FUN_CAD_RET_INTEGRACAO                     = "35";
    public static final String FUN_DOW_ARQ_INTEGRACAO                     = "36";
    public static final String FUN_REL_MOV_FINANCEIRO                     = "37";
    public static final String FUN_REL_TARIFACAO                          = "38";
    public static final String FUN_REL_CONSIGNACOES                       = "39";
    public static final String FUN_EDT_ORGAO                              = "40";
    //public static final String FUN_CONS_TAB_STATUS                      = "45";
    //public static final String FUN_EDT_TAB_STATUS                       = "46";
    //public static final String FUN_CONS_FUNCAO                          = "47";
    //public static final String FUN_EDT_FUNCAO                           = "48";
    //public static final String FUN_CONS_PAPEL                           = "49";
    //public static final String FUN_EDT_PAPEL                            = "50";
    public static final String FUN_CONS_PERFIL                            = "51";
    public static final String FUN_EDT_PERFIL                             = "52";
    //public static final String FUN_REL_MOV_MES                          = "53";
    //public static final String FUN_REL_VLR_RECEBIMENTO                  = "54";
    public static final String FUN_EDT_CORRESPONDENTES                    = "55";
    public static final String FUN_CONS_CORRESPONDENTES                   = "56";
    public static final String FUN_RES_MARGEM                             = "57";
    public static final String FUN_CONF_RESERVA                           = "58";
    public static final String FUN_CANC_RESERVA                           = "59";
    public static final String FUN_RENE_CONTRATO                          = "60";
    public static final String FUN_LIQ_CONTRATO                           = "61";
    public static final String FUN_SOL_EMPRESTIMO                         = "63";
    public static final String FUN_CONS_CONVENIOS                         = "64";
    public static final String FUN_EDT_CONVENIOS                          = "65";
    public static final String FUN_UPL_ARQUIVOS                           = "66";
    public static final String FUN_CONS_USU_SERVIDORES                    = "67";
    public static final String FUN_AUT_RESERVA                            = "68";
    //public static final String FUN_EXCL_USUARIO                         = "69";
    public static final String FUN_EXCL_ORGAO                             = "70";
    public static final String FUN_EXCL_CORRESPONDENTE                    = "71";
    public static final String FUN_EXCL_CONSIGNATARIA                     = "72";
    public static final String FUN_EXCL_SERVICO                           = "73";
    public static final String FUN_EXCL_ESTABELECIMENTO                   = "74";
    //public static final String FUN_EXCL_CONSIGNANTE                     = "75";
    public static final String FUN_CONS_MARGEM                            = "76";
    public static final String FUN_EDT_PRAZO                              = "77";
    public static final String FUN_EDT_COEFICIENTES                       = "78";
    public static final String FUN_SIM_CONSIGNACAO                        = "79";
    public static final String FUN_CANC_SOLICITACAO                       = "80";
    public static final String FUN_CONF_SOLICITACAO                       = "81";
    public static final String FUN_EDT_SERVIDOR                           = "82";
    public static final String FUN_DOW_ARQ_GENERICOS                      = "83";
    public static final String FUN_UPL_ARQ_GENERICOS                      = "84";
    public static final String FUN_EDT_CONSIGNATARIA                      = "85";
    public static final String FUN_EDT_CORRESPONDENTE                     = "86";
    public static final String FUN_REL_INTEGRACAO                         = "87";
    public static final String FUN_ALT_CONSIGNACAO                        = "88";
    public static final String FUN_ACE_CONSIG_CONSIGNATARIA               = "89";
    //public static final String FUN_INT_SIST_EXTERNO                     = "90";
    //public static final String FUN_AVER_SOLICITACAO                     = "91";
    public static final String FUN_ACE_CONSIG_ESTABELECIMENTO             = "92";
    public static final String FUN_EDT_CONV_CORRESPONDENTE                = "93";
    public static final String FUN_CONS_CONV_CORRESPONDENTE               = "94";
    //public static final String FUN_ATU_SISTEMA                          = "98";
    //public static final String FUN_EDT_REG_SERVIDOR                     = "99";
    public static final String FUN_CONS_ORGAO                             = "100";
    //public static final String FUN_ENVIAR_EMAIL                         = "101";
    public static final String FUN_DESLIQ_CONTRATO                        = "102";
    //public static final String FUN_CONF_VLR_PRESTACAO                   = "103";
    public static final String FUN_REL_SINTETICO                          = "104";
    public static final String FUN_IMPORTACAO_VIA_LOTE                    = "105";
    //public static final String FUN_ENVIAR_EMAIL_RESUMO                  = "106";
    public static final String FUN_ALT_MARGEM_CONSIGNAVEL                 = "107";
    public static final String FUN_IMP_RET_ATRASADO                       = "108";
    public static final String FUN_USUARIO_ADMINISTRADOR                  = "109";
    //public static final String FUN_RELACIONAR_CORRESPONDENTE            = "110";
    public static final String FUN_REAJUSTAR_CONTRATOS                    = "111";
    //public static final String FUN_SINCRONIZAR_VIA_ARQUIVO              = "112";
    //public static final String FUN_REEXPORTA_VIA_ARQUIVO                = "113";
    public static final String FUN_DUPLICAR_PARCELA                       = "114";
    public static final String FUN_EDITAR_MENSAGEM                        = "115";
    public static final String FUN_LIBERAR_ESTOQUE                        = "116";
    //public static final String FUN_CARIMBO_VIRTUAL                      = "117";
    //public static final String FUN_CADASTRAR_SERVIDOR_INATIVO           = "118";
    public static final String FUN_CONSULTAR_SERVIDOR                     = "119";
    //public static final String FUN_CONSULTAR_REG_SERVIDOR               = "120";
    public static final String FUN_ALONGAR_CONTRATO                       = "121";
    public static final String FUN_LIQUIDAR_PARCELA                       = "122";
    public static final String FUN_CRIAR_GRUPO_SERVICO                    = "123";
    public static final String FUN_CONS_GRUPO_SERVICO                     = "124";
    public static final String FUN_REL_AUDITORIA                          = "125";
    public static final String FUN_TAXA_JUROS                             = "126";
    //public static final String FUN_CONS_DADOS_CADASTRAIS_SER_CSA        = "127";
    //public static final String FUN_CONS_DADOS_FUNCIONAIS_SER_CSA        = "128";
    public static final String FUN_REL_LIMITE_CONTRATO_ENTIDADE           = "129";
    public static final String FUN_REL_LIMITE_CONTRATO_GRUPO_SVC          = "130";
    public static final String FUN_INTEGRAR_XML                           = "131";
    //public static final String FUN_REL_XML                              = "132";
    //public static final String FUN_REL_SQL                              = "133";
    public static final String FUN_REL_RANKING                            = "134";
    public static final String FUN_EDT_MOTIVO_OPERACAO                    = "135";
    public static final String FUN_EDT_PARAM_CONSIGNATARIA                = "136";
    public static final String FUN_ACOMPANHAR_COMPRA_CONTRATOS            = "137";
    public static final String FUN_EDT_SALDO_DEVEDOR                      = "138";
    public static final String FUN_INFORMAR_PGT_SALDO_DEVEDOR             = "139";
    public static final String FUN_COMP_CONTRATO                          = "140";
    public static final String FUN_CADASTRAR_USUARIO_SERVIDOR             = "141";
    public static final String FUN_CRIAR_GRUPO_CONSIGNATARIA              = "142";
    public static final String FUN_CONF_LIQUIDACAO_COMPRA                 = "143";
    public static final String FUN_REINICIALIZAR_SENHA_SERVIDOR           = "144";
    public static final String FUN_REINICIALIZAR_SENHA                    = "145";
    public static final String FUN_CONS_HISTORICO_MARGEM                  = "146";
    public static final String FUN_EDT_COEFICIENTE_CORRECAO               = "147";
    public static final String FUN_REL_BLOQUEIO_SERVIDOR                  = "148";
    public static final String FUN_CONS_DADOS_CADASTRAIS_SERVIDOR         = "149";
    public static final String FUN_EMITIR_EXTRATO_DIVIDA_SERVIDOR         = "150";
    public static final String FUN_DESFAZER_ULT_RETORNO                   = "151";
    //public static final String FUN_CONSULTA_SENHA_SERIVDOR              = "152";
    public static final String FUN_REL_REPASSE                            = "153";
    public static final String FUN_CONS_COEFICIENTE_CORRECAO              = "154";
    public static final String FUN_CANC_COMPRA                            = "155";
    public static final String FUN_SOLICITAR_SALDO_DEVEDOR                = "156";
    public static final String FUN_CRIAR_USUARIOS                         = "157";
    //public static final String FUN_BLOQ_DESBLOQUEAR_USUARIOS            = "158";
    public static final String FUN_BLOQ_DESBLOQUEAR_USU_SERVIDOR          = "159";
    public static final String FUN_ALTERAR_SENHA_USU_SERVIDOR             = "160";
    public static final String FUN_PESQUISA_AVANCADA_CONSIGNACAO          = "161";
    public static final String FUN_CONS_GRUPO_CONSIGNATARIA               = "162";
    public static final String FUN_REL_ESTATISTICO                        = "163";
    public static final String FUN_EDT_INDICES                            = "164";
    public static final String FUN_EXCL_INDICES                           = "165";
    public static final String FUN_REL_USUARIOS                           = "166";
    //public static final String FUN_EXCL_REG_SERVIDOR                    = "167";
    public static final String FUN_EDT_CNV_REG_SERVIDOR                   = "168";
    public static final String FUN_CONS_CNV_REG_SERVIDOR                  = "169";
    public static final String FUN_ALT_MARGEM_CONSIGNAVEL_MENOR           = "170";
    public static final String FUN_CONS_EXTRATO_MARGEM                    = "171";
    public static final String FUN_REL_ALTERACAO_ADE_VALOR_RETORNO        = "172";
    public static final String FUN_REL_AUMENTO_ADE_VALOR_FORA_LIMITE      = "173";
    public static final String FUN_ATIVAR_SERNHA_SERVIDOR                 = "174";
    public static final String FUN_LISTAR_SOLICITACAO_SALDO_DEVEDOR       = "175";
    //public static final String FUN_REL_ESTATISTICO_PMSP                 = "176";
    public static final String FUN_REL_INF_BANC_DIVERGENTE                = "177";
    public static final String FUN_REL_TAXAS_EFETIVAS                     = "178";
    public static final String FUN_EDT_CALENDARIO                         = "179";
    public static final String FUN_CONS_CALENDARIO                        = "180";
    public static final String FUN_REL_COMPROMETIMENTO                    = "181";
    public static final String FUN_REL_SINTETICO_MOV_FIN                  = "182";
    public static final String FUN_REL_OCORRENCIA_CONSIGNATARIA           = "183";
    public static final String FUN_INC_PENALIDADE                         = "184";
    public static final String FUN_EDT_TIPO_PENALIDADE                    = "185";
    public static final String FUN_EDT_BLOQUEIO_FUN_SER_USU               = "186";
    public static final String FUN_REL_CONF_CAD_MARGENS                   = "187";
    public static final String FUN_REIMP_CONSIGNACAO                      = "188";
    public static final String FUN_RETIRAR_CONTRATO_COMPRA                = "189";
    public static final String FUN_SOL_RECALCULO_SALDO_DEVEDOR            = "190";
    public static final String FUN_REJEITAR_PGT_SALDO_DEVEDOR             = "191";
    public static final String FUN_REL_OCORRENCIA_SERVIDOR                = "192";
    public static final String FUN_EDITAR_FAQ                             = "193";
    public static final String FUN_CONSULTAR_CNV_SVC_BLOQ_SERVIDOR        = "194";
    public static final String FUN_REL_OCORRENCIA_USUARIO                 = "195";
    public static final String FUN_EDITAR_BANNER_PROPAGANDA               = "196";
    public static final String FUN_CONSULTAR_BANNER_PROPAGANDA            = "197";
    public static final String FUN_REL_PROVISIONAMENTO_MARGEM             = "198";
    public static final String FUN_TRANSFERIR_MARGEM                      = "199";
    public static final String FUN_EDITAR_ANEXO_CONSIGNACAO               = "200";
    public static final String FUN_REL_GERENCIAL_GERAL                    = "201";
    public static final String FUN_RECALCULAR_MARGEM_GERAL                = "202";
    public static final String FUN_RECALCULAR_MARGEM_PARCIAL              = "203";
    public static final String FUN_TRANSFERIR_CONSIGNACAO_GERAL           = "204";
    public static final String FUN_TRANSFERIR_CONSIGNACAO_PARCIAL         = "205";
    public static final String FUN_VALIDAR_PROCESSAMENTO_VIA_LOTE         = "206";
    public static final String FUN_LISTAR_SOLICITACAO_CONTRATOS           = "207";
    public static final String FUN_SOLICITAR_SALDO_DEVEDOR_PARA_LIQ       = "208";
    public static final String FUN_CONS_PRAZO                             = "209";
    public static final String FUN_REL_INTEGRACAO_CONSIGNATARIA           = "210";
    public static final String FUN_CONS_TAXA_JUROS                        = "211";
    public static final String FUN_CONS_COEFICIENTES                      = "212";
    public static final String FUN_REL_OCORRENCIA_AUTORIZACAO             = "213";
    public static final String FUN_REL_PERCENTUAL_REJEITO                 = "214";
    public static final String FUN_EDT_EMPRESA_CORRESPONDENTE             = "215";
    public static final String FUN_CONS_EMPRESA_CORRESPONDENTE            = "216";
    public static final String FUN_ALTERAR_SENHA_AUTORIZACAO_USU_SER      = "217";
    //public static final String FUN_DOWNLOAD_ARQUIVO_LOG                 = "218";
    public static final String FUN_IMP_BLOQUEIO_SERVIDOR                  = "219";
    public static final String FUN_REL_ACOMP_COMPRA_CONTRATO              = "220";
    public static final String FUN_REL_PERCENTUAL_CARTEIRA                = "221";
    public static final String FUN_REL_EMPRESA_CORRESPONDENTE             = "222";
    public static final String FUN_EXTRATO_CONSOLIDADO_SERVIDOR           = "223";
    public static final String FUN_EMITIR_DECLARACAO_MARGEM               = "224";
    public static final String FUN_REGISTRAR_OCO_CONSIGNACAO              = "225";
    public static final String FUN_IMPORTACAO_ARQUIVO_CONCILIACAO         = "226";
    public static final String FUN_EDT_MENU                               = "227";
    public static final String FUN_ALT_AVANCADA_CONSIGNACAO               = "228";
    public static final String FUN_REL_SINTETICO_ACOMP_COMPRA             = "229";
    public static final String FUN_CRIAR_COMUNICACAO                      = "230";
    public static final String FUN_LER_COMUNICACAO                        = "231";
    public static final String FUN_EDITAR_COMUNICACAO                     = "232";
    //public static final String FUN_CONS_CONSULTAS_MDX                   = "233";
    //public static final String FUN_EDT_CONSULTAS_MDX                    = "234";
    //public static final String FUN_EXCL_CONSULTAS_MDX                   = "235";
    public static final String FUN_LISTAR_BLOQUEIOS_CSA                   = "236";
    public static final String FUN_EFETUAR_LOGIN_SISTEMA_BLOQUEADO        = "237";
    public static final String FUN_CONS_PARAM_SISTEMA_CSE                 = "238";
    public static final String FUN_EDT_PARAM_SISTEMA_CSE                  = "239";
    public static final String FUN_ATIVAR_TAXA_JUROS_DATA_FUTURA          = "240";
    public static final String FUN_EDT_MANUAL_AJUDA_SISTEMA               = "241";
    public static final String FUN_CANC_RENEGOCIACAO                      = "242";
    public static final String FUN_REL_CONTRATO_LIQUIDADO_POS_CORTE       = "243";
    //public static final String FUN_CONS_PERFIL_SUPORTE                  = "244";
    //public static final String FUN_EDT_PERFIL_SUPORTE                   = "245";
    //public static final String FUN_CONS_USUARIO_SUPORTE                 = "246";
    //public static final String FUN_EDT_USUARIO_SUPORTE                  = "247";
    public static final String FUN_USUARIO_AUDITOR                        = "248";
    public static final String FUN_LISTAR_ARQUIVOS_LOTE                   = "249";
    public static final String FUN_CONS_CONTRACHEQUE                      = "250";
    public static final String FUN_GERAR_NOVAS_SENHAS_USU_SER             = "251";
    //public static final String FUN_ATIVAR_NOVAS_SENHAS_USU_SER          = "252";
    public static final String FUN_APROVAR_SALDO_DEVEDOR                  = "253";
    public static final String FUN_EDT_RELATORIOS                         = "254";
    public static final String FUN_CONS_RESTRICAO_ACESSO                  = "255";
    public static final String FUN_EDT_RESTRICAO_ACESSO                   = "256";
    public static final String FUN_INCLUSAO_VIA_LOTE                      = "257";
    public static final String FUN_ALTERACAO_VIA_LOTE                     = "258";
    public static final String FUN_EXCLUSAO_VIA_LOTE                      = "259";
    public static final String FUN_INTEGRA_SOAP_OPERACIONAL               = "260";
    public static final String FUN_INTEGRA_SOAP_COMPRA                    = "261";
    public static final String FUN_INTEGRA_SOAP_FOLHA                     = "262";
    public static final String FUN_INCLUSAO_AVANCADA_CONSIGNACAO          = "263";
    public static final String FUN_DESLIQUIDACAO_AVANCADA_CONTRATO        = "264";
    public static final String FUN_CONSULTAR_PLANO_DESCONTO               = "265";
    public static final String FUN_EDT_PLANO_DESCONTO                     = "266";
    public static final String FUN_EXCLUIR_PLANO_DESCONTO                 = "267";
    public static final String FUN_CONSULTAR_ENDERECO                     = "268";
    public static final String FUN_EDT_ENDERECO                           = "269";
    public static final String FUN_CONS_PERMISSIONARIO                    = "270";
    public static final String FUN_EDT_PERMISSIONARIO                     = "271";
    public static final String FUN_EXC_PERMISSIONARIO                     = "272";
    public static final String FUN_CONSULTAR_POSTO                        = "273";
    public static final String FUN_EDT_POSTO                              = "274";
    public static final String FUN_EXCLUIR_ENDERECO                       = "275";
    public static final String FUN_INC_DESPESA_INDIVIDUAL                 = "276";
    public static final String FUN_CONSULTAR_DESPESA_INDIVIDUAL           = "277";
    public static final String FUN_INC_DESPESA_COMUM                      = "278";
    public static final String FUN_CONSULTAR_DESPESA_COMUM                = "279";
    public static final String FUN_ATUALIZAR_PROCESSO_COMPRA              = "280";
    public static final String FUN_INCLUIR_ANEXO_COMUNICACAO              = "281";
    public static final String FUN_CANCELAR_DESPESA_COMUM                 = "282";
    public static final String FUN_REL_DESCONTOS                          = "283";
    public static final String FUN_ALTERAR_MULTIPLOS_CONTRATOS            = "284";
    public static final String FUN_REL_OCORRENCIA_PERMISSIONARIO          = "285";
    public static final String FUN_CONSULTAR_SOLICITACAO_SUPORTE          = "286";
    public static final String FUN_EDITAR_SOLICITACAO_SUPORTE             = "287";
    public static final String FUN_REL_SERVICO_OPERACAO_MES               = "288";
    public static final String FUN_SER_CONS_CONSIGNATARIA                 = "289";
    public static final String FUN_REL_CONFERENCIA_PERMISSIONARIO         = "290";
    public static final String FUN_REL_MARKET_SHARE_CSA                   = "291";
    public static final String FUN_LIVE_CHAT                              = "292";
    public static final String FUN_REL_DESCONTOS_MOV_FIN                  = "293";
    public static final String FUN_REL_SINTETICO_DESCONTOS                = "294";
    public static final String FUN_EDT_IP_ACESSO_CSA                      = "295";
    public static final String FUN_CONS_CONSIGNATARIA                     = "296";
    public static final String FUN_CONSULTAR_ARQUIVOS_XML                 = "297";
    public static final String FUN_CONSULTAR_LIMITE_TAXA                  = "298";
    public static final String FUN_EDT_LIMITE_TAXA                        = "299";
    public static final String FUN_CONS_CALENDARIO_FOLHA                  = "300";
    public static final String FUN_EDT_CALENDARIO_FOLHA                   = "301";
    public static final String FUN_DESCANCELAR_CONTRATO                   = "302";
    public static final String FUN_SIMULAR_RENEGOCIACAO                   = "303";
    public static final String FUN_CONS_RECLAMACAO                        = "304";
    public static final String FUN_EDT_RECLAMACAO                         = "305";
    public static final String FUN_REL_SINTETICO_RECLAMACOES              = "306";
    public static final String FUN_CONS_MOTIVO_RECLAMACAO                 = "307";
    public static final String FUN_EDT_MOTIVO_RECLAMACAO                  = "308";
    public static final String FUN_CONFIRMACAO_VIA_LOTE                   = "309";
    public static final String FUN_LISTAR_SOLICITACAO_RENEGOCIACAO        = "310";
    public static final String FUN_REL_RECLAMACOES                        = "311";
    public static final String FUN_CONSULTA_SENHA_MULT_AUTORIZA_SER       = "312";
    public static final String FUN_GESTOR_EDITA_SENHA_MULT_AUT_SER        = "313";
    public static final String FUN_PROCESSA_LOTE_MULTIPLAS_CSAS           = "314";
    public static final String FUN_ENVIAR_MENSAGEM_POR_EMAIL              = "315";
    public static final String FUN_REINICIALIZAR_SENHA_CSE                = "316";
    public static final String FUN_REINICIALIZAR_SENHA_ORG                = "317";
    public static final String FUN_REINICIALIZAR_SENHA_CSA                = "318";
    public static final String FUN_REINICIALIZAR_SENHA_COR                = "319";
    public static final String FUN_REINICIALIZAR_SENHA_SUP                = "320";
    public static final String FUN_CRIAR_USUARIOS_CSE                     = "321";
    public static final String FUN_CRIAR_USUARIOS_CSA                     = "322";
    public static final String FUN_CRIAR_USUARIOS_COR                     = "323";
    public static final String FUN_CRIAR_USUARIOS_ORG                     = "324";
    public static final String FUN_CRIAR_USUARIOS_SUP                     = "325";
    public static final String FUN_BLOQ_DESBLOQUEAR_USUARIOS_CSE          = "326";
    public static final String FUN_BLOQ_DESBLOQUEAR_USUARIOS_ORG          = "327";
    public static final String FUN_BLOQ_DESBLOQUEAR_USUARIOS_CSA          = "328";
    public static final String FUN_BLOQ_DESBLOQUEAR_USUARIOS_COR          = "329";
    public static final String FUN_BLOQ_DESBLOQUEAR_USUARIOS_SUP          = "330";
    public static final String FUN_EDT_BLOQUEIO_FUN_SER_USU_CSE           = "331";
    public static final String FUN_EDT_BLOQUEIO_FUN_SER_USU_ORG           = "332";
    public static final String FUN_EDT_BLOQUEIO_FUN_SER_USU_CSA           = "333";
    public static final String FUN_EDT_BLOQUEIO_FUN_SER_USU_COR           = "334";
    public static final String FUN_EDT_BLOQUEIO_FUN_SER_USU_SUP           = "335";
    public static final String FUN_CONS_USUARIOS_CSE                      = "336";
    public static final String FUN_CONS_USUARIOS_ORG                      = "337";
    public static final String FUN_CONS_USUARIOS_CSA                      = "338";
    public static final String FUN_CONS_USUARIOS_COR                      = "339";
    public static final String FUN_CONS_USUARIOS_SUP                      = "340";
    public static final String FUN_EDT_USUARIOS_CSE                       = "341";
    public static final String FUN_EDT_USUARIOS_ORG                       = "342";
    public static final String FUN_EDT_USUARIOS_CSA                       = "343";
    public static final String FUN_EDT_USUARIOS_COR                       = "344";
    public static final String FUN_EDT_USUARIOS_SUP                       = "345";
    public static final String FUN_CONS_PERFIL_CSE                        = "346";
    public static final String FUN_CONS_PERFIL_ORG                        = "347";
    public static final String FUN_CONS_PERFIL_CSA                        = "348";
    public static final String FUN_CONS_PERFIL_COR                        = "349";
    public static final String FUN_CONS_PERFIL_SUP                        = "350";
    public static final String FUN_EDT_PERFIL_CSE                         = "351";
    public static final String FUN_EDT_PERFIL_ORG                         = "352";
    public static final String FUN_EDT_PERFIL_CSA                         = "353";
    public static final String FUN_EDT_PERFIL_COR                         = "354";
    public static final String FUN_EDT_PERFIL_SUP                         = "355";
    public static final String FUN_EXCL_USUARIO_CSE                       = "356";
    public static final String FUN_EXCL_USUARIO_ORG                       = "357";
    public static final String FUN_EXCL_USUARIO_CSA                       = "358";
    public static final String FUN_EXCL_USUARIO_COR                       = "359";
    public static final String FUN_EXCL_USUARIO_SUP                       = "360";
    public static final String FUN_LISTAR_USUARIO                         = "361";
    public static final String FUN_REL_OCORRENCIA_REGISTRO_SERVIDOR       = "362";
    public static final String FUN_SOLICITAR_PROPOSTAS_PGT_DIVIDA         = "363";
    public static final String FUN_ACOMPANHAR_FINANCIAMENTO_DIVIDA        = "364";
    public static final String FUN_INFORMAR_PROPOSTAS_PGT_DIVIDA          = "365";
    public static final String FUN_APROVAR_PROPOSTA_PGT_DIVIDA            = "366";
    public static final String FUN_EXPORTA_XML_LAYOUT                     = "367";
    public static final String FUN_REL_INADIMPLENCIA                      = "368";
    public static final String FUN_CANC_MINHAS_RESERVAS                   = "369";
    public static final String FUN_REL_COMUNICACOES                       = "370";
    public static final String FUN_ANEXAR_COMPROVANTE_PAG_SALDO           = "371";
    public static final String FUN_CONF_LIQUIDACAO                        = "372";
    public static final String FUN_CADASTRAR_SERVIDOR                     = "373";
    public static final String FUN_INCLUIR_CONSIGNACAO                    = "374";
    public static final String FUN_SOLICITAR_SALDO_DEV_EXCLUSAO_SER       = "375";
    public static final String FUN_IMPORTACAO_ADEQUACAO_A_MARGEM          = "376";
    public static final String FUN_PROCESSA_LOTE_INCONSISTENCIA           = "377";
    public static final String FUN_REL_CONFIRMACAO_LEITURA_MSG            = "378";
    public static final String FUN_ACOMPANHAR_LEILAO_VIA_SIMULACAO        = "379";
    public static final String FUN_INFORMAR_PROPOSTAS_LEILAO              = "380";
    //public static final String FUN_APROVAR_PROPOSTA_LEILAO              = "381";
    public static final String FUN_SUSP_AVANCADA_CONSIGNACAO              = "382";
    public static final String FUN_REIMPLANTAR_CAPITAL_DEVIDO             = "383";
    public static final String FUN_IMP_HISTORICO                          = "384";
    public static final String FUN_REL_SALDO_DEVEDOR_CSA                  = "385";
    public static final String FUN_REL_INCLUSOES_POR_CSA                  = "386";
    public static final String FUN_REL_PRD_PAGAS_POR_CSA_PERIODO          = "387";
    public static final String FUN_AUT_EMISSAO_SENHA_AUTORIZACAO          = "388";
    public static final String FUN_EDT_RESTRICAO_ACESSO_POR_FUNCAO        = "389";
    public static final String FUN_REL_SOLICITACAO_SUPORTE_JIRA           = "390";
    public static final String FUN_IMP_ARQ_CONCILIACAO_MULTIPLAS_CSAS     = "391";
    public static final String FUN_EDT_IP_ACESSO_CSE                      = "392";
    public static final String FUN_EDT_IP_ACESSO_ORG                      = "393";
    public static final String FUN_EDT_IP_ACESSO_COR                      = "394";
    public static final String FUN_AUT_CADASTRO_EMAIL_SERVIDOR            = "395";
    public static final String FUN_REL_DESCONTOS_A_EXPIRAR                = "396";
    public static final String FUN_REL_ANEXOS_CONSIGNACAO                 = "397";
    public static final String FUN_PESQUISAR_SERVIDOR                     = "398";
    public static final String FUN_SOLICITAR_LEILAO_REVERSO               = "399";
    public static final String FUN_VALIDAR_SERVIDOR                       = "400";
    public static final String FUN_IMP_SER_DESLIGADO_BLOQUEADO            = "401";
    public static final String FUN_EDT_STATUS_REGISTRO_SERVIDOR           = "402";
    public static final String FUN_MOTIVO_NAO_CONCRETIZACAO_LEILAO        = "403";
    public static final String FUN_CADASTRO_RISCO_SERVIDOR_CSA            = "404";
    public static final String FUN_EDITAR_ANEXO_SOLICITACAO               = "405";
    public static final String FUN_APROV_REJCT_ANEXO_SOLICITACAO          = "406";
    public static final String FUN_DESBLOQUEAR_USUARIOS_SER               = "407";
    public static final String FUN_UPLOAD_ARQUIVO_EM_LOTE                 = "408";
    public static final String FUN_LISTAR_SER_PENDENTES_VALIDACAO         = "409";
    public static final String FUN_DEFINIR_SENHA_APP                      = "410";
    public static final String FUN_REL_INC_BENEFICIARIO_POR_PERIODO       = "411";
    public static final String FUN_REL_CONCESSAO_BENEFICIARIOS            = "412";
    public static final String FUN_EXCL_BENEFICIARIO_PERIODO              = "413";
    public static final String FUN_CONSULTAR_BENEFICIOS                   = "414";
    public static final String FUN_ALTERAR_CADASTRO_BENEFICIOS            = "415";
    public static final String FUN_CONSULTAR_CALCULO_BENEFICIOS           = "416";
    public static final String FUN_ALTERAR_CALCULO_BENEFICIOS             = "417";
    public static final String FUN_IMP_SER_FALECIDO                       = "418";
    public static final String FUN_EDITAR_TEXTO_SISTEMA                   = "419";
    public static final String FUN_CONSULTAR_BENEFICIARIOS                = "420";
    public static final String FUN_ALTERAR_CADASTRO_BENEFICIARIOS         = "421";
    public static final String FUN_CONSULTAR_ANEXO_BENEFICIARIOS          = "422";
    public static final String FUN_EDITAR_ANEXO_BENEFICIARIOS             = "423";
    public static final String FUN_CONSULTAR_RELACAO_BENEFICIOS           = "424";
    public static final String FUN_CONSULTAR_ENDERECO_SERVIDOR            = "425";
    public static final String FUN_EDT_ENDERECO_SERVIDOR                  = "426";
    public static final String FUN_REL_DOC_BEN_TIPO_VALIDADE              = "427";
    public static final String FUN_INTEGRACAO_ORIENTADA_VALIDAR           = "428";
    public static final String FUN_INTEGRACAO_ORIENTADA_PROCESSAR         = "429";
    public static final String FUN_REL_GERENCIAL_GERAL_CSA                = "430";
    public static final String FUN_REL_BENEFICIARIO_DATA_NASCIMENTO       = "431";
    public static final String FUN_SIMULACAO_CONTRATO_BENEFICIO           = "432";
    public static final String FUN_REL_CONTRATO_BENEFICIO                 = "433";
    public static final String FUN_LISTAR_LANCAMENTOS_CBE                 = "434";
    public static final String FUN_EDITAR_CADASTRO_BENEFICIARIO_AVANCADA  = "435";
    public static final String FUN_REGISTRAR_OCORRENCIA_CONTRATO_BENEFICIO= "436";
    public static final String FUN_SIMULACAO_ALTERACAO_CONTRATO_BENEFICIO = "437";
    public static final String FUN_REL_MOVIMENTO_BENEFICIO_CONS_DIRF      = "438";
    public static final String FUN_CONSULTAR_FATURAMENTO_BENEFICIOS       = "439";
    public static final String FUN_REL_COMISSIONAMENTO_AGEN_ANALITICO     = "440";
    public static final String FUN_EDITAR_CONTRATO_BENEFICIO              = "441";
    public static final String FUN_EDITAR_CONTRATO_BENEFICIO_AVANCADO     = "442";
    public static final String FUN_CONSULTA_ARQ_FATURAMENTO_BENEFICIO     = "443";
    public static final String FUN_ALTERA_ARQ_FATURAMENTO_BENEFICIO       = "444";
    public static final String FUN_AUTODESBLOQUEIO_CSE_ORG                = "445";
    public static final String FUN_AUTODESBLOQUEIO_CSA_COR                = "446";
    public static final String FUN_AUTODESBLOQUEIO_SUP                    = "447";
    public static final String FUN_CONS_DIRF_SERVIDOR                     = "448";
    public static final String FUN_AUTO_CADASTRO_SENHA_SERVIDOR           = "449";
    public static final String FUN_CONS_CORRESPONDENTE                    = "450";
    public static final String FUN_EXCLUIR_ANEXOS_CONSIGNACAO             = "451";
    public static final String FUN_EDITAR_REGRAS_TAXA_DE_JUROS            = "452";
    public static final String FUN_REL_CONF_CAD_VERBAS_CSA                = "453";
    public static final String FUN_VALIDAR_DADOS_BANCARIOS_SER_HOST_A_HOST= "454";
    public static final String FUN_DISTRIBUIR_CONSIGNACOES_POR_SERVICOS   = "455";
    public static final String FUN_REL_SINTETICO_CONSULTA_MARGEM_POR_USU  = "456";
    public static final String FUN_UPLOAD_BOLETOS_EM_LOTE                 = "457";
    public static final String FUN_CONSULTAR_BOLETO                       = "458";
    public static final String FUN_FLUXO_BENEFICIOS_PORTAL_PUBLICO        = "459";
    public static final String FUN_REL_HISTORICO_DESCONTOS_SER            = "460";
    public static final String FUN_CONS_VARIACAO_MARGEM                   = "461";
    public static final String FUN_ENVIAR_RESUMO_ADE_POR_EMAIL_SMS        = "462";
    public static final String FUN_LST_CONTRATOS_PENDENTES_BENEFICIO      = "463";
    public static final String FUN_REL_TERMO_PRIVACIDADE                  = "464";
    public static final String FUN_CAD_DISPENSA_VALIDACAO_DIGITAL_SER     = "465";
    public static final String FUN_ENVIAR_ARQ_RECUPERACAO_CREDITO         = "466";
    public static final String FUN_CONSULTAR_MOVIMENTO_FINANCEIRO         = "467";
    public static final String FUN_EDITAR_FLUXO_PARCELAS                  = "468";
    public static final String FUN_CONFIRMAR_OP_FILA_AUTORIZACAO          = "469";
    public static final String FUN_DASHBOARD_PROCESSAMENTO_FOLHA          = "470";
    public static final String FUN_SER_SOLICITAR_CANCELAMENTO_BENEFICIO   = "471";
    public static final String FUN_REATIVAR_CONTRATO_BENEFICIO            = "472";
    public static final String FUN_EXECUTAR_DECISAO_JUDICIAL              = "473";
    public static final String FUN_INCLUIR_TERMO_GARANTIA_ALUGUEL         = "474";
    public static final String FUN_NOTA_FISCAL_FATURAMENTO_BENEFICIO      = "475";
    public static final String FUN_INTERROMPER_PROCESSAMENTO_FOLHA        = "476";
    public static final String FUN_REL_RECUPERACAO_CREDITO                = "477";
    public static final String FUN_CONS_PARAM_ORGAO                       = "478";
    public static final String FUN_EDT_PARAM_ORGAO                        = "479";
    public static final String FUN_CONS_VENDAS_TODOS_USUARIOS             = "480";
    public static final String FUN_CONVERTER_ARQ_INTEGRACAO               = "481";
    public static final String FUN_REMOVER_ARQ_INTEGRACAO                 = "482";
    public static final String FUN_ENVIAR_ARQ_SALDO_DEVEDOR_LOTE          = "483";
    public static final String FUN_CONFIRMAR_RENEGOCIACAO                 = "484";
    public static final String FUN_DEMITIR_COLABORADOR                    = "485";
    public static final String FUN_POWER_BI                               = "486";
    public static final String FUN_CONS_PARCELAS_POR_STATUS               = "487";
    public static final String FUN_EDITAR_ENDERECOS_CONSIGNATARIA         = "488";
    public static final String FUN_EXP_MOV_FINANCEIRO_COMPLEMENTAR        = "489";
    public static final String FUN_CONSULTA_EVENTOS_TOTEM                 = "490";
    public static final String FUN_EDITAR_ENDERECOS_CORRESPONDENTE        = "491";
    public static final String FUN_ENVIAR_MSG_PORTABILIDADE_CSA_COR       = "492";
    public static final String FUN_CONFIGURAR_TOTEM                       = "493";
    public static final String FUN_DOWNLOAD_ANEXOS_CONTRATO_CSE_SUP       = "494";
    public static final String FUN_EDITAR_FUNCOES                         = "495";
    public static final String FUN_EDITAR_AUTORIZACAO_PARCIAL             = "496";
    public static final String FUN_REVER_LEILAO_NAO_CONCRETIZADO          = "497";
    public static final String FUN_CANCELAR_BENEFICIO_INADIMPLENCIA       = "498";
    public static final String FUN_SOLICITAR_LIQUIDAR_CONSIGNACAO         = "499";
    public static final String FUN_CONS_PERFIL_SER                        = "500";
    public static final String FUN_EDT_PERFIL_SER                         = "501";
    public static final String FUN_CANC_SOLICITAR_LIQUIDAR_CONSIGNACAO    = "502";
    public static final String FUN_DASHBOARD_ANEXOS_CONSIGNACAO           = "503";
    public static final String FUN_REL_SINTETICO_DECISAO_JUDICIAL         = "504";
    public static final String FUN_REL_DECISAO_JUDICIAL                   = "505";
    public static final String FUN_REL_SINTETICO_OCORRENCIA_CONSIGNACAO   = "506";
    public static final String FUN_DEMONSTRAR_TAXA_JUROS                  = "507";
    public static final String FUN_REL_REGRAS_CONVENIO                    = "508";
    public static final String FUN_CONSULTAR_MARGEM_EXTERNA               = "509";
    public static final String FUN_EDT_BLOQUEIO_VINCULO_SERVIDOR          = "510";
    public static final String FUN_REL_AVALIACAO_FAQ                      = "511";
    public static final String FUN_CONSULTAR_ANEXOS_REGISTRO_SERVIDOR     = "512";
    public static final String FUN_EDITAR_ANEXOS_REGISTRO_SERVIDOR        = "513";
    public static final String FUN_REL_ESTATISTICO_PROCESSAMENTO          = "514";
    public static final String FUN_REIMPLANTAR_PARCELA_MANUAL             = "515";
    public static final String FUN_REL_SOLICITACAO_SALDO_DEVEDOR          = "516";
    public static final String FUN_PROCESSA_LOTE_INF_SALDO_DEVEDOR        = "517";
    public static final String FUN_EXPORTAR_COPIA_SEGURANCA               = "518";
    public static final String FUN_IMPORTAR_COPIA_SEGURANCA               = "519";
    public static final String FUN_AJUSTAR_CONSIGNACOES_A_MARGEM          = "520";
    public static final String FUN_NOTIFICA_CONSIGNACAO_A_CSE             = "521";
    public static final String FUN_DATA_VALOR_CONSIGNACAO_LIBERADO_SER    = "522";
    public static final String FUN_EDT_COMPOSICAO_MARGEM_SER              = "523";
    public static final String FUN_REL_HISTORICO_LOGIN                    = "524";
    public static final String FUN_CONF_MARGEM_FOLHA_SER                  = "525";
    public static final String FUN_REL_GERENCIAL_GERAL_INTERNACIONAL      = "526";
    public static final String FUN_VALIDAR_DOCUMENTOS                     = "527";
    public static final String FUN_DASHBOARD_CREDENCIAMENTO               = "528";
    public static final String FUN_EDT_EMAILS_SER_UNIDADES                = "529";
    public static final String FUN_EDT_LIMITE_MARGEM_CSA_ORG              = "530";
    public static final String FUN_RESERVA_CONFIRMA_DOIS_PASSOS           = "531";
    public static final String FUN_SOLICITAR_PORTABILIDADE                = "532";
    public static final String FUN_BLOQUEAR_POSTO_POR_CSA_SVC             = "533";
    public static final String FUN_RELATORIO_POLITICA_PRIVACIDADE         = "534";
    public static final String FUN_APROVAR_TERMO_ADITIVO_CSA              = "535";
    public static final String FUN_RELATORIO_CUSTOMIZADO                  = "536";
    public static final String FUN_INICIA_PROCESSO_CREDENCIAMENTO         = "537";
    public static final String FUN_BLOQUEAR_CSA_PARA_SERVIDOR             = "538";
    public static final String FUN_EDITAR_PARAM_POSTO_GRADUACAO           = "540";
    public static final String FUN_ANEXAR_FOTO                            = "541";
    public static final String FUN_CONSULTAR_FUNCOES_ENVIO_EMAIL          = "542";
    public static final String FUN_EDITAR_FUNCOES_ENVIO_EMAIL             = "543";
    public static final String FUN_PROCESSA_LOTE_RECISAO                  = "544";
    public static final String FUN_UPLOAD_ANEXO_CSA                       = "545";
    public static final String FUN_REIMPLANTE_CONSIGNACAO_LOTE            = "546";
    public static final String FUN_UPLOAD_ARQUIVOS_XML                    = "547";
    public static final String FUN_DOWNLOAD_ARQUIVOS_HISTORICO            = "548";
    public static final String FUN_RELATORIO_MOV_FIN_SER                  = "549";
    public static final String FUN_REL_PARCELAS_PROCESSADAS_FUTUDAS       = "550";
    public static final String FUN_EDT_SALDO_DEVEDOR_SOLICITACAO_SER      = "551";
    public static final String FUN_LISTAR_USUARIOS_AUTENTICADOS           = "552";
    public static final String FUN_ENCERRAR_SESSAO_USUARIO                = "553";
    public static final String FUN_REL_SALDO_DEVEDOR_SERVIDOR             = "554";
    public static final String FUN_VINCULO_CSA_RSE                        = "555";
    public static final String FUN_REL_GERENCIAL_GERAL_CSA_SINTETICO      = "556";
    public static final String FUN_AUTORIZAR_MARGEM_CONSIGNATARIA         = "557";
    public static final String FUN_REL_SINTETICO_AUTORIZACAO_MARGEM_SER   = "558";
    public static final String FUN_CADASTRAR_BENEFICIARIO_NA_RESERVA      = "559";
    public static final String FUN_CADASTRO_EDICAO_REGRA_LIMTE_OPERACAO   = "560";
    public static final String FUN_ENVIAR_EMAIL_SIMULACAO_SOLICITACAO_ADE = "561";
    public static final String FUN_ANALISAR_VARIACAO_MARGEM               = "562";
    public static final String FUN_CONSULTAR_PERFIL_CONSIGNADO            = "563";
    public static final String FUN_CONSULTAR_SALARIO_SERV_REST            = "564";
    public static final String FUN_INTEGRAR_CREDITO_TRABALHADOR           = "565";
    public static final String FUN_REL_SALDO_DEVEDOR_SER                  = "566";
    public static final String FUN_OCULTAR_RSE_PARA_CSA                   = "567";
    public static final String FUN_EDITAR_FORMULARIO_PESQUISA             = "568";
    public static final String FUN_RESPONDER_FORMULARIO_PESQUISA          = "569";
    public static final String FUN_ENVIAR_MENSAGEM_MASSA_PUSH_NOTIFICATION= "570";


    /* Funções que pertencem ao perfil administrador */
    public static List<String> FUNCOES_ADMINISTRADOR = Arrays.asList(
            FUN_USUARIO_ADMINISTRADOR,
            FUN_INTEGRA_SOAP_OPERACIONAL,
            FUN_INTEGRA_SOAP_COMPRA,
            FUN_INTEGRA_SOAP_FOLHA,
            FUN_INTEGRAR_XML,
            FUN_RECALCULAR_MARGEM_GERAL,
            FUN_RECALCULAR_MARGEM_PARCIAL,
            FUN_TRANSFERIR_CONSIGNACAO_GERAL,
            FUN_EDT_MENU,
            FUN_EFETUAR_LOGIN_SISTEMA_BLOQUEADO,
            FUN_CONS_PARAM_SISTEMA_CSE,
            FUN_EDT_PARAM_SISTEMA_CSE,
            FUN_ATIVAR_TAXA_JUROS_DATA_FUTURA,
            FUN_EDT_MANUAL_AJUDA_SISTEMA,
            FUN_CONS_PERFIL_SUP,
            FUN_EDT_PERFIL_SUP,
            FUN_CONS_USUARIOS_SUP,
            FUN_EDT_USUARIOS_SUP,
            FUN_EXPORTAR_COPIA_SEGURANCA,
            FUN_IMPORTAR_COPIA_SEGURANCA
    );

    /* Funções que não são permitidas a associação a um perfil */
    public static List<String> FUNCOES_NAO_PERMITIDAS_PERFIL = Arrays.asList(
            FUN_USUARIO_ADMINISTRADOR,
            FUN_INTEGRA_SOAP_OPERACIONAL,
            FUN_INTEGRA_SOAP_COMPRA,
            FUN_INTEGRA_SOAP_FOLHA,
            FUN_INTEGRAR_XML
    );

    /* Funções possíveis ao importar lote */
    public static List<String> FUNCOES_IMPORTACAO_LOTE = Arrays.asList(
            FUN_IMPORTACAO_VIA_LOTE,
            FUN_VALIDAR_PROCESSAMENTO_VIA_LOTE,
            FUN_INCLUSAO_VIA_LOTE,
            FUN_ALTERACAO_VIA_LOTE,
            FUN_EXCLUSAO_VIA_LOTE,
            FUN_CONFIRMACAO_VIA_LOTE
    );

    /* Chaves RSA para comunicação com o centralizador */
    public static final String RSA_MODULUS_ECONSIG = "9be6f8a47e08218d4e5caf063a52bd8624424829b5168ec76591a836f616cc5ccde1e0d93d8df239480f53e9424d222d8bf9b428a8066981d6cab80165704dc1";
    public static final String RSA_PUBLIC_KEY_ECONSIG = "10001";
    public static final String RSA_PRIVATE_KEY_ECONSIG = "1379243197b4784c682f5785559fa420af42c034c573cdddc6280d74e00434fe68a89e1ffd59242783d5a95d295e2506a136820993bf737726e2b9c9e13db509";

    public static final String RSA_MODULUS_CENTRALIZADOR = "dd4566956d39d745129c58c5443349cc54b9febc12b7eb41ff9c25dcb365dd8995f598a2c5233260e7d1e7401d58e85f4b64b95fea8ddd99bf1af9e014ed2f31";
    public static final String RSA_PUBLIC_KEY_CENTRALIZADOR = "010001";

    public static final String PROTOCOLO_KEYSTORE_PATH_PROPERTY = "/keystore";
    public static final String PROTOCOLO_KEYSTORE_PASS_PROPERTY = "igorlucaseconsig";
    public static final String PROTOCOLO_KEYSTORE_ALIAS_ECONSIG_PROPERTY = "econsig";
    public static final String PROTOCOLO_KEYSTORE_ALIAS_CA_PROPERTY = "zetraca";

    /* Nome do atributo de sessão para o par de chaves RSA para verificação de senhas */
    public static final String RSA_KEY_PAIR_SESSION_ATTR_NAME = "_RSA_KEY_PAIR_";

    /* Tamanho das chaves RSA geradas para verificação de senhas */
    public static final int RSA_KEY_SIZE = 512;

    /* Validação de movimento */
    public static final String VALIDACAO_MOVIMENTO_RESULTADO_OK    = "0";
    public static final String VALIDACAO_MOVIMENTO_RESULTADO_AVISO = "1";
    public static final String VALIDACAO_MOVIMENTO_RESULTADO_ERRO  = "2";

    /* Forma de cálculo de coeficientes de correção */
    public static final String FORMA_CALCULO_PADRAO = "0";
    public static final String FORMA_CALCULO_OUTRO  = "1";

    /* Tipos de coeficiente */
    public static final String CFT_DIARIO = "D";
    public static final String CFT_MENSAL = "M";

    /* Possíveis Origens da AutorizacaoDesconto */
    public static final String ORIGEM_ADE_NOVA = "1";
    public static final String ORIGEM_ADE_RENEGOCIADA = "2";
    public static final String ORIGEM_ADE_COMPRADA = "3";

    /* Possíveis motivos de encerramento de AutorizacaoDesconto*/
    public static final String TERMINO_ADE_VENDA = "1";
    public static final String TERMINO_ADE_RENEGOCIADA = "2";
    public static final String TERMINO_ADE_LIQ_ANTECIPADA = "3";
    public static final String TERMINO_ADE_CONCLUSAO = "4";
    public static final String TERMINO_ADE_CANCELADA = "5";

    /* Possíveis tipos de exportação de AutorizacaoDesconto*/
    public static final String ADE_EXPORTACAO_BLOQUEADA = "B";
    public static final String ADE_EXPORTACAO_PERMITIDA = "S";

    /* Tipos de ordenação das taxas de juros pelo prazo */
    //não se aplica
    public static final String ORDEM_TAXAS_NA = "0";
    //ordem crescente
    public static final String ORDEM_TAXAS_ASC = "1";
    //ordem decrescente
    public static final String ORDEM_TAXAS_DESC = "2";

    /* Motivos de bloqueio de consignatária pelo módulo de compra */
    public static final int BLOQUEIO_INF_SALDO_DEVEDOR_COMPRA = 1;
    public static final int BLOQUEIO_INF_PGT_SALDO_COMPRA     = 2;
    public static final int BLOQUEIO_LIQUIDACAO_COMPRA        = 3;
    public static final int BLOQUEIO_SOLIC_SALDO_DEVEDOR      = 4;

    /* Grupos de função */
    public static final String GRUPO_FUNCAO_GERAL                = "0";
    public static final String GRUPO_FUNCAO_OPERACIONAL          = "1";
    public static final String GRUPO_FUNCAO_COMPRA_CONTRATO      = "2";
    public static final String GRUPO_FUNCAO_SIMULACAO            = "3";
    public static final String GRUPO_FUNCAO_RELATORIOS           = "4";
    public static final String GRUPO_FUNCAO_MANUTENCAO_CSE       = "5";
    public static final String GRUPO_FUNCAO_MANUTENCAO_EST       = "6";
    public static final String GRUPO_FUNCAO_MANUTENCAO_ORG       = "7";
    public static final String GRUPO_FUNCAO_MANUTENCAO_SER       = "8";
    public static final String GRUPO_FUNCAO_MANUTENCAO_CSA       = "9";
    public static final String GRUPO_FUNCAO_MANUTENCAO_COR       = "10";
    public static final String GRUPO_FUNCAO_MANUTENCAO_SVC       = "11";
    public static final String GRUPO_FUNCAO_MANUTENCAO_CNV       = "12";
    public static final String GRUPO_FUNCAO_MANUTENCAO_USU       = "13";
    public static final String GRUPO_FUNCAO_MANUTENCAO_USU_CSE   = "14";
    public static final String GRUPO_FUNCAO_MANUTENCAO_USU_ORG   = "15";
    public static final String GRUPO_FUNCAO_MANUTENCAO_USU_CSA   = "16";
    public static final String GRUPO_FUNCAO_MANUTENCAO_USU_COR   = "17";
    public static final String GRUPO_FUNCAO_MANUTENCAO_USU_SUP   = "18";
    public static final String GRUPO_FUNCAO_MANUTENCAO_USU_SER   = "19";
    public static final String GRUPO_FUNCAO_INTEGRACAO_FOLHA     = "20";
    public static final String GRUPO_FUNCAO_ADMINISTRADOR        = "21";
    public static final String GRUPO_FUNCAO_SIS_DESC_PREFEITURA  = "22";
    public static final String GRUPO_FUNCAO_MANUTENCAO_BEN       = "23";

    /* Valor atribuido a margem dos servidores que não foram enviados pela folha para
     * que possam ser excluidos no final do processo de importação de margem */
    public static final String MARGEM_SERVIDOR_PRESTES_A_SER_EXCLUIDO = "-1000000";

    /* Tipos de processamento de importação de retorno */
    public static final int TIPO_RETORNO_NORMAL    = 1;
    public static final int TIPO_RETORNO_ATRASADO  = 2;
    public static final int TIPO_RETORNO_CRITICA   = 3;
    public static final int TIPO_RETORNO_CONCLUSAO = 4;

    public static final String SESSAO_INVALIDA = "SESSAO_INVALIDA";
    public static final String MSG_SESSAO_INVALIDA = "MSG_SESSAO_INVALIDA";

    /* Status possiveis dos filtros que podem ser configurados nos relatorios */
    public static final String REL_FILTRO_NAO_EXISTENTE = "0";
    public static final String REL_FILTRO_EXISTENTE = "1";
    public static final String REL_FILTRO_OBRIGATORIO = "2";

    /* Valores possíveis do filtro camppo_prd_realizado nos relatorios */
    public static final String REL_FILTRO_VLR_REALIZADO_MENOR_PREVISTO = "0";
    public static final String REL_FILTRO_VLR_REALIZADO_IGUAL_PREVISTO = "1";
    public static final String REL_FILTRO_VLR_REALIZADO_MAIOR_PREVISTO = "2";

    /* Valores possíveis do filtro campo_comprometimento_margem nos relatorios */
    public static final String REL_FILTRO_COMPROMETIMENTO_MARGEM_MENOR_ZERO = "0";
    public static final String REL_FILTRO_COMPROMETIMENTO_MARGEM_0_A_10 = "1";
    public static final String REL_FILTRO_COMPROMETIMENTO_MARGEM_10_A_20 = "2";
    public static final String REL_FILTRO_COMPROMETIMENTO_MARGEM_20_A_30 = "3";
    public static final String REL_FILTRO_COMPROMETIMENTO_MARGEM_30_A_40 = "4";
    public static final String REL_FILTRO_COMPROMETIMENTO_MARGEM_40_A_50 = "5";
    public static final String REL_FILTRO_COMPROMETIMENTO_MARGEM_50_A_60 = "6";
    public static final String REL_FILTRO_COMPROMETIMENTO_MARGEM_60_A_70 = "7";
    public static final String REL_FILTRO_COMPROMETIMENTO_MARGEM_70_A_80 = "8";
    public static final String REL_FILTRO_COMPROMETIMENTO_MARGEM_80_A_90 = "9";
    public static final String REL_FILTRO_COMPROMETIMENTO_MARGEM_90_A_100 = "10";
    public static final String REL_FILTRO_COMPROMETIMENTO_MARGEM_MAIOR_CEM = "11";

    //  Constantes para as operações disponíveis na interface de requisião remota de operação
    public static final String OP_ALONGAR_CONSIGNACAO       = "Alongar Consignacao";
    public static final String OP_ALONGAR_CONSIGNACAO_V6_0  = "Alongar Consignacao v6_0";
    public static final String OP_ALTERAR_CONSIGNACAO       = "Alterar Consignacao";
    public static final String OP_ALTERAR_CONSIGNACAO_V6_0  = "Alterar Consignacao v6_0";
    public static final String OP_ATUALIZAR_PARCELA         = "Atualizar Parcela";
    public static final String OP_AUTORIZAR_RESERVA         = "Autorizar Reserva";
    public static final String OP_AUTORIZAR_RESERVA_V6_0    = "Autorizar Reserva v6_0";
    public static final String OP_CANCELAR_CONSIGNACAO      = "Cancelar Consignacao";
    public static final String OP_CANCELAR_CONSIGNACAO_V6_0 = "Cancelar Consignacao v6_0";
    public static final String OP_CANCELAR_CONSIGNACAO_SV   = "Cancelar Consignacao SV";
    public static final String OP_CANCELAR_RENEGOCIACAO     = "Cancelar Renegociação";
    public static final String OP_CANCELAR_RENEGOCIACAO_V6_0 = "Cancelar Renegociação v6_0";
    public static final String OP_CANCELAR_RESERVA          = "Cancelar Reserva";
    public static final String OP_CANCELAR_RESERVA_V6_0     = "Cancelar Reserva v6_0";
    public static final String OP_CONFIRMAR_RESERVA         = "Confirmar Reserva";
    public static final String OP_CONFIRMAR_RESERVA_V6_0    = "Confirmar Reserva v6_0";
    public static final String OP_CONFIRMAR_SOLICITACAO     = "Confirmar Solicitacao";
    public static final String OP_CONFIRMAR_SOLICITACAO_V6_0 = "Confirmar Solicitacao v6_0";
    public static final String OP_CONFIRMAR_CONSIGNACAO     = "Confirmar Consignacao";
    public static final String OP_CONSULTAR_CONSIGNACAO     = "Consultar Consignacao";
    public static final String OP_CONSULTAR_CONSIGNACAO_V6_0 = "Consultar Consignacao v6_0";
    public static final String OP_CONSULTAR_CONSIGNACAO_V8_0 = "Consultar Consignacao v8_0";
    public static final String OP_CONSULTAR_MARGEM          = "Consultar Margem";
    public static final String OP_CONSULTAR_MARGEM_V3_0     = "Consultar Margem v3_0";
    public static final String OP_CONSULTAR_MARGEM_V6_0     = "Consultar Margem v6_0";
    public static final String OP_CONSULTAR_MARGEM_V7_0     = "Consultar Margem v7_0";
    public static final String OP_CONSULTAR_MARGEM_V8_0     = "Consultar Margem v8_0";
    public static final String OP_DETALHAR_CONSULTA_ADE     = "Detalhar Consulta Consignação";
    public static final String OP_DETALHAR_CONSULTA_ADE_V4_0 = "Detalhar Consulta Consignação v4_0";
    public static final String OP_DETALHAR_CONSULTA_ADE_V6_0 = "Detalhar Consulta Consignação v6_0";
    public static final String OP_DETALHAR_CONSULTA_ADE_V8_0 = "Detalhar Consulta Consignação v8_0";
    public static final String OP_INSERIR_SOLICITACAO       = "Inserir Solicitacao";
    public static final String OP_INSERIR_SOLICITACAO_V6_0  = "Inserir Solicitacao v6_0";
    public static final String OP_INSERIR_SOLICITACAO_V8_0  = "Inserir Solicitacao v8_0";
    public static final String OP_LIQUIDAR_CONSIGNACAO      = "Liquidar Contrato";
    public static final String OP_LIQUIDAR_CONSIGNACAO_V6_0 = "Liquidar Contrato v6_0";
    public static final String OP_LISTA_SOLICITACOES        = "Lista Solicitacoes";
    public static final String OP_REATIVAR_CONSIGNACAO      = "Reativar Consignacao";
    public static final String OP_REATIVAR_CONSIGNACAO_V6_0 = "Reativar Consignacao v6_0";
    public static final String OP_REATIVAR_CONSIGNACAO_V8_0 = "Reativar Consignacao v8_0";
    public static final String OP_RENEGOCIAR_CONSIGNACAO    = "Renegociar Contrato";
    public static final String OP_RENEGOCIAR_CONSIGNACAO_V6_0 = "Renegociar Contrato v6_0";
    public static final String OP_RESERVAR_MARGEM           = "Reservar Margem";
    public static final String OP_RESERVAR_MARGEM_V6_0      = "Reservar Margem v6_0";
    public static final String OP_RESERVAR_MARGEM_V8_0      = "Reservar Margem v8_0";
    public static final String OP_SIMULAR_CONSIGNACAO       = "Simular Consignacao";
    public static final String OP_SIMULAR_CONSIGNACAO_V8_0  = "Simular Consignacao v8_0";
    public static final String OP_SUSPENDER_CONSIGNACAO     = "Suspender Consignacao";
    public static final String OP_SUSPENDER_CONSIGNACAO_V6_0 = "Suspender Consignacao v6_0";
    public static final String OP_VALIDAR_ACESSO_SERVIDOR   = "Validar Acesso";
    public static final String OP_CONSULTAR_PARAMETROS      = "Consultar Parametros";
    public static final String OP_CONSULTAR_PARAMETROS_v2_0 = "Consultar Parametros v2_0";
    public static final String OP_CONSULTAR_PARAMETROS_v8_0 = "Consultar Parametros v8_0";
    public static final String OP_DESLIQUIDAR_CONTRATO      = "Desliquidar Contrato";
    public static final String OP_ALONGAR_CONTRATO          = "Alongar Contrato";
    public static final String OP_COMPRAR_CONTRATO          = "Comprar Contrato";
    public static final String OP_CADASTRAR_CONSIGNATARIA   = "Cadastrar Consignataria";
    public static final String OP_CONSULTAR_CONSIGNATARIA   = "Consultar Consignataria";
    public static final String OP_CADASTRAR_VERBA           = "Cadastrar Verba";
    public static final String OP_CONSULTAR_VERBA           = "Consultar Verba";
    public static final String OP_CADASTRAR_ORGAO           = "Cadastrar Orgao";
    public static final String OP_CONSULTAR_ORGAO           = "Consultar Orgao";
    public static final String OP_CADASTRAR_ESTABELECIMENTO = "Cadastrar Estabelecimento";
    public static final String OP_CONSULTAR_ESTABELECIMENTO = "Consultar Estabelecimento";
    public static final String OP_ATUALIZAR_MARGEM          = "Atualizar Margem";
    public static final String OP_ACOMPANHAR_COMPRA         = "Acompanhar Compra de Contrato";
    public static final String OP_EDT_SALDO_DEVEDOR         = "Informar Saldo Devedor";
    public static final String OP_INF_PG_SALDO_DEVEDOR      = "Informar Pagamento Saldo Devedor";
    public static final String OP_SOL_RECALC_SALDO_DEVEDOR  = "Solicitar Recalculo Saldo Devedor";
    public static final String OP_RETIRAR_CONTRATO_COMPRA   = "Retirar Contrato da Compra";
    public static final String OP_CANCELAR_CONTRATO_COMPRA  = "Cancelar Contrato da Compra";
    public static final String OP_LIQUIDAR_CONTRATO_COMPRA  = "Liquidar Contrato da Compra";
    public static final String OP_REJ_PG_SALDO_DEVEDOR      = "Rejeitar Pagamento Saldo Devedor";
    public static final String OP_CONSULTAR_ADE_PARA_COMPRA = "Consultar Consignações para Compra";
    public static final String OP_INCLUIR_ANEXO_CONSIGNACAO = "Incluir Anexo de Consignacao";
    public static final String OP_GERAR_SENHA_AUTORIZACAO   = "Gerar Senha Autorizacao";
    public static final String OP_CONSULTAR_CONTRACHEQUE    = "Consultar Contracheque";
    public static final String OP_CANCELAR_SOLICITACAO      = "Cancelar Solicitacao";
    public static final String OP_CONS_DADOS_CADASTRAIS     = "Consultar Dados Cadastrais";
    public static final String OP_CONS_DADOS_CADASTRAIS_V3_0  = "Consultar Dados Cadastrais v3_0";
    public static final String OP_VERIFICA_LIMITE_SENHA_AUT = "Verificar Limites Senha Autorizacao";
    public static final String OP_RECUPERAR_PERG_DADOS_CAD  = "Recuperar Pergunta Dados Cadastrais";
    public static final String OP_VERIFICA_RESP_PERG_DADOS  = "Verificar Resposta Pergunta Dados Cadastrais";
    public static final String OP_INCLUIR_DADO_CONSIGNACAO  = "Incluir Dado de Consignacao";
    public static final String OP_LISTAR_DADO_CONSIGNACAO   = "Listar Dado de Consignacao";
    public static final String OP_REQUISICAO_BASICA         = "Requisicao basica ao servico";
    public static final String OP_CADASTRAR_EMAIL_SERVIDOR  = "Cadastrar email do servidor";
    public static final String OP_VERIFICAR_EMAIL_SERVIDOR  = "Verificar Email Servidor";
    public static final String OP_CONSULTAR_PARCELA         = "Consultar Parcela";
    public static final String OP_LIQUIDAR_PARCELA          = "Liquidar Parcela";
    public static final String OP_CADASTRAR_TAXA_JUROS      = "Cadastrar Taxa de Juros";
    public static final String OP_PESQUISAR_SERVIDOR        = "Pesquisar Servidor";
    public static final String OP_CADASTRAR_SERVIDOR        = "Cadastrar Servidor";
    public static final String OP_CADASTRAR_SERVIDOR_V8_0   = "Cadastrar Servidor v8_0";
    public static final String OP_PESQUISAR_SERVIDOR_V8_0   = "Pesquisar Servidor v8_0";
    public static final String OP_CADASTRAR_USUARIO         = "Cadastrar Usuario";
    public static final String OP_MODIFICAR_CONSIGNANTE     = "Modificar Consignante";
    public static final String OP_CADASTRAR_CALENDARIO_FOLHA= "Cadastrar Calendario Folha";
    public static final String OP_MODIFICAR_PARAM_SISTEMA   = "Modificar Parametro de Sistema";
    public static final String OP_MODIFICAR_PARAM_SERVICO   = "Modificar Parametro de Servico";
    public static final String OP_MODIFICAR_USUARIO         = "Modificar Usuario";
    public static final String OP_EDITAR_STATUS_SERVIDOR    = "Editar Status Servidor";
    public static final String OP_EDITAR_STATUS_SERVIDOR_V8_0  = "Editar Status Servidor V8_0";
    public static final String OP_VALIDAR_DADOS_BANCARIOS_SER = "Validar Dados Bancarios Servidor";
    public static final String OP_VALIDAR_DADOS_BANCARIOS_SER_V8_0 = "Validar Dados Bancarios Servidor v8_0";
    public static final String OP_CONSULTAR_MOVIMENTO_FINANCEIRO = "Consultar Movimento Financeiro";
    public static final String OP_CADASTRAR_USUARIO_OPERACIONAL = "Cadastrar Usuario Operacional";
    public static final String OP_EDITAR_STATUS_USUARIO     = "Editar Status Usuario";
    public static final String OP_DOWNLOAD_ANEXOS_CONSIGNACAO = "Download Anexos de Consignacao";
    public static final String OP_CONSULTAR_SERVICO           = "Consultar Servico";
    public static final String OP_CADASTRAR_SERVICO           = "Cadastrar Servico";
    public static final String OP_CONSULTAR_PERFIL_USUARIO    = "Consultar Perfil Usuario";
    public static final String OP_LISTAR_ARQUIVO_INTEGRACAO   = "Listar Arquivo Integracao";
    public static final String OP_ENVIAR_ARQUIVO_INTEGRACAO   = "Enviar Arquivo Integracao";
    public static final String OP_DOWNLOAD_ARQUIVO_INTEGRACAO = "Download Arquivo Integracao";
    public static final String OP_LISTAR_SOLICITACAO_SALDO    = "Listar Solicitacao Saldo";
    public static final String OP_EDITAR_SALDO_DEVEDOR        = "Editar Saldo Devedor";
    public static final String OP_LISTAR_PARCELAS             = "Listar Parcelas";
    public static final String OP_CONSULTAR_VALIDACAO_DOCUMENTACAO_v8_0 = "Consultar Validacao Documentacao v8_0";
    public static final String OP_CONSULTAR_PERFIL_CONSIGNADO = "Consultar Perfil Consignado";
    public static final String OP_CONSULTAR_REGRAS = "Consultar Regras";

    public static final List<String> OPERACAO_NAO_AUTENTICA_SERVIDOR = Arrays.asList(
            OP_CADASTRAR_EMAIL_SERVIDOR,
            OP_VERIFICAR_EMAIL_SERVIDOR
    );

    public static final List<String> OPERACOES_AUTORIZAR_RESERVA = Arrays.asList(
            OP_AUTORIZAR_RESERVA,
            OP_AUTORIZAR_RESERVA_V6_0
    );

    public static final List<String> OPERACOES_CANCELAR_CONSIGNACAO = Arrays.asList(
            OP_CANCELAR_CONSIGNACAO,
            OP_CANCELAR_CONSIGNACAO_V6_0
    );

    public static final List<String> OPERACOES_CANCELAR_RENEGOCIACAO = Arrays.asList(
            OP_CANCELAR_RENEGOCIACAO,
            OP_CANCELAR_RENEGOCIACAO_V6_0
    );

    public static final List<String> OPERACOES_CANCELAR_RESERVA = Arrays.asList(
            OP_CANCELAR_RESERVA,
            OP_CANCELAR_RESERVA_V6_0
    );

    public static final List<String> OPERACOES_CONFIRMAR_RESERVA = Arrays.asList(
            OP_CONFIRMAR_RESERVA,
            OP_CONFIRMAR_RESERVA_V6_0
    );

    public static final List<String> OPERACOES_CONFIRMAR_SOLICITACAO = Arrays.asList(
            OP_CONFIRMAR_SOLICITACAO,
            OP_CONFIRMAR_SOLICITACAO_V6_0
    );

    public static final List<String> OPERACOES_CONSULTAR_CONSIGNACAO = Arrays.asList(
            OP_CONSULTAR_CONSIGNACAO,
            OP_CONSULTAR_CONSIGNACAO_V6_0,
            OP_CONSULTAR_CONSIGNACAO_V8_0
    );

    public static final List<String> OPERACOES_CONSULTAR_MARGEM = Arrays.asList(
            OP_CONSULTAR_MARGEM,
            OP_CONSULTAR_MARGEM_V3_0,
            OP_CONSULTAR_MARGEM_V6_0,
            OP_CONSULTAR_MARGEM_V7_0,
            OP_CONSULTAR_MARGEM_V8_0
    );

    public static final List<String> OPERACOES_INSERIR_SOLICITACAO = Arrays.asList(
            OP_INSERIR_SOLICITACAO,
            OP_INSERIR_SOLICITACAO_V6_0,
            OP_INSERIR_SOLICITACAO_V8_0
    );

    public static final List<String> OPERACOES_LIQUIDAR_CONSIGNACAO = Arrays.asList(
            OP_LIQUIDAR_CONSIGNACAO,
            OP_LIQUIDAR_CONSIGNACAO_V6_0
    );

    public static final List<String> OPERACOES_REATIVAR_CONSIGNACAO = Arrays.asList(
            OP_REATIVAR_CONSIGNACAO,
            OP_REATIVAR_CONSIGNACAO_V6_0,
            OP_REATIVAR_CONSIGNACAO_V8_0
    );

    public static final List<String> OPERACOES_RENEGOCIAR_CONSIGNACAO = Arrays.asList(
            OP_RENEGOCIAR_CONSIGNACAO,
            OP_RENEGOCIAR_CONSIGNACAO_V6_0
    );

    public static final List<String> OPERACOES_SUSPENDER_CONSIGNACAO = Arrays.asList(
            OP_SUSPENDER_CONSIGNACAO,
            OP_SUSPENDER_CONSIGNACAO_V6_0
    );

    public static final List<String> OPERACOES_DETALHAR_ADE = Arrays.asList(
            OP_DETALHAR_CONSULTA_ADE,
            OP_DETALHAR_CONSULTA_ADE_V4_0,
            OP_DETALHAR_CONSULTA_ADE_V6_0,
            OP_DETALHAR_CONSULTA_ADE_V8_0
    );

    public static final List<String> OPERACOES_ALTERAR_CONSIGNACAO = Arrays.asList(
            OP_ALTERAR_CONSIGNACAO,
            OP_ALTERAR_CONSIGNACAO_V6_0
    );

    public static final List<String> OPERACOES_RESERVAR_MARGEM = Arrays.asList(
            OP_RESERVAR_MARGEM,
            OP_RESERVAR_MARGEM_V6_0,
            OP_RESERVAR_MARGEM_V8_0
    );

    public static final List<String> OPERACOES_ALONGAR_CONSIGNACAO = Arrays.asList(
            OP_ALONGAR_CONSIGNACAO,
            OP_ALONGAR_CONSIGNACAO_V6_0
    );

    public static final List<String> OPERACOES_PESQUISAR_SERVIDOR = Arrays.asList(
            OP_PESQUISAR_SERVIDOR,
            OP_PESQUISAR_SERVIDOR_V8_0
    );

    public static final List<String> OPERACOES_VERIFICA_INCL_MENSAGEM_VINCULO = Arrays.asList(
            OP_CONSULTAR_MARGEM,
            OP_CONSULTAR_MARGEM_V3_0,
            OP_CONSULTAR_MARGEM_V6_0,
            OP_CONSULTAR_MARGEM_V7_0,
            OP_CONSULTAR_MARGEM_V8_0,
            OP_RESERVAR_MARGEM,
            OP_RESERVAR_MARGEM_V6_0,
            OP_RESERVAR_MARGEM_V8_0,
            OP_RENEGOCIAR_CONSIGNACAO,
            OP_RENEGOCIAR_CONSIGNACAO_V6_0,
            OP_COMPRAR_CONTRATO
    );

    // Constantes com os papéis disponíveis no sistema
    public static final String PAP_CONSIGNANTE    = "1";
    public static final String PAP_CONSIGNATARIA  = "2";
    public static final String PAP_ORGAO          = "3";
    public static final String PAP_CORRESPONDENTE = "4";
    public static final String PAP_ESTABELECIMENTO= "5";
    public static final String PAP_SERVIDOR       = "6";
    public static final String PAP_SUPORTE        = "7";

    // Constantes que definem periodicidade de envio de e-mails de auditoria
    public static final String PER_ENV_EMAIL_AUDIT_DIARIO       = "D";
    public static final String PER_ENV_EMAIL_AUDIT_SEMANAL      = "S";
    public static final String PER_ENV_EMAIL_AUDIT_MENSAL       = "M";
    public static final String PER_ENV_EMAIL_AUDIT_DESABILITADO = "X";

    // Base de cálculo padrão: usa o campo rseBaseCalculo
    public static final String TBC_PADRAO = "1";

    // Asssunto Comunicação Refinanciamento de Proposta
    public static final String ASSUNTO_REFINANCIAMENTO_PROPOSTA = "REFPAR";

    // método da requisição permitida ao recurso
    public static final String METODO_GET_POST = "0";
    public static final String METODO_GET      = "1";
    public static final String METODO_POST     = "2";

    // Tamanho do OTP
    public static final Short TAM_OTP = 6;
    public static final String ENVIA_OTP_DESABILITADO = "0";
    public static final String ENVIA_OTP_SMS = "1";
    public static final String ENVIA_OTP_EMAIL = "2";
    public static final String ENVIA_OTP_SMS_OU_EMAIL = "3";

    // Constantes com os menus disponíveis no sistema
    public static final String MENU_OPERACIONAL    = "1";
    public static final String MENU_RELATORIOS     = "2";
    public static final String MENU_MANUTENCOES    = "3";
    public static final String MENU_SISTEMA        = "4";

    // Constantes com os templates padronizados para gerar Relatório Editável
    public static final String TEMPLATE_REL_EDITAVEL_JASPER = "Editavel.jasper";
    public static final String TEMPLATE_REL_EDITAVEL_JRXML  = "Editavel_model.jrxml";

    // Constantes de limite da data dos arquivos de relatório de integração de CSA a serem verificados
    public static final int LIMITE_DIAS_DATA_RELAT_INTEGRACAO_A_VERIFICAR = 5;

    // Chave de parâmetros de inclusão avançada de contrato
    public static final String PARAM_INC_AVANCADA_ADE = "parametros_inc_avancada";
    public static final String PARAM_INC_AVANCADA_VALIDA_MARGEM = "valida_margem";
    public static final String PARAM_INC_AVANCADA_VALIDA_TAXA_JUROS = "valida_taxa_juros";
    public static final String PARAM_INC_AVANCADA_VALIDA_PRAZO = "valida_prazo";
    public static final String PARAM_INC_AVANCADA_VALIDA_DADOS_BANCARIOS = "valida_dados_bancarios";
    public static final String PARAM_INC_AVANCADA_VALIDA_SENHA_SERVIDOR = "valida_senha_servidor";
    public static final String PARAM_INC_AVANCADA_VALIDA_BLOQ_SER_CNV_CSA = "valida_bloq_ser_cnv_csa";
    public static final String PARAM_INC_AVANCADA_VALIDA_DATA_NASCIMENTO = "valida_data_nasc";
    public static final String PARAM_INC_AVANCADA_VALIDA_LIMITE_ADE = "valida_limite_ade";

    // Status de controle de processamento de lote via SOAP
    public static final short CPL_PENDENTE_UPLOAD = 0;
    public static final short CPL_UPLOAD_SUCESSO = 1;
    public static final short CPL_UPLOAD_FALHA = 2;
    public static final short CPL_PROCESSANDO = 3;
    public static final short CPL_PROCESSADO_SUCESSO = 4;
    public static final short CPL_PROCESSADO_FALHA = 5;
    public static final short CPL_FALHA = 6;
    public static final short CPL_RESULTADO_ENTREGUE = 7;

    // Métodos de Cálculo de Simulação
    public static final String MCS_BRASILEIRO = "BR";
    public static final String MCS_MEXICANO = "MX";
    public static final String MCS_INDIANO = "IN";

    // Capacidade Civil de Servidor
    public static final String CAP_PLENAMENTE = "1";
    public static final String CAP_CURATELADO = "2";
    public static final String CAP_TUTELADO = "3";

    public static final int TEMPO_DEFAULT_EXPIRACAO_SESSAO = 20;
    public static final int TEMPO_MAXIMO_EXPIRACAO_SESSAO = 40;
    public static final int TEMPO_DEFAULT_ATUALIZACAO_MARGEM_SESSAO_SER = 60;

    // Tipo período
    public static final String TIPO_PERIODO_INCLUSAO = "I";
    public static final String TIPO_PERIODO_ALTERACAO_MAIOR = "A+";
    public static final String TIPO_PERIODO_ALTERACAO_MENOR = "A-";
    public static final String TIPO_PERIODO_EXCLUSAO = "E";

    // Arquivos de configuração de importação
    public static final String ARQ_CONF_ENTRADA_DESLIGADO_BLOQUEADO = "imp_desligado_entrada.xml";
    public static final String ARQ_CONF_TRADUTOR_DESLIGADO_BLOQUEADO = "imp_desligado_tradutor.xml";

    /* Tipo Endereços */
    public static final String TIE_OUTRO = "1";
    public static final String TIE_RESIDENCIAL = "2";
    public static final String TIE_COMERCIAL = "3";
    public static final String TIE_FISCAL = "4";
    public static final String TIE_COBRANCA = "5";

    /* Tipo de Beneficiario */
    public static final String TIB_TITULAR = "1";
    public static final String TIB_DEPENDENTE = "2";
    public static final String TIB_AGREGADO = "3";

    /* Opções de código natureza consignataria*/
    public static final String NCA_CODIGO_OPERADORA_BENEFICIOS = "10";

    /* Status do Contrato Beneficio */
    public static final String SCB_CODIGO_SOLICITADO                           = "1";
    public static final String SCB_CODIGO_AGUARDANDO_INCLUSAO_OPERADORA        = "2";
    public static final String SCB_CODIGO_ATIVO                                = "3";
    public static final String SCB_CODIGO_CANCELAMENTO_SOLICITADO              = "4";
    public static final String SCB_CODIGO_AGUARDANDO_EXCLUSAO_OPERADORA        = "5";
    public static final String SCB_CODIGO_CANCELADO                            = "6";
    public static final String SCB_CODIGO_CANCELAMENTO_SOLICITADO_BENEFICIARIO = "7";

    public static final char HIB_TIPO_EXPORTACAO   = 'E';
    public static final char HIB_TIPO_REEXPORTACAO = 'X';
    public static final char HIB_TIPO_RETORNO      = 'R';

    /* STATUS REGRA para Edição de Regra de Taxa de Juros */
    public static final String REGRA_NOVA_TABELA_INICIADA = "1";
    public static final String REGRA_TABELA_ATIVA = "2";
    public static final String REGRA_TABELA_VIGENCIA_EXPIRADA = "3";

    public static final String OAUTH2_STATE_KEYPAIR_ATTIBUTE_NAME = "OAUTH2_STATE_KEYPAIR";
    public static final String OAUTH2_STATE_TOKEN_ATTIBUTE_NAME   = "OAUTH2_STATE_TOKEN";
    public static final String OAUTH2_ACAO_ATTIBUTE_NAME          = "OAUTH2_ACAO";
    public static final String OAUTH2_ACAO_LOGIN                  = "OAUTH2_ACAO_LOGIN";
    public static final String OAUTH2_ACAO_AUTORIZAR_OPERACAO     = "OAUTH2_ACAO_AUTORIZAR_OPERACAO";
    public static final String OAUTH2_ID_TOKEN                    = "OAUTH2_ID_TOKEN";

    public static final String CERT_PUBLIC_KEY                    = "CERT_PUBLIC_KEY";
    public static final String CERT_STATE                         = "state";
    public static final String CERT_TOKEN                         = "token";
    public static final String CERT_USU_CODIGO                    = "CERT_USU_CODIGO";
    public static final String CERT_DATA                          = "CERT_DATA";
    public static final String CERT_IP_ACESSO                     = "CERT_IP_ACESSO";

    /* Opções de decisão judicial */
    public static final String DECISAO_JUDICIAL_OPCAO_PENSAO_JUDICIAL      = "1";
    public static final String DECISAO_JUDICIAL_OPCAO_EXCLUIR_CONSIGNACAO  = "2";
    public static final String DECISAO_JUDICIAL_OPCAO_ADEQUACAO_MARGEM     = "3";
    public static final String DECISAO_JUDICIAL_OPCAO_ALTERAR_CONSIGNACAO  = "4";
    public static final String DECISAO_JUDICIAL_OPCAO_REATIVAR_CONSIGNACAO = "5";
    public static final String DECISAO_JUDICIAL_OPCAO_AUTORIZAR_CONSIGNACAO = "6";

    public static final String DECISAO_JUDICIAL_OPCAO_INCLUIR_CONSIGNACAO = "7";

    /* Valor limite para o prazo, acima ou igual a este valor será indeterminado */
    public static final int VLR_ADE_PRAZO_INDETERMINADO = 999;

    /* Item de regra de inconsistência que valida incidência de margem */
    public static final Short IIA_ITEM_INCONSISTENCIA_INCIDENCIA_MARGEM = 10;

    /* Classificacao Beneficiarios */
    public static final String BFC_CLASSIFICACAO_LEGAL                         = "L";
    public static final String BFC_CLASSIFICACAO_DECISAO_JUDICIAL              = "D";
    public static final String BFC_CLASSIFICACAO_ECONOMICO                     = "E";
    public static final String BFC_CLASSIFICACAO_ESPECIAL                      = "P";

    public static final List<String> BFC_CLASSIFICACOES = Arrays.asList(
            BFC_CLASSIFICACAO_LEGAL,
            BFC_CLASSIFICACAO_DECISAO_JUDICIAL,
            BFC_CLASSIFICACAO_ECONOMICO,
            BFC_CLASSIFICACAO_ESPECIAL
    );

    //Utilizado no filtro de serviço na listagem de serviços de consignatará
    public static final String SERVICO_TEM_ADE = "TEMADE";

    /*Indica se consignação possui decisão judicial*/
    public static final String DECISAO_JUDICIAL_SIM                         = "S";
    public static final String DECISAO_JUDICIAL_NAO                         = "N";

    /* Status de Pagamento para endpoint de conciliação */
    public static final String TODAS_PARCELAS                               = "1";
    public static final String TODAS_PARCELAS_PAGAS                         = "2";
    public static final String TODAS_PARCELAS_PAGAS_INTEGRALMENTE           = "3";
    public static final String TODAS_PARCELAS_PAGAS_PARCIALMENTE            = "4";
    public static final String TODAS_PARCELAS_REJEITADAS                    = "5";

    /* Status de Pagamento aceitos no endpoint de conciliação */
    public static final List<String> STATUS_PAGAMENTO = Arrays.asList(
            TODAS_PARCELAS,
            TODAS_PARCELAS_PAGAS,
            TODAS_PARCELAS_PAGAS_INTEGRALMENTE,
            TODAS_PARCELAS_PAGAS_PARCIALMENTE,
            TODAS_PARCELAS_REJEITADAS
    );

    //Opção para como será o arquivo na exportação.
    public static final String EXPORTA_ARQUIVO_POR_ENTIDADE = "2";

    //Número minimo de anexos para validação de documentos
    public static final int NUM_MIN_ANEXOS_VALIDACAO_PERIODO = 3;

   //Flag para determinar se irá filtrar os servidores por consignatária ou verba
    public static final String ENVIA_CSA_CONTRATOS_SERVIDOR     = "ENVIA_CSA_CONTRATOS_SERVIDOR";
    public static final String ENVIA_CSA_CNV_CONTRATOS_SERVIDOR = "ENVIA_CSA_CNV_CONTRATOS_SERVIDOR";

    public static final String DAD_VALOR_ = "dad_valor_";

    /*Indica se deve validar autorização solicitando senha ou se deve continua o fluxo*/
    public static final String VALIDA_AUTORIZACAO_SOLICITA_SENHA_SIM = "S";
    public static final String VALIDA_AUTORIZACAO_SOLICITA_SENHA_CONTINUA = "C";
    public static final String VALIDA_AUTORIZACAO_SOLICITA_SENHA_ALERTA = "A";

    /* Valores para o parâmetro TPC_EMAIL_NOTIFICACAOO_RESERVA_MARGEM */
    public static final String EMAIL_NOTIFICACAOO_RESERVA_MARGEM_DESABILITADO                = "0";
    public static final String EMAIL_NOTIFICACAOO_RESERVA_MARGEM_SMS                         = "1";
    public static final String EMAIL_NOTIFICACAOO_RESERVA_MARGEM_EMAIL                       = "2";
    public static final String EMAIL_NOTIFICACAOO_RESERVA_MARGEM_SMS_EMAIL                   = "3";
    public static final String EMAIL_NOTIFICACAOO_RESERVA_MARGEM_PUSH_NOTIFICATION           = "4";
    public static final String EMAIL_NOTIFICACAOO_RESERVA_MARGEM_SMS_EMAIL_PUSH_NOTIFICATION = "5";
    public static final String EMAIL_NOTIFICACAOO_RESERVA_MARGEM_EMAIL_PUSH_NOTIFICATION     = "6";

    // Constantes para desbloqueio automático de consignatária por saldo devedor de rescisão não ser informado
    public static final String PODE_DESBLOQUEAR_CSA = "pode_desbloquear";
    public static final String IS_CSA_COM_ADE_COM_RESCISAO = "is_rescisao";

    public static final String PERIODO_CARTAO_CONFIGURAVEL = "PERIODO_CARTAO_CONFIGURAVEL";

    /*Indica confirmação de termo de adesão pelo usuário*/
    public static final String TERMO_ADESAO_CONFIRMADO   = "1";
    public static final String TERMO_ADESAO_RECUSADO     = "2";
    public static final String TERMO_ADESAO_LER_DEPOIS   = "3";

    public static final String REGRAS_CONVENIO_NOME = "1";
    public static final String REGRAS_CONVENIO_CNPJ = "2";
    public static final String REGRAS_CONVENIO_ENDERECO = "3";
    public static final String REGRAS_CONVENIO_TOTAL_SERVIDORES = "4";
    public static final String REGRAS_CONVENIO_LINK_SISTEMA = "5";
    public static final String REGRAS_CONVENIO_DIA_REPASSE = "6";
    public static final String REGRAS_CONVENIO_DATA_CORTE = "7";
    public static final String REGRAS_CONVENIO_TIPO_MOVIMENTO_FINANCEIRO = "8";
    public static final String REGRAS_CONVENIO_TIPO_MARGEM = "9";
    public static final String REGRAS_CONVENIO_PROCESSAMENTO_FERIAS = "10";
    public static final String REGRAS_CONVENIO_REIMPLANTE = "11";
    public static final String REGRAS_CONVENIO_CONCLUIDO_PRAZO_FINAL = "12";
    public static final String REGRAS_CONVENIO_REIMPLANTE_CSA_OPTA = "13";
    public static final String REGRAS_CONVENIO_VALOR_MIN_PARCELA = "14";
    public static final String REGRAS_CONVENIO_MARGEM_CASADA = "15";
    public static final String REGRAS_CONVENIO_PERMITE_COMPRA = "16";
    public static final String REGRAS_CONVENIO_EXIGE_CERTIFICADO_CSA = "17";
    public static final String REGRAS_CONVENIO_EXIGE_CERTIFICADO_CSE = "18";
    public static final String REGRAS_CONVENIO_PORTAL_SER = "19";
    public static final String REGRAS_CONVENIO_EXIGE_SENHA_SER_CSA = "20";
    public static final String REGRAS_CONVENIO_EXIGE_SENHA_SER_CSE = "21";
    public static final String REGRAS_CONVENIO_FORMATO_ACESSO_SER = "22";
    public static final String REGRAS_CONVENIO_MODULO_RESCISAO = "23";
    public static final String REGRAS_CONVENIO_MODULO_RESCISAO_SALDO_DEVEDOR = "24";
    public static final String REGRAS_CONVENIO_BLOQUEIO_INATIVIDADE = "25";
    public static final String REGRAS_CONVENIO_RESTRICAO_IP = "26";
    public static final String REGRAS_CONVENIO_CERTIFICADO_DIGITAL = "27";
    public static final String REGRAS_CONVENIO_MATRICULA_NUMERICA = "28";
    public static final String REGRAS_CONVENIO_MATRICULA_QUANTIDADE_MAXIMA = "29";
    public static final String REGRAS_CONVENIO_ORG_CNPJ = "30";
    public static final String REGRAS_CONVENIO_ORG_QUANTIDADE_SERVIDORES = "31";
    public static final String REGRAS_CONVENIO_MAR_PORCENTAGEM = "32";
    public static final String REGRAS_CONVENIO_MAR_EXIBE_MARGEM_NEGATIVA_CSA = "33";
    public static final String REGRAS_CONVENIO_SALARY_PAY = "34";
    public static final String REGRAS_CONVENIO_ACESSA_API = "35";


    public static final String SCT_NAO_PROCESSADO = "1";
    public static final String SCT_PROCESSADO_SUCESSO = "2";
    public static final String SCT_ERRO_RESERVA_MARGEM = "3";
    public static final String SCT_CONTRATO_MIGRADO = "4";
    public static final String SCT_CONVENIO_NAO_ENCONTRADO = "5";
    public static final String SCT_MULTIPLOS_CONVENIOS_ENCONTRADOS = "6";
    public static final String SCT_CSA_NAO_EXISTE = "7";
    public static final String SCT_MULTIPLAS_CSAS = "8";
    public static final String SCT_NENHUM_SERVIDOR = "9";
    public static final String SCT_MULTIPLOS_SERVIDORES = "10";
    public static final String SCT_ERRO_MIGRACAO_CONTRATO = "11";
    public static final Short CONSULTA_DASHBOARD_BI_URL = 1;
    public static final Short CONSULTA_DASHBOARD_BI_ARQUIVO = 0;

    public static final String SAL_FOLHA            = "FOLHA";
    public static final String SAL_CALCULADO        = "CALCULADO";
    public static final String SAL_NAO_CALCULAVEL   = "NAO_CALCULADO";

    public static final String CHAVE_CALC_MARGEM_PORTAL_SER_SALARIO                = "salarioRse";
    public static final String CHAVE_CALC_MARGEM_PORTAL_MAR_DESCRICAO              = "margemDescricao";
    public static final String CHAVE_CALC_MARGEM_PORTAL_MAR_PORCENTAGEM            = "marPorcentagem";
    public static final String CHAVE_CALC_MARGEM_PORTAL_CALC_PORCENTAGEM           = "calculoPorcentagem";
    public static final String CHAVE_CALC_MARGEM_PORTAL_TOTAL_CONSIGNACOES_ATIVAS  = "totalConsignacoesAtivas";
    public static final String CHAVE_CALC_MARGEM_PORTAL_VALOR_FINAL_MARGEM         = "valorFinalMargem";
    public static final String CHAVE_CALC_MARGEM_PORTAL_VALOR_COMPULSORIO          = "valorCompulsorio";
    public static final String CHAVE_CALC_MARGEM_PORTAL_VALOR_MAIOR_C		       = "valorMaiorC";

    public static final String PER_SIM = "S";
    public static final String PER_NAO = "N";

    public static final String FILTRO_OCE_DATA_INI                                 = "oceDataIni";
    public static final String FILTRO_OCE_DATA_FIM                                 = "oceDataFim";

}
