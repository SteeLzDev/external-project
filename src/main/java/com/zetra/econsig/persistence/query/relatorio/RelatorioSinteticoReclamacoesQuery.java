package com.zetra.econsig.persistence.query.relatorio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioSinteticoReclamacoesQuery</p>
 * <p>Description: Relatório sintético de reclamações.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSinteticoReclamacoesQuery extends ReportHQuery {

    public AcessoSistema responsavel;

    public String dataInicio;

    public String dataFim;

    public String csaCodigo;

    private String estCodigo;

    private List<String> orgCodigos;

    public List<String> tmrCodigos;

    public String fields[];

    @Override
    public void setCriterios(TransferObject criterio) {
        dataInicio = (String) criterio.getAttribute("DATA_INI");
        dataFim = (String) criterio.getAttribute("DATA_FIM");
        csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
        tmrCodigos = (List<String>) criterio.getAttribute(Columns.TMR_CODIGO);
        estCodigo = (String) criterio.getAttribute(Columns.EST_CODIGO);
        orgCodigos = (List<String>) criterio.getAttribute(Columns.ORG_CODIGO);
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT ");
        sql.append(" rrs.consignataria.csaIdentificador as csaIdentificador, ");
        sql.append(" rrs.consignataria.csaCodigo as csaCodigo, ");
        sql.append(" rrs.consignataria.csaNome as csa, ");
        sql.append(" tm.tmrDescricao as motivo, ");
        sql.append(" count(*) as qtde ");

        sql.append(" FROM ReclamacaoRegistroSer rrs ");
        sql.append(" JOIN rrs.reclamacaoMotivoSet rm ");
        sql.append(" JOIN rm.tipoMotivoReclamacao tm ");
        sql.append(" JOIN rrs.registroServidor rs ");
        sql.append(" JOIN rs.orgao o ");
        sql.append(" JOIN o.estabelecimento e ");

        sql.append(" WHERE ");
        sql.append(" rrs.rrsData BETWEEN :dataInicio AND :dataFim ");

        if (!TextHelper.isNull(csaCodigo)) {
            sql.append(" AND rrs.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (tmrCodigos != null && tmrCodigos.size() > 0) {
            sql.append(" AND tm.tmrCodigo ").append(criaClausulaNomeada("tmrCodigos", tmrCodigos));
        }

        if (!TextHelper.isNull(estCodigo)) {
            sql.append(" AND e.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigo));
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            sql.append(" AND o.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        sql.append(" GROUP BY rrs.consignataria.csaIdentificador, rrs.consignataria.csaCodigo, rrs.consignataria.csaNome, tm.tmrDescricao ");

        sql.append(" ORDER BY csa, motivo");

        Query<Object[]> query = instanciarQuery(session, sql.toString());

        defineValorClausulaNomeada("dataInicio", parseDateTimeString(dataInicio), query);
        defineValorClausulaNomeada("dataFim", parseDateTimeString(dataFim), query);

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (tmrCodigos != null && tmrCodigos.size() > 0) {
            defineValorClausulaNomeada("tmrCodigos", tmrCodigos, query);
        }

        if (!TextHelper.isNull(estCodigo)) {
            defineValorClausulaNomeada("estCodigo", estCodigo, query);
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }

        fields = new String[5];
        fields[0] = "csaIdentificador";
        fields[1] = "csaCodigo";
        fields[2] = "csa";
        fields[3] = "motivo";
        fields[4] = "qtde";

        return query;
    }

    @Override
    public String[] getFields() {
        return fields;
    }
}
