package com.zetra.econsig.service.folha;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ImportaContrachequesException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.folha.contracheque.ImportaContracheques;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.criptografia.CriptografiaArquivos;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.contracheque.ListaContrachequeQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ImpArqContrachequeControllerBean</p>
 * <p>Description: Session Bean para a rotina de importação de arquivo de contracheques.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ImpArqContrachequeControllerBean implements ImpArqContrachequeController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImpArqContrachequeControllerBean.class);

    /**
     * Realiza a importação do arquivo de contracheques
     * @param nomeArquivo    : nome do arquivo contendo os contracheques
     * @param periodo        : período dos contracheques
     * @param responsavel    : responsável pela importação
     * @param tipoEntidade   : CSE/EST/ORG (Atualmente não é utilizado)
     * @param codigoEntidade : código de acordo com tipoEntidade (Atualmente não é utilizado)
     * @throws ServidorControllerException
     */
    @Override
    public void importaArquivoContracheques(String nomeArquivo, Date periodo, String tipoEntidade, String codigoEntidade, Boolean sobrepoe, AcessoSistema responsavel) throws ServidorControllerException {
        importaArquivoContracheques(nomeArquivo, periodo, tipoEntidade, codigoEntidade, sobrepoe, false, responsavel);
    }

    /**
     * Realiza a importação do arquivo de contracheques
     * @param nomeArquivo    : nome do arquivo contendo os contracheques
     * @param periodo        : período dos contracheques
     * @param responsavel    : responsável pela importação
     * @param tipoEntidade   : CSE/EST/ORG (Atualmente não é utilizado)
     * @param codigoEntidade : código de acordo com tipoEntidade (Atualmente não é utilizado)
     * @param ativo          : se true, importa somente servidor ativo
     * @throws ServidorControllerException
     */
    @Override
    public void importaArquivoContracheques(String nomeArquivo, Date periodo, String tipoEntidade, String codigoEntidade, Boolean sobrepoe, Boolean ativo, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            // Criar parametro de sistema para determinar qual classe utilizar
            String importadorContrachequeClassName = (String) ParamSist.getInstance().getParam(CodedValues.TPC_CLASSE_IMPORTADOR_CONTRACHEQUES, responsavel);
            if (!TextHelper.isNull(importadorContrachequeClassName)) {
                ImportaContracheques importador = (ImportaContracheques) Class.forName(importadorContrachequeClassName).getDeclaredConstructor().newInstance();
                importador.setSobrepoe(sobrepoe);
                importador.setAtivo(ativo);

                if (nomeArquivo.endsWith(".crypt")) {
                    File arquivoPlano = CriptografiaArquivos.descriptografarArquivo(nomeArquivo, true, responsavel);
                    if (arquivoPlano != null) {
                        nomeArquivo = arquivoPlano.getAbsolutePath();
                    }
                }

                File arquivo = new File(nomeArquivo);
                String nomeArqOriginal = null;
                boolean isZip = false;
                //confere se arquivo importado é ZIP. Neste caso, faz o unzip para a importação
                if (FileHelper.isZip(nomeArquivo)) {
                	isZip = true;
                	nomeArqOriginal = nomeArquivo;
                	nomeArquivo = FileHelper.unZip(nomeArquivo, arquivo.getAbsolutePath().substring(0, arquivo.getAbsolutePath().lastIndexOf(File.separatorChar)));
                }

                importador.importaArquivoContracheques(nomeArquivo, periodo, tipoEntidade, codigoEntidade, responsavel);

                if (isZip) {
                	// remove o arquivo descompactado utilizado na importação, deixando só o zip original no diretório
                	File arqAux = new File(nomeArquivo);
                	arqAux.delete();
                }

                // Renomeia o arquivo de entrada depois de concluido com sucesso
                nomeArquivo = !TextHelper.isNull(nomeArqOriginal) ? nomeArqOriginal:nomeArquivo;
                arquivo.renameTo(new File(nomeArquivo + ".ok"));

                // Grava log da Operação
                LogDelegate log = new LogDelegate(responsavel, Log.FOLHA, Log.IMP_ARQ_CONTRACHEQUES, Log.LOG_INFORMACAO);
                log.add(ApplicationResourcesHelper.getMessage("rotulo.log.arquivo", responsavel, nomeArquivo));
                log.write();
            } else {
                LOG.error("Não existe classe de importação de contracheques configurada para este sistema.");
            }
        } catch (ImportaContrachequesException ex) {
            throw new ServidorControllerException("mensagem.erro.contracheque.processar.arquivo", responsavel, ex, nomeArquivo);
        } catch (ClassNotFoundException ex) {
            throw new ServidorControllerException("mensagem.erro.contracheque.classe.importacao.nao.encontrada", responsavel, ex);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException ex) {
            throw new ServidorControllerException("mensagem.erro.contracheque.instanciar.classe.importacao", responsavel, ex);
        } catch (LogControllerException ex) {
            LOG.error("Um erro ocorreu ao gravar o log da operação de importação de contracheques.", ex);
        } catch (FileNotFoundException ex) {
        	throw new ServidorControllerException("mensagem.erro.contracheque.processar.arquivo", responsavel, ex, nomeArquivo);
		} catch (IOException ex) {
			throw new ServidorControllerException("mensagem.erro.contracheque.processar.arquivo", responsavel, ex, nomeArquivo);
		}
    }
    /**
     * Lista os contracheques de servidores, de acordo com os parâmetros.
     * @param rseCodigo
     * @param ccqPeriodo
     * @param obtemUltimo
     * @param responsavel
     * @return
     * @throws ServidorControllerException
     */
    @Override
    public List<TransferObject> listarContrachequeRse(String rseCodigo, Date ccqPeriodo, boolean obtemUltimo, AcessoSistema responsavel) throws ServidorControllerException {
        return listarContrachequeRse(rseCodigo, ccqPeriodo, obtemUltimo, -1, false, responsavel);
    }

    /**
     * Lista os contracheques de servidores, de acordo com os parâmetros.
     * @param rseCodigo
     * @param ccqPeriodo
     * @param obtemUltimo
     * @param ordem
     * @param count
     * @param responsavel
     * @return
     * @throws ServidorControllerException
     */
    @Override
    public List<TransferObject> listarContrachequeRse(String rseCodigo, Date ccqPeriodo, boolean obtemUltimo, int count, boolean ordemDesc, AcessoSistema responsavel) throws ServidorControllerException {
        return listarContrachequeRse(rseCodigo, ccqPeriodo, obtemUltimo, -1, false, null, null, responsavel);
    }

    @Override
    public List<TransferObject> listarContrachequeRse(String rseCodigo, Date ccqPeriodo, boolean obtemUltimo, int count, boolean ordemDesc, Date dataInicio, Date dataFim, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            // Seta os critérios da query
            ListaContrachequeQuery query = new ListaContrachequeQuery();
            query.rseCodigo = rseCodigo;
            query.ccqPeriodo = ccqPeriodo;
            query.obtemUltimo = obtemUltimo;
            query.ordemDesc = ordemDesc;
            query.dataInicio = dataInicio;
            query.dataFim = dataFim;

            if (count != -1) {
                query.maxResults = count;
            }

            // Lista os resultados
            List<TransferObject> result = query.executarDTO();

            // Grava log da operação
            LogDelegate log = new LogDelegate(responsavel, Log.REGISTRO_SERVIDOR, Log.SELECT, Log.LOG_INFORMACAO);
            log.setRegistroServidor(rseCodigo);
            log.add(ApplicationResourcesHelper.getMessage("rotulo.log.consultando.contracheque", responsavel));
            if (ccqPeriodo != null) {
                log.add(ApplicationResourcesHelper.getMessage("rotulo.log.periodo", responsavel, DateHelper.format(ccqPeriodo, "MM/yyyy")));
            }
            log.write();

            return result;
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
