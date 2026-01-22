/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     18/06/2018 11:20:43                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_anexo_beneficiario                                 */
/*==============================================================*/
create table tb_anexo_beneficiario
(
   BFC_CODIGO           varchar(32) not null,
   ABF_NOME             varchar(20) not null,
   USU_CODIGO           varchar(32) not null,
   TAR_CODIGO           varchar(32) not null,
   ABF_DESCRICAO        varchar(255) not null,
   ABF_ATIVO            smallint not null,
   ABF_DATA             datetime not null,
   ABF_DATA_REFERENCIA  date,
   ABF_IP_ACESSO        varchar(45),
   primary key (BFC_CODIGO, ABF_NOME)
) ENGINE=InnoDB;

alter table tb_anexo_beneficiario add constraint FK_R_706 foreign key (BFC_CODIGO)
      references tb_beneficiario (BFC_CODIGO) on delete restrict on update restrict;

alter table tb_anexo_beneficiario add constraint FK_R_707 foreign key (TAR_CODIGO)
      references tb_tipo_arquivo (TAR_CODIGO) on delete restrict on update restrict;

alter table tb_anexo_beneficiario add constraint FK_R_708 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;

