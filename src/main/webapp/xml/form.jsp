<%@ page contentType="text/html; charset=iso-8859-1" language="java" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<HTML>
<HEAD>
<TITLE><hl:message key="rotulo.xml.titulo"/></TITLE>
<META HTTP-EQUIV="Content-Type" CONTENT="text/html; charset=iso-8859-1">
</HEAD>
<%
String versaoVelha = JspHelper.verificaVarQryStr(request, "old");
%>

<BODY>
<P ALIGN="CENTER">
<FORM NAME="form1" METHOD="post" ACTION="requisicao.jsp">
<%
//scriptlet tempor�rio para teste da tarefa artf1821
if (!TextHelper.isNull(versaoVelha) && versaoVelha.equalsIgnoreCase("true")) {
%>
   <INPUT TYPE="hidden" NAME="old" VALUE="true">
<%
}
%>
  <TEXTAREA NAME="REQUISICAO" COLS="100" ROWS="30"></TEXTAREA>
	<br><br>
  <INPUT TYPE="submit" NAME="submit" VALUE='<hl:message key="rotulo.xml.enviar.consulta"/>'>
</FORM>
</P>
</BODY>
</HTML>
