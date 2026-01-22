package com.zetra.econsig.helper.upload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.core.FileItem;
import org.apache.commons.fileupload2.core.FileUploadException;
import org.apache.commons.fileupload2.core.FileUploadSizeException;
import org.apache.commons.fileupload2.core.ProgressListener;
import org.apache.commons.fileupload2.jakarta.JakartaFileCleaner;
import org.apache.commons.fileupload2.jakarta.JakartaServletFileUpload;
import org.aspectj.weaver.patterns.ParserException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.CharsetDetector;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.parser.EscritorArquivoTexto;
import com.zetra.econsig.parser.LeitorArquivoTexto;
import com.zetra.econsig.parser.Tradutor;
import com.zetra.econsig.persistence.entity.TipoArquivo;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.service.upload.UploadController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.web.filter.XSSPreventionFilter;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: UploadHelper</p>
 * <p>Description: Helper Class para upload de arquivos.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class UploadHelper {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(UploadHelper.class);

    public static final String[] EXTENSOES_PERMITIDAS_ANEXO_CONTRATO  = {".gif", ".jpg", ".jpeg", ".jpe", ".jfif", ".jfi", ".png", ".pdf", ".rtf", ".doc", ".docx", ".xls", ".xlsx", ".txt", ".csv", ".zip", ".webm", ".mp4", ".fad", ".wma", ".mp3", ".key", ".fida"};
    public static final String[] EXTENSOES_PERMITIDAS_ANEXO_CONTRATO_INTEGRACAO  = {".gif", ".jpg", ".jpeg", ".jpe", ".jfif", ".jfi", ".png", ".pdf", ".rtf", ".doc", ".docx", ".xls", ".xlsx", ".txt", ".csv", ".zip", ".webm", ".mp4", ".wma", ".mp3"};
    public static final String[] EXTENSOES_PERMITIDAS_ANEXO_COMUNICACAO  = {".gif", ".jpg", ".jpeg", ".jpe", ".jfif", ".jfi", ".pdf", ".rtf", ".doc"};
    public static final String[] EXTENSOES_PERMITIDAS_UPLOAD_GENERICO = {".doc", ".pdf", ".xls", ".docx", ".xlsx", ".txt", ".csv"};
    public static final String[] EXTENSOES_PERMITIDAS_UPLOAD_RECUPERACAO_CREDITO = {".xls", ".xlsx", ".txt", ".csv"};
    public static final String[] EXTENSOES_PERMITIDAS_UPLOAD_BANNER   = {".gif", ".jpg", ".jpeg", ".jpe", ".jfif", ".jfi"};
    public static final String[] EXTENSOES_PERMITIDAS_ANEXO_SALDO_DEVEDOR  = {".gif", ".jpg", ".jpeg", ".jpe", ".jfif", ".jfi", ".pdf"};
    public static final String[] EXTENSOES_PERMITIDAS_ANEXO_TXT  = {".txt"};
    public static final String[] EXTENSOES_PERMITIDAS_ANEXO_CREDITO_ELETRONICO  = {".jpg", ".jpeg", ".jpe", ".jfif", ".jfi", ".png", ".pdf"};
    public static final String[] EXTENSOES_PERMITIDAS_ARQUIVO_DE_LOTE_ANEXO  = {".zip"};
    public static final String[] EXTENSOES_PERMITIDAS_BOLETO_SERVIDOR  = {".zip"};
    public static final String[] EXTENSOES_PERMITIDAS_BOLETO_SERVIDOR_CONTEUDO = {".zip", ".gif", ".jpg", ".jpeg", ".jpe", ".jfif", ".jfi", ".png", ".pdf", ".rtf", ".doc", ".txt"};
    public static final String[] EXTENSOES_PERMITIDAS_DISPENSA_DIGITAL  = {".gif", ".jpg", ".jpeg", ".jpe", ".jfif", ".jfi", ".png", ".pdf", ".rtf", ".doc"};
    public static final String[] EXTENSOES_PERMITIDAS_ANEXO_REGISTRO_SERVIDOR = {".doc", ".pdf", ".docx"};
    public static final String[] EXTENSOES_PERMITIDAS_ARQUIVO_MENSAGEM = {".jpg", ".jpeg", ".jpe", ".jfif", ".jfi", ".png", ".pdf"};
    public static final String[] EXTENSOES_PERMITIDAS_ANEXO_PRE_VISUALIZAR = {".gif", ".jpg", ".jpeg", ".jpe", ".jfif", ".jfi", ".png", ".pdf", ".rtf", ".doc", ".docx", ".xls", ".xlsx", ".txt", ".csv", ".zip", ".fad"};
    public static final String[] EXTENSOES_PERMITIDAS_ANEXO_PRE_VISUALIZAR_AUDIO_VIDEO_ZIP = {".webm", ".mp4", ".wma", ".mp3", ".zip"};
    public static final String[] EXTENSOES_PERMITIDAS_ANEXO_AUDIO_VIDEO_ZIP = {".pdf", ".key", ".fida"};
    public static final String[] EXTENSOES_PERMITIDAS_ANEXO_PRE_VISUALIZAR_AUDIO_VIDEO = {".webm", ".mp4", ".wma", ".mp3"};
    public static final String[] EXTENSOES_PERMITIDAS_DOCUMENTOS_CREDENCIAMENTO  = {".gif", ".jpg", ".jpeg", ".jpe", ".jfif", ".jfi", ".png", ".pdf", ".rtf", ".doc", ".docx", ".zip"};
    public static final String[] EXTENSOES_PERMITIDAS_UPLOAD_RELATORIO_CUSTOMIZADO  = {".txt", ".pdf", ".xls", ".csv", ".doc", ".docx", ".xlsx", ".zip"};
    public static final String[] EXTENSOES_PERMITIDAS_PARA_CONVERSAO_TXT_RETORNO_ORGAO  = {".doc", ".docx", ".xls", ".xlsx", ".pdf", ".csv"};
    public static final String[] EXTENSOES_PERMITIDAS_UPLOAD_CONSIGNATARIA = {".docx", ".jfif", ".doc", ".rtf", ".pdf", ".png", ".jfi", ".jpe", ".jpg", ".gif", ".jpeg"};
    public static final String[] EXTENSOES_PERMITIDAS_DOWNLOAD_COPIA_SEGURANCA = {".zip", ".gz", ".gpg"};
    public static final String[] EXTENSOES_PERMITIDAS_SUBRELATORIO = {".jrxml"};
    public static final String[] EXTENSOES_PERMITIDAS_CREDITO_TRABALHADOR = {".csv", ".xlsx", ".json"};
    public static final String[] EXTENSOES_PERMITIDAS_ANEXO_APENAS_IMAGENS = {".gif", ".jpg", ".jpeg", ".jpe", ".jfif", ".jfi", ".png"};

    // Tamanho máximo de arquivo cujo upload será feito na memória.
    private static final int MAX_TAMANHO_ARQ_MEMORIA = 256;

    // Subdiretório onde os arquivos temporários são armazenados.
    public static final String SUBDIR_ARQUIVOS_TEMPORARIOS = "temp" + File.separator + "upload";

    private String diretorioRaizArquivos;
    private AcessoSistema responsavel;
    private final Map<String, List<String>> camposFormulario = new HashMap<>();
    private final List<FileItem<?>> arquivosCarregados = new ArrayList<>();
    private JakartaServletFileUpload servletFileUpload = null;
    private File primeiroArquivoMovido = null;
    private boolean requisicaoUpload = true;
    private final List<String> nomeCamposArquivos  = new ArrayList<>();

    public UploadHelper () {
        // let it go
    }

    /**
     * Processa a requisição de upload.
     * @param context
     * @param request
     * @param tamanhoMaximoArquivoUpload
     * @param responsavel
     * @throws ZetraException
     */
    public void processarRequisicao(ServletContext context, HttpServletRequest request, int tamanhoMaximoArquivoUpload) throws ZetraException {
        responsavel = JspHelper.getAcessoSistema(request);

        diretorioRaizArquivos = ParamSist.getDiretorioRaizArquivos();
        if (diretorioRaizArquivos == null) {
            throw new ZetraException("mensagem.erro.configuracao.diretorio.integracao.invalida", responsavel);
        }

        // Se não é uma requisição de upload, não há mais o que fazer.
        if (!JakartaServletFileUpload.isMultipartContent(request)) {
            requisicaoUpload = false;
            return;
        }

        final File diretorioTemporarioArquivos = new File(diretorioRaizArquivos + File.separatorChar + SUBDIR_ARQUIVOS_TEMPORARIOS);
        if (!diretorioTemporarioArquivos.exists() && !diretorioTemporarioArquivos.mkdirs()) {
            LOG.error("Não foi possível criar diretório para os arquivos temporários de upload.");
            throw new ZetraException("mensagem.erroInternoSistema", responsavel);
        }

        final DiskFileItemFactory diskFileItemFactory = DiskFileItemFactory.builder()
                .setFileCleaningTracker(JakartaFileCleaner.getFileCleaningTracker(context))
                .setBufferSize(MAX_TAMANHO_ARQ_MEMORIA)
                .setPath(diretorioTemporarioArquivos.getPath())
                .get();

        try {
            servletFileUpload = new JakartaServletFileUpload<>(diskFileItemFactory);
            servletFileUpload.setFileSizeMax(tamanhoMaximoArquivoUpload);
            servletFileUpload.setProgressListener(getProgressListener());
            servletFileUpload.setHeaderCharset(StandardCharsets.UTF_8);

            // Recupera os itens do request.
            final List<FileItem<?>> itensUpload = servletFileUpload.parseRequest(request);
            for (final FileItem<?> itemUpload : itensUpload) {

                if (itemUpload.isFormField()) {
                    final String nomeCampoFormulario = itemUpload.getFieldName();
                    List<String> valoresCampoFormulario = camposFormulario.get(XSSPreventionFilter.stripXSS(nomeCampoFormulario));
                    if (valoresCampoFormulario == null) {
                        valoresCampoFormulario = new ArrayList<>();
                        camposFormulario.put(nomeCampoFormulario, valoresCampoFormulario);
                    }
                    // final String valorCampoFormulario = Streams.asString(itemUpload.getInputStream(), "UTF-8");
                    final String valorCampoFormulario = new String(itemUpload.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                    valoresCampoFormulario.add(XSSPreventionFilter.stripXSS(tratarValorCampo(valorCampoFormulario)));
                } else {
                    nomeCamposArquivos.add(itemUpload.getFieldName());
                    arquivosCarregados.add(itemUpload);
                }
            }
        } catch (final Exception ex) {
            if (ex instanceof FileUploadSizeException) {
                final LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.UPLOAD_FILE, Log.LOG_ERRO);
                if (!TextHelper.isNull(getValorCampoFormulario("EST_CODIGO"))) {
                    log.setEstabelecimento(getValorCampoFormulario("EST_CODIGO"));
                }
                if (!TextHelper.isNull(getValorCampoFormulario("ORG_CODIGO"))) {
                    log.setOrgao(getValorCampoFormulario("ORG_CODIGO"));
                }
                log.add(ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.tamanho.maximo", responsavel, String.valueOf(request.getContentLength())));
                log.write();

                throw new ZetraException("mensagem.erro.arquivo.tamanho.maximo", responsavel, String.valueOf(request.getContentLength()));
            } else if ((ex instanceof FileUploadException) && (ex.getCause() != null) && ex.getCause().getClass().getName().contains("RequestTooBigException")) {
                LOG.error(ex.getMessage(), ex);
                throw new ZetraException("mensagem.erro.arquivo.tamanho.maximo", responsavel, String.valueOf(request.getContentLength()));
            } else {
                LOG.error(ex.getMessage(), ex);
                throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    /**
     * Metodo que codifica param/atributos do request para ISO-8859-1 para persistência na base eConsig.
     * @param valor
     * @return
     */
    public String tratarValorCampo(String valor) {
        try {
            if ((valor != null) && CharsetDetector.detectString(valor).toUpperCase().contains("UTF-8")) {
                return StringUtils.newStringIso8859_1(StringUtils.getBytesIso8859_1(StringUtils.newStringUtf8(valor.getBytes())));
            } else {
                return valor;
            }
        } catch (final IOException e) {
            return valor;
        }
    }

    /**
     * Obtém o valor de um campo do formulário postado.
     * @param nomeCampoFormulario
     * @return
     */
    public String getValorCampoFormulario(String nomeCampoFormulario) {
        final List<String> valoresCampoFormulario = camposFormulario.get(nomeCampoFormulario);
        if (valoresCampoFormulario != null) {
            return valoresCampoFormulario.get(0);
        }
        return null;
    }

    /**
     * Obtém uma lista de valores de um campo do formulário postado.
     * @param nomeCampoFormulario
     * @return
     */
    public List<String> getValoresCampoFormulario(String nomeCampoFormulario) {
        return camposFormulario.get(nomeCampoFormulario);
    }

    public Map<String, List<String>> getValoresCampos() {
        return camposFormulario;
    }

    /**
     * Salva o arquivo carregado para o servidor em suas localizações definitivas.
     * @param subdiretoriosDestino
     * @param extensoesArquivoPermitidas
     * @throws ZetraException
     */
    public void salvarArquivo(List<String> subdiretoriosDestino, String[] extensoesArquivoPermitidas, String nomeArquivoRenomeado, HttpSession session) throws ZetraException {
        String subdiretorioDestino;
        for (final String element : subdiretoriosDestino) {
            subdiretorioDestino = element;
            this.salvarArquivo(subdiretorioDestino, extensoesArquivoPermitidas, nomeArquivoRenomeado, session);
        }
    }

    /**
     * @param subdiretorioDestino
     * @throws ZetraException
     */
    public File salvarArquivo(String subdiretorioDestino) throws ZetraException {
        return this.salvarArquivo(subdiretorioDestino, null, null);
    }

    /**
     * Salva o arquivo carregado para o servidor em sua localização definitiva.
     * @param subdiretorioDestino
     * @param extensoesArquivoPermitidas
     * @throws ZetraException
     */
    public File salvarArquivo(String subdiretorioDestino, String[] extensoesArquivoPermitidas) throws ZetraException {
        return salvarArquivo(subdiretorioDestino, extensoesArquivoPermitidas, null);
    }

    /**
     * Salva o arquivo de anexo da ADE, carregado em diretório temporário do servidor, em sua localização definitiva.
     * @param nomeArquivo
     * @param autdes
     * @param request
     * @throws ZetraException
     */
    public static File moverArquivoAnexoTemporario(String nomeArquivo, String adeCodigo, String hashDir, AcessoSistema responsavel) throws ZetraException {
    	return moverArquivoAnexoTemporario(nomeArquivo, adeCodigo, hashDir, null, responsavel);
    }

    public static File moverArquivoAnexoTemporario(String nomeArquivo, String adeCodigo, String hashDir, String nomeAquirvoFinal, AcessoSistema responsavel) throws ZetraException {
        return moverArquivoAnexoTemporario(nomeArquivo, adeCodigo, hashDir, nomeAquirvoFinal, false, null, responsavel);
    }

    /**
     * Salva o arquivo de anexo da ADE, carregado em diretório temporário do servidor, em sua localização definitiva.
     * @param nomeArquivo
     * @param nomeAquirvoFinal
     * @param autdes
     * @param request
     * @throws ZetraException
     */
    public static File moverArquivoAnexoTemporario(String nomeArquivo, String adeCodigo, String hashDir, String nomeAquirvoFinal, boolean validarRecursivamente, String[] extensoesArquivoPermitidas, AcessoSistema responsavel) throws ZetraException {

        final TransferObject autdes = (new AutorizacaoDelegate()).buscaAutorizacao(adeCodigo, responsavel);
        final String diretorioRaizArquivos = ParamSist.getDiretorioRaizArquivos();
        final File diretorioTemporario = new File(diretorioRaizArquivos + File.separator + SUBDIR_ARQUIVOS_TEMPORARIOS +
                File.separatorChar + "anexo" + File.separatorChar + hashDir);
        final File arquivoOrigem = new File(diretorioTemporario.getPath() + File.separator + nomeArquivo);
        final File diretorioDestino = new File(diretorioRaizArquivos + File.separator + "anexo" + File.separatorChar +
                DateHelper.format((Date)autdes.getAttribute(Columns.ADE_DATA), "yyyyMMdd") + File.separatorChar +
                (String) autdes.getAttribute(Columns.ADE_CODIGO));

        try {
            if ((validarRecursivamente && !TextHelper.isNull(extensoesArquivoPermitidas)) && !FileHelper.validaExtensaoRecursivamente(arquivoOrigem.getAbsolutePath(),extensoesArquivoPermitidas)){
                Files.delete(arquivoOrigem.toPath());
                if (arquivoOrigem.getAbsolutePath().endsWith(".zip")) {
                    throw new ZetraException("mensagem.erro.upload.conteudo.zip.extensoes.permitidas", responsavel, TextHelper.join(extensoesArquivoPermitidas, ","));
                } else {
                    throw new ZetraException("mensagem.erro.upload.conteudo.extensoes.permitidas", responsavel, TextHelper.join(extensoesArquivoPermitidas, ","));
                }
            }

            nomeArquivo = TextHelper.isNull(nomeAquirvoFinal) ? nomeArquivo : nomeAquirvoFinal;
            final File arquivoDestino = new File(diretorioDestino.getPath() + File.separator + nomeArquivo);


            if (TextHelper.isNull(nomeArquivo) || (autdes == null) || TextHelper.isNull(hashDir)) {
                return null;
            }
            // Este método aceita somente o nome do arquivo sem path
            if (!nomeArquivo.equals(nomeArquivo.substring(nomeArquivo.lastIndexOf('/') + 1)) || !nomeArquivo.equals(nomeArquivo.substring(nomeArquivo.lastIndexOf('\\') + 1))) {
                // Gera log de segurança
                try {
                    final LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.UPLOAD_FILE, Log.LOG_ERRO_SEGURANCA);
                    log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.upload.arquivo.malicioso.arg0", responsavel, nomeArquivo));
                    log.write();
                } catch (final LogControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                }

                throw new ZetraException("rotulo.upload.arquivo.invalido", responsavel);
            }
            if (!arquivoOrigem.exists()) {
                throw new ZetraException("mensagem.erro.upload.arquivo.arg0", responsavel, arquivoOrigem.getName());
            }
            if (diretorioDestino.exists() || diretorioDestino.mkdirs()) {
                FileHelper.copyFile(arquivoOrigem, arquivoDestino);
                arquivoOrigem.delete();
            } else {
                throw new ZetraException("mensagem.erro.upload", responsavel);
            }

            return arquivoDestino;

        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("mensagem.erro.upload", responsavel, ex);
        } finally {
            try {
                // Só remove o diretório se estiver vazio, para que upload de múltiplos arquivos funcione.
                // A limpeza do diretório de temporários é feita pelo processo ProcessaExclusaoArquivosAntigos
                if ((diretorioTemporario != null) && diretorioTemporario.exists() && (diretorioTemporario.list().length == 0)) {
                    FileHelper.deleteDir(diretorioTemporario.getPath());
                }
            } catch (final IOException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ZetraException("mensagem.erro.upload", responsavel, ex);
            }
        }
    }


    /**
     * Salva o arquivo de anexo da ADE, carregado em diretório temporário do servidor, em sua localização definitiva.
     * @param nomeArquivo
     * @param autdes
     * @param request
     * @throws ZetraException
     */
    public static File retornaArquivoAnexoTemporario(String nomeArquivo, String hashDir, AcessoSistema responsavel) throws ZetraException {

        final String diretorioRaizArquivos = ParamSist.getDiretorioRaizArquivos();
        final File diretorioTemporario = new File(diretorioRaizArquivos + File.separator + SUBDIR_ARQUIVOS_TEMPORARIOS +
                File.separatorChar + "anexo" + File.separatorChar + hashDir);
        final File arquivoOrigem = new File(diretorioTemporario.getPath() + File.separator + nomeArquivo);
        try {
            // Este método aceita somente o nome do arquivo sem path
            if (!nomeArquivo.equals(nomeArquivo.substring(nomeArquivo.lastIndexOf('/')+1)) || !nomeArquivo.equals(nomeArquivo.substring(nomeArquivo.lastIndexOf('\\')+1))) {
                // Gerar log de segurança?
                throw new ZetraException("rotulo.upload.arquivo.invalido", responsavel);
            }
            if (!arquivoOrigem.exists()) {
                throw new ZetraException("mensagem.erro.upload",responsavel);
            }

        } catch (final ZetraException e) {
            throw e;
        } catch (final Exception e) {
            throw new ZetraException("mensagem.erro.upload",responsavel);
        } finally {
            try {
                // Só remove o diretório se estiver vazio, para que upload de múltiplos arquivos funcione.
                // A limpeza do diretório de temporários é feita pelo processo ProcessaExclusaoArquivosAntigos
                if ((diretorioTemporario != null) && diretorioTemporario.exists() && (diretorioTemporario.list().length == 0)) {
                    FileHelper.deleteDir(diretorioTemporario.getPath());
                }
            } catch (final IOException e) {
                throw new ZetraException("mensagem.erro.upload",responsavel);
            }
        }
        return arquivoOrigem;
    }

    /**
     * Salva o arquivo carregado para o servidor em sua localização definitiva.
     * @param subdiretorioDestino
     * @param extensoesArquivoPermitidas
     * @param nomeArquivoRenomeado Caso seja informando, o arquivo a ser copiado será renomeado para o parâmetro passado
     * @throws ZetraException
     */
    public File salvarArquivo(String subdiretorioDestino, String[] extensoesArquivoPermitidas, String nomeArquivoRenomeado) throws ZetraException {
        return salvarArquivo(subdiretorioDestino, extensoesArquivoPermitidas, nomeArquivoRenomeado, null);
    }

    /**
     * Salva o arquivo carregado para o servidor em sua localização definitiva.
     * @param session - se for passado será exibida mensagem de renomeamento de arquivo apra o usuário
     * @throws ZetraException
     */
    public File salvarArquivo(String subdiretorioDestino, String[] extensoesArquivoPermitidas, String nomeArquivoRenomeado, HttpSession session) throws ZetraException {
        final Map<String, File> arquivosSalvos = salvarArquivos(subdiretorioDestino, extensoesArquivoPermitidas, nomeArquivoRenomeado, session, true);
        if ((arquivosSalvos != null) && (arquivosSalvos.size() > 0)) {
            return arquivosSalvos.values().iterator().next();
        }
        return null;
    }

    /**
     * Salva vários arquivos carregados para o servidor, retornando um mapa
     * onde a chave é o nome do campo no formulário onde o anexo foi selecionado.
     * @param subdiretorioDestino
     * @param extensoesArquivoPermitidas
     * @param nomeArquivoRenomeado Caso seja informando, o arquivo a ser copiado será renomeado para o parâmetro passado
     * @return
     * @throws ZetraException
     */
    public Map<String, File> salvarArquivos(String subdiretorioDestino, String[] extensoesArquivoPermitidas, String nomeArquivoRenomeado) throws ZetraException {
        return salvarArquivos(subdiretorioDestino, extensoesArquivoPermitidas, nomeArquivoRenomeado, null, true);
    }

    /**
     * Salva o arquivo carregado para o servidor em sua localização definitiva.
     * @param session - se for passado será exibida mensagem de renomeamento de arquivo apra o usuário
     * @throws ZetraException
     */
    public Map<String, File> salvarArquivos(String subdiretorioDestino, String[] extensoesArquivoPermitidas, String nomeArquivoRenomeado, HttpSession session) throws ZetraException {
        return salvarArquivos(subdiretorioDestino, extensoesArquivoPermitidas, nomeArquivoRenomeado, session, true);
    }

    /**
     * Salva o arquivo carregado para o servidor em sua localização definitiva.
     * @param session - se for passado será exibida mensagem de renomeamento de arquivo apra o usuário
     * @throws ZetraException
     */
    public Map<String, File> salvarArquivos(String subdiretorioDestino, String[] extensoesArquivoPermitidas, String nomeArquivoRenomeado, HttpSession session, boolean sobrescrever) throws ZetraException {
    	// É necessário manter a instância de ServletFileUpload, caso contrário o garbage collector
    	// pode entrar em ação e eliminar a instâcia de File que apontava para o arquivo temporário,
    	// fazendo assim com que ele seja excluído.
    	if ((servletFileUpload == null) || (arquivosCarregados == null) || (arquivosCarregados.size() == 0)) {
    		throw new ZetraException("mensagem.erro.nenhum.arquivo.carregado", responsavel);
    	}

    	String arquivosRenomeados = "";

    	final Map<String, File> arquivosSalvos = new HashMap<>();
    	try {
    		for (final FileItem<?> arquivoCarregado : arquivosCarregados) {
    			if (arquivoCarregado.getSize() > 0) {
    				String nomeArquivo;
    				File arquivoSalvo = null;

    				// Se o arquivo ainda não foi copiado para o diretório destino.
    				if (primeiroArquivoMovido == null) {
    					String nameFile = arquivoCarregado.getName();
    					String novoNomeArquivo;
    					try {
    						novoNomeArquivo = FileHelper.prepararNomeArquivo(TextHelper.removeAccentCharsetArbitrario(nameFile));
    					} catch (final IOException e) {
    						LOG.error("Charset do nome do arquivo não suportado.");
    						throw new ZetraException("mensagem.erroInternoSistema", responsavel);
    					}


                        if (!nameFile.equalsIgnoreCase(novoNomeArquivo) && (session != null)) {
                            arquivosRenomeados += ApplicationResourcesHelper.getMessage("mensagem.upload.arquivo.renomeado", responsavel, nameFile, novoNomeArquivo);
                        }
                        nameFile = novoNomeArquivo;

                        if (!TextHelper.isNull(nomeArquivoRenomeado)) {
                            nomeArquivo = nomeArquivoRenomeado + (nameFile.indexOf(".") >= 0 ? nameFile.substring(nameFile.lastIndexOf("."), nameFile.length()) : "");
                        } else {
                            nomeArquivo = nameFile;
                        }

    					// Dependendo do browser, o nome do arquivo inclui o caminho completo do arquivo na
    					// máquina do cliente. Extrai só o nome do arquivo.
    					if (nomeArquivo != null) {
    						int ultimaPosicaoBarra = nomeArquivo.lastIndexOf(File.separatorChar);
    						if (ultimaPosicaoBarra < 0) {
    							ultimaPosicaoBarra = nomeArquivo.lastIndexOf('/');
    						}
    						if (ultimaPosicaoBarra < 0) {
    							ultimaPosicaoBarra = nomeArquivo.lastIndexOf('\\');
    						}
    						if (ultimaPosicaoBarra > 0) {
    							nomeArquivo = nomeArquivo.substring(ultimaPosicaoBarra + 1);
    						}
    					}

    					// Verifica se a extensão do arquivo é válida.
    					if (extensoesArquivoPermitidas != null) {
    						boolean extensaoValida = false;
                            extensoesArquivoPermitidas = atualizaExtensoesPermitidas(extensoesArquivoPermitidas, responsavel);

    						for (final String extensoesArquivoPermitida : extensoesArquivoPermitidas) {
    							if (nomeArquivo.toLowerCase().endsWith(extensoesArquivoPermitida.toLowerCase())) {
    								extensaoValida = true;
    								break;
    							}
    						}

    						if (!extensaoValida) {
    							throw new ZetraException("mensagem.erro.copia.impossivel.arquivos.permitidos", responsavel, TextHelper.join(extensoesArquivoPermitidas, ", "));
    						}
    					}
    				} else {
    					// Se algum arquivo já foi copiado, recupera o nome do primeiro.
    					nomeArquivo = primeiroArquivoMovido.getName();
    				}

    				try {
    					final File diretorioDefinitivo = new File(diretorioRaizArquivos + File.separatorChar + subdiretorioDestino);
    					final File diretorioQuarentena = new File(diretorioRaizArquivos + File.separatorChar + "temp/quarentena/");

    					if (!diretorioDefinitivo.exists() && !diretorioDefinitivo.mkdirs()) {
    						LOG.error("Não foi possível criar diretório destino para os arquivos de upload.");
    						throw new ZetraException("mensagem.erroInternoSistema", responsavel);
    					}

    					arquivoSalvo = new File(diretorioRaizArquivos + File.separatorChar + subdiretorioDestino + File.separatorChar + nomeArquivo);

                        // se já existe arquivo com o mesmo nome na pasta. verifica se pode apagar o arquivo
    					// antigo antes de salvar um novo
                        if (arquivoSalvo.exists()) {
                            if (!sobrescrever) {
                                throw new ZetraException("mensagem.erro.upload.sobreposicao.arquivo", responsavel, nomeArquivo);
                            } else {
                                arquivoSalvo.delete();
                            }
                        }

    					// Se nenhum arquivo foi criado ainda.
    					if (primeiroArquivoMovido == null) {

    						//Parâmetro que habilita a verificação dos arquivos pelo antivirus.
    						if (ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_VERIFICACAO_ARQUIVO_UPLOAD_ANTIVIRUS, responsavel)) {

    						    if (!diretorioQuarentena.exists() && !diretorioQuarentena.mkdirs()) {
    	                            LOG.error("Não foi possível criar diretório destino para os arquivos de upload.");
    	                            throw new ZetraException("mensagem.erroInternoSistema", responsavel);
    	                        }

    						    final File arquivoSalvoQuarentena = new File (diretorioRaizArquivos + File.separatorChar + "temp/quarentena/" + nomeArquivo);

    						    // se já existe arquivo com o mesmo nome na pasta apaga o arquivo e salva o novo
                                if (arquivoSalvoQuarentena.exists()) {
                                    arquivoSalvoQuarentena.delete();
                                }

    							// Copia o arquivo do diretório temporário para a quarentena.
                                Files.copy(arquivoCarregado.getInputStream(), Paths.get(arquivoSalvoQuarentena.getAbsolutePath()));

    							if (varreduraArquivoAntivirus(arquivoSalvoQuarentena.getAbsolutePath(), arquivoSalvoQuarentena.getName(), responsavel)) {
    								// Move o arquivo do diretório temporário para o definitivo.
    							    Files.move(arquivoSalvoQuarentena.toPath(), arquivoSalvo.toPath(), StandardCopyOption.REPLACE_EXISTING);
    							} else {
    								// Registra log de segurança.
    								try {
    									final LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.UPLOAD_FILE, Log.LOG_ERRO_SEGURANCA);
    									log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.upload.arquivo.malicioso.arg0", responsavel, arquivoSalvoQuarentena.getAbsolutePath()));
    									log.write();
    								} catch (final LogControllerException ex) {
    									LOG.error(ex.getMessage(), ex);
    								}

    								arquivoSalvoQuarentena.delete();
    								throw new ZetraException("mensagem.erro.upload.validacao.antivirus.arquivo", responsavel);
    							}
    						} else {
    						    // Copia o arquivo do diretório temporário para o definitivo.
    						    Files.copy(arquivoCarregado.getInputStream(), Paths.get(arquivoSalvo.getAbsolutePath()));
    						}

    						if (arquivosCarregados.size() == 1) {
    							// Se está copiando apenas um arquivo, salva a referência ao arquivo já
    							// copiado, para caso seja necessário gravá-lo em outra pasta, a referência
    							// seja reutilizada.
    							primeiroArquivoMovido = arquivoSalvo;
    						}
    					} else // Copia o primeiro arquivo que foi criado para os demais diretórios informados.
                        if (!FileHelper.copyFile(primeiroArquivoMovido, arquivoSalvo)) {
                        	throw new ZetraException("mensagem.erro.impossivel.copiar.arquivo", responsavel);
                        }

    					// Identifica os arquivos salvos pelo nome do campo no formulário Web
                        arquivosSalvos.put(arquivoCarregado.getFieldName(), arquivoSalvo);

    					if (!FileHelper.validaExtensaoRecursivamente(arquivoSalvo.getAbsolutePath(),extensoesArquivoPermitidas)){
    						arquivoSalvo.delete();
    						if (arquivoSalvo.getAbsolutePath().endsWith(".zip")) {
    							throw new ZetraException("mensagem.erro.upload.conteudo.zip.extensoes.permitidas", responsavel, TextHelper.join(extensoesArquivoPermitidas, ","));
    						} else {
    							throw new ZetraException("mensagem.erro.upload.conteudo.extensoes.permitidas", responsavel, TextHelper.join(extensoesArquivoPermitidas, ","));
    						}
    					}

    					final LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.UPLOAD_FILE, Log.LOG_INFORMACAO);
    					if (!TextHelper.isNull(getValorCampoFormulario("EST_CODIGO"))) {
    						log.setEstabelecimento(getValorCampoFormulario("EST_CODIGO"));
    					}
    					if (!TextHelper.isNull(getValorCampoFormulario("ORG_CODIGO"))) {
    						log.setOrgao(getValorCampoFormulario("ORG_CODIGO"));
    					}
    					log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.arquivo.arg0", responsavel, diretorioRaizArquivos + File.separatorChar + subdiretorioDestino + File.separatorChar + nomeArquivo));
    					log.write();
    				} catch (final ZetraException ex) {
    				    throw ex;
    				} catch (final ResourceAccessException ex) {
    					LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.upload.antivirus.acesso", responsavel), ex);
    					throw new ZetraException("mensagem.log.upload.antivirus.acesso", responsavel, ex);
    			    } catch (final Exception e) {
    					LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.upload.salvar",responsavel), e);
    					throw new ZetraException("mensagem.log.upload.salvar", responsavel, e);
    				}
    			}
    		}
    	} catch (final ZetraException e) {
    		// Se deu qualquer exceção, porém já copiou algum arquivo
    		// efetua a remoção para evitar que algum lixo fique no disco.
    		if ((arquivosSalvos != null) && (arquivosSalvos.size() > 0)) {
    			for (final File element : arquivosSalvos.values()) {
    				element.delete();
    			}
    		}
    		throw e;
    	}

    	if(!arquivosRenomeados.isEmpty() && (session != null)) {
    		arquivosRenomeados = arquivosRenomeados.substring(0, arquivosRenomeados.length());
    		session.setAttribute(CodedValues.MSG_ALERT, arquivosRenomeados);
    	}

    	return arquivosSalvos;
    }

    /**
     * Salva os arquivos na base dados, tabela tb_arquivo_ope_nao_confirmadas para autorização posterir da operação à qual estes pertencem
     */
    public void salvarArquivosBaseOpNaoConfirmadas(String oncCodigo, String hashDir) throws ZetraException {
        // Diretório raiz de arquivos eConsig
        final String absolutePath = ParamSist.getDiretorioRaizArquivos();

        // Diretorio de configuralção
        final String pathArqFilaOperacao = absolutePath + File.separatorChar + "filaoperacaosensivel";
        final File filePathArqFilaOperacao = new File(pathArqFilaOperacao);
        if (!filePathArqFilaOperacao.exists()) {
            filePathArqFilaOperacao.mkdirs();
        }

        for (final FileItem<?> fileItem : arquivosCarregados) {
            if (fileItem.getSize() > 0) {
                final ByteArrayOutputStream out = new ByteArrayOutputStream();

                try {
                    final String nomeOriginalArq = fileItem.getName();
                    final File arqTmpUpload = new File(pathArqFilaOperacao + File.separatorChar + nomeOriginalArq);
                    Files.copy(fileItem.getInputStream(), Paths.get(arqTmpUpload.getAbsolutePath()));

                    final String zipTempPath = arqTmpUpload.getAbsolutePath() + ".zip";
                    FileHelper.zip(arqTmpUpload.getAbsolutePath(), zipTempPath);
                    final String arqBase64 = Base64.encodeBase64String(Files.readAllBytes(Paths.get(zipTempPath)));

                    final SistemaController sistemaController = ApplicationContextProvider.getApplicationContext().getBean(SistemaController.class);
                    sistemaController.gravarAnexosFilaAutorizacao(oncCodigo, nomeOriginalArq, fileItem.getSize(), arqBase64, responsavel);

                    FileHelper.delete(zipTempPath);
                    FileHelper.delete(arqTmpUpload.getAbsolutePath());
                } catch (final IOException e) {
                    LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.upload.salvar",responsavel), e);
                    throw new ZetraException("mensagem.log.upload.salvar", responsavel, e);
                } finally {
                    if (out != null) {
                        try {
                            out.close();
                        } catch (final IOException e) {
                            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.upload.salvar",responsavel), e);
                            throw new ZetraException("mensagem.log.upload.salvar", responsavel, e);
                        }
                    }
                }
            }
        }

        if (!TextHelper.isNull(hashDir)) {
            final String diretorioDestinoUploadHelper = UploadHelper.SUBDIR_ARQUIVOS_TEMPORARIOS + File.separatorChar + "anexo" + File.separatorChar + hashDir;
            final File diretorioTemporario = new File (ParamSist.getDiretorioRaizArquivos() + File.separator + diretorioDestinoUploadHelper);

            if (diretorioTemporario.exists() && diretorioTemporario.isDirectory() && (diretorioTemporario.list().length > 0)) {
                try {
                    for (final File anexoTemp : diretorioTemporario.listFiles()) {
                        final String zipTempPath = anexoTemp.getAbsolutePath() + ".zip";
                        FileHelper.zip(anexoTemp.getAbsolutePath(), zipTempPath);
                        final String arqBase64 = Base64.encodeBase64String(Files.readAllBytes(Paths.get(zipTempPath)));

                        final SistemaController sistemaController = ApplicationContextProvider.getApplicationContext().getBean(SistemaController.class);
                        sistemaController.gravarAnexosFilaAutorizacao(oncCodigo, anexoTemp.getName(), anexoTemp.length(), arqBase64, responsavel);

                        FileHelper.delete(zipTempPath);
                    }

                    FileHelper.deleteDir(diretorioTemporario.getAbsolutePath());
                } catch (final IOException e) {
                    LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.upload.salvar",responsavel), e);
                    throw new ZetraException("mensagem.log.upload.salvar", responsavel, e);
                }
            }
        }
    }

    /**
     * retorna os nomes dos campos do formulário
     * @return
     */
    public List<String> getCamposFormularioNames() {
        final List<String> camposFormularioNames = new ArrayList<>();
        if (!camposFormulario.isEmpty()) {
            for (final Entry<String, List<String>> entry : camposFormulario.entrySet()) {
                if (!TextHelper.isNull(entry.getKey())) {
                    camposFormularioNames.add(entry.getKey().toString());
                }
            }
        }
        return camposFormularioNames;
    }

    /**
     * verifica se o formulário é de requisição de upload (multi-part form data).
     * @return
     */
    public boolean isRequisicaoUpload() {
        return requisicaoUpload;
    }

    /**
     * Retorna um listener para sinalizador de progresso.
     * @return
     */
    private ProgressListener getProgressListener () {
        return new ProgressListener() {
            private long qtde500KB = -1;
            @Override
            public void update(long pBytesRead, long pContentLength, int pItems) {
                if (pItems > 0) {
                    final long mQtde500KB = pBytesRead / 512000;
                    // Só imprime andamento a cada 500 kilobytes.
                    if ((qtde500KB != mQtde500KB) || (pBytesRead == pContentLength)) {
                        qtde500KB = mQtde500KB;
                        LOG.debug("Upload do item " + pItems);
                        if (pContentLength == -1) {
                            LOG.debug("Carregados " + valorLegivelEmBytes(pBytesRead) + ".");
                        } else {
                            LOG.debug("Carregados " + valorLegivelEmBytes(pBytesRead) + " do total de " + valorLegivelEmBytes(pContentLength) + ".");
                        }
                    }
                }
            }
        };
    }

    /**
     * Retorna uma descrição amigável para uma quantidade de bytes.
     * @param bytes
     * @return
     */
    private String valorLegivelEmBytes(long bytes) {
        String valorLegivel;
        long qtdeLegivel = bytes / (1048576);
        if (qtdeLegivel > 0.0) {
            valorLegivel = qtdeLegivel + "MB";
        } else {
            qtdeLegivel = bytes / 1024;
            if (qtdeLegivel > 0) {
                valorLegivel = qtdeLegivel + "KB";
            } else {
                valorLegivel = bytes + " bytes";
            }
        }

        return valorLegivel;
    }

    public String getFileName(int index) {
        return arquivosCarregados.get(index).getName();
    }

    /**
     * Retorna o conteúdo do arquivo recém enviado ao sistema
     * OBS: NÃO USAR PARA ARQUIVOS QUE POSSAM SER GRANDES
     * @param index
     * @return
     */
    public String getFileContent(int index) {
        try {
            final FileItem<?> item = arquivosCarregados.get(index);
            return FileHelper.readAll(item.getInputStream(), System.getProperty("file.encoding"));
        } catch (final IOException ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        }
    }

    public boolean hasArquivosCarregados() {
        if ((arquivosCarregados == null) || arquivosCarregados.isEmpty()) {
            return false;
        }

        for (final FileItem<?> item: arquivosCarregados) {
            if (item.getSize() > 0) {
                return true;
            }
        }
        return false;
    }

    public void removerArquivosCarregados(AcessoSistema responsavel) {
        if ((arquivosCarregados != null) && !arquivosCarregados.isEmpty()) {
            for (final FileItem<?> item: arquivosCarregados) {
                try {
                    item.delete();
                } catch (final IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        }
    }

    public static void limparUploadTempDir(AcessoSistema responsavel) throws ZetraException {
        final String diretorioRaizArquivos = ParamSist.getDiretorioRaizArquivos();
        if (diretorioRaizArquivos == null) {
            throw new ZetraException("mensagem.status.erro.diretorio.raiz.existe", responsavel);
        }

        final File diretorioTemporarioArquivos = new File(diretorioRaizArquivos + File.separatorChar + SUBDIR_ARQUIVOS_TEMPORARIOS);
        if (diretorioTemporarioArquivos.exists() && diretorioTemporarioArquivos.isDirectory() && (diretorioTemporarioArquivos.list().length > 0)) {
            try {
                // Apaga o conteúdo do diretório e ele próprio
                FileHelper.deleteDirContent(diretorioTemporarioArquivos);
            } catch (final IOException ex) {
                throw new ZetraException(ex);
            }
        }
    }

    /**
     * Verifica se o responsavel tem permissao para fazer o upload do determinado tipo de arquivo verificando na tb_tipo_arquivo
     * @param tipoArquivo
     * @param responsavel
     * @return
     * @throws ZetraException
     * TODO Carregar todos os tipos no web controller e validar todos de uma vez
     */
    public static boolean temPermissaoUpload(TipoArquivoEnum tipoArquivo, AcessoSistema responsavel) throws ZetraException {
        boolean retorno = true;

        final UploadController uploadController = ApplicationContextProvider.getApplicationContext().getBean(UploadController.class);
        final TipoArquivo data =  uploadController.buscaTipoArquivoByPrimaryKey(tipoArquivo.getCodigo(), responsavel);

        if((responsavel.isSup() && CodedValues.TPC_NAO.equals(data.getTarUploadSup())) || (responsavel.isCse() && CodedValues.TPC_NAO.equals(data.getTarUploadCse()))) {
            retorno = false;
        } else if ((responsavel.isOrg() && CodedValues.TPC_NAO.equals(data.getTarUploadOrg())) || (responsavel.isCsa() && CodedValues.TPC_NAO.equals(data.getTarUploadCsa()))) {
            retorno = false;
        } else if ((responsavel.isCor() && CodedValues.TPC_NAO.equals(data.getTarUploadCor())) || (responsavel.isSer() && CodedValues.TPC_NAO.equals(data.getTarUploadSer()))) {
            retorno = false;
        }

        return retorno;
    }

    /**
     * Usa serviço de antivirus para varrer aquivo
     * @param file - Informar o arquivo a ser verificado pelo antivirus.
     * @param fileName - Informar o nome do arquivo.
     * @return validacaoArqResult - Resultado retornado pelo antivirus: "True - para arquivos válidos" e "False para arquivos infectados".
     * @throws ZetraException
     */
    public static boolean varreduraArquivoAntivirus(String file, String fileName, AcessoSistema responsavel) throws ZetraException {
    	final RestTemplate restTemplate = new RestTemplate();
    	final FormHttpMessageConverter converter = new FormHttpMessageConverter();
    	restTemplate.getMessageConverters().add(converter);
    	final HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    	boolean validacaoArqResult = false;
    	final Path path = Paths.get(file);

    	final String urlAntivirusAnexo = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_ANTIVIRUS_ANEXO, responsavel);

    	byte[] in = null;
    	try {
    		in = Files.readAllBytes(path);
    	} catch (final IOException ex) {
            LOG.error(ex.getMessage(), ex);
    		throw new ZetraException("mensagem.erro.ler.arquivo.entrada", responsavel, ex);
    	}

    	final MultipartByteArrayResource resource = new MultipartByteArrayResource(in);
    	resource.setFilename(fileName);
    	final MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();

        parts.add("name", fileName);
        parts.add("FILES", resource);

    	final HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(parts, headers);
    	final ResponseEntity<String> responseEntity = restTemplate.exchange(urlAntivirusAnexo, HttpMethod.POST, requestEntity, String.class);
    	final String body = responseEntity.getBody();

    	if (body.contains("true")) {
    		validacaoArqResult = true;
    	} else if (body.contains("false")) {
    		validacaoArqResult = false;
    	}

    	return validacaoArqResult;
    }

    public List<String> getNomeCamposArquivos() {
        return nomeCamposArquivos;
    }

    /**
     * Converter o arquivo de retorno de órgão para o layout de consignante
     * @param file - Informar o arquivo a ser convertido.
     * @param OrgCodigo - Código do órgão que terá o arquivo convetido
     * @param AcessoSistema - responsavel
     * @return retorna mensagem de erro caso a conversão não tenha acontecido
     * @throws ZetraException
     */
    public String convertArquivoRetornoOrgaoLayoutCse(File file, String orgCodigo, AcessoSistema responsavel){

        String msgErro ="";

        final String absolutePath = ParamSist.getDiretorioRaizArquivos();
        final String pathConf = absolutePath + File.separatorChar + "conf";

        final String nomeEntradaImpRetorno  = (String)  ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_IMP_RETORNO, responsavel);
        final String nomeTradutorImpRetorno  = (String)  ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_IMP_RETORNO, responsavel);

        final String entradaImpRetornoOrg = pathConf + File.separatorChar + "cse" + File.separatorChar + orgCodigo + File.separatorChar + nomeEntradaImpRetorno;
        final String tradutorImpRetornoOrg = pathConf + File.separatorChar + "cse" + File.separatorChar + orgCodigo + File.separatorChar + nomeTradutorImpRetorno;

        final String entradaImpRetorno = pathConf + File.separatorChar + nomeEntradaImpRetorno;

        if(TextHelper.isNull(entradaImpRetornoOrg) || TextHelper.isNull(tradutorImpRetornoOrg)) {
            msgErro = ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.conf.xml.orgao.nao.existe", responsavel);
            LOG.error(msgErro);
            return msgErro;
        }

        FileHelper.rename(file.getAbsolutePath(), file.getAbsolutePath() + ".ORIG");
        final File arquivoLeitor = new File(file.getAbsolutePath() + ".ORIG");
        final String arquivoRetornoSaida = file.getAbsolutePath();

        try {
            final EscritorArquivoTexto escritor = new EscritorArquivoTexto(entradaImpRetorno, arquivoRetornoSaida);
            final LeitorArquivoTexto leitor = new LeitorArquivoTexto(entradaImpRetornoOrg, arquivoLeitor.getAbsolutePath());
            final Tradutor tradutor = new Tradutor(tradutorImpRetornoOrg, leitor, escritor);
            tradutor.traduz();
        } catch (ZetraException | ParserException ex) {
            LOG.error(ex.getMessage(), ex);
            msgErro = ex.getMessage();
            try {
                FileHelper.delete(arquivoLeitor.getAbsolutePath());
                FileHelper.delete(arquivoRetornoSaida);
            } catch (final IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }

        return msgErro;
    }

    public static String[] atualizaExtensoesPermitidas(String[] extensoesArquivoPermitidas, AcessoSistema responsavel) {
        final Set<String> extensoesImagem = new HashSet<>(Arrays.asList(EXTENSOES_PERMITIDAS_ANEXO_APENAS_IMAGENS));
        final Set<String> extensoesPermitidas = new HashSet<>(Arrays.asList(extensoesArquivoPermitidas));
        final String paramExtensoes = (String) ParamSist.getInstance().getParamOrDefault(CodedValues.TPC_EXTENSOES_PERMITIDAS_UPLOAD_ANEXO, null, responsavel);

        if (!TextHelper.isNull(paramExtensoes)) {
            if (Arrays.equals(extensoesArquivoPermitidas, EXTENSOES_PERMITIDAS_ANEXO_REGISTRO_SERVIDOR)) {
                return paramExtensoes.split(",");
            }

            for (final String ext: extensoesImagem) {
                if (extensoesPermitidas.contains(ext)) {
                    extensoesArquivoPermitidas = paramExtensoes.split(",");
                    break;
                }
            }
        }

        return extensoesArquivoPermitidas;
    }
}
