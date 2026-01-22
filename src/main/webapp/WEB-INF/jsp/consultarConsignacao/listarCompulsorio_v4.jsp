<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String serNome = (String) request.getAttribute("serNome");
List ades = (List<TransferObject>) request.getAttribute("ades");
BigDecimal margemDisponivelCompulsorio = (BigDecimal) request.getAttribute("margemDisponivelCompulsorio");
BigDecimal vlrTotalContratos = new BigDecimal("0.00");
%>

<c:set var="title">
  ${tituloPagina}
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
	<div class="card">
		<div class="card-header hasIcon pl-3">
		  <h2 class="card-header-title"><%=TextHelper.forHtmlContent(serNome.toUpperCase())%></h2>
		</div>
		<div class="card-body table-responsive">
			<table class="table table-striped table-hover">
				<thead>
					<tr>
						<th scope="col"><hl:message key="rotulo.consignataria.singular"/></th>
						<th scope="col"><hl:message key="rotulo.compulsorios.numero.ade"/></th>
						<th scope="col"><hl:message key="rotulo.consignacao.identificador"/></th>
						<th scope="col"><hl:message key="rotulo.servico.singular"/></td>
						<th scope="col"><hl:message key="rotulo.compulsorios.inclusao"/></th>
						<th scope="col"><hl:message key="rotulo.compulsorios.numero.prestacao"/></th>
						<th scope="col"><hl:message key="rotulo.compulsorios.pagas"/></th>
						<th scope="col"><hl:message key="rotulo.compulsorios.situacao"/></td>
						<th scope="col"><hl:message key="rotulo.compulsorios.valor.prestacao"/></th>					          
					</tr>
				</thead>
				<tbody>	            	
	            <%=JspHelper.msgRstVazio(ades == null || ades.size() == 0, "14", "lp")%>
	
	            <% 
	              if (ades != null && ades.size() > 0) {
	                 
	                 CustomTransferObject ade = null;
	                 String adeVlr, adeTipoVlr, adeNumero, adeIdentificador, adePrazo, adeData, prdPagas, svcDescricao, sadDescricao, csaNome;
	
	
	                 Iterator it = ades.iterator();
	                 while (it.hasNext()) {
	                     ade = (CustomTransferObject) it.next();
	                     
	                     adeTipoVlr = (String)ade.getAttribute(Columns.ADE_TIPO_VLR);
	                     adeVlr = ade.getAttribute(Columns.ADE_VLR) != null ? ade.getAttribute(Columns.ADE_VLR).toString() : "";
	                     if (!adeVlr.equals("")) {
	                         vlrTotalContratos = vlrTotalContratos.add(new BigDecimal(adeVlr));
	                         adeVlr = NumberHelper.format(Double.valueOf(adeVlr).doubleValue(), NumberHelper.getLang());
	                     }
	                     
	                     adeNumero = ade.getAttribute(Columns.ADE_NUMERO).toString();
	                     adeIdentificador = ade.getAttribute(Columns.ADE_IDENTIFICADOR) != null ? ade.getAttribute(Columns.ADE_IDENTIFICADOR).toString() : "";
	                     adePrazo = ade.getAttribute(Columns.ADE_PRAZO) != null ? ade.getAttribute(Columns.ADE_PRAZO).toString() : ApplicationResourcesHelper.getMessage("rotulo.compulsorios.prazo.indeterminado", responsavel);
	                     adeData = DateHelper.reformat(ade.getAttribute(Columns.ADE_DATA).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern());
	                     prdPagas = ade.getAttribute(Columns.ADE_PRD_PAGAS) != null ? ade.getAttribute(Columns.ADE_PRD_PAGAS).toString() : "0";
	                     sadDescricao = ade.getAttribute(Columns.SAD_DESCRICAO).toString();
	
	                     svcDescricao = (ade.getAttribute(Columns.CNV_COD_VERBA) != null && !ade.getAttribute(Columns.CNV_COD_VERBA).toString().equals("")) ? ade.getAttribute(Columns.CNV_COD_VERBA).toString() : ade.getAttribute(Columns.SVC_IDENTIFICADOR).toString();
	                     svcDescricao += (ade.getAttribute(Columns.ADE_INDICE) != null && !ade.getAttribute(Columns.ADE_INDICE).toString().equals("")) ? ade.getAttribute(Columns.ADE_INDICE).toString() : "";
	                     svcDescricao += " - " + ade.getAttribute(Columns.SVC_DESCRICAO).toString();
	
	                     csaNome = (String) ade.getAttribute(Columns.CSA_NOME_ABREV);
	                     if (csaNome == null || csaNome.trim().length() == 0) {
	                         csaNome = ade.getAttribute(Columns.CSA_NOME).toString();
	                     }
	                     csaNome = ade.getAttribute(Columns.CSA_IDENTIFICADOR) + " - " + csaNome;
                %>
                <tr>
                   <td><%=TextHelper.forHtmlContent(csaNome)%></td>
                   <td><%=TextHelper.forHtmlContent(adeNumero)%></td>
                   <td><%=TextHelper.forHtmlContent(adeIdentificador)%></td>
                   <td><%=TextHelper.forHtmlContent(svcDescricao)%></td>
                   <td><%=TextHelper.forHtmlContent(adeData)%></td>
                   <td><%=TextHelper.forHtmlContent(adePrazo)%></td>
                   <td><%=TextHelper.forHtmlContent(prdPagas)%></td>
                   <td><%=TextHelper.forHtmlContent(sadDescricao)%></td>
                   <td><%=TextHelper.forHtmlContent(ParamSvcTO.getDescricaoTpsTipoVlr(adeTipoVlr))%>&nbsp;<%=TextHelper.forHtmlContent(adeVlr)%></td>                                      
            	</tr>	                     
                <%
	                 }
	              }
	            %>            			
				</tbody>
				<tfoot>
				  <tr>
				  	<td colspan="5"><%=ApplicationResourcesHelper.getMessage("mensagem.listagem.margem.compulsorios", responsavel)%></td>
			  	  </tr>
				</tfoot>
			</table>	
		</div>
	</div>
	
	<div class="card">
		<div class="card-header hasIcon pl-3">
		  <h2 class="card-header-title"><hl:message key="rotulo.compulsorios.dados.valores"/></h2>
		</div>
		<div class="card-body table-responsive">
			<table class="table table-striped table-hover">
				<thead>
					<tr>				
						<th scope="col"><hl:message key="rotulo.compulsorios.passivel.liberacao"/></th>
						<th scope="col"><hl:message key="rotulo.compulsorios.valor.margem"/></td>
						<th scope="col"><hl:message key="rotulo.compulsorios.total"/></th>           
					</tr>
				</thead>
				<tbody>	            		            
	                <tr>
						<td><%=NumberHelper.format(vlrTotalContratos.doubleValue(), NumberHelper.getLang())%></td>
						<td ><%=NumberHelper.format(margemDisponivelCompulsorio.doubleValue() - vlrTotalContratos.doubleValue(), NumberHelper.getLang())%></td>
						<td ><%=NumberHelper.format(margemDisponivelCompulsorio.doubleValue(), NumberHelper.getLang())%></td>                         
	            	</tr>	                                			
				</tbody>
			</table>	
		</div>
	</div>
	
	
	
	
	
	
	
	
	


    <div class="btn-action">
  		<a href="#no-back" class="btn btn-outline-danger" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.cancelar"/></a>
    </div>
</c:set>
<c:set var="javascript">
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>