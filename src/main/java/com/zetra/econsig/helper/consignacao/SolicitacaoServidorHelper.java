package com.zetra.econsig.helper.consignacao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: SolicitacaoServidorHelper</p>
 * <p>Description: Helper para solicitação de consignações
 * pelo servidor.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class SolicitacaoServidorHelper {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SolicitacaoServidorHelper.class);

    /**
     * Lista os serviços que o servidor pode realizar solicitações, tanto para
     * simulação quanto para reserva.
     * @param orgCodigo
     * @param svcCodigo
     * @param temPermissaoSimulacao
     * @param temPermissaoReserva
     * @param responsavel
     * @return
     * @throws ViewHelperException
     */
    public static List<TransferObject> lstServicos(String orgCodigo, String svcCodigo, String csaCodigo, boolean temPermissaoSimulacao, boolean temPermissaoReserva, boolean temPermissaoSolicitacao, AcessoSistema responsavel) throws ViewHelperException {
        return lstServicos(orgCodigo, svcCodigo, csaCodigo, temPermissaoSimulacao, temPermissaoReserva, temPermissaoSolicitacao, false, null,null, responsavel);
    }

    public static List<TransferObject> lstServicos(String orgCodigo, String svcCodigo, String csaCodigo, boolean temPermissaoSimulacao, boolean temPermissaoReserva, boolean temPermissaoSolicitacao, boolean apenasSimulacao, String nseCodigo, AcessoSistema responsavel) throws ViewHelperException {
        return lstServicos(orgCodigo, svcCodigo, csaCodigo, temPermissaoSimulacao, temPermissaoReserva, temPermissaoSolicitacao, apenasSimulacao, nseCodigo, null, responsavel);
    }

    public static List<TransferObject> lstServicosCorrespondentes(String orgCodigo, String svcCodigo, String csaCodigo, boolean temPermissaoSimulacao, boolean temPermissaoReserva, boolean temPermissaoSolicitacao,String corCodigo, AcessoSistema responsavel) throws ViewHelperException {
        return lstServicos(orgCodigo, svcCodigo, csaCodigo, temPermissaoSimulacao, temPermissaoReserva, temPermissaoSolicitacao, false, null, corCodigo, responsavel);
    }

    /**
     * Overload de método lstServicos, mas com opção de retornar apenas os serviços que permitem simulação
     * @param orgCodigo
     * @param svcCodigo
     * @param csaCodigo
     * @param temPermissaoSimulacao
     * @param temPermissaoReserva
     * @param apenasSimulacao - opção para retorno ou não apenas de serviços de simulação
     * @param responsavel
     * @return
     * @throws ViewHelperException
     */
    public static List<TransferObject> lstServicos(String orgCodigo, String svcCodigo, String csaCodigo, boolean temPermissaoSimulacao, boolean temPermissaoReserva, boolean temPermissaoSolicitacao, boolean apenasSimulacao, String nseCodigo, String corCodigo, AcessoSistema responsavel) throws ViewHelperException {
        final List<TransferObject> servicos = new ArrayList<>();

        // Lista de serviços que o servidor pode solicitar
        List<TransferObject> servicosReserva = null;
        // Lista de serviços que o servidor pode simular
        List<TransferObject> servicosSimulacao = null;

        // Se servidor tem permissão para reservar margem, busca Lista de serviços
        // disponíveis para solicitação pelo servidor
        try {
            if (temPermissaoReserva || (temPermissaoSimulacao && ParamSist.getBoolParamSist(CodedValues.TPC_SIMULACAO_CONSIGNACAO, responsavel))) {
                final ServicoController servicoController = ApplicationContextProvider.getApplicationContext().getBean(ServicoController.class);
                if (TextHelper.isNull(corCodigo)) {
                    servicosReserva = servicoController.selectServicosComParametro(CodedValues.TPS_PERMITE_SERVIDOR_SOLICITAR, svcCodigo, orgCodigo, csaCodigo, "1", false, nseCodigo, responsavel);
                } else {
                    servicosReserva = servicoController.selectServicosComParametroCorrespondente(CodedValues.TPS_PERMITE_SERVIDOR_SOLICITAR, svcCodigo, orgCodigo, corCodigo, "1", false, nseCodigo, responsavel);
                }

                // Filtra a listagem excluindo serviços que são origem de alongamento
                if (servicosReserva != null && !servicosReserva.isEmpty() && ParamSist.paramEquals(CodedValues.TPC_TEM_ALONGAMENTO_CONTRATO, CodedValues.TPC_SIM, responsavel)) {
                    final ParametroController parametroController = ApplicationContextProvider.getApplicationContext().getBean(ParametroController.class);
                    servicosReserva = parametroController.filtrarServicosSemRelacionamentoAlongamento(servicosReserva, responsavel);
                }
            }
        } catch (ServicoControllerException | ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException(ex);
        }

        // Se servidor tem permissão para simular e o sistema tem simulação de consignação,
        // lista os serviços disponíveis para simulação
        try {
            if (temPermissaoSimulacao && ParamSist.getBoolParamSist(CodedValues.TPC_SIMULACAO_CONSIGNACAO, responsavel)) {
                final SimulacaoController simulacaoController = ApplicationContextProvider.getApplicationContext().getBean(SimulacaoController.class);
                final short dia = (short) Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                servicosSimulacao = simulacaoController.lstServicosSimulacao(csaCodigo, svcCodigo, orgCodigo, dia, responsavel);
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException(ex);
        }

        final Map<String, Boolean> svcCodigosSimulacao = new HashMap<>();
        if (servicosSimulacao != null) {
            for (TransferObject next : servicosSimulacao) {
                final String codigoServico = next.getAttribute(Columns.SVC_CODIGO).toString();
                svcCodigosSimulacao.put(codigoServico, Boolean.TRUE);
            }
        }

        // Navega nos serviços listados e monta lista final
        if (servicosReserva != null) {
            final String rotuloMenu = temPermissaoSolicitacao ? ApplicationResourcesHelper.getMessage("rotulo.simulacao.menu.servidor", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.simular.menu.servidor", responsavel);
            for (TransferObject next : servicosReserva) {
                final String codigoServico = next.getAttribute(Columns.SVC_CODIGO).toString();
                final String svcDescricao = next.getAttribute(Columns.SVC_DESCRICAO).toString().toUpperCase();
                final String naturezaServico = next.getAttribute(Columns.NSE_CODIGO).toString();
                final String label = rotuloMenu + " " + svcDescricao.toLowerCase();
                String link = null;
                boolean fluxoReservaMargem = false;

                if (svcCodigosSimulacao.containsKey(codigoServico) && CodedValues.NSE_CARTAO.equals(naturezaServico) && temPermissaoReserva) {
                    link = "../v3/solicitarReservaCartao?acao=selecionarCsa&SVC_CODIGO=" + codigoServico + "&SVC_DESCRICAO=" + TextHelper.forJavaScriptAttribute(TextHelper.encode64(svcDescricao));
                    fluxoReservaMargem = true;
                } else if (svcCodigosSimulacao.containsKey(codigoServico) || !temPermissaoReserva) {
                	if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                		link = "../v3/solicitarLeilao?acao=iniciarSimulacao&SVC_CODIGO=" + codigoServico + "&titulo=" + TextHelper.forJavaScriptAttribute(TextHelper.encode64(svcDescricao));
                	} else {
                		link = "../v3/simularConsignacao?acao=iniciarSimulacao&SVC_CODIGO=" + codigoServico + "&titulo=" + TextHelper.forJavaScriptAttribute(TextHelper.encode64(svcDescricao));
                	}
                	fluxoReservaMargem = false;
                } else if (!apenasSimulacao) {
                    link = "../v3/reservarMargem?acao=selecionarCsa&SVC_CODIGO=" + codigoServico + "&SVC_DESCRICAO=" + TextHelper.forJavaScriptAttribute(TextHelper.encode64(svcDescricao));
                    fluxoReservaMargem = true;
                } else {
                    continue;
                }

                next.setAttribute("svcCodigo", codigoServico);
                next.setAttribute("link", link);
                next.setAttribute("label", label);
                next.setAttribute("fluxoReservaMargem", fluxoReservaMargem);
                servicos.add(next);
            }
        }

        return servicos;
    }

    public static List<TransferObject> lstServicosRenegociaveis(String svcCodigo, String orgCodigo, String csaCodigo, String nseCodigo, AcessoSistema responsavel) throws ViewHelperException {
        try {
            final ServicoController servicoController = ApplicationContextProvider.getApplicationContext().getBean(ServicoController.class);
            final List<TransferObject> svcRenegociacao = servicoController.lstServicosRenegociaveisServidor(null, orgCodigo, null, null, responsavel);

            // Navega nos serviços listados e monta lista final
            final List<TransferObject> servicos = new ArrayList<>();

            if (svcRenegociacao != null) {
                final String rotuloMenu = ApplicationResourcesHelper.getMessage("rotulo.servico.renegociavel.simulacao.renegociacao.de", responsavel) + " ";
                for (TransferObject next : svcRenegociacao) {
                    final String codigoServico = next.getAttribute(Columns.SVC_CODIGO).toString();
                    final String svcDescricao = next.getAttribute(Columns.SVC_DESCRICAO).toString().toUpperCase();
                    final String svcIdentificador = (String) next.getAttribute(Columns.SVC_IDENTIFICADOR);
                    final String label = rotuloMenu + " " + svcDescricao.toLowerCase();

                    final String link = "../v3/simularRenegociacao?acao=listarCsaRenegociacao&SVC_CODIGO=" + codigoServico + "&ORG_CODIGO=" + orgCodigo + "&SVC_IDENTIFICADOR=" + svcIdentificador + "&SVC_DESCRICAO=" + TextHelper.forJavaScriptAttribute(TextHelper.encode64(svcDescricao));

                    next.setAttribute("link", link);
                    next.setAttribute("label", label);
                    servicos.add(next);
                }
            }

            return servicos;
        } catch (ServicoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erro.recuperar.servicos.disponiveis.renegociacao", responsavel);
        }
    }
}
