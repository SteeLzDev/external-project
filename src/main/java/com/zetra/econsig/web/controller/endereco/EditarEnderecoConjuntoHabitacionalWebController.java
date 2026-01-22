package com.zetra.econsig.web.controller.endereco;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import com.zetra.econsig.dto.entidade.EnderecoTransferObject;
import com.zetra.econsig.exception.EnderecoConjuntoHabitacionalControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.sdp.EnderecoConjuntoHabitacionalController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: EditarEnderecoConjuntoHabitacionalWebController</p>
 * <p>Description: Controlador Web para caso de uso editar endereço</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: rodrigo.rosa $
 * $Revision: 25329 $
 * $Date: 2020-06-24 14:15:21 -0300 (Qua, 24 jun 2020) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/editarEnderecoConjHab" })
public class EditarEnderecoConjuntoHabitacionalWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EditarEnderecoConjuntoHabitacionalWebController.class);

    @Autowired
    private EnderecoConjuntoHabitacionalController enderecoConjuntoHabitacionalController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        ParamSession paramSession = ParamSession.getParamSession(session);

        SynchronizerToken.saveToken(request);

        List<TransferObject> enderecos = null;
        String filtro = JspHelper.verificaVarQryStr(request, "FILTRO");

        int filtro_tipo = -1;
        try {
            filtro_tipo = Integer.parseInt(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO"));
        } catch (Exception ex1) {
        }

        try {
            CustomTransferObject criterio = new CustomTransferObject();
            String campo = null;

            if (!filtro.equals("") && filtro_tipo != -1) {
                switch (filtro_tipo) {
                    case 0:
                        campo = Columns.ECH_IDENTIFICADOR;
                        break;
                    case 1:
                        campo = Columns.ECH_DESCRICAO;
                        break;
                    case 2:
                        campo = Columns.ECH_CONDOMINIO;
                        break;
                    case 3:
                        campo = Columns.ECH_CONDOMINIO;
                        break;
                    default:
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            criterio.setAttribute(campo, filtro);
            criterio.setAttribute(Columns.ECH_CSA_CODIGO, responsavel.getCsaCodigo());

            if (filtro_tipo == 2) {
                criterio.setAttribute(Columns.ECH_CONDOMINIO, "S");
            } else if (filtro_tipo == 3) {
                criterio.setAttribute(Columns.ECH_CONDOMINIO, "N");
            }

            int total = enderecoConjuntoHabitacionalController.countEndereco(criterio, responsavel);
            int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (Exception ex) {
            }

            enderecos = enderecoConjuntoHabitacionalController.listaEndereco(criterio, offset, size, responsavel);

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
            configurarPaginador("../v3/editarEnderecoConjHab?acao=iniciar", "rotulo.paginacao.titulo.endereco", total, size, requestParams, false, request, model);
        } catch (EnderecoConjuntoHabitacionalControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            enderecos = new ArrayList<>();
        }

        //Colocando um endereço no paramSession
        Map<String, String[]> parametros = new HashMap<>();
        String link = request.getRequestURI();
        paramSession.addHistory(link, parametros);

        model.addAttribute("enderecos", enderecos);
        model.addAttribute("filtro", filtro);
        model.addAttribute("filtro_tipo", filtro_tipo);

        return viewRedirect("jsp/editarEnderecoConjuntoHabitacional/listarEnderecoConjuntoHabitacional", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=iniciarEdicao" })
    public String iniciarEdicao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String ech_codigo = null;
        EnderecoTransferObject endereco = null;
        String reqColumnsStr = "";
        String msgErro = JspHelper.verificaCamposForm(request, session, reqColumnsStr, ApplicationResourcesHelper.getMessage("mensagem.campos.obrigatorios", responsavel), "100%");
        boolean podeEditarEndereco = responsavel.temPermissao(CodedValues.FUN_EDT_ENDERECO);

        try {
            ech_codigo = JspHelper.verificaVarQryStr(request, "echCodigo") != null && !JspHelper.verificaVarQryStr(request, "echCodigo").isEmpty() && !JspHelper.verificaVarQryStr(request, "echCodigo").equals("null") ? JspHelper.verificaVarQryStr(request, "echCodigo") : request.getAttribute("echCodigo") != null && !request.getAttribute("echCodigo").equals("") ? request.getAttribute("echCodigo").toString() : null;

            //Se for edição de um endereço existente no sistema
            if (!TextHelper.isNull(ech_codigo)) {
                //carregar dados
                endereco = new EnderecoTransferObject(ech_codigo);
                endereco = enderecoConjuntoHabitacionalController.buscaEnderecoByPK(endereco, responsavel);
            }
        } catch (EnderecoConjuntoHabitacionalControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        model.addAttribute("ech_codigo", ech_codigo);
        model.addAttribute("endereco", endereco);
        model.addAttribute("msgErro", msgErro);
        model.addAttribute("podeEditarEndereco", podeEditarEndereco);

        return viewRedirect("jsp/editarEnderecoConjuntoHabitacional/editarEnderecoConjuntoHabitacional", request, session, model, responsavel);

    }

    @RequestMapping(params = { "acao=salvarEdicao" })
    public String salvarEdicao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws EnderecoConjuntoHabitacionalControllerException, InstantiationException, IllegalAccessException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String ech_codigo = JspHelper.verificaVarQryStr(request, "echCodigo") != null && !JspHelper.verificaVarQryStr(request, "echCodigo").equals("null") ? JspHelper.verificaVarQryStr(request, "echCodigo") : null;
        EnderecoTransferObject endereco = null;
        String descricao = JspHelper.verificaVarQryStr(request, "ECH_DESCRICAO");
        String identificador = JspHelper.verificaVarQryStr(request, "ECH_IDENTIFICADOR");
        Short unidades = Short.valueOf(JspHelper.verificaVarQryStr(request, "ECH_QTD_UNIDADES"));
        String condominio = JspHelper.verificaVarQryStr(request, "ECH_CONDOMINIO");

        endereco = new EnderecoTransferObject();
        endereco.setEchCodigo(ech_codigo);
        endereco.setConsignataria(responsavel.getCsaCodigo());
        endereco.setEchDescricao(descricao);
        endereco.setEchIdentificador(identificador);
        endereco.setEchQtdUnidades(unidades);
        endereco.setEchCondominio(TextHelper.isNull(condominio) ? "N" : "S");

        //inserção
        if (TextHelper.isNull(ech_codigo)) {
            ech_codigo = enderecoConjuntoHabitacionalController.createEndereco(endereco, responsavel);
            request.setAttribute("echCodigo", ech_codigo);
        } else {
            //update
            ech_codigo = enderecoConjuntoHabitacionalController.updateEndereco(endereco, responsavel);
        }

        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.endereco.alteracoes.salvas.sucesso", responsavel));
        request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

        return iniciarEdicao(request, response, session, model);
    }

    @RequestMapping(params = { "acao=excluir" })
    public String excluirEndereco(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        // Exclui o órgão
        if (request.getParameter("codigo") != null) {

            try {
                EnderecoTransferObject endRem = new EnderecoTransferObject(request.getParameter("codigo"));
                enderecoConjuntoHabitacionalController.removeEndereco(endRem, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.endereco.excluido.sucesso", responsavel));
            } catch (EnderecoConjuntoHabitacionalControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }
        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return iniciar(request, response, session, model);
    }
}
