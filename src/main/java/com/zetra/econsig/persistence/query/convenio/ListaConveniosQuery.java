package com.zetra.econsig.persistence.query.convenio;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConveniosQuery</p>
 * <p>Description: lista de convênios
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConveniosQuery extends HQuery {
    public String cnvCodVerba;
    public String csaCodigo;
    public String svcCodigo;
    public String orgCodigo;
    private List<String> svcCodigos;
    public boolean ativo;
    public boolean correspondenteConvenio;
    public List<String> cnvCodigos;
    public boolean svcAtivo;
    public String nseCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo =
            "select " +
            "cnv.cnvCodigo, " +
            "cnv.cnvCodVerba, " +
            "cnv.cnvCodVerbaRef, " +
            "cnv.cnvCodVerbaFerias, " +
            "org.orgCodigo, " +
            "csa.csaCodigo, " +
            "svc.svcCodigo, " +
            "cnv.statusConvenio.scvCodigo, " +
            "cnv.cnvDescricao, " +
            "cnv.cnvIdentificador, " +
            "org.orgIdentificador, " +
            "org.orgNome, " +
            "org.orgNomeAbrev, " +
            "csa.csaIdentificador, " +
            "csa.csaNome, " +
            "csa.csaNomeAbrev, " +
            "svc.svcIdentificador, " +
            "svc.svcDescricao, " +
            "svc.svcCodigo ";

        if (correspondenteConvenio) {
            corpo = corpo + ", crc.correspondente.corCodigo ";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from Convenio cnv");
        corpoBuilder.append(" inner join cnv.orgao org");
        corpoBuilder.append(" inner join cnv.consignataria csa");
        corpoBuilder.append(" inner join cnv.servico svc");

        if (correspondenteConvenio) {
            corpoBuilder.append(" inner join cnv.correspondenteConvenioSet crc");
        }

        corpoBuilder.append(" where 1=1 ");

        if (ativo) {
            corpoBuilder.append(" AND cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");

            if (correspondenteConvenio) {
                corpoBuilder.append(" AND crc.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
            }
            if (svcAtivo) {
            	corpoBuilder.append(" AND svc.svcAtivo = ").append(CodedValues.STS_ATIVO);
            }
        }

        if (!TextHelper.isNull(cnvCodVerba)) {
            corpoBuilder.append(" AND cnv.cnvCodVerba ").append(criaClausulaNomeada("cnvCodVerba",cnvCodVerba));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo",csaCodigo));
        }

        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" AND org.orgCodigo ").append(criaClausulaNomeada("orgCodigo",orgCodigo));
        }

        if (!TextHelper.isNull(svcCodigo)) {
            corpoBuilder.append(" AND svc.svcCodigo ").append(criaClausulaNomeada("svcCodigo",svcCodigo));
        } else if (svcCodigos != null && !svcCodigos.isEmpty()) {
            corpoBuilder.append(" AND svc.svcCodigo ").append(criaClausulaNomeada("svcCodigos", svcCodigos));
        }

        if (cnvCodigos != null && !cnvCodigos.isEmpty()) {
            corpoBuilder.append(" AND cnv.cnvCodigo ").append(criaClausulaNomeada("cnvCodigos", cnvCodigos));
        }

        if (!TextHelper.isNull(nseCodigo)) {
            corpoBuilder.append(" AND svc.nseCodigo ").append(criaClausulaNomeada("nseCodigo",nseCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(cnvCodVerba)) {
            defineValorClausulaNomeada("cnvCodVerba", cnvCodVerba, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        } else if (svcCodigos != null && !svcCodigos.isEmpty()) {
            defineValorClausulaNomeada("svcCodigos", svcCodigos, query);
        }

        if (cnvCodigos != null && !cnvCodigos.isEmpty()) {
            defineValorClausulaNomeada("cnvCodigos", cnvCodigos, query);
        }

        if (!TextHelper.isNull(nseCodigo)) {
            defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        List<String> campos = new ArrayList<>();
        campos.add(Columns.CNV_CODIGO);
        campos.add(Columns.CNV_COD_VERBA);
        campos.add(Columns.CNV_COD_VERBA_REF);
        campos.add(Columns.CNV_COD_VERBA_FERIAS);
        campos.add(Columns.CNV_ORG_CODIGO);
        campos.add(Columns.CNV_CSA_CODIGO);
        campos.add(Columns.CNV_SVC_CODIGO);
        campos.add(Columns.CNV_SCV_CODIGO);
        campos.add(Columns.CNV_DESCRICAO);
        campos.add(Columns.CNV_IDENTIFICADOR);
        campos.add(Columns.ORG_IDENTIFICADOR);
        campos.add(Columns.ORG_NOME);
        campos.add(Columns.ORG_NOME_ABREV);
        campos.add(Columns.CSA_IDENTIFICADOR);
        campos.add(Columns.CSA_NOME);
        campos.add(Columns.CSA_NOME_ABREV);
        campos.add(Columns.SVC_IDENTIFICADOR);
        campos.add(Columns.SVC_DESCRICAO);
        campos.add(Columns.SVC_CODIGO);
        if (correspondenteConvenio) {
            campos.add(Columns.CRC_COR_CODIGO);
        }

        return campos.toArray(new String[0]);
    }

    /**
     * Se for passada uma lista de TransferObjects, transforma em uma lista de códigos de servicos.
     * @param servicos
     */
    public void setServicos(List<String> servicos) {
        if (servicos != null && !servicos.isEmpty()) {
            svcCodigos = servicos;
        }
    }
}