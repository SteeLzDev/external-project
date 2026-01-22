<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.upload.UploadHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ taglib uri="/html-lib" prefix="hl"%>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
boolean permiteInserirAnexo = (Boolean) request.getAttribute("permiteInserirAnexo");
List<TransferObject> listArquivoRse = (List<TransferObject>) request.getAttribute("arquivosRse");
String rseCodigo = (String) request.getAttribute("rseCodigo");
List<TransferObject> tipoArquivos = (List<TransferObject>) request.getAttribute("tipoArquivos");
boolean novoAnexo = (Boolean) request.getAttribute("novoAnexo");
%>

<c:set var="javascript">
<script type="text/JavaScript" src="../js/javacrypt.js?<hl:message key="release.tag"/>"></script>
</c:set>
<c:set var="title">
  <hl:message key="rotulo.servidor.anexo.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <%if (permiteInserirAnexo && !novoAnexo){ %>
     <div class="page-title">
      <div class="row">
       <div class="col-sm mb-2">
        <div class="float-end">
          <a class="btn btn-primary" type="button" href="#" onClick="postData('../v3/editarAnexosRegistroServidor?acao=editar&NOVO_ANEXO=true&RSE_CODIGO=<%=rseCodigo%>&<%=SynchronizerToken.generateToken4URL(request)%>'); return false;"><hl:message key="rotulo.servidor.anexo.incluir.novo"/></a>
        </div>
       </div>
      </div>
     </div>
   <%} %>
   <%if (permiteInserirAnexo && novoAnexo){ %>
     <form method="POST" action="../v3/editarAnexosRegistroServidor?acao=salvar&RSE_CODIGO=<%=rseCodigo%>&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1" id="form1">
        <div class="row">
          <div class="col-sm-12 col-md-12">
            <div class="card">
              <div class="card-header">
                <h2 class="card-header-title"><hl:message key="rotulo.servidor.anexo.incluir"/></h2>
              </div>
              <div class="card-body">
                <div class="row">
                  <div class="form-group col-sm-6">
                     <label for="tipoArquivo"><hl:message key="rotulo.servidor.anexo.tipo.arquivo"/></label>
                     <SELECT NAME="tipoArquivo" ID="tipoArquivo" CLASS="form-control">
                       <OPTION VALUE="" SELECTED><hl:message key="rotulo.campo.selecione"/></OPTION>
                       <%for (TransferObject tipoArquivo : tipoArquivos) {%>
                          <OPTION VALUE="<%=(String) tipoArquivo.getAttribute(Columns.TAR_CODIGO)%>"><%=(String) tipoArquivo.getAttribute(Columns.TAR_DESCRICAO)%></OPTION>
                       <%} %>                     
                     </SELECT>
                  </div>
                </div>
                <div class="row">
                  <div class="form-group col-sm-12">
                    <hl:fileUploadV4 multiplo="true" tituloCampoArquivo="<%=ApplicationResourcesHelper.getMessage("rotulo.servidor.anexo.documento", responsavel) %>" mostraCampoDescricao="false" extensoes="<%=UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_REGISTRO_SERVIDOR%>" tipoArquivo="anexo_registro_servidor"/>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </form>
      <div class="btn-action">
        <a class="btn btn-outline-danger" href="#" onClick="postData('../v3/editarAnexosRegistroServidor?acao=editar&RSE_CODIGO=<%=rseCodigo%>&<%=SynchronizerToken.generateToken4URL(request)%>')"><hl:message key="rotulo.botao.cancelar"/></a>
        <a class="btn btn-primary" href="#" onClick="javascript: verificaCampos(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
      </div>
  <%} %>
  <% if (!novoAnexo) { %>
    <div class="row">
      <div class="col-sm-12 col-md-12 mb-2">
        <div class="card">
          <div class="card-header hasIcon pl-3">
            <h2 class="card-header-title"><hl:message key="rotulo.servidor.anexo.arquivo.disponivel"/></h2>
          </div>
          <div class="card-body table-responsive">
            <table class="table table-striped table-hover table-responsive">
              <thead>
                <tr>
                  <th id="dataArquivo"><hl:message key="rotulo.servidor.anexo.data" /></th>
                  <th id="responsavelArquivo"><hl:message key="rotulo.responsavel.singular" /></th>
                  <th id="descricaoArquivo"><hl:message key="rotulo.servidor.anexo.nome" /></th>
                  <th id="arquivo"><hl:message key="rotulo.acoes" /></th>
                </tr>
              </thead>
                <tbody>
  <%} %>
  <% if (!novoAnexo && listArquivoRse != null && !listArquivoRse.isEmpty()) { %>
                <%
                  String arqCodigo, usuLogin, arsNome, arsDataCriacao;
                   for (TransferObject arquivoRse : listArquivoRse) {
                      arqCodigo = (String) arquivoRse.getAttribute(Columns.ARQ_CODIGO);
                      usuLogin = (String) arquivoRse.getAttribute(Columns.USU_LOGIN);
                      arsNome = (String) arquivoRse.getAttribute(Columns.ARS_NOME);
                      arsDataCriacao = DateHelper.toDateTimeString((Date) arquivoRse.getAttribute(Columns.ARS_DATA_CRIACAO));
                %>
                <tr>
                  <td><%=TextHelper.forHtmlContent(arsDataCriacao)%></td>
                  <td><%=TextHelper.forHtmlContent(usuLogin)%></td>
                  <td><%=TextHelper.forHtmlContent(arsNome)%></td>
                  <td class="text-nowrap" id="nomeArquivo">
                                 <div class="actions">
                    <div class="dropdown">
                      <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <div class="form-inline">
                          <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<%=ApplicationResourcesHelper.getMessage("rotulo.acoes", responsavel)%>" aria-label="<%=ApplicationResourcesHelper.getMessage("rotulo.fila.op.sensiveis.ver.detalhes", responsavel)%>"> <svg>
                              <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                          </span> <hl:message key="rotulo.botao.opcoes"/>
                        </div>
                      </a>
                      <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                         <a class="dropdown-item" href="#no-back" onClick="fazDownload('<%=TextHelper.forJavaScript(arqCodigo)%>'); return false;"><hl:message key="rotulo.acoes.download"/></a>
                          <%if (permiteInserirAnexo){ %>
                            <a class="dropdown-item" href="#no-back" class="dropdown-item" onClick="doIt('e', '<%=TextHelper.forJavaScript(arqCodigo)%>', '<%=TextHelper.forJavaScript(arsNome)%>'); return false;"><hl:message key="rotulo.acoes.excluir"/></a>
                          <%} %>
                      </div>
                    </div>
                   </div>  
                  </td>
                </tr>
                <% } %> 
              <% } %>
              </tbody>
              <tfoot>
                <tr><td colspan="4"><hl:message key="mensagem.registro.servidor.anexo.lista.arquivos" /></td></tr>
              </tfoot>
            </table>
          </div>
        </div>
      </div>
    </div>
  <%if(!novoAnexo) {%>
      <div class="btn-action">
        <a class="btn btn-outline-danger" href="#" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
      </div>
  <%} %>
</c:set>
<c:set var="javascript">
  <hl:fileUploadV4 multiplo="true" tituloCampoArquivo="<%=ApplicationResourcesHelper.getMessage("rotulo.servidor.anexo.documento", responsavel) %>" mostraCampoDescricao="false" extensoes="<%=UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_REGISTRO_SERVIDOR%>" tipoArquivo="anexo_registro_servidor" scriptOnly="true"/>
  <script type="text/JavaScript">
  
    function verificaCampos() {
        <%if (permiteInserirAnexo){ %>
            var tipoArquivo = document.getElementById('tipoArquivo').value;
            var arquivo = document.getElementById('FILE1').value;
            
            if (tipoArquivo != '' && arquivo == '') {
                  alert('<hl:message key="mensagem.informe.registro.servidor.anexo.documento"/>');
                  return false;
            } else if (tipoArquivo == '' && arquivo != ''){
                alert('<hl:message key="mensagem.anexo.registro.servidor.tipo.arquivo.ausente"/>');
                return false;
            } else if (tipoArquivo == '' && arquivo == ''){
                alert('<hl:message key="mensagem.informe.registro.servidor.anexo.documento"/>');
                return false;
            } else {
          	  document.getElementById("form1").submit();
            }
      <%}%>  
    }
      
    function fazDownload(codigo) {
      <%if (permiteInserirAnexo){ %>
        postData('../v3/editarAnexosRegistroServidor?acao=download&arqCodigo=' + codigo + '&rseCodigo=<%=rseCodigo%>&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>', 'download');
      <%} else {%>
        postData('../v3/editarAnexosRegistroServidor?acao=somenteDownload&arqCodigo=' + codigo + '&rseCodigo=<%=rseCodigo%>&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>', 'download');
      <% }%>  
    }

    function doIt(opt, codigo, arq) {
        var msg = '', j;
        if (opt == 'e') {
        msg = '<hl:message key="mensagem.confirmacao.exclusao.arquivo"/>'.replace("{0}", arq);
          j = '../v3/editarAnexosRegistroServidor?acao=excluir&arqCodigo=' + codigo + '&rseCodigo=<%=rseCodigo%>&_skip_history_=true';
        } else {
          return false;
        }
    
        j = j + '&<%=SynchronizerToken.generateToken4URL(request)%>'
        if (msg != '') {
          if (confirm(msg)) {
            if (opt == 'i' || opt == 'v') {
            postData(j);
            } else {
              postData(j);
            }
          } else {
            return false;
          }
        } else {
          postData(j);
        }
        return true;
    }
  </script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>