package com.zetra.econsig.service.upload;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.UploadControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.DestinatarioEmail;
import com.zetra.econsig.persistence.entity.DestinatarioEmailHome;
import com.zetra.econsig.persistence.entity.TipoArquivo;
import com.zetra.econsig.persistence.entity.TipoArquivoHome;
import com.zetra.econsig.persistence.query.admin.ListaTipoArquivoQuery;

/**
 * <p>Title: UploadControllerBean</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class UploadControllerBean implements UploadController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(UploadControllerBean.class);

    @Override
    public TipoArquivo buscaTipoArquivoByPrimaryKey(String codigoTipoArquivo, AcessoSistema responsavel) throws UploadControllerException {
        TipoArquivo retorno = null;

        try {
            retorno = TipoArquivoHome.findByPrimaryKey(codigoTipoArquivo);
        } catch (FindException ex) {
            LOG.error(ex.getMessageKey(), ex);
            throw new UploadControllerException("mensagem.erro.parametro.nao.encontrado", (AcessoSistema) null, ex);
        }

        return retorno;
    }

    @Override
    public List<TransferObject> buscaTipoArquivoSer(AcessoSistema responsavel) throws UploadControllerException {
        try {
            ListaTipoArquivoQuery query = new ListaTipoArquivoQuery();
            query.tarUploadSer = "S";
            return query.executarDTO();

        } catch (HQueryException ex) {
            throw new UploadControllerException(ex);
        }
    }

    @Override
    public List<String> listarPapeisEnvioEmailUpload(AcessoSistema responsavel) throws UploadControllerException {
        try {
            List<DestinatarioEmail> listDests = DestinatarioEmailHome.listByFunCodigoPapOperador(responsavel.getFunCodigo(), responsavel.getPapCodigo());
            List<String> papCodigos = listDests.stream().map(d -> d.getPapelDestinatario().getPapCodigo()).collect(Collectors.toList());
            return papCodigos;
        } catch (FindException ex) {
            throw new UploadControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

}
