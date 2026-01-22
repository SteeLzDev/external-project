<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.NumberHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TransferObjectHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.TransferObject" %>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject" %>
<%@ page import="com.zetra.econsig.dto.entidade.ParamSvcTO" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<% 
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List<TransferObject> lstConsignatarias = (List<TransferObject> ) request.getAttribute("lstConsignatarias");
List<TransferObject> adesAnexo = (List<TransferObject>) request.getAttribute("adesAnexo");
boolean temAnexoPendente = "true".equals(request.getParameter("temAnexoPendente"));
%>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="title">
  <hl:message key="mensagem.dashboard.anexos.consignacao.titulo"/>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <div class="col-sm-6 col-md-6">
      <form name="form1" method="post" ACTION="../v3/visualizarDashboardAnexo">
        <% out.print(SynchronizerToken.generateHtmlToken(request)); %>
        <input type="hidden" name="acao" value="iniciar"/>
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
                  <div class="form-group col-sm-12">
                    <label for="EST_CODIGO"><hl:message key="rotulo.estabelecimento.singular"/></label>
                    <hl:htmlcombo listName="lstEstabelecimento" name="EST_CODIGO" fieldValue="<%=Columns.EST_CODIGO%>" fieldLabel="<%=Columns.EST_NOME + ";" + Columns.EST_IDENTIFICADOR%>" notSelectedLabel='<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>' autoSelect="true" classe="form-control"/>
                  </div>
                </div>
              </c:if>
              <c:if test="${not empty lstOrgao}">
                <div class="row">
                  <div class="form-group col-sm-12">
                    <label for="ORG_CODIGO"><hl:message key="rotulo.orgao.singular"/></label>
                    <hl:htmlcombo listName="lstOrgao" name="ORG_CODIGO" fieldValue="<%=Columns.ORG_CODIGO%>" fieldLabel="<%=Columns.ORG_NOME + ";" + Columns.ORG_IDENTIFICADOR%>" notSelectedLabel='<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>' autoSelect="true" classe="form-control"/>
                  </div>
                </div>
              </c:if>
              <c:if test="${not empty lstConsignataria}">
                <div class="row">
                  <div class="form-group col-sm-12">
                    <label for="CSA_CODIGO"><hl:message key="rotulo.consignataria.singular"/></label>
                    <hl:htmlcombo listName="lstConsignataria" name="CSA_CODIGO" fieldValue="<%=Columns.CSA_CODIGO%>" fieldLabel="<%=Columns.getColumnName(Columns.CSA_NOME_ABREV)  + ";" + Columns.CSA_IDENTIFICADOR%>" notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>" autoSelect="true" classe="form-control"/>
                  </div>
                </div>
              </c:if>
              <c:if test="${not empty lstCorrespondente}">
                <div class="row">
                  <div class="form-group col-sm-12">
                    <label for="COR_CODIGO"><hl:message key="rotulo.correspondente.singular"/></label>
                    <hl:htmlcombo listName="lstCorrespondente" name="COR_CODIGO" fieldValue="<%=Columns.COR_CODIGO%>" fieldLabel="<%=Columns.getColumnName(Columns.COR_NOME)  + ";" + Columns.COR_IDENTIFICADOR%>" notSelectedLabel="<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>" autoSelect="true" classe="form-control"/>
                  </div>
                </div>
              </c:if>
              <c:if test="${not empty lstServico}">
                <div class="row">
                  <div class="form-group col-sm-12">
                    <label for="SVC_CODIGO"><hl:message key="rotulo.servico.singular"/></label>
                    <hl:htmlcombo listName="lstServico" name="SVC_CODIGO" fieldValue="<%=Columns.SVC_CODIGO%>" fieldLabel="<%=Columns.SVC_DESCRICAO + ";" + Columns.SVC_IDENTIFICADOR%>" notSelectedLabel='<%=ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavel)%>' autoSelect="true" classe="form-control"/>
                  </div>
                </div>
              </c:if>
            </fieldset>
            <fieldset>
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
              <div class="row">
                <div class="form-group col-sm-12">
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
            </fieldset>
            <fieldset>
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
                  <hl:campoCPFv4 placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cpf", responsavel)%>' classe="form-control" nf="btnPesquisar"/>
                </div>
              </div>
            </fieldset>
            <fieldset>
              <h3 class="legend">
                <span><hl:message key="rotulo.dashboard.anexos.consignacao.filtro.pesquisa.titulo"/></span>
              </h3>
              <div class="row">
                <div class="form-group col-sm-12">
                  <label for="temAnexoPendente" class="pr-2"><hl:message key="rotulo.dashboard.anexos.consignacao.filtro.pendencia.anexo"/></label>
                  <input name="temAnexoPendente" id="temAnexoPendente" type="checkbox" value="true" <%= temAnexoPendente ? " checked " : "" %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"/>
                </div>
              </div>
            </fieldset>
          </div>
        </div>
        <div class="btn-action">
          <a class="btn btn-outline-danger" id="btnVoltar" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
          <a class="btn btn-outline-danger" id="btnLimpar" href="#no-back" onClick="postData('../v3/visualizarDashboardAnexo?acao=iniciar'); return false;"><hl:message key="rotulo.botao.limpar.filtros"/></a>
          <a class="btn btn-primary" id="btnPesquisar" href="#no-back" onClick="pesquisar(); return false;"><svg width="20"><use xlink:href="../img/sprite.svg#i-consultar"></use></svg><hl:message key="rotulo.botao.pesquisar"/></a>
        </div>
      </form>
    </div>
    <div class="col-sm-6 col-md-6">
      <div class="card">
        <div class="card-header hasIcon pl-3">
          <h2 class="card-header-title"><hl:message key="mensagem.dashboard.anexos.consignacao.card.consignatarias.titulo"/></h2>
        </div>
        <div class="card-body table-responsive p-0">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th scope="col"><hl:message key="rotulo.consignataria.singular"/></th>
                <th scope="col"><hl:message key="rotulo.consignataria.codigo"/></th>                           
                <th scope="col"><hl:message key="rotulo.dashboard.anexos.consignacao.qtd.pendencias"/></th>
                <th scope="col"><hl:message key="rotulo.acoes"/></th>
            </tr>
          </thead>
          <tbody>
          <%
          if (lstConsignatarias != null && !lstConsignatarias.isEmpty()) {
              for (TransferObject csa : lstConsignatarias) {
                String csaId = (String) csa.getAttribute(Columns.CSA_IDENTIFICADOR);
                String csaNome = (String) csa.getAttribute(Columns.CSA_NOME_ABREV);
                if (TextHelper.isNull(csaNome)) {
                   csaNome = (String) csa.getAttribute(Columns.CSA_NOME);
                }
                if (csaNome.length() > 50) {
                   csaNome = csaNome.substring(0, 47) + "...";
                }
                String qtd = csa.getAttribute("QTD_CONSIGNACOES").toString();
          %>               
            <tr>
              <td><%=TextHelper.forHtmlContent(csaNome)%></td>
              <td><%=TextHelper.forHtmlContent(csaId)%></td>
              <td class="text-right"><%=TextHelper.forHtmlContent(qtd)%></td>
              <td><a href="#no-back" onClick="detalharCSA('<%=TextHelper.forJavaScript(csa.getAttribute(Columns.CSA_CODIGO).toString())%>');" id="selecionaCSA" aria-label="<hl:message key="rotulo.botao.detalhar"/>"><hl:message key="rotulo.botao.detalhar"/></a></td>
            </tr>
          <% 
              }
          }
          %>
           </tbody>  
            <tfoot>
            <tr>
              <td colspan="3"><%=ApplicationResourcesHelper.getMessage("mensagem.dashboard.anexos.consignacao.card.consignatarias.rodape", responsavel) + " - " %>
                <span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTituloConsignatarias"))%></span>
              </td>
            </tr>
          </tfoot>
          </table>
          <% if (lstConsignatarias != null && !lstConsignatarias.isEmpty()) { %>
          <% request.setAttribute("_indice", "Consignatarias"); %>
          <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
          <% } %>
        </div>
      </div>
    </div>
  </div>
  <div class="card">
    <div class="card-header hasIcon pl-3">
      <h2 class="card-header-title"><hl:message key="<%= !temAnexoPendente ? "mensagem.dashboard.anexos.consignacao.card.ultimos.anexos.titulo" : "mensagem.dashboard.anexos.consignacao.card.consignacao.sem.anexo.titulo" %>"/></h2>
    </div>
    <div class="card-body table-responsive p-0">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col"><hl:message key="rotulo.consignataria.singular"/></th>
            <th scope="col"><hl:message key="rotulo.ocorrencia.responsavel"/></th>
            <th scope="col"><hl:message key="rotulo.consignacao.numero"/></th>
            <th scope="col"><hl:message key="rotulo.consignacao.identificador"/></th>
            <th scope="col"><hl:message key="rotulo.servico.singular"/></th>
            <th scope="col"><hl:message key="rotulo.servidor.singular"/></th>
            <th scope="col"><hl:message key="rotulo.consignacao.data.inclusao"/></th>
            <th scope="col"><hl:message key="rotulo.consignacao.valor.parcela.abreviado"/></th>
            <th scope="col"><hl:message key="rotulo.consignacao.prazo.abreviado"/></th>
            <th scope="col"><hl:message key="rotulo.consignacao.pagas"/></th>
            <th scope="col"><hl:message key="rotulo.consignacao.status"/></th>
            <th scope="col"><hl:message key="rotulo.acoes"/></th>
          </tr>
        </thead>
        <tbody>
          <% 
          if (adesAnexo != null && !adesAnexo.isEmpty()) {
              String adeCodigo, adeNumero, adeTipoVlr, adeData, adePrazo, adeVlr, adeIdentificador, prdPagas, adeCodReg;
              String servico, servidor, serTel, sadDescricao, cpf;
              String csaNome, csaId, loginResponsavel, adeResponsavel;
              String rseMatricula, serNome, serCpf;

              for (TransferObject ade : adesAnexo) {
                  ade = TransferObjectHelper.mascararUsuarioHistorico((CustomTransferObject) ade, null, responsavel);
                  adeCodigo = ade.getAttribute(Columns.ADE_CODIGO).toString();

                  adeTipoVlr = (ade.getAttribute(Columns.ADE_TIPO_VLR) != null ? ade.getAttribute(Columns.ADE_TIPO_VLR).toString() : CodedValues.TIPO_VLR_FIXO);
                  adeVlr = ade.getAttribute(Columns.ADE_VLR) != null ? ade.getAttribute(Columns.ADE_VLR).toString() : "";
                  if (!adeVlr.equals("")) {
                      adeVlr = NumberHelper.format(Double.valueOf(adeVlr).doubleValue(), NumberHelper.getLang());
                  }

                  adeNumero = ade.getAttribute(Columns.ADE_NUMERO).toString();
                  adeIdentificador = ade.getAttribute(Columns.ADE_IDENTIFICADOR) != null ? ade.getAttribute(Columns.ADE_IDENTIFICADOR).toString() : "";
                  adePrazo = ade.getAttribute(Columns.ADE_PRAZO) != null ? ade.getAttribute(Columns.ADE_PRAZO).toString() : "Indeterminado";
                  adeData = DateHelper.reformat(ade.getAttribute(Columns.ADE_DATA).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern());
                  prdPagas = ade.getAttribute(Columns.ADE_PRD_PAGAS) != null ? ade.getAttribute(Columns.ADE_PRD_PAGAS).toString() : "0";
                  servico = (ade.getAttribute(Columns.CNV_COD_VERBA) != null && !ade.getAttribute(Columns.CNV_COD_VERBA).toString().equals("")) ? ade.getAttribute(Columns.CNV_COD_VERBA).toString() : ade.getAttribute(Columns.SVC_IDENTIFICADOR).toString();
                  servico += (ade.getAttribute(Columns.ADE_INDICE) != null && !ade.getAttribute(Columns.ADE_INDICE).toString().equals("")) ? ade.getAttribute(Columns.ADE_INDICE).toString() : "";
                  servico += " - " + ade.getAttribute(Columns.SVC_DESCRICAO).toString();

                  csaId = (String) ade.getAttribute(Columns.CSA_IDENTIFICADOR);
                  csaNome = (String) ade.getAttribute(Columns.CSA_NOME_ABREV);
                  if (TextHelper.isNull(csaNome)) {
                     csaNome = (String) ade.getAttribute(Columns.CSA_NOME);
                  }
                  if (csaNome.length() > 50) {
                     csaNome = csaNome.substring(0, 47) + "...";
                  }

                  loginResponsavel = ade.getAttribute(Columns.USU_LOGIN) != null ? ade.getAttribute(Columns.USU_LOGIN).toString() : "";
                  adeResponsavel = (loginResponsavel.equalsIgnoreCase((String) ade.getAttribute(Columns.USU_CODIGO)) && ade.getAttribute(Columns.USU_TIPO_BLOQ) != null) ? (ade.getAttribute(Columns.USU_TIPO_BLOQ).toString() + "(*)") : loginResponsavel;

                  rseMatricula = ade.getAttribute(Columns.RSE_MATRICULA).toString();
                  serNome = ade.getAttribute(Columns.SER_NOME).toString();
                  serCpf = ade.getAttribute(Columns.SER_CPF).toString();
                  
                  sadDescricao = ade.getAttribute(Columns.SAD_DESCRICAO).toString();
          %>
          <tr>
            <td><%=TextHelper.forHtmlContent(csaNome) + " - " + TextHelper.forHtmlContent(csaId)%></td>
            <td><%=TextHelper.forHtmlContent(adeResponsavel)%></td>
            <td><%=TextHelper.forHtmlContent(adeNumero)%></td>
            <td><%=TextHelper.forHtmlContent(adeIdentificador)%></td>
            <td><%=TextHelper.forHtmlContent(servico)%></td>
            <td><%=TextHelper.forHtmlContent(rseMatricula) + " - " + TextHelper.forHtmlContent(serCpf) + " - " + TextHelper.forHtmlContent(serNome)%></td>
            <td><%=TextHelper.forHtmlContent(adeData)%></td>
            <td class="text-right"><%=TextHelper.forHtmlContent(ParamSvcTO.getDescricaoTpsTipoVlr(adeTipoVlr))%> <%=TextHelper.forHtmlContent(adeVlr)%></td>
            <td class="text-right"><%=TextHelper.forHtmlContent(adePrazo)%></td>
            <td class="text-right"><%=TextHelper.forHtmlContent(prdPagas)%></td>
            <td><%=TextHelper.forHtmlContent(sadDescricao)%></td>     
            <td>
              <% if (!temAnexoPendente) { %>
                <div class="actions">
                  <div class="dropdown">
                    <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                      <div class="form-inline">
                        <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.mais.acoes"/>" aria-label="<hl:message key="rotulo.mais.acoes"/>"> <svg>
                          <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                        </span> <hl:message key="rotulo.botao.opcoes"/>
                      </div>
                    </a>
                    <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                      <a class="dropdown-item" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL("../v3/visualizarDashboardAnexo?acao=downloadAnexos&ADE_CODIGO=" + adeCodigo, request))%>')" id="downloadADE">
                        <hl:message key="rotulo.download.anexos.consignacao.titulo"/>
                      </a>
                      <a class="dropdown-item" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL("../v3/visualizarDashboardAnexo?acao=detalharConsignacao&ADE_CODIGO=" + adeCodigo, request))%>')" id="detalharADE">
                        <hl:message key="rotulo.visualizar.consignacao.titulo"/>
                      </a>
                    </div>
                  </div>
                </div>
              <% } else { %>
                <a href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL("../v3/visualizarDashboardAnexo?acao=detalharConsignacao&ADE_CODIGO=" + adeCodigo, request))%>')" id="detalharADE" aria-label="<hl:message key="rotulo.botao.detalhar"/>"><hl:message key="rotulo.visualizar.consignacao.titulo"/></a>
              <% } %>
            </td>
          </tr>
        <%
            }
          }
        %>
        </tbody>
        <tfoot>
          <tr>
            <td colspan="12"><%=ApplicationResourcesHelper.getMessage(!temAnexoPendente ? "mensagem.dashboard.anexos.consignacao.card.ultimos.anexos.rodape" : "mensagem.dashboard.anexos.consignacao.card.consignacao.sem.anexo.rodape", responsavel) + " - " %>
              <span class="font-italic"><%=TextHelper.forHtmlContent(request.getAttribute("_paginacaoSubTituloAnexo"))%></span>
            </td>
          </tr>
        </tfoot>
      </table>
      <% if (adesAnexo != null && !adesAnexo.isEmpty()) { %>
      <% request.setAttribute("_indice", "Anexo"); %>
      <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
      <% } %>
    </div>
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
  
  function detalharCSA(csa) {
    postData('../v3/visualizarDashboardAnexo?acao=iniciar&temAnexoPendente=true&CSA_CODIGO=' + csa);
  }
  
  function pesquisar() {
    selecionarTodosItens('ADE_NUMERO_LIST');
    f0.submit();
  }

  $(document).ready(function() {
    formLoad();
  });
  </script>
  <hl:campoMatriculav4 scriptOnly="true"/>
</c:set>
<t:page_v4>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>