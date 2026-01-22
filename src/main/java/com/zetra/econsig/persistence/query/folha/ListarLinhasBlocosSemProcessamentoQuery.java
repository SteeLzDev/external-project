package com.zetra.econsig.persistence.query.folha;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.StatusBlocoProcessamentoEnum;
import com.zetra.econsig.values.TipoBlocoProcessamentoEnum;

/**
 * <p>Title: ListarLinhasBlocosProcessamentoQuery</p>
 * <p>Description: Lista as linhas que tiveram blocos mapeados em seus convênios</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarLinhasBlocosSemProcessamentoQuery extends HNativeQuery  {

    public boolean semProcessamento;
    public String tipoEntidade;
    public String codigoEntidade;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final boolean utilizaVerbaRef = ParamSist.paramEquals(CodedValues.TPC_UTILIZA_CNV_COD_VERBA_REF, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        final boolean temProcessamentoFerias = ParamSist.paramEquals(CodedValues.TPC_TEM_PROCESSAMENTO_FERIAS, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

        final List<String> tbpCodigos = List.of(
                TipoBlocoProcessamentoEnum.RETORNO.getCodigo(),
                TipoBlocoProcessamentoEnum.RETORNO_ATRASADO.getCodigo(),
                TipoBlocoProcessamentoEnum.RETORNO_DE_FERIAS.getCodigo()
                );
        final List<String> sbpCodigos = List.of(
                StatusBlocoProcessamentoEnum.PROCESSADO_COM_ERRO.getCodigo(),
                StatusBlocoProcessamentoEnum.PROCESSADO_COM_SUCESSO.getCodigo()
                );

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT bpr.bpr_linha ");
        corpoBuilder.append("FROM tb_bloco_processamento bpr ");
        corpoBuilder.append("WHERE bpr.tbp_codigo ").append(criaClausulaNomeada("tbpCodigos", tbpCodigos));
        corpoBuilder.append("  AND bpr.sbp_codigo ").append(criaClausulaNomeada("sbpCodigos", sbpCodigos));

        if (tipoEntidade != null && (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST))) {
            if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                corpoBuilder.append(" AND bpr.org_codigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            } else {
                corpoBuilder.append(" AND bpr.est_codigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            }
        }

        // Não existe uma parcela mapeada para pagamento
        corpoBuilder.append(" AND NOT EXISTS (SELECT 1 FROM tb_arquivo_retorno_parcela arp WHERE arp.id_linha = bpr.bpr_num_linha) ");

        if (semProcessamento) {
            // Lista blocos sem processamento, ou seja, convênio existe
            corpoBuilder.append(" AND (EXISTS (SELECT 1 FROM tb_convenio cnv WHERE cnv.cnv_cod_verba = bpr.cnv_cod_verba) ");

            if (utilizaVerbaRef) {
                corpoBuilder.append(" OR EXISTS (SELECT 1 FROM tb_convenio cnv WHERE cnv.cnv_cod_verba_ref = bpr.cnv_cod_verba) ");
            }
            if (temProcessamentoFerias) {
                corpoBuilder.append(" OR EXISTS (SELECT 1 FROM tb_convenio cnv WHERE cnv.cnv_cod_verba_ferias = bpr.cnv_cod_verba) ");
            }
            corpoBuilder.append(")");

        } else {
            // Lista blocos sem mapeamento, ou seja, convênio não existe
            corpoBuilder.append(" AND NOT EXISTS (SELECT 1 FROM tb_convenio cnv WHERE cnv.cnv_cod_verba = bpr.cnv_cod_verba) ");

            if (utilizaVerbaRef) {
                corpoBuilder.append(" AND NOT EXISTS (SELECT 1 FROM tb_convenio cnv WHERE cnv.cnv_cod_verba_ref = bpr.cnv_cod_verba) ");
            }
            if (temProcessamentoFerias) {
                corpoBuilder.append(" AND NOT EXISTS (SELECT 1 FROM tb_convenio cnv WHERE cnv.cnv_cod_verba_ferias = bpr.cnv_cod_verba) ");
            }
        }

        corpoBuilder.append(" ORDER BY bpr.bpr_ordem_execucao");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("tbpCodigos", tbpCodigos, query);
        defineValorClausulaNomeada("sbpCodigos", sbpCodigos, query);

        if (tipoEntidade != null && (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST))) {
            defineValorClausulaNomeada("codigoEntidade", codigoEntidade, query);
        }

        return query;
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }
}
