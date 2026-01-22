package com.zetra.econsig.bdd.steps.maps;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class BeneficiosElementMap {

    @FindBy(linkText = "Novo benefício")
    public WebElement novoBeneficio;

    @FindBy(id = "tb_consignataria.csa_codigo")
    public WebElement operadora;

    @FindBy(id = "tb_natureza_servico.nse_codigo")
    public WebElement natureza;

    @FindBy(xpath = "//*[@id=\"tb_servico.svc_codigo\"]/option[3]")
    public WebElement servicoPlanoDependente;

    @FindBy(name = "ben_descricao")
    public WebElement descricaoBeneficio;

    @FindBy(name = "ben_codigo_plano")
    public WebElement codigoPlano;

    @FindBy(name = "ben_codigo_registro")
    public WebElement codigoRegistro;

    @FindBy(name = "ben_codigo_contrato")
    public WebElement codigoContrato;

    @FindBy(id = "tb_tipo_beneficiario.tib_codigo")
    public WebElement tipoBeneficiario;

    @FindBy(linkText = "Bloquear/Desbloquear")
    public WebElement bloquearDesbloquear;

    @FindBy(linkText = "Salvar")
    public WebElement salvar;

    @FindBy(linkText = "Editar")
    public WebElement editar;

    @FindBy(linkText = "Excluir")
    public WebElement excluir;
}
