package com.zetra.econsig.persistence.entity;

import java.util.Calendar;
import java.util.Date;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;

/**
 * <p>Title: OcorrenciaEnderecoServidorHome</p>
 * <p>Description: Classe Home da entidade OcorrenciaEnderecoSer.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class OcorrenciaEnderecoServidorHome extends AbstractEntityHome {

    public static OcorrenciaEnderecoSer findByPrimaryKey(String oesCodigo) throws FindException {
        OcorrenciaEnderecoSer ocorrenciaEnderecoSer = new OcorrenciaEnderecoSer();
        ocorrenciaEnderecoSer.setOesCodigo(oesCodigo);
        return find(ocorrenciaEnderecoSer, oesCodigo);
    }

    public static OcorrenciaEnderecoSer create(TipoOcorrencia tipoOcorrencia, Usuario usuario,
            EnderecoServidor enderecoServidor, TipoMotivoOperacao tipoMotivoOperacao, Date data,
            String observacao, String ipAcesso) throws CreateException {
        OcorrenciaEnderecoSer ocorrenciaEnderecoSer = new OcorrenciaEnderecoSer();

        try {
            ocorrenciaEnderecoSer.setOesCodigo(DBHelper.getNextId());
            ocorrenciaEnderecoSer.setTipoOcorrencia(tipoOcorrencia);
            ocorrenciaEnderecoSer.setUsuario(usuario);
            ocorrenciaEnderecoSer.setEnderecoServidor(enderecoServidor);
            ocorrenciaEnderecoSer.setTipoMotivoOperacao(tipoMotivoOperacao);

            if (data == null) {
                data = Calendar.getInstance().getTime();
            }
            ocorrenciaEnderecoSer.setOesData(data);

            ocorrenciaEnderecoSer.setOesObs(observacao);
            ocorrenciaEnderecoSer.setOesIpAcesso(ipAcesso);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        }

        create(ocorrenciaEnderecoSer);

        return ocorrenciaEnderecoSer;
    }
}
