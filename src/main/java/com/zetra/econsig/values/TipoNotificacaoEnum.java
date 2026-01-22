package com.zetra.econsig.values;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

public enum TipoNotificacaoEnum {
    NOVA_PROPOSTA_LEILAO("1"),
    EMAIL_CADASTRO_SERVIDOR("2"),
    EMAIL_RETORNO_SERVIDOR("3"),
    EMAIL_CANCELAMENTO_CADASTRO_SERVIDOR("4"),
    EMAIL_NOVA_PROPOSTA_LEILAO("5"),
	EMAIL_CONTRATOS_REJEITADOS_FOLHA("6"),
	PROPOSTA_LEILAO_APROVADA("7"),
	EMAIL_NOTIFICACAO_BLOQUEIO_USUARIO_INATIVIDADE("8"),
	EMAIL_NOTIFICACAO_CONSIGNACAO_DEFERIDA("9"),
    EMAIL_NOTIFICACAO_ALTERACAO_CODVERBA_CONVENIO_CSA("10"),
	EMAIL_NOTIFICACAO_CSA_NOVA_SOLICITACAO_FEITA_POR_SERVIDOR("11"),
	EMAIL_NOTIFICACAO_CSA_SOLICITACAO_CANCELADA_POR_SERVIDOR("12"),
	NOVO_BOLETO_SERVIDOR("13"),
	EMAIL_RECUPERACAO_SENHA_SERVIDOR("14"),
	EMAIL_CONFIRMACAO_OPERACAO_SENSIVEL("15"),
	EMAIL_REPROVACAO_OPERACAO_SENSIVEL("16"),
	EMAIL_NOTIFICACAO_CSA_ERRO_KYC("17"),
	EMAIL_NOTIFICACAO_CRIAR_SENHA_NOVO_USUARIO("18"),
	EMAIL_OTP_SERVIDOR("19"),
	EMAIL_ALERTA_CRIACAO_NOVO_USU_CSE_ORG("20"),
	EMAIL_MENSAL_ALERTA_ATUALIZACAO_CET("21"),
    EMAIL_DIARIO_ALERTA_EXPIRACAO_CET_EM_7_DIAS("22"),
    EMAIL_DIARIO_ALERTA_EXPIRACAO_CET_HOJE("23"),
    EMAIL_DIARIO_ALERTA_CET_NAO_ATUALIZADAS("24"),
    EMAIL_DIARIO_ALERTA_CET_DESBLOQUEADAS("25"),
    EMAIL_SERVIDOR_CONTRATO_PENDENTE_REATIVACAO("26"),
    EMAIL_NOTIFICACAO_CSA_CONTRATO_COLOCADO_EM_ESTOQUE("27"),
    EMAIL_SERVIDOR_CONTRATO_REATIVACAO_PRD_REJEITADA("28"),
    EMAIL_CONSIGNATARIA_CREDENCIAMENTO("29"),
    EMAIL_CONFIRMACAO_DESBLOQUEIO_CSA("30"),
    EMAIL_NOTIFICACAO_DESBLOQUEIO_CSA("31"),
    EMAIL_CONSIGNANTE_CREDENCIAMENTO("32"),
    EMAIL_CADASTRO_SENHA_SERVIDOR("33"),
    EMAIL_BLOQUEIO_DESBLOQUEIO_SERVIDOR_CSA("34"),
	EMAIL_OTP_USUARIO("35"),
	EMAIL_ALTERACAO_PERFIL_USUARIO("36"),
	EMAIL_CONSIGNATARIA_NOVO_VINCULO("37"),
    EMAIL_CONSULTA_MARGEM_SERVIDOR("38"),
    EMAIL_NOTIFICACAO_RESERVA_MARGEM("39"),
    EMAIL_NOTIFICACAO_AUTORIZACAO_IRA_VENCER("40"),
    EMAIL_NOTIFICACAO_VINCULOS_BLOQ_DESBLOQ("41"),
    EMAIL_NOTIFICACAO_DESBLOQUEIO_VERBA_RSE("42"),
    EMAIL_NOTIFICAO_CSE_BLOQUEIO_CONSIGNATARIA("43"),
    EMAIL_NOTIFICACAO_PORTABILIDADE_CARTAO("44"),
    EMAIL_NOTIFICACAO_SIMULACAO_CONSIGNACAO("45"),
    EMAIL_NOTIFICACAO_LIMITE_CONSIGNACAO_DIARIO_CSA("46"),
    EMAIL_NOTIFICACAO_PRAZO_EXPIRACAO_SENHA("47"),
    EMAIL_CSE_BLOQUEIO_USUARIO("48"),
    EMAIL_SENHA_NOVO_USUARIO("49"),
    EMAIL_CSA_NOTIFICACAO_ALTERACAO_REGRAS_CONVENIO("50"),
    PUSH_NOTIFICATION_MENSAGEM_SERVIDOR("51")
	;

    private final String codigo;

    private TipoNotificacaoEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    /**
     * Recupera um tipo de notificação de acordo com o código passado.
     *
     * @param codigo Código do tipo de notificação que deve ser recuperado.
     * @return Retorna um tipo de notificação
     *
     * @throws IllegalArgumentException Caso o código do tipo de notificação informado seja inválido
     */
    public static TipoNotificacaoEnum recuperaTipoNotificacao(String codigo) {
        TipoNotificacaoEnum tipoArquivo = null;

        for (final TipoNotificacaoEnum tipo : TipoNotificacaoEnum.values()) {
            if (tipo.getCodigo().equals(codigo)) {
                tipoArquivo = tipo;
                break;
            }
        }

        if (tipoArquivo == null) {
            throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.tipo.notificacao.invalido", (AcessoSistema) null));
        }

        return tipoArquivo;
    }

}
