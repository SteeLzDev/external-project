package com.zetra.econsig.web.controller.orgao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;
/**
 * <p>Title: ListarServicoConveniosWebController</p>
 * <p>Description: Controlador Web para o caso de uso Manutencao de Orgao.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarServicoConvenios" })
public class ListarServicoConveniosWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarServicoConveniosWebController.class);

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private ConvenioController convenioController;

    @RequestMapping(params = { "acao=consultarConvenio" })
    public String consultarOrgao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=editarConvenio" })
    public String editarOrgao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
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

            SynchronizerToken.saveToken(request);

            boolean podeEditarCnvCor = responsavel.temPermissao(CodedValues.FUN_EDT_CONV_CORRESPONDENTE);
            String org_codigo = JspHelper.verificaVarQryStr(request, "org_codigo");
            OrgaoTransferObject orgao = consignanteController.findOrgao(org_codigo, responsavel);
            String org_nome = orgao.getOrgNome();

            String csa_codigo = responsavel.getCsaCodigo();

            String svc_codigo = JspHelper.verificaVarQryStr(request, "svc_codigo");
            CustomTransferObject svcTO = convenioController.findServico(svc_codigo, responsavel);
            String svc_descricao = (String) svcTO.getAttribute(Columns.SVC_DESCRICAO);
            String svc_identificador = (String) svcTO.getAttribute(Columns.SVC_IDENTIFICADOR);

            String subTitulo = (!svc_descricao.equals("") && !svc_identificador.equals("")) ? svc_identificador + " - " + svc_descricao : svc_identificador + svc_descricao;

            String cnv_codigo = null;
            try {
                List<TransferObject> lstCnv = convenioController.lstConvenios(null, csa_codigo, svc_codigo, org_codigo, true, responsavel);
                if (lstCnv == null || lstCnv.isEmpty()) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
                CustomTransferObject to = (CustomTransferObject) lstCnv.get(0);
                cnv_codigo = (String) to.getAttribute(Columns.CNV_CODIGO);
            } catch (ConvenioControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            // Salva os convênios
            if (podeEditarCnvCor && !JspHelper.verificaVarQryStr(request, "MM_update").equals("")) {
                try {
                    // Cria os convênios especificados
                    if (!csa_codigo.equals("")) {
                        List<String> corCodigos = request.getParameterValues("COR_CODIGO") != null ? Arrays.asList(request.getParameterValues("COR_CODIGO")) : null;
                        convenioController.criaConvenioCorrespondente(corCodigos, cnv_codigo, responsavel);
                        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.convenio.alteracoes.sucesso", responsavel));
                    }
                } catch (ConvenioControllerException ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                }
            }

            // Busca os Convênios
            List<TransferObject> convenios = null;
            try {
                convenios = convenioController.listCorCnvOrgao(cnv_codigo, responsavel);
            } catch (ConvenioControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                convenios = new ArrayList<>();
            }

            model.addAttribute("svc_codigo",svc_codigo);
            model.addAttribute("csa_codigo", csa_codigo);
            model.addAttribute("org_codigo", org_codigo);
            model.addAttribute("org_nome",org_nome);
            model.addAttribute("subTitulo", subTitulo);
            model.addAttribute("convenios", convenios);
            model.addAttribute("podeEditarCnvCor", podeEditarCnvCor);

        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/manterOrgao/listarServicoConvenios", request, session, model, responsavel);
    }

}
