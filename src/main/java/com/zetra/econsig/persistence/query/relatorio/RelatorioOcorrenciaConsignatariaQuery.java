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
 * <p>Title: RelatorioOcorrenciaConsignatariaQuery</p>
 * <p>Description: Query Relatório de Ocorrência Consignatária</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioOcorrenciaConsignatariaQuery extends ReportHQuery {

    public AcessoSistema responsavel;
    private String csaCodigo;
    private String dataIni;
    private String dataFim;
    private String opLogin;
    private List<String> tpeCodigos;
    private List<String> tocCodigos;

    @Override
    public void setCriterios(TransferObject criterio) {
        csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
        dataIni = (String) criterio.getAttribute("DATA_INI");
        dataFim = (String) criterio.getAttribute("DATA_FIM");
        opLogin = (String) criterio.getAttribute("OP_LOGIN");
        tpeCodigos = (List<String>) criterio.getAttribute("TPE_CODIGO");
        tocCodigos = (List<String>) criterio.getAttribute(Columns.TOC_CODIGO);
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder sql = new StringBuilder();
        String[] opLoginArray = null;

        sql.append("select occ.consignataria.csaCodigo AS CSA_CODIGO, occ.consignataria.csaIdentificador AS CSA_IDENTIFICADOR, ");
        sql.append("occ.consignataria.csaNome AS CSA_NOME, occ.consignataria.csaNomeAbrev AS CSA_NOME_ABREV, ");
        sql.append("concat(concat(occ.consignataria.csaIdentificador, ' - '), ");
        sql.append("case when nullif(trim(occ.consignataria.csaNomeAbrev), '') is null then occ.consignataria.csaNome ");
        sql.append("else occ.consignataria.csaNomeAbrev end) as CONSIGNATARIA, ");
        sql.append("toc.tocDescricao AS TOC_DESCRICAO, tpe.tpeDescricao as TPE_DESCRICAO, occ.occCodigo AS OCC_CODIGO, ");
        sql.append("occ.occData AS OCC_DATA, occ.occObs AS OCC_OBS, usu.usuLogin AS USU_LOGIN ");

        sql.append("from OcorrenciaConsignataria occ ");
        sql.append("inner join occ.tipoOcorrencia toc ");
        sql.append("inner join occ.usuario usu ");
        sql.append("left outer join occ.tipoPenalidade tpe ");

        sql.append("where 1 = 1");
        sql.append(" and occ.occData between :dataIni and :dataFim");

        if (!TextHelper.isNull(csaCodigo)) {
            sql.append(" and occ.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(opLogin)) {
            opLoginArray = TextHelper.split(opLogin.replaceAll(" ", ""), ",");
            sql.append(" and usu.usuLogin ").append(criaClausulaNomeada("opLoginArray", opLoginArray));
        }

        if (tpeCodigos != null && !tpeCodigos.isEmpty()) {
            sql.append(" and occ.tipoPenalidade.tpeCodigo ").append(criaClausulaNomeada("tpeCodigo", tpeCodigos));
        }

        if (tocCodigos != null && !tocCodigos.isEmpty()) {
            sql.append(" and toc.tocCodigo ").append(criaClausulaNomeada("tocCodigo", tocCodigos));
        }

        sql.append(" order by occ.consignataria.csaIdentificador, occ.occData desc");

        Query<Object[]> hQuery = instanciarQuery(session, sql.toString());

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, hQuery);
        }

        if (!TextHelper.isNull(dataIni)) {
            defineValorClausulaNomeada("dataIni", parseDateTimeString(dataIni), hQuery);
        }

        if (!TextHelper.isNull(dataFim)) {
            defineValorClausulaNomeada("dataFim", parseDateTimeString(dataFim), hQuery);
        }

        if (!TextHelper.isNull(opLogin)) {
            defineValorClausulaNomeada("opLoginArray", opLoginArray, hQuery);
        }

        if (tpeCodigos != null && !tpeCodigos.isEmpty()) {
            defineValorClausulaNomeada("tpeCodigo", tpeCodigos, hQuery);
        }

        if (tocCodigos != null && !tocCodigos.isEmpty()) {
            defineValorClausulaNomeada("tocCodigo", tocCodigos, hQuery);
        }

        return hQuery;
    }
}
