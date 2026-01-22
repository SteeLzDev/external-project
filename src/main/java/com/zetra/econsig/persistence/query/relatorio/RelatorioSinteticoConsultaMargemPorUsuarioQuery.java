package com.zetra.econsig.persistence.query.relatorio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.Columns;

public class RelatorioSinteticoConsultaMargemPorUsuarioQuery extends ReportHQuery {

    public String csaCodigo;
    public List<String> corCodigos;
    public String dataIni;
    public String dataFim;

    @SuppressWarnings("unchecked")
    @Override
    public void setCriterios(TransferObject criterio) {
        csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
        corCodigos = (List<String>) criterio.getAttribute(Columns.COR_CODIGO);
        dataIni = (String) criterio.getAttribute("DATA_INI");
        dataFim = (String) criterio.getAttribute("DATA_FIM");
    }

    @Override
    public Query<Object[]> preparar(Session session) {

        StringBuilder hql = new StringBuilder();

        hql.append(" SELECT usu.usuNome AS nomeUsuario, usu.usuLogin AS login, csa.csaNome AS consignataria, to_numeric(count(*)) AS quantidade, ");

        if(corCodigos == null || corCodigos.isEmpty() || corCodigos.contains("")){
            hql.append(" '' AS correspondente ");
        } else {
            hql.append(" cor.corNome AS correspondente ");
        }

        hql.append(" FROM Usuario usu");
        hql.append(" INNER JOIN usu.historicoConsultaMargemSet hcm");

        if(corCodigos == null || corCodigos.isEmpty() || corCodigos.contains("")){
            hql.append(" INNER JOIN usu.usuarioCsaSet usuCsa");
            hql.append(" INNER JOIN usuCsa.consignataria csa");
        } else {
            hql.append(" INNER JOIN usu.usuarioCorSet usuCor");
            hql.append(" INNER JOIN usuCor.correspondente cor");
            hql.append(" INNER JOIN cor.consignataria csa");
        }

        hql.append(" WHERE 1=1 ");

        if (!TextHelper.isNull(dataIni) && !TextHelper.isNull(dataFim)) {
            hql.append(" AND hcm.hcmData BETWEEN :dataIni AND :dataFim ");
        }

        if (!TextHelper.isNull(csaCodigo)) {
            hql.append(" AND csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if ((corCodigos != null && !corCodigos.isEmpty()) && !corCodigos.contains("-1") && !corCodigos.contains("")) {
            hql.append(" AND cor.corCodigo ").append(criaClausulaNomeada("corCodigo", corCodigos));
        }

        hql.append(" GROUP BY usu.usuNome, usu.usuLogin, csa.csaNome ");

        if (corCodigos != null && !corCodigos.isEmpty() && !corCodigos.contains("")) {
            hql.append(", cor.corNome ");
        }

        hql.append(" ORDER BY quantidade desc, nomeUsuario asc ");

        Query<Object[]> query = instanciarQuery(session, hql.toString());

        if (!TextHelper.isNull(dataIni) && !TextHelper.isNull(dataFim)) {
            defineValorClausulaNomeada("dataIni", parseDateTimeString(dataIni), query);
            defineValorClausulaNomeada("dataFim", parseDateTimeString(dataFim), query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if ((corCodigos != null && !corCodigos.isEmpty()) && !corCodigos.contains("-1") && !corCodigos.contains("")) {
            defineValorClausulaNomeada("corCodigo", corCodigos, query);
        }

        return query;
    }
}