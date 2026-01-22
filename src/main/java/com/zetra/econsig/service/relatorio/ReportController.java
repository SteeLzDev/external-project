package com.zetra.econsig.service.relatorio;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.report.config.Relatorio;

import net.sf.jasperreports.engine.JRDataSource;

/**
 * <p> Title: ReportController</p>
 * <p> Description: Interface para as classes que constroem os relatórios</p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ReportController {

    public String makeReport(String formato, CustomTransferObject criterio, Map<String, Object> parameters, Relatorio relatorio, AcessoSistema responsavel) throws ReportControllerException;

    public String makeReport(String formato, CustomTransferObject criterio, Map<String, Object> parameters, Relatorio relatorio, List<Object[]> conteudo, AcessoSistema responsavel) throws ReportControllerException;

    public String makeReport(String formato, Map<String, Object> parameters, Relatorio relatorio, JRDataSource myDataSource, AcessoSistema responsavel) throws ReportControllerException;

}
