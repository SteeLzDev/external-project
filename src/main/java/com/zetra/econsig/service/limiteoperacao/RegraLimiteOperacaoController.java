package com.zetra.econsig.service.limiteoperacao;

import java.math.BigDecimal;
import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.RegraLimiteOperacaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.RegraLimiteOperacao;

public interface RegraLimiteOperacaoController {

    public List<TransferObject> lstRegraLimiteOperacao(AcessoSistema responsavel) throws RegraLimiteOperacaoControllerException;

    public void validarLimiteOperacao(BigDecimal adeVlr, BigDecimal adeVlrLiquido, Integer adePrazo, Integer adeCarencia, String adePeriodicidade,
                                      String nseCodigo, String svcCodigo, String ncaCodigo, String csaCodigo, String corCodigo, String cnvCodVerba, String cnvCodVerbaRef,
                                      RegistroServidor registroServidor, List<String> adeCodigosRenegociacao, String acao, AcessoSistema responsavel) throws RegraLimiteOperacaoControllerException;

    public RegraLimiteOperacao findRegraByPrimaryKey(String rloCodigo, AcessoSistema responsavel) throws RegraLimiteOperacaoControllerException;

    public TransferObject findRegra(String rloCodigo, AcessoSistema responsavel) throws RegraLimiteOperacaoControllerException;

    public RegraLimiteOperacao createRegra(RegraLimiteOperacao regraLimiteOperacao, AcessoSistema responsavel) throws RegraLimiteOperacaoControllerException;

    public void removeRegra(String rloCodigo, AcessoSistema resposnavel) throws RegraLimiteOperacaoControllerException;

    public void updateRegra(RegraLimiteOperacao regraLimiteOperacao, AcessoSistema responsavel) throws RegraLimiteOperacaoControllerException;

    public List<TransferObject> lstRegrasLimiteOperacaoFilter(String csaCodigo, int count, int offset, AcessoSistema responsavel) throws RegraLimiteOperacaoControllerException;

    public int countRegrasLimiteOperacaoFilter(String csaCodigo, AcessoSistema responsavel) throws RegraLimiteOperacaoControllerException;

}
