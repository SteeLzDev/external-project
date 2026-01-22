<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="java.io.*"%>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

String csa_codigo = (String) request.getAttribute("csa_codigo");
String todos = (String) request.getAttribute("todos");
String adeIndice = (String) request.getAttribute("adeIndice");
String path = (String) request.getAttribute("path");
boolean permiteCadIndice = (boolean) request.getAttribute("permiteCadIndice");
List<TransferObject> servicos = (List<TransferObject>) request.getAttribute("servicos");
List<TransferObject> consignatarias = (List<TransferObject>) request.getAttribute("consignatarias");
List<File> arquivos = (List<File>) request.getAttribute("arquivos");

%>
<c:set var="title">
  <hl:message key="rotulo.duplicar.parcela.titulo"/>
</c:set>
<c:set var="imageHeader">
    <use xlink:href="#i-operacional"></use>
</c:set>
<c:set var="bodyContent">
<form name="form1" method="post" action="../v3/duplicarParcela?_skip_history_=true&<%=SynchronizerToken.generateToken4URL(request)%>">
  <input name="acao" type="hidden" id="acao" value="iniciar">
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="mensagem.duplicar.parcela.informe.dados"/></h2>
    </div>
    <div class="card-body">
    <% if (responsavel.isCseSupOrg()) { %>
      <div class="row">
        <div class="form-group col-sm-12  col-md-6">
          <label for="csa_codigo"><hl:message key="rotulo.consignataria.singular"/></label>
          <select name="csa_codigo" id="csa_codigo"
                  class="form-control form-select" onFocus="SetarEventoMascara(this,'#*200',true);"
                  onBlur="fout(this);ValidaMascara(this);"
                  onChange="AlteraCsa()">
            <option selected value=""><hl:message key="rotulo.campo.selecione"/></option>
            <%
            
              Collections.sort(consignatarias, new Comparator<TransferObject>() {
              public int compare(TransferObject c1, TransferObject c2) {
                String nome1 = (c1.getAttribute(Columns.CSA_NOME_ABREV) != null && !c1.getAttribute(Columns.CSA_NOME_ABREV).toString().trim().isEmpty()) ? (String)c1.getAttribute(Columns.CSA_NOME_ABREV) : (String)c1.getAttribute(Columns.CSA_NOME);
                nome1 += " - " + (String)c1.getAttribute(Columns.CSA_IDENTIFICADOR);
                String nome2 = (c2.getAttribute(Columns.CSA_NOME_ABREV) != null && !c2.getAttribute(Columns.CSA_NOME_ABREV).toString().trim().isEmpty()) ? (String)c2.getAttribute(Columns.CSA_NOME_ABREV) : (String)c2.getAttribute(Columns.CSA_NOME);
                nome2 += " - " + (String)c2.getAttribute(Columns.CSA_IDENTIFICADOR);
                return nome1.compareTo(nome2);
              }});
            
              Iterator<TransferObject> it = consignatarias.iterator();
              TransferObject consignataria;
              String codigo, nome;
              while (it.hasNext()) {
                consignataria = it.next();
                codigo = (String)consignataria.getAttribute(Columns.CSA_CODIGO);
                nome = (consignataria.getAttribute(Columns.CSA_NOME_ABREV) != null && !consignataria.getAttribute(Columns.CSA_NOME_ABREV).toString().trim().isEmpty()) ? (String)consignataria.getAttribute(Columns.CSA_NOME_ABREV) : (String)consignataria.getAttribute(Columns.CSA_NOME);
                nome += " - " + (String)consignataria.getAttribute(Columns.CSA_IDENTIFICADOR);
            %>
            <option value="<%=TextHelper.forHtmlAttribute(codigo)%>" <%=(JspHelper.verificaVarQryStr(request, "csa_codigo").equals(codigo)) ? "selected" : ""%>><%=TextHelper.forHtmlContent(nome)%></option>
            <%
              }
            %>
          </select>
        </div>
          <% } else { %>
            <input type="hidden" name="csa_codigo" value="<%=TextHelper.forHtmlAttribute(csa_codigo)%>">
          <% } %>
          <div class="form-group col-sm-12  col-md-6">
            <label for="rubrica"><hl:message key="rotulo.servico.singular"/></label>
            <select name="rubrica" id="rubrica"
                    class="form-control form-select" onFocus="SetarEventoMascara(this,'#*200',true);"
                    onBlur="fout(this);ValidaMascara(this);">
              <option selected value=""><hl:message key="rotulo.campo.selecione"/></option>
              <%
                Iterator<TransferObject> it = servicos.iterator();
                while (it.hasNext()) {
                  TransferObject servico = it.next();
                  String cnvCodVerba = (String)servico.getAttribute(Columns.CNV_COD_VERBA);
                  String cnvDescVerba = (String)servico.getAttribute(Columns.CNV_COD_VERBA) + " - " + (String)servico.getAttribute(Columns.SVC_DESCRICAO);
                  if (cnvCodVerba != null && !cnvCodVerba.equals("")) {
              %>
              <option value="<%=TextHelper.forHtmlAttribute(cnvCodVerba)%>" <%=(JspHelper.verificaVarQryStr(request, "rubrica").equals(cnvCodVerba)) ? "selected" : ""%>><%=TextHelper.forHtmlContent(cnvDescVerba)%></option>
              <%
                  }
                }
              %>
            </select>
          </div>
        </div>
        <%
          if (permiteCadIndice) {
        %>
        <div class="row">
          <div class="form-group col-sm-12 col-md-6">
            <label for="adeIndice"><hl:message key="rotulo.consignacao.indice"/></label>
            <hl:htmlinput name="adeIndice" type="text" classe="form-control" di="adeIndice" size="8" mask="#D2" value="<%=TextHelper.forHtmlAttribute(adeIndice)%>" readonly="false" />
            </div>
            <div class="form-check align-self-center col-sm-12 col-md-6 ">
              <input name="chkAdeIndiceTodos" class="form-check-input  ml-1" id="chkAdeIndiceTodos" type="checkbox" value="S" onClick="SetaAdeIndice(this , document.forms[0].adeIndice);" <%=TextHelper.forHtmlContent(todos)%>>
              <label class="form-check-label labelSemNegrito ml-1" for="chkAdeIndiceTodos"><hl:message key="rotulo.campo.todos.simples"/></label>
            </div>
        </div>
        <%
          }
        %>
        <div class="row">
          <div class="form-group col-sm-6">
            <label for="prd_mult"><hl:message key="rotulo.duplicar.parcela.multiplicador"/></label>
            <hl:htmlinput di="prd_mult" name="prd_mult" type="text" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, \"prd_mult\"))%>" classe="form-control" size="8" mask="#F11" onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); f0.valor.value=''; }"/>
          </div>
          <div class="form-group col-sm-6">
            <label for="valor"><hl:message key="rotulo.duplicar.parcela.valor"/></label>
            <hl:htmlinput di="valor" name="valor" type="text" value="<%=TextHelper.forHtmlAttribute(JspHelper.verificaVarQryStr(request, \"valor\"))%>" classe="form-control" size="8" mask="#F11" onBlur="if (this.value != '') { this.value = FormataContabil(parse_num(this.value), 2); f0.prd_mult.value=''; }"/>
          </div>
        </div>
      </div>
    </div>
    <div class="btn-action">
      <a href="#no-back" class="btn btn-outline-danger" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
      <a href="#no-back" class="btn btn-primary" onClick="verificaCampos('processarArquivo'); return false;"><hl:message key="rotulo.botao.processar"/></a>
      <a href="#no-back" class="btn btn-primary" onClick="verificaCampos('validarArquivo'); return false;"><hl:message key="rotulo.botao.validar"/></a>
    </div>
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><hl:message key="mensagem.duplicar.parcela.lista.arq.disponiveis"/></h2>
      </div>
      <div class="card-body table-responsive p-0">
        <table class="table table-striped table-hover">
          <thead>
            <tr>
              <th scope="col"><hl:message key="rotulo.duplicar.parcela.arquivo.nome"/></th>
              <th scope="col"><hl:message key="rotulo.duplicar.parcela.arquivo.tamanho"/></th>
              <th scope="col"><hl:message key="rotulo.duplicar.parcela.arquivo.data"/></th>
              <th scope="col"><hl:message key="rotulo.acoes"/></th>
            </tr>
          </thead>
          <tbody>
          <%if (arquivos == null || arquivos.size() == 0){ %>
          <tr class="Lp">
            <td colspan="6"><hl:message key="mensagem.duplicar.parcela.arquivo.nao.encontrado"/></td>
          </tr>
          <%
              } else {
                      Iterator iter = arquivos.iterator();
                      String lp = "Lp";
                      String data, nome, formato, conversor;
                      while (iter.hasNext()) {
                        File arquivo = (File)iter.next();
                        String tam = "";
                        if (arquivo.length() > 1024.00) {
                          tam = Math.round(arquivo.length() / 1024.00) + " " + ApplicationResourcesHelper.getMessage("rotulo.kilobyte.abreviado", responsavel);
                        } else {
                          tam = arquivo.length() + " " + ApplicationResourcesHelper.getMessage("rotulo.byte.abreviado", responsavel);
                        }
                        data = DateHelper.toDateTimeString(new java.sql.Date(arquivo.lastModified()));
                        nome = arquivo.getPath().substring(path.length() + 1);
                        formato = "";
                        conversor = null;
                        if (nome.toLowerCase().endsWith(".pdf")) {
                          formato = "pdf.gif";
                        } else if (nome.toLowerCase().endsWith(".txt")) {
                          formato = "text.gif";
                          conversor = "zip.gif";
                        } else if (nome.toLowerCase().endsWith(".htm")) {
                          formato = "html.gif";
                        } else if (nome.toLowerCase().endsWith(".zip")) {
                          formato = "zip.gif";
                          conversor = "text.gif";
                        }

                        nome = java.net.URLEncoder.encode(nome, "UTF-8");
                        lp = (!lp.equals("Lp"))?"Lp":"Li";
          %>
          <tr>
            <td><%=TextHelper.forHtmlContent(arquivo.getName())%></td>
            <td align="right"><%=TextHelper.forHtmlContent(tam)%></td>
            <td align="center"><%=TextHelper.forHtmlContent(data)%></td>
            <td>
              <div class="actions">
                <div class="dropdown">
                  <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <div class="form-inline">
                      <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="Mais ações" aria-label="Mais ações">
                        <svg><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                      </span><hl:message key="rotulo.botao.opcoes"/>
                    </div>
                  </a>
                  <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                    <a class="dropdown-item" href="#no-back" onClick="fazDownload('<%=TextHelper.forJavaScript(nome)%>', '<%=TextHelper.forJavaScript(csa_codigo)%>');"><hl:message key="rotulo.acoes.download"/></a>
                    <a class="dropdown-item" href="#no-back" onClick="doIt('e', '<%=TextHelper.forJavaScript(arquivo.getName())%>','<%=TextHelper.forJavaScript(nome)%>'); return false;"><hl:message key="rotulo.acoes.excluir"/></a>
                  </div>
                </div>
              </div>
            </td>
          </tr>
          <%
              }
            }
          %>
        </tbody>
        <tfoot>
          <tr>
            <td colspan="8">
              <hl:message key="rotulo.duplicar.parcela.listgem"/> 
            </td>
          </tr>
        </tfoot>
      </table>
    </div>
  </div>
</form>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
var f0 = document.forms[0];
function formLoad() {
    changeForm();
  }
</script>
<script type="text/JavaScript">
function verificaCampos(botao) {
<% if (responsavel.isCseSup()) { %>
  var Controles = new Array('csa_codigo', 'rubrica');
  var Msgs = new Array('<hl:message key="mensagem.informe.consignataria"/>',
                       '<hl:message key="mensagem.informe.servico"/>');
<% } else { %>
  var Controles = new Array('rubrica');
  var Msgs = new Array('<hl:message key="mensagem.informe.servico"/>');
<% } %>

  if (confirm(botao == 'validarArquivo' ? '<hl:message key="mensagem.confirmacao.duplicar.parcela.validar"/>' : '<hl:message key="mensagem.confirmacao.duplicar.parcela.processar"/>')) {
    if (ValidaCampos(Controles, Msgs)) {
      <% if (permiteCadIndice) { %>
         if ((!f0.chkAdeIndiceTodos.checked) && (f0.adeIndice.value == '')) {
            alert('<hl:message key="mensagem.informe.ade.indice"/>');
         } else if ((f0.prd_mult.value == '') && (f0.valor.value == '')) {
            alert('<hl:message key="mensagem.informe.duplicar.parcela.valor.multiplicador"/>');
         } else {
            f0.acao.value = botao;
            f0.submit();
         }
      <%} else {%>
          if ((f0.prd_mult.value == '') && (f0.valor.value == '')) {
            alert('<hl:message key="mensagem.informe.duplicar.parcela.valor.multiplicador"/>');
          } else {
            f0.acao.value = botao;
            f0.submit();
          }
      <%} %>
    }
  }
  return false;
}

function AlteraCsa() {
  f0.acao.value = 'iniciar';
  f0.submit();
  return true;
}

function SetaAdeIndice(chkBox, inputText) {
  if (chkBox.checked) {
    inputText.value = '';
    inputText.enabled = false;
    inputText.disabled = true;
  } else {
    inputText.enabled = true;
    inputText.disabled = false;
  }
  return true;
}

function formLoad() {
  focusFirstField();
}

function doIt(opt, arq, path) {
  var msg = '', j;
  if (opt == 'e') {
    msg = '<hl:message key="mensagem.confirmacao.duplicar.parcela.excluir.arquivo"/>'.replace('{0}', arq);
    <%
    String link = "../v3/duplicarParcela?acao=iniciar&csa_codigo=" + csa_codigo + "&" + SynchronizerToken.generateToken4URL(request);
    link = link.replace('?','$').replace('&','|').replace('=','(');
    %>
    j = '../v3/excluirArquivo?arquivo_nome=' + encodeURIComponent(path) + '&tipo=duplicacao&subtipo=<%=TextHelper.forJavaScriptBlock(csa_codigo)%>&entidade=csa&<%=SynchronizerToken.generateToken4URL(request)%>&link=<%=TextHelper.forJavaScriptBlock(link)%>';
    
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

function fazDownload(nome, csaCodigo){
  postData('../v3/downloadArquivo?arquivo_nome=' + encodeURIComponent(nome) + '&tipo=duplicacao&entidade=csa&subtipo=' + csaCodigo + '&skip_history=true' + '&<%=SynchronizerToken.generateToken4URL(request)%>','download');
}
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>