package com.zetra.econsig.persistence.query.consignacao;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class ObtemConsignacaoReimplanteQuery extends HQuery {

    public boolean count = false;
    public List<Long> adeNum;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String query;
        if (count) {
            query = "select count(distinct ade.adeCodigo) as total ";
        } else {
            query = "select " +
                    "ade.adeCodigo, " +
                    "ade.adeData, " +
                    "ade.adeVlr, " +
                    "ade.adeVlrRef, " +
                    "ade.adeNumero, " +
                    "ade.adePrazo, " +
                    "ade.adePrazoRef, " +
                    "ade.adePrdPagas, " +
                    "ade.adePrdPagasTotal, " +
                    "ade.adeIdentificador, " +
                    "ade.adeAnoMesIni, " +
                    "ade.adeAnoMesFim, " +
                    "ade.adeAnoMesIniRef, " +
                    "ade.adeAnoMesFimRef, " +
                    "ade.adeTipoVlr, " +
                    "ade.adeTipoTaxa, " +
                    "ade.adeIncMargem, " +
                    "ade.adeIntFolha, " +
                    "ade.adeIndice, " +
                    "ade.adeCodReg, " +
                    "ade.adeVlrTac, " +
                    "ade.adeVlrIof, " +
                    "ade.adeVlrMensVinc, " +
                    "ade.adeVlrLiquido, " +
                    "ade.adeDataHoraOcorrencia, " +
                    "ade.adeVlrSegPrestamista, " +
                    "ade.adeTaxaJuros, " +
                    "ade.adeBanco, " +
                    "ade.adeAgencia, " +
                    "ade.adeConta, " +
                    "ade.adeVlrSdoRet, " +
                    "ade.adeCarencia, " +
                    "ade.adeVlrPercentual, " +
                    "ade.adeVlrParcelaFolha, " +
                    "ade.adePeriodicidade, " +
                    "ade.adeVlrFolha, " +
                    "ade.adeDataReativacaoAutomatica, " +
                    "ade.adeDataStatus, " +
                    "sad.sadCodigo, " +
                    "sad.sadDescricao, " +
                    "usu.usuLogin, " +
                    "usu.usuNome, " +
                    "usu.usuTipoBloq, " +
                    "cnv.cnvCodVerba, " +
                    "csa.csaIdentificador, " +
                    "csa.csaNomeAbrev, " +
                    "csa.csaNome, " +
                    "svc.svcDescricao ";
        }

        StringBuilder corpoBuilder = new StringBuilder(query);
        corpoBuilder.append(" FROM AutDesconto ade");
        corpoBuilder.append(" INNER JOIN ade.statusAutorizacaoDesconto sad");
        corpoBuilder.append(" INNER JOIN ade.usuario usu");
        corpoBuilder.append(" INNER JOIN ade.verbaConvenio vco");
        corpoBuilder.append(" INNER JOIN vco.convenio cnv");
        corpoBuilder.append(" INNER JOIN cnv.consignataria csa");
        corpoBuilder.append(" INNER JOIN cnv.servico svc");

        corpoBuilder.append(" WHERE ade.adeNumero ").append(criaClausulaNomeada("adeNum", adeNum));

        Query<Object[]> qr = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("adeNum", adeNum, qr);

        return qr;
    }

    @Override
    protected String[] getFields() {
        return new String[]{
                Columns.ADE_CODIGO,
                Columns.ADE_DATA,
                Columns.ADE_VLR,
                Columns.ADE_VLR_REF,
                Columns.ADE_NUMERO,
                Columns.ADE_PRAZO,
                Columns.ADE_PRAZO_REF,
                Columns.ADE_PRD_PAGAS,
                Columns.ADE_PRD_PAGAS_TOTAL,
                Columns.ADE_IDENTIFICADOR,
                Columns.ADE_ANO_MES_INI,
                Columns.ADE_ANO_MES_FIM,
                Columns.ADE_ANO_MES_INI_REF,
                Columns.ADE_ANO_MES_FIM_REF,
                Columns.ADE_TIPO_VLR,
                Columns.ADE_TIPO_TAXA,
                Columns.ADE_INC_MARGEM,
                Columns.ADE_INT_FOLHA,
                Columns.ADE_INDICE,
                Columns.ADE_COD_REG,
                Columns.ADE_VLR_TAC,
                Columns.ADE_VLR_IOF,
                Columns.ADE_VLR_MENS_VINC,
                Columns.ADE_VLR_LIQUIDO,
                Columns.ADE_DATA_HORA_OCORRENCIA,
                Columns.ADE_VLR_SEG_PRESTAMISTA,
                Columns.ADE_TAXA_JUROS,
                Columns.ADE_BANCO,
                Columns.ADE_AGENCIA,
                Columns.ADE_CONTA,
                Columns.ADE_VLR_SDO_RET,
                Columns.ADE_CARENCIA,
                Columns.ADE_VLR_PERCENTUAL,
                Columns.ADE_VLR_PARCELA_FOLHA,
                Columns.ADE_PERIODICIDADE,
                Columns.ADE_VLR_FOLHA,
                Columns.ADE_DATA_REATIVACAO_AUTOMATICA,
                Columns.ADE_DATA_STATUS,
                Columns.ADE_SAD_CODIGO,
                Columns.SAD_DESCRICAO,
                Columns.USU_LOGIN,
                Columns.USU_NOME,
                Columns.USU_TIPO_BLOQ,
                Columns.CNV_COD_VERBA,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME_ABREV,
                Columns.CSA_NOME,
                Columns.SVC_DESCRICAO
        };
    }
}
