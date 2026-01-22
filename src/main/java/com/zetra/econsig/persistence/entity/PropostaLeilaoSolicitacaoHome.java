package com.zetra.econsig.persistence.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: PropostaPagamentoDividaHome</p>
 * <p>Description: Classe Home para a entidade PropostaLeilaoSolicitacao</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PropostaLeilaoSolicitacaoHome extends AbstractEntityHome {

    public static PropostaLeilaoSolicitacao findByPrimaryKey(String plsCodigo) throws FindException {
        PropostaLeilaoSolicitacao propostaLeilaoSolicitacao = new PropostaLeilaoSolicitacao();
        propostaLeilaoSolicitacao.setPlsCodigo(plsCodigo);
        return find(propostaLeilaoSolicitacao, plsCodigo);
    }

    public static List<PropostaLeilaoSolicitacao> findByAde(String adeCodigo) throws FindException {
        String query = "FROM PropostaLeilaoSolicitacao pls WHERE pls.autDesconto.adeCodigo = :adeCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("adeCodigo", adeCodigo);

        return findByQuery(query, parameters);
    }

    public static List<PropostaLeilaoSolicitacao> findByAdeStp(String adeCodigo, String stpCodigo) throws FindException {
        String query = "FROM PropostaLeilaoSolicitacao pls WHERE pls.autDesconto.adeCodigo = :adeCodigo AND pls.statusProposta.stpCodigo = :stpCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("adeCodigo", adeCodigo);
        parameters.put("stpCodigo", stpCodigo);

        return findByQuery(query, parameters);
    }

    public static PropostaLeilaoSolicitacao findByAdeCsa(String adeCodigo, String csaCodigo) throws FindException {
        String query = "FROM PropostaLeilaoSolicitacao pls WHERE pls.autDesconto.adeCodigo = :adeCodigo AND pls.consignataria.csaCodigo = :csaCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("adeCodigo", adeCodigo);
        parameters.put("csaCodigo", csaCodigo);

        List<PropostaLeilaoSolicitacao> propostas = findByQuery(query, parameters);
        if (propostas != null && propostas.size() > 0) {
            return propostas.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static PropostaLeilaoSolicitacao findByMelhorTaxa(String adeCodigo) throws FindException {
        String query = "FROM PropostaLeilaoSolicitacao pls WHERE pls.autDesconto.adeCodigo = :adeCodigo ORDER BY pls.plsTaxaJuros";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("adeCodigo", adeCodigo);

        List<PropostaLeilaoSolicitacao> propostas = findByQuery(query, parameters);
        if (propostas != null && propostas.size() > 0) {
            return propostas.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static PropostaLeilaoSolicitacao create(String adeCodigo, String csaCodigo, String svcCodigo, String usuCodigo, String stpCodigo, Integer plsNumero,
            BigDecimal plsValorLiberado, BigDecimal plsValorParcela, Integer plsPrazo, BigDecimal plsTaxaJuros, Date plsDataCadastro, Date plsDataValidade,
            BigDecimal plsOfertaAutDecremento, BigDecimal plsOfertaAutTaxaMin, String plsOfertaAutEmail, String plsTxtContatoCsa) throws CreateException {
        Session session = SessionUtil.getSession();
        PropostaLeilaoSolicitacao bean = new PropostaLeilaoSolicitacao();

        try {
            String objectId = DBHelper.getNextId();
            bean.setPlsCodigo(objectId);
            bean.setAutDesconto((AutDesconto) session.getReference(AutDesconto.class, adeCodigo));
            bean.setConsignataria((Consignataria) session.getReference(Consignataria.class, csaCodigo));
            bean.setServico((Servico) session.getReference(Servico.class, svcCodigo));
            bean.setUsuario((Usuario) session.getReference(Usuario.class, usuCodigo));
            bean.setStatusProposta((StatusProposta) session.getReference(StatusProposta.class, stpCodigo));
            bean.setPlsNumero(plsNumero);
            bean.setPlsValorLiberado(plsValorLiberado);
            bean.setPlsValorParcela(plsValorParcela);
            bean.setPlsPrazo(plsPrazo);
            bean.setPlsTaxaJuros(plsTaxaJuros);
            bean.setPlsDataCadastro((plsDataCadastro != null) ? plsDataCadastro : DateHelper.getSystemDatetime());
            bean.setPlsDataValidade(plsDataValidade);
            bean.setPlsOfertaAutDecremento(plsOfertaAutDecremento);
            bean.setPlsOfertaAutTaxaMin(plsOfertaAutTaxaMin);
            bean.setPlsOfertaAutEmail(plsOfertaAutEmail);
            bean.setPlsTxtContatoCsa(plsTxtContatoCsa);

            create(bean, session);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
        return bean;
    }
}
