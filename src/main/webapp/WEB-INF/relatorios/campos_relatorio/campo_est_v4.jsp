<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib prefix="hl" uri="/html-lib"%>
<% 
  AcessoSistema responsavelEstPage = JspHelper.getAcessoSistema(request);
  String obrEstPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
  
  String estCodigo = (String) JspHelper.verificaVarQryStr(request, "estCodigo");
  String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
  boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
  
  if (!responsavelEstPage.isOrg()) {
      List<TransferObject> estabelecimentosTO = (List<TransferObject>) request.getAttribute("listaEstabelecimentos");
%>
              <div class="form-group col-sm-12 col-md-6">
                <label id="lblEstabelecimentoEstPage" for="estCodigo">${descricoes[recurso]}</label>
                <%=JspHelper.geraCombo(estabelecimentosTO, "estCodigo", Columns.EST_CODIGO + ";" + Columns.EST_IDENTIFICADOR + ";" + Columns.EST_NOME, Columns.EST_NOME + ";" + Columns.EST_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelEstPage), null, false, 1, estCodigo, null, desabilitado, "form-control")%>
              </div>
              <%if (obrEstPage.equals("true")) {%>
              <script type="text/JavaScript">
              function funEstPage() {
                camposObrigatorios = camposObrigatorios + 'estCodigo,';
                msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.estabelecimento"/>,';
              }
              addLoadEvent(funEstPage);
              </script>
              <% } %>      
<%} %>
              <script type="text/JavaScript">
              function valida_campo_est() {
                 return true;
              }
              </script>
