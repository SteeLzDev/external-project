/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     07/06/2022 09:14:49                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_usuario_unidade                                    */
/*==============================================================*/
create table tb_usuario_unidade
(
   USU_CODIGO           varchar(32) not null,
   UNI_CODIGO           varchar(32) not null,
   primary key (USU_CODIGO, UNI_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_usuario_unidade add constraint FK_R_873 foreign key (UNI_CODIGO)
      references tb_unidade (UNI_CODIGO) on delete restrict on update restrict;

alter table tb_usuario_unidade add constraint FK_R_874 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;

