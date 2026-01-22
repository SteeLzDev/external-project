package com.zetra.econsig.webservice.rest.service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.LeilaoSolicitacaoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.persistence.entity.AnexoAutorizacaoDesconto;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.EditarAnexoConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.leilao.LeilaoSolicitacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.rest.RestService;
import com.zetra.econsig.webservice.rest.Secured;
import com.zetra.econsig.webservice.rest.request.AnexoAutDescontoRestRequest;

/**
 * <p>Title: AnexoConsignacaoService</p>
 * <p>Description: Serviço REST para tratar anexos de consignação</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Path("/anexoConsignacao")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class AnexoConsignacaoService extends RestService {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AnexoConsignacaoService.class);

    @Context
    SecurityContext securityContext;

    @POST
    @Secured
    @Path("/incluir")
    public Response incluirAnexoConsignacao(AnexoAutDescontoRestRequest dados, @Context
    HttpServletRequest request) throws ZetraException {
        AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

        // Verifica se veio algum dado preenchido, e se não retorna.
        if (dados == null || TextHelper.isNull(dados.adeCodigo) || TextHelper.isNull(dados.conteudo)) {
            return genericError(new ZetraException("mensagem.rest.parametros.ausente", responsavel));
        }

        if (!responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO)) {
            throw new AutorizacaoControllerException("mensagem.erro.usuario.sem.permissao.autorizar.operacao", responsavel);
        }

        PesquisarConsignacaoController pesquisarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);
        LeilaoSolicitacaoController leilaoSolicitacaoController = ApplicationContextProvider.getApplicationContext().getBean(LeilaoSolicitacaoController.class);
        EditarAnexoConsignacaoController editarAnexoConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(EditarAnexoConsignacaoController.class);

        CustomTransferObject autdes;
        try {
            autdes = pesquisarConsignacaoController.buscaAutorizacao(dados.adeCodigo, responsavel);
        } catch (AutorizacaoControllerException ex) {
            return genericError(ex);
        }

        //Anexos
        byte[] arq = Base64.decodeBase64(dados.conteudo);
        String path = "anexo" + File.separatorChar + DateHelper.format((Date) autdes.getAttribute(Columns.ADE_DATA), "yyyyMMdd") + File.separatorChar + dados.adeCodigo;
        File arquivoAnexo = new File(ParamSist.getDiretorioRaizArquivos() + File.separatorChar + path + File.separatorChar + dados.nome);

        // Verifica se existe o registro no banco relacionando o anexo à consignação, e caso não exista, deixa sobrepor
        AnexoAutorizacaoDesconto aad = editarAnexoConsignacaoController.findAnexoAutorizacaoDesconto(dados.adeCodigo, dados.nome, responsavel);
        if (aad != null) {
            return genericError(new ZetraException("mensagem.erro.anexo.ja.existe", responsavel));
        } else {
            // O arquivo existe porém não está associado à consignação. Remove o anexo do disco
            arquivoAnexo.delete();
        }

        try {
            FileUtils.writeByteArrayToFile(arquivoAnexo, arq);
        } catch (IOException e) {
            arquivoAnexo.delete();
            return genericError(new ZetraException("mensagem.erro.interno.contate.administrador", responsavel));
        }

        String paramTamMaxArqAnexo = (String) ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_ARQ_ANEXO_CONTRATO_ASSINATURA_DIGITAL, responsavel);
        int tamMaxArqAnexoAssDigital = (!TextHelper.isNull(paramTamMaxArqAnexo) ? (Integer.valueOf(paramTamMaxArqAnexo)).intValue() : 200);

        if (arquivoAnexo.length() / 1024 > tamMaxArqAnexoAssDigital) {
            arquivoAnexo.delete();
            return genericError(new ZetraException("mensagem.erro.arquivo.tamanho.maximo", responsavel, String.valueOf(tamMaxArqAnexoAssDigital) + "KB"));
        }

        //Valido a extensão dos documentos anexados para o credito eletrônico.
        boolean isOrigemLeilao = false;
        try {
            isOrigemLeilao = leilaoSolicitacaoController.temSolicitacaoLeilao(dados.adeCodigo, true, responsavel);
        } catch (LeilaoSolicitacaoControllerException ex) {
            arquivoAnexo.delete();
            return genericError(ex);
        }

        boolean exigeAssinaturaDigital = ParamSist.paramEquals(CodedValues.TPC_HABILITA_ASSINATURA_DIGITAL_CONSIGNACAO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        //DESENV-18005 especifica esse tipo fixo
        TipoArquivoEnum tipoArquivo = TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_GENERICO;

        String[] extensoesArquivoPermitidas;
        if (exigeAssinaturaDigital && !isOrigemLeilao) {
            extensoesArquivoPermitidas = UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_CREDITO_ELETRONICO;
        } else {
            extensoesArquivoPermitidas = UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_CONTRATO;
        }

        // Verifica se a extensão do arquivo é válida.
        if (extensoesArquivoPermitidas != null) {
            boolean extensaoValida = false;
            for (String extensoesArquivoPermitida : extensoesArquivoPermitidas) {
                if (dados.nome.toLowerCase().endsWith(extensoesArquivoPermitida.toLowerCase())) {
                    extensaoValida = true;
                    break;
                }
            }

            if (!extensaoValida) {
                arquivoAnexo.delete();
                return genericError(new ZetraException("mensagem.erro.copia.impossivel.arquivos.permitidos", responsavel, TextHelper.join(extensoesArquivoPermitidas, ", ")));
            }
        }

        try {
            String aadDescricao = TextHelper.forHtmlContent(dados.descricao);
            aadDescricao = (!TextHelper.isNull(aadDescricao) && aadDescricao.length() <= 255) ? aadDescricao : dados.nome;

            java.sql.Date aadPeriodo = PeriodoHelper.getInstance().getPeriodoAtual(autdes.getAttribute(Columns.ORG_CODIGO).toString(), responsavel);
            Date periodoContrato = (Date) autdes.getAttribute(Columns.ADE_ANO_MES_INI);
            java.sql.Date periodoContratoSql = DateHelper.toSQLDate(periodoContrato);

            if (aadPeriodo == null) {
                aadPeriodo = PeriodoHelper.getInstance().getPeriodoAtual(autdes.getAttribute(Columns.ORG_CODIGO).toString(), responsavel);
            }

            if (periodoContratoSql.compareTo(aadPeriodo) > 0) {
                aadPeriodo = periodoContratoSql;
            }

            editarAnexoConsignacaoController.createAnexoAutorizacaoDesconto(dados.adeCodigo, arquivoAnexo.getName(), aadDescricao, aadPeriodo, tipoArquivo, responsavel);
        } catch (Exception ex) {
            arquivoAnexo.delete();
            return genericError(ex);
        }

        return listarAnexoConsignacao(dados, request);
    }

    @POST
    @Secured
    @Path("/listar")
    public Response listarAnexoConsignacao(AnexoAutDescontoRestRequest dados, @Context
    HttpServletRequest request) throws ZetraException {
        AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();
        List<TransferObject> anexos;

        // Verifica se veio algum dado preenchido, e se não retorna.
        if (dados == null || TextHelper.isNull(dados.adeCodigo)) {
            return genericError(new ZetraException("mensagem.rest.parametros.ausente", responsavel));
        }

        EditarAnexoConsignacaoController editarAnexoConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(EditarAnexoConsignacaoController.class);
        AutorizacaoController autorizacaoController = ApplicationContextProvider.getApplicationContext().getBean("autorizacaoController", AutorizacaoController.class);

        // Busca os anexos do contrato
        try {
            boolean usuarioPodeModificarAde = autorizacaoController.usuarioPodeModificarAde(dados.adeCodigo, false, false, responsavel);
            boolean usuarioPodeConsultarAde = (usuarioPodeModificarAde || autorizacaoController.usuarioPodeConsultarAde(dados.adeCodigo, responsavel));

            if (!usuarioPodeConsultarAde) {
                return genericError(new ZetraException("mensagem.erro.include.get.file.permissao.anexo", responsavel));
            }

            CustomTransferObject cto = new CustomTransferObject();
            cto.setAttribute(Columns.AAD_ADE_CODIGO, dados.adeCodigo);
            cto.setAttribute(Columns.AAD_ATIVO, CodedValues.STS_ATIVO);

            anexos = editarAnexoConsignacaoController.lstAnexoAutorizacaoDesconto(cto, -1, -1, responsavel);

        } catch (AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            return genericError(ex);
        }

        List<String> filter = Arrays.asList("tar_descricao", "ade_codigo", "aad_ativo", "tar_codigo", "aad_nome", "ade_numero", "aad_data", "ade_data", "ade_numero", "aad_descricao", "usu_login", "aad_periodo");
        return Response.status(Response.Status.OK).entity(transformTOs(anexos, filter)).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
    }

    @POST
    @Secured
    @Path("/download")
    public Response downloadAnexoConsignacao(AnexoAutDescontoRestRequest dados, @Context
    HttpServletRequest request) throws ZetraException {
        AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

        // Verifica se veio algum dado preenchido, e se não retorna.
        if (dados == null || TextHelper.isNull(dados.adeCodigo) || TextHelper.isNull(dados.nome)) {
            return genericError(new ZetraException("mensagem.rest.parametros.ausente", responsavel));
        }

        AutorizacaoController autorizacaoController = ApplicationContextProvider.getApplicationContext().getBean("autorizacaoController", AutorizacaoController.class);
        PesquisarConsignacaoController pesquisarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);
        AnexoAutDescontoRestRequest retorno = new AnexoAutDescontoRestRequest();

        // Busca os anexos do contrato
        try {
            boolean usuarioPodeModificarAde = autorizacaoController.usuarioPodeModificarAde(dados.adeCodigo, false, false, responsavel);
            boolean usuarioPodeConsultarAde = (usuarioPodeModificarAde || autorizacaoController.usuarioPodeConsultarAde(dados.adeCodigo, responsavel));

            if (!usuarioPodeConsultarAde) {
                return genericError(new ZetraException("mensagem.erro.include.get.file.permissao.anexo", responsavel));
            }

            CustomTransferObject autdes = pesquisarConsignacaoController.buscaAutorizacao(dados.adeCodigo, responsavel);
            File arquivo = new File(ParamSist.getDiretorioRaizArquivos() + File.separatorChar + "anexo" + File.separatorChar + DateHelper.format((Date) autdes.getAttribute(Columns.ADE_DATA), "yyyyMMdd") + File.separatorChar + dados.adeCodigo + File.separatorChar + dados.nome);

            if (!arquivo.exists()) {
                return genericError(new ZetraException("mensagem.erro.anexo.nao.existe", responsavel));
            }

            retorno = new AnexoAutDescontoRestRequest();
            retorno.adeCodigo = dados.adeCodigo;
            retorno.nome = dados.nome;
            retorno.conteudo = Base64.encodeBase64String(FileUtils.readFileToByteArray(arquivo));

        } catch (AutorizacaoControllerException | IOException ex) {
            LOG.error(ex.getMessage(), ex);
            return genericError(ex);
        }

        HashMap<String, String> retornoMap = new HashMap<>();
        retornoMap.put("ade_codigo", retorno.adeCodigo);
        retornoMap.put("aad_nome", retorno.nome);
        retornoMap.put("conteudo", retorno.conteudo);

        JSONObject retornoJson = new JSONObject(retornoMap);

        return Response.status(Response.Status.OK).entity(retornoJson, null).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
    }

    @POST
    @Secured
    @Path("/excluir")
    public Response excluirAnexoConsignacao(AnexoAutDescontoRestRequest dados, @Context
    HttpServletRequest request) throws ZetraException {
        AcessoSistema responsavel = (AcessoSistema) securityContext.getUserPrincipal();

        // Verifica se veio algum dado preenchido, e se não retorna.
        if (dados == null || TextHelper.isNull(dados.adeCodigo) || TextHelper.isNull(dados.nome)) {
            return genericError(new ZetraException("mensagem.rest.parametros.ausente", responsavel));
        }

        PesquisarConsignacaoController pesquisarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);
        EditarAnexoConsignacaoController editarAnexoConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(EditarAnexoConsignacaoController.class);

        CustomTransferObject autdes = pesquisarConsignacaoController.buscaAutorizacao(dados.adeCodigo, responsavel);
        CustomTransferObject cto = new CustomTransferObject();
        cto.setAttribute(Columns.AAD_NOME, dados.nome);
        cto.setAttribute(Columns.AAD_ADE_CODIGO, dados.adeCodigo);
        cto.setAttribute(Columns.ADE_DATA, DateHelper.format((Date) autdes.getAttribute(Columns.ADE_DATA), "yyyyMMdd"));
        try {
            editarAnexoConsignacaoController.removeAnexoAutorizacaoDesconto(cto, responsavel);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return genericError(ex);
        }

        return listarAnexoConsignacao(dados, request);
    }

}
