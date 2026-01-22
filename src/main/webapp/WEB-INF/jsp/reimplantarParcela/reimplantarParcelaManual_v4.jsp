<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
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
BigDecimal adeVlr = (BigDecimal) request.getAttribute("adeVlr");
List<ParcelaDescontoTO> parcelas = (List<ParcelaDescontoTO>) request.getAttribute("parcelas");
int[] anosParcelas = (int[]) request.getAttribute("anosParcelas");
int anoAtual = DateHelper.getYear(DateHelper.getSystemDate());
%>
<c:set var="title">
  <hl:message key="rotulo.reimplantar.parcela.manual.titulo"/>
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
      <form action="../v3/reimplantarParcelaManual?acao=salvar" method="post" name="form1">
      <% out.print(SynchronizerToken.generateHtmlToken(request));%>      
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.parcela.plural"/></h2>
        </div>
        <div class="card-body pb-0 mb-0">
           <div class="alert alert-warning mb-0" role="alert">
             <p><hl:message key="mensagem.reimplantar.parcela.selecione.parcelas"/>:</p>
           </div>
        </div>
        <% if (anosParcelas != null) { %>
          <div class="card-body">
            <div class="col">
              <hl:message key="mensagem.reimplantar.parcela.selecione.ano.parcela"/>
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
                <th scope="col" width="10%">
                  <div class="form-check">                  
                   <input type="checkbox" class="form-check-input ml-0" id="checkAll" name="checkAll" aria-label="<%=ApplicationResourcesHelper.getMessage("mensagem.reimplantar.parcela.selecione.todas", responsavel) %>" title="" data-bs-toggle="tooltip" data-original-title="<%=ApplicationResourcesHelper.getMessage("mensagem.reimplantar.parcela.selecione.todas", responsavel) %>">
                  </div>                  
                </th>
                 <th scope="col" width="10%"><hl:message key="rotulo.parcela.numero"/></th>
                 <th scope="col" width="10%"><hl:message key="rotulo.parcela.data.desconto"/></th>
                 <th scope="col" width="10%"><hl:message key="rotulo.parcela.situacao"/></th>
                 <th scope="col" width="10%"><hl:message key="rotulo.parcela.valor.previsto"/></th>
                 <th scope="col" width="10%"><hl:message key="rotulo.parcela.valor.realizado"/></th>
                 <th scope="col" width="10%"><hl:message key="rotulo.acoes"/></th>
              </tr>
              </thead>
              <tbody>
               <%=JspHelper.msgRstVazio(parcelas.size() == 0, 6, responsavel)%>
               <%
               Calendar cal = Calendar.getInstance();
               for (int i = 0; i < parcelas.size(); i++) {
                   ParcelaDescontoTO prd = parcelas.get(i);
                   cal.setTime(prd.getPrdDataDesconto());

                   int anoParcela = cal.get(Calendar.YEAR);
                   String prdNumero = String.valueOf(prd.getPrdNumero());
                   String prdDataDesconto = DateHelper.toPeriodString(cal.getTime());
                   String spdCodigo = prd.getSpdCodigo();
                   String spdDescricao = prd.getSpdDescricao();
                   String prdVlrPrevisto = NumberHelper.reformat((prd != null && prd.getPrdVlrPrevisto() != null) ? prd.getPrdVlrPrevisto().toString() : adeVlr.toString(), "en", NumberHelper.getLang());
                   String prdVlrRealizado = NumberHelper.reformat((prd != null && prd.getPrdVlrRealizado() != null) ? prd.getPrdVlrRealizado().toString() : "0.00", "en", NumberHelper.getLang());
               %>               
                    <tr CLASS="<%=TextHelper.forHtmlAttribute(i%2==0?"Li":"Lp") + " parcela " + anoParcela%>">
                      <td class="ocultarColuna" aria-label="<%=ApplicationResourcesHelper.getMessage("mensagem.reimplantar.parcela.selecione.multiplas.reimplante", responsavel) %>" title="" data-bs-toggle="tooltip" data-original-title="<%=ApplicationResourcesHelper.getMessage("mensagem.reimplantar.parcela.selecione.multiplas.reimplante", responsavel) %>">
                         <div class="form-check">
                          <input type="checkbox" class="form-check-input ml-0" name="selecionarCheckBox" value="<%=TextHelper.forHtmlAttribute(prdNumero)%>" 
                          id="chk<%=TextHelper.forHtmlAttribute(prdNumero)%>">
                         </div>
                      </td>
                     <td class="selecionarColuna selecionarLinha"><%= TextHelper.forHtmlContent(prdNumero)%>
                        <!-- campo hidden necessário para find do jquery setar valor corretamente para a linha -->
                        <hl:htmlinput name="<%="colPrdNumero" + prdNumero%>"   type="hidden" di="<%="colPrdNumero" + prdNumero%>"   value="<%=TextHelper.forHtmlAttribute(prdNumero)%>" />
                     </td>                     
                     <td class="selecionarColuna selecionarLinha"><%=TextHelper.forHtmlContent(prdDataDesconto)%><hl:htmlinput type="hidden" name="<%=TextHelper.forHtmlAttribute("dataDesconto" + prdNumero)%>" di="<%=TextHelper.forHtmlAttribute("dataDesconto" + prdNumero)%>" value="<%=TextHelper.forHtmlAttribute(prdDataDesconto)%>"/></td>
                     <td class="selecionarColuna selecionarLinha"><%=TextHelper.forHtmlContent(spdDescricao)%></td>                   
                     <td class="selecionarColuna selecionarLinha"><%=TextHelper.forHtmlContent(prdVlrPrevisto)%><hl:htmlinput type="hidden" name="<%=TextHelper.forHtmlAttribute("vlrPrevisto" + prdNumero)%>" di="<%=TextHelper.forHtmlAttribute("vlrPrevisto" + prdNumero)%>" value="<%=TextHelper.forHtmlAttribute(prdVlrPrevisto)%>" /></td>
                     <td>                  
                        <hl:htmlinput type="text" di="<%=TextHelper.forHtmlAttribute("vlrRealizado" + prdNumero)%>" name="<%=TextHelper.forHtmlAttribute("vlrRealizado" + prdNumero)%>" value="<%=TextHelper.forHtmlAttribute(prdVlrRealizado)%>" size="7" mask="#F10" onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" others="disabled"/>                  
                     </td>
                     <td class="selecionarColuna selecionarLinha">
                        <a href="#" name="selecionaAcaoSelecionar" onClick="reimplantarPrd(<%=TextHelper.forHtmlAttribute(prdNumero)%>);"><hl:message key="rotulo.reimplantar.parcela.manual.acao.selecao"/></a>
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
              <h2 class="card-header-title"><hl:message key="rotulo.reimplantar.parcela.motivo.da.operacao"/></h2>
           </div>
           <div class="card-body">             
              <div class="form-group col-sm">
                 <label for="ocpMotivo"><hl:message key="rotulo.reimplantar.parcela.motivo"/><hl:message key="rotulo.campo.opcional"/>:</label>
                 <textarea class="form-control" id="ocpMotivo" name="ocpMotivo" rows="6"></textarea>
              </div>             
           </div>       
        </div>
        <div class="btn-action">
           <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL((paramSession.getLastHistory()), request))%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
           <a class="btn btn-primary" HREF="#no-back" onClick="reimplantarParcela(); return false;">
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
<script src="../js/colunaCheckboxInput.js?<hl:message key="release.tag"/>"></script>
<script type="text/JavaScript">
  //habilita campo de valor realizado quando linha da tabela é clicada e aparece coluna de checkbox pela 1a vez
  $(document).ready(function() {
  selecionaParcelas();
  });
  
  $(".selecionarLinha").click(function() {
  	var prdNumero = $(this).parent().find('input[type="hidden"]');		
  });

  function reimplantarPrd(numero) {
    var codigo = document.getElementById('chk'+numero);
    if (codigo.checked){
  	  codigo.checked = false;
    }else{
  	  codigo.checked = true;
    }
  }

  function reimplantarParcela() {
    var retorno = false;
  
    for (i=0; i < f0.elements.length; i++) {
      var e = f0.elements[i];
      if (((e.type == 'check') || (e.type == 'checkbox')) && (e.name == 'selecionarCheckBox')) {
        if (e.checked) {
          retorno = true;
        }
      }
    }
  
    if (retorno) {
      if (confirm('<hl:message key="mensagem.confirmacao.reimplantar.parcela"/>')) {
        f0.submit();
      }
    } else {
      alert ('<hl:message key="mensagem.informe.parcela"/>');
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
  ocultarColuna();
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>