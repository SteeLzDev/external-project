<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@page import="com.zetra.econsig.helper.texto.DateHelper"%>
<%@page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper"%>
<%@page import="com.zetra.econsig.helper.texto.LocaleHelper"%>
<%@page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@page import="com.zetra.econsig.helper.sistema.ShowFieldHelper"%>
<%@page import="com.zetra.econsig.dto.entidade.ParamSvcTO"%>
<%@page import="com.zetra.econsig.dto.TransferObject"%>
<%@page import="java.math.BigDecimal" %>
<%@page import="java.util.*" %>
<%@page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@page import="com.zetra.econsig.helper.margem.MargemDisponivel" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fl" uri="/function-lib"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="hl" uri="/html-lib"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
%>
<c:set var="title">
  <hl:message key="rotulo.copia.seguranca.titulo"/>
</c:set>

<c:set var="imageHeader">
  <use xlink:href="#i-sistema"></use>
</c:set>

<c:set var="bodyContent">
  <script type="text/JavaScript">
  function addLoadEvent(func) {
    var oldonload = window.onload;
    if (typeof window.onload != 'function') {
      window.onload = func;
    } else {
      window.onload = function() {
        if (oldonload) {
            oldonload();
        }
        func();
      }
    }
  }
  </script>
  <c:if test="${!temProcessoRodando}">
    <form action="${fl:forHtmlAttribute(formAction)}" method="post" name="form1">
      <div class="row">
        <div class="col-sm">
          <div class="card">
            <div class="card-header">
              <h2 class="card-header-title"><hl:message key="rotulo.copia.seguranca.titulo.exportar"/></h2>
            </div>
            <div class="card-body">
              <div class="row">
                <c:set var="recurso" value="/WEB-INF/relatorios/campos_relatorio/campo_data_execucao_v4.jsp" scope="session"/>
                <jsp:include page="${fl:forHtmlAttribute(recurso)}" flush="true">
                  <jsp:param name="OBRIGATORIO" value="false" />
                  <jsp:param name="PARAMETRO" value="" />
                  <jsp:param name="STRTIPO" value="" />
                </jsp:include>
                <div class="form-group col-sm-12 col-md-6">
                  <br/>
                </div>
                <c:set var="recurso" value="/WEB-INF/relatorios/campos_relatorio/campo_tipo_agendamento_v4.jsp" scope="session"/>
                <jsp:include page="${fl:forHtmlAttribute(recurso)}" flush="true">
                  <jsp:param name="OBRIGATORIO" value="true" />
                  <jsp:param name="PARAMETRO" value="" />
                  <jsp:param name="STRTIPO" value="" />
                </jsp:include>
                <c:set var="recurso" value="/WEB-INF/relatorios/campos_relatorio/campo_periodicidade_v4.jsp" scope="session"/>
                <jsp:include page="${fl:forHtmlAttribute(recurso)}" flush="true">
                  <jsp:param name="OBRIGATORIO" value="true" />
                  <jsp:param name="PARAMETRO" value="" />
                  <jsp:param name="STRTIPO" value="" />
                </jsp:include> 
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="btn-action">
        <a class="btn btn-secondary" id="btnExportar" href="#no-back" onClick="doIt('b', '', '');"><hl:message key="rotulo.botao.exportar.copia.seguranca"/></a>
        <a class="btn btn-primary" id="btnAgendar" href="#no-back" onClick="if(vf_periodo_cs()){f0.submit();} return false;"><hl:message key="rotulo.botao.novo.agengamento"/></a>
      </div>
      <div class="row">
        <div class="col-sm">
          <div class="card">
            <div class="card-header">
              <h2 class="card-header-title"><hl:message key="rotulo.copia.seguranca.titulo"/></h2>
            </div>
            <div class="card-body table-responsive p-0">
              <table class="table table-striped table-hover">
                <thead>
                  <tr>
                    <th scope="col"><hl:message key="rotulo.copia.seguranca.data"/></th>
                    <th scope="col"><hl:message key="rotulo.copia.seguranca.tipo.agendamento"/></th>
                    <th scope="col" width="10%"><hl:message key="rotulo.acoes"/></th>
                  </tr>
                </thead>
                <tbody>
                <c:choose>
                  <c:when test="${empty arquivosDTO}">
                  <tr>
                    <td colspan="6"><hl:message key="rotulo.nenhum.agendamento.encontrado"/></td>
                  </tr>
                  </c:when>
                  <c:otherwise>
                    <c:forEach items="${agendamentos}" var="agendamento" varStatus="agdStatus">
                      <fmt:formatDate value="${agendamento.agdDataPrevista}" var="agdDataPrevista" type="date" pattern="<%=LocaleHelper.getDatePattern()%>" />                    
                    <tr>
                      <td>${agdDataPrevista}</td>
                      <td>${agendamento.agdDescricao}</td>
                      <td>
                        <div class="actions">
                          <div class="dropdown">
                            <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                              <div class="form-inline">
                                <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>" aria-label="<%=ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)%>"> <svg>
                                    <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                                </span> <hl:message key="rotulo.botao.opcoes"/>
                              </div>
                            </a>
                            <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                              <a class="dropdown-item" href="#no-back" aria-label="<hl:message key="rotulo.botao.aria.excluir.copia.seguranca" arg0="${agendamento.agdDataPrevista}"/>" onClick="doIt('d', '${agdDataPrevista}', '${agendamento.agdCodigo}'); return false;"><hl:message key="rotulo.botao.excluir"/></a>
                            </div>
                          </div>
                        </div>
                      </td>
                    </tr>
                  </c:forEach>
                  </c:otherwise>
                </c:choose>
                </tbody>
                <tfoot>
                  <tr>
                    <td colspan="7"><hl:message key="rotulo.copia.seguranca.lista"/></td>
                  </tr>
                </tfoot>
              </table>
            </div>
          </div>
        </div>
      </div>
          <div class="card">
            <div class="card-header hasIcon">
              <span class="card-header-icon"><svg width="25"><use xlink:href="../img/sprite.svg#i-relatorio"></use></svg></span>
              <h2 class="card-header-title"><hl:message key="rotulo.listar.arq.copia.seguranca.importar"/></h2>
            </div>
            <div class="card-body table-responsive p-0">
              <table class="table table-striped table-hover">
                <thead>
                  <tr>
                    <th><hl:message key="rotulo.arquivo.nome"/></th>
                    <th><hl:message key="rotulo.arquivo.tamanho"/></th>
                    <th><hl:message key="rotulo.arquivo.data"/></th>
                    <th><hl:message key="rotulo.acoes"/></th>
                  </tr>
                </thead>
                <tbody>
                <c:choose>
                  <c:when test="${empty arquivosDTO}">
                  <tr>
                    <td colspan="6"><hl:message key="rotulo.nenhum.arquivo.encontrado"/></td>
                  </tr>
                  </c:when>
                  <c:otherwise>
                    <c:forEach items="${arquivosDTO}" var="arq" varStatus="arqStatus">                    
                    <tr>
                      <td>${arq.originalNome}</td>
                      <td>${arq.tam}</td>
                      <td>${arq.data}</td>
                      <td>
                        <div class="actions">
                          <div class="dropdown">
                            <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                              <div class="form-inline">
                                <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.botao.opcoes"/>" aria-label="<hl:message key="rotulo.botao.opcoes"/>">
                                  <svg><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-engrenagem"></use></svg>
                                </span>
                                <hl:message key="rotulo.botao.opcoes"/>
                              </div>
                            </a>
                            <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                              <a class="dropdown-item" href="#no-back" aria-label="<hl:message key="rotulo.botao.aria.importar.copia.seguranca" arg0="${arq.nome}"/>" onClick="doIt('r', '${arq.originalNome}', '${arqStatus.index}'); return false;"><hl:message key="rotulo.botao.importar"/></a>
                              <a class="dropdown-item" href="#no-back" aria-label="<hl:message key="rotulo.botao.aria.excluir.copia.seguranca" arg0="${arq.nome}"/>" onClick="doIt('e', '${arq.originalNome}', '${arq.nome}'); return false;"><hl:message key="rotulo.botao.excluir"/></a>
                            </div>
                          </div>
                        </div>
                      </td>
                    </tr>
                    </c:forEach>
                  </c:otherwise>
                </c:choose>
                </tbody>
                <tfoot>
                  <tr>
                    <td colspan="5"><hl:message key="mensagem.rodape.tabela.copia.seguranca.arquivos" arg0="<%=DateHelper.format(DateHelper.getSystemDate(), LocaleHelper.getMediumDatePattern())%>"/>
                    <span class="font-italic"> - <hl:message key="rotulo.paginacao.registros.sem.estilo" arg0="${_paginacaoPrimeiro}" arg1="${_paginacaoUltimo}" arg2="${_paginacaoQtdTotal}"/></span></td>
                  </tr>
                </tfoot>
              </table>
            </div>
            <div class="card-footer">
            <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
            </div>
          </div>
      <div class="btn-action">
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')" alt="<hl:message key="rotulo.botao.voltar"/>" title="<hl:message key="rotulo.botao.voltar"/>"><hl:message key="rotulo.botao.voltar"/></a>
      </div>
    </form>
  </c:if>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
function doIt(opt, arq, path) {
  var msg = '', j;
  if (opt == 'b') {
    msg = '<hl:message key="mensagem.confirmacao.exportar.copia.seguranca"/>'.replace("{0}", arq);
    j = '../v3/copiaSeguranca?acao=exportar&<%=SynchronizerToken.generateToken4URL(request)%>&agendamento=' + encodeURIComponent(path);
  } else if (opt == 'd') {
    msg = '<hl:message key="mensagem.confirmacao.desagendar.copia.seguranca"/>'.replace("{0}", arq);
    j = '../v3/copiaSeguranca?acao=desagendar&<%=SynchronizerToken.generateToken4URL(request)%>&agendamento=' + encodeURIComponent(path);
  } else if (opt == 'e') {
    msg = '<hl:message key="mensagem.confirmacao.exclusao.arquivo"/>'.replace("{0}", arq);
    j = '../v3/excluirArquivo?<%=SynchronizerToken.generateToken4URL(request)%>&arquivo_nome=' + encodeURIComponent(path) + '&tipo=backup';
  } else if (opt == 'r') {
    msg = '<hl:message key="mensagem.confirmacao.importacao.copia.seguranca"/>'.replace("{0}", arq);
    j = '../v3/copiaSeguranca?acao=importar&<%=SynchronizerToken.generateToken4URL(request)%>&arquivo_indice=' + encodeURIComponent(path);
  } else {
    return false;
  }
  if (msg != '') {
    ConfirmaUrl(msg, j);
  } else {
    postData(j);
  }
  return true;
}

function vf_periodo_cs() {
  if (camposObrigatorios != '' && camposObrigatorios[camposObrigatorios.length - 1] == ',') {
    camposObrigatorios = camposObrigatorios.substring(0, camposObrigatorios.length - 1);
  }
  
  if (msgCamposObrigatorios != '' && msgCamposObrigatorios[msgCamposObrigatorios.length - 1] == ',') {
    msgCamposObrigatorios = msgCamposObrigatorios.substring(0, msgCamposObrigatorios.length - 1);
  }
  
  if (msgCamposObrigatorios != '' && msgCamposObrigatorios.indexOf("*") >= 0) {
    msgCamposObrigatorios = msgCamposObrigatorios.replace(/\*/g,"");
  } 

  var _controles = camposObrigatorios.split(',');   
  var _msgs = msgCamposObrigatorios.split(',');
  
 
  limparErros();
  
  if (_controles != null && _controles.length > 0 && _controles[0].trim() != '') {
    if (!validarCampos("mensagens", _controles, _msgs)) {
      return false;
    }
  }

  return true;
}

function formLoad() {
  enableAll();
  focusFirstField();
}

function doLoad(reload) {
  if (reload) {
    setTimeout("refresh()", 10*1000);
  }
}

function refresh(param) {
  postData("../v3/copiaSeguranca?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>" + (param ? "&" + param : ""));
}

var f0 = document.forms[0];
var camposObrigatorios = '';
var msgCamposObrigatorios = '';
addLoadEvent(function() { formLoad(); doLoad(${temProcessoRodando}); });
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>