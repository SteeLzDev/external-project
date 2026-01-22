<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
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
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />

<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

Boolean temCET = (Boolean) request.getAttribute("temCET");
Boolean editaTaxaCet = (Boolean) request.getAttribute("editaTaxaCet");
String subtipo = (String) request.getAttribute("subtipo");
String svcDescricao = (String) request.getAttribute("svcDescricao");
String titulo = (String) request.getAttribute("titulo");
Boolean editar = (Boolean) request.getAttribute("editar");
Map svcBloqEdicaoTaxas = (Map) request.getAttribute("svcBloqEdicaoTaxas");
String svcCodigo = (String) request.getAttribute("svcCodigo");
Boolean temLimiteTaxa = (Boolean) request.getAttribute("temLimiteTaxa");
String mesReferencia = (String) request.getAttribute("mesReferencia");
List<TransferObject> limiteTaxa = (List<TransferObject>) request.getAttribute("limiteTaxa");
String dataAbertura = (String) request.getAttribute("dataAbertura");
String dataLimite = (String) request.getAttribute("dataLimite");
String ordTaxas = (String) request.getAttribute("ordTaxas");
Boolean readOnly = (Boolean) request.getAttribute("readOnly");
Boolean ocultarCamposTac = (Boolean) request.getAttribute("ocultarCamposTac");
String tipoTac = (String) request.getAttribute("tipoTac");
String tac = (String) request.getAttribute("tac");
String valorMinTac = (String) request.getAttribute("valorMinTac");
String valorMaxTac = (String) request.getAttribute("valorMaxTac");
Date dataCadastro = (Date) request.getAttribute("dataCadastro");
Date dataIniVigencia = (Date) request.getAttribute("dataIniVigencia");
Boolean dataInicialFutura = (Boolean) request.getAttribute("dataInicialFutura");
String periodo = (String) request.getAttribute("periodo");
List prazos = (List) request.getAttribute("prazos");
Map<String, String> prazosFile = (Map<String, String>) request.getAttribute("prazosFile");
String csaCodigo = (String) request.getAttribute("csaCodigo");
String tipo = (String) request.getAttribute("tipo");
Boolean temLimiteMaxTacCse = (Boolean) request.getAttribute("temLimiteMaxTacCse");
String maxTacCseEn = (String) request.getAttribute("maxTacCseEn");
String maxTacCsePt = (String) request.getAttribute("maxTacCsePt");
Map coeficientes = (Map) request.getAttribute("coeficientes");
List<TransferObject> servicos = (List<TransferObject>) request.getAttribute("servicos");
Boolean taxaJurosObrigatoria = (Boolean) request.getAttribute("taxaJurosObrigatoria");
Boolean exibeCETMinMax = (Boolean) request.getAttribute("exibeCETMinMax");

String prazoInicial = String.valueOf(((PrazoTransferObject) prazos.get(0)).getPrzVlr());
String prazoFinal = String.valueOf(((PrazoTransferObject) prazos.get(prazos.size() - 1)).getPrzVlr());

String jscript = "";
String jscriptVazio = "if (";
StringBuffer arrayPrazos = new StringBuffer();

//Exibe Botao Rodapé
boolean exibeBotaoRodape = request.getAttribute("exibeBotaoRodape") != null && (boolean) request.getAttribute("exibeBotaoRodape");
%>

<c:set var="title">
<%=temCET ? ApplicationResourcesHelper.getMessage("rotulo.editar.cet.titulo", responsavel, svcDescricao.toUpperCase(), titulo.toUpperCase()) : ApplicationResourcesHelper.getMessage("rotulo.editar.taxa.juros.titulo", responsavel, svcDescricao.toUpperCase(), titulo.toUpperCase())%>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <FORM NAME="form1" METHOD="post" ACTION="../v3/manterTaxas?subtipo=<%=TextHelper.forHtmlAttribute(subtipo)%>&<%=SynchronizerToken.generateToken4URL(request)%>">
      <% if (editar) { %>
      <div class="alert alert-warning mb-1" role="alert">
        <p class="mb-0"><%=temCET ? ApplicationResourcesHelper.getMessage("mensagem.informacao.cet.taxas", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.informacao.taxa.juros.taxas", responsavel) %></p>
      </div>
      <% } else if (svcBloqEdicaoTaxas.containsKey(svcCodigo)) { %>  
          <div class="alert alert-warning mb-1" role="alert">
            <p class="mb-0"><hl:message key="mensagem.informacao.taxa.juros.compartilhadas"/>
          <% CustomTransferObject sto = (CustomTransferObject) request.getAttribute("sto");
             String svc_descricao = (sto != null)? (String) sto.getAttribute(Columns.SVC_DESCRICAO): "";
             if (!TextHelper.isNull(svc_descricao)) { %>                
              <BR><%=ApplicationResourcesHelper.getMessage("mensagem.informacao.taxa.juros.editar.servico", responsavel, svc_descricao)%>
          <% } %>
          <BR></p>
          </div>
      <% } %>
      <div class="alert alert-warning mb-1" role="alert">
        <p class="mb-0"><%=temCET ? ApplicationResourcesHelper.getMessage("mensagem.informacao.cet.mes.referencia", responsavel, mesReferencia) : ApplicationResourcesHelper.getMessage("mensagem.informacao.taxa.juros.mes.referencia", responsavel, mesReferencia)%></p>
      </div>
      <% if (temLimiteTaxa) { %>
        <div class="alert alert-warning mb-1" role="alert">
          <p class="mb-0"><hl:message key="mensagem.informacao.taxa.juros.limite.maximo"/>:</p>
        <%
          for (TransferObject ctoLimiteTaxa : limiteTaxa) {
            String przLimiteTaxa = ctoLimiteTaxa.getAttribute(Columns.LTJ_PRAZO_REF).toString();
            String vlrLimiteTaxa = ctoLimiteTaxa.getAttribute(Columns.LTJ_JUROS_MAX).toString();
        %>
            <p class="mb-0"><%=ApplicationResourcesHelper.getMessage("mensagem.informacao.taxa.juros.limite.maximo.item", responsavel, NumberHelper.reformat(vlrLimiteTaxa, "en", NumberHelper.getLang(), 2, 4), przLimiteTaxa)%></p>
        <% } %>
        </div>
      <% } %>
      <div class="alert alert-warning mb-1" role="alert">
        <p class="mb-0"><%=temCET ? ApplicationResourcesHelper.getMessage("mensagem.informacao.cet.data.abertura", responsavel, dataAbertura, mesReferencia) : ApplicationResourcesHelper.getMessage("mensagem.informacao.taxa.juros.data.abertura", responsavel, dataAbertura, mesReferencia)%></p>
      </div>
      <% if (!TextHelper.isNull(dataLimite)) { %>
        <div class="alert alert-warning mb-1" role="alert">
          <p class="mb-0"><%=temCET ? ApplicationResourcesHelper.getMessage("mensagem.informacao.cet.data.cadastro", responsavel, mesReferencia, dataLimite) : ApplicationResourcesHelper.getMessage("mensagem.informacao.taxa.juros.data.cadastro", responsavel, mesReferencia, dataLimite)%></p>
        </div>
      <% } %>
      <% if (!ordTaxas.equals(CodedValues.ORDEM_TAXAS_NA)) { %>
         <div class="alert alert-warning mb-1" role="alert">
           <p class="mb-0"><%=(ordTaxas.equals(CodedValues.ORDEM_TAXAS_ASC)) ? ApplicationResourcesHelper.getMessage("mensagem.informacao.taxa.juros.ordem.crescente", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.informacao.taxa.juros.ordem.decrescente", responsavel)%></p>
         </div>   
      <% } %>
	  <div id="alertaEdicaoCETJuros" class="alert alert-warning mb-1" role="alert" style="display: none">
          <p class="mb-0"><%=ApplicationResourcesHelper.getMessage("mensagem.alerta.edicao.cet.taxa.juros.dados.nao.salvos", responsavel)%></p>
      </div>
      <script language="JavaScript" type="text/JavaScript">
      function carrega_arquivo() {
          var file = document.getElementById('FILE1').value;
      
          if (file == '' || file == null) {
            alert('<%=ApplicationResourcesHelper.getMessage("rotulo.lst.arq.generico.encontrado", responsavel)%>');
              return false;    
          }
        
        if (confirm('<%=ApplicationResourcesHelper.getMessage("mensagem.taxa.juros.carga.arquivo.info", responsavel)%>')) {
            f0.submit();
            return true;
        }
      
        return false;
      }
      </script>
      <div class="row mt-3">
        <div class="col-sm-12 col-md-6">
          <div class="row">
            <div class="col-sm-12">
              <div class="card">
                <div class="card-header">
                  <h2 class="card-header-title"><hl:message key="rotulo.taxa.juros.data.atualizacao"/></h2>
                </div>
                <div class="card-body">
                  <dl class="row data-list">
                    <dt class="col-5"><hl:message key="rotulo.taxa.juros.data.cadastro"/>:</dt>
                    <dd class="col-7"><%=(dataCadastro != null ? DateHelper.toDateString(dataCadastro) : ApplicationResourcesHelper.getMessage("rotulo.taxa.juros.data.cadastro.nd", responsavel))%></dd>
                    <dt class="col-5" id="iDataInicioVigencia"><hl:message key="rotulo.taxa.juros.data.vigencia"/>:</dt>
                    <% String strDataIniVig = (dataIniVigencia != null) ? DateHelper.toDateString(dataIniVigencia) : DateHelper.reformat(periodo, "yyyy-MM-dd", LocaleHelper.getDatePattern()); %>
                    <% if (dataInicialFutura && (responsavel.isCseSup() || responsavel.isCsa()) && responsavel.temPermissao(CodedValues.FUN_ATIVAR_TAXA_JUROS_DATA_FUTURA)) { %>
                    <dd class="col-7">  
                      <div class="row">
                        <div class="pl-0 col-sm-6 col-7">
                        <hl:htmlinput name="CFT_DATA_VIG" di="CFT_DATA_VIG"
                          type="text"
                          classe="form-control"
                          value="<%=TextHelper.forHtmlAttribute(strDataIniVig)%>"
                          size="10"
                          mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                          autoSkip="false"
                          ariaLabelBy="iDataInicioVigencia"
                        />
                        </div>
                        <div class="col-sm-4 col-4 mr-4">
                          <input type="hidden" name="CFT_DATA_VIG_OLD" value="<%=TextHelper.forHtmlAttribute(strDataIniVig)%>">   
                          <a class="btn btn-primary" title="<hl:message key="mensagem.ativar.taxa.juros.data.vigencia.clique.aqui"/>" alt="<hl:message key="mensagem.ativar.taxa.juros.data.vigencia.clique.aqui"/>" href="#no-back" onClick="if (vf_data_vigencia()) {f0.submit();} return false;">
                            <hl:message key="rotulo.botao.ativar"/>
                          </a>
                        </div>
                      </div>
                    </dd>    
                    <% } else { %>
                    <dd class="col-7">  
                          <%=TextHelper.forHtmlContent(strDataIniVig)%>
                    </dd> 
                    <% } %>
                    
                  </dl>
                </div>
              </div>
            </div>
            <% if (!readOnly) { %>
            <div class="col-sm-12">
              <div class="card">
                <div class="card-header">
                  <h2 class="card-header-title">
                    <% String msgCabecalho = "";
                    if (temCET) {
                     msgCabecalho = "mensagem.taxa.juros.selecione.arquivo.cet";
                    } else { 
                	    msgCabecalho = "mensagem.taxa.juros.selecione.arquivo.taxas";
                    } %>
                    <hl:message key="<%=msgCabecalho %>"/>
                  </h2>
                </div>
                <div class="card-body">
                    <hl:fileUploadV4 
                        divClassArquivo="col-sm-12" 
                        mostraCampoDescricao="false"
                        extensoes="<%=new String[]{"txt"}%>" 
                        tipoArquivo="carga_cft" />
                </div>
              </div>
              <div class="btn-action">
                <a class="btn btn-primary" href="#no-back" onClick="carrega_arquivo(); return false;">
                  <svg width="17">
                    <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-confirmar"></use>
                  </svg>
                  <hl:message key="rotulo.botao.enviar"/>
                </a>
              </div>
            </div>
            <% } %>
            <% if (!temCET && !ocultarCamposTac) { %>
            <div class="col-sm-12">
              <div class="card">
                <div class="card-header">
                  <h2 class="card-header-title">
                    <hl:message key="rotulo.taxa.juros.tac.titulo"/>
                  </h2>
                </div>  
                <div class="card-body">
                  <div class="row">
                    <div class="form-group col-sm-12">
                      <div class="form-group" role="radiogroup" aria-labelledby="iFormaDoTac">
                        <span id="iFormaDoTac"><hl:message key="rotulo.taxa.juros.tipo.tariva"/></span>
                        <div class="form-check pt-2">
                          <input class="form-check-input ml-1" type="radio" name="nTacPercenteOureal" id="iRadioTacReal" onchange="habilitaDesabilita()"checked>
                          <label class="form-check-label labelSemNegrito ml-1 pr-4" for="iRadioTacReal"><hl:message key="rotulo.taxa.juros.tac.valor"/></label>
                          <input class="form-check-input ml-1" type="radio" name="nTacPercenteOureal" id="iRadioTacPercente" onchange="habilitaDesabilita()">
                          <label class="form-check-label labelSemNegrito ml-1 pr-4" for="iRadioTacPercente"><hl:message key="rotulo.taxa.juros.tac.percentual"/></label>
                        </div>
                      </div>
                      <div class="row">
                        <div class="col-sm-8" id="iColTacReal">
                          <label class="ml-2" for="iTacReal"><hl:message key="rotulo.taxa.juros.tac.valor"/></label>
                          <input type="text" class="form-control" id="iTacReal" placeholder="Digite o TAC em real" value="1,00" name="tps_TAC_F" value="<%=TextHelper.forHtmlAttribute((!tipoTac.equals("P")) ? tac : "")%>" old="<%=TextHelper.forHtmlAttribute((!tipoTac.equals("P")) ? tac : "")%>" type="text" onFocus="SetarEventoMascaraV4(this,'#F7',true);" onBlur="fout(this);ValidaMascaraV4(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"  size="7" <%=(String)(readOnly ? "disabled" : "")%>>
                        </div>
                      </div>
                      <div class="row">
                        <div class="col-sm-8" id="iColTacPercent" >
                          <label class="ml-2" for="iTacPercent"><hl:message key="rotulo.taxa.juros.tac.percentual"/></label>
                          <input type="text" class="form-control" id="iTacPercent" placeholder="Digite o TAC em porcentagem" name="tps_TAC_P" value="<%=TextHelper.forHtmlAttribute((tipoTac.equals("P")) ? tac : "")%>" old="<%=TextHelper.forHtmlAttribute((tipoTac.equals("P")) ? tac : "")%>" type="text" onFocus="SetarEventoMascaraV4(this,'#F7',true);" onBlur="fout(this);ValidaMascaraV4(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"  size="7" <%=(String)(readOnly ? "disabled" : "")%>>
                        </div>
                      </div>
                      <div class="row">
                        <div class="col-sm-8" id="iColTacMin" >
                          <label class="ml-2" for="iValorMinTac"><hl:message key="rotulo.taxa.juros.tac.valor.minimo"/></label>
                          <input type="text" class="form-control" id="iValorMinTac" name="tps_<%=(String)CodedValues.TPS_VALOR_MIN_TAC%>" value="<%=TextHelper.forHtmlAttribute(valorMinTac)%>" old="<%=TextHelper.forHtmlAttribute(valorMinTac)%>" type="text" onFocus="SetarEventoMascara(this,'#F7',true);" onBlur="fout(this);ValidaMascara(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" size="7" <%=(String)(readOnly ? "disabled" : "")%> placeholder="Digite o valor minimo">
                        </div>
                      </div>
                      <div class="row">
                        <div class="col-sm-8" id="iColTacMax"  >
                          <label class="ml-2" for="iValorMaxTac"><hl:message key="rotulo.taxa.juros.tac.valor.maximo"/></label>
                          <input type="text" class="form-control" id="iValorMaxTac" name="tps_<%=(String)CodedValues.TPS_VALOR_MAX_TAC%>" value="<%=TextHelper.forHtmlAttribute(valorMaxTac)%>" old="<%=TextHelper.forHtmlAttribute(valorMaxTac)%>" type="text" onFocus="SetarEventoMascara(this,'#F7',true);" onBlur="fout(this);ValidaMascara(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" size="7" <%=(String)(readOnly ? "disabled" : "")%> placeholder="Digite o valor maximo">
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>  
            <% } %>
          </div>
        </div>		
        <div class="col-sm-12 col-md-6">
			<% if (editaTaxaCet && temCET) { %>
			<div class="card">
	            <div class="card-header">	              
	                <h2 class="card-header-title"><hl:message key="rotulo.taxa.juros.cet.titulo"/></h2>     				  
				</div> 
				<div class="card-body">
					<div class="form-group">
						<div class="row">
							<% if (exibeCETMinMax) { %>
		                    <div class="col-sm-6">
		                      <label class="ml-2" ><hl:message key="rotulo.taxa.juros.prazo.cet.minimo"/></label>							  
		                      <hl:htmlinput name="cetMinimo" type="hidden" value="" />
							  <hl:htmlinput  di="cetMinimo" name="cetMinimo" type="text" classe="form-control" mask="#F20"
							  	                                    onBlur=" if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 4);}"
							  	                                    size="10"/>
		                    </div>
							<% } else { %>
								<div class="col-sm-6">
			                      <label class="ml-2" ><hl:message key="rotulo.taxa.juros.prazo.cet"/></label>							  
			                      <hl:htmlinput name="cet" type="hidden" value="" />
								  <hl:htmlinput  di="cet" name="cet" type="text" classe="form-control" mask="#F20"
								  	                                    onBlur=" if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 4);}"
								  	                                    size="10"/>
			                    </div>
							<% } %>
		                    <div class="col-sm-6">
		                      <label class="ml-2" for="prazoInicio"><hl:message key="rotulo.taxa.juros.prazo.inicio"/></label>
		                      <hl:htmlinput name="prazoInicio" type="hidden" value="<%= prazoInicial %>" />
		                      <hl:htmlinput di="prazoInicio" name="prazoInicio" type="text" value="<%= prazoInicial %>" classe="form-control" mask="#F20"
		                                    onBlur=" if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 0);}"
		                                    size="10"/>
		                    </div>
	                    </div>
						<div class="row">
							<% if (exibeCETMinMax) { %>
								<div class="col-sm-6">
			                      <label class="ml-2" ><hl:message key="rotulo.taxa.juros.prazo.cet.maximo"/></label>							  
			                      <hl:htmlinput name="cet" type="hidden" value="" />
								  <hl:htmlinput  di="cet" name="cet" type="text" classe="form-control" mask="#F20"
								  	                                    onBlur=" if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 4);}"
								  	                                    size="10"/>
			                    </div>
							<% } else { %>
								<div class="col-sm-6">
			                      <label class="ml-2" ><hl:message key="rotulo.taxa.juros.prazo.taxa.juros"/></label>
			                      <hl:htmlinput name="taxaJuros" type="hidden" />
								  <hl:htmlinput di="taxaJuros" name="taxaJuros" type="text" classe="form-control" mask="#F20"
								  	                                    onBlur=" if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 4);}"
								  	                                    size="10"/>
			                    </div>
							<% } %>
		                    <div class="col-sm-6">
		                      <label class="ml-2" for="prazoFinal"><hl:message key="rotulo.taxa.juros.prazo.final"/></label>
		                      <hl:htmlinput name="prazoFinal" type="hidden" value="<%= TextHelper.forHtmlAttribute(prazoFinal) %>"/>
		                      <hl:htmlinput di="prazoFinal" name="prazoFinal" type="text" value="<%= TextHelper.forHtmlAttribute(prazoFinal) %>" classe="form-control" mask="#F20"
		                                    onBlur=" if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 0);}"
		                                    size="10"/>
		                    </div>
	                    </div>
						<% if (exibeCETMinMax) { %>
							<div class="row">							
								<div class="col-sm-6">
			                      <label class="ml-2" ><hl:message key="rotulo.taxa.juros.prazo.taxa.juros"/></label>
			                      <hl:htmlinput name="taxaJuros" type="hidden"/>
								  <hl:htmlinput di="taxaJuros" name="taxaJuros" type="text" classe="form-control" mask="#F20"
								  	                                    onBlur=" if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 4);}"
								  	                                    size="10"/>
			                    </div>							
		                    </div>
						<% } %>
					</div>
					<div class="btn-action">
		                <a class="btn btn-primary" href="#no-back" onClick="atualizar(); return false;">
		                  <svg width="17">
		                    <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-confirmar"></use>
		                  </svg>
		                  <hl:message key="rotulo.botao.atualizar"/>
		                </a>
	                </div>
			 </div>
		  </div> 	
		  <% } %>					
          <div class="card">
            <div class="card-header">
              <% if (editaTaxaCet && temCET) { %>
                <h2 class="card-header-title"><hl:message key="rotulo.taxa.juros.cet.faixas.prazos"/></h2>              
              <% } else if (editar) { %>
                <h2 class="card-header-title"><hl:message key="rotulo.editar.titulo"/><%=temCET ? ApplicationResourcesHelper.getMessage("rotulo.cet.abreviado", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.taxa.juros.abreviado", responsavel) %></h2>
              <% } else { %>
                <h2 class="card-header-title"><%=temCET ? ApplicationResourcesHelper.getMessage("rotulo.cet.abreviado", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.taxa.juros.abreviado", responsavel) %></h2>             
              <% } %>
            </div>
            <div class="card-body">
              <div class="col-sm-12 pl-0 pr-0">  
                <% if (!(editaTaxaCet && temCET)) { %>
                <h3 class="legend">
                  <span id="iPrazo"><hl:message key="rotulo.taxa.juros.prazo.plural"/></span>
                </h3>
                <% } %>
              </div>
              <div class="form-group" aria-labelledby="iPrazo">  
                <div class="row">
                  <%
                  CustomTransferObject cto = null;
                  String cftCodigo, nomeCampo, cftVlr, przCsa = "";
				  String cftVlrMinimo = "";
                  String cftVlrRef = "";
                  Short przVlr;
                  %>
                  <% 
                    Iterator it = prazos.iterator();
                    int itens = 0;
                    while (it.hasNext()) {
                      itens++;
                      PrazoTransferObject pto = (PrazoTransferObject)it.next();
                      przVlr = pto.getPrzVlr();
                      nomeCampo = String.valueOf(przVlr);
                      arrayPrazos.append(String.valueOf(przVlr)).append(",");
                      boolean carregadoArq = false;
                  
                      cto = (CustomTransferObject) coeficientes.get(przVlr.toString());
                      if (prazosFile != null && prazosFile.containsKey(nomeCampo)) {
                          cftVlr = NumberHelper.reformat(prazosFile.get(nomeCampo), "en", NumberHelper.getLang(), 2, 20);						  
                          carregadoArq = true;
                      } else if (cto != null) {
						cftVlr = NumberHelper.reformat(cto.getAttribute(Columns.CFT_VLR).toString(), "en", NumberHelper.getLang(), 2, 20);
                        cftVlrMinimo = (!TextHelper.isNull(cto.getAttribute(Columns.CFT_VLR_MINIMO)) ? NumberHelper.reformat(cto.getAttribute(Columns.CFT_VLR_MINIMO).toString(), "en", NumberHelper.getLang(), 2, 20) : "");
                        cftVlrRef = (!TextHelper.isNull(cto.getAttribute(Columns.CFT_VLR_REF)) ? NumberHelper.reformat(cto.getAttribute(Columns.CFT_VLR_REF).toString(), "en", NumberHelper.getLang(), 2, 20) : "");
                        cftCodigo = (cto.getAttribute(Columns.CFT_DATA_INI_VIG) == null && cto.getAttribute(Columns.CFT_CODIGO) != null) ? cto.getAttribute(Columns.CFT_CODIGO).toString() : "";
                        przCsa = (cto.getAttribute(Columns.CFT_PRZ_CSA_CODIGO) == null && cto.getAttribute(Columns.CFT_PRZ_CSA_CODIGO) != null) ? cto.getAttribute(Columns.CFT_PRZ_CSA_CODIGO).toString() : "";
                      } else {
                        cftVlr = "";
						cftVlrMinimo = "";
                      }  
                        
                      if (temLimiteTaxa) {
                        String vlrMaxJuros = "";
                        String vlrMaxTaxaCommposicaoCET = "";
                      Iterator itLimite = limiteTaxa.iterator();
                      while (itLimite.hasNext()) {
                          CustomTransferObject ctoLte = (CustomTransferObject)itLimite.next();
                          if (przVlr.intValue() <= Integer.parseInt(ctoLte.getAttribute(Columns.LTJ_PRAZO_REF).toString())) {
                          vlrMaxJuros = ctoLte.getAttribute(Columns.LTJ_JUROS_MAX).toString();
                          vlrMaxTaxaCommposicaoCET = ctoLte.getAttribute(Columns.LTJ_VLR_REF) != null ? ctoLte.getAttribute(Columns.LTJ_VLR_REF).toString() : "";
                                break;
                          }
                      }
                        if (!vlrMaxJuros.equals("")) {
                          jscript += "if (parseFloat(f0.cft_" + nomeCampo + ".value.replace(',', '.')) > " + vlrMaxJuros + ") ";
                          jscript += "{ alert('" + ApplicationResourcesHelper.getMessage("mensagem.erro.taxa.juros.valor.excedido", responsavel, (temCET ? ApplicationResourcesHelper.getMessage("rotulo.taxa.juros.prazo.cet", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.taxa.juros.abreviado", responsavel)), NumberHelper.format(Double.valueOf(vlrMaxJuros).doubleValue(), NumberHelper.getLang())) + "'); ";
                          jscript += "f0.cft_" + nomeCampo + ".focus(); ";
                          jscript += "return false; }";
                        }
                        
                        if (!vlrMaxTaxaCommposicaoCET.equals("")) {
                            jscript += "if (parseFloat(f0.taxa_" + nomeCampo + ".value.replace(',', '.')) > " + vlrMaxTaxaCommposicaoCET + ") ";
                            jscript += "{ alert('" + ApplicationResourcesHelper.getMessage("mensagem.erro.taxa.juros.valor.excedido", responsavel, (temCET ? ApplicationResourcesHelper.getMessage("rotulo.taxa.juros.prazo.taxa.juros", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.taxa.juros.abreviado", responsavel)), NumberHelper.format(Double.valueOf(vlrMaxTaxaCommposicaoCET).doubleValue(), NumberHelper.getLang())) + "'); ";
                            jscript += "f0.taxa_" + nomeCampo + ".focus(); ";
                            jscript += "return false; }";
                        }

                        if (taxaJurosObrigatoria) {
                            jscript += "if (parseFloat(f0.taxa_" + nomeCampo + ".value.replace(',', '.')) > parseFloat(f0.cft_" + nomeCampo + ".value.replace(',', '.'))) ";
                            jscript += "{ alert('" + ApplicationResourcesHelper.getMessage("mensagem.erro.limite.taxa.juros.composicao.cet.valor.maximo", responsavel) + "'); ";
                            jscript += "f0.taxa_" + nomeCampo + ".focus(); ";
                            jscript += "return false; }";

                            jscript += "if ((f0.taxa_" + nomeCampo + ".value === '') && (parseFloat(f0.cft_" + nomeCampo + ".value.replace(',', '.')) > 0)) ";
                            jscript += "{ alert('" + ApplicationResourcesHelper.getMessage("mensagem.erro.limite.taxa.juros.obrigatoria", responsavel) + "'); ";
                            jscript += "f0.taxa_" + nomeCampo + ".focus(); ";
                            jscript += "return false; }";
                        }
                      }
                      jscriptVazio += "(f0.cft_" + nomeCampo + ".value == '' || parseFloat(f0.cft_" + nomeCampo + ".value.replace(',', '.')) < 0) || ";
					  if (exibeCETMinMax) {
					  	jscriptVazio += "(f0.cft_min_" + nomeCampo + ".value == '' || parseFloat(f0.cft_min_" + nomeCampo + ".value.replace(',', '.')) < 0) || ";
					  }
                  %>
                  <div class="col-sm-4 divCftValor">
                  <% if (!(editaTaxaCet && temCET)) { %>
                    <label class="ml-2" for="<%=TextHelper.forHtmlAttribute("cft_" + nomeCampo)%>"><%=TextHelper.forHtmlContent(przVlr)%><%if (carregadoArq) {%>*<%} %></label>
                    <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(nomeCampo)%>" type="hidden" value="<%=TextHelper.forHtmlAttribute(przCsa)%>" />
                    <hl:htmlinput di="<%=TextHelper.forHtmlAttribute(\"cft_\" + nomeCampo)%>" name="<%=TextHelper.forHtmlAttribute(\"cft_\" + nomeCampo)%>" type="text" classe="form-control" mask="#F20"
                                  onBlur=" if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 4);}"
                                  size="10" value="<%=TextHelper.forHtmlAttribute(cftVlr)%>" others="<%=TextHelper.forHtmlAttribute(\"old='\" + cftVlr + \"'\" + (readOnly ? \" disabled\" : \"\"))%>" />
                      
                  <% } else { %>  
                    <h3 class="legend">
                      <span id="iPrazo"><hl:message key="rotulo.taxa.juros.prazo"/> <%=TextHelper.forHtmlContent(przVlr)%><%if (carregadoArq) {%>*<%} %></span>
                    </h3>
                    <div class="row">
						<% if (exibeCETMinMax) { %>
					        <!-- CET Mínimo -->
						    <div class="col-sm-12">
		                      <label class="ml-2" for="<%=TextHelper.forHtmlAttribute("cft_min_" + nomeCampo)%>"><hl:message key="rotulo.taxa.juros.prazo.cet.minimo"/></label>
		                      <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(nomeCampo)%>" type="hidden" value="<%=TextHelper.forHtmlAttribute(przCsa)%>" />
		                      <hl:htmlinput di="<%=TextHelper.forHtmlAttribute(\"cft_min_\" + nomeCampo)%>" name="<%=TextHelper.forHtmlAttribute(\"cft_min_\" + nomeCampo)%>" type="text" classe="form-control" mask="#F20"
		                                    onBlur=" if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 4);}"
		                                    size="10" value="<%=TextHelper.forHtmlAttribute(cftVlrMinimo)%>" others="<%=TextHelper.forHtmlAttribute(\"old='\" + cftVlrMinimo + \"'\" + (readOnly ? \" disabled\" : \"\"))%>" />
		                    </div>
	
					        <!-- CET Máximo -->
					        <div class="col-sm-12">
		                      <label class="ml-2" for="<%=TextHelper.forHtmlAttribute("cft_" + nomeCampo)%>"><hl:message key="rotulo.taxa.juros.prazo.cet.maximo"/></label>
		                      <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(nomeCampo)%>" type="hidden" value="<%=TextHelper.forHtmlAttribute(przCsa)%>" />
		                      <hl:htmlinput di="<%=TextHelper.forHtmlAttribute(\"cft_\" + nomeCampo)%>" name="<%=TextHelper.forHtmlAttribute(\"cft_\" + nomeCampo)%>" type="text" classe="form-control" mask="#F20"
		                                    onBlur=" if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 4);}"
		                                    size="10" value="<%=TextHelper.forHtmlAttribute(cftVlr)%>" others="<%=TextHelper.forHtmlAttribute(\"old='\" + cftVlr + \"'\" + (readOnly ? \" disabled\" : \"\"))%>" />
		                    </div>
					    <% } else { %>
					        <!-- CET Único -->
					        <div class="col-sm-12">
		                      <label class="ml-2" for="<%=TextHelper.forHtmlAttribute("cft_" + nomeCampo)%>"><hl:message key="rotulo.taxa.juros.prazo.cet"/></label>
		                      <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(nomeCampo)%>" type="hidden" value="<%=TextHelper.forHtmlAttribute(przCsa)%>" />
		                      <hl:htmlinput di="<%=TextHelper.forHtmlAttribute(\"cft_\" + nomeCampo)%>" name="<%=TextHelper.forHtmlAttribute(\"cft_\" + nomeCampo)%>" type="text" classe="form-control" mask="#F20"
		                                    onBlur=" if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 4);}"
		                                    size="10" value="<%=TextHelper.forHtmlAttribute(cftVlr)%>" others="<%=TextHelper.forHtmlAttribute(\"old='\" + cftVlr + \"'\" + (readOnly ? \" disabled\" : \"\"))%>" />
		                    </div>
					    <% } %>
	
					    <!-- Taxa de Juros -->
					    <div class="col-sm-12">
	                      <label class="ml-2" for="<%=TextHelper.forHtmlAttribute("taxa_" + nomeCampo)%>"><hl:message key="rotulo.taxa.juros.prazo.taxa.juros"/></label>
	                      <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(nomeCampo)%>" type="hidden" value="<%=TextHelper.forHtmlAttribute(przCsa)%>" />
	                      <hl:htmlinput di="<%=TextHelper.forHtmlAttribute(\"taxa_\" + nomeCampo)%>" name="<%=TextHelper.forHtmlAttribute(\"taxa_\" + nomeCampo)%>" type="text" classe="form-control" mask="#F20"
	                                    onBlur=" if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 4);}"
	                                    size="10" value="<%=TextHelper.forHtmlAttribute(cftVlrRef)%>" others="<%=TextHelper.forHtmlAttribute(\"old='\" + cftVlrRef + \"'\" + (readOnly ? \" disabled\" : \"\"))%>" />
	                    </div>
                    </div>
                  <% } %>
                  </div>
                  <%
                        if (itens == 12 && it.hasNext()) {
                          itens = 0;
                  %> 
                  <%    
                      }
                    }
                    if (jscriptVazio.endsWith(") || ")) {
                      jscriptVazio = jscriptVazio.subSequence(0, jscriptVazio.length() - 5) + ")) "
                                   + "{ if (confirm('" + ApplicationResourcesHelper.getMessage("mensagem.erro.taxa.juros.valor.invalido.zerar.valores", responsavel) + "')) { $( \"div .divCftValor input[name^='cft_']\" ).val( function( index, value ) {  if (value == \"\") { return FormataContabil(parse_num(\"0\"), 4); } else { return value; } } ); } else { return false; } }";
                    }
                    if (arrayPrazos.length() > 0) {
                      arrayPrazos.deleteCharAt(arrayPrazos.length() - 1);
                    }
                  %>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="col-sm-12">
          <div class="card">
            <div class="card-header">
              <h2 class="card-header-title"><hl:message key="rotulo.servico.copiar.taxas.titulo"/></h2>
            </div>
            <div class="card-body">
              <div class="row">
                <div class="form-group col-sm-12 col-md-6 mt-1">
                  <label for="copia_para_svc_corrente"><hl:message key="rotulo.servico.copiar.configuracao"/></label>
                  <div class='form-check'>
                    <%
                    request.setAttribute("servicos", servicos);
                    %>
                    <hl:htmlcombo listName="servicos" 
                                  name="copia_para_svc_corrente"
                                  di="copia_para_svc_corrente" 
                                  classe="form-control"
                                  fieldValue="<%=TextHelper.forHtmlAttribute( Columns.SVC_CODIGO )%>" 
                                  fieldLabel="<%=(String)(Columns.SVC_IDENTIFICADOR + ";" + Columns.SVC_DESCRICAO)%>" 
                                  notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel) %>"
                        />
    
                  </div>
                </div>
                <div class="form-group col-sm-12 col-md-6 mt-1">
                  <label for="copia_svc_corrente"><hl:message key="rotulo.servico.aplicar.configuracao"/></label>
                  <div class='form-check'>
                    <hl:htmlcombo listName="servicos" 
                                  name="copia_svc_corrente"
                                  di="copia_svc_corrente"
                                  classe="form-control" 
                                  fieldValue="<%=TextHelper.forHtmlAttribute( Columns.SVC_CODIGO )%>" 
                                  fieldLabel="<%=(String)(Columns.SVC_IDENTIFICADOR + ";" + Columns.SVC_DESCRICAO)%>" 
                                  size="4"
                    />
                  </div>
                  <div class='slider mt-2 col-sm-12 col-md-12 pl-0 pr-0'>
                    <div class='tooltip-inner'><hl:message key="mensagem.utilize.crtl"/></div>
                    <div class='btn-action float-end mt-3'>
                    <a class='btn btn-outline-danger' href='#' onclick="desmarcarSelecao('copia_svc_corrente')"><hl:message key="mensagem.limpar.selecao"/></a>
                    </div>
                  </div>
                    
                </div>
              </div> 
            </div>
          </div>
        </div>
      <INPUT TYPE="hidden" NAME="ALTERA_CFT" VALUE="0">
      <INPUT TYPE="hidden" NAME="ALTERA_PSC" VALUE="0">
      <INPUT TYPE="hidden" NAME="acao" VALUE="salvar">
      <INPUT TYPE="hidden" NAME="SVC_CODIGO" VALUE="<%=TextHelper.forHtmlAttribute(svcCodigo)%>">
      <INPUT TYPE="hidden" NAME="SVC_DESCRICAO" VALUE="<%=TextHelper.forHtmlAttribute(svcDescricao)%>">
      <INPUT TYPE="hidden" NAME="CSA_CODIGO" VALUE="<%=TextHelper.forHtmlAttribute(csaCodigo)%>">
      <INPUT TYPE="hidden" NAME="titulo" VALUE="<%=TextHelper.forHtmlAttribute(titulo)%>">
      <INPUT TYPE="hidden" NAME="tipo" VALUE="<%=TextHelper.forHtmlAttribute(tipo)%>">
      <INPUT TYPE="hidden" NAME="MM_update" VALUE="form1">
      <INPUT TYPE="hidden" NAME="tps_<%=(String)CodedValues.TPS_TAC_FINANCIADA%>" value="">
      <INPUT TYPE="hidden" NAME="tps_<%=(String)CodedValues.TPS_TIPO_TAC%>" value="">
    </div>
    <div id="actions" class="btn-action">
      <% if (!readOnly) { %>
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
        <a class="btn btn-primary" href="#no-back" onClick="vf_cadastro_coeficiente(); if (checkOrdenacao()) { if (vf_vlr_max()) { if(!temCetMinimoMaiorQueCetMaximo()) { f0.submit(); limparConfigCamposPreenchidosAutomaticamente();}}} return false;"><hl:message key="rotulo.botao.salvar"/></a>
          &nbsp;&nbsp;&nbsp;
      <% } else { %>
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
      <% } %>
    </div>
  </FORM>
  <% if (exibeBotaoRodape) { %>
  <div id="btns">
    <a id="page-up" onclick="up()">
      <svg xmlns="http://www.w3.org/2000/svg" width="147.344" height="147.344" viewBox="0 0 147.344 147.344">
        <path id="União_3" data-name="União 3" d="M-20,60.672a73.672,73.672,0,1,1,73.672,73.672A73.66,73.66,0,0,1-20,60.672ZM9.61,16.61a62.252,62.252,0,0,0,0,88.124,62.252,62.252,0,0,0,88.124,0,62.252,62.252,0,0,0,0-88.124,62.252,62.252,0,0,0-88.124,0ZM49.174,88.087,15.278,54.3a6.85,6.85,0,0,1,9.67-9.706L53.672,73.6,82.362,45.336A6.825,6.825,0,1,1,92,55.005L58.6,88.3a6.827,6.827,0,0,1-9.421-.212Z" transform="translate(20 13)"/>
      </svg>
    </a>
    <a id="page-down" onclick="down()">
      <svg xmlns="http://www.w3.org/2000/svg" width="147.344" height="147.344" viewBox="0 0 147.344 147.344">
        <path id="União_3" data-name="União 3" d="M-20,60.672a73.672,73.672,0,1,1,73.672,73.672A73.66,73.66,0,0,1-20,60.672ZM9.61,16.61a62.252,62.252,0,0,0,0,88.124,62.252,62.252,0,0,0,88.124,0,62.252,62.252,0,0,0,0-88.124,62.252,62.252,0,0,0-88.124,0ZM49.174,88.087,15.278,54.3a6.85,6.85,0,0,1,9.67-9.706L53.672,73.6,82.362,45.336A6.825,6.825,0,1,1,92,55.005L58.6,88.3a6.827,6.827,0,0,1-9.421-.212Z" transform="translate(20 13)"/>
      </svg>
    </a>
    <a id="page-actions" onclick="toActionBtns()">
      <svg xmlns="http://www.w3.org/2000/svg" width="145.344" height="145.344" viewBox="0 0 145.344 145.344">
        <path id="União_1" data-name="União 1" d="M-20,59.672a72.672,72.672,0,1,1,72.671,72.672A72.671,72.671,0,0,1-20,59.672Zm10.164,0A62.508,62.508,0,1,0,52.672-2.836,62.579,62.579,0,0,0-9.836,59.672Zm82.6,40.182H24.545A12.069,12.069,0,0,1,12.49,87.8V31.544A12.069,12.069,0,0,1,24.545,19.49h44.2a4.014,4.014,0,0,1,2.841,1.177L91.678,40.757A4.019,4.019,0,0,1,92.855,43.6V87.8A12.069,12.069,0,0,1,80.8,99.854Zm0-40.182a4.018,4.018,0,0,1,4.019,4.018V91.817H80.8A4.023,4.023,0,0,0,84.818,87.8V45.263L67.081,27.526H36.6V39.58H64.727a4.019,4.019,0,0,1,0,8.037H32.581A4.018,4.018,0,0,1,28.563,43.6V27.526H24.545a4.023,4.023,0,0,0-4.018,4.019V87.8a4.023,4.023,0,0,0,4.018,4.018h4.019V63.689a4.018,4.018,0,0,1,4.018-4.018ZM36.6,91.817H68.745V67.708H36.6Z" transform="translate(20 13)"/>
      </svg>
    </a>
  </div>
  <% }%>
</c:set>

<c:set var="javascript">
<hl:fileUploadV4 mostraCampoDescricao="false" extensoes="<%=new String[]{"txt"}%>" tipoArquivo="carga_cft" scriptOnly="true" />
<script type="text/JavaScript">
function desmarcarSelecao(elementId) {
  var elt = document.getElementById(elementId);
  elt.style.backgroundColor = "white";
  elt.selectedIndex = -1;
  elt.focus();
  return true;
}    
  
function carrega_arquivo() {
    var file = document.getElementById('FILE1').value;

    if (file == '' || file == null) {
      alert('<%=ApplicationResourcesHelper.getMessage("rotulo.lst.arq.generico.encontrado", responsavel)%>');
        return false;    
    }
  
  if (confirm('<%=ApplicationResourcesHelper.getMessage("mensagem.taxa.juros.carga.arquivo.info", responsavel)%>')) {
      f0.submit();
      return true;
  }

  return false;
}

function temCetMinimoMaiorQueCetMaximo() {
    <% if (exibeCETMinMax) { %>
	    const prazos = [<%=(String)arrayPrazos.toString()%>];
	
	    for (let i = 0; i < prazos.length; i++) {
	        const prazo = prazos[i];
	        const cetMinimoEl = document.getElementById("cft_min_" + prazo);
	        const cetMaximoEl = document.getElementById("cft_" + prazo);
	
	        if (!cetMinimoEl || !cetMaximoEl) continue;
	
	        const cetMinimo = parseFloat(cetMinimoEl.value.replace(',', '.'));
	        const cetMaximo = parseFloat(cetMaximoEl.value.replace(',', '.'));
	
	        if (cetMinimo > cetMaximo) {
	            alert('<hl:message key="mensagem.erro.atualizar.cet.minimo.maior.que.cet.maximo"/>');
	            cetMinimoEl.focus();
	            return true;
	        }
	    }
    <% } %>
    return false;
}

function atualizar() {
	const cet = parseFloat(document.getElementById('cet').value.replace(',', '.'));
	const taxaJuros = parseFloat(document.getElementById('taxaJuros').value.replace(',', '.'));
	const prazoInicio = document.getElementById('prazoInicio').value;
	const prazoFinal = document.getElementById('prazoFinal').value;
	let cetMinimo = null;
	
	<% if (exibeCETMinMax) { %>
		cetMinimo = parseFloat(document.getElementById('cetMinimo').value.replace(',', '.'));
		
		if (isNaN(cet) || isNaN(cetMinimo) || isNaN(taxaJuros) || !prazoInicio || !prazoFinal) {
			alert('<hl:message key="mensagem.erro.atualizar.cet.min.max.juros.prazos.obrigatorios"/>');
			return;
		}
		
		if(cetMinimo > cet) {
			alert('<hl:message key="mensagem.erro.atualizar.cet.minimo.maior.que.cet.maximo"/>');
			return;
		}
	<% } else { %>
		if (isNaN(cet) || isNaN(taxaJuros) || !prazoInicio || !prazoFinal) {
			alert('<hl:message key="mensagem.erro.atualizar.cet.juros.prazos.obrigatorios"/>');
			return;
		}
	<% } %>
	
	if (prazoInicio > prazoFinal) {
		alert('<hl:message key="mensagem.erro.atualizar.prazo.inicial.maior.que.prazo.final"/>');
		return;
	}

	const prazos = [<%=(String)arrayPrazos.toString()%>];
	
	prazos.forEach(prazo => {
		if (prazo >= prazoInicio && prazo <= prazoFinal) {
			preencherCampoAutomaticamente("cft_" + prazo, cet);
			preencherCampoAutomaticamente("taxa_" + prazo, taxaJuros);
			preencherCampoAutomaticamente("cft_min_" + prazo, cetMinimo);
		} else {
			limparCampo("cft_" + prazo, cet);
			limparCampo("taxa_" + prazo, taxaJuros);
			limparCampo("cft_min_" + prazo, cetMinimo);
			removerIconeAlertaNasLabels(prazo);
		}
	});
	
	document.getElementById("alertaEdicaoCETJuros").style.display = "block";
}

function preencherCampoAutomaticamente(id, valor) {
	const input = document.getElementById(id);
	if (!input) return;
	
	input.value = FormataContabil(parse_num(valor), 2);
	input.style.backgroundColor = "#cce5ff";
	adicionarIconeAlerta(id);
	monitorarAlteracaoManual(id);
}

function adicionarIconeAlerta(id) {	
	const label = document.querySelector("label[for=" + id + "]");
	if (label && !label.querySelector('.icone-alerta')) {
		const icone = document.createElement('span');
        icone.className = 'icone-alerta';
		icone.title = 'Campo preenchido automaticamente';
        icone.textContent = '⚠️';
        icone.style.color = '#dc3545'; // vermelho
        icone.style.marginLeft = '5px';
        label.appendChild(icone);
	}
}

function monitorarAlteracaoManual(id) {
	const input = document.getElementById(id);
	if (!input) return;
	
	input.addEventListener('input', () => {
		// Remove o fundo azul
		input.style.backgroundColor = "";
		
		// Remove o ícone de alerta
		const label = document.querySelector("label[for=" + id + "]");
		if (label) {
			const icone = label.querySelector('.icone-alerta');
			if (icone) {
				label.removeChild(icone);
			}
		}
	}, { once: true }); // Garante que o listener só será executado uma vez
}

function limparCampo(id) {
	const input = document.getElementById(id);
	if (!input) return;
	
	input.value = FormataContabil(parse_num(0), 2);
	input.style.backgroundColor = "";
}

function removerIconeAlertaNasLabels(prazo) {
	const cetLabel = document.querySelector("label[for=cft_" + prazo + "]");
	const taxaLabel = document.querySelector("label[for=taxa_" + prazo + "]");
	const cetMinLabel = document.querySelector("label[for=cft_min_" + prazo + "]");
	
	if (cetLabel) {
		const icone = cetLabel.querySelector('.icone-alerta');
		if (icone) cetLabel.removeChild(icone);
	}
	
	if (taxaLabel) {
		const icone = taxaLabel.querySelector('.icone-alerta');
		if (icone) taxaLabel.removeChild(icone);
	}
	
	if (cetMinLabel) {
		const icone = cetMinLabel.querySelector('.icone-alerta');
		if (icone) cetMinLabel.removeChild(icone);
	}
}

function limparConfigCamposPreenchidosAutomaticamente() {
	const prazos = [<%=(String)arrayPrazos.toString()%>];
	
	prazos.forEach(prazo => {
		const cetInput = document.getElementById("cft_" + prazo);
		const taxaInput = document.getElementById("taxa_" + prazo);
		const cetMinInput = document.getElementById("cft_min_" + prazo);
		
		if (cetInput) cetInput.style.backgroundColor = "";
		if (taxaInput) taxaInput.style.backgroundColor = "";
		if (cetMinInput) cetMinInput.style.backgroundColor = "";
		
		removerIconeAlertaNasLabels(prazo);
	});
	
	const alerta = document.getElementById("alertaEdicaoCETJuros");
	if (alerta) alerta.style.display = "none";
}

//Script para bloquear ou desbloquear os campos do TAC
var f0 = document.forms[0];

function formLoad() {
  habilitaDesabilita();
}
  
function habilitaDesabilita() {
  var disabled = document.getElementById('iRadioTacReal').checked;
 
  document.getElementById('iColTacReal').style.display = (disabled ? 'initial' : 'none');
  document.getElementById('iColTacPercent').style.display = (disabled ? 'none' : 'initial');
  document.getElementById('iColTacMin').style.display = (disabled ? 'none' : 'initial');
  document.getElementById('iColTacMax').style.display = (disabled ? 'none' : 'initial');
}



    window.onload = formLoad;

var f0 = document.forms[0];

function checkOrdenacao() {
  var arrayPrazos = [<%=(String)arrayPrazos.toString()%>];
   
  for (i = 0; i < arrayPrazos.length; i++) {
    var cmpCorrente = document.getElementById("cft_" + arrayPrazos[i]);
    var vlrCorrente = cmpCorrente.value;

    if (vlrCorrente != '' && parseFloat(vlrCorrente.replace(',', '.')) != 0.0) {

      if (i > 0) {
         for (j = i; j > 0; j--) {
            var cmpAnterior = document.getElementById("cft_" + arrayPrazos[j]);
            var vlrAnterior = cmpAnterior.value;
             <% if (ordTaxas.equals(CodedValues.ORDEM_TAXAS_ASC)) { %>
              if (vlrAnterior != '' && parseFloat(vlrAnterior.replace(',', '.')) != 0.0 && parseFloat(vlrAnterior.replace(',', '.')) > parseFloat(vlrCorrente.replace(',', '.'))) {
                alert('<hl:message key="mensagem.erro.taxa.juros.valor.ordem.crescente"/>');         
                cmpAnterior.focus();
                return false;
              }
            <% } else if (ordTaxas.equals(CodedValues.ORDEM_TAXAS_DESC)) { %>
              if (vlrAnterior != '' && parseFloat(vlrAnterior.replace(',', '.')) != 0.0 && parseFloat(vlrAnterior.replace(',', '.')) < parseFloat(vlrCorrente.replace(',', '.'))) {
                alert('<hl:message key="mensagem.erro.taxa.juros.valor.ordem.decrescente"/>');
                cmpAnterior.focus();
                return false;
              }
            <% } %>
          }
      }

      if (i < arrayPrazos.length - 1) {
        for (j = i; j < arrayPrazos.length - 1; j++) {
          var cmpPosterior = document.getElementById("cft_" + arrayPrazos[j]);
          var vlrPosterior = cmpPosterior.value;
              
          <% if (ordTaxas.equals(CodedValues.ORDEM_TAXAS_ASC)) { %>
            if (vlrPosterior != '' && parseFloat(vlrPosterior.replace(',', '.')) != 0.0 && parseFloat(vlrCorrente.replace(',', '.')) > parseFloat(vlrPosterior.replace(',', '.'))) {
              alert('<hl:message key="mensagem.erro.taxa.juros.valor.ordem.crescente"/>');          
              cmpPosterior.focus();
              return false;
            }
          <% } else if (ordTaxas.equals(CodedValues.ORDEM_TAXAS_DESC)) { %>
            if (vlrPosterior != '' && parseFloat(vlrPosterior.replace(',', '.')) != 0.0 && parseFloat(vlrCorrente.replace(',', '.')) < parseFloat(vlrPosterior.replace(',', '.'))) {
              alert('<hl:message key="mensagem.erro.taxa.juros.valor.ordem.decrescente"/>');
              cmpPosterior.focus();
              return false;
            }
         <% } %>
                     
        }
      }
    }
  }
  return true;
}

function vf_cadastro_coeficiente() {
  for (i=0; i < f0.elements.length; i++) {
    var e = f0.elements[i];
    if ((e.type == 'text') && (e.value != e.old)) {
      if (e.name.indexOf('cft_') != -1)
        f0.ALTERA_CFT.value = "1";
      else if (e.name.indexOf('tps_') != -1)
        f0.ALTERA_PSC.value = "1";
    }
  }
}

function vf_vlr_max() {
  <%=(String)jscriptVazio%>
  <% if (temLimiteTaxa) { %>
    <%=(String)jscript%>
  <% } %>

  <% if (!temCET && !ocultarCamposTac) { %>
    var vlrTacMin = isNaN(parseFloat(f0.tps_<%=(String)CodedValues.TPS_VALOR_MIN_TAC%>.value.replace(',', '.'))) ? 0 : parseFloat(f0.tps_<%=(String)CodedValues.TPS_VALOR_MIN_TAC%>.value.replace(',', '.'));
    var vlrTacMax = isNaN(parseFloat(f0.tps_<%=(String)CodedValues.TPS_VALOR_MAX_TAC%>.value.replace(',', '.'))) ? 0 : parseFloat(f0.tps_<%=(String)CodedValues.TPS_VALOR_MAX_TAC%>.value.replace(',', '.'));

    if (f0.tps_TAC_F.value != '' && f0.tps_TAC_P.value != '') {
      alert('<hl:message key="mensagem.erro.taxa.juros.valor.tac"/>');
      return false;
    }

    if (f0.tps_TAC_F.value != '' && (vlrTacMin != '' || vlrTacMax != '')) {
      alert('<hl:message key="mensagem.erro.taxa.juros.valor.tac.min.max"/>');
      return false;
    }

    if ((f0.tps_TAC_P.value == '' || parseFloat(f0.tps_TAC_P.value.replace(',', '.')) <= 0) &&
        (vlrTacMin != '' || vlrTacMax != '')) {
      alert('<hl:message key="mensagem.erro.taxa.juros.valor.tac.percentual"/>');
      return false;
    }

    if (vlrTacMin > vlrTacMax) {
      alert('<hl:message key="mensagem.erro.taxa.juros.valor.tac.min.maior.que"/>');
      return false;
    }

    if (f0.tps_TAC_F.value != '') {
      <% if (temLimiteMaxTacCse) { %>
      if (parseFloat(f0.tps_TAC_F.value.replace(',', '.')) > <%=TextHelper.forJavaScriptBlock(maxTacCseEn)%>) {
    	alert('<hl:message key="mensagem.erro.taxa.juros.valor.tac.maior.que"/>'.replace('{0}','<%=TextHelper.forJavaScriptBlock(maxTacCsePt)%>'));
    	return false;
      }
      <% } %>
      f0.tps_<%=(String)CodedValues.TPS_TAC_FINANCIADA%>.value = f0.tps_TAC_F.value;
      f0.tps_<%=(String)CodedValues.TPS_TIPO_TAC%>.value = 'F';
    } else if (f0.tps_TAC_P.value != '') {
      <% if (temLimiteMaxTacCse) { %>
      if (vlrTacMax > <%=TextHelper.forJavaScriptBlock(maxTacCseEn)%>) {
    	alert('<hl:message key="mensagem.erro.taxa.juros.valor.tac.max.maior.que"/>'.replace('{0}','<%=TextHelper.forJavaScriptBlock(maxTacCsePt)%>'));
        return false;
      }  
      <% } %>
      f0.tps_<%=(String)CodedValues.TPS_TAC_FINANCIADA%>.value = f0.tps_TAC_P.value;
      f0.tps_<%=(String)CodedValues.TPS_TIPO_TAC%>.value = 'P';
    }
  <% } %>
  return true;
}

function vf_data_vigencia() {
  f0.acao.value='ativar';
  var dataIni = new String(f0.CFT_DATA_VIG.value);
  var campos = obtemPartesData(dataIni);
  if (dataIni == '' || dataIni.length != 10 || campos.length != 3) {
    alert('<hl:message key="mensagem.erro.taxa.juros.data.vigencia"/>');
    return false;
  }
  if (verificaData(f0.CFT_DATA_VIG.value)) {
    var now = new Date();
    now.setHours(0);
    now.setMinutes(0);
    now.setSeconds(0);  
    now.setMilliseconds(0);  
    var then = new Date(campos[2], campos[1] - 1, campos[0]);
    if (then.getTime() < now.getTime()) {
      alert('<hl:message key="mensagem.erro.taxa.juros.data.vigencia.invalida"/>');
      return false;
    }
    if (f0.CFT_DATA_VIG.value != f0.CFT_DATA_VIG_OLD.value) {
      if (confirm('<hl:message key="mensagem.confirmacao.alteracao.taxa.juros.vigencia"/>')) {
        return true;
      }
    } else {
      alert('<hl:message key="mensagem.informacao.taxa.juros.data.vigencia.inalterada"/>');
      return false;
    }
  }
  return false;
}
</script>
  <script>
    <% if (exibeBotaoRodape) { %>
    let btnDown = document.querySelector('#btns');
    const pageActions = document.querySelector('#page-actions');
    const pageSize = document.body.scrollHeight;

    function up(){
      window.scrollTo({
        top: 0,
        behavior: "smooth",
      });
    }

    function down(){
      let toDown = document.body.scrollHeight;
      window.scrollBy({
        top: toDown,
        behavior: "smooth",
      });
    }

    function toActionBtns(){
      let save = document.querySelector('#actions').getBoundingClientRect().top;
      window.scrollBy({
        top: save,
        behavior: "smooth",
      });
    }

    function btnTab(){
      let scrollSize = document.documentElement.scrollTop;

      if(scrollSize >= 300){
        btnDown.classList.add('btns-active');
      } else {
        btnDown.classList.remove('btns-active');
      }
    }


    window.addEventListener('scroll', btnTab);
    <% } %>
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
