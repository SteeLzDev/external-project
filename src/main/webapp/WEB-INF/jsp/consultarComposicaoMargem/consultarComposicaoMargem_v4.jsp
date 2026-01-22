<%--
* <p>Title: consultarComposicaoMargem</p>
* <p>Description: Visualização de composição de margem</p>
* <p>Copyright: Copyright (c) 2018</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
  List lstCompMargem = (List) request.getAttribute("lstCompMargem");
  
  String rseCodigo = (String)request.getAttribute("rseCodigo");
  String voltar = (String) request.getAttribute("voltar");
%>
<c:set var="title">
  <hl:message key="rotulo.composicao.margem.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
<div class="row">
  <div class="col-sm-12 col-md-12">
      <div class="card">
         <div class="card-header">
            <h2 class="card-header-title">
              <hl:message key="rotulo.composicao.margem.titulo"/>
            </h2>
          </div>
          <div class="card-body">
        <%
          if (lstCompMargem != null && lstCompMargem.size() > 0) {
            Iterator itCompMargem = lstCompMargem.iterator();
            String codigo, descricao, verba, qtd, vencimento, desconto, tipo, strSvcDescricao;
            String strVrsDescricao, strCmaVinculo;
            String strCrsDescricao;
            String strCmaVinculoOld = "";
            String codigoOld = "";
            CustomTransferObject ctoCompMargem = null;
            int vinculos = 0;


            while (itCompMargem.hasNext()) {

            	 ctoCompMargem   = (CustomTransferObject)itCompMargem.next();
                 codigo 	     = ctoCompMargem.getAttribute("CODIGO") != null ? ctoCompMargem.getAttribute("CODIGO").toString(): "";
                 descricao 	     = ctoCompMargem.getAttribute("DESCRICAO") != null ? ctoCompMargem.getAttribute("DESCRICAO").toString(): "";
                 qtd 		     = ctoCompMargem.getAttribute("QUANTIDADE") != null ? ctoCompMargem.getAttribute("QUANTIDADE").toString(): "";
                 vencimento      = ctoCompMargem.getAttribute("VENCIMENTO") != null ? ctoCompMargem.getAttribute("VENCIMENTO").toString(): "";
                 desconto 	     = ctoCompMargem.getAttribute("DESCONTO") != null ? ctoCompMargem.getAttribute("DESCONTO").toString(): "";
                 verba 		     = ctoCompMargem.getAttribute("VERBA") != null ? ctoCompMargem.getAttribute("VERBA").toString(): "";
                 tipo 		     = ctoCompMargem.getAttribute("TIPO") != null ? ctoCompMargem.getAttribute("TIPO").toString(): "";
                 strSvcDescricao = ctoCompMargem.getAttribute("SVC_DESCRICAO") != null ? ctoCompMargem.getAttribute("SVC_DESCRICAO").toString(): "";

                 strCmaVinculo = (ctoCompMargem.getAttribute("VINCULO") != null ? ctoCompMargem.getAttribute("VINCULO").toString() : "-");
                 strVrsDescricao 	 = (ctoCompMargem.getAttribute(Columns.VRS_DESCRICAO) != null ? ctoCompMargem.getAttribute(Columns.VRS_DESCRICAO).toString() : "-");
                 strCrsDescricao     = (ctoCompMargem.getAttribute(Columns.CRS_DESCRICAO) != null ? ctoCompMargem.getAttribute(Columns.CRS_DESCRICAO).toString() : "-");
                   
                 if (!strCmaVinculoOld.equals(strCmaVinculo)) {
                    if (!strCmaVinculoOld.equals("")) {
        %>
                         </table>
                   <% } %>

                <%if(vinculos > 0){%>
                   <h2 class="legend pt-5">
                 <% } else {%>                
                   <h2 class="legend">
                 <% } %>
                  <span><hl:message key="rotulo.composicao.margem.vinculo"/> <%=TextHelper.forHtmlContent( !strCmaVinculo.equals("-") ? strCmaVinculo : "" + ++vinculos )%></span></h2>          
                   <dl class="row data-list pt-3">
                    <dt class="col-6"><hl:message key="rotulo.composicao.margem.categoria"/>:</dt>
                    <dd class="col-6"><%=TextHelper.forHtmlContent( strVrsDescricao )%></dd>
                    <dt class="col-6"><hl:message key="rotulo.composicao.margem.cargo.funcao"/>:</dt>
                    <dd class="col-6"><%=TextHelper.forHtmlContent( strCrsDescricao )%></dd> 
                  </dl>
                  
                  <div class="card-body table-responsive  pt-3">
                    <table class="table table-striped table-hover">
                      <thead>
                        <tr>
                          <th scope="col"><hl:message key="rotulo.composicao.margem.vencimento.identificador"/></th>
                          <th scope="col"><hl:message key="rotulo.composicao.margem.vencimento.descricao"/></th>
                          <th scope="col"><hl:message key="rotulo.composicao.margem.quantidade"/></th>
                          <th scope="col"><hl:message key="rotulo.composicao.margem.vencimento.valor"/></th>
                          <th scope="col"><hl:message key="rotulo.composicao.margem.desconto.valor"/></th>
                        </tr>
                      </thead>
                      <tbody>
                  <%
                     strCmaVinculoOld = strCmaVinculo;
                   }
                  %>
                    
                  <% if (tipo.equals("1")) { /* VENCIMENTOS */ %>
                      <tr>
                       <td><%=TextHelper.forHtmlContent(codigo)%></td>
                       <td><%=TextHelper.forHtmlContent(descricao)%></td>
                       <td align="center"><%=TextHelper.forHtmlContent(qtd)%></td>
                       <td align="center"><%=(String)(Double.parseDouble(vencimento) != 0.00 ? NumberHelper.reformat(vencimento, "en", NumberHelper.getLang()):"")%></td>
                       <td align="center"><%=(String)(Double.parseDouble(desconto) != 0.00 ? NumberHelper.reformat(String.valueOf(Math.abs(Double.parseDouble(desconto))), "en", NumberHelper.getLang()):"")%></td>
                      </tr>
                   <% } else { /* DESCONTOS */ %>
                     <% if (!codigoOld.equals(codigo)) { %>
                       <tr class="table-secondary">
                        <td ><%=TextHelper.forHtmlContent(codigo)%></td>
                        <td colspan="4"><%=TextHelper.forHtmlContent(descricao)%></td>
                       </tr>
                       <% codigoOld = codigo; %>
                     <% } %>
                     <tr>
                      <td></td>
                      <td><%=TextHelper.forHtmlContent(verba + " - " + strSvcDescricao)%></td>
                      <td><%=TextHelper.forHtmlContent(qtd)%></td>
                      <td><%=(String)(Double.parseDouble(vencimento) != 0.00 ? NumberHelper.reformat(vencimento, "en", NumberHelper.getLang()):"")%></td>
                      <td><%=NumberHelper.reformat(String.valueOf(Math.abs(Double.parseDouble(desconto))), "en", NumberHelper.getLang())%></td>
                     </tr>
                   <% } %>
                <%}%>
             </tbody>
             <tfoot>
              <tr>
                <td colspan="5"><hl:message key="rotulo.composicao.margem.listagem"/></td>
              </tr>
              </tfoot>
             </table>
           <%} else {%>
             <hl:message key="rotulo.composicao.margem.vencimento.erro"/>
           <%}%>
       </div>
     </div>
    </div>
  </div>
</div>
<div class="btn-action">
  <a class="btn btn-outline-danger" name="btnVoltar" id="btnVoltar" href="#no-back" onClick="postData('<%=voltar%>')"><hl:message key="rotulo.botao.voltar"/></a>
</div>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript">
	$('tbody').children('tr').css('background-color','#e8eaec');
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>

