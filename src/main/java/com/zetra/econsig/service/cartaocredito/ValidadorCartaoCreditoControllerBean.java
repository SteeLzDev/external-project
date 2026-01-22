package com.zetra.econsig.service.cartaocredito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.ConvenioHome;
import com.zetra.econsig.persistence.entity.OrgaoHome;
import com.zetra.econsig.persistence.entity.ParamSvcConsignante;
import com.zetra.econsig.persistence.entity.ParamSvcConsignanteHome;
import com.zetra.econsig.persistence.entity.RelacionamentoServico;
import com.zetra.econsig.persistence.entity.RelacionamentoServicoHome;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoPorRseCnvQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemTotalValorConsignacaoPorRseCnvQuery;
import com.zetra.econsig.persistence.query.convenio.ListaConveniosQuery;
import com.zetra.econsig.persistence.query.margem.ListaMargemRegistroServidorQuery;
import com.zetra.econsig.persistence.query.margem.ListaProvisionamentoMargemQuery;
import com.zetra.econsig.persistence.query.parametro.ListaRelacionamentosQuery;
import com.zetra.econsig.persistence.query.servico.ListaRelacionamentosServicoQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ValidadorCartaoCreditoControllerBean</p>
 * <p>Description: Session Bean para validações de operaçãos envolvendo consignações de cartão de crédito.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ValidadorCartaoCreditoControllerBean implements ValidadorCartaoCreditoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ValidadorCartaoCreditoControllerBean.class);

    /**
     * Valida se o lançamento é comportado na margem reservado para o serviço de cartão de crédito.
     * @param rseCodigo
     * @param vlrLancamento
     * @param cnvLancamento
     * @param codigoServicoCartao
     * @return lista de contratos de reservar de cartão aos quais o lançamento está relacionado
     * @throws AutorizacaoControllerException
     */
    @Override
    public List<TransferObject> validaLancamentoCartaoCredito(String rseCodigo, BigDecimal vlrLancamento, String cnvLancamento, String codigoServicoCartao, Date periodo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        LOG.debug("CARTÃO DE CRÉDITO");

        Convenio convenioCartaoCredito = null;
        String codigoServicoLancamento = null;

        // Recupera o convênio do serviço de provisionamento de margem
        try {
            final Convenio convenioLancamento = ConvenioHome.findByPrimaryKey(cnvLancamento);
            codigoServicoLancamento = convenioLancamento.getServico().getSvcCodigo();

            try {
                convenioCartaoCredito = ConvenioHome.findByChave(codigoServicoCartao, convenioLancamento.getConsignataria().getCsaCodigo(), convenioLancamento.getOrgao().getOrgCodigo());
            } catch (final FindException ex) {
                convenioCartaoCredito = null;
            }
        } catch (final FindException e) {
            LOG.error(e.getMessage(), e);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
        }

        if (convenioCartaoCredito == null) {
            throw new AutorizacaoControllerException("mensagem.erro.operacao.sem.convenio.provisionamento", responsavel);
        }

        final List<TransferObject> adesReserva = new ArrayList<>();
        BigDecimal valorReservado = calculaValorReservado(rseCodigo, convenioCartaoCredito, adesReserva, responsavel);

        LOG.debug("VALOR RESERVADO: " + valorReservado.toString());

        final BigDecimal valorLancado = calculaValorLancado(rseCodigo, convenioCartaoCredito, periodo, responsavel);

        LOG.debug("VALOR LANÇADO: " + valorLancado.toString());
        LOG.debug("VALOR A LANÇAR: " + vlrLancamento);

        final boolean exibeValorDisponivel = ParamSist.getBoolParamSist(CodedValues.TPC_EXIBE_VALOR_DISPONIVEL_CRITICA_CARTAO, responsavel);
        final String valorDisponivel = exibeValorDisponivel ? NumberHelper.format(valorReservado.subtract(valorLancado).doubleValue(), NumberHelper.getLang()) : "";

        // se houver limite de valor para o qual a margem pode exceder, este é somado ao valor reservado de cartão
        BigDecimal limite = new BigDecimal("0.00");

        try {
            final ParamSvcConsignante pse = ParamSvcConsignanteHome.findByTipoCseServico(CodedValues.TPS_VLR_LIMITE_ADE_SEM_MARGEM, CodedValues.CSE_CODIGO_SISTEMA, codigoServicoLancamento);
            if ((pse != null) && !TextHelper.isNull(pse.getPseVlr())) {
                try {
                    limite = new BigDecimal(pse.getPseVlr());
                } catch (final NumberFormatException ex) {
                    LOG.debug("Valor do parametro de limite de ade sem margem invalido: " + pse.getPseVlr());
                }
            }
        } catch (final FindException e) {
            // Parâmetro não existe, usa o valor padrão
        }

        valorReservado = valorReservado.add(limite);

        // Compara os valores lançados e reservados com o novo valor a ser lançado
        if (valorReservado.subtract(valorLancado).compareTo(vlrLancamento) < 0) {
            LOG.debug("VALOR A SER LANÇADO MAIOR QUE RESERVADO (" + valorLancado.add(vlrLancamento).toString() + " > " + valorReservado.toString() + ")");
            if (exibeValorDisponivel) {
                throw new AutorizacaoControllerException("mensagem.margemInsuficienteCartao.exibe.valor", responsavel, valorDisponivel);
            } else {
                throw new AutorizacaoControllerException("mensagem.margemInsuficienteCartao", responsavel);
            }
        }

        LOG.debug("VALOR A SER LANÇADO DENTRO DO VALOR RESERVADO");

        return adesReserva;
    }

    /**
     * Verifica se a alteração de valor reservado em função dos lançamentos já existentes para a reserva.
     * @param rseCodigo
     * @param vlrAlteracaoReserva
     * @param cnvCodigo - convênio da reserva de cartão
     * @throws AutorizacaoControllerException
     */
    @Override
    public void validaAlteracaoReservaCartaoCredito(String rseCodigo, BigDecimal vlrAlteracaoReserva, String cnvCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        LOG.debug("CARTÃO DE CRÉDITO");

        Convenio convenioCartaoCredito;

        // Recupera o convênio da reserva
        try {
            convenioCartaoCredito = ConvenioHome.findByPrimaryKey(cnvCodigo);
        } catch (final FindException e) {
            LOG.error(e.getMessage(), e);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
        }

        if (convenioCartaoCredito == null) {
            throw new AutorizacaoControllerException("mensagem.erro.operacao.convenio.nao.localizado", responsavel);
        }

        final BigDecimal valorReservado = calculaValorReservado(rseCodigo, convenioCartaoCredito, null, responsavel);

        LOG.debug("VALOR RESERVADO: " + valorReservado.toString());

        final BigDecimal valorLancado = calculaValorLancado(rseCodigo, convenioCartaoCredito, responsavel);

        LOG.debug("VALOR LANÇADO: " + valorLancado.toString());
        LOG.debug("VALOR A ALTERAR DA RESERVA: " + vlrAlteracaoReserva);

        // Verifica se a alteração no valor reservado comportará todos lançamentos.
        if (valorLancado.signum() > 0 && valorReservado.add(vlrAlteracaoReserva).compareTo(valorLancado) < 0) {
            LOG.debug("VALOR RESERVADO NÃO É SUFICIENTE PARA OS LANÇAMENTOS (" + valorReservado.add(vlrAlteracaoReserva).toString() + " < " + valorLancado.toString() + ")");
            throw new AutorizacaoControllerException("mensagem.erro.operacao.valor.reservado.insuficiente", responsavel);
        }

        LOG.debug("VALOR RESERVADO DENTRO DO VALOR LANÇADO");
    }

    /**
     * Verifica se os provisionamentos de margens e os lançamentos que nelas incidem estão consistentes.
     * Ou seja, testa se há valor provisionado suficiente para todos lançamentos.
     * @param rseCodigo Servidor para o qual se deseja verificar a consistência
     * @param adeCodigos Lista de ADEs a incluir como se fossem do servidor ou excluir com se dele não fossem
     * @param excluirAdesLista Indica se a lista de ADEs recebida via parâmetro deve ser excluída ou incluída na verificação
     * @throws AutorizacaoControllerException
     */
    @Override
    public void validaProvisiontamentoMargem(String rseCodigo, List<String> adeCodigos, boolean excluirAdesLista, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final ListaProvisionamentoMargemQuery queryProvisionamento = new ListaProvisionamentoMargemQuery();
            queryProvisionamento.rseCodigo = rseCodigo;
            queryProvisionamento.adeCodigos = adeCodigos;
            queryProvisionamento.excluirAdesCodigos = excluirAdesLista;

            final List<TransferObject> provisionamentoMargem = queryProvisionamento.executarDTO();

            if ((provisionamentoMargem != null) && (provisionamentoMargem.size() > 0)) {
                final Iterator<TransferObject> itProvisionamento = provisionamentoMargem.iterator();

                // Soma o total de valor provisionado e o total de valor lançado.
                TransferObject valoresProvisionamento;
                BigDecimal vlrProvisionado = BigDecimal.valueOf(0.0);
                BigDecimal vlrLancado = BigDecimal.valueOf(0.0);
                while (itProvisionamento.hasNext()) {
                    valoresProvisionamento = itProvisionamento.next();

                    // Somente contratos deferidos são considerados para a soma de valores provisionados.
                    if (CodedValues.SAD_DEFERIDA.equals(valoresProvisionamento.getAttribute(Columns.SAD_CODIGO))) {
                        vlrProvisionado = vlrProvisionado.add(valoresProvisionamento.getAttribute("VLR_PROVISIONADO") != null ? (BigDecimal) valoresProvisionamento.getAttribute("VLR_PROVISIONADO") : BigDecimal.valueOf(0.0));
                    }

                    // Somente contratos ativos são considerados para a soma de valores lançados.
                    if (CodedValues.SAD_CODIGOS_ATIVOS.contains(valoresProvisionamento.getAttribute(Columns.SAD_CODIGO))) {
                        vlrLancado = vlrLancado.add(valoresProvisionamento.getAttribute("VLR_LANCADO") != null ? (BigDecimal) valoresProvisionamento.getAttribute("VLR_LANCADO") : BigDecimal.valueOf(0.0));
                    }
                }

                // O valor provisionado não pode ser menor que o lançado.
                if (vlrProvisionado.compareTo(vlrLancado) < 0) {
                    throw new AutorizacaoControllerException("mensagem.erro.operacao.valor.provisionado.insuficiente", responsavel, vlrProvisionado.toPlainString(), vlrLancado.toPlainString());
                }
            }
        } catch (final HQueryException e) {
            LOG.error(e.getMessage(), e);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    /**
     * Calcula a quantidade reservada para um determinado serviço de cartão de crédito.
     * @param rseCodigo
     * @param convenioCartaoCredito
     * @return
     * @throws AutorizacaoControllerException
     */
    private BigDecimal calculaValorReservado(String rseCodigo, Convenio convenioCartaoCredito, List<TransferObject> adesReserva, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final List<String> sadCodigosPesquisa = new ArrayList<>();
            sadCodigosPesquisa.add(CodedValues.SAD_DEFERIDA);

            // Verifica se o registro servidor possui a reserva do serviço de provisionamento de margem
            final ListaConsignacaoPorRseCnvQuery queryAdesCartaoCredito = new ListaConsignacaoPorRseCnvQuery();
            queryAdesCartaoCredito.rseCodigo = rseCodigo;
            queryAdesCartaoCredito.cnvCodigo = convenioCartaoCredito.getCnvCodigo();
            queryAdesCartaoCredito.sadCodigos = sadCodigosPesquisa;
            final List<TransferObject> adesServicoCartaoCredito = queryAdesCartaoCredito.executarDTO();

            LOG.debug("NUMERO DE CONTRATOS DE CC: " + adesServicoCartaoCredito.size());

            // Soma os valores que foram reservados
            final Iterator<TransferObject> itAdesCartaoCredito = adesServicoCartaoCredito.iterator();
            BigDecimal valorReservado = new BigDecimal("0.0");
            CustomTransferObject cto = null;
            while (itAdesCartaoCredito.hasNext()) {
                cto = (CustomTransferObject) itAdesCartaoCredito.next();
                valorReservado = valorReservado.add(new BigDecimal(cto.getAttribute(Columns.ADE_VLR).toString()));
                if (adesReserva != null) {
                    adesReserva.add(cto);
                }
            }

            return valorReservado;
        } catch (final HQueryException e) {
            LOG.error(e.getMessage(), e);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    private BigDecimal calculaValorLancado(String rseCodigo, Convenio convenioCartaoCredito, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return calculaValorLancado(rseCodigo, convenioCartaoCredito, null, responsavel);
    }


    /**
     * Calcula o valor já lançado para as reservas de um convênio de provisionamento de margem.
     * @param rseCodigo
     * @param convenioCartaoCredito
     * @return
     * @throws AutorizacaoControllerException
     */
    private BigDecimal calculaValorLancado(String rseCodigo, Convenio convenioCartaoCredito, Date periodoLancamento, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final String csaCodigo = convenioCartaoCredito.getConsignataria().getCsaCodigo();
            final String orgCodigo = convenioCartaoCredito.getOrgao().getOrgCodigo();
            final String svcCodigo = convenioCartaoCredito.getServico().getSvcCodigo();

            // Busca pelos serviços que dependem do serviço de provisionamento de margem.
            final ListaRelacionamentosQuery queryRelacionamentos = new ListaRelacionamentosQuery();
            queryRelacionamentos.tntCodigo = CodedValues.TNT_CARTAO;
            queryRelacionamentos.svcCodigoOrigem = svcCodigo;
            final List<TransferObject> servicosDependentes = queryRelacionamentos.executarDTO();

            // Recupera os códigos de serviços dependentes
            final List<String> codigosServicosDependentes = new ArrayList<>();
            if ((servicosDependentes != null) && (servicosDependentes.size() > 0)) {
                for (final TransferObject servico : servicosDependentes) {
                    codigosServicosDependentes.add((String) servico.getAttribute(Columns.RSV_SVC_CODIGO_DESTINO));
                }
            }

            // Recupera os convênios associados aos serviços.
            final ListaConveniosQuery queryConvenios = new ListaConveniosQuery();
            queryConvenios.setServicos(codigosServicosDependentes);
            queryConvenios.csaCodigo = csaCodigo;
            queryConvenios.orgCodigo = orgCodigo;
            final List<TransferObject> conveniosDependentes = queryConvenios.executarDTO();

            if ((conveniosDependentes != null) && (conveniosDependentes.size() > 0)) {
                final List<String> cnvCodigos = new ArrayList<>();
                for (final TransferObject convenio : conveniosDependentes) {
                    cnvCodigos.add((String) convenio.getAttribute(Columns.CNV_CODIGO));
                }

                final java.sql.Date periodoAtual = periodoLancamento != null ? DateHelper.toSQLDate(periodoLancamento) : PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel);

                // Verifica se há outros lançamentos que dependem do serviço de provisionamento de margem
                final ObtemTotalValorConsignacaoPorRseCnvQuery queryAdesLancamentos = new ObtemTotalValorConsignacaoPorRseCnvQuery();
                queryAdesLancamentos.rseCodigo = rseCodigo;
                queryAdesLancamentos.cnvCodigos = cnvCodigos;
                queryAdesLancamentos.sadCodigos = CodedValues.SAD_CODIGOS_ATIVOS;
                queryAdesLancamentos.periodoAtual = periodoAtual;

                final BigDecimal valorLancado = queryAdesLancamentos.executarSomatorio();
                return valorLancado != null ? valorLancado : BigDecimal.ZERO;
            } else {
                LOG.debug("NENHUM CONVÊNIO ASSOCIADO AOS SERVIÇOS.");
                return BigDecimal.ZERO;
            }
        } catch (HQueryException | PeriodoException e) {
            LOG.error(e.getMessage(), e);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public MargemTO consultarMargemDisponivelLancamento(String rseCodigo, String csaCodigo, String svcCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return consultarMargemDisponivelLancamento(rseCodigo, csaCodigo, svcCodigo, null, responsavel);
    }

    @Override
    public MargemTO consultarMargemDisponivelLancamento(String rseCodigo, String csaCodigo, String svcCodigo, Date periodoLancamento, AcessoSistema responsavel) throws AutorizacaoControllerException {
        Convenio convenioCartaoCredito = null;
        MargemTO margem = null;

        try {
            // Busca o serviço de reserva baseado no relacionamento com o serviço de lançamento
            final List<RelacionamentoServico> relacionamentos = RelacionamentoServicoHome.findBySvcCodigoDestino(svcCodigo, Arrays.asList(CodedValues.TNT_CARTAO));
            if ((relacionamentos == null) || relacionamentos.isEmpty()) {
                // Se o serviço passado não tem relacionamento de cartão, então retorna null
                return null;
            }

            final String orgCodigo = OrgaoHome.findByRseCod(rseCodigo).getOrgCodigo();
            final String svcCodigoReserva = relacionamentos.get(0).getServicoBySvcCodigoOrigem().getSvcCodigo();

            // Busca o convênio do serviço de reserva
            try {
                convenioCartaoCredito = ConvenioHome.findByChave(svcCodigoReserva, csaCodigo, orgCodigo);
            } catch (final FindException ex) {
                LOG.warn("Não existe o convênio de reserva de cartão para a consignatária e órgão informados: " + ex.getMessage());
                return null;
            }

            // Busca o registro de margem do serviço de reserva
            final ListaMargemRegistroServidorQuery query = new ListaMargemRegistroServidorQuery();
            query.svcCodigo = svcCodigoReserva;
            query.csaCodigo = csaCodigo;
            query.orgCodigo = orgCodigo;
            query.rseCodigo = rseCodigo;
            final List<MargemTO> lstMargens = query.executarDTO(MargemTO.class);
            if ((lstMargens == null) || lstMargens.isEmpty()) {
                LOG.warn("Não existe o registro de margem ao qual o serviço de reserva incide");
                return null;
            }

            // Pega o primeiro registro de margem (em teoria só pode ter um)
            margem = lstMargens.get(0);
        } catch (HQueryException | FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

        final BigDecimal valorReservado = calculaValorReservado(rseCodigo, convenioCartaoCredito, null, responsavel);
        LOG.debug("VALOR RESERVADO: " + valorReservado.toString());

        final BigDecimal valorLancado = calculaValorLancado(rseCodigo, convenioCartaoCredito, periodoLancamento, responsavel);
        LOG.debug("VALOR LANÇADO: " + valorLancado.toString());

        // Alterar o valor restante para o valor reservado subtraído do valor já lançado
        margem.setMrsMargemRest(valorReservado.subtract(valorLancado).max(BigDecimal.ZERO));

        return margem;
    }

    @Override
    public boolean isReservaCartao(String svcCodigo) throws AutorizacaoControllerException {
        final ListaRelacionamentosServicoQuery lstRelSvcs = new ListaRelacionamentosServicoQuery();
        lstRelSvcs.svcCodigoOrigem = svcCodigo;
        lstRelSvcs.tntCodigo = CodedValues.TNT_CARTAO;

        List<TransferObject> lstResultDest;
        boolean isReservaCartao = false;
        try {
            lstResultDest = lstRelSvcs.executarDTO();
            isReservaCartao = (lstResultDest != null) && !lstResultDest.isEmpty();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException(ex);
        }

        return isReservaCartao;
    }
    
    @Override
    public List<TransferObject> determinaReservaCartao(List<TransferObject> contratos) throws AutorizacaoControllerException {
        final ListaRelacionamentosServicoQuery lstRelSvcs = new ListaRelacionamentosServicoQuery();
        lstRelSvcs.tntCodigo = CodedValues.TNT_CARTAO;

        List<TransferObject> lstResultDest;
        try {
            lstResultDest = lstRelSvcs.executarDTO();
            List<String> svcCodigosCartao = new ArrayList<>();
            
            for (TransferObject result : lstResultDest) {
                String svcCodigo = (String) result.getAttribute(Columns.RSV_SVC_CODIGO_ORIGEM);
                if (!svcCodigosCartao.contains(svcCodigo)) {
                    svcCodigosCartao.add(svcCodigo);
                }
            }
            
            for (TransferObject contrato: contratos) {
                String svcCodigo = (String) contrato.getAttribute(Columns.SVC_CODIGO);
                
                if (svcCodigosCartao != null && !svcCodigosCartao.isEmpty() && svcCodigosCartao.contains(svcCodigo)) {
                    contrato.setAttribute("isReservaCartao", Boolean.TRUE);
                } else {
                    contrato.setAttribute("isReservaCartao", Boolean.FALSE);
                }
            }
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException(ex);
        }

        return contratos;
    }
}
