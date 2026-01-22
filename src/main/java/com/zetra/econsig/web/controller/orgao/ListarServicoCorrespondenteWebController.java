package com.zetra.econsig.web.controller.orgao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ListarServicoCorrespondenteWebController</p>
 * <p>Description: Controlador Web para o caso de uso Manutencao de Orgao.</p>
 * <p>Copyright: Copyright (c) 2002-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Igor Lucas, Leonel Martins
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarServicoCorrespondente" })
public class ListarServicoCorrespondenteWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarServicoCorrespondenteWebController.class);

    @Autowired
    private ConvenioController convenioController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            final ParamSession paramSession = new ParamSession();

            // Valida o token
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            final StringBuilder titulo = new StringBuilder().append(ApplicationResourcesHelper.getMessage("rotulo.lista.servico.titulo", responsavel));
            final String subTitulo = JspHelper.verificaVarQryStr(request, "titulo");
            if (!subTitulo.equals("")) {
                titulo.append(" - ").append(subTitulo);
            }

            /* servico */
            final boolean podeEditarCnvCor = responsavel.temPermissao(CodedValues.FUN_EDT_CONV_CORRESPONDENTE);
            final boolean podeConsultarCnvCor = responsavel.temPermissao(CodedValues.FUN_CONS_CONV_CORRESPONDENTE);

            // Obtem o parametro codigo do orgao e define o link de cancelar.
            String orgCodigo = "";
            String cancel = "";
            if (responsavel.isOrg()) {
                orgCodigo = responsavel.getCodigoEntidade();
                cancel = "../v3/carregarPrincipal";
            } else if (request.getParameter("org") != null) {
                orgCodigo = request.getParameter("org");
                cancel = SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request);
            }

            if (TextHelper.isNull(orgCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Obtem o parametro codigo da consignataria e define o link de cancelar.
            String csaCodigo = JspHelper.verificaVarQryStr(request, "csa");
            if (responsavel.isCsa()) {
                csaCodigo = responsavel.getCodigoEntidade();
            }

            final List<TransferObject> servicos = convenioController.listCnvCorrespondenteByCsa(csaCodigo, orgCodigo, responsavel);

            model.addAttribute("titulo",titulo.toString());
            model.addAttribute("subTitulo",subTitulo);
            model.addAttribute("csa_codigo",csaCodigo);
            model.addAttribute("podeEditarCnvCor",podeEditarCnvCor);
            model.addAttribute("podeConsultarCnvCor",podeConsultarCnvCor);
            model.addAttribute("servicos",servicos);
            model.addAttribute("org_codigo",orgCodigo);
            model.addAttribute("cancel", cancel);

        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        return viewRedirect("jsp/manterOrgao/listarServicoCorrespondente", request, session, model, responsavel);
    }
}
