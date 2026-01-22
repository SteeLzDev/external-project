<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.zetra.econsig.helper.seguranca.AcessoSistema" %>
<%@ page import="com.zetra.econsig.helper.seguranca.SynchronizerToken" %>
<%@ page import="com.zetra.econsig.helper.texto.ApplicationResourcesHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TransferObjectHelper"%>
<%@ page import="com.zetra.econsig.helper.texto.DateHelper" %>
<%@ page import="com.zetra.econsig.helper.texto.TextHelper" %>
<%@ page import="com.zetra.econsig.helper.web.JspHelper" %>
<%@ page import="com.zetra.econsig.values.Columns" %>
<%@ page import="com.zetra.econsig.dto.*" %>
<%@ page import="com.zetra.econsig.dto.entidade.*" %>
<%@ page import="com.zetra.econsig.persistence.entity.OcorrenciaRegistroSer" %>
<%@ taglib uri="/html-lib" prefix="hl" %>
<jsp:useBean id="paramSession" scope="session" class="com.zetra.econsig.helper.web.ParamSession" />
<%
AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

boolean readOnly = "true".equalsIgnoreCase((String) request.getAttribute("readOnly"));
List listaSvcBloqueaveisServidor = (List) request.getAttribute("listaSvcBloqueaveisServidor");
List<TransferObject> historicoOcorrencia = (List<TransferObject>) request.getAttribute("historicoOcorrencia");

String rseCodigo = (String) request.getAttribute("rseCodigo");
String rseMatricula = (String) request.getAttribute("rseMatricula");
String serNomeCodificado = (String) request.getAttribute("serNomeCodificado");
String serNome = (String) request.getAttribute("serNome");
String qtdDefault = (String) request.getAttribute("qtdDefault");

// Pega os valores dos bloqueios por serviços
Map bloqueioServico = (Map) request.getAttribute("bloqueioServico");
List convenios = (List) request.getAttribute("convenios");
boolean exigeMotivo = (boolean) request.getAttribute("exigeMotivo");
boolean exigeOtp = (boolean) request.getAttribute("exigeOtp");
%>

<c:set var="title">
  <hl:message key="rotulo.servidor.listar.convenios.titulo"/>
</c:set>
<c:set var="imageHeader">
  <use xlink:href="#i-manutencao"></use>
</c:set>
<c:set var="bodyContent">
  <form method="post" action="../v3/listarConvenioServidor?acao=editar&<%=SynchronizerToken.generateToken4URL(request)%>" name="form1">
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><%=TextHelper.forHtmlContent(rseMatricula)%> - <%=TextHelper.forHtmlContent(serNome)%></h2>
      </div>
      <% if (!responsavel.isSer()) { %>
      <div class="card-body">
        <div class="row">
          <div class="form-group col-sm-6">
            <label for="qtd_default"><hl:message key="rotulo.servidor.listar.convenios.qtde.padrao"/></label>
            <hl:htmlinput name="qtd_default" di="qtd_default" type="text" classe="form-control" value="<%=TextHelper.forHtmlAttribute(qtdDefault)%>" readonly="<%=String.valueOf(readOnly)%>" size="2" onFocus="SetarEventoMascara(this,'#D2',true);" onBlur="fout(this);ValidaMascara(this);" placeHolder="<%=ApplicationResourcesHelper.getMessage("rotulo.servidor.listar.convenios.digite.qtde.padrao", responsavel) %>"/>
          </div>
        </div>
      </div>
      <% } %>
      <div class="table-responsive ">
        <table class="table table-striped table-hover">
          <thead>
            <tr>
              <th scope="col" width="10%"><hl:message key="rotulo.servidor.listar.convenios.verba"/></th>
              <th scope="col" width="15%"><hl:message key="rotulo.servidor.listar.convenios.descricao"/></th>
              <th scope="col" width="10%"><hl:message key="rotulo.servidor.listar.convenios.consignataria"/></th>
              <th scope="col" width="10%"><hl:message key="rotulo.servidor.listar.convenios.qtde.por.servico"/></th>
              <th scope="col" width="9%"><hl:message key="rotulo.servidor.listar.convenios.qtde.considerada"/></th>
              <th scope="col" width="9%"><hl:message key="rotulo.servidor.listar.convenios.qtde"/></th>
              <th scope="col" width="37%"><hl:message key="rotulo.servidor.listar.convenios.observacao"/></th>
            </tr>
          </thead>
          <tbody>
          <%=JspHelper.msgRstVazio(convenios.size()==0, 13, responsavel)%>
          <%
            boolean primeiro = true;
            StringBuffer cnvs = new StringBuffer();
            CustomTransferObject convenio = null;
            String svc_codigo, cnv_codigo, cnv_cod_verba, svc_descricao, csa_nome, pcr_vlr, pcr_vlr_ser, pcr_vlr_csa, pcr_vlr_cse, pcr_vlr_papel, pcr_obs;
            String psr_vlr;
            List listaCnvAlterados = new ArrayList();
            Iterator it = convenios.iterator();
            while (it.hasNext()) {
              convenio = (CustomTransferObject)it.next();
              cnv_codigo = (String)convenio.getAttribute(Columns.CNV_CODIGO);
              cnv_cod_verba = (convenio.getAttribute(Columns.CNV_COD_VERBA) != null) ? (String)convenio.getAttribute(Columns.CNV_COD_VERBA) : "";
              svc_descricao = (String)convenio.getAttribute(Columns.SVC_DESCRICAO);
              csa_nome = (convenio.getAttribute(Columns.CSA_NOME_ABREV) != null && !convenio.getAttribute(Columns.CSA_NOME_ABREV).equals("")) ? (String) convenio.getAttribute(Columns.CSA_NOME_ABREV) : (String) convenio.getAttribute(Columns.CSA_NOME);
              pcr_vlr = (convenio.getAttribute(Columns.PCR_VLR) != null) ? (String) convenio.getAttribute(Columns.PCR_VLR) : "";

              pcr_vlr_ser = (convenio.getAttribute(Columns.PCR_VLR_SER) != null) ? (String) convenio.getAttribute(Columns.PCR_VLR_SER) : "";
              pcr_vlr_csa = (convenio.getAttribute(Columns.PCR_VLR_CSA) != null) ? (String) convenio.getAttribute(Columns.PCR_VLR_CSA) : "";
              pcr_vlr_cse = (convenio.getAttribute(Columns.PCR_VLR_CSE) != null) ? (String) convenio.getAttribute(Columns.PCR_VLR_CSE) : "";

              if (responsavel.isSer()) {
                  pcr_vlr_papel = pcr_vlr_ser;
              } else if (responsavel.isCsa()) {
                  pcr_vlr_papel = pcr_vlr_csa;
              } else if (responsavel.isCseSupOrg()) {
                  pcr_vlr_papel = pcr_vlr_cse;
              } else {
                  pcr_vlr_papel = "";
              }

              pcr_obs = (convenio.getAttribute(Columns.PCR_OBS) != null) ? (String) convenio.getAttribute(Columns.PCR_OBS) : "";
            
              svc_codigo = (String)convenio.getAttribute(Columns.SVC_CODIGO);
              psr_vlr = (bloqueioServico.get(svc_codigo) != null) ? (String) bloqueioServico.get(svc_codigo) : "";
            
              // Lista de convenios (cnvs) a serem atualizados. Se responsavel for servidor, pode atualizar somente
              // convenios que os servicos sao bloqueaveis pelo servidor
              if (!responsavel.isSer() || listaSvcBloqueaveisServidor.contains(svc_codigo)) {
                  cnvs.append(cnv_codigo).append(",");
                  listaCnvAlterados.add(cnv_codigo);
              } 
              
              String rowspan = "1";
              if (primeiro && convenios.size() > 1 && !readOnly && !responsavel.isSer()) {
                  rowspan = "2";
              }
          %>
          
            <tr>
              <td><%=TextHelper.forHtmlContent(cnv_cod_verba.toUpperCase())%></td>
              <td><%=TextHelper.forHtmlContent(svc_descricao.toUpperCase())%></td>
              <td><%=TextHelper.forHtmlContent(csa_nome.toUpperCase())%></td>
              <td><%=TextHelper.forHtmlContent(psr_vlr)%></td>
              <td><%=TextHelper.forHtmlAttribute(pcr_vlr)%></td>
              <% if (!readOnly && (!responsavel.isSer() || listaSvcBloqueaveisServidor.contains(svc_codigo))) { %>
                <td><input type="text" name="cnv_<%=TextHelper.forHtmlAttribute(cnv_codigo)%>" id="cnv_<%=TextHelper.forHtmlAttribute(cnv_codigo)%>" class="form-control mt-1 pl-1 pr-1" value="<%=TextHelper.forHtmlAttribute(pcr_vlr_papel)%>" size="2" onFocus="SetarEventoMascara(this,'#D2',true);" onBlur="fout(this);ValidaMascara(this);" onChange="checkObs('<%=TextHelper.forJavaScript(cnv_codigo)%>')"></td>
                <td>
                <% if (primeiro && convenios.size() > 1  && !responsavel.isSer()) { %>
                  <div class="col-sm-12">
                    <div class="row">
                      <div class="col-md-12 col-lg-6 mt-1 pl-0 pr-1">
                        <input type="text" name="cnv2_<%=TextHelper.forHtmlAttribute(cnv_codigo)%>" id="cnv2_<%=TextHelper.forHtmlAttribute(cnv_codigo)%>" class="form-control" value="<%=TextHelper.forHtmlAttribute(pcr_obs)%>" size="30" onFocus="SetarEventoMascara(this,'#*1000',true);" onBlur="fout(this);ValidaMascara(this);">
                      </div>
                      <div class="col-md-12 col-lg-6 mt-1 pl-1">
                        <div class="btn-action text-center mb-0 pl-0 pr-1">
                          <a class="btn btn-primary ml-0" href="#no-back" onClick="copia_qdte(f0.cnv_<%=TextHelper.forJavaScript(cnv_codigo)%>, f0.cnv2_<%=TextHelper.forJavaScript(cnv_codigo)%>);"><hl:message key="mensagem.servidor.convenio.copiar.para.todos"/></a>
                        </div>
                      </div>
                    </div>
                  </div>
                <%} else { %>
                  <input type="text" name="cnv2_<%=TextHelper.forHtmlAttribute(cnv_codigo)%>" id="cnv2_<%=TextHelper.forHtmlAttribute(cnv_codigo)%>" class="form-control" value="<%=TextHelper.forHtmlAttribute(pcr_obs)%>" size="30" onFocus="SetarEventoMascara(this,'#*1000',true);" onBlur="fout(this);ValidaMascara(this);">
                <% } %>            
              </td>
              <% } else { %>
                <td><input type="text" name="cnv_<%=TextHelper.forHtmlAttribute(cnv_codigo)%>" id="cnv_<%=TextHelper.forHtmlAttribute(cnv_codigo)%>" class="form-control mt-1 pl-1 pr-1" value="<%=TextHelper.forHtmlAttribute(pcr_vlr_papel)%>" size="2" onFocus="SetarEventoMascara(this,'#D2',true);" onBlur="fout(this);ValidaMascara(this);" onChange="checkObs('<%=TextHelper.forJavaScript(cnv_codigo)%>')" disabled></td>
                <td><input type="text" name="cnv2_<%=TextHelper.forHtmlAttribute(cnv_codigo)%>" class="form-control" value="<%=TextHelper.forHtmlAttribute(pcr_obs)%>" size="30" disabled></td>
              <% } %> 
            </tr>
            <%
              primeiro = false;
            }
            %>
          </tbody>
          <tfoot>
            <tr>
              <td colspan="6"><hl:message key="rotulo.servidor.lista.convenios"/></td>
            </tr>
          </tfoot>
        </table>
      </div>
    </div>
    <% if (!responsavel.isSer()) { %>
    <div class="card">
      <div class="card-header">
        <h2 class="card-header-title"><hl:message key="rotulo.motivo.operacao.singular"/></h2>
      </div>
      <div class="card-body">
      <%-- Utiliza a tag library EfetivaAcaoMotivoOperacaoTag.java para exibir os dados do Tipo de Motivo da Operação --%>
        <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="<%=ApplicationResourcesHelper.getMessage("mensagem.confirmacao.bloqueio.verba", responsavel)%>" tmoSempreObrigatorio="<%=exigeMotivo%>" inputSizeCSS="col-sm-12"/>
      <%-- Fim dos dados do Motivo da Operação --%>
      </div>
    </div>
    <% } %>
        <div class="row">
            <div class="col-sm col-md">
                <div class="card">
                    <div class="card-header">
                        <h2 class="card-header-title">
                            <hl:message key="rotulo.servidor.historico.titulo.lower" />
                        </h2>
                    </div>
                    <div class="card-body table-responsive p-0">
                        <table id="dataTables" class="table table-striped table-hover">
                            <thead>
                                <tr>
                                    <th><hl:message key="rotulo.servidor.historico.tipo.ocorrencia"/></th>
                                    <th><hl:message key="rotulo.servidor.historico.usuario"/></th>
                                    <th><hl:message key="rotulo.servidor.historico.ocorrencia.data"/></th>
                                    <th><hl:message key="rotulo.servidor.historico.observacao"/></th>
                                    <th><hl:message key="rotulo.servidor.ip.acesso"/></th>
                                </tr>
                            </thead>
                            <tbody>
              <%
              if (historicoOcorrencia != null && !historicoOcorrencia.isEmpty()) {
                  Iterator<?> itHistorico = historicoOcorrencia.iterator();  
                  while (itHistorico.hasNext()) { 
                      CustomTransferObject cto = (CustomTransferObject) itHistorico.next();

                      cto = TransferObjectHelper.mascararUsuarioHistorico((CustomTransferObject) cto, null, responsavel);

                      String orsData = DateHelper.toDateTimeString((Date) cto.getAttribute(Columns.ORS_DATA));

                      String loginOrsResponsavel = cto.getAttribute(Columns.USU_LOGIN) != null ? cto.getAttribute(Columns.USU_LOGIN).toString() : "";
                      String orsObs = cto.getAttribute(Columns.ORS_OBS).toString();
                      String tocDescricao = (String) cto.getAttribute(Columns.TOC_DESCRICAO);
                      String orsIpAcesso = cto.getAttribute(Columns.ORS_IP_ACESSO) != null ? cto.getAttribute(Columns.ORS_IP_ACESSO).toString() : "";
              %>
                                <tr>
                                    <td><%=TextHelper.forHtmlContent(tocDescricao)%></td>
                                    <td><%=TextHelper.forHtmlContent(loginOrsResponsavel)%></td>
                                    <td><%=TextHelper.forHtmlContent(orsData)%></td>
                                    <td><%=TextHelper.forHtmlContent(orsObs)%></td>
                                    <td><%=TextHelper.forHtmlContent(orsIpAcesso)%></td>
                                </tr>
                                <%  } %>
                                <%  
               }
              %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

        <div class="btn-action">
      <% if (readOnly || (responsavel.isSer() && listaSvcBloqueaveisServidor.isEmpty())) { %>
        <a class="btn btn-outline-danger" href="#no-back" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>')" title="<hl:message key="rotulo.botao.voltar"/>"><hl:message key="rotulo.botao.voltar"/></a>
      <% } else { %>
        <a class="btn btn-outline-danger" onClick="postData('<%=TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request))%>'); return false;" href="#"><hl:message key="rotulo.botao.cancelar"/></a>
        <a class="btn btn-primary" id="btnEnvia" href="#no-back" onClick="validaForm(); return false;"><hl:message key="rotulo.botao.salvar"/></a>
        <INPUT NAME="MM_update"     TYPE="hidden" VALUE="form">
        <INPUT NAME="tipo"          TYPE="hidden" VALUE="editar">
        <INPUT NAME="RSE_CODIGO"    TYPE="hidden" VALUE="<%=TextHelper.forHtmlAttribute(rseCodigo)%>">
        <INPUT NAME="RSE_MATRICULA" TYPE="hidden" VALUE="<%=TextHelper.forHtmlAttribute(rseMatricula)%>">
        <INPUT NAME="SER_NOME"      TYPE="hidden" VALUE="<%=TextHelper.forHtmlAttribute(serNomeCodificado)%>">
        <INPUT NAME="convenios"     TYPE="hidden" VALUE="<%=TextHelper.forHtmlAttribute((cnvs))%>">
      <% } %>
    </div>

    <% if (exigeOtp) { %>
    <div class="modal fade" id="exibirOtpModal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1" aria-labelledby="exibirOtpModal" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="staticBackdropLabel"><hl:message key="rotulo.otp"/></h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <div class="container-fluid">
                <div class="row m-2">
                    <span id="instrucoes"><hl:message key="mensagem.senha.servidor.otp.nao.informado"/></span>
                </div>
                <div class="row">
                    <input type="password" class="form-control" id="codigoOtp" name="codigoOtp" placeholder='<hl:message key="rotulo.otp"/>'>
                </div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn btn-outline-danger" data-bs-dismiss="modal"><hl:message key="rotulo.botao.cancelar"/></button>
            <button type="button" class="btn btn-primary" onClick="validarOtp();"><hl:message key="rotulo.botao.enviar"/></button>
          </div>
        </div>
      </div>
    </div>
    <% } %>

  </form>
</c:set>
<c:set var="javascript">
  <link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-buttons-dt/css/buttons.dataTables.min.css"/>
  <link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-responsive-bs5/css/responsive.bootstrap5.min.css"/>
  <link rel="stylesheet" type="text/css" href="../node_modules/datatables.net-bs5/css/dataTables.bootstrap5.min.css"/>
  <script  src="../node_modules/datatables.net/js/jquery.dataTables.min.js"></script>
    <script src="../node_modules/datatables.net-bs5/js/dataTables.bootstrap5.min.js"></script>
    <script src="../node_modules/datatables.net-buttons/js/dataTables.buttons.min.js"></script>
    <script src="../node_modules/datatables.net-buttons/js/buttons.colVis.min.js"></script>
    <script src="../node_modules/datatables.net-buttons/js/buttons.html5.min.js"></script>
    <script src="../node_modules/datatables.net-buttons/js/buttons.print.min.js"></script>
    <script src="../node_modules/datatables.net-responsive/js/dataTables.responsive.min.js"></script>
    <script  src="../node_modules/moment/min/moment.min.js"></script>
  	<script  src="../node_modules/datatables.net-plugins/sorting/datetime-moment.js"></script>
    <script type="text/JavaScript">
     $(document).ready(function() {
         $.fn.dataTable.moment( 'DD/MM/YYYY HH:mm:ss' );
             $('#dataTables').DataTable({
                 "searching": false,
                 "paging": true,
                 "pageLength": 20,
                 "lengthChange": false,
                 "pagingType": "simple_numbers",
                 "order": [[2,"desc"]],
                 "autowidth" : true,
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
                         }
                       }
             });
         });
    </script>
    <% if (!responsavel.isSer()) { %>
    <hl:efetivaAcaoMotivoOperacaov4 msgConfirmacao="<%=ApplicationResourcesHelper.getMessage("mensagem.confirmacao.bloqueio.verba", responsavel)%>" scriptOnly="true" />
    <% } %>
    <script language="JavaScript" type="text/JavaScript">
    var f0 = document.forms[0];
    </script>
    <script language="JavaScript" type="text/JavaScript" src="../js/scripts_2810.js"></script>
    <script language="JavaScript" type="text/JavaScript" src="../js/xbdhtml.js"></script>
    <script language="JavaScript" type="text/JavaScript">
    function formLoad() {
    <% if (!readOnly) { %>
        focusFirstField();
    <% } %>
    }

    function copia_qdte(campoQtd, campoObs) {
      // Navega em todos os campos do formulário
      for (i = 0; i < f0.elements.length; i++) {
        var e = f0.elements[i];
        
        if (e.name.indexOf('cnv_') == 0) {
          // Se é campo de Qtd, atribui valor do campo padrão
          e.value = campoQtd.value;
        } else if (e.name.indexOf('cnv2_') == 0) {
          // Se é campo de Obs, atribui valor do campo padrão
          e.value = campoObs.value;
        }
      }
    }

    function checkObs(cnv) {
      qtdField = getElt('cnv_' + cnv);
      obsField = getElt('cnv2_' + cnv);
      
      if (qtdField.value == '') {
        obsField.value = '';
      }
    }

    function validaForm() {
        <% if (!responsavel.isSer()) { %>
        var tmoCodigo = getElt('TMO_CODIGO');
        <%-- Se exige motivo, tem que validar a seleção de um motivo e o preenchimento ou não da observação --%> 
        if (<%=exigeMotivo %>) {
            if (tmoCodigo.value && confirmaAcaoConsignacao()) {
            	enviaForm();
            <%-- Se exige motivo o motivo tem que estar preenchido, do contrário manda alerta para o usuário --%>
            } else if (!tmoCodigo.value) {
                alert('<hl:message key="mensagem.motivo.operacao.obrigatorio"/>');
                return false;
            }
        } else {
            <%-- Se NÃO exige motivo, se o motivo não foi selecionado, pode dar submit --%>
            if (!tmoCodigo.value) {
            	enviaForm();
            } 
            <%-- porém se o motivo foi selecionado, tem que verificar se o motivo exige obs --%>
            else if (tmoCodigo.value && confirmaAcaoConsignacao()) {
            	enviaForm();
            } 
        }
        <% } else { %>
            enviaForm();
        <% } %>
    }

    function enviaForm() {
        <% if (exigeOtp) { %>
        enviarOtp();
        <% } else { %>
        f0.submit();
        <% } %>
    }
    
    <% if (exigeOtp) { %>
    function validarOtp() {
        if ($('#codigoOtp').val()) {
            $('#exibirOtpModal').modal('hide');
            f0.submit();
        } else {
            alert('<hl:message key="mensagem.senha.servidor.otp.nao.informado"/>');
            $('#codigoOtp').focus();
        }
    }

    function enviarOtp() {
        $.post("../v3/listarConvenioServidor?acao=enviarOtp&_skip_history_=1", function() {
            $('#exibirOtpModal').modal('show');
        })
        .fail(function(err) {
            console.log(err);
            postData('../v3/exibirMensagem?acao=exibirMsgSessao');
        });
    }
    <% } %>
    </script>
</c:set>
<t:page_v4>
  <jsp:attribute name="header">${title}</jsp:attribute>
  <jsp:attribute name="imageHeader">${imageHeader}</jsp:attribute>
  <jsp:attribute name="javascript">${javascript}</jsp:attribute>
  <jsp:body>${bodyContent}</jsp:body>
</t:page_v4>
