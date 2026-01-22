<%@ page contentType="text/html; charset=iso-8859-1" language="java" %>
<%@ taglib uri="/html-lib" prefix="hl"%>

<%
  String tipo = request.getParameter("tipo");
%>
<html>
  <head>
    <title><hl:message key="rotulo.ajuda.captcha.titulo"/></title>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
    <script language="JavaScript" type="text/JavaScript" src="../js/scripts_2810.js"></script>
  </head>
  <body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
    <table width="100%" border="0" align="center" cellpadding="5" cellspacing="0">
      <tr>
        <td style="font-size: 10pt;">
          <% if(tipo.equals("1")){%>
                <hl:message key="mensagem.ajuda.captcha.usuario"/>
          <% } else if(tipo.equals("2")){ %>
                <hl:message key="mensagem.ajuda.captcha.servidor"/>
          <% } else if(tipo.equals("3")){ %>
                <hl:message key="mensagem.ajuda.captcha.falado"/>
          <% } else if(tipo.equals("4")){ %>
                <hl:message key="mensagem.ajuda.captcha.operacao.imagem"/>
          <% } else if(tipo.equals("5")){ %>
                <hl:message key="mensagem.ajuda.captcha.operacao.falado"/>
          <% } %>
        </td>
      </tr>
    </table>
  </body>
</html>