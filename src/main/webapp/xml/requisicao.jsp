<%@ page contentType="text/xml; charset=iso-8859-1" language="java" errorPage="" %>
<%@ page import="java.io.ByteArrayInputStream" %>
<%@ page import="java.io.ByteArrayOutputStream" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.webservice.legacyxml.RequisicaoXmlFrontController" %>
<%@ include file="../geral/env_navegacao.jsp" %>
<%
String arquivoXml = JspHelper.verificaVarQryStr(request, "REQUISICAO").trim();

if (!arquivoXml.equals("")) {
  // Processamento de Requisicao XML
  // LOG.info(arquivoXml);
  ByteArrayOutputStream saida = new ByteArrayOutputStream();
  ByteArrayInputStream entrada = new ByteArrayInputStream(arquivoXml.getBytes());

  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  RequisicaoXmlFrontController xmlFrontCntrl = new RequisicaoXmlFrontController(entrada, saida, responsavel);
  try {
     xmlFrontCntrl.processa();
  } catch (Exception ex) {
     LOG.error(ex.getMessage(), ex);
  }
  out.clear();
  String xmlOutStream = xmlFrontCntrl.getSaida().toString();
  if (!TextHelper.isNull(xmlOutStream)) {
      xmlOutStream = xmlOutStream.replaceAll("/>", "></Atributo>");
  }
  
  out.print(xmlOutStream);
}
%>