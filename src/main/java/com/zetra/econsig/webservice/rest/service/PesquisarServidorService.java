package com.zetra.econsig.webservice.rest.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.request.ConsultarMargemServidorPorCsaRestRequest;
import com.zetra.econsig.webservice.rest.request.PesquisarServidorRestRequest;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;

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
 * <p>Title: PesquisarServidorService</p>
 * <p>Description: Serviço REST para pesquisa de servidor.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Path("/pesquisarServidor")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class PesquisarServidorService extends RestService {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(PesquisarServidorService.class);

    @Context
    SecurityContext securityContext;

    @POST
    @Secured
    @Path("/pesquisar")
    public Response pesquisar(PesquisarServidorRestRequest dados) {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

        if (dados == null) {
            dados = new PesquisarServidorRestRequest();
        }

        if (!responsavel.temPermissao(CodedValues.FUN_RES_MARGEM) || responsavel.isSer()) {
            return genericError(new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel));
        }

        List<TransferObject> servidores = new ArrayList<>();
        try {
            // Valida obrigatoriedade de CPF e/ou Matricula
            validaCpfMatricula(dados, responsavel);

            final ServidorDelegate serDelegate = new ServidorDelegate();

            final List<String> srsCodigo = null;
            final boolean validaPermissionario = false;
            String orgCodigo = null;

            if (responsavel.isOrg()) {
                orgCodigo = responsavel.getOrgCodigo();
            }

            // Pesquisa servidor
            servidores = serDelegate.pesquisaServidor(responsavel.getTipoEntidade(), responsavel.getCodigoEntidade(), dados.estIdentificador, dados.orgIdentificador, dados.rseMatricula, dados.serCpf, responsavel, false, srsCodigo, validaPermissionario, orgCodigo);

        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            return genericError(e);
        }

        final List<String> filter = Arrays.asList("ser_codigo", "ser_nome", "ser_cpf", "est_codigo", "est_identificador", "est_nome",
                "org_codigo", "org_identificador", "org_nome", "rse_codigo", "rse_matricula", "srs_codigo","srs_descricao");

        return Response.status(Response.Status.OK).entity(transformTOs(servidores, filter)).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
    }

    @POST
    @Secured
    @Path("/pesquisarComoCsa")
    public Response pesquisarComoCsa(PesquisarServidorRestRequest dados) {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

        if (dados == null) {
            dados = new PesquisarServidorRestRequest();
        }

        if (!responsavel.temPermissao(CodedValues.FUN_RES_MARGEM) || responsavel.isSer()) {
            return genericError(new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel));
        }

        List<TransferObject> servidores = new ArrayList<>();
        try {
            // Valida obrigatoriedade de CPF e/ou Matricula
            validaCpfMatricula(dados, responsavel);

            final ServidorDelegate serDelegate = new ServidorDelegate();

            final List<String> srsCodigo = null;
            final boolean validaPermissionario = false;
            String orgCodigo = null;

            if (responsavel.isOrg()) {
                orgCodigo = responsavel.getOrgCodigo();
            }

            // Pesquisa servidor
            servidores = serDelegate.pesquisaServidor(responsavel.getTipoEntidade(), responsavel.getCodigoEntidade(), dados.estIdentificador, dados.orgIdentificador, dados.rseMatricula, dados.serCpf, responsavel, false, srsCodigo, validaPermissionario, orgCodigo);

        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            return genericError(e);
        }

        final List<String> filter = Arrays.asList("ser_codigo", "ser_nome", "ser_cpf","ser_data_nasc", "est_codigo", "est_identificador", "est_nome",
                "org_codigo", "org_identificador", "org_nome", "rse_codigo", "rse_matricula", "srs_codigo","srs_descricao", "rse_margem_rest", "rse_margem_rest2", "rse_margem_rest3");

        return Response.status(Response.Status.OK).entity(transformTOs(servidores, filter)).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
    }

    @POST
    @Secured
    @Path("/consultarMargem")
    public Response ConsultarMargem(ConsultarMargemServidorPorCsaRestRequest dados) {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

        if (dados == null) {
            dados = new ConsultarMargemServidorPorCsaRestRequest();
        }

        if (!responsavel.temPermissao(CodedValues.FUN_CONS_MARGEM) || !responsavel.isCsa()) {
            return genericError(new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel));
        }

        List<MargemTO> margens;
        try {
            final ConsultarMargemController consultarMargemController = ApplicationContextProvider.getApplicationContext().getBean(ConsultarMargemController.class);
            margens = consultarMargemController.consultarMargem(dados.rseCodigo, null, dados.svcCodigo, dados.csaCodigo, true, false, null, true, null, responsavel);
        } catch (final ServidorControllerException e) {
            LOG.error(e.getMessage(), e);
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = e.getMessage();
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        }

        final List<String> filter = Arrays.asList("mar_codigo", "mar_codigo_pai", "mar_descricao", "mar_sequencia", "mar_exibe_csa", "mar_tipo_vlr", "mrs_margem", "mrs_margem_usada", "mrs_margem_rest");
        margens.removeIf(margem -> TextHelper.isNull(margem.getMarDescricao()));

        return Response.status(Response.Status.OK).entity(transformTOs(margens, filter)).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
    }

    /**
     * Verifica CPF e matrícula se atentem aos requisitos configurados para o sistema
     *
     * @param dados
     * @param responsavel
     * @throws ZetraException
     */
    private void validaCpfMatricula(PesquisarServidorRestRequest dados, AcessoSistema responsavel) throws ZetraException {
        final String serCpf = dados.serCpf;
        String rseMatricula = dados.rseMatricula;

        final ParametroDelegate parDelegate = new ParametroDelegate();
        final boolean requerMatriculaCpf = parDelegate.requerMatriculaCpf(responsavel);

        if (requerMatriculaCpf && ((serCpf == null) || "".equals(serCpf) || (rseMatricula == null) || "".equals(rseMatricula))) {
            throw new ZetraException("mensagem.requerMatrCpf", responsavel);
        } else if (!requerMatriculaCpf && ((serCpf == null) || "".equals(serCpf)) && ((rseMatricula == null) || "".equals(rseMatricula))) {
            throw new ZetraException("mensagem.requerMatrOuCpf", responsavel);
        } else // Verifica se a matrícula possui número de caracteres esperado
        if ((rseMatricula != null) && !"".equals(rseMatricula)) {
            try {
                final Object tamMinMatricula = ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MATRICULA, responsavel);
                if (rseMatricula.toString().length() < Integer.parseInt(tamMinMatricula.toString())) {
                    throw new ZetraException("mensagem.erro.matricula.invalida", responsavel);
                }
                final Object paramMaxMatricula = ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MATRICULA_MAX, responsavel);
                final int tamMaxMatricula = ((paramMaxMatricula != null) && !"".equals(paramMaxMatricula)) ? Integer.parseInt(paramMaxMatricula.toString()) : 0;

                if ((tamMaxMatricula > 0) && (rseMatricula.toString().length() > tamMaxMatricula)) {
                    throw new ZetraException("mensagem.erro.matricula.invalida", responsavel);
                }
            } catch (final NumberFormatException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ZetraException("mensagem.erro.parametro.sistema.matricula.tam.max.invalido", responsavel);
            }

            // Verifica se matrícula é numérica
            if (ParamSist.paramEquals(CodedValues.TPC_MATRICULA_NUMERICA, CodedValues.TPC_SIM, responsavel)) {
                try {
                    rseMatricula = String.valueOf(Long.parseLong(rseMatricula));
                } catch (final NumberFormatException ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw new UsuarioControllerException("mensagem.erro.matricula.somente.numerica", (AcessoSistema) null);
                }
            }
        }
    }

}
