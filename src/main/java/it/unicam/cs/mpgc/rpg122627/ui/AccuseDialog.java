package it.unicam.cs.mpgc.rpg122627.ui;

import it.unicam.cs.mpgc.rpg122627.engine.GameSession;
import it.unicam.cs.mpgc.rpg122627.engine.action.AccuseAction;
import it.unicam.cs.mpgc.rpg122627.model.Clue;
import it.unicam.cs.mpgc.rpg122627.model.Person;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/** Finestra in cui il giocatore sceglie chi accusare e con quali indizi. */
final class AccuseDialog {

    private AccuseDialog() {
    }

    /** Mostra la finestra; restituisce l'accusa se confermata, altrimenti vuoto. */
    static Optional<AccuseAction> ask(Component parent, GameSession session) {
        List<Person> suspects = new ArrayList<>(session.caseFile().people().values());
        List<Clue> clues = session.foundClues();

        JComboBox<String> suspectBox = new JComboBox<>(suspects.stream().map(Person::name).toArray(String[]::new));
        DefaultListModel<String> clueModel = new DefaultListModel<>();
        clues.forEach(clue -> clueModel.addElement(clue.title()));
        JList<String> clueList = new JList<>(clueModel);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Chi accusi?"));
        panel.add(suspectBox);
        panel.add(new JLabel(" "));
        panel.add(new JLabel("Con quali prove? (selezione multipla)"));
        panel.add(new JScrollPane(clueList));

        int choice = JOptionPane.showConfirmDialog(parent, panel, "Accusa", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) {
            return Optional.empty();
        }
        Set<String> evidence = new HashSet<>();
        for (int index : clueList.getSelectedIndices()) {
            evidence.add(clues.get(index).id());
        }
        return Optional.of(new AccuseAction(suspects.get(suspectBox.getSelectedIndex()).id(), evidence));
    }
}
