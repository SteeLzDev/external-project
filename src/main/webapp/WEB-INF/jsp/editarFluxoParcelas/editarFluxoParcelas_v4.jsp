<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%

AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String adeCodigo = (String) request.getAttribute("adeCodigo");
CustomTransferObject autdes = (CustomTransferObject) request.getAttribute("autdes");
pageContext.setAttribute("autdes", autdes);
List<ParcelaDescontoTO> parcelas = (List<ParcelaDescontoTO>) request.getAttribute("parcelas");
Integer qtdParcelas = (Integer) request.getAttribute("qtdParcelas");
BigDecimal adeVlr = (BigDecimal) request.getAttribute("adeVlr");
int[] anosParcelas = (int[]) request.getAttribute("anosParcelas");
int anoAtual = DateHelper.getYear(DateHelper.getSystemDate());
%>
<c:set var="title">
  <hl:message key="rotulo.editar.fluxo.parcelas.titulo"/>
</c:set>

<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>

<c:set var="bodyContent">
   <div class="row">
     <div class="col-sm-5">
        <hl:detalharADEv4 name="autdes" table="false" type="alterar"/>
     </div>           
     <div class="col-sm-7">
      <form action="../v3/editarFluxoParcelas" method="post" name="form1">
      <% out.print(SynchronizerToken.generateHtmlToken(request));%>      
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.parcela.plural"/></h2>
        </div>
        <% if (anosParcelas != null) { %>
          <div class="card-body">
            <div class="col">
              <hl:message key="mensagem.editar.fluxo.parcela.selecione.ano.parcela"/>
              <select name="ano-parcela" id="ano-parcela" onchange="selecionaParcelas()">
                <% for (int ano : anosParcelas) { %>
                  <option value="<%=ano%>" <%= ano == anoAtual ? "selected" : "" %>><%=ano%></option>
                <% } %>
                </select>
              </div>
          </div>
        <% } %>
        
        <div class="card-body table-responsive">
          <table class="table table-striped table-hover TabelaResultado TabelaParcelas">            
            <thead>
              <tr>
                 <th scope="col" width="10%"><hl:message key="rotulo.parcela.numero"/></th>
                 <th scope="col" width="10%"><hl:message key="rotulo.parcela.data.desconto"/></th>
                 <th scope="col" width="10%"><hl:message key="rotulo.parcela.valor.previsto"/></th>
              </tr>
              </thead>
              <tbody>
               <%=JspHelper.msgRstVazio(parcelas.size() == 0, "6", "lp")%>
               <%
               Calendar cal = Calendar.getInstance();
               for (int i = 0; i < parcelas.size(); i++) {
                   ParcelaDescontoTO prd = parcelas.get(i);
                   cal.setTime(prd.getPrdDataDesconto());

                   int anoParcela = cal.get(Calendar.YEAR);
                   String prdNumero = String.valueOf(prd.getPrdNumero());
                   String prdDataDesconto = DateHelper.toPeriodString(cal.getTime());
                   String prdVlrPrevisto = NumberHelper.reformat((prd != null && prd.getPrdVlrPrevisto() != null) ? prd.getPrdVlrPrevisto().toString() : adeVlr.toString(), "en", NumberHelper.getLang());
               %>               
                    <tr CLASS="<%=TextHelper.forHtmlAttribute(i%2==0?"Li":"Lp") + " parcela " + anoParcela%>">
                     <td class="selecionarColuna selecionarLinha"><%= TextHelper.forHtmlContent(prdNumero)%> </td>                     
                     <td class="selecionarColuna selecionarLinha"><%=TextHelper.forHtmlContent(prdDataDesconto)%><hl:htmlinput type="hidden" name="<%=TextHelper.forHtmlAttribute("dataDesconto" + prdNumero)%>" di="<%=TextHelper.forHtmlAttribute("dataDesconto" + prdNumero)%>" value="<%=TextHelper.forHtmlAttribute(prdDataDesconto)%>"/></td>
                     <td>                  
                        <hl:htmlinput type="text" name="<%=TextHelper.forHtmlAttribute("vlrPrevisto" + prdNumero)%>" di="<%=TextHelper.forHtmlAttribute("vlrPrevisto" + prdNumero)%>" value="<%=TextHelper.forHtmlAttribute(prdVlrPrevisto)%>" size="7" mask="#F10" onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"/>
                        <hl:htmlinput type="hidden" name="<%=TextHelper.forHtmlAttribute("vlrPrevistoAnterior" + prdNumero)%>" di="<%=TextHelper.forHtmlAttribute("vlrPrevistoAnterior" + prdNumero)%>" value="<%=TextHelper.forHtmlAttribute(prdVlrPrevisto)%>" />                  
                        <hl:htmlinput type="hidden" name="colPrdNumero" di="<%="colPrdNumero" + prdNumero%>" value="<%=TextHelper.forHtmlAttribute(prdNumero)%>"/>
                     </td>
                   </tr>
                <%
                   }
                %>
              </tbody>
              <tfoot>
                  <tr><td colspan="5"><hl:message key="rotulo.consignacao.listagem.parcelas.ade" arg0="${adeNumero}"/></td></tr>
              </tfoot>
              
              <hl:htmlinput name="acao"    type="hidden" di="acao"    value="salvar" />
              <hl:htmlinput name="ADE_CODIGO"   type="hidden" di="ADE_CODIGO"   value="<%=TextHelper.forHtmlAttribute(adeCodigo)%>" />
                     
          </table>             
        </div>
       </div>
        <div class="card">
           <div class="card-header">
              <h2 class="card-header-title"><hl:message key="rotulo.liquidar.parcela.motivo.da.operacao"/></h2>
           </div>
           <div class="card-body">             
              <div class="form-group col-sm">
                 <label for="ocpMotivo"><hl:message key="rotulo.liquidar.parcela.motivo"/>&nbsp;<hl:message key="rotulo.campo.opcional"/>:</label>
                 <textarea class="form-control" id="ocpMotivo" name="ocpMotivo" rows="6"></textarea>
              </div>             
           </div>       
        </div>
        <div class="btn-action">
           
           <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL((paramSession.getLastHistory()), request))%>');"><hl:message key="rotulo.botao.voltar"/></a>
           <a class="btn btn-primary" HREF="#no-back" onClick="editarParcela(); return false;">
              <svg width="17">
                <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-confirmar"></use>
              </svg>
              <hl:message key="rotulo.botao.confirmar"/>
           </a>
        </div>
        </form>
      </div>      
    </div>  
</c:set>
<c:set var="javascript">
<%-- <script src="../js/colunaCheckboxInput.js?<hl:message key="release.tag"/>"></script> --%>
<script type="text/JavaScript">
$(document).ready(function() {
	selecionaParcelas();
});
	
function editarParcela() {
    var retorno = false;

    for (var i=0; i < f0.elements.length; i++) {
        var e = f0.elements[i];
        if (e.type == 'text' && e.name.startsWith('vlrPrevisto')) {
            var num = e.name.substring('vlrPrevisto'.length);
            if (e.value != e.defaultValue) {
            	// Mudou o valor, então manda a alteração
                retorno = true;
            } else {
                // Parcela não alterada, remove da lista de alteradas
                $("#colPrdNumero" + num).val('');
            }
        }
    }

    if (retorno) {
      	if (confirm('<hl:message key="mensagem.confirmacao.edicao.fluxo.parcela"/>')) {
        	f0.submit();
      	}
    } else {
      	alert ('<hl:message key="mensagem.edite.parcela"/>');
    }
    
}

function selecionaParcelas() {
  <% if (anosParcelas != null) { %>
    var rows = $('table.TabelaParcelas tr');
    var year = $('#ano-parcela').val();
    rows.filter('.parcela').hide();
    rows.filter('.' + year).show();
  <% } %>
}


  function formload() {
    selecionaParcelas();
  }
  
  f0 = document.forms[0];

</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>