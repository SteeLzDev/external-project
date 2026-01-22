/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     04/04/2019 12:01:24                          */
/*==============================================================*/


drop table if exists tb_cep;

/*==============================================================*/
/* Table: tb_cep                                                */
/*==============================================================*/
create table tb_cep
(
   CEP_CODIGO           char(8) not null,
   CEP_LOGRADOURO       varchar(255) not null,
   CEP_BAIRRO           varchar(100) not null,
   CEP_CIDADE           varchar(100) not null,
   CEP_ESTADO           varchar(100) not null,
   CEP_ESTADO_SIGLA     varchar(5) not null,
   primary key (CEP_CODIGO)
) ENGINE=InnoDB;
