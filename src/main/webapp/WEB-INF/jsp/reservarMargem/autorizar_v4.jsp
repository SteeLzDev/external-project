<%--
* <p>Title: autorizar.jsp</p>
* <p>Description: Página que autoriza a reserva da margem do servidor através de sua senha</p>
* <p>Copyright: Copyright (c) 2002-2017</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.math.*"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.periodo.PeriodoHelper" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ page import="com.zetra.econsig.dto.web.ResultadoSimulacao"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

// Verifica se há permissão para parâmetros de inclusão avançada
boolean usuPossuiIncAvancadaAde = responsavel.temPermissao(CodedValues.FUN_INCLUSAO_AVANCADA_CONSIGNACAO);

// Verifica se o sistema está configurado para trabalhar com o CET.
boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);

// Se o simulador segue as regras mexicanas
boolean simulacaoMetodoMexicano = ParamSist.paramEquals(CodedValues.TPC_METODO_CALCULO_SIMULACAO, CodedValues.MCS_MEXICANO, responsavel);
boolean simulacaoMetodoBrasileiro = ParamSist.paramEquals(CodedValues.TPC_METODO_CALCULO_SIMULACAO, CodedValues.MCS_BRASILEIRO, responsavel);

CustomTransferObject servidor = (CustomTransferObject) request.getAttribute("servidor");
CustomTransferObject permissionario = (CustomTransferObject) request.getAttribute("permissionario");
ParamSvcTO paramSvcCse = (ParamSvcTO) request.getAttribute("paramSvcCse");
List<ResultadoSimulacao> resultadoSimulacao = (List<ResultadoSimulacao>) request.getAttribute("resultadoSimulacao");
ResultadoSimulacao resultadoSimulacaoCSA = (ResultadoSimulacao) request.getAttribute("resultadoSimulacaoCSA");

Boolean permiteCadVlrLiqTxJuros = (Boolean) request.getAttribute("permiteCadVlrLiqTxJuros");
Boolean permiteCadVlrTac = (Boolean) request.getAttribute("permiteCadVlrTac");
Boolean permiteCadVlrIof = (Boolean) request.getAttribute("permiteCadVlrIof");
Boolean permiteCadVlrLiqLib = (Boolean) request.getAttribute("permiteCadVlrLiqLib");
Boolean permiteCadVlrMensVinc = (Boolean) request.getAttribute("permiteCadVlrMensVinc");
Boolean permiteCadVlrSegPrestamista = (Boolean) request.getAttribute("permiteCadVlrSegPrestamista");
Boolean possuiCorrecaoVlrPresente = (Boolean) request.getAttribute("possuiCorrecaoVlrPresente");
Boolean serSenhaObrigatoria = (Boolean) request.getAttribute("serSenhaObrigatoria");
Boolean bloqueiaReservaLimiteSimulador = (Boolean) request.getAttribute("bloqueiaReservaLimiteSimulador");
Integer qtdeConsignatariasSimulacao = (Integer) request.getAttribute("qtdeConsignatariasSimulacao");
String mascaraAdeIdentificador = (String) request.getAttribute("mascaraAdeIdentificador");
String labelTipoValor = (String) request.getAttribute("labelTipoValor");
String labelAdePrazo = (String) request.getAttribute("labelAdePrazo");

BigDecimal adeVlrCorrigido = (BigDecimal) request.getAttribute("adeVlrCorrigido");
String adePeriodicidade = (String) request.getAttribute("adePeriodicidade");
String adeVlr = (String) request.getAttribute("adeVlr");
String adePrazo = (String) request.getAttribute("adePrazo");
Integer adeCarencia = (Integer) request.getAttribute("adeCarencia");
String adeIndice = (String) request.getAttribute("adeIndice");
String adeIndiceDescricao = (String) request.getAttribute("adeIndiceDescricao");
String adeResponsavel = (String) request.getAttribute("adeResponsavel");
String adeDataIni = (String) request.getAttribute("adeDataIni");
String adeDataFim = (String) request.getAttribute("adeDataFim");
java.sql.Date ocaPeriodo = (java.sql.Date) request.getAttribute("ocaPeriodo");
boolean exigeCodAutorizacaoSMS = (Boolean) request.getAttribute("exigeCodAutorizacaoSMS");

String linkRet = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "linkRet")) ? JspHelper.verificaVarQryStr(request, "linkRet") : (String) request.getAttribute("linkRet");
String link = null;

if(!TextHelper.isNull(linkRet)) {
    link = SynchronizerToken.updateTokenInURL(linkRet.replace('$','?').replace('|','&').replace('(','='), request) + "&linkRet=" + linkRet;
} else {
    link = TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request));
}

String tps_277 = (String) request.getAttribute("tps_277");
boolean reservaSaudeSemRegras = request.getAttribute("reservaSaudeSemRegras") != null;
boolean enderecoObrigatorio = request.getAttribute("enderecoObrigatorio") != null && request.getAttribute("enderecoObrigatorio").toString().equals("true");
boolean celularObrigatorio = request.getAttribute("celularObrigatorio") != null && request.getAttribute("celularObrigatorio").toString().equals("true");
boolean enderecoCelularObrigatorio = request.getAttribute("enderecoCelularObrigatorio") != null && request.getAttribute("enderecoCelularObrigatorio").toString().equals("true");

boolean portalBeneficio = request.getAttribute("portalBeneficio") != null;
String corCodigo = (String) request.getAttribute("corCodigo");
boolean inclusaoJudicial = request.getAttribute("inclusaoJudicial") != null;
%>
<c:set var="title">
<%=TextHelper.forHtmlContent(request.getAttribute("tituloPagina"))%>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
      <% if(resultadoSimulacao != null) {
        Integer nroColunas = request.getAttribute("nroColunasSimulacao") != null ? (Integer) request.getAttribute("nroColunasSimulacao") : 1;
        int meio = new Double(Math.ceil((double) resultadoSimulacao.size() / nroColunas)).intValue();
      %>
        <div class="row">
          <div class="col-sm">
            <div class="card">
              <div class="card-header">
                <h2 class="card-header-title">
                  <hl:message key="rotulo.reservar.margem.resultado.simulacao"/> - <%=DateHelper.toDateTimeString(DateHelper.getSystemDatetime())%><br/>
                  <hl:message key="rotulo.reservar.margem.simulacao.parcela"/>:&nbsp;<hl:message key="rotulo.moeda"/>&nbsp;<%=TextHelper.forHtmlContent(adeVlr)%> - <hl:message key="rotulo.reservar.margem.simulacao.prazo"/>: <%=TextHelper.forHtmlContent(adePrazo)%>  
                </h2>
              </div>
              <div class="card-body table-responsive p-0">
                <table class="table table-striped table-hover">
                  <thead>
                    <tr>
                      <th scope="col"><hl:message key="rotulo.consignacao.ranking"/></th>
                      <th scope="col"><hl:message key="rotulo.consignataria.singular"/></th>
                      <th scope="col"><hl:message key="rotulo.consignacao.valor.liberado.abreviado"/>&nbsp;(<hl:message key="rotulo.moeda"/>)</th>
                      <% if (!temCET) { %>
                        <th scope="col"><hl:message key="rotulo.consignacao.taxa.juros"/></th>
                        <% if (simulacaoMetodoMexicano) { %>
                          <th scope="col"><hl:message key="rotulo.consignacao.valor.cat.abreviado"/> (<hl:message key="rotulo.porcentagem"/>)</th>
                          <th scope="col"><hl:message key="rotulo.consignacao.valor.iva.abreviado"/> (<hl:message key="rotulo.moeda"/>)</th>
                        <% } else if (simulacaoMetodoBrasileiro) { %>
                          <th scope="col"><hl:message key="rotulo.consignacao.valor.tac.abreviado"/> (<hl:message key="rotulo.moeda"/>)</th>
                          <th scope="col"><hl:message key="rotulo.consignacao.valor.iof.abreviado"/> (<hl:message key="rotulo.moeda"/>)</th>
                        <% } %>
                        <% } else { %>
                          <th scope="col"><hl:message key="rotulo.consignacao.cet"/></th>
                          <th scope="col"><hl:message key="rotulo.consignacao.cet.anual"/></th>
                        <% } %>
                    </tr>
                  </thead>
                  <tbody>
                      <% for(int i = 0; i < resultadoSimulacao.size(); i++) {
                          ResultadoSimulacao linhaResultadoSimulacao = resultadoSimulacao.get(i);
                      %>
                        <tr>
                          <td><%=TextHelper.forHtmlContent(linhaResultadoSimulacao.getRanking())%>&ordm;</td>
                          <td><%=TextHelper.forHtmlContentComTags(linhaResultadoSimulacao.getCsaNome().toUpperCase())%></td>
                          <td><%=TextHelper.forHtmlContent(linhaResultadoSimulacao.getTextoValorLiberado())%>&nbsp;</td>
                          <% if (temCET) { %>
                            <td><%=TextHelper.forHtmlContent(linhaResultadoSimulacao.getTextoValorTaxaJuros())%></td>
                            <td><%=TextHelper.forHtmlContent(linhaResultadoSimulacao.getTextoValorTaxaJurosAnual())%></td>
                          <% } else { %> 
                             <td><%=TextHelper.forHtmlContent(linhaResultadoSimulacao.getTextoValorTaxaJuros())%></td>
                             <% if (simulacaoMetodoMexicano || simulacaoMetodoBrasileiro) { %>
                              <td><%=TextHelper.forHtmlContent(linhaResultadoSimulacao.getTextoValorTaxaExtra())%></td>
                              <td><%=TextHelper.forHtmlContent(linhaResultadoSimulacao.getTextoValorImposto())%></td>
                             <% } %> 
                          <% } %>
                        </tr>
                      <% } %>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
      <% } %>
       <form action="<%= TextHelper.forHtmlAttribute(request.getAttribute("acaoFormulario")) %>" method="post" name="form1">
			<div class="row">
				<div class="col-sm">
					<div class="card">
						<div class="card-header hasIcon">
                            <span class="card-header-icon"><svg width="26"><use xlink:href="#i-consignacao"></use></svg></span>
                            <h2 class="card-header-title"><hl:message key="rotulo.dados.consignacao.titulo" /></h2>
						</div>
						<div class="card-body">
                            <div class="alert alert-warning" role="alert">
                                <ul>              
                                  <li><p class="mb-0"><hl:message key="mensagem.reservar.margem.verificar.info" /></p></li>
                                </ul>
                            </div>
							<dl class="row data-list">
                            <% if (request.getAttribute("csaNome") != null) { %>
 								<dt class="col-6"><hl:message key="rotulo.consignataria.singular"/>:</dt> <dd class="col-6"><%=TextHelper.forHtmlContent(request.getAttribute("csaNome"))%></dd>
                            <% } %>
                            <% if (request.getAttribute("corNome") != null) { %>
                                <dt class="col-6"><hl:message key="rotulo.correspondente.singular"/>:</dt> <dd class="col-6"><%=TextHelper.forHtmlContent(request.getAttribute("corNome"))%></dd>
                            <% } %>
                            <% if (request.getAttribute("taxaCadastrada") != null) { %>
                               <dt class="col-6"><hl:message key="<%= temCET ? "rotulo.consignacao.cet" : "rotulo.consignacao.taxa.juros"%>"/>:</dt> <dd class="col-6"><%=TextHelper.forHtmlContent(request.getAttribute("taxaCadastrada"))%></dd>
                            <% } %>
                                <%-- Utiliza a tag library DetalheServidorTag.java para exibir os dados do servidor --%>
                                <hl:detalharServidorv4 name="servidor" complementos="true" validaAvancadaDataNasc="validaDataNascAvancado" scope="request" />
                                <%-- Fim dos dados da ADE --%>
								<dt class="col-6"><hl:message key="rotulo.consignacao.data"/>:</dt> <dd class="col-6"><%=DateHelper.toDateString(DateHelper.getSystemDatetime())%></dd>
                            <% if (permiteCadVlrTac) { %>
                                <dt class="col-6"><hl:message key="rotulo.consignacao.valor.tac"/>&nbsp;(<hl:message key="rotulo.moeda"/>):</dt>
                                <dd class="col-6"><hl:htmlinput name="adeVlrTac" type="text" classe="form-control" di="adeVlrTac" size="8" mask="#F11" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "adeVlrTac"))%>" readonly="true" /></dd>
                            <% } %>
                            <% if (permiteCadVlrIof) { %>
                                <dt class="col-6"><hl:message key="rotulo.consignacao.valor.iof"/>&nbsp;(<hl:message key="rotulo.moeda"/>):</dt>
                                <dd class="col-6"><hl:htmlinput name="adeVlrIof" type="text" classe="form-control" di="adeVlrIof" size="8" mask="#F11" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "adeVlrIof"))%>" readonly="true" /></dd>
                            <% } %>
                            <% if (permiteCadVlrLiqLib) { %>
                                <dt class="col-6"><hl:message key="rotulo.consignacao.valor.liquido.liberado"/>&nbsp;(<hl:message key="rotulo.moeda"/>):</dt>
                                <dd class="col-6"><hl:htmlinput name="adeVlrLiquido" type="text" classe="form-control" di="adeVlrLiquido" size="12" mask="#F15" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "adeVlrLiquido"))%>" readonly="true" /></dd>
                            <% } %>
                            <% if (permiteCadVlrMensVinc) { %>
                                <dt class="col-6"><hl:message key="rotulo.consignacao.valor.mensalidade.vinc"/>&nbsp;(<hl:message key="rotulo.moeda"/>):</dt>
                                <dd class="col-6"><hl:htmlinput name="adeVlrMensVinc" type="text" classe="form-control" di="adeVlrMensVinc" size="12" mask="#F15" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "adeVlrMensVinc"))%>" readonly="true" /></dd>
                            <% } %>
                            <% if (permiteCadVlrSegPrestamista) { %>
                                <dt class="col-6"><hl:message key="rotulo.consignacao.seguro.prestamista"/>&nbsp;(<hl:message key="rotulo.moeda"/>):</dt>
                                <dd class="col-6"><hl:htmlinput name="adeVlrSegPrestamista" type="text" classe="form-control" di="adeVlrSegPrestamista" size="8" mask="#F11" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "adeVlrSegPrestamista"))%>" readonly="true" /></dd>
                            <% } %>
                            <% if (permiteCadVlrLiqTxJuros) { %>
                                <dt class="col-6"><hl:message key='<%=(String)((temCET) ? "rotulo.consignacao.cet" : "rotulo.consignacao.taxa.juros")%>'/>:</dt>
                                <dd class="col-6"><hl:htmlinput name="adeTaxaJuros" type="text" classe="form-control" di="adeTaxaJuros" size="12" mask="#F15" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "adeTaxaJuros"))%>" readonly="true" /></dd>
                            <% } %>
								<dt class="col-6"><hl:message key="rotulo.consignacao.valor.parcela"/>&nbsp;(<%=TextHelper.forHtmlContent(labelTipoValor)%>):</dt>
                                <dd class="col-6"><hl:htmlinput name="adeVlr" type="text" classe="form-control" di="adeVlr" size="8" mask="#F11" value="<%=TextHelper.forHtmlAttribute(adeVlr)%>" readonly="true" /></dd>
                            <% if (possuiCorrecaoVlrPresente && adeVlrCorrigido != null) { %>
                              <% String strAdeVlrCorrigido = NumberHelper.format(adeVlrCorrigido.doubleValue(), NumberHelper.getLang()); %>
                                <dt class="col-6"><hl:message key="rotulo.consignacao.valor.presente"/>&nbsp;(<hl:message key="rotulo.moeda"/>):</dt>
                                <dd class="col-6"><%=TextHelper.forHtmlContent(strAdeVlrCorrigido)%></dd>
                                <hl:htmlinput type="hidden" name="dataEvento" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, \"dataEvento\"))%>" />
                                <hl:htmlinput type="hidden" name="adeVlrCorrigido" value="<%=TextHelper.forHtmlAttribute(strAdeVlrCorrigido)%>" />
                            <% } %>
                  
                            <% String rotuloPeriodicidadePrazo = "(" + ApplicationResourcesHelper.getMessage("rotulo.meses", responsavel) + ")"; %>
                            <% if (request.getAttribute("exibirCampoPeriodicidade") != null) { %>
                                <% rotuloPeriodicidadePrazo = ""; %>
                                <dt class="col-6"><hl:message key="rotulo.consignacao.periodicidade"/>:</dt>
                                <dd class="col-6"><%= adePeriodicidade.equals(CodedValues.PERIODICIDADE_FOLHA_QUINZENAL) ? ApplicationResourcesHelper.getMessage("rotulo.consignacao.periodicidade.quinzenal", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.consignacao.periodicidade.mensal", responsavel) %></dd>
                                <hl:htmlinput name="adePeriodicidade" di="adePeriodicidade" type="hidden" value="<%=TextHelper.forHtmlAttribute(adePeriodicidade)%>" />
                            <% } else { %>
                               <hl:htmlinput type="hidden" name="adePeriodicidade" di="adePeriodicidade" value="<%=TextHelper.forHtmlAttribute(PeriodoHelper.getPeriodicidadeFolha(responsavel))%>" />
                            <% } %>
								<dt class="col-6"><hl:message key="rotulo.consignacao.prazo"/>&nbsp;<%=TextHelper.forHtmlContent(rotuloPeriodicidadePrazo)%>:</dt>
                                <dd class="col-6"><%=TextHelper.forHtmlContent(labelAdePrazo.equals("") ? ApplicationResourcesHelper.getMessage("rotulo.indeterminado", responsavel) : labelAdePrazo)%></dd>
                                <hl:htmlinput name="adePrazo" type="hidden" di="adePrazo" value="<%=TextHelper.forHtmlAttribute(adePrazo)%>" />
                                <hl:htmlinput name="adeSemPrazo" type="hidden" di="adeSemPrazo" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "adeSemPrazo"))%>" />
								<dt class="col-6"><hl:message key="rotulo.consignacao.data.inicial"/>:</dt>
                                <dd class="col-6"><%=TextHelper.forHtmlContent(adeDataIni)%></dd>
								<dt class="col-6"><hl:message key="rotulo.consignacao.data.final"/>:</dt>
                                <dd class="col-6"><%=TextHelper.forHtmlContent(adeDataFim.equals("") ? ApplicationResourcesHelper.getMessage("rotulo.indeterminado", responsavel) : adeDataFim)%></dd>

                              <% if (!responsavel.isSer() && ShowFieldHelper.showField(FieldKeysConstants.RESERVAR_MARGEM_CARENCIA, responsavel)) { %>
                                <dt class="col-6"><hl:message key="rotulo.consignacao.carencia"/>:</dt> <dd class="col-6"><%=TextHelper.forHtmlContent(adeCarencia)%>&nbsp;<%=TextHelper.forHtmlContent(rotuloPeriodicidadePrazo)%></dd>
                              <% } %>
                    
                              <% if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_CAD_INDICE, CodedValues.TPC_SIM, responsavel)) { %>
                                <dt class="col-6"><hl:message key="rotulo.consignacao.indice"/>:</dt>
                                <dd class="col-6"><hl:htmlinput name="adeIndice" type="text" classe="form-control" di="adeIndice" size="8" value="<%=TextHelper.forHtmlAttribute(adeIndice)%>" readonly="true" /><%=TextHelper.forHtmlContent(adeIndiceDescricao)%></dd>
                              <% } %>
                    
                              <% if (possuiCorrecaoVlrPresente) { 
                                   String strDataEvento = JspHelper.verificaVarQryStr(request, "dataEvento");
                              %>
                                <dt class="col-6"><hl:message key="rotulo.consignacao.data.evento"/>:</dt>
                                <dd class="col-6"><hl:htmlinput name="dataEvento" di="dataEvento" type="text" classe="form-control" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" value="<%=TextHelper.forHtmlAttribute(strDataEvento != null ? strDataEvento : \"\")%>" readonly="true" /></dd>
                              <% } %>
                    
                              <% if (!responsavel.isSer()) { %>
                                <dt class="col-6"><hl:message key="rotulo.consignacao.identificador"/>:</dt>
                                <dd class="col-6"><hl:htmlinput name="adeIdentificador" type="text" classe="form-control" di="adeIdentificador" size="15" mask="<%=TextHelper.isNull(mascaraAdeIdentificador) ? "#*40" : mascaraAdeIdentificador %>" nf="btnEnvia" readonly="true" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "adeIdentificador"))%>" /></dd>
                                <dt class="col-6"><hl:message key="rotulo.consignacao.responsavel"/>:</dt>
                                <dd class="col-6"><%=TextHelper.forHtmlContent(adeResponsavel)%></dd>
                                
                   
                                <% if (request.getAttribute("exigeModalidadeOperacao") != null) { %>
                                <dt class="col-6"><hl:message key="rotulo.consignacao.modalidade.operacao"/>:</dt>
                                <dd class="col-6"><hl:htmlinput name="tdaModalidadeOp" type="text" classe="form-control" di="tdaModalidadeOp" readonly="true" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "tdaModalidadeOp"))%>"/></dd>
                                <% } %> 
                    
                                <% if (request.getAttribute("exigeMatriculaSerCsa") != null) { %>
                                <dt class="col-6"><hl:message key="rotulo.consignacao.matricula.ser.csa"/>:</dt>
                                <dd class="col-6"><hl:htmlinput name="tdaMatriculaCsa" type="text" classe="form-control" di="tdaMatriculaCsa" readonly="true" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "tdaMatriculaCsa"))%>"/></dd>
                                <% } %>
                    
                              <% } %>
                    
                              <% if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "FILE1"))) { %>
                                <dt class="col-6"><hl:message key="rotulo.consignacao.anexo.arquivo"/>:</dt>
                                <dd class="col-6"><hl:htmlinput name="FILE1" type="text" classe="form-control" di="FILE1" size="15" readonly="true" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "FILE1"))%>" /></dd>
                                <dt class="col-6"><hl:message key="rotulo.consignacao.anexo.arquivo.desc"/>:</dt>
                                <dd class="col-6"><hl:htmlinput name="AAD_DESCRICAO" type="text" classe="form-control" di="AAD_DESCRICAO" readonly="true" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "AAD_DESCRICAO"))%>"/></dd>
                              <% } %>
                    
                              <%-- TODO O que fazer com coluna sem descricao --%>
                              <% if (resultadoSimulacaoCSA != null) { %>
                                <dt class="col-6">&nbsp;</dt>
                                <dd class="col-6"><hl:message key="mensagem.reservar.margem.ranking.consignataria" arg0="<%=TextHelper.forHtmlAttribute(resultadoSimulacaoCSA.getRanking())%>"/></dd>
                              <% } %>
                    
                              <%-- TODO Inclui processamento específico --%>
                              <% if (request.getAttribute("processaReservaMargem") != null) { %>
                                <%=TextHelper.forHtmlContent(request.getAttribute("processaReservaMargem"))%>
                              <% } %>
								
                            <% if (request.getAttribute("plaDescricao") != null) { %>
                                <dt class="col-6"><hl:message key="rotulo.plano.singular"/>:</dt> <dd class="col-6"><%=TextHelper.forHtmlContent(request.getAttribute("plaDescricao"))%></dd>
                            <% } %>
                              <dt class="col-6"><hl:message key="rotulo.servico.singular"/>:</dt> <dd class="col-6"><%=TextHelper.forHtmlContent(request.getAttribute("svcDescricao"))%></dd>
                              
                            <% if (request.getAttribute("permiteDescontoViaBoleto") != null) { %>
                                <dt class="col-6"><hl:message key="rotulo.reservar.margem.pagar.via.boleto"/>:</dt> <dd class="col-6"><%=request.getAttribute("permiteDescontoViaBoleto").equals("S") ? ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.nao", responsavel)%></dd>
                            <% } %>  
      
                            <% if (request.getAttribute("codigoDependente") != null) { %>
                                <dt class="col-6"><hl:message key="rotulo.reservar.margem.dependente"/>:</dt> <dd class="col-6"><%=ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel)%></dd>
                                <dt class="col-6"><hl:message key="rotulo.reservar.margem.dependente.nome"/>:</dt> <dd class="col-6"><%=TextHelper.forHtmlContent(request.getAttribute("nomeDependente"))%></dd>
                            <% } %>
                            
                            <% if (request.getAttribute("lstTipoDadoAdicional") != null) { %>
                                 <% for (TransferObject tda : (List<TransferObject>) request.getAttribute("lstTipoDadoAdicional")) {
                                      String tdaValor = TextHelper.forHtmlContent(JspHelper.parseValor(request, null, "TDA_" + (String) tda.getAttribute(Columns.TDA_CODIGO),(String) tda.getAttribute(Columns.TDA_DOMINIO))); 
                                 %>
                                  <hl:paramv4 
                                      prefixo="TDA_" 
                                      descricao="<%=TextHelper.forHtmlAttribute(tda.getAttribute(Columns.TDA_DESCRICAO))%>"
                                      codigo="<%=(String) tda.getAttribute(Columns.TDA_CODIGO)%>"
                                      dominio="<%=(String) tda.getAttribute(Columns.TDA_DOMINIO)%>"
                                      valor="<%=tdaValor%>"
                                      desabilitado="<%= CodedValues.CAS_BLOQUEADO.equals(tda.getAttribute(Columns.SPT_EXIBE)) %>"
                                      dte="true"
                                      />
                                 <% } %>
                              <% } %>
                              
                              <%-- Recebe o código de autorização digitado pelo Servidor via SMS --%>
                              <%if (responsavel.isSer() && exigeCodAutorizacaoSMS) { %>
					            <dt class="col-6"><hl:message key="rotulo.digite.codigo.autorizacao.enviado.cel"/>:</dt>
                                <dd class="col-6"><hl:htmlinput name="codAutorizacao" type="text" classe="form-control" di="codAutorizacao" size="8" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "codAutorizacao"))%>" /></dd>
					       <% } %>
                                
                            <%-- Senha do servidor --%>
                            <% if (!responsavel.isSer() || (responsavel.isSer() && ParamSist.getBoolParamSist(CodedValues.TPC_SOLICITA_SENHA_AUTORIZACAO_OPE_REALIZADAS_SER, responsavel))) { %>
                              <%
                                 String mascaraLogin = (String) ParamSist.getInstance().getParam(CodedValues.TPC_MASCARA_LOGIN_EXTERNO_SERVIDOR, responsavel);
                                 if (!TextHelper.isNull(mascaraLogin)) {
                              %>
                              <dt class="col-6"><hl:message key="rotulo.usuario.autorizacao.servidor.singular"/><%=serSenhaObrigatoria ? "" : " " + ApplicationResourcesHelper.getMessage("rotulo.campo.opcional", responsavel)%>:</dt>
                              <dd class="col-6"><hl:htmlinput name="serLogin" type="text" classe="form-control" di="serLogin" size="15" mask="<%=TextHelper.forHtmlAttribute(mascaraLogin)%>" /></dd>
                              <% } %>
                              <hl:senhaServidorv4 senhaObrigatoria="<%=(String)(serSenhaObrigatoria ? "true" : "false")%>"                                                 
                                                svcCodigo="<%=request.getAttribute("svcCodigo").toString()%>"
                                                senhaParaAutorizacaoReserva="true"
                                                nomeCampoSenhaCriptografada="serAutorizacao"
                                                rseCodigo="<%=request.getAttribute("rseCodigo").toString()%>"
                                                nf="btnEnvia"
                                                classe="form-control"/>
                            <% } %>
							</dl>
						</div>
					</div>
          
                    <%-- Abre tela de confirmação dos dados do servidor --%>
                    <%if(responsavel.isSer()){ %>
                        <hl:confirmarDadosSERv4 serCodigo="<%=TextHelper.forHtmlAttribute(request.getAttribute("serCodigo"))%>" rseCodigo="<%=TextHelper.forHtmlAttribute(request.getAttribute("rseCodigo"))%>" csaCodigo="<%=TextHelper.forHtmlAttribute(request.getAttribute("csaCodigo"))%>"/>
                    <%} %>

                    <%-- Inclusão Avançada --%>
                    <% if (responsavel.temPermissao(CodedValues.FUN_INCLUSAO_AVANCADA_CONSIGNACAO) && CodedValues.FUN_RES_MARGEM.equals(responsavel.getFunCodigo())) { %>
                        <%@ include file="../reservarMargem/incluirCamposInclusaoAvancada_v4.jsp" %>
                    <% } else if (inclusaoJudicial) {%>
                        <%@ include file="../reservarMargem/incluirCamposInclusaoAvancadaDecisao_v4.jsp" %>
                    <%} %>

                    <div class="btn-action">
                        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=link%>')" alt="<hl:message key="rotulo.botao.voltar"/>" title="<hl:message key="rotulo.botao.voltar"/>"><hl:message key="rotulo.botao.voltar"/></a>
                        <a class="btn btn-primary" name="btnEnvia" id="btnEnvia" href="#no-back" onClick="if(campos()){f0.submit();} return false;" alt="<hl:message key="rotulo.botao.confirmar"/>" title="<hl:message key="rotulo.botao.confirmar"/>"><svg width="17"><use xlink:href="#i-confirmar"></use></svg><hl:message key="rotulo.botao.confirmar"/></a>
                    </div>
				</div>
			</div>
            <%= SynchronizerToken.generateHtmlToken(request) %>
            <hl:htmlinput type="hidden" name="acao" value="<%= TextHelper.forHtmlAttribute(request.getAttribute("proximaOperacao") != null ? request.getAttribute("proximaOperacao").toString() : "incluirReserva") %>" />
            <hl:htmlinput type="hidden" name="CSA_CODIGO"     value="<%=TextHelper.forHtmlAttribute(request.getAttribute("csaCodigo"))%>" />
            <hl:htmlinput type="hidden" name="CNV_CODIGO"     value="<%=TextHelper.forHtmlAttribute(request.getAttribute("cnvCodigo"))%>" />
            <hl:htmlinput type="hidden" name="RSE_CODIGO"     value="<%=TextHelper.forHtmlAttribute(request.getAttribute("rseCodigo"))%>" />
            <hl:htmlinput type="hidden" name="ORG_CODIGO"     value="<%=TextHelper.forHtmlAttribute(request.getAttribute("orgCodigo"))%>" />          
            <hl:htmlinput type="hidden" name="SVC_CODIGO"     value="<%=TextHelper.forHtmlAttribute(request.getAttribute("svcCodigo"))%>" />
            <hl:htmlinput type="hidden" name="PLA_CODIGO"     value="<%=TextHelper.forHtmlAttribute(request.getAttribute("plaCodigo"))%>" />
            <hl:htmlinput type="hidden" name="PRM_CODIGO"     value="<%=TextHelper.forHtmlAttribute(request.getAttribute("prmCodigo"))%>" />
            <hl:htmlinput type="hidden" name="CFT_CODIGO"     value="<%=TextHelper.forHtmlAttribute(request.getAttribute("cftCodigo"))%>" />
            <hl:htmlinput type="hidden" name="DTJ_CODIGO" 	  value="<%=TextHelper.forHtmlAttribute(request.getAttribute("dtjCodigo")) %>" />
            
            <hl:htmlinput type="hidden" name="dataNasc"       value="<%=TextHelper.forHtmlAttribute(request.getAttribute("serDataNasc"))%>" />
            <hl:htmlinput type="hidden" name="numBanco"       value="<%=TextHelper.forHtmlAttribute(request.getAttribute("numBanco"))%>" />
            <hl:htmlinput type="hidden" name="numAgencia"     value="<%=TextHelper.forHtmlAttribute(request.getAttribute("numAgencia"))%>" />
            <hl:htmlinput type="hidden" name="numConta"       value="<%=TextHelper.forHtmlAttribute(request.getAttribute("numConta"))%>" />
            
            <hl:htmlinput type="hidden" name="adeCarencia"    value="<%=TextHelper.forHtmlAttribute((adeCarencia))%>" />
            <hl:htmlinput type="hidden" name="ocaPeriodo"     value="<%=ocaPeriodo != null ? TextHelper.forHtmlAttribute(DateHelper.format(ocaPeriodo, "yyyy-MM-dd")) : ""%>" />
            <hl:htmlinput type="hidden" name="rsePrazo"       value="<%=TextHelper.forHtmlAttribute(servidor.getAttribute(Columns.RSE_PRAZO))%>" />
            <hl:htmlinput type="hidden" name="rseMatricula"   value="<%=TextHelper.forHtmlAttribute((servidor.getAttribute(Columns.RSE_MATRICULA)))%>" />
            <hl:htmlinput type="hidden" name="vlrLiberado"    value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "adeVlrLiquido"))%>" />
            <hl:htmlinput type="hidden" name="ranking"        value="" />
            
            <hl:htmlinput type="hidden" name="telaConfirmacaoDuplicidade" value="<%=TextHelper.forHtmlAttribute(request.getParameter("telaConfirmacaoDuplicidade")) %>" />
          	<hl:htmlinput type="hidden" name="chkConfirmarDuplicidade" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "chkConfirmarDuplicidade")) %>"/>
          	<hl:htmlinput type="hidden" name="TMO_CODIGO" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "TMO_CODIGO")) %>" />
          	<hl:htmlinput type="hidden" name="ADE_OBS" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "ADE_OBS")) %>" />
  
            <% if (request.getAttribute("corCodigo") != null) {%>
              <hl:htmlinput type="hidden" name="COR_CODIGO"      value="<%=TextHelper.forHtmlAttribute(request.getAttribute("corCodigo"))%>" />
            <% } %>
  
            <% if (!permiteCadVlrTac && !permiteCadVlrIof && resultadoSimulacaoCSA != null && !temCET) { %>
              <hl:htmlinput type="hidden" name="adeVlrTac"      value="<%=TextHelper.forHtmlAttribute(resultadoSimulacaoCSA.getTextoValorTaxaExtra())%>" />
              <hl:htmlinput type="hidden" name="adeVlrIof"      value="<%=TextHelper.forHtmlAttribute(resultadoSimulacaoCSA.getTextoValorImposto())%>" />
            <% } %>
            <%if(reservaSaudeSemRegras) {%>
                <% if (request.getAttribute("codigoDependente") != null) { %>
                    <hl:htmlinput type="hidden" name="codigoDependente"      value="<%=TextHelper.forHtmlAttribute(request.getAttribute("codigoDependente"))%>" />
                <% } %>
               <% if (request.getAttribute("permiteDescontoViaBoleto") != null) { %>
                    <hl:htmlinput type="hidden" name="permiteDescontoViaBoleto"      value="<%=TextHelper.forHtmlAttribute(request.getAttribute("permiteDescontoViaBoleto"))%>" />
                <% } %>
                <hl:htmlinput type="hidden" name="reservaSaudeSemRegras" value="true" />
            <%} %>
            
            <%if(portalBeneficio && !TextHelper.isNull(corCodigo)){ %>
                <hl:htmlinput type="hidden" name="COR_CODIGO" value="<%=TextHelper.forHtmlAttribute(corCodigo)%>" />
                <hl:htmlinput type="hidden" name="PORTAL_BENEFICIO" value="<%=TextHelper.forHtmlAttribute(portalBeneficio)%>" />
            <%} %>

			<%if (inclusaoJudicial){ %>
            	<hl:htmlinput type="hidden" name="inclusaoJudicial" value="<%=TextHelper.forHtmlAttribute(inclusaoJudicial)%>" />
        	<%} %>            
            <hl:htmlinput type="hidden" name="cienteKYCNaoFinalizado" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "cienteKYCNaoFinalizado"))%>" />
            <INPUT TYPE="hidden" NAME="FILE1" ID="FILE1" VALUE="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "FILE1"))%>"/>
       </form>
</c:set>
<c:set var="javascript">
<%if (inclusaoJudicial){ %>
   <hl:fileUploadV4 scriptOnly="true" tipoArquivo="anexo_consignacao" />
<%} %>
<hl:senhaServidorv4 senhaObrigatoria="<%=(String)(serSenhaObrigatoria ? "true" : "false")%>"                                                 
                  svcCodigo="<%=request.getAttribute("svcCodigo").toString()%>"
                  senhaParaAutorizacaoReserva="true"
                  nomeCampoSenhaCriptografada="serAutorizacao"
                  rseCodigo="<%=request.getAttribute("rseCodigo").toString()%>"
                  nf="btnEnvia"
                  classe="form-control"
                  scriptOnly="true" />

<script type="text/JavaScript">
var f0 = document.forms[0];

function formLoad() {
  focusFirstField();
}

window.onload = formLoad;

function campos() {

  $('#btnEnvia').on('click', function (e) {
      e.preventDefault();
  });
	  
  if (<%=(boolean)serSenhaObrigatoria%> && f0.serLogin != null && f0.serLogin.value == '') {
    f0.serLogin.focus();
    alert('<hl:message key="mensagem.informe.ser.usuario"/>');
    return false;
  }
  if (<%=(boolean)serSenhaObrigatoria%> && f0.senha != null && trim(f0.senha.value) == '') {
    f0.senha.focus();
    alert('<hl:message key="mensagem.informe.ser.senha"/>');
    return false;
  }
  if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_MUNICIPIO_LOTACAO, responsavel)%> && f0.RSE_MUNICIPIO_LOTACAO != null && trim(f0.RSE_MUNICIPIO_LOTACAO.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.municipio.lotacao"/>');
    f0.RSE_MUNICIPIO_LOTACAO.focus();
    return false;
  }
  if ((<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel)%> || (<%=ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel)%> && (<%=celularObrigatorio%> || <%=enderecoCelularObrigatorio%>))) && f0.SER_TEL != null && trim(f0.SER_TEL.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.telefone"/>');
    f0.SER_TEL.focus();
    return false;
  }
  if ((<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_LOGRADOURO, responsavel)%> || (<%=ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_LOGRADOURO, responsavel)%> && (<%=enderecoObrigatorio%> || <%=enderecoCelularObrigatorio%>))) && f0.SER_END != null && trim(f0.SER_END.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.logradouro"/>');
    f0.SER_END.focus();
    return false;  
  }
  if ((<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO, responsavel)%> || (<%=ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO, responsavel)%> && (<%=enderecoObrigatorio%> || <%=enderecoCelularObrigatorio%>))) && f0.SER_NRO != null && trim(f0.SER_NRO.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.numero"/>');
    f0.SER_NRO.focus();
    return false;  
  }
  if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_COMPLEMENTO, responsavel)%> && f0.SER_COMPL != null && trim(f0.SER_COMPL.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.complemento"/>');
    f0.SER_COMPL.focus();
    return false;  
  }
  if ((<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_BAIRRO, responsavel)%> || (<%=ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_BAIRRO, responsavel)%> && (<%=enderecoObrigatorio%> || <%=enderecoCelularObrigatorio%>))) && f0.SER_BAIRRO != null && trim(f0.SER_BAIRRO.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.bairro"/>');
    f0.SER_BAIRRO.focus();
    return false;  
  }
  if ((<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CIDADE, responsavel)%> || (<%=ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CIDADE, responsavel)%> && (<%=enderecoObrigatorio%> || <%=enderecoCelularObrigatorio%>))) && f0.SER_CIDADE != null && trim(f0.SER_CIDADE.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.cidade"/>');
    f0.SER_CIDADE.focus();
    return false;  
  }
  if ((<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CEP, responsavel)%> || (<%=ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CEP, responsavel)%> && (<%=enderecoObrigatorio%> || <%=enderecoCelularObrigatorio%>))) && f0.SER_CEP != null && trim(f0.SER_CEP.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.cep"/>');
    f0.SER_CEP.focus();
    return false;  
  }
  if ((<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_UF, responsavel)%> || (<%=ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_UF, responsavel)%> && (<%=enderecoObrigatorio%> || <%=enderecoCelularObrigatorio%>))) && f0.SER_UF != null && trim(f0.SER_UF.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.estado"/>');
    f0.SER_UF.focus();
    return false;  
  }
  if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_IBAN, responsavel)%> && f0.SER_IBAN != null && trim(f0.SER_IBAN.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.iban"/>');
    f0.SER_IBAN.focus();
    return false;  
  }
  if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_DATA_NASCIMENTO, responsavel)%> && f0.SER_DATA_NASC != null && trim(f0.SER_DATA_NASC.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.data.nascimento"/>');
    f0.SER_DATA_NASC.focus();
    return false;  
  }
  if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_SEXO, responsavel)%> && f0.SER_SEXO != null && trim(f0.SER_SEXO.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.sexo"/>');
    f0.SER_SEXO.focus();
    return false;  
  }
  if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO_IDT, responsavel)%> && f0.SER_NRO_IDT != null && trim(f0.SER_NRO_IDT.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.identidade"/>');
    f0.SER_NRO_IDT.focus();
    return false;  
  }
  if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_DATA_IDT, responsavel)%> && f0.SER_DATA_IDT != null && trim(f0.SER_DATA_IDT.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.data.emissao.identidade"/>');
    f0.SER_DATA_IDT.focus();
    return false;  
  }
  if ((<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CELULAR, responsavel)%> || (<%=ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CELULAR, responsavel)%> && (<%=celularObrigatorio%> || <%=enderecoCelularObrigatorio%>))) && f0.SER_CEL != null && trim(f0.SER_CEL.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.celular"/>');
    f0.SER_CEL.focus();
    return false;  
  }
  if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NACIONALIDADE, responsavel)%> && f0.SER_NACIONALIDADE != null && trim(f0.SER_NACIONALIDADE.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.nacionalidade"/>');
    f0.SER_NACIONALIDADE.focus();
    return false;  
  }
  if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_SALARIO, responsavel)%> && f0.SER_SALARIO != null && trim(f0.SER_SALARIO.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.salario"/>');
    f0.SER_SALARIO.focus();
    return false;  
  }
  if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NATURALIDADE, responsavel)%> && f0.SER_NATURALIDADE != null && trim(f0.SER_NATURALIDADE.value) == '') {
    alert('<hl:message key="mensagem.informe.servidor.naturalidade"/>');
    f0.SER_NATURALIDADE.focus();
    return false;  
  }
  if (<%=ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_UF_NASCIMENTO, responsavel)%> && f0.SER_UF_NASCIMENTO != null && trim(f0.SER_UF_NASCIMENTO.value) == '') {
    alert('<hl:message key="mensagem.informe.uf.nascimento"/>');
    f0.SER_UF_NASCIMENTO.focus();
    return false;  
  } 
  if (f0.senha != null && trim(f0.senha.value) != '') {
    CriptografaSenha(f0.senha, f0.serAutorizacao, false);
  }

  <%if (inclusaoJudicial) {%>
  		if(!validarOpcoesAvancadas()){
  	  		return false;
 	  	}
  <%} %>
  return true;
}
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>