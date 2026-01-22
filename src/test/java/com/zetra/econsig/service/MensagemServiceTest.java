package com.zetra.econsig.service;

import com.zetra.econsig.dao.MensagemDao;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.entity.Mensagem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Date;

@Service
@Transactional
public class MensagemServiceTest {

    private final SecureRandom random = new SecureRandom();

    @Autowired
    private MensagemDao mensagemDao;

    public String criarMensagem(String usuCodigo) {
        return criarMensagem(null, usuCodigo);
    }
    public String criarMensagem(String menCodigo, String usuCodigo) {
        Mensagem mensagem = mensagemDao.findByMenCodigo(menCodigo);

        if (mensagem == null) {
            Date dataAtual = DateHelper.getSystemDatetime();

            mensagem = new Mensagem();
            mensagem.setMenCodigo(String.valueOf(random.nextInt()));
            mensagem.setUsuCodigo(usuCodigo);
            mensagem.setMenTitulo("Hoje tem Corinthians");
            mensagem.setMenTexto("Campeão mundial de 2000 e 2012");
            mensagem.setMenData(dataAtual);
            mensagem.setMenExibeCse("S");
            mensagem.setMenExibeCsa("S");
            mensagem.setMenExibeCor("S");
            mensagem.setMenExibeOrg("S");
            mensagem.setMenExibeSer("S");
            mensagem.setMenExibeSup("S");
            mensagem.setMenHtml("N");
            mensagem.setMenPermiteLerDepois("S");
            mensagem.setMenNotificarCseLeitura("N");
            mensagem.setMenBloqCsaSemLeitura("N");
            mensagem.setMenPublica("S");
            mensagem.setMenLidaIndividualmente("S");
            mensagem.setMenPushNotificationSer("N");
            mensagemDao.save(mensagem);
        }
        return mensagem.getMenCodigo();
    }
}
