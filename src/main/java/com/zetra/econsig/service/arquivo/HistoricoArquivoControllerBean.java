package com.zetra.econsig.service.arquivo;


import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.HistoricoArquivoControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.criptografia.CriptografiaArquivos;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.HistoricoArquivo;
import com.zetra.econsig.persistence.entity.HistoricoArquivoCorHome;
import com.zetra.econsig.persistence.entity.HistoricoArquivoCsaHome;
import com.zetra.econsig.persistence.entity.HistoricoArquivoCseHome;
import com.zetra.econsig.persistence.entity.HistoricoArquivoEstHome;
import com.zetra.econsig.persistence.entity.HistoricoArquivoHome;
import com.zetra.econsig.persistence.entity.HistoricoArquivoOrgHome;
import com.zetra.econsig.persistence.entity.TipoArquivoHome;
import com.zetra.econsig.persistence.query.admin.ListaTipoArquivoQuery;
import com.zetra.econsig.persistence.query.arquivo.ListaHistoricoArquivoQuery;
import com.zetra.econsig.persistence.query.arquivo.ListaHistoricoArquivoUploadQuery;
import com.zetra.econsig.persistence.query.arquivo.ListarHistoricoArquivoDashboardQuery;
import com.zetra.econsig.persistence.query.beneficios.beneficiario.ListaTipoArquivoByTarCodigoQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.TipoArquivoEnum;

/**
 * <p>Title: HistoricoArquivoControllerBean</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class HistoricoArquivoControllerBean implements HistoricoArquivoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(HistoricoArquivoControllerBean.class);

    private static final String DIRETORIO_TEMP = "tmphis";

    @Override
    public Long createHistoricoArquivo(String tipoEntidade, String codigoEntidade, TipoArquivoEnum tipoArquivo, String harNomeArquivo, String harObs, Date harDataProc, Date harPeriodo, String harResultadoProc, AcessoSistema responsavel) throws HistoricoArquivoControllerException {
        return createHistoricoArquivo(tipoEntidade, codigoEntidade, tipoArquivo, harNomeArquivo, harObs, harDataProc, harPeriodo, harResultadoProc, null, responsavel);
    }

    @Override
    public Long createHistoricoArquivo(String tipoEntidade, String codigoEntidade, TipoArquivoEnum tipoArquivo, String harNomeArquivo, String harObs,
                                       Date harDataProc, Date harPeriodo, String harResultadoProc, String funCodigo, AcessoSistema responsavel) throws HistoricoArquivoControllerException {
        try {
            // Verifica a quantidade de linhas do arquivo
            String nomeArquivo = null;
            Integer harQtdLinhas = 0;
            boolean removeArquivo = false;
            if (!TextHelper.isNull(harNomeArquivo)) {
                try {
                    if (harNomeArquivo.endsWith(".crypt")) {
                        final File arquivoPlano = CriptografiaArquivos.descriptografarArquivo(harNomeArquivo, false, responsavel);
                        if (arquivoPlano != null) {
                            harNomeArquivo = arquivoPlano.getAbsolutePath();
                            removeArquivo = true;
                        }
                    }

                    // Separa nome e path do arquivo
                    nomeArquivo = harNomeArquivo.substring(harNomeArquivo.lastIndexOf(File.separatorChar) + 1, harNomeArquivo.length());

                    if (nomeArquivo.toLowerCase().contains(".zip")) {
                        // Cria diretório temporário para extração dos arquivos
                        final String pathArquivo = harNomeArquivo.substring(0, harNomeArquivo.lastIndexOf(File.separatorChar)) + File.separatorChar + DIRETORIO_TEMP;
                        final File diretorio = new File(pathArquivo);
                        if (!diretorio.mkdir()) {
                            LOG.error("Não foi possível criar o diretório.");
                        }
                        // Extrai os arquivos
                        FileHelper.unZip(harNomeArquivo, pathArquivo);
                        // Conta o número de linhas de cada arquivo do diretório
                        final File arquivos[] = diretorio.listFiles();
                        for (final File arquivo : arquivos) {
                            harQtdLinhas += FileHelper.getNumberOfLines(arquivo.getAbsolutePath());
                        }
                        // Exclui o diretório temporário
                        FileHelper.deleteDir(pathArquivo);
                    } else {
                        // Conta o número de linhas do arquivo txt
                        harQtdLinhas = FileHelper.getNumberOfLines(harNomeArquivo);
                    }
                } catch (final IOException e) {
                    LOG.error("Não foi possível determinar a quantidade de linhas do arquivo: ['" + harNomeArquivo + "'].", e);
                }
            }

            final HistoricoArquivo historicoArquivo = HistoricoArquivoHome.create(responsavel.getUsuCodigo(), tipoArquivo, nomeArquivo, harObs, harDataProc, harPeriodo, harQtdLinhas, harResultadoProc, funCodigo);

            if (TextHelper.isNull(tipoEntidade)) {
                tipoEntidade = !TextHelper.isNull(responsavel.getTipoEntidade()) ? responsavel.getTipoEntidade() : "CSE";
            }
            if (TextHelper.isNull(codigoEntidade)) {
                codigoEntidade = !TextHelper.isNull(responsavel.getTipoEntidade()) ? responsavel.getCodigoEntidade() : CodedValues.CSE_CODIGO_SISTEMA;
            }

            if ("CSE".equalsIgnoreCase(tipoEntidade) || "SUP".equalsIgnoreCase(tipoEntidade)) {
                HistoricoArquivoCseHome.create(codigoEntidade, historicoArquivo.getHarCodigo());
            } else if ("EST".equalsIgnoreCase(tipoEntidade)) {
                HistoricoArquivoEstHome.create(codigoEntidade, historicoArquivo.getHarCodigo());
            } else if ("ORG".equalsIgnoreCase(tipoEntidade)) {
                HistoricoArquivoOrgHome.create(codigoEntidade, historicoArquivo.getHarCodigo());
            } else if ("CSA".equalsIgnoreCase(tipoEntidade)) {
                HistoricoArquivoCsaHome.create(codigoEntidade, historicoArquivo.getHarCodigo());
            } else if ("COR".equalsIgnoreCase(tipoEntidade)) {
                HistoricoArquivoCorHome.create(codigoEntidade, historicoArquivo.getHarCodigo());
            } else {
                throw new com.zetra.econsig.exception.CreateException("mensagem.erro.nao.possivel.criar.historico.arquivo", responsavel);
            }

            if (removeArquivo) {
                // Caso o arquivo originalmente está criptografado e foi descriptogradado para o download
                // então remove o arquivo gerado para o envio ao usuário
                new File(harNomeArquivo).delete();
            }

            return historicoArquivo.getHarCodigo();
        } catch (final com.zetra.econsig.exception.CreateException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(e.getMessage(), e);
            throw new HistoricoArquivoControllerException("mensagem.erro.nao.possivel.criar.historico.arquivo", responsavel);
        }
    }

    @Override
    public void updateHistoricoArquivo(Long harCodigo, TipoArquivoEnum tipoArquivo, String harObs, Date harPeriodo, String harResultadoProc, AcessoSistema responsavel) throws HistoricoArquivoControllerException {
        try {
            final HistoricoArquivo historicoArquivo = HistoricoArquivoHome.findByPrimaryKey(harCodigo);

            if ((tipoArquivo != null) && !tipoArquivo.equals(TipoArquivoEnum.recuperaTipoArquivo(historicoArquivo.getTipoArquivo().getTarCodigo()))) {
                historicoArquivo.setTipoArquivo(TipoArquivoHome.findByPrimaryKey(tipoArquivo.getCodigo()));
            }
            if (!TextHelper.isNull(harObs)) {
                historicoArquivo.setHarObs(harObs);
            }
            if (harPeriodo != null) {
                historicoArquivo.setHarPeriodo(harPeriodo);
            }
            if (!TextHelper.isNull(harResultadoProc) && !harResultadoProc.equals(historicoArquivo.getHarResultadoProc())) {
                historicoArquivo.setHarResultadoProc(harResultadoProc);
            }

            AbstractEntityHome.update(historicoArquivo);

        } catch (final FindException e) {
            LOG.error(e.getMessage(), e);
            throw new HistoricoArquivoControllerException("mensagem.erro.nao.possivel.alterar.historico.arquivo", responsavel);
        } catch (final UpdateException e) {
            LOG.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new HistoricoArquivoControllerException("mensagem.erro.nao.possivel.alterar.historico.arquivo", responsavel);
        }
    }

    @Override
    public List<TransferObject> lstTiposArquivo(AcessoSistema responsavel) throws HistoricoArquivoControllerException {
        try {
            final ListaTipoArquivoQuery query = new ListaTipoArquivoQuery();
            return query.executarDTO();

        } catch (final HQueryException ex) {
            throw new HistoricoArquivoControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> lstTiposArquivoByTarCodigos(List<String> tarCodigos, AcessoSistema responsavel) throws HistoricoArquivoControllerException {
        try {
            final ListaTipoArquivoByTarCodigoQuery query = new ListaTipoArquivoByTarCodigoQuery();
            query.tarCodigos = tarCodigos;

            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new HistoricoArquivoControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> lstHistoricoArquivosDashboard(String csaCodigo, Date filterPeriodo, AcessoSistema responsavel) throws HistoricoArquivoControllerException {
        try {
            final ListarHistoricoArquivoDashboardQuery query = new ListarHistoricoArquivoDashboardQuery(csaCodigo, filterPeriodo);
            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new HistoricoArquivoControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> lstHistoricoArquivo(List<String> tarCodigos, Date harPeriodo, String tipoEntidade, AcessoSistema responsavel) throws HistoricoArquivoControllerException {

        try {
            final ListaHistoricoArquivoQuery query = new ListaHistoricoArquivoQuery();
            query.periodo = harPeriodo;
            query.tarCodigo = tarCodigos;
            query.tipoEntidade = tipoEntidade;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new HistoricoArquivoControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> lstHistoricoArquivoUpload(List<String> tarCodigos, Date harPeriodo, String tipoEntidade, String funCodigo, AcessoSistema responsavel) throws HistoricoArquivoControllerException {

        try {
            final ListaHistoricoArquivoUploadQuery query = new ListaHistoricoArquivoUploadQuery();
            query.periodo = harPeriodo;
            query.tarCodigo = tarCodigos;
            query.tipoEntidade = tipoEntidade;
            query.funCodigo = funCodigo;
            if (responsavel.isOrg()) {
                if(responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                    query.estCodigo = responsavel.getEstCodigo();
                } else {
                    query.orgCodigo = responsavel.getOrgCodigo();
                }
            } else if (responsavel.isCsa() && AcessoSistema.ENTIDADE_CSA.equals(tipoEntidade)) {
                query.csaCodigo = responsavel.getCodigoEntidade();
            }
            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new HistoricoArquivoControllerException(ex);
        }
    }

}
