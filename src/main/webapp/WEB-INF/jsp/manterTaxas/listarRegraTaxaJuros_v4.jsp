<%--
* <p>Title: listarRegraTaxaJuros_v4</p>
* <p>Description: Listar Regra Taxa Juros v4</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: rodrigo.rosa $
* $Revision: 24740 $
* $Date: 2019-04-03 16:03:13 -0300 (Qua, 03 apr 2019) $
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ page import="com.zetra.econsig.helper.sistema.ShowFieldHelper" %>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

Boolean podeAlterarRegraTaxaJuros = (Boolean) request.getAttribute("podeAlterarRegraTaxaJuros");
Boolean ativarTabela = (Boolean) request.getAttribute("ativarTabela");
Boolean tabelaVazia = (Boolean) request.getAttribute("tabelaVazia");

//Pega dados vindo do webController
 String csaCodigo = (String) request.getAttribute("csaCodigo");
 List lstServico = (List) request.getAttribute("lstServico");
 List lstOrgao = (List) request.getAttribute("lstOrgao");
 List definicaoTaxaJuros = (List) request.getAttribute("definicaoTaxaJuros");
 String data = (String) request.getAttribute("data");
 String statusRegra = (String) request.getAttribute("statusRegra");

%>
<c:set var="javascript">
  <script type="text/JavaScript">
  f0 = document.forms[0];

  function filtrar() {
	    f0.submit();
	  }

  function checkedCampo(statusRegra) {
	    if (statusRegra == <%=CodedValues.REGRA_NOVA_TABELA_INICIADA%>) {
	    	document.getElementById("statusRegra1").setAttribute("checked",true);}
	    else if (statusRegra == <%=CodedValues.REGRA_TABELA_ATIVA%>) {
	    	document.getElementById("statusRegra2").setAttribute("checked",true);}
	    else if (statusRegra == <%=CodedValues.REGRA_TABELA_VIGENCIA_EXPIRADA%>) {
	    	document.getElementById("statusRegra3").setAttribute("checked",true);}
	  }

  
  function disabledInputData(statusRegra){

    if(statusRegra == <%=CodedValues.REGRA_TABELA_VIGENCIA_EXPIRADA%>){
      document.getElementById("DATA").removeAttribute("disabled");
    }else{
        document.getElementById("DATA").value='';
        document.getElementById("DATA").setAttribute("disabled", "disabled");
        document.getElementById("DATA").removeAttribute("style");
        
    }
  }


  window.onload = function(){
      disabledInputData(<%=statusRegra%>);
      checkedCampo(<%=statusRegra%>);
  };

  function confirmarExclusao(link){
    if (confirm('<hl:message key="mensagem.confirmacao.exclusao.regra.taxa.juros"/>')){
    	postData(link);
    }
    return false;
  }

  function confirmarAtivacao(link){
      if (confirm('<hl:message key="mensagem.confirmacao.ativacao.tabela.regra.taxa.juros"/>')){
      	postData(link);
      }
      return false;
    }

  function confirmarIniciar(link){
	  if (confirm('<hl:message key="mensagem.confirmacao.iniciar.tabela.regra.taxa.juros"/>')){
	  	postData(link);
	  }
	  return false;
	}

	function confirmarExcluirTabelaIniciada(link){
	  if (confirm('<hl:message key="mensagem.confirmacao.remover.tabela.iniciada.regra.taxa.juros"/>')){
	  	postData(link);
	  }
	  return false;
	}
	  
  </script>
</c:set>
<c:set var="title">
  <hl:message key="rotulo.regra.taxa.juros.editar.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <input type="hidden" value="<%=TextHelper.forHtmlAttribute(request.getAttribute("csaCodigo"))%>" name="csaCodigo" />
  <div class="row">
    <div class="col-sm-12 col-md-12 mb-2">
      <div class="float-end">
          <% if (podeAlterarRegraTaxaJuros && (tabelaVazia || ativarTabela)) {%>
            <a class="btn btn-primary" href="#no-back" onClick="postData('../v3/editarRegraTaxaJuros?acao=novo&_skip_history_=true&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>')"><hl:message key="rotulo.regra.taxa.juros.nova.regra"/></a>
          <% } %> 
        </div>
      </div>
    </div>
  <div class="row">
    <div class="col-sm">
 <FORM NAME="form1" METHOD="post"
    ACTION="../v3/editarRegraTaxaJuros?acao=consultar&_skip_history_=true&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>">
    <input type="hidden"
      value="<%=TextHelper.forHtmlAttribute(request.getAttribute("csaCodigo"))%>"
      name="csacodigo" />
        <div class="card">
          <div class="card-header">
            <h3 class="card-header-title">
              <hl:message key="rotulo.botao.pesquisar" />
            </h3>
          </div>
          <div class="card-body">
            <div class="row">
              <div class="form-group col-sm" role="radiogroup">
                <div><span><hl:message key="rotulo.regra.taxa.juros.status.regra" /></span></div>
                <div class="form-check form-check form-check-inline mt-2">
                  <label for="statusRegra1" class="formatacao ml-1 pr-4 text-nowrap align-text-top labelSemNegrito">
                    <input type="radio" class="form-check-input ml-1" id="statusRegra1" onclick="disabledInputData(this.value)" name="STATUS_REGRA" value="1" /> <hl:message key="rotulo.regra.taxa.juros.status.regra.aberto"/></label>
                </div>
                <div class="form-check form-check form-check-inline mt-2">
                  <label for="statusRegra2" class="formatacao ml-1 pr-4 text-nowrap align-text-top labelSemNegrito">
                    <input type="radio" class="form-check-input ml-1" id="statusRegra2" onclick="disabledInputData(this.value)" name="STATUS_REGRA" value="2" /> <hl:message key="rotulo.regra.taxa.juros.status.regra.vigente"/></label>
                </div>
                <div class="form-check form-check form-check-inline mt-2">
                  <label for="statusRegra3" class="formatacao ml-1 pr-4 text-nowrap align-text-top labelSemNegrito">
                    <input type="radio" class="form-check-input ml-1" id="statusRegra3" onclick="disabledInputData(this.value)" name="STATUS_REGRA" value="3" /> <hl:message key="rotulo.regra.taxa.juros.status.regra.finalizada"/></label>
                </div>
              </div>
              <div class="form-group col-sm">
                <label for="DATA"><hl:message key="rotulo.regra.taxa.juros.vigencia"/></label>
                <hl:htmlinput name="DATA" di="DATA" type="text" others="disabled='disabled'" classe="Edit form-control" 
                 size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" value="<%=TextHelper.forHtmlAttribute(data)%>" /> 
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm">
                <label for=""><hl:message key="rotulo.orgao.singular"/></label>
                <%=JspHelper.geraCombo(lstOrgao, "ORG_CODIGO", Columns.ORG_CODIGO, Columns.ORG_NOME, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavel), null, false, 1, JspHelper.verificaVarQryStr(request, "ORG_CODIGO"), null, false, "form-control")%>
              </div>
              <div class="form-group col-sm">
                <label for=""><hl:message key="rotulo.servico.singular"/></label>
                <%=JspHelper.geraCombo(lstServico, "SVC_CODIGO", Columns.SVC_CODIGO, Columns.SVC_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavel), null, false, 1, JspHelper.verificaVarQryStr(request, "SVC_CODIGO"), null, false, "form-control")%>
              </div>
            </div>
          </div>
        </div>
        <div class="btn-action d-print-none">
          <a class="btn btn-primary" href="#no-back" name="Filtrar" id="Filtrar" onClick="filtrar()" alt="<hl:message key="rotulo.botao.pesquisar"/>" title="<hl:message key="rotulo.botao.pesquisar"/>"> 
            <svg width="20"><use xlink:href="#i-consultar"></use></svg> <hl:message key="rotulo.botao.pesquisar" />
          </a>
        </div>
      </form>
    </div>
  </div>
  <div class="row">
    <div class="col-sm">
      <div class="card">
        <div class="card-header">
          <h3 class="card-header-title">
            <hl:message key="rotulo.regra.taxa.juros.listar.subtitulo" />
          </h3>
        </div>
        <div class="card-body p-0 table-responsive">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th><hl:message key="rotulo.regra.taxa.juros.data.vigencia.inicial" /></th>
                <th><hl:message key="rotulo.regra.taxa.juros.data.vigencia.final" /></th>
                <th><hl:message key="rotulo.regra.taxa.juros.orgao" /></th>
                <th><hl:message key="rotulo.regra.taxa.juros.servico" /></th>
                <% if (ShowFieldHelper.showField(FieldKeysConstants.DEFINICAO_TAXA_JUROS_FAIXA_ETARIA, responsavel)) { %>
                  <th><hl:message key="rotulo.regra.taxa.juros.faixa.etaria" /></th>
                <%} %>
                <% if (ShowFieldHelper.showField(FieldKeysConstants.DEFINICAO_TAXA_JUROS_FAIXA_MARGEM, responsavel)) { %>
                  <th><hl:message key="rotulo.regra.taxa.juros.faixa.margem" /> (<hl:message key="rotulo.moeda" />)</th>
                <%} %>
                <% if (ShowFieldHelper.showField(FieldKeysConstants.DEFINICAO_TAXA_JUROS_FAIXA_TEMP_SERVICO, responsavel)) { %>
                  <th><hl:message key="rotulo.regra.taxa.juros.faixa.tempo.servico" /></th>
                <%} %>
                <% if (ShowFieldHelper.showField(FieldKeysConstants.DEFINICAO_TAXA_JUROS_FAIXA_VLR_CONTRATO, responsavel)) { %>
                  <th><hl:message key="rotulo.regra.taxa.juros.faixa.valor.contrato"/>(<hl:message key="rotulo.moeda" />)</th>
                <%} %>
                <% if (ShowFieldHelper.showField(FieldKeysConstants.DEFINICAO_TAXA_JUROS_FAIXA_VLR_TOTAL, responsavel)) { %>
                  <th><hl:message key="rotulo.regra.taxa.juros.faixa.valor.total" /> (<hl:message key="rotulo.moeda" />)</th>
                <%} %>
                <th><hl:message key="rotulo.regra.taxa.juros.faixa.prazo" /></th>
                <% if (ShowFieldHelper.showField(FieldKeysConstants.DEFINICAO_TAXA_JUROS_FAIXA_SALARIO, responsavel)) { %>
                  <th><hl:message key="rotulo.regra.taxa.juros.faixa.salarial" /> (<hl:message key="rotulo.moeda" />)</th>
                <%} %>
                <th><hl:message key="rotulo.regra.taxa.juros.taxa.juros" /></th>
                <th><hl:message key="rotulo.regra.taxa.juros.funcao"/></th>
                <th><hl:message key="rotulo.acoes" /></th>
              </tr>
            </thead>
            <tbody>
                <%=JspHelper.msgRstVazio(definicaoTaxaJuros.size()==0, 14, responsavel)%>
              <%
            Iterator it = definicaoTaxaJuros.iterator();
            while (it.hasNext()) {
              CustomTransferObject dtj = (CustomTransferObject)it.next();
              String dtjCodigo = (String)dtj.getAttribute(Columns.DTJ_CODIGO);

              Date dataAux = (Date) dtj.getAttribute(Columns.DTJ_DATA_VIGENCIA_INI);
              String dtjVigenciaIni = null;
              if(!TextHelper.isNull(dataAux)){
            	  dtjVigenciaIni = DateHelper.reformat(dataAux.toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern());
              }
              Date dataAux2 = (Date) dtj.getAttribute(Columns.DTJ_DATA_VIGENCIA_FIM);
              String dtjVigenciaFim = null;
              if(!TextHelper.isNull(dataAux2)){
            	  dtjVigenciaFim = DateHelper.reformat(dataAux2.toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern());    
              }
              String orgNome = (String)dtj.getAttribute(Columns.ORG_NOME);

              String dtjServico = (String)dtj.getAttribute(Columns.SVC_DESCRICAO);

              String funNome = (String)dtj.getAttribute(Columns.FUN_CODIGO);

              String dtjFaixaSalarialIni = null;
              
              if(!TextHelper.isNull(dtj.getAttribute(Columns.DTJ_FAIXA_SALARIO_INI))){
            	  dtjFaixaSalarialIni = NumberHelper.reformat(dtj.getAttribute(Columns.DTJ_FAIXA_SALARIO_INI).toString(), "en", NumberHelper.getLang(), 2, 20);
              }
              
              String dtjFaixaSalarialFim = null;
              
              if(!TextHelper.isNull(dtj.getAttribute(Columns.DTJ_FAIXA_SALARIO_FIM))){
                  dtjFaixaSalarialFim = NumberHelper.reformat(dtj.getAttribute(Columns.DTJ_FAIXA_SALARIO_FIM).toString(), "en", NumberHelper.getLang(), 2, 20);
              }
              
              String dtjFaixaEtariaIni = null;
              
              if(!TextHelper.isNull(dtj.getAttribute(Columns.DTJ_FAIXA_ETARIA_INI))){
                  dtjFaixaEtariaIni = dtj.getAttribute(Columns.DTJ_FAIXA_ETARIA_INI).toString();
              }
              
              String dtjFaixaEtariaFim = null;
              
              if(!TextHelper.isNull(dtj.getAttribute(Columns.DTJ_FAIXA_ETARIA_FIM))){
            	  dtjFaixaEtariaFim = dtj.getAttribute(Columns.DTJ_FAIXA_ETARIA_FIM).toString();
              }
              
              String dtjFaixaValorContratoIni = null;
              
              if(!TextHelper.isNull(dtj.getAttribute(Columns.DTJ_FAIXA_VALOR_CONTRATO_INI))){
                  dtjFaixaValorContratoIni = NumberHelper.reformat(dtj.getAttribute(Columns.DTJ_FAIXA_VALOR_CONTRATO_INI).toString(), "en", NumberHelper.getLang(), 2, 20);
              }
             
              String dtjFaixaValorContratoFinal = null;
              
              if(!TextHelper.isNull(dtj.getAttribute(Columns.DTJ_FAIXA_VALOR_CONTRATO_FIM))){
                  dtjFaixaValorContratoFinal = NumberHelper.reformat(dtj.getAttribute(Columns.DTJ_FAIXA_VALOR_CONTRATO_FIM).toString(), "en", NumberHelper.getLang(), 2, 20);
              }
              
              String dtjFaixaValorTotalIni = null;
              
              if(!TextHelper.isNull(dtj.getAttribute(Columns.DTJ_FAIXA_VALOR_TOTAL_INI))){
            	  dtjFaixaValorTotalIni = NumberHelper.reformat(dtj.getAttribute(Columns.DTJ_FAIXA_VALOR_TOTAL_INI).toString(), "en", NumberHelper.getLang(), 2, 20);
              }
             
              String dtjFaixaValorTotalFinal = null;
              
              if(!TextHelper.isNull(dtj.getAttribute(Columns.DTJ_FAIXA_VALOR_TOTAL_FIM))){
            	  dtjFaixaValorTotalFinal = NumberHelper.reformat(dtj.getAttribute(Columns.DTJ_FAIXA_VALOR_TOTAL_FIM).toString(), "en", NumberHelper.getLang(), 2, 20);
              }
              
              
              String dtjFaixaMargemIni = null;
              
              if(!TextHelper.isNull(dtj.getAttribute(Columns.DTJ_FAIXA_MARGEM_INI))){
                  dtjFaixaMargemIni = NumberHelper.reformat(dtj.getAttribute(Columns.DTJ_FAIXA_MARGEM_INI).toString(), "en", NumberHelper.getLang(), 2, 20);
              }
             
              String dtjFaixaMargemFim = null;
              
              if(!TextHelper.isNull(dtj.getAttribute(Columns.DTJ_FAIXA_MARGEM_FIM))){
                  dtjFaixaMargemFim = NumberHelper.reformat(dtj.getAttribute(Columns.DTJ_FAIXA_MARGEM_FIM).toString(), "en", NumberHelper.getLang(), 2, 20);
              }
              
              String dtjTempoServicoIni = null;
              
              if(!TextHelper.isNull(dtj.getAttribute(Columns.DTJ_FAIXA_TEMP_SERVICO_INI))){
                  dtjTempoServicoIni = dtj.getAttribute(Columns.DTJ_FAIXA_TEMP_SERVICO_INI).toString();
              }
              
              String dtjTempoServicoFim = null;
              
              if(!TextHelper.isNull(dtj.getAttribute(Columns.DTJ_FAIXA_TEMP_SERVICO_FIM))){
                  dtjTempoServicoFim = dtj.getAttribute(Columns.DTJ_FAIXA_TEMP_SERVICO_FIM).toString();
              }

              String dtjFaixaPrazoIni = null;
              
              if(!TextHelper.isNull(dtj.getAttribute(Columns.DTJ_FAIXA_PRAZO_INI))){
                  dtjFaixaPrazoIni = dtj.getAttribute(Columns.DTJ_FAIXA_PRAZO_INI).toString();
              }
              
              String dtjFaixaPrazoFim = null;
              
              if(!TextHelper.isNull(dtj.getAttribute(Columns.DTJ_FAIXA_PRAZO_FIM))){
                  dtjFaixaPrazoFim = dtj.getAttribute(Columns.DTJ_FAIXA_PRAZO_FIM).toString();
              }
              
              String dtjTaxaJuros = null;
              
              if(!TextHelper.isNull(dtj.getAttribute(Columns.DTJ_TAXA_JUROS))){
            	  dtjTaxaJuros = NumberHelper.reformat(dtj.getAttribute(Columns.DTJ_TAXA_JUROS).toString(), "en", NumberHelper.getLang(), 2, 20);
              }
            %>
              <tr>
                <td><%=!TextHelper.isNull(dtjVigenciaIni) ? TextHelper.forHtmlContent(dtjVigenciaIni): ""%></td>
                <td><%=!TextHelper.isNull(dtjVigenciaFim) ? TextHelper.forHtmlContent(dtjVigenciaFim): ""%></td>
                <td><%=!TextHelper.isNull(orgNome) ? TextHelper.forHtmlContent(orgNome): ""%></td>
                <td><%=!TextHelper.isNull(dtjServico) ? TextHelper.forHtmlContent(dtjServico): ""%></td>
                <% if (ShowFieldHelper.showField(FieldKeysConstants.DEFINICAO_TAXA_JUROS_FAIXA_ETARIA, responsavel)) { %>
                  <td><%=!TextHelper.isNull(dtjFaixaEtariaIni) ? TextHelper.forHtmlContent(dtjFaixaEtariaIni): ""%>-<%=!TextHelper.isNull(dtjFaixaEtariaFim) ? TextHelper.forHtmlContent(dtjFaixaEtariaFim): ""%></td>
                <%} %>
                <% if (ShowFieldHelper.showField(FieldKeysConstants.DEFINICAO_TAXA_JUROS_FAIXA_MARGEM, responsavel)) { %>
                  <td><%=!TextHelper.isNull(dtjFaixaMargemIni) ? TextHelper.forHtmlContent(dtjFaixaMargemIni): ""%>-<%=!TextHelper.isNull(dtjFaixaMargemFim) ? TextHelper.forHtmlContent(dtjFaixaMargemFim): ""%></td>
                <%} %>
                <% if (ShowFieldHelper.showField(FieldKeysConstants.DEFINICAO_TAXA_JUROS_FAIXA_TEMP_SERVICO, responsavel)) { %>
                  <td><%=!TextHelper.isNull(dtjTempoServicoIni) ? TextHelper.forHtmlContent(dtjTempoServicoIni): ""%>-<%=!TextHelper.isNull(dtjTempoServicoFim) ? TextHelper.forHtmlContent(dtjTempoServicoFim): ""%></td>
                <%} %>
                <% if (ShowFieldHelper.showField(FieldKeysConstants.DEFINICAO_TAXA_JUROS_FAIXA_VLR_CONTRATO, responsavel)) { %>
                  <td><%=!TextHelper.isNull(dtjFaixaValorContratoIni) ? TextHelper.forHtmlContent(dtjFaixaValorContratoIni): ""%>-<%=!TextHelper.isNull(dtjFaixaValorContratoFinal) ? TextHelper.forHtmlContent(dtjFaixaValorContratoFinal): ""%></td>
                <%} %>
                <% if (ShowFieldHelper.showField(FieldKeysConstants.DEFINICAO_TAXA_JUROS_FAIXA_VLR_TOTAL, responsavel)) { %>
                  <td><%=!TextHelper.isNull(dtjFaixaValorTotalIni) ? TextHelper.forHtmlContent(dtjFaixaValorTotalIni): ""%>-<%=!TextHelper.isNull(dtjFaixaValorTotalFinal) ? TextHelper.forHtmlContent(dtjFaixaValorTotalFinal): ""%></td>
                <%} %>
                  <td><%=!TextHelper.isNull(dtjFaixaPrazoIni) ? TextHelper.forHtmlContent(dtjFaixaPrazoIni): ""%>-<%=!TextHelper.isNull(dtjFaixaPrazoFim) ? TextHelper.forHtmlContent(dtjFaixaPrazoFim): ""%></td>
                <% if (ShowFieldHelper.showField(FieldKeysConstants.DEFINICAO_TAXA_JUROS_FAIXA_SALARIO, responsavel)) { %>
                  <td><%=!TextHelper.isNull(dtjFaixaSalarialIni) ? TextHelper.forHtmlContent(dtjFaixaSalarialIni): ""%>-<%=!TextHelper.isNull(dtjFaixaSalarialFim) ? TextHelper.forHtmlContent(dtjFaixaSalarialFim): ""%></td>
                <%} %>
                <td><%=!TextHelper.isNull(dtjTaxaJuros) ? TextHelper.forHtmlContent(dtjTaxaJuros): ""%></td>
                <td><%=!TextHelper.isNull(funNome) ? TextHelper.forHtmlContent(funNome) : ""%></td>
                <% if(podeAlterarRegraTaxaJuros && statusRegra.equals(CodedValues.REGRA_NOVA_TABELA_INICIADA)) { %>
                <td>
                  <div class="actions">
                    <div class="dropdown">
                      <a class="dropdown-toggle ico-action" href="#"
                        role="button" id="userMenu"
                        data-bs-toggle="dropdown" aria-haspopup="true"
                        aria-expanded="false">
                        <div class="form-inline">
                          <span class="mr-1" data-bs-toggle="tooltip"
                            title="" data-original-title="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>"
                            aria-label="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>"> <svg>
                              <use
                                xmlns:xlink="http://www.w3.org/1999/xlink"
                                xlink:href="#i-engrenagem"></use></svg>
                          </span>
                          <hl:message
                            key="rotulo.acoes.lst.arq.generico.opcoes" />
                        </div>
                      </a>
                      <div class="dropdown-menu dropdown-menu-right"
                        aria-labelledby="userMenu">
                        <a class="dropdown-item" href="#no-back"
                          onClick="postData('../v3/editarRegraTaxaJuros?acao=editar&_skip_history_=true&dtjCodigo=<%=TextHelper.forJavaScriptAttribute(dtjCodigo)%>&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&dtjCodigo=<%=TextHelper.forJavaScriptAttribute(dtjCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                          <hl:message key="rotulo.acoes.editar" />
                        </a> 
                        <a class="dropdown-item" href="#no-back" onClick="confirmarExclusao('../v3/editarRegraTaxaJuros?acao=excluir&_skip_history_=true&dtjCodigo=<%=TextHelper.forJavaScriptAttribute(dtjCodigo)%>&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                          <hl:message key="rotulo.acoes.excluir" />
                        </a>
                      </div>
                    </div>
                  </div>
                </td>
                <% } else { %>
                <td>
                  <div class="actions">
                    <div class="dropdown">
                      <a class="dropdown-toggle ico-action" href="#"
                        role="button" id="userMenu"
                        data-bs-toggle="dropdown" aria-haspopup="true"
                        aria-expanded="false">
                        <div class="form-inline">
                          <span class="mr-1" data-bs-toggle="tooltip"
                            title="" data-original-title="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>"
                            aria-label="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>"> <svg>
                              <use
                                xmlns:xlink="http://www.w3.org/1999/xlink"
                                xlink:href="#i-engrenagem"></use></svg>
                          </span>
                          <hl:message
                            key="rotulo.acoes.lst.arq.generico.opcoes" />
                        </div>
                      </a>
                      <div class="dropdown-menu dropdown-menu-right"
                        aria-labelledby="userMenu">
                        <a class="dropdown-item" href="#no-back"
                          onClick="postData('../v3/editarRegraTaxaJuros?acao=visualizar&_skip_history_=true&dtjCodigo=<%=TextHelper.forJavaScriptAttribute(dtjCodigo)%>&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                          <hl:message key="rotulo.acoes.detalhar" />
                        </a>
                      </div>
                    </div>
                  </div>
                </td>
                <% } %>
              </tr>
              <% 
               }
               %>
            </tbody>
            <tfoot>
              <tr>
                <td colspan="14"><%=ApplicationResourcesHelper.getMessage("rotulo.regra.taxa.juros.listar.subtitulo", responsavel) + " - "%>
                  <span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span>
                </td>
              </tr>
            </tfoot>
          </table>
        </div>
        <div class="card-footer">
          <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp"%>
        </div>
      </div>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;" id="btnVoltar"><hl:message key="rotulo.botao.voltar"/></a>
    <% if (podeAlterarRegraTaxaJuros && !ativarTabela && !tabelaVazia) {%>
    <a class="btn btn-primary" href="#no-back" onClick="postData('../v3/editarRegraTaxaJuros?acao=iniciarTabela&_skip_history_=true&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>'); return false;" value="Iniciar"><hl:message key="rotulo.botao.regra.taxa.juros.iniciar.tabela" /></a>
    <% } %>
    <% if (statusRegra != null && statusRegra.equals(CodedValues.REGRA_NOVA_TABELA_INICIADA) && podeAlterarRegraTaxaJuros && ativarTabela) { %>
      <a class="btn btn-primary" href="#no-back" onClick="confirmarExcluirTabelaIniciada('../v3/editarRegraTaxaJuros?acao=excluirTabelaIniciada&_skip_history_=true&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>'); return false;"><hl:message key="rotulo.botao.excluir.tabela.iniciada.regra.taxa.juros"/></a>
    <% } %>
    <% if (podeAlterarRegraTaxaJuros && ativarTabela) {%> 
    <a class="btn btn-primary" href="#no-back" onClick="confirmarAtivacao('../v3/editarRegraTaxaJuros?acao=ativarTabela&_skip_history_=true&CSA_CODIGO=<%=TextHelper.forJavaScriptAttribute(csaCodigo)%>&<%=SynchronizerToken.generateToken4URL(request)%>'); return false;" value="Ativar"><hl:message key="rotulo.botao.regra.taxa.juros.ativar.tabela" /></a> 
    <% } %>
  </div>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>