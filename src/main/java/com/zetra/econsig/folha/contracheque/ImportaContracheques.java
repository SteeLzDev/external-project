package com.zetra.econsig.folha.contracheque;

import java.util.Date;

import com.zetra.econsig.exception.ImportaContrachequesException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ImportaContracheques</p>
 * <p>Description: Interface para implementação da rotina de importação de arquivo de contracheques.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ImportaContracheques {
    public void importaArquivoContracheques(String nomeArquivo, Date periodo, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ImportaContrachequesException;
    public void setSobrepoe(Boolean sobrepoe);
    public void setAtivo(Boolean ativo);
}
