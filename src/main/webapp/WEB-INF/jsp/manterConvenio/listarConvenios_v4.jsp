<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.CodedValues" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*"%>
<%@ page import="com.zetra.econsig.dto.entidade.*"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="hl" uri="/html-lib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession"/>
<%
  AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
  
  boolean podeEditarCnv = (Boolean) request.getAttribute("podeEditarCnv");
  boolean exigeMotivoOperacao = (Boolean) request.getAttribute("exigeMotivoOperacao");
  boolean utilizaCodVerbRef = (Boolean) request.getAttribute("utilizaCodVerbRef");
  boolean temProcessamentoFerias = (Boolean) request.getAttribute("temProcessamentoFerias");
  boolean temModuloBeneficio = (Boolean) request.getAttribute("temModuloBeneficio");
  boolean primeiro = true;
  boolean exibeBotaoRodape = (boolean) request.getAttribute("exibeBotaoRodape");

  int j = 0;
  
  List<TransferObject> convenios = (List<TransferObject>) request.getAttribute("convenios");
  List<TransferObject> lstOcorrencias = (List<TransferObject>) request.getAttribute("lstOcorrencias");
  
  String csa_codigo = (String) request.getAttribute("csa_codigo");
  String org_codigo = (String) request.getAttribute("org_codigo");
  String svc_codigo = (String) request.getAttribute("svc_codigo");
  String svc_descricao = (String) request.getAttribute("svc_descricao");
  String csa_nome = (String) request.getAttribute("csa_nome");
  String org_nome = (String) request.getAttribute("org_nome");
  String titulo = (String) request.getAttribute("titulo");
  String formAction = "../v3/mantemConvenio?acao=editar&"+SynchronizerToken.generateToken4URL(request);
  String cancel = SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request);
%>
<c:set var="title">
  <%=TextHelper.forHtmlContent(titulo)%>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <c:if test="${podeEditarCnv}">
    <div class="row">
      <div class="col-sm-12 col-md-12 mb-2">
        <div class="float-end">
          <button aria-expanded="false" class="btn btn-primary d-print-none" type="submit" onClick="abrirModal()"><hl:message key="mensagem.servidor.convenio.alterar.para.todos" /></button>
        </div>
      </div>
    </div>
  </c:if>
  <form action="../v3/mantemConvenio?acao=editar&<%=SynchronizerToken.generateToken4URL(request)%>"  method="POST" name="form1">
  <input type="HIDDEN" name="svc_codigo" value="<%=TextHelper.forHtmlAttribute(svc_codigo)%>">
  <input type="HIDDEN" name="blockeds" value="">
  <input type="HIDDEN" name="_skip_history_" value="true">
  <input type="HIDDEN" name="svc_descricao" value="<%=TextHelper.forHtmlAttribute(svc_descricao)%>">
  <c:choose>
    <c:when test="${csa_codigo != null && !csa_codigo.isBlank()}">
      <input type="HIDDEN" name="csa_codigo" value="<%=TextHelper.forHtmlAttribute(csa_codigo)%>">
      <input type="HIDDEN" name="csa_nome" value="<%=TextHelper.forHtmlAttribute(csa_nome)%>">
      <c:if test="${org_codigo != null && !org_codigo.isBlank()}">
        <input id="org_codigo" type="HIDDEN" name="org_codigo" value="<%=TextHelper.forHtmlAttribute(org_codigo)%>">    
      </c:if>
    </c:when>
    <c:otherwise>
      <input type="HIDDEN" name="org_codigo" value="<%=TextHelper.forHtmlAttribute(org_codigo)%>">
      <input type="HIDDEN" name="org_nome" value="<%=TextHelper.forHtmlAttribute(org_nome)%>">
   </c:otherwise>
  </c:choose>
  <input type="HIDDEN" name="operacao" value="editar">
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><%=TextHelper.forHtmlContent(svc_descricao)%> - <%=TextHelper.forHtmlContent(!TextHelper.isNull(csa_nome) ? csa_nome : org_nome)%></h2>
        </div>
        <div class="card-body table-responsive p-0">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
                <th nowrap scope="col" width="3%">
                  <div class="form-check">
                    <c:if test="${podeEditarCnv}">
                      <input type="checkbox" class="form-check-input ml-0" id="checkAll" name="checkAll" onClick="checkUnCheckAll();" data-bs-toggle="tooltip" data-original-title='<hl:message key="rotulo.acoes.selecionar"/>' alt='<hl:message key="rotulo.acoes.selecionar"/>' title='<hl:message key="rotulo.acoes.selecionar"/>'>
                    </c:if>
                  </div>
                </th>
                <c:choose>
                  <c:when test="${csa_codigo != null && !csa_codigo.isBlank()}">
                    <th nowrap><hl:message key="rotulo.orgao.singular"/></th>
                  </c:when>
                  <c:otherwise>
                    <th nowrap><hl:message key="rotulo.consignataria.singular"/></th>
                  </c:otherwise>
                </c:choose>
                <th nowrap><hl:message key="rotulo.codigo.verba.singular"/></th>            
                <c:if test="${utilizaCodVerbRef}"><th nowrap><hl:message key="rotulo.codigo.verba.ref.singular"/></th></c:if>            
                <c:if test="${temProcessamentoFerias}"><th nowrap><hl:message key="rotulo.codigo.verba.ferias.singular"/></th></c:if>
                <c:if test="${podeEditarCnv && temModuloBeneficio}"><th nowrap><hl:message key="rotulo.codigo.verba.dirf.singular"/></th></c:if>
                <c:if test="${podeEditarCnv}"><th nowrap><hl:message key="rotulo.acoes"/></th></c:if>
              </tr>
            </thead>
            <tbody>
            <c:choose>
              <c:when test="${convenios.size() == 0}">          
                <tr valign="baseline">
                  <td colspan="5"><%=!csa_codigo.equals("") ? ApplicationResourcesHelper.getMessage("mensagem.convenio.nenhum.orgao.encontrado", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.convenio.nenhuma.consignataria.encontrada", responsavel)%></td>
                </tr>
              </c:when>
              <c:otherwise>
              <%  String nome = "", codigo = "", scvCodigo = "", cnvCodVerba = "", cnvCodVerbaRef = "", cnvCodVerbaFerias = "", cnvCodVerbaDirf = "", cnvScvCodigo = "", cnvCodigo = "";                
                  CustomTransferObject convenio = null;
                  Iterator<TransferObject> it = convenios.iterator();                
                  while (it.hasNext()) {
                    convenio = (CustomTransferObject)it.next();                  
  
                    if (!csa_codigo.equals("")) {
                      nome = convenio.getAttribute(Columns.EST_IDENTIFICADOR).toString() + " - "
                           + convenio.getAttribute(Columns.ORG_NOME).toString() + " - "
                           + convenio.getAttribute(Columns.ORG_IDENTIFICADOR).toString();          
                      codigo = convenio.getAttribute(Columns.ORG_CODIGO).toString();
                    } else {
                      nome = convenio.getAttribute(Columns.CSA_NOME).toString();
                      codigo = convenio.getAttribute(Columns.CSA_CODIGO).toString();
                    }
  
                    scvCodigo = convenio.getAttribute("STATUS").toString();
                    cnvCodigo = (convenio.getAttribute(Columns.CNV_CODIGO) != null) ? convenio.getAttribute(Columns.CNV_CODIGO).toString() : "";
                    cnvCodVerba = (convenio.getAttribute(Columns.CNV_COD_VERBA) != null) ? convenio.getAttribute(Columns.CNV_COD_VERBA).toString() : "";
                    cnvCodVerbaRef = (convenio.getAttribute(Columns.CNV_COD_VERBA_REF) != null) ? convenio.getAttribute(Columns.CNV_COD_VERBA_REF).toString() : "";
                    cnvCodVerbaFerias = (convenio.getAttribute(Columns.CNV_COD_VERBA_FERIAS) != null) ? convenio.getAttribute(Columns.CNV_COD_VERBA_FERIAS).toString() : "";
                    cnvCodVerbaDirf = (convenio.getAttribute(Columns.CNV_COD_VERBA_DIRF) != null) ? convenio.getAttribute(Columns.CNV_COD_VERBA_DIRF).toString() : "";
                    cnvScvCodigo = (convenio.getAttribute(Columns.CNV_SCV_CODIGO) != null) ? convenio.getAttribute(Columns.CNV_SCV_CODIGO).toString() : "";
                    if (primeiro){
                        if (scvCodigo.equals(CodedValues.SCV_ATIVO)){
                           primeiro = false;
                        }
                    }
              %>
              <tr class='refLinha'> 
                 <td nowrap class="ocultarColuna" aria-label='<hl:message key="rotulo.acoes.selecionar"/>' title='<hl:message key="rotulo.acoes.selecionar"/>' data-bs-toggle="tooltip" data-original-title='<hl:message key="rotulo.acoes.selecionar"/>'>
                    <div class="form-check">
                      <input type="checkbox" class="form-check-input ml-0" value="<%=TextHelper.forHtmlAttribute(codigo)%>" 
                      id="chk_<%=TextHelper.forHtmlAttribute(codigo)%>" name="selecionarCheckBox"
                      onChange="disableFields(this, f0.cv_<%=TextHelper.forJavaScript(codigo)%>, f0.ref_<%=TextHelper.forJavaScript(codigo)%>, f0.ferias_<%=TextHelper.forJavaScript(codigo)%>, f0.dirf_<%=TextHelper.forJavaScript(codigo)%>);"
                      data-exibe-msg2="0" data-usa-link2="0" <%=(String)(scvCodigo.equals(CodedValues.SCV_ATIVO) ? "CHECKED" : "")%> <%=(String)(!podeEditarCnv ? "DISABLED" : "")%>>
                    </div>
                 </td>
                 <td class="selecionarColuna selecionarLinha">&nbsp;<%=TextHelper.forHtmlContent(nome)%>
                   <hl:htmlinput name="<%="colPrdNumero" + codigo%>"   type="hidden" di="<%="colPrdNumero" + codigo%>"   value="<%=TextHelper.forHtmlAttribute(codigo)%>" />
                    <c:choose>
                      <c:when test="${csa_codigo != null && !csa_codigo.isBlank()}">
                        <input id="<%=TextHelper.forHtmlAttribute("org_" + codigo)%>" type="HIDDEN" name=<%="org_" + codigo%> value="<%=TextHelper.forHtmlAttribute(nome)%>">
                      </c:when>
                      <c:otherwise>                
                        <input id="<%=TextHelper.forHtmlAttribute("csa_" + codigo)%>" type="HIDDEN" name=<%="csa_" + codigo%> value="<%=TextHelper.forHtmlAttribute(nome)%>">            
                      </c:otherwise>          
                    </c:choose>
                    <input id="<%=TextHelper.forHtmlAttribute("cnv_codigo_" + codigo)%>" type="HIDDEN" name="<%=TextHelper.forHtmlAttribute("cnv_codigo_" + codigo)%>" value="<%=TextHelper.forHtmlAttribute(cnvCodigo)%>">      
                  </td>
                  <td nowrap><hl:htmlinput name="<%=TextHelper.forHtmlAttribute("cv_" + codigo)%>"
                                                       di="<%=TextHelper.forHtmlAttribute("cv_" + codigo)%>"
                                                       type="text"
                                                       classe="form-control"
                                                       size="30"
                                                       mask="#*32"
                                                       value="<%=TextHelper.forHtmlAttribute(cnvCodVerba)%>"
                                                       others="<%=TextHelper.forHtmlAttribute((!podeEditarCnv || (cnvScvCodigo.equals(CodedValues.SCV_INATIVO) || cnvScvCodigo.equals("")))? "disabled" : "")%>"
                                                       />
                 </td>
                 <c:if test="${utilizaCodVerbRef}">                 
                   <td nowrap><hl:htmlinput name="<%=TextHelper.forHtmlAttribute("ref_" + codigo)%>"
                                                         di="<%=TextHelper.forHtmlAttribute("ref_" + codigo)%>"
                                                         type="text"
                                                         classe="form-control"
                                                         size="30"
                                                         mask="#*40"
                                                         value="<%=TextHelper.forHtmlAttribute(cnvCodVerbaRef)%>"
                                                         others="<%=TextHelper.forHtmlAttribute((!podeEditarCnv || (cnvScvCodigo.equals(CodedValues.SCV_INATIVO) || cnvScvCodigo.equals("")))? "disabled" : "")%>"
                                                         />      
                   </td>
                 </c:if>                 
                 <c:if test="${temProcessamentoFerias}">
                   <td nowrap><hl:htmlinput name="<%=TextHelper.forHtmlAttribute("ferias_" + codigo)%>"
                                                         di="<%=TextHelper.forHtmlAttribute("ferias_" + codigo)%>"
                                                         type="text"
                                                         classe="form-control"
                                                         size="30"
                                                         mask="#*40"
                                                         value="<%=TextHelper.forHtmlAttribute(cnvCodVerbaFerias)%>"
                                                         others="<%=TextHelper.forHtmlAttribute((!podeEditarCnv || (cnvScvCodigo.equals(CodedValues.SCV_INATIVO) || cnvScvCodigo.equals("")))? "disabled" : "")%>"
                                                         />      
                   </td>
                 </c:if>                 
                 <c:if test="${podeEditarCnv && temModuloBeneficio}">
                   <td nowrap><hl:htmlinput name="<%=TextHelper.forHtmlAttribute("dirf_" + codigo)%>"
                                                         di="<%=TextHelper.forHtmlAttribute("dirf_" + codigo)%>"
                                                         type="text"
                                                         classe="form-control"
                                                         size="30"
                                                         mask="#*40"
                                                         value="<%=TextHelper.forHtmlAttribute(cnvCodVerbaDirf)%>"
                                                         others="<%=TextHelper.forHtmlAttribute((!podeEditarCnv || (cnvScvCodigo.equals(CodedValues.SCV_INATIVO) || cnvScvCodigo.equals("")))? "disabled" : "")%>"
                                                         />      
                   </td>
                </c:if>
                <c:if test="${podeEditarCnv}">
                  <td class="selecionarColuna selecionarLinha">
                    <a href="#"  
                    onClick="enableFields(f0.chk_<%=TextHelper.forHtmlAttribute(codigo)%>, f0.cv_<%=TextHelper.forJavaScript(codigo)%>, f0.ref_<%=TextHelper.forJavaScript(codigo)%>, f0.ferias_<%=TextHelper.forJavaScript(codigo)%>, f0.dirf_<%=TextHelper.forJavaScript(codigo)%>);"
                    ><hl:message key="rotulo.acoes.selecionar"/></a>                      
                  </td>
                </c:if>
              </tr>
               <% } %>
               <input type='hidden' name="tipoCodigo" id="tipoCodigo" value="<%=TextHelper.forHtmlAttribute(!csa_codigo.equals("") ? "ORG_CODIGO" : "CSA_CODIGO")%>">
              </c:otherwise>
            </c:choose>
            </tbody>
          </table>
        </div>
      </div>
  
      <c:if test="${podeEditarCnv && exigeMotivoOperacao}">
        <div class="card">
          <div class="card-header">
            <h2 class="card-header-title"><hl:message key="rotulo.motivo.operacao.singular" /></h2>
          </div>
          <div class="card-body">
            <div class="row">
              <div class="form-group col-sm-12">
                <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="" operacaoConvenio="true" inputSizeCSS="col-sm-12" />
              </div>                
            </div>
          </div>
        </div>
      </c:if>
      <c:choose>
        <c:when test="${podeEditarCnv}">
          <div id="actions" class="btn-action">            
            <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(cancel)%>');"><hl:message key="rotulo.botao.voltar"/></a>            
            <a href="#" onClick="return vf_cadastro_cnv();" class="btn btn-primary"><hl:message key="rotulo.botao.salvar"/></a>  
          </div>
       </c:when>
       <c:otherwise>
         <div id="actions" class="btn-action">
           <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(cancel)%>');"><hl:message key="rotulo.botao.voltar"/></a>
         </div>
       </c:otherwise>
     </c:choose>
  
    <c:if test="${lstOcorrencias != null && !lstOcorrencias.isEmpty()}"> 
      <div class="card">
        <div class="card-header">
          <h2 class="card-header-title"><hl:message key="rotulo.ocorrencia.cnv.titulo"/></h2>
        </div> 
        <div class="card-body table-responsive p-0">
          <table class="table table-striped table-hover">
            <thead>
              <tr>
              <th nowrap><hl:message key="rotulo.ocorrencia.cnv.data"/></th>
              <th nowrap><hl:message key="rotulo.ocorrencia.cnv.convenio"/></th>
              <th nowrap><hl:message key="rotulo.ocorrencia.cnv.responsavel"/></th>
              <th nowrap><hl:message key="rotulo.ocorrencia.cnv.tipo"/></th>
              <th nowrap><hl:message key="rotulo.ocorrencia.cnv.descricao"/></th>
              <th nowrap><hl:message key="rotulo.ocorrencia.cnv.ip.acesso"/></th>            
              </tr>
            </thead>
            <tbody>
        <%
        j = 0;
        Iterator<TransferObject> itHistorico = lstOcorrencias.iterator();
        while (itHistorico.hasNext()) { 
            TransferObject cto = itHistorico.next();
            String ocoData = DateHelper.toDateTimeString((Date) cto.getAttribute(Columns.OCO_DATA));
  
            String loginOcoResponsavel = cto.getAttribute(Columns.USU_LOGIN) != null ? cto.getAttribute(Columns.USU_LOGIN).toString() : "";
            String ocoResponsavel = (loginOcoResponsavel.equalsIgnoreCase((String) cto.getAttribute(Columns.USU_CODIGO)) && cto.getAttribute(Columns.USU_TIPO_BLOQ) != null) ? 
                                    cto.getAttribute(Columns.USU_TIPO_BLOQ).toString() + "(*)" : loginOcoResponsavel;
            String ocoTipo = cto.getAttribute(Columns.TOC_DESCRICAO).toString();
            String ocoObs = cto.getAttribute(Columns.OCO_OBS).toString();
            String ocoIpAcesso = cto.getAttribute(Columns.OCO_IP_ACESSO) != null ? cto.getAttribute(Columns.OCO_IP_ACESSO).toString() : "";
            String tmoDescricao = cto.getAttribute(Columns.TMO_DESCRICAO) != null ? cto.getAttribute(Columns.TMO_DESCRICAO).toString() : "";
            if (!TextHelper.isNull(tmoDescricao)) {
                ocoObs += " " + ApplicationResourcesHelper.getMessage("rotulo.motivo.arg0", responsavel, tmoDescricao);
            }
  
            String svcDescricao = cto.getAttribute(Columns.SVC_DESCRICAO) != null ? cto.getAttribute(Columns.SVC_DESCRICAO).toString() : "";
            String orgNome = cto.getAttribute(Columns.ORG_NOME) != null ? cto.getAttribute(Columns.ORG_NOME).toString() : "";
            String csaNome = cto.getAttribute(Columns.CSA_NOME) != null ? cto.getAttribute(Columns.CSA_NOME).toString() : "";
            String cnvCodVerba = cto.getAttribute(Columns.CNV_COD_VERBA) != null ? cto.getAttribute(Columns.CNV_COD_VERBA).toString() : "";
            String convenio = ApplicationResourcesHelper.getMessage("rotulo.ocorrencia.cnv.convenio.valor", responsavel, svcDescricao, orgNome, csaNome, cnvCodVerba);
        %>
          <tr class="<%=(String)(j++%2==0?"Li":"Lp")%>">
            <td><%=TextHelper.forHtmlContent(ocoData)%></td>
            <td><%=TextHelper.forHtmlContentComTags(convenio)%></td>
            <td><%=TextHelper.forHtmlContent(ocoResponsavel)%></td>
            <td><%=TextHelper.forHtmlContent(ocoTipo)%></td>
            <td><%=TextHelper.forHtmlContent(ocoObs)%></td>
            <td><%=TextHelper.forHtmlContent(ocoIpAcesso)%></td>
          </tr>
        <%  } %>
            <tfoot>
              <tr>
                <td colspan="5"><%=ApplicationResourcesHelper.getMessage("rotulo.folha.lista.convenios.titulo", responsavel ) + " - " %>
                  <span class="font-italic">
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
    </c:if>
  </form>
   <!-- Modal: copiar para todos -->
   <div class="modal fade" id="copiarParaTodos" tabindex="-1" role="dialog" aria-labelledby="modalTitulo" aria-hidden="true" style="display: none;">
    <div class="modal-dialog modalTermoUso" role="document">
      <div class="modal-content">
        <form id='formCPT'>
        <div class="modal-header pb-0">
          <h5 class="modal-title about-title mb-0" id="modalTitulo"><hl:message key="mensagem.servidor.convenio.alterar.para.todos"/></h5>
          <button type="button" class="logout mr-2" data-bs-dismiss="modal" aria-label="Fechar">
            <span aria-hidden="true">x</span>
          </button>
        </div>
        <div class="modal-body">
          <span id="CopiarParaTodosModal">
            <label for='cv'><hl:message key="rotulo.codigo.verba.singular"/></label><hl:htmlinput name="cv" di="cv" type="text" classe="form-control" size="30" mask="#*32"/><br>
            <c:if test="${utilizaCodVerbRef}"><label for='ref'><hl:message key="rotulo.codigo.verba.ref.singular"/></label><hl:htmlinput name="ref" di="ref" type="text" classe="form-control" size="30" mask="#*40"/><br></c:if>
            <c:if test="${temProcessamentoFerias}"><label for='ferias'><hl:message key="rotulo.codigo.verba.ferias.singular"/></label><hl:htmlinput name="ferias" di="ferias" type="text" classe="form-control" size="30" mask="#*40"/><br></c:if>
            <c:if test="${podeEditarCnv && temModuloBeneficio}"><label for='dirf'><hl:message key="rotulo.codigo.verba.dirf.singular"/></label><hl:htmlinput name="dirf" di="dirf" type="text" classe="form-control" size="30" mask="#*40" /></c:if>
          </span>
        </div>
        <div class="modal-footer pt-0">
          <div class="btn-action mt-2 mb-0">
            <a href="#" class="btn btn-outline-danger" data-bs-dismiss="modal" aria-label='<hl:message key="rotulo.botao.cancelar"/>' title="<hl:message key='rotulo.botao.cancelar'/>">
              <hl:message key="rotulo.botao.cancelar" />
            </a>
            <a href="#" class="btn btn-primary" onClick="copiaCodVerba(formCPT.cv, formCPT.ref, formCPT.ferias, formCPT.dirf);" 
               aria-label="<hl:message key='mensagem.servidor.convenio.alterar.para.todos'/>" title="<hl:message key='mensagem.servidor.convenio.alterar.para.todos'/>">
               <hl:message key="mensagem.servidor.convenio.alterar.para.todos" />
            </a>
          </div>
        </div>
        </form>
      </div>
    </div>
  </div>
  <%if (exibeBotaoRodape) { %>
	<div id="btns">
	  <a id="page-up" onclick="up()">
        <svg xmlns="http://www.w3.org/2000/svg" width="147.344" height="147.344" viewBox="0 0 147.344 147.344">
		  <path id="União_3" data-name="União 3" d="M-20,60.672a73.672,73.672,0,1,1,73.672,73.672A73.66,73.66,0,0,1-20,60.672ZM9.61,16.61a62.252,62.252,0,0,0,0,88.124,62.252,62.252,0,0,0,88.124,0,62.252,62.252,0,0,0,0-88.124,62.252,62.252,0,0,0-88.124,0ZM49.174,88.087,15.278,54.3a6.85,6.85,0,0,1,9.67-9.706L53.672,73.6,82.362,45.336A6.825,6.825,0,1,1,92,55.005L58.6,88.3a6.827,6.827,0,0,1-9.421-.212Z" transform="translate(20 13)"/>
		</svg>              
	  </a>
	  <a id="page-down" onclick="down()">
        <svg xmlns="http://www.w3.org/2000/svg" width="147.344" height="147.344" viewBox="0 0 147.344 147.344">
		  <path id="União_3" data-name="União 3" d="M-20,60.672a73.672,73.672,0,1,1,73.672,73.672A73.66,73.66,0,0,1-20,60.672ZM9.61,16.61a62.252,62.252,0,0,0,0,88.124,62.252,62.252,0,0,0,88.124,0,62.252,62.252,0,0,0,0-88.124,62.252,62.252,0,0,0-88.124,0ZM49.174,88.087,15.278,54.3a6.85,6.85,0,0,1,9.67-9.706L53.672,73.6,82.362,45.336A6.825,6.825,0,1,1,92,55.005L58.6,88.3a6.827,6.827,0,0,1-9.421-.212Z" transform="translate(20 13)"/>
		</svg>
	  </a>
	  <a id="page-actions" onclick="toActionBtns()">
		<svg xmlns="http://www.w3.org/2000/svg" width="145.344" height="145.344" viewBox="0 0 145.344 145.344">
		  <path id="União_1" data-name="União 1" d="M-20,59.672a72.672,72.672,0,1,1,72.671,72.672A72.671,72.671,0,0,1-20,59.672Zm10.164,0A62.508,62.508,0,1,0,52.672-2.836,62.579,62.579,0,0,0-9.836,59.672Zm82.6,40.182H24.545A12.069,12.069,0,0,1,12.49,87.8V31.544A12.069,12.069,0,0,1,24.545,19.49h44.2a4.014,4.014,0,0,1,2.841,1.177L91.678,40.757A4.019,4.019,0,0,1,92.855,43.6V87.8A12.069,12.069,0,0,1,80.8,99.854Zm0-40.182a4.018,4.018,0,0,1,4.019,4.018V91.817H80.8A4.023,4.023,0,0,0,84.818,87.8V45.263L67.081,27.526H36.6V39.58H64.727a4.019,4.019,0,0,1,0,8.037H32.581A4.018,4.018,0,0,1,28.563,43.6V27.526H24.545a4.023,4.023,0,0,0-4.018,4.019V87.8a4.023,4.023,0,0,0,4.018,4.018h4.019V63.689a4.018,4.018,0,0,1,4.018-4.018ZM36.6,91.817H68.745V67.708H36.6Z" transform="translate(20 13)"/>
		</svg>
	  </a>
	</div>
  <% } %>      
</c:set>
<c:set var="javascript">
<% if (exigeMotivoOperacao) { %>
  <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="" scriptOnly="true" />
<% } %>
  <script src="../js/colunaCheckboxInput.js?<hl:message key="release.tag"/>"></script>
  <script type="text/JavaScript">
  var f0 = document.form1;
  window.onload = formLoad;
  
  function formLoad() {
	  <%if (primeiro == true){%>
	      ocultarColuna();
    <%}%>
  }
  //habilita campo de valor realizado quando linha da tabela é clicada e aparece coluna de checkbox pela 1a vez
  $(".selecionarLinha").click(function() {
  	var prdNumero = $(this).parent().find('input[type="hidden"]');		
  });

  function checkUnCheckAll() {
      if (f0.checkAll.checked) {
    	  checkAll(f0, 'selecionarCheckBox');
    	  checkAllCampos(f0);
      }	else {
    	  uncheckAll(f0, 'selecionarCheckBox');
    	  uncheckAllCampos(f0);
      }    
  }

  function abrirModal(){
	   $('#copiarParaTodos').modal('show');
	}

  function checkAllCampos(form) {
	  checkObj = document.forms[0].selecionarCheckBox;
	  for (i=0; i<checkObj.length; ++i) {
	     checkObj[i].checked = true;
	     document.getElementById('cv_' + checkObj[i].value).disabled = false;
	     <% if (utilizaCodVerbRef) { %>document.getElementById('ref_' + checkObj[i].value).disabled = false;<% } %>
	     <% if (temProcessamentoFerias) { %>document.getElementById('ferias_' + checkObj[i].value).disabled = false;<% } %>
	     <% if (podeEditarCnv && temModuloBeneficio) {%>document.getElementById('dirf_' + checkObj[i].value).disabled = false;<% } %>
	  }
	}

	function uncheckAllCampos(form) {
		checkObj = document.forms[0].selecionarCheckBox;
	  for (i=0; i<checkObj.length; ++i) {
	     checkObj[i].checked = false;
	     document.getElementById('cv_' + checkObj[i].value).disabled = true;
	     <% if(utilizaCodVerbRef) { %>document.getElementById('ref_' + checkObj[i].value).disabled = true;<% } %>
	     <% if(temProcessamentoFerias) { %>document.getElementById('ferias_' + checkObj[i].value).disabled = true;<% } %>
	     <% if (podeEditarCnv && temModuloBeneficio) {%>document.getElementById('dirf_' + checkObj[i].value).disabled = true;<% } %>
	  }
	}  
  
  <% if (!(!csa_codigo.equals("") && !org_codigo.equals(""))) {%>
  
  function copiaCodVerba(verba, verbaRef, verbaFerias, verbaDirf) {
    for (i=0; i<f0.elements.length; i++) {
      var e = f0.elements[i];
      if (e.name.indexOf('cv_') == 0 && e.disabled == false) {
        e.value = verba.value;
      }
      if (e.name.indexOf('ref_') == 0 && e.disabled == false) {
        e.value = verbaRef.value;
      }
      if (e.name.indexOf('ferias_') == 0 && e.disabled == false) {
        e.value = verbaFerias.value;
      }
      if (e.name.indexOf('dirf_') == 0 && e.disabled == false) {
      e.value = verbaDirf.value;
      }
    }
    $('#copiarParaTodos').modal('hide');
  }
  <% } %>
  function vf_cadastro_cnv() {
	  checkObj = document.forms[0].selecionarCheckBox;
    for (i=0; i<checkObj.length; ++i) {
      if (checkObj[i].checked) {
        var verbaCod = document.getElementById('cv_' + checkObj[i].value); 
        if (verbaCod.value == "") {
          var orgEl = document.getElementById('org_' + checkObj[i].value);
          if (orgEl != null) {
            alert('<hl:message key="mensagem.convenio.preencher.verba.orgao"/>'.replace('{0}', orgEl.value));
          } else {
            var csaEl = document.getElementById('csa_' + checkObj[i].value);
            alert('<hl:message key="mensagem.convenio.preencher.verba.csa"/>'.replace('{0}', csaEl.value));
          }
          return;
        } else {
        <% if (temProcessamentoFerias) { %>
            var verbaFerias = document.getElementById('ferias_' + checkObj[i].value);
            if (verbaFerias.value == verbaCod.value) {
              alert('<hl:message key="mensagem.convenio.verba.ferias.igual.verba.normal"/>');
              verbaFerias.focus();
              return;
            }
          <% } %>
        }
      }
    }
    enableAll();
    f0.submit();
  }
  
  function enableFields(codigo, verba, verbaRef, verbaFerias, verbaDirf){
	  if (verba.disabled == true && !codigo.checked){
	      verba.disabled = false;
	      if (verbaRef != null) {
	        verbaRef.disabled = false;
	      }
	      if (verbaFerias != null) {
	        verbaFerias.disabled = false;
	      }
	      if (verbaDirf != null) {
	        verbaDirf.disabled = false;
	      }		  
	  } else {
		  if (verba.disabled == false && codigo.checked){
	      verba.disabled = true;
	      if (verbaRef != null) {
	        verbaRef.disabled = true;
	      }
	      if (verbaFerias != null) {
	        verbaFerias.disabled = true;
	      }
	      if (verbaDirf != null) {
	        verbaDirf.disabled = true;
	      }		  
		  }
	  }
  }
  
  function disableFields(checkbox, verba, verbaRef, verbaFerias, verbaDirf) {
    if (!checkbox.checked) {
      verba.disabled = true;
    if (verbaRef != null) {
        verbaRef.disabled = true;
      }
      if (verbaFerias != null) {
        verbaFerias.disabled = true;
      }
      if (verbaDirf != null) {
        verbaDirf.disabled = true;
      }
    } else {
      verba.disabled = false;
    if (verbaRef != null) {
        verbaRef.disabled = false;
      }
      if (verbaFerias != null) {
        verbaFerias.disabled = false;
      }
      if (verbaDirf != null) {
        verbaDirf.disabled = false;
      }
    }
  }  
  </script>
  <script>
  		let btnDown = document.querySelector('#btns');
	const pageActions = document.querySelector('#page-actions');
	const pageSize = document.body.scrollHeight;
	
	function up(){
		window.scrollTo({
			top: 0,
			behavior: "smooth",
		});
	}
	
	function down(){
		let toDown = document.body.scrollHeight;
		window.scrollBy({
			top: toDown,
			behavior: "smooth",
		});
	}

	function toActionBtns(){
		let save = document.querySelector('#actions').getBoundingClientRect().top;
		window.scrollBy({
			top: save,
			behavior: "smooth",
		});
	}
	
	function btnTab(){
	    let scrollSize = document.documentElement.scrollTop;
	    
	    if(scrollSize >= 300){
		    btnDown.classList.add('btns-active');    
	    } else {
		    btnDown.classList.remove('btns-active');
	    }
	}
	

	window.addEventListener('scroll', btnTab);
  </script>
</c:set>
<t:page_v4>
    <jsp:attribute name="header">${title}</jsp:attribute>
    <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
    <jsp:attribute name="javascript">${javascript}</jsp:attribute>
    <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>