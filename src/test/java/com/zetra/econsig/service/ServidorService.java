package com.zetra.econsig.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.dao.ServidorDao;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.entity.Servidor;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Transactional
public class ServidorService {

    @Autowired
    private ServidorDao servidorDao;

    public Servidor obterServidorPeloCpf(String cpfServidor) {
        return servidorDao.findBySerCpf(cpfServidor);
    }

    public Servidor incluirServidor(String serNome, String serCpf) {
        return incluirServidor(serNome, serCpf, null);
    }

    public Servidor incluirServidor(String serNome, String serCpf, String email) {
        try {
            Servidor ser = new Servidor();
            ser.setSerCodigo(DBHelper.getNextId());
            ser.setSerNome(serNome);
            ser.setSerCpf(serCpf);
            ser.setSerPermiteAlterarEmail("S");
            ser.setSerEmail(email);

            return servidorDao.save(ser);
        } catch (MissingPrimaryKeyException ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    public Servidor alterarServidor(Servidor servidor) {
        return servidorDao.save(servidor);
    }

    public void excluirServidor(String serCodigo) {
        servidorDao.deleteById(serCodigo);
    }
}
