<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
String serNome = (String) request.getAttribute("serNome");
String rseMatricula = (String) request.getAttribute("rseMatricula");
List<TransferObject> historicos = (List<TransferObject>) request.getAttribute("historicos");
AcessoSistema responsavel = (AcessoSistema) request.getAttribute("responsavel");
%>

<c:set var="bodyContent">
    <div class="main-content">
      <div class="row">
        <div class="col-sm-12 col-md-12 mb-2">
          <div class="card">
            <div class="card-header">
              <h2 class="card-header-title"><%=TextHelper.forHtmlContent(rseMatricula + " - " + serNome)%></h2>
            </div>
            <div class="card-body table-responsive ">
              <table class="table table-striped table-hover">
                <thead>
                  <tr>
                    <th scope="col"><hl:message key="rotulo.historico.liq.antecipada.natureza"/></th>
                    <th scope="col"><hl:message key="rotulo.historico.liq.antecipada.situacao"/></th>
                    <th scope="col"><hl:message key="rotulo.historico.liq.antecipada.data.ocorrencia"/></th>
                    <th scope="col"><hl:message key="rotulo.historico.liq.antecipada.prazo"/></th>
                    <th scope="col"><hl:message key="rotulo.historico.liq.antecipada.pagas"/></th>
                  </tr>
                </thead>
                <tbody>
                <%=JspHelper.msgRstVazio(historicos.size()==0, 5, responsavel)%>
                <%
                       Iterator<TransferObject> it = historicos.iterator();
                         
                             while (it.hasNext()) {
                                 TransferObject historico = it.next();
                                                           
                                 String nseDesc = (String) historico.getAttribute(Columns.NSE_DESCRICAO);
                                 String sadCodigo = (String) historico.getAttribute(Columns.SAD_CODIGO);
                                 String situacao = (sadCodigo.equals(CodedValues.SAD_DEFERIDA) || sadCodigo.equals(CodedValues.SAD_EMANDAMENTO)) ? ApplicationResourcesHelper.getMessage("rotulo.compra.historico.reduzido", responsavel) : (sadCodigo.equals(CodedValues.SAD_SUSPENSA) || sadCodigo.equals(CodedValues.SAD_SUSPENSA_CSE)) ? ApplicationResourcesHelper.getMessage("rotulo.compra.historico.suspenso", responsavel) : (String) historico.getAttribute(Columns.SAD_DESCRICAO);
                                 String dataLiquidacao = DateHelper.toDateTimeString((Date) historico.getAttribute(Columns.OCA_DATA));
                                 int prazo = (historico.getAttribute(Columns.ADE_PRAZO_REF) != null) ? ((Integer) historico.getAttribute(Columns.ADE_PRAZO_REF)).intValue() : 0;
                                 int pagasTotais = (historico.getAttribute(Columns.ADE_PRD_PAGAS_TOTAL) != null) ? ((Integer) historico.getAttribute(Columns.ADE_PRD_PAGAS_TOTAL)).intValue() : 0;
                %>
                  <tr>
                    <td><%=TextHelper.forHtmlContent(nseDesc )%></td>
                    <td><%=TextHelper.forHtmlContent(situacao)%></td>
                    <td><%=TextHelper.forHtmlContent(dataLiquidacao )%></td>
                    <td><%=TextHelper.forHtmlContent(prazo )%></td>
                    <td><%=TextHelper.forHtmlContent(pagasTotais )%></td>
                  </tr>
				<%
                     }
                  %> 
                </tbody>
                <tfoot>
                  <tr>
                    <td colspan="5"><hl:message key="rotulo.historico.liq.listagem.consignacao"/>
                    </td>
                  </tr>
                </tfoot>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
</c:set>
<t:empty_v4>
    <jsp:body>${bodyContent}</jsp:body>
</t:empty_v4>