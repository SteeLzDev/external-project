    package com.zetra.econsig.web.controller.usuario;

import java.util.ArrayList;
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
import com.zetra.econsig.dto.entidade.EnderecoFuncaoTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ManterRestricaoAcessoUsuarioWebController</p>
 * <p>Description: Controlador Web para o caso de uso Manter Restrição de Acesso de Usuário.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/manterRestricaoAcessoUsuario" })
public class ManterRestricaoAcessoUsuarioWebController extends ControlePaginacaoWebController {

    @Autowired
    private UsuarioController usuarioController;

    @RequestMapping(params = { "acao=listar" })
    public String listar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Valida o token
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String usuCodigo = JspHelper.verificaVarQryStr(request, "usucodigo");
            if (TextHelper.isNull(usuCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            UsuarioTransferObject usu = usuarioController.findUsuario(usuCodigo, responsavel);
            String usuNome = usu.getUsuNome();
            String usuLogin = usu.getUsuLogin();

            CustomTransferObject usuario = usuarioController.findTipoUsuarioByLogin(usuLogin, responsavel);

            boolean podeEditarUsu = false;
            String tipo = "";
            String codEntidade = "";
            String cseCodigo = usuario.getAttribute(Columns.UCE_CSE_CODIGO) != null ? usuario.getAttribute(Columns.UCE_CSE_CODIGO).toString() : "";
            String csaCodigo = usuario.getAttribute(Columns.UCA_CSA_CODIGO) != null ? usuario.getAttribute(Columns.UCA_CSA_CODIGO).toString() : "";
            String corCodigo = usuario.getAttribute(Columns.UCO_COR_CODIGO) != null ? usuario.getAttribute(Columns.UCO_COR_CODIGO).toString() : "";
            String orgCodigo = usuario.getAttribute(Columns.UOR_ORG_CODIGO) != null ? usuario.getAttribute(Columns.UOR_ORG_CODIGO).toString() : "";
            String cspCodigo = usuario.getAttribute(Columns.USP_CSE_CODIGO) != null ? usuario.getAttribute(Columns.USP_CSE_CODIGO).toString() : "";

            // Determina o tipo da entidade do usuário
            if (!cseCodigo.equals("")) {
                tipo = AcessoSistema.ENTIDADE_CSE;
                codEntidade = cseCodigo;
                podeEditarUsu = responsavel.temPermissao(CodedValues.FUN_EDT_USUARIOS_CSE);
            } else if (!csaCodigo.equals("")) {
                tipo = AcessoSistema.ENTIDADE_CSA;
                codEntidade = csaCodigo;
                podeEditarUsu = responsavel.temPermissao(CodedValues.FUN_EDT_USUARIOS_CSA);
            } else if (!corCodigo.equals("")) {
                tipo = AcessoSistema.ENTIDADE_COR;
                codEntidade = corCodigo;
                podeEditarUsu = responsavel.temPermissao(CodedValues.FUN_EDT_USUARIOS_COR);
            } else if (!orgCodigo.equals("")) {
                tipo = AcessoSistema.ENTIDADE_ORG;
                codEntidade = orgCodigo;
                podeEditarUsu = responsavel.temPermissao(CodedValues.FUN_EDT_USUARIOS_ORG);
            } else if (!cspCodigo.equals("")) {
                tipo = AcessoSistema.ENTIDADE_SUP;
                codEntidade = cspCodigo;
                podeEditarUsu = responsavel.temPermissao(CodedValues.FUN_EDT_USUARIOS_SUP);
            }

            boolean podeEdtRestAcessoFun = podeEditarUsu && responsavel.temPermissao(CodedValues.FUN_EDT_RESTRICAO_ACESSO_POR_FUNCAO);
            // Assumi que se não pode editar, por segurança, não pode visualizar (pois só existe um parâmetro, o que permite editar)
            if (!podeEdtRestAcessoFun) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Valida o tipo de entidade, com a entidade do usuário que está sendo editado
            try {
                validaTipoEntidade(tipo, codEntidade, responsavel);
            } catch (ZetraException e) {
                // Redireciona para página de erro
                session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            //Valores especiais para página de usuários de suporte.
            if (tipo.equals(AcessoSistema.ENTIDADE_SUP)) {
                codEntidade = "1";
            }

            String filtro = JspHelper.verificaVarQryStr(request, "FILTRO");
            int filtro_tipo = -1;
            try {
                filtro_tipo = Integer.parseInt(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO"));
            } catch (Exception ex1) {
            }

            Map<String, EnderecoFuncaoTransferObject> mapFuncoesTo = null;
            try {
                CustomTransferObject criterio = new CustomTransferObject();
                if (!filtro.equals("") && filtro_tipo != -1) {
                    String campo = null;
                    switch (filtro_tipo) {
                        case 0:
                            campo = Columns.FUN_CODIGO;
                            break;
                        case 1:
                            campo = Columns.FUN_DESCRICAO;
                            break;
                        default:
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);

                    }
                    criterio.setAttribute(campo, CodedValues.LIKE_MULTIPLO + filtro + CodedValues.LIKE_MULTIPLO);
                }

                int total = usuarioController.selectFuncoes(usuCodigo, codEntidade, tipo, criterio, -1, -1, responsavel).size();
                int size = JspHelper.LIMITE;
                int offset = 0;
                try {
                    offset = Integer.parseInt(request.getParameter("offset"));
                } catch (Exception ex) {
                }

                mapFuncoesTo = usuarioController.selectFuncoes(usuCodigo, codEntidade, tipo, criterio, offset, size, responsavel);

                // Monta lista de parâmetros através dos parâmetros de request
                Set<String> params = new HashSet<>(request.getParameterMap().keySet());

                // Ignora os parâmetros abaixo
                params.remove("offset");
                params.remove("back");
                params.remove("linkRet");
                params.remove("linkRet64");
                params.remove("eConsig.page.token");
                params.remove("_skip_history_");
                params.remove("pager");
                params.remove("acao");

                List<String> requestParams = new ArrayList<>(params);
                String linkListagem = "../v3/manterRestricaoAcessoUsuario?acao=listar&usucodigo=" + usuCodigo;
                configurarPaginador(linkListagem, "rotulo.lst.arq.generico.titulo", total, size, requestParams, false, request, model);
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            model.addAttribute("usuCodigo", usuCodigo);
            model.addAttribute("usuNome", usuNome);
            model.addAttribute("usuLogin", usuLogin);
            model.addAttribute("tipo", tipo);
            model.addAttribute("codEntidade", codEntidade);
            model.addAttribute("podeEdtRestAcessoFun", podeEdtRestAcessoFun);
            model.addAttribute("filtro", filtro);
            model.addAttribute("filtro_tipo", filtro_tipo);
            model.addAttribute("mapFuncoesTo", mapFuncoesTo);

            return viewRedirect("jsp/manterUsuario/listarRestricaoAcessoUsuario", request, session, model, responsavel);

        } catch (UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Valida o token
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String usuCodigo = JspHelper.verificaVarQryStr(request, "usucodigo");
            String funCodigo = JspHelper.verificaVarQryStr(request, "funcodigo");

            if (TextHelper.isNull(usuCodigo) || TextHelper.isNull(funCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            UsuarioTransferObject usu = usuarioController.findUsuario(usuCodigo, responsavel);
            String usuNome = usu.getUsuNome();
            String usuLogin = usu.getUsuLogin();

            CustomTransferObject usuario = usuarioController.findTipoUsuarioByLogin(usuLogin, responsavel);

            boolean podeEditarUsu = false;
            String tipo = "";
            String codEntidade = "";
            String cseCodigo = usuario.getAttribute(Columns.UCE_CSE_CODIGO) != null ? usuario.getAttribute(Columns.UCE_CSE_CODIGO).toString() : "";
            String csaCodigo = usuario.getAttribute(Columns.UCA_CSA_CODIGO) != null ? usuario.getAttribute(Columns.UCA_CSA_CODIGO).toString() : "";
            String corCodigo = usuario.getAttribute(Columns.UCO_COR_CODIGO) != null ? usuario.getAttribute(Columns.UCO_COR_CODIGO).toString() : "";
            String orgCodigo = usuario.getAttribute(Columns.UOR_ORG_CODIGO) != null ? usuario.getAttribute(Columns.UOR_ORG_CODIGO).toString() : "";
            String cspCodigo = usuario.getAttribute(Columns.USP_CSE_CODIGO) != null ? usuario.getAttribute(Columns.USP_CSE_CODIGO).toString() : "";

            // Determina o tipo da entidade do usuário
            if (!cseCodigo.equals("")) {
                tipo = AcessoSistema.ENTIDADE_CSE;
                codEntidade = cseCodigo;
                podeEditarUsu = responsavel.temPermissao(CodedValues.FUN_EDT_USUARIOS_CSE);
            } else if (!csaCodigo.equals("")) {
                tipo = AcessoSistema.ENTIDADE_CSA;
                codEntidade = csaCodigo;
                podeEditarUsu = responsavel.temPermissao(CodedValues.FUN_EDT_USUARIOS_CSA);
            } else if (!corCodigo.equals("")) {
                tipo = AcessoSistema.ENTIDADE_COR;
                codEntidade = corCodigo;
                podeEditarUsu = responsavel.temPermissao(CodedValues.FUN_EDT_USUARIOS_COR);
            } else if (!orgCodigo.equals("")) {
                tipo = AcessoSistema.ENTIDADE_ORG;
                codEntidade = orgCodigo;
                podeEditarUsu = responsavel.temPermissao(CodedValues.FUN_EDT_USUARIOS_ORG);
            } else if (!cspCodigo.equals("")) {
                tipo = AcessoSistema.ENTIDADE_SUP;
                codEntidade = cspCodigo;
                podeEditarUsu = responsavel.temPermissao(CodedValues.FUN_EDT_USUARIOS_SUP);
            }

            boolean podeEdtRestAcessoFun = podeEditarUsu && responsavel.temPermissao(CodedValues.FUN_EDT_RESTRICAO_ACESSO_POR_FUNCAO);
            // Assumi que se não pode editar, por segurança, não pode visualizar (pois só existe um parâmetro, o que permite editar)
            if (!podeEdtRestAcessoFun) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Valida o tipo de entidade, com a entidade do usuário que está sendo editado
            try {
                validaTipoEntidade(tipo, codEntidade, responsavel);
            } catch (ZetraException e) {
                // Redireciona para página de erro
                session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            //Valores especiais para página de usuários de suporte.
            if (tipo.equals(AcessoSistema.ENTIDADE_SUP)) {
                codEntidade = "1";
            }

            Map<String, EnderecoFuncaoTransferObject> mapFuncoesTo = null;
            try {
                CustomTransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.FUN_CODIGO, funCodigo);
                mapFuncoesTo = usuarioController.selectFuncoes(usuCodigo, codEntidade, tipo, criterio, -1, -1, responsavel);
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            // Se for edição de usuário csa/cor, verifica se parâmetro de sistema permite cadastro de ip interno
            boolean permiteIpInterno = (tipo.equals("CSA") || tipo.equals("COR")) ? ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_CAD_IP_REDE_INTERNA_CSA_COR, responsavel) : true;

            EnderecoFuncaoTransferObject enderecoFuncaoTo = mapFuncoesTo.get(funCodigo);
            String funDescricao = enderecoFuncaoTo.getFunDescricao();
            String eafIpAcesso = JspHelper.verificaVarQryStr(request, "eafIpAcesso");
            String eafDdnsAcesso = JspHelper.verificaVarQryStr(request, "eafDdnsAcesso");

            if (TextHelper.isNull(eafIpAcesso)) {
                eafIpAcesso = TextHelper.isNull(enderecoFuncaoTo.getEafIpAcesso()) ? "" : enderecoFuncaoTo.getEafIpAcesso();
            }
            if (TextHelper.isNull(eafDdnsAcesso)) {
                eafDdnsAcesso = TextHelper.isNull(enderecoFuncaoTo.getEafDdnsAcesso()) ? "" : enderecoFuncaoTo.getEafDdnsAcesso();
            }

            model.addAttribute("usuCodigo", usuCodigo);
            model.addAttribute("funCodigo", funCodigo);
            model.addAttribute("usuNome", usuNome);
            model.addAttribute("usuLogin", usuLogin);
            model.addAttribute("tipo", tipo);
            model.addAttribute("codEntidade", codEntidade);
            model.addAttribute("podeEdtRestAcessoFun", podeEdtRestAcessoFun);
            model.addAttribute("mapFuncoesTo", mapFuncoesTo);
            model.addAttribute("permiteIpInterno", permiteIpInterno);
            model.addAttribute("funDescricao", funDescricao);
            model.addAttribute("eafIpAcesso", eafIpAcesso);
            model.addAttribute("eafDdnsAcesso", eafDdnsAcesso);

            return viewRedirect("jsp/manterUsuario/editarRestricaoAcessoUsuario", request, session, model, responsavel);

        } catch (UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=editar" })
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Valida o token
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String usuCodigo = JspHelper.verificaVarQryStr(request, "usucodigo");
            String funCodigo = JspHelper.verificaVarQryStr(request, "funcodigo");

            if (TextHelper.isNull(usuCodigo) || TextHelper.isNull(funCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            UsuarioTransferObject usu = usuarioController.findUsuario(usuCodigo, responsavel);
            String usuLogin = usu.getUsuLogin();

            CustomTransferObject usuario = usuarioController.findTipoUsuarioByLogin(usuLogin, responsavel);

            boolean podeEditarUsu = false;
            String tipo = "";
            String codEntidade = "";
            String cseCodigo = usuario.getAttribute(Columns.UCE_CSE_CODIGO) != null ? usuario.getAttribute(Columns.UCE_CSE_CODIGO).toString() : "";
            String csaCodigo = usuario.getAttribute(Columns.UCA_CSA_CODIGO) != null ? usuario.getAttribute(Columns.UCA_CSA_CODIGO).toString() : "";
            String corCodigo = usuario.getAttribute(Columns.UCO_COR_CODIGO) != null ? usuario.getAttribute(Columns.UCO_COR_CODIGO).toString() : "";
            String orgCodigo = usuario.getAttribute(Columns.UOR_ORG_CODIGO) != null ? usuario.getAttribute(Columns.UOR_ORG_CODIGO).toString() : "";
            String cspCodigo = usuario.getAttribute(Columns.USP_CSE_CODIGO) != null ? usuario.getAttribute(Columns.USP_CSE_CODIGO).toString() : "";

            // Determina o tipo da entidade do usuário
            if (!cseCodigo.equals("")) {
                tipo = AcessoSistema.ENTIDADE_CSE;
                codEntidade = cseCodigo;
                podeEditarUsu = responsavel.temPermissao(CodedValues.FUN_EDT_USUARIOS_CSE);
            } else if (!csaCodigo.equals("")) {
                tipo = AcessoSistema.ENTIDADE_CSA;
                codEntidade = csaCodigo;
                podeEditarUsu = responsavel.temPermissao(CodedValues.FUN_EDT_USUARIOS_CSA);
            } else if (!corCodigo.equals("")) {
                tipo = AcessoSistema.ENTIDADE_COR;
                codEntidade = corCodigo;
                podeEditarUsu = responsavel.temPermissao(CodedValues.FUN_EDT_USUARIOS_COR);
            } else if (!orgCodigo.equals("")) {
                tipo = AcessoSistema.ENTIDADE_ORG;
                codEntidade = orgCodigo;
                podeEditarUsu = responsavel.temPermissao(CodedValues.FUN_EDT_USUARIOS_ORG);
            } else if (!cspCodigo.equals("")) {
                tipo = AcessoSistema.ENTIDADE_SUP;
                codEntidade = cspCodigo;
                podeEditarUsu = responsavel.temPermissao(CodedValues.FUN_EDT_USUARIOS_SUP);
            }

            boolean podeEdtRestAcessoFun = podeEditarUsu && responsavel.temPermissao(CodedValues.FUN_EDT_RESTRICAO_ACESSO_POR_FUNCAO);
            // Assumi que se não pode editar, por segurança, não pode visualizar (pois só existe um parâmetro, o que permite editar)
            if (!podeEdtRestAcessoFun) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Valida o tipo de entidade, com a entidade do usuário que está sendo editado
            try {
                validaTipoEntidade(tipo, codEntidade, responsavel);
            } catch (ZetraException e) {
                // Redireciona para página de erro
                session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            //Valores especiais para página de usuários de suporte.
            if (tipo.equals(AcessoSistema.ENTIDADE_SUP)) {
                codEntidade = "1";
            }

            Map<String, EnderecoFuncaoTransferObject> mapFuncoesTo = null;
            try {
                CustomTransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.FUN_CODIGO, funCodigo);
                mapFuncoesTo = usuarioController.selectFuncoes(usuCodigo, codEntidade, tipo, criterio, -1, -1, responsavel);
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            EnderecoFuncaoTransferObject enderecoFuncaoTo = mapFuncoesTo.get(funCodigo);
            String eafIpAcesso = JspHelper.verificaVarQryStr(request, "eafIpAcesso");
            String eafDdnsAcesso = JspHelper.verificaVarQryStr(request, "eafDdnsAcesso");

            enderecoFuncaoTo.setEafIpAcesso(eafIpAcesso);
            enderecoFuncaoTo.setEafDdnsAcesso(eafDdnsAcesso);

            CustomTransferObject dadosTo = new CustomTransferObject();
            dadosTo.setAtributos(enderecoFuncaoTo.getAtributos());
            dadosTo.setAttribute(Columns.USU_CODIGO, usuCodigo);
            dadosTo.setAttribute("COD_ENTIDADE", codEntidade);
            dadosTo.setAttribute("TIPO", tipo);
            try {
                usuarioController.updateEnderecoAcessoFuncao(dadosTo, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alterar.usuario.sucesso", responsavel));
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            // Repassa o token salvo, pois o método irá revalidar o token
            request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

            ParamSession paramSession = ParamSession.getParamSession(session);
            paramSession.halfBack();

            return iniciar(request, response, session, model);

        } catch (UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    /**
     * Valida o tipo de entidade, com a entidade do usuário que está editando o usuário
     * @param request
     * @param responsavel
     * @throws LogControllerException
     * @throws ZetraException
     */
    protected void validaTipoEntidade(String tipo, String codigo, AcessoSistema responsavel) throws LogControllerException, ZetraException {
        if (isTipoEntidadeInvalido(tipo, codigo, responsavel)) {
            // Registra log de erro
            com.zetra.econsig.delegate.LogDelegate log = new com.zetra.econsig.delegate.LogDelegate(responsavel, Log.USUARIO, Log.CREATE, Log.LOG_ERRO_SEGURANCA);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.erro.manutencao.usuario.nao.permitida", responsavel).toUpperCase());
            log.write();
            throw new ZetraException("mensagem.erro.interno.contate.administrador", responsavel);
        }
    }
}