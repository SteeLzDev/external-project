<%@ page contentType="text/html" pageEncoding="UTF-8" language="java" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.helper.upload.UploadHelper" %>
<%@ page import="java.util.*"%>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="java.io.*"%>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.MensagemTO"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
String menTitulo = (String) request.getAttribute("menTitulo");
String titulo = (String) request.getAttribute("titulo");
String menData = (String) request.getAttribute("menData");
MensagemTO menTO = (MensagemTO) request.getAttribute("menTO");
String menSequencia = (String) request.getAttribute("menSequencia");
String funCodigo = (String) request.getAttribute("funCodigo");
AcessoSistema responsavel = (AcessoSistema) request.getAttribute("responsavel");
String menCodigo = (String) request.getAttribute("menCodigo");
String menTexto = (String) request.getAttribute("menTexto");
String linkDinamico = (String) request.getAttribute("linkDinamico");
List<?> consignatarias = (List<?>) request.getAttribute("consignatarias");
List<TransferObject> funcoes = (List<TransferObject>) request.getAttribute("funcoes");
List<?> naturezas = (List<?>) request.getAttribute("naturezas");
TransferObject arquivo = (TransferObject) request.getAttribute("arquivo");
int paramMenLidaIndividualmenteInt = (int)request.getAttribute("paramMenLidaIndividualmenteInt");
boolean podeEnviarPushNotificationEmMassa = (boolean) request.getAttribute("podeEnviarPushNotificationEmMassa");
String[] extensoesPermitidas = (String[]) request.getAttribute("extensoesPermitidas");
%>
<c:set var="title">
  <%=TextHelper.forHtmlContent(titulo)%>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-sistema"></use>
</c:set>
<c:set var="bodyContent">
<div class="row">
  <div class="col-sm">

  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title">
        <%=TextHelper.forHtmlContent(menTitulo)%>
        <% if (!TextHelper.forHtmlContent(menTitulo).equals("")) { %>
        - 
        <% } %>
        <hl:message key="rotulo.editar.grid"/>
      </h2>
    </div>
    <div class="card-body">
      <form method="post" action="<%=TextHelper.forHtmlAttribute(linkDinamico)%>&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1" enctype="multipart/form-data">
        <input type="hidden" name="CSA_CODIGO" value="<%=TextHelper.forHtmlAttribute((responsavel.isCsa()) ? responsavel.getCsaCodigo() : "")%>">
        <input type="hidden" name="funCodigo"  value="<%=TextHelper.forHtmlAttribute(funCodigo)%>">
        <input type="hidden" name="NSE_CODIGO" value="">
        <input type="hidden" name="menCodigo"  value="<%=TextHelper.forHtmlAttribute(menCodigo)%>">
        <input type="hidden" name="MM_update"  value="form1">
       
        <div class="row">
          <div class="form-group col-sm-8">
            <label for="iTitulo"><hl:message key="rotulo.mensagem.titulo"/></label>
            <hl:htmlinput di="iTitulo" name="menTitulo" type="text"  classe="form-control" value="<%=TextHelper.forHtmlAttribute(menTitulo)%>" placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.titulo", responsavel)%>' size="32" mask="#*A40"/>
          </div>
          <div class="form-group col-sm-4">
            <label for="iData"><hl:message key="rotulo.mensagem.data.criacao"/></label>
            <input type="text" class="form-control" name="menData" id="iData" value="<%=TextHelper.forHtmlAttribute(menData)%>" placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.data.criada.automaticamente", responsavel)%>' disabled>
          </div>
        </div>
        <% 
           funCodigo = menTO.getFunCodigo() != null ? menTO.getFunCodigo().toString() : "";
           if (funcoes != null && !funcoes.isEmpty()) {
        %>
        <div class="row">
          <div class="form-group col-sm-8">
            <label for="iFuncoes"><hl:message key="rotulo.funcao.plural"/></label>
            <%=JspHelper.geraCombo(funcoes, "funCodigoAux", Columns.FUN_CODIGO, Columns.FUN_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel), null, false, 1, funCodigo, "", false , "form-control")%>
          </div>
          <div class="form-group col-sm-4">
            <label for="iOrdem"><hl:message key="rotulo.mensagem.sequencia"/></label>
            <%menSequencia = menTO.getMenSequencia()!= null ? menTO.getMenSequencia().toString() : "" ;%>
            <hl:htmlinput name="menSequencia" type="text" di="iOrdem" classe="form-control" value="<%=TextHelper.forHtmlAttribute(menSequencia)%>" size="6" mask="#D4" placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.placeholder.digite.sequencia", responsavel)%>'/>
          </div>
        </div>
        <% } %>
        <fieldset>
          <legend class="legend">
            <span><hl:message key="rotulo.mensagem.configuracao.mensagem"/></span>
          </legend>
  		  <div class="form-group col-sm-8 mb-1">
          	<label for="men-publica"><hl:message key="label.boas.vindas.mensagem.publica"/></label>
            <input id="men-publica" class="form-check-input ml-1" type="checkbox" name="menPublica" type="checkbox" value="<%=(String)CodedValues.TPC_SIM%>" <%=TextHelper.forHtmlContent((menTO.getMenPublica().equals("S")) ? "CHECKED" : "")%>> 
  		  </div>
  		<%if(responsavel.isCseOrg() || responsavel.isSer()){%>
  			<%if(paramMenLidaIndividualmenteInt > 0) {%>          
      	          <div class="form-group col-sm-8 mb-1">
      	            <div><span><hl:message key="rotulo.mensagem.permite.ler.individualmente"/></span></div>
      	            <div class="form-check form-check-inline">
      	              <input class="form-check-input mt-2 ml-1" type="radio" name="menLidaIndividualmente" id="iSim" value="<%=(String)CodedValues.TPC_SIM%>" <%=TextHelper.forHtmlContent(TextHelper.isNull(menTO.getMenLidaIndividualmente()) || menTO.getMenLidaIndividualmente().equals(CodedValues.TPC_SIM) ? "checked" : "" )%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
      	              <label class="form-check-label labelSemNegrito ml-1 pr-4" for="iSim"><hl:message key="rotulo.sim"/></label>
                    </div>
                    <div class="form-check form-check-inline">
                    <input class="form-check-input mt-2 ml-1" type="radio" id="iNao" name="menLidaIndividualmente" value="<%=(String)CodedValues.TPC_NAO%>" <%=TextHelper.forHtmlContent(menTO.getMenLidaIndividualmente().equals(CodedValues.TPC_NAO) ? "checked" : "" )%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
      	              <label class="form-check-label labelSemNegrito ml-1 pr-4 pt-1" for="iNao"><hl:message key="rotulo.nao"/></label>
      	            </div>
                  </div>
  	        <%}%>
          <%}%>
            <div class="form-group col-sm-8 mb-1">
              <span><hl:message key="rotulo.mensagem.permite.ler.depois"/></span>
              <br/>
              <div class="form-check form-check-inline">
                <input class="form-check-input mt-2 ml-1" type="radio" name="menPermiteLerDepois" id="lSim" value="<%=(String)CodedValues.TPC_SIM%>" <%=TextHelper.forHtmlContent(TextHelper.isNull(menTO.getMenPermiteLerDepois()) || menTO.getMenPermiteLerDepois().equals(CodedValues.TPC_SIM) ? "checked" : "" )%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                <label class="form-check-label labelSemNegrito ml-1 pr-4" for="lSim"><hl:message key="rotulo.sim"/></label>
              </div>
              <div class="form-check form-check-inline">
                <input class="form-check-input mt-2 ml-1" type="radio" id="lNao" name="menPermiteLerDepois" value="<%=(String)CodedValues.TPC_NAO%>" <%=TextHelper.forHtmlContent(menTO.getMenPermiteLerDepois().equals(CodedValues.TPC_NAO) ? "checked" : "" )%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);">
                <label class="form-check-label labelSemNegrito ml-1 pr-4 pt-1" for="lNao"><hl:message key="rotulo.nao"/></label>
              </div>
            </div>
            <div class="form-group col-sm-8 mb-1">
              <span><hl:message key="rotulo.mensagem.notificar.cse.leitura"/></span>
              <br/>
              <div class="form-check form-check-inline">
                <input class="form-check-input mt-2 ml-1" type="radio" name="menNotificarCseLeitura" value="<%=(String)CodedValues.TPC_SIM%>" <%=TextHelper.forHtmlContent(TextHelper.isNull(menTO.getMenNotificarCseLeitura()) || menTO.getMenNotificarCseLeitura().equals(CodedValues.TPC_SIM) ? "checked" : "" )%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" id="nSim">
                <label class="form-check-label labelSemNegrito ml-1 pr-4" for="nSim"><hl:message key="rotulo.sim"/></label>
              </div>
              <div class="form-check form-check-inline">
                <input class="form-check-input mt-2 ml-1" type="radio" name="menNotificarCseLeitura" value="<%=(String)CodedValues.TPC_NAO%>" <%=TextHelper.forHtmlContent(menTO.getMenNotificarCseLeitura().equals(CodedValues.TPC_NAO) ? "checked" : "" )%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" id="nNao">
                <label class="form-check-label labelSemNegrito ml-1 pr-4 pt-1" for="nNao"><hl:message key="rotulo.nao"/></label>
              </div>
            </div>
            <div class="form-group col-sm-8 mb-1">
              <span><hl:message key="rotulo.mensagem.bloquear.csa.sem.leitura"/></span>
            <br/>
              <div class="form-check form-check-inline">
                <input class="form-check-input mt-2 ml-1" type="radio" name="menBloqCsaSemLeitura" value="<%=(String)CodedValues.TPC_SIM%>" <%=TextHelper.forHtmlContent(TextHelper.isNull(menTO.getMenBloqCsaSemLeitura()) || menTO.getMenBloqCsaSemLeitura().equals(CodedValues.TPC_SIM) ? "checked" : "" )%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" id="bSim">
                <label class="form-check-label labelSemNegrito ml-1 pr-4" for="bSim"><hl:message key="rotulo.sim"/></label>
              </div>
              <div class="form-check form-check-inline">
                <input class="form-check-input mt-2 ml-1" type="radio" name="menBloqCsaSemLeitura" value="<%=(String)CodedValues.TPC_NAO%>" <%=TextHelper.forHtmlContent(menTO.getMenBloqCsaSemLeitura().equals(CodedValues.TPC_NAO) ? "checked" : "" )%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" id="bNao">
                <label class="form-check-label labelSemNegrito ml-1 pr-4 pt-1" for="bNao"><hl:message key="rotulo.nao"/></label>
              </div>
            </div>
            <div class="form-group col-sm-8 mb-1">
              <span id="iExibirPara"><hl:message key="rotulo.mensagem.exibir.para"/></span>
              <div class="form-check">
                <div class="row" role="group" aria-labedby="iExibirPara">
                  <div class="col-sm-12 col-md-4">
                    <span class="text-nowrap"> 
                      <input class="form-check-input ml-1" type="checkbox" name="menExibeCse" type="checkbox" value="<%=(String)CodedValues.TPC_SIM%>" <%=TextHelper.forHtmlContent((menTO.getMenExibeCse().equals("S")) ? "CHECKED" : "")%> id="eEmpre"> 
                      <label class="form-check-label labelSemNegrito ml-1" for="eEmpre"><hl:message key="rotulo.consignante.singular"/></label>
                    </span>
                  </div>
                  <div class="col-sm-12 col-md-4">
                    <span class="text-nowrap"> 
                      <input class="form-check-input ml-1" type="checkbox" name="menExibeSer" type="checkbox" onclick="mudaCombo()" value="<%=(String)CodedValues.TPC_SIM%>" <%=TextHelper.forHtmlContent((menTO.getMenExibeSer().equals("S")) ? "CHECKED" : "")%> id="eEmpreg"> 
                      <label class="form-check-label labelSemNegrito ml-1 pr-4" for="eEmpreg"><hl:message key="rotulo.servidor.singular"/></label>
                    </span>
                  </div>
                  <div class="col-sm-12 col-md-4">
                    <span class="text-nowrap"> 
                      <input class="form-check-input ml-1" type="checkbox" name="menExibeOrg" type="checkbox" value="<%=(String)CodedValues.TPC_SIM%>" <%=TextHelper.forHtmlContent((menTO.getMenExibeOrg().equals("S")) ? "CHECKED" : "")%> id="eDepart"> 
                      <label class="form-check-label labelSemNegrito ml-1 pr-4" for="eDepart"><hl:message key="rotulo.orgao.singular"/></label>
                    </span>
                  </div>
                  <div class="col-sm-12 col-md-4">
                    <span class="text-nowrap"> 
                      <input class="form-check-input ml-1" type="checkbox" name="menExibeCor" type="checkbox" onclick="mudaCombo()" value="<%=(String)CodedValues.TPC_SIM%>" <%=TextHelper.forHtmlContent(menTO.getMenExibeCor().equals("S") ? "CHECKED" : "")%> id="eRepre"> 
                      <label class="form-check-label labelSemNegrito ml-1 pr-4" for="eRepre"><hl:message key="rotulo.correspondente.singular"/></label>
                    </span>
                  </div>
                  <div class="col-sm-12 col-md-4">
                    <span class="text-nowrap"> 
                      <input class="form-check-input ml-1" type="checkbox" name="menExibeCsa" type="checkbox" onclick="mudaCombo()" value="<%=(String)CodedValues.TPC_SIM%>" <%=TextHelper.forHtmlContent(menTO.getMenExibeCsa().equals("S") ? "CHECKED" : "")%> id="eProv"> 
                      <label class="form-check-label labelSemNegrito ml-1 pr-4" for="eProv"><hl:message key="rotulo.consignataria.singular"/></label>
                    </span>
                  </div>
                  <div class="col-sm-12 col-md-4">
                    <span class="text-nowrap"> 
                      <input class="form-check-input ml-1" type="checkbox" name="menExibeSup" type="checkbox" onclick="mudaCombo()" value="<%=(String)CodedValues.TPC_SIM%>" <%=TextHelper.forHtmlContent(menTO.getMenExibeSup().equals("S") ? "CHECKED" : "")%> id="eSup"> 
                      <label class="form-check-label labelSemNegrito ml-1 pr-4" for="eSup"><hl:message key="rotulo.suporte.singular"/></label>
                    </span>
                  </div>
                  <div class="col-sm-12 col-md-4">
                    <input class="form-check-input ml-1" type="checkbox" name="menCheckTodos" type="checkbox" onclick="checkTodos()" value="S" id="eTodos">
                    <label class="form-check-label labelSemNegrito ml-1 pr-4" for="eTodos"><hl:message key="rotulo.campo.todos.simples"/></label>
                  </div>
                </div>
              </div>
            </div>
            <% if (podeEnviarPushNotificationEmMassa) { %>
              <div class="form-group col-sm-8 mb-1">
                <span><hl:message key="rotulo.mensagem.push.notification.ser"/></span>
              <br/>
                <div class="form-check form-check-inline">
                  <input class="form-check-input mt-2 ml-1" type="radio" name="menPushNotificationSer" value="<%=(String)CodedValues.TPC_SIM%>" <%=TextHelper.forHtmlContent(TextHelper.isNull(menTO.getMenPushNotificationSer()) || menTO.getMenPushNotificationSer().equals(CodedValues.TPC_SIM) ? "checked" : "" )%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" id="pSim">
                  <label class="form-check-label labelSemNegrito ml-1 pr-4" for="pSim"><hl:message key="rotulo.sim"/></label>
                </div>
                <div class="form-check form-check-inline">
                  <input class="form-check-input mt-2 ml-1" type="radio" name="menPushNotificationSer" value="<%=(String)CodedValues.TPC_NAO%>" <%=TextHelper.forHtmlContent(menTO.getMenPushNotificationSer().equals(CodedValues.TPC_NAO) ? "checked" : "" )%> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);" id="pNao">
                  <label class="form-check-label labelSemNegrito ml-1 pr-4 pt-1" for="pNao"><hl:message key="rotulo.nao"/></label>
                </div>
              </div>
            <% } %>
            <div class="col-sm-12 col-md-6">
              <div class="form-group mb-1" style="page-break-after: avoid;">
                <label for="pdServico"><hl:message key="rotulo.consignataria.plural"/></label>
                <select name="CSA_CODIGO_AUX" class="form-control" id="CSA_CODIGO_AUX"  size="4" multiple>
                  <%
                    Iterator<?> it = consignatarias.iterator();
                    CustomTransferObject csa = null;
                    String csa_nome = null;
                    String csa_identificador, csaSelecionada, csaCodigo;
                    while (it.hasNext()) {
                      csa = (CustomTransferObject)it.next();
                      csa_identificador = (String)csa.getAttribute(Columns.CSA_IDENTIFICADOR);
                      csa_nome = (String)csa.getAttribute(Columns.getColumnName(Columns.CSA_NOME_ABREV));
                      csaSelecionada = (String) csa.getAttribute("SELECIONADO") == null ? "" : csa.getAttribute("SELECIONADO").toString();
                      csaCodigo = (String)csa.getAttribute(Columns.CSA_CODIGO);
                      if (csa_nome == null || csa_nome.trim().length() == 0)
                        csa_nome = csa.getAttribute(Columns.CSA_NOME).toString();
                      if (csa_nome.length() > 50) {
                          csa_nome = csa_nome.substring(0, 47) + "...";
                      }
                  %>
                          <option value="<%=TextHelper.forHtmlAttribute(csaCodigo)%>"<%=(String)((csaSelecionada.equals("S")) ? "SELECTED" : "")%>><%=TextHelper.forHtmlContent(csa_identificador)%> - <%=TextHelper.forHtmlContent(csa_nome)%></option>
                  <%
                    }
                  %>
                </select>
              </div>
              <div class="slider col-sm-8 col-md-12 pl-0 pr-0 mb-2">
                <div class="tooltip-inner"><hl:message key="mensagem.utilize.crtl"/></div>
              </div>
              <div class="btn-action float-end mt-3">
                <a class="btn btn-outline-danger" onClick="limparCombo(document.forms[0].CSA_CODIGO_AUX)" href="javascript:void(0);"><hl:message key="mensagem.limpar.selecao"/></a>
              </div>
            </div>
            <div class="col-sm-12 col-md-6">
              <div class="form-group mb-1" style="page-break-after: avoid;">
                <label for="pdServico"><hl:message key="rotulo.param.svc.natureza.servico"/></label>
                <select name="NSE_CODIGO_AUX" class="form-control" id="NSE_CODIGO_AUX"  size="4" multiple onclick="mudaComboNse()">
                  <%
                    Iterator<?> itNse = naturezas.iterator();
                    CustomTransferObject natureza = null;
                    String nse_descricao, nse_codigo;
                    while (itNse.hasNext()) {
                      natureza = (CustomTransferObject)itNse.next();
                      nse_descricao = (String)natureza.getAttribute(Columns.NSE_DESCRICAO);
                      nse_codigo = (String)natureza.getAttribute(Columns.NSE_CODIGO);                    
                  %>
                    <option value="<%=TextHelper.forHtmlAttribute(nse_codigo)%>"><%=TextHelper.forHtmlContent(nse_descricao)%></option>
                  <%
                    }
                  %>
                </select>
              </div>
              <div class="slider col-sm-8 col-md-12 pl-0 pr-0 mb-2">
                <div class="tooltip-inner"><hl:message key="mensagem.utilize.crtl"/></div>
              </div>
              <div class="btn-action float-end mt-3">
                <a class="btn btn-outline-danger" onClick="limparCombo(document.forms[0].NSE_CODIGO_AUX); mudaComboNse();" href="javascript:void(0);"><hl:message key="mensagem.limpar.selecao"/></a>
              </div>
            </div>
        </fieldset>
        <fieldset id="campo-anexo">
          <legend class="legend">
            <span><hl:message key="rotulo.comunicacao.anexo.plural"/></span>
          </legend>
          <hl:fileUploadV4 tipoArquivo="mensagem" multiplo="false" mostraCampoDescricao="false" extensoes="<%=UploadHelper.EXTENSOES_PERMITIDAS_ARQUIVO_MENSAGEM%>" divClassArquivo="form-group col-sm-6 mt-2" />
        </fieldset>
        <fieldset>
          <legend class="legend">
            <span><hl:message key="rotulo.mensagem.singular"/></span>
          </legend>
          <div class="row">
            <div class="form-group col-sm-12 col-md-12">
              <ul style="margin-top: 5px" id="uedit_button_strip"></ul>
              <textarea placeholder='<hl:message key="mensagem.placeholder.digite.mensagem"/>' name="innerTemp" cols="80" rows="30" class="form-control" id="uedit_textarea" onFocus="SetarEventoMascara(this,'#*65000',true);" onBlur="fout(this);ValidaMascara(this);"><%=TextHelper.forHtmlContent(menTexto)%></textarea>
            </div>
          </div>
        </fieldset>
        <% if (arquivo != null) { %>
        <div class="row">
          <div class="col-sm-12 col-md-12 mb-2">
            <div class="card">
              <div class="card-header hasIcon pl-3">
                <h2 class="card-header-title"><hl:message key="rotulo.servidor.cadastrar.dispensa.validacao.digital.arquivo.disponivel"/></h2>
              </div>
              <div class="card-body table-responsive p-0">
                <table class="table table-striped table-hover table-responsive">
                  <thead>
                    <tr>
                      <th width='20%' id="dataArquivo"><hl:message key="rotulo.servidor.cadastrar.dispensa.validacao.digital.data" /></th>
                      <th id="descricaoArquivo"><hl:message key="rotulo.servidor.cadastrar.dispensa.validacao.digital.nome" /></th>
                      <th width='10%' id="arquivo"><hl:message key="rotulo.acoes" /></th>
                    </tr>
                  </thead>
                  <tbody>
                    <%
                      String arqCodigo, arqNome, amnDataCriacao;
                      arqCodigo = arquivo.getAttribute(Columns.ARQ_CODIGO).toString();
                      arqNome = arquivo.getAttribute(Columns.AMN_NOME).toString();
                      amnDataCriacao = arquivo.getAttribute(Columns.AMN_DATA_CRIACAO).toString();
                    %>
                    <tr>
                      <td header="dataCriacaoArquivo"><%= amnDataCriacao %></td>
                      <td header="descricaoArquivo"><%= arqNome %></td>
                      <td class="text-nowrap" header="arquivo" id="nomeArquivo">
                        <div class="actions">
                          <div class="dropdown">
                            <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                              <div class="form-inline">
                                <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>" aria-label="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>"> <svg>
                                    <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                                </span> <hl:message key="rotulo.botao.opcoes"/>
                              </div>
                            </a>
                            <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                              <div class="position-relative">
                                <a href="#no-back" class="dropdown-item" onClick="fazDownload('<%=TextHelper.forJavaScript(arqCodigo)%>', '<%=TextHelper.forJavaScript(menCodigo)%>'); return false;"><hl:message key="rotulo.acoes.download"/>&nbsp;</a>
                                <a href="#no-back" class="dropdown-item" onClick="doIt('e', '<%=TextHelper.forJavaScript(arqCodigo)%>', '<%=TextHelper.forJavaScript(menCodigo)%>'); return false;"><hl:message key="rotulo.acoes.excluir"/>&nbsp;</a>
                              </div>
                            </div>
                          </div>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                  <tfoot>
                    <tr><td colspan="2"><hl:message key="mensagem.servidor.cadastrar.dispensa.validacao.digital.lista.arquivos" /></td></tr>
                  </tfoot>
                </table>
              </div>
            </div>
          </div>
        </div>
        <% } %>
        </form>
      </div>
    </div>
    <div class="btn-action d-print-none">
      <a class="btn btn-outline-danger" onClick="<%="postData('" + TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)) + "'); return false;"%>" href="#no-back"><hl:message key="rotulo.botao.cancelar"/></a>
      <a class="btn btn-primary" href="#no-back" name="btnSalvar" id="btnSalvar" onClick="if (validaCampos()){vf_escolha_nse();vf_escolha_csa();vf_escolha_fun();f0.submit();} return false;"><hl:message key="rotulo.botao.salvar"/></a>
    </div>
  </div>
</div>
</form>
</c:set>
<c:set var="javascript">
<hl:fileUploadV4 tipoArquivo="mensagem" multiplo="false" mostraCampoDescricao="false" extensoes="<%=extensoesPermitidas == null ? UploadHelper.EXTENSOES_PERMITIDAS_ARQUIVO_MENSAGEM : extensoesPermitidas%>" divClassArquivo="form-group col-sm-6 mt-2" scriptOnly="true" />
<link type="text/css" rel="stylesheet" href="../css/uedit.ui.css" />
<link type="text/css" rel="stylesheet" href="../css/uedit.ui.complete.css" />
<script type="text/javascript" src="../js/uedit.js"></script>
<script type="text/javascript" src="../js/uedit.ui.complete.js"></script>
<script type="text/JavaScript" src="../js/editorMsgs.js"></script>
<script>
  	const publicCheck = document.querySelector('#men-publica');
  	const campoAnexo = document.querySelector('#campo-anexo');


	publicCheck.addEventListener('change', function(){
	  if(this.checked){
	    campoAnexo.style.display = "none";
	  } else {
	    campoAnexo.style.display = "block";
	  }
	});

</script>
<script type="text/JavaScript">
  var f0 = document.forms[0];
  
  function formLoad() {
    mudaCombo();
    focusFirstField();
  }

  function fazDownload(codigo, menCodigo) {
    postData('../v3/manterMensagem?acao=downloadArquivo&arqCodigo=' + codigo + '&menCodigo=' + menCodigo + '&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>', 'download');
  }

  function doIt(opt, codigo, arq) {
      var msg = '', j;
      if (opt == 'e') {
      msg = '<hl:message key="mensagem.confirmacao.exclusao.arquivo"/>'.replace("{0}", arq);
        j = '../v3/manterMensagem?acao=excluir&arqCodigo=' + codigo + '&menCodigo=<%=menCodigo%>&_skip_history_=true';
      } else {
        return false;
      }
  
      j = j + '&<%=SynchronizerToken.generateToken4URL(request)%>'
      if (msg != '') {
        if (confirm(msg)) {
          if (opt == 'i' || opt == 'v') {
          postData(j);
          } else {
            postData(j);
          }
        } else {
          return false;
        }
      } else {
        postData(j);
      }
      return true;
  }
  
  var uedit_textarea = document.getElementById("uedit_textarea");
  var uedit_button_strip = document.getElementById("uedit_button_strip");
  var ueditorInterface = ueditInterface(uedit_textarea, uedit_button_strip);

  function mudaComboNse() {
    if (f0.NSE_CODIGO_AUX.value != null && f0.NSE_CODIGO_AUX.value != "") {	
    	f0.CSA_CODIGO_AUX.disabled=true;
    	f0.funCodigoAux.disabled=true;
    	limparCombo(f0.CSA_CODIGO_AUX);
    } else {
  		f0.CSA_CODIGO_AUX.disabled=false;
  		f0.funCodigoAux.disabled=false;
    }
  }
  

 
  window.onload = formLoad();
  
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>   
    <jsp:attribute name="javascript">${javascript}</jsp:attribute> 
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
