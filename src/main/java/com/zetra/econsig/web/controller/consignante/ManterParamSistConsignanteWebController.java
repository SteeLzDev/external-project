package com.zetra.econsig.web.controller.consignante;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.OcorrenciaParamSistCseTO;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.helper.web.v3.JspHelper;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ManterParamSistConsignanteWebController</p>
 * <p>Description: Controlador Web para o caso de uso de Manutenção de Parâmetros de Sistema</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: frederico.penido $
 * $Revision: 27612 $
 * $Date: 2019-08-19 15:40:20 -0300 (seg, 19 ago 2019) $
 */

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/manterParamConsignante" })
public class ManterParamSistConsignanteWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ManterParamSistConsignanteWebController.class);

    @Autowired
    private ParametroController parametroController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        boolean podeEditarConsignante = responsavel.temPermissao(CodedValues.FUN_EDT_PARAM_SISTEMA_CSE);
        boolean podeVisualizarTodosParamSist = responsavel.temPermissao(CodedValues.FUN_CONS_PARAM_SISTEMA_CSE) || responsavel.temPermissao(CodedValues.FUN_EDT_PARAM_SISTEMA_CSE);

        List<TransferObject> paramSist = null;
        try {
            if (responsavel.isCse()) {
                paramSist = parametroController.selectParamSistCse(CodedValues.TPC_SIM, null, null, null, responsavel);
            } else if (responsavel.isSup()) {
                paramSist = parametroController.selectParamSistCse(null, null, CodedValues.TPC_SIM, null, responsavel);
            }
        } catch (ParametroControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        List<TransferObject> paramSistConsulta = null;
        try {
            if (podeVisualizarTodosParamSist) {
                if (responsavel.isCse()) {
                    paramSistConsulta = parametroController.selectParamSistCse(null, CodedValues.TPC_SIM, null, null, responsavel);
                } else if (responsavel.isSup()) {
                    paramSistConsulta = parametroController.selectParamSistCse(null, null, null, CodedValues.TPC_SIM, responsavel);
                }
            }
        } catch (ParametroControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        Object tpcParam = null;

        // Busca os Parâmetros de Sistema Sobre a Reimplantação
        tpcParam = ParamSist.getInstance().getParam(CodedValues.TPC_REIMPLANTACAO_AUTOMATICA, responsavel);
        boolean tpcReimplantacaoAutomatica = (tpcParam != null && tpcParam.equals(CodedValues.TPC_SIM));
        tpcParam = ParamSist.getInstance().getParam(CodedValues.TPC_CSA_ALTERA_REIMPLANTACAO, responsavel);
        boolean tpcCsaEscolheReimpl = (tpcParam != null && tpcParam.equals(CodedValues.TPC_SIM));
        // Parâmetro para exibir botão responsável por levar para o Rodapé da pagina
        boolean exibeBotaoRodape = ParamSist.paramEquals(CodedValues.TPC_EXIBE_BOTAO_RESPONSAVEL_PELO_RODAPE_DA_PAGINA, CodedValues.TPC_SIM, responsavel);

        model.addAttribute("paramSistConsulta", paramSistConsulta);
        model.addAttribute("paramSist", paramSist);
        model.addAttribute("podeEditarConsignante", podeEditarConsignante);
        model.addAttribute("tpcReimplantacaoAutomatica", tpcReimplantacaoAutomatica);
        model.addAttribute("tpcCsaEscolheReimpl", tpcCsaEscolheReimpl);
        model.addAttribute("exibeBotaoRodape", exibeBotaoRodape);

        return viewRedirect("jsp/editarConsignante/editarParamConsignante", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        if (responsavel.temPermissao(CodedValues.FUN_EDT_PARAM_SISTEMA_CSE)) {
            try {
                String tpcCseAltera = responsavel.isCse() ? CodedValues.TPC_SIM : null;
                String tpcSupAltera = responsavel.isSup() ? CodedValues.TPC_SIM : null;
                List<TransferObject> paramSist = parametroController.selectParamSistCse(tpcCseAltera, null, tpcSupAltera, null, responsavel);

                if (paramSist != null && !paramSist.isEmpty()) {
                    for (TransferObject next : paramSist) {
                        String tpcCodigo = next.getAttribute(Columns.TPC_CODIGO).toString();
                        if (tpcCodigo.equals(CodedValues.TPC_DIR_RAIZ_ARQUIVOS)) {
                            continue;
                        }
                        String tpcDominio = next.getAttribute(Columns.TPC_DOMINIO).toString();
                        String tpcVlrDefault = (String) next.getAttribute(Columns.TPC_VLR_DEFAULT);
                        String oldPsiVlr = next.getAttribute(Columns.PSI_VLR) != null ? next.getAttribute(Columns.PSI_VLR).toString() : "";
                        String newPsiVlr = request.getParameter(tpcCodigo);
                        if (newPsiVlr == null) {
                            // Se o parâmetro não foi enviado na requisição, então não salva
                            continue;
                        }

                        if (TextHelper.isNull(newPsiVlr)) {
                            // Se o parâmetro foi enviado na requisição como vazio, este só pode ser vazio se o valor default também for
                            if (!TextHelper.isNull(tpcVlrDefault) && !TextHelper.isNull(oldPsiVlr)) {
                                // Se o valor default NÃO é nulo, e o valor antigo também não, mantém o valor antigo
                                newPsiVlr = oldPsiVlr;
                            }
                        }

                        if (!TextHelper.isNull(newPsiVlr) && (tpcDominio.equals("MONETARIO") || tpcDominio.equals("FLOAT"))) {
                            try {
                                newPsiVlr = NumberHelper.reformat(newPsiVlr, NumberHelper.getLang(), "en");
                            } catch (java.text.ParseException ex) {
                                LOG.error(ex.getMessage(), ex);
                            }
                        }

                        if (!oldPsiVlr.equals(newPsiVlr)) {
                            // Altera o parâmetro no banco
                            parametroController.updateParamSistCse(newPsiVlr, tpcCodigo, CodedValues.CSE_CODIGO_SISTEMA, responsavel);

                            // Altera o repositório de parâmetros
                            ParamSist.getInstance().setParam(tpcCodigo, newPsiVlr);
                        }
                    }
                }

                // Reinicializa os caches de dados
                JspHelper.limparCacheParametros();

                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alterar.parametro.consignante.sucesso", responsavel));
            } catch (ParametroControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }
        }

        ParamSession paramSession = ParamSession.getParamSession(session);
        model.addAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }

    @RequestMapping(params = { "acao=listarHistoricoParametro" })
    public String buscarHistoricoParamConsignante(HttpServletRequest request, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String filtro = JspHelper.verificaVarQryStr(request, "FILTRO");
        int filtro_tipo = -1;
        try {
            filtro_tipo = Integer.parseInt(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO"));
        } catch (Exception ex1) {
        }

        LinkedList<OcorrenciaParamSistCseTO> histParamCse = new LinkedList<>();
        try {
            OcorrenciaParamSistCseTO criterio = new OcorrenciaParamSistCseTO();

            //-------------- Seta Criterio da Listagem ------------------
            // Descrição
            if (filtro_tipo == 1) {
                criterio.setTpcDescricao(CodedValues.LIKE_MULTIPLO + filtro + CodedValues.LIKE_MULTIPLO);

                // Usuário
            } else if (filtro_tipo == 2) {
                criterio.setUsuLogin(CodedValues.LIKE_MULTIPLO + filtro + CodedValues.LIKE_MULTIPLO);

                // Código
            } else if (filtro_tipo == 3) {
                criterio.setTpcCodigo(filtro);

                // Lista tudo
            } else if (filtro_tipo != -1) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            // -----------------------------------

            int total = parametroController.countOcorrenciaParamSistCse(criterio, responsavel);
            int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (Exception ex) {
            }

            criterio.setAttribute("offset", offset);
            criterio.setAttribute("size", size);
            histParamCse = parametroController.selectOcorrenciaParamSistCse(criterio, responsavel);

            // Monta lista de parâmetros através dos parâmetros de request
            Set<String> params = new HashSet<>(request.getParameterMap().keySet());

            // Ignora os parâmetros abaixo
            params.remove("senha");
            params.remove("serAutorizacao");
            params.remove("cryptedPasswordFieldName");
            params.remove("offset");
            params.remove("back");
            params.remove("linkRet");
            params.remove("linkRet64");
            params.remove("eConsig.page.token");
            params.remove("_skip_history_");
            params.remove("pager");
            params.remove("acao");

            List<String> requestParams = new ArrayList<>(params);

            String linkListagem = "../v3/manterParamConsignante?acao=listarHistoricoParametro";
            configurarPaginador(linkListagem, "rotulo.consignante.pagina.titulo", total, size, requestParams, false, request, model);
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        model.addAttribute("filtro", filtro);
        model.addAttribute("filtro_tipo", filtro_tipo);
        model.addAttribute("histParamCse", histParamCse);

        return viewRedirect("jsp/editarConsignante/listarHistParamConsignante", request, session, model, responsavel);
    }
}
