package com.zetra.econsig.persistence.query.movimento;

import java.sql.Date;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.Columns;

public class ListaMovimentoFinanceiroQuery extends HNativeQuery {

    public Date periodo;
    public String rseMatricula;
    public String serCpf;
    public String orgIdentificador;
    public String estIdentificador;
    public String csaIdentificador;
    public String svcIdentificador;
    public String cnvCodVerba;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo = "select " +
                       "arm.pex_periodo, " +
                       "ser.ser_nome, " +
                       "arm.ser_cpf, " +
                       "arm.rse_matricula, " +
                       "arm.est_identificador, " +
                       "est.est_nome, " +
                       "arm.org_identificador, " +
                       "org.org_nome, " +
                       "arm.csa_identificador, " +
                       "csa.csa_nome, " +
                       "arm.svc_identificador, " +
                       "svc.svc_descricao, " +
                       "arm.cnv_cod_verba, " +
                       "arm.ade_data, " +
                       "arm.ade_ano_mes_ini, " +
                       "arm.ade_ano_mes_fim, " +
                       "arm.ade_vlr, " +
                       "arm.ade_prazo, " +
                       "ade.ade_prd_pagas, " +
                       "arm.ade_numero, " +
                       "arm.ade_indice, " +
                       "arm.arm_situacao ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from tb_arquivo_movimento arm ");

        corpoBuilder.append(" inner join tb_aut_desconto ade on (arm.ade_numero = ade.ade_numero) ");
        corpoBuilder.append(" inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
        corpoBuilder.append(" inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
        corpoBuilder.append(" inner join tb_servico svc on (cnv.svc_codigo = svc.svc_codigo) ");
        corpoBuilder.append(" inner join tb_consignataria csa on (cnv.csa_codigo = csa.csa_codigo) ");
        corpoBuilder.append(" inner join tb_orgao org on (cnv.org_codigo = org.org_codigo) ");
        corpoBuilder.append(" inner join tb_estabelecimento est on (org.est_codigo = est.est_codigo) ");
        corpoBuilder.append(" inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) ");
        corpoBuilder.append(" inner join tb_servidor ser on (rse.ser_codigo = ser.ser_codigo) ");

        corpoBuilder.append(" where arm.pex_periodo ").append(criaClausulaNomeada("periodo", periodo));

        if (!TextHelper.isNull(rseMatricula)) {
            corpoBuilder.append(" and arm.rse_matricula ").append(criaClausulaNomeada("rseMatricula", rseMatricula));
        }
        if (!TextHelper.isNull(serCpf)) {
            corpoBuilder.append(" and arm.ser_cpf ").append(criaClausulaNomeada("serCpf", serCpf));
        }
        if (!TextHelper.isNull(orgIdentificador)) {
            corpoBuilder.append(" and arm.org_identificador ").append(criaClausulaNomeada("orgIdentificador", orgIdentificador));
        }
        if (!TextHelper.isNull(estIdentificador)) {
            corpoBuilder.append(" and arm.est_identificador ").append(criaClausulaNomeada("estIdentificador", estIdentificador));
        }
        if (!TextHelper.isNull(csaIdentificador)) {
            corpoBuilder.append(" and arm.csa_identificador ").append(criaClausulaNomeada("csaIdentificador", csaIdentificador));
        }
        if (!TextHelper.isNull(svcIdentificador)) {
            corpoBuilder.append(" and arm.svc_identificador ").append(criaClausulaNomeada("svcIdentificador", svcIdentificador));
        }
        if (!TextHelper.isNull(cnvCodVerba)) {
            corpoBuilder.append(" and arm.cnv_cod_verba ").append(criaClausulaNomeada("cnvCodVerba", cnvCodVerba));
        }

        corpoBuilder.append(" order by arm.pex_periodo, arm.arm_situacao, arm.ade_data ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("periodo", periodo, query);

        if (!TextHelper.isNull(rseMatricula)) {
            defineValorClausulaNomeada("rseMatricula", rseMatricula, query);
        }
        if (!TextHelper.isNull(serCpf)) {
            defineValorClausulaNomeada("serCpf", serCpf, query);
        }
        if (!TextHelper.isNull(orgIdentificador)) {
            defineValorClausulaNomeada("orgIdentificador", orgIdentificador, query);
        }
        if (!TextHelper.isNull(estIdentificador)) {
            defineValorClausulaNomeada("estIdentificador", estIdentificador, query);
        }
        if (!TextHelper.isNull(csaIdentificador)) {
            defineValorClausulaNomeada("csaIdentificador", csaIdentificador, query);
        }
        if (!TextHelper.isNull(svcIdentificador)) {
            defineValorClausulaNomeada("svcIdentificador", svcIdentificador, query);
        }
        if (!TextHelper.isNull(cnvCodVerba)) {
            defineValorClausulaNomeada("cnvCodVerba", cnvCodVerba, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PEX_PERIODO,
                Columns.SER_NOME,
                Columns.SER_CPF,
                Columns.RSE_MATRICULA,
                Columns.EST_IDENTIFICADOR,
                Columns.EST_NOME,
                Columns.ORG_IDENTIFICADOR,
                Columns.ORG_NOME,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME,
                Columns.SVC_IDENTIFICADOR,
                Columns.SVC_DESCRICAO,
                Columns.CNV_COD_VERBA,
                Columns.ADE_DATA,
                Columns.ADE_ANO_MES_INI,
                Columns.ADE_ANO_MES_FIM,
                Columns.ADE_VLR,
                Columns.ADE_PRAZO,
                Columns.ADE_PRD_PAGAS,
                Columns.ADE_NUMERO,
                Columns.ADE_INDICE,
                Columns.ARM_SITUACAO,
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }
}
