package com.zetra.econsig.service.beneficios;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ContratoBeneficioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.ContratoBeneficio;
import com.zetra.econsig.persistence.entity.StatusContratoBeneficio;

/**
 * <p>Title: ContratoBeneficioController</p>
 * <p>Description: Controller para operaÃ§Ãµes de contrato beneficio</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ContratoBeneficioController {

    public ContratoBeneficio findByPrimaryKey(String cbeCodigo, AcessoSistema responsavel) throws ContratoBeneficioControllerException;

    public String criaOcorrenciaContratoBeneficio(String cbeCodigo, String tocCodigo, String ocbObs, Date ocbData, String tmoCodigo, AcessoSistema responsavel) throws ContratoBeneficioControllerException;

    public void update(ContratoBeneficio contratoBeneficio, String tmoCodigo, AcessoSistema responsavel) throws ContratoBeneficioControllerException;

    public void aprovar(List<TransferObject> beneficiario, String tmoCodigo, String ocbObs, AcessoSistema responsavel) throws ContratoBeneficioControllerException;

    public void rejeitar(List<TransferObject> beneficiario, String tmoCodigo, String ocbObs, AcessoSistema responsavel) throws ContratoBeneficioControllerException;

    public void cancelar(List<TransferObject> beneficiario, String tmoCodigo, String ocbObs, String tdaAdesao, String tdaMesesContruicao, boolean solicitacaoCancelamento, AcessoSistema responsavel) throws ContratoBeneficioControllerException;

    public void update(ContratoBeneficio cttBeneficio, String tocCodigo, String ocbObs, String tmoCodigo, AcessoSistema responsavel) throws ContratoBeneficioControllerException;

    public List<StatusContratoBeneficio> listAllStatusContratoBeneficio(AcessoSistema responsavel) throws ContratoBeneficioControllerException;

    public List<TransferObject> listLancamentosContratosBeneficiosByDataAndCbeCodigo(String cbeCodigo, Date prdDataDesconto, AcessoSistema responsavel) throws ContratoBeneficioControllerException;

    public List<TransferObject> listOcorrenciaContratosBeneficiosByCbeCodigo(TransferObject criterio, Boolean justInfo, AcessoSistema responsavel) throws ContratoBeneficioControllerException;

    public List<TransferObject> listarContratosBeneficioPorRegistroServidorQuery (TransferObject criterio, AcessoSistema responsavel) throws ContratoBeneficioControllerException;

    public List<TransferObject> listarContratosBeneficiosPendentesInclusao (TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws ContratoBeneficioControllerException;

    public List<TransferObject> listarContratosBeneficiosPendentesExclusao (TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws ContratoBeneficioControllerException;

    public long countContratosBeneficiosPendentesInclusao(TransferObject criterio, AcessoSistema responsavel) throws ContratoBeneficioControllerException;

    public long countContratosBeneficiosPendentesExclusao(TransferObject criterio, AcessoSistema responsavel) throws ContratoBeneficioControllerException;

    public List<String> criarReservaDeContratosBeneficios(String rseCodigo, Map<String, List<String>> dadosSimulacao, AcessoSistema responsavel) throws ContratoBeneficioControllerException;

    public List<String> criarReservaDeContratosBeneficios(String rseCodigo, Map<String, List<String>> dadosSimulacao, List<String> beneficiariosComContratoSaude, List<String> beneficiariosComContratoOdontologico, AcessoSistema responsavel) throws ContratoBeneficioControllerException;

    public List<String> criarReservaDeContratosBeneficiosMigracao(String rseCodigo, Map<String, List<String>> dadosSimulacao, AcessoSistema responsavel) throws ContratoBeneficioControllerException;

    public void validaCarenciaMigracaoContratoBeneficio(TransferObject contratoAtivo, String csaCodigo, String beCodigoNovoPlano, AcessoSistema responsavel) throws ContratoBeneficioControllerException;

    public TransferObject listarContratosBeneficiosMensalidadeEdicaoTela(String cbeCodigo, AcessoSistema responsavel) throws ContratoBeneficioControllerException;

    public void updateAnalisandoFuncaoEDadosAutorizacao(TransferObject dados, AcessoSistema responsavel) throws ContratoBeneficioControllerException;

    public void exclusaoManual(List<TransferObject> beneficiarios, Date cbeInicioVigencia, Date cbeDataFimVigencia, boolean cancelarInclusao, AcessoSistema responsavel) throws ContratoBeneficioControllerException;

    public void inclusaoManual(List<TransferObject> beneficiarios, Date cbeDataInicioVigencia, String cbeNumero, AcessoSistema responsavel) throws ContratoBeneficioControllerException;

    public void desfazerCancelamento(List<TransferObject> beneficiarios, String tmoCodigo, String ocbObs, AcessoSistema responsavel) throws ContratoBeneficioControllerException;

    public void desfazerCancelamentoAutomatico(AcessoSistema responsavel) throws ContratoBeneficioControllerException;

    public List<String> reativarContratoBeneficio(List<String> cbeCodigos, AcessoSistema responsavel) throws ContratoBeneficioControllerException;

    public void cancelarContratoBeneficioInadimplencia(String arquivoLote, AcessoSistema responsavel) throws ContratoBeneficioControllerException;

    public String criaContratoBeneficioSemRegrasModulos(String rseCodigo, String svcCodigo, String codigoDependente, String csaCodigo, String tibCodigo, BigDecimal adeVlr, AcessoSistema responsavel) throws ContratoBeneficioControllerException;
}
