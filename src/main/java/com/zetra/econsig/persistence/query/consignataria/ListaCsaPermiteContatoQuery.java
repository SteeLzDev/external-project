package com.zetra.econsig.persistence.query.consignataria;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCsaPermiteContatoQuery</p>
 * <p>Description: Querys de listagem das informacoes da csa que permitem ser contactada</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 */

public class ListaCsaPermiteContatoQuery extends HQuery{
    public List<String> csaCodigos;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {

        if ((csaCodigos != null) && (csaCodigos.size() > 1)) {
            final Set<String> csaCodigosSemRepeticao = new HashSet<>(csaCodigos);
            csaCodigos = new ArrayList<>();
            csaCodigos.addAll(csaCodigosSemRepeticao);
        }

        final StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select csa.csaCodigo, csa.csaWhatsapp, csa.csaEmailContato, csa.csaEmail, csa.csaTel, tpa.pcsVlr, csa.csaTxtContato ");
        corpoBuilder.append("from Consignataria csa ");
        corpoBuilder.append("inner join csa.paramConsignatariaSet tpa ");
        corpoBuilder.append("where tpa.tpaCodigo = '").append(CodedValues.TPA_PERMITE_SER_CONTATACDA_WHATSAPP_EMAIL_TELEFONE).append("' ");
        corpoBuilder.append("and tpa.pcsVlr != '").append(CodedValues.TPA_NAO_PERMITE_CONTATO).append("' ");

        if ((csaCodigos != null) && !csaCodigos.isEmpty()) {
            corpoBuilder.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigos", csaCodigos));
        }

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if ((csaCodigos != null) && !csaCodigos.isEmpty()) {
            defineValorClausulaNomeada("csaCodigos", csaCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSA_CODIGO,
                Columns.CSA_WHATSAPP,
                Columns.CSA_EMAIL_CONTATO,
                Columns.CSA_EMAIL,
                Columns.CSA_TEL,
                Columns.PCS_VLR,
                Columns.CSA_TXT_CONTATO
                };
    }
}
