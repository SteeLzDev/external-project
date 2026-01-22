package com.zetra.econsig.web.controller.usuario.servidor;

import java.io.File;
import java.security.KeyPair;
import java.util.Calendar;

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
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.criptografia.RSA;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.senha.SenhaHelper;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: InserirUsuarioServidorWebController.java</p>
 * <p>Description: Controlador Web para o caso de uso inserir usuário servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/inserirUsuarioServidor" })
public class InserirUsuarioServidorWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(InserirUsuarioServidorWebController.class);

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    private UsuarioController usuarioController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ZetraException {

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
        String titulo = ApplicationResourcesHelper.getMessage("rotulo.cadastrar.usuario.servidor.titulo", responsavel);

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
        model.addAttribute("tamMinSenhaServidor", tamMinSenhaServidor);
        model.addAttribute("tamMaxSenhaServidor", tamMaxSenhaServidor);
        model.addAttribute("titulo", titulo);
        model.addAttribute("msg", msg);
        model.addAttribute("acao", "INSERIR");
        model.addAttribute("rseMatricula", rseMatricula);
        model.addAttribute("orgIdentificador", orgIdentificador);
        model.addAttribute("estIdentificador", estIdentificador);
        model.addAttribute("orgCodigo", orgCodigo);
        model.addAttribute("serCpf", serCpf);
        model.addAttribute("rse", rse);
        model.addAttribute("servidor", servInfo);
        model.addAttribute("actionForm", "../v3/inserirUsuarioServidor?acao=inserir");
        model.addAttribute("serTel", TextHelper.isNull(servInfo.getAttribute(Columns.SER_TEL)) ? "" : servInfo.getAttribute(Columns.SER_TEL).toString());
        model.addAttribute("serCel", TextHelper.isNull(servInfo.getAttribute(Columns.SER_CELULAR)) ? "" : servInfo.getAttribute(Columns.SER_CELULAR).toString());
        model.addAttribute("serEmail", TextHelper.isNull(servInfo.getAttribute(Columns.SER_EMAIL)) ? "" : servInfo.getAttribute(Columns.SER_EMAIL).toString());
        model.addAttribute("emailObrigatorio", ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_USU_SERVIDOR_EMAIL, responsavel));
        model.addAttribute("celularSerEditavel", ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_USU_SERVIDOR_CELULAR, responsavel));
        model.addAttribute("telefoneSerEditavel", ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_USU_SERVIDOR_TELEFONE, responsavel));
        model.addAttribute("emailSerEditavel", ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_USU_SERVIDOR_EMAIL, responsavel));
        model.addAttribute("novoUsuarioSer", true);

        return viewRedirect("jsp/editarUsuarioServidor/editarUsuarioServidor", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=inserir" })
    public String inserirUsuarioServidor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ZetraException {
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
        String email = JspHelper.verificaVarQryStr(request, "email");
        String ddd = JspHelper.verificaVarQryStr(request, "ddd");
        String dddcel = JspHelper.verificaVarQryStr(request, "dddcel");
        String telefone = JspHelper.verificaVarQryStr(request, "telefone");
        String celular = JspHelper.verificaVarQryStr(request, "celular");

        if (!validaCamposObrigatorios(request, session, responsavel)) {
            request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));
            return iniciar(request, response, session, model);
        }

        RegistroServidorTO registroServidor = new RegistroServidorTO();
        registroServidor.setRseMatricula(rseMatricula);
        registroServidor.setOrgCodigo(orgCodigo);

        RegistroServidorTO rse = null;
        ServidorTransferObject servidor;

        try {
            rse = servidorController.findRegistroServidor(registroServidor, responsavel);
            servidor = servidorController.findServidor(rse.getSerCodigo(), responsavel);
        } catch (ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (!TextHelper.isNull(email) && !email.isEmpty()) {
            boolean existeEmailCadastrado = servidorController.existeEmailCadastrado(email.trim(), servidor.getSerCpf(), responsavel);
            if (existeEmailCadastrado) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.email.informado.em.uso.outro.cpf", responsavel));
                request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));
                return iniciar(request, response, session, model);
            }
        }

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
            CustomTransferObject servInfo = pesquisarServidorController.buscaServidor(rse.getRseCodigo(), responsavel);
            model.addAttribute("servidor", servInfo);

            boolean ok = false;

            if (!senhaNovaCriptografada.equals("")) {

                // Decriptografa a senha informada
                KeyPair keyPair = LoginHelper.getRSAKeyPair(request);
                String senhaAberta = RSA.decrypt(senhaNovaCriptografada, keyPair.getPrivate());
                String senhaNovaCrypt = SenhaHelper.criptografarSenha(login, senhaAberta, true, responsavel);
                Calendar cal = Calendar.getInstance();

                // Seta prazo de expiração da senha de acordo com o parâmetro de sistema
                String prazo = ParamSist.getInstance().getParam(CodedValues.TPC_PRAZO_EXPIRACAO_SENHA_USU_SER, responsavel).toString();
                cal.add(Calendar.DATE, Integer.parseInt(prazo));

                UsuarioTransferObject usuario = new UsuarioTransferObject();
                usuario.setStuCodigo(CodedValues.STU_ATIVO);
                usuario.setUsuDataCad(DateHelper.toSQLDate(DateHelper.getSystemDatetime()));
                usuario.setUsuDataExpSenha(DateHelper.toSQLDate(cal.getTime()));
                usuario.setUsuLogin(login);
                usuario.setUsuSenha(senhaNovaCrypt);
                usuario.setUsuNome(servidor.getSerNome());
                usuario.setUsuCPF(servidor.getSerCpf());

                // Se exige atualização de dados cadastrais no primeiro acesso do servidor
                if (ParamSist.paramEquals(CodedValues.TPC_EXIGE_ATUALIZACAO_DADOS_SER_PRIMEIRO_ACESSO, CodedValues.TPC_SIM, responsavel)) {
                    // Verifica se existe email cadastrado ou se o e-mail foi preenchido
                    if ((servidor.getSerEmail() == null || servidor.getSerEmail().isEmpty()) && (TextHelper.isNull(email))) {
                        throw new ZetraException("mensagem.erro.servidor.email.nao.cadastrado", responsavel);
                    }

                    // Verifica se existe telefone cadastrado ou se o celular foi preenchido
                    if ((servidor.getSerTel() == null || servidor.getSerTel().isEmpty()) && (TextHelper.isNull(telefone))) {
                        throw new ZetraException("mensagem.erro.servidor.telefone.nao.cadastrado", responsavel);
                    }
                }

                if ((ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_USU_SERVIDOR_EMAIL, responsavel))) {
                    if (!TextHelper.isNull(email) && !TextHelper.isEmailValid(email)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.email.invalido", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                    servidor.setSerEmail(email);
                }

                if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_USU_SERVIDOR_TELEFONE, responsavel)) {
                    // Atualiza telefone
                    telefone = TextHelper.isNull(ddd) ? telefone : ddd + '-' + telefone;
                    servidor.setSerTel(telefone);
                }

                if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_USU_SERVIDOR_CELULAR, responsavel)) {
                    // Atualiza celular
                    celular = TextHelper.isNull(dddcel) ? celular : dddcel + '-' + celular;
                    servidor.setSerCelular(celular);
                }

                //Update no servidor para refletir alterações
                servidorController.updateServidor(servidor, responsavel);

                //Criação do usuário
                usuarioController.createUsuario(usuario, CodedValues.PER_CODIGO_SERVIDOR, rse.getSerCodigo(), AcessoSistema.ENTIDADE_SER, senhaAberta, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.inserir.usuario.sucesso", responsavel));
                ok = true;
            }

            model.addAttribute("ok", ok);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String link = "../v3/listarUsuarioServidor?acao=pesquisarServidor&back=1&RSE_MATRICULA = " + rseMatricula + "&SER_CPF=" + servidor.getSerCpf();
        link = TextHelper.encode64(SynchronizerToken.updateTokenInURL(link, request));

        request.setAttribute("url64", link);
        return "jsp/redirecionador/redirecionar";
    }

    public boolean validaCamposObrigatorios(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) throws ZetraException {

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_USU_SERVIDOR_CELULAR, responsavel) && TextHelper.isNull(JspHelper.verificaVarQryStr(request, "celular"))) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.ser.celular", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_USU_SERVIDOR_TELEFONE, responsavel) && TextHelper.isNull(JspHelper.verificaVarQryStr(request, "telefone"))) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.ser.telefone", responsavel));
            return false;
        }

        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_USU_SERVIDOR_EMAIL, responsavel) && TextHelper.isNull(JspHelper.verificaVarQryStr(request, "email"))) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.ser.email", responsavel));
            return false;
        }

        return true;
    }

}
