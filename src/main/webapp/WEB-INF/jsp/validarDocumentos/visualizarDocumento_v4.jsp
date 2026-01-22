<%@page import="com.zetra.econsig.helper.web.JspHelper"%>
<%@page import="com.zetra.econsig.helper.seguranca.AcessoSistema"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="/html-lib" prefix="hl"%>

<%
    AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
    boolean pdf = request.getAttribute("pdf") != null;
    boolean imagem = request.getAttribute("imagem") != null;
    boolean audio = request.getAttribute("audio") != null;
    boolean video = request.getAttribute("video") != null;
    String hash = (String) request.getAttribute("hash");
    String nomeArquivo = (String) request.getAttribute("nomeArquivo");
    String adeCodigo = (String) request.getAttribute("adeCodigo");
    String adeData = (String) request.getAttribute("adeData");
    String aadNome = (String) request.getAttribute("aadNome");
%>

<c:set var="title">
  <%=nomeArquivo%>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-sistema"></use>
</c:set>

<c:set var="bodyContent">
  <div class="card">
     <div class="card-header hasIcon pl-3">
       <h3 class="card-header-title"><%=nomeArquivo%></h3>
     </div>
     <div class="card-body">
     <%if(!imagem) {%>
         <a href="#" data-bs-toggle="modal" data-bs-target="#modal"><%=nomeArquivo%></a>
         <div class="modal fade" id="modal" tabindex="-1" role="dialog" aria-labelledby="modalTitulo" aria-hidden="true">
          <div class="modal-dialog modalVisualizarPdf">
           <div class="modal-content">
             <div class="modal-header pb-0">
               <h5 class="modal-title about-title mb-0" id="modalTitulo"><%=nomeArquivo%></h5>
               <button type="button" class="logout mr-2" data-bs-dismiss="modal" onclick="javascript:window.close(); return false;" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
                 <span aria-hidden="true">x</span>
               </button>
             </div>
             <div class="modal-body text-center" id="conteudo">
             </div>
             <div class="modal-footer pt-0">
               <div class="btn-action mt-2 mb-0">
                 <a class="btn btn-outline-danger" data-bs-dismiss="modal" onclick="javascript:window.close(); return false;" aria-label='<hl:message key="rotulo.botao.fechar"/>' href="#" alt="<hl:message key="rotulo.botao.fechar"/>" title="<hl:message key="rotulo.botao.fechar"/>">
                   <hl:message key="rotulo.botao.fechar" />
                   </a>
                 </div>
               </div>
             </div>
           </div>
          </div>
      <%} else{ %>
            <div id="conteudo">
            </div>
      <%} %>
     
     </div>
  </div>
  <div class="btn-action">
      <a class="btn btn-outline-danger"  href="#no-back" onclick="javascript:window.close(); return false;"><hl:message key="rotulo.botao.fechar"/></a>
  </div>
</c:set>
<c:set var="javascript">
  <link rel="stylesheet" type="text/css" href="../viewer/css/viewer.css"/>
  <script  src="../viewer/js/viewer.min.js"></script>
  <script type="text/JavaScript">
    f0 = document.forms[0];

    function loadPdf() {
    	  const tag = document.createElement("object");
    	  tag.setAttribute("name", "pdf");
    	  tag.setAttribute("data", "");
    	  tag.setAttribute("type", "application/pdf");
    	  tag.setAttribute("width", "100%");
    	  tag.setAttribute("height", "650px");

    	  const elementPdf = document.getElementById("conteudo");
    	   elementPdf.appendChild(tag);
         
    	   $("object[name='pdf']").attr('data', '../v3/carregarStream?acao=download&hash=<%=hash%>&_skip_history_=true');
         abrirModal();
    	}

    function loadImg() {
        const tag = document.createElement("img");
    	  tag.setAttribute("name", "imgModal");
    	  tag.setAttribute("class", "card-img-top only");
    	  tag.setAttribute("src", "");
    	  tag.setAttribute("height", "330");
    	  tag.setAttribute("alt", "<%=nomeArquivo%>");
    
    	  const elementImg = document.getElementById("conteudo");
    	  elementImg.appendChild(tag);
    	  
        $("img[name='imgModal']").attr('src', '../v3/carregarStream?acao=downloadImg&adeCodigo=<%=adeCodigo%>&adeData=<%=adeData%>&aadNome=<%=aadNome%>&_skip_history_=true');
        
        const viewer = new Viewer(document.getElementById('conteudo'), {
      	  inline: false,
            backdrop: 'static',
            toolbar: {
          	    zoomIn: true,
          	    zoomOut: true,
          	    oneToOne: true,
          	    rotateLeft: true,
          	    rotateRight: true,
          	    flipHorizontal: true,
          	    flipVertical: true,
          	  },
        	});
        
        viewer.show();
  	}
    
    function loadAudio() {
      const tagAudio = document.createElement("audio");
	  tagAudio.setAttribute("id", "voice");
	  tagAudio.setAttribute("controls", "true");
	  
	  const tagSrc = document.createElement("source");
	  tagSrc.setAttribute("name", "audio");
	  tagSrc.setAttribute("src", "");

	  const elementAudio = document.getElementById("conteudo");
	  elementAudio.appendChild(tagAudio);
	  
	  const elementVoice = document.getElementById("voice");
	  elementVoice.appendChild(tagSrc);
	  
      $("source[name='audio']").attr('src', '../v3/carregarStream?acao=download&hash=<%=hash%>&_skip_history_=true');
      abrirModal();
	}
    
    function loadVideo() {
  	  const tagVideo = document.createElement("video");
	  tagVideo.setAttribute("id", "video");
	  tagVideo.setAttribute("controls", "true");
	  tagVideo.setAttribute("width","700");
	  tagVideo.setAttribute("height","500");
	  
	  const tagSrcVideo = document.createElement("source");
	  tagSrcVideo.setAttribute("name", "video");
	  tagSrcVideo.setAttribute("src", "");

	  const elementVideo = document.getElementById("conteudo");
	  elementVideo.appendChild(tagVideo);
	  
	  const elementVideoTag = document.getElementById("video");
	  elementVideoTag.appendChild(tagSrcVideo);
	  
      $("source[name='video']").attr('src', '../v3/carregarStream?acao=download&hash=<%=hash%>&_skip_history_=true');
      abrirModal();
  	}
    
    function abrirModal(){
    	$('#modal').modal('show'); 
    }
    
    $(document).ready(function() {
    	<%if(pdf){%>
        	loadPdf();
    	<%} else if(imagem){%>
    		loadImg();
		<%} else if(audio){%>
			loadAudio();
        <%} else if(video){%>
        	loadVideo();
        <%}%>
    });
  </script>
</c:set>
<t:ajuda_v4>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:ajuda_v4>