package com.zetra.econsig.service.consignacao;

import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: TransferirConsignacaoController</p>
 * <p>Description: Interface Remota para Session Bean para operação de transferência de consignações.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface TransferirConsignacaoController {

    public int countAdeTransferencia(String csaCodigoOrigem, String csaCodigoDestino, String svcCodigoOrigem, String svcCodigoDestino, String orgCodigo, List<String> sadCodigo, java.util.Date periodoIni, java.util.Date periodoFim, List<Long> adeNumero, String rseMatricula, String serCpf, boolean somenteConveniosAtivos, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> listarAdeTransferencia(String csaCodigoOrigem, String csaCodigoDestino, String svcCodigoOrigem, String svcCodigoDestino, String orgCodigo, List<String> sadCodigo, java.util.Date periodoIni, java.util.Date periodoFim, List<Long> adeNumero, String rseMatricula, String serCpf, int offset, int count, boolean somenteConveniosAtivos, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void transfereAde(String csaCodigoOrigem, String csaCodigoDestino, String svcCodigoOrigem, String svcCodigoDestino, String orgCodigo, List<String> sadCodigo, java.util.Date periodoIni, java.util.Date periodoFim, List<Long> adeNumero, String rseMatricula, String serCpf, String ocaObs, boolean atualizarAdeIncMargem, boolean somenteConveniosAtivos, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<String> transfereAde(List<String> adeCodigos, String rseCodigoNov, String rseCodigoAnt, String rseMatriculaAntiga, String orgIdentificadorAntigo, boolean validaBloqSer, boolean verificaRelacionamentos, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public boolean transfereAdeServidores(List<String> adeCodigoList, String rseCodigoOrigem, String rseCodigoDestino, String rseMatriculaOrigem, String orgIdentificadorOrigem, CustomTransferObject tipoMotivoOperacao, boolean comSenhaServidor, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> pesquisarConsignacaoRelacionamento(List<String> adeCodigoList, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> distribuirConsignacoesPorServicos(String svcCodigoOrigem, List<String> svcCodigosDestino, List<String> csaCodigos, String rseMatricula, String serCpf, String tmoCodigo, String ocaObs, boolean validar, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void transfereAdeNovoOrgao(String rseCodigo, String orgCodigoNovo, String tmoCodigo, String obs, AcessoSistema responsavel) throws AutorizacaoControllerException;
}
