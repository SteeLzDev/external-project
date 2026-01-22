<%--
* <p>Title: pesquisarServidor_v4.jsp</p>
* <p>Description: pesquisa servidor/consignação</p>
* <p>Copyright: Copyright (c) 2002-2017</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String rotuloBotaoCancelar = ApplicationResourcesHelper.getMessage("rotulo.botao.cancelar", responsavel);
String rotuloBotaoPesquisar = ApplicationResourcesHelper.getMessage("rotulo.botao.pesquisar", responsavel);
String rotuloBotaoPesquisaAvancada = ApplicationResourcesHelper.getMessage("rotulo.botao.pesquisaAvancada", responsavel);
String rotuloBotaoListarTodos = ApplicationResourcesHelper.getMessage("rotulo.botao.listarTodos", responsavel);
String acaoRetorno = (String) request.getAttribute("acaoRetorno");
String rseCodigo = (String) request.getAttribute("rseCodigo");

boolean podeListarTodos = (request.getAttribute("exibirOpcaoListarTodos") != null);
boolean filtrosOpcionais = (request.getAttribute("filtrosOpcionais") != null);
// se adeNumero não é relevante para a pesquisa, enviar esse parâmetro como true para esta página 
boolean omitirAdeNumero = TextHelper.isNull(request.getAttribute("omitirAdeNumero")) ? false : (Boolean) request.getAttribute("omitirAdeNumero");

// Para pesquisa de solicitações, define se exibe filtro de solicitação com anexo pendente de validação
boolean exibeFiltroAnexPendValidacao = (request.getAttribute("filtrarSolicAnexoPendenteValidacao") != null);

// Parâmetro de obrigatoriedade de CPF e Matrícula
boolean requerMatriculaCpf = (request.getAttribute("requerMatriculaCpf") != null);

//Parâmetro de obrigatoriedade de data de nascimento
boolean requerDataNascimento = (request.getAttribute("requerDataNascimento") != null);

// Configura exibição do campo de senha
boolean exibirCampoSenha = request.getAttribute("exibirCampoSenha") != null || request.getAttribute("exibirCampoSenhaAutorizacao") != null;
boolean senhaParaAutorizacaoReserva = request.getAttribute("exibirCampoSenhaAutorizacao") != null;
boolean senhaObrigatoria = senhaParaAutorizacaoReserva || request.getAttribute("senhaObrigatoriaConsulta") != null;

boolean exibeCaptcha = TextHelper.isNull(request.getAttribute("exibeCaptcha")) ? false : (Boolean) request.getAttribute("exibeCaptcha");
boolean exibeCaptchaAvancado = TextHelper.isNull(request.getAttribute("exibeCaptchaAvancado")) ? false : (Boolean) request.getAttribute("exibeCaptchaAvancado");
boolean exibeCaptchaDeficiente = TextHelper.isNull(request.getAttribute("exibeCaptchaDeficiente")) ? false : (Boolean) request.getAttribute("exibeCaptchaDeficiente");
//Ajusta o campo de lista de natureza e lista de margem para o fluxo de alterar Multiplas Consignções
boolean alterarMultiplasConsignacoes = (request.getAttribute("alterarMultiplasConsignacoes") != null);

boolean existeLimiteServico = (request.getAttribute("existeLimiteServico") != null);
String rseMatricula = (String) request.getAttribute("rseMatricula");
boolean verificaAutorizacaoSemSenha = request.getAttribute("verificaAutorizacaoSemSenha") != null;

%>
<c:set var="title">
   <%= TextHelper.forHtml(request.getAttribute("tituloPagina")) %>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#<%= TextHelper.forHtml(request.getAttribute("imageHeader")) != null ? TextHelper.forHtml(request.getAttribute("imageHeader")) : "i-manutencao"%>"></use>
</c:set>
<c:set var="bodyContent">
  <form action="<%= TextHelper.forHtmlAttribute(request.getAttribute("acaoFormulario")) %>" method="post" name="form1" id="form1">
    <%if (!verificaAutorizacaoSemSenha){ %>
	    <%= SynchronizerToken.generateHtmlToken(request) %>
    <%} %>
    
    <hl:htmlinput type="hidden" name="acao" value="<%= TextHelper.forHtmlAttribute(request.getAttribute("proximaOperacao") != null ? request.getAttribute("proximaOperacao").toString() : "pesquisarServidor") %>" />
    <hl:htmlinput type="hidden" name="TIPO_LISTA" value="pesquisa"/>
    <% if (!TextHelper.isNull(rseCodigo)) { %>
    <hl:htmlinput type="hidden" name="RSE_CODIGO" di="RSE_CODIGO" value="<%=rseCodigo%>"/>
    <% } %>
    <% if (existeLimiteServico && !TextHelper.isNull(rseMatricula)) { %>
    <hl:htmlinput type="hidden" name="RSE_MATRICULA" value="<%=TextHelper.forHtmlAttribute(rseMatricula)%>"/>
    <% } %>
      
    <div class="card">
      <div class="card-header hasIcon">
        <span class="card-header-icon"><svg width="26">
            <use xlink:href="../img/sprite.svg#i-consultar"></use></svg>
        </span>
        <h2 class="card-header-title"><hl:message key="mensagem.pesquisa.titulo"/></h2>
      </div>
      <div class="card-body">
        <fieldset>
          <h3 class="legend">
            <span><hl:message key="rotulo.consultar.consignacao.dados.convenio"/></span>
          </h3>

          <c:if test="${not empty lstEstabelecimento}">
            <div class="row">
              <div class="form-group col-sm-12 col-md-6">
                <label for="EST_CODIGO"><hl:message key="rotulo.estabelecimento.singular"/></label>
                <hl:htmlcombo listName="lstEstabelecimento" name="EST_CODIGO" fieldValue="<%=Columns.EST_CODIGO%>" fieldLabel="<%=Columns.EST_NOME + ";" + Columns.EST_IDENTIFICADOR%>" notSelectedLabel='<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>' autoSelect="true" classe="form-control"/>
              </div>
            </div>
          </c:if>

          <c:if test="${not empty lstOrgao}">
            <div class="row">
              <div class="form-group col-sm-12 col-md-6">
                <label for="ORG_CODIGO"><hl:message key="rotulo.orgao.singular"/></label>
                <hl:htmlcombo listName="lstOrgao" name="ORG_CODIGO" fieldValue="<%=Columns.ORG_CODIGO%>" fieldLabel="<%=Columns.ORG_NOME + ";" + Columns.ORG_IDENTIFICADOR%>" notSelectedLabel='<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>' autoSelect="true" classe="form-control"/>
              </div>
            </div>
          </c:if>

          <c:choose>
            <c:when test="${not empty lstConsignataria}">
              <div class="row" id="listConsignataria">
                <div class="form-group col-sm-12 col-md-6">
                  <label for="CSA_CODIGO"><hl:message key="rotulo.consignataria.singular"/></label>
                  <hl:htmlcombo listName="lstConsignataria" name="CSA_CODIGO" fieldValue="<%=Columns.CSA_CODIGO%>" fieldLabel="<%=Columns.getColumnName(Columns.CSA_NOME_ABREV)  + ";" + Columns.CSA_IDENTIFICADOR%>" notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>" autoSelect="true" classe="form-control" onChange="${listagemDinamicaDeServicos ? 'filtrarServico(this.value)' : ''}"/>
                </div>
              </div>
            </c:when>
            <c:when test="${not empty lstConsignatariaMultipla}">
              <div class="row">
                <div class="form-group col-sm-12 col-md-6">
                  <label for="CSA_CODIGO"><hl:message key="rotulo.consignataria.singular"/></label>
                  <hl:htmlcombo listName="lstConsignatariaMultipla" name="CSA_CODIGO" fieldValue="<%=Columns.CSA_CODIGO%>" fieldLabel="<%=Columns.getColumnName(Columns.CSA_NOME_ABREV)  + ";" + Columns.CSA_IDENTIFICADOR%>" notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavel)%>" autoSelect="true" classe="form-control" size="5"/>
                </div>
              </div>
            </c:when>
          </c:choose>

          <c:if test="${not empty lstNaturezaSvc}">
            <div class="row">
              <div class="form-group col-sm-12 col-md-6">
                <label for="NSE_CODIGO"><hl:message key="rotulo.natureza.servico.titulo"/></label>
                <hl:htmlcombo listName="lstNaturezaSvc" name="NSE_CODIGO" fieldValue="<%=Columns.NSE_CODIGO%>" fieldLabel="<%=Columns.NSE_DESCRICAO%>" selectedValue="<%=!alterarMultiplasConsignacoes ? "": "true" %>" notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.todas", responsavel)%>" autoSelect="true" classe="form-control" size="4"/>
              </div>
            </div>
          </c:if>

          <c:choose>
            <c:when test="${not empty lstPlano}">
              <div class="row">
                <div class="form-group col-sm-12 col-md-6">
                  <label for="PLA_CODIGO"><hl:message key="rotulo.plano.singular"/></label>
                  <hl:htmlcombo listName="lstPlano" name="PLA_CODIGO" fieldValue="<%=Columns.PLA_CODIGO%>" fieldLabel="<%=Columns.PLA_DESCRICAO + ";" + Columns.PLA_IDENTIFICADOR%>" notSelectedLabel='<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>' autoSelect="true" classe="form-control"/>
                </div>
              </div>
            </c:when>
            <c:when test="${not empty lstServico or listagemDinamicaDeServicos}">
              <div class="row" id="listServicos">
                <div class="form-group col-sm-12 col-md-6">
                  <label for="SVC_CODIGO"><hl:message key="rotulo.servico.singular"/></label>
                  <hl:htmlcombo listName="lstServico" name="SVC_CODIGO" fieldValue="<%=Columns.SVC_CODIGO%>" fieldLabel="<%=Columns.SVC_DESCRICAO + ";" + Columns.SVC_IDENTIFICADOR%>" notSelectedLabel='<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>' autoSelect="true" classe="form-control"/>
                </div>
              </div>
            </c:when>
            <c:when test="${not empty lstServicoMultiplo}">
              <div class="row">
                <div class="form-group col-sm-12 col-md-6">
                  <label for="SVC_CODIGO"><hl:message key="rotulo.servico.singular"/></label>
                  <hl:htmlcombo listName="lstServicoMultiplo" name="SVC_CODIGO" fieldValue="<%=Columns.SVC_CODIGO%>" fieldLabel="<%=Columns.SVC_DESCRICAO + ";" + Columns.SVC_IDENTIFICADOR%>" notSelectedLabel='<%=ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavel)%>' autoSelect="true" classe="form-control" size="3"/>
                </div>
              </div>
            </c:when>
          </c:choose>
          
          <c:if test="${not empty lstMargens}">
            <div class="row">
              <div class="form-group col-sm-12 col-md-6">
                <label for="NSE_CODIGO"><hl:message key="rotulo.margem.singular"/></label>
                <hl:htmlcombo listName="lstMargens" name="MAR_CODIGO" fieldValue="<%=Columns.MAR_CODIGO%>" fieldLabel="<%=Columns.MAR_DESCRICAO%>" selectedValue="<%=!alterarMultiplasConsignacoes ? "": "true" %>" notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.todas", responsavel)%>" autoSelect="false" classe="form-control" size="4"/>
              </div>
            </div>
          </c:if>

        </fieldset>

        <fieldset>
          <h3 class="legend">
            <span><hl:message key="rotulo.consultar.consignacao.dados.consignacao"/></span>
          </h3>

          <% if (!omitirAdeNumero) { %>
            <div id="exibeAdeNumero">
	            <div class="row">
	              <div class="form-group col-sm-12 col-md-6">
	                <label for="ADE_NUMERO"><hl:message key="rotulo.consignacao.numero"/></label>
	                <hl:htmlinput name="ADE_NUMERO"
	                              di="ADE_NUMERO"
	                              type="text"
	                              classe="form-control w-100"
	                              mask="#D20"
	                              value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "ADE_NUMERO"))%>"
	                              placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ade.numero", responsavel)%>'
	                              nf="RSE_MATRICULA" 
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
	                <select class="form-control w-100" id="ADE_NUMERO_LIST" name="ADE_NUMERO_LIST" multiple="multiple" size="6"></select>
	              </div>
	            </div>
            </div>
          <% } %>
            
          <% if (request.getAttribute("exibirFiltroDataSolicitacao") != null) { %>
            <div class="row">
              <div class="form-group col-sm-12 col-md-6">
                <span id="dataSolicitacao"><hl:message key="rotulo.pesquisa.data.solicitacao"/></span>
                <div class="row" role="group" aria-labelledby="dataSolicitacao">
                  <div class="form-group col-sm-12 col-md-1">
                    <div class="float-left align-middle mt-4 form-control-label">
                      <span><hl:message key="rotulo.pesquisa.data.prefixo.inicio"/></span>
                    </div>
                  </div>
                  <div class="form-group col-sm-12 col-md-5">
                    <hl:htmlinput name="ocaDataIni" di="ocaDataIni" type="text" classe="form-control w-100" placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "ocaDataIni"))%>"/>
                  </div>
                  <div class="form-group col-sm-12 col-md-1">
                    <div class="float-left align-middle mt-4 form-control-label">
                      <span><hl:message key="rotulo.pesquisa.data.prefixo.fim"/></span>
                    </div>
                  </div>
                  <div class="form-group col-sm-12 col-md-5">
                    <hl:htmlinput name="ocaDataFim" di="ocaDataFim" type="text" classe="form-control w-100" placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "ocaDataFim"))%>"/>
                  </div>
                </div>
              </div>
            </div>                
          <% } else if (request.getAttribute("exibirFiltroDataInclusao") != null) { %>
            <div class="row">
              <div class="form-group col-sm-12 col-md-6">
                <span id="dataInclusao"><hl:message key="rotulo.pesquisa.data.inclusao"/></span>
                <div class="row" role="group" aria-labelledby="dataInclusao">
                  <div class="form-group col-sm-12 col-md-1">
                    <div class="float-left align-middle mt-4 form-control-label">
                      <span><hl:message key="rotulo.pesquisa.data.prefixo.inicio"/></span>
                    </div>
                  </div>
                  <div class="form-group col-sm-12 col-md-5">
                    <hl:htmlinput name="periodoIni" di="periodoIni" type="text" classe="form-control w-100" placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "periodoIni"))%>"/>
                  </div>
                  <div class="form-group col-sm-12 col-md-1">
                    <div class="float-left align-middle mt-4 form-control-label">
                      <span><hl:message key="rotulo.pesquisa.data.prefixo.fim"/></span>
                    </div>
                  </div>
                  <div class="form-group col-sm-12 col-md-5">
                    <hl:htmlinput name="periodoFim" di="periodoFim" type="text" classe="form-control w-100" placeHolder="<%=LocaleHelper.getDatePlaceHolder()%>" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "periodoFim"))%>"/>
                  </div>
                </div>
              </div>
            </div>                
          <% } %>
          
          <% if (request.getAttribute("exibirFiltroSituacaoContrato") != null) { %>
              <div class="row">
                <div class="col-sm-12 col-md-12">
                  <h3 class="legend">
                    <span id="situacaoContrato"><hl:message key="rotulo.consignacao.status.contrato"/></span>
                  </h3>
                  <hl:filtroStatusAdeTagv4 />
                </div>
              </div>
          <%} %>
          
          <% if (responsavel.isCsa() && exibeFiltroAnexPendValidacao) { %>
            <div class="row">
              <div class="form-group col-sm-12 col-md-6">
                <label for="temAnexoPendenteValidacao"><hl:message key="rotulo.pesquisa.possui.anexo.pendente.validacao"/></label>
                <input name="temAnexoPendenteValidacao" id="temAnexoPendenteValidacao" type="checkbox" value="true" onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"/>
              </div>
            </div>
          <% } %>

        </fieldset>

        <% if (!responsavel.isSer()) { %>
          <fieldset>
            <h3 class="legend">
              <span><hl:message key="rotulo.consultar.consignacao.dados.servidor"/></span>
            </h3>

            <div class="row">
              <div class="form-group col-sm-12 col-md-6">
                <hl:campoMatriculav4 placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.matricula", responsavel)%>' value='<%=existeLimiteServico && !TextHelper.isNull(rseMatricula) ? rseMatricula : "" %>' disabled="<%=existeLimiteServico && !TextHelper.isNull(rseMatricula)%>"/>
              </div>
            </div>

            <% if (exibirCampoSenha) { %>
              <div class="row">
                <div class="form-group col-sm-12 col-md-6">
                  <hl:campoCPFv4 placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cpf", responsavel)%>' disabled="<%=existeLimiteServico && !TextHelper.isNull(rseMatricula)%>" classe="form-control"/>
                </div>
              </div>
              <%
                 String mascaraLogin = (String) ParamSist.getInstance().getParam(CodedValues.TPC_MASCARA_LOGIN_EXTERNO_SERVIDOR, responsavel);
                 if (!TextHelper.isNull(mascaraLogin)) {
              %>
              <div class="row">
                <div class="form-group col-sm-12 col-md-6">
                  <label for="serLogin"><hl:message key="rotulo.usuario.servidor.singular"/>&nbsp;<hl:message key="rotulo.campo.opcional"/></label>
                  <hl:htmlinput name="serLogin"
                                di="serLogin"
                                type="text"
                                classe="Edit"
                                size="8"
                                mask="<%=TextHelper.forHtmlAttribute(mascaraLogin)%>"
                  />
                </div>
              </div>
              <% 
                 } 
              %>
              <%if (!verificaAutorizacaoSemSenha){ %>
	              <div class="row">
	                <div class="form-group col-sm-12 col-md-6">
	                  <hl:senhaServidorv4 senhaObrigatoria="<%=String.valueOf(senhaObrigatoria)%>" 
	                                      senhaParaAutorizacaoReserva="<%=String.valueOf(senhaParaAutorizacaoReserva)%>"
	                                      nomeCampoSenhaCriptografada="serAutorizacao"
	                                      nf="btnPesquisar"
	                                      classe="form-control"
	                                      separador2pontos="false" 
	                                      comTagDD="false"/>
	                </div>
	              </div>
              <%} %>
            <% } else { %>
              <div class="row">
                <div class="form-group col-sm-12 col-md-6">
                  <hl:campoCPFv4 placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cpf", responsavel)%>' classe="form-control" nf="btnPesquisar" disabled="<%=existeLimiteServico && !TextHelper.isNull(rseMatricula)%>"/>
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

        <fieldset>
          <h3 class="legend">
            <span><hl:message key="rotulo.consultar.consignacao.demais.informacoes"/></span>
          </h3>

          <% if (request.getAttribute("exibirCampoInfBancaria") != null && !verificaAutorizacaoSemSenha) { %>
            <div class="row">
              <div class="form-group col-sm-12 col-md-6">
                <span id="informacoesBancarias"><hl:message key="rotulo.servidor.informacoesbancarias"/></span>
                <div class="row" role="group" aria-labelledby="informacoesBancarias">
                  <div class="form-group col-sm-12 col-md-1">
                    <div class="float-left align-middle mt-4 form-control-label">
                      <span><hl:message key="rotulo.servidor.informacoesbancarias.banco.abreviado"/></span>
                    </div>
                  </div>
                  <div class="form-group col-sm-12 col-md-3">
                    <hl:htmlinput name="numBanco"
                                  type="text"
                                  classe="form-control w-100"
                                  di="numBanco"
                                  size="3"
                                  mask="#D3"
                                  placeHolder="000"
                                  value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "numBanco"))%>" />
                  </div>
                  <div class="form-group col-sm-12 col-md-1">
                    <div class="float-left align-middle mt-4 form-control-label">
                      <span><hl:message key="rotulo.servidor.informacoesbancarias.agencia.abreviado"/></span>
                    </div>
                  </div>
                  <div class="form-group col-sm-12 col-md-3">
                    <hl:htmlinput name="numAgencia"
                                  type="text"
                                  classe="form-control w-100"
                                  di="numAgencia"
                                  size="8"
                                  mask="#*30"
                                  placeHolder="00000"
                                  value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "numAgencia"))%>"/>
                  </div>
                  <div class="form-group col-sm-12 col-md-1">
                    <div class="float-left align-middle mt-4 form-control-label">
                      <span><hl:message key="rotulo.servidor.informacoesbancarias.conta.abreviado"/></span>
                    </div>
                  </div>
                  <div class="form-group col-sm-12 col-md-3">
                    <hl:htmlinput name="numConta"
                                  type="text"
                                  classe="form-control w-100"
                                  di="numConta"
                                  size="12"
                                  mask="#*40"
                                  placeHolder="00000000"
                                  value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "numConta"))%>"/>
                  </div>
                </div>
              </div>
            </div>                
          <% } %>

          <% if (request.getAttribute("exibirFiltroTipoSaldo") != null) { %>
            <div class="row">
              <div class="col-sm-12 col-md-12">
                <h3 class="legend">
                  <span id="tipoSaldoDevedor"><hl:message key="rotulo.solicitacao.saldo.devedor.tipo"/></span>
                </h3>
                <div class="form-check">
                  <div class="row" role="radiogroup" aria-labelledby="tipoSaldoDevedor">
                    <div class="col-sm-12 col-md-12">
                      <label class="form-check-label" for="tipoSolicitacaoSaldo_sdv">
                        <input name="tipoSolicitacaoSaldo" id="tipoSolicitacaoSaldo_sdv" type="radio" value="sdv" class="form-check-input" <%=TextHelper.isNull(JspHelper.verificaVarQryStr(request, "tipoSolicitacaoSaldo")) || JspHelper.verificaVarQryStr(request, "tipoSolicitacaoSaldo").equals("sdv") ? "checked " : "" %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"/>
                        <span class="text-nowrap align-text-top"><hl:message key="rotulo.solicitacao.saldo.devedor.tipo.consulta"/></span>
                      </label>
                    </div>
                    <div class="col-sm-12 col-md-12">
                      <label class="form-check-label" for="tipoSolicitacaoSaldo_liq">
                        <input name="tipoSolicitacaoSaldo" id="tipoSolicitacaoSaldo_liq" type="radio" value="liq" class="form-check-input" <%=JspHelper.verificaVarQryStr(request, "tipoSolicitacaoSaldo").equals("liq") ? "checked " : "" %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"/>
                        <span class="text-nowrap align-text-top"><hl:message key="rotulo.solicitacao.saldo.devedor.tipo.liquidacao"/></span>
                      </label>
                    </div>
                    <% if (ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_SALDO_DEVEDOR_EXCLUSAO_SERVIDOR, responsavel)) { %>
                    <div class="col-sm-12 col-md-12">
                      <label class="form-check-label" for="tipoSolicitacaoSaldo_exc">
                        <input name="tipoSolicitacaoSaldo" id="tipoSolicitacaoSaldo_exc" type="radio" value="exc" class="form-check-input" <%=JspHelper.verificaVarQryStr(request, "tipoSolicitacaoSaldo").equals("exc") ? "checked " : "" %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"/>
                        <span class="text-nowrap align-text-top"><hl:message key="rotulo.solicitacao.saldo.devedor.tipo.exclusao"/></span>
                      </label>
                    </div>
                    <% } %>
                  </div>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="col-sm-12 col-md-12">
                <h3 class="legend">
                  <span id="statusSaldoDevedor"><hl:message key="rotulo.solicitacao.saldo.devedor.status"/></span>
                </h3>
                <div class="form-group">
                    <div class="form-check ">
                        <input name="situacaoSolicitacaoSaldo" id="situacaoSolicitacaoSaldo_0" type="radio" value="0" class="form-check-input" <%=TextHelper.isNull(JspHelper.verificaVarQryStr(request, "situacaoSolicitacaoSaldo")) || JspHelper.verificaVarQryStr(request, "situacaoSolicitacaoSaldo").equals("0") ? "checked " : "" %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"/>
                        <label class="form-check-label labelSemNegrito" for="situacaoSolicitacaoSaldo_0"><hl:message key="rotulo.solicitacao.saldo.devedor.status.todas"/></label>
                      </div>
                    <div class="form-check">
                        <input name="situacaoSolicitacaoSaldo" id="situacaoSolicitacaoSaldo_1" type="radio" value="1" class="form-check-input" <%=JspHelper.verificaVarQryStr(request, "situacaoSolicitacaoSaldo").equals("1") ? "checked " : "" %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"/>
                        <label class="form-check-label labelSemNegrito" for="situacaoSolicitacaoSaldo_1"><hl:message key="rotulo.solicitacao.saldo.devedor.status.bloqueadas"/></label>
                        <input name="diasSolicitacaoSaldo" id="diasSolicitacaoSaldo" type="text" class="form-control w-50 p-3" value="<%=TextHelper.forHtmlAttribute(request.getParameter("diasSolicitacaoSaldo") != null ? request.getParameter("diasSolicitacaoSaldo") : "0")%>" size="2" maxlength="2" onFocus="SetarEventoMascara(this,'#D2',true);" onBlur="fout(this);ValidaMascara(this);"/>
                    </div>
                    <% if (ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_BLOQ_CSA_N_LIQ_ADE_SALDO_PAGO_SER, responsavel)) { %>
                    <div class="form-check">
                        <input name="situacaoSolicitacaoSaldo" id="situacaoSolicitacaoSaldo_2" type="radio" value="2" class="form-check-input" <%=JspHelper.verificaVarQryStr(request, "situacaoSolicitacaoSaldo").equals("2") ? "checked " : "" %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"/>
                        <label class="form-check-label labelSemNegrito" for="situacaoSolicitacaoSaldo_2"><hl:message key="rotulo.solicitacao.saldo.devedor.status.bloqueadas.respondidas"/></label>
                        <input name="diasSolicitacaoSaldoPagaAnexo" id="diasSolicitacaoSaldoPagaAnexo" type="text" class="form-control w-50 p-3" value="<%=TextHelper.forHtmlAttribute(request.getParameter("diasSolicitacaoSaldoPagaAnexo") != null ? request.getParameter("diasSolicitacaoSaldoPagaAnexo") : "0")%>" size="2" maxlength="2" onFocus="SetarEventoMascara(this,'#D2',true);" onBlur="fout(this);ValidaMascara(this);"/>
                    </div>
                    <% } %>
                </div>
              </div>
            </div>
          <% } %>

          <% if (request.getAttribute("exibirTipoDecisaoJudicial") != null) { %>
            <div class="row">
              <div class="col-sm-12 col-md-12">
                <h3 class="legend">
                  <span id="tipoDecisaoJudicial"><hl:message key="rotulo.decisao.judicial.opcao"/></span>
                </h3>
                <div class="form-check">
                  <div class="row" role="radiogroup" aria-labelledby="tipoDecisaoJudicial">
                    <%-- DESENV-13999 : Exército - Excluir Opção de "Pensão Judicial/Compulsório" da Função Decisão Judicial
                    <div class="col-sm-12 col-md-12">
                      <label class="form-check-label" for="tipoDecisaoJudicial_1">
                        <input name="tipoDecisaoJudicial" id="tipoDecisaoJudicial_1" type="radio" value="<%=CodedValues.DECISAO_JUDICIAL_OPCAO_PENSAO_JUDICIAL%>" class="form-check-input" <%=JspHelper.verificaVarQryStr(request, "tipoDecisaoJudicial").equals(CodedValues.DECISAO_JUDICIAL_OPCAO_PENSAO_JUDICIAL) ? "checked " : "" %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"/>
                        <span class="text-nowrap align-text-top"><hl:message key="rotulo.decisao.judicial.opcao.pensao.judicial"/></span>
                      </label>
                    </div>
                    --%>
                    <div class="col-sm-12 col-md-12">
                      <label class="form-check-label" for="tipoDecisaoJudicial_2">
                        <input name="tipoDecisaoJudicial" id="tipoDecisaoJudicial_2"  onClick="incluirAdeDecisaoJucidicial(false);" type="radio" value="<%=CodedValues.DECISAO_JUDICIAL_OPCAO_EXCLUIR_CONSIGNACAO%>" class="form-check-input" <%=JspHelper.verificaVarQryStr(request, "tipoDecisaoJudicial").equals(CodedValues.DECISAO_JUDICIAL_OPCAO_EXCLUIR_CONSIGNACAO) ? "checked " : "" %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"/>
                        <span class="text-nowrap align-text-top"><hl:message key="rotulo.decisao.judicial.opcao.excluir.consignacao"/></span>
                      </label>
                    </div>
                    <div class="col-sm-12 col-md-12">
                      <label class="form-check-label" for="tipoDecisaoJudicial_3">
                        <input name="tipoDecisaoJudicial" id="tipoDecisaoJudicial_3"  onClick="incluirAdeDecisaoJucidicial(false);" type="radio" value="<%=CodedValues.DECISAO_JUDICIAL_OPCAO_ADEQUACAO_MARGEM%>" class="form-check-input" <%=JspHelper.verificaVarQryStr(request, "tipoDecisaoJudicial").equals(CodedValues.DECISAO_JUDICIAL_OPCAO_ADEQUACAO_MARGEM) ? "checked " : "" %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"/>
                        <span class="text-nowrap align-text-top"><hl:message key="rotulo.decisao.judicial.opcao.adequacao.margem"/></span>
                      </label>
                    </div>
                    <div class="col-sm-12 col-md-12">
                      <label class="form-check-label" for="tipoDecisaoJudicial_4">
                        <input name="tipoDecisaoJudicial" id="tipoDecisaoJudicial_4"  onClick="incluirAdeDecisaoJucidicial(false);" type="radio" value="<%=CodedValues.DECISAO_JUDICIAL_OPCAO_ALTERAR_CONSIGNACAO%>" class="form-check-input" <%=JspHelper.verificaVarQryStr(request, "tipoDecisaoJudicial").equals(CodedValues.DECISAO_JUDICIAL_OPCAO_ALTERAR_CONSIGNACAO) ? "checked " : "" %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"/>
                        <span class="text-nowrap align-text-top"><hl:message key="rotulo.decisao.judicial.opcao.alterar.consignacao"/></span>
                      </label>
                    </div>
                    <div class="col-sm-12 col-md-12">
                      <label class="form-check-label" for="tipoDecisaoJudicial_5">
                        <input name="tipoDecisaoJudicial" id="tipoDecisaoJudicial_5"  onClick="incluirAdeDecisaoJucidicial(false);" type="radio" value="<%=CodedValues.DECISAO_JUDICIAL_OPCAO_REATIVAR_CONSIGNACAO%>" class="form-check-input" <%=JspHelper.verificaVarQryStr(request, "tipoDecisaoJudicial").equals(CodedValues.DECISAO_JUDICIAL_OPCAO_REATIVAR_CONSIGNACAO) ? "checked " : "" %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"/>
                        <span class="text-nowrap align-text-top"><hl:message key="rotulo.decisao.judicial.opcao.reativar.consignacao"/></span>
                      </label>
                    </div>
                    <%if(responsavel.isCseSup()) {%>
                        <div class="col-sm-12 col-md-12">
                          <label class="form-check-label" for="tipoDecisaoJudicial_6">
                            <input name="tipoDecisaoJudicial" id="tipoDecisaoJudicial_6" onClick="incluirAdeDecisaoJucidicial(false);" type="radio" value="<%=CodedValues.DECISAO_JUDICIAL_OPCAO_AUTORIZAR_CONSIGNACAO%>" class="form-check-input" <%=JspHelper.verificaVarQryStr(request, "tipoDecisaoJudicial").equals(CodedValues.DECISAO_JUDICIAL_OPCAO_AUTORIZAR_CONSIGNACAO) ? "checked " : "" %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"/>
                            <span class="text-nowrap align-text-top"><hl:message key="rotulo.decisao.judicial.opcao.autorizar.consignacao"/></span>
                          </label>
                        </div>
                    <%} %>
                    <div class="col-sm-12 col-md-12">
                      <label class="form-check-label" for="tipoDecisaoJudicial_7">
                        <input name="tipoDecisaoJudicial" id="tipoDecisaoJudicial_7" onClick="incluirAdeDecisaoJucidicial(true);" type="radio" value="<%=CodedValues.DECISAO_JUDICIAL_OPCAO_INCLUIR_CONSIGNACAO%>" class="form-check-input" <%=JspHelper.verificaVarQryStr(request, "tipoDecisaoJudicial").equals(CodedValues.DECISAO_JUDICIAL_OPCAO_INCLUIR_CONSIGNACAO) ? "checked " : "" %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"/>
                        <span class="text-nowrap align-text-top"><hl:message key="rotulo.decisao.judicial.opcao.incluir.consignacao"/></span>
                      </label>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          <% } %>
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
        <div class="row">
          <div class="form-group col-sm-12 col-md-6">
            <div class="alert alert-info">
              <p class="mb-0"><hl:message key="<%=JspHelper.getRotuloAjudaPesquisaServidor(requerMatriculaCpf, !omitirAdeNumero, false, responsavel)%>"/></p>
            </div>
          </div>
        </div>
      </div>
    </div>
    <%-- Inclusão Avançada --%>
    <% if (responsavel.temPermissao(CodedValues.FUN_INCLUSAO_AVANCADA_CONSIGNACAO) && CodedValues.FUN_RES_MARGEM.equals(responsavel.getFunCodigo())) { %>
        <%@ include file="../reservarMargem/incluirCamposInclusaoAvancada_v4.jsp" %>
    <% } %>
    
    <%if (verificaAutorizacaoSemSenha){ %>
	    <%--Modal Senha Servidor --%>
		<div class="modal fade" id="modalSenhaSer" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true" >
		   <div class="modal-dialog" role="document">
		     <div class="modal-content">
		       <div class="modal-header pb-0">
		         <span class="modal-title about-title mb-0" id="exampleModalLabel"><hl:message key="mensagem.reservar.margem.senha.servidor.obrigatoria"/></span>
		         <button type="button" class="logout mr-1 d-print-none" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
		           <span aria-hidden="true">&times;</span>
		         </button>
		       </div>
		      <div class="modal-body">
			      <div class="alert alert-warning mb-3" id="msgAlertFim" role="alert" style="display: none;">
		    	 	 <p class="mb-1"><hl:message key="mensagem.consulta.margem.sem.senha.retirado"/></p> 
		    	  </div>
	    
		      	  <hl:senhaServidorv4 senhaObrigatoria="<%=String.valueOf(senhaObrigatoria)%>" 
		                                   senhaParaAutorizacaoReserva="<%=String.valueOf(senhaParaAutorizacaoReserva)%>"
		                                   nomeCampoSenhaCriptografada="serAutorizacao"
		                                   nf="btnPesquisar"
		                                   classe="form-control"
		                                   separador2pontos="false" 
		                                   comTagDD="false"/>
		                                   
		                                   
		           <% if (request.getAttribute("exibirCampoInfBancaria") != null) { %>
		            <div class="row">
		              <div class="form-group-pass-info-bank">
		                <b><span id="informacoesBancarias"><hl:message key="rotulo.servidor.informacoesbancarias"/></span></b>
		                <div class="row" role="group" aria-labelledby="informacoesBancarias">
		                  <div class="form-group col-sm-12 col-md-1">
		                    <div class="float-left align-middle mt-4 form-control-label">
		                      <span><hl:message key="rotulo.servidor.informacoesbancarias.banco.abreviado"/></span>
		                    </div>
		                  </div>
		                  <div class="form-group col-sm-12 col-md-3">
		                    <hl:htmlinput name="numBanco"
		                                  type="text"
		                                  classe="form-control w-100"
		                                  di="numBanco"
		                                  size="3"
		                                  mask="#D3"
		                                  placeHolder="000"
		                                  value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "numBanco"))%>" />
		                  </div>
		                  <div class="form-group col-sm-12 col-md-1">
		                    <div class="float-left align-middle mt-4 form-control-label">
		                      <span><hl:message key="rotulo.servidor.informacoesbancarias.agencia.abreviado"/></span>
		                    </div>
		                  </div>
		                  <div class="form-group col-sm-12 col-md-3">
		                    <hl:htmlinput name="numAgencia"
		                                  type="text"
		                                  classe="form-control w-100"
		                                  di="numAgencia"
		                                  size="8"
		                                  mask="#*30"
		                                  placeHolder="00000"
		                                  value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "numAgencia"))%>"/>
		                  </div>
		                  <div class="form-group col-sm-12 col-md-1">
		                    <div class="float-left align-middle mt-4 form-control-label">
		                      <span><hl:message key="rotulo.servidor.informacoesbancarias.conta.abreviado"/></span>
		                    </div>
		                  </div>
		                  <div class="form-group col-sm-12 col-md-3">
		                    <hl:htmlinput name="numConta"
		                                  type="text"
		                                  classe="form-control w-100"
		                                  di="numConta"
		                                  size="12"
		                                  mask="#*40"
		                                  placeHolder="00000000"
		                                  value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "numConta"))%>"/>
		                  </div>
		                </div>
		              </div>
		            </div>                
		          <% } %>
		      </div>
		      <div class="modal-footer pt-0">
		        <div class="btn-action mt-2 mb-0">
		          <a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.cancelar"/>' href="#" alt="<hl:message key="rotulo.botao.cancelar"/>" title="<hl:message key="rotulo.botao.cancelar"/>">
		            <hl:message key="rotulo.botao.cancelar" />
		          </a>
   		          <a class="btn btn-primary" id="btnPesquisar" href="#no-back" onClick="if(validaSubmit() && validarCamposOpcionais(true)){pesquisar(false); return false;} else { return false;}"><hl:message key="rotulo.botao.confirmar"/></a>
		        </div>
		      </div>
		    </div>
		  </div>
		</div>
    <%} %>
  </form>
  <div class="btn-action">
      <a class="btn btn-outline-danger" id="btnVoltar" href="#no-back" onClick="postData('<%=acaoRetorno%>'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
    <% if (request.getAttribute("exibirPesquisaAvancada") != null) { %>
    <a class="btn btn-primary" id="btnPesqAvancada" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL("../v3/consultarConsignacao?acao=filtrar", request))%>'); return false;"><%=TextHelper.forHtmlAttribute(rotuloBotaoPesquisaAvancada)%></a>
    <% } %>
    <% if (podeListarTodos) { %>
    <a class="btn btn-third" id="btnListarTudo" href="#no-back" onClick="listarTudo(); return false;"><%=TextHelper.forHtmlAttribute(rotuloBotaoListarTodos)%></a>
    <% } %>
    <% if (alterarMultiplasConsignacoes) { %>
    <a class="btn btn-primary" id="btnListarRelAltMultipla" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL("../v3/listarRelatorio?tipo=alteracao_multiplas_ade", request))%>'); return false;"><hl:message key="rotulo.alterar.multiplo.consignacao.listar.relatorios"/></a>
    <% } %>
    <%if (verificaAutorizacaoSemSenha){ %>
    	<a class="btn btn-primary" href="#no-back" onClick="validaAutorizacaoSemSenha();"><svg width="20"><use xlink:href="../img/sprite.svg#i-consultar"></use></svg><%=TextHelper.forHtmlAttribute(rotuloBotaoPesquisar)%></a>
    <%} else { %>
	    <a class="btn btn-primary" id="btnPesquisar" href="#no-back" onClick="if(validaSubmit()){pesquisar(true); return false;} else { return false;}"><svg width="20"><use xlink:href="../img/sprite.svg#i-consultar"></use></svg><%=TextHelper.forHtmlAttribute(rotuloBotaoPesquisar)%></a>
    <%} %>
  </div>
</c:set>
<c:set var="javascript">
  <script type="text/JavaScript" src="../js/listutils.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript" src="../js/listagem.js?<hl:message key="release.tag"/>"></script>
<script type="text/JavaScript">
f0 = document.forms["form1"];

function formLoad() {
  focusFirstField();
  hideEmptyFieldSet();  
  <% if (exibeCaptchaDeficiente) {%>
  montaCaptchaSom();
  <% } %>
}

function validarCamposOpcionais(senhaNaoObrigatoria) {
  var senhaObrigatoria = <%=senhaObrigatoria%> && senhaNaoObrigatoria;

  if (f0.senha != null && f0.senha.value == '' && senhaObrigatoria) {
    alert("<hl:message key="mensagem.informe.ser.senha"/>");
    f0.senha.focus();
    return false;
  }
  if ((f0.ocaDataIni != null && f0.ocaDataIni.value != '') && (!verificaData(f0.ocaDataIni.value))) {
    f0.ocaDataIni.focus();
    return false;
  }
  if ((f0.ocaDataFim != null && f0.ocaDataFim.value != '') && (!verificaData(f0.ocaDataFim.value))) {
    f0.ocaDataFim.focus();
    return false;
  }
  if ((f0.periodoIni != null && f0.periodoIni.value != '') && (!verificaData(f0.periodoIni.value))) {
    f0.periodoIni.focus();
    return false;
  }
  if ((f0.periodoFim != null && f0.periodoFim.value != '') && (!verificaData(f0.periodoFim.value))) {
    f0.periodoFim.focus();
    return false;
  }
  if (f0.numBanco != null && f0.numBanco.value == '' && senhaObrigatoria) {
    alert("<hl:message key="mensagem.informacaoBancariaObrigatoria"/>");
    f0.numBanco.focus();
    return false;
  }
  if (f0.numAgencia != null && f0.numAgencia.value == '' && senhaObrigatoria) {
    alert("<hl:message key="mensagem.informacaoBancariaObrigatoria"/>");
    f0.numAgencia.focus();
    return false;
  }
  if (f0.numConta != null && f0.numConta.value == '' && senhaObrigatoria) {
    alert("<hl:message key="mensagem.informacaoBancariaObrigatoria"/>");
    f0.numConta.focus();
    return false;
  }
  if (f0.tipoSolicitacaoSaldo != null && getCheckedRadio("form1", "tipoSolicitacaoSaldo") == null) {
    alert ("<hl:message key="mensagem.solicitacao.saldo.devedor.tipo.selecionar"/>");
    return false;
  }
  return true;
}

function validarCamposObrigatorios() {
  var requerAmbos = <%=TextHelper.forJavaScriptBlock(requerMatriculaCpf)%>;
  var requerDataNascimento = <%=TextHelper.forJavaScriptBlock(requerDataNascimento)%>;
  var filtrosOpcionais = <%=(boolean) (filtrosOpcionais || podeListarTodos)%>;
  <% if (request.getAttribute("exibirTipoDecisaoJudicial") != null) { %>
	const tipoDecisaoJudicial7 = document.getElementById("tipoDecisaoJudicial_7");
	if (tipoDecisaoJudicial7.chechked) {
		var Controles = new Array("ORG_CODIGO", "CSA_CODIGO", "SVC_CODIGO", "PLA_CODIGO");
		var Msgs = new Array(mensagem('mensagem.informe.orgao'),
		                       mensagem('mensagem.informe.consignataria'),
		                       mensagem('mensagem.informe.servico'),
		                       mensagem('mensagem.informe.plano'));
		  
		if (!filtrosOpcionais && !ValidaCampos(Controles, Msgs)) {
		      return false;
		}
	}
  <%} else { %>
	  var Controles = new Array("ORG_CODIGO", "CSA_CODIGO", "SVC_CODIGO", "PLA_CODIGO");
	  var Msgs = new Array(mensagem('mensagem.informe.orgao'),
	                       mensagem('mensagem.informe.consignataria'),
	                       mensagem('mensagem.informe.servico'),
	                       mensagem('mensagem.informe.plano'));
	  
	  if (!filtrosOpcionais && !ValidaCampos(Controles, Msgs)) {
	      return false;
	  }
  <%}%>
  if (f0.ADE_NUMERO != null) {
    if (f0.ADE_NUMERO.value != '') {
  	  <% if (request.getAttribute("exibirTipoDecisaoJudicial") != null) { %>
  	  if (getCheckedRadio("form1", "tipoDecisaoJudicial") == null) {
  	    alert("<hl:message key="mensagem.decisao.judicial.selecione.opcao"/>");
  	    return false;
  	  }
  	  <% } %>
      return true;
    } else if (f0.ADE_NUMERO_LIST != null && f0.ADE_NUMERO_LIST.length > 0) {
  	  <% if (request.getAttribute("exibirTipoDecisaoJudicial") != null) { %>
  	  if (getCheckedRadio("form1", "tipoDecisaoJudicial") == null) {
  	    alert("<hl:message key="mensagem.decisao.judicial.selecione.opcao"/>");
  	    return false;
  	  }
  	  <% } %>
      return true;
    }
  }

  if (requerAmbos) {
    if (f0.RSE_MATRICULA != null && f0.RSE_MATRICULA.value == '') {
      f0.RSE_MATRICULA.focus();
      alert(mensagem('mensagem.informe.matricula'));
      return false;
    } else if (f0.SER_CPF != null && f0.SER_CPF.value == '') {
      f0.SER_CPF.focus();
      alert(mensagem('mensagem.informe.cpf'));
      return false;
    }
  } else {
    if ((f0.RSE_MATRICULA == null || f0.RSE_MATRICULA.value == '') &&
        (f0.SER_CPF == null || f0.SER_CPF.value == '')) {
      if (f0.RSE_MATRICULA != null) {
        f0.RSE_MATRICULA.focus();
        alert(mensagem('mensagem.informe.matricula'));
        return false;
      } else if (f0.SER_CPF != null) {
        f0.SER_CPF.focus();
        alert(mensagem('mensagem.informe.cpf'));
        return false;
      }
    }
  }
  
  if (f0.SER_CPF != null && f0.SER_CPF.value != '' && !CPF_OK(extraiNumCNPJCPF(f0.SER_CPF.value))) {
    f0.SER_CPF.focus();
    return false;
  }
  
  if (requerDataNascimento && f0.SER_DATA_NASC != null) {
    // Se requer data de nascimento, verifica se foi informada
    var Controles = new Array("SER_DATA_NASC");
    var Msgs = new Array('<hl:message key="mensagem.informe.servidor.data.nascimento"/>');
    if (!ValidaCampos(Controles, Msgs)) {
      return false;
    }
  }
  
  <% if (responsavel.temPermissao(CodedValues.FUN_INCLUSAO_AVANCADA_CONSIGNACAO) && CodedValues.FUN_RES_MARGEM.equals(responsavel.getFunCodigo())) { %>
  if (!validarOpcoesAvancadas()) {
    return false;
  }
  <% } %>

  <% if (request.getAttribute("exibirTipoDecisaoJudicial") != null) { %>
  if (getCheckedRadio("form1", "tipoDecisaoJudicial") == null) {
    alert("<hl:message key="mensagem.decisao.judicial.selecione.opcao"/>");
    return false;
  }
  <% } %>

  <% if (alterarMultiplasConsignacoes && responsavel.isCseSupOrg()) { %>
        var lstNatureza = document.getElementById("NSE_CODIGO");
        var lstMargens = document.getElementById("MAR_CODIGO");
        if (lstNatureza.options[lstNatureza.selectedIndex] == null || lstNatureza.options[lstNatureza.selectedIndex] == 'undefined'){
        	alert("<hl:message key="mensagem.aviso.alterar.multiplo.consignacao.natureza.servico.obrigatorio"/>");
            return false;
        } else if (lstMargens.options[lstMargens.selectedIndex] == null || lstMargens.options[lstMargens.selectedIndex] == 'undefined'){
          alert("<hl:message key="mensagem.aviso.alterar.multiplo.consignacao.margem.obrigatorio"/>");
            return false;
        }
  <% } %>
  
  return true;
}

function pesquisar(senhaNaoObrigatoria) {
  if (validarCamposObrigatorios() && validarCamposOpcionais(senhaNaoObrigatoria)) {
    if (f0.senha != null && f0.senha.value != '') {
      CriptografaSenha(f0.senha, f0.serAutorizacao, false);
    }
    selecionarTodosItens('ADE_NUMERO_LIST');
     if(!senhaNaoObrigatoria){
    	const inputToken = '<%= SynchronizerToken.generateHtmlToken(request) %>';
		let criandoDOM = document.createElement('div');
		criandoDOM.innerHTML = inputToken;
		let inputForm = criandoDOM.firstElementChild;

		f0.appendChild(inputForm);
    }

    f0.submit();
  }
}

function validaSubmit() {  
  if (typeof vfRseMatricula === 'function') {
    return vfRseMatricula(true);
  } else {
    return true;
  }
}


function listarTudo() {
  if (validarCamposOpcionais(true)) {
    f0.TIPO_LISTA.value = 'TUDO';
    selecionarTodosItens('ADE_NUMERO_LIST');
    f0.submit();
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

function filtrarServico(csaCodigo) {
	$.post('<%=TextHelper.forHtmlAttribute(request.getAttribute("acaoFormulario")) %>', "acao=listarServicos&_skip_history_=true&CSA_CODIGO=" + csaCodigo, function(data) {
        try {
          var trimData = $.trim(JSON.stringify(data));
          var obj = JSON.parse(trimData);
          var $select = $('#SVC_CODIGO');
          $select.empty();
          var o = $('<option/>', { value: '' })
          .text('<%=TextHelper.forJavaScript(ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel))%>')
          .prop('selected', true);
          o.appendTo($select);
          for (var i = 0; i < obj.length; i++) {
              var o = $('<option/>', { value: obj[i].svcCodigo }).text(obj[i].svcDescricao + ' - ' + obj[i].svcIdentificador);
              o.appendTo($select);
          }
        } catch(err) {
        }
    }, "json");
    
    return true;
}


$(document).ready(function() {
	<% if (request.getAttribute("exibirTipoDecisaoJudicial") != null) { %>
	   var divLstConsignatarias = document.getElementById("listConsignataria");
	   var divLstServicos = document.getElementById("listServicos");
	   if (divLstConsignatarias) {
		   divLstConsignatarias.style.display = "none";
	   } 

	   if (divLstServicos) {
		   divLstServicos.style.display = "none";
	   }
	<% }%>
    formLoad();
	if (typeof f0.CSA_CODIGO !== "undefined" && f0.CSA_CODIGO.value != null && f0.CSA_CODIGO.value != "" &&
	   (typeof f0.SVC_CODIGO !== "undefined" && (f0.SVC_CODIGO.value == null || f0.SVC_CODIGO.value == "")) ) {
	     filtrarServico(f0.CSA_CODIGO.value);
	}
});

<%if (verificaAutorizacaoSemSenha){ %>
function validaAutorizacaoSemSenha() {
    var modalSenhaSer = new bootstrap.Modal(document.getElementById('modalSenhaSer'), {
      keyboard: false,
      backdrop: false
    });
    
    if(validarCamposObrigatorios()){
		const matricula = f0.RSE_MATRICULA != null ? f0.RSE_MATRICULA.value : "";
		const cpf = f0.SER_CPF != null ? f0.SER_CPF.value : "";
		const servico = f0.SVC_CODIGO != null ? f0.SVC_CODIGO.value : "";
     	const url = '../v3/reservarMargem?acao=validarAutorizacaoSemSenha&RSE_MATRICULA=' + matricula + '&SER_CPF=' + cpf + '&SVC_CODIGO=' + servico + '&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>';
		$.ajax({
            url: url,
            method: 'POST',
            dataType: 'json',
            success: function(data) {
		            if (data.situacao === 'S') {
		                modalSenhaSer.show();
		                return false;
		            } else if (data.situacao === 'A') {
						var msgAlertFim = document.getElementById("msgAlertFim");
						msgAlertFim.style.display = "block"

						modalSenhaSer.show();
		                return false;
		            } else {
		                if (validaSubmit()) {
		                	var form = document.getElementById("form1");
		                	var hiddenInput = document.getElementById("RSE_CODIGO");
		                	if (hiddenInput != null && hiddenInput != ''){
								form.removeChild(hiddenInput);
		                	}
		                    pesquisar(false);
		                } else {
		                    return false;
		                }
		            }
            },
            error: function(error) {
                console.error('Erro:', error);
                if (validaSubmit()) {
		                pesquisar(false);
		            } else {
		                return false;
		            }
            }
        });
    } else {
		return false;
    }
  }
<%} %>   

function incluirAdeDecisaoJucidicial(ocultar){
	var divAdeNumero = document.getElementById("exibeAdeNumero");
	var divLstConsignatarias = document.getElementById("listConsignataria");
	var divLstServicos = document.getElementById("listServicos");

    if (divLstConsignatarias && ocultar) {
	   divLstConsignatarias.style.display = "block";
    } else if (divLstConsignatarias && !ocultar) {
	   divLstConsignatarias.style.display = "none";
    } 

    if (divLstServicos && ocultar) {
 	   divLstServicos.style.display = "block";
    } else if (divLstServicos && !ocultar) {
 	   divLstServicos.style.display = "none";
    }
    
    if (divAdeNumero && ocultar) {
        divAdeNumero.style.display = "none";
    } else if (divAdeNumero && !ocultar) {
    	divAdeNumero.style.display = "block";
    }
}
window.onload = formLoad;
</script>
<% if (exibeCaptchaAvancado) { %>
<script src='https://www.google.com/recaptcha/api.js'></script>
<script type="text/JavaScript">
setInterval(function () {
    $("iframe[title*='recaptcha' i]").parent().parent().addClass('recaptcha_challenge');
}, 1000);
</script>
<% } %>
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
