package com.zetra.econsig.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zetra.econsig.dao.ConvenioVinculoRegistroDao;
import com.zetra.econsig.dao.VinculoRegistroServidorDao;
import com.zetra.econsig.persistence.entity.ConvenioVinculoRegistro;
import com.zetra.econsig.persistence.entity.VinculoRegistroServidor;

@Service
public class VinculoRegistroService {

    @Autowired
    private VinculoRegistroServidorDao vinculoRegistroServidorDao;

    @Autowired
    private ConvenioVinculoRegistroDao convenioVinculoRegistroDao;

    public void incluirVinculoRegistroServidor(String vrsCodigo, String vrsIdentificador) {
        VinculoRegistroServidor vinculoRegistroServidor = vinculoRegistroServidorDao.findByVrsAtivo(Short.parseShort("1"));
        if (vinculoRegistroServidor == null) {
            vinculoRegistroServidor = new VinculoRegistroServidor();
            vinculoRegistroServidor.setVrsCodigo(vrsCodigo);
            vinculoRegistroServidor.setVrsIdentificador(vrsIdentificador);
            vinculoRegistroServidor.setVrsDescricao("Vinculado");
            vinculoRegistroServidor.setVrsAtivo(Short.parseShort("1"));
            vinculoRegistroServidorDao.save(vinculoRegistroServidor);
        }
    }

    public void incluirConvenioVinculoRegistro(String vrsCodigo, String csaCodigo, String svcCodigo) {
        ConvenioVinculoRegistro convenioVinculoRegistro = convenioVinculoRegistroDao.findByVrsCodigoAndCsaCodigoAndSvcCodigo(vrsCodigo, csaCodigo, svcCodigo);

        if (convenioVinculoRegistro == null) {
            convenioVinculoRegistro = new ConvenioVinculoRegistro();
            convenioVinculoRegistro.setVrsCodigo(vrsCodigo);
            convenioVinculoRegistro.setCsaCodigo(csaCodigo);
            convenioVinculoRegistro.setSvcCodigo(svcCodigo);
            convenioVinculoRegistroDao.save(convenioVinculoRegistro);
        }
    }
}
