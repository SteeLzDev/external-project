package com.zetra.econsig.service.sistema;


import java.util.Date;
import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.TextoSistemaTO;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.TextoSistema;
import com.zetra.econsig.webservice.rest.request.TextoSistemaRestRequest;

/**
 * <p>Title: TextoSistemaController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface TextoSistemaController {

    public int countTextoSistema(CustomTransferObject criterio, AcessoSistema responsavel) throws ConsignanteControllerException;

    public List<TransferObject> lstTextoSistema(CustomTransferObject criterio, int offset, int rows, AcessoSistema responsavel) throws ConsignanteControllerException;

    public void updateTextoSistema(TextoSistemaTO textoSistema, AcessoSistema responsavel) throws ConsignanteControllerException;

    public TextoSistemaTO findTextoSistema(TextoSistemaTO textoSistema, AcessoSistema responsavel) throws ConsignanteControllerException;

    public List<TransferObject> lstMobileTextoSistema(String mtxChave, Date dataUltimaAlteracao, AcessoSistema responsavel) throws ConsignanteControllerException;

    public List<TextoSistema> updateMobileTextoSistema(List<TextoSistemaRestRequest> mensagens, Date texDataAlteracao, AcessoSistema responsavel) throws ConsignanteControllerException;


}