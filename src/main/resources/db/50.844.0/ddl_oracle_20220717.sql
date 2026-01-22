/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     26/04/2022 14:18:19                          */
/*==============================================================*/


/*==============================================================*/
/* Table: "tb_recurso_sistema"                                  */
/*==============================================================*/
create table tb_recurso_sistema  (
   res_chave            varchar2(200)                   not null,
   res_conteudo         blob                            not null,
   constraint pk_tb_recurso_sistema primary key (res_chave)
);

