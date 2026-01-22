<%@ tag import="java.util.*"%>
<%@ taglib uri="/html-lib" prefix="hl"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="fl" uri="/function-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="titulo" rtexprvalue="true"  required="true" %>

<%-- Modal para sub acessos --%>
<div class="modal fade" id="divModalSubAcesso" tabindex="-1" role="dialog" aria-hidden="true">
  <div class="modal-dialog modal-dialog-width" role="document">
    <div class="modal-content modal-content-height">
      <div class="modal-header pb-0">
        <span class="modal-title about-title mb-0" id="exampleModalLabel"> ${titulo}
        </span>
        <button type="button" class="logout mr-1 d-print-none" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>'>
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
        <iframe name="iframeSubAcesso" class="iframe-dialog-width"></iframe>
      </div>
      <div class="modal-footer pt-0">
        <div class="btn-action mt-2 mb-0">
          <a class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.fechar"/>' href="#" alt="<hl:message key="rotulo.botao.fechar"/>"
            title="<hl:message key="rotulo.botao.fechar"/>">
            <hl:message key="rotulo.botao.fechar" />
          </a>
        </div>
      </div>
    </div>
  </div>
</div>

<script>
function openModalSubAcesso(url){
  postData(url,'iframeSubAcesso')
  $('#divModalSubAcesso').modal('show');
}
</script>