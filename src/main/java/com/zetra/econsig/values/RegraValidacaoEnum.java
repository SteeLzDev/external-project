package com.zetra.econsig.values;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: RegraValidacaoEnum</p>
 * <p>Description: Enumeração de regras de validação.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum RegraValidacaoEnum {

    VALIDAR_INNODB_MYSQL("1"),
    VALIDAR_READ_COMMITTED_TX_ISOLATION("2"),
    VALIDAR_VERSAO_JAVA("4"),
    VALIDAR_TRIGGER_STATUS_ADE("5"),
    VALIDAR_ACESSO_ROOT_BD("6"),
    VALIDAR_ACESSO_EXCESSIVO_SISTEMA_ARQUIVOS("7"),
    VALIDAR_PERMISSOES_USUARIOS_SALARYPAY("8");

    private String codigo;

    private RegraValidacaoEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    /**
     * Recupera uma regra de validação de acordo com o código passado.
     * @param codigo Código da regra o que deve ser recuperado.
     * @return Retorna um status de agendamento
     * @throws IllegalArgumentException Caso o código da regra informada seja inválido
     */
    public static RegraValidacaoEnum recuperaRegraValidacao(String codigo) {
        RegraValidacaoEnum regraValidacao = null;

        for (RegraValidacaoEnum regra : RegraValidacaoEnum.values()) {
            if (regra.getCodigo().equals(codigo)) {
                regraValidacao = regra;
                break;
            }
        }

        if (regraValidacao == null) {
            throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.regra.validacao.codigo.invalido", (AcessoSistema)null));
        }

        return regraValidacao;
    }

}
