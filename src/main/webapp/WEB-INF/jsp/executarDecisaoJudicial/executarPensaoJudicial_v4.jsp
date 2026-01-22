<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

ServidorTransferObject servidor  = (ServidorTransferObject) request.getAttribute("servidor");
RegistroServidorTO registroServidor = (RegistroServidorTO) request.getAttribute("registroServidor");
List<MargemTO> margens = (List) request.getAttribute("margens");
String tituloResultado = (String) request.getAttribute("tituloResultado");
%>
<c:set var="title">
<hl:message key="rotulo.decisao.judicial.opcao.pensao.judicial" />
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
    <div class="card">
        <div class="card-header hasIcon">
            <span class="card-header-icon">
                <svg width="26"><use xlink:href="#i-servidor" /></svg>
            </span>
            <h2 class="card-header-title"><%= tituloResultado %></h2>
        </div>
        <div class="card-body">
            <form method="post" action="../v3/executarPensaoJudicial" name="form1">
                <fieldset>    
                    <h3 class="legend"><span><hl:message key="rotulo.decisao.judicial.opcao.pensao.judicial.dados.operacao" /></span></h3>
                
                   <% if (margens != null && margens.size() > 0) {
                          for (MargemTO margemTO : margens) {
                              
                              String descricao = (margemTO.getMarDescricao() != null ? margemTO.getMarDescricao() : ApplicationResourcesHelper.getMessage("rotulo.margem.singular", responsavel));
                              String nome_campo = "margem_" +  margemTO.getMarCodigo().toString();
                              String rse_margem = (margemTO.getMrsMargem() != null ? NumberHelper.format(margemTO.getMrsMargem().doubleValue(), NumberHelper.getLang()) : "0,00");
                              String rse_margem_rest = (margemTO.getMrsMargemRest() != null ? NumberHelper.format(margemTO.getMrsMargemRest().doubleValue(), NumberHelper.getLang()) : "0,00");
                              String rse_margem_usada = (margemTO.getMrsMargemUsada() != null ? NumberHelper.format(margemTO.getMrsMargemUsada().doubleValue(), NumberHelper.getLang()) : "0,00");
                   %>
                   <div class="row">
                    <div class="form-group col-sm-4">
                     <label for="<%=TextHelper.forHtmlAttribute(nome_campo)%>"><hl:message key="rotulo.servidor.margem" arg0="<%=descricao%>" fieldKey="<%=TextHelper.forHtmlAttribute(nome_campo)%>"/></label>
                      <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(nome_campo)%>"
                                 di="<%=TextHelper.forHtmlAttribute(nome_campo)%>"
                                 type="text"
                                 classe="form-control"
                                 value="<%=TextHelper.forHtmlAttribute(rse_margem)%>"
                                 onFocus="SetarEventoMascara(this,'#F11',true);"
                                 onBlur="fout(this);ValidaMascara(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                                 mask="#F11"
                                 placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.margem", responsavel, descricao) %>"
                                 others="disabled"                              
                       />
                      <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(nome_campo + "_antiga")%>"
                                 di="<%=TextHelper.forHtmlAttribute(nome_campo + "_antiga")%>"
                                 type="hidden"
                                 value="<%=TextHelper.forHtmlAttribute(rse_margem)%>"
                       />
                    </div>
                    <div class="form-group col-sm-4">
                     <label for="<%=TextHelper.forHtmlAttribute(nome_campo + "_usada")%>"><hl:message key="rotulo.servidor.margem.usada"/></label>
                      <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(nome_campo + "_usada")%>"
                                 di="<%=TextHelper.forHtmlAttribute(nome_campo + "_usada")%>"
                                 type="text"
                                 classe="form-control"
                                 value="<%=TextHelper.forHtmlAttribute(rse_margem_usada)%>"
                                 others="disabled"        
                       />
                    </div>
                    <div class="form-group col-sm-4">
                     <label for="<%=TextHelper.forHtmlAttribute(nome_campo + "_rest")%>"><hl:message key="rotulo.servidor.margem.restante"/></label>
                      <hl:htmlinput name="<%=TextHelper.forHtmlAttribute(nome_campo + "_rest")%>"
                                 di="<%=TextHelper.forHtmlAttribute(nome_campo + "_rest")%>"
                                 type="text"
                                 classe="form-control"
                                 value="<%=TextHelper.forHtmlAttribute(rse_margem_rest)%>"
                                 others="disabled"        
                       />
                    </div>
                   </div>
                  <%
                        } %>
                    <div class="row">  
                      <div class="form-group col-sm-6 mb-2">
                       <label for="compulsorio"><hl:message key="mensagem.informacao.servidor.compulsorio"/></label>
                        <hl:htmlinput name="compulsorio"
                                   di="compulsorio"
                                   type="text"
                                   classe="form-control"
                                   value=""
                                   onFocus="SetarEventoMascaraV4(this,'#F15',true);" 
                                   onBlur="calculaMargem();fout(this);ValidaMascaraV4(this);if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                                   mask="#F15"
                                   placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.placeholder.compulsorio", responsavel) %>"
                         />
                      </div>
                    </div>
                    <div class="row">
                      <div class="col-sm-6">
                        <div class="alert alert-info pl-3 pr-3 pt-2 pb-2 text-center" role="alert">
                          <p class="mb-0"><hl:message key="mensagem.informacao.servidor.importante.compulsorio"/></p>
                        </div>
                      </div>
                    </div>
                      
                    <%}%>

                <%-- Utiliza a tag library EfetivaAcaoMotivoOperacaoTag.java para exibir os dados do Tipo de Motivo da Operação --%>
                <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="<%=ApplicationResourcesHelper.getMessage("mensagem.confirmacao.alteracao.cadastro", responsavel)%>" operacaoRegistroServidor="true" tmoSempreObrigatorio="false" scriptOnly="false"/>
                <%-- Fim dos dados do Motivo da Operação --%>
                 </fieldset>
                <%= SynchronizerToken.generateHtmlToken(request) %>
                <hl:htmlinput type="hidden" name="acao" value="salvar" />
                <hl:htmlinput type="hidden" name="RSE_CODIGO" value="<%=TextHelper.forHtmlAttribute(registroServidor.getRseCodigo())%>" />
                <hl:htmlinput type="hidden" name="SER_CODIGO" value="<%=TextHelper.forHtmlAttribute(servidor.getSerCodigo())%>" />
                
             </form>
        </div>
    </div>
    <div class="btn-action">
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')" alt="<hl:message key="rotulo.botao.cancelar"/>" title="<hl:message key="rotulo.botao.cancelar"/>"><hl:message key="rotulo.botao.cancelar"/></a>
        <a class="btn btn-primary" href="javascript:void(0);" onClick="if (enviar()) {f0.submit();} return false;" alt="<hl:message key="rotulo.botao.salvar"/>" title="<hl:message key="rotulo.botao.salvar"/>"><hl:message key="rotulo.botao.salvar" /></a>
    </div>
</c:set>
<c:set var="javascript">
<hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="<%=ApplicationResourcesHelper.getMessage("mensagem.confirmacao.alteracao.cadastro", responsavel)%>" operacaoRegistroServidor="true" tmoSempreObrigatorio="true" scriptOnly="true"/>
<script type="text/JavaScript">
var f0 = document.forms[0];

function calculaMargem() {
    <%
	if (margens != null && margens.size() > 0) {
        for (MargemTO margemTO : margens) {
            String nome_campo = "margem_" +  margemTO.getMarCodigo().toString();
    %>
    var margemAntigo = parseFloat(document.getElementById('<%=(String) nome_campo + "_antiga"%>').value).toFixed(2);
    var compulsorio = document.getElementById("compulsorio").value;
    var campo = document.getElementById("<%=(String) nome_campo%>");
    var margem = parseFloat(document.getElementById("<%=(String) nome_campo%>").value).toFixed(2);

    if (margemAntigo != margem) {
      margem = margemAntigo;
    }
    if (compulsorio == null || compulsorio == "") {
      campo.setAttribute('value', margem.replace('.', ','));
    } else {
    
      margem = (parseFloat(margem).toFixed(2) - parseFloat(compulsorio).toFixed(2)).toFixed(2);
      campo.setAttribute('value', margem.replace('.', ','));

    }
    <% }
    }%>

    return true
}

function enviar() {
	var compulsorio = document.getElementById("compulsorio").value;
    if (compulsorio == "" || compulsorio == null) {
    	alert('<hl:message key="mensagem.informacao.valor.compulsorio.em.branco"/>');
        return false
    } else if (parseFloat(compulsorio) < 0) {
    	alert('<hl:message key="mensagem.informacao.valor.compulsorio.negativo"/>');
        return false
    } else { 
    <%
    if (margens != null && margens.size() > 0) {
        for (MargemTO margemTO : margens) {
            String nome_campo = "margem_" +  margemTO.getMarCodigo().toString();
    %>
    
    if (!validaCampoMargem('<%=(String) nome_campo%>')) {
        return false;
    }              
    <%
        }
    } 
    %>
    }

    if (!confirmaAcaoConsignacao ()) {
        return false;
    }

    enableAll();
    return true; 
}

function validaCampoMargem(nomeCampo) {
    if (f0[nomeCampo] != null) {
        var margem = parseFloat(parse_num(f0[nomeCampo].value));
        var margemAntiga = parseFloat(parse_num(f0[nomeCampo + '_antiga'].value));
        if (!f0[nomeCampo].value) {
            alert(mensagem('mensagem.erro.valor.margem.incorreto'));
            f0[nomeCampo].focus();
            return false;
        }
        if (margem > margemAntiga) {
            alert(mensagem('mensagem.erro.valor.margem.maior.atual'));
            f0[nomeCampo].focus();
            return false;
        }
    }
    return true;
}
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
