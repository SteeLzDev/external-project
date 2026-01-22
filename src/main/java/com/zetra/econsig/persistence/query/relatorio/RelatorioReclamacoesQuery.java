package com.zetra.econsig.persistence.query.relatorio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioReclamacoesQuery</p>
 * <p>Description: Query para relatório de reclamações do registro servidor.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioReclamacoesQuery extends ReportHQuery {

    private String dataIni;
    private String dataFim;
    private String estCodigo;
    private List<String> orgCodigos;
    private String csaCodigo;
    private List<String> tmrCodigos;

    @Override
    public void setCriterios(TransferObject criterio) {
        dataIni = (String) criterio.getAttribute("DATA_INI");
        dataFim = (String) criterio.getAttribute("DATA_FIM");
        estCodigo = (String) criterio.getAttribute(Columns.EST_CODIGO);
        orgCodigos = (List<String>) criterio.getAttribute(Columns.ORG_CODIGO);
        csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
        tmrCodigos = (List<String>) criterio.getAttribute(Columns.TMR_CODIGO);
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append(" select rrs.rrsCodigo AS RRS_CODIGO, ");
        corpoBuilder.append(" tmr.tmrCodigo AS TMR_CODIGO, tmr.tmrDescricao AS TMR_DESCRICAO, ");
        corpoBuilder.append(" rse.rseMatricula AS RSE_MATRICULA, ");
        corpoBuilder.append(" concat(concat(rse.rseMatricula, ' - '),ser.serNome) AS SERVIDOR, ");
        corpoBuilder.append(" ser.serNome AS SER_NOME, ser.serCpf AS SER_CPF, ");
        corpoBuilder.append(" concat(concat(csa.csaIdentificador, ' - '), ");
        corpoBuilder.append(" case when nullif(trim(csa.csaNomeAbrev), '') is null then csa.csaNome ");
        corpoBuilder.append(" else csa.csaNomeAbrev end) AS CONSIGNATARIA, ");
        corpoBuilder.append(" rrs.csaCodigo AS CSA_CODIGO, csa.csaIdentificador AS CSA_IDENTIFICADOR, csa.csaNome AS CSA_NOME, ");
        corpoBuilder.append(" rrs.rrsData AS RRS_DATA, substituir(rrs.rrsTexto, '\r\n', '') AS RRS_TEXTO, rrs.rrsIpAcesso AS RRS_IP_ACESSO ");
        corpoBuilder.append(" from ReclamacaoRegistroSer rrs ");
        corpoBuilder.append(" inner join rrs.registroServidor rse ");
        corpoBuilder.append(" inner join rse.servidor ser ");
        corpoBuilder.append(" inner join rrs.consignataria csa ");
        corpoBuilder.append(" inner join rrs.reclamacaoMotivoSet reclamacaoMotivo ");
        corpoBuilder.append(" inner join reclamacaoMotivo.tipoMotivoReclamacao tmr ");

        corpoBuilder.append(" where rrs.rrsData between :dataIni and :dataFim");

        if (!TextHelper.isNull(estCodigo)) {
            corpoBuilder.append(" and rse.orgao.estabelecimento.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigo));
        }

        if ((orgCodigos != null) && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" and rse.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if ((tmrCodigos != null) && !tmrCodigos.isEmpty()) {
            corpoBuilder.append(" and tmr.tmrCodigo ").append(criaClausulaNomeada("tmrCodigos", tmrCodigos));
        }

        corpoBuilder.append(" order by rrs.csaCodigo, rrs.rrsCodigo, tmr.tmrCodigo, rrs.rrsData ");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("dataIni", parseDateTimeString(dataIni), query);
        defineValorClausulaNomeada("dataFim", parseDateTimeString(dataFim), query);

        if (!TextHelper.isNull(estCodigo)) {
            defineValorClausulaNomeada("estCodigo", estCodigo, query);
        }

        if ((orgCodigos != null) && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if ((tmrCodigos != null) && !tmrCodigos.isEmpty()) {
            defineValorClausulaNomeada("tmrCodigos", tmrCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {"RRS_CODIGO",
                "TMR_CODIGO",
                "TMR_DESCRICAO",
                "RSE_MATRICULA",
                "SERVIDOR",
                "SER_NOME",
                "SER_CPF",
                "CONSIGNATARIA",
                "CSA_IDENTIFICADOR",
                "CSA_NOME",
                "RRS_DATA",
                "RRS_TEXTO",
        "RRS_IP_ACESSO"};
    }
}
