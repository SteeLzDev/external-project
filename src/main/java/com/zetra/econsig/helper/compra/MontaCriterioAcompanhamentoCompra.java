package com.zetra.econsig.helper.compra;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: MontaCriterioAcompanhamentoCompra</p>
 * <p>Description: criação de filtros de acompanhamento de compra de contratos específicos</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MontaCriterioAcompanhamentoCompra {
	/**
     * Recupera os critérios para busca de contratos com pendência de informação de saldo devedor.
     * @param criteriosSelecionadosPesquisa : Critérios selecionados na página de pesquisa
     * @return
     */
    public static CustomTransferObject getCriteriosBuscaPendenciaInfoSaldoDevedor(CustomTransferObject criteriosSelecionadosPesquisa) {
        CustomTransferObject criterios = new CustomTransferObject();
        if (criteriosSelecionadosPesquisa != null) {
            criterios.setAtributos(criteriosSelecionadosPesquisa.getAtributos());
        }
        criterios.setAttribute("origem", "0");
        criterios.setAttribute("temSaldoDevedor", "NAO");
        criterios.setAttribute("diasSemSaldoDevedor", "0");
        criterios.setAttribute("saldoDevedorPago", "TODOS");
        criterios.setAttribute("diasSemPagamentoSaldoDevedor", "0");
        criterios.setAttribute("liquidado", "NAO");
        criterios.setAttribute("diasSemLiquidacao", "0");
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_APROVACAO_SALDO_SERVIDOR_COMPRA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            criterios.setAttribute("saldoDevedorAprovado", "TODOS");
            criterios.setAttribute("diasSemAprovacaoSaldoDevedor", "0");
        }
        return criterios;
    }

    /**
     * Recupera os critérios para busca de contratos com pendência de pagamento de saldo devedor.
     * @param criteriosSelecionadosPesquisa : Critérios selecionados na página de pesquisa
     * @return
     */
    public static CustomTransferObject getCriteriosBuscaPendenciaAprovacaoSaldoDevedor(CustomTransferObject criteriosSelecionadosPesquisa) {
        CustomTransferObject criterios = new CustomTransferObject();
        if (criteriosSelecionadosPesquisa != null) {
            criterios.setAtributos(criteriosSelecionadosPesquisa.getAtributos());
        }
        criterios.setAttribute("origem", "0");
        criterios.setAttribute("temSaldoDevedor", "SIM");
        criterios.setAttribute("diasSemSaldoDevedor", "0");
        criterios.setAttribute("saldoDevedorAprovado", "NAO");
        criterios.setAttribute("diasSemAprovacaoSaldoDevedor", "0");
        criterios.setAttribute("saldoDevedorPago", "TODOS");
        criterios.setAttribute("diasSemPagamentoSaldoDevedor", "0");
        criterios.setAttribute("liquidado", "NAO");
        criterios.setAttribute("diasSemLiquidacao", "0");

        return criterios;
    }

    /**
     * Recupera os critérios para busca de contratos com pendência de pagamento de saldo devedor.
     * @param criteriosSelecionadosPesquisa : Critérios selecionados na página de pesquisa
     * @return
     */
    public static CustomTransferObject getCriteriosBuscaPendenciaPagtoSaldoDevedor(CustomTransferObject criteriosSelecionadosPesquisa) {
        CustomTransferObject criterios = new CustomTransferObject();
        if (criteriosSelecionadosPesquisa != null) {
            criterios.setAtributos(criteriosSelecionadosPesquisa.getAtributos());
        }
        criterios.setAttribute("origem", "1");
        criterios.setAttribute("temSaldoDevedor", "SIM");
        criterios.setAttribute("diasSemSaldoDevedor", "0");
        criterios.setAttribute("saldoDevedorPago", "NAO");
        criterios.setAttribute("diasSemPagamentoSaldoDevedor", "0");
        criterios.setAttribute("liquidado", "NAO");
        criterios.setAttribute("diasSemLiquidacao", "0");
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_APROVACAO_SALDO_SERVIDOR_COMPRA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            criterios.setAttribute("saldoDevedorAprovado", "SIM");
            criterios.setAttribute("diasSemAprovacaoSaldoDevedor", "0");
        }

        return criterios;
    }

    /**
     * Recupera os critérios para busca de contratos com pendência de liquidação.
     * @param criteriosSelecionadosPesquisa : Critérios selecionados na página de pesquisa
     * @return
     */
    public static CustomTransferObject getCriteriosBuscaPendenciaLiquidacao(CustomTransferObject criteriosSelecionadosPesquisa) {
        CustomTransferObject criterios = new CustomTransferObject();
        if (criteriosSelecionadosPesquisa != null) {
            criterios.setAtributos(criteriosSelecionadosPesquisa.getAtributos());
        }
        criterios.setAttribute("origem", "0");
        criterios.setAttribute("temSaldoDevedor", "SIM");
        criterios.setAttribute("diasSemSaldoDevedor", "0");
        criterios.setAttribute("saldoDevedorPago", "SIM");
        criterios.setAttribute("diasSemPagamentoSaldoDevedor", "0");
        criterios.setAttribute("liquidado", "NAO");
        criterios.setAttribute("diasSemLiquidacao", "0");
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_APROVACAO_SALDO_SERVIDOR_COMPRA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            criterios.setAttribute("saldoDevedorAprovado", "SIM");
            criterios.setAttribute("diasSemAprovacaoSaldoDevedor", "0");
        }
        if (!ParamSist.paramEquals(CodedValues.TPC_CICLO_VIDA_FIXO_PROCESSO_COMPRA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema()) &&
                ParamSist.paramEquals(CodedValues.TPC_AVANCA_FLUXO_COMPRA_SEM_CICLO_FIXO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            // Com ciclo de vida não fixo e permitindo avanço do fluxo da compra mesmo com operações fora de ordem,
            // lista pendência de liquidação independente se tenha saldo devedor informado ou aprovado.
            criterios.setAttribute("temSaldoDevedor", "TODOS");
            criterios.setAttribute("saldoDevedorAprovado", "TODOS");
        }

        return criterios;
    }

    /**
     * Recupera os critérios para busca de contratos com bloqueio de informação de saldo devedor.
     * @param criteriosSelecionadosPesquisa : Critérios selecionados na página de pesquisa
     * @return
     */
    public static CustomTransferObject getCriteriosBuscaBloqueioInfoSaldoDevedor(CustomTransferObject criteriosSelecionadosPesquisa) {
        CustomTransferObject criterios = getCriteriosBuscaPendenciaInfoSaldoDevedor(criteriosSelecionadosPesquisa);
        criterios.setAttribute("bloqueio", "0");
        if (criteriosSelecionadosPesquisa != null) {
            criterios.setAttribute("diasBloqueio", criteriosSelecionadosPesquisa.getAttribute("diasBloqueio"));
        }

        return criterios;
    }

    /**
     * Recupera os critérios para busca de contratos com bloqueio de pagamento de saldo devedor.
     * @param criteriosSelecionadosPesquisa : Critérios selecionados na página de pesquisa
     * @return
     */
    public static CustomTransferObject getCriteriosBuscaBloqueioAprovacaoSaldoDevedor(CustomTransferObject criteriosSelecionadosPesquisa) {
        CustomTransferObject criterios = getCriteriosBuscaPendenciaAprovacaoSaldoDevedor(criteriosSelecionadosPesquisa);
        criterios.setAttribute("bloqueio", "3");
        if (criteriosSelecionadosPesquisa != null) {
            criterios.setAttribute("diasBloqueio", criteriosSelecionadosPesquisa.getAttribute("diasBloqueio"));
        }
        return criterios;
    }

    /**
     * Recupera os critérios para busca de contratos com bloqueio de pagamento de saldo devedor.
     * @param criteriosSelecionadosPesquisa : Critérios selecionados na página de pesquisa
     * @return
     */
    public static CustomTransferObject getCriteriosBuscaBloqueioPagtoSaldoDevedor(CustomTransferObject criteriosSelecionadosPesquisa) {
        CustomTransferObject criterios = getCriteriosBuscaPendenciaPagtoSaldoDevedor(criteriosSelecionadosPesquisa);
        criterios.setAttribute("bloqueio", "1");
        if (criteriosSelecionadosPesquisa != null) {
            criterios.setAttribute("diasBloqueio", criteriosSelecionadosPesquisa.getAttribute("diasBloqueio"));
        }
        return criterios;
    }

    /**
     * Recupera os critérios para busca de contratos com bloqueio de liquidação.
     * @param criteriosSelecionadosPesquisa : Critérios selecionados na página de pesquisa
     * @return
     */
    public static CustomTransferObject getCriteriosBuscaBloqueioLiquidacao(CustomTransferObject criteriosSelecionadosPesquisa) {
        CustomTransferObject criterios = getCriteriosBuscaPendenciaLiquidacao(criteriosSelecionadosPesquisa);
        criterios.setAttribute("bloqueio", "2");
        if (criteriosSelecionadosPesquisa != null) {
            criterios.setAttribute("diasBloqueio", criteriosSelecionadosPesquisa.getAttribute("diasBloqueio"));
        }
        return criterios;
    }
}
