package com.zetra.econsig.web.controller.reinicializarsenha;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.senha.GeradorSenhaUtil;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ReinicializarSenhaServidorWebController.java</p>
 * <p>Description: Controlador Web para o caso de uso reinicializar senha de usuario.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/reinicializarSenhaServidor" })
public class ReinicializarSenhaServidorWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ReinicializarSenhaServidorWebController.class);

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @RequestMapping(params = { "acao=iniciarReinicializacaoSenhaSer" })
    public String iniciarReinicializacaoSenhaSer(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        SynchronizerToken.saveToken(request);

        try {
            final String usuCodigo = JspHelper.verificaVarQryStr(request, "USU_CODIGO");
            final UsuarioTransferObject usuario = usuarioController.findUsuario(usuCodigo, responsavel);
            final TransferObject servidor = pesquisarServidorController.buscaUsuarioServidor(usuCodigo, responsavel);
            final String serEmail = (String) servidor.getAttribute(Columns.SER_EMAIL);

            final String tipo = JspHelper.verificaVarQryStr(request, "tipo");
            final String operacao = JspHelper.verificaVarQryStr(request, "operacao");

            // Se não existe motivo de operação, segue direto para a operação
            final boolean exigeMotivoOperacaoUsu = ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_TIPO_MOTIVO_OPERACAO_USUARIO, responsavel) && FuncaoExigeMotivo.getInstance().exists(responsavel.getFunCodigo(), responsavel);
            if (!exigeMotivoOperacaoUsu) {
                return reinicializarSenha(usuCodigo, null, null, tipo, operacao, request, response, session, model);
            }

            final String reqColumnsStr = "TMO_CODIGO";
            final String msgErro = JspHelper.verificaCamposForm(request, session, reqColumnsStr, ApplicationResourcesHelper.getMessage("mensagem.campos.obrigatorios", responsavel), "100%");
            final Map<String, String[]> parametros = new HashMap<>();
            parametros.put("USU_CODIGO", new String[] { usuCodigo });
            parametros.put("USU_LOGIN", new String[] { usuario.getUsuLogin() });
            parametros.put("operacao", new String[] { operacao });
            parametros.put("tipo", new String[] { tipo });
            parametros.put("_skip_history_", new String[] { "true" });

            model.addAttribute("serEmail", serEmail);
            model.addAttribute("usuario", usuario);
            model.addAttribute("parametros", parametros);
            model.addAttribute("msgErro", msgErro);
            model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.efetiva.acao.usuario.reinicializar.senha", responsavel));
            model.addAttribute("msgConfirmacao", ApplicationResourcesHelper.getMessage("mensagem.confirmacao.reinicializar.senha.usuario", responsavel, usuario.getUsuLogin()));
            model.addAttribute("urlDestino", "../v3/reinicializarSenhaServidor?acao=reinicializarSenhaSer");

            return viewRedirect("jsp/editarUsuarioServidor/efetivarAcaoUsuario", request, session, model, responsavel);

        } catch (UsuarioControllerException | ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=reinicializarSenhaSer" })
    public String reinicializarSenhaServidor(@RequestParam(value = "USU_CODIGO", required = true, defaultValue = "") String usuCodigo,
            @RequestParam(value = "TMO_CODIGO", required = true, defaultValue = "") String tmoCodigo,
            @RequestParam(value = "ADE_OBS", required = true, defaultValue = "") String ousObs,
            @RequestParam(value = "tipo", required = true, defaultValue = "") String tipo,
            @RequestParam(value = "operacao", required = true, defaultValue = "") String operacao,
            HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return reinicializarSenha(usuCodigo, tmoCodigo, ousObs, tipo, operacao, request, response, session, model);
    }

    private String reinicializarSenha(@RequestParam(value = "USU_CODIGO", required = true, defaultValue = "") String usuCodigo,
                                     @RequestParam(value = "TMO_CODIGO", required = true, defaultValue = "") String tmoCodigo,
                                     @RequestParam(value = "ADE_OBS", required = true, defaultValue = "") String ousObs,
                                     @RequestParam(value = "tipo", required = true, defaultValue = "") String tipo,
                                     @RequestParam(value = "operacao", required = true, defaultValue = "") String operacao,
                                     HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            final boolean isSer = "reinicializar-ser".equals(operacao);
            if (!isSer) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Exige tipo de motivo da operacao
            final boolean exigeMotivoOperacaoUsu = ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_TIPO_MOTIVO_OPERACAO_USUARIO, responsavel) && FuncaoExigeMotivo.getInstance().exists(responsavel.getFunCodigo(), responsavel);
            if (exigeMotivoOperacaoUsu && TextHelper.isNull(tmoCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.motivo.operacao.obrigatorio", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // informa o motivo da operação para ser gravado junto com a ocorrência de usuário
            CustomTransferObject tmo = null;
            if (!TextHelper.isNull(tmoCodigo)) {
                tmo = new CustomTransferObject();
                tmo.setAttribute(Columns.USU_CODIGO, usuCodigo);
                tmo.setAttribute(Columns.TMO_CODIGO, tmoCodigo);
                tmo.setAttribute(Columns.OUS_OBS, ousObs);
            }

            if (!ParamSist.paramEquals(CodedValues.TPC_EMAIL_REINICIALIZACAO_SENHA, CodedValues.TPC_SIM, responsavel)) {
                // Define o tamanho da senha ser gerada, caso seja necessário.
                // Para usuário servidor, o tamanho da senha deve respeitar o parâmetro de sistema
                // com o tamanho máximo que deve ter.
                int tamanhoSenha = 8;
                try {
                    final Object paramTamMaxSenha = ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_SERVIDOR, responsavel);
                    if (!TextHelper.isNull(paramTamMaxSenha)) {
                        tamanhoSenha = Integer.valueOf(paramTamMaxSenha.toString());
                    }
                } catch (NumberFormatException ex) {
                    LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.tamanho.senha", responsavel) + ": " + ex.getMessage());
                }

                final boolean senhaNumerica = ParamSist.paramEquals(CodedValues.TPC_SENHA_CONS_SERVIDOR_SOMENTE_NUMERICA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                final String usuSenha = senhaNumerica ? GeradorSenhaUtil.getPasswordNumber(tamanhoSenha, responsavel) : GeradorSenhaUtil.getPassword(tamanhoSenha, AcessoSistema.ENTIDADE_SER, responsavel);
                usuarioController.alterarSenha(usuCodigo, usuSenha, null, true, true, false, tmo, null, responsavel);

                String msgSucesso = ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.reinicializada", responsavel);
                String msgSenha = "<br><font class=\"novaSenha\"> " + ApplicationResourcesHelper.getMessage("rotulo.usuario.nova.senha", responsavel) + ": " + usuSenha + "</font>";
                session.setAttribute(CodedValues.MSG_INFO, msgSucesso + msgSenha);

            } else {
                final UsuarioTransferObject dadosUsuario = usuarioController.findUsuario(usuCodigo, responsavel);

                // Busca o status do usuário
                final String stuCodigo = dadosUsuario.getAttribute(Columns.USU_STU_CODIGO).toString();

                if (!stuCodigo.equals(CodedValues.STU_ATIVO)) {
                    // Se usuário bloqueado, não pode reinicializar senha
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usuario.bloqueado", responsavel));
                } else {
                    final TransferObject usuarioSer = pesquisarServidorController.buscaUsuarioServidor(usuCodigo, responsavel);

                    // Encontrou Usuário e Servidor, verifica se possui e-mail cadastrado
                    final String serEmail = (String) usuarioSer.getAttribute(Columns.SER_EMAIL);
                    final String serCpf = (String) usuarioSer.getAttribute(Columns.SER_CPF);

                    final boolean omiteCpfServidor = ParamSist.paramEquals(CodedValues.TPC_OMITE_CPF_SERVIDOR, CodedValues.TPC_SIM, responsavel);
                    if (TextHelper.isNull(serEmail)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.email.nao.cadastrado", responsavel));
                    } else if (!omiteCpfServidor && TextHelper.isNull(serCpf)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.cpf.nao.cadastrado", responsavel));
                    } else {
                        // O servidor possui e-mail, então envia email com link para alterar senha
                        String linkReinicializacao = request.getRequestURL().toString();
                        linkReinicializacao = linkReinicializacao.replace("/v3/reinicializarSenhaServidor", "/v3/recuperarSenhaServidor?acao=iniciarServidor");
                        linkReinicializacao += "&enti=" + request.getParameter("tipo");
                        linkReinicializacao = SynchronizerToken.updateTokenInURL(linkReinicializacao, request);
                        // Gera uma nova codigo de recuparação de senha
                        final String cod_Senha = SynchronizerToken.generateToken();
                        // Atualiza o codigo de recuperação de senha do usuário
                        usuarioController.alteraChaveRecupSenha(usuCodigo, cod_Senha, responsavel);
                        // Envia e-mail com link para recuperação de senha
                        usuarioController.enviaLinkReinicializarSenhaSer(usuCodigo, null, linkReinicializacao, cod_Senha, responsavel);
                        // Inválida senha do usuário
                        usuarioController.alterarSenha(usuCodigo, "senha", null, false, true, true, tmo, null, responsavel);
                        // Retorna mensagem de sucesso para o usuário
                        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.reinicializar.senha.usuario.sucesso", responsavel, TextHelper.escondeEmail(serEmail)));
                    }
                }
            }
        } catch (UsuarioControllerException | ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(ParamSession.getParamSession(session).getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }
}