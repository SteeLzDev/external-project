/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     14/11/2019 14:46:41                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_arquivo_ser                                        */
/*==============================================================*/
create table tb_arquivo_ser
(
   SER_CODIGO           varchar(32) not null,
   ARQ_CODIGO           varchar(32) not null,
   primary key (SER_CODIGO, ARQ_CODIGO)
) ENGINE=InnoDB;

alter table tb_servidor
   add SER_DISPENSA_DIGITAL char(1);

alter table tb_servidor_validacao
   add SER_DISPENSA_DIGITAL char(1);

alter table tb_arquivo_ser add constraint FK_R_783 foreign key (SER_CODIGO)
      references tb_servidor (SER_CODIGO) on delete restrict on update restrict;

alter table tb_arquivo_ser add constraint FK_R_784 foreign key (ARQ_CODIGO)
      references tb_arquivo (ARQ_CODIGO) on delete restrict on update restrict;

