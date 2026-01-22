<%--
* <p>Title: listarComposicaoMargemServidor_v4.jsp</p>
* <p>Description: Página de listagem de composição de margem do servidor.</p>
* <p>Copyright: Copyright (c) 2002-2014</p>
* <p>Company: ZetraSoft Internet Service</p>
* $Author$
* $Revision$
* $Date$
--%>
<%@ page language="java" contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/showfield-lib" prefix="show" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
List<AcessoSistema> listaUsuarios = (List) request.getAttribute("listaUsuarios");
%>
<c:set var="title">
  <hl:message key="rotulo.usuario.autenticado.lista.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <div class="row">
    <div class="col-sm-12 col-ms-12">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.usuario.autenticado.lista.titulo"/></h2>
        </div>
        <div class="card-body table-responsive p-0">
          <table id="dataTables" class="table table-striped table-hover">
              <thead>
                 <tr>
                   <th scope="col"><hl:message key="rotulo.usuario.singular"/></th>
                   <th scope="col"><hl:message key="rotulo.usuario.nome"/></th>
                   <th scope="col"><hl:message key="rotulo.usuario.entidade"/></th>
                   <th scope="col"><hl:message key="rotulo.usuario.ip.acesso"/></th>
                   <th scope="col"><hl:message key="rotulo.usuario.data.ultimo.acesso"/></th>
                   <th scope="col"><hl:message key="rotulo.acoes"/></th>
                 </tr>
               </thead>
               <tbody>
               <%=JspHelper.msgRstVazio((listaUsuarios == null || listaUsuarios.isEmpty()), 6, responsavel)%>
               <%
                  if (!listaUsuarios.isEmpty()) {
                      Iterator<AcessoSistema> it = listaUsuarios.iterator();
                      while (it.hasNext()) {
                          AcessoSistema usuario = it.next();
                          String nomeEntidade = usuario.getNomeEntidade();
                          if (usuario.isSer()) {
                              nomeEntidade = ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel);
                          } else if (usuario.isOrg() || usuario.isCor()) {
                              nomeEntidade = usuario.getNomeEntidadePai() + " - " + usuario.getNomeEntidade();
                          }

                          String papel = "";
                          boolean podeConsultarUsuarioPapel = false;
                          if (usuario.isCse()) {
                              podeConsultarUsuarioPapel = responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSE);
                              papel = AcessoSistema.ENTIDADE_CSE;
                          } else if (usuario.isCsa()) {
                              podeConsultarUsuarioPapel = responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_CSA);
                              papel = AcessoSistema.ENTIDADE_CSA;
                          } else if (usuario.isOrg()) {
                              podeConsultarUsuarioPapel = responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_ORG);
                              papel = AcessoSistema.ENTIDADE_ORG;
                          } else if (usuario.isCor()) {
                              podeConsultarUsuarioPapel = responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_COR);
                              papel = AcessoSistema.ENTIDADE_COR;
                          } else if (usuario.isSup()) {
                              podeConsultarUsuarioPapel = responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_SUP);
                              papel = AcessoSistema.ENTIDADE_SUP;
                          } else if (usuario.isSer()) {
                              podeConsultarUsuarioPapel = responsavel.temPermissao(CodedValues.FUN_CONS_USU_SERVIDORES);
                              papel = AcessoSistema.ENTIDADE_SER;
                          }

                          String linkConsultarUsuario = "";
                          if (usuario.isSer()) {
                              linkConsultarUsuario = "../v3/listarUsuarioServidor" 
                                                   + "?acao=pesquisarServidor" 
                                                   + "&RSE_MATRICULA=" + usuario.getRseMatricula()
                                                   + "&SER_CPF=" + usuario.getSerCpf()
                              ;
                          } else {
                              linkConsultarUsuario = "../v3/listarUsuario" + StringUtils.capitalize(papel.toLowerCase()) 
                                                   + "?acao=listar"
                                                   + "&usu_codigo=" + TextHelper.forJavaScript(usuario.getUsuCodigo())
                                                   + "&tipo=" + TextHelper.forJavaScript(papel)
                                                   + "&codigo=" + TextHelper.forJavaScript(usuario.getCodigoEntidade())
                                                   + "&titulo=" + TextHelper.encode64(usuario.getNomeEntidade())
                              ;
                          }
               %>
                   <tr>
                     <td><%=TextHelper.forHtmlContent(usuario.getUsuLogin())%></td>
                     <td><%=TextHelper.forHtmlContent(usuario.getUsuNome())%></td>
                     <td><%=TextHelper.forHtmlContent(nomeEntidade)%></td>
                     <td><%=TextHelper.forHtmlContent(usuario.getIpUsuario())%></td>
                     <td><%=TextHelper.forHtmlContent(DateHelper.format(usuario.getDataUltimaRequisicao(), LocaleHelper.getDateTimePattern()))%></td>
                     <td>
                       <div class="actions">
                         <div class="dropdown">
                           <a class="dropdown-toggle ico-action" href="#" role="button" id="userMenu" data-bs-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                             <div class="form-inline">
                               <span class="mr-1" data-bs-toggle="tooltip" title="" data-original-title="<hl:message key="rotulo.mais.acoes"/>" aria-label="<hl:message key="rotulo.mais.acoes"/>"> <svg>
                                 <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#i-engrenagem"></use></svg>
                               </span> <hl:message key="rotulo.botao.opcoes"/>
                             </div>
                           </a>
                           <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userMenu">
                             <% if (responsavel.temPermissao(CodedValues.FUN_ENCERRAR_SESSAO_USUARIO)) { %>
                               <a class="dropdown-item" href="#no-back" onClick="postData('../v3/listarUsuariosAutenticados?acao=encerrar&usu_codigo=<%=TextHelper.forJavaScript(usuario.getUsuCodigo())%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.encerrar.sessao"/></a>
                             <% } %>
                             <% if (podeConsultarUsuarioPapel) { %>
                               <a class="dropdown-item" href="#no-back" onClick="postData('<%=linkConsultarUsuario%>&<%=TextHelper.forJavaScript(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.acoes.consultar"/></a>
                             <% } %>
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
             </table>
            </div>
        </div>
      </div>
   </div>
   <div class="btn-action" aria-label='<hl:message key="rotulo.botoes.acao.pagina"/>'>  
      <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL("../v3/carregarPrincipal", request))%>');" id="btnVoltar"><hl:message key="rotulo.botao.voltar"/></a>
   </div>
</c:set>
<c:set var="javascript">
<link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-buttons-dt/css/buttons.dataTables.min.css"/>
<link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-responsive-bs5/css/responsive.bootstrap5.min.css"/>
<link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-bs5/css/dataTables.bootstrap5.min.css"/>
<script  src="../node_modules/jszip/dist/jszip.min.js"></script>
<script  src="../node_modules/pdfmake/build/pdfmake.min.js"></script>
<script  src="../node_modules/pdfmake/build/vfs_fonts.js"></script>
<script  src="../node_modules/datatables.net/js/jquery.dataTables.min.js"></script>
<script  src="../node_modules/datatables.net-bs5/js/dataTables.bootstrap5.min.js"></script>
<script  src="../node_modules/datatables.net-buttons/js/dataTables.buttons.min.js"></script>
<script  src="../node_modules/datatables.net-buttons/js/buttons.colVis.min.js"></script>
<script  src="../node_modules/datatables.net-buttons/js/buttons.html5.min.js"></script>
<script  src="../node_modules/datatables.net-buttons/js/buttons.print.min.js"></script>
<script  src="../node_modules/datatables.net-responsive/js/dataTables.responsive.min.js"></script>
<script  src="../node_modules/moment/min/moment.min.js"></script>
<script  src="../node_modules/datatables.net-plugins/sorting/datetime-moment.js"></script>
<script src="../js/colunaCheckbox.js?<hl:message key="release.tag"/>"></script>
<script type="text/JavaScript">
	$(document).ready(function() {
		$.fn.dataTable.moment( 'DD/MM/YYYY HH:mm:ss' );
		$('#dataTables').DataTable({
		    "paging": true,
		  	"pageLength": 20,
	    	"lengthMenu": [
	          [20, 50, 100, -1],
	          [20, 50, 100, '<hl:message key="mensagem.datatables.all"/>']
	        ],
	        "order": [[ 4, "desc" ]],
	        "pagingType": "simple_numbers",
	        "dom": '<"card-body p-0" <"row pl-0 pr-4" <"col-sm-2 pl-0" B > <"col-sm-6 pl-0" l > <"col-sm-4 pr-0" f >> <"table-responsive" t> > <"card-footer" <"row" <"col-sm-6" i> <"col-sm-6" p >>>',
	        buttons: [
	            'colvis'
	        ],
	        stateSave: true,
	        stateSaveParams: function (settings, data) {
	      	    data.search.search = "";
	      	},
	        language: {
	        		  search:            '_INPUT_',
	        		  searchPlaceholder: '<hl:message key="mensagem.datatables.search.placeholder"/>',
	        		  processing:        '<hl:message key="mensagem.datatables.processing"/>',
	                  loadingRecords:    '<hl:message key="mensagem.datatables.loading"/>',
	                  info:              '<hl:message key="mensagem.datatables.info"/>',
	                  lengthMenu:        '<hl:message key="mensagem.datatables.length.menu"/>',
	                  infoEmpty:         '<hl:message key="mensagem.datatables.info.empty"/>',
	                  infoFiltered:      '<hl:message key="mensagem.datatables.info.filtered"/>',
	                  infoPostFix:       '',
	                  zeroRecords:       '<hl:message key="mensagem.datatables.zero.records"/>',
	                  emptyTable:        '<hl:message key="mensagem.datatables.empty.table"/>',
	                  aria: {
	                      sortAscending: '<hl:message key="mensagem.datatables.aria.sort.ascending"/>',
	                      sortDescending:'<hl:message key="mensagem.datatables.aria.sort.descending"/>'
	                  },
	                  paginate: {
	                    first:         '<hl:message key="mensagem.datatables.paginate.first.padrao.econsig"/>',
	                    previous:      '<hl:message key="mensagem.datatables.paginate.previous.padrao.econsig"/>',
	                    next:          '<hl:message key="mensagem.datatables.paginate.next.padrao.econsig"/>',
	                    last:          '<hl:message key="mensagem.datatables.paginate.last.padrao.econsig"/>'
	                },
	                buttons: {
	                    print :        '<hl:message key="mensagem.datatables.buttons.print"/>',
	                    colvis :       '<hl:message key="mensagem.datatables.buttons.colvis"/>'
	                },
	                decimal: ",",
	              },
	              initComplete: function () {
	                  var btns = $('.dt-button');
	                  btns.addClass('btn btn-primary btn-sm');
	                  btns.removeClass('dt-button');
	              }
        });

        $("#dataTables_filter").addClass('pt-2 px-3');
        $('#dataTables_info').addClass('p-3');
	    $("#dataTables_length").addClass('pt-3');

	});
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>   
