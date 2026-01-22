package com.zetra.econsig.persistence.query.proposta;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.StatusPropostaEnum;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;

/**
 * <p>Title: VerificaAusenciaPropostasPgtDivQuery</p>
 * <p>Description: Verifica se a consignação possui saldo devedor
 * informado, porém não tem informação de propostas de pagamento,
 * já que a consignatária não tem convênio para financiamento.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class VerificaAusenciaPropostasPgtDivQuery extends HQuery {

    public String adeCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        List<String> sadCodigo = CodedValues.SAD_CODIGOS_ATIVOS;
        String scvCodigo = CodedValues.SCV_ATIVO;
        String tntCodigo = CodedValues.TNT_FINANCIAMENTO_DIVIDA;
        String ssoCodigo = StatusSolicitacaoEnum.FINALIZADA.getCodigo();
        String[] tisCodigo = {TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR.getCodigo(),
                TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR_LIQUIDACAO.getCodigo()};
        String[] stpCodigo = {StatusPropostaEnum.AGUARDANDO_APROVACAO.getCodigo(),
                StatusPropostaEnum.APROVADA.getCodigo()};

        String ssoCodigo2 = StatusSolicitacaoEnum.PENDENTE.getCodigo();
        String tisCodigo2 = TipoSolicitacaoEnum.SOLICITACAO_PROPOSTA_PARCELAMENTO_DIVIDA.getCodigo();


        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select count(*)");
        corpoBuilder.append(" from SolicitacaoAutorizacao soa");
        corpoBuilder.append(" inner join soa.autDesconto ade");
        corpoBuilder.append(" inner join ade.verbaConvenio vco");
        corpoBuilder.append(" inner join vco.convenio cnv");
        corpoBuilder.append(" inner join cnv.servico svc");
        corpoBuilder.append(" inner join svc.relacionamentoServicoByOrigemSet rsv");

        corpoBuilder.append(" where ade.adeCodigo").append(criaClausulaNomeada("adeCodigo", adeCodigo));
        corpoBuilder.append(" and ade.statusAutorizacaoDesconto.sadCodigo").append(criaClausulaNomeada("sadCodigo", sadCodigo));
        corpoBuilder.append(" and soa.statusSolicitacao.ssoCodigo ").append(criaClausulaNomeada("ssoCodigo", ssoCodigo));
        corpoBuilder.append(" and soa.tipoSolicitacao.tisCodigo ").append(criaClausulaNomeada("tisCodigo", tisCodigo));
        corpoBuilder.append(" and rsv.tipoNatureza.tntCodigo ").append(criaClausulaNomeada("tntCodigo", tntCodigo));

        // Tem saldo
        corpoBuilder.append(" and exists (select 1 from ade.saldoDevedorSet sdv)");

        // Não tem propostas aprovadas ou aguardando aprovação
        corpoBuilder.append(" and not exists (");
        corpoBuilder.append("   select 1 from ade.propostaPagamentoDividaSet ppd");
        corpoBuilder.append("   where ppd.statusProposta.stpCodigo ").append(criaClausulaNomeada("stpCodigo", stpCodigo));
        corpoBuilder.append(" )");

        // Não tem solicitação de propostas pendente
        corpoBuilder.append(" and not exists (");
        corpoBuilder.append("   select 1 from ade.solicitacaoAutorizacaoSet soa2");
        corpoBuilder.append("   where soa2.tipoSolicitacao.tisCodigo ").append(criaClausulaNomeada("tisCodigo2", tisCodigo2));
        corpoBuilder.append("     and soa2.statusSolicitacao.ssoCodigo ").append(criaClausulaNomeada("ssoCodigo2", ssoCodigo2));
        corpoBuilder.append(" )");

        // A consignatária não tem convênio para o serviço destino do relacionamento de financiamento
        corpoBuilder.append(" and not exists (");
        corpoBuilder.append("   select 1 from Convenio cnv2");
        corpoBuilder.append("   where cnv2.consignataria.csaCodigo = cnv.consignataria.csaCodigo");
        corpoBuilder.append("     and cnv2.orgao.orgCodigo = cnv.orgao.orgCodigo");
        corpoBuilder.append("     and cnv2.servico.svcCodigo = rsv.servicoBySvcCodigoDestino.svcCodigo");
        corpoBuilder.append("     and cnv2.statusConvenio.scvCodigo ").append(criaClausulaNomeada("scvCodigo", scvCodigo));
        corpoBuilder.append(" )");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);
        defineValorClausulaNomeada("sadCodigo", sadCodigo, query);
        defineValorClausulaNomeada("ssoCodigo", ssoCodigo, query);
        defineValorClausulaNomeada("tisCodigo", tisCodigo, query);
        defineValorClausulaNomeada("tntCodigo", tntCodigo, query);
        defineValorClausulaNomeada("scvCodigo", scvCodigo, query);
        defineValorClausulaNomeada("stpCodigo", stpCodigo, query);

        defineValorClausulaNomeada("ssoCodigo2", ssoCodigo2, query);
        defineValorClausulaNomeada("tisCodigo2", tisCodigo2, query);

        return query;
    }
}
