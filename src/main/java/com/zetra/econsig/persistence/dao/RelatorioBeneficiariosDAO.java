package com.zetra.econsig.persistence.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.zetra.econsig.exception.ExportaArquivosBeneficioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: RelatorioBeneficiariosDAO</p>
 * <p>Description: Interface DAO para o reletorio de Beneficiario</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: Nostrum Consultoria e Projetos.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface RelatorioBeneficiariosDAO {

    public List<String> geraRelatorioBeneficiarios(List<String> orgaos, Date periodo, String nomeArquivoFinalTexto, String pathRelatorioConcessao,
            String nomeArqConfEntradaAbsoluto, String nomeArqConfSaidaAbsoluto, String nomeArqConfTradutorAbsoluto, Calendar dataAtual, AcessoSistema responsavel) throws ExportaArquivosBeneficioControllerException;
}
