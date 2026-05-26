package br.com.alunoonline.api.service;

import br.com.alunoonline.api.MatriculaAlunoStatusEnum;
import br.com.alunoonline.api.dto.AtualizarNotasRequestDTO;
import br.com.alunoonline.api.dto.DisciplinasAlunoResponseDTO;
import br.com.alunoonline.api.dto.HistoricoAlunoResponseDTO;
import br.com.alunoonline.api.model.Aluno;
import br.com.alunoonline.api.model.MatriculaAluno;
import br.com.alunoonline.api.repository.MatriculaAlunoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class MatriculaAlunoService {

    private static final Double MEDIA_PARA_APROVACAO = 7.0;

    @Autowired
    MatriculaAlunoRepository matriculaAlunoRepository;

    public void criarMatricula(MatriculaAluno matriculaAluno) {
        matriculaAluno.setStatus(MatriculaAlunoStatusEnum.MATRICULADO);
        matriculaAlunoRepository.save(matriculaAluno);
    }

    public void trancarMatricula(Long id) {
        MatriculaAluno matricula = matriculaAlunoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Matricula não encontrada"));

        if (matricula.getStatus().equals(MatriculaAlunoStatusEnum.MATRICULADO)) {
            matricula.setStatus(MatriculaAlunoStatusEnum.TRANCADO);
            matriculaAlunoRepository.save(matricula);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Só é possível trancar com status MATRICULADO");
        }
    }

    public void atualizarNotas(Long id, AtualizarNotasRequestDTO dto) {
        MatriculaAluno matricula = matriculaAlunoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Matricula não encontrada"));

        if (dto.getNota1() != null) matricula.setNota1(dto.getNota1());
        if (dto.getNota2() != null) matricula.setNota2(dto.getNota2());

        if (matricula.getNota1() != null && matricula.getNota2() != null) {
            Double media = (matricula.getNota1() + matricula.getNota2()) / 2;
            matricula.setStatus(media >= MEDIA_PARA_APROVACAO
                    ? MatriculaAlunoStatusEnum.APROVADO
                    : MatriculaAlunoStatusEnum.REPROVADO);
        }

        matriculaAlunoRepository.save(matricula);
    }

    public HistoricoAlunoResponseDTO emitirHistorico(Long alunoId) {

        // 1) Buscar todas as matrículas desse aluno
        List<MatriculaAluno> matriculas =
                matriculaAlunoRepository.findByAlunoId(alunoId);

        if (matriculas.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Nenhuma matrícula encontrada para esse aluno");
        }

        // 2) Pegar os dados do aluno (qualquer matrícula serve)
        Aluno aluno = matriculas.get(0).getAluno();

        // 3) Para cada matrícula, montar um DTO de disciplina
        List<DisciplinasAlunoResponseDTO> disciplinas =
                new ArrayList<>();

        for (MatriculaAluno matricula : matriculas) {
            DisciplinasAlunoResponseDTO disc =
                    new DisciplinasAlunoResponseDTO();

            disc.setNomeDisciplina(matricula.getDisciplina().getNome());
            disc.setNomeProfessor(
                    matricula.getDisciplina().getProfessor().getNome());
            disc.setNota1(matricula.getNota1());
            disc.setNota2(matricula.getNota2());

            // Só calcula a média se as duas notas existem
            if (matricula.getNota1() != null
                    && matricula.getNota2() != null) {
                Double media = (matricula.getNota1()
                        + matricula.getNota2()) / 2;
                disc.setMedia(media);
            }

            disc.setStatus(matricula.getStatus());
            disciplinas.add(disc);
        }

        // 4) Montar a resposta final e retornar
        HistoricoAlunoResponseDTO historico = new HistoricoAlunoResponseDTO();
        historico.setNomeAluno(aluno.getNome());
        historico.setEmailAluno(aluno.getEmail());
        historico.setCpfAluno(aluno.getCpf());
        historico.setDisciplinas(disciplinas);

        return historico;
    }


}
