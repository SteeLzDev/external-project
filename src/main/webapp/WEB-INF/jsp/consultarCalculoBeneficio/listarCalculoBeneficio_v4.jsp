<%--
* <p>Title: listarCalculoBeneficio_v4</p>
* <p>Description: Listar benefícios v4</p>
* <p>Copyright: Copyright (c) 2005</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: larissa.silva $
* $Revision: 24740 $
* $Date: 2018-07-10 16:03:13 -0300 (Ter, 10 jul 2018) $
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
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

Boolean podeAlterarCalculoBeneficio = (Boolean) request.getAttribute("podeAlterarCalculoBeneficio");
Boolean ativarTabela = (Boolean) request.getAttribute("ativarTabela");
Boolean tabelaVazia = (Boolean) request.getAttribute("tabelaVazia");

//Pega dados vindo do webController
String tipo = (String) request.getAttribute("tipo");
String codigo = responsavel.getCodigoEntidade();
List<?> calculoBeneficios = (List<?>) request.getAttribute("calculoBeneficios");
List orgaos = (List) request.getAttribute("orgaos");
List beneficios = (List) request.getAttribute("beneficios");
List tipoBeneficiarios = (List) request.getAttribute("tipoBeneficiarios");
List grauParentesco = (List) request.getAttribute("grauParentesco");
String data = (String) request.getAttribute("data");
String statusRegra = (String) request.getAttribute("statusRegra");
List motivoDependencia = (List) request.getAttribute("motivoDependencia");
%>
<c:set var="javascript">
  <script type="text/JavaScript">
  f0 = document.forms[0];

  function filtrar() {
    f0.submit();
  }
  
  function disabledInputData(statusRegra){

    if(statusRegra == "3"){
      document.getElementById("DATA").removeAttribute("disabled");
      document.getElementById("DATA").setAttribute("style", "background-color: white; color: black;");
    }else{
      document.getElementById("DATA").setAttribute("disabled", "disabled");
      document.getElementById("DATA").removeAttribute("style");
      document.getElementById("DATA").value="";
    }
  }

  window.onload = function(){
    disabledInputData(<%=statusRegra%>);
  };

  function confirmarExclusao(link){
    if (confirm('<hl:message key="mensagem.confirmacao.exclusao.calculo.beneficio"/>')){
      postData(link);
    }
    return false;
  }

  function confirmarAtivacao(link){
    if (confirm('<hl:message key="mensagem.confirmacao.ativacao.tabela.calculo.beneficio"/>')){
      postData(link);
    }
    return false;
  }

  function confirmarIniciar(link){
    if (confirm('<hl:message key="mensagem.confirmacao.iniciar.tabela"/>')){
      postData(link);
    }
    return false;
  }

  function confirmarExcluirTabelaIniciada(link){
    if (confirm('<hl:message key="mensagem.confirmacao.remover.tabela.iniciada"/>')){
      postData(link);
    }
    return false;
  }
  </script>
</c:set>
<c:set var="title">
  <hl:message key="rotulo.calculo.beneficio.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-beneficios"></use>
</c:set>
<c:set var="bodyContent">
  <% if ((podeAlterarCalculoBeneficio && ativarTabela) || (podeAlterarCalculoBeneficio && tabelaVazia)) {%>  
  <div class="btn-action d-print-none">
    <button data-bs-toggle="dropdown" aria-haspopup="true" id="acoes" aria-expanded="false" class="btn btn-primary" type="submit">
      <hl:message key="rotulo.mais.acoes" />
    </button>
    <div class="dropdown-menu dropdown-menu-right" aria-labelledby="acoes">
      <% if (podeAlterarCalculoBeneficio && ativarTabela) {%>
          <a class="dropdown-item" href="#no-back" onClick="postData('../v3/alterarCalculoBeneficio?acao=novoReajuste&<%=SynchronizerToken.generateToken4URL(request)%>')">
            <hl:message key="rotulo.botao.aplicar.reajuste" />
          </a>
      <% } %>
      <% if ((podeAlterarCalculoBeneficio && ativarTabela) || (podeAlterarCalculoBeneficio && tabelaVazia)) {%>  
          <a class="dropdown-item" href="#no-back" onClick="postData('../v3/alterarCalculoBeneficio?acao=novo&<%=SynchronizerToken.generateToken4URL(request)%>')"><hl:message key="rotulo.botao.novo.calculo"/></a>
      <% } %>
    </div>
  </div>
  <% } %>
  <div class="row">
    <div class="col-sm">
      <form action="../v3/consultarCalculoBeneficio" method="post" name="formPesquisar">
        <input type="hidden" name="acao" value="consultar"/>
        <div class="card">
          <div class="card-header hasIcon pl-3">
            <h3 class="card-header-title">
              <hl:message key="rotulo.botao.pesquisar" />
            </h3>
          </div>
          <div class="card-body">
            <div class="row">
              <div class="form-group col-sm-8">
                <span><hl:message key="rotulo.calculo.beneficio.status.regra" /></span>
                <br/>
                <div class="form-check form-check-inline mt-2">
                  <label for="statusRegra1" class="formatacao ml-1 pr-4 text-nowrap align-text-top">
                    <input type="radio" class="form-check-input ml-1" id="statusRegra1" onclick="disabledInputData(this.value)" name="STATUS_REGRA" value="1" <%= statusRegra.equals("1") ? "checked" : "" %> /> <hl:message key="rotulo.calculo.beneficio.status.regra.aberto"/></label>
                </div>
                <div class="form-check form-check-inline mt-2">
                  <label for="statusRegra2" class="formatacao ml-1 pr-4 text-nowrap align-text-top">
                    <input type="radio" class="form-check-input ml-1" id="statusRegra2" onclick="disabledInputData(this.value)" name="STATUS_REGRA" value="2" <%= statusRegra.equals("2") ? "checked" : "" %> /> <hl:message key="rotulo.calculo.beneficio.status.regra.vigente"/></label>
                </div>
                <div class="form-check form-check-inline mt-2">
                  <label for="statusRegra3" class="formatacao ml-1 pr-4 text-nowrap align-text-top">
                    <input type="radio" class="form-check-input ml-1" id="statusRegra3" onclick="disabledInputData(this.value)" name="STATUS_REGRA" value="3" <%= statusRegra.equals("3") ? "checked" : "" %> /> <hl:message key="rotulo.calculo.beneficio.status.regra.finalizada"/></label>
                </div>
              </div>
              <div class="form-group col-sm">
                <label for="DATA"><hl:message key="rotulo.calculo.beneficio.vigencia"/></label>
                <hl:htmlinput name="DATA" di="DATA" type="text" classe="Edit form-control" 
                size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" value="<%=TextHelper.forHtmlAttribute(data)%>" />
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm">
                <label for=""><hl:message key="rotulo.orgao.singular"/></label>
                <%=JspHelper.geraCombo(orgaos, "ORG_CODIGO", Columns.ORG_CODIGO, Columns.ORG_NOME, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavel), null, false, 1, JspHelper.verificaVarQryStr(request, "ORG_CODIGO"), null, false, "form-control")%>
              </div>
              <div class="form-group col-sm">
                <label for=""><hl:message key="rotulo.beneficio.singular"/></label>
                <%=JspHelper.geraCombo(beneficios, "BEN_CODIGO", Columns.BEN_CODIGO, Columns.BEN_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavel), null, false, 1, JspHelper.verificaVarQryStr(request, "BEN_CODIGO"), null, false, "form-control")%>
              </div>
              <div class="form-group col-sm">
                <label for=""><hl:message key="rotulo.calculo.beneficio.tipo.beneficiario"/></label>
                <%=JspHelper.geraCombo(tipoBeneficiarios, "TIB_CODIGO", Columns.TIB_CODIGO, Columns.TIB_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavel), null, false, 1, JspHelper.verificaVarQryStr(request, "TIB_CODIGO"), null, false, "form-control")%>
              </div>
              <div class="form-group col-sm">
                <label for=""><hl:message key="rotulo.calculo.beneficio.grau.parentesco"/></label>
                <%=JspHelper.geraCombo(grauParentesco, "GRP_CODIGO", Columns.GRP_CODIGO, Columns.GRP_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavel), null, false, 1, JspHelper.verificaVarQryStr(request, "GRP_CODIGO"), null, false, "form-control")%>
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm-3">
                <label for=""><hl:message key="rotulo.beneficiario.motivo.dependencia"/></label>
                <%=JspHelper.geraCombo(motivoDependencia, "MDE_CODIGO", Columns.MDE_CODIGO, Columns.MDE_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavel), null, false, 1, JspHelper.verificaVarQryStr(request, "MDE_CODIGO"), null, false, "form-control")%>
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
          <hl:message key="rotulo.calculo.beneficio.titulo" />
        </div>
        <div class="card-body table-responsive">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th><hl:message key="rotulo.calculo.beneficio.inicio.vigencia"/></th>
                <th><hl:message key="rotulo.calculo.beneficio.fim.vigencia"/></th>
                <th><hl:message key="rotulo.calculo.beneficio.orgao"/></th>
                <th><hl:message key="rotulo.calculo.beneficio.descricao"/></th>
                <th><hl:message key="rotulo.calculo.beneficio.tipo.beneficiario"/></th>
                <th><hl:message key="rotulo.calculo.beneficio.grau.parentesco"/></th>
                <th><hl:message key="rotulo.beneficiario.motivo.dependencia"/></th>
                <th><hl:message key="rotulo.calculo.beneficio.inicio.faixa.salarial"/></th>
                <th><hl:message key="rotulo.calculo.beneficio.fim.faixa.salarial"/></th>
                <th><hl:message key="rotulo.calculo.beneficio.inicio.faixa.etaria"/></th>
                <th><hl:message key="rotulo.calculo.beneficio.fim.faixa.etaria"/></th>
                <th><hl:message key="rotulo.calculo.beneficio.valor.beneficio"/></th>
                <th><hl:message key="rotulo.calculo.beneficio.valor.subsidio"/></th>
                <th><hl:message key="rotulo.acoes"/></th>
              </tr>
            </thead>
            <tbody>
            <%=JspHelper.msgRstVazio(calculoBeneficios.size()==0, 13, responsavel)%>
            <%
            Iterator<?> it = calculoBeneficios.iterator();
            while (it.hasNext()) {
              CustomTransferObject clb = (CustomTransferObject)it.next();
              String clbCodigo = (String)clb.getAttribute(Columns.CLB_CODIGO);
              Date dataAux = (Date) clb.getAttribute(Columns.CLB_VIGENCIA_INI);
              String clbVigenciaIni = null;
              if(!TextHelper.isNull(dataAux)){
                  clbVigenciaIni = DateHelper.reformat(dataAux.toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern());
              }
              Date dataAux2 = (Date) clb.getAttribute(Columns.CLB_VIGENCIA_FIM);
              String clbVigenciaFim = null;
              if(!TextHelper.isNull(dataAux2)){
                  clbVigenciaFim = DateHelper.reformat(dataAux2.toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern());    
              }
               String orgNome = (String)clb.getAttribute(Columns.ORG_NOME);
              String benDescricao = (String)clb.getAttribute(Columns.BEN_DESCRICAO);
              String tibDescricao = (String)clb.getAttribute(Columns.TIB_DESCRICAO);
              String grpDescricao = (String)clb.getAttribute(Columns.GRP_DESCRICAO);
              String mdeDescricao = (String)clb.getAttribute(Columns.MDE_DESCRICAO);
              
              String clbFaixaSalarialIni = null;
              
              if(!TextHelper.isNull(clb.getAttribute(Columns.CLB_FAIXA_SALARIAL_INI))){
                 clbFaixaSalarialIni = NumberHelper.reformat(clb.getAttribute(Columns.CLB_FAIXA_SALARIAL_INI).toString(), "en", NumberHelper.getLang(), 2, 20);
              }
              
              String clbFaixaSalarialFim = null;
              
              if(!TextHelper.isNull(clb.getAttribute(Columns.CLB_FAIXA_SALARIAL_FIM))){
                  clbFaixaSalarialFim = NumberHelper.reformat(clb.getAttribute(Columns.CLB_FAIXA_SALARIAL_FIM).toString(), "en", NumberHelper.getLang(), 2, 20);
              }
              
              String clbFaixaEtariaIni = null;
              
              if(!TextHelper.isNull(clb.getAttribute(Columns.CLB_FAIXA_ETARIA_INI))){
                  clbFaixaEtariaIni = clb.getAttribute(Columns.CLB_FAIXA_ETARIA_INI).toString();
              }
              
              String clbFaixaEtariaFim = null;
              
              if(!TextHelper.isNull(clb.getAttribute(Columns.CLB_FAIXA_ETARIA_FIM))){
                  clbFaixaEtariaFim = clb.getAttribute(Columns.CLB_FAIXA_ETARIA_FIM).toString();
              }
              
              String clbValorMensalidade = null;
              
              if(!TextHelper.isNull(clb.getAttribute(Columns.CLB_VALOR_MENSALIDADE))){
                  clbValorMensalidade = NumberHelper.reformat(clb.getAttribute(Columns.CLB_VALOR_MENSALIDADE).toString(), "en", NumberHelper.getLang(), 2, 20);
              }
             
              String clbValorSubsidio = null;
              
              if(!TextHelper.isNull(clb.getAttribute(Columns.CLB_VALOR_SUBSIDIO))){
                  clbValorSubsidio = NumberHelper.reformat(clb.getAttribute(Columns.CLB_VALOR_SUBSIDIO).toString(), "en", NumberHelper.getLang(), 2, 20);
              }
            %>
              <tr>
                <td><%=!TextHelper.isNull(clbVigenciaIni) ? TextHelper.forHtmlContent(clbVigenciaIni): ""%></td>
                <td><%=!TextHelper.isNull(clbVigenciaFim) ? TextHelper.forHtmlContent(clbVigenciaFim): ""%></td>
                <td><%=!TextHelper.isNull(orgNome) ? TextHelper.forHtmlContent(orgNome): ""%></td>
                <td><%=!TextHelper.isNull(benDescricao) ? TextHelper.forHtmlContent(benDescricao): ""%></td>
                <td><%=!TextHelper.isNull(tibDescricao) ? TextHelper.forHtmlContent(tibDescricao): ""%></td>
                <td><%=!TextHelper.isNull(grpDescricao) ? TextHelper.forHtmlContent(grpDescricao): ""%></td>
                <td><%=!TextHelper.isNull(mdeDescricao) ? TextHelper.forHtmlContent(mdeDescricao): ""%></td>
                <td><%=!TextHelper.isNull(clbFaixaSalarialIni) ? TextHelper.forHtmlContent(clbFaixaSalarialIni): ""%></td>
                <td><%=!TextHelper.isNull(clbFaixaSalarialFim) ? TextHelper.forHtmlContent(clbFaixaSalarialFim): ""%></td>
                <td><%=!TextHelper.isNull(clbFaixaEtariaIni) ? TextHelper.forHtmlContent(clbFaixaEtariaIni): ""%></td>
                <td><%=!TextHelper.isNull(clbFaixaEtariaFim) ? TextHelper.forHtmlContent(clbFaixaEtariaFim): ""%></td>
                <td><%=!TextHelper.isNull(clbValorMensalidade) ? TextHelper.forHtmlContent(clbValorMensalidade): ""%></td>
                <td><%=!TextHelper.isNull(clbValorSubsidio) ? TextHelper.forHtmlContent(clbValorSubsidio): ""%></td>
                <% if(podeAlterarCalculoBeneficio && statusRegra.equals("1")) { %>
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
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/alterarCalculoBeneficio?acao=editar&clbCodigo=<%=TextHelper.forJavaScriptAttribute(clbCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                          <hl:message key="rotulo.acoes.editar" />
                        </a> 
                        <a class="dropdown-item" href="#no-back" onClick="confirmarExclusao('../v3/alterarCalculoBeneficio?acao=excluir&clbCodigo=<%=TextHelper.forJavaScriptAttribute(clbCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
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
                        <a class="dropdown-item" href="#no-back" onClick="postData('../v3/consultarCalculoBeneficio?acao=visualizar&clbCodigo=<%=TextHelper.forJavaScriptAttribute(clbCodigo)%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')">
                          <hl:message key="rotulo.acoes.detalhar" />
                        </a>
                      </div>
                    </div>
                  </div>
                </td>
                <% } %>
              </tr>
              <% } %>
            </tbody>
            <tfoot>
              <tr>
                <td colspan="12"><%=ApplicationResourcesHelper.getMessage("mensagem.listagem.calculo.beneficio", responsavel) + " - "%>
                  <span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTitulo"))%></span>
                </td>
              </tr>
            </tfoot>
          </table>
        </div>
        <div class="card-footer">
          <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
        </div>
      </div>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;" id="btnVoltar"><hl:message key="rotulo.botao.voltar"/></a>
    <% if (podeAlterarCalculoBeneficio && !ativarTabela && !tabelaVazia) {%>
    <a class="btn btn-primary" href="#no-back" onClick="postData('../v3/alterarCalculoBeneficio?acao=iniciarTabela&<%=SynchronizerToken.generateToken4URL(request)%>'); return false;"><hl:message key="rotulo.botao.calculo.beneficio.iniciar.tabela" /></a>
    <% } %>
    <% if (statusRegra != null && statusRegra.equals("1") && podeAlterarCalculoBeneficio && ativarTabela) { %>
      <a class="btn btn-primary" href="#no-back" onClick="confirmarExcluirTabelaIniciada('../v3/alterarCalculoBeneficio?acao=excluirTabelaIniciada&<%=SynchronizerToken.generateToken4URL(request)%>')"><hl:message key="rotulo.botao.excluir.tabela.iniciada"/></a>
    <% } %>
    <% if (podeAlterarCalculoBeneficio && ativarTabela) {%> 
    <a class="btn btn-primary" href="#no-back" onClick="confirmarAtivacao('../v3/alterarCalculoBeneficio?acao=ativarTabela&<%=SynchronizerToken.generateToken4URL(request)%>')"><hl:message key="rotulo.botao.calculo.beneficio.ativar.tabela" /></a> 
    <% } %>
  </div>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>