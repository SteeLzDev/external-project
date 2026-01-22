<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%
   AcessoSistema responsavelDecisaoPage = JspHelper.getAcessoSistema(request);
   String obrigatorioDecisaoPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
   List<TransferObject> lstTipoJustica = (List<TransferObject>) request.getAttribute("lstTipoJustica");

   String paramDecisaoJudicialPage = JspHelper.verificaVarQryStr(request, "PARAMETRO");
   String tjuCodigo = JspHelper.verificaVarQryStr(request, "tjuCodigo");
   String djuEstado = JspHelper.verificaVarQryStr(request, "djuEstado");
   String djuComarca = JspHelper.verificaVarQryStr(request, "djuComarca");
   String djuNumProcesso = JspHelper.verificaVarQryStr(request, "djuNumProcesso");

   String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
   boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
%>
        <fieldset class="col-sm-12 col-md-12">
          <div class="legend"><span><hl:message key="rotulo.avancada.decisao.judicial.titulo"/></span></div>
          <div class="row">
            <div class="form-check form-group col-md-6 mt-2">
              <label for="tjuCodigo"><hl:message key="rotulo.avancada.decisao.judicial.tipo.justica"/></label>
              <%=JspHelper.geraCombo(lstTipoJustica, "tjuCodigo", Columns.TJU_CODIGO, Columns.TJU_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.campo.selecione", responsavelDecisaoPage), "class=\"form-control\"", desabilitado, 1, tjuCodigo)%>
            </div>
            <div class="form-check form-group col-md-6 mt-2">
              <label for="djuEstado"><hl:message key="rotulo.avancada.decisao.judicial.estado"/></label>
              <%= JspHelper.geraComboUF("djuEstado", "djuEstado", djuEstado, desabilitado, "form-control", responsavelDecisaoPage) %>
            </div>
          </div>
          <div class="row">
            <div class="form-check form-group col-md-6 mt-2">
              <label for="djuComarca"><hl:message key="rotulo.avancada.decisao.judicial.comarca"/></label>
              <select name="djuComarca" id="djuComarca" class="form-control"></select>
              <hl:htmlinput name="cidCodigo" di="cidCodigo" type="hidden" />
            </div>
            <% if (!paramDecisaoJudicialPage.equalsIgnoreCase("SINTETICO")) { %>
              <div class="form-check form-group col-md-6 mt-2">
                <label for="djuNumProcesso"><hl:message key="rotulo.avancada.decisao.judicial.numero.processo"/></label>
                <hl:htmlinput name="djuNumProcesso" di="djuNumProcesso" type="text" classe="form-control" size="40" value="<%=(djuNumProcesso != null ? djuNumProcesso : "")%>" />
              </div>
            <% } %>
          </div>
        </fieldset>
      <script type="text/JavaScript">
        <%if (obrigatorioDecisaoPage.equals("true")) {%>
            function funDecisaoJudicial() {
              camposObrigatorios = camposObrigatorios + 'tjuCodigo,djuEstado,djuComarca,djuNumProcesso';
              msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.tju.codigo"/>,<hl:message key="mensagem.informe.tju.estado"/>,<hl:message key="mensagem.informe.tju.comarca"/>,<hl:message key="mensagem.informe.num.processo"/>';
            }
            addLoadEvent(funDecisaoJudicial);
        <%}%>
        function valida_campo_decisao_judicial() {
          return true;
        }
      </script>
