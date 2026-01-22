package com.zetra.econsig.web.controller.orgao;

import java.util.ArrayList;
import java.util.HashSet;
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

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;
/**
 * <p>Title: ListarServicoOrgaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso listagem de servicos do orgao em Manutencao de Orgao.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarServicoOrgao" })
public class ListarServicoOrgaoWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarServicoOrgaoWebController.class);

    @Autowired
    private ConvenioController convenioController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {

            ParamSession paramSession = ParamSession.getParamSession(session);
            String csa_codigo = responsavel.getCsaCodigo();

            // Valida token
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String titulo = ApplicationResourcesHelper.getMessage("rotulo.listar.servico.orgao.titulo", responsavel);
            String subTitulo = JspHelper.verificaVarQryStr(request, "titulo");
            if (!subTitulo.equals("")) {
                titulo += " - " + subTitulo;
            }

            /* servicos */
            boolean podeEditarSvc = responsavel.temPermissao(CodedValues.FUN_EDT_SERVICOS);
            boolean podeConsultarSvc = responsavel.temPermissao(CodedValues.FUN_CONS_SERVICOS);
            /* convenio */
            boolean podeEditarCnv = responsavel.temPermissao(CodedValues.FUN_EDT_CONVENIOS);
            boolean podeConsultarCnv = responsavel.temPermissao(CodedValues.FUN_CONS_CONVENIOS);

            // Obtem o parametro codigo do orgao e define o link de cancelar.
            String org_codigo = "";
            String cancel = "";
            if (responsavel.isOrg()) {
                org_codigo = responsavel.getCodigoEntidade();
                cancel = "../v3/carregarPrincipal";
            } else if (request.getParameter("org") != null) {
                org_codigo = request.getParameter("org");
                cancel = SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request);
            }

            if (TextHelper.isNull(org_codigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            List<TransferObject> servicos = null;
            String parametros = "acao=iniciar" + "&org=" + org_codigo + "&titulo=" + subTitulo + "&" + SynchronizerToken.generateToken4URL(request);
            String linkRet = "../v3/listarServicoOrgao?" + parametros;

            String filtro = JspHelper.verificaVarQryStr(request, "FILTRO");
            int filtro_tipo = -1;
            try {
                filtro_tipo = Integer.parseInt(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO"));
            } catch (Exception ex1) {
            }

            try {
                CustomTransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.CNV_ORG_CODIGO, org_codigo);

                // Seta csa_codigo caso responsável seja consignatária
                if (responsavel.isCsa()) {
                    criterio.setAttribute(Columns.CNV_CSA_CODIGO, csa_codigo);
                }

                // -------------- Seta Criterio da Listagem ------------------
                // Bloqueado
                if (filtro_tipo == 0) {
                    criterio.setAttribute(Columns.CNV_SCV_CODIGO, CodedValues.SCV_INATIVO);
                    // Desbloqueado
                } else if (filtro_tipo == 1) {
                    criterio.setAttribute(Columns.CNV_SCV_CODIGO, CodedValues.NOT_EQUAL_KEY + CodedValues.SCV_INATIVO);
                    // Outros
                } else if (!filtro.equals("") && filtro_tipo != -1) {
                    String campo = null;

                    switch (filtro_tipo) {
                        case 2:
                            campo = Columns.SVC_IDENTIFICADOR;
                            break;
                        case 3:
                            campo = Columns.SVC_DESCRICAO;
                            break;
                        default:
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }

                    criterio.setAttribute(campo, CodedValues.LIKE_MULTIPLO + filtro + CodedValues.LIKE_MULTIPLO);
                }
                // ---------------------------------------

                int total = convenioController.countCnvScvCodigo(criterio, responsavel);
                int size = JspHelper.LIMITE;
                int offset = 0;
                try {
                    offset = Integer.parseInt(request.getParameter("offset"));
                } catch (Exception ex) {
                }

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

                servicos = convenioController.listCnvScvCodigo(criterio, offset, size, responsavel);

                configurarPaginador(linkRet, "rotulo.convenio.manutencao.titulo", total, size, requestParams, false, request, model);
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                servicos = new ArrayList<>();
            }

            model.addAttribute("titulo", titulo);
            model.addAttribute("parametros", parametros);
            model.addAttribute("filtro_tipo", filtro_tipo);
            model.addAttribute("podeEditarSvc", podeEditarSvc);
            model.addAttribute("podeConsultarSvc", podeConsultarSvc);
            model.addAttribute("podeEditarCnv", podeEditarCnv);
            model.addAttribute("podeConsultarCnv", podeConsultarCnv);
            model.addAttribute("org_codigo", org_codigo);
            model.addAttribute("subTitulo", subTitulo);
            model.addAttribute("servicos", servicos);
            model.addAttribute("cancel", cancel);
            model.addAttribute("filtro", filtro);

        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        return viewRedirect("jsp/manterOrgao/listarServicoOrgao", request, session, model, responsavel);
    }
}
