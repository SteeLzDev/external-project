<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="java.util.*"%>
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
<%@ page import="com.zetra.econsig.helper.criptografia.JCryptOld"%>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper"%>
<%@ page import="com.zetra.econsig.dto.web.RenegociarConsignacaoModel"%>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="t"  tagdir="/WEB-INF/tags" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
  AcessoSistema responsavel                  = JspHelper.getAcessoSistema(request);
  RenegociarConsignacaoModel renegociarModel = (RenegociarConsignacaoModel) request.getAttribute("renegociarModel");
  boolean compra                             = renegociarModel.isCompra();
  String tipo                                = compra ? "comprar" : "renegociar"; 
  //Parametro que indica se a senha do servidor foi informada e validada
  boolean senhaServidorOK = (session.getAttribute("senhaServidorRenegOK") != null && session.getAttribute("senhaServidorRenegOK").equals(renegociarModel.getRseCodigo()));
  TransferObject autdes   = renegociarModel.getAutdes();
  boolean temCET          = renegociarModel.isTemCET();
  String formAction       = compra ? "../v3/comprarConsignacao" : "../v3/renegociarConsignacao";
  String qtdeMinAnexos = (String) request.getAttribute("qtdeMinAnexos");
%>
<c:set var="title">
  <hl:message key="<%=(String)(compra ? "rotulo.comprar.consignacao.titulo" : "rotulo.renegociar.consignacao.titulo")%>"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <% if (renegociarModel.isPodeMostrarDatasRenegociacao()) { %>
    <div class="alert alert-warning" role="alert"><p class="mb-0"><hl:message key="mensagem.renegociar.consignacao.alerta.datasRenegociacao"/></p></div>
  <% } %>
  <div class="page-title">
    <div class="row d-print-none">
       <div class="col-sm-12 col-md-12 mb-2">
          <div class="float-end">
            <button data-bs-toggle="dropdown" aria-haspopup="true" id="acoes" aria-expanded="false" class="btn btn-primary" type="submit"><hl:message key="rotulo.mais.acoes"/></button>
            <div class="dropdown-menu dropdown-menu-right" aria-labelledby="acoes" x-placement="bottom-end"
                style="position: absolute; transform: translate3d(1097px, 50px, 0px); top: 0px; left: 0px; will-change: transform;">                
                <% if (renegociarModel.isPodeMostrarMargem() && renegociarModel.isPmtCompMargem()) { %>
                   <% if (!renegociarModel.isExigeSenhaServidor() || (renegociarModel.isExigeSenhaServidor() && senhaServidorOK)) { %>
                        <a class="dropdown-item" href="#no-back" onclick="postData('../v3/consultarComposicaoMargem?acao=<%=TextHelper.forJavaScriptAttribute(tipo)%>&RSE_CODIGO=<%=TextHelper.forJavaScript(renegociarModel.getRseCodigo())%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');"><hl:message key="rotulo.composicao.margem.acao"/></a>              
                   <% } else { %>
                        <a class="dropdown-item" href="#no-back" onclick="postData('../v3/consultarComposicaoMargem?acao=iniciar&RSE_MATRICULA=<%=(String)(autdes.getAttribute(Columns.RSE_MATRICULA))%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>','pesquisar')"><hl:message key="rotulo.composicao.margem.acao"/></a>
              
                   <% } %>
                <% } %>
                <% if (renegociarModel.isPossuiVariacaoMargem() && renegociarModel.isExibeAlgumaMargem()) { %>
                   <% if (!renegociarModel.isExigeSenhaServidor() || (renegociarModel.isExigeSenhaServidor() && senhaServidorOK)) { %>
                       <a class="dropdown-item" href="#no-back" onclick="postData('../v3/consultarVariacaoMargem?acao=<%=TextHelper.forJavaScriptAttribute(tipo)%>&RSE_CODIGO=<%=TextHelper.forJavaScript(renegociarModel.getRseCodigo())%>&_skip_history_=true&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>');"><hl:message key="rotulo.variacao.margem.acao"/></a>                
                   <% } else { %>
                       <a class="dropdown-item" href="#no-back" onclick="postData('../v3/consultarVariacaoMargem?acao=iniciar&RSE_MATRICULA=<%=(String)(autdes.getAttribute(Columns.RSE_MATRICULA))%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.variacao.margem.acao"/></a>              
                   <% } %>
                <% } %>
                <% if (renegociarModel.isSistExibeHistLiqAntecipadas() && renegociarModel.getNumAdeHistLiqAntecipadas() > 0) { %>   
                     <a class="dropdown-item" href="#no-back" onclick="openModalSubAcesso('<%=(compra) ? "../v3/comprarConsignacao" : "../v3/renegociarConsignacao"%>?RSE_MATRICULA=<%=(String)(autdes.getAttribute(Columns.RSE_MATRICULA) )%>&RSE_CODIGO=<%=(String)(autdes.getAttribute(Columns.RSE_CODIGO))%>&SVC_CODIGO=<%=TextHelper.forJavaScript(renegociarModel.getSvcCodigo())%>&SER_NOME=<%=TextHelper.forJavaScript(TextHelper.encode64(renegociarModel.getSerNome()))%>&acao=listarHistLiquidacoesAntecipadas&tipo=<%=TextHelper.forJavaScript((compra?"compra":"renegociar"))%>&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>')"><hl:message key="rotulo.historico.liq.antecipada.acao"/></a>                     
                <% } %>
            </div>
          </div>
       </div>
    </div>
  </div>
 <div class="row">
  <div>
    <%-- Utiliza a tag library DetalheConsignacaoTag.java para exibir os dados da ADE --%>
    <% pageContext.setAttribute("autdes", renegociarModel.getAutdesList()); %>
    <hl:detalharADEv4 name="autdes" table="false" type="alterar" />
    <%-- Fim dos dados da ADE --%>
   </div>
  </div>
  <div class="row">
     <div class="col-sm-12 col-md-12 mb-2">
        <div class="card">
          <div class="card-header">
             <h2 class="card-header-title"><hl:message key="mensagem.renegociar.consignacao.operacao.valores"/></h2>
          </div>
          <div class="card-body">
            <form action="<%=formAction + "?" + SynchronizerToken.generateToken4URL(request)%>" method="post" name="form1">
               <hl:htmlinput name="csaNomeAbrev"     type="hidden" di="csaNomeAbrev" value="<%=TextHelper.forHtmlAttribute(renegociarModel.getCsaNomeAbrev())%>" />
               <hl:htmlinput name="csaNome"          type="hidden" di="csaNome"          value="<%=TextHelper.forHtmlAttribute(renegociarModel.getCsaNome())%>" />
               <hl:htmlinput name="csaIdentificador" type="hidden" di="csaIdentificador" value="<%=TextHelper.forHtmlAttribute(renegociarModel.getCsaIdentificador())%>" />              
               <hl:htmlinput name="cnvCodVerba"      type="hidden" di="cnvCodVerba"      value="<%=TextHelper.forHtmlAttribute(renegociarModel.getCnvCodVerba())%>" />
               <hl:htmlinput name="svcIdentificador" type="hidden" di="svcIdentificador" value="<%=TextHelper.forHtmlAttribute(renegociarModel.getSvcIdentifcador())%>" />
               <hl:htmlinput name="svcDescricao"     type="hidden" di="svcDescricao"     value="<%=TextHelper.forHtmlAttribute(renegociarModel.getSvcDescricao())%>" />
               
               <div class="row">
                 <div class="form-group col-sm-6">
                   <label for="provedorServico"><hl:message key="rotulo.consignataria.singular"/></label>
                   <input type="text" class="form-control" id="provedorServico" name="provedorServico" value="<%=TextHelper.forHtmlAttribute(renegociarModel.getCsaNome())%>" disabled="">
                 </div>
                 <div class="form-group col-sm-6">
                   <label for="servicoProduto"><hl:message key="rotulo.servico.singular"/></label>
                   <input type="text" class="form-control" id="servicoProduto" name="servicoProduto" value="<%=TextHelper.forHtmlAttribute(renegociarModel.getServico())%>" disabled="">
                 </div>
              </div>
              <% if (renegociarModel.isPodeMostrarDatasRenegociacao()) { %>
              <div class="row">
                <div class="form-group col-sm-6">
                  <label for="dtEnceramentoContrato"><hl:message key="rotulo.renegociar.consignacao.data.encerra.contrato.anterior"/></label>
                  <input type="text" id='dtEnceramentoContrato' class="form-control" value="<%=TextHelper.forHtmlAttribute(DateHelper.toPeriodString((java.util.Date)renegociarModel.getOcaPeriodoRenegociacao())) %>" disabled="disabled">
                </div>
                <div class="form-group col-sm-6">
                  <label for="dtInicioContrato"><hl:message key="rotulo.renegociar.consignacao.data.inicia.contrato.novo"/></label>
                   <input type="text" id='dtInicioContrato' class="form-control" value="<%=TextHelper.forHtmlAttribute(DateHelper.toPeriodString((java.util.Date)renegociarModel.getAnoMesIniNovaAde())) %>" disabled="disabled">
                </div>
              </div>
              <div class="row">
                <div class="form-group col-sm-6">
                  <span class="text-nowrap align-text-top">
                    <input class="form-check-input ml-1" type="checkbox" name="alterarDataEncerramento" id="alterarDataEncerramento" value="S" <%= renegociarModel.isPadraoAlterarDataEncerramento() ? "checked" : "" %>>
                    <label class="form-check-label labelSemNegirto ml-1" aria-label='<hl:message key="rotulo.renegociar.consignacao.manter.data.encerramento.igual.inclusao"/>' for="alterarDataEncerramento"><hl:message key="rotulo.renegociar.consignacao.manter.data.encerramento.igual.inclusao"/></label>
                  </span>
                </div>
              </div>
              <% } %>
              <% if (renegociarModel.isPodeMostrarMargem()) { %>
                 <% if (!renegociarModel.isTpcExigeSenha() || (renegociarModel.isTpcExigeSenha() && senhaServidorOK)) { %>
                    <div class="row">
                      <div class="form-group col-sm-6">
                       <label for="rseMargemRestDisplay"><hl:message key="rotulo.renegociar.consignacao.margem.disponivel"/></label>
                       <input type="text" class="form-control" id="rseMargemRestDisplay" name="rseMargemRestDisplay" value="<%=TextHelper.forHtmlAttribute(renegociarModel.getDescricaoTipoVlrMargem())%>&nbsp;<%=NumberHelper.format(renegociarModel.getVlrMargemRestNew().doubleValue(), NumberHelper.getLang())%>" disabled="">
                       <hl:htmlinput name="rseMargemRest" type="hidden" di="rseMargemRest" value="<%=TextHelper.forHtmlAttribute((renegociarModel.getVlrMargemRestNew()))%>" />
                      </div>
                    </div>
                 <% } %>
              <% } %>
              <div class="legend"></div>
              <% if (renegociarModel.isValidarDataNasc()) { %>
                 <div class="row">
                  <div class="form-group col-sm-6">
                    <label for="dataNasc"><hl:message key="rotulo.servidor.dataNasc"/></label>
                    <hl:htmlinput name="dataNasc" type="text" classe="form-control" di="dataNasc" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" value="" placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.nova.data.nascimento", responsavel)%>"/>
                  </div> 
                 </div> 
              <% } %>
              <%if (renegociarModel.isSerInfBancariaObrigatoria()) { %>
                 <div class="legend">
                   <span><hl:message key="rotulo.servidor.informacoesbancarias"/></span>
                 </div>
                <div class="row">
                  <div class="form-group col-sm-2">
                    <label for="numBanco"><hl:message key="rotulo.servidor.informacoesbancarias.banco.abreviado"/></label>
                    <hl:htmlinput name="numBanco" type="text" classe="form-control" di="numBanco" size="3" mask="#D3" placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("rotulo.servidor.informacoesbancarias.banco", responsavel)%>"/>
                  </div>
                  <div class="form-group col-sm-4">
                    <label for="numAgencia"><hl:message key="rotulo.servidor.informacoesbancarias.agencia.abreviado"/></label>
                    <hl:htmlinput name="numAgencia" type="text" classe="form-control" di="numAgencia" size="5" mask="#*30" placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("rotulo.servidor.informacoesbancarias.agencia", responsavel)%>"/>
                  </div>
                  <div class="form-group col-sm-6">
                    <label for="numConta"><hl:message key="rotulo.servidor.informacoesbancarias.conta.abreviado"/></label>
                    <hl:htmlinput name="numConta" type="text" classe="form-control" di="numConta" size="12" mask="#*40" placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("rotulo.servidor.informacoesbancarias.conta", responsavel)%>"/>
                  </div>
                </div>
              <%} %>
              <div class="legend">
                <span><hl:message key="rotulo.dados.prestacao"/></span>
              </div>
              <div class="row">
                  <div class="form-group col-sm-6 mb-1">
                    <label for="adeVlr"><hl:message key="rotulo.consignacao.valor.parcela.novo"/>&nbsp;(<%=TextHelper.forHtmlContent(renegociarModel.getLabelTipoVlr())%>)</label>                    
                    <hl:htmlinput name="adeVlr" type="text" classe="form-control" di="adeVlr"  
                                  mask="#F11" value="<%=TextHelper.forHtmlAttribute(renegociarModel.getAdeVlrPadrao().replace('.',','))%>" 
                                  placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.novo.valor.parcela", responsavel)%>"
                                  others="<%=TextHelper.forHtmlAttribute(!renegociarModel.isAlteraAdeVlr() ? "disabled" : "")%>" 
                                  onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" />
                  </div>                  
              </div>
              <div class="row">
                    <div class="col-sm-6">
                      <div class="alert alert-info pl-3 pr-3 pt-2 pb-2 text-center" role="alert">
                        <p class="mb-0"><hl:message key="rotulo.consignacao.valor.parcela.atual"/>&nbsp;(<%=TextHelper.forHtmlContent(renegociarModel.getLabelTipoVlr())%>): <%=NumberHelper.format(renegociarModel.getValorTotal().doubleValue(), NumberHelper.getLang())%></p>
                      </div>
                    </div>
              </div>
              <% String rotuloPeriodicidadePrazo = renegociarModel.getRotuloPeriodicidadePrazo(); %>
              <% if (renegociarModel.isPermiteEscolherPeriodicidade() && !PeriodoHelper.folhaMensal(responsavel)) { %>
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
               <div class="form-group col-sm-6  mb-1">
                 <label for="adePrazo"><hl:message key="rotulo.consignacao.prazo.novo"/><%=TextHelper.forHtmlContent(rotuloPeriodicidadePrazo)%></label>
                 <%if (renegociarModel.getPrazosPossiveisMensal() == null || renegociarModel.getPrazosPossiveisMensal().isEmpty()) { %> 
                    <hl:htmlinput name="adePrazo" type="text" classe="form-control" di="adePrazo"  mask="#D4" onBlur="verificaPrazo();"
                                  others="<%=TextHelper.forHtmlAttribute((renegociarModel.getMaxPrazo()==0)? "disabled" : "")%>"
                                  placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.novo.prazo", responsavel)%>"/> 
                <% } else { %>
                    <select class="form-control form-select" name="adePrazo" id="adePrazo">
                      <option value="" selected><hl:message key="rotulo.campo.selecione"/></option>
                      <%for (Integer prazoVlr : renegociarModel.getPrazosPossiveisMensal()) { %>
                         <option value="<%=prazoVlr%>"><%=prazoVlr%></option>
                      <%} %>
                    </select> 
                <% } %>
               </div>
             </div>
             <%if (renegociarModel.isExibeVlrAtual()) { %> 
               <div class="row">
                  <div class="col-sm-6">
                    <div class="alert alert-info pl-3 pr-3 pt-2 pb-2 text-center" role="alert">
                      <p class="mb-0"><hl:message key="rotulo.consignacao.prazo.atual"/>: <%=(autdes.getAttribute(Columns.ADE_PRAZO) == null) ? ApplicationResourcesHelper.getMessage("rotulo.indeterminado", responsavel) : autdes.getAttribute(Columns.ADE_PRAZO).toString()%></p>
                    </div>
                  </div>
               </div>
             <%} %>
             <% if (renegociarModel.getMaxPrazo() <= 0 && (renegociarModel.getPrazosPossiveisMensal() == null || renegociarModel.getPrazosPossiveisMensal().isEmpty())) { %>
                 <div class="row">
                   <div class="form-group col-sm-6">
                     <label for="adeSemPrazo"><hl:message key="rotulo.consignacao.prazo.indeterminado"/></label>
                     <hl:htmlinput name="adeSemPrazo" type="checkbox" classe="form-control" di="adeSemPrazo" mask="#*200" onClick="setaPrazo();" others="<%=TextHelper.forHtmlAttribute((renegociarModel.getMaxPrazo()==0)?"CHECKED":"")%>" />
                   </div> 
                 </div>
             <% } %>
             <div class="legend">
                <span><hl:message key="rotulo.dados.adicionais"/></span>
             </div>
             <% if (ShowFieldHelper.showField(FieldKeysConstants.RENEGOCIACAO_PORTABILIDADE_CARENCIA, responsavel) && !renegociarModel.isPodeMostrarDatasRenegociacao()) { %>
               <div class="row">
                 <div class="form-group col-sm-6">
                   <label for="adeCarencia"><hl:message key="rotulo.consignacao.carencia.nova"/><%=TextHelper.forHtmlContent(rotuloPeriodicidadePrazo)%></label>
                   <hl:htmlinput name="adeCarencia" type="text" classe="form-control" di="adeCarencia"  mask="#D2" 
                                 value="<%=TextHelper.forHtmlAttribute(String.valueOf(renegociarModel.getCarenciaMinPermitida()))%>"
                                 onBlur="verificaCarencia()" others="<%=TextHelper.forHtmlAttribute((renegociarModel.getCarenciaMinPermitida() == renegociarModel.getCarenciaMaxPermitida()) ? "disabled" : "" )%>"
                                 placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.carencia", responsavel)%>"/>
                 </div>
               </div>
             <% } else { %>
               <hl:htmlinput name="adeCarencia" di="adeCarencia" type="hidden" value="<%=TextHelper.forHtmlAttribute(String.valueOf(renegociarModel.getCarenciaMinPermitida()))%>"/>
            <% } %>
             <% if (renegociarModel.isPermiteCadVlrTac()) { %>
                 <div class="row">
                   <div class="form-group col-sm-6">
                    <label for="adeVlrTac"><hl:message key="rotulo.consignacao.valor.tac.novo"/>&nbsp;(<hl:message key="rotulo.moeda"/>)</label>
                    <hl:htmlinput name="adeVlrTac" type="text" classe="form-control" di="adeVlrTac"  mask="#F11" value="" onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.nova.tac", responsavel)%>"/>
                   </div>
                 </div>
             <% } %>
             <% if (renegociarModel.isPermiteCadVlrTac() && renegociarModel.isExibeVlrAtual()) { %>
                 <div class="row">
                  <div class="col-sm-6">
                    <div class="alert alert-info pl-3 pr-3 pt-2 pb-2 text-center" role="alert">
                      <p class="mb-0"><hl:message key="rotulo.consignacao.valor.tac.atual"/>&nbsp;(<hl:message key="rotulo.moeda"/>): <%=TextHelper.forHtmlContent((autdes.getAttribute(Columns.ADE_VLR_TAC) != null && !autdes.getAttribute(Columns.ADE_VLR_TAC).equals("")) ? NumberHelper.format((new BigDecimal(autdes.getAttribute(Columns.ADE_VLR_TAC).toString())).doubleValue(), NumberHelper.getLang()) : "")%></p>
                    </div>
                  </div>
                 </div> 
             <% } %>
             <% if (renegociarModel.isPermiteCadVlrIof()) { %>
                 <div class="row">
                   <div class="form-group col-sm-6">
                    <label for="adeVlrIof"><hl:message key="rotulo.consignacao.valor.iof.novo"/>&nbsp;(<hl:message key="rotulo.moeda"/>)</label>
                    <hl:htmlinput name="adeVlrIof" type="text" classe="form-control" di="adeVlrIof"  mask="#F11" value="" onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" 
                                  placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.novo.iof", responsavel)%>"/>
                   </div>
                 </div>
             <% } %>
             <% if (renegociarModel.isPermiteCadVlrIof() && renegociarModel.isExibeVlrAtual()) { %>
                 <div class="row">
                  <div class="col-sm-6">
                    <div class="alert alert-info pl-3 pr-3 pt-2 pb-2 text-center" role="alert">
                      <p class="mb-0"><hl:message key="rotulo.consignacao.valor.iof.atual"/>&nbsp;(<hl:message key="rotulo.moeda"/>): <%=TextHelper.forHtmlContent((autdes.getAttribute(Columns.ADE_VLR_IOF) != null && !autdes.getAttribute(Columns.ADE_VLR_IOF).equals("")) ? NumberHelper.format((new BigDecimal(autdes.getAttribute(Columns.ADE_VLR_IOF).toString())).doubleValue(), NumberHelper.getLang()) : "")%></p>
                    </div>
                  </div>
                 </div> 
             <% } %>
             <%if (renegociarModel.isPermiteCadVlrLiqLib()) { %>
                <div class="row">
                  <div class="form-group col-sm-6  mb-1">
                    <label for="adeVlrLiquido"><hl:message key="rotulo.consignacao.valor.liquido.liberado.novo"/>&nbsp;(<hl:message key="rotulo.moeda"/>)</label>
                    <hl:htmlinput name="adeVlrLiquido" type="text" classe="form-control" di="adeVlrLiquido"  mask="#F11" value=""
                                  onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.novo.valor.liquido", responsavel)%>"/>
                  </div>
                </div>
             <%} %>
             <%if (renegociarModel.isPermiteCadVlrLiqLib() && renegociarModel.isExibeVlrAtual()) { %>
                <div class="row">
                  <div class="col-sm-6">
                    <div class="alert alert-info pl-3 pr-3 pt-2 pb-2 text-center" role="alert">
                      <p class="mb-0"><hl:message key="rotulo.consignacao.valor.liquido.liberado.atual"/>&nbsp;(<hl:message key="rotulo.moeda"/>):&nbsp;<%=TextHelper.forHtmlContent((autdes.getAttribute(Columns.ADE_VLR_LIQUIDO) != null && !autdes.getAttribute(Columns.ADE_VLR_LIQUIDO).equals("")) ? NumberHelper.format((new BigDecimal(autdes.getAttribute(Columns.ADE_VLR_LIQUIDO).toString())).doubleValue(), NumberHelper.getLang()) : "")%></p>
                    </div>
                  </div>
                </div>
             <%} %>
             <%if (renegociarModel.isPermiteCadVlrMensVinc()) { %>
                <div class="row">
                  <div class="form-group col-sm-6  mb-1">
                    <label for="adeVlrMensVinc"><hl:message key="rotulo.consignacao.valor.mensalidade.vinc.novo"/>&nbsp;(<hl:message key="rotulo.moeda"/>)</label>
                    <hl:htmlinput name="adeVlrMensVinc" type="text" classe="form-control" di="adeVlrMensVinc" mask="#F11" value=""
                                  onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                                  placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.novo.valor.mens.vinc", responsavel)%>"/>
                  </div>
                </div>
             <%} %>
             <%if (renegociarModel.isPermiteCadVlrMensVinc() && renegociarModel.isExibeVlrAtual()) { %>
                <div class="row">
                  <div class="col-sm-6">
                    <div class="alert alert-info pl-3 pr-3 pt-2 pb-2 text-center" role="alert">
                      <p class="mb-0"><hl:message key="rotulo.consignacao.valor.mensalidade.vinc.atual"/>&nbsp;(<hl:message key="rotulo.moeda"/>):>&nbsp;<%=TextHelper.forHtmlContent((autdes.getAttribute(Columns.ADE_VLR_MENS_VINC) != null && !autdes.getAttribute(Columns.ADE_VLR_MENS_VINC).equals("")) ? NumberHelper.format((new BigDecimal(autdes.getAttribute(Columns.ADE_VLR_MENS_VINC).toString())).doubleValue(), NumberHelper.getLang()) : "")%></p>
                    </div>
                  </div>
                </div>
             <%} %>
             <%if (renegociarModel.isSeguroPrestamista()) { %>
                <div class="row">
                  <div class="form-group col-sm-6  mb-1">
                    <label for="adeVlrSegPrestamista"><hl:message key="rotulo.consignacao.seguro.prestamista.novo"/>&nbsp;(<hl:message key="rotulo.moeda"/>)</label>
                    <hl:htmlinput name="adeVlrSegPrestamista" type="text" classe="form-control" di="segPrestamista" mask="#F11" onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }"
                                  placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.novo.seguro.prestamista", responsavel)%>"/>
                  </div>
                </div>
             <%} %>
             <%if (renegociarModel.isSeguroPrestamista() && renegociarModel.isExibeVlrAtual()) { %>
                <div class="row">
                  <div class="col-sm-6">
                    <div class="alert alert-info pl-3 pr-3 pt-2 pb-2 text-center" role="alert">
                      <p class="mb-0"><hl:message key="rotulo.consignacao.seguro.prestamista.atual"/>&nbsp;(<hl:message key="rotulo.moeda"/>):&nbsp;<%=TextHelper.forHtmlContent((autdes.getAttribute(Columns.ADE_VLR_SEG_PRESTAMISTA) != null && !autdes.getAttribute(Columns.ADE_VLR_SEG_PRESTAMISTA).equals("")) ? NumberHelper.format((new BigDecimal(autdes.getAttribute(Columns.ADE_VLR_SEG_PRESTAMISTA).toString())).doubleValue(), NumberHelper.getLang()) : "")%></p>
                    </div>
                  </div>
                </div>
             <%} %>
             <%if (renegociarModel.isPermiteVlrLiqTxJuros()) { %>
             <div class="row">
                  <div class="form-group col-sm-6  mb-1">
                    <label for="adeTaxaJuros"><hl:message key="<%=(String)(temCET ? "rotulo.consignacao.cet.novo" : "rotulo.consignacao.taxa.juros.nova")%>"/></label>
                    <hl:htmlinput name="adeTaxaJuros" type="text" classe="form-control" di="adeTaxaJuros"  mask="#F11" value=""
                                  onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); }" 
                                  placeHolder="<%=(String)ApplicationResourcesHelper.getMessage((String)(temCET ? "mensagem.placeholder.digite.novo.cet" : "mensagem.placeholder.digite.nova.taxa.juros"), responsavel)%>"/>
                  </div>
                </div>
                <%if (renegociarModel.isExibeVlrAtual()) { %>
                   <div class="row">
                     <div class="col-sm-6">
                       <div class="alert alert-info pl-3 pr-3 pt-2 pb-2 text-center" role="alert">
                         <p class="mb-0"><hl:message key="<%=(String)(temCET ? "rotulo.consignacao.cet.atual" : "rotulo.consignacao.taxa.juros.atual")%>"/>:&nbsp;<%=TextHelper.forHtmlContent((autdes.getAttribute(Columns.ADE_TAXA_JUROS) != null && !autdes.getAttribute(Columns.ADE_TAXA_JUROS).equals("")) ? NumberHelper.reformat(autdes.getAttribute(Columns.ADE_TAXA_JUROS).toString(), "en", NumberHelper.getLang()) : "")%></p>
                       </div>
                     </div>
                   </div>
                <%} %>   
              <%} %>
              <%if (renegociarModel.isPermiteCadIndice() && !renegociarModel.isIndiceSomenteAutomatico()) { 
                  if (!renegociarModel.isGeraComboIndice()) { %>
                     <div class="row">
                       <div class="form-group col-sm-6  mb-1">
                         <label for="adeIndice"><hl:message key="rotulo.consignacao.indice.novo"/>&nbsp;<hl:message key="rotulo.campo.opcional"/></label>
                         <hl:htmlinput name="adeIndice" type="text" classe="form-control" di="adeIndice" value="<%=TextHelper.forHtmlAttribute(renegociarModel.getVlrIndice())%>" others="<%=TextHelper.forHtmlAttribute(renegociarModel.isVlrIndiceDisabled() ? "disabled" : "")%>"  mask="<%=renegociarModel.getMascaraIndice()%>" 
                                       placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.novo.indice", responsavel)%>"/>
                       </div>
                     </div>
                     <%if (renegociarModel.isExibeVlrAtual()) { %>
                        <div class="row">
                          <div class="col-sm-6">
                            <div class="alert alert-info pl-3 pr-3 pt-2 pb-2 text-center" role="alert">
                              <p class="mb-0"><hl:message key="rotulo.consignacao.indice.atual"/>:&nbsp;<%=TextHelper.forHtmlContent((autdes.getAttribute(Columns.ADE_INDICE) != null) ? autdes.getAttribute(Columns.ADE_INDICE).toString() : "")%></p>
                            </div>
                          </div>
                        </div>
                     <%} %>    
              <%  } else { %>
                    <div class="row">
                      <div class="form-group col-sm-6  mb-1">
                        <label for="novoCET"><hl:message key="rotulo.consignacao.indice.novo"/>&nbsp;<hl:message key="rotulo.campo.opcional"/></label>
                        <select class="form-control form-select" id="adeIndice" class="form-control"
                          onFocus="SetarEventoMascaraV4(this,'#*200',true);" 
                          onBlur="fout(this);ValidaMascaraV4(this);">
                          <option value="" selected><hl:message key="rotulo.campo.selecione"/></option>
                          <%
                             CustomTransferObject next = null;
                             String ind_descricao = null;
                             String ind_codigo = null;
                             Iterator<TransferObject> it = renegociarModel.getIndices().iterator();
                             while (it.hasNext()) {
                               next = (CustomTransferObject) it.next();
                               ind_codigo = next.getAttribute(Columns.IND_CODIGO).toString();
                               ind_descricao = " - " + next.getAttribute(Columns.IND_DESCRICAO).toString();
                               out.print("<option value=\"" + ind_codigo + "\">" + ind_codigo + ind_descricao + "</option>");
                             }
                         %>
                        </select>
                      </div>
                    </div> 
              <%  }
                } %>
                <div class="row">
                  <div class="form-group col-sm-6 mb-1">
                    <label for="adeIdentificador"><hl:message key="rotulo.consignacao.identificador.novo"/><% if (!renegociarModel.isIdentificadorAdeObrigatorio()) { %>&nbsp;<hl:message key="rotulo.campo.opcional"/><% } %></label>
                    <hl:htmlinput name="adeIdentificador" type="text" classe="form-control" di="adeIdentificador" mask="<%=TextHelper.forHtmlAttribute(TextHelper.isNull(renegociarModel.getMascaraAdeIdentificador()) ? "#*40":renegociarModel.getMascaraAdeIdentificador())%>" placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.novo.ade.identificador", responsavel)%>"/>
                  </div>
                </div>
                <%if (renegociarModel.isExibeVlrAtual()) { %>
                  <div class="row">
                    <div class="col-sm-6">
                      <div class="alert alert-info pl-3 pr-3 pt-2 pb-2 text-center" role="alert">
                        <p class="mb-0"><hl:message key="rotulo.consignacao.identificador.atual"/>:&nbsp;<%=TextHelper.forHtmlContent(autdes.getAttribute(Columns.ADE_IDENTIFICADOR))%></p>
                      </div>
                    </div>
                  </div>
                <%} %>
                <%if (responsavel.isCsaCor() && renegociarModel.isExigeModalidadeOperacao()) { %>
                   <div class="row">
                     <div class="form-group col-sm-6 mb-1">
                       <label for="tdaModalidadeOp"><hl:message key="rotulo.consignacao.modalidade.operacao"/></label>
                       <hl:htmlinput name="tdaModalidadeOp" type="text" classe="form-control" di="tdaModalidadeOp" mask="#*6" 
                                     placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.modalidade.operacao", responsavel)%>"/>
                     </div>
                   </div>
                <%} %>
                <% for(TransferObject tda: renegociarModel.getTdaList()){%>
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
                <%if (responsavel.isCsaCor() && renegociarModel.isExigeMatriculaSerCsa()) { %>
                   <div class="row">
                     <div class="form-group col-sm-6 mb-1">
                       <label for="tdaMatriculaCsa"><hl:message key="rotulo.consignacao.matricula.ser.csa"/></label>
                       <hl:htmlinput name="tdaMatriculaCsa" type="text" classe="form-control" di="tdaMatriculaCsa" mask="#*20" 
                                     placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.informacao.matricula.ser.csa", responsavel)%>"/>
                     </div>
                   </div>
                <%} %>
                <%if (renegociarModel.isExigeEmailServidor()) { %>
                   <div class="row">
                     <div class="form-group col-sm-6 mb-1">
                       <label for="serEmail"><hl:message key="rotulo.renegociar.consignacao.ser.email"/></label>
                       <hl:htmlinput name="serEmail" type="text" classe="form-control" di="email" mask="#*100" 
                                     placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.email.servidor", responsavel)%>"/>
                     </div>
                   </div>
                <%} %>
                <%if (!TextHelper.isNull(renegociarModel.getMascaraLogin())) {%>
                   <div class="row">
                     <div class="form-group col-sm-6 mb-1">
                       <label for="serLogin"><hl:message key="rotulo.usuario.autorizacao.servidor.singular"/><%=renegociarModel.isSerSenhaObrigatoria() ? "" : " " + ApplicationResourcesHelper.getMessage("rotulo.campo.opcional", responsavel)%></label>
                       <hl:htmlinput name="serLogin" type="text" classe="form-control" di="serLogin" mask="<%=TextHelper.forHtmlAttribute(renegociarModel.getMascaraLogin())%>"
                                     placeHolder="<%=(String)ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.digite.ser.login", responsavel)%>"/>
                     </div>
                   </div>
                <%} %>
                   <div class="row">
                     <div class="form-group col-sm-6">   
                        <hl:senhaServidorv4 senhaObrigatoria="<%=(String)(renegociarModel.isSerSenhaObrigatoria() ? "true" : "false")%>"                               
                              svcCodigo="<%=TextHelper.forHtmlAttribute(renegociarModel.getSvcCodigo())%>"
                              senhaParaAutorizacaoReserva="true"
                              classe="form-control"
                              nomeCampoSenhaCriptografada="serAutorizacao"
                              inputSizeCSS="form-group col-sm-6 mb-1"
                              rseCodigo="<%=renegociarModel.getRseCodigo() %>"
                              nf="submit"
                              comTagDD="false"
                              separador2pontos="false" />
                     </div>       
                   </div>         
                <% if (compra && (ParamSist.paramEquals(CodedValues.TPC_INFORMA_NUM_PORTABILIDADE_CIP_COMPRA, CodedValues.NUM_PORTABILIDADE_CIP_COMPRA_OPCIONAL, responsavel) ||
                            ParamSist.paramEquals(CodedValues.TPC_INFORMA_NUM_PORTABILIDADE_CIP_COMPRA, CodedValues.NUM_PORTABILIDADE_CIP_COMPRA_OBRIGATORIO, responsavel))) {
                      boolean campoObrigatorio = ParamSist.paramEquals(CodedValues.TPC_INFORMA_NUM_PORTABILIDADE_CIP_COMPRA, CodedValues.NUM_PORTABILIDADE_CIP_COMPRA_OBRIGATORIO, responsavel); %>
                      <div class="row">
                        <div class="form-group col-sm-6 mb-1">
                         <label for="numCipCompra"><hl:message key="rotulo.usuario.autorizacao.servidor.singular"/><%=renegociarModel.isSerSenhaObrigatoria() ? "" : " " + ApplicationResourcesHelper.getMessage("rotulo.campo.opcional", responsavel)%></label>
                         <hl:htmlinput name="numCipCompra" di="numCipCompra" type="text" classe="form-control" mask="#*D" />
                        </div>
                      </div>
                <% } %>
                <%-- Anexo de arquivo de documentação adicional compra --%>
                 <% if (compra && (ParamSist.paramEquals(CodedValues.TPC_INFORMA_ANEXO_ADE_DOC_ADICIONAL_COMPRA, CodedValues.ANEXO_ADE_DOC_ADICIONAL_COMPRA_OPCIONAL, responsavel) || 
                        ParamSist.paramEquals(CodedValues.TPC_INFORMA_ANEXO_ADE_DOC_ADICIONAL_COMPRA, CodedValues.ANEXO_ADE_DOC_ADICIONAL_COMPRA_OBRIGATORIO, responsavel)) &&
                        responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO)) {
                        boolean anexoObrigatorio = ParamSist.paramEquals(CodedValues.TPC_INFORMA_ANEXO_ADE_DOC_ADICIONAL_COMPRA, CodedValues.ANEXO_ADE_DOC_ADICIONAL_COMPRA_OBRIGATORIO, responsavel); %>
                       <hl:fileUploadV4 
                           nomeCampoArquivo="FILE_DOC_ADICIONAL_COMPRA" 
                           tituloCampoArquivo="<%= ApplicationResourcesHelper.getMessage("rotulo.anexo.compra.doc.adicional", responsavel) %>" 
                           obrigatorio="<%=(boolean)(anexoObrigatorio)%>" 
                           mostraCampoDescricao="false"
                           divClassArquivo="form-group col-sm-6 mb-1"
                           tipoArquivo="anexo_consignacao"
                           multiplo="false" 
                        />                   
                <% } %>
                <%-- Anexo de arquivo --%>
                <% if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_ANEXO_INCLUSAO_CONTRATOS, CodedValues.TPC_SIM, responsavel) &&
                       responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO)) {
                %>      
                        <hl:fileUploadV4 obrigatorio="<%=renegociarModel.isAnexoObrigatorio()%>" tipoArquivo="anexo_consignacao" divClassArquivo="form-group col-sm-6 mb-1"/>
                      
                <% } %>
                <hl:htmlinput name="chkADE"         type="hidden" di="chkADE"         value="<%=TextHelper.forHtmlAttribute(TextHelper.join(renegociarModel.getAdesReneg(), ","))%>" /> 
                <hl:htmlinput name="CNV_CODIGO"     type="hidden" di="CNV_CODIGO"     value="<%=TextHelper.forHtmlAttribute(renegociarModel.getCnvCodigo())%>" />
                <hl:htmlinput name="CSA_CODIGO"     type="hidden" di="CSA_CODIGO"     value="<%=TextHelper.forHtmlAttribute(renegociarModel.getCsaCodigo())%>" /> 
                <hl:htmlinput name="ORG_CODIGO"     type="hidden" di="ORG_CODIGO"     value="<%=TextHelper.forHtmlAttribute(renegociarModel.getOrgCodigo())%>" />
                <hl:htmlinput name="RSE_CODIGO"     type="hidden" di="RSE_CODIGO"     value="<%=TextHelper.forHtmlAttribute(renegociarModel.getRseCodigo())%>" /> 
                <hl:htmlinput name="RSE_MATRICULA"  type="hidden" di="RSE_MATRICULA"  value="<%=TextHelper.forHtmlAttribute((autdes.getAttribute(Columns.RSE_MATRICULA)))%>" /> 
                <hl:htmlinput name="RSE_PRAZO"      type="hidden" di="RSE_PRAZO"      value="<%=TextHelper.forHtmlAttribute(autdes.getAttribute(Columns.RSE_PRAZO))%>" /> 
                <hl:htmlinput name="SVC_CODIGO"     type="hidden" di="SVC_CODIGO"     value="<%=TextHelper.forHtmlAttribute(renegociarModel.getSvcCodigo())%>" /> 
                <hl:htmlinput name="acao"           type="hidden" di="acao"           value="incluirReserva" /> 
                <hl:htmlinput name="vlrLimite"      type="hidden" di="vlrLimite"      value="<%=TextHelper.forHtmlAttribute(renegociarModel.getVlrLimite())%>" /> 
                <% if (!renegociarModel.isPermiteCadVlrTac() && !renegociarModel.isPermiteCadVlrIof()) { %>
                  <hl:htmlinput type="hidden" name="adeVlrTac" value="0,00" /> 
                  <hl:htmlinput type="hidden" name="adeVlrIof" value="0,00" />
                <% } %>
                <% if (renegociarModel.isPossuiControleVlrMaxDesconto()) { %>
                  <hl:htmlinput type="hidden" name="vlrMaxParcelaSaldoDevedor" value="<%=TextHelper.forHtmlAttribute((renegociarModel.getVlrMaxParcelaSaldoDevedor()))%>" />
                <% } %>                 
            </form>
          </div>
        </div>
     </div>
  </div>
  <div class="btn-action">
        <a class="btn btn-outline-danger" HREF="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')"><hl:message key="rotulo.botao.cancelar"/></a>
        <a class="btn btn-primary" HREF="#no-back" onClick="if(campos()){renegociaContrato(); return false;} else {return false;}">
          <svg width="17">
              <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-confirmar"></use>
          </svg>
          <hl:message key="rotulo.botao.confirmar"/>
        </a>
  </div>
</c:set>
<c:set var="javascript">
<script language="JavaScript" type="text/JavaScript" src="../js/javacrypt.js?<hl:message key="release.tag"/>"></script>
<hl:fileUploadV4 scriptOnly="true" tipoArquivo="anexo_consignacao" />
<hl:fileUploadV4 nomeCampoArquivo="FILE_DOC_ADICIONAL_COMPRA" scriptOnly="true" tipoArquivo="anexo_consignacao" multiplo="false"/>
<hl:senhaServidorv4 senhaObrigatoria="<%=(String)(renegociarModel.isSerSenhaObrigatoria() ? "true" : "false")%>"                               
      svcCodigo="<%=TextHelper.forHtmlAttribute(renegociarModel.getSvcCodigo())%>"
      senhaParaAutorizacaoReserva="true"
      classe="form-control"
      nomeCampoSenhaCriptografada="serAutorizacao"
      inputSizeCSS="form-group col-sm-6 mb-1"
      rseCodigo="<%=renegociarModel.getRseCodigo() %>"
      nf="submit"
      comTagDD="false"
      separador2pontos="false"
      scriptOnly="true" />

<script language="JavaScript" type="text/JavaScript">
f0 = document.forms[0];
var valor = 'IB';
var validarInfBancaria   = <%=TextHelper.forJavaScriptBlock(renegociarModel.isSerInfBancariaObrigatoria() && renegociarModel.isValidarInfBancaria())%>;
var validarDataNasc      = <%=TextHelper.forJavaScriptBlock(renegociarModel.isValidarDataNasc())%>;
maxPrazo                 = <%=TextHelper.forJavaScriptBlock(renegociarModel.getMaxPrazo())%>;
rsePrazo                 = '<%=TextHelper.forJavaScriptBlock(autdes.getAttribute(Columns.RSE_PRAZO) != null ? autdes.getAttribute(Columns.RSE_PRAZO).toString() : "")%>';
permitePrazoMaiorContSer = <%=TextHelper.forJavaScriptBlock(renegociarModel.isPermitePrazoMaiorContSer())%>;
identificadorObrigatorio = <%=TextHelper.forJavaScriptBlock(renegociarModel.isIdentificadorAdeObrigatorio())%>;

<%=(renegociarModel.getPrazosPossiveisMensal() != null && !renegociarModel.getPrazosPossiveisMensal().isEmpty() ? "var arPrazosMensal = [" + TextHelper.join(renegociarModel.getPrazosPossiveisMensal(), ", ") + "];" : "")%>
<%=(renegociarModel.getprazosPossiveisPeriodicidadeFolha() != null && !renegociarModel.getprazosPossiveisPeriodicidadeFolha().isEmpty() ? "var arPrazosPeriodicidadeFolha = [" + TextHelper.join(renegociarModel.getprazosPossiveisPeriodicidadeFolha(), ", ") + "];" : "")%>

$( document ).ready(function() {
	setaPrazo();
	focusFirstField();
	mudaPeriodicidade();

	<% if (renegociarModel.getPropostas() != null && !renegociarModel.getPropostas().isEmpty()) { %>
	     carregaPropostas();
	<% } %>
});

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
    } else  {
      f0.adePrazo.focus();
    }
  }

  function renegociaContrato() {
    var adeCarencia   = (f0.adeCarencia.value == '' ? 0 : parseInt(f0.adeCarencia.value));
    var prazoContrato = (f0.adePrazo.value == '' ? 0 : parseInt(f0.adePrazo.value));
    var prazoServidor = (rsePrazo == '' ? 0 : parseInt(rsePrazo));
    var quinzenal     = (f0.adePeriodicidade != undefined && f0.adePeriodicidade != null && f0.adePeriodicidade.value == 'Q');
    if (quinzenal) {
      prazoServidor = prazoServidor * 2;
    }
    
    if (rsePrazo != '' && (prazoContrato + adeCarencia > prazoServidor) && !permitePrazoMaiorContSer) {
      alert ('<hl:message key="mensagem.renegociar.consignacao.prazo.invalido"/>' + ' ' + rsePrazo);
      f0.adePrazo.focus();
    } else {
        if (<%=renegociarModel.isMensagemMargemComprometida()%>) {
           alert('<hl:message key="mensagem.alerta.margem.comprometida"/>');
         }
         if (<%=(String)((renegociarModel.isPermiteCadVlrTac() || renegociarModel.isPermiteCadVlrIof() || renegociarModel.isPermiteCadVlrLiqLib() || renegociarModel.isPermiteCadVlrMensVinc()) ? "verificaCadInfFin() && " : "")%> vf_reservar_margem(<%=renegociarModel.isPermiteRenegociarComprarMargem3NegativaCasada() ? "'false'" : renegociarModel.permiteVlrNegativo() ? "'false'" : ""%>) &&
             <%=(String)(renegociarModel.isValorMaxIgualSomaContratos() ? "verificaValor() && " : "")%> verificaDataNasc() && verificaEmail() &&
             <%=(String)(renegociarModel.isPrazoMaxIgualMaiorContratos() ? "verificaPrazoMax() && " : "")%>
             <%=(String)(renegociarModel.isSerInfBancariaObrigatoria() ? "verificaInfBanco() && " : "")%>
             <%=(String)((responsavel.isCsaCor() && renegociarModel.isExigeModalidadeOperacao()) ?  "verificaModalidadeOperacao() && " : "")%>
             <%=(String)((responsavel.isCsaCor() && renegociarModel.isExigeMatriculaSerCsa()) ?  "verificaMatriculaSerCsa() && " : "")%>
           (confirm('<%=TextHelper.forJavaScriptBlock(renegociarModel.getMensagem())%><hl:message key="<%=(String)(compra ? "mensagem.confirmacao.compra" : "mensagem.confirmacao.renegociacao")%>"/>'))){
            enableAll();
           f0.submit();
        }
    }
  }

  function verificaValor() {
    var valor = f0.adeVlr.value;
    valor     = valor.replace(',','.');
    if (f0.adeVlr != null && f0.adeVlr.value != '' && (valor > <%=TextHelper.forJavaScriptBlock(renegociarModel.getValorTotal())%>)) {
      alert('<hl:message key="mensagem.renegociar.consignacao.valor.inferior" arg0="<%=TextHelper.forHtmlAttribute(renegociarModel.getLabelTipoVlr())%>" arg1="<%=TextHelper.forHtmlAttribute(NumberHelper.format((renegociarModel.getValorTotal()).doubleValue(), NumberHelper.getLang()))%>"/>');
      f0.adeVlr.focus();
      return false;
    }
    return true;
  }

  function verificaPrazoMax() {
    if (f0.adePrazo != null && f0.adePrazo.value != '') {
      var prazo = parseInt(f0.adePrazo.value);
      if (prazo > <%=TextHelper.forJavaScriptBlock(renegociarModel.getMaiorPrazoRestante() != null ? renegociarModel.getMaiorPrazoRestante() : 999)%>) {
        alert('<hl:message key="mensagem.renegociar.consignacao.prazo.inferior" arg0="<%=TextHelper.forHtmlAttribute(String.valueOf(renegociarModel.getMaiorPrazoRestante()))%>"/>');
        f0.adePrazo.focus();
        return false;
      }
    }
    return true;
  }

  function verificaDataNasc() {
   if (validarDataNasc) {
     var dataNascBase = '<%=TextHelper.forJavaScriptBlock(JCryptOld.crypt("IB", TextHelper.isNull(renegociarModel.getSerDataNasc()) ? "vazio" : renegociarModel.getSerDataNasc().replaceAll("/", "")))%>';
     var dataNasc    = f0.dataNasc.value;
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

    var banco   = Javacrypt.crypt(valor, formataParaComparacao(f0.numBanco.value))[0];
    var agencia = Javacrypt.crypt(valor, formataParaComparacao(f0.numAgencia.value))[0];

    var conta = formataParaComparacao(f0.numConta.value);
    var pos   = 0;
    var letra = conta.substr(pos, 1);
    while (letra == 0 && pos < conta.length) {
      pos++;
      letra = conta.substr(pos, 1)  ;
    }

    conta = conta.substr(pos,conta.length);
    var conta1 = Javacrypt.crypt(valor, conta.substr(0, conta.length/2))[0];
    var conta2 = Javacrypt.crypt(valor, conta.substr(conta.length/2, conta.length))[0];

    if (ValidaCamposV4(Controles, Msgs)){
    <%if (!renegociarModel.isRseTemInfBancaria()) { /* servidor não tem informações bancárias cadastradas */ %>
       return true;
    <%} else {%>
      if (((banco != '<%=TextHelper.forJavaScriptBlock(renegociarModel.getNumBanco())%>') || (agencia != '<%=TextHelper.forJavaScriptBlock(renegociarModel.getNumAgencia())%>') || (conta1 != '<%=TextHelper.forJavaScriptBlock(renegociarModel.getNumConta1())%>') || (conta2 != '<%=TextHelper.forJavaScriptBlock(renegociarModel.getNumConta2())%>')) &&
          ((banco != '<%=TextHelper.forJavaScriptBlock(renegociarModel.getNumBancoAlt())%>') || (agencia != '<%=TextHelper.forJavaScriptBlock(renegociarModel.getNumAgenciaAlt())%>') || (conta1 != '<%=TextHelper.forJavaScriptBlock(renegociarModel.getNumContaAlt1())%>') || (conta2 != '<%=TextHelper.forJavaScriptBlock(renegociarModel.getNumContaAlt2())%>'))) {
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

  function verificaCadInfFin() {
    var Controles;
    var Msgs;

    Controles = new Array("adeVlrTac", "adeVlrIof", "adeVlrLiquido", "adeVlrMensVinc");

    Msgs = new Array('<hl:message key="mensagem.informe.ade.valor.tac"/>',
                     '<hl:message key="mensagem.informe.ade.valor.iof"/>',
                     '<hl:message key="mensagem.informe.ade.valor.liberado"/>',
                     '<hl:message key="mensagem.informe.ade.valor.mensalidade"/>');

    return ValidaCamposV4(Controles, Msgs);
  }

  function verificaCarencia() {
    var mensagem             = "";
    var carencia             = parseInt(f0.adeCarencia.value);
    var carenciaMinPermitida = parseInt(<%=renegociarModel.getCarenciaMinPermitida()%>);
    var carenciaMaxPermitida = parseInt(<%=renegociarModel.getCarenciaMaxPermitida()%>);

    var quinzenal = (f0.adePeriodicidade != undefined && f0.adePeriodicidade != null && f0.adePeriodicidade.value == 'Q');
    if (quinzenal) {
      carenciaMinPermitida = carenciaMinPermitida * 2;
      carenciaMaxPermitida = carenciaMaxPermitida * 2;
    }
    
    if ((carencia < carenciaMinPermitida) || (carencia > carenciaMaxPermitida)) {
      if (carenciaMaxPermitida > carenciaMinPermitida) {
        mensagem = '<hl:message key="mensagem.erro.carencia.entre.min.max"/>'.replace("{0}", carenciaMinPermitida).replace("{1}", carenciaMaxPermitida);
      } else if (carenciaMaxPermitida < carenciaMinPermitida) {
        mensagem = '<hl:message key="mensagem.erro.carencia.menor.max"/>'.replace("{0}", carenciaMaxPermitida);
      } else if (carenciaMaxPermitida == carenciaMinPermitida) {
        mensagem = '<hl:message key="mensagem.erro.carencia.fixa"/>'.replace("{0}", carenciaMinPermitida);
      }

      alert(mensagem);
      setTimeout(function() {
          f0.adeCarencia.focus();
        }, 100);
      return false;
    } else {
      return true;
    }
  }

  function verificaEmail() {
    <% if (renegociarModel.isExigeEmailServidor()) { %>
    if (f0.serEmail.value == null || f0.serEmail.value == '' || !isEmailValid(f0.serEmail.value)) {
      alert('<hl:message key="mensagem.informe.ser.email"/>');
      f0.serEmail.focus();
      return false;
    }
    <% } %>
    return true;
  }

  function campos() {
  <%if (renegociarModel.isPermiteCadVlrLiqLib()) {%>
    if(f0.adeVlrLiquido.value == ''){
      alert('<hl:message key="mensagem.informe.ade.valor.liberado"/>');
      f0.adeVlrLiquido.focus();
      return false;
    }
  <%}%>
    if (<%=renegociarModel.isSerSenhaObrigatoria()%> && f0.serLogin != null && f0.serLogin.value == '') {
      alert('<hl:message key="mensagem.informe.ser.usuario"/>');
      f0.serLogin.focus();
      return false;
    }
    if (<%=renegociarModel.isSerSenhaObrigatoria()%> && f0.senha != null && trim(f0.senha.value) == '') {
      alert('<hl:message key="mensagem.informe.ser.senha"/>');
      f0.senha.focus();
      return false;
    }
    if (f0.senha != null && trim(f0.senha.value) != '') {
      CriptografaSenha(f0.senha, f0.serAutorizacao, false);
    }
    
    if (!verificaAnexo()) {
      return false;
    }

    if (!verificarCamposAdicionais()) {
        return false;
    }
    
    <% if (compra && ParamSist.paramEquals(CodedValues.TPC_INFORMA_NUM_PORTABILIDADE_CIP_COMPRA, CodedValues.NUM_PORTABILIDADE_CIP_COMPRA_OBRIGATORIO, responsavel)) { %>
    if (f0.numCipCompra.value == '') {
      alert('<hl:message key="mensagem.informe.num.cip.compra"/>');
      f0.numCipCompra.focus();
      return false;
    }
    <% } %>

    return true;
  }

  function verificaAnexo() {
  <% if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_ANEXO_INCLUSAO_CONTRATOS, CodedValues.TPC_SIM, responsavel) && 
          responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO) && renegociarModel.isAnexoInclusaoContratosObrigatorio()) { %>
    if (document.getElementById('FILE1').value == '') {
      alert('<hl:message key="mensagem.informe.arquivo.upload"/>');
      return false;
    }
  <% } %>

  <% if (compra && ParamSist.paramEquals(CodedValues.TPC_INFORMA_ANEXO_ADE_DOC_ADICIONAL_COMPRA, CodedValues.ANEXO_ADE_DOC_ADICIONAL_COMPRA_OBRIGATORIO, responsavel) &&
          responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO)) { %>
    if (document.getElementById('FILE_DOC_ADICIONAL_COMPRA').value == '') {
      alert('<hl:message key="mensagem.informe.arquivo.upload"/>');
      return false;
    }
  <% } %>
  
    <%if(!TextHelper.isNull(qtdeMinAnexos) && Integer.valueOf(qtdeMinAnexos) > 0){%>
    	let elemento = document.getElementById("pic-progress-wrap-FILE1");
    	let qtdeMin = <%=qtdeMinAnexos%>;
    	if(elemento == null || elemento == 'undefined' || elemento.childNodes.length < qtdeMin){
    		alert('<%=ApplicationResourcesHelper.getMessage("mensagem.erro.upload.arquivo.qunt.min", responsavel, qtdeMinAnexos)%>');
          return false;
    	}
    <%}%>

    return true;
  }

  <% if (responsavel.isCsaCor() && renegociarModel.isExigeModalidadeOperacao()) { %>
  function verificaModalidadeOperacao() {
    if (f0.tdaModalidadeOp.value == null || f0.tdaModalidadeOp.value == '') {
      alert('<hl:message key="mensagem.erro.modalidade.operacao.obrigatorio"/>');
      return false;
    }
    return true;
  }
  <% } %>

  <% if (responsavel.isCsaCor() && renegociarModel.isExigeMatriculaSerCsa()) { %>
  function verificaMatriculaSerCsa() {
    if (f0.tdaMatriculaCsa.value == null || f0.tdaMatriculaCsa.value == '') {
      alert('<hl:message key="mensagem.erro.matricula.csa.obrigatoria"/>');
      return false;
    }
    return true;
  }
  <% } %>

  function mudaPeriodicidade() {
  <% if (!PeriodoHelper.folhaMensal(responsavel)) { %>       
    <% if (renegociarModel.getPrazosPossiveisMensal() != null && !renegociarModel.getPrazosPossiveisMensal().isEmpty()) { %>
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

  function verificarCamposAdicionais() {
	     
	     <% if (renegociarModel.getTdaList() != null) { %>
	     <% for (TransferObject tda : renegociarModel.getTdaList()) { %>
	     	var sptExibe = '<%=(String) tda.getAttribute(Columns.SPT_EXIBE)%>'; 
	        var cptExibe = '<%=(String) tda.getAttribute(Columns.CPT_EXIBE)%>'; 
	     	if ('O' == sptExibe || 'O' == cptExibe) {
	     		var elements = document.getElementsByName('TDA_<%=(String) tda.getAttribute(Columns.TDA_CODIGO)%>')

	     		if (elements[0].type == 'text') {

	     			var value = elements[0].value;
	     			if (!value || !value.trim()) {
	         			alert('<hl:message key="mensagem.preencher.campos.adicionais" arg0="<%=TextHelper.forHtmlAttribute(tda.getAttribute(Columns.TDA_DESCRICAO))%>"/> ');
	         			return false;
	         		}
	     		} else if (elements[0].type == 'radio') {
					var preenchido = false;
	     			for (el of elements) {
	    				if (el.checked) {
							preenchido = true;
							break;
	            		}
	             	}
	             	if (!preenchido) {
	         			alert('<hl:message key="mensagem.preencher.campos.adicionais" arg0="<%=TextHelper.forHtmlAttribute(tda.getAttribute(Columns.TDA_DESCRICAO))%>"/> ');
	             		return false;
	                }
	         	}
	        }
	     <% } %>
	   <% } %>

	   		return true;
	     }
</script>
</c:set>
<c:set var="pageModals">
  <t:modalSubAcesso>
    <jsp:attribute name="titulo"><hl:message key="rotulo.historico.liq.antecipada.acao"/></jsp:attribute>
  </t:modalSubAcesso>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="pageModals">${pageModals}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>   
    <jsp:attribute name="javascript">${javascript}</jsp:attribute> 
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>