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
<%@ page import="com.zetra.econsig.values.CodedValues" %>
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
Integer prazoRestante = (Integer) request.getAttribute("prazoRestante");
BigDecimal adeVlr = (BigDecimal) request.getAttribute("adeVlr");
int[] anosParcelas = (int[]) request.getAttribute("anosParcelas");
int anoAtual = DateHelper.getYear(DateHelper.getSystemDate());
boolean exigeMotivo = (request.getAttribute("exigeMotivo") != null ? (boolean) request.getAttribute("exigeMotivo") : false);
boolean colocarEmCarenciaLiqUltParcela = (request.getAttribute("colocarEmCarenciaLiqUltParcela") != null ? (boolean) request.getAttribute("colocarEmCarenciaLiqUltParcela") : false);
%>
<c:set var="title">
  <hl:message key="rotulo.liquidar.parcela.titulo"/>
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
      <form action="../v3/liquidarParcela" method="post" name="form1">
      <% out.print(SynchronizerToken.generateHtmlToken(request));%>      
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.parcela.plural"/></h2>
        </div>
        <div class="card-body pb-0 mb-0">
           <div class="alert alert-warning mb-0" role="alert">
             <p><hl:message key="mensagem.liquidar.parcela.selecione.parcelas"/>:</p>
           </div>
        </div>
        <% if (anosParcelas != null) { %>
          <div class="card-body">
            <div class="col">
              <hl:message key="mensagem.liquidar.parcela.selecione.ano.parcela"/>
              <select name="ano-parcela" id="ano-parcela" onchange="selecionaParcelas()">
                <% for (int ano : anosParcelas) { %>
                  <option value="<%=ano%>" <%= ano == anoAtual ? "selected" : "" %>><%=ano%></option>
                <% } %>
                </select>
              </div>
          </div>
        <% } %>
        <div class="card-body table-responsive p-0">
          <table class="table table-striped table-hover TabelaResultado TabelaParcelas">            
            <thead>
              <tr>
                <th scope="col" width="10%">
                  <div class="form-check">                  
                   <input type="checkbox" class="form-check-input ml-0" onClick="checkUnCheckAll();" id="checkAll" name="checkAll" aria-label="<%=ApplicationResourcesHelper.getMessage("mensagem.liquidar.parcela.selecione.todas", responsavel) %>" title="" data-bs-toggle="tooltip" data-original-title="<%=ApplicationResourcesHelper.getMessage("mensagem.liquidar.parcela.selecione.todas", responsavel) %>">
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
                   Integer prdCodigo = prd.getPrdCodigo();
                   String prdNumero = String.valueOf(prd.getPrdNumero());
                   String prdDataDesconto = DateHelper.toPeriodString(cal.getTime());
                   String spdCodigo = (prd != null ? prd.getSpdCodigo() : CodedValues.SPD_EMABERTO);
                   String spdDescricao = (prd != null ? prd.getSpdDescricao() : ApplicationResourcesHelper.getMessage("rotulo.em.aberto", responsavel));
                   String prdVlrPrevisto = NumberHelper.reformat((prd != null && prd.getPrdVlrPrevisto() != null) ? prd.getPrdVlrPrevisto().toString() : adeVlr.toString(), "en", NumberHelper.getLang());
                   String prdVlrRealizado = NumberHelper.reformat((prd != null && prd.getPrdVlrRealizado() != null) ? prd.getPrdVlrRealizado().toString() : "0.00", "en", NumberHelper.getLang());
                   boolean podeLiquidar = (spdCodigo.equals(CodedValues.SPD_EMABERTO) || spdCodigo.equals(CodedValues.SPD_REJEITADAFOLHA) || spdCodigo.equals(CodedValues.SPD_SEM_RETORNO));
                   String chaveParcela = (prdCodigo != null ? prdCodigo.toString() : "0") + ";" + prdDataDesconto;
               %>               
                    <tr CLASS="<%=TextHelper.forHtmlAttribute(i%2==0?"Li":"Lp") + " parcela " + anoParcela%>">
                      <td class="ocultarColuna" aria-label="<%=ApplicationResourcesHelper.getMessage("mensagem.liquidar.parcela.selecione.multiplas.liquidacao", responsavel) %>" title="" data-bs-toggle="tooltip" data-original-title="<%=ApplicationResourcesHelper.getMessage("mensagem.liquidar.parcela.selecione.multiplas.liquidacao", responsavel) %>">
                         <div class="form-check">
                          <input type="checkbox" class="form-check-input ml-0" name="selecionarCheckBox" value="<%=TextHelper.forHtmlAttribute(chaveParcela)%>" 
                          id="chk<%=TextHelper.forHtmlAttribute(chaveParcela)%>" onChange="enableFields('<%=TextHelper.forHtmlAttribute(chaveParcela)%>');">
                         </div>
                      </td>
                     <td class="selecionarColuna selecionarLinha"><%= TextHelper.forHtmlContent(prdNumero)%>
                        <%-- campo hidden necessário para find do jquery setar valor corretamente para a linha --%>
                        <hl:htmlinput name="<%="colPrdCodigo" + chaveParcela%>" type="hidden" di="<%="colPrdCodigo" + chaveParcela%>" value="<%=TextHelper.forHtmlAttribute(chaveParcela)%>" />
                     </td>                     
                     <td class="selecionarColuna selecionarLinha"><%=TextHelper.forHtmlContent(prdDataDesconto)%></td>
                     <td class="selecionarColuna selecionarLinha"><%=TextHelper.forHtmlContent(spdDescricao)%></td>                   
                     <td class="selecionarColuna selecionarLinha"><%=TextHelper.forHtmlContent(prdVlrPrevisto)%><hl:htmlinput type="hidden" name="<%=TextHelper.forHtmlAttribute("vlrPrevisto" + chaveParcela)%>" di="<%=TextHelper.forHtmlAttribute("vlrPrevisto" + chaveParcela)%>" value="<%=TextHelper.forHtmlAttribute(prdVlrPrevisto)%>" /></td>
                     <td><hl:htmlinput type="text" di="<%=TextHelper.forHtmlAttribute("vlrRealizado" + chaveParcela)%>" name="<%=TextHelper.forHtmlAttribute("vlrRealizado" + chaveParcela)%>" value="<%=TextHelper.forHtmlAttribute(prdVlrRealizado)%>" size="7" mask="#F10" onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" others="disabled"/></td>
                     <td class="selecionarColuna selecionarLinha">
                        <a href="#" name="selecionaAcaoSelecionar" onClick="enableFields('<%=TextHelper.forHtmlAttribute(chaveParcela)%>');"><hl:message key="rotulo.acoes.liquidar"/></a>
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
              <% if (!responsavel.isCseSupOrg()) { %>
                <hl:htmlinput name="tipo"         type="hidden" di="tipo"         value="liquidarparcela" /> 
              <%} %>
                     
          </table>             
        </div>
        </div>
        <% if (exigeMotivo) { %>
              <div class="card">
                  <div class="card-header">
                    <h2 class="card-header-title"><hl:message key="rotulo.liquidar.parcela.motivo.da.operacao"/></h2>
                  </div>
                  <div class="card-body">
                  <%-- Utiliza a tag library EfetivaAcaoMotivoOperacaoTag.java para exibir os dados do Tipo de Motivo da Operação --%>
                  <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="<%=ApplicationResourcesHelper.getMessage("mensagem.confirmacao.autorizacao", responsavel)%>" inputSizeCSS="col-sm-12"/>
                  <%-- Fim dos dados do Motivo da Operação --%>
                  </div>
              </div>
          <% } else { %>
              <div class="card">
               <div class="card-header">
                  <h2 class="card-header-title"><hl:message key="rotulo.liquidar.parcela.motivo.da.operacao"/></h2>
               </div>
               <div class="card-body">             
                  <div class="form-group col-sm">
                     <label for="ADE_OBS"><hl:message key="rotulo.liquidar.parcela.motivo"/>&nbsp;<hl:message key="rotulo.campo.opcional"/>:</label>
                     <textarea class="form-control" id="ADE_OBS" name="ADE_OBS" rows="6"></textarea>
                  </div>             
               </div>       
            </div>
          <%} %>      
        <div class="btn-action">
           
           <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL((paramSession.getLastHistory()), request))%>');"><hl:message key="rotulo.botao.voltar"/></a>
           <a class="btn btn-primary" HREF="#no-back" onClick="liquidarParcela(); return false;">
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
<% if (exigeMotivo) { %>
  <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="" scriptOnly="true" />
<% } %>
<script src="../js/colunaCheckboxInput.js?<hl:message key="release.tag"/>"></script>
<script type="text/JavaScript">
//habilita campo de valor realizado quando linha da tabela é clicada e aparece coluna de checkbox pela 1a vez
$(document).ready(function() {
    selecionaParcelas();
});

$(".selecionarLinha").click(function() {
    var prdCodigo = $(this).parent().find('input[type="hidden"]');
    habilitaVlrRealizado(prdCodigo[0].value);    
});

function enableFields(numero) {
    var codigo = document.getElementById('chk' + numero);
    habilitaVlrRealizado(numero, codigo.checked, true);
}

function liquidarParcela() {
    <% if (exigeMotivo) { %>
    if (!confirmaAcaoConsignacao()) {
        return false;
    }
    <% }%>
  
    var qtdPagas = 0;
    for (i=0; i < f0.elements.length; i++) {
        var e = f0.elements[i];
        if (((e.type == 'check') || (e.type == 'checkbox')) && (e.name == 'selecionarCheckBox')) {
            if (e.checked) {
                retorno = true;
                qtdPagas++;
            }
        }
    }

    if (qtdPagas > 0) {
        <% if (colocarEmCarenciaLiqUltParcela && prazoRestante > 0) { %>
        if (qtdPagas >= <%= prazoRestante %>) {
            if (confirm('<hl:message key="mensagem.liquidar.parcela.colocar.carencia.ultima.parcela"/>')) {
                f0.submit();
                return true;
            } else {
                return false;
            }
        }
        <% } %>
        if (confirm('<hl:message key="mensagem.confirmacao.liquidar.parcela"/>')) {
            f0.submit();
            return true;
        }

    } else {
        alert('<hl:message key="mensagem.informe.parcela"/>');
    }
}

function habilitaVlrRealizadoTodos() {
    var obj = document.getElementsByName('selecionarCheckBox');
    for (var i=0; i < obj.length; i++) {
        habilitaVlrRealizado(obj[i].value, true);
    }
}
  
function desabilitaVlrRealizadoTodos() {
    var obj = document.getElementsByName('selecionarCheckBox');
    for (var i=0; i < obj.length; i++) {
        habilitaVlrRealizado(obj[i].value, false);
    }
}
  
function habilitaVlrRealizado(numero, habilita, mantemCheck) {
    var campoPrevisto = document.getElementById('vlrPrevisto' + numero);
    var campoRealizado = document.getElementById('vlrRealizado' + numero);
    var checkArray = document.getElementsByName('selecionarCheckBox');
    var checkCorrente = false;
    for (var i=0; i < checkArray.length; i++) {
        if (checkArray[i].value == numero) {
            checkCorrente = checkArray[i]; 
        }
    }
    if (campoRealizado.disabled && (habilita == undefined || habilita == true)) {
        campoRealizado.value = campoPrevisto.value;
        campoRealizado.disabled = false;
    } else if (!campoRealizado.disabled && (habilita == undefined || habilita == false)) {
        campoRealizado.value = '<%= NumberHelper.reformat("0.00", "en", NumberHelper.getLang()) %>';
        campoRealizado.disabled = true;
    }
    if (mantemCheck == undefined || !mantemCheck) {
        checkCorrente.checked = !campoRealizado.disabled;
    }
}

function checkUnCheckAll() {
    if (f0.checkAll.checked) {
        checkAll(f0, 'selecionarCheckBox');
        habilitaVlrRealizadoTodos();
    } else {
        uncheckAll(f0, 'selecionarCheckBox');
        desabilitaVlrRealizadoTodos();
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