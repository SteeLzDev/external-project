<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.*" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="com.zetra.econsig.helper.financeiro.CDCHelper"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
boolean temX = false;
boolean temAsterisco = false;

AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

//Verifica os contratos selecionados para renegociação
String[] chkAde = (String[]) request.getAttribute("chkAde");

String svcCodigo = (String) request.getAttribute("svcCodigo");
String svcDescricao = (String) request.getAttribute("svcDescricao");
String svcIdentificador = (String) request.getAttribute("svcIdentificador");
String csaIdentificador = (String) request.getAttribute("csaIdentificador");
String csaNome = (String) request.getAttribute("csaNome");
String csaCodigo = (String) request.getAttribute("csaCodigo");
String rseCodigo = (String) request.getAttribute("rseCodigo");
CustomTransferObject servidor = (CustomTransferObject) request.getAttribute("servidor");
String orgCodigo = (String) request.getAttribute("orgCodigo");
Date dataAtual = DateHelper.getSystemDatetime();

// Parâmetros de sistema
boolean simulacaoPorTaxaJuros = (Boolean) request.getAttribute("simulacaoPorTaxaJuros");
boolean temCET = (Boolean) request.getAttribute("temCET");
boolean simulacaoMetodoMexicano = (Boolean) request.getAttribute("simulacaoMetodoMexicano");
boolean simulacaoMetodoBrasileiro = (Boolean) request.getAttribute("simulacaoMetodoBrasileiro");
float floatQtdeColunasSimulacao = (float) request.getAttribute("floatQtdeColunasSimulacao");
float nroColunas = floatQtdeColunasSimulacao;

float colspan = 6;
if (!simulacaoPorTaxaJuros) {
  colspan = (float)(4*nroColunas);
} else {
  colspan = (float)(9*nroColunas);
}

// Parâmetros de serviço
int qtdeConsignatariasSimulacao = (int) request.getAttribute("qtdeConsignatariasSimulacao");

int dia = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

String adePeriodicidade = (String) request.getAttribute("adePeriodicidade");

// Dados para simulação
String ade_vlr = (String) request.getAttribute("adeVlr");
String vlr_liberado = (String) request.getAttribute("vlrLiberado");
boolean vlrOk = true;

// Simulação
List<TransferObject> simulacao = (List<TransferObject>) request.getAttribute("simulacao");
boolean taxaJurosManCSA = (Boolean) request.getAttribute("taxaJurosManCSA");
boolean exibeCETMinMax = (Boolean) request.getAttribute("exibeCETMinMax");
boolean vlrLiberadoOk = (Boolean) request.getAttribute("vlrLiberadoOk");
%>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="title">
   <hl:message key="rotulo.simulacao.renegociacao.para.titulo" arg0="<%=TextHelper.forHtmlAttribute(csaNome.toUpperCase())%>"/>
</c:set>
<c:set var="bodyContent">
<FORM NAME="form1" METHOD="post" ACTION="../v3/simularRenegociacao?acao=simular&<%=SynchronizerToken.generateToken4URL(request)%>">
  <input type="hidden" name="flow" value="endpoint">
    <div class="card">
      <div class="card-header hasIcon">
        <span class="card-header-icon"><svg width="25"><use xlink:href="#i-simular-config"></use></svg></span>
        <h2 class="card-header-title"><hl:message key="rotulo.reservar.margem.resultado.simulacao"/> - <%=DateHelper.toDateTimeString(DateHelper.getSystemDatetime())%></h2>
      </div>
      <div class="card-body table-responsive">
        <div class="alert alert-warning m-0" role="alert">
          <p class="mb-0"><%=ade_vlr.equals("") ? ApplicationResourcesHelper.getMessage("rotulo.simulacao.renegociacao.valor.liberado.moeda", responsavel, NumberHelper.reformat(vlr_liberado, "en", NumberHelper.getLang(), true), csaNome, svcDescricao) : ApplicationResourcesHelper.getMessage("rotulo.simulacao.renegociacao.valor.prestacao.moeda", responsavel,  NumberHelper.reformat(ade_vlr, "en", NumberHelper.getLang(), true), csaNome, svcDescricao)%></p>
          <% if (simulacaoMetodoMexicano) { %>
          <p><%=ApplicationResourcesHelper.getMessage("rotulo.consignacao.periodicidade",responsavel).toUpperCase()%>:&nbsp;<%=ApplicationResourcesHelper.getMessage(adePeriodicidade.equals(CodedValues.PERIODICIDADE_FOLHA_QUINZENAL) ? "rotulo.consignacao.periodicidade.quinzenal" : "rotulo.consignacao.periodicidade.mensal", responsavel).toUpperCase()%></p>       
          <%} %>
          <% if (temAsterisco) { %>
          <p><hl:message key="mensagem.simulacao.consignatarias.trabalham.carencia.minima"/></p>
          <% } %>
        </div>
        <table class="table table-striped table-hover table-ranking">
          <thead>
            <tr>
              <%
                int meio = new Double(Math.ceil(simulacao.size() / nroColunas)).intValue();
                String tituloValor = "";
                if (floatQtdeColunasSimulacao == 1) {
                    tituloValor = ade_vlr.equals("") ? ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.parcela", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.liberado", responsavel);
                } else {
                    tituloValor = ade_vlr.equals("") ? ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.parcela.abreviado", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.liberado.abreviado", responsavel);
                }
                for (int j=0;j<nroColunas;j++) {
              %>
                <th scope="col" colspan="2"><hl:message key="rotulo.simulacao.renegociacao.prazo"/></th>
                <th scope="col" colspan="1"><%=TextHelper.forHtmlContent(tituloValor)%> (<hl:message key="rotulo.moeda"/>)</th>
                <% if (simulacaoPorTaxaJuros) { %>
                  <% if (temCET) { %>
                    <th scope="col"><hl:message key="rotulo.consignacao.cet"/></th>
                    <th scope="col"><hl:message key="rotulo.consignacao.cet.anual"/></th>
                      <%if(taxaJurosManCSA){ %>
                          <th scope="col"><hl:message key="rotulo.consignacao.taxa.juros"/></th>
                          <th scope="col"><hl:message key="rotulo.consignacao.taxa.juros.anual"/></th>
                      <%} %>
                  <% } else { %>
                    <th scope="col"><hl:message key="rotulo.consignacao.taxa.juros"/></th>
                    <% if (simulacaoMetodoMexicano) { %>
                      <th scope="col"><hl:message key="rotulo.consignacao.valor.cat.abreviado"/> (<hl:message key="rotulo.porcentagem"/>)</th>
                      <th scope="col"><hl:message key="rotulo.consignacao.valor.iva.abreviado"/> (<hl:message key="rotulo.moeda"/>)</th>
                    <% } else if (simulacaoMetodoBrasileiro) { %>
                      <th scope="col"><hl:message key="rotulo.consignacao.valor.tac.abreviado"/> (<hl:message key="rotulo.moeda"/>)</th>
                      <th scope="col"><hl:message key="rotulo.consignacao.valor.iof.abreviado"/> (<hl:message key="rotulo.moeda"/>)</th>
                    <% } %>
                  <% } %>
                <% } %>
                <th scope="col"><hl:message key="rotulo.acoes"/></th>
              <% } %>
            </tr>
          </thead>
          <tbody>
            <% if (simulacao.size() == 0) { %>
              <tr class="li">
                <td colspan="<%=(float)(8*nroColunas)%>"><hl:message key="mensagem.prazo.nenhum.encontrado"/></td>
              </tr>
            <%
              } else {
                String csa_codigo, csa_nome, cft_codigo, ranking, str_cft_vlr, cetAnual, str_cft_vlr_ref, jurosAnual, svcCodigoItem, dtj_codigo="";
                String tac = "", iof = "";
                String totalPagar = "", cat = "", iva = ""; // simulacaoMetodoMexicano
                BigDecimal cft_vlr;
                BigDecimal cft_vlr_ref;
                CustomTransferObject coeficiente = null;
                String vlr_ade, vlr_liberado_param, vlr_simulado, prazo;
				        boolean vlrParcelaForaMargem = false;
                for (int i=0; i<meio; i++) {
              %>
              <tr>
              <%
                for (int j = 0; j < nroColunas; j++) {
                  if (i + (j * meio) < simulacao.size()) {
                    coeficiente = (CustomTransferObject)simulacao.get(i + (j * meio));
                    csa_codigo = (String)coeficiente.getAttribute(Columns.CSA_CODIGO);
                    svcCodigoItem = (String)coeficiente.getAttribute(Columns.SVC_CODIGO);
                    csa_nome = (String)coeficiente.getAttribute("TITULO");
                    cft_codigo = (String)coeficiente.getAttribute(Columns.CFT_CODIGO);
                    ranking = (String)coeficiente.getAttribute("RANKING");
					          vlrParcelaForaMargem = ((Boolean) coeficiente.getAttribute("VLR_PARCELA_FORA_MARGEM_" + CodedValues.FUN_SIMULAR_RENEGOCIACAO)).booleanValue();
                    vlrOk = exibeCETMinMax && ade_vlr.equals("") ? !vlrParcelaForaMargem : ((Boolean) coeficiente.getAttribute("OK")).booleanValue();
                    prazo = coeficiente.getAttribute(Columns.PRZ_VLR).toString();					
                    
                    if(!TextHelper.isNull(coeficiente.getAttribute(Columns.DTJ_CODIGO +"_"+CodedValues.FUN_SIMULAR_RENEGOCIACAO))){
                        dtj_codigo = (String) coeficiente.getAttribute(Columns.DTJ_CODIGO +"_"+CodedValues.FUN_SIMULAR_RENEGOCIACAO);
                        cft_vlr = new BigDecimal(coeficiente.getAttribute("CFT_VLR_FUN_"+CodedValues.FUN_SIMULAR_RENEGOCIACAO).toString());
                        vlr_liberado_param = coeficiente.getAttribute("VLR_LIBERADO_"+CodedValues.FUN_SIMULAR_RENEGOCIACAO).toString();
                        vlr_ade = coeficiente.getAttribute("VLR_PARCELA_"+CodedValues.FUN_SIMULAR_RENEGOCIACAO).toString();
                    } else {
                        vlr_liberado_param = coeficiente.getAttribute("VLR_LIBERADO").toString();
                        vlr_ade = coeficiente.getAttribute("VLR_PARCELA").toString();
                        cft_vlr = new BigDecimal(coeficiente.getAttribute(Columns.CFT_VLR).toString());
                    }
                    str_cft_vlr = NumberHelper.format(cft_vlr.doubleValue(), NumberHelper.getLang(), 2, 8);
                    cetAnual = CDCHelper.getStrTaxaEquivalenteAnual(str_cft_vlr);
                    cft_vlr_ref = !TextHelper.isNull(coeficiente.getAttribute(Columns.CFT_VLR_REF)) ? new BigDecimal(coeficiente.getAttribute(Columns.CFT_VLR_REF).toString()) : null;
                    str_cft_vlr_ref = !TextHelper.isNull(cft_vlr_ref) ? NumberHelper.format(cft_vlr_ref.doubleValue(), NumberHelper.getLang(), 2, 8) : ApplicationResourcesHelper.getMessage("rotulo.consignacao.taxa.juros.nao.registrado", responsavel);
                    jurosAnual = !TextHelper.isNull(cft_vlr_ref) ? CDCHelper.getStrTaxaEquivalenteAnual(str_cft_vlr_ref) : ApplicationResourcesHelper.getMessage("rotulo.consignacao.taxa.juros.nao.registrado", responsavel);
  
                    if (!TextHelper.isNull(csa_nome)) {
                        temAsterisco |= (csa_nome.indexOf('*') != -1);
                    }
  
                    if (simulacaoPorTaxaJuros) {
                      if (simulacaoMetodoMexicano) {
                        cat = NumberHelper.reformat((coeficiente.getAttribute("CAT") != null) ? coeficiente.getAttribute("CAT").toString() : "0.00", "en", NumberHelper.getLang(), true);
                        iva = NumberHelper.reformat((coeficiente.getAttribute("IVA") != null) ? coeficiente.getAttribute("IVA").toString() : "0.00", "en", NumberHelper.getLang(), true);
                        totalPagar = NumberHelper.reformat((coeficiente.getAttribute("TOTAL_PAGAR") != null) ? coeficiente.getAttribute("TOTAL_PAGAR").toString() : "0.00", "en", NumberHelper.getLang(), true);
                      } else if (simulacaoMetodoBrasileiro) {
                        tac = NumberHelper.reformat((coeficiente.getAttribute("TAC_FINANCIADA") != null) ? coeficiente.getAttribute("TAC_FINANCIADA").toString() : "0.00", "en", NumberHelper.getLang(), true);
                        iof = NumberHelper.reformat((coeficiente.getAttribute("IOF") != null) ? coeficiente.getAttribute("IOF").toString() : "0.00", "en", NumberHelper.getLang(), true);
                      }
                    }
  
                    if (!ade_vlr.equals("")) {
                        if(!TextHelper.isNull(coeficiente.getAttribute(Columns.DTJ_CODIGO +"_"+CodedValues.FUN_SIMULAR_RENEGOCIACAO))){
                            vlr_simulado = NumberHelper.format(((BigDecimal)coeficiente.getAttribute("VLR_LIBERADO_"+CodedValues.FUN_SIMULAR_RENEGOCIACAO)).doubleValue(), NumberHelper.getLang(), true);
                        } else {
                            vlr_simulado = NumberHelper.format(((BigDecimal)coeficiente.getAttribute("VLR_LIBERADO")).doubleValue(), NumberHelper.getLang(), true);
                        }
                    } else {
                      if (vlr_ade.equals(new BigDecimal(Double.MAX_VALUE).toString())) {
                        vlr_simulado = "-";
                      } else {
                          if(!TextHelper.isNull(coeficiente.getAttribute(Columns.DTJ_CODIGO +"_"+CodedValues.FUN_SIMULAR_RENEGOCIACAO))){
                              vlr_simulado = NumberHelper.format(((BigDecimal)coeficiente.getAttribute("VLR_PARCELA_"+CodedValues.FUN_SIMULAR_RENEGOCIACAO)).doubleValue(), NumberHelper.getLang(), true);
                          } else {
                              vlr_simulado = NumberHelper.format(((BigDecimal)coeficiente.getAttribute("VLR_PARCELA")).doubleValue(), NumberHelper.getLang(), true);
                          }
                      }
                    }
                %>
                  <td colspan="2"><svg class="<%=(!vlrOk ? "i-indisponivel" : "i-disponivel")%>"><use xlink:href="#<%=(!vlrOk ? "i-status-x" : "i-status-v")%>"></use></svg><B><%=TextHelper.forHtmlContent(prazo)%></B></td>                    
                  <% if (vlrOk) { %>
                      <td onClick="javascript:reservar('<%=TextHelper.forJavaScript(csa_codigo)%>', '<%=TextHelper.forJavaScript(URLEncoder.encode(csa_nome, "ISO-8859-1"))%>', '<%=TextHelper.forJavaScriptAttribute(cft_codigo)%>', '<%=TextHelper.forJavaScriptAttribute(dtj_codigo)%>', '<%=TextHelper.forJavaScriptAttribute(vlr_ade)%>', '<%=TextHelper.forJavaScriptAttribute(vlr_liberado_param)%>', '<%=TextHelper.forJavaScriptAttribute(vlrLiberadoOk)%>', '<%=TextHelper.forJavaScriptAttribute(ranking)%>', '<%=TextHelper.forJavaScriptAttribute(simulacaoMetodoMexicano ? cat : tac)%>', '<%=TextHelper.forJavaScriptAttribute(simulacaoMetodoMexicano ? iva : iof)%>', '<%=TextHelper.forJavaScriptAttribute(svcCodigoItem)%>', '<%=TextHelper.forJavaScriptAttribute(prazo)%>'); return false;" align="right">
                  <% } else {
                       temX = true;
                  %>
                    <td align="right">
                  <% } %>
                  <%=TextHelper.forHtmlContent(vlr_simulado)%>&nbsp;
                    </td>
                    <% if (simulacaoPorTaxaJuros) { %>
                      <td align="right"><%=TextHelper.forHtmlContent(str_cft_vlr)%>&nbsp;</td>
                      <% if (temCET) { %>
                        <td align="right"><%=TextHelper.forHtmlContent(cetAnual)%>&nbsp;</td>
                           <%if (taxaJurosManCSA){ %>
                              <td align="right"><%=TextHelper.forHtmlContent(str_cft_vlr_ref)%></td>
                              <td align="right"><%=TextHelper.forHtmlContent(jurosAnual)%></td>
                           <%} %>
                      <% } else if (simulacaoMetodoBrasileiro) { %>
                        <td align="right"><%=TextHelper.forHtmlContent(simulacaoMetodoMexicano ? cat : tac)%>&nbsp;</td>
                        <td align="right"><%=TextHelper.forHtmlContent(simulacaoMetodoMexicano ? iva : iof)%>&nbsp;</td>
                      <% } %>
                    <% } %>
                  <td><%if (vlrOk) { %><a href="#no-back" onClick="javascript:reservar('<%=TextHelper.forJavaScriptAttribute(csa_codigo)%>', '<%=TextHelper.forJavaScriptAttribute(URLEncoder.encode(csa_nome, "ISO-8859-1"))%>', '<%=TextHelper.forHtmlContent(cft_codigo)%>', '<%=TextHelper.forJavaScriptAttribute(dtj_codigo)%>', '<%=TextHelper.forHtmlContent(vlr_ade)%>', '<%=TextHelper.forHtmlContent(vlr_liberado_param)%>', '<%=TextHelper.forJavaScriptAttribute(vlrLiberadoOk)%>', '<%=TextHelper.forHtmlContent(ranking)%>', '<%=TextHelper.forHtmlContent(simulacaoMetodoMexicano ? cat : tac)%>', '<%=TextHelper.forHtmlContent(simulacaoMetodoMexicano ? iva : iof)%>', '<%=TextHelper.forHtmlContent(svcCodigoItem)%>', '<%=TextHelper.forHtmlContent(prazo)%>'); return false;"><hl:message key="rotulo.acoes.selecionar"/></a><%} else {%><hl:message key="rotulo.acoes.selecionar"/><%}%></td>
                <% } else { %>
                  <td>&nbsp;</td>
                  <td>&nbsp;</td>
                  <td>&nbsp;</td>
                  <td>&nbsp;</td>
                  <% if (simulacaoPorTaxaJuros) { %>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <% if (!temCET) { %>
                      <td>&nbsp;</td>
                    <% } %>
                  <% } %>
                <%
                   }%>
                  
                 <%}
                %>
                </tr>
                <%}%>
                    
                <%}%>
              </tbody>
			  <tfoot>
                <tr>
                  <td colspan="<%=colspan%>"><hl:message key="mensagem.simulacao.resultado" arg0="<%=DateHelper.toDateTimeString(dataAtual)%>" /></td>
                </tr>
              </tfoot>
          </table>
	     </div>
		</div>
		<div class="btn-action">
			<a class="btn btn-outline-danger" href="#no-back" onclick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.voltar"/></a>
		</div>
    <% if (temX) { %>
          <input type="hidden" name="RSE_CODIGO" value="<%=TextHelper.forHtmlAttribute(rseCodigo)%>">
          <input type="hidden" name="SVC_CODIGO" value="<%=TextHelper.forHtmlAttribute(svcCodigo)%>">
          <input type="hidden" name="CSA_CODIGO" value="<%=TextHelper.forHtmlAttribute(csaCodigo)%>">
          <input type="hidden" name="CSA_NOME" value="<%=TextHelper.forHtmlAttribute(csaNome)%>">
          <input type="hidden" name="SVC_DESCRICAO" value="<%=TextHelper.forHtmlAttribute(svcDescricao)%>">
          <input type="hidden" name="SVC_IDENTIFICADOR" value="<%=TextHelper.forHtmlAttribute(svcIdentificador)%>">
          <input type="hidden" name="CSA_IDENTIFICADOR" value="<%=TextHelper.forHtmlAttribute(csaIdentificador)%>">
          <%for (int i = 0; i < chkAde.length; i++) {  %>
             <input type="hidden" name="chkADE" value="<%=TextHelper.forHtmlAttribute(chkAde[i])%>">
          <%}  %>          
<!--       Atividade futura, nescessario mudança de estrutura para inclusão de "motivoIndisponibilidade" conforme exemplo na visualizarRankingSimulacao -->
<!--           <div class="legenda"> -->
<%--             <h2 class="legenda-head"><hl:message key="mensagem.simulacao.informacao.legenda.indisponibilidade"/></h2> --%>
<!--             <div class="legenda-body"> -->
<%--               <p class="legenda-item"><span class="legenda-indice"><hl:message key="mensagem.simulacao.informacao.numero.1"/></span><hl:message key="mensagem.simulacao.informacao.texto.1"/></p> --%>
<%--               <p class="legenda-item"><span class="legenda-indice"><hl:message key="mensagem.simulacao.informacao.numero.2"/></span><hl:message key="mensagem.simulacao.informacao.texto.2"/></p> --%>
<%--               <p class="legenda-item"><span class="legenda-indice"><hl:message key="mensagem.simulacao.informacao.numero.3"/></span><hl:message key="mensagem.simulacao.informacao.texto.3"/></p> --%>
<%--               <p class="legenda-item"><span class="legenda-indice"><hl:message key="mensagem.simulacao.informacao.numero.4"/></span><hl:message key="mensagem.simulacao.informacao.texto.4"/></p> --%>
<%--               <p class="legenda-item"><span class="legenda-indice"><hl:message key="mensagem.simulacao.informacao.numero.5"/></span><hl:message key="mensagem.simulacao.informacao.texto.5"/></p> --%>
<%--               <% if (qtdeConsignatariasSimulacao != Integer.MAX_VALUE) { %> --%>
<%--               <p class="legenda-item"><span class="legenda-indice"><hl:message key="mensagem.simulacao.informacao.numero.6"/></span><hl:message key="mensagem.simulacao.informacao.texto.6" arg0="<%= String.valueOf(qtdeConsignatariasSimulacao) %>"/></p> --%>
<%--               <% } %> --%>
<!--             </div> -->
<!--            </div> -->
    <% } %>
</form>
</c:set>
<c:set var="javascript">
<script language="JavaScript" type="text/JavaScript">
  f0 = document.forms[0];

  function reservar(codigo, nome, cft, dtj, ade_vlr, vlr_liberado, vlrLiberadoOk, ranking, val1, val2, servico, prz_vlr) {
	console.log("IR PARA TELA DE CONFIRMAÇAO")
    var URL = '../v3/simularRenegociacao?acao=confirmar&CSA_CODIGO=' + codigo
            + '&CSA_NOME=' + nome
            + '&CFT_CODIGO=' + cft
            + '&DTJ_CODIGO=' + dtj
            + '&ADE_VLR=' + ade_vlr
            + '&VLR_LIBERADO=' + vlr_liberado
			+ '&vlrLiberadoOk=' + vlrLiberadoOk
            + '&RANKING=' + ranking
            + '&SVC_CODIGO=' + servico            
            + '&RSE_CODIGO=<%=TextHelper.forHtmlContent(rseCodigo)%>'            
            + '&CFT_DIA=<%=TextHelper.forHtmlContent(dia)%>'
            <% if (simulacaoMetodoMexicano) { %>
            + '&ADE_VLR_CAT=' + val1
            + '&ADE_VLR_IVA=' + val2
            + '&ADE_PERIODICIDADE=' + '<%=TextHelper.forHtmlContent(adePeriodicidade)%>'
            <% } else if (simulacaoMetodoBrasileiro) { %>
            + '&ADE_VLR_TAC=' + val1
            + '&ADE_VLR_IOF=' + val2
            <% } %>
            + '&PRZ_VLR=' + prz_vlr
            + '&SVC_IDENTIFICADOR= + <%=TextHelper.forHtmlContent(svcIdentificador)%>'
            + '&CSA_IDENTIFICADOR= + <%=TextHelper.forHtmlContent(csaIdentificador)%>'
            + '&tipo=simula_renegociacao'  
            <%for (int i = 0; i < chkAde.length; i++) { %>
                + '&chkADE=<%=TextHelper.forHtmlContent(chkAde[i] )%>'                
            <%}%>
            + '&<%out.print(SynchronizerToken.generateToken4URL(request));%>';
  
    postData(URL);
  }

  function formLoad() {
  }
  
</script>
</c:set>
<%-- Leiaute Fixo --%>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:attribute name="pageModals">${pageModals}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4> 