<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.LocaleHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.TransferObject"%>
<%@ page import="com.zetra.econsig.dto.CustomTransferObject"%>
<%@ taglib prefix="hl" uri="/html-lib"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

boolean rrsApenasPraCsaComAde = (boolean) request.getAttribute("rrsApenasPraCsaComAde");
boolean podeCriarReclamacao = (boolean) request.getAttribute("podeCriarReclamacao");

String csaCodigo = (String) request.getAttribute("csaCodigo");
String filtroDataIni = (String) request.getAttribute("filtroDataIni");
String filtroDataFim = (String) request.getAttribute("filtroDataFim");
String serCodigo = (String) request.getAttribute("serCodigo");

List<String> motivosCodigos = (List<String>) request.getAttribute("motivosCodigos");
List<TransferObject> reclamacoes = (List<TransferObject>) request.getAttribute("reclamacoes");
List<TransferObject> tiposReclamacao = (List<TransferObject>) request.getAttribute("tiposReclamacao");
List<TransferObject> consignatarias = (List<TransferObject>) request.getAttribute("consignatarias");
%>

<c:set var="title">
  <hl:message key="<%=TextHelper.forHtml("rotulo.listar.reclamacao.titulo")%>"/>
</c:set>

<c:set var="imageHeader">
  <use xlink:href="#i-sistema"></use>
</c:set>

<c:set var="bodyContent">
  <div class="btn-action">
    <% if (podeCriarReclamacao) { %>
      <a class="btn btn-primary" href="#no-back" onClick="postData('../v3/editarReclamacao?acao=editarReclamacao&<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.generateToken4URL(request))%>')">Nova reclamação</a>
    <% } %>
  </div>
  <div class="card">
    <div class="card-body p-0">
      <form action="../v3/editarReclamacao?acao=iniciar&<%=SynchronizerToken.generateToken4URL(request)%>" method="post" name="form1">
        <div class="opcoes-avancadas">
          <a class="opcoes-avancadas-head collapsed" href="#faq1" data-bs-toggle="collapse" aria-expanded="false" aria-controls="faq1">
            <hl:message key="rotulo.pesquisa.avancada"/>
          </a>
          <div class="card-body ml-4 collapse" id="faq1" >
            <div class="row">
              <div class="form-group col-sm-12 col-md-6">
                <label for="consignataria">Consignatária</label>
                <select name="CSA_CODIGO" id="CSA_CODIGO" class="form-control form-select" id="consignataria">
                  <option value="" SELECTED><hl:message key="rotulo.campo.todas"/></option>
                  <%
                   Iterator it = consignatarias.iterator();
                   CustomTransferObject csa = null;
                   String csaCodigo2, csaNome, csaId;
                   String selected = csaCodigo;
                   while (it.hasNext()) {
                     csa = (CustomTransferObject)it.next();
                     csaCodigo2 = (String)csa.getAttribute(Columns.CSA_CODIGO);
                     csaId = (String) csa.getAttribute(Columns.CSA_IDENTIFICADOR);
                     csaNome = (String) csa.getAttribute(Columns.CSA_NOME);
                     if (csaNome.length() > 50){
                        csaNome = csaNome.substring(0, 47) + "...";
                   }
                  %>
                    <option value="<%=TextHelper.forHtmlAttribute(csaCodigo2)%>" <%=(String)(selected.equals(csaCodigo2) ? "SELECTED" : "")%>><%=TextHelper.forHtmlContent(csaNome)%> - <%=TextHelper.forHtmlContent(csaId)%></option>
                  <% } %>
                </select>
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm-12 col-md-6" role="checkBox">
                <span id="tipoMotivoReclamacao" class="mb-2">Tipo de motivo de Reclamação</span>
                <div class="row" role="group" aria-labelledby="tipoMotivoReclamacao">
                  <div class="form-check" aria-labelledby="tipoMotivoReclamacao">
                    <%
                      CustomTransferObject tipo = null;
                      String nome = "", codigo = "", scv_codigo = "", marcado = "";
                  
                      Iterator ittr = tiposReclamacao.iterator();
                      while (ittr.hasNext()) {
                          tipo = (CustomTransferObject)ittr.next();
                          nome = tipo.getAttribute(Columns.TMR_DESCRICAO).toString();
                          codigo = tipo.getAttribute(Columns.TMR_CODIGO).toString();
                    %>
                    <div class="col-sm-12 col-md-6">
                      <input class="form-check-input ml-1" name="TMR_CODIGO" id="<%=TextHelper.forHtmlAttribute(nome)%>-<%=TextHelper.forHtmlAttribute(codigo)%>" type="checkbox" value="<%=TextHelper.forHtmlAttribute(codigo)%>" <%=(motivosCodigos != null && motivosCodigos.contains(codigo.toString()) ? "checked" : "")%>>
                      <label class="form-check-label ml-1 ml-1 ml-1 ml-1" for="<%=TextHelper.forHtmlAttribute(nome)%>-<%=TextHelper.forHtmlAttribute(codigo)%>">
                        <span class="text-nowrap align-text-top labelSemNegrito"><%=TextHelper.forHtmlContent(nome)%></span>
                      </label>
                    </div>
                    <% } %>
                  </div>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="form-group col-sm-12 col-md-6">
                <span id="dataDaReclamacao" class="labelSemNegrito ml-1">Data da reclamação</span>
                <div class="row mt-2" role="group" aria-labelledby="dataDaReclamacao">
                  <div class="form-check pt-2 col-sm-12 col-md-1">
                    <div class="float-left align-middle mt-4 form-control-label">
                      <label for="dataDaReclamacaoDE" class="labelSemNegrito">De</label>
                    </div>
                  </div>
                  <div class="form-check pt-2 col-sm-12 col-md-5">
                    <hl:htmlinput name="periodoIni" di="periodoIni" type="text" classe="Edit form-control w-100" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" value="<%=TextHelper.forHtmlAttribute(filtroDataIni)%>" />
                  </div>
                  <div class="form-check pt-2 col-sm-12 col-md-1">
                    <div class="float-left align-middle mt-4 form-control-label">
                      <label for="dataDaReclamacaoA" class="labelSemNegrito">a</label>
                    </div>
                  </div>
                  <div class="form-check pt-2 col-sm-12 col-md-5">
                    <hl:htmlinput name="periodoFim" di="periodoFim" type="text" classe="Edit form-control w-100" size="10" mask="<%=LocaleHelper.getDateJavascriptPattern()%>" value="<%=TextHelper.forHtmlAttribute(filtroDataFim)%>" />
                  </div>
                </div>
              </div>
            </div>
            <%if (!responsavel.isSer()) { %>
              <div class="row">
                <div class="col-sm-6">
                  <%@ include file="../consultarMargem/include_campo_matricula_v4.jsp" %>
                </div>
              </div>
              <div class="row">
                <div class="form-group col-sm-6">
                  <hl:campoCPFv4 nf="btnEnvia" placeHolder="<%=ApplicationResourcesHelper.getMessage("mensagem.informacao.servidor.cpf.completo", responsavel) %>" />
                </div>
              </div>
            <%} %>
            <div class="btn-action">
              <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/editarReclamacao?acao=iniciar&<%=(String)(SynchronizerToken.generateToken4URL(request))%>')"><hl:message key="rotulo.botao.voltar"/></a>
              <a class="btn btn-primary" href="#no-back" onClick="return validForm();"><hl:message key="rotulo.botao.pesquisar"/></a>
            </div>
          </div>
        </div>
      </form>
    </div>
  </div>
  <div class="card">
    <div class="card-header">
      <h2 class="card-header-title">Lista de reclamações</h2>
    </div>
    <div class="card-body table-responsive">
      <table class="table table-striped table-hover">
        <thead>
          <tr>
            <th scope="col"><hl:message key="rotulo.servidor.singular"/></th>
            <th scope="col"><hl:message key="rotulo.servidor.matricula"/></th>
            <th scope="col"><hl:message key="rotulo.consignataria.singular"/></th>
            <th scope="col"><hl:message key="rotulo.reclamacao.data"/></th>
            <th scope="col"><hl:message key="rotulo.reclamacao.texto"/></th>
            <th scope="col"><hl:message key="rotulo.reclamacao.acoes"/></th>
          </tr>
        </thead>
        <tbody>
        <%=JspHelper.msgRstVazio(reclamacoes.size()==0, "8", "lp")%>
        <%
          String rrsCodigo = "";
                 String remetente = "";
                 String matricula = "";
                 String destinatario = "";
                 String data = "";
                 String msg = "";
                     
                 Iterator itr = reclamacoes.iterator();
                 while (itr.hasNext()) {
                    TransferObject next = (TransferObject)itr.next();
                    rrsCodigo = (String) next.getAttribute(Columns.RRS_CODIGO);
                    remetente = (String) next.getAttribute(Columns.SER_NOME);
                    destinatario = (String) next.getAttribute(Columns.CSA_NOME_ABREV);
                    matricula = (String) next.getAttribute(Columns.RSE_MATRICULA);
                    data = DateHelper.toDateTimeString((Date) next.getAttribute(Columns.RRS_DATA));                          
                    msg = (String) next.getAttribute(Columns.RRS_TEXTO);
                    String displayMsg = (msg.length() > 100) ? msg.substring(0, 100) + "..." :msg;
          %>
          <tr>
            <td><%=TextHelper.forHtmlContent(remetente)%></td>
            <td><%=TextHelper.forHtmlContent(matricula)%></td>
            <td><%=TextHelper.forHtmlContent(destinatario)%></td>                                
            <td><%=TextHelper.forHtmlContent(data)%></td>
            <td><%=TextHelper.forHtmlContent(displayMsg)%></td>
            <td>
              <a href="#no-back" onClick="lerRrs('<%=TextHelper.forJavaScript(rrsCodigo)%>')">
                <hl:message key="rotulo.reclamacao.ler"/>
              </a>
            </td>
          </tr>
          <% } %>
        </tbody>
        <tfoot>
          <tr>
            <td colspan="8">
              <hl:message key="rotulo.reclamacao.listagem"/> - 
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
  <div class="btn-action">
    <a class="btn btn-outline-danger" href="#no-back" onClick="postData('../v3/carregarPrincipal'); return false;">Voltar</a>
  </div>
</c:set>

<c:set var="javascript">
  <script type="text/JavaScript" src="../js/scripts_2810.js"></script>
  <script type="text/JavaScript" src="../js/validaMascara_v4.js"></script>
  <script type="text/JavaScript" src="../js/validacoes.js"></script>
  <script type="text/JavaScript" src="../js/xbdhtml.js"></script>
  <script type="text/JavaScript">
      // valida o formulário antes do envio do submit
      function validForm() {
          var msg = '';
          var periodoIni = document.getElementById('periodoIni');
          var periodoFim = document.getElementById('periodoFim');
          var campoErrado = false;

          fout(periodoIni);
          if (!ValidaMascara(periodoIni)) {
              msg = '<hl:message key="mensagem.erro.listar.reclamacao.data.invalida"/>';
              periodoIni.value = '';                             
              periodoIni.focus(); 
              campoErrado = true;          
          }       

          fout(periodoFim);
          if (!ValidaMascara(periodoFim)) {
              msg = '<hl:message key="mensagem.erro.listar.reclamacao.data.invalida"/>';
              periodoFim.value = '';                            
              periodoFim.focus();
              campoErrado = true;                                          
          }     

          // valida se as datas estão preenchidas corretamente
          if (periodoIni.value != '' && periodoFim.value != '' && !campoErrado) {  
              var PartesData = new Array();
              PartesData = obtemPartesData(periodoIni.value);
              var DiaIni = PartesData[0];
              var MesIni = PartesData[1];
              var AnoIni = PartesData[2];
              PartesData = obtemPartesData(periodoFim.value);
              var DiaFim = PartesData[0];
              var MesFim = PartesData[1];
              var AnoFim = PartesData[2];
              if (!VerificaPeriodoExt(DiaIni, MesIni, AnoIni, DiaFim, MesFim, AnoFim, 30)) {
                periodoIni.focus();
                return false;
              }
          }

          if (msg != '') {
              alert(msg);
              return false;
          } else {
            if(typeof vfRseMatricula === 'function')
              {
                if(vfRseMatricula(true))
                {
                  f0.submit();
                }
              }
              else
              {
                f0.submit();
              }
          }
      }

      function lerRrs(rrsCodigo) {
         var url = "../v3/editarReclamacao?acao=detalharReclamacao&rrs_codigo=" + rrsCodigo + "&<%=SynchronizerToken.generateToken4URL(request)%>";
         postData(url);
      }

      function testDate(campo) {                    
          fout(campo);
          if (!ValidaMascara(campo)) {
              alert('<hl:message key="mensagem.erro.listar.reclamacao.data.invalida"/>');
              campo.value = '';
              with(document.form1) {
                  campo.name.focus();                
              }
              return false;
          }       
      }
  </script>
  <script type="text/JavaScript">
  	f0 = document.forms[0];
  </script>
</c:set>

<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>