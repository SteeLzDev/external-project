<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="title">
  <hl:message key="rotulo.markdown.sintaxe.marcacao"/>
</c:set>

<c:set var="imageHeader">
    <use xlink:href="#i-sistema"></use>
</c:set>

<c:set var="bodyContent">
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title"><hl:message key="rotulo.markdown.regras.formatacao"/></h2>
    </div>
    <div class="card-body">
      <div class="row">
        <div class="col-sm-5">
          <div class="row">
            <div class="col-sm text-right">
              <span>**<hl:message key="rotulo.markdown.negrito"/>**</span>
            </div>
          </div>
          <div class="row">
            <div class="col-sm mt-3 text-right">
              <span>_<hl:message key="rotulo.markdown.italico"/>_</span>
            </div>
          </div>
          <div class="row">
            <div class="col-sm mt-3 text-right">
              <span>|<hl:message key="rotulo.markdown.sublinhado"/>| </span>
            </div>
          </div>
          <div class="row">
            <div class="col-sm mt-3 text-right">
              <span>+<hl:message key="rotulo.markdown.fonte.vermelha"/>+</span>
            </div>
          </div>
          <div class="row">
            <div class="col-sm mt-3 text-right">
              <span>++<hl:message key="rotulo.markdown.fonte.amarela"/>++</span>
            </div>
          </div>
          <div class="row">
            <div class="col-sm mt-3 text-right">
              <span>+++<hl:message key="rotulo.markdown.fonte.verde"/>+++ </span>
            </div>
          </div>
          <div class="row">
            <div class="col-sm mt-3 text-right">
              <span>++++<hl:message key="rotulo.markdown.fonte.azul"/>++++</span>
            </div>
          </div>
          <div class="row">
            <div class="col-sm mt-3 text-right">
              <span>((<hl:message key="rotulo.markdown.alinhamento.esq"/>((</span>
            </div>
          </div>
          <div class="row">
            <div class="col-sm mt-3 text-right">
              <span>))<hl:message key="rotulo.markdown.alinhamento.dir"/>))</span>
            </div>
          </div>
          <div class="row">
            <div class="col-sm mt-3 text-right">
              <span>)(<hl:message key="rotulo.markdown.alinhamento.centro"/>)(</span>
            </div>
          </div>
          <div class="row">
            <div class="col-sm mt-1 text-right">
              <span>$!<hl:message key="rotulo.markdown.estilo.titulo"/>!</span>
            </div>
          </div>
          <div class="row">
            <div class="col-sm mt-1 text-right">
              <span>$@<hl:message key="rotulo.markdown.estilo.subtitulo"/>$@</span>
            </div>
          </div>
          <div class="row">
            <div class="col-sm mt-1 text-right">
              <span>$%<hl:message key="rotulo.markdown.estilo.texto"/>$%</span>
            </div>
          </div>
          <div class="row">
            <div class="col-sm mt-1 text-right">
              <span>![<hl:message key="rotulo.markdown.texto.alternativo"/>](<hl:message key="rotulo.markdown.url.imagem"/> "<hl:message key="rotulo.markdown.titulo"/>")</span>
            </div>
          </div>
          <div class="row">
            <div class="col-sm mt-4 text-right">
              <span>#<hl:message key="rotulo.markdown.titulo1"/>#</span>
            </div>
          </div>
          <div class="row">
            <div class="col-sm mt-3 text-right">
              <span>##<hl:message key="rotulo.markdown.titulo2"/>##</span>
            </div>
          </div>
          <div class="row">
            <div class="col-sm mt-4 text-right">
              <span>###<hl:message key="rotulo.markdown.titulo3"/>###</span>
            </div>
          </div>
          <div class="row">
            <div class="col-sm mt-3 text-right">
              <span>*<hl:message key="rotulo.markdown.a"/><br>*<hl:message key="rotulo.markdown.b"/><br>*<hl:message key="rotulo.markdown.c"/>
              </span>
            </div>
          </div>
          <div class="row">
            <div class="col-sm mt-3 text-right">
              <span>1.<hl:message key="rotulo.markdown.a"/><br>2.<hl:message key="rotulo.markdown.b"/><br>3.<hl:message key="rotulo.markdown.c"/>
              </span>
            </div>
          </div>
        </div>
        <div class="col-sm-7">
          <div class="row">
            <div class="col-sm-6">
              <span class="font-weight-bold"><hl:message key="rotulo.markdown.negrito"/></span>
            </div>
          </div>
          <div class="row">
            <div class="col-sm-6 mt-3">
              <span class="font-italic"><hl:message key="rotulo.markdown.italico"/></span>
            </div>
          </div>
          <div class="row">
            <div class="col-sm-6 mt-3">
              <span><u><hl:message key="rotulo.markdown.sublinhado"/></u></span>
            </div>
          </div>
          <div class="row">
            <div class="col-sm-6 mt-3">
              <span class="text-danger"><hl:message key="rotulo.markdown.fonte.vermelha"/></span>
            </div>
          </div>
          <div class="row">
            <div class="col-sm-6 mt-3">
              <span class="text-warning"><hl:message key="rotulo.markdown.fonte.amarela"/></span>
            </div>
          </div>
          <div class="row">
            <div class="col-sm-6 mt-3">
              <span class="text-success"><hl:message key="rotulo.markdown.fonte.verde"/></span>
            </div>
          </div>
          <div class="row">
            <div class="col-sm-6 mt-3">
              <span class="text-primary"><hl:message key="rotulo.markdown.fonte.azul"/></span>
            </div>
          </div>
          <div class="row">
            <div class="col-sm mt-3">
              <span class="text-left"><hl:message key="rotulo.markdown.alinhamento.esq"/></span>
            </div>
          </div>
          <div class="row">
            <div class="col-sm mt-3">
              <span class="text-right"><hl:message key="rotulo.markdown.alinhamento.dir"/></span>
            </div>
          </div>
          <div class="row">
            <div class="col-sm mt-3">
              <span class="text-right"><hl:message key="rotulo.markdown.alinhamento.centro"/></span>
            </div>
          </div>
          <div class="row">
            <div class="col-sm mt-1">
              <span class="estiloTitulo"><hl:message key="rotulo.markdown.estilo.titulo"/></span>
            </div>
          </div>
          <div class="row">
            <div class="col-sm mt-1">
              <span class="estiloSubtitulo"><hl:message key="rotulo.markdown.estilo.subtitulo"/></span>
            </div>
          </div>
          <div class="row">
            <div class="col-sm">
              <span class="estiloTexto"><hl:message key="rotulo.markdown.estilo.texto"/></span>
            </div>
          </div>
          <div class="row mt-2">
            <div class="col-sm">
              <span><hl:message key="rotulo.markdown.imagem"/></span>
            </div>
          </div>
          <div class="row">
            <div class="col-sm-6 mt-2">
              <h1 class="mb-0"><hl:message key="rotulo.markdown.titulo1"/></h1>
            </div>
          </div>
          <div class="row">
            <div class="col-sm-6">
              <h2><hl:message key="rotulo.markdown.titulo2"/></h2>
            </div>
          </div>
          <div class="row">
            <div class="col-sm-6 mt-1">
              <h3><hl:message key="rotulo.markdown.titulo3"/></h3>
            </div>
          </div>
          <div class="row">
            <div class="col-sm-4 mt-1">
              <ul>
                <li><hl:message key="rotulo.markdown.a"/></li>
                <li><hl:message key="rotulo.markdown.b"/></li>
                <li><hl:message key="rotulo.markdown.c"/></li>
              </ul>
            </div>
          </div>
          <div class="row">
            <div class="col-sm-4 mt-1">
              <ol>
                <li><hl:message key="rotulo.markdown.a"/></li>
                <li><hl:message key="rotulo.markdown.b"/></li>
                <li><hl:message key="rotulo.markdown.c"/></li>
              </ol>
            </div>
          </div>
        </div>
      </div>
      <div class="row">
        <fieldset>
          <h3 class="legend">
            <span><hl:message key="rotulo.markdown.regras.basicas"/></span>
          </h3>
          <div class="pl-2">
            <p>
             <hl:message key="mensagem.ajuda.markdown.paragrafos"/>
            </p>
            <div class="row justify-content-sm-center">
              <div class="col-sm-4">
                <hl:message key="rotulo.markdown.esse.codigo"/>:<br>$!<hl:message key="rotulo.texto.sistema.bloco"/> 1<br> <br><hl:message key="rotulo.texto.sistema.bloco"/> 2$!
              </div>
              <div class="col-sm-4">
                <hl:message key="rotulo.markdown.gera"/>:<br>$!<hl:message key="rotulo.texto.sistema.bloco"/> 1<br> <br><hl:message key="rotulo.texto.sistema.bloco"/> 2$!
              </div>
            </div>
            <p><hl:message key="rotulo.markdown.correto.seria"/></p>
            <div class="row justify-content-sm-center">
              <div class="col-sm-4">
                <hl:message key="rotulo.markdown.esse.codigo"/>:<br>$!<hl:message key="rotulo.texto.sistema.bloco"/> 1$!<br> <br>$!<hl:message key="rotulo.texto.sistema.bloco"/> 2$!
              </div>
              <div class="col-sm-4 pb-2">
                <hl:message key="rotulo.markdown.gera"/>:<br> <span class="font-weight-bold"><hl:message key="rotulo.texto.sistema.bloco"/> 1<br> <br><hl:message key="rotulo.texto.sistema.bloco"/> 2
                </span>
              </div>
            </div>
            <div class="legend"></div>
            <p><hl:message key="mensagem.ajuda.markdown.nao.pode.haver.espaco"/></p>
            <div class="row justify-content-sm-center">
              <div class="col-sm-4">
                <p>
                  <hl:message key="rotulo.markdown.esse.codigo"/>:<br>$! <hl:message key="rotulo.texto.sistema.texto"/> 1$!
                </p>
              </div>
              <div class="col-sm-4">
                <p>
                  <hl:message key="rotulo.markdown.gera"/>:<br>$! <hl:message key="rotulo.texto.sistema.texto"/> 1$!
                </p>
              </div>
            </div>
            <p><hl:message key="rotulo.markdown.correto.seria"/></p>
            <div class="row justify-content-sm-center">
              <div class="col-sm-4">
                <p>
                 <hl:message key="rotulo.markdown.esse.codigo"/>:<br>$!<hl:message key="rotulo.texto.sistema.texto"/> 1$!
                </p>
              </div>
              <div class="col-sm-4">
                <p>
                  <hl:message key="rotulo.markdown.gera"/>:<br> <span class="font-weight-bold"><hl:message key="rotulo.texto.sistema.texto"/> 1</span>
                </p>
              </div>
            </div>
          </div>
        </fieldset>
      </div>
    </div>
  </div>
  <div class="btn-action">
    <a class="btn btn-outline-danger mt-2" onClick="postData('<%=com.zetra.econsig.helper.texto.TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>');" value="Cancelar" href="#no-back"><hl:message key="rotulo.botao.voltar"/></a>
  </div>
</c:set>
<c:set var="javascript">
<script type="text/JavaScript">
</script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>