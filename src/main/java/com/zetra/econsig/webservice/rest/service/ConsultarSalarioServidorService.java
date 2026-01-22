package com.zetra.econsig.webservice.rest.service;
 
import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.MargemControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.TermoAdesaoControllerException;
import com.zetra.econsig.persistence.entity.Servidor;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.request.ConsentPayload;
import com.zetra.econsig.webservice.rest.request.ConsultarCPFRequest;
import com.zetra.econsig.webservice.rest.request.ConsultarSalarioRequest;
import com.zetra.econsig.webservice.rest.request.ConsultarSalarioResponse;
import com.zetra.econsig.webservice.rest.request.ErrorResponseConsultarCPF;
import com.zetra.econsig.webservice.rest.request.MargemSalarioResponse;
import com.zetra.econsig.webservice.rest.request.MatriculaSalarioItem;
import com.zetra.econsig.webservice.rest.request.ResponseRestRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
 
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
 
@Path("/servidor")
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Controller
public class ConsultarSalarioServidorService extends RestService {
 
    @Context
    private SecurityContext securityContext;
 
    @Autowired
    private ServidorController servidorController;
 
    @Autowired
    private MargemController margemController;
 
    @POST
    @Secured
    @Path("/consultarSalario")
    public Response consultarSalario(ConsultarSalarioRequest request)
        throws ServidorControllerException, MargemControllerException, LogControllerException, TermoAdesaoControllerException {
 
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
 
        if (responsavel.temPermissao(CodedValues.FUN_CONSULTAR_SALARIO_SERV_REST) && responsavel.isSup()) {
        
                List<Servidor> servidor = servidorController.findByCpf(request.cpf, responsavel);
                if (servidor.isEmpty()) {
                        return Response.status(Response.Status.NOT_FOUND).build();
                }
        
                ConsentPayload consentPayload = request.consentPayload;
                       
               if (request.consentPayload == null ||
                        request.consentPayload.consentDate == null ||
                        request.consentPayload.dataSet == null ||
                        request.consentPayload.hashConnection == null ||
                        request.consentPayload.termVersion == null) {
        
                        return Response.status(Response.Status.BAD_REQUEST)
                                .entity(new ResponseRestRequest(ApplicationResourcesHelper.getMessage(
                                        "mensagem.consulta.salario.servidor.consentimento.invalido", responsavel)))
                                .build();
 
                }
                  
            final List<TransferObject> servidores = servidorController.consultaSalarioServidor(request.cpf,
                    responsavel);
            if (servidores != null && !servidores.isEmpty()) {
                final List<ConsultarSalarioResponse> serResponseList = new ArrayList<>();
 
                for (TransferObject ser : servidores) {
                    final String rseCodigo = ser.getAttribute(Columns.RSE_CODIGO).toString();
                    final ConsultarSalarioResponse serResponse = consultarSalarioConvert(ser);
 
                    final TransferObject margem = margemController.lstMargemIncideEmprestimo(rseCodigo, responsavel);
                    String textoLogSalarioCalculado = null;
                    if (margem != null) {
                        final MargemSalarioResponse mar = new MargemSalarioResponse();
                        mar.setMargemMedia((BigDecimal) margem.getAttribute(Columns.MRS_MEDIA_MARGEM));
                        mar.setMargem((BigDecimal) margem.getAttribute(Columns.MRS_MARGEM));
                        mar.setMargemDescricao((String) margem.getAttribute(Columns.MAR_DESCRICAO));
                        mar.setMargemRest((BigDecimal) margem.getAttribute(Columns.MRS_MARGEM_REST));
                        mar.setMargemPorcentagem((BigDecimal) margem.getAttribute(Columns.MAR_PORCENTAGEM));
                        mar.setMargemUsada((BigDecimal) margem.getAttribute(Columns.MRS_MARGEM_USADA));
                        
 
                        // Obtém o somatório total de consignações que incidem nessa margem e nas
                        // margens casadas com esta margem, que afetam o valor da margem.
                        final Short marCodigo = (Short) margem.getAttribute(Columns.MAR_CODIGO);
                        final BigDecimal totalConsignacoes = margemController
                                .obtemVlrTotalConsignacoesCalculoSalario(rseCodigo, marCodigo, responsavel);
                        serResponse.setEmprestimos(totalConsignacoes);
 
                        if (serResponse.getSalario() == null || serResponse.getSalario().signum() <= 0) {
                            // Se o salário enviado pela folha está nulo, calcula o salário baseado na
                            // margem
                            // ao qual os serviços de empréstimo incidem, caso a porcentagem da margem
                            // esteja presente
                            if (mar.getMargemPorcentagem() != null && mar.getMargemPorcentagem().signum() > 0) {
                                final BigDecimal salarioCalculado = calcularSalarioPelaMargem(margem, totalConsignacoes,
                                        responsavel);
                                serResponse.setSalario(salarioCalculado);
                                serResponse.setSalarioCalculado(true);
                                textoLogSalarioCalculado = ApplicationResourcesHelper.getMessage(
                                        "mensagem.consulta.salario.servidor.salario.calculado", responsavel,
                                        String.valueOf(marCodigo),
                                        NumberHelper.format(salarioCalculado.doubleValue(), NumberHelper.getLang()));
                            } else {
                                return Response.status(Response.Status.EXPECTATION_FAILED).build();
                            }
                        }
                    }
                    serResponseList.add(serResponse);
 
                    final LogDelegate log = new LogDelegate(responsavel, Log.REGISTRO_SERVIDOR, Log.FIND,
                            Log.LOG_INFORMACAO);
                    log.setRegistroServidor(rseCodigo);
                    log.setUsuario(responsavel.getUsuCodigo());
                    log.add(ApplicationResourcesHelper.getMessage("mensagem.consulta.salario.servidor", responsavel));
                    if (textoLogSalarioCalculado != null) {
                        log.add(textoLogSalarioCalculado);
                    }
 
                    if (consentPayload != null) {
                        StringBuilder linhaLog = new StringBuilder();
                        linhaLog.append(consentPayload.dataSet != null ? consentPayload.dataSet : "").append(";");
                        linhaLog.append(consentPayload.consentDate != null ? consentPayload.consentDate : "").append(";");
                        linhaLog.append(consentPayload.hashConnection != null ? consentPayload.hashConnection : "").append(";");
                        linhaLog.append(consentPayload.termVersion != null ? consentPayload.termVersion : "").append(";");                      
 
 
                        log.add(linhaLog.toString());
                    }
 
                    log.write();
                }
 
                return Response.status(Response.Status.OK)
                        .entity(serResponseList)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8")
                        .build();
            }
        } else {
            String mensagem = !responsavel.temPermissao(CodedValues.FUN_CONSULTAR_SALARIO_SERV_REST)
                    ? ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.sem.permissao.autorizar.operacao",
                    null)
                    : ApplicationResourcesHelper.getMessage("usuario.nao.suporte.desautorizado", null);
 
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ResponseRestRequest(mensagem))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8")
                    .build();
        }
 
                return Response.status(Response.Status.NOT_FOUND).build();
    }
 
    @POST
    @Path("/consultarCPF")
    @Secured
    public Response consultarCPF(ConsultarCPFRequest request) {
 
        final AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        try {
                
                if (responsavel == null ||
                !responsavel.temPermissao(CodedValues.FUN_CONSULTAR_SALARIO_SERV_REST) ||
                !responsavel.isSup()) {
                        return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(new ErrorResponseConsultarCPF(401, ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.sem.permissao.autorizar.operacao", responsavel)))
                        .build();
                }
                
                final String cpf = request.cpf;
                if (cpf == null || cpf.length() != 14) {
                
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponseConsultarCPF(400, ApplicationResourcesHelper.getMessage("mensagem.erro.formacao.incorreta", responsavel)))
                        .build();
                }
                
                final List<Servidor> servidores = servidorController.findByCpf(cpf, responsavel);
                if (servidores == null || servidores.isEmpty()) {
                        return Response.status(Response.Status.NOT_FOUND)
                                .entity(new ErrorResponseConsultarCPF(404, ApplicationResourcesHelper.getMessage("mensagem.erro.rest.cpf.invalido", responsavel)))
                                .build();
                }
                
                final List<TransferObject> registros = servidorController.consultaSalarioServidor(cpf, responsavel);
                if (registros == null || registros.isEmpty()) {
                
                        return Response.status(206)
                                .entity(new ErrorResponseConsultarCPF(206, ApplicationResourcesHelper.getMessage("mensagem.erro.cpf.desativado", responsavel)))
                                .build();
                }
                
                final List<TransferObject> ativos = registros.stream()
                        .filter(this::isMatriculaAtiva)
                        .toList();
                if (ativos.isEmpty()) {
        
                return Response.status(206)
                        .entity(new ErrorResponseConsultarCPF(206, ApplicationResourcesHelper.getMessage("mensagem.erro.cpf.desativado", responsavel)))
                        .build();
                }
                
                final List<MatriculaSalarioItem> resposta = new ArrayList<>(ativos.size());
                for (TransferObject r : ativos) {
                        final String matricula = (String) r.getAttribute(Columns.RSE_MATRICULA);
                        final String flag = resolveFlagSalario(r, responsavel);
                        resposta.add(new MatriculaSalarioItem(matricula, flag));
                }
                return Response.status(Response.Status.OK)
                        .entity(resposta)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8")
                        .build();
       } catch (Exception e) {
       
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity(new ErrorResponseConsultarCPF(500, ApplicationResourcesHelper.getMessage("mensagem.erro.processamento.interno", responsavel)))
        .build();
       }
    }
 
    
   
 
    private boolean isMatriculaAtiva(TransferObject r) {
        return  "1".equals(r.getAttribute(Columns.SRS_CODIGO));
    }
 
    private String resolveFlagSalario(TransferObject r, AcessoSistema responsavel) {
    final BigDecimal rseSalario = (BigDecimal) r.getAttribute(Columns.RSE_SALARIO);
    if (rseSalario != null && rseSalario.signum() > 0) {
        return CodedValues.SAL_FOLHA;
    }
 
    final String rseCodigo = r.getAttribute(Columns.RSE_CODIGO).toString();
    final TransferObject margem;
    try {
        margem = margemController.lstMargemIncideEmprestimo(rseCodigo, responsavel);
    } catch (Exception ex) {
        return CodedValues.SAL_NAO_CALCULAVEL;
    }
    if (margem == null) return CodedValues.SAL_NAO_CALCULAVEL;
        final BigDecimal marPorc = (BigDecimal) margem.getAttribute(Columns.MAR_PORCENTAGEM);
        return (marPorc != null && marPorc.signum() > 0) ? CodedValues.SAL_CALCULADO : CodedValues.SAL_NAO_CALCULAVEL;
    }
 
   
 
    private ConsultarSalarioResponse consultarSalarioConvert(TransferObject ser) {
        final ConsultarSalarioResponse response = new ConsultarSalarioResponse();
        response.setMatricula((String) ser.getAttribute(Columns.RSE_MATRICULA));
        response.setNome((String) ser.getAttribute(Columns.SER_NOME));
        response.setCpf((String) ser.getAttribute(Columns.SER_CPF));
        response.setConsignanteNome((String) ser.getAttribute(Columns.CSE_NOME));
        response.setConsignanteCnpj((String) ser.getAttribute(Columns.CSE_CNPJ));
        response.setMunicipioLotacao((String) ser.getAttribute(Columns.RSE_MUNICIPIO_LOTACAO));
        response.setCargo((String) ser.getAttribute(Columns.CRS_DESCRICAO));
        response.setSalario((BigDecimal) ser.getAttribute(Columns.RSE_SALARIO));
        response.setProventos((BigDecimal) ser.getAttribute(Columns.RSE_PROVENTOS));
        response.setCompulsorios((BigDecimal) ser.getAttribute(Columns.RSE_DESCONTOS_COMP));
        response.setFacultativos((BigDecimal) ser.getAttribute(Columns.RSE_DESCONTOS_FACU));
        response.setOutros((BigDecimal) ser.getAttribute(Columns.RSE_OUTROS_DESCONTOS));
        response.setDataUltSalario(ser.getAttribute(Columns.RSE_DATA_ULT_SALARIO) != null
                ? DateHelper.format((Date) ser.getAttribute(Columns.RSE_DATA_ULT_SALARIO), "yyyy-MM-dd")
                : null);
        response.setDataDesligamento(ser.getAttribute(Columns.RSE_DATA_SAIDA) != null
                ? DateHelper.format((Date) ser.getAttribute(Columns.RSE_DATA_SAIDA), "yyyy-MM-dd")
                : null);
        response.setDataAdmissao(ser.getAttribute(Columns.RSE_DATA_ADMISSAO) != null
                ? DateHelper.format((Date) ser.getAttribute(Columns.RSE_DATA_ADMISSAO), "yyyy-MM-dd")
                : null);
        response.setDataPagamento(ser.getAttribute(Columns.RSE_DATA_ULT_SALARIO) != null
                ? DateHelper.format((Date) ser.getAttribute(Columns.RSE_DATA_ULT_SALARIO), "yyyy-MM-dd")
                : null);
        response.setAnoMes(ser.getAttribute(Columns.RSE_DATA_ULT_SALARIO) != null
                ? new SimpleDateFormat("yyyy-MM").format((Date) ser.getAttribute(Columns.RSE_DATA_ULT_SALARIO))
                : null);
 
        return response;
    }
 
    private BigDecimal calcularSalarioPelaMargem(TransferObject margem, BigDecimal totalConsignacoes,
                                                 AcessoSistema responsavel) {
        final BigDecimal marPercentual = margem.getAttribute(Columns.MAR_PORCENTAGEM) != null
                ? (BigDecimal) margem.getAttribute(Columns.MAR_PORCENTAGEM)
                : BigDecimal.ZERO;
        final BigDecimal margemRest = margem.getAttribute(Columns.MRS_MARGEM_REST) != null
                ? (BigDecimal) margem.getAttribute(Columns.MRS_MARGEM_REST)
                : BigDecimal.ZERO;
 
        if (marPercentual.signum() <= 0) {
            return BigDecimal.ZERO;
        }
 
        // Soma o total de consignações com a margem restante
        final BigDecimal margemBruta = totalConsignacoes.add(margemRest);
 
        // Faz a regra de três para obter o valor aproximado do salário pela margem
        // bruta
        return margemBruta.multiply(BigDecimal.valueOf(100.00)).divide(marPercentual, RoundingMode.HALF_UP);
    }
 
}