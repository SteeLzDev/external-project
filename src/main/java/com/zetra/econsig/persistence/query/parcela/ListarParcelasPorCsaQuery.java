package com.zetra.econsig.persistence.query.parcela;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarParcelasPorCsaQuery</p>
 * <p>Description: Classe responsavel por buscar todas as parcelas de determinada consignataria e demais critérios
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */

public class ListarParcelasPorCsaQuery extends HQuery {
    public AcessoSistema responsavel;

    public boolean parcelaDescontoPeriodo = false;
    public String csaCodigo;
    public Long adeNumero;
    public String adeIdentificador;
    public List<String> spdCodigos;
    public String serCpf;
    public String svcIdentificador;
    public Date prdDataDesconto;
    public String rseMatricula;
    public String estCodigo;
    public String orgCodigo;
    public String cnvCodVerba;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpo = new StringBuilder();

        corpo.append("SELECT ade.adeNumero,ade.adeIdentificador,svc.svcDescricao, svc.svcIdentificador, cnv.cnvCodVerba, ser.serNome, ser.serCpf, rse.rseMatricula, prd.prdNumero, prd.prdDataDesconto, prd.prdDataRealizado, prd.prdVlrPrevisto, prd.prdVlrRealizado, spd.spdDescricao, ocp.ocpObs ");

        if (parcelaDescontoPeriodo) {
            corpo.append(" FROM ParcelaDescontoPeriodo prd ");
            corpo.append(" LEFT OUTER JOIN prd.ocorrenciaParcelaPeriodoSet ocp ");
        } else {
            corpo.append(" FROM ParcelaDesconto prd ");
            corpo.append(" LEFT OUTER JOIN prd.ocorrenciaParcelaSet ocp ");
        }

        corpo.append(" INNER JOIN prd.statusParcelaDesconto spd ");
        corpo.append(" INNER JOIN prd.autDesconto ade ");
        corpo.append(" INNER JOIN ade.verbaConvenio vco ");
        corpo.append(" INNER JOIN vco.convenio cnv ");
        corpo.append(" INNER JOIN cnv.consignataria csa ");
        corpo.append(" INNER JOIN cnv.servico svc ");
        corpo.append(" INNER JOIN cnv.orgao org ");
        corpo.append(" INNER JOIN org.estabelecimento est ");
        corpo.append(" INNER JOIN ade.registroServidor rse ");
        corpo.append(" INNER JOIN rse.servidor ser ");

        corpo.append(" WHERE 1 = 1 ");
        
        if (!TextHelper.isNull(svcIdentificador)) {
            corpo.append(" AND svc.svcIdentificador ").append(criaClausulaNomeada("svcIdentificador", svcIdentificador));
        }
        
        if (!TextHelper.isNull(prdDataDesconto)) {
            corpo.append(" AND prd.prdDataDesconto ").append(criaClausulaNomeada("prdDataDesconto", prdDataDesconto));
        }
        
        if (!TextHelper.isNull(estCodigo)) {
            corpo.append(" AND est.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigo));
        }
        
        if (!TextHelper.isNull(orgCodigo)) {
            corpo.append(" AND org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }
        
        if (!TextHelper.isNull(cnvCodVerba)) {
            corpo.append(" AND cnv.cnvCodVerba ").append(criaClausulaNomeada("cnvCodVerba", cnvCodVerba));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpo.append(" AND csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(adeNumero)) {
            corpo.append(" AND ade.adeNumero ").append(criaClausulaNomeada("adeNumero", adeNumero));
        }

        if (!TextHelper.isNull(adeIdentificador)) {
            corpo.append(" AND ade.adeIdentificador ").append(criaClausulaNomeada("adeIdentificador", adeIdentificador));
        }

        if ((spdCodigos != null) && !spdCodigos.isEmpty()) {
            corpo.append(" AND spd.spdCodigo ").append(criaClausulaNomeada("spdCodigo", spdCodigos));
        }

        if (!TextHelper.isNull(serCpf)) {
            corpo.append(" AND ser.serCpf ").append(criaClausulaNomeada("serCpf", serCpf));
        }

        if (!TextHelper.isNull(rseMatricula)) {
            corpo.append(" AND rse.rseMatricula ").append(criaClausulaNomeada("rseMatricula", rseMatricula));
        }

        final Query<Object[]> query = instanciarQuery(session, corpo.toString());
        
        if (!TextHelper.isNull(svcIdentificador)) {
            defineValorClausulaNomeada("svcIdentificador", svcIdentificador, query);
        }
        
        if (!TextHelper.isNull(prdDataDesconto)) {
            defineValorClausulaNomeada("prdDataDesconto", prdDataDesconto, query);
        }
        
        if (!TextHelper.isNull(estCodigo)) {
            defineValorClausulaNomeada("estCodigo", estCodigo, query);
        }
        
        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }
        
        if (!TextHelper.isNull(cnvCodVerba)) {
            defineValorClausulaNomeada("cnvCodVerba", cnvCodVerba, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(adeNumero)) {
            defineValorClausulaNomeada("adeNumero", adeNumero, query);
        }

        if (!TextHelper.isNull(adeIdentificador)) {
            defineValorClausulaNomeada("adeIdentificador", adeIdentificador, query);
        }

        if ((spdCodigos != null) && !spdCodigos.isEmpty()) {
            defineValorClausulaNomeada("spdCodigo", spdCodigos, query);
        }

        if (!TextHelper.isNull(serCpf)) {
            defineValorClausulaNomeada("serCpf", serCpf, query);
        }

        if (!TextHelper.isNull(rseMatricula)) {
            defineValorClausulaNomeada("rseMatricula", rseMatricula, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
             Columns.ADE_NUMERO,
             Columns.ADE_IDENTIFICADOR,
             Columns.SVC_DESCRICAO,
             Columns.SVC_IDENTIFICADOR,
             Columns.CNV_COD_VERBA,
             Columns.SER_NOME,
             Columns.SER_CPF,
             Columns.RSE_MATRICULA,
             Columns.PRD_NUMERO,
             Columns.PRD_DATA_DESCONTO,
             Columns.PRD_DATA_REALIZADO,
             Columns.PRD_VLR_PREVISTO,
             Columns.PRD_VLR_REALIZADO,
             Columns.SPD_DESCRICAO,
             Columns.OCP_OBS
        };
    }
}
