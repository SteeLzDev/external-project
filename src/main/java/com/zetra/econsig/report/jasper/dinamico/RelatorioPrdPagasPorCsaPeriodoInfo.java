package com.zetra.econsig.report.jasper.dinamico;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
 * <p>Title: RelatorioPrdPagasPorCsaPeriodoInfo</p>
 * <p>Description: Definição de colunas dinâmicas do relatório sintético de parcelas pagas por consignatária e período</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioPrdPagasPorCsaPeriodoInfo extends DynamicReportInfo {

    public RelatorioPrdPagasPorCsaPeriodoInfo(Relatorio relatorio) {
        super(relatorio);
    }

    @Override
    protected DynamicReportBuilder reportDesign(AcessoSistema responsavel) {
        DynamicReportBuilder reportBuilder = new DynamicReportBuilder();
        reportBuilder.setWhenNoDataAllSectionNoDetail();

        LinkedList<String> campos = (LinkedList<String>) criterioQuery.getAttribute("campos");
        int countMes = (Integer) criterioQuery.getAttribute("countMes");

        Style borda = new Style("borda");

     // coluna csa identificador
        ColumnBuilder fixedColumnBuilder = buildColumn(campos.remove(), 150, false, "CLAVE", String.class.getName(), borda);
        AbstractColumn columnCsaIdentificador = fixedColumnBuilder.build();
        reportBuilder.addColumn(columnCsaIdentificador);

        // coluna csa nome
        fixedColumnBuilder = buildColumn(campos.remove(), 350, false, "DESCRIPCION", String.class.getName(), borda);
        AbstractColumn columnCsaNome = fixedColumnBuilder.build();
        reportBuilder.addColumn(columnCsaNome);

        List<SimpleColumn> lstColumns = new ArrayList<>();

        for (int i = 1; i < (countMes); i++) {
            String chave = "PAGO_" + i;

            ColumnBuilder columnBuilder = buildColumn(campos.remove(), 150, false, chave, Number.class.getName(), borda);

            SimpleColumn columnPago = (SimpleColumn) columnBuilder.build();
            lstColumns.add(columnPago);

            reportBuilder.addColumn(columnPago);
        }

        OperationColumn total = new OperationColumn();
        total.setColumns(lstColumns);
        total.setColumnOperation(ColumnOperation.SUM);
        total.setTitle(ApplicationResourcesHelper.getMessage("rotulo.relatorio.saldo.por.csa.total", responsavel));

        total.setWidth(150);
        total.setFixedWidth(false);
        total.setStyle(borda);
        total.setHeaderStyle(getHeaderStyle());
        reportBuilder.addColumn(total);

        Page page = new Page();
        page.setWidth((countMes * 150) + 650);
        page.setOrientationPortrait(false);
        reportBuilder.setPageSizeAndOrientation(page);

        return reportBuilder;

    }

}
