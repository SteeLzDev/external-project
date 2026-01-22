package com.zetra.econsig.values;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: TipoDispositivoEnum</p>
 * <p>Description: Enumeration de tipo de dispositivo do usuário</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum TipoDispositivoEnum {
    ANDROID("1"),
    IOS("2"),
    WINDOWS("3"),
    LEITOR_DIGITAIS("4");

    private String codigo;

    private TipoDispositivoEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    /**
     * Recupera um tipo de arquivo de acordo com o código passado.
     *
     * @param codigo Código do tipo de arquivo que deve ser recuperado.
     * @return Retorna um tipo de arquivo
     *
     * @throws IllegalArgumentException Caso o código do tipo de arquivo informádo seja inválido
     */
    public static TipoDispositivoEnum recuperaTipoArquivo(String codigo) {
        TipoDispositivoEnum tipoArquivo = null;

        for (TipoDispositivoEnum tipo : TipoDispositivoEnum.values()) {
            if (tipo.getCodigo().equals(codigo)) {
                tipoArquivo = tipo;
                break;
            }
        }

        if (tipoArquivo == null) {
            throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.tipo.dispositivo.invalido", (AcessoSistema) null));
        }

        return tipoArquivo;
    }

}
