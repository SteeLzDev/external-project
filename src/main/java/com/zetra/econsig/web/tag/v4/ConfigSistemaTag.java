package com.zetra.econsig.web.tag.v4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.FieldKeysConstants;

public class ConfigSistemaTag extends com.zetra.econsig.web.tag.ConfigSistemaTag {

	@Override
	public String generateHtml() throws ViewHelperException {
        // Obtém as configurações do sistema.
        carregaConfiguracaoSistema();

        StringBuilder html = new StringBuilder();

        try {
        	html.append("<div class=\"row\">");
	        	html.append("<div class=\"col-sm\">");
	        		html.append("<div class=\"card\">");
		        		html.append("<div class=\"card-header hasIcon\">");
			        		html.append("<span class=\"card-header-icon\"><svg width=\"26\">");
			                  html.append("<use xlink:href=\"../img/sprite.svg#i-sistema\"></use></svg>");
		                    html.append("</span>");
				        	html.append("<h2 class=\"card-header-title\">");
				        		html.append(ApplicationResourcesHelper.getMessage("rotulo.sistema.configuracoes", responsavel));
				        	html.append("</h2>");
			        	html.append("</div>");
			        	html.append("<div class=\"card-body\">");
				        	html.append("<dl class=\"row data-list\">");
					        	// Se há nível de segurança configurado.
					            if (exibeNivelSeguranca && responsavel.isCseSup() && nivelSeguranca != null && ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_NIVEL_SEGURANCA, responsavel)) {
					                html.append("<dt class=\"col-6\">");
					                	html.append(ApplicationResourcesHelper.getMessage("rotulo.nivel.seguranca.titulo", responsavel));
					                html.append("</dt>");
					                html.append("<dd class=\"col-6\">");
					                	html.append(TextHelper.forHtmlContent(nivelSeguranca) + " - ");
					                	html.append("<a href=\"#no-back\" onClick=\"postData('../v3/configurarSistema?acao=verNivelSeguranca')\">");
					                		html.append(ApplicationResourcesHelper.getMessage("rotulo.acoes.visualizar", responsavel));
					                	html.append("</a>");
					                html.append("</dd>");
					            }

					            // Exibe dia de corte e de repasse dos orgãos se forma diferentes do corte do sistema
					            if (diaCorteOrgaos != null && diaCorteOrgaos.size() > 0) {
					                if (periodoAtual != null && (!responsavel.isSer() && ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_PERIODO_ATUAL, responsavel) || responsavel.isSer() && ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_PERIODO_ATUAL_SER, responsavel))) {
					                    html.append("<dt class=\"col-6\">");
					                    	html.append(ApplicationResourcesHelper.getMessage("rotulo.sistema.periodo.atual.sistema", responsavel));
					                    html.append("</dt>");
					                    html.append("<dd class=\"col-6\">");
					                    	html.append(TextHelper.forHtmlContent(periodoAtual));
					                    html.append("</dd>");
					                }

					                if (periodoOrgaos != null && periodoOrgaos.size() > 0 && (!responsavel.isSer() && ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_PERIODO_ORGAOS, responsavel) || responsavel.isSer() && ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_PERIODO_ORGAOS_SER, responsavel))) {
					                    List<String> orgaos = new ArrayList<>(periodoOrgaos.keySet());
					                    Collections.sort(orgaos);
					                    Iterator<String> itOrgaos = orgaos.iterator();
					                    while (itOrgaos.hasNext()) {
					                        String orgNome = itOrgaos.next();
					                        html.append("<dt class=\"col-6\">");
					                        	html.append(ApplicationResourcesHelper.getMessage("rotulo.sistema.periodo.atual.arg0", responsavel,orgNome));
					                        html.append("</dt>");
					                        html.append("<dd class=\"col-6\">");
					                        	html.append(TextHelper.forHtmlContent(periodoOrgaos.get(orgNome)));
					                        html.append("</dd>");
					                    }
					                }

					                if (diaCorte != null && (!responsavel.isSer() && ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_DIA_CORTE, responsavel) || responsavel.isSer() && ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_DIA_CORTE_SER, responsavel))) {
					                    html.append("<dt class=\"col-6\">");
					                    	html.append(ApplicationResourcesHelper.getMessage("rotulo.sistema.dia.corte.sistema", responsavel));
					                    html.append("</dt>");
					                    html.append("<dd class=\"col-6\">");
					                    	html.append(imprimeDiaCorte(null));
					                    html.append("</dd>");
					                }

					                if (diaCorteOrgaos != null && diaCorteOrgaos.size() > 0 && (!responsavel.isSer() && ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_DIA_CORTE_ORGAOS, responsavel) || responsavel.isSer() && ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_DIA_CORTE_ORGAOS_SER, responsavel))) {
					                    List<String> orgaos = new ArrayList<>(diaCorteOrgaos.keySet());
					                    Collections.sort(orgaos);
					                    Iterator<String> itOrgaos = orgaos.iterator();
					                    while (itOrgaos.hasNext()) {
					                        String orgNome = itOrgaos.next();
					                        html.append("<dt class=\"col-6\">");
					                        	html.append(ApplicationResourcesHelper.getMessage("rotulo.sistema.dia.corte.arg0", responsavel, orgNome));
					                        html.append("</dt>");
					                        html.append("<dd class=\"col-6\">");
					                        	html.append(imprimeDiaCorte(diaCorteOrgaos.get(orgNome)));
					                        html.append("</dd>");
					                    }
					                }

					                if (diaRepasse != null && exibeDiaRepasse && (!responsavel.isSer() && ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_DIA_REPASSE, responsavel) || responsavel.isSer() && ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_DIA_REPASSE_SER, responsavel))) {
					                    html.append("<dt class=\"col-6\">");
					                    	html.append(ApplicationResourcesHelper.getMessage("rotulo.sistema.dia.repasse.sistema", responsavel));
					                    html.append("</dt>");
					                    html.append("<dd class=\"col-6\">");
					                    	html.append(TextHelper.forHtmlContent(diaRepasse));
					                    html.append("</dd>");
					                }

					                if (diaRepasseOrgaos != null && diaRepasseOrgaos.size() > 0  && exibeDiaRepasse && (!responsavel.isSer() && ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_DIA_REPASSE_ORGAOS, responsavel) || responsavel.isSer() && ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_DIA_REPASSE_ORGAOS_SER, responsavel))) {
					                    List<String> orgaos = new ArrayList<>(diaRepasseOrgaos.keySet());
					                    Collections.sort(orgaos);
					                    Iterator<String> itOrgaos = orgaos.iterator();
					                    while (itOrgaos.hasNext()) {
					                        String orgNome = itOrgaos.next();
					                        html.append("<dt class=\"col-6\">");
					                        	html.append(ApplicationResourcesHelper.getMessage("rotulo.sistema.dia.repasse.arg0", responsavel, orgNome));
					                        html.append("</dt>");
					                        html.append("<dd class=\"col-6\">");
					                        	html.append(TextHelper.forHtmlContent(diaRepasseOrgaos.get(orgNome)));
					                        html.append("</dd>");
					                    }
					                }

					            } else {
					                // Se o período foi definido corretamente
					                if (periodoAtual != null && (!responsavel.isSer() && ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_PERIODO_ATUAL, responsavel) || responsavel.isSer() && ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_PERIODO_ATUAL_SER, responsavel))) {
					                    html.append("<dt class=\"col-6\">");
					                    	html.append(ApplicationResourcesHelper.getMessage("rotulo.sistema.periodo.atual", responsavel));
					                    html.append("</dt>");
					                    html.append("<dd class=\"col-6\">");
					                    	html.append(TextHelper.forHtmlContent(periodoAtual));
					                    html.append("</dd>");
					                }

					                // Se há dia de corte configurado.
					                if (diaCorte != null && (!responsavel.isSer() && ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_DIA_CORTE, responsavel) || responsavel.isSer() && ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_DIA_CORTE_SER, responsavel))) {
					                    html.append("<dt class=\"col-6\">");
					                    	html.append(ApplicationResourcesHelper.getMessage("rotulo.sistema.dia.corte", responsavel));
					                    html.append("</dt>");
					                    html.append("<dd class=\"col-6\">");
					                    	html.append(imprimeDiaCorte(null));
					                    html.append("</dd>");
					                }

					                // Se há dia de repasse configurado.
					                if (diaRepasse != null && exibeDiaRepasse && (!responsavel.isSer() && ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_DIA_REPASSE, responsavel) || responsavel.isSer() && ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_DIA_REPASSE_SER, responsavel))) {
					                    html.append("<dt class=\"col-6\">");
					                    	html.append(ApplicationResourcesHelper.getMessage("rotulo.sistema.dia.repasse", responsavel));
					                    html.append("</dt>");
					                    html.append("<dd class=\"col-6\">");
					                    	html.append(TextHelper.forHtmlContent(diaRepasse));
					                    html.append("</dd>");
					                }
					            }

		                           // Se há dia de corte para a csa configurado.
                                if (diaCorteCsa != null && ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_DIA_CORTE_CSA, responsavel) && !responsavel.isSer()) {
                                    html.append("<dt class=\"col-6\">");
                                        html.append(ApplicationResourcesHelper.getMessage("rotulo.sistema.dia.corte.consignataria", responsavel));
                                    html.append("</dt>");
                                    html.append("<dd class=\"col-6\">");
                                    html.append(TextHelper.forHtmlContent(diaCorteCsa));
                                    html.append("</dd>");
                                }
								html.append("<dt class=\"col-6\">");
									html.append(ApplicationResourcesHelper.getMessage("rotulo.sistema.data.hora.sistema", responsavel));
								html.append("</dt>");
								html.append("<dd class=\"col-6\">");
									html.append(TextHelper.forHtmlContent(dataHoraSistema));
								html.append("</dd>");
				            html.append("</dl>");
			            html.append("</div>");
		            html.append("</div>");
	            html.append("</div>");
            html.append("</div>");

			if (!configuracaoModulosSistema.isEmpty()) {
				html.append("<div class=\"row\">");
					html.append("<div class=\"col-sm\">");
						html.append("<div class=\"card\">");
							html.append("<div class=\"card-header hasIcon\">");
								html.append("<span class=\"card-header-icon\"><svg width=\"36\">");
									html.append("<use xlink:href=\"../img/sprite.svg#i-sistema\"></use></svg>");
								html.append("</span>");
								html.append("<h2 class=\"card-header-title\">");
									html.append(ApplicationResourcesHelper.getMessage("rotulo.sistema.modulos.sistema", responsavel));
								html.append("</h2>");
							html.append("</div>");
							html.append("<div class=\"card-body table-responsive p-0\">");
								html.append("<table id=\"tableModulosSistema\" class=\"table table-striped table-hover\">");
								// Não funciona tfoot aqui, incluído via javascript
								html.append("</table>");
							html.append("</div>");
						html.append("</div>");
					html.append("</div>");
				html.append("</div>");
			}

            // Se o sistema possui configurações de taxas para os serviços.
            if (!responsavel.isSer() && cadastraTaxas && taxasServicos != null && taxasServicos.size() > 0 && ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_TAXA_SERVICOS, responsavel)) {
            	html.append("<div class=\"row\">");
	                html.append("<div class=\"col-sm\">");
	                	html.append("<div class=\"card\">");
		                	html.append("<div class=\"card-header hasIcon\">");
				        		html.append("<span class=\"card-header-icon\"><svg width=\"26\">");
				                  html.append("<use xlink:href=\"../img/sprite.svg#i-sistema\"></use></svg>");
			                    html.append("</span>");
			                	html.append("<h2 class=\"card-header-title\">");
			                		html.append((temCET ? ApplicationResourcesHelper.getMessage("rotulo.sistema.configuracoes.cet", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.sistema.configuracoes.taxa", responsavel)));
				                html.append("</h2>");
			                html.append("</div>");
			                html.append("<div class=\"card-body table-responsive p-0\">");
                                html.append("<table id=\"tableCadastroTaxas\" class=\"table table-striped table-hover\">");
                                // Não funciona tfoot aqui, incluído via javascript
                                html.append("</table>");
                            html.append("</div>");
		                html.append("</div>");
	                html.append("</div>");
                html.append("</div>");
            }

            // Se o sistema possui serviços configurados para cancelamento automático
            if (!responsavel.isSer() && configuracaoServicosCancelamentoAutomatico != null && configuracaoServicosCancelamentoAutomatico.size() > 0 &&
                    ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_CANCELAMENTO_AUTOMATICO, responsavel)) {
            	html.append("<div class=\"row\">");
	            	html.append("<div class=\"col-sm\">");
		            	html.append("<div class=\"card\">");
			            	html.append("<div class=\"card-header hasIcon\">");
				        		html.append("<span class=\"card-header-icon\"><svg width=\"36\">");
				                  html.append("<use xlink:href=\"../img/sprite.svg#i-confirmacao\"></use></svg>");
			                    html.append("</span>");
			            		html.append("<h2 class=\"card-header-title\">");
					            	html.append(ApplicationResourcesHelper.getMessage("rotulo.sistema.prazos.confirmacao", responsavel));
				            	html.append("</h2>");
			            	html.append("</div>");
			            	html.append("<div class=\"card-body table-responsive p-0\">");
                                html.append("<table id=\"tableCancelamentoAutomatico\" class=\"table table-striped table-hover\">");
                                    html.append("<tfoot>");
                                        html.append("<tr>");
                                        html.append("<td colspan=\"4\">");
                                        html.append(ApplicationResourcesHelper.getMessage("rotulo.prazo.listage.prazo.em", responsavel));
                                        html.append("<span class=\"font-weight-bold\"> " + (usaDiasUteisCancAutomatico ? ApplicationResourcesHelper.getMessage("rotulo.prazo.listagem.dias.uteis", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.prazo.listagem.dias.corridos", responsavel)) + "</span>");
                                        html.append("</td>");
                                    html.append("</tr>");
                                    html.append("</tfoot>");
                                html.append("</table>");
                            html.append("</div>");
		                html.append("</div>");
	                html.append("</div>");
                html.append("</div>");
            }

            // Se o sistema possui configurações de módulo de compra para os serviços.
            if (!responsavel.isSer() && temModuloAvancadoCompras && configuracaoServicosModuloAvancadoCompra != null && configuracaoServicosModuloAvancadoCompra.size() > 0 &&
                    ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_CONFIG_COMPRA_CONTRATO, responsavel)) {
            	html.append("<div class=\"row\">");
		        	html.append("<div class=\"col-sm\">");
			        	html.append("<div class=\"card\">");
				        	html.append("<div class=\"card-header hasIcon\">");
				        		html.append("<span class=\"card-header-icon\"><svg width=\"35\" class=\"mt-1\">");
				                  html.append("<use xlink:href=\"../img/sprite.svg#i-portabilidade\"></use></svg>");
			                    html.append("</span>");
					        	html.append("<h2 class=\"card-header-title\">");
						        	html.append(ApplicationResourcesHelper.getMessage("rotulo.prazos.compra", responsavel));
					        	html.append("</h2>");
				        	html.append("</div>");
				        	html.append("<div class=\"card-body table-responsive p-0\">");
    			                html.append("<table id=\"tableModuloAvancadoCompra\" class=\"table table-striped table-hover\">");
					        	html.append("<tfoot>");
					        	    html.append("<tr>");
					        	    html.append("<td colspan=\"4\">");
					        	    html.append(ApplicationResourcesHelper.getMessage("rotulo.prazo.listage.prazo.em", responsavel));
					        	    html.append("<span class=\"font-weight-bold\"> " + (usaDiasUteisControleCompra ? ApplicationResourcesHelper.getMessage("rotulo.prazo.listagem.dias.uteis", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.prazo.listagem.dias.corridos", responsavel)) + "</span>");
					                html.append("</td>");
		                        html.append("</tr>");
					        	html.append("</tfoot>");
					        	html.append("</table>");
				        	html.append("</div>");
			        	html.append("</div>");
		        	html.append("</div>");
	        	html.append("</div>");
            }

            // Se o sistema possui configurações de parâmetros de restrição para compra de contratos
            if (!responsavel.isSer() && temModuloCompra && configuracaoServicosCompraContrato != null && configuracaoServicosCompraContrato.size() > 0 &&
                    ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_CONFIG_COMPRA_CONTRATO, responsavel)) {
            	html.append("<div class=\"row\">");
		        	html.append("<div class=\"col-sm\">");
		        		html.append("<div class=\"card\">");
			        		html.append("<div class=\"card-header hasIcon\">");
				        		html.append("<span class=\"card-header-icon\"><svg width=\"34\">");
				                  html.append("<use xlink:href=\"../img/sprite.svg#i-restricao\"></use></svg>");
			                    html.append("</span>");
					        	html.append("<h2 class=\"card-header-title\">");
					        		html.append(ApplicationResourcesHelper.getMessage("rotulo.restricoes.compra", responsavel));
					        	html.append("</h2>");
				        	html.append("</div>");
				        	html.append("<div class=\"card-body table-responsive p-0\">");
				        	    html.append("<table id=\"tableCompraContrato\" class=\"table table-striped table-hover\">");
                                // Não funciona tfoot aqui, incluído via javascript
                                html.append("</table>");
                            html.append("</div>");
			            html.append("</div>");
		            html.append("</div>");
                html.append("</div>");

            }
            // Se o sistema possui configurações de parâmetros de restrição para renegociação de contratos
            if (!responsavel.isSer() && configuracaoServicosRenegociacaoContrato != null && configuracaoServicosRenegociacaoContrato.size() > 0 &&
                    ShowFieldHelper.showField(FieldKeysConstants.CONFIG_SISTEMA_CONFIG_RENEGOCIACAO, responsavel)) {
            	html.append("<div class=\"row\">");
	            	html.append("<div class=\"col-sm\">");
		            	html.append("<div class=\"card\">");
			            	html.append("<div class=\"card-header hasIcon\">");
				        		html.append("<span class=\"card-header-icon\"><svg width=\"34\">");
				                  html.append("<use xlink:href=\"../img/sprite.svg#i-restricao\"></use></svg>");
			                    html.append("</span>");
				            	html.append("<h2 class=\"card-header-title\">");
					            	html.append(ApplicationResourcesHelper.getMessage("rotulo.sistema.restricoes.renegociacao", responsavel));
				            	html.append("</h2>");
			            	html.append("</div>");
			            	html.append("<div class=\"card-body table-responsive p-0\">");
                                html.append("<table id=\"tableRenegociacaoContrato\" class=\"table table-striped table-hover\">");
                                // Não funciona tfoot aqui, incluído via javascript
                                html.append("</table>");
                            html.append("</div>");
		                html.append("</div>");
	                html.append("</div>");
                html.append("</div>");
            }

        } catch (ZetraException e) {
            throw new ViewHelperException(e);
        }

        return html.toString();
    }
}
