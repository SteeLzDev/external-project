package com.zetra.econsig.web.controller.usuario.servidor;

import java.io.File;
import java.security.KeyPair;
import java.util.Arrays;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.criptografia.RSA;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: AlterarSenhaUsuarioServidorWebController.java</p>
 * <p>Description: Controlador Web para o caso de uso alterar senha de usuário servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/alterarSenhaUsuarioServidor" })
public class AlterarSenhaUsuarioServidorWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AlterarSenhaUsuarioServidorWebController.class);

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    private UsuarioController usuarioController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        String rseMatricula = JspHelper.verificaVarQryStr(request, "RSE_MATRICULA");
        String serCpf = JspHelper.verificaVarQryStr(request, "SER_CPF");
        String orgIdentificador = JspHelper.verificaVarQryStr(request, "ORG_IDENTIFICADOR");
        String orgCodigo = JspHelper.verificaVarQryStr(request, "ORG_CODIGO");
        String estIdentificador = JspHelper.verificaVarQryStr(request, "EST_IDENTIFICADOR");
        String titulo = ApplicationResourcesHelper.getMessage("rotulo.alterar.usuario.servidor.titulo", responsavel);

        RegistroServidorTO registroServidor = new RegistroServidorTO();
        registroServidor.setRseMatricula(rseMatricula);
        registroServidor.setOrgCodigo(orgCodigo);

        RegistroServidorTO rse = null;
        CustomTransferObject servInfo = null;

        try {
            rse = servidorController.findRegistroServidor(registroServidor, responsavel);
            servInfo = pesquisarServidorController.buscaServidor(rse.getRseCodigo(), responsavel);
        } catch (ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Tamanho senha
        int tamMinSenhaServidor = 6;
        int tamMaxSenhaServidor = 8;
        try {
            tamMinSenhaServidor = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MIN_SENHA_SERVIDOR, responsavel)) ? Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MIN_SENHA_SERVIDOR, responsavel).toString()) : 6;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            tamMinSenhaServidor = 6;
        }
        try {
            tamMaxSenhaServidor = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_SERVIDOR, responsavel)) ? Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_SERVIDOR, responsavel).toString()) : 8;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            tamMaxSenhaServidor = 8;
        }

        if (estIdentificador.equals("") || orgIdentificador.equals("")) {
            try {
                OrgaoTransferObject org = consignanteController.findOrgao(orgCodigo, responsavel);
                orgIdentificador = org.getOrgIdentificador();
                EstabelecimentoTransferObject est = consignanteController.findEstabelecimento(org.getEstCodigo(), responsavel);
                estIdentificador = est.getEstIdentificador();
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        String absolutePath = ParamSist.getDiretorioRaizArquivos();
        absolutePath += File.separatorChar + "txt" + File.separatorChar + "principal";

        String msg = "";

        if (absolutePath != null) {
            File arquivo = null;

            String fileName = absolutePath + File.separatorChar + "senha.txt";
            arquivo = new File(fileName);

            try {
                if (arquivo != null && arquivo.exists()) {
                    msg = FileHelper.readAll(arquivo.getAbsolutePath());
                }
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        model.addAttribute("ok", false);
        model.addAttribute("tamMinSenhaServidor",tamMinSenhaServidor);
        model.addAttribute("tamMaxSenhaServidor", tamMaxSenhaServidor);
        model.addAttribute("titulo", titulo);
        model.addAttribute("msg", msg);
        model.addAttribute("acao", "ALTERAR_SENHA");
        model.addAttribute("rseMatricula", rseMatricula);
        model.addAttribute("orgIdentificador", orgIdentificador);
        model.addAttribute("estIdentificador", estIdentificador);
        model.addAttribute("orgCodigo", orgCodigo);
        model.addAttribute("serCpf", serCpf);
        model.addAttribute("rse", rse);
        model.addAttribute("servidor", servInfo);
        model.addAttribute("actionForm", "../v3/alterarSenhaUsuarioServidor?acao=alterar");

        return viewRedirect("jsp/editarUsuarioServidor/editarUsuarioServidor", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=alterar" })
    public String alterarSenhaUsuarioServidor (HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);
        String rseMatricula = JspHelper.verificaVarQryStr(request, "RSE_MATRICULA");
        String orgIdentificador = JspHelper.verificaVarQryStr(request, "ORG_IDENTIFICADOR");
        String orgCodigo = JspHelper.verificaVarQryStr(request, "ORG_CODIGO");
        String estIdentificador = JspHelper.verificaVarQryStr(request, "EST_IDENTIFICADOR");

        RegistroServidorTO registroServidor = new RegistroServidorTO();
        registroServidor.setRseMatricula(rseMatricula);
        registroServidor.setOrgCodigo(orgCodigo);

        RegistroServidorTO rse = null;
        CustomTransferObject servInfo;

        try {
            rse = servidorController.findRegistroServidor(registroServidor, responsavel);
        } catch (ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        boolean senhaServidorNumerica = ParamSist.paramEquals(CodedValues.TPC_SENHA_CONS_SERVIDOR_SOMENTE_NUMERICA, CodedValues.TPC_SIM, responsavel);

        // Verifica parâmetro que indica a forma do login de usuário servidor
        boolean loginComEstOrg = ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, responsavel);
        String login = null;
        if (loginComEstOrg) {
            login = estIdentificador + "-" + orgIdentificador + "-" + rseMatricula;
        } else {
            login = estIdentificador + "-" + rseMatricula;
        }

        String senhaNovaCriptografada = JspHelper.verificaVarQryStr(request, JspHelper.verificaVarQryStr(request, "cryptedPasswordFieldName"));

        try {
            servInfo = pesquisarServidorController.buscaServidor(rse.getRseCodigo(), responsavel);
            model.addAttribute("servidor", servInfo);

            boolean ok = false;

            if (!senhaNovaCriptografada.equals("")) {

                // Decriptografa a senha informada
                KeyPair keyPair = LoginHelper.getRSAKeyPair(request);
                String senhaAberta = RSA.decrypt(senhaNovaCriptografada, keyPair.getPrivate());
                UsuarioTransferObject usuario = usuarioController.findUsuarioByLogin(login, responsavel);

                if (senhaServidorNumerica) {
                    try {
                        Integer.parseInt(senhaAberta);
                        usuarioController.alterarSenha(usuario.getUsuCodigo(), senhaAberta, null, true, false, false, null, responsavel);

                        ok = true;
                    } catch (NumberFormatException nex) {
                        LOG.error(nex.getMessage(), nex);
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.consulta.deve.ser.numerica", responsavel));
                    }
                } else {
                    usuarioController.alterarSenha(usuario.getUsuCodigo(), senhaAberta, null, true, false, false, null, responsavel);
                    ok = true;
                }
                if(ParamSist.paramEquals(CodedValues.TPC_ALTERA_SENHA_TODOS_LOGINS_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                    String[] usuLogins = usuarioController.matriculasUsuariosServidores(usuario.getUsuCodigo(), null, false, responsavel);
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.senhas.usuarios.servidores.alteradas", responsavel, Arrays.toString(usuLogins).replace("[", "").replace("]", "")));
                } else {
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alterar.senha.aut.usuario.sucesso", responsavel));
                }
            }

            model.addAttribute("ok", ok);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String link = "../v3/listarUsuarioServidor?acao=pesquisarServidor&back=1&RSE_MATRICULA = " + rseMatricula + "&SER_CPF=" + servInfo.getAttribute(Columns.SER_CPF);
        link = TextHelper.encode64(SynchronizerToken.updateTokenInURL(link, request));

        request.setAttribute("url64", link);
        return "jsp/redirecionador/redirecionar";
    }
}
