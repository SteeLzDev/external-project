package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.values.AgendamentoEnum;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: VinculoRegistroServidorHome</p>
 * <p>Description: Classe Home para a entidade VinculoRegistroServidor</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class VinculoRegistroServidorHome extends AbstractEntityHome {

    public static VinculoRegistroServidor findByPrimaryKey(String vrsCodigo) throws FindException {
        final VinculoRegistroServidor vinculoRegistroServidor = new VinculoRegistroServidor();
        vinculoRegistroServidor.setVrsCodigo(vrsCodigo);
        return find(vinculoRegistroServidor, vrsCodigo);
    }

    public static VinculoRegistroServidor create(String vrsIdentificador, String vrsDescricao) throws CreateException {
        try {
            final VinculoRegistroServidor bean = new VinculoRegistroServidor();

            bean.setVrsCodigo(DBHelper.getNextId());
            bean.setVrsIdentificador(vrsIdentificador);
            bean.setVrsDescricao(vrsDescricao);
            bean.setVrsAtivo(CodedValues.STS_ATIVO);
            bean.setVrsDataCriacao(DateHelper.getSystemDatetime());
            create(bean);
            return bean;
        } catch (final MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        }
    }

    public static List<VinculoRegistroServidor> listaNovosVinculos() throws FindException{
        String query = "SELECT vrs FROM VinculoRegistroServidor vrs WHERE (vrs.vrsDataCriacao >= (SELECT MAX(oagDataInicio) FROM OcorrenciaAgendamento oag WHERE oag.agdCodigo = :agdCodigo ) OR vrs.vrsDataCriacao >= :dataAtual ) ";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("dataAtual", DateHelper.getSystemDate());
        parameters.put("agdCodigo", AgendamentoEnum.ENVIO_NOTIFICACAO_EMAIL_NOVO_VINCULO_CSA.getCodigo());

        return findByQuery(query, parameters);
    }

    public static List<VinculoRegistroServidor> listVinculosRseParaCsa(String csaCodigo) throws FindException {
        String query = "SELECT vrs FROM VinculoRegistroServidor vrs WHERE NOT EXISTS (SELECT 1 FROM VinculoCsaRse vcr INNER JOIN VinculoConsignataria tvc ON tvc.csaCodigo = :csaCodigo WHERE vrs.vrsCodigo = vcr.vrsCodigo ) ";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("csaCodigo", csaCodigo);

        return findByQuery(query, parameters);

    }

    public static List<VinculoRegistroServidor> findVcsCodigo(String vcsCodigo) throws FindException {
        String query = "SELECT vrs FROM VinculoRegistroServidor vrs WHERE EXISTS (SELECT 1 FROM VinculoCsaRse vcr WHERE vrs.vrsCodigo = vcr.vrsCodigo AND vcr.vcsCodigo = :vcsCodigo ) ";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("vcsCodigo", vcsCodigo);

        return findByQuery(query, parameters);
    }
}
