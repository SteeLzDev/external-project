package com.zetra.econsig.web.controller.arquivo;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.criptografia.CriptografiaArquivos;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.MimeDetector;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.AnexoAutorizacaoDesconto;
import com.zetra.econsig.service.consignacao.DownloadAnexoContratoController;
import com.zetra.econsig.service.consignacao.EditarAnexoConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.controller.FileAbstractWebController;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.criptografia.CriptografiaArquivos;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.MimeDetector;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.AnexoAutorizacaoDesconto;
import com.zetra.econsig.service.consignacao.DownloadAnexoContratoController;
import com.zetra.econsig.service.consignacao.EditarAnexoConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.controller.FileAbstractWebController;

import eu.medsea.mimeutil.MimeType;

@Controller
public class DownloadAnexoConsignacaoWebController extends FileAbstractWebController {

    @Autowired
    private EditarAnexoConsignacaoController editarAnexoConsignacaoController;

    @Autowired
    private DownloadAnexoContratoController downloadAnexoContratoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DownloadAnexoConsignacaoWebController.class);

    @RequestMapping(method={RequestMethod.POST}, value={"/v3/verificarAnexoContratoConsignacao"}, params={"tipo=anexo"}, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> verificarAnexoContratoConsignacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws IOException, InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        JsonObjectBuilder result = Json.createObjectBuilder();
        request.setAttribute("msg", ApplicationResourcesHelper.getMessage("mensagem.erro.download.anexo.consignacao", responsavel));

        FileStatus status = processar(request, response, session, model);
        if (!status.getResultado()) {
            session.setAttribute(CodedValues.MSG_ERRO, status.getMensagemErro());
            result.add("statusArquivo", false);
            return new ResponseEntity<>(result.build().toString(), HttpStatus.OK);
        } else {
            result.add("statusArquivo", true);
            return new ResponseEntity<>(result.build().toString(), HttpStatus.OK);
        }
    }

    @RequestMapping(method={RequestMethod.POST}, value={"/v3/downloadAnexoContratoConsignacao"}, params={"tipo=anexo"})
    public void downloadAnexoContratoConsignacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws IOException, InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        FileStatus status = processar(request, response, session, model);
        File arquivo = status.getArquivo();
        boolean removeArquivo = false;

        if (arquivo.getName().endsWith(".crypt")) {
            File arquivoPlano = CriptografiaArquivos.descriptografarArquivo(arquivo.getAbsolutePath(), false, responsavel);
            if (arquivoPlano != null) {
                arquivo = arquivoPlano;
                removeArquivo = true;
            }
        }

        // Gera log de download de arquivo
        try {
            LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.DOWNLOAD_FILE, Log.LOG_INFORMACAO);
            log.add(ApplicationResourcesHelper.getMessage("rotulo.download.arquivo.log", responsavel) + ": " + arquivo.getAbsolutePath());
            log.write();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.compact.erro.interno", responsavel) + ": " + ex.getMessage());
        }

        Set<MimeType> mimeSet = MimeDetector.MIMEUTIL.detect(arquivo.getAbsolutePath());
        String mime = mimeSet != null && mimeSet.size() > 0 ? mimeSet.toArray()[0].toString() : "APPLICATION/OCTET-STREAM";
        response.setContentType(mime);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + arquivo.getName() + "\"");

        long tamanhoArquivoBytes = arquivo.length();
        BufferedInputStream entrada = new BufferedInputStream(new FileInputStream(arquivo));
        if (tamanhoArquivoBytes > Integer.MAX_VALUE) {
            response.addHeader("Content-Length", Long.toString(tamanhoArquivoBytes));
            IOUtils.copyLarge(entrada, response.getOutputStream());
        } else {
            response.setContentLength((int) tamanhoArquivoBytes);
            IOUtils.copy(entrada, response.getOutputStream());
        }

        response.flushBuffer();
        entrada.close();

        if (removeArquivo) {
            arquivo.delete();
        }
    }

    @RequestMapping(method={RequestMethod.POST}, value={"/v3/downloadAnexoValidarDocumentos"}, params={"tipo=anexo"})
    public void downloadAnexoValidarDocumentos(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws IOException, InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            String adeCodigo = JspHelper.verificaVarQryStr(request, "ADE_CODIGO");
            String adeData = JspHelper.verificaVarQryStr(request, "ADE_DATA");
            Date periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(null, responsavel);

            List<String> tarCodigos = new ArrayList<>();
            tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_RG.getCodigo());
            tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_PAGAMENTO.getCodigo());
            tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_CONTRACHEQUE.getCodigo());
            tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_OUTRO.getCodigo());

            String pathFile = ParamSist.getDiretorioRaizArquivos()
                    + File.separatorChar + "anexo" + File.separatorChar + adeData
                    + File.separatorChar + adeCodigo + File.separatorChar;

            List<String> arquivos = new ArrayList<>();

            List<AnexoAutorizacaoDesconto> lstAnexo = editarAnexoConsignacaoController.lstAnexoTipoArquivoPeriodo(adeCodigo, tarCodigos, periodoAtual, responsavel);

            // Caso não exista arquivo para o periodo atual, precisamos buscar o mais recente
            if(lstAnexo == null || lstAnexo.isEmpty()) {
                lstAnexo = editarAnexoConsignacaoController.lstAnexoTipoArquivoMaxPeriodo(adeCodigo, tarCodigos, responsavel);
            }

            for(AnexoAutorizacaoDesconto anexo : lstAnexo) {
                String aadNome = anexo.getAadNome();
                arquivos.add(pathFile + aadNome);
            }

            CustomTransferObject autDesconto = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);

            //campos que podem ser usados para montar o padrão de nome de arquivo
            HashMap<String, String> campos = new HashMap<>();
            campos.put("rse_matricula", (String) autDesconto.getAttribute(Columns.RSE_MATRICULA));
            campos.put("ser_cpf", (String) autDesconto.getAttribute(Columns.SER_CPF));
            campos.put("ser_nome", (String) autDesconto.getAttribute(Columns.SER_NOME));
            campos.put("ade_numero", String.valueOf(autDesconto.getAttribute(Columns.ADE_NUMERO)));
            campos.put("cnv_cod_verba", String.valueOf(autDesconto.getAttribute(Columns.CNV_COD_VERBA)));
            campos.put("ade_identificador", String.valueOf(autDesconto.getAttribute(Columns.ADE_IDENTIFICADOR)));
            campos.put("ade_indice", String.valueOf(autDesconto.getAttribute(Columns.ADE_INDICE)));

            String nomeFinalZip = downloadAnexoContratoController.geraNomeAnexosPeriodo(campos, ".zip", null, true, responsavel);
            String arquivoZip = pathFile+nomeFinalZip;
            FileHelper.zip(arquivos, arquivoZip);

            File arquivo = new File(arquivoZip);

            LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.DOWNLOAD_FILE, Log.LOG_INFORMACAO);
            log.add(ApplicationResourcesHelper.getMessage("rotulo.download.arquivo.log", responsavel) + ": " + arquivo.getAbsolutePath());
            log.write();

            Set<MimeType> mimeSet = MimeDetector.MIMEUTIL.detect(arquivo.getAbsolutePath());
            String mime = mimeSet != null && mimeSet.size() > 0 ? mimeSet.toArray()[0].toString() : "APPLICATION/OCTET-STREAM";
            response.setContentType(mime);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + arquivo.getName() + "\"");

            long tamanhoArquivoBytes = arquivo.length();
            BufferedInputStream entrada = new BufferedInputStream(new FileInputStream(arquivo));
            if (tamanhoArquivoBytes > Integer.MAX_VALUE) {
                response.addHeader("Content-Length", Long.toString(tamanhoArquivoBytes));
                IOUtils.copyLarge(entrada, response.getOutputStream());
            } else {
                response.setContentLength((int) tamanhoArquivoBytes);
                IOUtils.copy(entrada, response.getOutputStream());
            }

            response.flushBuffer();
            entrada.close();

            FileHelper.delete(arquivoZip);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.compact.erro.interno", responsavel) + ": " + ex.getMessage());
        }
    }
}
