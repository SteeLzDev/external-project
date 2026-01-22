<%--
* <p>Title: consultarRelacaoBeneficios</p>
* <p>Description: Consultar relação benefícios v4</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: igor.lucas $
* $Revision: 26246 $
* $Date: 2019-02-14 09:27:49 -0200 (qui, 14 fev 2019) $
--%>

<%@ page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper"%>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.util.Date"%>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper"%>
<%@ page import="com.zetra.econsig.values.CodedValues"%>
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
<%@ page import="java.util.Iterator"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ page import="com.zetra.econsig.values.Columns"%>
<%@ page import="java.util.List"%>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken"%>

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
	AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

	List<TransferObject> ocorrencias = (List<TransferObject>) request.getAttribute("ocorrencias");
    String cbe_codigo = (String) request.getAttribute(Columns.CBE_CODIGO);
    String rse_codigo = (String) request.getAttribute(Columns.RSE_CODIGO);
    String bfc_codigo = (String) request.getAttribute(Columns.BFC_CODIGO);
    String ser_codigo = (String) request.getAttribute(Columns.SER_CODIGO);
    String tib_codigo = (String) request.getAttribute(Columns.TIB_CODIGO);
    String ben_codigo = (String) request.getAttribute(Columns.BEN_CODIGO);
    String contratosAtivos = (String) request.getAttribute("contratosAtivos");
    
    String prd_data_desconto = (String) request.getAttribute("prd_data_desconto");
%>


<script type="text/JavaScript">

function sendPeriodo(cbcPeriodo) {
    postData("../v3/listarLancamentosContratosBeneficios?acao=listar&contratosAtivos=<%=contratosAtivos%>&_skip_history_=true&<%=Columns.getColumnName(Columns.BEN_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(ben_codigo)%>&<%=Columns.getColumnName(Columns.SER_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(ser_codigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>&<%=Columns.getColumnName(Columns.CBE_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(cbe_codigo)%>&<%=Columns.getColumnName(Columns.RSE_CODIGO)%>=<%=TextHelper.forJavaScriptAttribute(rse_codigo)%>&<%=Columns.getColumnName(Columns.PRD_DATA_DESCONTO)%>=" + cbcPeriodo);
}

</script>

<c:set var="title">
  <hl:message key="rotulo.relacao.ocorrencia.contratos.beneficios.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-beneficios"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <div class="col-sm">
      <div class="card">
        <div class="card-header">
            <h2 class="card-header-title"><hl:message key="rotulo.relacao.ocorrencia.contratos.beneficios.titulo.tabela.historico"/></h2>
        </div>
        <div class="card-body table-responsive p-0">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th scope="col"><hl:message key="rotulo.relacao.ocorrencia.contratos.beneficios.parcela.data" /></th>
                <th scope="col"><hl:message key="rotulo.relacao.ocorrencia.contratos.beneficios.parcela.responsavel"/></th>
                <th scope="col"><hl:message key="rotulo.relacao.ocorrencia.contratos.beneficios.parcela.tipo" /></th>
                <th scope="col"><hl:message key="rotulo.relacao.ocorrencia.contratos.beneficios.parcela.descricao" /></th>
                <th scope="col"><hl:message key="rotulo.relacao.ocorrencia.contratos.beneficios.parcela.ip.acesso"/></th>
              </tr>
            </thead>
            <% if(ocorrencias.size() > 0){ %>
            <tbody>
             <% for(TransferObject ocorrencia : ocorrencias){ %>
            
                <tr class="selecionarLinha">
                  <td class="ocultarColuna" style="display: none;">
                    <div class="form-check">
                      <input type="checkbox" class="form-check-input ml-0" name="selecionarCheckBox">
                    </div>
                  </td>
                  <td class="selecionarColuna"><%= DateHelper.format((Date) ocorrencia.getAttribute(Columns.OCB_DATA), LocaleHelper.getDateTimePattern())%></td>
                  <td class="selecionarColuna"><%= ocorrencia.getAttribute(Columns.USU_NOME) %></td>
                  <td class="selecionarColuna"><%= ocorrencia.getAttribute(Columns.TOC_DESCRICAO) %></td>
                  <td class="selecionarColuna"><%= ocorrencia.getAttribute(Columns.OCB_OBS) %></td>
                  <td class="selecionarColuna"><%= ocorrencia.getAttribute(Columns.OCB_IP_ACESSO) %></td>
                </tr>
              
              <% } %>
            </tbody>
            <% } %>
            
            <% if(ocorrencias.size() == 0){ %>
            <tbody>
                  <tr class="lp"><td colspan="13"><hl:message key="mensagem.erro.nenhum.registro.encontrado"/></td></tr>
            </tbody>
            <% } %>
            <tfoot>
              <tr>
                <td colspan="5"><hl:message key="rotulo.relacao.ocorrencia.contratos.beneficios.listagem"/></td>
              </tr>
            </tfoot>
          </table>
        </div>
      </div>
    </div>
  </div>
   <div class="float-end">
    <div class="btn-action">
      <a href="#no-back" name="Button" class="btn btn-outline-danger" 
        onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"
      ><hl:message key="rotulo.botao.voltar"/></a>      
    </div>
  </div>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>