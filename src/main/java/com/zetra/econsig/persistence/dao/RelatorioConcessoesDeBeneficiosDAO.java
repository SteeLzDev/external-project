package com.zetra.econsig.persistence.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.zetra.econsig.exception.ExportaArquivosBeneficioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: RelatorioConcessoesDeBeneficiosDAO</p>
 * <p>Description: Interface DAO para o reletorio de Concessões de Beneficios</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: Nostrum Consultoria e Projetos.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface RelatorioConcessoesDeBeneficiosDAO {

    public List<String> geraRelatorioConcessoesDeBeneficios(List<String> orgaos, Date periodo, String nomeArquivoFinalTexto, String pathRelatorioConcessao,
            String nomeArqConfEntradaAbsoluto, String nomeArqConfSaidaAbsoluto, String nomeArqConfTradutorAbsoluto,
            Calendar dataAtual, boolean reenviaConceCadastroReativacao, AcessoSistema responsavel) throws ExportaArquivosBeneficioControllerException;

}
