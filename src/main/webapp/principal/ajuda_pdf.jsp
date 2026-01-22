<%@ page contentType="text/html; charset=iso-8859-1" language="java" %>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.parametro.ParamSist" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.MimeDetector" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="eu.medsea.mimeutil.MimeType" %> 
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String name = (responsavel.isCseSupOrg() ? "ajuda_cse.pdf" : (responsavel.isCsaCor() ? "ajuda_csa.pdf" : "ajuda_servidor.pdf"));
String absolutePath = ParamSist.getDiretorioRaizArquivos();
String fileName = absolutePath + File.separatorChar + "ajuda" + File.separatorChar + name;

// Verifica se o arquivo existe
File arquivo = new File(fileName);
if (!arquivo.exists()) {
  session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.include.get.file.nao.encontrado", responsavel, name));
  request.getRequestDispatcher("/v3/exibirMensagem?acao=exibirMsgSessao").forward(request, response);
  return;
}
out.clear();
Set<MimeType> mimeSet = MimeDetector.MIMEUTIL.detect(arquivo.getAbsolutePath());
String mime = mimeSet != null && mimeSet.size() > 0 ? mimeSet.toArray()[0].toString() : "APPLICATION/OCTET-STREAM";
response.setContentType(mime);
response.setContentLength((int) arquivo.length());
response.setHeader("Content-Disposition","attachment; filename=\""+ arquivo.getName() + "\"");

BufferedInputStream entrada = new BufferedInputStream(new FileInputStream(arquivo));
int i = 0;
while ((i = entrada.read()) != -1) {
  out.write(i);
}
entrada.close();
%>