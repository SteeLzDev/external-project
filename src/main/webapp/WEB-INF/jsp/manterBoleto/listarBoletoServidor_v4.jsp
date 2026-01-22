<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="java.net.URLEncoder" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>

<%
AcessoSistema responsavel = (AcessoSistema) request.getAttribute("responsavel");
boolean temProcessoRodando = (boolean) request.getAttribute("temProcessoRodando");
List boletoServidor = (List) request.getAttribute("boletoServidor");

int filtroTipo = (int) request.getAttribute("filtroTipo");
String filtro = (String) request.getAttribute("filtro");

boolean exibeCaptcha = (boolean) request.getAttribute("exibeCaptcha");
boolean exibeCaptchaAvancado = (boolean) request.getAttribute("exibeCaptchaAvancado");
boolean exibeCaptchaDeficiente = (boolean) request.getAttribute("exibeCaptchaDeficiente");

%>
<c:set var="title">
  <hl:message key="rotulo.upload.boleto.lote.titulo" />
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <!-- Pesquisar -->
    <div class="col-sm-5 col-md-4">
      <div class="card">
        <div class="card-header hasIcon">
          <span class="card-header-icon"><svg width="26"><use xlink:href="#i-upload"></use></svg></span>
          <h2 class="card-header-title"><hl:message key="rotulo.upload.arquivo.titulo"/></h2>
        </div>
        <div class="card-body">
          <form name="form1" action="../v3/manterBoleto?acao=upload&<%=SynchronizerToken.generateToken4URL(request)%>" method="post" enctype="multipart/form-data">
            <div class="row">
              <div class="form-group col-sm-12">
                <label for="arquivo"><hl:message key="rotulo.upload.arquivo.arquivo"/></label>
                <input type="file" class="form-control" id="arquivo" name="FILE1">
              </div>
            </div>
            <div class="row">
              <% if (exibeCaptcha || exibeCaptchaDeficiente) { %>
                <div class="form-group col-sm-5">
                  <label for="captcha"><hl:message key="rotulo.captcha.codigo"/>:</label>
                  <input type="text" class="form-control" id="captcha" name="captcha" placeholder='<hl:message key="mensagem.informacao.login.digite.codigo.acesso"/>'>
                </div>
               <% } %>
               <div class="form-group col-sm-6">
                 <div class="captcha">
                   <% if (exibeCaptcha) { %>
                     <img name="captcha_img" src="../captcha.jpg?t=<%=DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss")%>" alt='<hl:message key="rotulo.captcha.codigo"/>' title='<hl:message key="rotulo.captcha.codigo"/>'/>
                     <a href="#no-back" onclick="reloadCaptcha()"><img src="../img/icones/refresh.png" alt='<hl:message key="rotulo.captcha.novo.codigo"/>' title='<hl:message key="rotulo.captcha.novo.codigo"/>' border="0"/></a>
                     <a href="#no-back" class="btn-i-right pr-1" data-bs-toggle="popover" title="<hl:message key="rotulo.ajuda" />"
                       data-bs-content='<hl:message key="mensagem.ajuda.captcha.operacao.imagem.v3"/>'
                       data-original-title=<hl:message key="rotulo.ajuda" />>
                       <img src="../img/icones/help.png" alt='<hl:message key="rotulo.ajuda" />' title='<hl:message key="rotulo.ajuda" />' border="0">
                     </a>
                   <% } else if (exibeCaptchaAvancado) { %>
                     <hl:recaptcha />
                   <% } else if (exibeCaptchaDeficiente) {%>
                        <div id="divCaptchaSound"></div>
                        <a href="#no-back" onclick="reloadSimpleCaptcha()"><img src="../img/icones/refresh.png" alt='<hl:message key="rotulo.captcha.novo.audio"/>' title='<hl:message key="rotulo.captcha.novo.audio"/>' border="0"/></a>
                        <a href="#no-back" onclick="helpCaptcha3();"><img src="../img/icones/help.png" alt='<hl:message key="rotulo.ajuda"/>' title='<hl:message key="rotulo.ajuda"/>' border="0"/></a>
                   <% } %>
               </div>
               </div>
            </div>
          </form>
        </div>
      </div>
      <div class="btn-action">
        <a class="btn btn-outline-danger" aria-label="<hl:message key="rotulo.botao.voltar"/>" href="#" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar"/></a>
        <% if (!temProcessoRodando) { %>
        <a class="btn btn-primary" aria-label="<hl:message key="rotulo.botao.confirmar"/>" href="#" onClick="if(vf_upload_arquivos()){ form1.submit(); } return false;"><svg width="17"><use xlink:href="#i-confirmar"></use></svg> <hl:message key="rotulo.botao.confirmar"/></a>
        <% } %>
      </div>
    </div>

    <!-- Boleto servidor -->
    <div class="col-sm-7 col-md-8">
      <div class="card">
        <div class="card-header hasIcon pl-3">
          <h2 class="card-header-title">
            <hl:message key="rotulo.acao.pesquisar" />
          </h2>
        </div>
        <div class="card-body">
          <FORM NAME="formFiltro" METHOD="post" ACTION="../v3/manterBoleto?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>">
            <div class="row">
              <div class="form-group col-sm">
                <label for="iFiltro"><hl:message key="rotulo.boleto.servidor.filtro" /></label>
                <input
                  type="text" class="form-control" id="iFiltro"
                  name="FILTRO"
                  placeholder="<hl:message key='rotulo.acao.digite.filtro' />"
                  SIZE="10"
                  VALUE="<%=TextHelper.forHtmlAttribute(filtro)%>"
                  onFocus="SetarEventoMascara(this,'#*200',true);"
                  onBlur="fout(this);ValidaMascara(this);">
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm">
                <label for="FILTRO_TIPO"><hl:message key="rotulo.acao.filtrar.por" /></label>
                <select class="form-control form-select select" id="FILTRO_TIPO" name="FILTRO_TIPO" onFocus="SetarEventoMascara(this,'#*200',true);" onBlur="fout(this);ValidaMascara(this);">
                  <optgroup label="<hl:message key='rotulo.boleto.servidor.filtros'/>">
                    <OPTION VALUE=""  <%=(String) ((filtroTipo == -1) ? "SELECTED" : "")%>><hl:message key="rotulo.campo.sem.filtro" /></OPTION>
                    <OPTION VALUE="02" <%=(String) ((filtroTipo == 2) ? "SELECTED" : "")%>><hl:message key="rotulo.servidor.singular" /></OPTION>
                    <OPTION VALUE="03" <%=(String) ((filtroTipo == 3) ? "SELECTED" : "")%>><hl:message key="rotulo.servidor.cpf" /></OPTION>
                  </optgroup>
                </select>
              </div>
            </div>
          </form>
        </div>
      </div>
      <div class="btn-action">
        <a class="btn btn-primary" href="#no-back" onClick="formFiltro.submit(); return false;">
        <svg width="20"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="../img/sprite.svg#i-consultar"></use></svg>
          <hl:message key="rotulo.acao.pesquisar" />
        </a>
      </div>
      <div class="card">
        <div class="card-header hasIcon pl-3">
          <h2 class="card-header-title">
            <hl:message key="rotulo.upload.boleto.lote.titulo" />
          </h2>
        </div>
        <div class="card-body">
          <div class="table-responsive">
            <table class="table table-striped table-hover">
              <thead>
                <tr>
                  <th scope="col"><hl:message key="rotulo.servidor.singular" /></th>
                  <th scope="col"><hl:message key="rotulo.servidor.cpf" /></th>
                  <th scope="col"><hl:message key="rotulo.boleto.servidor.data.upload" /></th>
                  <th scope="col"><hl:message key="rotulo.boleto.servidor.data.download" /></th>
                  <th scope="col"><hl:message key="rotulo.acoes" /></th>
                </tr>
              </thead>
              <tbody>
                <%
                  Iterator<TransferObject> it = boletoServidor.iterator();
                        while (it.hasNext()) {
                            TransferObject arquivo = (TransferObject) it.next();

                            String bosCodigo = arquivo.getAttribute(Columns.BOS_CODIGO).toString();
                            String serNome = arquivo.getAttribute(Columns.SER_NOME).toString();
                            String serCpf = arquivo.getAttribute(Columns.SER_CPF).toString();

                            String dataUpload = "";
                            if (!TextHelper.isNull(arquivo.getAttribute(Columns.BOS_DATA_UPLOAD))) {
                              dataUpload = DateHelper.toDateTimeString((Date) arquivo.getAttribute(Columns.BOS_DATA_UPLOAD));
                            }
                            String dataDownload = "";
                            if (!TextHelper.isNull(arquivo.getAttribute(Columns.BOS_DATA_DOWNLOAD))) {
                              dataDownload = DateHelper.toDateTimeString((Date) arquivo.getAttribute(Columns.BOS_DATA_DOWNLOAD));
                            }

                %>
                <tr>
                  <td><%=TextHelper.forHtmlContent(serNome)%></td>
                  <td><%=TextHelper.forHtmlContent(serCpf)%></td>
                  <td><%=TextHelper.forHtmlContent(dataUpload)%></td>
                  <td><%=TextHelper.forHtmlContent(dataDownload)%></td>
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
                          <div class="position-relative">
	                        <a href="#no-back" class="dropdown-item" onClick="fazDownload('<%=TextHelper.forJavaScript(bosCodigo)%>'); return false;"><hl:message key="rotulo.acoes.download"/>&nbsp;</a>
                            <a href="#no-back" class="dropdown-item" onClick="doIt('e', '<%=TextHelper.forJavaScript(bosCodigo)%>', '<%=TextHelper.forJavaScript(serNome)%>'); return false;"><hl:message key="rotulo.acoes.excluir"/>&nbsp;</a>
                          </div>
                        </div>
                      </div>
                    </div>
                  </td>
                </tr>
                <%
                    }
                %>
              </tbody>
              <tfoot>
                <tr>
                  <td colspan="4">
                     <hl:message key="rotulo.lote.listagem.boletos" />
                     <span class="font-italic"> - <%=request.getAttribute("_paginacaoSubTitulo")%></span>
                  </td>
                </tr>
              </tfoot>
            </table>
            <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
          </div>
          <div class="btn-action">
            <a class="btn btn-outline-danger" aria-label='<hl:message key="rotulo.botao.voltar"/>' href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.voltar" /></a>
          </div>
        </div>
      </div>
    </div>
  </div>
</c:set>
<c:set var="javascript">
<% if (exibeCaptchaAvancado) { %>
<script src='https://www.google.com/recaptcha/api.js'></script>
<% } %>
<% if (exibeCaptchaDeficiente) {%>
<script type="text/JavaScript">
  montaCaptchaSom();
</script>
<% } %>
<script type="text/JavaScript">
function doIt(opt, codigo, arq) {
      var msg = '', j;
      if (opt == 'e') {
      msg = '<hl:message key="mensagem.confirmacao.exclusao.boleto"/>'.replace("{0}", arq);
        j = '../v3/manterBoleto?acao=excluir&bosCodigo=' + codigo + '&_skip_history_=true';
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

function doLoad(reload) {
  if (reload) {
    setTimeout("refresh()", 15*1000);
  }
}

function refresh() {
  postData("../v3/manterBoleto?acao=iniciar&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>");
}

function fazDownload(codigo){
  postData('../v3/manterBoleto?acao=download&bosCodigo=' + codigo + '&_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>', 'download');

}

function vf_upload_arquivos() {
  var controles = '';
  var msgs = '';

  controles = new Array("FILE1");
  msgs = new Array ('<%=ApplicationResourcesHelper.getMessage("mensagem.aviso.upload.selecione.arquivo", responsavel)%>');

  return ValidaCampos(controles, msgs);
}

window.onload = doLoad(<%=(boolean)temProcessoRodando%>);

</script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>