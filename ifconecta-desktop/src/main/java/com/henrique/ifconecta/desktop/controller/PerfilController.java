package com.henrique.ifconecta.desktop.controller;

import com.henrique.ifconecta.desktop.core.AsyncRunner;
import com.henrique.ifconecta.desktop.core.Session;
import com.henrique.ifconecta.desktop.core.http.ApiException;
import com.henrique.ifconecta.desktop.model.MeuPerfil;
import com.henrique.ifconecta.desktop.service.UsuarioService;
import com.henrique.ifconecta.desktop.ui.Avatar;
import com.henrique.ifconecta.desktop.ui.Format;
import com.henrique.ifconecta.desktop.ui.Toast;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Meu perfil — exibe os dados do usuário logado e permite editar o nome
 * e alterar a senha (PATCH /api/usuarios/me e /api/usuarios/me/senha).
 */
public class PerfilController {

    private static final int TAMANHO_MINIMO_SENHA = 8;

    @FXML private StackPane avatarBox;
    @FXML private Label nomeLabel;
    @FXML private Label tipoLabel;
    @FXML private Label emailLabel;
    @FXML private VBox dadosBox;

    @FXML private TextField nomeField;
    @FXML private Button salvarNomeBtn;

    @FXML private PasswordField senhaAtualField;
    @FXML private PasswordField novaSenhaField;
    @FXML private PasswordField confirmarSenhaField;
    @FXML private Label senhaError;
    @FXML private Button alterarSenhaBtn;

    @FXML
    private void initialize() {
        preencher(Session.get().me());
    }

    // ───────── Preenchimento ─────────

    private void preencher(MeuPerfil me) {
        if (me == null) {
            return;
        }
        avatarBox.getChildren().setAll(new Avatar(me.nome(), 64));
        nomeLabel.setText(me.nome());
        tipoLabel.setText(me.tipoLabel());
        emailLabel.setText(me.emailAcad());
        nomeField.setText(me.nome());

        dadosBox.getChildren().clear();
        dadosBox.getChildren().add(linha("Tipo", me.tipoLabel()));
        dadosBox.getChildren().add(linha("Status", textoOuTraco(me.status())));
        dadosBox.getChildren().add(linha("Data de criação", textoOuTraco(Format.dataHora(me.dataCriacao()))));

        if ("ALUNO".equalsIgnoreCase(me.tipo())) {
            dadosBox.getChildren().add(linha("Prontuário", textoOuTraco(me.prontuario())));
        } else if ("PROFESSOR".equalsIgnoreCase(me.tipo())) {
            dadosBox.getChildren().add(linha("SIAPE", textoOuTraco(me.siape())));
        } else if ("INSTITUCIONAL".equalsIgnoreCase(me.tipo())) {
            dadosBox.getChildren().add(linha("Setor", textoOuTraco(me.setor())));
            dadosBox.getChildren().add(linha("Cargo", textoOuTraco(me.cargo())));
        }
    }

    private HBox linha(String rotulo, String valor) {
        HBox linha = new HBox(12);
        linha.setAlignment(Pos.CENTER_LEFT);

        Label label = new Label(rotulo);
        label.getStyleClass().add("def-label");
        label.setMinWidth(140);

        Label value = new Label(valor);
        value.getStyleClass().add("def-value");
        value.setWrapText(true);
        HBox.setHgrow(value, javafx.scene.layout.Priority.ALWAYS);

        linha.getChildren().addAll(label, value);
        return linha;
    }

    // ───────── Editar nome ─────────

    @FXML
    private void onSalvarNome() {
        String novoNome = valor(nomeField);
        if (novoNome.isEmpty()) {
            Toast.error("Nome inválido", "Informe seu nome.");
            return;
        }

        salvarNomeBtn.setDisable(true);
        AsyncRunner.run(
                () -> {
                    UsuarioService.atualizarNome(novoNome);
                    return UsuarioService.meuPerfil();
                },
                atualizado -> {
                    salvarNomeBtn.setDisable(false);
                    Session.get().set(Session.get().token(), atualizado);
                    preencher(atualizado);
                    Toast.success("Nome atualizado!");
                },
                erro -> {
                    salvarNomeBtn.setDisable(false);
                    if (!(erro instanceof ApiException api && api.isUnauthorized())) {
                        Toast.error("Não foi possível atualizar o nome", mensagem(erro));
                    }
                });
    }

    // ───────── Alterar senha ─────────

    @FXML
    private void onAlterarSenha() {
        String senhaAtual = texto(senhaAtualField);
        String novaSenha = texto(novaSenhaField);
        String confirmar = texto(confirmarSenhaField);

        if (senhaAtual.isEmpty()) {
            marcarErroSenha("Informe sua senha atual.");
            return;
        }
        if (novaSenha.length() < TAMANHO_MINIMO_SENHA) {
            marcarErroSenha("A nova senha deve ter ao menos "
                    + TAMANHO_MINIMO_SENHA + " caracteres.");
            return;
        }
        if (!novaSenha.equals(confirmar)) {
            marcarErroSenha("A confirmação não corresponde à nova senha.");
            return;
        }
        limparErroSenha();

        alterarSenhaBtn.setDisable(true);
        AsyncRunner.runVoid(
                () -> UsuarioService.alterarSenha(senhaAtual, novaSenha),
                () -> {
                    alterarSenhaBtn.setDisable(false);
                    senhaAtualField.clear();
                    novaSenhaField.clear();
                    confirmarSenhaField.clear();
                    Toast.success("Senha alterada com sucesso!");
                },
                erro -> {
                    alterarSenhaBtn.setDisable(false);
                    if (!(erro instanceof ApiException api && api.isUnauthorized())) {
                        Toast.error("Não foi possível alterar a senha", mensagem(erro));
                    }
                });
    }

    private void marcarErroSenha(String msg) {
        senhaError.setText(msg);
        senhaError.setVisible(true);
        senhaError.setManaged(true);
    }

    private void limparErroSenha() {
        senhaError.setVisible(false);
        senhaError.setManaged(false);
    }

    // ───────── Auxiliares ─────────

    private static String valor(TextField field) {
        return field.getText() == null ? "" : field.getText().trim();
    }

    private static String texto(PasswordField field) {
        return field.getText() == null ? "" : field.getText();
    }

    private static String textoOuTraco(String valor) {
        return (valor == null || valor.isBlank()) ? "—" : valor;
    }

    private static String mensagem(Throwable erro) {
        if (erro instanceof ApiException api) {
            return api.getMessage();
        }
        return "Tente novamente em instantes.";
    }
}
