<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<% 
   String obrOrdenacaoPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
   String ordenacao = (String) JspHelper.verificaVarQryStr(request, "ORDENACAO");
   
   String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
   boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
%>
                  <div class="form-group col-sm-12 col-md-5">
                    <label id="lblOrdenacaoPage" for="ORDENACAO">${descricoes[recurso]}</label>
                    <SELECT ID= "ORDENACAO" NAME="ORDENACAO" SIZE="7" <% if (!TextHelper.isNull(ordenacao) || desabilitado) { %>disabled <%} %> class="form-control form-select w-100">
                      <OPTION VALUE="ORD01" <% if (!TextHelper.isNull(ordenacao) && ordenacao.equals("MATRICULA")) { %>SELECTED  <%} %>><hl:message key="rotulo.servidor.matricula"/></OPTION>
                      <OPTION VALUE="ORD02" <% if (!TextHelper.isNull(ordenacao) && ordenacao.equals(Columns.SER_NOME)) { %>SELECTED  <%} %>><hl:message key="rotulo.servidor.nome"/></OPTION>
                      <OPTION VALUE="ORD03" <% if (!TextHelper.isNull(ordenacao) && ordenacao.equals("TIPO")) { %>SELECTED  <%} %>><hl:message key="rotulo.servidor.tipo"/></OPTION>
                      <OPTION VALUE="ORD04" <% if (!TextHelper.isNull(ordenacao) && ordenacao.equals("CARGO")) { %>SELECTED  <%} %>><hl:message key="rotulo.servidor.cargo"/></OPTION>
                      <OPTION VALUE="ORD05" <% if (!TextHelper.isNull(ordenacao) && ordenacao.equals(Columns.CSA_IDENTIFICADOR)) { %>SELECTED  <%} %>><hl:message key="rotulo.consignataria.codigo.consignataria"/></OPTION>
                      <OPTION VALUE="ORD06" <% if (!TextHelper.isNull(ordenacao) && ordenacao.equals(Columns.SVC_DESCRICAO)) { %>SELECTED  <%} %>><hl:message key="rotulo.servico.tipo.desconto"/></OPTION>
                      <OPTION VALUE="ORD07" <% if (!TextHelper.isNull(ordenacao) && ordenacao.equals("VALOR_CONTRATO")) { %>SELECTED  <%} %>><hl:message key="rotulo.consignacao.valor.contrato"/></OPTION>
                      <OPTION VALUE="ORD08" <% if (!TextHelper.isNull(ordenacao) && ordenacao.equals(Columns.ADE_PRAZO)) { %>SELECTED  <%} %>><hl:message key="rotulo.consignacao.qtd.parcelas"/></OPTION>
                      <OPTION VALUE="ORD09" <% if (!TextHelper.isNull(ordenacao) && ordenacao.equals(Columns.ADE_VLR)) { %>SELECTED  <%} %>><hl:message key="rotulo.consignacao.valor.parcela"/></OPTION>
                      <OPTION VALUE="ORD10" <% if (!TextHelper.isNull(ordenacao) && ordenacao.equals(Columns.ADE_DATA)) { %>SELECTED  <%} %>><hl:message key="rotulo.consignacao.data.contrato"/></OPTION>
                      <OPTION VALUE="ORD11" <% if (!TextHelper.isNull(ordenacao) && ordenacao.equals("DATA_FIM")) { %>SELECTED  <%} %>><hl:message key="rotulo.consignacao.data.termino.contrato"/></OPTION>
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

                  <hl:htmlinput type="hidden" name="ORDENACAO_AUX" di="ORDENACAO_AUX" value="" />
                  <hl:htmlinput type="hidden" name="DESC_ORDENACAO" di="DESC_ORDENACAO" value="" />                      
                         
    <% if (obrOrdenacaoPage.equals("true")) { %>
        <script type="text/JavaScript">
        function funOrdenacaoPage() {
            camposObrigatorios = camposObrigatorios + 'ORDENACAO,';
            msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.ordenacao"/>,';
        }
        addLoadEvent(funOrdenacaoPage);     
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
         function valida_campo_ordenacao() {
             return true;
         }
        </script>        
            
