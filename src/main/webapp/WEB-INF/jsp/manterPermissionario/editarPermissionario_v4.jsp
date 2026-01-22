<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TransferObjectHelper"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />

<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
boolean podeEditar = responsavel.temPermissao(CodedValues.FUN_EDT_PERMISSIONARIO);
CustomTransferObject servidor = (CustomTransferObject) request.getAttribute("servidor");
TransferObject permissionario = (TransferObject) request.getAttribute("permissionario");
String prmDataOcupacao = (String) request.getAttribute("prmDataOcupacao");
String prmDataDesocupacao = (String) request.getAttribute("prmDataDesocupacao");
List<TransferObject> lstOcorrencias = (List<TransferObject>) request.getAttribute("lstOcorrencias");
List<TransferObject> lstEnderecos = (List<TransferObject>) request.getAttribute("lstEnderecos");
String rseCodigo = (request.getAttribute("rseCodigo") != null ? request.getAttribute("rseCodigo").toString() : "");
String prmCodigo = (request.getAttribute("prmCodigo") != null ? request.getAttribute("prmCodigo").toString() : "");
String rse_matricula = servidor != null && !TextHelper.isNull(servidor.getAttribute(Columns.RSE_MATRICULA)) ? servidor.getAttribute(Columns.RSE_MATRICULA).toString() : "";
String ser_nome = (servidor != null && !TextHelper.isNull(servidor.getAttribute(Columns.SER_NOME))) ? servidor.getAttribute(Columns.SER_NOME).toString() : "";
String ech_codigo = permissionario != null && !TextHelper.isNull(permissionario.getAttribute(Columns.ECH_CODIGO)) ? permissionario.getAttribute(Columns.ECH_CODIGO).toString() : JspHelper.verificaVarQryStr(request, "ECH_CODIGO");
%>
<c:set var="title">
  <% if (permissionario == null) { %>
   <hl:message key="rotulo.incluir.permissionario.titulo"/>
  <% } else { %>
   <hl:message key="rotulo.editar.permissionario.titulo"/>
  <% } %>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
 <form method="post" action="../v3/manterPermissionario?acao=salvar&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1" >
  <hl:htmlinput name="PRM_CODIGO" type="hidden" value="<%=TextHelper.forHtmlAttribute(prmCodigo)%>"/>
  <hl:htmlinput name="RSE_CODIGO" type="hidden" value="<%=TextHelper.forHtmlAttribute(rseCodigo)%>"/>
  <hl:htmlinput name="tipo" type="hidden" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "acao"))%>"/>
    <div class="row">
        <div class="col-sm-5">
          <div class="card">
            <div class="card-header">
              <h2 class="card-header-title"><hl:message key="rotulo.validar.servidor.dados.servidor" /></h2>
            </div>
            <div class="card-body">
               <dl class="row data-list">
               <%-- Utiliza a tag library DetalheServidorTag.java para exibir os dados do servidor --%>
               <hl:detalharServidorv4 name="servidor" scope="request"/>
              <%-- Fim dos dados do servidor --%>
              </dl>   
            </div>
          </div>
        </div>
        
        <div class="col-sm-7">
          <div class="card">
            <div class="card-header">
                <% if (permissionario == null) { %>
                  <h2 class="card-header-title"><hl:message key="rotulo.incluir.permissionario.titulo"/></h2>
                <% } else { %>
                  <h2 class="card-header-title"><%=TextHelper.forHtmlContent(rse_matricula)%> - <%=TextHelper.forHtmlContent(ser_nome)%></h2>
                <% } %>
            </div>
            <div class="card-body">
         <div class="row">
          <div class="form-group col-sm-6 col-md-6">
            <label for="end_ajuda"><hl:message key="rotulo.endereco.singular"/></label>
            <hl:htmlinput name="end_ajuda"
                          type="text"
                          classe="form-control"
                          value=""
                          size="7"
                          onFocus="SetarEventoMascara(this,'#*200',true);"
                          onBlur="fout(this);ValidaMascara(this);SelecionaComboExt(ECH_CODIGO, this.value);"
                          others="<%=permissionario == null ? "" : "disabled" %>"/>
          </div>
           <div class="form-group col-sm-3 col-md-3">
            <label><hl:message key="rotulo.endereco.singular"/></label>
            <%=JspHelper.geraCombo(lstEnderecos, "ECH_CODIGO", Columns.ECH_CODIGO, Columns.ECH_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), "class=\"form-control\"", true, 1, ech_codigo, (permissionario != null))%>
           </div>
         </div>
          <div class="row">
           <div class="form-group col-sm-6 col-md-6">
             <label for="PRM_COMPL_ENDERECO"><hl:message key="rotulo.permissionario.complemento.endereco"/></label>
                <hl:htmlinput name="PRM_COMPL_ENDERECO"
                              type="text"
                              classe="form-control"
                              value="<%=TextHelper.forHtmlAttribute(permissionario != null && !TextHelper.isNull(permissionario.getAttribute(Columns.PRM_COMPL_ENDERECO)) ? permissionario.getAttribute(Columns.PRM_COMPL_ENDERECO).toString() : "")%>"
                              size="32"
                              onBlur="fout(this);ValidaMascara(this);SelecionaComboExt(ECH_CODIGO, this.value);"
                              others="<%=permissionario == null || !podeEditar ? "" : "disabled" %>"/>
           </div>
          </div>
          <div class="row">
           <div class="form-group col-sm-6 col-md-6">
             <label for="PRM_DATA_OCUPACAO"><hl:message key="rotulo.permissionario.data.ocupacao"/></label>
                <hl:htmlinput name="PRM_DATA_OCUPACAO"
                                             type="text"
                                             classe="form-control"
                                             value="<%=TextHelper.forHtmlAttribute(prmDataOcupacao)%>"
                                             size="10"
                                             mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                                             others="<%=TextHelper.forHtmlAttribute(!podeEditar ? "disabled" : "")%>"/>
           </div>
           <div class="form-group col-sm-6 col-md-6">
             <label for="PRM_DATA_DESOCUPACAO"><hl:message key="rotulo.permissionario.data.desocupacao"/></label>
                <hl:htmlinput name="PRM_DATA_DESOCUPACAO"
                                             type="text"
                                             classe="form-control"
                                             value="<%=TextHelper.forHtmlAttribute(prmDataDesocupacao)%>"
                                             size="10"
                                             mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                                             others="<%=TextHelper.forHtmlAttribute(!podeEditar ? "disabled" : "")%>"/>
           </div>
          </div>
          <div class="row">
           <div class="form-group col-sm-6 col-md-6">
             <label for="PRM_TELEFONE"><hl:message key="rotulo.permissionario.telefone"/></label>
                <hl:htmlinput name="PRM_TELEFONE"
                                         onFocus="SetarEventoMascara(this,'#*20',true);" 
                                         onBlur="fout(this);ValidaMascara(this);" 
                                         type="text" 
                                         classe="form-control"
                                         value="<%=TextHelper.forHtmlAttribute(permissionario != null && !TextHelper.isNull(permissionario.getAttribute(Columns.PRM_TELEFONE)) ? permissionario.getAttribute(Columns.PRM_TELEFONE).toString() : "")%>" 
                                         size="32"                
                                         others="<%=TextHelper.forHtmlAttribute(!podeEditar ? "disabled" : "")%>"/>
           </div>
           <div class="form-group col-sm-6 col-md-6">
             <label for="PRM_EMAIL"><hl:message key="rotulo.permissionario.email"/></label>
                <hl:htmlinput name="PRM_EMAIL"
                                         onFocus="SetarEventoMascara(this,'#*100',true);" 
                                         onBlur="fout(this);ValidaMascara(this);" 
                                         type="text" 
                                         classe="form-control"
                                         value="<%=TextHelper.forHtmlAttribute(permissionario != null && !TextHelper.isNull(permissionario.getAttribute(Columns.PRM_EMAIL)) ? permissionario.getAttribute(Columns.PRM_EMAIL).toString() : !TextHelper.isNull(servidor.getAttribute(Columns.SER_EMAIL)) ? servidor.getAttribute(Columns.SER_EMAIL).toString() : "" )%>" 
                                         size="32"                
                                         others="<%=TextHelper.forHtmlAttribute(!podeEditar ? "disabled" : "")%>"/>
           </div>
          </div>
      <div class="row">
            <div class="form-group col-sm-6 col-md-6">
              <span id="partIna"><hl:message key="rotulo.permissionario.em.transferencia"/></span>
              <div class="form-check" role="radiogroup" aria-labelledby="PRM_EM_TRANSFERENCIA">
                  <input class="form-check-input ml-1" type="radio" name="PRM_EM_TRANSFERENCIA" value="S" id="PRM_EM_TRANSFERENCIA" <%= permissionario != null && permissionario.getAttribute(Columns.PRM_EM_TRANSFERENCIA) != null && permissionario.getAttribute(Columns.PRM_EM_TRANSFERENCIA).toString().equalsIgnoreCase(CodedValues.TPC_SIM) ? "checked" : "" %>>
                  <label class="form-check-label pr-3" for="inadSim">
                    <span class="text-nowrap align-text-top"><hl:message key="rotulo.sim"/></span>
                  </label>
                  <input class="form-check-input ml-1" type="radio" value="N" name="PRM_EM_TRANSFERENCIA" id="PRM_EM_TRANSFERENCIA" <%= permissionario != null && permissionario.getAttribute(Columns.PRM_EM_TRANSFERENCIA) != null && permissionario.getAttribute(Columns.PRM_EM_TRANSFERENCIA).toString().equalsIgnoreCase(CodedValues.TPC_NAO) ? "checked" : "" %>>
                  <label class="form-check-label" for="inadNao">
                    <span class="text-nowrap align-text-top"><hl:message key="rotulo.nao"/></span>
                  </label>
              </div>
            </div>
      </div>    
    </div>
  </div>
 </div>
</div>
    <% if (lstOcorrencias != null && !lstOcorrencias.isEmpty()) { %> 
		<div class="card">
			<div class="card-header hasIcon pl-3">
			  <h2 class="card-header-title"><hl:message key="rotulo.permissionario.historico"/></h2>
			</div>
			<div class="card-body table-responsive p-0">
				<table class="table table-striped table-hover">
					<thead>
						<tr>
							<th scope="col"><hl:message key="rotulo.permissionario.data"/></th>
							<th scope="col"><hl:message key="rotulo.permissionario.responsavel"/></th>
							<th scope="col"><hl:message key="rotulo.permissionario.tipo"/></th>  
							<th scope="col"><hl:message key="rotulo.permissionario.descricao"/></th>
							<th scope="col"><hl:message key="rotulo.permissionario.ip.acesso"/></th>
						</tr>
					</thead>
					<tbody>
                	    <%
                	        int i = 0;
                  	    	Iterator<TransferObject> itHistorico = lstOcorrencias.iterator();
                  	        while (itHistorico.hasNext()) { 
                  	          TransferObject cto = itHistorico.next();
                              cto = TransferObjectHelper.mascararUsuarioHistorico((CustomTransferObject) cto, null, responsavel);
                
                  	          String orsData = DateHelper.toDateTimeString((Date) cto.getAttribute(Columns.ORS_DATA));
                  	
                  	          String opeResponsavel = cto.getAttribute(Columns.USU_LOGIN) != null ? cto.getAttribute(Columns.USU_LOGIN).toString() : "";
                  	          String opeData = cto.getAttribute("data_ocorrencia") != null ? DateHelper.toDateTimeString((Date) cto.getAttribute("data_ocorrencia")) : "";
                  	          String opeTipo = cto.getAttribute(Columns.TOC_DESCRICAO).toString();
                  	          String opeObs = cto.getAttribute("observacao").toString();
                  	          String opeIpAcesso = cto.getAttribute("ip_acesso") != null ?  cto.getAttribute("ip_acesso").toString() : "";
                	    %>
                	      <tr class="<%=TextHelper.forHtmlAttribute(i++%2==0?"Li":"Lp")%>">
                	        <td><%=TextHelper.forHtmlContent(opeData)%></td>
                	        <td><%=TextHelper.forHtmlContent(opeResponsavel)%></td>
                	        <td><%=TextHelper.forHtmlContent(opeTipo)%></td>
                	        <td><%=JspHelper.formataMsgOca(opeObs)%></td>
                	        <td><%=TextHelper.forHtmlContent(opeIpAcesso)%></td>
                	      </tr>
                	    <%  } %>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="10">
								<span class="font-italic"><%=request.getAttribute("_paginacaoSubTitulo")%></span>
							</td>
						</tr>
					</tfoot>
				</table>
			</div>
			<div class="card-footer">
               <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
			</div>
		  </div>
  <% } %>

  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
    <% if (podeEditar) {%>
      <a class="btn btn-primary" href="#no-back" onClick="vf_cadastro_permissionario(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
    <% } %>
  </div> 
 </form>      
    <script type="text/JavaScript">
    f0 = document.forms[0];
    
    function vf_cadastro_permissionario() {
    	  var Controles = new Array("ECH_CODIGO", "PRM_COMPL_ENDERECO", "PRM_DATA_OCUPACAO");
    	  var Msgs = new Array('<hl:message key="mensagem.informe.prm.endereco"/>',
    	                	   '<hl:message key="mensagem.informe.prm.complemento"/>',
    	                 	   '<hl:message key="mensagem.informe.prm.data.ocupacao"/>');

    	  if (ValidaCampos(Controles, Msgs)) {
    		enableAll();
    	    f0.submit();
    	  }
    	}

  </script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>