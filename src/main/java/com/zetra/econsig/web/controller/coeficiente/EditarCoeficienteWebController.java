package com.zetra.econsig.web.controller.coeficiente;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.PrazoTransferObject;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.exception.SimulacaoControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.coeficiente.SetarCoeficienteController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: EditarCoeficienteWebController</p>
 * <p>Description: Controlador Web para o caso de uso Editar Coeficiente.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
public class EditarCoeficienteWebController extends AbstractWebController {

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private SetarCoeficienteController setarCoeficienteController;

    @Autowired
    private SimulacaoController simulacaoController;

    @Autowired
    private ServicoController servicoController;

    @RequestMapping(value = "/v3/editarCoeficiente")
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConvenioControllerException, SimulacaoControllerException, ParametroControllerException, ServicoControllerException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

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

        boolean tipoMensal = tipo.equals("M");

        //int fator = tipoMensal ? 0 : 1;
        int minDia = tipoMensal ? 0 : 1;
        int maxDia = tipoMensal ? 0 : 31;

        List<TransferObject> coeficientes = null;

        // Seleciona coeficientes cadastrados.
        try {
            if (tipoMensal) {
                coeficientes = simulacaoController.getCoeficienteMensal(csa_codigo, svc_codigo, -1, true, responsavel);
            } else {
                coeficientes = simulacaoController.getCoeficienteDiario(csa_codigo, svc_codigo, -1, true, responsavel);
            }
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            coeficientes = new ArrayList<>();
        }

        // Seleciona as taxas fixas
        String tac = "", op = "";
        try {
            List<String> tpsCodigos = new ArrayList<>();
            tpsCodigos.add(CodedValues.TPS_TAC_FINANCIADA);
            tpsCodigos.add(CodedValues.TPS_OP_FINANCIADA);
            List<TransferObject> parametros = parametroController.selectParamSvcCsa(svc_codigo, csa_codigo, tpsCodigos, false, responsavel);
            Iterator<TransferObject> it2 = parametros.iterator();
            TransferObject next = null;
            while (it2.hasNext()) {
                next = it2.next();
                if (next.getAttribute(Columns.TPS_CODIGO).equals(CodedValues.TPS_TAC_FINANCIADA)) {
                    tac = next.getAttribute(Columns.PSC_VLR).toString();
                    try {
                        tac = NumberHelper.reformat(tac, "en", NumberHelper.getLang());
                    } catch (java.text.ParseException ex2) {
                    }
                }
                if (next.getAttribute(Columns.TPS_CODIGO).equals(CodedValues.TPS_OP_FINANCIADA)) {
                    op = next.getAttribute(Columns.PSC_VLR).toString();
                    try {
                        op = NumberHelper.reformat(op, "en", NumberHelper.getLang());
                    } catch (java.text.ParseException ex2) {
                    }
                }
            }
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        boolean editar = (responsavel.temPermissao(CodedValues.FUN_EDT_COEFICIENTES) || responsavel.temPermissao(CodedValues.FUN_TAXA_JUROS));

        Map<String, String> svcBloqEdicaoTaxas = simulacaoController.getSvcCadTaxaBloqueado(responsavel);
        editar = editar && !svcBloqEdicaoTaxas.containsKey(svc_codigo);

        boolean readOnly = editar ? false : true;

        boolean ocultarCamposTac = ParamSist.getInstance().getParam(CodedValues.TPC_OCULTAR_CAMPOS_TAC, responsavel) != null && ParamSist.getInstance().getParam(CodedValues.TPC_OCULTAR_CAMPOS_TAC, responsavel).toString().equals(CodedValues.TPC_SIM);

        String svcDescricao = null;
        if (!editar && svcBloqEdicaoTaxas.containsKey(svc_codigo)) {
            CustomTransferObject sto = servicoController.findServico(svcBloqEdicaoTaxas.get(svc_codigo));
            svcDescricao = (sto != null) ? (String) sto.getAttribute(Columns.SVC_DESCRICAO) : "";
        }

        model.addAttribute("editar", editar);
        model.addAttribute("svcBloqEdicaoTaxas", svcBloqEdicaoTaxas);
        model.addAttribute("titulo", titulo);
        model.addAttribute("svc_codigo", svc_codigo);
        model.addAttribute("temCET", temCET);
        model.addAttribute("ocultarCamposTac", ocultarCamposTac);
        model.addAttribute("tac", tac);
        model.addAttribute("readOnly", readOnly);
        model.addAttribute("op", op);
        model.addAttribute("tipoMensal", tipoMensal);
        model.addAttribute("prazos", prazos);
        model.addAttribute("minDia", minDia);
        model.addAttribute("maxDia", maxDia);
        model.addAttribute("coeficientes", coeficientes);
        model.addAttribute("csa_codigo", csa_codigo);
        model.addAttribute("tipo", tipo);
        model.addAttribute("svc_descricao", svc_descricao);
        model.addAttribute("svcDescricao", svcDescricao);

        return viewRedirect("jsp/manterCoeficiente/editarCoeficiente", request, session, model, responsavel);
    }

    @RequestMapping(value = "/v3/editarCoeficiente", params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConvenioControllerException, SimulacaoControllerException, ParametroControllerException, ServicoControllerException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String svc_codigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
        String svc_descricao = JspHelper.verificaVarQryStr(request, "SVC_DESCRICAO");

        String tipo = JspHelper.verificaVarQryStr(request, "tipo");
        String titulo = JspHelper.verificaVarQryStr(request, "titulo");

        boolean tipoMensal = tipo.equals("M");

        int minDia = tipoMensal ? 0 : 1;
        int maxDia = tipoMensal ? 0 : 31;

        List<TransferObject> coeficientes = null;
        CustomTransferObject cto = null;
        String nome_campo;
        String cft_vlr;
        Short prz_vlr;

        PrazoTransferObject pto = null;
        Iterator<PrazoTransferObject> it = null;

        List<PrazoTransferObject> prazos = null;
        // Seleciona prazos ativos.

        String csa_codigo = "";
        if (responsavel.isCsa()) {
            csa_codigo = responsavel.getCodigoEntidade();
        } else {
            csa_codigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
        }

        try {
            prazos = simulacaoController.findPrazoCsaByServico(svc_codigo, csa_codigo, responsavel);
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Compara os tokens de sincronização
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.coeficiente.salvos", responsavel));
        } else {
            try {
                if (JspHelper.verificaVarQryStr(request, "ALTERA_CFT").equals("1")) {
                    // Salva os coeficientes
                    coeficientes = new ArrayList<>();
                    for (int i = minDia; i <= maxDia; i++) {
                        it = prazos.iterator();
                        while (it.hasNext()) {
                            pto = it.next();
                            prz_vlr = pto.getPrzVlr();
                            nome_campo = i + "_" + prz_vlr;
                            cft_vlr = JspHelper.verificaVarQryStr(request, "cft_" + nome_campo);
                            if (!cft_vlr.equals("")) {
                                cto = new CustomTransferObject();
                                cto.setAttribute(Columns.PZC_CSA_CODIGO, csa_codigo);
                                cto.setAttribute(Columns.PRZ_SVC_CODIGO, svc_codigo);
                                cto.setAttribute(Columns.CFT_DIA, String.valueOf(i));
                                cto.setAttribute(Columns.CFT_VLR, cft_vlr);
                                cto.setAttribute(Columns.PRZ_VLR, prz_vlr);
                                cto.setAttribute(Columns.CFT_CODIGO, JspHelper.verificaVarQryStr(request, "cft_codigo_" + nome_campo));
                                coeficientes.add(cto);
                            }
                        }
                    }
                    if (tipoMensal) {
                        setarCoeficienteController.setarCoeficienteMensal(coeficientes, responsavel);
                    } else {
                        setarCoeficienteController.setarCoeficienteDiario(coeficientes, responsavel);
                    }
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.coeficiente.salvos.sucesso", responsavel));
                }

                if (JspHelper.verificaVarQryStr(request, "ALTERA_PSC").equals("1")) {
                    // Salva as taxas fixas (paramSvcCsa)
                    List<String> tpsCodigos = new ArrayList<>();
                    tpsCodigos.add(CodedValues.TPS_TAC_FINANCIADA);
                    tpsCodigos.add(CodedValues.TPS_OP_FINANCIADA);
                    List<TransferObject> param = new ArrayList<>();
                    String psc_vlr = null;
                    for (int i = 0; i < tpsCodigos.size(); i++) {
                        cto = new CustomTransferObject();
                        psc_vlr = JspHelper.verificaVarQryStr(request, "tps_" + tpsCodigos.get(i));
                        if (!psc_vlr.equals("")) {
                            psc_vlr = NumberHelper.reformat(psc_vlr, NumberHelper.getLang(), "en", 2, 2);
                            cto.setAttribute(Columns.TPS_CODIGO, tpsCodigos.get(i));
                            cto.setAttribute(Columns.PSC_SVC_CODIGO, svc_codigo);
                            cto.setAttribute(Columns.PSC_CSA_CODIGO, csa_codigo);
                            cto.setAttribute(Columns.PSC_VLR, psc_vlr);
                            param.add(cto);
                        }
                    }
                    parametroController.updateParamSvcCsa(param, responsavel);
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.coeficiente.salvos.sucesso", responsavel));
                }

                // Ativa coeficientes.
                String linkAtiva = "../v3/verificarCoeficiente?acao=editarCoeficiente&CSA_CODIGO=" + csa_codigo + "&titulo=" + titulo + "&SVC_CODIGO=" + svc_codigo + "&SVC_DESCRICAO=" + svc_descricao + "&tipo=" + tipo + "&OPERACAO=ATIVAR" // &MM_update=true";
                        + "&" + SynchronizerToken.generateToken4URL(request);

                request.setAttribute("url64", TextHelper.encode64(linkAtiva));
                return "jsp/redirecionador/redirecionar";

            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }
        }

        return iniciar(request, response, session, model);
    }
}
