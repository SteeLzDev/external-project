package com.zetra.econsig.webservice.rest.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.zetra.econsig.service.mensagem.MensagemController;
import com.zetra.econsig.service.formulariopesquisa.FormularioPesquisaController;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.google.gson.Gson;
import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.EnderecoFuncaoTransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.OcorrenciaUsuarioTransferObject;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.ArquivoControllerException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.consignacao.SolicitacaoServidorHelper;
import com.zetra.econsig.helper.criptografia.JCrypt;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.ControleLogin;
import com.zetra.econsig.helper.sistema.CreateImageHelper;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.usuario.UsuarioHelper;
import com.zetra.econsig.job.process.ProcessaContraCheque;
import com.zetra.econsig.persistence.entity.ArquivoUsuario;
import com.zetra.econsig.persistence.entity.Servidor;
import com.zetra.econsig.persistence.entity.Usuario;
import com.zetra.econsig.report.config.ConfigRelatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.arquivo.ArquivoController;
import com.zetra.econsig.service.folha.ImpArqContrachequeController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CanalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.web.filter.XSSPreventionFilter;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.filter.IpWatchdog;
import com.zetra.econsig.webservice.rest.request.AutenticarEuConsigoMaisRequest;
import com.zetra.econsig.webservice.rest.request.CodigoUnicoRestResponse;
import com.zetra.econsig.webservice.rest.request.ConsultarMargemRestRequest;
import com.zetra.econsig.webservice.rest.request.ContraChequeRestRequest;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;
import com.zetra.econsig.webservice.rest.request.ServidorRestResponse;
import com.zetra.econsig.webservice.rest.request.UsuarioImgRestRequest;
import com.zetra.econsig.webservice.rest.request.UsuarioRestRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

/**
 * <p>Title: UsuarioService</p>
 * <p>Description: Serviço REST para usuário.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Path("/usuario")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
@Controller
public class UsuarioService extends RestService {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(UsuarioService.class);

    @Context
    private SecurityContext securityContext;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    private ArquivoController arquivoController;

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private ConsultarMargemController consultarMargemController;

    @Autowired
    private FormularioPesquisaController formularioPesquisaController;

    @POST
    @Path("/primeiroAcesso")
    public Response primeiroAcesso(UsuarioRestRequest usuario, @Context HttpServletRequest request) {
        final AcessoSistema respValidacao = AcessoSistema.getAcessoUsuarioSistema();

        // Verifica bloqueio por ip
        final String ip = request.getRemoteAddr();
        int delay = IpWatchdog.verificaIp(ip);
        if (delay > 0) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.rest.esperar.segundos", null, String.valueOf(delay));
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        if (usuario == null) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        final Response responseSist = verificaSistemaDisponivel();
        if (responseSist != null) {
            return responseSist;
        }

        if (!ParamSist.getBoolParamSist(CodedValues.TPC_VALIDA_OTP_PRIMEIRO_ACESSO_USUARIO, respValidacao)) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.rest.primeiro.acesso.otp.desabilitado", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        respValidacao.setTipoEntidade(AcessoSistema.ENTIDADE_SER);

        try {
            final Map<String, Object> retorno = usuarioController.primeiroAcesso(!TextHelper.isNull(usuario.id) ? usuario.id : usuario.cpf, usuario.orgCodigo, usuario.otp, respValidacao);
            IpWatchdog.desbloqueiaIp(ip);
            return Response.status(Response.Status.OK).entity(retorno).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        } catch (final UsuarioControllerException e) {
            delay = IpWatchdog.bloqueiaIp(ip);
            if (delay > 0) {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.rest.esperar.segundos", null, String.valueOf(delay));
                LOG.error(e.getMessage(), e);
                return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
            }
            LOG.error(e.getMessage(), e);

            if ("mensagem.usuario.possui.cadastro.ativo".equals(e.getMessageKey())) {
                return genericError(new ZetraException("mensagem.usuario.possui.cadastro.ativo.mobile", AcessoSistema.getAcessoUsuarioSistema()));
            }

            return genericError(e);
        }
    }

    @POST
    @Path("/enviaOTP")
    public Response enviaOTP(UsuarioRestRequest request) {
        if (request == null) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        final Response responseSist = verificaSistemaDisponivel();
        if (responseSist != null) {
            return responseSist;
        }

        if (TextHelper.isNull(request.usuCodigo) || TextHelper.isNull(request.token)) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
        responsavel.setTipoEntidade(AcessoSistema.ENTIDADE_SER);

        try {
            usuarioController.enviaOTPServidor(request.usuCodigo, request.token, request.email, request.telefone, responsavel);
        } catch (final UsuarioControllerException e) {
            LOG.error(e.getMessage(), e);
            return genericError(e);
        }

        return Response.status(Response.Status.OK).entity(new ResponseRestRequest()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
    }

    @POST
    @Path("/finalizaPrimeiroAcesso")
    public Response finalizaPrimeiroAcesso(UsuarioRestRequest usuario) {
        AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        if (responsavel == null) {
            responsavel = AcessoSistema.getAcessoUsuarioSistema();
            responsavel.setTipoEntidade(AcessoSistema.ENTIDADE_SER);
        }

        if (usuario == null) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        final Response responseSist = verificaSistemaDisponivel();
        if (responseSist != null) {
            return responseSist;
        }

        if (TextHelper.isNull(usuario.usuCodigo)) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.operacaoInvalida", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        if (TextHelper.isNull(usuario.otp)) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.otp.nao.informado", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        if (TextHelper.isNull(usuario.senha)) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.usuario.senha", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        if (TextHelper.isNull(usuario.token)) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.operacaoInvalida", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        try {
            final AcessoSistema respValidacao = AcessoSistema.getAcessoUsuarioSistema();

            if (!UsuarioHelper.isTokenOtpPageValido(usuario.usuCodigo, usuario.token, respValidacao)) {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.operacaoInvalida", null);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
            }

            final boolean habilitaSenhaApp = ParamSist.paramEquals(CodedValues.TPC_HABILITA_SENHA_APP, CodedValues.TPC_SIM, responsavel);

            // autentica usuário e retorna dados da autenticação para usuário
            final List<TransferObject> lstRses = usuarioController.validaOTPServidor(usuario.usuCodigo, usuario.senha, false, usuario.otp, usuario.email, usuario.telefone, habilitaSenhaApp, respValidacao);
            // recupera lista com todos os registros do CPF
            final List<TransferObject> lstRegistroServidores = servidorController.lstRegistroServidorPorCpf((String) lstRses.get(0).getAttribute(Columns.SER_CPF), null, respValidacao);

            final Map<String, Object> retorno = new HashMap<>();
            final Map<String, Object> retornoAux = autenticarUsuario(usuario.senha, lstRegistroServidores, (String) lstRses.get(0).getAttribute(Columns.SER_CPF), null, habilitaSenhaApp, false, true, respValidacao);

            final List<Object> orgaos = new ArrayList<>();
            if (retornoAux.get("rseList") != null) {
                orgaos.add(retornoAux.get("rseLogado"));
                orgaos.addAll((Collection<Object>) retornoAux.get("rseList"));
                retorno.put("rseList", orgaos);
            }

            retorno.put("rseLogado", retornoAux.get("rseLogado"));

            //Lista de parâmetros de sistema a serem passados para o Mobile
            final Map<String, String> paramSist = new HashMap<>();

            final boolean temLeilao = ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR, responsavel);
            paramSist.put(CodedValues.TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR.toString(), String.valueOf(temLeilao));

            final boolean smsCodAutorizacao = ParamSist.getBoolParamSist(CodedValues.TPC_ENVIA_SMS_CODIGO_UNICO_AUTORIZACAO_CONSIGNACAO, responsavel);
            paramSist.put(CodedValues.TPC_ENVIA_SMS_CODIGO_UNICO_AUTORIZACAO_CONSIGNACAO.toString(), String.valueOf(smsCodAutorizacao));

            retorno.put("paramSist", paramSist);

            //Verifica se o campo de telefone deve ser mostrado e requerido
            final Map<String, String> campoSist = new HashMap<>();
            final boolean exibeTelefone = ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel);
            boolean exigeTelefone = ParamSist.paramEquals(CodedValues.TPC_REQUER_TEL_SER_SOLIC_EMPRESTIMO, CodedValues.TPC_SIM, responsavel);

            exigeTelefone = exigeTelefone || ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel);

            campoSist.put("exibeTelefone", String.valueOf(exibeTelefone));
            campoSist.put("exigeTelefone", String.valueOf(exigeTelefone));

            campoSist.put("TPC_EXIGE_CAD_EMAIL_SER_PRIMEIRO_ACESSO_SIST", String.valueOf(ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_ATUALIZACAO_DADOS_SER_PRIMEIRO_ACESSO, responsavel) && ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_CAD_EMAIL_SER_PRIMEIRO_ACESSO_SIST, responsavel)));
            campoSist.put("TPC_EXIGE_CAD_TELEFONE_SER_PRIMEIRO_ACESSO_SIST", String.valueOf(ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_ATUALIZACAO_DADOS_SER_PRIMEIRO_ACESSO, responsavel) && ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_CAD_TELEFONE_SER_PRIMEIRO_ACESSO_SIST, responsavel)));

            final String ATUALIZAR_DADOS_SER_ENDERECO = ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_ENDERECO, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.ATUALIZAR_DADOS_SER_ENDERECO, responsavel) ? "S" : "N";
            final String ATUALIZAR_DADOS_SER_CIDADE = ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_CIDADE, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.ATUALIZAR_DADOS_SER_CIDADE, responsavel) ? "S" : "N";
            final String ATUALIZAR_DADOS_SER_BAIRRO = ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_BAIRRO, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.ATUALIZAR_DADOS_SER_BAIRRO, responsavel) ? "S" : "N";
            final String ATUALIZAR_DADOS_SER_COMPLEMENTO = ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_COMPLEMENTO, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.ATUALIZAR_DADOS_SER_COMPLEMENTO, responsavel) ? "S" : "N";
            final String ATUALIZAR_DADOS_SER_NUMERO = ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_NUMERO, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.ATUALIZAR_DADOS_SER_NUMERO, responsavel) ? "S" : "N";
            final String ATUALIZAR_DADOS_SER_CEP = ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_CEP, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.ATUALIZAR_DADOS_SER_CEP, responsavel) ? "S" : "N";
            final String ATUALIZAR_DADOS_SER_UF = ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_UF, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.ATUALIZAR_DADOS_SER_UF, responsavel) ? "S" : "N";

            campoSist.put("ATUALIZAR_DADOS_SER_ENDERECO", ATUALIZAR_DADOS_SER_ENDERECO);
            campoSist.put("ATUALIZAR_DADOS_SER_CIDADE", ATUALIZAR_DADOS_SER_CIDADE);
            campoSist.put("ATUALIZAR_DADOS_SER_BAIRRO", ATUALIZAR_DADOS_SER_BAIRRO);
            campoSist.put("ATUALIZAR_DADOS_SER_COMPLEMENTO", ATUALIZAR_DADOS_SER_COMPLEMENTO);
            campoSist.put("ATUALIZAR_DADOS_SER_NUMERO", ATUALIZAR_DADOS_SER_NUMERO);
            campoSist.put("ATUALIZAR_DADOS_SER_CEP", ATUALIZAR_DADOS_SER_CEP);
            campoSist.put("ATUALIZAR_DADOS_SER_UF", ATUALIZAR_DADOS_SER_UF);

            //inicio - DESENV-8259
            final String tocCodigo = CodedValues.TOC_DIVERGENCIA_CADASTRO_EMAIL_SERVIDOR;
            final List<TransferObject> listaOcorrenciaEmailIncorretoServidor = servidorController.lstDataOcorrenciaServidor(((ServidorRestResponse) retornoAux.get("rseLogado")).serCodigo, tocCodigo, responsavel);
            boolean servidorDentroDoPrazoSemValidacaoEmail = !ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_CAD_EMAIL_SER_PRIMEIRO_ACESSO_SIST, responsavel);

            if (!listaOcorrenciaEmailIncorretoServidor.isEmpty()) {
                final Date dataOcorrencia = (Date) listaOcorrenciaEmailIncorretoServidor.get(0).getAttribute(Columns.OCS_DATA);
                servidorDentroDoPrazoSemValidacaoEmail = servidorController.validaServidorDentroPrazoAcessoSistemaSemValidacaoEmail(((ServidorRestResponse) retornoAux.get("rseLogado")).serCodigo, dataOcorrencia, responsavel);
            }

            campoSist.put("servidorDentroDoPrazoSemValidacaoEmail", String.valueOf(servidorDentroDoPrazoSemValidacaoEmail));
            //FIM - DESENV-8259

            retorno.put("campoSist", campoSist);

            return Response.status(Response.Status.OK).entity(retorno).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        } catch (final ZetraException e) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = e.getMessage();
            LOG.error(e.getMessage(), e);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }
    }

    @POST
    @Secured
    @Path("/registraOcorrenciaEmailIncorreto")
    public Response registraOcorrenciaEmailIncorreto(UsuarioRestRequest usuario, @Context HttpServletRequest request) {
        try {
            final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

            final ServidorTransferObject serTO = servidorController.findServidor(responsavel.getCodigoEntidade(), responsavel);
            final String serCodigo = serTO.getSerCodigo();
            String qtdeDias = ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_ACESSO_SISTEMA_SERVIDOR_SEM_VALIDACAO_EMAIL, responsavel).toString();
            final String tmoObs = ApplicationResourcesHelper.getMessage("mensagem.informacao.email.incorreto.ocorrencia", responsavel, qtdeDias);

            // Ocorrencia de e-mail divergente + dias
            servidorController.criaOcorrenciaSER(serCodigo, CodedValues.TOC_DIVERGENCIA_CADASTRO_EMAIL_SERVIDOR, tmoObs, null, responsavel);

            final String tocCodigo = CodedValues.TOC_DIVERGENCIA_CADASTRO_EMAIL_SERVIDOR;
            final List<TransferObject> listaOcorrenciaEmailIncorretoServidor = servidorController.lstDataOcorrenciaServidor(serCodigo, tocCodigo, responsavel);

            if (!listaOcorrenciaEmailIncorretoServidor.isEmpty()) {
                final int resultadoEmDiasEntreDataAtualMenosDataOcorrenciaEmailIncorreto = DateHelper.dayDiff((Date) listaOcorrenciaEmailIncorretoServidor.get(0).getAttribute(Columns.OCS_DATA));
                final int diasAcessoSemValidacao = Integer.parseInt(qtdeDias) - resultadoEmDiasEntreDataAtualMenosDataOcorrenciaEmailIncorreto;
                qtdeDias = String.valueOf(diasAcessoSemValidacao);
            }

            // Mostra mensagem informando o servidor que pode usar o sistema em quantos dias sem validação e procurar o RH
            final ResponseRestRequest response = new ResponseRestRequest();
            response.mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.email.incorreto.prazo.acesso.sistema", responsavel, serTO.getSerNome(), qtdeDias);
            return Response.status(Response.Status.OK).entity(response).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();

        } catch (final ServidorControllerException ex) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ex.getMessage();
            LOG.error(ex.getMessage(), ex);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }
    }

    @POST
    @Secured
    @Path("/alterarSenha")
    public Response alterarSenha(UsuarioRestRequest usuario, @Context HttpServletRequest request) {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        final String usuCodigo = responsavel.getUsuCodigo();

        // Verifica bloqueio por ip
        final String ip = request.getRemoteAddr();
        final int delay = IpWatchdog.verificaIp(ip);
        if (delay > 0) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.rest.esperar.segundos", null, String.valueOf(delay));
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        if (usuario == null) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        final Response responseSist = verificaSistemaDisponivel();
        if (responseSist != null) {
            return responseSist;
        }

        if (TextHelper.isNull(usuario.senha)) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.usuario.senha", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        if (TextHelper.isNull(usuario.novaSenha) || TextHelper.isNull(usuario.confirmarSenha)) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.alterar.senha.aut", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        if (!usuario.novaSenha.equals(usuario.confirmarSenha)) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.cadastrar.senha.servidor.diferente", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        final String senhaClean = XSSPreventionFilter.stripXSS(usuario.senha);
        final String novaSenhaClean = XSSPreventionFilter.stripXSS(usuario.novaSenha);

        if (TextHelper.isNull(senhaClean)) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.usuario.senha", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        if (TextHelper.isNull(novaSenhaClean)) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.alterar.senha.aut", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        if (senhaClean.equals(novaSenhaClean)) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.ativar.senha.diferente.atual", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        try {
            final UsuarioTransferObject usuTO = usuarioController.findUsuario(usuCodigo, responsavel);

            final String usuSenha = usuTO.getUsuSenha();
            final String usuSenhaApp = usuTO.getUsuSenhaApp();
            
            String senha = null;
            final boolean habilitaSenhaApp = ParamSist.paramEquals(CodedValues.TPC_HABILITA_SENHA_APP, CodedValues.TPC_SIM, responsavel);

            if (habilitaSenhaApp) {
                senha = usuSenhaApp;
            } else {
                senha = usuSenha;
            }

            if (TextHelper.isNull(senha) || !JCrypt.verificaSenha(senhaClean, senha)) {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", null);
                return Response.status(Response.Status.UNAUTHORIZED).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
            }

            // Chegou até aqui porque a senha atual está correta
            usuarioController.alterarSenhaApp(usuCodigo, novaSenhaClean, false, true, responsavel);

            IpWatchdog.desbloqueiaIp(ip);

            final ResponseRestRequest response = new ResponseRestRequest();
            response.mensagem = ApplicationResourcesHelper.getMessage("mensagem.senha.alterada.sucesso", null);
            return Response.status(Response.Status.OK).entity(response).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", null);
            LOG.error(ex.getMessage(), ex);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

    }

    @POST
    @Path("/login")
    public Response login(UsuarioRestRequest usuario, @Context HttpServletRequest request) {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        return login(usuario, false, true, request, responsavel);
    }

    @POST
    @Path("/listarRegistros")
    public Response listarRegistros(UsuarioRestRequest usuario, @Context HttpServletRequest request) {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        return login(usuario, true, true, request, responsavel);
    }

    public Response loginMobileOauth(UsuarioRestRequest usuario, HttpServletRequest request, AcessoSistema responsavel) {
        return login(usuario, false, false, request, responsavel);
    }

    private Response login(UsuarioRestRequest usuario, boolean permiteAguardAprovacaoCadastro, boolean autenticarUsuario, HttpServletRequest request, AcessoSistema responsavel) {
        // Verifica bloqueio por ip
        final String ip = request.getRemoteAddr();
        final Integer portaLogica = request.getRemotePort();
        int delay = IpWatchdog.verificaIp(ip);
        if (delay > 0) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.rest.esperar.segundos", null, String.valueOf(delay));
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }
        if (usuario == null) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        if (TextHelper.isNull(responsavel)) {
            responsavel = AcessoSistema.getAcessoUsuarioSistema();
            responsavel.setTipoEntidade(AcessoSistema.ENTIDADE_SER);
        }

        final Response responseSist = verificaSistemaDisponivel(responsavel);
        if (responseSist != null) {
            return responseSist;
        }

        final String senhaClean = ParamSist.getBoolParamSist(CodedValues.TPC_SENHA_EXTERNA, responsavel) ? usuario.senha : XSSPreventionFilter.stripXSS(usuario.senha);
        final String idClean = XSSPreventionFilter.stripXSS(!TextHelper.isNull(usuario.id) ? usuario.id : usuario.cpf);
        final String orgCodigoClean = XSSPreventionFilter.stripXSS(usuario.orgCodigo);
        final String rseMatriculaClean = XSSPreventionFilter.stripXSS(usuario.matricula);
      
        if (TextHelper.isNull(idClean)) {
            final boolean omiteCpf = ParamSist.getBoolParamSist(CodedValues.TPC_OMITE_CPF_SERVIDOR, responsavel);
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = omiteCpf ? ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.email", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.cpf", responsavel);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        if (TextHelper.isNull(senhaClean) && autenticarUsuario) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.usuario.senha", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        final AcessoSistema usuAcesso = AcessoSistema.getAcessoUsuarioSistema();
        usuAcesso.setIpUsuario(ip);
        usuAcesso.setPortaLogicaUsuario(portaLogica);
        usuAcesso.setCanal(CanalEnum.REST);

        final Map<String, Object> retorno = new HashMap<>();
        try {
            List<String> lstOrgId = null;
            if (!TextHelper.isNull(orgCodigoClean)) {
                lstOrgId = new ArrayList<>();
                lstOrgId.add(orgCodigoClean);
            }

            final boolean omiteCpf = ParamSist.getBoolParamSist(CodedValues.TPC_OMITE_CPF_SERVIDOR, responsavel);
            List<TransferObject> lstRegistroServidores = null;

            if (omiteCpf || usuario.totem) {
                lstRegistroServidores = servidorController.lstRegistroServidorPorEmail(idClean, lstOrgId, AcessoSistema.getAcessoUsuarioSistema());
            } else {
                lstRegistroServidores = servidorController.lstRegistroServidorPorCpf(idClean, lstOrgId, AcessoSistema.getAcessoUsuarioSistema());
            }

            if ((lstRegistroServidores != null) && !lstRegistroServidores.isEmpty()) {
                final boolean habilitaSenhaApp = ParamSist.paramEquals(CodedValues.TPC_HABILITA_SENHA_APP, CodedValues.TPC_SIM, responsavel);

                final Map<String, Object> retornoAux = autenticarUsuario(senhaClean, lstRegistroServidores, idClean, rseMatriculaClean, habilitaSenhaApp, permiteAguardAprovacaoCadastro, autenticarUsuario, usuAcesso);
                final ServidorRestResponse rseLogado = (ServidorRestResponse) retornoAux.get("rseLogado");
                responsavel = new AcessoSistema(rseLogado.usuCodigo);
                responsavel.setIpUsuario(ip);
                responsavel.setPortaLogicaUsuario(portaLogica);
                responsavel.setCanal(CanalEnum.REST);
                responsavel.setTipoEntidade(AcessoSistema.ENTIDADE_SER);
                responsavel.setDadosServidor(rseLogado.estCodigo, orgCodigoClean, rseLogado.rseCodigo, rseLogado.rseMatricula, rseLogado.cpf, rseLogado.email, null, rseLogado.srsCodigo);

                final List<Object> orgaos = new ArrayList<>();
                if (retornoAux.get("rseList") != null) {
                    orgaos.add(rseLogado);
                    orgaos.addAll((Collection<Object>) retornoAux.get("rseList"));
                    retorno.put("rseList", orgaos);
                }
                retorno.put("rseLogado", rseLogado);
                //Lista de parâmetros de sistema a serem passados para o Mobile
                final Map<String, String> paramSist = new HashMap<>();

                final boolean temLeilao = ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR, responsavel);
                final boolean smsCodAutorizacao = ParamSist.getBoolParamSist(CodedValues.TPC_ENVIA_SMS_CODIGO_UNICO_AUTORIZACAO_CONSIGNACAO, responsavel);
                final boolean solicitaPortRanking = ParamSist.getBoolParamSist(CodedValues.TPC_SOLICITAR_PORTABILIDADE_COM_RANKING_CONSIGNATARIA, responsavel);
                final boolean portMargenNegativa = ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_PORTABILIDADE_MARGEM_NEGATIVA, responsavel);

                // DESENV-16381
                final Object paramSistRequerTelefoneServidorSolicitacaoSaldoDevedor = ParamSist.getInstance().getParam(CodedValues.TPC_REQUER_TEL_SER_SOLIC_SALDO_DEVEDOR, responsavel);
                final String periodicidadeFolha = PeriodoHelper.getPeriodicidadeFolha(responsavel);
                final Object paramSistRaioBusca = ParamSist.getInstance().getParam(CodedValues.TPC_RAIO_METROS_BUSCA_END_CONSIGNATARIAS, responsavel);
                final Object paramSistExibeConfiguracaoDadosServidorSimulador = ParamSist.getInstance().getParam(CodedValues.TPC_EXIBE_CONF_DADOS_SER_SIMULADOR, responsavel);

                //melhores nomes para uso no app Flutter
                paramSist.put("TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR", String.valueOf(temLeilao));
                paramSist.put("TPC_ENVIA_SMS_CODIGO_UNICO_AUTORIZACAO_CONSIGNACAO", String.valueOf(smsCodAutorizacao));
                paramSist.put("TPC_PERIODICIDADE_FOLHA", periodicidadeFolha);
                paramSist.put("TPC_RAIO_METROS_BUSCA_END_CONSIGNATARIAS", String.valueOf(paramSistRaioBusca));
                paramSist.put("TPC_REQUER_TEL_SER_SOLIC_SALDO_DEVEDOR", String.valueOf(paramSistRequerTelefoneServidorSolicitacaoSaldoDevedor));
                paramSist.put("TPC_EXIBE_CONF_DADOS_SER_SIMULADOR", String.valueOf(paramSistExibeConfiguracaoDadosServidorSimulador));
                paramSist.put("TPC_SOLICITAR_PORTABILIDADE_COM_RANKING_CONSIGNATARIA", String.valueOf(solicitaPortRanking));
                paramSist.put("TPC_PERMITE_PORTABILIDADE_MARGEM_NEGATIVA", String.valueOf(portMargenNegativa));

                // DESENV-19430
                // PT-BR: Parâmetro de exibição do tutorial no mobile
                // EN-US: Tutorial display parameter on mobile
                final Object paramExibeTutorialMobile = ParamSist.getInstance().getParam(CodedValues.TPC_EXIBE_TUTORIAL_MOBILE, responsavel);
                paramSist.put("TPC_EXIBE_TUTORIAL_MOBILE", String.valueOf(paramExibeTutorialMobile));

                //Verifica se o campo de telefone deve ser mostrado e requerido
                final Map<String, String> campoSist = new HashMap<>();

                final boolean exibeTelefone = ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel);
                boolean exigeTelefone = ParamSist.paramEquals(CodedValues.TPC_REQUER_TEL_SER_SOLIC_EMPRESTIMO, CodedValues.TPC_SIM, responsavel);

                exigeTelefone = exigeTelefone || ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel);

                campoSist.put("exibeTelefone", String.valueOf(exibeTelefone));
                campoSist.put("exigeTelefone", String.valueOf(exigeTelefone));

                final String ATUALIZAR_DADOS_SER_EMAIL = ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_EMAIL, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.ATUALIZAR_DADOS_SER_EMAIL, responsavel) ? "S" : "N";
                final String ATUALIZAR_DADOS_SER_TELEFONE = ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_TELEFONE, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.ATUALIZAR_DADOS_SER_TELEFONE, responsavel) ? "S" : "N";
                final String ATUALIZAR_DADOS_SER_CELULAR = ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_CELULAR, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.ATUALIZAR_DADOS_SER_CELULAR, responsavel) ? "S" : "N";

                final String ATUALIZAR_DADOS_SER_ENDERECO = ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_ENDERECO, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.ATUALIZAR_DADOS_SER_ENDERECO, responsavel) ? "S" : "N";
                final String ATUALIZAR_DADOS_SER_CIDADE = ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_CIDADE, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.ATUALIZAR_DADOS_SER_CIDADE, responsavel) ? "S" : "N";
                final String ATUALIZAR_DADOS_SER_BAIRRO = ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_BAIRRO, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.ATUALIZAR_DADOS_SER_BAIRRO, responsavel) ? "S" : "N";
                final String ATUALIZAR_DADOS_SER_COMPLEMENTO = ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_COMPLEMENTO, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.ATUALIZAR_DADOS_SER_COMPLEMENTO, responsavel) ? "S" : "N";
                final String ATUALIZAR_DADOS_SER_NUMERO = ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_NUMERO, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.ATUALIZAR_DADOS_SER_NUMERO, responsavel) ? "S" : "N";
                final String ATUALIZAR_DADOS_SER_CEP = ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_CEP, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.ATUALIZAR_DADOS_SER_CEP, responsavel) ? "S" : "N";
                final String ATUALIZAR_DADOS_SER_UF = ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_UF, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.ATUALIZAR_DADOS_SER_UF, responsavel) ? "S" : "N";

                campoSist.put("ATUALIZAR_DADOS_SER_EMAIL", ATUALIZAR_DADOS_SER_EMAIL);
                campoSist.put("ATUALIZAR_DADOS_SER_TELEFONE", ATUALIZAR_DADOS_SER_TELEFONE);
                campoSist.put("ATUALIZAR_DADOS_SER_CELULAR", ATUALIZAR_DADOS_SER_CELULAR);

                campoSist.put("ATUALIZAR_DADOS_SER_ENDERECO", ATUALIZAR_DADOS_SER_ENDERECO);
                campoSist.put("ATUALIZAR_DADOS_SER_CIDADE", ATUALIZAR_DADOS_SER_CIDADE);
                campoSist.put("ATUALIZAR_DADOS_SER_BAIRRO", ATUALIZAR_DADOS_SER_BAIRRO);
                campoSist.put("ATUALIZAR_DADOS_SER_COMPLEMENTO", ATUALIZAR_DADOS_SER_COMPLEMENTO);
                campoSist.put("ATUALIZAR_DADOS_SER_NUMERO", ATUALIZAR_DADOS_SER_NUMERO);
                campoSist.put("ATUALIZAR_DADOS_SER_CEP", ATUALIZAR_DADOS_SER_CEP);
                campoSist.put("ATUALIZAR_DADOS_SER_UF", ATUALIZAR_DADOS_SER_UF);

                String EDITAR_DADOS_SER_EMAIL = ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_EMAIL, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_EMAIL, responsavel) ? "S" : "N";
                EDITAR_DADOS_SER_EMAIL = !ParamSist.paramEquals(CodedValues.TPC_BLOQUEIA_EDICAO_EMAIL_SERVIDOR_CAD_FOLHA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema()) ? "N" : EDITAR_DADOS_SER_EMAIL;
                final String EDITAR_DADOS_SER_TELEFONE = ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_TELEFONE, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_TELEFONE, responsavel) ? "S" : "N";
                final String EDITAR_DADOS_SER_CELULAR = ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_CELULAR, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_CELULAR, responsavel) ? "S" : "N";

                final String EDITAR_DADOS_SER_LOGRADOURO = ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_LOGRADOURO, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_LOGRADOURO, responsavel) ? "S" : "N";
                final String EDITAR_DADOS_SER_CIDADE = ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_CIDADE, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_CIDADE, responsavel) ? "S" : "N";
                final String EDITAR_DADOS_SER_BAIRRO = ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_BAIRRO, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_BAIRRO, responsavel) ? "S" : "N";
                final String EDITAR_DADOS_SER_COMPLEMENTO = ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_COMPLEMENTO, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_COMPLEMENTO, responsavel) ? "S" : "N";
                final String EDITAR_DADOS_SER_NUMERO = ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_NRO, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_NRO, responsavel) ? "S" : "N";
                final String EDITAR_DADOS_SER_CEP = ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_CEP, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_CEP, responsavel) ? "S" : "N";
                final String EDITAR_DADOS_SER_UF = ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_UF, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_UF, responsavel) ? "S" : "N";

                campoSist.put("EDITAR_DADOS_SER_EMAIL", EDITAR_DADOS_SER_EMAIL);
                campoSist.put("EDITAR_DADOS_SER_TELEFONE", EDITAR_DADOS_SER_TELEFONE);
                campoSist.put("EDITAR_DADOS_SER_CELULAR", EDITAR_DADOS_SER_CELULAR);

                campoSist.put("EDITAR_DADOS_SER_LOGRADOURO", EDITAR_DADOS_SER_LOGRADOURO);
                campoSist.put("EDITAR_DADOS_SER_CIDADE", EDITAR_DADOS_SER_CIDADE);
                campoSist.put("EDITAR_DADOS_SER_BAIRRO", EDITAR_DADOS_SER_BAIRRO);
                campoSist.put("EDITAR_DADOS_SER_COMPLEMENTO", EDITAR_DADOS_SER_COMPLEMENTO);
                campoSist.put("EDITAR_DADOS_SER_NUMERO", EDITAR_DADOS_SER_NUMERO);
                campoSist.put("EDITAR_DADOS_SER_CEP", EDITAR_DADOS_SER_CEP);
                campoSist.put("EDITAR_DADOS_SER_UF", EDITAR_DADOS_SER_UF);

                final String CONFIRMACAO_DADOS_SERVIDOR_EMAIL = ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_EMAIL, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_EMAIL, responsavel) ? "S" : "N";
                final String CONFIRMACAO_DADOS_SERVIDOR_TELEFONE = ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel) ? "S" : "N";
                final String CONFIRMACAO_DADOS_SERVIDOR_CELULAR = ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CELULAR, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CELULAR, responsavel) ? "S" : "N";

                final String CONFIRMACAO_DADOS_SERVIDOR_LOGRADOURO = ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_LOGRADOURO, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_LOGRADOURO, responsavel) ? "S" : "N";
                final String CONFIRMACAO_DADOS_SERVIDOR_CIDADE = ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CIDADE, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CIDADE, responsavel) ? "S" : "N";
                final String CONFIRMACAO_DADOS_SERVIDOR_BAIRRO = ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_BAIRRO, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_BAIRRO, responsavel) ? "S" : "N";
                final String CONFIRMACAO_DADOS_SERVIDOR_COMPLEMENTO = ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_COMPLEMENTO, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_COMPLEMENTO, responsavel) ? "S" : "N";
                final String CONFIRMACAO_DADOS_SERVIDOR_NRO = ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO, responsavel) ? "S" : "N";
                final String CONFIRMACAO_DADOS_SERVIDOR_CEP = ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CEP, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CEP, responsavel) ? "S" : "N";
                final String CONFIRMACAO_DADOS_SERVIDOR_UF = ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_UF, responsavel) ? "O" : ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_UF, responsavel) ? "S" : "N";

                campoSist.put("CONFIRMACAO_DADOS_SERVIDOR_EMAIL", CONFIRMACAO_DADOS_SERVIDOR_EMAIL);
                campoSist.put("CONFIRMACAO_DADOS_SERVIDOR_TELEFONE", CONFIRMACAO_DADOS_SERVIDOR_TELEFONE);
                campoSist.put("CONFIRMACAO_DADOS_SERVIDOR_CELULAR", CONFIRMACAO_DADOS_SERVIDOR_CELULAR);

                campoSist.put("CONFIRMACAO_DADOS_SERVIDOR_LOGRADOURO", CONFIRMACAO_DADOS_SERVIDOR_LOGRADOURO);
                campoSist.put("CONFIRMACAO_DADOS_SERVIDOR_CIDADE", CONFIRMACAO_DADOS_SERVIDOR_CIDADE);
                campoSist.put("CONFIRMACAO_DADOS_SERVIDOR_BAIRRO", CONFIRMACAO_DADOS_SERVIDOR_BAIRRO);
                campoSist.put("CONFIRMACAO_DADOS_SERVIDOR_COMPLEMENTO", CONFIRMACAO_DADOS_SERVIDOR_COMPLEMENTO);
                campoSist.put("CONFIRMACAO_DADOS_SERVIDOR_NRO", CONFIRMACAO_DADOS_SERVIDOR_NRO);
                campoSist.put("CONFIRMACAO_DADOS_SERVIDOR_CEP", CONFIRMACAO_DADOS_SERVIDOR_CEP);
                campoSist.put("CONFIRMACAO_DADOS_SERVIDOR_UF", CONFIRMACAO_DADOS_SERVIDOR_UF);

                /*
                 *  @deprecated TODO Remover esta lógica de verificação de dados cadastrais depois
                 *  que a versão ionic do aplicativo mobile for descontinuado, manter apenas a lista listaOcorrenciaEmailIncorretoServidor
                 */
                campoSist.put("TPC_EXIGE_CAD_EMAIL_SER_PRIMEIRO_ACESSO_SIST", String.valueOf(ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_ATUALIZACAO_DADOS_SER_PRIMEIRO_ACESSO, responsavel) && ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_CAD_EMAIL_SER_PRIMEIRO_ACESSO_SIST, responsavel)));
                campoSist.put("TPC_EXIGE_CAD_TELEFONE_SER_PRIMEIRO_ACESSO_SIST", String.valueOf(ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_ATUALIZACAO_DADOS_SER_PRIMEIRO_ACESSO, responsavel) && ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_CAD_TELEFONE_SER_PRIMEIRO_ACESSO_SIST, responsavel)));

                // inicio - DESENV-8259
                final String tocCodigo = CodedValues.TOC_DIVERGENCIA_CADASTRO_EMAIL_SERVIDOR;
                final List<TransferObject> listaOcorrenciaEmailIncorretoServidor = servidorController.lstDataOcorrenciaServidor(rseLogado.serCodigo, tocCodigo, responsavel);

                final int qtdeDiasQueUsuarioPodeUsarSistemaSemValidarEmail = TextHelper.isNum(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_ACESSO_SISTEMA_SERVIDOR_SEM_VALIDACAO_EMAIL, responsavel)) ? Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_ACESSO_SISTEMA_SERVIDOR_SEM_VALIDACAO_EMAIL, responsavel).toString()) : 0;
                boolean servidorDentroDoPrazoSemValidacaoEmail = !ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_CAD_EMAIL_SER_PRIMEIRO_ACESSO_SIST, responsavel) || (qtdeDiasQueUsuarioPodeUsarSistemaSemValidarEmail > 0);

                if (!listaOcorrenciaEmailIncorretoServidor.isEmpty()) {
                    final Date dataOcorrencia = (Date) listaOcorrenciaEmailIncorretoServidor.get(0).getAttribute(Columns.OCS_DATA);
                    servidorDentroDoPrazoSemValidacaoEmail = servidorController.validaServidorDentroPrazoAcessoSistemaSemValidacaoEmail(rseLogado.serCodigo, dataOcorrencia, responsavel);
                }

                /*
                 *  @deprecated TODO Remover esta lógica de verificação de dados cadastrais depois
                 *  que a versão ionic do aplicativo mobile for descontinuado, manter apenas a lista listaOcorrenciaEmailIncorretoServidor
                 */
                campoSist.put("servidorDentroDoPrazoSemValidacaoEmail", String.valueOf(servidorDentroDoPrazoSemValidacaoEmail));
                //FIM - DESENV-8259

                paramSist.put("TPC_EXIGE_CAD_EMAIL_SER_PRIMEIRO_ACESSO_SIST", String.valueOf(ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_ATUALIZACAO_DADOS_SER_PRIMEIRO_ACESSO, responsavel) && ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_CAD_EMAIL_SER_PRIMEIRO_ACESSO_SIST, responsavel)));
                paramSist.put("TPC_EXIGE_CAD_TELEFONE_SER_PRIMEIRO_ACESSO_SIST", String.valueOf(ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_ATUALIZACAO_DADOS_SER_PRIMEIRO_ACESSO, responsavel) && ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_CAD_TELEFONE_SER_PRIMEIRO_ACESSO_SIST, responsavel)));
                paramSist.put("TPC_EXIGE_ATUALIZACAO_DADOS_SER_PRIMEIRO_ACESSO", String.valueOf(ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_ATUALIZACAO_DADOS_SER_PRIMEIRO_ACESSO, responsavel)));

                //DESENV-15999
                paramSist.put("TPC_HABILITAR_INTEGRACAO_SALARYPAY", String.valueOf(ParamSist.getBoolParamSist(CodedValues.TPC_HABILITAR_INTEGRACAO_SALARYPAY, responsavel)));
                paramSist.put("TPC_INTEGRAR_SALARYPAY_A_CIELO", String.valueOf(ParamSist.getBoolParamSist(CodedValues.TPC_INTEGRAR_SALARYPAY_A_CIELO, responsavel)));
                paramSist.put("TPC_VALIDAR_KYC_FACESWEB_INTEGRACAO_SALARYPAY", String.valueOf(ParamSist.getBoolParamSist(CodedValues.TPC_VALIDAR_KYC_FACESWEB_INTEGRACAO_SALARYPAY, responsavel)));
                paramSist.put("TPC_VALIDAR_KYC_BANK_AS_SERVICE_INTEGRACAO_SALARYPAY", String.valueOf(ParamSist.getBoolParamSist(CodedValues.TPC_VALIDAR_KYC_BANK_AS_SERVICE_INTEGRACAO_SALARYPAY, responsavel)));
                paramSist.put("TPC_APURACAO_AUTOMATICA_CADASTRO_FACESWEB", String.valueOf(ParamSist.getBoolParamSist(CodedValues.TPC_APURACAO_AUTOMATICA_CADASTRO_FACESWEB, responsavel)));

                paramSist.put("redirecionarAlteracaoDadosEmail", String.valueOf(redirecionarAlteracaoDadosEmail(listaOcorrenciaEmailIncorretoServidor, retornoAux, responsavel)));
                paramSist.put("mostrarBotaoCancelar", String.valueOf(mostrarBotaoCancelar(listaOcorrenciaEmailIncorretoServidor)));

                campoSist.put("exibeFaq", "true");

                // DESENV-18007
                paramSist.put("TPC_TAM_MAX_ARQ_ANEXO_CONTRATO_ASSINATURA_DIGITAL", String.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_ARQ_ANEXO_CONTRATO_ASSINATURA_DIGITAL, responsavel)));
                campoSist.put("editarAnexoConsignacao", "true");
                campoSist.put("extensoesPermitidasAnexoContrato", String.join(",", UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_CONTRATO));

                //DESENV-18004
                paramSist.put("TPC_IMPRIMIR_BOLETO_MOBILE", String.valueOf(ParamSist.getBoolParamSist(CodedValues.TPC_IMPRIMIR_BOLETO_MOBILE, responsavel)));

                // DESENV-18436
                paramSist.put("TPC_LISTAGEM_ATALHOS_HOME", String.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_LISTAGEM_ATALHOS_HOME, responsavel)));

                //DESENV-23941
                paramSist.put("TPC_LINK_ACESSO_SISTEMA", (String) ParamSist.getInstance().getParam(CodedValues.TPC_LINK_ACESSO_SISTEMA, responsavel));

                // DESENV-20174
                paramSist.put("TPC_EXIGE_RECONHECIMENTO_FACIL_SOLICITACAO_SERVIDOR", String.valueOf(ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_RECONHECIMENTO_FACIL_SOLICITACAO_SERVIDOR, responsavel) && ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_RECONHECIMENTO_FACIL_SOLICITACAO_SERVIDOR, responsavel)));

                // DESENV-20239
                paramSist.put("TPC_URL_SERVICO_FACES_WEB", (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_SERVICO_FACES_WEB, responsavel));
                paramSist.put("TPC_API_KEY_FACES_WEB", (String) ParamSist.getInstance().getParam(CodedValues.TPC_API_KEY_FACES_WEB, responsavel));

                // DESENV-20259
                paramSist.put("TPC_METODO_CALCULO_SIMULACAO", (String) ParamSist.getInstance().getParam(CodedValues.TPC_METODO_CALCULO_SIMULACAO, responsavel));

                // DESENV-20253: se o param de serviço 251 não estiver configurado como S para nenhum serviço e csa, então este parâmetro
                //               diz ao cliente REST que não é necessário fazer chamada remota de verificação de exigência de anexo de documentação em uma solicitação
                campoSist.put("checkAssinaturaDigitalConfigurado", String.valueOf(!parametroController.selectParamSvcCsa(new ArrayList<>(), null,
                        List.of(CodedValues.TPS_EXIGE_ASSINATURA_DIGITAL_SOLICITACOES), false, responsavel).stream()
                        .filter(param -> CodedValues.PSC_BOOLEANO_SIM.equals(param.getAttribute(Columns.PSC_VLR)) ||
                                CodedValues.PSC_BOOLEANO_SIM.equals(param.getAttribute(Columns.PSC_VLR_REF))).toList().isEmpty()));

                // DESENV-20334
                paramSist.put("TPC_SIMULADOR_COM_CET_TIPO_OPERACAO", String.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_SIMULADOR_COM_CET_TIPO_OPERACAO, responsavel)));
                paramSist.put("TPC_TEM_CET", String.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_TEM_CET, responsavel)));

                // DESENV-21006
                paramSist.put("TPC_RECONHECIMENTO_FACIAL_ACESSO_SERVIDOR", String.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_RECONHECIMENTO_FACIAL_ACESSO_SERVIDOR, responsavel)));

                // DESENV-21022
                paramSist.put("TPC_SOLICITA_SENHA_AUTORIZACAO_OPE_REALIZADAS_SER", String.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_SOLICITA_SENHA_AUTORIZACAO_OPE_REALIZADAS_SER, responsavel)));
                paramSist.put("TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR", String.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, responsavel)));

                // DESENV-21551
                paramSist.put("TPC_DIAS_VALIDADE_AUTORIZACAO_SERVIDOR_CON_MAR_POR_COD", String.valueOf(ParamSist.getIntParamSist(CodedValues.TPC_DIAS_VALIDADE_AUTORIZACAO_SERVIDOR_CON_MAR_POR_COD, 0, responsavel)));

                // DESENV-22220
                paramSist.put("TPC_PERMITE_PORTABILIDADE_CARTAO", String.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_PERMITE_PORTABILIDADE_CARTAO, responsavel)));

                //DESENV-22641
                paramSist.put("TPC_EXIBIR_NOTIFICACAO_EXPIRACAO_SENHA_SER", String.valueOf(ParamSist.getIntParamSist(CodedValues.TPC_QUANTIDADE_DIAS_NOTIFICACAO_EXPIRACAO_SENHA_SER, 0, usuAcesso)));

                //DESENV-23113
            	paramSist.put("TPC_CSA_PODE_CADASTRAR_CET_MININO_E_MAXIMO", String.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_CSA_PODE_CADASTRAR_CET_MININO_E_MAXIMO, responsavel)));
                
                final TransferObject usuarioAutentica = usuarioController.findTipoUsuarioByLogin(usuAcesso.getUsuLogin(), responsavel);
                final String dataExpiracaoSenha = usuarioAutentica.getAttribute(Columns.USU_DATA_EXP_SENHA).toString();
                campoSist.put("dataExpiracaoSenha", dataExpiracaoSenha);

                retorno.put("campoSist", campoSist);
                retorno.put("paramSist", paramSist);

                //DESENV-23948
                final MensagemController mensagemController = ApplicationContextProvider.getApplicationContext().getBean(MensagemController.class);
                final List<TransferObject> mensagens = mensagemController.pesquisaMensagem(responsavel, 0, true);
                retorno.put("mensagens", mensagemController.parseToResponse(mensagens));

                // Grava log de login sucesso
                final LogDelegate log = new LogDelegate(responsavel, Log.SISTEMA, Log.LOGIN, Log.LOG_LOGIN_SUCESSO);
                log.add(ApplicationResourcesHelper.getMessage("mensagem.log.user.agent.arg0", (AcessoSistema) null, request.getHeader("user-agent")));
                log.write();

            } else {
                delay = IpWatchdog.bloqueiaIp(ip);
                if (delay > 0) {
                    final ResponseRestRequest responseError = new ResponseRestRequest();
                    responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.rest.esperar.segundos", null, String.valueOf(delay));
                    return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
                }

                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", null);

                final LogDelegate log = new LogDelegate(usuAcesso, Log.SISTEMA, Log.LOGIN, Log.LOG_LOGIN_ERRO);
                log.add(ApplicationResourcesHelper.getMessage("mensagem.log.id.arg0", (AcessoSistema) null, idClean));
                log.add(ApplicationResourcesHelper.getMessage("mensagem.log.erro.arg0", (AcessoSistema) null, responseError.mensagem));
                log.add(ApplicationResourcesHelper.getMessage("mensagem.log.user.agent.arg0", (AcessoSistema) null, request.getHeader("user-agent")));
                log.write();

                return Response.status(Response.Status.UNAUTHORIZED).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
            }
        } catch (final ServidorControllerException e1) {
            LOG.error(e1.getMessage(), e1);
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = e1.getMessage();
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        } catch (final ViewHelperException e) {
            LOG.error(e.getMessage(), e);
            final ResponseRestRequest responseError = new ResponseRestRequest();
            final Throwable ex = (e.getCause() != null) && (e.getCause().getMessage() != null) ? e.getCause() : e;
            responseError.mensagem = ex.getMessage();
            responseError.senhaExpirada = e.isSenhaExpirada();
            // Vai entrar nesta exceção para vários casos que não é de senha inválida
            if (e.getMessage().equalsIgnoreCase(ApplicationResourcesHelper.getMessage("mensagem.usuarioSenhaInvalidos", null))) {
                delay = IpWatchdog.bloqueiaIp(ip);
                if (delay > 0) {
                    responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.rest.esperar.segundos", null, String.valueOf(delay));
                    return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
                }

                // vai entrar nessa exceção em casos de usuário ou senha inválidos. Então é código 401.
                return Response.status(Response.Status.UNAUTHORIZED).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
            }
            // todo outro erro é conflito 405.
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }
        IpWatchdog.desbloqueiaIp(ip);

        return Response.status(Response.Status.OK).entity(retorno).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
    }

    /**
    * Verifica se o usuário deve atualizar os dados de email
    */
    private boolean redirecionarAlteracaoDadosEmail(List<TransferObject> listaOcorrenciaEmailIncorretoServidor, Map<String, Object> retornoAux, AcessoSistema responsavel) throws ServidorControllerException {
        final boolean exigeCadastramentoEmail = ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_ATUALIZACAO_DADOS_SER_PRIMEIRO_ACESSO, responsavel) && ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_CAD_EMAIL_SER_PRIMEIRO_ACESSO_SIST, responsavel);

        if (exigeCadastramentoEmail) {
            final ServidorRestResponse ser = (ServidorRestResponse) retornoAux.get("rseLogado");

            if (TextHelper.isNull(ser.dataValidacaoEmail) || TextHelper.isNull(ser.dataUltimoAcesso)) {
                final int qtdeDiasQueUsuarioPodeUsarSistemaSemValidarEmail = TextHelper.isNum(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_ACESSO_SISTEMA_SERVIDOR_SEM_VALIDACAO_EMAIL, responsavel)) ? Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_ACESSO_SISTEMA_SERVIDOR_SEM_VALIDACAO_EMAIL, responsavel).toString()) : 0;

                if ((qtdeDiasQueUsuarioPodeUsarSistemaSemValidarEmail <= 0) || listaOcorrenciaEmailIncorretoServidor.isEmpty()) {
                    return true;
                } else {
                    final Date dataOcorrencia = (Date) listaOcorrenciaEmailIncorretoServidor.get(0).getAttribute(Columns.OCS_DATA);
                    final boolean servidorDentroDoPrazoSemValidacaoEmail = servidorController.validaServidorDentroPrazoAcessoSistemaSemValidacaoEmail(ser.serCodigo, dataOcorrencia, responsavel);
                    // Se não estiver dentro do prazo, é para redirecionar
                    return !servidorDentroDoPrazoSemValidacaoEmail;
                }
            } else {
                return false;
            }

        } else {
            return false;
        }
    }

    /**
     * Verifica se o botão cancelar pode aparecer ou não para cadastro de email
     */
    private boolean mostrarBotaoCancelar(List<TransferObject> listaOcorrenciaEmailIncorretoServidor) {
        return (listaOcorrenciaEmailIncorretoServidor == null) || listaOcorrenciaEmailIncorretoServidor.isEmpty();
    }

    @POST
    @Secured
    @Path("/logout")
    public Response logout() {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        responsavel.setCanal(CanalEnum.REST);
        final String usuCodigo = responsavel.getUsuCodigo();

        try {
            usuarioController.deleteUsuarioChaveSessao(usuCodigo);
            new LogDelegate(responsavel, Log.SISTEMA, Log.LOGOUT, Log.LOG_LOGOUT).write();
        } catch (final UsuarioControllerException e) {
            LOG.error(e.getMessage(), e);
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = e.getMessage();
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            //a principio não responde erro ao Mobile pois o logout foi efetuado com sucesso, porém o logo não foi registrado
        }

        return Response.status(Response.Status.OK).entity(new ResponseRestRequest()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
    }

    @POST
    @Secured
    @Path("/consultarMargem")
    public Response consultarMargem(ConsultarMargemRestRequest consultarMargem) {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

        final boolean recuperaDataFimRetorno = !TextHelper.isNull(consultarMargem.dataFimRetorno) && "true".equals(consultarMargem.dataFimRetorno);

        final List<String> rseCodigo = new ArrayList<>();
        final List<String> dataFimRetorno = new ArrayList<>();

        if (responsavel.isCse()) {
            if (!responsavel.temPermissao(CodedValues.FUN_CONS_MARGEM)) {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.usuarioNaoTemPermissao", null);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
            }

            try {
                if (!TextHelper.isNull(consultarMargem.rseCodigo)) {
                    // Busca o registro servidor pelo rseCodigo
                    final TransferObject lstRegistroServidor = servidorController.findRegistroServidor(consultarMargem.rseCodigo, AcessoSistema.getAcessoUsuarioSistema());
                    if (TextHelper.isNull(lstRegistroServidor)) {
                        final String msgError = ApplicationResourcesHelper.getMessage("mensagem.nenhumServidorEncontrado", null);
                        LOG.error(msgError);
                        final ResponseRestRequest responseError = new ResponseRestRequest();
                        responseError.mensagem = msgError;
                        return Response.status(Response.Status.CONFLICT).entity(List.of(responseError)).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
                    } else {
                        rseCodigo.add(consultarMargem.rseCodigo);

                        if (recuperaDataFimRetorno) {
                            final Date dataFimRetornoDate = PeriodoHelper.getInstance().getDataFimPeriodoAtual((String) lstRegistroServidor.getAttribute(Columns.ORG_CODIGO), responsavel);

                            if (dataFimRetornoDate != null) {
                                dataFimRetorno.add(DateHelper.toISOStringWithLocalTimeZone(PeriodoHelper.getInstance().getDataFimPeriodoAtual((String) lstRegistroServidor.getAttribute(Columns.ORG_CODIGO), responsavel)));
                            }
                        }
                    }
                } else if (!TextHelper.isNull(consultarMargem.serCpf)) {
                    // Busca o registro servidor pelo cpf
                    final List<TransferObject> lstRegistroServidor = servidorController.lstRegistroServidorPorCpf(consultarMargem.serCpf, null, AcessoSistema.getAcessoUsuarioSistema());
                    if (lstRegistroServidor.isEmpty()) {
                        final String msgError = ApplicationResourcesHelper.getMessage("mensagem.nenhumServidorEncontrado", null);
                        LOG.error(msgError);
                        final ResponseRestRequest responseError = new ResponseRestRequest();
                        responseError.mensagem = msgError;
                        return Response.status(Response.Status.CONFLICT).entity(List.of(responseError)).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
                    } else {
                        for (final TransferObject lst : lstRegistroServidor) {
                            rseCodigo.add(lst.getAttribute(Columns.RSE_CODIGO).toString()); //DESENV-20348: para MVP do SalaryFits será assumido que apenas um rse código está mapeado por CPF.

                            if (recuperaDataFimRetorno) {
                                final Date dataFimRetornoDate = PeriodoHelper.getInstance().getDataFimPeriodoAtual((String) lst.getAttribute(Columns.ORG_CODIGO), responsavel);

                                if (dataFimRetornoDate != null) {
                                    dataFimRetorno.add(DateHelper.toISOStringWithLocalTimeZone(PeriodoHelper.getInstance().getDataFimPeriodoAtual((String) lst.getAttribute(Columns.ORG_CODIGO), responsavel)));
                                }
                            }
                        }
                    }
                } else if (!TextHelper.isNull(consultarMargem.matricula)) {
                    // Busca o registro servidor pela matricula
                    final List<TransferObject> lstRegistroServidor = servidorController.findRegistroServidoresByMatriculas(List.of(consultarMargem.matricula), AcessoSistema.getAcessoUsuarioSistema());
                    if (lstRegistroServidor.isEmpty()) {
                        final String msgError = ApplicationResourcesHelper.getMessage("mensagem.nenhumServidorEncontrado", null);
                        LOG.error(msgError);
                        final ResponseRestRequest responseError = new ResponseRestRequest();
                        responseError.mensagem = msgError;
                        return Response.status(Response.Status.CONFLICT).entity(List.of(responseError)).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
                    } else {
                        for (final TransferObject lst : lstRegistroServidor) {
                            rseCodigo.add(lst.getAttribute(Columns.RSE_CODIGO).toString()); //DESENV-20348: para MVP do SalaryFits será assumido que apenas um rse código está mapeado por CPF.

                            if (recuperaDataFimRetorno) {
                                final Date dataFimRetornoDate = PeriodoHelper.getInstance().getDataFimPeriodoAtual((String) lst.getAttribute(Columns.ORG_CODIGO), responsavel);

                                if (dataFimRetornoDate != null) {
                                    dataFimRetorno.add(DateHelper.toISOStringWithLocalTimeZone(PeriodoHelper.getInstance().getDataFimPeriodoAtual((String) lst.getAttribute(Columns.ORG_CODIGO), responsavel)));
                                }
                            }
                        }
                    }
                } else {
                    final ResponseRestRequest responseError = new ResponseRestRequest();
                    responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
                    LOG.error(responseError.mensagem);
                    return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
                }
            } catch (ServidorControllerException | PeriodoException e) {
                LOG.error(e.getMessage(), e);
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = e.getMessage();
                return Response.status(Response.Status.CONFLICT).entity(List.of(responseError)).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
            }
        } else if (responsavel.isSup()) {
            try {
                if (!TextHelper.isNull(consultarMargem.serCpf)) {
                    // Busca o registro servidor pelo cpf
                    final List<TransferObject> lstRegistroServidor = servidorController.lstRegistroServidorPorCpf(consultarMargem.serCpf, null, AcessoSistema.getAcessoUsuarioSistema());
                    if (lstRegistroServidor.isEmpty()) {
                        final String msgError = ApplicationResourcesHelper.getMessage("mensagem.nenhumServidorEncontrado", null);
                        LOG.error(msgError);
                        final ResponseRestRequest responseError = new ResponseRestRequest();
                        responseError.mensagem = msgError;
                        return Response.status(Response.Status.CONFLICT).entity(List.of(responseError)).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
                    }

                    final TransferObject rseTO = lstRegistroServidor.get(0);
                    rseCodigo.add(rseTO.getAttribute(Columns.RSE_CODIGO).toString()); //DESENV-20348: para MVP do SalaryFits será assumido que apenas um rse código está mapeado por CPF.

                    if (recuperaDataFimRetorno) {
                        final Date dataFimRetornoDate = PeriodoHelper.getInstance().getDataFimPeriodoAtual((String) rseTO.getAttribute(Columns.ORG_CODIGO), responsavel);

                        if (dataFimRetornoDate != null) {
                            dataFimRetorno.add(DateHelper.toISOStringWithLocalTimeZone(PeriodoHelper.getInstance().getDataFimPeriodoAtual((String) rseTO.getAttribute(Columns.ORG_CODIGO), responsavel)));
                        }
                    }
                } else {
                    final String msgError = ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.cpf", null);
                    LOG.error(msgError);
                    final ResponseRestRequest responseError = new ResponseRestRequest();
                    responseError.mensagem = msgError;
                    return Response.status(Response.Status.CONFLICT).entity(List.of(responseError)).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
                }
            } catch (ServidorControllerException | PeriodoException e) {
                LOG.error(e.getMessage(), e);
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = e.getMessage();
                return Response.status(Response.Status.CONFLICT).entity(List.of(responseError)).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
            }
        } else {
            rseCodigo.add(responsavel.getRseCodigo());
        }

        final HashMap<String, List<Map<String, Object>>> result = new HashMap<>();
        if (rseCodigo.size() > 1) {
            for (int i = 0; i < rseCodigo.size(); i++) {
                List<MargemTO> margens;
                try {
                    margens = consultarMargemController.consultarMargem(rseCodigo.get(i), null, null, null, true, true, null, false, consultarMargem != null ? consultarMargem.nseCodigo : null, responsavel);
                } catch (final ServidorControllerException e) {
                    LOG.error(e.getMessage(), e);
                    final ResponseRestRequest responseError = new ResponseRestRequest();
                    responseError.mensagem = e.getMessage();
                    return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
                }

                final List<String> filter = Arrays.asList("mar_codigo", "mar_codigo_pai", "mar_descricao", "mar_sequencia", "mar_exibe_ser", "mar_tipo_vlr", "mrs_margem", "mrs_margem_usada", "mrs_margem_rest");
                margens.removeIf(margem -> TextHelper.isNull(margem.getMarDescricao()));

                final List<Map<String, Object>> margensList = transformTOs(margens, filter);

                //DESENV-22003: incluíndo data fim de retorno nos resultados
                if (recuperaDataFimRetorno) {
                    for (final Map<String, Object> margemTO : margensList) {
                        margemTO.put("data_fim_retorno", dataFimRetorno.get(i));
                    }
                }

                // Consulta em quais naturezas de servico, incide a margem
                if ((consultarMargem == null) || (consultarMargem.nseCodigo == null) || consultarMargem.nseCodigo.isEmpty()) {
                    final Object vlrParamSist = ParamSist.getInstance().getParam(CodedValues.TPC_VLR_PADRAO_MINIMO_CONTRATO, responsavel);

                    for (final Map<String, Object> margemTO : margensList) {
                        if (margemTO.get("mar_codigo_pai") == null) {
                            try {
                                final List<TransferObject> lista = consultarMargemController.lstMargemNatureza(margemTO.get("mar_codigo").toString(), responsavel);
                                final List<String> filter1 = Arrays.asList("nse_codigo", "nse_descricao");
                                final List<Map<String, Object>> naturezas = transformTOs(lista, filter1);
                                margemTO.put("tb_natureza_servico", naturezas);

                                //passando valor mínimo padrão
                                if (!TextHelper.isNull(vlrParamSist)) {
                                    margemTO.put("TPC_VLR_PADRAO_MINIMO_CONTRATO", Double.parseDouble((String) vlrParamSist));
                                } else {
                                    margemTO.put("TPC_VLR_PADRAO_MINIMO_CONTRATO", 0.0);
                                }
                            } catch (final ServidorControllerException e) {
                                LOG.error(e.getMessage(), e);
                                final ResponseRestRequest responseError = new ResponseRestRequest();
                                responseError.mensagem = e.getMessage();
                                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
                            }
                        }
                    }
                }
                result.put(rseCodigo.get(i), margensList);
            }
            return Response.status(Response.Status.OK).entity(result).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        } else {
            List<MargemTO> margens;
            try {
                margens = consultarMargemController.consultarMargem(rseCodigo.get(0), null, null, null, true, true, null, false, consultarMargem != null ? consultarMargem.nseCodigo : null, responsavel);
            } catch (final ServidorControllerException e) {
                LOG.error(e.getMessage(), e);
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = e.getMessage();
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
            }

            final List<String> filter = Arrays.asList("mar_codigo", "mar_codigo_pai", "mar_descricao", "mar_sequencia", "mar_exibe_ser", "mar_tipo_vlr", "mrs_margem", "mrs_margem_usada", "mrs_margem_rest");
            margens.removeIf(margem -> TextHelper.isNull(margem.getMarDescricao()));

            final List<Map<String, Object>> margensList = transformTOs(margens, filter);

            if (recuperaDataFimRetorno) {
                for (final Map<String, Object> margemTO : margensList) {
                    margemTO.put("data_fim_retorno", dataFimRetorno);
                }
            }

            if ((consultarMargem == null) || (consultarMargem.nseCodigo == null) || consultarMargem.nseCodigo.isEmpty()) {
                final Object vlrParamSist = ParamSist.getInstance().getParam(CodedValues.TPC_VLR_PADRAO_MINIMO_CONTRATO, responsavel);

                for (final Map<String, Object> margemTO : margensList) {
                    if (margemTO.get("mar_codigo_pai") == null) {
                        try {
                            final List<TransferObject> lista = consultarMargemController.lstMargemNatureza(margemTO.get("mar_codigo").toString(), responsavel);
                            final List<String> filter1 = Arrays.asList("nse_codigo", "nse_descricao");
                            final List<Map<String, Object>> naturezas = transformTOs(lista, filter1);
                            margemTO.put("tb_natureza_servico", naturezas);

                            //passando valor mínimo padrão
                            if (!TextHelper.isNull(vlrParamSist)) {
                                margemTO.put("TPC_VLR_PADRAO_MINIMO_CONTRATO", Double.parseDouble((String) vlrParamSist));
                            } else {
                                margemTO.put("TPC_VLR_PADRAO_MINIMO_CONTRATO", 0.0);
                            }
                        } catch (final ServidorControllerException e) {
                            LOG.error(e.getMessage(), e);
                            final ResponseRestRequest responseError = new ResponseRestRequest();
                            responseError.mensagem = e.getMessage();
                            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
                        }
                    }
                }
            }
            return Response.status(Response.Status.OK).entity(margensList).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }
    }

    private Map<String, Object> autenticarUsuario(String senhaClean, List<TransferObject> lstRegistroServidores, String id, String rseMatricula, boolean senhaApp, boolean permiteAguardAprovacaoCadastro, boolean autenticarUsuario, AcessoSistema responsavel) throws ZetraException {
    	if (lstRegistroServidores.size() == 1) {
    	            final TransferObject rseTO = lstRegistroServidores.get(0);
    	            final ServidorRestResponse response = autenticaRegistroServidor(senhaClean, id, rseTO, senhaApp, permiteAguardAprovacaoCadastro, autenticarUsuario, responsavel);
    	            if (response != null) {
    	                final Map<String, Object> mapRetorno = new HashMap<>();
    	                mapRetorno.put("rseLogado", response);
    	                return mapRetorno;
    	            } else {
    	                throw new ViewHelperException("mensagem.usuarioSenhaInvalidos", responsavel);
    	            }
    	        } else {
    	            boolean autenticou = false;
    	            ZetraException erroOriginal = null;
    	            final List<ServidorRestResponse> lstServidores = new ArrayList<>();

    	            ServidorRestResponse response = null;
    	            String usuCodigoUsado = "";
    	            for (final TransferObject rseOrdenado : lstRegistroServidores) {
    	                try {
    	                    if (!TextHelper.isNull(rseMatricula) && !rseMatricula.equals(rseOrdenado.getAttribute(Columns.RSE_MATRICULA))) {
    	                        continue;
    	                    }
    	                    if (!autenticou) {
    	                        try {
    	                            response = autenticaRegistroServidor(senhaClean, id, rseOrdenado, senhaApp, permiteAguardAprovacaoCadastro, autenticarUsuario, responsavel);

    	                            //foi a 19203 mudou
    	                            if(!TextHelper.isNull(response)) {
	                                   usuCodigoUsado = response.usuCodigo;
    	                            }
    	                        } catch (final ZetraException e) {
    	                            LOG.error(e.getMessage(), e);
    	                            if ((erroOriginal == null) || !"mensagem.senha.servidor.consulta.invalida".equals(e.getMessageKey())) {
    	                                erroOriginal = e;
    	                            }
    	                        }
    	                    }
    	                    if (!autenticou && (response != null)) {
    	                        autenticou = true;
    	                    } else {
    	                        final ServidorRestResponse serResponse = preencheRseResponseBasico(rseOrdenado);
    	                        final CustomTransferObject usuario = pesquisarServidorController.buscaUsuarioServidor(null, null, (String) rseOrdenado.getAttribute(Columns.RSE_MATRICULA), (String) rseOrdenado.getAttribute(Columns.ORG_IDENTIFICADOR), (String) rseOrdenado.getAttribute(Columns.EST_IDENTIFICADOR), responsavel);
    	                        final String usuCodigoAtual = (String) usuario.getAttribute(Columns.USU_CODIGO);
    	                        final String usuLogin = (String) rseOrdenado.getAttribute(Columns.USU_LOGIN);

    	                        if(TextHelper.isNull(usuLogin) || usuCodigoAtual.equals(usuCodigoUsado)) {
    	                        	continue;
	                        	}

    	                        serResponse.token = usuarioController.gerarChaveSessaoUsuario((String) usuario.getAttribute(Columns.USU_CODIGO), AcessoSistema.getAcessoUsuarioSistema());
    	                        serResponse.statusUsuario = (String) usuario.getAttribute(Columns.USU_STU_CODIGO);

    	                        final Map<String, EnderecoFuncaoTransferObject> funcoes = usuarioController.selectFuncoes((String) usuario.getAttribute(Columns.USU_CODIGO), (String) usuario.getAttribute(Columns.SER_CODIGO), AcessoSistema.ENTIDADE_SER, responsavel);
    	                        serResponse.permissoes = new ArrayList<>(funcoes.keySet());

    	                        final Collection<ArquivoUsuario> lstArqs = usuarioController.findArquivoUsuario((String) usuario.getAttribute(Columns.USU_CODIGO), TipoArquivoEnum.ARQUIVO_IMAGEM_PERFIL_USUARIO.getCodigo(), AcessoSistema.getAcessoUsuarioSistema());
    	                        if ((lstArqs != null) && !lstArqs.isEmpty()) {
    	                            serResponse.imagem = Base64.encodeBase64String(lstArqs.iterator().next().getAusConteudo());
    	                        }

	                            lstServidores.add(serResponse);
	                            usuCodigoUsado = usuCodigoAtual;



    	                    }
    	                } catch (final ZetraException zex) {
    	                    LOG.error(zex.getMessage(), zex);
    	                    if (erroOriginal == null) {
    	                        erroOriginal = zex;
    	                    }
    	                    continue;
    	                }
    	            }

    	            if (autenticou) {
    	                final Map<String, Object> mapRetorno = new HashMap<>();
    	                mapRetorno.put("rseLogado", response);
    	                mapRetorno.put("rseList", lstServidores);

    	                //	DESENV-17727 Remove as tentativas de acesso. Para não bloquear usuários indevidamente.
    	                final List<TransferObject> usuarios = usuarioController.lstUsuariosSer(response.cpf, null, null, null, (AcessoSistema) null);
    	                for (final TransferObject usuario : usuarios) {
    	                    ControleLogin.getInstance().resetTetantivasLogin((String) usuario.getAttribute(Columns.USU_CODIGO));
    	                }

    	                return mapRetorno;
    	            } else if (erroOriginal != null) {
    	                throw new ViewHelperException(erroOriginal);
    	            } else {
    	                throw new ViewHelperException("mensagem.usuarioSenhaInvalidos", responsavel);
    	            }
    	        }

    }

    private ServidorRestResponse preencheRseResponseBasico(TransferObject rseOrdenado) {
        final ServidorRestResponse serResponse = new ServidorRestResponse();
        serResponse.rseCodigo = (String) rseOrdenado.getAttribute(Columns.RSE_CODIGO);
        serResponse.rseMatricula = (String) rseOrdenado.getAttribute(Columns.RSE_MATRICULA);
        serResponse.estIdentificador = (String) rseOrdenado.getAttribute(Columns.EST_IDENTIFICADOR);
        serResponse.estCodigo = (String) rseOrdenado.getAttribute(Columns.EST_CODIGO);
        serResponse.estNome = (String) rseOrdenado.getAttribute(Columns.EST_NOME);
        serResponse.orgIdentificador = (String) rseOrdenado.getAttribute(Columns.ORG_IDENTIFICADOR);
        serResponse.orgCodigo = (String) rseOrdenado.getAttribute(Columns.ORG_CODIGO);
        serResponse.orgNome = (String) rseOrdenado.getAttribute(Columns.ORG_NOME);
        serResponse.srsDescricao = (String) rseOrdenado.getAttribute(Columns.SRS_DESCRICAO);
        serResponse.srsCodigo = (String) rseOrdenado.getAttribute(Columns.SRS_CODIGO);
        return serResponse;
    }

    private ServidorRestResponse autenticaRegistroServidor(String senhaClean, String id, TransferObject rseTO, boolean senhaApp, boolean permiteAguardAprovacaoCadastro, boolean autenticarUsuario, AcessoSistema responsavel) throws ZetraException {
        final String usuLogin = (String) rseTO.getAttribute(Columns.USU_LOGIN);
        if (!TextHelper.isNull(usuLogin)) {
            final TransferObject usuarioAutenticado = UsuarioHelper.autenticarUsuario(usuLogin, senhaClean, senhaApp, permiteAguardAprovacaoCadastro, autenticarUsuario, rseTO, responsavel);
            final String usuCodigo = (String) usuarioAutenticado.getAttribute(Columns.USU_CODIGO);
            final String estCodigo = (String) rseTO.getAttribute(Columns.EST_CODIGO);
            final String orgCodigo = (String) rseTO.getAttribute(Columns.ORG_CODIGO);
            final String rseCodigo = (String) rseTO.getAttribute(Columns.RSE_CODIGO);
            final String serCodigo = (String) rseTO.getAttribute(Columns.SER_CODIGO);
            final String serNome = (String) rseTO.getAttribute(Columns.SER_NOME);
            final String serCpf = (String) rseTO.getAttribute(Columns.SER_CPF);
            final String serEmail = (String) rseTO.getAttribute(Columns.SER_EMAIL);
            final String rseMatricula = (String) rseTO.getAttribute(Columns.RSE_MATRICULA);
            final String srsCodigo = (String) rseTO.getAttribute(Columns.SRS_CODIGO);
            final ServidorRestResponse serResponse = new ServidorRestResponse();
            final Date usuDataExpSenha = (Date) usuarioAutenticado.getAttribute(Columns.USU_DATA_EXP_SENHA);


            final Collection<ArquivoUsuario> lstArqs = usuarioController.findArquivoUsuario(usuCodigo, TipoArquivoEnum.ARQUIVO_IMAGEM_PERFIL_USUARIO.getCodigo(), AcessoSistema.getAcessoUsuarioSistema());
            if ((lstArqs != null) && !lstArqs.isEmpty()) {
                serResponse.imagem = Base64.encodeBase64String(lstArqs.iterator().next().getAusConteudo());
            }

            serResponse.token = usuarioController.gerarChaveSessaoUsuario(usuCodigo, AcessoSistema.getAcessoUsuarioSistema());
            serResponse.usuCodigo = usuCodigo;
            serResponse.statusUsuario = (String) rseTO.getAttribute(Columns.USU_STU_CODIGO);
            serResponse.id = id;
            serResponse.cpf = serCpf;
            serResponse.email = serEmail;
            serResponse.nome = serNome;
            serResponse.nomeMae = (String) rseTO.getAttribute(Columns.SER_NOME_MAE);
            serResponse.telefone = (String) rseTO.getAttribute(Columns.SER_TEL);
            serResponse.orgIdentificador = (String) rseTO.getAttribute(Columns.ORG_IDENTIFICADOR);
            serResponse.orgCodigo = orgCodigo;
            serResponse.estIdentificador = (String) rseTO.getAttribute(Columns.EST_IDENTIFICADOR);
            serResponse.estCodigo = estCodigo;
            serResponse.orgNome = !TextHelper.isNull(rseTO.getAttribute(Columns.ORG_NOME_ABREV)) ? (String) rseTO.getAttribute(Columns.ORG_NOME_ABREV) : (String) rseTO.getAttribute(Columns.ORG_NOME);
            serResponse.endereco = (String) rseTO.getAttribute(Columns.SER_END);
            serResponse.numero = (String) rseTO.getAttribute(Columns.SER_NRO);
            serResponse.complemento = (String) rseTO.getAttribute(Columns.SER_COMPL);
            serResponse.bairro = (String) rseTO.getAttribute(Columns.SER_BAIRRO);
            serResponse.cidade = (String) rseTO.getAttribute(Columns.SER_CIDADE);
            serResponse.cep = (String) rseTO.getAttribute(Columns.SER_CEP);
            serResponse.uf = (String) rseTO.getAttribute(Columns.SER_UF);
            serResponse.municipioLotacao = (String) rseTO.getAttribute(Columns.RSE_MUNICIPIO_LOTACAO);
            serResponse.rseCodigo = rseCodigo;
            serResponse.rseMatricula = rseMatricula;
            serResponse.rseTipo = (String) rseTO.getAttribute(Columns.RSE_TIPO);
            serResponse.srsCodigo = srsCodigo;
            serResponse.estNome = (String) rseTO.getAttribute(Columns.EST_NOME);
            serResponse.serCodigo = serCodigo;
            serResponse.dataNascimento = DateHelper.format((Date) rseTO.getAttribute(Columns.SER_DATA_NASC), LocaleHelper.getDatePattern());
            //TODO mudar para campo iban quando for criado
            serResponse.iban = (String) rseTO.getAttribute(Columns.RSE_AGENCIA_SAL_2);
            serResponse.sexo = (String) rseTO.getAttribute(Columns.SER_SEXO);
            serResponse.nroIdentidade = (String) rseTO.getAttribute(Columns.SER_NRO_IDT);
            serResponse.dataIdentidade = DateHelper.format((Date) rseTO.getAttribute(Columns.SER_DATA_IDT), LocaleHelper.getDatePattern());
            serResponse.nacionalidade = (String) rseTO.getAttribute(Columns.SER_NACIONALIDADE);
            serResponse.naturalidade = (String) rseTO.getAttribute(Columns.SER_CID_NASC) + " - " + (String) rseTO.getAttribute(Columns.SER_UF_NASC);
            serResponse.salario = (BigDecimal) rseTO.getAttribute(Columns.RSE_SALARIO) != null ? ((BigDecimal) rseTO.getAttribute(Columns.RSE_SALARIO)).toString() : null;
            serResponse.dataAdmissao = DateHelper.format((Date) rseTO.getAttribute(Columns.RSE_DATA_ADMISSAO), LocaleHelper.getDatePattern());
            serResponse.celular = (String) rseTO.getAttribute(Columns.SER_CELULAR);
            serResponse.dataValidacaoEmail = DateHelper.format((Date) rseTO.getAttribute(Columns.SER_DATA_VALIDACAO_EMAIL), LocaleHelper.getDateTimePattern());
            serResponse.permiteAlterarEmail = CodedValues.TPC_SIM.equals(rseTO.getAttribute(Columns.SER_PERMITE_ALTERAR_EMAIL));
            serResponse.dataIdentificacaoPessoal = DateHelper.format((Date) rseTO.getAttribute(Columns.SER_DATA_IDENTIFICACAO_PESSOAL), LocaleHelper.getDateTimePattern());
            serResponse.dataUltimoAcesso = DateHelper.format((Date) rseTO.getAttribute(Columns.USU_DATA_ULT_ACESSO), LocaleHelper.getDateTimePattern());
            serResponse.rseMotivoFaltaMargem = (String) rseTO.getAttribute(Columns.RSE_MOTIVO_FALTA_MARGEM);
            serResponse.usuDataExpSenha = DateHelper.format((Date) usuDataExpSenha, LocaleHelper.getDateTimePattern());

            final Map<String, EnderecoFuncaoTransferObject> funcoes = usuarioController.selectFuncoes(usuCodigo, serCodigo, AcessoSistema.ENTIDADE_SER, responsavel);
            serResponse.permissoes = new ArrayList<>(funcoes.keySet());

            // Atualiza o responsável
            responsavel.setUsuLogin(usuLogin);
            responsavel.setTipoEntidade(AcessoSistema.ENTIDADE_SER);
            responsavel.setCodigoEntidade(serCodigo);
            responsavel.setNomeEntidade(serNome);
            responsavel.setDadosServidor(serCodigo, orgCodigo, rseCodigo, rseMatricula, serCpf, serEmail, null, srsCodigo);
            responsavel.setPermissoes(funcoes);

            // inclui as naturezas de serviço disponíveis para solicitação pelo servidor
            final boolean temPermissaoSimulacao = serResponse.permissoes.contains(CodedValues.FUN_SIM_CONSIGNACAO);
            final boolean temPermissaoSolicitacao = responsavel.temPermissao(CodedValues.FUN_SOL_EMPRESTIMO);
            final boolean temPermissaoReserva = serResponse.permissoes.contains(CodedValues.FUN_RES_MARGEM);
            final List<TransferObject> servicosReserva = SolicitacaoServidorHelper.lstServicos((String) rseTO.getAttribute(Columns.ORG_CODIGO), null, null, temPermissaoSimulacao, temPermissaoReserva, temPermissaoSolicitacao, responsavel);

            serResponse.nseCodigos = new HashMap<>();
            if ((servicosReserva != null) && !servicosReserva.isEmpty()) {
                for (final TransferObject nse : servicosReserva) {
                    serResponse.nseCodigos.put((String) nse.getAttribute(Columns.NSE_CODIGO), (String) nse.getAttribute(Columns.NSE_DESCRICAO));
                }
            }

            serResponse.exibeBotaoIniciarLeilao = ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR, responsavel);
            serResponse.podeSimular = responsavel.temPermissao(CodedValues.FUN_SIM_CONSIGNACAO);
            serResponse.podeSolicitar = responsavel.temPermissao(CodedValues.FUN_SOL_EMPRESTIMO);

            // DESENV-8257: envio de info de obrigatoriedade de cadastro de telefone e/ou e-mail no primeiro acesso
            // se não for o primeiro acesso do usuário ao sistema, ambos serão invariavelmente falsos
            serResponse.exigeCadEmailPrimeiroAcesso = !TextHelper.isNull(rseTO.getAttribute(Columns.USU_DATA_ULT_ACESSO)) ? false : ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_ATUALIZACAO_DADOS_SER_PRIMEIRO_ACESSO, responsavel) && ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_CAD_EMAIL_SER_PRIMEIRO_ACESSO_SIST, responsavel);

            serResponse.exigeCadTelPrimeiroAcesso = !TextHelper.isNull(rseTO.getAttribute(Columns.USU_DATA_ULT_ACESSO)) ? false : ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_ATUALIZACAO_DADOS_SER_PRIMEIRO_ACESSO, responsavel) && ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_CAD_TELEFONE_SER_PRIMEIRO_ACESSO_SIST, responsavel);

            // DESENV-6584: Se sistema simula agrupado por natureza de serviço
            serResponse.simulaAgrupadoPorNatureza = ParamSist.getBoolParamSist(CodedValues.TPC_SIMULADOR_AGRUPADO_NATUREZA_SERVICO, responsavel);

            serResponse.possuiFormularioPesquisaSemResposta = formularioPesquisaController.findFormularioPesquisaMaisAntigoSemResposta(usuCodigo, responsavel) != null;

            return serResponse;
        }

        return null;
    }

    @POST
    @Secured
    @Path("/gerarCodigoAutorizacao")
    public Response gerarCodigoAutorizacao() {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        final CodigoUnicoRestResponse codigoUnicoRestResponse = new CodigoUnicoRestResponse();

        try {
            codigoUnicoRestResponse.codigoUnico = usuarioController.gerarSenhaAutorizacaoRest(responsavel);

            //Verifica o modo de entrega conforme parâmetro 362 para preenchimento da mensagem de retorno
            final String modoEntrega = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_ALTERACAO_SENHA_AUT_SERVIDOR, responsavel)) ? ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_ALTERACAO_SENHA_AUT_SERVIDOR, responsavel).toString() : CodedValues.ALTERACAO_SENHA_AUT_SER_ENVIA_EMAIL;

            final ServidorTransferObject servidor = servidorController.findServidor(responsavel.getSerCodigo(), responsavel);
            final String serCel = servidor.getSerCelular();
            final String serEmail = servidor.getSerEmail();

            if (CodedValues.ALTERACAO_SENHA_AUT_SER_EMAIL_E_TELA.equals(modoEntrega) && !TextHelper.isNull(serEmail)) {
                codigoUnicoRestResponse.msgRetorno = ApplicationResourcesHelper.getMessage("mobile.mensagem.codigo.unico.enviado.por.email.e.tela", responsavel, codigoUnicoRestResponse.codigoUnico, serEmail);
            } else if (CodedValues.ALTERACAO_SENHA_AUT_SER_EMAIL_E_TELA.equals(modoEntrega) && TextHelper.isNull(serEmail)) {
                codigoUnicoRestResponse.msgRetorno = ApplicationResourcesHelper.getMessage("mobile.mensagem.codigo.unico.enviado.por.email.ou.tela", responsavel, codigoUnicoRestResponse.codigoUnico);
            } else if ((CodedValues.ALTERACAO_SENHA_AUT_SER_ENVIA_EMAIL.equals(modoEntrega) && !TextHelper.isNull(serEmail)) || (CodedValues.ALTERACAO_SENHA_AUT_SER_EXIBE_TELA.equals(modoEntrega) && !TextHelper.isNull(serEmail)) || (CodedValues.ALTERACAO_SENHA_AUT_SER_EMAIL_OU_TELA.equals(modoEntrega) && !TextHelper.isNull(serEmail)) || (CodedValues.ALTERACAO_SENHA_AUT_SER_SMS_E_EMAIL.equals(modoEntrega) && TextHelper.isNull(serCel) && !TextHelper.isNull(serEmail))) {
                codigoUnicoRestResponse.msgRetorno = ApplicationResourcesHelper.getMessage("mobile.mensagem.codigo.unico.enviado.por.email", responsavel, serEmail);
            } else if ((CodedValues.ALTERACAO_SENHA_AUT_SER_SMS.equals(modoEntrega) && !TextHelper.isNull(serCel)) || (CodedValues.ALTERACAO_SENHA_AUT_SER_SMS_E_EMAIL.equals(modoEntrega) && TextHelper.isNull(serEmail) && !TextHelper.isNull(serCel))) {
                codigoUnicoRestResponse.msgRetorno = ApplicationResourcesHelper.getMessage("mobile.mensagem.codigo.unico.enviado.por.sms", responsavel, serCel);
            } else if (CodedValues.ALTERACAO_SENHA_AUT_SER_SMS_E_EMAIL.equals(modoEntrega) && !TextHelper.isNull(serCel) && !TextHelper.isNull(serEmail)) {
                codigoUnicoRestResponse.msgRetorno = ApplicationResourcesHelper.getMessage("mobile.mensagem.codigo.unico.enviado.por.email.sms", responsavel, serEmail, serCel);
            } else {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mobile.mensagem.erro.envio.codigo.unico", responsavel, serEmail, serCel);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
            }
        } catch (UsuarioControllerException | ServidorControllerException e) {
            LOG.error(e.getMessage(), e);
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = e.getMessage();
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }
        return Response.status(Response.Status.OK).entity(codigoUnicoRestResponse).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
    }

    @POST
    @Secured
    @Path("/consultaCodigoAutorizacao")
    public Response consultaCodigoAutorizacao() {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        final List<CodigoUnicoRestResponse> codigoUnicoRestResponseList = new LinkedList<>();

        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        try {
            final List<TransferObject> senhasAutorizacao = usuarioController.lstSenhaAutorizacaoServidorRest(responsavel);
            for (final TransferObject senhaAutorizacao : senhasAutorizacao) {
                final CodigoUnicoRestResponse codigoUnico = new CodigoUnicoRestResponse();
                codigoUnico.qtdOperacoes = (Short) senhaAutorizacao.getAttribute(Columns.SAS_QTD_OPERACOES);
                codigoUnico.dataExpiracao = dateFormat.format((Date) senhaAutorizacao.getAttribute(Columns.SAS_DATA_EXPIRACAO));
                codigoUnico.dataCriacao = dateTimeFormat.format((Date) senhaAutorizacao.getAttribute(Columns.SAS_DATA_CRIACAO));

                codigoUnicoRestResponseList.add(codigoUnico);
            }
        } catch (final UsuarioControllerException e) {
            LOG.error(e.getMessage(), e);
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = e.getMessage();
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        return Response.status(Response.Status.OK).entity(codigoUnicoRestResponseList).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
    }

    @POST
    @Secured
    @Path("/removerCodigoAutorizacao")
    public Response removerCodigoAutorizacao(CodigoUnicoRestResponse codigoUnico) {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

        try {
            final DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            final Date dataCriacao = formatter.parse(codigoUnico.dataCriacao);
            usuarioController.cancelaSenhaAutorizacaoRest(dataCriacao, responsavel);
        } catch (UsuarioControllerException | ParseException e) {
            LOG.error(e.getMessage(), e);
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.remover.codigo.unico", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        final ResponseRestRequest responseError = new ResponseRestRequest();
        responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.remover.codigo.unico", null);
        return Response.status(Response.Status.OK).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
    }

    @POST
    @Secured
    @Path("/consultarContraCheque")
    public Response consultarContraCheque(ContraChequeRestRequest contraCheque) {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

        final String rseCodigo = responsavel.getRseCodigo();
        final String matricula = responsavel.getRseMatricula();

        if (TextHelper.isNull(rseCodigo) || TextHelper.isNull(matricula)) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        if (!responsavel.temPermissao(CodedValues.FUN_CONS_CONTRACHEQUE)) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.usuarioNaoTemPermissao", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        FileInputStream contrachequeStream = null;
        try {
            if ((contraCheque != null) && !TextHelper.isNull(contraCheque.periodo)) {

                final String absolutePath = new File(ParamSist.getDiretorioRaizArquivos()).getCanonicalPath();

                final String caminhoBackgroudbase = absolutePath + File.separatorChar + "imagem" + File.separatorChar + "fundo_contracheque.png";
                final String caminhoBackgroud = CreateImageHelper.gerarImagemTransparente(caminhoBackgroudbase);

                // --------------------------------------------------------------------------

                final com.zetra.econsig.report.config.Relatorio relatorio = ConfigRelatorio.getInstance().getRelatorio("contra_cheque");
                final Map<String, String[]> parameterMap = new HashMap<>();
                parameterMap.put("rseCodigo", new String[] { rseCodigo });
                parameterMap.put("periodo", new String[] { DateHelper.reformat(contraCheque.periodo, "yyyy-MM", "yyyy-MM-01") });
                parameterMap.put("MATRICULA", new String[] { matricula });
                parameterMap.put("NOME", new String[] { responsavel.getName() });
                parameterMap.put("CAMINHO_BACKGROUND", new String[] { caminhoBackgroud });

                final String nomeArquivo = "contracheque_" + contraCheque.periodo;
                parameterMap.put(ReportManager.REPORT_FILE_NAME, new String[] { nomeArquivo });

                String exportDir = absolutePath + File.separatorChar + "relatorio" + File.separatorChar + "cse" + File.separatorChar + "contra_cheque";

                File dir = new File(exportDir);
                if (!dir.exists()) {
                    dir.mkdir();
                }

                exportDir += File.separatorChar + rseCodigo;

                dir = new File(exportDir);
                if (!dir.exists()) {
                    dir.mkdir();
                }

                parameterMap.put(ReportManager.REPORT_DIR_EXPORT, new String[] { exportDir });

                final ProcessaContraCheque processaContraCheque = new ProcessaContraCheque(relatorio, parameterMap, null, responsavel);
                processaContraCheque.run();

                // -------   Leio o contracheque para um string base64  -----------------

                final File contrachequeFile = new File(exportDir + File.separatorChar + nomeArquivo + ".pdf");
                contrachequeStream = new FileInputStream(contrachequeFile);
                final byte ccData[] = new byte[(int) contrachequeFile.length()];
                contrachequeStream.read(ccData);

                final String ccBase64 = Base64.encodeBase64String(ccData);

                // ----------------------------------------------------------------------

                final ContraChequeRestRequest retorno = new ContraChequeRestRequest();
                retorno.periodo = contraCheque.periodo;
                retorno.arquivo = ccBase64;

                contrachequeFile.delete();

                return Response.status(Response.Status.OK).entity(retorno).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();

            } else {

                final Date dataInicio = (contraCheque != null) && (contraCheque.dataInicio != null) ? DateHelper.parse(contraCheque.dataInicio, "yyyy-MM-dd HH:mm:ss") : null;
                final Date dataFim = (contraCheque != null) && (contraCheque.dataFim != null) ? DateHelper.parse(contraCheque.dataFim, "yyyy-MM-dd HH:mm:ss") : null;

                final ImpArqContrachequeController impArqContrachequeController = ApplicationContextProvider.getApplicationContext().getBean(ImpArqContrachequeController.class);
                final List<TransferObject> contracheques = impArqContrachequeController.listarContrachequeRse(rseCodigo, null, false, 12, true, dataInicio, dataFim, responsavel);
                final List<ContraChequeRestRequest> periodos = new LinkedList<>();

                if ((contracheques != null) && (contracheques.size() > 0)) {
                    TransferObject periodoTO = null;
                    String periodoId, periodoNome;

                    for (final TransferObject contracheque : contracheques) {
                        periodoTO = contracheque;
                        periodoId = DateHelper.format((Date) periodoTO.getAttribute(Columns.CCQ_PERIODO), "yyyy-MM");
                        periodoNome = DateHelper.getMonthName((Date) periodoTO.getAttribute(Columns.CCQ_PERIODO)) + "/" + DateHelper.getYear((Date) periodoTO.getAttribute(Columns.CCQ_PERIODO));
                        if (DateHelper.getMonth((Date) periodoTO.getAttribute(Columns.CCQ_PERIODO)) == 12) {
                            periodoNome += " - " + ApplicationResourcesHelper.getMessage("rotulo.servidor.contracheque.periodo.decimo.terceiro", responsavel);
                        }

                        final ContraChequeRestRequest periodo = new ContraChequeRestRequest();
                        periodo.periodo = periodoId;
                        periodo.periodoNome = periodoNome;

                        periodos.add(periodo);

                    }
                }
                return Response.status(Response.Status.OK).entity(periodos).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        } finally {
            if (contrachequeStream != null) {
                try {
                    contrachequeStream.close();
                } catch (final IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        }
    }

    @POST
    @Path("/uploadImg")
    @Secured
    public Response uploadImagem(UsuarioImgRestRequest request) {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

        try {
            final byte[] imagem = Base64.decodeBase64(request.imagem);
            usuarioController.insereAlteraImagemUsuario(responsavel.getUsuCodigo(), imagem, responsavel);
        } catch (final UsuarioControllerException e) {
            LOG.error(e.getMessage(), e);
            return genericError(e);
        }
        return Response.status(Response.Status.OK).entity(new ResponseRestRequest()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
    }

    @POST
    @Path("/validarOTPCodigo")
    public Response validarOTPCodigo(AutenticarEuConsigoMaisRequest request) {
        if (request == null) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        final Response responseSist = verificaSistemaDisponivel();
        if (responseSist != null) {
            return responseSist;
        }

        if (TextHelper.isNull(request.email) || TextHelper.isNull(request.token)) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

        try {
            final Usuario usu = usuarioController.findUsuarioByEmailAndToken(request.email, request.token, responsavel);

            //verifica se o token está vencido 5min
            if ((usu.getUsuOtpDataCadastro() == null) || (DateHelper.minDiff(usu.getUsuOtpDataCadastro()) > 5)) {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.codigo.otp.vencido", null);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
            }

        } catch (final UsuarioControllerException e) {
            LOG.error(e.getMessage(), e);
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = e.getMessage();
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        return Response.status(Response.Status.OK).entity(new AutenticarEuConsigoMaisRequest(true)).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
    }

    @POST
    @Path("/recuperarDadosValidacaoSer")
    @Consumes("application/json")
    @Produces("application/json")
    public Response recuperarDadosValidacaoSer(UsuarioRestRequest usuario) {
        return recuperarDadosValidacaoSerV2(usuario);
    }

    @GET
    @Secured
    @Path("/v2/dadosValidacaoSer")
    @Consumes("application/json")
    @Produces("application/json")
    public Response recuperarDadosValidacaoSerV2(UsuarioRestRequest usuario) {
        final String idClean = XSSPreventionFilter.stripXSS(usuario.id);
        final boolean serAtivo = usuario.retornaApenasSrsAtivo;

        final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

        if (TextHelper.isNull(idClean)) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.cpf", responsavel);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        try {
            final List<Servidor> servidores = servidorController.findByCpf(idClean, responsavel);

            final ServidorRestResponse serResponse = new ServidorRestResponse();
            if (!servidores.isEmpty()) {
                final Servidor servidor = servidores.getFirst();
                serResponse.cpf = servidor.getSerCpf();
                serResponse.email = servidor.getSerEmail();

                final List<RegistroServidorTO> registrosServidor = servidorController.findRegistroServidorBySerCodigo(servidor.getSerCodigo(), responsavel);
                if ((registrosServidor != null) && !registrosServidor.isEmpty()) {
                    final Optional<RegistroServidorTO> filter;

                    if (serAtivo) {
                        final List<RegistroServidorTO> registrosServidorAtivoOuBloqueado = registrosServidor.stream().filter(rse -> CodedValues.SRS_ATIVOS.contains(rse.getSrsCodigo())).toList();

                        filter = retornarRegistroServidorAtivo(registrosServidorAtivoOuBloqueado);
                    } else {
                        filter = registrosServidor.stream().filter(rse -> CodedValues.SRS_ATIVOS.contains(rse.getSrsCodigo())).findFirst();
                    }

                    if (filter.isPresent()) {
                        serResponse.rseMatricula = filter.get().getRseMatricula();
                        serResponse.dataAdmissao = filter.get().getRseDataAdmissao() != null ? DateHelper.toISOStringWithLocalTimeZone(filter.get().getRseDataAdmissao()) : null;
                    } else {
                        throw new Exception(ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel));
                    }
                }

                serResponse.id = servidor.getSerCpf();
                serResponse.dataValidacaoEmail = servidor.getSerDataValidacaoEmail() != null ? DateHelper.toDateTimeString(servidor.getSerDataValidacaoEmail()) : null;
                serResponse.permiteAlterarEmail = CodedValues.TPC_SIM.equals(servidor.getSerPermiteAlterarEmail());
                serResponse.dataIdentificacaoPessoal = servidor.getSerDataIdentificacaoPessoal() != null ? DateHelper.toDateTimeString(servidor.getSerDataIdentificacaoPessoal()) : null;
                serResponse.permiteSobreporSenha = ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_SALARYPAY_SOBREPOR_SENHA_SERVIDOR, responsavel);
                serResponse.integracaoEconsigSalaryPay = ParamSist.getBoolParamSist(CodedValues.TPC_HABILITAR_INTEGRACAO_SALARYPAY, responsavel);
                serResponse.validaKycFacesWeb = ParamSist.getBoolParamSist(CodedValues.TPC_VALIDAR_KYC_FACESWEB_INTEGRACAO_SALARYPAY, responsavel);
                serResponse.validaKycBankAsService = ParamSist.getBoolParamSist(CodedValues.TPC_VALIDAR_KYC_BANK_AS_SERVICE_INTEGRACAO_SALARYPAY, responsavel);
                serResponse.integraSalaryPayCielo = ParamSist.getBoolParamSist(CodedValues.TPC_INTEGRAR_SALARYPAY_A_CIELO, responsavel);
                serResponse.apuracaoAutomaticaCadastroFacesWeb = ParamSist.getBoolParamSist(CodedValues.TPC_APURACAO_AUTOMATICA_CADASTRO_FACESWEB, responsavel);
            }

            final List<TransferObject> usuarios = usuarioController.lstUsuariosSer(idClean, serResponse.rseMatricula, null, null, null);
            if ((usuarios != null) && (!usuarios.isEmpty())) {
                serResponse.statusUsuario = (String) usuarios.getFirst().getAttribute(Columns.USU_STU_CODIGO);
            } else {
                serResponse.statusUsuario = CodedValues.STU_EXCLUIDO;
            }
            //TODO: tratar para retornar mais de um servidor
            return Response.status(Response.Status.OK).entity(serResponse).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = e.getMessage();
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }
    }

    @POST
    @Path("/enviarOtpConfirmacaoEmail")
    public Response enviarOtpConfirmacaoEmail(UsuarioRestRequest usuario) {
        final String idClean = XSSPreventionFilter.stripXSS(usuario.id);
        String emailClean = XSSPreventionFilter.stripXSS(usuario.email);

        final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

        final Response responseSist = verificaSistemaDisponivel();
        if (responseSist != null) {
            return responseSist;
        }

        if (TextHelper.isNull(idClean)) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.cpf", responsavel);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        try {
            emailClean = usuarioController.consultarEmailServidor(true, idClean, emailClean, CodedValues.ALTERACAO_SENHA_AUT_SER_ENVIA_EMAIL, responsavel);
            final List<Servidor> servidores = servidorController.findByCpf(idClean, responsavel);

            //TODO: tratar futuramente para os múltiplos servidores
            usuarioController.gerarOtpConfirmacaoEmail(servidores.get(0), emailClean, responsavel);
        } catch (ServidorControllerException | UsuarioControllerException e1) {
            LOG.error(e1.getMessage(), e1);
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = e1.getMessage();
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        return Response.status(Response.Status.OK).entity(new ResponseRestRequest(emailClean)).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
    }

    @POST
    @Path("/validarOtpConfirmacaoEmail")
    public Response validarOtpConfirmacaoEmail(UsuarioRestRequest usuario) {
        final String idClean = XSSPreventionFilter.stripXSS(usuario.id);
        final String otpClean = XSSPreventionFilter.stripXSS(usuario.otp);

        final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

        final Response responseSist = verificaSistemaDisponivel();
        if (responseSist != null) {
            return responseSist;
        }

        if (TextHelper.isNull(idClean)) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.cpf", responsavel);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        if (TextHelper.isNull(otpClean)) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.otp.nao.informado", responsavel);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        try {
            if (usuarioController.validarOtpConfirmacaoEmail(otpClean, idClean, responsavel)) {
                return Response.status(Response.Status.OK).entity(new ResponseRestRequest()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
            } else {
                final ResponseRestRequest responseError = new ResponseRestRequest();
                responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.otp.invalido", responsavel);
                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
            }
        } catch (final UsuarioControllerException e) {
            LOG.error(e.getMessage(), e);
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = e.getMessage();
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }
    }

    /**
     * Método originalmente utilizado para garantir a identificação pessoal no aplicativo SalaryPay.
     * Não é mais utilizado pois a senha do eConsig foi desvinculada da senha do SalaryPay.
     *
     * @param usuario
     * @return
     */
    @POST
    @Secured
    @Path("/atualizaDataIdentificacaoPessoal")
    public Response atualizaDataIdentificacaoPessoal(UsuarioRestRequest usuario) {
        return Response.status(Response.Status.OK).entity(new ResponseRestRequest()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
    }

    @POST
    @Secured
    @Path("/atualizarDadosPessoaisObrigatorios")
    public Response atualizarDadosPessoaisObrigatorios(UsuarioRestRequest usuario) {
        final Response responseSist = verificaSistemaDisponivel();
        if (responseSist != null) {
            return responseSist;
        }

        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

        if (!responsavel.isSer()) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.mensagem.usoIncorretoSistema", responsavel);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        final boolean exigeAtualizacaoDados = ParamSist.paramEquals(CodedValues.TPC_EXIGE_ATUALIZACAO_DADOS_SER_PRIMEIRO_ACESSO, CodedValues.TPC_SIM, responsavel);
        final boolean exigeEmail = ParamSist.paramEquals(CodedValues.TPC_EXIGE_CAD_EMAIL_SER_PRIMEIRO_ACESSO_SIST, CodedValues.TPC_SIM, responsavel);
        final boolean exigeTel = ParamSist.paramEquals(CodedValues.TPC_EXIGE_CAD_TELEFONE_SER_PRIMEIRO_ACESSO_SIST, CodedValues.TPC_SIM, responsavel);

        try {
            final ServidorTransferObject serTO = servidorController.findServidor(responsavel.getSerCodigo(), responsavel);
            if (!TextHelper.isNull(usuario.email)) {
                serTO.setSerDataValidacaoEmail(new Timestamp(DateHelper.getSystemDatetime().getTime()));
            }

            if ((ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_EMAIL, responsavel) || (exigeAtualizacaoDados && exigeEmail)) && TextHelper.isNull(usuario.email)) {
                throw new ServidorControllerException("mensagem.informe.servidor.email", responsavel);
            } else if (ShowFieldHelper.canEdit(FieldKeysConstants.ATUALIZAR_DADOS_SER_EMAIL, responsavel)) {
                serTO.setSerEmail(usuario.email);
            }

            if (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_TELEFONE, responsavel) && TextHelper.isNull(usuario.telefone)) {
                throw new ServidorControllerException("mensagem.informe.servidor.telefone", responsavel);
            } else if (exigeAtualizacaoDados && exigeTel && TextHelper.isNull(usuario.telefone) && TextHelper.isNull(usuario.celular)) {
                throw new ServidorControllerException("mensagem.informe.servidor.telefone.ou.celular", responsavel);
            } else if (ShowFieldHelper.canEdit(FieldKeysConstants.ATUALIZAR_DADOS_SER_TELEFONE, responsavel)) {
                serTO.setSerTel(usuario.telefone);
            }

            if (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_CELULAR, responsavel) && TextHelper.isNull(usuario.celular)) {
                throw new ServidorControllerException("mensagem.informe.servidor.celular", responsavel);
            } else if (exigeAtualizacaoDados && exigeTel && TextHelper.isNull(usuario.telefone) && TextHelper.isNull(usuario.celular)) {
                throw new ServidorControllerException("mensagem.informe.servidor.telefone.ou.celular", responsavel);
            } else if (ShowFieldHelper.canEdit(FieldKeysConstants.ATUALIZAR_DADOS_SER_CELULAR, responsavel)) {
                serTO.setSerCelular(usuario.celular);
            }

            if (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_ENDERECO, responsavel) && TextHelper.isNull(usuario.endereco)) {
                throw new ServidorControllerException("mensagem.informe.servidor.logradouro", responsavel);
            } else if (ShowFieldHelper.canEdit(FieldKeysConstants.ATUALIZAR_DADOS_SER_ENDERECO, responsavel)) {
                serTO.setSerEnd(usuario.endereco);
            }

            if (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_CIDADE, responsavel) && TextHelper.isNull(usuario.cidade)) {
                throw new ServidorControllerException("mensagem.informe.servidor.cidade", responsavel);
            } else if (ShowFieldHelper.canEdit(FieldKeysConstants.ATUALIZAR_DADOS_SER_CIDADE, responsavel)) {
                serTO.setSerCidade(usuario.cidade);
            }

            if (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_BAIRRO, responsavel) && TextHelper.isNull(usuario.bairro)) {
                throw new ServidorControllerException("mensagem.informe.servidor.bairro", responsavel);
            } else if (ShowFieldHelper.canEdit(FieldKeysConstants.ATUALIZAR_DADOS_SER_BAIRRO, responsavel)) {
                serTO.setSerBairro(usuario.bairro);
            }

            if (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_COMPLEMENTO, responsavel) && TextHelper.isNull(usuario.complemento)) {
                throw new ServidorControllerException("mensagem.informe.servidor.complemento", responsavel);
            } else if (ShowFieldHelper.canEdit(FieldKeysConstants.ATUALIZAR_DADOS_SER_COMPLEMENTO, responsavel)) {
                serTO.setSerCompl(usuario.complemento);
            }

            if (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_NUMERO, responsavel) && TextHelper.isNull(usuario.numero)) {
                throw new ServidorControllerException("mensagem.informe.servidor.numero", responsavel);
            } else if (ShowFieldHelper.canEdit(FieldKeysConstants.ATUALIZAR_DADOS_SER_NUMERO, responsavel)) {
                serTO.setSerNro(usuario.numero);
            }

            if (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_CEP, responsavel) && TextHelper.isNull(usuario.cep)) {
                throw new ServidorControllerException("mensagem.informe.servidor.cep", responsavel);
            } else if (ShowFieldHelper.canEdit(FieldKeysConstants.ATUALIZAR_DADOS_SER_CEP, responsavel)) {
                serTO.setSerCep(usuario.cep);
            }

            if (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_UF, responsavel) && TextHelper.isNull(usuario.uf)) {
                throw new ServidorControllerException("mensagem.informe.servidor.estado", responsavel);
            } else if (ShowFieldHelper.canEdit(FieldKeysConstants.ATUALIZAR_DADOS_SER_UF, responsavel)) {
                serTO.setSerUf(usuario.uf);
            }

            if (exigeAtualizacaoDados && ParamSist.paramEquals(CodedValues.TPC_RECONHECIMENTO_FACIAL_ACESSO_SERVIDOR, CodedValues.TPC_SIM, responsavel) && usuario.primeiroAcesso) {

                final TipoArquivoEnum frontal = TipoArquivoEnum.ARQUIVO_RECONHECIMENTO_FACIAL_FRONTAL_SERVIDOR;
                final TipoArquivoEnum perfilEsquerdo = TipoArquivoEnum.ARQUIVO_RECONHECIMENTO_FACIAL_PERFIL_ESQUERDO_SERVIDOR;
                final TipoArquivoEnum perfilDireito = TipoArquivoEnum.ARQUIVO_RECONHECIMENTO_FACIAL_PERFIL_DIREITO_SERVIDOR;

                if (frontal != null) {
                    salvarCapturaFotosPrimeiroAcesso(usuario.fotoFrente, frontal.getCodigo(), responsavel);
                }

                if (perfilEsquerdo != null) {
                    salvarCapturaFotosPrimeiroAcesso(usuario.fotoEsquerda, perfilEsquerdo.getCodigo(), responsavel);
                }

                if (perfilDireito != null) {
                    salvarCapturaFotosPrimeiroAcesso(usuario.fotoDireita, perfilDireito.getCodigo(), responsavel);
                }
            }

            serTO.setSerDataIdentificacaoPessoal(new Timestamp(DateHelper.getSystemDatetime().getTime()));
            servidorController.updateServidor(serTO, responsavel);

        } catch (final ZetraException e1) {
            LOG.error(e1.getMessage(), e1);
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = e1.getMessage();
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        return Response.status(Response.Status.OK).entity(new ResponseRestRequest()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
    }

    @POST
    @Secured
    @Path("/atualizarDadosPessoaisPerfil")
    public Response atualizarDadosPessoaisPerfil(UsuarioRestRequest usuario) {
        final Response responseSist = verificaSistemaDisponivel();
        if (responseSist != null) {
            return responseSist;
        }

        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

        if (!responsavel.isSer()) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.mensagem.usoIncorretoSistema", responsavel);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        try {
            final ServidorTransferObject serTO = servidorController.findServidor(responsavel.getSerCodigo(), responsavel);

            if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_EMAIL, responsavel) && TextHelper.isNull(usuario.email)) {
                throw new ServidorControllerException("mensagem.informe.servidor.email", responsavel);
            } else if (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_EMAIL, responsavel)) {
                serTO.setSerEmail(usuario.email);
            }

            if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_TELEFONE, responsavel) && TextHelper.isNull(usuario.telefone)) {
                throw new ServidorControllerException("mensagem.informe.servidor.telefone", responsavel);
            } else if (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_TELEFONE, responsavel)) {
                serTO.setSerTel(usuario.telefone);
            }

            if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_CELULAR, responsavel) && TextHelper.isNull(usuario.celular)) {
                throw new ServidorControllerException("mensagem.informe.servidor.celular", responsavel);
            } else if (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_CELULAR, responsavel)) {
                serTO.setSerCelular(usuario.celular);
            }

            if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_LOGRADOURO, responsavel) && TextHelper.isNull(usuario.endereco)) {
                throw new ServidorControllerException("mensagem.informe.servidor.logradouro", responsavel);
            } else if (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_LOGRADOURO, responsavel)) {
                serTO.setSerEnd(usuario.endereco);
            }

            if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_CIDADE, responsavel) && TextHelper.isNull(usuario.cidade)) {
                throw new ServidorControllerException("mensagem.informe.servidor.cidade", responsavel);
            } else if (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_CIDADE, responsavel)) {
                serTO.setSerCidade(usuario.cidade);
            }

            if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_BAIRRO, responsavel) && TextHelper.isNull(usuario.bairro)) {
                throw new ServidorControllerException("mensagem.informe.servidor.bairro", responsavel);
            } else if (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_BAIRRO, responsavel)) {
                serTO.setSerBairro(usuario.bairro);
            }

            if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_COMPLEMENTO, responsavel) && TextHelper.isNull(usuario.complemento)) {
                throw new ServidorControllerException("mensagem.informe.servidor.complemento", responsavel);
            } else if (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_COMPLEMENTO, responsavel)) {
                serTO.setSerCompl(usuario.complemento);
            }

            if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_NRO, responsavel) && TextHelper.isNull(usuario.numero)) {
                throw new ServidorControllerException("mensagem.informe.servidor.numero", responsavel);
            } else if (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_NRO, responsavel)) {
                serTO.setSerNro(usuario.numero);
            }

            if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_CEP, responsavel) && TextHelper.isNull(usuario.cep)) {
                throw new ServidorControllerException("mensagem.informe.servidor.cep", responsavel);
            } else if (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_CEP, responsavel)) {
                serTO.setSerCep(usuario.cep);
            }

            if (ShowFieldHelper.isRequired(FieldKeysConstants.EDT_SERVIDOR_UF, responsavel) && TextHelper.isNull(usuario.uf)) {
                throw new ServidorControllerException("mensagem.informe.servidor.estado", responsavel);
            } else if (ShowFieldHelper.canEdit(FieldKeysConstants.EDT_SERVIDOR_UF, responsavel)) {
                serTO.setSerUf(usuario.uf);
            }

            servidorController.updateServidor(serTO, responsavel);

        } catch (final ZetraException e1) {
            LOG.error(e1.getMessage(), e1);
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = e1.getMessage();
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        return Response.status(Response.Status.OK).entity(new ResponseRestRequest()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
    }

    /**
     * Método originalmente utilizado para validação do e-mail do servidor usando o mesmo do SalaryPay.
     * Não é mais utilizado pois a senha do eConsig foi desvinculada da senha do SalaryPay.
     *
     * @param usuario
     * @return
     */
    @POST
    @Path("/validarEmail")
    public Response validarEmail(UsuarioRestRequest usuario) {
        return Response.status(Response.Status.OK).entity(new ResponseRestRequest()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
    }

    /**
     * Método originalmente utilizado para garantir a identificação pessoal no aplicativo SalaryPay.
     * Não é mais utilizado pois a senha do eConsig foi desvinculada da senha do SalaryPay.
     *
     * @param usuario
     * @return
     */
    @POST
    @Path("/verificarSelfieSignIn")
    public Response verificarSelfieSignIn(UsuarioRestRequest usuario) {
        return Response.status(Response.Status.OK).entity(new ResponseRestRequest()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
    }

    /**
     * Método originalmente utilizado para recuperação de senha no aplicativo SalaryPay.
     * Não é mais utilizado pois a senha do eConsig foi desvinculada da senha do SalaryPay.
     *
     * @param usuario
     * @return
     */
    @POST
    @Path("/definirSenha")
    public Response definirSenha(UsuarioRestRequest usuario) {
        return Response.status(Response.Status.OK).entity(new ResponseRestRequest()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
    }

    // DESENV-18533: Registra o log do aceite dos termos de uso de download de arquivo com dados sensíveis com os dados do dispositivo e do usuário que fez a solicitação
    // Na tb_log informações mais resumidas como a descrição da ação
    // Na tb_ocorrencia_usuario é registrado os dados do dispositivo e o tipo de ocorrencia
    @POST
    @Secured
    @Path("/registraLogDownloadArquivosDadosSensiveis")
    public Response registraLogDownloadArquivosDadosSensiveis(Map<String, String> dadosLog) {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        responsavel.setCanal(CanalEnum.REST);
        final String modelo = dadosLog.get("modelo");
        final String produto = dadosLog.get("produto");
        final String marca = dadosLog.get("marca");
        final String dispositivo_fisico = dadosLog.get("dispositivo_fisico");
        final String ip_dispositivo = dadosLog.get("ip_dispositivo");
        final String tipo_log = dadosLog.get("tipo_log");
        final String email_usuario_logado = dadosLog.get("email_usuario_logado");

        final List<String> listaDados = Arrays.asList(modelo, produto, marca, "Dispositivo físico: " + dispositivo_fisico, ip_dispositivo, "Tipo consulta: " + tipo_log, email_usuario_logado);
        final String registroLog = String.join(";", listaDados);

        try {
            // definição da ocorrência
            final String tocCodigo = CodedValues.TOC_ACEITACAO_TERMO_DOWNLOAD_DADOS_SENSIVEIS_MOBILE;

            final OcorrenciaUsuarioTransferObject ocorrencia = new OcorrenciaUsuarioTransferObject();
            ocorrencia.setOusUsuCodigo(responsavel.getUsuCodigo());
            ocorrencia.setUsuCodigo(responsavel.getUsuCodigo());
            ocorrencia.setTocCodigo(tocCodigo);
            ocorrencia.setOusIpAcesso(ip_dispositivo);
            ocorrencia.setOusObs(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.termo.aceite.arquivo.dados.sensiveis.mobile", responsavel, registroLog));

            usuarioController.createOcorrenciaUsuario(ocorrencia, responsavel);

            // definição do registro no log
            final LogDelegate log = new LogDelegate(responsavel, Log.SERVIDOR, Log.TERMO_ACEITE_DOWNLOAD_ARQUIVO_DADOS_SENSIVEIS, Log.LOG_INFORMACAO);
            log.write();
        } catch (final UsuarioControllerException e) {
            LOG.error(e.getMessage(), e);
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = e.getMessage();
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return Response.status(Response.Status.OK).entity(new ResponseRestRequest()).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
    }

    private void salvarCapturaFotosPrimeiroAcesso(String arquivo, String tarCodigo, AcessoSistema responsavel) {
        try {
            final ServidorTransferObject servidor = servidorController.findServidor(responsavel.getCodigoEntidade(), responsavel);

            final TransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.SER_CODIGO, servidor.getSerCodigo());
            criterio.setAttribute(Columns.ARQ_CONTEUDO, arquivo.getBytes());
            criterio.setAttribute(Columns.ARQ_TAR_CODIGO, tarCodigo);
            criterio.setAttribute(Columns.ASE_NOME, servidor.getSerCodigo());

            arquivoController.createArquivoServidor(criterio, responsavel);
        } catch (final ArquivoControllerException | ServidorControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
        }
    }

    private Optional<RegistroServidorTO> retornarRegistroServidorAtivo(List<RegistroServidorTO> registrosServidorAtivoOuBloqueado) {
        if (registrosServidorAtivoOuBloqueado.size() > 1) {
            final List<RegistroServidorTO> registrosServidorAtivo = registrosServidorAtivoOuBloqueado.stream().filter(status -> CodedValues.SRS_ATIVO.equals(status.getAttribute(Columns.SRS_CODIGO))).toList();

            if (!registrosServidorAtivo.isEmpty()) {
                return registrosServidorAtivo.stream().findFirst();
            }
        }

        return registrosServidorAtivoOuBloqueado.stream().findFirst();
    }
}
