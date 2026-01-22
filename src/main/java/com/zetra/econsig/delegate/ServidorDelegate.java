package com.zetra.econsig.delegate;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.HistoricoArquivoControllerException;
import com.zetra.econsig.exception.MargemControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.folha.ImpArqContrachequeController;
import com.zetra.econsig.service.folha.ImpCadMargemController;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.service.servidor.ImpArqSerDesligadoBloqueadoController;
import com.zetra.econsig.service.servidor.ImpBloqueioServidorController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.AcaoTipoDadoAdicionalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ServidorDelegate</p>
 * <p>Description: Delegate para a operações relacionada a Servidores.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ServidorDelegate extends AbstractDelegate {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ServidorDelegate.class);

    private PesquisarServidorController pesquisarServidorController = null;
    private ServidorController serController = null;
    private ImpCadMargemController impCadMargemController = null;
    private ImpBloqueioServidorController impBloqueioServidorController = null;
    private ImpArqSerDesligadoBloqueadoController impArqSerDesligadoBloqueadoController = null;
    private MargemController margemController = null;

    private ServidorController getServidorController() throws ServidorControllerException {
        try {
            if (serController == null) {
                serController = ApplicationContextProvider.getApplicationContext().getBean(ServidorController.class);
            }
            return serController;
        } catch (Exception ex) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    private PesquisarServidorController getPesquisarServidorController() throws ServidorControllerException {
        try {
            if (pesquisarServidorController == null) {
                pesquisarServidorController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarServidorController.class);
            }
            return pesquisarServidorController;
        } catch (Exception ex) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    private ImpCadMargemController getImpCadMargemController() throws ServidorControllerException {
        try {
            if (impCadMargemController == null) {
                impCadMargemController = ApplicationContextProvider.getApplicationContext().getBean(ImpCadMargemController.class);
            }
            return impCadMargemController;
        } catch (Exception ex) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    private MargemController getMargemController() throws MargemControllerException {
        try {
            if (margemController == null) {
                margemController = ApplicationContextProvider.getApplicationContext().getBean(MargemController.class);
            }
            return margemController;
        } catch (Exception ex) {
            throw new MargemControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    private ImpBloqueioServidorController getImpBloqueioServidorController() throws ServidorControllerException {
        try {
            if (impBloqueioServidorController == null) {
                impBloqueioServidorController = ApplicationContextProvider.getApplicationContext().getBean(ImpBloqueioServidorController.class);
            }
            return impBloqueioServidorController;
        } catch (Exception ex) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    private ImpArqSerDesligadoBloqueadoController getImpArqSerDesligadoBloqueadoController() throws ServidorControllerException {
        try {
            if (impArqSerDesligadoBloqueadoController == null) {
                impArqSerDesligadoBloqueadoController = ApplicationContextProvider.getApplicationContext().getBean(ImpArqSerDesligadoBloqueadoController.class);
            }
            return impArqSerDesligadoBloqueadoController;
        } catch (Exception ex) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }

    }

    /**************************** MÉTODOS DE NEGÓCIO *******************************/

    public String cadastrarServidor(ServidorTransferObject servidor, RegistroServidorTO registroServidor, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().cadastrarServidor(servidor, registroServidor, responsavel);
    }

    public ServidorTransferObject findServidor(ServidorTransferObject servidor, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().findServidor(servidor, responsavel);
    }

    public ServidorTransferObject findServidor(String serCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().findServidor(new ServidorTransferObject(serCodigo), responsavel);
    }

    public ServidorTransferObject findServidor(String serCpf, String rseMatricula, String serNroIdt, java.sql.Date serDataNasc, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().findServidor(serCpf, rseMatricula, serNroIdt, serDataNasc, responsavel);
    }

    public ServidorTransferObject findServidorByRseCodigo(String rseCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().findServidorByRseCodigo(rseCodigo, responsavel);
    }

    public void updateServidor(ServidorTransferObject servidor, AcessoSistema responsavel) throws ServidorControllerException {
        getServidorController().updateServidor(servidor, responsavel);
    }

    public void updateServidor(ServidorTransferObject servidor, String tocCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        getServidorController().updateServidor(servidor, tocCodigo, responsavel);
    }

    public CustomTransferObject buscaServidor(String rseCodigo, String serCodigo, boolean retornaMargem, boolean retornaUsuLogin, AcessoSistema responsavel) throws ServidorControllerException {
        return getPesquisarServidorController().buscaServidor(rseCodigo, serCodigo, retornaMargem, retornaUsuLogin, responsavel);
    }

    public CustomTransferObject buscaServidor(String rseCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        return getPesquisarServidorController().buscaServidor(rseCodigo, null, false, false, responsavel);
    }

    public CustomTransferObject buscaServidor(String rseCodigo, boolean retornaMargem, AcessoSistema responsavel) throws ServidorControllerException {
        return getPesquisarServidorController().buscaServidor(rseCodigo, null, retornaMargem, false, responsavel);
    }

    public CustomTransferObject buscaServidor(String rseCodigo, String serCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        return getPesquisarServidorController().buscaServidor(rseCodigo, serCodigo, false, false, responsavel);
    }

    public int countRegistroServidor(List<String> srsCodigos, List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().countRegistroServidor(srsCodigos, orgCodigos, estCodigos, responsavel);
    }

    public int countRegistroServidorTransferidos(AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().countRegistroServidorTransferidos(responsavel);
    }

    public int countRegistroServidorExcluidos(AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().countRegistroServidorExcluidos(responsavel);
    }

    public List<TransferObject> lstRegistroServidor(List<String> srsCodigos, List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().lstRegistroServidor(srsCodigos, orgCodigos, estCodigos, responsavel);
    }

    public List<TransferObject> lstRegistroServidor(String serCodigo, String orgCodigo, String estCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().lstRegistroServidor(serCodigo, orgCodigo, estCodigo, responsavel);
    }

    public List<TransferObject> lstRegistroServidor(String serCodigo, List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().lstRegistroServidor(serCodigo, orgCodigos, estCodigos, responsavel);
    }

    public List<TransferObject> lstRegistroServidor(String serCodigo, List<String> orgCodigos, List<String> estCodigos, boolean recuperaRseExcluidos, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().lstRegistroServidor(serCodigo, orgCodigos, estCodigos, recuperaRseExcluidos, responsavel);
    }

    public List<TransferObject> lstRegistroServidorAuditoriaTotal(AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().lstRegistroServidorAuditoriaTotal(responsavel);
    }

    public List<TransferObject> countQtdeServidorPorOrg(AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().countQtdeServidorPorOrg(responsavel);
    }

    public List<TransferObject> lstOrsRegistroServidor(TransferObject toOrsRegistroServidor, int offset, int count, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().lstOrsRegistroServidor(toOrsRegistroServidor, offset, count, responsavel);
    }

    public RegistroServidorTO findRegistroServidor(RegistroServidorTO registroServidor, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().findRegistroServidor(registroServidor, false, responsavel);
    }

    public RegistroServidorTO findRegistroServidor(String rseCodigo, boolean retornaMargem, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().findRegistroServidor(new RegistroServidorTO(rseCodigo), retornaMargem, responsavel);
    }

    public RegistroServidorTO findRegistroServidor(String rseCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().findRegistroServidor(new RegistroServidorTO(rseCodigo), false, responsavel);
    }

    public List<RegistroServidorTO> findRegistroServidorBySerCodigo(String serCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().findRegistroServidorBySerCodigo(serCodigo, responsavel);
    }

    public void updateRegistroServidorSemHistoricoMargem(RegistroServidorTO registroServidor, AcessoSistema responsavel) throws ServidorControllerException {
        getServidorController().updateRegistroServidorSemHistoricoMargem(registroServidor, responsavel);
    }

    public void updateRegistroServidor(RegistroServidorTO registroServidor, boolean validaMargem, boolean calculaMargem, boolean transferenciaMargem, AcessoSistema responsavel) throws ServidorControllerException {
        getServidorController().updateRegistroServidor(registroServidor, null, validaMargem, calculaMargem, transferenciaMargem, true, responsavel);
    }

    public void updateRegistroServidor(RegistroServidorTO registroServidor, List<MargemTO> margens, boolean validaMargem, boolean calculaMargem, boolean transferenciaMargem, AcessoSistema responsavel) throws ServidorControllerException {
        getServidorController().updateRegistroServidor(registroServidor, margens, validaMargem, calculaMargem, transferenciaMargem, true, responsavel);
    }

    public TransferObject getRegistroServidorPelaMatricula(String serCodigo, String orgCodigo, String estCodigo, String rseMatricula, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().getRegistroServidorPelaMatricula(serCodigo, orgCodigo, estCodigo, rseMatricula, responsavel);
    }

    // Pesquisa Servidor
    public List<TransferObject> pesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, AcessoSistema responsavel) throws ServidorControllerException {
        return getPesquisarServidorController().pesquisaServidor(tipo, codigo, estIdentificador, orgIdentificador, rseMatricula, serCPF, responsavel, true, null, false, null);
    }

    public List<TransferObject> pesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, AcessoSistema responsavel, boolean validaCpfMatricula, boolean validaPermissionario) throws ServidorControllerException {
        return getPesquisarServidorController().pesquisaServidor(tipo, codigo, estIdentificador, orgIdentificador, rseMatricula, serCPF, responsavel, validaCpfMatricula, null, validaPermissionario, null);
    }

    public List<TransferObject> pesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, AcessoSistema responsavel, boolean validaCpfMatricula, List<String> rseSrsCodigo, boolean validaPermissionario) throws ServidorControllerException {
        return getPesquisarServidorController().pesquisaServidor(tipo, codigo, estIdentificador, orgIdentificador, rseMatricula, serCPF, responsavel, validaCpfMatricula, rseSrsCodigo, validaPermissionario, null);
    }

    public List<TransferObject> pesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, AcessoSistema responsavel, boolean validaCpfMatricula, List<String> rseSrsCodigo, boolean validaPermissionario, String orgCodigo) throws ServidorControllerException {
        return getPesquisarServidorController().pesquisaServidor(tipo, codigo, estIdentificador, orgIdentificador, rseMatricula, serCPF, responsavel, validaCpfMatricula, rseSrsCodigo, validaPermissionario, orgCodigo);
    }

    public List<TransferObject> pesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, int offset, int count, AcessoSistema responsavel) throws ServidorControllerException {
        return getPesquisarServidorController().pesquisaServidor(tipo, codigo, estIdentificador, orgIdentificador, rseMatricula, serCPF, offset, count, responsavel, true, null, false, null);
    }

    public List<TransferObject> pesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, int offset, int count, AcessoSistema responsavel, boolean validaCpfMatricula, List<String> rseSrsCodigo, boolean validaPermissionario) throws ServidorControllerException {
        return getPesquisarServidorController().pesquisaServidor(tipo, codigo, estIdentificador, orgIdentificador, rseMatricula, serCPF, offset, count, responsavel, validaCpfMatricula, rseSrsCodigo, validaPermissionario, null);
    }

    public List<TransferObject> pesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, int offset, int count, AcessoSistema responsavel, boolean validaCpfMatricula, List<String> rseSrsCodigo, boolean validaPermissionario, String orgCodigo) throws ServidorControllerException {
        return getPesquisarServidorController().pesquisaServidor(tipo, codigo, estIdentificador, orgIdentificador, rseMatricula, serCPF, offset, count, responsavel, validaCpfMatricula, rseSrsCodigo, validaPermissionario, orgCodigo);
    }

    public List<TransferObject> pesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, int offset, int count, AcessoSistema responsavel, boolean validaCpfMatricula, List<String> rseSrsCodigo, boolean validaPermissionario, String orgCodigo, TransferObject criterios) throws ServidorControllerException {
        return getPesquisarServidorController().pesquisaServidor(tipo, codigo, estIdentificador, orgIdentificador, rseMatricula, serCPF, offset, count, responsavel, validaCpfMatricula, rseSrsCodigo, validaPermissionario, orgCodigo, criterios);
    }

    public List<TransferObject> pesquisaServidor(String tipo, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, int offSet, int count, AcessoSistema responsavel, boolean validaCpfMatricula, List<String> rseSrsCodigo, boolean validaPermissionario, String orgCodigo, TransferObject criterios, String vrsCodigo, boolean retornaUsuLogin) throws ServidorControllerException {
        return getPesquisarServidorController().pesquisaServidor(tipo, codigo, estIdentificador, orgIdentificador, rseMatricula, serCPF, offSet, count, responsavel, validaCpfMatricula, rseSrsCodigo, validaPermissionario, orgCodigo, criterios, vrsCodigo, retornaUsuLogin, null);
    }

    // Pesquisa Cargo
    public List<TransferObject> lstCargo(AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().lstCargo(responsavel);
    }

    // Pesquisa Cargo
    public List<TransferObject> findCargoByIdentificador(String crsIdentificador, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().findCargoByIdentificador(crsIdentificador, responsavel);
    }

    // Pesquisa Padrão
    public List<TransferObject> lstPadrao(AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().lstPadrao(responsavel);
    }

    public List<TransferObject> findPadraoByIdentificador(String prsIdentificador, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().findPadraoByIdentificador(prsIdentificador, responsavel);
    }

    // Pesquisa SubOrgão
    public List<TransferObject> lstSubOrgao(AcessoSistema responsavel, String orgCodigo) throws ServidorControllerException {
        return getServidorController().lstSubOrgao(responsavel, orgCodigo);
    }

    public List<TransferObject> findSubOrgaoByIdentificador(String sboIdentificador, String orgCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().findSubOrgaoByIdentificador(sboIdentificador, orgCodigo, responsavel);
    }

    // Pesquisa Unidade
    public List<TransferObject> lstUnidade(AcessoSistema responsavel, String sboCodigo) throws ServidorControllerException {
        return getServidorController().lstUnidade(responsavel, sboCodigo);
    }

    public List<TransferObject> findUnidadeByIdentificador(String uniIdentificador, String sboCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().findUnidadeByIdentificador(uniIdentificador, sboCodigo, responsavel);
    }

    // Recalcula margem
    public void recalculaMargem(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws MargemControllerException {
        getMargemController().recalculaMargem(tipoEntidade, entCodigos, responsavel);
    }

    public void recalculaMargemComHistorico(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws MargemControllerException {
        getMargemController().recalculaMargemComHistorico(tipoEntidade, entCodigos, responsavel);
    }

    public String importaCadastroMargens(String nomeArquivoEntrada, String tipoEntidade, String codigoEntidade, boolean margemTotal, boolean geraTransferidos, AcessoSistema responsavel) throws ServidorControllerException {
        Long harCodigo = null;
        boolean gerouException = false;
        try {
            try {
                String harObs = "";
                if (margemTotal) {
                    harObs += "Importa margem total. ";
                }
                if (geraTransferidos) {
                    harObs += "Gera arquivo de transferidos.";
                }

                HistoricoArquivoDelegate hisArqDelegate = new HistoricoArquivoDelegate();
                String harResultado = CodedValues.STS_INATIVO.toString();
                harCodigo = hisArqDelegate.createHistoricoArquivo(tipoEntidade, codigoEntidade, TipoArquivoEnum.ARQUIVO_CADASTRO_MARGENS, nomeArquivoEntrada, harObs, null, null, harResultado, CodedValues.FUN_IMP_CAD_MARGENS, responsavel);
            } catch (HistoricoArquivoControllerException e) {
                LOG.error("Não foi possível inserir o histórico do arquivo de margem '" + nomeArquivoEntrada + "'.", e);
            }

            // Importa o cadastro de margens
            String resultImport = getImpCadMargemController().importaCadastroMargens(nomeArquivoEntrada, tipoEntidade, codigoEntidade, margemTotal, geraTransferidos, responsavel);

            return resultImport;
        } catch (ServidorControllerException ex) {
            gerouException = true;
            throw ex;
        } finally {
            if (harCodigo != null) {
                try {
                    Date pexPeriodo = null;
                    try {
                        List<String> codigos = null;
                        if (!TextHelper.isNull(codigoEntidade)) {
                            codigos = new ArrayList<>();
                            codigos.add(codigoEntidade);
                        }
                        List<String> orgCodigos = ("ORG".equalsIgnoreCase(tipoEntidade) ? codigos : null);
                        List<String> estCodigos = ("EST".equalsIgnoreCase(tipoEntidade) ? codigos : null);

                        PeriodoDelegate perDelegate = new PeriodoDelegate();
                        TransferObject to = perDelegate.obtemPeriodoExportacaoDistinto(orgCodigos, estCodigos, responsavel);
                        pexPeriodo = DateHelper.parse(to.getAttribute(Columns.PEX_PERIODO).toString(), "yyyy-MM-dd");
                    } catch (Exception e) {
                        LOG.error("Não foi possível localizar o período atual de exportação.", e);
                    }

                    String harResultado = CodedValues.STS_ATIVO.toString();
                    if (gerouException) {
                        harResultado = CodedValues.STS_INATIVO.toString();
                    }
                    HistoricoArquivoDelegate hisArqDelegate = new HistoricoArquivoDelegate();
                    hisArqDelegate.updateHistoricoArquivo(harCodigo, null, null, pexPeriodo, harResultado, responsavel);
                } catch (HistoricoArquivoControllerException e) {
                    LOG.error("Não foi possível inserir o histórico do arquivo de margem '" + nomeArquivoEntrada + "'.", e);
                }
            }
        }
    }

    public void importaServidoresTransferidos(String nomeArquivo, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ServidorControllerException {
        Long harCodigo = null;
        boolean gerouException = false;
        try {
            try {
                String harObs = "";
                HistoricoArquivoDelegate hisArqDelegate = new HistoricoArquivoDelegate();
                String harResultado = CodedValues.STS_INATIVO.toString();
                harCodigo = hisArqDelegate.createHistoricoArquivo(tipoEntidade, codigoEntidade, TipoArquivoEnum.ARQUIVO_TRANSFERIDOS, nomeArquivo, harObs, null, null, harResultado, responsavel);
            } catch (HistoricoArquivoControllerException e) {
                LOG.error("Não foi possível inserir o histórico do arquivo de transferidos '" + nomeArquivo + "'.", e);
            }

            // Importa o os servidores transferidos
            getImpCadMargemController().importaServidoresTransferidos(nomeArquivo, tipoEntidade, codigoEntidade, responsavel);

        } catch (ServidorControllerException ex) {
            gerouException = true;
            throw ex;
        } catch (Exception ex) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } finally {
            if (harCodigo != null) {
                try {
                    Date pexPeriodo = null;
                    try {
                        List<String> codigos = null;
                        if (!TextHelper.isNull(codigoEntidade)) {
                            codigos = new ArrayList<>();
                            codigos.add(codigoEntidade);
                        }
                        List<String> orgCodigos = ("ORG".equalsIgnoreCase(tipoEntidade) ? codigos : null);
                        List<String> estCodigos = ("EST".equalsIgnoreCase(tipoEntidade) ? codigos : null);

                        PeriodoDelegate perDelegate = new PeriodoDelegate();
                        TransferObject to = perDelegate.obtemPeriodoExportacaoDistinto(orgCodigos, estCodigos, responsavel);
                        pexPeriodo = DateHelper.parse(to.getAttribute(Columns.PEX_PERIODO).toString(), "yyyy-MM-dd");
                    } catch (Exception e) {
                        LOG.error("Não foi possível localizar o período atual de exportação.", e);
                    }

                    String harResultado = CodedValues.STS_ATIVO.toString();
                    if (gerouException) {
                        harResultado = CodedValues.STS_INATIVO.toString();
                    }
                    HistoricoArquivoDelegate hisArqDelegate = new HistoricoArquivoDelegate();
                    hisArqDelegate.updateHistoricoArquivo(harCodigo, null, null, pexPeriodo, harResultado, responsavel);
                } catch (HistoricoArquivoControllerException e) {
                    LOG.error("Não foi possível inserir o histórico do arquivo de transferidos '" + nomeArquivo + "'.", e);
                }
            }
        }
    }

    public boolean qtdLinhasArqTransferidosAcimaPermitido(String nomeArquivo, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ServidorControllerException {
        return getImpCadMargemController().qtdLinhasArqTransferidosAcimaPermitido(nomeArquivo, tipoEntidade, codigoEntidade, responsavel);
    }

    public void importaArquivoContracheques(String nomeArquivo, Date periodo, String tipoEntidade, String codigoEntidade, Boolean sobrepoe, Boolean ativo, AcessoSistema responsavel) throws ServidorControllerException {
        Long harCodigo = null;
        boolean gerouException = false;
        try {
            try {
                String harObs = "";
                HistoricoArquivoDelegate hisArqDelegate = new HistoricoArquivoDelegate();
                String harResultado = CodedValues.STS_INATIVO.toString();
                harCodigo = hisArqDelegate.createHistoricoArquivo(tipoEntidade, codigoEntidade, TipoArquivoEnum.ARQUIVO_CONTRACHEQUES, nomeArquivo, harObs, null, periodo, harResultado, responsavel);
            } catch (HistoricoArquivoControllerException e) {
                LOG.error("Não foi possível inserir o histórico do arquivo de contracheques '" + nomeArquivo + "'.", e);
            }

            // Importa o os servidores transferidos
            ImpArqContrachequeController impArqContrachequeController = ApplicationContextProvider.getApplicationContext().getBean(ImpArqContrachequeController.class);
            impArqContrachequeController.importaArquivoContracheques(nomeArquivo, periodo, tipoEntidade, codigoEntidade, sobrepoe, ativo, responsavel);

        } catch (ServidorControllerException ex) {
            gerouException = true;
            throw ex;
        } catch (Exception ex) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } finally {
            if (harCodigo != null) {
                try {
                    String harResultado = CodedValues.STS_ATIVO.toString();
                    if (gerouException) {
                        harResultado = CodedValues.STS_INATIVO.toString();
                    }
                    HistoricoArquivoDelegate hisArqDelegate = new HistoricoArquivoDelegate();
                    hisArqDelegate.updateHistoricoArquivo(harCodigo, null, null, null, harResultado, responsavel);
                } catch (HistoricoArquivoControllerException e) {
                    LOG.error("Não foi possível inserir o histórico do arquivo de contracheques '" + nomeArquivo + "'.", e);
                }
            }
        }
    }

    public void importarBloqueioServidor(String nomeArquivo, AcessoSistema responsavel) throws ServidorControllerException {
        Long harCodigo = null;
        boolean gerouException = false;
        try {
            // Insere histórico do arquivo
            try {
                String harObs = "";
                String absolutePath = ParamSist.getDiretorioRaizArquivos();
                String pathFile;
                String fileName;

                if (responsavel.isCsa()) {
                    pathFile = absolutePath + File.separatorChar + "bloqueio_ser" + File.separatorChar + "csa";
                    fileName = pathFile + File.separatorChar + responsavel.getCsaCodigo() + File.separatorChar + nomeArquivo;
                } else {
                    pathFile = absolutePath + File.separatorChar + "bloqueio_ser" + File.separatorChar + "cse";
                    fileName = pathFile + File.separatorChar + nomeArquivo;
                }

                HistoricoArquivoDelegate hisArqDelegate = new HistoricoArquivoDelegate();
                String harResultado = CodedValues.STS_INATIVO.toString();
                harCodigo = hisArqDelegate.createHistoricoArquivo(null, null, TipoArquivoEnum.ARQUIVO_BLOQUEIO_SERVIDOR, fileName, harObs, null, null, harResultado, responsavel);
            } catch (HistoricoArquivoControllerException e) {
                LOG.error("Não foi possível inserir o histórico do arquivo de bloqueio de servidor '" + nomeArquivo + "'.", e);
            }

            // Importa o bloqueio de servidor
            getImpBloqueioServidorController().importarBloqueioServidor(nomeArquivo, responsavel);
        } catch (ServidorControllerException ex) {
            gerouException = true;
            throw ex;
        } finally {
            if (harCodigo != null) {
                try {
                    Date pexPeriodo = null;
                    String harResultado = CodedValues.STS_ATIVO.toString();
                    if (gerouException) {
                        harResultado = CodedValues.STS_INATIVO.toString();
                    }
                    HistoricoArquivoDelegate hisArqDelegate = new HistoricoArquivoDelegate();
                    hisArqDelegate.updateHistoricoArquivo(harCodigo, null, null, pexPeriodo, harResultado, responsavel);
                } catch (HistoricoArquivoControllerException e) {
                    LOG.error("Não foi possível inserir o histórico do arquivo de bloqueio de servidor '" + nomeArquivo + "'.", e);
                }
            }
        }
    }

    public List<TransferObject> getEstCivil(AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().getEstCivil(responsavel);
    }

    public String getEstCivil(String estCvlCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().getEstCivil(estCvlCodigo, responsavel);
    }

    public List<TransferObject> getNivelEscolaridade(AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().getNivelEscolaridade(responsavel);
    }

    public String getNivelEscolaridade(String nesCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().getNivelEscolaridade(nesCodigo, responsavel);
    }

    public List<TransferObject> getTipoHabitacao(AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().getTipoHabitacao(responsavel);
    }

    public String getTipoHabitacao(String thaCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().getTipoHabitacao(thaCodigo, responsavel);
    }

    public String buscaImgServidor(String serCpf, String rseCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().buscaImgServidor(serCpf, rseCodigo, responsavel);
    }

    public List<TransferObject> selectVincRegistroServidor(boolean ativos, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().selectVincRegistroServidor(ativos, responsavel);
    }

    public List<TransferObject> findVincRegistroServidor(String vrsIdentificador, boolean ativos, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().findVincRegistroServidor(vrsIdentificador, ativos, responsavel);
    }

    public List<TransferObject> lstPosto(AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().lstPosto(responsavel);
    }

    public List<TransferObject> lstTipoRegistroServidor(AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().lstTipoRegistroServidor(responsavel);
    }

    public List<TransferObject> lstCapacidadeCivil(AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().lstCapacidadeCivil(responsavel);
    }

    public List<TransferObject> lstDadosServidor(AcaoTipoDadoAdicionalEnum acao, VisibilidadeTipoDadoAdicionalEnum visibilidade, String serCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().lstDadosServidor(acao, visibilidade, serCodigo, responsavel);
    }

    public String getValorDadoServidor(String serCodigo, String tdaCodigo) throws ServidorControllerException {
        return getServidorController().getValorDadoServidor(serCodigo, tdaCodigo);
    }

    public void setValorDadoServidor(String serCodigo, String tdaCodigo, String dadValor, AcessoSistema responsavel) throws ServidorControllerException {
        getServidorController().setValorDadoServidor(serCodigo, tdaCodigo, dadValor, responsavel);
    }

    public TransferObject findServidorProprietarioAde(String adeCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().findServidorProprietarioAde(adeCodigo, responsavel);
    }

    public CustomTransferObject buscaUsuarioServidor(String usuCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        return getPesquisarServidorController().buscaUsuarioServidor(usuCodigo, responsavel);
    }

    public CustomTransferObject buscaUsuarioServidor(String usuCodigo, String usuLogin, String rseMatricula, String orgIdentificador, String estIdentificador, AcessoSistema responsavel) throws ServidorControllerException {
        return getPesquisarServidorController().buscaUsuarioServidor(usuCodigo, usuLogin, rseMatricula, orgIdentificador, estIdentificador, responsavel);
    }

    public List<TransferObject> lstTipoBaseCalculo(AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().lstTipoBaseCalculo(responsavel);
    }

    public TransferObject sorteiaPerguntaDadosCadastrais(Short pdcGrupo, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().sorteiaPerguntaDadosCadastrais(pdcGrupo, responsavel);
    }

    public boolean validaPerguntaDadosCadastrais(String rseCodigo, Short pdcGrupo, Short pdcNumero, String resposta, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().validaPerguntaDadosCadastrais(rseCodigo, pdcGrupo, pdcNumero, resposta, responsavel);
    }

    public boolean existeEmailCadastrado(String serEmail, String serCpfExceto, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().existeEmailCadastrado(serEmail, serCpfExceto, responsavel);
    }

    public void cadastrarEmailServidor(String rseCodigo, String email, String protocoloCodigo, AcessoSistema responsavel, AcessoSistema usuarioSuporteResponsavel) throws ServidorControllerException {
        getServidorController().cadastrarEmailServidor(rseCodigo, email, protocoloCodigo, responsavel, usuarioSuporteResponsavel);
    }

    public List<TransferObject> lstRegistroServidorPorCpf(String serCpf, List<String> orgCodigos, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().lstRegistroServidorPorCpf(serCpf, orgCodigos, responsavel);
    }

    public List<String> listarCpfServidoresAtivos(AcessoSistema responsavel) throws ServidorControllerException {
        return getPesquisarServidorController().listarCpfServidoresAtivos(responsavel);
    }

    public List<String> listarEmailServidoresAtivos(AcessoSistema responsavel) throws ServidorControllerException {
        return getPesquisarServidorController().listarEmailServidoresAtivos(responsavel);
    }

    public void importaDesligadoBloqueado(String nomeArquivoEntrada, boolean validar, AcessoSistema responsavel) throws ServidorControllerException {
        getImpArqSerDesligadoBloqueadoController().importaDesligadoBloqueado(nomeArquivoEntrada, validar, responsavel);
    }

    public List<TransferObject> listarCodigoServidorConsignacaoAtivaRetorno(List<Integer> diasParam, AcessoSistema responsavel) throws ServidorControllerException {
        return getPesquisarServidorController().listarCodigoServidorConsignacaoAtivaRetorno(diasParam, responsavel);
    }

    public void cancelarCadastroServidor(AcessoSistema responsavel) throws ServidorControllerException {
        getServidorController().cancelarCadastroServidor(responsavel);
    }

    public TransferObject findCargoByCrsCodigo(String crsCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().findCargoByCrsCodigo(crsCodigo, responsavel);
    }

    public List<TransferObject> findRegistroServidoresByMatriculas(List<String> rseMatriculas, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().findRegistroServidoresByMatriculas(rseMatriculas, responsavel);
    }

    public List<TransferObject> listarMargensRse(String rseCodigo, String svcCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        return getServidorController().listarMargensRse(rseCodigo, svcCodigo, responsavel);
    }

    public String getEmailSerNotificacaoOperacao(String funCodigo, String papCodigoOperador, String serCodigo, AcessoSistema responsavel) throws ServidorControllerException {
    	return getServidorController().getEmailSerNotificacaoOperacao(funCodigo, papCodigoOperador, serCodigo, responsavel);
    }

    public List<TransferObject> lstMargemComServicoAtivo(AcessoSistema responsavel) throws MargemControllerException {
        return getMargemController().lstMargemComServicoAtivo(responsavel);
    }
}
