<%@ page contentType="text/html; charset=iso-8859-1" language="java" %>
<%@ page import="java.util.*" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%
String[] tests = (String[]) request.getAttribute("tests");
String[] testsNames = (String[]) request.getAttribute("testsNames");
Map<String, String> result = (Map<String, String>) request.getAttribute("result");
%>
<html>
  <head>
    <%@ include file="../../../geral/head.jsp"%>
    <title><hl:message key="rotulo.nome.sistema"/> - <hl:message key="rotulo.status.status"/></title>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
    <script language="JavaScript" type="text/JavaScript" src="../js/scripts_2810.js"></script>
    <link rel="stylesheet" href="../css/style.css" type="text/css">
    <style>
    td {
      FONT-SIZE: 14pt
    }
    </style>
  </head>
  <body>
    <table width="900" border="0" align="center" cellpadding="0" cellspacing="0">
      <tr>
        <td valign="top"><img src="../img/view.jsp?nome=login/logo_cse.gif" border="0"></td>
        <td><img src="../img/blank.gif" width="20" border="0"></td>
        <td>      
          <table width="800" border="0" align="center" cellpadding="0" cellspacing="0">
            <tr valign="top">
              <td COLSPAN="2">
                <table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" CLASS="TabelaResultado">
                  <tr>
                    <td valign="top">
                      <table width="100%" border="0" cellpadding="2" CELLSPACING="1">
                        <tr class="tabelatopo">
                          <td class="tabelatopo" width="80"><hl:message key="rotulo.status.teste"/></td>
                          <td class="tabelatopo" width="60"><hl:message key="rotulo.status.resultado"/></td>
                          <td class="tabelatopo"><hl:message key="rotulo.status.observacao"/></td>
                        </tr>
                        <%
                            for (int i = 0; i < tests.length; i++) {
                                    String resultCode = (String) result.get(tests[i]);
                                    String resultMessage = (String) result.get(tests[i] + "-OBS");
                        %>
                          <tr height="45" class="<%=(String)((i % 2 == 0) ? "Lp" : "Li")%>" onMouseOver="selRow(this);" onMouseOut="unselRow(this);">
                            <td width="25%"><%=TextHelper.forHtmlContent(testsNames[i])%></td>
                            <td align="center">
                              <%
                                  if (resultCode != null && resultCode.equals("OK")) {
                              %>
                                <img src="../img/icones/check.gif"/>
                              <%
                                  } else if (resultCode != null && resultCode.equals("ALERTA")) {
                              %>
                                <img src="../img/icones/warning.gif"/>
                              <%
                                  } else {
                              %>
                                <img src="../img/icones/error.gif"/>
                              <%        
                                  }
                              %>
                            </td>
                            <td><%=(String)(resultMessage != null ? resultMessage : "N/A")%></td>
                          </tr>
                        <%
                            }
                        %>
                      </table>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
  </body>
</html>
