package com.zetra.econsig.econsig.values;

import org.springframework.beans.factory.annotation.Autowired;

import com.zetra.econsig.econsig.utils.LoginInfo;
import com.zetra.econsig.econsig.utils.LoginInfo.TipoUsuario;
import com.zetra.econsig.helper.ConfigurationHelper;

public class LoginValues {

	private static String arquivoLogin = "src/test/resources/application-parameters-test.properties";
    
	@Autowired
	private static ConfigurationHelper c = new ConfigurationHelper(arquivoLogin);

    public static LoginInfo suporte = new LoginInfo(c.getProp("suporte.user"), c.getProp("suporte.password"), TipoUsuario.SUP);
    public static LoginInfo servidor1 = new LoginInfo(c.getProp("servidor1.user"), c.getProp("servidor1.password"), TipoUsuario.SER);
    public static LoginInfo servidor2 = new LoginInfo(c.getProp("servidor2.user"), c.getProp("servidor2.password"), TipoUsuario.SER);
    public static LoginInfo servidor3 = new LoginInfo(c.getProp("servidor3.user"), c.getProp("servidor3.password"), TipoUsuario.SER);
    public static LoginInfo cse1 = new LoginInfo(c.getProp("cse1.user"), c.getProp("cse1.password"), TipoUsuario.CSE);
    public static LoginInfo cse2 = new LoginInfo(c.getProp("cse2.user"), c.getProp("cse2.password"), TipoUsuario.CSE);
    public static LoginInfo org1 = new LoginInfo(c.getProp("org1.user"), c.getProp("org1.password"), TipoUsuario.ORG);
    public static LoginInfo org2 = new LoginInfo(c.getProp("org2.user"), c.getProp("org2.password"), TipoUsuario.ORG);
    public static LoginInfo csa1 = new LoginInfo(c.getProp("csa1.user"), c.getProp("csa1.password"), TipoUsuario.CSA);
    public static LoginInfo csa2 = new LoginInfo(c.getProp("csa2.user"), c.getProp("csa2.password"), TipoUsuario.CSA);
	public static LoginInfo csa3 = new LoginInfo(c.getProp("csa3.user"), c.getProp("csa3.password"), TipoUsuario.CSA);
    public static LoginInfo cor1 = new LoginInfo(c.getProp("cor1.user"), c.getProp("cor1.password"), TipoUsuario.COR);
    public static LoginInfo cor2 = new LoginInfo(c.getProp("cor2.user"), c.getProp("cor2.password"), TipoUsuario.COR);

    public static LoginInfo[] UM_POR_PAPEL = { suporte, cse1, org1, csa1, cor1, servidor1 };
    public static LoginInfo[] TODOS = { suporte, cse1, cse2, org1, org2, csa1, csa2, cor1, cor2, servidor1 };

    public static String senhaCsa1Plana = c.getProp("senha.csa.plana.1");
    public static String senhaCsa1Cripto = c.getProp("senha.csa.cripto.1");

    public static String senhaCor1Plana = c.getProp("senha.cor.plana.1");
    public static String senhaCor1Cripto = c.getProp("senha.cor.cripto.1");
}
