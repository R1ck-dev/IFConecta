package com.henrique.ifconecta.desktop.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.henrique.ifconecta.desktop.core.AsyncRunner;
import com.henrique.ifconecta.desktop.core.http.ApiException;
import com.henrique.ifconecta.desktop.model.Curso;
import com.henrique.ifconecta.desktop.model.DisciplinaResumo;
import com.henrique.ifconecta.desktop.service.CursoService;
import com.henrique.ifconecta.desktop.ui.Icons;
import com.henrique.ifconecta.desktop.ui.Toast;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Tela Cursos — lista os cursos do campus com suas disciplinas.
 * Port de cursos.jsx do front web.
 */
public class CursosController {

    @FXML private VBox listaBox;
    @FXML private StackPane loadingBox;

    @FXML
    private void initialize() {
        carregar();
    }

    private void carregar() {
        mostrarLoading(true);
        AsyncRunner.run(
                () -> {
                    List<Curso> cursos = CursoService.listar();
                    List<DisciplinaResumo> disciplinas = CursoService.disciplinas();
                    return new Dados(cursos, disciplinas);
                },
                dados -> {
                    mostrarLoading(false);
                    renderizar(dados.cursos(), agruparPorCurso(dados.disciplinas()));
                },
                erro -> {
                    mostrarLoading(false);
                    if (!(erro instanceof ApiException api && api.isUnauthorized())) {
                        Toast.error("Não foi possível carregar os cursos", mensagem(erro));
                    }
                    renderizar(List.of(), Map.of());
                });
    }

    private static Map<String, List<DisciplinaResumo>> agruparPorCurso(List<DisciplinaResumo> disciplinas) {
        Map<String, List<DisciplinaResumo>> mapa = new LinkedHashMap<>();
        for (DisciplinaResumo d : disciplinas) {
            mapa.computeIfAbsent(d.cursoId(), k -> new ArrayList<>()).add(d);
        }
        return mapa;
    }

    private void renderizar(List<Curso> cursos, Map<String, List<DisciplinaResumo>> porCurso) {
        listaBox.getChildren().clear();
        if (cursos.isEmpty()) {
            listaBox.getChildren().add(estadoVazio());
            return;
        }
        for (Curso curso : cursos) {
            listaBox.getChildren().add(cartaoCurso(curso,
                    porCurso.getOrDefault(curso.id(), List.of())));
        }
    }

    private VBox cartaoCurso(Curso curso, List<DisciplinaResumo> disciplinas) {
        VBox cartao = new VBox(10);
        cartao.getStyleClass().add("card");

        Label nome = new Label(curso.nome());
        nome.getStyleClass().add("item-title");
        nome.setWrapText(true);

        HBox chips = new HBox(8);
        chips.setAlignment(Pos.CENTER_LEFT);
        if (curso.sigla() != null && !curso.sigla().isBlank()) {
            chips.getChildren().add(chip(curso.sigla(), false));
        }
        if (curso.modalidade() != null && !curso.modalidade().isBlank()) {
            chips.getChildren().add(chip(curso.modalidade(), true));
        }

        cartao.getChildren().addAll(nome, chips);

        if (disciplinas.isEmpty()) {
            Label vazio = new Label("Sem disciplinas cadastradas");
            vazio.getStyleClass().add("item-meta");
            cartao.getChildren().add(vazio);
        } else {
            FlowPane disciplinasPane = new FlowPane(8, 8);
            for (DisciplinaResumo d : disciplinas) {
                disciplinasPane.getChildren().add(
                        chip(d.nome() + " (" + d.cargaHoraria() + "h)", true));
            }
            cartao.getChildren().add(disciplinasPane);
        }
        return cartao;
    }

    private Label chip(String texto, boolean mudo) {
        Label chip = new Label(texto);
        chip.getStyleClass().add("chip");
        if (mudo) {
            chip.getStyleClass().add("chip-muted");
        }
        return chip;
    }

    private VBox estadoVazio() {
        VBox caixa = new VBox(8);
        caixa.getStyleClass().add("empty");

        StackPane icone = new StackPane(Icons.of("fth-book", 24));
        icone.getStyleClass().add("empty-icon");

        Label titulo = new Label("Nenhum curso cadastrado");
        titulo.getStyleClass().add("empty-title");

        Label msg = new Label("Quando houver cursos no campus, eles aparecem aqui.");
        msg.getStyleClass().add("empty-msg");
        msg.setWrapText(true);

        caixa.getChildren().addAll(icone, titulo, msg);
        return caixa;
    }

    private void mostrarLoading(boolean visivel) {
        loadingBox.setVisible(visivel);
        loadingBox.setManaged(visivel);
    }

    private static String mensagem(Throwable erro) {
        if (erro instanceof ApiException api) {
            return api.getMessage();
        }
        return "Tente novamente em instantes.";
    }

    private record Dados(List<Curso> cursos, List<DisciplinaResumo> disciplinas) {
    }
}
