package com.zetra.econsig.persistence.query.leilao;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.Query;

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
 * <p>Title: ListaAcompanhamentoLeilaoSolicitacaoQuery</p>
 * <p>Description: Pesquisa de acompanhamento do módulo de leilão de solicitação.</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaAcompanhamentoLeilaoSolicitacaoQuery extends HQuery {

    public boolean count = false;

    public String adeCodigo;
    public String csaCodigo;
    public String tipoLeilao;
    public String tipoFiltro;
    public String dataAberturaIni;
    public String dataAberturaFim;
    public String horasFimLeilao;
    public String rseMatricula;
    public String serCpf;
    public String cidCodigo;
    public String posCodigo;
    public String rsePontuacao;
    public String rseMargemLivre;
    public String arrRisco;

    public AcessoSistema responsavel;

    public String ordenacao;
    private static String ASC = "ASC";
    private static String DESC = "DESC";

    private String margemLivreIni;
    private String margemLivreFim;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        boolean matriculaExata = ParamSist.paramEquals(CodedValues.TPC_PESQUISA_MATRICULA_INTEIRA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

        String tisCodigo = TipoSolicitacaoEnum.SOLICITACAO_PROPOSTA_LEILAO_VIA_SIMULACAO.getCodigo();
        List<String> stpCodigos = new ArrayList<>();
        List<String> ssoCodigos = new ArrayList<>();
        List<String> sadCodigos = new ArrayList<>();

        Date dataAtual = Calendar.getInstance().getTime();

        if (tipoFiltro.equals("0")) {
            // Pendentes de informação de propostas
            ssoCodigos.add(StatusSolicitacaoEnum.PENDENTE.getCodigo());
            sadCodigos.add(CodedValues.SAD_SOLICITADO);

        } else if (tipoFiltro.equals("1")) {
            // Proposta ofertada, aguardando aprovação
            ssoCodigos.add(StatusSolicitacaoEnum.PENDENTE.getCodigo());
            ssoCodigos.add(StatusSolicitacaoEnum.EXPIRADA.getCodigo());
            stpCodigos.add(StatusPropostaEnum.AGUARDANDO_APROVACAO.getCodigo());
            sadCodigos.add(CodedValues.SAD_SOLICITADO);

        } else if (tipoFiltro.equals("2")) {
            // Propostas ofertada e aceita
            ssoCodigos.add(StatusSolicitacaoEnum.FINALIZADA.getCodigo());
            stpCodigos.add(StatusPropostaEnum.APROVADA.getCodigo());
            sadCodigos.add(CodedValues.SAD_CANCELADA);

        } else if (tipoFiltro.equals("3")) {
            // Propostas ofertadas e rejeitadas
            ssoCodigos.add(StatusSolicitacaoEnum.FINALIZADA.getCodigo());
            stpCodigos.add(StatusPropostaEnum.REJEITADA.getCodigo());
            sadCodigos.add(CodedValues.SAD_CANCELADA);

        } else if (tipoFiltro.equals("4")) {
            // Propostas ofertadas, aguardando aprovação, aceitas ou rejeitadas
            ssoCodigos.add(StatusSolicitacaoEnum.PENDENTE.getCodigo());
            ssoCodigos.add(StatusSolicitacaoEnum.EXPIRADA.getCodigo());
            ssoCodigos.add(StatusSolicitacaoEnum.FINALIZADA.getCodigo());
            stpCodigos.add(StatusPropostaEnum.AGUARDANDO_APROVACAO.getCodigo());
            stpCodigos.add(StatusPropostaEnum.APROVADA.getCodigo());
            stpCodigos.add(StatusPropostaEnum.REJEITADA.getCodigo());
            sadCodigos.add(CodedValues.SAD_SOLICITADO);
            sadCodigos.add(CodedValues.SAD_CANCELADA);

        } else {
            throw new HQueryException("mensagem.erro.filtro.invalido", (AcessoSistema) null);
        }

        // define variação da margem livre
        if (!TextHelper.isNull(rseMargemLivre)) {
            margemLivreIni = String.valueOf(Integer.valueOf(rseMargemLivre) - 10);
            margemLivreFim = rseMargemLivre;
        }
        StringBuilder corpoBuilder = new StringBuilder();

        if (count) {
            corpoBuilder.append("select count(distinct ade.adeCodigo)");

        } else {
            corpoBuilder.append("select ");
            corpoBuilder.append(" soa.soaData,");
            corpoBuilder.append(" soa.soaDataValidade,");
            corpoBuilder.append(" ade.adeCodigo,");
            corpoBuilder.append(" ade.adeNumero,");
            corpoBuilder.append(" ade.adeTipoVlr,");
            corpoBuilder.append(" ade.adeVlrLiquido,");
            corpoBuilder.append(" ade.adeVlr,");
            corpoBuilder.append(" ade.adePrazo,");

            corpoBuilder.append(" cid.cidNome,");
            corpoBuilder.append(" uf.ufCod,");

            corpoBuilder.append(" rse.rseMatricula,");
            corpoBuilder.append(" rse.rseCodigo,");
            corpoBuilder.append(" ser.serNome,");
            corpoBuilder.append(" rse.rsePontuacao,");
            corpoBuilder.append(" arr.arrRisco,");
            corpoBuilder.append(" min(prop2.plsTaxaJuros),");
            corpoBuilder.append(" min(prop.plsTaxaJuros),");
            corpoBuilder.append(" ser.serCpf, ");
            corpoBuilder.append(" ade.adeIncMargem, ");
            corpoBuilder.append(" soa.statusSolicitacao.ssoCodigo,");
            // variação da margem livre de acordo com a incidência de margem do contrato
            corpoBuilder.append(" (case when ade.adeIncMargem = " + CodedValues.INCIDE_MARGEM_SIM + " then ");
            corpoBuilder.append(" ((rse.rseMargemRest / rse.rseMargem) * 100.00) ");
            corpoBuilder.append(" when ade.adeIncMargem = " + CodedValues.INCIDE_MARGEM_SIM_2 + " then ");
            corpoBuilder.append(" ((rse.rseMargemRest2 / rse.rseMargem2) * 100.00) ");
            corpoBuilder.append(" when ade.adeIncMargem = " + CodedValues.INCIDE_MARGEM_SIM_3 + " then ");
            corpoBuilder.append(" ((rse.rseMargemRest3 / rse.rseMargem3) * 100.00) ");
            corpoBuilder.append(" else ");
            corpoBuilder.append(" '0.00' ");
            corpoBuilder.append(" end) as variacaoMargemLivre, ");
            // variação da margem extra livre de acordo com a incidência de margem do contrato
            corpoBuilder.append(" coalesce((select ((mrs.mrsMargemRest / mrs.mrsMargem) * 100.00) ");
            corpoBuilder.append(" from MargemRegistroServidor mrs ");
            corpoBuilder.append(" where mrs.registroServidor.rseCodigo = rse.rseCodigo ");
            corpoBuilder.append(" and mrs.margem.marCodigo = ade.adeIncMargem),'0.00') as variacaoMargemExtraLivre ");

            if (tipoFiltro.equals("2") || tipoFiltro.equals("3") || tipoFiltro.equals("4")) {
                corpoBuilder.append(", adeDestino.adeCodigo");
            }
        }

        corpoBuilder.append(" from SolicitacaoAutorizacao soa");
        corpoBuilder.append(" inner join soa.autDesconto ade");
        corpoBuilder.append(" inner join ade.registroServidor rse");
        corpoBuilder.append(" inner join rse.servidor ser");
        corpoBuilder.append(" left outer join rse.analiseRiscoRegistroSerSet arr");

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" WITH arr.consignataria.csaCodigo = :csaCodigoArr");
        }

        corpoBuilder.append(" left outer join ade.cidade cid");
        corpoBuilder.append(" left outer join cid.uf uf");

        if (tipoFiltro.equals("2") || tipoFiltro.equals("3") || tipoFiltro.equals("4")) {
            corpoBuilder.append(" left outer join ade.relacionamentoAutorizacaoByAdeCodigoOrigemSet rad");
            corpoBuilder.append(" with rad.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_LEILAO_SOLICITACAO).append("'");
            corpoBuilder.append(" left outer join rad.autDescontoByAdeCodigoDestino adeDestino");
        }

        corpoBuilder.append(" inner join ade.propostaLeilaoSolicitacaoSet prop ");
        corpoBuilder.append(" left join ade.propostaLeilaoSolicitacaoSet prop2 ");

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" WITH prop2.consignataria.csaCodigo = :csaCodigoProp");
        }

        corpoBuilder.append(" where soa.tipoSolicitacao.tisCodigo ").append(criaClausulaNomeada("tisCodigo", tisCodigo));

        if (!TextHelper.isNull(adeCodigo)) {
            corpoBuilder.append(" and ade.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));
        }

        if (tipoFiltro.equals("0") || tipoFiltro.equals("1")) {
            corpoBuilder.append(" and ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));
            corpoBuilder.append(" and soa.statusSolicitacao.ssoCodigo ").append(criaClausulaNomeada("ssoCodigos", ssoCodigos));
        } else {
            // situação do contrato
            corpoBuilder.append(" and (ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));
            corpoBuilder.append(" or (ade.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_SOLICITADO).append("' ");
            corpoBuilder.append(" and soa.soaDataValidade < :dataAtual)) ");
            // situação do leilão
            corpoBuilder.append(" and (soa.statusSolicitacao.ssoCodigo ").append(criaClausulaNomeada("ssoCodigos", ssoCodigos));
            corpoBuilder.append(" or (soa.statusSolicitacao.ssoCodigo = '").append(StatusSolicitacaoEnum.PENDENTE.getCodigo()).append("' ");
            corpoBuilder.append(" and soa.soaDataValidade < :dataAtual)) ");
        }

        if (!TextHelper.isNull(posCodigo)) {
            corpoBuilder.append(" and rse.postoRegistroServidor.posCodigo ").append(criaClausulaNomeada("posCodigo", posCodigo));
        }

        if (!TextHelper.isNull(cidCodigo)) {
            corpoBuilder.append(" and ade.cidade.cidCodigo ").append(criaClausulaNomeada("cidCodigo", cidCodigo));
        }

        if (!TextHelper.isNull(rsePontuacao)) {
            corpoBuilder.append(" and rse.rsePontuacao >= :rsePontuacao");
        }

        if (!TextHelper.isNull(arrRisco)) {
            corpoBuilder.append(" and (arr.arrRisco IS NULL OR arr.arrRisco <= :arrRisco)");
        }

        if (tipoFiltro.equals("0")) {
            // Pendente de informação de propostas
            corpoBuilder.append(" and prop2.plsCodigo IS NULL ");
        } else if (tipoFiltro.equals("2") || tipoFiltro.equals("3")) {
            // Proposta aprovada ou rejeitada, ou ainda aguardando aprovação mas com leilão vencido
            corpoBuilder.append(" and (prop2.statusProposta.stpCodigo ").append(criaClausulaNomeada("stpCodigos", stpCodigos));
            corpoBuilder.append("  or (prop2.statusProposta.stpCodigo = '").append(StatusPropostaEnum.AGUARDANDO_APROVACAO.getCodigo()).append("' and soa.soaDataValidade < :dataAtual)) ");
        } else {
            // Proposta ofertada, aguardando aprovação
            corpoBuilder.append(" and prop2.statusProposta.stpCodigo ").append(criaClausulaNomeada("stpCodigos", stpCodigos));
        }

        if (tipoFiltro.equals("2") || tipoFiltro.equals("3")) {
            corpoBuilder.append(" and (adeDestino.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_SOLICITADO).append("' ");
            corpoBuilder.append(" or (adeDestino.adeCodigo IS NULL and soa.soaDataValidade < :dataAtual)) ");
        } else if (tipoFiltro.equals("4")) {
            corpoBuilder.append(" and (adeDestino.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_SOLICITADO).append("' ");
            corpoBuilder.append(" or adeDestino.adeCodigo IS NULL) ");
        }

        if (!TextHelper.isNull(tipoLeilao) && !"0".equals(tipoLeilao)) {
            corpoBuilder.append("1".equals(tipoLeilao) ? " AND NOT EXISTS " : " AND EXISTS ");
            corpoBuilder.append(" (SELECT 1 FROM ade.relacionamentoAutorizacaoByAdeCodigoDestinoSet radPortabilidade");
            corpoBuilder.append(" WHERE radPortabilidade.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_SOLICITACAO_PORTABILIDADE).append("')");
        }

        // Adiciona cláusula de matricula e cpf
        if (responsavel.isSer()) {
            corpoBuilder.append(" AND ").append(criaClausulaNomeada("rse.rseCodigo", "rseCodigo", responsavel.getRseCodigo()));
        } else {
            corpoBuilder.append(ListaServidorQuery.gerarClausulaMatriculaCpf(rseMatricula, serCpf, !matriculaExata));
        }

        // Adiciona cláusula de período ini/fim da solicitação
        if (!TextHelper.isNull(dataAberturaIni)) {
            corpoBuilder.append(" AND soa.soaData >= :dataAberturaIni");
        }
        if (!TextHelper.isNull(dataAberturaFim)) {
            corpoBuilder.append(" AND soa.soaData <= :dataAberturaFim");
        }

        // Adiciona cláusula de período ini/fim da validade da solicitação
        if (!TextHelper.isNull(horasFimLeilao)) {
            corpoBuilder.append(" AND (soa.soaDataValidade <= add_hour(current_timestamp(),:horasFimLeilao))");
        }

        // Exibe somente leilões dentro da validade na listagen de pendentes e expirados
        if (tipoFiltro.equals("0") || tipoFiltro.equals("1")) {
            corpoBuilder.append(" and soa.soaDataValidade >= :dataAtual ");
        }

        // Adiciona cláusula de variação de margem livre
        if (!TextHelper.isNull(rseMargemLivre)) {
            corpoBuilder.append(" and ((ade.adeIncMargem = " + CodedValues.INCIDE_MARGEM_SIM + " ");
            if (margemLivreIni.equals("0")) {
                corpoBuilder.append("   and ((rse.rseMargemRest / rse.rseMargem) * 100.00) <= :margemLivreFim) ");
            } else if (margemLivreFim.equals("100")) {
                corpoBuilder.append(" and ((rse.rseMargemRest / rse.rseMargem) * 100.00) > :margemLivreIni) ");
            } else {
                corpoBuilder.append(" and ((rse.rseMargemRest / rse.rseMargem) * 100.00) > :margemLivreIni and ((rse.rseMargemRest / rse.rseMargem) * 100.00) <= :margemLivreFim) ");
            }
            corpoBuilder.append(" or (ade.adeIncMargem = " + CodedValues.INCIDE_MARGEM_SIM_2 + " ");
            if (margemLivreIni.equals("0")) {
                corpoBuilder.append("   and ((rse.rseMargemRest2 / rse.rseMargem2) * 100.00) <= :margemLivreFim) ");
            } else if (margemLivreFim.equals("100")) {
                corpoBuilder.append(" and ((rse.rseMargemRest2 / rse.rseMargem2) * 100.00) > :margemLivreIni) ");
            } else {
                corpoBuilder.append(" and ((rse.rseMargemRest2 / rse.rseMargem2) * 100.00) > :margemLivreIni and ((rse.rseMargemRest2 / rse.rseMargem2) * 100.00) <= :margemLivreFim) ");
            }
            corpoBuilder.append(" or (ade.adeIncMargem = " + CodedValues.INCIDE_MARGEM_SIM_3 + " ");
            if (margemLivreIni.equals("0")) {
                corpoBuilder.append("   and ((rse.rseMargemRest3 / rse.rseMargem3) * 100.00) <= :margemLivreFim) ");
            } else if (margemLivreFim.equals("100")) {
                corpoBuilder.append(" and ((rse.rseMargemRest3 / rse.rseMargem3) * 100.00) > :margemLivreIni) ");
            } else {
                corpoBuilder.append(" and ((rse.rseMargemRest3 / rse.rseMargem3) * 100.00) > :margemLivreIni and ((rse.rseMargemRest3 / rse.rseMargem3) * 100.00) <= :margemLivreFim) ");
            }
            corpoBuilder.append(" or (ade.adeIncMargem > " + CodedValues.INCIDE_MARGEM_SIM_3 + " ");
            corpoBuilder.append(" and (exists (select 1 ");
            corpoBuilder.append(" from MargemRegistroServidor mrs ");
            corpoBuilder.append(" where mrs.registroServidor.rseCodigo = rse.rseCodigo ");
            corpoBuilder.append(" and mrs.margem.marCodigo = ade.adeIncMargem ");
            if (margemLivreIni.equals("0")) {
                corpoBuilder.append(" and ((mrs.mrsMargemRest / mrs.mrsMargem) * 100.00) <= :margemLivreFim ");
            } else if (margemLivreFim.equals("100")) {
                corpoBuilder.append(" and ((mrs.mrsMargemRest / mrs.mrsMargem) * 100.00) > :margemLivreIni ");
            } else {
                corpoBuilder.append(" and ((mrs.mrsMargemRest / mrs.mrsMargem) * 100.00) > :margemLivreIni ");
                corpoBuilder.append(" and ((mrs.mrsMargemRest / mrs.mrsMargem) * 100.00) <= :margemLivreFim ");
            }
            if (margemLivreIni.equals("0")) {
                // caso o servidor não tenha margem extra, enquadra no "até 10%"
                corpoBuilder.append(" or not exists (select 1 ");
                corpoBuilder.append(" from MargemRegistroServidor mrs ");
                corpoBuilder.append(" where mrs.registroServidor.rseCodigo = rse.rseCodigo ");
                corpoBuilder.append(" and mrs.margem.marCodigo = ade.adeIncMargem ");
                corpoBuilder.append(" ) ");
            }
            corpoBuilder.append(" ) ");
            corpoBuilder.append(" ))) ");
        }

        if (!count) {
            corpoBuilder.append(" group by soa.soaData, soa.soaDataValidade, ade.adeCodigo, ade.adeNumero, ade.adeTipoVlr, ade.adeVlrLiquido, ade.adeVlr, ade.adePrazo,");
            corpoBuilder.append("rse.rseMatricula, ser.serNome, rse.rsePontuacao, ser.serCpf");
            if (tipoFiltro.equals("2") || tipoFiltro.equals("3") || tipoFiltro.equals("4")) {
                corpoBuilder.append(", adeDestino.adeCodigo");
            }
            if (tipoFiltro.equals("2")) {
                corpoBuilder.append(" having min(prop2.statusProposta.stpCodigo) = '").append(StatusPropostaEnum.APROVADA.getCodigo()).append("' ");
                corpoBuilder.append(" or min(prop2.plsTaxaJuros) = min(prop.plsTaxaJuros) ");
            } else if (tipoFiltro.equals("3")) {
                corpoBuilder.append(" having min(prop2.statusProposta.stpCodigo) = '").append(StatusPropostaEnum.REJEITADA.getCodigo()).append("' ");
                corpoBuilder.append(" or max(prop2.plsTaxaJuros) != min(prop.plsTaxaJuros) ");
            }

            if (!TextHelper.isNull(ordenacao) || responsavel.isSer()) {
                corpoBuilder.append(" order by ").append(recuperaOrdenacao());
            }
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("tisCodigo", tisCodigo, query);

        if (!TextHelper.isNull(adeCodigo)) {
            defineValorClausulaNomeada("adeCodigo", adeCodigo, query);
        }

        defineValorClausulaNomeada("sadCodigos", sadCodigos, query);
        defineValorClausulaNomeada("ssoCodigos", ssoCodigos, query);

        if (!stpCodigos.isEmpty()) {
            defineValorClausulaNomeada("stpCodigos", stpCodigos, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigoProp", csaCodigo, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigoArr", csaCodigo, query);
        }

        if (!TextHelper.isNull(arrRisco)) {
            defineValorClausulaNomeada("arrRisco", arrRisco, query);
        }

        if (!TextHelper.isNull(posCodigo)) {
            defineValorClausulaNomeada("posCodigo", posCodigo, query);
        }

        if (!TextHelper.isNull(cidCodigo)) {
            defineValorClausulaNomeada("cidCodigo", cidCodigo, query);
        }

        if (!TextHelper.isNull(rsePontuacao)) {
            defineValorClausulaNomeada("rsePontuacao", Integer.valueOf(rsePontuacao), query);
        }

        if (responsavel.isSer()) {
            defineValorClausulaNomeada("rseCodigo", responsavel.getRseCodigo(), query);
        } else {
            ListaServidorQuery.definirClausulaMatriculaCpf(rseMatricula, serCpf, !matriculaExata, query);
        }

        if (!TextHelper.isNull(dataAberturaIni)) {
            try {
                defineValorClausulaNomeada("dataAberturaIni", DateHelper.parse(dataAberturaIni + " 00:00:00", LocaleHelper.getDateTimePattern()), query);
            } catch (ParseException ex) {
                throw new HQueryException("mensagem.erro.data.inicio.informada.invalida.arg0", (AcessoSistema) null, dataAberturaIni);
            }
        }
        if (!TextHelper.isNull(dataAberturaFim)) {
            try {
                defineValorClausulaNomeada("dataAberturaFim", DateHelper.parse(dataAberturaFim + " 23:59:59", LocaleHelper.getDateTimePattern()), query);
            } catch (ParseException ex) {
                throw new HQueryException("mensagem.erro.data.fim.informada.invalida.arg0", (AcessoSistema) null, dataAberturaFim);
            }
        }

        if (!TextHelper.isNull(horasFimLeilao)) {
            defineValorClausulaNomeada("horasFimLeilao", horasFimLeilao, query);
        }

        // Exibe somente leilões dentro da validade na listagen de pendentes e expirados
        if (dataAtual != null) {
            defineValorClausulaNomeada("dataAtual", dataAtual, query);
        }

        if (!TextHelper.isNull(rseMargemLivre)) {
            if (!margemLivreIni.equals("0")) {
                defineValorClausulaNomeada("margemLivreIni", Double.valueOf(margemLivreIni), query);
            }
            if (!margemLivreFim.equals("100")) {
                defineValorClausulaNomeada("margemLivreFim", Double.valueOf(margemLivreFim), query);
            }
        }

        return query;
    }

    private String recuperaOrdenacao() {
        String order = "";

        //Caso seja servidor, retorna a lista de solicitações de leião
        //ordenada pela data de criação de forma decrescente.
        if (responsavel.isSer()) {
            return "soa.soaData DESC";
        }

        Map<String, String> ordenacoesPossiveis = new HashMap<>();
        ordenacoesPossiveis.put("ORD01", "soa.soaData");
        ordenacoesPossiveis.put("ORD02", "soa.soaDataValidade");
        ordenacoesPossiveis.put("ORD03", "ser.serNome");
        ordenacoesPossiveis.put("ORD04", "cid.cidNome");
        ordenacoesPossiveis.put("ORD05", "ade.adeNumero");
        ordenacoesPossiveis.put("ORD06", "ade.adeVlrLiquido");
        ordenacoesPossiveis.put("ORD07", "ade.adeVlr");
        ordenacoesPossiveis.put("ORD08", "ade.adePrazo");
        ordenacoesPossiveis.put("ORD09", "rse.rsePontuacao");
        ordenacoesPossiveis.put("ORD10", "prop2.plsTaxaJuros");
        ordenacoesPossiveis.put("ORD11", "min(prop.plsTaxaJuros)");
        ordenacoesPossiveis.put("ORD12", "variacaoMargemLivre, variacaoMargemExtraLivre");
        ordenacoesPossiveis.put("ORD13", "arr.arrRisco");
        ordenacoesPossiveis.put("ORD14", "case when soa.soaDataValidade > data_corrente() then 1 else 2 end");

        List<String> lstOrdenacaoAux = new ArrayList<>();
        if (!TextHelper.isNull(ordenacao)) {
            lstOrdenacaoAux = Arrays.asList(ordenacao.split(","));
        }

        if (lstOrdenacaoAux != null && !lstOrdenacaoAux.isEmpty()) {
            int contador = 0;
            for (String ordernacaoAux : lstOrdenacaoAux) {
                ordernacaoAux = ordernacaoAux.replaceAll("\\[|\\]", "").trim();

                for (String chave : ordenacoesPossiveis.keySet()) {
                    if (ordernacaoAux.equalsIgnoreCase(chave + ";" + ASC)) {
                        order += ordenacoesPossiveis.get(chave).replaceAll(",", " ASC,") + " " + ASC;
                    } else if (ordernacaoAux.equalsIgnoreCase(chave + ";" + DESC)) {
                        order += ordenacoesPossiveis.get(chave).replaceAll(",", " DESC,") + " " + DESC;
                    }
                }

                if (contador < lstOrdenacaoAux.size() - 1 && !TextHelper.isNull(order)) {
                    contador++;
                    order += ", ";
                }
            }
        }

        while (order.trim().endsWith(",")) {
            order = order.substring(0, order.lastIndexOf(","));
        }

        return order;
    }

    @Override
    protected String[] getFields() {
        String[] fields = {
                Columns.SOA_DATA,
                Columns.SOA_DATA_VALIDADE,
                Columns.ADE_CODIGO,
                Columns.ADE_NUMERO,
                Columns.ADE_TIPO_VLR,
                Columns.ADE_VLR_LIQUIDO,
                Columns.ADE_VLR,
                Columns.ADE_PRAZO,
                Columns.CID_NOME,
                Columns.UF_COD,
                Columns.RSE_MATRICULA,
                Columns.RSE_CODIGO,
                Columns.SER_NOME,
                Columns.RSE_PONTUACAO,
                Columns.ARR_RISCO,
                Columns.PLS_TAXA_JUROS,
                "PLS_TAXA_JUROS_MIN",
                Columns.SER_CPF,
                Columns.ADE_INC_MARGEM,
                Columns.SSO_CODIGO,
                "VARIACAO_MARGEM_LIVRE",
                "VARIACAO_MARGEM_EXTRA_LIVRE"
        };

        if (tipoFiltro.equals("2") || tipoFiltro.equals("3") || tipoFiltro.equals("4")) {
            String[] fields2 = Arrays.copyOf(fields, fields.length + 1);
            fields2[fields.length] = Columns.RAD_ADE_CODIGO_DESTINO;
            return fields2;
        }

        return fields;
    }
}
