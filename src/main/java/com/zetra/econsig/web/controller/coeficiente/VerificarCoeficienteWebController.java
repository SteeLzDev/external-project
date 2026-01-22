package com.zetra.econsig.web.controller.coeficiente;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.PrazoTransferObject;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.coeficiente.AtivarCoeficienteController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: VerificarCoeficienteWebController</p>
 * <p>Description: Controlador Web para o caso de uso Verificar Coeficiente.</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author: moises.souza $
 * $Revision: 24435 $
 * $Date: 2018-05-28 08:58:59 -0300 (Seg, 28 mai 2018) $
 */
@Controller
@RequestMapping(value = "/v3/verificarCoeficiente")
public class VerificarCoeficienteWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(VerificarCoeficienteWebController.class);

    @Autowired
    private AtivarCoeficienteController ativarCoeficienteController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private SimulacaoController simulacaoController;

    @RequestMapping(params = { "acao=editarCoeficiente" })
    public String editarCoeficiente(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=consultarCoeficiente" })
    public String consultarCoeficiente(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return iniciar(request, response, session, model);
    }

    private String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Valida o token
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Verifica se o sistema está configurado para trabalhar com o CET.
            boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);

            String tipo = JspHelper.verificaVarQryStr(request, "tipo");
            String csa_codigo = "";
            if (responsavel.isCsa()) {
                csa_codigo = responsavel.getCodigoEntidade();
            } else {
                csa_codigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
            }

            String svc_codigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
            String svc_descricao = JspHelper.verificaVarQryStr(request, "SVC_DESCRICAO");
            String titulo = JspHelper.verificaVarQryStr(request, "titulo");
            String acao = JspHelper.verificaVarQryStr(request, "acao");

            if (svc_codigo.equals("") || svc_descricao.equals("") || csa_codigo.equals("") || titulo.equals("") || tipo.equals("")) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            List<PrazoTransferObject> prazos = null;
            // Seleciona prazos ativos.
            try {
                prazos = simulacaoController.findPrazoCsaByServico(svc_codigo, csa_codigo, responsavel);
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String operacao = JspHelper.verificaVarQryStr(request, "OPERACAO");
            boolean mostraAtivo = operacao.equals("ATIVO");
            boolean tipoMensal = tipo.equals("M");

            boolean podeAtivarPsc = !mostraAtivo;
            boolean podeAtivarCft = !mostraAtivo;
            boolean pscAtivo = true;
            boolean cftAtivo = true;

            int minDia = tipoMensal ? 0 : 1;
            int maxDia = tipoMensal ? 0 : 31;

            List<TransferObject> coeficientes = null;

            // Seleciona coeficientes cadastrados.
            try {
                if (mostraAtivo) {
                    coeficientes = simulacaoController.getCoeficienteAtivo(csa_codigo, svc_codigo, (short) -1, new BigDecimal(0), new BigDecimal(0), responsavel);
                } else if (tipoMensal) {
                    coeficientes = simulacaoController.getCoeficienteMensal(csa_codigo, svc_codigo, -1, responsavel);
                } else {
                    coeficientes = simulacaoController.getCoeficienteDiario(csa_codigo, svc_codigo, -1, responsavel);
                }
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                coeficientes = new ArrayList<>();
            }

            // Seleciona as taxas fixas
            BigDecimal tacBd = new BigDecimal("0");
            BigDecimal opBd = new BigDecimal("0");
            String tac = "", op = "";
            if (!temCET) {
                try {
                    List<String> tpsCodigos = new ArrayList<>();
                    tpsCodigos.add(CodedValues.TPS_TAC_FINANCIADA);
                    tpsCodigos.add(CodedValues.TPS_OP_FINANCIADA);
                    List<TransferObject> parametros = parametroController.selectParamSvcCsa(svc_codigo, csa_codigo, tpsCodigos, mostraAtivo, responsavel);
                    Iterator<TransferObject> it2 = parametros.iterator();
                    TransferObject next = null;
                    Object data_ini_vig = null;
                    while (it2.hasNext()) {
                        next = it2.next();
                        if (next.getAttribute(Columns.TPS_CODIGO).equals(CodedValues.TPS_TAC_FINANCIADA)) {
                            tac = next.getAttribute(Columns.PSC_VLR).toString();
                            try {
                                tacBd = new BigDecimal(tac);
                                tac = NumberHelper.reformat(tac, "en", NumberHelper.getLang());
                            } catch (java.text.ParseException ex2) {
                            }
                        }
                        if (next.getAttribute(Columns.TPS_CODIGO).equals(CodedValues.TPS_OP_FINANCIADA)) {
                            op = next.getAttribute(Columns.PSC_VLR).toString();
                            try {
                                opBd = new BigDecimal(op);
                                op = NumberHelper.reformat(op, "en", NumberHelper.getLang());
                            } catch (java.text.ParseException ex2) {
                            }
                        }

                        data_ini_vig = next.getAttribute(Columns.PSC_DATA_INI_VIG);
                        podeAtivarPsc = podeAtivarPsc && (data_ini_vig == null);
                        pscAtivo = pscAtivo && (data_ini_vig != null);
                    }
                } catch (Exception ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                }
                podeAtivarPsc = podeAtivarPsc && !(tac.equals("") || op.equals(""));
                pscAtivo = pscAtivo && !(tac.equals("") || op.equals(""));
            }

            boolean ocultarCamposTac = ParamSist.getInstance().getParam(CodedValues.TPC_OCULTAR_CAMPOS_TAC, responsavel) != null && ParamSist.getInstance().getParam(CodedValues.TPC_OCULTAR_CAMPOS_TAC, responsavel).toString().equals(CodedValues.TPC_SIM);

            boolean podeAtivar = ((podeAtivarCft && podeAtivarPsc) || (podeAtivarCft && pscAtivo) || (podeAtivarPsc && cftAtivo));

            model.addAttribute("mostraAtivo", mostraAtivo);
            model.addAttribute("temCET", temCET);
            model.addAttribute("ocultarCamposTac", ocultarCamposTac);
            model.addAttribute("acao", acao);
            model.addAttribute("svc_descricao", svc_descricao);
            model.addAttribute("titulo", titulo);
            model.addAttribute("tac", tac);
            model.addAttribute("op", op);
            model.addAttribute("tipoMensal", tipoMensal);
            model.addAttribute("prazos", prazos);
            model.addAttribute("minDia", minDia);
            model.addAttribute("maxDia", maxDia);
            model.addAttribute("coeficientes", coeficientes);
            model.addAttribute("tacBd", tacBd);
            model.addAttribute("opBd", opBd);
            model.addAttribute("svc_codigo", svc_codigo);
            model.addAttribute("csa_codigo", csa_codigo);
            model.addAttribute("tipo", tipo);
            model.addAttribute("podeAtivar", podeAtivar);
            model.addAttribute("podeAtivarPsc", podeAtivarPsc);
            model.addAttribute("podeAtivarCft", podeAtivarCft);
            model.addAttribute("pscAtivo", pscAtivo);
            model.addAttribute("cftAtivo", cftAtivo);
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        return viewRedirect("jsp/manterCoeficiente/verificarCoeficiente", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=ativar" })
    public String ativar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws Exception {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String tipo = JspHelper.verificaVarQryStr(request, "tipo");
        boolean tipoMensal = tipo.equals("M");

        String csa_codigo = "";
        if (responsavel.isCsa()) {
            csa_codigo = responsavel.getCodigoEntidade();
        } else {
            csa_codigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
        }

        String svc_codigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");

        boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);

        // Compara os tokens de sincronização
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.coeficiente.ativados", responsavel));
        } else {

            if (JspHelper.verificaVarQryStr(request, "ATIVA_CFT").equals("1")) {
                // Ativa os coeficientes
                if (tipoMensal) {
                    ativarCoeficienteController.ativarCoeficienteMensal(csa_codigo, svc_codigo, responsavel);
                } else {
                    ativarCoeficienteController.ativarCoeficienteDiario(csa_codigo, svc_codigo, responsavel);
                }
            }

            if (!temCET && JspHelper.verificaVarQryStr(request, "ATIVA_PSC").equals("1")) {
                // Ativa as taxas
                List<String> tpsCodigos = new ArrayList<>();
                tpsCodigos.add(CodedValues.TPS_TAC_FINANCIADA);
                tpsCodigos.add(CodedValues.TPS_OP_FINANCIADA);
                parametroController.ativaParamSvcCsa(svc_codigo, csa_codigo, tpsCodigos, responsavel);
            }

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.coeficiente.ativados.sucesso", responsavel));
        }

        return iniciar(request, response, session, model);
    }
}
