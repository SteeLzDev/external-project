package com.zetra.econsig.persistence.query.beneficios.contrato;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaLancamentosContratosBeneficiosQuery</p>
 * <p>Description: Lista lançamentos de contratos de benefícios</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaLancamentosContratosBeneficiosQuery extends HQuery {

    public String cbeCodigo;
    public Date prdDataDesconto;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {



        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT tla.tlaDescricao, ");
        corpoBuilder.append("rse.rseMatricula, ");
        corpoBuilder.append("ser.serNome, ");
        corpoBuilder.append("bfc.bfcNome, ");
        corpoBuilder.append("bfc.bfcCpf, ");
        corpoBuilder.append("tib.tibDescricao, ");
        corpoBuilder.append("grp.grpDescricao, ");
        corpoBuilder.append("bfc.bfcDataNascimento, ");
        corpoBuilder.append("ade.adeNumero, ");
        corpoBuilder.append("csa.csaNome, ");
        corpoBuilder.append("csa.csaIdentificador, ");
        corpoBuilder.append("ben.benDescricao, ");
        corpoBuilder.append("ben.benCodigoContrato, ");
        corpoBuilder.append("ben.benCodigoRegistro, ");
        corpoBuilder.append("ben.benCodigoPlano, ");
        corpoBuilder.append("cbe.cbeNumero, ");
        corpoBuilder.append("cbe.cbeDataInicioVigencia, ");
        corpoBuilder.append("cbe.cbeDataFimVigencia, ");
        corpoBuilder.append("ade.adeData, ");
        corpoBuilder.append("prd.prdDataDesconto, ");
        corpoBuilder.append("prd.prdVlrPrevisto, ");
        corpoBuilder.append("prd.prdVlrRealizado, ");
        corpoBuilder.append("spd.spdDescricao ");
        corpoBuilder.append(" FROM ContratoBeneficio cbe ");
        corpoBuilder.append(" INNER JOIN cbe.autDescontoSet ade ");
        corpoBuilder.append(" INNER JOIN ade.tipoLancamento tla ");
        corpoBuilder.append(" INNER JOIN cbe.beneficiario bfc ");
        corpoBuilder.append(" INNER JOIN ade.registroServidor rse ");
        corpoBuilder.append(" INNER JOIN cbe.beneficio ben ");
        corpoBuilder.append(" INNER JOIN ben.consignataria csa ");
        corpoBuilder.append(" LEFT JOIN bfc.grauParentesco grp ");
        corpoBuilder.append(" INNER JOIN rse.servidor ser ");
        corpoBuilder.append(" INNER JOIN bfc.tipoBeneficiario tib ");
        corpoBuilder.append(" INNER JOIN ade.parcelaDescontoSet prd ");
        corpoBuilder.append(" INNER JOIN prd.statusParcelaDesconto spd ");

        corpoBuilder.append(" WHERE cbe.cbeCodigo = :cbeCodigo ");
        corpoBuilder.append(" AND tla.tlaCodigo is not NULL");

        if(!TextHelper.isNull(prdDataDesconto)) {
            corpoBuilder.append(" AND prd.prdDataDesconto = :prdDataDesconto ");
        }
        corpoBuilder.append(" ORDER BY prd.prdDataRealizado DESC");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("cbeCodigo", cbeCodigo, query);

        if(!TextHelper.isNull(prdDataDesconto)) {
            defineValorClausulaNomeada("prdDataDesconto", prdDataDesconto, query);
        }
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TLA_DESCRICAO,
                Columns.RSE_MATRICULA,
                Columns.SER_NOME,
                Columns.BFC_NOME,
                Columns.BFC_CPF,
                Columns.TIB_DESCRICAO,
                Columns.GRP_DESCRICAO,
                Columns.BFC_DATA_NASCIMENTO,
                Columns.ADE_NUMERO,
                Columns.CSA_NOME,
                Columns.CSA_IDENTIFICADOR,
                Columns.BEN_DESCRICAO,
                Columns.BEN_CODIGO_CONTRATO,
                Columns.BEN_CODIGO_REGISTRO,
                Columns.BEN_CODIGO_PLANO,
                Columns.CBE_NUMERO,
                Columns.CBE_DATA_INICIO_VIGENCIA,
                Columns.CBE_DATA_FIM_VIGENCIA,
                Columns.ADE_DATA,
                Columns.PRD_DATA_DESCONTO,
                Columns.PRD_VLR_PREVISTO,
                Columns.PRD_VLR_REALIZADO,
                Columns.SPD_DESCRICAO
        };
    }
}