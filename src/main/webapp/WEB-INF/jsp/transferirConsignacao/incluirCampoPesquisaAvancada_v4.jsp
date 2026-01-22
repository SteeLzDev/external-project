<%--
* <p>Title: filtrarConsignacao_v4</p>
* <p>Description: Página de pesquisa avançada de consignação</p>
* <p>Copyright: Copyright (c) 2007</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author: rodrigo.rosa $
* $Revision: 27230 $
* $Date: 2019-07-15 13:12:56 -0300 (seg, 15 jul 2019) $
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%
String filtroPesquisa = JspHelper.verificaVarQryStr(request, "filtro_pesquisa"); 
AcessoSistema _responsavel = JspHelper.getAcessoSistema(request);

List<TransferObject> gruposConsignataria = (List<TransferObject>) request.getAttribute("lstGrupoConsignataria");
List<TransferObject> consignatarias = (List<TransferObject>) request.getAttribute("lstConsignataria");
List<TransferObject> correspondentes  = (List<TransferObject>) request.getAttribute("lstCorrespondente");
List<TransferObject> convenios  = (List<TransferObject>) request.getAttribute("lstConvenio");
List<TransferObject> gruposServico  = (List<TransferObject>) request.getAttribute("lstGrupoServico");
List<TransferObject> orgaos  = (List<TransferObject>) request.getAttribute("lstOrgao");
List<TransferObject> _margens  = (List<TransferObject>) request.getAttribute("lstMargem");
List<TransferObject> tipoMotivoOperacao  = (List<TransferObject>) request.getAttribute("lstMotivoOperacaoConsignacao");
String mascaraIndice = (String) request.getAttribute("mascaraIndice");

// Booleano do Parâmetro de obrigatoriedade de CPF e Matrícula
Boolean requerMatriculaCpf = (Boolean) request.getAttribute("requerMatriculaCpf");

String rotuloCampoTodos = (String) request.getAttribute("rotuloCampoTodos");
String ordenacao = (String) request.getAttribute("ordenacao");
List<?> lstOrdenacaoAux = (List<?>) request.getAttribute("lstOrdenacaoAux");
String ordemAdeData = (String) request.getAttribute("ordemAdeData");
String ordemSerCpf = (String) request.getAttribute("ordemSerCpf");
String ordemRseMatricula = (String) request.getAttribute("ordemRseMatricula");
String ordemSerNome = (String) request.getAttribute("ordemSerNome");
%>
<div class="opcoes-avancadas">
  <a class="opcoes-avancadas-head" href="#faq1" data-bs-toggle="collapse" aria-expanded="false" aria-controls="faq1" aria-label='<hl:message key="mensagem.inclusao.avancada.clique.aqui"/>'><hl:message key="rotulo.avancada.opcoes"/></a>
  <div class="collapse" id="faq1">
    <div class="opcoes-avancadas-body pl-4">
      <form action="../v3/transferirConsignacao" method="post" name="formPesqAvancada">
        <%= SynchronizerToken.generateHtmlToken(request) %>
        <input type="hidden" name="acao" value="listarConsignacoes" />
<%--         <hl:htmlinput name="acao" di="acao" type="hidden" value="pesquisarConsignacao" /> --%>
<%--         <hl:htmlinput name="FORM" di="FORM" type="hidden" value="formPesqAvancada" /> --%>
<%--         <hl:htmlinput name="TIPO_LISTA" di="TIPO_LISTA" type="hidden" value="pesquisa_avancada" /> --%>
<%--         <input type="hidden" name="RSE_MATRICULA_ORI" value="<%=TextHelper.forHtmlAttribute(rseMatriculaOri)%>" /> --%>
<%--         <input type="hidden" name="RSE_MATRICULA_DES" value="<%=TextHelper.forHtmlAttribute(rseMatriculaDes)%>" /> --%>
        <input type="hidden" name="RSE_CODIGO_ORI" value="<%=request.getAttribute("rseCodigoOri")%>" />
        <input type="hidden" name="RSE_CODIGO_DES" value="<%=request.getAttribute("rseCodigoDes")%>" />
        <fieldset>
          <h3 class="legend">
            <span><hl:message key="rotulo.consultar.consignacao.dados.convenio"/></span>
          </h3>
          <div class="row">

            <% if (_responsavel.isCseSupOrg() && gruposConsignataria != null && gruposConsignataria.size() > 0) { %>
              <div class="form-group col-sm-12 col-md-6">
                <label for="TGC_CODIGO"><hl:message key="rotulo.grupo.consignataria.singular"/></label>
                <%= JspHelper.geraCombo(gruposConsignataria, "TGC_CODIGO", Columns.TGC_CODIGO, Columns.TGC_DESCRICAO, rotuloCampoTodos, null, false, 1, JspHelper.verificaVarQryStr(request, "TGC_CODIGO"), null, false, "form-control form-select") %>
              </div>
            <% } %>

            <% if ((_responsavel.isCseSupOrg() || _responsavel.isSer()) && consignatarias != null && consignatarias.size() > 0) { %>
              <div class="form-group col-sm-12 col-md-6">
                <label for="CSA_CODIGO"><hl:message key="rotulo.consignataria.singular"/></label>
                <%= JspHelper.geraCombo(consignatarias, "CSA_CODIGO", Columns.CSA_CODIGO, Columns.getColumnName(Columns.CSA_NOME_ABREV) + ";" + Columns.CSA_IDENTIFICADOR, rotuloCampoTodos, null, false, 1, JspHelper.verificaVarQryStr(request, "CSA_CODIGO"), null, false, "form-control form-select") %>
              </div>
            <% } %>

            <% if (_responsavel.isCsa() && correspondentes != null && correspondentes.size() > 0) { %>
              <div class="form-group col-sm-12 col-md-6">
                <label for="COR_CODIGO"><hl:message key="rotulo.correspondente.singular"/></label>
                <%= JspHelper.geraCombo(correspondentes, "COR_CODIGO", Columns.COR_CODIGO, Columns.COR_NOME + ";" + Columns.COR_IDENTIFICADOR, rotuloCampoTodos, null, false, 1, JspHelper.verificaVarQryStr(request, "COR_CODIGO"), null, false, "form-control form-select") %>
              </div>
            <% } %>

            <% if ((_responsavel.isCseSup() || _responsavel.isCsaCor()) && orgaos != null && orgaos.size() > 0) { %>
              <div class="form-group col-sm-12 col-md-6">
                <label for="ORG_CODIGO"><hl:message key="rotulo.orgao.singular"/></label>
                <%= JspHelper.geraCombo(orgaos, "ORG_CODIGO", Columns.ORG_CODIGO, Columns.ORG_NOME + ";" + Columns.ORG_IDENTIFICADOR, rotuloCampoTodos, null, false, 1, JspHelper.verificaVarQryStr(request, "ORG_CODIGO"), null, false, "form-control form-select") %>
              </div>
            <% } %>

            <% if ((_responsavel.isCseSupOrg() || _responsavel.isSer()) && gruposServico != null && gruposServico.size() > 0) { %>
              <div class="form-group col-sm-12 col-md-6">
                <label for="TGS_CODIGO"><hl:message key="rotulo.grupo.servico.titulo"/></label>
                <%= JspHelper.geraCombo(gruposServico, "TGS_CODIGO", Columns.TGS_CODIGO, Columns.TGS_GRUPO, rotuloCampoTodos, null, false, 1, JspHelper.verificaVarQryStr(request, "TGS_CODIGO"), null, false, "form-control form-select") %>
              </div>
            <% } %>

            <% if (convenios != null && convenios.size() > 0) { %>
              <div class="form-group col-sm-12 col-md-6">
                <label for="SVC_CODIGO"><hl:message key="rotulo.servico.singular"/></label>
                <%= JspHelper.geraCombo(convenios, "SVC_CODIGO", Columns.SVC_CODIGO, Columns.SVC_DESCRICAO + ";" + Columns.SVC_IDENTIFICADOR, rotuloCampoTodos, null, false, 1, JspHelper.verificaVarQryStr(request, "SVC_CODIGO"), null, false, "form-control form-select") %>
              </div>
            <% } %>

            <div class="form-group col-sm-12 col-md-6">
              <label for="CNV_COD_VERBA"><hl:message key="rotulo.codigo.verba.singular"/></label>
              <hl:htmlinput name="CNV_COD_VERBA" 
                            di="CNV_COD_VERBA" 
                            type="text" 
                            classe="form-control"
                            onFocus="SetarEventoMascaraV4(this,'#*32',true);" 
                            placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.codigo.verba", _responsavel)%>'
                            value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "CNV_COD_VERBA"))%>" 
              />
            </div>
          </div>
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
                            onFocus="SetarEventoMascaraV4(this,'#D20',true);"
                            placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ade.numero", _responsavel)%>'
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

            <div class="form-group col-sm-12 col-md-6">
              <label for="ADE_IDENTIFICADOR"><hl:message key="rotulo.consignacao.identificador"/></label>
              <hl:htmlinput name="ADE_IDENTIFICADOR" 
                            di="ADE_IDENTIFICADOR" 
                            type="text" 
                            classe="form-control"
                            onFocus="SetarEventoMascaraV4(this,'#*40',true);" 
                            placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ade.identificador", _responsavel)%>'
                            value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "ADE_IDENTIFICADOR"))%>" 
              />
            </div>

            <% if (!TextHelper.isNull(mascaraIndice)) { %>
              <div class="form-group col-sm-12 col-md-6">
                <label for="ADE_INDICE"><hl:message key="rotulo.consignacao.indice"/></label>
                <hl:htmlinput name="ADE_INDICE" 
                              di="ADE_INDICE" 
                              type="text" 
                              classe="form-control" 
                              onFocus="SetarEventoMascaraV4(this,'<%=mascaraIndice%>',true);" 
                              placeHolder='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ade.indice", _responsavel)%>'
                              value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "ADE_INDICE"))%>" 
                />
              </div>
            <% } %>

            <div class="form-group col-sm-12 col-md-6 cep-input">
              <span id="periodo"><hl:message key="rotulo.pesquisa.data.periodo"/></span>
              <div class="row" role="group" aria-labelledby="periodo">
                <div class="form-group col-sm-12 col-md-5">
                  <hl:htmlinput name="ADE_ANO_MES_INI" 
                                di="ADE_ANO_MES_INI" 
                                type="text" 
                                classe="form-control w-100 mr-2" 
                                size="10" 
                                onFocus="SetarEventoMascaraV4(this,'DD/DDDD',true);" 
                                placeHolder="MM/AAAA"
                                ariaLabel='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.data.periodo", _responsavel)%>'
                                value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "ADE_ANO_MES_INI"))%>" 
                  />
                </div>
                <% String tipoOcorrenciaPeriodo = JspHelper.verificaVarQryStr(request, "tipoOcorrenciaPeriodo");%>
                <div class="form-group col-sm-12 col-md-7">
                  <select class="form-control form-select w-100" id="tipoOcorrenciaPeriodo" name="tipoOcorrenciaPeriodo" aria-label='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.tipo.periodo", _responsavel)%>' onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);">
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
                                onFocus="SetarEventoMascaraV4(this,'<%=LocaleHelper.getDateJavascriptPattern()%>',true);" 
                                value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "periodoIni"))%>" 
                                ariaLabel='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.data.inclusao.ini", _responsavel)%>'
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
                                onFocus="SetarEventoMascaraV4(this,'<%=LocaleHelper.getDateJavascriptPattern()%>',true);" 
                                value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, "periodoFim"))%>" 
                                ariaLabel='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.data.inclusao.fim", _responsavel)%>'
                  />
                </div>
              </div>
            </div>
            
            <div class="col-sm-12 col-md-12">
              <h3 class="legend">
                <span id="situacaoContrato"><hl:message key="rotulo.consignacao.status.contrato"/></span>
              </h3>
              <hl:filtroStatusAdeTagv4 />
            </div>

          </div>
        </fieldset>

        <fieldset>
          <h3 class="legend">
            <span><hl:message key="rotulo.consultar.consignacao.demais.informacoes"/></span>
          </h3>
          <div class="form-group">
            <% if (_margens != null && _margens.size() > 0) { %>
            <div class="form-group col-sm-12 col-md-6">
              <label for="ADE_INC_MARGEM"><hl:message key="rotulo.consignacao.incide.margem"/></label>
              <%= JspHelper.geraCombo(_margens, "ADE_INC_MARGEM", Columns.MAR_CODIGO, Columns.MAR_DESCRICAO, rotuloCampoTodos, null, false, 1, JspHelper.verificaVarQryStr(request, "ADE_INC_MARGEM"), null, false, "form-control") %>
            </div>
            <% } %>

            <% if (tipoMotivoOperacao != null && tipoMotivoOperacao.size() > 0) { %>
            <div class="form-group col-sm-12 col-md-6">
              <label for="TMO_CODIGO"><hl:message key="rotulo.efetiva.acao.consignacao.dados.tipo.mtv.cancelamento"/></label>
              <%= JspHelper.geraCombo(tipoMotivoOperacao, "TMO_CODIGO", Columns.TMO_CODIGO, Columns.TMO_DESCRICAO, rotuloCampoTodos, null, false, 1, JspHelper.verificaVarQryStr(request, "TMO_CODIGO"), null, false, "form-control") %>
            </div>
            <% } %>

            <% if (_responsavel.isCseSup()) { %>
            <div class="col-sm-12 col-md-6">
              <div class="form-group mb-0">
                <span id="listarConsignacoesArquivadas"><hl:message key="rotulo.consignacao.historico.arquivado"/></span>
              </div>
              <div class="form-check form-check-inline mt-2" role="radiogroup" aria-labelledby="listarConsignacoesArquivadas">
                <input class="form-check-input" type="radio" name="arquivado" id="arquivado_sim" value="S" <%= (JspHelper.verificaVarQryStr(request, "arquivado").equals("S")) ? "checked" : "" %>/>
                <label class="text-nowrap labelSemNegrito form-check-label align-text-top" for="arquivado_sim"><hl:message key="rotulo.sim"/></label>
              </div>
              <div class="form-check form-check-inline">
                <input class="form-check-input" type="radio" name="arquivado" id="arquivado_nao" value="N" <%=(!JspHelper.verificaVarQryStr(request, "arquivado").equals("S")) ? "checked" : "" %>/>
                <label class="text-nowrap form-check-label labelSemNegrito align-text-top" for="arquivado_nao"><hl:message key="rotulo.nao"/></label>
              </div>
            </div>
            <% } %>

            <% if (_responsavel.isCsaCor()) { %>
            <div class="col-sm-12 col-md-6">
              <div class="form-group mb-0">
                <span id="listarMinhasConsignacoes"><hl:message key="rotulo.pesquisa.minhas.reservas"/></span>
              </div>
              <div class="form-check form-check-inline mt-2" role="radiogroup" aria-labelledby="listarMinhasConsignacoes">
                <input class="form-check-input" type="radio" name="adePropria" id="adePropria_sim" value="1" <%= (JspHelper.verificaVarQryStr(request, "adePropria").equals("1")) ? "checked" : "" %>/>
                <label class="form-check-label labelSemNegrito" for="adePropria_sim"><hl:message key="rotulo.sim"/></label>
              </div>
              <div class="form-check form-check-inline  mt-2">
                <input class="form-check-input" type="radio" name="adePropria" id="adePropria_nao" value="0" <%=(!JspHelper.verificaVarQryStr(request, "adePropria").equals("1")) ? "checked" : "" %>/>
                <label class="form-check-label labelSemNegrito"  for="adePropria_nao"><hl:message key="rotulo.nao"/></label>
              </div>
            </div>
            <% } %>
              <div class="col-sm-12 col-md-6">
                <div class="form-group mb-0">
                  <span id="integrafolha"><hl:message key="rotulo.consignacao.integra.folha"/></span>
                </div>
                <div class="form-check form-check-inline mt-2" role="radiogroup" aria-labelledby="integrafolha">
                    <input class="form-check-input" type="radio" name="ADE_INT_FOLHA" id="ADE_INT_FOLHA_1" value="1" <%= (JspHelper.verificaVarQryStr(request, "ADE_INT_FOLHA").equals("1")) ? " checked " : "" %>/>
                    <label class="text-nowrap labelSemNegrito align-text-top" for="ADE_INT_FOLHA_1"><hl:message key="rotulo.sim"/></label>
                  </div>
                  <div class="form-check form-check-inline" >
                    <input class="form-check-input" type="radio" name="ADE_INT_FOLHA" id="ADE_INT_FOLHA_0" value="0" <%= (JspHelper.verificaVarQryStr(request, "ADE_INT_FOLHA").equals("0")) ? " checked " : "" %>/>
                    <label class="text-nowrap labelSemNegrito align-text-top" for="ADE_INT_FOLHA_0"><hl:message key="rotulo.nao"/></label>
                  </div>
                </div>
              </div>
              <div class="col-sm-12 col-md-12">
                <h3 class="legend">
                  <span id="saldoDevedor"><hl:message key="rotulo.saldo.devedor.singular"/></span>
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

        </fieldset>

        <fieldset>
          <h3 class="legend">
            <span><hl:message key="rotulo.ordenacao"/></span>
          </h3>

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
            <div class="col-sm-12 col-md-12">
              <div class="row">
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
                            <label class="form-check-label formatacao ml-1 pr-4" for="ORDEM_DATA1" aria-label='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ordenacao.data.asc", _responsavel)%>'><hl:message key="rotulo.crescente"/></label>
                          </div>
                          <div class="col-sm-12 col-md-4">
                            <input class="form-check-input ml-1" type="radio" name="ORDEM_DATA" id="ORDEM_DATA2" value="DESC" <%= (TextHelper.isNull(ordemAdeData) || ordemAdeData.equals("DESC")) ? " checked " : "" %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"/>
                            <label class="form-check-label formatacao ml-1" for="ORDEM_DATA2" aria-label='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ordenacao.data.desc", _responsavel)%>'><hl:message key="rotulo.decrescente"/></label>
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
                            <label class="form-check-label formatacao ml-1 pr-4" for="ORDEM_CPF1" aria-label='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ordenacao.cpf.asc", _responsavel)%>'><hl:message key="rotulo.crescente"/></label>
                          </div>
                          <div class="col-sm-12 col-md-4">
                            <input class="form-check-input ml-1" type="radio" name="ORDEM_CPF" id="ORDEM_CPF2" value="DESC" <%= ((!TextHelper.isNull(ordemSerCpf) && ordemSerCpf.equals("DESC"))) ? " checked " : "" %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"/>
                            <label class="form-check-label formatacao ml-1" for="ORDEM_CPF2" aria-label='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ordenacao.cpf.desc", _responsavel)%>'><hl:message key="rotulo.decrescente"/></label>
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
                            <label class="form-check-label formatacao ml-1 pr-4" for="ORDEM_MATRICULA1" aria-label='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ordenacao.matricula.asc", _responsavel)%>'><hl:message key="rotulo.crescente"/></label>
                          </div>
                          <div class="col-sm-12 col-md-4">
                            <input class="form-check-input ml-1" type="radio" name="ORDEM_MATRICULA" id="ORDEM_MATRICULA2" value="DESC" <%= ((!TextHelper.isNull(ordemRseMatricula) && ordemRseMatricula.equals("DESC"))) ? " checked " : "" %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"/>
                            <label class="form-check-label formatacao ml-1" for="ORDEM_MATRICULA2" aria-label='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ordenacao.matricula.desc", _responsavel)%>'><hl:message key="rotulo.decrescente"/></label>
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
                            <label class="form-check-label formatacao ml-1 pr-4" for="ORDEM_NOME1" aria-label='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ordenacao.nome.asc", _responsavel)%>'><hl:message key="rotulo.crescente"/></label>
                          </div>
                          <div class="col-sm-12 col-md-4">
                            <input class="form-check-input ml-1" type="radio" name="ORDEM_NOME" id="ORDEM_NOME2" value="DESC" <%= ((!TextHelper.isNull(ordemSerNome) && ordemSerNome.equals("DESC"))) ? " checked " : "" %> onFocus="SetarEventoMascara(this,'#*100',true);" onBlur="fout(this);ValidaMascara(this);"/>
                            <label class="form-check-label formatacao ml-1" for="ORDEM_NOME2" aria-label='<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.ordenacao.nome.desc", _responsavel)%>'><hl:message key="rotulo.decrescente"/></label>
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
          </div>
        </fieldset>
      </form>
      <div class="btn-action">
        <a class="btn btn-primary" id="btnPesquisar" href="#no-back" onClick="if(validaSubmit()){pesquisar(); return false;}"><svg width="20"><use xlink:href="../img/sprite.svg#i-consultar"></use></svg><hl:message key="rotulo.botao.pesquisar"/></a>
      </div>
    </div>
  </div>
</div> 
<script type="text/JavaScript" src="../js/listutils.js?<hl:message key="release.tag"/>"></script>
<script type="text/JavaScript" src="../js/listagem.js?<hl:message key="release.tag"/>"></script>

<script type="text/JavaScript">

function formLoad() {
  focusFirstField();
  hideEmptyFieldSet();
  configurarOrdenacao();
  
  <%
  String[] adeNumeros = request.getParameterValues("ADE_NUMERO_LIST");
  if (adeNumeros != null && adeNumeros.length > 0) {
  %>
  var adeNumeros = [<%=TextHelper.forJavaScriptBlock(TextHelper.join(adeNumeros, ","))%>];
  loadSelectOptions(f0.ADE_NUMERO_LIST, adeNumeros, '', true);
  document.getElementById('MULT_ADE_NUMERO').style.display = '';
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
    
    return true;    
  }
}

function pesquisar() {
  atribui_ordenacao();
  
  if (validaFormPesqAvancada()) { 
    with (document.formPesqAvancada) {
      selecionarTodosItens('ADE_NUMERO_LIST');
      submit();
    }
  }
}

function validaSubmit() {
  return true;
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
      insereItem('ADE_NUMERO', 'ADE_NUMERO_LIST');
    }
}

function removeNumero() {
    removeDaLista('ADE_NUMERO_LIST');
    if (document.getElementById('ADE_NUMERO_LIST').length == 0) {
        document.getElementById('adeLista').style.display = 'none';
        document.getElementById('removeAdeLista').style.display = 'none';
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

f0 = document.forms["formPesqAvancada"];
window.onload = formLoad;




</script>
<% if (!_responsavel.isSer()) { %>
  <hl:campoMatriculav4 scriptOnly="true"/>
<% } %> 
