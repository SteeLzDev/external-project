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
AcessoSistema responsavelTagPage = JspHelper.getAcessoSistema(request);
String obrTagPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
String tagCodigo = (String) JspHelper.verificaVarQryStr(request, "tagCodigo");
String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;

List<TransferObject> tipoAgendamento = (List<TransferObject>) request.getAttribute("listaTiposAgendamento");
String rotuloTodos = ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavelTagPage);
%>
                <div class="form-group col-sm-12 col-md-6">
                  <label id="lblTipoAgendamentoTagPage" for="tagCodigo">${descricoes[recurso]}</label>
                  <%=JspHelper.geraCombo(tipoAgendamento, "tagCodigo", Columns.TAG_CODIGO, Columns.TAG_DESCRICAO, rotuloTodos, null, (TextHelper.isNull(tagCodigo) && !desabilitado), 1, tagCodigo, null, desabilitado, "form-control")%>
                </div>
                <script type="text/JavaScript">
                <%if (obrTagPage.equals("true")) {%>
                function funTagPage() {
                  with(document.forms[0]) {
                    if (tagCodigo != null && !tagCodigo.disabled) {
                      camposObrigatorios = camposObrigatorios + 'tagCodigo,';
                      msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.tipo.agendamento"/>,';
                    }
                  }
                }
                addLoadEvent(funTagPage);     
                <%}%>
                function valida_campo_tipo_agendamento() {
                  with(document.forms[0]) {
                    if (tagCodigo != null && !tagCodigo.disabled) {
                      var controles = new Array('tagCodigo');
                      var msgs = new Array ('<hl:message key="mensagem.informe.tipo.agendamento"/>');
                      if (!ValidaCampos(controles, msgs)) {
                        return false;
                      }
                    }
                  }
                  return true;
                }
                </script>
