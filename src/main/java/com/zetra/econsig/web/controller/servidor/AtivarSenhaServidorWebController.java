package com.zetra.econsig.web.controller.servidor;

import java.io.File;
import java.security.KeyPair;
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
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.criptografia.RSA;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.senha.SenhaHelper;
import com.zetra.econsig.helper.senhaexterna.SenhaExterna;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: AtivarSenhaServidorWebController</p>
 * <p>Description: Controlador Web para o caso de uso Ativar Senha Servidor.
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author: igor.lucas $
 * $Revision: 26660 $
 * $Date: 2019-05-03 11:50:10 -0300 (sex, 03 mai 2019) $
 */
@Controller
@RequestMapping(value = { "/v3/ativarSenhaServidor" })
public class AtivarSenhaServidorWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AtivarSenhaServidorWebController.class);

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    private UsuarioController usuarioController;

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String senhaCriptografada = JspHelper.verificaVarQryStr(request, "senhaCriptografada");
        String senhaNovaCriptografada = JspHelper.verificaVarQryStr(request, "senhaNovaCriptografada");

        //Valida o token de sessão para evitar a chamada direta à operação
        if ((!TextHelper.isNull(senhaCriptografada) || !TextHelper.isNull(senhaNovaCriptografada)) && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String absolutePath = ParamSist.getDiretorioRaizArquivos();
        absolutePath += File.separatorChar + "txt" + File.separatorChar + "principal";

        String msgSenhaServidor = "";

        int intpwdStrength = 0;
        String pwdStrength = ParamSist.getInstance().getParam(CodedValues.TPC_SER_PWD_STRENGTH_LEVEL, responsavel) != null ? ParamSist.getInstance().getParam(CodedValues.TPC_SER_PWD_STRENGTH_LEVEL, responsavel).toString() : "3";

     // Transforma o parâmetro em um número inteiro
        try {
            intpwdStrength = Integer.parseInt(pwdStrength);
        } catch (NumberFormatException ex) {
            intpwdStrength = 3;
        }

        int pwdStrengthLevel = 1; // very weak

        if (intpwdStrength == 2) { // weak
            pwdStrengthLevel = 16;
        } else if (intpwdStrength == 3) { // mediocre
            pwdStrengthLevel = 25;
        } else if (intpwdStrength >= 4) { // strong
            pwdStrengthLevel = 35;
        }

        if (absolutePath != null) {
            File arquivo = null;

            String fileName = absolutePath + File.separatorChar + "senha_servidor.txt";
            arquivo = new File(fileName);

            try {
                if (arquivo != null && arquivo.exists()) {
                    msgSenhaServidor = FileHelper.readAll(arquivo.getAbsolutePath());
                }
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        String link = JspHelper.verificaVarQryStr(request, "link");
        if (link.equals("lastHistory")) {
            ParamSession paramSession = ParamSession.getParamSession(session);
            link = paramSession.getLastHistory();
        } else if (link.equals("")) {
            link = "../v3/carregarPrincipal?mostraMensagem=true&limitaMsg=true";
        }
        link = SynchronizerToken.updateTokenInURL(link, request);

        model.addAttribute("pwdStrengthLevel", pwdStrengthLevel);
        model.addAttribute("link", link);
        model.addAttribute("msgSenhaServidor", msgSenhaServidor);

        return viewRedirect("jsp/autenticarServidor/ativarSenhaServidor", request, session, model, responsavel);
    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=ativarSenha" })
    public String ativarSenha(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema acessoConsig = AcessoSistema.getAcessoUsuarioSistema();
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String senhaCriptografada = JspHelper.verificaVarQryStr(request, "senhaCriptografada");
        String senhaNovaCriptografada = JspHelper.verificaVarQryStr(request, "senhaNovaCriptografada");

        //Valida o token de sessão para evitar a chamada direta à operação
        if ((!TextHelper.isNull(senhaCriptografada) || !TextHelper.isNull(senhaNovaCriptografada)) && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        boolean senhaAtivada = false;
        try {
            if (!senhaCriptografada.equals("") && !senhaNovaCriptografada.equals("")) {
                String loginExterno = JspHelper.verificaVarQryStr(request, "serLogin");

                // Decriptografa as senhas informadas
                KeyPair keyPair = LoginHelper.getRSAKeyPair(request);
                String senhaAberta = RSA.decrypt(senhaCriptografada, keyPair.getPrivate());
                String senhaNovaAberta = RSA.decrypt(senhaNovaCriptografada, keyPair.getPrivate());

                try {
                    boolean alterou = SenhaExterna.getInstance().atualizarSenha(loginExterno, senhaAberta, senhaNovaAberta, responsavel.getIpUsuario());
                    if (!alterou) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.ativar.senha", responsavel));
                    } else {
                        String msgAlert = ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.salvar.log.auditoria", responsavel);
                        try {
                            // Valida a nova senha para poder pegar o usuário servidor para o log.
                            CustomTransferObject result = SenhaHelper.validarSenhaExternaServidor(loginExterno, senhaNovaAberta, responsavel.getIpUsuario(), null, true, acessoConsig);
                            String resultMatricula = (String) result.getAttribute(SenhaExterna.KEY_RG);
                            String resultCPF = (String) result.getAttribute(SenhaExterna.KEY_CPF);
                            String msgErro = (String) result.getAttribute(SenhaExterna.KEY_ERRO);
                            if (!TextHelper.isNull(msgErro)) {
                                try {
                                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage(msgErro, responsavel));
                                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                                } catch (RuntimeException rex) {
                                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.ativar.senha", responsavel));
                                    LOG.error(rex.getMessage(), rex);
                                }
                            } else if (resultMatricula == null) {
                                session.setAttribute(CodedValues.MSG_ALERT, msgAlert);
                                LOG.warn(msgAlert);
                            } else {
                                List<TransferObject> servidores = pesquisarServidorController.pesquisaServidor("CSE", "1", null, null, resultMatricula, resultCPF, acessoConsig, false, false);
                                // Se não encontrar, tenta novamente sem o CPF.
                                if (servidores == null || servidores.isEmpty()) {
                                    servidores = pesquisarServidorController.pesquisaServidor("CSE", "1", null, null, resultMatricula, null, acessoConsig, false, false);
                                }
                                if (servidores == null || servidores.isEmpty()) {
                                    session.setAttribute(CodedValues.MSG_ALERT, msgAlert);
                                    LOG.warn(msgAlert);
                                } else {
                                    CustomTransferObject servidor = (CustomTransferObject) servidores.get(0);
                                    String estIdentificador = consignanteController.findEstabelecimento(consignanteController.findOrgao(servidor.getAttribute(Columns.ORG_CODIGO).toString(), acessoConsig).getEstCodigo(), acessoConsig).getEstIdentificador();
                                    String usuLogin = estIdentificador + "-" + servidor.getAttribute(Columns.RSE_MATRICULA);

                                    CustomTransferObject usuario = usuarioController.findTipoUsuarioByLogin(usuLogin, acessoConsig);
                                    if (usuario == null) {
                                        session.setAttribute(CodedValues.MSG_ALERT, msgAlert);
                                        LOG.warn(msgAlert);
                                    } else {
                                        // Grava ocorrência de alteração de senha do usuário
                                        CustomTransferObject ocorrencia = new CustomTransferObject();
                                        ocorrencia.setAttribute(Columns.OUS_USU_CODIGO, usuario.getAttribute(Columns.USU_CODIGO).toString());
                                        ocorrencia.setAttribute(Columns.OUS_TOC_CODIGO, CodedValues.TOC_ALTERACAO_SENHA_USUARIO);
                                        ocorrencia.setAttribute(Columns.OUS_OUS_USU_CODIGO, responsavel.getUsuCodigo());
                                        ocorrencia.setAttribute(Columns.OUS_OBS, ApplicationResourcesHelper.getMessage("rotulo.ativar.senha.servidor.externa.ocorrencia", responsavel));
                                        ocorrencia.setAttribute(Columns.OUS_IP_ACESSO, responsavel.getIpUsuario());

                                        usuarioController.createOcorrenciaUsuario(ocorrencia, responsavel);

                                        senhaAtivada = true;
                                    }
                                }
                            }
                        } catch (UsuarioControllerException | ServidorControllerException | ConsignanteControllerException ex) {
                            /*
                             * Somente imprime mensagem de erro pois a ativação foi bem sucedida.
                             * Deu erro somente no momento de fazer o log.
                             */
                            LOG.error(ex.getMessage(), ex);
                        }
                        if (senhaAtivada) {
                            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.ativar.senha.servidor.sucesso", responsavel));
                        }
                    }
                } catch (com.zetra.econsig.exception.UsuarioControllerException ex) {
                    String msg1 = ex.getMessage();
                    if (msg1 == null && ex.getCause() != null) {
                        msg1 = ex.getCause().getMessage();
                    }
                    LOG.debug("UsuarioControllerException: " + msg1);
                    session.setAttribute(CodedValues.MSG_ERRO, msg1);
                }
            }
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("senhaAtivada", senhaAtivada);

        return iniciar(request, response, session, model);
    }

}
