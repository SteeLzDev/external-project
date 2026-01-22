package com.zetra.econsig.values;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: TipoMotivoBloqueioEnum</p>
 * <p>Description: Constantes específicas de motivos bloqueio.</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum TipoMotivoBloqueioEnum {
    // Bloqueado manualmente
    BLOQUEIO_MANUAL("0"),
    // Pendência em processo de portabilidade/compra
    PENDENCIA_PROCESSO_PORTABILIDADE("1"),
    // Pendência em informação de saldo solicitado pelo servidor
    PENDENCIA_INFORMACAO_SALDO_DEVEDOR_SERVIDOR("2"),
    // Pendência em comunicação sem resposta
    PENDENCIA_COMUNICACAO_SEM_RESPOSTA("3"),
    // Pendência de liquidação de consignação com saldo devedor pago
    PENDENCIA_LIQUIDACAO_ADE_SALDO_PAGO("4"),
    // Pendência em mensagem sem leitura
    PENDENCIA_MENSAGEM_SEM_LEITURA("5"),
    // Pendência em consignação sem a quantidade mínima de anexos
    PENDENCIA_QTD_MINIMA_ANEXOS_ADE("6"),
    // Bloqueado automaticamente por segurança
    BLOQUEIO_AUTOMATICO_SEGURANCA("7"),
    // Data de expiração passada
    DATA_EXPIRACAO_VENCIDA("8"),
    // Desbloqueio Pendente ao suporte.
    DESBLOQUEIO_PENDENTE_APROVACAO("9"),
    // Bloqueado por CET expirado
    PENDENCIA_CET_EXPIRADO("10"),
    // Pendência por não informar saldo devedor de rescisão
    PENDENCIA_INFO_SALDO_DEVEDOR_RESCISAO("11")
    ;

    private String codigo;

    private TipoMotivoBloqueioEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    /**
     * Recupera um tipo de motivo de bloqueio de acordo com o código passado.
     *
     * @param codigo Código do tipo de motivo de bloqueio que deve ser recuperado.
     * @return Retorna um tipo de motivo de bloqueio
     *
     * @throws IllegalArgumentException Caso o código do tipo de motivo de bloqueio informádo seja inválido
     */
    public static TipoMotivoBloqueioEnum recuperaTipoMotivoBloqueio(String codigo) {
        TipoMotivoBloqueioEnum tipoMotivoBloqueio = null;

        for (TipoMotivoBloqueioEnum tipo : TipoMotivoBloqueioEnum.values()) {
            if (tipo.getCodigo().equals(codigo)) {
                tipoMotivoBloqueio = tipo;
                break;
            }
        }

        if (tipoMotivoBloqueio == null) {
            throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.tipo.motivo.bloqueio.invalido", (AcessoSistema) null));
        }

        return tipoMotivoBloqueio;
    }
}
