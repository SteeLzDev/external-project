<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<% 
   AcessoSistema responsavelOrdenacaoAdePage = JspHelper.getAcessoSistema(request);
   String obrOrdAdePage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
   String ordenacao = (String) JspHelper.verificaVarQryStr(request, "ORDENACAO");
   String ordemQtd = (String) JspHelper.verificaVarQryStr(request, "ORDEM_QTD");
   String ordemTotal = (String) JspHelper.verificaVarQryStr(request, "ORDEM_TOTAL");
   String ordemPrestacao = (String) JspHelper.verificaVarQryStr(request, "ORDEM_PRESTACAO");
   String ordemCapitalDev = (String) JspHelper.verificaVarQryStr(request, "ORDEM_CAPITALDEVIDO");
   
   String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
   boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
%>

                  <div class="form-group col-sm-12 col-md-5">
                    <label id="lblOrdAdePage" for="ORDENACAO">${descricoes[recurso]}</label>
                    <SELECT NAME="ORDENACAO" ID="ORDENACAO" SIZE="4" <% if (!TextHelper.isNull(ordenacao) || desabilitado) {%> disabled <%} %> class="form-control form-select w-100">
                      <OPTION VALUE="CONTRATOS" <% if (!TextHelper.isNull(ordenacao) && ordenacao.equals("CONTRATOS")) { %> SELECTED  <%} %>><hl:message key="rotulo.relatorio.qtd.contratos"/></OPTION>
                      <OPTION VALUE="VALOR" <% if (!TextHelper.isNull(ordenacao) && ordenacao.equals("VALOR")) { %> SELECTED  <%} %>><hl:message key="rotulo.relatorio.total.prd.mensal"/></OPTION>
                      <OPTION VALUE="PRESTACAO" <% if (!TextHelper.isNull(ordenacao) && ordenacao.equals("PRESTACAO")) { %> SELECTED  <%} %>><hl:message key="rotulo.relatorio.total.prd.geral"/></OPTION>
                      <OPTION VALUE="CAPITAL_DEVIDO" <% if (!TextHelper.isNull(ordenacao) && ordenacao.equals("CAPITAL_DEVIDO")) { %> SELECTED  <%} %>><hl:message key="rotulo.consignacao.capital.devido"/></OPTION>
                    </SELECT>                    
                  </div>

                  <div class="form-group col-sm-12 col-md-1 p-0 mt-3" id="setasOrdenacao">
                    <a class="btn btn-primary btn-ordenacao pr-0 mt-2" href="#no-back" onClick="move(document.forms[0].ORDENACAO, document.forms[0].ORDENACAO.selectedIndex, -1); setaOrdenacao(); return false;">
                      <svg width="15">
                       <use xlink:href="#i-avancar"></use>
                      </svg>
                    </a>
                    <a class="btn btn-primary btn-ordenacao pr-0 mt-2" href="#no-back" onClick="move(document.forms[0].ORDENACAO, document.forms[0].ORDENACAO.selectedIndex, +1); setaOrdenacao(); return false;">
                      <svg width="15">
                       <use xlink:href="#i-voltar"></use>
                      </svg>
                    </a>
                  </div>

                  <div class="col-sm-12 col-md-6 mt-3" id="listaOrdenacao")>
                    <div class="form-check">
                      <div class="row">
                        <div class="col-sm-12 col-md-12 mt-2">
                          <div class="row" role="radiogroup" aria-labelledby="ORDEM_QTD">
                            <div class="col-sm-12 col-md-4">
                              <div class="form-group my-0">
                                <span><hl:message key="rotulo.relatorio.qtd.contratos"/></span>
                              </div>
                            </div>
                            <div class="col-sm-12 col-md-3 ml-3">
                              <INPUT class="form-check-input ml-1" TYPE="radio" NAME="ORDEM_QTD" ID="ORDEM_QTD1" TITLE="<hl:message key="rotulo.crescente"/>" VALUE="ASC" <%if (TextHelper.isNull(ordemQtd) || (!TextHelper.isNull(ordemQtd) && ordemQtd.equals("ASC"))) { %> checked <%} %> <%if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                              <label class="form-check-label formatacao ml-1 pr-4" for="ORDEM_QTD1" aria-label="<%=ApplicationResourcesHelper.getMessage("mensagem.relatorio.ordem.crescente", responsavelOrdenacaoAdePage, ApplicationResourcesHelper.getMessage("rotulo.relatorio.qtd.contratos", responsavelOrdenacaoAdePage).toLowerCase())%>"><hl:message key="rotulo.crescente"/></label>
                            </div>
                            <div class="col-sm-12 col-md-3 ml-3">
                              <INPUT class="form-check-input ml-1" TYPE="radio" NAME="ORDEM_QTD" ID="ORDEM_QTD2" TITLE="<hl:message key="rotulo.decrescente"/>" VALUE="DESC" <%if ((!TextHelper.isNull(ordemQtd) && ordemQtd.equals("DESC"))) { %> checked <%} %> <%if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                              <label class="form-check-label formatacao ml-1" for="ORDEM_QTD2" aria-label="<%=ApplicationResourcesHelper.getMessage("mensagem.relatorio.ordem.decrescente", responsavelOrdenacaoAdePage, ApplicationResourcesHelper.getMessage("rotulo.relatorio.qtd.contratos", responsavelOrdenacaoAdePage).toLowerCase())%>"><hl:message key="rotulo.decrescente"/></label>
                            </div>
                          </div>
                          <div class="row" role="radiogroup" aria-labelledby="ORDEM_TOTAL">
                            <div class="col-sm-12 col-md-4">
                              <div class="form-group my-0">
                                  <span><hl:message key="rotulo.relatorio.total.prd.mensal"/></span>
                              </div>
                            </div>
                            <div class="col-sm-12 col-md-3 ml-3">
                              <INPUT class="form-check-input ml-1" TYPE="radio" NAME="ORDEM_TOTAL" ID="ORDEM_TOTAL1" TITLE="<hl:message key="rotulo.crescente"/>" VALUE="ASC" <%if (TextHelper.isNull(ordemTotal) || (!TextHelper.isNull(ordemTotal) && ordemTotal.equals("ASC"))) { %> checked <%} %> <%if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                              <label class="form-check-label formatacao ml-1 pr-4" for="ORDEM_TOTAL1" aria-label="<%=ApplicationResourcesHelper.getMessage("mensagem.relatorio.ordem.crescente", responsavelOrdenacaoAdePage, ApplicationResourcesHelper.getMessage("rotulo.relatorio.total.prd.mensal", responsavelOrdenacaoAdePage).toLowerCase())%>"><hl:message key="rotulo.crescente"/></label>
                            </div>
                            <div class="col-sm-12 col-md-3 ml-3">
                              <INPUT class="form-check-input ml-1" TYPE="radio" NAME="ORDEM_TOTAL" ID="ORDEM_TOTAL2" TITLE="<hl:message key="rotulo.decrescente"/>" VALUE="DESC" <%if ((!TextHelper.isNull(ordemTotal) && ordemTotal.equals("DESC"))) { %> checked <%} %> <%if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                              <label class="form-check-label formatacao ml-1" for="ORDEM_TOTAL2" aria-label="<%=ApplicationResourcesHelper.getMessage("mensagem.relatorio.ordem.decrescente", responsavelOrdenacaoAdePage, ApplicationResourcesHelper.getMessage("rotulo.relatorio.total.prd.mensal", responsavelOrdenacaoAdePage).toLowerCase())%>"><hl:message key="rotulo.decrescente"/></label>
                            </div>
                          </div>
                          <div class="row" role="radiogroup" aria-labelledby="ORDEM_PRESTACAO">
                            <div class="col-sm-12 col-md-4">
                              <div class="form-group my-0">
                                 <span><hl:message key="rotulo.relatorio.total.prd.geral"/></span>
                              </div>
                            </div>
                            <div class="col-sm-12 col-md-3 ml-3">
                              <INPUT class="form-check-input ml-1" TYPE="radio" NAME="ORDEM_PRESTACAO" ID="ORDEM_PRESTACAO1" TITLE="<hl:message key="rotulo.crescente"/>" VALUE="ASC" <%if (TextHelper.isNull(ordemPrestacao) ||  (!TextHelper.isNull(ordemPrestacao) && ordemPrestacao.equals("ASC"))) { %> checked <%} %> <%if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                              <label class="form-check-label formatacao ml-1 pr-4" for="ORDEM_PRESTACAO1" aria-label="<%=ApplicationResourcesHelper.getMessage("mensagem.relatorio.ordem.crescente", responsavelOrdenacaoAdePage, ApplicationResourcesHelper.getMessage("rotulo.relatorio.total.prd.geral", responsavelOrdenacaoAdePage).toLowerCase())%>"><hl:message key="rotulo.crescente"/></label>
                            </div>
                            <div class="col-sm-12 col-md-3 ml-3">
                              <INPUT class="form-check-input ml-1" TYPE="radio" NAME="ORDEM_PRESTACAO" ID="ORDEM_PRESTACAO2" TITLE="<hl:message key="rotulo.decrescente"/>" VALUE="DESC" <%if ((!TextHelper.isNull(ordemPrestacao) && ordemPrestacao.equals("DESC"))) { %> checked <%} %> <%if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                              <label class="form-check-label formatacao ml-1" for="ORDEM_PRESTACAO2" aria-label="<%=ApplicationResourcesHelper.getMessage("mensagem.relatorio.ordem.decrescente", responsavelOrdenacaoAdePage, ApplicationResourcesHelper.getMessage("rotulo.relatorio.total.prd.geral", responsavelOrdenacaoAdePage).toLowerCase())%>"><hl:message key="rotulo.decrescente"/></label>
                            </div>
                          </div>
                          <div class="row" role="radiogroup" aria-labelledby="ORDEM_CAPITALDEVIDO">
                            <div class="col-sm-12 col-md-4">
                              <div class="form-group my-0">
                                 <span><hl:message key="rotulo.consignacao.capital.devido"/></span>
                              </div>
                            </div>
                            <div class="col-sm-12 col-md-3 ml-3">
                              <INPUT class="form-check-input ml-1" TYPE="radio" NAME="ORDEM_CAPITALDEVIDO" ID="ORDEM_CAPITALDEVIDO1" TITLE="<hl:message key="rotulo.crescente"/>" VALUE="ASC" <%if (TextHelper.isNull(ordemCapitalDev) || (!TextHelper.isNull(ordemCapitalDev) && ordemCapitalDev.equals("ASC"))) { %> checked <%} %> <%if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                              <label class="form-check-label formatacao ml-1 pr-4" for="ORDEM_CAPITALDEVIDO1" aria-label="<%=ApplicationResourcesHelper.getMessage("mensagem.relatorio.ordem.crescente", responsavelOrdenacaoAdePage, ApplicationResourcesHelper.getMessage("rotulo.consignacao.capital.devido", responsavelOrdenacaoAdePage).toLowerCase())%>"><hl:message key="rotulo.crescente"/></label>
                            </div>
                            <div class="col-sm-12 col-md-3 ml-3">
                              <INPUT class="form-check-input ml-1" TYPE="radio" NAME=ORDEM_CAPITALDEVIDO ID="ORDEM_CAPITALDEVIDO2" TITLE="<hl:message key="rotulo.decrescente"/>" VALUE="DESC" <%if ((!TextHelper.isNull(ordemCapitalDev) && ordemCapitalDev.equals("DESC"))) { %> checked <%} %> <%if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                              <label class="form-check-label formatacao ml-1" for="ORDEM_CAPITALDEVIDO2" aria-label="<%=ApplicationResourcesHelper.getMessage("mensagem.relatorio.ordem.decrescente", responsavelOrdenacaoAdePage, ApplicationResourcesHelper.getMessage("rotulo.consignacao.capital.devido", responsavelOrdenacaoAdePage).toLowerCase())%>"><hl:message key="rotulo.decrescente"/></label>
                            </div>
                          </div>
                        </div>
                      </div>
                      <hl:htmlinput type="hidden" name="ORDENACAO_AUX" di="ORDENACAO_AUX" value="" />                      
                      <hl:htmlinput type="hidden" name="DESC_ORDENACAO" di="DESC_ORDENACAO" value="" />
                  </div>                      
                </div>
          
    <% if (obrOrdAdePage.equals("true")) { %>
        <script type="text/JavaScript">
        function funOrdAdePage() {
            camposObrigatorios = camposObrigatorios + 'ORDENACAO,';
            camposObrigatorios = camposObrigatorios + 'ORDEM_QTD,';
            camposObrigatorios = camposObrigatorios + 'ORDEM_TOTAL,';
            camposObrigatorios = camposObrigatorios + 'ORDEM_PRESTACAO,';
            camposObrigatorios = camposObrigatorios + 'ORDEM_CAPITALDEVIDO,';
            msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.ordenacao"/>,';
            msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.ade.qtd"/>,';
            msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.prd.total.mensal"/>,';
            msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.prd.total.geral"/>,';
            msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.ade.capital.devido"/>,';
        }
        addLoadEvent(funOrdAdePage);     
        </script>
    <% } %>                                            


        <script type="text/JavaScript">
         function atribui_ordenacao() {
        	 var ordenacao = "";
        	 var descOrdenacao = "";
             with(document.forms[0]) {
                for (var i = 0; i < ORDENACAO.length; i++) {
                  ordenacao += ORDENACAO.options[i].value;
                  descOrdenacao += ORDENACAO.options[i].text;
                  if (i < ORDENACAO.length - 1) {
                      ordenacao += ", ";
                      descOrdenacao += ", ";
                  }
                }
                ORDENACAO_AUX.value = ordenacao;
                DESC_ORDENACAO.value = descOrdenacao;
             }
             return true;
         }         
        </script>        

        <script type="text/JavaScript">
         function valida_campo_ordenacao_ade() {
             return true;
         }
        </script>        
