package com.zetra.econsig.values;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: AgendamentoEnum</p>
 * <p>Description: Enumeração de operações eConsig.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum OperacaoEConsigEnum {

    ALONGAR_CONTRATO       ("rotulo.alongar.consignacao.titulo"),
    ALTERAR_CONSIGNACAO    ("rotulo.alterar.consignacao.titulo"),
    AUTORIZAR_RESERVA      ("rotulo.autorizar.reserva.titulo"),
    CANCELAR_CONSIGNACAO   ("rotulo.cancelar.consignacao.titulo"),
    CANCELAR_RESERVA       ("rotulo.cancelar.reserva.titulo"),
    CONFIRMAR_RESERVA      ("rotulo.confirmar.reserva.titulo"),
    CONFIRMAR_SOLICITACAO  ("rotulo.confirmar.solicitacao.titulo"),
    CONSULTAR_CONSIGNACAO  ("rotulo.consultar.consignacao.titulo"),
    CONSULTAR_MARGEM       ("rotulo.consultar.margem.titulo"),
    DEFERIR_CONSIGNACAO    ("rotulo.deferir.consignacao.titulo"),
    DESLIQUIDAR_CONTRATO   ("rotulo.desliquidar.consignacao.titulo"),
    INDEFERIR_CONSIGNACAO  ("rotulo.indeferir.consignacao.titulo"),
    INSERIR_SOLICITACAO    ("rotulo.inserir.solicitacao.titulo"),
    LIQUIDAR_CONSIGNACAO   ("rotulo.liquidar.consignacao.titulo"),
    LISTA_SOLICITACOES     ("rotulo.listar.solicitacao.consignacao.titulo"),
    REATIVAR_CONSIGNACAO   ("rotulo.reativar.consignacao.titulo"),
    RENEGOCIAR_CONSIGNACAO ("rotulo.renegociar.consignacao.titulo"),
    RESERVAR_MARGEM        ("rotulo.reservar.margem.titulo"),
    SIMULAR_CONSIGNACAO    ("rotulo.simular.consignacao.titulo"),
    SUSPENDER_CONSIGNACAO  ("rotulo.suspender.consignacao.titulo"),
    SOLICITAR_LIQUIDACAO   ("rotulo.efetiva.acao.consignacao.solicitar.liquidacao");

    private String operacao;

    private OperacaoEConsigEnum(String operacao) {
        this.operacao = operacao;
    }

    public String getOperacao() {
        return ApplicationResourcesHelper.getMessage(operacao, AcessoSistema.getAcessoUsuarioSistema());
    }
}
