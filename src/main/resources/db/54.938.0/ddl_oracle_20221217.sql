/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     01/12/2022 10:41:53                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_imagem_servidor                                  */
/*==============================================================*/
create table tb_imagem_servidor  (
   ims_cpf              varchar2(19)                    not null,
   ims_nome_arquivo     varchar2(100)                   not null,
   constraint pk_tb_imagem_servidor primary key (ims_cpf)
);

