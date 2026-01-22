package com.zetra.econsig.values;

import java.util.ArrayList;
import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: OperacaoHistoricoMargemEnum</p>
 * <p>Description: Operações que geram histórico de margem.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum OperacaoHistoricoMargemEnum {

    CONSIGNACAO            ("1", ApplicationResourcesHelper.getMessage("rotulo.enum.operacao.historico.margem.consignacao", (AcessoSistema) null)),
    EDT_REGISTRO_SERVIDOR  ("2", ApplicationResourcesHelper.getMessage("rotulo.enum.operacao.historico.margem.edicao.registro.servidor", (AcessoSistema) null)),
    IMPORTACAO_MARGEM      ("3", ApplicationResourcesHelper.getMessage("rotulo.enum.operacao.historico.margem.importacao.margem", (AcessoSistema) null)),
    EXPORTACAO_MOV_FIN     ("4", ApplicationResourcesHelper.getMessage("rotulo.enum.operacao.historico.margem.exportacao.movimento.financeiro", (AcessoSistema) null)),
    IMPORTACAO_RET_MOV_FIN ("5", ApplicationResourcesHelper.getMessage("rotulo.enum.operacao.historico.margem.importacao.retorno", (AcessoSistema) null)),
    REVERSAO_RET_MOV_FIN   ("6", ApplicationResourcesHelper.getMessage("rotulo.enum.operacao.historico.margem.reversao.retorno", (AcessoSistema) null)),
    CANCEL_AUTOMATICO_ADE  ("7", ApplicationResourcesHelper.getMessage("rotulo.enum.operacao.historico.margem.cancelamento.automatico.consigancoes", (AcessoSistema) null)),
    RECALCULO_MARGEM       ("8", ApplicationResourcesHelper.getMessage("rotulo.enum.operacao.historico.margem.recalculo.margem", (AcessoSistema) null)),
    TRANSFERENCIA_MARGEM   ("9", ApplicationResourcesHelper.getMessage("rotulo.enum.operacao.historico.margem.transferencia.margem", (AcessoSistema) null));

    private final String codigo;
    private final String descricao;

    private OperacaoHistoricoMargemEnum(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    /**
     * Recupera o objeto de enumeração de tipo de operação de histórico de margem de acordo com o código passado.
     * @param codigo : Código do objeto de enumeração
     * @return
     * @throws IllegalArgumentException
     */
    public static OperacaoHistoricoMargemEnum recuperaOperacaoHistoricoMargemEnum(String codigo) {
        for (OperacaoHistoricoMargemEnum tipo : OperacaoHistoricoMargemEnum.values()) {
            if (tipo.getCodigo().equals(codigo)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.tipo.operacao.historico.margem", (AcessoSistema) null));
    }

    public static List<TransferObject> getListValues() {
        List<TransferObject> list = new ArrayList<TransferObject>();
        for (OperacaoHistoricoMargemEnum tipo : OperacaoHistoricoMargemEnum.values()) {
            TransferObject row = new CustomTransferObject();
            row.setAttribute("CODIGO", tipo.getCodigo());
            row.setAttribute("DESCRICAO", tipo.getDescricao());
            list.add(row);
        }
        return list;
    }

    public boolean equals(OperacaoHistoricoMargemEnum outro) {
        return getCodigo().equals(outro.getCodigo());
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }
}
