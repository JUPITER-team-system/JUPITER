package com.management.jupiter.controllers;

import com.management.jupiter.models.*;
import com.management.jupiter.models.enums.Role;
import com.management.jupiter.models.enums.TlType;
import com.management.jupiter.repository.ClanRepository;
import com.management.jupiter.repository.impl.AdminRepositoryImpl;
import com.management.jupiter.repository.impl.ClanRepositoryImpl;
import com.management.jupiter.security.SessionManager;
import com.management.jupiter.services.AdminService;
import com.management.jupiter.services.ClanService;
import com.management.jupiter.services.UserService;
import com.management.jupiter.util.ViewLoader;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class AdminFXController {

    @FXML private AnchorPane contentPane;
    @FXML private Label      headerLabel;
    @FXML private Button     btnClans;
    @FXML private Button     btnTLs;
    @FXML private Button     btnCoders;

    private final AdminRepositoryImpl adminRepo    = new AdminRepositoryImpl();
    private final UserService         userService  = new UserService();
    private final AdminService        adminService = new AdminService(userService, adminRepo);
    private final ClanRepository      clanRepo     = new ClanRepository();
    private final ClanService         clanService  = new ClanService(clanRepo);

    private Button activeBtn;

    // Cache de clanes para la columna "Clan" — se invalida en cada refresh
    private List<Clan> clanCache = List.of();

    @FXML
    public void initialize() {
        User u = SessionManager.getInstance().getCurrentUser();
        if (u != null) headerLabel.setText("JUPITER  ·  Welcome, " + u.getUsername());
        setActiveButton(btnClans);
        handleClans();
    }

    // ══════════════════════════════════════════════════════════
    //  CLANES
    // ══════════════════════════════════════════════════════════
    @FXML
    private void handleClans() {
        setActiveButton(btnClans);
        VBox root = buildSectionRoot("Clan Management");

        ObservableList<Clan> data = FXCollections.observableArrayList();
        TableView<Clan> table = new TableView<>(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("jupiter-table");
        table.getColumns().addAll(List.of(
            col("ID",          c -> new SimpleStringProperty(c.getValue().getId())),
            col("Name",        c -> new SimpleStringProperty(c.getValue().getName())),
            col("Description", c -> new SimpleStringProperty(c.getValue().getDescription())),
            col("TLs",         c -> new SimpleStringProperty(String.valueOf(c.getValue().getTls().size()))),
            col("Coders",      c -> new SimpleStringProperty(String.valueOf(c.getValue().getCoders().size())))
        ));
        VBox.setVgrow(table, Priority.ALWAYS);
        Label statusLabel = statusLabel();

        HBox bar = actionBar(
            btn("＋  New Clan",     () -> showClanDialog(null, data, statusLabel)),
            btn("✎  Edit",          () -> {
                Clan sel = table.getSelectionModel().getSelectedItem();
                if (sel != null) showClanDialog(sel, data, statusLabel);
                else showStatus(statusLabel, "⚠  Select a clan to edit.", false);
            }),
            btn("✕  Delete",        () -> {
                Clan sel = table.getSelectionModel().getSelectedItem();
                if (sel == null) { showStatus(statusLabel, "⚠  Select a clan to delete.", false); return; }
                if (confirm("Delete clan \"" + sel.getName() + "\"?")) {
                    try { clanService.delete(sel); refreshClans(data); showStatus(statusLabel, "✔  Clan deleted.", true); }
                    catch (Exception e) { showStatus(statusLabel, "✗  " + e.getMessage(), false); }
                }
            }),
            btn("📥  Carga Masiva", () -> {
                Clan sel = table.getSelectionModel().getSelectedItem();
                if (sel == null) { showStatus(statusLabel, "⚠  Select a clan for bulk load.", false); return; }
                showBulkLoadDialog(sel, data, statusLabel);
            }),
            btn("⟳  Refresh",       () -> { refreshClans(data); showStatus(statusLabel, "✔  Refreshed.", true); })
        );

        root.getChildren().addAll(bar, statusLabel, table);
        setContent(root);
        refreshClans(data);
    }

    private void refreshClans(ObservableList<Clan> data) {
        try {
            List<Clan> fresh = clanService.readAll();
            data.setAll(fresh);
            clanCache = fresh;          // actualizar cache también
        } catch (Exception e) {
            data.clear();
        }
    }

    private void showClanDialog(Clan existing, ObservableList<Clan> data, Label statusLabel) {
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle(existing == null ? "New Clan" : "Edit Clan");
        dlg.setHeaderText(null);
        TextField tfName = styledField("Clan name", existing == null ? "" : existing.getName());
        TextField tfDesc = styledField("Description", existing == null ? "" : existing.getDescription());
        VBox body = new VBox(12, label("Name *"), tfName, label("Description"), tfDesc);
        body.setPadding(new Insets(20));
        dlg.getDialogPane().setContent(body);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        styleDialogPane(dlg.getDialogPane());
        dlg.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;
            String name = tfName.getText().trim();
            String desc = tfDesc.getText().trim();
            if (name.isEmpty()) { alert("Clan name is required."); return; }
            try {
                if (existing == null) {
                    clanService.add(new Clan(null, name, desc));
                    showStatus(statusLabel, "✔  Clan \"" + name + "\" created.", true);
                } else {
                    existing.setName(name); existing.setDescription(desc);
                    clanService.edit(existing);
                    showStatus(statusLabel, "✔  Clan \"" + name + "\" updated.", true);
                }
                refreshClans(data);
            } catch (Exception e) {
                showStatus(statusLabel, "✗  " + e.getMessage(), false);
                alert("Error: " + e.getMessage());
            }
        });
    }

    private void showBulkLoadDialog(Clan clan, ObservableList<Clan> data, Label parentStatus) {
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle("Bulk Load Coders — " + clan.getName());
        dlg.setHeaderText(null);
        Label hint = new Label("CSV format: full_name, email, password  (password optional)");
        hint.setWrapText(true); hint.getStyleClass().add("info-label");
        Label dropZone = new Label("📂  Drag & drop CSV here\nor click 'Browse'");
        dropZone.setAlignment(Pos.CENTER); dropZone.setWrapText(true);
        dropZone.setStyle("-fx-border-color:#94a3b8;-fx-border-style:dashed;-fx-border-width:2;"
                + "-fx-border-radius:8;-fx-background-radius:8;-fx-padding:30;"
                + "-fx-font-size:14px;-fx-text-fill:#64748b;");
        dropZone.setPrefHeight(100);
        Label fileLabel = new Label("No file selected.");
        fileLabel.getStyleClass().add("info-label");
        final File[] sel = {null};
        dropZone.setOnDragOver(e -> { if (e.getDragboard().hasFiles()) e.acceptTransferModes(TransferMode.COPY); e.consume(); });
        dropZone.setOnDragDropped(e -> {
            if (e.getDragboard().hasFiles()) {
                File f = e.getDragboard().getFiles().get(0);
                if (f.getName().endsWith(".csv")) { sel[0] = f; fileLabel.setText("✔  " + f.getName()); }
                else fileLabel.setText("⚠  Only .csv files.");
            }
            e.setDropCompleted(true); e.consume();
        });
        Button btnBrowse = btn("📁  Browse", () -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Select CSV"); fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
            File f = fc.showOpenDialog(dlg.getOwner());
            if (f != null) { sel[0] = f; fileLabel.setText("✔  " + f.getName()); }
        });
        VBox body = new VBox(12, hint, dropZone, btnBrowse, fileLabel);
        body.setPadding(new Insets(20)); body.setPrefWidth(460);
        dlg.getDialogPane().setContent(body);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        styleDialogPane(dlg.getDialogPane());
        dlg.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;
            if (sel[0] == null) { alert("No file selected."); return; }
            try {
                List<String[]> rows = parseCsvCoders(sel[0]);
                if (rows.isEmpty()) { alert("No valid rows found."); return; }
                int n = adminRepo.bulkInsertCoders(rows, clan.getId());
                refreshClans(data);
                showStatus(parentStatus, "✔  " + n + " coder(s) loaded into \"" + clan.getName() + "\".", true);
            } catch (Exception e) {
                alert("Error: " + e.getMessage());
                showStatus(parentStatus, "✗  " + e.getMessage(), false);
            }
        });
    }

    private List<String[]> parseCsvCoders(File file) throws Exception {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line; boolean first = true;
            while ((line = br.readLine()) != null) {
                line = line.trim(); if (line.isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (first && (parts[0].toLowerCase().contains("name") || parts[0].toLowerCase().contains("full"))) { first = false; continue; }
                first = false;
                if (parts.length >= 2) rows.add(parts);
            }
        }
        return rows;
    }

    // ══════════════════════════════════════════════════════════
    //  TEAM LEADERS
    // ══════════════════════════════════════════════════════════
    @FXML
    private void handleTLs() {
        setActiveButton(btnTLs);
        VBox root = buildSectionRoot("Team Leaders");

        // Refrescar cache de clanes para la columna Clan
        refreshClanCache();

        ObservableList<User> data = FXCollections.observableArrayList();
        TableView<User> table = new TableView<>(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("jupiter-table");
        table.getColumns().addAll(List.of(
            col("Name",    c -> new SimpleStringProperty(c.getValue().getUsername())),
            col("Email",   c -> new SimpleStringProperty(c.getValue().getEmail())),
            col("TL Type", c -> new SimpleStringProperty(
                    c.getValue() instanceof Tl t && t.getTlType() != null ? t.getTlType().name() : "—")),
            col("Clan",    c -> new SimpleStringProperty(getClanNameFromCache(c.getValue())))
        ));
        Label statusLabel = statusLabel();
        VBox.setVgrow(table, Priority.ALWAYS);

        HBox bar = actionBar(
            btn("＋  New TL",  () -> showUserDialog(null, Role.TL, data, statusLabel)),
            btn("✎  Edit",     () -> {
                User sel2 = table.getSelectionModel().getSelectedItem();
                if (sel2 != null) showUserDialog(sel2, Role.TL, data, statusLabel);
                else showStatus(statusLabel, "⚠  Select a TL to edit.", false);
            }),
            btn("✕  Delete",   () -> deleteUser(table, data, statusLabel)),
            btn("⟳  Refresh",  () -> {
                refreshClanCache();
                refreshUsers(data, Role.TL);
                showStatus(statusLabel, "✔  Refreshed.", true);
            })
        );
        root.getChildren().addAll(bar, statusLabel, table);
        setContent(root);
        refreshUsers(data, Role.TL);
    }

    // ══════════════════════════════════════════════════════════
    //  CODERS
    // ══════════════════════════════════════════════════════════
    @FXML
    private void handleCoders() {
        setActiveButton(btnCoders);
        VBox root = buildSectionRoot("Developers");

        refreshClanCache();

        ObservableList<User> data = FXCollections.observableArrayList();
        TableView<User> table = new TableView<>(data);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getStyleClass().add("jupiter-table");
        table.getColumns().addAll(List.of(
            col("Name",  c -> new SimpleStringProperty(c.getValue().getUsername())),
            col("Email", c -> new SimpleStringProperty(c.getValue().getEmail())),
            col("Clan",  c -> new SimpleStringProperty(getClanNameFromCache(c.getValue())))
        ));
        Label statusLabel = statusLabel();
        VBox.setVgrow(table, Priority.ALWAYS);

        HBox bar = actionBar(
            btn("＋  New Coder", () -> showUserDialog(null, Role.CODER, data, statusLabel)),
            btn("✎  Edit",       () -> {
                User sel2 = table.getSelectionModel().getSelectedItem();
                if (sel2 != null) showUserDialog(sel2, Role.CODER, data, statusLabel);
                else showStatus(statusLabel, "⚠  Select a developer to edit.", false);
            }),
            btn("✕  Delete",     () -> deleteUser(table, data, statusLabel)),
            btn("⟳  Refresh",    () -> {
                refreshClanCache();
                refreshUsers(data, Role.CODER);
                showStatus(statusLabel, "✔  Refreshed.", true);
            })
        );
        root.getChildren().addAll(bar, statusLabel, table);
        setContent(root);
        refreshUsers(data, Role.CODER);
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    /** Recarga el cache de clanes UNA vez, no por fila. */
    private void refreshClanCache() {
        try { clanCache = clanService.readAll(); }
        catch (Exception ignored) { clanCache = List.of(); }
    }

    /** Busca el nombre del clan en el cache (O(n) local, sin red). */
    private String getClanNameFromCache(User u) {
        String uid = u.getId();
        for (Clan c : clanCache) {
            if (c.getTls().stream().anyMatch(t -> uid.equals(t.getId()))) return c.getName();
            if (c.getCoders().stream().anyMatch(co -> uid.equals(co.getId()))) return c.getName();
        }
        return "—";
    }

    /** Re-consulta la BD y actualiza la ObservableList in-place (no reemplaza la referencia). */
    private void refreshUsers(ObservableList<User> data, Role role) {
        try {
            List<User> fresh = adminRepo.getAll().stream()
                    .filter(u -> u.getRole() == role).toList();
            data.setAll(fresh);
        } catch (Exception e) {
            data.clear();
        }
    }

    private void showUserDialog(User existing, Role role, ObservableList<User> data, Label statusLabel) {
        Dialog<ButtonType> dlg = new Dialog<>();
        dlg.setTitle(existing == null ? "New " + role.name() : "Edit " + role.name());
        dlg.setHeaderText(null);

        TextField     tfName  = styledField("Full name",  existing == null ? "" : existing.getUsername());
        TextField     tfEmail = styledField("Email",      existing == null ? "" : existing.getEmail());
        PasswordField pfPass  = new PasswordField();
        pfPass.setPromptText(existing == null ? "Password" : "New password (leave blank to keep)");
        pfPass.getStyleClass().add("jupiter-field");

        ComboBox<TlType> cbTlType = new ComboBox<>(FXCollections.observableArrayList(TlType.values()));
        cbTlType.setPromptText("TL Type"); cbTlType.getStyleClass().add("jupiter-combo");
        if (existing instanceof Tl t) cbTlType.setValue(t.getTlType());
        else if (role == Role.TL)      cbTlType.setValue(TlType.PROGRAMACION);

        // Clan combo — solo en edición
        ComboBox<Clan> cbClan = new ComboBox<>();
        cbClan.setPromptText("Assign to clan (optional)");
        cbClan.getStyleClass().add("jupiter-combo");
        cbClan.setButtonCell(new ClanListCell()); cbClan.setCellFactory(lv -> new ClanListCell());
        List<Clan> opts = new ArrayList<>();
        opts.add(new Clan(null, "— No clan —", ""));
        opts.addAll(clanCache.isEmpty() ? clanService.readAll() : clanCache);
        cbClan.setItems(FXCollections.observableArrayList(opts));

        if (existing != null) {
            String curName = getClanNameFromCache(existing);
            opts.stream().filter(c -> curName.equals(c.getName())).findFirst().ifPresent(cbClan::setValue);
            if (cbClan.getValue() == null) cbClan.setValue(opts.get(0));
        } else {
            cbClan.setValue(opts.get(0));
        }

        VBox body = new VBox(10); body.setPadding(new Insets(20));
        body.getChildren().addAll(label("Full Name *"), tfName, label("Email *"), tfEmail,
                label("Password" + (existing == null ? " *" : "")), pfPass);
        if (role == Role.TL)    body.getChildren().addAll(label("TL Type *"), cbTlType);
        if (existing != null)   body.getChildren().addAll(label("Clan"), cbClan);

        dlg.getDialogPane().setContent(body);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        styleDialogPane(dlg.getDialogPane());

        dlg.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;
            String name  = tfName.getText().trim();
            String email = tfEmail.getText().trim();
            String pass  = pfPass.getText().trim();

            if (name.isEmpty() || email.isEmpty()) { alert("Name and email are required."); return; }
            if (existing == null && pass.isEmpty()) { alert("Password is required."); return; }
            if (role == Role.TL && cbTlType.getValue() == null) { alert("TL Type is required."); return; }

            try {
                if (existing == null) {
                    TlType tlType = role == Role.TL ? cbTlType.getValue() : null;
                    adminService.createUser(name, email, pass, role, null, tlType);
                    showStatus(statusLabel, "✔  " + role.name() + " \"" + name + "\" created.", true);
                } else {
                    AdminService.updateUser(existing.getId(), name,  "full_name");
                    AdminService.updateUser(existing.getId(), email, "email");
                    if (!pass.isEmpty())
                        AdminService.updateUser(existing.getId(), pass, "password");
                    if (role == Role.TL && cbTlType.getValue() != null)
                        AdminService.updateUser(existing.getId(), cbTlType.getValue().name(), "specialty");

                    Clan chosen = cbClan.getValue();
                    if (chosen != null && chosen.getId() != null) {
                        validateAndAssignClan(existing, chosen, role,
                                role == Role.TL ? cbTlType.getValue() : null);
                    } else if (chosen != null && chosen.getId() == null) {
                        AdminService.updateUser(existing.getId(), "", "clan_id");
                    }
                    showStatus(statusLabel, "✔  User \"" + name + "\" updated.", true);
                }
                // Refrescar cache y tabla después de cualquier cambio
                refreshClanCache();
                refreshUsers(data, role);
            } catch (Exception e) {
                showStatus(statusLabel, "✗  " + e.getMessage(), false);
                alert("Error: " + e.getMessage());
            }
        });
    }

    private void validateAndAssignClan(User user, Clan clan, Role role, TlType tlType) throws Exception {
        if (role == Role.TL && tlType != null) {
            Optional<Clan> freshOpt = new ClanRepositoryImpl().findById(clan.getId());
            if (freshOpt.isPresent()) {
                long count = freshOpt.get().getTls().stream()
                        .filter(t -> t.getTlType() == tlType && !t.getId().equals(user.getId()))
                        .count();
                int limit = tlType == TlType.PROGRAMACION ? 1 : 3;
                if (count >= limit)
                    throw new Exception("Clan \"" + clan.getName() + "\" already has "
                            + limit + " TL(s) of type " + tlType + ".");
            }
        }
        AdminService.updateUser(user.getId(), clan.getId(), "clan_id");
    }

    private void deleteUser(TableView<User> table, ObservableList<User> data, Label statusLabel) {
        User sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { showStatus(statusLabel, "⚠  Select a user to delete.", false); return; }
        if (!confirm("Delete user \"" + sel.getUsername() + "\"?")) return;
        try {
            adminService.deleteUser(sel.getId());
            data.remove(sel);
            showStatus(statusLabel, "✔  User deleted.", true);
        } catch (Exception e) {
            showStatus(statusLabel, "✗  " + e.getMessage(), false);
            alert("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleExit() {
        SessionManager.getInstance().logout();
        Stage stage = (Stage) contentPane.getScene().getWindow();
        ViewLoader.navigate(stage, "/views/login.fxml", "Jupiter – Management System", 900, 600);
    }

    // ── UI helpers ─────────────────────────────────────────────────────────
    private VBox buildSectionRoot(String title) {
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
    private <S> TableColumn<S, String> col(String header,
            javafx.util.Callback<TableColumn.CellDataFeatures<S, String>,
            javafx.beans.value.ObservableValue<String>> factory) {
        TableColumn<S, String> c = new TableColumn<>(header); c.setCellValueFactory(factory); return c;
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
        lbl.setStyle(ok ? "-fx-text-fill:#22c55e;-fx-font-weight:600;"
                        : "-fx-text-fill:#ef4444;-fx-font-weight:600;");
        lbl.setVisible(true);
    }
    private void styleDialogPane(DialogPane pane) {
        pane.getStylesheets().add(getClass().getResource("/styles/jupiter.css").toExternalForm());
        pane.getStyleClass().add("jupiter-dialog"); pane.setPrefWidth(460);
    }
    private void setActiveButton(Button btn) {
        if (activeBtn != null) activeBtn.getStyleClass().remove("nav-btn-active");
        activeBtn = btn;
        if (btn != null) btn.getStyleClass().add("nav-btn-active");
    }
    private void alert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null); a.setTitle("Jupiter");
        a.getDialogPane().getStylesheets().add(getClass().getResource("/styles/jupiter.css").toExternalForm());
        a.showAndWait();
    }
    private boolean confirm(String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
        a.setHeaderText(null); a.setTitle("Confirm");
        a.getDialogPane().getStylesheets().add(getClass().getResource("/styles/jupiter.css").toExternalForm());
        Optional<ButtonType> res = a.showAndWait();
        return res.isPresent() && res.get() == ButtonType.YES;
    }
    private static class ClanListCell extends ListCell<Clan> {
        @Override protected void updateItem(Clan item, boolean empty) {
            super.updateItem(item, empty);
            setText(empty || item == null ? null : item.getName());
        }
    }
}
