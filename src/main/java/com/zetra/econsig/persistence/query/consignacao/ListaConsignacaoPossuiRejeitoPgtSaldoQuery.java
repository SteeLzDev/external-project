package com.zetra.econsig.persistence.query.consignacao;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignacaoPossuiRejeitoPgtSaldoQuery</p>
 * <p>Description: Lista consignações que possuem ocorrencia por rejeito de pagamento de saldo devedor</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignacaoPossuiRejeitoPgtSaldoQuery extends HNativeQuery {

    public String csaCodigo;
    public boolean count;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo = "";

        if (count) {
            corpo = "select count(*) as total ";
        } else {
            corpo = "select "
                  +  "adeDestino.ADE_CODIGO as adeCodDestino, "
                  +  "adeOrigem.ADE_CODIGO, "
                  +  "adeOrigem.ADE_NUMERO, "
                  +  "adeOrigem.ADE_IDENTIFICADOR, "
                  +  "adeOrigem.ADE_TIPO_VLR, "
                  +  "adeOrigem.ADE_VLR, "
                  +  "adeOrigem.ADE_PRAZO, "
                  +  "adeOrigem.ADE_DATA, "
                  +  "sadOrigem.sad_descricao, "
                  +  "rad.RAD_DATA, "
                  +  "rad.RAD_DATA_INF_SALDO, "
                  +  "rad.RAD_DATA_PGT_SALDO, "
                  +  "csaOrigem.csa_codigo as csaOriCod, "
                  +  "csaOrigem.csa_nome as csaOriNome, "
                  +  "csaDestino.csa_codigo as csaDestCod, "
                  +  "csaDestino.csa_nome as csaDestNome, "
                  +  "ser.ser_cpf, "
                  +  "ser.ser_nome, "
                  +  "rse.rse_matricula "
                  ;
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append("from tb_relacionamento_autorizacao rad  ");

        corpoBuilder.append("inner join tb_aut_desconto adeOrigem ON (rad.ADE_CODIGO_ORIGEM = adeOrigem.ADE_CODIGO) ");
        corpoBuilder.append("inner join tb_status_autorizacao_desconto sadOrigem ON (adeOrigem.SAD_CODIGO = sadOrigem.SAD_CODIGO) ");
        corpoBuilder.append("inner join tb_verba_convenio vcoOrigem ON (vcoOrigem.vco_codigo = adeOrigem.VCO_CODIGO) ");
        corpoBuilder.append("inner join tb_convenio cnvOrigem ON (cnvOrigem.cnv_codigo = vcoOrigem.cnv_codigo) ");
        corpoBuilder.append("inner join tb_consignataria csaOrigem ON (csaOrigem.csa_codigo = cnvOrigem.csa_codigo)    ");

        corpoBuilder.append("inner join tb_aut_desconto adeDestino ON (rad.ADE_CODIGO_DESTINO = adeDestino.ADE_CODIGO) ");
        corpoBuilder.append("inner join tb_verba_convenio vcoDestino ON (vcoDestino.vco_codigo = adeDestino.VCO_CODIGO) ");
        corpoBuilder.append("inner join tb_convenio cnvDestino ON (cnvDestino.cnv_codigo = vcoDestino.cnv_codigo) ");
        corpoBuilder.append("inner join tb_consignataria csaDestino ON (csaDestino.csa_codigo = cnvDestino.csa_codigo) ");
        corpoBuilder.append("inner join tb_param_svc_consignante pse ON (pse.svc_codigo = cnvDestino.svc_codigo and pse.tps_codigo = '" + CodedValues.TPS_REJEICAO_PGT_SDV_BLOQUEIA_AMBAS_CSAS + "') ");

        corpoBuilder.append("inner join tb_registro_servidor rse ON (rse.rse_codigo = adeDestino.RSE_CODIGO) ");
        corpoBuilder.append("inner join tb_servidor ser ON (ser.ser_codigo = rse.ser_codigo) ");

        corpoBuilder.append(" where rad.TNT_CODIGO = '").append(CodedValues.TNT_CONTROLE_COMPRA).append("' ");
        corpoBuilder.append(" and adeOrigem.SAD_CODIGO = '").append(CodedValues.SAD_AGUARD_LIQUI_COMPRA).append("' ");
        corpoBuilder.append(" and adeDestino.SAD_CODIGO = '").append(CodedValues.SAD_AGUARD_CONF).append("' ");
        corpoBuilder.append(" and coalesce(pse.PSE_VLR, '0') = '1' ");

        corpoBuilder.append(" and exists ( ");
        corpoBuilder.append(" select 1 from tb_ocorrencia_autorizacao oca ");
        corpoBuilder.append(" where oca.toc_codigo = '").append(CodedValues.TOC_REJEICAO_PAGAMENTO_SALDO_DEVEDOR).append("'");
        corpoBuilder.append(" and oca.oca_data > rad.RAD_DATA");
        corpoBuilder.append(" and oca.ADE_CODIGO = adeOrigem.ADE_CODIGO");
        corpoBuilder.append(" ) ");

        corpoBuilder.append(" AND (csaDestino.csa_codigo ").append(criaClausulaNomeada("csaCodigoDestino", csaCodigo));
        corpoBuilder.append(" OR csaOrigem.csa_codigo ").append(criaClausulaNomeada("csaCodigoOrigem", csaCodigo));
        corpoBuilder.append(" ) ");

        corpoBuilder.append(" ORDER BY csaOrigem.csa_nome, adeOrigem.ADE_NUMERO ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("csaCodigoDestino", csaCodigo, query);
        defineValorClausulaNomeada("csaCodigoOrigem", csaCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO + "Dest",
                Columns.ADE_CODIGO,
                Columns.ADE_NUMERO,
                Columns.ADE_IDENTIFICADOR,
                Columns.ADE_TIPO_VLR,
                Columns.ADE_VLR,
                Columns.ADE_PRAZO,
                Columns.ADE_DATA,
                Columns.SAD_DESCRICAO,
                Columns.RAD_DATA,
                Columns.RAD_DATA_INF_SALDO,
                Columns.RAD_DATA_PGT_SALDO,
                Columns.CSA_CODIGO + "Ori",
                Columns.CSA_NOME + "Ori",
                Columns.CSA_CODIGO + "Dest",
                Columns.CSA_NOME + "Dest",
                Columns.SER_CPF,
                Columns.SER_NOME,
                Columns.RSE_MATRICULA
         };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
        csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
    }
}
