/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     20/05/2022 11:24:54                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_campo_usuario                                      */
/*==============================================================*/
create table tb_campo_usuario
(
   USU_CODIGO           varchar(32) not null,
   CAU_CHAVE            varchar(200) not null,
   CAU_VALOR            char(1) not null,
   primary key (USU_CODIGO, CAU_CHAVE)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_campo_usuario add constraint FK_R_869 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;

