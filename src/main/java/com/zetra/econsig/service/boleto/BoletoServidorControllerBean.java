package com.zetra.econsig.service.boleto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.BoletoServidorControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sms.EnviaSMSHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.persistence.entity.Arquivo;
import com.zetra.econsig.persistence.entity.ArquivoHome;
import com.zetra.econsig.persistence.entity.BoletoServidor;
import com.zetra.econsig.persistence.entity.BoletoServidorHome;
import com.zetra.econsig.persistence.query.boleto.ListaBoletoServidorExpiradoQuery;
import com.zetra.econsig.persistence.query.boleto.ListaBoletoServidorQuery;
import com.zetra.econsig.service.notificacao.NotificacaoDispositivoController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;

/**
 * <p>Title: BoletoServidorControllerBean</p>
 * <p>Description: Session Bean para operações sobre boleto servidor</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class BoletoServidorControllerBean implements BoletoServidorController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(BoletoServidorControllerBean.class);

    @Autowired
    private NotificacaoDispositivoController notificacaoDispositivoController;

    @Autowired
    private ServidorController servidorController;

    @Override
    public String createBoleto(TransferObject criterio, AcessoSistema responsavel) throws BoletoServidorControllerException {
        try {
            // Cria arquivo do boleto
            String arqConteudo = criterio.getAttribute(Columns.ARQ_CONTEUDO).toString();
            Arquivo arquivo = ArquivoHome.create(arqConteudo, TipoArquivoEnum.ARQUIVO_BOLETO_PARCELA_EM_ATRASO.getCodigo());

            // Associa ao servidor
            String serCodigo = criterio.getAttribute(Columns.BOS_SER_CODIGO).toString();
            String csaCodigo = criterio.getAttribute(Columns.BOS_CSA_CODIGO).toString();
            String usuCodigo = criterio.getAttribute(Columns.BOS_USU_CODIGO).toString();
            String arqCodigo = arquivo.getArqCodigo();
            BoletoServidor bos = BoletoServidorHome.create(serCodigo, csaCodigo, usuCodigo, arqCodigo);

            // Grava log de Erro
            String bosCodigo = bos.getBosCodigo();
            LogDelegate log = new LogDelegate(responsavel, Log.BOLETO_SERVIDOR, Log.CREATE, Log.LOG_INFORMACAO);
            log.setBoletoServidor(bosCodigo);
            log.write();

            return bosCodigo;

        } catch (CreateException | LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new BoletoServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void removeBoleto(String bosCodigo, AcessoSistema responsavel) throws BoletoServidorControllerException {
        try {
            // Data de exclusão do boleto
            Timestamp bosDataExclusao = new Timestamp(Calendar.getInstance().getTimeInMillis());

            // Encontra boleto do servidor
            BoletoServidor bos = BoletoServidorHome.findByPrimaryKey(bosCodigo);
            Arquivo arquivo = bos.getArquivo();

            // Verifica se o boleto que será removido pertence a consignatária do responsável
            if (!responsavel.isSistema() && !bos.getConsignataria().getCsaCodigo().equals(responsavel.getCsaCodigo())) {
                throw new BoletoServidorControllerException("mensagem.usuarioNaoTemPermissao", responsavel);
            }

            // Altera data de exclusão do boleto e remove ligação com arquivo que será removido
            bos.setBosDataExclusao(bosDataExclusao);
            bos.setArquivo(null);

            BoletoServidorHome.update(bos);

            // Remove arquivo de boleto
            if (arquivo != null) {
                ArquivoHome.remove(arquivo);
            }

            // Grava log de Erro
            LogDelegate log = new LogDelegate(responsavel, Log.BOLETO_SERVIDOR, Log.DELETE, Log.LOG_INFORMACAO);
            log.setBoletoServidor(bosCodigo);
            log.write();

        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new BoletoServidorControllerException("mensagem.boleto.nao.encontrado", responsavel, ex);
        } catch (UpdateException | RemoveException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new BoletoServidorControllerException("mensagem.erro.remover.boleto.servidor", responsavel, ex);
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new BoletoServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void removeBoletosExpirados(int diasAposEnvio, AcessoSistema responsavel) throws BoletoServidorControllerException {
        try {
            ListaBoletoServidorExpiradoQuery query = new ListaBoletoServidorExpiradoQuery();
            query.diasAposEnvio = diasAposEnvio;
            List<String> bosCodigos = query.executarLista();
            if (bosCodigos != null && !bosCodigos.isEmpty()) {
                for (String bosCodigo : bosCodigos) {
                    removeBoleto(bosCodigo, responsavel);
                }
            }
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new BoletoServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void atualizaDataDownloadBoleto(String bosCodigo, AcessoSistema responsavel) throws BoletoServidorControllerException {
        try {
            // Data de exclusão do boleto
            Timestamp bosDataDownload = new Timestamp(Calendar.getInstance().getTimeInMillis());

            // Encontra boleto do servidor
            BoletoServidor bos = BoletoServidorHome.findByPrimaryKey(bosCodigo);

            // Verifica se o boleto que será baixado pertence ao servidor responsável
            String serCodigo = responsavel.getSerCodigo();
            if (!bos.getServidor().getSerCodigo().equals(serCodigo)) {
                throw new BoletoServidorControllerException("mensagem.usuarioNaoTemPermissao", responsavel);
            }

            if (TextHelper.isNull(bos.getBosDataDownload())) {
                // Altera data de download do boleto apenas se não tiver sido baixado anteriormente
                bos.setBosDataDownload(bosDataDownload);

                BoletoServidorHome.update(bos);
            }

            // Grava log de Erro
            LogDelegate log = new LogDelegate(responsavel, Log.BOLETO_SERVIDOR, Log.DOWNLOAD_FILE, Log.LOG_INFORMACAO);
            log.setBoletoServidor(bosCodigo);
            log.write();

        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new BoletoServidorControllerException("mensagem.boleto.nao.encontrado", responsavel, ex);
        } catch (UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new BoletoServidorControllerException("mensagem.erro.atualizar.download.boleto.servidor", responsavel, ex, bosCodigo);
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new BoletoServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<String> uploadBoleto(File zipCarregado, AcessoSistema responsavel) throws BoletoServidorControllerException {
        String path = ParamSist.getDiretorioRaizArquivos();
        String outputPath = path + File.separatorChar + "anexo" + File.separatorChar + "tmpBoletosEmLote" + File.separatorChar + responsavel.getUsuCodigo();

        // Verifica se arquivo existe
        if (zipCarregado == null || !zipCarregado.exists() || TextHelper.isNull(zipCarregado.getName())) {
            throw new BoletoServidorControllerException("mensagem.erro.copia.impossivel.arquivos.permitidos", responsavel, TextHelper.join(UploadHelper.EXTENSOES_PERMITIDAS_BOLETO_SERVIDOR, ", "));
        }

        // Verifica se é arquivo zip
        String testPath = zipCarregado.getAbsolutePath();
        try {
            if (!testPath.toLowerCase().endsWith(".zip") || !FileHelper.isZip(testPath)) {
                File aApagar = new File(testPath);
                aApagar.delete();

                throw new BoletoServidorControllerException("mensagem.erro.copia.impossivel.arquivos.permitidos", responsavel, TextHelper.join(UploadHelper.EXTENSOES_PERMITIDAS_BOLETO_SERVIDOR, ", "));
            }
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new BoletoServidorControllerException("mensagem.erroInternoSistema", responsavel, e);
        }

        String csaCodigo = responsavel.getCsaCodigo();

        String pathPdf = outputPath + File.separatorChar + "pdf";
        try {
            File diretorio = new File(pathPdf);
            // Senão existe, cria o diretório
            if (!diretorio.exists()) {
                diretorio.mkdirs();
            }

            FileHelper.unZipAll(zipCarregado.getAbsolutePath(), pathPdf, true);

        } catch (IOException e) {
            // Não foi possível descompactar arquivo de boleto
            LOG.error(e.getMessage(), e);
            zipCarregado.delete();
            throw new BoletoServidorControllerException("mensagem.erroInternoSistema", responsavel, e);
        }

        // Itera os arquivos descompactados e associa aos servidores
        File pathOutputPdf = new File(pathPdf);
        String[] extensions = new String[] { "pdf" };
        Collection<File> pdfs = FileUtils.listFiles(pathOutputPdf, extensions, true);
        List<String> critica = new ArrayList<>();

        for (File pdf : pdfs) {
            String nameFile = pdf.getName();
            // Valida CPF
            String serCpf = nameFile.toLowerCase().replaceAll(".pdf", "");

            if (TextHelper.dropSeparator(serCpf).length() != 11) {
                LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.erro.tradutor.cpf.formato.incorreto", responsavel, serCpf));
                critica.add(serCpf);
            } else if (!TextHelper.cpfOk(TextHelper.dropSeparator(serCpf))) {
                LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.erro.cpf.numero.invalido", responsavel, serCpf));
                critica.add(serCpf);
            }
            // Formata CPF
            serCpf = TextHelper.format(serCpf, "###.###.###-##");

            try {
                // Busca servidor
                List<TransferObject> servidores = servidorController.listarServidorPossuiAde(serCpf, responsavel);

                if (servidores == null || servidores.isEmpty()) {
                    LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel));
                    critica.add(serCpf);
                }

                // Lê o conteúdo do arquivo
                byte[] conteudoArquivoPdf = Files.readAllBytes(Paths.get(pdf.getAbsolutePath()));

                // Transforma o conteúdo em Base64 para gravação no banco de dados
                byte[] conteudoArquivoBase64 = Base64.getEncoder().encode(conteudoArquivoPdf);

                // Grava o conteúdo no banco
                String arqConteudo = new String(conteudoArquivoBase64);

                TransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.BOS_CSA_CODIGO, csaCodigo);
                criterio.setAttribute(Columns.BOS_USU_CODIGO, responsavel.getUsuCodigo());
                criterio.setAttribute(Columns.ARQ_CONTEUDO, arqConteudo);

                for (TransferObject servidor : servidores) {
                    try {
                        criterio.setAttribute(Columns.BOS_SER_CODIGO, servidor.getAttribute(Columns.SER_CODIGO).toString());
                        createBoleto(criterio, responsavel);
                        notificarServidorNovoBoleto(servidor, conteudoArquivoPdf, responsavel);
                    } catch (BoletoServidorControllerException e) {
                        LOG.error("Não foi possível ler pdf: " + nameFile, e);
                        critica.add(serCpf);
                    }
                }
            } catch (IOException e) {
                LOG.error("Não foi possível ler pdf: " + nameFile, e);
                critica.add(serCpf);
            } catch (ServidorControllerException e) {
                LOG.error("Não foi possível encontrar o servidor: " + serCpf, e);
                critica.add(serCpf);
            }
        }

        try {
            // Remove diretório e dependências após processamento
            FileUtils.deleteDirectory(new File(zipCarregado.getParent()));
        } catch (IOException e) {
            LOG.error("Não foi possível remover o diretório.", e);
        }

        return critica;
    }

    private void notificarServidorNovoBoleto(TransferObject servidor, byte[] conteudoArquivoPdf, AcessoSistema responsavel) {
        String modoNotificaoSer = (String) ParamSist.getInstance().getParam(CodedValues.TPC_MODO_NOTIFICACAO_NOVO_BOLETO_SERVIDOR, responsavel);
        if (!TextHelper.isNull(modoNotificaoSer)) {
            String serCodigo = (String) servidor.getAttribute(Columns.SER_CODIGO);
            String serEmail = (String) servidor.getAttribute(Columns.SER_EMAIL);
            String serCelular = (String) servidor.getAttribute(Columns.SER_CELULAR);

            boolean notificacaoEnviada = false;
            String[] modos = modoNotificaoSer.split("\\&|\\|");
            for (String modo : modos) {
                try {
                    if ((modo.equals("EMAIL") || modo.equals("ANEXO")) && !TextHelper.isNull(serEmail)) {
                        if (modo.equals("ANEXO")) {
                            EnviaEmailHelper.enviarEmailNovoBoletoServidor(serEmail, responsavel.getNomeEntidade(), servidor, conteudoArquivoPdf, responsavel);
                        } else {
                            EnviaEmailHelper.enviarEmailNovoBoletoServidor(serEmail, responsavel.getNomeEntidade(), servidor, null, responsavel);
                        }
                    } else if (modo.equals("SMS") && !TextHelper.isNull(serCelular)) {
                        EnviaSMSHelper.enviarSMSNovoBoleto(serCelular, responsavel.getNomeEntidade(), responsavel);
                    } else if (modo.equals("MOBILE")) {
                        String titulo = ApplicationResourcesHelper.getMessage("mensagem.notificacao.push.novo.boleto.servidor.titulo", responsavel);
                        String texto = ApplicationResourcesHelper.getMessage("mensagem.notificacao.push.novo.boleto.servidor.texto", responsavel, responsavel.getNomeEntidade());
                        notificacaoEnviada |= notificacaoDispositivoController.enviarNotificacao(serCodigo, titulo, texto, TipoNotificacaoEnum.NOVO_BOLETO_SERVIDOR, CodedValues.FUN_UPLOAD_BOLETOS_EM_LOTE, responsavel);
                    } else {
                        LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.aviso.parametro.sistema.valor.invalido", responsavel, CodedValues.TPC_MODO_NOTIFICACAO_NOVO_BOLETO_SERVIDOR, modo));
                        continue;
                    }

                    // Se não deu exceção, marca que a notificação foi enviada
                    notificacaoEnviada = true;
                } catch (ZetraException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
                if (notificacaoEnviada && modoNotificaoSer.indexOf(modo + "|") >= 0) {
                    // Se a notificação foi enviada, e os próximos modos de notificação são opcionais
                    // (operador OU (|), então sai do laço interrompendo o envio de novas notificações
                    break;
                }
            }
        }
    }

    @Override
    public int countBoletoServidor(TransferObject criterio, AcessoSistema responsavel) throws BoletoServidorControllerException {
        try {
            ListaBoletoServidorQuery query = new ListaBoletoServidorQuery();
            query.count = true;
            query.responsavel = responsavel;

            if (criterio != null) {
                query.serCpf = (String) criterio.getAttribute(Columns.SER_CPF);
                query.serNome = (String) criterio.getAttribute(Columns.SER_NOME);

                if (!TextHelper.isNull(criterio.getAttribute("SOMENTE_NAO_BAIXADO"))) {
                    query.listarSomenteNaoBaixados = (boolean) criterio.getAttribute("SOMENTE_NAO_BAIXADO");
                }
            }

            return query.executarContador();

        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new BoletoServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> listarBoletoServidor(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws BoletoServidorControllerException {
        try {
            ListaBoletoServidorQuery query = new ListaBoletoServidorQuery();
            query.responsavel = responsavel;

            if (offset != -1) {
                query.firstResult = offset;
            }

            if (count != -1) {
                query.maxResults = count;
            }

            if (criterio != null) {
                query.serCpf = (String) criterio.getAttribute(Columns.SER_CPF);
                query.serNome = (String) criterio.getAttribute(Columns.SER_NOME);

                if (!TextHelper.isNull(criterio.getAttribute("SOMENTE_NAO_BAIXADO"))) {
                    query.listarSomenteNaoBaixados = (boolean) criterio.getAttribute("SOMENTE_NAO_BAIXADO");
                }
            }

            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new BoletoServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public TransferObject findBoletoServidor(String bosCodigo, AcessoSistema responsavel) throws BoletoServidorControllerException {
        try {
            TransferObject retorno = new CustomTransferObject();

            // Encontra boleto do servidor
            BoletoServidor bos = BoletoServidorHome.findByPrimaryKey(bosCodigo);

            // Verifica se o boleto encontrado pertence a consignatária do responsável, caso seja consignatária
            if (responsavel.isCsa() && !bos.getConsignataria().getCsaCodigo().equals(responsavel.getCsaCodigo())) {
                throw new BoletoServidorControllerException("mensagem.usuarioNaoTemPermissao", responsavel);
            }

            // Verifica se o boleto encontrado pertence ao servidor responsável, caso seja servidor
            if (responsavel.isSer() && !bos.getServidor().getSerCodigo().equals(responsavel.getSerCodigo())) {
                throw new BoletoServidorControllerException("mensagem.usuarioNaoTemPermissao", responsavel);
            }

            String arqCodigo = bos.getArquivo().getArqCodigo();
            Arquivo arquivo = ArquivoHome.findByPrimaryKey(arqCodigo);

            retorno.setAttribute(Columns.BOS_CODIGO, bos.getBosCodigo());
            retorno.setAttribute(Columns.BOS_CSA_CODIGO, bos.getConsignataria().getCsaCodigo());
            retorno.setAttribute(Columns.BOS_DATA_UPLOAD, bos.getBosDataUpload());
            retorno.setAttribute(Columns.BOS_DATA_DOWNLOAD, bos.getBosDataDownload());
            retorno.setAttribute(Columns.BOS_DATA_EXCLUSAO, bos.getBosDataExclusao());
            retorno.setAttribute(Columns.BOS_SER_CODIGO, bos.getServidor().getSerCodigo());
            retorno.setAttribute(Columns.BOS_USU_CODIGO, bos.getUsuario().getUsuCodigo());

            retorno.setAttribute(Columns.ARQ_CODIGO, arqCodigo);
            retorno.setAttribute(Columns.ARQ_CONTEUDO, arquivo.getArqConteudo());
            retorno.setAttribute(Columns.ARQ_TAR_CODIGO, arquivo.getTipoArquivo().getTarCodigo());

            return retorno;

        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new BoletoServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

}
