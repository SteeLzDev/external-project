package com.zetra.econsig.helper.margem;

import java.math.BigDecimal;
import java.util.List;

import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.cartaocredito.ValidadorCartaoCreditoController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: MargemDisponivel</p>
 * <p>Description: Centraliza a busca da margem disponível.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MargemDisponivel {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MargemDisponivel.class);

    private ExibeMargem exibeMargem;
    private String marDescricao;
    private BigDecimal margemRestante;
    private String tipoVlr = CodedValues.TIPO_VLR_FIXO;
    private String marDescricaoDependente;
    private BigDecimal margemRestanteDependente;

    public MargemDisponivel(String rseCodigo, String csaCodigo, String svcCodigo, Short adeIncMargem, AcessoSistema responsavel) throws ViewHelperException {
        this(rseCodigo, csaCodigo, svcCodigo, adeIncMargem, false, null, responsavel);
    }

    public MargemDisponivel(String rseCodigo, String csaCodigo, String svcCodigo, Short adeIncMargem, boolean serAtivo, AcessoSistema responsavel) throws ViewHelperException {
        this(rseCodigo, csaCodigo, svcCodigo, adeIncMargem, serAtivo, null, responsavel);
    }

    public MargemDisponivel(String rseCodigo, String csaCodigo, String svcCodigo, Short adeIncMargem, List<String> adeCodigosRenegociacao, AcessoSistema responsavel) throws ViewHelperException {
        this(rseCodigo, csaCodigo, svcCodigo, adeIncMargem, false, null, responsavel);
    }

    public MargemDisponivel(String rseCodigo, String csaCodigo, String svcCodigo, Short adeIncMargem, boolean serAtivo, List<String> adeCodigosRenegociacao, AcessoSistema responsavel) throws ViewHelperException {
        try {
            margemRestante = new BigDecimal("0.00");

            if (adeIncMargem == null) {
                if (!TextHelper.isNull(svcCodigo)) {
                    // Consulta os parâmetros de serviço e define em qual margem deve ser consultada
                    final ParamSvcTO paramSvcCse = ParamSvcTO.getParamSvcTO(svcCodigo, responsavel);
                    adeIncMargem = paramSvcCse.getTpsIncideMargem();
                } else {
                    throw new ViewHelperException("mensagem.usoIncorretoSistema", responsavel);
                }
            }

            if (adeIncMargem.equals(CodedValues.INCIDE_MARGEM_NAO)) {
                margemRestante = new BigDecimal(Double.MAX_VALUE);

                if (ParamSist.paramEquals(CodedValues.TPC_EXIBE_VALOR_DISPONIVEL_LANCAMENTO_CARTAO, CodedValues.TPC_SIM, responsavel) && CodedValues.FUN_RES_MARGEM.equals(responsavel.getFunCodigo())) {
                    try {
                        ValidadorCartaoCreditoController validadorCartaoCreditoController = ApplicationContextProvider.getApplicationContext().getBean(ValidadorCartaoCreditoController.class);
                        MargemTO margem = validadorCartaoCreditoController.consultarMargemDisponivelLancamento(rseCodigo, csaCodigo, svcCodigo, responsavel);
                        if (margem != null) {
                            margemRestante = margem.getMrsMargemRest();
                            exibeMargem = new ExibeMargem(margem, responsavel);
                            if (margem.getMarTipoVlr() != null) {
                                tipoVlr = margem.getMarTipoVlr().toString();
                            }
                        }
                    } catch (AutorizacaoControllerException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }

            } else {
                ConsultarMargemController consultarMargemController = ApplicationContextProvider.getApplicationContext().getBean(ConsultarMargemController.class);
                List<MargemTO> margens = consultarMargemController.consultarMargem(rseCodigo, null, svcCodigo, csaCodigo, null, true, serAtivo, true, adeCodigosRenegociacao, responsavel);
                for (MargemTO margem : margens) {
                    Short marCodigo = margem.getMarCodigo();
                    if (marCodigo.equals(adeIncMargem)) {
                        marDescricao = margem.getMarDescricao();
                        if (margem.getMrsMargemRest() != null) {
                            margemRestante = margem.getMrsMargemRest();
                        }
                        exibeMargem = new ExibeMargem(margem, responsavel);
                        if (margem.getMarTipoVlr() != null) {
                            tipoVlr = margem.getMarTipoVlr().toString();
                        }
                    } else if (margem.getMrsMargemRest() != null) {
                        // Se retornou uma margem além daquela da incidência do serviço, verifica se é uma margem dependente
                        // e caso seja, verifica se deve ser exibida
                        if ((responsavel.isCseOrg() && ParamSist.paramEquals(CodedValues.TPC_EXIBE_MARGENS_DEPENDENTES_PARA_CSE_ORG, CodedValues.TPC_SIM, responsavel)) ||
                                (responsavel.isCsaCor() && ParamSist.paramEquals(CodedValues.TPC_EXIBE_MARGENS_DEPENDENTES_PARA_CSA_COR, CodedValues.TPC_SIM, responsavel)) ||
                                (responsavel.isSup() && ParamSist.paramEquals(CodedValues.TPC_EXIBE_MARGENS_DEPENDENTES_PARA_SUP, CodedValues.TPC_SIM, responsavel)) ||
                                (responsavel.isSer() && ParamSist.paramEquals(CodedValues.TPC_EXIBE_MARGENS_DEPENDENTES_PARA_SER, CodedValues.TPC_SIM, responsavel))) {
                            if (margem.getMarCodigoPai() != null && margem.getMarCodigoPai().equals(adeIncMargem)) {
                                marDescricaoDependente = margem.getMarDescricao();
                                margemRestanteDependente = margem.getMrsMargemRest();
                            }
                        }
                    }
                }
            }
            if (exibeMargem == null) {
                exibeMargem = new ExibeMargem(null, responsavel);
            }
        } catch (ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException(ex);
        }
    }

    public ExibeMargem getExibeMargem() {
        return exibeMargem;
    }

    public String getMarDescricao() {
        return marDescricao;
    }

    public BigDecimal getMargemRestante() {
        return margemRestante;
    }

    public String getTipoVlr() {
        return tipoVlr;
    }

    public String getMarDescricaoDependente() {
        return marDescricaoDependente;
    }

    public BigDecimal getMargemRestanteDependente() {
        return margemRestanteDependente;
    }
}
