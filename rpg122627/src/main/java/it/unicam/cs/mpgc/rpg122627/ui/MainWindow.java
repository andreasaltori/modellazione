package it.unicam.cs.mpgc.rpg122627.ui;

import it.unicam.cs.mpgc.rpg122627.engine.Action;
import it.unicam.cs.mpgc.rpg122627.engine.ActionResult;
import it.unicam.cs.mpgc.rpg122627.engine.GameListener;
import it.unicam.cs.mpgc.rpg122627.engine.GameSession;
import it.unicam.cs.mpgc.rpg122627.engine.GameStatus;
import it.unicam.cs.mpgc.rpg122627.engine.RandomDice;
import it.unicam.cs.mpgc.rpg122627.model.CaseFile;
import it.unicam.cs.mpgc.rpg122627.model.Clue;
import it.unicam.cs.mpgc.rpg122627.model.Investigator;
import it.unicam.cs.mpgc.rpg122627.model.Skill;
import it.unicam.cs.mpgc.rpg122627.persistence.CaseRepository;
import it.unicam.cs.mpgc.rpg122627.persistence.SaveData;
import it.unicam.cs.mpgc.rpg122627.persistence.SaveRepository;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

/**
 * Finestra principale. Non contiene regole di gioco: chiede alla {@link GameSession} le azioni
 * disponibili, le mostra come pulsanti e ne scrive l'esito (ricevuto come {@link GameListener}).
 */
@SuppressWarnings("serial") // la finestra non viene mai serializzata
public final class MainWindow extends JFrame implements GameListener {

    private static final String SAVE_SLOT = "partita";

    private final CaseRepository cases;
    private final SaveRepository saves;
    private final JLabel statusLabel = new JLabel();
    private final JTextArea storyArea = textArea();
    private final JTextArea notebookArea = textArea();
    private final JPanel actionsPanel = new JPanel();
    private final JButton accuseButton = new JButton("Accusa un sospettato…");

    private String caseId;
    private GameSession session;

    public MainWindow(CaseRepository cases, SaveRepository saves) {
        super("Investigatore – RPG investigativo");
        this.cases = cases;
        this.saves = saves;
        buildLayout();
        setJMenuBar(buildMenu());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(980, 640);
        setLocationRelativeTo(null);
    }

    /** Mostra la finestra e avvia la prima partita. */
    public void open() {
        setVisible(true);
        if (!startNewGame()) {
            dispose();
            System.exit(0);
        }
    }

    // ---- costruzione dell'interfaccia -----------------------------------------------------

    private void buildLayout() {
        statusLabel.setBorder(new EmptyBorder(8, 12, 8, 12));
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Diario", new JScrollPane(storyArea));
        tabs.addTab("Taccuino degli indizi", new JScrollPane(notebookArea));

        actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.Y_AXIS));
        JScrollPane actionsScroll = new JScrollPane(actionsPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        actionsScroll.setPreferredSize(new Dimension(340, 0));
        actionsScroll.setBorder(javax.swing.BorderFactory.createTitledBorder("Cosa fai?"));

        accuseButton.addActionListener(event -> accuse());
        JPanel east = new JPanel(new BorderLayout());
        east.add(actionsScroll, BorderLayout.CENTER);
        east.add(accuseButton, BorderLayout.SOUTH);

        add(statusLabel, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        add(east, BorderLayout.EAST);
    }

    private JMenuBar buildMenu() {
        JMenu menu = new JMenu("Partita");
        menu.add(menuItem("Nuova partita", this::startNewGame));
        menu.add(menuItem("Salva", this::saveGame));
        menu.add(menuItem("Carica", this::loadGame));
        menu.addSeparator();
        menu.add(menuItem("Esci", () -> System.exit(0)));
        JMenuBar bar = new JMenuBar();
        bar.add(menu);
        return bar;
    }

    private static JMenuItem menuItem(String text, Runnable action) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(event -> action.run());
        return item;
    }

    private static JTextArea textArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setMargin(new java.awt.Insets(8, 8, 8, 8));
        area.setFont(new Font(Font.SERIF, Font.PLAIN, 15));
        return area;
    }

    // ---- flusso di gioco ------------------------------------------------------------------

    /** Chiede nome e specializzazione e avvia una nuova partita. @return false se annullato. */
    private boolean startNewGame() {
        JTextField nameField = new JTextField("Ispettore");
        JComboBox<Skill> skillBox = new JComboBox<>(Skill.values());
        skillBox.setRenderer((list, value, index, selected, focus) -> new JLabel(value.label()));
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Come ti chiami?"));
        panel.add(nameField);
        panel.add(Box.createVerticalStrut(8));
        panel.add(new JLabel("In cosa sei più bravo? (+" + Investigator.SPECIALTY_BONUS + ")"));
        panel.add(skillBox);

        while (true) {
            int choice = JOptionPane.showConfirmDialog(this, panel, "Nuovo investigatore",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (choice != JOptionPane.OK_OPTION) {
                return false;
            }
            if (!nameField.getText().isBlank()) {
                break;
            }
            showError("Inserisci un nome.");
        }
        try {
            CaseFile caseFile = cases.load(cases.availableCaseIds().get(0));
            Investigator investigator = Investigator.create(nameField.getText(), (Skill) skillBox.getSelectedItem());
            caseId = caseFile.id();
            attach(GameSession.newGame(caseFile, investigator, new RandomDice()));
            storyArea.setText(caseFile.title() + "\n\n" + caseFile.intro() + "\n\n"
                    + session.currentLocation().name() + ". " + session.currentLocation().description() + "\n");
            refresh();
            return true;
        } catch (IOException | IllegalArgumentException e) {
            showError("Impossibile avviare la partita: " + e.getMessage());
            return false;
        }
    }

    private void saveGame() {
        if (session == null) {
            return;
        }
        try {
            saves.save(SAVE_SLOT, SaveData.from(caseId, session.state()));
            JOptionPane.showMessageDialog(this, "Partita salvata.");
        } catch (IOException e) {
            showError("Salvataggio non riuscito: " + e.getMessage());
        }
    }

    private void loadGame() {
        try {
            Optional<SaveData> data = saves.load(SAVE_SLOT);
            if (data.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Non c'è nessuna partita salvata.");
                return;
            }
            CaseFile caseFile = cases.load(data.get().caseId());
            caseId = caseFile.id();
            attach(GameSession.restore(caseFile, data.get().toState(), new RandomDice()));
            storyArea.setText("Partita caricata. Ti trovi in: " + session.currentLocation().name() + ".\n"
                    + session.currentLocation().description() + "\n");
            refresh();
        } catch (IOException | IllegalArgumentException e) {
            showError("Caricamento non riuscito: " + e.getMessage());
        }
    }

    private void attach(GameSession newSession) {
        session = newSession;
        session.addListener(this);
    }

    private void accuse() {
        AccuseDialog.ask(this, session).ifPresent(session::perform);
        refresh();
    }

    @Override
    public void onActionPerformed(Action action, ActionResult result) {
        storyArea.append("\n" + result.message() + "\n");
        storyArea.setCaretPosition(storyArea.getDocument().getLength());
    }

    // ---- aggiornamento della vista --------------------------------------------------------

    private void refresh() {
        Investigator hero = session.state().investigator();
        statusLabel.setText(hero.name() + "   |   Luogo: " + session.currentLocation().name()
                + "   |   Tempo: " + session.state().timeLeft() + "/" + session.caseFile().timeLimit()
                + "   |   Livello " + hero.level() + " (" + hero.experience() + "/" + hero.experienceForNextLevel() + " PE)"
                + "   |   " + skillSummary(hero));

        actionsPanel.removeAll();
        for (Action action : session.availableActions()) {
            actionsPanel.add(actionButton(action));
        }
        if (session.isOver()) {
            actionsPanel.add(new JLabel(session.state().status() == GameStatus.WON
                    ? "Caso risolto!" : "Caso chiuso senza colpevole."));
        }
        accuseButton.setEnabled(!session.isOver());

        notebookArea.setText(notebookText());
        actionsPanel.revalidate();
        actionsPanel.repaint();
    }

    private JButton actionButton(Action action) {
        JButton button = new JButton("<html><body style='width:240px'>"
                + escape(action.describe(session.caseFile())) + "</body></html>");
        button.setAlignmentX(LEFT_ALIGNMENT);
        button.setHorizontalAlignment(JButton.LEFT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height));
        button.addActionListener(event -> {
            session.perform(action);
            refresh();
        });
        return button;
    }

    private String notebookText() {
        if (session.foundClues().isEmpty()) {
            return "Non hai ancora trovato nessun indizio.";
        }
        StringBuilder text = new StringBuilder();
        for (Clue clue : session.foundClues()) {
            text.append("• ").append(clue.title()).append("\n  ").append(clue.description()).append("\n\n");
        }
        return text.toString();
    }

    private static String skillSummary(Investigator hero) {
        return String.join(" · ", Arrays.stream(Skill.values())
                .map(skill -> skill.label() + " " + hero.skill(skill)).toList());
    }

    private static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Errore", JOptionPane.ERROR_MESSAGE);
    }
}
