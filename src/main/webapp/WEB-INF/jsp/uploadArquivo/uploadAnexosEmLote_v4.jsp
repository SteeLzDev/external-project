<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.File" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  boolean exibeCaptcha = (boolean) request.getAttribute("exibeCaptcha");
  boolean exibeCaptchaAvancado = (boolean) request.getAttribute("exibeCaptchaAvancado");
  boolean exibeCaptchaDeficiente = (boolean) request.getAttribute("exibeCaptchaDeficiente");
  String identificadorAdes = (String) request.getAttribute("identificadorAdes");
  String pathDownload = (String) request.getAttribute("pathDownload");
  ArrayList<Object> arquivosDownload = (ArrayList<Object>) request.getAttribute("arquivosDownload");
  int qtdColunas = (int) request.getAttribute("qtdColunas");
%>
<c:set var="title">
  <hl:message key="rotulo.upload.anexo.em.lote.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-upload"></use>
</c:set>
<c:set var="bodyContent">
	<br>
	<br>
	<div class="card">
	    <div class="card-header hasIcon">
	      <span class="card-header-icon"><svg width="26"><use xlink:href="#i-upload"></use></svg></span>
	      <h2 class="card-header-title"><hl:message key="rotulo.upload.arquivo.titulo"/></h2>
	    </div>
	    <div class="card-body">
			<FORM NAME="form1" METHOD="POST" ACTION="../v3/uploadAnexosEmLote?acao=upload&<%=SynchronizerToken.generateToken4URL(request)%>" ENCTYPE="multipart/form-data">
				<input name="FORM" type="hidden" value="form1">
				<div class="row">
					<div class=" col-md-12 form-check mt-2" role="radiogroup" aria-labelledby="iArquivosAnexosIdentificadosPor">
	                	<div class="form-group my-0">
	                    	<span id="iArquivosAnexosIdentificadosPor"><hl:message key="rotulo.upload.arquivo.em.lote.identificador.anexo"/></span>
	                  	</div>
	                  	<div class="form-check mt-2">
							<div class="form-check-inline">
		                  		<input class="form-check-input ml-1" type="radio" name="IDENTIFICADOR_ANEXOS" id="identificadorADE" value="ADE_IDENTIFICADOR" <% if (!TextHelper.isNull(identificadorAdes) &&  identificadorAdes.equals("ADE_IDENTIFICADOR")) {%>checked<% } %> onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);" >
		                    	<label class="form-check-label pr-3" for="identificadorADE">
		                      		<span class="text-nowrap align-text-top"><hl:message key="rotulo.consignacao.identificador"/></span>
		                    	</label>
							</div>
							<div class="form-check form-check-inline">
		                    	<input class="form-check-input ml-1" type="radio" name="IDENTIFICADOR_ANEXOS" id="numeroADE" value="ADE_NUMERO" <% if (!TextHelper.isNull(identificadorAdes) && identificadorAdes.equals("ADE_NUMERO")) {%>checked<% } %> onFocus="SetarEventoMascaraV4(this,'#*100',true);" onBlur="fout(this);ValidaMascaraV4(this);" >
		                    	<label class="form-check-label" for="numeroADE">
		                      		<span class="text-nowrap align-text-top"><hl:message key="rotulo.consignacao.numero"/></span>
		                    	</label>
							</div>
	                  	</div>
	                </div>
	            </div>
 				<div class="row">
            		<div class="form-group col-sm-12">
              			<label for="arquivo"><hl:message key="rotulo.upload.generico.arquivo"/></label>
              			<input type="file" class="form-control" id="FILE1" name="FILE1">
            		</div>
          		</div>

  				 <!-- Captcha padrão -->
        		<% if (exibeCaptcha || exibeCaptchaAvancado || exibeCaptchaDeficiente) { %>
        		<div class="row">
        			<div class="form-group col-sm-5">
    	       			<label for="captcha"><hl:message key="rotulo.upload.arquivo.codigo"/></label>
          	  			<input type="text" class="form-control" id="captcha" name="captcha" placeholder='<hl:message key="mensagem.placeholder.digite.codigo" />'>
          			</div>
          			<div class="form-group col-sm-6">
    	       			<div class="captcha">
    	       			<% if (exibeCaptcha) { %>
    	       				<img name="captcha_img" src="../captcha.jpg?t=<%=DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss")%>" alt='<hl:message key="rotulo.captcha.codigo"/>' title='<hl:message key="rotulo.captcha.codigo"/>'/>
							<a href="#no-back" onclick="reloadCaptcha()"><img src="../img/icones/refresh.png" alt='<hl:message key="rotulo.captcha.novo.codigo"/>' title='<hl:message key="rotulo.captcha.novo.codigo"/>' border="0"/></a>
			                <a href="#no-back" class="btn-i-right pr-1" data-bs-toggle="popover" title="<hl:message key="rotulo.ajuda"/>"
			                   data-bs-content='<hl:message key="mensagem.ajuda.captcha.operacao.imagem.v3"/>'
			                   data-original-title=<hl:message key="rotulo.ajuda" />>
			                   <img src="../img/icones/help.png" alt='<hl:message key="rotulo.ajuda"/>' title='<hl:message key="rotulo.ajuda"/>' border="0">
			                </a>

			            <!-- Captcha avançado (recaptcha) -->
        				<% } else if (exibeCaptchaAvancado) { %>
               				<hl:recaptcha />

   						<!-- Captcha deficiente visual -->
       					<% } else if (exibeCaptchaDeficiente) {%>

                           	<div id="divCaptchaSound" >
                           		<a href="#no-back" onclick="reloadSimpleCaptcha()"><img src="../img/icones/refresh.png" alt='<hl:message key="rotulo.captcha.novo.audio"/>' title='<hl:message key="rotulo.captcha.novo.audio"/>' border="0"/></a></td>
                       			<a href="#no-back" onclick="helpCaptcha5();"><img src="../img/icones/help.png" alt='<hl:message key="rotulo.ajuda"/>' title='<hl:message key="rotulo.ajuda"/>' border="0"/></a></td>
                       		</div>
       					<% } %>
			            </div>
			        </div>
           		</div>
   				<% } %>
       		</form>
       	</div>
   	</div>

   	<div class="btn-action">
    	<a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal')"><hl:message key="rotulo.botao.cancelar"/></a>
    	<a class="btn btn-primary" href="#no-back" onClick="if(vf_upload_arquivos()){f0.submit();} return false;"><hl:message key="rotulo.botao.confirmar"/></a>
  	</div>

    <div class="row">
    	<div class="col-sm-12">
        	<div class="card">
            	<div class="card-header pl-3">
              		<h2 class="card-header-title"><hl:message key="rotulo.upload.arquivo.disponivel"/></h2>
            	</div>
            	<div class="card-body table-responsive p-0">
					<table class="table table-striped table-hover">
						<thead>
							<tr>
								<th scope="col"><hl:message key="rotulo.upload.arquivo.nome"/></th>
								<th scope="col"><hl:message key="rotulo.upload.arquivo.tamanho"/></th>
								<th scope="col"><hl:message key="rotulo.upload.arquivo.data"/></th>
								<th scope="col"><hl:message key="rotulo.acoes.upload.arquivo.acoes"/></th>
              				</tr>
              			</thead>
              			<tbody>
 <%
    if (arquivosDownload == null || arquivosDownload.size() == 0){
 %>
           <td colspan="<%=qtdColunas%>"><hl:message key="mensagem.erro.upload.arquivo.nenhum.encontrado"/></td>
<%
    } else {
      int i = 0;
      Iterator<Object> it = arquivosDownload.iterator();
      while (it.hasNext()) {
        File arquivo = (File)it.next();
        String tam = "";
        if (arquivo.length() > 1024.00) {
          tam = Math.round(arquivo.length() / 1024.00) + " " + ApplicationResourcesHelper.getMessage("rotulo.kilobyte.abreviado", responsavel);
        } else {
          tam = arquivo.length() + " " + ApplicationResourcesHelper.getMessage("rotulo.byte.abreviado", responsavel);
        }
        String data = DateHelper.toDateTimeString(new java.sql.Date(arquivo.lastModified()));
        String nome = arquivo.getPath().substring(pathDownload.length());

        String formato = "text.gif";

        String[] partesNomeArq = nome.split(File.separator);

        nome = java.net.URLEncoder.encode(nome, "UTF-8");
  %>
         <tr>
           <td><%=TextHelper.forHtmlContent(arquivo.getName())%></td>
           <td align="right"><%=TextHelper.forHtmlContent(tam)%>&nbsp;</td>
           <td align="center"><%=TextHelper.forHtmlContent(data)%></td>
           <td>
             <div class="position-relative">
               <a href="#no-back" onClick="javascript:downloadArquivo(encodeURIComponent('<%=TextHelper.forJavaScript(nome)%>')); return false;">
                 <hl:message key="rotulo.acoes.upload.arquivo.download"/>&nbsp;
               </a>
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
                              <td colspan="5"><hl:message key="rotulo.paginacao.titulo.download.arq.upload.anexo.lote"/>
                                <span class="font-italic"> -
                                  <hl:message key="rotulo.paginacao.registros.sem.estilo" arg0="${_paginacaoPrimeiro}" arg1="${_paginacaoUltimo}" arg2="${_paginacaoQtdTotal}"/>
                                </span>
                            </td>
                          </tr>
                        </tfoot>
                    </table>
				</div>
                <div class="card-footer">
                  <%@ include file="../paginador/incluirBarraPaginacao_v4.jsp" %>
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
				 <div class="modal-body"><span><hl:message key="mensagem.upload.em.lote.aguarde"/></span></div>
			   </div>
			 </div>
		   </div>
		 </div>
	   </div>
	  </div>

</c:set>

<c:set var="javascript">
	<script type="text/JavaScript" src="../js/scripts_2810.js"></script>
	<script type="text/JavaScript" src="../js/validacoes.js"></script>
	<script type="text/JavaScript" src="../js/validaform.js"></script>
	<script type="text/JavaScript" src="../js/validalogin.js?<hl:message key="release.tag"/>"></script>
	<script type="text/JavaScript" src="../js/xbdhtml.js"></script>
	<% if (exibeCaptchaAvancado) { %>
	<script src='https://www.google.com/recaptcha/api.js'></script>
	<% } %>
	<script type="text/JavaScript">
		var f0 = document.forms[0];
		document.getElementById('captcha').blur();

		function formLoad() {
		  f0.FILE1.focus();
          <% if (exibeCaptchaDeficiente) {%>
          montaCaptchaSom();
          <% } %>
		}
		function vf_upload_arquivos() {
		  var controles = new Array("FILE1", "IDENTIFICADOR_ANEXOS");
		  var msgs = new Array ('<%=ApplicationResourcesHelper.getMessage("mensagem.aviso.upload.em.lote.selecione.arquivo", responsavel)%>',
				                '<%=ApplicationResourcesHelper.getMessage("mensagem.aviso.upload.em.lote.selecione.identifcador", responsavel)%>');
		  var csaCodigo = '';
		  var complemento = '';

		  var ok = ValidaCampos(controles, msgs)
		  if (ok) {
			  $('#modalAguarde').modal({
					backdrop: 'static',
					keyboard: false
				});
		  }
		  return ok;
		}

		function downloadArquivo(arquivo) {
		    endereco = "";
		    endereco = "../v3/downloadArquivo?arquivo_nome=" + arquivo +"&tipo=anexosEmLote&" + "<%=SynchronizerToken.generateToken4URL(request)%>";
		    postData(endereco,'download');
		}

		window.onload = formLoad;
	</script>
</c:set>

<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>