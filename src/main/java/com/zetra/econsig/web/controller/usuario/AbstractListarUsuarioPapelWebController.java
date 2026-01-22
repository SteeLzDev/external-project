package com.zetra.econsig.web.controller.usuario;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: AbstractListarUsuarioPapelWebController</p>
 * <p>Description: Controlador Web base para o caso de uso Listar Usuário por papel.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class AbstractListarUsuarioPapelWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AbstractListarUsuarioPapelWebController.class);

    @Autowired
    ServidorController servidorController;

    @Autowired
    private UsuarioController usuarioController;

    protected final String getTituloPagina(HttpServletRequest request, AcessoSistema responsavel) {
        return TextHelper.decode64(JspHelper.verificaVarQryStr(request, "titulo"));
    }
    protected final String getTituloPaginaBase64(HttpServletRequest request, AcessoSistema responsavel) {
        return TextHelper.encode64(getTituloPagina(request, responsavel));
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        super.configurarPagina(request, session, model, responsavel);

        model.addAttribute("linkAction", getLinkAction());
        model.addAttribute("linkInserirUsuario", getLinkInserirUsuario());
        model.addAttribute("linkEditarUsuario", getLinkEditarUsuario());
        model.addAttribute("linkDetalharUsuario", getLinkDetalharUsuario());
        model.addAttribute("linkExcluirUsuario", getLinkExcluirUsuario());
        model.addAttribute("linkReinicializarSenhaUsuario", getLinkReinicializarSenhaUsuario());
        model.addAttribute("linkConsultarUsuario", getLinkConsultarUsuario());
        model.addAttribute("linkBloquearUsuario", getLinkBloquearUsuario());
        model.addAttribute("inserirUsuario", false);
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.usuario.papel.titulo", responsavel, getTituloPagina(request, responsavel)));
    }

    @RequestMapping(params = { "acao=listar" })
    public String listar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String codigo = getCodigoEntidade(request);
        String titulo = getTituloPagina(request, responsavel);

        // Valida o token
        if (!(responsavel.isSup() && getTipoEntidade().equals(AcessoSistema.ENTIDADE_SUP)) && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        ParamSession paramSession = ParamSession.getParamSession(session);

        // Exige tipo de motivo da operacao
        boolean exigeMotivoOperacaoUsu = ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_TIPO_MOTIVO_OPERACAO_USUARIO, responsavel);

        // Valida o tipo de entidade, com a entidade do usuário que está criando novo usuário
        try {
            validaTipoEntidade(request, responsavel);
        } catch (ZetraException e) {
            // Redireciona para página de erro
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        List<TransferObject> usuarios = null;
        String linkCancelar = "";
        String fromLstPerfil = JspHelper.verificaVarQryStr(request, "fromLstPerfil");
        if (getTipoEntidade().equals(AcessoSistema.ENTIDADE_SUP) && (fromLstPerfil == null || fromLstPerfil.isEmpty() || fromLstPerfil == "0")) {
            linkCancelar = "../v3/carregarPrincipal";
        } else {
            linkCancelar = TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request));
        }

        int qtdColunas = 4;

        String filtro = JspHelper.verificaVarQryStr(request, "FILTRO");
        int filtro_tipo = -1;
        try {
            filtro_tipo = Integer.parseInt(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO"));
        } catch (Exception ex1) {
        }

        try {
            CustomTransferObject criterio = new CustomTransferObject();

            // Não exibe usuários de centralizador para consignatárias/correspondentes
            if (!responsavel.isSup()) {
                criterio.setAttribute("OCULTA_USU_VISIVEL", Boolean.TRUE);
            }

            // Seta Criterio da Listagem
            // Bloqueado
            if (filtro_tipo == 0) {
                List<String> statusBloqueados = new ArrayList<>();
                statusBloqueados.add(CodedValues.STU_BLOQUEADO);
                statusBloqueados.add(CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE);
                statusBloqueados.add(CodedValues.STU_BLOQUEADO_POR_CSE);
                statusBloqueados.add(CodedValues.STU_EXCLUIDO);
                statusBloqueados.add(CodedValues.STU_BLOQUEADO_AUSENCIA_TEMPORARIA);
                statusBloqueados.add(CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA);
                statusBloqueados.add(CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE_FIM_VIGENCIA);
                criterio.setAttribute(Columns.USU_STU_CODIGO, statusBloqueados);
                // Desbloqueado
            } else if (filtro_tipo == 1) {
                criterio.setAttribute(Columns.USU_STU_CODIGO, CodedValues.STU_ATIVO);
                // Outros
            } else if (filtro_tipo == 5) {
                criterio.setAttribute(Columns.PER_CODIGO, CodedValues.IS_NULL_KEY);
                // Outros
            } else if (!filtro.equals("") && filtro_tipo != -1) {
                String campo = null;

                switch (filtro_tipo) {
                    case 2:
                        campo = Columns.USU_LOGIN;
                        break;
                    case 3:
                        campo = Columns.USU_NOME;
                        break;
                    case 4:
                        campo = Columns.PER_DESCRICAO;
                        break;
                    case 9:
                        campo = Columns.USU_CPF;
                        break;
                    default:
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                criterio.setAttribute(campo, CodedValues.LIKE_MULTIPLO + filtro + CodedValues.LIKE_MULTIPLO);
                // Tipo
            }

            // filtra pelo perfil quando na exibição de usuários de um perfil
            String perCodigo = JspHelper.verificaVarQryStr(request, "per_codigo");
            if (!TextHelper.isNull(perCodigo)) {
                criterio.setAttribute(Columns.PER_CODIGO, perCodigo);
                model.addAttribute("perCodigo", perCodigo);
            }

            // filtra pelo código do usuário, quando vem da tela de usuários autenticados
            String usuCodigo = JspHelper.verificaVarQryStr(request, "usu_codigo");
            if (!TextHelper.isNull(usuCodigo)) {
                criterio.setAttribute(Columns.USU_CODIGO, usuCodigo);
                model.addAttribute("usuCodigo", usuCodigo);
            }

            int total = usuarioController.countUsuarios(getTipoEntidade(), codigo, criterio, responsavel);
            int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (Exception ex) {
            }

            usuarios = usuarioController.getUsuarios(getTipoEntidade(), codigo, criterio, offset, size, responsavel);

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

            List<String> listParams = new ArrayList<>(params);
            configurarPaginador(getLinkAction(), "rotulo.paginacao.titulo.usuario", total, size, listParams, false, request, model);
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            usuarios = new ArrayList<>();
        }

        model.addAttribute("codigo", codigo);
        model.addAttribute("titulo", titulo);
        model.addAttribute("exigeMotivoOperacaoUsu", exigeMotivoOperacaoUsu);
        model.addAttribute("filtro", filtro);
        model.addAttribute("filtro_tipo", filtro_tipo);
        model.addAttribute("linkCancelar", linkCancelar);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("qtdColunas", qtdColunas);

        return viewRedirect("jsp/manterUsuario/listarUsuarioPapel", request, session, model, responsavel);
    }

    /**
     * Valida o tipo de entidade, com a entidade do usuário que está criando novo usuário
     * @param request
     * @param responsavel
     * @throws LogControllerException
     * @throws ZetraException
     */
    protected void validaTipoEntidade(HttpServletRequest request, AcessoSistema responsavel) throws LogControllerException, ZetraException {
        String tipo = getTipoEntidade();
        String codEntidade = getCodigoEntidade(request);

        if (isTipoEntidadeInvalido(tipo, codEntidade, responsavel)) {
            // Registra log de erro
            com.zetra.econsig.delegate.LogDelegate log = new com.zetra.econsig.delegate.LogDelegate(responsavel, Log.USUARIO, Log.CREATE, Log.LOG_ERRO_SEGURANCA);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.erro.manutencao.usuario.nao.permitida", responsavel).toUpperCase());
            log.write();
            throw new ZetraException("mensagem.erro.interno.contate.administrador", responsavel);
        }
    }

    protected String getCodigoEntidade(HttpServletRequest request) {
        return JspHelper.verificaVarQryStr(request, "codigo");
    }

    protected abstract String getTipoEntidade();

    protected abstract String getLinkAction();

    protected abstract String getLinkDetalharUsuario();

    protected abstract String getLinkConsultarUsuario();

    protected abstract String getLinkInserirUsuario();

    protected abstract String getLinkEditarUsuario();

    protected abstract String getLinkBloquearUsuario();

    protected abstract String getLinkReinicializarSenhaUsuario();

    protected abstract String getLinkExcluirUsuario();

    protected boolean unidades(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model)  {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String orgCodigo = request.getAttribute(Columns.ORG_CODIGO) != null ? (String) request.getAttribute(Columns.ORG_CODIGO) : null;

        try {
            HashMap<String, List<TransferObject>> hashOrgUnidades = new HashMap<>();
            List<TransferObject> lstUnidadesSubOrgaos = servidorController.lstUnidadeSubOrgao(responsavel);
            List<TransferObject> lstSubOrgao = servidorController.lstSubOrgao(responsavel, orgCodigo);

            for(TransferObject subOrgao : lstSubOrgao) {
                String subOrgCodigo = (String) subOrgao.getAttribute(Columns.SBO_CODIGO);
                List<TransferObject> unidades = new ArrayList<>();

                for(TransferObject unidadesSubOrg : lstUnidadesSubOrgaos) {
                    String subOrgUnidade = (String) unidadesSubOrg.getAttribute(Columns.SBO_CODIGO);
                    if(subOrgCodigo.equals(subOrgUnidade)) {
                        unidades.add(unidadesSubOrg);
                    }
                }

                if(!unidades.isEmpty()) {
                    hashOrgUnidades.put(subOrgCodigo, unidades);
                }
            }

            if(hashOrgUnidades.isEmpty() || lstSubOrgao == null || lstSubOrgao.isEmpty() || lstUnidadesSubOrgaos == null || lstUnidadesSubOrgaos.isEmpty()) {
                return Boolean.FALSE;
            }


            String usuCodigo = JspHelper.verificaVarQryStr(request, "usu_codigo");
            usuCodigo = TextHelper.isNull(usuCodigo) ? (String) request.getAttribute("usu_codigo") : usuCodigo;
            List<String> usuUnidades = usuarioController.unidadesPermissaoEdtUsuario(usuCodigo, responsavel);

            ParamSession paramSession = ParamSession.getParamSession(session);
            String link = request.getAttribute("linkVoltar") !=null ? (String) request.getAttribute("linkVoltar") : paramSession.getLastHistory();

            model.addAttribute("lstSubOrgao",lstSubOrgao);
            model.addAttribute("hashOrgUnidades",hashOrgUnidades);
            model.addAttribute("usuUnidades",usuUnidades);
            model.addAttribute("usuCodigo",usuCodigo);
            model.addAttribute("linkVoltar",link);
        } catch (ServidorControllerException | UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
        }
        return Boolean.TRUE;
    }

    protected boolean salvarUnidadesSubOrgao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model)  {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            String usuCodigo = request.getParameter("usuCodigo");
            List<String> uniCodigos = request.getParameter("uniCodigos") != null ? Arrays.asList(request.getParameterValues("uniCodigos")) : new ArrayList<>();

            usuarioController.atribuirUnidadesUsuario(usuCodigo, uniCodigos, responsavel);

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("rotulo.usuario.unidades.msg.sucesso", responsavel));
            request.setAttribute("usu_codigo", usuCodigo);
            request.setAttribute("linkVoltar", request.getParameter("linkVoltar"));
            return Boolean.TRUE;
        } catch (UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            LOG.error(ex.getMessage(), ex);
            return Boolean.FALSE;
        }
    }
}
