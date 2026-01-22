package com.zetra.econsig.service.beneficios;

import java.util.Date;
import java.util.List;

import com.zetra.econsig.exception.ExportaArquivosBeneficioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ExportaArquivosBeneficioController</p>
 * <p>Description: Interface para exportação de arquivos do modulo Beneficio</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ExportaArquivosBeneficioController {

    public void geraRelatorioBeneficiariosEConcessoesDeBeneficios(List<String> orgaos, Date periodo, Date periodoDataInicio,
            Date periodoDataFim, AcessoSistema responsavel) throws ExportaArquivosBeneficioControllerException;

    public void exportaArquivosOperadoras(boolean reexporta, Date dataInicioIntegracaoOperadoraInformada, List<String> tipoOperacaoArquivoOperadora, List<String> csaCodigo,
            List<String> rseCodigo, List<String> orgCodigo, List<String> estCodigo, AcessoSistema responsavel) throws ExportaArquivosBeneficioControllerException;

    public void geraRelatorioBeneficiosConsolidadosDirf(List<String> csaCodigos, String orgCodigo, String nomeArqRetorno, Date periodo, AcessoSistema responsavel) throws ExportaArquivosBeneficioControllerException;
}