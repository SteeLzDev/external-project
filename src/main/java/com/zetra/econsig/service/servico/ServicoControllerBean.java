package com.zetra.econsig.service.servico;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.zetra.econsig.persistence.query.servico.ListaServicosByNseCsaQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.Servico;
import com.zetra.econsig.persistence.entity.ServicoHome;
import com.zetra.econsig.persistence.query.servico.ListaNaturezaServicoQuery;
import com.zetra.econsig.persistence.query.servico.ListaOcorrenciaServicoQuery;
import com.zetra.econsig.persistence.query.servico.ListaRelacionamentosServicoQuery;
import com.zetra.econsig.persistence.query.servico.ListaServicoCancelamentoAutomaticoQuery;
import com.zetra.econsig.persistence.query.servico.ListaServicoComParametroQuery;
import com.zetra.econsig.persistence.query.servico.ListaServicoCorQuery;
import com.zetra.econsig.persistence.query.servico.ListaServicoCsaQuery;
import com.zetra.econsig.persistence.query.servico.ListaServicoMaxParametroQuery;
import com.zetra.econsig.persistence.query.servico.ListaServicoModuloCompraQuery;
import com.zetra.econsig.persistence.query.servico.ListaServicoNaturezaServicosQuery;
import com.zetra.econsig.persistence.query.servico.ListaServicoOrgQuery;
import com.zetra.econsig.persistence.query.servico.ListaServicoParametroCompraQuery;
import com.zetra.econsig.persistence.query.servico.ListaServicoParametroRenegociacaoQuery;
import com.zetra.econsig.persistence.query.servico.ListaServicosRenegociaveisServidorQuery;
import com.zetra.econsig.persistence.query.servico.ListaTipoRelacionamentosSvcQuery;
import com.zetra.econsig.persistence.query.servico.ObtemNaturezaServicoQuery;
import com.zetra.econsig.persistence.query.servico.ObtemServicoRelacCnvAtivoQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ServicoControllerBean</p>
 * <p>Description: Session Façade para operações relacionadas a Serviço.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ServicoControllerBean implements ServicoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ServicoControllerBean.class);

    @Override
    public List<TransferObject> selectServicosOrgao(String orgCodigo, AcessoSistema responsavel) throws ServicoControllerException {
        try {
            ListaServicoOrgQuery query = new ListaServicoOrgQuery();
            query.orgCodigo = orgCodigo;
            return query.executarDTO();
        } catch (HQueryException ex) {
            throw new ServicoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> selectServicosCorrespondente(String corCodigo, AcessoSistema responsavel) throws ServicoControllerException {
        try {
            ListaServicoCorQuery query = new ListaServicoCorQuery();
            query.corCodigo = corCodigo;
            return query.executarDTO();
        } catch (HQueryException ex) {
            throw new ServicoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> selectServicosCsa(String csaCodigo, AcessoSistema responsavel) throws ServicoControllerException {
        try {
            ListaServicoCsaQuery query = new ListaServicoCsaQuery();
            query.csaCodigo = csaCodigo;
            return query.executarDTO();
        } catch (HQueryException ex) {
            throw new ServicoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> selectServicosComParametro(String tpsCodigo, String orgCodigo, String csaCodigo, String pseVlr, boolean selectNull, AcessoSistema responsavel) throws ServicoControllerException {
        return selectServicosComParametro(tpsCodigo, null, orgCodigo, csaCodigo, pseVlr, selectNull, null, responsavel);
    }

    @Override
    public List<TransferObject> selectServicosComParametro(String tpsCodigo, String svcCodigo, String orgCodigo, String csaCodigo, String pseVlr, boolean selectNull, AcessoSistema responsavel) throws ServicoControllerException {
        return selectServicosComParametro(tpsCodigo, svcCodigo, orgCodigo, csaCodigo, pseVlr, selectNull, null, responsavel);
    }

    @Override
    public List<TransferObject> selectServicosComParametro(String tpsCodigo, String orgCodigo, String csaCodigo, String pseVlr, boolean selectNull, String nseCodigo, AcessoSistema responsavel) throws ServicoControllerException {
        return selectServicosComParametro(tpsCodigo, null, orgCodigo, csaCodigo, pseVlr, selectNull, nseCodigo, responsavel);
    }

    /**
     * Busca os serviços ativos que tem o parâmetro de serviço identificado pelo código
     * "tpsCodigo" com valor igual a "pseVlr" (ou com parâmetro null, caso o flag
     * "selectNull" seja igual a true), que possuem algum convênio ativo, com um órgão e
     * consignatária ativas.
     * @param tpsCodigo
     * @param orgCodigo
     * @param svcCodigo
     * @param csaCodigo
     * @param pseVlr
     * @param selectNull
     * @return
     * @throws DAOException
     */
    @Override
    public List<TransferObject> selectServicosComParametro(String tpsCodigo, String svcCodigo, String orgCodigo, String csaCodigo, String pseVlr, boolean selectNull, String nseCodigo, AcessoSistema responsavel) throws ServicoControllerException {
        List<String> pseVlrs = new ArrayList<>();
        if (!TextHelper.isNull(pseVlr)) {
            pseVlrs.add(pseVlr);
        }
        return this.selectServicosComParametro(tpsCodigo, svcCodigo, orgCodigo, csaCodigo, pseVlrs, selectNull, nseCodigo, responsavel);
    }

    /**
     * Busca os serviços ativos que tem o parâmetro de serviço identificado pelo código
     * "tpsCodigo" com valores iguais a "pseVlrs" (ou com parâmetro null, caso o flag
     * "selectNull" seja igual a true), que possuem algum convênio ativo, com um órgão e
     * consignatária ativas, com possibilidade de filtrar por natureza de serviços.
     * @param tpsCodigo
     * @param orgCodigo
     * @param svcCodigo
     * @param csaCodigo
     * @param pseVlrs
     * @param selectNull
     * @param nseCodigo
     * @return
     * @throws DAOException
     */
    @Override
    public List<TransferObject> selectServicosComParametro(String tpsCodigo, String svcCodigo, String orgCodigo, String csaCodigo, List<String> pseVlrs, boolean selectNull, String nseCodigo, AcessoSistema responsavel) throws ServicoControllerException {
        try {
            ListaServicoComParametroQuery query = new ListaServicoComParametroQuery();
            query.tpsCodigo = tpsCodigo;
            query.svcCodigo = svcCodigo;
            query.orgCodigo = orgCodigo;
            query.csaCodigo = csaCodigo;
            query.pseVlrs = pseVlrs;
            query.selectNull = selectNull;
            query.nseCodigo = nseCodigo;

            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServicoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> selectServicosComParametroCorrespondente(String tpsCodigo, String svcCodigo, String orgCodigo, String corCodigo, String pseVlr, boolean selectNull, String nseCodigo, AcessoSistema responsavel) throws ServicoControllerException {
        List<String> pseVlrs = new ArrayList<>();
        if (!TextHelper.isNull(pseVlr)) {
            pseVlrs.add(pseVlr);
        }
        try {
            ListaServicoComParametroQuery query = new ListaServicoComParametroQuery();
            query.tpsCodigo = tpsCodigo;
            query.svcCodigo = svcCodigo;
            query.orgCodigo = orgCodigo;
            query.corCodigo = corCodigo;
            query.pseVlrs = pseVlrs;
            query.selectNull = selectNull;
            query.nseCodigo = nseCodigo;

            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServicoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstServicosRenegociaveisServidor(String svcCodigo, String orgCodigo, String csaCodigo, String nseCodigo, AcessoSistema responsavel) throws ServicoControllerException {
        ListaServicosRenegociaveisServidorQuery query = new ListaServicosRenegociaveisServidorQuery();
        short dia = (short) Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        query.svcCodigo = svcCodigo;
        query.orgCodigo = orgCodigo;
        query.csaCodigo = csaCodigo;
        query.cftDia = dia;

        try {
            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServicoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> selectServicosComParametro(String tpsCodigo, String orgCodigo, String entidade, String csaCodigo, List<String> pseVlrs, boolean selectNull, boolean ativos, AcessoSistema responsavel) throws ServicoControllerException {
        try {
            ListaServicoComParametroQuery query = new ListaServicoComParametroQuery();
            query.tpsCodigo = tpsCodigo;
            query.orgCodigo = orgCodigo;
            query.csaCodigo = csaCodigo;
            query.pseVlrs = pseVlrs;
            query.selectNull = selectNull;
            query.ativos = ativos;
            query.entidade = entidade;

            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServicoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Recupera os serviços que tenham parâmetros do módulo avançado de compras cadastrados.
     * @param orgCodigo
     * @param csaCodigo
     * @return
     * @throws ServicoControllerException
     */
    @Override
    public List<TransferObject> selectServicosModuloAvancadoCompras(String orgCodigo, String csaCodigo, String nseCodigo) throws ServicoControllerException {
        try {
            ListaServicoModuloCompraQuery query = new ListaServicoModuloCompraQuery();
            query.orgCodigo = orgCodigo;
            query.csaCodigo = csaCodigo;
            query.nseCodigo = nseCodigo;

            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServicoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Recupera os serviços e seus parâmetros de restrição de compra de contrato
     * @param orgCodigo
     * @param csaCodigo
     * @param nseCodigo
     * @return
     * @throws ServicoControllerException
     */
    @Override
    public List<TransferObject> selectServicosParametroCompra(String orgCodigo, String csaCodigo, String nseCodigo) throws ServicoControllerException {
        try {
            ListaServicoParametroCompraQuery query = new ListaServicoParametroCompraQuery();
            query.orgCodigo = orgCodigo;
            query.csaCodigo = csaCodigo;
            query.nseCodigo = nseCodigo;

            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServicoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Recupera os serviços e seus parâmetros de restrição de renegociação de contrato
     * @param orgCodigo
     * @param csaCodigo
     * @param nseCodigo
     * @return
     * @throws ServicoControllerException
     */
    @Override
    public List<TransferObject> selectServicosParametroRenegociacao(String orgCodigo, String csaCodigo, String nseCodigo) throws ServicoControllerException {
        try {
            ListaServicoParametroRenegociacaoQuery query = new ListaServicoParametroRenegociacaoQuery();
            query.orgCodigo = orgCodigo;
            query.csaCodigo = csaCodigo;
            query.nseCodigo = nseCodigo;

            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServicoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Recupera os serviços que tenham parâmetros de prazo de cancelamento automático cadastrados.
     * @param orgCodigo
     * @param csaCodigo
     * @return
     * @throws ServicoControllerException
     */
    @Override
    public List<TransferObject> selectServicosCancelamentoAutomatico(String orgCodigo, String csaCodigo, String nseCodigo) throws ServicoControllerException {
        try {
            ListaServicoCancelamentoAutomaticoQuery query = new ListaServicoCancelamentoAutomaticoQuery();
            query.orgCodigo = orgCodigo;
            query.csaCodigo = csaCodigo;
            query.nseCodigo = nseCodigo;

            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServicoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Retorna a lista dos códigos de serviços relacionados ao serviço de origem (svcCodigoOrigem)
     * que possui convênio ativo com a consignatária (csaCodigo) e órgão (orgCodigo), pela
     * natureza (tntCodigo) informados por parâmetros.
     * @param svcCodigoOrigem
     * @param csaCodigo
     * @param orgCodigo
     * @param tntCodigo
     * @param responsavel
     * @return
     * @throws ServicoControllerException
     */
    @Override
    public List<String> obtemServicoRelacionadoComConvenioAtivo(String svcCodigoOrigem, String csaCodigo, String orgCodigo, String tntCodigo, AcessoSistema responsavel) throws ServicoControllerException {
        try {
            ObtemServicoRelacCnvAtivoQuery query = new ObtemServicoRelacCnvAtivoQuery();
            query.svcCodigoOrigem = svcCodigoOrigem;
            query.csaCodigo = csaCodigo;
            query.orgCodigo = orgCodigo;
            query.tntCodigo = tntCodigo;
            return query.executarLista();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServicoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Recupera o código do serviço com o maior valor de um determinado parâmetro
     * @param tpsCodigo
     * @param nseCodigo
     * @param ativos
     * @return
     * @throws ServicoControllerException
     */
    @Override
    public String selectServicoMaxParametro(String tpsCodigo, String nseCodigo, boolean ativos) throws ServicoControllerException {
        try {
            ListaServicoMaxParametroQuery query = new ListaServicoMaxParametroQuery();
            query.tpsCodigo = tpsCodigo;
            query.nseCodigo = nseCodigo;
            query.ativos = ativos;

            List<TransferObject> servico = query.executarDTO();
            if (servico != null && servico.size() > 0) {
                TransferObject to = servico.get(0);
                return (String) to.getAttribute(Columns.SVC_CODIGO);
            }
            return null;
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServicoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Recupera as naturezas de serviço
     * @param orderById Indica se ordena a lista pelo identificador.
     * @return
     * @throws ServicoControllerException
     */
    @Override
    public List<TransferObject> lstNaturezasServicos(boolean orderById) throws ServicoControllerException {
        return lstNaturezasServicos(orderById, false);
    }

    @Override
    public List<TransferObject> lstNaturezasServicos(boolean orderById, boolean naturezaBeneficio) throws ServicoControllerException {
        return this.lstNaturezasServicos(null, orderById, naturezaBeneficio);
    }

    /**
     * retorna naturezas de serviço que tenham convênio ativo para o órgão dado
     * @param orgCodigo
     * @param orderById
     * @param naturezaBeneficio
     * @return
     * @throws ServicoControllerException
     */
    @Override
    public List<TransferObject> lstNaturezasServicos(String orgCodigo, boolean orderById, boolean naturezaBeneficio) throws ServicoControllerException {
        try {
            ListaNaturezaServicoQuery query = new ListaNaturezaServicoQuery();
            query.naturezaBeneficio = naturezaBeneficio;
            query.orderById = orderById;
            if (!TextHelper.isNull(orgCodigo)) {
                query.orgCodigo = orgCodigo;
            }
            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServicoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Recupera as naturezas de serviço do eBeneficios
     * @param orderById Indica se ordena a lista pelo identificador.
     * @return
     * @throws ServicoControllerException
     */
    @Override
    public List<TransferObject> lstNaturezasServicosBeneficios(boolean orderById) throws ServicoControllerException {
        try {
            ListaNaturezaServicoQuery query = new ListaNaturezaServicoQuery();
            query.orderById = orderById;
            List<String> nseCodigo = new ArrayList<>();
            nseCodigo.add(CodedValues.NSE_PLANO_DE_SAUDE);
            nseCodigo.add(CodedValues.NSE_PLANO_ODONTOLOGICO);
            query.nseCodigo = nseCodigo;
            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServicoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Recupera as naturezas de relacionamentos de serviço existentes no sistema
     * @return
     * @throws ServicoControllerException
     */
    @Override
    public List<String> lstTipoNaturezasRelSvc() throws ServicoControllerException {
        try {
            ListaTipoRelacionamentosSvcQuery query = new ListaTipoRelacionamentosSvcQuery();
            return query.executarLista();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServicoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public CustomTransferObject findServico(String svcCodigo) throws ServicoControllerException {
        CustomTransferObject to = new CustomTransferObject();
        try {
            if (svcCodigo != null) {
                Servico svc = ServicoHome.findByPrimaryKey(svcCodigo);
                if (svc != null) {
                    to.setAttribute(Columns.SVC_CODIGO, svc.getSvcCodigo());
                    to.setAttribute(Columns.SVC_DESCRICAO, svc.getSvcDescricao());
                    to.setAttribute(Columns.SVC_IDENTIFICADOR, svc.getSvcIdentificador());
                    to.setAttribute(Columns.SVC_ATIVO, svc.getSvcAtivo());
                    to.setAttribute(Columns.SVC_PRIORIDADE, svc.getSvcPrioridade());
                }
            }
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServicoControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
        return to;
    }

    @Override
    public CustomTransferObject findNaturezaServico(String svcCodigo, AcessoSistema responsavel) throws ServicoControllerException {
        try {
            ObtemNaturezaServicoQuery query = new ObtemNaturezaServicoQuery();
            query.svcCodigo = svcCodigo;

            List<TransferObject> rsList = query.executarDTO();
            if (rsList != null && !rsList.isEmpty()) {
                return (CustomTransferObject) rsList.get(0);
            }

            return null;
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServicoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstOcorrenciaServico(String svcCodigo, List<String> tocCodigos, int offset, int count, AcessoSistema responsavel) throws ServicoControllerException {
        try {
            ListaOcorrenciaServicoQuery query = new ListaOcorrenciaServicoQuery();
            query.svcCodigo = svcCodigo;
            query.tocCodigos = tocCodigos;

            if (count != -1) {
                query.maxResults = count;
                query.firstResult = offset;
            }
            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServicoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public int countOcorrenciaServico(String svcCodigo, List<String> tocCodigos, AcessoSistema responsavel) throws ServicoControllerException {
        try {
            ListaOcorrenciaServicoQuery query = new ListaOcorrenciaServicoQuery();
            query.count = true;
            query.svcCodigo = svcCodigo;
            query.tocCodigos = tocCodigos;
            return query.executarContador();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServicoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<Servico> findByNseCodigo(String nseCodigo, AcessoSistema responsavel) throws ServicoControllerException {
        try {
            return ServicoHome.findByNseCodigo(nseCodigo);
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServicoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstServicoByNaturezas(List<String> nseCodigos, AcessoSistema responsavel) throws ServicoControllerException {
        try {
            ListaServicoNaturezaServicosQuery query = new ListaServicoNaturezaServicosQuery();
            query.nseCodigos = nseCodigos;
            return query.executarDTO();
        } catch (HQueryException ex) {
            throw new ServicoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Lista os serviços de rlecionamento por tipo de natureza
     * @param servicoOrigem
     * @param servicoDestinho
     * @param tipoNatureza
     * @param responsavel
     * @return
     * @throws ServicoControllerException
     */
    @Override
    public List<TransferObject> listaRelacionamentoServicosPorTipoNatureza(String servicoOrigem, String servicoDestinho, String tipoNatureza, AcessoSistema responsavel) throws ServicoControllerException {
        try {
            ListaRelacionamentosServicoQuery listaRelacionamentosServicoQuery = new ListaRelacionamentosServicoQuery();
            listaRelacionamentosServicoQuery.svcCodigoOrigem = servicoOrigem;
            listaRelacionamentosServicoQuery.svcCodigoDestino = servicoDestinho;
            listaRelacionamentosServicoQuery.tntCodigo = tipoNatureza;

            return listaRelacionamentosServicoQuery.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServicoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstServicosByNseCsa(String nseCodigo, String csaCodigo, AcessoSistema responsavel) throws ServicoControllerException {
        try{
            ListaServicosByNseCsaQuery listaServicosByNseCsaQuery = new ListaServicosByNseCsaQuery();
            listaServicosByNseCsaQuery.csaCodigo = csaCodigo;
            listaServicosByNseCsaQuery.nseCodigo = nseCodigo;
            return listaServicosByNseCsaQuery.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServicoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
