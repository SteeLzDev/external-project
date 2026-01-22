<%@page import="com.zetra.econsig.values.CanalEnum"%>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%
   String obrCanal = JspHelper.verificaVarQryStr(request, "OBRIGATORIO");
   String canal = (String) JspHelper.verificaVarQryStr(request, "canal");

   String paramDisabled = JspHelper.verificaVarQryStr(request, "disabled");
   boolean desabilitado = (!TextHelper.isNull(paramDisabled) && paramDisabled.equals("true")) ? true:false;
%>
      <div class="form-group col-sm-12 col-md-6">
        <label id="lblOrigemAcesso" for="canal">${descricoes[recurso]}</label>
        <select name="canal" id="canal" class="Select form-control" nf="btnEnvia" onFocus="SetarEventoMascaraV4(this,'#*200',true);" onBlur="fout(this);ValidaMascaraV4(this);" <% if (!TextHelper.isNull(canal) || desabilitado) { %>disabled <%} %>>
          <option value="" <% if (TextHelper.isNull(canal)) { %>SELECTED <%} %>><hl:message key="rotulo.campo.todos"/></option>
          <% for (CanalEnum c : CanalEnum.values()) { %>
                <option value="<%=c.getCodigo()%>" <% if (!TextHelper.isNull(canal) && canal.equals(c.getCodigo())) { %>SELECTED  <%} %>><%=c.name()%></option>
          <% } %>
        </select>
      </div>

      <script type="text/JavaScript">
        <%if (obrCanal.equals("true")) {%>
            function funCanal() {
              camposObrigatorios = camposObrigatorios + 'canal,';
              msgCamposObrigatorios = msgCamposObrigatorios + '<hl:message key="mensagem.informe.canal.acesso"/>,';
            }
            addLoadEvent(funCanal);
        <%}%>
        function valida_campo_canal() {
          return true;
        }
      </script>
