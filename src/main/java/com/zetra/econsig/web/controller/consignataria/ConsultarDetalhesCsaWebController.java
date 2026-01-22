package com.zetra.econsig.web.controller.consignataria;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.helper.web.v3.JspHelper;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ConsultarDetalhesCsaWebController</p>
 * <p>Description: Controlador Web para o caso de uso Consultar Detalhes de Consignatárias.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/consultarDetalhesCsa" })
public class ConsultarDetalhesCsaWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AbstractWebController.class);

    @Autowired
    private ConsignatariaController consignatariaController;

    @RequestMapping(params = { "acao=listar" })
    public String listarConsignataria(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            boolean podeConsultarCsa = responsavel.temPermissao(CodedValues.FUN_SER_CONS_CONSIGNATARIA);

            int filtroTipo = -1;
            if (JspHelper.verificaVarQryStr(request, "filtroTipo") != null) {
                if (!JspHelper.verificaVarQryStr(request, "filtroTipo").isEmpty()) {
                    filtroTipo = Integer.parseInt(JspHelper.verificaVarQryStr(request, "filtroTipo"));
                }
            }

            String filtro = filtroTipo == 4 ? request.getParameter("NCA_CODIGO") : JspHelper.verificaVarQryStr(request, "FILTRO");
            String filtroAlfabeto = JspHelper.verificaVarQryStr(request, "filtroAlfabeto");
            String ncaCodigo = "";

            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.NCA_EXIBE_SER, CodedValues.TPC_SIM);

            if (!filtro.equals("") && filtroTipo != -1) {
                String campo = null;

                switch (filtroTipo) {
                    case 2:
                        campo = Columns.CSA_IDENTIFICADOR;
                        break;
                    case 3:
                        campo = Columns.CSA_NOME + CodedValues.OR_KEY + Columns.CSA_NOME_ABREV;
                        break;
                    case 4:
                        campo = Columns.CSA_NCA_NATUREZA;
                        ncaCodigo = filtro;
                        break;
                    default:
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                criterio.setAttribute(campo, CodedValues.LIKE_MULTIPLO + filtro + CodedValues.LIKE_MULTIPLO);
            }

            if (!filtroAlfabeto.equals("")) {
                criterio.setAttribute(Columns.CSA_NOME, filtroAlfabeto + CodedValues.LIKE_MULTIPLO);
            }

            int total = consignatariaController.countConsignatarias(criterio, responsavel);
            int size = JspHelper.LIMITE;
            int offset = 0;
            if (request.getParameter("offset") != null) {
                if (!request.getParameter("offset").isEmpty()) {
                    offset = Integer.parseInt(request.getParameter("offset"));
                }
            }

            List<TransferObject> consignatarias = consignatariaController.lstConsignatarias(criterio, offset, size, responsavel);
            List<TransferObject> lstNatureza = consignatariaController.lstNatureza();

            configurarPaginador("../v3/consultarDetalhesCsa?acao=listar&FILTRO=" + filtro + "&filtroTipo=" + filtroTipo + "&NCA_CODIGO=" + ncaCodigo, "rotulo.listar.consignataria.titulo", total, size, null, false, request, model);

            // Seta atributos no model
            model.addAttribute("responsavel", responsavel);
            model.addAttribute("podeConsultarCsa", podeConsultarCsa);
            model.addAttribute("consignatarias", ManterConsignatariaWebController.converterListaDtoConsignataria(consignatarias, false, false, responsavel));
            model.addAttribute("filtro", filtro);
            model.addAttribute("filtroAlfabeto", filtroAlfabeto);
            model.addAttribute("filtroTipo", filtroTipo);
            model.addAttribute("lstNatureza", lstNatureza);
            model.addAttribute("ncaCodigoSelecionado", ncaCodigo);

        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }
        return viewRedirect("jsp/consultarDetalhesCsa/listarConsignataria", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=visualizar" })
    public String visualizar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            ParamSession paramSession = ParamSession.getParamSession(session);

            String csaCodigo = responsavel.isCsa() ? responsavel.getCodigoEntidade() : JspHelper.verificaVarQryStr(request, "csa");
            String csaNome = "";

            ConsignatariaTransferObject consignataria = null;
            try {
                if (!TextHelper.isNull(csaCodigo)) {
                    consignataria = consignatariaController.findConsignataria(csaCodigo, responsavel);
                    csaNome = consignataria != null ? consignataria.getCsaNomeAbreviado() : "";
                    if (csaNome == null || csaNome.isBlank()) {
                        csaNome = consignataria != null ? consignataria.getCsaNome() : "";
                    }
                }
            } catch (Exception ex) {
            }
            String btnVoltar = SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request);

            // Seta atributos no model
            model.addAttribute("btnVoltar", btnVoltar);
            model.addAttribute("consignataria", consignataria);
            model.addAttribute("csaNome", csaNome);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/consultarDetalhesCsa/visualizarConsignataria", request, session, model, responsavel);
    }
}
