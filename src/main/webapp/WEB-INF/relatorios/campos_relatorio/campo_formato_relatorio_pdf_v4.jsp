<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ taglib uri="/html-lib" prefix="hl"%>

<% if (TextHelper.isNull(request.getParameter("formato")) || request.getParameter("formato").equals("PDF")) { %>
  <hl:htmlinput type="hidden" name="formato" di="formato" value="PDF" />
<% } %>

<script type="text/JavaScript">
  function valida_campo_formato_relatorio_pdf() {
     return true;
  }
</script>