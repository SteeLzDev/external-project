package com.zetra.econsig.values;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: StatusProtocoloSenhaAutorizacaoEnum</p>
 * <p>Description: Enumeração de status do protocolo de senha de autorização.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum StatusProtocoloSenhaAutorizacaoEnum {

    INVALIDO("I"),
    VALIDO("V"),
    CONSUMIDO("C");

    private String codigo;

    private StatusProtocoloSenhaAutorizacaoEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        String chave = "";
        if (codigo.equals(StatusProtocoloSenhaAutorizacaoEnum.INVALIDO.getCodigo())) {
            chave = "rotulo.status.protocolo.senha.autorizacao.invalido";
        } else if (codigo.equals(StatusProtocoloSenhaAutorizacaoEnum.VALIDO.getCodigo())) {
            chave = "rotulo.status.protocolo.senha.autorizacao.valido";
        } else if (codigo.equals(StatusProtocoloSenhaAutorizacaoEnum.CONSUMIDO.getCodigo())) {
            chave = "rotulo.status.protocolo.senha.autorizacao.consumido";
        }

        return ApplicationResourcesHelper.getMessage(chave, AcessoSistema.getAcessoUsuarioSistema());
    }

    /**
     * Recupera um status do protocolo de senha de autorização de acordo com o código passado.
     * @param codigo Código do status do protocolo de senha de autorização que deve ser recuperado.
     * @return Retorna um status do protocolo de senha de autorização
     * @throws IllegalArgumentException Caso o código do status do protocolo de senha de autorização informádo seja inválido
     */
    public static StatusProtocoloSenhaAutorizacaoEnum recuperaStatusCompra(String codigo) {
        StatusProtocoloSenhaAutorizacaoEnum retorno = null;

        for (StatusProtocoloSenhaAutorizacaoEnum status : StatusProtocoloSenhaAutorizacaoEnum.values()) {
            if (status.getCodigo().equals(codigo)) {
                retorno = status;
                break;
            }
        }

        if (retorno == null) {
            throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.status.protocolo.senha.autorizacao.invalido", null));
        }

        return retorno;
    }

    public final boolean equals(StatusProtocoloSenhaAutorizacaoEnum other) {
        return (this==other || getCodigo().equals(other.getCodigo()));
    }
}
