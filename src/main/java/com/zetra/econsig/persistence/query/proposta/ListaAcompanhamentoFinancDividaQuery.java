package com.zetra.econsig.persistence.query.proposta;

import java.text.ParseException;
import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.persistence.query.servidor.ListaServidorQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusPropostaEnum;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;

/**
 * <p>Title: ListaAcompanhamentoFinancDividaQuery</p>
 * <p>Description: Pesquisa de acompanhamento do módulo de financiamento
 * de dívida de cartão.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaAcompanhamentoFinancDividaQuery extends HQuery {

    public boolean count = false;
    public String csaCodigo;
    public String tipoFiltro;
    public String periodoIni;
    public String periodoFim;
    public Long adeNumero;
    public String rseMatricula;
    public String serCpf;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        boolean matriculaExata = ParamSist.paramEquals(CodedValues.TPC_PESQUISA_MATRICULA_INTEIRA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

        List<String> sadCodigo = CodedValues.SAD_CODIGOS_ATIVOS;
        String scvCodigo = CodedValues.SCV_ATIVO;
        String tntCodigo = CodedValues.TNT_FINANCIAMENTO_DIVIDA;

        String[] tisCodigo = null;
        String ssoCodigo = null;
        String[] stpCodigo = null;

        boolean contratoTerceiros = false;

        if (tipoFiltro.equals("0")) {
            // Contratos desta entidade: Pendentes de informação de saldo e propostas de pagamento parcelado
            tisCodigo = new String[]{TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR.getCodigo(), TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR_LIQUIDACAO.getCodigo()};
            ssoCodigo = StatusSolicitacaoEnum.PENDENTE.getCodigo();
            stpCodigo = null;

        } else if (tipoFiltro.equals("1")) {
            // Contratos desta entidade: Saldo devedor informado, propostas ofertadas, aguardando aprovação
            tisCodigo = new String[]{TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR.getCodigo(), TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR_LIQUIDACAO.getCodigo()};
            ssoCodigo = StatusSolicitacaoEnum.FINALIZADA.getCodigo();
            stpCodigo = new String[]{StatusPropostaEnum.AGUARDANDO_APROVACAO.getCodigo()};

        } else if (tipoFiltro.equals("2")) {
            // Contratos desta entidade: Saldo devedor informado, propostas ofertadas e aceitas, aguardando processo de renegociação
            tisCodigo = new String[]{TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR.getCodigo(), TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR_LIQUIDACAO.getCodigo()};
            ssoCodigo = StatusSolicitacaoEnum.FINALIZADA.getCodigo();
            stpCodigo = new String[]{StatusPropostaEnum.APROVADA.getCodigo()};

        } else if (tipoFiltro.equals("3")) {
            // Contratos de terceiros: Pendentes de informação de propostas de pagamento parcelado
            contratoTerceiros = true;
            tisCodigo = new String[]{TipoSolicitacaoEnum.SOLICITACAO_PROPOSTA_PARCELAMENTO_DIVIDA.getCodigo()};
            ssoCodigo = StatusSolicitacaoEnum.PENDENTE.getCodigo();
            stpCodigo = null;

        } else if (tipoFiltro.equals("4")) {
            // Contratos de terceiros: Propostas já ofertadas, aguardando aprovação
            contratoTerceiros = true;
            tisCodigo = new String[]{TipoSolicitacaoEnum.SOLICITACAO_PROPOSTA_PARCELAMENTO_DIVIDA.getCodigo()};
            ssoCodigo = StatusSolicitacaoEnum.PENDENTE.getCodigo();
            stpCodigo = new String[]{StatusPropostaEnum.AGUARDANDO_APROVACAO.getCodigo()};

        } else if (tipoFiltro.equals("5")) {
            // Contratos de terceiros: Propostas ofertadas e aceitas, aguardando processo de compra
            contratoTerceiros = true;
            tisCodigo = new String[]{TipoSolicitacaoEnum.SOLICITACAO_PROPOSTA_PARCELAMENTO_DIVIDA.getCodigo()};
            ssoCodigo = StatusSolicitacaoEnum.FINALIZADA.getCodigo();
            stpCodigo = new String[]{StatusPropostaEnum.APROVADA.getCodigo()};

        } else {
            throw new HQueryException("mensagem.erro.filtro.invalido", (AcessoSistema) null);
        }


        StringBuilder corpoBuilder = new StringBuilder();
        if (count) {
            corpoBuilder.append("select count(distinct ade.adeCodigo)");
        } else {
            corpoBuilder.append("select ");
            corpoBuilder.append(" max(soa.soaData),");
            corpoBuilder.append(" ade.adeCodigo,");
            corpoBuilder.append(" ade.adeNumero,");
            corpoBuilder.append(" ade.adeTipoVlr,");
            corpoBuilder.append(" ade.adeVlr,");
            corpoBuilder.append(" sdv.sdvValor,");
            corpoBuilder.append(" sdv.sdvValorComDesconto,");
            corpoBuilder.append(" rse.rseMatricula,");
            corpoBuilder.append(" ser.serNome,");
            corpoBuilder.append(" ser.serCpf,");
            corpoBuilder.append(" csa.csaIdentificador,");
            corpoBuilder.append(" csa.csaNome,");
            corpoBuilder.append(" csa.csaNomeAbrev,");
            corpoBuilder.append(" min(rsv.servicoBySvcCodigoDestino.svcCodigo)");
        }

        corpoBuilder.append(" from SolicitacaoAutorizacao soa");
        corpoBuilder.append(" inner join soa.autDesconto ade");
        corpoBuilder.append(" inner join ade.verbaConvenio vco");
        corpoBuilder.append(" inner join vco.convenio cnv");
        corpoBuilder.append(" inner join cnv.servico svc");
        corpoBuilder.append(" inner join cnv.consignataria csa");
        corpoBuilder.append(" inner join svc.relacionamentoServicoByOrigemSet rsv");
        corpoBuilder.append(" inner join ade.registroServidor rse");
        corpoBuilder.append(" inner join rse.servidor ser");
        corpoBuilder.append(" left join ade.saldoDevedorSet sdv");

        corpoBuilder.append(" where ade.statusAutorizacaoDesconto.sadCodigo").append(criaClausulaNomeada("sadCodigo", sadCodigo));
        corpoBuilder.append(" and rsv.tipoNatureza.tntCodigo ").append(criaClausulaNomeada("tntCodigo", tntCodigo));
        corpoBuilder.append(" and soa.tipoSolicitacao.tisCodigo ").append(criaClausulaNomeada("tisCodigo", tisCodigo));
        corpoBuilder.append(" and soa.statusSolicitacao.ssoCodigo ").append(criaClausulaNomeada("ssoCodigo", ssoCodigo));

        if (stpCodigo != null) {
            // Tem propostas no status informado
            corpoBuilder.append(" and exists (");
            corpoBuilder.append("   select 1 from ade.propostaPagamentoDividaSet ppd");
            corpoBuilder.append("   where ppd.statusProposta.stpCodigo ").append(criaClausulaNomeada("stpCodigo", stpCodigo));
            corpoBuilder.append("     and ppd.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
            corpoBuilder.append(" )");
        } else {
            // Não tem propostas pendentes
            stpCodigo = new String[]{StatusPropostaEnum.AGUARDANDO_APROVACAO.getCodigo(), StatusPropostaEnum.APROVADA.getCodigo()};

            corpoBuilder.append(" and not exists (");
            corpoBuilder.append("   select 1 from ade.propostaPagamentoDividaSet ppd");
            corpoBuilder.append("   where ppd.statusProposta.stpCodigo ").append(criaClausulaNomeada("stpCodigo", stpCodigo));
            corpoBuilder.append("     and ppd.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
            corpoBuilder.append(" )");
        }

        if (contratoTerceiros) {
            // A consignatária da reserva de cartão não tem convênio para o serviço destino do relacionamento de financiamento
            corpoBuilder.append(" and not exists (");
            corpoBuilder.append("   select 1 from Convenio cnv2");
            corpoBuilder.append("   where cnv2.consignataria.csaCodigo = cnv.consignataria.csaCodigo");
            corpoBuilder.append("     and cnv2.orgao.orgCodigo = cnv.orgao.orgCodigo");
            corpoBuilder.append("     and cnv2.servico.svcCodigo = rsv.servicoBySvcCodigoDestino.svcCodigo");
            corpoBuilder.append("     and cnv2.statusConvenio.scvCodigo ").append(criaClausulaNomeada("scvCodigo", scvCodigo));
            corpoBuilder.append(" )");

            // A consignatária do usuário tem convênio para o serviço destino do relacionamento de financiamento
            corpoBuilder.append(" and exists (");
            corpoBuilder.append("   select 1 from Convenio cnv3");
            corpoBuilder.append("   where cnv3.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
            corpoBuilder.append("     and cnv3.orgao.orgCodigo = cnv.orgao.orgCodigo");
            corpoBuilder.append("     and cnv3.servico.svcCodigo = rsv.servicoBySvcCodigoDestino.svcCodigo");
            corpoBuilder.append("     and cnv3.statusConvenio.scvCodigo ").append(criaClausulaNomeada("scvCodigo", scvCodigo));
            corpoBuilder.append(" )");
        } else {
            // Se contratos da própria consignatária, filtra pelo código informado
            corpoBuilder.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        // Adiciona cláusula de matricula e cpf
        corpoBuilder.append(ListaServidorQuery.gerarClausulaMatriculaCpf(rseMatricula, serCpf, !matriculaExata));

        // Adiciona clásula do adeNumero, caso informado
        if (adeNumero != null) {
            corpoBuilder.append(" AND ade.adeNumero ").append(criaClausulaNomeada("adeNumero", adeNumero));
        }

        // Adiciona cláusula de período ini/fim da solicitação de saldo
        if (!TextHelper.isNull(periodoIni)) {
            corpoBuilder.append(" AND soa.soaData >= :periodoIni");
        }
        if (!TextHelper.isNull(periodoFim)) {
            corpoBuilder.append(" AND soa.soaData <= :periodoFim");
        }

        if (!count) {
            corpoBuilder.append(" group by");
            corpoBuilder.append(" ade.adeCodigo,");
            corpoBuilder.append(" ade.adeNumero,");
            corpoBuilder.append(" ade.adeTipoVlr,");
            corpoBuilder.append(" ade.adeVlr,");
            corpoBuilder.append(" sdv.sdvValor,");
            corpoBuilder.append(" sdv.sdvValorComDesconto,");
            corpoBuilder.append(" rse.rseMatricula,");
            corpoBuilder.append(" ser.serNome,");
            corpoBuilder.append(" ser.serCpf,");
            corpoBuilder.append(" csa.csaIdentificador,");
            corpoBuilder.append(" csa.csaNome,");
            corpoBuilder.append(" csa.csaNomeAbrev");

            corpoBuilder.append(" order by 1");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("sadCodigo", sadCodigo, query);
        defineValorClausulaNomeada("tntCodigo", tntCodigo, query);
        defineValorClausulaNomeada("ssoCodigo", ssoCodigo, query);
        defineValorClausulaNomeada("tisCodigo", tisCodigo, query);

        if (contratoTerceiros) {
            defineValorClausulaNomeada("scvCodigo", scvCodigo, query);
        }
        if (stpCodigo != null) {
            defineValorClausulaNomeada("stpCodigo", stpCodigo, query);
        }

        ListaServidorQuery.definirClausulaMatriculaCpf(rseMatricula, serCpf, !matriculaExata, query);

        if (adeNumero != null) {
            defineValorClausulaNomeada("adeNumero", adeNumero, query);
        }
        if (!TextHelper.isNull(periodoIni)) {
            try {
                defineValorClausulaNomeada("periodoIni", DateHelper.parse(periodoIni + " 00:00:00", LocaleHelper.getDateTimePattern()), query);
            } catch (ParseException ex) {
                throw new HQueryException("mensagem.erro.data.inicio.informada.invalida.arg0", (AcessoSistema) null, periodoIni);
            }
        }
        if (!TextHelper.isNull(periodoFim)) {
            try {
                defineValorClausulaNomeada("periodoFim", DateHelper.parse(periodoFim + " 23:59:59", LocaleHelper.getDateTimePattern()), query);
            } catch (ParseException ex) {
                throw new HQueryException("mensagem.erro.data.fim.informada.invalida.arg0", (AcessoSistema) null, periodoFim);
            }
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String [] {
                Columns.SOA_DATA,
                Columns.ADE_CODIGO,
                Columns.ADE_NUMERO,
                Columns.ADE_TIPO_VLR,
                Columns.ADE_VLR,
                Columns.SDV_VALOR,
                Columns.SDV_VALOR_COM_DESCONTO,
                Columns.RSE_MATRICULA,
                Columns.SER_NOME,
                Columns.SER_CPF,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME,
                Columns.CSA_NOME_ABREV,
                Columns.RSV_SVC_CODIGO_DESTINO
        };
    }
}
