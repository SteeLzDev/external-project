package com.zetra.econsig.service.sdp;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PlanoDescontoControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.ParametroPlano;
import com.zetra.econsig.persistence.entity.ParametroPlanoHome;
import com.zetra.econsig.persistence.entity.Plano;
import com.zetra.econsig.persistence.entity.PlanoHome;
import com.zetra.econsig.persistence.query.sdp.plano.BuscaPlanoDescontoQuery;
import com.zetra.econsig.persistence.query.sdp.plano.ListaNaturezaPlanoQuery;
import com.zetra.econsig.persistence.query.sdp.plano.ListaPlanosDescontoQuery;
import com.zetra.econsig.persistence.query.sdp.plano.ListaPlanosDescontoSemRateioQuery;
import com.zetra.econsig.persistence.query.sdp.plano.ListaPlanosDescontoTaxaUsoQuery;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: PlanoDescontoControllerBean</p>
 * <p>Description: Session bean de manutenção de planos de desconto.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class PlanoDescontoControllerBean implements PlanoDescontoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(PlanoDescontoControllerBean.class);

    @Autowired
    private ParametroController parametroController;

    @Override
    public TransferObject buscaPlanoDesconto(TransferObject criterio, AcessoSistema responsavel) throws PlanoDescontoControllerException {
        try {
            BuscaPlanoDescontoQuery query = new BuscaPlanoDescontoQuery();

            if (criterio != null) {
                String plaCodigo = (String) criterio.getAttribute(Columns.PLA_CODIGO);
                if (!TextHelper.isNull(plaCodigo)) {
                    query.plaCodigo = plaCodigo;
                }

                String csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
                if (!TextHelper.isNull(csaCodigo)) {
                    query.csaCodigo = csaCodigo;
                }

                String svcCodigo = (String) criterio.getAttribute(Columns.SVC_CODIGO);
                if (!TextHelper.isNull(svcCodigo)) {
                    query.svcCodigo = svcCodigo;
                }

                String plaIdentificador = (String) criterio.getAttribute(Columns.PLA_IDENTIFICADOR);
                if (!TextHelper.isNull(plaIdentificador)) {
                    query.plaIdentificador = plaIdentificador;
                }

                String plaDescricao = (String) criterio.getAttribute(Columns.PLA_DESCRICAO);
                if (!TextHelper.isNull(plaDescricao)) {
                    query.plaDescricao = plaDescricao;
                }

                Short plaAtivo = (Short) criterio.getAttribute(Columns.PLA_ATIVO);
                if (plaAtivo != null) {
                    query.plaAtivo = plaAtivo;
                }
            }

            List<TransferObject> plano = query.executarDTO();

            if (plano == null || plano.isEmpty()) {
                throw new PlanoDescontoControllerException("mensagem.erro.plano.nao.encontrado", responsavel);
            }

            return plano.get(0);
        } catch (HQueryException ex) {
            throw new PlanoDescontoControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> lstPlanoDesconto(TransferObject criterios, AcessoSistema responsavel) throws PlanoDescontoControllerException {
        return lstPlanoDesconto(criterios, -1, -1, responsavel);
    }

    @Override
    public List<TransferObject> lstPlanoDesconto(TransferObject criterios, int offset, int count, AcessoSistema responsavel) throws PlanoDescontoControllerException {
        ListaPlanosDescontoQuery lstPlanos = new ListaPlanosDescontoQuery();

        if (offset != -1) {
            lstPlanos.firstResult = offset;
        }

        if (count != -1) {
            lstPlanos.maxResults = count;
        }

        // recupera o csaCodigo quando for usuário de consignatária
        if (responsavel.isCsa()) {
        	if (criterios == null) {
        		criterios = new CustomTransferObject();
        	}
        	criterios.setAttribute(Columns.CSA_CODIGO, responsavel.getCsaCodigo());
        }

        if (criterios != null) {
            String csaCodigo = (String) criterios.getAttribute(Columns.CSA_CODIGO);
            if (!TextHelper.isNull(csaCodigo)) {
                lstPlanos.csaCodigo = csaCodigo;
            }

            String svcCodigo = (String) criterios.getAttribute(Columns.SVC_CODIGO);
            if (!TextHelper.isNull(svcCodigo)) {
                lstPlanos.svcCodigo = svcCodigo;
            }

            String plaIdentificador = (String) criterios.getAttribute(Columns.PLA_IDENTIFICADOR);
            if (!TextHelper.isNull(plaIdentificador)) {
                lstPlanos.plaIdentificador = plaIdentificador;
            }

            String plaDescricao = (String) criterios.getAttribute(Columns.PLA_DESCRICAO);
            if (!TextHelper.isNull(plaDescricao)) {
                lstPlanos.plaDescricao = plaDescricao;
            }

            Short plaAtivo = (Short) criterios.getAttribute(Columns.PLA_ATIVO);
            if (plaAtivo != null) {
                lstPlanos.plaAtivo = plaAtivo;
            }

            Boolean taxaUso = (Boolean) criterios.getAttribute(Columns.NPL_CODIGO);
            if (taxaUso != null) {
                lstPlanos.taxaUso = taxaUso.booleanValue();
            }
        }

        try {
            return lstPlanos.executarDTO();
        } catch (HQueryException ex) {
            throw new PlanoDescontoControllerException(ex);
        }
    }

    @Override
    public int countPlanosDesconto(TransferObject criterio, AcessoSistema responsavel) throws PlanoDescontoControllerException {
        ListaPlanosDescontoQuery query = new ListaPlanosDescontoQuery();
        query.count = true;

        // recupera o csaCodigo quando for usuário de consignatária
        if (responsavel.isCsa()) {
        	if (criterio == null) {
        		criterio = new CustomTransferObject();
        	}
        	criterio.setAttribute(Columns.CSA_CODIGO, responsavel.getCsaCodigo());
        }

        if (criterio != null) {
            String csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
            if (!TextHelper.isNull(csaCodigo)) {
                query.csaCodigo = csaCodigo;
            }

            String svcCodigo = (String) criterio.getAttribute(Columns.SVC_CODIGO);
            if (!TextHelper.isNull(svcCodigo)) {
                query.svcCodigo = svcCodigo;
            }

            String plaIdentificador = (String) criterio.getAttribute(Columns.PLA_IDENTIFICADOR);
            if (!TextHelper.isNull(plaIdentificador)) {
                query.plaIdentificador = plaIdentificador;
            }

            String plaDescricao = (String) criterio.getAttribute(Columns.PLA_DESCRICAO);
            if (!TextHelper.isNull(plaDescricao)) {
                query.plaDescricao = plaDescricao;
            }

            Short plaAtivo = (Short) criterio.getAttribute(Columns.PLA_ATIVO);
            if (plaAtivo != null) {
                query.plaAtivo = plaAtivo;
            }
        }

        try {
            return query.executarContador();
        } catch (HQueryException ex) {
            throw new PlanoDescontoControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> lstPlanoDescontoSemRateio(TransferObject criterios, int offset, int count, AcessoSistema responsavel) throws PlanoDescontoControllerException {
        try {
            ListaPlanosDescontoSemRateioQuery lstPlanos = new ListaPlanosDescontoSemRateioQuery();

            if (offset != -1) {
                lstPlanos.firstResult = offset;
            }

            if (count != -1) {
                lstPlanos.maxResults = count;
            }

            if (criterios != null) {
                String csaCodigo = (String) criterios.getAttribute(Columns.CSA_CODIGO);
                if (!TextHelper.isNull(csaCodigo)) {
                    lstPlanos.csaCodigo = csaCodigo;
                }

                String svcCodigo = (String) criterios.getAttribute(Columns.SVC_CODIGO);
                if (!TextHelper.isNull(svcCodigo)) {
                    lstPlanos.svcCodigo = svcCodigo;
                }

                String plaCodigo = (String) criterios.getAttribute(Columns.PLA_CODIGO);
                if (!TextHelper.isNull(plaCodigo)) {
                    lstPlanos.plaCodigo = plaCodigo;
                }

                String plaIdentificador = (String) criterios.getAttribute(Columns.PLA_IDENTIFICADOR);
                if (!TextHelper.isNull(plaIdentificador)) {
                    lstPlanos.plaIdentificador = plaIdentificador;
                }

                String plaDescricao = (String) criterios.getAttribute(Columns.PLA_DESCRICAO);
                if (!TextHelper.isNull(plaDescricao)) {
                    lstPlanos.plaDescricao = plaDescricao;
                }

                Short plaAtivo = (Short) criterios.getAttribute(Columns.PLA_ATIVO);
                if (plaAtivo != null) {
                    lstPlanos.plaAtivo = plaAtivo;
                }

                Boolean taxaUso = (Boolean) criterios.getAttribute(Columns.NPL_CODIGO);
                if (taxaUso != null) {
                    lstPlanos.taxaUso = taxaUso.booleanValue();
                }

            }

            return lstPlanos.executarDTO();
        } catch (HQueryException ex) {
            throw new PlanoDescontoControllerException(ex);
        }
    }

    @Override
    public String createPlanoDesconto(TransferObject planoDesconto, AcessoSistema responsavel) throws PlanoDescontoControllerException {
        return createPlanoDesconto(planoDesconto, null, responsavel);
    }

    @Override
    public String createPlanoDesconto(TransferObject planoDesconto, List<TransferObject> lstParamPlano, AcessoSistema responsavel) throws PlanoDescontoControllerException {
        String plaCodigo = null;
        try {
            Plano planoBean = null;
            try {
                // Verifica se já existe plano de descnto com mesmo identificador
                planoBean = PlanoHome.findByIdn((String) planoDesconto.getAttribute(Columns.PLA_IDENTIFICADOR));
            } catch (FindException ex) {
                LOG.error(ex.getMessage(), ex);
            }
            if (planoBean != null) {
                throw new PlanoDescontoControllerException("mensagem.erro.incluir.plano.duplicado", responsavel);
            }

            if (TextHelper.isNull(planoDesconto.getAttribute(Columns.NPL_CODIGO))) {
                throw new PlanoDescontoControllerException("mensagem.erro.incluir.plano.natureza.nao.informada", responsavel);
            }

            if (TextHelper.isNull(planoDesconto.getAttribute(Columns.SVC_CODIGO))) {
                throw new PlanoDescontoControllerException("mensagem.erro.incluir.plano.servico.nao.informado", responsavel);
            }

            if (TextHelper.isNull(planoDesconto.getAttribute(Columns.CSA_CODIGO))) {
                throw new PlanoDescontoControllerException("mensagem.erro.incluir.plano.consignataria.nao.informada", responsavel);
            }

            Plano novoPlanoBean = PlanoHome.create((String) planoDesconto.getAttribute(Columns.SVC_CODIGO), (String) planoDesconto.getAttribute(Columns.CSA_CODIGO), (String) planoDesconto.getAttribute(Columns.NPL_CODIGO),
                                                   (String) planoDesconto.getAttribute(Columns.PLA_IDENTIFICADOR), (String) planoDesconto.getAttribute(Columns.PLA_DESCRICAO), (Short) planoDesconto.getAttribute(Columns.PLA_ATIVO));

            plaCodigo = novoPlanoBean.getPlaCodigo();

            String svcCodigo = (String) planoDesconto.getAttribute(Columns.SVC_CODIGO);

            try {
                atualizaParametrosPlano(plaCodigo, svcCodigo, lstParamPlano, responsavel);
            } catch (UpdateException ex) {
                TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                LOG.error(ex.getMessage(), ex);
                throw new PlanoDescontoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            } catch (PlanoDescontoControllerException pex) {
                TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                LOG.error(pex.getMessage(), pex);
                throw pex;
            }

            LogDelegate log = new LogDelegate(responsavel, Log.PLANO_DESCONTO, Log.CREATE, Log.LOG_INFORMACAO);
            log.setPlano(plaCodigo);
            log.getUpdatedFields(planoDesconto.getAtributos(), null);
            log.write();
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new PlanoDescontoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (com.zetra.econsig.exception.CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new PlanoDescontoControllerException("mensagem.erro.incluir.plano.arg0", responsavel, ex, ex.getMessage());
        }
        return plaCodigo;
    }

    @Override
    public void updatePlanoDesconto(TransferObject planoDesconto, AcessoSistema responsavel) throws PlanoDescontoControllerException {
        updatePlanoDesconto(planoDesconto, null, responsavel);
    }

    @Override
    public void updatePlanoDesconto(TransferObject planoDesconto, List<TransferObject> lstParamPlano, AcessoSistema responsavel) throws PlanoDescontoControllerException {
        try {
            Plano planoBean = findPlanoBean(planoDesconto, responsavel);
            String plaCodigo = planoBean.getPlaCodigo();
            LogDelegate log = new LogDelegate(responsavel, Log.PLANO_DESCONTO, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setPlano(plaCodigo);

            // Compara a versão do cache com a passada por parâmetro
            TransferObject planoDescontoCache = setPlanoDescontoValues(planoBean);
            CustomTransferObject merge = log.getUpdatedFields(planoDesconto.getAtributos(), planoDescontoCache.getAtributos());

            if (merge.getAtributos().containsKey(Columns.PLA_IDENTIFICADOR)) {

                // Verifica se não existe outro plano com o mesmo ID
                TransferObject teste = new ConsignatariaTransferObject();
                teste.setAttribute(Columns.PLA_IDENTIFICADOR, merge.getAttribute(Columns.PLA_IDENTIFICADOR));

                boolean existe = false;
                try {
                    findPlanoBean(teste, responsavel);
                    existe = true;
                } catch (PlanoDescontoControllerException ex) {}
                if (existe) {
                    throw new PlanoDescontoControllerException("mensagem.erro.alterar.plano.duplicado", responsavel);
                }
                planoBean.setPlaIdentificador((String) merge.getAttribute(Columns.PLA_IDENTIFICADOR));
            }

            if (merge.getAtributos().containsKey(Columns.PLA_ATIVO)) {
                Short plaAtivo = (Short) merge.getAttribute(Columns.PLA_ATIVO);
                planoBean.setPlaAtivo(plaAtivo);
            }

            if (merge.getAtributos().containsKey(Columns.PLA_DESCRICAO)) {
                planoBean.setPlaDescricao((String) merge.getAttribute(Columns.PLA_DESCRICAO));
            }

            PlanoHome.update(planoBean);
            String svcCodigo = (String) planoDesconto.getAttribute(Columns.SVC_CODIGO);

            if (lstParamPlano != null) {
                atualizaParametrosPlano(plaCodigo, svcCodigo, lstParamPlano, responsavel);
            }

            log.write();
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new PlanoDescontoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (PlanoDescontoControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw ex;
        } catch (UpdateException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new PlanoDescontoControllerException(ex);
        }
    }

    @Override
    public void removePlanoDesconto(TransferObject planoDesconto, AcessoSistema responsavel) throws PlanoDescontoControllerException {
        try {
            Plano planoBean = findPlanoBean(planoDesconto, responsavel);
            String plaCodigo = planoBean.getPlaCodigo();

            //remove os parâmetros ligados a este plano
            try {
                List<ParametroPlano> paramList = ParametroPlanoHome.findByPlano(plaCodigo);

                if (paramList != null && !paramList.isEmpty()) {
                    for (ParametroPlano param: paramList) {
                        ParametroPlanoHome.remove(param);
                    }
                }
            } catch (FindException ex) {
                LOG.info("Nenhum parâmetro de plano existente", ex);
            }

            String csaCodigo = plaCodigo;
            PlanoHome.remove(planoBean);

            LogDelegate logDelegate = new LogDelegate(responsavel, Log.PLANO_DESCONTO, Log.DELETE, Log.LOG_INFORMACAO);
            logDelegate.setPlano(plaCodigo);
            logDelegate.setConsignataria(csaCodigo);
            logDelegate.write();
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new PlanoDescontoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (com.zetra.econsig.exception.RemoveException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new PlanoDescontoControllerException("mensagem.erro.excluir.plano.dependentes", responsavel);
        }
    }

    @Override
    public TransferObject findPlanoDesconto(TransferObject planoDesconto, AcessoSistema responsavel) throws PlanoDescontoControllerException {
        return setPlanoDescontoValues(findPlanoBean(planoDesconto, responsavel));
    }

    @Override
    public List<TransferObject> lstPlanoDescontoTaxaUso(String csaCodigo, String svcCodigo, String rseCodigo, AcessoSistema responsavel) throws PlanoDescontoControllerException {
        try {
            ListaPlanosDescontoTaxaUsoQuery query = new ListaPlanosDescontoTaxaUsoQuery();
            query.csaCodigo = csaCodigo;
            query.svcCodigo = svcCodigo;
            query.rseCodigo = rseCodigo;

            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new PlanoDescontoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstNaturezasPlanos() throws PlanoDescontoControllerException {
        try {
            ListaNaturezaPlanoQuery query = new ListaNaturezaPlanoQuery();
            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new PlanoDescontoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    private TransferObject setPlanoDescontoValues(Plano planoBean) {
        TransferObject planoTO = new CustomTransferObject();

        planoTO.setAttribute(Columns.PLA_CODIGO, planoBean.getPlaCodigo());
        planoTO.setAttribute(Columns.PLA_DESCRICAO, planoBean.getPlaDescricao());
        planoTO.setAttribute(Columns.PLA_IDENTIFICADOR, planoBean.getPlaIdentificador());
        planoTO.setAttribute(Columns.PLA_ATIVO, planoBean.getPlaAtivo());

        return planoTO;
    }

    private Plano findPlanoBean(TransferObject plano, AcessoSistema responsavel) throws PlanoDescontoControllerException {
        Plano planoBean = null;
        try {
            if (plano.getAttribute(Columns.PLA_CODIGO) != null) {
                planoBean = PlanoHome.findByPrimaryKey((String) plano.getAttribute(Columns.PLA_CODIGO));
            } else if (plano.getAttribute(Columns.PLA_IDENTIFICADOR) != null) {
                planoBean = PlanoHome.findByIdn((String) plano.getAttribute(Columns.PLA_IDENTIFICADOR));
            } else {
                throw new PlanoDescontoControllerException("mensagem.erro.plano.nao.encontrado", responsavel);
            }
        } catch (FindException e) {
            throw new PlanoDescontoControllerException("mensagem.erro.plano.nao.encontrado", responsavel, e);
        }
        return planoBean;
    }

    private void validaParamSvc(Map<String, String> tppMap, String svcCodigo, AcessoSistema responsavel) throws ParametroControllerException, PlanoDescontoControllerException {
        ParamSvcTO paramSvc = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

        Integer prazoMaxSvc = !TextHelper.isNull(paramSvc.getTpsMaxPrazo()) ? Integer.parseInt(paramSvc.getTpsMaxPrazo()) : null;
        Integer prazoMaxPla = !TextHelper.isNull(tppMap.get(CodedValues.TPP_PRAZO_MAX_PLANO)) ? Integer.parseInt(tppMap.get(CodedValues.TPP_PRAZO_MAX_PLANO)) : null;
        Float vlrPlano = null;
        try {
            vlrPlano = !TextHelper.isNull(tppMap.get(CodedValues.TPP_VLR_PLANO)) ? Float.parseFloat(NumberHelper.reformat(tppMap.get(CodedValues.TPP_VLR_PLANO), NumberHelper.getLang(), "en")) : null;
        } catch (NumberFormatException | ParseException ex) {
            throw new PlanoDescontoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

        if (paramSvc.isTpsPrazoFixo() && prazoMaxSvc != null && prazoMaxPla != null) {
            if (prazoMaxSvc != prazoMaxPla) {
                throw new PlanoDescontoControllerException("mensagem.erro.plano.prazo.deve.ser.igual.arg0", responsavel, String.valueOf(prazoMaxSvc));
            }
        }

        if (prazoMaxSvc != null && prazoMaxSvc.intValue() == 0 && prazoMaxPla != null && prazoMaxPla != 0) {
            throw new PlanoDescontoControllerException("mensagem.erro.plano.prazo.deve.ser.indeterminado", responsavel);
        }

        if (prazoMaxSvc != null && prazoMaxSvc.intValue() != 0 && prazoMaxPla != null) {
            if (prazoMaxPla != 0) {
                if (prazoMaxPla.intValue() > prazoMaxSvc.intValue()) {
                    throw new PlanoDescontoControllerException("mensagem.erro.plano.prazo.deve.ser.menor.arg0", responsavel, String.valueOf(prazoMaxSvc));
                }
            } else  {
                throw new PlanoDescontoControllerException("mensagem.erro.plano.prazo.deve.ser.determinado.limitado.arg0", responsavel, String.valueOf(prazoMaxSvc));
            }
        }

        if (prazoMaxSvc != null && prazoMaxPla == null) {
            if (prazoMaxSvc.intValue() != 0) {
                if (!paramSvc.isTpsPrazoFixo()) {
                    throw new PlanoDescontoControllerException("mensagem.erro.plano.prazo.deve.ser.limitado.arg0", responsavel, String.valueOf(prazoMaxSvc));
                } else {
                    throw new PlanoDescontoControllerException("mensagem.erro.plano.prazo.deve.ser.igual.svc.arg0", responsavel, String.valueOf(prazoMaxSvc));
                }
            } else {
                throw new PlanoDescontoControllerException("mensagem.erro.plano.prazo.deve.ser.indeterminado", responsavel);
            }
        }

        if (vlrPlano != null) {
            Float vlrMinAdeSvc = !TextHelper.isNull(paramSvc.getTpsVlrMinimoContrato()) ? Float.parseFloat(paramSvc.getTpsVlrMinimoContrato()) : null;
            Float vlrMaxAdeSvc = !TextHelper.isNull(paramSvc.getTpsVlrMaximoContrato())  ? Float.parseFloat(paramSvc.getTpsVlrMaximoContrato()) : null;
            boolean vlrAlteraSvc = paramSvc.isTpsAlteraAdeVlr();

            if (vlrMinAdeSvc != null && vlrMinAdeSvc.compareTo(vlrPlano) > 0) {
                try {
                    throw new PlanoDescontoControllerException("mensagem.erro.plano.valor.deve.ser.maior.arg0", responsavel, NumberHelper.reformat(vlrMinAdeSvc.toString(), "en", NumberHelper.getLang()));
                } catch (ParseException ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw new PlanoDescontoControllerException("mensagem.erroInternoSistema", responsavel, ex);
                }
            }

            if (vlrMaxAdeSvc != null && vlrMaxAdeSvc.compareTo(vlrPlano) < 0) {
                try {
                    throw new PlanoDescontoControllerException("mensagem.erro.plano.valor.deve.ser.menor.arg0", responsavel, NumberHelper.reformat(vlrMaxAdeSvc.toString(), "en", NumberHelper.getLang()));
                } catch (ParseException ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw new PlanoDescontoControllerException("mensagem.erroInternoSistema", responsavel, ex);
                }
            }

            if (!vlrAlteraSvc) {
                Float adeVlr = (!TextHelper.isNull(paramSvc.getTpsAdeVlr())) ? Float.parseFloat(paramSvc.getTpsAdeVlr()) : null;

                if (adeVlr != null && vlrPlano.compareTo(adeVlr) != 0) {
                    try {
                        throw new PlanoDescontoControllerException("mensagem.erro.plano.valor.deve.ser.igual.arg0", responsavel, NumberHelper.reformat(adeVlr.toString(), "en", NumberHelper.getLang()));
                    } catch (ParseException ex) {
                        LOG.error(ex.getMessage(), ex);
                        throw new PlanoDescontoControllerException("mensagem.erroInternoSistema", responsavel, ex);
                    }
                }
            }
        }
    }

    private void atualizaParametrosPlano(String plaCodigo, String svcCodigo, List<TransferObject> lstParamPlano, AcessoSistema responsavel) throws PlanoDescontoControllerException, UpdateException, LogControllerException {
        Map<String, String> paramMap = new HashMap<>();
        for (TransferObject paramPlano: lstParamPlano) {
            String tppCodigo = (String) paramPlano.getAttribute(Columns.TPP_CODIGO);
            String pplVlr = (String) paramPlano.getAttribute(Columns.PPL_VALOR);
            paramMap.put(tppCodigo, pplVlr);
        }

        try {
            validaParamSvc(paramMap, svcCodigo, responsavel);
        } catch (ParametroControllerException ex) {
            throw new PlanoDescontoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

        for (TransferObject paramPlano: lstParamPlano) {
            String tppCodigo = (String) paramPlano.getAttribute(Columns.TPP_CODIGO);
            String pplValor = null;
            try {
                pplValor = tppCodigo.equals(CodedValues.TPP_VLR_PLANO) && !TextHelper.isNull(paramPlano.getAttribute(Columns.PPL_VALOR)) ? NumberHelper.reformat((String) paramPlano.getAttribute(Columns.PPL_VALOR), NumberHelper.getLang(), "en") : (String) paramPlano.getAttribute(Columns.PPL_VALOR);
            } catch (ParseException ex) {
                throw new PlanoDescontoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }

            try {
                ParametroPlano paramBean = ParametroPlanoHome.findByPrimaryKey(tppCodigo, plaCodigo);

                if (!paramPlano.getAttribute(Columns.PPL_VALOR).equals(paramBean.getPplValor())) {
                    String pplValorOld = paramBean.getPplValor();
                    paramBean.setPplValor(pplValor);
                    ParametroPlanoHome.update(paramBean);

                    LogDelegate logParam = new LogDelegate(responsavel, Log.PLANO_DESCONTO, Log.UPDATE, Log.LOG_INFORMACAO);
                    logParam.setPlano(plaCodigo);
                    logParam.setParametroPlano(tppCodigo);
                    logParam.addChangedField(Columns.PPL_VALOR, pplValor, pplValorOld);
                    logParam.write();
                }
            } catch (FindException ex) {
                try {
                    ParametroPlanoHome.create(tppCodigo, plaCodigo, pplValor);

                    LogDelegate logParam = new LogDelegate(responsavel, Log.PLANO_DESCONTO, Log.CREATE, Log.LOG_INFORMACAO);
                    logParam.setPlano(plaCodigo);
                    logParam.setParametroPlano(tppCodigo);
                    logParam.add(Columns.PPL_VALOR, pplValor);
                    logParam.write();
                } catch (CreateException ex1) {
                    TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                    throw new PlanoDescontoControllerException("mensagem.erroInternoSistema", responsavel, ex1);
                }
            }
        }
    }
}
