package com.zetra.econsig.service.servico;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.NaturezaServicoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.NaturezaServico;
import com.zetra.econsig.persistence.entity.NaturezaServicoHome;
import com.zetra.econsig.persistence.query.servico.ListaNaturezaServicoQuery;
import com.zetra.econsig.service.beneficios.BeneficioControllerBean;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: NaturezaServicoControllerBean</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class NaturezaServicoControllerBean implements NaturezaServicoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(BeneficioControllerBean.class);

    @Override
    public List<NaturezaServico> listaNaturezas(AcessoSistema responsavel) throws NaturezaServicoControllerException {
        try {
            return NaturezaServicoHome.listaNaturezas(responsavel);
        } catch (Exception ex) {
            throw new NaturezaServicoControllerException(ex);
        }
    }

    @Override
    public List<NaturezaServico> listaNaturezasByNseCodigo(List<String> nseCodigos, AcessoSistema responsavel) throws NaturezaServicoControllerException {
        return listaNaturezasByNseCodigo(nseCodigos, false, responsavel);
    }

    @Override
    public List<NaturezaServico> listaNaturezasByNseCodigo(List<String> nseCodigos, boolean naturezaBeneficio, AcessoSistema responsavel) throws NaturezaServicoControllerException {
        try {
            List<NaturezaServico> naturezasServico = new ArrayList<>();
            ListaNaturezaServicoQuery listaNaturezaServicoQuery = new ListaNaturezaServicoQuery();
            listaNaturezaServicoQuery.naturezaBeneficio = naturezaBeneficio;
            listaNaturezaServicoQuery.nseCodigo = nseCodigos;
            List<TransferObject> tos = listaNaturezaServicoQuery.executarDTO();

            for (TransferObject to : tos) {
                NaturezaServico naturezaServico = new NaturezaServico();
                String nseCodigo = to.getAttribute(Columns.NSE_CODIGO).toString();
                String nseDescricao = to.getAttribute(Columns.NSE_DESCRICAO).toString();
                naturezaServico.setNseCodigo(nseCodigo);
                naturezaServico.setNseDescricao(nseDescricao);

                naturezasServico.add(naturezaServico);
            }

            return naturezasServico;
        } catch (HQueryException e) {
            LOG.error(e.getMessage(), e);
            throw new NaturezaServicoControllerException(e);
        }
    }

    @Override
    public NaturezaServico buscaNaturezaServico(String nseCodigo, AcessoSistema responsavel) throws NaturezaServicoControllerException {
        try {
            return NaturezaServicoHome.findByPrimaryKey(nseCodigo);
        } catch (FindException e) {
            LOG.error(e.getMessage(), e);
            throw new NaturezaServicoControllerException(e);
        }
    }

    @Override
    public List<NaturezaServico> listaNaturezasByNseCodigoPai(String nseCodigoPai, AcessoSistema responsavel) throws NaturezaServicoControllerException {
        return listaNaturezasByNseCodigoPai(nseCodigoPai, false,  responsavel);
    }

    @Override
    public List<NaturezaServico> listaNaturezasByNseCodigoPai(String nseCodigoPai, boolean naturezaBeneficio, AcessoSistema responsavel) throws NaturezaServicoControllerException {
        try {
            List<NaturezaServico> naturezasServico = new ArrayList<>();
            ListaNaturezaServicoQuery listaNaturezaServicoQuery = new ListaNaturezaServicoQuery();
            listaNaturezaServicoQuery.naturezaBeneficio = naturezaBeneficio;
            listaNaturezaServicoQuery.nseCodigoPai = nseCodigoPai;
            List<TransferObject> tos = listaNaturezaServicoQuery.executarDTO();

            for (TransferObject to : tos) {
                NaturezaServico naturezaServico = new NaturezaServico();
                String nseCodigo = to.getAttribute(Columns.NSE_CODIGO).toString();
                String nseDescricao = to.getAttribute(Columns.NSE_DESCRICAO).toString();
                naturezaServico.setNseCodigo(nseCodigo);
                naturezaServico.setNseDescricao(nseDescricao);

                naturezasServico.add(naturezaServico);
            }

            return naturezasServico;
        } catch (HQueryException e) {
            LOG.error(e.getMessage(), e);
            throw new NaturezaServicoControllerException(e);
        }
    }

    /**
     * listas as naturezas de serviço filhas de uma natureza na hierarquia.
     * @param orgCodigo - se preenchido, busca nas naturezas de serviço com convênios ativos para o órgão dado
     * @param nseCodigoPai - código da natureza de serviço pai
     * @param naturezaBeneficio - filtra naturezas benefícios
     * @param responsavel
     */
    @Override
    public List<NaturezaServico> listaNaturezasByNseCodigoPai(String orgCodigo, String nseCodigoPai, boolean naturezaBeneficio, AcessoSistema responsavel) throws NaturezaServicoControllerException {
        try {
            List<NaturezaServico> naturezasServico = new ArrayList<>();
            ListaNaturezaServicoQuery listaNaturezaServicoQuery = new ListaNaturezaServicoQuery();
            listaNaturezaServicoQuery.naturezaBeneficio = naturezaBeneficio;
            listaNaturezaServicoQuery.nseCodigoPai = nseCodigoPai;
            if (!TextHelper.isNull(orgCodigo)) {
                listaNaturezaServicoQuery.orgCodigo = orgCodigo;
            }
            List<TransferObject> tos = listaNaturezaServicoQuery.executarDTO();

            for (TransferObject to : tos) {
                NaturezaServico naturezaServico = new NaturezaServico();
                String nseCodigo = to.getAttribute(Columns.NSE_CODIGO).toString();
                String nseDescricao = to.getAttribute(Columns.NSE_DESCRICAO).toString();
                naturezaServico.setNseCodigo(nseCodigo);
                naturezaServico.setNseDescricao(nseDescricao);

                naturezasServico.add(naturezaServico);
            }

            return naturezasServico;
        } catch (HQueryException e) {
            LOG.error(e.getMessage(), e);
            throw new NaturezaServicoControllerException(e);
        }
    }
}
