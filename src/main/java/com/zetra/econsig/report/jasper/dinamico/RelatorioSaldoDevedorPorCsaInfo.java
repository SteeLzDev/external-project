package com.zetra.econsig.report.jasper.dinamico;

import java.util.ArrayList;
import java.util.LinkedList;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.report.config.Relatorio;

import ar.com.fdvs.dj.domain.ColumnOperation;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.constants.Page;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.OperationColumn;
import ar.com.fdvs.dj.domain.entities.columns.SimpleColumn;

/**
 * <p>Title: RelatorioSaldoDevedorPorCsaInfo</p>
 * <p>Description: Definição de colunas dinâmicas do relatório sintético de saldo devedor por consignatária e período</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSaldoDevedorPorCsaInfo extends DynamicReportInfo {

    public RelatorioSaldoDevedorPorCsaInfo(Relatorio relatorio) {
        super(relatorio);
    }

    @Override
    protected DynamicReportBuilder reportDesign(AcessoSistema responsavel) {
        DynamicReportBuilder reportBuilder = new DynamicReportBuilder();
        reportBuilder.setWhenNoDataAllSectionNoDetail();

        LinkedList<String> campos = (LinkedList<String>) criterioQuery.getAttribute("campos");
        int countMes = (Integer) criterioQuery.getAttribute("countMes");
        int countEst = (Integer) criterioQuery.getAttribute("countEst");
        int countPeriodos = (Integer) criterioQuery.getAttribute("countPeriodos");
        LinkedList<String> alias = (LinkedList<String>) criterioQuery.getAttribute("alias");
        LinkedList<String> camposSpan = (LinkedList<String>) criterioQuery.getAttribute("camposSpan");
        String formato = (String) criterioQuery.getAttribute("formato");

        Style borda = new Style("borda");

        // coluna csa identificador
        ColumnBuilder fixedColumnBuilder = buildColumn(campos.remove(), 150, false, "CLAVE", String.class.getName(), borda);
        AbstractColumn columnCsaIdentificador = fixedColumnBuilder.build();
        reportBuilder.addColumn(columnCsaIdentificador);

        // coluna csa nome
        fixedColumnBuilder = buildColumn(campos.remove(), 700, false, "DESCRIPCION", String.class.getName(), borda);
        AbstractColumn columnCsaNome = fixedColumnBuilder.build();
        reportBuilder.addColumn(columnCsaNome);

        LinkedList<Integer> lstInicioPeriodo = new LinkedList<Integer>();
        int somatorioColunas = 2;

        for (int i = 1; i < (countMes); i++) {
            ArrayList<SimpleColumn> lstTotais = new ArrayList<>();

            for (int j = 1; j <= countPeriodos; j++) {
                ArrayList<Object> lstColumns = new ArrayList<Object>();
                String cabecalhoText = null;

                for (int k = 1; k <= countEst; k++) {
                    String chave = "DEVIDO_Q_" + i + "_" + k + "_" + j;

                    if (alias.contains(chave)) {
                        if (k == 1) {
                            lstInicioPeriodo.add(somatorioColunas);
                            if (formato.equals("TEXT")) {
                                cabecalhoText = camposSpan.remove().toString();
                            }
                        }

                        String nomeColuna = (!formato.equals("TEXT")) ? campos.remove() : cabecalhoText + " " + campos.remove();
                        ColumnBuilder columnBuilder = buildColumn(nomeColuna, 300, false, chave, Number.class.getName(), borda);

                        AbstractColumn columnDevido = columnBuilder.build();
                        lstColumns.add(columnDevido);

                        reportBuilder.addColumn(columnDevido);

                        somatorioColunas++;
                    }
                }

                String chaveTotal = "TOTAL_" + i + "_" + j;

                if (alias.contains(chaveTotal)) {
                    String nomeColunaTotal = (!formato.equals("TEXT")) ? campos.remove() : campos.remove() + " " + cabecalhoText ;
                    ColumnBuilder columnBuilder = buildColumn(nomeColunaTotal, 300, false, chaveTotal, Number.class.getName(), borda);

                    SimpleColumn columnTotal = (SimpleColumn) columnBuilder.build();
                    lstTotais.add(columnTotal);

                    reportBuilder.addColumn(columnTotal);

                    somatorioColunas++;
                }
            }

            String chaveMensual = "REC_MENSUAL_" + i;

            ColumnBuilder columnBldr = buildColumn(campos.remove(), 300, false, chaveMensual, Number.class.getName(), borda);

            AbstractColumn columnMensual = columnBldr.build();

            reportBuilder.addColumn(columnMensual);
            somatorioColunas++;

            if (!lstTotais.isEmpty() && lstTotais.size() > 1) {
                OperationColumn diferenca = new OperationColumn();
                LinkedList<SimpleColumn> invertido = new LinkedList<>();
                for (int t = lstTotais.size() - 1; t >= 0; t--) {
                    invertido.add(lstTotais.get(t));
                }
                diferenca.setColumns(invertido);
                diferenca.setColumnOperation(ColumnOperation.SUBSTRACT);
                String nomeColunaDif = (!formato.equals("TEXT")) ? ApplicationResourcesHelper.getMessage("rotulo.relatorio.saldo.por.csa.diferenca", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.relatorio.saldo.por.csa.diferenca", responsavel) + " " + i;
                diferenca.setTitle(nomeColunaDif);

                diferenca.setWidth(400);
                diferenca.setFixedWidth(false);
                diferenca.setStyle(borda);
                diferenca.setHeaderStyle(getHeaderStyle());
                reportBuilder.addColumn(diferenca);
                somatorioColunas++;
            }

        }

        if (!formato.equals("TEXT")) {
            for (Integer inicioPeriodo: lstInicioPeriodo) {
                reportBuilder.setColspan(inicioPeriodo, (countEst + 1),ApplicationResourcesHelper.getMessage("rotulo.relatorio.saldo.por.csa.saldo.recuperar", responsavel) + " " + camposSpan.remove().toString(), getHeaderStyle());
            }
            reportBuilder.setHeaderHeight(150);
            reportBuilder.setHeaderVariablesHeight(150);
        }

        Page page = new Page();
        page.setWidth((countMes * countMes * countEst * countPeriodos * 400) + 700);
        page.setOrientationPortrait(false);
        reportBuilder.setPageSizeAndOrientation(page);

        return reportBuilder;
    }

}
