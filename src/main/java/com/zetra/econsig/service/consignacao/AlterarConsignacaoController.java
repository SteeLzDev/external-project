package com.zetra.econsig.service.consignacao;

import java.math.BigDecimal;
import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.parametros.AlterarConsignacaoParametros;
import com.zetra.econsig.dto.parametros.AlterarMultiplasConsignacoesParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: AlterarConsignacaoControllerBean</p>
 * <p>Description: Session Bean para operação de Alteração de Contrato.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface AlterarConsignacaoController  {

    public void alterar(AlterarConsignacaoParametros alterarParam, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void atualizarConsignacao(AlterarConsignacaoParametros alterarParam, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void alterarMultiplosAdes(List<TransferObject> ade, AlterarMultiplasConsignacoesParametros parametros, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> validarAlteracaoMultiplosAdes(AlterarMultiplasConsignacoesParametros parametros, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void alterarConsignacoesDescontoEmFila(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> possuiRelacionamentoAlteracaoJudicial(String adeCodigoOrigem, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public boolean verificarAlteracaoPosterior(String adeCodigo, BigDecimal adeVlrAtual, BigDecimal adeVlrAnterior, AcessoSistema responsavel) throws AutorizacaoControllerException;
}
