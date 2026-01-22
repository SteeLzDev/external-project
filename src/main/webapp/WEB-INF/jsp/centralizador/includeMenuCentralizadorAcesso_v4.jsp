<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="com.zetra.econsig.values.Columns"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper"%>
<%@ page import="com.zetra.econsig.values.FieldKeysConstants"%>
<%@ page import="com.zetra.econsig.values.CodedValues"%>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper"%>
<%@ page import="com.zetra.econsig.helper.web.v3.JspHelper"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String urlConsig =  (String) (session.getAttribute("attrSessionAcessoUrl") != null ? session.getAttribute("attrSessionAcessoUrl") : "");
String urlCentralAcesso = (String) (session.getAttribute("urlCentralizadorAcesso") != null ? session.getAttribute("urlCentralizadorAcesso") : "");
String parametrosCentral = (String) (session.getAttribute("parametrosCentral") != null ? session.getAttribute("parametrosCentral") : "");
Boolean pingCentralizador = (Boolean) request.getAttribute("executePingCentralizador"); 
%>


      <a class="dropdown-item" href="<%=urlCentralAcesso %>/select-company"><%=ApplicationResourcesHelper.getMessage("mensagem.centralizador.escolher.outro.sistema", responsavel)%></a>
      <a class="dropdown-item" href="<%=urlCentralAcesso %>/update-password"><%=ApplicationResourcesHelper.getMessage("rotulo.menu.alterar.senha", responsavel)%></a>
      <a class="dropdown-item" href="#sairModalCentralAcesso" data-bs-toggle="modal"><%=ApplicationResourcesHelper.getMessage("rotulo.centralizador.sair", responsavel)%></a>
