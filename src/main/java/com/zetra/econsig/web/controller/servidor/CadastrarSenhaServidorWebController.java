package com.zetra.econsig.web.controller.servidor;

import java.io.File;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.EnderecoFuncaoTransferObject;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.OcorrenciaUsuarioTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.dto.web.CampoQuestionarioDadosServidor;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.criptografia.RSA;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.senha.SenhaHelper;
import com.zetra.econsig.helper.sistema.CreateImageHelper;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.usuario.UsuarioHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.job.process.ProcessaPoliticaPrivacidade;
import com.zetra.econsig.report.config.ConfigRelatorio;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.arquivo.ArquivoController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.upload.UploadController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.web.controller.AbstractWebController;
import com.zetra.econsig.web.servlet.ImageCaptchaServlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.JspException;

/**
 * <p>Title: CadastrarSenhaServidorWebController</p>
 * <p>Description: Controlador Web para o caso de uso cadastrar senha de usuário servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(value = { "/v3/cadastrarSenhaServidor" })
public class CadastrarSenhaServidorWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CadastrarSenhaServidorWebController.class);

    private static final String INPUT_MASK = "MASK";
    private static final String INPUT_TYPE = "TYPE";
    private static final String INPUT_NAME = "NAME";
    private static final String INPUT_VALUE = "VALUE";
    private static final String INPUT_LABEL = "LABEL";
    private static final String INPUT_OPTION_LABEL = "OPTION_LABEL";
    private static final String INPUT_OPTION_VALUE = "OPTION_VALUE";
    private static final String INPUT_PLACEHOLDER = "PLACEHOLDERE";
    private static final String INPUT_JAVASCRIPT = "JAVASCRIPT";
    private static final String TIPO_PERGUNTA = "TIPO_PERGUNTA";

    // Constantes para tipo de pergunta. Perguntas do mesmo tipo não podem ser exibidas ao mesmo tempo.
    private static final String PERGUNTA_PARENTESCO = "1";

    private static final int QUANTIDADE_PERGUNTAS = 3;
    private static final int QUANTIDADE_RESPOSTAS = 10;
    private static final int LIMITE_TENTATIVAS = 10;

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private UploadController uploadController;

    @Autowired
    private ArquivoController arquivoController;

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        // Cria um responsável mesmo sem ter o código do usuário para registrar no log o IP de acesso e a função acessada
        final AcessoSistema responsavel = new AcessoSistema(CodedValues.USU_CODIGO_SISTEMA, JspHelper.getRemoteAddr(request), JspHelper.getRemotePort(request));
        responsavel.setTipoEntidade(AcessoSistema.ENTIDADE_SER);
        responsavel.setFunCodigo(CodedValues.FUN_AUTO_CADASTRO_SENHA_SERVIDOR);

        try {
            // Cadastro avançado de usuário servidor
            final boolean cadastroAvancadoUsuSer = ParamSist.paramEquals(CodedValues.TPC_HABILITA_CADASTRO_AVANCADO_USU_SER, CodedValues.TPC_SIM, responsavel);
            final boolean usaRecuperacaoSenhaNoAutoCadastro = ParamSist.paramEquals(CodedValues.TPC_USA_RECUPERACAO_SENHA_AUTO_CADASTRO_SER, CodedValues.TPC_SIM, responsavel);
            final boolean habilitaRecuperacaoSenhaServidor = ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_RECUPERAR_SENHA_SER, CodedValues.TPC_SIM, responsavel);
            final boolean loginServidorComCpf = ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_CPF, CodedValues.TPC_SIM, responsavel);

            if (usaRecuperacaoSenhaNoAutoCadastro && !habilitaRecuperacaoSenhaServidor) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.auto.cadastro.senha.ser.erro.parametro.recuperacao.senha.desabilitado", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Recupera o login para definir se é CPF ou Matrícula e salvar para utilização na página
            final String login = JspHelper.verificaVarQryStr(request, "username");
            if (loginServidorComCpf) {
                model.addAttribute("cpf", login);
            } else {
                model.addAttribute("matricula", login);
            }

            if (cadastroAvancadoUsuSer) {
                // monta lista de órgãos para seleção
                final List<TransferObject> listaOrgaos = consignanteController.lstOrgaos(null, responsavel);
                model.addAttribute("listaOrgaos", listaOrgaos);

                // redireciona para a página de selecionar servidor para cadastro de senha
                return viewRedirect("jsp/cadastrarSenhaServidor/selecionarServidorCadastroSenha", request, session, model, responsavel);
            } else {
                // redireciona para a página de informar dados do servidor
                return informarDadosServidor(request, response, session, model);
            }

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, params = { "acao=selecionarServidor" })
    public String selecionarServidor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        // Cria um responsável mesmo sem ter o código do usuário para registrar no log o IP de acesso e a função acessada
        final AcessoSistema responsavel = new AcessoSistema(CodedValues.USU_CODIGO_SISTEMA, JspHelper.getRemoteAddr(request), JspHelper.getRemotePort(request));
        responsavel.setTipoEntidade(AcessoSistema.ENTIDADE_SER);
        responsavel.setFunCodigo(CodedValues.FUN_AUTO_CADASTRO_SENHA_SERVIDOR);

        try {
            final String cpf = (String) (model.getAttribute("cpf") != null ? model.getAttribute("cpf") : JspHelper.getFieldValue(request, FieldKeysConstants.CAD_SERVIDOR_CPF, "", true, responsavel));
            String matricula = (String) (model.getAttribute("matricula") != null ? model.getAttribute("matricula") : JspHelper.getFieldValue(request, FieldKeysConstants.CAD_SERVIDOR_MATRICULA, "", true, responsavel));
            final String orgCod = (String) (model.getAttribute("orgCod") != null ? model.getAttribute("orgCod") : JspHelper.getFieldValue(request, FieldKeysConstants.CAD_SERVIDOR_ORG_CODIGO, "", true, responsavel));

            String serCodigo = null;
            CustomTransferObject servidor = null;

            // Busca o servidor
            servidor = pesquisarServidor(cpf, matricula, orgCod, responsavel);
            if (servidor != null) {
                serCodigo = (String) servidor.getAttribute(Columns.SER_CODIGO);
                matricula = (String) servidor.getAttribute(Columns.RSE_MATRICULA);
            }

            final String orgIdentificador = (String) servidor.getAttribute(Columns.ORG_IDENTIFICADOR);
            final String estIdentificador = (String) servidor.getAttribute(Columns.EST_IDENTIFICADOR);
            UsuarioTransferObject usuarioSer = null;

            // Verifica parâmetro que indica a forma do login de usuário servidor
            final boolean loginComEstOrg = ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, responsavel);
            String usuLogin = null;
            if (loginComEstOrg) {
                usuLogin = estIdentificador + "-" + orgIdentificador + "-" + matricula;
            } else {
                usuLogin = estIdentificador + "-" + matricula;
            }

            // Verifica se o usuário já existe
            try {
                usuarioSer = usuarioController.findUsuarioByLogin(usuLogin, responsavel);
            } catch (final Exception ex) {
            }

            final boolean habilitaRecSenha = ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_RECUPERAR_SENHA_SER, CodedValues.TPC_SIM, responsavel);
            final boolean redirecionaRecSenha = ParamSist.paramEquals(CodedValues.TPC_REDIRECIONA_SER_CAD_SENHA, CodedValues.TPC_SIM, responsavel);
            if((usuarioSer != null) && habilitaRecSenha && redirecionaRecSenha && !CodedValues.USU_SENHA_SERVIDOR_INICIAL.equalsIgnoreCase(usuarioSer.getUsuSenha())) {
                final List<TransferObject> usuariosComMesmoCPF = usuarioController.lstUsuariosSerLoginComCpf(null, null, cpf, null, null, true, responsavel);
                if (!usuariosComMesmoCPF.isEmpty() && !TextHelper.isNull(usuariosComMesmoCPF)) {
                    return alterarServidoresComCPF(usuariosComMesmoCPF, request, response, session, model, responsavel);
                } else {
                    // Não localizou o servidor, retorna mensagem de erro
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel));
                    return iniciar(request, response, session, model);
                }
            }

            final List<TransferObject> listEstadoCivil = servidorController.getEstCivil(responsavel);

            model.addAttribute("listEstadoCivil", listEstadoCivil);
            model.addAttribute("cpf", cpf);
            model.addAttribute("matricula", matricula);
            model.addAttribute("orgCod", orgCod);
            model.addAttribute("serCodigo", serCodigo);
            model.addAttribute("servidor", servidor);

            // recupera campos para montar o questionário
            final List<CampoQuestionarioDadosServidor> camposQuestionarioDadosServidor = montarQuestionarioServidor(servidor, responsavel);
            model.addAttribute("camposQuestionarioDadosServidor", camposQuestionarioDadosServidor);

            return informarDadosServidor(request, response, session, model);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return iniciar(request, response, session, model);
        }
    }

    public String alterarServidoresComCPF(List<TransferObject> usuariosComMesmoCPF, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model, AcessoSistema responsavel) throws ZetraException {
        // Início validação para saber se os usuarios servidores possuem o mesmo e-mail. Caso contrário retorna erro
        final Set<String> emailsServidores = new HashSet<>();
        for (final TransferObject usuarioCPF : usuariosComMesmoCPF) {
            try {
                final ServidorTransferObject servidor = servidorController.findServidor(usuarioCPF.getAttribute(Columns.SER_CODIGO).toString(), AcessoSistema.recuperaAcessoSistemaByLogin(usuarioCPF.getAttribute(Columns.USU_LOGIN).toString(), JspHelper.getRemoteAddr(request), JspHelper.getRemotePort(request)));
                emailsServidores.add(servidor.getSerEmail());
            } catch (final ZetraException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ZetraException("mensagem.erro.servidor.nao.encontrado", responsavel);
            }
        }

        // Emitir uma mensagem de erro caso o CPF informado tenha mais de um registro na tabela "tb_servidor", com e-mails diferentes, e matrículas ativas (caso de uma pessoa usando CPF de outra, como pai/filho, cônjuges, etc).
        if (emailsServidores.size() > 1) {
            throw new ZetraException("mensagem.erro.email.servidor.diferente.mesmo.cpf", responsavel);
        }

        // Gera um novo codigo de recuparação de senha
        final String codRecSenha = SynchronizerToken.generateToken();

        String usuLogin = null;
        String link = null;
        String usuCodigo = null;
        String serEmail = null;

        // Verifica se existe mais de um usuário com mesmo CPF, utilizando a mesma lógica do caso de uso AutenticarServidor com parâmetro de sistema 674
        for (final TransferObject usu : usuariosComMesmoCPF) {
            usuLogin = usu.getAttribute(Columns.USU_LOGIN).toString();

            String serCodigo = null;

            // Envia email com link pra alteração de senha
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.info.login.servidor", responsavel, usuLogin));
            List<RegistroServidorTO> registroServidor = new ArrayList<>();

            try {
                // Busca o registro servidor que não deve estar na situação de excluído
                serCodigo = usu.getAttribute(Columns.SER_CODIGO).toString();

                final ServidorTransferObject servidor = servidorController.findServidor(serCodigo, responsavel);

                if (servidor == null) {
                    throw new ZetraException("mensagem.erro.servidor.nao.encontrado", responsavel);
                }

                registroServidor = servidorController.findRegistroServidorBySerCodigo(serCodigo, responsavel);
                if (registroServidor.isEmpty() || (registroServidor == null)) {
                    throw new ZetraException("mensagem.erro.servidor.nao.encontrado", responsavel);
                }

                // Encontrou Usuário e Servidor, verifica se possui e-mail cadastrado
                serEmail = servidor.getSerEmail();
                emailsServidores.add(serEmail);
                final String serCpf = servidor.getSerCpf();

                if (TextHelper.isNull(serEmail)) {
                    throw new ZetraException("mensagem.erro.servidor.email.nao.cadastrado", responsavel);
                }
                if (TextHelper.isNull(serCpf)) {
                    throw new ZetraException("mensagem.erro.servidor.cpf.nao.cadastrado", responsavel);
                }

            } catch (final ServidorControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return iniciar(request, response, session, model);

            }

            // valida IP/DNS de acesso
            UsuarioHelper.verificarIpDDNSAcesso(AcessoSistema.ENTIDADE_SER, serCodigo, JspHelper.getRemoteAddr(request), (String) usu.getAttribute(Columns.USU_IP_ACESSO), (String) usu.getAttribute(Columns.USU_DDNS_ACESSO), (String) usu.getAttribute(Columns.USU_CODIGO), responsavel);

            // O servidor possui e-mail, então envia email com link para alterar senha
            usuCodigo = (String) usu.getAttribute(Columns.USU_CODIGO);
            responsavel = AcessoSistema.recuperaAcessoSistemaByLogin(usuLogin, JspHelper.getRemoteAddr(request), JspHelper.getRemotePort(request));
            link = request.getRequestURL().toString();
            link = link.replace("cadastrarSenhaServidor", "recuperarSenha");
            link += "?acao=iniciarServidor&enti=" + responsavel.getTipoEntidade();

            try {
                // Atualiza o codigo de recuperação de senha do usuário
                usuarioController.alteraChaveRecupSenha(usuCodigo, codRecSenha, responsavel);
            } catch (final UsuarioControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return iniciar(request, response, session, model);
            }
        }

        //Mesmo que tenha um ou mais usuários, deve-se enviar apenas um único e-mail para reinicializar senha
        // Envia e-mail com link para recuperação de senha
        usuarioController.enviaLinkReinicializarSenhaSer(usuCodigo, null, link, codRecSenha, responsavel);
        // Retorna mensagem de sucesso para o usuário
        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.cad.senha.servidor.ja.cadastrado.email.enviado", responsavel, TextHelper.escondeEmail(serEmail)));
        return viewRedirect("jsp/recuperarSenha/recuperarSenhaSerPasso2", request, session, model, responsavel);
    }

    public String informarDadosServidor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        // Cria um responsável mesmo sem ter o código do usuário para registrar no log o IP de acesso e a função acessada
        final AcessoSistema responsavel = new AcessoSistema(CodedValues.USU_CODIGO_SISTEMA, JspHelper.getRemoteAddr(request), JspHelper.getRemotePort(request));
        responsavel.setTipoEntidade(AcessoSistema.ENTIDADE_SER);
        responsavel.setFunCodigo(CodedValues.FUN_AUTO_CADASTRO_SENHA_SERVIDOR);
        try {
            // Cadastro avançado de usuário servidor
            final boolean cadastroAvancadoUsuSer = ParamSist.paramEquals(CodedValues.TPC_HABILITA_CADASTRO_AVANCADO_USU_SER, CodedValues.TPC_SIM, responsavel);
            final boolean usaRecuperacaoSenhaNoAutoCadastro = ParamSist.paramEquals(CodedValues.TPC_USA_RECUPERACAO_SENHA_AUTO_CADASTRO_SER, CodedValues.TPC_SIM, responsavel);
            final boolean senhaServidorNumerica = ParamSist.paramEquals(CodedValues.TPC_SENHA_CONS_SERVIDOR_SOMENTE_NUMERICA, CodedValues.TPC_SIM, responsavel);
            final boolean atualizaDadosServidor = ParamSist.paramEquals(CodedValues.TPC_ATUALIZA_DADOS_SERVIDOR_CAD_SENHA, CodedValues.TPC_SIM, responsavel);

            //DESENV-18083 - INICIO
            final String absolutePath = ParamSist.getDiretorioRaizArquivos();
            final String nomeArquivo = "cad_senha.msg";

            File arqTermoUsoCadSenha = null;
            String msgTermoUsoCadSenha;
            arqTermoUsoCadSenha = new File(absolutePath + File.separatorChar + "termo_de_uso", nomeArquivo);

            if (!arqTermoUsoCadSenha.exists()) {
                try {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.termo.de.uso.nao.encontrado", responsavel));
                } catch (final Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                }
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            } else {
                msgTermoUsoCadSenha = FileHelper.readAll(arqTermoUsoCadSenha.getAbsolutePath());
            }

            msgTermoUsoCadSenha = msgTermoUsoCadSenha.replace("\n", "<br>");

            File arqPoliticaPrivacidadeCadSenha = null;
            String msgPoliticaPrivacidadeCadSenha = "";
            arqPoliticaPrivacidadeCadSenha = new File(absolutePath + File.separatorChar + "politica_privacidade", nomeArquivo);

            if (arqPoliticaPrivacidadeCadSenha.exists()) {
                msgPoliticaPrivacidadeCadSenha = FileHelper.readAll(arqPoliticaPrivacidadeCadSenha.getAbsolutePath());

                final String caminhoBackgroudbase = absolutePath + File.separatorChar + "imagem" + File.separatorChar + "fundo_contracheque.png";
                final String caminhoBackgroud = CreateImageHelper.gerarImagemTransparente(caminhoBackgroudbase);

                final Relatorio relatorio = ConfigRelatorio.getInstance().getRelatorio("politica_privacidade");
                final Map<String, String[]> parameterMap = new HashMap<>();
                parameterMap.put("CORPO", new String[] { msgPoliticaPrivacidadeCadSenha });
                parameterMap.put("CAMINHO_BACKGROUND", new String[] { caminhoBackgroud });

                final String nomeArquivoPoliticaPrivacidade = "politica_privacidade";
                parameterMap.put(ReportManager.REPORT_FILE_NAME, new String[] { nomeArquivoPoliticaPrivacidade });

                final String exportDir = absolutePath + File.separatorChar + "relatorio" + File.separatorChar + "cse" + File.separatorChar + "politica_privacidade";

                final File dir = new File(exportDir);
                if (!dir.exists()) {
                    dir.mkdir();
                }

                parameterMap.put(ReportManager.REPORT_DIR_EXPORT, new String[] { exportDir });

                final ProcessaPoliticaPrivacidade processaPoliticaPrivacidade = new ProcessaPoliticaPrivacidade(relatorio, parameterMap, null, responsavel);
                processaPoliticaPrivacidade.run();

                final File politicaPrivacidadeFile = new File(exportDir + File.separatorChar + nomeArquivoPoliticaPrivacidade + ".pdf");
                final String fileBase64 = Base64.encodeBase64String(FileUtils.readFileToByteArray(politicaPrivacidadeFile));

                model.addAttribute("politicaPrivacidadeFile", fileBase64);

                msgPoliticaPrivacidadeCadSenha = msgPoliticaPrivacidadeCadSenha.replace("\n", "<br>");

            } else {
                msgPoliticaPrivacidadeCadSenha = FileHelper.readAll(arqPoliticaPrivacidadeCadSenha.getAbsolutePath());
            }

            model.addAttribute("termoUsoCadSenha", msgTermoUsoCadSenha);
            model.addAttribute("politicaPrivacidadeCadSenha", msgPoliticaPrivacidadeCadSenha);
            //DESENV-18083 - FIM

            final String domainParam = (String) ParamSist.getInstance().getParam(CodedValues.TPC_DOMINIO_EMAIL_CAD_SENHA, responsavel);
            final List<TransferObject> domainEmail = new ArrayList<>();

            if(!TextHelper.isNull(domainParam)) {
                for(final String domain : domainParam.split(";")) {
                    final CustomTransferObject item = new CustomTransferObject();
                    item.setAttribute("domain", domain);
                    domainEmail.add(item);
                }
                model.addAttribute("allowedDomains", domainEmail);
            }

            final List<TransferObject> listaAnexos = uploadController.buscaTipoArquivoSer(responsavel);
            if(!listaAnexos.isEmpty()) {
                model.addAttribute("listaAnexos", listaAnexos);
            }

            final String tamMax = (String) ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_ARQ_ANEXO_REGISTRO_SERVIDOR, responsavel);

            model.addAttribute("tamMax", tamMax);
            model.addAttribute("usaRecuperacaoSenhaNoAutoCadastro", usaRecuperacaoSenhaNoAutoCadastro);
            model.addAttribute("cadastroAvancadoUsuSer", cadastroAvancadoUsuSer);
            model.addAttribute("senhaServidorNumerica", senhaServidorNumerica);
            model.addAttribute("atualizaDadosServidor", atualizaDadosServidor);

            // monta lista de órgãos para seleção
            final List<TransferObject> listaOrgaos = consignanteController.lstOrgaos(null, responsavel);
            model.addAttribute("listaOrgaos", listaOrgaos);
            model.addAttribute("celularSerEditavel", ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_USU_SERVIDOR_CELULAR, responsavel));
            model.addAttribute("telefoneSerEditavel", ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_USU_SERVIDOR_TELEFONE, responsavel));
            model.addAttribute("emailSerEditavel", ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_USU_SERVIDOR_EMAIL, responsavel));

            return viewRedirect("jsp/cadastrarSenhaServidor/cadastrarSenhaServidor", request, session, model, responsavel);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        // Cria um responsável mesmo sem ter o código do usuário para registrar no log o IP de acesso e a função acessada
        final AcessoSistema responsavel = new AcessoSistema(CodedValues.USU_CODIGO_SISTEMA, JspHelper.getRemoteAddr(request), JspHelper.getRemotePort(request));
        final boolean cadastroAvancadoUsuSer = ParamSist.paramEquals(CodedValues.TPC_HABILITA_CADASTRO_AVANCADO_USU_SER, CodedValues.TPC_SIM, responsavel);
        final String tamMax = (String) ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_ARQ_ANEXO_REGISTRO_SERVIDOR, responsavel);
        final UploadHelper uploadHelper = new UploadHelper();
        Map<String, File> salvarArquivos = null;

        try {
            uploadHelper.processarRequisicao(request.getServletContext(), request, Integer.parseInt(tamMax) * 1024);

            String msgSucess = "";
            boolean ok = false;

            final boolean servidorCadastraSenha = ParamSist.paramEquals(CodedValues.TPC_SERVIDOR_CADASTRA_SENHA, CodedValues.TPC_SIM, responsavel);
            final boolean senhaServidorNumerica = ParamSist.paramEquals(CodedValues.TPC_SENHA_CONS_SERVIDOR_SOMENTE_NUMERICA, CodedValues.TPC_SIM, responsavel);
            final boolean omiteCpfServidor = ParamSist.paramEquals(CodedValues.TPC_OMITE_CPF_SERVIDOR, CodedValues.TPC_SIM, responsavel);
            final boolean usaRecuperacaoSenhaNoAutoCadastro = ParamSist.paramEquals(CodedValues.TPC_USA_RECUPERACAO_SENHA_AUTO_CADASTRO_SER, CodedValues.TPC_SIM, responsavel);
            final boolean atualizaDadosServidor = ParamSist.paramEquals(CodedValues.TPC_ATUALIZA_DADOS_SERVIDOR_CAD_SENHA, CodedValues.TPC_SIM, responsavel);

            final String cpf = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_CPF);
            String matricula = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_MATRICULA);
            final String identidade = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_IDENTIDADE);
            final String carteira = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_NO_CARTEIRA);
            final String pis = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_PIS);
            final String str_dt_nasc = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_DATA_NASC);
            final String dataAdmissao = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_DATA_ADMISSAO);
            final String priNomeMae = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_PRIMEIRO_NOME_MAE);
            final String orgCod = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_ORG_CODIGO);

            final String bcoSal = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_EDITAR_BANCO_SAL);
            final String agSal = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_EDITAR_AGENCIA_SALARIO);
            final String ccSal = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_EDITAR_CONTA_SALARIO);
            final String bcosal2 = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_EDITAR_BANCO_SAL_2);
            final String agSal2 = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_EDITAR_AGENCIA_SALARIO2);
            final String ccSal2 = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_EDITAR_CONTA_SALARIO2);
            final String associado = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_EDITAR_ASSOCIADO);
            final String clt = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_EDITAR_CLT);
            final String dataFimEng = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_EDITAR_DATA_FIM_ENGAJAMENTO);
            final String dataLimPerm = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_EDITAR_DATA_LIMITE_PERMANENCIA);
            final String estabilizado = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_EDITAR_ESTABILIZADO);
            final String lotacao = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_EDITAR_MUNICIPIO_LOTACAO);
            final String praca = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_EDITAR_PRACA);
            final String rsePrazo = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_EDITAR_PRAZO);

            final String pisEdicao = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_EDITAR_PIS);
            final String bairro = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_EDITAR_BAIRRO);
            final String cartProf = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_EDITAR_CART_PROF);
            final String cep = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_EDITAR_CEP);
            final String cidNasc = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_EDITAR_CID_NASC);
            final String cidade = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_EDITAR_CIDADE);
            final String complemento = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_EDITAR_COMPL);
            final String dataidentidade = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_EDITAR_DATA_IDT);
            final String emissorIdentidade = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_EDITAR_EMISSOR_IDT);
            final String endereco = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_EDITAR_END);
            final String estCivil = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_EDITAR_EST_CIVIL);
            final String nacionalidade = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_EDITAR_NACIONALIDADE);
            final String nomeConjuge = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_EDITAR_NOME_CONJUGE);
            final String nomePai = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_EDITAR_NOME_PAI);
            final String numero = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_EDITAR_NRO);
            final String nroIdentidade = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_EDITAR_NRO_IDT);
            final String uf = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_EDITAR_UF);
            final String ufIdentidade = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_EDITAR_UF_IDT);
            final String ufNascimento = JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_EDITAR_UF_NASC);
            final String senhaCriptografada = JspHelper.verificaVarQryStr(request, uploadHelper, JspHelper.verificaVarQryStr(request, uploadHelper, "cryptedPasswordFieldName"));
            final String dica = (JspHelper.verificaVarQryStr(request, uploadHelper, "dica") != null) ? JspHelper.verificaVarQryStr(request, uploadHelper, "dica") : "";
            String email = JspHelper.verificaVarQryStr(request, uploadHelper, "email");
            String emailConfirmacao = JspHelper.verificaVarQryStr(request, uploadHelper, "email_confirmacao");

            final String domainEmail = JspHelper.verificaVarQryStr(request, uploadHelper, "domain_email");
            final String domainEmailConfirmacao = JspHelper.verificaVarQryStr(request, uploadHelper, "domain_confirmacao");

            final String domainParam = (String) ParamSist.getInstance().getParam(CodedValues.TPC_DOMINIO_EMAIL_CAD_SENHA, responsavel);

            if((domainParam != null) && !TextHelper.isNull(domainEmail) && !TextHelper.isNull(domainEmailConfirmacao)) {
                email = email+"@"+domainEmail;
                emailConfirmacao = emailConfirmacao+"@"+domainEmailConfirmacao;
            }

            //DESENV-18083 - INICIO
            final String termoDeUso = JspHelper.verificaVarQryStr(request, uploadHelper, "termoDeUso");
            final String politicaPrivacidade = JspHelper.verificaVarQryStr(request, uploadHelper, "politicaPrivacidade");
            boolean existeArqPoliticaPrivacidade = false;

            final String absolutePath = ParamSist.getDiretorioRaizArquivos();
            final File arqPoliticaPrivacidadeCadSenha = new File(absolutePath + File.separatorChar + "politica_privacidade", "cad_senha.msg");

            if (arqPoliticaPrivacidadeCadSenha.exists()) {
                existeArqPoliticaPrivacidade = true;
            }
            //DESENV-18083 - FIM

            CustomTransferObject servidor = null;
            if ((!TextHelper.isNull(cpf) || omiteCpfServidor) && !TextHelper.isNull(matricula)) {
                servidor = pesquisarServidor(cpf, matricula, orgCod, responsavel);
                if (servidor != null) {
                    matricula = (String) servidor.getAttribute(Columns.RSE_MATRICULA);
                }
            }

            // Se por algum motivo náo foi possível encontrar um servidor, antes de fazer dar exceção por buscar algo com getAttributte mais embaixo, já lança exceção.
            if (servidor == null) {
                throw new ZetraException("mensagem.erro.servidor.nao.encontrado", responsavel);
            }

            final List<TransferObject> listaAnexos = uploadController.buscaTipoArquivoSer(responsavel);

            if(atualizaDadosServidor && !listaAnexos.isEmpty()) {
                final String path = UploadHelper.SUBDIR_ARQUIVOS_TEMPORARIOS + File.separatorChar + "anexos_cad_senha_servidor";
                salvarArquivos = uploadHelper.salvarArquivos(path, UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_CONTRATO, null);

                for(final TransferObject file : listaAnexos) {
                    final File anexo = salvarArquivos.get("btn-arquivo-"+file.getAttribute(Columns.TAR_CODIGO).toString());
                    if ((anexo != null) && anexo.exists()) {
                        final byte[] fileContent = FileUtils.readFileToByteArray(anexo);
                        final byte[] conteudoArquivoBase64 = Base64.encodeBase64(fileContent);

                       final CustomTransferObject arquivo = new CustomTransferObject();
                       arquivo.setAttribute(Columns.SER_CODIGO, servidor.getAttribute(Columns.SER_CODIGO));
                       arquivo.setAttribute(Columns.ARQ_CONTEUDO, conteudoArquivoBase64);
                       arquivo.setAttribute(Columns.ARQ_TAR_CODIGO, file.getAttribute(Columns.TAR_CODIGO));
                       arquivo.setAttribute(Columns.ASE_NOME, anexo.getName());

                       arquivoController.createArquivoServidor(arquivo, responsavel);
                    }
                }
            }

            String senhaAberta = null;
            if (!TextHelper.isNull(senhaCriptografada)) {
                // Decriptografa a senha informada
                final KeyPair keyPair = LoginHelper.getRSAKeyPair(request);

                try {
                    senhaAberta = RSA.decrypt(senhaCriptografada, keyPair.getPrivate());
                    if (senhaAberta == null) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.usuario.senha", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }

                    if (senhaServidorNumerica) {
                        try {
                            Integer.parseInt(senhaAberta);
                        } catch (final NumberFormatException e) {
                            // senha com caracteres inválidos quando configurado para ser só numérico
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.consulta.deve.ser.numerica", responsavel));
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                        }
                    }
                } catch (final javax.crypto.BadPaddingException e) {
                    // Corresponde a tentativa de decriptografia com chave errada. A sessão pode ter expirado. Tentar novamente.
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            UsuarioTransferObject usuarioSer = null;

            if (!TextHelper.isNull(matricula) && (!TextHelper.isNull(senhaCriptografada) || (usaRecuperacaoSenhaNoAutoCadastro && !TextHelper.isNull(email) && !TextHelper.isNull(emailConfirmacao)))) {
                try {
                    final String serEmail = (String) servidor.getAttribute(Columns.SER_EMAIL);

                    // Permite que apenas servidores que ainda não possuam e-mail realizem o auto cadastro de usuário quando o parâmetro para utilizar a recuperação de senha tiver habilitado
                    if (usaRecuperacaoSenhaNoAutoCadastro && !TextHelper.isNull(serEmail)) {
                        throw new ZetraException("mensagem.erro.cadastrar.senha.servidor.email.ja.cadastrado", responsavel);
                    }

                    // Verifica o captcha
                    final String captchaAnswer = JspHelper.verificaVarQryStr(request, uploadHelper, "captcha");
                    if (ImageCaptchaServlet.armazenaCaptcha(session.getId(), (String) session.getAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY))
                            && !ImageCaptchaServlet.validaCaptcha(session.getId(), captchaAnswer)) {
                        throw new ZetraException("mensagem.erro.captcha.invalido", responsavel);
                    }

                    final String orgCodigo = (String) servidor.getAttribute(Columns.ORG_CODIGO);
                    final String orgIdentificador = (String) servidor.getAttribute(Columns.ORG_IDENTIFICADOR);
                    final String estCodigo = (String) servidor.getAttribute(Columns.EST_CODIGO);
                    final String estIdentificador = (String) servidor.getAttribute(Columns.EST_IDENTIFICADOR);

                    if (cadastroAvancadoUsuSer) {
                        // Verifica quantidade de perguntas respondidas
                        int quantidadePerguntas = 0;

                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_NOME_MAE, Columns.SER_NOME_MAE, "SER_NOME_MAE", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_NOME_PAI, Columns.SER_NOME_PAI, "SER_NOME_PAI", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_NOME_CONJUGE, Columns.SER_NOME_CONJUGE, "SER_NOME_CONJUGE", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_TELEFONE, Columns.SER_TEL, "SER_TEL", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_CELULAR, Columns.SER_CELULAR, "SER_CELULAR", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_ENDERECO, Columns.SER_END, "SER_END", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_NUM_ENDERECO, Columns.SER_NRO, "SER_NRO", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_BAIRRO, Columns.SER_BAIRRO, "SER_BAIRRO", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_CEP, Columns.SER_CEP, "SER_CEP", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_CIDADE, Columns.SER_CIDADE, "SER_CIDADE", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_MUNICIPIO_LOTACAO, Columns.RSE_MUNICIPIO_LOTACAO, "RSE_MUNICIPIO_LOTACAO", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_CIDADE_NASC, Columns.SER_CID_NASC, "SER_CID_NASC", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_DIA_NASC, Columns.SER_DATA_NASC, "SER_DIA_NASC", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_MES_NASC, Columns.SER_DATA_NASC, "SER_MES_NASC", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_ANO_NASC, Columns.SER_DATA_NASC, "SER_ANO_NASC", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_DIA_ADMISSAO, Columns.RSE_DATA_ADMISSAO, "RSE_DIA_ADMISSAO", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_MES_ADMISSAO, Columns.RSE_DATA_ADMISSAO, "RSE_MES_ADMISSAO", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_ANO_ADMISSAO, Columns.RSE_DATA_ADMISSAO, "RSE_ANO_ADMISSAO", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_NUM_IDENTIDADE, Columns.SER_NRO_IDT, "SER_NRO_IDT", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_NUM_CART_TRABALHO, Columns.SER_CART_PROF, "SER_CART_PROF", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_NUM_PIS, Columns.SER_PIS, "SER_PIS", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_DIA_IDT, Columns.SER_DATA_IDT, "SER_DIA_IDT", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_MES_IDT, Columns.SER_DATA_IDT, "SER_MES_IDT", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_ANO_IDT, Columns.SER_DATA_IDT, "SER_ANO_IDT", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_CATEGORIA, Columns.RSE_TIPO, "RSE_TIPO", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_AGENCIA_SALARIO, Columns.RSE_AGENCIA_SAL, "RSE_AGENCIA_SAL", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_CONTA_SALARIO, Columns.RSE_CONTA_SAL, "RSE_CONTA_SAL", servidor, uploadHelper, request, responsavel);

                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_AGENCIA_SALARIO2, Columns.RSE_AGENCIA_SAL_2, "RSE_AGENCIA_SAL_2", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_ASSOCIADO, Columns.RSE_ASSOCIADO, "RSE_ASSOCIADO", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_BANCO_SAL, Columns.RSE_BANCO_SAL, "RSE_BANCO_SAL", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_BANCO_SAL_2, Columns.RSE_BANCO_SAL_2, "RSE_BANCO_SAL_2", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_CLT, Columns.RSE_CLT, "RSE_CLT", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_CONTA_SALARIO2, Columns.RSE_CONTA_SAL_2, "RSE_CONTA_SAL_2", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_DATA_FIM_ENGAJAMENTO, "FORMATED_DATA_FIM_ENGAJAMENTO", "RSE_DATA_FIM_ENGAJAMENTO", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_DATA_LIMITE_PERMANENCIA, "FORMATED_DATA_LIMITE_PERMANENCIA", "RSE_DATA_LIMITE_PERMANENCIA", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_ESTABILIZADO, Columns.RSE_ESTABILIZADO, "RSE_ESTABILIZADO", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_PRACA, Columns.RSE_PRACA, "RSE_PRACA", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_PRAZO, Columns.RSE_PRAZO, "RSE_PRAZO", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_COMPL, Columns.SER_COMPL, "SER_COMPL", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_EMISSOR_IDT, Columns.SER_EMISSOR_IDT, "SER_EMISSOR_IDT", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_EST_CIVIL, Columns.SER_EST_CIVIL, "SER_EST_CIVIL", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_NACIONALIDADE, Columns.SER_NACIONALIDADE, "SER_NACIONALIDADE", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_UF, Columns.SER_UF, "SER_UF", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_UF_IDT, Columns.SER_UF_IDT, "SER_UF_IDT", servidor, uploadHelper, request, responsavel);
                        quantidadePerguntas += validarCampoCadastroAvancado(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_UF_NASC, Columns.SER_UF_NASC, "SER_UF_NASC", servidor, uploadHelper, request, responsavel);

                        // Valida quantas respostas foram enviadas
                        if (quantidadePerguntas != QUANTIDADE_PERGUNTAS) {
                            throw new ZetraException("mensagem.erro.cadastrar.senha.servidor.pergunta.nao.respondida", responsavel);
                        }

                    } else {
                        // consistências só são feitas quando os respectivos campos estão visíveis na tela
                        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_DATA_NASC, responsavel)) {
                            final String dtNascServidor = (servidor.getAttribute(Columns.SER_DATA_NASC) != null ? DateHelper.format((Date) servidor.getAttribute(Columns.SER_DATA_NASC), LocaleHelper.getDatePattern()) : null);
                            if ((dtNascServidor != null) && !dtNascServidor.equals(str_dt_nasc)) {
                                throw new ZetraException("mensagem.erro.servidor.nao.encontrado", responsavel);
                            }
                        }

                        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_IDENTIDADE, responsavel)) {
                            final String idntServidor = (String) servidor.getAttribute(Columns.SER_NRO_IDT);
                            if ((idntServidor != null) && !idntServidor.trim().equalsIgnoreCase(identidade.trim())) {
                                throw new ZetraException("mensagem.erro.servidor.nao.encontrado", responsavel);
                            }
                        }

                        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_NO_CARTEIRA, responsavel)) {
                            final String noCartServidor = (String) servidor.getAttribute(Columns.SER_CART_PROF);
                            if ((noCartServidor != null) && !noCartServidor.trim().equals(carteira.trim())) {
                                throw new ZetraException("mensagem.erro.servidor.nao.encontrado", responsavel);
                            }
                        }

                        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_PIS, responsavel)) {
                            final String pisServidor = (String) servidor.getAttribute(Columns.SER_PIS);
                            if ((pisServidor != null) && !pisServidor.trim().equals(pis.trim())) {
                                throw new ZetraException("mensagem.erro.servidor.nao.encontrado", responsavel);
                            }
                        }

                        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_DATA_ADMISSAO, responsavel)) {
                            final java.util.Date rseDataAdmissao = (java.util.Date) servidor.getAttribute(Columns.RSE_DATA_ADMISSAO);

                            if (rseDataAdmissao != null) {
                                if (dataAdmissao != null) {
                                    final Date dataAdmissaoParsed = DateHelper.parse(dataAdmissao, LocaleHelper.getDatePattern());

                                    if (DateHelper.dayDiff(rseDataAdmissao, dataAdmissaoParsed) != 0) {
                                        throw new ZetraException("mensagem.erro.servidor.nao.encontrado", responsavel);
                                    }
                                } else {
                                    throw new ZetraException("mensagem.erro.servidor.nao.encontrado", responsavel);
                                }
                            } else if (!TextHelper.isNull(dataAdmissao)) {
                                throw new ZetraException("mensagem.erro.servidor.nao.encontrado", responsavel);
                            }
                        }

                        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_PRIMEIRO_NOME_MAE, responsavel)) {
                            final String buscaPrimeiroNomeMae = !TextHelper.isNull(servidor.getAttribute(Columns.SER_NOME_MAE)) ? ((String) servidor.getAttribute(Columns.SER_NOME_MAE)).split(" ")[0].trim() : "";
                            final String primeiroNomeTela = !TextHelper.isNull(priNomeMae) ? (priNomeMae).split(" ")[0].trim() : "";
                            if (!TextHelper.isNull(buscaPrimeiroNomeMae) && !buscaPrimeiroNomeMae.toLowerCase().equals(primeiroNomeTela.toLowerCase())) {
                                throw new ZetraException("mensagem.erro.servidor.nao.encontrado", responsavel);
                            }
                        }
                    }

                    if (!usaRecuperacaoSenhaNoAutoCadastro) {
                        session.removeAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY);
                    }

                    if (!usaRecuperacaoSenhaNoAutoCadastro && (senhaAberta.length() < getTamMinSenhaServidor(responsavel))) {
                        throw new ZetraException("mensagem.erro.cadastrar.senha.servidor.minimo", responsavel);
                    } else if (!usaRecuperacaoSenhaNoAutoCadastro && (senhaAberta.length() > getTamMaxSenhaServidor(responsavel))) {
                        throw new ZetraException("mensagem.erro.cadastrar.senha.servidor.maximo", responsavel);
                    } else {
                        // Verifica parâmetro que indica a forma do login de usuário servidor
                        final boolean loginComEstOrg = ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, responsavel);
                        String usuLogin = null;
                        if (loginComEstOrg) {
                            usuLogin = estIdentificador + "-" + orgIdentificador + "-" + matricula;
                        } else {
                            usuLogin = estIdentificador + "-" + matricula;
                        }

                        // Verifica se o usuário já existe
                        try {
                            usuarioSer = usuarioController.findUsuarioByLogin(usuLogin, responsavel);
                        } catch (final Exception ex) {
                        }

                        if ((usuarioSer != null) && !cadastroAvancadoUsuSer && !servidorCadastraSenha) {
                            throw new ZetraException("mensagem.erro.cadastrar.senha.servidor.ja.cadastrado", responsavel);
                        } else {
                            final boolean criaUsuarioSer = (usuarioSer == null);

                            // Se usuário não existir, cria um novo
                            if (criaUsuarioSer) {
                                usuarioSer = new UsuarioTransferObject();
                                usuarioSer.setUsuLogin(usuLogin);
                                usuarioSer.setUsuCPF(cpf);
                                usuarioSer.setUsuNome((String) servidor.getAttribute(Columns.SER_NOME));
                            }

                            usuarioSer.setUsuDicaSenha(dica);
                            usuarioSer.setStuCodigo(CodedValues.STU_ATIVO);
                            if (usaRecuperacaoSenhaNoAutoCadastro) {
                                // Gera uma senha aleatória, pois o campo é obrigatório no banco de dados.
                                usuarioSer.setUsuSenha(SynchronizerToken.generateToken());
                            } else {
                                // Criptografa a senha do usuário servidor
                                final String senhaCrypt = SenhaHelper.criptografarSenha(usuLogin, senhaAberta, true, responsavel);
                                usuarioSer.setUsuSenha(senhaCrypt);
                            }

                            // Se permite alteração de e-mail, verifica se o e-mail informado é igual ao e-mail de confirmação
                            if (!TextHelper.isNull(email)) {
                                if (!email.equals(emailConfirmacao)) {
                                    throw new ZetraException("mensagem.erro.cadastrar.senha.servidor.email", responsavel);
                                } else if (!TextHelper.isEmailValid(email)) {
                                    throw new ZetraException("mensagem.erro.email.invalido", responsavel);
                                }
                                usuarioSer.setUsuEmail(email);
                            }

                            final String prazo = ParamSist.getInstance().getParam(CodedValues.TPC_PRAZO_EXPIRACAO_SENHA_USU_SER, responsavel).toString();
                            final Calendar cal = Calendar.getInstance();
                            cal.add(Calendar.DATE, Integer.parseInt(prazo));

                            usuarioSer.setUsuDataExpSenha(new java.sql.Date(cal.getTimeInMillis()));

                            // Se usuário não existir, cria um novo
                            final String tipoEntidade = AcessoSistema.ENTIDADE_SER;
                            final String codigoEntidade = (String) servidor.getAttribute(Columns.SER_CODIGO);
                            final String rseCodigo = (String) servidor.getAttribute(Columns.RSE_CODIGO);
                            final String usuCodigo = (criaUsuarioSer ? CodedValues.USU_CODIGO_SISTEMA : usuarioSer.getAttribute(Columns.USU_CODIGO).toString());
                            // Cria um AcessoSistema da entidade SER para permitir que o usuário servidor seja criado corretamente
                            final AcessoSistema usuAcesso = new AcessoSistema(usuCodigo, JspHelper.getRemoteAddr(request), JspHelper.getRemotePort(request));
                            usuAcesso.setTipoEntidade(tipoEntidade);
                            usuAcesso.setCodigoEntidade(codigoEntidade);

                            final boolean emailTelCelPodeEditar = ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_USU_SERVIDOR_CELULAR, responsavel) || ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_USU_SERVIDOR_TELEFONE, responsavel) || ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_USU_SERVIDOR_EMAIL, responsavel);

                            if (cadastroAvancadoUsuSer || usaRecuperacaoSenhaNoAutoCadastro || emailTelCelPodeEditar || atualizaDadosServidor) {
                                final String ddd = JspHelper.verificaVarQryStr(request, uploadHelper, "ddd");
                                final String dddcel = JspHelper.verificaVarQryStr(request, uploadHelper, "dddcel");
                                String telefone = JspHelper.verificaVarQryStr(request, uploadHelper, "telefone");
                                String celular = JspHelper.verificaVarQryStr(request, uploadHelper, "celular");

                                final ServidorTransferObject servidorTO = servidorController.findServidor(codigoEntidade, responsavel);
                                final RegistroServidorTO registroSer = servidorController.findRegistroServidor(rseCodigo, responsavel);

                                if (!validaObrigatoriedadeEmailTelCelServidor(uploadHelper, request, session, responsavel)) {
                                    request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));
                                    return iniciar(request, response, session, model);
                                }

                                // Se o e-mail informado é inválido, ou não foi informado e é obrigatório, retorna erro
                                if ((!TextHelper.isNull(email) && !TextHelper.isEmailValid(email)) || (TextHelper.isNull(email) && usaRecuperacaoSenhaNoAutoCadastro)) {
                                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.email.invalido", responsavel));
                                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                                } else if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_USU_SERVIDOR_EMAIL, responsavel)) {
                                    servidorTO.setSerEmail(email);
                                }

                                if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_USU_SERVIDOR_TELEFONE, responsavel)) {
                                    // Atualiza telefone
                                    telefone = TextHelper.isNull(ddd) ? telefone : ddd + '-' + telefone;
                                    servidorTO.setSerTel(telefone);
                                }

                                if (ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_USU_SERVIDOR_CELULAR, responsavel)) {
                                    // Atualiza celular
                                    celular = TextHelper.isNull(dddcel) ? celular : dddcel + '-' + celular;
                                    servidorTO.setSerCelular(celular);
                                }

                                //somente dados que não estão presentes no banco serão salvos
                                if(atualizaDadosServidor) {
                                    if(!TextHelper.isNull(bcoSal) && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_EDITAR_BANCO_SAL, responsavel) && TextHelper.isNull(registroSer.getRseBancoSal())) {
                                        registroSer.setRseBancoSal(bcoSal);
                                    }
                                    if(!TextHelper.isNull(agSal) && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_EDITAR_AGENCIA_SALARIO, responsavel) && TextHelper.isNull(registroSer.getRseAgenciaSal())) {
                                        registroSer.setRseAgenciaSal(agSal);
                                    }
                                    if(!TextHelper.isNull(ccSal) && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CONTA_SALARIO, responsavel) && TextHelper.isNull(registroSer.getRseContaSal())) {
                                        registroSer.setRseContaSal(ccSal);
                                    }
                                    if(!TextHelper.isNull(bcosal2) && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_EDITAR_BANCO_SAL_2, responsavel) && TextHelper.isNull(registroSer.getRseBancoSalAlternativo())) {
                                        registroSer.setRseBancoSalAlternativo(bcosal2);
                                    }
                                    if(!TextHelper.isNull(agSal2) && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_EDITAR_AGENCIA_SALARIO2, responsavel) && TextHelper.isNull(registroSer.getRseAgenciaSalAlternativa())) {
                                        registroSer.setRseAgenciaSalAlternativa(agSal2);
                                    }
                                    if(!TextHelper.isNull(ccSal2) && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CONTA_SALARIO2, responsavel) && TextHelper.isNull(registroSer.getRseContaSalAlternativa())) {
                                        registroSer.setRseContaSalAlternativa(ccSal2);
                                    }
                                    if(!TextHelper.isNull(associado) && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_EDITAR_ASSOCIADO, responsavel) && TextHelper.isNull(registroSer.getRseAssociado())) {
                                        registroSer.setRseAssociado(associado);
                                    }
                                    if(!TextHelper.isNull(clt) && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CLT, responsavel) && TextHelper.isNull(registroSer.getRseCLT())) {
                                        registroSer.setRseCLT(clt);
                                    }
                                    if(!TextHelper.isNull(dataFimEng) && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_EDITAR_DATA_FIM_ENGAJAMENTO, responsavel) && TextHelper.isNull(registroSer.getRseDataFimEngajamento())) {
                                        registroSer.setRseDataFimEngajamento(new java.sql.Timestamp(DateHelper.parse(dataFimEng, LocaleHelper.getDatePattern()).getTime()));
                                    }
                                    if(!TextHelper.isNull(dataLimPerm) && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_EDITAR_DATA_LIMITE_PERMANENCIA, responsavel) && TextHelper.isNull(registroSer.getRseDataLimitePermanencia())) {
                                        registroSer.setRseDataLimitePermanencia(new java.sql.Timestamp(DateHelper.parse(dataLimPerm, LocaleHelper.getDatePattern()).getTime()));
                                    }
                                    if(!TextHelper.isNull(estabilizado) && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_EDITAR_ESTABILIZADO, responsavel) && TextHelper.isNull(registroSer.getRseEstabilizado())) {
                                        registroSer.setRseEstabilizado(estabilizado);
                                    }
                                    if(!TextHelper.isNull(lotacao) && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_EDITAR_MUNICIPIO_LOTACAO, responsavel) && TextHelper.isNull(registroSer.getRseMunicipioLotacao())) {
                                        registroSer.setRseMunicipioLotacao(lotacao);
                                    }
                                    if(!TextHelper.isNull(praca) && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_EDITAR_PRACA, responsavel) && TextHelper.isNull(registroSer.getRsePraca())) {
                                        registroSer.setRsePraca(praca);
                                    }
                                    if(!TextHelper.isNull(rsePrazo) && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_EDITAR_PRAZO, responsavel) && TextHelper.isNull(registroSer.getRsePrazo())) {
                                        registroSer.setRsePrazo(Integer.valueOf(rsePrazo.toString()));
                                    }

                                    if(!TextHelper.isNull(pisEdicao) && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_EDITAR_PIS, responsavel) && TextHelper.isNull(servidorTO.getSerPis())) {
                                        servidorTO.setSerPis(pisEdicao);
                                    }
                                    if(!TextHelper.isNull(bairro) && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_EDITAR_BAIRRO, responsavel) && TextHelper.isNull(servidorTO.getSerBairro())) {
                                        servidorTO.setSerBairro(bairro);
                                    }
                                    if(!TextHelper.isNull(cartProf) && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CART_PROF, responsavel) && TextHelper.isNull(servidorTO.getSerCartProf())) {
                                        servidorTO.setSerCartProf(cartProf);
                                    }
                                    if(!TextHelper.isNull(cep) && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CEP, responsavel) && TextHelper.isNull(servidorTO.getSerCep())) {
                                        servidorTO.setSerCep(cep);
                                    }
                                    if(!TextHelper.isNull(cidNasc) && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CID_NASC, responsavel) && TextHelper.isNull(servidorTO.getSerCidNasc())) {
                                        servidorTO.setSerCidNasc(cidNasc);
                                    }
                                    if(!TextHelper.isNull(cidade) && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_EDITAR_CIDADE, responsavel) && TextHelper.isNull(servidorTO.getSerCidade())) {
                                        servidorTO.setSerCidade(cidade);
                                    }
                                    if(!TextHelper.isNull(complemento) && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_EDITAR_COMPL, responsavel) && TextHelper.isNull(servidorTO.getSerCompl())) {
                                        servidorTO.setSerCompl(complemento);
                                    }
                                    if(!TextHelper.isNull(dataidentidade) && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_EDITAR_DATA_IDT, responsavel) && TextHelper.isNull(servidorTO.getSerDataIdt())) {
                                        servidorTO.setSerDataIdt(DateHelper.toSQLDate(DateHelper.parse(dataidentidade, LocaleHelper.getDatePattern())));
                                    }
                                    if(!TextHelper.isNull(emissorIdentidade) && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_EDITAR_EMISSOR_IDT, responsavel) && TextHelper.isNull(servidorTO.getSerEmissorIdt())) {
                                        servidorTO.setSerEmissorIdt(emissorIdentidade);
                                    }
                                    if(!TextHelper.isNull(endereco) && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_EDITAR_END, responsavel) && TextHelper.isNull(servidorTO.getSerEnd())) {
                                        servidorTO.setSerEnd(endereco);
                                    }
                                    if(!TextHelper.isNull(estCivil) && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_EDITAR_EST_CIVIL, responsavel) && TextHelper.isNull(servidorTO.getSerEstCivil())) {
                                        servidorTO.setSerEstCivil(estCivil);
                                    }
                                    if(!TextHelper.isNull(nacionalidade) && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_EDITAR_NACIONALIDADE, responsavel) && TextHelper.isNull(servidorTO.getSerNacionalidade())) {
                                        servidorTO.setSerNacionalidade(nacionalidade);
                                    }
                                    if(!TextHelper.isNull(nomeConjuge) && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_EDITAR_NOME_CONJUGE, responsavel) && TextHelper.isNull(servidorTO.getSerNomeConjuge())) {
                                        servidorTO.setSerNomeConjuge(nomeConjuge);
                                    }
                                    if(!TextHelper.isNull(nomePai) && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_EDITAR_NOME_PAI, responsavel) && TextHelper.isNull(servidorTO.getSerNomePai())) {
                                        servidorTO.setSerNomePai(nomePai);
                                    }
                                    if(!TextHelper.isNull(numero) && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_EDITAR_NRO, responsavel) && TextHelper.isNull(servidorTO.getSerNro())) {
                                        servidorTO.setSerNro(numero);
                                    }
                                    if(!TextHelper.isNull(nroIdentidade) && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_EDITAR_NRO_IDT, responsavel) && TextHelper.isNull(servidorTO.getSerNroIdt())) {
                                        servidorTO.setSerNroIdt(nroIdentidade);
                                    }
                                    if(!TextHelper.isNull(uf) && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_EDITAR_UF, responsavel) && TextHelper.isNull(servidorTO.getSerUf())) {
                                        servidorTO.setSerUf(uf);
                                    }
                                    if(!TextHelper.isNull(ufIdentidade) && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_EDITAR_UF_IDT, responsavel) && TextHelper.isNull(servidorTO.getSerUfIdt())) {
                                        servidorTO.setSerUfIdt(ufIdentidade);
                                    }
                                    if(!TextHelper.isNull(ufNascimento) && ShowFieldHelper.canEdit(FieldKeysConstants.CAD_SERVIDOR_EDITAR_UF_NASC, responsavel) && TextHelper.isNull(servidorTO.getSerUfNasc())) {
                                        servidorTO.setSerUfNasc(ufNascimento);
                                    }
                                }

                                servidorController.updateRegistroServidorSemHistoricoMargem(registroSer, responsavel);

                                // Ao criar usuário com recuperação de senha no auto cadastro já envia email de recuperação, assim não é necessário enviar no fluxo de alteração de email do servidor com auto cadastro
                                if(usaRecuperacaoSenhaNoAutoCadastro && criaUsuarioSer) {
                                    servidorController.updateServidor(servidorTO, false, responsavel);
                                } else {
                                    servidorController.updateServidor(servidorTO, responsavel);
                                }
                            }

                            if (criaUsuarioSer) {
                                final Map<String, EnderecoFuncaoTransferObject> permissoes = new HashMap<>();
                                final List<String> funcoes = usuarioController.getFuncaoPerfil(AcessoSistema.ENTIDADE_SER, null, CodedValues.PER_CODIGO_SERVIDOR, usuAcesso);
                                final Map<String, String> mapFuncoes = usuarioController.getMapFuncoes(null, responsavel);
                                for (final String funCodigo : funcoes) {
                                    permissoes.put(funCodigo, new EnderecoFuncaoTransferObject(funCodigo, mapFuncoes.get(funCodigo)));
                                }
                                // Seta as permissões do papel de servidor no AcessoSistema para que ele possa se
                                // auto-atribuir as permissões, uma vez que o sistema não permite um usuário atribuir
                                // permissão que não tenha
                                usuAcesso.setPermissoes(permissoes);

                                //Quando se está criando um usuário e o usuário recupera senha no auto cadastro não pode validar a força da senha na criação do usuário.
                                if (usaRecuperacaoSenhaNoAutoCadastro) {
                                    usuarioController.createUsuario(usuarioSer, CodedValues.PER_CODIGO_SERVIDOR, codigoEntidade, tipoEntidade, null, true, senhaAberta, false, usuAcesso);
                                } else {
                                    usuarioController.createUsuario(usuarioSer, CodedValues.PER_CODIGO_SERVIDOR, codigoEntidade, tipoEntidade, senhaAberta, usuAcesso);
                                }
                            } else {
                                if (!TextHelper.isNull(senhaAberta)) {
                                    SenhaHelper.validarForcaSenha(senhaAberta, true, responsavel);
                                }

                                final OcorrenciaUsuarioTransferObject ocorrenciaUsu = null;
                                usuarioController.updateUsuario(usuarioSer, ocorrenciaUsu, null, null, tipoEntidade, codigoEntidade, null, usuAcesso);
                            }

                            //DESENV-18083 - INICIO
                            if("false".equals(termoDeUso) || (existeArqPoliticaPrivacidade && "false".equals(politicaPrivacidade))) {
                                final String erro = "false".equals(termoDeUso) ? ApplicationResourcesHelper.getMessage("mensagem.erro.termo.de.consentimento.cadastro.usuario.servidor.nao.aceito", responsavel)
                                                                         : ApplicationResourcesHelper.getMessage("mensagem.erro.politica.privacidade.cadastro.usuario.servidor.nao.aceito", responsavel);

                                session.setAttribute(CodedValues.MSG_ERRO, erro);
                                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                            } else {
                                String tocCodigo = CodedValues.TOC_ACEITACAO_TERMO_DE_USO_CADASTRO_SENHA;

                                CustomTransferObject ocorrencia = new CustomTransferObject();
                                ocorrencia.setAttribute(Columns.OUS_USU_CODIGO, usuCodigo);
                                ocorrencia.setAttribute(Columns.OUS_TOC_CODIGO, tocCodigo);
                                ocorrencia.setAttribute(Columns.OUS_OUS_USU_CODIGO, usuCodigo);
                                ocorrencia.setAttribute(Columns.OUS_OBS, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.aceitacao.termo.de.consentimento", responsavel));
                                ocorrencia.setAttribute(Columns.OUS_IP_ACESSO, responsavel.getIpUsuario());

                                usuarioController.createOcorrenciaUsuario(ocorrencia, responsavel);

                                if(existeArqPoliticaPrivacidade) {
                                    tocCodigo = CodedValues.TOC_ACEITACAO_POLITICA_PRIVACIDADE_CADASTRO_SENHA;

                                    ocorrencia = new CustomTransferObject();
                                    ocorrencia.setAttribute(Columns.OUS_USU_CODIGO, usuCodigo);
                                    ocorrencia.setAttribute(Columns.OUS_TOC_CODIGO, tocCodigo);
                                    ocorrencia.setAttribute(Columns.OUS_OUS_USU_CODIGO, usuCodigo);
                                    ocorrencia.setAttribute(Columns.OUS_OBS, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.aceitacao.politica.privacidade.cad.senha", responsavel));
                                    ocorrencia.setAttribute(Columns.OUS_IP_ACESSO, responsavel.getIpUsuario());

                                    usuarioController.createOcorrenciaUsuario(ocorrencia, responsavel);
                                }
                            }
                            //DESENV-18083 - FIM

                            if (usaRecuperacaoSenhaNoAutoCadastro) {
                                // caso o parâmetro de utilizaçao de recuperação de senha no auto cadastro esteja habilitado, redireciona para o caso de uso de recuperação de senha
                                String urlDestino = "recuperarSenha?acao=concluirServidor&usu=servidor";
                                urlDestino += "&matricula=" + matricula;
                                urlDestino += "&USU_CPF=" + cpf;
                                urlDestino += "&USU_EMAIL=" + email;
                                urlDestino += "&captcha=" + captchaAnswer;
                                if (loginComEstOrg) {
                                    urlDestino += "&codigo_orgao=" + orgCodigo;
                                } else {
                                    urlDestino += "&codigo_orgao=" + estCodigo;
                                }
                                urlDestino = SynchronizerToken.updateTokenInURL(urlDestino, request);
                                return "forward:/v3/" + urlDestino;
                            } else {
                                msgSucess = ApplicationResourcesHelper.getMessage("mensagem.cadastrar.senha.servidor.sucesso", responsavel);
                                ok = true;
                            }
                        }
                    }
                } catch (final Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    if (cadastroAvancadoUsuSer) {
                        model.addAttribute("cpf", JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_CPF));
                        model.addAttribute("matricula", JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_MATRICULA));
                        model.addAttribute("orgCod", JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_ORG_CODIGO));
                        model.addAttribute("tamMax", tamMax);
                        return selecionarServidor(request, response, session, model);
                    } else {
                        return iniciar(request, response, session, model);
                    }
                }
            }

            if (!TextHelper.isNull(email) && ParamSist.paramEquals(CodedValues.TPC_ENVIA_EMAIL_FIM_FLUXO_CAD_SENHA, CodedValues.TPC_SIM, responsavel)) {
                try {
                    final String[] serNome = TextHelper.split((String) servidor.getAttribute(Columns.SER_NOME), " ");
                    final String serPrimeiroNome = serNome[0].trim().toString();
                    final String diretorioManual = ParamSist.getDiretorioRaizArquivos() + File.separatorChar + "manualexplicativo" ;

                    final List<String> filesNames = FileHelper.getFilesInDir(diretorioManual);
                    final List<String> filesNamesFullPath = filesNames.stream().map(f -> (f = diretorioManual + File.separatorChar + f)).collect(Collectors.toList());

                    EnviaEmailHelper.enviarCadastroSenhaServidor(email, serPrimeiroNome, filesNamesFullPath, responsavel);
                } catch (final ViewHelperException e) {
                    throw new UsuarioControllerException("mensagem.erro.enviar.email.servidor", responsavel, e);
                }
            }

            model.addAttribute("usaRecuperacaoSenhaNoAutoCadastro", usaRecuperacaoSenhaNoAutoCadastro);
            model.addAttribute("atualizaDadosServidor", atualizaDadosServidor);
            model.addAttribute("cadastroAvancadoUsuSer", cadastroAvancadoUsuSer);
            model.addAttribute("senhaServidorNumerica", senhaServidorNumerica);
            model.addAttribute("listaOrgaos", null);
            model.addAttribute("msgSucess", msgSucess);
            model.addAttribute("ok", ok);
            model.addAttribute("celularSerEditavel", ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_USU_SERVIDOR_CELULAR, responsavel));
            model.addAttribute("telefoneSerEditavel", ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_USU_SERVIDOR_TELEFONE, responsavel));
            model.addAttribute("emailSerEditavel", ShowFieldHelper.canEdit(FieldKeysConstants.EDITAR_USU_SERVIDOR_EMAIL, responsavel));
            model.addAttribute("tamMax", tamMax);

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());

            if (cadastroAvancadoUsuSer) {
                model.addAttribute("cpf", JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_CPF));
                model.addAttribute("matricula", JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_MATRICULA));
                model.addAttribute("orgCod", JspHelper.verificaVarQryStr(request, uploadHelper, FieldKeysConstants.CAD_SERVIDOR_ORG_CODIGO));
                model.addAttribute("tamMax", tamMax);
                return selecionarServidor(request, response, session, model);
            } else {
                return iniciar(request, response, session, model);
            }
        } finally {
            if ((salvarArquivos != null) && (salvarArquivos.size() > 0)) {
                for (final File anexo : salvarArquivos.values()) {
                    anexo.delete();
                }
            }
        }

        return viewRedirect("jsp/cadastrarSenhaServidor/cadastrarSenhaServidor", request, session, model, responsavel);
    }

    private boolean validaObrigatoriedadeEmailTelCelServidor(UploadHelper uploadHelper, HttpServletRequest request, HttpSession session, AcessoSistema responsavel) throws ZetraException {
        if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_USU_SERVIDOR_CELULAR, responsavel) && TextHelper.isNull(JspHelper.verificaVarQryStr(request, uploadHelper, "celular"))) {
        	session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.ser.celular", responsavel));
        	return false;
        	}

        	if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_USU_SERVIDOR_TELEFONE, responsavel) && TextHelper.isNull(JspHelper.verificaVarQryStr(request, uploadHelper ,"telefone"))) {
        	session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.ser.telefone", responsavel));
        	return false;
        	}

        	if (ShowFieldHelper.isRequired(FieldKeysConstants.EDITAR_USU_SERVIDOR_EMAIL, responsavel) && TextHelper.isNull(JspHelper.verificaVarQryStr(request, uploadHelper ,"email"))) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.ser.email", responsavel));
            return false;
        }
        return true;
    }

    /**
     * Método para retornar o tamanho mínimo permitido para senha de servidor
     * @param responsavel
     * @return
     */
    private int getTamMinSenhaServidor(AcessoSistema responsavel) {
        int tamMinSenhaServidor = 6;

        try {
            tamMinSenhaServidor = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MIN_SENHA_SERVIDOR, responsavel)) ? Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MIN_SENHA_SERVIDOR, responsavel).toString()) : 6;
        } catch (final Exception ex) {
            tamMinSenhaServidor = 6;
        }
        return tamMinSenhaServidor;
    }

    /**
     * Método para retornar o tamanho máximo permitido para senha de servidor
     * @param responsavel
     * @return
     */
    private int getTamMaxSenhaServidor(AcessoSistema responsavel) {
        int tamMaxSenhaServidor = 8;

        try {
            tamMaxSenhaServidor = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_SERVIDOR, responsavel)) ? Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_SERVIDOR, responsavel).toString()) : 8;
        } catch (final Exception ex) {
            tamMaxSenhaServidor = 8;
        }
        return tamMaxSenhaServidor;
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        // Nível de Severidade da nova senha dos usuários servidores
        final String pwdStrength = ParamSist.getInstance().getParam(CodedValues.TPC_SER_PWD_STRENGTH_LEVEL, responsavel) != null ? ParamSist.getInstance().getParam(CodedValues.TPC_SER_PWD_STRENGTH_LEVEL, responsavel).toString() : "3";

        // Transforma o parâmetro em um número inteiro
        int intpwdStrength;
        try {
            intpwdStrength = Integer.parseInt(pwdStrength);
        } catch (final NumberFormatException ex) {
            intpwdStrength = 3;
        }

        int pwdStrengthLevel = 1; // very weak
        String strpwdStrengthLevel = ApplicationResourcesHelper.getMessage("rotulo.nivel.senha.muito.baixo", responsavel);
        String nivel = "muito.baixo";
        if (intpwdStrength == 2) { // weak
            pwdStrengthLevel = 16;
            strpwdStrengthLevel = ApplicationResourcesHelper.getMessage("rotulo.nivel.senha.baixo", responsavel);
            nivel = "baixo";
        } else if (intpwdStrength == 3) { // mediocre
            pwdStrengthLevel = 25;
            strpwdStrengthLevel = ApplicationResourcesHelper.getMessage("rotulo.nivel.senha.medio", responsavel);
            nivel = "medio";
        } else if (intpwdStrength >= 4) { // strong
            pwdStrengthLevel = 35;
            strpwdStrengthLevel = ApplicationResourcesHelper.getMessage("rotulo.nivel.senha.alto", responsavel);
            nivel = "alto";
        }
        String chave = "rotulo.ajuda.alteracaoSenha." + nivel + ".servidor";
        final boolean senhaServidorNumerica = ParamSist.paramEquals(CodedValues.TPC_SENHA_CONS_SERVIDOR_SOMENTE_NUMERICA, CodedValues.TPC_SIM, responsavel);
        if (senhaServidorNumerica) {
            chave += ".numerica";
        }
        final String strMensagemSenha = ApplicationResourcesHelper.getMessage(chave, responsavel);
        final String strMensagemSenha1 = ApplicationResourcesHelper.getMessage(chave + ".1", responsavel);
        final String strMensagemSenha2 = ApplicationResourcesHelper.getMessage(chave + ".2", responsavel);
        final String strMensagemSenha3 = ApplicationResourcesHelper.getMessage(chave + ".3", responsavel);
        final String strMensagemErroSenha = ApplicationResourcesHelper.getMessage("mensagem.erro.requisitos.minimos.seguranca.senha.informada." + nivel, responsavel);

        model.addAttribute("tamMaxSenhaServidor", getTamMaxSenhaServidor(responsavel));
        model.addAttribute("tamMinSenhaServidor", getTamMinSenhaServidor(responsavel));
        model.addAttribute("pwdStrengthLevel", pwdStrengthLevel);
        model.addAttribute("strpwdStrengthLevel", strpwdStrengthLevel);
        model.addAttribute("intpwdStrength", intpwdStrength);
        model.addAttribute("strMensagemSenha", strMensagemSenha);
        model.addAttribute("strMensagemSenha1", strMensagemSenha1);
        model.addAttribute("strMensagemSenha2", strMensagemSenha2);
        model.addAttribute("strMensagemSenha3", strMensagemSenha3);
        model.addAttribute("strMensagemErroSenha", strMensagemErroSenha);
    }

    /**
     * Método para pesquisar um servidor através do dados informados
     * @param cpf
     * @param matricula
     * @param orgCod
     * @param responsavel
     * @return
     * @throws Exception
     */
    private CustomTransferObject pesquisarServidor(String cpf, String matricula, String orgCod, AcessoSistema responsavel) throws Exception {
        CustomTransferObject servidor = null;
        String estIdentificador = null;
        String orgIdentificador = null;

        if (!TextHelper.isNull(orgCod)) {
            final OrgaoTransferObject registroOrgao = consignanteController.findOrgao(orgCod, responsavel);
            final EstabelecimentoTransferObject registroEst = consignanteController.findEstabelecimento(registroOrgao.getEstCodigo(), responsavel);
            estIdentificador = registroEst.getEstIdentificador();
            orgIdentificador = registroOrgao.getOrgIdentificador();
        }

        final List<TransferObject> lstServidor = pesquisarServidorController.pesquisaServidor("CSE", CodedValues.CSE_CODIGO_SISTEMA, estIdentificador, orgIdentificador, matricula, cpf, responsavel, true, CodedValues.SRS_ATIVOS, false);

        if (lstServidor.isEmpty() || (lstServidor.get(0) == null)) {
            throw new ZetraException("mensagem.erro.servidor.nao.encontrado", responsavel);
        } else if (lstServidor.size() > 1) {
            throw new ZetraException("mensagem.multiplosServidoresEncontrados", responsavel);
        } else {
            servidor = (CustomTransferObject) lstServidor.get(0);
        }
        return servidor;
    }

    /**
     * Método para inicializar as perguntas disponíveis para sorteio do questionário
     * @throws ZetraException
     */
    private List<Map<String, String>> criarQuestionario() throws ZetraException {
        final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
        final List<Map<String, String>> perguntas = new ArrayList<>();
        Map<String, String> pergunta = null;

        // Nome da Mãe
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_NOME_MAE, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.nome.mae", responsavel));
            pergunta.put(INPUT_NAME, "SER_NOME_MAE");
            pergunta.put(INPUT_VALUE, Columns.SER_NOME_MAE);
            pergunta.put(INPUT_TYPE, "SELECT");
            pergunta.put(INPUT_OPTION_LABEL, Columns.SER_NOME_MAE);
            pergunta.put(INPUT_OPTION_VALUE, Columns.SER_NOME_MAE);
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.nome.mae", responsavel));
            pergunta.put(TIPO_PERGUNTA, PERGUNTA_PARENTESCO);
            perguntas.add(pergunta);
        }

        // Nome do Pai
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_NOME_PAI, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.nome.pai", responsavel));
            pergunta.put(INPUT_NAME, "SER_NOME_PAI");
            pergunta.put(INPUT_VALUE, Columns.SER_NOME_PAI);
            pergunta.put(INPUT_TYPE, "SELECT");
            pergunta.put(INPUT_OPTION_LABEL, Columns.SER_NOME_PAI);
            pergunta.put(INPUT_OPTION_VALUE, Columns.SER_NOME_PAI);
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.nome.pai", responsavel));
            pergunta.put(TIPO_PERGUNTA, PERGUNTA_PARENTESCO);
            perguntas.add(pergunta);
        }

        // Nome do Conjuge
        pergunta = new HashMap<>();
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_NOME_CONJUGE, responsavel)) {
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.nome.conjuge", responsavel));
            pergunta.put(INPUT_NAME, "SER_NOME_CONJUGE");
            pergunta.put(INPUT_VALUE, Columns.SER_NOME_CONJUGE);
            pergunta.put(INPUT_TYPE, "SELECT");
            pergunta.put(INPUT_OPTION_LABEL, Columns.SER_NOME_CONJUGE);
            pergunta.put(INPUT_OPTION_VALUE, Columns.SER_NOME_CONJUGE);
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.nome.conjuge", responsavel));
            pergunta.put(TIPO_PERGUNTA, PERGUNTA_PARENTESCO);
            perguntas.add(pergunta);
        }

        // Telefone
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_TELEFONE, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.telefone", responsavel));
            pergunta.put(INPUT_NAME, "SER_TEL");
            pergunta.put(INPUT_VALUE, Columns.SER_TEL);
            pergunta.put(INPUT_TYPE, "SELECT");
            pergunta.put(INPUT_OPTION_LABEL, Columns.SER_TEL);
            pergunta.put(INPUT_OPTION_VALUE, Columns.SER_TEL);
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.telefone", responsavel));
            perguntas.add(pergunta);
        }

        // Celular
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_CELULAR, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.celular", responsavel));
            pergunta.put(INPUT_NAME, "SER_CELULAR");
            pergunta.put(INPUT_VALUE, Columns.SER_CELULAR);
            pergunta.put(INPUT_TYPE, "SELECT");
            pergunta.put(INPUT_OPTION_LABEL, Columns.SER_CELULAR);
            pergunta.put(INPUT_OPTION_VALUE, Columns.SER_CELULAR);
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.celular", responsavel));
            perguntas.add(pergunta);
        }

        // Endereço
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_ENDERECO, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.endereco", responsavel));
            pergunta.put(INPUT_NAME, "SER_END");
            pergunta.put(INPUT_VALUE, Columns.SER_END);
            pergunta.put(INPUT_TYPE, "SELECT");
            pergunta.put(INPUT_OPTION_LABEL, Columns.SER_END);
            pergunta.put(INPUT_OPTION_VALUE, Columns.SER_END);
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.endereco", responsavel));
            perguntas.add(pergunta);
        }

        // Número do Endereço
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_NUM_ENDERECO, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.num.endereco", responsavel));
            pergunta.put(INPUT_NAME, "SER_NRO");
            pergunta.put(INPUT_VALUE, Columns.SER_NRO);
            pergunta.put(INPUT_TYPE, "INPUT");
            pergunta.put(INPUT_MASK, "#*15");
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.num.endereco", responsavel));
            perguntas.add(pergunta);
        }

        // Bairro
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_BAIRRO, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.bairro", responsavel));
            pergunta.put(INPUT_NAME, "SER_BAIRRO");
            pergunta.put(INPUT_VALUE, Columns.SER_BAIRRO);
            pergunta.put(INPUT_TYPE, "SELECT");
            pergunta.put(INPUT_OPTION_LABEL, Columns.SER_BAIRRO);
            pergunta.put(INPUT_OPTION_VALUE, Columns.SER_BAIRRO);
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.bairro", responsavel));
            perguntas.add(pergunta);
        }

        // CEP
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_CEP, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.cep", responsavel));
            pergunta.put(INPUT_NAME, "SER_CEP");
            pergunta.put(INPUT_VALUE, Columns.SER_CEP);
            pergunta.put(INPUT_TYPE, "SELECT");
            pergunta.put(INPUT_OPTION_LABEL, Columns.SER_CEP);
            pergunta.put(INPUT_OPTION_VALUE, Columns.SER_CEP);
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.cep", responsavel));
            perguntas.add(pergunta);
        }

        // Cidade
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_CIDADE, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.cidade", responsavel));
            pergunta.put(INPUT_NAME, "SER_CIDADE");
            pergunta.put(INPUT_VALUE, Columns.SER_CIDADE);
            pergunta.put(INPUT_TYPE, "SELECT");
            pergunta.put(INPUT_OPTION_LABEL, Columns.SER_CIDADE);
            pergunta.put(INPUT_OPTION_VALUE, Columns.SER_CIDADE);
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.cidade", responsavel));
            perguntas.add(pergunta);
        }

        // Município Lotação
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_MUNICIPIO_LOTACAO, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.municipio.lotacao", responsavel));
            pergunta.put(INPUT_NAME, "RSE_MUNICIPIO_LOTACAO");
            pergunta.put(INPUT_VALUE, Columns.RSE_MUNICIPIO_LOTACAO);
            pergunta.put(INPUT_TYPE, "SELECT");
            pergunta.put(INPUT_OPTION_LABEL, Columns.RSE_MUNICIPIO_LOTACAO);
            pergunta.put(INPUT_OPTION_VALUE, Columns.RSE_MUNICIPIO_LOTACAO);
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.municipio.lotacao", responsavel));
            perguntas.add(pergunta);
        }

        // Cidade de nascimento
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_CIDADE_NASC, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.cidade.nasc", responsavel));
            pergunta.put(INPUT_NAME, "SER_CID_NASC");
            pergunta.put(INPUT_VALUE, Columns.SER_CID_NASC);
            pergunta.put(INPUT_TYPE, "SELECT");
            pergunta.put(INPUT_OPTION_LABEL, Columns.SER_CID_NASC);
            pergunta.put(INPUT_OPTION_VALUE, Columns.SER_CID_NASC);
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.cidade.nasc", responsavel));
            perguntas.add(pergunta);
        }

        // Dia de Nascimento
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_DIA_NASC, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.dia.nasc", responsavel));
            pergunta.put(INPUT_NAME, "SER_DIA_NASC");
            pergunta.put(INPUT_VALUE, Columns.SER_DATA_NASC);
            pergunta.put(INPUT_TYPE, "INPUT");
            pergunta.put(INPUT_MASK, "#D2");
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.dia.nasc", responsavel));
            perguntas.add(pergunta);
        }

        // Mês de Nascimento
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_MES_NASC, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.mes.nasc", responsavel));
            pergunta.put(INPUT_NAME, "SER_MES_NASC");
            pergunta.put(INPUT_VALUE, Columns.SER_DATA_NASC);
            pergunta.put(INPUT_TYPE, "INPUT");
            pergunta.put(INPUT_MASK, "#D2");
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.mes.nasc", responsavel));
            perguntas.add(pergunta);
        }

        // Ano de Nascimento
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_ANO_NASC, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.ano.nasc", responsavel));
            pergunta.put(INPUT_NAME, "SER_ANO_NASC");
            pergunta.put(INPUT_VALUE, Columns.SER_DATA_NASC);
            pergunta.put(INPUT_TYPE, "INPUT");
            pergunta.put(INPUT_MASK, "DDDD");
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.ano.nasc", responsavel));
            perguntas.add(pergunta);
        }

        // Dia de Admissão
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_DIA_ADMISSAO, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.dia.admissao", responsavel));
            pergunta.put(INPUT_NAME, "RSE_DIA_ADMISSAO");
            pergunta.put(INPUT_VALUE, Columns.RSE_DATA_ADMISSAO);
            pergunta.put(INPUT_TYPE, "INPUT");
            pergunta.put(INPUT_MASK, "#D2");
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.dia.admissao", responsavel));
            perguntas.add(pergunta);
        }

        // Mês de Admissão
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_MES_ADMISSAO, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.mes.admissao", responsavel));
            pergunta.put(INPUT_NAME, "RSE_MES_ADMISSAO");
            pergunta.put(INPUT_VALUE, Columns.RSE_DATA_ADMISSAO);
            pergunta.put(INPUT_TYPE, "INPUT");
            pergunta.put(INPUT_MASK, "#D2");
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.mes.admissao", responsavel));
            perguntas.add(pergunta);
        }

        // Ano de Admissão
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_ANO_ADMISSAO, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.ano.admissao", responsavel));
            pergunta.put(INPUT_NAME, "RSE_ANO_ADMISSAO");
            pergunta.put(INPUT_VALUE, Columns.RSE_DATA_ADMISSAO);
            pergunta.put(INPUT_TYPE, "INPUT");
            pergunta.put(INPUT_MASK, "DDDD");
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.ano.admissao", responsavel));
            perguntas.add(pergunta);
        }

        // Número da Carteira de Identidade (RG)
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_NUM_IDENTIDADE, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.num.identidade", responsavel));
            pergunta.put(INPUT_NAME, "SER_NRO_IDT");
            pergunta.put(INPUT_VALUE, Columns.SER_NRO_IDT);
            pergunta.put(INPUT_TYPE, "SELECT");
            pergunta.put(INPUT_OPTION_LABEL, Columns.SER_NRO_IDT);
            pergunta.put(INPUT_OPTION_VALUE, Columns.SER_NRO_IDT);
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.num.identidade", responsavel));
            perguntas.add(pergunta);
        }

        // Número da Carteira de Trabalho
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_NUM_CART_TRABALHO, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.num.cart.trabalho", responsavel));
            pergunta.put(INPUT_NAME, "SER_CART_PROF");
            pergunta.put(INPUT_VALUE, Columns.SER_CART_PROF);
            pergunta.put(INPUT_TYPE, "SELECT");
            pergunta.put(INPUT_OPTION_LABEL, Columns.SER_CART_PROF);
            pergunta.put(INPUT_OPTION_VALUE, Columns.SER_CART_PROF);
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.num.cart.trabalho", responsavel));
            perguntas.add(pergunta);
        }

        // Número do PIS
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_NUM_PIS, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.num.pis", responsavel));
            pergunta.put(INPUT_NAME, "SER_PIS");
            pergunta.put(INPUT_VALUE, Columns.SER_PIS);
            pergunta.put(INPUT_TYPE, "SELECT");
            pergunta.put(INPUT_OPTION_LABEL, Columns.SER_PIS);
            pergunta.put(INPUT_OPTION_VALUE, Columns.SER_PIS);
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.num.pis", responsavel));
            perguntas.add(pergunta);
        }

        // Dia de Emissão da Carteira de Identidade (RG)
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_DIA_IDT, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.dia.idt", responsavel));
            pergunta.put(INPUT_NAME, "SER_DIA_IDT");
            pergunta.put(INPUT_VALUE, Columns.SER_DATA_IDT);
            pergunta.put(INPUT_TYPE, "INPUT");
            pergunta.put(INPUT_MASK, "#D2");
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.dia.idt", responsavel));
            perguntas.add(pergunta);
        }

        // Mês de Emissão da Carteira de Identidade (RG)
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_MES_IDT, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.mes.idt", responsavel));
            pergunta.put(INPUT_NAME, "SER_MES_IDT");
            pergunta.put(INPUT_VALUE, Columns.SER_DATA_IDT);
            pergunta.put(INPUT_TYPE, "INPUT");
            pergunta.put(INPUT_MASK, "#D2");
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.mes.idt", responsavel));
            perguntas.add(pergunta);
        }

        // Ano de Emissão da Carteira de Identidade (RG)
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_ANO_IDT, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.ano.idt", responsavel));
            pergunta.put(INPUT_NAME, "SER_ANO_IDT");
            pergunta.put(INPUT_VALUE, Columns.SER_DATA_IDT);
            pergunta.put(INPUT_TYPE, "INPUT");
            pergunta.put(INPUT_MASK, "DDDD");
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.ano.idt", responsavel));
            perguntas.add(pergunta);
        }

        // Categoria Profissional
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_CATEGORIA, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.categoria", responsavel));
            pergunta.put(INPUT_NAME, "RSE_TIPO");
            pergunta.put(INPUT_VALUE, Columns.RSE_TIPO);
            pergunta.put(INPUT_TYPE, "SELECT");
            pergunta.put(INPUT_OPTION_LABEL, Columns.RSE_TIPO);
            pergunta.put(INPUT_OPTION_VALUE, Columns.RSE_TIPO);
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.categoria", responsavel));
            perguntas.add(pergunta);
        }

        // Agência Depósito Salarial
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_AGENCIA_SALARIO, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.agencia.salario", responsavel));
            pergunta.put(INPUT_NAME, "RSE_AGENCIA_SAL");
            pergunta.put(INPUT_VALUE, Columns.RSE_AGENCIA_SAL);
            pergunta.put(INPUT_TYPE, "INPUT");
            pergunta.put(INPUT_MASK, "#D5");
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.agencia.salario", responsavel));
            perguntas.add(pergunta);
        }

        // Conta Depósito Salarial
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_CONTA_SALARIO, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.conta.salario", responsavel));
            pergunta.put(INPUT_NAME, "RSE_CONTA_SAL");
            pergunta.put(INPUT_VALUE, Columns.RSE_CONTA_SAL);
            pergunta.put(INPUT_TYPE, "INPUT");
            pergunta.put(INPUT_MASK, "#*40");
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.conta.salario", responsavel));
            perguntas.add(pergunta);
        }



        // Endereço Complemento
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_COMPL, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.compl", responsavel));
            pergunta.put(INPUT_NAME, "SER_COMPL");
            pergunta.put(INPUT_VALUE, Columns.SER_COMPL);
            pergunta.put(INPUT_TYPE, "SELECT");
            pergunta.put(INPUT_OPTION_LABEL, Columns.SER_COMPL);
            pergunta.put(INPUT_OPTION_VALUE, Columns.SER_COMPL);
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.compl", responsavel));
            perguntas.add(pergunta);
        }

        // Servidor Nacionalidade
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_NACIONALIDADE, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.nacionalidade", responsavel));
            pergunta.put(INPUT_NAME, "SER_NACIONALIDADE");
            pergunta.put(INPUT_VALUE, Columns.SER_NACIONALIDADE);
            pergunta.put(INPUT_TYPE, "INPUT");
            pergunta.put(INPUT_MASK, "#*40");
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.nacionalidade", responsavel));
            perguntas.add(pergunta);
        }

        // Servidor UF
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_UF, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.uf", responsavel));
            pergunta.put(INPUT_NAME, "SER_UF");
            pergunta.put(INPUT_VALUE, Columns.SER_UF);
            pergunta.put(INPUT_TYPE, "INPUT");
            pergunta.put(INPUT_OPTION_LABEL, Columns.SER_UF);
            pergunta.put(INPUT_OPTION_VALUE, Columns.SER_UF);
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.uf", responsavel));
            perguntas.add(pergunta);
        }

        // Servidor UF Identidade
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_UF_IDT, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.uf.idt", responsavel));
            pergunta.put(INPUT_NAME, "SER_UF_IDT");
            pergunta.put(INPUT_VALUE, Columns.SER_UF_IDT);
            pergunta.put(INPUT_TYPE, "INPUT");
            pergunta.put(INPUT_TYPE, "INPUT");
            pergunta.put(INPUT_MASK, "#*40");
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.uf.idt", responsavel));
            perguntas.add(pergunta);
        }

        // Servidor UF de Nascimento
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_UF_NASC, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.uf.nasc", responsavel));
            pergunta.put(INPUT_NAME, "SER_UF_NASC");
            pergunta.put(INPUT_VALUE, Columns.SER_UF_NASC);
            pergunta.put(INPUT_TYPE, "INPUT");
            pergunta.put(INPUT_TYPE, "INPUT");
            pergunta.put(INPUT_MASK, "#*40");
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.uf.nasc", responsavel));
            perguntas.add(pergunta);
        }

        // Emissor Identidade
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_EMISSOR_IDT, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.emissor.idt", responsavel));
            pergunta.put(INPUT_NAME, "SER_EMISSOR_IDT");
            pergunta.put(INPUT_VALUE, Columns.SER_EMISSOR_IDT);
            pergunta.put(INPUT_TYPE, "INPUT");
            pergunta.put(INPUT_MASK, "#*40");
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.emissor.idt", responsavel));
            perguntas.add(pergunta);
        }

        // Estado civil
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_EST_CIVIL, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.estado.civil", responsavel));
            pergunta.put(INPUT_NAME, "SER_EST_CIVIL");
            pergunta.put(INPUT_VALUE, Columns.SER_EST_CIVIL);
            pergunta.put(INPUT_TYPE, "SELECT");
            pergunta.put(INPUT_OPTION_LABEL, Columns.EST_CIVIL_DESCRICAO);
            pergunta.put(INPUT_OPTION_VALUE, Columns.SER_EST_CIVIL);
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.estado.civil", responsavel));
            perguntas.add(pergunta);
        }

        // banco
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_BANCO_SAL, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.banco", responsavel));
            pergunta.put(INPUT_NAME, "RSE_BANCO_SAL");
            pergunta.put(INPUT_VALUE, Columns.RSE_BANCO_SAL);
            pergunta.put(INPUT_TYPE, "SELECT");
            pergunta.put(INPUT_OPTION_LABEL, Columns.RSE_BANCO_SAL);
            pergunta.put(INPUT_OPTION_VALUE, Columns.RSE_BANCO_SAL);
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.banco", responsavel));
            perguntas.add(pergunta);
        }

        // banco alternativo
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_BANCO_SAL_2, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.banco.alternativo", responsavel));
            pergunta.put(INPUT_NAME, "RSE_BANCO_SAL_2");
            pergunta.put(INPUT_VALUE, Columns.RSE_BANCO_SAL_2);
            pergunta.put(INPUT_TYPE, "SELECT");
            pergunta.put(INPUT_OPTION_LABEL, Columns.RSE_BANCO_SAL_2);
            pergunta.put(INPUT_OPTION_VALUE, Columns.RSE_BANCO_SAL_2);
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.banco.alternativo", responsavel));
            perguntas.add(pergunta);
        }

        // agencia alternativa
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_AGENCIA_SALARIO2, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.agencia.alternativo", responsavel));
            pergunta.put(INPUT_NAME, "RSE_AGENCIA_SAL_2");
            pergunta.put(INPUT_VALUE, Columns.RSE_AGENCIA_SAL_2);
            pergunta.put(INPUT_TYPE, "INPUT");
            pergunta.put(INPUT_MASK, "#D5");
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.agencia.alternativo", responsavel));
            perguntas.add(pergunta);
        }

        // conta salario alternativa
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_CONTA_SALARIO2, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.conta.alternativa", responsavel));
            pergunta.put(INPUT_NAME, "RSE_CONTA_SAL_2");
            pergunta.put(INPUT_VALUE, Columns.RSE_CONTA_SAL_2);
            pergunta.put(INPUT_TYPE, "INPUT");
            pergunta.put(INPUT_MASK, "#*40");
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.conta.alternativa", responsavel));
            perguntas.add(pergunta);
        }

        // datas fim de engajamento
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_DATA_FIM_ENGAJAMENTO, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.data.fim.engajamento", responsavel));
            pergunta.put(INPUT_NAME, "RSE_DATA_FIM_ENGAJAMENTO");
            pergunta.put(INPUT_VALUE, Columns.RSE_DATA_FIM_ENGAJAMENTO);
            pergunta.put(INPUT_TYPE, "SELECT");
            pergunta.put(INPUT_OPTION_LABEL, "FORMATED_DATA_FIM_ENGAJAMENTO");
            pergunta.put(INPUT_OPTION_VALUE, "FORMATED_DATA_FIM_ENGAJAMENTO");
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.data.fim.engajamento", responsavel));
            perguntas.add(pergunta);
        }

        // data limite permanencia
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_DATA_LIMITE_PERMANENCIA, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.data.limite.permanencia", responsavel));
            pergunta.put(INPUT_NAME, "RSE_DATA_LIMITE_PERMANENCIA");
            pergunta.put(INPUT_VALUE, Columns.RSE_DATA_LIMITE_PERMANENCIA);
            pergunta.put(INPUT_TYPE, "SELECT");
            pergunta.put(INPUT_OPTION_LABEL, "FORMATED_DATA_LIMITE_PERMANENCIA");
            pergunta.put(INPUT_OPTION_VALUE, "FORMATED_DATA_LIMITE_PERMANENCIA");
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.data.limite.permanencia", responsavel));
            perguntas.add(pergunta);
        }

        // rse praca
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_PRACA, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.praca", responsavel));
            pergunta.put(INPUT_NAME, "RSE_PRACA");
            pergunta.put(INPUT_VALUE, Columns.RSE_PRACA);
            pergunta.put(INPUT_TYPE, "INPUT");
            pergunta.put(INPUT_MASK, "#*400");
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.praca", responsavel));
            perguntas.add(pergunta);
        }

        // rse prazo
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_PRAZO, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.prazo", responsavel));
            pergunta.put(INPUT_NAME, "RSE_PRAZO");
            pergunta.put(INPUT_VALUE, Columns.RSE_PRAZO);
            pergunta.put(INPUT_TYPE, "INPUT");
            pergunta.put(INPUT_MASK, "#D11");
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.prazo", responsavel));
            perguntas.add(pergunta);
        }

        // rse_associado
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_ASSOCIADO, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.associado", responsavel));
            pergunta.put(INPUT_NAME, "RSE_ASSOCIADO");
            pergunta.put(INPUT_VALUE, Columns.RSE_ASSOCIADO);
            pergunta.put(INPUT_TYPE, "RADIO");
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.associado", responsavel));
            perguntas.add(pergunta);
        }

        // rse_clt
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_CLT, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.clt", responsavel));
            pergunta.put(INPUT_NAME, "RSE_CLT");
            pergunta.put(INPUT_VALUE, Columns.RSE_CLT);
            pergunta.put(INPUT_TYPE, "RADIO");
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.clt", responsavel));
            perguntas.add(pergunta);
        }

        // rse_estabilizado
        if (ShowFieldHelper.showField(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_ESTABILIZADO, responsavel)) {
            pergunta = new HashMap<>();
            pergunta.put(INPUT_LABEL, ApplicationResourcesHelper.getMessage("rotulo.questionario.servidor.estabilizado", responsavel));
            pergunta.put(INPUT_NAME, "RSE_ESTABILIZADO");
            pergunta.put(INPUT_VALUE, Columns.RSE_ESTABILIZADO);
            pergunta.put(INPUT_TYPE, "RADIO");
            pergunta.put(INPUT_JAVASCRIPT, ApplicationResourcesHelper.getMessage("mensagem.informe.questionario.servidor.estabilizado", responsavel));
            perguntas.add(pergunta);
        }

        return perguntas;
    }

    /**
     * Método para sortear as perguntas que serão exibidas na tela de cadastro de senha de servidor
     * @param servidor
     * @param perguntas
     * @param responsavel
     * @return
     * @throws JspException
     */
    private List<Integer> sorteiaPerguntas(TransferObject servidor, List<Map<String, String>> perguntas, AcessoSistema responsavel) throws ZetraException {
        int limite = 0;
        final List<Integer> perguntasSorteadas = new ArrayList<>();
        final List<String> tiposPerguntas = new ArrayList<>();

        do {
            int randomNumber = 0;
            try {
                randomNumber = NumberHelper.getRandomNumber(perguntas.size() - 1, 0);
            } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
                throw new ZetraException("mensagem.erro.interno.questionario.servidor", responsavel);
            }

            final Map<String, String> pergunta = perguntas.get(randomNumber);
            final String tipoPergunta = pergunta.get(TIPO_PERGUNTA);
            if (!perguntasSorteadas.contains(randomNumber) && !TextHelper.isNull(servidor.getAttribute(pergunta.get(INPUT_VALUE))) && (TextHelper.isNull(tipoPergunta) || !tiposPerguntas.contains(tipoPergunta))) {

                // Inclui tipo de pergunta selecionado para evitar duplicidade de perguntas do mesmo tipo
                if (!TextHelper.isNull(tipoPergunta)) {
                    tiposPerguntas.add(tipoPergunta);
                }

                // Inclui pergunta as perguntas sorteadas
                perguntasSorteadas.add(randomNumber);
            }

            if (LIMITE_TENTATIVAS < limite++) {
                throw new ZetraException("mensagem.erro.interno.questionario.servidor", responsavel);
            }
        } while (perguntasSorteadas.size() < QUANTIDADE_PERGUNTAS);

        return perguntasSorteadas;
    }

    /**
     * Método para sortear as opções de respostas das perguntas selecionadas para serem exibidas na tela de cadastro de senha
     * @param servidor
     * @param responsavel
     * @return
     * @throws JspException
     */
    private List<TransferObject> sorteiaRespostas(TransferObject servidor, AcessoSistema responsavel) throws ZetraException {
        final List<TransferObject> sorteados = new ArrayList<>();
        sorteados.add(servidor);

        // Recupera somente os números da matrícula
        final String rseMatricula = servidor.getAttribute(Columns.RSE_MATRICULA).toString();
        final List<String> selecionados = new ArrayList<>();

        int limite = 0;
        do {
            // Sorteia um servidor passando como seed a matrícula do servidor, assim serão retornados os 4 mesmos servidores iniciais
            final TransferObject sorteado = pesquisarServidorController.sorteiaServidor(sorteados, rseMatricula, responsavel);
            if (sorteado != null) {
                final String serCodigo = sorteado.getAttribute(Columns.SER_CODIGO).toString();
                if (!selecionados.contains(serCodigo)) {
                    // Adiciona servidor selecionado para evitar duplicidade
                    selecionados.add(serCodigo);
                    // Adiciona na lista de servidores que será retornada pelo método
                    sorteados.add(sorteado);
                }
            }

            if (LIMITE_TENTATIVAS < limite++) {
                throw new ZetraException("mensagem.erro.interno.questionario.servidor", responsavel);
            }
        } while (sorteados.size() < (QUANTIDADE_RESPOSTAS / 2));

        limite = 0;
        do {
            // Sorteia um servidor sem passar um seed, para que sejam retornados servidores distintos
            final TransferObject sorteado = pesquisarServidorController.sorteiaServidor(sorteados, null, responsavel);
            if (sorteado != null) {
                final String serCodigo = sorteado.getAttribute(Columns.SER_CODIGO).toString();
                if (!selecionados.contains(serCodigo)) {
                    // Adiciona servidor selecionado para evitar duplicidade
                    selecionados.add(serCodigo);
                    // Adiciona na lista de servidores que será retornada pelo método
                    sorteados.add(sorteado);
                }
            }

            if (LIMITE_TENTATIVAS < limite++) {
                throw new ZetraException("mensagem.erro.interno.questionario.servidor", responsavel);
            }
        } while (sorteados.size() < QUANTIDADE_RESPOSTAS);

        // Embaralha as respostas selecionadas
        Collections.shuffle(sorteados);

        return sorteados;
    }

    /**
     * Método para montar o questionário que será exibido para o servidor no cadastro de senha
     * @param servidor
     * @param responsavel
     * @return
     * @throws JspException
     */
    private List<CampoQuestionarioDadosServidor> montarQuestionarioServidor(TransferObject servidor, AcessoSistema responsavel) throws ZetraException {
        // Inicializa perguntas do questionário
        final List<Map<String, String>> perguntas = criarQuestionario();

        // Sorteia as perguntas antes das respostas para ter certeza que o servidor terá a quantidade mínina de perguntas a serem respondidas
        final List<Integer> perguntasSorteadas = sorteiaPerguntas(servidor, perguntas, responsavel);

        // Sorteia servidores para montar as respostas possíveis
        final List<TransferObject> servidores = sorteiaRespostas(servidor, responsavel);

        servidores.forEach(ser -> {
            if(ser.getAttribute(Columns.SER_EST_CIVIL) != null) {
                final String estCivilCodigo = ser.getAttribute(Columns.SER_EST_CIVIL).toString();
                final String estCivilDesc = servidorController.getEstCivil(estCivilCodigo, responsavel);
                ser.setAttribute(Columns.EST_CIVIL_DESCRICAO, estCivilDesc);
            }
        });

        // Monta o questionário com as perguntas e repostas sorteadas
        final List<CampoQuestionarioDadosServidor> camposQuestionarioDadosServidor = new ArrayList<>();
        for (final Integer index : perguntasSorteadas) {
            final Map<String, String> pergunta = perguntas.get(index);
            final CampoQuestionarioDadosServidor campo = new CampoQuestionarioDadosServidor(pergunta.get(INPUT_NAME), pergunta.get(INPUT_TYPE), pergunta.get(INPUT_MASK), pergunta.get(INPUT_LABEL), pergunta.get(INPUT_OPTION_VALUE), pergunta.get(INPUT_OPTION_LABEL), servidores, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), pergunta.get(INPUT_JAVASCRIPT), pergunta.get(INPUT_PLACEHOLDER));
            camposQuestionarioDadosServidor.add(campo);
        }

        return camposQuestionarioDadosServidor;
    }

    private int validarCampoCadastroAvancado(String chaveCampo, String coluna, String campoResposta, TransferObject servidor, UploadHelper uploadHelper, HttpServletRequest request, AcessoSistema responsavel) throws ZetraException, ParseException {
        if (ShowFieldHelper.showField(chaveCampo, responsavel)) {
            if (FieldKeysConstants.CAD_SERVIDOR_AVANCADO_DIA_NASC.equals(chaveCampo) || FieldKeysConstants.CAD_SERVIDOR_AVANCADO_DIA_IDT.equals(chaveCampo) || FieldKeysConstants.CAD_SERVIDOR_AVANCADO_DIA_ADMISSAO.equals(chaveCampo) ||
                    FieldKeysConstants.CAD_SERVIDOR_AVANCADO_MES_NASC.equals(chaveCampo) || FieldKeysConstants.CAD_SERVIDOR_AVANCADO_MES_IDT.equals(chaveCampo) || FieldKeysConstants.CAD_SERVIDOR_AVANCADO_MES_ADMISSAO.equals(chaveCampo) ||
                    FieldKeysConstants.CAD_SERVIDOR_AVANCADO_ANO_NASC.equals(chaveCampo) || FieldKeysConstants.CAD_SERVIDOR_AVANCADO_ANO_IDT.equals(chaveCampo) || FieldKeysConstants.CAD_SERVIDOR_AVANCADO_ANO_ADMISSAO.equals(chaveCampo)) {
                final java.util.Date dataBase = (java.util.Date) servidor.getAttribute(coluna);
                if (dataBase != null) {
                    final int valorResposta = TextHelper.isNum(JspHelper.verificaVarQryStr(request, uploadHelper, campoResposta)) ? Integer.parseInt(JspHelper.verificaVarQryStr(request, uploadHelper, campoResposta)) : -1;
                    int valorBase = -1;

                    if (FieldKeysConstants.CAD_SERVIDOR_AVANCADO_DIA_NASC.equals(chaveCampo) || FieldKeysConstants.CAD_SERVIDOR_AVANCADO_DIA_IDT.equals(chaveCampo) || FieldKeysConstants.CAD_SERVIDOR_AVANCADO_DIA_ADMISSAO.equals(chaveCampo)) {
                        valorBase = DateHelper.getDay(dataBase);
                    } else if (FieldKeysConstants.CAD_SERVIDOR_AVANCADO_MES_NASC.equals(chaveCampo) || FieldKeysConstants.CAD_SERVIDOR_AVANCADO_MES_IDT.equals(chaveCampo) || FieldKeysConstants.CAD_SERVIDOR_AVANCADO_MES_ADMISSAO.equals(chaveCampo)) {
                        valorBase = DateHelper.getMonth(dataBase);
                    } else if (FieldKeysConstants.CAD_SERVIDOR_AVANCADO_ANO_NASC.equals(chaveCampo) || FieldKeysConstants.CAD_SERVIDOR_AVANCADO_ANO_IDT.equals(chaveCampo) || FieldKeysConstants.CAD_SERVIDOR_AVANCADO_ANO_ADMISSAO.equals(chaveCampo)) {
                        valorBase = DateHelper.getYear(dataBase);
                    }

                    if ((valorBase != -1) && (valorResposta != -1)) {
                        if (valorBase != valorResposta) {
                            LOG.error("Valor esperado '" + valorBase + "' / Valor informado '" + valorResposta + "'");
                            throw new ZetraException("mensagem.erro.cadastrar.senha.servidor.dados.incorretos", responsavel);
                        }
                        return 1;
                    }
                }
            } if(FieldKeysConstants.CAD_SERVIDOR_AVANCADO_DIA_NASC.equals(chaveCampo) || FieldKeysConstants.CAD_SERVIDOR_AVANCADO_DIA_IDT.equals(chaveCampo)) {
            	final String valorBase = DateHelper.reformat(servidor.getAttribute(coluna).toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern());
                final String valorResposta = JspHelper.verificaVarQryStr(request, uploadHelper, campoResposta);
                if (!TextHelper.isNull(valorBase) && !TextHelper.isNull(valorResposta)) {
                    if (!valorBase.equalsIgnoreCase(valorResposta)) {
                        LOG.error("Valor esperado '" + valorBase + "' / Valor informado '" + valorResposta + "'");
                        throw new ZetraException("mensagem.erro.cadastrar.senha.servidor.dados.incorretos", responsavel);
                    }
                    return 1;
                }
            } else {
                final String valorBase = servidor.getAttribute(coluna).toString();
                final String valorResposta = JspHelper.verificaVarQryStr(request, uploadHelper, campoResposta);
                if (!TextHelper.isNull(valorBase) && !TextHelper.isNull(valorResposta)) {
                    if (!valorBase.equalsIgnoreCase(valorResposta)) {
                        LOG.error("Valor esperado '" + valorBase + "' / Valor informado '" + valorResposta + "'");
                        throw new ZetraException("mensagem.erro.cadastrar.senha.servidor.dados.incorretos", responsavel);
                    }
                    return 1;
                }
            }
        }
        return 0;
    }
}