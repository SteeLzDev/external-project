package com.zetra.econsig.service.servidor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.exception.MargemControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.MargemRegistroServidor;

/**
 * <p>Title: ConsultarMargemControllerBean</p>
 * <p>Description: Session Bean para a operação de Consulta de Margem.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ConsultarMargemController {

    public boolean servidorTemMargem(String rseCodigo, BigDecimal adeVlr, String svcCodigo, boolean serAtivo, AcessoSistema responsavel) throws ServidorControllerException;

    public List<MargemTO> consultarMargem(String rseCodigo, BigDecimal adeVlr, String svcCodigo, String csaCodigo, boolean senhaServidorOK, boolean serAtivo, AcessoSistema responsavel) throws ServidorControllerException;

    public List<MargemTO> consultarMargem(String rseCodigo, BigDecimal adeVlr, String svcCodigo, String csaCodigo, boolean mobile, boolean senhaServidorOK, String senhaUtilizada, boolean serAtivo, String nseCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public List<MargemTO> consultarMargem(String rseCodigo, BigDecimal adeVlr, String svcCodigo, String csaCodigo, boolean senhaServidorOK, String senhaUtilizada, boolean serAtivo, String nseCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public List<MargemTO> consultarMargem(String rseCodigo, BigDecimal adeVlr, String svcCodigo, String csaCodigo, Short marCodigo, boolean senhaServidorOK, String senhaUtilizada, boolean serAtivo, boolean ignoraExibicaoMargem, List<String> adeCodigosRenegociacao, String nseCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public List<MargemTO> consultarMargem(String rseCodigo, BigDecimal adeVlr, String svcCodigo, String csaCodigo, Short marCodigo, boolean senhaServidorOK, boolean serAtivo, boolean ignoraExibicaoMargem, List<String> adeCodigosRenegociacao, AcessoSistema responsavel) throws ServidorControllerException;

    public List<MargemTO> consultarMargem(String rseCodigo, BigDecimal adeVlr, String svcCodigo, String csaCodigo, Short marCodigo, boolean mobile, boolean senhaServidorOK, String senhaUtilizada, boolean serAtivo, boolean ignoraExibicaoMargem, List<String> adeCodigosRenegociacao, String nseCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public MargemTO consultarMargemLimitePorCsa(String rseCodigo, String csaCodigo, Short codMargemLimite, List<String> adeCodigosRenegociacao, AcessoSistema responsavel) throws ServidorControllerException;

    public BigDecimal calcularMargemProporcional(Short marCodigo, BigDecimal margemRest, RegistroServidorTO registroServidor, String csaCodigo, List<String> adeCodigosRenegociacao, AcessoSistema responsavel) throws ServidorControllerException;

    public BigDecimal calcularMargemLateral(Short marCodigo, BigDecimal margemRest, RegistroServidorTO registroServidor, AcessoSistema responsavel) throws ServidorControllerException;

    public BigDecimal limitarMargemRestanteCsa(Short marCodigo, String rseCodigo, BigDecimal rseMargem, BigDecimal rseMargemRest, BigDecimal rseMargemUsada, String csaCodigo, String Orgcodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public BigDecimal getMargemDisponivelCompulsorio(String rseCodigo, String svcCodigo, String svcPrioridade, Short adeIncMargem, boolean controlaMargem, String adeCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> lstExtratoMargemRse(String rseCodigo, String orgCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public BigDecimal somarContratosTratamentoEspecialMargem(String rseCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public List<TransferObject> lstMargemNatureza(String marCodigo, AcessoSistema responsavel) throws ServidorControllerException;

    public Map<String, String> consultarMargemDisponivelTotal(AcessoSistema responsavel) throws ServidorControllerException;

    public MargemRegistroServidor getMargemRegistroServidor(String rseCodigo, Short marCodigo, AcessoSistema responsavel) throws MargemControllerException;

    public RegistroServidorTO atualizaMargemServidorExternoRegistroServidor(String url, RegistroServidorTO registroServidor, AcessoSistema responsavel) throws ServidorControllerException;
}
