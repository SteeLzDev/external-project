<%--
* <p>Title: inserirServico</p>
* <p>Description: insere servico novo</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: andrea.giorgini $
* $Revision: 26750 $
* $Date: 2019-05-17 15:05:03 -0300 (sex, 17 mai 2019) $
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t"     tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl"    uri="/html-lib" %>
<%@ taglib prefix="c"     uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.dto.web.*"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  String tpcSim = (String) request.getAttribute("sim");
%>
<c:set var="title">
  <hl:message key="rotulo.servico.inclusao"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
     <div class="col-sm">
        <div class="card">
           <div class="card-header">
              <h2 class="card-header-title"><hl:message key="rotulo.servico.novo"/></h2>
           </div>
           <div class="card-body">
           <form method="post" action="../v3/manterServico?acao=incluirServico&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
            <input name="acao" type="hidden" value="salvarServico" />
            <input name="incluir" type="hidden" value="true" />
             <div class="row">
                  <div class="form-group col-sm-12 col-md-4 mt-1">
                    <label for="SVC_IDENTIFICADOR"><hl:message key="rotulo.servico.identificador"/></label>
                    <input class="form-control" name="SVC_IDENTIFICADOR" id="SVC_IDENTIFICADOR" type="text" size="32" 
                      value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "SVC_IDENTIFICADOR"))%>"
                      placeHolder="<hl:message key="rotulo.servico.identificador.digite"/>"
                    />
                  </div>
                  <div class="form-group col-sm-12 col-md-4 mt-1">
                    <label for="SVC_DESCRICAO"><hl:message key="rotulo.servico.descricao"/></label>
                    <input class="form-control" name="SVC_DESCRICAO" id="SVC_DESCRICAO" type="text" size="32" 
                          value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "SVC_DESCRICAO"))%>"
                          placeHolder="<hl:message key="rotulo.servico.descricao.digite"/>"
                    />
                  </div>
                  <div class="form-group col-sm-12 col-md-4 mt-1">
                    <label for="NSE_CODIGO"><hl:message key="rotulo.param.svc.natureza.servico"/></label>
                    <%
                      List<?> naturezas = (List<?>) request.getAttribute("naturezas");
                      request.setAttribute("naturezas", naturezas);
                    %>
                    <hl:htmlcombo listName="naturezas" name="NSE_CODIGO" classe="form-control"  
                        fieldValue="<%=TextHelper.forHtmlAttribute( Columns.NSE_CODIGO )%>" 
                        fieldLabel="<%=TextHelper.forHtmlAttribute( Columns.NSE_DESCRICAO )%>" 
                        notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel) %>"
                    />
                  </div>
             </div>
             <h3 class='legend'><span><hl:message key="rotulo.servico.copiar.parametros.titulo"/></span></h3>
             <div class="row">
                  <div class="form-group col-sm-12 col-md-4 mt-1">
                    <label for="copia_svc"><hl:message key="rotulo.servico.copiar.configuracao"/></label>
                    <%
                      List<?> servicos = (List<?>) request.getAttribute("servicos");
                      request.setAttribute("servicos", servicos);
                    %>
                    <hl:htmlcombo listName="servicos" name="copia_svc" classe="form-control"
                        fieldValue="<%=TextHelper.forHtmlAttribute( Columns.SVC_CODIGO )%>" 
                        fieldLabel="<%=(String)(Columns.SVC_IDENTIFICADOR + ";" + Columns.SVC_DESCRICAO)%>" 
                        notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel) %>"
                    />
                  </div>
             </div>             
             <h3 class='legend'><span><hl:message key="rotulo.servico.copiar.convenios.titulo"/></span></h3>
             <div class="row">
                  <div class="form-group col-sm-4 col-md-4 mt-1">
                    <label for="copia_cnv"><hl:message key="rotulo.servico.copiar.configuracao"/></label>
                    <%
                    servicos = (List<?>) request.getAttribute("servicos");
                    request.setAttribute("servicos", servicos);
                    %>
                    <hl:htmlcombo listName="servicos" name="copia_cnv" classe="form-control"
                        fieldValue="<%=TextHelper.forHtmlAttribute( Columns.SVC_CODIGO )%>" 
                        fieldLabel="<%=(String)(Columns.SVC_IDENTIFICADOR + ";" + Columns.SVC_DESCRICAO)%>" 
                        notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel) %>"
                    />
                  </div>
                  <div class="form-group col-sm-4 col-md-4 mt-1">
                    <span class="text-nowrap align-text-top"><input name="copia_param_svc_csa" id="copia_param_svc_csa" type="checkbox" value="<%=tpcSim%>" class="form-check-input ml-1">
                    <label for="copia_param_svc_csa" class="form-check-label labelSemNegrito ml-1 col-sm-6 col-md-12"><hl:message key="rotulo.servico.copiar.parametros.csa"/></label></span>
                    <span class="text-nowrap align-text-top"><input name="copia_bloqueio_cnv"  id="copia_bloqueio_cnv" type="checkbox" value="<%=tpcSim%>" class="form-check-input ml-1">
                    <label for="copia_bloqueio_cnv" class="form-check-label labelSemNegrito ml-1 col-sm-6 col-md-12"><hl:message key="rotulo.servico.copiar.bloqueio.verbas"/></label></span>
                    <span class="text-nowrap align-text-top"><input name="copia_bloqueio_svc"  id="copia_bloqueio_svc" type="checkbox" value="<%=tpcSim%>" class="form-check-input ml-1">
                    <label for="copia_bloqueio_svc" class="form-check-label labelSemNegrito ml-1 col-sm-6 col-md-12"><hl:message key="rotulo.servico.copiar.bloqueio.servicos"/></label></span>                                 
                  </div>
             </div>
           </form>
           </div>
        </div>
     </div>
  </div>
  <div class="btn-action">
   <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
   <a class="btn btn-primary" HREF="#no-back" onClick="if(vf_cadastro_svc()){f0.submit();} return false;"><hl:message key="rotulo.botao.salvar"/></a>
  </div>
</c:set>
<c:set var="javascript">
   <script type="text/JavaScript" src="../js/scripts_2810.js"></script>
   <script type="text/JavaScript" src="../js/validaform.js"></script>
   <script type="text/JavaScript" src="../js/validacoes.js"></script>
   <script type="text/JavaScript">
   var f0 = document.forms[0];
   	window.onload = formLoad;
    function formLoad(){
      f0.SVC_IDENTIFICADOR.focus();
    }
   </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>