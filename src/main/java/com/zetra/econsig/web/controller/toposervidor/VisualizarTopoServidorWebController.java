package com.zetra.econsig.web.controller.toposervidor;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.consignacao.SolicitacaoServidorHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: VisualizarTopoServidorWebController</p>
 * <p>Description: Controlador Web para o caso de uso Visualizar Topo do portal do Servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(value = { "/v3/visualizarTopoServidor" })
public class VisualizarTopoServidorWebController extends AbstractWebController {

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private ServidorController servidorController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        SynchronizerToken.saveToken(request);

        // Verifica se o sistema permite a alteração do login do servidor (registro funcional) sem sair do sistema
        boolean permiteAlterarLogin = ParamSist.paramEquals(CodedValues.TPC_PERMITE_ALTERAR_LOGIN_SERVIDOR, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        model.addAttribute("permiteAlterarLogin", permiteAlterarLogin);

        //Verifica se o módulo de leilão via simulação do servidor está habilitado.
        boolean moduloLeilaoHabilitado = ParamSist.paramEquals(CodedValues.TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        model.addAttribute("moduloLeilaoHabilitado", moduloLeilaoHabilitado);

        // Busca Lista de serviços disponíveis para solicitação pelo servidor
        List<TransferObject> servicosReserva = null;
        try {
            String orgCodigo = responsavel.getOrgCodigo();
            if (!TextHelper.isNull(orgCodigo)) {
                boolean temPermissaoSimulacao = responsavel.temPermissao(CodedValues.FUN_SIM_CONSIGNACAO);
                boolean temPermissaoReserva = responsavel.temPermissao(CodedValues.FUN_RES_MARGEM);
                boolean temPermissaoSolicitacao = responsavel.temPermissao(CodedValues.FUN_SOL_EMPRESTIMO);
                servicosReserva = SolicitacaoServidorHelper.lstServicos(orgCodigo, null, null, temPermissaoSimulacao, temPermissaoReserva, temPermissaoSolicitacao, responsavel);
            }
        } catch (ViewHelperException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        model.addAttribute("servicosReserva", servicosReserva);
        // Necessário para o novo leiaute
        session.setAttribute("servicosReserva", servicosReserva);

        //Busca Lista de serviços disponíveis para solicitação pelo servidor no leilão reverso.
        List<TransferObject> servicosReservaLeilao = null;
        try {
            String orgCodigo = responsavel.getOrgCodigo();
            if (!TextHelper.isNull(orgCodigo) && moduloLeilaoHabilitado) {
                boolean temPermissaoSimulacao = responsavel.temPermissao(CodedValues.FUN_SIM_CONSIGNACAO);
                boolean temPermissaoReserva = responsavel.temPermissao(CodedValues.FUN_RES_MARGEM);
                boolean temPermissaoSolicitacao = responsavel.temPermissao(CodedValues.FUN_SOL_EMPRESTIMO);
                servicosReservaLeilao = SolicitacaoServidorHelper.lstServicos(orgCodigo, null, null, temPermissaoSimulacao, temPermissaoReserva,temPermissaoSolicitacao, responsavel);

                for(TransferObject servico: servicosReservaLeilao) {
                    //flag que diferencia o fluxo como vindo do leilão reverso, e não de uma solicitação normal.
                    String link = ((String) servico.getAttribute("link")) + "&origem=true";
                    servico.setAttribute("link", link);
                }
            }
        } catch (ViewHelperException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        model.addAttribute("servicosReservaLeilao", servicosReservaLeilao);



        //Mostra os convênios bloqueados
        String rseCodigo = "";
        if (responsavel.getRseCodigo() != null) {
          rseCodigo = responsavel.getRseCodigo();
        }
        boolean temConvenioBloqueado = false;

        if (!rseCodigo.equals("")) {
            try {
              // Seleciona os convenios bloqueados.
              boolean mostraConveniosBloqueados = responsavel.temPermissao(CodedValues.FUN_CONSULTAR_CNV_SVC_BLOQ_SERVIDOR);
              if (mostraConveniosBloqueados) {
                temConvenioBloqueado = (servidorController.countConvenioBloqueados(rseCodigo, responsavel.getOrgCodigo(), responsavel.getCsaCodigo(), responsavel) > 0);
                if (!temConvenioBloqueado) {
                  java.util.Map<String, Long> svcBloqueios = parametroController.getBloqueioSvcRegistroServidor(rseCodigo, null, responsavel);
                  Long bloqueados = svcBloqueios.get("B");
                  temConvenioBloqueado = (bloqueados != null && bloqueados.intValue() > 0);
                }
                if (!temConvenioBloqueado) {
                  java.util.Map<String, Long> nseBloqueios = parametroController.getBloqueioNseRegistroServidor(rseCodigo, null, responsavel);
                  Long bloqueados = nseBloqueios.get("B");
                  temConvenioBloqueado = (bloqueados != null && bloqueados.intValue() > 0);
                }
                if (!temConvenioBloqueado) {
                    java.util.Map<String, Long> csaBloqueios = parametroController.getBloqueioCsaRegistroServidor(rseCodigo, null, responsavel);
                    Long bloqueados = csaBloqueios.get("B");
                    temConvenioBloqueado = (bloqueados != null && bloqueados.intValue() > 0);
                  }
              }
            } catch (ServidorControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            } catch (ParametroControllerException e) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }
        model.addAttribute("temConvenioBloqueado", temConvenioBloqueado);

        // Recupera a quantidade de registro servidor com o mesmo ser_codigo
        int qtdeRegistroServidor = 0;
        try {
            if (!TextHelper.isNull(responsavel.getSerCodigo())) {
                qtdeRegistroServidor = servidorController.countRegistroServidorSerCodigo(responsavel.getSerCodigo(), false, responsavel);
            }
        } catch (ServidorControllerException ex) {
          session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }
        model.addAttribute("qtdeRegistroServidor", qtdeRegistroServidor);

        return viewRedirect("jsp/visualizarTopoServidor/visualizarTopoServidor", request, session, model, responsavel);
    }

}
