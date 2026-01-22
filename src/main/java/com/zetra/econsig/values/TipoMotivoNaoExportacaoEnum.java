package com.zetra.econsig.values;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: TipoMotivoNaoExportacaoEnum</p>
 * <p>Description: Constantes específicas de motivos de não exportação.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum TipoMotivoNaoExportacaoEnum {
    SERVIDOR_SEM_MARGEM_SUFICIENTE("1"),
    OPERACAO_SEM_ANEXO_OBRIGATORIO("2"),
    SERVIDOR_EXCLUIDO_DATA_ULT_SALARIO_PASSADA("3"),
    SERVIDOR_BLOQUEADO_DATA_ULT_SALARIO_PASSADA("4"),
    SERVIDOR_FALECIDO("5"),
    SERVIDOR_EXCLUIDO("6"),
    OPERACAO_SEM_PERMISSAO_GESTOR("7"),
    AGUARDANDO_ANALISE_SUP("8");

    private String codigo;

    private TipoMotivoNaoExportacaoEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    /**
     * Recupera um tipo de motivo de não exportação de acordo com o código passado.
     *
     * @param codigo Código do tipo de motivo de não exportação que deve ser recuperado.
     * @return Retorna um tipo de motivo de não exportação
     *
     * @throws IllegalArgumentException Caso o código do tipo de motivo de não exportação informádo seja inválido
     */
    public static TipoMotivoNaoExportacaoEnum recuperaTipoMotivoNaoExportacao(String codigo) {
        TipoMotivoNaoExportacaoEnum tipoMotivoNaoExportacao = null;

        for (TipoMotivoNaoExportacaoEnum tipo : TipoMotivoNaoExportacaoEnum.values()) {
            if (tipo.getCodigo().equals(codigo)) {
                tipoMotivoNaoExportacao = tipo;
                break;
            }
        }

        if (tipoMotivoNaoExportacao == null) {
            throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.tipo.motivo.nao.exportacao.invalido", (AcessoSistema) null));
        }

        return tipoMotivoNaoExportacao;
    }
}
