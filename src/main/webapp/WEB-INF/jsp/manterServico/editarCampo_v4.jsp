<%--
* <p>Title: editarCampo</p>
* <p>Description: Edição de campo Prioridade para Servicos e Convenios</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: andrea.giorgini $
* $Revision: 26846 $
* $Date: 2019-05-30 10:27:34 -0300 (qui, 30 mai 2019) $
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t"     tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl"    uri="/html-lib" %>
<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  
  boolean podeEditar = (Boolean) request.getAttribute("podeEditar");
  
  String svcIdentificador = (String) request.getAttribute("svcIdentificador");
  String svcDescricao = (String) request.getAttribute("svcDescricao");
  String svcPrioridade = (String) request.getAttribute("svcPrioridade");
  String svc_codigo = (String) request.getAttribute("svc_codigo");
  String cnv_codigo = (String) request.getAttribute("cnv_codigo");
  String cnvCodVerba = (String) request.getAttribute("cnvCodVerba");
  String cnvPrioridade = (String) request.getAttribute("cnvPrioridade");
  String csaIdentificador = (String) request.getAttribute("csaIdentificador");
  String csaNome = (String) request.getAttribute("csaNome");
  
  String voltar = "../v3/manterServico?acao=iniciar";
%>
<c:set var="title">
  <hl:message key="rotulo.editar.grid"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
     <div class="col-sm">
        <div class="card">
           <div class="card-header">
              <h2 class="card-header-title"><%=TextHelper.forHtmlContent(svcIdentificador)%> - <%=TextHelper.forHtmlContent(svcDescricao)%></h2>
           </div>
           <div class="card-body">
           <form method="post" action="../v3/manterServico?acao=editarCampo&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
             <input name="SVC_CODIGO" type="hidden" value="<%=TextHelper.forHtmlAttribute(svc_codigo)%>">
             <input name="CNV_CODIGO" type="hidden" value="<%=TextHelper.forHtmlAttribute(cnv_codigo)%>">
             <input name="CNV_COD_VERBA" type="hidden" value="<%=TextHelper.forHtmlAttribute(cnvCodVerba)%>">
             <%if(svc_codigo != null && (cnv_codigo == null || cnv_codigo.equals(""))){ %>
             <div class="row">
                  <div class="form-group col-sm-12 col-md-4">
                    <label for="SVC_IDENTIFICADOR"><hl:message key="rotulo.servico.identificador"/></label>
                    <input class="form-control" name="SVC_IDENTIFICADOR" type="text" size="32"
                      value="<%=TextHelper.forHtmlAttribute(svcIdentificador)%>" disabled='disabled'
                    />
                  </div>
                  <div class="form-group col-sm-12 col-md-4">
                    <label for="SVC_DESCRICAO"><hl:message key="rotulo.servico.descricao"/></label>
                    <input class="form-control" name="SVC_DESCRICAO" type="text" size="32"
                          value="<%=TextHelper.forHtmlAttribute(svcDescricao)%>" disabled='disabled'
                    />
                  </div>
                  <div class="form-group col-sm-12 col-md-4">
                    <label for="SVC_PRIORIDADE"><hl:message key="rotulo.servico.prioridade"/></label>
                    <input class="form-control" name="SVC_PRIORIDADE" type="text" size="10" 
                          value="<%=TextHelper.forHtmlAttribute((svcPrioridade != null ? svcPrioridade: ""))%>"
                          others="<%=TextHelper.forHtmlAttribute( !podeEditar ? "disabled" : "")%>"
                          placeHolder="<hl:message key="rotulo.servico.prioridade.digite"/>"
                          onFocus="SetarEventoMascara(this,'#D3',true);"
                    />
                  </div>
             </div>
             <%} else if(cnv_codigo != null && !cnv_codigo.equals("")){ %>
             <div class="row">
                  <div class="form-group col-sm-12 col-md-4">
                    <label for="CSA_IDENTIFICADOR"><hl:message key="rotulo.consignataria.singular"/></label>
                    <input class="form-control" name="CSA_IDENTIFICADOR" type="text" size="40"
                      value="<%=TextHelper.forHtmlAttribute(csaIdentificador)%> - <%=TextHelper.forHtmlAttribute(csaNome)%>"
                      disabled='disabled'
                    />
                  </div>
                  <div class="form-group col-sm-12 col-md-4">
                    <label for="CNV_COD_VERBA"><hl:message key="rotulo.convenio.codigo.verba"/></label>
                    <input class="form-control" name="CNV_COD_VERBA" type="text" size="40"
                          value="<%=TextHelper.forHtmlAttribute(cnvCodVerba)%>" 
                          disabled='disabled'
                    />
                  </div>
                  <div class="form-group col-sm-12 col-md-4">
                    <label for="CNV_PRIORIDADE"><hl:message key="rotulo.servico.prioridade"/></label>
                    <input class="form-control" name="CNV_PRIORIDADE" type="text" size="10"
                          value="<%=TextHelper.forHtmlAttribute((cnvPrioridade != null? cnvPrioridade: ""))%>"
                          others="<%=TextHelper.forHtmlAttribute( !podeEditar ? "disabled" : "")%>"
                          placeHolder="<hl:message key="rotulo.servico.prioridade.digite"/>"
                          onFocus="SetarEventoMascara(this,'#D3',true);" 
                    />
                  </div>
             </div>        
             <%} %>
           </form>
           </div>
        </div>
     </div>
  </div>
  <div class="btn-action">
  <% if (podeEditar) { %>
   <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScript(voltar)%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
   <a class="btn btn-primary" HREF="#no-back" onClick="f0.submit(); return false;" ><hl:message key="rotulo.botao.salvar"/></a>
   <% } else {%>
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScript(voltar)%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
   <%} %>
  </div>
</c:set>
<c:set var="javascript">
   <script type="text/JavaScript">
     var f0 = document.forms[0];
   </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>