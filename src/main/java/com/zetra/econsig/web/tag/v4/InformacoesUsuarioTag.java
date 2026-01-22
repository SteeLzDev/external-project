package com.zetra.econsig.web.tag.v4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.FinanciamentoDividaControllerException;
import com.zetra.econsig.exception.LeilaoSolicitacaoControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.parametro.ParamCsa;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.financiamentodivida.FinanciamentoDividaController;
import com.zetra.econsig.service.leilao.LeilaoSolicitacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.tag.ZetraTagSupport;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

/**
 * <p>Title: InformacoesUsuarioTag</p>
 * <p>Description: Tag para impressão de informações ao usuario v4.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author: igor.lucas $
 * $Revision: 30014 $
 * $Date: 2020-07-29 11:09:56 -0300 (qua, 29 jul 2020) $
 */
public class InformacoesUsuarioTag extends ZetraTagSupport {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(InformacoesUsuarioTag.class);

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private FinanciamentoDividaController financiamentoDividaController;

    @Autowired
    private LeilaoSolicitacaoController leilaoSolicitacaoController;

	@Autowired
	private PesquisarConsignacaoController pesquisarConsignacaoController;

    private AcessoSistema responsavel;
    private boolean csaBloqueada;
    private String motivoBloqueioCsa;
    private boolean csaNenhumConvenioAtivo;
    private boolean podeConsultarMotivoBloqueio;
    private boolean podeConsultarCsa;
	private boolean csaConvenioAtivoSvcReservaCartao;
	private boolean csaPodeVenderContratoCartao = false;
	private int numeroContratosReservaCartaoSemLancamento = 0;
	private int numeroContratosAptosPortabilidade = 0;

    /**
     * Carrega as informacoes a serem exibidas.
     */
    private void carregaInformacoes() {
        responsavel = JspHelper.getAcessoSistema((HttpServletRequest) pageContext.getRequest());

        try {
            if (responsavel.isCsaCor()) {
                // Carrega informações de bloqueio de consignatária
                carregaInformacaoBloqueioConsignataria();
                // Carrega informações de csa sem convênios ativos
                carregaInformacaoNenhumConvenioAtivo();
                carregaInformacaoConvenioAtivoComReservaCartao();
                if (csaConvenioAtivoSvcReservaCartao) {
                    carregaInformacaoReservaCartao();
                }
            }
        } catch (ViewHelperException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Carrega as informacoes sobre o status da consignataria logada.
     * @throws ViewHelperException
     */
    private void carregaInformacaoBloqueioConsignataria() throws ViewHelperException {
        // Limpas as informações anteriormente carregadas
        csaBloqueada = false;
        motivoBloqueioCsa = null;
        csaNenhumConvenioAtivo = false;
        podeConsultarMotivoBloqueio = false;
        podeConsultarCsa = false;

        // Se o usuário não é de consignatária, não há o que ser carregado
        if (!responsavel.isCsa()) {
            return;
        }

        podeConsultarMotivoBloqueio = responsavel.temPermissao(CodedValues.FUN_LISTAR_BLOQUEIOS_CSA);
        podeConsultarCsa = responsavel.temPermissao(CodedValues.FUN_CONS_CONSIGNATARIA);

        try {
            String csaCodigo = responsavel.getCodigoEntidade();

            ConsignatariaTransferObject consignataria = consignatariaController.findConsignataria(csaCodigo, responsavel);

            if (consignataria != null && (consignataria.getCsaAtivo().equals(CodedValues.STS_INATIVO) ||
                    consignataria.getCsaAtivo().equals(CodedValues.STS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA))) {
                csaBloqueada = true;
                // Ocorrencias temporarias que sao apagadas apos o desbloqueio automatico
                List<String> tocCodigos = new ArrayList<>();
                tocCodigos.add(CodedValues.TOC_CONSIGNATARIA_COM_PENDENCIAS_COMPRA);
                tocCodigos.add(CodedValues.TOC_CONSIGNATARIA_COM_PENDENCIAS_SALDO);

                List<TransferObject> lstBloqueio = consignatariaController.lstOcaConsignataria(csaCodigo, tocCodigos, responsavel);
                if (!lstBloqueio.isEmpty()) {
                    motivoBloqueioCsa = "";
                    for (TransferObject to : lstBloqueio) {
                        if (!TextHelper.isNull(to.getAttribute(Columns.OCC_OBS))
                                && motivoBloqueioCsa.indexOf((String) to.getAttribute(Columns.OCC_OBS)) == -1) {
                            motivoBloqueioCsa += (String) to.getAttribute(Columns.OCC_OBS) + " ";
                        }
                    }
                } else {
                    // Motivo padronizado para evitar casos onde nao foi cadastrada nenhuma ocorrencia,
                    // ou seja, foi executado o update do status manualmente no banco
                    motivoBloqueioCsa = ApplicationResourcesHelper.getMessage("mensagem.consignataria.bloqueada", responsavel);
                }
            }
        } catch (ConsignatariaControllerException e) {
            throw new ViewHelperException(e);
        }
    }

    /**
     * Exibe mensagem caso não exista convênios ativos para a consignatária logada.
     * @throws ViewHelperException
     */
    private void carregaInformacaoNenhumConvenioAtivo() throws ViewHelperException {
        List<TransferObject> convenios = new ArrayList<>();
        try {
            convenios = convenioController.lstCnvEntidade(responsavel.getCodigoEntidade(), responsavel.getTipoEntidade(), null, responsavel);
            if (convenios.isEmpty()) {
                csaNenhumConvenioAtivo = true;
            }
        } catch (ConvenioControllerException e) {
            throw new ViewHelperException(e);
        }
    }

	private void carregaInformacaoConvenioAtivoComReservaCartao() throws ViewHelperException {
		List<TransferObject> convenios;
		try {
			convenios = convenioController.lstSvcCnvAtivos(CodedValues.NSE_CARTAO, responsavel.getCsaCodigo(), true, responsavel);
			if (!convenios.isEmpty()) {
				csaConvenioAtivoSvcReservaCartao = true;
			}
		} catch (ConvenioControllerException e) {
			throw new ViewHelperException(e);
		}
	}

	private void carregaInformacaoReservaCartao() throws ViewHelperException {
		try {
			if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_PORTABILIDADE_CARTAO, CodedValues.TPC_SIM, responsavel)) {
				csaPodeVenderContratoCartao = CodedValues.TPA_SIM.equals(ParamCsa.getParamCsa(responsavel.getCsaCodigo(), CodedValues.TPA_CSA_PODE_VENDER_CONTRATO_CARTAO, responsavel));
				numeroContratosReservaCartaoSemLancamento = pesquisarConsignacaoController.obterTotalReservaCartaoSemLancamento(responsavel.getCsaCodigo(), responsavel);
				if (CodedValues.TPA_SIM.equals(ParamCsa.getParamCsa(responsavel.getCsaCodigo(), CodedValues.TPA_CSA_PODE_COMPRAR_CONTRATO_CARTAO, responsavel))) {
					numeroContratosAptosPortabilidade = pesquisarConsignacaoController.obterTotalContratosPortabilidadeCartaoCsa(responsavel.getCsaCodigo(), responsavel);
				}
			} else {
				csaPodeVenderContratoCartao = false;
				numeroContratosReservaCartaoSemLancamento = 0;
				numeroContratosAptosPortabilidade = 0;				
			}
		} catch (AutorizacaoControllerException e) {
			throw new ViewHelperException(e);
		}
	}

    @Override
    public int doEndTag() throws JspException {
        try {
            pageContext.getOut().print(generateHtml());
        } catch (IOException ex) {
            throw new JspException(ex.getMessage());
        }
        // Continue processing this page
        return (EVAL_PAGE);
    }

	private String generateHtml() {
        // Obtém as informações
        carregaInformacoes();

        StringBuilder html = new StringBuilder();

        // Mensagem para consignatária bloqueada
        if (responsavel.isCsa() && csaBloqueada) {
        	html.append("<div class=\"row\">");
        		html.append("<div class=\"col-sm\">");
        			html.append("<div class=\"card w-100\">");
        				html.append("<div class=\"card-header hasIcon\">");
			        		html.append("<span class=\"card-header-icon\"><svg width=\"31\" class=\"pt-1\">");
			                  html.append("<use xlink:href=\"../img/sprite.svg#i-info\"></use></svg>");
		                    html.append("</span>");
	    					html.append("<h2 class=\"card-header-title\">");
	    					html.append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.informacoes", responsavel));
	    					html.append("</h2>");
    					html.append("</div>");
    					html.append("<div class=\"card-body\">");
	    					// Mensagem para nenhum convênio encontrado
	    			        if ((responsavel.isCsa() || responsavel.isCor()) && csaNenhumConvenioAtivo) {
	    			            html.append("<div class=\"alert alert-warning\" role=\"alert\">");
		    			            html.append("<span>");
		    			            	html.append(ApplicationResourcesHelper.getMessage("mensagem.nenhumConvenioAtivo", responsavel));
		    			            html.append("</span>");
	    			            html.append("</div>");
	    			        }
							if (!TextHelper.isNull(motivoBloqueioCsa)) {
							html.append("<dl class=\"row data-list\">");
								html.append("<dt class=\"col-6\">");
									html.append(motivoBloqueioCsa);
								html.append("</dt>");
								html.append("<dd class=\"col-6\">");
									if (podeConsultarMotivoBloqueio) {
					                    html.append("<A HREF=\"#no-back\" onClick=\"postData('../v3/listarBloqueiosConsignataria?acao=iniciar&CSA_CODIGO=").append(TextHelper.forJavaScriptAttribute(responsavel.getCodigoEntidade())).append("')\">").append(ApplicationResourcesHelper.getMessage("rotulo.acoes.detalhar", responsavel)).append("</a>");
					                }
									else if (podeConsultarCsa) {
					                    html.append("<A HREF=\"#no-back\" onClick=\"postData('../v3/manterConsignataria?acao=editarConsignataria&csa=").append(TextHelper.forJavaScriptAttribute(responsavel.getCodigoEntidade())).append("')\">").append(ApplicationResourcesHelper.getMessage("rotulo.acoes.detalhar", responsavel)).append("</a>");
					                }
								html.append("</dd>");
							html.append("</dl>");
							}
						html.append("</div>");
					html.append("</div>");
                html.append("</div>");
            html.append("</div>");
        } else if (responsavel.isCsaCor() && csaNenhumConvenioAtivo) {
        	html.append("<div class=\"row\">");
	        	html.append("<div class=\"col-sm\">");
		        	html.append("<div class=\"card w-100\">");
			        	html.append("<div class=\"card-header hasIcon\">");
			        		html.append("<span class=\"card-header-icon\"><svg width=\"31\" class=\"pt-1\">");
			                  html.append("<use xlink:href=\"../img/sprite.svg#i-info\"></use></svg>");
		                    html.append("</span>");
			        		html.append("<h2 class=\"card-header-title\">");
			        			html.append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.informacoes", responsavel));
		        			html.append("</h2>");
	        			html.append("</div>");
	        			html.append("<div class=\"card-body\">");
	        				html.append("<span>");
	        					html.append(ApplicationResourcesHelper.getMessage("mensagem.nenhumConvenioAtivo", responsavel));
        					html.append("</span>");
    					html.append("</div>");
					html.append("</div>");
				html.append("</div>");
			html.append("</div>");

        }

        // Se tem módulo de financiamento de dívida, verifica se existem solicitações de propostas
        // pendentes para a consignatária, para exibir alerta na página inicial.
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel) && responsavel.isCsa()) {
            try {
                TransferObject criteriosPesquisa = new CustomTransferObject();
                // Pendentes de informação de propostas de pagamento parcelado
                criteriosPesquisa.setAttribute("filtro", "3");
                criteriosPesquisa.setAttribute("CSA_CODIGO", responsavel.getCsaCodigo());
                int total = financiamentoDividaController.contarFinanciamentoDivida(criteriosPesquisa, responsavel);

                if (total > 0) {
                	html.append("<div class=\"row\">");
                		html.append("<div class=\"col-sm\">");
                			html.append("<div class=\"card w-100\">");
			                	html.append("<div class=\"card-header hasIcon\">");
					        		html.append("<span class=\"card-header-icon\"><svg width=\"28\" class=\"pt-1\">");
					                  html.append("<use xlink:href=\"../img/sprite.svg#i-cartao\"></use></svg>");
				                    html.append("</span>");
			                		html.append("<h2 class=\"card-header-title\">");
			                			html.append(ApplicationResourcesHelper.getMessage("rotulo.acompanhamento.financiamento.divida.cartao.titulo", responsavel));
		                			html.append("</h2>");
			                	html.append("</div>");
			                	if (responsavel.temPermissao(CodedValues.FUN_ACOMPANHAR_FINANCIAMENTO_DIVIDA)) {
			                		html.append("<div class=\"card-body\">");
				                		html.append("<ul class=\"list-links\">");
					                		html.append("<li title=\" ");
					                			html.append(ApplicationResourcesHelper.getMessage("mensagem.mais.detalhes.clique.aqui", responsavel));
					                		html.append("\">");
					                			html.append("<a href=\"#no-back\" onClick=\"postData('../v3/acompanharFinanciamentoDivida?acao=iniciar&pesquisar=true&origem=1&filtro=3')\">");
					                				html.append(ApplicationResourcesHelper.getMessage("mensagem.existem.contratos.pendentes.pagamento.parcelado", responsavel));
					                			html.append("</a>");
				                			html.append("</li>");
					                	html.append("</ul>");
				                	html.append("</div>");
			                	} else {
			                		html.append("<div class=\"card-body\">");
				                		html.append("<span>");
				                			html.append(ApplicationResourcesHelper.getMessage("mensagem.existem.contratos.pendentes.pagamento.parcelado", responsavel));
				                		html.append("</span>");
			                		html.append("</div>");
			                	}
		                    html.append("</div>");
	                    html.append("</div>");
                    html.append("</div>");
                }
            } catch (FinanciamentoDividaControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR, CodedValues.TPC_SIM, responsavel) && responsavel.isCsaCor()) {
            try {
                TransferObject criteriosPesquisa = new CustomTransferObject();
                // Pendentes de informação de propostas de pagamento parcelado
                criteriosPesquisa.setAttribute("filtro", "0");
                criteriosPesquisa.setAttribute("CSA_CODIGO", responsavel.getCsaCodigo());
                criteriosPesquisa.setAttribute("todosConveniosAtivos", Boolean.TRUE);
                int total = leilaoSolicitacaoController.contarLeilaoSolicitacao(criteriosPesquisa, responsavel);

                if (total > 0) {
                	html.append("<div class=\"row\">");
                		html.append("<div class=\"col-sm\">");
                			html.append("<div class=\"card w-100\">");
                				html.append("<div class=\"card-header hasIcon\">");
	    			        		html.append("<span class=\"card-header-icon\"><svg width=\"26\">");
	    			                  html.append("<use xlink:href=\"../img/sprite.svg#i-acompanhar\"></use></svg>");
	    		                    html.append("</span>");
                					html.append("<h2 class=\"card-header-title\">");
                						html.append(ApplicationResourcesHelper.getMessage("rotulo.acompanhamento.leilao.solicitacao.titulo", responsavel));
                					html.append("</h2>");
            					html.append("</div>");
            					if (responsavel.temPermissao(CodedValues.FUN_ACOMPANHAR_LEILAO_VIA_SIMULACAO)) {
            						html.append("<div class=\"card-body\">");
				                		html.append("<ul class=\"list-links\">");
					                		html.append("<li title=\" ");
					            				html.append(ApplicationResourcesHelper.getMessage("mensagem.mais.detalhes.clique.aqui", responsavel));
				            				html.append("\">");
				            					html.append("<a href=\"#no-back\" onClick=\"postData('../v3/acompanharLeilao?acao=iniciar&pesquisar=true&filtro=0')\" >");
				            						html.append(ApplicationResourcesHelper.getMessage("mensagem.existem.leiloes.solicitacao.pendentes", responsavel));
				        						html.append("</a>");
				            				html.append("</li>");
					                	html.append("</ul>");
				                	html.append("</div>");
			                    } else {
			                    	html.append("<div class=\"card-body\">");
				                    	html.append("<span>");
				                    		html.append(ApplicationResourcesHelper.getMessage("mensagem.existem.leiloes.solicitacao.pendentes", responsavel));
				            			html.append("</span>");
			            			html.append("</div>");
			                    }
		                    html.append("</div>");
		                html.append("</div>");
		            html.append("</div>");
                }
            } catch (LeilaoSolicitacaoControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

		if (responsavel.isCsaCor() && (numeroContratosReservaCartaoSemLancamento > 0 || (csaPodeVenderContratoCartao && numeroContratosAptosPortabilidade > 0))) {
			html.append("<div class=\"row\">");
				html.append("<div class=\"col-sm\">");
					html.append("<div class=\"card w-100\">");
						html.append("<div class=\"card-header hasIcon\">");
							html.append("<span class=\"card-header-icon\"><svg width=\"31\" class=\"pt-1\">");
								html.append("<use xlink:href=\"../img/sprite.svg#i-info\"></use></svg>");
							html.append("</span>");
							html.append("<h2 class=\"card-header-title\">");
								html.append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.reserva.cartao", responsavel));
							html.append("</h2>");
						html.append("</div>");

						html.append("<div class=\"card-body\">");
							html.append("<span>");
								if ((numeroContratosAptosPortabilidade > 0) && (numeroContratosReservaCartaoSemLancamento > 0) && csaPodeVenderContratoCartao) {
									html.append(ApplicationResourcesHelper.getMessage("mensagem.contratos.reserva.sem.lancamento.e.aptos.portabilidade", responsavel, Integer.toString(numeroContratosReservaCartaoSemLancamento), Integer.toString(numeroContratosAptosPortabilidade)));
								} else if (numeroContratosReservaCartaoSemLancamento > 0) {
									html.append(ApplicationResourcesHelper.getMessage("mensagem.contratos.reserva.sem.lancamento", responsavel, Integer.toString(numeroContratosReservaCartaoSemLancamento)));
								} else if (numeroContratosReservaCartaoSemLancamento == 0) {
									html.append(ApplicationResourcesHelper.getMessage("mensagem.contratos.aptos.portabilidade", responsavel, Integer.toString(numeroContratosAptosPortabilidade)));
								}
								if (responsavel.temPermissao(CodedValues.FUN_REL_PROVISIONAMENTO_MARGEM)) {
									html.append("<b class=\"col-6\">");
										html.append("<a href=\"#no-back\" onClick=\"postData('../v3/listarRelatorio?tipo=provisionamento_margem')\">");
											html.append("&nbsp");
											html.append(ApplicationResourcesHelper.getMessage("rotulo.acoes.visualizar", responsavel));
										html.append("</a>");
									html.append("</b>");
								}
							html.append("</span>");
						html.append("</div>");
					html.append("</div>");
				html.append("</div>");
			html.append("</div>");
		}

		// Verifica se deve exibir acesso à comunicações pendentes
        final HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        final String eConsigPageToken = SynchronizerToken.generateToken4URL(request);
        final boolean existeComunicao = request.getAttribute("existeComunicacao") != null && (boolean) request.getAttribute("existeComunicacao");
        if (existeComunicao) {
            html.append("<div class=\"card\">");
                html.append("<div class=\"card-header hasIcon\">");
                    html.append("<span class=\"card-header-icon\">");
                        html.append("<svg width=\"26\"><use xlink:href=\"#i-mensagem\"></use></svg>");
                    html.append("</span>");
                    html.append("<h2 class=\"card-header-title\">").append(ApplicationResourcesHelper.getMessage("rotulo.geral.principal.existem.comunicacao", responsavel)).append("</h2>");
                html.append("</div>");
                html.append("<div class=\"card-body\">");
                    html.append("<ul class=\"list-links\">");
                        html.append("<li>");
                            html.append("<a href=\"#no-back\" title=\"").append(ApplicationResourcesHelper.getMessage("mensagem.existe.comunicacao.geral", responsavel));
                            html.append("\" onClick=\"postData('../v3/enviarComunicacao?acao=listar&").append(eConsigPageToken).append("')\">");
                            html.append(ApplicationResourcesHelper.getMessage("mensagem.existe.comunicacao.geral", responsavel)).append("</a>");
                        html.append("</li>");
                    html.append("</ul>");
                html.append("</div>");
            html.append("</div>");
        }

        return html.toString();
    }
}
