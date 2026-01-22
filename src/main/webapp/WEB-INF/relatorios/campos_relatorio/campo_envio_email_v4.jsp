<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="descricao">${descricoes[recurso]}</c:set>
<%
  AcessoSistema responsavelEmailDestinatarioPage = JspHelper.getAcessoSistema(request);
  String obrEmailDestinatarioPage = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
  String descEmailDestinatarioPage = pageContext.getAttribute("descricao").toString();   
  String emailDestinatario = (String) JspHelper.verificaVarQryStr(request, "email_destinatario");
  
  String paramDisabledEmailDestinatarioPage = JspHelper.verificaVarQryStr(request, "disabled");
  boolean desabilitadoEmailDestinatarioPage = (!TextHelper.isNull(paramDisabledEmailDestinatarioPage) && paramDisabledEmailDestinatarioPage.equals("true")) ? true : false;
%>
                <div class="form-group col-sm-12 col-md-6">
                  <label id="lblEmailDestinatario" for="email_destinatario"><%=TextHelper.forHtmlContent(descEmailDestinatarioPage)%></label>
                  <input type="text" name="email_destinatario" id="email_destinatario" class="form-control" <% if (!TextHelper.isNull(emailDestinatario) || desabilitadoEmailDestinatarioPage) { %>disabled <%} %> onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);" value="<%=TextHelper.forHtmlAttribute(emailDestinatario)%>" placeholder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0", responsavelEmailDestinatarioPage, descEmailDestinatarioPage)%>">
                </div>
                <script type="text/JavaScript">
                <%if (obrEmailDestinatarioPage.equals("true")) {%>
                function funEmailDestinatarioPage() {
                  camposObrigatorios = camposObrigatorios + 'email_destinatario,';
                  msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.email.destinatario.relatorio.agendado"/>,';
                }
                addLoadEvent(funEmailDestinatarioPage);     
                <%}%>
                function valida_campo_envio_email() {
                  var campoEnvioEmail = document.forms[0].email_destinatario;
                  if (document.forms[0].dataPrevista != null && campoEnvioEmail != null && !campoEnvioEmail.disabled) {
                    var email = campoEnvioEmail.value;
                    if (email != null && trim(email) != '' && !isEmailValid(email)) {
                      alert("<hl:message key="mensagem.informe.email.valido"/>");
                      campoEnvioEmail.focus();
                      return false;
                    }
                  }
                  return true;
                }
                </script>
