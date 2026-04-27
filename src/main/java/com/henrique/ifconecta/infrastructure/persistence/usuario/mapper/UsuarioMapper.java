package com.henrique.ifconecta.infrastructure.persistence.usuario.mapper;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

import com.henrique.ifconecta.domain.usuario.model.Aluno;
import com.henrique.ifconecta.domain.usuario.model.Institucional;
import com.henrique.ifconecta.domain.usuario.model.Professor;
import com.henrique.ifconecta.domain.usuario.model.Usuario;
import com.henrique.ifconecta.infrastructure.persistence.usuario.entity.AlunoJpaEntity;
import com.henrique.ifconecta.infrastructure.persistence.usuario.entity.InstitucionalJpaEntity;
import com.henrique.ifconecta.infrastructure.persistence.usuario.entity.ProfessorJpaEntity;
import com.henrique.ifconecta.infrastructure.persistence.usuario.entity.UsuarioJpaEntity;

@Component
public class UsuarioMapper {
    // Domain -> JPA
    public UsuarioJpaEntity toEntity(Usuario usuario) {
        if (usuario instanceof Aluno aluno) {
            AlunoJpaEntity entity = new AlunoJpaEntity();
            preencherDadosBaseParaEntity(entity, aluno);
            entity.setProntuario(aluno.getProntuario());
            return entity;
        }

        if (usuario instanceof Professor professor) {
            ProfessorJpaEntity entity = new ProfessorJpaEntity();
            preencherDadosBaseParaEntity(entity, professor);
            entity.setSiape(professor.getSiape());
            return entity;
        }

        if (usuario instanceof Institucional institucional) {
            InstitucionalJpaEntity entity = new InstitucionalJpaEntity();
            preencherDadosBaseParaEntity(entity, institucional);
            entity.setSetor(institucional.getSetor());
            entity.setCargo(institucional.getCargo());
            return entity;
        }
        
        throw new IllegalArgumentException("Tipo de usuário não suportado pelo Mapper");
    }

    // JPA -> Domain
    public Usuario toDomain(UsuarioJpaEntity entity) {

        UsuarioJpaEntity realEntity = (UsuarioJpaEntity) Hibernate.unproxy(entity);

        if (realEntity instanceof AlunoJpaEntity alunoEntity) {
            return new Aluno(
                alunoEntity.getId(),
                alunoEntity.getNome(),
                alunoEntity.getEmailAcad(),
                alunoEntity.getSenhaHash(),
                alunoEntity.getStatus(),
                alunoEntity.getRole(),
                alunoEntity.getDataCriacao(),
                alunoEntity.getProntuario()
            );
        }

        if (realEntity instanceof ProfessorJpaEntity professorEntity) {
            return new Professor(
                professorEntity.getId(),
                professorEntity.getNome(),
                professorEntity.getEmailAcad(),
                professorEntity.getSenhaHash(),
                professorEntity.getStatus(),
                professorEntity.getRole(),
                professorEntity.getDataCriacao(),
                professorEntity.getSiape()
            );
        }

        if (realEntity instanceof InstitucionalJpaEntity instEntity) {
            return new Institucional(
                instEntity.getId(),
                instEntity.getNome(),
                instEntity.getEmailAcad(),
                instEntity.getSenhaHash(),
                instEntity.getStatus(),
                instEntity.getRole(),
                instEntity.getDataCriacao(),
                instEntity.getSetor(),
                instEntity.getCargo()
            );
        }

        throw new IllegalArgumentException("Tipo de entidade JPA não suportado pelo Mapper");
    }

    private void preencherDadosBaseParaEntity(UsuarioJpaEntity entity, Usuario domain) {
        entity.setId(domain.getId());
        entity.setNome(domain.getNome());
        entity.setEmailAcad(domain.getEmailAcad());
        entity.setSenhaHash(domain.getSenhaHash());
        entity.setStatus(domain.getStatus());
        entity.setRole(domain.getRole());
        entity.setDataCriacao(domain.getDataCriacao());
    }
}
