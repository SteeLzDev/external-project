<%--
* <p>Title: filtrarConsignacao_v4</p>
* <p>Description: Página de pesquisa avançada de consignação</p>
* <p>Copyright: Copyright (c) 2007</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper" %>
<%@ page import="com.zetra.econsig.persistence.entity.CampoUsuario" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
String filtroPesquisa = JspHelper.verificaVarQryStr(request, "filtro_pesquisa"); 
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

List<TransferObject> gruposConsignataria = (List<TransferObject>) request.getAttribute("lstGrupoConsignataria");
List<TransferObject> consignatarias = (List<TransferObject>) request.getAttribute("lstConsignataria");
List<TransferObject> correspondentes  = (List<TransferObject>) request.getAttribute("lstCorrespondente");
List<TransferObject> naturezas = (List<TransferObject>) request.getAttribute("lstNaturezas");
List<TransferObject> convenios  = (List<TransferObject>) request.getAttribute("lstConvenio");
List<TransferObject> gruposServico  = (List<TransferObject>) request.getAttribute("lstGrupoServico");
List<TransferObject> orgaos  = (List<TransferObject>) request.getAttribute("lstOrgao");
List<TransferObject> margens  = (List<TransferObject>) request.getAttribute("lstMargem");
List<TransferObject> tipoMotivoOperacao  = (List<TransferObject>) request.getAttribute("lstMotivoOperacaoConsignacao");
String mascaraIndice = (String) request.getAttribute("mascaraIndice");
Map<String, String> mapCampoUsuario  = (Map<String, String>) request.getAttribute("mapCampoUsuario");


// Booleano que verifica se exige senha do servidor para mostrar o valor da margem
Boolean exigeSenhaConsMargem = (Boolean) request.getAttribute("exigeSenhaConsMargem");

// Booleano do Parâmetro de obrigatoriedade de CPF e Matrícula
Boolean requerMatriculaCpf = (Boolean) request.getAttribute("requerMatriculaCpf");

//Parâmetro de obrigatoriedade de data de nascimento
boolean requerDataNascimento = (request.getAttribute("requerDataNascimento") != null);

String rotuloCampoTodos = (String) request.getAttribute("rotuloCampoTodos");
String ordenacao = (String) request.getAttribute("ordenacao");
List lstOrdenacaoAux = (List) request.getAttribute("lstOrdenacaoAux");
String ordemAdeData = (String) request.getAttribute("ordemAdeData");
String ordemSerCpf = (String) request.getAttribute("ordemSerCpf");
String ordemRseMatricula = (String) request.getAttribute("ordemRseMatricula");
String ordemSerNome = (String) request.getAttribute("ordemSerNome");

boolean pesquisaAvancada = TextHelper.isNull(request.getAttribute("pesquisaAvancada")) ? false : (Boolean) request.getAttribute("pesquisaAvancada");
boolean exibeCaptcha = TextHelper.isNull(request.getAttribute("exibeCaptcha")) ? false : (Boolean) request.getAttribute("exibeCaptcha");
boolean exibeCaptchaAvancado = TextHelper.isNull(request.getAttribute("exibeCaptchaAvancado")) ? false : (Boolean) request.getAttribute("exibeCaptchaAvancado");
boolean exibeCaptchaDeficiente = TextHelper.isNull(request.getAttribute("exibeCaptchaDeficiente")) ? false : (Boolean) request.getAttribute("exibeCaptchaDeficiente");
%>
<c:set var="title">
   <%= TextHelper.forHtml(request.getAttribute("tituloPagina")) %>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <div class="card">
    <div class="card-header hasIcon">
        <span class="card-header-icon"><svg width="26">
            <use xlink:href="../img/sprite.svg#i-consultar"></use></svg>
        </span>
        <h2 class="card-header-title"><hl:message key="mensagem.pesquisa.titulo"/></h2>
        <span class="card-header-icon-right dropdown-toggle" id="cardHeaderIconDropdown" style="cursor:pointer;" data-bs-toggle="dropdown">
                <svg width="26">
                  <use xlink:href="../img/sprite.svg#i-engrenagem" id="svgUseDropdown"></use>
        </svg>
        </span>
        <div class="container" id="dropDownMenu">
        <ul class="dropdown-menu" >
            <% if (responsavel.isCseSupOrg() && gruposConsignataria != null && gruposConsignataria.size() > 0) { %>
                <li><label><input data-value="<%=FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_TGC_CODIGO%>" type="checkbox"/>&nbsp;<hl:message key="rotulo.grupo.consignataria.singular"/></label></li>
            <% } %>
            <% if((responsavel.isCseSupOrg() || responsavel.isSer()) && consignatarias != null && consignatarias.size() > 0) { %>
                <li><label><input data-value="<%=FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_CSA_CODIGO%>" type="checkbox"/>&nbsp;<hl:message key="rotulo.consignataria.singular"/></label></li>
            <% } %>
            <% if(responsavel.isCsa() && correspondentes != null && correspondentes.size() > 0){ %>
                <li><label><input data-value="<%=FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_COR_CODIGO%>" type="checkbox"/>&nbsp;<hl:message key="rotulo.correspondente.singular"/></label></li>
            <% } %>
            <% if((responsavel.isCseSup() || responsavel.isCsaCor()) && orgaos != null && orgaos.size() > 0 ){ %>
                <li><label><input data-value="<%=FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_ORG_CODIGO%>" type="checkbox"/>&nbsp;<hl:message key="rotulo.orgao.singular"/></label></li>
            <% } %>
            <% if((responsavel.isCseSupOrg() || responsavel.isSer()) && gruposServico != null && gruposServico.size() > 0){ %>
                <li><label><input data-value="<%=FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_TGS_CODIGO%>" type="checkbox"/>&nbsp;<hl:message key="rotulo.grupo.servico.singular"/></label></li>
            <% } %>
            <%if(naturezas != null && naturezas.size() > 0){ %>
                <li><label><input data-value="<%=FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_NSE_CODIGO%>" type="checkbox"/>&nbsp;<hl:message key="rotulo.natureza.servico.titulo"/></label></li>
            <% } %>
            <%if(convenios != null && convenios.size() > 0){ %>
                <li><label><input data-value="<%=FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_SVC_CODIGO%>" type="checkbox"/>&nbsp;<hl:message key="rotulo.servico.singular"/></label></li>
            <% } %>
            <li><label><input data-value="<%=FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_CNV_COD_VERBA%>" type="checkbox"/>&nbsp;<hl:message key="rotulo.convenio.codigo.verba"/></label></li>
            <% if (!responsavel.isSer()) { %>
                <li><label><input data-value="<%=FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_SRS_CODIGO%>" type="checkbox"/>&nbsp;<hl:message key="rotulo.servidor.situacao"/></label></li>
            <% } %>
            <li><label><input data-value="<%=FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_ADE_IDENTIFICADOR%>" type="checkbox"/>&nbsp;<hl:message key="rotulo.consignacao.identificador"/></label></li>
            <%if(!TextHelper.isNull(mascaraIndice)){ %>
                <li><label><input data-value="<%=FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_ADE_INDICE%>" type="checkbox"/>&nbsp;<hl:message key="rotulo.consignacao.indice"/></label></li>
            <% } %>
            <li><label><input data-value="<%=FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_PERIODO%>" type="checkbox"/>&nbsp;<hl:message key="rotulo.pesquisa.data.periodo"/></label></li>
            <li><label><input data-value="<%=FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_SAD_CODIGO%>" type="checkbox"/>&nbsp;<hl:message key="rotulo.consignacao.status.contrato"/></label></li>
            <%if(margens != null && margens.size() > 0){ %>
                <li><label><input data-value="<%=FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_ADE_INC_MARGEM%>" type="checkbox"/>&nbsp;<hl:message key="rotulo.consignacao.incide.margem"/></label></li>
            <% }  %>
            <%if(tipoMotivoOperacao != null && tipoMotivoOperacao.size() > 0){ %>
                <li><label><input data-value="<%=FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_TMO_CODIGO%>" type="checkbox"/>&nbsp;<hl:message key="rotulo.motivo.operacao.singular"/></label></li>
            <% } %>
            <%if(responsavel.isCseSup()){ %>
                <li><label><input data-value="<%=FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_ARQUIVADO%>" type="checkbox"/>&nbsp;<hl:message key="rotulo.consignacao.arquivada"/></label></li>
            <% }  %>
            <%if(responsavel.isCsaCor()){ %>
                <li><label><input data-value="<%=FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_ADE_PROPRIA%>" type="checkbox"/>&nbsp;<hl:message key="rotulo.consignacao.propria"/></label></li>
            <% } %>
            <li><label><input data-value="<%=FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_ADE_INT_FOLHA%>" type="checkbox"/>&nbsp;<hl:message key="rotulo.consignacao.integra.folha"/></label></li>
            <li><label><input data-value="<%=FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_INF_SALDO_DEVEDOR%>" type="checkbox"/>&nbsp;<hl:message key="rotulo.saldo.devedor.singular"/></label></li>
            <li><label><input data-value="<%=FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_TIPO_ORDENACAO%>" type="checkbox"/>&nbsp;<hl:message key="rotulo.ordenacao"/></label></li>
            <li class="liBtnSalvar"><button class="btn btn-primary" name="btnSalvar" id="btnSalvar"><hl:message key="rotulo.botao.salvar"/></button></li>
        </ul>
</div>
  
  </div>
    <div class="card-body">

      <form action="<%= TextHelper.forHtmlAttribute(request.getAttribute("acaoFormulario")) %>" method="post" name="formPesqAvancada">
        <%= SynchronizerToken.generateHtmlToken(request) %>
        <% if (pesquisaAvancada) { %>
          <hl:htmlinput name="acao" di="acao" type="hidden" value="pesquisarConsignacao" />
          <hl:htmlinput name="FORM" di="FORM" type="hidden" value="formPesqAvancada" />
          <hl:htmlinput name="TIPO_LISTA" di="TIPO_LISTA" type="hidden" value="pesquisa_avancada" />
        <% } else { %>
          <hl:htmlinput name="acao" di="acao" type="hidden" value="pesquisarServidor" />
          <hl:htmlinput name="TIPO_LISTA" di="TIPO_LISTA" type="hidden" value="pesquisa"/>
        <% } %>

        <fieldset style="display:none;">
          <h3 class="legend">
            <span><hl:message key="rotulo.consultar.consignacao.dados.convenio"/></span>
          </h3>
          <div class="row">
            <% if(ShowFieldHelper.exibeCampoUsuario(FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_TGC_CODIGO, responsavel)){%>
                <div class="form-group col-sm-12 col-md-6">
                  <label for="TGC_CODIGO"><hl:message key="rotulo.grupo.consignataria.singular"/></label>
                  <%= JspHelper.geraCombo(gruposConsignataria, "TGC_CODIGO", Columns.TGC_CODIGO, Columns.TGC_DESCRICAO, rotuloCampoTodos, null, false, 1, JspHelper.verificaVarQryStr(request, "TGC_CODIGO"), null, false, "form-control") %>
                </div>
              <% } %>
            
             <% if((responsavel.isCseSupOrg() || responsavel.isSer()) && consignatarias != null && consignatarias.size() > 0 && ShowFieldHelper.exibeCampoUsuario(FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_CSA_CODIGO, responsavel)){%>
              <div class="form-group col-sm-12 col-md-6">
                <label for="CSA_CODIGO"><hl:message key="rotulo.consignataria.singular"/></label>
                <%= JspHelper.geraCombo(consignatarias, "CSA_CODIGO", Columns.CSA_CODIGO, Columns.getColumnName(Columns.CSA_NOME_ABREV) + ";" + Columns.CSA_IDENTIFICADOR, rotuloCampoTodos, null, false, 1, JspHelper.verificaVarQryStr(request, "CSA_CODIGO"), null, false, "form-control") %>
              </div>
            <% } %>

            <% if(responsavel.isCsa() && correspondentes != null && correspondentes.size() > 0 && ShowFieldHelper.exibeCampoUsuario(FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_COR_CODIGO, responsavel)){%>
              <div class="form-group col-sm-12 col-md-6">
                <label for="COR_CODIGO"><hl:message key="rotulo.correspondente.singular"/></label>
                <%= JspHelper.geraCombo(correspondentes, "COR_CODIGO", Columns.COR_CODIGO, Columns.COR_NOME + ";" + Columns.COR_IDENTIFICADOR, rotuloCampoTodos, null, false, 1, JspHelper.verificaVarQryStr(request, "COR_CODIGO"), null, false, "form-control") %>
              </div>
            <% } %>

            <% if((responsavel.isCseSup() || responsavel.isCsaCor()) && orgaos != null && orgaos.size() > 0 && ShowFieldHelper.exibeCampoUsuario(FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_ORG_CODIGO, responsavel)){%>
              <div class="form-group col-sm-12 col-md-6">
                <label for="ORG_CODIGO"><hl:message key="rotulo.orgao.singular"/></label>
                <%= JspHelper.geraCombo(orgaos, "ORG_CODIGO", Columns.ORG_CODIGO, Columns.ORG_NOME + ";" + Columns.ORG_IDENTIFICADOR, rotuloCampoTodos, null, false, 1, JspHelper.verificaVarQryStr(request, "ORG_CODIGO"), null, false, "form-control") %>
              </div>
           <% } %>

            <% if((responsavel.isCseSupOrg() || responsavel.isSer()) && gruposServico != null && gruposServico.size() > 0  && ShowFieldHelper.exibeCampoUsuario(FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_TGS_CODIGO, responsavel)){%>
              <div class="form-group col-sm-12 col-md-6">
                <label for="TGS_CODIGO"><hl:message key="rotulo.grupo.servico.titulo"/></label>
                <%= JspHelper.geraCombo(gruposServico, "TGS_CODIGO", Columns.TGS_CODIGO, Columns.TGS_GRUPO, rotuloCampoTodos, null, false, 1, JspHelper.verificaVarQryStr(request, "TGS_CODIGO"), null, false, "form-control") %>
              </div>
           <% } %>
            
            <% if(naturezas != null && naturezas.size() > 0 && ShowFieldHelper.exibeCampoUsuario(FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_NSE_CODIGO, responsavel)){%>
              <div class="form-group col-sm-12 col-md-6">
                <label for="NSE_CODIGO"><hl:message key="rotulo.natureza.servico.titulo"/></label>
                 <%=JspHelper.geraCombo(naturezas, "NSE_CODIGO", Columns.NSE_CODIGO, Columns.NSE_DESCRICAO, rotuloCampoTodos, null, false, 3, JspHelper.verificaVarQryStr(request, "NSE_CODIGO"), null, false, "form-control")%>
              </div>
           <% } %>

            <% if(convenios != null && convenios.size() > 0 && ShowFieldHelper.exibeCampoUsuario(FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_SVC_CODIGO, responsavel)){%>
              <div class="form-group col-sm-12 col-md-6">
                <label for="SVC_CODIGO"><hl:message key="rotulo.servico.singular"/></label>
                <%= JspHelper.geraCombo(convenios, "SVC_CODIGO", Columns.SVC_CODIGO, Columns.SVC_DESCRICAO + ";" + Columns.SVC_IDENTIFICADOR, rotuloCampoTodos, null, false, 1, JspHelper.verificaVarQryStr(request, "SVC_CODIGO"), null, false, "form-control") %>
              </div>
              <% } %>

            <% if(ShowFieldHelper.exibeCampoUsuario(FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_CNV_COD_VERBA, responsavel)){%>
                <div class="form-group col-sm-12 col-md-6">
                  <label for="CNV_COD_VERBA"><hl:message key="rotulo.codigo.verba.singular"/></label>
                  <hl:htmlinput name="CNV_COD_VERBA" 
                                di="CNV_COD_VERBA" 
                                type="text" 
                                classe="form-control"
                                mask="#*32" 
                                placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.codigo.verba", responsavel)%>'
                                value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "CNV_COD_VERBA"))%>" 
                  />
                </div>
            <% } %>
          </div>
        </fieldset>

        <% if (!responsavel.isSer()) { %>
            <fieldset style="display:none;">
              <h3 class="legend">
                <span><hl:message key="rotulo.consultar.consignacao.dados.servidor"/></span>
              </h3>

              <div class="row">
                <div class="form-group col-sm-12 col-md-6">
                  <hl:campoMatriculav4 placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.matricula", responsavel)%>' />
                </div>
              </div>

              <div class="row">
                <div class="form-group col-sm-12 col-md-6">
                  <hl:campoCPFv4 placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cpf", responsavel)%>' classe="form-control"/>
                </div>
              </div>

              <% if (exigeSenhaConsMargem) { %>
                <div class="row">
                  <div class="form-group col-sm-12 col-md-6">
                    <label for="CNV_COD_VERBA"><hl:message key="rotulo.senha.servidor.consulta.singular"/>&nbsp;<hl:message key="rotulo.servidor.singular"/>&nbsp;<hl:message key="rotulo.campo.opcional"/></label>
                    <hl:htmlpassword name="senha"
                                     cryptedfield="serAutorizacao"
                                     classe="form-control"
                                     mask="#*200"
                                     isSenhaServidor="true"
                    />
                  </div>
                </div>
              <% } %>
    
              <% if(ShowFieldHelper.exibeCampoUsuario(FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_SRS_CODIGO, responsavel)){%>
                <div class="row">
                    <div class="col-sm-12 col-md-12">
                      <h3 class="legend">
                        <span id="situacaoServidor"><hl:message key="rotulo.servidor.status.servidor"/></span>
                      </h3>
                      <hl:filtroStatusRegSerTagv4 />
                    </div>
                </div>
              <% } %>

            </fieldset>
        <% } %>

        <% if (requerDataNascimento) { %>
              <div class="row">
                <div class="form-group col-sm-12 col-md-6">
                            <label for="SER_DATA_NASC"><hl:message key="rotulo.servidor.dataNasc" fieldKey="SER_DATA_NASC"/></label>
                            <hl:htmlinput name="SER_DATA_NASC"
                                di="SER_DATA_NASC"
                                type="text"
                                classe="form-control"
                                value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "SER_DATA_NASC"))%>"
                                mask="<%=LocaleHelper.getDateJavascriptPattern()%>"
                                placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>"                             
                            />
                </div>
              </div>
        <% } %>

        <fieldset style="display:none;">
          <h3 class="legend">
            <span><hl:message key="rotulo.consultar.consignacao.dados.consignacao"/></span>
          </h3>
          <div class="row">
            <div class="form-group col-sm-12 col-md-6">
              <label for="ADE_NUMERO"><hl:message key="rotulo.consignacao.numero"/></label>
              <hl:htmlinput name="ADE_NUMERO"
                            di="ADE_NUMERO"
                            type="text"
                            classe="form-control w-100"
                            mask="#D20"
                            placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ade.numero", responsavel)%>'
                            value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "ADE_NUMERO"))%>"
              />
            </div>
            <div class="form-group col-sm-12 col-md-1 mt-4">
              <a id="adicionaAdeLista" class="btn btn-primary w-50" href="javascript:void(0);" onClick="adicionaNumero()" aria-label='<hl:message key="mensagem.inserir.ade.numero.clique.aqui"/>'>
                <svg width="15"><use xlink:href="../img/sprite.svg#i-avancar"></use></svg>
              </a>
              <a id="removeAdeLista" class="btn btn-primary w-50 mt-1" href="javascript:void(0);" onClick="removeNumero()" aria-label='<hl:message key="mensagem.remover.ade.numero.clique.aqui"/>' style="display: none">
                <svg width="15"><use xlink:href="../img/sprite.svg#i-voltar"></use></svg>
              </a>
            </div>
            <div id="adeLista" class="form-group col-sm-12 col-md-5 mt-4" style="display: none">
              <select class="form-control form-select w-100" id="ADE_NUMERO_LIST" name="ADE_NUMERO_LIST" multiple="multiple" size="6"></select>
            </div>

            <% if(ShowFieldHelper.exibeCampoUsuario(FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_ADE_IDENTIFICADOR, responsavel)){%>
                <div class="form-group col-sm-12 col-md-6">
                  <label for="ADE_IDENTIFICADOR"><hl:message key="rotulo.consignacao.identificador"/></label>
                  <hl:htmlinput name="ADE_IDENTIFICADOR" 
                                di="ADE_IDENTIFICADOR" 
                                type="text" 
                                classe="form-control"
                                mask="#*40" 
                                placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ade.identificador", responsavel)%>'
                                value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "ADE_IDENTIFICADOR"))%>" 
                  />
                </div>
            <% } %>
            
            <% if(!TextHelper.isNull(mascaraIndice) && ShowFieldHelper.exibeCampoUsuario(FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_ADE_INDICE, responsavel)){%>
              <div class="form-group col-sm-12 col-md-6">
                <label for="ADE_INDICE"><hl:message key="rotulo.consignacao.indice"/></label>
                <hl:htmlinput name="ADE_INDICE" 
                              di="ADE_INDICE" 
                              type="text" 
                              classe="form-control" 
                              mask="<%=mascaraIndice%>" 
                              placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ade.indice", responsavel)%>'
                              value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "ADE_INDICE"))%>" 
                />
              </div>
            <% } %>
            
            <% if(ShowFieldHelper.exibeCampoUsuario(FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_PERIODO, responsavel)){%>
                <div class="form-group col-sm-12 col-md-6 cep-input">
                  <span id="periodo"><hl:message key="rotulo.pesquisa.data.periodo"/></span>
                  <div class="row" role="group" aria-labelledby="periodo">
                    <div class="form-group col-sm-12 col-md-5">
                      <hl:htmlinput name="ADE_ANO_MES_INI" 
                                    di="ADE_ANO_MES_INI" 
                                    type="text" 
                                    classe="form-control w-100 mr-2" 
                                    size="10" 
                                    mask="DD/DDDD" 
                                    placeHolder="MM/AAAA"
                                    ariaLabel='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.data.periodo", responsavel)%>'
                                    value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "ADE_ANO_MES_INI"))%>" 
                      />
                    </div>
                    <% String tipoOcorrenciaPeriodo = JspHelper.verificaVarQryStr(request, "tipoOcorrenciaPeriodo");%>
                    <div class="form-group col-sm-12 col-md-7">
                      <select class="form-control form-select w-100" id="tipoOcorrenciaPeriodo" name="tipoOcorrenciaPeriodo" aria-label='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.tipo.periodo", responsavel)%>' onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);">
                        <option value="inclusao"  <%= (TextHelper.isNull(tipoOcorrenciaPeriodo) || tipoOcorrenciaPeriodo.equals("inclusao")) ? " selected " : ""%>><hl:message key="rotulo.pesquisa.data.periodo.inclusao"/></option>
                        <option value="alteracao" <%=(String)((tipoOcorrenciaPeriodo.equals("alteracao")) ? " selected " : "")%>><hl:message key="rotulo.pesquisa.data.periodo.alteracao"/></option>
                        <option value="suspensao" <%=(String)((tipoOcorrenciaPeriodo.equals("suspensao")) ? " selected " : "")%>><hl:message key="rotulo.pesquisa.data.periodo.suspensao"/></option>
                      </select>
                    </div>
                  </div>
                </div>

                <div class="form-group col-sm-12 col-md-6 cep-input">
                    <span id="dataInclusao"><hl:message key="rotulo.pesquisa.data.inclusao"/></span>
                      <div class="row" role="group" aria-labelledby="dataInclusao">
                        <div class="form-group col-sm-12 col-md-1">
                          <div class="float-left align-middle mt-4 form-control-label">
                            <span><hl:message key="rotulo.pesquisa.data.prefixo.inicio"/></span>
                          </div>
                        </div>
                        <div class="form-group col-sm-12 col-md-5">
                          <hl:htmlinput name="periodoIni" 
                                        di="periodoIni" 
                                        type="text" 
                                        classe="form-control w-100" 
                                        placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" 
                                        mask="<%=LocaleHelper.getDateJavascriptPattern()%>" 
                                        value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "periodoIni"))%>" 
                                        ariaLabel='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.data.inclusao.ini", responsavel)%>'
                          />
                        </div>
                        
                        <div class="form-group col-sm-12 col-md-1">
                          <div class="float-left align-middle mt-4 form-control-label">
                            <span><hl:message key="rotulo.pesquisa.data.prefixo.fim"/></span>
                          </div>
                        </div>
                        <div class="form-group col-sm-12 col-md-5">
                          <hl:htmlinput name="periodoFim" 
                                        di="periodoFim" 
                                        type="text" 
                                        classe="form-control w-100" 
                                        placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" 
                                        mask="<%=LocaleHelper.getDateJavascriptPattern()%>" 
                                        value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "periodoFim"))%>" 
                                        ariaLabel='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.data.inclusao.fim", responsavel)%>'
                          />
                        </div>
                      </div>
                </div>
            <% } %>
            
            <% if(ShowFieldHelper.exibeCampoUsuario(FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_SAD_CODIGO, responsavel)){%>
                <div class="col-sm-12 col-md-12">
                  <h3 class="legend">
                    <span id="situacaoContrato"><hl:message key="rotulo.consignacao.status.contrato"/></span>
                  </h3>
                  <hl:filtroStatusAdeTagv4 />
                </div>
            <% } %>
          </div>
        </fieldset>

        <fieldset style="display:none;">
          <h3 class="legend">
            <span><hl:message key="rotulo.consultar.consignacao.demais.informacoes"/></span>
          </h3>
          <div class="form-check">
            <div class="row">
                <% if(margens != null && margens.size() > 0 && ShowFieldHelper.exibeCampoUsuario(FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_ADE_INC_MARGEM, responsavel)){%>
                    <div class="form-group col-sm-12 col-md-6">
                      <label for="ADE_INC_MARGEM"><hl:message key="rotulo.consignacao.incide.margem"/></label>
                      <%= JspHelper.geraCombo(margens, "ADE_INC_MARGEM", Columns.MAR_CODIGO, Columns.MAR_DESCRICAO, rotuloCampoTodos, null, false, 3, JspHelper.verificaVarQryStr(request, "ADE_INC_MARGEM"), null, false, "form-control") %>
                    </div>
                  <% } %>

                <% if(tipoMotivoOperacao != null && tipoMotivoOperacao.size() > 0 && ShowFieldHelper.exibeCampoUsuario(FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_TMO_CODIGO, responsavel)){%>
                    <div class="form-group col-sm-12 col-md-6">
                      <label for="TMO_CODIGO"><hl:message key="rotulo.efetiva.acao.consignacao.dados.tipo.mtv.cancelamento"/></label>
                      <%= JspHelper.geraCombo(tipoMotivoOperacao, "TMO_CODIGO", Columns.TMO_CODIGO, Columns.TMO_DESCRICAO, rotuloCampoTodos, null, false, 1, JspHelper.verificaVarQryStr(request, "TMO_CODIGO"), null, false, "form-control") %>
                    </div>
                  <% } %>

                <% if(responsavel.isCseSup() && ShowFieldHelper.exibeCampoUsuario(FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_ARQUIVADO, responsavel)){%>
                    <div class="col-sm-12 col-md-6">
                      <div class="form-group mb-0">
                        <span id="listarConsignacoesArquivadas"><hl:message key="rotulo.consignacao.historico.arquivado"/></span>
                      </div>
                      <div class="form-check mt-2" role="radiogroup" aria-labelledby="listarConsignacoesArquivadas">
                        <label class="form-check-label pr-3" style="padding-right: 1.5rem" for="arquivado_sim"> 
                          <input class="form-check-input" type="radio" name="arquivado" id="arquivado_sim" value="S" <%= (JspHelper.verificaVarQryStr(request, "arquivado").equals("S")) ? "checked" : "" %>/>
                          <span class="text-nowrap align-text-top"><hl:message key="rotulo.sim"/></span>
                        </label>
                        <label class="form-check-label" for="arquivado_nao"> 
                          <input class="form-check-input" type="radio" name="arquivado" id="arquivado_nao" value="N" <%=(!JspHelper.verificaVarQryStr(request, "arquivado").equals("S")) ? "checked" : "" %>/>
                          <span class="text-nowrap align-text-top"><hl:message key="rotulo.nao"/></span>
                        </label>
                      </div>
                    </div>
                  <% } %>

                <% if(responsavel.isCsaCor() && ShowFieldHelper.exibeCampoUsuario(FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_ADE_PROPRIA, responsavel)){%>
                    <div class="col-sm-12 col-md-6">
                      <div class="form-group mb-0">
                        <span id="listarMinhasConsignacoes"><hl:message key="rotulo.pesquisa.minhas.reservas"/></span>
                      </div>
                      <div class="form-check mt-2" role="radiogroup" aria-labelledby="listarMinhasConsignacoes">
                        <label class="form-check-label pr-3" for="adePropria_sim"> 
                          <input class="form-check-input" type="radio" name="adePropria" id="adePropria_sim" value="1" <%= (JspHelper.verificaVarQryStr(request, "adePropria").equals("1")) ? "checked" : "" %>/>
                          <span class="text-nowrap align-text-top"><hl:message key="rotulo.sim"/></span>
                        </label>
                        <label class="form-check-label" for="adePropria_nao">
                          <input class="form-check-input" type="radio" name="adePropria" id="adePropria_nao" value="0" <%=(!JspHelper.verificaVarQryStr(request, "adePropria").equals("1")) ? "checked" : "" %>/>
                          <span class="text-nowrap align-text-top"><hl:message key="rotulo.nao"/></span>
                        </label>
                      </div>
                    </div>
                  <% } %>

                <% if(ShowFieldHelper.exibeCampoUsuario(FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_ADE_INT_FOLHA, responsavel)){%>
                  <div class="col-sm-12 col-md-6">
                    <div class="form-group mb-0">
                      <span id="integrafolha"><hl:message key="rotulo.consignacao.integra.folha"/></span>
                    </div>
                    <div class="form-check mt-2" role="radiogroup" aria-labelledby="integrafolha">
                      <label class="form-check-label pr-3" style="padding-right: 1.5rem" for="ADE_INT_FOLHA_1">
                        <input class="form-check-input" type="radio" name="ADE_INT_FOLHA" id="ADE_INT_FOLHA_1" value="1" <%= (JspHelper.verificaVarQryStr(request, "ADE_INT_FOLHA").equals("1")) ? " checked " : "" %>/>
                        <span class="text-nowrap align-text-top"><hl:message key="rotulo.sim"/></span>
                      </label>
                      <label class="form-check-label" for="ADE_INT_FOLHA_0">
                        <input class="form-check-input" type="radio" name="ADE_INT_FOLHA" id="ADE_INT_FOLHA_0" value="0" <%= (JspHelper.verificaVarQryStr(request, "ADE_INT_FOLHA").equals("0")) ? " checked " : "" %>/>
                        <span class="text-nowrap align-text-top"><hl:message key="rotulo.nao"/></span>
                      </label>
                    </div>
                  </div>
                <% } %>

            <% if(ShowFieldHelper.exibeCampoUsuario(FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_INF_SALDO_DEVEDOR, responsavel)){%>
              <div class="col-sm-12 col-md-12">
                <h3 class="legend">
                </h3>
                  <div class="form-check">
                    <% String infSaldoDevedorBuscaAnterior = JspHelper.verificaVarQryStr(request, "infSaldoDevedor"); %>
                    <div class="row" role="radiogroup" aria-labelledby="saldoDevedor">
                      <div class="col-sm-12 col-md-12">
                        <label class="form-check-label" for="infSaldoDevedor_1">
                          <input class="form-check-input" type="radio" name="infSaldoDevedor" id="infSaldoDevedor_1" value="1" <%=(String)((infSaldoDevedorBuscaAnterior.equals("1")) ? "checked" : "")%> />
                          <span class="text-nowrap align-text-top"><hl:message key="mensagem.pesquisa.saldo.solicitado"/></span>
                        </label>
                      </div>
                      <div class="col-sm-12 col-md-12">
                        <label class="form-check-label" for="infSaldoDevedor_2">
                          <input class="form-check-input" type="radio" name="infSaldoDevedor" id="infSaldoDevedor_2" value="2" <%=(String)((infSaldoDevedorBuscaAnterior.equals("2")) ? "checked" : "")%> />
                          <span class="text-nowrap align-text-top"><hl:message key="mensagem.pesquisa.saldo.solicitado.nao.informado"/></span>
                        </label>
                      </div>
                      <div class="col-sm-12 col-md-12">
                        <label class="form-check-label" for="infSaldoDevedor_3">
                          <input class="form-check-input" type="radio" name="infSaldoDevedor" id="infSaldoDevedor_3" value="3" <%=(String)((infSaldoDevedorBuscaAnterior.equals("3")) ? "checked" : "")%> />
                          <span class="text-nowrap align-text-top"><hl:message key="mensagem.pesquisa.saldo.solicitado.informado"/></span>
                        </label>
                      </div>
                      <div class="col-sm-12 col-md-12">
                        <label class="form-check-label formatacao" for="infSaldoDevedor_4">
                          <input class="form-check-input" type="radio" name="infSaldoDevedor" id="infSaldoDevedor_4" value="4" <%=(String)((infSaldoDevedorBuscaAnterior.equals("4")) ? "checked" : "")%> /> 
                          <span class="text-nowrap align-text-top"><hl:message key="mensagem.pesquisa.saldo.nao.solicitado"/></span>
                        </label>
                      </div>
                    </div>
                  </div>
              </div>
            <% } %>
            </div>
          </div>
        </fieldset>

        <fieldset style="display:none;">
          <h3 class="legend">
            <span id="grupoOrdenacao"><hl:message key="rotulo.ordenacao"/></span>
          </h3>
          <div class="row">
            <% if(ShowFieldHelper.exibeCampoUsuario(FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_TIPO_ORDENACAO, responsavel)){%>
                <div class="col-sm-12 col-md-12">
                  <% String tipoOrdenacaoBuscaAnterior = JspHelper.verificaVarQryStr(request, "tipoOrdenacao"); %>
                  <div class="form-check">
                    <div class="row" role="radiogroup" aria-labelledby="grupoOrdenacao">
                      <div class="col-sm-12 col-md-12">
                        <label class="form-check-label" for="tipoOrdenacao_1">
                          <input class="form-check-input" type="radio" name="tipoOrdenacao" id="tipoOrdenacao_1" value="1" <%=(String)((tipoOrdenacaoBuscaAnterior.trim().isEmpty() || tipoOrdenacaoBuscaAnterior.equals("1")) ? "checked" : "")%> onclick="configurarOrdenacao()" />
                          <span class="text-nowrap align-text-top"><hl:message key="mensagem.informacao.ordenacao.padrao"/></span>
                        </label>
                      </div>
                      <div class="col-sm-12 col-md-12">
                        <label class="form-check-label" for="tipoOrdenacao_2">
                          <input class="form-check-input" type="radio" name="tipoOrdenacao" id="tipoOrdenacao_2" value="2" <%=(String)((tipoOrdenacaoBuscaAnterior.equals("2")) ? "checked" : "")%> onclick="configurarOrdenacao()" />
                          <span class="text-nowrap align-text-top"><hl:message key="mensagem.informacao.ordenacao.desconto.folha.asc"/></span>
                        </label>
                      </div>
                      <div class="col-sm-12 col-md-12">
                        <label class="form-check-label" for="tipoOrdenacao_3">
                          <input class="form-check-input" type="radio" name="tipoOrdenacao" id="tipoOrdenacao_3" value="3" <%=(String)((tipoOrdenacaoBuscaAnterior.equals("3")) ? "checked" : "")%> onclick="configurarOrdenacao()" />
                          <span class="text-nowrap align-text-top"><hl:message key="mensagem.informacao.ordenacao.desconto.folha.desc"/></span>
                        </label>
                      </div>
                      <div class="col-sm-12 col-md-12">
                        <label class="form-check-label formatacao" for="tipoOrdenacao_4">
                          <input class="form-check-input" type="radio" name="tipoOrdenacao" id="tipoOrdenacao_4" value="4" <%=(String)((tipoOrdenacaoBuscaAnterior.equals("4")) ? "checked" : "")%> onclick="configurarOrdenacao()" /> 
                          <span class="text-nowrap align-text-top"><hl:message key="mensagem.informacao.ordenacao.desconto.personalizada"/></span>
                        </label>
                      </div>
                    </div>
                  </div>
    
                  <div id="ordenacaoPersonalizada" class="row" style="display: none">
                    <div class="form-group col-sm-12 col-md-5">
                      <select class="form-control form-select w-100" id="ORDENACAO" name="ORDENACAO" size="4">
                        <%
                        // Se existir uma ordem já pré selecionada para a ordenação, monta a lista na ordem
                        if (lstOrdenacaoAux != null && !lstOrdenacaoAux.isEmpty()) {
                            Iterator iteOrdAux = lstOrdenacaoAux.iterator();
                            String ordAux = "";
                            while (iteOrdAux.hasNext()) {
                                ordAux = iteOrdAux.next().toString();
                                ordAux = ordAux.replaceAll("ASC|DESC", "").replaceAll("\\[|\\]|;", "").trim();
                                if (ordAux.equalsIgnoreCase("ORD01")) {
                        %>
                            <option VALUE="ORD01"><hl:message key="rotulo.consignacao.data"/></option>
                          <% } else if (ordAux.equalsIgnoreCase("ORD02")) { %>
                            <option VALUE="ORD02"><hl:message key="rotulo.servidor.cpf"/></option>
                          <% } else if (ordAux.equalsIgnoreCase("ORD03")) { %>
                            <option VALUE="ORD03"><hl:message key="rotulo.servidor.matricula"/></option>
                          <% } else if (ordAux.equalsIgnoreCase("ORD04")) { %>
                            <option VALUE="ORD04"><hl:message key="rotulo.servidor.nome"/></option>
                          <% } %>
                        <% } %>
                        <% } else { %>
                          <option VALUE="ORD01"><hl:message key="rotulo.consignacao.data"/></option>
                          <option VALUE="ORD02"><hl:message key="rotulo.servidor.cpf"/></option>
                          <option VALUE="ORD03"><hl:message key="rotulo.servidor.matricula"/></option>
                          <option VALUE="ORD04"><hl:message key="rotulo.servidor.nome"/></option>
                        <% } %>
                      </select>
                    </div>
                    <div class="form-group col-sm-12 col-md-1">
                      <a class="btn btn-primary btn-ordenacao pr-0" href="#no-back" onClick="move(document.forms[0].ORDENACAO, document.forms[0].ORDENACAO.selectedIndex, -1); atribui_ordenacao(); return false;"> 
                        <svg width="15">
                            <use xlink:href="../img/sprite.svg#i-avancar"></use>
                        </svg>
                      </a> 
                      <a class="btn btn-primary btn-ordenacao pr-0 mt-2" href="#no-back" onClick="move(document.forms[0].ORDENACAO, document.forms[0].ORDENACAO.selectedIndex, +1); atribui_ordenacao(); return false;">
                        <svg width="15">
                            <use xlink:href="../img/sprite.svg#i-voltar"></use>
                        </svg>
                      </a>
                    </div>
                    <div class="col-sm-12 col-md-6">
                      <div class="form-check">
                        <div class="row">
                          <div class="col-sm-12 col-md-12 mt-2">
                            <div class="row" role="radiogroup" aria-labelledby="rgData">
                              <div class="col-sm-12 col-md-3">
                                <div class="form-group my-0">
                                  <span class="mr-2 text-nowrap" id="rgData"><hl:message key="rotulo.consignacao.data"/></span>
                                </div>
                              </div>
                              <div class="col-sm-12 col-md-4">
                                <input class="form-check-input ml-1" type="radio" dir="ltr" name="ORDEM_DATA" id="ORDEM_DATA1" value="ASC" <%= ((!TextHelper.isNull(ordemAdeData) && ordemAdeData.equals("ASC"))) ? " checked " : "" %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"/>
                                <label class="form-check-label formatacao ml-1 pr-4" for="ORDEM_DATA1" aria-label='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ordenacao.data.asc", responsavel)%>'><hl:message key="rotulo.crescente"/></label>
                              </div>
                              <div class="col-sm-12 col-md-4">
                                <input class="form-check-input ml-1" type="radio" name="ORDEM_DATA" id="ORDEM_DATA2" value="DESC" <%= (TextHelper.isNull(ordemAdeData) || ordemAdeData.equals("DESC")) ? " checked " : "" %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"/>
                                <label class="form-check-label formatacao ml-1" for="ORDEM_DATA2" aria-label='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ordenacao.data.desc", responsavel)%>'><hl:message key="rotulo.decrescente"/></label>
                              </div>
                            </div>
                            <div class="row" role="radiogroup" aria-labelledby="rgCpf">
                              <div class="col-sm-12 col-md-3">
                                <div class="form-group my-0">
                                  <span class="mr-2 text-nowrap" id="rgCpf"><hl:message key="rotulo.servidor.cpf"/></span>
                                </div>
                              </div>
                              <div class="col-sm-12 col-md-4">
                                <input class="form-check-input ml-1" type="radio" dir="ltr" name="ORDEM_CPF" id="ORDEM_CPF1" value="ASC" <%= (TextHelper.isNull(ordemSerCpf) || (!TextHelper.isNull(ordemSerCpf) && ordemSerCpf.equals("ASC"))) ? " checked " : "" %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"/>
                                <label class="form-check-label formatacao ml-1 pr-4" for="ORDEM_CPF1" aria-label='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ordenacao.cpf.asc", responsavel)%>'><hl:message key="rotulo.crescente"/></label>
                              </div>
                              <div class="col-sm-12 col-md-4">
                                <input class="form-check-input ml-1" type="radio" name="ORDEM_CPF" id="ORDEM_CPF2" value="DESC" <%= ((!TextHelper.isNull(ordemSerCpf) && ordemSerCpf.equals("DESC"))) ? " checked " : "" %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"/>
                                <label class="form-check-label formatacao ml-1" for="ORDEM_CPF2" aria-label='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ordenacao.cpf.desc", responsavel)%>'><hl:message key="rotulo.decrescente"/></label>
                              </div>
                            </div>
                            <div class="row" role="radiogroup" aria-labelledby="rgMatricula">
                              <div class="col-sm-12 col-md-3">
                                <div class="form-group my-0">
                                  <span class="mr-2 text-nowrap" id="rgMatricula"><hl:message key="rotulo.servidor.matricula"/></span>
                                </div>
                              </div>
                              <div class="col-sm-12 col-md-4">
                                <input class="form-check-input ml-1" type="radio" dir="ltr" name="ORDEM_MATRICULA" id="ORDEM_MATRICULA1" value="ASC" <%= (TextHelper.isNull(ordemRseMatricula) || (!TextHelper.isNull(ordemRseMatricula) && ordemRseMatricula.equals("ASC"))) ? " checked " : "" %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"/>
                                <label class="form-check-label formatacao ml-1 pr-4" for="ORDEM_MATRICULA1" aria-label='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ordenacao.matricula.asc", responsavel)%>'><hl:message key="rotulo.crescente"/></label>
                              </div>
                              <div class="col-sm-12 col-md-4">
                                <input class="form-check-input ml-1" type="radio" name="ORDEM_MATRICULA" id="ORDEM_MATRICULA2" value="DESC" <%= ((!TextHelper.isNull(ordemRseMatricula) && ordemRseMatricula.equals("DESC"))) ? " checked " : "" %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"/>
                                <label class="form-check-label formatacao ml-1" for="ORDEM_MATRICULA2" aria-label='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ordenacao.matricula.desc", responsavel)%>'><hl:message key="rotulo.decrescente"/></label>
                              </div>
                            </div>
                            <div class="row" role="radiogroup" aria-labelledby="rgNome">
                              <div class="col-sm-12 col-md-3">
                                <div class="form-group my-0">
                                  <span class="mr-2 text-nowrap" id="rgNome"><hl:message key="rotulo.servidor.nome"/></span>
                                </div>
                              </div>
                              <div class="col-sm-12 col-md-4">
                                <input class="form-check-input ml-1" type="radio" dir="ltr" name="ORDEM_NOME" id="ORDEM_NOME1" value="ASC" <%= (TextHelper.isNull(ordemSerNome) ||  (!TextHelper.isNull(ordemSerNome) && ordemSerNome.equals("ASC"))) ? " checked " : "" %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"/>
                                <label class="form-check-label formatacao ml-1 pr-4" for="ORDEM_NOME1" aria-label='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ordenacao.nome.asc", responsavel)%>'><hl:message key="rotulo.crescente"/></label>
                              </div>
                              <div class="col-sm-12 col-md-4">
                                <input class="form-check-input ml-1" type="radio" name="ORDEM_NOME" id="ORDEM_NOME2" value="DESC" <%= ((!TextHelper.isNull(ordemSerNome) && ordemSerNome.equals("DESC"))) ? " checked " : "" %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"/>
                                <label class="form-check-label formatacao ml-1" for="ORDEM_NOME2" aria-label='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ordenacao.nome.desc", responsavel)%>'><hl:message key="rotulo.decrescente"/></label>
                              </div>
                            </div>
                            <hl:htmlinput type="hidden" name="ORDENACAO_AUX" di="ORDENACAO_AUX" value="" />                      
                            <hl:htmlinput type="hidden" name="DESC_ORDENACAO" di="DESC_ORDENACAO" value="" />                      
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
               <% } %>
          </div>
        </fieldset>
        <% if (exibeCaptcha || exibeCaptchaAvancado || exibeCaptchaDeficiente) { %>
             <div class="row"> 
             <% if (!exibeCaptchaAvancado) {%>
               <div class="form-group col-md-5">
                 <label for="captcha"><hl:message key="rotulo.captcha.codigo"/>:</label>
                 <input type="text" class="form-control" id="captcha" name="captcha" placeholder='<hl:message key="mensagem.informacao.login.digite.codigo.acesso"/>'>
               </div>
             <% } %>
               <div class="form-group col-sm-6">
                 <div class="captcha pl-3">
                   <% if (exibeCaptcha) { %>
                     <img name="captcha_img" src="../captcha.jpg?t=<%=DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss")%>" alt='<hl:message key="rotulo.captcha.codigo"/>' title='<hl:message key="rotulo.captcha.codigo"/>'/>
                       <div>
                       <a href="#no-back" onclick="reloadCaptcha()"><img src="../img/icones/refresh.png" alt='<hl:message key="rotulo.captcha.novo.codigo"/>' title='<hl:message key="rotulo.captcha.novo.codigo"/>' border="0"/></a>
                       <a href="#no-back" class="btn-i-right pr-1" data-bs-toggle="popover" title="<hl:message key="rotulo.ajuda" />"
                          data-bs-content='<hl:message key="mensagem.ajuda.captcha.operacao.imagem.v3"/>'
                          data-original-title=<hl:message key="rotulo.ajuda" />> 
                         <img src="../img/icones/help.png" alt='<hl:message key="rotulo.ajuda" />' title='<hl:message key="rotulo.ajuda" />' border="0">
                       </a>
                       </div>
                   <% } else if (exibeCaptchaAvancado) { %>
                       <hl:recaptcha />
                   <% } else if (exibeCaptchaDeficiente) {%>
                     <div id="divCaptchaSound"></div>
                     <a href="#no-back" onclick="reloadSimpleCaptcha()"><img src="../img/icones/refresh.png" alt='<hl:message key="rotulo.captcha.novo.audio"/>' title='<hl:message key="rotulo.captcha.novo.audio"/>' border="0"/></a>
                     <a href="#no-back" onclick="helpCaptcha3();"><img src="../img/icones/help.png" alt='<hl:message key="rotulo.ajuda"/>' title='<hl:message key="rotulo.ajuda"/>' border="0"/></a>
                   <% } %>
                 </div>
               </div>
             </div>
           <% } %>
      </form>
      
      <% if (!pesquisaAvancada) { %>
        <div class="row">
          <div class="form-group col-sm-12 col-md-6">
            <div class="alert alert-info">
              <p class="mb-0"><hl:message key="<%=JspHelper.getRotuloAjudaPesquisaServidor(requerMatriculaCpf, true, false, responsavel)%>"/></p>
            </div>
          </div>
        </div>
      <% } %>

    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-primary" id="btnPesquisar" href="#no-back" onClick="if(validaSubmit()){pesquisar(); return false;}"><svg width="20"><use xlink:href="../img/sprite.svg#i-consultar"></use></svg><hl:message key="rotulo.botao.pesquisar"/></a>
  </div>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript" src="../js/listutils.js?<hl:message key="release.tag"/>"></script>
<script type="text/JavaScript" src="../js/listagem.js?<hl:message key="release.tag"/>"></script>

<script language="JavaScript" type="text/JavaScript">
requerAmbos = <%=(boolean) requerMatriculaCpf%>;
requerDataNascimento = <%=TextHelper.forJavaScriptBlock(requerDataNascimento)%>;
console.log(requerDataNascimento);

function formLoad() {
  focusFirstField();
  showFieldSetIfNotEmpty();
  <% if(ShowFieldHelper.exibeCampoUsuario(FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_TIPO_ORDENACAO, responsavel)){%>
      configurarOrdenacao();
  <% } %>
  <% if (exibeCaptchaDeficiente) {%>
  montaCaptchaSom();
  <% } %>

  <%
  String[] adeNumeros = request.getParameterValues("ADE_NUMERO_LIST");
  if (adeNumeros != null && adeNumeros.length > 0) {
  %>
  var adeNumeros = [<%=TextHelper.forJavaScriptBlock(TextHelper.join(adeNumeros, ","))%>];
  loadSelectOptions(f0.ADE_NUMERO_LIST, adeNumeros, '', true);
  document.getElementById('MULT_ADE_NUMERO').style.display = '';
  document.getElementById('RSE_MATRICULA').disabled = true;
  document.getElementById('SER_CPF').disabled = true;
  <%
  }
  %>
}

function validaFormPesqAvancada() {
  with (document.formPesqAvancada) {
    if ((ADE_NUMERO != null && ADE_NUMERO.value != '') || (ADE_NUMERO_LIST != null && ADE_NUMERO_LIST.length > 0)) {
      // Se está pesquisando pelo ADE_NUMERO, não precisa informar 
      // mais nenhum outro critério
      return true;
    }
    
  <% if (!responsavel.isSer()) { %>
    
    if(!formPesqAvancada["ORG_CODIGO"] && !formPesqAvancada["CNV_COD_VERBA"] &&
            !formPesqAvancada["ADE_ANO_MES_INI"] && !formPesqAvancada["SAD_CODIGO"] &&
            (SER_CPF == null || SER_CPF.value == '') && (formPesqAvancada["RSE_MATRICULA"] != null && (RSE_MATRICULA == null || RSE_MATRICULA.value == ''))){
        alert('<hl:message key="mensagem.consultar.consignacao.avancada.campos.obrigatorios.1"/>');
        return false;
    }
        
    if ((formPesqAvancada["RSE_MATRICULA"] != null && RSE_MATRICULA != null && RSE_MATRICULA.value != '') || 
        (formPesqAvancada["SER_CPF"] != null && SER_CPF != null && SER_CPF.value != '') ||
        (formPesqAvancada["SER_DATA_NASC"] != null && SER_DATA_NASC != null && SER_DATA_NASC.value != '')) {
      // Se pelo menos a matrícula ou o CPF foram informados ...
      if (requerAmbos) {
        // Se requer matrícula e CPF, verifica se ambos foram informados
        var Controles = new Array("RSE_MATRICULA", "SER_CPF");
        var Msgs = new Array('<hl:message key="mensagem.informe.matricula"/>',
                             '<hl:message key="mensagem.informe.cpf"/>');
        if (!ValidaCampos(Controles, Msgs)) {
          return false;
        }
      }

      if (requerDataNascimento) {
        // Se requer data de nascimento, verifica se foi informada
        var Controles = new Array("SER_DATA_NASC");
        var Msgs = new Array('<hl:message key="mensagem.informe.servidor.data.nascimento"/>');
        if (!ValidaCampos(Controles, Msgs)) {
          return false;
        }
      }

      if (formPesqAvancada["SER_CPF"] != null && SER_CPF != null && SER_CPF.value != '' &&
         !CPF_OK(extraiNumCNPJCPF(SER_CPF.value))) {
        // Se o CPF foi informado, valida o CPF
        SER_CPF.focus();
        return false;
      }
      
      // Se OK, não precisa mais de nenhum critério
      return true;
    }
  
    <% if(ShowFieldHelper.exibeCampoUsuario(FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_CNV_COD_VERBA, responsavel)){%>
        if (CNV_COD_VERBA != null && CNV_COD_VERBA.value != '') {
          // Se está pesquisando pelo CNV_COD_VERBA, não precisa informar 
          // mais nenhum outro critério
          return true;
        }
        <% } %>
        
     <% if(ShowFieldHelper.exibeCampoUsuario(FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_SAD_CODIGO, responsavel)){%>
            if (SAD_CODIGO != null) {
                for (var i=0; i < SAD_CODIGO.length; i++) {
                  if (SAD_CODIGO[i].checked == true) {
                    return true;    
                  }
                }
                alert('<hl:message key="mensagem.consultar.consignacao.avancada.status.ade"/>');
                return false;
            }
    <% } %>
    
    if((!formPesqAvancada["CNV_COD_VERBA"] || CNV_COD_VERBA == null || CNV_COD_VERBA.value == '') && (SER_CPF == null || SER_CPF.value == '') && (formPesqAvancada["RSE_MATRICULA"] != null && (RSE_MATRICULA == null || RSE_MATRICULA.value == ''))
            && (!formPesqAvancada["ORG_CODIGO"] || !formPesqAvancada["CNV_COD_VERBA"] || !formPesqAvancada["ADE_ANO_MES_INI"])){
        alert('<hl:message key="mensagem.consultar.consignacao.avancada.campos.obrigatorios.2"/>');
        return false;
    }

    if(formPesqAvancada["ORG_CODIGO"] && formPesqAvancada["CNV_COD_VERBA"] && formPesqAvancada["ADE_ANO_MES_INI"]){
      // Se não matrícula/CPF e nem ade_numero os campos abaixo devem ser informados
      var Controles = new Array("ORG_CODIGO", "CNV_COD_VERBA", "ADE_ANO_MES_INI");
      var Msgs = new Array('<hl:message key="mensagem.informe.orgao"/>',
                           '<hl:message key="mensagem.informe.codigo.verba"/>',
                           '<hl:message key="mensagem.informe.ade.data.ini"/>');
      return ValidaCampos(Controles, Msgs);
    }
  <% } else { %>
    return true;    
  <% } %>
  }
}

function pesquisar() {
    <% if(ShowFieldHelper.exibeCampoUsuario(FieldKeysConstants.PESQUISA_AVANCADA_CONSIGNACAO_TIPO_ORDENACAO, responsavel)){%>
             atribui_ordenacao();
     <% } %>
    if (validaFormPesqAvancada()) { 
      with (document.formPesqAvancada) {
        <% if (exigeSenhaConsMargem) { %>
        if (senha != null && senha.value != '') {
          CriptografaSenha(senha, serAutorizacao, false);
        }
        <% } %>  
        selecionarTodosItens('ADE_NUMERO_LIST');
        submit();
      }
    }
}

function validaSubmit() {
  if(typeof vfRseMatricula === 'function') {
    return vfRseMatricula(true);
  } else {
      return true;
  }
}

function atribui_ordenacao() {
  var ordenacao = "";
  var descOrdenacao = "";
  with(document.forms[0]) {
     for (var i = 0; i < ORDENACAO.length; i++) {
       if (ORDENACAO.options[i].value == 'ORD01') {
           ordenacao += "[" + ORDENACAO.options[i].value + ";" + getCheckedRadio('formPesqAvancada', 'ORDEM_DATA') + "]";
       } else if (ORDENACAO.options[i].value == 'ORD02') {
           ordenacao += "[" + ORDENACAO.options[i].value + ";" + getCheckedRadio('formPesqAvancada', 'ORDEM_CPF') + "]";
       } else if (ORDENACAO.options[i].value == 'ORD03') {
           ordenacao += "[" + ORDENACAO.options[i].value + ";" + getCheckedRadio('formPesqAvancada', 'ORDEM_MATRICULA') + "]";
       } else if (ORDENACAO.options[i].value == 'ORD04') {
           ordenacao += "[" + ORDENACAO.options[i].value + ";" + getCheckedRadio('formPesqAvancada', 'ORDEM_NOME') + "]";
       }

       if (i < ORDENACAO.length - 1) {
           ordenacao += ", ";
       }
     }
     
     ORDENACAO_AUX.value = ordenacao;
  }
}         

function adicionaNumero() {
    var ade = document.getElementById('ADE_NUMERO').value;

    if (ade != '' && (/\D/.test(ade) || ade.length > 20)) {
        alert('<hl:message key="mensagem.erro.ade.numero.invalido"/>');
         return;
    }
    
    if (document.getElementById('ADE_NUMERO').value != '') {
      document.getElementById('adeLista').style.display = '';
      document.getElementById('removeAdeLista').style.display = '';
      document.getElementById('RSE_MATRICULA').disabled = true;
      document.getElementById('SER_CPF').disabled = true;
      insereItem('ADE_NUMERO', 'ADE_NUMERO_LIST');
    }
}

function removeNumero() {
    removeDaLista('ADE_NUMERO_LIST');
    if (document.getElementById('ADE_NUMERO_LIST').length == 0) {
        document.getElementById('adeLista').style.display = 'none';
        document.getElementById('removeAdeLista').style.display = 'none';
        document.getElementById('RSE_MATRICULA').disabled = false;
        document.getElementById('SER_CPF').disabled = false;
    }
}

function configurarOrdenacao() {
    var tipoOrdenacao = getCheckedRadio("formPesqAvancada", "tipoOrdenacao");
    if (tipoOrdenacao == '4') {
        document.getElementById('ordenacaoPersonalizada').style.display = '';
    } else {
        document.getElementById('ordenacaoPersonalizada').style.display = 'none';
    }
}

$('.dropdown-menu button').on('click', function (){
    var parametros = [];
    $('ul li input').each(function(){
        if($(this).is(":checked")){
            parametros.push({cauChave: $(this).attr('data-value'), cauValor: 'S'});
        }else{
            parametros.push({cauChave: $(this).attr('data-value'), cauValor: 'N'});
        }
    });
    postData("../v3/consultarConsignacao?acao=fixarCampos&campoUsuario=" + JSON.stringify(parametros));
});

<% for (Map.Entry<String, String> entry : mapCampoUsuario.entrySet()) {
    if (entry.getValue().equals("S")){ %>
        $( "input[data-value='<%= entry.getKey() %>']").prop("checked", true);
<% }else{ %>
        $( "input[data-value='<%= entry.getKey() %>']").prop("checked", false);
    <% }
} %>


$(document).on('click', '#dropDownMenu .dropdown-menu', function (e) {
      e.stopPropagation();
});

function showFieldSetIfNotEmpty(){
    $('fieldset').each(function(){
        if($(this).has('input').length > 0 || $(this).has('select').length > 0){
             $(this).css('display', 'block');
        }
    })
}

f0 = document.forms["formPesqAvancada"];
window.onload = formLoad;
</script>
<% if (!responsavel.isSer()) { %>
  <hl:campoMatriculav4 scriptOnly="true"/>
<% } %> 
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
