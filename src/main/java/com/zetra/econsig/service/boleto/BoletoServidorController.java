package com.zetra.econsig.service.boleto;

import java.io.File;
import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.BoletoServidorControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: BoletoServidorController</p>
 * <p>Description: Interface Remota para o Session Bean para operações sobre boleto servidor</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface BoletoServidorController {

    public String createBoleto(TransferObject criterio, AcessoSistema responsavel) throws BoletoServidorControllerException;

    public void atualizaDataDownloadBoleto(String bosCodigo, AcessoSistema responsavel) throws BoletoServidorControllerException;

    public List<String> uploadBoleto(File zipCarregado, AcessoSistema responsavel) throws BoletoServidorControllerException;

    public void removeBoleto(String bosCodigo, AcessoSistema responsavel) throws BoletoServidorControllerException;

    public void removeBoletosExpirados(int diasAposEnvio, AcessoSistema responsavel) throws BoletoServidorControllerException;

    public int countBoletoServidor(TransferObject criterio, AcessoSistema responsavel) throws BoletoServidorControllerException;

    public List<TransferObject> listarBoletoServidor(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws BoletoServidorControllerException;

    public TransferObject findBoletoServidor(String bosCodigo, AcessoSistema responsavel) throws BoletoServidorControllerException;

}
