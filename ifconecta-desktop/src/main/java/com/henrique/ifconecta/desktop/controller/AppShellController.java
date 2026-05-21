package com.henrique.ifconecta.desktop.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.kordamp.ikonli.javafx.FontIcon;

import com.henrique.ifconecta.desktop.core.Router;
import com.henrique.ifconecta.desktop.core.Session;
import com.henrique.ifconecta.desktop.model.MeuPerfil;
import com.henrique.ifconecta.desktop.ui.Avatar;
import com.henrique.ifconecta.desktop.ui.Icons;
import com.henrique.ifconecta.desktop.ui.Theme;
import com.henrique.ifconecta.desktop.ui.Toast;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Casca da aplicação — port de AppShell/Header/Sidebar (ifconecta-web/src/components/layout.jsx).
 * Header + sidebar fixos; a área central troca de conteúdo via {@link #setContent}.
 */
public class AppShellController {

    @FXML private VBox sidebar;
    @FXML private StackPane contentHost;
    @FXML private Button comunicadoBtn;
    @FXML private Button accountBtn;
    @FXML private FontIcon themeIcon;

    /** view -> botão da sidebar, para alternar o estado "active". */
    private final Map<String, Button> navButtons = new LinkedHashMap<>();
    private ContextMenu accountMenu;

    @FXML
    private void initialize() {
        MeuPerfil me = Session.get().me();

        construirSidebar(me);

        boolean podeComunicar = me != null && me.podeComunicar();
        comunicadoBtn.setVisible(podeComunicar);
        comunicadoBtn.setManaged(podeComunicar);

        themeIcon.setIconLiteral(Theme.isDark() ? "fth-sun" : "fth-moon");
        accountBtn.setGraphic(new Avatar(me == null ? "" : me.nome(), 32));
        accountMenu = construirMenuConta();
    }

    /** Coloca uma tela na área central e marca o item de menu correspondente. */
    public void setContent(String viewName, Parent node) {
        contentHost.getChildren().setAll(node);
        navButtons.forEach((nome, botao) -> {
            botao.getStyleClass().remove("active");
            if (nome.equals(viewName)) {
                botao.getStyleClass().add("active");
            }
        });
    }

    // ───────── Header ─────────

    @FXML
    private void onToggleTheme() {
        Theme.toggle();
        themeIcon.setIconLiteral(Theme.isDark() ? "fth-sun" : "fth-moon");
    }

    @FXML
    private void onComunicado() {
        emBreve("Comunicados");
    }

    @FXML
    private void onNotificacoes() {
        emBreve("Notificações");
    }

    @FXML
    private void onAccountMenu() {
        if (accountMenu.isShowing()) {
            accountMenu.hide();
        } else {
            accountMenu.show(accountBtn, Side.BOTTOM, 0, 6);
        }
    }

    // ───────── Sidebar ─────────

    private void construirSidebar(MeuPerfil me) {
        sidebar.getChildren().setAll(
                secao("Feed"),
                navLink("timeline", "fth-home", "Timeline"),
                navLink("clubes", "fth-users", "Clubes"),
                navLink("notificacoes", "fth-bell", "Notificações"),
                secao("Acadêmico"),
                navLink("academico", "fth-book-open", "Minhas turmas"),
                navLink("cursos", "fth-award", "Cursos"),
                secao("Você"),
                navLink("perfil", "fth-user", "Meu perfil"));

        if (me != null && me.isAdmin()) {
            sidebar.getChildren().addAll(secao("Admin"), navLink("admin", "fth-shield", "Painel"));
        }

        Region espaco = new Region();
        VBox.setVgrow(espaco, Priority.ALWAYS);
        sidebar.getChildren().addAll(espaco, cartaoUsuario(me));
    }

    private Label secao(String texto) {
        Label label = new Label(texto.toUpperCase());
        label.getStyleClass().add("sb-section");
        return label;
    }

    private Button navLink(String view, String iconLiteral, String rotulo) {
        Button botao = new Button(rotulo);
        botao.getStyleClass().add("sb-link");
        botao.setGraphic(Icons.of(iconLiteral, 18));
        botao.setMaxWidth(Double.MAX_VALUE);
        botao.setOnAction(e -> navegar(view, rotulo));
        navButtons.put(view, botao);
        return botao;
    }

    private HBox cartaoUsuario(MeuPerfil me) {
        HBox cartao = new HBox(10);
        cartao.getStyleClass().add("sb-user");
        cartao.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox(1);
        Label nome = new Label(me == null ? "—" : me.nome());
        nome.getStyleClass().add("sb-user-name");
        Label papel = new Label(me == null ? "" : me.tipoLabel());
        papel.getStyleClass().add("sb-user-role");
        info.getChildren().addAll(nome, papel);
        HBox.setHgrow(info, Priority.ALWAYS);

        cartao.getChildren().addAll(new Avatar(me == null ? "" : me.nome(), 36), info);
        return cartao;
    }

    // ───────── Menu de conta ─────────

    private ContextMenu construirMenuConta() {
        ContextMenu menu = new ContextMenu();

        MenuItem perfil = new MenuItem("Meu perfil");
        perfil.setOnAction(e -> emBreve("Meu perfil"));

        MenuItem config = new MenuItem("Configurações");
        config.setOnAction(e -> emBreve("Configurações"));

        MenuItem sair = new MenuItem("Sair");
        sair.setOnAction(e -> logout());

        menu.getItems().addAll(perfil, config, new SeparatorMenuItem(), sair);
        return menu;
    }

    private void logout() {
        Session.get().clear();
        Router.get().showLogin();
        Toast.info("Até logo!", "Você saiu da sua conta.");
    }

    // ───────── Navegação ─────────

    private void navegar(String view, String rotulo) {
        if ("timeline".equals(view)) {
            Router.get().navigate("timeline");
        } else {
            emBreve(rotulo);
        }
    }

    private void emBreve(String tela) {
        Toast.info(tela, "Esta tela chega na próxima fase da migração para desktop.");
    }
}
