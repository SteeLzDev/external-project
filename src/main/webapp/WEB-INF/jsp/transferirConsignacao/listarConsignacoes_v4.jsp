<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
    AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

	String rseCodigoOri = (String) request.getAttribute("rseCodigoOri");
	String rseCodigoDes = (String) request.getAttribute("rseCodigoDes");

	CustomTransferObject servidorOri = (CustomTransferObject) request.getAttribute("servidorOri");
	CustomTransferObject servidorDes = (CustomTransferObject) request.getAttribute("servidorDes");

	List<CustomTransferObject> ades = (List<CustomTransferObject>) request.getAttribute("ades");

	String msgConfirmacao = (String) request.getAttribute("msgConfirmacao");
%>

<c:set var="title">
	<hl:message key="rotulo.transferir.consignacao.titulo" />
</c:set>

<c:set var="imageHeader">
	<use xlink:href="#i-operacional"></use>
</c:set>

<c:set var="bodyContent">
    <%@ include file="../transferirConsignacao/incluirCampoPesquisaAvancada_v4.jsp" %>
	<form method="post" action="../v3/transferirConsignacao" name="form1">
		<div class="row">
			<div class=col-sm>
				<div class="card">
					<div class="card-header">
						<h2 class="card-header-title">
							<hl:message key="rotulo.transferir.consignacao.ser.origem" />
						</h2>
					</div>
					<div class="card-body">
						<dl class="row data-list firefox-print-fix">
							<%-- Utiliza a tag library DetalheServidorTag.java para exibir os dados do servidor --%>
							<% pageContext.setAttribute("servidorOri", servidorOri); %>
							<hl:detalharServidorv4 name="servidorOri" />
						</dl>
					</div>
				</div>
			</div>
			<div class=col-sm>
				<div class="card">
					<div class="card-header">
						<h2 class="card-header-title">
							<hl:message key="rotulo.transferir.consignacao.ser.destino" />
						</h2>
					</div>
					<div class="card-body">
						<dl class="row data-list firefox-print-fix">
							<%-- Utiliza a tag library DetalheServidorTag.java para exibir os dados do servidor --%>
							<% pageContext.setAttribute("servidorDes", servidorDes); %>
							<hl:detalharServidorv4 name="servidorDes" />
						</dl>
					</div>
				</div>
			</div>
		</div>

		<%
		    out.print(SynchronizerToken.generateHtmlToken(request));
		%>
		
		<input type="hidden" name="RSE_CODIGO_ORI" value="<%=TextHelper.forHtmlAttribute(rseCodigoOri)%>" />
		<input type="hidden" name="RSE_CODIGO_DES" value="<%=TextHelper.forHtmlAttribute(rseCodigoDes)%>" />

		<div class="card">
			<div class="card-header">
				<h2 class="card-header-title">Dados da operação</h2>
			</div>
			<div class="card-body">
				<div class="row">
					<div class="col-sm-12 col-md-6">
						<div class="form-group mb-1" role="radiogroup" aria-labelledby="agDescricao">
							<div><span id="agDescricao"><hl:message key="mensagem.ajuda.transf.contratos.transferir.todos"/></span></div>
							<div class="form-check form-check-inline pt-3">
								<input class="form-check-input ml-1" type="radio" name="transferirTodos" id="transferirTodosSim" value="S" title="Sim">
								<label class="form-check-label labelSemNegrito ml-1 pr-4" for="transferirTodosSim"><hl:message key="rotulo.sim"/></label>
							</div>
							<div class="form-check form-check-inline pt-3">
								<input class="form-check-input ml-1" type="radio" name="transferirTodos" id="transferirTodosNao" value="N" title="Não" checked="">
								<label class="form-check-label labelSemNegrito ml-1 pr-4" for="transferirTodosNao"><hl:message key="rotulo.nao"/></label>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
        
		<div class="card">
			<div class="card-header">
				<h2 class="card-header-title">
					<hl:message key="mensagem.informe.contratos.transferencia" />
				</h2>
			</div>
			<div class="card-body table-responsive p-0">
				<table class="table table-striped table-hover">
					<thead>
						<tr>
							<th scope="col" width="3%" class="colunaUnica" style="display: none;">
	                            <div class="form-check">
	                            	<input type="checkbox" class="form-check-input ml-0" name="checkAll_chkADE" id="checkAll_chkADE" data-bs-toggle="tooltip" data-original-title="" alt="" title="">
		                        </div>                  
	                        </th>
							<th scope="col"><hl:message key="<%= (!responsavel.isCsa()) ? "rotulo.consignataria.singular" : "rotulo.correspondente.singular" %>" /></th>
							<th scope="col"><hl:message key="rotulo.consignacao.responsavel" /></th>
							<th scope="col"><hl:message key="rotulo.consignacao.numero" /></th>
							<th scope="col"><hl:message key="rotulo.consignacao.identificador" /></th>
							<th scope="col"><hl:message key="rotulo.servico.singular" /></th>
							<th scope="col"><hl:message key="rotulo.consignacao.data.inclusao" /></th>
							<th scope="col"><hl:message key="rotulo.consignacao.valor.parcela.abreviado" /></th>
							<th scope="col"><hl:message key="rotulo.consignacao.prazo.abreviado" /></th>
							<th scope="col"><hl:message key="rotulo.consignacao.pagas" /></th>
							<th scope="col"><hl:message key="rotulo.consignacao.status" /></th>
							<th scope="col"><hl:message key="rotulo.acoes" /></th>
						</tr>
					</thead>
					<tbody>
						<%
							if (!ades.isEmpty()) {
								String adeNumero, adeCodigo, adeTipoVlr, adeData, adePrazo, adeVlr, adeIdentificador, prdPagas,	adeCodReg;
								String nome, servico, servidor, serTel, sadDescricao, cpf;
								String loginResponsavel, adeResponsavel;

								CustomTransferObject ade = null;
								Iterator<CustomTransferObject> it = ades.iterator();
								while (it.hasNext()) {
									ade = (CustomTransferObject) it.next();

									adeCodigo = ade.getAttribute(Columns.ADE_CODIGO).toString();
									adeTipoVlr = (String) ade.getAttribute(Columns.ADE_TIPO_VLR);
									adeVlr = ade.getAttribute(Columns.ADE_VLR) != null ? ade.getAttribute(Columns.ADE_VLR).toString() : "";
									if (!adeVlr.equals("")) {
										adeVlr = NumberHelper.format(Double.valueOf(adeVlr).doubleValue(), NumberHelper.getLang());
									}
									adeNumero = ade.getAttribute(Columns.ADE_NUMERO).toString();
									adeIdentificador = ade.getAttribute(Columns.ADE_IDENTIFICADOR) != null ? ade.getAttribute(Columns.ADE_IDENTIFICADOR).toString() : "";
									adePrazo = ade.getAttribute(Columns.ADE_PRAZO) != null ? ade.getAttribute(Columns.ADE_PRAZO).toString() : ApplicationResourcesHelper.getMessage("rotulo.indeterminado", responsavel);
									adeData = DateHelper.reformat(ade.getAttribute(Columns.ADE_DATA).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern());
									prdPagas = ade.getAttribute(Columns.ADE_PRD_PAGAS) != null ? ade.getAttribute(Columns.ADE_PRD_PAGAS).toString() : "0";
									servico = (ade.getAttribute(Columns.CNV_COD_VERBA) != null && !ade.getAttribute(Columns.CNV_COD_VERBA).toString().equals("")) ? ade.getAttribute(Columns.CNV_COD_VERBA).toString() : ade.getAttribute(Columns.SVC_IDENTIFICADOR).toString();
									servico += (ade.getAttribute(Columns.ADE_INDICE) != null && !ade.getAttribute(Columns.ADE_INDICE).toString().equals("")) ? ade.getAttribute(Columns.ADE_INDICE).toString() : "";
									servico += " - " + ade.getAttribute(Columns.SVC_DESCRICAO).toString();
									servidor = ade.getAttribute(Columns.RSE_MATRICULA) + " - " + ade.getAttribute(Columns.SER_NOME);
									serTel = ade.getAttribute(Columns.SER_TEL) != null ? ade.getAttribute(Columns.SER_TEL).toString() : "";
									cpf = ade.getAttribute(Columns.SER_CPF).toString();
									adeCodReg = ((ade.getAttribute(Columns.ADE_COD_REG) != null && !ade.getAttribute(Columns.ADE_COD_REG).equals("")) ? ade.getAttribute(Columns.ADE_COD_REG).toString() : CodedValues.COD_REG_DESCONTO);
									nome = (String) ade.getAttribute(Columns.CSA_NOME_ABREV);
									
									if (nome == null || nome.trim().length() == 0) {
										nome = ade.getAttribute(Columns.CSA_NOME).toString();
									}
									
									if (!responsavel.isCsa()) {
										nome = ade.getAttribute(Columns.CSA_IDENTIFICADOR) + " - " + nome;
									} else {
										nome = ade.getAttribute(Columns.COR_IDENTIFICADOR) != null ? ade.getAttribute(Columns.COR_IDENTIFICADOR) + " - " + ade.getAttribute(Columns.COR_NOME) : "";
									}
									
									loginResponsavel = ade.getAttribute(Columns.USU_LOGIN) != null ? ade.getAttribute(Columns.USU_LOGIN).toString() : "";
									adeResponsavel = (loginResponsavel .equalsIgnoreCase((String) ade.getAttribute(Columns.USU_CODIGO)) && ade.getAttribute(Columns.USU_TIPO_BLOQ) != null) ? (ade.getAttribute(Columns.USU_TIPO_BLOQ).toString() + "(*)") : loginResponsavel;
									sadDescricao = ade.getAttribute(Columns.SAD_DESCRICAO).toString();
						%>
						<tr class="selecionarLinha">
							<td class="colunaUnica" aria-label="" title="" data-bs-toggle="tooltip" data-original-title="" style="display: none;">
	                            <div class="form-check">
	                            	<input type="checkbox" class="form-check-input ml-0" name="chkAdeCodigo" value="<%=TextHelper.forHtmlAttribute(adeCodigo)%>">
	                        	</div>
	                        </td>
							<td class="selecionarColuna"><%=TextHelper.forHtmlContent(nome)%></td>
							<td class="selecionarColuna"><%=TextHelper.forHtmlContent(adeResponsavel)%></td>
							<td class="selecionarColuna"><%=TextHelper.forHtmlContent(adeNumero)%></td>
							<td class="selecionarColuna"><%=TextHelper.forHtmlContent(adeIdentificador)%></td>
							<td class="selecionarColuna"><%=TextHelper.forHtmlContent(servico + (adeCodReg.equals(CodedValues.COD_REG_ESTORNO) ? " - " + ApplicationResourcesHelper.getMessage("rotulo.consignacao.cod.reg.credito", responsavel) : ""))%>&nbsp;</td>
							<td class="selecionarColuna"><%=TextHelper.forHtmlContent(adeData)%></td>
							<td class="selecionarColuna"><%=TextHelper.forHtmlContent(ParamSvcTO.getDescricaoTpsTipoVlr(adeTipoVlr))%> <%=TextHelper.forHtmlContent(adeVlr)%></td>
							<td class="selecionarColuna"><%=TextHelper.forHtmlContent(adePrazo)%></td>
							<td class="selecionarColuna"><%=TextHelper.forHtmlContent(prdPagas)%></td>
							<td class="selecionarColuna"><%=TextHelper.forHtmlContent(sadDescricao)%></td>
							<td class="acoes">
								<div class="actions">
                    				<div class="dropdown">
                      					<a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                      						<div class="form-inline">
                      							<span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.mais.acoes"/>" aria-label="<hl:message key="rotulo.mais.acoes"/>">
                          							<svg> <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                          						</span> <hl:message key="rotulo.botao.opcoes" />
                        					</div>
                      					</a>
                      					<div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                         					<a class="dropdown-item" href="#" onclick ="escolhechk('Selecionar',this)"><hl:message key="rotulo.acoes.selecionar" /></a>
                         					<a class="dropdown-item" href="#no-back" aria-label="<hl:message key="rotulo.acoes.editar" />" onClick="doIt('edt', '<%=TextHelper.forJavaScript(adeCodigo)%>');"><hl:message key="rotulo.acoes.editar"/></a>
                      					</div>
									</div>
								</div>
							</td>
						</tr>
						<%
								}
							} else {
						%>
						<tr><td colspan="12"><hl:message key="mensagem.erro.nenhuma.consignacao.encontrada.transferir.consignacao" /></td></tr>
						<%
						    }
						%>
					</tbody>
					<tfoot>
	            		<tr><td colspan="12"><%=ApplicationResourcesHelper.getMessage("rotulo.consignacao.listagem.consignacao", responsavel) + " - " %><span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span></td></tr>
            		</tfoot>
				</table>
				<%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
			</div>
		</div>
		<div class="btn-action">
			<a class="btn btn-outline-danger" id="btnCancelar" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(paramSession.getLastHistory())%>')"><hl:message key="rotulo.botao.cancelar" /></a>
			<a class="btn btn-primary" id="btnConcluir" href="#no-back" onClick="vfAdeCodigo()"><hl:message key="rotulo.botao.concluir" /></a>
		</div>
	
		<input name="acao" type="hidden" value="efetivarAcao">
		<input name="opt" type="hidden" value="tr">
		<input name="flow" type="hidden" value="endpoint">
		<input name="tipo" type="hidden" value="transferir_consignacao">
		<input name="rseMatriculaOri" type="hidden" value="<%=TextHelper.forHtmlAttribute((servidorOri.getAttribute(Columns.RSE_MATRICULA)))%>">
		<input name="rseMatriculaDes" type="hidden" value="<%=TextHelper.forHtmlAttribute((servidorDes.getAttribute(Columns.RSE_MATRICULA)))%>">
	    <% for (CustomTransferObject adeNumero : ades) { %>
        <input type="hidden" name="ADE_CODIGO_NUMBER" value="<%=TextHelper.forHtmlAttribute(adeNumero.getAttribute(Columns.ADE_CODIGO).toString())%>">
        <% } %>
    </form>
</c:set>

<c:set var="javascript">
	<script type="text/JavaScript" src="../js/scripts_2810.js"></script>
	<script type="text/JavaScript" src="../js/validacoes.js"></script>
	<script type="text/JavaScript" src="../js/validaform.js"></script>
	<script type="text/JavaScript" src="../js/xbdhtml.js"></script>
	<script type="text/JavaScript" src="../js/listutils.js"></script>

	<script type="text/JavaScript">
		var f0 = document.getElementsByName("form1")[0];

		function vfAdeCodigo() {
			var cont = 0;
            var transTodos = document.getElementById("transferirTodosSim").value;
			
		    for (i=0; i < f0.elements.length; i++) {
		    	var e = f0.elements[i];
		    	if (((e.type == 'check') || (e.type == 'checkbox')) && e.checked) {
		        	cont++;
		      	}
		    }    

		    if (cont < 1 && !(transTodos == "S")) {
		    	alert('<hl:message key="mensagem.informe.um.contrato.transferencia"/>');
		      	return false;
		    } else if(confirm('<%=TextHelper.forJavaScript(msgConfirmacao)%>')){
		    	f0.submit();
		    }
		}
		
		function doIt(opt, ade) {
			if (opt == 'edt') {
		    	postData('../v3/consultarConsignacao?acao=detalharConsignacao&ADE_CODIGO=' + ade + '&<%=SynchronizerToken.generateToken4URL(request)%>');
		    }
		}

		/* **Click na linha
		 * 1- Mostrar a coluna de checkbox, quando se clica na linha.
		*/
		var clicklinha = false;

		$(".selecionarColuna").click(function() {
			// 1- Seleciona a linha e mostra a coluna dos checks
			
			var checked = $("table tbody tr input[type=checkbox]:checked").length;

			if (checked == 0) {

				if (clicklinha) {
					$("table th:nth-child(-n+1)").hide();
					$(".colunaUnica").hide();
				} else {
					$("table th:nth-child(-n+1)").show();
					$(".colunaUnica").show();
				}

				clicklinha = !clicklinha;
			}
		});
		
		var verificarCheckbox = function () {
			var checked = $("table tbody tr input[type=checkbox]:checked").length;
			var total = $("table tbody tr input[type=checkbox]").length;
			$("input[id*=checkAll_]").prop('checked', checked == total);
			if (checked == 0) {
				$("table thead tr th.colunaUnica, table tbody tr td.colunaUnica").hide();
			} else {
				$("table thead tr th.colunaUnica, table tbody tr td.colunaUnica").show();
			}
		};

		$("table tbody tr td").not("td.colunaUnica, td.acoes").click(function (e) {
			$(e.target).parents('tr').find('input[type=checkbox]').click();
		});

		function escolhechk(idchk,e) {
		 	$(e).parents('tr').find('input[type=checkbox]').click();
		}

		$("table tbody tr input[type=checkbox]").click(function (e) {
			verificarCheckbox();
			var checked = e.target.checked;
			if (checked) {
				$(e.target).parents('tr').addClass("table-checked");
			} else {
				$(e.target).parents('tr').removeClass("table-checked");
			}
		});

		$("input[id*=checkAll_").click(function (e){
			var checked = e.target.checked;
			$('table tbody tr input[type=checkbox]').prop('checked', checked);
			if (checked) {
				$("table tbody tr").addClass("table-checked");
			} else {
				$("table tbody tr").removeClass("table-checked");
			}
			verificarCheckbox();
		});
	</script>
</c:set>

<t:page_v4>
	<jsp:attribute name="header">${title}</jsp:attribute>
	<jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
	<jsp:attribute name="javascript">${javascript}</jsp:attribute>
	<jsp:body>${bodyContent}</jsp:body>
</t:page_v4>