package com.zetra.econsig.service.consignataria;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.CorrespondenteTransferObject;
import com.zetra.econsig.dto.entidade.GrupoConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.OcorrenciaConsignatariaTransferObject;
import com.zetra.econsig.dto.parametros.AlterarMultiplasConsignacoesParametros;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.AnexoConsignataria;
import com.zetra.econsig.persistence.entity.AnexoCredenciamentoCsa;
import com.zetra.econsig.persistence.entity.Consignataria;
import com.zetra.econsig.persistence.entity.ConsultaMargemSemSenha;
import com.zetra.econsig.persistence.entity.Correspondente;
import com.zetra.econsig.persistence.entity.CredenciamentoCsa;
import com.zetra.econsig.persistence.entity.DestinatarioEmailCsa;
import com.zetra.econsig.persistence.entity.DestinatarioEmailCsaSvc;
import com.zetra.econsig.persistence.entity.DestinatarioEmailCsaSvcId;
import com.zetra.econsig.persistence.entity.EnderecoConsignataria;
import com.zetra.econsig.persistence.entity.InformacaoCsaServidor;
import com.zetra.econsig.persistence.entity.LimiteMargemCsaOrg;
import com.zetra.econsig.persistence.entity.ModeloTermoAditivo;
import com.zetra.econsig.persistence.entity.ModeloTermoTag;
import com.zetra.econsig.persistence.entity.OcorrenciaCredenciamentoCsa;
import com.zetra.econsig.persistence.entity.StatusCredenciamento;
import com.zetra.econsig.persistence.entity.TipoEndereco;
import com.zetra.econsig.persistence.entity.VinculoConsignataria;
import com.zetra.econsig.persistence.entity.VinculoRegistroServidor;
import com.zetra.econsig.report.jasper.dto.RegrasConvenioParametrosBean;
import com.zetra.econsig.values.TipoFiltroPesquisaFluxoEnum;

/**
 * <p>Title: ConsignatariaController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ConsignatariaController {
    // Correspondente
    public CorrespondenteTransferObject findCorrespondente(CorrespondenteTransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public CorrespondenteTransferObject findCorrespondente(String corCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public CorrespondenteTransferObject findCorrespondenteByIdn(String corIdentificador, String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public String createCorrespondente(CorrespondenteTransferObject correspondente, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void updateCorrespondente(CorrespondenteTransferObject correspondente, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void removeCorrespondente(CorrespondenteTransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<TransferObject> lstCorrespondentes(TransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<TransferObject> lstCorrespondentes(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public int countCorrespondentes(TransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<Correspondente> findCorrespondenteByCsaCodigo(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    // Consignataria
    public ConsignatariaTransferObject findConsignataria(ConsignatariaTransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public ConsignatariaTransferObject findConsignataria(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public ConsignatariaTransferObject findConsignatariaByIdn(String csaIdentificador, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public ConsignatariaTransferObject findConsignatariaByCorrespondente(String corCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<Consignataria> findConsignatariaComEmailCadastrado(AcessoSistema responsavel) throws ConsignatariaControllerException;

    public String createConsignataria(ConsignatariaTransferObject consignataria, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void updateConsignataria(ConsignatariaTransferObject consignataria, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void removeConsignataria(ConsignatariaTransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<TransferObject> lstConsignatarias(TransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<TransferObject> lstConsignatarias(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<Consignataria> lstConsignatariaByNcaCodigo(String ncaCodigo, AcessoSistema resposavel) throws ConsignatariaControllerException;

    public List<TransferObject> lstConsignatariaByNatureza(String ncaCodigo, AcessoSistema resposavel) throws ConsignatariaControllerException;

    public List<TransferObject> lstConsignatariaByNaturezas(List<String> ncaCodigos, AcessoSistema resposavel) throws ConsignatariaControllerException;

    public List<TransferObject> lstConsignatariasSaldoDevedorServidor(AcessoSistema responsavel) throws ConsignatariaControllerException;

    public int countConsignatarias(TransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public Map<String, String> getCsaIdentificadorMap(AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void bloqueiaCsaExpiradas(AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void bloqueiaCsa(ConsignatariaTransferObject consignataria, String observacao, String tpeCodigo, String tmoCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void bloqueiaCsaSolicitacaoSaldoPagoComAnexoNaoLiquidado(AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void bloqueiaCsaCetExpirado(AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void bloqueiaCsaMensagemNaoLida() throws ConsignatariaControllerException;

    public void bloqueiaCsaNaoConfirmacaoLiquidacao() throws ConsignatariaControllerException;

    public void desbloqueiaCsa(ConsignatariaTransferObject consignataria, String observacao, String tpeCodigo, String tmoCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void inserePenalidade(ConsignatariaTransferObject consignataria, String observacao, String tpeCodigo, String tmoCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<String> bloquearConsignatarias(List<String> csaCodigos, String observacao, String tocCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<String> bloquearConsignatariasContratos(List<TransferObject> adesResponsaveisBloqueio, String observacao, String tocCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void desbloqueiaCsaPrazoDesbloqAutomatico() throws ConsignatariaControllerException;

    public void desbloqueiaCsaPenalidadeExpirada() throws ConsignatariaControllerException;

    public boolean verificarDesbloqueioAutomaticoConsignataria(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public boolean verificarDesbloqueioAutomaticoConsignatariaPorAdeCodigo(String adeCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void incluirOcorrenciaConsignatarias(Collection<String> csaCodigos, String tocCodigo, String observacao, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public String recuperarMensagemDesbloqueioConsignataria(String csaCodigo);

    // Ocorrência de consignatária
    public List<TransferObject> lstOcaConsignatarias(OcorrenciaConsignatariaTransferObject octoOcaConsignataria, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<TransferObject> lstOcaConsignatarias(OcorrenciaConsignatariaTransferObject octoOcaConsignataria, int offset, int count, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public int countOcaConsignatarias(OcorrenciaConsignatariaTransferObject octoOcaConsignataria, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<TransferObject> lstOcaConsignataria(String csaCodigo, List<String> tocCodigos, AcessoSistema responsavel) throws ConsignatariaControllerException;

    // Grupo Consignatária
    public GrupoConsignatariaTransferObject findGrupoCsaByIdentificador(String tgcIdentificador) throws ConsignatariaControllerException;

    public List<TransferObject> lstGrupoConsignataria(String strTgcCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public String insGrupoConsignataria(String strGrupoCsaIdentificador, String strGrupoCsaDescricao, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void edtGrupoConsignataria(GrupoConsignatariaTransferObject grupoCsa, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void renGrupoConsignataria(String strGrupoCsaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    // Empresa Correspondente
    public String createEmpresaCorrespondente(CustomTransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void updateEmpresaCorrespondente(CustomTransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void removeEmpresaCorrespondente(String ecoCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public int countEmpresaCorrespondente(CustomTransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<TransferObject> lstEmpresaCorrespondente(CustomTransferObject criterio, int offset, int count, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public TransferObject findEmpresaCorrespondente(CustomTransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<TransferObject> lstAssociacaoEmpresaCorrespondente(CustomTransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void associaEmpresaCorrespondente(CustomTransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<TransferObject> lstConsignatariaSerTemAde(String serCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<TransferObject> lstConsignatariaSerTemAde(String serCodigo, String rseCodigo, boolean sadAtivos, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<ConsignatariaTransferObject> lstConsignatariasAExpirar(Date dataExpiracao, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<TransferObject> lstNatureza() throws ConsignatariaControllerException;

    public List<TransferObject> lstConsignatariasComAdeRenegociaveis(String rseCodigo, String svcCodigo, String orgCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<Consignataria> lstConsignatariaProjetoInadimplencia() throws ConsignatariaControllerException;

    public void enviaEmailAlertaProximidadeCorte(AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<TransferObject> lstConsignatariaConvenio(TransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<TransferObject> lstCorrespondenteConvenio(TransferObject criterio, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void enviarEmailAlertaRetornoServidor(AcessoSistema responsavel) throws ConsignatariaControllerException;

    public Consignataria findConsignatariaByNumeroContratoBeneficio(String numeroContratoBenificio, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<Consignataria> lstConsignatariaByNcaCodigoAndStatusConvenioAndNaturezaServico(String ncaCodigo, String scvCodigo, String nseCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<TransferObject> lstConsignatariaPorNaturezaServico(String nseCodigo, TipoFiltroPesquisaFluxoEnum nFiltrarPor, String nFiltro) throws ConsignatariaControllerException;

    public List<TransferObject> lstConsignatariaPorNaturezaServico(String orgCodigo, String nseCodigo, TipoFiltroPesquisaFluxoEnum nFiltrarPor, String nFiltro) throws ConsignatariaControllerException;

    public List<TransferObject> lstContratosCsaOcorrenciaPeriodo(String csaCodigo, String corCodigo, List<String> tocCodigos, Date dataIni, Date dataFim, String mesAno, int offset, int count, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<TransferObject> lstContratosCsaOcorrenciaPeriodo(String csaCodigo, String corCodigo, List<String> tocCodigos, List<String> sadCodigos, Date dataIni, Date dataFim, String mesAno, int offset, int count, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void bloqueiaConsignatariasComAdeSemNumAnexosMin(Date dataIniVerificacao, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<TransferObject> lstAdesUsuCsaCorSemNumAnexoMin(String csaCodigo, Date dataIniVerificacao, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<TransferObject> lstAdesUsuCsaCorSemNumAnexoMin(String csaCodigo, Date dataIniVerificacao, int offset, int count, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public int countAdesUsuCsaCorSemNumAnexoMin(String csaCodigo, Date dataIniVerificacao, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public int countContratosCsaOcorrenciaPeriodo(String csaCodigo, String corCodigo, List<String> tocCodigos, Date dataIni, Date dataFim, String mesAno, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public BigDecimal sumContratosCsaOcorrenciaPeriodo(String csaCodigo, String corCodigo, List<String> tocCodigos, Date dataIni, Date dataFim, String mesAno, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void criarOcorrenciaAtualizarDados(String csaCodigo, String tocObs, AcessoSistema responsavel) throws ConsignatariaControllerException;

    // endereços consignataria
    public List<TransferObject> lstEnderecoConsignatariaByCsaCodigo(String csaCodigo, int count, int offset, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public EnderecoConsignataria findEnderecoConsignatariaByPKCsaCodigo(String encCodigo, String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public int countEnderecoConsignatariaByCsaCodigo(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<TipoEndereco> listAllTipoEndereco(AcessoSistema responsavel) throws ConsignatariaControllerException;

    public EnderecoConsignataria createEnderecoConsignataria(String csaCodigo, String tieCodigo, String encLogradouro, String encNumero, String encComplemento, String encBairro, String encMunicipio, String encUf, String encCep, BigDecimal encLatitude, BigDecimal encLongitude, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public EnderecoConsignataria updateEnderecoConsignataria(String encCodigo, String csaCodigo, String tieCodigo, String encLogradouro, String encNumero, String encComplemento, String encBairro, String encMunicipio, String encUf, String encCep, BigDecimal encLatitude, BigDecimal encLongitude, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public EnderecoConsignataria removeEnderecoConsignataria(String encCodigo, String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void enviarEmailNotificacaoConsignacaoAjustadoMargem(List<TransferObject> autDes, AlterarMultiplasConsignacoesParametros parametros, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<TransferObject> lstConsignatariaByTpsCodigo(String tpsCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<TransferObject> lstConsignatariaCoeficienteAtivoExpirado(Integer diasParaExpiracao, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<TransferObject> lstConsignatariaCoeficienteAtivoDesbloqueado(AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<TransferObject> lstConsignatariaCoeficienteBloqueado(AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void updateCorrespondente(CorrespondenteTransferObject correspondente, boolean bloquearDesbloquear, String tmoCodigo, String ocrObs, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void criarCredenciamentoConsignataria(String csaCodigo, String scrCodigo, Date creDataIni, Date creDataFim, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<TransferObject> lstCredenciamentoCsaDashboard(AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<StatusCredenciamento> lstStatusCredenciamentoByScrCodigos(List<String> scrCodigos, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<TransferObject> lstCredenciamentoCsaDashboardFiltro(Date creDataIni, Date creDataFim, List<String> scrCodigos, List<String> csaCodigos, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<TransferObject> lstLimiteMargemCsaOrgByCsaCodigo(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void salvarLimiteMargemCsaOrg(List<LimiteMargemCsaOrg> lstLimiteMargemCsaOrg, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<OcorrenciaCredenciamentoCsa> lstOcorrenciaCredenciamentoCsa(String creCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<AnexoCredenciamentoCsa> lstAnexoCredenciamentoCsa(String creCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public String registrarAnexoCredenciamentoCsa(String creCodigo, String nomesAnexo, String tarCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void alterarStatusNotificarCseCredenciamento(String creCodigo, List<String> anexos, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void aprovarCredenciamentoCsa(String creCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void reprovarCredenciamentoCsa(String creCodigo, String tmoCodigo, String tmoObs, boolean reprovarTermoAss, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void preencherTermoCredenciamentoCsa(String creCodigo, String anexoTermoPreenchido, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<AnexoCredenciamentoCsa> lstAnexoCredenciamentoCsaTipoArquivo(String creCodigo, String tarCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void assinarTermoAditivoCredenciamentoCsa(String creCodigo, String anexoAssTermo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public CredenciamentoCsa findByCsaCodigoCredenciamentoCsa(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void assinarTermoAditivoCseCredenciamentoCsa(String creCodigo, String anexoAssTermoCse, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void finalizarCredenciamentoCsa(String creCodigo, String anexoAssTermo, boolean desbloquearCsa, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public int countServicosCsaCetExpirado(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<TransferObject> lstServicosCsaCetExpirado(String csaCodigo, int offset, int count, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public CredenciamentoCsa findByCreCodigoCredenciamentoCsa(String creCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public String getEmailCsaNotificacaoOperacao(String funCodigo, String papCodigoOperador, String csaCodigo, String svcCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<TransferObject> lstFuncoesEnvioEmailCsa(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void salvarFuncoesEnvioEmailCsa(List<DestinatarioEmailCsa> listaInc, List<DestinatarioEmailCsa> listaAlt, List<DestinatarioEmailCsa> listaExc, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public TransferObject getDestinatarioEmailCsaSvcById(DestinatarioEmailCsaSvcId id, AcessoSistema responsavel) throws ConsignatariaControllerException;
    
    public List<TransferObject> lstServicosDestinatarioEmailCsaSvc(String funCodigo, String papCodigo, String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;
    
    public Map<String, List<String>> mapaServicosDestinatarioEmailCsaSvc(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;
    
    public void salvarServicosDestinatarioEmailCsaSvc(Map<String, Set<String>> mapaServicosCsa, AcessoSistema responsavel) throws ConsignatariaControllerException;
    
    public List<TransferObject> lstConsignatariaCoeficienteAtivo(AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void createRegistroAnexoCsa(AnexoConsignataria anexoConsignataria, AcessoSistema responsavel) throws ConsignatariaControllerException;

    boolean excluiAnexoConsignataria(AcessoSistema responsavel, String csaCodigo, String nomeArquivo) throws ConsignatariaControllerException, HQueryException, UpdateException;

    public List<TransferObject> lstInformacaoCsaServidor(CustomTransferObject criterio, int offset, int size, AcessoSistema responsavel) throws HQueryException;

    public InformacaoCsaServidor findInformacaoCsaServidorByIcsCodigo(String icsCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void removeInformacaoCsaServidor(String icsCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public InformacaoCsaServidor createInformacaoCsaServidor(String csaCodigo, String serCodigo, String icsValor, AcessoSistema responsavel) throws ConsignatariaControllerException, MissingPrimaryKeyException;

    public InformacaoCsaServidor updateInformacaoCsaServidor(String icsCodigo, String csaCodigo, String serCodigo, String icsValor, AcessoSistema responsavel) throws UpdateException, ConsignatariaControllerException;

    public void notificaCsaNovosVinculos(AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<TransferObject> listaCsaPermiteContato(List<String> csaCodigos, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public VinculoConsignataria findVinculoCsa(String vcsCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    List<VinculoConsignataria> findVinculosCsa(String csa, AcessoSistema responsavel) throws ConsignatariaControllerException;

    List<VinculoRegistroServidor> findVinculosRseParaCsa(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    List<VinculoRegistroServidor> findVinculoRseCsa(String vcsCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public int salvarEditarVinculoRseCsa(VinculoConsignataria vinculoConsignataria, String vrsCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void excluirVinculoCsa(String vcsCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public VinculoConsignataria findVinculoCsaPorVrsCsa(String csaCodigo, String vrsCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public String impCadastroConsignatarias(String nomeArquivoEntrada, boolean validar, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<ConsultaMargemSemSenha> listaConsignatariaConsultaMargemSemSenhaByRseCodigo(String rseCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<ConsultaMargemSemSenha> listaConsignatariaConsultaMargemSemSenhaByCsaCodigo(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<ConsultaMargemSemSenha> listaConsignatariaConsultaMargemSemSenhaByRseCodigoByCsaCodigo(String rseCodigo, String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public ConsultaMargemSemSenha createConsignatariaConsultaMargemSemSenha(String rseCodigo, String csaCodigo, Date cssDataIni, Date cssDataFim, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public ConsultaMargemSemSenha updateConsignatariaConsultaMargemSemSenha(String cssCodigo, String permissao, AcessoSistema responsavel) throws UpdateException, ConsignatariaControllerException;

    public List<ConsultaMargemSemSenha> listaCsaConsultaMargemSemSenhaAlertaPermissaoRetirada(String rseCodigo, String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public Map<String, String> findCsasComOperacoesEmAndamentoByRseCodigo(AcessoSistema responsavel) throws ConsignatariaControllerException;

    public String incluirOcorrenciaConsignataria(String csaCodigo, String tocCodigo, String observacao, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<TransferObject> listaOccBloqDesbloqVinculosByCsa(String csaCodigo, int offset, int count, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public int countOccBloqDesbloqVinculosByCsa(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public void enviarEmailNotificacaoVinculosBloqDesbloq(String csaCodigo, List<String> occCodigos, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<TransferObject> listaCsaPortabilidadeCartao(String csaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<TransferObject> listaCodTituloModeloTermoAditivo(String mtaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public List<ModeloTermoTag> listaTagsModeloTermoAditivo(String mtaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;

    public ModeloTermoAditivo findModeloTermoAditivo(String mtaCodigo, AcessoSistema responsavel) throws ConsignatariaControllerException;


	public void enviarNotificacaoAlteracaoRegrasConvenio(String csaCodigo, List<RegrasConvenioParametrosBean> dadosAlterados, AcessoSistema responsavel) throws ConsignatariaControllerException;
}
