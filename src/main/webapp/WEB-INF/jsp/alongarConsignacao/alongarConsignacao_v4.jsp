<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ page import="com.zetra.econsig.helper.margem.MargemDisponivel" %>
<%@ page import="com.zetra.econsig.helper.margem.ExibeMargem" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.periodo.PeriodoHelper" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page import="com.zetra.econsig.helper.criptografia.JCryptOld"%>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper"%>
<%@ page import="com.zetra.econsig.dto.entidade.ParamSvcTO"%>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
boolean serInfBancariaObrigatoria = (boolean) request.getAttribute("serInfBancariaObrigatoria");
boolean validarInfBancaria = (boolean) request.getAttribute("validarInfBancaria");
boolean validarDataNasc = (boolean) request.getAttribute("validarDataNasc");
boolean mensagemMargemComprometida = (boolean) request.getAttribute("mensagemMargemComprometida");
boolean permiteCadVlrTac = (boolean) request.getAttribute("permiteCadVlrTac");
boolean permiteCadVlrIof = (boolean) request.getAttribute("permiteCadVlrIof");
boolean permiteCadVlrLiqLib = (boolean) request.getAttribute("permiteCadVlrLiqLib");
boolean permiteCadVlrMensVinc = (boolean) request.getAttribute("permiteCadVlrMensVinc");
String mensagem = (String) request.getAttribute("mensagem");
int carenciaMinPermitida = (int) request.getAttribute("carenciaMinPermitida");
int carenciaMaxPermitida = (int) request.getAttribute("carenciaMaxPermitida");
boolean serSenhaObrigatoria = (boolean) request.getAttribute("serSenhaObrigatoria");
boolean anexoInclusaoContratosObrigatorio = (boolean) request.getAttribute("anexoInclusaoContratosObrigatorio");
String serDataNasc = (String) request.getAttribute("serDataNasc");
boolean rseTemInfBancaria = (boolean) request.getAttribute("rseTemInfBancaria");
String numBanco = (String) request.getAttribute("numBanco");
String numAgencia = (String) request.getAttribute("numAgencia");
String numConta1 = (String) request.getAttribute("numConta1");
String numConta2 = (String) request.getAttribute("numConta2");
String numBancoAlt = (String) request.getAttribute("numBancoAlt");
String numAgenciaAlt = (String) request.getAttribute("numAgenciaAlt");
String numContaAlt2 = (String) request.getAttribute("numContaAlt2");
String numContaAlt1 = (String) request.getAttribute("numContaAlt1");
Set<Integer> prazosPossiveisMensal = (Set<Integer>) request.getAttribute("prazosPossiveisMensal");
CustomTransferObject autdes = (CustomTransferObject) request.getAttribute("autdes");
String cnvCodVerbaAlongamento = (String) request.getAttribute("cnvCodVerbaAlongamento");
String svcIdentificadorAlongamento = (String) request.getAttribute("svcIdentificadorAlongamento");
String svcDescricaoAlongamento = (String) request.getAttribute("svcDescricaoAlongamento");
boolean podeMostrarMargem = (boolean) request.getAttribute("podeMostrarMargem");
MargemDisponivel margemDisponivel = (MargemDisponivel) request.getAttribute("margemDisponivel");
BigDecimal margemRestNew = (BigDecimal) request.getAttribute("margemRestNew");
String labelTipoVlr = (String) request.getAttribute("labelTipoVlr");
String adeVlrPadrao = (String) request.getAttribute("adeVlrPadrao");
boolean alteraAdeVlr = (boolean) request.getAttribute("alteraAdeVlr");
boolean permiteEscolherPeriodicidade = (boolean) request.getAttribute("permiteEscolherPeriodicidade");
int maxPrazo = (int) request.getAttribute("maxPrazo");
boolean permiteCadIndice = (boolean) request.getAttribute("permiteCadIndice");
boolean indiceSomenteAutomatico = (boolean) request.getAttribute("indiceSomenteAutomatico");
String vlrIndice = (String) request.getAttribute("vlrIndice");
boolean indiceNumerico = (boolean) request.getAttribute("indiceNumerico");
boolean identificadorAdeObrigatorio = (boolean) request.getAttribute("identificadorAdeObrigatorio");
String mascaraAdeIdentificador = (String) request.getAttribute("mascaraAdeIdentificador");
String cnvCodigoAlongamento = (String) request.getAttribute("cnvCodigoAlongamento");
List<TransferObject> tdaList = (List<TransferObject>) request.getAttribute("tdaList");
String adeCodigo = (String) request.getAttribute("adeCodigo");
boolean anexoObrigatorio = (boolean) request.getAttribute("anexoObrigatorio");
String rseCodigo = (String) request.getAttribute("rseCodigo");
String csaCodigo = (String) request.getAttribute("csaCodigo");
String orgCodigo = (String) request.getAttribute("orgCodigo");
String svcCodigoAlongamento = (String) request.getAttribute("svcCodigoAlongamento");
String vlrLimite = (String) request.getAttribute("vlrLimite");
String perMaxParc = (String) request.getAttribute("perMaxParc");
boolean permitePrazoMaiorContSer = (boolean) request.getAttribute("permitePrazoMaiorContSer");
Set<Integer> prazosPossiveisPeriodicidadeFolha = (Set<Integer>) request.getAttribute("prazosPossiveisPeriodicidadeFolha");

%>
<c:set var="title">
  <hl:message key="rotulo.alongar.consignacao.titulo"/>
</c:set>

<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>

<c:set var="bodyContent">

<form action="<%= TextHelper.forHtmlAttribute(request.getAttribute("acaoFormulario")) %>" method="post" name="form1">
<%= SynchronizerToken.generateHtmlToken(request) %>
<hl:htmlinput type="hidden" name="acao" value="<%= TextHelper.forHtmlAttribute(request.getAttribute("proximaOperacao") != null ? request.getAttribute("proximaOperacao").toString() : "incluirReserva") %>" />
  <div class="row">
    <%-- Utiliza a tag library DetalheConsignacaoTag.java para exibir os dados da ADE --%>
    <% pageContext.setAttribute("autdes", autdes); %>
    <hl:detalharADEv4 name="autdes" table="false" type="alterar"/>
    <%-- Fim dos dados da ADE --%>
    <div class="col-sm-12">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="mensagem.alongar.consignacao.informe.valores"/></h2>
        </div>
        <div class="card-body">
          <hl:htmlinput name="csaNomeAbrev" type="hidden" di="csaNomeAbrev" value="<%=TextHelper.forHtmlAttribute(autdes.getAttribute(Columns.CSA_NOME_ABREV))%>" />
          <hl:htmlinput name="csaNome" type="hidden" di="csaNome" value="<%=TextHelper.forHtmlAttribute(autdes.getAttribute(Columns.CSA_NOME))%>" />
          <hl:htmlinput name="csaIdentificador" type="hidden" di="csaIdentificador" value="<%=TextHelper.forHtmlAttribute(autdes.getAttribute(Columns.CSA_IDENTIFICADOR))%>" />
          <hl:htmlinput name="cnvCodVerba" type="hidden" di="cnvCodVerba" value="<%=TextHelper.forHtmlAttribute(cnvCodVerbaAlongamento)%>" />
          <hl:htmlinput name="svcIdentificador" type="hidden" di="svcIdentificador" value="<%=TextHelper.forHtmlAttribute(svcIdentificadorAlongamento)%>" />
          <hl:htmlinput name="svcDescricao" type="hidden" di="svcDescricao" value="<%=TextHelper.forHtmlAttribute(svcDescricaoAlongamento)%>" />
          <% if (validarDataNasc) { %>
          <div class="row">
            <div class="form-group col-sm-12  col-md-6">
              <label for="dataNasc"><hl:message key="rotulo.servidor.dataNasc"/></label>
              <hl:htmlinput name="dataNasc" type="text" classe="form-control" di="dataNasc" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" value=""/>
            </div>
          </div>    
          <% } %>
          <% if (serInfBancariaObrigatoria) { %>
          <h3 class="legend">
            <span><hl:message key="rotulo.servidor.informacoesbancarias"/></span>
          </h3>
          <div class="row">
            <div class="form-group col-sm-6">  
              <div class="row">
                <div class="col-sm-3">                                
                  <label for="numBanco">
                    <hl:message key="rotulo.servidor.informacoesbancarias.banco.abreviado"/>
                  </label>
                  <hl:htmlinput name="numBanco" 
                                type="text" 
                                classe="form-control" 
                                di="numBanco" 
                                size="3" 
                                mask="#D3"
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.numero.banco", responsavel)%>"
                  />
                </div>
                <div class="col-sm-9">              
                  <label for="numAgencia">
                    <hl:message key="rotulo.servidor.informacoesbancarias.agencia.abreviado"/>
                  </label>
                    <hl:htmlinput name="numAgencia" 
                                  type="text" 
                                  classe="form-control" 
                                  di="numAgencia" 
                                  size="5"
                                  mask="#*30"
                                  placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.numero.agencia", responsavel)%>"/>
                </div> 
              </div>             
            </div>
            <div class="form-group col-sm-3">
              <label for="numConta">
                <hl:message key="rotulo.servidor.informacoesbancarias.conta.abreviado"/>
              </label>
              <hl:htmlinput name="numConta" 
                            type="text" 
                            classe="form-control" 
                            di="numConta"
                            size="12"
                            mask="#*40" 
                            placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.numero.conta", responsavel)%>"/>
            </div>                                
          </div>            
          <% } %>
          <% if (podeMostrarMargem) { %>
          <div class="row">
            <div class="form-group col-sm-12  col-md-6">
              <label for="dataNasc"><hl:message key="rotulo.alongar.consignacao.margem.disponivel"/></label>
              <input id="dataNasc" name="dataNasc" type="text" class="form-control" value="<%=TextHelper.forHtmlContent(ParamSvcTO.getDescricaoTpsTipoVlr(margemDisponivel.getTipoVlr()))%> <%=(String)(margemDisponivel.getExibeMargem().isSemRestricao() ? NumberHelper.format(margemRestNew.doubleValue(), NumberHelper.getLang()) : "0,00")%>" disabled/>
            </div>
          </div>
          <% } %>
          <% if (permiteCadVlrTac) { %>
          <div class="row">
            <div class="form-group col-sm-12  col-md-6">
              <label for="adeVlrTac"><hl:message key="rotulo.consignacao.valor.tac.novo"/> (<hl:message key="rotulo.moeda"/>)</label>
              <hl:htmlinput name="adeVlrTac" type="text" classe="form-control" di="adeVlrTac" size="8" mask="#F11" value="" onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
              placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.nova.tac", responsavel)%>"/>
            </div>
            <div class="form-group col-sm-12  col-md-6">
              <label for="adeVlrTac2"><hl:message key="rotulo.consignacao.valor.tac.atual"/> (<hl:message key="rotulo.moeda"/>)</label>
              <input name="adeVlrTac2" type="text" class="form-control" id="adeVlrTac" size="8" value="<%=(String)((autdes.getAttribute(Columns.ADE_VLR_TAC) != null) ? NumberHelper.format(((BigDecimal)autdes.getAttribute(Columns.ADE_VLR_TAC)).doubleValue(), NumberHelper.getLang()) : "")%>" disabled/>
            </div>
          </div>
          <% } %>
          <% if (permiteCadVlrIof) { %>
            <div class="row">
              <div class="form-group col-sm-12  col-md-6">
                <label for="adeVlrIof"><hl:message key="rotulo.consignacao.valor.iof.novo"/> (<hl:message key="rotulo.moeda"/>)</label>
                <hl:htmlinput name="adeVlrIof" type="text" classe="form-control" di="adeVlrIof" size="8" mask="#F11" value="" onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.novo.iof", responsavel)%>"/>
              </div>
              <div class="form-group col-sm-12  col-md-6">
                <label for="adeVlrIof2"><hl:message key="rotulo.consignacao.valor.iof.atual"/> (<hl:message key="rotulo.moeda"/>)</label>
                <input name="adeVlrIof2" type="text" class="form-control" id="adeVlrIof2" size="8" value="<%=(String)((autdes.getAttribute(Columns.ADE_VLR_IOF) != null) ? NumberHelper.format(((BigDecimal)autdes.getAttribute(Columns.ADE_VLR_IOF)).doubleValue(), NumberHelper.getLang()) : "")%>" disabled/>
              </div>
            </div>
          <% } %>
          <% if (permiteCadVlrLiqLib) { %>
            <div class="row">
              <div class="form-group col-sm-12  col-md-6">
                <label for="adeVlrLiquido"><hl:message key="rotulo.consignacao.valor.liquido.liberado.novo"/> (<hl:message key="rotulo.moeda"/>)</label>
                <hl:htmlinput name="adeVlrLiquido" type="text" classe="form-control" di="adeVlrLiquido" size="8" mask="#F11" value="" onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.novo.valor.liquido", responsavel)%>"/> 
              </div>
              <div class="form-group col-sm-12  col-md-6">
                <label for="adeVlrLiquido2"><hl:message key="rotulo.consignacao.valor.liquido.liberado.atual"/> (<hl:message key="rotulo.moeda"/>)</label>
                <input name="adeVlrLiquido2" type="text" class="form-control" id="adeVlrLiquido2" size="8" value="<%=(String)((autdes.getAttribute(Columns.ADE_VLR_LIQUIDO) != null) ? NumberHelper.format(((BigDecimal)autdes.getAttribute(Columns.ADE_VLR_LIQUIDO)).doubleValue(), NumberHelper.getLang()) : "")%>" disabled/>
              </div>
            </div>
          <% } %>
          <% if (permiteCadVlrMensVinc) { %>
            <div class="row">
              <div class="form-group col-sm-12  col-md-6">
                <label for="adeVlrMensVinc"><hl:message key="rotulo.consignacao.valor.mensalidade.vinc.novo"/> (<hl:message key="rotulo.moeda"/>)</label>
                <hl:htmlinput name="adeVlrMensVinc" type="text" classe="form-control" di="adeVlrMensVinc" size="8" mask="#F11" value="" onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.novo.valor.mens.vinc", responsavel)%>"/> 
              </div>
              <div class="form-group col-sm-12  col-md-6">
                <label for="adeVlrMensVinc2"><hl:message key="rotulo.consignacao.valor.mensalidade.vinc.atual"/> (<hl:message key="rotulo.moeda"/>)</label>
                <input name="adeVlrMensVinc2" type="text" class="form-control" id="adeVlrMensVinc2" size="8" value="<%=(String)((autdes.getAttribute(Columns.ADE_VLR_MENS_VINC) != null) ? NumberHelper.format(((BigDecimal)autdes.getAttribute(Columns.ADE_VLR_MENS_VINC)).doubleValue(), NumberHelper.getLang()) : "")%>" disabled/>
              </div>
            </div>
          <% } %>
          <div class="row">
            <div class="form-group col-sm-12  col-md-6">
              <label for="adeVlr"><hl:message key="rotulo.consignacao.valor.parcela.novo"/> (<%=TextHelper.forHtmlContent(labelTipoVlr)%>)</label>
              <hl:htmlinput name="adeVlr" type="text" classe="form-control" di="adeVlr" size="8" mask="#F11" value="<%=TextHelper.forHtmlAttribute(adeVlrPadrao.replace('.',','))%>" others="<%=TextHelper.forHtmlAttribute(!alteraAdeVlr ? "disabled" : "")%>" onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
              placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("rotulo.consultar.margem.valor.parcela.placeholder", responsavel)%>"/> 
            </div>
            <div class="form-group col-sm-12  col-md-6">
              <label for="adeVlr2"><hl:message key="rotulo.consignacao.valor.parcela.atual"/> (<%=TextHelper.forHtmlContent(labelTipoVlr)%>)</label>
              <input name="adeVlr2" type="text" class="form-control" id="adeVlr2" size="8" value="<%=NumberHelper.format(((BigDecimal)autdes.getAttribute(Columns.ADE_VLR)).doubleValue(), NumberHelper.getLang())%>" disabled/>
            </div>
          </div>
          <% String rotuloPeriodicidadePrazo = " (" + ApplicationResourcesHelper.getMessage("rotulo.meses", responsavel) + ")"; %>
          <% if (permiteEscolherPeriodicidade && !PeriodoHelper.folhaMensal(responsavel)) { %>
          <% rotuloPeriodicidadePrazo = ""; %>
            <div class="row">
              <div class="form-group col-sm-6">
                <label for="adePeriodicidade"><hl:message key="rotulo.consignacao.periodicidade"/></label>
                <select class="form-control form-select" onFocus="SetarEventoMascaraV4(this,'#*200',true);"
                        onBlur="fout(this);ValidaMascaraV4(this);"
                        onChange="mudaPeriodicidade()"
                        name="adePeriodicidade" id="adePeriodicidade">
                  <option value=""><hl:message key="rotulo.campo.selecione"/></option>
                  <option value="<%=(String)CodedValues.PERIODICIDADE_FOLHA_QUINZENAL%>" selected><hl:message key="rotulo.consignacao.periodicidade.quinzenal"/></option>
                  <option value="<%=(String)CodedValues.PERIODICIDADE_FOLHA_MENSAL%>"><hl:message key="rotulo.consignacao.periodicidade.mensal"/></option>
                </select>
              </div>
            </div>
           <% } else { %>
                <hl:htmlinput type="hidden" name="adePeriodicidade" di="adePeriodicidade" value="<%=TextHelper.forHtmlAttribute(PeriodoHelper.getPeriodicidadeFolha(responsavel))%>" />
           <% } %>
           <div class="row">
             <div class="form-group col-sm-6">
               <label for="adePrazo"><hl:message key="rotulo.consignacao.prazo.novo"/><%=TextHelper.forHtmlContent(rotuloPeriodicidadePrazo)%></label>
               <%if (prazosPossiveisMensal == null || prazosPossiveisMensal.isEmpty()) { %> 
                  <hl:htmlinput name="adePrazo" type="text" classe="form-control" di="adePrazo"  mask="#D4" onBlur="verificaPrazo();"
                                others="<%=TextHelper.forHtmlAttribute((maxPrazo==0)? "disabled" : "")%>"
                                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.novo.prazo", responsavel)%>"/> 
              <% } else { %>
                  <select class="form-control form-select" name="adePrazo" id="adePrazo">
                    <option value="" selected><hl:message key="rotulo.campo.selecione"/></option>
                    <%for (Integer prazoVlr : prazosPossiveisMensal) { %>
                       <option value="<%=prazoVlr%>"><%=prazoVlr%></option>
                    <%} %>
                  </select> 
              <% } %>
             </div>
            <div class="form-group col-sm-12  col-md-6">
              <label for="adePrazo2"><hl:message key="rotulo.consignacao.prazo.atual"/> <%=TextHelper.forHtmlContent(rotuloPeriodicidadePrazo)%></label>
              <input name="adePrazo2" type="text" class="form-control" id="adePrazo2" size="3" value="<%=(autdes.getAttribute(Columns.ADE_PRAZO) == null) ? ApplicationResourcesHelper.getMessage("rotulo.indeterminado", responsavel) : autdes.getAttribute(Columns.ADE_PRAZO).toString()%>" disabled/>
            </div>
           </div>
           <% if (ShowFieldHelper.showField(FieldKeysConstants.ALONGAR_CONSIGNACAO_CARENCIA, responsavel)) { %>
           <div class="row">
            <div class="form-group col-sm-12  col-md-6">
              <label for="adeCarencia"><hl:message key="rotulo.consignacao.carencia.nova"/><%=TextHelper.forHtmlContent(rotuloPeriodicidadePrazo)%></label>
              <hl:htmlinput name="adeCarencia" type="text" classe="form-control" di="adeCarencia" size="10" mask="#D2" value="<%=TextHelper.forHtmlAttribute(String.valueOf(carenciaMinPermitida))%>" onBlur="verificaCarencia()" others="<%=TextHelper.forHtmlAttribute((carenciaMinPermitida==carenciaMaxPermitida) ? "disabled" : "" )%>"
              placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.carencia", responsavel)%>"/> 
            </div>
          </div>
          <% } else { %>
            <hl:htmlinput name="adeCarencia" di="adeCarencia" type="hidden" value="<%=String.valueOf(carenciaMinPermitida) != null ? TextHelper.forHtmlAttribute(String.valueOf(carenciaMinPermitida)) : "0" %>"/>
          <% } %>
            
            <%
            if (permiteCadIndice && !indiceSomenteAutomatico) {
              boolean vlrIndiceDisabled = false;
              if (vlrIndice != null) {
                if (!vlrIndice.trim().equals("")) {
                  vlrIndiceDisabled = true;
                } else {
                  vlrIndiceDisabled = false;
                }
              }
            %>
            <div class="row">
              <div class="form-group col-sm-12  col-md-6">
                <label for="adeIndice"><hl:message key="rotulo.consignacao.indice.novo"/> <hl:message key="rotulo.campo.opcional"/></label>
                <hl:htmlinput name="adeIndice" type="text" classe="form-control" di="adeIndice" value="<%=TextHelper.forHtmlAttribute(vlrIndice)%>" others="<%=TextHelper.forHtmlAttribute(vlrIndiceDisabled ? "disabled" : "")%>" size="10" mask="<%=(String)(indiceNumerico ? "#D2" : "#A2")%>"
                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("rotulo.reajuste.indice.placeholder", responsavel)%>"/>
              </div>
              <div class="form-group col-sm-12  col-md-6">
                <label for="adeIndice2"><hl:message key="rotulo.consignacao.indice.atual"/></label>
                <input name="adeIndice2" type="text" class="form-control" id="adeIndice" size="8" value="<%=(String)((autdes.getAttribute(Columns.ADE_INDICE) != null) ? autdes.getAttribute(Columns.ADE_INDICE).toString() : "")%>" disabled/>
              </div>
            </div>
            <% } %>
            <div class="row">
              <div class="form-group col-sm-12  col-md-6">
                <label for="adeIdentificador"><hl:message key="rotulo.consignacao.identificador.novo"/> <% if (!identificadorAdeObrigatorio) { %>&nbsp;<hl:message key="rotulo.campo.opcional"/><% } %></label>
                <hl:htmlinput name="adeIdentificador" type="text" classe="form-control" di="adeIdentificador" size="10" mask="<%=TextHelper.forHtmlAttribute(TextHelper.isNull(mascaraAdeIdentificador) ? "#*20":mascaraAdeIdentificador )%>"
                placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.novo.ade.identificador", responsavel)%>"/>
              </div>
              <div class="form-group col-sm-12  col-md-6">
                <label for="adeIdentificador2"><hl:message key="rotulo.consignacao.identificador.atual"/></label>
                <input name="adeIdentificador2" type="text" class="form-control" id="adeIdentificador2" size="8" value="<%=TextHelper.forHtmlContent(autdes.getAttribute(Columns.ADE_IDENTIFICADOR))%>" disabled/>
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm-12  col-md-6">
                <label for="novoSvc"><hl:message key="rotulo.servico.novo"/></label>
                <input name="novoSvc" type="text" class="form-control" id="novoSvc" size="8" value="<%=TextHelper.forHtmlContent(svcDescricaoAlongamento)%>" disabled/>
                <hl:htmlinput name="CNV_CODIGO" type="hidden" di="CNV_CODIGO" value="<%=TextHelper.forHtmlAttribute(cnvCodigoAlongamento)%>"/>
              </div>
            </div>
            <% 
              for(TransferObject tda : tdaList){
            %>
            <hl:paramv4 
                    prefixo="TDA_"
                    descricao="<%=TextHelper.forHtmlAttribute(tda.getAttribute(Columns.TDA_DESCRICAO))%>"
                    codigo="<%=(String) tda.getAttribute(Columns.TDA_CODIGO)%>"
                    dominio="<%=(String) tda.getAttribute(Columns.TDA_DOMINIO)%>"
                    valor=""
                    valorOriginal="<%=(String) tda.getAttribute("VALOR_ORIGINAL")%>"
                    desabilitado="<%= CodedValues.CAS_BLOQUEADO.equals(tda.getAttribute(Columns.SPT_EXIBE)) %>"
            />
           <%}%>
          <%-- Anexo de arquivo --%>
          <% if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_ANEXO_INCLUSAO_CONTRATOS, CodedValues.TPC_SIM, responsavel) &&
                       responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO)) {
                 %>
                <div class="row">
                  <div class="col-sm-12 col-md-6">
                    <hl:fileUploadV4 obrigatorio="<%=true%>" tipoArquivo="anexo_consignacao"/>
                  </div>
                </div>
          <% } %>               
          <%
             String mascaraLogin = (String) ParamSist.getInstance().getParam(CodedValues.TPC_MASCARA_LOGIN_EXTERNO_SERVIDOR, responsavel);
             if (!TextHelper.isNull(mascaraLogin)) {
          %>
          <div class="row">
            <div class="form-group col-sm-12  col-md-6">
              <label for="serLogin"><hl:message key="rotulo.usuario.autorizacao.servidor.singular"/><%=serSenhaObrigatoria ? "" : " " + ApplicationResourcesHelper.getMessage("rotulo.campo.opcional", responsavel)%><hl:message key="rotulo.campo.opcional"/></label>
              <hl:htmlinput name="serLogin" type="text" classe="form-control" di="serLogin" size="15" mask="<%=TextHelper.forHtmlAttribute(mascaraLogin)%>" />
            </div>            
          </div>
          <% } %>
          <div class="row">
            <div class="col-sm-12 col-md-6 form-group">
              <hl:senhaServidorv4 senhaObrigatoria="<%=(String)(serSenhaObrigatoria ? "true" : "false")%>"                                 
                                senhaParaAutorizacaoReserva="true"
                                nomeCampoSenhaCriptografada="serAutorizacao"
                                rseCodigo="<%=rseCodigo%>"
                                classe="form-control"
                                comTagDD="false"
                                nf="submit" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <hl:htmlinput name="ADE_CODIGO"     type="hidden" di="ADE_CODIGO"     value="<%=TextHelper.forHtmlAttribute(adeCodigo)%>" />
  <hl:htmlinput name="CSA_CODIGO"      type="hidden" di="CSA_CODIGO"      value="<%=TextHelper.forHtmlAttribute(csaCodigo)%>" />
  <hl:htmlinput name="ORG_CODIGO"      type="hidden" di="ORG_CODIGO"      value="<%=TextHelper.forHtmlAttribute(orgCodigo)%>" />  
  <hl:htmlinput name="RSE_CODIGO"      type="hidden" di="RSE_CODIGO"      value="<%=TextHelper.forHtmlAttribute(rseCodigo)%>" />
  <hl:htmlinput name="rseMatricula"   type="hidden" di="rseMatricula"   value="<%=TextHelper.forHtmlAttribute(autdes.getAttribute(Columns.RSE_MATRICULA))%>" />
  <hl:htmlinput name="rsePrazo"       type="hidden" di="rsePrazo"       value="<%=TextHelper.forHtmlAttribute(autdes.getAttribute(Columns.RSE_PRAZO))%>" />
  <hl:htmlinput name="SVC_CODIGO"      type="hidden" di="SVC_CODIGO"      value="<%=TextHelper.forHtmlAttribute(svcCodigoAlongamento)%>" />
  <hl:htmlinput name="tipo"           type="hidden" di="tipo"           value="alongar" />
  <hl:htmlinput name="vlrLimite"      type="hidden" di="vlrLimite"      value="<%=TextHelper.forHtmlAttribute(vlrLimite)%>" />
  <hl:htmlinput name="adeVlrOld"      type="hidden" di="adeVlrOld"      value="<%=TextHelper.forHtmlAttribute(autdes.getAttribute(Columns.ADE_VLR))%>" />
  <hl:htmlinput name="perMaxParc"     type="hidden" di="perMaxParc"     value="<%=TextHelper.forHtmlAttribute(perMaxParc)%>" />
  
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')" alt="<hl:message key="rotulo.botao.voltar"/>" title="<hl:message key="rotulo.botao.voltar"/>"><hl:message key="rotulo.botao.voltar"/></a>
    <a class="btn btn-primary" id="submit" href="#no-back" onClick="if(campos() && verificaAdeVlr()) {alongaContrato();} return false;" alt="<hl:message key="rotulo.botao.confirmar"/>" title="<hl:message key="rotulo.botao.confirmar"/>"><svg width="17"><use xlink:href="#i-confirmar"></use></svg><hl:message key="rotulo.botao.confirmar"/></a>
  </div>
</form>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript" src="../js/javacrypt.js?<hl:message key="release.tag"/>"></script>
<hl:fileUploadV4 obrigatorio="<%=true%>" tipoArquivo="anexo_consignacao" scriptOnly="true"/>
<hl:senhaServidorv4 senhaObrigatoria="<%=(String)(serSenhaObrigatoria ? "true" : "false")%>"                                 
                  senhaParaAutorizacaoReserva="true"
                  nomeCampoSenhaCriptografada="serAutorizacao"
                  rseCodigo="<%=rseCodigo%>"
                  classe="form-control"
                  comTagDD="false"
                  nf="submit"
                  scriptOnly="true" />

<script type="text/JavaScript">

var valor = 'IB';
var validarInfBancaria = <%=TextHelper.forJavaScriptBlock(serInfBancariaObrigatoria && validarInfBancaria)%>;
var validarDataNasc = <%=TextHelper.forJavaScriptBlock(validarDataNasc)%>;

f0 = document.forms[0];

function formLoad() {
  setaPrazo();
  focusFirstField();
  mudaPeriodicidade();
}

window.onload = formLoad;

function setaPrazo() {
  if (f0.adeSemPrazo != null) {
    if (maxPrazo == 0) {       // somente prazo indeterminado
      f0.adeSemPrazo.checked = true;
      f0.adeSemPrazo.disabled = true;
    } else if (maxPrazo > 0) { // somente prazo determinado menor que maxPrazo
      f0.adeSemPrazo.checked = false;
      f0.adeSemPrazo.disabled = true;
    } else {                   // qualquer prazo
      f0.adeSemPrazo.disabled = false;
    }
    f0.adePrazo.disabled = f0.adeSemPrazo.checked;
  }
  if (f0.adePrazo.disabled) {
    f0.adePrazo.value = '';
  } else {
    f0.adePrazo.focus();
  }
}

function alongaContrato() {
  if (rsePrazo != '' && (parseInt(f0.adePrazo.value) > parseInt(rsePrazo)) && !permitePrazoMaiorContSer) {
    alert ('<hl:message key="mensagem.erro.prazo.maior.ser"/>'.replace("{0}", rsePrazo));
    f0.adePrazo.focus();
    //return false;
  } else {
      if (<%=(boolean)mensagemMargemComprometida%>) {
         alert('<hl:message key="mensagem.alerta.margem.comprometida"/>');
      }
      if (<%=(String)((permiteCadVlrTac || permiteCadVlrIof || permiteCadVlrLiqLib || permiteCadVlrMensVinc) ? "verificaCadInfFin() && " : "")%> vf_reservar_margem() &&
          verificaDataNasc() <%=(String)(serInfBancariaObrigatoria ? " && verificaInfBanco()" : "")%> &&
         (confirm('<%=TextHelper.forJavaScriptBlock(mensagem)%><hl:message key="mensagem.confirmacao.alongamento"/>'))) {
          enableAll();
         f0.submit();
      }
  }
}

function verificaAdeVlr() {
  var adeVlr = f0.adeVlr.value;
  var adeVlrOld = f0.adeVlrOld.value;
  var perMaxParc = f0.perMaxParc.value;

  if (parseFloat(adeVlr) > parseFloat(adeVlrOld)) {
    alert('<hl:message key="mensagem.erro.valor.parcela.maior.atual"/>');
    f0.adeVlr.focus();
    return false;
  }

  if (parseFloat(adeVlr.replace(',', '.')) > (parseFloat(adeVlrOld.replace(',', '.')) * parseFloat(perMaxParc.replace(',', '.')))) {
    alert('<hl:message key="mensagem.erro.valor.parcela.maior.percent.atual" arg0="<%=NumberHelper.format((new BigDecimal(perMaxParc.replaceAll(",", "."))).multiply(new BigDecimal("100.00")).doubleValue(), NumberHelper.getLang())%>"/>');
    f0.adeVlr.focus();
    return false;
  }
  return true;
}

function verificaCadInfFin() {
  var Controles;
  var Msgs;

  Controles = new Array("adeVlrTac", "adeVlrIof", "adeVlrLiquido", "adeVlrMensVinc");

  Msgs = new Array('<hl:message key="mensagem.informe.ade.valor.tac"/>',
               '<hl:message key="mensagem.informe.ade.valor.iof"/>',
               '<hl:message key="mensagem.informe.ade.valor.liberado"/>',
               '<hl:message key="mensagem.informe.ade.valor.mensalidade"/>');

  return ValidaCampos(Controles, Msgs);
}

function verificaCarencia() {
  var mensagem = "";
  var carencia = parseInt(f0.adeCarencia.value);
  var carenciaMinPermitida = parseInt(<%=(int)carenciaMinPermitida%>);
  var carenciaMaxPermitida = parseInt(<%=(int)carenciaMaxPermitida%>);

  if ((carencia < carenciaMinPermitida) || (carencia > carenciaMaxPermitida)) {
    if (carenciaMaxPermitida > carenciaMinPermitida) {
      mensagem = '<hl:message key="mensagem.erro.carencia.entre.min.max" arg0="<%=TextHelper.forHtmlAttribute(String.valueOf(carenciaMinPermitida))%>" arg1="<%=TextHelper.forHtmlAttribute(String.valueOf(carenciaMaxPermitida))%>"/>';
    } else if (carenciaMaxPermitida < carenciaMinPermitida) {
      mensagem = '<hl:message key="mensagem.erro.carencia.menor.max" arg0="<%=TextHelper.forHtmlAttribute(String.valueOf(carenciaMaxPermitida))%>"/>';
    } else if (carenciaMaxPermitida == carenciaMinPermitida) {
      mensagem = '<hl:message key="mensagem.erro.carencia.fixa" arg0="<%=TextHelper.forHtmlAttribute(String.valueOf(carenciaMinPermitida))%>"/>';
    }

    alert(mensagem);
    f0.adeCarencia.focus();
    return false;
  } else {
    return true;
  }
}

function campos() {
  if (<%=(boolean)serSenhaObrigatoria%> && f0.serLogin != null && f0.serLogin.value == '') {
    alert('<hl:message key="mensagem.informe.ser.usuario"/>');
    f0.serLogin.focus();
    return false;
  }
  if (<%=(boolean)serSenhaObrigatoria%> && f0.senha != null && trim(f0.senha.value) == '') {
    alert('<hl:message key="mensagem.informe.ser.senha"/>');
    f0.senha.focus();
    return false;
  }

  if (f0.senha != null && f0.senha.value != '') {
    f0.serAutorizacao.value = criptografaRSA(f0.senha.value);
    f0.senha.value = '';
  }
  
  if (!verificaAnexo()) {
    return false;
  }
  
  return true;
}

function verificaAnexo() {
  <% if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_ANEXO_INCLUSAO_CONTRATOS, CodedValues.TPC_SIM, responsavel) &&
              responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO) && anexoInclusaoContratosObrigatorio) { %>
    if (document.getElementById('FILE1').value == '') {
          alert('<hl:message key="mensagem.informe.arquivo.upload"/>');
          return false;
    }
  <% } %>
    return true;
}

function verificaDataNasc() {
 if (validarDataNasc) {
   var dataNascBase = '<%=TextHelper.forJavaScriptBlock(JCryptOld.crypt("IB", TextHelper.isNull(serDataNasc) ? "vazio" : serDataNasc.replaceAll("/", "")))%>';
   var dataNasc = f0.dataNasc.value;
   if (dataNasc == '') { 
     alert('<hl:message key="mensagem.dataNascNaoInformada"/>');
     f0.dataNasc.focus();
     return false; 
   } else {
     dataNasc = dataNasc.replace(/\//g, '');
     dataNasc = Javacrypt.crypt(valor, dataNasc)[0];
     if (dataNasc != dataNascBase) {
       alert('<hl:message key="mensagem.dataNascNaoConfere"/>');
       f0.dataNasc.focus();
       return false;
     }
   }
 }
 return true;
}

function verificaInfBanco() {
  var Controles = new Array("numBanco", "numAgencia", "numConta");
  var Msgs = new Array('<hl:message key="mensagem.informacaoBancariaObrigatoria"/>',
                       '<hl:message key="mensagem.informacaoBancariaObrigatoria"/>',
                       '<hl:message key="mensagem.informacaoBancariaObrigatoria"/>');

  var banco = Javacrypt.crypt(valor, formataParaComparacao(f0.numBanco.value))[0];
  var agencia = Javacrypt.crypt(valor, formataParaComparacao(f0.numAgencia.value))[0];

  var conta = formataParaComparacao(f0.numConta.value);
  var pos = 0;
  var letra = conta.substr(pos, 1);
  while (letra == 0 && pos < conta.length) {
    pos++;
    letra = conta.substr(pos, 1)  ;
  }

  conta = conta.substr(pos,conta.length);
  var conta1 = Javacrypt.crypt(valor, conta.substr(0, conta.length/2))[0];
  var conta2 = Javacrypt.crypt(valor, conta.substr(conta.length/2, conta.length))[0];

  if (ValidaCampos(Controles, Msgs)) {
  <%if (!rseTemInfBancaria) { /* servidor não tem informações bancárias cadastradas */ %>
      return true;
  <%} else {%>
    if (((banco != '<%=TextHelper.forJavaScriptBlock(numBanco)%>') || (agencia != '<%=TextHelper.forJavaScriptBlock(numAgencia)%>') || (conta1 != '<%=TextHelper.forJavaScriptBlock(numConta1)%>') || (conta2 != '<%=TextHelper.forJavaScriptBlock(numConta2)%>')) &&
        ((banco != '<%=TextHelper.forJavaScriptBlock(numBancoAlt)%>') || (agencia != '<%=TextHelper.forJavaScriptBlock(numAgenciaAlt)%>') || (conta1 != '<%=TextHelper.forJavaScriptBlock(numContaAlt1)%>') || (conta2 != '<%=TextHelper.forJavaScriptBlock(numContaAlt2)%>'))) {
      if (validarInfBancaria) {
        alert('<hl:message key="mensagem.informacaoBancariaIncorreta"/>');
        return false;
      }
      if(confirm('<hl:message key="mensagem.informacaoBancariaIncorreta.continuar"/>')) {
        return true;
      } else {
        f0.numBanco.focus();
        return false;
      }
    }
    return true;
  <%}%>    
  }
  return false;
}

function mudaPeriodicidade() {
<% if (!PeriodoHelper.folhaMensal(responsavel)) { %>       
  <% if (prazosPossiveisMensal != null && !prazosPossiveisMensal.isEmpty()) { %>
  if (f0.adePeriodicidade != undefined && f0.adePeriodicidade != null) {
    if (f0.adePrazo) {
      f0.adePrazo.selectedIndex = "0";
      if (f0.adePeriodicidade.value != 'M') {
        loadSelectOptions(f0.adePrazo, arPrazosPeriodicidadeFolha, '');
      } else {
        loadSelectOptions(f0.adePrazo, arPrazosMensal, '');
      }
    }
  }
  <% } %>
<% } %>
}
</script>
<script type="text/JavaScript">
maxPrazo = '<%=TextHelper.forJavaScriptBlock(maxPrazo)%>';
permitePrazoMaiorContSer = <%=TextHelper.forJavaScriptBlock(permitePrazoMaiorContSer)%>;
rsePrazo = '<%=(String)(autdes.getAttribute(Columns.RSE_PRAZO) != null ? autdes.getAttribute(Columns.RSE_PRAZO).toString() : "")%>';
identificadorObrigatorio = <%=TextHelper.forJavaScriptBlock(identificadorAdeObrigatorio)%>;
<%=(prazosPossiveisMensal != null && !prazosPossiveisMensal.isEmpty() ? "var arPrazosMensal = [" + TextHelper.join(prazosPossiveisMensal, ", ") + "];" : "")%>
<%=(prazosPossiveisPeriodicidadeFolha != null && !prazosPossiveisPeriodicidadeFolha.isEmpty() ? "var arPrazosPeriodicidadeFolha = [" + TextHelper.join(prazosPossiveisPeriodicidadeFolha, ", ") + "];" : "")%>
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>