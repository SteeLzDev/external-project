package com.zetra.econsig.webservice.rest.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.zetra.econsig.delegate.ConsignacaoDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConvenioTransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.parametros.RenegociarConsignacaoParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.cartaocredito.ValidadorCartaoCreditoController;
import com.zetra.econsig.service.consignacao.AlongarConsignacaoControllerBean;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.request.ConsignacaoRestRequest;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;

import jakarta.mail.MessagingException;
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
 * <p>Title: PortabilidadeConsignacaoService</p>
 * <p>Description: Serviço REST para portabilidade.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Path("/portabilidade")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class PortabilidadeConsignacaoService extends RestService {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(PortabilidadeConsignacaoService.class);
    private static final String MEDIA_TYPE = MediaType.APPLICATION_JSON + "; charset=UTF-8";

	@Context
	SecurityContext securityContext;
     
	@POST
	@Secured
	@Path("/confirmar")
	public Response solicitarPortabilidade(ConsignacaoRestRequest dados) {
		final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

	    final boolean temPermissaoSolicitar = responsavel.temPermissao(CodedValues.FUN_SOLICITAR_PORTABILIDADE);
	    if(!temPermissaoSolicitar) {
	        return genericError(new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel));
	    }

		if (dados == null) {
			dados = new ConsignacaoRestRequest();
		}

		if (TextHelper.isNull(dados.adeCarencia)) {
            dados.adeCarencia = 0;
        }

        if (TextHelper.isNull(dados.adeIdentificador)) {
            dados.adeIdentificador = ApplicationResourcesHelper.getMessage("rotulo.consignacao.identificador.solicitacao", responsavel);
        }

		try {
            validaCamposObrigatorios(dados, responsavel);
        } catch (final ZetraException e) {
            LOG.error(e.getMessage(), e);
            return genericError(e);
        }

        final ConsultarMargemController consultarMargemController = ApplicationContextProvider.getApplicationContext().getBean(ConsultarMargemController.class);
		try {
			if (!consultarMargemController.servidorTemMargem(responsavel.getRseCodigo(), dados.adeVlr, dados.svcCodigo, true,
					responsavel)) {
				return genericError(new ZetraException("mensagem.margemInsuficiente", responsavel));
			}
		} catch (final ServidorControllerException e) {
		    LOG.error(e.getMessage(), e);
			return genericError(e);
		}

		String adeCodigo;
        TransferObject boleto = new CustomTransferObject();
        try {
            adeCodigo = realizaReserva(dados, responsavel);
            boleto = buscaNovaAutorizacao(adeCodigo, responsavel);
            EnviaEmailHelper.enviaBoleto(boleto, responsavel);
        } catch (final MessagingException e) {
           //se ocorrer um erro no envio de email ignorar e deixar o processo seguir
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            return genericError(e);
        }

        return Response.status(Response.Status.OK).entity(transformTO(boleto, null)).header(HttpHeaders.CONTENT_TYPE, MEDIA_TYPE).build();
	}

	private void validaCamposObrigatorios(ConsignacaoRestRequest dados, AcessoSistema responsavel) throws ZetraException {
		final BigDecimal adeVlr = dados.adeVlr;
		final BigDecimal vlrLiberado = dados.valorLiberado;

		if (adeVlr == null) {
			if (vlrLiberado == null) {
				throw new ZetraException("mensagem.informe.valor.parcela.ou.valor.liberado", responsavel);
			}
        	if (vlrLiberado.doubleValue() <= 0) {
        		throw new ZetraException("mensagem.erro.valor.liberado.maior.zero", responsavel);
        	}
		}

        if (((dados.cnvCodigo == null) && "".equals(dados.cnvCodigo)) && ((dados.svcCodigo == null) || "".equals(dados.svcCodigo))) {
            throw new ZetraException("mensagem.informe.verba.ou.servico", responsavel);
        }

        if(((dados.csaCodigo == null) && "".equals(dados.csaCodigo))){
            throw new ZetraException("mensagem.erro.nenhuma.consignataria.encontrada", responsavel);
        }

        if ((dados.adePrazo == null) || (dados.adePrazo <= 0)) {
            throw new ZetraException("mensagem.informe.ade.prazo", responsavel);
        }

        if (dados.adePrazo < 0) {
            throw new ZetraException("mensagem.qtdParcelasInvalida", responsavel);
        }

        if ((dados.adeCodigosRenegociacao == null) || dados.adeCodigosRenegociacao.isEmpty()) {
            throw new ZetraException("mensagem.erro.contratos.renegociados.obrigatorio.rest", responsavel);
        }
	}

	private String realizaReserva(ConsignacaoRestRequest dados, AcessoSistema responsavel) throws ZetraException, IOException {
        final String adePeriodicidade = PeriodoHelper.getPeriodicidadeFolha(responsavel);

		final ConsignacaoDelegate consigDelegate = new ConsignacaoDelegate();

		final ConvenioController convenioController = ApplicationContextProvider.getApplicationContext().getBean(ConvenioController.class);
		ConvenioTransferObject convenio = null;
		if(!TextHelper.isNull(dados.cnvCodigo)) {
		    convenio = convenioController.findByPrimaryKey(dados.cnvCodigo, responsavel);
		} else {
		    convenio = convenioController.findByUniqueKey(dados.csaCodigo, dados.svcCodigo, responsavel.getOrgCodigo(), responsavel);
		}

		final RenegociarConsignacaoParametros renegociarParam = new RenegociarConsignacaoParametros();
        renegociarParam.setTipo(responsavel.getTipoEntidade());
        renegociarParam.setRseCodigo(responsavel.getRseCodigo());
        renegociarParam.setAdeVlr(dados.adeVlr);
        renegociarParam.setCorCodigo(dados.corCodigo);
        renegociarParam.setAdePrazo(dados.adePrazo);
        renegociarParam.setAdeCarencia(dados.adeCarencia);
        renegociarParam.setAdeIdentificador(dados.adeIdentificador);
        renegociarParam.setCnvCodigo(convenio.getCnvCodigo());
        renegociarParam.setAdeIndice(dados.adeIndice);
        renegociarParam.setAdeVlrTac(dados.adeVlrTac);
        renegociarParam.setAdeVlrIof(dados.adeVlrIof);
        renegociarParam.setAdeVlrLiquido(dados.adeVlrLiquido);
        renegociarParam.setAdeVlrMensVinc(dados.adeVlrMensVinc);
        renegociarParam.setAdeTaxaJuros(dados.adeTaxaJuros);
        renegociarParam.setAdeCodigosRenegociacao(dados.adeCodigosRenegociacao);
        renegociarParam.setCftCodigo(dados.cftCodigo);
        renegociarParam.setCdeVlrLiberado(dados.valorLiberado);
        renegociarParam.setCdeTxtContato("");
        renegociarParam.setAdeBanco(dados.numBanco);
        renegociarParam.setAdeAgencia(dados.numAgencia);
        renegociarParam.setAdeConta(dados.numConta);
        renegociarParam.setAdePeriodicidade(adePeriodicidade);
        renegociarParam.setDtjCodigo(dados.dtjCodigo);
        renegociarParam.setComSerSenha(true);
        if (!TextHelper.isNull(dados.serTelefoneSolicitacao)) {
            renegociarParam.setTdaTelSolicitacaoSer(dados.serTelefoneSolicitacao);
        }

        renegociarParam.setCompraContrato(Boolean.TRUE);

        final AlongarConsignacaoControllerBean solicitarPortabilidadeControllerBean = ApplicationContextProvider.getApplicationContext().getBean(AlongarConsignacaoControllerBean.class);

		String adeCodigoNovo;
		try {
		    adeCodigoNovo = solicitarPortabilidadeControllerBean.renegociar(renegociarParam, responsavel);
			// inclui ocorrência de operação
			consigDelegate.criaOcorrenciaADE(adeCodigoNovo, CodedValues.TOC_OPERACAO_REST, ApplicationResourcesHelper.getMessage("mensagem.informacao.insercao.compra.rest", responsavel), responsavel);
		} catch (final AutorizacaoControllerException e) {
		    LOG.error(e.getMessage(), e);
			throw e;
		}
		return adeCodigoNovo;
	}

	@POST
    @Secured
    @Path("/listar")
    public Response listarPassiveisCompra(ConsignacaoRestRequest dados) {
	    final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        final String rseCodigo = responsavel.getRseCodigo();

        final CustomTransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_OPERACAO", (dados.tipoOperacao == null || dados.tipoOperacao.isEmpty()) ? "comprar" : dados.tipoOperacao);
        criterio.setAttribute(Columns.SVC_CODIGO, dados.svcCodigo);
        criterio.setAttribute(Columns.CSA_CODIGO, dados.csaCodigo);

        final List<String> filter = Arrays.asList("ade_numero", "ade_vlr", "ade_data", "ade_carencia", "ade_codigo", "cnv_cod_verba", "sad_codigo", "sad_descricao", "ade_prd_pagas", "csa_codigo", "ade_data", "csa_nome", "ade_identificador", "ade_ano_mes_ini", "ade_ano_mes_fim", "svc_codigo", "svc_descricao", "svc_identificador", "ade_vlr_liquido", "ade_prazo", "ade_taxa_juros", "ade_tipo_vlr", "cft_vlr", "cft_vlr_anual", "usu_login", "saldo_devedor", "tem_solicitacao_saldo_devedor", "tem_solicitacao_saldo_devedor_respondida", "permite_cadastro_saldo_devedor", "cnv_codigo", "isReservaCartao");

        final PesquisarConsignacaoController pesquisarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);
        final ParametroController parametroController = ApplicationContextProvider.getApplicationContext().getBean(ParametroController.class);
                final ValidadorCartaoCreditoController validadorCartaoCreditoController =  ApplicationContextProvider.getApplicationContext().getBean(ValidadorCartaoCreditoController.class);
        List<TransferObject> contratos;

        try {
            contratos = pesquisarConsignacaoController.pesquisaAutorizacao(AcessoSistema.ENTIDADE_CSA, dados.csaCodigo, rseCodigo, null, null, CodedValues.SAD_CODIGOS_PORTABILIDADE, null, -1, -1, criterio, responsavel);
            contratos = parametroController.filtraAdeRestringePortabilidade(contratos, rseCodigo, dados.svcCodigo, responsavel);
            contratos = validadorCartaoCreditoController.determinaReservaCartao(contratos);
            
            return Response.status(Response.Status.OK).entity(transformTOs(contratos, filter)).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
        } catch (AutorizacaoControllerException | ParametroControllerException e) {
            LOG.error(e.getMessage(), e);
            return genericError(e);
        }
    }

    @POST
    @Secured
    @Path("/listarCsaCompCartao")
    public Response listarCsaCompCartao() {
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

        if (!responsavel.isSer() || !responsavel.temPermissao(CodedValues.FUN_SOLICITAR_PORTABILIDADE) || !ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_PORTABILIDADE_CARTAO, responsavel)) {
            return genericError(new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel));
        }

        try {
            final ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
            final List<TransferObject> consignatarias = consignatariaController.listaCsaPortabilidadeCartao(null, responsavel);
            final ParametroController parametroController = ApplicationContextProvider.getApplicationContext().getBean(ParametroController.class);
           
            if ((consignatarias == null) || consignatarias.isEmpty()) {
                return genericError(new ZetraException("mensagem.solicitar.portabilidade.cartao.nao.existe.consignatarias", responsavel));
            }

            final Map<String, TransferObject> csaMap = new HashMap<>();
            
            for(TransferObject csa : consignatarias){
                CustomTransferObject csaFilter = new CustomTransferObject();
                String csaCodigo = (String) csa.getAttribute(Columns.CSA_CODIGO);
                csaFilter.setAttribute(Columns.CSA_CODIGO, csaCodigo);
                csaFilter.setAttribute(Columns.CSA_IDENTIFICADOR, csa.getAttribute(Columns.CSA_IDENTIFICADOR));
                csaFilter.setAttribute(Columns.CSA_NOME, csa.getAttribute(Columns.CSA_NOME));
                csaFilter.setAttribute(Columns.CSA_NOME_ABREV, csa.getAttribute(Columns.CSA_NOME_ABREV));
                csaFilter.setAttribute("exigeCadastroEnderecoSolicitacaoEmprestimo", "N");
                
                csaMap.put(csaCodigo,csaFilter);
            }   
                        
            try {      

                final List<TransferObject> lstParamSvcCsa = parametroController.selectParamSvcCsa(Arrays.asList(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO), responsavel);
                
                final ValidadorCartaoCreditoController validadorCartaoCreditoController = ApplicationContextProvider.getApplicationContext().getBean(ValidadorCartaoCreditoController.class);
                // Filtra apenas os parâmetros dos serviços que são de reserva de cartão
                final List<TransferObject> lstParamSvcReservaCartaoCsa = lstParamSvcCsa.stream()
                        .filter(paramSvcCsa -> {
                            try {
                            	String svcCodigo = paramSvcCsa.getAttribute("tb_param_svc_consignataria.svc_codigo").toString();
                                return validadorCartaoCreditoController.isReservaCartao(svcCodigo);
                            } catch (AutorizacaoControllerException ex) {
                                LOG.warn(ex.getMessage(), ex);
                                return false;
                            }
                        }).collect(Collectors.toList());
                
                for (final TransferObject paramSvcCsa : lstParamSvcReservaCartaoCsa) {
                   String csaCode = paramSvcCsa.getAttribute("tb_param_svc_consignataria.csa_codigo").toString();
                    
                   if ((paramSvcCsa != null) && (paramSvcCsa.getAttribute(Columns.PSC_VLR) != null)) {
                        // N = Nada obrigatório | E = Endereço obrigatório | C = Celular obrigatório | EC = Endereço e celular obrigatórios
                        final String pscVlr = (!paramSvcCsa.getAttribute(Columns.PSC_VLR).toString().isEmpty()) ? paramSvcCsa.getAttribute(Columns.PSC_VLR).toString() : "N";

                        if (csaMap.get(csaCode) != null){
                            TransferObject csaParam = csaMap.get(csaCode);
                            csaParam.setAttribute("exigeCadastroEnderecoSolicitacaoEmprestimo", pscVlr);
                            csaMap.put(csaCode,csaParam);
                        }
                    }
                }
                
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                return genericError(ex);
		    }
            
            List<Map<String, Object>> listaConsignatarias = csaMap.values().stream().map(e -> e.getAtributos()).collect(Collectors.toList());
            return Response.status(Response.Status.OK).entity(listaConsignatarias).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
            
        } catch (final ConsignatariaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            return genericError(ex);
        }
    }
    
    @POST
    @Secured
    @Path("/portabilidadeCartao")
    public Response portabilidadeCartao(ConsignacaoRestRequest dados) {
    	final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
    	
    	if (!responsavel.isSer() || !responsavel.temPermissao(CodedValues.FUN_SOLICITAR_PORTABILIDADE) || !ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_PORTABILIDADE_CARTAO, responsavel)) {
            return genericError(new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel));
        }
    	
    	if (dados == null) {
            final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ApplicationResourcesHelper.getMessage("mensagem.rest.parametros.ausente", null);
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON+"; charset=UTF-8").build();
        }
    	
    	if (TextHelper.isNull(dados.adeCarencia)) {
            dados.adeCarencia = 0;
        }

        if (TextHelper.isNull(dados.adeIdentificador)) {
            dados.adeIdentificador = ApplicationResourcesHelper.getMessage("rotulo.consignacao.identificador.solicitacao", responsavel);
        }
   	
    	try {
            validaCamposObrigatoriosPortabilidadeCartao(dados, responsavel);
        } catch (final ZetraException e) {
            LOG.error(e.getMessage(), e);
            return genericError(e);
        }
    	
    	
    	try {
			String cnvCodigo = encontrarCnvCodigo(dados, responsavel);
			dados.cnvCodigo = cnvCodigo;
		} catch (ConvenioControllerException e) {
			LOG.error(e.getMessage(), e);
            return genericError(e);
		}
    	
    	final ConsultarMargemController consultarMargemController = ApplicationContextProvider.getApplicationContext().getBean(ConsultarMargemController.class);
		try {
			if (!consultarMargemController.servidorTemMargem(responsavel.getRseCodigo(), null, dados.svcCodigo, true,
					responsavel)) {
				return genericError(new ZetraException("mensagem.margemInsuficiente", responsavel));
			}
			
			final ParametroController parametroController = ApplicationContextProvider.getApplicationContext().getBean(ParametroController.class);
	        final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(dados.svcCodigo, responsavel);
	        
	        if (paramSvcCse.isTpsPrazoFixo() && !TextHelper.isNull(paramSvcCse.getTpsMaxPrazo())) {
	            dados.adePrazo = Integer.valueOf(paramSvcCse.getTpsMaxPrazo());
	        } else if (!TextHelper.isNull(dados.adePrazo)) {
	            dados.adePrazo = null;
	        }
		} catch (final ServidorControllerException | ParametroControllerException e) {
		    LOG.error(e.getMessage(), e);
			return genericError(e);
		}
		
		String adeCodigoRetorno;
        try {
            adeCodigoRetorno = realizaReservaPortabilidadeCartao(dados, responsavel);
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
            return genericError(e);
        }

        final PesquisarConsignacaoController pesquisarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);
        
        CustomTransferObject autdesAtualizada;
		try {
			autdesAtualizada = pesquisarConsignacaoController.buscaAutorizacao(adeCodigoRetorno, responsavel);
			final List<String> filterCartao = Arrays.asList("ade_numero", "ade_vlr", "ade_data", "ade_codigo", "sad_codigo", "sad_descricao", "dad_valor48");
	        return Response.status(Response.Status.OK).entity(transformTO(autdesAtualizada, filterCartao)).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
		} catch (AutorizacaoControllerException ex) {
			ex.printStackTrace();
			final ResponseRestRequest responseError = new ResponseRestRequest();
            responseError.mensagem = ex.getMessage();
            return Response.status(Response.Status.CONFLICT).entity(responseError).header(HttpHeaders.CONTENT_TYPE, RETURN_CONTENT_TYPE).build();
		}
    }
    	
    	private void validaCamposObrigatoriosPortabilidadeCartao(ConsignacaoRestRequest dados, AcessoSistema responsavel) throws ZetraException {
    		
    		if (((dados.adeCodigo == null) || "".equals(dados.adeCodigo))) {
    			throw new ZetraException("mensagem.informe.ade.codigo.portabilidade.cartao", responsavel);
    		}
    		
    		if (((dados.adeVlr == null) || "".equals(dados.adeVlr))) {
    			throw new ZetraException("mensagem.informe.valor.parcela.ou.valor.liberado", responsavel);    		
    		}

            if(((dados.csaCodigo == null) || "".equals(dados.csaCodigo))){
                throw new ZetraException("mensagem.erro.nenhuma.consignataria.encontrada", responsavel);
            }
    	}
    	
    	private String encontrarCnvCodigo(ConsignacaoRestRequest dados, AcessoSistema responsavel) throws ConvenioControllerException {
    		final ConvenioController convenioController = ApplicationContextProvider.getApplicationContext().getBean(ConvenioController.class);
    		dados.svcCodigo = convenioController.findServicoByAdeCodigo(dados.adeCodigo, responsavel).getSvcCodigo();

    		ConvenioTransferObject convenio = null;
    		if(!TextHelper.isNull(dados.cnvCodigo)) {
    		    convenio = convenioController.findByPrimaryKey(dados.cnvCodigo, responsavel);
    		} else {
    		    convenio = convenioController.findByUniqueKey(dados.csaCodigo, dados.svcCodigo, responsavel.getOrgCodigo(), responsavel);
    		}
			return convenio.getCnvCodigo();
    	}
    	
    	private String realizaReservaPortabilidadeCartao(ConsignacaoRestRequest dados, AcessoSistema responsavel) throws ZetraException{
            final String adePeriodicidade = PeriodoHelper.getPeriodicidadeFolha(responsavel);
            
            List<String> adeCodigos = new ArrayList<>();
            adeCodigos.add(dados.adeCodigo);

    		final ConsignacaoDelegate consigDelegate = new ConsignacaoDelegate();

    		final RenegociarConsignacaoParametros renegociarParam = new RenegociarConsignacaoParametros();
            renegociarParam.setTipo(responsavel.getTipoEntidade());
            renegociarParam.setRseCodigo(responsavel.getRseCodigo());
            renegociarParam.setAdeVlr(dados.adeVlr); 
            renegociarParam.setCorCodigo(dados.corCodigo);
            renegociarParam.setAdePrazo(dados.adePrazo); 
            renegociarParam.setAdeCarencia(dados.adeCarencia); 
            renegociarParam.setAdeIdentificador(dados.adeIdentificador); 
            renegociarParam.setCnvCodigo(dados.cnvCodigo);
            renegociarParam.setAdeIndice(dados.adeIndice);
            renegociarParam.setAdeVlrTac(dados.adeVlrTac); 
            renegociarParam.setAdeVlrIof(dados.adeVlrIof); 
            renegociarParam.setAdeVlrLiquido(dados.adeVlrLiquido); 
            renegociarParam.setAdeVlrMensVinc(dados.adeVlrMensVinc); 
            renegociarParam.setAdeTaxaJuros(dados.adeTaxaJuros); 
            renegociarParam.setAdeCodigosRenegociacao(adeCodigos);
            renegociarParam.setCftCodigo(dados.cftCodigo); 
            renegociarParam.setCdeVlrLiberado(dados.valorLiberado); 
            renegociarParam.setCdeTxtContato("");
            renegociarParam.setAdeBanco(dados.numBanco); 
            renegociarParam.setAdeAgencia(dados.numAgencia); 
            renegociarParam.setAdeConta(dados.numConta); 
            renegociarParam.setAdePeriodicidade(adePeriodicidade); 
            renegociarParam.setDtjCodigo(dados.dtjCodigo);
            renegociarParam.setComSerSenha(true); 
            if (!TextHelper.isNull(dados.serTelefoneSolicitacao)) {
                renegociarParam.setTdaTelSolicitacaoSer(dados.serTelefoneSolicitacao);
            }

            renegociarParam.setCompraContrato(Boolean.TRUE); 
            renegociarParam.setPortabilidadeCartao(true); 

            final AlongarConsignacaoControllerBean solicitarPortabilidadeControllerBean = ApplicationContextProvider.getApplicationContext().getBean(AlongarConsignacaoControllerBean.class);

    		String adeCodigoNovo;
    		try {
    		    adeCodigoNovo = solicitarPortabilidadeControllerBean.renegociar(renegociarParam, responsavel);
    			// inclui ocorrência de operação
    			consigDelegate.criaOcorrenciaADE(adeCodigoNovo, CodedValues.TOC_PORTABILIDADE_CARTAO, ApplicationResourcesHelper.getMessage("mensagem.portabilidade.cartao.ocorrencia", responsavel), responsavel);
    		} catch (final AutorizacaoControllerException e) {
    		    LOG.error(e.getMessage(), e);
    			throw e;
    		}
    		return adeCodigoNovo;
    
    }
    
}
