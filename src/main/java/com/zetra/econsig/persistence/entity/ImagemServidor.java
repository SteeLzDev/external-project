package com.zetra.econsig.persistence.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "tb_imagem_servidor")
public class ImagemServidor implements Serializable {

    @Column(name = "ims_nome_arquivo")
    private  String imsNomeArquivo;

    @Id
    @Column(name = "ims_cpf")
    private String imsCpf;

    public String getNomeArquivo() {
        return imsNomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.imsNomeArquivo = nomeArquivo;
    }

    public String getCpf() {
        return imsCpf;
    }

    public void setCpf(String cpf) {
        this.imsCpf = cpf;
    }
}
