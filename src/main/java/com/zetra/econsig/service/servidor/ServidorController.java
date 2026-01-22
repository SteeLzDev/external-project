package com.zetra.econsig.service.servidor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.DestinatarioEmailSer;
import com.zetra.econsig.persistence.entity.EnderecoServidor;
import com.zetra.econsig.persistence.entity.HistoricoMargemFolha;
import com.zetra.econsig.persistence.entity.OcorrenciaRegistroSer;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.Servidor;
import com.zetra.econsig.persistence.entity.TipoEndereco;
import com.zetra.econsig.values.AcaoTipoDadoAdicionalEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;

/**
 * <p>Title: ServidorController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ServidorController {

    public String cadastrarServidor(ServidorTransferObject servidor, RegistroServidorTO registroServidor, AcessoSistema responsavel) throws ServidorControllerException;

    public ServidorTransferObject findServidor(ServidorTransferObject servidor, AcessoSistema responsavel) throws ServidorControllerException;

    public ServidorTransferObject findServidor(String serCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public ServidorTransferObject findServidor(String serCpf, String rseMatricula, String serNroIdt, java.sql.Date serDataNasc, AcessoSistema responsavel) throws ServidorControllerException;

    public List<Servidor> findByCpf(String serCpf, AcessoSistema responsavel) throws ServidorControllerException;

    public void updateServidor(ServidorTransferObject servidor, AcessoSistema responsavel) throws ServidorControllerException;

    public void updateServidor(ServidorTransferObject servidor, String tocCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public void removeEnderecoServidor(EnderecoServidor enderecoServidor, AcessoSistema responsavel) throws ServidorControllerException;

    public int countRegistroServidor(List<String> srsCodigos, List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws ServidorControllerException;

    public int countRegistroServidorTransferidos(AcessoSistema responsavel) throws ServidorControllerException;

    public int countRegistroServidorExcluidos(AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> lstRegistroServidor(List<String> srsCodigos, List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> lstRegistroServidor(String serCodigo, String orgCodigo, String estCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> lstRegistroServidor(String serCodigo, List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> lstRegistroServidor(String serCodigo, List<String> orgCodigos, List<String> estCodigos, boolean recuperaRseExcluidos, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> lstRegistroServidorAuditoriaTotal(AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> countQtdeServidorPorOrg(AcessoSistema responsavel) throws ServidorControllerException;

    public String criaOcorrenciaRSE(String rseCodigo, String tocCodigo, String orsObs, String tmoCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public void criaOcorrenciaRSE(List<TransferObject> excluidosTO, String tocCodigo, String orsObs, String tmoCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public String criaOcorrenciaSER(String serCodigo, String tocCodigo, String orsObs, String tmoCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> lstOrsRegistroServidor(TransferObject toOrsRegistroServidor, int offset, int count, AcessoSistema responsavel) throws ServidorControllerException;

    public int countOrsRegistroServidor(TransferObject toOrsRegistroServidor, AcessoSistema responsavel) throws ServidorControllerException;

    public RegistroServidorTO findRegistroServidor(String rseCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public RegistroServidorTO findRegistroServidor(String rseCodigo, boolean retornaMargem, AcessoSistema responsavel) throws ServidorControllerException;

    public RegistroServidorTO findRegistroServidor(RegistroServidorTO registroServidor, AcessoSistema responsavel) throws ServidorControllerException;

    public RegistroServidorTO findRegistroServidor(RegistroServidorTO registroServidor, boolean retornaMargem, AcessoSistema responsavel) throws ServidorControllerException;

    public List<RegistroServidorTO> findRegistroServidorBySerCodigo(String serCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public String createRegistroServidor(RegistroServidorTO registroServidor, AcessoSistema responsavel) throws ServidorControllerException;

    public void updateRegistroServidorSemHistoricoMargem(RegistroServidorTO registroServidor, AcessoSistema responsavel) throws ServidorControllerException;

    public void updateRegistroServidorSemHistoricoMargem(RegistroServidorTO registroServidor, boolean importacaoTransferidos, AcessoSistema responsavel) throws ServidorControllerException;

    public void updateRegistroServidor(RegistroServidorTO registroServidor, boolean validaMargem, boolean calculaMargem, boolean transferenciaMargem, AcessoSistema responsavel) throws ServidorControllerException;

    public void updateRegistroServidor(RegistroServidorTO registroServidor, List<MargemTO> margens, boolean validaMargem, boolean calculaMargem, boolean transferenciaMargem, AcessoSistema responsavel) throws ServidorControllerException;

    public void updateRegistroServidor(RegistroServidorTO registroServidor, List<MargemTO> margens, boolean validaMargem, boolean calculaMargem, boolean transferenciaMargem, boolean geraHistoricoMargem, AcessoSistema responsavel) throws ServidorControllerException;

    public void updateServidorAndUpdateRegistroServidor(RegistroServidorTO registroServidor, ServidorTransferObject servidor, List<MargemTO> margens, Boolean aceiteTermoUso, AcessoSistema responsavel) throws ServidorControllerException;

    public TransferObject getRegistroServidorPelaMatricula(String serCodigo, String orgCodigo, String estCodigo, String rseMatricula, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> lstStatusRegistroServidor(boolean ignoraStatusExcluidos, boolean ignoraStatusBloqSeguranca, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> getEstCivil(AcessoSistema responsavel);

    public String getEstCivil(String estCvlCodigo, AcessoSistema responsavel);

    public List<TransferObject> getNivelEscolaridade(AcessoSistema responsavel);

    public String getNivelEscolaridade(String nesCodigo, AcessoSistema responsavel);

    public List<TransferObject> getTipoHabitacao(AcessoSistema responsavel);

    public String getTipoHabitacao(String thaCodigo, AcessoSistema responsavel);

    public String buscaImgServidor(String serCpf, String rseCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> selectVincRegistroServidor(boolean ativos, AcessoSistema responsavel) throws ServidorControllerException;

    public List<String> selectCnvVincRseSer(String csaCodigo, String svcCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public void updateCnvVincRseSer(String csaCodigo, String svcCodigo, List<String> vrsCodigos, Map<String, List<String>> vinculosBloqDesbloq, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> lstCargo(AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> lstPadrao(AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> lstSubOrgao(AcessoSistema responsavel, String orgCodigo) throws ServidorControllerException;

    public List<TransferObject> lstUnidade(AcessoSistema responsavel, String sboCodigo) throws ServidorControllerException;

    public List<TransferObject> pesquisarHistoricoMargem(String rseCodigo, int offset, int count, TransferObject criterio, AcessoSistema responsavel) throws ServidorControllerException;
    public int countHistoricoMargem(String rseCodigo, TransferObject criterio, AcessoSistema responsavel) throws ServidorControllerException;

    public int countConvenioBloqueados(String rseCodigo, String orgCodigo, String csaCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> selectConvenioBloqueados(String rseCodigo, String orgCodigo, String csaCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public void setRseQtdAdeDefault(List<String> cnvCodigo) throws ServidorControllerException;

    public List<TransferObject> lstPosto(AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> lstTipoRegistroServidor(AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> lstCapacidadeCivil(AcessoSistema responsavel) throws ServidorControllerException;

    public boolean isDataNascServidorValida (String dataAValidar, String dataServidor, String svcCodigo, String dateFormat, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> lstHistoricoVariacaoMargem(String rseCodigo, Short marCodigo, int qtdeMesesPesquisa, AcessoSistema responsavel) throws ServidorControllerException;

    public boolean transferirMargem(RegistroServidorTO registroServidor, String transfTotal, BigDecimal valor, Short marCodigoOrigem, Short marCodigoDestino, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> lstRegistroServidorUsuarioSer(String usuLogin, boolean recuperaRseExcluidos, AcessoSistema responsavel) throws ServidorControllerException;

    public TransferObject findServidorProprietarioAde(String adeCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public int countRegistroServidorSerCodigo(String serCodigo, boolean recuperaRseExcluidos, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> lstRegistroServidorSerCodigo(String serCodigo, boolean recuperaRseExcluidos, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> lstTipoBaseCalculo(AcessoSistema responsavel) throws ServidorControllerException;

    public TransferObject sorteiaPerguntaDadosCadastrais(Short pdcGrupo, AcessoSistema responsavel) throws ServidorControllerException;

    public boolean validaPerguntaDadosCadastrais(String rseCodigo, Short pdcGrupo, Short pdcNumero, String resposta, AcessoSistema responsavel) throws ServidorControllerException;

    public boolean existeEmailCadastrado(String serEmail, String serCpfExceto, AcessoSistema responsavel) throws ServidorControllerException;

    public void cadastrarEmailServidor(String rseCodigo, String email, String protocoloCodigo, AcessoSistema responsavel, AcessoSistema usuarioSuporteResponsavel) throws ServidorControllerException;

    public List<TransferObject> lstRegistroServidorPorCpf(String serCpf, List<String> orgCodigoList, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> lstRegistroServidorPorEmail(String serEmail, List<String> orgCodigoList, AcessoSistema responsavel) throws ServidorControllerException;

    public ServidorTransferObject findServidorByUsuCodigo(String usuCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public ServidorTransferObject findServidorByRseCodigo(String rseCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> findCargoByIdentificador(String crsIdentificador, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> findPadraoByIdentificador(String prsIdentificador, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> findSubOrgaoByIdentificador(String sboIdentificador, String orgCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> findUnidadeByIdentificador(String uniIdentificador, String sboCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> findVincRegistroServidor(String vrsIdentificador, boolean ativos, AcessoSistema responsavel) throws ServidorControllerException;

    public void aprovarCadastroServidor(RegistroServidorTO registroServidor, ServidorTransferObject servidor, boolean aprovar, AcessoSistema responsavel) throws ServidorControllerException;

    public int countOcorrenciaSerUnionRse(TransferObject criterio, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> lstOcorrenciaSerUnionRse(TransferObject toOrsRegistroServidor, int offset, int count, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> lstDataOcorrenciaServidor(String serCodigo, String tocCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public TransferObject recuperarDadosBanco(Short bcoCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public TransferObject findCargoByCrsCodigo(String crsCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public java.util.Date obtemDataInclusaoRegistroServidor(String rseCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public void cancelarCadastroServidor(AcessoSistema responsavel) throws ServidorControllerException;

    public void createRelRegistroServidor(String rseCodigoOrigem, String rseCodigoDestino, String tntCodigo, String usuCodigo, java.util.Date rreCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public List<EnderecoServidor> listEnderecoServidorByCodigo(String serCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TipoEndereco> listAllTipoEndereco(AcessoSistema responsavel) throws ServidorControllerException;

    public EnderecoServidor findEnderecoServidorByCodigo(String ensCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public EnderecoServidor createEnderecoServidor(EnderecoServidor enderecoServidor, AcessoSistema responsavel) throws ServidorControllerException;

    public void updateEnderecoServidor(EnderecoServidor enderecoServidor, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> findRegistroServidoresByMatriculas(List<String> rseMatriculas, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> listarMargensRse(String rseCodigo, String svcCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> listarServidorPossuiAde(String serCpf, AcessoSistema responsavel) throws ServidorControllerException;

    public void cadastrarDispensaDigitalServidor(TransferObject criterio, TransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws ServidorControllerException;

    public boolean validaServidorDentroPrazoAcessoSistemaSemValidacaoEmail(String serCodigo, Date dataOcorrencia, AcessoSistema responsavel) throws ServidorControllerException;

    public void updateMargensRegistroServidor(String rseCodigo, List<MargemTO> margens, String compulsorio, String tmoCodigo, String obsMotivoOperacao, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> lstDadosServidor(AcaoTipoDadoAdicionalEnum acao, VisibilidadeTipoDadoAdicionalEnum visibilidade, String serCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> lstTipoDadoAdicionalServidorQuery(AcaoTipoDadoAdicionalEnum acao, VisibilidadeTipoDadoAdicionalEnum visibilidade, AcessoSistema responsavel) throws ServidorControllerException;

    public String getValorDadoServidor(String serCodigo, String tdaCodigo) throws ServidorControllerException;

    public void setValorDadoServidor(String serCodigo, String tdaCodigo, String dadValor, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> lstDataOcorrenciaServidorDescontoParcial(String serCodigo, String tocCodigo, boolean ordenar, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> lstServidoresArquivamento(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws ServidorControllerException;

    public void arquivarServidor(String serCodigo, String rseCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> lstHistoricoServidor(String rseMatricula, String serCpf, String orgCodigo, String estCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public void bloquearRegistroServidorPorMotivoSeguranca(String rseCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> lstHistoricoVariacaoMargemBruta(String rseCodigo, Short marCodigo, int qtdeMesesPesquisa, AcessoSistema responsavel) throws ServidorControllerException;

    public void gravarDadoServidor(String serCodigo, String tdaCodigo, String dasValor, AcessoSistema responsavel) throws ServidorControllerException;

    public void notificaServidorContratosPendentesReativacao(AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> listarComposicaoMargem(TransferObject composicaoMargem, AcessoSistema responsavel) throws ServidorControllerException;

    public TransferObject findComposicaoMargem(String cmaCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public void editarComposicaoMargem(TransferObject composicaoMargem, AcessoSistema responsavel) throws ServidorControllerException;

    public void excluirComposicaoMargem(String cmaCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> findVencimento(String vctIdentificador, AcessoSistema responsavel) throws ServidorControllerException;

    public void confirmarMargemFolha(List<String> rseCodigos, AcessoSistema responsavel) throws ServidorControllerException;

    public void rejeitarMargemFolha(List<String> rseCodigos, AcessoSistema responsavel) throws ServidorControllerException;

    public OcorrenciaRegistroSer obtemUltimaOcorrenciaRegistroServidor(String rseCodigo, List<String> tocCodigos, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> lstUnidadeSubOrgao(AcessoSistema responsavel) throws ServidorControllerException;

    public String updateServidor(ServidorTransferObject servidor, boolean enviaEmail, AcessoSistema responsavel) throws ServidorControllerException;

    public TransferObject findRegistroServidoresByLikeMatricula(String rseMatricula, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> listaServidorPorCsaVerba(String csaCodigo, String cnvCodVerba, AcessoSistema responsavel) throws ServidorControllerException;

    public Servidor findByCpfMatricula(String matricula, String cpf, AcessoSistema responsavel) throws ServidorControllerException;

    public RegistroServidor findRseTipo(String matricula, String serCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public void updateCnvVincCsaSvc(String csaCodigo, String svcCodigo, List<String> vrsCodigos, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> lstFuncoesEnvioEmailSer(String serCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public void salvarFuncoesEnvioEmailSer(List<DestinatarioEmailSer> listaInc, List<DestinatarioEmailSer> listaExc, AcessoSistema responsavel) throws ServidorControllerException;

    public String getEmailSerNotificacaoOperacao(String funCodigo, String papCodigoOperador, String serCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> findByRseTocCodigos(String rseCodigo, List<String> tocCodigos, AcessoSistema responsavel) throws ServidorControllerException;

    public void enviarNotificacaoSer(String serCodigo, String csaCodigo, String corpoSms, String tituloPush, String textoPush, TipoNotificacaoEnum tipoNotificacao, String dataVencimento, AcessoSistema responsavel);

    public void enviarNotificacaoVencimentoAutorizacao(Integer qtdeDiasVencimentoAutorizacao, AcessoSistema responsavel);

    public List<HistoricoMargemFolha> lstHistoricoMargemFolhaRseFiltro(String rseCodigo, Date periodoIni, Date periodoFim, Short marCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> consultaSalarioServidor(String cpf, AcessoSistema responsavel) throws ServidorControllerException;

    public void createRegistroServidorOcultoCsa(String rseCodigo, List<String> csaCodigos, AcessoSistema responsavel) throws ServidorControllerException;

    public List<String> listCsaOcultasRse(String rseCodigo, AcessoSistema responsavel) throws ServidorControllerException;
}
