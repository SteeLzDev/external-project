/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     01/12/2022 10:33:47                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_imagem_servidor                                    */
/*==============================================================*/
CREATE TABLE tb_imagem_servidor
(
   IMS_CPF              varchar(19)  not null,
   IMS_NOME_ARQUIVO     varchar(100) not null,
   primary key (IMS_CPF)
) ENGINE=InnoDB DEFAULT CHARSET=latin1
;

-- GARANTE QUE A TABELA ANTIGA EXISTA PARA NÃO DAR ERRO AO TENTAR MIGRAR OS DADOS
CREATE TABLE IF NOT EXISTS tb_imagens_servidores (
  cpf varchar(19) NOT NULL,
  nomeArquivo varchar(100) NOT NULL,
  PRIMARY KEY (cpf)
) ENGINE=InnoDB DEFAULT CHARSET=latin1
;

INSERT INTO tb_imagem_servidor (IMS_CPF, IMS_NOME_ARQUIVO)
SELECT cpf, MIN(nomeArquivo)
FROM tb_imagens_servidores
GROUP BY cpf;

DROP TABLE tb_imagens_servidores;
