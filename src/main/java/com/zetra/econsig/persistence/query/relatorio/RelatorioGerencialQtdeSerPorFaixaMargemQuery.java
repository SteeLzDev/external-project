package com.zetra.econsig.persistence.query.relatorio;

import java.math.BigDecimal;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p> Title: RelatorioGerencialQtdeSerPorFaixaMargemQuery</p>
 * <p> Description: Lista quantidade de servidores por faixa de margem calculada através da média das margens incrementando e decrementando o desvio padrão calculado das margens.</p>
 * <p> Copyright: Copyright (c) 2009 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioGerencialQtdeSerPorFaixaMargemQuery extends ReportHQuery {

    private static final int LIMITE_MAX_FAIXAS = 10;
    private final Short incideMargem;
    private final BigDecimal mediaMargem;
    private final BigDecimal desvioMargem;
    // Formatação para os valores monetários
    private static final String PATTERN_VALOR_MONETARIO = ApplicationResourcesHelper.getMessage("rotulo.moeda.pattern", (AcessoSistema) null);


    public RelatorioGerencialQtdeSerPorFaixaMargemQuery(BigDecimal mediaMargem, BigDecimal desvioMargem, Short incideMargem) {
        this.incideMargem = incideMargem;
        this.mediaMargem = mediaMargem;
        this.desvioMargem = desvioMargem;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final String margemRest;
        if (incideMargem.equals(CodedValues.INCIDE_MARGEM_SIM)) {
            margemRest = "coalesce(rse.rseMargemRest,0)";
        } else if (incideMargem.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
            margemRest = "coalesce(rse.rseMargemRest2,0)";
        } else if (incideMargem.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
            margemRest = "coalesce(rse.rseMargemRest3,0)";
        } else {
            throw new HQueryException("rotulo.campo.nao.definido.abreviado", (AcessoSistema) null);
        }

        String srsAtivo = CodedValues.SRS_ATIVO;
        String descricao = montaDescricao(margemRest);
        String valor1 = montaValor1(margemRest);
        String valor2 = montaValor2(margemRest);
        String contador = montaContador(margemRest);

        StringBuilder corpo = new StringBuilder();
        corpo.append("SELECT ");
        corpo.append(descricao).append(" AS DESCRICAO, ");
        corpo.append(valor1).append(" AS VALOR_1, ");
        corpo.append(valor2).append(" AS VALOR_2, ");
        corpo.append("SUM(").append(contador).append(") AS QUANTIDADE ");
        corpo.append("FROM RegistroServidor rse ");
        corpo.append("WHERE rse.statusRegistroServidor.srsCodigo ").append(criaClausulaNomeada("srsAtivo", srsAtivo));
        corpo.append(" GROUP BY ").append(descricao).append(", ");
        corpo.append(valor1).append(", ").append(valor2);
        corpo.append(" ORDER BY ").append(valor1).append(" ASC, ").append(valor2).append(" + 1 ASC");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        defineValorClausulaNomeada("srsAtivo", srsAtivo, query);

        return query;
    }

    private String montaDescricao(String margemRest) {
        StringBuilder retorno = new StringBuilder("CASE ");

        int i = 0;
        BigDecimal valorBase = new BigDecimal(mediaMargem.doubleValue());
        while (i++ < LIMITE_MAX_FAIXAS) {
            BigDecimal valorLimite = valorBase.add(desvioMargem);
            retorno.append("WHEN ").append(margemRest).append(" between ").append(valorBase.setScale(2, java.math.RoundingMode.UP)).append(" and ");
            retorno.append(valorLimite.setScale(2, java.math.RoundingMode.UP)).append(" THEN '");
            retorno.append(NumberHelper.formata(valorBase.doubleValue(), PATTERN_VALOR_MONETARIO)).append(" "+ApplicationResourcesHelper.getMessage("rotulo.ate", (AcessoSistema) null)+" ");
            retorno.append(NumberHelper.formata(valorLimite.doubleValue(), PATTERN_VALOR_MONETARIO)).append("' ");
            valorBase = valorBase.add(desvioMargem);
        }
        retorno.append("WHEN ").append(margemRest).append(" > ").append(valorBase.setScale(2, java.math.RoundingMode.UP)).append(" ");
        retorno.append("THEN '"+ApplicationResourcesHelper.getMessage("rotulo.maior.que", (AcessoSistema) null)+" ").append(NumberHelper.formata(valorBase.doubleValue(), PATTERN_VALOR_MONETARIO)).append("' ");

        i = 0;
        valorBase = new BigDecimal(mediaMargem.doubleValue());
        while (i++ < LIMITE_MAX_FAIXAS) {
            BigDecimal valorLimite = valorBase.subtract(desvioMargem);
            if (valorBase.compareTo(BigDecimal.ZERO) > 0) {
                retorno.append("WHEN ").append(margemRest).append(" between ").append(BigDecimal.ZERO.setScale(2, java.math.RoundingMode.UP)).append(" and ");
                retorno.append(valorBase.setScale(2, java.math.RoundingMode.UP)).append(" THEN '");
                retorno.append(NumberHelper.formata(BigDecimal.ZERO.doubleValue(), PATTERN_VALOR_MONETARIO)).append(" "+ApplicationResourcesHelper.getMessage("rotulo.ate", (AcessoSistema) null)+" ");
                retorno.append(NumberHelper.formata(valorBase.doubleValue(), PATTERN_VALOR_MONETARIO)).append("' ");

                retorno.append("WHEN ").append(margemRest).append(" between ").append(valorLimite.setScale(2, java.math.RoundingMode.UP)).append(" and ");
                retorno.append(BigDecimal.ZERO.setScale(2, java.math.RoundingMode.UP)).append(" THEN '");
                retorno.append(NumberHelper.formata(valorLimite.doubleValue(), PATTERN_VALOR_MONETARIO)).append(" "+ApplicationResourcesHelper.getMessage("rotulo.ate", (AcessoSistema) null)+" ");
                retorno.append(NumberHelper.formata(BigDecimal.ZERO.doubleValue(), PATTERN_VALOR_MONETARIO)).append("' ");
            } else {
                retorno.append("WHEN ").append(margemRest).append(" between ").append(valorLimite.setScale(2, java.math.RoundingMode.UP)).append(" and ");
                retorno.append(valorBase.setScale(2, java.math.RoundingMode.UP)).append(" THEN '");
                retorno.append(NumberHelper.formata(valorLimite.doubleValue(), PATTERN_VALOR_MONETARIO)).append(" "+ApplicationResourcesHelper.getMessage("rotulo.ate", (AcessoSistema) null)+" ");
                retorno.append(NumberHelper.formata(valorBase.doubleValue(), PATTERN_VALOR_MONETARIO)).append("' ");
            }
            valorBase = valorBase.subtract(desvioMargem);
        }
        retorno.append("WHEN ").append(margemRest).append(" < ").append(valorBase.setScale(2, java.math.RoundingMode.UP)).append(" ");
        retorno.append("THEN '"+ApplicationResourcesHelper.getMessage("rotulo.menor.que", (AcessoSistema) null)+" ").append(NumberHelper.formata(valorBase.doubleValue(), PATTERN_VALOR_MONETARIO)).append("' ");

        retorno.append("END");

        return retorno.toString();
    }

    private String montaContador(String margemRest) {
        StringBuilder retorno = new StringBuilder("CASE ");

        int i = 0;
        BigDecimal valorBase = new BigDecimal(mediaMargem.doubleValue());
        while (i++ < LIMITE_MAX_FAIXAS) {
            BigDecimal valorLimite = valorBase.add(desvioMargem);
            retorno.append("WHEN ").append(margemRest).append(" between ").append(valorBase.setScale(2, java.math.RoundingMode.UP)).append(" and ");
            retorno.append(valorLimite.setScale(2, java.math.RoundingMode.UP)).append(" THEN 1 ");
            valorBase = valorBase.add(desvioMargem);
        }
        retorno.append("WHEN ").append(margemRest).append(" > ").append(valorBase.setScale(2, java.math.RoundingMode.UP)).append(" ");
        retorno.append("THEN 1 ");

        i = 0;
        valorBase = new BigDecimal(mediaMargem.doubleValue());
        while (i++ < LIMITE_MAX_FAIXAS) {
            BigDecimal valorLimite = valorBase.subtract(desvioMargem);
            if (valorBase.compareTo(BigDecimal.ZERO) > 0) {
                retorno.append("WHEN ").append(margemRest).append(" between ").append(BigDecimal.ZERO.setScale(2, java.math.RoundingMode.UP)).append(" and ");
                retorno.append(valorBase.setScale(2, java.math.RoundingMode.UP)).append(" THEN 1 ");

                retorno.append("WHEN ").append(margemRest).append(" between ").append(valorLimite.setScale(2, java.math.RoundingMode.UP)).append(" and ");
                retorno.append(BigDecimal.ZERO.setScale(2, java.math.RoundingMode.UP)).append(" THEN 1 ");
            } else {
                retorno.append("WHEN ").append(margemRest).append(" between ").append(valorLimite.setScale(2, java.math.RoundingMode.UP)).append(" and ");
                retorno.append(valorBase.setScale(2, java.math.RoundingMode.UP)).append(" THEN 1 ");
            }
            valorBase = valorBase.subtract(desvioMargem);
        }
        retorno.append("WHEN ").append(margemRest).append(" < ").append(valorBase.setScale(2, java.math.RoundingMode.UP)).append(" ");
        retorno.append("THEN 1 ");

        retorno.append("ELSE 0 END");

        return retorno.toString();
    }

    private String montaValor1(String margemRest) {
        StringBuilder retorno = new StringBuilder("CASE ");

        int i = 0;
        BigDecimal valorBase = new BigDecimal(mediaMargem.doubleValue());
        BigDecimal valorLimite = BigDecimal.ZERO;
        while (i++ < LIMITE_MAX_FAIXAS) {
            valorLimite = valorBase.add(desvioMargem);
            retorno.append("WHEN ").append(margemRest).append(" between ").append(valorBase.setScale(2, java.math.RoundingMode.UP)).append(" and ");
            retorno.append(valorLimite.setScale(2, java.math.RoundingMode.UP)).append(" THEN ");
            retorno.append(valorBase.setScale(2, java.math.RoundingMode.UP)).append(" ");
            valorBase = valorBase.add(desvioMargem);
        }
        retorno.append("WHEN ").append(margemRest).append(" > ").append(valorBase.setScale(2, java.math.RoundingMode.UP)).append(" ");
        retorno.append("THEN ").append(valorBase.setScale(2, java.math.RoundingMode.UP)).append(" ");

        i = 0;
        valorBase = new BigDecimal(mediaMargem.doubleValue());
        valorLimite = BigDecimal.ZERO;
        while (i++ < LIMITE_MAX_FAIXAS) {
            valorLimite = valorBase.subtract(desvioMargem);
            if (valorBase.compareTo(BigDecimal.ZERO) > 0) {
                retorno.append("WHEN ").append(margemRest).append(" between ").append(BigDecimal.ZERO.setScale(2, java.math.RoundingMode.UP)).append(" and ");
                retorno.append(valorBase.setScale(2, java.math.RoundingMode.UP)).append(" THEN ");
                retorno.append(BigDecimal.ZERO.setScale(2, java.math.RoundingMode.UP)).append(" ");

                retorno.append("WHEN ").append(margemRest).append(" between ").append(valorLimite.setScale(2, java.math.RoundingMode.UP)).append(" and ");
                retorno.append(BigDecimal.ZERO.setScale(2, java.math.RoundingMode.UP)).append(" THEN ");
                retorno.append(valorLimite.setScale(2, java.math.RoundingMode.UP)).append(" ");
            } else {
                retorno.append("WHEN ").append(margemRest).append(" between ").append(valorLimite.setScale(2, java.math.RoundingMode.UP)).append(" and ");
                retorno.append(valorBase.setScale(2, java.math.RoundingMode.UP)).append(" THEN ");
                retorno.append(valorLimite.setScale(2, java.math.RoundingMode.UP)).append(" ");
            }
            valorBase = valorBase.subtract(desvioMargem);
        }
        retorno.append("WHEN ").append(margemRest).append(" < ").append(valorBase.setScale(2, java.math.RoundingMode.UP)).append(" ");
        retorno.append("THEN ").append(valorBase.setScale(2, java.math.RoundingMode.UP)).append(" ");

        retorno.append("ELSE 0 END");

        return retorno.toString();
    }

    private String montaValor2(String margemRest) {
        StringBuilder retorno = new StringBuilder("CASE ");

        int i = 0;
        BigDecimal valorBase = new BigDecimal(mediaMargem.doubleValue());
        BigDecimal valorLimite = BigDecimal.ZERO;
        while (i++ < LIMITE_MAX_FAIXAS) {
            valorLimite = valorBase.add(desvioMargem);
            retorno.append("WHEN ").append(margemRest).append(" between ").append(valorBase.setScale(2, java.math.RoundingMode.UP)).append(" and ");
            retorno.append(valorLimite.setScale(2, java.math.RoundingMode.UP)).append(" THEN ");
            retorno.append(valorLimite.setScale(2, java.math.RoundingMode.UP)).append(" ");
            valorBase = valorBase.add(desvioMargem);
        }
        retorno.append("WHEN ").append(margemRest).append(" > ").append(valorBase.setScale(2, java.math.RoundingMode.UP)).append(" ");
        retorno.append("THEN ").append(valorLimite.setScale(2, java.math.RoundingMode.UP)).append(" ");

        i = 0;
        valorBase = new BigDecimal(mediaMargem.doubleValue());
        valorLimite = BigDecimal.ZERO;
        while (i++ < LIMITE_MAX_FAIXAS) {
            valorLimite = valorBase.subtract(desvioMargem);
            if (valorBase.compareTo(BigDecimal.ZERO) > 0) {
                retorno.append("WHEN ").append(margemRest).append(" between ").append(BigDecimal.ZERO.setScale(2, java.math.RoundingMode.UP)).append(" and ");
                retorno.append(valorBase.setScale(2, java.math.RoundingMode.UP)).append(" THEN ");
                retorno.append(valorBase.setScale(2, java.math.RoundingMode.UP)).append(" ");

                retorno.append("WHEN ").append(margemRest).append(" between ").append(valorLimite.setScale(2, java.math.RoundingMode.UP)).append(" and ");
                retorno.append(BigDecimal.ZERO.setScale(2, java.math.RoundingMode.UP)).append(" THEN ");
                retorno.append(BigDecimal.ZERO.setScale(2, java.math.RoundingMode.UP)).append(" ");
            } else {
                retorno.append("WHEN ").append(margemRest).append(" between ").append(valorLimite.setScale(2, java.math.RoundingMode.UP)).append(" and ");
                retorno.append(valorBase.setScale(2, java.math.RoundingMode.UP)).append(" THEN ");
                retorno.append(valorBase.setScale(2, java.math.RoundingMode.UP)).append(" ");
            }
            valorBase = valorBase.subtract(desvioMargem);
        }
        retorno.append("WHEN ").append(margemRest).append(" < ").append(valorBase.setScale(2, java.math.RoundingMode.UP)).append(" ");
        retorno.append("THEN ").append(valorBase.setScale(2, java.math.RoundingMode.UP)).append(" ");

        retorno.append("ELSE 0 END");

        return retorno.toString();
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "DESCRICAO",
                "VALOR_1",
                "VALOR_2",
                "QUANTIDADE"
        };
    }
}
