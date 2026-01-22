package com.zetra.econsig.values;

/**
 * <p>Title: ModeloEmailEnum</p>
 * <p>Description: Enumeração para seleção do modelo de email.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum ModeloEmailEnum {

    // 7.1) Método "EnviaEmailHelper.enviarEmailNotificacaoCadastroServidor"
    ENVIAR_EMAIL_NOTIFICACAO_CADASTRO_SERVIDOR("emailNotificacaoCadastroSer"),
    // 7.2) Método "EnviaEmailHelper.enviarEmailCsasAlteracaoSer"
    ENVIAR_EMAIL_CSAS_ALTERACAO_SER("enviarEmailCsasAlteracaoSer"),
    // 7.3) Método "EnviaEmailHelper.enviarEmailAlertaEnvioArquivosFolha"
    ENVIAR_EMAIL_ALERTA_ENVIO_ARQUIVOS_FOLHA_ANTES("emailAlertaEnvioArqFolha_antes"),
    ENVIAR_EMAIL_ALERTA_ENVIO_ARQUIVOS_FOLHA_DEPOIS("emailAlertaEnvioArqFolha_depois"),
    // 7.4) Método "EnviaEmailHelper.enviarEmailAlertaEnvioArquivosFolha"
    ENVIAR_EMAIL_ALERTA_ENVIO_ARQUIVOS_FOLHA_ANTES_CSA("emailAlertEnvArqFolha_antes_csa"),
    ENVIAR_EMAIL_ALERTA_ENVIO_ARQUIVOS_FOLHA_DEPOIS_CSA("emailAlertEnvArqFolha_depois_csa"),
    // 7.5) Método "EnviaEmailHelper.enviarEmailUploadArquivoCsa"
    ENVIAR_EMAIL_UPLOAD_ARQUIVO_CSA("enviarEmailUploadArquivoCsa"),
    // 7.6) Método "EnviaEmailHelper.enviarEmailAlertaRetornoServidor"
    ENVIAR_EMAIL_ALERTA_RETORNO_SERVIDOR("enviarEmailAlertaRetornoServidor"),
    // 7.7) Método "EnviaEmailHelper.enviaEmailNotificacaoCsaCancelamentoCadastroServidor"
    ENVIA_EMAIL_NOTIFICACAO_CSA_CANCELAMENTO_CADASTRO_SERVIDOR("emailNotificaCsaCancelCadSer"),
	// 7.8) Método "EnviaEmailHelper.enviaEmailServidorContratosRejeitados"
    ENVIA_EMAIL_SERVIDOR_CONTRATOS_REJEITADOS("emailServidorContratosRejeitados"),
	// 7.9) Método "EnviaEmailHelper.enviaNotificacaoBloqueioUsuarioInatividade"
    ENVIA_EMAIL_BLOQUEIO_USUARIO_INATIVIDADE("enviarNotifUsuBloqInatividade"),
    // 8.0) Método "EnviaEmailHelper.enviaEmailNotificacaoConsignacaoDeferida"
    ENVIA_EMAIL_CONSIGNACAO_DEFERIDA("emailNotificacaoConsignacaoDef"),
    //10.0)Método "EnviaEmailHelper.enviaNotifAlterCodVerbaConvCSA"
    ENVIAR_EMAIL_NOTIF_ALTER_CODVERBA_CONVENIO_CSA("enviaNotifAlterCodVerbaConvCSA"),
    // DESENV-10999 "EnviaEmailHelper.enviarEmailCsaSolicitacaoFeitaPorSer"
    ENVIA_EMAIL_CSA_NOVA_SOLICITACAO_DO_SERVIDOR("emailNovaSolicitacaoFeitaPeloSer"),
    // DESENV-10999 "EnviaEmailHelper.enviarEmailCsaSolicitacaoCanceladaPorSer"
    ENVIA_EMAIL_CSA_SOLICITACAO_CANCELADA_POR_SERVIDOR("soliciCanceladaPorSerSemMargem"),
    // DESENV-11562 "EnviaEmailHelper.enviarEmailDownloadNaoRealizadoMovFin"
    ENVIA_EMAIL_DOWNLOAD_NAO_REALIZADO_MOV_FIN("emailDownloadNaoRealizadoMovFin"),
    // DESENV-11699 "EnviaEmailHelper.enviarEmailNovoBoletoServidor"
    ENVIA_EMAIL_NOVO_BOLETO_SERVIDOR("emailNovoBoletoServidor"),
    // DESENV-12087 "EnviaEmailHelper.enviarLinkRecuperacaoSenhaServidor"
    ENVIA_EMAIL_LINK_RECUPERACAO_SENHA_SERVIDOR("emailRecuperacaoSenhaServidor"),
    ENVIAR_EMAIL_CONFIRMACAO_OPERACAO_SENSIVEL("emailConfirmacaoOperacaoSensivel"),
    ENVIAR_EMAIL_REPROVACAO_OPERACAO_SENSIVEL("emailReprovacaoOperacaoSensivel"),
    // DESENV-13591 "EnviaEmailHelper.enviaEmailNotificacaoCsaErroKYC"
    ENVIAR_EMAIL_NOTIFICACAO_CSA_ERRO_KYC("notificaProblemasProcessoKYC"),
    // DESENV-13856 "Alterar o e-mail enviado na inclusão de novo usuário para usar a tabela de modelo de e-mail"
    ENVIA_EMAIL_LINK_DEFINIR_SENHA_NOVO_USUARIO("emailDefinirSenhaNovoUsuario"),
    // DESENV-13961 "SalaryPay - Alterar email de envio de OTP - eConsig"
    ENVIAR_EMAIL_OTP_SERVIDOR("enviarEmailOtpServidor"),
    ENVIAR_EMAIL_OTP_USUARIO("enviarEmailOtpUsuario"),
    ENVIAR_EMAIL_EXPIRACAO_PARA_CSA("enviarEmailExpiracaoParaCsa"),
    ENVIAR_EMAIL_EXPIRACAO_PARA_CSA_TEXTO("enviarEmailExpiracaoParaCsaTexto"),
    ENVIAR_EMAIL_EXPIRACAO_CSA("enviarEmailExpiracaoCsa"),
    ENVIAR_EMAIL_EXPIRACAO_CSA_TEXTO("enviarEmailExpiracaoCsaTexto"),
    ENVIAR_EMAIL_ALERTA_CRIACAO_NOVO_USU_CSE("enviarEmailAlertaCriaNovoUsuCse"),
    //DESENV-17251 "envio de e-mail mensal a consignatárias configuradas com validade da taxa de juros na criação destas"
    ENVIAR_EMAIL_MENSAL_ATUALIZAR_TAXAS("enviarEmailMensalAtualizarTaxas"),
    ENVIAR_EMAIL_DIARIO_EXPIRACAO_TAXAS_7_DIAS("enviarEmailExpiraTaxasEm7dias"),
    ENVIAR_EMAIL_DIARIO_EXPIRACAO_TAXAS_HOJE("enviarEmailExpiraTaxasHoje"),
    ENVIAR_EMAIL_DIARIO_TAXAS_NAO_ATUALIZADAS("enviarEmailTaxasNaoAtualizadas"),
    ENVIAR_EMAIL_DIARIO_TAXAS_DESBLOQUEADAS("enviarEmailTaxasDesbloqueadas"),
    ENVIAR_EMAIL_SERVIDOR_CONTRATOS_SUSPENSOS_PENDENTES_REATIVACAO("enviarEmailSerAdeSuspPendReat"),
    ENVIAR_EMAIL_CSA_CONTRATOS_COLOCADOS_EM_ESTOQUE("enviarEmailCsaAdeStatusEmEstoque"),
    ENVIAR_EMAIL_NOTIFICACAO_CSE_REATIVACAO_PRD_REJEITADA("enviarEmailCseReativacaoAde"),
    ENVIAR_EMAIL_NOTIFICACAO_CSA_CREDENCIAMENTO("enviarEmailCsaCredenciamento"),
    ENVIAR_EMAIL_CONFIRMACAO_DESBLOQUEIO_CSA("enviarEmailConfirmacaoDesbloqCSA"),
    ENVIAR_EMAIL_NOTIFICACAO_DESBLOQUEIO_CSA("enviarEmailNotDesbloqCSA"),
    ENVIAR_EMAIL_NOTIFICACAO_CSE_CREDENCIAMENTO("enviarEmailCseCredenciamento"),
    ENVIAR_EMAIL_NOTIFICACAO_SITUACAO_CREDENCIAMENTO_CSA("enviarEmailSitCredenciamentoCsa"),
    ENVIAR_EMAIL_NOTIFICACAO_TERMO_PREENCHIDO_CREDENCIAMENTO_CSA("enviarEmailTermoPreenchidoCse"),
    ENVIAR_EMAIL_NOTIFICACAO_TERMO_ASS_CREDENCIAMENTO_CSA("enviarEmailTermoAssCsa"),
    ENVIAR_EMAIL_NOTIFICACAO_TERMO_ASS_CREDENCIAMENTO_CSE("enviarEmailTermoAssCse"),
    ENVIAR_EMAIL_NOTIFICACAO_CREDENCIAMENTO_CONCLUIDO_CSE("enviarEmailCredConclCse"),
    ENVIAR_EMAIL_NOTIFICACAO_CREDENCIAMENTO_CONCLUIDO_CSA("enviarEmailCredConclCsa"),
    //DESENV-18086 - eNomina - Enviar e-mail ao final do fluxo de auto cadastro do usuário servidor
    ENVIAR_EMAIL_NOTIFICACAO_CADASTRO_SENHA_SERVIDOR("emailCadastroSenhaServidor"),
    ENVIAR_EMAIL_NOTIFICACAO_DOC_CREDENCIAMENTO_CSA("enviarEmailDocCredenciamentoCsa"),
    ENVIAR_EMAIL_BLOQ_DESBLOQ_SERVIDOR_CSA("enviarEmailBloqDesbloqSerCsa"),
    // DESENV-19621 : LICIT-4846: PM Ouro Preto - Revisão de acessos
    ENVIAR_EMAIL_ALTERACAO_PERFIL_USUARIO("enviarEmailAlteracaoPerfilUsu"),
    ENVIAR_EMAIL_SER_SALDO_INSUF_VERBA_RESCISORIA("enviarEmailSerVerbaRescisoria"),
    ENVIAR_EMAIL_NOTIFICACAO_NOVO_VINCULO("enviarEmailCsaNovoVinculo"),
    ENVIAR_EMAIL_NOTIFICACAO_ERRO_CRIAR_ARQ_MARGEM("enviarEmailErroCriarArqMargem"),
    ENVIAR_EMAIL_NOTIFICACAO_BLOQ_SER_VARIACAO_MARGEM_LIMITE_CSA("enviarEmailBloqSerLimCsa"),
    ENVIAR_EMAIL_NOTIFICACAO_CSA_BLOQ_SER_VARIACAO_MARGEM_LIMITE_CSA("enviarEmailCsaBloqSerCnv"),
    ENVIAR_EMAIL_OFERTA_REFINANCIANENTO_CSA("enviarEmailRefinanciamentoCsa"),
    EMAIL_CONSULTA_MARGEM_SERVIDOR("enviarEmailConsultaMargemSer"),
    ENVIAR_EMAIL_NOVO_CONTRATO_VERBA_RESCISORIA_SER("enviarEmailSerAdeVerbaRescisoria"),
    ENVIAR_EMAIL_NOTIFICACAO_RESERVA_MARGEM("enviarEmailNotReservaMargem"),
    ENVIAR_EMAIL_NOTIFICACAO_AUTORIZACAO_IRA_VENCER("enviarEmailAutorizacaoIraVencer"),
    ENVIAR_EMAIL_CSA_DESBLOQUEIO_VERBA("enviarEmailCsaDesbloqueioVerba"),
    ENVIAR_EMAIL_NOTIFICACAO_VINCULOS_BLOQ_DESBLOQ("enviarEmailVinculosBloqDesbloq"),
    ENVIAR_EMAIL_NOTIFICACAO_CSE_BLOQUEIO_CONSIGNATARIA("enviarEmailCseBloqCsa"),
    ENVIAR_EMAIL_NOTIFICACAO_PORTABILIDADE_CARTAO("enviarEmailPortabilidadeCartao"),
    ENVIA_EMAIL_SIMULACAO_CONSIGNACAO("enviarEmailSimulacaoConsignacao"),
    ENVIAR_EMAIL_LIMITE_ATINGIDO_CSA("enviarEmailLimiteAtingidoCsa"),
    ENVIAR_EMAIL_NOTIFICACAO_PRAZO_EXPIRACAO_SENHA("enviarEmailPrazoExpiracaoSenha"),
    ENVIAR_EMAIL_CSE_BLOQUEIO_USUARIO("enviarEmailCseBloqueioUsuario"),
    ENVIA_EMAIL_SENHA_NOVO_USUARIO("emailSenhaNovoUsuario"),
    ENVIAR_EMAIL_CSA_NOTIFICACAO_ALTERACAO_REGRAS_CONVENIO("enviarEmailCsaNotificacaoRegras")
    ;

    private final String codigo;

    private ModeloEmailEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
