package com.zetra.econsig.persistence.dao;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.ExportaArquivosBeneficioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.service.beneficios.ContratoBeneficioController;

/**
 * <p>Title: ExportaArquivoOperadoraDAO</p>
 * <p>Description: Interface DAO para a rotinha de exportação do arquivo de beneficio.</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ExportaArquivoOperadoraDAO {

    public void exportaArquivoOperadora(boolean reexporta, String dataFiltroOperacaoMin, String dataFiltroOperacaoMax, List<String> tipoOperacaoArquivoOperadora,
            String csaCodigo, List<String> rseCodigo, List<String> orgCodigo, List<String> estCodigo, Map<String, String> configuracao, ContratoBeneficioController contratoBeneficioController, boolean permiteCancelarBeneficioSemAprovacao, AcessoSistema responsavel) throws ExportaArquivosBeneficioControllerException;

}
