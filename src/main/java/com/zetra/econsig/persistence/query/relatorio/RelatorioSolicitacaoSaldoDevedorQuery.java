package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.TipoSolicitacaoEnum;

import java.util.List;

/**
 * <p> Title: RelatorioConsignatariasQuery</p>
 * <p> Description: Relatório de cadastro de Consignatárias.</p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSolicitacaoSaldoDevedorQuery extends ReportHQuery {

    public String tipoSolicitacao;
    public String periodoIni;
    public String periodoFim;
    public String adeNumero;
    public String rseMatricula;
    public String serCpf;
    public String estCodigo;
    public List<String> orgCodigos;
    public String csaCodigo;

    @Override
    public void setCriterios(TransferObject criterio) {
        tipoSolicitacao = (String) criterio.getAttribute("tipoSolicitacao");
        periodoIni = (String) criterio.getAttribute("periodoIni");
        periodoFim = (String) criterio.getAttribute("periodoFim");
        adeNumero = (String) criterio.getAttribute("adeNumero");
        rseMatricula = (String) criterio.getAttribute("rseMatricula");
        serCpf = (String) criterio.getAttribute("serCpf");
        estCodigo = (String) criterio.getAttribute("estCodigo");
        orgCodigos = (List<String>) criterio.getAttribute("orgCodigo");
        csaCodigo = (String) criterio.getAttribute("csaCodigo");
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String[] tisCodigo = null;
        if (TextHelper.isNull(tipoSolicitacao)) {
            tisCodigo = new String[] { TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR.getCodigo(), TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR_LIQUIDACAO.getCodigo() };
        } else {
            tisCodigo = new String[] { tipoSolicitacao };
        }

        String corpo = "SELECT rse.rseMatricula as rse_matricula," +
                "ser.serCpf as ser_cpf," +
                "ser.serNome as ser_nome," +
                "ade.adeNumero as ade_numero," +
                "ade.adeVlr as ade_vlr," +
                "coalesce(str(ade.adePrazo), '" + ApplicationResourcesHelper.getMessage("rotulo.indeterminado", AcessoSistema.getAcessoUsuarioSistema()) + "') as ade_prazo, " +
                "coalesce(str(ade.adePrdPagas), '0') as ade_prd_pagas, " +
                "svc.svcDescricao as svc_descricao," +
                "csa.csaNomeAbrev as csa_nome_abrev," +
                "soa.soaData as data_solicitacao," +
                "sso.ssoDescricao as status, " +
                "sdv.sdvValor as saldo_devedor," +
                "sdv.sdvDataMod as data_informacao";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" FROM AutDesconto ade");
        corpoBuilder.append(" INNER JOIN ade.registroServidor rse");
        corpoBuilder.append(" INNER JOIN rse.servidor ser");
        corpoBuilder.append(" INNER JOIN ade.verbaConvenio vco");
        corpoBuilder.append(" INNER JOIN vco.convenio cnv");
        corpoBuilder.append(" INNER JOIN cnv.consignataria csa");
        corpoBuilder.append(" INNER JOIN cnv.servico svc");
        corpoBuilder.append(" INNER JOIN cnv.orgao org");
        corpoBuilder.append(" INNER JOIN org.estabelecimento est");
        corpoBuilder.append(" INNER JOIN ade.solicitacaoAutorizacaoSet soa");
        corpoBuilder.append(" INNER JOIN soa.statusSolicitacao sso");
        corpoBuilder.append(" LEFT JOIN ade.saldoDevedorSet sdv");
        corpoBuilder.append(" WHERE soa.tipoSolicitacao.tisCodigo ").append(criaClausulaNomeada("tisCodigo", tisCodigo));

        if (!TextHelper.isNull(periodoIni) && !TextHelper.isNull(periodoFim)) {
            corpoBuilder.append(" AND soa.soaData BETWEEN :periodoIni AND :periodoFim ");
        }

        // Não existe uma solicitação mais nova, listando assim sempre a última
        corpoBuilder.append(" AND NOT EXISTS (");
        corpoBuilder.append(" SELECT 1 FROM ade.solicitacaoAutorizacaoSet soa2");
        corpoBuilder.append(" WHERE soa2.tipoSolicitacao.tisCodigo ").append(criaClausulaNomeada("tisCodigo", tisCodigo));
        corpoBuilder.append(" AND soa2.soaData > soa.soaData");
        if (!TextHelper.isNull(periodoIni) && !TextHelper.isNull(periodoFim)) {
            corpoBuilder.append(" AND soa2.soaData BETWEEN :periodoIni AND :periodoFim ");
        }
        corpoBuilder.append(" )");


        if (!TextHelper.isNull(adeNumero)) {
            corpoBuilder.append(" AND ade.adeNumero = :adeNumero ");
        }

        if (!TextHelper.isNull(rseMatricula)) {
            corpoBuilder.append(" AND rse.rseMatricula = :rseMatricula ");
        }

        if (!TextHelper.isNull(serCpf)) {
            corpoBuilder.append(" AND ser.serCpf = :serCpf ");
        }

        if (!TextHelper.isNull(estCodigo)) {
            corpoBuilder.append(" AND est.estCodigo = :estCodigo ");
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" AND org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND csa.csaCodigo = :csaCodigo ");
        }

        corpoBuilder.append(" GROUP BY rse.rseMatricula, ser.serCpf, ser.serNome, ade.adeNumero, ade.adeVlr, ade.adePrazo, ade.adePrdPagas, svc.svcDescricao, csa.csaNomeAbrev, soa.soaData, sso.ssoDescricao, sdv.sdvValor, sdv.sdvDataMod");
        corpoBuilder.append(" ORDER BY rse.rseMatricula, ade.adeNumero, status desc");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("tisCodigo", tisCodigo, query);

        if (!TextHelper.isNull(periodoIni) && !TextHelper.isNull(periodoFim)) {
            defineValorClausulaNomeada("periodoIni", parseDateString(periodoIni), query);
            defineValorClausulaNomeada("periodoFim", parseDateString(periodoFim), query);
        }

        if (!TextHelper.isNull(adeNumero)) {
            defineValorClausulaNomeada("adeNumero", adeNumero, query);
        }

        if (!TextHelper.isNull(rseMatricula)) {
            defineValorClausulaNomeada("rseMatricula", rseMatricula, query);
        }

        if (!TextHelper.isNull(serCpf)) {
            defineValorClausulaNomeada("serCpf", serCpf, query);
        }

        if (!TextHelper.isNull(estCodigo)) {
            defineValorClausulaNomeada("estCodigo", estCodigo, query);
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }
}
