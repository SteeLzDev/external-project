<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String acaoFormulario = (String) request.getAttribute("acaoFormulario");
String tituloPagina = (String) request.getAttribute("tituloPagina");
String msgConfirmacao = (String) request.getAttribute("msgConfirmacao");

boolean temPermissaoAnexarLiquidar = TextHelper.isNull(request.getAttribute("temPermissaoAnexarLiquidar")) ? false : (boolean) request.getAttribute("temPermissaoAnexarLiquidar");
boolean operacaoPermiteSelecionarPeriodo = TextHelper.isNull(request.getAttribute("operacaoPermiteSelecionarPeriodo")) ? false : (boolean) request.getAttribute("operacaoPermiteSelecionarPeriodo");
Set<Date> periodos = (Set<Date>) request.getAttribute("periodos");

List<TransferObject> autdesList = (List<TransferObject>) request.getAttribute("autdesList");
List<TransferObject> lstTipoJustica = (List<TransferObject>) request.getAttribute("lstTipoJustica");

//Exibição e obrigatoriedade campos decisão judicial
Boolean exibirTipoJustica = (Boolean) request.getAttribute("exibirTipoJustica");
Boolean tipoJusticaObrigatorio = (Boolean) request.getAttribute("tipoJusticaObrigatorio");

Boolean exibirComarcaJustica = (Boolean) request.getAttribute("exibirComarcaJustica");
Boolean comarcaJusticaObrigatorio = (Boolean) request.getAttribute("comarcaJusticaObrigatorio");

Boolean exibirNumeroProcesso = (Boolean) request.getAttribute("exibirNumeroProcesso");
Boolean numeroProcessoObrigatorio = (Boolean) request.getAttribute("numeroProcessoObrigatorio");

Boolean exibirDataDecisao = (Boolean) request.getAttribute("exibirDataDecisao");
Boolean dataDecisaoObrigatorio = (Boolean) request.getAttribute("dataDecisaoObrigatorio");

Boolean exibirTextoDecisao = (Boolean) request.getAttribute("exibirTextoDecisao");
Boolean textoDecisaoObrigatorio = (Boolean) request.getAttribute("textoDecisaoObrigatorio");

Boolean exibirAnexo = (Boolean) request.getAttribute("exibirAnexo");
Boolean anexoObrigatorio = (Boolean) request.getAttribute("anexoObrigatorio");
boolean exigeSenhaServidor = TextHelper.isNull(request.getAttribute("exigeSenhaServidor")) ? false : (boolean) request.getAttribute("exigeSenhaServidor");

%>
<c:set var="title">
${tituloPagina}
</c:set>
<c:set var="imageHeader">
<use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
      <form method="post" action="<%=TextHelper.forHtmlAttribute(SynchronizerToken.updateTokenInURL(request.getAttribute("acaoFormulario") + "?acao=liquidar&_skip_history_=true", request))%>" name="formTmo" <%= temPermissaoAnexarLiquidar ? "ENCTYPE='multipart/form-data'" :"" %>>
        <div class="row">
            <%-- Utiliza a tag library DetalheConsignacaoTag.java para exibir os dados da ADE --%>
            <% pageContext.setAttribute("autdes", autdesList); %>       
            <hl:detalharADEv4 name="autdes" table="false" type="alterar"/>
            <%-- Fim dos dados da ADE --%>
        </div>
        <div class="col-sm p-0">
            <div class="card">
                <div class="card-header">
                  <h2 class="card-header-title"><hl:message key="rotulo.efetiva.acao.consignacao.dados.operacao"/></h2>
                </div>
                  <div class="card-body">
                    <% if (operacaoPermiteSelecionarPeriodo && (periodos != null && !periodos.isEmpty())) { %>
                       <dl>
                          <dt class="col-sm-12 px-1">
                              <label for="OCA_PERIODO">
                                <hl:message key="rotulo.folha.periodo"/>
                              </label>
                          </dt>
                           <dd class="col-sm-12 px-1">
                              <select name="OCA_PERIODO" 
                                      id="OCA_PERIODO" 
                                      class="form-control form-select"
                                      onFocus="SetarEventoMascara(this,'#*200',true);" 
                                      onBlur="fout(this);ValidaMascara(this);">
                                <% for (Date periodo : periodos) { %>
                                  <option value="<%=TextHelper.forHtmlAttribute(periodo)%>">
                                    <%=TextHelper.forHtmlContent(DateHelper.toPeriodString(periodo))%>
                                  </option>
                                <% } %>
                              </select>
                           </dd>
                       </dl>
                    <% } %>                   
                    <%-- Utiliza a tag library EfetivaAcaoMotivoOperacaoTag.java para exibir os dados do Tipo de Motivo da Operação --%>
                    <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="<%=TextHelper.forHtmlAttribute(msgConfirmacao)%>" inputSizeCSS="col-sm-12"/>
                    <%-- Fim dos dados do Motivo da Operação --%>
                    <% if (exigeSenhaServidor) { %>
                          <div class="row">
                            <div class="form-group col-sm-6">
                              <hl:senhaServidorv4 senhaObrigatoria="true"                                                 
                                  senhaParaAutorizacaoReserva="true"
                                  nomeCampoSenhaCriptografada="serAutorizacao"
                                  rseCodigo="<%=request.getAttribute("rseCodigo") != null ? request.getAttribute("rseCodigo").toString() : ""%>"
                                  svcCodigo="<%=request.getAttribute("svcCodigo") != null ? request.getAttribute("svcCodigo").toString() : ""%>"
                                  nf="btnEnvia"
                                  classe="form-control"
                                  inputSizeCSS="col-sm-12 px-1"
                                  separador2pontos="false"
                               />
                          </div>
                        </div>
                   <% } %>
                  <% if (temPermissaoAnexarLiquidar && exibirAnexo) {%>
                    <div class="row">
                      <div class="form-group col-sm-6">
                        <label for="arquivo"><hl:message key="rotulo.efetiva.acao.consignacao.dados.arquivo"/><% if (!anexoObrigatorio) { %><hl:message key="rotulo.campo.opcional"/><% } %></label>
                        <input type="file" class="form-control" id="FILE1" name="FILE1">
                      </div>
                    </div>
                    <div class="row">
                      <div class="form-group ml-3" aria-labelledby="visibilidade">
                        <div class="form-check pt-2">
                          <span id="visibilidade">
                            <hl:message key="rotulo.avancada.anexos.visibilidade"/>
                          </span>
                          <fieldset class="col-sm-12 col-md-12">
                              <div class="form-check form-check-inline">
                                  <input class="form-check-input ml-1" id="aadExibeSup" type="checkbox" name="aadExibe" value="<%=CodedValues.PAP_SUPORTE%>" checked>
                                  <label class="form-check-label labelSemNegrito ml-1" for="aadExibeSup">
                                      <hl:message key="rotulo.suporte.singular"/>
                                  </label>
                              </div>
                              <div class="form-check form-check-inline">
                                  <input class="form-check-input ml-1" id="aadExibeCse" type="checkbox" name="aadExibe" value="<%=CodedValues.PAP_CONSIGNANTE%>" checked>
                                  <label class="form-check-label labelSemNegrito ml-1" for="aadExibeCse">
                                      <hl:message key="rotulo.consignante.singular"/>
                                  </label>
                              </div>
                              <div class="form-check form-check-inline">
                                  <input class="form-check-input ml-1" id="aadExibeOrg" type="checkbox" name="aadExibe" value="<%=CodedValues.PAP_ORGAO%>" checked>
                                  <label class="form-check-label labelSemNegrito ml-1" for="aadExibeOrg">
                                      <hl:message key="rotulo.orgao.singular"/>
                                  </label>
                              </div>
                              <div class="form-check form-check-inline">
                                  <input class="form-check-input ml-1" id="aadExibeCsa" type="checkbox" name="aadExibe" value="<%=CodedValues.PAP_CONSIGNATARIA%>" checked>
                                  <label class="form-check-label labelSemNegrito ml-1" for="aadExibeCsa">
                                      <hl:message key="rotulo.consignataria.singular"/>
                                  </label>
                              </div>
                              <div class="form-check form-check-inline">
                                  <input class="form-check-input ml-1" id="aadExibeCor" type="checkbox" name="aadExibe" value="<%=CodedValues.PAP_CORRESPONDENTE%>" checked>
                                  <label class="form-check-label labelSemNegrito ml-1" for="aadExibeCor">
                                      <hl:message key="rotulo.correspondente.singular"/>
                                  </label>
                              </div>
                              <div class="form-check form-check-inline">
                                  <input class="form-check-input ml-1" id="aadExibeSer" type="checkbox" name="aadExibe" value="<%=CodedValues.PAP_SERVIDOR%>" checked>
                                  <label class="form-check-label labelSemNegrito ml-1" for="aadExibeSer">
                                      <hl:message key="rotulo.servidor.singular"/>
                                  </label>
                              </div>
                              <div class="form-check form-check-inline">
                                  <input class="form-check-input ml-1" id="checkTodos"  type="checkbox" onclick="!this.checked ? uncheckAll(f0, 'aadExibe') : checkAll(f0, 'aadExibe')" value="S" checked>
                                  <label class="form-check-label labelSemNegrito ml-1" for="checkTodos">
                                      <hl:message key="rotulo.campo.todos.simples"/>
                                  </label>
                              </div>
                          </fieldset>
                        </div>
                      </div>
                    </div>
                  <% } %>
               </div>
            </div>
            
            <% if ((responsavel.isCseSupOrg() || responsavel.isCsa()) && ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, responsavel)) { %>
            <div class="card">
              <div class="card-header">
                <h2 class="card-header-title">
                  <hl:message key="rotulo.avancada.decisao.judicial.titulo"/>
                </h2>
              </div>
              <div class="card-body">
                <div class="row">
                  <% if (exibirTipoJustica) { %>
                  <div class="form-check form-group col-md-4 mt-2">
                    <label for="tjuCodigo"><hl:message key="rotulo.avancada.decisao.judicial.tipo.justica"/></label>
                    <%=JspHelper.geraCombo(lstTipoJustica, "tjuCodigo", Columns.TJU_CODIGO, Columns.TJU_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), "class=\"form-control\"")%>
                  </div>
                  <% } %>
                  <% if (exibirComarcaJustica) { %>
                  <div class="form-check form-group col-md-2 mt-2">
                    <label for="djuEstado"><hl:message key="rotulo.avancada.decisao.judicial.estado"/></label>
                    <%= JspHelper.geraComboUF("djuEstado", "djuEstado", "", false, "form-control", responsavel) %>
                  </div>
                  <div class="form-check form-group col-md-6 mt-2">
                    <label for="djuComarca"><hl:message key="rotulo.avancada.decisao.judicial.comarca"/></label>
                    <select name="djuComarca" id="djuComarca" class="form-control"></select>
                    <hl:htmlinput name="cidCodigo" di="cidCodigo" type="hidden" />
                  </div>
                  <% } %>
                </div>
                <div class="row">
                  <% if (exibirNumeroProcesso) { %>
                  <div class="form-check form-group col-md-8 mt-2">
                    <label for="djuNumProcesso"><hl:message key="rotulo.avancada.decisao.judicial.numero.processo"/></label>
                    <hl:htmlinput name="djuNumProcesso" di="djuNumProcesso" type="text" classe="form-control" size="40"/>
                  </div>
                  <% } %>
                  <% if (exibirDataDecisao) { %>
                  <div class="form-check form-group col-md-4 mt-2">
                    <label for="djuData"><hl:message key="rotulo.avancada.decisao.judicial.data"/></label>
                    <hl:htmlinput name="djuData" di="djuData" type="text" classe="form-control" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>"/>
                  </div>
                  <% } %>
                </div>
                <% if (exibirTextoDecisao) { %>
                <div class="row">
                  <div class="form-check form-group col-md-12 mt-2">
                    <label for="djuTexto"><hl:message key="rotulo.avancada.decisao.judicial.texto"/></label>
                    <textarea name="djuTexto" id="djuTexto" class="form-control" cols="32" rows="5" onFocus="SetarEventoMascara(this,'#*10000',true);" onBlur="fout(this);ValidaMascara(this);"></textarea>
                  </div>
                </div>
                <% } %>
              </div>
            </div>
            <% } %>

            <div class="btn-action">
              <a class="btn btn-outline-danger" href="#no-back"  onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.voltar"/></a>
              <a class="btn btn-primary" id="btnEnvia" href="#" onClick="if(confirmaAcaoConsignacao() && verificaCamposTju()){enviarRequisicao();} return false;"><hl:message key="rotulo.botao.salvar"/></a>

              <% for (TransferObject ade : autdesList) { %>
                 <input type="hidden" name="ADE_CODIGO" value="<%=TextHelper.forHtmlAttribute(ade.getAttribute(Columns.ADE_CODIGO))%>">
              <%  } %>
           </div>
       </div>
   </form>
</c:set>
<c:set var="javascript">
<hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="<%=TextHelper.forHtmlAttribute(msgConfirmacao)%>" scriptOnly="true" />
  <% if (exigeSenhaServidor) { %>
  <hl:senhaServidorv4 senhaObrigatoria="true"                                                 
                      senhaParaAutorizacaoReserva="true"
                      nomeCampoSenhaCriptografada="serAutorizacao"
                      rseCodigo="<%=request.getAttribute("rseCodigo") != null ? request.getAttribute("rseCodigo").toString() : ""%>"
                      svcCodigo="<%=request.getAttribute("svcCodigo") != null ? request.getAttribute("svcCodigo").toString() : ""%>"
                      scriptOnly="true"
      />
  <% } %>
 <script type="text/JavaScript">
  var f0 = document.forms[0];

  $(document).ready(function() {
      document.getElementById('djuEstado').setAttribute("onchange", "listarCidades(this.value)");
      document.getElementById('djuComarca').setAttribute("onchange", "setCidCodigo(this.value)");      
  });

  function listarCidades(codEstado) {
    if (!codEstado) {
        document.getElementById('djuComarca').innerText = "";
        $("[name='cidCodigo']").val("");            
        return;
    } else {  
      $.ajax({  
        type : 'post',
        url : "../v3/listarCidades?acao=<%=request.getAttribute("acaoListarCidades")%>&codEstado=" + codEstado + "&_skip_history_=true",
        async : true,
        contentType : 'application/json',            
        success : function(data) {

            var options = "<option value>" + "<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>" + "</option> ";
            var cidades = null;
            var nomeCidade = null;
            var codigoCidade = null;                 

            data.forEach(function(objeto) {
              codigoCidade = objeto.atributos['<%=Columns.CID_CODIGO_IBGE%>'];
              nomeCidade = objeto.atributos['<%=Columns.CID_NOME%>'];
              options = options.concat('<option value="').concat(objeto.atributos['<%=Columns.CID_CODIGO%>']).concat('">').concat(nomeCidade).concat('</option>');                    
            });
            
            document.getElementById('djuComarca').innerHTML = options;                
        },
        error: function (response) {
              console.log(response.statusText);
        }
      });
    }
  }

  function setCidCodigo(cidCodigo) {
    $("[name='cidCodigo']").val(cidCodigo);
  }

  function verificaCamposTju() {
    var validaCamposTju = false;
    var Controles = new Array();
    var Msgs = new Array();
    
    // Verifica se algum campo foi informado, sendo assim, tipo de justiça é obrigatório
  	Controles = new Array("tjuCodigo", "djuEstado", "djuComarca", "djuNumProcesso", "djuData", "djuTexto");
    for (var j=0;j<Controles.length;j++) {
        var elementTju =  document.getElementById(Controles[j]);

        if (elementTju != null && (elementTju.value != null && elementTju.value != '')) {
            validaCamposTju = true;
            break;
        }
    }

    // Verifica campos obrigatórios na campo sistema
    Controles = new Array();
    if (<%=tipoJusticaObrigatorio%> || validaCamposTju) {
    	Controles.push("tjuCodigo");
    	Msgs.push('<hl:message key="mensagem.informe.tju.codigo"/>');
    }

    if (<%=comarcaJusticaObrigatorio%>) {
    	Controles.push("djuEstado");
    	Msgs.push('<hl:message key="mensagem.informe.tju.estado"/>');
    }

    if (<%=comarcaJusticaObrigatorio%>) {
    	Controles.push("djuComarca");
    	Msgs.push('<hl:message key="mensagem.informe.tju.comarca"/>');
    }

    if (<%=numeroProcessoObrigatorio%>) {
    	Controles.push("djuNumProcesso");
    	Msgs.push('<hl:message key="mensagem.informe.num.processo"/>');
    }

    if (<%=dataDecisaoObrigatorio%>) {
    	Controles.push("djuData");
    	Msgs.push('<hl:message key="mensagem.informe.tju.data"/>');
    }

    if (<%=textoDecisaoObrigatorio%>) {
    	Controles.push("djuTexto");
    	Msgs.push('<hl:message key="mensagem.informe.tju.texto"/>');
    }
    
    if (<%=anexoObrigatorio%>) {
    	Controles.push("FILE1");
    	Msgs.push('<hl:message key="mensagem.informe.tju.anexo"/>');
    }

    if (!ValidaCampos(Controles, Msgs)) {      
       return false;
    }

    return true;
  }

  function enviarRequisicao() {
	    if (f0.senha != null && trim(f0.senha.value) != '') {
	        CriptografaSenha(f0.senha, f0.serAutorizacao, false);
	    }
	    f0.submit();
  } 
 </script>
</c:set> 
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
