package com.zetra.econsig.webservice.rest.service;

import static com.zetra.econsig.values.ApplicationResourcesKeys.MENSAGEM_ERRO_INTERNO_SISTEMA;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import com.zetra.econsig.delegate.ConsignacaoDelegate;
import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.delegate.ConvenioDelegate;
import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConvenioTransferObject;
import com.zetra.econsig.dto.entidade.CorrespondenteTransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServicoTransferObject;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.dto.parametros.ReservarMargemParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.consignacao.ReservaMargemHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.senha.SenhaHelper;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.web.filter.XSSPreventionFilter;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.request.CodigoAutorizacaoRestRequest;
import com.zetra.econsig.webservice.rest.request.ConsignacaoRestRequest;
import com.zetra.econsig.webservice.rest.request.ReservarMargemResponseRest;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

/**
 * <p>Title: ReservarMargemService</p>
 * <p>Description: Serviço REST para reserva de margem.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Path("/reservar")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class ReservarMargemService extends RestService {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ReservarMargemService.class);

    // Subdiretório onde os arquivos temporários são armazenados.
    public static final String SUBDIR_ARQUIVOS_TEMPORARIOS = "temp" + File.separator + "upload";

    @Context
    SecurityContext securityContext;

    @POST
    @Secured
    @Path("/reservarMargem")
    public Response inserirSolicitacao(ConsignacaoRestRequest dados, @Context HttpServletRequest request) throws ZetraException {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        Response resultadoValidacao = validarOperacao(responsavel, List.of(CodedValues.FUN_RES_MARGEM), null);
        if (resultadoValidacao != null) {
            return resultadoValidacao;
        }

        // Verifica se veio algum dado preenchido, e se não retorna.
        if (dados == null) {
            final ReservarMargemResponseRest responseError = new ReservarMargemResponseRest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

        // rseCodigo tem que estar preenchido, se é usuário de csa ou cor
        if ((responsavel.isCsa() || responsavel.isCor()) && ((dados.rseCodigo == null) || dados.rseCodigo.isEmpty())) {
            return genericError(new ZetraException("mensagem.rest.parametros.ausente", responsavel));
        }

        // Ignora os parâmetros de exigência de telefone/otp para o caso de SalaryPay
        if (TextHelper.isNull(dados.nseCodigo) || !(CodedValues.NSE_SALARYPAY.equals(dados.nseCodigo))) {
            boolean exigeTelefone = ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel);

            if (exigeTelefone && TextHelper.isNull(dados.serTelefone)) {
                return genericError(new ZetraException("mensagem.informe.servidor.telefone", responsavel));
            }

            exigeTelefone = ParamSist.paramEquals(CodedValues.TPC_REQUER_TEL_SER_SOLIC_EMPRESTIMO, CodedValues.TPC_SIM, responsavel);

            if (exigeTelefone && TextHelper.isNull(dados.serTelefoneSolicitacao)) {
                return genericError(new ZetraException("mensagem.informe.servidor.telefone.solicitacao", responsavel));
            }

            final boolean exigeSMSAutorizacao = ParamSist.getBoolParamSist(CodedValues.TPC_ENVIA_SMS_CODIGO_UNICO_AUTORIZACAO_CONSIGNACAO, responsavel);

            if (exigeSMSAutorizacao && !dados.otpValidado) {
                if (TextHelper.isNull(dados.otp)) {
                    return genericError(new ZetraException("mensagem.erro.codigo.autorizacao.invalido", responsavel));
                }

                final CodigoAutorizacaoService validador = new CodigoAutorizacaoService();
                final CodigoAutorizacaoRestRequest req = new CodigoAutorizacaoRestRequest();
                req.codAutorizacao = dados.otp;

                final Response result = validador.validarCodigoAutorizacao(req, request, responsavel);

                if(result.getStatus() != 200) {
                    final ResponseRestRequest responseError = new ResponseRestRequest();
                    responseError.mensagem = ((Map<String, String>)result.getEntity()).get("mensagem");
                    return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();

                }
            }
        }

        // Se o responsavel for um servidor, pega os dados deste, senão, obtém da requisição
        final String rseCodigo = (responsavel.isSer() ? responsavel.getRseCodigo() : dados.rseCodigo);
        String serCodigo = TextHelper.isNull(dados.serCodigo) ? null : dados.serCodigo;
        // Busca o servidor para validar se a informação passada é correta
        final ServidorDelegate serDelegate = new ServidorDelegate();
        RegistroServidorTO rse = null;

        if (!TextHelper.isNull(rseCodigo)) { //DESENV-20348: reservas cujo responsável é SUP podem vir com o rseCodigo nulo. A busca deste será pelo CPF.
            try {
                rse = serDelegate.findRegistroServidor(rseCodigo, responsavel);
            } catch (final ServidorControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                return genericError(ex);
            }
        } else if (!responsavel.isSup()) {
            return genericError(new ServidorControllerException("mensagem.erro.nenhum.registro.servidor.encontrado", responsavel));
        }

        if (dados.adeCarencia == null) {
            dados.adeCarencia = 0;
        }

        // para reserva solicitada pelo servidor
        if (responsavel.isSer()) {
            dados.adeIdentificador = ApplicationResourcesHelper.getMessage("rotulo.consignacao.identificador.solicitacao", responsavel);
        }
        if (TextHelper.isNull(dados.adeIdentificador)) {
            dados.adeIdentificador = "";
        }

        // Valida o convenio
        final ConvenioDelegate cnvDelegate = new ConvenioDelegate();
        ConvenioTransferObject convenio = null;
        if (responsavel.isSer()) {
            if ((dados.cnvCodigo == null) || "".equals(dados.cnvCodigo)) {
                final ReservarMargemResponseRest responseError = criarReservaResponse(dados, ApplicationResourcesHelper.getMessage("mensagem.informe.codigo.convenio", null));

                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
            }

            try {
                convenio = cnvDelegate.findByPrimaryKey(dados.cnvCodigo, responsavel);
                dados.svcCodigo = convenio.getSvcCodigo();
            } catch (final ZetraException ex) {
                LOG.error(ex.getMessage(), ex);
                final ReservarMargemResponseRest responseError = criarReservaResponse(dados, ex.getMessage());

                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
            }
        } else if (responsavel.isCsaCor()) {
            if ((dados.svcCodigo == null) || "".equals(dados.svcCodigo)) {
                final ReservarMargemResponseRest responseError = criarReservaResponse(dados, ApplicationResourcesHelper.getMessage("mensagem.informe.servico.codigo", null));

                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
            }

            // Busca o Convênio pelo Órgão do Servidor, Serviço Informado e Consignatária do usuário
            try {
                convenio = cnvDelegate.findByUniqueKey(responsavel.getCsaCodigo(), dados.svcCodigo, rse.getOrgCodigo(), responsavel);
                dados.cnvCodigo = convenio.getCnvCodigo();
            } catch (final ZetraException ex) {
                LOG.error(ex.getMessage(), ex);
                final ReservarMargemResponseRest responseError = criarReservaResponse(dados, ex.getMessage());

                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
            }
        } else if (responsavel.isSup()) {
            if (TextHelper.isNull(dados.serCpf)) {
                return genericError(new ZetraException("mensagem.rest.cpf.nao.informado", responsavel));
            }

            //DESENV-20348: atualmente a reserva via API REST com usuário SUP está disponível apenas para o SalaryFits.
            if (!(CodedValues.NSE_SALARYPAY.equals(dados.nseCodigo))) {
                return genericError(new ZetraException("mensagem.rest.nse.invalido", responsavel));
            }

            if (TextHelper.isNull(dados.svcIdentificador)) {
                return genericError(new ZetraException("mensagem.informe.svc.identificador", responsavel));
            }

            ServicoTransferObject svcTO = cnvDelegate.findServicoByIdn(dados.svcIdentificador, responsavel);
            if (CodedValues.STS_ATIVO.shortValue() !=  svcTO.getSvcAtivo().shortValue()) {
                return genericError(new ZetraException("mensagem.erro.servico.nao.encontrado", responsavel));
            }

            final List<TransferObject> lstSers = serDelegate.pesquisaServidor(AcessoSistema.ENTIDADE_SUP, null, null, null, null, dados.serCpf, responsavel, false, List.of(CodedValues.SRS_ATIVO), false);

            if ((lstSers == null) || lstSers.isEmpty()) {
                return genericError(new ZetraException("mensagem.erro.nenhum.servidor.encontrado", responsavel));
            }

            final TransferObject ownerSer = lstSers.get(0);
            dados.rseCodigo = (String) ownerSer.getAttribute(Columns.RSE_CODIGO);
            dados.rseBancoSal = (String) ownerSer.getAttribute(Columns.RSE_BANCO_SAL);
            dados.rseAgenciaSal = (String) ownerSer.getAttribute(Columns.RSE_AGENCIA_SAL);
            dados.rseContaSal = (String) ownerSer.getAttribute(Columns.RSE_CONTA_SAL);
            dados.rseBancoSal2 = (String) ownerSer.getAttribute(Columns.RSE_BANCO_SAL_2);
            dados.rseAgenciaSal2 = (String) ownerSer.getAttribute(Columns.RSE_AGENCIA_SAL_2);
            dados.rseContaSal2 = (String) ownerSer.getAttribute(Columns.RSE_CONTA_SAL_2);
            dados.rsePrazo = (Integer) ownerSer.getAttribute(Columns.RSE_PRAZO);
            dados.serDataNasc = (ownerSer.getAttribute(Columns.SER_DATA_NASC)) != null ? ownerSer.getAttribute(Columns.SER_DATA_NASC).toString() : null;

            final List<TransferObject> listCnvs = cnvDelegate.lstConvenios(null, null, svcTO.getSvcCodigo(), (String) ownerSer.getAttribute(Columns.ORG_CODIGO), true, responsavel);

            if ((listCnvs == null) || listCnvs.isEmpty()) {
                return genericError(new ZetraException("mensagem.convenioNaoEncontrado", responsavel));
            }
            final TransferObject cnvTO = listCnvs.get(0);

            dados.cnvCodigo = (String) cnvTO.getAttribute(Columns.CNV_CODIGO);
            convenio = new ConvenioTransferObject(dados.cnvCodigo);
            convenio.setCnvCodVerba((String) cnvTO.getAttribute(Columns.CNV_COD_VERBA));
            convenio.setCnvCodVerbaRef((String) cnvTO.getAttribute(Columns.CNV_COD_VERBA_REF));
            convenio.setCnvCodVerbaFerias((String) cnvTO.getAttribute(Columns.CNV_COD_VERBA_FERIAS));
            convenio.setSvcCodigo(svcTO.getSvcCodigo());
            convenio.setOrgCodigo((String) cnvTO.getAttribute(Columns.CNV_ORG_CODIGO));
            convenio.setCsaCodigo((String) cnvTO.getAttribute(Columns.CNV_CSA_CODIGO));
            serCodigo = (String) ownerSer.getAttribute(Columns.SER_CODIGO);

        } else {
            // Se não for um dos casos acima, sai
            final ReservarMargemResponseRest responseError = criarReservaResponse(dados, ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_INTERNO_SISTEMA, null));

            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

        if (dados.adeVlr == null) {
            final ReservarMargemResponseRest responseError = criarReservaResponse(dados, ApplicationResourcesHelper.getMessage("mensagem.informe.ade.valor", null));

            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

        // Se a senha do servidor for obrigatório na reserva, verifica se foi informada e valida-a
        final ParametroDelegate paramDelegate = new ParametroDelegate();
        try {
            // Verifica o parâmetro de obrigatoriedade de senha na reserva
            if (paramDelegate.senhaServidorObrigatoriaReserva(rseCodigo, convenio.getSvcCodigo(), convenio.getCsaCodigo(), responsavel)) {
                if (TextHelper.isNull(dados.serSenha)) {
                    final ReservarMargemResponseRest responseError = criarReservaResponse(dados, ApplicationResourcesHelper.getMessage("mensagem.informe.ser.senha", null));

                    return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
                }

                // Verifica se a senha está correta
                try {
                    SenhaHelper.validarSenhaServidor(rseCodigo, dados.serSenha, request.getRemoteAddr(), null, convenio.getSvcCodigo(), true, false, responsavel);
                } catch (final UsuarioControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                    final ReservarMargemResponseRest responseError = criarReservaResponse(dados, ex.getMessage());

                    return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
                }
            }
        } catch (final ParametroControllerException ex) {
            // Erro na verificação do parâmetro de senha do servidor
            LOG.error(ex.getMessage(), ex);
            final ReservarMargemResponseRest responseError = criarReservaResponse(dados, ex.getMessage());

            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

        // Parâmetros de serviços necessários
        final List<String> tpsCodigos = new ArrayList<>();
        tpsCodigos.add(CodedValues.TPS_EXIBE_CIDADE_CONFIRMACAO_SOLICITACAO);

        try {
            final ParametroDelegate parDelegate = new ParametroDelegate();
            final ParamSvcTO paramSvc = parDelegate.selectParamSvcCse(convenio.getSvcCodigo(), tpsCodigos, responsavel);
            final String paramExibeCampoCidade = paramSvc.getTpsExibeCidadeConfirmacaoSolicitacao();
            if (!TextHelper.isNull(paramExibeCampoCidade) && (CodedValues.EXIBIR_CIDADE_CONFIRMACAO_SOLICITACAO_OBRIGATORIO.equals(paramExibeCampoCidade)) && TextHelper.isNull(dados.cidCod)) {
                final ReservarMargemResponseRest responseError = criarReservaResponse(dados, ApplicationResourcesHelper.getMessage("mensagem.informe.cidade.assinatura.contrato", null));

                return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
            }
        } catch (final ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            final ReservarMargemResponseRest responseError = criarReservaResponse(dados, ex.getMessage());

            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }

        if (!TextHelper.isNull(dados.corIdentificador)) {
            try {
                final ConsignatariaDelegate consignatariaDelegate = new ConsignatariaDelegate();
                final CorrespondenteTransferObject cor = consignatariaDelegate.findCorrespondenteByIdn(dados.corIdentificador, convenio.getCsaCodigo(), responsavel);
                dados.corCodigo = cor.getCorCodigo();
            } catch (final ConsignatariaControllerException consignatariaControllerException) {
                final ReservarMargemResponseRest responseError = criarReservaResponse(dados, consignatariaControllerException.getMessage());

                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
            }
        }

        String adeCodigo;
        TransferObject boleto;
        try {
            // recupera parâmetros de serviço
            final List<String> parametrosSvc = new ArrayList<>();
            parametrosSvc.add(CodedValues.TPS_CAD_VALOR_TAC);
            parametrosSvc.add(CodedValues.TPS_CAD_VALOR_IOF);
            parametrosSvc.add(CodedValues.TPS_CAD_VALOR_LIQUIDO_LIBERADO);
            parametrosSvc.add(CodedValues.TPS_CAD_VALOR_MENSALIDADE_VINC);
            parametrosSvc.add(CodedValues.TPS_INF_BANCARIA_OBRIGATORIA);
            parametrosSvc.add(CodedValues.TPS_VLR_LIQ_TAXA_JUROS);
            parametrosSvc.add(CodedValues.TPS_EXIGE_SEGURO_PRESTAMISTA);
            parametrosSvc.add(CodedValues.TPS_POSSUI_CORRECAO_VALOR_PRESENTE);
            parametrosSvc.add(CodedValues.TPS_POSSUI_CORRECAO_SALDO_DEVEDOR);
            parametrosSvc.add(CodedValues.TPS_BUSCA_BOLETO_EXTERNO);
            parametrosSvc.add(CodedValues.TPS_VALIDAR_DATA_NASCIMENTO_NA_RESERVA);
            parametrosSvc.add(CodedValues.TPS_VALIDAR_INF_BANCARIA_NA_RESERVA);
            parametrosSvc.add(CodedValues.TPS_CLASSE_JAVA_PROC_ESPECIFICO_RESERVA);
            parametrosSvc.add(CodedValues.TPS_DEFERE_AUT_CONTRATO_INCLUIDO_SER);
            parametrosSvc.add(CodedValues.TPS_ANEXO_INCLUSAO_CONTRATOS_OBRIGATORIO_CSE_ORG_SUP);
            parametrosSvc.add(CodedValues.TPS_ANEXO_INCLUSAO_CONTRATOS_OBRIGATORIO_CSA_COR);
            parametrosSvc.add(CodedValues.TPS_ANEXO_INCLUSAO_CONTRATOS_OBRIGATORIO_SER);
            parametrosSvc.add(CodedValues.TPS_INCIDE_MARGEM);
            parametrosSvc.add(CodedValues.TPS_INTEGRA_FOLHA);
            parametrosSvc.add(CodedValues.TPS_TIPO_VLR);
            parametrosSvc.add(CodedValues.TPS_PERCENTUAL_BASE_CALC_DESCONTO_EM_FILA);
            parametrosSvc.add(CodedValues.TPS_BASE_CALC_DESCONTO_EM_FILA);
            final ParamSvcTO paramSvcCse = paramDelegate.selectParamSvcCse(convenio.getSvcCodigo(), parametrosSvc, responsavel);
            // valida dados da reserva
            validaReserva(convenio, dados, paramSvcCse, responsavel);
            // realiza reserva de margem
            adeCodigo = realizaReserva(convenio, dados, paramSvcCse, responsavel);
            // busca boleto
            boleto = !responsavel.isSup() && !responsavel.isCsaCor() ? buscaNovaAutorizacao(adeCodigo, responsavel) : buscaNovaAutorizacao(adeCodigo, serCodigo, convenio.getOrgCodigo(), responsavel);
           
        
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            final ReservarMargemResponseRest responseError = criarReservaResponse(dados, ex.getMessage());

            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }
        return Response.status(Response.Status.OK).entity(transformTO(boleto, null)).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
    }

    private ReservarMargemResponseRest criarReservaResponse(ConsignacaoRestRequest dados, String mensagem) {
        final ReservarMargemResponseRest responseError = new ReservarMargemResponseRest();

        responseError.adePrazo = dados.adePrazo;
        responseError.adeVlr = dados.adeVlr;
        responseError.cnvCodigo = dados.cnvCodigo;
        responseError.corIdentificador = dados.corIdentificador;
        responseError.serCpf = dados.serCpf;
        responseError.mensagem = mensagem;
        return responseError;
    }

    private String realizaReserva(ConvenioTransferObject convenio, ConsignacaoRestRequest dados, ParamSvcTO paramSvcCse, AcessoSistema responsavel) throws ZetraException, IOException {

        final ConsignacaoDelegate consigDelegate = new ConsignacaoDelegate();

        final boolean deferimentoAutoSolicSer = paramSvcCse.isTpsDeferimentoAutoSolicitacaoServidor();
        final Short adeIncMargem = paramSvcCse.getTpsIncideMargem();
        final Short adeIntFolha = paramSvcCse.getTpsIntegraFolha();
        final String adeTipoVlr = paramSvcCse.getTpsTipoVlr();

        String sadCodigo = null;
        if (responsavel.isSer()) {
            sadCodigo = (deferimentoAutoSolicSer ? CodedValues.SAD_DEFERIDA : CodedValues.SAD_SOLICITADO);
        }

        final ReservarMargemParametros reservaParam = new ReservarMargemParametros();

        final ParametroDelegate paramDelegate = new ParametroDelegate();

        // Se o responsavel for um servidor, pega os dados deste
        if (responsavel.isSer()) {
            reservaParam.setRseCodigo(responsavel.getRseCodigo());
        } else if (responsavel.isCsa() || responsavel.isCor() || responsavel.isSup()) { //DESENV-20348: é permitido a usuários SUP reservar margem via API REST para serviços de natureza salarypay.
            // Se o responsavel for Csa ou Cor, usa o rseCodigo informado
            reservaParam.setRseCodigo(dados.rseCodigo);
        } else {
            // Se não for um dos casos acima, sai
            throw new ViewHelperException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel);
        }

        if (responsavel.isCor()) {
            reservaParam.setCorCodigo(responsavel.getCodigoEntidade());
        } else {
            reservaParam.setCorCodigo(dados.corCodigo);
        }

        reservaParam.setAdeVlr(dados.adeVlr);
        reservaParam.setAdePrazo(dados.adePrazo);
        reservaParam.setAdeCarencia(paramDelegate.calcularAdeCarenciaDiaCorteCsa(dados.adeCarencia, convenio.getCsaCodigo(), convenio.getOrgCodigo(), responsavel));
        reservaParam.setAdeIdentificador(dados.adeIdentificador);
        reservaParam.setCnvCodigo(dados.cnvCodigo);
        reservaParam.setSadCodigo(sadCodigo);
        reservaParam.setAdeIntFolha(adeIntFolha);
        reservaParam.setAdeTipoVlr(adeTipoVlr);
        reservaParam.setAdeIncMargem(adeIncMargem);
        reservaParam.setAdeIndice(dados.adeIndice);
        reservaParam.setAdeVlrTac(dados.adeVlrTac);
        reservaParam.setAdeVlrIof(dados.adeVlrIof);
        final BigDecimal adeVlrLiquido = (dados.adeVlrLiquido != null) ? dados.adeVlrLiquido : dados.valorLiberado;
        reservaParam.setAdeVlrLiquido(adeVlrLiquido);
        reservaParam.setAdeVlrMensVinc(dados.adeVlrMensVinc);
        reservaParam.setSerAtivo(Boolean.TRUE);
        reservaParam.setCnvAtivo(Boolean.TRUE);
        reservaParam.setSerCnvAtivo(Boolean.TRUE);
        reservaParam.setSvcAtivo(Boolean.TRUE);
        reservaParam.setCsaAtivo(Boolean.TRUE);
        reservaParam.setOrgAtivo(Boolean.TRUE);
        reservaParam.setEstAtivo(Boolean.TRUE);
        reservaParam.setCseAtivo(Boolean.TRUE);
        reservaParam.setValidar(Boolean.FALSE);
        reservaParam.setAcao("RESERVAR");
        reservaParam.setAdeTaxaJuros(dados.adeTaxaJuros);
        reservaParam.setPermitirValidacaoTaxa(Boolean.TRUE);
        reservaParam.setCdeVlrLiberado(dados.valorLiberado);
        reservaParam.setCdeTxtContato("");
        reservaParam.setAdeBanco(dados.numBanco);
        reservaParam.setAdeAgencia(dados.numAgencia);
        reservaParam.setAdeConta(dados.numConta);
        reservaParam.setMobileEconsig(dados.isMobile == null ? Boolean.FALSE : dados.isMobile);
        if(!TextHelper.isNull(dados.TDA_40)) {
            reservaParam.setDadoAutorizacao("40",XSSPreventionFilter.stripXSS(dados.TDA_40));
        }
        if(!TextHelper.isNull(dados.TDA_41)) {
            reservaParam.setDadoAutorizacao("41", XSSPreventionFilter.stripXSS(new String( Base64.decodeBase64(dados.TDA_41))));
        }
        if(!TextHelper.isNull(dados.TDA_48)) {
            reservaParam.setDadoAutorizacao(CodedValues.TDA_NOME_ESTABELECIMENTO_CARTAO, XSSPreventionFilter.stripXSS(dados.TDA_48));
        }
        if(!TextHelper.isNull(dados.TDA_49)) {
            reservaParam.setDadoAutorizacao(CodedValues.TDA_INFO_ESTABELECIMENTO_CARTAO, XSSPreventionFilter.stripXSS(dados.TDA_49));
        }
        if(!TextHelper.isNull(dados.TDA_90)) {
            reservaParam.setDadoAutorizacao(CodedValues.TDA_TRANSFERENCIA_TAXA, XSSPreventionFilter.stripXSS(dados.TDA_90));
        }
        if(!TextHelper.isNull(dados.TDA_91)) {
            reservaParam.setDadoAutorizacao(CodedValues.TDA_TRANSFERENCIA_VALOR_CREDITADO, XSSPreventionFilter.stripXSS(dados.TDA_91));
        }
        reservaParam.setNomeResponsavel(responsavel.getUsuNome());

        if (responsavel.isSer()) {
            reservaParam.setComSerSenha(Boolean.TRUE);
        } else {
            reservaParam.setComSerSenha(!TextHelper.isNull(dados.serSenha));
            reservaParam.setSerSenha(dados.serSenha);
        }
        if (dados.serTelefoneSolicitacao != null) {
            reservaParam.setTdaTelSolicitacaoSer(dados.serTelefoneSolicitacao);
        }

        // cidade da ade
        if (dados.cidCod != null) {
            reservaParam.setCidCodigo(dados.cidCod);
        }

        Date periodo = null;

        if (!TextHelper.isNull(dados.periodo) &&
                ((ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, responsavel) &&
                        ParamSist.paramEquals(CodedValues.TPC_PERMITE_ESCOLHER_PERIODO_EM_AGRUPAMENTO, CodedValues.TPC_SIM, responsavel)) ||
                        ParamSist.paramEquals(CodedValues.TPC_HABILITA_EXTENSAO_PERIODO_FOLHA_AJUSTES, CodedValues.TPC_SIM, responsavel))) {
            final String strOcaPeriodo = dados.periodo;
            try {
                if (strOcaPeriodo.matches("([0-9]{2})/([0-9]{4})")) {
                    periodo = DateHelper.parsePeriodString(strOcaPeriodo);
                    final String ocaPeriodo = DateHelper.format(periodo, "yyyy-MM-dd");
                    reservaParam.setOcaPeriodo(ocaPeriodo);
                } else {
                    throw new ZetraException("mensagem.erro.periodo.invalido", responsavel);
                }
            } catch (final ParseException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ZetraException("mensagem.erro.periodo.invalido", responsavel);
            }
        }

        if ((dados.anexos != null) && (dados.anexos.length > 0)) {
            final String hashDir = UUID.randomUUID().toString();
            final byte[] arq = Base64.decodeBase64(dados.anexos[0].get("data"));
            final String path = ParamSist.getDiretorioRaizArquivos() + File.separatorChar + SUBDIR_ARQUIVOS_TEMPORARIOS + File.separatorChar + "anexo"
                    + File.separatorChar + hashDir + File.separatorChar + dados.anexos[0].get("nome");
            final File arquivoAnexo = new File(path);

            if (arquivoAnexo.exists()) {
                throw new AutorizacaoControllerException("mensagem.erro.anexo.ja.existe", responsavel);
            }

            try {
                FileUtils.writeByteArrayToFile(arquivoAnexo, arq);
                reservaParam.setAnexo(arquivoAnexo);
                reservaParam.setIdAnexo(hashDir);
            } catch (java.io.IOException e) {
                throw new AutorizacaoControllerException("mensagem.erro.interno.anexo.nao.copiado", responsavel);
            }

        }

        /* TODO: verificar se esses dados devem ser enviados na reserva de serviços de naturezas que não possuem taxas
        reservaParam.setAdeVlrSegPrestamista((BigDecimal) adeVlrSegPrestamista);
        reservaParam.setAdeDtHrOcorrencia(adeDtHrOcorrencia);
        reservaParam.setCftCodigo(cftCodigo);
         */

        String adeCodigo;
        try {
            adeCodigo = consigDelegate.reservarMargem(reservaParam, responsavel);

            // atualiza dados do servidor, se necessario
            if (responsavel.isSer()) {
                final ServidorDelegate serDelegate = new ServidorDelegate();

                final String serCodigo = responsavel.getSerCodigo();
                final ServidorTransferObject servidorUpd = new ServidorTransferObject(serCodigo);

                if (dados.serEndereco != null) {
                    servidorUpd.setSerEnd(dados.serEndereco);
                }
                if (dados.serComplemento != null) {
                    servidorUpd.setSerCompl(dados.serComplemento);
                }
                if (dados.serBairro != null) {
                    servidorUpd.setSerBairro(dados.serBairro);
                }
                if (dados.serCidade != null) {
                    servidorUpd.setSerCidade(dados.serCidade);
                }
                if (dados.serUf != null) {
                    servidorUpd.setSerUf(dados.serUf);
                }
                if (dados.serCep != null) {
                    servidorUpd.setSerCep(dados.serCep);
                }
                if (dados.serNro != null) {
                    servidorUpd.setSerNro(dados.serNro);
                }
                if (dados.serTelefone != null) {
                    servidorUpd.setSerTel(dados.serTelefone);
                }
                serDelegate.updateServidor(servidorUpd, responsavel);

                // Monta dados do registro servidor para ser alterado
                if (dados.rseMunicipioLotacao != null) {
                    final Object rseMunicipioLotacao = dados.rseMunicipioLotacao;
                    final RegistroServidorTO registroServidorUpd = new RegistroServidorTO(responsavel.getRseCodigo());
                    registroServidorUpd.setRseMunicipioLotacao((String) rseMunicipioLotacao);
                    serDelegate.updateRegistroServidor(registroServidorUpd, false, false, false, responsavel);
                }
            }
        } catch (AutorizacaoControllerException | ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }

        return adeCodigo;
    }

    private void validaReserva(ConvenioTransferObject convenio, ConsignacaoRestRequest dados, ParamSvcTO paramSvcCse, AcessoSistema responsavel) throws ZetraException {
        final String svcCodigo = convenio.getSvcCodigo();

        boolean validarMargemReserva = true;
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_DESCONTO_EM_FILA, CodedValues.TPC_SIM, responsavel)) {
            if (!TextHelper.isNull(paramSvcCse.getTpsBaseCalcDescontoEmFila()) && !TextHelper.isNull(paramSvcCse.getTpsPercentualBaseCalcDescontoEmFila())) {
                // Se o sistema permite módulo de desconto em fila e o serviço está configurado para realizar a fila
                // então não realiza validação de margem na reserva, pois esta não irá incidir na margem
                validarMargemReserva = false;
            }
        }

        // Recupera os dados do servidor
        final ServidorDelegate serDelegate = new ServidorDelegate();
        String rseCodigo;
        if (responsavel.isSer()) {
            rseCodigo = responsavel.getRseCodigo();
        } else if (responsavel.isCsaCor() || responsavel.isSup()) { //DESENV-20348: aceitas reservas feitas via API REST de usuários suportes para natureza salarypay
            rseCodigo = dados.rseCodigo;
        } else {
            // Se não for um dos casos acima, sai
            throw new ViewHelperException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel);
        }

        TransferObject servidor = null;
        if (!responsavel.isSup()) {
            servidor = serDelegate.buscaServidor(rseCodigo, responsavel);
        } else {
            servidor = new CustomTransferObject();
            servidor.setAttribute(Columns.SER_DATA_NASC, dados.serDataNasc);
            servidor.setAttribute(Columns.RSE_PRAZO, dados.rsePrazo);
            servidor.setAttribute(Columns.RSE_BANCO_SAL, dados.rseBancoSal);
            servidor.setAttribute(Columns.RSE_AGENCIA_SAL, dados.rseAgenciaSal);
            servidor.setAttribute(Columns.RSE_CONTA_SAL, dados.rseContaSal);
            servidor.setAttribute(Columns.RSE_BANCO_SAL_2, dados.rseBancoSal2);
            servidor.setAttribute(Columns.RSE_AGENCIA_SAL_2, dados.rseAgenciaSal2);
            servidor.setAttribute(Columns.RSE_CONTA_SAL_2, dados.rseContaSal2);

        }

        // Valida data de nascimento
        if (paramSvcCse.isTpsValidarDataNascimentoNaReserva()) {
            // Valida a data de nascimento do servidor de acordo com a data informada pelo usuário
            try {
                final String paramDataNasc = servidor.getAttribute(Columns.SER_DATA_NASC) != null ? DateHelper.reformat(servidor.getAttribute(Columns.SER_DATA_NASC).toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern()) : "";
                if (!paramDataNasc.equals(dados.serDataNasc)) {
                    throw new ZetraException("mensagem.dataNascNaoConfere", responsavel);
                }
            } catch (final ParseException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ZetraException("mensagem.dataNascNaoConfere", responsavel);
            }
        }

        // Validar dados bancários
        final boolean serInfBancariaObrigatoria = paramSvcCse.isTpsInfBancariaObrigatoria();
        final boolean validarInfBancaria = paramSvcCse.isTpsValidarInfBancariaNaReserva();
        final String rseBancoSal = servidor.getAttribute(Columns.RSE_BANCO_SAL) != null ? JspHelper.removePadrao(servidor.getAttribute(Columns.RSE_BANCO_SAL).toString(), "0", JspHelper.ESQ) : "";
        final String rseAgenciaSal = servidor.getAttribute(Columns.RSE_AGENCIA_SAL) != null ? JspHelper.removePadrao(servidor.getAttribute(Columns.RSE_AGENCIA_SAL).toString(), "0", JspHelper.ESQ) : "";
        final String rseContaSal = servidor.getAttribute(Columns.RSE_CONTA_SAL) != null ? JspHelper.removePadrao(servidor.getAttribute(Columns.RSE_CONTA_SAL).toString(), "0", JspHelper.ESQ) : "";
        final String rseBancoSalAlt = servidor.getAttribute(Columns.RSE_BANCO_SAL_2) != null ? JspHelper.removePadrao(servidor.getAttribute(Columns.RSE_BANCO_SAL_2).toString(), "0", JspHelper.ESQ) : "";
        final String rseAgenciaSalAlt = servidor.getAttribute(Columns.RSE_AGENCIA_SAL_2) != null ? JspHelper.removePadrao(servidor.getAttribute(Columns.RSE_AGENCIA_SAL_2).toString(), "0", JspHelper.ESQ) : "";
        final String rseContaSalAlt = servidor.getAttribute(Columns.RSE_CONTA_SAL_2) != null ? JspHelper.removePadrao(servidor.getAttribute(Columns.RSE_CONTA_SAL_2).toString(), "0", JspHelper.ESQ) : "";
        if (serInfBancariaObrigatoria && validarInfBancaria) {
            // Se as informações bancárias são obrigatórias e devem ser válidas,
            // então valida as informações digitadas pelo usuário
            if ((!TextHelper.formataParaComparacao(rseBancoSal).equals(TextHelper.formataParaComparacao(dados.numBanco)) || !TextHelper.formataParaComparacao(rseAgenciaSal).equals(TextHelper.formataParaComparacao(dados.numAgencia)) || !TextHelper.formataParaComparacao(rseContaSal).equals(TextHelper.formataParaComparacao(dados.numConta))) && // somente se a duas não forem iguais
                    (!TextHelper.formataParaComparacao(rseBancoSalAlt).equals(TextHelper.formataParaComparacao(dados.numBanco)) || !TextHelper.formataParaComparacao(rseAgenciaSalAlt).equals(TextHelper.formataParaComparacao(dados.numAgencia)) || !TextHelper.formataParaComparacao(rseContaSalAlt).equals(TextHelper.formataParaComparacao(dados.numConta)))) {
                throw new ZetraException("mensagem.informacaoBancariaIncorreta", responsavel);
            }
        }

        final CustomTransferObject reserva = new CustomTransferObject();
        reserva.setAttribute("ADE_PRAZO", dados.adePrazo);
        reserva.setAttribute("ADE_CARENCIA", dados.adeCarencia);
        reserva.setAttribute("ADE_VLR", dados.adeVlr);
        // Se o responsavel for um servidor, pega os dados deste
        if (responsavel.isSer()) {
            reserva.setAttribute("RSE_CODIGO", responsavel.getRseCodigo());
            reserva.setAttribute("RSE_PRAZO", responsavel.getRsePrazo());
        } else if (responsavel.isCsa() || responsavel.isCor() || responsavel.isSup()) { //DESENV-20348: permite usuários SUP reservar margem via API REST para natureza salarypay
            // Se o responsavel for Csa ou Cor, recupera os dados do servidor informado
            reserva.setAttribute("RSE_CODIGO", dados.rseCodigo);
            reserva.setAttribute("RSE_PRAZO", servidor.getAttribute(Columns.RSE_PRAZO));
        } else {
            // Se não for um dos casos acima, sai
            throw new ViewHelperException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel);
        }
        reserva.setAttribute("SVC_CODIGO", svcCodigo);
        reserva.setAttribute("CSE_CODIGO", CodedValues.CSE_CODIGO_SISTEMA);
        reserva.setAttribute("ADE_IDENTIFICADOR", dados.adeIdentificador);
        reserva.setAttribute("OPERACAO", CodedValues.OP_INSERIR_SOLICITACAO);

        ReservaMargemHelper.validaReserva(convenio, reserva, responsavel, false, validarMargemReserva, true);
    }
}
