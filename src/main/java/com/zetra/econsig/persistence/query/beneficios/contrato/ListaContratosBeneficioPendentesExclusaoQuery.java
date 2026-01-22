package com.zetra.econsig.persistence.query.beneficios.contrato;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusContratoBeneficioEnum;


/**
 * <p>Title: ListaContratosBeneficioPendentesExclusaoQuery</p>
 * <p>Description: Lista os Contratos Beneficio que estão pendentes de exclusão na operadora (consignatária).</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author: marcos.nolasco $
 * $Revision: 26503 $
 * $Date: 2019-09-21 10:52:07 -0300 (sab, 21 set 2019) $
 */
public class ListaContratosBeneficioPendentesExclusaoQuery extends HNativeQuery {

    public boolean count = false;
    public String csaCodigo;
    public String statusContrato;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        if (count) {
            corpoBuilder.append(" select count(*) ");
        } else {
            corpoBuilder.append(" select cbe.cbe_codigo CBE_CODIGO, rse_matricula MATRICULA, bfc_nome NOME_BENEFICIARIO, bfc_cpf CPF_BENEFICIARIO, cbe_numero CBE_NUMERO, ben_descricao BEN_DESCRICAO, "
                    + "if(CBE_DATA_FIM_VIGENCIA is NULL,CBE_DATA_CANCELAMENTO,CBE_DATA_FIM_VIGENCIA) FIM_VIGENCIA, DateDiff(curdate(),OCB_DATA) DIAS_STATUS");
        }
        corpoBuilder.append(" from tb_contrato_beneficio cbe");
        corpoBuilder.append(" inner join tb_beneficiario bfc on (cbe.bfc_codigo = bfc.bfc_codigo)");
        corpoBuilder.append(" inner join tb_beneficio ben on (ben.ben_codigo = cbe.ben_codigo)");
        corpoBuilder.append(" inner join tb_servidor ser on (ser.ser_codigo = bfc.ser_codigo)");
        corpoBuilder.append(" inner join tb_registro_servidor rse on (rse.ser_codigo = ser.ser_codigo)");
        corpoBuilder.append(" inner join tb_consignataria csa on (csa.csa_codigo = ben.csa_codigo)");
        corpoBuilder.append(" inner join tb_ocorrencia_ctt_beneficio ocb on (ocb.cbe_codigo = cbe.cbe_codigo)");
        corpoBuilder.append(" where 1=1 ");
        corpoBuilder.append(" and cbe.scb_codigo = :scbCodigo ");
        corpoBuilder.append(" and ocb.toc_codigo = :tocCodigo");
        if (!TextHelper.isNull(statusContrato)) {
            corpoBuilder.append(" and ocb.ocb_obs like :statusContrato");
        }
        corpoBuilder.append(" and csa.csa_codigo = :csaCodigo");
        if (!count) {
            corpoBuilder.append(" order by MATRICULA, DIAS_STATUS desc");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("scbCodigo", StatusContratoBeneficioEnum.AGUARD_EXCLUSAO_OPERADORA.getCodigo(), query);
        defineValorClausulaNomeada("tocCodigo", CodedValues.TOC_ALTERACAO_STATUS_CONTRATO_BENEFICIO, query);
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);

        if (!TextHelper.isNull(statusContrato)) {
            defineValorClausulaNomeada("statusContrato", "%" + statusContrato, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CBE_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.BFC_NOME,
                Columns.BFC_CPF,
                Columns.CBE_NUMERO,
                Columns.BEN_DESCRICAO,
                "FIM_VIGENCIA",
                "DIAS_STATUS"
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }

}
