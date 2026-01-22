package com.zetra.econsig.service.beneficios;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.CalculoBeneficioControllerException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.Beneficio;
import com.zetra.econsig.persistence.entity.CalculoBeneficio;
import com.zetra.econsig.persistence.entity.CalculoBeneficioHome;
import com.zetra.econsig.persistence.entity.GrauParentesco;
import com.zetra.econsig.persistence.entity.MotivoDependencia;
import com.zetra.econsig.persistence.entity.Orgao;
import com.zetra.econsig.persistence.entity.TipoBeneficiario;
import com.zetra.econsig.persistence.query.beneficios.ListaCalculoBeneficioQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: CalculoBeneficioControllerBean</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class CalculoBeneficioControllerBean implements CalculoBeneficioController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CalculoBeneficioControllerBean.class);

    @Override
    public int lstCountCalculoBeneficio(TransferObject criterio, AcessoSistema responsavel) throws CalculoBeneficioControllerException {
        try {
            ListaCalculoBeneficioQuery query = new ListaCalculoBeneficioQuery();

            if (criterio != null && !criterio.getAtributos().isEmpty()) {

                if (!TextHelper.isNull(criterio.getAttribute("BEN_CODIGO"))) {
                    query.benCodigo = criterio.getAttribute("BEN_CODIGO").toString();
                }

                if (!TextHelper.isNull(criterio.getAttribute("ORG_CODIGO"))) {
                    query.orgCodigo = criterio.getAttribute("ORG_CODIGO").toString();
                }

                if (!TextHelper.isNull(criterio.getAttribute("STATUS_REGRA"))) {
                    query.statusRegra = criterio.getAttribute("STATUS_REGRA").toString();
                }

                if (!TextHelper.isNull(criterio.getAttribute("TIB_CODIGO"))) {
                    query.tibCodigo = criterio.getAttribute("TIB_CODIGO").toString();
                }

                if (!TextHelper.isNull(criterio.getAttribute("GRP_CODIGO"))) {
                    query.grpCodigo = criterio.getAttribute("GRP_CODIGO").toString();
                }

                if (!TextHelper.isNull(criterio.getAttribute("MDE_CODIGO"))) {
                    query.mdeCodigo = criterio.getAttribute("MDE_CODIGO").toString();
                }

                if (!TextHelper.isNull(criterio.getAttribute("DATA"))) {
                    query.data = DateHelper.parse(criterio.getAttribute("DATA").toString(), LocaleHelper.getDatePattern());
                }
            }

            query.count = true;

            return query.executarContador();
        } catch (ParseException ex) {
            throw new CalculoBeneficioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (HQueryException ex) {
            throw new CalculoBeneficioControllerException(ex);
        }
    }

    @Override
    public void iniciarTabelaVigente(AcessoSistema responsavel) throws CalculoBeneficioControllerException {
        try {
            List<CalculoBeneficio> calculoBeneficios = CalculoBeneficioHome.listaCalculoBeneficioObject(2, responsavel);

            for (CalculoBeneficio calculoBeneficio : calculoBeneficios) {
                create(calculoBeneficio.getTipoBeneficiario(), calculoBeneficio.getOrgao(), calculoBeneficio.getBeneficio(), calculoBeneficio.getGrauParentesco(), calculoBeneficio.getMotivoDependencia(), null, null, calculoBeneficio.getClbValorMensalidade(), calculoBeneficio.getClbValorSubsidio(), calculoBeneficio.getClbFaixaEtariaIni(), calculoBeneficio.getClbFaixaEtariaFim(), calculoBeneficio.getClbFaixaSalarialIni(), calculoBeneficio.getClbFaixaSalarialFim(), responsavel);
            }
        } catch (Exception ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new CalculoBeneficioControllerException(ex);
        }
    }

    @Override
    public void ativarTabelaIniciada(AcessoSistema responsavel) throws CalculoBeneficioControllerException {
        try {
            CalculoBeneficioHome.ativarTabela();
        } catch (Exception ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new CalculoBeneficioControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> listaCalculoBeneficio(TransferObject criterio, int offset, int size, AcessoSistema responsavel) throws CalculoBeneficioControllerException {
        try {
            ListaCalculoBeneficioQuery query = new ListaCalculoBeneficioQuery();

            if (criterio != null && !criterio.getAtributos().isEmpty()) {

                if (!TextHelper.isNull(criterio.getAttribute("BEN_CODIGO"))) {
                    query.benCodigo = criterio.getAttribute("BEN_CODIGO").toString();
                }

                if (!TextHelper.isNull(criterio.getAttribute("ORG_CODIGO"))) {
                    query.orgCodigo = criterio.getAttribute("ORG_CODIGO").toString();
                }

                if (!TextHelper.isNull(criterio.getAttribute("STATUS_REGRA"))) {
                    query.statusRegra = criterio.getAttribute("STATUS_REGRA").toString();
                }

                if (!TextHelper.isNull(criterio.getAttribute("TIB_CODIGO"))) {
                    query.tibCodigo = criterio.getAttribute("TIB_CODIGO").toString();
                }

                if (!TextHelper.isNull(criterio.getAttribute("GRP_CODIGO"))) {
                    query.grpCodigo = criterio.getAttribute("GRP_CODIGO").toString();
                }

                if (!TextHelper.isNull(criterio.getAttribute("MDE_CODIGO"))) {
                    query.mdeCodigo = criterio.getAttribute("MDE_CODIGO").toString();
                }

                if (!TextHelper.isNull(criterio.getAttribute("DATA"))) {
                    query.data = DateHelper.parse(criterio.getAttribute("DATA").toString(), LocaleHelper.getDatePattern());
                }
            }

            query.count = false;

            if (size != 0) {
                query.firstResult = offset;
                query.maxResults = size;
            }

            return query.executarDTO();
        } catch (ParseException ex) {
            throw new CalculoBeneficioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (HQueryException ex) {
            throw new CalculoBeneficioControllerException(ex);
        }
    }

    @Override
    public void update(CalculoBeneficio calculoBeneficio, AcessoSistema responsavel) throws CalculoBeneficioControllerException {
        try {

            CalculoBeneficio calculoBeneficioAntigo = CalculoBeneficioHome.findCalculoBeneficioByCodigo(calculoBeneficio.getClbCodigo());
            LogDelegate log = new LogDelegate(responsavel, Log.CALCULO_BENEFICIO, Log.UPDATE, Log.LOG_INFORMACAO);

            if (calculoBeneficio.getBeneficio().getBenCodigo() != null && calculoBeneficio.getBeneficio().getBenCodigo().compareTo(calculoBeneficioAntigo.getBeneficio().getBenCodigo()) != 0) {
                log.addChangedField(Columns.CLB_BEN_CODIGO, calculoBeneficio.getBeneficio().getBenCodigo());
            }

            if ((calculoBeneficio.getOrgao() != null && calculoBeneficioAntigo.getOrgao() == null) || calculoBeneficio.getOrgao() != null && calculoBeneficio.getOrgao().getOrgCodigo().compareTo(calculoBeneficioAntigo.getOrgao().getOrgCodigo()) != 0) {
                log.addChangedField(Columns.CLB_ORG_CODIGO, calculoBeneficio.getOrgao().getOrgCodigo());
            }
            if ((calculoBeneficio.getTipoBeneficiario() != null && calculoBeneficioAntigo.getTipoBeneficiario() == null) || (calculoBeneficio.getTipoBeneficiario() != null && calculoBeneficio.getTipoBeneficiario().getTibCodigo().compareTo(calculoBeneficioAntigo.getTipoBeneficiario().getTibCodigo()) != 0)) {
                log.addChangedField(Columns.CLB_TIB_CODIGO, calculoBeneficio.getTipoBeneficiario().getTibCodigo());
            }

            if ((calculoBeneficio.getGrauParentesco() != null && calculoBeneficioAntigo.getGrauParentesco() == null) || (calculoBeneficio.getGrauParentesco() != null && calculoBeneficio.getGrauParentesco().getGrpCodigo().compareTo(calculoBeneficioAntigo.getGrauParentesco().getGrpCodigo()) != 0)) {
                log.addChangedField(Columns.GRP_CODIGO, calculoBeneficio.getGrauParentesco().getGrpCodigo());
            }

            if ((calculoBeneficio.getClbValorMensalidade() != null && calculoBeneficioAntigo.getClbValorMensalidade() == null) || calculoBeneficio.getClbValorMensalidade() != null && calculoBeneficio.getClbValorMensalidade().compareTo(calculoBeneficioAntigo.getClbValorMensalidade()) != 0) {
                log.addChangedField(Columns.CLB_VALOR_MENSALIDADE, calculoBeneficio.getClbValorMensalidade());
            }

            if ((calculoBeneficio.getClbValorSubsidio() != null && calculoBeneficioAntigo.getClbValorSubsidio() == null) || calculoBeneficio.getClbValorSubsidio() != null && calculoBeneficio.getClbValorSubsidio().compareTo(calculoBeneficioAntigo.getClbValorSubsidio()) != 0) {
                log.addChangedField(Columns.CLB_VALOR_SUBSIDIO, calculoBeneficio.getClbValorSubsidio());
            }

            if ((calculoBeneficio.getClbFaixaEtariaIni() != null && calculoBeneficioAntigo.getClbFaixaEtariaIni() == null) || calculoBeneficio.getClbFaixaEtariaIni() != null && calculoBeneficio.getClbFaixaEtariaIni().compareTo(calculoBeneficioAntigo.getClbFaixaEtariaIni()) != 0) {
                log.addChangedField(Columns.CLB_FAIXA_ETARIA_INI, calculoBeneficio.getClbFaixaEtariaIni());
            }
            if ((calculoBeneficio.getClbFaixaSalarialFim() != null && calculoBeneficioAntigo.getClbFaixaEtariaFim() == null) || calculoBeneficio.getClbFaixaEtariaFim() != null && calculoBeneficio.getClbFaixaEtariaFim().compareTo(calculoBeneficioAntigo.getClbFaixaEtariaFim()) != 0) {
                log.addChangedField(Columns.CLB_FAIXA_ETARIA_FIM, calculoBeneficio.getClbFaixaEtariaFim());
            }

            if ((calculoBeneficio.getClbFaixaSalarialIni() != null && calculoBeneficioAntigo.getClbFaixaSalarialIni() == null) || calculoBeneficio.getClbFaixaSalarialIni() != null && calculoBeneficio.getClbFaixaSalarialIni().compareTo(calculoBeneficioAntigo.getClbFaixaSalarialIni()) != 0) {
                log.addChangedField(Columns.CLB_FAIXA_SALARIAL_INI, calculoBeneficio.getClbFaixaSalarialIni());
            }
            if ((calculoBeneficio.getClbFaixaSalarialFim() != null && calculoBeneficioAntigo.getClbFaixaEtariaFim() == null) || calculoBeneficio.getClbFaixaSalarialFim() != null && calculoBeneficio.getClbFaixaSalarialFim().compareTo(calculoBeneficioAntigo.getClbFaixaSalarialFim()) != 0) {
                log.addChangedField(Columns.CLB_FAIXA_SALARIAL_FIM, calculoBeneficio.getClbFaixaSalarialFim());
            }

            CalculoBeneficioHome.update(calculoBeneficio);
            log.write();
        } catch (Exception ex) {
            throw new CalculoBeneficioControllerException(ex);
        }
    }

    @Override
    public CalculoBeneficio create(TipoBeneficiario tipoBeneficio, Orgao orgao, Beneficio beneficio, GrauParentesco grauParentesco, MotivoDependencia motivoDependencia, Date vigenciaIni, Date vigenciaFim, BigDecimal valorMensalidade, BigDecimal valorSubsidio, Short faixaEtariaIni, Short faixaEtariaFim, BigDecimal fixaSalarialIni, BigDecimal fixaSalarialFim, AcessoSistema responsavel) throws CalculoBeneficioControllerException {
        try {
            CalculoBeneficio calculoBeneficio = CalculoBeneficioHome.create(tipoBeneficio, orgao, beneficio, grauParentesco, motivoDependencia, vigenciaIni, vigenciaFim, valorMensalidade, valorSubsidio, faixaEtariaIni, faixaEtariaFim, fixaSalarialIni, fixaSalarialFim);
            LogDelegate log = new LogDelegate(responsavel, Log.CALCULO_BENEFICIO, Log.CREATE, Log.LOG_INFORMACAO);
            log.write();
            return calculoBeneficio;
        } catch (Exception ex) {
            throw new CalculoBeneficioControllerException(ex);
        }
    }

    @Override
    public void remove(CalculoBeneficio calculoBeneficio, AcessoSistema responsavel) throws CalculoBeneficioControllerException {
        try {
            CalculoBeneficioHome.remove(calculoBeneficio);
            LogDelegate log = new LogDelegate(responsavel, Log.CALCULO_BENEFICIO, Log.DELETE, Log.LOG_INFORMACAO);
            log.write();
        } catch (Exception ex) {
            throw new CalculoBeneficioControllerException(ex);
        }
    }

    @Override
    public void excluirTabelaIniciada(AcessoSistema responsavel) throws CalculoBeneficioControllerException {
        try {
            CalculoBeneficioHome.deletarTabelaIniciada();

            LogDelegate log = new LogDelegate(responsavel, Log.CALCULO_BENEFICIO, Log.DELETE, Log.LOG_INFORMACAO);
            log.write();
        } catch (Exception ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new CalculoBeneficioControllerException("mensagem.erro.remover.tabela.iniciada", responsavel);
        }
    }

    @Override
    public CalculoBeneficio findCalculoBeneficioByCodigo(String clbCodigo, AcessoSistema responsavel) throws CalculoBeneficioControllerException {
        try {
            CalculoBeneficio calculoBeneficio = CalculoBeneficioHome.findCalculoBeneficioByCodigo(clbCodigo);
            LogDelegate log = new LogDelegate(responsavel, Log.CALCULO_BENEFICIO, Log.FIND, Log.LOG_INFORMACAO);
            log.write();
            return calculoBeneficio;
        } catch (Exception ex) {
            throw new CalculoBeneficioControllerException(ex);
        }
    }
}
