package com.zetra.econsig.values;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: AcaoEnum</p>
 * <p>Description: Enumeração do ações.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum AcaoEnum {

    RETER_VALOR_REVISAO_MARGEM("1"),
    LIBERAR_VALOR_RETIDO_REVISAO_MARGEM("2"),
    CANCELAMENTO_BEN_MOTIVO_PERDA_CONDICAO_DEPENDENTE("3"),
    CANCELAR_CONTRATO_BENEFICIO_POR_INADIPLENCIA("4"),
    CANCELAMENTO_BENEFICIO_POR_MIGRACAO_BENEFICIO("5"),
    CANCELAMENTO_BENEFICIO_PELO_SERVIDOR("6"),
    CANCELAMENTO_BENEFICIO_OBITO("7"),
    BLOQUEAR_SERVIDOR("8"),
    EXCLUIR_SERVIDOR("9"),
    REGISTRAR_FALECIMENTO_SERVIDOR("10"),
    SUSPENDER_CONTRATO_PARCELA_REJEITADA("11");

    private String codigo;

    private AcaoEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public static AcaoEnum recuperaAcao(String codigo) {
        AcaoEnum acao = null;

        for (AcaoEnum aca : AcaoEnum.values()) {
            if (aca.getCodigo().equals(codigo)) {
                acao = aca;
                break;
            }
        }

        if (acao == null) {
            throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.informado.acao.invalido", (AcessoSistema) null));
        }

        return acao;
    }

    public final boolean equals(AcaoEnum other) {
        return (this==other || getCodigo().equals(other.getCodigo()));
    }
}
