<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

Integer tamanhoMatricula = (Integer) request.getAttribute("tamanhoMatricula");
Integer tamMaxMatricula  = (Integer) request.getAttribute("tamMaxMatricula");
String maskMatricula     = (String) request.getAttribute("maskMatricula");
String rseMatriculaDes   = (String) request.getAttribute("rseMatriculaDes");
String rseMatriculaOri   = (String) request.getAttribute("rseMatriculaOri");
String rotuloBotaoPesquisar = ApplicationResourcesHelper.getMessage("rotulo.botao.pesquisar", responsavel);

List<CustomTransferObject> servidoresOri = (List<CustomTransferObject>) request.getAttribute("servidoresOri");
List<CustomTransferObject> servidoresDes = (List<CustomTransferObject>) request.getAttribute("servidoresDes");

%>

<c:set var="title">
	<hl:message key="rotulo.transferir.consignacao.titulo" />
</c:set>

<c:set var="imageHeader">
	<use xlink:href="#i-operacional"></use>
</c:set>

<c:set var="bodyContent">
	<div class="row justify-content-md-center">
		<div class="col-sm-12 form-check mt-2 form-group">
			<div class="card">
				<div class="card-header hasIcon">
					<span class="card-header-icon"><svg width="26">
						<use xlink:href="#i-consultar"></use></svg></span>
					<h2 class="card-header-title">
						<hl:message key="mensagem.pesquisa.informe.dados" />
					</h2>
				</div>
				<form method="post" action="../v3/transferirConsignacao" name="form1">
					<input type="hidden" name="acao" value="pesquisar" />
					<div class="card-body">
						<div class="row">
							<div class="form-group col-sm-6">
								<label for="RSE_MATRICULA_ORI"><hl:message key="rotulo.servidor.matricula.origem" /></label>
								<hl:htmlinput name="RSE_MATRICULA_ORI" di="RSE_MATRICULA_ORI" 
									type="text"
									mask="<%=TextHelper.forHtmlAttribute(maskMatricula)%>"
									classe="form-control" size="20"
									maxlength="<%=(String)(tamMaxMatricula > 0 ? String.valueOf(tamMaxMatricula) : \"20\") %>"
									value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "RSE_MATRICULA_ORI"))%>"
									placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.matricula", responsavel)%>"
									onBlur="vfRseMatricula(false,'RSE_MATRICULA_ORI')" />
							</div>
						</div>
						<div class="row">
							<div class="form-group col-sm-6">
								<label for="RSE_MATRICULA_DES"><hl:message key="rotulo.servidor.matricula.destino" /></label>
								<hl:htmlinput name="RSE_MATRICULA_DES" di="RSE_MATRICULA_DES"
									type="text"
									mask="<%=TextHelper.forHtmlAttribute(maskMatricula)%>"
									classe="form-control" size="20"
									maxlength="<%=(String)(tamMaxMatricula > 0 ? String.valueOf(tamMaxMatricula) : \"20\") %>"
									value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "RSE_MATRICULA_DES"))%>"
									placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.matricula", responsavel)%>"
									onBlur="vfRseMatricula(false,'RSE_MATRICULA_DES')" />
							</div>
						</div>
					</div>
				</form>
			</div>
			<div class="btn-action">
				<a class="btn btn-outline-danger" id="btnVoltar" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar" /></a>
				<a class="btn btn-primary" id="btnPesquisar" href="#no-back" onClick="validaSubmit(); return false;"><svg width="20"> <use xlink:href="../img/sprite.svg#i-consultar"></use></svg><%=TextHelper.forHtmlAttribute(rotuloBotaoPesquisar)%></a>
			</div>
			<br>

			<% if (!TextHelper.isNull(rseMatriculaOri) && !TextHelper.isNull(rseMatriculaDes)) { %>
			<form method="post" action="../v3/transferirConsignacao" name="form2">
				<input type="hidden" name="acao" value="listarConsignacoes" />
				<input type="hidden" name="RSE_MATRICULA_ORI" value="<%=TextHelper.forHtmlAttribute(rseMatriculaOri)%>" />
				<input type="hidden" name="RSE_MATRICULA_DES" value="<%=TextHelper.forHtmlAttribute(rseMatriculaDes)%>" />

				<div class="row">
					<div class="col-sm-12">
						<div class="card">
							<div class="card-header">
								<h2 class="card-header-title">
									<hl:message key="mensagem.servidor.origem" />
								</h2>
							</div>
							<div class="card-body table-responsive">
								<table class="table table-striped table-hover" id="colunaUnicaOri">
									<thead>
										<tr>
											<th scope="col" width="3%" class="colunaUnicaOri" style="display: none;"></th>
											<th scope="col"><hl:message key="rotulo.servidor.nome" /></th>
											<th scope="col"><hl:message key="rotulo.servidor.cpf" /></th>
											<th scope="col"><hl:message key="rotulo.servidor.status" /></th>
											<th scope="col"><hl:message key="rotulo.servidor.matricula" /></th>
											<th scope="col"><hl:message key="rotulo.orgao.singular" /></th>
											<th scope="col"><hl:message key="rotulo.estabelecimento.abreviado" /></th>
											<th scope="col"><hl:message key="rotulo.acoes" /></th>
										</tr>
									</thead>
									<tbody>
										<%  if (!servidoresOri.isEmpty()) {
												CustomTransferObject servidor = null;
												String serNome, serCpf, rseMatricula, orgNome, rseCodigo, orgIdentificador, estIdentificador, serStatus;
												Iterator<CustomTransferObject> it = servidoresOri.iterator();
												while (it.hasNext()) {
													servidor = (CustomTransferObject)it.next();
													serNome  = (String)servidor.getAttribute(Columns.SER_NOME);      
													serCpf   = (String)servidor.getAttribute(Columns.SER_CPF);
													rseMatricula = (String)servidor.getAttribute(Columns.RSE_MATRICULA);
													orgNome   = (String)servidor.getAttribute(Columns.ORG_NOME);
													rseCodigo = (String)servidor.getAttribute(Columns.RSE_CODIGO);
													orgIdentificador = (String)servidor.getAttribute(Columns.ORG_IDENTIFICADOR);
													estIdentificador = (String)servidor.getAttribute(Columns.EST_IDENTIFICADOR);
													serStatus = (String)servidor.getAttribute(Columns.SRS_DESCRICAO);
										%>
										<tr class="selecionarLinhaOri">
											<td class="colunaUnicaOri" aria-label="" title="" data-bs-toggle="tooltip" data-original-title="" style="display: none;">
						                    	<div class="form-check">
						                    		<input type="checkbox" class="form-check-input ml-0" name="RSE_CODIGO_ORI" value="<%=TextHelper.forHtmlAttribute(rseCodigo)%>" onClick="marcaCheck(f1, 'RSE_CODIGO_ORI', this.value)" />
						                       	</div>
						                    </td>
											<td class="selecionarColunaOri"><%=TextHelper.forHtmlContent(serNome)%></td>
											<td class="selecionarColunaOri"><%=TextHelper.forHtmlContent(serCpf)%></td>
											<td class="selecionarColunaOri"><%=TextHelper.forHtmlContent(serStatus)%></td>
											<td class="selecionarColunaOri"><%=TextHelper.forHtmlContent(rseMatricula)%></td>
											<td class="selecionarColunaOri"><%=TextHelper.forHtmlContent(orgNome + " - " + orgIdentificador)%></td>
											<td class="selecionarColunaOri"><%=TextHelper.forHtmlContent(estIdentificador)%></td>
											<td class="selecionarColunaOri"><a href="#no-back" onclick ="escolhechk('Selecionar',this)"><hl:message key="rotulo.acoes.selecionar" /></a></td>
										</tr>
										<% 
    											}
  											} else {
										%>
										<tr><td colspan="7"><hl:message key="mensagem.erro.nenhum.servidor.encontrado.transferir.consignacao" /></td></tr>
										<% 	} %>
									</tbody>
								</table>
							</div>
						</div>
					</div>
					<br> <br>
					
					<div class="col-sm-12">
						<div class="card">
							<div class="card-header">
								<h2 class="card-header-title">
									<hl:message key="mensagem.servidor.destino" />
								</h2>
							</div>
							<div class="card-body table-responsive p-0">
								<table class="table table-striped table-hover" id="colunaUnicaDes">
									<thead>
										<tr>
											<th scope="col" width="3%" class="colunaUnicaDes" style="display: none;"></th>
											<th scope="col"><hl:message key="rotulo.servidor.nome" /></th>
											<th scope="col"><hl:message key="rotulo.servidor.cpf" /></th>
											<th scope="col"><hl:message key="rotulo.servidor.status" /></th>
											<th scope="col"><hl:message key="rotulo.servidor.matricula" /></th>
											<th scope="col"><hl:message key="rotulo.orgao.singular" /></th>
											<th scope="col"><hl:message key="rotulo.estabelecimento.abreviado" /></th>
											<th scope="col"><hl:message key="rotulo.acoes" /></th>
										</tr>
									</thead>
									<tbody>
										<%
											if (!servidoresDes.isEmpty()) {
											    CustomTransferObject servidor = null;
											    String serNome, serCpf, rseMatricula, orgNome, rseCodigo, orgIdentificador, estIdentificador, serStatus;
											    Iterator<CustomTransferObject> it = servidoresDes.iterator();
											    while (it.hasNext()) {
											    	servidor = (CustomTransferObject)it.next();
											      	serNome  = (String)servidor.getAttribute(Columns.SER_NOME);
											      	serCpf   = (String)servidor.getAttribute(Columns.SER_CPF);
											      	rseMatricula = (String)servidor.getAttribute(Columns.RSE_MATRICULA);
											      	orgNome   = (String)servidor.getAttribute(Columns.ORG_NOME);
											      	rseCodigo = (String)servidor.getAttribute(Columns.RSE_CODIGO);
											      	orgIdentificador = (String)servidor.getAttribute(Columns.ORG_IDENTIFICADOR);
											      	estIdentificador = (String)servidor.getAttribute(Columns.EST_IDENTIFICADOR);
											      	serStatus = (String)servidor.getAttribute(Columns.SRS_DESCRICAO);
										%>
										<tr class="selecionarLinhaDes">
											<td class="colunaUnicaDes" aria-label="" title="" data-bs-toggle="tooltip" data-original-title="" style="display: none;">
												<input name="RSE_CODIGO_DES" type="checkbox" value="<%=TextHelper.forHtmlAttribute(rseCodigo)%>" onClick="marcaCheck(f1, 'RSE_CODIGO_DES', this.value)" />
											</td>
											<td class="selecionarColunaDes"><%=TextHelper.forHtmlContent(serNome)%></td>
											<td class="selecionarColunaDes"><%=TextHelper.forHtmlContent(serCpf)%></td>
											<td class="selecionarColunaDes"><%=TextHelper.forHtmlContent(serStatus)%></td>
											<td class="selecionarColunaDes"><%=TextHelper.forHtmlContent(rseMatricula)%></td>
											<td class="selecionarColunaDes"><%=TextHelper.forHtmlContent(orgNome + " - " + orgIdentificador)%></td>
											<td class="selecionarColunaDes"><%=TextHelper.forHtmlContent(estIdentificador)%></td>
											<td class="selecionarColunaDes"><a href="#no-back" onclick ="escolhechk('Selecionar',this)"><hl:message key="rotulo.acoes.selecionar" /></a></td>
										</tr>
										<%
												}
										  	} else {
										%>
										<tr><td colspan="7"><hl:message key="mensagem.erro.nenhum.servidor.encontrado.transferir.consignacao" /></td></tr>								
										<%
									  		} 
										%>
									</tbody>
								</table>
							</div>
						</div>
					</div>
				</div>
			</form>
			<div class="btn-action">
				<a class="btn btn-outline-danger" id="btnVoltar" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar" /></a>
				<a class="btn btn-primary" id="btnPesquisar" href="#no-back" onClick="vfRseCodigo()"><svg width="20"> <use xlink:href="../img/sprite.svg#i-consultar"></use></svg><%=TextHelper.forHtmlAttribute(rotuloBotaoPesquisar)%></a>
			</div>
			<% } %>
		</div>
	</div>
</c:set>

<c:set var="javascript">
	<script type="text/JavaScript" src="../js/scripts_2810.js"></script>
	<script type="text/JavaScript" src="../js/validaMascara_v4.js"></script>
	<script type="text/JavaScript" src="../js/validacoes.js"></script>
	<script type="text/JavaScript" src="../js/validaform.js"></script>
	<script type="text/JavaScript" src="../js/xbdhtml.js"></script>
	<script type="text/JavaScript" src="../js/listutils.js"></script>

	<script type="text/JavaScript">
    var f0 = document.forms[0];
    var f1 = document.forms[1];

    focusFirstField();

    function vfPesquisa() {
      var matriculaFieldO = document.getElementById('RSE_MATRICULA_ORI');
      var matriculaO = matriculaFieldO.value;
      var matriculaFieldD = document.getElementById('RSE_MATRICULA_DES');
      var matriculaD = matriculaFieldD.value;
      var tamanho = <%=TextHelper.forJavaScriptBlock(tamanhoMatricula)%>;
      var tamMaxMatricula = <%=TextHelper.forJavaScriptBlock(tamMaxMatricula)%>;

      if (matriculaO == '' || matriculaD == '') {
        alert('<hl:message key="mensagem.informe.campo.matricula.origem.destino"/>');
        matriculaFieldO.focus();
        return false;
      } if (matriculaO != '' && matriculaO.length < tamanho) {
        alert('<hl:message key="mensagem.transferir.consignacao.tam.min.matricula.origem" arg0="<%=TextHelper.forHtmlAttribute(String.valueOf(tamanhoMatricula))%>"/>');
        matriculaFieldO.focus();
        return false;
      } else if (matriculaO != '' && tamMaxMatricula > 0 && matriculaO.length > tamMaxMatricula) {
        alert('<hl:message key="mensagem.transferir.consignacao.tam.max.matricula.origem" arg0="<%=TextHelper.forHtmlAttribute(String.valueOf(tamMaxMatricula))%>"/>');
        matriculaFieldO.focus();
        return false;
      } if (matriculaD != '' && matriculaD.length < tamanho) {
        alert('<hl:message key="mensagem.transferir.consignacao.tam.min.matricula.destino" arg0="<%=TextHelper.forHtmlAttribute(String.valueOf(tamanhoMatricula))%>"/>');
        matriculaFieldD.focus();
        return false;
      } else if (matriculaD != '' && tamMaxMatricula > 0 && matriculaD.length > tamMaxMatricula) {
        alert('<hl:message key="mensagem.transferir.consignacao.tam.max.matricula.destino" arg0="<%=TextHelper.forHtmlAttribute(String.valueOf(tamMaxMatricula))%>"/>');
        matriculaFieldD.focus();
        return false;
      } else {
        return true;
      }
    }

    function vfRseCodigo() {
	  var rseCodigoOri;
	  var rseCodigoDes;

      var cont = 0;

      for (i=0; i < f1.elements.length; i++) {
        var e = f1.elements[i];
        if (((e.type == 'check') || (e.type == 'checkbox')) && e.checked) {
          if (e.name == 'RSE_CODIGO_ORI') {
            rseCodigoOri = e.value;
          } else if (e.name == 'RSE_CODIGO_DES') {
            rseCodigoDes = e.value;
          }
          cont++;
        }
      }
      if (cont != 2) {
        alert('<hl:message key="mensagem.informe.servidor.origem.destino"/>');
        return false;
      } else if (rseCodigoOri == rseCodigoDes) {
        alert('<hl:message key="mensagem.informe.servidor.origem.destino.distintos"/>');
        return false;
      } else {
        f1.submit();
      }
    }

    function vfRseMatricula(validaForm, fieldID) 
    {  
      if(validaForm === undefined) {
      	validaForm = false;
   	  }
      
	  var matriculaField = document.getElementById(fieldID);
      var matricula = matriculaField.value;
      var tamMinMatricula = <%=TextHelper.forJavaScriptBlock(tamanhoMatricula)%>;    
      var tamMaxMatricula = <%=TextHelper.forJavaScriptBlock(tamMaxMatricula)%>;

      if (matricula != ''){    	
        if(validaForm){
          if(matricula.length < tamMinMatricula){
            alert('<hl:message key="mensagem.erro.matricula.tamanho.min" arg0="<%=TextHelper.forHtmlAttribute(String.valueOf(tamanhoMatricula))%>"/>');
        	if (QualNavegador() == "NE") {
              globalvar = matriculaField;
              setTimeout("globalvar.focus()",0);
            } else {
                matriculaField.focus();
            }        	
      	  } else if(tamMaxMatricula > 0 && matricula.length > tamMaxMatricula){
            alert('<hl:message key="mensagem.erro.matricula.tamanho.max" arg0="<%=TextHelper.forHtmlAttribute(String.valueOf(tamMaxMatricula))%>"/>');
						if (QualNavegador() == "NE") {
							globalvar = matriculaField;
							setTimeout("globalvar.focus()", 0);
						} else {
							matriculaField.focus();
						}
					} else {
						matriculaField.style.color = 'black';
						return true;
					}
				} else {
					if (matricula.length < tamMinMatricula) {
						matriculaField.style.color = 'red';
						return false;
					} else if (tamMaxMatricula > 0
							&& matricula.length > tamMaxMatricula) {
						matriculaField.style.color = 'red';
						return false;
					} else {
						matriculaField.style.color = 'black';
						return true;
					}
				}
			} else {
				matriculaField.style.color = 'black';
				return true;
			}
		}

		function validaSubmit() {
			if (vfPesquisa()) {
				if (typeof vfRseMatricula === 'function') {
					if (vfRseMatricula(true, 'RSE_MATRICULA_ORI')
							&& vfRseMatricula(true, 'RSE_MATRICULA_DES')) {
						f0.submit();
					}
				} else {
					f0.submit();
				}
			}
		}

		function marcaCheck(form, chkNome, chkValor) {
		  for (i=0; i < form.elements.length; i++) {
		    var e = form.elements[i];
		    if (((e.type == 'check') || (e.type == 'checkbox')) && (e.name == chkNome)) {
		      if (e.value != chkValor) {
		        e.checked = false;
		      }        
		    }
		  }
		}

		function escolhechk(idchk,e) {
		 	$(e).parents('tr').find('input[type=checkbox]').click();
		}

		$("table tbody tr td.selecionarColunaOri, td.selecionarColunaDes").click(function (e) {
			if(e.target.tagName != 'A') {
				$(e.target).parents('tr').find('input[type=checkbox]').click();
			}
		});

		function verificarCheckbox (name) {
			var checked;
			var tabela;
			if(name == "RSE_CODIGO_ORI"){
				checked = $("table[id='colunaUnicaOri'] tbody tr input[type=checkbox]:checked").length;
				tabela = "Ori";
			} else {
				checked = $("table[id='colunaUnicaDes'] tbody tr input[type=checkbox]:checked").length;
				tabela = "Des";
			}

			if (checked == 0) {
				$("table thead tr th.colunaUnica" + tabela +", table tbody tr td.colunaUnica" + tabela).hide();
			} else {
				$("table thead tr th.colunaUnica" + tabela +", table tbody tr td.colunaUnica" + tabela).show();
			}
		};

		$("table tbody tr input[type=checkbox]").click(function (e) {
			verificarCheckbox(e.target.name);
			var checked = e.target.checked;
			var origem;

			if(e.target.name == "RSE_CODIGO_ORI"){
				origem = "selecionarLinhaOri";
			} else {
				origem = "selecionarLinhaDes";
			}

			var elementos = document.getElementsByClassName(origem); 
			
			for (i=0; i < elementos.length; i++) {
			    var elemento = elementos[i];
				$(elemento).removeClass("table-checked");
			}
			
			if (checked) {
				$(e.target).parents('tr').addClass("table-checked");
			}
		});
	</script>
</c:set>

<t:page_v4>
	<jsp:attribute name="header">${title}</jsp:attribute>
	<jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
	<jsp:attribute name="javascript">${javascript}</jsp:attribute>
	<jsp:body>${bodyContent}</jsp:body>
</t:page_v4>