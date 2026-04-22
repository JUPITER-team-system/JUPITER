package com.management.jupiter.controllers;

import com.management.jupiter.models.Clan;
import com.management.jupiter.models.Coder;
import com.management.jupiter.models.User;
import com.management.jupiter.repository.ClanRepository;
import com.management.jupiter.repository.impl.ClanRepositoryImpl;
import com.management.jupiter.security.SessionManager;
import com.management.jupiter.services.ClanService;
import com.management.jupiter.util.ViewLoader;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

public class TLFxController {

    @FXML private AnchorPane contentPane;
    @FXML private Label      headerLabel;
    @FXML private Button     btnMyClans;
    @FXML private Button     btnMessages;
    @FXML private Button     btnCells;

    private final ClanRepository     clanRepo    = new ClanRepository();
    private final ClanService        clanService = new ClanService(clanRepo);
    private final ClanRepositoryImpl clanImpl    = new ClanRepositoryImpl();

    private Button activeBtn;
    private String selectedClanId = null;

    // Clanes propios del TL (cacheados para reutilizar en Messages)
    private List<Clan> myClans = List.of();

    @FXML
    public void initialize() {
        User u = SessionManager.getInstance().getCurrentUser();
        if (u != null) headerLabel.setText("JUPITER  ·  Welcome, " + u.getUsername());
        setActiveButton(btnMyClans);
        handleMyClans();
    }

    // ══════════════════════════════════════════════════════════
    //  MY CLANS — solo los clanes asignados a este TL
    // ══════════════════════════════════════════════════════════
    @FXML
    private void handleMyClans() {
        setActiveButton(btnMyClans);
        VBox root = buildRoot("My Clans & Coders");

        TableView<Clan> tblClans = buildTable();
        tblClans.getColumns().addAll(List.of(
            col("Clan Name",   c -> new SimpleStringProperty(c.getValue().getName())),
            col("Description", c -> new SimpleStringProperty(c.getValue().getDescription())),
            col("TLs",         c -> new SimpleStringProperty(String.valueOf(c.getValue().getTls().size()))),
            col("Coders",      c -> new SimpleStringProperty(String.valueOf(c.getValue().getCoders().size())))
        ));

        TableView<Coder> tblCoders = buildTable();
        tblCoders.getColumns().addAll(List.of(
            col("Name",  c -> new SimpleStringProperty(c.getValue().getUsername())),
            col("Email", c -> new SimpleStringProperty(c.getValue().getEmail()))
        ));

        Label coderTitle = new Label("Coders in selected clan");
        coderTitle.getStyleClass().add("sub-section-title");

        tblClans.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                selectedClanId = sel.getId();
                tblCoders.setItems(FXCollections.observableArrayList(sel.getCoders()));
            } else {
                tblCoders.setItems(FXCollections.emptyObservableList());
            }
        });

        loadMyClans(tblClans);
        VBox.setVgrow(tblClans, Priority.ALWAYS);
        VBox.setVgrow(tblCoders, Priority.ALWAYS);

        HBox bar = actionBar(btn("⟳  Refresh", () -> loadMyClans(tblClans)));
        root.getChildren().addAll(bar, tblClans, coderTitle, tblCoders);
        setContent(root);
    }

    private void loadMyClans(TableView<Clan> table) {
        try {
            User me = SessionManager.getInstance().getCurrentUser();
            List<Clan> all = clanService.readAll();

            // Filtrar solo los clanes donde este TL está asignado (via user.clan_id)
            myClans = (me != null)
                    ? all.stream()
                         .filter(c -> c.getTls().stream()
                                 .anyMatch(t -> me.getId().equals(t.getId())))
                         .toList()
                    : List.of();

            // Si aún no tiene clanes asignados, mostrar todos (modo demo)
            table.setItems(FXCollections.observableArrayList(myClans.isEmpty() ? all : myClans));
            if (!table.getItems().isEmpty()) table.getSelectionModel().selectFirst();
        } catch (Exception e) {
            table.setItems(FXCollections.observableArrayList());
        }
    }

    // ══════════════════════════════════════════════════════════
    //  MESSAGES — solo en clanes del TL
    // ══════════════════════════════════════════════════════════
    @FXML
    private void handleMessages() {
        setActiveButton(btnMessages);
        VBox root = buildRoot("Clan Messages");

        // Recargar mis clanes
        User me = SessionManager.getInstance().getCurrentUser();
        List<Clan> all = clanService.readAll();
        List<Clan> assignedClans = (me != null)
                ? all.stream()
                     .filter(c -> c.getTls().stream().anyMatch(t -> me.getId().equals(t.getId())))
                     .toList()
                : List.of();
        // Si no hay asignados, usar todos (fallback)
        List<Clan> clanOptions = assignedClans.isEmpty() ? all : assignedClans;

        ComboBox<Clan> cbClan = new ComboBox<>(FXCollections.observableArrayList(clanOptions));
        cbClan.setPromptText("Select a clan…");
        cbClan.getStyleClass().add("jupiter-combo");
        cbClan.setButtonCell(new ClanListCell());
        cbClan.setCellFactory(lv -> new ClanListCell());

        ObservableList<String[]> msgData = FXCollections.observableArrayList();
        ListView<String> list = new ListView<>();
        list.getStyleClass().add("jupiter-list");
        list.setPlaceholder(new Label("No messages for this clan."));
        VBox.setVgrow(list, Priority.ALWAYS);
        Label statusLabel = statusLabel();

        TextField tfTitle = styledField("Title (optional)", "");
        TextArea taMsg = new TextArea();
        taMsg.setPromptText("Write a message…");
        taMsg.setPrefRowCount(3);
        taMsg.getStyleClass().add("jupiter-field");

        cbClan.setOnAction(e -> {
            Clan chosen = cbClan.getValue();
            if (chosen == null) return;
            selectedClanId = chosen.getId();
            loadMessages(chosen.getId(), list, msgData);
        });
        // Auto-select first clan
        if (!clanOptions.isEmpty()) cbClan.setValue(clanOptions.get(0));

        HBox bar = actionBar(
            btn("📤  Post", () -> {
                if (selectedClanId == null) { showStatus(statusLabel, "⚠  Select a clan first.", false); return; }
                String msg = taMsg.getText().trim();
                if (msg.isEmpty()) { showStatus(statusLabel, "⚠  Write a message first.", false); return; }
                try {
                    String title = tfTitle.getText().trim().isEmpty()
                            ? "Message " + java.time.LocalDate.now() : tfTitle.getText().trim();
                    clanImpl.saveMessage(selectedClanId, title, msg);
                    taMsg.clear(); tfTitle.clear();
                    loadMessages(selectedClanId, list, msgData);
                    showStatus(statusLabel, "✔  Message posted.", true);
                } catch (Exception ex) { showStatus(statusLabel, "✗  " + ex.getMessage(), false); }
            }),
            btn("🗑  Delete selected", () -> {
                int idx = list.getSelectionModel().getSelectedIndex();
                if (idx < 0) { showStatus(statusLabel, "⚠  Select a message to delete.", false); return; }
                try {
                    clanImpl.deleteMessage(msgData.get(idx)[0]);
                    loadMessages(selectedClanId, list, msgData);
                    showStatus(statusLabel, "✔  Message deleted.", true);
                } catch (Exception ex) { showStatus(statusLabel, "✗  " + ex.getMessage(), false); }
            })
        );

        root.getChildren().addAll(label("Clan"), cbClan, label("Title"), tfTitle,
                label("Message"), taMsg, bar, statusLabel, list);
        setContent(root);
    }

    private void loadMessages(String clanId, ListView<String> list, ObservableList<String[]> msgData) {
        try {
            List<String[]> raw = clanImpl.getMessagesByClan(clanId);
            msgData.setAll(raw);
            list.setItems(FXCollections.observableArrayList(
                raw.stream().map(r -> "[" + formatTs(r[3]) + "]  " + r[1] + " — " + r[2]).toList()
            ));
        } catch (Exception e) { list.setItems(FXCollections.emptyObservableList()); }
    }

    // ══════════════════════════════════════════════════════════
    //  CELLS (sin cambios requeridos)
    // ══════════════════════════════════════════════════════════
    @FXML
    private void handleCells() {
        setActiveButton(btnCells);
        VBox root = buildRoot("Cells");

        ComboBox<Clan> cbClan = new ComboBox<>();
        cbClan.setPromptText("Select a clan…");
        cbClan.getStyleClass().add("jupiter-combo");
        cbClan.setButtonCell(new ClanListCell());
        cbClan.setCellFactory(lv -> new ClanListCell());
        try { cbClan.setItems(FXCollections.observableArrayList(clanService.readAll())); } catch (Exception ignored) {}

        ObservableList<String[]> cellData = FXCollections.observableArrayList();
        TableView<String[]> table = new TableView<>();
        table.getStyleClass().add("jupiter-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No cells for this clan."));
        TableColumn<String[], String> cName = new TableColumn<>("Cell Name");
        TableColumn<String[], String> cDate = new TableColumn<>("Created");
        cName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()[1]));
        cDate.setCellValueFactory(d -> new SimpleStringProperty(formatTs(d.getValue()[2])));
        table.getColumns().addAll(cName, cDate);
        table.setItems(cellData);
        VBox.setVgrow(table, Priority.ALWAYS);
        Label statusLabel = statusLabel();

        cbClan.setOnAction(e -> {
            Clan chosen = cbClan.getValue();
            if (chosen == null) return;
            selectedClanId = chosen.getId();
            loadCells(chosen.getId(), cellData, table);
        });

        HBox bar = actionBar(
            btn("＋  New Cell", () -> {
                if (selectedClanId == null) { showStatus(statusLabel, "⚠  Select a clan first.", false); return; }
                TextInputDialog dlg = new TextInputDialog();
                dlg.setTitle("New Cell"); dlg.setHeaderText("Cell name:");
                dlg.getDialogPane().getStylesheets().add(getClass().getResource("/styles/jupiter.css").toExternalForm());
                dlg.showAndWait().ifPresent(name -> {
                    if (name.isBlank()) return;
                    try { clanImpl.saveCell(selectedClanId, name); loadCells(selectedClanId, cellData, table);
                          showStatus(statusLabel, "✔  Cell created.", true);
                    } catch (Exception ex) { showStatus(statusLabel, "✗  " + ex.getMessage(), false); }
                });
            }),
            btn("✕  Delete", () -> {
                int idx = table.getSelectionModel().getSelectedIndex();
                if (idx < 0) { showStatus(statusLabel, "⚠  Select a cell to delete.", false); return; }
                try { clanImpl.deleteCell(cellData.get(idx)[0]); loadCells(selectedClanId, cellData, table);
                      showStatus(statusLabel, "✔  Cell deleted.", true);
                } catch (Exception ex) { showStatus(statusLabel, "✗  " + ex.getMessage(), false); }
            }),
            btn("⟳  Refresh", () -> { if (selectedClanId != null) loadCells(selectedClanId, cellData, table); })
        );

        root.getChildren().addAll(label("Clan"), cbClan, bar, statusLabel, table);
        setContent(root);
    }

    private void loadCells(String clanId, ObservableList<String[]> cellData, TableView<String[]> table) {
        try { cellData.setAll(clanImpl.getCellsByClan(clanId)); table.refresh(); }
        catch (Exception e) { cellData.clear(); }
    }

    @FXML
    private void handleExit() {
        SessionManager.getInstance().logout();
        Stage stage = (Stage) contentPane.getScene().getWindow();
        ViewLoader.navigate(stage, "/views/login.fxml", "Jupiter – Management System", 900, 600);
    }

    // ── UI helpers ─────────────────────────────────────────────────────────
    private <S> TableView<S> buildTable() {
        TableView<S> t = new TableView<>();
        t.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        t.getStyleClass().add("jupiter-table");
        t.setPlaceholder(new Label("No data found.")); return t;
    }
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
    private TextField styledField(String prompt, String value) {
        TextField tf = new TextField(value); tf.setPromptText(prompt);
        tf.getStyleClass().add("jupiter-field"); return tf;
    }
    private Label label(String text) {
        Label l = new Label(text); l.getStyleClass().add("form-label"); return l;
    }
    private Label statusLabel() {
        Label l = new Label(""); l.getStyleClass().add("status-label");
        l.setVisible(false); l.setWrapText(true); return l;
    }
    private void showStatus(Label lbl, String msg, boolean ok) {
        lbl.setText(msg);
        lbl.setStyle(ok ? "-fx-text-fill: #22c55e; -fx-font-weight: 600;"
                        : "-fx-text-fill: #ef4444; -fx-font-weight: 600;");
        lbl.setVisible(true);
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
    private static class ClanListCell extends ListCell<Clan> {
        @Override protected void updateItem(Clan item, boolean empty) {
            super.updateItem(item, empty);
            setText(empty || item == null ? null : item.getName());
        }
    }
}
