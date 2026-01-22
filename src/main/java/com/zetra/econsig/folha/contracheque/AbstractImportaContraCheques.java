package com.zetra.econsig.folha.contracheque;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.ImportaContrachequesException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.ContrachequeRegistroSer;
import com.zetra.econsig.persistence.entity.ContrachequeRegistroSerId;
import com.zetra.econsig.persistence.entity.ContrachequeRegistroServidorHome;

/**
 * <p>Title: AbstractImportaContraCheques</p>
 * <p>Description: Classe abstrata com classes comuns a serem herdadas por implementações de ImportaContraCheques.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class AbstractImportaContraCheques implements ImportaContracheques {

    private Boolean sobrepoe = true;
    private Boolean ativo = false;

	@Override
	public abstract void importaArquivoContracheques(String nomeArquivo, Date periodo, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ImportaContrachequesException;

	@Override
	public void setSobrepoe(Boolean sobrepoe) {
	    this.sobrepoe = sobrepoe;
	}

	@Override
    public void setAtivo(Boolean ativo) {
	    this.ativo = ativo;
	}

	public boolean getAtivo() {
	    return ativo;
	}

	protected void criarContraChequeRegistroServidor(String rseCodigo, Date ccqPeriodo, String ccqTexto) throws ImportaContrachequesException {
		ContrachequeRegistroSerId id = new ContrachequeRegistroSerId(rseCodigo, ccqPeriodo);

		try {
			ContrachequeRegistroSer contracheque = ContrachequeRegistroServidorHome.findByPrimaryKey(id);
			// se já houver contracheque para este período do servidor, atualiza com os novos dados sendo importados
			if (!sobrepoe) {
			    ccqTexto = contracheque.getCcqTexto() + "\n\n" + ccqTexto.toString();
			}
			contracheque.setCcqTexto(ccqTexto.toString());
			contracheque.setCcqDataCarga(new Timestamp(Calendar.getInstance().getTimeInMillis()));
			ContrachequeRegistroServidorHome.update(contracheque);
		} catch (FindException e) {
			// se ainda não existir contracheque para o período para este servidor, cria um
			try {
				ContrachequeRegistroServidorHome.create(rseCodigo, ccqPeriodo, ccqTexto.toString());
			} catch (CreateException e1) {
				throw new ImportaContrachequesException("mensagem.erro.contracheque.gravar.registro", (AcessoSistema) null, e1);
			}
		} catch (UpdateException e) {
			throw new ImportaContrachequesException("mensagem.erro.contracheque.gravar.registro", (AcessoSistema) null, e);
		}
	}


}
