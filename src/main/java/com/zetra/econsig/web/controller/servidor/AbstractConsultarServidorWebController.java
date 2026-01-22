package com.zetra.econsig.web.controller.servidor;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.CompraContratoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.margem.ControleConsulta;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.senha.SenhaHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.ConsultaMargemSemSenha;
import com.zetra.econsig.service.compra.CompraContratoController;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.CancelarConsignacaoController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.InformacaoSerCompraEnum;
import com.zetra.econsig.web.servlet.AudioCaptchaServlet;
import com.zetra.econsig.web.servlet.ImageCaptchaServlet;

import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ConsultarServidorWebController</p>
 * <p>Description: Controlador Web Base para os casos de uso que pesquisam pelo servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class AbstractConsultarServidorWebController extends AbstractServidorWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AbstractConsultarServidorWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private CancelarConsignacaoController cancelarConsignacaoController;

    @Autowired
    private CompraContratoController compraContratoController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private ServicoController servicoController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        SynchronizerToken.saveToken(request);
        model.addAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, session.getAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY));
        definirAcaoRetorno(request, response, session, model);

        // Remove captcha consulta de consignação
        session.removeAttribute("isValidCaptcha");

        try {
            // Parâmetro de obrigatoriedade de CPF e Matrícula
            boolean requerMatriculaCpf = false;
            if (ParamSist.paramEquals(CodedValues.TPC_INF_MAT_CPF_EDT_CONSIGNACAO, CodedValues.TPC_SIM, responsavel)) {
                requerMatriculaCpf = false;
            } else {
                requerMatriculaCpf = parametroController.requerMatriculaCpf(responsavel);
            }
            if (requerMatriculaCpf) {
                model.addAttribute("requerMatriculaCpf", Boolean.TRUE);
            }

            // Parâmetro de obrigatoriedade de data de nascimento
	        if (parametroController.requerDataNascimento(responsavel)) {
	            model.addAttribute("requerDataNascimento", Boolean.TRUE);
	        }
        } catch (final ParametroControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/consultarServidor/pesquisarServidor", request, session, model, responsavel);
    }

    protected void definirAcaoRetorno(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        model.addAttribute("acaoRetorno", "../v3/carregarPrincipal");
    }

    @RequestMapping(params = { "acao=pesquisarServidor" })
    public String pesquisarServidor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, InstantiationException, IllegalAccessException, ParseException, ZetraException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Existem dois momentos de validação, primeiro para validar a necessidade de solicitar senha na pesquisa e outro para continuar. Porém na segunda validação que é no selecionar o servidor
        // Não fazemos validação para retorno, somente para continuar o fluxo, por isso duas variáveis uma validar o momento de solicitar senha e outro para retornar senha obrigatória.
        final boolean validaAutorizacaoSemSenha = !TextHelper.isNull(session.getAttribute("valida_autorizacao"));
        final boolean validaAutorizacaoSemSenhaNovamente = !TextHelper.isNull(session.getAttribute("valida_autorizacao_novamente"));

        if (!SynchronizerToken.isTokenValid(request)) {
            if (validaAutorizacaoSemSenha) {
                session.removeAttribute("valida_autorizacao");
                return CodedValues.VALIDA_AUTORIZACAO_SOLICITA_SENHA_CONTINUA;
            }
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Como existe a validação via post por javaScript para definir se redirecionará ou solicitará senha do servidor ainda não podemos
        // atualizar o token, pois ele vai ser reutiliza-do no final do processo.
        if (!validaAutorizacaoSemSenha) {
            session.removeAttribute("valida_autorizacao");
            SynchronizerToken.saveToken(request);
        }

        final String[] adeNumeros = request.getParameterValues("ADE_NUMERO_LIST");
        final String adeNumero    = JspHelper.verificaVarQryStr(request, "ADE_NUMERO");
        final String rseMatricula = JspHelper.verificaVarQryStr(request, "RSE_MATRICULA");
        final String serCpf       = JspHelper.verificaVarQryStr(request, "SER_CPF");
        final String serDataNasc  = JspHelper.verificaVarQryStr(request, "SER_DATA_NASC");
        final String vrsCodigo    = JspHelper.verificaVarQryStr(request, "cadastrarServidor_vinculo");
        final String tipoDecisaoJudicial = JspHelper.verificaVarQryStr(request, "tipoDecisaoJudicial");

        if (adeNumeros != null) {
            for (final String adeNum : adeNumeros) {
                if (!adeNum.matches("^[0-9]+$")) {
                    throw new AutorizacaoControllerException("mensagem.erro.ade.numero.invalido.arg0", responsavel, adeNum);
                }
            }
        }

        // Verifica se necessita de matricula e CPF para efetuar consulta
        // Parâmetro de obrigatoriedade de CPF e Matrícula
        boolean requerMatriculaCpf = false;
        if (ParamSist.paramEquals(CodedValues.TPC_INF_MAT_CPF_EDT_CONSIGNACAO, CodedValues.TPC_SIM, responsavel)) {
            requerMatriculaCpf = false;
        } else {
            requerMatriculaCpf = parametroController.requerMatriculaCpf(responsavel);
        }

        // Se matrícula e cpf são nulos, e não foi informado ade número, redireciona para a tela de pesquisa
        if (TextHelper.isNull(rseMatricula) && TextHelper.isNull(serCpf) && TextHelper.isNull(adeNumero) && ((adeNumeros == null) || (adeNumeros.length == 0))) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.campo", responsavel));
            return iniciar(request, response, session, model);
        }
        // Se pelo menos um campo foi informado, mas o sistema requer que ambos sejam informados,
        // redireciona para a tela de pesquisa
        if (requerMatriculaCpf && (
                (!TextHelper.isNull(rseMatricula) &&  TextHelper.isNull(serCpf)) ||
                (TextHelper.isNull(rseMatricula) && !TextHelper.isNull(serCpf)))) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.pesquisa.matricula.e.cpf.obrigatorios", responsavel));
            return iniciar(request, response, session, model);
        }

        // Parâmetro de obrigatoriedade de data de nascimento
        final boolean requerDataNascimento = parametroController.requerDataNascimento(responsavel);

        // Se não informou data de nascimento do servidor na pesquisa, redireciona para a tela de pesquisa
        if (requerDataNascimento && TextHelper.isNull(serDataNasc)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.dataNascNaoInformada", responsavel));
            return iniciar(request, response, session, model);
        }

        final Object paramValidarVezesCaptcha = ParamSist.getInstance().getParam(CodedValues.TPC_VALIDAR_QTDE_VEZES_CAPTCHA_CONSULTAR_CONSIGNACAO, null);
        if (responsavel.isCsaCor() && CodedValues.FUN_CONS_CONSIGNACAO.equals(responsavel.getFunCodigo()) && !TextHelper.isNull(paramValidarVezesCaptcha) && (Integer.parseInt(paramValidarVezesCaptcha.toString()) >= 0)) {
            final boolean isValid = validarCaptcha(request, response, session, model, responsavel);
            if (!isValid) {
                return iniciar(request, response, session, model);
            }
        }

        final boolean geraSenhaAutOtp = ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel) && ParamSist.paramEquals(CodedValues.TPC_GERA_OTP_SENHA_AUTORIZACAO, CodedValues.TPC_SIM, responsavel);

        // Se foi digitado a senha do servidor, armazena a senha na sessão,
        // para o caso de precisar redirecionar para tela de seleção de servidor
        final String serSenha = JspHelper.verificaVarQryStr(request, JspHelper.verificaVarQryStr(request, "cryptedPasswordFieldName"));
        if (!"".equals(serSenha)) {
            session.setAttribute("strParSenha", "true");
            session.setAttribute("serAutorizacao", serSenha);
            session.setAttribute("serLogin", JspHelper.verificaVarQryStr(request, "serLogin"));
        } else if ((!validaAutorizacaoSemSenha && !validaAutorizacaoSemSenhaNovamente) && !geraSenhaAutOtp && ParamSist.paramEquals(CodedValues.TPC_SENHA_SER_OBRIGATORIA_EXIBIR_DADOS_CSA_COR, CodedValues.TPC_SIM, responsavel) &&
                   responsavel.isCsaCor() && (CodedValues.FUN_CONS_MARGEM.equals(responsavel.getFunCodigo()) || CodedValues.FUN_RES_MARGEM.equals(responsavel.getFunCodigo()))) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.ser.senha", responsavel));
            return iniciar(request, response, session, model);
        }

        boolean validaPermissionario = false;
        if (CodedValues.FUN_CONSULTAR_DESPESA_INDIVIDUAL.equals(responsavel.getFunCodigo()) || CodedValues.FUN_INC_DESPESA_INDIVIDUAL.equals(responsavel.getFunCodigo())) {
            validaPermissionario = true;
        }

        String orgCodigo = null;
        if (CodedValues.FUN_INCLUIR_CONSIGNACAO.equals(responsavel.getFunCodigo())) {
            orgCodigo = JspHelper.verificaVarQryStr(request, "ORG_CODIGO");
        }

        String rseCodigo = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "RSE_CODIGO")) ? JspHelper.verificaVarQryStr(request, "RSE_CODIGO") : null;
        final TransferObject criterios = new CustomTransferObject();
        if (!TextHelper.isNull(rseMatricula) || !TextHelper.isNull(serCpf)) {
            String tipoEntidade = responsavel.getTipoEntidade();
            String codigoEntidade = responsavel.getCodigoEntidade();

            if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                tipoEntidade = AcessoSistema.ENTIDADE_EST;
                codigoEntidade = responsavel.getCodigoEntidadePai();
            } else if (responsavel.isCor() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
                tipoEntidade = AcessoSistema.ENTIDADE_CSA;
                codigoEntidade = responsavel.getCodigoEntidadePai();
            }

            if (!TextHelper.isNull(rseCodigo)) {
                criterios.setAttribute("RSE_CODIGO", rseCodigo);
            }

            if (!TextHelper.isNull(serDataNasc)) {
                // Verifica formatação e realiza parse da data de nascimento
            	Date serDataNascimento = null;
                try {
                    serDataNascimento = DateHelper.parse(serDataNasc, LocaleHelper.getDatePattern());
                } catch (final ParseException ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.data.nascimento.informada.invalida", responsavel, serDataNasc));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                criterios.setAttribute("serDataNascimento", serDataNascimento);
            }

            final int total = pesquisarServidorController.countPesquisaServidor(tipoEntidade, codigoEntidade, null, null, rseMatricula, serCpf, responsavel, false, null, validaPermissionario, orgCodigo, criterios, vrsCodigo);
            final int offset = (!TextHelper.isNull(request.getParameter("offset")) && TextHelper.isNum(request.getParameter("offset"))) ?  Integer.parseInt(request.getParameter("offset")) : 0;
            final int size = JspHelper.LIMITE;

            // Se não encontrou nenhum servidor, define mensagem de erro e retorna à página de pesquisa
            if (total == 0) {
                if (validaAutorizacaoSemSenha) {
                    session.removeAttribute("valida_autorizacao");
                    return CodedValues.VALIDA_AUTORIZACAO_SOLICITA_SENHA_CONTINUA;
                }
                final StringBuilder msg = new StringBuilder().append(ApplicationResourcesHelper.getMessage("mensagem.selecionar.servidor.erro.nenhum.registro", responsavel)).append(":<br>");

                if (!TextHelper.isNull(rseMatricula)) {
                    msg.append(ApplicationResourcesHelper.getMessage("rotulo.servidor.matricula", responsavel)).append(": <span class=\"normal\">").append(rseMatricula).append("</span> ");
                }
                if (!TextHelper.isNull(serCpf)) {
                    msg.append(ApplicationResourcesHelper.getMessage("rotulo.servidor.cpf", responsavel)).append(": <span class=\"normal\">").append(serCpf).append("</span>");
                }

                session.setAttribute(CodedValues.MSG_ERRO, msg.toString());
                return tratarSevidorNaoEncontrado(request, response, session, model);
            }

            final List<TransferObject> lstServidor = pesquisarServidorController.pesquisaServidor(tipoEntidade, codigoEntidade, null, null, rseMatricula, serCpf, offset, size, responsavel, false, null, validaPermissionario, orgCodigo, criterios, vrsCodigo, false, null);

            if ((lstServidor == null) || lstServidor.isEmpty()) {
                if (validaAutorizacaoSemSenha) {
                    session.removeAttribute("valida_autorizacao");
                    return CodedValues.VALIDA_AUTORIZACAO_SOLICITA_SENHA_CONTINUA;
                }
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.selecionar.servidor.erro.nenhum.registro", responsavel));
                return tratarSevidorNaoEncontrado(request, response, session, model);
            }

            final TransferObject servidor = lstServidor.get(0);

            if (!TextHelper.isNull(rseMatricula) && !TextHelper.isNull(serCpf)) {
                if (!servidor.getAttribute(Columns.SER_CPF).toString().substring(0, Math.min(serCpf.length(), servidor.getAttribute(Columns.SER_CPF).toString().length())).equals(serCpf)) {
                    session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.cpfInvalido", responsavel));
                }

                final String matricula = servidor.getAttribute(Columns.RSE_MATRICULA).toString();
                if ((matricula != null) && !matricula.isEmpty()) {
                    if (ParamSist.paramEquals(CodedValues.TPC_MATRICULA_NUMERICA, CodedValues.TPC_SIM, responsavel)) {
                        if (!Long.valueOf(matricula.substring(0, Math.min(rseMatricula.length(), matricula.length()))).equals(Long.valueOf(rseMatricula))) {
                            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.matriculaInvalida", responsavel));
                        }
                    } else if (!matricula.substring(0, Math.min(rseMatricula.length(), matricula.length())).equals(rseMatricula)) {
                        session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.matriculaInvalida", responsavel));
                    }
                }
            }

            // Se tem mais de um, redireciona para a página de seleção de servidor
            if (total > 1) {
                if (validaAutorizacaoSemSenha) {
                    session.setAttribute("valida_autorizacao_novamente", Boolean.TRUE);
                    session.removeAttribute("valida_autorizacao");
                    return CodedValues.VALIDA_AUTORIZACAO_SOLICITA_SENHA_CONTINUA;
               }
                model.addAttribute("lstServidor", lstServidor);

                // Monta lista de parâmetros através dos parâmetros de request
                final Set<String> params = new HashSet<>(request.getParameterMap().keySet());

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

                final List<String> requestParams = new ArrayList<>(params);

                model.addAttribute("queryString", getQueryString(requestParams, request));

                // Monta link de paginação
                final String linkSelecionaServidor = request.getRequestURI() + "?acao=pesquisarServidor";
                configurarPaginador(linkSelecionaServidor, "rotulo.paginacao.titulo.servidor", total, size, requestParams, false, request, model);

                final String proximaAcao = definirProximaOperacao(request, responsavel);
                model.addAttribute("proximaAcao", proximaAcao);

                // Define se é alteração de contrato de Decisão Judicial
                model.addAttribute("tipoDecisaoJudicial", tipoDecisaoJudicial);

                if (CodedValues.FUN_COMP_CONTRATO.equals(responsavel.getFunCodigo()) && !geraSenhaAutOtp && !ParamSist.paramEquals(CodedValues.TPC_TEM_VALIDACAO_DIGITAL_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                    try {
                        final InformacaoSerCompraEnum exigeInfCompra = parametroController.senhaServidorObrigatoriaCompra(responsavel);
                        if (InformacaoSerCompraEnum.SENHA.equals(exigeInfCompra)) {
                            model.addAttribute("exibirCampoSenhaAutorizacao", Boolean.TRUE);
                        } else if (InformacaoSerCompraEnum.CONTA_BANCARIA.equals(exigeInfCompra)) {
                            model.addAttribute("exibirCampoInfBancaria", Boolean.TRUE);
                        }
                    } catch (final ParametroControllerException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }

                return viewRedirect("jsp/consultarServidor/selecionarServidor", request, session, model, responsavel);
            }

            rseCodigo = (String) servidor.getAttribute(Columns.RSE_CODIGO);
        }

        if (validaAutorizacaoSemSenha) {
            session.removeAttribute("valida_autorizacao");
            final String svcCodigo = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "SVC_CODIGO")) ? JspHelper.verificaVarQryStr(request, "SVC_CODIGO") : null;
            if (parametroController.verificaAutorizacaoReservaSemSenha(rseCodigo, svcCodigo, true, adeNumero, responsavel)) {
                final List<ConsultaMargemSemSenha> lstCsaMargemSemSenha = consignatariaController.listaCsaConsultaMargemSemSenhaAlertaPermissaoRetirada(rseCodigo, responsavel.getCsaCodigo(), responsavel);
                if ((lstCsaMargemSemSenha != null) && !lstCsaMargemSemSenha.isEmpty()) {
                    return CodedValues.VALIDA_AUTORIZACAO_SOLICITA_SENHA_ALERTA;
                }

                return CodedValues.VALIDA_AUTORIZACAO_SOLICITA_SENHA_SIM;
            } else {
                session.setAttribute("valida_autorizacao_novamente", Boolean.TRUE);
                return CodedValues.VALIDA_AUTORIZACAO_SOLICITA_SENHA_CONTINUA;
            }
        }

        // Repassa o token salvo, pois o método irá revalidar o token
        request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

        if ((validaAutorizacaoSemSenhaNovamente || validaAutorizacaoSemSenha) && CodedValues.FUN_COMP_CONTRATO.equals(responsavel.getFunCodigo())) {
            // Existe validação de OTP e digital na compra, porém se não exige senha, não pode fazer essa validação
            session.setAttribute("SEMSENHA_SER", Boolean.TRUE);
        } else if (validaAutorizacaoSemSenhaNovamente) {
            session.removeAttribute("valida_autorizacao_novamente");
        }

        // Realiza a pesquisa de consignação
        executarCancelamentoAutomatico(rseCodigo, adeNumero, session, responsavel);
        return continuarOperacao(rseCodigo, adeNumero, request, response, session, model);
    }

    /**
     * Realiza o tratamento caso nenhum servidor seja encontrado na pesquisa. Por padrão, retorna
     * à tela de pesquisa inicial com a mensagem de erro
     * @param request
     * @param response
     * @param session
     * @param model
     * @return
     */
    protected String tratarSevidorNaoEncontrado(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return iniciar(request, response, session, model);
    }

    /**
     * Dá sequência à operação, após a escolha de um servidor
     * @param rseCodigo
     * @param adeNumero
     * @param request
     * @param response
     * @param session
     * @param model
     * @return
     * @throws AutorizacaoControllerException
     * @throws UsuarioControllerException
     * @throws ZetraException
     */
    protected abstract String continuarOperacao(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, @RequestParam(value = "ADE_NUMERO", required = true, defaultValue = "") String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws AutorizacaoControllerException, UsuarioControllerException;

    /**
     * Define qual a URI da próxima operação
     * @param request
     * @param responsavel
     * @return
     */
    protected abstract String definirProximaOperacao(HttpServletRequest request, AcessoSistema responsavel);

    /**
     * Executa o cancelamento automático de consignações para o servidor selecionado
     * @param rseCodigo
     * @param adeNumero
     * @param session
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    private void executarCancelamentoAutomatico(String rseCodigo, String adeNumero, HttpSession session, AcessoSistema responsavel) throws AutorizacaoControllerException {
        if (!TextHelper.isNull(rseCodigo) || !TextHelper.isNull(adeNumero)) {
            // Se o parametro diz que o cancelamento automático não é diário (de consignações
            // ou de solicitações), então executa rotina de cancelamento para o servidor específico
            final List<String> sad = new ArrayList<>();
            final Object adeExpiradas = ParamSist.getInstance().getParam(CodedValues.TPC_CANC_AUT_DIARIO_CONSIGNACOES, responsavel);
            final Object solExpiradas = ParamSist.getInstance().getParam(CodedValues.TPC_CANC_AUT_DIARIO_SOLICITACOES, responsavel);
            if ((adeExpiradas == null) || CodedValues.TPC_NAO.equals(adeExpiradas)) {
                sad.add(CodedValues.SAD_AGUARD_CONF);
                sad.add(CodedValues.SAD_AGUARD_DEFER);
            }
            if ((solExpiradas == null) || CodedValues.TPC_NAO.equals(solExpiradas)) {
                sad.add(CodedValues.SAD_SOLICITADO);
            }
            if (sad.size() > 0) {
                cancelarConsignacaoController.cancelarExpiradas(rseCodigo, adeNumero, sad, responsavel);
            }
        }
    }

    protected boolean validarSenhaServidor(String rseCodigo, boolean consomeSenha, HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        if (!TextHelper.isNull(rseCodigo)) {
            try {
                SenhaHelper.validarSenha(request, rseCodigo, null, false, false, consomeSenha, responsavel);
            } catch (final ViewHelperException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return false;
            }
        }
        return true;
    }

    @RequestMapping(params = { "acao=listarHistLiquidacoesAntecipadas" })
    protected String listarHistLiquidacoesAntecipadas(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        // Na consulta de margem uma janela com esta página é aberta, logo não pode invalidar ou trocar token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            final String rseCodigo = JspHelper.verificaVarQryStr(request, "RSE_CODIGO");
            final String rseMatricula = JspHelper.verificaVarQryStr(request, "RSE_MATRICULA");
            final String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
            final String serNome = TextHelper.decode64(JspHelper.verificaVarQryStr(request, "SER_NOME"));

            //Parâmetros de serviço
            ParamSvcTO paramSvcCse = null;
            try {
                paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final int numMaxHistoricosLiquidacoes = (!TextHelper.isNull(paramSvcCse.getTpsNumAdeHistLiquidacoesAntecipadas()) ? Integer.parseInt(paramSvcCse.getTpsNumAdeHistLiquidacoesAntecipadas()) : 0);
            final int numMaxHistoricosSuspensoes = (!TextHelper.isNull(paramSvcCse.getTpsNumAdeHistSuspensoes()) ? Integer.parseInt(paramSvcCse.getTpsNumAdeHistSuspensoes()) : 0);

            final TransferObject nseTO = servicoController.findNaturezaServico(svcCodigo, responsavel);

            List<TransferObject> historicos = null;
            if (((numMaxHistoricosLiquidacoes > 0) || (numMaxHistoricosSuspensoes > 0)) && (nseTO != null)) {
                historicos = compraContratoController.lstHistoricoSuspensoesLiquidacoesAntecipadas(rseCodigo, (String) nseTO.getAttribute(Columns.NSE_CODIGO), numMaxHistoricosLiquidacoes, numMaxHistoricosSuspensoes, responsavel);
            } else {
                historicos = new ArrayList<>();
            }

            // Lista de consignações que tiveram valor reduzido
            final List<String> sadCodigos= new ArrayList<>();
            sadCodigos.add(CodedValues.SAD_DEFERIDA);
            sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
            final List<String> tocCodigos = null;
            final List<TransferObject> valorReduzido = compraContratoController.lstHistoricoConsignacao(rseCodigo, (String) nseTO.getAttribute(Columns.NSE_CODIGO), sadCodigos, tocCodigos, -1, true, responsavel);

            if ((historicos != null) && (valorReduzido != null) && !valorReduzido.isEmpty()) {
                historicos.addAll(valorReduzido);
            } else if ((historicos == null) && (valorReduzido != null) && !valorReduzido.isEmpty()) {
                historicos = valorReduzido;
            }

            model.addAttribute("historicos", historicos);
            model.addAttribute("rseMatricula", rseMatricula);
            model.addAttribute("serNome", serNome);
            model.addAttribute("responsavel", responsavel);

            return viewRedirect("jsp/renegociarConsignacao/listarHistLiquidacoesAntecipadas", request, session, model, responsavel);

        } catch (ServicoControllerException | CompraContratoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=gerarSenhaAutorizacaoOtp" }, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> gerarSenhaAutorizacaoOtp(HttpServletRequest request, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final String rseCodigo = JspHelper.verificaVarQryStr(request, "RSE_CODIGO");
        final String modoEntrega = JspHelper.verificaVarQryStr(request, "MODO_ENTREGA");
        String otpReconhecimentoFacial = null;

        // Envia OTP servidor
        if (ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel) &&
            ParamSist.paramEquals(CodedValues.TPC_GERA_OTP_SENHA_AUTORIZACAO, CodedValues.TPC_SIM, responsavel)) {

            final JsonObjectBuilder result = Json.createObjectBuilder();

            if (TextHelper.isNull(rseCodigo)) {
                result.add("mensagem", ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.nao.encontrado", responsavel));
                return new ResponseEntity<>(result.build().toString(), HttpStatus.NOT_FOUND);
            }

            try {
                if(TextHelper.isNull(modoEntrega)) {
                    usuarioController.gerarSenhaAutorizacaoOtp(rseCodigo, AcessoSistema.getAcessoUsuarioSistema());
                }else if(CodedValues.ALTERACAO_SENHA_AUT_SER_RECONHECIMENTO_FACIAL.equals(modoEntrega)){
                    otpReconhecimentoFacial = usuarioController.gerarSenhaAutorizacaoOtp(rseCodigo, modoEntrega, AcessoSistema.getAcessoUsuarioSistema());
                    result.add("otpReconhecimentoFacial", otpReconhecimentoFacial);
                }else {
                    usuarioController.gerarSenhaAutorizacaoOtp(rseCodigo, modoEntrega, AcessoSistema.getAcessoUsuarioSistema());
                }
            } catch (final UsuarioControllerException e) {
                result.add("mensagem", e.getMessage());
                return new ResponseEntity<>(result.build().toString(), HttpStatus.CONFLICT);
            }

            result.add("mensagem", ApplicationResourcesHelper.getMessage("mensagem.sucesso.senha.autorizacao.servidor.enviada", responsavel));
            return new ResponseEntity<>(result.build().toString(), HttpStatus.OK);
        }

        return null;
    }

    /**
     * @param request
     * @param response
     * @param session
     * @param model
     * @param responsavel
     */
    protected boolean validarCaptcha(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model, AcessoSistema responsavel) {
        boolean retorno = true;

        // DESENV-13988 - Criação de novo parâmetro de sistema (captcha na consulta de consignação).
        if (TextHelper.isNull(session.getAttribute("isValidCaptcha")) || !Boolean.parseBoolean(session.getAttribute("isValidCaptcha").toString())) {
            final String usuCodigo = responsavel.getUsuCodigo();

            if (responsavel.isCsaCor()) {
                final boolean podeConsultar = ControleConsulta.getInstance().podeConsultarConsignacaoSemCaptcha(usuCodigo);

                if (!podeConsultar) {
                    final boolean defVisual = responsavel.isDeficienteVisual();

                    if (!defVisual) {
                        if (ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_TELA_LOGIN, CodedValues.TPC_SIM, responsavel)) {
                            if (ImageCaptchaServlet.armazenaCaptcha(session.getId(), (String) session.getAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY))
                                        && !ImageCaptchaServlet.validaCaptcha(session.getId(), request.getParameter("captcha"))) {
                                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                                retorno = false;
                            }
                            session.removeAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY);
                        } else if (ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN, CodedValues.TPC_SIM, responsavel)) {
                            final String remoteAddr = request.getRemoteAddr();

                            if (!isValidCaptcha(request.getParameter("g-recaptcha-response"), remoteAddr, responsavel)) {
                                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                                retorno = false;
                            }
                        }
                    } else {
                        final boolean exigeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                        if (exigeCaptchaDeficiente) {
                            final String captchaAnswer = request.getParameter("captcha");

                            if (captchaAnswer == null) {
                                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                                retorno = false;
                            }

                            final String captchaCode   = (String) session.getAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
                            if ((captchaCode == null) || !captchaCode.equalsIgnoreCase(captchaAnswer)) {
                                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                                retorno = false;
                            }
                            session.removeAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
                        }
                    }
                }
            }

            if (retorno) {
                session.setAttribute("isValidCaptcha", true);
                ControleConsulta.getInstance().somarValorCaptcha(usuCodigo);
            }
        }
        return retorno;
    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=validarAutorizacaoSemSenha" }, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> validarAutorizacaoSemSenha(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final JsonObjectBuilder result = Json.createObjectBuilder();
        session.setAttribute("valida_autorizacao", Boolean.TRUE);
        try {
            result.add("situacao", pesquisarServidor(request, response, session, model));
        } catch (InstantiationException | IllegalAccessException | ServletException | IOException | ParseException | ZetraException e) {
            result.add("situacao", e.getMessage());
            return new ResponseEntity<>(result.build().toString(), HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(result.build().toString(), HttpStatus.OK);
    }
}
