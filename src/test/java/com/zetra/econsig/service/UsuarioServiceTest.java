package com.zetra.econsig.service;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zetra.econsig.dao.OcorrenciaUsuarioDao;
import com.zetra.econsig.dao.PerfilUsuarioDao;
import com.zetra.econsig.dao.SenhaAnteriorDao;
import com.zetra.econsig.dao.SenhaAutorizacaoServidorDao;
import com.zetra.econsig.dao.UsuarioCorDao;
import com.zetra.econsig.dao.UsuarioCsaDao;
import com.zetra.econsig.dao.UsuarioCseDao;
import com.zetra.econsig.dao.UsuarioDao;
import com.zetra.econsig.dao.UsuarioOrgDao;
import com.zetra.econsig.dao.UsuarioSerDao;
import com.zetra.econsig.dao.UsuarioSupDao;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.criptografia.JCrypt;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.entity.PerfilUsuario;
import com.zetra.econsig.persistence.entity.Usuario;
import com.zetra.econsig.persistence.entity.UsuarioCor;
import com.zetra.econsig.persistence.entity.UsuarioCsa;
import com.zetra.econsig.persistence.entity.UsuarioCse;
import com.zetra.econsig.persistence.entity.UsuarioOrg;
import com.zetra.econsig.persistence.entity.UsuarioSer;
import com.zetra.econsig.persistence.entity.UsuarioSup;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.OperacaoValidacaoTotpEnum;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UsuarioServiceTest {

    private final SecureRandom random = new SecureRandom();

    @Autowired
    private UsuarioDao usuarioDao;

    @Autowired
    private UsuarioCsaDao usuarioCsaDao;

    @Autowired
    private UsuarioCorDao usuarioCorDao;

    @Autowired
    private UsuarioSerDao usuarioSerDao;

    @Autowired
    private UsuarioCseDao usuarioCseDao;

    @Autowired
    private UsuarioSupDao usuarioSupDao;

    @Autowired
    private UsuarioOrgDao usuarioOrgDao;

    @Autowired
    private PerfilUsuarioDao perfilUsuarioDao;

    @Autowired
    private OcorrenciaUsuarioDao ocorrenciaUsuarioDao;

    @Autowired
    private SenhaAnteriorDao senhaAnteriorDao;

    @Autowired
    private SenhaAutorizacaoServidorDao senhaAutorizacaoServidorDao;

    public String getCsaCodigo(String login) {
        return usuarioCsaDao.getCsaCodigoByUsuLogin(login);
    }

    public String getCorCodigo(String login) {
        return usuarioCorDao.getCorCodigoByUsuLogin(login);
    }

    public String getSerCodigo(String login) {
        return usuarioSerDao.getSerCodigoByUsuLogin(login);
    }

    public String getUsuCodigoServ(String login) {
        return usuarioSerDao.getUsuCodByUsuLogin(login);
    }

    public Usuario getUsuario(String login) {
        return usuarioDao.findByUsuLogin(login);
    }

    public String criarUsuario(String login, String status, String cpf) {
        return criarUsuario(login, status, cpf, null);
    }

    public String criarUsuario(String login, String status, String cpf, String email) {
        Usuario usuario = usuarioDao.findByUsuLogin(login);

        if (usuario == null) {
            Date dataAtual = DateHelper.getSystemDatetime();
            if (email == null) {
                email = "usuario" + random.nextInt() + "@econsig.com.br";
            }

            usuario = new Usuario();
            usuario.setUsuCodigo(String.valueOf(random.nextInt()));
            usuario.setStuCodigo(status);
            usuario.setUsuDataCad(dataAtual);
            usuario.setUsuLogin(login);
            usuario.setUsuSenha("ZEs8poztus9rc");
            usuario.setUsuNome("Automacao");
            usuario.setUsuEmail(email);
            usuario.setUsuDataExpSenha(dataAtual);
            usuario.setUsuCpf(cpf);
            usuario.setUsuTel("3136259856");
            usuario.setUsuAutorizaEmailMarketing("S");
            usuario.setUsuOperacoesValidacaoTotp(OperacaoValidacaoTotpEnum.AUTORIZACAO_OPERACAO_SENSIVEL.getCodigo());
            usuarioDao.save(usuario);
        }
        return usuario.getUsuCodigo();
    }

    public void criarUsuarioCse(String login, String cpf, String status) {
        String usuCodigo = criarUsuario(login, status, cpf);

        UsuarioCse usuarioCse = new UsuarioCse();
        usuarioCse.setCseCodigo("1");
        usuarioCse.setUsuCodigo(usuCodigo);
        usuarioCseDao.save(usuarioCse);
    }

    public void criarUsuarioSup(String login, String cpf, String status) {
        String usuCodigo = criarUsuario(login, status, cpf);

        UsuarioSup usuarioSup = new UsuarioSup();
        usuarioSup.setCseCodigo("1");
        usuarioSup.setUsuCodigo(usuCodigo);
        usuarioSupDao.save(usuarioSup);
    }

    public void criarUsuarioSup(String login, String cpf, String status, String email) {
        String usuCodigo = criarUsuario(login, status, cpf, email);

        UsuarioSup usuarioSup = new UsuarioSup();
        usuarioSup.setCseCodigo("1");
        usuarioSup.setUsuCodigo(usuCodigo);
        usuarioSupDao.save(usuarioSup);
    }

    public void criarUsuarioCsa(String login, String cpf, String status, String usuLoginCsa) {
        String csaCodigo = usuarioCsaDao.getCsaCodigoByUsuLogin(usuLoginCsa);
        criarUsuarioCsaNaConsignataria(login, cpf, null, status, csaCodigo);
    }

    public void criarUsuarioCsaNaConsignataria(String login, String cpf, String email, String status, String csaCodigo) {
        String usuCodigo = criarUsuario(login, status, cpf, email);

        UsuarioCsa usuarioCsa = new UsuarioCsa();
        usuarioCsa.setCsaCodigo(csaCodigo);
        usuarioCsa.setUsuCodigo(usuCodigo);
        usuarioCsaDao.save(usuarioCsa);
    }

    public void criarUsuarioCsaNaConsignatariaCasoNaoExista(String login, String cpf, String email, String status, String csaCodigo) {
        Usuario usuario = usuarioDao.findByUsuLogin(login);
        if (usuario == null) {
            criarUsuarioCsaNaConsignataria(login, cpf, email, status, csaCodigo);
        }
    }

    public void criarUsuarioOrg(String login, String cpf, String status, String usuLoginOrg) {
        String usuCodigo = criarUsuario(login, status, cpf);
        String orgCodigo = usuarioOrgDao.getOrgCodigoByUsuLogin(usuLoginOrg);

        UsuarioOrg usuarioOrg = new UsuarioOrg();
        usuarioOrg.setOrgCodigo(orgCodigo);
        usuarioOrg.setUsuCodigo(usuCodigo);
        usuarioOrgDao.save(usuarioOrg);
    }

    public void criarUsuarioCor(String login, String cpf, String status, String usuLoginCor) {
        String usuCodigo = criarUsuario(login, status, cpf);
        String corCodigo = usuarioCorDao.getCorCodigoByUsuLogin(usuLoginCor);

        UsuarioCor usuarioOrg = new UsuarioCor();
        usuarioOrg.setCorCodigo(corCodigo);
        usuarioOrg.setUsuCodigo(usuCodigo);
        usuarioCorDao.save(usuarioOrg);
    }

    public Usuario criarUsuarioSer(String login, String senhaPlana, String nome, String cpf, String status, String serCodigo) {
        return criarUsuarioSer(login, senhaPlana, nome, cpf, status, serCodigo, null, null, null);
    }

    public Usuario criarUsuarioSer(String login, String senhaPlana, String nome, String cpf, String status, String serCodigo, String usuEmail, Collection<String> ipList, Collection<String> dnsList) {
        try {
            Usuario usuario = new Usuario();
            usuario.setUsuCodigo(DBHelper.getNextId());
            usuario.setStuCodigo(status);
            usuario.setUsuDataCad(DateHelper.getSystemDate());
            usuario.setUsuLogin(login);
            usuario.setUsuSenha(JCrypt.crypt(senhaPlana));
            usuario.setUsuNome(nome);
            usuario.setUsuDataExpSenha(DateHelper.addDays(DateHelper.getSystemDate(), 30));
            usuario.setUsuCpf(cpf);
            usuario.setUsuAutorizaEmailMarketing("S");
            usuario.setUsuOperacoesValidacaoTotp(OperacaoValidacaoTotpEnum.AUTORIZACAO_OPERACAO_SENSIVEL.getCodigo());
            usuario.setUsuEmail(usuEmail);

            if (ipList != null && !ipList.isEmpty()) {
                usuario.setUsuIpAcesso(ipList.stream().collect(Collectors.joining(";")));
            }

            if (dnsList != null && !dnsList.isEmpty()) {
                usuario.setUsuDdnsAcesso(dnsList.stream().collect(Collectors.joining(";")));
            }

            usuario = usuarioDao.save(usuario);
            String usuCodigo = usuario.getUsuCodigo();

            UsuarioSer usuarioSer = new UsuarioSer();
            usuarioSer.setSerCodigo(serCodigo);
            usuarioSer.setUsuCodigo(usuCodigo);
            usuarioSerDao.save(usuarioSer);

            PerfilUsuario perfilUsuario = new PerfilUsuario();
            perfilUsuario.setUsuCodigo(usuCodigo);
            perfilUsuario.setPerCodigo(CodedValues.PER_CODIGO_SERVIDOR);
            perfilUsuarioDao.save(perfilUsuario);

            return usuario;
        } catch (MissingPrimaryKeyException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void alterarUsuExigeCertificado(String login, String status) {
        Usuario usuario = usuarioDao.findByUsuLogin(login);

        usuario.setUsuExigeCertificado(status);
        usuarioDao.save(usuario);
    }

    public void alterarUsuIPAcesso(String login, String usuIpAcesso) {
        Usuario usuario = usuarioDao.findByUsuLogin(login);

        usuario.setUsuIpAcesso(usuIpAcesso);
        usuarioDao.save(usuario);
    }

    public void alterarStatusUsuario(String login, String status) {
        Usuario usuario = usuarioDao.findByUsuLogin(login);

        usuario.setStuCodigo(status);
        usuarioDao.save(usuario);
    }

    public void alterarSenhaUsuario(String login, String senha) {
        Usuario usuario = usuarioDao.findByUsuLogin(login);

        usuario.setUsuSenha(senha);
        usuarioDao.save(usuario);
    }

    public void alterarUsuario(Usuario usuario) {
        usuarioDao.save(usuario);
    }

    public void removerSenhaAutorizacaoServidor(String login) {
        Usuario usuario = usuarioDao.findByUsuLogin(login);
        senhaAutorizacaoServidorDao.removeByUsuCodigo(usuario.getUsuCodigo());
    }

    public void removerUsuario(String usuCodigo) {
        ocorrenciaUsuarioDao.removeByUsuCodigo(usuCodigo);
        senhaAutorizacaoServidorDao.removeByUsuCodigo(usuCodigo);
        senhaAnteriorDao.removeByUsuCodigo(usuCodigo);
        usuarioCseDao.removeByUsuCodigo(usuCodigo);
        usuarioCsaDao.removeByUsuCodigo(usuCodigo);
        usuarioCorDao.removeByUsuCodigo(usuCodigo);
        usuarioOrgDao.removeByUsuCodigo(usuCodigo);
        usuarioSerDao.removeByUsuCodigo(usuCodigo);
        usuarioSupDao.removeByUsuCodigo(usuCodigo);
    	usuarioDao.deleteById(usuCodigo);
    }
}
