<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.util.regex.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%

AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

boolean podeEditarBanner = (boolean) request.getAttribute("podeEditarBanner");

String csaCodigo = (String) request.getAttribute("csaCodigo");
String separatorString = (String) request.getAttribute("separatorString");

List<?> consignatarias = (List<?>) request.getAttribute("consignatarias");
List<?> listFilesOffset = (List<?>) request.getAttribute("listFilesOffset");
%>
<c:set var="title">
<hl:message key="rotulo.conf.banner.titulo"/>
</c:set>
<c:set var="imageHeader">
   <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">   

<FORM NAME="form1" METHOD="POST" ACTION="../v3/manterBanner?acao=upload&<%=SynchronizerToken.generateToken4URL(request)%>" ENCTYPE="multipart/form-data">
  <input name="FORM" type="hidden" value="form1">
  <input NAME="CSA_CODIGO" TYPE="HIDDEN" VALUE="">
  <% if (podeEditarBanner) { %>
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.upload.arquivo.titulo"/></h2>
    </div>
    <div class="card-body">
      <div class="row">
        <div class="form-group col-sm-6">
          <label for="arquivo"><hl:message key="rotulo.upload.arquivo.arquivo"/></label>
          <input type="file" class="form-control" id="arquivo" SIZE="50" name="FILE1">
        </div>
      </div>
      <div class="row">
        <div class="form-group col-sm-6">
          <label for="CSA_CODIGO_AUX"><hl:message key="rotulo.consignataria.singular"/></label>
          <select class="form-control form-select" id="CSA_CODIGO_AUX" name="csa_codigo_aux" size="4">
            <%
                 Iterator itCsa = consignatarias.iterator();
                 CustomTransferObject csa = null;
                 String csa_nome = null;
                 while (itCsa.hasNext()) {
                    csa = (CustomTransferObject)itCsa.next();
  
                    csa_nome = (String)csa.getAttribute(Columns.CSA_NOME_ABREV);
                    if (csa_nome == null || csa_nome.trim().length() == 0)
                        csa_nome = csa.getAttribute(Columns.CSA_NOME).toString();
              %>
                 <OPTION VALUE="<%=TextHelper.forHtmlAttribute(csa.getAttribute(Columns.CSA_CODIGO))%>"><%=TextHelper.forHtml((csa_nome))%></OPTION>
             <%
                 }
             %>
          </select>
        </div>
      </div>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger" aria-label="<hl:message key="rotulo.botao.voltar"/>" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;"><hl:message key="rotulo.botao.cancelar"/></a>
    <a class="btn btn-primary" type="submit" onclick="if(vf_upload_arquivos()){f0.submit();} return false;" href="#no-back"><hl:message key="rotulo.botao.confirmar"/></a>
  </div>
</form>
<% } %> 
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.conf.banner.tabela.titulo"/></h2>
    </div>
    <div class="card-body table-responsive p-0">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col"><hl:message key="rotulo.conf.banner.banner"/></th>
            <th scope="col"><hl:message key="rotulo.consignataria.singular"/></th>
            <th scope="col"><hl:message key="rotulo.usuario.situacao"/></th>
            <th scope="col"><hl:message key="rotulo.acoes"/></th>
          </tr>
        </thead>
        <tbody>
          <%
           
           if (!listFilesOffset.isEmpty()) { 
               Iterator it = listFilesOffset.iterator();
               List jaAdicionadas = new ArrayList();
               while (it.hasNext()) {
                   String fileName = (String) it.next();
        
                   boolean ativo = (!fileName.endsWith(".inativo")) ? true:false;
                   String displayName = (!ativo) ? fileName.replaceAll(".inativo", ""):fileName;
                   int lastFileSeparator = displayName.lastIndexOf(separatorString);
                   String imgCsaName = "";
                   if (lastFileSeparator != -1) {
                       int penultimoFileSeparator = displayName.lastIndexOf(separatorString,(!separatorString.equals("\\")) ? (lastFileSeparator - 1):(lastFileSeparator - 2));
                       String imgCsaCod = displayName.substring(penultimoFileSeparator + 1, lastFileSeparator);
                       Iterator itCsa = consignatarias.iterator();
                       while (itCsa.hasNext()) {
                           CustomTransferObject csa = (CustomTransferObject) itCsa.next();
                           if (csa.getAttribute(Columns.CSA_CODIGO).equals(imgCsaCod)) {
                               imgCsaName = (String)csa.getAttribute(Columns.CSA_NOME_ABREV);
                               if (TextHelper.isNull(imgCsaName)) {
                                   imgCsaName = csa.getAttribute(Columns.CSA_NOME).toString();
                               }
                               break;
                           }
                       }
                       
                   }
                   if (jaAdicionadas.contains(displayName)) {
                       continue;
                   } else {
                       jaAdicionadas.add(displayName);
                   }           
           %>
           <tr>
            <td><%=TextHelper.forHtmlContent(displayName.substring(lastFileSeparator + 1, displayName.length()))%></td>
            <td><%=TextHelper.forHtmlContent(imgCsaName)%></td>
            <td class="<%=(String)((ativo) ? "":"block" )%>"><%=(String)((ativo) ? ApplicationResourcesHelper.getMessage("rotulo.usuario.papel.desbloqueado", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.usuario.papel.bloqueado", responsavel))%></td>
            <td>
              <div class="actions">
                <div class="dropdown">
                  <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    <div class="form-inline">
                       <span class=" mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.mais.acoes"/>" aria-label="<hl:message key="rotulo.mais.acoes"/>"> <svg>
                         <use xlink:href="#i-engrenagem"></use></svg>
                       </span> <hl:message key="rotulo.botao.opcoes"/>
                    </div>
                  </a>
                  <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                    <a class="dropdown-item" href="#no-back" onClick="visualizar('<%=TextHelper.forJavaScript(fileName.replaceAll((separatorString.equals("\\")) ? "\\\\":separatorString, "/"))%>')"><hl:message key="rotulo.acoes.visualizar"/></a>
                     <%if (podeEditarBanner) { %>
                    <a class="dropdown-item" href="#no-back" onClick="postData('../v3/manterBanner?acao=editar&operacao=<%=(String)((ativo) ? "bloquear":"ativar" )%>&arq=<%=TextHelper.forJavaScriptAttribute(fileName)%>&<%=SynchronizerToken.generateToken4URL(request)%>')"><hl:message key="rotulo.acoes.bloquear.desbloquear"/></a>
                    <a class="dropdown-item" href="#no-back" onclick="apagar('<%=TextHelper.forJavaScript(displayName.substring(lastFileSeparator + 1, displayName.length()))%>','<%=TextHelper.forJavaScript((fileName.endsWith(".inativo")) ? displayName + ".inativo":displayName)%>')"><hl:message key="rotulo.acoes.remover"/></a> 
                    <% } %>  
                  </div>
                </div>
              </div>
               <% 
               }       
           } else {%>
               <tr>
                 <td colspan="5"><hl:message key="rotulo.conf.banner.encontrado"/></td>
               </tr>
           <% } %>
         </tbody>
         <tfoot>
          <tr>
            <td colspan="12">
              <%=ApplicationResourcesHelper.getMessage("mensagem.listagem.banner", responsavel) + " - "%>
              <span class="font-italic"> <hl:message key="rotulo.paginacao.registros.sem.estilo" arg0="${_paginacaoPrimeiro}" arg1="${_paginacaoUltimo}" arg2="${_paginacaoQtdTotal}"/></span>
            </td>                
          </tr>
        </tfoot>
       </table>
     </div>
     <div class="card-footer">
      <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
    </div>
 </div>
 <% if (!podeEditarBanner) { %>
 <div class="btn-action">
   <a class="btn btn-outline-danger" aria-label="<hl:message key="rotulo.botao.voltar"/>" href="#no-back" onClick="postData('../v3/carregarPrincipal')"><hl:message key="rotulo.botao.voltar"/></a>
 </div>
 <% } %>
<!-- Modal visualizar banner -->
    <div class="modal fade" id="visualizaBanner" tabindex="-1" role="dialog" aria-labelledby="modalTitulo" aria-hidden="true" style="display: none">
        <div class="modal-dialog modal-lg modalBannerPropaganda" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body pb-0 pt-1 mb-3 mt-3 text-center">
                    <img id="imageDiv"  src="../img/view.jsp?nome=banner/metro.jpg" alt='<hl:message key="rotulo.conf.banner.anuncio"/>' title='<hl:message key="rotulo.conf.banner.anuncio"/>'>
                </div>
                <div class="modal-footer pt-0">
                    <div class="btn-action mt-2 mb-0">
                        <a class="btn btn-outline-danger" onclick="fecharModal()" href="#no-back" aria-label="<hl:message key="rotulo.botao.fechar"/>"><hl:message key="rotulo.botao.fechar"/></a>
                    </div>
                </div>
            </div>
        </div>
    </div>
<!-- Modal aguarde -->
<div class="modal fade" id="modalAguarde" data-focus="false" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
 <div class="modal-dialog-upload modal-dialog" role="document">
   <div class="modal-content">
     <div class="modal-body">
       <div class="row">
         <div class="col-md-12 d-flex justify-content-center">
           <img src="../img/loading.gif" class="loading">
         </div>
         <div class="col-md-12">
           <div class="modal-body"><span><hl:message key="mensagem.conf.banner.aguarde"/></span></div>            
         </div>
       </div>
     </div>
   </div>
 </div>
</div>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript" src="../js/watermark.js"></script>
<script type="text/JavaScript">
  var f0 = document.form1;
</script>
<script type="text/JavaScript">

function verificaCombo() {
    var selecao = document.form1.FILTRO_TIPO[document.form1.FILTRO_TIPO.selectedIndex].value;    
    // Desabilita campo de filtro se a selecao for cse, servidor ou orgao
    if (selecao != 07) {
      document.form1.FILTRO.disabled=true;
      // Limpa campo de filtro
      document.form1.FILTRO.value="";
    } else {
      document.form1.FILTRO.disabled=false;
    } 
}

function hideBanner() {
    var divBanner = document.getElementById("watermark");
    var hideButton = document.getElementById("hideButton");
    hideButton.style.visibility = "hidden";
    divBanner.style.visibility = "hidden"; 
}

function visualizar(bannerName) {
    var imgDiv = document.getElementById("imageDiv");
    var imgPath = "../img/view.jsp?nome=banner/" + bannerName;     
    imgDiv.src = imgPath;
    $('#visualizaBanner').modal('show');
}

function fecharModal(){
    $(function () {
        $('#visualizaBanner').modal('hide');
    });
}

function formLoad() {
    f0.FILE1.focus();
}
 
function vf_upload_arquivos() {
    var controles = new Array("FILE1");
    var msgs = new Array ('<%=ApplicationResourcesHelper.getMessage("mensagem.conf.banner.informe.arquivo.upload", responsavel)%>'+'!');
    var csaCodigo = '';
    var complemento = '';

    if (f0.csa_codigo_aux != null) {
       for (i = 0 ; i < f0.csa_codigo_aux.length ; i++) {
          if (f0.csa_codigo_aux.options[i].selected) {
             csaCodigo += complemento;
             csaCodigo += (f0.csa_codigo_aux.options[i].value);
             complemento = ',';
          }
       }
       f0.CSA_CODIGO.value = csaCodigo;
    } 
    
    if (f0.CSA_CODIGO.value == null || f0.CSA_CODIGO.value == '') {
       alert('<%=ApplicationResourcesHelper.getMessage("mensagem.informe.consignataria", responsavel)%>');
       return false;
    }

    var ok = ValidaCampos(controles, msgs);
    if (ok) {
        $('#modalAguarde').modal({
            backdrop: 'static',
            keyboard: false
        });
    }
    return ok;
}

function apagar(arq, path) {    
  var msg = '', j;

  msg = '<%=ApplicationResourcesHelper.getMessage("mensagem.confirmacao.conf.banner.exclusao", responsavel)%> "' + arq + '"?';
  j = '../v3/excluirArquivo?arquivo_nome=' + encodeURIComponent(path) + '&tipo=imagem&subtipo=banner';
  j = j + '&<%=SynchronizerToken.generateToken4URL(request)%>'
  ConfirmaUrl(msg, j);  
}

$("#dialogAutorizacao").on('dialogclose', function(event) {
    if($('#modalAguarde').is(":visible")) {
	    $('#modalAguarde').modal('toggle');
    }
});
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>   
    <jsp:attribute name="javascript">${javascript}</jsp:attribute> 
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>