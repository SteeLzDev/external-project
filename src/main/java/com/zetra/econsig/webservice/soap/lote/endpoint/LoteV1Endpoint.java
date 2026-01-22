package com.zetra.econsig.webservice.soap.lote.endpoint;

import static com.zetra.econsig.values.ApplicationResourcesKeys.MENSAGEM_ERRO_DIRETORIO_INEXISTENTE;
import static com.zetra.econsig.values.ApplicationResourcesKeys.MENSAGEM_ERRO_LOTE_GRAVAR_ARQUIVO;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.transport.context.TransportContextHolder;

import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.lote.LoteHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.usuario.UsuarioHelper;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.Processo;
import com.zetra.econsig.persistence.entity.ControleProcessamentoLote;
import com.zetra.econsig.service.lote.LoteController;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.values.CanalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.soap.endpoint.EndpointBase;
import com.zetra.econsig.webservice.soap.lote.v1.Anexo;
import com.zetra.econsig.webservice.soap.lote.v1.LoteResultRequest;
import com.zetra.econsig.webservice.soap.lote.v1.LoteResultResponse;
import com.zetra.econsig.webservice.soap.lote.v1.LoteUploadRequest;
import com.zetra.econsig.webservice.soap.lote.v1.LoteUploadResponse;
import com.zetra.econsig.webservice.soap.util.SoapMessageHelper;

/**
 * <p>Title: LoteV1Endpoint</p>
 * <p>Description: Endpoint SOAP para o serviço Lote versão 1.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@Endpoint
public class LoteV1Endpoint extends EndpointBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(LoteV1Endpoint.class);

    private static final String NAMESPACE_URI = "LoteService-v1_0";

    @Autowired
    private LoteController loteController;

    @Autowired
    private SistemaController sistemaController;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "loteUploadRequest")
    @ResponsePayload
    public LoteUploadResponse loteUpload(@RequestPayload LoteUploadRequest loteUploadRequest) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());

        LoteUploadResponse response = new LoteUploadResponse();

        final String usuario = loteUploadRequest.getUsuario();
        final String senha = loteUploadRequest.getSenha();
        final String tipo = loteUploadRequest.getTipo();
        final String leiaute = loteUploadRequest.getLeiaute();
        final Anexo arquivoLote = loteUploadRequest.getLote();
        final String arquivoCentralizador = loteUploadRequest.getIdentificador();

        final boolean validar = "validar".equalsIgnoreCase(tipo);

        final AcessoSistema responsavel;
        try {
            responsavel = autenticaUsuario(usuario, senha, validar, remoteAddr, remotePort);
        } catch (final ViewHelperException ex) {
            LOG.error(ex.getMessage(), ex);
            response.setCodRetorno(String.valueOf(CodedValues.CPL_FALHA));
            response.setMensagem(ex.getMessage());
            return response;
        }

        // Se o sistema estiver bloqueado ou inativo, nenhum arquivo de lote pode ser processado
        if (sistemaController.isSistemaBloqueado(responsavel)) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.sistema.bloqueado.inativo", responsavel));
            response.setCodRetorno(String.valueOf(CodedValues.CPL_FALHA));
            response.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.erro.sistema.bloqueado.inativo", responsavel));
            return response;
        }

        ControleProcessamentoLote controleProcessamentoLote = loteController.findProcessamentoByArquivoCentralizador(arquivoCentralizador);

        // Se o arquivo não possui registro ou se estiver pendente de upload ou algum erro tiver ocorrido no upload, permite o upload.
        // Se já estiver processado, responde com o nome do arquivo eConsig (identificador a ser utilizado para consulta do resultado)
        if (controleProcessamentoLote != null && controleProcessamentoLote.getCplStatus() != CodedValues.CPL_PENDENTE_UPLOAD && controleProcessamentoLote.getCplStatus() != CodedValues.CPL_UPLOAD_FALHA) {
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.informacao.arquivo.lote.soap.presente.servidor", responsavel, arquivoCentralizador));
            response.setCodRetorno(String.valueOf(CodedValues.CPL_UPLOAD_SUCESSO));
            response.setMensagem(controleProcessamentoLote.getCplArquivoEconsig());
            return response;
        }

        // DESENV-10065: Verifica se existe algum processo de lote executando para a consignatária, independente do arquivo ou usuário executando
        final String chave1 = "LOTE" + "|" + responsavel.getCodigoEntidade();
        final boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(chave1, null);
        if (!temProcessoRodando) {
            response = salvarLote(leiaute, arquivoLote, "validar".equalsIgnoreCase(tipo), responsavel);

            if (response.getCodRetorno().equals(String.valueOf(CodedValues.CPL_UPLOAD_SUCESSO))) {
                // Inclui o registro na tabela de controle do processamento
                final String arquivoeConsig = response.getMensagem();
                final File arquivoEntrada = new File(arquivoeConsig);
                if (arquivoEntrada.exists() && arquivoEntrada.canWrite()) {
                    try {
                        controleProcessamentoLote = loteController.incluirProcessamento(arquivoCentralizador, arquivoeConsig, CodedValues.CPL_UPLOAD_SUCESSO, responsavel);
                    } catch (final ZetraException ex) {
                        LOG.error(ex.getMessage(), ex);
                        response.setCodRetorno(String.valueOf(CodedValues.CPL_FALHA));
                        response.setMensagem(ex.getMessage());
                        return response;
                    }

                    // Se upload com sucesso, inicia o processo de execução
                    final ProcessaLoteSoap processaLoteSoap = new ProcessaLoteSoap(tipo, leiaute, arquivoEntrada, validar, responsavel);
                    ControladorProcessos.getInstance().incluir(chave1, processaLoteSoap);
                    processaLoteSoap.start();
                } else {
                    LOG.error("Arquivo não encontrado: \"" + arquivoeConsig + "\"");
                    response.setCodRetorno(String.valueOf(CodedValues.CPL_UPLOAD_FALHA));
                    response.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.erro.lote.arquivo.nao.encontrado", responsavel));
                    return response;
                }
            }

            return response;

        } else {
            response.setCodRetorno(String.valueOf(CodedValues.CPL_UPLOAD_FALHA));
            response.setMensagem(ApplicationResourcesHelper.getMessage(responsavel.isCsa() ? "mensagem.erro.arquivo.lote.soap.upload.concorrente.csa" : "mensagem.erro.arquivo.lote.soap.upload.concorrente.cor", responsavel));
            return response;
        }
    }

    /**
     * Metodo utilizado para processamento de lote via SOAP, no caso pelo centralizador.
     * @param leiaute
     * @param arquivoLote
     * @param validar
     * @param responsavel
     * @return
     */
    private LoteUploadResponse salvarLote(String leiaute, Anexo arquivoLote, boolean validar, AcessoSistema responsavel) {
        final LoteUploadResponse response = new LoteUploadResponse();

        final String codEntidade = responsavel.getCodigoEntidade();

        // Grava o arquivo de lote no sistema de arquivo
        final String rootPath = ParamSist.getDiretorioRaizArquivos();
        final String pathLote = rootPath + File.separator + "lote" + File.separator + "csa" + File.separator + codEntidade + File.separator;

        // Verifica se o caminho para a gravação existe
        final File dir = new java.io.File(pathLote);
        if (!dir.exists() && !dir.mkdirs()) {
            LOG.error(ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_DIRETORIO_INEXISTENTE, responsavel));
            response.setCodRetorno(String.valueOf(CodedValues.CPL_UPLOAD_FALHA));
            response.setMensagem(ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_DIRETORIO_INEXISTENTE, responsavel));
            return response;
        }

        // Gera o nome do arquivo de lote
        File arquivoEntrada = new File(pathLote + "REQUISICAO_" + DateHelper.format(DateHelper.getSystemDatetime(), "ddMMyyyyHHmmss") + ".TXT");
        arquivoLote.setNomeArquivo(arquivoEntrada.getName());
        try {
            arquivoEntrada = salvarArquivoLote(arquivoEntrada.getParent(), arquivoLote);
        } catch (final Exception ex) {
            LOG.error(ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_LOTE_GRAVAR_ARQUIVO, responsavel));
            response.setCodRetorno(String.valueOf(CodedValues.CPL_UPLOAD_FALHA));
            response.setMensagem(ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_LOTE_GRAVAR_ARQUIVO, responsavel));
            return response;
        }

        response.setCodRetorno(String.valueOf(CodedValues.CPL_UPLOAD_SUCESSO));
        response.setMensagem(arquivoEntrada.getAbsolutePath());
        return response;
    }

    private File salvarArquivoLote(String path, Anexo arquivo) throws ZetraException, FileNotFoundException, IOException {
        final File pth = new File(path);
        final File file = new File(pth, arquivo.getNomeArquivo());

        final byte[] content = arquivo.getArquivo();
        if ("PK".equals(new String(content, 0,2))) {
            try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(content));
                    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {

                zis.getNextEntry();
                org.apache.commons.io.IOUtils.copy(zis, out);
            }
        } else {
            Files.write(file.toPath(), content);
        }

        return file;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "loteResultRequest")
    @ResponsePayload
    public LoteResultResponse loteResult(@RequestPayload LoteResultRequest loteResultRequest) {
        final String remoteAddr = SoapMessageHelper.getRemoteAddr(TransportContextHolder.getTransportContext());
        final Integer remotePort = SoapMessageHelper.getRemotePort(TransportContextHolder.getTransportContext());

        final LoteResultResponse response = new LoteResultResponse();

        final String usuario = loteResultRequest.getUsuario();
        final String senha = loteResultRequest.getSenha();
        final String arquivoeConsig = loteResultRequest.getIdentificador();

        LOG.info("Verificando o resultado do processamento do arquivo de lote " + arquivoeConsig);

        AcessoSistema responsavel = null;
        try {
            responsavel = autenticaUsuario(usuario, senha, null, remoteAddr, remotePort);
        } catch (final ViewHelperException ex) {
            LOG.error(ex.getMessage(), ex);
            response.setCodRetorno(String.valueOf(CodedValues.CPL_FALHA));
            response.setMensagem(ex.getMessage());
            return response;
        }

        // Quando o webservice é consumido com o identificador concatenado com .ok trata-se de uma confirmação de que a resposta
        // já foi consumida com sucesso e pode remover da tabela de controle.
        if (arquivoeConsig.endsWith(".ok")) {
            final ControleProcessamentoLote controleProcessamentoLote = loteController.findProcessamentoByArquivoeConsig(arquivoeConsig.replaceFirst("\\.ok$", ""));
            if (controleProcessamentoLote != null) {
                try {
                    loteController.excluirProcessamento(controleProcessamentoLote, responsavel);
                    LOG.info(ApplicationResourcesHelper.getMessage("mensagem.informacao.arquivo.lote.soap.processamento.concluido", responsavel, arquivoeConsig.replaceFirst("\\.ok$", "")));
                    response.setCodRetorno(String.valueOf(CodedValues.CPL_PROCESSADO_SUCESSO));
                    response.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.informacao.arquivo.lote.soap.processamento.concluido", responsavel, arquivoeConsig.replaceFirst("\\.ok$", "")));
                    return response;
                } catch (ZetraException ex) {
                    LOG.error(ex.getMessage(), ex);
                    response.setCodRetorno(String.valueOf(CodedValues.CPL_FALHA));
                    response.setMensagem(ex.getMessage());
                    return response;
                }
            }
        }

        final ControleProcessamentoLote controleProcessamentoLote = loteController.findProcessamentoByArquivoeConsig(arquivoeConsig);
        if (controleProcessamentoLote != null) {
            LOG.info("O arquivo de lote " + arquivoeConsig + " corresponde ao arquivo " + controleProcessamentoLote.getCplArquivoCentralizador() + " no lado do Centralizador.");
            final Short status = controleProcessamentoLote.getCplStatus();
            if (status == CodedValues.CPL_PROCESSANDO) {
                LOG.info(ApplicationResourcesHelper.getMessage("mensagem.informacao.arquivo.lote.soap.processando", responsavel, arquivoeConsig));
                response.setCodRetorno(String.valueOf(status));
                response.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.informacao.arquivo.lote.soap.processando", responsavel, arquivoeConsig));
                return response;
            } else if (status == CodedValues.CPL_PROCESSADO_FALHA) {
                LOG.info(ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.lote.soap.processamento.interrompido", responsavel, arquivoeConsig));
                response.setCodRetorno(String.valueOf(status));
                response.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.lote.soap.processamento.interrompido", responsavel, arquivoeConsig));
                return response;
            }
        } else {
            response.setCodRetorno(String.valueOf(CodedValues.CPL_FALHA));
            response.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.lote.soap.identificador.desconhecido", responsavel, arquivoeConsig));
            return response;
        }

        final File dirResult = new File(arquivoeConsig).getParentFile();
        final File[] matchingFiles = dirResult.listFiles((FilenameFilter) (dir, name) -> name.startsWith(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.critica.prefixo", (AcessoSistema) null) + new File(arquivoeConsig).getName().toString()) || name.startsWith(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.validacao.prefixo", (AcessoSistema) null) + new File(arquivoeConsig).getName().toString()));

        File resultFile = null;
        if (matchingFiles.length == 1) {
            resultFile = matchingFiles[0];
        } else {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.lote.soap.arquivo.critica.nao.encontrado", responsavel));
            response.setCodRetorno(String.valueOf(CodedValues.CPL_FALHA));
            response.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.lote.soap.arquivo.critica.nao.encontrado", responsavel));
            return response;
        }

        try {
            controleProcessamentoLote.setCplStatus(CodedValues.CPL_RESULTADO_ENTREGUE);
            loteController.alterarProcessamento(controleProcessamentoLote, responsavel);
        } catch (final ZetraException ex) {
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.lote.soap.conclusao.processamento.falhou", responsavel), ex);
            response.setCodRetorno(String.valueOf(CodedValues.CPL_FALHA));
            response.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.lote.soap.conclusao.processamento.falhou", responsavel));
            return response;
        }

        LOG.info(ApplicationResourcesHelper.getMessage("mensagem.informacao.arquivo.lote.soap.processamento.concluido", responsavel, arquivoeConsig));
        response.setCodRetorno(String.valueOf(CodedValues.CPL_RESULTADO_ENTREGUE));
        response.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.informacao.arquivo.lote.soap.processamento.concluido", responsavel, arquivoeConsig));

        // O arquivo de critica é criado já compactado.
        final Anexo anexo = new Anexo();
        anexo.setNomeArquivo(resultFile.getAbsolutePath());
        try {
            anexo.setArquivo(Files.readAllBytes(resultFile.toPath()));
        } catch (final IOException ex) {
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.lote.soap.conclusao.processamento.falhou", responsavel), ex);
            response.setCodRetorno(String.valueOf(CodedValues.CPL_FALHA));
            response.setMensagem(ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.lote.soap.conclusao.processamento.falhou", responsavel));
            return response;
        }

        response.setLote(anexo);
        return response;
    }

    private AcessoSistema autenticaUsuario(String usuario, String senha, Boolean validar, String remoteAddr, Integer remotePort) throws ViewHelperException {
        // Autentica o usuário e a senha
        final AcessoSistema responsavel = new AcessoSistema(null, remoteAddr, remotePort);
        TransferObject usuarioLogado = null;
        try {
            usuarioLogado = UsuarioHelper.autenticarUsuario(usuario, senha, responsavel);
            responsavel.setUsuCodigo((String) usuarioLogado.getAttribute(Columns.USU_CODIGO));
            responsavel.setCodigoEntidade((String) usuarioLogado.getAttribute("COD_ENTIDADE"));
            responsavel.setTipoEntidade((String) usuarioLogado.getAttribute("TIPO_ENTIDADE"));
            responsavel.setCanal(CanalEnum.SOAP);
        } catch (final ViewHelperException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException(ex.getMessageKey(), responsavel);
        }

        try {
            // Busca as permissões do usuário
            responsavel.setPermissoes(new UsuarioDelegate().selectFuncoes(responsavel.getUsuCodigo(), responsavel.getCodigoEntidade(), responsavel.getTipoEntidade(), responsavel));
        } catch (final UsuarioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erro.carregar.permissoes.usuario", responsavel);
        }

        if (validar != null) {
            final String funCodigo = (validar) ? CodedValues.FUN_VALIDAR_PROCESSAMENTO_VIA_LOTE : CodedValues.FUN_IMPORTACAO_VIA_LOTE;

            if (!responsavel.temPermissao(funCodigo)) {
                throw new ViewHelperException("mensagem.erro.usuario.sem.permissao.autorizar.operacao", responsavel);
            }

            // Seta função executada para conferir se há regra de restrição de acesso
            responsavel.setFunCodigo(funCodigo);
        }
        return responsavel;
    }

    private static class ProcessaLoteSoap extends Processo {

        private final String leiaute;
        private final File arquivoEntrada;
        private final AcessoSistema responsavel;
        private final boolean validar;

        public ProcessaLoteSoap(String tipo, String leiaute, File arquivoEntrada, boolean validar, AcessoSistema responsavel) {
            this.leiaute = leiaute;
            this.arquivoEntrada = arquivoEntrada;
            this.validar = validar;
            this.responsavel = responsavel;
        }

        @Override
        protected void executar() {
            try {
                final LoteHelper loteHelper = new LoteHelper(responsavel.getCsaCodigo(), responsavel.getCorCodigo(), validar, true, true, false, false, null, responsavel);
                loteHelper.importarLote(leiaute + "_entrada.xml", leiaute + "_tradutor.xml", arquivoEntrada.getName());
            } catch (final ZetraException ex) {
                LOG.error(ex.getMessage(), ex);
                mensagem = ex.getMessage();
            }
        }
    }
}