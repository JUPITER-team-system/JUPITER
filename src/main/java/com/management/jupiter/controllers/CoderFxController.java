package com.management.jupiter.controllers;

import com.management.jupiter.models.Clan;
import com.management.jupiter.models.Tl;
import com.management.jupiter.models.User;
import com.management.jupiter.repository.ClanRepository;
import com.management.jupiter.repository.impl.ClanRepositoryImpl;
import com.management.jupiter.security.SessionManager;
import com.management.jupiter.services.ClanService;
import com.management.jupiter.util.ViewLoader;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class CoderFxController {

    @FXML private AnchorPane contentPane;
    @FXML private Label      headerLabel;
    @FXML private Button     btnTeamwork;
    @FXML private Button     btnTLS;
    @FXML private Button     btnCell;
    @FXML private Button     btnTLSMessages;

    private final ClanRepository     clanRepo    = new ClanRepository();
    private final ClanService        clanService = new ClanService(clanRepo);
    private final ClanRepositoryImpl clanImpl    = new ClanRepositoryImpl();

    private Button activeBtn;

    // Clan del coder (cacheado al inicializar)
    private Clan myClan = null;

    @FXML
    public void initialize() {
        User u = SessionManager.getInstance().getCurrentUser();
        if (u != null) headerLabel.setText("JUPITER  ·  " + u.getUsername());
        // Buscar el clan del coder al cargar
        loadMyClan();
        setActiveButton(btnTeamwork);
        handleTeamwork();
    }

    /** Busca y cachea el clan asignado a este coder. */
    private void loadMyClan() {
        try {
            User me = SessionManager.getInstance().getCurrentUser();
            if (me == null) return;
            List<Clan> all = clanService.readAll();
            Optional<Clan> found = all.stream()
                    .filter(c -> c.getCoders().stream().anyMatch(co -> me.getId().equals(co.getId())))
                    .findFirst();
            myClan = found.orElse(null);
        } catch (Exception ignored) {}
    }

    // ══════════════════════════════════════════════════════════
    //  MY TEAMWORK — solo coders del mismo clan
    // ══════════════════════════════════════════════════════════
    @FXML
    private void handleTeamwork() {
        setActiveButton(btnTeamwork);
        VBox root = buildRoot("My Teamwork");

        String clanLabel = myClan != null ? "Clan: " + myClan.getName() : "No clan assigned yet.";
        Label info = new Label(clanLabel);
        info.getStyleClass().add("info-label");

        TableView<User> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("jupiter-table");
        table.setPlaceholder(new Label("No teammates found."));
        table.getColumns().addAll(List.of(
            col("Name",  c -> new SimpleStringProperty(c.getValue().getUsername())),
            col("Email", c -> new SimpleStringProperty(c.getValue().getEmail())),
            col("Role",  c -> new SimpleStringProperty(c.getValue().getRole().name()))
        ));
        VBox.setVgrow(table, Priority.ALWAYS);

        loadMyTeammates(table);
        HBox bar = actionBar(btn("⟳  Refresh", () -> {
            loadMyClan();
            info.setText(myClan != null ? "Clan: " + myClan.getName() : "No clan assigned yet.");
            loadMyTeammates(table);
        }));
        root.getChildren().addAll(info, bar, table);
        setContent(root);
    }

    private void loadMyTeammates(TableView<User> table) {
        if (myClan == null) { table.setItems(FXCollections.observableArrayList()); return; }
        // Mostrar TODOS los coders del clan, incluyendo el propio coder
        List<User> teammates = myClan.getCoders().stream()
                .map(co -> (User) co)
                .toList();
        table.setItems(FXCollections.observableArrayList(teammates));
    }

    // ══════════════════════════════════════════════════════════
    //  MY TLS — solo TLs del mismo clan
    // ══════════════════════════════════════════════════════════
    @FXML
    private void handleTLS() {
        setActiveButton(btnTLS);
        VBox root = buildRoot("My Team Leaders");

        Label info = new Label(myClan != null
                ? "Team Leaders in clan: " + myClan.getName()
                : "No clan assigned.");
        info.getStyleClass().add("info-label");

        TableView<Tl> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("jupiter-table");
        table.setPlaceholder(new Label("No TLs found."));
        table.getColumns().addAll(List.of(
            col("Name",    c -> new SimpleStringProperty(c.getValue().getUsername())),
            col("Email",   c -> new SimpleStringProperty(c.getValue().getEmail())),
            col("TL Type", c -> new SimpleStringProperty(c.getValue().getTlType() != null
                    ? c.getValue().getTlType().name() : "—"))
        ));
        VBox.setVgrow(table, Priority.ALWAYS);

        loadMyTLs(table);
        HBox bar = actionBar(btn("⟳  Refresh", () -> { loadMyClan(); loadMyTLs(table); }));
        root.getChildren().addAll(info, bar, table);
        setContent(root);
    }

    private void loadMyTLs(TableView<Tl> table) {
        if (myClan == null) { table.setItems(FXCollections.observableArrayList()); return; }
        table.setItems(FXCollections.observableArrayList(myClan.getTls()));
    }

    // ══════════════════════════════════════════════════════════
    //  MY CELL (sin cambios requeridos)
    // ══════════════════════════════════════════════════════════
    @FXML
    private void handleCell() {
        setActiveButton(btnCell);
        VBox root = buildRoot("My Cell");
        Label info = new Label("Your current cell — sub-group within your clan managed by your TL.");
        info.getStyleClass().add("info-label"); info.setWrapText(true);
        Label placeholder = new Label("No cell assigned yet. Contact your Team Leader.");
        placeholder.getStyleClass().add("placeholder-label");
        root.getChildren().addAll(info, placeholder);
        setContent(root);
    }

    // ══════════════════════════════════════════════════════════
    //  TL MESSAGES — mensajes del clan del coder, más recientes primero
    // ══════════════════════════════════════════════════════════
    @FXML
    private void handleTLSMessages() {
        setActiveButton(btnTLSMessages);
        VBox root = buildRoot("TL Messages");

        Label info = new Label(myClan != null
                ? "Messages from TLs in clan: " + myClan.getName()
                : "No clan assigned.");
        info.getStyleClass().add("info-label");

        ListView<String> list = new ListView<>();
        list.getStyleClass().add("jupiter-list");
        list.setPlaceholder(new Label("No messages yet."));
        VBox.setVgrow(list, Priority.ALWAYS);

        loadTLMessages(list);
        HBox bar = actionBar(btn("⟳  Refresh", () -> { loadMyClan(); loadTLMessages(list); }));
        root.getChildren().addAll(info, bar, list);
        setContent(root);
    }

    private void loadTLMessages(ListView<String> list) {
        if (myClan == null) { list.setItems(FXCollections.observableArrayList()); return; }
        try {
            List<String[]> raw = clanImpl.getMessagesByClan(myClan.getId());
            // raw = [id, title, message, created_at] — ya ordenado DESC
            List<String> display = raw.stream()
                    .map(r -> "📅 " + formatTs(r[3]) + "  |  📌 " + r[1] + "\n      " + r[2])
                    .toList();
            list.setItems(FXCollections.observableArrayList(display));
        } catch (Exception e) {
            list.setItems(FXCollections.observableArrayList());
        }
    }

    // ── Exit ────────────────────────────────────────────────────────────────
    @FXML
    private void handleExit() {
        SessionManager.getInstance().logout();
        Stage stage = (Stage) contentPane.getScene().getWindow();
        ViewLoader.navigate(stage, "/views/login.fxml", "Jupiter – Management System", 900, 600);
    }

    // ── UI helpers ─────────────────────────────────────────────────────────
    private <S> TableColumn<S, String> col(String header,
            javafx.util.Callback<TableColumn.CellDataFeatures<S, String>,
            javafx.beans.value.ObservableValue<String>> f) {
        TableColumn<S, String> c = new TableColumn<>(header); c.setCellValueFactory(f); return c;
    }
    private VBox buildRoot(String title) {
        Label lbl = new Label(title); lbl.getStyleClass().add("section-title");
        VBox root = new VBox(16); root.setPadding(new Insets(28));
        root.getChildren().add(lbl); VBox.setVgrow(root, Priority.ALWAYS); return root;
    }
    private void setContent(VBox node) {
        AnchorPane.setTopAnchor(node,0.0); AnchorPane.setBottomAnchor(node,0.0);
        AnchorPane.setLeftAnchor(node,0.0); AnchorPane.setRightAnchor(node,0.0);
        contentPane.getChildren().setAll(node);
    }
    private HBox actionBar(Button... buttons) {
        HBox bar = new HBox(10); bar.setAlignment(Pos.CENTER_LEFT);
        bar.getChildren().addAll(buttons); return bar;
    }
    private Button btn(String text, Runnable action) {
        Button b = new Button(text); b.getStyleClass().add("action-btn");
        b.setOnAction(e -> action.run()); return b;
    }
    private void setActiveButton(Button btn) {
        if (activeBtn != null) activeBtn.getStyleClass().remove("nav-btn-active");
        activeBtn = btn;
        if (btn != null) btn.getStyleClass().add("nav-btn-active");
    }
    private String formatTs(String ts) {
        if (ts == null) return "—";
        return ts.length() > 16 ? ts.substring(0, 16).replace("T", " ") : ts;
    }
}
