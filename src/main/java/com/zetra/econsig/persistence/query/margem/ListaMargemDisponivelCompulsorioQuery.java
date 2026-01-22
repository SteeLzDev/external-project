package com.zetra.econsig.persistence.query.margem;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ListaMargemDisponivelCompulsorioQuery</p>
 * <p>Description: Retorna o saldo de margem disponível para inclusão de
 * compulsório. Em sistema que a folha envia margem líquida, o saldo será
 * a margem da folha subtraído da diferença entre inclusões e exclusões
 * no período. Em sistema que controla margem, será a própria margem
 * restante.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaMargemDisponivelCompulsorioQuery extends HNativeQuery {

    public boolean alteracao = false;
    public boolean controlaMargem = false;
    public String rseCodigo;
    public Short adeIncMargem;
    public String adeCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String campoMargemFolha = null;
        String campoMargemUsada = null;
        final boolean margemExtra = (adeIncMargem != null) && !CodedValues.INCIDE_MARGEM_SIM.equals(adeIncMargem) && !CodedValues.INCIDE_MARGEM_SIM_2.equals(adeIncMargem) && !CodedValues.INCIDE_MARGEM_SIM_3.equals(adeIncMargem);
        if (adeIncMargem != null) {
            if (controlaMargem && !margemExtra) {
                campoMargemFolha = adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM)   ? "rse.rse_margem_rest"
                        : adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_2) ? "rse.rse_margem_rest_2"
                                : adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_3) ? "rse.rse_margem_rest_3"
                                        : null;
                campoMargemUsada = "to_decimal(0.00, 13, 2)";
            } else if (controlaMargem && margemExtra) {
                campoMargemFolha = "mrs.mrs_margem_rest";
                campoMargemUsada = "to_decimal(0.00, 13, 2)";
            } else {
                campoMargemFolha = adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM)   ? "rse.rse_margem"
                        : adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_2) ? "rse.rse_margem_2"
                                : adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_3) ? "rse.rse_margem_3"
                                        : null;
                campoMargemUsada = adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM)   ? "rse.rse_margem_usada"
                        : adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_2) ? "rse.rse_margem_usada_2"
                                : adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_3) ? "rse.rse_margem_usada_3"
                                        : null;
            }
            if (campoMargemFolha == null) {
                throw new HQueryException("mensagem.erro.incidencia.margem.invalida.compulsorio", (AcessoSistema) null);
            }
        } else {
            throw new HQueryException("mensagem.erro.informe.incidencia.margem", (AcessoSistema) null);
        }

        final StringBuilder corpoBuilder = new StringBuilder();
        if (controlaMargem && !margemExtra) {
            // Se controla margem, o saldo do periodo será a própria margem restante
            corpoBuilder.append("SELECT ").append(campoMargemFolha).append(" AS MARGEM, ").append(campoMargemUsada).append(" AS MARGEM_USADA ");
            corpoBuilder.append(" FROM tb_registro_servidor rse ");
            corpoBuilder.append(" WHERE rse.rse_codigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        } else if (controlaMargem && margemExtra) {
            corpoBuilder.append("SELECT ").append(campoMargemFolha).append(" AS MARGEM, ").append(campoMargemUsada).append(" AS MARGEM_USADA ");
            corpoBuilder.append(" FROM tb_margem_registro_servidor mrs ");
            corpoBuilder.append(" WHERE mrs.rse_codigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
            corpoBuilder.append(" AND mrs.mar_codigo ").append(criaClausulaNomeada("adeIncMargem", adeIncMargem));
        } else {

            final List<String> sadCodigoCompulsorio = new ArrayList<>();

            if(ParamSist.getBoolParamSist(CodedValues.TPC_CONTRATOS_ESTOQUE_NAO_CONTABILIZAM_MARGEM, AcessoSistema.getAcessoUsuarioSistema())) {
                sadCodigoCompulsorio.addAll(CodedValues.SAD_CODIGOS_INCLUSAO_COMPULSORIO_SEM_ESTOQUE);
            } else {
                sadCodigoCompulsorio.addAll(CodedValues.SAD_CODIGOS_INCLUSAO_COMPULSORIO_COM_ESTOQUE);
            }

            // Se não controla margem, o saldo do período será dado pela diferença entre inclusões e exclusões
            // ainda não enviadas para a folha, que não estão dentro da margem
            corpoBuilder.append("SELECT ").append(campoMargemFolha).append(" AS MARGEM, COALESCE(X.MARGEM_USADA_SALDO, ").append(campoMargemUsada).append(") AS MARGEM_USADA ");
            corpoBuilder.append(" FROM tb_registro_servidor rse ");
            corpoBuilder.append(" LEFT OUTER JOIN (");
            corpoBuilder.append(" SELECT ade.rse_codigo AS RSE_CODIGO, ");
            corpoBuilder.append("   SUM(ade.ade_vlr * ");
            corpoBuilder.append("       CASE WHEN oca.toc_codigo = '").append(CodedValues.TOC_TARIF_RESERVA).append("' THEN 1 ");
            corpoBuilder.append("            WHEN oca.toc_codigo = '").append(CodedValues.TOC_TARIF_LIQUIDACAO).append("' THEN -1 ");
            corpoBuilder.append("            ELSE 0 END) AS MARGEM_USADA_SALDO ");
            corpoBuilder.append(" FROM tb_aut_desconto ade ");
            corpoBuilder.append(" INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo)");
            corpoBuilder.append(" INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo)");
            corpoBuilder.append(" INNER JOIN tb_ocorrencia_autorizacao oca ON (ade.ade_codigo = oca.ade_codigo)");

            corpoBuilder.append(" WHERE ade.rse_codigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
            corpoBuilder.append(" AND ade.sad_codigo IN ('").append(TextHelper.join(sadCodigoCompulsorio, "','")).append("')");
            corpoBuilder.append(" AND ade.ade_inc_margem ").append(criaClausulaNomeada("adeIncMargem", adeIncMargem));
            if (!TextHelper.isNull(adeCodigo) && !alteracao) {
                // No caso de alteração, o contrato a ser alterado não deve ser ignorado da contagem
                // pois a análise do valor a ser reservado na alteração contém apenas a diferença adicional.
                corpoBuilder.append(" AND ade.ade_codigo <> :adeCodigo");
            }
            corpoBuilder.append(" AND oca.toc_codigo IN ('").append(CodedValues.TOC_TARIF_RESERVA).append("', '").append(CodedValues.TOC_TARIF_LIQUIDACAO).append("')  ");

            // Lista apenas as ocorrências que ainda não impactaram na margem dos servidores.
            // Caso já tenha processado o retorno referente ao último movimento, considera os contratos cuja data da ocorrência seja maior que a data fim do
            // último movimento. Caso contrário, considera os contratos cuja data da ocorrência seja maior que a data de início do último movimento.
            corpoBuilder.append(" AND oca.oca_data >= (");
            corpoBuilder.append("   SELECT CASE WHEN NOT EXISTS (");
            corpoBuilder.append("     SELECT 1 FROM tb_historico_conclusao_retorno hcr ");
            corpoBuilder.append("     WHERE hcr.org_codigo = hie.org_codigo AND hcr.hcr_periodo = hie.hie_periodo AND hcr.hcr_desfeito = 'N') ");
            corpoBuilder.append("   THEN MAX(hie.hie_data_ini) ELSE add_day(MAX(hie.hie_data_fim), 1) END ");
            corpoBuilder.append("   FROM tb_historico_exportacao hie ");
            corpoBuilder.append("   WHERE hie.org_codigo  = cnv.org_codigo ");
            corpoBuilder.append("   AND hie.hie_periodo = (SELECT MAX(ult.hie_periodo) FROM tb_historico_exportacao ult WHERE ult.org_codigo  = cnv.org_codigo) ");
            corpoBuilder.append(" )");

            corpoBuilder.append(" GROUP BY ade.rse_codigo");
            corpoBuilder.append(") AS X ON (X.RSE_CODIGO = rse.rse_codigo)");
            corpoBuilder.append(" WHERE rse.rse_codigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        }

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        if (!controlaMargem) {
            defineValorClausulaNomeada("adeIncMargem", adeIncMargem, query);

            if (!TextHelper.isNull(adeCodigo)) {
                defineValorClausulaNomeada("adeCodigo", adeCodigo, query);
            }
        } else if (margemExtra) {
            defineValorClausulaNomeada("adeIncMargem", adeIncMargem, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {"MARGEM", "MARGEM_USADA"};
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }
}
