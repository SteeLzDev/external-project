package com.zetra.econsig.service.sdp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.parametros.ReservarMargemParametros;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.DespesaComumControllerException;
import com.zetra.econsig.exception.DespesaIndividualControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PermissionarioControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.DespesaComum;
import com.zetra.econsig.persistence.entity.DespesaComumHome;
import com.zetra.econsig.persistence.entity.EnderecoConjHabitacional;
import com.zetra.econsig.persistence.entity.EnderecoConjuntoHabitacionalHome;
import com.zetra.econsig.persistence.entity.OcorrenciaDespesaComum;
import com.zetra.econsig.persistence.entity.OcorrenciaDespesaComumHome;
import com.zetra.econsig.persistence.entity.Plano;
import com.zetra.econsig.persistence.entity.PlanoHome;
import com.zetra.econsig.persistence.entity.PostoRegistroServidorHome;
import com.zetra.econsig.persistence.entity.StatusDespesaComum;
import com.zetra.econsig.persistence.query.historico.HistoricoDespesaComumOcorrenciaQuery;
import com.zetra.econsig.persistence.query.sdp.despesacomum.ListaDespesaComumQuery;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusDespesaComumEnum;

/**
 * <p>Title: DespesaComumControllerBean</p>
 * <p>Description: Session Bean para a operações relacionada a Despesa Comum.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class DespesaComumControllerBean implements DespesaComumController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DespesaComumControllerBean.class);

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private DespesaIndividualController despesaIndividualController;

    @Autowired
    private PermissionarioController permissionarioController;

    @Autowired
    private ParametroController parametroController;

    protected String criaOcorrencia(String decCodigo, String tocCodigo, String odcObs, AcessoSistema responsavel) throws DespesaComumControllerException {
        try {
            OcorrenciaDespesaComum ocorrencia = OcorrenciaDespesaComumHome.create(decCodigo, tocCodigo, responsavel.getUsuCodigo(), responsavel.getIpUsuario(), odcObs);
            return ocorrencia.getOdcCodigo();
        } catch (CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new DespesaComumControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public String createDespesaComum(TransferObject despesaComum, String carencia, AcessoSistema responsavel) throws DespesaComumControllerException {
        try {
            String echCodigo = (String) despesaComum.getAttribute(Columns.DEC_ECH_CODIGO);
            String plaCodigo = (String) despesaComum.getAttribute(Columns.DEC_PLA_CODIGO);
            String posCodigo = (String) despesaComum.getAttribute(Columns.DEC_POS_CODIGO);
            String sdcCodigo = StatusDespesaComumEnum.ATIVO.getCodigo();
            Date decData = (Date) despesaComum.getAttribute(Columns.DEC_DATA);
            BigDecimal decValor = (BigDecimal) despesaComum.getAttribute(Columns.DEC_VALOR);
            Integer decPrazo = (Integer) despesaComum.getAttribute(Columns.DEC_PRAZO);
            String decIdentificador = !TextHelper.isNull(despesaComum.getAttribute(Columns.DEC_IDENTIFICADOR)) ? despesaComum.getAttribute(Columns.DEC_IDENTIFICADOR).toString() : "";

            // Verifica se o permissionário está ativo
            EnderecoConjHabitacional endereco = EnderecoConjuntoHabitacionalHome.findByPrimaryKey(echCodigo);
            if (!endereco.getConsignataria().getCsaCodigo().equals(responsavel.getCsaCodigo())) {
                throw new DespesaComumControllerException("mensagem.erro.despesa.comum.outra.csa", responsavel);
            }

            // Verifica se o plano está ativo
            Plano plano = PlanoHome.findByPrimaryKey(plaCodigo);
            if (!plano.getPlaAtivo().equals(CodedValues.STS_ATIVO)) {
                throw new DespesaComumControllerException("mensagem.erro.incluir.despesa.comum.plano.bloqueado", responsavel);
            }

            // Seleciona os parâmetros do plano
            Map<String, String> parametrosPlano = parametroController.getParamPlano(plaCodigo, responsavel);

            boolean descontoPosto = parametrosPlano.containsKey(CodedValues.TPP_DESCONTO_POR_POSTO) && parametrosPlano.get(CodedValues.TPP_DESCONTO_POR_POSTO).equals(CodedValues.PLANO_DESCONTO_POR_POSTO_SIM);

            if (descontoPosto) {
                if (TextHelper.isNull(posCodigo)) {
                    throw new DespesaComumControllerException("mensagem.informe.posto.despesa.comum", responsavel);
                }

                try {
                    PostoRegistroServidorHome.findByPrimaryKey(posCodigo);
                } catch (FindException ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw new DespesaComumControllerException("mensagem.erro.posto.nao.encontrado.despesa.comum", responsavel);
                }
            }

            // Exibe os permissionários ligados ao endereço onde será incluída uma despesa individual para cada
            TransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.ECH_CODIGO, echCodigo);
            if (!TextHelper.isNull(decData)) {
                criterio.setAttribute(Columns.DEC_DATA, decData);
            }
            if (!TextHelper.isNull(posCodigo)) {
                criterio.setAttribute(Columns.POS_CODIGO, posCodigo);
            }
            List<TransferObject> permissionarios = null;
            try {
                permissionarios = permissionarioController.lstPermissionarios(criterio, -1, -1, responsavel);
            } catch (PermissionarioControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }

            if (permissionarios == null || permissionarios.isEmpty()) {
                throw new DespesaComumControllerException("mensagem.erro.despesa.comum.permissionarios.inexistentes", responsavel);
            }

            // Seleciona parâmetros do serviço
            String svcCodigo = plano.getServico().getSvcCodigo();
            ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

            // Cadastro de indices
            String indicePadrao = parametrosPlano.containsKey(CodedValues.TPP_INDICE_PLANO) && !TextHelper.isNull(parametrosPlano.get(CodedValues.TPP_INDICE_PLANO)) ? parametrosPlano.get(CodedValues.TPP_INDICE_PLANO).toString() : "";

            // Calcula valor rateio
            BigDecimal decValorRateio = null;
            int qtdePermissionarios = permissionarios.size();
            int qtdeUnidadesEndereco = endereco.getEchQtdUnidades();
            if (!parametrosPlano.containsKey(CodedValues.TPP_TIPO_RATEIO_PLANO) || parametrosPlano.get(CodedValues.TPP_TIPO_RATEIO_PLANO).equals(CodedValues.PLANO_SEM_RATEIO)) {
                decValorRateio = decValor;
            } else if (parametrosPlano.get(CodedValues.TPP_TIPO_RATEIO_PLANO).equals(CodedValues.PLANO_RATEIO_POR_PERMISSIONARIO)) {
                decValorRateio = decValor.divide(new BigDecimal(qtdePermissionarios), 2, java.math.RoundingMode.DOWN);
            } else if (parametrosPlano.get(CodedValues.TPP_TIPO_RATEIO_PLANO).equals(CodedValues.PLANO_RATEIO_POR_UNIDADE)) {
                decValorRateio = qtdeUnidadesEndereco > 0 ? decValor.divide(new BigDecimal(qtdeUnidadesEndereco), 2, java.math.RoundingMode.DOWN) : decValor;
            }

            // Setar dados consignação
            Integer adeCarencia = Integer.valueOf(carencia);
            Integer adePrazo = decPrazo;
            BigDecimal adeVlr = decValorRateio;
            // TODO Índice não será setado a princípio na tela de lançamento de despesa comum, por isso está sendo setado com o índice padrão
            String adeIndice = indicePadrao;

            // Valida parâmetros do plano desconto
            parametroController.validaParametrosPlanoDesconto(plaCodigo, svcCodigo, adeVlr, adePrazo, adeIndice, responsavel);

            // Cria a despesa comum
            DespesaComum bean = DespesaComumHome.create(echCodigo, plaCodigo, posCodigo, sdcCodigo, decValor, decValorRateio, decPrazo, DateHelper.getSystemDatetime(), null, decIdentificador);
            String decCodigo = bean.getDecCodigo();

            // Cria as despesas individuais ligadas à despesa comum
            TransferObject despesaIndividual = new CustomTransferObject();
            despesaIndividual.setAttribute(Columns.DEI_DEC_CODIGO, decCodigo);
            despesaIndividual.setAttribute(Columns.DEI_PLA_CODIGO, plaCodigo);
            despesaIndividual.setAttribute("decDataRetroativa", decData);

            ReservarMargemParametros margemParam = new ReservarMargemParametros();
            margemParam.setAdePrazo(adePrazo);
            margemParam.setAdeCarencia(adeCarencia);
            margemParam.setComSerSenha(Boolean.FALSE);
            margemParam.setAdeTipoVlr(paramSvcCse.getTpsTipoVlr());
            margemParam.setAdeIntFolha(paramSvcCse.getTpsIntegraFolha());
            margemParam.setAdeIncMargem(paramSvcCse.getTpsIncideMargem());
            margemParam.setAdeIndice(adeIndice);
            margemParam.setAdeIdentificador(decIdentificador);
            margemParam.setValidar(Boolean.FALSE);
            margemParam.setPermitirValidacaoTaxa(Boolean.TRUE);
            margemParam.setSerAtivo(Boolean.TRUE);
            margemParam.setCnvAtivo(Boolean.TRUE);
            margemParam.setSerCnvAtivo(Boolean.TRUE);
            margemParam.setSvcAtivo(Boolean.TRUE);
            margemParam.setCsaAtivo(Boolean.TRUE);
            margemParam.setOrgAtivo(Boolean.TRUE);
            margemParam.setEstAtivo(Boolean.TRUE);
            margemParam.setCseAtivo(Boolean.TRUE);
            margemParam.setAcao("RESERVAR");
            margemParam.setNomeResponsavel(responsavel.getUsuNome());

            // Inclui despesas individuais
            String adeCodigo = null;
            for (TransferObject permissionario : permissionarios) {
                String prmCodigo = permissionario.getAttribute(Columns.PRM_CODIGO).toString();
                String rseCodigo = permissionario.getAttribute(Columns.RSE_CODIGO).toString();
                String csaCodigo = responsavel.getCsaCodigo();
                String orgCodigo = permissionario.getAttribute(Columns.ORG_CODIGO).toString();

                despesaIndividual.setAttribute(Columns.DEI_PRM_CODIGO, prmCodigo);

                margemParam.setRseCodigo(rseCodigo);
                margemParam.setAdeVlr(adeVlr);

                try {
                    CustomTransferObject convenio = convenioController.getParamCnv(csaCodigo, orgCodigo, svcCodigo, true, true, responsavel);
                    margemParam.setCnvCodigo(convenio.getAttribute(Columns.CNV_CODIGO).toString());
                } catch (ConvenioControllerException ex) {
                    TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                    LOG.error(ex.getMessage(), ex);
                    throw new DespesaComumControllerException(ex);
                }

                adeCodigo = despesaIndividualController.createDespesaIndividual(despesaIndividual, margemParam, responsavel);
            }

            // Atualiza a data de início e fim da despesa comum com a data da última despesa comum criada
            AutDesconto autorizacao = AutDescontoHome.findByPrimaryKey(adeCodigo);
            bean.setDecDataIni(autorizacao.getAdeAnoMesIni());
            bean.setDecDataFim(autorizacao.getAdeAnoMesFim());
            DespesaComumHome.update(bean);

            // Criar ocorrência de criação de despesa Comum
            String odcObs = (TextHelper.isNull(decData) ?
                ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.odi.obs.inclusao.despesa.comum", responsavel) :
                ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.odi.obs.inclusao.despesa.comum.com.data", responsavel, DateHelper.format(decData, LocaleHelper.getDatePattern()).toString())
            );
            criaOcorrencia(decCodigo, CodedValues.TOC_INCLUSAO_DESPESA_COMUM, odcObs, responsavel);

            LogDelegate logDelegate = new LogDelegate(responsavel, Log.DESPESA_COMUM, Log.CREATE, Log.LOG_INFORMACAO);
            logDelegate.setDespesaComum(decCodigo);
            logDelegate.setEndereco(echCodigo);
            logDelegate.setPlano(plaCodigo);
            logDelegate.setPosto(posCodigo);
            logDelegate.setStatusDespesaComum(sdcCodigo);
            logDelegate.write();

            return decCodigo;

        } catch (LogControllerException | CreateException | UpdateException | FindException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new DespesaComumControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (ParametroControllerException | DespesaIndividualControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new DespesaComumControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> findDespesasComuns(TransferObject criterios, AcessoSistema responsavel) throws DespesaComumControllerException {
        try {
            ListaDespesaComumQuery listaDespesaComumQuery = new ListaDespesaComumQuery();

            if (criterios.getAttribute(Columns.DEC_ECH_CODIGO) != null) {
                listaDespesaComumQuery.echCodigo = criterios.getAttribute(Columns.DEC_ECH_CODIGO).toString();
            }
            if (criterios.getAttribute(Columns.DEC_PLA_CODIGO) != null) {
                listaDespesaComumQuery.plaCodigo = criterios.getAttribute(Columns.DEC_PLA_CODIGO).toString();
            }
            if (criterios.getAttribute(Columns.DEC_CODIGO) != null) {
                listaDespesaComumQuery.decCodigo = criterios.getAttribute(Columns.DEC_CODIGO).toString();
            }

            return listaDespesaComumQuery.executarDTO();
        } catch (HQueryException ex) {
            throw new DespesaComumControllerException(ex);
        }
    }

    @Override
    public int countDespesasComuns(TransferObject criterios, AcessoSistema responsavel) throws DespesaComumControllerException {
        try {
            ListaDespesaComumQuery listaDespesaComumQuery = new ListaDespesaComumQuery();

            if (criterios.getAttribute(Columns.DEC_ECH_CODIGO) != null) {
                listaDespesaComumQuery.echCodigo = criterios.getAttribute(Columns.DEC_ECH_CODIGO).toString();
            }
            if (criterios.getAttribute(Columns.DEC_PLA_CODIGO) != null) {
                listaDespesaComumQuery.plaCodigo = criterios.getAttribute(Columns.DEC_PLA_CODIGO).toString();
            }
            if (criterios.getAttribute(Columns.DEC_CODIGO) != null) {
                listaDespesaComumQuery.decCodigo = criterios.getAttribute(Columns.DEC_CODIGO).toString();
            }

            listaDespesaComumQuery.count = true;

            return listaDespesaComumQuery.executarContador();
        } catch (HQueryException ex) {
            throw new DespesaComumControllerException(ex);
        }
    }

    @Override
    public TransferObject findDespesaComum(String decCodigo, AcessoSistema responsavel) throws DespesaComumControllerException {
        TransferObject retorno = null;
        TransferObject criterios = new CustomTransferObject();
        criterios.setAttribute(Columns.DEC_CODIGO, decCodigo);
        List<TransferObject> despesas = findDespesasComuns(criterios, responsavel);

        if (despesas != null && despesas.size() > 0) {
            retorno = despesas.get(0);
        }

        return retorno;
    }

    @Override
    public void cancelarDespesaComum(String decCodigo, AcessoSistema responsavel) throws DespesaComumControllerException {
        try {
            DespesaComum despesaComum = DespesaComumHome.findByPrimaryKey(decCodigo);

            if(!despesaComum.getStatusDespesaComum().getSdcCodigo().equals(StatusDespesaComumEnum.ATIVO.getCodigo())){
                TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                throw new DespesaComumControllerException("mensagem.erro.despesa.comum.ja.encerrada", responsavel);
            }

            despesaComum.setStatusDespesaComum(new StatusDespesaComum(StatusDespesaComumEnum.CANCELADO.getCodigo()));

            //Atualiza status despesa comum
            LogDelegate logDelegate = new LogDelegate(responsavel, Log.DESPESA_COMUM, Log.UPDATE, Log.LOG_INFORMACAO);
            logDelegate.setDespesaComum(decCodigo);
            logDelegate.setStatusDespesaComum(despesaComum.getStatusDespesaComum().getSdcCodigo());

            DespesaComumHome.update(despesaComum);

            //Cria ocorrencia de cancelamento
            criaOcorrencia(decCodigo, CodedValues.TOC_CANCELAMENTO_DESPESA_COMUM, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.odi.obs.cancelamento.despesa.comum", responsavel), responsavel);

            logDelegate.write();

            //Cancela todas as despesas individuais relacionadas
            List<TransferObject> despesasIndividuais = despesaIndividualController.findDespesasIndividuais(decCodigo, responsavel);

            String adeCodigo = null;
            for (TransferObject despesaIndividual : despesasIndividuais) {
                adeCodigo = (String) despesaIndividual.getAttribute(Columns.ADE_CODIGO);

                despesaIndividualController.cancelaDespesaIndividual(adeCodigo, responsavel);
                //deiDelegate.cancelaDespesasIndividuais(adeCodigo, responsavel);
            }

        } catch (FindException | UpdateException | LogControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new DespesaComumControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (DespesaIndividualControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new DespesaComumControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> findOcorrencias(String decCodigo, AcessoSistema responsavel) throws DespesaComumControllerException{
        try {
            List<TransferObject> resultado = new ArrayList<>();

            // Histórico de ocorrências
            HistoricoDespesaComumOcorrenciaQuery query1 = new HistoricoDespesaComumOcorrenciaQuery();
            query1.decCodigo = decCodigo;
            resultado.addAll(query1.executarDTO());

            // Ordena o resultado das pesquisas
            Collections.sort(resultado, (o1, o2) -> {
                java.util.Date d1 = (java.util.Date) o1.getAttribute(Columns.ODC_DATA);
                java.util.Date d2 = (java.util.Date) o2.getAttribute(Columns.ODC_DATA);
                return d2.compareTo(d1);
            });

            return resultado;
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DespesaComumControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
