/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     11/12/2019 14:42:25                          */
/*==============================================================*/


alter table tb_servidor
   add SER_DATA_IDENTIFICACAO_PESSOAL datetime;

alter table tb_servidor
   add SER_DATA_VALIDACAO_EMAIL datetime;

alter table tb_servidor
   add SER_PERMITE_ALTERAR_EMAIL char(1) not null default 'S';

alter table tb_servidor_validacao
   add SER_DATA_IDENTIFICACAO_PESSOAL datetime;

alter table tb_servidor_validacao
   add SER_DATA_VALIDACAO_EMAIL datetime;

alter table tb_servidor_validacao
   add SER_PERMITE_ALTERAR_EMAIL char(1) not null default 'S';

