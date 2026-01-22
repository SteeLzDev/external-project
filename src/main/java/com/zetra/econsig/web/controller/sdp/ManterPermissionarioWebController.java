package com.zetra.econsig.web.controller.sdp;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.EnderecoConjuntoHabitacionalControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PermissionarioControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.sdp.EnderecoConjuntoHabitacionalController;
import com.zetra.econsig.service.sdp.PermissionarioController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.servidor.AbstractConsultarServidorWebController;

/**
 * <p>Title: ManterPermissionarioWebController</p>
 * <p>Description: Controlador Web para o caso de uso Manter Permissionário.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/manterPermissionario" })
public class ManterPermissionarioWebController extends AbstractConsultarServidorWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ManterPermissionarioWebController.class);

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private EnderecoConjuntoHabitacionalController enderecoConjuntoHabitacionalController;

    @Autowired
    private PermissionarioController permissionarioController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @RequestMapping(params = { "acao=listar" })
    public String listar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if ((request.getParameter("FILTRO") != null || request.getParameter("FILTRO_TIPO") != null) && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        List<TransferObject> lstPermissionarios = null;

        String filtro = JspHelper.verificaVarQryStr(request, "FILTRO");
        int filtroTipo = -1;
        try {
            filtroTipo = Integer.parseInt(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO"));
        } catch (Exception ex1) {
        }

        try {
            TransferObject criterio = new CustomTransferObject();

            // -------------- Seta Criterio da Listagem ------------------
            // Bloqueado
            if (filtroTipo == 0) {
                criterio.setAttribute(Columns.PRM_ATIVO, CodedValues.STS_INATIVO);
                // Desbloqueado
            } else if (filtroTipo == 1) {
                criterio.setAttribute(Columns.PRM_ATIVO, CodedValues.STS_ATIVO);
                // Outros
            } else if (!filtro.equals("") && filtroTipo != -1) {
                String campo = null;

                switch (filtroTipo) {
                    case 2:
                        campo = Columns.RSE_MATRICULA;
                        break;
                    case 3:
                        campo = Columns.SER_CPF;
                        break;
                    case 4:
                        campo = Columns.SER_NOME;
                        break;
                    case 5:
                        campo = "ENDERECO";
                        break;
                    default:
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                criterio.setAttribute(campo, CodedValues.LIKE_MULTIPLO + filtro + CodedValues.LIKE_MULTIPLO);
            }
            // ---------------------------------------

            int total = permissionarioController.countPermissionarios(criterio, responsavel);
            int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (Exception ex) {
            }

            if (total > 0) {
                lstPermissionarios = permissionarioController.lstPermissionarios(criterio, offset, size, responsavel);
            } else {
                lstPermissionarios = new ArrayList<>();
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

            String linkListarPermissionarios = request.getRequestURI() + "?acao=listar";
            configurarPaginador(linkListarPermissionarios, "rotulo.listar.permissionario.titulo", total, size, requestParams, false, request, model);

            model.addAttribute("queryString", getQueryString(requestParams, request));

        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            lstPermissionarios = new ArrayList<>();
        }

        model.addAttribute("filtro", filtro);
        model.addAttribute("filtro_tipo", filtroTipo);
        model.addAttribute("lstPermissionarios", lstPermissionarios);

        return viewRedirect("jsp/manterPermissionario/listarPermissionario", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=consultar" })
    public String consultar(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, @RequestParam(value = "PRM_CODIGO", required = true, defaultValue = "") String prmCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String csaCodigo = responsavel.getCsaCodigo();

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            if (TextHelper.isNull(rseCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.permissionario.nao.encontrado", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            CustomTransferObject servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);

            TransferObject permissionario = null;

            if (!TextHelper.isNull(prmCodigo)) {
                permissionario = permissionarioController.findPermissionario(prmCodigo, responsavel);
            }

            if (permissionario != null && !TextHelper.isNull(permissionario.getAttribute(Columns.CSA_CODIGO)) && !permissionario.getAttribute(Columns.CSA_CODIGO).toString().equals(csaCodigo)) {
                permissionario = null;
            }

            String prmDataOcupacao = (permissionario != null && permissionario.getAttribute(Columns.PRM_DATA_OCUPACAO) != null ? permissionario.getAttribute(Columns.PRM_DATA_OCUPACAO).toString() : "");
            if (!prmDataOcupacao.equals("")) {
                prmDataOcupacao = DateHelper.reformat(prmDataOcupacao, "yyyy-MM-dd", LocaleHelper.getDatePattern());
            }

            String prmDataDesocupacao = (permissionario != null && permissionario.getAttribute(Columns.PRM_DATA_DESOCUPACAO) != null ? permissionario.getAttribute(Columns.PRM_DATA_DESOCUPACAO).toString() : "");
            if (!prmDataDesocupacao.equals("")) {
                prmDataDesocupacao = DateHelper.reformat(prmDataDesocupacao, "yyyy-MM-dd", LocaleHelper.getDatePattern());
            }

            TransferObject criterioOcorrenciaPermisionario = new CustomTransferObject();
            criterioOcorrenciaPermisionario.setAttribute(Columns.OPE_PRM_CODIGO, prmCodigo);
            int total = permissionarioController.countOcorrenciaPermisionario(criterioOcorrenciaPermisionario, responsavel);

            int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (Exception ex) {
            }

            List<TransferObject> lstOcorrencias = null;

            if (total > 0) {
                lstOcorrencias = permissionarioController.lstHistoricoPermissionario(criterioOcorrenciaPermisionario, offset, size, responsavel);
            } else {
                lstOcorrencias = new ArrayList<>();
            }

            String linkListarPermissionarios = request.getRequestURI() + "?acao=consultar&PRM_CODIGO=" + prmCodigo + "&RSE_CODIGO=" + rseCodigo;

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
            configurarPaginador(linkListarPermissionarios, "rotulo.listar.permissionario.titulo", total, size, requestParams, false, request, model);

            TransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.ECH_CSA_CODIGO, csaCodigo);
            List<TransferObject> lstEnderecos = enderecoConjuntoHabitacionalController.listaEndereco(criterio, -1, -1, responsavel);

            model.addAttribute("rseCodigo", rseCodigo);
            model.addAttribute("prmCodigo", prmCodigo);

            model.addAttribute("servidor", servidor);
            model.addAttribute("permissionario", permissionario);
            model.addAttribute("prmDataOcupacao", prmDataOcupacao);
            model.addAttribute("prmDataDesocupacao", prmDataDesocupacao);
            model.addAttribute("lstOcorrencias", lstOcorrencias);
            model.addAttribute("lstEnderecos", lstEnderecos);

        } catch (PermissionarioControllerException | EnderecoConjuntoHabitacionalControllerException | ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);

        } catch (ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/manterPermissionario/editarPermissionario", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=incluir" })
    public String incluir(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String csaCodigo = responsavel.getCsaCodigo();

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            if (TextHelper.isNull(rseCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.permissionario.nao.encontrado", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            CustomTransferObject servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);

            String srsCodigo = servidor != null && !TextHelper.isNull(servidor.getAttribute(Columns.SRS_CODIGO)) ? servidor.getAttribute(Columns.SRS_CODIGO).toString() : "";

            if (!srsCodigo.equals(CodedValues.SRS_ATIVO)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.permissionario.bloqueado.excluido", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            TransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.ECH_CSA_CODIGO, csaCodigo);
            List<TransferObject> lstEnderecos = enderecoConjuntoHabitacionalController.listaEndereco(criterio, -1, -1, responsavel);

            model.addAttribute("rseCodigo", rseCodigo);
            model.addAttribute("servidor", servidor);
            model.addAttribute("lstEnderecos", lstEnderecos);

        } catch (EnderecoConjuntoHabitacionalControllerException | ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/manterPermissionario/editarPermissionario", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=editar" })
    public String editar(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, @RequestParam(value = "PRM_CODIGO", required = true, defaultValue = "") String prmCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return consultar(rseCodigo, prmCodigo, request, response, session, model);
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, @RequestParam(value = "PRM_CODIGO", required = false, defaultValue = "") String prmCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String tipo = JspHelper.verificaVarQryStr(request, "tipo");

        // Atualiza o permissionario
        String reqColumnsStr = tipo.equals("editar") ?  "PRM_DATA_OCUPACAO" : "ECH_CODIGO|PRM_COMPL_ENDERECO|PRM_DATA_OCUPACAO";
        String msgErro = JspHelper.verificaCamposForm(request, session, reqColumnsStr, ApplicationResourcesHelper.getMessage("mensagem.campos.obrigatorios", responsavel), "100%");

        if (!msgErro.isEmpty()) {
            session.setAttribute(CodedValues.MSG_ERRO, msgErro);

        } else {
            try {
                TransferObject permissionario = new CustomTransferObject();
                permissionario.setAttribute(Columns.PRM_CODIGO, prmCodigo);
                permissionario.setAttribute(Columns.PRM_RSE_CODIGO, rseCodigo);
                permissionario.setAttribute(Columns.PRM_ECH_CODIGO, JspHelper.verificaVarQryStr(request, "ECH_CODIGO"));
                permissionario.setAttribute(Columns.PRM_COMPL_ENDERECO, JspHelper.verificaVarQryStr(request, "PRM_COMPL_ENDERECO"));

                String prm_data_ocupacao = JspHelper.verificaVarQryStr(request, "PRM_DATA_OCUPACAO");
                permissionario.setAttribute(Columns.PRM_DATA_OCUPACAO, !TextHelper.isNull(prm_data_ocupacao) ? new java.sql.Timestamp(DateHelper.parse(prm_data_ocupacao, LocaleHelper.getDatePattern()).getTime()) : null);

                String prm_data_desocupacao = JspHelper.verificaVarQryStr(request, "PRM_DATA_DESOCUPACAO");
                permissionario.setAttribute(Columns.PRM_DATA_DESOCUPACAO, !TextHelper.isNull(prm_data_desocupacao) ? new java.sql.Timestamp(DateHelper.parse(prm_data_desocupacao, LocaleHelper.getDatePattern()).getTime()) : null);

                String prm_em_transferencia = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "PRM_EM_TRANSFERENCIA")) && JspHelper.verificaVarQryStr(request, "PRM_EM_TRANSFERENCIA").equals(CodedValues.TPC_SIM) ? CodedValues.TPC_SIM : CodedValues.TPC_NAO;
                permissionario.setAttribute(Columns.PRM_EM_TRANSFERENCIA, prm_em_transferencia);
                permissionario.setAttribute(Columns.PRM_TELEFONE, JspHelper.verificaVarQryStr(request, "PRM_TELEFONE"));
                permissionario.setAttribute(Columns.PRM_EMAIL, JspHelper.verificaVarQryStr(request, "PRM_EMAIL"));
                permissionario.setAttribute(Columns.PRM_ATIVO, CodedValues.STS_ATIVO.toString());

                // Verifica o TPA 33 - Permite ocupar mesmo imóvel
                String tpaPermiteOcuparMesmoImovel = parametroController.getParamCsa(responsavel.getCsaCodigo(), CodedValues.TPA_PERMITE_OCUPAR_MESMO_IMOVEL_SDP, responsavel);

                if (tpaPermiteOcuparMesmoImovel == null || tpaPermiteOcuparMesmoImovel.equals(CodedValues.TPA_NAO)) {
                    TransferObject morador = permissionarioController.findPermissionarioPorEndereco(permissionario, responsavel);
                    if (morador != null && !morador.getAttribute(Columns.PRM_CODIGO).equals(permissionario.getAttribute(Columns.PRM_CODIGO))) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.permissionario.nao.pode.ocupar.mesmo.imovel", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                }

                if (!TextHelper.isNull(prmCodigo)) {
                    if (tipo.equals("incluir")) {
                        // reativação de permissionário
                        permissionarioController.updatePermissionario(permissionario, true, responsavel);
                    } else {
                        permissionarioController.updatePermissionario(permissionario, responsavel);
                    }
                } else {
                    prmCodigo = permissionarioController.createPermissionario(permissionario, responsavel);
                }

                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.permissionario.salvo.sucesso", responsavel));

            } catch (PermissionarioControllerException | ParametroControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);

            } catch (ParseException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        // Volta para a página anterior
        ParamSession paramSession = ParamSession.getParamSession(session);
        model.addAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }

    @RequestMapping(params = { "acao=excluir" })
    public String excluir(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, @RequestParam(value = "PRM_CODIGO", required = true, defaultValue = "") String prmCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            String prmDataDesocupacaoStr = JspHelper.verificaVarQryStr(request, "prm_data_desocupacao");
            Date prmDataDesocupacao = null;

            if (TextHelper.isNull(prmDataDesocupacaoStr)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.data.desocupacao", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            } else {
                try {
                    prmDataDesocupacao = new java.sql.Timestamp(DateHelper.parse(prmDataDesocupacaoStr, LocaleHelper.getDatePattern()).getTime());
                } catch (Exception ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.permissionario.data.desocupacao", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            // Exclui permissionário
            permissionarioController.removePermissionario(prmCodigo, prmDataDesocupacao, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.permissionario.excluido.sucesso", responsavel));

            // Volta para a página anterior
            ParamSession paramSession = ParamSession.getParamSession(session);
            model.addAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";

        } catch (PermissionarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @Override
    protected String continuarOperacao(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, @RequestParam(value = "ADE_NUMERO", required = true, defaultValue = "") String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws AutorizacaoControllerException {
        return incluir(rseCodigo, request, response, session, model);
    }

    @Override
    protected String definirProximaOperacao(HttpServletRequest request, AcessoSistema responsavel) {
        return "incluir";
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.selecionar.servidor.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/manterPermissionario?_skip_history_=true");
        model.addAttribute("omitirAdeNumero", true);
    }

    @Override
    protected void definirAcaoRetorno(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        ParamSession paramSession = ParamSession.getParamSession(session);
        model.addAttribute("acaoRetorno", SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request));
    }
}
