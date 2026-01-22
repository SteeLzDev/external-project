package com.zetra.econsig.service.juros;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DefinicaoTaxaJurosControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.DefinicaoTaxaJuros;
import com.zetra.econsig.persistence.entity.DefinicaoTaxaJurosHome;
import com.zetra.econsig.persistence.query.definicaotaxajuros.PesquisarDefinicaoTaxaJurosQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: DefinicaoTaxaJurosControllerBean</p>
 * <p>Description: Session Bean para a operação de definição de regra de taxa de juros </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author: rodrigo.rosa $
 * $Revision: 25240 $
 * $Date: 2019-04-10 10:02:48 -0300 (qua, 10 abr 2019) $
 */
@Service
@Transactional
public class DefinicaoTaxaJurosControllerBean implements DefinicaoTaxaJurosController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DefinicaoTaxaJurosControllerBean.class);

    //Cria uma regra de taxa de juros do zero
    @Override
    public DefinicaoTaxaJuros create(String csaCodigo, String orgCodigo, String svcCodigo, String funCodigo, Short dtjFaixaEtariaIni, Short dtjFaixaEtariaFim, Short dtjFaixaTempServicoIni, Short dtjFaixaTempServicoFim, BigDecimal dtjFaixaSalarioIni, BigDecimal dtjFaixaSalarioFim, BigDecimal dtjFaixaMargemIni, BigDecimal dtjFaixaMargemFim, BigDecimal dtjFaixaValorTotalIni, BigDecimal dtjFaixaValorTotalFim, BigDecimal dtjFaixaValorContratoIni, BigDecimal dtjFaixaValorContratoFim, Short dtjFaixaPrazoIni, Short dtjFaixaPrazoFim, BigDecimal dtjTaxaJuros, BigDecimal dtjTaxaJurosMinima, Date dtjDataCadastro, AcessoSistema responsavel) throws DefinicaoTaxaJurosControllerException {
        try {
            final DefinicaoTaxaJuros definicaoTaxaJuros = DefinicaoTaxaJurosHome.create(csaCodigo, orgCodigo, svcCodigo, funCodigo,
                    dtjFaixaEtariaIni, dtjFaixaEtariaFim, dtjFaixaTempServicoIni, dtjFaixaTempServicoFim, dtjFaixaSalarioIni, dtjFaixaSalarioFim,
                    dtjFaixaMargemIni, dtjFaixaMargemFim, dtjFaixaValorTotalIni, dtjFaixaValorTotalFim, dtjFaixaValorContratoIni, dtjFaixaValorContratoFim,
                    dtjFaixaPrazoIni, dtjFaixaPrazoFim, dtjTaxaJuros, dtjTaxaJurosMinima, dtjDataCadastro);

            final LogDelegate log = new LogDelegate(responsavel, Log.DEFINICAO_REGRA_TAXA_JUROS, Log.CREATE, Log.LOG_INFORMACAO);
            log.setDefinicaoTaxaJuros(definicaoTaxaJuros.getDtjCodigo());
            log.setConsignataria(csaCodigo);
            log.setServico(svcCodigo);
            if (!TextHelper.isNull(orgCodigo)) {
                log.setOrgao(orgCodigo);
            }
            if (!TextHelper.isNull(funCodigo)) {
                log.setFuncao(funCodigo);
            }
            log.write();

            return definicaoTaxaJuros;
        } catch (Exception ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new DefinicaoTaxaJurosControllerException(ex);
        }
    }

    // Atualiza uma regra de taxa de juros existente
    @Override
    public void update(DefinicaoTaxaJuros definicaoTaxaJuros, AcessoSistema responsavel) throws DefinicaoTaxaJurosControllerException {
        try {
            final DefinicaoTaxaJuros regraTaxaJurosAntiga = DefinicaoTaxaJurosHome.findDefinicaoTaxaJurosByCodigo(definicaoTaxaJuros.getDtjCodigo());
            final LogDelegate log = new LogDelegate(responsavel, Log.DEFINICAO_REGRA_TAXA_JUROS, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setDefinicaoTaxaJuros(definicaoTaxaJuros.getDtjCodigo());

            if ((definicaoTaxaJuros.getOrgCodigo() != null && regraTaxaJurosAntiga.getOrgCodigo() == null) || (definicaoTaxaJuros.getOrgCodigo() == null && regraTaxaJurosAntiga.getOrgCodigo() != null) || (definicaoTaxaJuros.getOrgCodigo() != null && definicaoTaxaJuros.getOrgCodigo().compareTo(regraTaxaJurosAntiga.getOrgCodigo()) != 0)) {
                log.addChangedField(Columns.DTJ_ORG_CODIGO, definicaoTaxaJuros.getOrgCodigo(), regraTaxaJurosAntiga.getOrgCodigo());
            }

            if ((definicaoTaxaJuros.getSvcCodigo() != null && regraTaxaJurosAntiga.getSvcCodigo() == null) || (definicaoTaxaJuros.getSvcCodigo() == null && regraTaxaJurosAntiga.getSvcCodigo() != null) || (definicaoTaxaJuros.getSvcCodigo() != null && definicaoTaxaJuros.getSvcCodigo().compareTo(regraTaxaJurosAntiga.getSvcCodigo()) != 0)) {
                log.addChangedField(Columns.DTJ_SVC_CODIGO, definicaoTaxaJuros.getSvcCodigo(), regraTaxaJurosAntiga.getSvcCodigo());
            }

            if ((definicaoTaxaJuros.getFunCodigo() != null && regraTaxaJurosAntiga.getFunCodigo() == null) || (definicaoTaxaJuros.getFunCodigo() == null && regraTaxaJurosAntiga.getFunCodigo() != null) || (definicaoTaxaJuros.getFunCodigo() != null && definicaoTaxaJuros.getFunCodigo().compareTo(regraTaxaJurosAntiga.getFunCodigo()) != 0)) {
                log.addChangedField(Columns.DTJ_FUN_CODIGO, definicaoTaxaJuros.getFunCodigo(), regraTaxaJurosAntiga.getFunCodigo());
            }

            if ((definicaoTaxaJuros.getDtjTaxaJuros() != null && regraTaxaJurosAntiga.getDtjTaxaJuros() == null) || definicaoTaxaJuros.getDtjTaxaJuros() != null && definicaoTaxaJuros.getDtjTaxaJuros().compareTo(regraTaxaJurosAntiga.getDtjTaxaJuros()) != 0) {
                log.addChangedField(Columns.DTJ_TAXA_JUROS, definicaoTaxaJuros.getDtjTaxaJuros(), regraTaxaJurosAntiga.getDtjTaxaJuros());
            }
            
            if ((definicaoTaxaJuros.getDtjTaxaJurosMinima() != null && regraTaxaJurosAntiga.getDtjTaxaJurosMinima() == null) || definicaoTaxaJuros.getDtjTaxaJurosMinima() != null && definicaoTaxaJuros.getDtjTaxaJurosMinima().compareTo(regraTaxaJurosAntiga.getDtjTaxaJurosMinima()) != 0) {
                log.addChangedField(Columns.DTJ_TAXA_JUROS_MINIMA, definicaoTaxaJuros.getDtjTaxaJurosMinima(), regraTaxaJurosAntiga.getDtjTaxaJurosMinima());
            }

            if ((definicaoTaxaJuros.getDtjFaixaEtariaIni() != null && regraTaxaJurosAntiga.getDtjFaixaEtariaIni() == null) || definicaoTaxaJuros.getDtjFaixaEtariaIni() != null && definicaoTaxaJuros.getDtjFaixaEtariaIni().compareTo(regraTaxaJurosAntiga.getDtjFaixaEtariaIni()) != 0) {
                log.addChangedField(Columns.DTJ_FAIXA_ETARIA_INI, definicaoTaxaJuros.getDtjFaixaEtariaIni(), regraTaxaJurosAntiga.getDtjFaixaEtariaIni());
            }
            if ((definicaoTaxaJuros.getDtjFaixaEtariaFim() != null && regraTaxaJurosAntiga.getDtjFaixaEtariaFim() == null) || definicaoTaxaJuros.getDtjFaixaEtariaFim() != null && definicaoTaxaJuros.getDtjFaixaEtariaFim().compareTo(regraTaxaJurosAntiga.getDtjFaixaEtariaFim()) != 0) {
                log.addChangedField(Columns.DTJ_FAIXA_ETARIA_FIM, definicaoTaxaJuros.getDtjFaixaEtariaFim(), regraTaxaJurosAntiga.getDtjFaixaEtariaFim());
            }

            if ((definicaoTaxaJuros.getDtjFaixaTempServicoIni() != null && regraTaxaJurosAntiga.getDtjFaixaTempServicoIni() == null) || definicaoTaxaJuros.getDtjFaixaTempServicoIni() != null && definicaoTaxaJuros.getDtjFaixaTempServicoIni().compareTo(regraTaxaJurosAntiga.getDtjFaixaTempServicoIni()) != 0) {
                log.addChangedField(Columns.DTJ_FAIXA_TEMP_SERVICO_INI, definicaoTaxaJuros.getDtjFaixaTempServicoIni(), regraTaxaJurosAntiga.getDtjFaixaTempServicoIni());
            }
            if ((definicaoTaxaJuros.getDtjFaixaTempServicoFim() != null && regraTaxaJurosAntiga.getDtjFaixaTempServicoFim() == null) || definicaoTaxaJuros.getDtjFaixaTempServicoFim() != null && definicaoTaxaJuros.getDtjFaixaTempServicoFim().compareTo(regraTaxaJurosAntiga.getDtjFaixaTempServicoFim()) != 0) {
                log.addChangedField(Columns.DTJ_FAIXA_TEMP_SERVICO_FIM, definicaoTaxaJuros.getDtjFaixaTempServicoFim(), regraTaxaJurosAntiga.getDtjFaixaTempServicoFim());
            }

            if ((definicaoTaxaJuros.getDtjFaixaSalarioIni() != null && regraTaxaJurosAntiga.getDtjFaixaSalarioIni() == null) || definicaoTaxaJuros.getDtjFaixaSalarioIni() != null && definicaoTaxaJuros.getDtjFaixaSalarioIni().compareTo(regraTaxaJurosAntiga.getDtjFaixaSalarioIni()) != 0) {
                log.addChangedField(Columns.DTJ_FAIXA_SALARIO_INI, definicaoTaxaJuros.getDtjFaixaSalarioIni(), regraTaxaJurosAntiga.getDtjFaixaSalarioIni());
            }
            if ((definicaoTaxaJuros.getDtjFaixaSalarioFim() != null && regraTaxaJurosAntiga.getDtjFaixaSalarioFim() == null) || definicaoTaxaJuros.getDtjFaixaSalarioFim() != null && definicaoTaxaJuros.getDtjFaixaSalarioFim().compareTo(regraTaxaJurosAntiga.getDtjFaixaSalarioFim()) != 0) {
                log.addChangedField(Columns.DTJ_FAIXA_SALARIO_FIM, definicaoTaxaJuros.getDtjFaixaSalarioFim(), regraTaxaJurosAntiga.getDtjFaixaSalarioFim());
            }

            if ((definicaoTaxaJuros.getDtjFaixaMargemIni() != null && regraTaxaJurosAntiga.getDtjFaixaMargemIni() == null) || definicaoTaxaJuros.getDtjFaixaMargemIni() != null && definicaoTaxaJuros.getDtjFaixaMargemIni().compareTo(regraTaxaJurosAntiga.getDtjFaixaMargemIni()) != 0) {
                log.addChangedField(Columns.DTJ_FAIXA_MARGEM_INI, definicaoTaxaJuros.getDtjFaixaMargemIni(), regraTaxaJurosAntiga.getDtjFaixaMargemIni());
            }
            if ((definicaoTaxaJuros.getDtjFaixaMargemFim() != null && regraTaxaJurosAntiga.getDtjFaixaMargemFim() == null) || definicaoTaxaJuros.getDtjFaixaMargemFim() != null && definicaoTaxaJuros.getDtjFaixaMargemFim().compareTo(regraTaxaJurosAntiga.getDtjFaixaMargemFim()) != 0) {
                log.addChangedField(Columns.DTJ_FAIXA_MARGEM_FIM, definicaoTaxaJuros.getDtjFaixaMargemFim(), regraTaxaJurosAntiga.getDtjFaixaMargemFim());
            }

            if ((definicaoTaxaJuros.getDtjFaixaValorTotalIni() != null && regraTaxaJurosAntiga.getDtjFaixaValorTotalIni() == null) || definicaoTaxaJuros.getDtjFaixaValorTotalIni() != null && definicaoTaxaJuros.getDtjFaixaValorTotalIni().compareTo(regraTaxaJurosAntiga.getDtjFaixaValorTotalIni()) != 0) {
                log.addChangedField(Columns.DTJ_FAIXA_VALOR_TOTAL_INI, definicaoTaxaJuros.getDtjFaixaValorTotalIni(), regraTaxaJurosAntiga.getDtjFaixaValorTotalIni());
            }
            if ((definicaoTaxaJuros.getDtjFaixaValorTotalFim() != null && regraTaxaJurosAntiga.getDtjFaixaValorTotalFim() == null) || definicaoTaxaJuros.getDtjFaixaValorTotalFim() != null && definicaoTaxaJuros.getDtjFaixaValorTotalFim().compareTo(regraTaxaJurosAntiga.getDtjFaixaValorTotalFim()) != 0) {
                log.addChangedField(Columns.DTJ_FAIXA_VALOR_TOTAL_FIM, definicaoTaxaJuros.getDtjFaixaValorTotalFim(), regraTaxaJurosAntiga.getDtjFaixaValorTotalFim());
            }

            if ((definicaoTaxaJuros.getDtjFaixaValorContratoIni() != null && regraTaxaJurosAntiga.getDtjFaixaValorContratoIni() == null) || definicaoTaxaJuros.getDtjFaixaValorContratoIni() != null && definicaoTaxaJuros.getDtjFaixaValorContratoIni().compareTo(regraTaxaJurosAntiga.getDtjFaixaValorContratoIni()) != 0) {
                log.addChangedField(Columns.DTJ_FAIXA_VALOR_CONTRATO_INI, definicaoTaxaJuros.getDtjFaixaValorContratoIni(), regraTaxaJurosAntiga.getDtjFaixaValorContratoIni());
            }
            if ((definicaoTaxaJuros.getDtjFaixaValorContratoFim() != null && regraTaxaJurosAntiga.getDtjFaixaValorContratoFim() == null) || definicaoTaxaJuros.getDtjFaixaValorContratoFim() != null && definicaoTaxaJuros.getDtjFaixaValorContratoFim().compareTo(regraTaxaJurosAntiga.getDtjFaixaValorContratoFim()) != 0) {
                log.addChangedField(Columns.DTJ_FAIXA_VALOR_CONTRATO_FIM, definicaoTaxaJuros.getDtjFaixaValorContratoFim(), regraTaxaJurosAntiga.getDtjFaixaValorContratoFim());
            }

            if ((definicaoTaxaJuros.getDtjFaixaPrazoIni() != null && regraTaxaJurosAntiga.getDtjFaixaPrazoIni() == null) || definicaoTaxaJuros.getDtjFaixaPrazoIni() != null && definicaoTaxaJuros.getDtjFaixaPrazoIni().compareTo(regraTaxaJurosAntiga.getDtjFaixaPrazoIni()) != 0) {
                log.addChangedField(Columns.DTJ_FAIXA_PRAZO_INI, definicaoTaxaJuros.getDtjFaixaPrazoIni(), regraTaxaJurosAntiga.getDtjFaixaPrazoIni());
            }
            if ((definicaoTaxaJuros.getDtjFaixaPrazoFim() != null && regraTaxaJurosAntiga.getDtjFaixaPrazoFim() == null) || definicaoTaxaJuros.getDtjFaixaPrazoFim() != null && definicaoTaxaJuros.getDtjFaixaPrazoFim().compareTo(regraTaxaJurosAntiga.getDtjFaixaPrazoFim()) != 0) {
                log.addChangedField(Columns.DTJ_FAIXA_PRAZO_FIM, definicaoTaxaJuros.getDtjFaixaPrazoFim(), regraTaxaJurosAntiga.getDtjFaixaPrazoFim());
            }

            DefinicaoTaxaJurosHome.update(definicaoTaxaJuros);
            log.write();
        } catch (Exception ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new DefinicaoTaxaJurosControllerException(ex);
        }
    }

    @Override
    public void excluir(DefinicaoTaxaJuros definicaoTaxaJuros, AcessoSistema responsavel) throws DefinicaoTaxaJurosControllerException {
        try {
            DefinicaoTaxaJurosHome.remove(definicaoTaxaJuros);
            final LogDelegate log = new LogDelegate(responsavel, Log.DEFINICAO_REGRA_TAXA_JUROS, Log.DELETE, Log.LOG_INFORMACAO);
            log.setDefinicaoTaxaJuros(definicaoTaxaJuros.getDtjCodigo());
            log.write();
        } catch (Exception ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new DefinicaoTaxaJurosControllerException(ex);
        }
    }

    @Override
    public DefinicaoTaxaJuros findDefinicaoByCodigo(String dtjCodigo, AcessoSistema responsavel) throws DefinicaoTaxaJurosControllerException {
        try {
            return DefinicaoTaxaJurosHome.findDefinicaoTaxaJurosByCodigo(dtjCodigo);
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DefinicaoTaxaJurosControllerException(ex);
        }
    }

    @Override
    public int lstCountDefinicaoTaxaJuros(TransferObject criterio, AcessoSistema responsavel) throws DefinicaoTaxaJurosControllerException {
        try {
            final PesquisarDefinicaoTaxaJurosQuery query = new PesquisarDefinicaoTaxaJurosQuery();

            if (criterio != null && !criterio.getAtributos().isEmpty()) {
                if (criterio.getAttribute("ORG_CODIGO") != null) {
                    query.orgCodigo = criterio.getAttribute("ORG_CODIGO").toString();
                }
                if (criterio.getAttribute("SVC_CODIGO") != null) {
                    query.svcCodigo = criterio.getAttribute("SVC_CODIGO").toString();
                }
                if (criterio.getAttribute("STATUS_REGRA") != null) {
                    query.statusRegra = criterio.getAttribute("STATUS_REGRA").toString();
                }
                if (criterio.getAttribute("DATA") != null) {
                    query.data = criterio.getAttribute("DATA").toString();
                }
                if (criterio.getAttribute(Columns.DTJ_CONSIGNATARIA) != null) {
                    query.csaCodigo = criterio.getAttribute(Columns.DTJ_CONSIGNATARIA).toString();
                }
            }
            query.count = true;
            return query.executarContador();
        } catch (Exception ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new DefinicaoTaxaJurosControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> listaDefinicaoRegraTaxaJuros(TransferObject criterio, int offset, int size, AcessoSistema responsavel) throws DefinicaoTaxaJurosControllerException {
        try {
            final PesquisarDefinicaoTaxaJurosQuery query = new PesquisarDefinicaoTaxaJurosQuery();

            if (criterio != null && !criterio.getAtributos().isEmpty()) {
                if (criterio.getAttribute("ORG_CODIGO") != null) {
                    query.orgCodigo = criterio.getAttribute("ORG_CODIGO").toString();
                }
                if (criterio.getAttribute("SVC_CODIGO") != null) {
                    query.svcCodigo = criterio.getAttribute("SVC_CODIGO").toString();
                }
                if (criterio.getAttribute("STATUS_REGRA") != null) {
                    query.statusRegra = criterio.getAttribute("STATUS_REGRA").toString();
                }
                if (criterio.getAttribute("DATA") != null) {
                    query.data = criterio.getAttribute("DATA").toString();
                }
                if (criterio.getAttribute(Columns.DTJ_CONSIGNATARIA) != null) {
                    query.csaCodigo = criterio.getAttribute(Columns.DTJ_CONSIGNATARIA).toString();
                }
                if (criterio.getAttribute("pesquisaComDataVigenciaFim") != null) {
                    query.pesquisaComDataVigenciaFim = (boolean) criterio.getAttribute("pesquisaComDataVigenciaFim");
                }
                if (size != 0) {
                    query.firstResult = offset;
                    query.maxResults = size;
                }
            }

            query.count = false;
            return query.executarDTO();
        } catch (HQueryException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new DefinicaoTaxaJurosControllerException(ex);
        }
    }

    @Override
    public void iniciarTabelaVigente(String csaCodigo, AcessoSistema responsavel) throws DefinicaoTaxaJurosControllerException {
        try {
            final List<DefinicaoTaxaJuros> definicaoTaxaJuros = DefinicaoTaxaJurosHome.selectDefinicaoTaxaJurosTabelaAtiva(csaCodigo, responsavel);
            if (definicaoTaxaJuros != null && !definicaoTaxaJuros.isEmpty()) {
                final Date dataCadastro = DateHelper.getSystemDatetime();
                for (DefinicaoTaxaJuros definicaoTaxaJuro : definicaoTaxaJuros) {
                    create(definicaoTaxaJuro.getConsignataria().getCsaCodigo(), definicaoTaxaJuro.getOrgao() != null ? definicaoTaxaJuro.getOrgao().getOrgCodigo() : null,
                            definicaoTaxaJuro.getServico().getSvcCodigo(), definicaoTaxaJuro.getFuncao() != null ? definicaoTaxaJuro.getFuncao().getFunCodigo() : null,
                            definicaoTaxaJuro.getDtjFaixaEtariaIni(), definicaoTaxaJuro.getDtjFaixaEtariaFim(), definicaoTaxaJuro.getDtjFaixaTempServicoIni(), definicaoTaxaJuro.getDtjFaixaTempServicoFim(),
                            definicaoTaxaJuro.getDtjFaixaSalarioIni(), definicaoTaxaJuro.getDtjFaixaSalarioFim(), definicaoTaxaJuro.getDtjFaixaMargemIni(), definicaoTaxaJuro.getDtjFaixaMargemFim(),
                            definicaoTaxaJuro.getDtjFaixaValorTotalIni(), definicaoTaxaJuro.getDtjFaixaValorTotalFim(), definicaoTaxaJuro.getDtjFaixaValorContratoIni(), definicaoTaxaJuro.getDtjFaixaValorContratoFim(),
                            definicaoTaxaJuro.getDtjFaixaPrazoIni(), definicaoTaxaJuro.getDtjFaixaPrazoFim(), definicaoTaxaJuro.getDtjTaxaJuros(), definicaoTaxaJuro.getDtjTaxaJurosMinima(), dataCadastro, responsavel);
                }
            }

            LogDelegate log = new LogDelegate(responsavel, Log.DEFINICAO_REGRA_TAXA_JUROS, Log.CREATE, Log.LOG_INFORMACAO);
            log.setConsignataria(csaCodigo);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.log.regra.taxa.juros.iniciar.tabela", responsavel));
            log.write();
        } catch (Exception ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new DefinicaoTaxaJurosControllerException(ex);
        }
    }

    @Override
    public void ativarTabelaIniciada(String csaCodigo, AcessoSistema responsavel) throws DefinicaoTaxaJurosControllerException {
        try {
            DefinicaoTaxaJurosHome.ativarTabela(csaCodigo, responsavel);

            LogDelegate log = new LogDelegate(responsavel, Log.DEFINICAO_REGRA_TAXA_JUROS, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setConsignataria(csaCodigo);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.log.regra.taxa.juros.ativar.tabela", responsavel));
            log.write();
        } catch (Exception ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new DefinicaoTaxaJurosControllerException(ex);
        }
    }

    @Override
    public void excluirTabelaIniciada(String csaCodigo, AcessoSistema responsavel) throws DefinicaoTaxaJurosControllerException {
        try {
            DefinicaoTaxaJurosHome.deletarTabelaIniciada(csaCodigo);

            LogDelegate log = new LogDelegate(responsavel, Log.DEFINICAO_REGRA_TAXA_JUROS, Log.DELETE, Log.LOG_INFORMACAO);
            log.setConsignataria(csaCodigo);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.log.regra.taxa.juros.excluir.tabela", responsavel));
            log.write();
        } catch (Exception ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new DefinicaoTaxaJurosControllerException("mensagem.erro.remover.tabela.iniciada", responsavel);
        }
    }

    @Override
    public void modificaDataFimVigencia(String cftDataFimVigencia, List<TransferObject> lstRegraJurosSuperior, AcessoSistema responsavel) throws DefinicaoTaxaJurosControllerException {
        try {
            // Altera data fim de vigencia das taxas a vencer
            Date cftDataFimVig = DateHelper.parse(cftDataFimVigencia + " 23:59:59", LocaleHelper.getDateTimePattern());

            for (TransferObject regraJurosSuperior : lstRegraJurosSuperior) {
                DefinicaoTaxaJuros definicaoTaxaJuros = findDefinicaoByCodigo((String) regraJurosSuperior.getAttribute(Columns.DTJ_CODIGO), responsavel);
                definicaoTaxaJuros.setDtjDataVigenciaFim(cftDataFimVig);
                update(definicaoTaxaJuros, responsavel);
            }
        } catch (ParseException | DefinicaoTaxaJurosControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DefinicaoTaxaJurosControllerException("mensagem.erroInternoSistema", responsavel);
        }
    }
}
