package com.zetra.econsig.service.parametro;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.OcorrenciaParamSistCseTO;
import com.zetra.econsig.dto.entidade.ParamCnvRseTO;
import com.zetra.econsig.dto.entidade.ParamCsaRseTO;
import com.zetra.econsig.dto.entidade.ParamNseRseTO;
import com.zetra.econsig.dto.entidade.ParamSistCseTO;
import com.zetra.econsig.dto.entidade.ParamSvcCseTO;
import com.zetra.econsig.dto.entidade.ParamSvcRseTO;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.ParamTarifCseTO;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.BatchManager;
import com.zetra.econsig.persistence.entity.ParamOrgao;
import com.zetra.econsig.persistence.entity.ParametroAgendamento;
import com.zetra.econsig.persistence.entity.TipoParamSistConsignante;
import com.zetra.econsig.persistence.entity.TipoParamSvc;
import com.zetra.econsig.values.InformacaoSerCompraEnum;

/**
 * <p>Title: ParametroController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ParametroController {
    // ParamTarifCse
    public List<TransferObject> selectParamTarifCse(String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public void updateParamTarifCse(ParamTarifCseTO paramTarifCse, AcessoSistema responsavel) throws ParametroControllerException;

    // ParamSvcCse
    public void copiaParamSvc(String svcOrigem, String svcDestino, BatchManager batman, AcessoSistema responsavel) throws ParametroControllerException;

    public ParamSvcCseTO findParamSvcCse(ParamSvcCseTO paramSvcCse, AcessoSistema responsavel) throws ParametroControllerException;

    public List<TransferObject> selectParamSvcCse(String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public List<TransferObject> selectParamSvcCse(String svcCodigo, String cseAltera, AcessoSistema responsavel) throws ParametroControllerException;

    public ParamSvcTO getParamSvcCseTO(String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public List<TransferObject> recuperaIncidenciasMargem(AcessoSistema responsavel) throws ParametroControllerException;

    public CustomTransferObject getParamSvcCse(String svcCodigo, String tpsCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public void updateParamSvcCse(ParamSvcCseTO paramSvcCse, AcessoSistema responsavel) throws ParametroControllerException;

    public List<TransferObject> lstParamSvcCse(String tpsCodigo, String pseVlr, AcessoSistema responsavel) throws ParametroControllerException;

    // ParamSistCse
    public String findParamSistCse(String tpcCodigo, String cseCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public ParamSistCseTO findParamSistCse(ParamSistCseTO param, AcessoSistema responsavel) throws ParametroControllerException;

    public List<TransferObject> selectParamSistCse(String tpcCseAltera, String tpcCseConsulta, String tpcSupAltera, String tpcSupConsulta, AcessoSistema responsavel) throws ParametroControllerException;

    public List<TransferObject> selectParamSistCseEditavelPerfil(String papCodigo, String perCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public LinkedList<OcorrenciaParamSistCseTO> selectOcorrenciaParamSistCse(OcorrenciaParamSistCseTO criterio, AcessoSistema responsavel) throws ParametroControllerException;

    public int countOcorrenciaParamSistCse(OcorrenciaParamSistCseTO criterio, AcessoSistema responsavel) throws ParametroControllerException;

    public void updateParamSistCse(String psiVlr, String tpcCodigo, String cseCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public void updateParamSistCse(ParamSistCseTO paramSistCse, AcessoSistema responsavel) throws ParametroControllerException;

    public boolean senhaServidorObrigatoriaReserva(String rseCodigo, String svcCodigo, String csaCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public boolean senhaServidorObrigatoriaConfSolicitacao(String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public boolean senhaServidorObrigatoriaCancelarReneg(String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public boolean senhaServidorObrigatoriaConsultaMargem(String rseCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public InformacaoSerCompraEnum senhaServidorObrigatoriaCompra(AcessoSistema responsavel) throws ParametroControllerException;

    public boolean requerMatriculaCpf(AcessoSistema responsavel) throws ParametroControllerException;

    public boolean requerMatriculaCpf(boolean lote, AcessoSistema responsavel) throws ParametroControllerException;

    public boolean requerDataNascimento(AcessoSistema responsavel) throws ParametroControllerException;

    public TipoParamSistConsignante findTipoParamSistConsignante(String tpcCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    // ParamSvc
    public TipoParamSvc findTipoParamServico(String tpsCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public List<TransferObject> lstTipoParamSvc(AcessoSistema responsavel) throws ParametroControllerException;

    public List<TransferObject> lstTipoParamSvcSobrepoe(AcessoSistema responsavel) throws ParametroControllerException;

    public List<TransferObject> selectParamSvcSobrepoe(String svcCodigo, String rseCodigo, List<String> tpsCodigos, AcessoSistema responsavel) throws ParametroControllerException;

    public void updateParamSvcSobrepoe(List<TransferObject> parametros, AcessoSistema responsavel) throws ParametroControllerException;

    // ParamSvcCor
    public List<TransferObject> selectParamSvcCor(String svcCodigo, String corCodigo, List<String> tpsCodigos, boolean ativo, AcessoSistema responsavel) throws ParametroControllerException;

    public List<TransferObject> selectParamSvcCor(List<String> svcCodigos, List<String> corCodigos, List<String> tpsCodigos, boolean ativo, AcessoSistema responsavel) throws ParametroControllerException;

    public void updateParamSvcCor(List<TransferObject> parametros, AcessoSistema responsavel) throws ParametroControllerException;

    public void deleteParamIgualCsa(List<TransferObject> tpsCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    // ParamSvcCsa
    public void copiaParamSvcCsa(String svcOrigem, String svcDestino, BatchManager batman, AcessoSistema responsavel) throws ParametroControllerException;

    public List<TransferObject> selectParamSvcCsa(List<String> tpsCodigos, AcessoSistema responsavel) throws ParametroControllerException;

    public List<TransferObject> selectParamSvcCsa(String svcCodigo, String csaCodigo, List<String> tpsCodigos, boolean ativo, AcessoSistema responsavel) throws ParametroControllerException;

    public List<TransferObject> selectParamSvcCsa(List<String> svcCodigos, List<String> csaCodigos, List<String> tpsCodigos, boolean ativo, AcessoSistema responsavel) throws ParametroControllerException;

    public List<TransferObject> selectParamSvcCsa(String csaIdentificadorInterno, List<String> tpsCodigos, boolean ativo, AcessoSistema responsavel) throws ParametroControllerException;

    public void updateParamSvcCsa(List<TransferObject> parametros, AcessoSistema responsavel) throws ParametroControllerException;

    public void deleteParamIgualCse(List<TransferObject> tpsCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public void deleteParamIgualCseRse(List<TransferObject> tpsCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public void ativaParamSvcCsa(String svcCodigo, String csaCodigo, List<String> tpsCodigos, AcessoSistema responsavel) throws ParametroControllerException;

    public boolean permiteContratoValorNegativo(String csaCodigo, String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    // ParamCnvRse
    public List<TransferObject> lstServicoServidor(String rseCodigo, String csaCodigo, boolean ativos, AcessoSistema responsavel) throws ParametroControllerException;

    public List<TransferObject> lstServicoServidor(String rseCodigo, String csaCodigo, String nseCodigo, boolean ativos, AcessoSistema responsavel) throws ParametroControllerException;

    public List<TransferObject> lstServicoServidor(String rseCodigo, String csaCodigo, String nseCodigo, boolean ativos, int offset, int count, AcessoSistema responsavel) throws ParametroControllerException;

    /**
     * BLOQUEIO DE CONVENIO POR REGISTRO SERVIDOR
     **/
    public void copiaParamCnvRse(String cnvOrigem, String cnvDestino, AcessoSistema responsavel) throws ParametroControllerException;

    public List<TransferObject> lstBloqueioCnvRegistroServidor(String rseCodigo, String csaCodigo, String svcCodigo, Boolean inativosSomenteComBloqueio, AcessoSistema responsavel) throws ParametroControllerException;

    public CustomTransferObject getBloqueioCnvRegistroServidor(String rseCodigo, String csaCodigo, String svcCodigo, Boolean inativosSomenteComBloqueio, AcessoSistema responsavel) throws ParametroControllerException;

    public void setBloqueioCnvRegistroServidor(List<ParamCnvRseTO> bloqueios, CustomTransferObject tmoObject, AcessoSistema responsavel) throws ParametroControllerException;

    public void setBloqueioCnvRegistroServidor(ParamCnvRseTO paramCnvRse, AcessoSistema responsavel) throws ParametroControllerException;

    public void corrigeBloqueioServidor(AcessoSistema responsavel) throws ParametroControllerException;

    public void copiaBloqueioCnv(String rseCodNovo, String rseCodAnt, AcessoSistema responsavel) throws ParametroControllerException;

    public boolean temServidorBloqueadoCnv(AcessoSistema responsavel) throws ParametroControllerException;

    /** FIM BLOQUEIO DE CONVENIO POR REGISTRO SERVIDOR **/

    /**
     * BLOQUEIO DE SERVIÇO POR REGISTRO SERVIDOR
     **/
    public void copiaParamSvcRse(String svcOrigem, String svcDestino, BatchManager batman, AcessoSistema responsavel) throws ParametroControllerException;

    public List<TransferObject> lstBloqueioSvcRegistroServidor(String rseCodigo, String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public Map<String, Long> getBloqueioSvcRegistroServidor(String rseCodigo, String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public void setBloqueioSvcRegistroServidor(ParamSvcRseTO bloqueio, CustomTransferObject tmoObject, AcessoSistema responsavel) throws ParametroControllerException;

    public void setBloqueioSvcRegistroServidor(List<ParamSvcRseTO> bloqueios, CustomTransferObject tmoObject, AcessoSistema responsavel) throws ParametroControllerException;

    public void copiaBloqueioSvc(String rseCodNovo, String rseCodAnt, AcessoSistema responsavel) throws ParametroControllerException;

    public boolean temServidorBloqueadoSvc(AcessoSistema responsavel) throws ParametroControllerException;

    /** FIM BLOQUEIO DE SERVIÇO POR REGISTRO SERVIDOR **/

    /**
     * BLOQUEIO DE NATUREZA DE SERVIÇO POR REGISTRO SERVIDOR
     **/
    public List<TransferObject> lstBloqueioNseRegistroServidor(String rseCodigo, String nseCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public Map<String, Long> getBloqueioNseRegistroServidor(String rseCodigo, String nseCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public void setBloqueioNseRegistroServidor(List<ParamNseRseTO> bloqueios, CustomTransferObject tmoObject, AcessoSistema responsavel) throws ParametroControllerException;

    public void setBloqueioNseRegistroServidor(ParamNseRseTO paramNseRse, AcessoSistema responsavel) throws ParametroControllerException;

    public void copiaBloqueioNse(String rseCodNovo, String rseCodAnt, AcessoSistema responsavel) throws ParametroControllerException;

    public boolean temServidorBloqueadoNse(AcessoSistema responsavel) throws ParametroControllerException;

    /** FIM BLOQUEIO DE NATUREZA DE SERVIÇO POR REGISTRO SERVIDOR **/

    /**
     * BLOQUEIO DE CONSIGNATÁRIA POR REGISTRO SERVIDOR
     **/
    public List<TransferObject> lstBloqueioCsaRegistroServidor(String rseCodigo, String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public Map<String, Long> getBloqueioCsaRegistroServidor(String rseCodigo, String csaCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public void setBloqueioCsaRegistroServidor(ParamCsaRseTO bloqueio, CustomTransferObject tmoObject, AcessoSistema responsavel) throws ParametroControllerException;

    public void setBloqueioCsaRegistroServidor(List<ParamCsaRseTO> bloqueios, CustomTransferObject tmoObject, AcessoSistema responsavel) throws ParametroControllerException;

    public void copiaBloqueioCsa(String rseCodNovo, String rseCodAnt, AcessoSistema responsavel) throws ParametroControllerException;

    public boolean temServidorBloqueadoCsa(AcessoSistema responsavel) throws ParametroControllerException;

    /**
     * FIM BLOQUEIO DE CONSIGNATÁRIA POR REGISTRO SERVIDOR
     **/

    // ParamCsa
    public List<TransferObject> selectParamCsaNaoNulo(AcessoSistema responsavel) throws ParametroControllerException;

    public List<TransferObject> selectParamCsa(String csaCodigo, String tpaCseAltera, String tpaCsaAltera, String tpaSupAltera, AcessoSistema responsavel) throws ParametroControllerException;

    @Deprecated
    public List<TransferObject> selectParamCsa(String csaCodigo, String tpaCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    @Deprecated
    public List<TransferObject> selectParamCsa(String csaCodigo, String tpaCodigo, String tpaCseAltera, String tpaCsaAltera, String tpaSupAltera, AcessoSistema responsavel) throws ParametroControllerException;

    public String getParamCsa(String csaCodigo, String tpaCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public void updateParamCsa(CustomTransferObject cto, AcessoSistema responsavel) throws ParametroControllerException;

    public void inserirRelacionamento(String tntCodigo, String svcCodigo, List<String> svcDestino, AcessoSistema responsavel) throws ParametroControllerException;

    public List<TransferObject> lstRelacionamento(String tntCodigo, String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public List<TransferObject> getRelacionamentoSvc(String tntCodigo, String svcCodigoOrigem, String svcCodigoDestino, AcessoSistema responsavel) throws ParametroControllerException;

    public List<TransferObject> filtrarServicosSemRelacionamentoAlongamento(List<TransferObject> servicos, AcessoSistema responsavel) throws ParametroControllerException;

    public List<TransferObject> lstRelacionamentoSvcCorrecao(String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public boolean hasValidacaoDataNasc(AcessoSistema responsavel) throws ParametroControllerException;

    // Correção de Saldo
    public CustomTransferObject getServicoCorrecao(String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public List<MargemTO> lstMargensIncidentes(String svcCodigo, String csaCodigo, String orgCodigo, String rseCodigo, Short marCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public Short getSvcIncMargem(String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public List<TransferObject> lstTipoNatureza(AcessoSistema responsavel) throws ParametroControllerException;

    // ParametroAgendamento
    public ParametroAgendamento findParamAgendamento(String agdCodigo, String pagNome, String pagValor, AcessoSistema responsavel) throws ParametroControllerException;

    public List<ParametroAgendamento> findParamAgendamento(String agdCodigo, String pagNome, AcessoSistema responsavel) throws ParametroControllerException;

    public void atualizaParamAgendamento(String agdCodigo, String nome, String valor, AcessoSistema responsavel) throws ParametroControllerException;

    public List<TransferObject> lstRestricoesAcesso(String csaCodigo, int offset, int count, AcessoSistema responsavel) throws ParametroControllerException;

    public List<TransferObject> lstTodasRestricoesAcesso(AcessoSistema responsavel) throws ParametroControllerException;

    public int countRestricoesAcesso(String csaCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public String createRestricaoAcesso(TransferObject restricaoAcessoTO, AcessoSistema responsavel) throws ParametroControllerException;

    public void excluirRegraRestricaoAcesso(String rraCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    // AcessoRecurso
    public List<TransferObject> lstFuncoesAcessoRecurso(AcessoSistema responsavel) throws ParametroControllerException;

    public String createAcessoRecurso(String funCodigo, String papCodigo, String acrRecurso, String acrParametro, String acrOperacao, String acrSessao, String acrBloqueio, Short acrAtivo, String acrFimFluxo, String itmCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public void removeAcessoRecursoByFunCodigo(String funCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    // AcessoUsuario
    public TransferObject getAcessoUsuario(String acrCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public void saveAcessoUsuario(String acrCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    // parâmetros de Plano de desconto do SDP
    public List<TransferObject> selectParamPlano(String plaCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public Map<String, String> getParamPlano(String plaCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public void validaParametrosPlanoDesconto(String plaCodigo, String svcCodigo, BigDecimal adeVlr, Integer adePrazo, String adeIndice, AcessoSistema responsavel) throws ParametroControllerException;

    public List<TransferObject> selectTipoNaturezaEditavelServico(String nseCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public List<TransferObject> listaLimitesMaxMinParamSvcCseNse(List<String> tpsCodigos, String nseCodigo, boolean limiteMinimo, AcessoSistema responsavel) throws ParametroControllerException;

    // ParamOrgao
    public List<TransferObject> selectParamOrgaoEditavel(String orgCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public void updateParamOrgao(String paoVlr, String taoCodigo, String orgCodigo, AcessoSistema responsavel) throws ParametroControllerException;
    public ParamOrgao findParamOrgaoByOrgCodigoAndTaoCodigo(String orgCodigo, String taoCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public Integer buscarQuantidadeParamOrgao(String taoCodigo, String estCodigo, String orgCodigo, String paoVlr, AcessoSistema responsavel) throws ParametroControllerException;

    public int calcularAdeCarenciaDiaCorteCsa(int adeCarencia, String csaCodigo, String orgCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    List<TransferObject> filtraAdeRestringePortabilidade(List<TransferObject> lstConsignacao, String rseCodigo, String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public boolean isReservaSaudeSemModulo(String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public List<TransferObject> selectParamSvcCsaDiferente(List<String> svcCodigos, List<String> csaCodigos, List<String> tpsCodigos, String pscVlrDiferente, String pscVlrRefDiferente, boolean ativo, AcessoSistema responsavel) throws ParametroControllerException;

    public List<TransferObject> selectSvcByValorFixo(String csaCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public boolean isExigeReconhecimentoFacialServidor(String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public void invertVinculoParam(int invertCode, String csaCodigo, AcessoSistema responsavel) throws ConvenioControllerException;

    public boolean isObrigatorioAnexoInclusao (String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public boolean isSimularConsignacaoComReconhecimentoFacialELiveness(String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public List<TransferObject> lstBloqueioCnvRegistroServidorEntidade(String csaCodigo, AcessoSistema responsavel) throws ParametroControllerException;

    public List<TransferObject> lstBloqueioCnvRegistroServidorCnvCodigos(List<String> cnvCodigos, AcessoSistema responsavel) throws ParametroControllerException;

    public boolean verificaAutorizacaoReservaSemSenha(String rseCodigo, String svcCodigo, boolean senhaObrigatoria, String adeNumero, AcessoSistema responsavel) throws ParametroControllerException;

    public InformacaoSerCompraEnum senhaServidorObrigatoriaCompra(String svcCodigo, String rseCodigo, AcessoSistema responsavel) throws ParametroControllerException;
}