package com.zetra.econsig.web.controller.desconto;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
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
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PlanoDescontoControllerException;
import com.zetra.econsig.exception.RelatorioControllerException;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.sdp.PlanoDescontoController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ConsultarPlanoDescontoWebController</p>
 * <p>Description: Controlador Web para caso de uso de manutençao de plano de desconto</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: anderson.assis $
 * $Revision: 25329 $
 * $Date: 2020-06-30 18:15:21 -0300 (Ter, 30 jun 2020) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/consultarPlanoDesconto" })
public class ConsultarPlanoDescontoWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConsultarPlanoDescontoWebController.class);

    @Autowired
    private PlanoDescontoController planoDescontoController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private ServicoController servicoController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, RelatorioControllerException, PlanoDescontoControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        SynchronizerToken.saveToken(request);

        boolean podeEditarPla = responsavel.temPermissao(CodedValues.FUN_EDT_PLANO_DESCONTO);
        boolean podeExcluirPla = responsavel.temPermissao(CodedValues.FUN_EXCLUIR_PLANO_DESCONTO);
        boolean podeConsultarPla = responsavel.temPermissao(CodedValues.FUN_CONSULTAR_PLANO_DESCONTO);

        String filtro = JspHelper.verificaVarQryStr(request, "FILTRO");
        int filtro_tipo = -1;
        try {
            filtro_tipo = Integer.parseInt(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO"));
        } catch (Exception ex1) {
        }

        List<?> planos = null;
        try {
            CustomTransferObject criterio = new CustomTransferObject();

            // -------------- Seta Criterio da Listagem ------------------
            // Bloqueado
            if (filtro_tipo == 0) {
                criterio.setAttribute(Columns.PLA_ATIVO, CodedValues.STS_INATIVO);
                // Desbloqueado
            } else if (filtro_tipo == 1) {
                criterio.setAttribute(Columns.PLA_ATIVO, CodedValues.STS_ATIVO);
                // Outros
            } else if (filtro_tipo != -1) {
                String campo = null;

                switch (filtro_tipo) {
                    case 2:
                        campo = Columns.PLA_IDENTIFICADOR;
                        break;
                    case 3:
                        campo = Columns.PLA_DESCRICAO;
                        break;
                    default:
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel) + ".");
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                criterio.setAttribute(campo, CodedValues.LIKE_MULTIPLO + filtro + CodedValues.LIKE_MULTIPLO);
            }

            // ---------------------------------------

            criterio.setAttribute(Columns.CSA_CODIGO, responsavel.getCsaCodigo());
            int total = planoDescontoController.countPlanosDesconto(criterio, responsavel);
            int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (Exception ex) {
            }

            criterio.setAttribute(Columns.CSA_CODIGO, responsavel.getCsaCodigo());
            planos = planoDescontoController.lstPlanoDesconto(criterio, offset, size, responsavel);

            String link = "../v3/consultarPlanoDesconto?acao=iniciar";
            configurarPaginador(link, "rotulo.paginacao.titulo.consignataria", total, size, null, false, request, model);
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            planos = new ArrayList<>();
        }

        model.addAttribute("podeEditarPla", podeEditarPla);
        model.addAttribute("podeExcluirPla", podeExcluirPla);
        model.addAttribute("podeConsultarPla", podeConsultarPla);
        model.addAttribute("filtro", filtro);
        model.addAttribute("filtro_tipo", filtro_tipo);
        model.addAttribute("planos", planos);

        return viewRedirect("jsp/consultarPlanoDesconto/listarPlanoDesconto", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=editar" })
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws RelatorioControllerException, PlanoDescontoControllerException, InstantiationException, IllegalAccessException, ServletException, IOException, ServicoControllerException, ParseException {
        return abrirTelaDeEdicaoOuConsulta(request, response, session, model);
    }

    @RequestMapping(params = { "acao=consultar" })
    public String consultar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws RelatorioControllerException, PlanoDescontoControllerException, InstantiationException, IllegalAccessException, ServletException, IOException, ServicoControllerException, ParseException {
        return abrirTelaDeEdicaoOuConsulta(request, response, session, model);
    }

    private String abrirTelaDeEdicaoOuConsulta(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, RelatorioControllerException, PlanoDescontoControllerException, InstantiationException, IllegalAccessException, ServicoControllerException, ParseException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        boolean podeEditarPla = responsavel.temPermissao(CodedValues.FUN_EDT_PLANO_DESCONTO);
        boolean podeExcluirPla = responsavel.temPermissao(CodedValues.FUN_EXCLUIR_PLANO_DESCONTO);
        boolean podeConsultarPla = responsavel.temPermissao(CodedValues.FUN_CONSULTAR_PLANO_DESCONTO);
        String plaCodigo = JspHelper.verificaVarQryStr(request, "plaCodigo");
        String plaDescricao = "";
        String plaIdentificador = "";
        String svcCodigo = JspHelper.verificaVarQryStr(request, "svcCodigo");
        String tipo = JspHelper.verificaVarQryStr(request, "tipo");
        String nplCodigo = JspHelper.verificaVarQryStr(request, "nplCodigo");
        String csaCodigo = responsavel.isCsa() ? responsavel.getCodigoEntidade() : responsavel.isCor() ? responsavel.getCodigoEntidadePai() : null;
        String reqColumnsStr = "plaIdentificador|plaDescricao|svcCodigo|nplCodigo";
        String msgErro = JspHelper.verificaCamposForm(request, session, reqColumnsStr, ApplicationResourcesHelper.getMessage("mensagem.campos.obrigatorios", responsavel), "100%");
        String svcCodigoPg = "";
        String nplCodigoPg = "";
        Short plaAtivo = Short.valueOf("1");
        ParamSession paramSession = ParamSession.getParamSession(session);

        List<TransferObject> lstSvc = servicoController.selectServicosCsa(csaCodigo, responsavel);
        List<TransferObject> lstNpl = planoDescontoController.lstNaturezasPlanos();

        if (!TextHelper.isNull(tipo) && !tipo.isEmpty() && tipo.equals("salvar") && msgErro.length() == 0 && podeEditarPla) {
            TransferObject planoDesconto = new CustomTransferObject();

            try {
                if (plaCodigo != null && !plaCodigo.equals("")) {
                    planoDesconto.setAttribute(Columns.PLA_CODIGO, plaCodigo);
                } else {
                    planoDesconto.setAttribute(Columns.PLA_ATIVO, CodedValues.STS_ATIVO);
                }
                String plaIdnPg = JspHelper.verificaVarQryStr(request, "plaIdentificador");
                String plaDescPg = JspHelper.verificaVarQryStr(request, "plaDescricao");
                svcCodigoPg = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "svcCodigo")) ? JspHelper.verificaVarQryStr(request, "svcCodigo") : JspHelper.verificaVarQryStr(request, "svcCodigoHidden");
                nplCodigoPg = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "nplCodigo")) ? JspHelper.verificaVarQryStr(request, "nplCodigo") : JspHelper.verificaVarQryStr(request, "nplCodigoHidden");

                if (!TextHelper.isNull(plaIdnPg)) {
                    planoDesconto.setAttribute(Columns.PLA_IDENTIFICADOR, plaIdnPg);
                }
                if (!TextHelper.isNull(plaDescPg)) {
                    planoDesconto.setAttribute(Columns.PLA_DESCRICAO, plaDescPg);
                }
                if (!TextHelper.isNull(svcCodigoPg)) {
                    planoDesconto.setAttribute(Columns.SVC_CODIGO, svcCodigoPg);
                }
                if (request.getParameter("MM_insert") != null) {
                    planoDesconto.setAttribute(Columns.CSA_CODIGO, csaCodigo);
                }

                if (!TextHelper.isNull(nplCodigoPg)) {
                    planoDesconto.setAttribute(Columns.NPL_CODIGO, nplCodigoPg);
                }

                Enumeration<?> reqstNames = request.getParameterNames();
                List<TransferObject> paramPlanoList = new ArrayList<>();
                while (reqstNames.hasMoreElements()) {
                    String paramName = (String) reqstNames.nextElement();
                    if (paramName.startsWith("tpp_")) {
                        String paramKey = paramName.substring(4);
                        String paramValue = request.getParameter(paramName);

                        CustomTransferObject paramTO = new CustomTransferObject();
                        paramTO.setAttribute(Columns.TPP_CODIGO, paramKey);
                        paramTO.setAttribute(Columns.PPL_VALOR, paramValue);
                        paramPlanoList.add(paramTO);
                    }
                }
                if (!TextHelper.isNull(plaCodigo)) {
                    planoDescontoController.updatePlanoDesconto(planoDesconto, paramPlanoList, responsavel);
                } else {
                    //Criando novo plano de desconto.
                    plaCodigo = planoDescontoController.createPlanoDesconto(planoDesconto, paramPlanoList, responsavel);
                    //Colocando um endereço no paramSession
                    Map<String, String[]> parametros = new HashMap<>();
                    parametros.put("plaCodigo", new String[] { csaCodigo });
                    String link = request.getRequestURI();
                    paramSession.addHistory(link, parametros);
                }
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.salvar.plano.sucesso", responsavel));

            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }
        }

        TransferObject planoDesconto = null;

        if (!TextHelper.isNull(plaCodigo)) {
            TransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.PLA_CODIGO, plaCodigo);
            planoDesconto = planoDescontoController.findPlanoDesconto(criterio, responsavel);

            plaDescricao = (String) planoDesconto.getAttribute(Columns.PLA_DESCRICAO);
            plaIdentificador = (String) planoDesconto.getAttribute(Columns.PLA_IDENTIFICADOR);
            plaAtivo = (Short) planoDesconto.getAttribute(Columns.PLA_ATIVO);

            svcCodigo = TextHelper.isNull(svcCodigo) ? svcCodigoPg : svcCodigo;
            nplCodigo = TextHelper.isNull(nplCodigo) ? nplCodigoPg : nplCodigo;
        }

        //recupera os valores de plano de desconto
        List<TransferObject> lstParamPla = null;
        HashMap<Object, Object> hshParamPlano = new HashMap<>();
        String tppIndice = null;
        String strMaxPrazo = null;
        if (!TextHelper.isNull(plaCodigo)) {
            try {
                lstParamPla = parametroController.selectParamPlano(plaCodigo, responsavel);

                Iterator<TransferObject> itParam = lstParamPla.iterator();
                while (itParam.hasNext()) {
                    CustomTransferObject ctoParam = (CustomTransferObject) itParam.next();

                    String paramName = (String) ctoParam.getAttribute(Columns.TPP_CODIGO);
                    String paramVlr = (String) ctoParam.getAttribute(Columns.PPL_VALOR);

                    hshParamPlano.put(paramName, paramVlr);
                }
                tppIndice = (String) hshParamPlano.get(CodedValues.TPP_INDICE_PLANO);
                strMaxPrazo = (String) hshParamPlano.get(CodedValues.TPP_PRAZO_MAX_PLANO);
            } catch (ParametroControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                lstParamPla = new ArrayList<>();
            }
        }
        int intMaxPrazo = (!TextHelper.isNull(strMaxPrazo)) ? Integer.parseInt(strMaxPrazo) : -1;
        String ade_vlr = (hshParamPlano.get(CodedValues.TPP_VLR_PLANO) != null && !hshParamPlano.get(CodedValues.TPP_VLR_PLANO).equals("") ? NumberHelper.reformat(hshParamPlano.get(CodedValues.TPP_VLR_PLANO).toString(), "en", NumberHelper.getLang()) : "");

        String tituloPagina = TextHelper.isNull(plaCodigo) ? "rotulo.incluir.plano.titulo" : "rotulo.editar.plano.titulo";
        tituloPagina = ApplicationResourcesHelper.getMessage(tituloPagina, responsavel);

        model.addAttribute("podeEditarPla", podeEditarPla);
        model.addAttribute("podeExcluirPla", podeExcluirPla);
        model.addAttribute("podeConsultarPla", podeConsultarPla);
        model.addAttribute("plaCodigo", plaCodigo);
        model.addAttribute("plaDescricao", plaDescricao);
        model.addAttribute("plaIdentificador", plaIdentificador);
        model.addAttribute("plaAtivo", plaAtivo);
        model.addAttribute("svcCodigo", svcCodigo);
        model.addAttribute("nplCodigo", nplCodigo);
        model.addAttribute("csaCodigo", csaCodigo);
        model.addAttribute("reqColumnsStr", reqColumnsStr);
        model.addAttribute("msgErro", msgErro);
        model.addAttribute("svcCodigoPg", svcCodigoPg);
        model.addAttribute("nplCodigoPg", nplCodigoPg);
        model.addAttribute("planoDesconto", planoDesconto);
        model.addAttribute("tituloPagina", tituloPagina);
        model.addAttribute("lstParamPla", lstParamPla);
        model.addAttribute("hshParamPlano", hshParamPlano);
        model.addAttribute("lstSvc", lstSvc);
        model.addAttribute("lstNpl", lstNpl);
        model.addAttribute("tppIndice", tppIndice);
        model.addAttribute("strMaxPrazo", strMaxPrazo);
        model.addAttribute("intMaxPrazo", intMaxPrazo);
        model.addAttribute("ade_vlr", ade_vlr);

        return viewRedirect("jsp/editarPlanoDesconto/editarPlanoDesconto", request, session, model, responsavel);

    }

    @RequestMapping(params = { "acao=bloquear" })
    public String bloquearOuDesBloquear(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, RelatorioControllerException, PlanoDescontoControllerException, InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String plaCodigo = request.getParameter("codigo");
        String tipo = request.getParameter("tipo");
        String status = request.getParameter("status");

        if (plaCodigo != null && !TextHelper.isNull(status)) {

            try {
                boolean bloqueado = status.equals(CodedValues.STS_INATIVO.toString());

                if (bloqueado) {
                    TransferObject toUpdate = new CustomTransferObject();
                    toUpdate.setAttribute(Columns.PLA_ATIVO, CodedValues.STS_ATIVO);
                    toUpdate.setAttribute(Columns.PLA_CODIGO, plaCodigo);
                    planoDescontoController.updatePlanoDesconto(toUpdate, responsavel);
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.desbloquear.plano.sucesso", responsavel));
                } else {
                    TransferObject toUpdate = new CustomTransferObject();
                    toUpdate.setAttribute(Columns.PLA_ATIVO, CodedValues.STS_INATIVO);
                    toUpdate.setAttribute(Columns.PLA_CODIGO, plaCodigo);
                    planoDescontoController.updatePlanoDesconto(toUpdate, responsavel);
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.bloquear.plano.sucesso", responsavel));
                }

            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }

        }
        //Se vier da página de editar, retornará para a pagina de editar
        if (!TextHelper.isNull(tipo) && !tipo.isEmpty() && tipo.equals("editar")) {
            String svcCodigo = JspHelper.verificaVarQryStr(request, "svcCodigo");
            String nplCodigo = JspHelper.verificaVarQryStr(request, "nplCodigo");

            request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));
            return "forward:/v3/consultarPlanoDesconto?acao=editar&" + "svcCodigo=" + svcCodigo + "&nplCodigo=" + nplCodigo + "&plaCodigo=" + plaCodigo + "&back=1" + SynchronizerToken.generateToken4URL(request);
        }

        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=excluir" })
    public String excluir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, RelatorioControllerException, PlanoDescontoControllerException, InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);
        String plaCodigo = JspHelper.verificaVarQryStr(request, "codigo");

        try {
            TransferObject toRemove = new CustomTransferObject();
            toRemove.setAttribute(Columns.PLA_CODIGO, plaCodigo);
            planoDescontoController.removePlanoDesconto(toRemove, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.excluir.plano.sucesso", responsavel));
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
        }

        return iniciar(request, response, session, model);
    }
}
