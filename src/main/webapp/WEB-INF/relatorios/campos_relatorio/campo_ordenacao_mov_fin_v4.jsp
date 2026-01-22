<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<% 
   AcessoSistema responsavelOrdenacaoMovFinPage = JspHelper.getAcessoSistema(request);
   String obrOrdMovFinPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
   
   String ordenacao = (String) JspHelper.verificaVarQryStr(request, "ORDENACAO");
   String ordemStatus = (String) JspHelper.verificaVarQryStr(request, "ORDEM_STATUS");
   String ordemPar = (String) JspHelper.verificaVarQryStr(request, "ORDEM_PARCELAS");
   String ordemPrestacao = (String) JspHelper.verificaVarQryStr(request, "ORDEM_PRESTACAO");
   
   String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
   boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
%>
                  <div class="form-group col-sm-12 col-md-5">
                    <label id="lblOrdMovFinPage" for="ORDENACAO">${descricoes[recurso]}</label>
                    <SELECT NAME="ORDENACAO" SIZE="4" <% if (!TextHelper.isNull(ordenacao) || desabilitado) { %>disabled <%} %> class="form-control form-select w-100">
                      <OPTION VALUE="STATUS" <% if (!TextHelper.isNull(ordenacao) && ordenacao.equals("STATUS")) { %>SELECTED  <%} %>><hl:message key="rotulo.relatorio.status"/></OPTION>
                      <OPTION VALUE="NUM_PARCELAS" <% if (!TextHelper.isNull(ordenacao) && ordenacao.equals("NUM_PARCELAS")) { %>SELECTED  <%} %>><hl:message key="rotulo.relatorio.nro.parcelas"/></OPTION>                      
                      <OPTION VALUE="TOTAL_PRESTACOES" <% if (!TextHelper.isNull(ordenacao) && ordenacao.equals("TOTAL_PRESTACOES")) { %>SELECTED  <%} %>><hl:message key="rotulo.relatorio.total.prd"/></OPTION> 
                    </SELECT>
                  </div>                    

                  <div class="form-group col-sm-12 col-md-1 p-0 mt-3">
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

                  <div class="col-sm-12 col-md-6 mt-3">
                    <div class="form-check">
                      <div class="row">
                        <div class="col-sm-12 col-md-12 mt-2">
                          <div class="row" role="radiogroup" aria-labelledby="ORDEM_QTD">
                            <div class="col-sm-12 col-md-5">
                              <div class="form-group my-0">
                                <span><hl:message key="rotulo.relatorio.status"/></span>
                              </div>
                            </div>
                            <div class="col-sm-12 col-md-3">
                              <INPUT class="form-check-input ml-1" TYPE="radio" NAME="ORDEM_STATUS" ID="ORDEM_STATUS1" TITLE="<hl:message key="rotulo.crescente"/>" VALUE="ASC" <%if (TextHelper.isNull(ordemStatus) || (!TextHelper.isNull(ordemStatus) && ordemStatus.equals("ASC"))) { %> checked <%} %> <%if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                              <label class="form-check-label formatacao ml-1 pr-4" for="ORDEM_STATUS1" aria-label="<%=ApplicationResourcesHelper.getMessage("mensagem.relatorio.ordem.crescente", responsavelOrdenacaoMovFinPage, ApplicationResourcesHelper.getMessage("rotulo.relatorio.status", responsavelOrdenacaoMovFinPage).toLowerCase())%>"><hl:message key="rotulo.crescente"/></label>
                            </div>
                            <div class="col-sm-12 col-md-3">
                              <INPUT class="form-check-input ml-1" TYPE="radio" NAME="ORDEM_STATUS" ID="ORDEM_STATUS2" TITLE="<hl:message key="rotulo.decrescente"/>" VALUE="DESC" <%if ((!TextHelper.isNull(ordemStatus) && ordemStatus.equals("DESC"))) { %> checked <%} %> <%if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                              <label class="form-check-label formatacao ml-1" for="ORDEM_STATUS2" aria-label="<%=ApplicationResourcesHelper.getMessage("mensagem.relatorio.ordem.crescente", responsavelOrdenacaoMovFinPage, ApplicationResourcesHelper.getMessage("rotulo.relatorio.status", responsavelOrdenacaoMovFinPage).toLowerCase())%>"><hl:message key="rotulo.decrescente"/></label>
                            </div>
                          </div>
                          <div class="row" role="radiogroup" aria-labelledby="ORDEM_QTD">
                            <div class="col-sm-12 col-md-5">
                              <div class="form-group my-0">
                                <span><hl:message key="rotulo.relatorio.nro.parcelas"/></span>
                              </div>
                            </div>
                            <div class="col-sm-12 col-md-3">
                              <INPUT class="form-check-input ml-1" TYPE="radio" NAME="ORDEM_PARCELAS" ID="ORDEM_PARCELAS1" TITLE="<hl:message key="rotulo.crescente"/>" VALUE="ASC"  <%if (TextHelper.isNull(ordemPar) || (!TextHelper.isNull(ordemPar) && ordemStatus.equals("ASC"))) { %> checked <%} %> <%if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                              <label class="form-check-label formatacao ml-1 pr-4" for="ORDEM_PARCELAS1" aria-label="<%=ApplicationResourcesHelper.getMessage("mensagem.relatorio.ordem.crescente", responsavelOrdenacaoMovFinPage, ApplicationResourcesHelper.getMessage("rotulo.relatorio.nro.parcelas", responsavelOrdenacaoMovFinPage).toLowerCase())%>"><hl:message key="rotulo.crescente"/></label>
                            </div>
                            <div class="col-sm-12 col-md-3">
                              <INPUT class="form-check-input ml-1" TYPE="radio" NAME="ORDEM_PARCELAS" ID="ORDEM_PARCELAS2" TITLE="<hl:message key="rotulo.decrescente"/>" VALUE="DESC" <%if ((!TextHelper.isNull(ordemPar) && ordemPar.equals("DESC"))) { %> checked <%} %> <%if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                              <label class="form-check-label formatacao ml-1" for="ORDEM_PARCELAS2" aria-label="<%=ApplicationResourcesHelper.getMessage("mensagem.relatorio.ordem.crescente", responsavelOrdenacaoMovFinPage, ApplicationResourcesHelper.getMessage("rotulo.relatorio.nro.parcelas", responsavelOrdenacaoMovFinPage).toLowerCase())%>"><hl:message key="rotulo.decrescente"/></label>
                            </div>
                          </div>
                          <div class="row" role="radiogroup" aria-labelledby="ORDEM_QTD">
                            <div class="col-sm-12 col-md-5">
                              <div class="form-group my-0">
                                <span><hl:message key="rotulo.relatorio.total.prd"/></span>
                              </div>
                            </div>
                            <div class="col-sm-12 col-md-3">
                              <INPUT class="form-check-input ml-1" TYPE="radio" NAME="ORDEM_PRESTACAO" ID="ORDEM_PRESTACAO1" TITLE="<hl:message key="rotulo.crescente"/>" VALUE="ASC" <%if (TextHelper.isNull(ordemPrestacao) || (!TextHelper.isNull(ordemPrestacao) && ordemPrestacao.equals("ASC"))) { %> checked <%} %> <%if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                              <label class="form-check-label formatacao ml-1 pr-4" for="ORDEM_PRESTACAO1" aria-label="<%=ApplicationResourcesHelper.getMessage("mensagem.relatorio.ordem.crescente", responsavelOrdenacaoMovFinPage, ApplicationResourcesHelper.getMessage("rotulo.relatorio.total.prd", responsavelOrdenacaoMovFinPage).toLowerCase())%>"><hl:message key="rotulo.crescente"/></label>
                            </div>
                            <div class="col-sm-12 col-md-3">
                              <INPUT class="form-check-input ml-1" TYPE="radio" NAME="ORDEM_PRESTACAO" ID="ORDEM_PRESTACAO2" TITLE="<hl:message key="rotulo.decrescente"/>" VALUE="DESC" <%if ((!TextHelper.isNull(ordemPrestacao) && ordemPrestacao.equals("DESC"))) { %> checked <%} %> <%if (desabilitado) {%> disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);">
                              <label class="form-check-label formatacao ml-1" for="ORDEM_PRESTACAO2" aria-label="<%=ApplicationResourcesHelper.getMessage("mensagem.relatorio.ordem.crescente", responsavelOrdenacaoMovFinPage, ApplicationResourcesHelper.getMessage("rotulo.relatorio.total.prd", responsavelOrdenacaoMovFinPage).toLowerCase())%>"><hl:message key="rotulo.decrescente"/></label>
                            </div>
                          </div>
                        </div>
                      </div>
                      <hl:htmlinput type="hidden" name="ORDENACAO_AUX" di="ORDENACAO_AUX" value="" />                                            
                      <hl:htmlinput type="hidden" name="DESC_ORDENACAO" di="DESC_ORDENACAO" value="" />                      
                    </div>                      
                  </div>

    <% if (obrOrdMovFinPage.equals("true")) { %>
        <script type="text/JavaScript">
        function funOrdMovFinPage() {
            camposObrigatorios = camposObrigatorios + 'ORDENACAO,';
            camposObrigatorios = camposObrigatorios + 'ORDEM_STATUS,';
            camposObrigatorios = camposObrigatorios + 'ORDEM_PARCELAS,';
            camposObrigatorios = camposObrigatorios + 'ORDEM_PRESTACAO,';
            msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.ordenacao"/>,';
            msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.status"/>,';
            msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.ade.prazo"/>,';
            msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.prd.total"/>,';
        }
        addLoadEvent(funOrdMovFinPage);     
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
         function valida_campo_ordenacao_mov_fin() {
             return true;
         }
        </script>        

