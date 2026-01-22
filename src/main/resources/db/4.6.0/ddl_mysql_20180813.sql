/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     13/08/2018 12:00:18                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_chave_criptografia_arquivo                         */
/*==============================================================*/
create table tb_chave_criptografia_arquivo
(
   PAP_CODIGO           varchar(32) not null,
   TAR_CODIGO           varchar(32) not null,
   CAA_CODIGO_ENT       varchar(32) not null,
   CAA_CHAVE            text not null,
   CAA_DATA             datetime not null,
   primary key (PAP_CODIGO, TAR_CODIGO, CAA_CODIGO_ENT)
) ENGINE=InnoDB;

alter table tb_chave_criptografia_arquivo add constraint FK_R_722 foreign key (TAR_CODIGO)
      references tb_tipo_arquivo (TAR_CODIGO) on delete restrict on update restrict;

alter table tb_chave_criptografia_arquivo add constraint FK_R_723 foreign key (PAP_CODIGO)
      references tb_papel (PAP_CODIGO) on delete restrict on update restrict;

